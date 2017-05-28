/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.libs;

import java.awt.Point;

/**
 *
 *
 * @see <a href="http://itpro.nikkeibp.co.jp/atcl/column/15/120700278/011300004/?P=5">
 * リスト7●Scaleインタフェースの実装クラス</a>
 * @author Toast kid
 *
 */
public class Scales {

    /**
     * calculate euclid distance.
     * @param p1
     * @param p2
     * @return
     */
    public static double euclid(final Point p1, final Point p2) {
        final double squareDistance = Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2);
        return Math.sqrt(squareDistance);
    }

    /**
     * calculate manhattan distance.
     * @param p1
     * @param p2
     * @return
     */
    public static double manhattan(final Point p1, final Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }
}
