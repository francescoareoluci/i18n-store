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

import it.unifi.ing.translationModel.LocalizedCurrencyItem;
import it.unifi.ing.translationModel.LocalizedField;
import it.unifi.ing.translationModel.LocalizedTextualItem;
import it.unifi.ing.translationModel.TranslatableType;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Path("/admin")
public class AdminEndpoint {

    private static final Logger logger = LogManager.getLogger(AdminEndpoint.class);

    private LocalizedField nameLocalizedField;
    private LocalizedField descriptionLocalizedField;
    private LocalizedField categoryLocalizedField;

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

        // Get translation fields for product
        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!getLocalizedFields(localizedFieldList)) {
            logger.error("Requested translation for a non configured field");
            return Response.status(404).build();
        }

        // Get all products
        List<Product> productList = productDao.getProductList();
        for (Product p : productList) {
            // Build localized textual item dto list
            List<LocalizedTextualItemDto> localizedTextualItemDtos = DtoMapper
                    .convertProductLocalizedItemListToDto(nameLocalizedField,
                            descriptionLocalizedField, categoryLocalizedField,
                            p.getLocalizedTextualItemList(), null, true);

            // Build localized currency dto
            List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = DtoMapper
                    .convertLocalizedCurrencyListToDto(null,
                            p.getLocalizedCurrencyItemList(), true);

            // Create product dto
            ProductDto productDto = DtoFactory.buildProductDto(p.getId(), p.getProdManufacturer().getName(),
                    localizedTextualItemDtos, localizedCurrencyItemDtoList);
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

        // Get translation fields for product
        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!getLocalizedFields(localizedFieldList)) {
            logger.error("Requested translation for a non configured field");
            return Response.status(404).build();
        }

        // Build localized textual item dto list
        List<LocalizedTextualItemDto> localizedTextualItemDtos = DtoMapper
                .convertProductLocalizedItemListToDto(nameLocalizedField,
                        descriptionLocalizedField, categoryLocalizedField,
                        product.getLocalizedTextualItemList(), null, true);

        // Build localized currency dto
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = DtoMapper
                .convertLocalizedCurrencyListToDto(null,
                        product.getLocalizedCurrencyItemList(), true);

        // Create product dto
        ProductDto productDto = DtoFactory.buildProductDto(product.getId(),
                product.getProdManufacturer().getName(), localizedTextualItemDtos,
                localizedCurrencyItemDtoList);

        return Response.status(200).entity(productDto).build();
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
        List<LocalizedTextualItemDto> localizedTextualItemDtoList = productDto.getLocalizedTextualItemList();
        if (localizedTextualItemDtoList.isEmpty()) {
            logger.error("Sent product does not contain any localization info");
            return Response.status(404).build();
        }

        // Retrieve available locales
        List<Locale> localeList = localeDao.getLocaleList();

        // Check for valid product locales
        if (checkForInvalidLocaleInDto(localeList, localizedTextualItemDtoList)) {
            logger.error("Sent product contains an invalid locale");
            return Response.status(404).build();
        }

        // @TODO: check for valid locale in currency list

        // Retrieve available currencies
        List<Currency> currencyList = currencyDao.getCurrencyList();

        // Check for invalid currencies in dto
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = productDto.getLocalizedCurrencyItem();
        if (checkForInvalidCurrencyInDto(currencyList, localizedCurrencyItemDtoList)) {
            logger.error("Sent product contains an invalid currency");
            return Response.status(404).build();
        }

        // Check for manufacturers in dto
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
        Product product = buildProduct(admin, manufacturer, localeList, currencyList, productDto);
        if (product == null) {
            logger.error("Unable to build requested product");
            return Response.status(404).build();
        }

        productDao.addEntity(product);

        logger.info("Persisted a new product with id: " + product.getId());

        return Response.status(200).build();
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
            return Response.status(401).build();
        }
        String username = JWTUtil.getUsernameFromToken(token);
        if (username == null) {
            logger.error("Cannot get username from token");
            return Response.status(401).build();
        }

        // Check for valid product
        Product product = productDao.getEntityById(productDto.getId());
        if (product == null) {
            logger.error("Unable to retrieve product with id: " + productDto.getId());
            return Response.status(404).build();
        }

        // Check for valid textual product localizations
        List<LocalizedTextualItem> localizedTextualItemList = product.getLocalizedTextualItemList();
        if (localizedTextualItemList.isEmpty()) {
            logger.error("Empty textual localization list for product with id: " + productDto.getId());
            return Response.status(404).build();
        }

        // Check for valid product currencies
        List<LocalizedCurrencyItem> localizedCurrencyItemList = product.getLocalizedCurrencyItemList();
        if (localizedCurrencyItemList.isEmpty()) {
            logger.error("Empty currency localization list for product with id: " + productDto.getId());
            return Response.status(404).build();
        }

        // Get admin entity
        Admin admin = adminDao.getAdminByUsername(username);
        if (admin == null) {
            logger.error("Unable to retrieve user " + username);
            return Response.status(404).build();
        }

        // Check for product localizations in dto
        List<LocalizedTextualItemDto> localizedTextualItemDtoList = productDto.getLocalizedTextualItemList();
        if (localizedTextualItemDtoList.isEmpty()) {
            logger.error("Sent product does not contain any localization info");
            return Response.status(404).build();
        }

        // Check for product currencies in dto
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = productDto.getLocalizedCurrencyItem();
        if (localizedCurrencyItemDtoList.isEmpty()) {
            logger.error("Sent product does not contain any currency info");
            return Response.status(404).build();
        }

        // Retrieve available locales
        List<Locale> localeList = localeDao.getLocaleList();

        // Check for valid product locales
        if (checkForInvalidLocaleInDto(localeList, localizedTextualItemDtoList)) {
            logger.error("Sent product contains an invalid locale");
            return Response.status(404).build();
        }

        // @TODO: add check for locale in currencies

        // Retrieve available currencies
        List<Currency> currencyList = currencyDao.getCurrencyList();

        if (checkForInvalidCurrencyInDto(currencyList, localizedCurrencyItemDtoList)) {
            logger.error("Sent product contains an invalid currency");
            return Response.status(404).build();
        }

        // Check that the sent localizations have the same id of the persisted one
        if (checkForInvalidLocalizationInDto(localizedTextualItemList, localizedTextualItemDtoList)) {
            logger.error("Sent product contains an invalid localization identifier");
            return Response.status(404).build();
        }

        // @TODO: add check for currencies id

        // Check for manufacturer in dto
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

        // Edit product
        product = updateProduct(product, localizedCurrencyItemDtoList, localizedTextualItemDtoList,
                localeList, currencyList, manufacturer, admin);

        productDao.updateEntity(product);

        logger.info("Edited product with id: " + product.getId());

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

        return Response.status(200).build();
    }

    /**
     * @implNote Check if passed dto contains a supported locale
     * @param localeList: list of supported locales
     * @param localizedTextualItemDtoList: list of textual items in dto
     * @return true if an invalid locale is detected
     */
    private boolean checkForInvalidLocaleInDto(List<Locale> localeList,
                                             List<LocalizedTextualItemDto> localizedTextualItemDtoList)
    {
        boolean invalidLanguage = false;
        boolean localeFound = false;
        for (LocalizedTextualItemDto ltiDto : localizedTextualItemDtoList) {
            for (Locale l : localeList) {
                if (l.getCountryCode().equals(ltiDto.getCountry()) &&
                        l.getLanguageCode().equals(ltiDto.getLocale())) {
                    localeFound = true;
                    break;
                }
            }

            if (!localeFound) {
                invalidLanguage = true;
                break;
            }
        }

        return invalidLanguage;
    }

    /**
     * @implNote Check if the passed dto contains an invalid currency
     * @param currencyList: list of supported currencies
     * @param localizedCurrencyItemDtoList: list of currencies items in dto
     * @return true if an invalid currency is detected
     */
    private boolean checkForInvalidCurrencyInDto(List<Currency> currencyList,
                                               List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList)
    {
        boolean invalidCurrency = false;
        boolean currencyFound = false;
        for (LocalizedCurrencyItemDto lciDto : localizedCurrencyItemDtoList) {
            for (Currency c : currencyList) {
                if (c.getCurrency().equals(lciDto.getCurrency())) {
                    currencyFound = true;
                    break;
                }
            }

            if (!currencyFound) {
                invalidCurrency = true;
                break;
            }
        }

        return invalidCurrency;
    }

    /**
     * @implNote Check if passed dto contains the same locales of the persisted
     *              product
     * @param localizedTextualItemList: list of product localized textual items
     * @param localizedTextualItemDtoList: list of dto localized textual items
     * @return true if an invalid localization is detected
     */
    private boolean checkForInvalidLocalizationInDto(List<LocalizedTextualItem> localizedTextualItemList,
                                                    List<LocalizedTextualItemDto> localizedTextualItemDtoList)
    {
        boolean invalidLocalization = false;
        boolean localizationFound = false;
        for (LocalizedTextualItemDto ltiDto : localizedTextualItemDtoList) {
            for (LocalizedTextualItem lti : localizedTextualItemList) {
                if (lti.getId().equals(ltiDto.getId())) {
                    localizationFound = true;
                    break;
                }
            }

            if (!localizationFound) {
                invalidLocalization = true;
                break;
            }
        }

        return invalidLocalization;
    }

    /**
     * @implNote Build a product by using passed dto
     * @param admin: administrator instance
     * @param manufacturer: manufacturer instance
     * @param localeList: supported locale list
     * @param currencyList: supported currency list
     * @param productDto: passed product dto
     * @return product instance
     */
    private Product buildProduct(Admin admin,
                              Manufacturer manufacturer,
                              List<Locale> localeList,
                              List<Currency> currencyList,
                              ProductDto productDto)
    {
        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!getLocalizedFields(localizedFieldList)) {
            return null;
        }

        List<LocalizedTextualItemDto> localizedTextualItemDtoList = productDto.getLocalizedTextualItemList();
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = productDto.getLocalizedCurrencyItem();

        Product product = ModelFactory.product();
        product.setProdManufacturer(manufacturer);
        product.setProdAdministrator(admin);

        List<LocalizedTextualItem> localizedTextualItemList = new ArrayList<>();
        for (LocalizedTextualItemDto ltiDto : localizedTextualItemDtoList) {
            LocalizedTextualItem lti = ModelFactory.localizedTextualItem();
            lti.setTranslatableItem(product);

            for (Locale l : localeList) {
                if (ltiDto.getLocale().equals(l.getLanguageCode()) &&
                        ltiDto.getCountry().equals(l.getCountryCode())) {
                    lti.setLocale(l);
                    break;
                }
            }
            boolean fieldFound = false;
            if (ltiDto.getFieldType().equals(nameLocalizedField.getType())) {
                lti.setLocalizedField(nameLocalizedField);
                fieldFound = true;
            }
            else if (ltiDto.getFieldType().equals(descriptionLocalizedField.getType())) {
                lti.setLocalizedField(descriptionLocalizedField);
                fieldFound = true;
            }
            else if (ltiDto.getFieldType().equals(categoryLocalizedField.getType())) {
                lti.setLocalizedField(categoryLocalizedField);
                fieldFound = true;
            }
            if (fieldFound) {
                lti.setText(ltiDto.getText());
            }
            else {
                logger.error("Invalid field type found in requested product: " +
                        ltiDto.getFieldType());
                return null;
            }

            localizedTextualItemList.add(lti);
        }

        List<LocalizedCurrencyItem> localizedCurrencyItemList = new ArrayList<>();
        for (LocalizedCurrencyItemDto lciDto : localizedCurrencyItemDtoList) {
            LocalizedCurrencyItem lci = ModelFactory.localizedCurrencyItem();
            for (Currency c : currencyList) {
                if (lciDto.getCurrency().equals(c.getCurrency())) {
                    lci.setCurrency(c);
                    break;
                }
            }
            lci.setPrice(lciDto.getPrice());
            lci.setProduct(product);
            for (Locale l : localeList) {
                if (lciDto.getLocale().equals(l.getLanguageCode()) &&
                        lciDto.getCountry().equals(l.getCountryCode())) {
                    lci.setLocale(l);
                    break;
                }
            }

            localizedCurrencyItemList.add(lci);
        }

        product.setLocalizedCurrencyItemList(localizedCurrencyItemList);
        product.setLocalizedTextualItemList(localizedTextualItemList);

        return product;
    }

    private Product updateProduct(Product product, List<LocalizedCurrencyItemDto> localizedCurrencyItemDtos,
                                  List<LocalizedTextualItemDto> localizedTextualItemDtos,
                                  List<Locale> localeList, List<Currency> currencyList,
                                  Manufacturer manufacturer, Admin admin)
    {
        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!getLocalizedFields(localizedFieldList)) {
            return null;
        }

        List<LocalizedCurrencyItem> localizedCurrencyItemList = product.getLocalizedCurrencyItemList();
        List<LocalizedTextualItem> localizedTextualItemList = product.getLocalizedTextualItemList();

        // Edit product
        product.setProdManufacturer(manufacturer);
        product.setProdAdministrator(admin);

        for (LocalizedTextualItemDto ltiDto : localizedTextualItemDtos) {
            for (LocalizedTextualItem lti : localizedTextualItemList) {
                if (lti.getId().equals(ltiDto.getId())) {
                    lti.setTranslatableItem(product);
                    for (Locale l : localeList) {
                        if (ltiDto.getLocale().equals(l.getLanguageCode()) &&
                                ltiDto.getCountry().equals(l.getCountryCode())) {
                            lti.setLocale(l);
                            break;
                        }
                    }
                    lti.setText(ltiDto.getText());
                    boolean fieldFound = false;
                    if (ltiDto.getFieldType().equals(nameLocalizedField.getType())) {
                        lti.setLocalizedField(nameLocalizedField);
                        fieldFound = true;
                    }
                    else if (ltiDto.getFieldType().equals(descriptionLocalizedField.getType())) {
                        lti.setLocalizedField(descriptionLocalizedField);
                        fieldFound = true;
                    }
                    else if (ltiDto.getFieldType().equals(categoryLocalizedField.getType())) {
                        lti.setLocalizedField(categoryLocalizedField);
                        fieldFound = true;
                    }
                    if (fieldFound) {
                        lti.setText(ltiDto.getText());
                    }
                    else {
                        return null;
                    }
                }
            }
        }
        product.setLocalizedTextualItemList(localizedTextualItemList);

        for (LocalizedCurrencyItemDto lciDto : localizedCurrencyItemDtos) {
            for (LocalizedCurrencyItem lci : localizedCurrencyItemList) {
                if (lci.getId().equals(lciDto.getId())) {
                    lci.setProduct(product);
                    for (Locale l : localeList) {
                        if (lciDto.getLocale().equals(l.getLanguageCode()) &&
                                lciDto.getCountry().equals(l.getCountryCode())) {
                            lci.setLocale(l);
                            break;
                        }
                    }
                    lci.setPrice(lciDto.getPrice());
                    for (Currency c : currencyList) {
                        if (lciDto.getCurrency().equals(c.getCurrency())) {
                            lci.setCurrency(c);
                        }
                    }
                }
            }
        }
        product.setLocalizedCurrencyItemList(localizedCurrencyItemList);

        return product;
    }

    private boolean getLocalizedFields(List<LocalizedField> localizedFieldList)
    {
        boolean foundName = false;
        boolean foundDescr = false;
        boolean foundCat = false;
        for (LocalizedField lf : localizedFieldList) {
            switch (lf.getType()) {
                case TranslatableType.productName:
                    nameLocalizedField = lf;
                    foundName = true;
                    break;
                case TranslatableType.productDescription:
                    descriptionLocalizedField = lf;
                    foundDescr = true;
                    break;
                case TranslatableType.productCategory:
                    categoryLocalizedField = lf;
                    foundCat = true;
                    break;
                default:
                    break;
            }
        }

        if (!foundName || !foundDescr || !foundCat) {
            return false;
        }
        return true;
    }
}
