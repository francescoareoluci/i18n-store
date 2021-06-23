package it.unifi.ing.controllers;

import it.unifi.ing.dao.CustomerDao;
import it.unifi.ing.dao.LocaleDao;
import it.unifi.ing.dto.DtoFactory;
import it.unifi.ing.dto.LocalizedProductDto;
import it.unifi.ing.dto.ProductDto;
import it.unifi.ing.model.Customer;
import it.unifi.ing.model.Locale;
import it.unifi.ing.model.LocalizedProduct;
import it.unifi.ing.model.Product;
import it.unifi.ing.security.JWTUtil;
import it.unifi.ing.security.UserRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/search")
public class SearchEndpoint {

    private static final Logger logger = LogManager.getLogger(SearchEndpoint.class);
    private static final String querySeparator = "\\+";

    @Inject
    private SearchEngine searchEngine;

    @Inject
    private LocaleDao localeDao;
    @Inject
    private CustomerDao customerDao;

    @GET
    @Path("/products/{query}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response queryProducts(@Context HttpHeaders headers,
                                  @PathParam("query") String userQuery,
                                  @QueryParam("type") String queryType)
    {
        logger.debug("Requested /products/" + userQuery + " endpoint");

        if (userQuery == null || userQuery.isEmpty()) {
            logger.debug("User has requested an empty query");
            return Response.status(404).build();
        }

        SearchEngine.SearchType searchType;
        if (queryType == null) {
            searchType = SearchEngine.SearchType.FUZZY;
        }
        else {
            try {
                searchType = SearchEngine.SearchType.valueOf(queryType);
            }
            catch (IllegalArgumentException e) {
                logger.error("Illegal query type specified: " + queryType);
                searchType = SearchEngine.SearchType.FUZZY;
            }
        }

        // Get username and role from token
        List<String> tokenResult = getUsernameAndRoleFromToken(headers);
        // Set a default locale
        Locale userLocale = localeDao.getLocaleList().get(0);

        boolean isAdmin = false;
        if (tokenResult != null && tokenResult.size() == 2) {
            // User logged. Get locale
            String username = tokenResult.get(0);
            String role = tokenResult.get(1);
            switch (UserRole.valueOf(role)) {
                case ADMIN:
                    // Admin can receive multiple localized products
                    logger.debug("User " + username + " [administrator] has requested a search");
                    isAdmin = true;
                    break;
                case CUSTOMER:
                    // Customer receive products in his/her language
                    logger.debug("User " + username + " [customer] has requested a search");
                    Customer c = customerDao.getCustomerByUsername(username);
                    userLocale = c.getUserLocale();
                    break;
                case NO_RIGHTS:
                    // No role: use default locale
                    logger.debug("User " + username + " [no role] has requested a search");
                    break;
                default:
                    break;
            }
        }

        String[] queryArray = userQuery.split(querySeparator);
        String query = "";
        if (queryArray.length == 1) {
            query = queryArray[0];
        }
        else {
            for (int i = 0; i < queryArray.length; i++) {
                query += queryArray[i] + " ";
            }
        }

        logger.info("User has requested a search with query: " + query + " with type: " +
                String.valueOf(searchType));

        // Retrieve matching entities (fuzzy match by default)
        List<Product> productList = searchEngine.searchProducts(query, searchType);

        List<ProductDto> productDtoList = new ArrayList<>();

        for (Product p : productList) {
            List<LocalizedProductDto> localizedProductDtoList = new ArrayList<>();
            for (LocalizedProduct lp : p.getLocalizedProductList()) {

                if (isAdmin || lp.getLocale().getId().equals(userLocale.getId())) {
                    LocalizedProductDto localizedProductDto = DtoFactory.buildLocalizedProductDto(lp.getId(),
                            lp.getName(), lp.getDescription(), lp.getCategory(), lp.getCurrency().getCurrency(),
                            lp.getPrice(), lp.getLocale().getLanguageCode(), lp.getLocale().getCountryCode());

                    localizedProductDtoList.add(localizedProductDto);
                }
            }

            ProductDto productDto = DtoFactory.buildProductDto(p.getId(), p.getProdManufacturer().getName(), localizedProductDtoList);
            productDtoList.add(productDto);
        }

        return Response.status(200).entity(productDtoList).build();
    }

    @GET
    @Path("/products/similar-to/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getSimilarProducts(@Context HttpHeaders headers,
                                       @PathParam("id") int productId)
    {
        logger.debug("Requested /products/similar-to/" + productId + " endpoint");

        // Get username and role from token
        List<String> tokenResult = getUsernameAndRoleFromToken(headers);
        // Set a default locale
        Locale userLocale = localeDao.getLocaleList().get(0);

        boolean isAdmin = false;
        if (tokenResult != null && tokenResult.size() == 2) {
            // User logged. Get locale
            String username = tokenResult.get(0);
            String role = tokenResult.get(1);
            switch (UserRole.valueOf(role)) {
                case ADMIN:
                    // Admin can receive multiple localized products
                    logger.debug("User " + username + " [administrator] has requested a search");
                    isAdmin = true;
                    break;
                case CUSTOMER:
                    // Customer receive products in his/her language
                    logger.debug("User " + username + " [customer] has requested a search");
                    Customer c = customerDao.getCustomerByUsername(username);
                    userLocale = c.getUserLocale();
                    break;
                case NO_RIGHTS:
                    // No role: use default locale
                    logger.debug("User " + username + " [no role] has requested a search");
                    break;
                default:
                    break;
            }
        }

        logger.info("User has requested a search of products similar to: " + productId);

        // Retrieve matching entities (fuzzy match by default)
        List<Product> productList = searchEngine.searchSimilarProducts(productId);

        List<ProductDto> productDtoList = new ArrayList<>();

        for (Product p : productList) {
            List<LocalizedProductDto> localizedProductDtoList = new ArrayList<>();
            for (LocalizedProduct lp : p.getLocalizedProductList()) {

                if (isAdmin || lp.getLocale().getId().equals(userLocale.getId())) {
                    LocalizedProductDto localizedProductDto = DtoFactory.buildLocalizedProductDto(lp.getId(),
                            lp.getName(), lp.getDescription(), lp.getCategory(), lp.getCurrency().getCurrency(),
                            lp.getPrice(), lp.getLocale().getLanguageCode(), lp.getLocale().getCountryCode());

                    localizedProductDtoList.add(localizedProductDto);
                }
            }

            ProductDto productDto = DtoFactory.buildProductDto(p.getId(), p.getProdManufacturer().getName(), localizedProductDtoList);
            productDtoList.add(productDto);
        }

        return Response.status(200).entity(productDtoList).build();
    }


    private List<String> getUsernameAndRoleFromToken(HttpHeaders headers)
    {
        String token = extractBearerHeader(headers);
        if (token.isEmpty()) {
            logger.debug("Non-logged user has requested search");
            return null;
        }
        String username = JWTUtil.getUsernameFromToken(token);
        if (username == null) {
            logger.error("Cannot get username from token");
            return null;
        }

        String userRole = JWTUtil.getUserRoleFromToken(token);
        if (userRole == null) {
            logger.error("Cannot get user role from token");
            return null;
        }

        List<String> returnValues = new ArrayList<>();
        returnValues.add(username);
        returnValues.add(userRole);

        return returnValues;
    }

    private String extractBearerHeader(HttpHeaders headers)
    {
        // Get the HTTP Authorization header from the request
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null) {
            return "";
        }

        // Extract the token from the HTTP Authorization header
        return authorizationHeader.substring("Bearer".length()).trim();
    }

}
