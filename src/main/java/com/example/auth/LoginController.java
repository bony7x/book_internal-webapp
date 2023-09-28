package com.example.auth;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/login")
@ApplicationScoped
public class LoginController {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
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
}