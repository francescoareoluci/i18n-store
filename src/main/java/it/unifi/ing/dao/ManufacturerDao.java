package it.unifi.ing.dao;

import it.unifi.ing.model.Manufacturer;

import java.util.List;

public class ManufacturerDao extends BaseDao<Manufacturer> {

    private static final long serialVersionUID = 5886237024901911153L;

    public ManufacturerDao() { super(Manufacturer.class); }

    public List<Manufacturer> getManufacturerList()
    {
        return entityManager.createQuery("SELECT m FROM Manufacturer m", Manufacturer.class)
                .getResultList();
    }

    public Manufacturer getManufacturerByName(String name)
    {
        List<Manufacturer> result = entityManager.createQuery("SELECT m FROM Manufacturer m WHERE m.name = :name",
                Manufacturer.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

}
