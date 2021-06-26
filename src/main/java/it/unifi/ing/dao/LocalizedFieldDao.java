package it.unifi.ing.dao;

import it.unifi.ing.translation.LocalizedField;

import java.util.List;

public class LocalizedFieldDao extends BaseDao<LocalizedField> {

    private static final long serialVersionUID = 5886237024901911153L;

    public LocalizedFieldDao() { super(LocalizedField.class); }

    public List<LocalizedField> getLocalizedFieldList()
    {
        return entityManager.createQuery("SELECT l FROM LocalizedField l", LocalizedField.class)
                .getResultList();
    }

    public LocalizedField getLocalizedFieldByType(String type)
    {
        return entityManager
                .createQuery("SELECT l FROM LocalizedField l WHERE l.type = :type ",
                    LocalizedField.class)
                .setParameter("type", type)
                .getSingleResult();
    }
}
