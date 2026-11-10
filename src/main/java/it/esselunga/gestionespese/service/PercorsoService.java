package it.esselunga.gestionespese.service;

import it.esselunga.gestionespese.dto.PercorsoDto;
import it.esselunga.gestionespese.dto.TappaPercorsoDto;
import it.esselunga.gestionespese.entity.Magazzino;
import it.esselunga.gestionespese.entity.Posizione;
import it.esselunga.gestionespese.entity.Spesa;
import it.esselunga.gestionespese.entity.StatoSpesa;
import it.esselunga.gestionespese.mapper.MagazzinoMapper;
import it.esselunga.gestionespese.mapper.PosizioneMapper;
import it.esselunga.gestionespese.repository.MagazzinoRepository;
import it.esselunga.gestionespese.repository.SpesaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Calcolo del percorso di consegna.
 * <p>
 * I punti (magazzino e consegne delle spese PRONTE) formano un grafo completo: ogni punto
 * ha la lista di tutti gli altri punti raggiungibili.
 * <ol>
 *     <li>si parte dal magazzino;</li>
 *     <li>dal punto corrente si sceglie, nella sua lista, il punto più vicino
 *         (teorema di Pitagora su latitudine e longitudine);</li>
 *     <li>il punto da cui si proviene viene eliminato dalle liste di tutti gli altri punti,
 *         così non può più essere scelto;</li>
 *     <li>si ripete finché la lista del punto corrente non è vuota, cioè finché tutti i punti
 *         sono stati visitati;</li>
 *     <li>infine si rientra al magazzino.</li>
 * </ol>
 * Le distanze restituite al client sono invece in km, calcolate in linea d'aria con la formula
 * dell'emisenoverso (haversine), perché i gradi non sono una misura leggibile per l'utente.
 * Il tracciato stradale è disegnato dal frontend seguendo l'ordine calcolato qui.
 */
@ApplicationScoped
public class PercorsoService {

    private static final double RAGGIO_TERRA_KM = 6371.0;
    private static final int LONGITUDINE = 0;
    private static final int LATITUDINE = 1;

    private final MagazzinoRepository magazzinoRepository;
    private final SpesaRepository spesaRepository;
    private final MagazzinoMapper magazzinoMapper;
    private final PosizioneMapper posizioneMapper;

    public PercorsoService(MagazzinoRepository magazzinoRepository,
                           SpesaRepository spesaRepository,
                           MagazzinoMapper magazzinoMapper,
                           PosizioneMapper posizioneMapper) {
        this.magazzinoRepository = magazzinoRepository;
        this.spesaRepository = spesaRepository;
        this.magazzinoMapper = magazzinoMapper;
        this.posizioneMapper = posizioneMapper;
    }

    /**
     * Punto del grafo: il magazzino (spesa null) oppure la consegna di una spesa.
     */
    private record Punto(Spesa spesa, Posizione posizione) {
    }

    /**
     * Calcola il giro di consegna delle spese PRONTE del magazzino indicato.
     * Se non ci sono spese pronte il percorso è vuoto (nessuna tappa, distanza 0).
     *
     * @param magazzinoId id del magazzino (dal percorso della richiesta)
     * @return percorso con le tappe ordinate e le distanze
     */
    public PercorsoDto calcola(String magazzinoId) {
        Magazzino magazzino = trovaMagazzino(magazzinoId);
        List<Spesa> spesePronte = spesaRepository.trovaPerMagazzinoEStato(magazzino.getId(), StatoSpesa.PRONTA);

        // Costruzione del grafo: ogni punto ha la lista di tutti gli altri punti
        Punto partenza = new Punto(null, magazzino.getPosizione());
        List<Punto> punti = new ArrayList<>();
        punti.add(partenza);
        spesePronte.forEach(spesa -> punti.add(new Punto(spesa, spesa.getPosizioneConsegna())));

        Map<Punto, List<Punto>> grafo = new LinkedHashMap<>();
        for (Punto punto : punti) {
            List<Punto> altri = new ArrayList<>(punti);
            altri.remove(punto);
            grafo.put(punto, altri);
        }

        // Visita: dal punto corrente si va al più vicino, eliminando il punto di provenienza
        PercorsoDto percorso = new PercorsoDto();
        percorso.setMagazzino(magazzinoMapper.toDto(magazzino));

        Punto corrente = partenza;
        double distanzaTotale = 0;
        int ordine = 1;

        while (!grafo.get(corrente).isEmpty()) {
            Punto piuVicino = piuVicino(corrente, grafo.get(corrente));

            for (List<Punto> lista : grafo.values()) {
                lista.remove(corrente);
            }

            double distanzaKm = distanzaKm(corrente.posizione(), piuVicino.posizione());
            percorso.getTappe().add(tappa(ordine++, piuVicino.spesa(), distanzaKm));
            distanzaTotale += distanzaKm;
            corrente = piuVicino;
        }

        // Rientro al magazzino
        double distanzaRientro = distanzaKm(corrente.posizione(), partenza.posizione());
        distanzaTotale += distanzaRientro;

        percorso.setDistanzaRientroKm(arrotonda(distanzaRientro));
        percorso.setDistanzaTotaleKm(arrotonda(distanzaTotale));
        return percorso;
    }

    private Magazzino trovaMagazzino(String magazzinoId) {
        Magazzino magazzino = ObjectId.isValid(magazzinoId)
                ? magazzinoRepository.findById(new ObjectId(magazzinoId))
                : null;
        if (magazzino == null) {
            throw new NotFoundException("Magazzino " + magazzinoId + " non trovato");
        }
        return magazzino;
    }

    /**
     * Punto della lista più vicino al punto corrente (a parità di distanza vince il primo della lista).
     */
    private Punto piuVicino(Punto corrente, List<Punto> candidati) {
        Punto piuVicino = candidati.get(0);
        double distanzaMinima = distanzaPitagora(corrente.posizione(), piuVicino.posizione());
        for (Punto candidato : candidati) {
            double distanza = distanzaPitagora(corrente.posizione(), candidato.posizione());
            if (distanza < distanzaMinima) {
                distanzaMinima = distanza;
                piuVicino = candidato;
            }
        }
        return piuVicino;
    }

    /**
     * Distanza usata per scegliere il punto più vicino: teorema di Pitagora
     * sulle differenze di latitudine e longitudine (in gradi).
     */
    double distanzaPitagora(Posizione da, Posizione a) {
        double deltaLat = a.getCoordinates().get(LATITUDINE) - da.getCoordinates().get(LATITUDINE);
        double deltaLon = a.getCoordinates().get(LONGITUDINE) - da.getCoordinates().get(LONGITUDINE);
        return Math.sqrt(deltaLat * deltaLat + deltaLon * deltaLon);
    }

    /**
     * Distanza in linea d'aria in km (formula dell'emisenoverso), usata solo per le informazioni restituite.
     */
    double distanzaKm(Posizione da, Posizione a) {
        double lat1 = Math.toRadians(da.getCoordinates().get(LATITUDINE));
        double lat2 = Math.toRadians(a.getCoordinates().get(LATITUDINE));
        double deltaLat = lat2 - lat1;
        double deltaLon = Math.toRadians(a.getCoordinates().get(LONGITUDINE) - da.getCoordinates().get(LONGITUDINE));

        double h = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        return 2 * RAGGIO_TERRA_KM * Math.asin(Math.sqrt(h));
    }

    private TappaPercorsoDto tappa(int ordine, Spesa spesa, double distanzaDallaPrecedenteKm) {
        TappaPercorsoDto tappa = new TappaPercorsoDto();
        tappa.setOrdine(ordine);
        tappa.setSpesaId(spesa.getId().toHexString());
        tappa.setNomeCliente(spesa.getNomeCliente());
        tappa.setIndirizzoConsegna(spesa.getIndirizzoConsegna());
        tappa.setPosizione(posizioneMapper.toDto(spesa.getPosizioneConsegna()));
        tappa.setDistanzaDallaPrecedenteKm(arrotonda(distanzaDallaPrecedenteKm));
        return tappa;
    }

    private static double arrotonda(double km) {
        return Math.round(km * 100) / 100.0;
    }
}
