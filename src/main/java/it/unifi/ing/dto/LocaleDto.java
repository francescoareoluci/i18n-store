package it.unifi.ing.dto;

import it.unifi.ing.dao.LocaleDao;

public class LocaleDto extends BaseDto {

    private String languageCode;
    private String countryCode;

    public LocaleDto() {}

    public String getLanguageCode() { return this.languageCode; }
    public String getCountryCode() { return this.countryCode; }

    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
}
