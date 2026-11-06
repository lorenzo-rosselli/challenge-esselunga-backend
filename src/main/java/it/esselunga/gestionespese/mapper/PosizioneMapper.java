package it.esselunga.gestionespese.mapper;

import it.esselunga.gestionespese.dto.PosizioneDto;
import it.esselunga.gestionespese.entity.Posizione;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Conversione tra PosizioneDto (latitudine, longitudine) e Posizione GeoJSON.
 * In GeoJSON l'ordine delle coordinate è [longitudine, latitudine].
 */
@ApplicationScoped
public class PosizioneMapper {

    private static final int LONGITUDINE = 0;
    private static final int LATITUDINE = 1;

    public Posizione toEntity(PosizioneDto dto) {
        if (dto == null) {
            return null;
        }
        Posizione posizione = new Posizione();
        posizione.setCoordinates(List.of(dto.getLongitudine(), dto.getLatitudine()));
        return posizione;
    }

    public PosizioneDto toDto(Posizione posizione) {
        if (posizione == null || posizione.getCoordinates() == null) {
            return null;
        }
        PosizioneDto dto = new PosizioneDto();
        dto.setLatitudine(posizione.getCoordinates().get(LATITUDINE));
        dto.setLongitudine(posizione.getCoordinates().get(LONGITUDINE));
        return dto;
    }
}
