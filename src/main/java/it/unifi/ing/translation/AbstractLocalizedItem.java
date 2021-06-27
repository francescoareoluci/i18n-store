package it.unifi.ing.translation;

import it.unifi.ing.model.BaseEntity;
import it.unifi.ing.model.Locale;
import it.unifi.ing.model.TranslatableItem;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.TermVector;

import javax.persistence.*;

@Entity
@Table(name = "localized_items")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractLocalizedItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "locale_sid")
    private Locale locale;

    @ManyToOne
    @JoinColumn(name = "translatable_item_sid")
    private TranslatableItem translatableItem;

    @ManyToOne
    @JoinColumn(name = "localized_field_sid")
    private LocalizedField localizedField;

    public AbstractLocalizedItem() {}
    public AbstractLocalizedItem(String uuid) { super(uuid); }

    public Locale getLocale() { return this.locale; }
    public TranslatableItem getTranslatableItem() { return this.translatableItem; }
    public LocalizedField getLocalizedField() { return this.localizedField; }

    /**
     * @implNote This is needed in order to search for Product.AbstractLocalizedItem.text.
     *           This method will be overrided by LocalizedTextualItem only
      */
    @Field(termVector = TermVector.YES)
    @Transient
    public String getText() { return null; }

    public void setLocale(Locale locale) { this.locale = locale; }
    public void setTranslatableItem(TranslatableItem translatableItem)
    {
        this.translatableItem = translatableItem;
    }
    public void setLocalizedField(LocalizedField localizedField) { this.localizedField = localizedField; }
}
