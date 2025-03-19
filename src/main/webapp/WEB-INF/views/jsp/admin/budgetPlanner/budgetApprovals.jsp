<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="meeting2-heading">
	<h3>
		<spring:message code="rfi.createrfi.approvalroute.label" />
	</h3>
</div>

<div id="apprTab" class="pad_all_15 collapse in float-leftwidth-100position-relative">
	<div class="dynamic-approval form-group">
		<c:if test="${budgetPojo.budgetStatus eq 'PENDING' or budgetPojo.budgetStatus eq 'ACTIVE' or budgetPojo.budgetStatus eq 'APPROVED' or budgetPojo.budgetStatus eq 'EXPIRED' or budgetPojo.budgetStatus eq 'CANCELED'}">
			<div id="apprTab" class="pad_all_15 collapse in float-left width-100 position-relative in">
				<c:set var="currentBatch" value="0" />
				<c:forEach items="${budgetPojo.approvals}" var="approval">
					<c:if test="${approval.batchNo == 0}">
						<div class="pad_all_15 float-left appr-div position-relative">
							<label>Level ${approval.level}</label>
							<c:if test="${approval.active}">
								<div class="color-green marg-left-10">
									&nbsp;&nbsp;&nbsp; <i class="fa fa-arrow-left color-green" aria-hidden="true"></i>&nbsp;&nbsp;[Active]
								</div>
							</c:if>
							<div class="Approval-lavel1-upper">
								<c:forEach items="${approval.approvalUsers}" var="user" varStatus="status">
									<div class="Approval-lavel1 ${user.approvalStatus eq 'REJECTED' ? 'cross-red-mark' : ''} ${user.approvalStatus eq 'APPROVED' ? 'right-green-mark' : ''}">${user.user.name}
										<c:if test="${!user.user.active}">
										[<span class="inactiveCaption">INACTIVE</span>]
									</c:if>
									</div>
									<c:if test="${fn:length(approval.approvalUsers) > (status.index + 1)}">
										<span class="or-seg">${approval.approvalType}</span>
									</c:if>
								</c:forEach>
							</div>
						</div>
					</c:if>

					<c:if test="${approval.batchNo > 0}">
						<c:if test="${approval.batchNo != currentBatch}">
							<c:set var="currentBatch" value="${approval.batchNo}" />
							<div class="pad_all_15 float-left appr-div position-relative">
								<label>Additional Approval ${approval.batchNo}</label>
							</div>
						</c:if>
						<div class="pad_all_15 float-left appr-div position-relative">
							<label>Level ${approval.level}</label>
							<c:if test="${approval.active}">
								<div class="color-green marg-left-10">
									&nbsp;&nbsp;&nbsp; <i class="fa fa-arrow-left color-green" aria-hidden="true"></i>&nbsp;&nbsp;[Active]
								</div>
							</c:if>
							<div class="Approval-lavel1-upper">
								<c:forEach items="${approval.approvalUsers}" var="user" varStatus="status">
									<div class="Approval-lavel1 ${user.approvalStatus eq 'REJECTED' ? 'cross-red-mark' : ''} ${user.approvalStatus eq 'APPROVED' ? 'right-green-mark' : ''}">${user.user.name}
										<c:if test="${!user.user.active}">
										[<span class="inactiveCaption">INACTIVE</span>]
									</c:if>
									</div>
									<c:if test="${fn:length(approval.approvalUsers) > (status.index + 1)}">
										<span class="or-seg">${approval.approvalType}</span>
									</c:if>
								</c:forEach>
							</div>
						</div>
					</c:if>
				</c:forEach>
			</div>
		</c:if>


		<!-- For LOOP -->
		<c:if test="${(budgetPojo.budgetStatus eq 'NEW' or budgetPojo.budgetStatus eq 'DRAFT' or budgetPojo.budgetStatus eq 'REJECTED') or empty budgetPojo.id}">
			<div class="row pad_all_20">
				<div class="align-right col-md-3"></div>
				<div class="col-md-9 btn-add-approve-lavel">
					<spring:message code="tooltip.add.approval.level" var="addapproval" />
					<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="addSelect" data-toggle="tooltip" ${permissions.disabled ? 'disabled': ''} data-placement="top" title="${addapproval}">
						<spring:message code="application.addapproval.button" />
					</button>
				</div>
			</div>
			<c:forEach items="${budgetPojo.approvals}" var="approval" varStatus="status">
				<div id="new-approval-${status.index + 1}" class="row new-approval test">
					<div class="align-right col-md-3">
						<label class="level"><spring:message code="rfi.createrfi.level" /> ${status.index + 1}</label>
					</div>
					<div class="col-md-5">
						<form:select style="margin-left: 7px;" data-validation="required" id="multipleSelectExample-${status.index}" path="approvals[${status.index}].approvalUsers" cssClass="tagTypeMultiSelect  user-list-all chosen-select" data-placeholder="Approvers" multiple="multiple">
							<c:forEach items="${userList}" var="usr" >
							<c:if test="${usr.id == '-1' }">
								<form:option value="-1" label="${usr.name}" disabled="true" />
							</c:if>
							<c:if test="${usr.id != '-1' }">
								<form:option value="${usr.id}" label="${usr.name}" />
							</c:if>
						</c:forEach>
						</form:select>
					</div>
					<div class="col-md-4 pad0">
						<div  class="btn-address-field pt-0" style="position: relative;">
							<button type="button" class="btn dropdown-toggle" data-toggle="dropdown">
								<i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
							</button>
							<ul class="dropdown-menu dropup">
							<li><a href="javascript:void(0);" class="small" tabIndex="-1"> <form:checkbox path="approvals[${status.index}].approvalType" value="OR" cssClass="approval_condition" />&nbsp;Any
							</a></li>
							<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <form:checkbox path="approvals[${status.index}].approvalType" value="AND" cssClass="approval_condition" />&nbsp;All
							</a></li>
							</ul>
							<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval">
								<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
							</button>
						</div>
					</div>
				</div>
			</c:forEach>
		</c:if>
	</div>
</div>
<c:if test="${(budgetPojo.budgetStatus eq 'REJECTED')}">
	<div class="clear"></div>
	<c:if test="${not empty budgetPojo.budgetComment}">

		<div class=" clearfix event_info Approval-tab " style="margin-top: 0.5%; margin-bottom: 10px;">
			<div class="remark-tab pad0">
				<h4 class="test">
					<spring:message code="summarydetails.approval.comments" />
				</h4>
				<div class="pad_all_15 float-left width-100">
					<c:forEach items="${budgetPojo.budgetComment}" var="comment">
						<div class="Comments_inner ${comment.approved ? 'comment-right' : 'comment-wrong'}">

							<h3>${comment.createdBy.name}
								<span> <fmt:formatDate value="${comment.createdDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								</span>
							</h3>
							<div class="${comment.approved ? 'approve-div' : 'reject-div'}">${comment.comment}</div>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
	</c:if>
</c:if>
<style>
.lavel-txt {
	width: 540px !important;
}

@media ( min-width :900px) and (max-width:1600px) {
	.lavel-txt {
		width: 405px !important;
	}
}

@media ( min-width :768px) and (max-width:1366px) {
	.lavel-txt {
		width: 308px !important;
	}
}

.new-approval label {
	float: left !important;
}
.dropdown-toggle {
	/* width: 78.52px; */
	width: 40px;
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
</style>
<script type="text/JavaScript">
	var index;
	index = ${fn:length(budgetPojo.approvals) + 1};
	$(document).ready(function() {
		
		$(document).delegate('.small', 'click', function(e) {
			$(this).find(".approval_condition").trigger("click");	
		});

		//$(".tagTypeMultiSelect").select2();

		$("#addSelect").click(function(e) {
			e.preventDefault();
			var template = '<div id="new-approval-'+index+'" class="row new-approval test">';
			template += '<div class="align-right col-md-3 "><label class="level"><spring:message code="rfi.createrfi.level"/> ' + index + '</label></div>';
			template += '<div class="col-md-5 col-sm-5 col-xs-5" id="sel">';
			template += '<select style="margin-left: 7px;" data-validation="required" id="multipleSelectExample-'+(index - 1)+'" name="approvals[' + (index - 1) + '].approvalUsers" class="level-txt tagTypeMultiSelect user-list-all chosen-select" data-placeholder="Approvers"  multiple>';
			<c:forEach items="${budgetApprovalUserList}" var="users">
				<c:if test="${users.id == '-1' }">
					template += '<option value="-1" disabled >${users.name}</option>';
				</c:if>
				<c:if test="${users.id != '-1' }">
					template += '<option value="${users.id}" >${users.name}</option>';
				</c:if>
			</c:forEach>
			template += '</select></div><div class="col-md-4 pad0">';
			template += '<div   class="btn-address-field pt-0" style="position: relative;" ><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
			template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
			template += '<ul class="dropdown-menu dropup">';
			template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="approvals[' + (index - 1) + '].approvalType" checked="checked" value="OR" class="approval_condition" type="checkbox"/>&nbsp;Any </a></li>';
			template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="approvals[' + (index - 1) + '].approvalType" value="AND" class="approval_condition"  type="checkbox"/>&nbsp;All </a></li>	</ul>';
			template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
			template += '</div>';

			$(".dynamic-approval").append(template);
			$('#multipleSelectExample-'+(index - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });
			updateUserList('',$('#select#multipleSelectExample-'+(index - 1)),'ALL_USER');
			index++;
		});

		$(document).delegate('.removeApproval', 'click', function(e) {
			$(this).closest(".new-approval").remove();

			$(".new-approval").each(function(i, v) {
				i++;
				$(this).attr("id", "new-approval-" + i);
				$(this).find(".level").text('Level ' + i);
				
				$(this).find(".approval_condition").each(function(){
					$(this).attr("name",'approvals[' +(i-1) + '].approvalType');
				}) // checkbox name reindex
				
				$(this).find("input[name='_approvals[" + i + "].approvalType']").each(function(){
					$(this).attr('name','_approvals[' +(i-1) + '].approvalType');
				}); //Checkbox hidden val reindex
				
				
				$(this).find(".tagTypeMultiSelect").each(function(){
					$(this).attr("name",'approvals[' +(i-1) + '].approvalUsers');
				}) //select name reindex
				
				$(this).find("input[name='_approvals[" + i + "].approvalUsers']").each(function(){
					$(this).attr("name",'_approvals[' +(i-1) + '].approvalUsers');
				}) //select name reindex hidden
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

	});
	
	
/* 	window.onload = function() {
		//Register onclick event for button after loading document and dom
		document.getElementById("addSelect").addEventListener("click", function() {

		});
	}; */

	//Create a dynamic Select
/* 	function createSelect() {

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

	} */
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>