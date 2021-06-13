package it.unifi.ing.dao;

import java.util.List;

import it.unifi.ing.model.Admin;

public class AdminDao extends BaseDao<Admin> {

    private static final long serialVersionUID = -5801192557049009408L;

    public AdminDao() { super(Admin.class); }

    // @TODO: this method its duplicated with customer, how to abstract it?
    public Admin login(Admin user) {
        List<Admin> result = entityManager
                .createQuery( "SELECT a FROM Admin a WHERE a.mail = :email "
                        + "AND a.password = :pass", Admin.class)
                .setParameter( "email", user.getMail() )
                .setParameter( "pass", user.getPassword() )
                .setMaxResults( 1 )
                .getResultList();

        if ( result.isEmpty() ) {
            return null;
        }

        return result.get(0);
    }

    public List<Admin> getAdminList()
    {
        return entityManager.createQuery("SELECT a FROM Admin a", Admin.class)
                .getResultList();
    }

}
