package it.unifi.ing.dao;

import java.util.List;

import it.unifi.ing.model.Product;

public class ProductDao extends BaseDao<Product> {

    private static final long serialVersionUID = -1274999582269299645L;

    public ProductDao() { super(Product.class); }

    public List<Product> getProductList()
    {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class)
                .getResultList();
    }

    public List<Product> getProductsByManufacturerId(Long manufacturerId)
    {
        return entityManager
                .createQuery("SELECT p from Product p where p.prodManufacturer.id = :id", Product.class)
                .setParameter("id", manufacturerId)
                .getResultList();
    }

    public List<Product> getProductsByAdminId(Long adminId)
    {
        return entityManager
                .createQuery("SELECT p FROM Product p WHERE p.prodAdministrator.id = :id", Product.class)
                .setParameter("id", adminId)
                .getResultList();
    }

}
