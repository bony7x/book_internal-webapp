package com.example.auth.controller;

import com.example.auth.controller.dto.UserDto;
import com.example.auth.controller.dto.UserUpdateDto;
import com.example.auth.controller.dto.UserUpdateRoleDto;
import com.example.auth.controller.mapper.LoginMapper;
import com.example.auth.domain.service.UserPersistenceService;
import com.example.auth.exception.UserNotFoundException;
import com.example.customer.exception.CustomerConflictException;
import com.example.customer.exception.CustomerNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Path("/api")
@Slf4j
public class UserController {

    @Inject
    UserPersistenceService userPersistenceService;

    @Inject
    LoginMapper mapper;


    @POST
    @Path("/users/current")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(String token) {
        log.debug("getAllUsers");

        UserDto dto = mapper.map(userPersistenceService.getCurrentUser(token));
        return Response.status(200).entity(dto).build();
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        log.debug("getAllUsers");

        List<UserDto> dtos = mapper.map(userPersistenceService.getAllUsers());
        return Response.status(200).entity(dtos).build();
    }

    @GET
    @Path("/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") Integer id) {
        log.debug("getUser: {}", id);

        try {
            UserDto dto = mapper.map(userPersistenceService.getUser(id));
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(UserNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/users/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(UserUpdateDto update) {
        log.debug("updateUser: {}", update);

        try {
            UserDto dto = mapper.map(userPersistenceService.updateUser(mapper.map(update)));
            dto.setPassword(update.getPassword());
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(UserNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/users")
    public Response deleteUser(UserDto user) {
        log.debug("deleteUser: {}", user);

        try {
            userPersistenceService.deleteUser(mapper.map(user));
            return Response.status(200).build();
        } catch (Exception e) {
            if (e.getClass().equals(CustomerNotFoundException.class) || e.getClass()
                    .equals(UserNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            if (e.getClass().equals(CustomerConflictException.class)) {
                return Response.status(409).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserRole(UserUpdateRoleDto update) {
        log.debug("updateUserRole: {}", update);

        try {
            update.setRole(update.getRole().toUpperCase());
            userPersistenceService.updateUserRole(mapper.map(update));
            return Response.status(200).build();
        } catch (Exception e) {
            if (e.getClass().equals(UserNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }
}
