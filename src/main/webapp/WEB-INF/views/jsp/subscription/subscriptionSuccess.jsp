<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div id="page-content">
	<div class="container">
		<div class="registraion_wrap">
			<div class="reg_inner">
				<div class="reg_heading">${param.renewal != null ? 'Renewal ' : (param.changePlan != null ? 'Change of Subscription ' : 'Subscription ')}successful${param.changePlan != null ? ' to ' : ' for ' }plan [${subscription.plan.shortDescription}]</div>
				<div class="reg_form_box">
					<div class="con_inner">
						<div class="con_row">
							<div class="con_text"><spring:message code="subscription.success.summary"/></div>
							<c:if test="${subscription.totalPriceAmount > 0 }">
								<div class="con_result"><spring:message code="subscription.success.subscription.for"/> ${subscription.planType == 'PER_USER' ? paymentTransaction.userQuantity : paymentTransaction.eventQuantity } ${subscription.planType == 'PER_USER' ? ' Users' : ' Events' } ${ (subscription.planType == 'PER_USER' && subscription.planPeriod.planDuration == '12') ? ' for 1' : subscription.planPeriod.planDuration }
									${subscription.planType == 'PER_USER' && subscription.planPeriod.planDuration == '12' ? ' Year' : subscription.planType == 'PER_USER' ? ' Months' : '' }</div>
							</c:if>
						</div>
						<div class="con_row">
							<div class="con_text">Transaction Status</div>
							<div class="con_result" style="color: ${paymentTransaction.status.value == 'SUCCESS' ? 'green' : 'red'};">${paymentTransaction.status.value}
								<span style="display: ${paymentTransaction.status.value == 'IN_PROGRESS' ? '' : 'none'};color: #737373;">&nbsp;(Please refresh the page to check the updated transaction status)</span></div>
						</div>
						<div class="con_row">
							<div class="con_text"><spring:message code="subscription.success.transaction.id"/></div>
							<div class="con_result">${paymentTransaction.paymentToken}</div>
						</div>
						<c:if test="${!empty paymentTransaction.currencyCode}">
							<div class="con_row">
								<div class="con_text"><spring:message code="prtemplate.total.amount"/></div>
								<div class="con_result">${paymentTransaction.currencyCode}
									<c:if test="${buyer.registrationOfCountry.countryCode == 'MY'}">
										<fmt:formatNumber groupingUsed = "false" var="tax" type="number" minFractionDigits="0" maxFractionDigits="2" value="${subscription.plan.tax}" />
									</c:if>
									<c:if test="${buyer.registrationOfCountry.countryCode != 'MY'}">
										<fmt:formatNumber groupingUsed = "false" var="tax" type="number" minFractionDigits="0" maxFractionDigits="2" value="0" />
									</c:if>
								<fmt:formatNumber groupingUsed = "true" type="number" minFractionDigits="2" maxFractionDigits="2" value="${paymentTransaction.totalPriceAmount}" /> (<spring:message code="subscription.inclusive.of"/> ${tax} % SST)
								</div>
							</div>
						</c:if>
						</form>
					</div>
				</div>
				<div class="clear"></div>
			</div>
		</div>
	</div>
</div>
<style>
	.pset_footer {
		position: absolute;
		bottom: 0;
	}

	.registraion_wrap {
		padding: 36px 0 0px 0;
	}
</style>