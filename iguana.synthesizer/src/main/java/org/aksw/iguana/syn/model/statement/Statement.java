package org.aksw.iguana.syn.model.statement;

public interface Statement {

    public enum StatementPartIdentifier {
        SUBJECT,
        PREDICATE,
        OBJECT
    }

    public enum StatementLanguage {
        RDF_NTRIPLE("N-TRIPLE"),
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

    /*
    hand over class and implement returns here
    public static String getStatementControlSymbol (AbstractStatement.StatementControlSymbol statementControlSymbol){
        return null;
    } */

    public String getSubject();

    public String getPredicate();

    public String getObject();

    public String getCompleteStatement();
}
