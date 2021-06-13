package it.unifi.ing.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

public abstract class BaseDao<E> implements Serializable {

    private static final long serialVersionUID = 687375512411827564L;

    @PersistenceContext
    protected EntityManager entityManager;

    protected final Class<E> type;

    public BaseDao(Class<E> type) { this.type = type; }

    public E getEntityById(Long id) { return entityManager.find(type, id); }

    public void addEntity(E entity) { entityManager.persist(entity); }

    public void updateEntity(E entity) { entityManager.merge(entity); }

    public void deleteEntity(E entity) { entityManager.remove(entity); }
}
