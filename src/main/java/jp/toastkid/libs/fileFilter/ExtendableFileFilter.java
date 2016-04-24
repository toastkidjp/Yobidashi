package jp.toastkid.libs.fileFilter;

import java.io.File;
import java.io.FilenameFilter;
/**
 *
 * @see <a href="http://qingdao.jugem.jp/?eid=103">
 * 【Java】Fileクラスのlist(FilenameFilter filter)メソッド</a>
 * @author Toast kid
 *
 */
public final class ExtendableFileFilter implements FilenameFilter {
    /** フィルタ対象文字列 */
    private String FILTER_KEYWORD = "";
    /** フォルダを一覧に加えるか否か */
    private boolean isArrawDir = true;
    public boolean isArrawDir() {
        return isArrawDir;
    }
    public void setArrawDir(final boolean isArrawDir) {
        this.isArrawDir = isArrawDir;
    }
    public String getFILTER_KEYWORD() {
        return FILTER_KEYWORD;
    }
    public void setFILTER_KEYWORD(final String fILTER_KEYWORD) {
        FILTER_KEYWORD = fILTER_KEYWORD;
    }

    @Override
    public boolean accept(final File dir, final String name){
        // Fileクラスのオブジェクト生成
        final File file = new File(name);
        // ディレクトリならばfalseを返却(リストに追加しない)
        if(isArrawDir && file.isDirectory()){
            return false;
        }
        // ファイル名の末尾が FILTER_KEYWORD で終わっているならば true を返却(リストに追加)
        // そうでなければ false を返却(リストに追加しない)
        return (name.endsWith(FILTER_KEYWORD));
    }


}
