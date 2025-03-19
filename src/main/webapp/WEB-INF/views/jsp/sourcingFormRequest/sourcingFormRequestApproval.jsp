<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<h4>
	<spring:message code="rfi.createrfi.approvalroute.label.2" /> :
	<c:if test="${sourcingFormRequest.approvalsCount !=null and sourcingFormRequest.approvalsCount !=0}">
	(Add Minimum ${sourcingFormRequest.approvalsCount} Approval)
	</c:if>
</h4>
<div class="dynamic-approval col-md-10">
	<div class="form_field">
		<div class="form_field row">
			<div class="col-sm-11 col-md-11 col-xs-11">
				<form:checkbox path="enableApprovalReminder" id="idEnableApprovalReminder" class="custom-checkbox" />
			    <label class="col-sm-11 col-md-11 col-xs-11 control-label" style="margin: inherit;"><spring:message code="enable.approval.reminder" /></label>
			</div>
		</div>
		<div class="form_field col-md-3 col-sm-4" id="approvalReminderHourDiv" style="padding-left: 18px;">
		  <div class="row form_field">
			<label class="col-sm-3 control-label wd-cl" style="margin-top: 11px;"><spring:message code="approval.reminder.hour" /></label>
			 <div class="col-sm-3 pd-wd">
			  <form:input path="reminderAfterHour" id="apprvlReminderHour" style="width: 80px;" data-validation="required number" data-validation-allowing="range[1;99]" data-validation-error-msg-container="#hour-error-dialog" autocomplete="off" value="${reminderAfterHour}" class="form-control autoSave" placeholder="e.g. 24" data-sanitize="numberFormat" maxLength="2" />
			 </div>
			<label class="col-sm-3 control-label" style="margin-top: 11px;"><spring:message code="approval.hour" /></label>
		  </div>
			 <div id="hour-error-dialog" style="margin-left: 175px;"></div>
		  <div class="row form_field">	
			<label class="col-sm-3 control-label mr-tp-wd" style="margin-top: 16px;"><spring:message code="approval.reminder.count" /></label>
			<div class="col-sm-3 pd-wd">
			  <form:input path="reminderCount" id="apprvlReminderCount" style="width: 80px;" data-validation="required number" data-validation-allowing="range[1;99]" data-validation-error-msg-container="#count-error-dialog" autocomplete="off" value="${reminderCount}" class="form-control autoSave mr-tp" placeholder="e.g. 3" data-sanitize="numberFormat" maxLength="2" />
			</div>
			<label class="col-sm-remi control-label" style="margin-top: 16px;"><spring:message code="reminder.emails" /></label>
			
			  <div style="margin-top: 16px;">
				<form:checkbox path="notifyEventOwner" id="idNotifyEventOwner" class="custom-checkbox" />
			    <label class="notify-change col-sm-remi control-label"><spring:message code="notify.event.owner" /></label>
	   	      </div>
	   	 
			</div>
			<div id="count-error-dialog" style="margin-left: 65px;"></div>
		</div>
	</div>
	<div class="form_field">
		<div>
		<div class="col-sm-11 col-md-11 col-xs-11">
				<form:checkbox path="addAdditionalApprovals" cssClass="custom-checkbox autoSave" />
				<label class="col-sm-11 col-md-11 col-xs-11 control-label">Allow to Add Additional Approval</label>
			</div>
		</div>
	</div>

	<div class="row cloneready">
		<div class="col-md-3 col-sm-4">
			<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="addSelect" data-placement="top">Add Approval Level</button>
		</div>
	</div>
	<!-- For LOOP -->
	<c:set var="doneUsers" value="" />
	<c:set var="doneLevel" value="" />
	<div class="approVAlRouteBox">
		<c:forEach items="${sourcingFormRequest.sourcingFormApprovalRequests}" var="approval" varStatus="status">
			<div id="new-approval-${status.index + 1}" class="row new-approval">
				<c:if test="${approval.active}">
					<c:forEach items="${sourcingFormRequest.sourcingFormApprovalRequests[status.index].approvalUsersRequest}" var="usr">
						<c:if test="${usr.approvalStatus != 'PENDING' }">
							<c:set var="doneUsers" value="${doneUsers.concat(\"'\").concat(usr.user.id).concat(\"',\") }" />
							<c:set var="doneLevel" value="${sourcingFormRequest.sourcingFormApprovalRequests[status.index].id}" />
						</c:if>
					</c:forEach>

				</c:if>
				<div class="col-md-2 col-xs-12 col-sm-3">
					<label class="level"> Level ${status.index + 1} </label>
				</div>
				<div class="col-md-7 col-sm-7 col-xs-8 ${buyerReadOnlyAdmin ? 'disabled' : ''} ${approval.done ? 'disabled':''}">
					<form:hidden class="approval_id_hidden" path="sourcingFormApprovalRequests[${status.index}].id" />
					<span class="dropUp">
					<form:select autocomplete="off" data-validation="required" id="multipleSelectExample-${status.index}" path="sourcingFormApprovalRequests[${status.index}].approvalUsersRequest" cssClass="user-list-all chosen-select tagTypeMultiSelect ${approval.done ? 'disabled':''}" data-placeholder="Approvers" multiple="multiple">
						<c:forEach items="${userList1}" var="usr" >
							<c:if test="${usr.id == '-1' }">
								<form:option value="-1" label="${usr.name}" disabled="true" />
							</c:if>
							<c:if test="${usr.id != '-1' }">
								<form:option value="${usr.id}" label="${usr.name}" />
							</c:if>
						</c:forEach>						
					</form:select>
					</span>
				</div>
				<div class="col-sm-2 col-md-3 col-xs-3 pad0">
					<div class="btn-address-field pt-0">
						<button type="button" class="btn dropdown-toggle  ${ (approval.active || approval.done) ? 'disabled':''}" data-toggle="dropdown">
							<i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
						</button>
						<ul class="dropdown-menu dropup">
							<li><a href="javascript:void(0);" class="small" tabIndex="-1"> <form:checkbox path="sourcingFormApprovalRequests[${status.index}].approvalType" value="OR" cssClass="approval_condition" label="Any"></form:checkbox>
							</a></li>
							<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <form:checkbox path="sourcingFormApprovalRequests[${status.index}].approvalType" value="AND" cssClass="approval_condition" label="All"></form:checkbox>
							</a></li>
						</ul>
						<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval  ${(approval.active || approval.done) ? 'disabled':''}">
							<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
						</button>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</div>


<script type="text/JavaScript">
	var index = 0;
	index = ${fn:length(sourcingFormRequest.sourcingFormApprovalRequests) + 1};
	$(document).ready(function() {
		
		$(document).delegate('.small', 'click', function(e) {
			//console.log($(this).find(".approval_condition"));
			$(this).find(".approval_condition").trigger("click");	
		});

		//.collapse('toggle')
		/* Prevent To Remove In Edit Mode */
		var lockedLevel = '${doneLevel}';
		var lockedElement = [${!empty doneUsers ? doneUsers : ''} ''];
		$.each(lockedElement, function(i, item){
			$(".tagTypeMultiSelect").find('option[selected="selected"][value="'+item+'"]').attr('data-locked',true);			
		});
		/* Prevent To Remove In Edit Mode */
		/* $(".tagTypeMultiSelect").select2(); */
		
		document.getElementById("addSelect").addEventListener("click", function(e) {
			e.preventDefault();
			var template = '<div id="new-approval-'+index+'" class="row new-approval">';
			template += '<input type="hidden" class="approval_id_hidden" name="sourcingFormApprovalRequests[' + (index - 1) + '].id" value="" />';
			template += '<div class="col-md-2 col-sm-3"><label class="level">Level ' + index + '</label></div>';
			template += '<div class="col-md-7 col-sm-7" id="sel">';
			template += '<span class="dropUp"><select data-validation="required" autocomplete="off" id="multipleSelectExample-'+(index - 1)+'" name="sourcingFormApprovalRequests[' + (index - 1) + '].approvalUsersRequest" class="user-list-all chosen-select tagTypeMultiSelect" data-placeholder="Approvers" multiple>';
			<c:forEach items="${userList}" var="users">
			<c:if test="${users.user.id != '-1' }">
				template += '<option value="${users.id}" >${users.user.name}</option>';
			</c:if>	
			<c:if test="${users.user.id == '-1' }">
				template += '<option value="-1" disabled>${users.user.name}</option>';
			</c:if>			
			</c:forEach> 
			template += '</select></span></div><div class="col-sm-2 col-md-2 pad0">';
			template += '<div class="btn-address-field pt-0"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
			template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
			template += '<ul class="dropdown-menu dropup">';
			template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="sourcingFormApprovalRequests[' + (index - 1) + '].approvalType" checked="checked" value="OR" class="approval_condition" type="checkbox"/><label>Any</label></a></li>';
			template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="sourcingFormApprovalRequests[' + (index - 1) + '].approvalType" value="AND" class="approval_condition"  type="checkbox"/><label>All</label></a></li>	</ul>';
			template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
			template += '</div>';

			$(".dynamic-approval").find('.approVAlRouteBox').append(template);
			$('#multipleSelectExample-'+(index - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });

			updateUserList('',$('#multipleSelectExample-'+(index - 1)),'ALL_USER'); //inital add users in drop down 
			$(".approVAlRouteBox").animate({ scrollTop: $(".approVAlRouteBox")[0].scrollHeight}, 1000);
			index++;
		});

		$(document).delegate('.removeApproval', 'click', function(e) {
			$(this).closest(".new-approval").remove();
			$(this).closest(".approval_id_hidden").remove();	

			$(".new-approval").each(function(i, v) {
				i++;
				$(this).attr("id", "new-approval-" + i);
				$(this).find(".level").text('Level ' + i);

				
				$(this).find(".approval_id_hidden").each(function(){					
					$(this).attr("name",'sourcingFormApprovalRequests[' +(i-1) + '].id');					
				});
				
				$(this).find(".approval_condition").each(function(){
					$(this).attr("name",'sourcingFormApprovalRequests[' +(i-1) + '].approvalType');
				}); // checkbox name reindex
				
				$(this).find("input[name='_sourcingFormApprovalRequests[" + i + "].approvalType']").each(function(){
					$(this).attr('name','_sourcingFormApprovalRequests[' +(i-1) + '].approvalType');
				}); //Checkbox hidden val reindex
				
				
				$(this).find(".tagTypeMultiSelect").each(function(){
					$(this).attr("name",'sourcingFormApprovalRequests[' +(i-1) + '].approvalUsersRequest');
					$(this).attr("id", "multipleSelectExample-" + ((i-1)) + "");					
				}) //select name reindex
				
				$(this).find("input[name='_sourcingFormApprovalRequests[" + i + "].approvalUsersRequest']").each(function(){
					$(this).attr("name",'_sourcingFormApprovalRequests[' +(i-1) + '].approvalUsersRequest');
				}) //select name reindex
				
			});
			index--;
		});

		$(document).delegate('.approval_condition', 'click', function(e) {
		
			$(this).closest('.btn-address-field').find(".approval_condition").each(function(){
				$(this).prop('checked', false);
		      	//console.log($(this))
			});
		  
			$(this).prop('checked', true);
		 
			var current_val = $(this).val();
		
			if (current_val == "OR") {
				//console.log(current_val);
				$(this).closest('.btn-address-field').find(".dropdown-toggle").html('<i class="fa fa-user" aria-hidden="true"></i>');
			}
			if (current_val == "AND") {
				//console.log(current_val);
				$(this).closest('.btn-address-field').find(".dropdown-toggle").html('<i class="fa fa-users" aria-hidden="true"></i>');
			}
		});
		
		if(document.getElementById('idEnableApprovalReminder') != null){
			if(document.getElementById('idEnableApprovalReminder').checked) {
				$('#approvalReminderHourDiv').show();
			}else{
				$('#approvalReminderHourDiv').hide();
			} 
		}
		$("#idEnableApprovalReminder").change(function() {
		    if(this.checked) {
		    	$('#approvalReminderHourDiv').show();
		    	$("#idNotifyEventOwner").prop("checked", true);
		    }else{
		    	$('#approvalReminderHourDiv').hide();
		    	$("#idNotifyEventOwner").prop("checked", false);
		    }
		    $.uniform.update();
		});

	});
	
	

	//Create a dynamic Select
	function createSelect() {

		var sel = document.getElementById("sel");
		//alert("hello");
		//Create array of options to be added
		var array = [ "Steven White", "Nancy King", "Nancy Davolio", "Michael Leverling", "Michael Suyama" ];

		//Create and append select list
		var selectList = document.createElement("select");
		selectList.setAttribute("id", "mySelect");
		selectList.setAttribute("multiple", "");
		selectList.setAttribute("placeholder", "Approvers");

		sel.appendChild(selectList);
		$(".ApprovalOption").first().clone().appendTo(sel);
		//Create and append the options
		for (var i = 0; i < array.length; i++) {
			var option = document.createElement("option");
			option.setAttribute("value", array[i]);
			option.text = array[i];
			selectList.appendChild(option);
			$('select').select2();
		}

	}
	
	$.validate({
		  inlineErrorMessageCallback: function($input, errorMessage, config) {
		     // Return an element that should contain the error message.
		     // This callback will called when validateOnBlur is set to true (default) and/or errorMessagePosition is set to 'inline'
		  },
		  submitErrorMessageCallback: function($form, errorMessages, config) {
		    // Return an element that should contain all error messages.
		    // This callback will be called when errorMessagePosition is set to 'top'
		  }
		});
</script>


<%-- <div id="apprTab" class="pad_all_15 collapse in float-left width-100 position-relative in">
			<c:forEach items="${sourcingFormRequest.sourcingFormApprovalRequests}" var="approval">
				<div class="pad_all_15 float-left appr-div position-relative">
					<label>Level ${approval.level}</label>
					<c:if test="${approval.active}">
						<div class="color-green marg-left-10">
							&nbsp;&nbsp;&nbsp; <i class="fa fa-arrow-left color-green" aria-hidden="true"></i>&nbsp;&nbsp;[Active]
						</div>
					</c:if>
					<div class="Approval-lavel1-upper">
						<c:forEach items="${approval.approvalUsersRequest}" var="user" varStatus="status">
							<div
								class="Approval-lavel1 ${user.approvalStatus eq 'REJECTED' ? 'cross-red-mark' : ''} ${user.approvalStatus eq 'APPROVED' ? 'right-green-mark' : ''}">${user.user.name}
								<c:if test="${!user.user.active}">
										[<span class="inactiveCaption">INACTIVE</span>]
									</c:if>
							</div>
							<c:if test="${fn:length(approval.approvalUsersRequest) > (status.index + 1)}">
								<span class="or-seg">${approval.approvalType}</span>
							</c:if>
						</c:forEach>
					</div>
				</div>
		</c:forEach>
	</div>  --%>
<style>

.pt-0 {
	padding-top: 1px !important;
	padding-left: 10px;
}

.select2-container-multi {
	margin-top: 10px !important;
}

#prSummaryForm .approVAlRouteBox {
	max-height: 300px;
	overflow-y: auto;
	overflow-x: hidden;
}

.row.dynamic-approval {
	margin-top: 10px;
}

.new-approval label {
	/* float: right; */
	padding: 15px 0;
}

.btn-address-field {
	padding-top: 10px;
	margin-left: -15px;
}

.removeApproval {
	margin-left: 10px;
}

.cloneready {
	margin-bottom: 10px;
}

.collapseable  .meeting2-heading .checkbox-primary label {
	color: #636363 !important;
	float: left;
	font-size: 14px;
	margin-bottom: 15px;
	padding: 20px 0 18px 15px !important;
	width: 100%;
}

.checkbox.inline.no_indent input {
	margin-left: 10px;
	padding-top: 0;
}

.dropdown-menu a.small label {
	display: inline-block;
	float: right;
	padding: 2px 10px;
	position: absolute;
}

.mr-tp {
   margin-top: 7px;
}

.wd-cl {
    width: 207px;
}

.pd-wd {
    width: 80px;
    padding: inherit;
}

.mr-tp-wd {
    width: 95px;
    margin-top: 7px;
}
.col-sm-remi {
    position: relative;
    min-height: 1px;
    padding-right: 15px;
    padding-left: 15px;
}
@media (min-width: 768px)
.notify-change{
    width: 25%;
}
</style>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
