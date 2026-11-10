package it.esselunga.gestionespese.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Percorso di consegna di un magazzino.
 * Parte dal magazzino, visita le tappe nell'ordine indicato e rientra al magazzino.
 */
public class PercorsoDto {

    /** Punto di partenza e di rientro. */
    private MagazzinoDto magazzino;

    /** Tappe nell'ordine di consegna. */
    private List<TappaPercorsoDto> tappe = new ArrayList<>();

    /** Distanza dall'ultima tappa al magazzino, in km. */
    private double distanzaRientroKm;

    /** Lunghezza totale del giro, rientro compreso, in km. */
    private double distanzaTotaleKm;

    public MagazzinoDto getMagazzino() {
        return magazzino;
    }

    public void setMagazzino(MagazzinoDto magazzino) {
        this.magazzino = magazzino;
    }

    public List<TappaPercorsoDto> getTappe() {
        return tappe;
    }

    public void setTappe(List<TappaPercorsoDto> tappe) {
        this.tappe = tappe;
    }

    public double getDistanzaRientroKm() {
        return distanzaRientroKm;
    }

    public void setDistanzaRientroKm(double distanzaRientroKm) {
        this.distanzaRientroKm = distanzaRientroKm;
    }

    public double getDistanzaTotaleKm() {
        return distanzaTotaleKm;
    }

    public void setDistanzaTotaleKm(double distanzaTotaleKm) {
        this.distanzaTotaleKm = distanzaTotaleKm;
    }
}
