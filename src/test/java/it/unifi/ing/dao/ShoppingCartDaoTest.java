package it.unifi.ing.dao;

import it.unifi.ing.model.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ShoppingCartDaoTest extends JpaTest {

    private ShoppingCartDao shoppingCartDao;
    private ShoppingCart shoppingCart;
    private ProductCart productCart;
    private Customer customer;

    @Override
    protected void init() throws InitializationError
    {
        shoppingCart = ModelFactory.shoppingCart();
        productCart = ModelFactory.productCart();
        customer = ModelFactory.customer();

        productCart.setShoppingCart(shoppingCart);
        productCart.setShoppingCart(shoppingCart);

        List<ProductCart> productCartList = new ArrayList<>();
        productCartList.add(productCart);

        shoppingCart.setCustomer(customer);
        shoppingCart.setProductCartList(productCartList);

        entityManager.persist(customer);
        entityManager.persist(shoppingCart);
        entityManager.persist(productCart);

        shoppingCartDao = new ShoppingCartDao();
        try {
            FieldUtils.writeField(shoppingCartDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testSave()
    {
        ShoppingCart s = ModelFactory.shoppingCart();
        shoppingCartDao.addEntity(s);

        assertEquals(s,
                entityManager.createQuery("Select s FROM ShoppingCart s WHERE s.uuid =:uuid", ShoppingCart.class)
                        .setParameter("uuid", s.getUuid())
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        shoppingCart = entityManager.find(ShoppingCart.class, shoppingCart.getId());
        shoppingCart.setCustomer(customer);

        shoppingCartDao.updateEntity(shoppingCart);

        assertEquals(shoppingCart.getCustomer(),
                entityManager.createQuery("SELECT s FROM ShoppingCart s WHERE s.uuid =:uuid", ShoppingCart.class)
                        .setParameter("uuid", shoppingCart.getUuid())
                        .getSingleResult()
                        .getCustomer());
    }

    @Test
    public void testDelete()
    {
        ShoppingCart newShoppingCart = ModelFactory.shoppingCart();

        shoppingCartDao.addEntity(newShoppingCart);

        newShoppingCart = entityManager.find(ShoppingCart.class, newShoppingCart.getId());
        shoppingCartDao.deleteEntity(newShoppingCart);
        ShoppingCart retrievedResult = shoppingCartDao.getEntityById(newShoppingCart.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        ShoppingCart retrievedResult = shoppingCartDao.getEntityById(shoppingCart.getId());

        assertEquals(shoppingCart.getId(), retrievedResult.getId());
        assertEquals(shoppingCart.getUuid(), retrievedResult.getUuid());
        assertEquals(shoppingCart.getCustomer(), retrievedResult.getCustomer());
        assertEquals(shoppingCart.getProductCartList().get(0), retrievedResult.getProductCartList().get(0));
    }
}
