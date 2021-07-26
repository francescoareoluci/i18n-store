package it.unifi.ing.rest;

import it.unifi.ing.dao.*;
import it.unifi.ing.dto.*;
import it.unifi.ing.model.*;

import it.unifi.ing.security.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.List;

import it.unifi.ing.translation.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Path("/admin")
public class AdminEndpoint {

    private static final Logger logger = LogManager.getLogger(AdminEndpoint.class);

    @Inject
    private LocalizedFieldHandler localizedFieldHandler;
    @Inject
    private AdminDao adminDao;
    @Inject
    private CustomerDao customerDao;
    @Inject
    private ProductDao productDao;
    @Inject
    private CartProductDao cartProductDao;
    @Inject
    private PurchasedProductDao purchasedProductDao;
    @Inject
    private LocaleDao localeDao;
    @Inject
    private ManufacturerDao manufacturerDao;
    @Inject
    private CurrencyDao currencyDao;
    @Inject
    private LocalizedFieldDao localizedFieldDao;

    public AdminEndpoint() {}

    @GET
    @Path("/users")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@Context UriInfo uriInfo)
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

        return Response.status(HttpResponse.ok).entity(userDtoList).build();
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

        // Get translation fields for product
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
                            p.getLocalizedItemList(), null, true);
            if (localizedTextualItemDtos == null) {
                logger.error("Unable to build localized textual item dto list");
                return Response.status(HttpResponse.internalServerError).build();
            }

            // Build localized currency dto
            List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = DtoMapper
                    .convertLocalizedCurrencyListToDto(null, localizedFieldHandler.getProductPriceField(),
                            p.getLocalizedItemList(), true);
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
            return Response.status(HttpResponse.notFound).build();
        }

        // Get translation fields for product
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
                        product.getLocalizedItemList(), null, true);

        // Build localized currency dto
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = DtoMapper
                .convertLocalizedCurrencyListToDto(null, localizedFieldHandler.getProductPriceField(),
                        product.getLocalizedItemList(), true);

        // Create product dto
        ProductDto productDto = DtoFactory.buildProductDto(product.getId(),
                product.getProdManufacturer().getName(), localizedTextualItemDtos,
                localizedCurrencyItemDtoList);

        return Response.status(HttpResponse.ok).entity(productDto).build();
    }

    @POST
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
            return Response.status(HttpResponse.unauthorized).build();
        }
        String username = JWTUtil.getUsernameFromToken(token);
        if (username == null) {
            logger.error("Cannot get username from token");
            return Response.status(HttpResponse.unauthorized).build();
        }

        // Get admin entity
        Admin admin = adminDao.getAdminByUsername(username);
        if (admin == null) {
            logger.error("Unable to retrieve user " + username);
            return Response.status(HttpResponse.notFound).build();
        }

        // Check for product localizations
        List<LocalizedTextualItemDto> localizedTextualItemDtoList = productDto.getLocalizedTextualItemList();
        if (localizedTextualItemDtoList.isEmpty()) {
            logger.error("Sent product does not contain any localization info");
            return Response.status(HttpResponse.badRequest).build();
        }

        // Retrieve available locales
        List<Locale> localeList = localeDao.getLocaleList();
        // Retrieve available currencies
        List<Currency> currencyList = currencyDao.getCurrencyList();
        // Retrieve dto manufacturer
        Manufacturer manufacturer = manufacturerDao.getManufacturerByName(productDto.getManufacturer());
        // Check dto fields
        if (checkInvalidProductDtoFields(productDto, localeList, currencyList)) {
            logger.error("Sent product contains invalid fields");
            return Response.status(HttpResponse.badRequest).build();
        }
        // Check for manufacturers in dto
        if (manufacturer == null) {
            if (productDto.getManufacturer().isEmpty()) {
                logger.error("Manufacturer can not be empty");
                return Response.status(HttpResponse.badRequest).build();
            }
            // Create new one
            logger.info("The following non-existing manufacturer will be created: " +
                    productDto.getManufacturer());
            manufacturer = ModelFactory.manufacturer();
            manufacturer.setName(productDto.getManufacturer());

            manufacturerDao.addEntity(manufacturer);
            logger.info("Persisted a new manufacturer with id: " + manufacturer.getId());
        }

        // Get translation fields for product
        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!localizedFieldHandler.setProductLocalizedFields(localizedFieldList)) {
            logger.error("Requested translation for a non configured field");
            return Response.status(HttpResponse.notFound).build();
        }

        // Create product
        Product product = DtoMapper.buildProduct(admin, manufacturer, localeList, currencyList, productDto,
                localizedFieldHandler.getProductLocalizedFieldList());
        if (product == null) {
            logger.error("Unable to build requested product");
            return Response.status(HttpResponse.badRequest).build();
        }

        productDao.addEntity(product);

        logger.info("Persisted a new product with id: " + product.getId());

        return Response.status(HttpResponse.ok).build();
    }

    /**
     * @implNote Edit a product and/or its localization info. Currently
     *           does not support the adding/removing of a localization, only
     *           editing of the existing ones.
     * @param headers: HTTP header of the request
     * @param productDto: Sent JSON object
     * @return response
     */
    @PUT
    @Path("/products/edit")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response editProduct(@Context HttpHeaders headers,
                                ProductDto productDto)
    {
        logger.debug("Requested /products/edit endpoint");

        // Get username from token
        String token = JWTUtil.extractBearerHeader(headers);
        if (token.isEmpty()) {
            logger.error("Empty bearer header");
            return Response.status(HttpResponse.unauthorized).build();
        }
        String username = JWTUtil.getUsernameFromToken(token);
        if (username == null) {
            logger.error("Cannot get username from token");
            return Response.status(HttpResponse.unauthorized).build();
        }

        // Check for valid product
        Product product = productDao.getEntityById(productDto.getId());
        if (product == null) {
            logger.error("Unable to retrieve product with id: " + productDto.getId());
            return Response.status(HttpResponse.notFound).build();
        }

        // Get admin entity
        Admin admin = adminDao.getAdminByUsername(username);
        if (admin == null) {
            logger.error("Unable to retrieve user " + username);
            return Response.status(HttpResponse.notFound).build();
        }

        List<LocalizedItem> localizedItemList = product.getLocalizedItemList();
        List<LocalizedTextualItem> localizedTextualItemList = new ArrayList<>();
        List<LocalizedCurrencyItem> localizedCurrencyItemList = new ArrayList<>();
        for (LocalizedItem ali : localizedItemList) {
            if (ali instanceof LocalizedTextualItem) {
                localizedTextualItemList.add((LocalizedTextualItem) ali);
            }
            else if (ali instanceof LocalizedCurrencyItem) {
                localizedCurrencyItemList.add((LocalizedCurrencyItem) ali);
            }
        }
        // Check for valid textual product localizations
        if (localizedTextualItemList.isEmpty()) {
            logger.error("Empty textual localization list for product with id: " + productDto.getId());
            return Response.status(HttpResponse.badRequest).build();
        }

        // Check for valid product currencies
        if (localizedCurrencyItemList.isEmpty()) {
            logger.error("Empty currency localization list for product with id: " + productDto.getId());
            return Response.status(HttpResponse.badRequest).build();
        }

        // Retrieve available locales
        List<Locale> localeList = localeDao.getLocaleList();
        // Retrieve available currencies
        List<Currency> currencyList = currencyDao.getCurrencyList();
        // Retrieve dto manufacturer
        Manufacturer manufacturer = manufacturerDao.getManufacturerByName(productDto.getManufacturer());
        // Check dto fields
        if (checkInvalidProductDtoFields(productDto, localeList, currencyList)) {
            logger.error("Sent product contains invalid fields");
            return Response.status(HttpResponse.badRequest).build();
        }
        // Check for manufacturers in dto
        if (manufacturer == null) {
            if (productDto.getManufacturer().isEmpty()) {
                logger.error("Manufacturer can not be empty");
                return Response.status(HttpResponse.badRequest).build();
            }
            // Create new one
            logger.info("The following non-existing manufacturer will be created: " +
                    productDto.getManufacturer());
            manufacturer = ModelFactory.manufacturer();
            manufacturer.setName(productDto.getManufacturer());

            manufacturerDao.addEntity(manufacturer);
            logger.info("Persisted a new manufacturer with id: " + manufacturer.getId());
        }

        List<LocalizedTextualItemDto> localizedTextualItemDtoList = productDto.getLocalizedTextualItemList();
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = productDto.getLocalizedCurrencyItemList();

        // Check that the sent textual localizations have the same id of the persisted one
        if (UtilsDto.checkForInvalidDtoTextualLocalization(localizedTextualItemList, localizedTextualItemDtoList)) {
            logger.error("Sent product contains an invalid textual localization identifier");
            return Response.status(HttpResponse.badRequest).build();
        }

        // Check that the sent currency localizations have the same id of the persisted one
        if (UtilsDto.checkForInvalidDtoCurrencyLocalization(localizedCurrencyItemList, localizedCurrencyItemDtoList)) {
            logger.error("Sent product contains an invalid currency localization identifier");
            return Response.status(HttpResponse.badRequest).build();
        }

        // Get translation fields for product
        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!localizedFieldHandler.setProductLocalizedFields(localizedFieldList)) {
            logger.error("Requested translation for a non configured field");
            return Response.status(HttpResponse.notFound).build();
        }

        // Edit product
        product = DtoMapper.updateProduct(product, localizedCurrencyItemDtoList, localizedTextualItemDtoList,
                localeList, currencyList, manufacturer, admin,
                localizedFieldHandler.getProductLocalizedFieldList());

        if (product == null) {
            logger.error("Error editing product");
            return Response.status(HttpResponse.badRequest).build();
        }

        productDao.updateEntity(product);

        logger.info("Edited product with id: " + product.getId());

        return Response.status(HttpResponse.ok).build();
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
            return Response.status(HttpResponse.notFound).build();
        }

        // Remove cart products references
        List<CartProduct> cartProductList = cartProductDao.getCartProductList();
        for (CartProduct pc : cartProductList) {
            if (pc.getProduct().getId().equals(productId)) {
                cartProductDao.deleteEntity(pc);
            }
        }

        // Remove purchased products references
        List<PurchasedProduct> purchasedProductList = purchasedProductDao.getPurchasedProductList();
        for (PurchasedProduct pp : purchasedProductList) {
            if (pp.getProduct().getId().equals(productId)) {
                purchasedProductDao.deleteEntity(pp);
            }
        }

        productDao.deleteEntity(product);

        logger.info("Removed product with id: " + product.getId());

        return Response.status(HttpResponse.ok).build();
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

        return Response.status(HttpResponse.ok).entity(localeDtoList).build();
    }

    @POST
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

        return Response.status(HttpResponse.ok).build();
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

        return Response.status(HttpResponse.ok).entity(manufacturerDtoList).build();
    }

    @POST
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

        return Response.status(HttpResponse.ok).build();
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

        return Response.status(HttpResponse.ok).entity(currencyDtoList).build();
    }

    @POST
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

        return Response.status(HttpResponse.ok).build();
    }

    private boolean checkInvalidProductDtoFields(ProductDto productDto, List<Locale> localeList,
                                          List<Currency> currencyList)
    {
        // Check for product localizations
        List<LocalizedTextualItemDto> localizedTextualItemDtoList = productDto.getLocalizedTextualItemList();
        if (localizedTextualItemDtoList.isEmpty()) {
            logger.error("Sent product does not contain any textual localization info");
            return true;
        }

        // Check for valid product locales in textual localization
        if (UtilsDto.checkForDtoInvalidTextualLocale(localeList, localizedTextualItemDtoList)) {
            logger.error("Sent product contains an invalid locale");
            return true;
        }

        // Check for product localizations
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = productDto.getLocalizedCurrencyItemList();
        if (localizedCurrencyItemDtoList.isEmpty()) {
            logger.error("Sent product does not contain any currency localization info");
            return true;
        }

        // Check for invalid currencies in dto
        if (UtilsDto.checkForDtoInvalidCurrency(currencyList, localizedCurrencyItemDtoList)) {
            logger.error("Sent product contains an invalid currency");
            return true;
        }

        // Check for invalid product locales in currency localization
        if (UtilsDto.checkForDtoInvalidCurrencyLocale(localeList, localizedCurrencyItemDtoList)) {
            logger.error("Sent product contains an invalid locale");
            return true;
        }

        return false;
    }

}
