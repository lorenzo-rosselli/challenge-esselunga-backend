package it.esselunga.gestionespese.service;

import io.quarkus.panache.common.Sort;
import it.esselunga.gestionespese.dto.MagazzinoDto;
import it.esselunga.gestionespese.mapper.MagazzinoMapper;
import it.esselunga.gestionespese.repository.MagazzinoRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Logica applicativa dei magazzini (sola lettura).
 */
@ApplicationScoped
public class MagazzinoService {

    private final MagazzinoRepository magazzinoRepository;
    private final MagazzinoMapper magazzinoMapper;

    public MagazzinoService(MagazzinoRepository magazzinoRepository, MagazzinoMapper magazzinoMapper) {
        this.magazzinoRepository = magazzinoRepository;
        this.magazzinoMapper = magazzinoMapper;
    }

    /**
     * @return tutti i magazzini, ordinati per nome
     */
    public List<MagazzinoDto> lista() {
        return magazzinoRepository.listAll(Sort.by("nome")).stream()
                .map(magazzinoMapper::toDto)
                .toList();
    }
}
