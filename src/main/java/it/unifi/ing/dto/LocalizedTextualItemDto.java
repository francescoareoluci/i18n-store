package it.unifi.ing.dto;

public class LocalizedTextualItemDto extends BaseDto {

    private String fieldType;
    private String text;
    private String locale;
    private String country;

    public LocalizedTextualItemDto() {}

    public String getFieldType() { return this.fieldType; }
    public String getText() { return this.text; }
    public String getLocale() { return this.locale; }
    public String getCountry() { return this.country; }

    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public void setText(String text) { this.text = text; }
    public void setLocale(String locale) { this.locale = locale; }
    public void setCountry(String country) { this.country = country; }
}
