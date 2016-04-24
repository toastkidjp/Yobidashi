package jp.toastkid.libs.wiki;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Lists;

/**
 * Wiki text convert to Markdown.
 * @author Toast kid
 *
 */
public class Wiki2Markdown {

    private static final Pattern LINK_PATTERN
        = Pattern.compile("\\[(.+?)\\|(.+?)\\]", Pattern.DOTALL);

    private static final Pattern WIKIPEDIA_PATTERN
        = Pattern.compile("\\{[W|w]ikipedia:(.+?)\\}", Pattern.DOTALL);

    private static final Pattern TWITTER_PATTERN
        = Pattern.compile("\\{[T|t]witter:(.+?)\\}", Pattern.DOTALL);

    public static List<String> convert(final Iterable<String> strs) {
        final List<String> converted = Lists.mutable.empty();
        strs.forEach(str -> {
            converted.add(convertLine(str));
        });
        return converted;
    }

    private static String convertLine(final String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }

        String base = str;
        if (base.contains("[") && base.contains("|")) {
            final Matcher matcher = LINK_PATTERN.matcher(base);
            while (matcher.find()) {
                final String alt = matcher.group(1);
                final String url = matcher.group(2);
                base = base.replaceFirst(LINK_PATTERN.pattern(), String.format("[%s](%s)", alt, url));
            }
        }

        if (base.contains("ikipedia")) {
            final Matcher matcher = WIKIPEDIA_PATTERN.matcher(base);
            while (matcher.find()) {
                base = base.replaceFirst(WIKIPEDIA_PATTERN.pattern(), "https://ja.wikipedia.org/wiki/" + matcher.group(1));
            }
        }

        if (base.contains("witter")) {
            final Matcher matcher = TWITTER_PATTERN.matcher(base);
            while (matcher.find()) {
                base = base.replaceFirst(TWITTER_PATTERN.pattern(), "https://www.twitter.com/" + matcher.group(1));
            }
        }

        if (base.startsWith("*")) {
            return base.replaceFirst("\\*.* ", "#");
        }

        if (base.startsWith("#")) {
            return base.replaceFirst("#.* ", "1.");
        }

        return base;
    }
}
