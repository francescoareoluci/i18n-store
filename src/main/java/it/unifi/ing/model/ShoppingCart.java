package it.unifi.ing.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCart extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "customer_sid")
    private Customer customer;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<ProductCart> productCartList;

    public ShoppingCart() {}
    public ShoppingCart(String uuid) { super(uuid); }

    public Customer getCustomer() { return this.customer; }
    public List<ProductCart> getProductCartList() { return this.productCartList; }

    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setProductCartList(List<ProductCart> productCartList) { this.productCartList = productCartList; }
}
