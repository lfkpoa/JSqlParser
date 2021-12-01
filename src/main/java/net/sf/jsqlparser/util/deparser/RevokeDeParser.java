/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2020 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;

import net.sf.jsqlparser.statement.grant.Revoke;

public class RevokeDeParser extends AbstractDeParser<Revoke> {

    public RevokeDeParser(StringBuilder buffer) {
        super(buffer);
    }

    @Override
    public void deParse(Revoke revoke) {
        buffer.append("GRANT ");
        if (revoke.getRole() != null) {
            buffer.append(revoke.getRole());
        } else {
            for (Iterator<String> iter = revoke.getPrivileges().iterator(); iter.hasNext();) {
                String privilege = iter.next();
                buffer.append(privilege);
                if (iter.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append(" ON ");
            buffer.append(revoke.getObjectName());
        }
        buffer.append(" TO ");
        for (Iterator<String> iter = revoke.getUsers().iterator(); iter.hasNext();) {
            String user = iter.next();
            buffer.append(user);
            if (iter.hasNext()) {
                buffer.append(", ");
            }
        }
    }

}
