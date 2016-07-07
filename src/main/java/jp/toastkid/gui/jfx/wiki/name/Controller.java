package jp.toastkid.gui.jfx.wiki.name;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

/**
 * NameMaker's controller.
 * @author Toast kid
 *
 */
public class Controller implements Initializable {

    /** name nationalities. */
    @FXML
    public ComboBox<String> nationalities;
    /** name generating values. */
    @FXML
    public ComboBox<Integer> nameNums;
    /** contains generated names. */
    @FXML
    public TextArea nameOutput;

    /** name generator. */
    private NameGenerator nameGenerator;

    @Override
    public void initialize(final URL arg0, final ResourceBundle arg1) {

        final ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            final long start = System.currentTimeMillis();
            nameGenerator = new NameGenerator();
            nationalities.getItems().addAll(nameGenerator.getNationalities());
            nationalities.getSelectionModel().select(0);

            //nameGenerator.checkXml();

            System.out.println(Thread.currentThread().getName() + " Ended initialize NameMaker."
                    + (System.currentTimeMillis() - start) + "ms");
        });
        executor.shutdown();
    }

    /**
     * 名前を生成する.
     */
    @FXML
    protected void generateNames() {
        nameOutput.setText(
                nameGenerator.generate(nationalities.getValue(), nameNums.getValue().intValue()));
    }

}
