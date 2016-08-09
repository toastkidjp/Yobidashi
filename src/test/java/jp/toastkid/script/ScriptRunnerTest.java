package jp.toastkid.script;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import jp.toastkid.script.ClojureRunner;
import jp.toastkid.script.GroovyRunner;
import jp.toastkid.script.JavaScriptRunner;
import jp.toastkid.script.Language;
import jp.toastkid.script.ScriptRunner;
import jp.toastkid.script.ShellRunner;

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
