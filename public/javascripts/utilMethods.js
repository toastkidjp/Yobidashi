/**
 * 便利メソッドを収録
 */

/** 
 * 数値を3桁カンマ区切りの文字列にして返す。
 * <HR>
 * (130511) 追加<BR>
 * @see <a href="http://webdev.seesaa.net/article/22769178.html">JavaScriptで3桁区切りのカンマ</a>
 */
function addFigure(str) {
	var num = new String(str).replace(/,/g, "");
	while(num != (num = num.replace(/^(-?\d+)(\d{3})/, "$1,$2")));
	return num;
}
