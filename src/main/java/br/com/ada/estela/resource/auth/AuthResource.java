package br.com.ada.estela.resource.auth;

import br.com.ada.estela.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private AuthService authService;

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid AuthRequest authRequest) {
        AuthResponse response = authService.autenticar(authRequest.email(), authRequest.senha());
        if (response != null) {
            return Response.ok(response).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
