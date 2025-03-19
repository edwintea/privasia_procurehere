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
<sec:authentication property="principal.id" var="loggedInUserId" />
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('ROLE_SUPPLIER_PERFORMANCE_VIEW_ONLY')" var="readOnlyPermission" />
<spring:message var="rfxCreateDetails" code="application.rfx.create.details" />
<spring:message code="unmask.search.user" var="unMasUserPlace" />
<jsp:useBean id="now" class="java.util.Date" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreateDetails}] });
});
</script>

<style>
.err-red {
	color: #ff5757;
}

ul#industryCategoryList {
	list-style: none;
	padding: 0;
	position: absolute;
	z-index: 9;
	background: #fff;
	border-left: 1px solid #ccc;
	width: 100%;
	border-right: 1px solid #ccc;
	max-height: 200px;
	overflow: auto;
}

#industryCategoryList li:first-child {
	border-top: 1px solid #ccc;
}

#industryCategoryList li {
	border-bottom: 1px solid #ccc;
	padding: 10px;
	cursor: pointer;
}

#industryCategoryList li:hover {
	background: #0cb6ff;
	color: #fff;
}

.physicalCriterion>div {
	width: 20px;
	float: left;
}

.physicalCriterion>span {
	width: calc(100% - 30px);
	float: left;
}

.bootstrap-timepicker-widget.dropdown-menu.open {
	max-width: 160px;
}

.bootstrap-timepicker-widget.dropdown-menu.open input {
	width: 35px;
	min-width: 35px;
	max-width: 35px;
}

#approverSCount {
	width: 0;
	height: 0;
	border: 0;
	margin-bottom: 10px;
}

[aria-describedby="addEditContactPopup"] {
	max-width: 600px;
}

.d-flex-line {
	display: flex;
	align-items: center;
}

#addReminder1 .time-lbl {
	width: auto;
	margin-right: 15px;
}

#addReminder1 .remind-lbl {
	width: auto;
	margin-left: 15px;
	margin-right: 20px;
}

.ml-15 {
	margin-left: 15px !important;
}

.mb-15 {
	margin-bottom: 15px;
}

.w-100 {
	width: 100% !important;
}

.marginbottom {
	margin-bottom: 1.1%;
}

#allowEvaluation {
	margin-top: 6%;
}
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}

/* #rfxEnvOpeningAfter { */
/* 	margin-top: -27%; */
/* } */

.content-between {
	display: flex;
	justify-content: space-between;
	font-size: 14px;
	font-weight: bold;
	margin-bottom: 5px;
}

.usersListTable .row i.fa {
	font-size: 18px;
	color: #7f7f7f;
	padding-top: 0;
}

.marginDisable .row {
	cursor: pointer;
}

.marginDisable .row:hover {
	background: #fafcfe;
}

.d-flex {
	display: flex;
	font-size: 14px;
	font-weight: bold;
	margin-bottom: 5px;
}

.d-flex-line {
	display: flex;
}

.selected-item {
	position: absolute;
	left: 55%;
}

.readOnlyClass {
	pointer-events: none !important;
	cursor: not-allowed !important;
}

input[readonly].for-clander-view {
	cursor: default !important;
}

.chosen-container-single .chosen-single span {
	width: 40rem;
}

.ml-10 {
	margin-left: 10px;
}
</style>

<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
					</a></li>
					<li class="active"><a href="${pageContext.request.contextPath}/buyer/supplierPerformanceList"> Performance Evaluation List</a></li>
					<li>Details</li>
				</ol>
				<section class="create_list_sectoin">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap tender-request-heading">
							<spring:message code="sp.evaluation" />
						</h2>
						<h2 class="trans-cap pull-right">
							<spring:message code="application.status" />: ${spForm.formStatus}
						</h2>
					</div>
					<jsp:include page="supplierPerformanceFormHeader.jsp"></jsp:include>
					<div class="tab-pane active error-gap-div">
						<form:form class="bordered-row" autocomplete="off" id="demo-form1" method="post" modelAttribute="spForm" action="${pageContext.request.contextPath}/buyer/saveSupplierPerformanceForm">
							<form:hidden path="id" name="id" />
							<input type="hidden" id="formId" name="formId" value="${spForm.id}">
							<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
							<div class="clear"></div>
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="upload_download_wrapper clearfix event_info">
								<h4>
									<spring:message code="sp.evaluation.form.information.label" />
								</h4>
								<div class="row">
									<div class="form-tander1 pad_left_right_15">
										<div class="col-sm-4 col-md-3 col-xs-6">
											<label> <spring:message code="sp.form.id" />
											</label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<p>${spForm.formId}</p>
										</div>
									</div>
									<div class="form-tander1 pad_left_right_15">
										<div class="col-sm-4 col-md-3 col-xs-6">
											<label> <spring:message code="sp.form.name" />
											</label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<p>${spForm.formName}</p>
										</div>
									</div>
									<div class="form-tander1 line-set pad_left_right_15">
										<div class="col-sm-4 col-md-3 col-xs-6">
											<label> <spring:message code="sp.form.owner" />
											</label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<p>${spForm.formOwner.name }
												(${spForm.formOwner.communicationEmail})
											</p>
										</div>
									</div>
								</div>
							</div>

							<div class="upload_download_wrapper clearfix marg-top-10 event_info event_form">
								<h4>
									<spring:message code="perfromance.evaluation.details" />
								</h4>
								<div class="form_field">
									<div>
										<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdetails.event.referencenumber" />
										</label>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<form:input path="referenceNumber" type="text" readonly="${!empty spForm.eventId ? 'true' : 'false'}" data-validation="required alphanumeric length" data-validation-allowing="/ -_" data-validation-length="max64" class="form-control autoSave" />
										</div>
									</div>
								</div>
								<c:if test="${!empty templateFields ? (tf:visibility( templateFields, 'EVENT_NAME' )) : 'true' }">
									<div class="form_field">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="defultMenu.sp.ref.name" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<form:input path="referenceName" type="text" readonly="${!empty spForm.eventId ? 'true' : 'false'}" data-validation="required" class="form-control autoSave" maxlength="200" />
											</div>
										</div>
									</div>
								</c:if>
								<!-- Supplier -->
							<div class="form_field">
									<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="application.supplier" />
									</div>
									<div class="col-md-5 ${!empty spForm.eventId ? 'disabled' : '' } ">
										<spring:message code="application.supplier.required" var="required" />
										<form:select path="awardedSupplier" id="chosenSupplier"  class="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
											<form:option value="">
												<spring:message code="Product.favoriteSupplier" />
											</form:option>
											<c:forEach items="${favSupp}" var="supp">
												<c:if test="${empty supp.id}">
													<option value="" disabled>${supp.companyName}</option>
												</c:if>
												<c:if test="${!empty supp.id and supp.id != spForm.awardedSupplier.id}">
													<option value="${supp.id}">${supp.companyName}</option>
												</c:if>
												<c:if test="${!empty supp.id and supp.id == spForm.awardedSupplier.id}">
													<option value="${supp.id}" selected>${supp.companyName}</option>
												</c:if>
											</c:forEach>
			
										</form:select>
									</div>
								</div>
								
								<c:if test="${spForm.template.procurementCategoryVisible}">
									<!-- Procurement Categories -->
									<div class="form_field">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label">
												<spring:message code="label.procurement.category" />
											</label>
										</div>
										<div id="idProcurementCategory" class="col-md-5 ${(!empty spForm.eventId or spForm.template.procurementCategoryDisabled) ? 'disabled' : '' }">
											<spring:message code="procurement.category.empty" var="required" />
											<form:select path="procurementCategory" id="chosenProcurementCategory" readonly="${(not empty spForm.eventId) ? 'true' : ''}" cssClass="chosen-select" 
													data-validation-optional="${spForm.template.procurementCategoryOptional ? 'true' : 'false'}" 
													data-validation="${spForm.template.procurementCategoryOptional ? '' : 'required'}"
												 	data-validation-error-msg-required="${required}">
												<form:option value="">
													<spring:message code="procurement.info.category.select" />
												</form:option>
												<c:forEach items="${procurementCategoryList}" var="procurementCategories">
													<c:if test="${empty spForm.procurementCategory.id}">
														<option value="" disabled>Select Procurement Category</option>
													</c:if>
													<c:if test="${!empty spForm.procurementCategory.id and procurementCategories.id eq spForm.procurementCategory.id}">
														<option value="${procurementCategories.id}" selected>${procurementCategories.procurementCategories}</option>
													</c:if>
													<c:if test="${!empty spForm.procurementCategory}">
														<option value="${procurementCategories.id}">${procurementCategories.procurementCategories}</option>
													</c:if>
												</c:forEach>
											</form:select>
										</div>
									</div>
								</c:if>
								
								<!-- Business Unit -->
								<div class="form_field">
									<div class="">
										<label class="col-sm-4 col-md-3 col-xs-6 control-label">
											<spring:message code="label.businessUnit" />
										</label>
									</div>
									<div id="idBusinessUnitDiv" class="col-md-5 ${!empty spForm.eventId ? 'disabled' : '' }">
										<spring:message code="business.unit.empty" var="required" />
										<form:select path="businessUnit" id="chosenBusinessUnit" cssClass="chosen-select" data-validation="required" 
											data-validation-error-msg-required="${required}">
											<form:option value="">
												<spring:message code="pr.select.business.unit" />
											</form:option>
											<c:forEach items="${businessUnitList}" var="unitData">
												<c:if test="${empty unitData.id}">
													<option value="" disabled>${unitData.unitName}</option>
												</c:if>
												<c:if test="${!empty unitData.id and unitData.id != spForm.businessUnit.id}">
													<option value="${unitData.id}">${unitData.unitName}</option>
												</c:if>
												<c:if test="${!empty unitData.id and unitData.id == spForm.businessUnit.id}">
													<option value="${unitData.id}" selected>${unitData.unitName}</option>
												</c:if>
											</c:forEach>
										</form:select>
									</div>
								</div>
								
								<!-- Performance Evaluators -->
								<div class="form-group" id="teamMember">
									<label class="col-sm-4 col-md-3 control-label"><spring:message code="eventsummary.envelopes.evaluator" /></label>
									<div class="col-sm-8 col-md-5">
										<div class="width100 pull-left">
											<div class="col-md-9 col-sm-9 pad0">
												<span class="dropUp"> 
												<select id="evaluatorList1" class="user-list-normal chosen-select" selected-id="evaluator-id" cssClass="form-control chosen-select" name="userList1">
														<option value=""><spring:message code="select.evaluator.placeholder" /></option>
														<c:forEach items="${Evaluators}" var="evaluator">
															<c:if test="${evaluator.id == '-1' }">
																<option value="-1" disabled="true">${evaluator.name}</option>
															</c:if>
															<c:if test="${evaluator.id != '-1' }">
																<option value="${evaluator.id}">${evaluator.name}</option>
															</c:if>
														</c:forEach>
												</select>
												</span>
												<div class="form-error font-red  hide" id="evaluator-err">
													<spring:message code="select.evaluator.placeholder.error" />
												</div>
											</div>
											<span class="col-md-3 col-sm-3 pad0 ">
												<button class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out addEvaluatorToList" style="margin-left: 4px;" title="" data-placement="top" data-toggle="tooltip" data-original-title='<spring:message code="tooltip.add.evaluator" />' list-type="Evaluator">
													<i class="fa fa-plus" aria-hidden="true"></i>
												</button>
											</span>
										</div>
									</div>
									</div>
								<div class="frm-field">
									<div class="col-sm-4 col-md-3 control-label">&nbsp;</div>
									<div class="col-sm-8 col-md-5">
									<div class="ph_table_border marg-top-10">
											<div class="reminderList marginDisable evaluatorList">
												<c:forEach items="${assignedEvaluators}" var="evaluator">
													<div class="row" evaluator-id="${evaluator.evaluator.id}">
														<div class="col-md-10">
															<p>${evaluator.evaluator.name}</p>
														</div>
														<c:if test="${spForm.formStatus != 'SUSPENDED'}">
															<div class="col-md-2">
																<a href="" class="removeEvaluatorsList" list-type="Evaluator"> <i class="fa fa-times-circle"></i>
																</a>
															</div>
														</c:if>
													</div>
												</c:forEach>
												<c:if test="${empty assignedEvaluators}">
													<div class="row" style="min-height: 20px;" evaluator-id="">
														<div class="col-md-8 ">
															<p>
																<spring:message code="envelope.add.evaluator.placeholder" />
															</p>
														</div>
													</div>
												</c:if>
											</div>
										</div>
									</div>
								</div>
								<div class="upload_download_wrapper clearfix marg-top-10 event_info event_form">
								<h4>
									<spring:message code="supplier.performance.period" />
								</h4>
								<!-- Start Date -->
								<div class="form_field">
									<div class="col-md-3">
										<form:label path="evaluationStartDate" cssClass="control-label">
											<spring:message code="productlist.startDate.item" />
										</form:label>
									</div>
			
									<div class="col-md-3">
										<div class="input-prepend input-group ${spForm.formStatus == 'SUSPENDED' && spForm.oldFormStatus == 'ACTIVE' ? 'disabled' : ''}">
											<spring:message code="dateformat.placeholder" var="dateformat" />
											<form:input id="evaluationStartDate" path="evaluationStartDate" data-validation="required" class="bootstrap-datepicker form-control for-clander-view evaluationStartDate" data-fv-date-min="15/10/2016" placeholder="${dateformat}" autocomplete="off" />
											<div id="contract_dateErrorBlock"></div>
											<form:errors path="evaluationStartDate" cssClass="error" />
										</div>
									</div>
									<div class="col-md-2 ">
										<div class="bootstrap-timepicker dropdown width_150_fix ${spForm.formStatus == 'SUSPENDED' && spForm.oldFormStatus == 'ACTIVE' ? 'disabled' : ''}">
											<form:input path="evaluationStartTime" autocomplete="off" onclick="this.blur();" data-validation="required" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control autoSave" />
										</div>
									</div>
								</div>
								
								<!-- Evaluation duration -->
								<div class="form_field">
									<div class="col-md-3">
										<form:label path="evaluationDuration" cssClass="control-label">
											<spring:message code="sp.evaluation.duration" />
										</form:label>
									</div>
									<div class="col-md-2">
										<label id="evaluationDuration" class="form-control autoSave"></label>
										<form:hidden id="evaluationDurationHidden" path="evaluationDuration" cssClass="" />
									</div>
									<div class="col-md-2">
										<spring:message code="eventdetails.event.days" var="days" />
										<label class="control-label" style="margin-top: 10px;">${days}</label>
									</div>
								</div>
								
								<!-- End Date -->		
								<div class="form_field">
									<div class="col-md-3">
										<form:label path="evaluationEndDate" cssClass="control-label">
											<spring:message code="productlist.endDate.item" />
										</form:label>
									</div>
									<div class="col-md-3 ">
										<div class="input-prepend input-group">
											<spring:message code="dateformat.placeholder" var="dateformat" />
											<form:input id="evaluationEndDateId" data-validation="required" path="evaluationEndDate" class="bootstrap-datepicker form-control for-clander-view" data-fv-date-min="15/10/2016" placeholder="${dateformat}" autocomplete="off" />
											<div id="contract_dateErrorBlock"></div>
											<form:errors path="evaluationEndDate" cssClass="error" />
										</div>
									</div>
									<div class="col-md-2">
										<div class="bootstrap-timepicker dropdown width_150_fix">
											<form:input path="evaluationEndTime" autocomplete="off" onclick="this.blur();" data-validation="required" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control autoSave" />
										</div>
									</div>
								</div>
			
								<!--reminder for Performance Evaluation  -->
								<div class="form_field">
									<div class="col-md-3">
										<label for="idStatus" class="control-label"><spring:message code="supplier.performance.reminder" /> </label>
									</div>
									<div class="col-md-5">
										<div class="">
											<a class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out title='<spring:message code="add.reminder.placeholder" />' data-placement="top" id="idAddReminder" data-toggle="tooltip"> <img src="${pageContext.request.contextPath}/resources/images/ring_cion.png"> Add Reminder
											</a>
										</div>
										<div class="ph_table_border marg-top-10">
											<div class="reminderList marginDisable formReminderList">
												<c:forEach items="${reminderList}" var="reminder">
													<div class="row reminderId " id="${reminder.id}">
														<input type="hidden" id="reminderDate" name="reminderDate" value="${reminder.reminderDate}">
														<input type="hidden" name="remindMeDays" value="${reminder.interval}">
														<input type="hidden" name="reminderSent" value="${reminder.reminderSent}">
														<fmt:formatDate var="reminderDateTime" value="${reminder.reminderDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
														<input type="hidden" id="reminderDateDel" class="reminderDateDel" value="${reminderDateTime}">
														<div class="col-md-10 reminders">
															<p id="reminderDate">
																 <b>Reminder Date: </b>${reminderDateTime}</p>
														</div>
														<div class="col-md-2">
															<a href="" class="removeEvaluationReminder"> <i class="fa fa-times-circle"></i>
															</a>
														</div>
													</div>
												</c:forEach>
											</div>
										</div>
									</div>
								</div>
								
								<!-- Recurrence evaluation -->
								<div class="form_field ${spForm.formStatus == 'SUSPENDED' ? 'disabled' : ''}">
									<div class="col-md-3">
										<spring:message code="sp.recurrence.evaluation" var="visible" />
										<label class="control-label">${visible}</label>
									</div>
									<div class="col-md-5">
										<div class="col-md-1 marg-top-10">
											<form:checkbox path="isRecurrenceEvaluation" id="recEvaluation" class="custom-checkbox" title="" label="" />
										</div>
										<div class="col-md-5 ${spForm.isRecurrenceEvaluation ? '' : 'disabled'}" >
											<form:input path="recurrenceEvaluation" type="text" id="recurrenceEvaluation" data-validation="length"  data-validation-length="max3" class="form-control " />
										</div>
										<div class="col-md-2">
											<spring:message code="eventdetails.event.days" var="days" />
											<label class="control-label" style="margin-top: 10px;">${days}</label>
										</div>
									</div>
								</div>
								
								<div class="form_field ${spForm.formStatus == 'SUSPENDED' ? 'disabled' : ''}">
									<div class="col-md-3">
										<spring:message code="sp.no.of.recurrence" var="visible" />
										<label class="control-label">${visible}</label>
									</div>
									<div class="col-md-5">
										<div class="col-md-1">
											&nbsp;
										</div>
										<div class="col-md-5 ${spForm.isRecurrenceEvaluation ? '' : 'disabled'}">
											<form:input path="noOfRecurrence" type="text" id="noOfRecurrence" data-validation="length"  data-validation-length="max3" class="form-control autoSave" />
										</div>
										<div class="col-md-2">
											<spring:message code="sp.recurrence.times" var="times" />
											<label class="control-label" style="margin-top: 10px;">${times}</label>
										</div>
									</div>
								</div>
							</div>
							<!-- Supplier Performance Criteria -->
							<div class="panel sum-accord">
								<div class="panel-heading">
									<h4 class="panel-title">
										<a data-toggle="collapse" class="accordion" href="#collapseEight"> <spring:message code="sp.evaluation.title" /> </a>
									</h4>
								</div>
								<div id="collapseEight" class="panel-collapse">
									<div class="panel-body pad_all_15">
										<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
											
											<div class="import-supplier-inner-first-new global-list form-middle">
												<div class="ph_tabel_wrapper">
													<div class="main_table_wrapper ph_table_border mega" style="margin: 0;">
														<table class="ph_table border-none display table table-bordered " width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
															<thead>
																<tr>
																	<th class="for-left width_50 width_50_fix"><spring:message code="application.no2" /></th>
																	<th class="for-left width_200 width_200_fix"><spring:message code="performance.criteria.lable" /></th>
																	<th class="for-left width_100 width_100_fix"><spring:message code="label.maxscore" /></th>
																	<th class="for-left width_100 width_100_fix"><spring:message code="label.weightage" /></th>
																</tr>
															</thead>
															<tbody>
																<c:forEach items="${spForm.criteria}" var="item" varStatus="status">
																	<tr>
																		<form:hidden path="criteria[${status.index}].id"/>
																		<c:if test="${item.order == 0}">
																			<td class="for-left width_50 width_50_fix section_name"> ${item.level}.${item.order} </td>
																			<td class="for-left width_200 width_200_fix section_name" align="center">${item.name}</td>
																			<td class="for-left width_100 width_100_fix section_name" align="center"><fmt:formatNumber type="number" value="${item.maximumScore}" /></td>
																			<c:if test="${item.allowToUpdateSectionWeightage && spForm.formStatus != 'SUSPENDED'}">
																				<td class="for-left width_100 width_100_fix" align="center">
																					<form:input autocomplete="off" path="criteria[${status.index}].weightage" class="form-control validateNumber" data-validation="number required custom" 
																						data-validation-allowing="range[0.1;100.00],float" data-validation-error-msg-number="Input value must be numeric within range from 0.1 to 100.00"
																						data-validation-regexp="^\d{0,3}(\.\d{1,2})?$" data-validation-ignore="," data-validation-error-msg="Only numbers allowed, length should be less than 3 and after decimal 2 digits allowed"
																						/>
																				</td>
																			</c:if>
																			<c:if test="${!item.allowToUpdateSectionWeightage || spForm.formStatus == 'SUSPENDED'}">
																				<td class="for-left width_100 width_100_fix" align="center">${item.weightage}</td>
																			</c:if>
																		</c:if>
																	
																		<c:if test="${item.order != 0}">
																			<td class="for-left width_50 width_50_fix"> ${item.level}.${item.order} </td>
																			<td class="for-left width_200 width_200_fix" align="center">${item.name}</td>
																			<td class="for-left width_100 width_100_fix" align="center"><fmt:formatNumber type="number" value="${item.maximumScore}" /></td>
																			<td class="for-left width_100 width_100_fix" align="center">${item.weightage}</td>
																		</c:if>
																	</tr>
																</c:forEach>
															</tbody>
														</table>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</form:form>
					</div>
					<div class="btn-next">
						<c:if test="${!buyerReadOnlyAdmin}">
							<input type="button" id="submitStep1EventDetail" class="top-marginAdminList step_btn_1 btn btn-info ph_btn hvr-pop hvr-rectangle-out submitStep1" value=<spring:message code="application.next" /> />
						</c:if>
					</div>
				</section>
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<div class="modal fade" id="confirmDeleteReminder" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="rfaevent.confirm.delete.reminderlist" />
				</label> <input type="hidden" id="deleteIdReminder" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelReminder">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<!-- Add reminder pop-up -->
<div class="modal fade" id="addReminder" role="dialog" data-backdrop="static">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="sp.evaluation.reminder.header" />
				</h3>
				<button class="close for-absulate" data-dismiss="modal" id="crossRem" type="button" style="border: none;">
					<i class="fa fa-times-circle"></i>
				</button>
			</div>
			<form action="${pageContext.request.contextPath}/buyer/saveProductContract" method="post" id="reminder1">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<div class="row d-flex-line mb-15">
						<input name="reminderId" id="reminderId" value="${reminderId}" type="hidden"> <label class="remind-lbl" style="margin-left: 15px;"> <spring:message code="sp.evalutaion.sendreminder" />
						</label>
					</div>
					<div class="row" style="margin-top: 10px">
						<div class="col-md-12">
							<div class="col-md-7 reminderInterval">
								<input name="remindMeDays" id="remindMeDays" data-validation="required,number" data-validation-error-msg-container="#reminderDaysErr" maxlength="3" type="text" class="form-control" />
							</div>
							<div class="col-md-5" >
								<label>DAYS</label>
							</div>
							<div class="col-md-12 text-left" id="reminderDaysErr"></div>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="button" id="saveReminderButton" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">
						<spring:message code="application.save" />
					</button>
					<button type="button" id="reminderCan" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!-- Evaluator Popup -->
<div class="flagvisibility dialogBox" id="removeEvaluatorListPopup">
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="evaluatorListId" name="evaluatorListId" value=""> <input type="hidden" id="evaluatorListType" name="evaluatorListType" value="">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 evaluatorInfoBlock">
				<spring:message code="event.confirm.to.remove" /> "<span></span>" <spring:message code="application.from" /> <span></span> <spring:message code="application.envelope.list" />
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeEvaluatorListPerson" data-original-title='<spring:message code="tooltip.delete" />'><spring:message code="application.delete" /></a>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1"><spring:message code="application.cancel" /></button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>

<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/multiselect/jquery.multi-select.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/multiselect/jquery.quicksearch.js"/>"></script>

<script type="text/javascript">

<c:if test="${(readOnlyPermission or buyerReadOnlyAdmin) or spForm.formOwner.id ne loggedInUserId }">
$(window).bind('load', function() {
	var allowedFields = '.wiz-step,#submitStep1EventDetail,#previousButton';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
});
</c:if>


$(document).delegate('.validateNumber', 'keydown', function(e) {
	// Allow: backspace, delete, tab, escape, enter and .
	if ($.inArray(e.keyCode, [ 46, 8, 9, 27, 13, 110, 190 ]) !== -1 ||
	// Allow: Ctrl+A, Command+A
	(e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) ||
	// Allow: home, end, left, right, down, up
	(e.keyCode >= 35 && e.keyCode <= 40)) {
		// let it happen, don't do anything
		return;
	}
	// Ensure that it is a number and stop the keypress
	if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
		e.preventDefault();
	}
});

$.validate({
	lang : 'en',
	onfocusout : false,
	validateOnBlur : true,
	modules : 'date,sanitize'
});


$(function() {
	"use strict";
	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

	$('.bootstrap-datepicker').bsdatepicker({
		format : 'dd/mm/yyyy',
		minDate : now,
		onRender : function(date) {
			return date.valueOf() < now.valueOf() ? 'disabled' : '';
		}

	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
	});
	
	$('#submitStep1EventDetail').click(function(e) {
		e.preventDefault();
		if($('#demo-form1').isValid()) {
			$('#loading').show();
			$('#demo-form1').submit();
		}
	});
	
	$('.bootstrap-datepicker').datepicker().on('changeDate', function (ev) {
	    $('.bootstrap-datepicker').change();
	});
	
});

var formStatus = '${spForm.formStatus}';

// Add Evaluator
$('.addEvaluatorToList').click(function(e) {

	e.preventDefault();
	var currentBlock = $(this);
	var listType = currentBlock.attr('list-type');
	var userId = $('#evaluatorList1').val();
	console.log(userId);
	var spFormId = $("#formId").val();

	if (userId === undefined || userId === "" || userId === null) {
		$("#evaluator-err").removeClass("hide");
	}

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getContextPath() + '/buyer/addEvaluatorToList';
	$.ajax({
		type : "POST",
		url : ajaxUrl,

		data : {
			'userId' : userId,
			'listType' : listType,
			'spFormId' : spFormId,
		},
		beforeSend : function(xhr) {

			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success : function(data) {
			$('.evaluatorList.marginDisable').html('');
			userList = evaluatorListForEvent(data, listType);
			$('.evaluatorList.marginDisable').append(userList);
			currentBlock.parent().prev().find('select').find('option[value="' + userId + '"]').remove();
			currentBlock.parent().prev().find('select').val('').trigger("chosen:updated");
			$('#loading').hide();
			$("#evaluator-err").addClass("hide");
		},
		error : function(request, textStatus, errorThrown) {
			console.log("error");
			console.log("ERROR : " + request.getResponseHeader('error'));
			var error = request.getResponseHeader('error');

			if (request.getResponseHeader('error')) {

				$.jGrowl(request.getResponseHeader('error'), {
					sticky : false,
					position : 'top-right',
					theme : 'bg-red'
				});

				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
			}
			$('#loading').hide();

		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

function evaluatorListForEvent(data, listType) {
	var userList = '';
	$.each(data, function(i, user) {
		userList += '<div class="row" evaluator-id="' + user.id + '">';
		if (listType == 'Approver') {
			userList += '<div class="col-md-10"> <p>' + user.name + '</p> </div>';
			userList += '<div class="col-md-2"> <a href=""  class="downbutton"><i class="fa fa-times-circle"></i></a></div>';
			userList += '</div>';
		} else {
			userList += '<div class="col-md-10"><p>' + user.name + '</p></div>';
		}
		if(formStatus != 'SUSPENDED') {
			userList += '<div class="col-md-2">';
			userList += '<a href="" class="removeEvaluatorsList" list-type="' + listType + '"><i class="fa fa-times-circle"></i></a></div>';
		}
		userList += '</div>';
	});
	if (userList == '') {
		userList += '<div class="row" user-id=""><div class="col-md-12"><p>Add ' + listType + '</p></div></div>';
	}
	// console.log(userList);
	return userList;
}

// Remove Evaluators
$(document).delegate('.removeEvaluatorsList', 'click', function(e) {
	e.preventDefault();
	var currentBlock = $(this);
	var listType = currentBlock.attr('list-type');
	var listUserId = currentBlock.closest('.row').attr('evaluator-id');
	var userName = currentBlock.parent().prev().find('p').text();
	var spFormId = $("#formId").val();

	$("#removeEvaluatorListPopup").dialog({
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
	$("#removeEvaluatorListPopup").find('.evaluatorInfoBlock').find('span:first-child').text(userName);
	$("#removeEvaluatorListPopup").find('.evaluatorInfoBlock').find('span:last-child').text(listType);
	$("#removeEvaluatorListPopup").find('#evaluatorListId').val(listUserId);
	$("#removeEvaluatorListPopup").find('#evaluatorListType').val(listType + "?");
	$('.ui-dialog-title').text('Remove ' + listType);
});

$(document).delegate('.removeEvaluatorListPerson', 'click', function(e) {
	e.preventDefault();

	var userId = $("#removeEvaluatorListPopup").find('#evaluatorListId').val();
	var listType = $("#removeEvaluatorListPopup").find('#evaluatorListType').val();
	var spFormId = $("#formId").val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$.ajax({
		type : "POST",
		url : getBuyerContextPath('removeEvaluatorToList'),
		data : {
			spFormId : spFormId,
			userId : userId
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success : function(data) {
			userList = evaluatorListForEventDropdown(data, listType);
			$("#evaluatorList1").html(userList);
			$("#evaluatorList1").trigger("chosen:updated");

			if ($('.row[evaluator-id="' + userId + '"]').siblings().length == 0) {
				userList = evaluatorListForEvent('', listType);
				$('.row[evaluator-id="' + userId + '"]').closest('.width100.usersListTable').html(userList);
			}
			$('.row[evaluator-id="' + userId + '"]').remove();
			$("#removeEvaluatorListPopup").dialog('close');
			updateUserList('', $("#evaluatorList1"), 'NORMAL_USER');
			$('#loading').hide();
		},
		error : function(e) {
			console.log(e);
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

function evaluatorListForEventDropdown(data, listType) {
	var userList = '<option value="">Select Evaluator</option>';
	$.each(data, function(i, user) {
		if (user.id == null || user.id == '' || user.id == '-1') {
			userList += '<option value="-1" disabled>' + user.name + '</option>';
		} else {
			userList += '<option value="' + user.id + '">' + user.name + '</option>';
		}
	});
	return userList;
}

$(document).ready(function() {
		
	$('#addReminder').on('hidden.bs.modal', function () {
	    $(this).find('form').trigger('reset');
	})
		
	$('.timepicker-example').timepicker({
		disableFocus : true,
		minuteStep : 5,
		/* defaultTime : 'current', */
		/* showInputs : false, */
		disableFocus : true,
		explicitMode : false
	}).on('show.timepicker', function(e) {
		setTimeout(function() {
			$('.bootstrap-timepicker-widget.dropdown-menu.open input').addClass('disabled');
		}, 500);
	}).on('changeTime.timepicker', function(e) {
		
		/* Validate the time input on value change */
		$(e.target).validate();
	});
});

// $('#recurrenceEvaluation').validate(function(valid, elem) {
// 	   console.log('Element '+elem.name+' is '+( valid ? 'valid' : 'invalid'));
// });
	
$("#idAddReminder").click(function(e) {
	e.preventDefault();
	console.log("adding reminder");
	var reminderDate = $.trim($('#evaluationEndDateId').val());
	if (reminderDate != '') {
		$('#addReminder').find('#remindMeDays').val('');
		$("#addReminder").modal("show");
	} else {
		$('#evaluationEndDateId').blur();
	}
});

$("#saveReminderButton").click(function(e) {
	e.preventDefault();
	if ($('#reminder1').isValid()) {
		var remindMeDays = $('#remindMeDays').val();

		if (remindMeDays.length == 0) {
			$.jGrowl("Reminder Days cannot be empty", {
				sticky: false,
				position: 'top-right',
				theme: 'bg-red'
			});
		}

		if ($('.formReminderList').find(('input[value="' + remindMeDays + '"]')).val() == remindMeDays) {
			$.jGrowl("Reminder already exists for same date", {
				sticky: false,
				position: 'top-right',
				theme: 'bg-red'
			});
			return false;
		}

		var evaluationEndDate = $.trim($('#evaluationEndDateId').val());
		console.log('evaluationEndDate', evaluationEndDate);

		var dateParts = evaluationEndDate.split("/");

		console.log('dateParts', dateParts);

		// month is 0-based, that's why we need dataParts[1] - 1
		var reminderDate = new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);
		reminderDate.setDate(reminderDate.getDate() - parseInt(remindMeDays));
		console.log('reminderDate', reminderDate);

		if (reminderDate.getTime() < new Date().getTime()) {
			$.jGrowl("Reminder Date cannot be in past", {
				sticky: false,
				position: 'top-right',
				theme: 'bg-red'
			});
			return false;
		} else {
			$('.formReminderList.marginDisable .blankEvelator').remove();
			var dataHtml = '<div class="row">';
			dataHtml += '<div class="col-md-10"> <p><b>Reminder Date: </b>' + moment(reminderDate).format('DD/MM/YYYY') + '</p> </div>';
			dataHtml += '<div class="col-md-2"> <a href="" class="removeEvaluationReminder"><i class="fa fa-times-circle"></i></a></div>';
			dataHtml += '<input type="hidden" name="remindMeDays" value="' + remindMeDays + '">';
			dataHtml += '<input type="hidden" name="reminderSent" value="false">';
			dataHtml += '</div>';
			$('.formReminderList.marginDisable').append(dataHtml);
		}

		$('#addReminder').modal('toggle');
	}
});

$(document).delegate('.removeEvaluationReminder', 'click', function(e) {
	e.preventDefault();
	$(this).closest('.row').remove();
});

$(document).ready(function(){
	 var txt = "";
	 var valSel ="";
	 var valrem = "";
	  $("#reminderButton ,#reminderCan, #crossRem").click(function(){
		  $("#newMsg").empty();
      });
	 
	 valSel = $(".remindMeTime :selected").attr('value');
	 
	 $("input").keyup(function(){
	     valrem = $("#remindMe").val();
	     txt = "";
	     txt += "Remind me "+"<html><i>"+ valrem +"</i></html>"+" "+ valSel +" before the end date & time .";
	     $("#newMsg").html(txt);
	    
	    });
	 
	 $(".remindMeTime").change(function () {
    	 valSel = $(".remindMeTime :selected").attr('value');
    	  txt = "";
 	     txt += "Remind me "+"<html><i>"+ valrem +"</i></html>"+" "+valSel +" before the end date & time .";
 	     $("#newMsg").html(txt);
    
	    })
});

$(document).on("keyup", "#chosenSupplier_chosen .chosen-search input", keyDebounceDelay(function(e) {
	// ignore arrow keys
	switch (e.keyCode) {
		case 17: // CTRL
			return false;
			break;
		case 18: // ALT
			return false;
			break;
		case 37:
			return false;
			break;
		case 38:
			return false;
			break;
		case 39:
			return false;
			break;
		case 40:
			return false;
			break;
	}
	var supplier = $.trim(this.value);
	if (supplier.length > 2 || supplier.length == 0 || e.keyCode == 8) {
		reloadSupplierList();
	}
}, 650));

function keyDebounceDelay(callback, ms) {
	var timer = 0;
	return function() {
		var context = this, args = arguments;
		clearTimeout(timer);
		timer = setTimeout(function() {
			callback.apply(context, args);
		}, ms || 0);
	};
}

function reloadSupplierList() {
	var searchSupplier = $.trim($('#chosenSupplier_chosen .chosen-search input').val());
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url: getContextPath() + '/buyer/searchFavouriteuppliers',
		data: {
			'searchSupplier': searchSupplier,
		},
		type: 'POST',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data) {
			var html = '';
			if (data != '' && data != null && data.length > 0) {
				$('#chosenSupplier').find('option:not(:first)').remove();
				$.each(data, function(key, value) {
					if (value.id == null || value.id == '') {
						html += '<option value="" disabled>' + value.companyName + '</option>';
					} else {
						html += '<option value="' + value.id + '">' + value.companyName + '</option>';
					}
				});
			}
			$('#chosenSupplier').append(html);
			$("#chosenSupplier").trigger("chosen:updated");
			$('#chosenSupplier_chosen .chosen-search input').val(searchSupplier);
			$('#loading').hide();
		},
		error: function(error) {
			console.log(error);
		}
	});
}

$('#recEvaluation').change(function() {
    if(this.checked) {
    	$('#recurrenceEvaluation').attr('data-validation','length required number');
    	$('#noOfRecurrence').attr('data-validation','length required number');
    	$('#recurrenceEvaluation').parent().removeClass('disabled');
    	$('#noOfRecurrence').parent().removeClass('disabled');
    } else {
    	$('#recurrenceEvaluation').attr('data-validation','length');
    	$('#noOfRecurrence').attr('data-validation','length');
    	$('#recurrenceEvaluation').parent().addClass('disabled');
    	$('#noOfRecurrence').parent().addClass('disabled');
    	$('#recurrenceEvaluation').validate();
    	$('#noOfRecurrence').validate();
    }
});

$('#evaluationStartDate,#evaluationEndDate,#evaluationStartTime,#evaluationEndTime').change(function() {

	if($('#evaluationStartDate').val() !== '' && $('#evaluationEndDate').val() !== '' && $('#evaluationStartTime').val() !== '' && $('#evaluationEndTime').val() !== '') {
	 	var oneDay = 24*60*60*1000;
		
		var evaluationStartDate = $('#evaluationStartDate').val();
		//console.log('evaluationStartDate', evaluationStartDate);
	
		var evaluationEndDate = $('#evaluationEndDateId').val();
		//console.log('evaluationEndDate', evaluationEndDate);

		
		var startDate = moment(evaluationStartDate + ' ' + $('#evaluationStartTime').val() , "DD/MM/YYYY hh:mm a");
		//console.log('start date', startDate);
		
		var endDate = moment(evaluationEndDate + ' ' + $('#evaluationEndTime').val() , "DD/MM/YYYY hh:mm a");
		//console.log('end date', endDate);
		
		var duration = endDate.diff(startDate, "minutes");
		//console.log("Duration", duration);
		
		
		if(duration < 0) {
			duration = 0;
		} else if(duration > 0) {
			duration = duration/(24*60);
		}
		duration = Math.ceil(duration);	
		
		$('#evaluationDuration').text(duration);
		$('#evaluationDurationHidden').attr("value", duration);
	}
	
});

$('#evaluationEndDateId').datepicker().on('changeDate', function (ev) {
    var endDate = $('#evaluationEndDateId').val();
    var dt = new Date();
   	var time = dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
      
    $('input[name="remindMeDays"]').each(function(i, item) {
    	var days = parseInt($(this).val());
    	var alertDate = new Date();
    	alertDate.setTime(ev.date.getTime()-(days * 24 * 60 * 60 * 1000));
     	if(dt > alertDate){
    		$(this).closest('.row').remove();
    	} else {
    		$(this).parent().find('p').html('<b>Reminder Date: </b>' + moment(alertDate).format('DD/MM/YYYY'));
    	}
    });
});
		
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/ajaxFormPlugin.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<!-- daterange picker js and css start -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<!-- EQul height js-->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
