package org.aksw.iguana.syn.synthesizer.impl;

import org.aksw.iguana.syn.model.statement.AbstractStatement;
import org.aksw.iguana.syn.model.statement.impl.BQLStatement;
import org.aksw.iguana.syn.model.statement.impl.RDFStatement;
import org.aksw.iguana.syn.synthesizer.Synthesizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class RDFtoBQLInsertStatementSythesizer implements Synthesizer {

    private final AbstractStatement.StatementLanguageIdentifier sourceLanguage = AbstractStatement.StatementLanguageIdentifier.RDF;
    private final AbstractStatement.StatementLanguageIdentifier targetLanguage = AbstractStatement.StatementLanguageIdentifier.BQL_INSERT;

    public AbstractStatement.StatementLanguageIdentifier getSourceLanguage() {
        return sourceLanguage;
    }

    public AbstractStatement.StatementLanguageIdentifier getTargetLanguage() {
        return targetLanguage;
    }

    public BQLStatement synthesizeBQLStatementFromRDFStatement(RDFStatement rdfStatement) {
        //TODO: Better Abstraction in the Receivment of Control-Symbol characters according to Statement Class Instance

        HashMap<AbstractStatement.StatementPartIdentifier, String> synthesizedStatementParts = new HashMap<>();

        synthesizedStatementParts.put(AbstractStatement.StatementPartIdentifier.SUBJECT, rdfStatement.getSubject());
        synthesizedStatementParts.put(AbstractStatement.StatementPartIdentifier.PREDICATE, rdfStatement.getPredicate());
        synthesizedStatementParts.put(AbstractStatement.StatementPartIdentifier.OBJECT, rdfStatement.getPredicate());

        System.out.println("SUBJECT as RDF");
        System.out.println(synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.SUBJECT));

        HashMap<AbstractStatement.StatementPartIdentifier, ArrayList<AbstractStatement.StatementControlSymbol>> statementControlSymbolsToSynthesize = new HashMap<>();

        statementControlSymbolsToSynthesize.put(AbstractStatement.StatementPartIdentifier.SUBJECT,
                        new ArrayList<AbstractStatement.StatementControlSymbol>(Arrays.asList(
                                AbstractStatement.StatementControlSymbol.URI_NODE_OPENING_BRACKET,
                                AbstractStatement.StatementControlSymbol.URI_NODE_CLOSING_BRACKET,
                                AbstractStatement.StatementControlSymbol.URI_NODE_PROTOCOL_COLON,
                                AbstractStatement.StatementControlSymbol.URI_NODE_SLASH,
                                AbstractStatement.StatementControlSymbol.URI_NODE_FULLSTOP
                            )
                        )
        );


        /** Synthesisation of SUBJECT Symbols*/
        for (AbstractStatement.StatementControlSymbol currentSubjectStatementControlSymbolsToSynthesize :
                statementControlSymbolsToSynthesize.get(AbstractStatement.StatementPartIdentifier.SUBJECT)) {

                /**Actual Synthesization through character replace*/
                synthesizedStatementParts.put(AbstractStatement.StatementPartIdentifier.SUBJECT,
                        synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.SUBJECT).replace(
                            RDFStatement.getStatementControlSymbol(currentSubjectStatementControlSymbolsToSynthesize),
                            BQLStatement.getStatementControlSymbol(currentSubjectStatementControlSymbolsToSynthesize)
                        )
                    );

        }

        System.out.println("SUBJECT as synthesized BQL Insert");
        System.out.println(synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.SUBJECT));


        return new BQLStatement(
                synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.SUBJECT),
                synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.PREDICATE),
                synthesizedStatementParts.get(AbstractStatement.StatementPartIdentifier.OBJECT)
        );
    }

}
