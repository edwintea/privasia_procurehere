<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authorize access="hasRole('SUBSCRIPTION_DETAILS') or hasRole('ADMIN')" var="canEdit" />
<spring:message var="promotionCodeDesk" code="application.owner.promotion.code" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${promotionCodeDesk}] });
});
</script>

<div id="page-content" view-name="promotionalCode">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a href="${pageContext.request.contextPath}/owner/ownerDashboard">
					<spring:message code="application.dashboard" />
				</a></li>

			<li class="active"><c:out value='${btnValue}' /> <spring:message code="promotion.administration" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="" style="color: white;">
				<img src="${pageContext.request.contextPath}/resources/images/promo1.png" height="30px" width="30px">
				<spring:message code="promotion.title" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="promotion.title" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<form:form id="frmPromotionalCode" cssClass="form-horizontal bordered-row" method="post" action="promotionalCode" modelAttribute="promotionObject">
					<form:hidden path="id" />

					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="promoCode" cssClass="marg-top-10">
								<spring:message code="promo.code.title" />
							</form:label>
						</div>
						<div class="col-md-5 ${promotionObject.inUse ? 'disabled':''}">
							<spring:message code="promotion.promoCode.placeHolder" var="name" />
							<form:input path="promoCode" data-validation="required length" data-validation-length="1-64" cssClass="form-control" id="idPromoCode" placeholder="${name}" />
						</div>
					</div>

					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="promoName" cssClass="marg-top-10">
								<spring:message code="promo.code.name" />
							</form:label>
						</div>
						<div class="col-md-5 ${promotionObject.inUse ? 'disabled':''}">
							<spring:message code="promotion.promoName.placeHolder" var="name" />
							<form:input path="promoName" data-validation="required length" data-validation-length="1-250" cssClass="form-control" id="idPromoName" placeholder="${name}" />
						</div>
					</div>


					<div class="row marg-bottom-20 marg_left_0 ">
						<div class="col-md-3">
							<form:label path="promoDiscount" cssClass="marg-top-10">
								<spring:message code="promo.code.discount" />
							</form:label>
						</div>
						<div class="col-md-5 ${promotionObject.inUse ? 'disabled':''}">
							<spring:message code="promotion.promoDiscount.placeHolder" var="name" />
							<form:input type="text" path="promoDiscount" data-validation="required length number" data-validation-length="1-6" cssClass="form-control" id="idPromoDiscount" placeholder="${name}" />

						</div>
					</div>

					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="discountType" cssClass="marg-top-10">

								<spring:message code="promo.code.discountType" />
							</form:label>
						</div>
						<div class="col-md-5 ${promotionObject.inUse ? 'disabled':''}">
							<form:select path="discountType" cssClass="form-control disablesearch chosen-select" id="idDiscountType" data-validation="required">
								<form:option value="" label="Select Discount type" />
								<form:options items="${discountList}" />
							</form:select>
						</div>
					</div>

					<div class="row marg-bottom-20 marg_left_0 ">
						<div class="col-md-3">
							<form:label path="usageLimit" cssClass="marg-top-10">
								<spring:message code="promo.code.usagelimit" />
							</form:label>
						</div>
						<div class="col-md-5 ${promotionObject.inUse ? 'disabled':''}">
							<spring:message code="promotion.promoUsagesLimit.placeHolder" var="name" />
							<form:input type="text" path="usageLimit" data-validation="length number" data-validation-length="max6" cssClass="form-control" id="idPromoUsageLimit" placeholder="${name}" data-validation-optional="true" />
						</div>
					</div>

					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="usageLimitType" cssClass="marg-top-10">
								<spring:message code="promo.code.usageLimitType" />
							</form:label>
						</div>
						<div class="col-md-5 ${promotionObject.inUse ? 'disabled':''}">
							<form:select path="usageLimitType" cssClass="form-control disablesearch chosen-select" id="idUsageLimitType" data-validation="" data-validation-length="1-32">
								<form:option value="" label="Select Usage Limit Type" />
								<form:options items="${usageLimitTypeList}" />
							</form:select>
						</div>
					</div>

					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="buyer" cssClass="marg-top-10">
								<spring:message code="promo.code.buyers" />
							</form:label>
						</div>
						<div class="col-md-5 ${promotionObject.inUse ? 'disabled':''}">
							<form:select path="buyer" cssClass="form-control chosen-select" id="idBuyerList">
								<form:option value="" label="Select Buyer" />
								<form:options items="${buyerList}" itemValue="id" itemLabel="companyName"></form:options>
							</form:select>
						</div>
					</div>
					
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="buyer" cssClass="marg-top-10">
								<spring:message code="promo.code.supplier" />
							</form:label>
						</div>
						<div class="col-md-5 ${promotionObject.inUse ? 'disabled':''}">
							<form:select path="supplier" cssClass="form-control chosen-select" id="idBuyerList">
								<form:option value="" label="Select Supplier" />
								<form:options items="${supplierList}" itemValue="id" itemLabel="companyName"></form:options>
							</form:select>
						</div>
					</div>
					
					
					
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="buyer" cssClass="marg-top-10">
								<spring:message code="promo.code.supplierplan" />
							</form:label>
						</div>
						<div class="col-md-5 ${promotionObject.inUse ? 'disabled':''}">
							<form:select path="supplierPlan" cssClass="form-control chosen-select" id="idBuyerList">
								<form:option value="" label="Select Supplier Plan" />
								<form:options items="${supplierPlanList}" itemValue="id" itemLabel="planName"></form:options>
							</form:select>
						</div>
					</div>
					
					
					
					
					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="buyerPlan" cssClass="marg-top-10">
								<spring:message code="promo.code.buyerplan" />
							</form:label>
						</div>
						<div class="col-md-5 ${promotionObject.inUse ? 'disabled':''}">
							<form:select path="buyerPlan" cssClass="form-control chosen-select" id="idBuyerList">
								<form:option value="" label="Select Buyer Plan" />
								<form:options items="${buyerplanList}" itemValue="id" itemLabel="planName"></form:options>
							</form:select>
						</div>
					</div>
					

					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<label><spring:message code="promotion.promotionEffective" /></label>
						</div>
						<div class="col-md-5  ${promotionObject.inUse ? 'disabled':''}">
							<div>
								<form:input path="effectiveDate" readonly="true" id="effectiveDate" data-original-title="Effective date" class="nvclick form-control for-clander-view" style="width: 398px;" data-validation="required date" data-validation-format="dd/mm/yyyy"

									placeholder="dd/mm/yyyy" />
							</div>
						</div>
					</div>

					<div class="row marg-bottom-20 marg_left_0">

						<div class="col-md-3 ">
							<label><spring:message code="promotion.promotionExpiry" /></label>
						</div>
						<div class="col-md-5 ${promotionObject.inUse ? 'disabled':''}">
							<div>
								<form:input path="expiryDate" id="expiryDate" readonly="true" data-original-title="Expiry date" class="nvclick form-control for-clander-view" style="width: 398px;" data-validation="required date" data-validation-format="dd/mm/yyyy" placeholder="dd/mm/yyyy" />
							</div>
						</div>
					</div>


					<div class="row marg-bottom-20 marg_left_0">
						<div class="col-md-3">
							<form:label path="status" cssClass="marg-top-10">
								<spring:message code="application.status" />
							</form:label>
						</div>
						<div class="col-md-5">

							<form:select path="status" cssClass="form-control disablesearch chosen-select" id="idStatus">

								<form:options items="${statusList}" />
							</form:select>
						</div>
					</div>



					<div class="row marg-bottom-202 marg_left_0">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15 marg-top-20">

							<form:button type="submit" value="save" id="savePromotionalCode" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ${canEdit ? '':'disabled'}">Save</form:button>

							<c:url value="/admin/promotionalCodeList" var="createUrl" />
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
<style>
input[readonly].for-clander-view, input[readonly].for-timepicker-view {
	cursor: default !important;
}

</style>

<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});
</script>

<script>
	$(document).ready(function() {
		$(function() {
			"use strict";
			var nowTemp = new Date();
			var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
			$('#effectiveDate ,#expiryDate').bsdatepicker({
				format : 'dd/mm/yyyy',
				onRender : function(date) {
					if (date.valueOf() < now.valueOf()) {
						 return 'disabled'; 
					}
				}

			}).on('changeDate', function(e) {
				$(this).blur();
				$(this).bsdatepicker('hide');
			});

		});
	});
</script>