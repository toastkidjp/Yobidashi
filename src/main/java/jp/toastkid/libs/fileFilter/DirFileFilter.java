package jp.toastkid.libs.fileFilter;
import java.io.File;
import java.io.FilenameFilter;

/**
 * フォルダ専用のファイルフィルタ、Fileクラスのlistメソッドで使用
 * <HR>
 * (120610) 正常に動作しなかったので修正<BR>
 * @see <a href="http://qingdao.jugem.jp/?eid=103">
 * 【Java】Fileクラスのlist(FilenameFilter filter)メソッド</a>
 * @author Toast kid
 *
 */
public final class DirFileFilter implements FilenameFilter {

    @Override
    public boolean accept(final File dir, final String name){
        // Fileクラスのオブジェクト生成
        final File file = new File(dir.getAbsolutePath() + "/" + name);
        // フォルダならば false を返却(リストに追加しない)
        if(file.isDirectory()){
            return true;
        }
        // falseを返却(リストに追加しない)
        return false;
    }
}