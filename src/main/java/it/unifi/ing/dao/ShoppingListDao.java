package it.unifi.ing.dao;

import it.unifi.ing.model.ShoppingList;

public class ShoppingListDao extends BaseDao<ShoppingList> {

    private static final long serialVersionUID = 8460970852601231377L;

    public ShoppingListDao() { super(ShoppingList.class); }

}