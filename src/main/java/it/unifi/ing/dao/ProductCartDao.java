package it.unifi.ing.dao;

import it.unifi.ing.model.ProductCart;

import java.util.List;

public class ProductCartDao extends BaseDao<ProductCart> {

    private static final long serialVersionUID = -7064134840077746980L;

    public ProductCartDao() { super(ProductCart.class); }

    public List<ProductCart> getProductCartList()
    {
        return entityManager.createQuery("SELECT p FROM ProductCart p", ProductCart.class)
                .getResultList();
    }
}
