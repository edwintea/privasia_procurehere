$.formUtils.addValidator({
	name : 'buyer_address',
	validatorFunction : function(value, $el, config, language, $form) {
		var response = true;
		var fieldName = $el.attr('name');
		console.log($('[name="' + fieldName + '"]:checked').length);
		if ($('[name="' + fieldName + '"]:checked').length == 0) {
			response = false;
		}
		return response;
	},
	errorMessage : 'This is a required field',
	errorMessageKey : 'badBuyerAddress'
});

$(document).delegate('.delivery_add', 'keyup', function() {
	var $rows = $('.role-bottom-ul li');
	var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
	$rows.show().filter(function() {
		var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
		return !~text.indexOf(val);
	}).hide();
});

$(document).delegate('.role-bottom-ul li [type="radio"]', 'click', function() {
	var dataAddress = $(this).closest('li').children('.del-add').html();
	$('.phisicalArressBlock').html(dataAddress);
	$('.physicalCriterion input[type="checkbox"]').prop('checked', true);
	$('.buyerAddressRadios').addClass('active');
	$.uniform.update();
	$("#sub-credit").slideUp();
});

$(document).on("click", ".physicalCriterion + span.pull-left", function(e) {
	$("#sub-credit").slideToggle();
});

$(document).delegate('.access_check', 'click', function(e) {
	$('.access_check').prop('checked', false);
	$(this).prop('checked', true);
	tempId = $(this).attr("id");
	selector = $("#" + tempId).closest("div").find(".dropdown-toggle");
	console.log(selector);
	
	// Remove any icons for roles
	selector.html(""); // Clear any existing icons
 
	if ($(this).data('uid') == "" || $(this).data('uid') == undefined) return;
 
	var userId = $(this).data('uid');
	var prId = $('#id').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getContextPath() + "/buyer/addTeamMemberToList";
 
	if (userId == "") {
		$("#editor-err").removeClass("hide ");
		return;
	}
	$("#editor-err").addClass("hide");
	$.ajax({
		type: "POST",
		url: ajaxUrl,
		data: {
			memberType: "Associate_Owner",
			userId: userId,
			prId: prId
		},
		beforeSend: function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			$('#loading').hide();
		},
		success: function(data) {
			userList = userListForEvent(data);
 
			if ($('#prTeamMembersList').length > 0) {
				$('#prTeamMembersList').DataTable().ajax.reload();
			}
 
			$('#appUsersList').html("");
			$('#appUsersList').html(userList);
			$('#TeamMemberList').find('option[value="' + userId + '"]').remove();
			$('#TeamMemberList').trigger("chosen:updated");
			$('#loading').hide();
		},
		error: function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete: function() {
			$('#loading').hide();
			//window.location.href = window.location.href;
		}
	});
});

$('.addTeamMemberToList').click(function(e) {
	e.preventDefault();
	console.log("Team member is here");
	var currentBlock = $(this);

	// Set Associate Owner as the default member type
	var memberType = "Associate_Owner"; // Removed the previous logic to get the member type
	console.log(memberType);

	var userId = $("#TeamMemberList").val();
	var prId = $('#id').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getContextPath() + "/buyer/addTeamMemberToList"

	if (userId == "") {
		// $('p[id=idGlobalErrorMessage]').html("Please Select User");
		// $('div[id=idGlobalError]').show();
		$("#editor-err").removeClass("hide ");
		return;
	}
	$("#editor-err").addClass("hide");
	$.ajax({
		type : "POST",
		url : ajaxUrl,
		data : {
			memberType : memberType, // Use the default member type
			userId : userId,
			prId : prId
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			$('#loading').hide();
		},
		success : function(data) {
			userList = userListForEvent(data);
			if ($('#prTeamMembersList').length > 0) {
				$('#prTeamMembersList').DataTable().ajax.reload();
			}
			$('#appUsersList').html("");
			$('#appUsersList').html(userList);
			$('#TeamMemberList').find('option[value="' + userId + '"]').remove();
			$('#TeamMemberList').trigger("chosen:updated");
			$('#loading').hide();
			updateUserList('', $("#TeamMemberList"), 'NORMAL_USER');
		},
		error : function(request, textStatus, errorThrown) {

			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
			//window.location.href = window.location.href;
		}
	});
});

function userListForEvent(data) {
	var userList = '';

	$.each(data, function(i, user) {
		userList += '<tr data-username="' + user.user.name + '" approver-id="' + user.user.id + '">';
		userList += '<td class="width_50_fix "></td>';
		userList += '<td>' + user.user.name + '<br> <span>' + user.user.loginId + '</span> </td>';
		// Remove any icons for roles
		userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown" title="Team Member"> </a>';
		userList += '<ul class="dropdown-menu dropup"><li><a href="javascript:void(0);" class="small"><input ';
			userList += 'data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Associate_Owner" ';
		if (user.teamMemberType == "Associate_Owner") userList += ' checked="checked" ';
		userList += 'type="checkbox" value="Associate_Owner">&nbsp;Associate Owner</a> </li>';
		userList += '</ul></li></ul></div></div></td><td>';
		userList += '<div class="cqa_del"> <a href="#" list-type="Team Member" class="removeApproversList" title="Delete"><i class="glyphicon glyphicon-trash" style="color: red" aria-hidden="true" title="Remove"></i></a> </div></td></tr>';
	});

	return userList;
}

function userListForEventDropdown(data, listType) {
	return "";
	var userList = '<option value="">Select Editor</option>';
	$.each(data, function(i, user) {
		userList += '<option value="' + user.id + '">' + user.name + '</option>';
	});
	return userList;
}

// remove approvers list
$(document).delegate('.removeApproversList', 'click', function(e) {

	e.preventDefault();
	var currentBlock = $(this);
	var listType = currentBlock.attr('list-type');
	var listUserId = currentBlock.closest('tr').attr('approver-id');

	var userName = currentBlock.closest('tr').attr('data-username');

	$("#removeApproverListPopup").dialog({
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
	$("#removeApproverListPopup").find('.approverInfoBlock').find('span:first-child').text(userName);
	$("#removeApproverListPopup").find('.approverInfoBlock').find('span:last-child').text(listType);
	$("#removeApproverListPopup").find('#approverListId').val(listUserId);
	$("#removeApproverListPopup").find('#approverListType').val(listType);
	/* $('.ui-dialog-title').text('Remove ' + listType); */
});

$(document).delegate('.removeApproverListPerson', 'click', function(e) {
	var userId = $("#removeApproverListPopup").find('#approverListId').val();
	var listType = $("#removeApproverListPopup").find('#approverListType').val();
	var prId = $('#id').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getContextPath() + "/buyer/removeTeamMemberfromList"

	$.ajax({
		type : "POST",
		url : ajaxUrl,
		data : {
			prId : prId,
			listType : listType,
			userId : userId
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success : function(data) {
			userList = approverListForEventDropdown(data);

			if ($('#prTeamMembersList').length > 0) {
				$('#prTeamMembersList').DataTable().ajax.reload();
			}
			$('#appUsersList tr[approver-id="' + userId + '"]').remove();
			// $('#TeamMemberList').html(userList).trigger("chosen:updated");
			// $('#TeamMemberList').html(userList).trigger("chosen:updated");

			// if ($('.row[approver-id="' + userId + '"]').siblings().length ==
			// 0) {
			// userList = approverListForEvent('', listType);
			// $('.row[approver-id="' + userId +
			// '"]').closest('.width100.usersListTable').html(userList);
			// }

			// $('.row[approver-id="' + userId + '"]').remove();
			updateUserList('', $('#TeamMemberList'), 'NORMAL_USER');
			$("#removeApproverListPopup").dialog('close');
			// reorderApproverList();
		},
		error : function(e) {
			console.log("Error");
		},
		complete : function() {
			$('#loading').hide();
//			window.location.href = window.location.href;
		}
	});
});
function approverListForEventDropdown(data, listType) {

	var userList = '<option value="">Select Team Member</option>';
	$.each(data, function(i, user) {
		userList += '<option value="' + user.id + '">' + user.name + '</option>';
	});
	return userList;
}

/*
 * $('.addEditorToList').click(function(e) { e.preventDefault(); var currentBlock = $(this); var listType = currentBlock.attr('list-type'); var userId =
 * currentBlock.parent().prev().find('select').val(); var prId = $('#id').val(); console.log('PR ID : ' + prId); var header =
 * $("meta[name='_csrf_header']").attr("content"); var token = $("meta[name='_csrf']").attr("content"); var ajaxUrl = getContextPath() +
 * "/buyer/addEditorToList" $.ajax({ type : "POST", url : ajaxUrl,
 * 
 * data : { listType : listType, userId : userId, prId : prId }, beforeSend : function(xhr) {
 * 
 * $('#loading').show(); xhr.setRequestHeader(header, token); xhr.setRequestHeader("Accept", "application/json"); $('#loading').hide(); }, success :
 * function(data) { userList = userListForEvent(data, listType); currentBlock.closest('.width100').next('.usersListTable').html(userList);
 * currentBlock.parent().prev().find('select').find('option[value="' + userId + '"]').remove();
 * currentBlock.parent().prev().find('select').val('').trigger("chosen:updated"); $('#loading').hide(); }, error : function(request, textStatus,
 * errorThrown) { console.log("error"); $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error')); $('div[id=idGlobalError]').show();
 * $('#loading').hide(); }, complete : function() { $('#loading').hide(); } }); });
 * 
 * function userListForEventDropdown(data, listType) { var userList = '<option value="">Select Editor</option>'; $.each(data, function(i, user) {
 * userList += '<option value="' + user.id + '">' + user.name + '</option>'; }); return userList; }
 * 
 * function userListForEvent(data, listType) { var userList = ''; $.each(data, function(i, user) { userList += '<div class="row" user-id="' + user.id +
 * '">'; if (listType == 'Approver') { userList += '<div class="col-md-1 pad0"><a href="" class="upbutton"><i class="fa fa-level-up"
 * aria-hidden="true"></i></a>'; userList += '<a href="" class="downbutton"><i class="fa fa-level-down" aria-hidden="true"></i></a></div>';
 * userList += '<div class="col-md-9 pad0"><p>' + user.name + '</p></div>'; } else { userList += '<div class="col-md-10"><p>' + user.name + '</p></div>'; }
 * userList += '<div class="col-md-2">'; userList += '<a href="" class="removeUserList" list-type="' + listType + '"><i class="fa fa-times-circle"></i></a></div></div>';
 * }); if (userList == '') { userList += '<div class="row" user-id=""><div class="col-md-12"><p>Add ' + listType + '</p></div></div>'; } return
 * userList; }
 * 
 * $('.addViwerToList').click(function(e) { e.preventDefault(); var currentBlock = $(this); var listType = currentBlock.attr('list-type'); var userId =
 * currentBlock.parent().prev().find('select').val(); var prId = $('#id').val(); var header = $("meta[name='_csrf_header']").attr("content"); var
 * token = $("meta[name='_csrf']").attr("content"); var ajaxUrl = getContextPath() + "/buyer/addViwerToList" $.ajax({ type : "POST", url : ajaxUrl,
 * data : { listType : listType, userId : userId, prId : prId }, beforeSend : function(xhr) {
 * 
 * $('#loading').show(); xhr.setRequestHeader(header, token); xhr.setRequestHeader("Accept", "application/json"); $('#loading').hide(); }, success :
 * function(data) { userList = viewerListForEvent(data, listType); currentBlock.closest('.width100').next('.usersListTable').html(userList);
 * currentBlock.parent().prev().find('select').find('option[value="' + userId + '"]').remove();
 * currentBlock.parent().prev().find('select').val('').trigger("chosen:updated"); $('#loading').hide(); }, error : function(request, textStatus,
 * errorThrown) { $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error')); $('div[id=idGlobalError]').show(); $('#loading').hide(); },
 * complete : function() { $('#loading').hide(); } }); });
 * 
 * function viewerListForEventDropdown(data, listType) { var userList = '<option value="">Select Viewer</option>'; $.each(data, function(i, user) {
 * userList += '<option value="' + user.id + '">' + user.name + '</option>'; }); return userList; } function viewerListForEvent(data, listType) {
 * var userList = ''; $.each(data, function(i, user) { userList += '<div class="row" viewer-id="' + user.id + '">'; if (listType == 'Approver') {
 * userList += '<div class="col-md-1 pad0"><a href="" class="upbutton"><i class="fa fa-level-up" aria-hidden="true"></i></a>'; userList += '<a
 * href="" class="downbutton"><i class="fa fa-level-down" aria-hidden="true"></i></a></div>'; userList += '<div class="col-md-9 pad0"><p>' +
 * user.name + '</p></div>'; } else { userList += '<div class="col-md-10"><p>' + user.name + '</p></div>'; } userList += '<div
 * class="col-md-2">'; userList += '<a href="" class="removeViewersList" list-type="' + listType + '"><i class="fa fa-times-circle"></i></a></div></div>';
 * }); if (userList == '') { userList += '<div class="row" viewer-id=""><div class="col-md-12"><p>Add ' + listType + '</p></div></div>'; }
 * return userList; } // remove Editor list
 * 
 * $(document).delegate('.removeUserList', 'click', function(e) { e.preventDefault(); var currentBlock = $(this); var listType =
 * currentBlock.attr('list-type'); var listUserId = currentBlock.closest('.row').attr('user-id'); var userName =
 * currentBlock.parent().prev().find('p').text();
 * 
 * $("#removeUserListPopup").dialog({ modal : true, maxWidth : 400, minHeight : 100, dialogClass : "", show : "fadeIn", draggable : true, resizable :
 * false, dialogClass : "dialogBlockLoaded" }); $('.ui-widget-overlay').addClass('bg-white opacity-60');
 * $("#removeUserListPopup").find('.userInfoBlock').find('span:first-child').text(userName);
 * $("#removeUserListPopup").find('.userInfoBlock').find('span:last-child').text(listType);
 * $("#removeUserListPopup").find('#userListId').val(listUserId); $("#removeUserListPopup").find('#userListType').val(listType);
 * $('.ui-dialog-title').text('Remove ' + listType); });
 * 
 * $(document).delegate('.removeUserListPerson', 'click', function(e) { var userId = $("#removeUserListPopup").find('#userListId').val(); var listType =
 * $("#removeUserListPopup").find('#userListType').val(); var prId = $('#id').val(); var header = $("meta[name='_csrf_header']").attr("content"); var
 * token = $("meta[name='_csrf']").attr("content"); var ajaxUrl = getContextPath() + "/buyer/removeEditorToList" $.ajax({ type : "POST", url :
 * ajaxUrl, data : { prId : prId, listType : listType, userId : userId }, beforeSend : function(xhr) { $('#loading').show();
 * xhr.setRequestHeader(header, token); xhr.setRequestHeader("Accept", "application/json"); }, success : function(data) { userList =
 * userListForEventDropdown(data, listType); // $('.row[user-id="'+userId+'"]').closest('.width100.usersListTable').html(userList); $('.row[user-id="' +
 * userId + '"]').closest('.width100.usersListTable').prev().find('select').html(userList); $('.row[user-id="' + userId +
 * '"]').closest('.width100.usersListTable').prev().find('select').trigger("chosen:updated"); if ($('.row[user-id="' + userId +
 * '"]').siblings().length == 0) { userList = userListForEvent('', listType); $('.row[user-id="' + userId +
 * '"]').closest('.width100.usersListTable').html(userList); } $('.row[user-id="' + userId + '"]').remove();
 * $("#removeUserListPopup").dialog('close'); }, error : function(e) { console.log("Error"); }, complete : function() { $('#loading').hide(); } });
 * }); // remove viewer list
 * 
 * $(document).delegate('.removeViewersList', 'click', function(e) {
 * 
 * e.preventDefault(); var currentBlock = $(this); var listType = currentBlock.attr('list-type'); var listUserId =
 * currentBlock.closest('.row').attr('viewer-id'); var userName = currentBlock.parent().prev().find('p').text();
 * 
 * $("#removeViewerListPopup").dialog({ modal : true, maxWidth : 400, minHeight : 100, dialogClass : "", show : "fadeIn", draggable : true, resizable :
 * false, dialogClass : "dialogBlockLoaded" }); $('.ui-widget-overlay').addClass('bg-white opacity-60');
 * $("#removeViewerListPopup").find('.viewerInfoBlock').find('span:first-child').text(userName);
 * $("#removeViewerListPopup").find('.viewerInfoBlock').find('span:last-child').text(listType);
 * $("#removeViewerListPopup").find('#viewerListId').val(listUserId); $("#removeViewerListPopup").find('#viewerListType').val(listType);
 * $('.ui-dialog-title').text('Remove ' + listType); });
 * 
 * $(document).delegate('.removeViewerListPerson', 'click', function(e) { var userId = $("#removeViewerListPopup").find('#viewerListId').val(); var
 * listType = $("#removeViewerListPopup").find('#viewerListType').val(); var prId = $('#id').val(); var header =
 * $("meta[name='_csrf_header']").attr("content"); var token = $("meta[name='_csrf']").attr("content"); var ajaxUrl = getContextPath() +
 * "/buyer/removeViwerToList" $.ajax({ type : "POST", url : ajaxUrl, data : { prId : prId, listType : listType, userId : userId }, beforeSend :
 * function(xhr) { $('#loading').show(); xhr.setRequestHeader(header, token); xhr.setRequestHeader("Accept", "application/json"); }, success :
 * function(data) { userList = viewerListForEventDropdown(data, listType); //
 * $('.row[user-id="'+userId+'"]').closest('.width100.usersListTable').html(userList); $('.row[viewer-id="' + userId +
 * '"]').closest('.width100.usersListTable').prev().find('select').html(userList); $('.row[viewer-id="' + userId +
 * '"]').closest('.width100.usersListTable').prev().find('select').trigger("chosen:updated"); if ($('.row[viewer-id="' + userId +
 * '"]').siblings().length == 0) { userList = viewerListForEvent('', listType); $('.row[viewer-id="' + userId +
 * '"]').closest('.width100.usersListTable').html(userList); } $('.row[viewer-id="' + userId + '"]').remove();
 * $("#removeViewerListPopup").dialog('close'); }, error : function(e) { console.log("Error"); }, complete : function() { $('#loading').hide(); } });
 */

// Skip JQuery validations for save draft
$(".skipvalidation ").on('click', function(e) {
	if ($("#skipper").val() == undefined) {
		e.preventDefault();
		$(this).after("<input type='hidden' id='skipper' value='1'>");
		$('form.has-validation-callback :input').each(function() {
			$(this).on('beforeValidation', function(value, lang, config) {
				$(this).attr('data-validation-skipped', 1);
			});
		});
		$(this).trigger("click");
	}
});

// save draft
$('#submitStep1PrDetailDraft').click(function(e) {
	$('#prCreateForm').attr('action', getContextPath() + "/buyer/savePrDraft");
	$('#prCreateForm').submit();

});

$('#businessUnit ,#costCenterId').on('change', function(e) {
	e.preventDefault();
	var currentBlock = $(this);
	$("#remainAmt").css("visibility", "hidden");
	var businessUnitId = $("#businessUnit").val();
	var costCenterId = $("#costCenterId").val();
	var lockBudget=$("#lockBudgetValue").val();
	if(lockBudget=='true'){
		console.log('budget true');
		if(businessUnitId!=''&& costCenterId!=''){
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			
		var ajaxUrl = getContextPath() + "/buyer/checkBudgetAmount/";
	
		$.ajax({
			type : "GET",
			url : ajaxUrl + businessUnitId + "/" + costCenterId,
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				$('#loading').hide();
			},
			success : function(data, textStatus, request) {
				$("#remainAmt").text(request.getResponseHeader('remainingAmt'));
				$("#remainAmt").css("visibility", "visible");
				$('#loading').hide();
				$('#idGlobalInfo').hide();
				$('div[id=idGlobalError]').hide();
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
		}
	}else{
		console.log("not true");
	}
});





