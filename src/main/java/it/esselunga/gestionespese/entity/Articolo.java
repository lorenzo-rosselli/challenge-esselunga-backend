package it.esselunga.gestionespese.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

import java.math.BigDecimal;

/**
 * Articolo del catalogo.
 * Assunzione: il prezzo è fisso e non varia nel tempo.
 */
@MongoEntity(collection = "articoli")
public class Articolo {

    private ObjectId id;

    private String nome;

    private String descrizione;

    private BigDecimal prezzo;

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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }
}
