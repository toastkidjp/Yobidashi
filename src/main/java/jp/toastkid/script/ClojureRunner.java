package jp.toastkid.script;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

import javax.script.ScriptContext;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;

import clojure.contrib.jsr223.ClojureScriptEngineFactory;

/**
 * Clojure runner.
 *
 * @author Toast kid
 *
 */
public class ClojureRunner extends ScriptRunner {

    /**
     * init ScriptEngine.
     */
    public ClojureRunner() {
        engine = new ClojureScriptEngineFactory().getScriptEngine();
    }

    @Override
    public Optional<String> run(final String script) {

        if (StringUtils.isEmpty(script)) {
            return Optional.empty();
        }
        final StringBuilder result = new StringBuilder();

        try (final StringWriter writer = new StringWriter();) {
            final ScriptContext context = engine.getContext();
            context.setWriter(writer);
            context.setErrorWriter(writer);

            final java.lang.Object run = engine.eval(script);
            result.append(writer.toString()).append(LINE_SEPARATOR);
            if (run != null) {
                result.append("return = ").append(run.toString());
            }
            writer.close();
        } catch (final CompilationFailedException | IOException | ScriptException e) {
            e.printStackTrace();
            result.append("Occurred Exception.").append(LINE_SEPARATOR).append(e.getMessage());
        }
        return Optional.of(result.toString());
    }

    public void put(final String key, final Object value) {
        engine.put(key, value);
    }

    public Object get(final String key) {
        return engine.get(key);
    }
}
