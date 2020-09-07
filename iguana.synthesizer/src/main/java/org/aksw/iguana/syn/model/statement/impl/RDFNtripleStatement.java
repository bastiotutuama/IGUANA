package org.aksw.iguana.syn.model.statement.impl;

import org.aksw.iguana.syn.model.statement.AbstractStatement;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.core.TriplePath;

public class RDFNtripleStatement extends AbstractStatement implements org.aksw.iguana.syn.model.statement.Statement {

    private String subject;
    private String predicate;
    private String object;

    //private Statement rdfStatment;
    private Node subjectNode;
    private Node predicateNode;
    private Node objectNode;

    public RDFNtripleStatement(Statement rdfStatment) {
       this(rdfStatment.getSubject().asNode(), rdfStatment.getPredicate().asNode(), rdfStatment.getSubject().asNode());
    }

    public RDFNtripleStatement(TriplePath triplePath) {
        this(triplePath.getSubject(), triplePath.getPredicate(), triplePath.getObject());
    }

    public RDFNtripleStatement(Node subjectNode, Node predicateNode, Node objectNode) {
        this.subjectNode = subjectNode;
        this.predicateNode = predicateNode;
        this.objectNode = objectNode;
        this.subject = getTripleNodeAsRDFNtripleNodeString(subjectNode);
        this.predicate = getTripleNodeAsRDFNtripleNodeString(predicateNode);
        this.object = getTripleNodeAsRDFNtripleNodeString(objectNode);
    }

    public static String getTripleNodeAsRDFNtripleNodeString(Node tripleNode){
        if (tripleNode.isURI()) {
            return getStatementControlSymbol(AbstractStatement.StatementControlSymbol.URI_NODE_OPENING_BRACKET) + tripleNode.toString() + getStatementControlSymbol(AbstractStatement.StatementControlSymbol.URI_NODE_CLOSING_BRACKET);
        } else if(tripleNode.isLiteral()) {
            return  getStatementControlSymbol(AbstractStatement.StatementControlSymbol.LITERAL_OPENING_BRACKET)
                    + tripleNode.getLiteral().getLexicalForm()
                    + getStatementControlSymbol(AbstractStatement.StatementControlSymbol.LITERAL_CLOSING_BRACKET)
                    + getStatementControlSymbol(AbstractStatement.StatementControlSymbol.LITERAL_DATATYPE_DELIMETER)
                    + getStatementControlSymbol(AbstractStatement.StatementControlSymbol.URI_NODE_OPENING_BRACKET)
                    + tripleNode.getLiteral().getDatatypeURI()
                    + getStatementControlSymbol(AbstractStatement.StatementControlSymbol.URI_NODE_CLOSING_BRACKET);
        }else {
            return tripleNode.toString();
        }
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

            case LITERAL_DATATYPE_DELIMETER:
                return "^^";

            case LITERAL_DATATYPE_SPECIFIER_BOOLEAN:
                return "^^<http://www.w3.org/2001/XMLSchema#boolean>";

            case LITERAL_DATATYPE_SPECIFIER_FLOAT:
                return "^^<http://www.w3.org/2001/XMLSchema#decimal>";

            case LITERAL_DATATYPE_SPECIFIER_INTEGER:
                return "^^<http://www.w3.org/2001/XMLSchema#integer>";

            case LITERAL_DATATYPE_SPECIFIER_BLOB:
            case LITERAL_DATATYPE_SPECIFIER_TEXT:
                return "^^<http://www.w3.org/2001/XMLSchema#string>";

            case URI_ILLEGAL_PERCENT_WHICH_WILL_NOT_ESCAPE_IN_INSERT_HTTP_REQUEST:
                return "%";

            default:
                return "";
        }
    }

    public static boolean doesStatementControlSymbolExistForString(String controlSymbolCandidate) {
        for (AbstractStatement.StatementControlSymbol currentStatementControlSymbolAssociationToCheck: getStatemenControlSymbolIdentifiers()) {
            if ( getStatementControlSymbol(currentStatementControlSymbolAssociationToCheck).equals(controlSymbolCandidate) ){
                return true;
            }
        }
        return false;
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

    public String getStatmentPart(StatementPartIdentifier statementPartIdentifier) {
        switch (statementPartIdentifier) {
            case SUBJECT:
                return getSubject();

            case PREDICATE:
                return getPredicate();

            case OBJECT:
                return getObject();

            default:
                throw new UnsupportedOperationException("Stament part could not be identified.");
        }
    }



    public boolean objectIsURINode () {
        return objectNode.isURI();
    }

    public boolean objectIsLiteral () {
        return objectNode.isLiteral();
    }

    public String getLexicalFormOfObjectLiteral() {
        if (objectIsLiteral()) {
            return objectNode.getLiteral().getLexicalForm();
        } else {
            return "";
        }
    }

    public String getObjectLiteralDatatypeURI () {
        if (objectIsLiteral()) {
            return objectNode.getLiteral().getDatatypeURI();
        } else {
            return "";
        }
    }

    public String getCompleteObjectLiteralDatatypeAssertion(){
        if (this.objectIsLiteral()) {
            return RDFNtripleStatement.getStatementControlSymbol(AbstractStatement.StatementControlSymbol.LITERAL_DATATYPE_DELIMETER) +
                    RDFNtripleStatement.getStatementControlSymbol(AbstractStatement.StatementControlSymbol.URI_NODE_OPENING_BRACKET) +
                    getObjectLiteralDatatypeURI() +
                    RDFNtripleStatement.getStatementControlSymbol(AbstractStatement.StatementControlSymbol.URI_NODE_CLOSING_BRACKET);
        } else {
            return "";
        }
    }

}
