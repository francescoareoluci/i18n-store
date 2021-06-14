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
                .createQuery("SELECT l FROM LocalizedProduct l WHERE l.product.id = :id", LocalizedProduct.class)
                .setParameter("id", productId)
                .getResultList();
    }

    public List<LocalizedProduct> getTranslationsByLocaleId(Long localeId)
    {
       return entityManager
                .createQuery("SELECT l FROM LocalizedProduct l WHERE l.locale.id = :id", LocalizedProduct.class)
                .setParameter("id", localeId)
                .getResultList();
    }

    public LocalizedProduct getTranslationByProductAndLocaleId(Long productId, Long localeId)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedProduct l WHERE l.locale.id = :localeId " +
                        "AND l.product.id = :productId", LocalizedProduct.class)
                .setParameter("localeId", localeId)
                .setParameter("productId", productId)
                .getSingleResult();
    }

    public List<LocalizedProduct> getTranslationsByName(String name)
    {
        return entityManager
                .createQuery("SELECT l from LocalizedProduct l WHERE l.name = :name", LocalizedProduct.class)
                .setParameter("name", name)
                .getResultList();
    }

    public List<LocalizedProduct> getTranslationsByCategory(String category)
    {
        return entityManager
                .createQuery("SELECT l from LocalizedProduct l WHERE l.category = :category", LocalizedProduct.class)
                .setParameter("category", category)
                .getResultList();
    }
}
