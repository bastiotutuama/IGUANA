package org.aksw.iguana.syn.model;

public abstract class AbstractStatement {

    private String subject;
    private String predicate;
    private String object;

    public abstract String getSubject();

    public abstract String getPredicate();

    public abstract String getObject();
}
