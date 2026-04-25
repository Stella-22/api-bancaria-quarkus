package br.com.ada.estela.resource.conta;

import br.com.ada.estela.service.ContaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@Path("/contas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContaResource {

    @Inject
    private ContaService contaService;

    @POST
    @Transactional
    @RolesAllowed("GERENTE")
    public Response cadastrar(
            @Valid ContaDTO contaDTO,
            @Context UriInfo uriInfo) {

        ContaDTO contaSalva = contaService.cadastrar(contaDTO);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(contaSalva.getId().toString())
                .build();
        return Response.created(location)
                .entity(contaSalva)
                .build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"GERENTE","CLIENTE"})
    public Response buscarPorId(@PathParam("id") Long id,
                                @Context UriInfo uriInfo) {
        ContaDTO conta = contaService.buscarPorId(id, uriInfo);
        if (conta == null) {
            throw new NotFoundException("Cliente com id " + id + " nao encontrado");
        }
        return Response.ok(conta).build();
    }
}
