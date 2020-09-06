package org.aksw.iguana.syn.model.query;

import java.util.Map;

public interface Query {

    public enum QueryLanguage {
        SPARQL,
        BQL
    }

    public enum QueryType {
        SELECT,
        CONSTRUCT,
        UNSUPPORTED
    }

    public enum QueryClauseType {
        TYPE_CLAUSE,
        VARIABLES_CLAUSE,
        SOURCE_GRAPHS_CLAUSE,
        DESTINATION_GRAPHS_CLAUSE,
        PATTERN_CLAUSE,
        RESULTS_ORDER_CLAUSE,
        RESULTS_GROUP_CLAUSE,
        RESULT_HAVING_CLAUSE,
        RESULT_LIMIT_CLAUSE
    }

    public QueryLanguage getQueryLanguage();

    public QueryType getQueryType();

    public String getQueryAsString();

    public Map<QueryClauseType, QueryClause> getQueryClauses();

    public QueryClause getQueryClauseForType(QueryClauseType queryClauseType);

    public void setQueryClauseForType(QueryClause queryClause);

}
