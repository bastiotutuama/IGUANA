package org.aksw.iguana.syn.model.statement;

public interface Statement {
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
