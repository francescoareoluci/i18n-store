package it.unifi.ing.model;

import javax.persistence.*;

@Entity
@Table(name = "customers")
public class Customer extends AbstractUser {

    @ManyToOne
    @JoinColumn(name = "locale_sid")
    private Locale userLocale;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private ShoppingCart shoppingCart;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private ShoppingList shoppingList;

    public Customer() {};

    public Customer(String uuid) { super(uuid); }

    public Locale getUserLocale() { return this.userLocale; }
    public ShoppingCart getShoppingCart() { return this.shoppingCart; }
    public ShoppingList getShoppingList() { return this.shoppingList; }

    public void setUserLocale(Locale userLocale) { this.userLocale = userLocale; }
    public void setShoppingCart(ShoppingCart shoppingCart) { this.shoppingCart = shoppingCart; }
    public void setShoppingList(ShoppingList shoppingList) { this.shoppingList = shoppingList; }
}
