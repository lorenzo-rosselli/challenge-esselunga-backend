package it.esselunga.gestionespese.dto;

import it.esselunga.gestionespese.entity.StatoSpesa;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Spesa restituita dalle API (UC1, UC2, UC3).
 */
public class SpesaDto {

    private String id;

    private String nomeCliente;

    private String indirizzoConsegna;

    private PosizioneDto posizioneConsegna;

    private String magazzinoId;

    private StatoSpesa stato;

    private Instant dataCreazione;

    private Instant dataUltimaModifica;

    /** Da rimandare nella richiesta di modifica (UC2) per il controllo delle modifiche concorrenti. */
    private Long versione;

    private BigDecimal totale;

    private List<ElementoSpesaDto> articoli = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getIndirizzoConsegna() {
        return indirizzoConsegna;
    }

    public void setIndirizzoConsegna(String indirizzoConsegna) {
        this.indirizzoConsegna = indirizzoConsegna;
    }

    public PosizioneDto getPosizioneConsegna() {
        return posizioneConsegna;
    }

    public void setPosizioneConsegna(PosizioneDto posizioneConsegna) {
        this.posizioneConsegna = posizioneConsegna;
    }

    public String getMagazzinoId() {
        return magazzinoId;
    }

    public void setMagazzinoId(String magazzinoId) {
        this.magazzinoId = magazzinoId;
    }

    public StatoSpesa getStato() {
        return stato;
    }

    public void setStato(StatoSpesa stato) {
        this.stato = stato;
    }

    public Instant getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Instant dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public Instant getDataUltimaModifica() {
        return dataUltimaModifica;
    }

    public void setDataUltimaModifica(Instant dataUltimaModifica) {
        this.dataUltimaModifica = dataUltimaModifica;
    }

    public Long getVersione() {
        return versione;
    }

    public void setVersione(Long versione) {
        this.versione = versione;
    }

    public BigDecimal getTotale() {
        return totale;
    }

    public void setTotale(BigDecimal totale) {
        this.totale = totale;
    }

    public List<ElementoSpesaDto> getArticoli() {
        return articoli;
    }

    public void setArticoli(List<ElementoSpesaDto> articoli) {
        this.articoli = articoli;
    }
}
