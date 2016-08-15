package jp.toastkid.wiki.models;

import java.io.Serializable;

/**
 * View templates.
 * @author Toast kid
 *
 */
public enum ViewTemplate implements Serializable {
    SECOND(  Defines.TEMPLATE_DIR + "/main_2nd.html"),
    MATERIAL(Defines.TEMPLATE_DIR + "/main_material.html");

    private String path;

    /**
     * call from only this class.
     * @param path
     */
    private ViewTemplate(final String path) {
        this.path = path;
    }

    /**
     * 文字列から適合する enum を返す.
     * @param str
     * @return
     */
    public static ViewTemplate parse(final String str) {
        if (str == null) {
            return MATERIAL;
        }
        switch (str.toLowerCase()) {
            case "second":
            case "2nd":
                return SECOND;
            case "material":
            case "materialize":
            default:
                return MATERIAL;
        }
    }
    /**
     * テンプレートに合わせた テンプレートファイル名を返す.
     * @param tmpl ViewTemplate
     * @return テンプレートに合わせたテンプレートファイル名
     */
    public String getPath() {
        return this.path;
    }
}