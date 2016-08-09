package jp.toastkid.gui.jfx.script;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jp.toastkid.gui.jfx.wiki.ArticleGenerator;
import jp.toastkid.gui.jfx.wiki.Main;
import jp.toastkid.libs.utils.CollectionUtil;
import jp.toastkid.libs.utils.FileUtil;

/**
 * Script area's controller.
 * @author Toast kid
 *
 */
public class Controller {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /** Script file name. */
    @FXML
    public TextField scriptName;
    /** script input. */
    @FXML
    public TextArea scripterInput;
    /** script output. */
    @FXML
    public TextArea scripterOutput;
    /** names of script language. */
    @FXML
    public ComboBox<String> scriptLanguage;

    /** in script area. */
    @FXML
    public VBox scripterArea;

    /** parent's stage. */
    private Stage stage;

    /**
     *
     * @param stage
     */
    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * hide article search box area.
     */
    @FXML
    public void openScripter() {
        scripterArea.setManaged(true);
        scripterArea.visibleProperty().setValue(true);
        scripterInput.requestFocus();
    }

    /**
     * hide article search box area.
     * @see <a href="http://stackoverflow.com/questions/19923443/
     *javafx-fill-empty-space-when-component-is-not-visible">
     * JavaFX Fill empty space when component is not visible?</a>
     */
    @FXML
    public void hideScripter() {
        scripterArea.visibleProperty().setValue(false);
        scripterArea.setManaged(false);
    }

    /**
     * open script file.
     * @see <a href="http://javafx-trick.appspot.com/article/110010/80074/70110.html">
     * ファイル選択ダイアログ(showOpenDialog)の作成方法</a>
     */
    @FXML
    public void openScript() {
        final FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(FileUtil.uriToPath(ArticleGenerator.findInstallDir())));
        final File result = fc.showOpenDialog(stage.getScene().getWindow());
        loadScript(result);
    }

    /**
     * Script をファイルに保存する.
     */
    @FXML
    public void saveScript() {
        try {
            if (new File(scriptName.getText()) != null) {
                final File file = new File("script"
                        + Language.extension(scriptLanguage.getSelectionModel().getSelectedItem()));
                Files.createFile(file.toPath());
                scriptName.setText(file.getAbsolutePath());
            }
            final File file = new File(scriptName.getText());
            Files.write(
                    file.toPath(),
                    scripterInput.getText().getBytes("UTF-8"),
                    StandardOpenOption.WRITE
                    );
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
        }
    }

    /**
     * Script をファイルからreloadする.
     */
    @FXML
    public void reloadScript() {
        loadScript(new File(scriptName.getText()));
    }

    /**
     * script をファイルから読み込む.
     * @param file
     */
    private void loadScript(final File file) {
        if (file == null || !file.exists()) {
            return;
        }
        try {
            scriptName.setText(file.getCanonicalPath());
            scripterInput.setText(
                    CollectionUtil.implode(Files.readAllLines(file.toPath()),System.lineSeparator())
                    );
        } catch (final IOException e) {
            LOGGER.error("Caught error.", e);
        }
    }

    /**
     * open script file by editor.
     */
    @FXML
    public void editScript() {
        final File openTarget = new File(scriptName.getText());
        if (openTarget.canRead()) {
            jp.toastkid.gui.jfx.wiki.Controller.openFileByEditor(openTarget);
        }
    }

    /**
     * run script use input plain text.
     */
    @FXML
    public void runScript() {
        scripterOutput
            .setText("Work in Progress......Could you please wait a moment?");
        final Language lang = Language.valueOf(
                scriptLanguage.getSelectionModel().getSelectedItem().toUpperCase());
        final ScriptRunner runner = ScriptRunner.find(lang);
        final String result = runner.run(scripterInput.getText()).get();
        if (StringUtils.isEmpty(result)) {
            return;
        }
        scripterOutput.setText(result);
    }

}
