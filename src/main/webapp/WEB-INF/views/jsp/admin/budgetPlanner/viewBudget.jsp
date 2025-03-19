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
.mr-7 {
	margin-right: 7px;
}

.pad-30 {
	padding: 0 3%;
}

.for-absulate.close {
	right: 15px;
	top: 11px;
}

.mt-25 {
	margin-top: 25px;
}

.mb-35 {
	margin-bottom: 35px;
}

.mt-30 {
	margin-top: 30px;
}

.pr-0 {
	padding-right: 0;
}

.mt-5 {
	margin-top: 5px;
}

.t-al-r {
	text-align: right;
}

.modal-body {
	padding: 25px 20px !important;
}

.p-0 {
	padding: 0;
}

.d-flex-a-center {
	display: flex;
	align-items: center;
	margin-bottom: 15px;
}

.d-flex-a-center input {
	margin-top: 0;
}

.right_button {
	text-align: right;
}

.p-0 {
	padding: 0 !important;
}

.w-80 {
	width: 80px;
}

.d-flex {
	display: flex;
}

.p-11 {
	padding: 11px;
	padding-left: 15px;
}

.mt-20-b-0 {
	margin-top: 20px;
	margin-bottom: 0;
}

.btn-add-approve-lavel {
	display: flex;
	justify-content: left;
	margin-top: 25px;
	margin-left:63px;
}

.error {
	color: #ff5757 !important
}

.alert-lbl-text {
	color: red;
	margin-top: 3px;
	border-radius: 5px;
	padding: 7px 5px;
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
				<c:url value="/admin/budgets/listBudget" var="manageBudget" />
				<li>
					<a id="manageBudget" href="${manageBudget}"> <spring:message code="defaultmenu.budget.manage" />
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
			<c:if  test="${permissions.changeBudget  && canEdit}">
				<div class="white-bg border-all-side float-left width-100 pad_all_15">
				<div class="row">
						<div class="right_button" style="position: relative; margin-right: 10px;">
						<button style="float: right;" class="w-80 btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="btnBudgetTransfer">
							<spring:message code="budget.transfer" />
						</button>
						<button style="float: right;" class="w-80 btn btn-plus btn-info hvr-pop hvr-rectangle-out mr-7" id="btnBudgetDeduct">
							<spring:message code="budget.deduct" />
						</button>
						<button style="float: right;" class="w-80 btn btn-plus btn-info hvr-pop hvr-rectangle-out mr-7" id="btnBudgetAdd">
							<spring:message code="budget.add" />
						</button>
					</div>
				</div>
				</div>
			</c:if>
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
						<input type="hidden" id="totalAmountValue" value="${budget.totalAmount}">
							<p class="totalAmountPara">${totalAmount}</p>
						</div>
					</div>
					<div class="form-tander1 requisition-summary-box">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label> Remaining Amount :</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
						<fmt:formatNumber var="remainingAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.remainingAmount}" />
							<input type="hidden" id="remainingAmountValue" value="${budget.remainingAmount}">
							<p class="remainingAmountPara">${remainingAmount}</p>
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
						<div class="pad_all_15">
							<div class="form-tander1 requisition-summary-box ">
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
									<label> Revised Total Amount :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
								<fmt:formatNumber var="addRevisionAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.addRevisionAmount}" />
									<p>${addRevisionAmount}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box ">
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
									<label> Revised Remaining Amount :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
								<fmt:formatNumber var="addRevisionRemainingAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.addRevisionRemainingAmount}" />
									<p>${addRevisionRemainingAmount}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
									<label> Revision Justification :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${budget.addRevisionJustification}</p>
								</div>
							</div>
						</div>
					</div>
				</c:if>

				<c:if test="${not empty budget.deductRevisionJustification}">
					<div class="upload_download_wrapper clearfix event_info Approval-tab " style="margin-top: 0.5%;">
						<h4 class="panel-title list-pad-0">Deduct Amount Revision</h4>
						<div class="pad_all_15">
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
									<label> Revised Total Amount :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
								<fmt:formatNumber var="deductRevisionAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.deductRevisionAmount}" />
									<p>${deductRevisionAmount}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box ">
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
									<label> Revised Remaining Amount :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
								<fmt:formatNumber var="deductRevisionRemainingAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.deductRevisionRemainingAmount}" />
									<p>${deductRevisionRemainingAmount}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
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
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
									<label> Revised Total Amount :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
								<fmt:formatNumber var="transferRevisionAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.transferRevisionAmount}" />
									<p>${transferRevisionAmount}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box ">
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
									<label> Revised Remaining Amount :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
								<fmt:formatNumber var="transferRevisionRemainingAmount" type="number" minFractionDigits="2" maxFractionDigits="6" value="${budget.transferRevisionRemainingAmount}" />
									<p>${transferRevisionRemainingAmount}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
									<label> Revision Justification :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${budget.transferRevisionJustification}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
									<label> Transfer to Business Unit :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${budget.toBusinessUnit.unitName}</p>
								</div>
							</div>
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
									<label> Transfer to Cost Center :</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-6">
									<p>${budget.toCostCenter.costCenter}</p>
								</div>
							</div>
							<c:if test="${not empty budget.conversionRate}">
							<div class="form-tander1 requisition-summary-box">
								<div class="col-sm-4 col-md-3 col-xs-6 pad-left-0">
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
										<%-- <td class="width_200 width_200_fix align-left wo-rp">${listDoc.description}</td> --%>
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
				<div class="upload_download_wrapper clearfix event_info Approval-tab " style="margin-top: 0.5%; margin-bottom: 10px;">
					<h4>
						<spring:message code="label.budget.approvals.route" />
					</h4>
					<div id="apprTab" class="pad_all_15 collapse in float-left width-100 position-relative in">
						<c:set var="currentBatch" value="0" />
						<c:forEach items="${budget.approvals}" var="approval">
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
					<c:if test="${not empty budget.budgetComment}">
						<div class="remark-tab pad0">
							<h4 class="test">
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
				</div>
			</c:if>
 				<c:url value="/admin/budgets/listBudget" var="listBudget" />
				<a href="${listBudget}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous" style="position: relative; left: 43%;"> <spring:message code="application.back" /></a>
 		</div>
	</div>
</div>



<!-- add-popup -->
<div id="add-popup" class="modal fade" role="dialog"   data-backdrop="static">
	<form:form id="addBudgetForm" cssClass="form-horizontal" action="${pageContext.request.contextPath}/admin/budgets/changeBudget?${_csrf.parameterName}=${_csrf.token}" method="post" modelAttribute="changeBudget" autocomplete="off">
		<div class="modal-dialog" style="width: 90%; max-width: 930px; max-height:500px;" >
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center">
						<spring:message code="label.budget.addAmount" />
					</h3>
					<button class="close for-absulate cancelButtonApproval cancelAddBudget" type="button" data-dismiss="modal">X</button>
				</div>
				<form:hidden class="form-control addBudgetId" path="id" />
				<div class="modal-body hasSuccessAdd" style="padding: 25px 20px 0 !important; overflow: auto">


					<spring:message code="label.budget.revision.justification" var="placeRevisionJustification" />
					<div class="row">
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.addAmount" /></label>
							</div>
							<div class="col-md-8">
								<fmt:formatNumber var="addAmount" type="number" minFractionDigits="2" maxFractionDigits="2" value="${addAmount}" />
								<form:input id="addAmount" path="addAmount"  value="${addAmount}" class="form-control autoSave newAmount" placeholder="add Amount" 
								style="margin-right: 15px;z-index: 5;" data-validation="required validate_custom_length positive1" data-validation-regexp="^\d{1,10}(\.\d{1,2})?$" data-sanitize="numberFormat" data-sanitize-number-format="0,0.00"/>
							</div>
						</div>
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.revision.total.amount" /></label>
							</div>
							<div class="col-md-8">
								<form:input id="addRevisionAmount" path="addRevisionAmount" type="text" class="form-control revisionAmount" readonly="true"  />
							</div>
						</div>
					</div>

					<spring:message code="label.budget.addAmount" var="placeAddAmount" />
					<div class="row">
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.revision.justification" /></label>
							</div>
							<div class="col-md-8">
								<form:textarea path="addRevisionJustification" id="addRevisionJustification" type="text" class="form-control autoSave" placeholder="${placeRevisionJustification} " data-validation="required" />
							</div>
						</div>
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.revision.remaining.amount" /></label>
							</div>
							<div class="col-md-8">
								<form:input id="revisionRemainingAmount" path="addRevisionRemainingAmount" type="text" class="form-control revisionAmount" readonly="true"  />
							</div>
						</div>
					</div>
					<!-- additional approver  -->
					<div class="row">
						<div class="btn-add-approve-lavel ">
							<spring:message code="tooltip.add.approval.level" var="addapproval" />
							<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="addAmountAddApprover" data-toggle="tooltip" data-placement="top" title="${addapproval}">
								<spring:message code="application.addapproval.button" />
							</button>
						</div>

						<div class="addAmount-dynamic-approval mb-35 mt-30">
							<p class="approvalErrMsg alert-lbl-text align-center" hidden>
								<spring:message code="require.msg.add.approvals" />
							</p>
						</div>
					</div>
				</div>

				<div class="modal-footer  text-center">
					<input class="btn btn-info hvr-pop hvr-rectangle-out ph_btn_midium" id="addBudgetSubmit" type="submit" value="<spring:message code="application.save" />" data-dismiss="modal">
					<button class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium cancelButtonApproval cancelAddBudget" id="cancelAddBudget" type="button" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</form:form>
</div>


<!-- deduct-popup -->
<div id="deduct-popup" class="modal fade" role="dialog"  data-backdrop="static">
	<form:form id="deductBudgetForm" cssClass="form-horizontal" action="${pageContext.request.contextPath}/admin/budgets/changeBudget?${_csrf.parameterName}=${_csrf.token}" method="post" modelAttribute="changeBudget" autocomplete="off">
		<div class="modal-dialog" style="width: 90%; max-width: 930px; max-height:500px;">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center">
						<spring:message code="label.budget.deductAmount" />
					</h3>
					<button class="close for-absulate cancelButtonApproval cancelDeductBudget" type="button" data-dismiss="modal">X</button>
				</div>
				<form:hidden class="form-control addBudgetId" path="id" />
				<div class="modal-body hasSuccessDeduct" style="padding: 25px 20px 0 !important;  overflow: auto;">
					<spring:message code="label.budget.revision.justification" var="placeRevisionJustification" />
					<div class="row">
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.deductAmount" /></label>
							</div>
							<div class="col-md-8">
								<fmt:formatNumber var="deductAmount" type="number" minFractionDigits="2" maxFractionDigits="2" value="${deductAmount}" />
								<form:input id="deductAmount" path="deductAmount"  value="${deductAmount}" class="form-control autoSave newAmount" placeholder="Deduct Amount" style="margin-right: 15px;z-index: 5;" data-validation="required validate_custom_length positive1" data-validation-regexp="^\d{1,10}(\.\d{1,2})?$" data-sanitize="numberFormat" data-sanitize-number-format="0,0.00" />
							</div>
						</div>
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.revision.total.amount" /></label>
							</div>
							<div class="col-md-8">
								<form:input id="deductRevisionAmount" path="deductRevisionAmount" type="text" class="form-control revisionAmount" readonly="true" />
							</div>
						</div>
					</div>
					<spring:message code="label.budget.deductAmount" var="placeDeductAmount" />
					<div class="row">
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.revision.justification" /></label>
							</div>
							<div class="col-md-8">
								<form:textarea path="deductRevisionJustification" id="deductRevisionJustification" type="text" class="form-control autoSave" placeholder="${placeRevisionJustification} " data-validation="required" />
							</div>
						</div>
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.revision.remaining.amount" /></label>
							</div>
							<div class="col-md-8">
								<form:input id="deductedRevisedRemainingAmount" path="deductRevisionRemainingAmount" type="text" class="form-control revisionAmount" readonly="true"  />
							</div>
						</div>
					</div>
					<!-- additional approver  -->
					<div class="row">
						<div class="btn-add-approve-lavel">
							<spring:message code="tooltip.add.approval.level" var="addapproval" />
							<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="deductAmountAddApprover" data-toggle="tooltip" data-placement="top" title="${addapproval}">
								<spring:message code="application.addapproval.button" />
							</button>
						</div>
						<div class="deductAmount-dynamic-approval mb-35 mt-30">
							<p class="approvalErrMsg alert-lbl-text align-center" hidden>
								<spring:message code="require.msg.add.approvals" />
							</p>
						</div>
					</div>
				</div>
				<div class="modal-footer  text-center">
					<input class="btn btn-info hvr-pop hvr-rectangle-out ph_btn_midium" id="deductBudgetSubmit" value="Save" type="submit" data-dismiss="modal">
					<button class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium cancelButtonApproval cancelDeductBudget" type="button" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</form:form>
</div>

<!-- transfer-popup -->
<div id="transfer-popup" class="modal fade transfer-mdl" role="dialog" data-backdrop="static">
	<form:form id="transferBudgetForm" cssClass="form-horizontal" action="${pageContext.request.contextPath}/admin/budgets/changeBudget?${_csrf.parameterName}=${_csrf.token}" method="post" modelAttribute="changeBudget" autocomplete="off">
		<div class="modal-dialog" style="width: 90%; max-width: 930px;  max-height:500px;">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center">
						<spring:message code="label.budget.transferAmount" />
					</h3>
					<button class="close for-absulate cancelButtonApproval cancelTransferBudget" type="button" data-dismiss="modal">X</button>
				</div>
				<form:hidden class="form-control addBudgetId" path="id" />
				<div class="modal-body hasSuccessTransfer" style="padding: 25px 20px 0 !important; overflow: auto;">
					<div class="row">
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.to.businessUnit" /></label>
							</div>
							<div class="col-md-8">
								<form:select path="toBusinessUnit" cssClass="form-control chosen-select" id="idToBusinessUnit" data-validation="required" data-validation-error-msg-required="${required}">
									<form:option value="">
										<spring:message code="pr.select.business.unit" />
									</form:option>
									<form:options items="${businessUnitList}" itemValue="id" itemLabel="unitName" />
								</form:select>
							</div>
						</div>
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.costCenter" /></label>
							</div>
							<div class="col-md-8">
								<form:select path="toCostCenter" cssClass="form-control chosen-select" id="toCostCenter" data-validation="required">
									<form:option value="">
										<spring:message code="budget.default.cost.center" />
									</form:option>
									<form:options items="${costCenterList}" itemValue="id" itemLabel="costCenter" />
								</form:select>
							</div>
						</div>
					</div>

					<spring:message code="label.budget.transferAmount" var="placeTransferAmount" />
					<div class="row">
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.transferAmount" /></label>
							</div>
							<div class="col-md-8">
								<fmt:formatNumber var="transferAmount" type="number" minFractionDigits="2" maxFractionDigits="2" value="${transferAmount}" />
								<form:input id="transferAmount" path="transferAmount" data-sanitize="numberFormat" data-sanitize-number-format="0,0.00" value="${transferAmount}" class="form-control autoSave newAmount" placeholder="${placeTransferAmount} " style="margin-right: 15px;z-index: 5;" data-validation="required validate_custom_length positive1" data-validation-regexp="^\d{1,10}(\.\d{1,2})?$" />
							</div>
						</div>
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.revision.total.amount" /></label>
							</div>
							<div class="col-md-8">
								<form:input id="transferRevisionAmount" path="transferRevisionAmount" type="text" class="form-control revisionAmount" readonly="true" />
							</div>
						</div>
					</div>
					<spring:message code="label.budget.revision.justification" var="placeRevisionJustification" />
					<div class="row">
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.conversionRate" /></label>
							</div>
							<div class="col-md-8">
								<form:input path="conversionRate" type="text"  id="conversionRate" class="form-control" data-validation="validate_custom_length positive1" data-validation-regexp="^\d{1,10}(\.\d{1,2})?$" />
							</div>
						</div>
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
							<div class="col-md-4 p-0">
								<label><spring:message code="label.budget.revision.remaining.amount" /></label>
							</div>
							<div class="col-md-8">
								<form:input id="transferRevisedRemainingAmount" path="transferRevisionRemainingAmount" type="text" class="form-control revisionAmount" readonly="true"  />
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6 marg-bottom-10 d-flex-a-center">
								<div class="col-md-4 p-0">
									<label><spring:message code="label.budget.revision.justification" /></label>
								</div>
								<div class="col-md-8">
									<form:textarea path="transferRevisionJustification" id="transferRevisionJustification" type="text" class="form-control autoSave" placeholder="${placeRevisionJustification} " data-validation="required" />
								</div>
							</div>
					</div>

					<!-- additional approver  -->
					<div class="row">
						<div class="btn-add-approve-lavel">
							<spring:message code="tooltip.add.approval.level" var="addapproval" />
							<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="transferAmountAddApprover" data-toggle="tooltip" data-placement="top" title="${addapproval}">
								<spring:message code="application.addapproval.button" />
							</button>
						</div>
						<div class="transferAmount-dynamic-approval mb-35 mt-30">
							<p class="approvalErrMsg alert-lbl-text align-center" hidden>
								<spring:message code="require.msg.add.approvals" />
							</p>
						</div>
					</div>
				</div>
				<div class="modal-footer  text-center">
					<input class="btn btn-info hvr-pop hvr-rectangle-out ph_btn_midium" id="transferBudgetSubmit" value="Save" type="submit" data-dismiss="modal">
					<button class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium cancelButtonApproval cancelTransferBudget" type="button" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</form:form>
</div>



<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});
</script>
<script>

	$('#btnBudgetAdd').click(function(event) {
		event.preventDefault();
		$('#add-popup').modal('show');

	});
	$('#addBudgetSubmit').click(function(event) {
		event.preventDefault();
		if (!$("#addBudgetForm").isValid()) {
			return false;
		}
		if ($(".approvalMandatory").is(':visible')) {
		$("#addBudgetForm").submit();
		}else{
			$(".approvalErrMsg").removeAttr('hidden');
			return false;
		}
	});
	$('#deductBudgetSubmit').click(function(event) {
		event.preventDefault();
		if (!$("#deductBudgetForm").isValid()) {
			return false;
		}
		if ($(".approvalMandatory").is(':visible')) {
			$("#deductBudgetForm").submit();
			}else{
				$(".approvalErrMsg").removeAttr('hidden');
				return false;
			}
		$("#deductBudgetForm").submit();
	});
	$('#transferBudgetSubmit').click(function(event) {
		event.preventDefault();
		if (!$("#transferBudgetForm").isValid()) {
			return false;
		}
		if ($(".approvalMandatory").is(':visible')) {
			$("#transferBudgetForm").submit();
			}else{
				$(".approvalErrMsg").removeAttr('hidden');
				return false;
			}
		$("#transferBudgetForm").submit();
	});

	$('#btnBudgetDeduct').click(function(event) {
		event.preventDefault();
		$('#deduct-popup').modal('show');

	});
	$('#btnBudgetTransfer').click(function(event) {
		event.preventDefault();
		$('#transfer-popup').modal('show');
	});

	$(document).ready(function() {
		//popup values
		$('.fromAmount').val($('#totalAmount').val());
		$('#transferRemainingAmount').val($('.remainingAmount').val());
		$('.addBudgetId').val($('#id').val());
		$('.addPopUpBudgetId').val($('#budgetId').val());
		$('.addPopUpCurrency').val($('#baseCurrency option:selected').text());
		$('.addPopUpBusinessUnit').val($('#idBusinessUnit option:selected').text());
		$('.addPopUpCostCenter').val($('#idCostCenter option:selected').text());

	});

	window.onload = function() {
		//Register onclick event for button after loading document and dom
		document.getElementById("addAmountAddApprover").addEventListener("click", function() {
		});
		document.getElementById("deductAmountAddApprover").addEventListener("click", function() {
		});
		document.getElementById("transferAmountAddApprover").addEventListener("click", function() {
		});
	}

	/* set revision amount  */
	$('#addAmount').keyup(function() {
		var addAmount=$(this).val().replace(/,/g, "");
		 addAmount = parseFloat(addAmount);
		var remainingAmountValue=parseFloat($('#remainingAmountValue').val());
		var totalAmountValue=parseFloat($('#totalAmountValue').val());
		if (!isNaN(addAmount)) {
			console.log('addAmount : ' + addAmount);
			var revisionAmount = parseFloat(addAmount) + parseFloat(totalAmountValue);
			revisionAmount=ReplaceNumberWithCommas(revisionAmount.toFixed(2));
			$('#addRevisionAmount').val(revisionAmount);
		
			var revisedRemainingAmount = parseFloat(addAmount) + parseFloat(remainingAmountValue);
			revisedRemainingAmount=ReplaceNumberWithCommas(revisedRemainingAmount.toFixed(2));
			$('#revisionRemainingAmount').val(revisedRemainingAmount);
		
		}else{
			$('#addRevisionAmount').val('');
			$('#revisionRemainingAmount').val('');
		}
	});

	$('#deductAmount').keyup(function() {
		var deductAmount=$(this).val().replace(/,/g, "");
		deductAmount = parseFloat(deductAmount);
		var remainingAmountValue=parseFloat($('#remainingAmountValue').val());
		var totalAmountValue=parseFloat($('#totalAmountValue').val());
		if (!isNaN(deductAmount)) {
			var revisionAmount = parseFloat(totalAmountValue) - parseFloat(deductAmount);
			revisionAmount=ReplaceNumberWithCommas(revisionAmount.toFixed(2));
			$('#deductRevisionAmount').val(revisionAmount);
			
			var deductedRevisedRemainingAmount = parseFloat(remainingAmountValue) - parseFloat(deductAmount);
			deductedRevisedRemainingAmount=ReplaceNumberWithCommas(deductedRevisedRemainingAmount.toFixed(2));
			$('#deductedRevisedRemainingAmount').val(deductedRevisedRemainingAmount);
		}else{
			$('#deductRevisionAmount').val('');
			$('#deductedRevisedRemainingAmount').val('');
		}
	});

	$('#transferAmount').keyup(function() {
		var transferAmount=$(this).val().replace(/,/g, "");
		transferAmount = parseFloat(transferAmount);
		var remainingAmountValue=parseFloat($('#remainingAmountValue').val());
		var totalAmountValue=parseFloat($('#totalAmountValue').val());
		if (!isNaN(transferAmount)) {
			var revisionAmount = parseFloat(totalAmountValue) - parseFloat(transferAmount);
			revisionAmount=ReplaceNumberWithCommas(revisionAmount.toFixed(2));
			$('#transferRevisionAmount').val(revisionAmount);
			
			var transferRevisedRemainingAmount = parseFloat(remainingAmountValue) - parseFloat(transferAmount);
			transferRevisedRemainingAmount=ReplaceNumberWithCommas(transferRevisedRemainingAmount.toFixed(2));
			$('#transferRevisedRemainingAmount').val(transferRevisedRemainingAmount);
		}else{
			$('#transferRevisionAmount').val('');
			$('#transferRevisedRemainingAmount').val('');
		}
	});
	function ReplaceNumberWithCommas(yourNumber) {
		// Seperates the components of the number
		var n = yourNumber.toString().split(".");
		// Comma-fies the first part
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		// Combines the two sections
		return n.join(".");
	}

	$.formUtils.addValidator({
		name : 'validate_custom_length',
		validatorFunction : function(value, $el, config, language, $form) {
			var val = value.split(".");
			if (val[0].replace(/,/g, '').length > 20) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The input value is longer than 20 number',
		errorMessageKey : 'validateLengthCustom'
	});

	$.formUtils.addValidator({
		name : 'positive1',
		required : false,
		validatorFunction : function(val, $el) {

			if (val != null && val != '')
				return Number(val.replace(/,/g, "")) >= 0;
		},
		errorMessage : 'Please Insert Correct Amount',
		errorMessageKey : 'positiveCustomValuu'
	});


	$(".cancelButtonApproval").click(function(e){
		$(".new-approval").each(function(i, v) {
		$(this).closest(".new-approval").remove();
			i++;
			$(this).attr("id", "new-approval-" + i);
			$(this).find(".level").text('Level ' + i);

			$(this).find(".approval_condition").each(function() {
				$(this).attr("name", 'approvals[' + (i - 1) + '].approvalType');
			}) // checkbox name reindex

			$(this).find("input[name='_approvals[" + i + "].approvalType']").each(function() {
				$(this).attr('name', '_approvals[' + (i - 1) + '].approvalType');
			}); //Checkbox hidden val reindex

			$(this).find(".tagTypeMultiSelect").each(function() {
				$(this).attr("name", 'approvals[' + (i - 1) + '].approvalUsers');
			}) //select name reindex

			$(this).find("input[name='_approvals[" + i + "].approvalUsers']").each(function() {
				$(this).attr("name", '_approvals[' + (i - 1) + '].approvalUsers');
			}) //select name reindex hidden
		index--;
		});
		$(".tagTypeMultiSelect").select2();
		  
	});
	$(document).ready(function() {
		
	$("#addAmountAddApprover").click(function(e) {
				e.preventDefault();
				$(".approvalErrMsg").attr('hidden',true);
				var template = '<div id="new-approval-'+index+'" class="row mt-5 new-approval approvalMandatory">';
				template += '<div class="align-right  col-md-2 "><label class="level" style="margin-top:3px;"><spring:message code="rfi.createrfi.level"/> ' + index + '</label></div>';
				template += '<div class="col-md-4" id="sel">';
				template += '<select style="margin-left: 10px;" data-validation="required" id="multipleSelectExample-' + (index - 1) + '" name="approvals[' + (index - 1)
						+ '].approvalUsers" class="level-txt tagTypeMultiSelect user-list-all chosen-select" data-placeholder=<spring:message code="createrfi.approvalroute.approvers.placeholder" /> multiple>';
				<c:forEach items="${budgetApprovalUserList}" var="users">
				<c:if test="${users.id == '-1' }">
					template += '<option value="-1" disabled >${users.name}</option>';
				</c:if>
				<c:if test="${users.id != '-1' }">
					template += '<option value="${users.id}" >${users.name}</option>';
				</c:if>
				</c:forEach>
				template += '</select></div><div class="col-md-4 pad0">';
				template += '<div class="btn-address-field"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
				template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
				template += '<ul class="dropdown-menu dropup">';
				template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="approvals[' + (index - 1)
						+ '].approvalType" checked="checked" value="OR" class="approval_condition" type="checkbox"/>&nbsp;<spring:message code="application.any" /></a></li>';
				template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="approvals[' + (index - 1)
						+ '].approvalType" value="AND" class="approval_condition"  type="checkbox"/>&nbsp;<spring:message code="buyercreation.all" /></a></li>	</ul>';
				template += '<button style="margin-left: 15px;" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out addAmountRemoveApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
				template += '</div>';
				$(".addAmount-dynamic-approval").append(template);
				$('#multipleSelectExample-'+(index - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });
				updateUserList('',$('#select#multipleSelectExample-'+(index - 1)),'ALL_USER');
				index++;
			});

	$(document).delegate('.addAmountRemoveApproval', 'click', function(e) {
		$(this).closest(".new-approval").remove();

		$(".new-approval").each(function(i, v) {
			i++;
			$(this).attr("id", "new-approval-" + i);
			$(this).find(".level").text('Level ' + i);

			$(this).find(".approval_condition").each(function() {
				$(this).attr("name", 'approvals[' + (i - 1) + '].approvalType');
			}) // checkbox name reindex

			$(this).find("input[name='_approvals[" + i + "].approvalType']").each(function() {
				$(this).attr('name', '_approvals[' + (i - 1) + '].approvalType');
			}); //Checkbox hidden val reindex

			$(this).find(".tagTypeMultiSelect").each(function() {
				$(this).attr("name", 'approvals[' + (i - 1) + '].approvalUsers');
			}) //select name reindex

			$(this).find("input[name='_approvals[" + i + "].approvalUsers']").each(function() {
				$(this).attr("name", '_approvals[' + (i - 1) + '].approvalUsers');
			}) //select name reindex hidden
		});
		index--;
	});

	$("#deductAmountAddApprover").click(function(e) {
				e.preventDefault();
				$(".approvalErrMsg").attr('hidden',true);
				var template = '<div id="new-approval-'+index+'" class="row mt-5 new-approval approvalMandatory">';
				template += '<div class="align-right col-md-2 "><label class="level" style="margin-top:3px;"><spring:message code="rfi.createrfi.level"/> ' + index + '</label></div>';
				template += '<div class="col-md-4" id="sel">';
				template += '<select style="margin-left: 10px;" data-validation="required" id="multipleSelectExample-' + (index - 1) + '" name="approvals[' + (index - 1)
						+ '].approvalUsers" class="level-txt tagTypeMultiSelect user-list-all chosen-select" data-placeholder=<spring:message code="createrfi.approvalroute.approvers.placeholder" /> multiple>';
				<c:forEach items="${budgetApprovalUserList}" var="users">
				<c:if test="${users.id == '-1' }">
					template += '<option value="-1" disabled >${users.name}</option>';
				</c:if>
				<c:if test="${users.id != '-1' }">
					template += '<option value="${users.id}" >${users.name}</option>';
				</c:if>
				</c:forEach>
				template += '</select></div><div class="col-md-4 pad0">';
				template += '<div class="btn-address-field"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
				template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
				template += '<ul class="dropdown-menu dropup">';
				template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="approvals[' + (index - 1)
						+ '].approvalType" checked="checked" value="OR" class="approval_condition" type="checkbox"/>&nbsp;<spring:message code="application.any" /></a></li>';
				template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="approvals[' + (index - 1)
						+ '].approvalType" value="AND" class="approval_condition"  type="checkbox"/>&nbsp;<spring:message code="buyercreation.all" /></a></li>	</ul>';
				template += '<button style="margin-left: 15px;" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out deductAmountRemoveApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
				template += '</div>';

				$(".deductAmount-dynamic-approval").append(template);
				$('#multipleSelectExample-'+(index - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });
				updateUserList('',$('#select#multipleSelectExample-'+(index - 1)),'ALL_USER');
				index++;
			});

	$(document).delegate('.deductAmountRemoveApproval', 'click', function(e) {
		$(this).closest(".new-approval").remove();

		$(".new-approval").each(function(i, v) {
			i++;
			$(this).attr("id", "new-approval-" + i);
			$(this).find(".level").text('Level ' + i);

			$(this).find(".approval_condition").each(function() {
				$(this).attr("name", 'approvals[' + (i - 1) + '].approvalType');
			}) // checkbox name reindex

			$(this).find("input[name='_approvals[" + i + "].approvalType']").each(function() {
				$(this).attr('name', '_approvals[' + (i - 1) + '].approvalType');
			}); //Checkbox hidden val reindex

			$(this).find(".tagTypeMultiSelect").each(function() {
				$(this).attr("name", 'approvals[' + (i - 1) + '].approvalUsers');
			}) //select name reindex

			$(this).find("input[name='_approvals[" + i + "].approvalUsers']").each(function() {
				$(this).attr("name", '_approvals[' + (i - 1) + '].approvalUsers');
			}) //select name reindex hidden
		});
		index--;
	});

	$("#transferAmountAddApprover").click(function(e) {
				e.preventDefault();
				$(".approvalErrMsg").attr('hidden',true);
				var template = '<div id="new-approval-'+index+'" class="row mt-5 new-approval approvalMandatory">';
				template += '<div class="align-right col-md-2 "><label class="level" style="margin-top:3px;"><spring:message code="rfi.createrfi.level"/> ' + index + '</label></div>';
				template += '<div class="col-md-4" id="sel">';
				template += '<select style="margin-left: 10px;" data-validation="required" id="multipleSelectExample-' + (index - 1) + '" name="approvals[' + (index - 1)
						+ '].approvalUsers" class="level-txt tagTypeMultiSelect user-list-all chosen-select" data-placeholder=<spring:message code="createrfi.approvalroute.approvers.placeholder" /> multiple>';
				<c:forEach items="${budgetApprovalUserList}" var="users">
				<c:if test="${users.id == '-1' }">
					template += '<option value="-1" disabled >${users.name}</option>';
				</c:if>
				<c:if test="${users.id != '-1' }">
					template += '<option value="${users.id}" >${users.name}</option>';
				</c:if>
				</c:forEach>
				template += '</select></div><div class="col-md-4 pad0">';
				template += '<div class="btn-address-field"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
				template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
				template += '<ul class="dropdown-menu dropup">';
				template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="approvals[' + (index - 1)
						+ '].approvalType" checked="checked" value="OR" class="approval_condition" type="checkbox"/>&nbsp;<spring:message code="application.any" /></a></li>';
				template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="approvals[' + (index - 1)
						+ '].approvalType" value="AND" class="approval_condition"  type="checkbox"/>&nbsp;<spring:message code="buyercreation.all" /></a></li>	</ul>';
				template += '<button style="margin-left: 15px;" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out transferAmountRemoveApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
				template += '</div>';

				$(".transferAmount-dynamic-approval").append(template);
				$('#multipleSelectExample-'+(index - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });
				updateUserList('',$('#select#multipleSelectExample-'+(index - 1)),'ALL_USER');
				index++;
			});

	$(document).delegate('.transferAmountRemoveApproval', 'click', function(e) {
		$(this).closest(".new-approval").remove();

		$(".new-approval").each(function(i, v) {
			i++;
			$(this).attr("id", "new-approval-" + i);
			$(this).find(".level").text('Level ' + i);

			$(this).find(".approval_condition").each(function() {
				$(this).attr("name", 'approvals[' + (i - 1) + '].approvalType');
			}) // checkbox name reindex

			$(this).find("input[name='_approvals[" + i + "].approvalType']").each(function() {
				$(this).attr('name', '_approvals[' + (i - 1) + '].approvalType');
			}); //Checkbox hidden val reindex

			$(this).find(".tagTypeMultiSelect").each(function() {
				$(this).attr("name", 'approvals[' + (i - 1) + '].approvalUsers');
			}) //select name reindex

			$(this).find("input[name='_approvals[" + i + "].approvalUsers']").each(function() {
				$(this).attr("name", '_approvals[' + (i - 1) + '].approvalUsers');
			}) //select name reindex hidden
		});
		index--;
	});
	});
</script>

<script>
function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}
var index;
index = ${fn:length(budgetPojo.approvals) + 1};
$(document).ready(function() {
	$(document).delegate('.small', 'click', function(e) {
		$(this).find(".approval_condition").trigger("click");	
	});
	$(".tagTypeMultiSelect").select2();

	$("#addSelect").click(function(e) {
		e.preventDefault();
		var template = '<div id="new-approval-'+index+'" class="row new-approval test">';
		template += '<div class="align-right col-md-3 "><label class="level"  ><spring:message code="rfi.createrfi.level"/> ' + index + '</label></div>';
		template += '<div class="col-md-5" id="sel">';
		template += '<select style="margin-left: 7px;" data-validation="required" id="multipleSelectExample-'+(index - 1)+'" name="approvals[' + (index - 1) + '].approvalUsers" class="level-txt tagTypeMultiSelect" data-placeholder=<spring:message code="createrfi.approvalroute.approvers.placeholder" /> multiple>';
		<c:forEach items="${budgetApprovalUserList}" var="users">
		<c:if test="${users.id == '-1' }">
		template += '<option value="-1" disabled >${users.name}</option>';
		</c:if>
		<c:if test="${users.id != '-1' }">
		template += '<option value="${users.id}" >${users.name}</option>';
		</c:if>
		</c:forEach>
		template += '</select></div><div class="col-md-4 pad0">';
		template += '<div style="position: relative;left: -69px;" class="btn-address-field"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
		template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
		template += '<ul class="dropdown-menu dropup">';
		template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="approvals[' + (index - 1) + '].approvalType" checked="checked" value="OR" class="approval_condition" type="checkbox"/>&nbsp;<spring:message code="application.any" /></a></li>';
		template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="approvals[' + (index - 1) + '].approvalType" value="AND" class="approval_condition"  type="checkbox"/>&nbsp;<spring:message code="buyercreation.all" /></a></li>	</ul>';
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
	
	
	$(".cancelAddBudget").click(function(){
		$(".hasSuccessAdd").find("div").removeClass('has-success');
		$("#addAmount").val('');
		$("#addRevisionAmount").val('');
		$("#addRevisionJustification").val('');
		$("#revisionRemainingAmount").val('');
	});

	$(".cancelTransferBudget").click(function(){
		$(".hasSuccessTransfer").find("div").removeClass('has-success')
		$("#transferAmount").val('');
		$("#transferRevisionAmount").val('');
		$("#conversionRate").val('');
		$("#transferRevisedRemainingAmount").val('');
		$("#transferRevisionJustification").val('');
		$("#idToBusinessUnit").val('');
		$('#idToBusinessUnit').trigger("chosen:updated");
		$("#toCostCenter").val('');
		$('#toCostCenter').trigger("chosen:updated");

	});

	$(".cancelDeductBudget").click(function(){
		$(".hasSuccessDeduct").find("div").removeClass('has-success')
		$("#deductAmount").val('');
		$("#deductRevisionAmount").val('');
		$("#deductRevisionJustification").val('');
		$("#deductedRevisedRemainingAmount").val('');
	});

});

</script>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
