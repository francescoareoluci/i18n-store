package it.unifi.ing.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @ManyToOne
    private Manufacturer prodManufacturer;
    @ManyToOne
    private Admin prodAdministrator;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<LocalizedProduct> localizedProductList;

    public Product() {}
    public Product(String uuid) { super(uuid); }

    public Manufacturer getProdManufacturer() { return this.prodManufacturer; }
    public Admin getProdAdministrator() { return this.prodAdministrator; }
    public List<LocalizedProduct> getLocalizedProductList() { return this.localizedProductList; }

    public void setProdManufacturer(Manufacturer prodManufacturer)
    {
        this.prodManufacturer = prodManufacturer;
    }
    public void setProdAdministrator(Admin prodAdministrator)
    {
        this.prodAdministrator = prodAdministrator;
    }
    public void setLocalizedProductList(List<LocalizedProduct> localizedProductList)
    {
        this.localizedProductList = localizedProductList;
    }

}
