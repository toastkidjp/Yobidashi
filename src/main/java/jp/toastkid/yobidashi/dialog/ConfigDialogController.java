package jp.toastkid.yobidashi.dialog;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import jp.toastkid.yobidashi.Config;

/**
 * 設定ダイアログのコントローラ.
 * @author Toast kid
 * @see <a href="http://d.hatena.ne.jp/aoe-tk/20130526/1369577773">
 * JavaFX2.2でダイアログを作る方法</a>
 */
public final class ConfigDialogController {

    /** author(tool user name). */
    @FXML
    public TextField author;

    /** tool's title. */
    @FXML
    public TextField wikiTitle;

    /** tool's icon. */
    @FXML
    public TextField wikiIcon;

    /** path/to/article_dir. */
    @FXML
    public TextField articleFolder;

    /** path/to/picture_dir. */
    @FXML
    public TextField pictureFolder;

    /** path/to/music_file_dir. */
    @FXML
    public TextField musicFolder;

    /** home article name. */
    @FXML
    public TextField home;

    /** using view template. */
    @FXML
    public ComboBox<String> viewTemplate;

    /**
     * ファイルから設定値を読み込んでセットしておく.
     */
    public final void loadConfig() {
        author.setText(Config.get("author"));
        wikiTitle.setText(Config.get("wikiTitle"));
        wikiIcon.setText(Config.get("wikiIcon"));
        articleFolder.setText(Config.get( "articleDir"));
        pictureFolder.setText(Config.get("imageDir"));
        musicFolder.setText(Config.get("musicDir"));
        home.setText(Config.get("home"));
        viewTemplate.getSelectionModel().select(Config.get("viewTemplate"));
    }

    /**
     * 設定をファイルに保存する。
     * <HR>
     * (130707) 作成<BR>
     */
    @FXML
    public final void storeConfig() {
        final Map<String,String> newValues = new HashMap<String,String>(10);
        if (StringUtils.isNotEmpty(author.getText())) {
            newValues.put("author", author.getText());
        }
        if (StringUtils.isNotEmpty(wikiTitle.getText())) {
            newValues.put("wikiTitle", wikiTitle.getText());
        }
        if (StringUtils.isNotEmpty(wikiIcon.getText())) {
            newValues.put("wikiIcon", wikiIcon.getText());
        }
        if (StringUtils.isNotEmpty(articleFolder.getText())) {
            newValues.put("articleDir", articleFolder.getText());
        }
        if (StringUtils.isNotEmpty(pictureFolder.getText())) {
            newValues.put("imageDir", pictureFolder.getText());
        }
        if (StringUtils.isNotEmpty(musicFolder.getText())) {
            newValues.put("musicDir", musicFolder.getText());
        }
        if (StringUtils.isNotEmpty(home.getText())) {
            newValues.put("home", home.getText());
        }
        newValues.put("viewTemplate", viewTemplate.getSelectionModel().getSelectedItem().toString());
        Config.store(newValues);
        home.getScene().getWindow().hide();
    }

    /**
     * ダイアログをキャンセルする.
     * @param event
     * @return 選択された状態
     */
    @FXML
    public void cancel(final ActionEvent event) {
        home.getScene().getWindow().hide();
    }
}
