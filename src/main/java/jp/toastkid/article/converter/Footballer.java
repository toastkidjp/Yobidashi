/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.article.converter;

import jp.toastkid.libs.utils.MathUtil;

/**
 *
 * @author Toast kid
 *
 */
public final class Footballer {

    /** Football position. */
    public static enum Position {
        GK, DF, MF, FW
    };

    /** family name. */
    public String familyName;

    /** full name. */
    public String fullName;

    /** position. */
    public Position position;

    /** uniform number. */
    public int number;

    /**
     * @param str
     */
    public static final Footballer getFootballer(final String str) {
        final String[] line = str.split("\\|");
        final Footballer fb = new Footballer();
        fb.number = MathUtil.getInt(line[0].trim());
        if (1 < line.length) {
            fb.position = parsePosition(line[1].trim());
        }
        if (2 < line.length) {
            fb.familyName = line[2].trim();
        }
        return fb;
    }

    /**
     * ポジション表記に合った定数を返す。
     * @param pos ポジション表記(DF,GK 等)
     * @return ポジション表記に合った定数
     */
    private static final Position parsePosition(final String pos) {
        if (Position.GK.toString().equals(pos)) {
            return Position.GK;
        } else if (Position.DF.toString().equals(pos)) {
            return Position.DF;
        } else if (Position.MF.toString().equals(pos)) {
            return Position.MF;
        } else {
            return Position.FW;
        }
    }
}
