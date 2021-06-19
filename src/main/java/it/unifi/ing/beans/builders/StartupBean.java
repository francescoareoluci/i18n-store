package it.unifi.ing.beans.builders;

import it.unifi.ing.dao.*;
import it.unifi.ing.model.*;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Arrays;

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
    private CustomerDao customerDao;
    @Inject
    private ProductDao productDao;
    @Inject
    private TranslationDao translationDao;
    @Inject
    private ShoppingListDao shoppingListDao;
    @Inject
    private ShoppingCartDao shoppingCartDao;
    @Inject
    private ProductCartDao productCartDao;
    @Inject
    private PurchasedProductDao purchasedProductDao;

    public StartupBean() {
    }

    @PostConstruct
    @Transactional
    public void init() {
        Admin user1 = buildAdmin("Mario", "Rossi", "mario.rossi@example.com", "pass1");
        Admin user2 = buildAdmin("Carlo", "Bianchi", "carlo.bianchi@example.com", "pass2");

        Manufacturer manufacturer1 = buildManufacturer("Samsung");
        Manufacturer manufacturer2 = buildManufacturer("Sony");

        Locale locale1 = buildLocale("it", "IT");
        Locale locale2 = buildLocale("en", "US");

        Customer customer1 = buildCustomer("John", "White", "john.white@example.com", "pass3", locale2);
        Customer customer2 = buildCustomer("Carla", "Verdi", "carla.verdi@example.com", "pass4", locale1);

        ShoppingCart sc1 = buildShoppingCart(customer1);
        ShoppingCart sc2 = buildShoppingCart(customer2);

        ShoppingList sl1 = buildShoppingList(customer1);
        ShoppingList sl2 = buildShoppingList(customer2);

        Product prod1 = buildProduct(user1, manufacturer1);
        Product prod2 = buildProduct(user1, manufacturer2);

        LocalizedProduct lc1 = buildLocalizedProduct("Sony Alpha a7II", "The Sony Alpha α7 II Mirrorless " +
                "Digital Camera is the world's first full-frame camera with 5-axis image " +
                "stabilization and provides camera shake compensation for " +
                "wide-ranging mountable lenses.", "electronic", locale2, prod1, "$", (float)1598.00);
        LocalizedProduct lc2 = buildLocalizedProduct("Sony Alpha a7II", "Struttura completa, " +
                "dimensioni del palmo. Perfezione per tutti. Stabilità per tutti.La qualità" +
                " delle immagini mozzafiato incontra la libertà di ripresa senza pari nel 7 II, " +
                "la prima fotocamera full-frame al mondo con stabilizzazione " +
                "dell'immagine a 5 assi.", "elettronica", locale1, prod1, "€", (float)1.499);
        LocalizedProduct lc3 = buildLocalizedProduct("Samsung Smartphone Galaxy S21", "Pro Grade " +
                "Camera: Zoom in close, take photos and videos like a pro, " +
                "and capture incredible share-ready moments " +
                "with our easy-to-use, multi-lens camera", "electronic", locale2, prod2, "$", (float)799.99);
        LocalizedProduct lc4 = buildLocalizedProduct("Samsung Smartphone Galaxy S21", "Fotocamera " +
                "con teleobiettivo 64MP, Fotocamera frontale 12MP, Fotocamera grandangolare 12MP: " +
                "tutta la tecnologia che ti occorre per i migliori scatti con il tuo smartphone ",
                "elettronica", locale1, prod2, "€", (float)879.99);

        prod1.setLocalizedProductList(Arrays.asList(lc1, lc2));
        prod2.setLocalizedProductList(Arrays.asList(lc3, lc4));

        PurchasedProduct pp1 = buildPurchasedProduct(prod1, sl1);
        PurchasedProduct pp2 = buildPurchasedProduct(prod2, sl2);

        ProductCart pc1 = buildProductCart(prod2, sc1);

        sl1.setPurchasedProductList(Arrays.asList(pp1));
        sl2.setPurchasedProductList(Arrays.asList(pp2));

        sc1.setProductCartList(Arrays.asList(pc1));

        customer1.setShoppingList(sl1);
        customer1.setShoppingCart(sc1);
        customer2.setShoppingList(sl2);
        customer2.setShoppingCart(sc2);

        adminDao.addEntity(user1);
        adminDao.addEntity(user2);
        manufacturerDao.addEntity(manufacturer1);
        manufacturerDao.addEntity(manufacturer2);
        localeDao.addEntity(locale1);
        localeDao.addEntity(locale2);
        productDao.addEntity(prod1);
        productDao.addEntity(prod2);
        translationDao.addEntity(lc1);
        translationDao.addEntity(lc2);
        translationDao.addEntity(lc3);
        translationDao.addEntity(lc4);
        purchasedProductDao.addEntity(pp1);
        purchasedProductDao.addEntity(pp2);
        productCartDao.addEntity(pc1);
        shoppingCartDao.addEntity(sc1);
        shoppingCartDao.addEntity(sc2);
        shoppingListDao.addEntity(sl1);
        shoppingListDao.addEntity(sl2);
        customerDao.addEntity(customer1);
        customerDao.addEntity(customer2);
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
                                                   Locale locale, Product product, String currency, float price)
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

    private ProductCart buildProductCart(Product p, ShoppingCart sc)
    {
        ProductCart pc = ModelFactory.productCart();
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

}
