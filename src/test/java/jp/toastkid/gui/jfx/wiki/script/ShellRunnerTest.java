package jp.toastkid.gui.jfx.wiki.script;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import jp.toastkid.gui.jfx.script.JavaScriptRunner;
import jp.toastkid.gui.jfx.script.ShellRunner;

/**
 * check {@link ShellRunner}.
 * @author Toast kid
 *
 */
public class ShellRunnerTest {

    /** test object. */
    private ShellRunner runner;

    /**
     * set up.
     */
    @Before
    public void setUp() {
        runner = new ShellRunner();
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
     * check printable.
     */
    @Test
    public void test() {
        System.out.println(new ShellRunner().run("ls").get());
        System.out.println(new ShellRunner().run("echo \"tomato\" | sed 's/o/a/g'").get());
        System.out.println(new ShellRunner().run("curl 'http://www.yahoo.co.jp'").get());
    }

    /**
     * monkey test.
     */
    @Test
    public void testRunByMonkey() {
        runner.run("jaisdfe");
    }
}
