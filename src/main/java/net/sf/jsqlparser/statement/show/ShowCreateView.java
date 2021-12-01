/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement.show;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

public class ShowCreateView implements Statement {

    private Table view;

    @Override
    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

    public Table getView() {
        return view;
    }

    public void setView(Table view) {
        this.view = view;
    }

    @Override
    public String toString() {
        StringBuilder sql = new StringBuilder("SHOW CREATE ");
        sql.append("VIEW ");
        sql.append(view);
        return sql.toString();
    }

    public ShowCreateView withView(Table view) {
        this.setView(view);
        return this;
    }
}
