package org.aksw.iguana.syn.model.query.impl;

import org.aksw.iguana.syn.model.query.AbstractQuery;
import org.aksw.iguana.syn.model.query.Query;

public class BqlQuery extends AbstractQuery implements Query {

    private String bqlQuery;

    public BqlQuery(String bqlQuery, QueryType queryType) {
        this.bqlQuery = bqlQuery;
        setQueryType(queryType);
    }

    @Override
    public QueryLanguage getQueryLanguage() {
        return QueryLanguage.BQL;
    }

    @Override
    public String getQueryAsString() {
        return bqlQuery;
    }
}
