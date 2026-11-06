package it.esselunga.gestionespese.resource;

import it.esselunga.gestionespese.dto.ArticoloDto;
import it.esselunga.gestionespese.service.ArticoloService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * Endpoint REST del catalogo articoli (sola lettura).
 */
@Path("/articoli")
@Produces(MediaType.APPLICATION_JSON)
public class ArticoloResource {

    private final ArticoloService articoloService;

    public ArticoloResource(ArticoloService articoloService) {
        this.articoloService = articoloService;
    }

    /**
     * Catalogo articoli: il frontend lo usa per comporre la spesa e mostrare
     * in anteprima prezzi e totale.
     *
     * @return 200 con la lista degli articoli ordinata per nome
     */
    @GET
    public List<ArticoloDto> lista() {
        return articoloService.lista();
    }
}
