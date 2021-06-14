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
                                             List<LocalizedProductDto> localizedProductDtoList)
    {
        ProductDto productDto = new ProductDto();
        productDto.setId(id);
        productDto.setManufacturer(manufacturer);
        productDto.setLocalizedInfo(localizedProductDtoList);

        return productDto;
    }

    public static LocalizedProductDto buildLocalizedProductDto(Long id, String name, String description,
                                                               String category, String currency, String price,
                                                               String locale)
    {
        LocalizedProductDto localizedProductDto = new LocalizedProductDto();
        localizedProductDto.setId(id);
        localizedProductDto.setName(name);
        localizedProductDto.setDescription(description);
        localizedProductDto.setCategory(category);
        localizedProductDto.setCurrency(currency);
        localizedProductDto.setPrice(price);
        localizedProductDto.setLocale(locale);

        return localizedProductDto;
    }

    public static LocaleDto buildLocaleDto(Long id, String languageCode, String countryCode)
    {
        LocaleDto localeDto = new LocaleDto();
        localeDto.setId(id);
        localeDto.setCountryCode(countryCode);
        localeDto.setLanguageCode(languageCode);

        return localeDto;
    }

    public static ShoppingCartDto buildShoppingCartDto(Long id, List<ProductDto> productDtoList)
    {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(id);
        shoppingCartDto.setCartProducts(productDtoList);

        return shoppingCartDto;
    }

    public static ShoppingListDto buildShoppingListDto(Long id, List<ProductDto> productDtoList)
    {
        ShoppingListDto shoppingListDto = new ShoppingListDto();
        shoppingListDto.setId(id);
        shoppingListDto.setPurchasedProducts(productDtoList);

        return shoppingListDto;
    }

}