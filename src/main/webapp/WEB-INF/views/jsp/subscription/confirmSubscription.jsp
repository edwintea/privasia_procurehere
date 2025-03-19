<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="registraion_wrap">
	<div class="reg_inner">
		<div class="reg_heading"><spring:message code="subscription.confirm.subscription.plan"/> [${subscription.plan.shortDescription}]</div>
		<div class="reg_form_box">
			<div class="rf_inner">
				<form class="form-horizontal" id="idSubscribeForm" action='${pageContext.request.contextPath}/buyerSubscription/confirm' method="post">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
					<input type="hidden" name="token" value="${sessionScope.TOKEN}" /> 
					<input type="hidden" name="payerId" value="${sessionScope.PAYERID}" />
					<input type="hidden" name="paymentTransactionId" value="${paymentTransaction.id}" />
					<div class="rf_row">
						<div class="rfr_text"><spring:message code="buyplan.full.name"/></div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="required" value="${paymentTransaction.fullName}">
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text"><spring:message code="buyer.profilesetup.loginmail"/></div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="email" value="${paymentTransaction.loginEmail}">
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text"><spring:message code="buyer.profilesetup.commemail"/></div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="email" value="${paymentTransaction.communicationEmail}">
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text"><spring:message code="subscription.contact.detail"/></div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="number" value="${paymentTransaction.companyContactNumber}">
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text"><spring:message code="buyercreation.company"/></div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="required" value="${paymentTransaction.companyName}">
						</div>
					</div>
<%-- 					<div class="rf_row">
						<div class="rfr_text">Quantity</div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="required" value="${subscription.planQuantity} ${subscription.plan.chargeModel == 'FLAT_FEE' ? (subscription.plan.periodUnit) : ' CREDIT'}S" />
						</div>
					</div> --%>
					<div class="rf_row">
						<div class="rfr_text"><spring:message code="prsummary.total2"/></div>
						<div class="rfr_field">
						<fmt:formatNumber groupingUsed = "false" var="payAmount" type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentTransaction.totalPriceAmount}" />
						<fmt:formatNumber groupingUsed = "false" var="tax" type="number" minFractionDigits="0" maxFractionDigits="2" value="${subscription.plan.tax}" />
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="required" value="${paymentTransaction.currencyCode}<fmt:formatNumber groupingUsed = "true"  type="number" minFractionDigits="0" maxFractionDigits="2" value="${payAmount}" />"> (Inclusive of ${tax}% SST)
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">&nbsp;</div>
						<div class="rfr_field" id="idButtonHolder">
							<button type="submit" class="frm_bttn1 hvr-pop sub_frm"><spring:message code="renew.plan.confirm.pay"/></button>
						</div>
					</div>
				</form>
			</div>
		</div>
		<div class="clear"></div>
	</div>
</div>
<!-- Resource jQuery -->
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
</script>
<style>
.pset_footer {
    position: absolute;
    bottom: 0;
    }
</style>