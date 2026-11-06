package it.esselunga.gestionespese.resource;

import it.esselunga.gestionespese.dto.CreazioneSpesaDto;
import it.esselunga.gestionespese.dto.ModificaSpesaDto;
import it.esselunga.gestionespese.dto.SpesaDto;
import it.esselunga.gestionespese.service.SpesaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Endpoint REST delle spese.
 */
@Path("/spese")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SpesaResource {

    private final SpesaService spesaService;

    public SpesaResource(SpesaService spesaService) {
        this.spesaService = spesaService;
    }

    /**
     * Lista di tutte le spese.
     *
     * @return 200 con tutte le spese, dalla più recente
     */
    @GET
    public List<SpesaDto> lista() {
        return spesaService.lista();
    }

    /**
     * Creazione di una spesa.
     *
     * @return 201 se la spesa è creata; 400 se la richiesta non è valida
     */
    @POST
    public Response crea(@NotNull(message = "Il corpo della richiesta è obbligatorio") @Valid CreazioneSpesaDto dto) {
        SpesaDto spesa = spesaService.crea(dto);
        return Response.status(Response.Status.CREATED).entity(spesa).build();
    }

    /**
     * UC2 - Modifica parziale di una spesa (nome cliente, indirizzo e posizione di consegna).
     *
     * @return 200 con la spesa aggiornata; 400 se la richiesta non è valida; 404 se la spesa non esiste;
     *         409 se la spesa non è più modificabile o è stata modificata nel frattempo
     */
    @PATCH
    @Path("/{id}")
    public SpesaDto modifica(@PathParam("id") String id,
                             @NotNull(message = "Il corpo della richiesta è obbligatorio") @Valid ModificaSpesaDto dto) {
        return spesaService.modifica(id, dto);
    }
}
