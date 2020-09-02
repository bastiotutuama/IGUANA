package org.aksw.iguana.syn.model;

public abstract class AbstractStatement implements Statement{

    public abstract String getSubject();

    public abstract String getPredicate();

    public abstract String getObject();

    @Override
    public String getCompleteStatement() {
        return getSubject() + " " + getPredicate() + " " + getObject() + " .";
    }
}
