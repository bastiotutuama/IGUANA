package org.aksw.iguana.syn.controller;

import org.aksw.iguana.syn.model.statement.Statement;
import org.aksw.iguana.syn.model.statement.impl.RDFNtripleStatement;
import org.aksw.iguana.syn.synthesizer.statement.impl.RDFtoBQLInsertStatementSythesizer;
import org.aksw.iguana.syn.util.FileParser;

import java.util.ArrayList;

public class MainController {

    public static void main(String [] args) {
        ArrayList<Statement> fileStatements = FileParser.readInStatementsFromFile("/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8_small.nt", FileParser.SupportedInputLanguage.N_TRIPLE);

        for (Statement fileStatement : fileStatements) {
            System.out.println("RDF-NTriple Statement");
            System.out.println(fileStatement.getCompleteStatement());
            System.out.println();

            System.out.println("Synthesized BQL-Insert Statement");
            System.out.println(RDFtoBQLInsertStatementSythesizer.synthesizeBQLStatementFromRDFStatement((RDFNtripleStatement) fileStatement).getCompleteStatement());
            System.out.println();
        }

    }

}
