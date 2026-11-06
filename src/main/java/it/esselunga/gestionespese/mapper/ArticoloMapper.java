package it.esselunga.gestionespese.mapper;

import it.esselunga.gestionespese.dto.ArticoloDto;
import it.esselunga.gestionespese.entity.Articolo;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Conversione da Articolo (entity) ad ArticoloDto.
 */
@ApplicationScoped
public class ArticoloMapper {

    private final IdMapper idMapper;

    public ArticoloMapper(IdMapper idMapper) {
        this.idMapper = idMapper;
    }

    public ArticoloDto toDto(Articolo articolo) {
        if (articolo == null) {
            return null;
        }
        ArticoloDto dto = new ArticoloDto();
        dto.setId(idMapper.toString(articolo.getId()));
        dto.setNome(articolo.getNome());
        dto.setDescrizione(articolo.getDescrizione());
        dto.setPrezzo(articolo.getPrezzo());
        return dto;
    }
}
