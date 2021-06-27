package it.unifi.ing.model;

import it.unifi.ing.translation.LocalizedItem;
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
    private List<LocalizedItem> localizedItemList;

    public TranslatableItem() {}
    public TranslatableItem(String uuid) { super(uuid); }

    public List<LocalizedItem> getLocalizedItemList()
    {
        return this.localizedItemList;
    }

    public void setLocalizedItemList(List<LocalizedItem> localizedItemList)
    {
        this.localizedItemList = localizedItemList;
    }
}
