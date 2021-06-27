package it.unifi.ing.translation;

import javax.persistence.*;

@Entity
@Table(name = "localized_textual_items")
public class LocalizedTextualItem extends AbstractLocalizedItem {

    @Column(name = "text", columnDefinition="varchar(4000)")
    private String text;

    public LocalizedTextualItem() {}
    public LocalizedTextualItem(String uuid) { super(uuid); }

    @Override
    public String getText() { return this.text; }

    public void setText(String text) { this.text = text; }
}
