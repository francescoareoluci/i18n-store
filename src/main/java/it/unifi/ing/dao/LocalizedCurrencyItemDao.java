package it.unifi.ing.dao;

import it.unifi.ing.translation.LocalizedCurrencyItem;

import java.util.List;

public class LocalizedCurrencyItemDao extends BaseDao<LocalizedCurrencyItem> {

    private static final long serialVersionUID = 5886237024901911153L;

    public LocalizedCurrencyItemDao() { super(LocalizedCurrencyItem.class); }

    public List<LocalizedCurrencyItem> getLocalizedCurrencyItemList()
    {
        return entityManager.createQuery("SELECT l FROM LocalizedCurrencyItem l", LocalizedCurrencyItem.class)
                .getResultList();
    }

    public List<LocalizedCurrencyItem> getLocalizedCurrencyItemByLocaleId(Long localeId)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedCurrencyItem l WHERE l.locale.id = :id",
                        LocalizedCurrencyItem.class)
                .setParameter("id", localeId)
                .getResultList();
    }

    public List<LocalizedCurrencyItem> getLocalizedCurrencyItemByProductId(Long productId)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedCurrencyItem l WHERE l.translatableItem.id = :id",
                        LocalizedCurrencyItem.class)
                .setParameter("id", productId)
                .getResultList();
    }

    public List<LocalizedCurrencyItem> getLocalizedCurrencyItemByCurrencyId(Long currencyId)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedCurrencyItem l WHERE l.currency.id = :id",
                        LocalizedCurrencyItem.class)
                .setParameter("id", currencyId)
                .getResultList();
    }

    public List<LocalizedCurrencyItem> getLocalizedCurrencyItemByProductAndLocaleId(Long productId, Long localeId)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedCurrencyItem l WHERE l.locale.id = :localeId " +
                        "AND l.translatableItem.id = :productId", LocalizedCurrencyItem.class)
                .setParameter("localeId", localeId)
                .setParameter("productId", productId)
                .getResultList();
    }

    public LocalizedCurrencyItem getLocalizedCurrencyItemByProductLocaleAndCurrency(Long productId, Long localeId,
                                                                                          Long currencyId)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedCurrencyItem l WHERE l.locale.id = :localeId " +
                        "AND l.translatableItem.id = :productId AND l.currency.id = :currencyId",
                        LocalizedCurrencyItem.class)
                .setParameter("localeId", localeId)
                .setParameter("productId", productId)
                .setParameter("currencyId", currencyId)
                .getSingleResult();
    }
}
