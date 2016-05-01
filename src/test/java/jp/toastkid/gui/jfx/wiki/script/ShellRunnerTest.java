package jp.toastkid.gui.jfx.wiki.script;

import org.junit.Test;

/**
 * check {@link ShellRunner}.
 * @author Toast kid
 *
 */
public class ShellRunnerTest {

    /**
     * check printable.
     */
    @Test
    public void test() {
        System.out.println(new ShellRunner().run("ls").get());
        System.out.println(new ShellRunner().run("echo \"tomato\" | sed 's/o/a/g'").get());
        System.out.println(new ShellRunner().run("curl 'http://www.yahoo.co.jp'").get());
    }

}
