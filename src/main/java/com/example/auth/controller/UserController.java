package com.example.auth.controller;

import com.example.auth.controller.dto.UserAndCountDto;
import com.example.auth.controller.dto.UserDto;
import com.example.auth.controller.dto.UserResponseDto;
import com.example.auth.controller.dto.UserUpdateDto;
import com.example.auth.controller.dto.UserUpdateRoleDto;
import com.example.auth.controller.mapper.LoginMapper;
import com.example.auth.domain.entity.User;
import com.example.auth.domain.service.UserPersistenceService;
import com.example.auth.exception.UserConflictException;
import com.example.auth.exception.UserNotFoundException;
import com.example.customer.exception.CustomerConflictException;
import com.example.customer.exception.CustomerNotFoundException;
import com.example.request.ExtendedRequest;
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
import java.util.ArrayList;
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

    @POST
    @Path("/users/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllUsers(ExtendedRequest request) {
        log.debug("getAllUsers");

        UserAndCountDto userAndCountDto = userPersistenceService.getAllUsers(request);
        List<UserDto> dtos = mapper.map(userAndCountDto.getUsers());
        UserResponseDto responseDto = mapper.mapToResponse(dtos, request, userAndCountDto.getTotalCount());
        return Response.status(200).entity(responseDto).build();
    }

    @GET
    @Path("/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") Integer id) {
        log.debug("getUser: {}", id);

        try {
            UserDto dto = mapper.map(userPersistenceService.getUser(id));
            List<UserDto> dtos = new ArrayList<>(List.of(dto));
            return Response.status(200).entity(dtos).build();
        } catch (Exception e) {
            if (e.getClass().equals(UserNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        log.debug("getAllBooks");

        List<User> users = userPersistenceService.getAllUsers();
        List<UserDto> dtos = mapper.map(users);
        return Response.status(200).entity(dtos).build();
    }

/*    @PUT
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
    }*/

    @PUT
    @Path("/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserNameEmail(@PathParam("id") Integer id, UserUpdateDto update) {
        log.debug("updateUser: {} {}", id, update);

        try {
            UserDto user = mapper.map(userPersistenceService.updateUserNameEmail(mapper.map(update), id));
            return Response.status(200).entity(user).build();
        } catch (Exception e) {
            if (e.getClass().equals(UserConflictException.class)) {
                return Response.status(409).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/users/{userId}")
    public Response deleteUser(@PathParam("userId") Integer id) {
        log.debug("deleteUser: {}", id);

        try {
            userPersistenceService.deleteUser(id);
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
