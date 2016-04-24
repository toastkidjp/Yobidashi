package jp.toastkid.libs.tinysegmenter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
/**
 *
 * <HR>
 * (130404) 作成開始<BR>
 * @author Toast kid
 */
public final class CharacterClassifier {

    private final String[] patterns = {
            "[一二三四五六七八九十百千万億兆]:M",
            "[一-龠々〆ヵヶ]:H",
            "[ぁ-ん]:I",
            "[ァ-ヴーｱ-ﾝﾞｰ]:K",
            "[a-zA-Zａ-ｚＡ-Ｚ]:A",
            "[0-9０-９]:N"
          };

    private final Map<String,Pattern> pMap;

    /**
     *<HR>
     * (130404) 作成開始<BR>
     */
    public CharacterClassifier(){
        pMap = new HashMap<String,Pattern>(patterns.length);
        for (int i = 0; i < patterns.length; i++) {
            final String[] pats = patterns[i].split(":");
            pMap.put(pats[1], Pattern.compile(pats[0], Pattern.DOTALL));
        }
    }
    /**
     * 文字種別を判定して返す。
     * <HR>
     * (130404) 作成開始<BR>
     * @param str
     * @return 文字種別
     */
    public String classify( final String str){
        final Iterator<String> iter = pMap.keySet().iterator();
        while (iter.hasNext()) {
            final String key = iter.next();
            if ( pMap.get(key).matcher(str).find() ) {
                return key;
            }
        }
        return "O";
    }
}
