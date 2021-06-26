package it.unifi.ing.dao;

import it.unifi.ing.translationModel.LocalizedTextualItem;

import java.util.List;

public class LocalizedTextualItemDao extends BaseDao<LocalizedTextualItem> {

    private static final long serialVersionUID = 5886237024901911153L;

    public LocalizedTextualItemDao() { super(LocalizedTextualItem.class); }

    public List<LocalizedTextualItem> getLocalizedTextualItemList()
    {
        return entityManager.createQuery("SELECT l FROM LocalizedTextualItem l", LocalizedTextualItem.class)
                .getResultList();
    }

    public List<LocalizedTextualItem> getLocalizedTextualItemByLocaleId(Long localeId)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedTextualItem l WHERE l.locale.id = :id",
                        LocalizedTextualItem.class)
                .setParameter("id", localeId)
                .getResultList();
    }

    public List<LocalizedTextualItem> getLocalizedTextualItemByProductId(Long productId)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedTextualItem l WHERE l.product.id = :id",
                        LocalizedTextualItem.class)
                .setParameter("id", productId)
                .getResultList();
    }

    public List<LocalizedTextualItem> getLocalizedTextualItemByFieldId(Long fieldId)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedTextualItem l WHERE l.localizedField.id = :id",
                        LocalizedTextualItem.class)
                .setParameter("id", fieldId)
                .getResultList();
    }

    public List<LocalizedTextualItem> getLocalizedTextualItemByProductAndLocaleId(Long productId, Long localeId)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedTextualItem l WHERE l.locale.id = :localeId " +
                        "AND l.product.id = :productId", LocalizedTextualItem.class)
                .setParameter("localeId", localeId)
                .setParameter("productId", productId)
                .getResultList();
    }

    public LocalizedTextualItem getLocalizedTextualItemByProductLocaleAndType(Long productId,
                                                                              Long localeId,
                                                                              Long typeId)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedTextualItem l WHERE l.locale.id = :localeId " +
                        "AND l.product.id = :productId AND l.localizedField.id = :typeId",
                        LocalizedTextualItem.class)
                .setParameter("localeId", localeId)
                .setParameter("productId", productId)
                .setParameter("typeId", typeId)
                .getSingleResult();
    }
}