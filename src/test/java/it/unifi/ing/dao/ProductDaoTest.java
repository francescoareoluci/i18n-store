package it.unifi.ing.dao;

import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import it.unifi.ing.model.Admin;
import it.unifi.ing.model.Manufacturer;
import it.unifi.ing.model.ModelFactory;
import it.unifi.ing.model.Product;

public class ProductDaoTest extends JpaTest {

    private ProductDao productDao;
    private Product product;
    private Manufacturer manufacturer;
    private Admin admin;

    @Override
    protected void init() throws InitializationError
    {
        product = ModelFactory.product();
        manufacturer = ModelFactory.manufacturer();
        admin = ModelFactory.admin();

        product.setProdAdministrator(admin);
        product.setProdManufacturer(manufacturer);

        entityManager.persist(manufacturer);
        entityManager.persist(admin);
        entityManager.persist(product);

        productDao = new ProductDao();
        try {
            FieldUtils.writeField(productDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testSave()
    {
        Product p = ModelFactory.product();
        productDao.addEntity(p);

        assertEquals(p,
                entityManager.createQuery("Select p FROM Product p WHERE p.uuid =:uuid", Product.class)
                        .setParameter("uuid", p.getUuid() )
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        product = entityManager.find(Product.class, product.getId());

        productDao.updateEntity(product);

        assertEquals(product.getProdManufacturer(),
                entityManager.createQuery("SELECT p FROM Product p WHERE p.uuid =:uuid", Product.class)
                        .setParameter("uuid", product.getUuid())
                        .getSingleResult()
                        .getProdManufacturer());


    }

    @Test
    public void testDelete()
    {
        Product newProduct = ModelFactory.product();

        productDao.addEntity(newProduct);

        newProduct = entityManager.find(Product.class, newProduct.getId());
        productDao.deleteEntity(newProduct);
        Product retrievedResult = productDao.getEntityById(newProduct.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        Product retrievedResult = productDao.getEntityById(product.getId());

        assertEquals(product.getId(), retrievedResult.getId());
        assertEquals(product.getUuid(), retrievedResult.getUuid());
        assertEquals(product.getProdManufacturer(), retrievedResult.getProdManufacturer());
        assertEquals(product.getProdAdministrator(), retrievedResult.getProdAdministrator());
    }

    @Test
    public void testGetProductList()
    {
        List<Product> productList;
        productList = productDao.getProductList();

        for (Product p : productList) {
            assertEquals(p, productDao.getEntityById(p.getId()));
        }
    }

    @Test
    public void testFindByManufacturerId()
    {
        assertEquals(product, productDao.getProductsByManufacturerId(manufacturer.getId()).get(0));
    }

    @Test
    public void testFindByAdminId()
    {
        assertEquals(product, productDao.getProductsByAdminId(admin.getId()).get(0));
    }

}
