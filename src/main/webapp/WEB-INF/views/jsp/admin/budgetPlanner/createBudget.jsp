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
<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_EDIT') or hasRole('ADMIN')" var="canEditBudget" />
<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_VIEW_ONLY') or hasRole('ADMIN')" var="canViewBudget" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<style>
.pr-0 {
	padding-right: 0;
}

.mtb-25 {
	margin: 25px 0;
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
.btn-add-file {
    display: flex;
    justify-content: left;
    margin-left: 78px;
}
</style>

<c:set var="fileType" value="" />
<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
	<c:set var="fileType" value="${fileType}${index.first ? '': ','}${type}" />
</c:forEach>

<div id="page-content">
	<input id="fileType" hidden value="${fileType}"> <input id=fileSizeLimit hidden value="${ownerSettings.fileSizeLimit}">
	<div class="col-md-12 p-0">
		<ol class="breadcrumb">
			<c:url value="/buyer/buyerDashboard" var="dashboardUrl" />
			<li>
				<a id="manageBudget" href="${dashboardUrl}"> <spring:message code="application.dashboard" />
				</a>
			</li>
			<c:url value="/admin/budgets/listBudget" var="manageBudget" />
			<li>
				<a id="manageBudget" href="${manageBudget}"> <spring:message code="defaultmenu.budget.manage" />
				</a>
			</li>
			<li class="active">
				<c:out value='${btnValue}' />
				<spring:message code="budget.budget" />
			</li>
		</ol>
		<!-- page title block -->
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap line_icon">
				<c:out value='${btnValue}' />
				<spring:message code="budget.budget" />
			</h2>
		</div>

		<div class="col_12 graph">
			<div class="white_box_brd pad_all_15">
				<section class="index_table_block">
					<%-- 	<c:if test="">
						<div class="right_button">
							<button class="w-80 btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="btnBudgetAdd">
								<spring:message code="budget.add" />
							</button>
							<button class="w-80 btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="btnBudgetDeduct">
								<spring:message code="budget.deduct" />
							</button>
							<button class="w-80 btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="btnBudgetTransfer">
								<spring:message code="budget.transfer" />
							</button>
						</div>
					</c:if> --%>
					<div class="row marg-top-10">
						<div class="col-xs-12 ">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
							<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
								<div class="meeting2-heading">
									<h3>
										<c:out value='${btnValue}' />
										<spring:message code="budget.budget" />
									</h3>
								</div>
								<div class="import-supplier-inner-first-new pad_all_15 global-list">
									<form:form cssClass="form-horizontal" action="${pageContext.request.contextPath}/admin/budgets/budget?${_csrf.parameterName}=${_csrf.token}" method="post" modelAttribute="budgetPojo" id="idBudget" enctype="multipart/form-data" autocomplete="off">
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<form:input path="budgetStatus" type="hidden"/>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="budgetId" cssClass="marg-top-10">
													<spring:message code="budget.id" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="label.budget.id" var="budgetIdPlace" />
												<form:input path="budgetId" type="text" data-validation-length="0-60" data-validation="length" cssClass="form-control budgetId" value="${budgetId}" readonly="true" />
											</div>
										</div>
										<form:hidden id="id" path="id" name="id" />
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="budgetName" cssClass="marg-top-10">
													<spring:message code="label.budget.name" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="enter.budget.name" var="budgetNamePlace" />
												<spring:message code="budget.budgetName.length" var="length" />
												<form:input path="budgetName" type="text" data-validation-length="1-50" data-validation="required length" cssClass="form-control" data-validation-error-msg-length="${length}" placeholder="${budgetNamePlace}" disabled="${budgetPojo.budgetStatus eq 'PENDING' or budgetPojo.budgetStatus eq 'APPROVED' or budgetPojo.budgetStatus eq 'ACTIVE' or budgetPojo.budgetStatus eq 'EXPIRED'}" />
											</div>
										</div>

										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="businessUnit" cssClass="marg-top-10">
													<spring:message code="label.budget.businessUnit" />
												</form:label>
											</div>
											<div class="col-md-5 ${btnValue =='Update' and idSettings.idSettingType == 'BUSINESS_UNIT' ? 'disabled' : ''}">
												<spring:message code="application.status.required" var="required" />
												<form:select path="businessUnit" cssClass="form-control chosen-select" id="idBusinessUnit" data-validation="required" disabled="${budgetPojo.budgetStatus eq 'PENDING' or budgetPojo.budgetStatus eq 'APPROVED' or budgetPojo.budgetStatus eq 'ACTIVE' or budgetPojo.budgetStatus eq 'EXPIRED'}">
													<form:option value="">
														<spring:message code="pr.select.business.unit" />
													</form:option>
													<form:options items="${businessUnitList}" itemValue="id" itemLabel="unitName" />
												</form:select>
											</div>
										</div>

										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="costCenter" cssClass="marg-top-10">
													<spring:message code="label.budget.costCenter" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="application.status.required" var="required" />
												<form:select path="costCenter" cssClass="form-control chosen-select" id="idCostCenter" data-validation="required" disabled="${budgetPojo.budgetStatus eq 'PENDING' or budgetPojo.budgetStatus eq 'APPROVED' or budgetPojo.budgetStatus eq 'ACTIVE' or budgetPojo.budgetStatus eq 'EXPIRED'}">
													<form:option value="">
														<spring:message code="budget.default.cost.center" />
													</form:option>
													<form:options items="${costCenterList}" itemValue="id" itemLabel="costCenter" />
												</form:select>
											</div>
										</div>

										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="validFrom" cssClass="marg-top-10">
													<spring:message code="label.budget.validFrom" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="enter.budget.validFrom" var="budgetvalidFromPlace" />
												<spring:message code="budget.date.required" var="required" />
												<form:input id="validFrom" path="validFrom" disabled="${not empty budgetPojo.id and budgetPojo.budgetStatus ne 'REJECTED' and budgetPojo.budgetStatus ne 'DRAFT' ?'true':'false'}" class="nvclick bootstrap-datepicker form-control for-clander-view" data-validation="required" data-validation-error-msg-required="${required}" data-validation-format="dd/mm/yyyy" autocomplete="off" placeholder="${budgetvalidFromPlace}" />
											</div>
										</div>

										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="validTo" cssClass="marg-top-10">
													<spring:message code="label.budget.validTo" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="enter.budget.validTo" var="budgetvalidToPlace" />
												<spring:message code="budget.date.required" var="required" />
												<form:input id="validTo" path="validTo" disabled="${not empty budgetPojo.id and budgetPojo.budgetStatus ne 'REJECTED' and budgetPojo.budgetStatus ne 'DRAFT' ?'true':'false'}" class="nvclick bootstrap-datepicker form-control for-clander-view" data-validation="required" data-validation-format="dd/mm/yyyy" data-validation-error-msg-required="${required}" placeholder="${budgetvalidToPlace}" />
											</div>
										</div>

										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<label class="marg-top-10" ><spring:message code="label.budget.budgetOverRun" /></label>
											</div>
											<div class="col-md-1 marg-top-10 sup-category ">
												<span class=""> <form:checkbox path="budgetOverRun" id="budgetOverRun" class="custom-checkbox" label="Yes" title="Enable budget overrun" disabled="${budgetPojo.budgetStatus eq 'PENDING' or budgetPojo.budgetStatus eq 'APPROVED' or budgetPojo.budgetStatus eq 'ACTIVE'}" />
												</span>
											</div>
											<%-- 		<div class="col-md-1 marg-top-10 sup-category ">
												<span class=""> <form:checkbox path="budgetOverRunNo" id="budgetOverRunNo" class="custom-checkbox" label="No" title="Enable budget overrun" disabled="${budgetPojo.budgetStatus eq 'APPROVED' or budgetPojo.budgetStatus eq 'ACTIVE' or budgetPojo.budgetStatus eq 'EXPIRED'}" />
												</span>
											</div> --%>
										</div>

										<div class="row marg-bottom-20" id="hideOverRunNotification">
											<div class="col-md-3">
												<label class="marg-top-10" ><spring:message code="label.budget.budgetOverRun.notification" /></label>
											</div>
											<div class="col-md-1 marg-top-10 sup-category ">
												<span class=""> <form:checkbox path="budgetOverRunNotification" id="budgetOverRunNotification" class="custom-checkbox" label="Yes" title="Enable budget overrun notification" disabled="${budgetPojo.budgetStatus eq 'PENDING' or budgetPojo.budgetStatus eq 'APPROVED' or budgetPojo.budgetStatus eq 'ACTIVE' or budgetPojo.budgetStatus eq 'EXPIRED'}" />
												</span>
											</div>
											<%-- 	<div class="col-md-1 marg-top-10 sup-category ">
												<span class=""> <form:checkbox path="budgetOverRunNotificationNo" id="budgetOverRunNotificationNo" class="custom-checkbox" label="No" title="Enable budget overrun notification" disabled="${budgetPojo.budgetStatus eq 'APPROVED' or budgetPojo.budgetStatus eq 'ACTIVE' or budgetPojo.budgetStatus eq 'EXPIRED'}" />
												</span>
											</div> --%>
										</div>
										<%-- 		<form:hidden class="form-control" class="remainingAmount" path="remainingAmount" /> --%>

										<div class="row marg-bottom-20 marg-top-10">
											<div class="col-md-3">
												<form:label path="totalAmount" cssClass="marg-top-10">
													<spring:message code="label.budget.totalAmount" />
												</form:label>
											</div>
											<div class="col-md-3">
												<spring:message code="enter.budget.totalAmount" var="totalAmountPlace" />
												<fmt:formatNumber var="budgetAmount" type="number" minFractionDigits="2" maxFractionDigits="2" value="${totalAmount}" />
												<form:input id="totalAmount" path="totalAmount" type="text" value="${totalAmount}" class="form-control autoSave" placeholder="${totalAmountPlace}" style="margin-right: 15px;z-index: 5;" data-validation="required validate_custom_length positive1" data-validation-regexp="^\d{1,20}(\.\d{1,2})?$"  data-sanitize="numberFormat" data-sanitize-number-format="0,0.00" disabled="${budgetPojo.budgetStatus eq 'PENDING' or budgetPojo.budgetStatus eq 'APPROVED' or budgetPojo.budgetStatus eq 'ACTIVE' or budgetPojo.budgetStatus eq 'EXPIRED'}" />
											</div>
											<div class="col-md-2">
												<form:select path="baseCurrency" style="width: 100px;" cssClass="form-control chosen-select" id="baseCurrency" data-validation="" data-validation-error-msg-required="${required}">
													<form:option value="">
														<spring:message code="budget.default.currency" />
													</form:option>
													<form:options items="${baseCurrencyList}" itemValue="id" itemLabel="currencyCode" />
												</form:select>
											</div>
										</div>
										<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
											<div class="meeting2-heading">
												<label class="p-11"><spring:message code="label.budget.budgetFiles" /></label>
											</div>

											<div class="divFileInput">
												<div class="row">
													<div class="align-right col-md-3"></div>
													<div class="col-md-9 btn-add-file">
														<button class="mtb-25 btn btn-plus btn-info hvr-pop hvr-rectangle-out" type="button" name="Add more files" id="addNewFile">
															<spring:message code="label.budget.add.file" />
														</button>
													</div>
												</div>
												<div class="row removeDocDiv">
													<div class="col-md-3"></div>
													<div class="col-md-5">
														<div class="row">
															<div class="col-md-11 pr-0">
																<div data-provides="fileinput" class="fileinput fileinput-new input-group">
																	<div data-trigger="fileinput" class="form-control">
																		<i class="glyphicon glyphicon-file fileinput-exists"></i> <span id="idOtherFileUploadSpan" class="fileinput-filename show_name"></span>
																	</div>
																	<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new" id="selectNew">Select file</span> <span class="fileinput-exists" id="fileinput-exists">Change</span> <input id="" name="budgetFilesArr" type="file" readonly="${budgetPojo.budgetStatus eq 'PENDING' or budgetPojo.budgetStatus eq 'APPROVED' or budgetPojo.budgetStatus eq 'ACTIVE' or budgetPojo.budgetStatus eq 'EXPIRED'}" />
																	</span>
																</div>
															</div>
															<div class="col-md-1">
																<span class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeFile"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
																</span>
															</div>
														</div>
													</div>
												</div>
											</div>

											<div class="col-md-0"></div>
											<div class="col-md-10">
												<spring:message code="application.note" />
												:<br />
												<ul>
													<li>
														<spring:message code="createrfi.documents.max.size" />
														${ownerSettings.fileSizeLimit} MB
													</li>
													<li>
														<spring:message code="createrfi.documents.allowed.file.extension" />
														: ${fileType}.
													</li>
												</ul>
											</div>

											<c:if test="${not empty budgetPojo.budgetDocuments}">
												<div class="Invited-Supplier-List-table pad_all_15 add-supplier">
													<div class="ph_table_border">
														<div class="mega">
															<table class="header ph_table border-none" width="100%" border="0" cellspacing="0" cellpadding="0">
																<thead>
																	<tr>
																		<c:if test="${budgetPojo.budgetStatus eq 'PENDING' or budgetPojo.budgetStatus eq 'NEW' or budgetPojo.budgetStatus eq 'DRAFT' or budgetPojo.budgetStatus eq 'REJECTED'}">
																			<th class="width_200 width_200_fix align-center"><spring:message code="application.action" /></th>
																		</c:if>
																		<th class="width_200 width_200_fix align-left"><spring:message code="event.document.filename" /></th>
																		<th class="width_200 width_200_fix align-left"><spring:message code="event.document.publishdate" /></th>
																	</tr>
																</thead>
															</table>
															<table class="data ph_table border-none" width="100%" border="0" cellspacing="0" cellpadding="0" id="prDocList">
																<tbody>
																	<c:forEach items="${budgetPojo.budgetDocuments}" var="budgetFile">
																		<tr>
																			<c:if test="${budgetPojo.budgetStatus eq 'PENDING' or budgetPojo.budgetStatus eq 'NEW' or budgetPojo.budgetStatus eq 'DRAFT' or budgetPojo.budgetStatus eq 'REJECTED'}">
																				<td class="width_200 width_200_fix align-center">
																					<form method="GET">
																						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <a id="downloadButton" href="${pageContext.request.contextPath}/admin/budgets/downloadBudgetDocument/${budgetFile.id}" data-placement="top" title='<spring:message code="tooltip.download" />'> <img src="${pageContext.request.contextPath}/resources/images/download.png">
																						</a> &nbsp; <span> <a id="deleteDoc" href="${pageContext.request.contextPath}/admin/budgets/deleteDocument/${budgetPojo.id}/" data-id="${budgetFile.id}" data-placement="top" title='<spring:message code="application.remove" />' class="removeDocFile" removeDocId="${budgetFile.id}"> <img src="${pageContext.request.contextPath}/resources/images/delete.png">
																						</a>
																						</span> &nbsp;
																					</form>
																				</td>
																			</c:if>
																			<td class="width_200 width_200_fix align-left">${budgetFile.fileName}</td>
																			<fmt:formatDate var="uploadDate" value="${budgetFile.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																			<td class="width_200 width_200_fix align-left">${uploadDate}</td>
																		</tr>
																	</c:forEach>
																</tbody>
															</table>
														</div>
													</div>
												</div>
											</c:if>
										</div>
										<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
											<jsp:include page="budgetApprovals.jsp" />
										</div>
										<div class="clear"></div>
										<c:url value="/admin/budgets/cancelBudget/${id}" var="cancelBudget" />
										<div class="marg-top-10 row marg-bottom-20">
											<div class="col-md-3">
												<label class="marg-top-10"></label>
											</div>
											<c:if test="${not permissions.disabled}">
												<div class="col-md-9 dd sky mar_b_15" style="position: relative; left: 10%;">
													<input type="submit" value="${btnValue}" id="saveBudget" class=" btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" ${permissions.disabled eq true ? 'disabled' :''}  /> <a href="${manageBudget}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
													</a>
													<c:if test="${permissions.creator and budgetPojo.budgetStatus == 'REJECTED'}">
														<div style="float: right;">
															<a href="#cancelModal" class="btn btn-black ph_btn_midium button-previous" style="position: relative; right: 100%;" data-toggle="modal"> Cancel Budget </a>
														</div>
													</c:if>
												</div>
											</c:if>
										</div>
									</form:form>
								</div>
							</div>
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>
</div>


<!-- delete pop up -->
<div class="flagvisibility dialogBox" id="removeBudgetDocumentPopup" title='<spring:message code="tooltip.remove.team.member" />'>
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock">
				<spring:message code="event.confirm.to.remove" />
				<span></span> ?
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeBudgetDocument" data-original-title="Remove"><spring:message code="application.remove" /></a>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>

<!-- Cancel pop up -->
<div class="modal fade" id="cancelModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.cancel.budget" />
				</h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="budget.confirm.message.cancel" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmCancel" class="btn btn-info ph_btn_small pull-left hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/admin/budgets/cancelBudget/${budgetPojo.id}" title="Yes"> Yes
				</a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					No
				</button>
			</div>
		</div>
	</div>
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
 
	var rowCount;
</script>
<script>
	$('#addNewFile').click(function(e) {
		var indexx = 1;
		var html = '<div class="row removeDocDiv">'
		html += '<div class="col-md-3"></div>'
		html += '<div class="col-md-5">'
		html += '<div class="row">'
		html += '<div class="col-md-11 pr-0">'
		html += '<div data-provides="fileinput" class="fileinput fileinput-new input-group">'
		html += '<div data-trigger="fileinput" class="form-control">'
		html += '<i class="glyphicon glyphicon-file fileinput-exists"></i>'
		html += '<span id="idOtherFileUploadSpan" class="fileinput-filename show_name"></span>'
		html += '</div>'
		html += '<span class="input-group-addon btn btn-black btn-file">'
		html += '<span class="fileinput-new" id="selectNew">Select file</span>'
		html += '<span class="fileinput-exists" id="fileinput-exists">Change</span>'
		html += '<input id="" name="budgetFilesArr" type="file" readonly="${budgetPojo.budgetStatus eq 'PENDING' or budgetPojo.budgetStatus eq 'APPROVED' or budgetPojo.budgetStatus eq 'ACTIVE' or budgetPojo.budgetStatus eq 'EXPIRED'}" />'
		html += '</span>'
		html += '</div>'
		html += '</div>'
		html += '<div class="col-md-1">'
		html += '<span class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeFile">'
		html += '<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>'
		html += '</span>'
		html += '</div>'
		html += '</div>'
		html += '</div>'
		indexx++;
		$('.divFileInput').append(html);
	});

	$('#saveBudget').on('click', function(e) {
		e.preventDefault();
		$('#loading').show();
		if ($("#idBudget").isValid()) {
			//check valid To date is greater than valid from date
			$("#idBudget").submit();
		}else{
			$('#loading').hide();
		}
	});

	$(document).delegate('.removeFile', 'click', function(e) {
		$(this).parents('.removeDocDiv').remove();
	});
</script>
<script>
	var docId;

	$(document).delegate('.removeBudgetDocument', 'click', function(e) {
		var href = $('#deleteDoc').attr("href");
		window.location = href + docId;
	});
	//remove budget document
	$(document).delegate('#deleteDoc', 'click', function(e) {
		e.preventDefault();
		var currentBlock = $(this);
		var fileName = $(this).parents('td').next('td').text();
		docId = $(this).attr("data-id");
		$("#removeBudgetDocumentPopup").dialog({
			modal : true,
			maxWidth : 400,
			minHeight : 100,
			dialogClass : "",
			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded"
		});
		$('.ui-widget-overlay').addClass('bg-white opacity-60');
		$("#removeBudgetDocumentPopup").find('.approverInfoBlock').find('span:first-child').text(fileName);
		$('.ui-dialog-title').text('Remove Document');
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript">
	$(document).ready(function() {
		
		var totalAmt=Math.abs(Number($("#totalAmount").val().replace(/,/g, "")));
		totalAmt =parseFloat(totalAmt);
		if(totalAmt!=''){
			totalAmt=ReplaceNumberWithCommas(totalAmt.toFixed(2));
			$("#totalAmount").val(totalAmt);
		}
		var toDate = new Date();

		var nowTemp = new Date();
		var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
		$('#validTo').bsdatepicker({
			format : 'dd/mm/yyyy',
			onRender : function(date) {
				var stringValidFrom = $('#validFrom').val();
				if(stringValidFrom!=''){
					var dateParts = stringValidFrom.split("/");
					var dateObject = new Date(dateParts[2], dateParts[1] - 1, dateParts[0]);
					toDate = new Date(dateParts[2], dateParts[1] - 1, dateParts[0]);
					toDate.setFullYear(toDate.getFullYear() + 1)
					/* Incrementing year by 1 */
					dateObject.setFullYear(dateObject.getFullYear() + 1)
					var validToDate=new Date(dateObject.getFullYear(), dateObject.getMonth(), dateObject.getDate(), 0, 0, 0, 0);
					console.log("validToDate"+validToDate);
					return date.valueOf() > validToDate.valueOf() ? 'disabled' : '';
				}else{
					return date.valueOf() < now.valueOf() ? 'disabled' : '';
				}
			}
		}).on('changeDate', function(e) {
			$(this).blur();
			$(this).bsdatepicker('hide');
		});

		$('#validFrom').bsdatepicker({
			format : 'dd/mm/yyyy',
			minDate : now,
			onRender : function(date) {
				return date.valueOf() < now.valueOf() ? 'disabled' : '';
			}
		}).on('changeDate', function(e) {
			var selectedDate = e.target.value;
			var dateParts = selectedDate.split("/");
			var dateObject = new Date(dateParts[2], dateParts[1] - 1, dateParts[0]);
			toDate = new Date(dateParts[2], dateParts[1] - 1, dateParts[0]);
			toDate.setFullYear(toDate.getFullYear() + 1)

			/* Incrementing year by 1 */
			dateObject.setFullYear(dateObject.getFullYear() + 1)
			var validToDate = dateObject.getDate() + "/" + (dateObject.getMonth() + 1) + "/" + dateObject.getFullYear();
			$("#validTo").bsdatepicker('setValue', validToDate);
			$(this).blur();
			$(this).bsdatepicker('hide');
		});

		rowCount = $('#filesTable tr').length;
		$('#Load_File-error-dialog').hide();

		/* edit mode if budgetOverRun is true */
		if ($('#budgetOverRun').is(':checked')) {
			$('#hideOverRunNotification').show();
		} else {
			$('#hideOverRunNotification').hide();
		}

		$('#budgetOverRun').change(function() {
			if (this.checked)
				$('#hideOverRunNotification').show();
			else
				$('#hideOverRunNotification').hide();
		});

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
	
	$('#idBusinessUnit').on('change', function(){
		var value=this.value;
		if(value != undefined && value != ''){
		<c:if test="${isEnableUnitAndCostCorrelation}">
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var url = getContextPath() +  "/buyer/getAssingedCostCenterList";
			$.ajax({
				type : "GET",
				url : url,
				data : {
					unitId : value,
				},
				beforeSend : function(xhr) {
					var html='';
					$('#idCostCenter').html(html);
				    $("#idCostCenter").trigger("chosen:updated");
					$('#loading').show();
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				complete : function() {
					$('#loading').hide();
				},
				success : function(data) {
					var html='';
					html += '<option value="" selected="selected">'+ 'Select Cost Center' +'</option>';
					$.each(data, function(i, item) {
						if(item.description.length > 0){
							html += '<option value="' + item.id + '">' + item.costCenter + ' - ' + item.description + '</option>';
						}else{
							html += '<option value="' + item.id + '">' + item.costCenter + '</option>';
						}						
					});
					$('#idCostCenter').append(html);
					$("#idCostCenter").trigger("chosen:updated");
				},
				error : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
			});
		</c:if>
		}
	})
</script>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />