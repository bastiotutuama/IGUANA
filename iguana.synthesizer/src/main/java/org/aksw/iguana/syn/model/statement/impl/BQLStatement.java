package org.aksw.iguana.syn.model.statement.impl;

import org.aksw.iguana.syn.model.statement.AbstractStatement;
import org.aksw.iguana.syn.model.statement.Statement;

public class BQLStatement extends AbstractStatement implements Statement {

    @Override
    public String getStatementControlSymbol(StatementControlSymbol statementControlSymbol) {
        switch (statementControlSymbol) {
            case URI_NODE_SLASH:
            case URI_PREDICATE_SLASH:
                return "|";

            case URI_NODE_FULLSTOP:
            case URI_PREDICATE_FULLSTOP:
                return ",";

            case URI_NODE_PROTOCOL_COLON:
            case URI_PREDICATE_PROTOCOL_COLON:
                return "=";

            case URI_NODE_OPENING_BRACKET:
                return "/uri<";

            case URI_NODE_CLOSING_BRACKET:
                return ">";

            case URI_PREDICATE_CLOSING_BRACKET:
                return "\"@[]";

            case URI_PREDICATE_OPENING_BRACKET:
            case LITERAL_OPENING_BRACKET:
            case LITERAL_CLOSING_BRACKET:
                return "\"";

            case LITERAL_DATATYPE_SPECIFIER_BOOLEAN:
                return "^^type:bool";

            case LITERAL_DATATYPE_SPECIFIER_BLOB:
                return "^^type:blob";

            case LITERAL_DATATYPE_SPECIFIER_FLOAT:
                return "^^type:float64";

            case LITERAL_DATATYPE_SPECIFIER_INTEGER:
                return "^^type:int64";

            case LITERAL_DATATYPE_SPECIFIER_TEXT:
                return "^^type:text";

            default:
                return "";
        }
    }

    public String getSubject() {
        return null;
    }

    public String getPredicate() {
        return null;
    }

    public String getObject() {
        return null;
    }
}
