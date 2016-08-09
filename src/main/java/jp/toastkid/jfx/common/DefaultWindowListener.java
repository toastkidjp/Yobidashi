package jp.toastkid.jfx.common;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

   /**
    * ウィンドウが閉じられた時の動作を定義したクラス、単独で立ち上げる JFrame はこれをセットすればよい。
    */
    public final class DefaultWindowListener extends WindowAdapter{
        public final void windowClosing(final WindowEvent e) {
            System.exit(0);
      }
    }
