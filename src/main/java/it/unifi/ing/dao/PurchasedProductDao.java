package it.unifi.ing.dao;

import it.unifi.ing.model.PurchasedProduct;

import java.util.List;

public class PurchasedProductDao extends BaseDao<PurchasedProduct> {

    private static final long serialVersionUID = 5582829087358713822L;

    public PurchasedProductDao() { super(PurchasedProduct.class); }

    public List<PurchasedProduct> getPurchasedProductList()
    {
        return entityManager.createQuery("SELECT p FROM PurchasedProduct p", PurchasedProduct.class)
                .getResultList();
    }
}
