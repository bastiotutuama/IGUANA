package org.aksw.iguana.syn.model.query.impl;

import org.aksw.iguana.syn.model.query.AbstractQuery;
import org.aksw.iguana.syn.model.query.Query;
import org.apache.jena.query.QueryType;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.syntax.*;

import java.util.List;

public class SparqlQuery extends AbstractQuery implements Query {

    private org.apache.jena.query.Query jenaSparqlQuery;
    private List<Element> jenaQueryPatternElements;
    private static QueryType[] supportedJenaQueryTypes = {QueryType.SELECT, QueryType.CONSTRUCT};

    public SparqlQuery(org.apache.jena.query.Query jenaSparqlQuery) {

        this.jenaSparqlQuery = jenaSparqlQuery;
        this.jenaQueryPatternElements = ((ElementGroup)  this.jenaSparqlQuery.getQueryPattern()).getElements();

        //Map Jena-QueryType to Synthesizer-Query-Type
        switch (this.jenaSparqlQuery.queryType()) {
            case SELECT:
                setQueryType(Type.SELECT);
                break;

            case CONSTRUCT:
                setQueryType(Type.CONSTRUCT);
                break;

            default:
                setQueryType(Type.UNSUPPORTED);
                break;
        }

    }

    @Override
    public Language getLanguage() {
        return Language.SPARQL;
    }

    @Override
    public String getQueryAsString() {
        return jenaSparqlQuery.toString(Syntax.syntaxSPARQL_11);
    }

    public static QueryType[] getSupportedJenaQueryTypes() {
        return supportedJenaQueryTypes;
    }

    public List<Element> getJenaQueryPatternElements(){
        return jenaQueryPatternElements;
    }

    private boolean checkJenaQueryPatternElementsForSpecificElement(Class specificQueryPatternElement){
        for (Element currentQueryPatternElement:getJenaQueryPatternElements()) {
            if (specificQueryPatternElement.isInstance(currentQueryPatternElement))
                return true;
        }
        return false;
    }

    public boolean queryPatternContainsOptionalElement(){
       return checkJenaQueryPatternElementsForSpecificElement(ElementOptional.class);
    }

    public boolean queryPatternContainsUnionElement(){
        return checkJenaQueryPatternElementsForSpecificElement(ElementUnion.class);
    }

    public boolean queryPatternContainsFilterElement(){
        return checkJenaQueryPatternElementsForSpecificElement(ElementFilter.class);
    }

    public boolean queryPatternContainsNamedGraphElement(){
        return checkJenaQueryPatternElementsForSpecificElement(ElementNamedGraph.class);
    }



}
