package it.unifi.ing.dao;

import it.unifi.ing.model.LocalizedProduct;

import java.util.List;

public class TranslationDao extends BaseDao<LocalizedProduct> {

    private static final long serialVersionUID = 2012271912711738162L;

    public TranslationDao() { super(LocalizedProduct.class); }

    public List<LocalizedProduct> getLocalizedProductList()
    {
        return entityManager.createQuery("SELECT l FROM LocalizedProduct l", LocalizedProduct.class)
                .getResultList();
    }

    public List<LocalizedProduct> getTranslationsByProductId(Long productId)
    {
        return entityManager
                .createQuery("SELECT l from LocalizedProduct l where l.product.id = :id", LocalizedProduct.class)
                .setParameter("id", productId)
                .getResultList();
    }

    public List<LocalizedProduct> getTranslationsByLocaleId(Long localeId)
    {
       return entityManager
                .createQuery("SELECT l from LocalizedProduct l where l.locale.id = :id", LocalizedProduct.class)
                .setParameter("id", localeId)
                .getResultList();
    }

    public List<LocalizedProduct> getTranslationsByName(String name)
    {
        return entityManager
                .createQuery("SELECT l from LocalizedProduct l where l.productName = :name", LocalizedProduct.class)
                .setParameter("name", name)
                .getResultList();
    }

    public List<LocalizedProduct> getTranslationsByCategory(String category)
    {
        return entityManager
                .createQuery("SELECT l from LocalizedProduct l where l.productCategory = :category", LocalizedProduct.class)
                .setParameter("category", category)
                .getResultList();
    }
}
