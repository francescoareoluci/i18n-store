package it.unifi.ing.beans.builders;

import it.unifi.ing.dao.*;
import it.unifi.ing.model.*;
import it.unifi.ing.translationModel.LocalizedCurrencyItem;
import it.unifi.ing.translationModel.LocalizedField;
import it.unifi.ing.translationModel.LocalizedTextualItem;
import it.unifi.ing.translationModel.TranslatableType;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
@Startup
public class StartupBean {

    @Inject
    private AdminDao adminDao;
    @Inject
    private ManufacturerDao manufacturerDao;
    @Inject
    private LocaleDao localeDao;
    @Inject
    private CurrencyDao currencyDao;
    @Inject
    private CustomerDao customerDao;
    @Inject
    private ProductDao productDao;
    @Inject
    private ShoppingListDao shoppingListDao;
    @Inject
    private ShoppingCartDao shoppingCartDao;
    @Inject
    private LocalizedFieldDao localizedFieldDao;

    public StartupBean() {}

    @PostConstruct
    @Transactional
    public void init()
    {
        Admin user1 = buildAdmin("Mario", "Rossi", "mario.rossi@example.com", "pass1");
        Admin user2 = buildAdmin("Carlo", "Bianchi", "carlo.bianchi@example.com", "pass2");

        Manufacturer manufacturer1 = buildManufacturer("Samsung");
        Manufacturer manufacturer2 = buildManufacturer("Sony");
        Manufacturer manufacturer3 = buildManufacturer("Netgear");
        Manufacturer manufacturer4 = buildManufacturer("Blue Microphone");
        Manufacturer manufacturer5 = buildManufacturer("Bose");
        Manufacturer manufacturer6 = buildManufacturer("Sennheiser");
        Manufacturer manufacturer7 = buildManufacturer("Tommy Hilfiger");
        Manufacturer manufacturer8 = buildManufacturer("Lacoste");

        Locale locale1 = buildLocale("it", "IT");
        Locale locale2 = buildLocale("en", "US");

        Currency currency1 = buildCurrency("€");
        Currency currency2 = buildCurrency("$");

        Customer customer1 = buildCustomer("John", "White", "john.white@example.com", "pass3", locale2);
        Customer customer2 = buildCustomer("Carla", "Verdi", "carla.verdi@example.com", "pass4", locale1);

        ShoppingCart sc1 = buildShoppingCart(customer1);
        ShoppingCart sc2 = buildShoppingCart(customer2);

        ShoppingList sl1 = buildShoppingList(customer1);
        ShoppingList sl2 = buildShoppingList(customer2);

        Product prod1 = buildProduct(user1, manufacturer2);

        LocalizedField lf1 = buildLocalizedField(TranslatableType.productName);
        LocalizedField lf2 = buildLocalizedField(TranslatableType.productDescription);
        LocalizedField lf3 = buildLocalizedField(TranslatableType.productCategory);

        List<LocalizedTextualItem> prod1TextualItems = new ArrayList<>();
        prod1TextualItems.add(buildLocalizedTextualItem("Sony Alpha a7II", prod1, locale2, lf1));
        prod1TextualItems.add(buildLocalizedTextualItem("The Sony Alpha a7II Mirrorless " +
                "Digital Camera is the world's first full-frame camera with 5-axis image " +
                        "stabilization and provides camera shake compensation for " +
                        "wide-ranging mountable lenses.", prod1, locale2, lf2));
        prod1TextualItems.add(buildLocalizedTextualItem("camera", prod1, locale2, lf3));
        prod1TextualItems.add(buildLocalizedTextualItem("Sony Alpha a7II", prod1, locale1, lf1));
        prod1TextualItems.add(buildLocalizedTextualItem("ITA DESCR", prod1, locale1, lf2));
        prod1TextualItems.add(buildLocalizedTextualItem("fotocamera", prod1, locale1, lf3));

        List<LocalizedCurrencyItem> prod1CurrencyItems = new ArrayList<>();
        prod1CurrencyItems.add(buildLocalizedCurrencyItem(currency1, (float)1499.00, prod1, locale1));
        prod1CurrencyItems.add(buildLocalizedCurrencyItem(currency2, (float)1598.00, prod1, locale2));

        prod1.setLocalizedTextualItemList(prod1TextualItems);
        prod1.setLocalizedCurrencyItemList(prod1CurrencyItems);

        PurchasedProduct pp1 = buildPurchasedProduct(prod1, sl1);

        sl1.setPurchasedProductList(Arrays.asList(pp1));
        sl2.setPurchasedProductList(Arrays.asList());

        sc1.setCartProductList(Arrays.asList());

        customer1.setShoppingList(sl1);
        customer1.setShoppingCart(sc1);
        customer2.setShoppingList(sl2);
        customer2.setShoppingCart(sc2);

        adminDao.addEntity(user1);
        adminDao.addEntity(user2);
        manufacturerDao.addEntity(manufacturer1);
        manufacturerDao.addEntity(manufacturer2);
        manufacturerDao.addEntity(manufacturer3);
        manufacturerDao.addEntity(manufacturer4);
        manufacturerDao.addEntity(manufacturer5);
        manufacturerDao.addEntity(manufacturer6);
        manufacturerDao.addEntity(manufacturer7);
        manufacturerDao.addEntity(manufacturer8);
        localeDao.addEntity(locale1);
        localeDao.addEntity(locale2);
        currencyDao.addEntity(currency1);
        currencyDao.addEntity(currency2);
        localizedFieldDao.addEntity(lf1);
        localizedFieldDao.addEntity(lf2);
        localizedFieldDao.addEntity(lf3);
        productDao.addEntity(prod1);
        shoppingCartDao.addEntity(sc1);
        shoppingCartDao.addEntity(sc2);
        shoppingListDao.addEntity(sl1);
        shoppingListDao.addEntity(sl2);
        customerDao.addEntity(customer1);
        customerDao.addEntity(customer2);
    }

    private void cleanDB()
    {
        List<Customer> customerList = customerDao.getCustomerList();
        for (Customer c : customerList) { customerDao.deleteEntity(c); }

        List<Product> productList = productDao.getProductList();
        for (Product p : productList) { productDao.deleteEntity(p); }

        List<Admin> adminList = adminDao.getAdminList();
        for (Admin a : adminList) { adminDao.deleteEntity(a); }

        List<Manufacturer> manufacturerList = manufacturerDao.getManufacturerList();
        for (Manufacturer m : manufacturerList) { manufacturerDao.deleteEntity(m); }

        List<Locale> localeList = localeDao.getLocaleList();
        for (Locale l : localeList) { localeDao.deleteEntity(l); }
    }

    private Admin buildAdmin(String firstName, String lastName, String email, String password)
    {
        Admin user = ModelFactory.admin();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMail(email);
        user.setPassword(password);

        return user;
    }

    private Customer buildCustomer(String firstName, String lastName, String email, String password, Locale locale)
    {
        Customer user = ModelFactory.customer();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMail(email);
        user.setPassword(password);
        user.setUserLocale(locale);

        return user;
    }

    private Manufacturer buildManufacturer(String name)
    {
        Manufacturer manufacturer = ModelFactory.manufacturer();
        manufacturer.setName(name);

        return manufacturer;
    }

    private Locale buildLocale(String languageCode, String countryCode)
    {
        Locale locale = ModelFactory.locale();
        locale.setLanguageCode(languageCode);
        locale.setCountryCode(countryCode);

        return locale;
    }

    private Product buildProduct(Admin admin, Manufacturer manufacturer)
    {
        Product product = ModelFactory.product();
        product.setProdAdministrator(admin);
        product.setProdManufacturer(manufacturer);

        return product;
    }

    private LocalizedProduct buildLocalizedProduct(String name, String description, String category,
                                                   Locale locale, Product product, Currency currency, float price)
    {
        LocalizedProduct localizedProduct = ModelFactory.localizedProduct();
        localizedProduct.setName(name);
        localizedProduct.setDescription(description);
        localizedProduct.setCategory(category);
        localizedProduct.setCurrency(currency);
        localizedProduct.setPrice(price);
        localizedProduct.setLocale(locale);
        localizedProduct.setProduct(product);

        return localizedProduct;
    }

    private ShoppingList buildShoppingList(Customer c)
    {
        ShoppingList sl = ModelFactory.shoppingList();
        sl.setCustomer(c);

        return sl;
    }

    private ShoppingCart buildShoppingCart(Customer c)
    {
        ShoppingCart sc = ModelFactory.shoppingCart();
        sc.setCustomer(c);

        return sc;
    }

    private CartProduct buildCartProduct(Product p, ShoppingCart sc)
    {
        CartProduct pc = ModelFactory.cartProduct();
        pc.setShoppingCart(sc);
        pc.setProduct(p);

        return pc;
    }

    private PurchasedProduct buildPurchasedProduct(Product p, ShoppingList sl)
    {
        PurchasedProduct pp = ModelFactory.purchasedProduct();
        pp.setShoppingList(sl);
        pp.setProduct(p);

        return pp;
    }

    private Currency buildCurrency(String currency)
    {
        Currency c = ModelFactory.currency();
        c.setCurrency(currency);

        return c;
    }

    private LocalizedCurrencyItem buildLocalizedCurrencyItem(Currency c,
                                                             float price,
                                                             Product product,
                                                             Locale locale)
    {
        LocalizedCurrencyItem localizedCurrencyItem = ModelFactory.localizedCurrencyItem();
        localizedCurrencyItem.setCurrency(c);
        localizedCurrencyItem.setPrice(price);
        localizedCurrencyItem.setProduct(product);
        localizedCurrencyItem.setLocale(locale);

        return localizedCurrencyItem;
    }

    private LocalizedField buildLocalizedField(String type)
    {
        LocalizedField localizedField = ModelFactory.localizedField();
        localizedField.setType(type);

        return localizedField;
    }

    private LocalizedTextualItem buildLocalizedTextualItem(String text,
                                                           TranslatableItem product,
                                                           Locale locale,
                                                           LocalizedField localizedField)
    {
        LocalizedTextualItem localizedTextualItem = ModelFactory.localizedTextualItem();
        localizedTextualItem.setLocale(locale);
        localizedTextualItem.setTranslatableItem(product);
        localizedTextualItem.setLocalizedField(localizedField);
        localizedTextualItem.setText(text);

        return localizedTextualItem;
    }

}
