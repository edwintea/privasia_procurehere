$(document).ready(function() {
	$(".s1_question_list li").draggable({
		helper : "clone",
		revert : "invalid"
	});

	// $(".s1_question_list li.selected");

	var onDrop = function(e, ui) {
		var drag_id = $(ui.draggable).attr("id");
		var targetElem = $(this).attr("id");
		deleteMe = true;
		$(this).find(".placeholder").remove();
		var drgid = "('" + drag_id + "')";
		var querstionAppnd = '<img style="float:right;" class="removeButton" onclick="showConfirmDelete' + drgid + '" src="' + getContextPath() + '/resources/images/black-xross.png">';
		$("</i><li id='Question" + drag_id + "'></li>").html(ui.draggable.html()).appendTo(this).append(querstionAppnd);
		$(ui.helper).remove(); // destroy clone
		$(ui.draggable).hide();
		datanoofbqcqsValid();
	};
	$(".s1_question_list li.selected").each(function() {
		var currentData = $(this);
		onDrop.call($(".s1_dragdrop_area ul"), {}, {
			draggable : currentData
		});
	});

	$(".s1_dragdrop_area ul").droppable({
		activeClass : "ui-state-default",
		hoverClass : "ui-state-hover",
		drop : function(event, ui) {
			var drag_id = $(ui.draggable).attr("id");
			var targetElem = $(this).attr("id");
			deleteMe = true;
			$(this).find(".placeholder").remove();
			/* $(this).html("Dropped! inside " + targetElem); */
			var drgid = "('" + drag_id + "')";
			var querstionAppnd = '<img style="float:right;" class="removeButton" onclick="showConfirmDelete' + drgid + '" src="' + getContextPath() + '/resources/images/black-xross.png">';
			$("</i><li id='Question" + drag_id + "'></li>").html(ui.draggable.html()).appendTo(this).append(querstionAppnd);
			$(ui.helper).remove(); // destroy clone
			$(ui.draggable).hide(); // remove from list
			datanoofbqcqsValid();
		}
	});

	$(document).delegate('#confirmRemovebqcq', 'click', function(e) {
		var id = $('#confirmRemovebqcqId').val();
		var bqName = $.trim($("#Question" + id).text());
		$("#Question" + id).remove();
		$("#" + id).show();
		if ($("#" + id).length == 1) {
			$("#" + id).show();
		} else {
			var html = '<li id="' + id + '" class="ui-draggable ui-draggable-handle">' + bqName + '<input name="bqids[]" value="' + id + '" type="hidden"></li>';
			$('#question_list ul').append(html);
		}
		$('#myModalEnvelop').modal('hide');
		datanoofbqcqsValid();
	});

	// $(document).delegate('.deleteEnvelop','click',function(e){

	$('#datanoofbqcqs').on('blur', function() {
		$('.s1_dragdrop_area').removeClass('form-control');
		$('.s1_dragdrop_area').parent().removeClass('has-error');
		if ($(this).val() == '') {
			$('.s1_dragdrop_area').addClass('form-control');
			$('.s1_dragdrop_area').parent().addClass('has-error');
		}
	});

});

function datanoofbqcqsValid() {
	var selectedData = $('#dropable_area li:not(.placeholder)').length;
	if (selectedData == 0) {
		selectedData = '';
	}
	$('#datanoofbqcqs').val(selectedData);
	$('#datanoofbqcqs').blur();
}

function showConfirmDelete(id) {

	$('#myModalEnvelop').modal('show');
	$('#confirmRemovebqcqId').val(id);
}

$(document).delegate('.del_envelop', 'click', function(e) {
	e.preventDefault();
	var envelopeId = $(this).data('delenv');
	var eventId = $(this).data('eventid');

	$('#confirmDeleteenvlp').find('#envepID').val(envelopeId);
	$('#confirmDeleteenvlp').find('#eventId').val(eventId);
	$('#confirmDeleteenvlp').modal();
});

// add Evaluator
$('.addEvaluatorToList').click(function(e) {

	e.preventDefault();
	var currentBlock = $(this);
	var listType = currentBlock.attr('list-type');
	var userId = currentBlock.parent().prev().find('select').val();
	var eventId = $("#eventId").val();
	var envelopeId = $('#id').val();
	var leadEvaluaterid = $('#selectedOwner').val();

	if (userId === undefined || userId === "" || userId === null) {
		$("#evaluator-err").removeClass("hide");
	}

	console.log('envelope ID : ' + envelopeId);
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getBuyerContextPath('addEvaluatorToList');

	/* getContextPath()+"/buyer/RFI/addEvaluatorToList" */
	$.ajax({
		type : "POST",
		url : ajaxUrl,

		data : {
			'userId' : userId,
			'listType' : listType,
			'eventId' : eventId,
			'envelopeId' : envelopeId,
			'leadEvaluaterid':leadEvaluaterid
		},
		beforeSend : function(xhr) {

			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			$('#loading').hide();
		},
		success : function(data) {
			userList = evaluatorListForEvent(data, listType);
			currentBlock.closest('.width100').next('.usersListTable').html(userList);
			currentBlock.parent().prev().find('select').find('option[value="' + userId + '"]').remove();
			currentBlock.parent().prev().find('select').val('').trigger("chosen:updated");
			$('#loading').hide();
			$("#evaluator-err").addClass("hide");
		},
		error : function(request, textStatus, errorThrown) {
			console.log("error");
			console.log("ERROR : " + request.getResponseHeader('error'));
			var error = request.getResponseHeader('error');

			if (request.getResponseHeader('error')) {

				$.jGrowl(request.getResponseHeader('error'), {
					sticky : false,
					position : 'top-right',
					theme : 'bg-red'
				});

				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
			}
			$('#loading').hide();

		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

function evaluatorListForEvent(data, listType) {
	var userList = '';
	$.each(data, function(i, user) {

		userList += '<div class="row" evaluator-id="' + user.user.id + '">';
		if (listType == 'Approver') {
			userList += '<div class="col-md-1 pad0"><a href="" class="upbutton"><i class="fa fa-level-up" aria-hidden="true"></i></a>';
			userList += '<a href="" class="downbutton"><i class="fa fa-level-down" aria-hidden="true"></i></a></div>';
			userList += '<div class="col-md-9 pad0"><p>' + user.user.name + '</p></div>';
		} else {
			userList += '<div class="col-md-10"><p>' + user.user.name + '</p></div>';
		}
		userList += '<div class="col-md-2">';
		userList += '<a href="" class="removeEvaluatorsList" list-type="' + listType + '"><i class="fa fa-times-circle"></i></a></div></div>';
	});
	if (userList == '') {
		userList += '<div class="row" user-id=""><div class="col-md-12"><p>Add ' + listType + '</p></div></div>';
	}
	// console.log(userList);
	return userList;
}

// removeEvaluatorList

$(document).delegate('.removeEvaluatorsList', 'click', function(e) {
	e.preventDefault();
	var currentBlock = $(this);
	var listType = currentBlock.attr('list-type');
	var listUserId = currentBlock.closest('.row').attr('evaluator-id');
	var userName = currentBlock.parent().prev().find('p').text();
	var eventId = $("#eventId").val();
	var envelopeId = $('#id').val();

	$("#removeEvaluatorListPopup").dialog({
		modal : true,
		maxWidth : 400,
		minHeight : 100,
		dialogClass : "",
		show : "fadeIn",
		draggable : true,
		resizable : false,
		dialogClass : "dialogBlockLoaded"
	});
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
	$("#removeEvaluatorListPopup").find('.evaluatorInfoBlock').find('span:first-child').text(userName);
	$("#removeEvaluatorListPopup").find('.evaluatorInfoBlock').find('span:last-child').text(listType);
	$("#removeEvaluatorListPopup").find('#evaluatorListId').val(listUserId);
	$("#removeEvaluatorListPopup").find('#evaluatorListType').val(listType + "?");
	$('.ui-dialog-title').text('Remove ' + listType);
});

$(document).delegate('.removeEvaluatorListPerson', 'click', function(e) {
	e.preventDefault();
	if (!$('#editEnvelopeForm').isValid()) {
		return false;
	}
	var userId = $("#removeEvaluatorListPopup").find('#evaluatorListId').val();
	var listType = $("#removeEvaluatorListPopup").find('#evaluatorListType').val();
	var eventId = $("#eventId").val();
	var envelopeId = $('#id').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	console.log(envelopeId + "-" + eventId + "-" + userId);

	$.ajax({
		type : "POST",
		url : getBuyerContextPath('removeEvaluatorToList'),
		data : {
			envelopeId : envelopeId,
			eventId : eventId,
			userId : userId
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success : function(data) {
			userList = evaluatorListForEventDropdown(data, listType);
			$("#evaluatorList1").html(userList);
			$("#evaluatorList1").trigger("chosen:updated");

			if ($('.row[evaluator-id="' + userId + '"]').siblings().length == 0) {
				userList = evaluatorListForEvent('', listType);
				$('.row[evaluator-id="' + userId + '"]').closest('.width100.usersListTable').html(userList);
			}
			$('.row[evaluator-id="' + userId + '"]').remove();
			$("#removeEvaluatorListPopup").dialog('close');
			updateUserList('', $("#evaluatorList1"), 'NORMAL_USER');
			$('#loading').hide();
		},
		error : function(e) {
			console.log(e);
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

function evaluatorListForEventDropdown(data, listType) {
	var userList = '<option value="">Select Evaluator</option>';
	$.each(data, function(i, user) {
		if (user.id == null || user.id == '' || user.id == '-1') {
			userList += '<option value="-1" disabled>' + user.name + '</option>';
		} else {
			userList += '<option value="' + user.id + '">' + user.name + '</option>';
		}
	});
	return userList;
}