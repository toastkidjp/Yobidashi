package jp.toastkid.libs;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import org.junit.Test;

/**
 * Props's test cases.
 * @author Toast kid
 *
 */
public class PropsTest {

    /** test resources directory path. */
    private static final String PATH = "src/test/resources/libs/props";

    /** expected object. */
    private static final Properties EXPECTED = new Properties();
    static {
        EXPECTED.put("tomato", "120");
        EXPECTED.put("editorPath", "C:/Program Files (x86)/TeraPad/TeraPad.exe");
        EXPECTED.put("onion", "百円");
    }

    /**
     * check behavior readDir method.
     */
    @Test
    public final void testReadDirStr() {
        assertEquals(Optional.empty(), Props.readDir((String) null));
        assertEquals(Optional.empty(), Props.readDir("notExits"));
        assertEquals(EXPECTED, Props.readDir(PATH).get());
    }

    /**
     * check behavior readDir method.
     */
    @Test
    public final void testReadDirPath() {
        assertEquals(Optional.empty(), Props.readDir((Path) null));
        assertEquals(Optional.empty(), Props.readDir(Paths.get("notExits")));
        assertEquals(EXPECTED, Props.readDir(Paths.get(PATH)).get());
    }

}
