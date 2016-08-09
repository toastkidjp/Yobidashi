package jp.toastkid.script;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import jp.toastkid.script.JavaScriptRunner;

/**
 * {@link JavaScriptRunner}'s test.
 * @author Toast kid
 *
 */
public class JavaScriptRunnerTest {

    /** test object. */
    private JavaScriptRunner runner;

    /**
     * initialize test object.
     */
    @Before
    public void setUp() {
        runner = new JavaScriptRunner();
    }

    /**
     * check {@link JavaScriptRunner#run(String)}.
     */
    @Test
    public void testRunNullable() {
        assertEquals(Optional.empty(), runner.run(null));
    }

    /**
     * check {@link JavaScriptRunner#run(String)}.
     */
    @Test
    public void testRunWithEmpty() {
        assertEquals(Optional.empty(), runner.run(""));
    }

    /**
     * check {@link JavaScriptRunner#run(String)}.
     */
    @Test
    public void testRun() {
        assertEquals("Hello world.", runner.run("print('Hello world.');").get().trim());
    }

    /**
     * monkey test.
     */
    @Test
    public void testRunByMonkey() {
        runner.run("jaisdfe");
    }
}
