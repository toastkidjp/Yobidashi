package jp.toastkid.gui.jfx.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import jp.toastkid.gui.jfx.script.ClojureRunner;

/**
 * {@link ClojureRunner}'s test,
 *
 * @author Toast kid
 * @see <a href=
 *      "https://github.com/ato/clojure-jsr223/blob/master/samples/jsr223/EvalScript.java">
 *      EvalScript.java</a>
 *
 */
public class ClojureRunnerTest {

    /** test object. */
    private ClojureRunner runner;

    /**
     * init every execute.
     */
    @Before
    public void setUp() {
        runner = new ClojureRunner();
    }

    /**
     * check {@link ClojureRunner#run(String)}.
     */
    @Test
    public void testRunNullable() {
        assertEquals(Optional.empty(), runner.run(null));
    }

    /**
     * check {@link ClojureRunner#run(String)}.
     */
    @Test
    public void testRunWithEmpty() {
        assertEquals(Optional.empty(), runner.run(""));
    }

    /**
     * check {@link ClojureRunner#run(String)}.
     */
    @Test
    public void testRun() {
        assertEquals(
                "Hello,  World!",
                runner.run("(def foo \"World!\") (println \"Hello, \" foo)"
                        ).get().trim());
    }

    /**
     * monkey test.
     */
    @Test
    public void testRunByMonkey() {
        runner.run("jaisdfe");
    }

    /**
     * check {@link ClojureRunner#put(String, Object)} and {@link ClojureRunner#get(String)}.
     */
    @Test
    public void testPutAndGet() {
        runner.put("tomato", 130);
        assertEquals(130, runner.get("tomato"));
        assertNull(runner.get("ne"));
    }
}
