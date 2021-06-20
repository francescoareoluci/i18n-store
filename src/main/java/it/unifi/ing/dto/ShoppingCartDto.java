package it.unifi.ing.dto;

import java.util.List;

public class ShoppingCartDto extends BaseDto {

    private List<ProductDto> cartProducts;
    private float totalCost;

    public ShoppingCartDto() {}

    public List<ProductDto> getCartProducts() { return this.cartProducts; }
    public float getTotalCost() { return this.totalCost; }

    public void setCartProducts(List<ProductDto> cartProducts) { this.cartProducts = cartProducts; }
    public void setTotalCost(float totalCost) { this.totalCost = totalCost; }
}
