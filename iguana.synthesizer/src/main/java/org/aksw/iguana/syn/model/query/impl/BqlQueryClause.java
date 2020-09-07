package org.aksw.iguana.syn.model.query.impl;

import org.aksw.iguana.syn.model.query.AbstractQueryClause;
import org.aksw.iguana.syn.model.query.Query;
import org.aksw.iguana.syn.model.query.QueryClause;

public class BqlQueryClause extends AbstractQueryClause implements QueryClause {

    public BqlQueryClause(Query.QueryClauseType queryClauseType) {
        super(queryClauseType);
    }

}
