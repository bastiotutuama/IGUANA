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
    private ArrayList<String> clauseVariables = new ArrayList<>();
    private Map<String, String> clauseVariableAggregatorExpressions = new HashMap<>();


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

    public void addToClauseVariables(List<String> newClauseVariable) {
        clauseVariables.addAll(newClauseVariable);
    }

    public void addToClauseVariables(String newClauseVariable){
        clauseVariables.add(newClauseVariable);
    }

    public ArrayList<String> getClauseVariables() {
        return clauseVariables;
    }

    public Map<String, String> getClauseVariableAggregatorExpressions() {
        return clauseVariableAggregatorExpressions;
    }

    public void addToClauseVariableAggregatorExpressions(String variable, String variableAggregatorExpression){
        clauseVariableAggregatorExpressions.put(variable, variableAggregatorExpression);
    }
}
