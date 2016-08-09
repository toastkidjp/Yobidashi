package jp.toastkid.wiki.control;

import javafx.scene.control.ListCell;
import jp.toastkid.wiki.models.Article;

/**
 * 記事一覧リストのセル.
 * @author Toast kid
 * @see <a href="http://aoe-tk.hatenablog.com/entry/20131206/1386345344">
 * ListViewやTableViewのセルをカスタマイズする方法 (JavaFX Advent Calendar2013 7日目)</a>
 */
public class ArticleListCell extends ListCell<Article> {
    /** style class. */
    private static final String LIST_CELL_STYLE_CLASS = "list-cell";

    /** style class. */
    private static final String STYLE_CLASS = "article-cell";

    private boolean bound = false;

    /**
     * init object.
     */
    public ArticleListCell() {
        initComponents();
        this.getStyleClass().setAll(STYLE_CLASS, LIST_CELL_STYLE_CLASS);
    }

    /**
     * init components.
     */
    private void initComponents() {
        /*
        this.title = new Label();
        VBox.setVgrow(title, Priority.NEVER);
        this.title.getStyleClass().setAll(LIST_CELL_STYLE_CLASS, TITLE_STYLE_CLASS);
        title.setFont(new Font("System Bold", 14.0));

        this.description = new Label();
        VBox.setVgrow(description, Priority.ALWAYS);
        this.description.getStyleClass().setAll(LIST_CELL_STYLE_CLASS, DESCRIPTION_STYLE_CLASS);

        this.cellContainer = new VBox(5);
        this.cellContainer.getChildren().addAll(title, description);
        */
    }

    @Override
    protected void updateItem(final Article article, final boolean empty) {
        super.updateItem(article, empty);
        if (!bound) {
            //title.wrappingWidthProperty().bind(getListView().widthProperty().subtract(25));
            //description.wrappingWidthProperty().bind(getListView().widthProperty().subtract(20));
            bound = true;
        }

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(new StringBuilder().append(article.extention().substring(1) ).append(" | ")
                        .append(article.title).append("\n最終更新：")
                        .append(article.lastModifiedText()).toString());
        }
    }

}