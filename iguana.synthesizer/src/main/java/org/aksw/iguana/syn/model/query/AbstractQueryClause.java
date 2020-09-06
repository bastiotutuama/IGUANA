package org.aksw.iguana.syn.model.query;

public abstract class AbstractQueryClause implements QueryClause{

    public AbstractQueryClause(Query.QueryClauseType queryClauseType, String clauseString) {
        this.queryClauseType = queryClauseType;
        this.clauseString = clauseString;
    }

    private Query.QueryClauseType queryClauseType;
    private String clauseString;

    public void setQueryClauseType(Query.QueryClauseType queryClauseType) {
        this.queryClauseType = queryClauseType;
    }

    @Override
    public Query.QueryClauseType getClauseType() {
        return queryClauseType;
    }

    @Override
    public String getClauseString() {
        return clauseString;
    }

    @Override
    public void setClauseString(String clauseString) {
        this.clauseString = clauseString;
    }
}
