package it.unifi.ing.controllers;

import it.unifi.ing.model.Product;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class SearchEngine {

    @PersistenceContext
    private EntityManager entityManager;

    public SearchEngine() {}

    public List<Product> searchProducts(String keyword)
    {
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

        // Hibernate Search DSL query
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Product.class).get();

        org.apache.lucene.search.Query query = qb
                .keyword()
                .onFields("localizedProductList.name", "localizedProductList.description")
                .matching(keyword)
                .createQuery();

        // Wrap Lucene query in a javax.persistence.Query
        javax.persistence.Query persistenceQuery =
                fullTextEntityManager.createFullTextQuery(query, Product.class);

        // Search
        return persistenceQuery.getResultList();

    }
}
