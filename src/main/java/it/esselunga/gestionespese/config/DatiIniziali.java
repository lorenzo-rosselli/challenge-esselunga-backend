package it.esselunga.gestionespese.config;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import it.esselunga.gestionespese.entity.Articolo;
import it.esselunga.gestionespese.entity.Magazzino;
import it.esselunga.gestionespese.entity.Posizione;
import it.esselunga.gestionespese.repository.ArticoloRepository;
import it.esselunga.gestionespese.repository.MagazzinoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Caricamento dei dati iniziali all'avvio dell'applicazione.
 * <p>
 * Il cliente non richiede API per gestire magazzini e articoli, ma sono necessari
 * per creare una spesa: vengono quindi inseriti all'avvio.
 * <p>
 * Ogni elemento viene inserito solo se non ne esiste già uno con lo stesso nome,
 * per evitare duplicati ai riavvii successivi (i dati già presenti non vengono mai sovrascritti).
 */
@ApplicationScoped
public class DatiIniziali {

    private final MagazzinoRepository magazzinoRepository;
    private final ArticoloRepository articoloRepository;

    public DatiIniziali(MagazzinoRepository magazzinoRepository, ArticoloRepository articoloRepository) {
        this.magazzinoRepository = magazzinoRepository;
        this.articoloRepository = articoloRepository;
    }

    void onStart(@Observes StartupEvent event) {
        caricaMagazzini();
        caricaArticoli();
    }

    private void caricaMagazzini() {
        List<Magazzino> magazzini = List.of(
                magazzino("Magazzino Pioltello", 45.4977, 9.3301),
                magazzino("Magazzino Biandrate", 45.4528, 8.4651));
        int inseriti = 0;
        for (Magazzino magazzino : magazzini) {
            if (!magazzinoRepository.isPreent(magazzino.getNome())) {
                magazzinoRepository.persist(magazzino);
                inseriti++;
            }
        }
        Log.infof("Dati iniziali: inseriti %d magazzini su %d", inseriti, magazzini.size());
    }

    private void caricaArticoli() {
        List<Articolo> articoli = List.of(
                articolo("Latte 1L", "Latte fresco intero, bottiglia da 1 litro", "1.29"),
                articolo("Spaghetti 500g", "Spaghetti di semola di grano duro", "0.89"),
                articolo("Pane 500g", "Pane fresco di giornata", "1.60"),
                articolo("Uova x6", "Uova fresche di categoria A, confezione da 6", "2.19"),
                articolo("Mele 1kg", "Mele Golden Delicious, sacchetto da 1 kg", "1.99"),
                articolo("Passata 500g", "Passata di pomodoro italiano", "1.09"),
                articolo("Olio 1L", "Olio extravergine di oliva", "8.90"),
                articolo("Caffè 250g", "Caffè macinato per moka", "3.49"));
        int inseriti = 0;
        for (Articolo articolo : articoli) {
            if (!articoloRepository.isPreent(articolo.getNome())) {
                articoloRepository.persist(articolo);
                inseriti++;
            }
        }
        Log.infof("Dati iniziali: inseriti %d articoli su %d", inseriti, articoli.size());
    }

    private static Magazzino magazzino(String nome, double latitudine, double longitudine) {
        Posizione posizione = new Posizione();
        posizione.setCoordinates(List.of(longitudine, latitudine)); // GeoJSON: [longitudine, latitudine]
        Magazzino magazzino = new Magazzino();
        magazzino.setNome(nome);
        magazzino.setPosizione(posizione);
        return magazzino;
    }

    private static Articolo articolo(String nome, String descrizione, String prezzo) {
        Articolo articolo = new Articolo();
        articolo.setNome(nome);
        articolo.setDescrizione(descrizione);
        articolo.setPrezzo(new BigDecimal(prezzo));
        return articolo;
    }
}
