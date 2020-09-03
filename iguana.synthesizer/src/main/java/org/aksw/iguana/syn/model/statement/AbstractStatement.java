package org.aksw.iguana.syn.model.statement;

public abstract class AbstractStatement implements Statement{

    public enum StatementPartIdentifier {
        SUBJECT,
        PREDICATE,
        OBJECT
    }

    public enum StatementLanguageIdentifier {
        RDF_NTRIPLE,
        BQL_INSERT
    }

    public enum StatementControlSymbol {
        URI_NODE_PROTOCOL_COLON,
        URI_NODE_SLASH,
        URI_NODE_FULLSTOP,
        URI_NODE_OPENING_BRACKET,
        URI_NODE_CLOSING_BRACKET,
        URI_PREDICATE_OPENING_BRACKET,
        URI_PREDICATE_CLOSING_BRACKET,
        URI_PREDICATE_PROTOCOL_COLON,
        URI_PREDICATE_SLASH,
        URI_PREDICATE_FULLSTOP,
        LITERAL_OPENING_BRACKET,
        LITERAL_CLOSING_BRACKET,
        LITERAL_DATATYPE_DELIMETER,
        LITERAL_DATATYPE_SPECIFIER_TEXT,
        LITERAL_DATATYPE_SPECIFIER_INTEGER,
        LITERAL_DATATYPE_SPECIFIER_FLOAT,
        LITERAL_DATATYPE_SPECIFIER_BOOLEAN,
        LITERAL_DATATYPE_SPECIFIER_BLOB
    }

   // public abstract String getStatementControlSymbol (StatementControlSymbol statementControlSymbol);

    public abstract String getSubject();

    public abstract String getPredicate();

    public abstract String getObject();

    @Override
    public String getCompleteStatement() {
        return getSubject() + " " + getPredicate() + " " + getObject() + " .";
    }
}
