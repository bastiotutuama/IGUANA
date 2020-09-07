package org.aksw.iguana.syn.synthesizer.query.impl;

import org.aksw.iguana.syn.model.query.AbstractQueryClause;
import org.aksw.iguana.syn.model.query.Query;
import org.aksw.iguana.syn.model.query.QueryClause;
import org.aksw.iguana.syn.model.query.impl.BqlQuery;
import org.aksw.iguana.syn.model.query.impl.BqlQueryClause;
import org.aksw.iguana.syn.model.query.impl.SparqlQuery;
import org.aksw.iguana.syn.model.query.impl.SparqlQueryClause;
import org.aksw.iguana.syn.model.statement.impl.RDFNtripleStatement;
import org.aksw.iguana.syn.synthesizer.Synthesizer;
import org.aksw.iguana.syn.synthesizer.statement.impl.RDFNtripleStatementToBadwolfStatementSythesizer;
import org.apache.jena.query.QueryFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SparqlQueryToBqlQuerySynthesizer implements Synthesizer {

    private static boolean debugOutPutEnabled = true;

    public static void main(String[] args) {

        String queryInputFilePathAsString = "/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Queries/swdf_queries.txt";
        String bqlTargetGraphName = "swdf";
        Path queryInputFilePath = Paths.get(queryInputFilePathAsString);

        ArrayList<String> sparqlQueriesFromFileAsString = new ArrayList<>();

        try {
            Files.lines(queryInputFilePath).forEach(new Consumer<String>() {
                @Override
                public void accept(String queryFromFile) {
                    sparqlQueriesFromFileAsString.add(queryFromFile);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<SparqlQuery> allSynthesizableSparqlQueries = new ArrayList<>();

        for (String currentSparqlQueryFromFileAsString:sparqlQueriesFromFileAsString) {

            org.apache.jena.query.Query jenaSparqlQueryFromFile = QueryFactory.create(currentSparqlQueryFromFileAsString);
            SparqlQuery sparqlQueryFromFile = new SparqlQuery(jenaSparqlQueryFromFile);

            if (sparqlQueryCanBeSynthesizedToBqlQuery(sparqlQueryFromFile)) {

                BqlQuery synthesizedBqlQuery = synthesizeBqlQueryFromSparqlQuery(sparqlQueryFromFile, bqlTargetGraphName);
                String bqlQueryString = synthesizedBqlQuery.getQueryAsString();

                if (debugOutPutEnabled){
                    System.out.println(sparqlQueryFromFile);
                    //printDebugOutputForSparqlQuery(sparqlQueryFromFile);
                    System.out.println("SYNTHESIZED BQL QUERY");
                    System.out.println(bqlQueryString + "\n");
                }

                allSynthesizableSparqlQueries.add(sparqlQueryFromFile);
            }

        }

        System.out.println("Number of SPARQL Queries in File: " + sparqlQueriesFromFileAsString.size());
        System.out.println("Number of thereof BQL-Synthesizable Queries: " + allSynthesizableSparqlQueries.size());

    }

    private static boolean sparqlQueryCanBeSynthesizedToBqlQuery(SparqlQuery sparqlQuery){

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

    private static void printDebugOutputForSparqlQuery(SparqlQuery sparqlQueryFromFile){
        System.out.println("------------------------------------------ " + "\n");
        System.out.println(sparqlQueryFromFile);

        System.out.println("TYPE CLAUSE");
        System.out.println("Keyword: " + sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.TYPE_CLAUSE).getClauseKeyword() + "\n");


        System.out.println("RESULT VARIABLES CLAUSE");
        List<String> clauseVars = ((AbstractQueryClause) sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.RESULT_VARIABLES_CLAUSE)).getClauseElements();
        System.out.println("Result variables: " + Arrays.toString(clauseVars.toArray()));
        System.out.println("Result variable expressions: " + ((AbstractQueryClause) sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.RESULT_VARIABLES_CLAUSE)).getClauseElementAggregatorExpressions() + "\n");

        System.out.println("PATTERN CLAUSE");
        System.out.println("Triples:");
        int i = 1;
        for (String currentQueryPatternTriple:sparqlQueryFromFile.getQueryPatternTriplesAsRdfNtripleString()) {
            System.out.println(i + ": [" + currentQueryPatternTriple + "]");
            i++;
        }
        System.out.println();

        if (sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.RESULTS_ORDER_CLAUSE) != null) {
            System.out.println("ORDER BY CLAUSE");
            QueryClause orderByClause = sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.RESULTS_ORDER_CLAUSE);
            System.out.println(((AbstractQueryClause) orderByClause).getClauseSortConditions());
            System.out.println();
        }

        if (sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.RESULTS_GROUP_CLAUSE) != null) {
            System.out.println("GROUP BY CLAUSE");
            QueryClause groupByClause = sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.RESULTS_GROUP_CLAUSE);
            System.out.println(((AbstractQueryClause) groupByClause).getClauseElements());
            System.out.println();
        }

        if (sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.RESULT_LIMIT_CLAUSE) != null) {
            System.out.println("LIMIT CLAUSE");
            QueryClause groupByClause = sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.RESULT_LIMIT_CLAUSE);
            System.out.println("Limit amount: " + ((AbstractQueryClause) groupByClause).getClauseSpecificationAmount());
            System.out.println();
        }
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
