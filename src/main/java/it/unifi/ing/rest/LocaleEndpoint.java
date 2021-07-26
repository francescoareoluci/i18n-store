package it.unifi.ing.rest;

import it.unifi.ing.dao.LocaleDao;
import it.unifi.ing.dto.DtoFactory;
import it.unifi.ing.dto.LocaleDto;
import it.unifi.ing.model.Locale;
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
public class LocaleEndpoint {

    private static final Logger logger = LogManager.getLogger(LocaleEndpoint.class);

    @Inject
    private LocaleDao localeDao;

    public LocaleEndpoint() {}

    @GET
    @Path("/locales")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocales()
    {
        logger.debug("Requested GET /locales endpoint");

        List<LocaleDto> localeDtoList = new ArrayList<>();

        // Get locales
        List<Locale> localeList = localeDao.getLocaleList();
        for (Locale l : localeList) {
            LocaleDto localeDto = DtoFactory.buildLocaleDto(l.getId(), l.getLanguageCode(), l.getCountryCode());
            localeDtoList.add(localeDto);
        }

        return Response.status(HttpResponse.ok).entity(localeDtoList).build();
    }

    @POST
    @Path("/locales")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addLocale(LocaleDto localeDto)
    {
        logger.debug("Requested POST /locales endpoint");

        Locale locale = ModelFactory.locale();
        locale.setCountryCode(localeDto.getCountryCode());
        locale.setLanguageCode(localeDto.getLanguageCode());

        // Persist given locale
        localeDao.addEntity(locale);

        logger.info("Persisted a new locale with id: " + locale.getId());

        return Response.status(HttpResponse.ok).build();
    }

}
