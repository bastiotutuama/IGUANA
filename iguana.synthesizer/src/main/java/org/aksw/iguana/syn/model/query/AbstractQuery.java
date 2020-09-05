package org.aksw.iguana.syn.model.query;

public abstract class AbstractQuery implements Query {

    private Type queryType;

    public Type getQueryType() {
        return queryType;
    }

    public void setQueryType(Type queryType) {
        this.queryType = queryType;
    }
}
