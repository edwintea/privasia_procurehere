<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<spring:message var="supplierPlanDesk" code="application.owner.supplier.subscription.plan" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere-public.css"/>">
<!-- <script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierPlanDesk}] });
});
</script> -->
<div id="page-content">
	<div class="container col-md-12">
		<header class="form_header">
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap Events-Listing-heading">Supplier Subscription Plans</h2>
			</div>
		</header>
		<div class=" margin-bottom-5">
			<form:form action="${pageContext.request.contextPath}/supplier/billing/doInitiateSupplierPlanPayment" id="supplierCheckoutForm" method="post" modelAttribute="supplier" autocomplete="off">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<input type="hidden" name="supplerPlan" id="supplerPlan" value="${plan.id}" />
				<input type="hidden" name="planPrice" id="planPrice" value="${plan.price }">
				<input type="hidden" name="supplierPrice" id="supplierPrice" value="${plan.price }">
				<div class="buyer-check-section">
					<p class="font-27">Fill in your details to start your subscription process</p>
				</div>
				<div class="row">


					<div class="col-sm-4 col-md-4 col-lg-4 col-xs-4 pack-amount-details">
						<input type="hidden" name="promoCodeId" id="promoCodeId">
						<h4 class="border-btm toptitle" id="toptitle">${plan.planName }</h4>
						<div class="pack-details">
							<p class="sub-heading">Supplier Subscription</p>

						</div>
						<div class="pack-details">
							<div class="form-inline">
								<label class="margin-right-20">Promo Code</label> <input type="text" name="promocode" id="promocode" class="form-control"> <span class="pull-right promo-price" id="promoDiscountPrice"> <input type="hidden" name="promoDiscountPrice" value="0.00"> 0.00
								</span> <span id="promoError"></span>
							</div>
						</div>
						<h4 class="border-btm"></h4>
						<div class="margin-top-5">
							<h4 class="pull-left">TOTAL FEE</h4>
							<h4 class="pull-right" id="totalPrice">
								<input type="hidden" name="totalPrice" value="${plan.price }" /> US$ ${plan.price }
							</h4>
						</div>
						<div id="paypalPayment" style="display: none;" class="btn-submit-unlimited ">
							<span class="rfr_field proceedPayment" id="supplierCheckOutBtn"></span>
							<div>
								<img src="<c:url value="/resources/images/public/Visa.png"/>" alt="Visa"> <img src="<c:url value="/resources/images/public/mastercard-badge.png"/>" alt="Mastercard"> <img src="<c:url value="/resources/images/public/american-express.png"/>" alt="American Express"> <img
									src="<c:url value="/resources/images/public/discover.png"/>" alt="visa-badge">  <img src="<c:url value="/resources/images/public/McAfee.png"/>" alt="McAfee">
							</div>
						</div>
						<div id="submitPayment" class="btn-submit">
							<button type="submit" class="btn btn-primary btn-padding submitSuppButton relativePos">Submit</button>
							<div>
								<img src="<c:url value="/resources/images/public/McAfee.png"/>" alt="McAfee-badge" class="max-80 img-responsive" style="margin-top: 10px;">
							</div>
						</div>
					</div>
					<!-- 					<div class="col-sm-1 col-md-1 col-lg-1 col-xs-0"></div> -->
				</div>
			</form:form>
		</div>
	</div>
</div>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script src="//www.paypalobjects.com/api/checkout.js" async></script>
<script type="text/javascript">
	$.validate({
		lang : 'en',
		modules : 'date, security'
	});

	window.paypalCheckoutReady = function() {
		paypal.checkout.setup('${merchantId}', {
			environment : '${paypalEnvironment}',
			container : 'supplierCheckoutForm',
			condition : function() {
				return $('#supplierCheckoutForm').isValid();
			},
			//button: 'placeOrderBtn'
			buttons : [ {
				container : 'supplierCheckOutBtn',
				type : 'checkout',
				color : 'blue',
				size : 'medium',
				shape : 'rect'
			} ]
		});

	};
</script>
<style>
.pack-amount-details {
	margin-left: 15px;
}

.buyer-check-section p:nth-child(1) {
	margin-top: 1%;
	margin-bottom: 1%;
}

.btn-submit {
	text-align: center;
	margin-top: 35px;
}

.btn-padding {
	padding: 0 65px;
}

.margin-top-5 {
	margin-top: 5px;
}

.left-span .glyph-icon {
	display: none;
}

.pull-right .glyph-icon {
	display: none;
}

.promo-price {
	margin-right: 10px;
}

.sub-heading {
	margin-top: 5% !important;
}

.pack-amount-details img {
	max-width: 50px;
}

.btn-submit-unlimited {
	text-align: center;
	margin-top: 25px;
}

.font-27 {
	text-align: left !important;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$('#submitPayment').css({
			'display' : 'none'
		});

		$('#paypalPayment').css({
			'display' : 'block'
		});

		$('#idAdminMobileNo').mask('+00 00000000000', {
			placeholder : "+60 122735465"
		});
		$('#idCompanyContactNumber').mask('+00 00000000000', {
			placeholder : "+60 322761533"
		});
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierPlanCheckout.js"/>"></script>