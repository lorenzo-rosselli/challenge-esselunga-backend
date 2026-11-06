package it.esselunga.gestionespese.service;

import io.quarkus.panache.common.Sort;
import it.esselunga.gestionespese.dto.CreazioneSpesaDto;
import it.esselunga.gestionespese.dto.SpesaDto;
import it.esselunga.gestionespese.entity.Articolo;
import it.esselunga.gestionespese.entity.ElementoSpesa;
import it.esselunga.gestionespese.entity.Spesa;
import it.esselunga.gestionespese.entity.StatoSpesa;
import it.esselunga.gestionespese.mapper.SpesaMapper;
import it.esselunga.gestionespese.repository.ArticoloRepository;
import it.esselunga.gestionespese.repository.MagazzinoRepository;
import it.esselunga.gestionespese.repository.SpesaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    private final SpesaRepository spesaRepository;
    private final MagazzinoRepository magazzinoRepository;
    private final ArticoloRepository articoloRepository;
    private final SpesaMapper spesaMapper;

    public SpesaService(SpesaRepository spesaRepository,
                        MagazzinoRepository magazzinoRepository,
                        ArticoloRepository articoloRepository,
                        SpesaMapper spesaMapper) {
        this.spesaRepository = spesaRepository;
        this.magazzinoRepository = magazzinoRepository;
        this.articoloRepository = articoloRepository;
        this.spesaMapper = spesaMapper;
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
