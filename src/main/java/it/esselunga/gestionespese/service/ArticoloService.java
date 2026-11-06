package it.esselunga.gestionespese.service;

import io.quarkus.panache.common.Sort;
import it.esselunga.gestionespese.dto.ArticoloDto;
import it.esselunga.gestionespese.mapper.ArticoloMapper;
import it.esselunga.gestionespese.repository.ArticoloRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Logica applicativa degli articoli del catalogo (sola lettura).
 */
@ApplicationScoped
public class ArticoloService {

    private final ArticoloRepository articoloRepository;
    private final ArticoloMapper articoloMapper;

    public ArticoloService(ArticoloRepository articoloRepository, ArticoloMapper articoloMapper) {
        this.articoloRepository = articoloRepository;
        this.articoloMapper = articoloMapper;
    }

    /**
     * @return tutti gli articoli del catalogo, ordinati per nome
     */
    public List<ArticoloDto> lista() {
        return articoloRepository.listAll(Sort.by("nome")).stream()
                .map(articoloMapper::toDto)
                .toList();
    }
}
