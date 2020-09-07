package org.aksw.iguana.syn.model.query.impl;

import org.aksw.iguana.syn.model.query.AbstractQuery;
import org.aksw.iguana.syn.model.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class BqlQuery extends AbstractQuery implements Query {

    private String bqlQuery;

    public BqlQuery(QueryType queryType) {
        setQueryType(queryType);
    }

    public BqlQuery(String bqlQuery, QueryType queryType) {
        this.bqlQuery = bqlQuery;
        setQueryType(queryType);
    }

    @Override
    public QueryLanguage getQueryLanguage() {
        return QueryLanguage.BQL;
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

        for (QueryClauseType currentQueryClauseType : queryClauseTypes) {

            BqlQueryClause currentBqlQueryClause = (BqlQueryClause) getQueryClauseForType(currentQueryClauseType);

            if (currentBqlQueryClause != null) {
                queryStringBuilder.append(currentBqlQueryClause.getClauseKeyword());

                if (currentBqlQueryClause.getClauseKeyword().length() > 0)
                    queryStringBuilder.append(" ");

                switch (currentQueryClauseType) {
                    case RESULT_VARIABLES_CLAUSE:
                        Iterator<String> resultVariablesIterator = currentBqlQueryClause.getClauseElements().iterator();
                        while (resultVariablesIterator.hasNext()){
                            String resultVariable = resultVariablesIterator.next();

                            if (currentBqlQueryClause.getClauseElementAggregatorExpressions().containsKey(resultVariable)) {
                                queryStringBuilder.append(currentBqlQueryClause.getClauseElementAggregatorExpressions().get(resultVariable));
                                queryStringBuilder.append(" AS ");
                            }
                            queryStringBuilder.append("?");
                            queryStringBuilder.append(resultVariable);

                            if (resultVariablesIterator.hasNext())
                                queryStringBuilder.append(", ");
                        }
                        queryStringBuilder.append(" ");
                        break;

                    case SOURCE_GRAPHS_CLAUSE:
                        Iterator<String> sourceGraphsIterator = currentBqlQueryClause.getClauseElements().iterator();
                        while (sourceGraphsIterator.hasNext()){
                            String sourceGraph = sourceGraphsIterator.next();

                            queryStringBuilder.append("?");
                            queryStringBuilder.append(sourceGraph);

                            if (sourceGraphsIterator.hasNext())
                                queryStringBuilder.append(", ");
                        }
                        queryStringBuilder.append(" ");
                        break;

                    case DESTINATION_GRAPHS_CLAUSE:
                        break;

                    case PATTERN_CLAUSE:
                        Iterator<String> patternStatementsIterator = currentBqlQueryClause.getClauseElements().iterator();
                        queryStringBuilder.append("{ ");
                        while (patternStatementsIterator.hasNext()){
                            String patternStatement = patternStatementsIterator.next();

                            queryStringBuilder.append(patternStatement);

                            if (patternStatementsIterator.hasNext())
                                queryStringBuilder.append(" . ");
                        }
                        queryStringBuilder.append(" } ");
                        break;

                    case RESULTS_ORDER_CLAUSE:
                        break;
                    case RESULTS_GROUP_CLAUSE:
                        break;
                    case RESULT_HAVING_CLAUSE:
                        break;
                    case RESULT_LIMIT_CLAUSE:
                        break;

                }

            }

        }


        return queryStringBuilder.toString();
    }
}
