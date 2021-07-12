package it.unifi.ing.dto;

import java.util.List;

public class ShoppingCartDto extends BaseDto {

    private List<ProductDto> cartProducts;
    private float totalCost;
    private String costCurrency;

    public ShoppingCartDto() {}

    public List<ProductDto> getCartProducts() { return this.cartProducts; }
    public float getTotalCost() { return this.totalCost; }
    public String getCostCurrency() { return this.costCurrency; }

    public void setCartProducts(List<ProductDto> cartProducts) { this.cartProducts = cartProducts; }
    public void setTotalCost(float totalCost) { this.totalCost = totalCost; }
    public void setCostCurrency(String costCurrency) { this.costCurrency = costCurrency; }
}
