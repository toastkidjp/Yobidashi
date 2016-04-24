package jp.toastkid.libs.hash;

/**
 * 任意のハッシュ関数
 * @author Toast kid
 *
 */
public interface HashFunction {
    /**
     * 文字列からハッシュ値を算出して返す.
     * @param key
     * @return
     */
    public long hash(final Object key);

}
