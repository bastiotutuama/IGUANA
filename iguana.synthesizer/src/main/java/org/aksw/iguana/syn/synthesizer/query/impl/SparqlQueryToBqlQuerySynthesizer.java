package org.aksw.iguana.syn.synthesizer.query.impl;

import org.aksw.iguana.syn.model.query.Query;
import org.aksw.iguana.syn.model.query.impl.SparqlQuery;
import org.aksw.iguana.syn.synthesizer.Synthesizer;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SparqlQueryToBqlQuerySynthesizer implements Synthesizer {

    public static void main(String[] args) {

        String queryInputFilePathAsString = "/Users/sebastian/Dropbox/Academic Education/Uni Paderborn/Bachelor Thesis/Datasets and Queries/SWDF/Queries/swdf_queries.txt";
        Path queryInputFilePath = Paths.get(queryInputFilePathAsString);

        ArrayList<String> sparqlQueriesFromFileAsString = new ArrayList<>();

        try {
            Files.lines(queryInputFilePath).forEach(new Consumer<String>() {
                @Override
                public void accept(String queryFromFile) {
                    sparqlQueriesFromFileAsString.add(queryFromFile);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (String currentSparqlQueryFromFileAsString:sparqlQueriesFromFileAsString) {
            org.apache.jena.query.Query jenaSparqlQueryFromFile = QueryFactory.create(currentSparqlQueryFromFileAsString);
            //System.out.println(jenaSparqlQueryFromFile.serialize(Syntax.syntaxSPARQL));
            SparqlQuery sparqlQueryFromFile = new SparqlQuery(jenaSparqlQueryFromFile);


            if (sparqlQueryCanBeSynthesizedToBqlQuery(sparqlQueryFromFile)) {
                ElementGroup queryPatternElementGroup =  (ElementGroup) jenaSparqlQueryFromFile.getQueryPattern();
                List<Element> queryPatternElements = queryPatternElementGroup.getElements();

                System.out.println(jenaSparqlQueryFromFile.getQueryPattern().toString());

                System.out.println(sparqlQueryFromFile);
            }

        }


    }

    private static boolean sparqlQueryCanBeSynthesizedToBqlQuery(SparqlQuery sparqlQuery){

        if (sparqlQuery.getQueryType() == Query.Type.UNKNOWN)
            return false;

        return true;
    }

}
