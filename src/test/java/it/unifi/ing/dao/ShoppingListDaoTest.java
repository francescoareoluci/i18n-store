package it.unifi.ing.dao;

import it.unifi.ing.model.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ShoppingListDaoTest extends JpaTest {

    private ShoppingListDao shoppingListDao;
    private ShoppingList shoppingList;
    private PurchasedProduct purchasedProduct;
    private Customer customer;

    @Override
    protected void init() throws InitializationError
    {
        shoppingList = ModelFactory.shoppingList();
        purchasedProduct = ModelFactory.purchasedProduct();
        customer = ModelFactory.customer();

        purchasedProduct.setShoppingList(shoppingList);
        purchasedProduct.setShoppingList(shoppingList);

        List<PurchasedProduct> purchasedProductList = new ArrayList<>();
        purchasedProductList.add(purchasedProduct);

        shoppingList.setCustomer(customer);
        shoppingList.setPurchasedProductList(purchasedProductList);

        entityManager.persist(customer);
        entityManager.persist(shoppingList);
        entityManager.persist(purchasedProduct);

        shoppingListDao = new ShoppingListDao();
        try {
            FieldUtils.writeField(shoppingListDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testSave()
    {
        ShoppingList s = ModelFactory.shoppingList();
        shoppingListDao.addEntity(s);

        assertEquals(s,
                entityManager.createQuery("Select s FROM ShoppingList s WHERE s.uuid =:uuid", ShoppingList.class)
                        .setParameter("uuid", s.getUuid())
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        shoppingList = entityManager.find(ShoppingList.class, shoppingList.getId());
        shoppingList.setCustomer(customer);

        shoppingListDao.updateEntity(shoppingList);

        assertEquals(shoppingList.getCustomer(),
                entityManager.createQuery("SELECT s FROM ShoppingList s WHERE s.uuid =:uuid", ShoppingList.class)
                        .setParameter("uuid", shoppingList.getUuid())
                        .getSingleResult()
                        .getCustomer());
    }

    @Test
    public void testDelete()
    {
        ShoppingList newShoppingList = ModelFactory.shoppingList();

        shoppingListDao.addEntity(newShoppingList);

        newShoppingList = entityManager.find(ShoppingList.class, newShoppingList.getId());
        shoppingListDao.deleteEntity(newShoppingList);
        ShoppingList retrievedResult = shoppingListDao.getEntityById(newShoppingList.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        ShoppingList retrievedResult = shoppingListDao.getEntityById(shoppingList.getId());

        assertEquals(shoppingList.getId(), retrievedResult.getId());
        assertEquals(shoppingList.getUuid(), retrievedResult.getUuid());
        assertEquals(shoppingList.getCustomer(), retrievedResult.getCustomer());
        assertEquals(shoppingList.getPurchasedProductList().get(0), retrievedResult.getPurchasedProductList().get(0));
    }
}
