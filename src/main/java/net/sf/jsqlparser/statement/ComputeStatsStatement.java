/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement;

import net.sf.jsqlparser.schema.Table;

public class ComputeStatsStatement implements Statement {

    private Table table;

    public ComputeStatsStatement() {
        // empty constructor
    }

    public ComputeStatsStatement(Table table) {
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "COMPUTE STATS" + table.getFullyQualifiedName();
    }

    @Override
    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

    public ComputeStatsStatement withTable(Table table) {
        this.setTable(table);
        return this;
    }
}
