package it.unifi.ing.beans.builders;

import it.unifi.ing.dao.*;
import it.unifi.ing.model.*;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
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

        Currency currency1 = buildCurrency("$");
        Currency currency2 = buildCurrency("€");

        Customer customer1 = buildCustomer("John", "White", "john.white@example.com", "pass3", locale2);
        Customer customer2 = buildCustomer("Carla", "Verdi", "carla.verdi@example.com", "pass4", locale1);

        ShoppingCart sc1 = buildShoppingCart(customer1);
        ShoppingCart sc2 = buildShoppingCart(customer2);

        ShoppingList sl1 = buildShoppingList(customer1);
        ShoppingList sl2 = buildShoppingList(customer2);

        Product prod1 = buildProduct(user1, manufacturer2);
        Product prod2 = buildProduct(user1, manufacturer1);
        Product prod3 = buildProduct(user2, manufacturer3);
        Product prod4 = buildProduct(user2, manufacturer4);
        Product prod5 = buildProduct(user1, manufacturer2);
        Product prod6 = buildProduct(user2, manufacturer5);
        Product prod7 = buildProduct(user1, manufacturer6);
        Product prod8 = buildProduct(user1, manufacturer7);
        Product prod9 = buildProduct(user2, manufacturer8);

        LocalizedProduct lc1 = buildLocalizedProduct("Sony Alpha a7II", "The Sony Alpha a7II Mirrorless " +
                "Digital Camera is the world's first full-frame camera with 5-axis image " +
                "stabilization and provides camera shake compensation for " +
                "wide-ranging mountable lenses.", "camera", locale2, prod1, currency1, (float)1598.00);
        LocalizedProduct lc2 = buildLocalizedProduct("Sony Alpha a7II", "Struttura completa, " +
                "dimensioni del palmo. Perfezione per tutti. Stabilità per tutti.La qualità" +
                " delle immagini mozzafiato incontra la libertà di ripresa senza pari nel 7 II, " +
                "la prima fotocamera full-frame al mondo con stabilizzazione " +
                "dell'immagine a 5 assi.", "fotocamera", locale1, prod1, currency2, (float)1.499);
        LocalizedProduct lc3 = buildLocalizedProduct("Samsung Smartphone Galaxy S21", "Pro Grade " +
                "Camera: Zoom in close, take photos and videos like a pro, " +
                "and capture incredible share-ready moments " +
                "with our easy-to-use, multi-lens camera", "smartphone", locale2, prod2, currency1, (float)799.99);
        LocalizedProduct lc4 = buildLocalizedProduct("Samsung Smartphone Galaxy S21", "Fotocamera " +
                "con teleobiettivo 64MP, Fotocamera frontale 12MP, Fotocamera grandangolare 12MP: " +
                "tutta la tecnologia che ti occorre per i migliori scatti con il tuo smartphone ",
                "smartphone", locale1, prod2, currency2, (float)879.99);
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
        LocalizedProduct lc9 = buildLocalizedProduct("Sony SEL1670Z", "A compact ZEISS zoom " +
                "for all occasions: This lightweight mid-range zoom combines renowned ZEISS optical performance " +
                "with a constant F4 maximum aperture for consistently fine " +
                "performance throughout the zoom range. - Built-in Optical SteadyShot image " +
                "stabilization compensates for camera shake that can blur images " +
                "when shooting handheld. Sharp, clear night scenes or indoor shots " +
                "in dim lighting can be captured without the need to boost ISO " +
                "sensitivity and risk increased noise.", "camera", locale2, prod5, currency1, (float)651.94);
        LocalizedProduct lc10 = buildLocalizedProduct("Sony SEL-1670Z", "La qualità Zeiss in " +
                        "uno zoom compatto e versatile: Abbiamo inserito l'ottica Zeiss migliore della " +
                        "categoria, in uno zoom " +
                        "medio compatto e pratico, adatto a un'ampia scelta di applicazioni. " +
                        "La gamma di zoom da 16 mm a 70 mm è ideale per la maggior parte " +
                        "delle situazioni di scatto, rendendo questo obiettivo " +
                        "la scelta ideale per foto e riprese di tutti i giorni. - La stabilizzazione " +
                        "dell'immagine con SteadyShot ottico integrato compensa le vibrazioni della fotocamera, " +
                        "causa di sfocature quando scatti senza cavalletto. " +
                        "Gli scatti nitidi e le scene in interni scarsamente " +
                        "illuminati sono facili da ottenere, senza aumentare la " +
                        "sensibilità ISO e rischiare così di generare disturbi.", "fotocamera", locale1,
                        prod5, currency2, (float)758.00);
        LocalizedProduct lc11 = buildLocalizedProduct("Bose QuietComfort 35 II", "What happens " +
                "when you clear away the noisy distractions of the world? Concentration " +
                "goes to the next level. You get deeper into your music, your work, " +
                "or whatever you want to focus on. That’s the power of Bose QuietComfort 35 " +
                "wireless headphones II. Put them on and get closer to what you’re most passionate about. " +
                "And that’s just the beginning. QuietComfort 35 wireless headphones II " +
                "are now enabled with Bose AR — an innovative, audio-only take on augmented reality. " +
                "Embedded in your headphones is a multi-directional motion sensor. " +
                "One that Bose AR can utilize to provide contextual audio based on where you are. ",
                "audio", locale2, prod6, currency1, (float)299.99);
        LocalizedProduct lc12 = buildLocalizedProduct("Bose QuietComfort 35 II", "Cosa succede " +
                "quando elimini le distrazioni intorno a te? la concentrazione aumenta. " +
                "Puoi dedicarti alla musica, al lavoro, a ciò che vuoi. Tutto questo grazie alle " +
                "cuffie bose quietcomfort 35 ii wireless. Indossale e vivi le tue passioni. E " +
                "questo non è che l'inizio. Le cuffie quietcomfort 35 ii wireless sono ora compatibili " +
                "con bose ar, un'innovativa versione di realtà aumentata solo audio. Le cuffie sono dotate " +
                "di un sensore di movimento multidirezionale che bose ar può utilizzare per fornire audio " +
                "contestuale in base al luogo in cui ti trovi.", "audio", locale1, prod6,
                currency2, (float)299.99);
        LocalizedProduct lc13 = buildLocalizedProduct("Sennheiser HD 599 SE", "The Sennheiser " +
                "HD 599 Special Edition pushes performance barriers. It’s a premium headphone" +
                " for those seeking timeless design and build quality along with exceptional sound. " +
                "Powered by Sennheiser proprietary transducer technology and featuring the 'Ergonomic " +
                "Acoustic Refinement' (E.A.R) design, the HD 599 Special Edition represents a step into the " +
                "world of audiophile sound reproduction. This open back, around ear headphone delivers a " +
                "natural tonal balance featuring exceptional detail and definition with " +
                "outstanding spatial performance.", "audio", locale2, prod7, currency1, (float)199.99);
        LocalizedProduct lc14 = buildLocalizedProduct("Sennheiser HD 599 SE", "La best in class, " +
                "rimasterizzata: Sennheiser HD 599 Special Edition infrange le barriere delle prestazioni. " +
                "È una cuffia premium per coloro che ricercano un suono sofisticato, " +
                "design e qualità costruttiva. Con la potenza dei trasduttori di tecnologia " +
                "Sennheiser e con design “Ergonomic Acoustic Refinement” (E.A.R), la HD 599 Special " +
                "Edition rappresenta il primo approccio nel mondo della riproduzione da audiofili. " +
                "Questa cuffia aperta circumaurale garantisce un bilanciamento naturale dei toni, un " +
                "dettaglio eccezionale ed una performance spaziale senza precedenti.", "audio", locale1, prod7,
                currency2, (float)199.99);
        LocalizedProduct lc15 = buildLocalizedProduct("Tommy Hilfiger Men's Core Logo T-Shirt",
                "The Core Logo T-Shirt from Tommy Hilfiger comes in Jet Black colour, featuring a crew " +
                        "neck and short sleeves. Being 100% Organic - excluding trims, this t-shirt for men sports " +
                        "large embroidered logo on front and small logo on left sleeve. - " +
                        "100% Cotton - Machine Wash",
                "fashion", locale2, prod8, currency1, (float)34.99);
        LocalizedProduct lc16 = buildLocalizedProduct("Tommy Hilfiger Core Tommy Logo Tee Maglietta Uomo",
                "La t-shirt Core Logo di Tommy Hilfiger è disponibile in colore Jet Black, " +
                        "con girocollo e maniche corte. Essendo 100% organica - escluse le finiture, " +
                        "questa t-shirt da uomo sfoggia un grande logo ricamato sul davanti e un " +
                        "piccolo logo sulla manica sinistra. - 100% Cotone - Lavabile " +
                        "in lavatrice", "moda", locale1, prod8, currency2, (float)29.99);
        LocalizedProduct lc17 = buildLocalizedProduct("Lacoste Men's Short Sleeve",
                "Symbol of relaxed elegance since 1933, the Lacoste brand, backed by its " +
                        "authentic roots in sports, offers a unique and original universe " +
                        "through the medium of a large range of products for men, women and children. " +
                        "In the 114 countries where the brand is present with a selective " +
                        "distribution network, every second two Lacoste products are sold: " +
                        "apparel including the famous l.12.12 polo, leather goods, " +
                        "fragrances, footwear, eyewear, watches.", "fashion", locale2, prod9,
                        currency1, (float)49.99);
        LocalizedProduct lc18 = buildLocalizedProduct("Lacoste Polo T-Shirt Uomo", "Simbolo di " +
                "eleganza dal 1933, il marchio Lacoste, forte delle sue autentiche radici nello sport, offre" +
                "un universo unico e originale attraverso un'ampia gamma di prodotti per uomo, donna e bambino. " +
                "Nei 114 paesi in cui il marchio è presente con una rete di distribuzione selettiva, " +
                "ogni secondo vengono venduti due prodotti Lacoste: abbigliamento tra cui la famosa polo l.12.12, " +
                "pelletteria, profumi, calzature, occhiali, orologi.","moda", locale1, prod9,
                currency2, (float)49.99);


        prod1.setLocalizedProductList(Arrays.asList(lc1, lc2));
        prod2.setLocalizedProductList(Arrays.asList(lc3, lc4));
        prod3.setLocalizedProductList(Arrays.asList(lc5, lc6));
        prod4.setLocalizedProductList(Arrays.asList(lc7, lc8));
        prod5.setLocalizedProductList(Arrays.asList(lc9, lc10));
        prod6.setLocalizedProductList(Arrays.asList(lc11, lc12));
        prod7.setLocalizedProductList(Arrays.asList(lc13, lc14));
        prod8.setLocalizedProductList(Arrays.asList(lc15, lc16));
        prod9.setLocalizedProductList(Arrays.asList(lc17, lc18));

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
        manufacturerDao.addEntity(manufacturer5);
        manufacturerDao.addEntity(manufacturer6);
        manufacturerDao.addEntity(manufacturer7);
        manufacturerDao.addEntity(manufacturer8);
        localeDao.addEntity(locale1);
        localeDao.addEntity(locale2);
        currencyDao.addEntity(currency1);
        currencyDao.addEntity(currency2);
        productDao.addEntity(prod1);
        productDao.addEntity(prod2);
        productDao.addEntity(prod3);
        productDao.addEntity(prod4);
        productDao.addEntity(prod5);
        productDao.addEntity(prod6);
        productDao.addEntity(prod7);
        productDao.addEntity(prod8);
        productDao.addEntity(prod9);
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

}
