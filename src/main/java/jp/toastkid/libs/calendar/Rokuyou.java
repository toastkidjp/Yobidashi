package jp.toastkid.libs.calendar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 六曜.
 * @author Toast kid
 *
 */
public class Rokuyou {
    /**
     * deny make instance.
     */
    private Rokuyou() {
        // NOOP.
    }
    /** 六曜一覧. */
    public static final List<String> rokuyo
        = Collections.unmodifiableList(Arrays.asList(new String[]{
             "先勝","友引","先負","仏滅","大安","赤口"
     }));
}
