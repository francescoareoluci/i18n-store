package it.unifi.ing.controllers;

import it.unifi.ing.dao.CustomerDao;
import it.unifi.ing.dao.ProductDao;
import it.unifi.ing.dao.TranslationDao;
import it.unifi.ing.dto.*;
import it.unifi.ing.model.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/customer")
public class CustomerEndpoint {

    @Inject
    private CustomerDao customerDao;
    @Inject
    private TranslationDao translationDao;
    @Inject
    private ProductDao productDao;

    public CustomerEndpoint() {}

    @GET
    @Path("/{id}/products")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getProducts(@PathParam("id") Long userId)
    {
        List<ProductDto> productDtoList = new ArrayList<>();

        // Get user locale
        Customer customer = customerDao.getEntityById(userId);
        if (customer == null) {
            return Response.status(404).build();
        }
        // Get user locale
        Locale locale = customer.getUserLocale();
        if (locale == null) {
            return Response.status(404).build();
        }

        // Get all localized products
        List<LocalizedProduct> localizedProducts = translationDao.getTranslationsByLocaleId(locale.getId());
        for (LocalizedProduct lp : localizedProducts) {
            // Build dtos
            LocalizedProductDto localizedProductDto = DtoFactory.buildLocalizedProductDto(lp.getId(),
                    lp.getName(), lp.getDescription(), lp.getCategory(), lp.getCurrency(),
                    String.valueOf(lp.getPrice()), lp.getLocale().getLanguageCode());

            ProductDto productDto = DtoFactory.buildProductDto(lp.getProduct().getId(),
                    lp.getProduct().getProdManufacturer().getName(), Arrays.asList(localizedProductDto));

            productDtoList.add(productDto);
        }

        return Response.status(200).entity(productDtoList).build();
    }

    @GET
    @Path("/{userId}/products/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getProducts(@PathParam("userId") Long userId,
                                @PathParam("productId") Long productId)
    {
        // Get user
        Customer customer = customerDao.getEntityById(userId);
        if (customer == null) {
            return Response.status(404).build();
        }
        // Get user locale
        Locale locale = customer.getUserLocale();
        if (locale == null) {
            return Response.status(404).build();
        }

        // Get all localized products
        Product product = productDao.getEntityById(productId);
        if (product == null) {
            return Response.status(404).build();
        }

        for (LocalizedProduct lp : product.getLocalizedProductList()) {
            if (lp.getLocale().getId() == locale.getId()) {
                LocalizedProductDto localizedProductDto = DtoFactory.buildLocalizedProductDto(lp.getId(),
                        lp.getName(), lp.getDescription(), lp.getCategory(), lp.getCurrency(),
                        String.valueOf(lp.getPrice()), lp.getLocale().getLanguageCode());

                ProductDto productDto = DtoFactory.buildProductDto(lp.getProduct().getId(),
                        lp.getProduct().getProdManufacturer().getName(), Arrays.asList(localizedProductDto));

                return Response.status(200).entity(productDto).build();
            }
        }

        return Response.status(404).build();
    }

    @GET
    @Path("/{id}/shopping-cart")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getUserCart(@PathParam("id") Long userId)
    {
        List<ProductDto> cartProducts = new ArrayList<>();

        Customer customer = customerDao.getEntityById(userId);
        if (customer == null) {
            return Response.status(404).build();
        }
        // Get user locale
        Locale locale = customer.getUserLocale();
        if (locale == null) {
            return Response.status(404).build();
        }
        // Get user shopping cart
        ShoppingCart shoppingCart = customer.getShoppingCart();
        if (shoppingCart == null) {
            return Response.status(404).build();
        }

        // Get products
        List<ProductCart> productCartList = shoppingCart.getProductCartList();
        for (ProductCart p : productCartList) {
            Product product = p.getProduct();
            LocalizedProduct lp = translationDao.getTranslationByProductAndLocaleId(
                    product.getId(), locale.getId());

            LocalizedProductDto lpDto = DtoFactory.buildLocalizedProductDto(lp.getId(),
                    lp.getName(), lp.getDescription(), lp.getCategory(), lp.getCurrency(),
                    String.valueOf(lp.getPrice()), lp.getLocale().getLanguageCode());

            ProductDto productDto = DtoFactory.buildProductDto(product.getId(),
                    product.getProdManufacturer().getName(), Arrays.asList(lpDto));

            cartProducts.add(productDto);
        }

        ShoppingCartDto shoppingCartDto = DtoFactory.buildShoppingCartDto(shoppingCart.getId(), cartProducts);

        return Response.status(200).entity(shoppingCartDto).build();
    }

    @GET
    @Path("/{id}/shopping-list")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getUserShoppingList(@PathParam("id") Long userId)
    {
        List<ProductDto> purchasedProducts = new ArrayList<>();

        Customer customer = customerDao.getEntityById(userId);
        if (customer == null) {
            return Response.status(404).build();
        }
        // Get user locale
        Locale locale = customer.getUserLocale();
        if (locale == null) {
            return Response.status(404).build();
        }
        // Get user shopping list
        ShoppingList shoppingList = customer.getShoppingList();
        if (shoppingList == null) {
            return Response.status(404).build();
        }

        // Get products
        List<PurchasedProduct> purchasedProductList = shoppingList.getPurchasedProductList();
        for (PurchasedProduct p : purchasedProductList) {
            Product product = p.getProduct();
            LocalizedProduct lp = translationDao.getTranslationByProductAndLocaleId(
                    product.getId(), locale.getId());

            LocalizedProductDto lpDto = DtoFactory.buildLocalizedProductDto(lp.getId(),
                    lp.getName(), lp.getDescription(), lp.getCategory(), lp.getCurrency(),
                    String.valueOf(lp.getPrice()), lp.getLocale().getLanguageCode());

            ProductDto productDto = DtoFactory.buildProductDto(product.getId(),
                    product.getProdManufacturer().getName(), Arrays.asList(lpDto));

            purchasedProducts.add(productDto);
        }

        ShoppingListDto shoppingListDto = DtoFactory.buildShoppingListDto(shoppingList.getId(), purchasedProducts);

        return Response.status(200).entity(shoppingListDto).build();
    }
}
