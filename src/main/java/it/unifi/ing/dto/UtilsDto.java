package it.unifi.ing.dto;

import it.unifi.ing.model.Currency;
import it.unifi.ing.model.Locale;
import it.unifi.ing.translation.LocalizedItem;
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
        List<LocalizedItemDto> localizedItemDtos = new ArrayList<>(localizedTextualItemDtoList);
        return checkForDtoInvalidLocale(localeList, localizedItemDtos);
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
        List<LocalizedItemDto> localizedItemDtos = new ArrayList<>(localizedCurrencyItemDtos);
        return checkForDtoInvalidLocale(localeList, localizedItemDtos);
    }

    /**
     * @implNote Check if passed dto contains an unsupported locale
     * @param localeList: list of supported locales
     * @param localizedItemDtos: list of localization items in dto
     * @return true if an invalid locale is detected
     */
    public static boolean checkForDtoInvalidLocale(List<Locale> localeList,
                                                    List<LocalizedItemDto> localizedItemDtos)
    {
        boolean invalidLanguage = false;
        boolean localeFound = false;
        for (LocalizedItemDto aliDto : localizedItemDtos) {
            for (Locale l : localeList) {
                if (l.getCountryCode().equals(aliDto.getCountryCode()) &&
                        l.getLanguageCode().equals(aliDto.getLanguageCode())) {
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
        List<LocalizedItem> localizedItemList = new ArrayList<>(localizedTextualItemList);
        List<LocalizedItemDto> localizedItemDtoList = new ArrayList<>(localizedTextualItemDtoList);
        return checkForInvalidDtoLocalization(localizedItemList, localizedItemDtoList);
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
        List<LocalizedItem> localizedItemList = new ArrayList<>(localizedCurrencyItemList);
        List<LocalizedItemDto> localizedItemDtoList = new ArrayList<>(localizedTextualItemDtoList);
        return checkForInvalidDtoLocalization(localizedItemList, localizedItemDtoList);
    }

    /**
     * @implNote Check if passed dto contains the same localization id of the persisted
     *              product
     * @param localizedItemList: list of product localized items
     * @param localizedItemDtoList: list of dto localized items
     * @return true if an invalid localization is detected
     */
    public static boolean checkForInvalidDtoLocalization(List<LocalizedItem> localizedItemList,
                                                   List<LocalizedItemDto> localizedItemDtoList)
    {
        boolean invalidLocalization = false;
        boolean localizationFound = false;
        for (LocalizedItemDto aliDto : localizedItemDtoList) {
            for (LocalizedItem ali : localizedItemList) {
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
