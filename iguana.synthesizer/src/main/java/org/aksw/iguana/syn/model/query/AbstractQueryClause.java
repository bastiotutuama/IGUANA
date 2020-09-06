package org.aksw.iguana.syn.model.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractQueryClause implements QueryClause{

    public AbstractQueryClause(Query.QueryClauseType queryClauseType, String clauseString) {
        this.queryClauseType = queryClauseType;
        this.clauseString = clauseString;
    }

    private Query.QueryClauseType queryClauseType;
    private String clauseKeyword;
    private String clauseString;
    private ArrayList<String> clauseElements = new ArrayList<>();
    private Map<String, String> clauseElementAggregatorExpressions = new HashMap<>();


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

    @Override
    public String getClauseKeyword() {
        return clauseKeyword;
    }

    @Override
    public void setClauseKeyword(String clauseKeyword) {
        this.clauseKeyword = clauseKeyword;
    }

    public void addToClauseElements(List<String> newClauseElement) {
        clauseElements.addAll(newClauseElement);
    }

    public void addToClauseElements(String newClauseElement){
        clauseElements.add(newClauseElement);
    }

    public ArrayList<String> getClauseElements() {
        return clauseElements;
    }

    public Map<String, String> getClauseElementAggregatorExpressions() {
        return clauseElementAggregatorExpressions;
    }

    public void addToClauseElementAggregatorExpressions(String variable, String elementAggregatorExpression){
        clauseElementAggregatorExpressions.put(variable, elementAggregatorExpression);
    }
}
