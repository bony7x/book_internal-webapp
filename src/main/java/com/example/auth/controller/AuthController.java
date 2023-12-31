package com.example.auth.controller;

import com.example.auth.controller.dto.RegisterUserAsCustomerDto;
import com.example.auth.controller.dto.TokenDto;
import com.example.auth.controller.dto.UserDto;
import com.example.auth.controller.mapper.LoginMapper;
import com.example.auth.domain.entity.User;
import com.example.auth.domain.service.AuthenticationPersistenceService;
import com.example.auth.domain.service.UserPersistenceService;
import com.example.auth.exception.UserConflictException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.service.AuthenticationService;
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
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Path("/api")
@Slf4j
public class AuthController {

    @Inject
    AuthenticationService service;

    @Inject
    AuthenticationPersistenceService authenticationPersistenceService;

    @Inject
    LoginMapper mapper;

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(UserDto dto) {
        log.debug("registerClient: {}", dto);

        try {
            User login = service.register(mapper.map(dto));
            UserDto logged = mapper.map(login);
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(@Context HttpHeaders headers) {
        log.debug("loginClient");

        String authString = headers.getRequestHeaders().getFirst("Authorization");
        try {
            String basicAuthToken = service.login(authString);
            return Response.status(200).entity(new TokenDto(basicAuthToken)).build();
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

    @POST
    @Path("/registerCustomer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUserAsCustomer(RegisterUserAsCustomerDto request) {
        log.debug("registerUserAsCustomer: {}", request);

        try {
            TokenDto tokenDto = new TokenDto(
                    authenticationPersistenceService.registerUserAsCustomer(request.getUser(), request.getFirstName(),
                            request.getLastName(),
                            request.getAddress()));
            return Response.status(201).entity(tokenDto).build();
        } catch (Exception e) {
            if (e.getClass().equals(UserConflictException.class)) {
                return Response.status(409).entity(e.getMessage()).build();
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
