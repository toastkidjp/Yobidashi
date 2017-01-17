package jp.toastkid.yobidashi.message;

/**
 * It's a mere marker.
 *
 * @author Toast kid
 *
 */
public class ToolsDrawerMessage implements Message {

    /**
     * Call only internal.
     */
    private ToolsDrawerMessage() {
        // NOP.
    }

    /**
     * Make empty message.
     * @return ToolsDrawerMessage
     */
    public static Message make() {
        return new ToolsDrawerMessage();
    }

}
