package it.unifi.ing.dto;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DtoFactoryTest {

    @Test
    public void testBuildManufacturerDto()
    {
        ManufacturerDto manufacturerDto = DtoFactory.buildManufacturerDto(1L, "man");

        assertEquals(manufacturerDto.getId(), 1L, 0);
        assertEquals(manufacturerDto.getName(), "man");
    }

    @Test
    public void testBuildUserDto()
    {
        UserDto user = DtoFactory.buildUserDto(2L, "a", "b",
                "a.b@example.com", UserDto.UserRole.ADMIN);

        assertEquals(user.getId(), 2L, 0);
        assertEquals(user.getFirstName(), "a");
        assertEquals(user.getLastName(), "b");
        assertEquals(user.getMail(), "a.b@example.com");
        assertEquals(user.getRole(), UserDto.UserRole.ADMIN);
    }

    @Test
    public void testBuildLocaleDto()
    {
        LocaleDto localeDto = DtoFactory.buildLocaleDto(3L, "it", "IT");

        assertEquals(localeDto.getId(), 3L, 0);
        assertEquals(localeDto.getLanguageCode(), "it");
        assertEquals(localeDto.getCountryCode(), "IT");
    }

    @Test
    public void testBuildCurrencyDto()
    {
        CurrencyDto currencyDto = DtoFactory.buildCurrencyDto(3L, "$");

        assertEquals(currencyDto.getId(), 3L, 0);
        assertEquals(currencyDto.getCurrency(), "$");
    }

    @Test
    public void testBuildLocalizedProductDto()
    {
        LocalizedProductDto localizedProductDto = DtoFactory.buildLocalizedProductDto(4L, "a", "b",
                "c", "$", 10.15f, "en", "US");

        assertEquals(localizedProductDto.getId(), 4L, 0);
        assertEquals(localizedProductDto.getName(), "a");
        assertEquals(localizedProductDto.getDescription(), "b");
        assertEquals(localizedProductDto.getCategory(), "c");
        assertEquals(localizedProductDto.getCurrency(), "$");
        assertEquals(localizedProductDto.getPrice(), 10.15f, 0);
        assertEquals(localizedProductDto.getLocale(), "en");
        assertEquals(localizedProductDto.getCountry(), "US");
    }

    @Test
    public void testBuildProductDto()
    {
        List<LocalizedProductDto> localizedProductDtoList = new ArrayList<>();
        localizedProductDtoList.add(DtoFactory.buildLocalizedProductDto(4L, "a", "b",
                "c", "$", 10.15f, "en", "US"));
        localizedProductDtoList.add(DtoFactory.buildLocalizedProductDto(5L, "d", "e",
                "f", "$", 14.10f, "en", "US"));

        ProductDto productDto = DtoFactory.buildProductDto(6L, "man", localizedProductDtoList);

        assertEquals(productDto.getId(), 6L, 0);
        assertEquals(productDto.getManufacturer(), "man");
        for (int i = 0; i < localizedProductDtoList.size(); i++) {
            assertEquals(productDto.getLocalizedInfo().get(i).getId(), localizedProductDtoList.get(i).getId());
        }
    }

    @Test
    public void testBuildShoppingListDto()
    {
        List<LocalizedProductDto> localizedProductDtoList = new ArrayList<>();
        localizedProductDtoList.add(DtoFactory.buildLocalizedProductDto(4L, "a", "b",
                "c", "$", 10.15f, "en", "US"));
        localizedProductDtoList.add(DtoFactory.buildLocalizedProductDto(5L, "d", "e",
                "f", "$", 14.10f, "en", "US"));

        List<ProductDto> productDtoList = new ArrayList<>();
        ProductDto productDto = DtoFactory.buildProductDto(6L, "man", localizedProductDtoList);
        productDtoList.add(productDto);

        ShoppingListDto shoppingListDto = DtoFactory.buildShoppingListDto(10L, productDtoList);

        assertEquals(shoppingListDto.getId(), 10L, 0);
        for (int i = 0; i < productDtoList.size(); i++) {
            assertEquals(shoppingListDto.getPurchasedProducts().get(0).getId(), productDtoList.get(i).getId());
        }
    }

    @Test
    public void testBuildShoppingCartDto()
    {
        List<LocalizedProductDto> localizedProductDtoList = new ArrayList<>();
        localizedProductDtoList.add(DtoFactory.buildLocalizedProductDto(4L, "a", "b",
                "c", "$", 10.15f, "en", "US"));
        localizedProductDtoList.add(DtoFactory.buildLocalizedProductDto(5L, "d", "e",
                "f", "$", 14.10f, "en", "US"));

        float totalCost = 0;
        for (LocalizedProductDto l : localizedProductDtoList) {
            totalCost += Float.valueOf(l.getPrice());
        }

        List<ProductDto> productDtoList = new ArrayList<>();
        ProductDto productDto = DtoFactory.buildProductDto(6L, "man", localizedProductDtoList);
        productDtoList.add(productDto);

        ShoppingCartDto shoppingCartDto = DtoFactory.buildShoppingCartDto(10L, productDtoList, totalCost);

        assertEquals(shoppingCartDto.getId(), 10L, 0);
        for (int i = 0; i < productDtoList.size(); i++) {
            assertEquals(shoppingCartDto.getCartProducts().get(0).getId(), productDtoList.get(i).getId());
        }
        assertEquals(shoppingCartDto.getTotalCost(), totalCost, 0);
    }
}
