package it.esselunga.gestionespese.resource;

import it.esselunga.gestionespese.dto.MagazzinoDto;
import it.esselunga.gestionespese.service.MagazzinoService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * Endpoint REST dei magazzini (sola lettura).
 */
@Path("/magazzini")
@Produces(MediaType.APPLICATION_JSON)
public class MagazzinoResource {

    private final MagazzinoService magazzinoService;

    public MagazzinoResource(MagazzinoService magazzinoService) {
        this.magazzinoService = magazzinoService;
    }

    /**
     * Elenco dei magazzini, utile al frontend per scegliere il magazzino della spesa.
     *
     * @return 200 con la lista dei magazzini ordinata per nome
     */
    @GET
    public List<MagazzinoDto> lista() {
        return magazzinoService.lista();
    }
}
