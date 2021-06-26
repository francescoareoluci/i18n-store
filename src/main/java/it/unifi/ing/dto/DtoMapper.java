package it.unifi.ing.dto;

import it.unifi.ing.model.*;
import it.unifi.ing.translation.LocalizedCurrencyItem;
import it.unifi.ing.translation.LocalizedField;
import it.unifi.ing.translation.LocalizedTextualItem;
import it.unifi.ing.translation.TranslatableType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DtoMapper {

    private static final Logger logger = LogManager.getLogger(DtoMapper.class);

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

    /**
     * @implNote Build a product by using passed dto
     * @param admin: administrator instance
     * @param manufacturer: manufacturer instance
     * @param localeList: supported locale list
     * @param currencyList: supported currency list
     * @param productDto: passed product dto
     * @return product instance or null if an error occurs
     */
    public static Product buildProduct(Admin admin,
                                 Manufacturer manufacturer,
                                 List<Locale> localeList,
                                 List<Currency> currencyList,
                                 ProductDto productDto,
                                 LocalizedField nameLocalizedField,
                                 LocalizedField descriptionLocalizedField,
                                 LocalizedField categoryLocalizedField)
    {
        List<LocalizedTextualItemDto> localizedTextualItemDtoList = productDto.getLocalizedTextualItemList();
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = productDto.getLocalizedCurrencyItem();

        Product product = ModelFactory.product();
        product.setProdManufacturer(manufacturer);
        product.setProdAdministrator(admin);

        List<LocalizedTextualItem> localizedTextualItemList = new ArrayList<>();
        for (LocalizedTextualItemDto ltiDto : localizedTextualItemDtoList) {
            LocalizedTextualItem lti = ModelFactory.localizedTextualItem();
            lti.setTranslatableItem(product);

            for (Locale l : localeList) {
                if (ltiDto.getLocale().equals(l.getLanguageCode()) &&
                        ltiDto.getCountry().equals(l.getCountryCode())) {
                    lti.setLocale(l);
                    break;
                }
            }
            boolean fieldFound = false;
            if (ltiDto.getFieldType().equals(nameLocalizedField.getType())) {
                lti.setLocalizedField(nameLocalizedField);
                fieldFound = true;
            }
            else if (ltiDto.getFieldType().equals(descriptionLocalizedField.getType())) {
                lti.setLocalizedField(descriptionLocalizedField);
                fieldFound = true;
            }
            else if (ltiDto.getFieldType().equals(categoryLocalizedField.getType())) {
                lti.setLocalizedField(categoryLocalizedField);
                fieldFound = true;
            }
            if (fieldFound) {
                lti.setText(ltiDto.getText());
            }
            else {
                logger.error("Invalid field type found in requested product: " +
                        ltiDto.getFieldType());
                return null;
            }

            localizedTextualItemList.add(lti);
        }

        List<LocalizedCurrencyItem> localizedCurrencyItemList = new ArrayList<>();
        for (LocalizedCurrencyItemDto lciDto : localizedCurrencyItemDtoList) {
            LocalizedCurrencyItem lci = ModelFactory.localizedCurrencyItem();
            for (Currency c : currencyList) {
                if (lciDto.getCurrency().equals(c.getCurrency())) {
                    lci.setCurrency(c);
                    break;
                }
            }
            lci.setPrice(lciDto.getPrice());
            lci.setProduct(product);
            for (Locale l : localeList) {
                if (lciDto.getLocale().equals(l.getLanguageCode()) &&
                        lciDto.getCountry().equals(l.getCountryCode())) {
                    lci.setLocale(l);
                    break;
                }
            }

            localizedCurrencyItemList.add(lci);
        }

        product.setLocalizedCurrencyItemList(localizedCurrencyItemList);
        product.setLocalizedTextualItemList(localizedTextualItemList);

        return product;
    }

    /**
     * @implNote Update product with passed dto
     * @param product: product to be updated
     * @param localizedCurrencyItemDtos: dto localized currency items
     * @param localizedTextualItemDtos: dto localized textual items
     * @param localeList: supported locales
     * @param currencyList: supported currencies
     * @param manufacturer: product manufacturer
     * @param admin: administrator
     * @return update product or null if an error occurs
     */
    public static Product updateProduct(Product product, List<LocalizedCurrencyItemDto> localizedCurrencyItemDtos,
                                  List<LocalizedTextualItemDto> localizedTextualItemDtos,
                                  List<Locale> localeList, List<Currency> currencyList,
                                  Manufacturer manufacturer, Admin admin,
                                  LocalizedField nameLocalizedField, LocalizedField descriptionLocalizedField,
                                  LocalizedField categoryLocalizedField)
    {
        // Get product localized items
        List<LocalizedCurrencyItem> localizedCurrencyItemList = product.getLocalizedCurrencyItemList();
        List<LocalizedTextualItem> localizedTextualItemList = product.getLocalizedTextualItemList();

        product.setProdManufacturer(manufacturer);
        product.setProdAdministrator(admin);

        // Modify textual localizations
        for (LocalizedTextualItemDto ltiDto : localizedTextualItemDtos) {
            for (LocalizedTextualItem lti : localizedTextualItemList) {
                // If dto localization id is equal to product localization id
                if (lti.getId().equals(ltiDto.getId())) {
                    lti.setTranslatableItem(product);
                    for (Locale l : localeList) {
                        if (ltiDto.getLocale().equals(l.getLanguageCode()) &&
                                ltiDto.getCountry().equals(l.getCountryCode())) {
                            lti.setLocale(l);
                            break;
                        }
                    }
                    lti.setText(ltiDto.getText());
                    // Set correct field
                    boolean fieldFound = false;
                    if (ltiDto.getFieldType().equals(nameLocalizedField.getType())) {
                        lti.setLocalizedField(nameLocalizedField);
                        fieldFound = true;
                    }
                    else if (ltiDto.getFieldType().equals(descriptionLocalizedField.getType())) {
                        lti.setLocalizedField(descriptionLocalizedField);
                        fieldFound = true;
                    }
                    else if (ltiDto.getFieldType().equals(categoryLocalizedField.getType())) {
                        lti.setLocalizedField(categoryLocalizedField);
                        fieldFound = true;
                    }
                    if (fieldFound) {
                        lti.setText(ltiDto.getText());
                    }
                    else {
                        logger.error("Invalid field type found in requested product: " +
                                ltiDto.getFieldType());
                        return null;
                    }
                }
            }
        }
        product.setLocalizedTextualItemList(localizedTextualItemList);

        // Modify currency localizations
        for (LocalizedCurrencyItemDto lciDto : localizedCurrencyItemDtos) {
            for (LocalizedCurrencyItem lci : localizedCurrencyItemList) {
                if (lci.getId().equals(lciDto.getId())) {
                    lci.setProduct(product);
                    for (Locale l : localeList) {
                        if (lciDto.getLocale().equals(l.getLanguageCode()) &&
                                lciDto.getCountry().equals(l.getCountryCode())) {
                            lci.setLocale(l);
                            break;
                        }
                    }
                    lci.setPrice(lciDto.getPrice());
                    for (Currency c : currencyList) {
                        if (lciDto.getCurrency().equals(c.getCurrency())) {
                            lci.setCurrency(c);
                        }
                    }
                }
            }
        }
        product.setLocalizedCurrencyItemList(localizedCurrencyItemList);

        return product;
    }
}
