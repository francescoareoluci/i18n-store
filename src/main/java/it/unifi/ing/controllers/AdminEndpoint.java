package it.unifi.ing.controllers;

import it.unifi.ing.dao.*;
import it.unifi.ing.dto.*;
import it.unifi.ing.model.*;

import it.unifi.ing.security.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Path("/admin")
public class AdminEndpoint {

    private static final Logger logger = LogManager.getLogger(AdminEndpoint.class);

    @Inject
    private AdminDao adminDao;
    @Inject
    private CustomerDao customerDao;
    @Inject
    private ProductDao productDao;
    @Inject
    private ProductCartDao productCartDao;
    @Inject
    private PurchasedProductDao purchasedProductDao;
    @Inject
    private LocaleDao localeDao;
    @Inject
    private ManufacturerDao manufacturerDao;
    @Inject
    private CurrencyDao currencyDao;

    public AdminEndpoint() {}

    @GET
    @Path("/users")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers()
    {
        logger.debug("Requested /users endpoint");

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

        return Response.status(200).entity(userDtoList).build();
    }

    @GET
    @Path("/products")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getProducts()
    {
        logger.debug("Requested /products endpoint");

        List<ProductDto> productDtoList = new ArrayList<>();

        // Get all products
        List<Product> productList = productDao.getProductList();
        for (Product p : productList) {
            // Get all translations for a product
            List<LocalizedProduct> localizedProductList = p.getLocalizedProductList();

            // Build dto arrays
            List<LocalizedProductDto> localizedProductDtoList = new ArrayList<>();
            for (LocalizedProduct lp : localizedProductList) {
                LocalizedProductDto localizedProductDto = DtoFactory.buildLocalizedProductDto(lp.getId(),
                        lp.getName(), lp.getDescription(), lp.getCategory(), lp.getCurrency().getCurrency(),
                        lp.getPrice(), lp.getLocale().getLanguageCode(),
                        lp.getLocale().getCountryCode());

                localizedProductDtoList.add(localizedProductDto);
            }
            ProductDto productDto = DtoFactory.buildProductDto(p.getId(),
                    p.getProdManufacturer().getName(), localizedProductDtoList);

            productDtoList.add(productDto);
        }

        return Response.status(200).entity(productDtoList).build();
    }

    @GET
    @Path("/products/{id}")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getProductById(@PathParam("id") Long productId)
    {
        logger.debug("Requested /products/" + productId + " endpoint");

        // Get specific product
        Product product = productDao.getEntityById(productId);
        if (product == null) {
            logger.warn("Unable to find product with id: " + productId);
            return Response.status(404).build();
        }

        // Get product translations
        List<LocalizedProduct> localizedProductList = product.getLocalizedProductList();

        // Build dto array
        List<LocalizedProductDto> localizedProductDtoList = new ArrayList<>();
        for (LocalizedProduct lp : localizedProductList) {
            LocalizedProductDto localizedProductDto = DtoFactory.buildLocalizedProductDto(lp.getId(),
                    lp.getName(), lp.getDescription(), lp.getCategory(), lp.getCurrency().getCurrency(),
                    lp.getPrice(), lp.getLocale().getLanguageCode(),
                    lp.getLocale().getCountryCode());

            localizedProductDtoList.add(localizedProductDto);
        }
        ProductDto productDto = DtoFactory.buildProductDto(product.getId(),
                    product.getProdManufacturer().getName(), localizedProductDtoList);

        return Response.status(200).entity(productDto).build();
    }

    @PUT
    @Path("/products/add")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addProduct(@Context HttpHeaders headers,
                               ProductDto productDto)
    {
        logger.debug("Requested /products/add endpoint");

        // Get username from token
        String token = JWTUtil.extractBearerHeader(headers);
        if (token.isEmpty()) {
            logger.error("Empty bearer header");
            return Response.status(401).build();
        }
        String username = JWTUtil.getUsernameFromToken(token);
        if (username == null) {
            logger.error("Cannot get username from token");
            return Response.status(401).build();
        }

        // Get admin entity
        Admin admin = adminDao.getAdminByUsername(username);
        if (admin == null) {
            logger.error("Unable to retrieve user " + username);
            return Response.status(404).build();
        }

        // Check for product localizations
        List<LocalizedProductDto> localizedProductDtoList = productDto.getLocalizedInfo();
        if (localizedProductDtoList.isEmpty()) {
            logger.error("Sent product does not contain any localization info");
            return Response.status(404).build();
        }

        // Retrieve available locales
        List<Locale> localeList = localeDao.getLocaleList();

        // Check for valid product locales
        boolean invalidLanguage = false;
        boolean localeFound = false;
        for (LocalizedProductDto lpDto : localizedProductDtoList) {
            for (Locale l : localeList) {
                if (l.getCountryCode().equals(lpDto.getCountry()) &&
                    l.getLanguageCode().equals(lpDto.getLocale())) {
                    localeFound = true;
                    break;
                }
            }

            if (!localeFound) {
                invalidLanguage = true;
                break;
            }
        }

        if (invalidLanguage) {
            logger.error("Sent product contains an invalid locale");
            return Response.status(404).build();
        }

        // Retrieve available currencies
        List<Currency> currencyList = currencyDao.getCurrencyList();

        // Check for valid product currencies
        boolean invalidCurrency = false;
        boolean currencyFound = false;
        for (LocalizedProductDto lpDto : localizedProductDtoList) {
            for (Currency c : currencyList) {
                if (c.getCurrency().equals(lpDto.getCurrency())) {
                    currencyFound = true;
                    break;
                }
            }

            if (!currencyFound) {
                invalidCurrency = true;
                break;
            }
        }

        if (invalidCurrency) {
            logger.error("Sent product contains an invalid currency");
            return Response.status(404).build();
        }

        Manufacturer manufacturer = manufacturerDao.getManufacturerByName(productDto.getManufacturer());
        if (manufacturer == null) {
            // Create new one
            logger.info("The following non-existing manufacturer will be created: " +
                    productDto.getManufacturer());
            manufacturer = ModelFactory.manufacturer();
            manufacturer.setName(productDto.getManufacturer());

            manufacturerDao.addEntity(manufacturer);
            logger.info("Persisted a new manufacturer with id: " + manufacturer.getId());
        }

        // Create product
        Product product = ModelFactory.product();
        product.setProdManufacturer(manufacturer);
        product.setProdAdministrator(admin);

        List<LocalizedProduct> localizedProductList = new ArrayList<>();
        for (LocalizedProductDto lpDto : localizedProductDtoList) {
            LocalizedProduct lp = ModelFactory.localizedProduct();
            lp.setProduct(product);
            for (Locale l : localeList) {
                if (lpDto.getLocale().equals(l.getLanguageCode()) &&
                    lpDto.getCountry().equals(l.getCountryCode())) {
                    lp.setLocale(l);
                    break;
                }
            }
            lp.setName(lpDto.getName());
            lp.setDescription(lpDto.getDescription());
            lp.setCategory(lpDto.getCategory());
            for (Currency c : currencyList) {
                if (lpDto.getCurrency().equals(c.getCurrency())) {
                    lp.setCurrency(c);
                }
            }
            lp.setPrice(lpDto.getPrice());

            localizedProductList.add(lp);
        }

        product.setLocalizedProductList(localizedProductList);

        productDao.addEntity(product);

        logger.info("Persisted a new product with id: " + product.getId());

        return Response.status(200).build();
    }

    @DELETE
    @Path("/products/remove/{id}")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Transactional
    public Response removeProduct(@PathParam("id") Long productId)
    {
        logger.debug("Requested /products/remove/" + productId + " endpoint");

        Product product = productDao.getEntityById(productId);
        if (product == null) {
            logger.error("Invalid product identifier");
            return Response.status(404).build();
        }

        // Remove cart products references
        List<ProductCart> productCartList = productCartDao.getProductCartList();
        for (ProductCart pc : productCartList) {
            if (pc.getProduct().getId() == productId) {
                productCartDao.deleteEntity(pc);
            }
        }

        // Remove purchased products references
        List<PurchasedProduct> purchasedProductList = purchasedProductDao.getPurchasedProductList();
        for (PurchasedProduct pp : purchasedProductList) {
            if (pp.getProduct().getId() == productId) {
                purchasedProductDao.deleteEntity(pp);
            }
        }

        productDao.deleteEntity(product);

        logger.info("Removed product with id: " + product.getId());

        return Response.status(200).build();
    }

    @GET
    @Path("/locales")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocales()
    {
        logger.debug("Requested /locales endpoint");

        List<LocaleDto> localeDtoList = new ArrayList<>();

        // Get locales
        List<Locale> localeList = localeDao.getLocaleList();
        for (Locale l : localeList) {
            LocaleDto localeDto = DtoFactory.buildLocaleDto(l.getId(), l.getLanguageCode(), l.getCountryCode());
            localeDtoList.add(localeDto);
        }

        return Response.status(200).entity(localeDtoList).build();
    }

    @PUT
    @Path("/locales/add")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addLocale(LocaleDto localeDto)
    {
        logger.debug("Requested /locales/add endpoint");

        Locale locale = ModelFactory.locale();
        locale.setCountryCode(localeDto.getCountryCode());
        locale.setLanguageCode(localeDto.getLanguageCode());

        // Persist given locale
        localeDao.addEntity(locale);

        logger.info("Persisted a new locale with id: " + locale.getId());

        return Response.status(200).build();
    }

    @GET
    @Path("/manufacturers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getManufacturerList()
    {
        logger.debug("Requested /manufacturers endpoint");

        List<ManufacturerDto> manufacturerDtoList = new ArrayList<>();

        // Get locales
        List<Manufacturer> manufacturerList = manufacturerDao.getManufacturerList();
        for (Manufacturer m : manufacturerList) {
            ManufacturerDto manufacturerDto = DtoFactory.buildManufacturerDto(m.getId(), m.getName());
            manufacturerDtoList.add(manufacturerDto);
        }

        return Response.status(200).entity(manufacturerDtoList).build();
    }

    @PUT
    @Path("/manufacturers/add")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addManufacturer(ManufacturerDto manufacturerDtoDto)
    {
        logger.debug("Requested /manufacturers/add endpoint");

        Manufacturer manufacturer = ModelFactory.manufacturer();
        manufacturer.setName(manufacturerDtoDto.getName());

        // Persist given manufacturer
        manufacturerDao.addEntity(manufacturer);

        logger.info("Persisted a new manufacturer with id: " + manufacturer.getId());

        return Response.status(200).build();
    }

    @GET
    @Path("/currencies")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrencies()
    {
        logger.debug("Requested /currencies endpoint");

        List<CurrencyDto> currencyDtoList = new ArrayList<>();

        // Get locales
        List<Currency> currencyList = currencyDao.getCurrencyList();
        for (Currency c : currencyList) {
            CurrencyDto currencyDto = DtoFactory.buildCurrencyDto(c.getId(), c.getCurrency());
            currencyDtoList.add(currencyDto);
        }

        return Response.status(200).entity(currencyDtoList).build();
    }

    @PUT
    @Path("/currencies/add")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCurrency(CurrencyDto currencyDto)
    {
        logger.debug("Requested /currencies/add endpoint");

        Currency currency = ModelFactory.currency();
        currency.setCurrency(currencyDto.getCurrency());

        // Persist given currency
        currencyDao.addEntity(currency);

        logger.info("Persisted a new currency with id: " + currency.getId());

        return Response.status(200).build();
    }

}
