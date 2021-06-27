package it.unifi.ing.dto;

public abstract class LocalizedItemDto extends BaseDto {

    private String fieldType;
    private String languageCode;
    private String countryCode;

    public LocalizedItemDto() {}

    public String getFieldType() { return this.fieldType; }
    public String getLanguageCode() { return this.languageCode; }
    public String getCountryCode() { return this.countryCode; }

    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
}
