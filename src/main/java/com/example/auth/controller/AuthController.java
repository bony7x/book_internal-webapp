package com.example.auth.controller;

import com.example.auth.controller.dto.LoginDto;
import com.example.auth.controller.mapper.LoginMapper;
import com.example.auth.domain.entity.Login;
import com.example.auth.exception.UserConflictException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.service.AuthenticationService;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
class TokenResponseDto {

    String token;
}

@ApplicationScoped
@Path("/api")
@Slf4j
public class AuthController {

    @Inject
    AuthenticationService service;

    @Inject
    LoginMapper mapper;

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerClient(LoginDto dto) {
        log.debug("registerClient: {}", dto);

        try {
            Login login = service.register(mapper.map(dto));
            LoginDto logged = mapper.map(login);
            return Response.status(201).entity(logged).build();
        } catch (Exception e) {
            if (e.getClass().equals(UserConflictException.class)) {
                return Response.status(409).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }


    /*@POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("login")
    public Response loginClient(@HeaderParam("Authorization") String authorizationHeader) {
        try {
            // Check if the Authorization header is present
            if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Invalid authentication credentials")
                        .build();
            }

            // Extract and decode the base64-encoded credentials
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
            String[] parts = credentials.split(":", 2);
            String username = parts[0];
            String password = parts[1];

            // TODO: Authenticate the user using the extracted username and password

            // Assuming authentication succeeds, return a success response
            return Response.status(Response.Status.OK)
                    .entity("Login successful")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An error occurred during login")
                    .build();
        }
    }
*/
    @POST
    @Path("/login")
    @PermitAll()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginClient(@Context HttpHeaders headers) {
        log.debug("loginClient");

        String authString = headers.getRequestHeaders().getFirst("Authorization");
        try {
            String basicAuthToken = service.login(authString);
            return Response.status(200).entity(new TokenResponseDto(basicAuthToken)).build();
        } catch (Exception e) {
            if (e.getClass().equals(UserConflictException.class)) {
                return Response.status(409).entity(e.getMessage()).build();
            }
            if (e.getClass().equals(UserNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logoutClient() {
        log.debug("logoutClient");

        return Response.status(200).build();
    }
}
