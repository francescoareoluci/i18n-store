package it.unifi.ing.dto;

public abstract class AbstractLocalizedItemDto extends BaseDto {

    private String locale;
    private String country;

    public AbstractLocalizedItemDto() {}

    public String getLocale() { return this.locale; }
    public String getCountry() { return this.country; }

    public void setLocale(String locale) { this.locale = locale; }
    public void setCountry(String country) { this.country = country; }
}
