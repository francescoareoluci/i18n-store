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

import it.unifi.ing.translation.LocalizedField;
import it.unifi.ing.translation.LocalizedFieldHandler;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Path("/customer")
public class CustomerEndpoint {

    private static final Logger logger = LogManager.getLogger(CustomerEndpoint.class);

    @Inject
    private LocalizedFieldHandler localizedFieldHandler;
    @Inject
    private CustomerDao customerDao;
    @Inject
    private ProductDao productDao;
    @Inject
    private ShoppingCartDao shoppingCartDao;
    @Inject
    private ShoppingListDao shoppingListDao;
    @Inject
    private LocalizedFieldDao localizedFieldDao;

    public CustomerEndpoint() {}

    @GET
    @Path("/products")
    @JWTTokenNeeded(Permissions = UserRole.CUSTOMER)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getProducts(@Context HttpHeaders headers)
    {
        logger.debug("Requested /products endpoint");

        // Get username from token
        String username = getUsernameFromToken(headers);
        if (username.isEmpty()) { return Response.status(401).build(); }

        List<ProductDto> productDtoList = new ArrayList<>();

        // Get user
        Customer customer = customerDao.getCustomerByUsername(username);
        if (customer == null) {
            logger.error("Unable to retrieve user by username: " + username);
            return Response.status(404).build();
        }
        // Get user locale
        Locale locale = customer.getUserLocale();
        if (locale == null) {
            logger.error("Unable to retrieve locale for user " + customer.getMail());
            return Response.status(404).build();
        }

        logger.info("Customer " + customer.getMail() + " has requested the product list. Language: " +
                locale.getLanguageCode());

        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!localizedFieldHandler.setProductLocalizedFields(localizedFieldList)) {
            logger.error("Requested translation for a non configured field");
            return Response.status(404).build();
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

            // Build localized currency dto list
            List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = DtoMapper
                    .convertLocalizedCurrencyListToDto(locale, localizedFieldHandler.getProductPriceField(),
                            p.getLocalizedItemList(), false);

            // Create product dto
            ProductDto productDto = DtoFactory.buildShortProductDto(p.getId(), p.getProdManufacturer().getName(),
                    localizedTextualItemDtos, localizedCurrencyItemDtoList);
            productDtoList.add(productDto);
        }

        return Response.status(200).entity(productDtoList).build();
    }

    @GET
    @Path("/products/{productId}")
    @JWTTokenNeeded(Permissions = UserRole.CUSTOMER)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getProductById(@Context HttpHeaders headers,
                                @PathParam("productId") Long productId)
    {
        logger.debug("Requested /products/" + productId + " endpoint");

        // Get username from token
        String username = getUsernameFromToken(headers);
        if (username.isEmpty()) { return Response.status(401).build(); }

        // Get user
        Customer customer = customerDao.getCustomerByUsername(username);
        if (customer == null) {
            logger.error("Unable to retrieve user by username: " + username);
            return Response.status(404).build();
        }
        // Get user locale
        Locale locale = customer.getUserLocale();
        if (locale == null) {
            logger.error("Unable to retrieve locale for user " + customer.getMail());
            return Response.status(404).build();
        }

        // Get all localized products by product id
        Product product = productDao.getEntityById(productId);
        if (product == null) {
            return Response.status(404).build();
        }

        logger.info("Customer " + customer.getMail() + " has requested the product id: " +
                productId + ". Language: " + locale.getLanguageCode());

        // Get localized fields of interest for products (name, description, category)
        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!localizedFieldHandler.setProductLocalizedFields(localizedFieldList)) {
            logger.error("Requested translation for a non configured field");
            return Response.status(404).build();
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

        return Response.status(200).entity(productDto).build();
    }

    @GET
    @Path("/shopping-cart")
    @JWTTokenNeeded(Permissions = UserRole.CUSTOMER)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getUserCart(@Context HttpHeaders headers)
    {
        logger.debug("Requested /shopping-cart endpoint");

        // Get username from token
        String username = getUsernameFromToken(headers);
        if (username.isEmpty()) { return Response.status(401).build(); }

        List<ProductDto> cartProducts = new ArrayList<>();

        Customer customer = customerDao.getCustomerByUsername(username);
        if (customer == null) {
            logger.error("Unable to retrieve user by username: " + username);
            return Response.status(404).build();
        }
        // Get user locale
        Locale locale = customer.getUserLocale();
        if (locale == null) {
            logger.error("Unable to retrieve locale for user " + customer.getMail());
            return Response.status(404).build();
        }
        // Get user shopping cart
        ShoppingCart shoppingCart = customer.getShoppingCart();
        if (shoppingCart == null) {
            logger.error("Unable to retrieve shopping cart for user " + customer.getMail());
            return Response.status(404).build();
        }

        // Get localized fields of interest for products (name, description, category)
        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!localizedFieldHandler.setProductLocalizedFields(localizedFieldList)) {
            logger.error("Requested translation for a non configured field");
            return Response.status(404).build();
        }

        // Get products
        List<CartProduct> cartProductList = shoppingCart.getCartProductList();
        float totalCost = 0;
        for (CartProduct p : cartProductList) {
            Product product = p.getProduct();

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

            for (LocalizedCurrencyItemDto ltiDto : localizedCurrencyItemDtoList) {
                if (ltiDto.getLanguageCode().equals(locale.getLanguageCode()) &&
                        ltiDto.getCountryCode().equals(locale.getCountryCode())) {
                    totalCost += ltiDto.getPrice();
                }
            }

            // Build product dto
            ProductDto productDto = DtoFactory.buildShortProductDto(product.getId(),
                    product.getProdManufacturer().getName(), localizedTextualItemDtos, localizedCurrencyItemDtoList);

            cartProducts.add(productDto);
        }

        ShoppingCartDto shoppingCartDto = DtoFactory.buildShoppingCartDto(shoppingCart.getId(),
                cartProducts, totalCost);

        return Response.status(200).entity(shoppingCartDto).build();
    }

    @POST
    @Path("/shopping-cart/add/{prodId}")
    @JWTTokenNeeded(Permissions = UserRole.CUSTOMER)
    @Transactional
    public Response addProductToCart(@Context HttpHeaders headers,
                                @PathParam("prodId") Long productId)
    {
        logger.debug("Requested /shopping-cart/add/" + productId +  " endpoint");

        // Get username from token
        String username = getUsernameFromToken(headers);
        if (username.isEmpty()) { return Response.status(401).build(); }

        Customer customer = customerDao.getCustomerByUsername(username);
        if (customer == null) {
            logger.error("Unable to retrieve user by username: " + username);
            return Response.status(404).build();
        }

        // Get user shopping cart
        ShoppingCart shoppingCart = customer.getShoppingCart();
        if (shoppingCart == null) {
            logger.error("Unable to retrieve shopping cart for user " + customer.getMail());
            return Response.status(404).build();
        }

        // Get products
        List<CartProduct> cartProductList = shoppingCart.getCartProductList();
        Product newProduct = productDao.getEntityById(productId);
        if (newProduct == null) {
            logger.error("Unable to retrieve product id: " + productId);
            return Response.status(404).build();
        }
        CartProduct newCartProduct = ModelFactory.cartProduct();
        newCartProduct.setProduct(newProduct);
        newCartProduct.setShoppingCart(shoppingCart);
        cartProductList.add(newCartProduct);
        shoppingCart.setCartProductList(cartProductList);

        // Persist
        shoppingCartDao.updateEntity(shoppingCart);

        logger.info("User " + username + " has added to its cart the product id: " + productId);

        return Response.status(200).build();
    }

    @POST
    @Path("/shopping-cart/remove/{prodId}")
    @JWTTokenNeeded(Permissions = UserRole.CUSTOMER)
    @Transactional
    public Response removeProductFromCart(@Context HttpHeaders headers,
                                @PathParam("prodId") Long productId)
    {
        logger.debug("Requested /shopping-cart/remove/" + productId + " endpoint");

        // Get username from token
        String username = getUsernameFromToken(headers);
        if (username.isEmpty()) { return Response.status(401).build(); }

        Customer customer = customerDao.getCustomerByUsername(username);
        if (customer == null) {
            logger.error("Unable to retrieve user by username: " + username);
            return Response.status(404).build();
        }

        // Get user shopping cart
        ShoppingCart shoppingCart = customer.getShoppingCart();
        if (shoppingCart == null) {
            logger.error("Unable to retrieve shopping cart for user " + customer.getMail());
            return Response.status(404).build();
        }

        // Get products
        List<CartProduct> cartProductList = shoppingCart.getCartProductList();
        int removeIdx = 0;
        boolean productFound = false;
        for (CartProduct pc : cartProductList) {
            if (pc.getProduct().getId().equals(productId)) {
                productFound = true;
                break;
            }
            removeIdx++;
        }

        if (!productFound) {
            logger.warn("User " + username + " has requested to remove non-existent" +
                    "product [" + productId + "] from its cart");
            return Response.status(404).build();
        }

        cartProductList.remove(removeIdx);
        shoppingCart.setCartProductList(cartProductList);

        // Persist
        shoppingCartDao.updateEntity(shoppingCart);

        logger.info("User " + username + " has removed from its cart the product id: " + productId);

        return Response.status(200).build();
    }

    @POST
    @Path("/shopping-cart/checkout")
    @JWTTokenNeeded(Permissions = UserRole.CUSTOMER)
    @Transactional
    public Response purchaseCart(@Context HttpHeaders headers)
    {
        logger.debug("Requested /shopping-cart/checkout endpoint");

        // Get username from token
        String username = getUsernameFromToken(headers);
        if (username.isEmpty()) { return Response.status(401).build(); }

        Customer customer = customerDao.getCustomerByUsername(username);
        if (customer == null) {
            logger.error("Unable to retrieve user by username: " + username);
            return Response.status(404).build();
        }
        // Get user shopping cart
        ShoppingCart shoppingCart = customer.getShoppingCart();
        if (shoppingCart == null) {
            logger.error("Unable to retrieve shopping cart for user " + customer.getMail());
            return Response.status(404).build();
        }
        // Get user shopping list
        ShoppingList shoppingList = customer.getShoppingList();
        if (shoppingList == null) {
            logger.error("Unable to retrieve shopping list for user " + customer.getMail());
            return Response.status(404).build();
        }

        // Get already purchased products
        List<PurchasedProduct> purchasedProductList = shoppingList.getPurchasedProductList();

        // Get products in cart
        List<CartProduct> cartProductList = shoppingCart.getCartProductList();
        if (cartProductList.size() == 0) {
            logger.info("User " + username + " has requested checkout with empty shopping cart");
            return Response.status(200).build();
        }

        for (CartProduct pc : cartProductList) {
            Product p = pc.getProduct();
            PurchasedProduct pp = ModelFactory.purchasedProduct();
            pp.setProduct(p);
            pp.setShoppingList(shoppingList);
            purchasedProductList.add(pp);
        }

        shoppingList.setPurchasedProductList(purchasedProductList);
        cartProductList.clear();
        shoppingCart.setCartProductList(cartProductList);

        // Persist
        shoppingListDao.updateEntity(shoppingList);
        shoppingCartDao.updateEntity(shoppingCart);

        logger.info("User " + username + " has performed products checkout");

        return Response.status(200).build();
    }

    @GET
    @Path("/shopping-list")
    @JWTTokenNeeded(Permissions = UserRole.CUSTOMER)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response getUserShoppingList(@Context HttpHeaders headers)
    {
        logger.debug("Requested /shopping-list endpoint");

        // Get username from token
        String username = getUsernameFromToken(headers);
        if (username.isEmpty()) { return Response.status(401).build(); }

        List<ProductDto> purchasedProducts = new ArrayList<>();

        Customer customer = customerDao.getCustomerByUsername(username);
        if (customer == null) {
            logger.error("Unable to retrieve user by username: " + username);
            return Response.status(404).build();
        }
        // Get user locale
        Locale locale = customer.getUserLocale();
        if (locale == null) {
            logger.error("Unable to retrieve locale for user " + customer.getMail());
            return Response.status(404).build();
        }
        // Get user shopping list
        ShoppingList shoppingList = customer.getShoppingList();
        if (shoppingList == null) {
            logger.error("Unable to retrieve shopping list for user " + customer.getMail());
            return Response.status(404).build();
        }

        // Get localized fields of interest for products (name, description, category)
        List<LocalizedField> localizedFieldList = localizedFieldDao.getLocalizedFieldList();
        if (!localizedFieldHandler.setProductLocalizedFields(localizedFieldList)) {
            logger.error("Requested translation for a non configured field");
            return Response.status(404).build();
        }

        // Get products
        List<PurchasedProduct> purchasedProductList = shoppingList.getPurchasedProductList();
        for (PurchasedProduct p : purchasedProductList) {
            Product product = p.getProduct();

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

            // Build product dto
            ProductDto productDto = DtoFactory.buildShortProductDto(product.getId(),
                    product.getProdManufacturer().getName(), localizedTextualItemDtos, localizedCurrencyItemDtoList);

            purchasedProducts.add(productDto);
        }

        ShoppingListDto shoppingListDto = DtoFactory.buildShoppingListDto(shoppingList.getId(), purchasedProducts);

        return Response.status(200).entity(shoppingListDto).build();
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
