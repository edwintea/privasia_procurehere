<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="registraion_wrap">
	<div class="reg_inner">
		<div class="reg_heading">CONFIRM SUBSCRIPTION for plan [${subscription.plan.shortDescription}]</div>
		<div class="reg_form_box">
			<div class="rf_inner">
				<form class="form-horizontal" id="idSubscribeForm" action='${pageContext.request.contextPath}/subscription/confirm' method="post">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
					<input type="hidden" name="token" value="${sessionScope.TOKEN}" /> 
					<input type="hidden" name="payerId" value="${sessionScope.PAYERID}" />
					<input type="hidden" name="paymentTransactionId" value="${paymentTransaction.id}" />
					<div class="rf_row">
						<div class="rfr_text">Full Name</div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="required" value="${paymentTransaction.fullName}">
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">Login Email</div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="email" value="${paymentTransaction.loginEmail}">
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">Communication Email</div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="email" value="${paymentTransaction.communicationEmail}">
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">Contact Detail</div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="number" value="${paymentTransaction.companyContactNumber}">
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">Company Name</div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="required" value="${paymentTransaction.companyName}">
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text"><spring:message code="label.rftbq.th.quantity" /></div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="required" value="${subscription.planQuantity} ${subscription.plan.chargeModel == 'FLAT_FEE' ? (subscription.plan.periodUnit) : ' CREDIT'}S" />
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">Total</div>
						<div class="rfr_field">
							<input type="text" class="field_style form-control" readonly="readonly" data-validation="required" value="${paymentTransaction.currencyCode} ${paymentTransaction.amount}">
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">&nbsp;</div>
						<div class="rfr_field" id="idButtonHolder">
							<button type="submit" class="frm_bttn1 hvr-pop sub_frm">Confirm & Pay</button>
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
