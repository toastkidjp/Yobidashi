package jp.toastkid.article.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.collections.impl.factory.Lists;
import org.junit.Test;

/**
 * {@link Formation}'s test.
 *
 * @author Toast kid
 *
 */
public class FormationTest {

    /**
     * Check {@link Formation#getPitch(java.util.List, Formation)}.
     */
    @Test
    public void testGetPitch() {
        final String pitch = Formation.getPitch(
                Lists.mutable.of(
                        Footballer.getFootballer("1 | GK | スポルティエッロ"),
                        Footballer.getFootballer("2 | RB | マヌエル・パブロ"),
                        Footballer.getFootballer("4 | CB | コロッチーニ"),
                        Footballer.getFootballer("5 | CB | カニーニ"),
                        Footballer.getFootballer("3 | LB | ブリーヴィオ"),
                        Footballer.getFootballer("6 | DMF | ベルナルディーニ"),
                        Footballer.getFootballer("8 | CMF | デフェウディス"),
                        Footballer.getFootballer("10 | OMF | デペトリス"),
                        Footballer.getFootballer("7 | RSMF | バルデス"),
                        Footballer.getFootballer("11 | LSMF | ドーニ"),
                        Footballer.getFootballer("9 | CF | デニス")
                        ),
                Formation.parseFormation("4-5-1tv")
            );
        assertTrue(pitch.contains(" height=\"350\" width=\"280\"></a></p><div style=\"position: absolute;"
                + " text-align: center; left:120px; top: 300px;\">"
                + "<span class=\"pitch_name pitch_gk\"><b>スポルティエッロ</b></span><br>"
                + "<span class=\"pitch_name pitch_gk\"># 1</div>"
                + "<div style=\"position: absolute; text-align: center; left:234px; top: 204px;\">"
                + "<span class=\"pitch_name pitch_fw\"><b>マヌエル・パブロ</b>"
                + "</span><br><span class=\"pitch_name pitch_fw\"># 2</div>"
                + "<div style=\"position: absolute; text-align: center; left:42px; top: 204px;\">"
                + "<span class=\"pitch_name pitch_fw\"><b>コロッチーニ</b></span><br>"
                + "<span class=\"pitch_name pitch_fw\"># 4</div>"
                + "<div style=\"position: absolute; text-align: center; left:96px; top: 234px;\">"
                + "<span class=\"pitch_name pitch_fw\"><b>カニーニ</b></span><br>"
                + "<span class=\"pitch_name pitch_fw\"># 5</div>"
                + "<div style=\"position: absolute; text-align: center; left:180px; top: 234px;\">"
                + "<span class=\"pitch_name pitch_fw\"><b>ブリーヴィオ</b></span><br>"
                + "<span class=\"pitch_name pitch_fw\"># 3</div>"
                + "<div style=\"position: absolute; text-align: center; left:138px; top: 168px;\">"
                + "<span class=\"pitch_name pitch_fw\"><b>ベルナルディーニ</b></span><br>"
                + "<span class=\"pitch_name pitch_fw\"># 6</div>"
                + "<div style=\"position: absolute; text-align: center; left:70px; top: 132px;\">"
                + "<span class=\"pitch_name pitch_fw\"><b>デフェウディス</b></span><br>"
                + "<span class=\"pitch_name pitch_fw\"># 8</div>"
                + "<div style=\"position: absolute; text-align: center; left:194px; top: 132px;\">"
                + "<span class=\"pitch_name pitch_fw\"><b>デペトリス</b></span><br>"
                + "<span class=\"pitch_name pitch_fw\"># 10</div>"
                + "<div style=\"position: absolute; text-align: center; left:234px; top: 80px;\">"
                + "<span class=\"pitch_name pitch_fw\"><b>バルデス</b></span><br>"
                + "<span class=\"pitch_name pitch_fw\"># 7</div>"
                + "<div style=\"position: absolute; text-align: center; left:42px; top: 80px;\">"
                + "<span class=\"pitch_name pitch_fw\"><b>ドーニ</b></span><br>"
                + "<span class=\"pitch_name pitch_fw\"># 11</div>"
                + "<div style=\"position: absolute; text-align: center; left:138px; top: 20px;\">"
                + "<span class=\"pitch_name pitch_fw\"><b>デニス</b></span><br>"
                + "<span class=\"pitch_name pitch_fw\"># 9</div></div></td><td><table><tr>"
                + "<th> No </th><th> Pos </th><th> Name</th></tr>"
                + "<tr><td> 1 </td><td> GK </td><td> スポルティエッロ</td></tr>"
                + "<tr><td> 2 </td><td> FW </td><td> マヌエル・パブロ</td></tr>"
                + "<tr><td> 4 </td><td> FW </td><td> コロッチーニ</td></tr>"
                + "<tr><td> 5 </td><td> FW </td><td> カニーニ</td></tr>"
                + "<tr><td> 3 </td><td> FW </td><td> ブリーヴィオ</td></tr>"
                + "<tr><td> 6 </td><td> FW </td><td> ベルナルディーニ</td></tr>"
                + "<tr><td> 8 </td><td> FW </td><td> デフェウディス</td></tr>"
                + "<tr><td> 10 </td><td> FW </td><td> デペトリス</td></tr>"
                + "<tr><td> 7 </td><td> FW </td><td> バルデス</td></tr>"
                + "<tr><td> 11 </td><td> FW </td><td> ドーニ</td></tr>"
                + "<tr><td> 9 </td><td> FW </td><td> デニス</td></tr>"
                + "</table></td></tr></table>"));
    }

    /**
     * Check {@link Formation#parseFormation(String)}.
     */
    @Test
    public void testParseFormation() {
        assertEquals(Formation.TV_4_5_1, Formation.parseFormation("4-5-1tv"));
        assertEquals(Formation.TV_3_6_1, Formation.parseFormation("3-6-1tv"));
        assertEquals(Formation.DV_4_4_2, Formation.parseFormation("4-4-2dv"));
        assertEquals(Formation.DV_4_5_1, Formation.parseFormation("4-5-1dv"));
    }

}
