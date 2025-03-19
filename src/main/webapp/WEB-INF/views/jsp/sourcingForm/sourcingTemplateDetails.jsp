<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('ROLE_REQUEST_TEMPLATE_EDIT') or hasRole('ADMIN')" var="canEdit" />
<spring:message var="rfxCreateDetails" code="application.rfx.create.details" />
<spring:message code="sourcing.template.inactivate.label" var="inactivateLabel" />
<spring:message code="buyer.dashboard.active" var="activeLabel" />
<spring:message code="sourcing.template.inactive.label" var="inactiveLabel" />
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
					</a></li>
					<c:if test="${! empty sourceForm.formName}">
						<li class="active"><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/sourceTemplateList"><spring:message code="sourcingtemplates.list" /> &nbsp>&nbsp</li>
					</c:if>
					<c:if test="${ empty sourceForm.formName}">
						<li class="active"><a href="${pageContext.request.contextPath}/buyer/sourceTemplateList"><spring:message code="sourcingtemplates.list" /> &nbsp>&nbsp</li>
					</c:if>
					<li class="active"><a href="#"> <c:out value='${createTemplate}' /></li>
				</ol>
				<section class="create_list_sectoin">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap tender-request-heading">${createTemplate}</h2>
						<h2 class="trans-cap pull-right">
							<spring:message code="application.status" />
							: ${sourceForm.status}
						</h2>
					</div>
					<div class="clear"></div>
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<div class="clear"></div>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<jsp:include page="sourcingTemplateHeader.jsp"></jsp:include>
					<div class="clear"></div>
					<c:url var="saveSourcingFormTemplate" value="/buyer/saveSourcingFormTemplate" />
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<form:form id="sourcetemplate" action="${saveSourcingFormTemplate}" method="post" modelAttribute="sourceForm">
						<input type="hidden" name="id" value="${sourceForm.id}">
						<div class="tab-pane active">
							<div class="tab-content Invited-Supplier-List ">
								<div class="tab-pane active white-bg" id="step-1">
									<div class="upload_download_wrapper clearfix mar-t20 event_info">
										<h4>
											<spring:message code="sourcing.template.details" />
										</h4>
										<div class="row">
											<div class="form-tander1 pad_all_15">
												<div class="col-sm-4 col-md-3 col-xs-6">
													<label> <spring:message code="rfxTemplate.templateName" />
													</label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-6 ">
													<spring:message var="formname" code="sourcingtemplate.name" />
													<form:input path="formName" type="text" placeholder="${formname}" data-validation="required length" data-validation-length="max64" class="form-control" />
												</div>
											</div>
											<div class="form-tander1 pad_all_15">
												<div class="col-sm-4 col-md-3 col-xs-6">
													<label> <spring:message code="template.description" />
													</label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<spring:message var="placeDescription" code="sourcingtemplate.description" />
													<form:textarea path="description" maxlength="1000" placeholder="${placeDescription}" rows="5" data-validation="length" data-validation-length="max250" class="form-control"></form:textarea>
													<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-10 marg-top-10">
							<div class="meeting2-heading">
								<h3>
									<spring:message code="sourcing.procurement.info" />
								</h3>
							</div>
							<div class="import-supplier-inner-first-new pad_all_15 global-list">

								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="procurement.info.method" /></label>
									</div>
									<div class="col-md-3 dd sky ${isTemplateUsed ? 'disabled':''} ">
										<form:select class="chosen-select" id="chosenProcurementMethod" path="templateFieldBinding.procurementMethod">
											<form:option value="">
												<spring:message code="procurement.info.method.select" />
											</form:option>
											<c:forEach items="${procurementMethodList}" var="procurementMethod">
												<form:option value="${procurementMethod.id}">${procurementMethod.procurementMethod}</form:option>
											</c:forEach>
										</form:select>
									</div>
									<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.visible" var="visible" />
										<form:checkbox id="procurementMethodVisibleId" path="templateFieldBinding.procurementMethodVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.read.only" var="read" />
										<form:checkbox id="procurementMethodReadOnlyId" path="templateFieldBinding.procurementMethodDisabled" class="custom-checkbox" title="${read}" label="${read}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.optional" var="optional" />
										<form:checkbox id="procurementMethodOptionalId" path="templateFieldBinding.procurementMethodOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
									</div>
								</div>
								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="procurement.info.category" /></label>
									</div>
									<div class="col-md-3 dd sky ${isTemplateUsed ? 'disabled':''} ">
										<form:select class="chosen-select" id="chosenProcurementCategory" path="templateFieldBinding.procurementCategory">
											<form:option value="">
												<spring:message code="procurement.info.category.select" />
											</form:option>
											<c:forEach items="${procurementCategoryList}" var="procurementCategory">
												<form:option value="${procurementCategory.id}">${procurementCategory.procurementCategories}</form:option>
											</c:forEach>
										</form:select>
									</div>
									<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.visible" var="visible" />
										<form:checkbox id="procurementCategoryVisibleId" path="templateFieldBinding.procurementCategoryVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.read.only" var="read" />
										<form:checkbox id="procurementCategoryReadOnlyId" path="templateFieldBinding.procurementCategoryDisabled" class="custom-checkbox" title="${read}" label="${read}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.optional" var="optional" />
										<form:checkbox id="procurementCategoryOptionalId" path="templateFieldBinding.procurementCategoryOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
									</div>
								</div>
							</div>
						</div>


						<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-10">
							<div class="meeting2-heading">
								<h3>
									<spring:message code="sourcing.template.budeget" />
								</h3>
							</div>
							<div class="import-supplier-inner-first-new pad_all_15 global-list">
								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="label.base.currency" /></label>
									</div>
									<div class="col-md-3 dd sky ${isTemplateUsed ? 'disabled':''} ">
										<form:select class="chosen-select" id="chosenBaseCurrency" path="templateFieldBinding.baseCurrency">
											<form:option value="">
												<spring:message code="select.base.currency" />
											</form:option>
											<c:forEach items="${baseCurrencyList}" var="baseCurrency">
												<form:option value="${baseCurrency.id}">${baseCurrency.currencyCode}</form:option>
											</c:forEach>
										</form:select>
									</div>
									<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.visible" var="visible" />
										<form:checkbox id="basecurrencyVisibleId" path="templateFieldBinding.baseCurrencyVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.read.only" var="read" />
										<form:checkbox id="baseCcurrencyReadOnlyId" path="templateFieldBinding.baseCurrencyDisabled" class="custom-checkbox" title="${read}" label="${read}" />
									</div>
								</div>


								<div class="form-tander1">
									<div class="col-md-3 pl-0">
										<label class="marg-top-10"><spring:message code="buyersettings.decimal" /></label>
									</div>
									<div class="col-md-3 pl-7 dd sky ${isTemplateUsed ? 'disabled':''}">
										<form:select path="decimal" cssClass="form-control chosen-select decimalChange" data-validation="required">
											<form:option value="">
												<spring:message code="buyersettings.selectdacimal" />
											</form:option>
											<form:option value="1"></form:option>
											<form:option value="2"></form:option>
											<form:option value="3"></form:option>
											<form:option value="4"></form:option>
										</form:select>
										<div id="decimalDisabledError"></div>
									</div>
								</div>
								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="label.costcenter" /></label>
									</div>
									<div class="col-md-3 dd sky ${isTemplateUsed ? 'disabled':''} ">
										<form:select class="chosen-select" id="chosenCostCenter" path="templateFieldBinding.costCenter">
											<form:option value="">
												<spring:message code="rfs.select.cost.center" />
											</form:option>
											<c:forEach items="${costCenterList}" var="costCenter">
												<form:option value="${costCenter.id}">${costCenter.costCenter} - ${costCenter.description}</form:option>
											</c:forEach>
										</form:select>
									</div>
									<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.visible" var="visible" />
										<form:checkbox path="templateFieldBinding.costCenterVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.read.only" var="read" />
										<form:checkbox path="templateFieldBinding.costCenterDisabled" class="custom-checkbox" title="${read}" label="${read}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.optional" var="optional" />
										<form:checkbox path="templateFieldBinding.costCenterOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
									</div>
								</div>
								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="label.businessUnit" /></label>
									</div>
									<div class="col-md-3 dd sky ${isTemplateUsed ? 'disabled':''}">
										<form:select class="chosen-select" id="chosenBusinessUnit" path="templateFieldBinding.businessUnit">
											<form:option value="">
												<spring:message code="pr.select.business.unit" />
											</form:option>
											<form:options items="${businessUnitList}" itemValue="id" itemLabel="unitName" />
										</form:select>
										<div id="businessUnitDisabledError"></div>
									</div>
									<div class="check-wrapper first hide ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="" class="custom-checkbox" title="${visible}" label="${visible}" value="true" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.businessUnitDisabled" class="custom-checkbox" title="${read}" label="${read}" />
									</div>
									<div class="check-wrapper hide ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="" class="custom-checkbox" title="${optional}" label="${optional}" value="true" />
									</div>
								</div>
								
								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="label.groupCode" /></label>
									</div>
									<div class="col-md-3 dd sky ${isTemplateUsed ? 'disabled':''} ">
										<form:select class="chosen-select" id="chosenGroupCode" path="templateFieldBinding.groupCode">
											<form:option value="">
												<spring:message code="rfs.select.group.Code" />
											</form:option>
											<c:forEach items="${groupCodeList}" var="grpC">
												<form:option value="${grpC.id}">${grpC.groupCode} - ${grpC.description}</form:option>
											</c:forEach>
										</form:select>
									</div>
									<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.visible" var="visible" />
										<form:checkbox path="templateFieldBinding.groupCodeVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.read.only" var="read" />
										<form:checkbox path="templateFieldBinding.groupCodeDisabled" class="custom-checkbox" title="${read}" label="${read}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="prtemplate.label.optional" var="optional" />
										<form:checkbox path="templateFieldBinding.groupCodeOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
									</div>
								</div>
								
								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="label.availableBudget" /></label>
									</div>
									<div class="col-md-2 ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="enter.available.budget.placeholder" var="budget" />
										<fmt:formatNumber var="budgetAmount" type="number" minFractionDigits="${sourceForm.decimal}" maxFractionDigits="${sourceForm.decimal}" value="${sourceForm.templateFieldBinding.budgetAmount}" />
										<form:input path="templateFieldBinding.budgetAmount" autocomplete="off" value="${budgetAmount}" id="budgetAmount" name="budgetAmount" data-validation="validate_custom_length positive" placeholder="${budget}" class="form-control" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${sourceForm.decimal}})?$" />
										<span class="customError"></span>
									</div>
									<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.budgetAmountVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.budgetAmountDisabled" class="custom-checkbox" title="${read}" label="${read}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.budgetAmountOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
									</div>
								</div>


								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="rfs.estimatedBudget.label" /></label>
									</div>
									<div class="col-md-2 ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="enter.available.estimated.placeholder" var="estimated" />
										<fmt:formatNumber var="estimatedBudget" type="number" minFractionDigits="${sourceForm.decimal}" maxFractionDigits="${sourceForm.decimal}" value="${sourceForm.templateFieldBinding.estimatedBudget}" />
										<form:input path="templateFieldBinding.estimatedBudget" autocomplete="off" value="${estimatedBudget}" id="estimatedBudget" name="estimatedBudget" data-validation="validate_custom_length positive" placeholder="${estimated}" class="form-control" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${sourceForm.decimal}})?$" />
										<span class="customError"></span>
									</div>
									<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.estimatedBudgetVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.estimatedBudgetDisabled" class="custom-checkbox" title="${read}" label="${read}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.estimatedBudgetOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
									</div>
								</div>
								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="rfs.historicAmount.label" /></label>
									</div>
									<div class="col-md-2 ${isTemplateUsed ? 'disabled':''}">
										<spring:message code="enter.available.historic.placeholder" var="estimated" />
										<fmt:formatNumber var="historicaAmount" type="number" minFractionDigits="${sourceForm.decimal}" maxFractionDigits="${sourceForm.decimal}" value="${sourceForm.templateFieldBinding.historicAmount}" />
										<form:input path="templateFieldBinding.historicAmount" autocomplete="off" value="${historicaAmount}" id="historicaAmount" name="historicaAmount" data-validation="validate_custom_length positive" placeholder="${estimated}" class="form-control" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${sourceForm.decimal}})?$" />
										<span class="customError"></span>
									</div>
									<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.historicAmountVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.historicAmountDisabled" class="custom-checkbox" title="${read}" label="${read}" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.historicAmountOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
									</div>
								</div>
								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="sourcing.minimumSupplierRating" /></label>
									</div>
									<div class="col-md-3 ${isTemplateUsed ? 'disabled':''}">
										<form:input path="templateFieldBinding.minimumSupplierRating" autocomplete="off" value="${templateFieldBinding.minimumSupplierRating}" id="minimumSupplierRating" name="minimumSupplierRating" placeholder="Enter Minimum Supplier Rating" class="form-control" data-validation-optional="true" data-validation="number custom validateMin"
											data-validation-allowing="range[0.00;9999.99],float" data-validation-regexp="^\d+\.?\d{0,2}$" data-validation-error-msg-number="Input value must be numeric within range from 0 to 9999.99" data-validation-error-msg-custom="Input value must be numeric within range from 0 to 9999.99" />
										<span class="customError"></span>
									</div>
									<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.minimumSupplierRatingVisible" class="custom-checkbox" title="Visible" label="Visible" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.minimumSupplierRatingDisabled" class="custom-checkbox" title="Read Only" label="Read Only" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.minimumSupplierRatingOptional" class="custom-checkbox" title="Optional" label="Optional" />
									</div>
								</div>
								<div class="row marg-bottom-10">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="sourcing.maximumSupplierRating" /></label>
									</div>
									<div class="col-md-3 ${isTemplateUsed ? 'disabled':''}">
										<form:input path="templateFieldBinding.maximumSupplierRating" autocomplete="off" value="${templateFieldBinding.maximumSupplierRating}" id="maximumSupplierRating" name="maximumSupplierRating" placeholder="Enter Maximum Supplier Rating" class="form-control" data-validation-optional="true" data-validation="number custom validateMax"
											data-validation-allowing="range[1.00;9999.99],float" data-validation-regexp="^\d+\.?\d{0,2}$" data-validation-error-msg-number="Input value must be numeric within range from 1 to 9999.99" data-validation-error-msg-custom="Input value must be numeric within range from 1 to 9999.99" />
										<span class="customError"></span>
									</div>
									<div class="check-wrapper first ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.maximumSupplierRatingVisible" class="custom-checkbox" title="Visible" label="Visible" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.maximumSupplierRatingDisabled" class="custom-checkbox" title="Read Only" label="Read Only" />
									</div>
									<div class="check-wrapper ${isTemplateUsed ? 'disabled':''}">
										<form:checkbox path="templateFieldBinding.maximumSupplierRatingOptional" class="custom-checkbox" title="Optional" label="Optional" />
									</div>
								</div>
							</div>
						</div>
						<div class="upload_download_wrapper collapseable  clearfix event_info ">
							<h4>
								<spring:message code="sourcing.event.team.members" />
							</h4>
							<div id="apprTab" data-aproval="" class="pad_all_15 collapse in float-left width-100 position-relative in ${isTemplateUsed? 'disabled':''}">
								<jsp:include page="sourcingTemplateTeamMembers.jsp"></jsp:include>
							</div>
						</div>
						<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab  in">
							<h4>
								<spring:message code="rfi.createrfi.approvalroute.label.2" />
							</h4>
							<div id="apprTab" data-aproval="" class="pad_all_15 collapse in float-left width-100 position-relative in ${isTemplateUsed? 'disabled':''}">
								<jsp:include page="sourcingFormApproval.jsp" />
							</div>
						</div>



						<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab  in">
							<h4>
								<spring:message code="template.user" />
							</h4>
							<div id="templateUserTab" data-aproval="" class="pad_all_15 collapse in float-left width-100 position-relative in ${isTemplateUsed? 'disabled':''}">
								<div class="row">
									<div class="col-sm-4 col-md-3">
										<label class="marg-top-10"><spring:message code="assign.template.user" /></label>
									</div>
									<div class="col-md-4 col-sm-6">
										<div class="input-group search_box_gray disp-flex">
											<select class="chosen-select user-list-normal-sourcing-template" id="selectedUserList" selected-id="data-value">
												<option value=""><spring:message code="placeholder.search.user.templates" /></option>
												<c:forEach items="${assignedUserListDropDown}" var="userList">
													<c:if test="${userList.id == '-1'}">
														<option value="-1" disabled>${userList.name}</option>
													</c:if>
													<c:if test="${userList.id != '-1' }">
														<option value="${userList.id}">${userList.name}</option>
													</c:if>
												</c:forEach>
											</select>
										</div>
										<div class="error templateUserError" hidden>
											<spring:message code="please.select.user" />
										</div>
									</div>
									<div class="col-md-2 col-sm-2">
										<button class="btn btn-info hvr-pop hvr-rectangle-out" type="button" data-toggle="tooltip" data-placement="top" id="addMoreUsers">
											<i class="fa fa-plus" aria-hidden="true"></i>
										</button>
									</div>
								</div>
								<div class="clear"></div>
								<div class="container-fluid">
									<div class="row">
										<div class="col-xs-12">
											<section class="index_table_block marg-top-20">
												<div class="ph_tabel_wrapper scrolableTable_UserList">
													<table id="tableList1" class="ph_table display table table-bordered noarrow" cellspacing="0">
														<thead>
															<tr>
																<th class=""><spring:message code="application.action1" /></th>
																<th class=""><spring:message code="application.username" /></th>
															</tr>
														</thead>
													</table>
												</div>
											</section>
										</div>
									</div>
								</div>
							</div>
						</div>



						<%-- <div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
							<div class="meeting2-heading">
								<div class="checkbox checkbox-primary">
									<label> <spring:message code="rfi.createrfi.approvalroute.label" /> </label>
								</div>
							</div>
							<div id="apprTab" data-aproval="" class="pad_all_15 collapse in float-left width-100 position-relative in ${isTemplateUsed? 'disabled':''}  ">
								<div class="">
									<jsp:include page="sourcingFormApproval.jsp" />
								</div>
							</div>
						</div> --%>
						<div class="btn-next">
							<c:if test="${(sourceForm.status eq 'DRAFT' and !isTemplateUsed) }">
								<form:button type="submit" id="saveRfxTemplate" class="btn1 btn btn-info ph_btn step_btn_1 marg-top-20 hvr-pop hvr-rectangle-out submitStep1">
								${button}
								</form:button>
							</c:if>
							<c:if test="${flag}">
								<form:button type="submit" id="saveRfxTemplate" class="btn1 btn btn-info ph_btn step_btn_1 marg-top-20 hvr-pop hvr-rectangle-out submitStep1">
								${button}
								</form:button>
							</c:if>
							<c:if test="${sourceForm.status ne 'DRAFT' and isTemplateUsed}">
								<c:url value="/buyer/sourcingTemplateDocument/${sourcingForm.id}" var="createUrl" />
								<a href="${createUrl}" class="btn1 btn btn-info ph_btn step_btn_1 marg-top-20 hvr-pop hvr-rectangle-out submitStep1"><spring:message code="application.next" /></a>
							</c:if>
							<c:if test="${sourceForm.status ne 'DRAFT' and !isTemplateUsed}">
								<c:if test="${flag}">
									<c:if test="${isDuplicateName}">
										<c:url value="/buyer/sourcingTemplateDocument/${sourcingForm.id}" var="createUrl" />
										<a href="${createUrl}" class="btn1 btn btn-info ph_btn step_btn_1 marg-top-20 hvr-pop hvr-rectangle-out submitStep1"> <spring:message code="application.next" />
										</a>
									</c:if>
								</c:if>
								<c:if test="${!flag}">
									<form:button type="submit" id="saveRfxTemplate" class="btn1 btn btn-info ph_btn step_btn_1 marg-top-20 hvr-pop hvr-rectangle-out submitStep1">
								${button}
								</form:button>
								</c:if>
							</c:if>
							<spring:message code="application.cancel" var="cancel" />
							<input type="button" id="submitStep1PrDetailDraft" class="btn1 top-marginAdminList step_btn_1 btn btn-black marg-left-10 hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 skipvalidation" value="${cancel}" onclick="location.href='sourceTemplateList';" />

							<spring:message code="application.draft" var="Active" />
							<spring:message code="application.draft" var="draft" />
							<spring:message code="application.draft" var="Active" />
							<c:if test="${ not empty templateId and sourceForm.status ne 'DRAFT' and canEdit}">
								<!-- 								<input type="button" id="activePrTemplate" -->
								<!-- 									class="btn btn-info ph_btn step_btn_1 marg-top-20 hvr-pop hvr-rectangle-out submitStep1 pull-right" -->
								<!-- 									value="Active" /> -->
								<%-- 				<input type="button" id="activePrTemplate" class="btn1 btn marg-left-10 btn-info ph_btn step_btn_1 marg-top-20 hvr-pop hvr-rectangle-out submitStep1 pull-right" value="${sourceForm.status eq 'ACTIVE' ? 'Inactive' : 'Active' }" /> --%>
								<a id="activePrTemplate" class="btn1 btn ph_btn step_btn_1 hvr-pop marg-top-20 submitStep1 pull-right   ${sourceForm.status eq 'ACTIVE' ? 'btn-danger' : 'btn-info hvr-rectangle-out' }">${sourceForm.status eq 'ACTIVE' ? inactivateLabel : activeLabel }</a>
							</c:if>
							<c:if test="${ not empty sourceForm.id and sourceForm.status ne 'DRAFT' and canEdit}">
								<spring:message code="application.saveas" var="saveas" />
								<input type="button" id="saveAsPrTemplate" style="margin-right: 1%;" class="btn1 btn ph_btn step_btn_1 marg-top-20 btn-warning hvr-pop hvr-rectangle-out pull-right submitStep1" value="${saveas}" />
							</c:if>
							<input type="hidden" name="userId" id="userId" />
						</div>
					</form:form>
			</div>
		</div>
	</div>
</div>
<!-- TEMPLATE SAVE AS POPUP -->
<div class="flagvisibility dialogBox" id="prTemplateSaveAsPopup" title="Template Save As">
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="templateId" value="${templateId}" />
		<form>
			<div class="marg-top-20 tempaleData">
				<div class="row marg-bottom-10">
					<input type="hidden" id="templateId" name="templateId" value="${templateId}" />
					<div class=" col-md-4">
						<label> <spring:message code="rfxTemplate.templateName" />
						</label>
					</div>
					<div class="col-md-8">
						<spring:message code="rfxTemplate.templateDescription.placeHolder" var="desc" />
						<spring:message code="rfxTemplate.templateName.placeHolder" var="name" />
						<spring:message code="prtemplate.enter.template" var="tempnameplace" />
						<input data-validation="required length" data-validation-length="max64" class="form-control marg-bottom-10" name="tempName" id="tempName" placeholder="${tempnameplace}" /> <span class="customError"></span>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-md-4">
						<label> <spring:message code="rfxTemplate.templateDescription" />
						</label>
					</div>
					<div class="col-md-8">
						<spring:message code="prtemplate.template.description" var="tempdesc" />
						<textarea name="tempDescription" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" id="tempDescription" placeholder="${tempdesc}"></textarea>
						<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
					</div>
				</div>
			</div>
		</form>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<button type="button" title="" class=" btn btn-info ph_btn_midium btn-tooltip hvr-pop hvr-rectangle-out" id="saveAsSourcingTemp" data-original-title="Delete">
						<spring:message code="application.create" />
					</button>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- TEMPLATE ACTIVE AS POPUP -->
<div class="flagvisibility dialogBox" id="prTemplateactivePopup" title="Template Active">
	<div class="float-left width100 pad_all_15 white-bg">
		<form>
			<div class="marg-top-20 tempaleData"></div>
			<input type="hidden" value="${sourceForm.status eq 'ACTIVE' ? inactiveLabel : activeLabel }" id="tempTitle">
		</form>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<form action="${pageContext.request.contextPath}/buyer/activeSourcingFormTemplate" method="post">
						<input type="hidden" id="templateId" name="templateId" value="${templateId}" /> 
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
 						<input type="submit" class="btn ph_btn_midium btn-tooltip hvr-pop  ${sourceForm.status eq 'ACTIVE' ? 'btn-danger' : 'btn-info hvr-rectangle-out' }"  value="${sourceForm.status eq 'ACTIVE' ? inactivateLabel : activeLabel }" /> 
						<a class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1"><spring:message code="application.cancel" /></a>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>

<!--Template user Delete  popup-->
<div class="flagvisibility dialogBox" id="removeTemplateUserListPopup" title='<spring:message code="tooltip.remove.template.user" />'>
	<div class="float-left width100 pad_all_15 white-bg">
		<!-- <input type="hidden" id="approverListId" name="approverListId" value=""> <input type="hidden" id="approverListName" name="approverListName" value=""> -->
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock2">
				<spring:message code="template.confirm.to.remove" />
				<span></span>
				<spring:message code="template.confirm.to.remove.userlist" />
				<span></span> ?
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<button type="button" id="deleteUser" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out">
						<spring:message code="application.remove" />
					</button>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>
<style>
<!--
.btn1 {
	height: 38px !important;
	min-width: 180px !important;
	font-size: 16px !important;
	line-height: 38px;
}

.marg-left-15 {
	margin-left: 15px;
}

.marg-right-1 {
	margin-right: 1%;
}
-->
</style>
<style>
.radio>span {
	padding: 0 !important;
}

.pl-0 {
	padding-left: 0;
}

.pl-7 {
	padding-left: 7px;
}

.check-wrapper {
	width: 115px;
}

@media ( min-width : 992px) {
	.col-md-2 {
		width: 25% !important;
	}
}

.templateUserError {
	color: #ff5757 !important;
}

.dialogBlockLoaded2 {
	border: 1px solid rgba(0, 0, 0, .2)!;
	-webkit-box-shadow: 0 5px 15px rgba(0, 0, 0, .5) !important;
	box-shadow: 0 5px 15px rgba(0, 0, 0, .5) !important;
}
</style>
<script>

<c:if test="${assignedCount > 0}">
//Remove click event handler on checkbox labels. It affects disabled checkboxes
$('.check-wrapper').on('click', function(e){
	e.preventDefault();
	return false;
});
</c:if>
	$.validate({
		lang : 'en',
		modules : 'date,sanitize'
	});

	$(function() {
		$("[data-role=submit]").click(function() {
			$(this).closest("form").submit();
		});
	});

	$.formUtils.addValidator({
		name : 'validate_custom_length',
		validatorFunction : function(value, $el, config, language, $form) {
			var val = value.split(".");
			if (val[0].replace(/,/g, '').length > 13) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The input value is longer than 13 characters',
		errorMessageKey : 'validateLengthCustom'
	});
	$.formUtils.addValidator({
		name : 'validateMax',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var minValue=$("#minimumSupplierRating").val();
			 var maxValue=$("#maximumSupplierRating").val();
			 if(maxValue != '' && minValue != '') {
				 if(parseFloat($("#maximumSupplierRating").val()) < parseFloat($("#minimumSupplierRating").val()) && parseFloat($("#maximumSupplierRating").val()) != parseFloat($("#minimumSupplierRating").val())){
					 response = false;
				 }
			 }
			return response;
		},
		errorMessage : 'Maximum supplier Rating/Grade must be greater than Minimum Supplier Rating/Grade',
		errorMessageKey : 'badMinMax'
	});

	$.formUtils.addValidator({
		name : 'validateMin',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var minValue=$("#minimumSupplierRating").val();
			 var maxValue=$("#maximumSupplierRating").val();
			 if(maxValue != '' && minValue != '') {
				 if(parseFloat($("#minimumSupplierRating").val()) > parseFloat($("#maximumSupplierRating").val()) && parseFloat($("#maximumSupplierRating").val()) != parseFloat($("#minimumSupplierRating").val())){
					 response = false;
				 }
			 }
			return response;
		},
		errorMessage : 'Minimum supplier Rating/Grade must be smaller than Maximum Supplier Rating/Grade',
		errorMessageKey : 'badMinMax'
	});

	$('#maximumSupplierRating').on('keyup', function() {
		$('#maximumSupplierRating').validate(function(valid, elem) {});
		$('#minimumSupplierRating').validate(function(valid, elem) {});
	});
	  
	$('#minimumSupplierRating').on('keyup', function() {
		$('#minimumSupplierRating').validate(function(valid, elem) {});
		$('#maximumSupplierRating').validate(function(valid, elem) {});
	});
	
	var userList2=[];
	var userId2=[];
	
	$(document).ready(
			function() {
			$(document).delegate('#budgetAmount', 'change', function(e) {
				var decimalLimit = $('.decimalChange').val();
				console.log("decimalLimit"+decimalLimit);
				var budgetAmount = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
				budgetAmount = !isNaN(budgetAmount) ? budgetAmount.toFixed(decimalLimit) : '';
				console.log(ReplaceNumberWithCommas((budgetAmount)));
				$('#budgetAmount').val(ReplaceNumberWithCommas((budgetAmount)));
			});
			
			$(document).delegate('#historicaAmount', 'change', function(e) {
				var decimalLimit = $('.decimalChange').val();
				var historicaAmount = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
				historicaAmount = !isNaN(historicaAmount) ? historicaAmount.toFixed(decimalLimit) : '';
				console.log(ReplaceNumberWithCommas((historicaAmount)));
				$('#historicaAmount').val(ReplaceNumberWithCommas((historicaAmount)));
			});
			$(document).delegate('#estimatedBudget', 'change', function(e) {
				var decimalLimit = $('.decimalChange').val();
				var estimatedBudget = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
				estimatedBudget = !isNaN(estimatedBudget) ? estimatedBudget.toFixed(decimalLimit) : '';
				console.log(ReplaceNumberWithCommas((estimatedBudget)));
				$('#estimatedBudget').val(ReplaceNumberWithCommas((estimatedBudget)));
			});
					
	function ReplaceNumberWithCommas(yourNumber) {
		// Seperates the components of the number
		var n = yourNumber.toString().split(".");
		// Comma-fies the first part
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		// Combines the two sections
		return n.join(".");
	}

	
	$("#addMoreUsers").on('click', function(){
		var selectedName=$("#selectedUserList option:selected").text();
		var selectedId=$("#selectedUserList option:selected").val();
		if(selectedId.length > 0){
			$('.templateUserError').attr('hidden','');
			var isPresentId=userId2.indexOf(selectedId.toString());
			var isPresentName=userList2.indexOf(selectedName.toString());
			if(isPresentId == -1){
				userList2.push(selectedName.toString());
				userId2.push(selectedId.toString().trim());
				var markup = "<tr><td><a class='deleteUserForTemplate' href='javascript:void(0);' id="+selectedId.toString()+" data-value="+selectedId.toString()+" title=<spring:message code='application.remove'/> > <img src=${pageContext.request.contextPath}/resources/images/delete1.png /> </a></td><td>"+selectedName.toString()+"</td></tr>"
				//index++;
				$('#tableList1').append(markup);
				$('#userId').val(userId2);
				$('#selectedUserList').find('option[value="' + selectedId + '"]').remove();
				$('#selectedUserList').trigger("chosen:updated");
			}
		}else {
			$('.templateUserError').removeAttr('hidden');
		}
	});

		<c:forEach items="${assignedUserList}" var="user">
			$('#tableList1').append("<tr><td><a class='deleteUserForTemplate' href='javascript:void(0);' data-value='${user.id}' title=<spring:message code='application.delete'/> > <img src=${pageContext.request.contextPath}/resources/images/delete1.png /> </a></td><td>"+'${user.name}'+"</td></tr>");
			var isAvailableUser=userId2.indexOf('${user.id}');
			var isAvailableUserName=userList2.indexOf('${user.name}');
			if(isAvailableUser==-1){
				userId2.push('${user.id}');
				userList2.push('${user.name}');
			}
			$('#userId').val('');
			$('#userId').val(userId2);
		</c:forEach>
		
	$(document).delegate('.deleteUserForTemplate', 'click', function(e) {
			e.preventDefault();
			text2= $(this).parent().next('td').text();
			id2=$(this).data("value");

			$("#removeTemplateUserListPopup").dialog({
				modal : true,
				maxWidth : 400,
				minHeight : 100,
				dialogClass : "",
				show : "fadeIn",
				draggable : true,
				resizable : false,
				dialogClass : "dialogBlockLoaded2"
			});
			$('.ui-widget-overlay').addClass('bg-white opacity-60');
			$('.ui-dialog-title').addClass('title-ellipsis');
			$("#removeTemplateUserListPopup").find('.approverInfoBlock2').find('span:first-child').text("\""+text2+"\"");
			$('.title-ellipsis').text('Remove Template User');
	});
	
	var id2;
	var text2;

	$(document).delegate('#deleteUser', 'click', function(e){
		e.preventDefault();
	    var id=id2;
	    var index = userId2.indexOf(id);
	    if (index !== -1) userId2.splice(index, 1);
	    
	    $('#userId').val(userId2);
	    var text = text2;
	    var index2 = userList2.indexOf(text);
	    if (index2 !== -1) userList2.splice(index2, 1);
	    
	    $('#tableList1 tbody').empty();
	    var i;
	    var html="";
	    for (i = 0; i < userList2.length; i++) {
	    	var userNameFromArray=userList2[i];
	    	var userIdFromArray=userId2[i];
	      html = "<tr><td><a class='deleteUserForTemplate ${canEdit ? '' : 'disabled'}' href='javascript:void(0);' id="+userIdFromArray.toString()+" data-value="+userIdFromArray.toString()+" title=<spring:message code='application.delete'/> > <img src=${pageContext.request.contextPath}/resources/images/delete1.png /> </a></td><td>"+userNameFromArray.toString()+"</td></tr>";
	      $('#tableList1').append(html);
	    }
	    $("#removeTemplateUserListPopup").dialog('close');
		var newRow='<option value="' + id + '" data-name="' + text + '">' + text + '</option>';
		$('#selectedUserList').append(newRow);
		updateUserList('', $("#selectedUserList"), 'NORMAL_USER');
	    $('#selectedUserList').trigger("chosen:updated");
	});
	
	
});
		
</script>
<script>
$(document).ready(function() {
$('#chosenBusinessUnit').on('change', function(){
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
					$('#chosenCostCenter').html(html);
				    $("#chosenCostCenter").trigger("chosen:updated");
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
					$('#chosenCostCenter').append(html);
					$("#chosenCostCenter").trigger("chosen:updated");
				},
				error : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
			});
		</c:if>
		
		<c:if test="${isEnableUnitAndGroupCodeCorr}">
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var url = getContextPath() +  "/buyer/getAssingedGroupCodeList";
				$.ajax({
					type : "GET",
					url : url,
					data : {
						buId : value,
					},
					beforeSend : function(xhr) {
						var html='';
						$('#chosenGroupCode').html(html);
					    $("#chosenGroupCode").trigger("chosen:updated");
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
						html += '<option value="" selected="selected">'+ 'Select Group Code' +'</option>';
						$.each(data, function(i, item) {
							if(item.description.length > 0){
								html += '<option value="' + item.id + '">' + item.groupCode + ' - ' + item.description + '</option>';
							}else{
								html += '<option value="' + item.id + '">' + item.groupCode + '</option>';
							}
						});
						$('#chosenGroupCode').append(html);
						$("#chosenGroupCode").trigger("chosen:updated");
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
});
</script>

<!-- <script>
<c:if test="${assignedCount > 0} ">
$(window).bind('load', function() {
	var allowedFields = '#idRfxTemplateName, #idRfxTemplateDescription, #idStatus, #saveAsPrTemplate, #saveRfxTemplate, #cancelId, #dashboardLink, #listLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>
<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '#cancelId, #dashboardLink, #listLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>
	$.validate({
		lang : 'en'
	});
</script> -->


<script>
 <c:if test="${buyerReadOnlyAdmin or !canEdit}">
	$(window).bind('load', function() {
		var allowedFields = '#cancelId, #dashboardLink, #listLink, #submitStep1PrDetailDraft, #saveRfxTemplate';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
</c:if>
</script> 

<script type="text/javascript" src="<c:url value="/resources/js/view/sourceformCq.js"/>"></script>
