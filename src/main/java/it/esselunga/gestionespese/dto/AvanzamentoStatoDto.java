package it.esselunga.gestionespese.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Richiesta di avanzamento della spesa allo stato successivo.
 * <p>
 * Il client non indica il nuovo stato: lo determina il server seguendo il flusso
 * CREATA → IN_PREPARAZIONE → PRONTA → IN_CONSEGNA → CONSEGNATA.
 * La versione serve a evitare avanzamenti doppi (es. doppio clic sul bottone)
 * o basati su dati non aggiornati.
 */
public class AvanzamentoStatoDto {

    /** Versione letta dal client: se nel frattempo la spesa è cambiata, l'avanzamento viene rifiutato. */
    @NotNull(message = "La versione è obbligatoria")
    @PositiveOrZero(message = "La versione non può essere negativa")
    private Long versione;

    public Long getVersione() {
        return versione;
    }

    public void setVersione(Long versione) {
        this.versione = versione;
    }
}
