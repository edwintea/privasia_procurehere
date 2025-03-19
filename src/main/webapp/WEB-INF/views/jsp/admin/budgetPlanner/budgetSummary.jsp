<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/multiselect/multi-select.css"/>">
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />



<style>
.pad-30 {
	padding: 0 3%;
}

.btn:focus, .btn:active:focus, .btn.active:focus, .btn.focus, .btn:active.focus,
	.btn.active.focus {
	outline: none !important;
}

.btn-black:active, .btn-black:focus, .btn-black:hover {
	border-color: transparent !important;
}

.pad-left-0 {
	padding-left: 0 ! important;
}
</style>

<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li>
					<a href="${buyerDashboard}"> <spring:message code="application.dashboard" />
					</a>
				</li>
				<li class="active">
					<spring:message code="defaultmenu.budget.summary" />
				</li>
			</ol>
			<div class="Section-title title_border gray-bg mar-b20">
				<h2 class="trans-cap supplier">
					<spring:message code="defaultmenu.budget.summary" />
					<h2 class="trans-cap pull-right">
						<spring:message code="application.status" />
						: ${budget.budgetStatus}
					</h2>
				</h2>
			</div>
			<div class="clear"></div>
			<div class="white-bg border-all-side float-left width-100 pad_all_15">
				<div class="row">
					<div class="col-md-6 col-sm-12 col-xs-12">
						<input type="hidden" id="prId" value="${budget.id}">
						<div class="tag-line">
							<h2>
								<spring:message code="label.budget.name" />
								: ${budget.budgetName != null ? budget.budgetName : 'N/A'}
							</h2>
						</div>
					</div>
					<div class="col-md-6 col-sm-12 col-xs-12">
						<div class="pull-right tag-line-right">
							<h2>
								<spring:message code="budget.owner" />
								: ${budget.budgetOwner.name} / ${budget.budgetOwner.communicationEmail}
							</h2>
						</div>
					</div>
				</div>
			</div>
			<div class="clear"></div>
			<div class="tab-pane" style="display: block">
				<div class="upload_download_wrapper clearfix event_info">
					<h4>
						<spring:message code="budget.info" />
					</h4>
					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label> <spring:message code="budget.id" /> :
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>${budget.budgetId}</p>
						</div>
					</div>
					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label> <spring:message code="label.budget.name" /> :
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>${budget.budgetName}</p>
						</div>
					</div>

					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label> <spring:message code="sourcing.businessUnit" />
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>${budget.businessUnit.unitName}</p>
						</div>
					</div>
					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label> <spring:message code="sourcing.CostCenter" />
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>${budget.costCenter.costCenter}</p>
						</div>
					</div>
					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label> <spring:message code="budget.currency" /> :
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>${budget.baseCurrency.currencyCode}</p>
						</div>
					</div>
					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label><spring:message code="label.budget.validFrom" /> :</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>
								<fmt:formatDate value="${budget.validFrom}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
							</p>
						</div>
					</div>
					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label><spring:message code="label.budget.validTo" /> :</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>
								<fmt:formatDate value="${budget.validTo}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
							</p>
						</div>
					</div>
					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label><spring:message code="budget.created.date" /> :</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>
								<fmt:formatDate value="${budget.createdDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
							</p>
						</div>
					</div>
					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label> <spring:message code="label.budget.budgetOverRun" />
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>${budget.budgetOverRun ==true ?'Yes':'No' }</p>
						</div>
					</div>
					
					<c:if test="${budget.budgetOverRun ==true}">
						<div class="form-tander1 requisition-summary-box">
							<div class="col-sm-4 col-md-3 col-xs-6">
								<label> <spring:message code="label.budget.budgetOverRun.notification" />
								</label>
							</div>
							<div class="col-sm-5 col-md-5 col-xs-6">
								<p>${budget.budgetOverRunNotification ==true ?'Yes':'No' }</p>
							</div>
						</div>
					</c:if>
					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label> Total Budget Amount :</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
						<fmt:formatNumber var="totalAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.totalAmount}" />
							<p>${totalAmount}</p>
						</div>
					</div>
					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label> Remaining Amount :</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
						<fmt:formatNumber var="remainingAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.remainingAmount}" />
							<p class="totalAmountPara">${remainingAmount}</p>
						</div>
					</div>
					<c:if test="${not empty budget.revisionDate}">
						<div class="form-tander1 requisition-summary-box">
							<div class="col-sm-4 col-md-3 col-xs-6">
								<label>Revision Date :</label>
							</div>
							<div class="col-sm-5 col-md-5 col-xs-6">
								<p>
									<fmt:formatDate value="${budget.revisionDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								</p>
							</div>
						</div>
					</c:if>
				</div>
				<c:if test="${not empty budget.addRevisionJustification}">
					<div class="upload_download_wrapper clearfix event_info Approval-tab " style="margin-top: 0.5%;">
						<h4 class="panel-title list-pad-0">Add Amount Revision</h4>
						<div class="form-tander1 requisition-summary-box ">
							<div class="col-sm-4 col-md-3 col-xs-6">
								<label> Revised Total Amount :</label>
							</div>
							<div class="col-sm-5 col-md-5 col-xs-6">
							<fmt:formatNumber var="addRevisionAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.addRevisionAmount}" />
								<p>${addRevisionAmount}</p>
							</div>
						</div>
						<div class="form-tander1 requisition-summary-box ">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> Revised Remaining Amount :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
								<fmt:formatNumber var="addRevisionRemainingAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.addRevisionRemainingAmount}" />
									<p>${addRevisionRemainingAmount}</p>
								</div>
						</div>
						<div class="form-tander1 requisition-summary-box">
							<div class="col-sm-4 col-md-3 col-xs-6">
								<label> Revision Justification :</label>
							</div>
							<div class="col-sm-5 col-md-5 col-xs-6">
								<p>${budget.addRevisionJustification}</p>
							</div>
						</div>
					</div>
				</c:if>

				<c:if test="${not empty budget.deductRevisionJustification}">
					<div class="upload_download_wrapper clearfix event_info Approval-tab " style="margin-top: 0.5%;">
						<h4 class="panel-title list-pad-0">Deduct Amount Revision</h4>
						<div class="pad_all_15">
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> Revised Total Amount :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<fmt:formatNumber var="deductRevisionAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.deductRevisionAmount}" />
									<p>${deductRevisionAmount}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box ">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> Revised  Remaining Amount :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
								<fmt:formatNumber var="deductRevisionRemainingAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.deductRevisionRemainingAmount}" />
									<p>${deductRevisionRemainingAmount}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> Revision Justification :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${budget.deductRevisionJustification}</p>
								</div>
							</div>
						</div>
					</div>
				</c:if>

				<c:if test="${not empty budget.transferRevisionJustification}">
					<div class="upload_download_wrapper clearfix event_info Approval-tab " style="margin-top: 0.5%;">
						<h4 class="panel-title list-pad-0">Transfer Amount Revision</h4>
						<div class="pad_all_15">
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> Revised Total Amount :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
								<fmt:formatNumber var="transferRevisionAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.transferRevisionAmount}" />
									<p>${transferRevisionAmount}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box ">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> Revised  Remaining Amount :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
								<fmt:formatNumber var="transferRevisionRemainingAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.transferRevisionRemainingAmount}" />
									<p>${transferRevisionRemainingAmount}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> Revision Justification :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${budget.transferRevisionJustification}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> Transfer to Business Unit :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${budget.toBusinessUnit.unitName}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> Transfer to Cost Center :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${budget.toCostCenter.costCenter}</p>
								</div>
							</div>
							<c:if test="${not empty budget.conversionRate}">
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6">
									<label> Conversion Rate :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${budget.conversionRate}</p>
								</div>
							</div>
							</c:if>
						</div>
					</div>
				</c:if>
			</div>

			<div class="upload_download_wrapper clearfix event_info Approval-tab " style="margin-top: 0.5%;">
				<h4 class="panel-title list-pad-0">
					<a><spring:message code="budget.document" /></a>
				</h4>
				<div class="pad_all_15">
					<div class="panel-body mega">
						<table class="tabaccor padding-none-td header" width="100%" cellpadding="0" cellspacing="0" border="0">
							<thead>
								<tr>
									<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="rfs.documents.name" /></th>
									<%-- <th class="width_200 width_200_fix align-left wo-rp"><spring:message code="rfs.documents.description" /></th> --%>
									<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="rfs.documents.date&time" /></th>
									<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="rfs.documents.size" /></th>
								</tr>
							</thead>
						</table>
						<table width="100%" cellpadding="0" cellspacing="0" border="0" class="deta pad-for-table">
							<tbody>
								<c:forEach var="budgetFile" items="${budget.budgetDocuments}">
									<tr>
										<td class="width_200 width_200_fix align-left wo-rp">
											<a class="bluelink" href="${pageContext.request.contextPath}/admin/budgets/downloadBudgetDocument/${budgetFile.id}"> ${budgetFile.fileName}</a>
										</td>
										<%-- 	<td class="width_200 width_200_fix align-left wo-rp">${listDoc.description}</td> --%>
										<td class="width_200 width_200_fix align-left wo-rp">
											<fmt:formatDate value="${budgetFile.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</td>
										<td class="width_100 width_100_fix align-left wo-rp">${budgetFile.fileSizeInKb}KB</td>
									</tr>
								</c:forEach>
								<c:if test="${fn:length(budget.budgetDocuments) == 0}">
									<tr>
										<td class="width_200 width_200_fix align-left wo-rp" colspan="4">
											<spring:message code="budget.no.document" />
										</td>
									</tr>
								</c:if>
							</tbody>
						</table>
					</div>
				</div>
			</div>

			<c:if test="${not empty budget.approvals}">
				<div class="upload_download_wrapper clearfix event_info Approval-tab " style="margin-top: 0.5%;">
					<h4>
						<spring:message code="label.budget.approvals.route" />
					</h4>
					<div id="apprTab" class="pad_all_15 collapse in float-left width-100 position-relative in">
						<c:forEach items="${budget.approvals}" var="approval">
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
						</c:forEach>
					</div>
					<!-- <div class="clear"></div> -->

					<c:if test="${not empty budget.budgetComment}">
						<div class="remark-tab pad0">
							<h4>
								<spring:message code="summarydetails.approval.comments" />
							</h4>
							<div class="pad_all_15 float-left width-100">
								<c:forEach items="${budget.budgetComment}" var="comment">
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
					</c:if>
					<%-- <c:if test="${approvalUsers.status eq 'PENDING' and eventPermissions.activeApproval}"> --%>
					<div class="form_field pad-30">
						<form id="approvedForm" action="${pageContext.request.contextPath}/admin/budgets/approve?${_csrf.parameterName}=${_csrf.token}" method="post">
							<div class="row">
								<input type="hidden" name="reject" id="reject" /> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="id" id="id" value="${budget.id}">
								<div class="col-sm-12 col-md-12 align-right col-xs-12 ">
									<label class="control-label"><spring:message code="label.meeting.remark" /> : </label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-12 ">
									<textarea name="remarks" id="approvalremarks" rows="4" data-validation="length" data-validation-length="0-450" class="form-control"></textarea>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20 marg-bottom-20">
									<button type="button" id="approvedButton" class="btn btn-info ph_btn hvr-pop marg-left-10 hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">Approve</button>
									<button type="button" id="rejectedButton" class="btn btn-black ph_btn hvr-pop marg-left-10 hvr-rectangle-out1  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">Reject</button>
								</div>
							</div>
						</form>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</div>

<script>
	$('#approvedButton').click(function(e) {
		e.preventDefault();
		$(this).addClass('disabled');
		console.log("approved");
		$('#approvedForm').submit();
	});
	$('#rejectedButton').click(function(e) {
		e.preventDefault();
		$(this).addClass('disabled');
		console.log("approved");
		$('#reject').val("reject");
		$('#approvedForm').submit();
	});
</script>
