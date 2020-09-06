package org.aksw.iguana.syn.synthesizer.query.impl;

import org.aksw.iguana.syn.model.query.AbstractQueryClause;
import org.aksw.iguana.syn.model.query.Query;
import org.aksw.iguana.syn.model.query.impl.SparqlQuery;
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
                //System.out.println(jenaSparqlQueryFromFile.getQueryPattern().toString());

                System.out.println("------------------------------------------ " + "\n");

                System.out.println(sparqlQueryFromFile);

                System.out.println("TYPE CLAUSE");
                System.out.println("Keyword: " + sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.TYPE_CLAUSE).getClauseKeyword() + "\n");


                System.out.println("RESULT VARIABLES CLAUSE");
                List<String> clauseVars = ((AbstractQueryClause) sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.RESULT_VARIABLES_CLAUSE)).getClauseElements();
                System.out.println("result variables: " + Arrays.toString(clauseVars.toArray()));
                System.out.println("result variable expressions: " + ((AbstractQueryClause) sparqlQueryFromFile.getQueryClauseForType(Query.QueryClauseType.RESULT_VARIABLES_CLAUSE)).getClauseElementAggregatorExpressions() + "\n");

                System.out.println("PATTERN CLAUSE");
                System.out.println("triple paths:");
                int i = 1;
                for (String currentQueryPatternTriple:sparqlQueryFromFile.getQueryPatternTriples()) {
                    System.out.println(i + ": [" + currentQueryPatternTriple + "]");
                    i++;
                }
                System.out.println();

                allSynthesizableSparqlQueries.add(sparqlQueryFromFile);
            }

        }

        System.out.println("Number of SPARQL Queries in File: " + sparqlQueriesFromFileAsString.size());
        System.out.println("Number of thereof BQL-Synthesizable Queries: " + allSynthesizableSparqlQueries.size());

    }

    private static boolean sparqlQueryCanBeSynthesizedToBqlQuery(SparqlQuery sparqlQuery){

        if (sparqlQuery.getQueryType() == Query.QueryType.UNSUPPORTED)
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
