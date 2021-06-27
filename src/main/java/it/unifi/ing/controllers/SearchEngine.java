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

    private static final Logger logger = LogManager.getLogger(SearchEngine.class);
    private static final int defaultEditDistance = 1;
    private static final int defaultPrefixLength = 2;
    private static final int defaultSlopFactor = 1;

    public enum SearchType {
        STANDARD,
        STANDARD_AND,
        FUZZY,
        PHRASE
    }

    @PersistenceContext
    private EntityManager entityManager;

    public SearchEngine() {}

    public List<Product> searchProducts(String userQuery, SearchType searchType)
    {
        if (userQuery == null || userQuery.isEmpty()) {
            return null;
        }

        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

        // Hibernate Search DSL query
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Product.class).get();

        logger.info("Searching for [" + userQuery + "] inside Product entities");

        Query query;
        switch (searchType) {
            case STANDARD:
                query = createStandardQuery(qb, userQuery);
                break;
            case STANDARD_AND:
                query = createANDStandardQuery(qb, userQuery);
                break;
            case FUZZY:
                query = createFuzzyQuery(qb, userQuery, defaultEditDistance, defaultPrefixLength);
                break;
            case PHRASE:
                query = createPhraseQuery(qb, userQuery, defaultSlopFactor);
                break;
            default:
                query = createStandardQuery(qb, userQuery);
                break;
        }

        // Wrap Lucene query in a javax.persistence.Query
        javax.persistence.Query persistenceQuery =
                fullTextEntityManager.createFullTextQuery(query, Product.class);

        // Search
        return persistenceQuery.getResultList();
    }

    public List<Product> searchSimilarProducts(int entityId)
    {
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

        // Hibernate Search DSL query
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Product.class).get();

        logger.debug("Searching for products similar to the one with id: " + entityId);

        Query query = createMLTQuery(qb, entityId);

        // Wrap Lucene query in a javax.persistence.Query
        javax.persistence.Query persistenceQuery =
                fullTextEntityManager.createFullTextQuery(query, Product.class);

        // Search
        return persistenceQuery.getResultList();
    }

    /**
     * @implSpec Create a standard query. Search in each field for each keyword in mathQuery.
     *          Will return entities which contains at least a specified keyword
     * @param qb: QueryBuilder
     * @param matchQuery: Set of words to be matched
     * @return Final query
     */
    private Query createStandardQuery(QueryBuilder qb, String matchQuery)
    {
        return qb
                .simpleQueryString()
                .onFields("localizedItemList.text")
                .matching(matchQuery)
                .createQuery();
    }

    /**
     * @implSpec Create a standard query. Search in each field for each keyword in mathQuery.
     *          Will return entities which contains all the specified keywords
     * @param qb: QueryBuilder
     * @param matchQuery: Set of words to be matched
     * @return Final query
     */
    private Query createANDStandardQuery(QueryBuilder qb, String matchQuery)
    {
        return qb
                .simpleQueryString()
                .onFields("localizedItemList.text")
                .withAndAsDefaultOperator()
                .matching(matchQuery)
                .createQuery();
    }

    /**
     * @implSpec Create a fuzzy query. Search in each field for each keyword in mathQuery.
     *          Will return entities which contains at least a specified keyword using the
     *          specified editDistance and the prefixLength
     * @param qb: QueryBuilder
     * @param matchQuery: Set of words to be matched
     * @param editDistance: Edit distance to be used in fuzzy matching
     * @param prefixLength: Length of the prefix ignore by the "fuzzyness"
     * @return Final query
     */
    private Query createFuzzyQuery(QueryBuilder qb, String matchQuery, int editDistance, int prefixLength)
    {
        return qb
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(editDistance)
                .withPrefixLength(prefixLength)
                .onFields("localizedItemList.text")
                .matching(matchQuery)
                .createQuery();
    }

    /**
     * @implSpec Create a phrase query. Search exact or approximate sentences. Approximate
     *          search can be specified by passing a non-zero slop factor
     * @param qb: QueryBuilder
     * @param matchQuery: Phrase to be matched
     * @param slop: Number of other words permitted in the sentence
     * @return Final query
     */
    private Query createPhraseQuery(QueryBuilder qb, String matchQuery, int slop)
    {
        if (slop < 0) {
            logger.debug("Requested a negative slop value in phrase query creation. Setting it to zero");
            slop = 0;
        }

        return qb
                .phrase()
                    .withSlop(slop)
                .onField("localizedItemList.text")
                .sentence(matchQuery)
                .createQuery();
    }

    /**
     * @implSpec Create a More Like This query. This query will search for entities
     *              similar to the one with identifier objectId
     * @param qb: QueryBuilder
     * @param objectId: Entity identifier
     * @return Final query
     */
    private Query createMLTQuery(QueryBuilder qb, int objectId)
    {
        return qb
                .moreLikeThis()
                .excludeEntityUsedForComparison()
                .favorSignificantTermsWithFactor(2f)
                .comparingField("localizedItemList.text")
                .toEntityWithId(objectId)
                .createQuery();
    }
}
