package it.esselunga.gestionespese.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.esselunga.gestionespese.entity.Spesa;
import it.esselunga.gestionespese.entity.StatoSpesa;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Accesso alla collection "spese".
 */
@ApplicationScoped
public class SpesaRepository implements PanacheMongoRepository<Spesa> {

    /**
     * Spese di un magazzino che si trovano nello stato indicato,
     * dalla meno recente alla più recente.
     *
     * @param magazzinoId id del magazzino
     * @param stato       stato delle spese da cercare
     * @return spese trovate
     */
    public List<Spesa> trovaPerMagazzinoEStato(ObjectId magazzinoId, StatoSpesa stato) {
        Document filtro = new Document("magazzinoId", magazzinoId).append("stato", stato.name());
        Document ordinamento = new Document("dataCreazione", 1); // 1 = crescente
        return find(filtro, ordinamento).list();
    }
}
