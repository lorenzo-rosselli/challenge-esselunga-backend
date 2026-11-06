package it.esselunga.gestionespese.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Elemento della richiesta di creazione di una spesa.
 * Il client indica solo articolo e quantità: nome, prezzo e prezzoTotale sono valorizzati dal server.
 */
public class ElementoCreazioneSpesaDto {

    @NotBlank(message = "L'articolo è obbligatorio")
    @Pattern(regexp = "^[0-9a-fA-F]{24}$", message = "L'articolo non è un identificativo valido")
    private String articoloId;

    @NotNull(message = "La quantità è obbligatoria")
    @Positive(message = "La quantità deve essere maggiore di zero")
    private Integer quantita;

    public String getArticoloId() {
        return articoloId;
    }

    public void setArticoloId(String articoloId) {
        this.articoloId = articoloId;
    }

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }
}
