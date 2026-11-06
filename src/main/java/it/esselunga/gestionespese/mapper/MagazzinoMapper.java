package it.esselunga.gestionespese.mapper;

import it.esselunga.gestionespese.dto.MagazzinoDto;
import it.esselunga.gestionespese.entity.Magazzino;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Conversione da Magazzino (entity) a MagazzinoDto.
 */
@ApplicationScoped
public class MagazzinoMapper {

    private final IdMapper idMapper;
    private final PosizioneMapper posizioneMapper;

    public MagazzinoMapper(IdMapper idMapper, PosizioneMapper posizioneMapper) {
        this.idMapper = idMapper;
        this.posizioneMapper = posizioneMapper;
    }

    public MagazzinoDto toDto(Magazzino magazzino) {
        if (magazzino == null) {
            return null;
        }
        MagazzinoDto dto = new MagazzinoDto();
        dto.setId(idMapper.toString(magazzino.getId()));
        dto.setNome(magazzino.getNome());
        dto.setPosizione(posizioneMapper.toDto(magazzino.getPosizione()));
        return dto;
    }
}
