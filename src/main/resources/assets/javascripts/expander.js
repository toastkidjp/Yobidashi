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

/**
 *
 * <a href ="http://kyasper.com/jquery-tips/">
 * jQuery とっても簡単、ページ内リンクでスムーズスクロール</a>
 */
$(function(){
    $('a[href^=#]').click(function() {
       var href= $(this).attr("href");
       var target = $(href == "#" || href == "" ? 'html' : href);
       var position = target.offset().top;
       $('body,html').animate({scrollTop:position}, 300, 'swing');
       return false;
    });
});