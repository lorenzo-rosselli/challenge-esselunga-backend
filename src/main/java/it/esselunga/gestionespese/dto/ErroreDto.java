package it.esselunga.gestionespese.dto;

import java.time.Instant;

/**
 * Corpo della risposta in caso di errore applicativo.
 */
public class ErroreDto {

    private int codice;

    private String errore;

    private String messaggio;

    private Instant timestamp;

    public ErroreDto() {
    }

    public ErroreDto(int codice, String errore, String messaggio) {
        this.codice = codice;
        this.errore = errore;
        this.messaggio = messaggio;
        this.timestamp = Instant.now();
    }

    public int getCodice() {
        return codice;
    }

    public void setCodice(int codice) {
        this.codice = codice;
    }

    public String getErrore() {
        return errore;
    }

    public void setErrore(String errore) {
        this.errore = errore;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
