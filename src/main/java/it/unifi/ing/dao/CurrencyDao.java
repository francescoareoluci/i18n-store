package it.unifi.ing.dao;

import it.unifi.ing.model.Currency;

import java.util.List;

public class CurrencyDao extends BaseDao<Currency> {

    private static final long serialVersionUID = -1099380611071853261L;

    public CurrencyDao() { super(Currency.class); }

    public List<Currency> getCurrencyList()
    {
        return entityManager.createQuery("SELECT c FROM Currency c", Currency.class)
                .getResultList();
    }

}
