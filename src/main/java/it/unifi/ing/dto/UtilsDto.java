package it.unifi.ing.dto;

import it.unifi.ing.model.Currency;
import it.unifi.ing.model.Locale;
import it.unifi.ing.translation.AbstractLocalizedItem;
import it.unifi.ing.translation.LocalizedCurrencyItem;
import it.unifi.ing.translation.LocalizedTextualItem;

import java.util.ArrayList;
import java.util.List;

public class UtilsDto {

    /**
     * @implNote Check if passed dto textual localizations contain an unsupported locale
     * @param localeList: list of supported locales
     * @param localizedTextualItemDtoList: list of textual items in dto
     * @return true if an invalid locale is detected
     */
    public static boolean checkForDtoInvalidTextualLocale(List<Locale> localeList,
                                                    List<LocalizedTextualItemDto> localizedTextualItemDtoList)
    {
        List<AbstractLocalizedItemDto> abstractLocalizedItemDtos = new ArrayList<>(localizedTextualItemDtoList);
        return checkForDtoInvalidLocale(localeList, abstractLocalizedItemDtos);
    }

    /**
     * @implNote Check if passed dto currency localizations contain an unsupported locale
     * @param localeList: list of supported locales
     * @param localizedCurrencyItemDtos: list of currency items in dto
     * @return true if an invalid locale is detected
     */
    public static boolean checkForDtoInvalidCurrencyLocale(List<Locale> localeList,
                                                     List<LocalizedCurrencyItemDto> localizedCurrencyItemDtos)
    {
        List<AbstractLocalizedItemDto> abstractLocalizedItemDtos = new ArrayList<>(localizedCurrencyItemDtos);
        return checkForDtoInvalidLocale(localeList, abstractLocalizedItemDtos);
    }

    /**
     * @implNote Check if passed dto contains an unsupported locale
     * @param localeList: list of supported locales
     * @param abstractLocalizedItemDtos: list of localization items in dto
     * @return true if an invalid locale is detected
     */
    public static boolean checkForDtoInvalidLocale(List<Locale> localeList,
                                                    List<AbstractLocalizedItemDto> abstractLocalizedItemDtos)
    {
        boolean invalidLanguage = false;
        boolean localeFound = false;
        for (AbstractLocalizedItemDto aliDto : abstractLocalizedItemDtos) {
            for (Locale l : localeList) {
                if (l.getCountryCode().equals(aliDto.getCountry()) &&
                        l.getLanguageCode().equals(aliDto.getLocale())) {
                    localeFound = true;
                    break;
                }
            }

            if (!localeFound) {
                invalidLanguage = true;
                break;
            }
        }

        return invalidLanguage;
    }

    /**
     * @implNote Check if the passed dto contains an invalid currency
     * @param currencyList: list of supported currencies
     * @param localizedCurrencyItemDtoList: list of currencies items in dto
     * @return true if an invalid currency is detected
     */
    public static boolean checkForDtoInvalidCurrency(List<Currency> currencyList,
                                               List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList)
    {
        boolean invalidCurrency = false;
        boolean currencyFound = false;
        for (LocalizedCurrencyItemDto lciDto : localizedCurrencyItemDtoList) {
            for (Currency c : currencyList) {
                if (c.getCurrency().equals(lciDto.getCurrency())) {
                    currencyFound = true;
                    break;
                }
            }

            if (!currencyFound) {
                invalidCurrency = true;
                break;
            }
        }

        return invalidCurrency;
    }

    /**
     * @implNote Check if passed dto contains the same textual localization id of the persisted product
     * @param localizedTextualItemList list of product localized textual items
     * @param localizedTextualItemDtoList list of dto localized textual items
     * @return true if an invalid localization is detected
     */
    public static boolean checkForInvalidDtoTextualLocalization(List<LocalizedTextualItem> localizedTextualItemList,
                                                          List<LocalizedTextualItemDto> localizedTextualItemDtoList)
    {
        List<AbstractLocalizedItem> abstractLocalizedItemList = new ArrayList<>(localizedTextualItemList);
        List<AbstractLocalizedItemDto> abstractLocalizedItemDtoList = new ArrayList<>(localizedTextualItemDtoList);
        return checkForInvalidDtoLocalization(abstractLocalizedItemList, abstractLocalizedItemDtoList);
    }

    /**
     * @implNote Check if passed dto contains the same currency localization id of the persisted product
     * @param localizedCurrencyItemList list of product localized currency items
     * @param localizedTextualItemDtoList list of dto localized currency items
     * @return true if an invalid localization is detected
     */
    public static boolean checkForInvalidDtoCurrencyLocalization(List<LocalizedCurrencyItem> localizedCurrencyItemList,
                                                           List<LocalizedCurrencyItemDto> localizedTextualItemDtoList)
    {
        List<AbstractLocalizedItem> abstractLocalizedItemList = new ArrayList<>(localizedCurrencyItemList);
        List<AbstractLocalizedItemDto> abstractLocalizedItemDtoList = new ArrayList<>(localizedTextualItemDtoList);
        return checkForInvalidDtoLocalization(abstractLocalizedItemList, abstractLocalizedItemDtoList);
    }

    /**
     * @implNote Check if passed dto contains the same localization id of the persisted
     *              product
     * @param abstractLocalizedItemList: list of product localized items
     * @param abstractLocalizedItemDtoList: list of dto localized items
     * @return true if an invalid localization is detected
     */
    public static boolean checkForInvalidDtoLocalization(List<AbstractLocalizedItem> abstractLocalizedItemList,
                                                   List<AbstractLocalizedItemDto> abstractLocalizedItemDtoList)
    {
        boolean invalidLocalization = false;
        boolean localizationFound = false;
        for (AbstractLocalizedItemDto aliDto : abstractLocalizedItemDtoList) {
            for (AbstractLocalizedItem ali : abstractLocalizedItemList) {
                if (ali.getId().equals(aliDto.getId())) {
                    localizationFound = true;
                    break;
                }
            }

            if (!localizationFound) {
                invalidLocalization = true;
                break;
            }
        }

        return invalidLocalization;
    }
}
