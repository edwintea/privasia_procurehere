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
	$('.buyerAddressRadios1').addClass('active');
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

	if ($(this).data('uid') == "" || $(this).data('uid') == undefined) return;

	/** ** Update ** */
	var userId = $(this).data('uid');
	var prId = $('#id').val();
	var poId = $('#poId').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getContextPath() + "/buyer/addTeamMemberToPoList"

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
			memberType : "Associate_Owner",
			userId : userId,
			poId : poId
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

$('.addTeamMemberToPoList').off().on('click',function(e) {
	var currentBlock = $(this);
	var memberType="Associate_Owner";
	$('.access_check').prop('checked', false);

	var userId = $("#TeamMemberList").val();
	var prId = $('#prId').val();
	var poId = $('#poId').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getContextPath() + "/buyer/addTeamMemberToPoList"


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
			memberType : memberType,
			userId : userId,
			poId : poId
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
			updateUserList('', $("#TeamMemberList"), 'ALL_USER');
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
	e.preventDefault();
});

function userListForEvent(data) {
	var userList = '';

	$.each(data, function(i, user) {
		userList += '<tr  data-username="' + user.user.name + '" approver-id="' + user.user.id + '">';
		userList += '<td class="width_50_fix "></td>';
		userList += '<td>' + user.user.name + '<br> <span>' + user.user.loginId + '</span> </td>';
        /*
		userList += '<td class="edit-drop"><div class="advancee_menu"><div class="adm_box">';
		    userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown" title="Associate Owner">  </a>';
		    userList += '<ul class="dropdown-menu dropup"><li><a href="javascript:void(0);" class="small"><input ';
		        userList += 'data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Associate_Owner" ';
		        userList += ' checked="checked"  ';
		        userList += 'type="checkbox" value="Associate_Owner">&nbsp;Associate Owner</a> </li>';
        userList += '</ul></li></ul></div></div></td>';
        */
		userList += '<td><div class="cqa_del"> <a href="#" list-type="Team Member" class="removeApproversList" title="Delete"></a> </div></td></tr>'
	});
	if (userList == '') {
		// userList += '<div class="row" user-id=""><div
		// class="col-md-12"><p>Add ' + listType + '</p></div></div>';
	}
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
		dialogClass : "removeApproverDialogBlockLoaded"
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
	var prId = $('#prId').val();
	var poId = $('#poId').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getContextPath() + "/buyer/removeTeamMemberfromPoList"

	$.ajax({
		type : "POST",
		url : ajaxUrl,
		data : {
			poId : poId,
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

// Handle the form submission
$('#poCreateForm').on('submit', function(event) {
    $('#loading').show();
    event.preventDefault(); // Prevent the default form submission
    var formData = $(this).serialize();
    let poId = $('#poId').val();
    let prId = $('#prId').val();

    let approval = $('.approval_div').length; //count dom if exist
    console.log(typeof approval);

    if (typeof approval !== 'undefined') {
        if(parseInt(approval) > 0){
            let check = checkCurrentApprovalCount();
            console.log(check);
            console.log(typeof check.passed)

            if(!check.passed){
                $('p[id=idGlobalErrorMessage]').html('Add Minimum "'+check.minApprovalCount+'" Approval Route');
                $('div[id=idGlobalError]').show();
                $('div[id=idGlobalSuccess]').hide();

                $(window).scrollTop($('div[id=idGlobalError]').offset().top);
                return false;
            }
        }
    }//else approval is not pre-set

    $.ajax({
        url: getContextPath()+'/buyer/createPoDetails/'+prId,
        type: $(this).attr('method'),
        data: formData,
        success: function(response) {
            if(!toDashboard){
                window.location.href = getContextPath() +'/buyer/po/poDocumentView/' + poId+'?prId='+prId;
            }else{
                window.location.href = getContextPath() +'/buyer/savePoDraft/' + poId+'?prId='+prId;
            }
        },
        error: function(xhr, status, error) {
            alert('An error occurred: ' + error);
        }
    });
});


// save draft
$('#submitPoDraft').off().on('click',function(e) {
    toDashboard = true;
	$('#poCreateForm').attr('action', getContextPath() + "/buyer/savePoDraft");
	$('#poCreateForm').submit();
	return false;
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

$('.mdl_cancel_po').on('shown.bs.modal', function () {
    console.log("Modal is now shown.");
    $('#cancelReason').val('');
    $('#cancelPo').removeAttr('disabled');
    $('.form-error').hide()
});