package it.unifi.ing.dao;

import it.unifi.ing.model.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PurchasedProductDaoTest extends JpaTest {

    private PurchasedProductDao purchasedProductDao;
    private PurchasedProduct purchasedProduct;
    private ShoppingList shoppingList;
    private Product product;

    @Override
    protected void init() throws InitializationError
    {
        purchasedProduct = ModelFactory.purchasedProduct();
        shoppingList = ModelFactory.shoppingList();
        product = ModelFactory.product();

        purchasedProduct.setProduct(product);
        purchasedProduct.setShoppingList(shoppingList);

        entityManager.persist(product);
        entityManager.persist(shoppingList);
        entityManager.persist(purchasedProduct);

        purchasedProductDao = new PurchasedProductDao();
        try {
            FieldUtils.writeField(purchasedProductDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testSave()
    {
        PurchasedProduct p = ModelFactory.purchasedProduct();
        purchasedProductDao.addEntity(p);

        assertEquals(p,
                entityManager.createQuery("Select p FROM PurchasedProduct p WHERE p.uuid =:uuid", PurchasedProduct.class)
                        .setParameter("uuid", p.getUuid())
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        purchasedProduct = entityManager.find(PurchasedProduct.class, purchasedProduct.getId());
        purchasedProduct.setShoppingList(shoppingList);

        purchasedProductDao.updateEntity(purchasedProduct);

        assertEquals(purchasedProduct.getShoppingList(),
                entityManager.createQuery("SELECT p FROM PurchasedProduct p WHERE p.uuid =:uuid", PurchasedProduct.class)
                        .setParameter("uuid", purchasedProduct.getUuid())
                        .getSingleResult()
                        .getShoppingList());
    }

    @Test
    public void testDelete()
    {
        PurchasedProduct newProduct = ModelFactory.purchasedProduct();

        purchasedProductDao.addEntity(newProduct);

        newProduct = entityManager.find(PurchasedProduct.class, newProduct.getId());
        purchasedProductDao.deleteEntity(newProduct);
        PurchasedProduct retrievedResult = purchasedProductDao.getEntityById(newProduct.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        PurchasedProduct retrievedResult = purchasedProductDao.getEntityById(purchasedProduct.getId());

        assertEquals(purchasedProduct.getId(), retrievedResult.getId());
        assertEquals(purchasedProduct.getUuid(), retrievedResult.getUuid());
        assertEquals(purchasedProduct.getShoppingList(), retrievedResult.getShoppingList());
        assertEquals(purchasedProduct.getProduct(), retrievedResult.getProduct());
    }
}
