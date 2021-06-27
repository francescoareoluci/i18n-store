package it.unifi.ing.dao;

import it.unifi.ing.model.*;
import it.unifi.ing.translation.LocalizedField;
import it.unifi.ing.translation.LocalizedTextualItem;
import it.unifi.ing.translation.TranslatableField;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LocalizedTextualItemDaoTest extends JpaTest {

    private LocalizedTextualItemDao localizedTextualItemDao;
    private LocalizedTextualItem localizedTextualItem;
    private LocalizedField localizedField;
    private Product product;
    private Locale locale;

    @Override
    protected void init() throws InitializationError
    {
        locale = ModelFactory.locale();
        locale.setLanguageCode("it");
        locale.setCountryCode("IT");

        localizedField = ModelFactory.localizedField();
        localizedField.setType(TranslatableField.productName);

        product = ModelFactory.product();

        localizedTextualItem = ModelFactory.localizedTextualItem();
        localizedTextualItem.setLocalizedField(localizedField);
        localizedTextualItem.setText("testo1");
        localizedTextualItem.setLocale(locale);
        localizedTextualItem.setTranslatableItem(product);

        product.setAbstractLocalizedItemList(Arrays.asList(localizedTextualItem));

        entityManager.persist(locale);
        entityManager.persist(localizedField);
        entityManager.persist(localizedTextualItem);
        entityManager.persist(product);

        localizedTextualItemDao = new LocalizedTextualItemDao();
        try {
            FieldUtils.writeField(localizedTextualItemDao, "entityManager", entityManager, true);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
    }

    @Test
    public void testSave()
    {
        LocalizedTextualItem lti = ModelFactory.localizedTextualItem();
        localizedTextualItemDao.addEntity(lti);

        assertEquals(lti,
                entityManager.createQuery("Select l FROM LocalizedTextualItem l WHERE l.uuid =:uuid",
                        LocalizedTextualItem.class)
                        .setParameter("uuid", lti.getUuid() )
                        .getSingleResult());
    }

    @Test
    public void testUpdate()
    {
        localizedTextualItem = entityManager.find(LocalizedTextualItem.class, localizedTextualItem.getId());
        localizedTextualItem.setText("updName");

        localizedTextualItemDao.updateEntity(localizedTextualItem);

        assertEquals(localizedTextualItem.getText(),
                entityManager.createQuery("SELECT l FROM LocalizedTextualItem l WHERE l.uuid =:uuid",
                        LocalizedTextualItem.class)
                        .setParameter("uuid", localizedTextualItem.getUuid())
                        .getSingleResult()
                        .getText());

    }

    @Test
    public void testDelete()
    {
        LocalizedTextualItem newLti = ModelFactory.localizedTextualItem();

        localizedTextualItemDao.addEntity(newLti);

        newLti = entityManager.find(LocalizedTextualItem.class, newLti.getId());
        localizedTextualItemDao.deleteEntity(newLti);
        LocalizedTextualItem retrievedResult = localizedTextualItemDao.getEntityById(newLti.getId());

        assertNull(retrievedResult);
    }

    @Test
    public void testFindById()
    {
        LocalizedTextualItem retrievedResult = localizedTextualItemDao.getEntityById(localizedTextualItem.getId());

        assertEquals(localizedTextualItem.getId(), retrievedResult.getId());
        assertEquals(localizedTextualItem.getUuid(), retrievedResult.getUuid());
        assertEquals(localizedTextualItem.getText(), retrievedResult.getText());
        assertEquals(localizedTextualItem.getLocalizedField(), retrievedResult.getLocalizedField());
        assertEquals(localizedTextualItem.getTranslatableItem(), retrievedResult.getTranslatableItem());
        assertEquals(localizedTextualItem.getLocale(), retrievedResult.getLocale());
    }

    @Test
    public void testGetLocalizedTextualItemList()
    {
        List<LocalizedTextualItem> localizedTextualItemList;
        localizedTextualItemList = localizedTextualItemDao.getLocalizedTextualItemList();

        for (LocalizedTextualItem l : localizedTextualItemList) {
            assertEquals(l, localizedTextualItemDao.getEntityById(l.getId()));
        }
    }

    @Test
    public void testGetLocalizedTextualItemByProductId()
    {
        List<LocalizedTextualItem> localizedTextualItemList;
        localizedTextualItemList = localizedTextualItemDao.getLocalizedTextualItemByProductId(product.getId());

        assertEquals(localizedTextualItem.getId(), localizedTextualItemList.get(0).getId());
    }

    @Test
    public void testGetLocalizedTextualItemByLocaleId()
    {
        List<LocalizedTextualItem> localizedTextualItemList;
        localizedTextualItemList = localizedTextualItemDao.getLocalizedTextualItemByLocaleId(locale.getId());

        assertEquals(localizedTextualItem.getId(), localizedTextualItemList.get(0).getId());
    }

    @Test
    public void testGetLocalizedTextualItemByType()
    {
        List<LocalizedTextualItem> localizedTextualItemList;
        localizedTextualItemList = localizedTextualItemDao.getLocalizedTextualItemByFieldId(localizedField.getId());

        assertEquals(localizedTextualItem.getId(), localizedTextualItemList.get(0).getId());
    }

    @Test
    public void testGetLocalizedTextualItemByProductAndLocale()
    {
        List<LocalizedTextualItem> localizedTextualItemList;
        localizedTextualItemList = localizedTextualItemDao.getLocalizedTextualItemByProductAndLocaleId(
                product.getId(), locale.getId());

        assertEquals(localizedTextualItem.getId(), localizedTextualItemList.get(0).getId());
    }

    @Test
    public void testGetLocalizedTextualItemByProductLocaleAndType()
    {
        LocalizedTextualItem lti;
        lti = localizedTextualItemDao.getLocalizedTextualItemByProductLocaleAndType(
                product.getId(), locale.getId(), localizedField.getId());

        assertEquals(localizedTextualItem.getId(), lti.getId());
    }

}
