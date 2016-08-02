package jp.toastkid.gui.jfx.wiki.music;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.factory.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import jp.toastkid.gui.jfx.wiki.Functions;
import jp.toastkid.gui.jfx.wiki.models.Config;

/**
 * Music Player's controller
 * @author Toast kid
 *
 */
public class Controller implements Initializable {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    /** (Mp3) タブ. */
    @FXML
    public TabPane musicTabPane;

    /** (Mp3) 現在選択中のファイル名を表示する. */
    @FXML
    public TextField currentMusicFileName;

    /** (Mp3) ファイル再生インスタンス. */
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(final URL arg0, final ResourceBundle arg1) {
        prepareMp3Player();
    }

    /**
     * MP3プレイヤーの用意をする.
     */
    private final void prepareMp3Player() {
        final ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            final long start = System.currentTimeMillis();
            musicTabPane.getTabs().clear();
            final String musicDir = Config.get("musicDir");
            if (StringUtils.isBlank(musicDir)) {
                return;
            }
            final String[] dirs = musicDir.split(",");
            for (final String dirName : dirs) {
                final File dir = new File(dirName.trim());
                openMp3Tab(dir);
            }

            LOGGER.info(Thread.currentThread().getName() + " Ended initialize MP3 Player."
                    + (System.currentTimeMillis() - start ) + "ms");
        });
        executor.shutdown();
    }

    /**
     * 音楽を再生する.
     */
    @FXML
    private void startMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        final String currentMusicDir
                = musicTabPane.getSelectionModel().getSelectedItem().getText();
        final String fileName = currentMusicFileName.getText().toString();
        if (!StringUtils.isNotBlank(currentMusicDir)
                || !StringUtils.isNotBlank(fileName)) {
            return;
        }
        final Media media = new Media(
                Functions.format4Mp3Player(
                        new File(
                                currentMusicDir.replaceAll("　", "%E3%80%80"),
                                fileName.replaceAll("　", "%E3%80%80")
                        ).getAbsolutePath()
                    )
                );
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(Integer.MAX_VALUE);
            mediaPlayer.play();
        } else if (!mediaPlayer.isAutoPlay()) {
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(Integer.MAX_VALUE);
            mediaPlayer.play();
        }
    }

    /**
     * 再生を止める.
     */
    @FXML
    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * 新しいタブを開く.
     * @param dir 開く音楽ファイルフォルダ
     */
    public void openMp3Tab(final File dir) {
        final Tab tab = new Tab();
        final ListView<String> lv = new ListView<String>();
        lv.setMaxHeight(230.0);
        tab.setContent(lv);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        loadMp3List(lv, dir);
        final MultipleSelectionModel<String> articleSM = lv.getSelectionModel();
        articleSM.setSelectionMode(SelectionMode.SINGLE);
        articleSM.selectedItemProperty().addListener(
                (value, o1, o2) -> {setFileName(value);}
                );
        musicTabPane.getTabs().add(tab);
        musicTabPane.getSelectionModel().select(tab);
        tab.setText(dir.getAbsolutePath());
    }

    /**
     * MP3ファイル名をセットする.
     * @param value ObservableValue
     */
    private void setFileName(@SuppressWarnings("rawtypes") final ObservableValue value) {
        currentMusicFileName.setText(value.getValue().toString());
    }

    /**
     * MP3ファイル一覧をロードする.
     * @param lv ListView
     * @param dir フォルダ
     */
    @FXML
    private void loadMp3List(final ListView<String> lv, final File dir) {
        final ObservableList<String> items = lv.getItems();
        items.removeAll();
        Lists.immutable.with(dir.listFiles()).each(f ->{
            if (Functions.isValidMusicFile(f.getName())) {
                items.add(f.getName());
            }
        });
        lv.requestLayout();
    }

    /**
     * List reloading.
     */
    public void reload() {
        prepareMp3Player();
    }

}
