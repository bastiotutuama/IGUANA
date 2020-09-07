package org.aksw.iguana.syn.util;

import org.aksw.iguana.syn.model.query.impl.SparqlQuery;
import org.aksw.iguana.syn.model.statement.AbstractStatement;
import org.aksw.iguana.syn.model.statement.impl.RDFNtripleStatement;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Consumer;

public class FileParser {


    private static String DEFAULT_BASE_URL = "https://example.com";

    public static ArrayList<org.aksw.iguana.syn.model.statement.Statement> readInStatementsFromFile(String inputFilePath, AbstractStatement.StatementLanguage statementLanguage){
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // use the RDFDataMgr to find the input file
        InputStream inputStream = RDFDataMgr.open( inputFilePath );
        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "File: " + inputFilePath + " not found");
        }

        // read the RDF file given in the specified language
        model.read(inputStream, DEFAULT_BASE_URL, statementLanguage.toString());

        // write it to standard out
        //model.write(System.out, SupportedInputLanguage.N_TRIPLE.toString());

        ArrayList<org.aksw.iguana.syn.model.statement.Statement> statementsFromFile = new ArrayList<>();

        // list the statements in the Model
        StmtIterator stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()) {
            Statement jenaRdfStatement = stmtIterator.nextStatement();  // get next statement
            /* //print Literal Datatype, if object is literal:
            if (jenaRdfStatement.getObject().isLiteral()) {
                System.out.println("Datatype");
                System.out.println(jenaRdfStatement.getObject().asLiteral().getDatatype());
                System.out.println("Lexical Form of Object");
                System.out.println(jenaRdfStatement.getObject().asLiteral().getLexicalForm());
                System.out.println();
            }*/
            statementsFromFile.add(new RDFNtripleStatement(jenaRdfStatement)); // add jenaRdfStatement to Collection according to the Statement interface
        }

        return statementsFromFile;

    }

    public static  ArrayList<SparqlQuery> readInSparqlQueriesFromFile(String pInputFilePath){

        Path inputFilePath = Paths.get(pInputFilePath);
        ArrayList<SparqlQuery> sparqlQueriesFromFile = new ArrayList<>();

        try {
            Files.lines(inputFilePath).forEach(new Consumer<String>() {
                @Override
                public void accept(String queryFromFile) {
                    org.apache.jena.query.Query jenaSparqlQueryFromFile = QueryFactory.create(queryFromFile);
                    sparqlQueriesFromFile.add(new SparqlQuery(jenaSparqlQueryFromFile));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sparqlQueriesFromFile;
    }

}
