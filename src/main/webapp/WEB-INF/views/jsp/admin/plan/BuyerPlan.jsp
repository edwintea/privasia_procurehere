<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<sec:authorize access="hasRole('SUBSCRIPTION_DETAILS') or hasRole('ADMIN')" var="canEdit" />
<spring:message var="buyerCreatePlanDesk" code="application.owner.buyer.subscription.plan" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerCreatePlanDesk}] });
});
</script>

<style type="text/css">
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}

.infoSticker {
	background: #eff4f6 none repeat scroll 0 0;
	display: block;
	height: 38px;
	line-height: 38px;
	position: absolute;
	right: 0;
	text-align: center;
	top: 50%;
	z-index: 4;
}
</style>

<div id="page-content" view-name="plan">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li>
				<a href="${pageContext.request.contextPath}/owner/ownerDashboard" id="idDashboard">
					<spring:message code="application.dashboard" />
				</a>
			</li>
			<c:url value="/admin/plan/buyerPlanList" var="createUrl" />
			<li>
				<a href="${createUrl}" id="idPlanList">
					<spring:message code="plan.list" />
				</a>
			</li>
			<li class="active">
				<c:out value='${btnValue}' />
				<spring:message code="plan.administration" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="plan.title" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="plan.title" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<c:url var="savePlan" value="/admin/plan/saveBuyerPlan" />
				<form:form id="frmBuyerPlan" cssClass="form-horizontal bordered-row" method="post" action="${savePlan}" modelAttribute="buyerPlan">
					<form:hidden path="id" />
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="planName" cssClass="marg-top-10">
								<spring:message code="plan.planName" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="plan.planName.placeHolder" var="name" />
							<form:input path="planName" data-validation="required length" data-validation-length="1-128" cssClass="form-control" id="idPlanName" placeholder="${name}" />
						</div>
					</div>
					<%-- 					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="planGroup" cssClass="marg-top-10">
							Plan Group
							</form:label>
						</div>
						<div class="col-md-5">
							<form:input path="planGroup" data-validation="length" data-validation-length="0-128" cssClass="form-control" id="idPlanName" placeholder="${name}" />
						</div>
					</div>
 --%>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="planOrder" cssClass="marg-top-10">
							Plan Order for display
							</form:label>
						</div>
						<div class="col-md-5">
							<form:select path="planOrder" class="chosen-select disablesearch" id="chosenOrder">
								<form:option value="1">1</form:option>
								<form:option value="2">2</form:option>
								<form:option value="3">3</form:option>
								<form:option value="4">4</form:option>
								<form:option value="5">5</form:option>
								<form:option value="6">6</form:option>
								<form:option value="7">7</form:option>
								<form:option value="8">8</form:option>
								<form:option value="9">9</form:option>
								<form:option value="10">10</form:option>
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="shortDescription" cssClass="marg-top-10">
								Short Description
							</form:label>
						</div>
						<div class="col-md-5">
							<form:textarea path="shortDescription" class="form-control textarea-autosize" data-validation="required length" data-validation-length="1-150" cssClass="form-control" id="idPlanDescription" placeholder="Enter Short Description. " />
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="planStatus" cssClass="marg-top-10">
								<spring:message code="application.status" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:select path="planStatus" cssClass="chosen-select disablesearch" id="idStatus" data-validation="required">
								<form:options items="${statusList}" />
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="planStatus" cssClass="marg-top-10">
								Plan Type
							</form:label>
						</div>
						<div class="col-md-5">
							<form:select path="planType" cssClass="chosen-select disablesearch" id="planTypeId" data-validation="required">
								<form:options items="${planTypeList}" />
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<label class="marg-top-10">Currency</label>
						</div>
						<div class="col-md-5">
							<form:select cssClass="chosen-select" id="chosenCurrency" path="currency" data-validation="required">
								<form:options items="${currencyList}" itemValue="id" itemLabel="currencyCode" />
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<label class="marg-top-10">Base Price</label>
						</div>
						<div class="col-md-5">
							<form:hidden path="baseUsers" />
							<fmt:formatNumber groupingUsed = "false" var="BasePrice" type="number" value="${buyerPlan.basePrice}" />
							<form:input path="basePrice" value="${BasePrice}" data-validation="number" data-validation-optional="true" cssClass="form-control" id="idBasePrice" placeholder="Enter Base Price" />
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<label class="marg-top-10">Tax</label>
						</div>
						<div class="col-md-5">
							<%-- <fmt:formatNumber var="tax" type="number" value="${buyerPlan.tax}" /> --%>
							<form:input path="tax" data-validation="custom" data-validation-regexp="^\d{1,16}(\.\d{1,2})?$" data-validation-ignore=","
								data-validation-error-msg="Only numbers and ',' allowed, length should be less then 16 and after decimal 2 digits allowed" cssClass="form-control" id="idPlanTax" placeholder="Enter tax" data-sanitize="numberFormat"
								data-sanitize-number-format="0,0.00" />
						</div>
					</div>
					<div class="clear"></div>
					<div class="Invited-Supplier-List import-supplier white-bg">
						<div class="meeting2-heading">
							<h3>Plan Details</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<!-- <span class="rangeList">
							</span> -->
								<div class="col-md-3">
									<label class="marg-top-10">Range</label>
								</div>
								<div class="col-md-2">
									<%-- <form:input path="" placeholder="e.g. 1" class="form-control" id="rangeStartId" data-validation="number validate_range_start" data-validation-optional="true" /> --%>
									<form:input path="" placeholder="e.g. 1" class="form-control" id="rangeStartId" data-validation="number" data-validation-optional="true" />
								</div>
								<div class="col-md-1">
									<label class="marg-top-10">To</label>
								</div>
								<div class="col-md-2 rangeEndiv">
									<%-- <form:input path="" placeholder="e.g. 5" class="form-control" id="rangeEndId" data-validation="number validate_range_end" data-validation-optional="true" /> --%>
									<form:input path="" placeholder="e.g. 5" class="form-control" id="rangeEndId" data-validation="number" data-validation-optional="true" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10">Price</label>
								</div>
								<div class="col-md-5">
									<form:input path="" placeholder="e.g 199" class="form-control" id="priceId" data-validation="number" data-validation-optional="true" />
								</div>
							</div>

							<div class="row marg-bottom-10">
								<div class="col-md-3"></div>
								<div class="col-md-5 marg-left-10">
									<form:button type="button" id="addRange" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">Add</form:button>
								</div>
							</div>
							<div class="ph_table_border">
								<div class="mega range-header ${empty buyerPlan.rangeList ? 'hide' : ''}">
									<table class="header ph_table border-none" width="100%">
										<thead>
											<tr>
												<th class="width_50 width_50_fix align-center"><spring:message code="application.action1"/></th>
												<th class="width_200 width_200_fix align-left"><spring:message code="application.description"/></th>
												<th class="width_100 width_100_fix align-center">Price</th>
											</tr>
										</thead>
									</table>
									<table class="data ph_table border-none" width="100%" id="rangeList">
										<tbody>
											<c:if test="${not empty buyerPlan.rangeList}">
												<c:forEach var="range" items="${buyerPlan.rangeList}" varStatus="status">
													<c:if test="${not empty range.price}">
														<tr data-pos="${status.index}" >
															<td class="width_50 width_50_fix align-center removeRange" >
																<a data-placement="top" title="Delete">
																	<img src="${pageContext.request.contextPath}/resources/images/delete1.png">
																</a>
																<form:hidden path="rangeList[${status.index}].id" />
																<form:hidden path="rangeList[${status.index}].rangeStart" data-start="start" class="range-start" />
																<form:hidden path="rangeList[${status.index}].rangeEnd" data-end="end" class="range-end" />
																<form:hidden path="rangeList[${status.index}].price" />
																<form:hidden path="rangeList[${status.index}].displayLabel" />
															<td class="width_200 width_200_fix align-left">${range.displayLabel}</td>

															<td class="width_100 width_100_fix align-center">
																<fmt:formatNumber groupingUsed = "true" type="number" value="${range.price}" />
															</td>

														</tr>
													</c:if>
												</c:forEach>
											</c:if>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
					<div class="clear"></div>
					<div class="Invited-Supplier-List import-supplier white-bg marg-top-10">
						<div class="meeting2-heading">
							<h3>Time Period Details</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10">Duration</label>
								</div>
								<div class="col-md-4">
									<form:input path="" placeholder="e.g. 12 months" data-validation="number" data-validation-optional="true" class="chosen-container-single form-control mar-b10 feature_box" id="durationId" />
								</div>
								<div class="col-md-1">
									<label class="infoSticker" style="width: 70%"> Months</label>
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10">Discount</label>
								</div>
								<div class="col-md-4">
									<form:input path="" placeholder="e.g. 10 %" data-validation="number" data-validation-optional="true" class="chosen-container-single form-control mar-b10 feature_box" id="discountId" />
								</div>
								<div class="col-md-1">
									<label class="infoSticker" style="width: 70%">( % )</label>
								</div>
							</div>
						</div>
						<div class="row marg-bottom-10">
							<div class="col-md-3"></div>
							<div class="col-md-5 marg-left-10">
								<form:button type="button" id="addPeriod" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">Add</form:button>
							</div>
						</div>
						<div class="ph_table_border">
							<div class="mega period-header ${empty buyerPlan.planPeriodList ? 'hide' : ''}">
								<table class="header ph_table border-none" width="100%">
									<thead>
										<tr>
											<th class="width_50 width_50_fix align-center"><spring:message code="application.action1"/></th>
											<th class="width_200 width_200_fix align-center">Duration</th>
											<th class="width_100 width_100_fix align-center">Discount</th>
										</tr>
									</thead>
								</table>
								<table class="data ph_table border-none" width="100%" id="periodList">
									<tbody>
										<c:if test="${not empty buyerPlan.planPeriodList}">
											<c:forEach var="period" items="${buyerPlan.planPeriodList}" varStatus="status">
												<c:if test="${not empty period.planDuration}">
													<tr data-pos="${status.index}">
														<td class="width_50 width_50_fix align-center removePeriod">
															<a data-placement="top" title="Delete"> 
																<img src="${pageContext.request.contextPath}/resources/images/delete1.png">
															</a>
															<form:hidden path="planPeriodList[${status.index}].id" />
															<form:hidden path="planPeriodList[${status.index}].planDuration" />
															<form:hidden path="planPeriodList[${status.index}].planDiscount" />
														<td class="width_200 width_200_fix align-center">${period.planDuration}</td>
														<td class="width_100 width_100_fix align-center">${period.planDiscount}(%)</td>
													</tr>
												</c:if>
											</c:forEach>
										</c:if>
									</tbody>
								</table>
							</div>
						</div>
					</div>
					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15 marg-top-20">
							<form:button type="submit" id="saveBuyerPlan" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out  ${canEdit ? '':'disabled'}">save</form:button>
							<c:url value="/admin/plan/buyerPlanList" var="createUrl" />
							<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">
								<spring:message code="application.cancel" />
							</a>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/saas.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/ckeditor/ckeditor.js"/>"></script>
<script>
   // CKEDITOR.env.isCompatible = true;
</script>
<script>
	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true
	});
	/*  $.formUtils.addValidator({
		name : 'validate_range_start',
		validatorFunction : function(value, $el, config, language, $form) {
			var endRange = parseInt($("#rangeEndId").val());
			if (isNaN(endRange) || endRange == ''  || endRange == 'undefined') {
				return true;
			}
			if (isNaN($("#rangeStartId").val().replace(/,/g, '')) || $("#rangeStartId").val() == '') {
				return false;
			}
			var startRange = parseInt($("#rangeStartId").val());
			
			if (parseInt(startRange) >= parseInt(endRange)) {
				return false;
			}
		},
		errorMessage : 'Start range value should be less than end value',
		errorMessageKey : 'lte'
	});	
	
	$.formUtils.addValidator({
		name : 'validate_range_end',
		validatorFunction : function(value, $el, config, language, $form) {
			if (isNaN($("#rangeEndId").val().replace(/,/g, '')) || $("#rangeEndId").val() == '') {
				return false;
			}
			var endRange = parseInt($("#rangeEndId").val());
			var startRange = parseInt($("#rangeStartId").val());
			if (isNaN(startRange) || startRange == '' || startRange == 'undefined') {
				startRange = 0;
			}
			
			if (parseInt(startRange) >= parseInt(endRange)) {
				return false;
			}
		},
		errorMessage : 'End range value should be greater than start value',
		errorMessageKey : 'lte'
	});	 */
	
	
	//CKEDITOR.inline( 'description' );

	<c:if test="${planInUse}">
//	CKEDITOR.config.readOnly = true;
	$(window).bind('load', function() {
		var allowedFields = '#idStatus,#saveBuyerPlan,#idPlanDescription,#idStatus,.button-previous,#idPlanList,#idDashboard';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if>

</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/plan.js"/>"></script>