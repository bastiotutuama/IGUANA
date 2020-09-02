package org.aksw.iguana.syn.model.impl;

import org.aksw.iguana.syn.model.AbstractStatement;
import org.apache.jena.rdf.model.Statement;

public class RDFStatement extends AbstractStatement implements org.aksw.iguana.syn.model.Statement {

    private Statement rdfStatment;

    public RDFStatement(Statement rdfStatment) {
        this.rdfStatment = rdfStatment;
    }

    public Statement getRdfStatment() {
        return rdfStatment;
    }

    public String getSubject() {
        return rdfStatment.getSubject().toString();
    }

    public String getPredicate() {
        return rdfStatment.getPredicate().toString();
    }

    public String getObject() {
        return rdfStatment.getObject().toString();
    }
}
