package it.esselunga.gestionespese.exception;

import it.esselunga.gestionespese.dto.ErroreDto;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Gestione centralizzata degli errori applicativi.
 * <p>
 * Il codice applicativo usa le eccezioni standard di Jakarta REST, con un messaggio
 * specifico per ogni caso:
 * <ul>
 *     <li>{@code NotFoundException} → 404 (risorsa dell'URL inesistente)</li>
 *     <li>{@code BadRequestException} → 400 (dati della richiesta non coerenti)</li>
 *     <li>{@code ClientErrorException(messaggio, Response.Status.CONFLICT)} → 409
 *         (spesa non modificabile, conflitto di versione)</li>
 * </ul>
 * Di default Quarkus restituirebbe lo status corretto ma con corpo vuoto: questo mapper
 * aggiunge il messaggio nel corpo della risposta come {@link ErroreDto}.
 * <p>
 * Gli errori di validazione dei DTO (Bean Validation) sono gestiti automaticamente
 * da Quarkus con risposta 400.
 */
public class GestoreEccezioni {

    @ServerExceptionMapper
    public Response gestisci(WebApplicationException e) {
        Response.StatusType status = e.getResponse().getStatusInfo();
        ErroreDto errore = new ErroreDto(status.getStatusCode(), status.getReasonPhrase(), e.getMessage());
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(errore)
                .build();
    }
}
