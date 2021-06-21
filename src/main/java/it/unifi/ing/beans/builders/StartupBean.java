package it.unifi.ing.beans.builders;

import it.unifi.ing.dao.*;
import it.unifi.ing.model.*;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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

    @PersistenceContext
    private EntityManager entityManager;

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

        Locale locale1 = buildLocale("it", "IT");
        Locale locale2 = buildLocale("en", "US");

        Currency currency1 = buildCurrency("$");
        Currency currency2 = buildCurrency("€");

        Customer customer1 = buildCustomer("John", "White", "john.white@example.com", "pass3", locale2);
        Customer customer2 = buildCustomer("Carla", "Verdi", "carla.verdi@example.com", "pass4", locale1);

        ShoppingCart sc1 = buildShoppingCart(customer1);
        ShoppingCart sc2 = buildShoppingCart(customer2);

        ShoppingList sl1 = buildShoppingList(customer1);
        ShoppingList sl2 = buildShoppingList(customer2);

        Product prod1 = buildProduct(user1, manufacturer1);
        Product prod2 = buildProduct(user1, manufacturer2);
        Product prod3 = buildProduct(user2, manufacturer3);
        Product prod4 = buildProduct(user2, manufacturer4);

        LocalizedProduct lc1 = buildLocalizedProduct("Sony Alpha a7II", "The Sony Alpha a7II Mirrorless " +
                "Digital Camera is the world's first full-frame camera with 5-axis image " +
                "stabilization and provides camera shake compensation for " +
                "wide-ranging mountable lenses.", "electronic", locale2, prod1, currency1, (float)1598.00);
        LocalizedProduct lc2 = buildLocalizedProduct("Sony Alpha a7II", "Struttura completa, " +
                "dimensioni del palmo. Perfezione per tutti. Stabilità per tutti.La qualità" +
                " delle immagini mozzafiato incontra la libertà di ripresa senza pari nel 7 II, " +
                "la prima fotocamera full-frame al mondo con stabilizzazione " +
                "dell'immagine a 5 assi.", "elettronica", locale1, prod1, currency2, (float)1.499);
        LocalizedProduct lc3 = buildLocalizedProduct("Samsung Smartphone Galaxy S21", "Pro Grade " +
                "Camera: Zoom in close, take photos and videos like a pro, " +
                "and capture incredible share-ready moments " +
                "with our easy-to-use, multi-lens camera", "electronic", locale2, prod2, currency1, (float)799.99);
        LocalizedProduct lc4 = buildLocalizedProduct("Samsung Smartphone Galaxy S21", "Fotocamera " +
                "con teleobiettivo 64MP, Fotocamera frontale 12MP, Fotocamera grandangolare 12MP: " +
                "tutta la tecnologia che ti occorre per i migliori scatti con il tuo smartphone ",
                "elettronica", locale1, prod2, currency2, (float)879.99);
        LocalizedProduct lc5 = buildLocalizedProduct("Netgear R7000 Router WiFi Nighthawk", "Fast " +
                        "wifi performance: Get up to 2000 square feet wireless coverage" +
                        " with AC2300 speed (Dual band up to 600 + 1625 Mbps). - Recommended for up to " +
                        "35 devices: Reliably stream videos, play games, surf the " +
                        "internet, and connect smart home devices. - Wired ethernet ports: plug in " +
                        "computers, game consoles, streaming players, and other " +
                        "nearby wired devices with 4 x 1 gigabit ethernet ports", "electronic", locale2,
                        prod3, currency1, (float)149.99);
        LocalizedProduct lc6 = buildLocalizedProduct("Netgear R7000 Router WiFi Nighthawk", "Copertura " +
                        "Wireless fino a 110 metri quadri con velocità AC2300 " +
                        "(Dual band fino a 600 + 1625 Mbps). - Raccomandato fino a 35 dispositivi:" +
                        "Guarda video, gioca online e gestisci i dispositivi smart home. -" +
                        "4 x 1 porte gigabit ethernet: connetti computer, console, lettori streaming e" +
                        "ogni altro dispositivo wired.", "elettronica", locale1,
                        prod3, currency2, (float)149.99);
        LocalizedProduct lc7 = buildLocalizedProduct("Blue Yeti USB Mic", "Custom three-capsule" +
                "array: produces clear, powerful, broadcast-quality sound for YouTube, " +
                "game streaming, podcasting, Skype calls and music. - Four pickup patterns: cardioid, " +
                "Omni, bidirectional, and stereo pickup patterns offer " +
                "incredible flexibility, allowing you to record in ways " +
                "that would normally require multiple microphones", "audio", locale2, prod4, currency1,
                (float)129.99);
        LocalizedProduct lc8 = buildLocalizedProduct("Blue Yeti USB Mic", "Matrice a tre " +
                        "capsule personalizzata: produce audio nitido, potente e di " +
                        "qualità professionale per YouTube, streaming di gioco, registrazione di podcast, " +
                        "chiamate Skype e musica. - Quattro modalità di rilevamento: le modalità " +
                        "cardioide, omnidirezionale, bidirezionale e stereo " +
                        "offrono un'incredibile versatilità e ti consentono di effettuare registrazioni " +
                        "che normalmente richiederebbero più microfoni", "audio", locale1, prod4, currency2,
                (float)139.99);

        prod1.setLocalizedProductList(Arrays.asList(lc1, lc2));
        prod2.setLocalizedProductList(Arrays.asList(lc3, lc4));
        prod3.setLocalizedProductList(Arrays.asList(lc5, lc6));
        prod4.setLocalizedProductList(Arrays.asList(lc7, lc8));

        PurchasedProduct pp1 = buildPurchasedProduct(prod1, sl1);
        PurchasedProduct pp2 = buildPurchasedProduct(prod2, sl2);

        CartProduct pc1 = buildCartProduct(prod2, sc1);

        sl1.setPurchasedProductList(Arrays.asList(pp1));
        sl2.setPurchasedProductList(Arrays.asList(pp2));

        sc1.setCartProductList(Arrays.asList(pc1));

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
        localeDao.addEntity(locale1);
        localeDao.addEntity(locale2);
        currencyDao.addEntity(currency1);
        currencyDao.addEntity(currency2);
        productDao.addEntity(prod1);
        productDao.addEntity(prod2);
        productDao.addEntity(prod3);
        productDao.addEntity(prod4);
        shoppingCartDao.addEntity(sc1);
        shoppingCartDao.addEntity(sc2);
        shoppingListDao.addEntity(sl1);
        shoppingListDao.addEntity(sl2);
        customerDao.addEntity(customer1);
        customerDao.addEntity(customer2);

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

}
