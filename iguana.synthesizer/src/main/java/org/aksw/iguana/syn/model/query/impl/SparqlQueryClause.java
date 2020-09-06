package org.aksw.iguana.syn.model.query.impl;

import org.aksw.iguana.syn.model.query.AbstractQueryClause;
import org.aksw.iguana.syn.model.query.Query;

public class SparqlQueryClause extends AbstractQueryClause {

    public SparqlQueryClause(Query.QueryClauseType queryClauseType, String clauseString) {
        super(queryClauseType, clauseString);
    }
}
