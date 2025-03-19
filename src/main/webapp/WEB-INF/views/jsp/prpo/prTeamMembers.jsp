<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<div class="form_field">
	<div class="form-group ">
		<label class="col-sm-4 col-md-3 col-xs-6 control-label"> </label>
		<div class="col-sm-5 col-md-5 col-xs-6">
			<div class="col-md-12">
				<div class="ia-invite-controls-area">
					<div class="group">
						<div class="input-group mrg15T mrg15B ${buyerReadOnlyAdmin ? 'disabled' : ''}">
							<select id="TeamMemberList" class="user-list-normal chosen-select" selected-id="approver-id" name="approverList1" cssClass="form-control chosen-select">
								<option value=""><spring:message code="prsummary.select.team.member" /></option>
								<c:forEach items="${prTeamMembers}" var="TeamMember">
									<c:if test="${TeamMember.id == '-1' }">
										<option value="-1" disabled="true">${TeamMember.name}</option>
									</c:if>
									<c:if test="${TeamMember.id != '-1' }">
										<option value="${TeamMember.id}">${TeamMember.name}</option>
									</c:if>
								</c:forEach>
							</select>
							<div class="input-group-btn">
								
								<ul class="dropdown-menu dropup">
									<li><a href="javascript:void(0);" class="small" tabIndex="-1"> <input id="access_editor" value="Editor" class="access_check" type="checkbox" /> &nbsp; <spring:message code="eventsummary.checkbox.editor" />
									</a></li>
									<li><a href="javascript:void(0);" class="small" tabIndex="-1"> <input id="access_viewer" value="Viewer" class="access_check" type="checkbox" /> &nbsp; <spring:message code="eventsummary.checkbox.viewer" />
									</a></li>
									<li><a href="javascript:void(0);" class="small" tabIndex="-1"> <input id="access_Associate_Owner" value="Associate_Owner" class="access_check" type="checkbox" /> &nbsp; <spring:message code="eventsummary.checkbox.associate.owner" />
									</a></li>
								</ul>
								<button class="btn btn-primary addTeamMemberToList" type="button">+</button>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="col-md-12 col-sm-12 col-xs-12">
				<div class="mem-tab">
					<table cellpading="0" cellspacing="0" width="100%" id="appUsersList" class="table-hover table">
						<c:forEach items="${pr.prTeamMembers}" var="teamMembers">
							<tr data-username="${teamMembers.user.name}" approver-id="${teamMembers.user.id}">
								<td class="width_50_fix "></td>
								<td>${teamMembers.user.name}<br> <span>${teamMembers.user.loginId}</span>
								</td>
								<td class="edit-drop">
									<div class="advancee_menu">
										
									</div>
								</td>
								<td>
									<div class="cqa_del">
										<a href="#" list-type="Team Member" class="removeApproversList " title='<spring:message code="tooltip.delete" />'> </a>
									</div>
								</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>


<!--Approver  popup-->
<div class="flagvisibility dialogBox" id="removeApproverListPopup" title='<spring:message code="tooltip.remove.team.member" />'>
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="approverListId" name="approverListId" value=""> <input type="hidden" id="approverListType" name="approverListType" value="">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock">
				<spring:message code="event.confirm.to.remove" />
				<span></span>
				<spring:message code="application.from" />
				<span></span>
				<spring:message code="application.list" />?
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeApproverListPerson" data-original-title="Delete"> <spring:message code="tooltip.delete" />
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

.box_active {
	background: rgba(0, 0, 0, 0) none repeat scroll 0 0 !important;
}

.caret {
	color: #fff !important;
}

/* #TeamMemberList_chosen a div {
	display: none;
} */
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
	padding-top: 0;
}

#appUsersList tr:nth-child(odd) {
	background: #FFF
}

#appUsersList tr:nth-child(even) {
	background: #CCC
}
</style>


<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});
	$(document).on('click', '.toggle-button', function() {
		$(this).toggleClass('toggle-button-selected');
	});
	//$(document).on('click', '.small', function() {
		//$(this).find(".access_check").trigger("click");

	//});

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
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
