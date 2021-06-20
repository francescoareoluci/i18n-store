package it.unifi.ing.dao;

import it.unifi.ing.model.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TranslationDaoTest extends JpaTest {

    private TranslationDao translationDao;
    private LocalizedProduct localizedProduct;
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
        currency.setCurrency("€");

        product = ModelFactory.product();

        localizedProduct = ModelFactory.localizedProduct();
        localizedProduct.setName("nome1");
        localizedProduct.setDescription("desc1");
        localizedProduct.setCategory("categoria1");
        localizedProduct.setCurrency(currency);
        localizedProduct.setPrice((float)12.34);
        localizedProduct.setProduct(product);
        localizedProduct.setLocale(locale);

        List<LocalizedProduct> localizedProductList  = new ArrayList<>();
        localizedProductList.add(localizedProduct);

        product.setLocalizedProductList(localizedProductList);

        entityManager.persist(locale);
        entityManager.persist(currency);
        entityManager.persist(localizedProduct);
        entityManager.persist(product);

        translationDao = new TranslationDao();
        try {
            FieldUtils.writeField(translationDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testSave()
    {
        LocalizedProduct p = ModelFactory.localizedProduct();
        translationDao.addEntity(p);

        assertEquals(p,
                entityManager.createQuery("Select p FROM LocalizedProduct p WHERE p.uuid =:uuid", LocalizedProduct.class)
                        .setParameter("uuid", p.getUuid() )
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        localizedProduct = entityManager.find(LocalizedProduct.class, localizedProduct.getId());
        localizedProduct.setName("nome2");

        translationDao.updateEntity(localizedProduct);

        assertEquals(localizedProduct.getName(),
                entityManager.createQuery("SELECT p FROM LocalizedProduct p WHERE p.uuid =:uuid", LocalizedProduct.class)
                        .setParameter("uuid", localizedProduct.getUuid())
                        .getSingleResult()
                        .getName());

    }

    @Test
    public void testDelete()
    {
        LocalizedProduct newProduct = ModelFactory.localizedProduct();

        translationDao.addEntity(newProduct);

        newProduct = entityManager.find(LocalizedProduct.class, newProduct.getId());
        translationDao.deleteEntity(newProduct);
        LocalizedProduct retrievedResult = translationDao.getEntityById(newProduct.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        LocalizedProduct retrievedResult = translationDao.getEntityById(localizedProduct.getId());

        assertEquals(localizedProduct.getId(), retrievedResult.getId());
        assertEquals(localizedProduct.getUuid(), retrievedResult.getUuid());
        assertEquals(localizedProduct.getName(), retrievedResult.getName());
        assertEquals(localizedProduct.getDescription(), retrievedResult.getDescription());
        assertEquals(localizedProduct.getCategory(), retrievedResult.getCategory());
        assertEquals(localizedProduct.getCurrency(), retrievedResult.getCurrency());
        assertEquals(localizedProduct.getPrice(), retrievedResult.getPrice(), 0);
        assertEquals(localizedProduct.getProduct(), retrievedResult.getProduct());
        assertEquals(localizedProduct.getLocale(), retrievedResult.getLocale());
    }

    @Test
    public void testGetLocalizedProductList()
    {
        List<LocalizedProduct> localizedProductList;
        localizedProductList = translationDao.getLocalizedProductList();

        for (LocalizedProduct p : localizedProductList) {
            assertEquals(p, translationDao.getEntityById(p.getId()));
        }
    }

    @Test
    public void testGetTranslationsByProductId()
    {
        List<LocalizedProduct> localizedProductList;
        localizedProductList = translationDao.getTranslationsByProductId(product.getId());

        assertEquals(localizedProduct.getId(), localizedProductList.get(0).getId());
    }

    @Test
    public void testGetTranslationsByLocaleId()
    {
        List<LocalizedProduct> localizedProductList;
        localizedProductList = translationDao.getTranslationsByLocaleId(locale.getId());

        assertEquals(localizedProduct.getId(), localizedProductList.get(0).getId());
    }

    @Test
    public void testGetTranslationsByName()
    {
        List<LocalizedProduct> localizedProductList;
        localizedProductList = translationDao.getTranslationsByName(localizedProduct.getName());

        assertEquals(localizedProduct.getId(), localizedProductList.get(0).getId());
    }

    @Test
    public void testGetTranslationsByCategory()
    {
        List<LocalizedProduct> localizedProductList;
        localizedProductList = translationDao.getTranslationsByCategory(localizedProduct.getCategory());

        assertEquals(localizedProduct.getId(), localizedProductList.get(0).getId());
    }

}
