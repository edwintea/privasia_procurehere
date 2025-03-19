/* Chosen select */

$(function() {
	"use strict";
	$('.chosen-select').not('.disablesearch').chosen({search_contains:true}).change(function() {
		if($(this).attr('multiple')) {
			$(this).validate();
		}
		$(this).blur();
	});
	$('.chosen-select.disablesearch').chosen({
		disable_search : true
	}).change(function() {
		$(this).blur();
	});
	$(".chosen-search").append('<i class="glyph-icon icon-search"></i>');
	$(".chosen-single div").html('<i class="glyph-icon icon-caret-down"></i>');
	$('.closeDialog').click(function(e){
		e.preventDefault();
		$('.ui-dialog-titlebar-close').click();
	});
});
