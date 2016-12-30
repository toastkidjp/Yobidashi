package jp.toastkid.article.converter;

import java.util.ArrayList;
import java.util.List;

import jp.toastkid.libs.utils.FileUtil;

/**
 * Definition of football formation.
 *
 * @author Toast kid
 *
 */
public final class Formation {

    /** リソースフォルダ */
    private static final String RESOURCES_DIR = "assets/football/";

    /** サッカーのプレイ人数 */
    private static final int ELEVEN   = 11;

    /** 4-4-2 の 守備的 MF 2人 */
    public  static final int DV_4_4_2 = 0;

    /** 4-2-3-1 */
    public  static final int TV_4_5_1 = 1;

    /** 4-2-3-1 */
    public  static final int DV_4_5_1 = 2;

    /** 3-5-1-1 */
    private static final int TV_3_6_1 = 3;

    /**
     * ピッチの HTML を構築して返す.
     * @param team
     * @param formation
     * @return ピッチの HTML 文字列表現
     */
    public static final String getPitch(
            final List<Footballer> team,
            final int formation
            ) {
        final StringBuilder output = new StringBuilder();
        output.append("<table><tr></tr><tr><td>");
        // ピッチ
        output.append("<div class=\"pitch\" style=\"position:relative;\">");
        output.append("<p><img src=\"")
              .append(Formation.class.getClassLoader().getResource("assets/").toString())
              .append("football/pitch.png\" height=\"350\" width=\"280\"></a></p>");
        final List<String> positionList = getPositionList(formation);
        for (int i = 0; i < ELEVEN; i++) {
            final Footballer fb = team.get(i);
            final String[] pos = positionList.get(i).split("\t");
            output.append("<div style=\"position: absolute; text-align: center; left:");
            output.append(pos[0]);
            output.append("px; top: ");
            output.append(pos[1]);
            output.append("px;\"><span class=\"pitch_name ");
            output.append(getColor(fb.position));
            output.append("\"><b>");
            output.append(fb.familyName );
            output.append("</b></span><br><span class=\"pitch_name ");
            output.append(getColor(fb.position));
            output.append("\">");
            output.append("# ");
            output.append(fb.number);
            output.append("</div>");
        }
        output.append("</div>");
        output.append("</td><td>");
        // table.
        output.append("<table><tr><th> No </th><th> Pos </th><th> Name</th></tr>");
        for (final Footballer f : team) {
            output.append("<tr><td> ");
            output.append(f.number);
            output.append(" </td><td> ");
            output.append(f.position);
            output.append(" </td><td> ");
            output.append(f.familyName);
            output.append("</td></tr>");
        }
        output.append("</table>");
        output.append("</td></tr></table>");
        return output.toString();
    }

    /**
     * ポジションごとの文字色を返す.この文字色はサカつくシリーズの定義に由来する.
     * @param pos ポジション(Footballer クラスの定数)
     * @return ポジションごとの文字色
     */
    private static final String getColor(final Footballer.Position pos) {
        if (Footballer.Position.GK.equals(pos)) {
            return "pitch_gk";
        } else if (Footballer.Position.DF.equals(pos)) {
            return "pitch_df";
        } else if (Footballer.Position.MF.equals(pos)) {
            return "pitch_mf";
        } else {
            return "pitch_fw";
        }
    }

    /**
     * フォーメーションに応じたポジション一覧を返す.
     * @param formation このクラスのフォーメーション定数
     * @return ポジション一覧
     */
    private static final List<String> getPositionList(final int formation) {
        final List<String> positionList = new ArrayList<String>(ELEVEN);
        if (DV_4_4_2 == formation) {
            positionList.addAll(
                    FileUtil.readLinesFromStream(
                            RESOURCES_DIR + "DV_4_4_2.txt",
                            "utf-8"
                    )
                );
        } else if (DV_4_5_1 == formation) {
            positionList.addAll(
                    FileUtil.readLinesFromStream(
                            RESOURCES_DIR + "DV_4_5_1.txt",
                            "utf-8"
                    )
                );
        } else if (TV_4_5_1 == formation) {
            positionList.addAll(
                    FileUtil.readLinesFromStream(
                            RESOURCES_DIR + "TV_4_5_1.txt",
                            "utf-8"
                    )
                );
        } else if (TV_3_6_1 == formation) {
            positionList.addAll(
                    FileUtil.readLinesFromStream(
                            RESOURCES_DIR + "TV_3_6_1.txt",
                            "utf-8"
                    )
                );
        }
        return positionList;
    }

    /**
     * 文字列からフォーメーションを判定し、それを表す定数値を返す.
     * @param str 文字列
     * @return フォーメーション定数
     */
    public static final int parseFormation(final String str) {
        final String target = str.toLowerCase().replace("{formation:", "");
        if (target.startsWith("4-5-1tv")
                || target.startsWith("4-3-2-1")) {
            return TV_4_5_1;
        } else if (target.startsWith("4-5-1dv")
                || target.startsWith("4-2-3-1")) {
            return DV_4_5_1;
        } else if (target.startsWith("3-6-1tv")
                || target.startsWith("3-5-1-1")) {
            return TV_3_6_1;
        }
        return DV_4_4_2;
    }
}
