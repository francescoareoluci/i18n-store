package it.unifi.ing.dao;

import it.unifi.ing.model.*;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

public class CustomerDaoTest extends JpaTest {

    private CustomerDao customerDao;
    private Customer user;

    @Override
    protected void init() throws InitializationError
    {
        user = ModelFactory.customer();
        user.setMail("user@example.com");
        user.setPassword("mysecretpass");

        entityManager.persist(user);

        customerDao = new CustomerDao();
        try {
            FieldUtils.writeField(customerDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testAdd()
    {
        Customer customer = ModelFactory.customer();
        customer.setMail("user2@example.com");
        customer.setPassword("anothersecretpass");

        customerDao.addEntity(customer);

        assertEquals(customer,
                entityManager.createQuery( "Select c FROM Customer c WHERE c.uuid =:uuid", Customer.class )
                        .setParameter("uuid", customer.getUuid() )
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        user = entityManager.find(Customer.class, user.getId());
        user.setMail("changed@example.com");

        customerDao.updateEntity(user);

        assertEquals(user.getMail(),
                entityManager.createQuery( "SELECT c FROM Customer c WHERE c.uuid =:uuid", Customer.class)
                        .setParameter("uuid", user.getUuid())
                        .getSingleResult()
                        .getMail());
    }

    @Test
    public void testDelete()
    {
        Customer newCustomer = ModelFactory.customer();
        newCustomer.setMail("user3@example.com");
        newCustomer.setPassword("anothersecretpass2");

        customerDao.addEntity(newCustomer);

        newCustomer = entityManager.find(Customer.class, newCustomer.getId());
        customerDao.deleteEntity(newCustomer);
        Customer retrievedResult = customerDao.getEntityById(newCustomer.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        Customer retrievedResult = customerDao.getEntityById(user.getId());

        assertEquals(user.getId(), retrievedResult.getId());
        assertEquals(user.getUuid(), retrievedResult.getUuid());
        assertEquals(user.getMail(), retrievedResult.getMail());
        assertEquals(user.getPassword(), retrievedResult.getPassword());
    }

    @Test
    public void testGetCustomerList()
    {
        List<Customer> customerList;
        customerList = customerDao.getCustomerList();

        for (Customer c : customerList) {
            assertEquals(c, customerDao.getEntityById(c.getId()));
        }
    }

    @Test
    public void testLogin()
    {
        Customer newUser = ModelFactory.customer();
        newUser.setMail("user@example.com");
        newUser.setPassword("mysecretpass");

        assertEquals(user, customerDao.login(newUser));
    }

    @Test
    public void testLoginWrongCredentials()
    {
        Customer newUser = ModelFactory.customer();
        newUser.setMail("user2@example.com");
        newUser.setPassword("mywrongpass");

        assertNull(customerDao.login(newUser));
    }

}