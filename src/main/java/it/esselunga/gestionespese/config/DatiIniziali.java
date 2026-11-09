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
        // Locker Esselunga dell'area Firenze - Prato - Pistoia; coordinate da OpenStreetMap
        List<Magazzino> magazzini = List.of(
                magazzino("Campi Bisenzio", 43.82168, 11.12161),
                magazzino("Firenze Osmannoro", 43.81062, 11.17371),
                magazzino("Firenze Via Canova", 43.77954, 11.19867),
                magazzino("Prato Leonardo da Vinci", 43.86479, 11.09654),
                magazzino("Firenze Novoli", 43.79122, 11.22319),
                magazzino("Sesto Fiorentino", 43.82637, 11.21228),
                magazzino("Prato Viale Galilei", 43.90354, 11.10815),
                magazzino("Pistoia Porta Nuova", 43.92326, 10.91338),
                magazzino("Firenze Gignoro", 43.76974, 11.29915));
        int inseriti = 0;
        for (Magazzino magazzino : magazzini) {
            if (!magazzinoRepository.isPresent(magazzino.getNome())) {
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
                articolo("Caffè 250g", "Caffè macinato per moka", "3.49"),
                articolo("Prosciutto 100g", "Prosciutto cotto a fette, vaschetta da 100 g", "2.49"),
                articolo("Petto di pollo 500g", "Petto di pollo a fette, vaschetta da 500 g", "5.90"),
                articolo("Tonno 3x80g", "Tonno all'olio di oliva, confezione da 3 scatolette", "3.29"),
                articolo("Sale 1kg", "Sale marino fino iodato", "0.49"),
                articolo("Biscotti 700g", "Biscotti frollini, pacco da 700 g", "2.29"),
                articolo("Acqua 6x1,5L", "Acqua minerale naturale, cassa da 6 bottiglie da 1,5 litri", "1.99"));
        int inseriti = 0;
        for (Articolo articolo : articoli) {
            if (!articoloRepository.isPresent(articolo.getNome())) {
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
