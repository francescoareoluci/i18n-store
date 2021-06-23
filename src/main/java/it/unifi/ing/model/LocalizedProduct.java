package it.unifi.ing.model;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TermVector;

import javax.persistence.*;

@Indexed
@Entity
@Table(name = "translations")
public class LocalizedProduct extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "locale_sid")
    private Locale locale;

    @ManyToOne
    @JoinColumn(name = "product_sid")
    private Product product;

    @Column(name = "name")
    @Field(termVector = TermVector.NO, store = Store.YES)
    private String name;
    @Column(name = "description", columnDefinition="varchar(4000)")
    @Field(termVector = TermVector.YES)
    private String description;
    @Column(name = "category")
    private String category;
    @Column(name = "price")
    private float price;

    @ManyToOne
    @JoinColumn(name = "currency_sid")
    private Currency currency;


    public LocalizedProduct() {}
    public LocalizedProduct(String uuid) { super(uuid); }

    public Locale getLocale() { return this.locale ;}
    public Product getProduct() { return this.product; }
    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public String getCategory() { return this.category; }
    public Currency getCurrency() { return this.currency; }
    public float getPrice() { return this.price; }

    public void setLocale(Locale locale) { this.locale = locale; }
    public void setProduct(Product product) { this.product = product; }
    public void setName(String productName) { this.name = productName; }
    public void setDescription(String productDescription) { this.description = productDescription; }
    public void setCategory(String productCategory) { this.category = productCategory; }
    public void setCurrency(Currency productCurrency) { this.currency = productCurrency; }
    public void setPrice(float productPrice) { this.price = productPrice; }

}
