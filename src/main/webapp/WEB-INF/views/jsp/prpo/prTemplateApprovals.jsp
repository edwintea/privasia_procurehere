<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="Invited-Supplier-List import-supplier white-bg">
	<div class="meeting2-heading">
		<h3><spring:message code="rfi.createrfi.approvalroute.label" /></h3>
	</div>


	<div id="apprTab" class="pad_all_15 collapse in float-leftwidth-100position-relative">
		<div class="dynamic-approval form-group">
			<div class="row marg-bottom-10 pad_all_20">
				<div class="align-left col-md-5"></div>
				<div class="col-md-9 ">
					<div class="check-wrapper first">
						<spring:message code="prtemplate.label.visible" var="visible" />
						<form:checkbox path="approvalVisible" class="custom-checkbox approvalCheck" title="${visible}" label="${visible}" />
					</div>
					<div class="check-wrapper">
						<spring:message code="prtemplate.label.read.only" var="read" />
						<form:checkbox path="approvalReadOnly" class="custom-checkbox readOnlyCheck" title="${read}" label="${read}" />
					</div>
					<div class="check-wrapper" style="display:none">
						<spring:message code="prtemplate.label.read.only" var="read" />
						<form:checkbox path="approvalOptional" class="custom-checkbox optionalCheck" title="${read}" label="${read}" />
					</div>
					<div class="form_field  ${isTemplateUsed ? 'disabled':''}">
						<div class="approvalcount row">
							<div class="col-md-5">
								<label class="control-label"> <spring:message code="prtemplate.label.minimum" />
								</label>
							</div>

							<div class="col-sm-3 col-md-5 col-xs-5">
								<div class="input-prepend input-group">
									<spring:message code="sourcingForm.approval.place.approvalCount" var="approversCount" />
									<form:input path="minimumApprovalCount" autocomplete="off" id="minimumApprovalCount" placeholder="Enter Approval Count" class="form-control autoSave" type="number" min="0" max="9" />
									<span class="error" id="minimumCountError"></span>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row cloneready marg-bottom-10 pad_all_20">
					<div class="align-left col-md-5">
						<!--<label class="li-32 marg-none app-la">Add Approvers</label> -->
					</div>
					<div class="col-md-9">
						<spring:message code="tooltip.add.approval.level" var="addapproval" />
						<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="addSelect" data-toggle="tooltip" data-placement="top" title="${addapproval}">
							<spring:message code="application.addapproval.button" />
						</button>
						<span class="error" id="minimumCountError"></span> <!-- Error message span -->
					</div>
				</div>
				<!-- For LOOP -->
				<c:forEach items="${prTemplate.approvals}" var="approval" varStatus="status">
					<div id="new-approval-${status.index + 1}" class="row new-approval">
						<div class="align-right  col-md-3 ">
							<label class="level"><spring:message code="rfi.createrfi.level" /> ${status.index + 1}</label>
						</div>
						<div class="col-md-5 ${assignedCount > 0 ? 'disabled':''}">
							<form:select data-validation="required" id="multipleSelectExample-${status.index}" path="approvals[${status.index}].approvalUsers" cssClass="tagTypeMultiSelect user-list-all chosen-select  ${approval.done || (!empty prTemplate ? (tf:prApprovalReadonly(prTemplate)) : false) ? 'disabled':''}"
										 multiple="multiple">
								<c:forEach items="${userList}" var="usr">
									<c:if test="${usr.id == '-1' }">
										<form:option value="-1" label="${usr.user.name}" disabled="true" />
									</c:if>
									<c:if test="${usr.id != '-1' }">
										<form:option value="${usr.id}" label="${usr.user.name}" />
									</c:if>
								</c:forEach>
							</form:select>


						</div>
						<div class="col-md-4 pad0">
							<div class="btn-address-field pt-0">
								<button type="button" class="btn dropdown-toggle" data-toggle="dropdown">
									<i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
								</button>
								<ul class="dropdown-menu dropup mid-width">

									<li><a href="javascript:void(0);" class="small checkbox-check" tabIndex="-1">
										<spring:message code="application.any" var="any" />
										<form:checkbox path="approvals[${status.index}].approvalType" value="OR" cssClass="approval_condition" label="${any}"></form:checkbox>
									</a></li>

									<li><a href="javascript:void(0);" class="small checkbox-check" tabIndex="-1">
										<spring:message code="buyercreation.all" var="all"/>
										<form:checkbox path="approvals[${status.index}].approvalType" value="AND" cssClass="approval_condition" label="${all}"></form:checkbox>
									</a></li>
								</ul>
								<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval">
									<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
								</button>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>


<style>
	.pt-0 {
		padding-top: 1px !important;
		padding-left: 10px;
	}
	.select2-container-multi {
		margin-top: 10px !important;
	}
	.row.dynamic-approval {
		margin-top: 10px;
	}
	.new-approval label {
		float: right;
		padding: 15px;
	}
	.checkbox-check label{
		float: none;
		padding: 6px 3px 0;
		font-family: 'open_sansregular',"Helvetica Neue",Helvetica,Arial,sans-serif;
		margin-bottom: 0px !important;

	}
	.mid-width{
		min-width: 170px !important;
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
	.check-wrapper-minimum {
		margin-top: 15px; /* Space above the minimum approval count section */
	}
	.check-wrapper-minimum label {
		margin-bottom: 5px; /* Space below the label */
		margin-right: 130px;
	}
	.check-wrapper-minimum .custom-textbox approvalCheck {
		margin-top: 5px; /* Space between the label and the text field */
	}
	.error-message {
		color: red;
		font-weight: bold;
		display: block; /* Ensure it's visible */
		margin-top: 5px; /* Set the text color to red for error messages */
	}
</style>

<script type="text/JavaScript">
var index = ${fn:length(prTemplate.approvals) + 1};

function approvalSelect() {
	return '<div id="new-approval-'+index+'" class="row new-approval">' +
				'<div class="align-right  col-md-3 ">' +
					'<label class="level"><spring:message code="rfi.createrfi.level"/> ' + index + '</label>' +
				'</div>' +
				'<div class="col-md-5" id="sel">'+
					'<select ' +
						'data-validation="required" ' +
						'id="multipleSelectExample-'+(index - 1)+'" ' +
						'name="approvals[' + (index - 1) + '].approvalUsers" ' +
						'class="user-list-all chosen-select tagTypeMultiSelect" ' +
						'data-placeholder=<spring:message code="createrfi.approvalroute.approvers.placeholder" />  ' +
						'multiple>' +
						approvalOptions() +
					'</select>' +
				'</div>' +
			'<div class="col-md-4 pad0">' +
				'<div class="btn-address-field pt-0">' +
					'<button type="button" class="btn dropdown-toggle" data-toggle="dropdown">' +
						'<i class="fa fa-user" aria-hidden="true"></i>' +
					'</button>'+
					'<ul class="dropdown-menu dropup">'+
						'<li>' +
							'<a href="javascript:void(0);" class="small"  tabIndex="-1">' +
								'<input name="approvals[' + (index - 1) + '].approvalType" ' +
									'checked="checked" value="OR" ' +
									'class="approval_condition" type="checkbox"/>' +
										'&nbsp;<spring:message code="application.any" /> '+
							'</a>' +
						'</li>' +
						'<li>' +
							'<a href="javascript:void(0);" class="small " tabIndex="-1">' +
								'<input name="approvals[' + (index - 1) + '].approvalType" ' +
									'value="AND" class="approval_condition" ' +
									'type="checkbox"/>&nbsp;<spring:message code="buyercreation.all" /> '+
							'</a>' +
						'</li>	' +
					'</ul>' +
					'<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval"> ' +
						'<i class="glyphicon glyphicon-remove" aria-hidden="true"></i> ' +
					'</button>' +
				'</div>' +
			'</div>';
}

function approvalOptions() {
	let template = '';
	<c:forEach items="${templateApprovalUserList}" var="users">
	<c:if test="${users.id == '-1' }">
	template += '<option value="-1" disabled >${users.user.name}</option>';
	</c:if>

	<c:if test="${users.id != '-1' }">
	template += '<option value="${users.id}" >${users.user.name}</option>';
	</c:if>
	</c:forEach>
	return template
}

function addSelectDelegation() {
	$("#addSelect").click(function(e) {
		e.preventDefault();
		let template = approvalSelect();

		$(".dynamic-approval").append(template);
		$('#multipleSelectExample-'+(index - 1))
				.chosen({search_contains:true})
				.change(function() {
					$(this).validate();
					$(this).blur();
					$(this).parent()
							.find('.chosen-search-input')
							.attr('placeholder', '');
				});
		updateUserList('',$('#select#multipleSelectExample-'+(index - 1)),'ALL_USER');
		index++;
	});
}

function smallDelegation() {
	$(document).delegate('.small', 'click', function(e) {
		$(this).find(".approval_condition").trigger("click");
	});
}

function removeApprovalDelegation() {
	$(document).delegate('.removeApproval', 'click', function(e) {
		$(this).closest(".new-approval").remove();
		$(".new-approval").each(function(i, v) {
			i++;
			$(this).attr("id", "new-approval-" + i);
			$(this).find(".level").text('Level ' + i);
			$(this).find(".approval_condition").each(function(){
				$(this).attr("name",'approvals[' +(i-1) + '].approvalType');
			});
			$(this).find("input[name='_approvals[" + i + "].approvalType']").each(function(){
				$(this).attr('name','_approvals[' +(i-1) + '].approvalType');
			});
			$(this).find(".tagTypeMultiSelect").each(function(){
				$(this).attr("name",'approvals[' +(i-1) + '].approvalUsers');
				$(this).attr("id", "multipleSelectExample-" + ((i-1)) + "");
			});
			$(this).find("input[name='_approvals[" + i + "].approvalUsers']").each(function(){
				$(this).attr("name",'_approvals[' +(i-1) + '].approvalUsers');
			});
		});
		index--;
	});
}

function approvalConditionDelegation() {
	$(document).delegate('.approval_condition', 'click', function(e) {
		$(this).closest('.btn-address-field').find(".approval_condition").each(function(){
			$(this).prop('checked', false);
			//console.log($(this))
		});

		$(this).prop('checked', true);

		let current_val = $(this).val();
		let faClass = current_val === "AND" ? "fa fa-users" : "fa fa-user";
		$(this).closest('.btn-address-field').find(".dropdown-toggle")
				.html('<i class=' + faClass + ' aria-hidden="true"></i>');
	});
}

function toggleAddApprovalButtonPr() {
	var isVisible = $('.approvalCheck').is(':checked');
	var isReadOnly = $('.readOnlyCheck').is(':checked');

	// Enable or disable the Add Approval button and minimum approval count field based on the Visible checkbox
	$('#addSelect').prop('disabled', !isVisible);
	$('#minimumApprovalCount').prop('disabled', !isVisible);

	if (!isVisible) {
		$('#minimumCountError').text('');
		$('#minimumApprovalCount').removeClass('input-error');
		$('.new-approval').remove();
		isReadOnly = false;
		index = 1;
	}

	if (isVisible && $('#minimumApprovalCount').val() === '') {
		if(parseInt(countApproval) > 0){
			$('#minimumApprovalCount').val(countApproval);
		}else{
			$('#minimumApprovalCount').val();
		}
		$('#minimumCountError').text('');
		$('#minimumApprovalCount').removeClass('input-error');
	}

	// Hide elements if both checkboxes are either checked or unchecked
	if ((!isVisible && !isReadOnly) || (isVisible && isReadOnly)) {
		$('#minimumApprovalCount').hide();
		$('.control-label').hide();
		$('.new-approval').show();
	} else {
		$('#minimumApprovalCount').show();
		$('.control-label').show();
		$('.new-approval').show();
	}

	if (!isVisible && !isReadOnly) {
		$('#addSelect').prop('disabled', true);
	} else {
		$('#addSelect').prop('disabled', false);
	}
}

function validateApprovers() {
	var isValid = true;
	var errorMessage = '';

	// Iterate through each approval level
	$('.new-approval').each(function() {
		var level = $(this).find('.level').text().split(' ')[1];
		var selectedUsers = $(this).find('select.tagTypeMultiSelect').val() || [];

		// Check for duplicate users within the same level
		var uniqueUsers = new Set();
		var duplicateUsers = false;

		selectedUsers.forEach(function(user) {
			if (uniqueUsers.has(user)) {
				duplicateUsers = true;
			} else {
				uniqueUsers.add(user);
			}
		});

		if (duplicateUsers) {
			isValid = false;
			errorMessage = 'Duplicate users are not allowed within the same approval level.';
			$(this).find('.error-message').text(errorMessage);
			$(this).find('.error-message').addClass('input-error');
		} else {
			$(this).find('.error-message').text('');
			$(this).find('.error-message').removeClass('input-error');
		}
	});

	// Display error message if not valid
	if (!isValid) {
		$('#approverCountError').text(errorMessage);
		$('#approverCountError').addClass('input-error');
	} else {
		$('#approverCountError').text('');
		$('#approverCountError').removeClass('input-error');
	}

	console.log('Approver Validation Status:', isValid); // Debugging line
	return isValid;
}

// New validation function for minimum approval count and approvers
function validateMinimumApprovalAndApprovers() {
	var isValid = true;
	var errorMessage = '';

	// Check if minimum approval count is filled
	var minimumCount = parseInt($('#minimumApprovalCount').val()) || 0;
	console.log('Minimum Approval Count:', minimumCount);

	if (minimumCount > 0) {
		// Count how many approval levels are currently added
		var approvalLevelsCount = $('.new-approval').length;



		// Check if there are approvers selected in each level
		var approversSelected = $('.new-approval').find('select.tagTypeMultiSelect').toArray().every(function(select) {
			return $(select).val() && $(select).val().length > 0;
		});

		if (!approversSelected) {
			isValid = false;
			errorMessage = 'Please select approvers for all approval levels';
		}
	}

	// Display error message if not valid
	if (!isValid) {
		$('.error-message').html(errorMessage).show();
		$('#minimumApprovalCount').addClass('input-error');
	} else {
		$('.error-message').hide();
		$('#minimumApprovalCount').removeClass('input-error');
	}

	console.log('Validation Result:', isValid);
	return isValid;
}


function addApprovalButtonDelegation() {
	$('.approvalCheck, .readOnlyCheck').change(function() {
		toggleAddApprovalButtonPr();
	});
}

function documentDelegation() {
	$(document).on('change', '.new-approval', function() {
		validateApprovers();
	});
}

function formDelegation() {
	$('form').on('submit', function(e) {
		if ($('#minimumApprovalCount').val() === '') {
			$('#minimumApprovalCount').val('0');
		}
		if (!keepCountInRange() || !validateApprovers() || !validateMinimumApprovalAndApprovers()) {
			e.preventDefault(); // Stop form submission if validation fails
			console.log('Form submission prevented due to validation error.');
		}
	});
}

function keepCountInRange() {
	let selector = '#minimumApprovalCount';
	let approvalCount = parseInt($(selector).val());
	let min = 0;
	let max = 9;

	$('#minimumCountError').text('');
	$(selector).removeClass('input-error');

	if (approvalCount < min) {
		$(selector).val(min);
	} else if ((approvalCount > max) || (isNaN(approvalCount))) {
		$('#minimumCountError').text('The value must be a single digit (0-9).');
		$(selector).addClass('input-error');
		$(selector).val('');
		return false;
	}
	return true;
}

function minimumApprovalCountDelegation() {
	$('#minimumApprovalCount').on('change', function() {
		keepCountInRange();
	});
}

$(document).ready(function() {
	addSelectDelegation();
	smallDelegation();
	removeApprovalDelegation();
	approvalConditionDelegation();
	toggleAddApprovalButtonPr();
	minimumApprovalCountDelegation();
	addApprovalButtonDelegation();
	documentDelegation();
	formDelegation();
});
</script>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>