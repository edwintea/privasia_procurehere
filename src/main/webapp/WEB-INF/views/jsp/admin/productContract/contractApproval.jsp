<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.id" var="loggedInUserId" />
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ROLE_CONTRACT_EDIT')" var="canEdit" />
<div class="dynamic-approval col-md-12">
	<div class="form_field" id="idReminderSettings">
		<div class="row">
			<div class="col-md-11  ${contractExpire || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' || buyerReadOnlyAdmin ? 'disabled':''}" >
				<form:checkbox path="enableApproval" id="idEnableApproval" class="custom-checkbox" />
			    <label style="margin-left: 18px;"><spring:message code="enable.contract.approval" /></label>
			</div>
		</div>
		<div class="row approval_reminder ${!productContract.enableApproval ? 'disabled' : '' }" >
			<div class="col-md-11 ${contractExpire || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' || buyerReadOnlyAdmin ? 'disabled':''}" >
				<form:checkbox path="enableApprovalReminder" id="idEnableApprovalReminder" class="custom-checkbox" />
			    <label style="margin-left: 18px;"><spring:message code="enable.approval.reminder" /></label>
			</div>
		</div>
		<div class="form_field col-md-3 col-sm-4" id="approvalReminderHourDiv" style="padding-top: 8px; padding-left: 7px;">
		  <div class="row form_field" style="margin-bottom: 0px !important; ">
			<label class="col-sm-3 control-label wd-cl" style="margin-top: 3px;"><spring:message code="approval.reminder.hour" /></label>
			 <div class="col-sm-3 pd-wd  ${contractExpire || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' || buyerReadOnlyAdmin ? 'disabled':''}">
			  <form:input path="reminderAfterHour" id="apprvlReminderHour" style="width: 80px;" data-validation="required number" data-validation-allowing="range[1;99]" data-validation-error-msg-container="#hour-error-dialog" autocomplete="off" value="${reminderAfterHour}" class="form-control autoSave" placeholder="e.g. 24" data-sanitize="numberFormat" maxLength="2" />
 			 </div>
			<label class="control-label" style="margin-top: 3px; margin-left: 13px;"><spring:message	code="approval.hour" /></label>
		  </div>
		   <div id="hour-error-dialog" style="margin-left: 190px;"></div>
		  <div class="row form_field" style="margin-bottom: 0px !important; ">	
			<label class="col-sm-3 control-label mr-tp-wd" style="margin-top: 10px;"><spring:message code="approval.reminder.count" /></label>
			<div class="col-sm-3 pd-wd  ${contractExpire || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' || buyerReadOnlyAdmin ? 'disabled':''}">
 			  <form:input path="reminderCount" id="apprvlReminderCount" style="width: 80px;" data-validation="required number" data-validation-allowing="range[1;99]" data-validation-error-msg-container="#count-error-dialog" autocomplete="off" value="${reminderCount}" class="form-control autoSave mr-tp" placeholder="e.g. 3" data-sanitize="numberFormat" maxLength="2" />
			</div>
			<label class="col-sm-remi control-label" style="margin-top: 10px;"><spring:message code="reminder.emails" /></label>
		  
		  <div style="margin-top: 16px;" class=" ${contractExpire || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' || buyerReadOnlyAdmin ? 'disabled':''}">
				<form:checkbox path="notifyEventOwner" id="idNotifyEventOwner" class="custom-checkbox" />
			    <label style="margin-left: 19px;"><spring:message code="contract.notify.event.owner" /></label>
	   	 </div>
		
		</div>
		  <div id="count-error-dialog" style="margin-left: 75px;"></div>
		</div>
	</div>
	<div class="row cloneready" style="${!productContract.enableApproval ? 'display:none' : ''}">
		<div class="col-md-3 col-sm-4 ">
			<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out ${ productContract.isEditable ? ( contractExpire || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' || buyerReadOnlyAdmin ? 'disabled':'') : ''}" id="addSelect" data-placement="top">
				<spring:message code="application.addapproval.button" />
			</button>
		</div>
	</div>
	<!-- For LOOP -->
	<c:set var="doneUsers" value="" />
	<c:set var="doneLevel" value="" />
	<div existing-approvers="${loggedInUserId}"></div>	
	<div class="approVAlRouteBox" style="max-height: 300px; min-height: 10px;">
		<c:if test="${productContract.enableApproval}">
			<c:forEach items="${productContract.approvals}" var="approval" varStatus="status">
				<div id="new-approval-${status.index + 1}" class="row new-approval">
					<c:if test="${approval.active}">
						<c:forEach items="${productContract.approvals[status.index].approvalUsers}" var="usr">
							<c:if test="${usr.approvalStatus != 'PENDING' }">
								<c:set var="doneUsers" value="${doneUsers.concat(\"'\").concat(usr.user.id).concat(\"',\") }" />
								<c:set var="doneLevel" value="${productContract.approvals[status.index].id}" />
							</c:if>
						</c:forEach>
					</c:if>
	
					<div class="col-md-2 col-xs-12 col-sm-3">
						<label class="level mb-0"> <spring:message code="rfi.createrfi.level" /> ${status.index + 1}
						</label>
					</div>
					<div class="col-md-5 col-sm-7 col-xs-8 approval-chosen-multiselect ${buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' || approval.done || contractExpire ? 'disabled' : ''}">
						<form:hidden class="approval_id_hidden" path="approvals[${status.index}].id" />
						<spring:message code="createrfi.approvalroute.approvers.placeholder" var="approvers" />
						<span class="dropUp">
						<form:select data-validation="required" id="multipleSelectExample-${status.index}" path="approvals[${status.index}].approvalUsers" cssClass="tagTypeMultiSelect user-list-all chosen-select  ${approval.done ? 'disabled':''}"
							 multiple="multiple" selected-id="existing-approvers" >
							<c:forEach items="${userList}" var="usr">
								<c:if test="${usr.id == '-1' }">
									<form:option value="-1" label="${usr.user.name}" disabled="true" />
								</c:if>
								<c:if test="${usr.id != '-1' }">
									<form:option value="${usr.id}" label="${usr.user.name}" />
								</c:if>
							</c:forEach>
						</form:select>
						</span>
					</div>
	
					<div class="col-sm-12 col-md-3 col-xs-4">
						<div class="btn-address-field pt-0">
							<button type="button" class="btn dropdown-toggle  ${ (approval.active || approval.done || contractExpire || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' || buyerReadOnlyAdmin ) ? 'disabled':''}" data-toggle="dropdown">
								<i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
							</button>
							<ul class="dropdown-menu dropup">
								<li><a href="javascript:void(0);" class="small" tabIndex="-1">
										<spring:message code="application.any" var="any" />
										<form:checkbox path="approvals[${status.index}].approvalType" value="OR" cssClass="approval_condition" label="${any}"></form:checkbox>
									</a></li>
								<li><a href="javascript:void(0);" class="small " tabIndex="-1">
										<spring:message code="buyercreation.all" var="all" />
										<form:checkbox path="approvals[${status.index}].approvalType" value="AND" cssClass="approval_condition" label="${all}"></form:checkbox>
									</a></li>
							</ul>
							<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval  ${approval.active || approval.done || contractExpire || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' || buyerReadOnlyAdmin ? 'disabled':''}"
							${approval.active || approval.done ? 'disabled="disabled"':''}>
								<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
							</button>
						</div>
					</div>
				</div>
			</c:forEach>
		</c:if>
		
	</div>
</div>
<style>
.mb-0 {
	margin-bottom: 0 !important;
}
.chosen-select {
    display: contents !important;
    }
.pt-0 {
	padding-top: 1px !important;
	padding-left: 10px;
}

.select2-container-multi {
	margin-top: 10px !important;
}

#prSummaryForm .approVAlRouteBox {
/* 	max-height: 300px;
	overflow-y: auto;
	overflow-x: hidden;
 */}

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

</style>
<script type="text/JavaScript">
	var index = 0;
	index = ${fn:length(productContract.approvals) + 1};
	$(document).ready(function() {
		
		$('#page-content').find('.approVAlRouteBox select.chosen-select.disabled').each(function() {
	    	$(this).parent().find('.chosen-search-input').attr("disabled", "disabled"); 
		});
		
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
			template += '<input type="hidden" class="approval_id_hidden" name="approvals[' + (index - 1) + '].id" value="" />';
			template += '<div class="col-md-2 col-sm-3"><label class="level mb-0"><spring:message code="rfi.createrfi.level"/> ' + index + '</label></div>';
			template += '<div class="col-md-5 col-sm-7 col-xs-8 approval-chosen-multiselect" id="sel">';
			template += '<span class="dropUp"><select data-validation="required" selected-id="existing-approvers" id="multipleSelectExample-'+(index - 1)+'" name="approvals[' + (index - 1) + '].approvalUsers" class="user-list-all chosen-select tagTypeMultiSelect" data-placeholder=<spring:message code="createrfi.approvalroute.approvers.placeholder"/>  multiple>';
			<c:forEach items="${userList}" var="users">
				<c:if test="${users.id == '-1' }">
					template += '<option value="-1" disabled >${users.user.name}</option>';
				</c:if>
				<c:if test="${users.id != '-1' }">
					template += '<option value="${users.id}" >${users.user.name}</option>';
				</c:if>				
			</c:forEach> 
			template += '</select></span></div><div class="col-sm-12 col-md-3 col-xs-4">';
			template += '<div class="btn-address-field pt-0"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
			template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
			template += '<ul class="dropdown-menu dropup">';
			template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="approvals[' + (index - 1) + '].approvalType" checked="checked" value="OR" class="approval_condition" type="checkbox"/><label>Any</label></a></li>';
			template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="approvals[' + (index - 1) + '].approvalType" value="AND" class="approval_condition"  type="checkbox"/><label>All</label></a></li>	</ul>';
			template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
			template += '</div>';

			$(".dynamic-approval").find('.approVAlRouteBox').append(template);
			//for dynamic validation
			$('#multipleSelectExample-'+(index - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });
			updateUserList('',$('#multipleSelectExample-'+(index - 1)),'ALL_USER');
			$(".approVAlRouteBox").animate({ scrollTop: $(".approVAlRouteBox")[0].scrollHeight}, 1000);
			index++;
			console.log("Index After Add : " , index);
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
		    	$('#apprvlReminderHour').val('');
		    	$('#apprvlReminderCount').val('');
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
			/* $('select').select2(); */
		}

	}
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js?1"/>"></script>
