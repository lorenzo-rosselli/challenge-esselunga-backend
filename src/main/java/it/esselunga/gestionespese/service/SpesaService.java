package it.esselunga.gestionespese.service;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import io.quarkus.panache.common.Sort;
import it.esselunga.gestionespese.dto.CreazioneSpesaDto;
import it.esselunga.gestionespese.dto.ModificaSpesaDto;
import it.esselunga.gestionespese.dto.SpesaDto;
import it.esselunga.gestionespese.entity.Articolo;
import it.esselunga.gestionespese.entity.ElementoSpesa;
import it.esselunga.gestionespese.entity.Spesa;
import it.esselunga.gestionespese.entity.StatoSpesa;
import it.esselunga.gestionespese.mapper.PosizioneMapper;
import it.esselunga.gestionespese.mapper.SpesaMapper;
import it.esselunga.gestionespese.repository.ArticoloRepository;
import it.esselunga.gestionespese.repository.MagazzinoRepository;
import it.esselunga.gestionespese.repository.SpesaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Logica applicativa delle spese.
 */
@ApplicationScoped
public class SpesaService {

    /** Stati in cui la spesa è modificabile (UC2): prima che parta dal magazzino. */
    private static final Set<StatoSpesa> STATI_MODIFICABILI = EnumSet.of(StatoSpesa.CREATA, StatoSpesa.IN_PREPARAZIONE);

    private final SpesaRepository spesaRepository;
    private final MagazzinoRepository magazzinoRepository;
    private final ArticoloRepository articoloRepository;
    private final SpesaMapper spesaMapper;
    private final PosizioneMapper posizioneMapper;

    public SpesaService(SpesaRepository spesaRepository,
                        MagazzinoRepository magazzinoRepository,
                        ArticoloRepository articoloRepository,
                        SpesaMapper spesaMapper,
                        PosizioneMapper posizioneMapper) {
        this.spesaRepository = spesaRepository;
        this.magazzinoRepository = magazzinoRepository;
        this.articoloRepository = articoloRepository;
        this.spesaMapper = spesaMapper;
        this.posizioneMapper = posizioneMapper;
    }

    /**
     * Lista di tutte le spese con il loro stato.
     * <p>
     * Filtri e ordinamenti personalizzati sono gestiti dal frontend: il backend
     * restituisce l'intera lista, dalla più recente alla meno recente.
     *
     * @return tutte le spese, ordinate per data di creazione decrescente
     */
    public List<SpesaDto> lista() {
        return spesaRepository.listAll(Sort.by("dataCreazione", Sort.Direction.Descending)).stream()
                .map(spesaMapper::toDto)
                .toList();
    }

    /**
     * Creazione di una spesa.
     * <p>
     * verifica che il magazzino esista;
     * verifica che ogni articolo esista e compaia una sola volta;
     * copia nome e prezzo dell'articolo in ogni elemento e calcola prezzoTotale e totale;
     * imposta stato CREATA, date di creazione/modifica e versione 0.
     *
     *
     * @param dto richiesta già validata nella forma (Bean Validation)
     * @return spesa salvata
     */
    public SpesaDto crea(CreazioneSpesaDto dto) {
        Spesa spesa = spesaMapper.toEntity(dto);

        verificaMagazzino(spesa.getMagazzinoId());
        Map<ObjectId, Articolo> articoli = caricaArticoli(spesa.getArticoli());

        BigDecimal totale = BigDecimal.ZERO;
        for (ElementoSpesa elemento : spesa.getArticoli()) {
            Articolo articolo = articoli.get(elemento.getArticoloId());
            BigDecimal prezzoTotale = articolo.getPrezzo().multiply(BigDecimal.valueOf(elemento.getQuantita()));
            elemento.setNome(articolo.getNome());
            elemento.setPrezzo(articolo.getPrezzo());
            elemento.setPrezzoTotale(prezzoTotale);
            totale = totale.add(prezzoTotale);
        }

        // MongoDB salva le date al millisecondo: si tronca per avere lo stesso valore in risposta e su db
        Instant adesso = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        spesa.setTotale(totale);
        spesa.setStato(StatoSpesa.CREATA);
        spesa.setDataCreazione(adesso);
        spesa.setDataUltimaModifica(adesso);
        spesa.setVersione(0L);

        spesaRepository.persist(spesa);
        return spesaMapper.toDto(spesa);
    }

    /**
     * UC2 - Modifica parziale di una spesa.
     * <p>
     * Sono modificabili nome del cliente e dati di consegna (indirizzo e posizione insieme),
     * solo se la spesa è in uno stato modificabile e la versione inviata coincide con quella salvata.
     * Verifica e aggiornamento avvengono con un'unica operazione atomica su MongoDB
     * (findOneAndUpdate), così una modifica concorrente o un cambio di stato avvenuto
     * nel frattempo non possono essere sovrascritti.
     *
     * @param id  id della spesa (dal percorso della richiesta)
     * @param dto campi da modificare e versione letta dal client
     * @return spesa aggiornata
     */
    public SpesaDto modifica(String id, ModificaSpesaDto dto) {
        ObjectId spesaId = toObjectIdOrNotFound(id);

        Bson filtro = Filters.and(
                Filters.eq("_id", spesaId),
                Filters.eq("versione", dto.getVersione()),
                Filters.in("stato", STATI_MODIFICABILI.stream().map(Enum::name).toList()));

        List<Bson> modifiche = new ArrayList<>();
        if (dto.getNomeCliente() != null) {
            modifiche.add(Updates.set("nomeCliente", dto.getNomeCliente()));
        }
        if (dto.getIndirizzoConsegna() != null) {
            modifiche.add(Updates.set("indirizzoConsegna", dto.getIndirizzoConsegna()));
            modifiche.add(Updates.set("posizioneConsegna", posizioneMapper.toEntity(dto.getPosizioneConsegna())));
        }
        modifiche.add(Updates.set("dataUltimaModifica", Instant.now().truncatedTo(ChronoUnit.MILLIS)));
        modifiche.add(Updates.inc("versione", 1L));

        Spesa aggiornata = spesaRepository.mongoCollection().findOneAndUpdate(
                filtro,
                Updates.combine(modifiche),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

        if (aggiornata == null) {
            throw motivoModificaRifiutata(spesaId, dto.getVersione());
        }
        return spesaMapper.toDto(aggiornata);
    }

    /**
     * Se l'aggiornamento non ha trovato la spesa, ne individua il motivo
     * per restituire l'errore corretto al client.
     */
    private RuntimeException motivoModificaRifiutata(ObjectId spesaId, Long versioneInviata) {
        Spesa spesa = spesaRepository.findById(spesaId);
        if (spesa == null) {
            return new NotFoundException("Spesa " + spesaId.toHexString() + " non trovata");
        }
        if (!STATI_MODIFICABILI.contains(spesa.getStato())) {
            return new ClientErrorException("La spesa non è modificabile nello stato " + spesa.getStato()
                    + ": la modifica è consentita solo negli stati " + STATI_MODIFICABILI, Response.Status.CONFLICT);
        }
        return new ClientErrorException("La spesa è stata modificata nel frattempo (versione attuale "
                + spesa.getVersione() + ", versione inviata " + versioneInviata + "): ricaricare la spesa e riprovare",
                Response.Status.CONFLICT);
    }

    /** Un id nel percorso con formato non valido non può corrispondere a nessuna spesa: 404. */
    private ObjectId toObjectIdOrNotFound(String id) {
        if (!ObjectId.isValid(id)) {
            throw new NotFoundException("Spesa " + id + " non trovata");
        }
        return new ObjectId(id);
    }

    private void verificaMagazzino(ObjectId magazzinoId) {
        if (magazzinoRepository.findByIdOptional(magazzinoId).isEmpty()) {
            throw new BadRequestException("Magazzino " + magazzinoId.toHexString() + " non trovato");
        }
    }

    /**
     * Carica con un'unica query gli articoli della spesa, verificando che non ci siano
     * articoli ripetuti e che esistano tutti.
     *
     * @return articoli indicizzati per id
     */
    private Map<ObjectId, Articolo> caricaArticoli(List<ElementoSpesa> elementi) {
        Set<ObjectId> ids = new HashSet<>();
        for (ElementoSpesa elemento : elementi) {
            if (!ids.add(elemento.getArticoloId())) {
                throw new BadRequestException("L'articolo " + elemento.getArticoloId().toHexString()
                        + " è presente più volte nella spesa: indicare una sola riga con la quantità totale");
            }
        }

        Map<ObjectId, Articolo> articoli = articoloRepository.trovaPerIds(ids).stream()
                .collect(Collectors.toMap(Articolo::getId, Function.identity()));

        List<String> mancanti = ids.stream()
                .filter(id -> !articoli.containsKey(id))
                .map(ObjectId::toHexString)
                .toList();
        if (!mancanti.isEmpty()) {
            throw new BadRequestException("Articoli non trovati: " + String.join(", ", mancanti));
        }
        return articoli;
    }
}
