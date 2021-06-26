package it.unifi.ing.translation;

import it.unifi.ing.model.TranslatableItem;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

import javax.persistence.*;

@Indexed
@Entity
@Table(name = "localized_textual_items")
public class LocalizedTextualItem extends AbstractLocalizedItem {

    @ManyToOne
    @JoinColumn(name = "translatable_item_sid")
    private TranslatableItem translatableItem;

    @ManyToOne
    @JoinColumn(name = "localized_field_sid")
    private LocalizedField localizedField;

    @Column(name = "text", columnDefinition="varchar(4000)")
    @Field(termVector = TermVector.YES)
    private String text;

    public LocalizedTextualItem() {}
    public LocalizedTextualItem(String uuid) { super(uuid); }

    public TranslatableItem getTranslatableItem() { return this.translatableItem; }
    public LocalizedField getLocalizedField() { return this.localizedField; }
    public String getText() { return this.text; }

    public void setTranslatableItem(TranslatableItem translatableItem)
    {
        this.translatableItem = translatableItem;
    }
    public void setLocalizedField(LocalizedField localizedField) { this.localizedField = localizedField; }
    public void setText(String text) { this.text = text; }
}
