package it.unifi.ing.translation;

import it.unifi.ing.model.BaseEntity;
import it.unifi.ing.model.Locale;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractLocalizedItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "locale_sid")
    private Locale locale;

    public AbstractLocalizedItem() {}
    public AbstractLocalizedItem(String uuid) { super(uuid); }

    public Locale getLocale() { return this.locale; }

    public void setLocale(Locale locale) { this.locale = locale; }
}
