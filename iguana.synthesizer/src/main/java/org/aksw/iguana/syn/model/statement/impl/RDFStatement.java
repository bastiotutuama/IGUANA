package org.aksw.iguana.syn.model.statement.impl;

import org.aksw.iguana.syn.model.statement.AbstractStatement;
import org.apache.jena.rdf.model.Statement;

public class RDFStatement extends AbstractStatement implements org.aksw.iguana.syn.model.statement.Statement {

    private Statement rdfStatment;

    public RDFStatement(Statement rdfStatment) {
        this.rdfStatment = rdfStatment;
    }

    public Statement getRdfStatment() {
        return rdfStatment;
    }

    public static String getStatementControlSymbol(StatementControlSymbol statementControlSymbol) {
        switch (statementControlSymbol) {
            case URI_NODE_SLASH:
            case URI_PREDICATE_SLASH:
                return "/";

            case URI_NODE_FULLSTOP:
            case URI_PREDICATE_FULLSTOP:
                return ".";

            case URI_NODE_PROTOCOL_COLON:
            case URI_PREDICATE_PROTOCOL_COLON:
                return ":";

            case URI_NODE_OPENING_BRACKET:
            case URI_PREDICATE_OPENING_BRACKET:
                return "<";

            case URI_NODE_CLOSING_BRACKET:
            case URI_PREDICATE_CLOSING_BRACKET:
                return ">";

            case LITERAL_OPENING_BRACKET:
            case LITERAL_CLOSING_BRACKET:
                return "\"";

            case LITERAL_DATATYPE_SPECIFIER_BOOLEAN:
                return "^^<http://www.w3.org/2001/XMLSchema#boolean>";

            case LITERAL_DATATYPE_SPECIFIER_FLOAT:
                return "^^<http://www.w3.org/2001/XMLSchema#decimal>";

            case LITERAL_DATATYPE_SPECIFIER_INTEGER:
                return "^^<http://www.w3.org/2001/XMLSchema#integer>";

            case LITERAL_DATATYPE_SPECIFIER_BLOB:
            case LITERAL_DATATYPE_SPECIFIER_TEXT:
                return "^^<http://www.w3.org/2001/XMLSchema#string>";

            default:
                return "";
        }
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
