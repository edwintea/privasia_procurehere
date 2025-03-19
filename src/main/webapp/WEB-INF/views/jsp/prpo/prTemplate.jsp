<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="prTemplateDesk" code="application.pr.template" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${prTemplateDesk}] });
});
</script>
<div id="page-content" view-name="rfxTemplate">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
			<li>
				<a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" />
				</a>
			</li>
			<c:url value="/buyer/prTemplateList" var="createUrl" />
			<li>
				<a id="listLink" href="${createUrl} "> <spring:message code="prtemplate.template.list" />
				</a>
			</li>
			<li class="active">
				<c:out value='${btnValue}' />
				<spring:message code="eventawards.pr.template" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="prtemplate.template.administration" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg" style="margin-bottom: 1%;">
			<div class="meeting2-heading">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="eventawards.pr.template" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<c:url var="saveRfxTemplate" value="saveRfxTemplate" />
				<form:form id="frmRfxTemplate" cssClass="form-horizontal bordered-row" method="post" action="savePrTemplate" modelAttribute="prTemplate">
					<input type="hidden" id="templateId" value="${prTemplate.id}">
				   <input type=hidden id="conversionRateValue" value="${prTemplate.templateFieldBinding.conversionRate}">

					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="templateName" cssClass="marg-top-10">
								<spring:message code="rfxTemplate.templateName" />
							</form:label>
							<form:hidden path="buyer.id" />
						</div>
						<form:hidden path="id" />
						<div class="col-md-5">
							<spring:message code="prtemplate.enter.template" var="tempname" />
							<form:input path="templateName" data-validation-length="1-128" data-validation="required length" cssClass="form-control" id="idRfxTemplateName" placeholder="${tempname}" />
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="templateDescription" cssClass="marg-top-10">
								<spring:message code="rfxTemplate.templateDescription" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="prtemplate.template.description" var="tempdesc" />
							<form:textarea path="templateDescription" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" cssClass="form-control" id="idRfxTemplateDescription" placeholder="${tempdesc}" />
							<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="status" cssClass="marg-top-10">
								<spring:message code="application.status" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:select path="status" cssClass="form-control chosen-select disablesearch" id="idStatus" data-validation="required">
								<form:options items="${statusList}" />
							</form:select>
						</div>
					</div>
					<div class="clear"></div>
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="prsummary.general.information" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="pr.name" /></label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-5">
									<spring:message var="placeName" code="pr.place.name" />
									<form:input path="templateFieldBinding.prName" data-validation="length" data-validation-length="0-128" placeholder="${placeName}" class="form-control" />
									<div id="prNameDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<spring:message code="prtemplate.label.read.only" var="read" />
									<form:checkbox path="templateFieldBinding.prNameDisabled" class="custom-checkbox" data-validation-error-msg-container="#prNameDisabledError" data-validation="readonly_data" title="${read}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"> <spring:message code="pr.requester" />
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-5">
									<spring:message var="placeRequester" code="pr.place.requester" />
									<form:textarea path="templateFieldBinding.requester" rows="5" placeholder="${placeRequester}" data-validation="length" data-validation-length="0-500" class="form-control"></form:textarea>
									<div id="requesterDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.requesterDisabled" class="custom-checkbox" data-validation-error-msg-container="#requesterDisabledError" data-validation="readonly_data" title="${read}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10 control-label"> <spring:message code="pr.correspondence.address" />
									</label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-5">
									<div class="input-prepend input-group">
										<label class="physicalCriterion pull-left"> </label> <span class="pull-left buyerAddressRadios <c:if test="${not empty prTemplate.templateFieldBinding.correspondenceAddress}">active</c:if>"> <span class="phisicalArressBlock pull-left marg-top-10"> <c:if test="${not empty buyerAddress}">
													<div class="">
														<h5>${buyerAddress.title}</h5>
														<span class='desc'>${buyerAddress.line1} ${buyerAddress.line2}, ${buyerAddress.city}, ${buyerAddress.zip}, ${buyerAddress.state.stateName} , ${buyerAddress.state.country.countryName} </span>
													</div>
												</c:if>
										</span>
											<div class=" align-right">
												<a id="deletecorpAddress" class="pull-right" style="font-size: 18px; line-height: 1; padding: 0px; color: rgb(127, 127, 127); margin-top: 8px;"> <i class="fa fa-times-circle"></i>
												</a>
											</div>
										</span>
									</div>
									<div id="sub-credit" class="invite-supplier delivery-address margin-top-10" ${not empty prTemplate.templateFieldBinding.correspondenceAddress ? 'style="display: none;"' : ''}>
										<div class="role-upper ">
											<div class="col-sm-12 col-md-12 col-xs-12 float-left">
												<input type="text" placeholder='<spring:message code="event.search.address.placeholder" />' class="form-control delivery_add">
											</div>
										</div>
										<div class="chk_scroll_box">
											<div class="scroll_box_inner overscrollInnerBox">
												<div class="role-main">
													<div class="role-bottom small-radio-btn">
														<ul class="role-bottom-ul">
															<c:forEach var="address" items="${addressList}">
																<li>
																	<div class="radio-info">
																		<label> <form:radiobutton path="templateFieldBinding.correspondenceAddress" value="${address.id}" class="custom-radio" />
																		</label>
																	</div>
																	<div class="del-add">
																		<h5>${address.title}</h5>
																		<span class='desc'>${address.line1} ${address.line2}, ${address.city}, ${address.zip}, ${address.state.stateName}, ${address.country}</span>
																	</div>
																</li>
															</c:forEach>
														</ul>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div id="correspondenceAddressError"></div>
									<!-- 	<div id="address-buyer-dialog"></div> -->
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.correspondenceAddressDisabled" class="custom-checkbox" data-validation-error-msg-container="#correspondenceAddressError" data-validation="readonly_data_address" title="${read}" label="${read}" />
								</div>
							</div>
						</div>
					</div>
					<div class="Invited-Supplier-List import-supplier white-bg" style="margin-bottom: 10px;">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="pr.finance" />
							</h3>
						</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="lockBudget" style="margin-top: 15px;">
								<spring:message code="label.enable.budget.lock" />
							</form:label>
						</div>
						<div class="col-md-1 ${assignedCount > 0 ? 'disabled':''}"  style="margin-top: 15px;">
							<spring:message code="prtemplate.enter.template" var="tempname" />
							<form:checkbox path="lockBudget" id="idLockBudget" cssClass="custom-checkbox marg-top-10" />
						</div>
					</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="pr.base.currency" /></label>
								</div>
								<div class="col-md-3 dd sky  ${assignedCount > 0 ? 'disabled':''}">
									<form:select class="chosen-select" id="chosenCurrency" path="templateFieldBinding.baseCurrency">
										<form:option value="">
											<spring:message code="prtemplate.default.currency" />
										</form:option>
										<form:options items="${currencyList}" itemValue="id" itemLabel="currencyCode" />
									</form:select>
									<div id="baseCurrencyDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.baseCurrencyDisabled" class="custom-checkbox" data-validation-error-msg-container="#baseCurrencyDisabledError" data-validation="readonly_data" title="${read}" label="${read}" />
								</div>
							</div>
							<!-- conversion rate  -->
							<div  class="row marg-bottom-10" hidden>
								<p class="conversionRateAmount" class="conversionRateAmount"></p>
							</div>
						<div class="row marg-bottom-10" id="conversionRateDiv">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="label.conversionRate" /></label>
								</div>
								<div class="col-md-3 dd sky ${assignedCount > 0 ? 'disabled':''} ">
									<c:choose>
										<c:when test="${empty templateFieldBinding.decimal}">
											<c:set var="decimalSet" value="0,0.00"></c:set>
										</c:when>
										<c:when test="${templateFieldBinding.decimal==1}">
											<c:set var="decimalSet" value="0,0.0"></c:set>
										</c:when>
										<c:when test="${templateFieldBinding.decimal==2}">
											<c:set var="decimalSet" value="0,0.00"></c:set>
										</c:when>
										<c:when test="${templateFieldBinding.decimal==3}">
											<c:set var="decimalSet" value="0,0.000"></c:set>
										</c:when>
										<c:when test="${templateFieldBinding.decimal==4}">
											<c:set var="decimalSet" value="0,0.0000"></c:set>
										</c:when>
									</c:choose>
									<c:if test="${not empty templateFieldBinding.decimal}">
										<fmt:formatNumber var="conversionRate" type="number" minFractionDigits="${templateFieldBinding.decimal}" maxFractionDigits="${templateFieldBinding.decimal}" value="${templateFieldBinding.conversionRate}" />
									</c:if>
									<c:if test="${empty templateFieldBinding.decimal}">
										<fmt:formatNumber var="conversionRate" type="number" minFractionDigits="2" maxFractionDigits="2" value="${templateFieldBinding.conversionRate}" />
									</c:if>
									<form:input path="templateFieldBinding.conversionRate" value="${conversionRate}" id="conversionRate" name="conversionRate" data-sanitize="numberFormat" data-validation="validate_custom_length" placeholder="${conversionRate}" class="form-control"  data-validation-length="max13"  data-sanitize-number-format="${decimalSet}"  data-validation-regexp="^[\d,]{1,7}(\.\d{1,${{templateFieldBinding.decimal}})?$" />
									<div id="conversionDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.conversionRateDisabled" data-validation-error-msg-container="#conversionDisabledError" data-validation="readonly_data" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
							</div>


							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="pr.decimal" /></label>
								</div>
								<div class="col-md-3 dd sky  ${assignedCount > 0 ? 'disabled':''}">
									<form:select path="templateFieldBinding.decimal" cssClass="form-control chosen-select" id="decimalChoose">
										<form:option value="">
											<spring:message code="pr.select.decimal" />
										</form:option>
										<form:option value="1"></form:option>
										<form:option value="2"></form:option>
										<form:option value="3"></form:option>
										<form:option value="4"></form:option>
									</form:select>
									<div id="decimalDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.decimalDisabled" data-validation-error-msg-container="#decimalDisabledError" data-validation="readonly_data" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="label.costcenter" /></label>
								</div>
								<div class="col-md-3 dd sky ${assignedCount > 0 ? 'disabled':''} ">
									<form:select class="chosen-select" id="chosenCostCenter" path="templateFieldBinding.costCenter">
										<form:option value="">
											<spring:message code="pr.select.cost.center" />
										</form:option>
										<c:forEach items="${costCenterList}" var="costCenter">
											<form:option value="${costCenter.id}">${costCenter.costCenter} - ${costCenter.description}</form:option>
										</c:forEach>
									</form:select>
									<div id="costCenterDisabledError"></div>
								</div>
								<div class="check-wrapper first">
									<spring:message code="prtemplate.label.visible" var="visible" />
									<form:checkbox path="templateFieldBinding.costCenterVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.costCenterDisabled" data-validation-error-msg-container="#costCenterDisabledError" data-validation="readonly_data" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
								<div class="check-wrapper">
									<spring:message code="prtemplate.label.optional" var="optional" />
									<form:checkbox path="templateFieldBinding.costCenterOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="label.businessUnit" /></label>
								</div>
								<div class="col-md-3 dd sky  ${assignedCount > 0 ? 'disabled':''}">
									<form:select class="chosen-select" id="chosenBusinessUnit" path="templateFieldBinding.businessUnit">
										<form:option value="">
											<spring:message code="pr.select.business.unit" />
										</form:option>
										<form:options items="${businessUnitList}" itemValue="id" itemLabel="unitName" />
									</form:select>
									<div id="businessUnitDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.businessUnitDisabled" data-validation-error-msg-container="#businessUnitDisabledError"  class="custom-checkbox" title="${read}" label="${read}" />
								</div>
							</div>

							<!--removing field mentioned in budget planner   -->
							
								<div class="row marg-bottom-10" id="availabelBudgetDiv">
									<div class="col-md-3">
										<label class="marg-top-10"><spring:message code="label.availableBudget" /></label>
									</div>
									<div class="col-md-3 dd sky ${assignedCount > 0 ? 'disabled':''} ">
	
										<c:choose>
											<c:when test="${empty templateFieldBinding.decimal}">
												<c:set var="decimalSet" value="0,0.00"></c:set>
											</c:when>
											<c:when test="${templateFieldBinding.decimal==1}">
												<c:set var="decimalSet" value="0,0.0"></c:set>
											</c:when>
											<c:when test="${templateFieldBinding.decimal==2}">
												<c:set var="decimalSet" value="0,0.00"></c:set>
											</c:when>
											<c:when test="${templateFieldBinding.decimal==3}">
												<c:set var="decimalSet" value="0,0.000"></c:set>
											</c:when>
											<c:when test="${templateFieldBinding.decimal==4}">
												<c:set var="decimalSet" value="0,0.0000"></c:set>
											</c:when>
										</c:choose>
										<fmt:formatNumber var="availableBudget" type="number" minFractionDigits="${templateFieldBinding.decimal}" maxFractionDigits="${templateFieldBinding.decimal}" value="${templateFieldBinding.availableBudget}" />
										<form:input path="templateFieldBinding.availableBudget" class="form-control" placeholder="${availableBudget}" data-validation="validate_custom_length" data-sanitize="numberFormat" data-validation-length="max13" data-sanitize-number-format="${decimalSet}" data-validation-regexp="^[\d,]{1,7}(\.\d{1,4})?$" />
										<div id="availableBudgetDisabledError"></div>
									</div>
									<div class="check-wrapper first">
										<form:checkbox path="templateFieldBinding.availableBudgetVisible" class="custom-checkbox" title="${visible}" label="${visible}" />
									</div>
									<div class="check-wrapper">
										<form:checkbox path="templateFieldBinding.availableBudgetDisabled" data-validation-error-msg-container="#availableBudgetDisabledError" data-validation="readonly_data" class="custom-checkbox" title="${read}" label="${read}" />
									</div>
									<div class="check-wrapper">
										<form:checkbox path="templateFieldBinding.availableBudgetOptional" class="custom-checkbox" title="${optional}" label="${optional}" />
									</div>
								</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="pr.payment.term" /></label>
								</div>
								<div class="col-md-3 dd sky  ${assignedCount > 0 ? 'disabled':''}">
									<form:select class="chosen-select" id="chosenPaymentTerms" path="templateFieldBinding.paymentTerms">
										<form:option value="">
											<spring:message code="pr.select.payment.terms" />
										</form:option>
										<c:forEach items="${paymentTermsList}" var="paymentTerm">
											<c:set var="paymentTermOption" value="${paymentTerm.paymentTermCode} - ${paymentTerm.description}" />
											<c:choose>
												<c:when test="${selectedPaymentTerms == paymentTermOption}">
													<form:option value="${paymentTerm.id}" selected="true">${paymentTermOption}</form:option>
												</c:when>
												<c:otherwise>
													<form:option value="${paymentTerm.id}">${paymentTermOption}</form:option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</form:select>
									<div id="paymentTermsDisabledError"></div>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.paymentTermsDisabled" data-validation-error-msg-container="#paymentTermsDisabledError" data-validation="readonly_data" class="custom-checkbox" title="${read}" label="${read}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="prtemplate.terms.condition" /> </label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-5">
									<spring:message code="prtemplate.terms.conditions" var="terms" />
									<form:textarea path="templateFieldBinding.termAndCondition" rows="5" placeholder="${terms}" data-validation="length" data-validation-length="0-850" class="form-control"></form:textarea>
									<div id="termAndConditionError"></div>
									<span class="sky-blue"><spring:message code="terms.max.characters.only" /></span>
								</div>
								<div class="check-wrapper">
									<form:checkbox path="templateFieldBinding.termAndConditionDisabled" data-validation-error-msg-container="#termAndConditionError" class="custom-checkbox" data-validation="readonly_data" title="${read}" label="${read}" />
								</div>
							</div>
						</div>
					</div>


                    <div style="margin-bottom: 10px;">
                        <div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
                            <jsp:include page="prTemplateApprovals.jsp" />
                        </div>
                    </div>

					<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
						<jsp:include page="poTemplateApprovals.jsp" />
					</div>

					<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
						<jsp:include page="grTemplateApprovals.jsp" />
					</div>

					<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
						<jsp:include page="invoiceTemplateApprovals.jsp" />
					</div>

					<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
						<jsp:include page="/WEB-INF/views/jsp/prpo/prTemplateTeamMembers.jsp"></jsp:include>
					</div>

					<div class="Invited-Supplier-List import-supplier white-bg">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="prtemplate.supplier.setting.label" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">

							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="prtemplate.radio.open.supplier" /></label>
								</div>
								<div class="col-md-3 dd sky marg-top-10 ${assignedCount > 0 ? 'disabled':''}" id="hideOpen">
									<spring:message code="prtemplate.hide.open.supplier" var="hideopen" />
									<form:checkbox path="templateFieldBinding.hideOpenSupplier" class="custom-checkbox" id="hideOpenSupplier" title="${hideopen}" label="${hideopen}" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="prtemplate.radio.contract.items" /></label>
								</div>
								<div class="col-md-3 dd sky  ${assignedCount > 0 ? 'disabled':''}">
									<spring:message code="prtemplate.hide.contract.contract.only" var="contractItem" />
									<form:checkbox path="contractItemsOnly" class="custom-checkbox" id="contractItemsOnly" title="${contractItem}" label="${contractItem}" />
								</div>
							</div>
						</div>
					</div>


					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20 mt-15">
						<div class="meeting2-heading">
							<h3>
								<spring:message code="template.user" />
							</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row">
								<div class="col-sm-4 col-md-3">
									<label class="marg-top-10"><spring:message code="assign.template.user" /></label>
								</div>
								<div class="col-md-4 col-sm-6">
									<div class="input-group search_box_gray disp-flex">
										<select class="chosen-select user-list-normal-pr-template" id="selectedUserList" selected-id="data-value">
											<option value=""><spring:message code="placeholder.search.user.templates" /></option>
											<c:forEach items="${assignedUserListDropDown}" var="userList">
												<c:if test="${userList.id == '-1'}">
													<option value="-1" disabled>${userList.name}</option>
												</c:if>
												<c:if test="${userList.id != '-1' }">
													<option value="${userList.id}">${userList.name}</option>
												</c:if>
											</c:forEach>
											<%-- 												<option value="${userList.id}">${userList.name}</option>   --%>
										</select> <span class="col-md-2 col-sm-2">
											<button class="btn btn-info hvr-pop hvr-rectangle-out" type="button" data-toggle="tooltip" data-placement="top" id="addMoreUsers">
												<i class="fa fa-plus" aria-hidden="true"></i>
											</button>
										</span>
									</div>
									<div class="error templateUserError" hidden>
										<spring:message code="please.select.user" />
									</div>
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
			</div>

			<div class="row marg-bottom-202">
				<!-- <div class="col-md-3">
							<label class="marg-top-10"></label>
						</div> -->
				<div class="col-md-9 btns-lower marg-top-20">
					<sec:authorize access="hasRole('PR_TEMPLATE_EDIT') or hasRole('ADMIN')" var="canEdit" />
					<form:button type="submit" id="saveRfxTemplate" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" disabled="${!canEdit}">${btnValue}</form:button>
					<c:url value="/buyer/prTemplateList" var="createUrl" />
					<a href="${createUrl}" id="cancelId" class="btn btn-black hvr-pop marg-left-10 hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
					</a>
					<c:if test="${prTemplate.id ne null}">
						<form:button type="button" id="saveAsPrTemplate" class="btn btn-alt btn-hover ph_btn_midium btn-warning hvr-pop pull-right ${canEdit ? '':'disabled'}">
							<spring:message code="application.saveas" />
						</form:button>
					</c:if>
				</div>
			</div>
			<input type="hidden" name="userId" id="userId" />
			</form:form>
		</div>
	</div>
</div>
</div>
<!-- TEMPLATE SAVE AS POPUP -->
<div class="flagvisibility dialogBox" id="prTemplateSaveAsPopup" title="Template Save As">
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="tempId" name="tempId" />
		<form>
			<div class="marg-top-20 tempaleData">
				<div class="row marg-bottom-10">
					<div class=" col-md-4">
						<label> <spring:message code="rfxTemplate.templateName" />
						</label>
					</div>
					<div class="col-md-8">
						<spring:message code="rfxTemplate.templateDescription.placeHolder" var="desc" />
						<spring:message code="rfxTemplate.templateName.placeHolder" var="name" />
						<input data-validation-length="1-128" data-validation="required length" class="form-control marg-bottom-10" name="tempName" id="tempName" placeholder="Enter Template Name...." /> <span class="customError"></span>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-md-4">
						<label> <spring:message code="rfxTemplate.templateDescription" />
						</label>
					</div>
					<div class="col-md-8">
						<textarea name="tempDescription" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" id="tempDescription" placeholder="Enter Template Description...."></textarea>
					</div>
				</div>
			</div>
		</form>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_midium btn-tooltip hvr-pop hvr-rectangle-out" id="saveAsTemp" data-original-title="Delete"><spring:message code="application.create" /></a>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_midium hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
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
.radio>span {
	padding: 0 !important;
}

.check-wrapper {
	width: 115px;
}

.mt-15 {
	margin-top: 20px;
}
#uniform-lockBudget1 span{
margin-top: 10px !important;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>

var userList2=[];
var userId2=[];
$(document).ready(function() {
	if($("#idLockBudget").prop("checked") == true){
		$('#availabelBudgetDiv').hide();
	}else{
		$('#availabelBudgetDiv').show();
		}
	var conversionRateValue=$("#conversionRateValue").val();
	$("#conversionRateAmount").text(conversionRateValue);
	if(conversionRateValue=='' ){
		$('#conversionRateDiv').attr('hidden', true);
	}else{
		var decimalVal=parseInt($("#decimalChoose").val());
		var conversionRate=Math.abs(Number($(".conversionRateAmount").val().replace(/,/g, "")));
		conversionRate =parseFloat(conversionRate);
		if(conversionRate!=''){
			conversionRate=ReplaceNumberWithCommas(conversionRate.toFixed(decimalVal));
			$("#conversionRate").val(conversionRate);
		}
	}
	var defaultCurrency = $('#chosenCurrency').val();
	$('#chosenCurrency').change(function() {
		var newCurrency = $('#chosenCurrency').val();
		if (newCurrency != defaultCurrency) {
			$('#conversionRateDiv').removeAttr('hidden');
		} else {
			$('#conversionRateDiv').attr('hidden', true);
		}
	});
	
	
	
	$('#decimalChoose').change(function(){
		var decimalVal=parseInt($(this).val());
		var conversionRateValue=$("#conversionRate").val();
		$(".conversionRateAmount").text(conversionRateValue);
		var conversionRate=Math.abs(Number($(".conversionRateAmount").html().replace(/,/g, "")));
		conversionRate =parseFloat(conversionRate);
		if(conversionRate!=''){
			conversionRate=ReplaceNumberWithCommas(conversionRate.toFixed(decimalVal));
			$("#conversionRate").val(conversionRate);
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

	$('#idLockBudget').click(function() {
		if($("#idLockBudget").prop("checked") == true){
			$('#availabelBudgetDiv').hide();
		}else{
			$('#availabelBudgetDiv').show();
			}
	});
	
	$("#contractItemsOnly").click(function() {

		if ($("#contractItemsOnly").prop("checked") == true) {
			$("#hideOpenSupplier").prop("checked", true);
			$("#hideOpenSupplier").addClass("readOnlyClass", true);
			$("#hideOpen").addClass("readOnlyClass", true);
		} else {
			$("#hideOpenSupplier").removeClass("readOnlyClass", true);
			$("#hideOpen").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

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
      html = "<tr><td><a class='deleteUserForTemplate' href='javascript:void(0);' id="+userIdFromArray.toString()+" data-value="+userIdFromArray.toString()+" title=<spring:message code='application.delete'/> > <img src=${pageContext.request.contextPath}/resources/images/delete1.png /> </a></td><td>"+userNameFromArray.toString()+"</td></tr>";
      $('#tableList1').append(html);
    }
    $("#removeTemplateUserListPopup").dialog('close');
	var newRow='<option value="' + id + '" data-name="' + text + '">' + text + '</option>';
	$('#selectedUserList').append(newRow);
	updateUserList('', $("#selectedUserList"), 'NORMAL_USER');
    $('#selectedUserList').trigger("chosen:updated");
});

//remove approvers list
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


<c:if test="${assignedCount > 0}">

//Remove click event handler on checkbox labels. It affects disabled checkboxes
$('.check-wrapper').on('click', function(e){
	e.preventDefault();
	return false;
});

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
		lang : 'en',
		modules : 'date,sanitize'
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
		}
	})
});
</script>
<style>
.readOnlyClass {
	pointer-events: none; !:;
	important:;
}

.dialogBlockLoaded2 {
	border: 1px solid rgba(0, 0, 0, .2)!;
	-webkit-box-shadow: 0 5px 15px rgba(0, 0, 0, .5) !important;
	box-shadow: 0 5px 15px rgba(0, 0, 0, .5) !important;
}

.error {
	color: #ff5757 !important;
}

.disp-flex {
	display: flex;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/view/prTemplate.js"/>"></script>
