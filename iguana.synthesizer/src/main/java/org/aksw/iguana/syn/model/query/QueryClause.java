package org.aksw.iguana.syn.model.query;

public interface QueryClause {

    public Query.QueryClauseType getClauseType();

    public String getClauseString();

    public void setClauseString(String clauseString);

}
