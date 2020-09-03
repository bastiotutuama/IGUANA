package org.aksw.iguana.syn.controller;

import org.aksw.iguana.syn.model.statement.Statement;
import org.aksw.iguana.syn.model.statement.impl.RDFNtripleStatement;
import org.aksw.iguana.syn.synthesizer.statement.impl.RDFtoBQLInsertStatementSythesizer;
import org.aksw.iguana.syn.util.FileParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainController {

    public static void main(String [] args) {
        ArrayList<Statement> fileStatements = FileParser.readInStatementsFromFile("/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8_small.nt", FileParser.SupportedInputLanguage.N_TRIPLE);
        ArrayList<String> outputStatements = new ArrayList<>();

        for (Statement fileStatement : fileStatements) {
            //System.out.println("RDF-NTriple Statement");
            //System.out.println(fileStatement.getCompleteStatement());
            //System.out.println();

            //System.out.println("Synthesized BQL-Insert Statement");
            String synthesizedBQLInsertStatement = RDFtoBQLInsertStatementSythesizer.synthesizeBQLStatementFromRDFStatement((RDFNtripleStatement) fileStatement).getCompleteStatement();
            System.out.println(synthesizedBQLInsertStatement);
            outputStatements.add(synthesizedBQLInsertStatement);
            //System.out.println();
        }

        Path outputFile = Paths.get("/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8.bql");
        try {
            Files.write(outputFile, outputStatements, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
