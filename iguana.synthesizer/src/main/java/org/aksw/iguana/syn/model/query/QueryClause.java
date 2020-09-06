package org.aksw.iguana.syn.model.query;

public interface QueryClause {

    public enum SortOrder{ASC,DESC};

    public Query.QueryClauseType getClauseType();

    public void setClauseString(String clauseString);

    public String getClauseKeyword();

    public void setClauseKeyword(String clauseString);

}
