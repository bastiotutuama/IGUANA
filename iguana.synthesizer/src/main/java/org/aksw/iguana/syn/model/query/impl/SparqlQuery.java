package org.aksw.iguana.syn.model.query.impl;

import org.aksw.iguana.syn.model.query.AbstractQuery;
import org.aksw.iguana.syn.model.query.Query;
import org.aksw.iguana.syn.model.query.QueryClause;
import org.apache.jena.query.SortCondition;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.expr.ExprVar;
import org.apache.jena.sparql.expr.aggregate.*;
import org.apache.jena.sparql.syntax.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class SparqlQuery extends AbstractQuery implements Query {

    private org.apache.jena.query.Query jenaSparqlQuery;
    private static org.apache.jena.query.QueryType[] supportedJenaQueryTypes = {org.apache.jena.query.QueryType.SELECT, org.apache.jena.query.QueryType.CONSTRUCT};
    private List<Element> jenaQueryPatternElements;
    private List<TriplePath> jenaQueryPatternTriplePaths = new ArrayList<>();
    private List<String> queryPatternTriples = new ArrayList<>();

    public SparqlQuery(org.apache.jena.query.Query jenaSparqlQuery) {

        this.jenaSparqlQuery = jenaSparqlQuery;
        this.jenaQueryPatternElements = ((ElementGroup)  this.jenaSparqlQuery.getQueryPattern()).getElements();

        //Map Jena-QueryType to Synthesizer-Query-Type
        switch (this.jenaSparqlQuery.queryType()) {
            case SELECT:
                setQueryType(QueryType.SELECT);
                break;

            case CONSTRUCT:
                setQueryType(QueryType.CONSTRUCT);
                break;

            default:
                setQueryType(QueryType.UNSUPPORTED);
                break;
        }

        //TYPE CLAUSE
        SparqlQueryClause typeClause = new SparqlQueryClause(QueryClauseType.TYPE_CLAUSE, jenaSparqlQuery.queryType().toString());
        typeClause.setClauseKeyword(jenaSparqlQuery.queryType().toString());
        setQueryClauseForType(typeClause);

        //RESULT VARIABLES CLAUSE
        String resultVariablesClauseString = ""; //TODO
        SparqlQueryClause resultVariablesClause = new SparqlQueryClause(QueryClauseType.RESULT_VARIABLES_CLAUSE, resultVariablesClauseString);
        resultVariablesClause.addToClauseElements(jenaSparqlQuery.getResultVars());
        jenaSparqlQuery.getProject().getExprs().forEach(new BiConsumer<Var, Expr>() {
            @Override
            public void accept(Var variable, Expr expression) {
                if (expression instanceof ExprAggregator){
                    Aggregator aggregator = ((ExprAggregator) expression).getAggregator();
                    resultVariablesClause.addToClauseElementAggregatorExpressions(variable.getVarName(), aggregator.toString());
                }
            }
        });
        setQueryClauseForType(resultVariablesClause);

        //PATTERN CLAUSE
        fillQueryPatternTriplePathsFromJenaQueryPatternElements(jenaQueryPatternElements);
        SparqlQueryClause patternClause = new SparqlQueryClause(QueryClauseType.PATTERN_CLAUSE, Arrays.toString(getQueryPatternTriples().toArray()));
        patternClause.addToClauseElements(getQueryPatternTriples());
        setQueryClauseForType(patternClause);

        //ORDER BY CLAUSE
        if (jenaSparqlQuery.hasOrderBy() && orderByHasOnlySimpleDirectionExpressions()) {
            String sortOrderValue = jenaSparqlQuery.getOrderBy().toString();
            SparqlQueryClause orderByClause = new SparqlQueryClause(QueryClauseType.RESULTS_ORDER_CLAUSE, sortOrderValue);
            for (SortCondition currentSortCondition:jenaSparqlQuery.getOrderBy()) {
                QueryClause.SortOrder currentConditionSortOrder = currentSortCondition.getDirection() == -1 ? QueryClause.SortOrder.DESC : QueryClause.SortOrder.ASC;
                orderByClause.addToClauseSortConditions(currentSortCondition.getExpression().getVarName(), currentConditionSortOrder);
            }
            setQueryClauseForType(orderByClause);
        }

    }

    @Override
    public QueryLanguage getQueryLanguage() {
        return QueryLanguage.SPARQL;
    }

    @Override
    public String getQueryAsString() {
        return jenaSparqlQuery.toString(Syntax.syntaxSPARQL_11);
    }


    public static org.apache.jena.query.QueryType[] getSupportedJenaQueryTypes() {
        return supportedJenaQueryTypes;
    }

    private List<Element> getJenaQueryPatternElements(){
        return jenaQueryPatternElements;
    }

    private Collection<Expr> getJenaSparqlResultExpressionsList(){
        return jenaSparqlQuery.getProject().getExprs().values();
    }

    private void fillQueryPatternTriplePathsFromJenaQueryPatternElements(List<Element> jenaQueryPatternElements){
        for (Element currentJenaQueryPatternElementPathBlock:jenaQueryPatternElements) {
            if (currentJenaQueryPatternElementPathBlock instanceof ElementPathBlock){
                ElementPathBlock tripleElementPathBlock = (ElementPathBlock) currentJenaQueryPatternElementPathBlock;
                List<TriplePath> currentTriplePaths = tripleElementPathBlock.getPattern().getList();
                for (TriplePath currentTriplePath:currentTriplePaths) {
                    addToJenaQuerryPatternTriplePathsAndTriples(currentTriplePath);
                }
            }
        }
    }

    private void addToJenaQuerryPatternTriplePathsAndTriples(TriplePath triplePath){
        jenaQueryPatternTriplePaths.add(triplePath);
        StringBuilder currentTriple = new StringBuilder();
        //Subject
        if (triplePath.getSubject().isURI()) {
            currentTriple.append("<" + triplePath.getSubject().toString() + ">");
        } else {
            currentTriple.append(triplePath.getSubject().toString());
        }
        currentTriple.append(" ");
        //Predicate
        if (triplePath.getPredicate().isURI()) {
            currentTriple.append("<" + triplePath.getPredicate().toString() + ">");
        } else {
            currentTriple.append(triplePath.getPredicate().toString());
        }
        currentTriple.append(" ");
        //Object
        if (triplePath.getObject().isURI()) {
            currentTriple.append("<" + triplePath.getObject().toString() + ">");
        } else {
            currentTriple.append(triplePath.getObject().toString());
        }
        queryPatternTriples.add(currentTriple.toString());
    }

    public List<String> getQueryPatternTriples() {
        return queryPatternTriples;
    }

    private boolean checkJenaQueryPatternElementsForSpecificElement(Class specificQueryPatternElement){
        for (Element currentQueryPatternElement:getJenaQueryPatternElements()) {
            if (specificQueryPatternElement.isInstance(currentQueryPatternElement))
                return true;
        }
        return false;
    }

    public boolean queryPatternContainsOptionalElement(){
       return checkJenaQueryPatternElementsForSpecificElement(ElementOptional.class);
    }

    public boolean queryPatternContainsUnionElement(){
        return checkJenaQueryPatternElementsForSpecificElement(ElementUnion.class);
    }

    public boolean queryPatternContainsFilterElement(){
        return checkJenaQueryPatternElementsForSpecificElement(ElementFilter.class);
    }

    public boolean queryPatternContainsNamedGraphElement(){
        return checkJenaQueryPatternElementsForSpecificElement(ElementNamedGraph.class);
    }

    public boolean resultVariableExpressionsContainAnAggregatorExpressionDifferentFromCountandSumOrANonAggratorExpression(){
        for (Expr jenaSparqlResultExpression:getJenaSparqlResultExpressionsList()) {
            if (jenaSparqlResultExpression instanceof ExprAggregator){
                Aggregator aggregator = ((ExprAggregator) jenaSparqlResultExpression).getAggregator();
                //if (!(aggregator instanceof AggCountVar || aggregator instanceof AggCountVarDistinct || aggregator instanceof AggSum))
                if (! (aggregator.getName().equals("COUNT") || aggregator.getName().equals("SUM")))
                    return true;
            } else {
                //Expression is no Aggregator
                return true;
            }
        }
        return false;
    }

    public boolean orderByHasOnlySimpleDirectionExpressions(){
        for (SortCondition currentSortCondition:jenaSparqlQuery.getOrderBy()) {
            if (!(currentSortCondition.getExpression() instanceof ExprVar)){
                return false;
            }
        }
        return true;
    }
}
