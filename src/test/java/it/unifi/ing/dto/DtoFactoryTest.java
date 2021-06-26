package it.unifi.ing.dto;

import it.unifi.ing.translation.LocalizedTextualItem;
import it.unifi.ing.translation.TranslatableType;
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
    public void testBuildLocalizedCurrencyItemDto()
    {
        LocalizedCurrencyItemDto localizedCurrencyItemDto = DtoFactory.buildLocalizedCurrencyItemDto(
                1L, "$", 12f, "it", "IT");

        assertEquals(1L, localizedCurrencyItemDto.getId(), 0);
        assertEquals("$", localizedCurrencyItemDto.getCurrency());
        assertEquals(12f, localizedCurrencyItemDto.getPrice(), 0);
        assertEquals("it", localizedCurrencyItemDto.getLocale());
        assertEquals("IT", localizedCurrencyItemDto.getCountry());
    }

    @Test
    public void testBuildLocalizedTextualItemDto()
    {
        LocalizedTextualItemDto localizedTextualItemDto = DtoFactory.buildLocalizedTextualItemDto(
                1L, "test", TranslatableType.productName, "it", "IT");

        assertEquals(1L, localizedTextualItemDto.getId(), 0);
        assertEquals("test", localizedTextualItemDto.getText());
        assertEquals(TranslatableType.productName, localizedTextualItemDto.getFieldType());
        assertEquals("it", localizedTextualItemDto.getLocale());
        assertEquals("IT", localizedTextualItemDto.getCountry());
    }

    @Test
    public void testBuildProductDto()
    {
        List<LocalizedTextualItemDto> localizedTextualItemDtoList = new ArrayList<>();
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = new ArrayList<>();

        localizedTextualItemDtoList.add(DtoFactory.buildLocalizedTextualItemDto(4L, "a", "product_name",
                "en", "US"));
        localizedTextualItemDtoList.add(DtoFactory.buildLocalizedTextualItemDto(5L, "b",
                "product_description", "en", "US"));
        localizedCurrencyItemDtoList.add(DtoFactory.buildLocalizedCurrencyItemDto(6L, "$", 20.0f,
                "en", "US"));

        ProductDto productDto = DtoFactory.buildProductDto(6L, "man",
                localizedTextualItemDtoList, localizedCurrencyItemDtoList);

        assertEquals(productDto.getId(), 6L, 0);
        assertEquals(productDto.getManufacturer(), "man");
        for (int i = 0; i < localizedTextualItemDtoList.size(); i++) {
            assertEquals(productDto.getLocalizedTextualItemList().get(i).getId(),
                    localizedTextualItemDtoList.get(i).getId());
        }
        for (int i = 0; i < localizedCurrencyItemDtoList.size(); i++) {
            assertEquals(productDto.getLocalizedCurrencyItem().get(i).getId(),
                    localizedCurrencyItemDtoList.get(i).getId());
        }
    }

    @Test
    public void testBuildShoppingListDto()
    {
        List<LocalizedTextualItemDto> localizedTextualItemDtoList = new ArrayList<>();
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = new ArrayList<>();

        localizedTextualItemDtoList.add(DtoFactory.buildLocalizedTextualItemDto(4L, "a", "product_name",
                "en", "US"));
        localizedTextualItemDtoList.add(DtoFactory.buildLocalizedTextualItemDto(5L, "b",
                "product_description", "en", "US"));
        localizedCurrencyItemDtoList.add(DtoFactory.buildLocalizedCurrencyItemDto(6L, "$", 20.0f,
                "en", "US"));

        ProductDto productDto = DtoFactory.buildProductDto(6L, "man",
                localizedTextualItemDtoList, localizedCurrencyItemDtoList);

        List<ProductDto> productDtoList = new ArrayList<>();
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
        List<LocalizedTextualItemDto> localizedTextualItemDtoList = new ArrayList<>();
        List<LocalizedCurrencyItemDto> localizedCurrencyItemDtoList = new ArrayList<>();

        localizedTextualItemDtoList.add(DtoFactory.buildLocalizedTextualItemDto(4L, "a", "product_name",
                "en", "US"));
        localizedTextualItemDtoList.add(DtoFactory.buildLocalizedTextualItemDto(5L, "b",
                "product_description", "en", "US"));
        localizedCurrencyItemDtoList.add(DtoFactory.buildLocalizedCurrencyItemDto(6L, "$", 20.0f,
                "en", "US"));

        ProductDto productDto = DtoFactory.buildProductDto(6L, "man",
                localizedTextualItemDtoList, localizedCurrencyItemDtoList);

        float totalCost = 0;
        for (LocalizedCurrencyItemDto l : localizedCurrencyItemDtoList) {
            totalCost += l.getPrice();
        }

        List<ProductDto> productDtoList = new ArrayList<>();
        productDtoList.add(productDto);

        ShoppingCartDto shoppingCartDto = DtoFactory.buildShoppingCartDto(10L, productDtoList, totalCost);

        assertEquals(shoppingCartDto.getId(), 10L, 0);
        for (int i = 0; i < productDtoList.size(); i++) {
            assertEquals(shoppingCartDto.getCartProducts().get(0).getId(), productDtoList.get(i).getId());
        }
        assertEquals(shoppingCartDto.getTotalCost(), totalCost, 0);
    }

}
