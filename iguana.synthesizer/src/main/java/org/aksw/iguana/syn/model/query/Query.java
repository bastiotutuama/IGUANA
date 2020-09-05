package org.aksw.iguana.syn.model.query;

public interface Query {

    public enum Language {
        SPARQL,
        BQL
    }

    public enum Type{
        SELECT,
        CONSTRUCT,
        UNSUPPORTED
    }

    public Language getLanguage();

    public Type getQueryType();

    public String getQueryAsString();

}
