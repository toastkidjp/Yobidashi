package jp.toastkid.gui.jfx.script;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import jp.toastkid.gui.jfx.script.ClojureRunner;
import jp.toastkid.gui.jfx.script.GroovyRunner;
import jp.toastkid.gui.jfx.script.JavaScriptRunner;
import jp.toastkid.gui.jfx.script.Language;
import jp.toastkid.gui.jfx.script.ScriptRunner;
import jp.toastkid.gui.jfx.script.ShellRunner;

/**
 * {@link ScriptRunner}'s test.
 * @author Toast kid
 *
 */
public class ScriptRunnerTest {

    /**
     * check {@link ScriptRunner#find(Language)}.
     */
    @Test
    public void testFind() {
        assertTrue(ScriptRunner.find(Language.CLOJURE)    instanceof ClojureRunner);
        assertTrue(ScriptRunner.find(Language.GROOVY)     instanceof GroovyRunner);
        assertTrue(ScriptRunner.find(Language.SHELL)      instanceof ShellRunner);
        assertTrue(ScriptRunner.find(Language.JAVASCRIPT) instanceof JavaScriptRunner);
    }

}
