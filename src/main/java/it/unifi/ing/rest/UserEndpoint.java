package it.unifi.ing.rest;

import it.unifi.ing.dao.AdminDao;
import it.unifi.ing.dao.CustomerDao;
import it.unifi.ing.dto.DtoFactory;
import it.unifi.ing.dto.UserDto;
import it.unifi.ing.model.Admin;
import it.unifi.ing.model.Customer;
import it.unifi.ing.security.JWTTokenNeeded;
import it.unifi.ing.security.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class UserEndpoint {

    private static final Logger logger = LogManager.getLogger(UserEndpoint.class);

    @Inject
    private AdminDao adminDao;
    @Inject
    private CustomerDao customerDao;

    public UserEndpoint() {}

    @GET
    @Path("/users")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers()
    {
        logger.debug("Requested GET /users endpoint");

        List<UserDto> userDtoList = new ArrayList<>();

        // Get all admins
        List<Admin> users = adminDao.getAdminList();
        for (Admin u : users) {
            UserDto userDto = DtoFactory.buildUserDto(u.getId(), u.getFirstName(),
                    u.getLastName(), u.getMail(), UserDto.UserRole.ADMIN);
            userDtoList.add(userDto);
        }

        // Get all customers
        List<Customer> customers = customerDao.getCustomerList();
        for (Customer c : customers) {
            UserDto userDto = DtoFactory.buildUserDto(c.getId(), c.getFirstName(),
                    c.getLastName(), c.getMail(), UserDto.UserRole.CUSTOMER);
            userDtoList.add(userDto);
        }

        return Response.status(HttpResponse.ok).entity(userDtoList).build();
    }

}
