package org.aksw.iguana.syn.controller;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.aksw.iguana.di.http.BadwolfHttpImporter;
import org.aksw.iguana.syn.model.query.AbstractQueryClause;
import org.aksw.iguana.syn.model.query.Query;
import org.aksw.iguana.syn.model.query.QueryClause;
import org.aksw.iguana.syn.model.query.impl.BqlQuery;
import org.aksw.iguana.syn.model.query.impl.SparqlQuery;
import org.aksw.iguana.syn.model.statement.AbstractStatement;
import org.aksw.iguana.syn.model.statement.Statement;
import org.aksw.iguana.syn.model.statement.impl.RDFNtripleStatement;
import org.aksw.iguana.syn.synthesizer.statement.impl.RDFNtripleStatementToBadwolfStatementSythesizer;
import org.aksw.iguana.syn.util.FileParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.aksw.iguana.syn.synthesizer.query.impl.SparqlQueryToBqlQuerySynthesizer.sparqlQueryCanBeSynthesizedToBqlQuery;
import static org.aksw.iguana.syn.synthesizer.query.impl.SparqlQueryToBqlQuerySynthesizer.synthesizeBqlQueryFromSparqlQuery;

public class MainController {

    private static final String defaultSynthesizationSelection = "RDF-NTRIPLE_TO_BADWOLF_STATEMENTS";

    private static final String defaultOutputMethod = "endpoint";

    // /Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Queries/swdf_queries.sparql
    // /home/otutuama/datasets/swdfu8.nt
    private static final String defaultInputFilePath = "/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8_5000.nt";

    //"http://localhost:1234/bql";
    private static final String defaultEndpointAdress = "http://131.234.29.241:1234/bql";

    private static final String defaultGraphName = "swdf";

    //"/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8_5000.bql"
    private static final String defaultOutputFilePath = "/home/otutuama/datasets/swdfu8.bql";

    private static final int defaultChunkSize = 5; //maxmium chunk-size for http POST on Badwolf: 25000

    private static boolean debugOutPutEnabled = true;


    public static void main(String [] args) {

        String synthesizationSelection;
        String outputMethod;
        String inputFilePath;
        String graphName;
        String endpointAdress;
        String outputFilePath;
        int chunkSize;

        synthesizationSelection     = setVariableFromInputArguments(args, 0, defaultSynthesizationSelection);
        outputMethod                = setVariableFromInputArguments(args, 1, defaultOutputMethod);
        inputFilePath               = setVariableFromInputArguments(args, 2, defaultInputFilePath);
        endpointAdress              = setVariableFromInputArguments(args, 3, defaultEndpointAdress);
        outputFilePath              = setVariableFromInputArguments(args, 3, defaultOutputFilePath);
        graphName                   = setVariableFromInputArguments(args, 4, defaultGraphName);
        chunkSize                   = Integer.parseInt(setVariableFromInputArguments(args, 5, String.valueOf(defaultChunkSize)));

        switch (synthesizationSelection) {

            case "RDF-NTRIPLE_TO_BADWOLF_STATEMENTS":

                ArrayList<Statement> rdfNtripleStatements = FileParser.readInStatementsFromFile(inputFilePath, AbstractStatement.StatementLanguage.RDF_NTRIPLE);

                if (outputMethod.equals("endpoint")) {
                    synthesizeRdfNtripleStatmentListToBqlAndLoadToBqlEndpointAsChunks(rdfNtripleStatements, graphName, endpointAdress, chunkSize, inputFilePath);
                } else if (outputMethod.equals("file")) {
                    synthesizeRdfNtripleStatmentListToBadwolfStatementsAndWriteToFile(rdfNtripleStatements, outputFilePath);
                } else {
                    System.out.println("Output-Method not recognized");
                }

                break;

            case "SPARQL_TO_BQL_QUERIES":

                ArrayList<SparqlQuery> sparqlQueriesFromFile = FileParser.readInSparqlQueriesFromFile(inputFilePath);

                if (outputMethod.equals("file")) {
                    synthesizeSparqlQueryListListToBqlQueriesAndWriteToFile(sparqlQueriesFromFile, graphName, outputFilePath);
                } else {
                    System.out.println("Output-Method not recognized");
                }

                break;

            default:
                System.out.println("Synthesization-Selection not supported");
                break;
        }


    }

    private static String setVariableFromInputArguments(String[] arguments, int argumentIndex, String defaultValue){
        if(arguments.length >= argumentIndex + 1 && !arguments[argumentIndex].isEmpty()) {
            return arguments[argumentIndex];
        } else {
            return defaultValue;
        }
    }

    private static void writeListOfStringsToFile(ArrayList<String> listOfStrings, String outputFilePath) {
        Path outputFile = Paths.get(outputFilePath);
        try {
            Files.write(outputFile, listOfStrings, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void synthesizeRdfNtripleStatmentListToBadwolfStatementsAndWriteToFile(ArrayList<Statement> rdfNtripleStatements, String outputFilePath){
        ArrayList<String> synthesizedBadwolfStatements = new ArrayList<>();
        ArrayList<String> synthesizedBadwolfStatementsAsBqlInsert = RDFNtripleStatementToBadwolfStatementSythesizer.generateBQLInsertQueryFromRDFNtripleStatements("GRAPH_NAME", rdfNtripleStatements);
        for (Statement rdfNtripleStatement : rdfNtripleStatements) {

            if (debugOutPutEnabled) {
                System.out.println("RDF-NTriple Statement");
                System.out.println(rdfNtripleStatement.getCompleteStatement() + "\n");
            }

            String synthesizedBadwolfStatement = RDFNtripleStatementToBadwolfStatementSythesizer.synthesizeBadwolfStatementFromRDFStatement((RDFNtripleStatement) rdfNtripleStatement).getCompleteStatement();

            if (debugOutPutEnabled) {
                System.out.println("Synthesized Badwolf Statement");
                System.out.println(synthesizedBadwolfStatement  + "\n");
            }

            synthesizedBadwolfStatements.add(synthesizedBadwolfStatement);
        }
        String outputFilePathForCorrespondingBqlInserts = outputFilePath.replace(".badwolf", "_bql_insert-queries.bql");
        writeListOfStringsToFile(synthesizedBadwolfStatements, outputFilePath);
        writeListOfStringsToFile(synthesizedBadwolfStatementsAsBqlInsert, outputFilePathForCorrespondingBqlInserts);
    }

    private static void synthesizeRdfNtripleStatmentListToBqlAndLoadToBqlEndpointAsChunks(ArrayList<Statement> rdfNtripleStatements, String graphName, String endpointAdress, int chunkSize, String inputFilePath){
        String outputFilePathForSynthesizedAndInsertRdfNtripleStatements = inputFilePath.replace(".nt", "_synthesized-and-inserted.nt");
        BufferedWriter synthesizedAndInsertRdfNtripleStatementsOutputWriter = null;
        try {
            synthesizedAndInsertRdfNtripleStatementsOutputWriter = new BufferedWriter(new FileWriter(outputFilePathForSynthesizedAndInsertRdfNtripleStatements, true));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ArrayList<String> currentBqlInsertQueriesChunk;
        int i = 0;
        while (i<rdfNtripleStatements.size()){
            currentBqlInsertQueriesChunk = RDFNtripleStatementToBadwolfStatementSythesizer.generateBQLInsertQueryFromRDFNtripleStatements(graphName, rdfNtripleStatements.subList(i, i + chunkSize));
            ArrayList<String> currentRdfStatementsFromChunkAsString = new ArrayList<>();
            for (Statement rdfNtripleStatement:rdfNtripleStatements.subList(i, i + chunkSize)) {
                currentRdfStatementsFromChunkAsString.add(rdfNtripleStatement.getCompleteStatement());
            }
            sendBqlInsertQueryListToBqlEndpoint(endpointAdress, currentBqlInsertQueriesChunk, currentRdfStatementsFromChunkAsString, synthesizedAndInsertRdfNtripleStatementsOutputWriter);
            i+=chunkSize;
            //System.out.println("Approximate load-progress:" + i  * 100 / rdfNtripleStatements.size() + "%");
        }
    }

    private static void sendBqlInsertQueryListToBqlEndpoint(String endpointAdress, ArrayList<String> bqlInsertQueryList, ArrayList<String> correspondingRdfStatementStringsToBqlInsertStatements, BufferedWriter synthesizedAndInsertRdfNtripleStatementsOutputWriter){
        Queue<String> synthesizedAndInsertedRdfStatements = new ConcurrentLinkedQueue<String>();

        StringWriter requestStringWriter = new StringWriter();
        for (String bqlInsertQuery:bqlInsertQueryList) {
            requestStringWriter.append(bqlInsertQuery);
        }
        String allBqlInsertStatements = requestStringWriter.toString();

        Observer<String> responseObserver = new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String synthesizedAndInsertedRdfStatement) {
                synthesizedAndInsertedRdfStatements.add(synthesizedAndInsertedRdfStatement);
                try {
                    synthesizedAndInsertRdfNtripleStatementsOutputWriter.write(synthesizedAndInsertedRdfStatement + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                try {
                    synthesizedAndInsertRdfNtripleStatementsOutputWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        BadwolfHttpImporter.sendRequestToBadwolfEndpoint(endpointAdress, responseObserver, allBqlInsertStatements, correspondingRdfStatementStringsToBqlInsertStatements);
    }

    private static void synthesizeSparqlQueryListListToBqlQueriesAndWriteToFile(ArrayList<SparqlQuery> sparqlQueries, String bqlTargetGraphName, String outputFilePath){
        ArrayList<String> synthesizedBqlQueries = new ArrayList<>();
        ArrayList<String> synthesizableSparqlQueries = new ArrayList<>();

        for (SparqlQuery sparqlQueryFromFile : sparqlQueries) {

            if (sparqlQueryCanBeSynthesizedToBqlQuery(sparqlQueryFromFile)) {

                BqlQuery synthesizedBqlQuery = synthesizeBqlQueryFromSparqlQuery(sparqlQueryFromFile, bqlTargetGraphName);

                String bqlQueryString = synthesizedBqlQuery.getQueryAsString();
                String sparqlQueryString = sparqlQueryFromFile.getQueryAsStringInOneLine();
                synthesizedBqlQueries.add(bqlQueryString);
                synthesizableSparqlQueries.add(sparqlQueryString);

                if (debugOutPutEnabled){
                    System.out.println("SPARQL QUERY");
                    System.out.println(sparqlQueryString + "\n");
                    //printDebugOutputForSparqlQuery(sparqlQueryFromFile);
                    System.out.println("SYNTHESIZED BQL QUERY");
                    System.out.println(bqlQueryString + "\n");
                }

            }

        }

        writeListOfStringsToFile(synthesizedBqlQueries, outputFilePath);
        String outputFilePathForSynthesizableSparqlQueries = outputFilePath.replace(".bql", "_synthesizable.sparql");
        writeListOfStringsToFile(synthesizableSparqlQueries, outputFilePathForSynthesizableSparqlQueries);

        if (debugOutPutEnabled) {
            System.out.println("Number of SPARQL Queries in File: " + sparqlQueries.size());
            System.out.println("Number of thereof BQL-Synthesizable Queries: " + synthesizableSparqlQueries.size());
        }
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

}
