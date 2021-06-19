package it.unifi.ing.dao;

import java.util.List;

import it.unifi.ing.model.Customer;

public class CustomerDao extends BaseDao<Customer> {

    private static final long serialVersionUID = 1652487230448041907L;

    public CustomerDao() { super(Customer.class); }

    public List<Customer> getCustomerList()
    {
        return entityManager.createQuery("SELECT c FROM Customer c", Customer.class)
                .getResultList();
    }

    public Customer getUserByUsername(String username)
    {
        List<Customer> result = entityManager
                .createQuery("SELECT c FROM Customer c WHERE c.mail = :mail", Customer.class)
                .setParameter("mail", username)
                .setMaxResults(1)
                .getResultList();

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    // @TODO: this method its duplicated with admin, how to abstract it?
    public Customer login(Customer user)
    {
        List<Customer> result = entityManager
                .createQuery("SELECT c FROM Customer c WHERE c.mail = :email "
                        + "AND c.password = :pass", Customer.class)
                .setParameter("email", user.getMail())
                .setParameter("pass", user.getPassword())
                .setMaxResults(1)
                .getResultList();

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

}
