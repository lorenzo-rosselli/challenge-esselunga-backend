package it.esselunga.gestionespese.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

/**
 * Magazzino: punto di partenza e di rientro del percorso di consegna.
 */
@MongoEntity(collection = "magazzini")
public class Magazzino {

    private ObjectId id;

    private String nome;

    private Posizione posizione;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Posizione getPosizione() {
        return posizione;
    }

    public void setPosizione(Posizione posizione) {
        this.posizione = posizione;
    }
}
