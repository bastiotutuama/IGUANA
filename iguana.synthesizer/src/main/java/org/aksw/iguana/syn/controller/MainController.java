package org.aksw.iguana.syn.controller;

import org.aksw.iguana.di.http.BadwolfHttpImporter;
import org.aksw.iguana.syn.model.statement.Statement;
import org.aksw.iguana.syn.model.statement.impl.RDFNtripleStatement;
import org.aksw.iguana.syn.synthesizer.statement.impl.RDFNtripleStatementToBadwolfStatementSythesizer;
import org.aksw.iguana.syn.util.FileParser;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainController {

    private static final String defaultOutputMethod = "endpoint";
    private static final String defaultInputFilePath = "/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8_5000.nt"; //"/home/otutuama/datasets/swdfu8.nt";
    private static final String defaultEndpointAdress = "http://131.234.29.241:1234/bql"; //"http://localhost:1234/bql";
    private static final String defaultGraphName = "swdf";
    private static final String defaultOutputFilePath = "/home/otutuama/datasets/swdfu8.bql"; //"/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8_5000.bql"
    private static final int defaultChunkSize = 5;


    public static void main(String [] args) {

        String outputMethod;
        String inputFilePath;
        String graphName;
        String endpointAdress;
        String outputFilePath;
        int chunkSize;

        outputMethod = setVariableFromInputArguments(args, 0, defaultOutputMethod);
        inputFilePath = setVariableFromInputArguments(args, 1, defaultInputFilePath);
        endpointAdress = setVariableFromInputArguments(args, 2, defaultEndpointAdress);
        outputFilePath = setVariableFromInputArguments(args, 2, defaultOutputFilePath);
        graphName = setVariableFromInputArguments(args, 3, defaultGraphName);
        chunkSize = Integer.parseInt(setVariableFromInputArguments(args, 4, String.valueOf(defaultChunkSize)));

        ArrayList<Statement> rdfNtripleStatements = FileParser.readInStatementsFromFile(inputFilePath, FileParser.SupportedInputLanguage.N_TRIPLE);

        if (outputMethod.equals("endpoint")) {
            synthesizeRdfNtripleStatmentListToBqlAndLoadToBqlEndpointAsChunks(rdfNtripleStatements, graphName, endpointAdress, chunkSize);
        } else if (outputMethod.equals("file")) {
            synthesizeRdfNtripleStatmentListToBqlAndWriteToFile(rdfNtripleStatements, outputFilePath);
        } else {
            System.out.println("Output-Method not recognized");
        }
    }

    private static String setVariableFromInputArguments(String[] arguments, int argumentIndex, String defaultValue){
        if(arguments.length >= argumentIndex + 1 && !arguments[argumentIndex].isEmpty()) {
            return arguments[argumentIndex];
        } else {
            return defaultValue;
        }
    }

    private static void synthesizeRdfNtripleStatmentListToBqlAndWriteToFile(ArrayList<Statement> rdfNtripleStatements, String outputFilePath){

        ArrayList<String> outputStatements = new ArrayList<>();
        for (Statement rdfNtripleStatement : rdfNtripleStatements) {

            //System.out.println("RDF-NTriple Statement");
            //System.out.println(fileStatement.getCompleteStatement());

            String synthesizedBQLInsertStatement = RDFNtripleStatementToBadwolfStatementSythesizer.synthesizeBQLStatementFromRDFStatement((RDFNtripleStatement) rdfNtripleStatement).getCompleteStatement();
            //System.out.println("Synthesized BQL-Insert Statement");
            //System.out.println(synthesizedBQLInsertStatement);

            outputStatements.add(synthesizedBQLInsertStatement);
        }

        Path outputFile = Paths.get(outputFilePath);
        try {
            Files.write(outputFile, outputStatements, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void synthesizeRdfNtripleStatmentListToBqlAndLoadToBqlEndpointAsChunks(ArrayList<Statement> rdfNtripleStatements, String graphName, String endpointAdress, int chunkSize){
        ArrayList<String> currentBqlInsertQueriesChunk;
        int i = 0;
        while (i<rdfNtripleStatements.size()){
            currentBqlInsertQueriesChunk = RDFNtripleStatementToBadwolfStatementSythesizer.generateBQLInsertQueryFromRDFNtripleStatements(graphName, rdfNtripleStatements.subList(i, i + chunkSize - 1));
            sendBqlInsertQueryListToBqlEndpoint(endpointAdress,currentBqlInsertQueriesChunk);
            i+=chunkSize;
            //System.out.println("Approximate load-progress:" + i  * 100 / rdfNtripleStatements.size() + "%");
        }
    }

    private static void sendBqlInsertQueryListToBqlEndpoint(String endpointAdress, ArrayList<String> bqlInsertQueryList){
        StringWriter requestStringWriter = new StringWriter();
        for (String bqlInsertQuery:bqlInsertQueryList) {
            requestStringWriter.append(bqlInsertQuery);
        }
        String allBqlInsertStatements = requestStringWriter.toString();
        try {
            requestStringWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BadwolfHttpImporter.sendRequestToBadwolfEndpoint(endpointAdress, allBqlInsertStatements);
    }

}
