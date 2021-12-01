/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2020 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util.validation.validator;

import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.grant.Revoke;
import net.sf.jsqlparser.util.validation.ValidationCapability;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;

/**
 * @author gitmotte
 */
public class RevokeValidator extends AbstractValidator<Revoke> {

    @Override
    public void validate(Revoke revoke) {
        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, Feature.grant);
            if (isNotEmpty(revoke.getUsers())) {
                revoke.getUsers().forEach(u -> validateName(NamedObject.user, u));
            }
            if (revoke.getRole() != null) {
                validateName(NamedObject.role, revoke.getRole());
            }

            // can't validate grant.getObjectName() - don't know the kind of this object.
        }
    }

}
