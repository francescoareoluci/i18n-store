package it.unifi.ing.dto;

import java.util.List;

public class ProductDto extends BaseDto {

    private String manufacturer;
    private List<LocalizedProductDto> localizedInfo;

    public ProductDto() {}

    public String getManufacturer() { return this.manufacturer; }
    public List<LocalizedProductDto> getLocalizedInfo() { return this.localizedInfo; }

    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public void setLocalizedInfo(List<LocalizedProductDto> localizedInfo)
    {
        this.localizedInfo = localizedInfo;
    }

}
