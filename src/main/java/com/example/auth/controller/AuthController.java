package com.example.auth.controller;

import com.example.auth.controller.dto.LoginDto;
import com.example.auth.controller.mapper.LoginMapper;
import com.example.auth.domain.entity.Login;
import com.example.auth.domain.service.AuthenticationPersistenceService;
import com.example.auth.exception.UserConflictException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.service.AuthenticationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

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
            if(e.getClass().equals(UserConflictException.class)){
                return Response.status(409).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginClient(LoginDto dto) {
        log.debug("loginClient: {}", dto);

        try {
            Login login = service.login(mapper.map(dto));
            LoginDto logged = mapper.map(login);
            return Response.status(200).entity(logged).build();
        } catch (Exception e) {
            if(e.getClass().equals(UserConflictException.class)){
                return Response.status(409).entity(e.getMessage()).build();
            }
            if(e.getClass().equals(UserNotFoundException.class)){
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logoutClient(LoginDto dto) {
        log.debug("logoutClient: {}", dto);

        try {
            Login login = service.logout(mapper.map(dto));
            LoginDto logged = mapper.map(login);
            return Response.status(200).entity(logged).build();
        } catch (Exception e) {
            if(e.getClass().equals(UserConflictException.class)){
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }
}
