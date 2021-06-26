package it.unifi.ing.dto;

public class LocalizedCurrencyItemDto extends AbstractLocalizedItemDto {

    private String currency;
    private float price;

    public LocalizedCurrencyItemDto() {}

    public String getCurrency() { return this.currency; }
    public float getPrice() { return this.price; }

    public void setCurrency(String currency) { this.currency = currency; }
    public void setPrice(float price) { this.price = price; }
}
