package jp.toastkid.gui.jfx.wiki.script;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import jp.toastkid.libs.utils.CollectionUtil;

/**
 * Java code runner.
 *
 * @author Toast kid
 * @see <a href="http://triadsou.hatenablog.com/entry/20120623/1340454798">
 * Java Compiler API を使って何かしてみよう</a>
 */
public class JavaRunner extends ScriptRunner {

    /** base java source. */
    private static final String BASE_SOURCE =
            "import java.util.*;" +
            "public class Hello {" +
            "    public static List<Object> main(String args[]) {" +
            "        final List<Object> list = new ArrayList<>();" +
            "        ${code}" +
            "        return list;" +
            "    }" +
            "}";

    @Override
    public Optional<String> run(final String script) {
        return Optional.of(
                compile(BASE_SOURCE.replace("${code}", script).replace("System.out.println", "list.add"))
                );
    }

    public String compile(final String script) {

        //System.out.println(script);

        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector<JavaFileObject> diagnostics
            = new DiagnosticCollector<JavaFileObject>();

        final JavaFileObject file = new JavaSourceFromString("Hello", script);

        final String[] compileOptions = new String[] { "-d", "build/classes" };
        final Iterable<String> compilationOptionss = Arrays.asList(compileOptions);

        final Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        final StringWriter writer = new StringWriter();
        final CompilationTask task = compiler.getTask(
                writer,
                null,
                diagnostics,
                compilationOptionss,
                null,
                compilationUnits
                );

        final boolean success = task.call();

        if (!success) {
            System.out.println("Compiration failed.");
            return "";
        }
        try {
            final Class<?> clazz = Class.forName("Hello");
            final Method method = clazz.getMethod("main", new Class[] { String[].class });
            final List<Object> ret = (List<Object>) method.invoke(null, new Object[] { null });
            return CollectionUtil.implode(ret, System.lineSeparator());
        } catch (final ClassNotFoundException e) {
            System.err.println("Class not found: " + e);
            e.printStackTrace();
        } catch (final NoSuchMethodException e) {
            System.err.println("No such method: " + e);
        } catch (final IllegalAccessException e) {
            System.err.println("Illegal access: " + e);
        } catch (final InvocationTargetException e) {
            System.err.println("Invocation target: " + e);
        }
        return "";
    }

    /**
     *
     * @author Toast kid
     *
     */
    private class JavaSourceFromString extends SimpleJavaFileObject {

        final String code;

        JavaSourceFromString(final String name, final String code) {
            super(URI.create("string:///" + name.replace('.', '/') +
                    Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
            return code;
        }
    }
}