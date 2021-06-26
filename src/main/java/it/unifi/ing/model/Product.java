package it.unifi.ing.model;

import it.unifi.ing.translation.LocalizedCurrencyItem;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.util.List;

@Indexed
@Entity
@Table(name = "products")
public class Product extends TranslatableItem {

    @ManyToOne
    private Manufacturer prodManufacturer;
    @ManyToOne
    private Admin prodAdministrator;

    /*
    @IndexedEmbedded
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<LocalizedTextualItem> localizedTextualItemList;
    */

    @IndexedEmbedded
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<LocalizedCurrencyItem> localizedCurrencyItemList;

    public Product() {}
    public Product(String uuid) { super(uuid); }

    public Manufacturer getProdManufacturer() { return this.prodManufacturer; }
    public Admin getProdAdministrator() { return this.prodAdministrator; }
    //public List<LocalizedTextualItem> getLocalizedTextualItemList() { return this.localizedTextualItemList; }
    public List<LocalizedCurrencyItem> getLocalizedCurrencyItemList() { return this.localizedCurrencyItemList; }

    public void setProdManufacturer(Manufacturer prodManufacturer)
    {
        this.prodManufacturer = prodManufacturer;
    }
    public void setProdAdministrator(Admin prodAdministrator)
    {
        this.prodAdministrator = prodAdministrator;
    }
    /*
    public void setLocalizedTextualItemList(List<LocalizedTextualItem> localizedTextualItemList)
    {
        this.localizedTextualItemList = localizedTextualItemList;
    }
    */

    public void setLocalizedCurrencyItemList(List<LocalizedCurrencyItem> localizedCurrencyItemList)
    {
        this.localizedCurrencyItemList = localizedCurrencyItemList;
    }

}
