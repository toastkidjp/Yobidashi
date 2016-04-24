package jp.toastkid.libs.wiki;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * YOLP static map url builder.
 * @author Toast kid
 * @see <a href="http://developer.yahoo.co.jp/webapi/map/openlocalplatform/v1/static.html">
 * Yahoo!スタティックマップAPI</a>
 */
public class YolpMapBuilder {

    /** API. */
    private static final String API = "http://map.olp.yahooapis.jp/OpenLocalPlatform/V1/static";

    /** map width. */
    private int width;

    /** map height. */
    private int height;

    /** Y!ID. */
    private String appId;

    /** pins. */
    public final List<String> pins;

    /**
     * build instance use this.
     * @param b
     */
    public YolpMapBuilder() {
        pins = new ArrayList<String>();
    }

    @Override
    public String toString() {
        final StringBuilder url = new StringBuilder();
        url.append("<img width=\"").append(this.width).append("\" height=\"").append(this.height)
            .append("\" ")
            .append("src=\"").append(API).append("?appid=").append(this.appId)
            .append("&width=").append(this.width).append("&height=").append(this.height);
        IntStream.range(0, pins.size())
            .forEach(i -> {url.append("&pin").append(i + 1).append("=").append(pins.get(i));});
        url.append("\" />");
        return url.toString();
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public void setAppId(final String appId) {
        this.appId = appId;
    }
}
