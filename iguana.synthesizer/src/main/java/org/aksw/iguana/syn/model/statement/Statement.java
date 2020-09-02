package org.aksw.iguana.syn.model.statement;

public interface Statement {

    public String getSubject();

    public String getPredicate();

    public String getObject();

    public String getCompleteStatement();
}
