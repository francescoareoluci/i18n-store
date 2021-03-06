package it.unifi.ing.model;

import it.unifi.ing.translation.LocalizedCurrencyItem;
import it.unifi.ing.translation.LocalizedField;
import it.unifi.ing.translation.LocalizedTextualItem;

import java.util.UUID;

public class ModelFactory {

    private ModelFactory(){
    }

    public static Admin admin() {
        return new Admin(UUID.randomUUID().toString());
    }
    public static Customer customer() { return new Customer(UUID.randomUUID().toString()); }
    public static Product product() { return new Product(UUID.randomUUID().toString()); }
    public static Locale locale() { return new Locale(UUID.randomUUID().toString()); }
    public static Manufacturer manufacturer() { return new Manufacturer(UUID.randomUUID().toString()); }
    public static Currency currency() { return new Currency(UUID.randomUUID().toString()); }
    public static CartProduct cartProduct() { return new CartProduct(UUID.randomUUID().toString()); }
    public static PurchasedProduct purchasedProduct() { return new PurchasedProduct(UUID.randomUUID().toString()); }
    public static ShoppingList shoppingList() { return new ShoppingList(UUID.randomUUID().toString()); }
    public static ShoppingCart shoppingCart() { return new ShoppingCart(UUID.randomUUID().toString()); }
    public static LocalizedField localizedField() { return new LocalizedField(UUID.randomUUID().toString()); }
    public static LocalizedTextualItem localizedTextualItem()
    {
        return new LocalizedTextualItem(UUID.randomUUID().toString());
    }
    public static LocalizedCurrencyItem localizedCurrencyItem()
    {
        return new LocalizedCurrencyItem(UUID.randomUUID().toString());
    }
}
