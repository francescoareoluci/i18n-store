package it.unifi.ing.dto;

public class LocalizedProductDto extends BaseDto {

    private String name;
    private String description;
    private String category;
    private String currency;
    private String price;
    private String locale;
    private String country;

    public LocalizedProductDto() {}

    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public String getCategory() { return this.category; }
    public String getCurrency() { return this.currency; }
    public String getPrice() { return this.price; }
    public String getLocale() { return this.locale; }
    public String getCountry() { return this.country; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setPrice(String price) { this.price = price; }
    public void setLocale(String locale) { this.locale = locale; }
    public void setCountry(String country) { this.country = country; }

}
