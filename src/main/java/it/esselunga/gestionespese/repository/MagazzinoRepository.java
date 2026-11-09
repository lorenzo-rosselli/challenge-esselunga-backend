package it.esselunga.gestionespese.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import it.esselunga.gestionespese.entity.Magazzino;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Accesso alla collection "magazzini".
 */
@ApplicationScoped
public class MagazzinoRepository implements PanacheMongoRepository<Magazzino> {

    /**
     * Verifica se esiste già un magazzino con il nome indicato.
     *
     * @param nome nome da cercare (confronto esatto)
     * @return true se nella collection "magazzini" è presente almeno un documento con quel nome
     */
    public boolean isPresent(String nome) {
        return count("nome", nome) > 0;
    }
}
