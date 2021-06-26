package it.unifi.ing.dto;

import java.util.List;

public class DtoFactory {

    public DtoFactory() {}

    public static ManufacturerDto buildManufacturerDto(Long id, String name)
    {
        ManufacturerDto manufacturerDto = new ManufacturerDto();
        manufacturerDto.setId(id);
        manufacturerDto.setName(name);

        return manufacturerDto;
    }

    public static UserDto buildUserDto(Long id, String firstName, String lastName,
                                       String mail, UserDto.UserRole role)
    {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setMail(mail);
        userDto.setRole(role);

        return userDto;
    }

    public static ProductDto buildProductDto(Long id, String manufacturer,
                                             List<LocalizedTextualItemDto> localizedTextualItemDtos,
                                             List<LocalizedCurrencyItemDto> localizedCurrencyItemDtos)
    {
        ProductDto productDto = new ProductDto();
        productDto.setId(id);
        productDto.setManufacturer(manufacturer);
        productDto.setLocalizedTextualItemList(localizedTextualItemDtos);
        productDto.setLocalizedCurrencyItem(localizedCurrencyItemDtos);

        return productDto;
    }

    public static LocaleDto buildLocaleDto(Long id, String languageCode, String countryCode)
    {
        LocaleDto localeDto = new LocaleDto();
        localeDto.setId(id);
        localeDto.setCountryCode(countryCode);
        localeDto.setLanguageCode(languageCode);

        return localeDto;
    }

    public static ShoppingCartDto buildShoppingCartDto(Long id, List<ProductDto> productDtoList, float totalCost)
    {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(id);
        shoppingCartDto.setCartProducts(productDtoList);
        shoppingCartDto.setTotalCost(totalCost);

        return shoppingCartDto;
    }

    public static ShoppingListDto buildShoppingListDto(Long id, List<ProductDto> productDtoList)
    {
        ShoppingListDto shoppingListDto = new ShoppingListDto();
        shoppingListDto.setId(id);
        shoppingListDto.setPurchasedProducts(productDtoList);

        return shoppingListDto;
    }

    public static CurrencyDto buildCurrencyDto(Long id, String currency)
    {
        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setId(id);
        currencyDto.setCurrency(currency);

        return currencyDto;
    }

    public static LocalizedCurrencyItemDto buildLocalizedCurrencyItemDto(Long id, String currency,
                                                                         float price, String locale,
                                                                         String country)
    {
        LocalizedCurrencyItemDto localizedCurrencyItemDto = new LocalizedCurrencyItemDto();
        localizedCurrencyItemDto.setId(id);
        localizedCurrencyItemDto.setCurrency(currency);
        localizedCurrencyItemDto.setPrice(price);
        localizedCurrencyItemDto.setCountry(country);
        localizedCurrencyItemDto.setLocale(locale);

        return localizedCurrencyItemDto;
    }

    public static LocalizedTextualItemDto buildLocalizedTextualItemDto(Long id, String text,
                                                                       String type, String locale,
                                                                       String country)
    {
        LocalizedTextualItemDto localizedTextualItemDto = new LocalizedTextualItemDto();
        localizedTextualItemDto.setId(id);
        localizedTextualItemDto.setFieldType(type);
        localizedTextualItemDto.setText(text);
        localizedTextualItemDto.setLocale(locale);
        localizedTextualItemDto.setCountry(country);

        return localizedTextualItemDto;
    }

}
