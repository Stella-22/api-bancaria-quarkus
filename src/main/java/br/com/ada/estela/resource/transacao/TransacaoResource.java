package br.com.ada.estela.resource.transacao;

import br.com.ada.estela.service.TransacaoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/transacoes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransacaoResource {

    @Inject
    private TransacaoService transacaoService;

    @GET
    @Path("/{id}")
    @RolesAllowed({"GERENTE","CLIENTE"})
    public Response buscarPorId(@PathParam("id") Long id) {
        TransacaoDTO transacao = transacaoService.buscarPorId(id);
        if (transacao == null) {
            throw new NotFoundException("Transação com id " + id + " não encontrado");
        }
        return Response.ok(transacao).build();
    }

    @GET
    @RolesAllowed({"GERENTE","CLIENTE"})
    public Response buscarPorContaId(@QueryParam("contaId") Long contaId) {
        return Response.ok(transacaoService.buscarPorContaId(contaId)).build();
    }

}
