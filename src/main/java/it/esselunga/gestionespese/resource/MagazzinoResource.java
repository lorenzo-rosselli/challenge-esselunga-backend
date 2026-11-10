package it.esselunga.gestionespese.resource;

import it.esselunga.gestionespese.dto.MagazzinoDto;
import it.esselunga.gestionespese.dto.PercorsoDto;
import it.esselunga.gestionespese.service.MagazzinoService;
import it.esselunga.gestionespese.service.PercorsoService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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
    private final PercorsoService percorsoService;

    public MagazzinoResource(MagazzinoService magazzinoService, PercorsoService percorsoService) {
        this.magazzinoService = magazzinoService;
        this.percorsoService = percorsoService;
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

    /**
     * Percorso di consegna delle spese PRONTE del magazzino:
     * partenza dal magazzino, tappe in ordine di consegna, rientro al magazzino.
     *
     * @return 200 con il percorso (senza tappe se non ci sono spese pronte); 404 se il magazzino non esiste
     */
    @GET
    @Path("/{id}/percorso")
    public PercorsoDto percorso(@PathParam("id") String id) {
        return percorsoService.calcola(id);
    }
}
