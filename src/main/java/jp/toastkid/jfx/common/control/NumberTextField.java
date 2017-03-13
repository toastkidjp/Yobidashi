package jp.toastkid.jfx.common.control;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXTextField;

/**
 * 数値の入力だけを受け付ける TextField.
 * @author Toast kid
 * @see <a href="http://stackoverflow.com/questions/7555564/
 *what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx">
 * What is the recommended way to make a numeric TextField in JavaFX?</a>
 */
public class NumberTextField extends JFXTextField {

    @Override
    public void replaceText(final int start, final int end, final String text) {
        if (validate(text)){
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(final String text) {
        if (validate(text)) {
            super.replaceSelection(text);
        }
    }

    private boolean validate(final String text) {
        return text.matches("[0-9]*");
    }

    public int intValue() {
        final String text = getText();
        if (StringUtils.isBlank(text)) {
            return 0;
        }
        return Integer.parseInt(text);
    }

    public double doubleValue() {
        final String text = getText();
        if (StringUtils.isBlank(text)) {
            return 0;
        }
        return Double.parseDouble(text);
    }
}