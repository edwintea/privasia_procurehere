<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<sec:authorize access="hasRole('SUBSCRIPTION_DETAILS') or hasRole('ADMIN')" var="canEdit" />
<style type="text/css">
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}
</style>
<div id="page-content" view-name="supplierplan">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li>
				<a href="${pageContext.request.contextPath}/owner/ownerDashboard">
					<spring:message code="application.dashboard" />
				</a>
			</li>
			<c:url value="/admin/supplierplan/supplierplanList" var="createUrl" />
			<li>
				<a href="${createUrl} ">
					<spring:message code="supplierplan.list" />
				</a>
			</li>
			<li class="active">
				<spring:message code="supplierplan.administration" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="supplierplan.title" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="supplierplan.title" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<c:url var="savePlan" value="/admin/supplierplan/savePlan" />
				<form:form id="frmPlan" cssClass="form-horizontal bordered-row" method="post" action="${savePlan}" modelAttribute="plan">
				<c:set var="tax" value="${plan.tax != null ? plan.tax : 0}" scope="page"/>
				<c:set var="defaultTaxAmount" value="${ 50 + ((50 * tax)/100)}"/>
				<fmt:formatNumber groupingUsed = "false" var="taxValueFormat" type="number" minFractionDigits="0" maxFractionDigits="2" value="${tax}" />
				<fmt:formatNumber groupingUsed = "false" var="taxAmountFormt" type="number" minFractionDigits="0" maxFractionDigits="2" value="${defaultTaxAmount}" />
				<input type="hidden" id="taxValueFormat" value="${taxValueFormat}">
					<input type="hidden" id="taxAmountFormt" value="${taxAmountFormt}">
					<form:hidden path="id" />
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="planName" cssClass="marg-top-10">
								<spring:message code="plan.planName" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="plan.planDescription.placeHolder" var="desc" />
							<spring:message code="plan.planName.placeHolder" var="name" />
							<form:input path="planName" data-validation="required length" data-validation-length="1-128" cssClass="form-control" id="idPlanName" placeholder="${name}" />
						</div>
					</div>
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
							<form:textarea path="shortDescription" class="form-control textarea-autosize" data-validation="required length" data-validation-length="1-150" cssClass="form-control" id="idPlanDescription"
								placeholder="${desc}" />
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
					<div class="clear"></div>
					<!-- Plan Details -->
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>Plan Details</h3>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list">
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10">Validity Period &amp; Type</label>
								</div>
								<div class="col-md-3">
									<form:input path="period" placeholder="e.g. 1" class="form-control" data-validation="required length" data-validation-length="max6" />
								</div>
								<div class="col-md-2">
									<form:select path="periodUnit" cssClass="chosen-select disablesearch" data-validation="required">
										<form:options items="${periodUnitTypeList}" />
									</form:select>
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10">Tax</label>
								</div>
								<div class="col-md-2">
								<form:input path="tax" data-validation="custom" data-validation-regexp="^\d{1,16}(\.\d{1,2})?$" data-validation-ignore=","
								data-validation-error-msg="Only numbers and ',' allowed, length should be less then 16 and after decimal 2 digits allowed" cssClass="form-control" id="idTax" placeholder="Enter tax" data-sanitize="numberFormat"
								data-sanitize-number-format="0,0.00" value="${taxValueFormat}"/>
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10">Price &amp; Currency</label>
								</div>
								<div class="col-md-3">
									<form:input path="price" placeholder="e.g. 50" class="form-control" data-validation="required number length" data-validation-length="max10" />
								</div>
								<div class="col-md-2">
									<form:select class="chosen-select" id="chosenCurrency" path="currency" data-validation="required">
										<form:option value="">Currency</form:option>
										<form:options items="${currencyList}" itemValue="id" itemLabel="currencyCode" />
									</form:select>
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10">Charge Model</label>
								</div>
								<div class="col-md-2">
									<form:select class="chosen-select disablesearch" path="chargeModel">
										<form:options items="${chargeModelList}" itemLabel="value" />
									</form:select>
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<label class="marg-top-10">Buyer Limit</label>
								</div>
								<div class="col-md-3">
									<form:input path="buyerLimit" placeholder="e.g. 10" class="form-control" data-validation="required number" data-validation-allowing="range[1;999999999]" />
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3">
									<form:label path="description" cssClass="marg-top-10">
										<spring:message code="plan.planDescription" />
									</form:label>
								</div>
								<div class="col-md-3">
									<div class="width100 inlineEditor" onclick="this.style.backgroundColor='#fff'">
										<div>
											<%
												String defaultHtml = "<h3 id='idEditorPlanName'>Fusce vitae porttitor</h3><p id='idEditorPlanDesc'><strong>Lorem ipsum dolor sit amet dolor. Duis blandit vestibulum faucibus a, tortor. </strong></p><p>Proin nunc justo felis mollis tincidunt, risus risus pede, posuere cubilia Curae, Nullam euismod, enim. Etiam nibh ultricies dolor ac dignissim erat volutpat. Vivamus fermentum nisl nulla sem in metus. Maecenas wisi. Donec nec erat volutpat.</p><div id='idEditorPrice' class='price'>USD 50</div><div id='idEditorTax' class='unit'></div><div id='idEditorUnit' class='unit'><span id='idEditorFeeType'></span><span id='idEditorDuration'>PER MONTH</span></div><hr><blockquote id='plan_info'><ul><li>Clip from anywhere on the web</li><li>Share and discuss in Evernote</li><li>Sync across phones an computers</li><li>Access notes when you?re offline</li><li>Save emails into Evernote</li><li>Add passcode lock on mobile apps</li></ul></blockquote><blockquote><p>Libero nunc, rhoncus ante ipsum non ipsum. Nunc eleifend pede turpis id sollicitudin fringilla. Phasellus ultrices, velit ac arcu.</p></blockquote><p>Pellentesque nunc. Donec suscipit erat. Pellentesque habitant morbi tristique ullamcorper.</p><p><s>Mauris mattis feugiat lectus nec mauris. Nullam vitae ante.</s></p>";
													pageContext.setAttribute("defaultHtml", defaultHtml);
											%>
											<textarea id="description" name="description" class="form-control" data-validation-length="1-2000" data-validation="required length"
												placeholder="e.g. SME Basic Plan with per event charges limited to 2 users">
										<c:if test="${!empty plan.description}">
											<c:out value="${plan.description }" escapeXml="true" />
										</c:if>
										
										<c:if test="${empty plan.description}">
											<c:out value="${defaultHtml}" escapeXml="true" />
										</c:if>
									</textarea>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15 marg-top-20">
							<form:button type="submit" value="${btnValue}" id="savePlan" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out  ${canEdit ? '':'disabled'}">${btnValue}</form:button>
							<c:url value="/admin/supplierplan/supplierplanList" var="createUrl" />
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
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/ckeditor/ckeditor.js"/>"></script>
<script>
    CKEDITOR.env.isCompatible = true;
</script>
<script>




	$.validate({
		lang : 'en'
	});
	
	CKEDITOR.inline( 'description' );

	<c:if test="${inUse}">
	CKEDITOR.config.readOnly = true;
	$(window).bind('load', function() {
		var allowedFields = '#chosenOrder,#savePlan,#idPlanDescription,#idStatus,.button-previous';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	$(document).ready(function(){
		
		var taxAmountFormt = $('#taxAmountFormt').val();
		var taxValueFormat = $('#taxValueFormat').val();
		$('#idEditorTax').html('('+taxAmountFormt+' Inclusive '+taxValueFormat+'% SST)');
		});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/plan.js"/>"></script>