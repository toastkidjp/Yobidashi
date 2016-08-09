package jp.toastkid.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * run command line script. You need sh.
 * @author Toast kid
 *
 */
public class ShellRunner extends ScriptRunner {

    @Override
    public Optional<String> run(final String script) {
        final ProcessBuilder b = new ProcessBuilder("sh", "-c", script);

        if (StringUtils.isEmpty(script)) {
            return Optional.empty();
        }
        final StringBuilder result = new StringBuilder()
                .append("$ ").append(script).append(LINE_SEPARATOR);

        try (final StringWriter writer = new StringWriter();) {
            final Process start = b.start();
            new BufferedReader(new InputStreamReader(start.getInputStream())).lines().forEach(line -> {
                result.append(line).append(LINE_SEPARATOR);
            });
            writer.close();
        } catch (final CompilationFailedException | IOException e) {
            e.printStackTrace();
            result.append("Occurred Exception.").append(LINE_SEPARATOR)
                  .append(e.getMessage());
        }
        return Optional.of(result.toString());
    }

}
