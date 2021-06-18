package it.unifi.ing.controllers;

import it.unifi.ing.dao.AdminDao;
import it.unifi.ing.dao.CustomerDao;
import it.unifi.ing.model.Admin;
import it.unifi.ing.model.Customer;
import it.unifi.ing.model.ModelFactory;

import it.unifi.ing.security.*;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

@Path("/auth")
public class AuthEndpoint {

    @Inject
    private AdminDao adminDao;
    @Inject
    private CustomerDao customerDao;

    public AuthEndpoint() {}

    @GET
    @Path("/test")
    public Response test()
    {
        return Response.ok().entity("Service online").build();
    }

    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpHeaders headers)
    {
        // Fetch authorization header
        final List<String> authorization = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if(authorization == null || authorization.isEmpty()) {
            return Response.ok().entity("Service online").build();
        }

        // Get encoded username and password (Basic auth)
        final String encodedUserPassword = authorization.get(0).replaceFirst("Basic" + " ", "");

        // Decode username and password
        String usernameAndPassword;
        usernameAndPassword = new String(Base64.getDecoder().decode(encodedUserPassword));

        // Split username and password with token
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();

        // Verifying Username and password
        System.out.println(username);
        System.out.println(password);

        // Search for user
        Customer customer = ModelFactory.customer();
        customer.setMail(username);
        customer.setPassword(password);
        Customer loggedCustomer = customerDao.login(customer);
        if (loggedCustomer != null) {
            String token = JWTUtil.createJWT(UUID.randomUUID().toString(), "i18n-store", username,
                    String.valueOf(UserRole.CUSTOMER), 100000);
            return Response.ok().entity(token).build();
        }

        // Try for admin user
        Admin admin = ModelFactory.admin();
        admin.setMail(username);
        admin.setPassword(password);
        Admin loggedAdmin = adminDao.login(admin);
        if (loggedAdmin != null) {
            String token = JWTUtil.createJWT(UUID.randomUUID().toString(), "i18n-store", username,
                    String.valueOf(UserRole.ADMIN), 100000);
            return Response.ok().entity(token).build();
        }

        return Response.ok().entity("Service Online").build();
    }
}
