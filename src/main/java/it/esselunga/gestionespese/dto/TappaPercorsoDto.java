package it.esselunga.gestionespese.dto;

/**
 * Tappa del percorso di consegna (UC4): una spesa da consegnare.
 */
public class TappaPercorsoDto {

    /** Posizione della tappa nel percorso, a partire da 1. */
    private int ordine;

    private String spesaId;

    private String nomeCliente;

    private String indirizzoConsegna;

    private PosizioneDto posizione;

    /** Distanza in linea d'aria dal punto precedente (il magazzino per la prima tappa), in km. */
    private double distanzaDallaPrecedenteKm;

    public int getOrdine() {
        return ordine;
    }

    public void setOrdine(int ordine) {
        this.ordine = ordine;
    }

    public String getSpesaId() {
        return spesaId;
    }

    public void setSpesaId(String spesaId) {
        this.spesaId = spesaId;
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

    public PosizioneDto getPosizione() {
        return posizione;
    }

    public void setPosizione(PosizioneDto posizione) {
        this.posizione = posizione;
    }

    public double getDistanzaDallaPrecedenteKm() {
        return distanzaDallaPrecedenteKm;
    }

    public void setDistanzaDallaPrecedenteKm(double distanzaDallaPrecedenteKm) {
        this.distanzaDallaPrecedenteKm = distanzaDallaPrecedenteKm;
    }
}
