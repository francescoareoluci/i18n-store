package it.unifi.ing.dto;

import it.unifi.ing.model.*;
import it.unifi.ing.translationModel.LocalizedCurrencyItem;
import it.unifi.ing.translationModel.LocalizedField;
import it.unifi.ing.translationModel.LocalizedTextualItem;
import it.unifi.ing.translationModel.TranslatableType;

import java.util.ArrayList;
import java.util.List;

public class DtoMapper {

    public static LocaleDto convertLocaleToDto(Locale locale)
    {
        return DtoFactory.buildLocaleDto(locale.getId(),
                locale.getLanguageCode(), locale.getCountryCode());
    }

    public static Locale convertDtoToLocale(LocaleDto localeDto)
    {
        Locale locale = ModelFactory.locale();
        locale.setLanguageCode(localeDto.getLanguageCode());
        locale.setCountryCode(localeDto.getCountryCode());
        return locale;
    }

    public static ManufacturerDto convertManufacturerToDto(Manufacturer manufacturer)
    {
        return DtoFactory.buildManufacturerDto(manufacturer.getId(), manufacturer.getName());
    }

    public static Manufacturer convertDtoToManufacturer(ManufacturerDto manufacturerDto)
    {
        Manufacturer manufacturer = ModelFactory.manufacturer();
        manufacturer.setName(manufacturerDto.getName());
        return manufacturer;
    }

    public static List<LocalizedCurrencyItemDto> convertLocalizedCurrencyListToDto(
                        Locale locale,
                        List<LocalizedCurrencyItem> localizedCurrencyItemList,
                        boolean isAdmin)
    {
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtos = new ArrayList<>();
        for (LocalizedCurrencyItem lci : localizedCurrencyItemList) {
            if (isAdmin || lci.getLocale().getId().equals(locale.getId())) {
                LocalizedCurrencyItemDto localizedCurrencyItemDto = new LocalizedCurrencyItemDto();
                localizedCurrencyItemDto.setId(lci.getId());
                localizedCurrencyItemDto.setCurrency(lci.getCurrency().getCurrency());
                localizedCurrencyItemDto.setPrice(lci.getPrice());
                localizedCurrencyItemDto.setLocale(lci.getLocale().getLanguageCode());
                localizedCurrencyItemDto.setCountry(lci.getLocale().getCountryCode());

                localizedCurrencyItemDtos.add(localizedCurrencyItemDto);
            }
        }

        return localizedCurrencyItemDtos;
    }

    public static List<LocalizedTextualItemDto> convertProductLocalizedItemListToDto(
            LocalizedField nameLocalizedField,
            LocalizedField descriptionLocalizedField,
            LocalizedField categoryLocalizedField,
            List<LocalizedTextualItem> localizedTextualItemList,
            Locale locale, boolean isAdmin)
    {
        List<LocalizedTextualItemDto> localizedTextualItemDtos = new ArrayList<>();

        for (LocalizedTextualItem lti : localizedTextualItemList) {
            boolean found = false;
            if (isAdmin || lti.getLocale().getId().equals(locale.getId())) {
                LocalizedTextualItemDto ltiDto = new LocalizedTextualItemDto();

                Long fieldId = lti.getLocalizedField().getId();
                if (nameLocalizedField.getId().equals(fieldId)) {
                    ltiDto.setFieldType(TranslatableType.productName);
                    found = true;
                } else if (descriptionLocalizedField.getId().equals(fieldId)) {
                    ltiDto.setFieldType(TranslatableType.productDescription);
                    found = true;
                } else if (categoryLocalizedField.getId().equals(fieldId)) {
                    ltiDto.setFieldType(TranslatableType.productCategory);
                    found = true;
                }

                if (found) {
                    ltiDto.setId(lti.getId());
                    ltiDto.setText(lti.getText());
                    ltiDto.setLocale(lti.getLocale().getLanguageCode());
                    ltiDto.setCountry(lti.getLocale().getCountryCode());
                    localizedTextualItemDtos.add(ltiDto);
                }
            }
        }

        return localizedTextualItemDtos;
    }
}
