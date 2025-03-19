//Download Excel poitem
$('#downloadTemplate').click(function(e) {
	e.preventDefault();
	var prId = $.trim($('#prId').val());
	window.location.href = getContextPath() + "/buyer/prItemTemplate/" + prId;
});

//Approval pop up
$(document).delegate('.editApprovalPopupButton', 'click', function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	$("#editApprovalPopup").dialog({
		modal : true,
		minWidth : 300,
		width : '45%',
		maxWidth : 600,
		minHeight : 200,
		dialogClass : "",
		show : "fadeIn",
		draggable : false,
		dialogClass : "dialogBlockLoaded"
	});
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
});

