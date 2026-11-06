package it.esselunga.gestionespese.entity;

import org.bson.types.ObjectId;

import java.math.BigDecimal;

/**
 * Elemento della lista articoli di una spesa (documento annidato, non è una collection).
 * Nome e prezzo sono copiati dall'articolo in fase di creazione;
 * prezzoTotale = prezzo x quantita, calcolato dal server.
 */
public class ElementoSpesa {

    private ObjectId articoloId;

    private String nome;

    private BigDecimal prezzo;

    private int quantita;

    private BigDecimal prezzoTotale;

    public ObjectId getArticoloId() {
        return articoloId;
    }

    public void setArticoloId(ObjectId articoloId) {
        this.articoloId = articoloId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public BigDecimal getPrezzoTotale() {
        return prezzoTotale;
    }

    public void setPrezzoTotale(BigDecimal prezzoTotale) {
        this.prezzoTotale = prezzoTotale;
    }
}
