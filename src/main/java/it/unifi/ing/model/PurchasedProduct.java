package it.unifi.ing.model;

import javax.persistence.*;

@Entity
@Table(name = "purchased_products")
public class PurchasedProduct extends BaseEntity {

    @ManyToOne()
    @JoinColumn(name = "product_sid")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "shopping_list_sid")
    private ShoppingList shoppingList;

    public PurchasedProduct() {}
    public PurchasedProduct(String uuid) { super(uuid); }

    public Product getProduct() { return this.product; }
    public ShoppingList getShoppingList() { return this.shoppingList; }

    public void setProduct(Product product) { this.product = product; }
    public void setShoppingList(ShoppingList shoppingList) { this.shoppingList = shoppingList; }
}
