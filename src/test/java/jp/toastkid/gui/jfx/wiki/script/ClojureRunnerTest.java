package jp.toastkid.gui.jfx.wiki.script;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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

    @Test
    public void test() {
        final ClojureRunner runner = new ClojureRunner();
        assertEquals("Hello,  World!", runner.run("(def foo \"World!\") (println \"Hello, \" foo)").get().trim());
    }

}
