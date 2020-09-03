package org.aksw.iguana.syn.util;

import org.aksw.iguana.syn.model.statement.impl.RDFStatement;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class FileParser {

    /**
     * Serialized Strings as seen here: https://jena.apache.org/documentation/javadoc/jena/org/apache/jena/rdf/model/Model.html#read-java.lang.String-java.lang.String-
     */
    public enum SupportedInputLanguage{

        N_TRIPLE("N-TRIPLE");

        private final String jenaLangIdentifier;

        /**
         * @param jenaLangIdentifier
         */
        SupportedInputLanguage(final String jenaLangIdentifier) {
            this.jenaLangIdentifier = jenaLangIdentifier;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return jenaLangIdentifier;
        }
    }

    private static String DEFAULT_BASE_URL = "https://example.com";

    public static ArrayList<org.aksw.iguana.syn.model.statement.Statement> readInStatementsFromFile(String inputFileUri, SupportedInputLanguage supportedInputLanguage){
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // use the RDFDataMgr to find the input file
        InputStream inputStream = RDFDataMgr.open( inputFileUri );
        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "File: " + inputFileUri + " not found");
        }

        // read the RDF file given in the specified language
        model.read(inputStream, DEFAULT_BASE_URL, supportedInputLanguage.toString());

        // write it to standard out
        //model.write(System.out);

        ArrayList<org.aksw.iguana.syn.model.statement.Statement> statementsFromFile = new ArrayList<>();

        // list the statements in the Model
        StmtIterator stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()) {
            Statement jenaRdfStatement = stmtIterator.nextStatement();  // get next statement
            //print Literal Datatype, if object is literal:
            /**if (jenaRdfStatement.getObject().isLiteral()) {
                System.out.println("Datatype");
                System.out.println(jenaRdfStatement.getObject().asLiteral().getDatatype());
                System.out.println("Lexical Form of Object");
                System.out.println(jenaRdfStatement.getObject().asLiteral().getLexicalForm());
                System.out.println();
            }*/
            statementsFromFile.add(new RDFStatement(jenaRdfStatement)); // add jenaRdfStatement to Collection according to the Statement interface
        }

        return statementsFromFile;

    }

}
