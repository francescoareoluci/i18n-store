package it.unifi.ing.translationModel;

import it.unifi.ing.model.BaseEntity;
import it.unifi.ing.model.Locale;
import it.unifi.ing.model.TranslatableItem;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

import javax.persistence.*;

@Indexed
@Entity
@Table(name = "localized_textual_items")
public class LocalizedTextualItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "product_sid")
    private TranslatableItem translatableItem;

    @ManyToOne
    @JoinColumn(name = "locale_sid")
    private Locale locale;

    @ManyToOne
    @JoinColumn(name = "localized_field_sid")
    private LocalizedField localizedField;

    @Column(name = "text", columnDefinition="varchar(4000)")
    @Field(termVector = TermVector.YES)
    private String text;

    public LocalizedTextualItem() {}
    public LocalizedTextualItem(String uuid) { super(uuid); }

    public TranslatableItem getTranslatableItem() { return this.translatableItem; }
    public Locale getLocale() { return this.locale; }
    public LocalizedField getLocalizedField() { return this.localizedField; }
    public String getText() { return this.text; }

    public void setTranslatableItem(TranslatableItem translatableItem)
    {
        this.translatableItem = translatableItem;
    }
    public void setLocale(Locale locale) { this.locale = locale; }
    public void setLocalizedField(LocalizedField localizedField) { this.localizedField = localizedField; }
    public void setText(String text) { this.text = text; }
}
