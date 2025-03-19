<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="rfxCreateDetails" code="application.rfx.create.details" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${sourcingRequestCreateDetails}] });
});
</script>
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
					</a></li>
					<li class="active"><spring:message code="rfs.sourcing.form.request" /></li>
				</ol>
				<section class="create_list_sectoin">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap tender-request-heading">
							<spring:message code="rfs.create.sourcing.form" />
						</h2>
						<h2 class="trans-cap pull-right">
							<spring:message code="application.status" />
							: ${sourcingFormRequest.status}
						</h2>

					</div>
					<jsp:include page="sourcingFormHeader.jsp"></jsp:include>
					<%-- <jsp:include page="/WEB-INF/views/jsp/rfx/eventDetailsTour.jsp"></jsp:include> --%>
					<div class="tab-pane active error-gap-div">
						<form:form class="bordered-row" id="demo-form1" method="post" modelAttribute="sourcingFormRequest" action="${pageContext.request.contextPath}/buyer/saveSourcingFormDetails">
							<input type="hidden" name="approvalsCount" value="${sourcingFormRequest.approvalsCount}" id="approvalsCount" />
							<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
							<div class="clear"></div>
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="upload_download_wrapper clearfix event_info">
								<h4>
									<spring:message code="formdetails.form.information.label" />
								</h4>
								<div class="row">
									<div class="form-tander1 pad_left_right_15">
										<div class="col-sm-4 col-md-3 col-xs-6">
											<label> <spring:message code="formdetails.form.id" />
											</label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<p>${sourcingFormRequest.formId}</p>
										</div>
									</div>
									<div class="form-tander1 pad_left_right_15">
										<div class="col-sm-4 col-md-3 col-xs-6">
											<label> <spring:message code="formdetails.form.type" />
											</label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
											<p>
												<spring:message code="rfs.sourcing.form" />
											</p>
										</div>
									</div>
									<div class="form-tander1 line-set pad_left_right_15">
										<div class="col-sm-4 col-md-3 col-xs-6">
											<label> <spring:message code="formdetails.form.owner" />
											</label>
										</div>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<p>${sourcingFormRequest.formOwner.name }
												<br> ${sourcingFormRequest.formOwner.communicationEmail} <br>
												<spring:message code="formdetails.form.telephone" />${sourcingFormRequest.formOwner.phoneNumber}
											</p>
										</div>
									</div>
								</div>
							</div>
							<div class="upload_download_wrapper clearfix marg-top-10 event_info event_form">
								<h4>
									<spring:message code="formdetails.form.details" />
								</h4>
								<div class="form_field">
									<div>
										<label class="col-sm-4 col-md-3 col-xs-6 control-label"> </label>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<form:checkbox path="urgentForm" cssClass="custom-checkbox" />
											<label><spring:message code="rfs.urgent.request.checkbox" /></label>
										</div>
									</div>
								</div>
								<div class="form_field">
									<div>
										<form:hidden path="id" id="id" />
										<form:hidden path="tenantId" />
										<form:hidden path="formId" />
										<form:hidden path="formOwner.name" />
										<form:hidden path="formOwner.communicationEmail" />
										<form:hidden path="formOwner.phoneNumber" />
										<form:hidden path="formDetailCompleted" />
										<form:hidden path="cqCompleted" />
										<form:hidden path="bqCompleted" />
										<form:hidden path="summaryCompleted" />
										<form:hidden path="status" value="${sourcingFormRequest.status}" />
										<input type="hidden" value="${reqId }" name="reqId"> <label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="formdetails.form.referencenumber" />
										</label>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<spring:message code="formdetails.form.referencenumber" var="reference" />
											<form:input path="referanceNumber" type="text" placeholder="${reference}" class="form-control" data-validation="required alphanumeric length" data-validation-allowing="/ -_" data-validation-length="max64" />
										</div>
									</div>
								</div>
								<form:input path="sourcingFormName" type="hidden"></form:input>
								<div class="form_field">
									<div>
										<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="formdetails.form.name" />
										</label>
										<div class="col-sm-5 col-md-5 col-xs-6">
											<form:input path="sourcingFormName" class="form-control" readonly="true" disabled="true" />
										</div>
									</div>
								</div>




							</div>
							<div class="upload_download_wrapper clearfix marg-top-10 event_info event_form event_info">
								<h4>
									<spring:message code="formdetails.form.description.label" />
								</h4>
								<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle">
									<div class="col-md-8">
										<p>
											<spring:message code="formdetails.form.description.about" />
										</p>
									</div>
									<div class="form_field" style="margin-top: 1%;">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="formdetails.form.description" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<form:textarea path="description" class="form-control" data-validation="length" data-validation-length="max500" />
												<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
											</div>
										</div>
									</div>
								</div>
							</div>
							<c:if test="${!empty templateFields ? (tf:rfsvisibility( templateFields, 'PROCUREMENT_METHOD' ) || tf:rfsvisibility( templateFields, 'PROCUREMENT_CATEGORY' )) : 'false' }">
								<div class="upload_download_wrapper marg-top-10 event_info white-bg">
									<h4>
										<spring:message code="sourcing.procurement.info" />
									</h4>
									<div class="event_form">
										<c:if test="${!empty templateFields ? (tf:rfsvisibility( templateFields, 'PROCUREMENT_METHOD' )) : 'true' }">
											<div class="form_field">
												<div class="form-group">
													<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="procurement.info.method" />
													</label>
													<div class="col-sm-5 col-md-5 col-xs-6">
														<div class="form-group ${!empty templateFields ? (tf:rfsreadonly( templateFields, 'PROCUREMENT_METHOD' ) ? 'disabled' : '') : ''}">
															<form:select path="procurementMethod" cssClass="form-control chosen-select" class="custom-select" data-validation="${!empty templateFields ? (tf:rfsrequired( templateFields, 'PROCUREMENT_METHOD' ) ? 'required' : '') : ''}">
																<form:option value="">
																	<spring:message code="procurement.info.method.select" />
																</form:option>
																<form:options items="${procurementMethodList}" itemValue="id" itemLabel="procurementMethod" />
															</form:select>
														</div>
													</div>
												</div>
											</div>
										</c:if>
										<c:if test="${!empty templateFields ? (tf:rfsvisibility( templateFields, 'PROCUREMENT_CATEGORY' )) : 'true' }">
											<div class="form_field">
												<div class="form-group">
													<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="procurement.info.category" />
													</label>
													<div class="col-sm-5 col-md-5 col-xs-6">
														<div class="form-group ${!empty templateFields ? (tf:rfsreadonly( templateFields, 'PROCUREMENT_CATEGORY' ) ? 'disabled' : '') : ''}">
															<form:select path="procurementCategories" cssClass="form-control chosen-select" class="custom-select" data-validation="${!empty templateFields ? (tf:rfsrequired( templateFields, 'PROCUREMENT_CATEGORY' ) ? 'required' : '') : ''}">
																<form:option value="">
																	<spring:message code="procurement.info.category.select" />
																</form:option>
																<form:options items="${procurementCategoryList}" itemValue="id" itemLabel="procurementCategories" />
															</form:select>
														</div>
													</div>
												</div>
											</div>
										</c:if>
									</div>
								</div>
							</c:if>
							<div class="upload_download_wrapper marg-top-10 event_info white-bg">
								<h4>
									<spring:message code="rfs.finance" />
								</h4>
								<div class="event_form">

									<c:if test="${!empty templateFields ? (tf:rfsvisibility( templateFields, 'BASE_CURRENCY' )) : 'true' }">
										<div class="form_field">
											<div>
												<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="label.base.currency" />
												</label>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<div class="form-group ${!empty templateFields ? (tf:rfsreadonly( templateFields, 'BASE_CURRENCY' ) ? 'disabled' : '') : ''}">
														<form:select path="currency" cssClass="form-control chosen-select" class="custom-select" data-validation="required">
															<form:option value="">
																<spring:message code="select.base.currency" />
															</form:option>
															<form:options items="${baseCurrencyList}" itemValue="id" itemLabel="currencyCode" />
														</form:select>
													</div>
												</div>
											</div>
										</div>
									</c:if>
									<div class="form_field">
										<div>
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="eventdescription.decimal.label" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6 disabled">
												<form:select path="decimal" cssClass="form-control chosen-select  disablesearch decimalChange">
													<form:option value="1">1</form:option>
													<form:option value="2">2</form:option>
													<form:option value="3">3</form:option>
													<form:option value="4">4</form:option>
												</form:select>


											</div>
										</div>
									</div>

									<c:if test="${!empty templateFields ? (tf:rfsvisibility( templateFields, 'BUDGET_AMOUNT' )) : 'true' }">
										<div class="form_field">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="rfs.availableBudget.label" />
												</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<fmt:formatNumber var="budgetAmount" type="number" minFractionDigits="${sourcingFormRequest.decimal}" maxFractionDigits="${sourcingFormRequest.decimal}" value="${sourcingFormRequest.budgetAmount}" />
												<form:input path="budgetAmount" autocomplete="off" type="text" value="${budgetAmount}" class="form-control autoSave" placeholder="e.g. RM 50" readonly="${!empty templateFields ? (tf:rfsreadonly( templateFields, 'BUDGET_AMOUNT' )) : 'false'}"
													data-validation="${!empty templateFields ? (tf:rfsrequired( templateFields, 'BUDGET_AMOUNT' ) ? 'required' : '') : ''} validate_custom_length positive" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${sourcingFormRequest.decimal}})?$" />
											</div>
										</div>
									</c:if>
									<c:if test="${!empty templateFields ? (tf:rfsvisibility( templateFields, 'ESTIMATED_BUDGET' )) : 'true' }">
										<div class="form_field">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="rfs.estimatedBudget.label" />
												</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<fmt:formatNumber var="estimatedBudget" type="number" minFractionDigits="${sourcingFormRequest.decimal}" maxFractionDigits="${sourcingFormRequest.decimal}" value="${sourcingFormRequest.estimatedBudget}" />
												<form:input path="estimatedBudget" autocomplete="off" type="text" value="${estimatedBudget}" class="form-control autoSave" placeholder="e.g. RM 50" data-validation="${!empty templateFields ? (tf:rfsrequired( templateFields, 'ESTIMATED_BUDGET' ) ? 'required' : '') : ''} validate_custom_length positive"
													readonly="${!empty templateFields ? (tf:rfsreadonly( templateFields, 'ESTIMATED_BUDGET' )) : 'false'}" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${sourcingFormRequest.decimal}})?$" />
											</div>
										</div>
									</c:if>

									<c:if test="${!empty templateFields ? (tf:rfsvisibility( templateFields, 'HISTORIC_AMOUNT' )) : 'true' }">
										<div class="form_field">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label> <spring:message code="rfs.historicAmount.label" />
												</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6">
												<fmt:formatNumber var="historicaAmount" type="number" minFractionDigits="${sourcingFormRequest.decimal}" maxFractionDigits="${sourcingFormRequest.decimal}" value="${sourcingFormRequest.historicaAmount}" />
												<form:input path="historicaAmount" autocomplete="off" type="text" value="${historicaAmount}" class="form-control autoSave" placeholder="e.g. RM 50" data-validation="${!empty templateFields ? (tf:rfsrequired( templateFields, 'HISTORIC_AMOUNT' ) ? 'required' : '') : ''} validate_custom_length positive"
													readonly="${!empty templateFields ? (tf:rfsreadonly( templateFields, 'HISTORIC_AMOUNT' )) : 'false'}" data-validation-regexp="^[\d,]{1,10}(\.\d{1,${sourcingFormRequest.decimal}})?$" />
											</div>
										</div>
									</c:if>
									<c:if test="${(!empty templateFields ? (tf:rfsvisibility( templateFields, 'MINIMUM_SUPPLIER_RATING' )) : 'true' )}">
										<div class="form_field">
											<div>
												<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="sourcing.minimumSupplierRating" />
												</label>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<div class="input-prepend input-group ${ (!empty templateFields ? (tf:rfsreadonly( templateFields, 'MINIMUM_SUPPLIER_RATING' ) ? 'disabled': '') : '')}">
														<form:input path="minimumSupplierRating" autocomplete="off" id="minimumSupplierRating" placeholder="Minimum Supplier Rating/Grade" class="form-control" type="text" data-validation-optional="true"
															data-validation="${!empty templateFields ? (tf:rfsrequired( templateFields, 'MINIMUM_SUPPLIER_RATING' ) ? 'required' : '') : ''} custom number validateMin" data-validation-allowing="range[0.00;9999.99],float" data-validation-regexp="^\d+\.?\d{0,2}$"
															data-validation-error-msg-custom="Input value must be numeric within range from 0 to 9999.99" data-validation-error-msg-number="Input value must be numeric within range from 0 to 9999.99" />
													</div>
												</div>
											</div>
										</div>
									</c:if>


									<c:if test="${(!empty templateFields ? (tf:rfsvisibility( templateFields, 'MAXIMUM_SUPPLIER_RATING' )) : 'true' )}">
										<div class="form_field">
											<div>
												<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="sourcing.maximumSupplierRating" />
												</label>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<div class="input-prepend input-group ${ (!empty templateFields ? (tf:rfsreadonly( templateFields, 'MAXIMUM_SUPPLIER_RATING' ) ? 'disabled': '') : '')}">
														<form:input path="maximumSupplierRating" autocomplete="off" id="maximumSupplierRating" placeholder="Maximum Supplier Rating/Grade" class="error form-control" type="text" data-validation-optional="true"
															data-validation="${!empty templateFields ? (tf:rfsrequired( templateFields, 'MAXIMUM_SUPPLIER_RATING' ) ? 'required' : '') : ''} custom number validateMax" data-validation-allowing="range[1.00;9999.99],float" data-validation-regexp="^\d+\.?\d{0,2}$"
															data-validation-error-msg-custom="Input value must be numeric within range from 1 to 9999.99" data-validation-error-msg-number="Input value must be numeric within range from 1 to 9999.99" />
													</div>
												</div>
											</div>
										</div>
									</c:if>
									<div class="form_field">
										<div class="form-group">
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="label.businessUnit" />
											</label>
											<div class="col-sm-5 col-md-5 col-xs-6 ${idSettings.idSettingType == 'BUSINESS_UNIT' ? 'disabled' : ''}">
												<div class="form-group ${!empty templateFields ? (tf:rfsreadonly( templateFields, 'BUSINESS_UNIT' ) ? 'disabled' : '') : ''}">
													<form:select path="businessUnit" id="businessUnit" cssClass="form-control chosen-select autoSave" class="custom-select" data-validation="${!empty templateFields ? (tf:rfsrequired( templateFields, 'BUSINESS_UNIT' ) ? 'required' : '') : ''}">
														<form:option value="">
															<spring:message code="rfs.select.business.unit" />
														</form:option>
														<form:options items="${businessUnitList}" itemValue="id" itemLabel="unitName" />
													</form:select>
												</div>
											</div>
										</div>
									</div>

									<c:if test="${!empty templateFields ? (tf:rfsvisibility( templateFields, 'COST_CENTER' )) : 'true' }">
										<div class="form_field">
											<div class="form-group">
												<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="label.costcenter" />
												</label>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<div class="form-group ${!empty templateFields ? (tf:rfsreadonly( templateFields, 'COST_CENTER' ) ? 'disabled' : '') : ''}">
														<form:select path="costCenter" id="costCenterId" cssClass="form-control chosen-select" class="custom-select" data-validation="${!empty templateFields ? (tf:rfsrequired( templateFields, 'COST_CENTER' ) ? 'required' : '') : ''}">
															<form:option value="">
																<spring:message code="rfs.select.cost.center" />
															</form:option>
															<c:forEach items="${costCenterList}" var="costCenter">
																<form:option value="${costCenter.id}">${costCenter.costCenter} - ${costCenter.description}</form:option>
															</c:forEach>
														</form:select>
													</div>
												</div>
											</div>
										</div>
									</c:if>
									<c:if test="${!empty templateFields ? (tf:rfsvisibility( templateFields, 'GROUP_CODE' )) : 'true' }">
<%-- 										<c:if test="${sourcingFormRequest.groupCodeOld == null }"> --%>
											<div class="form_field">
												<div class="form-group">
													<label class="col-sm-4 col-md-3 col-xs-6 control-label"> <spring:message code="label.groupCode" />
													</label>
													<div class="col-sm-5 col-md-5 col-xs-6">
														<div class="form-group ${!empty templateFields ? (tf:rfsreadonly( templateFields, 'GROUP_CODE' ) ? 'disabled' : '') : ''}">
															<form:select path="groupCode" id="idGroupCode" cssClass="form-control chosen-select" class="custom-select" data-validation="${!empty templateFields ? (tf:rfsrequired( templateFields, 'GROUP_CODE' ) ? 'required' : '') : ''}">
																<form:option value="">
																	<spring:message code="rfs.select.group.Code" />
																</form:option>
																<c:forEach items="${groupCodeList}" var="gpC">
																	<form:option value="${gpC.id}">${gpC.groupCode} - ${gpC.description}</form:option>
																</c:forEach>
															</form:select>
														</div>
													</div>
												</div>
											</div>
<%-- 										</c:if> --%>
<%-- 										<c:if test="${sourcingFormRequest.groupCodeOld != null}"> --%>
<!-- 											<div class="form_field"> -->
<%-- 												<label class="col-sm-4 col-md-3 col-xs-6"> <spring:message code="rfs.Group.code" /> --%>
<!-- 												</label> -->
<!-- 												<div class="col-sm-5 col-md-5 col-xs-6"> -->
<%-- 													<spring:message code="rfs.enter.Group.code" var="glcode" /> --%>
<%-- 													<form:input path="groupCodeOld" autocomplete="off" cssClass="form-control" id="idGlCode" placeholder="${glcode}" data-validation="length" data-validation-optional="true" data-validation-length="1-20" /> --%>
<%-- 													<form:errors path="groupCodeOld" cssClass="error" /> --%>
<!-- 												</div> -->
<!-- 											</div> -->
<%-- 										</c:if> --%>
									</c:if>
									
								</div>
							</div>
							<div class="upload_download_wrapper clearfix marg-top-10 event_info w-42rm">
								<h4>
									<spring:message code="sourcing.event.team.members" />
								</h4>
								<jsp:include page="/WEB-INF/views/jsp/sourcingFormRequest/sourcingFormRequestTeamMembers.jsp"></jsp:include>
							</div>
							<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab  in">
								<%-- <c:if test="${not empty sourcingFormRequest.approvalsCount}"> --%>
								<jsp:include page="/WEB-INF/views/jsp/sourcingFormRequest/sourcingFormRequestApproval.jsp"></jsp:include>
								<%-- 	</c:if> --%>
							</div>
							<div class="btn-next">
								<input type="button" id="submitStep1FormDetail" class="top-marginAdminList step_btn_1 btn btn-info ph_btn hvr-pop hvr-rectangle-out submitStep1" value="Next" />
								
								<c:if test="${(isAdmin or eventPermissions.owner)}">
									<a href="#confirmCancelRequest" role="button" class="top-marginAdminList btn btn-danger ph_btn hvr-pop pull-right" data-toggle="modal"><spring:message code="rfs.summary.cancel.request" /></a>
								</c:if>
							</div>
						</form:form>
					</div>
				</section>
			</div>
		</div>
	</div>
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
<style>
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
</style>
<script type="text/javascript">
	$(function(){
		$('#submitStep1FormDetail').click(function(){
			console.log("save Form details called....................");
			$('#demo-form1').attr('action',getContextPath() + "/buyer/saveSourcingFormDetails" );
			if($('#demo-form1').isValid()) {
				$('#demo-form1').submit();
			}
		});
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<style>
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

input[readonly].for-clander-view {
	cursor: default !important;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<!-- daterange picker js and css start -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css?1"/>">
<!-- EQul height js-->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
<!-- Theme layout -->

<script type="text/javascript">

$.validate({
	lang : 'en',
	modules : 'sanitize'
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

  
$('#maximumSupplierRating').on('keyup', function() {
	$('#maximumSupplierRating').validate(function(valid, elem) {});
	$('#minimumSupplierRating').validate(function(valid, elem) {});
});
  
$('#minimumSupplierRating').on('keyup', function() {
	$('#minimumSupplierRating').validate(function(valid, elem) {});
	$('#maximumSupplierRating').validate(function(valid, elem) {});
});

$(document).ready(
		function() {
		$(document).delegate('input[name="budgetAmount"]', 'change', function(e) {
			var decimalLimit = $('.decimalChange').val();
			var budgetAmount = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
			budgetAmount = !isNaN(budgetAmount) ? budgetAmount.toFixed(decimalLimit) : '';
			console.log(ReplaceNumberWithCommas((budgetAmount)));
			$('#budgetAmount').val(ReplaceNumberWithCommas((budgetAmount)));
		});
		
		$(document).delegate('input[name="historicaAmount"]', 'change', function(e) {
			var decimalLimit = $('.decimalChange').val();
			var historicaAmount = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
			historicaAmount = !isNaN(historicaAmount) ? historicaAmount.toFixed(decimalLimit) : '';
			console.log(ReplaceNumberWithCommas((historicaAmount)));
			$('#historicaAmount').val(ReplaceNumberWithCommas((historicaAmount)));
		});
		
		$(document).delegate('input[name="estimatedBudget"]', 'change', function(e) {
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
		
		});
<c:if test="${eventPermissions.viewer or sourcingFormRequest.status ne 'DRAFT'}">
$(window).bind('load', function() {
	var allowedFields = '#submitStep1FormDetail,.tab-link > a ,#nextButton,#dashboardLink,#previousButton,#bubble, #downloadButton';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('.dropUp').addClass('disabled');
});
</c:if>

$('#businessUnit').on('change', function(){
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
				$('#costCenterId').html(html);
			    $("#costCenterId").trigger("chosen:updated");
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
					console.log("item.description " + item.description + " Length " + item.description.length);
					if(item.description.length > 0){
						html += '<option value="' + item.id + '">' + item.costCenter + ' - ' + item.description + '</option>';
					}else{
						html += '<option value="' + item.id + '">' + item.costCenter + '</option>';
					}
				});
				$('#costCenterId').append(html);
				$("#costCenterId").trigger("chosen:updated");
			},
			error : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
				$('div[id=idGlobalError]').show();
			}
		});
	</c:if>
	
	<c:if test="${isEnableUnitAndGpCodeCorr}">
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
					$('#idGroupCode').html(html);
				    $("#idGroupCode").trigger("chosen:updated");
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
						console.log("item.description " + item.description + " Length " + item.description.length);
						if(item.description.length > 0){
							html += '<option value="' + item.id + '">' + item.groupCode + ' - ' + item.description + '</option>';
						}else{
							html += '<option value="' + item.id + '">' + item.groupCode + '</option>';
						}
					});
					$('#idGroupCode').append(html);
					$("#idGroupCode").trigger("chosen:updated");
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
<script type="text/javascript" src="<c:url value="/resources/js/view/modalValidation.js"/>"></script>
