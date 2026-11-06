package it.esselunga.gestionespese.mapper;

import it.esselunga.gestionespese.dto.CreazioneSpesaDto;
import it.esselunga.gestionespese.dto.ElementoCreazioneSpesaDto;
import it.esselunga.gestionespese.dto.ElementoSpesaDto;
import it.esselunga.gestionespese.dto.SpesaDto;
import it.esselunga.gestionespese.entity.ElementoSpesa;
import it.esselunga.gestionespese.entity.Spesa;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

/**
 * Conversione tra i DTO della spesa e l'entity Spesa.
 * <p>
 * Il mapper converte solo i dati ricevuti o da restituire: i campi impostati dal server
 * (stato, date, versione, nome e prezzo degli articoli, totali) sono valorizzati nel service.
 */
@ApplicationScoped
public class SpesaMapper {

    private final IdMapper idMapper;
    private final PosizioneMapper posizioneMapper;

    public SpesaMapper(IdMapper idMapper, PosizioneMapper posizioneMapper) {
        this.idMapper = idMapper;
        this.posizioneMapper = posizioneMapper;
    }

    public Spesa toEntity(CreazioneSpesaDto dto) {
        if (dto == null) {
            return null;
        }
        Spesa spesa = new Spesa();
        spesa.setNomeCliente(dto.getNomeCliente());
        spesa.setIndirizzoConsegna(dto.getIndirizzoConsegna());
        spesa.setPosizioneConsegna(posizioneMapper.toEntity(dto.getPosizioneConsegna()));
        spesa.setMagazzinoId(idMapper.toObjectId(dto.getMagazzinoId()));
        spesa.setArticoli(toEntityList(dto.getArticoli()));
        return spesa;
    }

    public ElementoSpesa toEntity(ElementoCreazioneSpesaDto dto) {
        if (dto == null) {
            return null;
        }
        ElementoSpesa elemento = new ElementoSpesa();
        elemento.setArticoloId(idMapper.toObjectId(dto.getArticoloId()));
        elemento.setQuantita(dto.getQuantita());
        return elemento;
    }

    // --- Entity -> risposta ---

    public SpesaDto toDto(Spesa spesa) {
        if (spesa == null) {
            return null;
        }
        SpesaDto dto = new SpesaDto();
        dto.setId(idMapper.toString(spesa.getId()));
        dto.setNomeCliente(spesa.getNomeCliente());
        dto.setIndirizzoConsegna(spesa.getIndirizzoConsegna());
        dto.setPosizioneConsegna(posizioneMapper.toDto(spesa.getPosizioneConsegna()));
        dto.setMagazzinoId(idMapper.toString(spesa.getMagazzinoId()));
        dto.setStato(spesa.getStato());
        dto.setDataCreazione(spesa.getDataCreazione());
        dto.setDataUltimaModifica(spesa.getDataUltimaModifica());
        dto.setVersione(spesa.getVersione());
        dto.setTotale(spesa.getTotale());
        dto.setArticoli(toDtoList(spesa.getArticoli()));
        return dto;
    }

    public ElementoSpesaDto toDto(ElementoSpesa elemento) {
        if (elemento == null) {
            return null;
        }
        ElementoSpesaDto dto = new ElementoSpesaDto();
        dto.setArticoloId(idMapper.toString(elemento.getArticoloId()));
        dto.setNome(elemento.getNome());
        dto.setPrezzo(elemento.getPrezzo());
        dto.setQuantita(elemento.getQuantita());
        dto.setPrezzoTotale(elemento.getPrezzoTotale());
        return dto;
    }

    // --- Liste ---

    private List<ElementoSpesa> toEntityList(List<ElementoCreazioneSpesaDto> dtos) {
        List<ElementoSpesa> elementi = new ArrayList<>();
        if (dtos != null) {
            dtos.forEach(dto -> elementi.add(toEntity(dto)));
        }
        return elementi;
    }

    private List<ElementoSpesaDto> toDtoList(List<ElementoSpesa> elementi) {
        List<ElementoSpesaDto> dtos = new ArrayList<>();
        if (elementi != null) {
            elementi.forEach(elemento -> dtos.add(toDto(elemento)));
        }
        return dtos;
    }
}
