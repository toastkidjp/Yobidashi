package jp.toastkid.jfx.common.control;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.jfoenix.controls.JFXTextField;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;

/**
 * This class is a TextField which implements an "autocomplete" functionality,
 * based on a supplied list of entries.
 * <pre>
 * new AutoCompleteTextField().getEntries().addAll(Arrays.asList("AA", "AB", "AC","BCA"));
 * </pre>
 * @author Caleb Brinkman
 * @author Toast kid
 * @see <a href="https://gist.github.com/floralvikings/10290131">Gist</a>
 */
public class AutoCompleteTextField extends JFXTextField {

    /** The existing autocomplete entries. */
    private final SortedSet<String> entries;

    /** The popup used to select an entry. */
    private final ContextMenu entriesPopup;

    /**
     * Construct a new AutoCompleteTextField.
     */
    public AutoCompleteTextField() {
        this("");
    }

    /**
     * Construct a new AutoCompleteTextField.
     */
    public AutoCompleteTextField(final String defaultText) {
        super();
        entries      = new TreeSet<>();
        entriesPopup = new ContextMenu();

        if (StringUtils.isNotEmpty(defaultText)) {
            this.setText(defaultText);
        }

        textProperty().addListener((observableValue, s, s2) -> {
            if (getText().length() == 0) {
                entriesPopup.hide();
                return;
            }

            final LinkedList<String> searchResult = new LinkedList<>();
            searchResult.addAll(entries.subSet(getText(), getText() + Character.MAX_VALUE));
            if (entries.size() > 0) {
                populatePopup(searchResult);
                if (!entriesPopup.isShowing()) {
                    entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                }
            } else {
                entriesPopup.hide();
            }
        });
        focusedProperty().addListener((value, bool1, bool2) -> {entriesPopup.hide();});
    }

    /**
     * Get the existing set of autocomplete entries.
     *
     * @return The existing autocomplete entries.
     */
    public SortedSet<String> getEntries() {
        return entries;
    }

    /**
     * Populate the entry set with the given search results. Display is limited
     * to 10 entries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private void populatePopup(List<String> searchResult) {
        final List<CustomMenuItem> menuItems = new LinkedList<>();
        // If you'd like more entries, modify this line.
        final int maxEntries = 10;
        final int count = Math.min(searchResult.size(), maxEntries);
        searchResult.subList(0, count).stream().forEach(result -> {
            final Label          label = new Label(result);
            final CustomMenuItem item  = new CustomMenuItem(label, true);
            item.setOnAction(eve -> {
                setText(result);
                entriesPopup.hide();
            });
            menuItems.add(item);
        });
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }
}
