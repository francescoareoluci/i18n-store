package it.unifi.ing.model;

import javax.persistence.*;

@Entity
@Table(name = "cart_products")
public class ProductCart extends BaseEntity {

    @ManyToOne()
    @JoinColumn(name = "product_sid")
    private Product product;

    @ManyToOne()
    @JoinColumn(name = "cart_sid")
    private ShoppingCart shoppingCart;

    public ProductCart() {}
    public ProductCart(String uuid) { super(uuid); }

    public Product getProduct() { return this.product; }
    public ShoppingCart getShoppingCart() { return this.shoppingCart; }

    public void setProduct(Product product) { this.product = product; }
    public void setShoppingCart(ShoppingCart shoppingCart) { this.shoppingCart = shoppingCart; }
}
