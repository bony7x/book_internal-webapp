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
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
@Path("/api")
@Slf4j
public class AuthController {

    @Inject
    JsonWebToken jwt;

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

    @POST
    @Path("/login")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginClient(@Context HttpHeaders headers, LoginDto dto) {
        log.debug("loginClient: {}", dto);

        var a = headers.getRequestHeaders().getFirst("Authorization");
        String b = a.replace("Basic","");
        byte[] c = Base64.getDecoder().decode(b);
        String d = new String(c);
        try {
            String basicAuthToken = service.login(mapper.map(dto));
            return Response.status(200).entity(basicAuthToken).build();
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
