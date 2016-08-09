package jp.toastkid.script;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javax.script.ScriptContext;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;

/**
 * Groovy's runner.
 * @author Toast kid
 *
 */
public class GroovyRunner extends ScriptRunner {

    /**
     * init ScriptEngine.
     */
    public GroovyRunner() {
        engine = new GroovyScriptEngineFactory().getScriptEngine();
    }

    @Override
    public Optional<String> run(final String script) {
        if (StringUtils.isEmpty(script)) {
            return Optional.empty();
        }
        if (engine == null) {
            System.out.println("groovy null");
        }

        final StringBuilder result = new StringBuilder();

        try (final StringWriter writer = new StringWriter();) {
            if (engine != null) {
                final ScriptContext context = engine.getContext();
                context.setWriter(new PrintWriter(writer));
                context.setErrorWriter(new PrintWriter(writer));
            }
            final java.lang.Object run = engine.eval(script);
            result.append(writer.toString()).append(LINE_SEPARATOR);
            if (run != null) {
                result.append("return = ").append(run.toString());
            }
            writer.close();
        } catch (final CompilationFailedException | IOException | javax.script.ScriptException e) {
            e.printStackTrace();
            result.append("Occurred Exception.").append(LINE_SEPARATOR)
                .append(e.getMessage());
        }
        return Optional.of(result.toString());
    }
}
