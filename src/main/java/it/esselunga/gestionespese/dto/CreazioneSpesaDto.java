package it.esselunga.gestionespese.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Richiesta di creazione di una spesa (UC1).
 * Id, stato, date, versione e totali non sono accettati dal client: li imposta il server.
 */
public class CreazioneSpesaDto {

    @NotBlank(message = "Il nome del cliente è obbligatorio")
    private String nomeCliente;

    @NotBlank(message = "L'indirizzo di consegna è obbligatorio")
    private String indirizzoConsegna;

    @NotNull(message = "La posizione di consegna è obbligatoria")
    @Valid
    private PosizioneDto posizioneConsegna;

    @NotBlank(message = "Il magazzino è obbligatorio")
    @Pattern(regexp = "^[0-9a-fA-F]{24}$", message = "Il magazzino non è un identificativo valido")
    private String magazzinoId;

    /** Almeno un elemento. */
    @NotEmpty(message = "La spesa deve contenere almeno un articolo")
    @Valid
    private List<@NotNull(message = "Gli elementi della spesa non possono essere nulli") ElementoCreazioneSpesaDto> articoli = new ArrayList<>();

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

    public List<ElementoCreazioneSpesaDto> getArticoli() {
        return articoli;
    }

    public void setArticoli(List<ElementoCreazioneSpesaDto> articoli) {
        this.articoli = articoli;
    }
}
