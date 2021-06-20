package it.unifi.ing.controllers;

import it.unifi.ing.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.apache.lucene.search.Query;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class SearchEngine {

    private static final Logger logger = LogManager.getLogger(CustomerEndpoint.class);

    @PersistenceContext
    private EntityManager entityManager;

    public SearchEngine() {}

    public List<Product> searchProducts(String keywords, boolean fuzzyMatch)
    {
        if (keywords == null || keywords.isEmpty()) {
            return null;
        }

        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

        // Hibernate Search DSL query
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Product.class).get();

        String finalQuery = keywords;

        logger.info("Searching for [" + finalQuery + "] inside Product entities");

        Query query = qb
                .keyword()
                .onFields("localizedProductList.name", "localizedProductList.description")
                .matching(finalQuery)
                .createQuery();

        if (fuzzyMatch) {
            query = qb
                    .keyword()
                        .fuzzy()
                            .withEditDistanceUpTo(2)
                            .withPrefixLength(2)
                    .onFields("localizedProductList.name", "localizedProductList.description")
                    .matching(finalQuery)
                    .createQuery();
        }

        // Wrap Lucene query in a javax.persistence.Query
        javax.persistence.Query persistenceQuery =
                fullTextEntityManager.createFullTextQuery(query, Product.class);

        // Search
        return persistenceQuery.getResultList();
    }
}
