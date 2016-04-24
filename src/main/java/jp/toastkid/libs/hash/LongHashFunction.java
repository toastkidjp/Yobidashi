package jp.toastkid.libs.hash;

import jp.toastkid.libs.utils.Strings;

/**
 * long 値のHashFunction.
 * @author Toast kid
 *
 */
public class LongHashFunction implements HashFunction {

    @Override
    public long hash(final Object key) {
        if (key instanceof String) {
            return Strings.longHash((String) key);
        } else {
            return Strings.longHash(key.toString());
        }
    }

}
