package org.aksw.iguana.syn.model.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractQuery implements Query {

    private QueryType queryType;
    private HashMap<QueryClauseType, QueryClause> queryClauses = new HashMap<>();


    @Override
    public String toString() {
        return getQueryAsString();
    }

    @Override
    public String getQueryAsString() {
        StringBuilder queryStringBuilder = new StringBuilder();

        ArrayList<QueryClauseType> queryClauseTypes = new ArrayList<>(Arrays.asList(
                QueryClauseType.TYPE_CLAUSE,
                QueryClauseType.RESULT_VARIABLES_CLAUSE,
                QueryClauseType.SOURCE_GRAPHS_CLAUSE,
                QueryClauseType.DESTINATION_GRAPHS_CLAUSE,
                QueryClauseType.PATTERN_CLAUSE,
                QueryClauseType.RESULTS_ORDER_CLAUSE,
                QueryClauseType.RESULTS_GROUP_CLAUSE,
                QueryClauseType.RESULT_HAVING_CLAUSE,
                QueryClauseType.RESULT_LIMIT_CLAUSE
            )
        );

        for (QueryClauseType currentQueryClauseType:queryClauseTypes) {
            queryStringBuilder.append(getQueryClauseForType(currentQueryClauseType));
        }

        return queryStringBuilder.toString();
    }

    @Override
    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    @Override
    public Map<QueryClauseType, QueryClause> getQueryClauses() {
        return queryClauses;
    }

    @Override
    public void setQueryClauseForType(QueryClause queryClause) {
        this.queryClauses.put(queryClause.getClauseType(), queryClause);
    }

    @Override
    public QueryClause getQueryClauseForType(QueryClauseType queryClauseType) {
        if (this.queryClauses.containsKey(queryClauseType)){
            return this.queryClauses.get(queryClauseType);
        }
        return null;
    }
}
