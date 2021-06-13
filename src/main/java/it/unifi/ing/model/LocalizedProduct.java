package it.unifi.ing.model;

import javax.persistence.*;

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
    private String productName;
    @Column(name = "description")
    private String productDescription;
    @Column(name = "category")
    private String productCategory;
    @Column(name = "currency")
    private String productCurrency;
    @Column(name = "price")
    private float productPrice;

    public LocalizedProduct() {}
    public LocalizedProduct(String uuid) { super(uuid); }

    public Locale getLocale() { return this.locale ;}
    public Product getProduct() { return this.product; }
    public String getProductName() { return this.productName; }
    public String getProductDescription() { return this.productDescription; }
    public String getProductCategory() { return this.productCategory; }
    public String getProductCurrency() { return this.productCurrency; }
    public float getProductPrice() { return this.productPrice; }

    public void setLocale(Locale locale) { this.locale = locale; }
    public void setProduct(Product product) { this.product = product; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setProductDescription(String description) { this.productDescription = productDescription; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }
    public void setProductCurrency(String currency) { this. productCurrency = productCurrency; }
    public void setProductPrice(float price) { this.productPrice = productPrice; }

}
