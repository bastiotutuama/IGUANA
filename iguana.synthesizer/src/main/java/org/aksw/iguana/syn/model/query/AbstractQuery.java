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
    public void addQueryClause(QueryClause queryClause) {
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
