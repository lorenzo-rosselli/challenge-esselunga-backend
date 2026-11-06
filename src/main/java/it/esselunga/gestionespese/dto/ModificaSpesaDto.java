package it.esselunga.gestionespese.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Richiesta di modifica parziale di una spesa (UC2).
 * <p>
 * Sono modificabili solo nome del cliente e dati di consegna, e solo negli stati
 * CREATA e IN_PREPARAZIONE. Il client invia soltanto i campi da cambiare:
 * quelli assenti (null) restano invariati.
 * Indirizzo e posizione di consegna vanno modificati insieme, per mantenerli coerenti
 * (la posizione è usata dal calcolo del percorso, UC4).
 */
public class ModificaSpesaDto {

    /** Se presente, non può essere vuoto. */
    @Pattern(regexp = ".*\\S.*", message = "Il nome del cliente non può essere vuoto")
    private String nomeCliente;

    /** Se presente, non può essere vuoto. Va inviato insieme a posizioneConsegna. */
    @Pattern(regexp = ".*\\S.*", message = "L'indirizzo di consegna non può essere vuoto")
    private String indirizzoConsegna;

    /** Va inviata insieme a indirizzoConsegna. */
    @Valid
    private PosizioneDto posizioneConsegna;

    /** Versione letta dal client: se nel frattempo la spesa è cambiata, la modifica viene rifiutata. */
    @NotNull(message = "La versione è obbligatoria")
    @PositiveOrZero(message = "La versione non può essere negativa")
    private Long versione;

    @JsonIgnore
    @AssertTrue(message = "Indicare almeno un campo da modificare (nomeCliente oppure indirizzoConsegna e posizioneConsegna)")
    public boolean isAlmenoUnCampoDaModificare() {
        return nomeCliente != null || indirizzoConsegna != null || posizioneConsegna != null;
    }

    @JsonIgnore
    @AssertTrue(message = "Indirizzo e posizione di consegna devono essere modificati insieme")
    public boolean isConsegnaCompleta() {
        return (indirizzoConsegna == null) == (posizioneConsegna == null);
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

    public Long getVersione() {
        return versione;
    }

    public void setVersione(Long versione) {
        this.versione = versione;
    }
}
