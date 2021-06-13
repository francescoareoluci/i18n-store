package it.unifi.ing.dao;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import it.unifi.ing.model.*;

public class AdminDaoTest extends JpaTest {

    private AdminDao adminDao;
    private Admin admin;

    @Override
    protected void init() throws InitializationError
    {
        admin = ModelFactory.admin();
        admin.setMail("user@example.com");
        admin.setPassword("mysecretpass");

        entityManager.persist(admin);

        adminDao = new AdminDao();
        try {
            FieldUtils.writeField(adminDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testAdd()
    {
        Admin admin = ModelFactory.admin();
        admin.setMail("user2@example.com");
        admin.setPassword("anothersecretpass");

        adminDao.addEntity(admin);

        assertEquals(admin,
                entityManager.createQuery( "Select a FROM Admin a WHERE a.uuid =:uuid", Admin.class )
                        .setParameter("uuid", admin.getUuid() )
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        admin = entityManager.find(Admin.class, admin.getId());
        admin.setMail("changed@example.com");

        adminDao.updateEntity(admin);

        assertEquals(admin.getMail(),
                entityManager.createQuery( "SELECT a FROM Admin a WHERE a.uuid =:uuid", Admin.class)
                        .setParameter("uuid", admin.getUuid())
                        .getSingleResult()
                        .getMail());
    }

    @Test
    public void testDelete()
    {
        Admin newUser = ModelFactory.admin();
        newUser.setMail("user3@example.com");
        newUser.setPassword("anothersecretpass2");

        adminDao.addEntity(newUser);

        newUser = entityManager.find(Admin.class, newUser.getId());
        adminDao.deleteEntity(newUser);
        Admin retrievedResult = adminDao.getEntityById(newUser.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        Admin retrievedResult = adminDao.getEntityById(admin.getId());

        assertEquals(admin.getId(), retrievedResult.getId());
        assertEquals(admin.getUuid(), retrievedResult.getUuid());
        assertEquals(admin.getMail(), retrievedResult.getMail());
        assertEquals(admin.getPassword(), retrievedResult.getPassword());
    }

    @Test
    public void testGetAdminList()
    {
        List<Admin> adminList;
        adminList = adminDao.getAdminList();

        for (Admin a : adminList) {
            assertEquals(a, adminDao.getEntityById(a.getId()));
        }
    }

    @Test
    public void testLogin()
    {
        Admin newUser = ModelFactory.admin();
        newUser.setMail("user@example.com");
        newUser.setPassword("mysecretpass");

        assertEquals(admin, adminDao.login(newUser));
    }

    @Test
    public void testLoginWrongCredentials()
    {
        Admin newUser = ModelFactory.admin();
        newUser.setMail("user2@example.com");
        newUser.setPassword("mywrongpass");

        assertNull(adminDao.login(newUser));
    }
}
