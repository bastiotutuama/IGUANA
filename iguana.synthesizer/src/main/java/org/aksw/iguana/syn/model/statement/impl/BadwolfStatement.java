package org.aksw.iguana.syn.model.statement.impl;

import org.aksw.iguana.syn.model.statement.AbstractStatement;
import org.aksw.iguana.syn.model.statement.Statement;

public class BadwolfStatement extends AbstractStatement implements Statement {

    private String subject;
    private String predicate;
    private String object;

    public enum IllegalStatementResourceCharacter {
        ILLEGAL_LITERAL_FULLSTOP(".", ","),
        ILLEGAL_LITERAL_OPEN_ANGLE_BRACKET("<", "("),
        ILLEGAL_LITERAL_CLOSED_ANGLE_BRACKET(">", ")"),
        ILLEGAL_LITERAL_SLASH("/", "|"),
        ILLEGAL_LITERAL_COLON(":", "="),
        ILLEGAL_LITERAL_AT_FOLLOWING_BRACKETS("@[]", "(at)"),
        ILLEGAL_LITERAL_CR("\r", ""),
        ILLEGAL_LITERAL_LF("\n", ""),
        ILLEGAL_LITERAL_CRLF("\r\n", ""),
        ILLEGAL_LITERAL_BACKSLASH("\\", "");


        private String illegalCharacterSequence;
        private String characterSequenceSubstitue;

        IllegalStatementResourceCharacter(String illegalCharacterSequence, String characterSequenceSubstitue) {
            this.illegalCharacterSequence = illegalCharacterSequence;
            this.characterSequenceSubstitue = characterSequenceSubstitue;
        }

        public String getIllegalCharacterSequence() {
            return illegalCharacterSequence;
        }

        public String getCharacterSequenceSubstitue() {
            return characterSequenceSubstitue;
        }
    }

    /* public static List<IllegalStatementResourceCharacter> getIllegalStatementResourceCharacters () {
        return Arrays.asList(IllegalStatementResourceCharacter.class.getEnumConstants());
    } */

    public static String getStatementControlSymbol(StatementControlSymbol statementControlSymbol) {
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

            case LITERAL_DATATYPE_DELIMETER:
                return "^^";

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

            case URI_ILLEGAL_PERCENT_WHICH_WILL_NOT_ESCAPE_IN_INSERT_HTTP_REQUEST:
                return "§";

            default:
                return "";
        }
    }

    public BadwolfStatement(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getPredicate() {
        return predicate;
    }

    @Override
    public String getObject() {
        return object;
    }
}
