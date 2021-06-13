package it.unifi.ing.dao;

import it.unifi.ing.model.Locale;
import it.unifi.ing.model.LocalizedProduct;
import it.unifi.ing.model.ModelFactory;
import it.unifi.ing.model.Product;
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

    @Override
    protected void init() throws InitializationError
    {
        locale = ModelFactory.locale();
        locale.setLanguageCode("it");
        locale.setCountryCode("IT");

        product = ModelFactory.product();

        localizedProduct = ModelFactory.localizedProduct();
        localizedProduct.setProductName("nome1");
        localizedProduct.setProductDescription("desc1");
        localizedProduct.setProductCategory("categoria1");
        localizedProduct.setProductCurrency("euro");
        localizedProduct.setProductPrice((float)12.34);
        localizedProduct.setProduct(product);
        localizedProduct.setLocale(locale);

        List<LocalizedProduct> localizedProductList  = new ArrayList<>();
        localizedProductList.add(localizedProduct);

        product.setLocalizedProductList(localizedProductList);

        entityManager.persist(locale);
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
        localizedProduct.setProductName("nome2");

        translationDao.updateEntity(localizedProduct);

        assertEquals(localizedProduct.getProductName(),
                entityManager.createQuery("SELECT p FROM LocalizedProduct p WHERE p.uuid =:uuid", LocalizedProduct.class)
                        .setParameter("uuid", localizedProduct.getUuid())
                        .getSingleResult()
                        .getProductName());

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
        assertEquals(localizedProduct.getProductName(), retrievedResult.getProductName());
        assertEquals(localizedProduct.getProductDescription(), retrievedResult.getProductDescription());
        assertEquals(localizedProduct.getProductCategory(), retrievedResult.getProductCategory());
        assertEquals(localizedProduct.getProductCurrency(), retrievedResult.getProductCurrency());
        assertEquals(localizedProduct.getProductPrice(), retrievedResult.getProductPrice(), 0);
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
        localizedProductList = translationDao.getTranslationsByName(localizedProduct.getProductName());

        assertEquals(localizedProduct.getId(), localizedProductList.get(0).getId());
    }

    @Test
    public void testGetTranslationsByCategory()
    {
        List<LocalizedProduct> localizedProductList;
        localizedProductList = translationDao.getTranslationsByCategory(localizedProduct.getProductCategory());

        assertEquals(localizedProduct.getId(), localizedProductList.get(0).getId());
    }

}
