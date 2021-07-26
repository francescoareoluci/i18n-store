package it.unifi.ing.rest;

import it.unifi.ing.dao.ManufacturerDao;
import it.unifi.ing.dto.DtoFactory;
import it.unifi.ing.dto.ManufacturerDto;
import it.unifi.ing.model.Manufacturer;
import it.unifi.ing.model.ModelFactory;
import it.unifi.ing.security.JWTTokenNeeded;
import it.unifi.ing.security.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class ManufacturerEndpoint {

    private static final Logger logger = LogManager.getLogger(ManufacturerEndpoint.class);

    @Inject
    private ManufacturerDao manufacturerDao;

    public ManufacturerEndpoint() {}

    @GET
    @Path("/manufacturers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getManufacturerList()
    {
        logger.debug("Requested GET /manufacturers endpoint");

        List<ManufacturerDto> manufacturerDtoList = new ArrayList<>();

        // Get locales
        List<Manufacturer> manufacturerList = manufacturerDao.getManufacturerList();
        for (Manufacturer m : manufacturerList) {
            ManufacturerDto manufacturerDto = DtoFactory.buildManufacturerDto(m.getId(), m.getName());
            manufacturerDtoList.add(manufacturerDto);
        }

        return Response.status(HttpResponse.ok).entity(manufacturerDtoList).build();
    }

    @POST
    @Path("/manufacturers")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addManufacturer(ManufacturerDto manufacturerDtoDto)
    {
        logger.debug("Requested POST /manufacturers endpoint");

        Manufacturer manufacturer = ModelFactory.manufacturer();
        manufacturer.setName(manufacturerDtoDto.getName());

        // Persist given manufacturer
        manufacturerDao.addEntity(manufacturer);

        logger.info("Persisted a new manufacturer with id: " + manufacturer.getId());

        return Response.status(HttpResponse.ok).build();
    }

}
