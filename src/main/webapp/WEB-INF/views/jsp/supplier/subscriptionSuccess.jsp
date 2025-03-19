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
				<div class="reg_heading">${renewal ? '' : ''} Subscription successful for plan [${subscription.supplierPlan.shortDescription}]</div>
				<div class="reg_form_box">
					<div class="con_inner">
						<div class="con_row">
							<div class="con_text">Summary of Transaction</div>
							<c:if test="${subscription.supplierPlan.price > 0 }">
								<div class="con_result">Subscription ${renewal ? '' : ''} for ${subscription.supplierPlan.shortDescription}</div>
							</c:if>
							<c:if test="${subscription.supplierPlan.price == 0 }">
								<div class="con_result">Trial subscription for ${subscription.supplierPlan.period} ${subscription.supplierPlan.periodUnit}S.</div>
							</c:if>
						</div>
						<div class="con_row">
							<div class="con_text">Transaction Status</div>
							<div class="con_result" style="color: ${paymentTransaction.status.value == 'SUCCESS' ? 'green' : 'red'};">${paymentTransaction.status.value}
								<span style="display: ${paymentTransaction.status.value == 'IN_PROGRESS' ? '' : 'none'};color: #737373;">&nbsp;(Please refresh the page to check the updated transaction status)</span>
							</div>
						</div>
						<div class="con_row">
							<div class="con_text">Transaction ID</div>
							<div class="con_result">${paymentTransaction.paymentToken}</div>
						</div>
						<c:if test="${!empty paymentTransaction.currencyCode }">
							<div class="con_row">
								<div class="con_text">Total Amount</div>

								<c:if test="${supplier.registrationOfCountry.countryCode == 'MY'}">
									<c:set var="tax" value="${subscription.supplierPlan.tax != null ? subscription.supplierPlan.tax : 0}"
										scope="page" />
									<fmt:formatNumber var="tax" type="number" minFractionDigits="0" maxFractionDigits="2" value="${tax}" />
								</c:if>
								<c:if test="${supplier.registrationOfCountry.countryCode != 'MY'}">
									<c:set var="tax" value="0" scope="page" />
									<fmt:formatNumber var="tax" type="number" minFractionDigits="0" maxFractionDigits="2" value="0" />
								</c:if>
								<div class="con_result">${paymentTransaction.currencyCode}&nbsp;${paymentTransaction.amount} (Inclusive of ${tax} % SST)</div>
							</div>
						</c:if>
					</div>
				</div>
				<div class="clear"></div>
			</div>
		</div>
	</div>
</div>