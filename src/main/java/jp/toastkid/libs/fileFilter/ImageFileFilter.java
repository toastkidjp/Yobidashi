package jp.toastkid.libs.fileFilter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;
/**
 * 画像ファイルを拡張子から判定して選別する。
 * <HR>
 * (120519) 作成開始<BR>
 * @see <a href="http://qingdao.jugem.jp/?eid=103">
 * 【Java】Fileクラスのlist(FilenameFilter filter)メソッド</a>
 * @author Toast kid
 *
 */
public final class ImageFileFilter implements FilenameFilter {
    /** フィルタ対象文字列を検出する正規表現 */
    private static final Pattern pat = Pattern.compile(
            "\\.(jpg|jpeg|bmp|png|svg|gif)$",
            Pattern.DOTALL
            );

    /** このフラグが立っている時はフォルダも可とする。 */
    private boolean isArrawDir = false;
    /**
    * フォルダを許可するか否かを指定して初期化
    * @param pIsArrawDir true ならフォルダも許可する。
    */
    public ImageFileFilter(final boolean pIsArrawDir){
        this.isArrawDir = pIsArrawDir;
    }
    /**
    * FilenameFilterインタフェースで宣言されているacceptメソッドを記述.
    *
    * ファイル名の末尾が"FILTER_KEYWORD" の指定通りならば true を返却(リストに追加)
    * そうでなければ false を返却(リストに追加しない)
    *
    * @param dir File型,FilenameFilterTestクラスにあるOBJECT_DIRと同一の値
    * @param name String型,OBJECT_DIRのディレクトリ内に存在している、ある1ファイルの名前
    * @return boolean型
    */
    public boolean accept(
            final File dir,
            final String name
            ){
        if(isArrawDir && new File(dir + "/" + name).isDirectory()){
            return true;
        } else{
            return pat.matcher(name.toLowerCase()).find();
        }
    }

}
