<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/saas.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<div id="page-content">
	<div class="container col-md-12">
		<div class="registraion_wrap">
			<div class="reg_inner">
				<div class="reg_heading">CONFIRM ${renewal ? 'Renewal' : ''} SUBSCRIPTION for plan [${subscription.supplierPlan.shortDescription}]</div>
				<div class="reg_form_box">
					<div class="rf_inner">
						<form class="form-horizontal" id="idSubscribeForm" action="${pageContext.request.contextPath}/supplier/${renewal ? 'billing/confirmRenew' : 'confirm'}" method="post">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<input type="hidden" name="token" value="${sessionScope.TOKEN}" />
							<input type="hidden" name="payerId" value="${sessionScope.PAYERID}" />
							<input type="hidden" name="paymentTransactionId" value="${paymentTransaction.id}" />
							<div class="rf_row">
								<div class="rfr_text">Company Name</div>
								<div class="rfr_field">
									<input type="text" class="field_style form-control" readonly="readonly" value="${paymentTransaction.companyName}">
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text">Communication Email</div>
								<div class="rfr_field">
									<input type="text" class="field_style form-control" readonly="readonly" value="${paymentTransaction.communicationEmail}">
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="label.rftbq.th.quantity" /></div>
								<div class="rfr_field">
									<input type="text" class="field_style form-control" readonly="readonly"
										value="${subscription.supplierPlan.period} ${subscription.supplierPlan.chargeModel == 'FLAT_FEE' ? (subscription.supplierPlan.periodUnit) : ' CREDIT'}S" />
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text">Total</div>
								<div class="rfr_field">
								<c:set var="tax" value="${subscription.supplierPlan.tax != null ?subscription.supplierPlan.tax : 0}" scope="page"/>
								<fmt:formatNumber var="tax" type="number" minFractionDigits="0" maxFractionDigits="2" value="${tax}" />
									<input type="text" class="field_style form-control" readonly="readonly" value="${paymentTransaction.currencyCode} ${paymentTransaction.amount} (Inclusive of ${tax}% SST)">
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
	</div>
</div>