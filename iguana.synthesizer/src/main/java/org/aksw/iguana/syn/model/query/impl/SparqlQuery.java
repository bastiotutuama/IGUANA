package org.aksw.iguana.syn.model.query.impl;

import org.aksw.iguana.syn.model.query.AbstractQuery;
import org.aksw.iguana.syn.model.query.Query;
import org.apache.jena.*;

public class SparqlQuery extends AbstractQuery implements Query {

    private org.apache.jena.query.Query jenaSparqlQuery;

    public SparqlQuery(org.apache.jena.query.Query jenaSparqlQuery) {

        this.jenaSparqlQuery = jenaSparqlQuery;

        //Map Jena-QueryType to Synthesizer-Query-Type
        switch (this.jenaSparqlQuery.queryType()) {
            case SELECT:
                setQueryType(Type.SELECT);
                break;

            case CONSTRUCT:
                setQueryType(Type.CONSTRUCT);
                break;

            default:
                setQueryType(Type.UNKNOWN);
                break;
        }

    }

    @Override
    public Language getLanguage() {
        return Language.SPARQL;
    }



}
