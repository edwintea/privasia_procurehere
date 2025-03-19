<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.id" var="loggedInUserId" />
<spring:message code="eventsummary.checkbox.editor" var="editorLabel" />
<spring:message code="eventsummary.checkbox.viewer" var="viewerLabel" />
<spring:message code="eventsummary.checkbox.associate.owner" var="associateOwnerLabel" />
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
					</a></li>
					<c:if test="${! empty spForm.formName}">
						<li class="active"><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/supplierPerformanceList"><spring:message code="sp.evaluation.list" /> &nbsp>&nbsp</a></li>
					</c:if>
					<c:if test="${ empty spForm.formName}">
						<li class="active"><a href="${pageContext.request.contextPath}/buyer/spTemplateList"><spring:message code="sptemplate.list" /> &nbsp>&nbsp</a></li>
					</c:if>
				</ol>
				<section class="create_list_sectoin">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap tender-request-heading">${createTemplate}</h2>
						<h2 class="trans-cap pull-right">
							<spring:message code="application.status" />
							: ${spForm.formStatus}
						</h2>
					</div>
					<div class="clear"></div>
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<div class="clear"></div>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<jsp:include page="supplierPerformanceFormHeader.jsp"></jsp:include>
					<div class="clear"></div>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<input type="hidden" name="id" value="${supplierPerformanceTemplate.id}">
					<div class="tab-pane active">
						<div class="upload_download_wrapper clearfix event_info">
							<div>
								<h4>
									<spring:message code="supplier.performance.information" />
									<c:if test="${spForm.formStatus ne 'DRAFT'}">
										<form:form action="${pageContext.request.contextPath}/buyer/downloadPerformanceEvaluationSummary/${spForm.id}" method="POST">
											<button class="btn btn-sm float-right btn-info hvr-pop marg-right-10 btn-cls" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.spfsummary.download.summary" />'>
												<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
												</span> <span class="button-content"><spring:message code="performance.evaluation.summary.button" /></span>
											</button>
										</form:form>
									</c:if>
								</h4>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.form.id" />:
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
<%--							<c:if test="${not empty spForm.procurementCategory}">--%>
								<div class="form-tander1 requisition-summary-box">
									<div class="col-sm-4 col-md-3 col-xs-6">
										<label><spring:message code="label.procurement.category" />: </label>
									</div>
									<div>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<p>${spForm.procurementCategory != null ? spForm.procurementCategory.procurementCategories : 'N/A'}</p>
										</div>
									</div>
								</div>
<%--							</c:if>--%>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.business.unit" />:
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${spForm.businessUnit.unitName}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> <spring:message code="supplier.performance.evaluator" />:
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
									<label> <spring:message code="supplier.performance.start.date" />:
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
									<label> <spring:message code="supplier.performance.end.date" />:
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
									<label> <spring:message code="supplier.performance.reminder" />:
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>
										<c:if test="${empty reminderList}">
										No Reminders.
										</c:if>
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
									<label> <spring:message code="supplier.performance.recurrence" />:
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${spForm.isRecurrenceEvaluation ? 'Allowed - ' : 'Not Allowed'} ${spForm.isRecurrenceEvaluation ? spForm.recurrenceEvaluation : '' } ${spForm.isRecurrenceEvaluation ? ' Days' : ''}</p>
								</div>
							</div>
							
							<c:if test="${spForm.isRecurrenceEvaluation}">
								<div class="form-tander1 requisition-summary-box">
									<div class="col-sm-4 col-md-3 col-xs-6">
										<label> 
											<spring:message code="supplier.performance.no.of.recurrence" />:
										</label>
									</div>
									<div class="col-sm-5 col-md-5 col-xs-6">
										<p>${spForm.noOfRecurrence}</p>
									</div>
								</div>
							</c:if>
							
						</div>
						<jsp:include page="supplierPerformanceCriteria.jsp" />
					 	<jsp:include page="supplierPerformanceAudit.jsp" />
					</div>
					<c:if test="${spForm.formStatus eq 'DRAFT'}">
						<div class="row">
							<div class="col-md-3" style="margin-top: 10px" >
								<a href="${pageContext.request.contextPath}/buyer/editSupplierPerformanceEvaluation/${spForm.id}" id="previousButton" role="button" class="btn btn-black ph_btn hvr-pop marg-left-10"><spring:message code="application.previous" /></a>
							</div>
							<div class="col-md-9" style="margin-top: 10px">
								<form:form action="${pageContext.request.contextPath}/buyer/spfFinish/${spForm.id}" method="POST">
									<button id="finishButton" class="btn btn-info ph_btn hvr-pop marg-left-10 right-header-button hvr-rectangle-out">
										<spring:message code="application.finish" />
									</button>
								</form:form>
								<c:if test="${spForm.formStatus eq 'DRAFT'}">
									<a href="#confirmCancelspf" id="cancelButton" role="button" class="btn btn-danger marg-left-10 ph_btn hvr-pop right-header-button" data-toggle="modal"><spring:message code="supplier.performance.cancel" /></a>
								</c:if>
							</div>
						</div>
					</c:if>
					<c:if test="${spForm.formStatus eq 'SUSPENDED' and spForm.formOwner.id eq loggedInUserId}">
						<div class="clear"></div>
						<div class="row">
							<a href="#confirmResumeForm" role="button" class="btn btn-info marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal"><spring:message code="application.resume" /></a>
						</div>
					</c:if>
					
				</section>
			</div>
		</div>
	</div>
</div>


<!-- RESUMEN FORM -->
<div class="modal fade" id="confirmResumeForm" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="resume.confirmation" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="resume.dialog.text" />
							</label>
						</div>
					</div>
			</div>
			<form:form id="idResumeForm" modelAttribute="spForm" action="${pageContext.request.contextPath}/buyer/spfFinish/${spForm.id}?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
				<input type="hidden" name="id" value="${spForm.id}"> 
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<form:button type="button" id="resumeForm" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
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


<!-- cancel Event popup  -->
<div class="modal fade" id="confirmCancelspf" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form  action="${pageContext.request.contextPath}/buyer/cancelSPForm/${spForm.id}" method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="spf.confirm.to.cancel" />
							</label>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="submit" id="spfCancelRequest" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
						<spring:message code="application.yes" />
					</button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
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
<<script type="text/javascript">
$('#finishButton').click(function(){
	$('#loading').show();
});
$('#spfCancelRequest').click(function(){
	$('#loading').show();
});
$('#previousButton').click(function(){
	$('#loading').show();
});

$('#resumeForm').click(function(e) {
	e.preventDefault();
	if($('#idResumeForm').isValid()) {
		$('#loading').show();
		$('#idResumeForm').submit();
	}
});

</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">