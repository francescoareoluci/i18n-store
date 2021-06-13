package it.unifi.ing.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "shopping_lists")
public class ShoppingList extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "customer_sid")
    private Customer customer;

    @OneToMany(mappedBy = "shoppingList")
    private List<PurchasedProduct> purchasedProductList;

    public ShoppingList() {}
    public ShoppingList(String uuid)  { super(uuid); }

    public Customer getCustomer() { return this.customer; }
    public List<PurchasedProduct> getPurchasedProductList() { return this.purchasedProductList; }

    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setPurchasedProductList(List<PurchasedProduct> purchasedProductList)
    {
        this.purchasedProductList = purchasedProductList;
    }
}
