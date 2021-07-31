package it.unifi.ing.rest;

import it.unifi.ing.dao.CurrencyDao;
import it.unifi.ing.dto.CurrencyDto;
import it.unifi.ing.dto.DtoFactory;
import it.unifi.ing.model.Currency;
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

@Path("/currencies")
public class CurrenciesEndpoint {

    private static final Logger logger = LogManager.getLogger(CurrenciesEndpoint.class);

    @Inject
    private CurrencyDao currencyDao;

    public CurrenciesEndpoint() {}

    @GET
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrencies()
    {
        logger.debug("Requested GET /currencies endpoint");

        List<CurrencyDto> currencyDtoList = new ArrayList<>();

        // Get locales
        List<Currency> currencyList = currencyDao.getCurrencyList();
        for (Currency c : currencyList) {
            CurrencyDto currencyDto = DtoFactory.buildCurrencyDto(c.getId(), c.getCurrency());
            currencyDtoList.add(currencyDto);
        }

        return Response.status(HttpResponse.ok).entity(currencyDtoList).build();
    }

    @POST
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCurrency(CurrencyDto currencyDto)
    {
        logger.debug("Requested POST /currencies endpoint");

        Currency currency = ModelFactory.currency();
        currency.setCurrency(currencyDto.getCurrency());

        // Persist given currency
        currencyDao.addEntity(currency);

        logger.info("Persisted a new currency with id: " + currency.getId());

        return Response.status(HttpResponse.ok).build();
    }

}
