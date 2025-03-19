<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/saas.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<div class="registraion_wrap">
	<div class="reg_inner">
		<div class="reg_heading">${param.renewal != null ? 'Renewal' : (param.changePlan != null ? 'Change of Subscription' : 'Subscription')} successful ${param.changePlan != null ? 'to' : 'for' }
			plan [${subscription.plan.shortDescription}]</div>
		<div class="reg_form_box">
			<div class="con_inner">
				<div class="con_row">
					<div class="con_text">Summary of Transaction</div>
					<c:if test="${subscription.plan.price > 0 }">
						<div class="con_result">Subscription for ${subscription.planQuantity} ${subscription.plan.chargeModel == 'FLAT_FEE' ? (subscription.plan.periodUnit) : ' CREDIT'}S.</div>
					</c:if>
					<c:if test="${subscription.plan.price == 0 }">
						<div class="con_result">Trial subscription for ${subscription.plan.period} ${subscription.plan.periodUnit}S.</div>
					</c:if>
				</div>
				<div class="con_row">
					<div class="con_text">Transaction Status</div>
					<div class="con_result" style="color: ${paypalResponse['ACK'] == 'Success' ? 'green' : 'red'};">${paypalResponse['ACK']}</div>
				</div>
				<div class="con_row">
					<div class="con_text">Transaction ID</div>
					<div class="con_result">${paypalResponse['PAYMENTINFO_0_TRANSACTIONID']}</div>
				</div>
				<c:if test="${!empty paypalResponse['PAYMENTINFO_0_CURRENCYCODE'] }">
					<div class="con_row">
						<div class="con_text">Total Amount</div>
						<div class="con_result">${paypalResponse['PAYMENTINFO_0_CURRENCYCODE']}${paypalResponse['PAYMENTINFO_0_AMT']}</div>
					</div>
				</c:if>
				</form>
			</div>
		</div>
		<div class="clear"></div>
	</div>
</div>
