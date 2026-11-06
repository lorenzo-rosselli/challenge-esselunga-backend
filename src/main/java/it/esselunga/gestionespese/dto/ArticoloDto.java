package it.esselunga.gestionespese.dto;

import java.math.BigDecimal;

/**
 * Articolo del catalogo restituito dalle API.
 */
public class ArticoloDto {

    private String id;

    private String nome;

    private String descrizione;

    private BigDecimal prezzo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
