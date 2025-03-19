<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.id" var="loggedInUserId" />
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message code="eventsummary.checkbox.editor" var="editorLabel" />
<spring:message code="eventsummary.checkbox.viewer" var="viewerLabel" />
<spring:message code="eventsummary.checkbox.associate.owner" var="associateOwnerLabel" />
	<div class="row">
		<div class="col-md-12 col-sm-12 close-da">
				<section class="create_list_sectoin">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<input type="hidden" name="id" value="${supplierPerformanceTemplate.id}">
					<div class="tab-pane active">
						<div class="upload_download_wrapper clearfix event_info">
							<div>
								<h4>
									<spring:message code="supplier.performance.information" />
								</h4>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label>
										<spring:message code="supplier.performance.form.id" />:
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${spForm.formId}</p>
								</div>
							</div>
								<div class="form-tander1 requisition-summary-box">
									<div class="col-sm-4 col-md-3 col-xs-6">
										<label> <spring:message code="supplier.performance.form.name" />:
										</label>
									</div>
									<div class="col-sm-5 col-md-5 col-xs-6">
										<p>${spForm.formName}</p>
									</div>
								</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label><spring:message code="supplier.performance.form.owner" />: </label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
										<p>${spForm.formOwner.name}(${spForm.formOwner.communicationEmail})</p>
									</div>
							</div>
						</div>
						<div class="upload_download_wrapper clearfix marg-top-10 event_info">
							<h4>
								<spring:message code="supplier.performance.details" />
							</h4>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.reference.number" />:
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${spForm.referenceNumber}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.reference.name" />:
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${spForm.referenceName}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.form.supplier" />:
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${spForm.awardedSupplier.companyName}</p>
								</div>
							</div>
							<c:if test="${not empty spForm.procurementCategory}">
								<div class="form-tander1 requisition-summary-box">
									<div class="col-sm-4 col-md-3 col-xs-6">
										<label><spring:message code="label.procurement.category" />: </label>
									</div>
									<div>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<p>${spForm.procurementCategory.procurementCategories}</p>
										</div>
									</div>
								</div>
							</c:if>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.business.unit" /> :
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${spForm.businessUnit.unitName}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.evaluator" /> :
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>
										<c:forEach items="${spForm.evaluators}" var="evaluator" varStatus="status">
											${status.index + 1}. 
											${evaluator.evaluator.name}
											<br />
										</c:forEach>
									</p>
								</div>
							</div>
						</div>
						<div class="upload_download_wrapper clearfix marg-top-10 event_info">
							<h4>
								<spring:message code="supplier.performance.period" />
							</h4>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.start.date" /> :
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>
										<fmt:formatDate value="${spForm.evaluationStartDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
									</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.end.date" /> :
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>
										<fmt:formatDate value="${spForm.evaluationEndDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
									</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.reminder" /> :
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>
										<c:forEach items="${reminderList}" var="reminder" varStatus="status">
											<spring:message code="rfaevent.reminder" /> ${status.index + 1}:
											<fmt:formatDate value="${reminder.reminderDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											<br />
										</c:forEach>
									</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.recurrence" /> :
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${spForm.isRecurrenceEvaluation ? 'Allowed - ' : 'Not Allowed'} ${spForm.isRecurrenceEvaluation ? spForm.recurrenceEvaluation : '' } ${spForm.isRecurrenceEvaluation ? ' Days' : ''}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="sp.no.of.recurrence" /> :
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${spForm.noOfRecurrence}</p>
								</div>
							</div>
						</div>
						
						<jsp:include page="supplierPerformanceCriteria.jsp" />
					 	<jsp:include page="supplierPerformanceAudit.jsp" />
					</div>

	                     <!-- Suspend Block -->
	                     <div class="row">
	                     	<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20">
								<c:if test="${(spForm.formStatus eq 'ACTIVE' or spForm.formStatus eq 'SCHEDULED') and spForm.formOwner.id eq loggedInUserId}">
									<a href="#confirmSuspendForm" role="button" class="btn btn-warning ph_btn hvr-pop align-center" data-toggle="modal"><spring:message code="lable.form.suspend" /></a>
								</c:if>
							</div>
						</div>
				</section>
			</div>
		</div>
<!-- SUSPEND FORM -->
<div class="modal fade" id="confirmSuspendForm" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="suspend.confirmation" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="suspend.dialog.text" />
							</label>
						</div>
					</div>
			</div>
			<form:form id="idSuspendForm" modelAttribute="spForm" action="${pageContext.request.contextPath}/buyer/suspendForm?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
				<input type="hidden" name="id" value="${spForm.id}"> 
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="button" id="suspendForm" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
						<spring:message code="application.yes" />
					</form:button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no" />
					</button>
				</div>
				</form:form>
		</div>
	</div>
</div>
<style>
.btn-cls {
	position: absolute;
    right: 30px;
    top: 200px;
}

</style>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">

<script type="text/javascript">
	$(document).ready(function() {
		$("#spfSubmit").click(function(e) {
			$("#evalUserForm").submit();
		});
		
	});
	
	$('#suspendForm').click(function(e) {
		e.preventDefault();
		if($('#idSuspendForm').isValid()) {
			$('#loading').show();
			$('#idSuspendForm').submit();
		}
	});
</script>