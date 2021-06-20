package it.unifi.ing.dao;

import it.unifi.ing.model.Currency;
import it.unifi.ing.model.ModelFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CurrencyDaoTest extends JpaTest {

    private CurrencyDao currencyDao;
    private Currency currency;

    @Override
    protected void init() throws InitializationError
    {
        currency = ModelFactory.currency();
        currency.setCurrency("$");

        entityManager.persist(currency);

        currencyDao = new CurrencyDao();
        try {
            FieldUtils.writeField(currencyDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testAdd()
    {
        Currency newCurrency = ModelFactory.currency();
        newCurrency.setCurrency("€");

        currencyDao.addEntity(newCurrency);

        assertEquals(newCurrency,
                entityManager.createQuery( "Select c FROM Currency c WHERE c.uuid =:uuid", Currency.class )
                        .setParameter("uuid", newCurrency.getUuid() )
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        currency = entityManager.find(Currency.class, currency.getId());
        currency.setCurrency("£");

        currencyDao.updateEntity(currency);

        assertEquals(currency.getCurrency(),
                entityManager.createQuery( "Select c FROM Currency c WHERE c.uuid =:uuid", Currency.class)
                        .setParameter("uuid", currency.getUuid())
                        .getSingleResult()
                        .getCurrency());
    }

    @Test
    public void testDelete()
    {
        Currency newCurrency = ModelFactory.currency();
        newCurrency.setCurrency("¥");

        currencyDao.addEntity(newCurrency);

        newCurrency = entityManager.find(Currency.class, newCurrency.getId());
        currencyDao.deleteEntity(newCurrency);
        Currency retrievedResult = currencyDao.getEntityById(newCurrency.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        Currency retrievedResult = currencyDao.getEntityById(currency.getId());

        assertEquals(currency.getId(), retrievedResult.getId());
        assertEquals(currency.getUuid(), retrievedResult.getUuid());
        assertEquals(currency.getCurrency(), retrievedResult.getCurrency());
    }

    @Test
    public void testGetLocaleList()
    {
        List<Currency> currencyList;
        currencyList = currencyDao.getCurrencyList();

        for (Currency c : currencyList) {
            assertEquals(c, currencyDao.getEntityById(c.getId()));
        }
    }
}
