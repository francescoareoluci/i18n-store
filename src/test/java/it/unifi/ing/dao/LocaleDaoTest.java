package it.unifi.ing.dao;

import it.unifi.ing.model.Locale;
import it.unifi.ing.model.ModelFactory;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LocaleDaoTest extends JpaTest {

    private LocaleDao localeDao;
    private Locale locale;

    @Override
    protected void init() throws InitializationError
    {
        locale = ModelFactory.locale();
        locale.setCountryCode("GB");
        locale.setLanguageCode("en");

        entityManager.persist(locale);

        localeDao = new LocaleDao();
        try {
            FieldUtils.writeField(localeDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testAdd()
    {
        Locale newLocale = ModelFactory.locale();
        locale.setCountryCode("IT");
        locale.setLanguageCode("it");

        localeDao.addEntity(newLocale);

        assertEquals(newLocale,
                entityManager.createQuery( "Select l FROM Locale l WHERE l.uuid =:uuid", Locale.class )
                        .setParameter("uuid", newLocale.getUuid() )
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        locale = entityManager.find(Locale.class, locale.getId());
        locale.setCountryCode("EN");

        localeDao.updateEntity(locale);

        assertEquals(locale.getCountryCode(),
                entityManager.createQuery( "SELECT l FROM Locale l WHERE l.uuid =:uuid", Locale.class)
                        .setParameter("uuid", locale.getUuid())
                        .getSingleResult()
                        .getCountryCode());
    }

    @Test
    public void testDelete()
    {
        Locale newLocale = ModelFactory.locale();
        newLocale.setCountryCode("es");
        newLocale.setLanguageCode("ES");

        localeDao.addEntity(newLocale);

        newLocale = entityManager.find(Locale.class, newLocale.getId());
        localeDao.deleteEntity(newLocale);
        Locale retrievedResult = localeDao.getEntityById(newLocale.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        Locale retrievedResult = localeDao.getEntityById(locale.getId());

        assertEquals(locale.getId(), retrievedResult.getId());
        assertEquals(locale.getUuid(), retrievedResult.getUuid());
        assertEquals(locale.getCountryCode(), retrievedResult.getCountryCode());
        assertEquals(locale.getLanguageCode(), retrievedResult.getLanguageCode());
    }

    @Test
    public void testGetLocaleList()
    {
        List<Locale> localeList;
        localeList = localeDao.getLocaleList();

        for (Locale l : localeList) {
            assertEquals(l, localeDao.getEntityById(l.getId()));
        }
    }
}
