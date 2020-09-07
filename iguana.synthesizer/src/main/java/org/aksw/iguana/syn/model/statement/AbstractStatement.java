package org.aksw.iguana.syn.model.statement;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractStatement implements Statement{

    public enum StatementPartIdentifier {
        SUBJECT,
        PREDICATE,
        OBJECT
    }

    public enum StatementLanguage {
        RDF_NTRIPLE("N-TRPLE"),
        BQL_INSERT("");

        private final String jenaLanguageIdentifier;

        /**
         * @param jenaLanguageIdentifier
         */
        StatementLanguage(final String jenaLanguageIdentifier) {
            this.jenaLanguageIdentifier = jenaLanguageIdentifier;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return jenaLanguageIdentifier;
        }
    }

    public enum StatementControlSymbol {
        URI_NODE_PROTOCOL_COLON,
        URI_NODE_SLASH,
        URI_NODE_FULLSTOP,
        URI_NODE_OPENING_BRACKET,
        URI_NODE_CLOSING_BRACKET,
        URI_ILLEGAL_PERCENT_WHICH_WILL_NOT_ESCAPE_IN_INSERT_HTTP_REQUEST,
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



    //TODO enable inhertance fo constrolSymbol Methods

    public static List<StatementControlSymbol> getStatemenControlSymbolIdentifiers () {
        return Arrays.asList(StatementControlSymbol.class.getEnumConstants());
    }

    //TODO enable inhertance fo constrolSymbol Methods

    // public abstract String getStatementControlSymbol (StatementControlSymbol statementControlSymbol);

    //TODO enable inhertance fo constrolSymbol Methods

    // public abstract boolean doesStatementControlSymbolExistForString(String controlSymbolCandidate);

    public abstract String getSubject();

    public abstract String getPredicate();

    public abstract String getObject();

    @Override
    public String getCompleteStatement() {
        return getSubject() + " " + getPredicate() + " " + getObject() + " .";
    }

    public String getCompleteStatementWithoutFullStop() {
        return getSubject() + " " + getPredicate() + " " + getObject();
    }
}
