package it.unifi.ing.beans.builders;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@Startup
@DependsOn("StartupBean")
public class SearchBean {

    @PersistenceContext
    private EntityManager entityManager;

    public SearchBean() {}

    @PostConstruct
    public void init() {
        // Initialize entities indexing
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
