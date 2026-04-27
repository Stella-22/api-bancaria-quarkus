package br.com.ada.estela.resource.conta;

import br.com.ada.estela.exception.UnprocessableEntityException;
import br.com.ada.estela.resource.transacao.TransacaoDTO;
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
            throw new NotFoundException("Conta com id " + id + " não encontrado");
        }
        return Response.ok(conta).build();
    }

    @POST
    @Path("/{id}/deposito")
    @RolesAllowed({"GERENTE","CLIENTE"})
    public Response depositar(@PathParam("id") Long id, @Valid TransacaoDTO transacaoDTO) {
        try {
            TransacaoDTO transacao = contaService.depositar(id, transacaoDTO);
            transacao.setSaldoAtual(contaService.getSaldoAtual(id));
            return Response.ok(transacao).build();
        } catch (UnprocessableEntityException e) {
            return Response.status(422)
                    .entity(String.format("{ \"erro\": \"%s\" }", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/{id}/saque")
    @RolesAllowed({"GERENTE","CLIENTE"})
    public Response sacar(@PathParam("id") Long id, @Valid TransacaoDTO transacaoDTO) {
        try {
            TransacaoDTO transacao = contaService.sacar(id, transacaoDTO);
            transacao.setSaldoAtual(contaService.getSaldoAtual(id));
            return Response.ok(transacao).build();
        } catch (UnprocessableEntityException e) {
            return Response.status(422)
                    .entity(String.format("{ \"erro\": \"%s\" }", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/{id}/transferencia")
    @RolesAllowed({"GERENTE","CLIENTE"})
    public Response transferir(@PathParam("id") Long id, @Valid TransacaoDTO transacaoDTO) {
        try {
            TransacaoDTO transacao = contaService.transferir(id, transacaoDTO);
            transacao.setSaldoAtual(contaService.getSaldoAtual(id));
            return Response.ok(transacao).build();
        } catch (UnprocessableEntityException e) {
            return Response.status(422)
                    .entity(String.format("{ \"erro\": \"%s\" }", e.getMessage()))
                    .build();
        }
    }
}
