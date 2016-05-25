package jp.toastkid.gui.jfx.wiki.script;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * JavaRunner's test case.
 * @author Toast kid
 *
 */
public class JavaRunnerTest {

    /** Java Runner. */
    private JavaRunner javaRunner;

    /**
     * initialize each test method.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        javaRunner = new JavaRunner();
    }

    /**
     * test simple running.
     */
    @Test
    public void testSimpleRun() {
        assertEquals("hello", new JavaRunner().run("System.out.println(\"hello\");").get());
    }

    /**
     * test simple running.
     */
    @Test
    public void testUsableLambda() {
        assertEquals("hello", javaRunner.run(
                "StringBuilder sb = new StringBuilder();"
                + "Arrays.asList(\"h\", \"e\", \"l\", \"l\", \"o\").forEach(c -> {sb.append(c);});"
                + "System.out.println(sb.toString());").get());
    }

}
