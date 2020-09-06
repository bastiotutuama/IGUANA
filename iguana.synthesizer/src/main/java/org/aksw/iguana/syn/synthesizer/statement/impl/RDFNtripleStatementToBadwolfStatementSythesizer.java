package org.aksw.iguana.syn.synthesizer.statement.impl;

import org.aksw.iguana.syn.model.statement.AbstractStatement;
import org.aksw.iguana.syn.model.statement.Statement;
import org.aksw.iguana.syn.model.statement.impl.BadwolfStatement;
import org.aksw.iguana.syn.model.statement.impl.RDFNtripleStatement;
import org.aksw.iguana.syn.synthesizer.Synthesizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class RDFNtripleStatementToBadwolfStatementSythesizer implements Synthesizer {

    private final AbstractStatement.StatementLanguageIdentifier sourceLanguage = AbstractStatement.StatementLanguageIdentifier.RDF_NTRIPLE;
    private final AbstractStatement.StatementLanguageIdentifier targetLanguage = AbstractStatement.StatementLanguageIdentifier.BQL_INSERT;

    public AbstractStatement.StatementLanguageIdentifier getSourceLanguage() {
        return sourceLanguage;
    }

    public AbstractStatement.StatementLanguageIdentifier getTargetLanguage() {
        return targetLanguage;
    }

    public static BadwolfStatement synthesizeBadwolfStatementFromRDFStatement(RDFNtripleStatement rdfNtripleStatement) {
        //TODO: Better Abstraction in the Receivment of Control-Symbol characters according to Statement Class Instance

        HashMap<AbstractStatement.StatementPartIdentifier, String> synthesizedStatementParts = new HashMap<>();

        synthesizedStatementParts.put(AbstractStatement.StatementPartIdentifier.SUBJECT, rdfNtripleStatement.getSubject());
        synthesizedStatementParts.put(AbstractStatement.StatementPartIdentifier.PREDICATE, rdfNtripleStatement.getPredicate());

        /* Case-Handling for Synthesization of Literal Content with characters that are not allowed in BQL-Literals */
        String intialObjectContent = rdfNtripleStatement.getObject();
        String escapedObjectOntent = replaceIllegalBQLStatemtentLiteralCharactersInObjectLiteralContent(rdfNtripleStatement);
        boolean wasEqualAfterAscaping = intialObjectContent.equals(escapedObjectOntent);
        if (!wasEqualAfterAscaping) {
            //System.out.println("wasn't equal - before: " + intialObjectContent + " after: " + escapedObjectOntent );
        }
        synthesizedStatementParts.put(AbstractStatement.StatementPartIdentifier.OBJECT, escapedObjectOntent);


        HashMap<AbstractStatement.StatementPartIdentifier, ArrayList<AbstractStatement.StatementControlSymbol>> statementControlSymbolsToSynthesize = new HashMap<>();

        statementControlSymbolsToSynthesize.put(AbstractStatement.StatementPartIdentifier.SUBJECT,
                new ArrayList<AbstractStatement.StatementControlSymbol>(Arrays.asList(
                        AbstractStatement.StatementControlSymbol.URI_NODE_SLASH,
                        AbstractStatement.StatementControlSymbol.URI_NODE_PROTOCOL_COLON,
                        AbstractStatement.StatementControlSymbol.URI_NODE_FULLSTOP,
                        AbstractStatement.StatementControlSymbol.URI_NODE_OPENING_BRACKET,
                        AbstractStatement.StatementControlSymbol.URI_NODE_CLOSING_BRACKET,
                        AbstractStatement.StatementControlSymbol.URI_ILLEGAL_PERCENT_WHICH_WILL_NOT_ESCAPE_IN_INSERT_HTTP_REQUEST
                    )
                )
        );

        statementControlSymbolsToSynthesize.put(AbstractStatement.StatementPartIdentifier.PREDICATE,
                new ArrayList<AbstractStatement.StatementControlSymbol>(Arrays.asList(
                       AbstractStatement.StatementControlSymbol.URI_PREDICATE_SLASH,
                        AbstractStatement.StatementControlSymbol.URI_PREDICATE_PROTOCOL_COLON,
                        AbstractStatement.StatementControlSymbol.URI_PREDICATE_FULLSTOP,
                        AbstractStatement.StatementControlSymbol.URI_PREDICATE_OPENING_BRACKET,
                        AbstractStatement.StatementControlSymbol.URI_PREDICATE_CLOSING_BRACKET,
                        AbstractStatement.StatementControlSymbol.URI_ILLEGAL_PERCENT_WHICH_WILL_NOT_ESCAPE_IN_INSERT_HTTP_REQUEST
                    )
                )
        );

        statementControlSymbolsToSynthesize.put(AbstractStatement.StatementPartIdentifier.OBJECT,
                new ArrayList<AbstractStatement.StatementControlSymbol>(Arrays.asList(
                        AbstractStatement.StatementControlSymbol.LITERAL_DATATYPE_SPECIFIER_TEXT,
                        AbstractStatement.StatementControlSymbol.LITERAL_DATATYPE_SPECIFIER_BOOLEAN,
                        AbstractStatement.StatementControlSymbol.LITERAL_DATATYPE_SPECIFIER_FLOAT,
                        AbstractStatement.StatementControlSymbol.LITERAL_DATATYPE_SPECIFIER_INTEGER,
                        AbstractStatement.StatementControlSymbol.LITERAL_DATATYPE_SPECIFIER_BLOB,
                        AbstractStatement.StatementControlSymbol.LITERAL_OPENING_BRACKET,
                        AbstractStatement.StatementControlSymbol.LITERAL_CLOSING_BRACKET
                )
                )
        );

        ArrayList<AbstractStatement.StatementPartIdentifier> statementPartIdentifiersToIterateOver = new ArrayList<>(
                Arrays.asList(
                        AbstractStatement.StatementPartIdentifier.SUBJECT,
                        AbstractStatement.StatementPartIdentifier.PREDICATE,
                        AbstractStatement.StatementPartIdentifier.OBJECT)
        );


        for (AbstractStatement.StatementPartIdentifier currentStatementPartIdentifier: statementPartIdentifiersToIterateOver) {

            ArrayList<AbstractStatement.StatementControlSymbol> currentStatementPartControlSymbolsToSynthesize;

            /** Synthesization of each statement-part: SUBJECT, PREDICATE, OBJECT*/

            if (currentStatementPartIdentifier == AbstractStatement.StatementPartIdentifier.OBJECT && rdfNtripleStatement.objectIsURINode()) {
                //If Object is URI-Node, use the SUBJECT-synthesization
                currentStatementPartControlSymbolsToSynthesize = statementControlSymbolsToSynthesize.get(AbstractStatement.StatementPartIdentifier.SUBJECT);
            } else {
                currentStatementPartControlSymbolsToSynthesize = statementControlSymbolsToSynthesize.get(currentStatementPartIdentifier);
            }


            for (AbstractStatement.StatementControlSymbol currentStatementPartControlSymbolToSynthesize : currentStatementPartControlSymbolsToSynthesize) {

                String controlSymbolReplacementTarget = RDFNtripleStatement.getStatementControlSymbol(currentStatementPartControlSymbolToSynthesize);
                String controlSymbolReplacementSource = BadwolfStatement.getStatementControlSymbol(currentStatementPartControlSymbolToSynthesize);

                /*Actual statement-part synthesization through character replace*/
                synthesizedStatementParts.put(currentStatementPartIdentifier,
                        synthesizedStatementParts.get(currentStatementPartIdentifier).replace(controlSymbolReplacementTarget, controlSymbolReplacementSource)
                );

            }

            if (currentStatementPartIdentifier == AbstractStatement.StatementPartIdentifier.OBJECT && rdfNtripleStatement.objectIsLiteral()) {

                /* Case-Handling for Synthesization of Literal Datatypes which are not associated in BQL. */
                //check if no control-symbol-mapping is present in Source-Statement-Dictionary
                String objectLiteralDatatypeURI = rdfNtripleStatement.getObjectLiteralDatatypeURI();
                if (!RDFNtripleStatement.doesStatementControlSymbolExistForString(objectLiteralDatatypeURI)) {
                    //No Mapping exists in source-Statement-Dictionary -> No adequate mapping in target-Statement-Dictionary available => replace with BQL default Datatype-Identifer (type:text).
                    String completeRdfNtripleDatatypeAssertion = rdfNtripleStatement.getCompleteObjectLiteralDatatypeAssertion();
                    synthesizedStatementParts.put(currentStatementPartIdentifier,
                            synthesizedStatementParts.get(currentStatementPartIdentifier).replace(
                                    completeRdfNtripleDatatypeAssertion,
                                    BadwolfStatement.getStatementControlSymbol(AbstractStatement.StatementControlSymbol.LITERAL_DATATYPE_SPECIFIER_TEXT)
                            )
                    );
                }


            }


        }

        return new BadwolfStatement(
                synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.SUBJECT),
                synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.PREDICATE),
                synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.OBJECT)
        );
    }

    /**
     * Case-Handling for Synthesization of Literal Content with characters that are not allowed in BQL-Literals
     * @param rdfNtripleStatement
     * @return
     */
    public static String replaceIllegalBQLStatemtentLiteralCharactersInObjectLiteralContent(RDFNtripleStatement rdfNtripleStatement){
        if(rdfNtripleStatement.objectIsLiteral()) {
            ArrayList<BadwolfStatement.IllegalStatementResourceCharacter> illegalBQLLiteralResourceCharacters = new ArrayList<>(
                    Arrays.asList(
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_FULLSTOP,
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_COLON,
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_SLASH,
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_OPEN_ANGLE_BRACKET,
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_CLOSED_ANGLE_BRACKET,
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_AT_FOLLOWING_BRACKETS,
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_CR,
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_LF,
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_CRLF,
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_BACKSLASH,
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_DATAYPE_DELIMETER,
                            BadwolfStatement.IllegalStatementResourceCharacter.ILLEGAL_LITERAL_DOUBLE_QUOTES
                    )
            );
            String rdfObjectLiteralContent = rdfNtripleStatement.getLexicalFormOfObjectLiteral();
            String rdfObjectLiteralContentWithIllegalStatementResourceCharactersRemoved = rdfObjectLiteralContent + "";
            for (BadwolfStatement.IllegalStatementResourceCharacter currentIllegalLiteralResourceCharacter : illegalBQLLiteralResourceCharacters) {
                //replace all illegal Literal Character with substitute
                rdfObjectLiteralContentWithIllegalStatementResourceCharactersRemoved = rdfObjectLiteralContentWithIllegalStatementResourceCharactersRemoved.replace(
                        currentIllegalLiteralResourceCharacter.getIllegalCharacterSequence(),
                        currentIllegalLiteralResourceCharacter.getCharacterSequenceSubstitue()
                );
            }
            return RDFNtripleStatement.getStatementControlSymbol(AbstractStatement.StatementControlSymbol.LITERAL_OPENING_BRACKET) +
                    rdfObjectLiteralContentWithIllegalStatementResourceCharactersRemoved +
                    RDFNtripleStatement.getStatementControlSymbol(AbstractStatement.StatementControlSymbol.LITERAL_CLOSING_BRACKET) +
                    rdfNtripleStatement.getCompleteObjectLiteralDatatypeAssertion();
        } else {
            return rdfNtripleStatement.getObject();
        }
    }

    public static ArrayList<String> generateBQLInsertQueryFromRDFNtripleStatements(String graphName,List<Statement> rdfNtripleStatements) {
        ArrayList<String> bqlInsertQueries = new ArrayList<>();

        for (Statement fileStatement : rdfNtripleStatements) {
            String synthesizedBQLInsertStatement = RDFNtripleStatementToBadwolfStatementSythesizer.synthesizeBadwolfStatementFromRDFStatement((RDFNtripleStatement) fileStatement).getCompleteStatementWithoutFullStop();
            bqlInsertQueries.add("INSERT DATA INTO ?" + graphName + " {" + synthesizedBQLInsertStatement + "};");
        }
        return bqlInsertQueries;
    }
}
