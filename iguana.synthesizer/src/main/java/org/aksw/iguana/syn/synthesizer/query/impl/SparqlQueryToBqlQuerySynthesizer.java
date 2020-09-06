package org.aksw.iguana.syn.synthesizer.query.impl;

import org.aksw.iguana.syn.model.query.AbstractQueryClause;
import org.aksw.iguana.syn.model.query.Query;
import org.aksw.iguana.syn.model.query.QueryClause;
import org.aksw.iguana.syn.model.query.impl.SparqlQuery;
import org.aksw.iguana.syn.model.query.impl.SparqlQueryClause;
import org.aksw.iguana.syn.synthesizer.Synthesizer;
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

    public static void main(String[] args) {

        String queryInputFilePathAsString = "/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Queries/swdf_queries.txt";
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
            //System.out.println(jenaSparqlQueryFromFile.serialize(Syntax.syntaxSPARQL));

            SparqlQuery sparqlQueryFromFile = null;
            if (jenaSparqlQueryFromFile != null && jenaSparqlQueryTypeIsSupported(jenaSparqlQueryFromFile)){
                    sparqlQueryFromFile = new SparqlQuery(jenaSparqlQueryFromFile);
            }

            if (sparqlQueryFromFile != null && sparqlQueryCanBeSynthesizedToBqlQuery(sparqlQueryFromFile)) {
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
                for (String currentQueryPatternTriple:sparqlQueryFromFile.getQueryPatternTriples()) {
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

    private static boolean jenaSparqlQueryTypeIsSupported(org.apache.jena.query.Query jenaSparqlQuery){
        for (int i = 0; i<SparqlQuery.getSupportedJenaQueryTypes().length; i++){
            if ( SparqlQuery.getSupportedJenaQueryTypes()[i] == jenaSparqlQuery.queryType())
                return true;
        }
        return false;
    }

}
