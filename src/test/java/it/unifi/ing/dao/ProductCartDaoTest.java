package it.unifi.ing.dao;

import it.unifi.ing.model.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProductCartDaoTest extends JpaTest {

    private ProductCartDao productCartDao;
    private ProductCart productCart;
    private ShoppingCart shoppingCart;
    private Product product;

    @Override
    protected void init() throws InitializationError
    {
        productCart = ModelFactory.productCart();
        shoppingCart = ModelFactory.shoppingCart();
        product = ModelFactory.product();

        productCart.setProduct(product);
        productCart.setShoppingCart(shoppingCart);

        entityManager.persist(product);
        entityManager.persist(shoppingCart);
        entityManager.persist(productCart);

        productCartDao = new ProductCartDao();
        try {
            FieldUtils.writeField(productCartDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testSave()
    {
        ProductCart p = ModelFactory.productCart();
        productCartDao.addEntity(p);

        assertEquals(p,
                entityManager.createQuery("Select p FROM ProductCart p WHERE p.uuid =:uuid", ProductCart.class)
                        .setParameter("uuid", p.getUuid() )
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        productCart = entityManager.find(ProductCart.class, productCart.getId());
        productCart.setShoppingCart(shoppingCart);

        productCartDao.updateEntity(productCart);

        assertEquals(productCart.getShoppingCart(),
                entityManager.createQuery("SELECT p FROM ProductCart p WHERE p.uuid =:uuid", ProductCart.class)
                        .setParameter("uuid", productCart.getUuid())
                        .getSingleResult()
                        .getShoppingCart());

    }

    @Test
    public void testDelete()
    {
        ProductCart newProduct = ModelFactory.productCart();

        productCartDao.addEntity(newProduct);

        newProduct = entityManager.find(ProductCart.class, newProduct.getId());
        productCartDao.deleteEntity(newProduct);
        ProductCart retrievedResult = productCartDao.getEntityById(newProduct.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        ProductCart retrievedResult = productCartDao.getEntityById(productCart.getId());

        assertEquals(productCart.getId(), retrievedResult.getId());
        assertEquals(productCart.getUuid(), retrievedResult.getUuid());
        assertEquals(productCart.getShoppingCart(), retrievedResult.getShoppingCart());
        assertEquals(productCart.getProduct(), retrievedResult.getProduct());
    }
}
