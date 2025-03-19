$(function() {
	currentSourcingBlocks();
	$('.nav-tabs a#tabTemplateId').on('hidden.bs.tab', function(e) {
		currentSourcingBlocks();
	});
	$(window).resize(function() {
		currentSourcingBlocks();
	});
	$('.searchrftEventTT').click(function(event) {
		event.preventDefault();
		$("#idGlobalInfo").hide();
		$("#idGlobalError").hide();
		$("#idGlobalWarn").hide();
		$("#idEventInfo").hide();

		var searchValue = $('#searchValue').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type : "POST",
			url : getContextPath() + "/buyer/searchSourcingRequest",
			data : {
				searchValue : searchValue,
			},
			dataType : "json",
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data) {
				var html = '';
				var found = false;
				$.each(data, function(key, value) {
					found = true;
					html += '<div class="col-md-3 marg-bottom-10 idRftEvent" id="' + value.id + '" data-value="' + value.id + '" style="display: block">';
					html += '<div class="lower-bar-search-contant-main-block" id="test">';
					html += '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">';
					html += '<h4 class="text-ellipsis-x" >' + (value.sourcingFormName ? value.sourcingFormName : '') + '</h4></div>';
					html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
					html += '<label>Reference Number :</label> <span class="green">' + (value.referanceNumber ? value.referanceNumber : '') + '</span> </div>';
					html += '<div class="lower-bar-search-contant-main-contant pad-top-side-10">';
					html += '<label>Created By : </label> <span class="green">' + (value.createdByName ? value.createdByName : '') + '</span></div>';
					html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
					html += '<label>Created Date :</label> <span class="green">' + (value.createdDate ? value.createdDate : '') + '</span></div>';
					html += '<div class="lower-bar-search-contant-main-contant  pad_all_10"><div>';
					html += '<form action="' + getContextPath() + '/buyer/copyFromSourcingFormRequest" class="col-md-12" method="post" style="float: right;">';
					html += '<input type="hidden" id="formId" value="' + value.id + '" name="formId">';
					html += '<input type="hidden" id="_csrf" value="' + token + '" name="_csrf">';
					html += '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>';
					html += '</form>' + '</div></div></div></div>';
				});
				$('#rftEvents').show();
				$('#rftEvents > .row').html(html);
				currentSourcingBlocks();
				if (!found) {
					$("#idEventInfoMessage").html('No matching data found');
					$("#idEventInfo").show();
					$('#rftEvents').hide();
				}
			},
			error : function(request, textStatus, errorThrown) {
				if (request.getResponseHeader('error')) {
					$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
					$("#idGlobalError").show();
				}
				if (request.getResponseHeader('info')) {
					$("#idGlobalInfoMessage").html(request.getResponseHeader('info').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
					$("#idGlobalInfo").show();
				}
			}
		});
	});

});

$(document).delegate('.quickview ', 'click', function(e) {

	diag_id = $(this).data("qv");

	$("#" + diag_id).dialog({
		modal : true,
		minWidth : 300,
		width : '90%',
		maxWidth : 600,
		minHeight : 200,
		dialogClass : "",
		show : "fadeIn",
		draggable : false,
		dialogClass : "dialogBlockLoaded"
	});
});

$('.searchSourcingTemplatesss').click(function(event) {
	$("#idGlobalInfo").hide();
	$("#idTemplateInfo").hide();
	$("#idGlobalError").hide();
	$("#idGlobalWarn").hide();

	var templateName = $('#idTemplateName').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		type : "POST",
		url : getContextPath() + "/buyer/searchSourcingTemplate",
		data : {
			templateName : templateName,
		},
		dataType : "json",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data) {
			var html = '';
			var found = false;
			$.each(data, function(key, value) {
				found = true;
				
				html += '<div class="col-md-3 marg-bottom-10 idRftEvent currentTemplates" id="' + value.id + '" data-value="' + value.id + '" style="display: block">';
				html += '<div class="lower-bar-search-contant-main-block copy-frm-prev" id="test">';
				html += '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">';
				html += '<h4 class="text-ellipsis-x" title="' + value.formName + '">' + value.formName + '</h4></div>';
				html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5 descBlock disp-flex">';
				html += '<div class="green text-ellipsis-x">';
				html += '<label class="pull-left">Description :</label> <span style="color: #06ccb3 !important" data-toggle="tooltip" data-original-title="' + value.description + '"> '
							+ ( value.description == undefined ? '' : value.description ) + ' </span> </div></div>';
				html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
				html += '<label>Created By :</label> <span class="green">' + (value.createdByName ? value.createdByName : '') + '</span></div>';
				html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
				html += '<label>Created Date :</label> <span class="green">' + value.createdDate + '</span> </div>';
				html += '<div class="lower-bar-search-contant-main-contant  pad_all_10"><div>';
				html += '<form action="' + getContextPath() + '/buyer/copyFromSourcingTemplate" class="col-md-12" method="post" style="float: right;">';
				html += '<input type="hidden" id="sourcingTemplateId" value="' + value.id + '" name="sourcingTemplateId">';
				html += '<input type="hidden" id="_csrf" value="' + token + '" name="_csrf">';
				html += '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>';
				html += '</form></div>';
				html += '</div></div></div>';
			});
			$('#prTemplates > .row').html(html);
			currentSourcingBlocks();
			if (!found) {
				$("#idTemplateInfoMessage").html('No matching data found');
				$("#idTemplateInfo").show();
				$('#rftEvents').hide();
			}
		},
		error : function(request, textStatus, errorThrown) {
			if (request.getResponseHeader('error')) {
				$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
				$("#idGlobalError").show();
			}
			if (request.getResponseHeader('info')) {
				$("#idGlobalInfoMessage").html(request.getResponseHeader('info').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
				$("#idGlobalInfo").show();
			}
		}
	});
});

$('#tabTemplateId').click(function() {
	template = true;
	previousPr = false;
	currentSourcingBlocks();
	$('.searchTemplatefield').removeClass('flagvisibility');
	$('.searchpreviousfield').addClass('flagvisibility');
});
$('#tabPreviousId').click(function() {
	previousPr = true;
	template = false;
	currentSourcingBlocks();
	$('.searchpreviousfield').removeClass('flagvisibility');
	$('.searchTemplatefield').addClass('flagvisibility');
});
var template = true;
var previousPr = false;
var scrollTimer, lastScrollFireTime = 0;

// on scroll template loading templates
var templatePageNo = 0;

// on scroll copy previous loading previous pr
var prPageNo = 0;

function currentSourcingBlocks() {
	var heights = [];
	var fullWidth = parseInt($('.tab-pane.active').width());
	var blkWidth = parseInt($('.currentTemplates:first').width());
	var noofBlok = parseInt(fullWidth / blkWidth);
	$(".currentTemplates:visible").each(function(i) {
		var eachhgt = {};
		if ($(this).attr('data-value') != undefined) {
			if ($(this).hasClass('hightedBlock')) {
				eachhgt['hgt'] = $(this).height() - 83;
			} else {
				eachhgt['hgt'] = $(this).height();
			}
		} else {
			eachhgt['hgt'] = 0;
		}
		eachhgt['value'] = $(this).attr('data-value');
		heights.push(eachhgt);
	});

	var maxhgt = [];
	var prevBlocks = [];
	$.each(heights, function(i, hgt) {
		maxhgt.push(hgt.hgt);
		prevBlocks.push(hgt.value);
		if (i == (noofBlok - 1) || (i > (noofBlok - 1) && ((i + 1) % noofBlok) == 0)) {
			var MaxValHgt = Math.max.apply(null, maxhgt);
			var descHgt = [];
			var createdHgt = [];
			$.each(prevBlocks, function(i, blk) {
				if (blk != undefined) {
					$(".currentTemplates[data-value=" + blk + "] > div").css('min-height', (MaxValHgt + 83)).parent().addClass('hightedBlock');
				}
			});
			maxhgt = [];
			prevBlocks = [];
		}
	});
	var MaxValHgt = Math.max.apply(null, maxhgt);
	$.each(prevBlocks, function(i, blk) {
		if (blk != undefined) {
			$(".currentTemplates[data-value=" + blk + "] > div").css('min-height', (MaxValHgt + 83)).parent().addClass('hightedBlock');
		}
	});
}
function priviousTemplatesBlocks() {
	var heights = [];
	$(".priviousTemplates:visible").each(function(i) {
		var eachhgt = {};
		if ($(this).attr('data-value') != undefined) {
			eachhgt['hgt'] = $(this).height();
		} else {
			eachhgt['hgt'] = 0;
		}
		eachhgt['value'] = $(this).attr('data-value');
		heights.push(eachhgt);
	});
	console.log(heights);
	var maxhgt = [];
	var prevBlocks = [];
	$.each(heights, function(i, hgt) {
		maxhgt.push(hgt.hgt);
		prevBlocks.push(hgt.value);
		if (i == 3 || (i > 3 && ((i + 1) % 4) == 0)) {
			var MaxValHgt = Math.max.apply(null, maxhgt);
			var descHgt = [];
			var createdHgt = [];
			$.each(prevBlocks, function(i, blk) {
				if (blk != undefined) {
					$(".priviousTemplates[data-value=" + blk + "] > div").css('min-height', (MaxValHgt + 83));
				}
			});
			maxhgt = [];
			prevBlocks = [];
		}
	});
	var MaxValHgt = Math.max.apply(null, maxhgt);
	$.each(prevBlocks, function(i, blk) {
		if (blk != undefined) {
			$(".priviousTemplates[data-value=" + blk + "] > div").css('min-height', (MaxValHgt + 83));
		}
	});
}
$(document).ready(function() {

	$('.addTeamMemberToList').click(function(e) {
		e.preventDefault();
		var currentBlock = $(this);

		var memberType = $(this).closest('div').find('.access_check:checkbox:checked').val(); // $('.access_check:checkbox:checked').val();//
		if (memberType == undefined || memberType == "") {
			memberType = "Viewer";
		}
		var userId = $("#TeamMemberList").val();// currentBlock.parent().prev().find('select').val();
		var formId = $('.event_form').find('#id').val();
		var ajaxUrl = getContextPath() + "/buyer/teamMember/addTeamMemberToList"
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		if (userId == "") {
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
				formId : formId
			},
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				$('#loading').hide();
			},
			success : function(data) {
				userList = userListForEvent(data);

				$('#appUsersList').html("");
				$('#appUsersList').html(userList);
				$('#TeamMemberList').find('option[value="' + userId + '"]').remove();
				$('#TeamMemberList').trigger("chosen:updated");
				$('#loading').hide();
				if ($('#eventTeamMembersList').length > 0) {
					$('#eventTeamMembersList').DataTable().ajax.reload();
				}
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

	$(document).delegate('.access_check', 'click', function(e) {
		$('.access_check').prop('checked', false);
		$(this).prop('checked', true);
		tempId = $(this).attr("id");
		selector = $("#" + tempId).closest("div").find(".dropdown-toggle");
		console.log(selector);
		if ($(this).val() == "Editor") {

			selector.html("<i class='glyphicon glyphicon-pencil' aria-hidden='true '></i>");
		}
		if ($(this).val() == "Associate_Owner") {
			selector.html("<i class='fa fa-user-plus' aria-hidden='true '></i>");
		}
		if ($(this).val() == "Viewer") {
			selector.html("<i class='glyphicon glyphicon-eye-open' aria-hidden='true '></i>");
		}

		if ($(this).data('uid') == "" || $(this).data('uid') == undefined) return;

		/** ** Update ** */
		var memberType = $(this).val();
		var currentBlock = $(this);

		var userId = $(this).data('uid');
		var formId = $('.event_form').find('#id').val();
		var ajaxUrl = getContextPath() + "/buyer/teamMember/addTeamMemberToList"
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type : "POST",
			url : ajaxUrl,
			data : {
				memberType : memberType,
				userId : userId,
				formId : formId
			},
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				$('#loading').hide();
			},
			success : function(data) {
				userList = userListForEvent(data);
				if ($('#eventTeamMembersList').length > 0) {
					$('#eventTeamMembersList').DataTable().ajax.reload();
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

	function userListForEvent(data) {
		var userList = '';

		$.each(data, function(i, user) {
			userList += '<tr  data-username="' + user.user.name + '" approver-id="' + user.user.id + '">';
			userList += '<td class="width_50_fix "></td>';
			userList += '<td>' + user.user.name + '<br> <span>' + user.user.loginId + '</span> </td>';
			userList += '<td class="edit-drop"><div class="advancee_menu"><div class="adm_box">';
			if (user.teamMemberType == "Editor")
				userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"  title="Editor"> <i class="glyphicon glyphicon-pencil " aria-hidden="true"></i> </a>';
			else if (user.teamMemberType == "Viewer")
				userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"  title="Viewer"> <i class="glyphicon glyphicon-eye-open" aria-hidden="true"></i> </a>';
			else if (user.teamMemberType == "Associate_Owner") userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"  title="Associate Owner"> <i class="fa fa-user-plus" aria-hidden="true"></i> </a>';
			userList += '<ul class="dropdown-menu dropup"><li><a href="javascript:void(0);" class="small"><input ';
			if (user.teamMemberType == "Editor") userList += ' checked="checked"  ';
			userList += 'data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Editor" type="checkbox" value="Editor">&nbsp;Editor</a> </li>';
			userList += '<li><a  href="javascript:void(0);" class="small"><input data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Viewer" ';
			if (user.teamMemberType == "Viewer") userList += ' checked="checked"  ';
			userList += 'data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Viewer" type="checkbox" value="Viewer">&nbsp;Viewer</a> </li>';
			userList += '<li><a  href="javascript:void(0);" class="small"><input data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Associate_Owner" ';
			if (user.teamMemberType == "Associate_Owner") userList += ' checked="checked"  ';
			userList += 'type="checkbox" value="Associate_Owner">&nbsp;Associate Owner</a> </li>';
			userList += '</ul></li></ul></div></div></td><td>'
			userList += '<div class="cqa_del"> <a href="#" list-type="Team Member" class="removeApproversList"  title="Delete"></a> </div></td></tr>'

		});
		if (userList == '') {
			// userList += '<div class="row" user-id=""><div
			// class="col-md-12"><p>Add ' + listType + '</p></div></div>';
		}
		return userList;
	}

	$(document).delegate('.removeApproverListPerson', 'click', function(e) {
		var userId = $("#removeApproverListPopup").find('#approverListId').val();
		var listType = $("#removeApproverListPopup").find('#approverListType').val();
		var formId = $('.event_form').find('#id').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type : "POST",
			url : getBuyerContextPath('removeSourcingTeamMemberfromList'),
			data : {
				formId : formId,
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
				$('#appUsersList tr[approver-id="' + userId + '"]').remove();
				$('#TeamMemberList').html(userList).trigger("chosen:updated");
				$("#removeApproverListPopup").dialog('close');
				if ($('#eventTeamMembersList').length > 0) {

					$('#eventTeamMembersList').DataTable().ajax.reload();
				}
				updateUserList('', $("#TeamMemberList"), 'NORMAL_USER');
			},
			error : function(e) {
				console.log("Error");
			},
			complete : function() {
				$('#loading').hide();
//				window.location.href = window.location.href;
			}
		});
	});

	// remove approvers list
	$(document).delegate('.removeApproversList', 'click', function(e) {
		e.preventDefault();
		var currentBlock = $(this);
		var listType = currentBlock.attr('list-type');

		var listUserId = currentBlock.closest('tr').attr('approver-id');
		console.log(listUserId);
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
	});

	function approverListForEventDropdown(data, listType) {

		var userList = '<option value="">Select Team Member</option>';
		$.each(data, function(i, user) {
			userList += '<option value="' + user.id + '">' + user.name + '</option>';
		});
		return userList;
	}

});
