package it.unifi.ing.model;

import it.unifi.ing.translationModel.LocalizedTextualItem;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "translatable_items")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class TranslatableItem extends BaseEntity
{
    @IndexedEmbedded
    @OneToMany(mappedBy = "translatableItem", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<LocalizedTextualItem> localizedTextualItemList;

    public TranslatableItem() {}
    public TranslatableItem(String uuid) { super(uuid); }

    public List<LocalizedTextualItem> getLocalizedTextualItemList()
    {
        return this.localizedTextualItemList;
    }

    public void setLocalizedTextualItemList(List<LocalizedTextualItem> localizedTextualItemList)
    {
        this.localizedTextualItemList = localizedTextualItemList;
    }
}
