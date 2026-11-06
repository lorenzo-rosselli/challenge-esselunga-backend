package it.esselunga.gestionespese.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Richiesta di modifica di una spesa (UC2).
 * Sono modificabili solo i dati di consegna, e solo negli stati CREATA e IN_PREPARAZIONE.
 */
public class ModificaSpesaDto {

    @NotBlank(message = "L'indirizzo di consegna è obbligatorio")
    private String indirizzoConsegna;

    @NotNull(message = "La posizione di consegna è obbligatoria")
    @Valid
    private PosizioneDto posizioneConsegna;

    /** Versione letta dal client: se nel frattempo la spesa è cambiata, la modifica viene rifiutata. */
    @NotNull(message = "La versione è obbligatoria")
    @PositiveOrZero(message = "La versione non può essere negativa")
    private Long versione;

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

    public Long getVersione() {
        return versione;
    }

    public void setVersione(Long versione) {
        this.versione = versione;
    }
}
