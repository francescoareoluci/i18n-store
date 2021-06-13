package it.unifi.ing.dao;

import it.unifi.ing.model.Locale;

import java.util.List;

public class LocaleDao extends BaseDao<Locale> {

    private static final long serialVersionUID = 5886237024901911153L;

    public LocaleDao() { super(Locale.class); }

    public List<Locale> getLocaleList()
    {
        List<Locale> retrievedEntities = entityManager.createQuery("SELECT l FROM Locale l", Locale.class)
                .getResultList();

        return retrievedEntities;
    }

}
