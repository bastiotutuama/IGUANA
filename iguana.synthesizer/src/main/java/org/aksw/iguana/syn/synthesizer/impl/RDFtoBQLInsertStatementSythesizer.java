package org.aksw.iguana.syn.synthesizer.impl;

import org.aksw.iguana.syn.model.statement.AbstractStatement;
import org.aksw.iguana.syn.model.statement.impl.BQLInsertStatement;
import org.aksw.iguana.syn.model.statement.impl.RDFNtripleStatement;
import org.aksw.iguana.syn.synthesizer.Synthesizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class RDFtoBQLInsertStatementSythesizer implements Synthesizer {

    private final AbstractStatement.StatementLanguageIdentifier sourceLanguage = AbstractStatement.StatementLanguageIdentifier.RDF_NTRIPLE;
    private final AbstractStatement.StatementLanguageIdentifier targetLanguage = AbstractStatement.StatementLanguageIdentifier.BQL_INSERT;

    public AbstractStatement.StatementLanguageIdentifier getSourceLanguage() {
        return sourceLanguage;
    }

    public AbstractStatement.StatementLanguageIdentifier getTargetLanguage() {
        return targetLanguage;
    }

    public static BQLInsertStatement synthesizeBQLStatementFromRDFStatement(RDFNtripleStatement rdfNtripleStatement) {
        //TODO: Better Abstraction in the Receivment of Control-Symbol characters according to Statement Class Instance

        HashMap<AbstractStatement.StatementPartIdentifier, String> synthesizedStatementParts = new HashMap<>();

        synthesizedStatementParts.put(AbstractStatement.StatementPartIdentifier.SUBJECT, rdfNtripleStatement.getSubject());
        synthesizedStatementParts.put(AbstractStatement.StatementPartIdentifier.PREDICATE, rdfNtripleStatement.getPredicate());
        synthesizedStatementParts.put(AbstractStatement.StatementPartIdentifier.OBJECT, rdfNtripleStatement.getObject());

        HashMap<AbstractStatement.StatementPartIdentifier, ArrayList<AbstractStatement.StatementControlSymbol>> statementControlSymbolsToSynthesize = new HashMap<>();

        statementControlSymbolsToSynthesize.put(AbstractStatement.StatementPartIdentifier.SUBJECT,
                new ArrayList<AbstractStatement.StatementControlSymbol>(Arrays.asList(
                        AbstractStatement.StatementControlSymbol.URI_NODE_SLASH,
                        AbstractStatement.StatementControlSymbol.URI_NODE_PROTOCOL_COLON,
                        AbstractStatement.StatementControlSymbol.URI_NODE_FULLSTOP,
                        AbstractStatement.StatementControlSymbol.URI_NODE_OPENING_BRACKET,
                        AbstractStatement.StatementControlSymbol.URI_NODE_CLOSING_BRACKET
                    )
                )
        );

        statementControlSymbolsToSynthesize.put(AbstractStatement.StatementPartIdentifier.PREDICATE,
                new ArrayList<AbstractStatement.StatementControlSymbol>(Arrays.asList(
                       AbstractStatement.StatementControlSymbol.URI_PREDICATE_SLASH,
                        AbstractStatement.StatementControlSymbol.URI_PREDICATE_PROTOCOL_COLON,
                        AbstractStatement.StatementControlSymbol.URI_PREDICATE_FULLSTOP,
                        AbstractStatement.StatementControlSymbol.URI_PREDICATE_OPENING_BRACKET,
                        AbstractStatement.StatementControlSymbol.URI_PREDICATE_CLOSING_BRACKET
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
                    /**Actual statement-part synthesization through character replace*/
                    synthesizedStatementParts.put(currentStatementPartIdentifier,
                            synthesizedStatementParts.get(currentStatementPartIdentifier).replace(
                                    RDFNtripleStatement.getStatementControlSymbol(currentStatementPartControlSymbolToSynthesize),
                                    BQLInsertStatement.getStatementControlSymbol(currentStatementPartControlSymbolToSynthesize)
                            )
                    );
            }

        }

        return new BQLInsertStatement(
                synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.SUBJECT),
                synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.PREDICATE),
                synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.OBJECT)
        );
    }

}
