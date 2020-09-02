package org.aksw.iguana.syn.controller;

import org.aksw.iguana.syn.model.statement.Statement;
import org.aksw.iguana.syn.util.FileParser;

import java.util.Collection;

public class MainController {

    public static void main(String [] args) {
        Collection<Statement> fileStatements = FileParser.readInStatementsFromFile("/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Dataset/swdfu8_small.nt", FileParser.SupportedInputLanguage.nTriple);

        for (Statement fileStatement : fileStatements) {
            System.out.println(fileStatement.getCompleteStatement());
        }
        //System.out.println(fileStatements.toString());
    }

}
