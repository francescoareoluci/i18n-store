package it.unifi.ing.dao;

import it.unifi.ing.model.CartProduct;

import java.util.List;

public class CartProductDao extends BaseDao<CartProduct> {

    private static final long serialVersionUID = -7064134840077746980L;

    public CartProductDao() { super(CartProduct.class); }

    public List<CartProduct> getCartProductList()
    {
        return entityManager.createQuery("SELECT p FROM CartProduct p", CartProduct.class)
                .getResultList();
    }
}
