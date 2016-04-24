package jp.toastkid.libs.comparator;

import java.util.Comparator;
import java.util.Map;
/**
 * Map の value でソートするための比較のクラス
 * @see <a href="http://www.mlab.im.dendai.ac.jp/~yamada/ir/Map/sort.html">引用元</a>
 * @author Toast kid
 */
public final class NumberMapComparator implements Comparator<String> {

    /** マップ */
    private final Map<String,?> map;

    /**
     * 与えられたマップで初期化
     */
    public NumberMapComparator(final Map<String,?> map) {
        this.map = map;
    }

    /** key 2つが与えられたときに、その value で比較 */
    @Override
    public int compare(final String key1, final String key2) {
        // value を取得
        final double i1 = Double.parseDouble(map.get(key1).toString());
        final double i2 = Double.parseDouble(map.get(key2).toString());
        // value の降順, valueが等しいときは key の辞書順
        if (i1 == i2) {
            return key1.toLowerCase().compareTo(key2.toLowerCase());
        } else if(i1 < i2){
            return 1;
        } else {
            return -1;
        }
    }

}
