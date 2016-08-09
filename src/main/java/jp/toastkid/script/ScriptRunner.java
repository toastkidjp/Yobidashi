package jp.toastkid.script;

import java.util.Optional;

import javax.script.ScriptEngine;

/**
 * Script language's script runner.
 * @author Toast kid
 * @see <a href="http://oshiete.goo.ne.jp/qa/4768100.html">System.out.printlnの出力先</a>
 * @see <a href="http://tango.hatenablog.com/entry/20111118/1321647579">
 * 変更した標準出力の出力先を元の標準出力に戻す方法</a>
 */
public abstract class ScriptRunner {

    /** System line separator. */
    protected static final String LINE_SEPARATOR = System.lineSeparator();

    /** Groovy's runner. */
    private static final ScriptRunner GROOVY  = new GroovyRunner();

    /** JavaScript's runner. */
    private static final ScriptRunner JS      = new JavaScriptRunner();

    /** Python's runner. */
    private static final ScriptRunner PYTHON  = new PythonRunner();

    /** Clojure's runner. */
    private static final ScriptRunner CLOJURE = new ClojureRunner();

    /** Shell's runner. */
    private static final ScriptRunner SHELL   = new ShellRunner();

    /** ScriptEngine. */
    protected ScriptEngine engine;

    /**
     * run and return result.
     * @return reslut or empty.
     */
    public abstract Optional<String> run(final String script);

    /**
     * file の拡張子から適切な ScriptEngine を返す.
     * @param fileName file name contains extention.
     * @return appropriate ScriptEngine.
     */
    public static ScriptRunner find(final Language languageName) {
        switch (languageName) {
            case JAVASCRIPT:
                return JS;
            case CLOJURE:
                return CLOJURE;
            case PYTHON:
                return PYTHON;
            case SHELL:
                return SHELL;
            case GROOVY:
            default:
                return GROOVY;
        }
    }
}
