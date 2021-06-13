package it.unifi.ing.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "locales")
public class Locale extends BaseEntity {

    private String countryCode;
    private String languageCode;

    public Locale() {}
    public Locale(String uuid) { super(uuid); }

    public String getCountryCode() { return this.countryCode; }
    public String getLanguageCode() { return this.languageCode; }

    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }

}
