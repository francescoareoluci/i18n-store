package it.unifi.ing.dto;

public abstract class AbstractLocalizedItemDto extends BaseDto {

    private String fieldType;
    private String locale;
    private String country;

    public AbstractLocalizedItemDto() {}

    public String getFieldType() { return this.fieldType; }
    public String getLocale() { return this.locale; }
    public String getCountry() { return this.country; }

    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public void setLocale(String locale) { this.locale = locale; }
    public void setCountry(String country) { this.country = country; }
}
