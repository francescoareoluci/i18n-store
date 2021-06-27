package it.unifi.ing.dto;

import it.unifi.ing.model.*;
import it.unifi.ing.translation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DtoMapper {

    private static final Logger logger = LogManager.getLogger(DtoMapper.class);

    public static Manufacturer convertDtoToManufacturer(ManufacturerDto manufacturerDto)
    {
        Manufacturer manufacturer = ModelFactory.manufacturer();
        manufacturer.setName(manufacturerDto.getName());
        return manufacturer;
    }

    public static List<LocalizedCurrencyItemDto> convertLocalizedCurrencyListToDto(
                        Locale locale,
                        LocalizedField priceLocalizedField,
                        List<LocalizedItem> localizedItemList,
                        boolean isAdmin)
    {
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtos = new ArrayList<>();
        for (LocalizedItem ali : localizedItemList) {
            boolean found = false;
            if (isAdmin || ali.getLocale().getId().equals(locale.getId())) {
                LocalizedCurrencyItemDto lciDto = new LocalizedCurrencyItemDto();

                Long fieldId = ali.getLocalizedField().getId();
                if (priceLocalizedField.getId().equals(fieldId)) {
                    lciDto.setFieldType(TranslatableField.productPrice);
                    found = true;
                }

                if (found) {
                    lciDto.setId(ali.getId());
                    lciDto.setLanguageCode(ali.getLocale().getLanguageCode());
                    lciDto.setCountryCode(ali.getLocale().getCountryCode());
                    if (ali instanceof LocalizedCurrencyItem) {
                        LocalizedCurrencyItem lci = (LocalizedCurrencyItem) ali;
                        lciDto.setCurrency(lci.getCurrency().getCurrency());
                        lciDto.setPrice(lci.getPrice());
                    }
                    else {
                        logger.error("LocalizedItem [" + ali.getId() + "] is" +
                                "not an instance of LocalizedCurrencyItem");
                        return null;
                    }

                    localizedCurrencyItemDtos.add(lciDto);
                }
            }
        }

        return localizedCurrencyItemDtos;
    }

    public static List<LocalizedTextualItemDto> convertProductLocalizedItemListToDto(
            LocalizedField nameLocalizedField,
            LocalizedField descriptionLocalizedField,
            LocalizedField categoryLocalizedField,
            List<LocalizedItem> localizedItemList,
            Locale locale, boolean isAdmin)
    {
        List<LocalizedTextualItemDto> localizedTextualItemDtos = new ArrayList<>();

        for (LocalizedItem ali : localizedItemList) {
            boolean found = false;
            if (isAdmin || ali.getLocale().getId().equals(locale.getId())) {
                LocalizedTextualItemDto ltiDto = new LocalizedTextualItemDto();

                Long fieldId = ali.getLocalizedField().getId();
                if (nameLocalizedField.getId().equals(fieldId)) {
                    ltiDto.setFieldType(TranslatableField.productName);
                    found = true;
                } else if (descriptionLocalizedField.getId().equals(fieldId)) {
                    ltiDto.setFieldType(TranslatableField.productDescription);
                    found = true;
                } else if (categoryLocalizedField.getId().equals(fieldId)) {
                    ltiDto.setFieldType(TranslatableField.productCategory);
                    found = true;
                }

                if (found) {
                    ltiDto.setId(ali.getId());
                    ltiDto.setLanguageCode(ali.getLocale().getLanguageCode());
                    ltiDto.setCountryCode(ali.getLocale().getCountryCode());
                    if (ali instanceof LocalizedTextualItem) {
                        LocalizedTextualItem lti = (LocalizedTextualItem) ali;
                        ltiDto.setText(lti.getText());
                    }
                    else {
                        logger.error("LocalizedItem [" + ali.getId() + "] is" +
                                "not an instance of LocalizedTextualItem");
                        return null;
                    }

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
     * @param localizedFieldList: list of localized fields for product
     * @return product instance or null if an error occurs
     */
    public static Product buildProduct(Admin admin,
                                       Manufacturer manufacturer,
                                       List<Locale> localeList,
                                       List<Currency> currencyList,
                                       ProductDto productDto,
                                       List<LocalizedField> localizedFieldList)
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
            lti.setText(ltiDto.getText());

            for (Locale l : localeList) {
                if (ltiDto.getLanguageCode().equals(l.getLanguageCode()) &&
                        ltiDto.getCountryCode().equals(l.getCountryCode())) {
                    lti.setLocale(l);
                    break;
                }
            }
            boolean fieldFound = false;
            for (LocalizedField lf : localizedFieldList) {
                if (ltiDto.getFieldType().equals(lf.getType())) {
                    lti.setLocalizedField(lf);
                    fieldFound = true;
                    break;
                }
            }
            if (!fieldFound) {
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
            lci.setTranslatableItem(product);
            for (Locale l : localeList) {
                if (lciDto.getLanguageCode().equals(l.getLanguageCode()) &&
                        lciDto.getCountryCode().equals(l.getCountryCode())) {
                    lci.setLocale(l);
                    break;
                }
            }
            boolean fieldFound = false;
            for (LocalizedField lf : localizedFieldList) {
                if (lciDto.getFieldType().equals(lf.getType())) {
                    lci.setLocalizedField(lf);
                    fieldFound = true;
                    break;
                }
            }
            if (!fieldFound) {
                logger.error("Invalid field type found in requested product: " +
                        lciDto.getFieldType());
                return null;
            }

            localizedCurrencyItemList.add(lci);
        }

        List<LocalizedItem> localizedItemList = new ArrayList<>();
        localizedItemList.addAll(localizedTextualItemList);
        localizedItemList.addAll(localizedCurrencyItemList);

        product.setLocalizedItemList(localizedItemList);

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
     * @param localizedFieldList: list of localized fields for product
     * @return update product or null if an error occurs
     */
    public static Product updateProduct(Product product, List<LocalizedCurrencyItemDto> localizedCurrencyItemDtos,
                                  List<LocalizedTextualItemDto> localizedTextualItemDtos,
                                  List<Locale> localeList, List<Currency> currencyList,
                                  Manufacturer manufacturer, Admin admin,
                                  List<LocalizedField> localizedFieldList)
    {
        // Get product localized items
        List<LocalizedItem> localizedItemList = product.getLocalizedItemList();
        List<LocalizedCurrencyItem> localizedCurrencyItemList = new ArrayList<>();
        List<LocalizedTextualItem> localizedTextualItemList = new ArrayList<>();
        for (LocalizedItem ali : localizedItemList) {
            if (ali instanceof LocalizedTextualItem) {
                localizedTextualItemList.add((LocalizedTextualItem) ali);
            }
            else if (ali instanceof  LocalizedCurrencyItem) {
                localizedCurrencyItemList.add((LocalizedCurrencyItem) ali);
            }
        }

        product.setProdManufacturer(manufacturer);
        product.setProdAdministrator(admin);

        // Modify textual localizations
        for (LocalizedTextualItemDto ltiDto : localizedTextualItemDtos) {
            for (LocalizedTextualItem lti : localizedTextualItemList) {
                // If dto localization id is equal to product localization id
                if (lti.getId().equals(ltiDto.getId())) {
                    lti.setTranslatableItem(product);
                    // Set locale
                    for (Locale l : localeList) {
                        if (ltiDto.getLanguageCode().equals(l.getLanguageCode()) &&
                                ltiDto.getCountryCode().equals(l.getCountryCode())) {
                            lti.setLocale(l);
                            break;
                        }
                    }
                    // Set text
                    lti.setText(ltiDto.getText());

                    // Set correct field
                    boolean fieldFound = false;
                    for (LocalizedField lf : localizedFieldList) {
                        if (ltiDto.getFieldType().equals(lf.getType())) {
                            lti.setLocalizedField(lf);
                            fieldFound = true;
                            break;
                        }
                    }
                    if (!fieldFound) {
                        logger.error("Invalid field type found in requested product: " +
                                ltiDto.getFieldType());
                        return null;
                    }
                }
            }
        }

        // Modify currency localizations
        for (LocalizedCurrencyItemDto lciDto : localizedCurrencyItemDtos) {
            for (LocalizedCurrencyItem lci : localizedCurrencyItemList) {
                if (lci.getId().equals(lciDto.getId())) {
                    lci.setTranslatableItem(product);
                    // Set locale
                    for (Locale l : localeList) {
                        if (lciDto.getLanguageCode().equals(l.getLanguageCode()) &&
                                lciDto.getCountryCode().equals(l.getCountryCode())) {
                            lci.setLocale(l);
                            break;
                        }
                    }
                    // Set price
                    lci.setPrice(lciDto.getPrice());

                    // Set currency
                    for (Currency c : currencyList) {
                        if (lciDto.getCurrency().equals(c.getCurrency())) {
                            lci.setCurrency(c);
                        }
                    }

                    // Set correct field
                    boolean fieldFound = false;
                    for (LocalizedField lf : localizedFieldList) {
                        if (lciDto.getFieldType().equals(lf.getType())) {
                            lci.setLocalizedField(lf);
                            fieldFound = true;
                            break;
                        }
                    }
                    if (!fieldFound) {
                        logger.error("Invalid field type found in requested product: " +
                                lciDto.getFieldType());
                        return null;
                    }
                }
            }
        }
        List<LocalizedItem> newLocalizedItemList = new ArrayList<>();
        newLocalizedItemList.addAll(localizedTextualItemList);
        newLocalizedItemList.addAll(localizedCurrencyItemList);

        product.setLocalizedItemList(newLocalizedItemList);

        return product;
    }
}
