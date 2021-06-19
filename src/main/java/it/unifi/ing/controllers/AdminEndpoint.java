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

@Path("/admin")
public class AdminEndpoint {

    @Inject
    private AdminDao adminDao;
    @Inject
    private CustomerDao customerDao;
    @Inject
    private ProductDao productDao;
    @Inject
    private LocaleDao localeDao;
    @Inject
    private ManufacturerDao manufacturerDao;

    public AdminEndpoint() {}

    @GET
    @Path("/users")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers()
    {
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
                        lp.getName(), lp.getDescription(), lp.getCategory(), lp.getCurrency(),
                        String.valueOf(lp.getPrice()), lp.getLocale().getLanguageCode(),
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
        // Get specific product
        Product product = productDao.getEntityById(productId);
        if (product == null) {
            return Response.status(404).build();
        }

        // Get product translations
        List<LocalizedProduct> localizedProductList = product.getLocalizedProductList();

        // Build dto array
        List<LocalizedProductDto> localizedProductDtoList = new ArrayList<>();
        for (LocalizedProduct lp : localizedProductList) {
            LocalizedProductDto localizedProductDto = DtoFactory.buildLocalizedProductDto(lp.getId(),
                    lp.getName(), lp.getDescription(), lp.getCategory(), lp.getCurrency(),
                    String.valueOf(lp.getPrice()), lp.getLocale().getLanguageCode(),
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
        // Get username from token
        String token = extractBearerHeader(headers);
        if (token.isEmpty()) {
            return Response.status(401).build();
        }
        String username = JWTUtil.getUsernameFromToken(token);
        if (username == null) {
            return Response.status(401).build();
        }

        // Get admin entity
        Admin admin = adminDao.getUserByUsername(username);
        if (admin == null) {
            return Response.status(404).build();
        }

        // Check for product localizations
        List<LocalizedProductDto> localizedProductDtoList = productDto.getLocalizedInfo();
        if (localizedProductDtoList.isEmpty()) {
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
            return Response.status(404).build();
        }

        List<Manufacturer> manufacturerList = manufacturerDao.getManufacturerByName(productDto.getManufacturer());
        Manufacturer manufacturer;
        if (manufacturerList.isEmpty()) {
            // Create new one
            manufacturer = ModelFactory.manufacturer();
            manufacturer.setName(productDto.getManufacturer());
        }
        else {
            manufacturer = manufacturerList.get(0);
        }

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
            lp.setCurrency(lpDto.getCurrency());
            lp.setPrice(Float.parseFloat(lpDto.getPrice()));

            localizedProductList.add(lp);
        }

        product.setLocalizedProductList(localizedProductList);

        productDao.addEntity(product);

        return Response.status(200).build();
    }

    @GET
    @Path("/locales")
    @JWTTokenNeeded(Permissions = UserRole.ADMIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocales()
    {
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
        Locale locale = ModelFactory.locale();
        locale.setCountryCode(localeDto.getCountryCode());
        locale.setLanguageCode(localeDto.getLanguageCode());

        // Persist given locale
        localeDao.addEntity(locale);

        return Response.status(200).build();
    }

    @GET
    @Path("/manufacturers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getManufacturerList()
    {
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
        Manufacturer manufacturer = ModelFactory.manufacturer();
        manufacturer.setName(manufacturerDtoDto.getName());

        // Persist given manufacturer
        manufacturerDao.addEntity(manufacturer);

        return Response.status(200).build();
    }

    private String extractBearerHeader(HttpHeaders headers)
    {
        // Get the HTTP Authorization header from the request
        String authorizationHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Extract the token from the HTTP Authorization header
        return authorizationHeader.substring("Bearer".length()).trim();
    }

}
