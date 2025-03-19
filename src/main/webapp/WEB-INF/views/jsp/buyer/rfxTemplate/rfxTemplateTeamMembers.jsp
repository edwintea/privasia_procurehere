<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<div class="meeting2-heading">
	<h3>
		<spring:message code="team.member.label" />
	</h3>
</div>
<div>
	<div class="col-sm-5 col-md-5 col-xs-6" style="margin: 1%;">
			<div class="col-md-12" id="teamMember">
			<div class="ia-invite-controls-area">
				<div class="group">
					<div class="input-group mrg15T mrg15B">
						<select id="TeamMemberList" class="chosen-select user-list-normal" selected-id="approver-id" name="approverList1" cssClass="form-control chosen-select">
							<option value=""><spring:message code="select.team.member.placeholder" /></option>
							<c:forEach items="${userTeamMembers}" var="TeamMember" varStatus="count">
								<%-- <option value="${TeamMember.id}" data-name="${TeamMember.name}">${TeamMember.name}</option> --%>
								<c:if test="${TeamMember.id == '-1' }">
									<option value="-1" disabled>${TeamMember.name}</option>
								</c:if>
								<c:if test="${TeamMember.id != '-1' }">
									<option value="${TeamMember.id}" data-name="${TeamMember.name}">${TeamMember.name}</option>
								</c:if>
							</c:forEach>
							<c:set var="qqq" scope="session" value="${userTeamMembers}" />
						</select>
						<div class="input-group-btn">
							<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
								<span class="glyphicon glyphicon-eye-open"></span>
							</button>
							<ul class="dropdown-menu dropup">

								<li><a href="javascript:void(0);" class="small " tabIndex="-1">
										<input id="access_editor" value="Editor" class="access_check" type="checkbox" />
										&nbsp;
										<spring:message code="eventsummary.checkbox.editor" />
									</a></li>

								<li><a href="javascript:void(0);" class="small " tabIndex="-1">
										<input id="access_viewer" value="Viewer" class="access_check" type="checkbox" />
										&nbsp;
										<spring:message code="eventsummary.checkbox.viewer" />
									</a></li>

								<li><a href="javascript:void(0);" class="small " tabIndex="-1">
										<input id="access_Associate_Owner" value="Associate_Owner" class="access_check" type="checkbox" />
										&nbsp;
										<spring:message code="eventsummary.checkbox.associate.owner" />
									</a></li>
							</ul>
							<button class="btn btn-primary addTemplateTeamMembers" type="button">+</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="col-md-12 col-sm-12 col-xs-12">
			<div class="mem-tab">
				<table cellpading="0" cellspacing="0" width="100%" id="appUsersList" class="table-hover table tableApp">
					<c:forEach items="${assignedTeamMembers}" var="teamMembers" varStatus="status">
						<tr data-username="${teamMembers.user.name}" approver-id="${teamMembers.user.id}" approver-type="${teamMembers.teamMemberType}">
							<td class="width_50_fix "></td>
							<td>${teamMembers.user.name}<br> <span>${teamMembers.user.loginId}</span> <form:input type="hidden" path="teamMembers[${status.index}].user.id" name="teamMembers[${status.index}].user.id" value="${teamMembers.user.id}" id="userId-${status.index}" />
							</td>
							<td class="edit-drop">
								<div class="advancee_menu">
									<div class="adm_box">
										<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown">
											<i class="glyphicon ${teamMembers.teamMemberType=='Editor'?'glyphicon-pencil':teamMembers.teamMemberType=='Viewer'?'glyphicon-eye-open':teamMembers.teamMemberType=='Associate_Owner'?'fa fa-user-plus':''}" aria-hidden="true"
												title="${teamMembers.teamMemberType=='Editor'?'editor':teamMembers.teamMemberType=='Viewer'?'Viewer':teamMembers.teamMemberType=='Associate_Owner'?'Associate Owner':''}"></i>
										</a>

										<form:input type="hidden" path="teamMembers[${status.index}].teamMemberType" class="memberTypeClass" value="${teamMembers.teamMemberType}" name="teamMembers[${status.index}].teamMemberType" id="memberType-${status.index}" />
										</a>
										<ul class="dropdown-menu dropup">

											<li><a href="javascript:void(0);" class="small" tabIndex="-1">
													<input data-uid="${teamMembers.user.id}" id="${teamMembers.user.id}-Editor" ${teamMembers.teamMemberType=='Editor' ? 'checked' : ''} value="Editor" class="access_check" type="checkbox" data-index="${status.index}" />
													&nbsp;
													<spring:message code="eventsummary.checkbox.editor" />
												</a></li>

											<li><a href="javascript:void(0);" class="small " tabIndex="-1">
													<input id="${teamMembers.user.id}-Viewer" ${teamMembers.teamMemberType=='Viewer'?'checked': ''} value="Viewer" data-uid="${teamMembers.user.id}" class="access_check" type="checkbox" data-index="${status.index}" />
													&nbsp;
													<spring:message code="eventsummary.checkbox.viewer" />
												</a></li>

											<li><a href="javascript:void(0);" class="small " tabIndex="-1">
													<input id="${teamMembers.user.id}-Associate_Owner" ${teamMembers.teamMemberType=='Associate_Owner'?'checked': ''} value="Associate_Owner" data-uid="${teamMembers.user.id}" class="access_check" type="checkbox" data-index="${status.index}" />
													&nbsp;
													<spring:message code="eventsummary.checkbox.associate.owner" />
												</a></li>
										</ul>
									</div>
								</div>
							</td>
							<td>
								<div class="cqa_del">
									<a href="javascript:void(0);" list-type="Team Member" class="removeApproversList" data-toggle="dropdown" title="Delete"></a>
								</div>
							</td>
						</tr>
					</c:forEach>

				</table>
			</div>
		</div>
	</div>
		<c:if test="${teamMember}">
			<div class="col-md-2 sup-category  " style="margin: 1%;">
				<div class="check-wrapper  ${assignedCount > 0 ? 'disabled':''}">
					<spring:message code="prtemplate.label.read.only" var="read" />
					<span><form:checkbox path="readOnlyTeamMember" id="teamMemberReadOnly" class="custom-checkbox" title="" label="${read}" /></span>
				</div>
			</div>
		</c:if>

</div>
<!--Team Member Delete  popup-->
<div class="flagvisibility dialogBox" id="removeApproverListPopup" title='<spring:message code="tooltip.remove.team.member" />'>
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="approverListId" name="approverListId" value="">
		<input type="hidden" id="approverListType" name="approverListType" value="">
		<input type="hidden" id="approverListName" name="approverListName" value="">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock">
				<spring:message code="event.confirm.to.remove" />
				<span></span>
				<spring:message code="application.from" />
				<span></span>
				?
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">

					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeApproverListPerson" data-original-title="Delete">
						<spring:message code="tooltip.delete" />
					</a>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>
<style>
.input-group.mrg15T.mrg15B {
	background-color: #f5f5f5;
	margin-bottom: 0;
	padding: 12px;
}

.mem-tab {
	border: 1px solid #ddd;
	border-radius: 2px;
	float: left;
	height: 300px;
	overflow: auto;
	position: relative;
	width: 100%;
}

.tableApp>tbody>tr>td {
	padding: 5px !important;
}

.box_active {
	background: rgba(0, 0, 0, 0) none repeat scroll 0 0 !important;
}

.caret {
	color: #fff !important;
}

.cq_row_parent .input-group-btn {
	width: 200px;
}

.dropdown-menu input {
	display: inline !important;
	width: auto !important;
}

.advancee_menu ul {
	top: 100% !important;
	left: 0 !important;
}

.dropdown-menu .checkbox {
	display: inline-block !important;
	margin: 0 !important;
	padding: 0 !important;
	position: relative !important;
}

.dropdown-menu .checkbox input[type="checkbox"] {
	position: relative !important;
	margin-left: 0 !important;
}

.open>.dropdown-menu {
	padding-bottom: 17px;
	approver-id padding-top: 0;
}

#appUsersList tr:nth-child(odd) {
	background: #FFF
}

#appUsersList tr:nth-child(even) {
	background: #CCC
}
</style>
<script type="text/javascript">
	var assignedUsers = new Array();

	var allUsers = [];
	var leftUsers = new Array();

	$(document).ready(function() {
		$('#TeamMemberList option').each(function() {
			if ($(this).attr('value') != undefined && $(this).attr('value').length > 0 && $(this).attr('data-name') != undefined) {
				allUsers.push({
					userId : $(this).attr('value'),
					userName : $(this).attr('data-name'),
					teamMemberType : "Viewer"
				});
			}
		});
		$('#appUsersList tr').each(function() {
			if ($(this).attr('approver-id') != undefined && $(this).attr('approver-id').length > 0 && $(this).attr('data-username') != undefined && $(this).attr('approver-type') != undefined) {
				assignedUsers.push({
					userId : $(this).attr('approver-id'),
					userName : $(this).attr('data-username'),
					teamMemberType : $(this).attr('approver-type')
				});
			}
		});
		leftUsers = allUsers;
		console.log("load...leftUsers...", leftUsers)
	});

	$('.addTemplateTeamMembers').click(function(e) {
		//debugger
		var currentBlock = $(this);
		var userData = {
			userId : '',
			userName : '',
			teamMemberType : ''
		}

		var teamMemberType = $(this).closest('div').find('.access_check:checkbox:checked').val(); // $('.access_check:checkbox:checked').val();//
		// Viewer
		if (teamMemberType == undefined || teamMemberType == "") {
			teamMemberType = "Viewer";
		}
		console.log(">>> " + $("#TeamMemberList option:selected").val());
		var userId = $("#TeamMemberList option:selected").val();// currentBlock.parent().prev().find('select').val();
		var userName = $('#TeamMemberList option:selected').attr('data-name');

		var eventId = $('.event_form').find('#id').val();

		userData.userId = userId;
		userData.userName = userName;
		userData.teamMemberType = teamMemberType;

		var found = false;
		var tempArr = new Array();
		if (userData.userId != undefined && userData.userId.length > 0 && userData.userName != undefined) {
			if(!assignedUsers.some(e => e.userId == userData.userId)){
				assignedUsers.push({
					userId : userData.userId,
					userName : userData.userName,
					teamMemberType : userData.teamMemberType
				});
			}
		}

		if (userId == "") {

			$("#editor-err").removeClass("hide ");
			return;
		}

		if (userId == "" && ($('#teamMemberReadOnly').is(':checked'))) {
			$('#teamMember').parent().addClass('has-error').append('<span style="margin-left: 3%;"  class="help-block form-error">This is a required field</span>');
			return false;
		} else {
			$('#teamMember').parent().removeClass('has-error').find('.form-error').remove();
		}

		userList = userListForEvent(assignedUsers);
		$('#appUsersList').html("");
		$('#appUsersList').html(userList);
		$('#TeamMemberList').find('option[value="' + userId + '"]').remove();

		$('#TeamMemberList option').each(function() {
			if ($(this).attr('value') != undefined && $(this).attr('value').length > 0 && $(this).attr('data-name') != undefined) {
				tempArr.push({
					userId : $(this).attr('value'),
					userName : $(this).attr('data-name'),
					teamMemberType : "Viewer"
				});
			}
		});
		leftUsers = tempArr;
		$('#TeamMemberList').trigger("chosen:updated");
		$('#loading').hide();
		if ($('#eventTeamMembersList').length > 0) {
			$('#eventTeamMembersList').DataTable().ajax.reload();
		}
	})

	//remove approvers list
	$(document).delegate('.removeApproversList', 'click', function(e) {
		e.preventDefault();
		var currentBlock = $(this);
		var listType = currentBlock.attr('list-type');
		var listUserId = currentBlock.closest('tr').attr('approver-id');
		var listUserName = currentBlock.closest('tr').attr('data-username');

		var userName = currentBlock.parent().prev().find('p').text();

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
		$("#removeApproverListPopup").find('.approverInfoBlock').find('span:first-child').text(listUserName);
		$("#removeApproverListPopup").find('.approverInfoBlock').find('span:last-child').text(listType);
		$("#removeApproverListPopup").find('#approverListId').val(listUserId);
		$("#removeApproverListPopup").find('#approverListName').val(listUserName);
		$("#removeApproverListPopup").find('#approverListType').val(listType);
		$('.ui-dialog-title').text('Remove ' + listType);
	});

	function userListForEvent(data) {
		var userList = '';
		$.each(data, function(i, user) {

			console.log(user);

			userList += '<tr  data-username="' + user.userName + '" approver-id="' + user.userId + '">';
			userList += '<td class="width_50_fix "></td>';
			userList += '<td> <br>' + user.userName + '<span style="display: none;">' + user.userId + '</span>';
			userList += '<input type="hidden" path="teamMembers['+i+'].user.id" name="teamMembers['+i+'].user.id" value="' + user.userId + '" id="userId-'+i+'" /></td>';

			userList += '<td class="edit-drop"><div class="advancee_menu"><div class="adm_box">';
			if (user.teamMemberType == "Editor") {
				userList += '<input type="hidden" path="teamMembers['+i+'].teamMemberType" name="teamMembers['+i+'].teamMemberType" value="'+user.teamMemberType+'" id="memberType-'+i+'"/>';
				userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"> <i class="glyphicon glyphicon-pencil " aria-hidden="true " data-toggle="dropdown"  title="Editor"></i> </a>';
			} else if (user.teamMemberType == "Viewer") {
				userList += '<input type="hidden" path="teamMembers['+i+'].teamMemberType"  name="teamMembers['+i+'].teamMemberType" value="'+user.teamMemberType+'" id="memberType-'+i+'"/>';
				userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"> <i class="glyphicon glyphicon-eye-open" aria-hidden="true " data-toggle="dropdown"  title="Viewer"></i> </a>';
			} else if (user.teamMemberType == "Associate_Owner") {
				userList += '<input type="hidden" path="teamMembers['+i+'].teamMemberType" name="teamMembers['+i+'].teamMemberType"  value="'+user.teamMemberType+'" id="memberType-'+i+'"/>';
				userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"> <i class="fa fa-user-plus" aria-hidden="true " data-toggle="dropdown"  title="Associate Owner" ></i> </a>';
			}
			userList += '<ul class="dropdown-menu dropup"><li><a href="javascript:void(0);" class="small"><input ';
			if (user.teamMemberType == "Editor")
				userList += ' checked="checked"  ';
			userList += 'data-uid="' + user.userId + '" class="access_check" id="' + user.userId + '-Editor" type="checkbox" data-index="' + i + '" value="Editor">&nbsp;Editor</a> </li>';
			userList += '<li><a  href="javascript:void(0);" class="small"><input data-uid="' + user.userId + '" class="access_check" id="' + user.userId + '-Viewer" ';
			if (user.teamMemberType == "Viewer")
				userList += ' checked="checked"  ';
			userList += 'data-uid="' + user.userId + '" class="access_check" id="' + user.userId + '-Viewer" type="checkbox" data-index="' + i + '"  value="Viewer">&nbsp;Viewer</a> </li>';
			userList += '<li><a  href="javascript:void(0);" class="small"><input data-uid="' + user.userId + '" class="access_check" id="' + user.userId + '-Associate_Owner" ';
			if (user.teamMemberType == "Associate_Owner")
				userList += ' checked="checked"  ';
			userList += 'type="checkbox" data-index="' + i + '"  value="Associate_Owner">&nbsp;Associate Owner</a> </li>';
			userList += '</ul></li></ul></div></div></td><td>'
			userList += '<div class="cqa_del" data-toggle="dropdown" title="Delete">  <a href="#" list-type="Team Member" class="removeApproversList"></a> </div></td></tr>'

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
		var userName = $("#removeApproverListPopup").find('#approverListName').val();
		leftUsers.push({
			userId : userId,
			userName : userName,
			teamMemberType : "Viewer"
		});

		assignedUsers = [];

		userList = approverListForEventDropdown(leftUsers);
		$('#appUsersList tr[approver-id="' + userId + '"]').remove();

		$('#TeamMemberList').html(userList).trigger("chosen:updated");
		$('#appUsersList tr').each(function() {
			assignedUsers.push({
				userId : $(this).attr('approver-id'),
				userName : $(this).attr('data-username'),
				teamMemberType : "Viewer"
			});
		});
		// $('.row[approver-id="' + userId + '"]').remove();
		$("#removeApproverListPopup").dialog('close');
		// reorderApproverList();
		if ($('#eventTeamMembersList').length > 0) {
			$('#eventTeamMembersList').DataTable().ajax.reload();
		}
		if (assignedUsers == "" && ($('#teamMemberReadOnly').is(':checked'))) {
			$('#teamMember').parent().addClass('has-error').append('<span style="margin-left: 3%;"  class="help-block form-error">This is a required field</span>');
			return false;
		} else {
			$('#teamMember').parent().removeClass('has-error').find('.form-error').remove();
		}
		updateUserList('', $("#TeamMemberList"), 'NORMAL_USER');
	});

	function approverListForEventDropdown(data, listType) {
		var userList = '<option value="">Select Team Member</option>';
		$.each(data, function(i, user) {
			userList += '<option value="' + user.userId + '" data-name="' + user.userName + '">' + user.userName + '</option>';
		});
		return userList;
	}

	$(document).delegate('.access_check', 'click', function(e) {
		$('.access_check').prop('checked', false);
		$(this).prop('checked', true);
		tempId = $(this).attr("id");
		var arr = tempId.split("-");
		var index = $(this).attr('data-index');
		console.log("arr.......", arr);
		console.log("index.......", index);
		var listUserId = $('.removeApproversList').closest('tr').attr('approver-id');
		var listUserType = $('.removeApproversList').closest('tr').attr('approver-type');
		var listUserName = $('.removeApproversList').closest('tr').attr('data-username');
		selector = $("#" + tempId).closest("div").find(".dropdown-toggle");
		console.log(selector);
		var currentSelectedVal = arr[1];

		/* if ($(this).val() == "Editor") {
			selector.html("<i class='glyphicon glyphicon-pencil' aria-hidden='true '></i>");
		}
		if ($(this).val() == "Associate_Owner") {
			selector.html("<i class='fa fa-user-plus' aria-hidden='true '></i>");
		}
		if ($(this).val() == "Viewer") {
			selector.html("<i class='glyphicon glyphicon-eye-open' aria-hidden='true '></i>");
		} */

		$('#memberType-' + index).remove();

		if (currentSelectedVal == "Associate_Owner") {
			if (index != undefined) {
				var html = '';
				html += '<i class="fa fa-user-plus" aria-hidden="true" data-toggle="dropdown" title="Associate Owner"></i>';
				html += '<input type="hidden" path="teamMembers['+index+'].teamMemberType" value="'+arr[1]+'" name="teamMembers['+index+'].teamMemberType" id="memberType-'+index+'" />'
				selector.html(html);
			}
			if (index == undefined) {
				selector.html("<i class='fa fa-user-plus' aria-hidden='true '></i>");
			}
		}
		if (currentSelectedVal == "Viewer") {
			if (index != undefined) {
				var html = '';
				html += '<i class="glyphicon glyphicon-eye-open" aria-hidden="true" data-toggle="dropdown" title="Viewer"></i>';
				html += '<input type="hidden" path="teamMembers['+index+'].teamMemberType" value="'+arr[1]+'" name="teamMembers['+index+'].teamMemberType" id="memberType-'+index+'" />'
				selector.html(html);
			}
			if (index == undefined) {
				selector.html("<i class='glyphicon glyphicon-eye-open' aria-hidden='true '></i>");
			}
		}
		if (currentSelectedVal == "Editor") {
			if (index != undefined) {
				var html = '';
				html += '<i class="glyphicon glyphicon-pencil" aria-hidden="true" data-toggle="dropdown" title="Editor"></i>';
				html += '<input type="hidden" path="teamMembers['+index+'].teamMemberType" value="'+arr[1]+'" name="teamMembers['+index+'].teamMemberType" id="memberType-'+index+'" />'
				selector.html(html);
			}
			if (index == undefined) {
				selector.html("<i class='glyphicon glyphicon-pencil' aria-hidden='true '></i>");
			}
		}
		if ($(this).data('uid') == "" || $(this).data('uid') == undefined)
			return;

		/** ** Update ** */
		var memberType = $(this).val();
		var currentBlock = $(this);

		var userId = $(this).data('uid');
		var eventId = $('.event_form').find('#id').val();
		$('#loading').hide();
		if ($('#eventTeamMembersList').length > 0) {
			$('#eventTeamMembersList').DataTable().ajax.reload();
		}
	});

	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});
	$(document).on('click', '.toggle-button', function() {
		$(this).toggleClass('toggle-button-selected');
	});
	$(document).on('click', '.small', function() {
		$(this).find(".access_check").trigger("click");

	});
	function DropDown(el) {
		this.dd = el;
		this.placeholder = this.dd.children('span');
		this.opts = this.dd.find('ul.dropdown > li');
		this.val = '';
		this.index = -1;
		this.initEvents();
	}
	DropDown.prototype = {
		initEvents : function() {
			var obj = this;

			obj.dd.on('click', function(event) {
				$(this).toggleClass('active');
				return false;
			});

			obj.opts.on('click', function() {
				var opt = $(this);
				obj.val = opt.text();
				obj.index = opt.index();
				obj.placeholder.text(obj.val);
			});
		},
		getValue : function() {
			return this.val;
		},
		getIndex : function() {
			return this.index;
		}
	}

	$(function() {

		var dd = new DropDown($('#dd'));

		$(document).click(function() {
			// all dropdowns
			$('.wrapper-dropdown-3').removeClass('active');
		});

	});

	$(document).ready(function() {
		$("#teamMemberReadOnly").click(function() {
			$('#teamMember').parent().removeClass('has-error').find('.form-error').remove();
			if ($(this).prop('checked')) {
				if (assignedUsers == '') {
					$(this).prop('checked', false);
					$('#teamMember').parent().addClass('has-error').append('<span style="margin-left: 3%;"  class="help-block form-error">This is a required field</span>');

					return false;
				}
				$.uniform.update();
			}
		});
	});
</script>