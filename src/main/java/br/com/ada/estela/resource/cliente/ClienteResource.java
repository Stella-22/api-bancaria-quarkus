package br.com.ada.estela.resource.cliente;

import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.service.ClienteService;
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
import java.util.List;

@Path("/clientes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    private ClienteService clienteService;

    @POST
    @Transactional
    @RolesAllowed("GERENTE")
    public Response cadastrar(
            @Valid ClienteDTO clienteDTO,
            @Context UriInfo uriInfo) {

        Cliente cliente = clienteService.cadastrar(clienteDTO);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(cliente.id.toString())
                .build();
        return Response.created(location)
                .entity(toResponse(cliente))
                .build();
    }

    @GET
    @RolesAllowed("GERENTE")
    public Response buscar() {
        List<Cliente> clientes = clienteService.buscarTodos();
        List<ClienteDTO> dtos = clientes.stream()
                .map(this::toResponse)
                .toList();
        return Response.ok(dtos).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("GERENTE")
    public Response buscarPorId(@PathParam("id") Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            throw new NotFoundException("Cliente com id " + id + " nao encontrado");
        }
        return Response.ok(toResponse(cliente)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("GERENTE")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, @Valid ClienteDTO clienteDTO) {

        Cliente cliente = clienteService.atualizar(id, clienteDTO);
        return Response.ok(toResponse(cliente)).build();
    }

    private ClienteDTO toResponse(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.id);
        dto.setNome(cliente.getNome());
        dto.setEmail(cliente.getUsuario().getEmail());
        return dto;
    }

}
