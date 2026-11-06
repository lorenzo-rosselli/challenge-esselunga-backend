package it.esselunga.gestionespese.dto;

/**
 * Magazzino restituito dalle API.
 */
public class MagazzinoDto {

    private String id;

    private String nome;

    private PosizioneDto posizione;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public PosizioneDto getPosizione() {
        return posizione;
    }

    public void setPosizione(PosizioneDto posizione) {
        this.posizione = posizione;
    }
}
