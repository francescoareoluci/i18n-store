package it.unifi.ing.dto;

import java.util.List;

public class ShoppingListDto extends BaseDto {

    private List<ProductDto> purchasedProducts;

    public ShoppingListDto() {}

    public List<ProductDto> getPurchasedProducts() { return this.purchasedProducts; }

    public void setPurchasedProducts(List<ProductDto> purchasedProducts) { this.purchasedProducts = purchasedProducts; }

}
