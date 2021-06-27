package it.unifi.ing.dao;

import it.unifi.ing.model.*;
import it.unifi.ing.translation.LocalizedCurrencyItem;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LocalizedCurrencyItemDaoTest extends JpaTest {

    private LocalizedCurrencyItemDao localizedCurrencyItemDao;
    private LocalizedCurrencyItem localizedCurrencyItem;
    private Product product;
    private Locale locale;
    private Currency currency;

    @Override
    protected void init() throws InitializationError
    {
        locale = ModelFactory.locale();
        locale.setLanguageCode("it");
        locale.setCountryCode("IT");

        currency = ModelFactory.currency();
        currency.setCurrency("$");

        product = ModelFactory.product();

        localizedCurrencyItem = ModelFactory.localizedCurrencyItem();
        localizedCurrencyItem.setPrice(12.5f);
        localizedCurrencyItem.setCurrency(currency);
        localizedCurrencyItem.setLocale(locale);
        localizedCurrencyItem.setTranslatableItem(product);

        product.setAbstractLocalizedItemList(Arrays.asList(localizedCurrencyItem));

        entityManager.persist(locale);
        entityManager.persist(currency);
        entityManager.persist(localizedCurrencyItem);
        entityManager.persist(product);

        localizedCurrencyItemDao = new LocalizedCurrencyItemDao();
        try {
            FieldUtils.writeField(localizedCurrencyItemDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testSave()
    {
        LocalizedCurrencyItem lci = ModelFactory.localizedCurrencyItem();
        localizedCurrencyItemDao.addEntity(lci);

        assertEquals(lci,
                entityManager.createQuery("Select l FROM LocalizedCurrencyItem l WHERE l.uuid =:uuid",
                        LocalizedCurrencyItem.class)
                        .setParameter("uuid", lci.getUuid() )
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        localizedCurrencyItem = entityManager.find(LocalizedCurrencyItem.class, localizedCurrencyItem.getId());
        localizedCurrencyItem.setPrice(15.0f);

        localizedCurrencyItemDao.updateEntity(localizedCurrencyItem);

        assertEquals(localizedCurrencyItem.getPrice(),
                entityManager.createQuery("SELECT l FROM LocalizedCurrencyItem l WHERE l.uuid =:uuid",
                        LocalizedCurrencyItem.class)
                        .setParameter("uuid", localizedCurrencyItem.getUuid())
                        .getSingleResult()
                        .getPrice(), 0);

    }

    @Test
    public void testDelete()
    {
        LocalizedCurrencyItem newLci = ModelFactory.localizedCurrencyItem();

        localizedCurrencyItemDao.addEntity(newLci);

        newLci = entityManager.find(LocalizedCurrencyItem.class, newLci.getId());
        localizedCurrencyItemDao.deleteEntity(newLci);
        LocalizedCurrencyItem retrievedResult = localizedCurrencyItemDao.getEntityById(newLci.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        LocalizedCurrencyItem retrievedResult = localizedCurrencyItemDao.getEntityById(localizedCurrencyItem.getId());

        assertEquals(localizedCurrencyItem.getId(), retrievedResult.getId());
        assertEquals(localizedCurrencyItem.getUuid(), retrievedResult.getUuid());
        assertEquals(localizedCurrencyItem.getPrice(), retrievedResult.getPrice(), 0);
        assertEquals(localizedCurrencyItem.getCurrency(), retrievedResult.getCurrency());
        assertEquals(localizedCurrencyItem.getTranslatableItem(), retrievedResult.getTranslatableItem());
        assertEquals(localizedCurrencyItem.getLocale(), retrievedResult.getLocale());
    }

    @Test
    public void testGetLocalizedCurrencyItemList()
    {
        List<LocalizedCurrencyItem> localizedCurrencyItemList;
        localizedCurrencyItemList = localizedCurrencyItemDao.getLocalizedCurrencyItemList();

        for (LocalizedCurrencyItem l : localizedCurrencyItemList) {
            assertEquals(l, localizedCurrencyItemDao.getEntityById(l.getId()));
        }
    }

    @Test
    public void testGetLocalizedCurrencyItemByProductId()
    {
        List<LocalizedCurrencyItem> localizedCurrencyItemList;
        localizedCurrencyItemList = localizedCurrencyItemDao.getLocalizedCurrencyItemByProductId(product.getId());

        assertEquals(localizedCurrencyItem.getId(), localizedCurrencyItemList.get(0).getId());
    }

    @Test
    public void testGetLocalizedCurrencyItemByLocaleId()
    {
        List<LocalizedCurrencyItem> localizedCurrencyItemList;
        localizedCurrencyItemList = localizedCurrencyItemDao.getLocalizedCurrencyItemByLocaleId(locale.getId());

        assertEquals(localizedCurrencyItem.getId(), localizedCurrencyItemList.get(0).getId());
    }

    @Test
    public void testGetLocalizedCurrencyItemByCurrency()
    {
        List<LocalizedCurrencyItem> localizedCurrencyItemList;
        localizedCurrencyItemList = localizedCurrencyItemDao.getLocalizedCurrencyItemByCurrencyId(currency.getId());

        assertEquals(localizedCurrencyItem.getId(), localizedCurrencyItemList.get(0).getId());
    }

    @Test
    public void testGetLocalizedCurrencyItemByProductAndLocale()
    {
        List<LocalizedCurrencyItem> localizedCurrencyItemList;
        localizedCurrencyItemList = localizedCurrencyItemDao.getLocalizedCurrencyItemByProductAndLocaleId(
                product.getId(), locale.getId());

        assertEquals(localizedCurrencyItem.getId(), localizedCurrencyItemList.get(0).getId());
    }

    @Test
    public void testGetLocalizedCurrencyItemByProductLocaleAndCurrency()
    {
        LocalizedCurrencyItem localizedCurrencyItemList;
        localizedCurrencyItemList = localizedCurrencyItemDao.getLocalizedCurrencyItemByProductLocaleAndCurrency(
                product.getId(), locale.getId(), currency.getId());

        assertEquals(localizedCurrencyItem.getId(), localizedCurrencyItemList.getId());
    }

}
