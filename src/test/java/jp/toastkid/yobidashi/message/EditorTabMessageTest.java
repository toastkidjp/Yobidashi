package jp.toastkid.yobidashi.message;

import static org.junit.Assert.assertSame;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

/**
 * {@link EditorTabMessage}'s test case.
 *
 * @author Toast kid
 *
 */
public class EditorTabMessageTest {

    /**
     * Test of {@link EditorTabMessage#make(Path)}.
     */
    @Test
    public void testMake() {
        final Path path = Paths.get("temp");
        final EditorTabMessage message = EditorTabMessage.make(path);
        assertSame(path, message.getPath());
    }

}
