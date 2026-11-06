package it.esselunga.gestionespese.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Spesa online: aggregato principale del dominio.
 * Contiene la lista degli articoli acquistati come documenti annidati.
 */
@MongoEntity(collection = "spese")
public class Spesa {

    private ObjectId id;

    private String nomeCliente;

    /** Modificabile in UC2. */
    private String indirizzoConsegna;

    /** Modificabile in UC2 insieme all'indirizzo. */
    private Posizione posizioneConsegna;

    /** Riferimento al magazzino di partenza e rientro del percorso (UC4). */
    private ObjectId magazzinoId;

    private StatoSpesa stato;

    private Instant dataCreazione;

    private Instant dataUltimaModifica;

    /** Contatore per le modifiche concorrenti: parte da 0, +1 a ogni modifica. */
    private long versione;

    /** Somma dei prezzoTotale degli elementi, calcolata dal server. */
    private BigDecimal totale;

    private List<ElementoSpesa> articoli = new ArrayList<>();

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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

    public Posizione getPosizioneConsegna() {
        return posizioneConsegna;
    }

    public void setPosizioneConsegna(Posizione posizioneConsegna) {
        this.posizioneConsegna = posizioneConsegna;
    }

    public ObjectId getMagazzinoId() {
        return magazzinoId;
    }

    public void setMagazzinoId(ObjectId magazzinoId) {
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

    public long getVersione() {
        return versione;
    }

    public void setVersione(long versione) {
        this.versione = versione;
    }

    public BigDecimal getTotale() {
        return totale;
    }

    public void setTotale(BigDecimal totale) {
        this.totale = totale;
    }

    public List<ElementoSpesa> getArticoli() {
        return articoli;
    }

    public void setArticoli(List<ElementoSpesa> articoli) {
        this.articoli = articoli;
    }
}
