/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.libs.lambda;

/**
 * Common predicates.
 *
 * @author Toast kid
 */
public final class Filters {

    /**
     * Deny make instance.
     */
    private Filters() {
        // NOP.
    }

    /**
     * Return Predicate use filter out null.
     * @return filter out null objects.
     */
    public static <T> boolean isNotNull(final T t) {
        return t != null;
    }

    /**
     * Return Predicate use filter out not-null.
     * @return filter out not-null objects.
     */
    public static <T> boolean isNull(final T t) {
        return t == null;
    }
}
