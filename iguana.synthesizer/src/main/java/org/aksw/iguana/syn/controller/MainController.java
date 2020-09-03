package org.aksw.iguana.syn.controller;

import org.aksw.iguana.syn.model.statement.Statement;
import org.aksw.iguana.syn.model.statement.impl.RDFNtripleStatement;
import org.aksw.iguana.syn.synthesizer.impl.RDFtoBQLInsertStatementSythesizer;
import org.aksw.iguana.syn.util.FileParser;

import java.util.ArrayList;

public class MainController {

    public static void main(String [] args) {
        ArrayList<Statement> fileStatements = FileParser.readInStatementsFromFile("/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8_small.nt", FileParser.SupportedInputLanguage.N_TRIPLE);

        for (Statement fileStatement : fileStatements) {
            System.out.println(fileStatement.getCompleteStatement());
        }

        System.out.println(fileStatements.get(0).getCompleteStatement());

        RDFNtripleStatement firstRdfNtripleStatement = (RDFNtripleStatement) fileStatements.get(0);
        System.out.println("First RDF Statement Synthesization");
        System.out.println(RDFtoBQLInsertStatementSythesizer.synthesizeBQLStatementFromRDFStatement(firstRdfNtripleStatement).getCompleteStatement());
    }

}
