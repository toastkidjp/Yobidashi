package jp.toastkid.libs.fileFilter;

import java.io.File;
import java.io.FilenameFilter;
/**
 *
 * @see <a href="http://qingdao.jugem.jp/?eid=103">【Java】Fileクラスのlist(FilenameFilter filter)メソッド</a>
 * @author Toast kid
 *
 */
public final class TextFileFilter implements FilenameFilter {

    /** フィルタ対象文字列 */
    private static final String FILTER_KEYWORD = ".txt";

    /** このフラグが立っている時はフォルダも可とする。 */
    private boolean isArrawDir = false;

    /**
     * フォルダを許可するか否かを指定して初期化
     * @param pIsArrawDir true ならフォルダも許可する。
     */
    public TextFileFilter(final boolean pIsArrawDir){
        this.isArrawDir = pIsArrawDir;
    }

    /**
     * FilenameFilterインタフェースで宣言されているacceptメソッドを記述
     *
     * ファイル名の末尾が"FILTER_KEYWORD" の指定通りならば true を返却(リストに追加)
     * そうでなければ false を返却(リストに追加しない)
     *
     * @param dir File型,FilenameFilterTestクラスにあるOBJECT_DIRと同一の値
     * @param name String型,OBJECT_DIRのディレクトリ内に存在している、ある1ファイルの名前
     * @return boolean型
     */
    @Override
    public boolean accept(final File dir, final String name){

        if (name == null) {
            return false;
        }

        if(isArrawDir && new File(dir + "/" + name).isDirectory()){
            return true;
        }
        return (name.endsWith(FILTER_KEYWORD));
    }

}
