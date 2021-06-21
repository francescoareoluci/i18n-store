package it.unifi.ing.dao;

import it.unifi.ing.model.*;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CartProductDaoTest extends JpaTest {

    private CartProductDao cartProductDao;
    private CartProduct cartProduct;
    private ShoppingCart shoppingCart;
    private Product product;

    @Override
    protected void init() throws InitializationError
    {
        cartProduct = ModelFactory.cartProduct();
        shoppingCart = ModelFactory.shoppingCart();
        product = ModelFactory.product();

        cartProduct.setProduct(product);
        cartProduct.setShoppingCart(shoppingCart);

        entityManager.persist(product);
        entityManager.persist(shoppingCart);
        entityManager.persist(cartProduct);

        cartProductDao = new CartProductDao();
        try {
            FieldUtils.writeField(cartProductDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testSave()
    {
        CartProduct p = ModelFactory.cartProduct();
        cartProductDao.addEntity(p);

        assertEquals(p,
                entityManager.createQuery("Select p FROM CartProduct p WHERE p.uuid =:uuid", CartProduct.class)
                        .setParameter("uuid", p.getUuid() )
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        cartProduct = entityManager.find(CartProduct.class, cartProduct.getId());
        cartProduct.setShoppingCart(shoppingCart);

        cartProductDao.updateEntity(cartProduct);

        assertEquals(cartProduct.getShoppingCart(),
                entityManager.createQuery("SELECT p FROM CartProduct p WHERE p.uuid =:uuid", CartProduct.class)
                        .setParameter("uuid", cartProduct.getUuid())
                        .getSingleResult()
                        .getShoppingCart());

    }

    @Test
    public void testDelete()
    {
        CartProduct newProduct = ModelFactory.cartProduct();

        cartProductDao.addEntity(newProduct);

        newProduct = entityManager.find(CartProduct.class, newProduct.getId());
        cartProductDao.deleteEntity(newProduct);
        CartProduct retrievedResult = cartProductDao.getEntityById(newProduct.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        CartProduct retrievedResult = cartProductDao.getEntityById(cartProduct.getId());

        assertEquals(cartProduct.getId(), retrievedResult.getId());
        assertEquals(cartProduct.getUuid(), retrievedResult.getUuid());
        assertEquals(cartProduct.getShoppingCart(), retrievedResult.getShoppingCart());
        assertEquals(cartProduct.getProduct(), retrievedResult.getProduct());
    }
}
