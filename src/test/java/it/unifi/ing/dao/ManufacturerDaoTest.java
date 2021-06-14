package it.unifi.ing.dao;

import it.unifi.ing.model.Manufacturer;
import it.unifi.ing.model.ModelFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ManufacturerDaoTest extends JpaTest {

    private ManufacturerDao manufacturerDao;
    private Manufacturer manufacturer;

    @Override
    protected void init() throws InitializationError
    {
        manufacturer = ModelFactory.manufacturer();
        manufacturer.setName("manufacturer1");

        entityManager.persist(manufacturer);

        manufacturerDao = new ManufacturerDao();
        try {
            FieldUtils.writeField(manufacturerDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testAdd()
    {
        Manufacturer newManufacturer = ModelFactory.manufacturer();
        newManufacturer.setName("manufacturer2");

        manufacturerDao.addEntity(newManufacturer);

        assertEquals(newManufacturer,
                entityManager.createQuery( "Select m FROM Manufacturer m WHERE m.uuid =:uuid", Manufacturer.class )
                        .setParameter("uuid", newManufacturer.getUuid() )
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        manufacturer = entityManager.find(Manufacturer.class, manufacturer.getId());
        manufacturer.setName("manufacturer3");

        manufacturerDao.updateEntity(manufacturer);

        assertEquals(manufacturer.getName(),
                entityManager.createQuery( "SELECT m FROM Manufacturer m WHERE m.uuid =:uuid", Manufacturer.class)
                        .setParameter("uuid", manufacturer.getUuid())
                        .getSingleResult()
                        .getName());
    }

    @Test
    public void testDelete()
    {
        Manufacturer newManufacturer = ModelFactory.manufacturer();
        newManufacturer.setName("manufacturer4");

        manufacturerDao.addEntity(newManufacturer);

        newManufacturer = entityManager.find(Manufacturer.class, newManufacturer.getId());
        manufacturerDao.deleteEntity(newManufacturer);
        Manufacturer retrievedResult = manufacturerDao.getEntityById(newManufacturer.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        Manufacturer retrievedResult = manufacturerDao.getEntityById(manufacturer.getId());

        assertEquals(manufacturer.getId(), retrievedResult.getId());
        assertEquals(manufacturer.getUuid(), retrievedResult.getUuid());
        assertEquals(manufacturer.getName(), retrievedResult.getName());
    }

    @Test
    public void testGetLocaleList()
    {
        List<Manufacturer> manufacturerList;
        manufacturerList = manufacturerDao.getManufacturerList();

        for (Manufacturer m : manufacturerList) {
            assertEquals(m, manufacturerDao.getEntityById(m.getId()));
        }
    }
}
