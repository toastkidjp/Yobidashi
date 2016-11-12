package jp.toastkid.wiki.lib;

/**
 * GitHub widget's factory.
 *
 * @author Toast kid
 */
public class GitHubWidgetFactory {

    /** Host of GitHub. */
    private static final String GITHUB = "http://github.com/";

    /** Format. */
    private static final String FORMAT = "<a href='" + GITHUB + "%s'>%s</a><br/>";

    /**
     * Make HTML widget.
     * @param orgRepoPair
     * @return HTML widget
     */
    public static String make(final String orgRepoPair) {

        if (!orgRepoPair.contains("/")) {
            return orgRepoPair;
        }

        final String[] pair = orgRepoPair.split("/");
        final StringBuilder widget = new StringBuilder();

        widget.append("<table class=\"table table-hover table-stripe\">");
        widget.append("<tr><td colspan='6'>");
        widget.append("<a href='").append(GITHUB).append(pair[0]).append("'>")
            .append(pair[0]).append("</a>");
        widget.append("/");
        widget.append("<a href='").append(GITHUB).append(orgRepoPair).append("'>")
            .append(pair[1]).append("</a><br/>");
        widget.append("</td></tr>");
        widget.append("<tr>");
        widget.append(makeTd(makeLink(orgRepoPair + ".git", "Clone")));
        widget.append(makeTd(makeLink(orgRepoPair + "/branches", "Branches")));
        widget.append(makeTd(makeLink(orgRepoPair + "/releases", "Releases")));
        widget.append(makeTd(makeLink(orgRepoPair + "/tags", "Tags")));
        widget.append(makeTd(makeLink(orgRepoPair + "/graphs/contributors", "Contributors")));
        widget.append(makeTd(makeLink(orgRepoPair + "/archive/master.zip", "Download ZIP")));
        widget.append("</tr>");
        widget.append("</table>");
        return widget.toString();
    }

    /**
     * Make string around td tag.
     * @param str
     * @return
     */
    private static final String makeTd(final String str) {
        return "<td>" + str + "</td>";
    }

    /**
     * Make link.
     * @param suffix
     * @param text
     * @return
     */
    private static String makeLink(final String suffix, final String text) {
        return String.format(FORMAT, suffix, text);
    }
}
