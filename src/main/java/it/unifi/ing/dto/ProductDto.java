package it.unifi.ing.dto;

import java.util.List;

public class ProductDto extends BaseDto {

    private String manufacturer;
    private List<LocalizedTextualItemDto> localizedTextualItemList;
    private List<LocalizedCurrencyItemDto> localizedCurrencyItemList;

    public ProductDto() {}

    public String getManufacturer() { return this.manufacturer; }
    public List<LocalizedTextualItemDto> getLocalizedTextualItemList()
    {
        return this.localizedTextualItemList;
    }
    public List<LocalizedCurrencyItemDto> getLocalizedCurrencyItemList()
    {
        return this.localizedCurrencyItemList;
    }

    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public void setLocalizedTextualItemList(List<LocalizedTextualItemDto> localizedTextualItemList)
    {
        this.localizedTextualItemList = localizedTextualItemList;
    }

    public void setLocalizedCurrencyItem(List<LocalizedCurrencyItemDto> localizedCurrencyItemList)
    {
        this.localizedCurrencyItemList = localizedCurrencyItemList;
    }

}
