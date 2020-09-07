package org.aksw.iguana.syn.model.statement;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractStatement implements Statement{

    //TODO enable inhertance for constrolSymbol Methods

    public static List<StatementControlSymbol> getStatemenControlSymbolIdentifiers () {
        return Arrays.asList(StatementControlSymbol.class.getEnumConstants());
    }

    //TODO enable inhertance for constrolSymbol Methods

    // public abstract String getStatementControlSymbol (StatementControlSymbol statementControlSymbol);

    //TODO enable inhertance for constrolSymbol Methods

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
