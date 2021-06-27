package it.unifi.ing.model;

import it.unifi.ing.translation.AbstractLocalizedItem;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "translatable_items")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class TranslatableItem extends BaseEntity
{
    @IndexedEmbedded
    @OneToMany(mappedBy = "translatableItem", cascade = CascadeType.ALL)
    private List<AbstractLocalizedItem> abstractLocalizedItemList;

    public TranslatableItem() {}
    public TranslatableItem(String uuid) { super(uuid); }

    public List<AbstractLocalizedItem> getAbstractLocalizedItemList()
    {
        return this.abstractLocalizedItemList;
    }

    public void setAbstractLocalizedItemList(List<AbstractLocalizedItem> abstractLocalizedItemList)
    {
        this.abstractLocalizedItemList = abstractLocalizedItemList;
    }
}
