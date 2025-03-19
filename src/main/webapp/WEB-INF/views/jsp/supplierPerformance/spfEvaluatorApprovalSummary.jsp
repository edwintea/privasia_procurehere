<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
<spring:message var="rfxCreatingSummaryDesk" code="application.rfx.create.summary" />
<spring:message var="rfxTeamMembersDesk" code="application.rfx.team.members" />
<spring:message var="rfxbidHistoryDesk" code="application.rfa.bid.history" />

<div class="Section-title title_border gray-bg">
	<h2 class="trans-cap manage_icon"> <spring:message code="sp.evaluation" /> </h2>
	<h2 class="trans-cap pull-right"> <spring:message code="application.status" /> : ${spForm.formStatus} </h2>
</div>
<div class="Invited-Supplier-List white-bg">
	<div class="row">
		<div class="col-md-6 col-sm-6 close-da">
			<div class="row">
				<div class="col-md-10">
					<div class="meeting2-heading-new">
						<jsp:useBean id="now" class="java.util.Date" />
						<c:if test="${now.time gt spForm.evaluationStartDate.time}">
							<label><spring:message code="application.closedate" /></label>
							<h3>
								<span> <fmt:formatDate value="${spForm.evaluationEndDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								</span>
							</h3>
						</c:if>
						<c:if test="${now.time lt spForm.evaluationStartDate.time}">
							<label><spring:message code="application.startdate" /></label>
							<h3>
								<span> <fmt:formatDate value="${spForm.evaluationStartDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								</span>
							</h3>
						</c:if>
					</div>
				</div>
			</div>
		</div>
		<c:if test="${(spForm.formStatus != 'CANCELED' and spForm.formStatus != 'SUSPENDED' and spForm.formStatus != 'CLOSED' and spForm.formStatus != 'CONCLUDED')}">
			<div class="col-md-6 col-sm-6 close-da">
				<div id="main">
					<div class="content">
						<div class="counter">
							<jsp:useBean id="today" class="java.util.Date" />
							<c:if test="${today.time lt spForm.evaluationEndDate.time and today.time gt spForm.evaluationStartDate.time}">
								<div>
									<h3>
										<spring:message code="application.time.left.end" />
									</h3>
								</div>
								<div class="main-example">
									<fmt:formatDate value="${spForm.evaluationEndDate}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="evaluationEndDate" />
									<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/spForm/${spForm.id}" data-reload-url="${pageContext.request.contextPath}/buyer/spfEvaluatorApprovalSummary/${evaluatorUser.id}" data-date="${evaluationEndDate}"></div>
								</div>
							</c:if>
							<c:if test="${today.time lt spForm.evaluationStartDate.time}">
								<div>
									<h3>
										<spring:message code="application.time.left.start" />
									</h3>
								</div>
								<div class="main-example">
									<fmt:formatDate value="${spForm.evaluationStartDate}" pattern="yyyy-MM-dd HH:mm" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" var="evaluationStartDate" />
									<div class="countdown-container" id="main-example1" data-url="${pageContext.request.contextPath}/timeRemaining/spForm/${spForm.id}" data-reload-url="${pageContext.request.contextPath}/buyer/spfEvaluatorApprovalSummary/${evaluatorUser.id}" data-date="${evaluationStartDate}"></div>
								</div>
							</c:if>
							<div class="pull-right marg-top-10">
								<h3 style="padding-right: 20px;">Approval Status: ${evaluatorUser.evaluationStatus eq 'DRAFT' or evaluatorUser.evaluationStatus eq 'PENDING' ? 'PENDING' : evaluatorUser.evaluationStatus}</h3>
							</div>
						</div>
					</div>
				</div>
				<!-- /#Main Div -->
			</div>
		</c:if>
		<!-- /.Columns Div -->
	</div>
	<!-- /.Row Div -->
</div>
<div class="clear"></div>
<div class="tab-main-inner pad_all_15 border-all-side float-left width-100 marg_top_20">
	<div class="row">
		<div class="col-md-3">
			<b><spring:message code="supplier.performance.form.id" />:</b> ${spForm.formId} <br/> <b><spring:message code="supplier.performance.form.owner" />:</b> ${spForm.formOwner.name}, ${spForm.formOwner.communicationEmail}<br />
		</div>
				
		<div class="col-sm-12 col-md-9 pull-right">
			<div class="pull-right">
				<form:form action="${pageContext.request.contextPath}/buyer/downloadPerformanceEvaluationApprovalSummary/${evaluatorUser.id}" method="POST">
					<button class="btn btn-sm float-right btn-info hvr-pop marg-right-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.spfsummary.download.summary" />'>
						<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
						</span> <span class="button-content"><spring:message code="performance.evaluation.summary.button" /></span>
					</button>
				</form:form>
			</div>
		</div>
	</div>
</div>
<div class="Invited-Supplier-List white-bg">
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
								<label> <spring:message code="supplier.performance.evalutr" /> :
								</label>
							</div>
							<div class="col-sm-5 col-md-5 col-xs-6">
								<p>
									${evaluatorUser.evaluator.name}
								</p>
							</div>
						</div>
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
					</div>
					<c:if test="${evaluatorUser.evaluationStatus ne 'DRAFT'}">
						<jsp:include page="summarySPFormApprovals.jsp" />
					</c:if>
					
					<jsp:include page="evaluatorPerfEvaluationAppCriteria.jsp" />
				 	<jsp:include page="supplierPerformanceAudit.jsp" />
				</div>
			</section>
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
		
	});
</script>