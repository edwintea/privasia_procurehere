<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<c:if test="${not empty supplierForm.approvals}">
		<div class="upload_download_wrapper clearfix event_info Approval-tab " style="margin-top: -1%;">

			<h4>
				<spring:message code="supplierForm.approval.route" />
				<c:if test="${(eventPermissions.owner or isAdmin)  && supplierForm.status eq 'SUBMITTED' && supplierForm.approvalStatus eq 'PENDING'}">
					<button class="editApprovalPopupButton btnEditApproval sixbtn" data-toggle="dropdown" title="Edit">
						<img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" alt="edit" />
					</button>
				</c:if>
			</h4>
			<div id="apprTab" class="pad_all_15 collapse in float-left width-100 position-relative in">
				<c:set var="currentBatch" value="0" />
				<c:forEach items="${supplierForm.approvals}" var="approval">
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
			<div class="clear"></div>
			<c:if test="${not empty supplierForm.formComments}">
				<div class="remark-tab pad0">
					<h4>
						<spring:message code="summarydetails.approval.comments" />
					</h4>
					<div class="pad_all_15 float-left width-100">
						<c:forEach items="${supplierForm.formComments}" var="comment">
							<div class="Comments_inner ${comment.approved ? 'comment-right' : 'comment-wrong'}">

								<h3>${comment.createdBy.name}
									<span> <fmt:formatDate value="${comment.createdDate}" pattern="E dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
									</span>
								</h3>
								<div class="${comment.approved ? 'approve-div' : 'reject-div'}">${comment.comment}</div>
							</div>
						</c:forEach>
					</div>

				</div>
			</c:if>
			<c:if test="${supplierForm.approvalStatus eq 'PENDING' and eventPermissions.activeApproval}">
				<div class="form_field">
					<form id="approvedRejectForm" method="post">
						<div class="row">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="formSubId" id="formSubId" value="${supplierForm.id}">


							<div class="col-sm-12 col-md-12 align-right col-xs-12 ">
								<label class="control-label"><spring:message code="label.meeting.remark" /> : </label>
							</div>
							<div class="col-sm-5 col-md-5 col-xs-12 ">
								<textarea name="remarks" id="approvalremarks" rows="4" data-validation="required length" data-validation-length="0-450" class="form-control"></textarea>
							</div>

						</div>
						<div class="row">
							<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20 marg-bottom-20">
								<button type="button" id="formapprovedButton" class="btn btn-info ph_btn hvr-pop marg-left-10 hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">Approve</button>
								<button type="button" id="formrejectedButton" class="btn btn-black ph_btn hvr-pop marg-left-10 hvr-rectangle-out1  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">Reject</button>
							</div>
						</div>
					</form>
				</div>
			</c:if>

		</div>
	</c:if>
	<!--  EDIT APPROVAL POPUP -->
<div class="flagvisibility dialogBox" id="editApprovalPopup" title="Edit Approval">
	<div class="float-left width100 pad_all_15 white-bg">
		<c:url value="/buyer/updateSuppFormSubmitionApproval" var="updateSuppFormSubmitionApproval" />
		<form:form id="supplierApprovalForm" action="${updateSuppFormSubmitionApproval}" method="post" modelAttribute="supplierForm">
			<form:hidden path="id" />
			<jsp:include page="supplierFormSubmitionEditApproval.jsp"></jsp:include>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center marg-top-20">
						<button type="reset" id="resetApprovalForm" style="display: none;"></button>
						<button type="submit" id="updateApproval" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out">
							<spring:message code="application.update" />
						</button>
						<button type="button" id="cancelApprovalForm" class="closeDialog closeEditPopUp btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>
<script type="text/javascript">
//summary edit Approval pop up
$(document).delegate('.editApprovalPopupButton', 'click', function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	$("#editApprovalPopup").dialog({
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
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
});

$('#formapprovedButton').click(function(e) {
    $('#approvalremarks').removeAttr('data-validation');
    $('#approvalremarks').attr('data-validation', 'length');
	e.preventDefault();
	if($('#approvedRejectForm').isValid()) {
		$(this).addClass('disabled');
		$('#formrejectedButton').addClass('disabled');

	} else {
		return;
	}
	console.log("approved");
	$('#approvedRejectForm').attr('action', getContextPath() + "/buyer/formSubmitionApproved");
	$('#approvedRejectForm').submit();

});
$('#formrejectedButton').click(function(e) {
    $('#approvalremarks').removeAttr('data-validation');
    $('#approvalremarks').attr('data-validation', 'required length');
	e.preventDefault();
	if($('#approvedRejectForm').isValid()) {
		$(this).addClass('disabled');
		$('#formapprovedButton').addClass('disabled');

	} else {
		return;
	}
	console.log("rejectedButton");
	$('#approvedRejectForm').attr('action', getContextPath() + "/buyer/formSubmitionRejected");
	$('#approvedRejectForm').submit();

});

</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
