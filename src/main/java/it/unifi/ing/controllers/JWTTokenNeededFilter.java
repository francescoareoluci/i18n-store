package it.unifi.ing.controllers;

import io.jsonwebtoken.Claims;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@Provider
@JWTTokenNeeded
@Priority(Priorities.AUTHENTICATION)
public class JWTTokenNeededFilter implements ContainerRequestFilter {
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the HTTP Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        try {
            // Extract the token from the HTTP Authorization header
            String token = authorizationHeader.substring("Bearer".length()).trim();
            // Extract claims from token
            Claims claims = JWTFactory.extractClaimsFromToken(token);

            Method method = resourceInfo.getResourceMethod();
            if(method != null){
                // Get allowed permission on method
                JWTTokenNeeded JWTContext = method.getAnnotation(JWTTokenNeeded.class);
                UserRole permission = JWTContext.Permissions();

                if(permission != UserRole.NO_RIGHTS) {
                    // Get Role from jwt
                    String role = claims.get("userRole", String.class);
                    UserRole roleUser = UserRole.valueOf(role);

                    // if role allowed != role jwt -> UNAUTHORIZED
                    if (!permission.equals(roleUser)) {
                        throw new Exception("UnallowedRole");
                    }

                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
