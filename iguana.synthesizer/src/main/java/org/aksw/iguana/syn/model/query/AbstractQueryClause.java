package org.aksw.iguana.syn.model.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractQueryClause implements QueryClause{

    public AbstractQueryClause(Query.QueryClauseType queryClauseType) {
        this.queryClauseType = queryClauseType;
    }

    public AbstractQueryClause(Query.QueryClauseType queryClauseType, String clauseString) {
        this.queryClauseType = queryClauseType;
        this.clauseString = clauseString;
    }

    private Query.QueryClauseType queryClauseType;
    private String clauseKeyword = "";
    private String clauseString = "";
    private long clauseSpecificationAmount;
    private ArrayList<String> clauseElements = new ArrayList<>();
    private Map<String, String> clauseElementAggregatorExpressions = new HashMap<>();
    private Map<String, SortOrder> clauseSortConditions = new HashMap<>();

    public void setQueryClauseType(Query.QueryClauseType queryClauseType) {
        this.queryClauseType = queryClauseType;
    }

    @Override
    public Query.QueryClauseType getClauseType() {
        return queryClauseType;
    }

    @Override
    public String toString() {
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

    public long getClauseSpecificationAmount() {
        return clauseSpecificationAmount;
    }

    public void setClauseSpecificationAmount(long clauseSpecificationAmount) {
        this.clauseSpecificationAmount = clauseSpecificationAmount;
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

    public void addToClauseElementAggregatorExpressions(String element, String elementAggregatorExpression){
        clauseElementAggregatorExpressions.put(element, elementAggregatorExpression);
    }

    public void addToClauseElementAggregatorExpressions(Map<String, String> newClauseElementAggregatorExpressions){
        clauseElementAggregatorExpressions.putAll(newClauseElementAggregatorExpressions);
    }

    public void addToClauseSortConditions(String variable, SortOrder sortOrder){
        clauseSortConditions.put(variable, sortOrder);
    }

    public void addToClauseSortConditions( Map<String, SortOrder> newClauseSortConditions){
        clauseSortConditions.putAll(newClauseSortConditions);
    }

    public Map<String, SortOrder> getClauseSortConditions() {
        return clauseSortConditions;
    }
}
