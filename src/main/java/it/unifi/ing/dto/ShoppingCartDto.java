package it.unifi.ing.dto;

import java.util.List;

public class ShoppingCartDto extends BaseDto {

    private List<ProductDto> cartProducts;

    public ShoppingCartDto() {}

    public List<ProductDto> getCartProducts() { return this.cartProducts; }

    public void setCartProducts(List<ProductDto> cartProducts) { this.cartProducts = cartProducts; }
}
