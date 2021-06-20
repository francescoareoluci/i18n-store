package it.unifi.ing.dto;

public class CurrencyDto extends BaseDto {

    private String currency;

    public CurrencyDto() {}

    public String getCurrency() { return this.currency; }

    public void setCurrency(String currency) { this.currency = currency; }
}
