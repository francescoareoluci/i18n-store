package it.unifi.ing.rest;

import it.unifi.ing.dao.*;
import it.unifi.ing.dto.*;
import it.unifi.ing.model.Customer;
import it.unifi.ing.model.Locale;
import it.unifi.ing.model.Product;
import it.unifi.ing.security.JWTTokenNeeded;
import it.unifi.ing.security.JWTUtil;
import it.unifi.ing.security.UserRole;
import it.unifi.ing.translation.LocalizedField;
import it.unifi.ing.translation.LocalizedFieldHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/products")
public class LocalizedProductEndpoint {

    private static final Logger logger = LogManager.getLogger(CustomerEndpoint.class);

    @Inject
    private LocalizedFieldHandler localizedFieldHandler;
    @Inject
    private CustomerDao customerDao;
    @Inject
    private ProductDao productDao;
    @Inject
    private LocalizedFieldDao localizedFieldDao;

    public LocalizedProductEndpoint() {}

    @GET
    @JWTTokenNeeded(Permissions = UserRole.CUSTOMER)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getProducts(@Context HttpHeaders headers)
    {
        logger.debug("Requested /products endpoint");

        // Get username from token
        String username = getUsernameFromToken(headers);
        if (username.isEmpty()) { return Response.status(HttpResponse.unauthorized).build(); }

        List<ProductDto> productDtoList = new ArrayList<>();

        // Get user
        Customer customer = customerDao.getCustomerByUsername(username);
        if (customer == null) {
            logger.error("Unable to retrieve user by username: " + username);
            return Response.status(HttpResponse.notFound).build();
        }
        // Get user locale
        Locale locale = customer.getUserLocale();
        if (locale == null) {
            logger.error("Unable to retrieve locale for user " + customer.getMail());
            return Response.status(HttpResponse.notFound).build();
        }

        logger.info("Customer " + customer.getMail() + " has requested the product list. Language: " +
                locale.getLanguageCode());

        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!localizedFieldHandler.setProductLocalizedFields(localizedFieldList)) {
            logger.error("Requested translation for a non configured field");
            return Response.status(HttpResponse.notFound).build();
        }

        // Get all products
        List<Product> productList = productDao.getProductList();
        for (Product p : productList) {
            // Build localized textual item dto list
            List<LocalizedTextualItemDto> localizedTextualItemDtos = DtoMapper
                    .convertProductLocalizedItemListToDto(localizedFieldHandler.getProductNameField(),
                            localizedFieldHandler.getProductDescriptionField(),
                            localizedFieldHandler.getProductCategoryField(),
                            p.getLocalizedItemList(), locale, false);
            if (localizedTextualItemDtos == null) {
                logger.error("Unable to build localized textual item dto list");
                return Response.status(HttpResponse.internalServerError).build();
            }

            // Build localized currency dto list
            List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = DtoMapper
                    .convertLocalizedCurrencyListToDto(locale, localizedFieldHandler.getProductPriceField(),
                            p.getLocalizedItemList(), false);
            if (localizedCurrencyItemDtoList == null) {
                logger.error("Unable to build localized currency item dto list");
                return Response.status(HttpResponse.internalServerError).build();
            }

            // Create product dto
            ProductDto productDto = DtoFactory.buildShortProductDto(p.getId(), p.getProdManufacturer().getName(),
                    localizedTextualItemDtos, localizedCurrencyItemDtoList);
            productDtoList.add(productDto);
        }

        return Response.status(HttpResponse.ok).entity(productDtoList).build();
    }

    @GET
    @Path("/{productId}")
    @JWTTokenNeeded(Permissions = UserRole.CUSTOMER)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getProductById(@Context HttpHeaders headers,
                                   @PathParam("productId") Long productId)
    {
        logger.debug("Requested /products/" + productId + " endpoint");

        // Get username from token
        String username = getUsernameFromToken(headers);
        if (username.isEmpty()) { return Response.status(HttpResponse.unauthorized).build(); }

        // Get user
        Customer customer = customerDao.getCustomerByUsername(username);
        if (customer == null) {
            logger.error("Unable to retrieve user by username: " + username);
            return Response.status(HttpResponse.notFound).build();
        }
        // Get user locale
        Locale locale = customer.getUserLocale();
        if (locale == null) {
            logger.error("Unable to retrieve locale for user " + customer.getMail());
            return Response.status(HttpResponse.notFound).build();
        }

        // Get all localized products by product id
        Product product = productDao.getEntityById(productId);
        if (product == null) {
            return Response.status(HttpResponse.notFound).build();
        }

        logger.info("Customer " + customer.getMail() + " has requested the product id: " +
                productId + ". Language: " + locale.getLanguageCode());

        // Get localized fields of interest for products (name, description, category)
        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!localizedFieldHandler.setProductLocalizedFields(localizedFieldList)) {
            logger.error("Requested translation for a non configured field");
            return Response.status(HttpResponse.notFound).build();
        }

        // Build localized textual item dto list
        List<LocalizedTextualItemDto> localizedTextualItemDtos = DtoMapper
                .convertProductLocalizedItemListToDto(localizedFieldHandler.getProductNameField(),
                        localizedFieldHandler.getProductDescriptionField(),
                        localizedFieldHandler.getProductCategoryField(),
                        product.getLocalizedItemList(), locale, false);

        // Build localized currency dto list
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = DtoMapper
                .convertLocalizedCurrencyListToDto(locale, localizedFieldHandler.getProductPriceField(),
                        product.getLocalizedItemList(), false);

        // Create product dto
        ProductDto productDto = DtoFactory.buildProductDto(product.getId(),
                product.getProdManufacturer().getName(), localizedTextualItemDtos,
                localizedCurrencyItemDtoList);

        return Response.status(HttpResponse.ok).entity(productDto).build();
    }

    private String getUsernameFromToken(HttpHeaders headers)
    {
        String token = JWTUtil.extractBearerHeader(headers);
        if (token.isEmpty()) {
            logger.error("Empty bearer header");
            return "";
        }
        String username = JWTUtil.getUsernameFromToken(token);
        if (username == null) {
            logger.error("Cannot get username from token");
            return "";
        }
        return username;
    }

}
