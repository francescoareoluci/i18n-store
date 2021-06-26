package it.unifi.ing.translationModel;

import it.unifi.ing.model.BaseEntity;
import it.unifi.ing.model.Currency;
import it.unifi.ing.model.Locale;
import it.unifi.ing.model.Product;

import javax.persistence.*;

@Entity
@Table(name = "localized_currency_items")
public class LocalizedCurrencyItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_sid")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "locale_sid")
    private Locale locale;

    @ManyToOne
    @JoinColumn(name = "currency_sid")
    private Currency currency;

    @Column(name = "price")
    private float price;

    public LocalizedCurrencyItem() {}
    public LocalizedCurrencyItem(String uuid) { super(uuid); }

    public Product getProduct() { return this.product; }
    public Locale getLocale() { return this.locale; }
    public Currency getCurrency() { return this.currency; }
    public float getPrice() { return this.price; }

    public void setProduct(Product product) { this.product = product; }
    public void setLocale(Locale locale) { this.locale = locale; }
    public void setCurrency(Currency currency) { this.currency = currency; }
    public void setPrice(float price) { this.price = price; }
}
