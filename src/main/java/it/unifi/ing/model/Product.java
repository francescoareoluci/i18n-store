package it.unifi.ing.model;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;

@Indexed
@Entity
@Table(name = "products")
public class Product extends TranslatableItem {

    @ManyToOne
    @JoinColumn(name = "manufacturer_sid")
    private Manufacturer prodManufacturer;
    @ManyToOne
    @JoinColumn(name = "administrator_sid")
    private Admin prodAdministrator;

    public Product() {}
    public Product(String uuid) { super(uuid); }

    public Manufacturer getProdManufacturer() { return this.prodManufacturer; }
    public Admin getProdAdministrator() { return this.prodAdministrator; }

    public void setProdManufacturer(Manufacturer prodManufacturer)
    {
        this.prodManufacturer = prodManufacturer;
    }
    public void setProdAdministrator(Admin prodAdministrator)
    {
        this.prodAdministrator = prodAdministrator;
    }

}
