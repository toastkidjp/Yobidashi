package jp.toastkid.libs.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import jp.toastkid.libs.Pair;
import jp.toastkid.libs.utils.GzipUtils;
import net.arnx.jsonic.JSONException;

import org.junit.Test;

/**
 * check {@link GzipUtils} behavior.
 * @author Toast kid
 *
 */
public class GzipUtilsTest {
    /** resources dir. */
    private static final String RESOURCES_DIR = "./src/test/resources/utils/";

    /** testing file name. */
    private static final String FILE_NAME = "pair.gz";

    /** testing object. */
    private static final Pair<String, Integer> PAIR = Pair.of("tomato", 200);

    /**
     * check behavior gzip().
     * @throws JSONException
     * @throws IOException
     */
    @Test
    public final void testGzip() throws JSONException, IOException {
        assertEquals(
                "[31, -117, 8, 0, 0, 0, 0, 0, 0, 0, -85, 86, -54, 73, 77, 43, 81, -78, 82, 42, -55,"
                + " -49, 77, 44, -55, 87, -46, 81, 42, -54, 76, -49, 0, 10, 24, 25, 24, -44, 2,"
                + " 0, 32, 114, -102, -113, 29, 0, 0, 0]",
                Arrays.toString(GzipUtils.gzip(PAIR)));;
    }

    /**
     * check behavior gunzip().
     * @throws JSONException
     * @throws IOException
     */
    @Test
    public final void testGunzip() throws JSONException, IOException {
        assertEquals("{left=tomato, right=200}", GzipUtils.gunzip(GzipUtils.gzip(PAIR)));
    }

    /**
     * check read behavior.
     * @throws IOException
     */
    @Test
    public final void testRead() throws IOException {
        assertEquals(PAIR.toString(), GzipUtils.read(RESOURCES_DIR + FILE_NAME).get(0));
    }

    /**
     * check write behavior.
     * @throws IOException
     */
    @Test
    public final void testWrite() throws IOException {
        GzipUtils.write(PAIR, FILE_NAME);
        assertEquals(PAIR.toString(), GzipUtils.read(FILE_NAME).get(0));
        new File(FILE_NAME).delete();
    }

}
