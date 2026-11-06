package it.esselunga.gestionespese.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * Posizione geografica esposta dalle API come latitudine/longitudine.
 * Il mapper la converte nel formato GeoJSON usato su MongoDB ([longitudine, latitudine]).
 */
public class PosizioneDto {

    /** Latitudine, tra -90 e 90. */
    @NotNull(message = "La latitudine è obbligatoria")
    @DecimalMin(value = "-90.0", message = "La latitudine deve essere compresa tra -90 e 90")
    @DecimalMax(value = "90.0", message = "La latitudine deve essere compresa tra -90 e 90")
    private Double latitudine;

    /** Longitudine, tra -180 e 180. */
    @NotNull(message = "La longitudine è obbligatoria")
    @DecimalMin(value = "-180.0", message = "La longitudine deve essere compresa tra -180 e 180")
    @DecimalMax(value = "180.0", message = "La longitudine deve essere compresa tra -180 e 180")
    private Double longitudine;

    public Double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(Double latitudine) {
        this.latitudine = latitudine;
    }

    public Double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(Double longitudine) {
        this.longitudine = longitudine;
    }
}
