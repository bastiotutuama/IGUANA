package org.aksw.iguana.syn.controller;

import org.aksw.iguana.syn.model.statement.Statement;
import org.aksw.iguana.syn.model.statement.impl.RDFStatement;
import org.aksw.iguana.syn.synthesizer.impl.RDFtoBQLInsertStatementSythesizer;
import org.aksw.iguana.syn.util.FileParser;

import java.util.ArrayList;
import java.util.Collection;

public class MainController {

    public static void main(String [] args) {
        ArrayList<Statement> fileStatements = FileParser.readInStatementsFromFile("/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8_small.nt", FileParser.SupportedInputLanguage.N_TRIPLE);

        /*for (Statement fileStatement : fileStatements) {
            System.out.println(fileStatements.get(0).getCompleteStatement());
        }*/

        System.out.println(fileStatements.get(0).getCompleteStatement());

        RDFStatement firstRdfStatement = (RDFStatement) fileStatements.get(0);
        System.out.println("First RDF Statement Synthesization");
        System.out.println(RDFtoBQLInsertStatementSythesizer.synthesizeBQLStatementFromRDFStatement(firstRdfStatement));
        //System.out.println(fileStatements.toString());
    }

}
