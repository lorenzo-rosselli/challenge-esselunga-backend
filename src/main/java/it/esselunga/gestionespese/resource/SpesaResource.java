package it.esselunga.gestionespese.resource;

import it.esselunga.gestionespese.dto.CreazioneSpesaDto;
import it.esselunga.gestionespese.dto.SpesaDto;
import it.esselunga.gestionespese.service.SpesaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
     * Creazione di una spesa.
     *
     * @return 201 se la spesa è creata; 400 se la richiesta non è valida
     */
    @POST
    public Response crea(@NotNull(message = "Il corpo della richiesta è obbligatorio") @Valid CreazioneSpesaDto dto) {
        SpesaDto spesa = spesaService.crea(dto);
        return Response.status(Response.Status.CREATED).entity(spesa).build();
    }
}
