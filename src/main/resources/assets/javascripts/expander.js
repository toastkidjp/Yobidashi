/**
 * 折り畳み機能
 * (130803) 作成<BR>
 */
var isOpen = false;
function open(expanderID){
	if (!isOpen) {
		document.getElementById(expanderID).style.display = "block";
		isOpen = true;
	} else {
		close(expanderID);
	}
}
function close(expanderID){
	document.getElementById(expanderID).style.display = "none";
	isOpen = false;
}
