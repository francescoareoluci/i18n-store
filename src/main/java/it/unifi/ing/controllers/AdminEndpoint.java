package it.unifi.ing.controllers;

import it.unifi.ing.dao.*;
import it.unifi.ing.dto.*;
import it.unifi.ing.model.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
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
                        String.valueOf(lp.getPrice()), lp.getLocale().getLanguageCode());

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
                    String.valueOf(lp.getPrice()), lp.getLocale().getLanguageCode());

            localizedProductDtoList.add(localizedProductDto);
        }
        ProductDto productDto = DtoFactory.buildProductDto(product.getId(),
                    product.getProdManufacturer().getName(), localizedProductDtoList);

        return Response.status(200).entity(productDto).build();
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
    @Produces(MediaType.APPLICATION_JSON)
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
}
