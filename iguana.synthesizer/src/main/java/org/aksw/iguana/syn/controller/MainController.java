package org.aksw.iguana.syn.controller;

import org.aksw.iguana.di.http.BadwolfHttpImporter;
import org.aksw.iguana.syn.model.statement.Statement;
import org.aksw.iguana.syn.model.statement.impl.RDFNtripleStatement;
import org.aksw.iguana.syn.synthesizer.statement.impl.RDFtoBQLInsertStatementSythesizer;
import org.aksw.iguana.syn.util.FileParser;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainController {

    public static void main(String [] args) {
        ArrayList<Statement> fileStatements = FileParser.readInStatementsFromFile("/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8.nt", FileParser.SupportedInputLanguage.N_TRIPLE);
        ArrayList<String> currentStatementChunks = new ArrayList<>();

        //TODO MEthod for Bulk.Insert Statement
        /*
        int lineLimit = 2000;
        int i = 0;
        for (Statement fileStatement : fileStatements) {
            i++;
            //System.out.println("RDF-NTriple Statement");
            //System.out.println(fileStatement.getCompleteStatement());
            //System.out.println();

            //System.out.println("Synthesized BQL-Insert Statement");
            //if( i> 100) {
                String synthesizedBQLInsertStatement = RDFtoBQLInsertStatementSythesizer.synthesizeBQLStatementFromRDFStatement((RDFNtripleStatement) fileStatement).getCompleteStatement();
                //System.out.println(synthesizedBQLInsertStatement);
                outputStatements.add(synthesizedBQLInsertStatement);
            //}


            if (i>=lineLimit) {
                break;
            }
        }

        //Insert Additions
        outputStatements.add(0, "INSERT DATA INTO ?swdf {");
        String lastTripleWithouFullStop = outputStatements.get(outputStatements.size()-1).replace(".",""); //remove full-stop of last triple.
        outputStatements.set(outputStatements.size()-1, lastTripleWithouFullStop);
        outputStatements.add("};");*/

        //outputStatements = RDFtoBQLInsertStatementSythesizer.generateBQLInsertStatementsFromRDFNtripleStatements(fileStatements.subList(0,750));

        int chunkSize = 5;
        int i = 0;
        while (i<200000/*fileStatements.size()*/){
            currentStatementChunks = RDFtoBQLInsertStatementSythesizer.generateBQLInsertStatementsFromRDFNtripleStatements(fileStatements.subList(i, i + chunkSize - 1));
            sendStatementsToBqlEndpoint(currentStatementChunks);
            i+=chunkSize;
        }

      //  BadwolfHttpImporter.shutdownNetworkClient();

        /*
        Path outputFile = Paths.get("/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8_insert.bql");
        try {
            //Files.write(outputFile, outputStatements, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    private static void sendStatementsToBqlEndpoint(ArrayList<String> statementList){
        StringWriter requestStringWriter = new StringWriter();
        for (String bqlInsertStatement:statementList) {
            requestStringWriter.append(bqlInsertStatement);
        }
        String allBqlInsertStatements = requestStringWriter.toString();
        try {
            requestStringWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BadwolfHttpImporter.sendRequestToBadwolfEndpoint("http://131.234.29.241:1234/bql", allBqlInsertStatements);
    }

}
