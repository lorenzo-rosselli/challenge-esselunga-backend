package it.esselunga.gestionespese.dto;

import java.math.BigDecimal;

/**
 * Elemento della lista articoli di una spesa, restituito dalle API.
 */
public class ElementoSpesaDto {

    private String articoloId;

    private String nome;

    private BigDecimal prezzo;

    private Integer quantita;

    private BigDecimal prezzoTotale;

    public String getArticoloId() {
        return articoloId;
    }

    public void setArticoloId(String articoloId) {
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

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }

    public BigDecimal getPrezzoTotale() {
        return prezzoTotale;
    }

    public void setPrezzoTotale(BigDecimal prezzoTotale) {
        this.prezzoTotale = prezzoTotale;
    }
}
