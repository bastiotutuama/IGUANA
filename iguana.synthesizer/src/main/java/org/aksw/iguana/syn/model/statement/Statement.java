package org.aksw.iguana.syn.model.statement;

public interface Statement {

    public abstract String getStatementControlSymbol (AbstractStatement.StatementControlSymbol statementControlSymbol);

    public String getSubject();

    public String getPredicate();

    public String getObject();

    public String getCompleteStatement();
}
