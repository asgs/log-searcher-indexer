/**
 * 
 */
$(document).ready(
	function() {
		var style = $("#searchPanel").attr("style");
		$("#searchPanel").attr("style",style + ";width:" + ($("#container").width() * 1 / 2 - 1) + "px");
		style = $("#adminPanel").attr("style");
		$("#adminPanel").attr("style",style + ";width:" + ($("#container").width() * 1 / 2 - 1) + "px");
		//$("#mainNav").attr("style","width:" + (window.innerWidth * 1 / 8) + "px");
		$("hr").attr("style","color:cyan;width:" + (window.innerWidth * 1 / 2) + "px");
	}
);
