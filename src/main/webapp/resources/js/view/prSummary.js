//Download Excel pritem
$('#downloadTemplate').click(function(e) {
	e.preventDefault();
	var prId = $.trim($('#prId').val());
	window.location.href = getContextPath() + "/buyer/prItemTemplate/" + prId;
});

$('#approvedButton').click(function(e) {
	e.preventDefault();
	$(this).addClass('disabled');
	console.log("approved");
	$('#approvedRejectForm').attr('action', getContextPath() + "/buyer/prApproved");
	$('#approvedRejectForm').submit();

});
$('#rejectedButton').click(function(e) {
	e.preventDefault();
	$(this).addClass('disabled');
	console.log("rejectedButton");
	$('#approvedRejectForm').attr('action', getContextPath() + "/buyer/prRejected");
	$('#approvedRejectForm').submit();

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
//	$("#idReminderSettings").hide();
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
//	$('.ui-dialog-title').text('Add Contact Person');
//	$('#addEditContactPopup').find('a.addContactPerson').text('Add Contact Person');
});