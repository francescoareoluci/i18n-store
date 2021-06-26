package it.unifi.ing.dto;

public class LocalizedCurrencyItemDto extends BaseDto {

    private String currency;
    private float price;
    private String locale;
    private String country;

    public LocalizedCurrencyItemDto() {}

    public String getCurrency() { return this.currency; }
    public float getPrice() { return this.price; }
    public String getLocale() { return this.locale; }
    public String getCountry() { return this.country; }

    public void setCurrency(String currency) { this.currency = currency; }
    public void setPrice(float price) { this.price = price; }
    public void setLocale(String locale) { this.locale = locale; }
    public void setCountry(String country) { this.country = country; }
}
