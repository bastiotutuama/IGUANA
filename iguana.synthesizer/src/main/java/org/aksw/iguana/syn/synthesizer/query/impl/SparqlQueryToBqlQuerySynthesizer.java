package org.aksw.iguana.syn.synthesizer.query.impl;

import org.aksw.iguana.syn.model.query.Query;
import org.aksw.iguana.syn.model.query.impl.BqlQuery;
import org.aksw.iguana.syn.model.query.impl.BqlQueryClause;
import org.aksw.iguana.syn.model.query.impl.SparqlQuery;
import org.aksw.iguana.syn.model.query.impl.SparqlQueryClause;
import org.aksw.iguana.syn.model.statement.impl.RDFNtripleStatement;
import org.aksw.iguana.syn.synthesizer.Synthesizer;
import org.aksw.iguana.syn.synthesizer.statement.impl.RDFNtripleStatementToBadwolfStatementSythesizer;

public class SparqlQueryToBqlQuerySynthesizer implements Synthesizer {

    public static boolean sparqlQueryCanBeSynthesizedToBqlQuery(SparqlQuery sparqlQuery){

        //TODO Reactivate and implement CONSTRUCT-Query Synthesization, when adequate.
        if (sparqlQuery.getQueryType() == Query.QueryType.UNSUPPORTED || sparqlQuery.getQueryType() == Query.QueryType.CONSTRUCT)
            return false;

        if (sparqlQuery.queryPatternContainsFilterElement())
            return false;

        if (sparqlQuery.queryPatternContainsOptionalElement())
            return false;

        if (sparqlQuery.queryPatternContainsUnionElement())
            return false;

        //TO REVIEW
        if (sparqlQuery.queryPatternContainsNamedGraphElement())
            return false;

        if (sparqlQuery.resultVariableExpressionsContainAnAggregatorExpressionDifferentFromCountandSumOrANonAggratorExpression())
            return false;

        return true;
    }

    public static BqlQuery synthesizeBqlQueryFromSparqlQuery(SparqlQuery sparqlQuery, String sourceGraphName){
        BqlQuery bqlQuery = new BqlQuery(sparqlQuery.getQueryType());

        //TYPE CLAUSE
        BqlQueryClause typeClause = new BqlQueryClause(Query.QueryClauseType.TYPE_CLAUSE);
        typeClause.setClauseKeyword(sparqlQuery.getQueryClauseForType(Query.QueryClauseType.TYPE_CLAUSE).getClauseKeyword());
        bqlQuery.addQueryClause(typeClause);

        //RESULT VARIABLES CLAUSE
        BqlQueryClause resultVariablesClause = new BqlQueryClause(Query.QueryClauseType.RESULT_VARIABLES_CLAUSE);
        resultVariablesClause.addToClauseElements(((SparqlQueryClause) sparqlQuery.getQueryClauseForType(Query.QueryClauseType.RESULT_VARIABLES_CLAUSE)).getClauseElements());
        resultVariablesClause.addToClauseElementAggregatorExpressions(((SparqlQueryClause) sparqlQuery.getQueryClauseForType(Query.QueryClauseType.RESULT_VARIABLES_CLAUSE)).getClauseElementAggregatorExpressions());
        bqlQuery.addQueryClause(resultVariablesClause);

        //SOURCE GRAPHS CLAUSE
        BqlQueryClause sourceGraphsClause = new BqlQueryClause(Query.QueryClauseType.SOURCE_GRAPHS_CLAUSE);
        sourceGraphsClause.setClauseKeyword("FROM");
        sourceGraphsClause.addToClauseElements(sourceGraphName);
        bqlQuery.addQueryClause(sourceGraphsClause);

        //PATTERN CLAUSE
        BqlQueryClause patternClause = new BqlQueryClause(Query.QueryClauseType.PATTERN_CLAUSE);
        patternClause.setClauseKeyword("WHERE");
        for (RDFNtripleStatement rdfNtripleStatement:sparqlQuery.getJenaQueryPatternTripleStatements()){
            String synthesizedBqlQueryPatternStatement = RDFNtripleStatementToBadwolfStatementSythesizer.synthesizeBadwolfStatementFromRDFStatement(rdfNtripleStatement).getCompleteStatementWithoutFullStop();
            patternClause.addToClauseElements(synthesizedBqlQueryPatternStatement);
        }
        //patternClause.addToClauseElements(sparqlQuery.getQueryPatternTriplesAsRdfNtripleString());
        bqlQuery.addQueryClause(patternClause);

        //ORDER BY CLAUSE
        if (sparqlQuery.getQueryClauseForType(Query.QueryClauseType.RESULTS_ORDER_CLAUSE) != null) {
            BqlQueryClause orderByClause = new BqlQueryClause(Query.QueryClauseType.RESULTS_ORDER_CLAUSE);
            orderByClause.setClauseKeyword("ORDER BY");
            orderByClause.addToClauseSortConditions(((SparqlQueryClause) sparqlQuery.getQueryClauseForType(Query.QueryClauseType.RESULTS_ORDER_CLAUSE)).getClauseSortConditions());
            bqlQuery.addQueryClause(orderByClause);
        }

        //GROUP BY CLAUSE
        if (sparqlQuery.getQueryClauseForType(Query.QueryClauseType.RESULTS_GROUP_CLAUSE) != null) {
            BqlQueryClause groupByClause = new BqlQueryClause(Query.QueryClauseType.RESULTS_GROUP_CLAUSE);
            groupByClause.setClauseKeyword("GROUP BY");
            groupByClause.addToClauseElements(((SparqlQueryClause) sparqlQuery.getQueryClauseForType(Query.QueryClauseType.RESULTS_GROUP_CLAUSE)).getClauseElements());
            bqlQuery.addQueryClause(groupByClause);
        }

        //LIMIT CLAUSE
        if (sparqlQuery.getQueryClauseForType(Query.QueryClauseType.RESULT_LIMIT_CLAUSE) != null) {
            BqlQueryClause limitClause = new BqlQueryClause(Query.QueryClauseType.RESULT_LIMIT_CLAUSE);
            limitClause.setClauseKeyword("LIMIT");
            limitClause.setClauseSpecificationAmount(((SparqlQueryClause) sparqlQuery.getQueryClauseForType(Query.QueryClauseType.RESULT_LIMIT_CLAUSE)).getClauseSpecificationAmount());
            bqlQuery.addQueryClause(limitClause);
        }

        return bqlQuery;
    }

}
