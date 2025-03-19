<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">


<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />

<style>
.ph_btn_custom {
	height: 40px !important;
	min-width: 170px !important;
	font-size: 16px !important;
	line-height: 39px;
	font-weight: 500;
}

.btnEditApproval {
	background: none;
	border: none;
	outline: none;
}
</style>
<div class="clear"></div>
<div class="white-bg border-all-side float-left width-100 pad_all_15">
	<div class="row">
		<div class="col-md-6 col-sm-12 col-xs-12">
			<input type="hidden" id="prId" value="${sourcingFormRequest.id}">
			<div class="tag-line">

				<h2>
					<spring:message code="rfs.summary.request.creator" />
					: ${sourcingFormRequest.createdBy.name} / ${sourcingFormRequest.createdBy.communicationEmail}
				</h2>
			</div>
		</div>
		<div class="col-md-6 col-sm-12 col-xs-12">
			<div class="pull-right tag-line-right">
				<h2>
					<spring:message code="rfs.summary.request.template" />
					: ${sourcingFormRequest.sourcingFormName != null ? sourcingFormRequest.sourcingFormName : 'N/A'}
				</h2>
			</div>
		</div>
	</div>

	<div class="pull-right">
		<form:form action="${pageContext.request.contextPath}/buyer/downlaodSourcingSummary/${sourcingFormRequest.id}" method="GET">
			<button class="btn btn-sm float-right btn-info hvr-pop marg-left-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.rfssummary.download.summary" />'>
				<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
				</span> <span class="button-content"><spring:message code="rfssummary.summary.label" /></span>
			</button>
		</form:form>
	</div>
</div>
<div class="clear"></div>
<div class="tab-pane" style="display: block">
	<div class="upload_download_wrapper clearfix event_info">
		<h4>
			<spring:message code="prsummary.general.information" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="buyer.dashboard.sourcing.requestid" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${sourcingFormRequest.formId}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="sourcing.reference.number" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${sourcingFormRequest.referanceNumber}</p>
			</div>
		</div>

		<%-- <div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="application.date" /> &amp; <spring:message code="application.time" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<fmt:formatDate value="${sourcingFormRequest.createdDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				</p>
			</div>
		</div> --%>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="sourcing.creator" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					${sourcingFormRequest.createdBy.name} <br> ${sourcingFormRequest.createdBy.communicationEmail} <br>
					<c:if test="${not empty sourcingFormRequest.createdBy.phoneNumber}">HP: ${ sourcingFormRequest.createdBy.phoneNumber}</c:if>
				</p>
			</div>
		</div>

		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="sourcing.description" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${sourcingFormRequest.description}</p>
			</div>
		</div>
		<!-- <div class="form-tander1 requisition-summary-box"> -->
	</div>

	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="sourcing.procurement.information" />
		</h4>
		<div id="beep" class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="procurement.info.method" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${not empty sourcingFormRequest.procurementMethod.procurementMethod ? sourcingFormRequest.procurementMethod.procurementMethod:' ' }</p>
			</div>
		</div>
		<div id="beep" class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="procurement.info.category" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${not empty sourcingFormRequest.procurementCategories.procurementCategories ?sourcingFormRequest.procurementCategories.procurementCategories:' ' }</p>
			</div>
		</div>
	</div>


	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="rfs.summary.finance.information" />
		</h4>
		<c:if test="${not empty sourcingFormRequest.currency}">
			<div id="beep" class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="eventdescription.basecurrency.label" /> :
					</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>${not empty sourcingFormRequest.currency.currencyCode ?sourcingFormRequest.currency.currencyCode:' ' }</p>
				</div>
			</div>
		</c:if>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.decimal.label" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${sourcingFormRequest.decimal}</p>
			</div>
		</div>

		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="rfs.availableBudget.label" /> :</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<fmt:formatNumber type="number" minFractionDigits="${sourcingFormRequest.decimal}" maxFractionDigits="${sourcingFormRequest.decimal}" value="${sourcingFormRequest.budgetAmount}" />
				</p>
				<c:if test="${empty sourcingFormRequest.budgetAmount}">
					<p>-</p>
				</c:if>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="rfs.estimatedBudget.label" /> :</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<fmt:formatNumber type="number" minFractionDigits="${sourcingFormRequest.decimal}" maxFractionDigits="${sourcingFormRequest.decimal}" value="${sourcingFormRequest.estimatedBudget}" />
				</p>
				<c:if test="${empty sourcingFormRequest.estimatedBudget}">
					<p>-</p>
				</c:if>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="rfs.historicAmount.label" /> :</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<fmt:formatNumber type="number" minFractionDigits="${sourcingFormRequest.decimal}" maxFractionDigits="${sourcingFormRequest.decimal}" value="${sourcingFormRequest.historicaAmount}" />
				</p>
				<c:if test="${empty sourcingFormRequest.historicaAmount}">
					<p>-</p>
				</c:if>
			</div>
		</div>

		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="sourcing.minimumSupplierRating" />:</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${not empty sourcingFormRequest.minimumSupplierRating?sourcingFormRequest.minimumSupplierRating:'N/A'}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="sourcing.maximumSupplierRating" />:</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${not empty sourcingFormRequest.maximumSupplierRating?sourcingFormRequest.maximumSupplierRating:'N/A'}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="sourcing.businessUnit" /> </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${not empty sourcingFormRequest.businessUnit ? sourcingFormRequest.businessUnit.unitName : '-'}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="sourcing.CostCenter" /> </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${not empty sourcingFormRequest.costCenter.costCenter ?sourcingFormRequest.costCenter.costCenter:' ' }-${not empty sourcingFormRequest.costCenter.description ?sourcingFormRequest.costCenter.description:' '}</p>
			</div>
		</div>

		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="rfs.Group.code" /> :</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${not empty sourcingFormRequest.groupCode ? sourcingFormRequest.groupCode.groupCode: (not empty sourcingFormRequest.groupCodeOld ? sourcingFormRequest.groupCodeOld : '-')}-${not empty sourcingFormRequest.groupCode ? sourcingFormRequest.groupCode.description : ' '}</p>
			</div>
		</div>


	</div>

	<%-- <div class="panel sum-accord" style="margin-bottom: 5px; margin-top: 6px;">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" data-parent="#accordion" class="accordion" href="#collapseThree"><spring:message code="rfs.documents" /></a>
			</h4>
		</div>
		<div id="collapseThree" class="panel-collapse collapse">
			<div class="panel-body">
				<div class="table-responsive width100 borderAllData">
					<table class="tabaccor padding-none-td table" width="100%" cellpadding="0" cellspacing="0" border="0">
						<thead>
							<tr>
								<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="rfs.documents.name" /></th>
								<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="rfs.documents.description" /></th>
								<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="rfs.documents.date&time" /></th>
								<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="rfs.documents.size" /></th>
							</tr>
						</thead>
						<tbody>
							<c:if test="${fn:length(listDocs) == 0}">
								<td valign="top" colspan="4" class="dataTables_empty">
									<spring:message code="application.nodata" />
								</td>
							</c:if>
							<c:if test="${!empty listDocs}">
								<c:forEach var="listDoc" items="${listDocs}">
									<tr>
										<td class="width_200 width_200_fix align-left wo-rp">
											<a class="bluelink" href="${pageContext.request.contextPath}/buyer/downloadRfsDocument/${listDoc.id}"> ${listDoc.fileName}</a>
										</td>
										<td class="width_200 width_200_fix align-left wo-rp">${listDoc.description}</td>
										<td class="width_100 width_100_fix align-left wo-rp">
											<fmt:formatDate value="${listDoc.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										</td>
										<td class="width_100 width_100_fix align-left wo-rp">${listDoc.fileSizeInKb}KB</td>
									</tr>
								</c:forEach>
							</c:if>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div> --%>

	<jsp:include page="uploadDocument.jsp" />
	<jsp:include page="requestSummaryCq.jsp" />
	<jsp:include page="requestSummaryBq.jsp" />
	<jsp:include page="requestSummarySor.jsp" />


	<c:if test="${not empty sourcingFormRequest.sourcingFormApprovalRequests}">
		<div class="upload_download_wrapper clearfix event_info Approval-tab " style="margin-top: -1%;">

			<h4>
				<spring:message code="rfs.reqsummary.approvals.route1" />
				<c:if test="${eventPermissions.owner and sourcingFormRequest.status == 'PENDING'}">
					<button class="editApprovalPopupButton btnEditApproval sixbtn" data-toggle="dropdown" title="Edit">
						<img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" alt="edit" />
					</button>
				</c:if>
			</h4>
			<div id="apprTab" class="pad_all_15 collapse in float-left width-100 position-relative in">
				<c:if test="${sourcingFormRequest.enableApprovalReminder}">
					<div>
						<div class="row">
							<label style="margin-left: 17px;">Reminder Settings: </label>
						</div>
						<div class="marg-left-10">
							<p>Reminder emails sent every ${sourcingFormRequest.reminderAfterHour} hours.</p>
							<p>Maximum ${sourcingFormRequest.reminderCount} reminder emails.</p>
							<c:if test="${sourcingFormRequest.notifyEventOwner}">
								<p>
									<spring:message code="rfs.notification.owner.message" />
								</p>
							</c:if>
						</div>
					</div>
				</c:if>
				<c:set var="currentBatch" value="0" />
				<c:forEach items="${sourcingFormRequest.sourcingFormApprovalRequests}" var="approval">
					<c:if test="${approval.batchNo == 0}">
						<div class="pad_all_15 float-left appr-div position-relative">
							<label>Level ${approval.level}</label>
							<c:if test="${approval.active}">
								<div class="color-green marg-left-10">
									&nbsp;&nbsp;&nbsp; <i class="fa fa-arrow-left color-green" aria-hidden="true"></i>&nbsp;&nbsp;[Active]
								</div>
							</c:if>
							<div class="Approval-lavel1-upper">
								<c:forEach items="${approval.approvalUsersRequest}" var="user" varStatus="status">
									<div class="Approval-lavel1 ${user.approvalStatus eq 'REJECTED' ? 'cross-red-mark' : ''} ${user.approvalStatus eq 'APPROVED' ? 'right-green-mark' : ''}">${user.user.name}
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
								<c:forEach items="${approval.approvalUsersRequest}" var="user" varStatus="status">
									<div class="Approval-lavel1 ${user.approvalStatus eq 'REJECTED' ? 'cross-red-mark' : ''} ${user.approvalStatus eq 'APPROVED' ? 'right-green-mark' : ''}">${user.user.name}
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
					</c:if>

				</c:forEach>
			</div>
			<div class="clear"></div>
			<c:if test="${not empty sourcingFormRequest.requestComments}">
				<div class="remark-tab pad0">
					<h4>
						<spring:message code="summarydetails.approval.comments" />
					</h4>
					<div class="pad_all_15 float-left width-100">
						<c:forEach items="${sourcingFormRequest.requestComments}" var="comment">
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
			<c:if test="${sourcingFormRequest.status eq 'PENDING' and eventPermissions.activeApproval}">
				<div class="form_field">
					<form id="approvedRejectForm" method="post">
						<div class="row">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="requestId" id="requestId" value="${sourcingFormRequest.id}">


							<div class="col-sm-12 col-md-12 align-right col-xs-12 ">
								<label class="control-label"><spring:message code="label.meeting.remark" /> : </label>
							</div>
							<div class="col-sm-5 col-md-5 col-xs-12 ">
								<textarea name="remarks" id="approvalremarks" rows="4" data-validation="length" data-validation-length="0-450" class="form-control"></textarea>
							</div>

						</div>
						<div class="row">
							<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20 marg-bottom-20">
								<button type="button" id="sourcingapprovedButton" class="btn btn-info ph_btn hvr-pop marg-left-10 hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">Approve</button>
								<button type="button" id="sourcingrejectedButton" class="btn btn-black ph_btn hvr-pop marg-left-10 hvr-rectangle-out1  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">Reject</button>
							</div>
						</div>
					</form>
				</div>
			</c:if>

		</div>
	</c:if>

	<c:if test="${sourcingFormRequest.status eq 'APPROVED' && sourcingFormRequest.addAdditionalApprovals and (!eventPermissions.editor || (eventPermissions.editor and eventPermissions.owner))}">
		<form:form class="bordered-row" id="demo-form1" method="post" modelAttribute="sourcingFormRequest" action="${pageContext.request.contextPath}/buyer/saveAdditionalApproval/${sourcingFormRequest.id}">
			<%-- 	<c:if test="${sourcingFormRequest.status eq 'FINISHED'}"> --%>
			<div class="panel sum-accord " style="margin-top: 0%;">
				<div class="panel-heading">
					<h4 class="panel-title">
						<a data-toggle="collapse" class="accordion" href="#collapseApproval"> <spring:message code="additional.approvals.route" />
						</a>
					</h4>
				</div>
				<div id="collapseApproval" class="panel-collapse collapse">
					<div class="dynamic-Add-approval col-md-6">
						<div class="row cloneready">
							<c:if test="${!isTemplateUsed}">
								<div class="col-md-3 col-sm-3">
									<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="addAddiApproval" data-placement="top" style="margin-top: 10px;">
										<spring:message code="add.approval.level" />
									</button>
								</div>
							</c:if>
						</div>
						<!-- For LOOP -->
						<c:set var="doneUsers" value="" />
						<c:set var="doneLevel" value="" />
						<div class="approVAlRouteBox">
							<c:forEach items="${sourcingFormRequest.sourcingFormApprovalRequests}" var="approval" varStatus="status">
								<c:if test="${approval.done}">
									<%-- <form:hidden path="sourcingFormApprovalRequests[${status.index}].sourcingFormRequest.id" /> --%>
									<form:hidden path="sourcingFormApprovalRequests[${status.index}].id" />
									<c:forEach items="${sourcingFormRequest.sourcingFormApprovalRequests[status.index].approvalUsersRequest}" var="usr" varStatus="usrIndex">
										<input type="hidden" name="sourcingFormApprovalRequests[${status.index}].approvalUsersRequest" value="${usr.user.id}" />
									</c:forEach>

								</c:if>
								<c:if test="${!approval.done}">
									<div id="addnew-approval-${status.index + 1}" class="row new-approval addnew-approval">
										<c:if test="${approval.active}">
											<c:forEach items="${sourcingFormRequest.sourcingFormApprovalRequests[status.index].approvalUsersRequest}" var="usr">
												<c:if test="${usr.approvalStatus != 'PENDING' }">
													<c:set var="doneUsers" value="${doneUsers.concat(\"'\").concat(usr.user.id).concat(\"',\") }" />
													<c:set var="doneLevel" value="${sourcingFormRequest.sourcingFormApprovalRequests[status.index].id}" />
												</c:if>
											</c:forEach>

										</c:if>
										<div class="col-md-2 col-sm-2 mb-0">
											<label class="level">Level ${approval.level }</label>
										</div>
										<div class="col-md-7 col-sm-7" id="sel">
											<form:hidden class="addapproval_id_hidden" path="sourcingFormApprovalRequests[${status.index}].id" />
											<form:hidden class="addlevel_hidden" path="sourcingFormApprovalRequests[${status.index}].level" />
											<span class="dropUp"> <form:select autocomplete="off" path="sourcingFormApprovalRequests[${status.index}].approvalUsersRequest" cssClass="addtagTypeMultiSelect user-list-all chosen-select tagTypeMultiSelect" data-validation="required" id="multipleSelectExample-${status.index}" data-placeholder="Approvers" multiple="multiple">
													<c:forEach items="${userList1}" var="user">
														<c:if test="${user.id != '-1' }">
															<form:option value="${user.id}">${user.name}</form:option>
														</c:if>
														<c:if test="${user.id == '-1' }">
															<form:option value="-1" disabled="true">${user.name}</form:option>
														</c:if>
													</c:forEach>
												</form:select>
											</span>
										</div>
										<div class="col-sm-2 col-md-3 col-xs-3 pad0">
											<div class="btn-address-field pt-0">
												<button type="button" class="btn dropdown-toggle" data-toggle="dropdown">
													<i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
												</button>
												<ul class="dropdown-menu dropup">
													<li><a href="javascript:void(0);" class="small addsmall" tabIndex="-1"> <form:checkbox path="sourcingFormApprovalRequests[${status.index}].approvalType" value="OR" cssClass="approval_condition addapproval_condition" label="Any"></form:checkbox>
													</a></li>
													<li><a href="javascript:void(0);" class="small addsmall" tabIndex="-1"> <form:checkbox path="sourcingFormApprovalRequests[${status.index}].approvalType" value="AND" cssClass="approval_condition addapproval_condition" label="All"></form:checkbox>
													</a></li>
												</ul>
												<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval addremoveApproval ${(approval.active || approval.done) ? 'disabled':''}">
													<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
												</button>
											</div>
										</div>
									</div>
								</c:if>

							</c:forEach>
						</div>
					</div>


					<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 ">
						<button type="submit" id="saveAdditionalApproval" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out" title="Save">
							<spring:message code="application.save" />
						</button>
						<button id="finishAdditionalApproval" type="button" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out" title="Finish">
							<spring:message code="application.finish" />
						</button>


					</div>
				</div>
		</form:form>
	</c:if>
</div>
<%-- 	</c:if> --%>
<jsp:include page="/WEB-INF/views/jsp/sourcingForm/summarySourcingTeamMembers.jsp"></jsp:include>
<%--  <c:if test="${sourcingFormRequest.status ne 'DRAFT' && sourcingFormRequest.addAdditionalApprovals}">
	<jsp:include page="approvalDocument.jsp" />
</c:if>  --%>
<jsp:include page="sourcingRequest.jsp" />
</div>
<c:if test="${sourcingFormRequest.status eq 'DRAFT'}">
	<div class="row">
		<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20">
			<c:url var="prRemark" value="/buyer/prRemark/${sourcingFormRequest.id}" />
			<c:if test="${empty viewMode}">
				<a href="${pageContext.request.contextPath}/buyer/createSourcingRequestSorList/${sourcingFormRequest.id }" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1" id="previousButton"><spring:message code="application.previous" /></a>
			</c:if>
			<c:if test="${sourcingFormRequest.status eq 'DRAFT' and eventPermissions.owner}">
				<c:url var="finishRequest" value="/buyer/finishRequest/${sourcingFormRequest.id }" />
				<a href="${finishRequest}" id="finishRequest" class="btn btn-info ph_btn hvr-pop marg-left-10 right-header-button hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }"><spring:message code="application.finish" /></a>
			</c:if>

			<c:if test="${(isAdmin or eventPermissions.owner)}">
				<a href="#confirmCancelRequest" role="button" class="btn btn-danger ph_btn hvr-pop pull-right" data-toggle="modal"><spring:message code="rfs.summary.cancel.request" /></a>

			</c:if>
		</div>
	</div>
</c:if>
<div class="event_form">
	<input id="id" type="hidden" value="${sourcingFormRequest.id}">
</div>
<!-- cancel Event popup  -->
<div class="modal fade" id="confirmCancelRequest" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form  action="${pageContext.request.contextPath}/buyer/cancelSourcingReq/${sourcingFormRequest.id}" method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="request.confirm.to.cancel" /> </br>
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="reasonCancel" />
							<textarea name="description" class="width-100" placeholder="${reasonCancel}" id="reasonCancel" rows="3" data-validation="required length" data-validation-length="max500" ></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="submit" id="RfsCancelRequest" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
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
<!-- ADD TEAM MEMBER Dialog -->
<div class="flagvisibility dialogBox" id="teamMemberListPopup" title="Sourcing Team Members">
	<div class="float-left width100 pad_all_15 white-bg ${!empty readOnlyTeamMember and readOnlyTeamMember ?'disabled':''  }">
		<div class="form_field">
			<div class="form-group ">
				<div class="pull-left width100">
					<div class="col-md-12 marginBottomA">
						<div class="ia-invite-controls-area">
							<div class="group">
								<div class="input-group mrg15T mrg15B">
									<select id="TeamMemberList" class="chosen-select" name="approverList1" cssClass="form-control chosen-select">
										<option value=""><spring:message code="prsummary.select.team.member" /></option>
										<c:forEach items="${userTeamMemberList}" var="TeamMember">
											<option value="${TeamMember.id}">${TeamMember.name}</option>
										</c:forEach>
									</select>
									<div class="input-group-btn">
										<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
											<span class="glyphicon glyphicon-eye-open"></span>
										</button>
										<ul class="dropdown-menu dropup">
											<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="access_editor" value="Editor" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.editor" />
											</a></li>
											<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="access_viewer" value="Viewer" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.viewer" />
											</a></li>
											<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="access_Associate_Owner" value="Associate_Owner" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.associate.owner" />
											</a></li>
										</ul>
										<button class="btn btn-primary addTeamMemberToList" type="button">+</button>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-12 col-sm-12 col-xs-12 margeinAllMDZero">
						<div class="mem-tab">
							<table cellpading="0" cellspacing="0" width="100%" id="appUsersList" class="table-hover table">
								<c:forEach items="${sourcingFormRequest.sourcingFormTeamMember}" var="teamMembers">
									<tr data-username="${teamMembers.user.name}" approver-id="${teamMembers.user.id}">
										<td class="width_50_fix ">
											<!--	<img src='<c:url value="/resources/images/profile_setting_image.png"/>' alt="profile " /> -->
										</td>
										<td>${teamMembers.user.name}<br> <span>${teamMembers.user.loginId}</span>
										</td>
										<td class="edit-drop">
											<div class="advancee_menu">
												<div class="adm_box">
													<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"> <i class="glyphicon ${teamMembers.teamMemberType=='Editor'?'glyphicon-pencil':teamMembers.teamMemberType=='Viewer'?'glyphicon-eye-open':teamMembers.teamMemberType=='Associate_Owner'?'fa fa-user-plus':''} " aria-hidden="true"
														title="${teamMembers.teamMemberType=='Editor'?editorLabel:teamMembers.teamMemberType=='Viewer'?viewerLabel:teamMembers.teamMemberType=='Associate_Owner'?associateOwnerLabel:''}"></i>
													</a>
													<ul class="dropdown-menu dropup">
														<li><a href="javascript:void(0);" class="small" tabIndex="-1"> <input data-uid="${teamMembers.user.id}" id="${teamMembers.user.id}-Editor" ${teamMembers.teamMemberType=='Editor' ? 'checked' : ''} value="Editor" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.editor" />
														</a></li>
														<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="${teamMembers.user.id}-Viewer" ${teamMembers.teamMemberType=='Viewer'?'checked': ''} value="Viewer" data-uid="${teamMembers.user.id}" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.viewer" />
														</a></li>
														<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="${teamMembers.user.id}-Associate_Owner" ${teamMembers.teamMemberType=='Associate_Owner'?'checked': ''} value="Associate_Owner" data-uid="${teamMembers.user.id}" class="access_check" type="checkbox" /> &nbsp;<spring:message
																	code="eventsummary.checkbox.associate.owner" />
														</a></li>
													</ul>
												</div>
											</div>
										</td>
										<td>
											<div class="cqa_del">
												<a href="javascript:void(0);" list-type="Team Member" class="removeApproversList"><spring:message code="tooltip.delete" /></a>
											</div>
										</td>
									</tr>
								</c:forEach>
							</table>
						</div>
					</div>
					<div class="col-md-12 d-flex margeinAllMDZero">
						<a class="center-btn closeDialog btn btn-black pull-right hvr-pop ph_btn_small hvr-rectangle-out1" style="margin-right: 45%;" href="javascript:void(0);" onclick="window.location.reload();"> <spring:message code="eventReport.cls" />
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Edit Approval Dialog -->
<%-- <div class="flagvisibility dialogBox" id="editApprovalPopup" title="Edit Approval">
	<div class="float-left width100 pad_all_15 white-bg">
		<c:url value="/buyer/updatePrApproval" var="updatePrApproval" />
		<form:form id="prSummaryForm" action="${updatePrApproval}" method="post" modelAttribute="sourcingFormRequest">
			<form:hidden path="id" />
			<jsp:include page="/WEB-INF/views/jsp/sourcingForm/sourcingFormApproval.jsp"></jsp:include>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center">
						<button type="reset" id="resetEventContctForm" style="display: none;"></button>
						<button type="submit" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out">
							<spring:message code="application.update" />
						</button>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div> --%>


<!--Team Member  popup-->
<div class="set-title flagvisibility dialogBox" id="removeApproverListPopup" title='<spring:message code="tooltip.remove.team.member" />'>
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="approverListId" name="approverListId" value=""> <input type="hidden" id="approverListType" name="approverListType" value="">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock">
				<spring:message code="prtemplate.sure.want.remove" />
				<span></span>
				<spring:message code="application.from" />
				<span></span>
				<spring:message code="application.list" />
				?
			</p>
		</div>
		<div class="event_form">
			<input id="id" type="hidden" value="${sourcingFormRequest.id}">
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeApproverListPerson" data-original-title='<spring:message code="tooltip.delete" />'><spring:message code="application.delete" /></a>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>
<!--  EDIT APPROVAL POPUP -->
<div class="flagvisibility dialogBox" id="editApprovalPopup" title="Edit Approval">
	<div class="float-left width100 pad_all_15 white-bg">
		<c:url value="/buyer/sourcingFormRequest/updateSourcingFormRequestApproval" var="updateSourcingFormRequestApproval" />
		<form:form id="eventSummaryForm" action="${updateSourcingFormRequestApproval}" method="post" modelAttribute="sourcingFormRequest">
			<form:hidden path="id" />
			<jsp:include page="/WEB-INF/views/jsp/sourcingFormRequest/sourcingFormRequestEditApproval.jsp"></jsp:include>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center marg-top-20">
						<button type="reset" id="resetEventContctForm" style="display: none;"></button>
						<button type="submit" id="submitUpdateApproval" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out">
							<spring:message code="application.update" />
						</button>
						<button type="button" id="cancelUpdateApproval" class="closeDialog closeEditPopUp btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>

<style>
.set-title {
	position: unset !important;
	margin-top: 50px !important;
}

.mb-0 label {
	margin-bottom: 0 !important;
}

.row.new-approval {
	margin-bottom: 5px !important;
}

.pt-0 {
	padding-top: 1px !important;
	padding-left: 10px;
}

.width-90 {
	width: 90%;
}

.editTeamMemberList {
	margin-left: 50px;
}

div.radio[id^='uniform-']>span {
	margin-top: 0 !important;
}

label.select-radio {
	width: auto;
}

#event {
	padding-left: 0;
}

.input-group.mrg15T.mrg15B {
	background-color: #f5f5f5;
	margin-bottom: 0;
	padding: 0;
}

.margeinAllMDZero {
	margin: 0;
	clear: both;
}

.marginBottomA {
	margin-bottom: 20px;
}

#appUsersList td {
	padding: 5px;
}

.mem-tab {
	border: 1px solid #ddd;
	border-radius: 2px;
	float: left;
	height: 300px;
	overflow: auto;
	position: relative;
	width: 100%;
}

.box_active {
	background: rgba(0, 0, 0, 0) none repeat scroll 0 0 !important;
}

.caret {
	color: #fff !important;
}

.cq_row_parent .input-group-btn {
	width: 200px;
}

.dropdown-menu input {
	display: inline !important;
	width: auto !important;
}

.advancee_menu ul {
	top: 100% !important;
	left: 0 !important;
}

.dropdown-menu .checkbox {
	display: inline-block !important;
	margin: 0 !important;
	padding: 0 !important;
	position: relative !important;
}

.dropdown-menu .checkbox input[type="checkbox"] {
	position: relative !important;
	margin-left: 0 !important;
}

.open>.dropdown-menu {
	padding-bottom: 17px;
	padding-top: 0;
}

#appUsersList tr:nth-child(odd) {
	background: #FFF
}

#appUsersList tr:nth-child(even) {
	background: #CCC
}

#eventTeamMembersList td, #eventTeamMembersList th {
	text-align: left !important;
	max-width: 100px !important;
}

#eventTeamMembersList {
	margin: 0 !important;
}

#eventTeamMembersList_length, #eventTeamMembersList_info,
	#eventTeamMembersList_paginate {
	display: none;
}

.dataTables_wrapper.form-inline.no-footer {
	overflow: hidden;
}

.editTeamMemberList {
	margin-left: 50px;
}

.grand-price-heading {
	width: 250px;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting_asc::after {
	content: "" !important;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting::after {
	content: "" !important
}

.inactiveCaption {
	margin: 0 0px 0 0px !important;
	font-weight: bold !important;
	color: #ff1d33 !important;
}

.white-space-pre {
	white-space: pre;
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
	padding: 0;
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

.approvalcount {
	margin-top: 3%;
}
</style>


<script>
$('.closeEditPopUp').click(function(e) {
	e.preventDefault();
	var id = $('#prId').val();
	var url = getContextPath() + '/buyer/viewSourcingSummary/'+ id;
	window.location = url;
});

	$('#sourcingapprovedButton').click(function(e) {
		e.preventDefault();
		if($('#approvedRejectForm').isValid()) {
			$(this).addClass('disabled');
			$('#sourcingrejectedButton').addClass('disabled');
		} else {
			return;
		}
		console.log("approved");
		$('#approvedRejectForm').attr('action', getContextPath() + "/buyer/requestApproved");
		$('#approvedRejectForm').submit();

	});
	$('#submitUpdateApproval').click(function(e) {
		e.preventDefault();
		if($('#eventSummaryForm').isValid()) {
			$(this).addClass('disabled');
		} else {
			return;
		}
		$('#cancelUpdateApproval').addClass('disabled');
		$('#eventSummaryForm').submit();

	});
	
	$('#sourcingrejectedButton').click(function(e) {
		e.preventDefault();
		if($('#approvedRejectForm').isValid()) {
			$(this).addClass('disabled');
			$('#sourcingapprovedButton').addClass('disabled');
		} else {
			return;
		}
		console.log("rejectedButton");
		$('#approvedRejectForm').attr('action', getContextPath() + "/buyer/requestRejected");
		$('#approvedRejectForm').submit();

	});
</script>


<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '.accordion,#idSumDownload,.tab-link > a,#nextButton,#dashboardLink,#previousButton,#bubble, #downloadButton,#approvedButton,#rejectedButton,#idSumDownload,.s1_view_desc,.bluelink,#approvalremarks, .downloadPoBtn, #downloadTemplate';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	$.validate({
		lang : 'en',
		modules : 'file'
	});
	


</script>
<script type="text/JavaScript">
	var addindex = ${additionalLevelNext};
	console.log("additionalLevelNext : " + addindex);
	//var indexlvl = ${additionalLevelNext};
	
	var defaultIndex=${additionalLevelStart};
	
	$(document).ready(function() {
		
		
		$('#finishRequest').click(function() {
			$(this).addClass('disabled');
		});
		
		$('#RfsCancelRequest').click(function() {
			var cancelRequest = $('#reasonCancel').val();
			if (cancelRequest != '') {
			$(this).addClass('disabled');
			}
		});
		
		
		$(document).delegate('.addsmall', 'click', function(e) {
			$(this).find(".addapproval_condition").trigger("click");	
		});

		//.collapse('toggle')
		/* Prevent To Remove In Edit Mode */
		var lockedLevel = '${doneLevel}';
		var lockedElement = [${!empty doneUsers ? doneUsers : ''} ''];
		$.each(lockedElement, function(i, item){
			$(".addtagTypeMultiSelect").find('option[selected="selected"][value="'+item+'"]').attr('data-locked',true);			
		});

		$(document).delegate('#addAddiApproval', 'click', function(e) {
			console.log("additionalLevelNext : " + addindex );
			e.preventDefault();
			var template = '<div id="addnew-approval-'+addindex+'" class="row new-approval addnew-approval">';
			template += '<input type="hidden" class="addapproval_id_hidden" name="sourcingFormApprovalRequests[' + (addindex - 1) + '].id" value="" />';
			template += '<input type="hidden" class="addlevel_hidden" name="sourcingFormApprovalRequests[' + (addindex - 1) + '].level" value="' + addindex + '" />';
			template += '<div class="col-md-2 col-sm-2"><label class="level mb-0">Level ' + addindex + '</label></div>';
			template += '<div class="col-md-7 col-sm-7" id="sel">';
			template += '<span class="dropUp"><select data-validation="required" autocomplete="off" id="multipleSelectExample-'+(addindex - 1)+'" name="sourcingFormApprovalRequests[' + (addindex - 1) + '].approvalUsersRequest" class="addtagTypeMultiSelect chosen-select user-list-all tagTypeMultiSelect" data-placeholder="Approvers" multiple>';
			<c:forEach items="${userList1}" var="users">
				<c:if test="${users.id != '-1' }">
					template += '<option value="${users.id}" >${users.name}</option>';
				</c:if>
				<c:if test="${users.id == '-1' }">
					template += '<option value="-1"  disabled="true">${users.name}</option>';
				</c:if>
			</c:forEach> 
			template += '</select></span></div><div class="col-sm-2 col-md-2 pad0">';
			template += '<div class="btn-address-field pt-0"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
			template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
			template += '<ul class="dropdown-menu dropup">';
			template += '<li><a href="javascript:void(0);" class="small addsmall"  tabIndex="1"><input name="sourcingFormApprovalRequests[' + (addindex - 1) + '].approvalType" checked="checked" value="OR" class="approval_condition addapproval_condition" type="checkbox"/><label>Any</label></a></li>';
			template += '<li><a href="javascript:void(0);" class="small addsmall" tabIndex="1"><input name="sourcingFormApprovalRequests[' + (addindex - 1) + '].approvalType" value="AND" class="approval_condition addapproval_condition"  type="checkbox"/><label>All</label></a></li>	</ul>';
			template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval addremoveApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
			template += '</div>';

			$(".dynamic-Add-approval").find('.approVAlRouteBox').append(template);

			$('#multipleSelectExample-'+(addindex - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });	

			$(".approVAlRouteBox").animate({ scrollTop: $(".approVAlRouteBox")[0].scrollHeight}, 1000);
			addindex++;
		});

		$(document).delegate('.addremoveApproval', 'click', function(e) {
			e.preventDefault();
			$(this).closest(".addnew-approval").remove();
			$(this).closest(".addapproval_id_hidden").remove();	
			$(this).closest(".addlevel_hidden").remove();	
			var ind=defaultIndex;
			console.log('Default Index ', defaultIndex);

			$(".addnew-approval").each(function(i, v) {
				$(this).attr("id", "addnew-approval-" + ind);
				$(this).find(".level").text('Level ' + ind);

				
				$(this).find(".addapproval_id_hidden").each(function(){					
					$(this).attr("name",'sourcingFormApprovalRequests[' +(ind-1) + '].id');
				});
				
				$(this).find(".addlevel_hidden").each(function(){					
					$(this).attr("name",'sourcingFormApprovalRequests[' +(ind-1) + '].level');
					$(this).val(ind);
				});
				
				$(this).find(".addapproval_condition").each(function(){
					$(this).attr("name",'sourcingFormApprovalRequests[' +(ind-1) + '].approvalType');
				}); // checkbox name reindex
				
				$(this).find("input[name='_sourcingFormApprovalRequests[" + (ind) + "].approvalType']").each(function(){//
					$(this).attr('name','_sourcingFormApprovalRequests[' +(ind-1) + '].approvalType');
				}); //Checkbox hidden val reindex
				
				
				$(this).find(".addtagTypeMultiSelect").each(function(){
					$(this).attr("name",'sourcingFormApprovalRequests[' +(ind-1) + '].approvalUsersRequest');
					$(this).attr("id", "multipleSelectExample-" + ((i-1)) + "");
				}) //select name reindex
				
				$(this).find("input[name='_sourcingFormApprovalRequests[" + (ind) + "].approvalUsersRequest']").each(function(){//
					$(this).attr("name",'_sourcingFormApprovalRequests[' +(ind-1) + '].approvalUsersRequest');
				}) //select name reindex
				
				ind++;
			});
			addindex--;
		});

		$(document).delegate('.addapproval_condition', 'click', function(e) {
		
			$(this).closest('.btn-address-field').find(".addapproval_condition").each(function(){
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
			//$('select').select2();
		}

	}
	
	$(document).delegate('#saveAdditionalApproval', 'click', function(e) {

		e.preventDefault();
		if($('#demo-form1').isValid()) {
			$(this).addClass('disabled');
			$('#finishAdditionalApproval').addClass('disabled');
		} else {
			return;
		}
		$("#demo-form1").submit();
	});
	
	$(document).delegate('#finishAdditionalApproval', 'click', function(e) {
		e.preventDefault();
		if($('#demo-form1').isValid()) {
			$(this).addClass('disabled');
			$('#saveAdditionalApproval').addClass('disabled');
		} else {
			return;
		}
		$("#demo-form1").attr('action', getContextPath()+'/buyer/finishAdditionalApproval/${sourcingFormRequest.id}');
		$("#demo-form1").submit();
	});
	
	$('document').ready(function() {

		//alert("eventTeamMembersList :"+${event.id});
		var eventTemMembersTable = $('#sourcingTeamMembersList').DataTable({
			"processing" : true,
			"serverSide" : true,
			"paging" : false,
			"info" : false,
			"ordering" : false,
			"searching" : false,
			"ajax" : {
				"url" : getContextPath() + '/buyer/sourcingTeamMembersList/${sourcingFormRequest.id}'
 			},
			"columns" : [ {
				"data" : "user.name",
				"className" : "align-left",
				"defaultContent" : "Demo Test"
			}, {
				"data" : "user.loginId",
				"className" : "align-left",
				"defaultContent" : "Demo 2"
			}, {
				"data" : "teamMemberType",
				"className" : "align-left",
				"defaultContent" : ""
			} ]
		});

	});
	
	
</script>
<style>
.rfsDocu {
	margin-top: -1%;
}

.center-btn {
	text-align: center;
	margin: 0 auto;
	margin-top: 25px !important;
}
</style>
<script>
	$(document).delegate('.editTeamMemberList', 'click', function(e) {
		$("#teamMemberListPopup").dialog({
			modal : true,
			minWidth : 300,
			width : '50%',
			maxWidth : 600,
			minHeight : 200,
			dialogClass : "",
			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded"
		});
	});
</script>

<script>
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
	// $('.ui-dialog-title').text('Add Contact Person');
	// $('#addEditContactPopup').find('a.addContactPerson').text('Add Contact
	// Person');
});
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/createSourcingRequest.js"/>"></script>
<script type="text/javascript">
		// Reset modal form and validation states when modal is hidden
		$('.modal').on('hidden.bs.modal', function () {
			const form = $(this).find('form');
			form[0].reset(); // Reset the form fields
			form.validate().resetForm(); // Reset the validation states
			form.find('.error').remove(); // Remove any existing error messages
			form.find('.has-error').removeClass('has-error'); // Remove error class
	});
</script>