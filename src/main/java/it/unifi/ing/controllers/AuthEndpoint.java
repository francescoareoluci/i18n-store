package it.unifi.ing.controllers;

import it.unifi.ing.dao.AdminDao;
import it.unifi.ing.dao.CustomerDao;
import it.unifi.ing.model.Admin;
import it.unifi.ing.model.Customer;
import it.unifi.ing.model.ModelFactory;
import it.unifi.ing.security.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Path("/auth")
public class AuthEndpoint {

    private static final Logger logger = LogManager.getLogger(AuthEndpoint.class);

    @Inject
    private AdminDao adminDao;
    @Inject
    private CustomerDao customerDao;

    public AuthEndpoint() {}

    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response login(@Context HttpHeaders headers)
    {
        logger.debug("Login requested");

        // Fetch authorization header
        final List<String> authorization = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || authorization.isEmpty()) {
            logger.error("Invalid authorization header for login request");
            return Response.status(401).build();
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

        logger.debug("The following user has requested the authorization: " + username +
                " with password " + password);

        // Search for user
        Customer customer = ModelFactory.customer();
        customer.setMail(username);
        customer.setPassword(password);
        Customer loggedCustomer = customerDao.login(customer);
        if (loggedCustomer != null) {
            String jwtId = UUID.randomUUID().toString();
            logger.info("User " + username + " is a valid customer. Sending JWT with id: " + jwtId);

            String token = JWTUtil.createJWT(jwtId, "i18n-store", username,
                    String.valueOf(UserRole.CUSTOMER), loggedCustomer.getUserLocale().getLanguageCode());
            return Response.ok().entity(token).build();
        }

        // Try for admin user
        Admin admin = ModelFactory.admin();
        admin.setMail(username);
        admin.setPassword(password);
        Admin loggedAdmin = adminDao.login(admin);
        if (loggedAdmin != null) {
            String jwtId = UUID.randomUUID().toString();
            logger.info("User " + username + " is a valid administrator. Sending JWT with id: " + jwtId);

            String token = JWTUtil.createJWT(jwtId, "i18n-store", username,
                    String.valueOf(UserRole.ADMIN), "en");
            return Response.ok().entity(token).build();
        }

        logger.info("Invalid user " + username + " has requested login");

        return Response.status(401).build();
    }
}
