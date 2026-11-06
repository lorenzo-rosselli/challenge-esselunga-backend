package it.esselunga.gestionespese.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.esselunga.gestionespese.entity.Articolo;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;

/**
 * Accesso alla collection "articoli".
 */
@ApplicationScoped
public class ArticoloRepository implements PanacheMongoRepository<Articolo> {

    /**
     * Verifica se esiste già un articolo con il nome indicato.
     *
     * @param nome nome da cercare (confronto esatto)
     * @return true se nella collection "articoli" è presente almeno un documento con quel nome
     */
    public boolean isPreent(String nome) {
        return count("nome", nome) > 0;
    }

    /**
     * Restituisce gli articoli con gli id indicati, con un'unica query.
     * Gli id inesistenti vengono semplicemente ignorati.
     *
     * @param ids id degli articoli da cercare
     * @return articoli trovati
     */
    public List<Articolo> trovaPerIds(Collection<ObjectId> ids) {
        return find(new Document("_id", new Document("$in", ids))).list();
    }
}
