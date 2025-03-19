<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="registraion_wrap">
	<div class="reg_inner">
		<div class="reg_heading">Subscription error for plan [${plan.shortDescription}]</div>
		<div class="reg_form_box">
			<div class="con_inner">
				<div class="con_row">
					<div class="con_text">Summary of Transaction</div>
					<div class="con_result">There was an error during subscription purchase.</div>
				</div>
				<div class="con_row">
					<div class="con_text">Transaction Error Code</div>
					<div class="con_result" style="color: red">${paymentTransaction.errorCode}</div>
				</div>
				<div class="con_row">
					<div class="con_text">Transaction Error Message</div>
					<div class="con_result">${paymentTransaction.errorMessage}</div>
				</div>
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
					<div class="con_result">${paymentTransaction.currencyCode} &nbsp; ${paymentTransaction.amount}  (Inclusive of ${tax} % SST)</div>
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
