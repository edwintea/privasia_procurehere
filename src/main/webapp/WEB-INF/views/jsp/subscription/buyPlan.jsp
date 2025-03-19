<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="registraion_wrap">
	<div class="reg_inner">
		<div class="reg_heading">Enter Details to purchase [${plan.shortDescription}]</div>
		<div class="reg_form_box">
			<div class="rf_inner">
				<form:form class="form-horizontal" commandName="subscription" id="idSubscribeForm" action='${pageContext.request.contextPath}/buyerSubscription/payment/${plan.id}' method="post">
					<form:hidden path="priceAmount" />
					<form:hidden path="priceDiscount" />
					<form:hidden path="promoCodeDiscount" />
					<form:hidden path="totalPriceAmount" />
					<form:hidden path="userQuantity" />
					<form:hidden path="eventQuantity" />
					<form:hidden path="planType" />
					<form:hidden path="autoChargeSubscription" />
					<c:if test="${not empty subscription.planPeriod}">
						<form:hidden path="planPeriod.id" />
						<form:hidden path="planPeriod.planDuration" />
						<form:hidden path="planPeriod.planDiscount" />
					</c:if>
					<form:hidden path="range.id" />
					<form:hidden path="range.price" />
					<c:if test="${not empty subscription.paymentTransactions[0].promoCode}">
						<form:hidden path="paymentTransactions[0].promoCode.id" />
					</c:if>
					<div class="rf_row">
						<div class="rfr_text">Company Name</div>
						<div class="rfr_field">
							<form:input type="text" id="idCompanyName" cssClass="form-control" placeholder="Your company name" data-validation="required length alphanumeric company_name" data-validation-allowing="-_ &.()" data-validation-length="4-124"
								path="paymentTransactions[0].companyName" />
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">Company Registration No</div>
						<div class="rfr_field">
							<form:input type="text" id="idCompRegNum" cssClass="form-control" placeholder="Your company registration number" data-validation="required length alphanumeric crn_number" data-validation-allowing="-_ " data-validation-length="2-124"
								path="paymentTransactions[0].companyRegistrationNumber" />
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">Full Name</div>
						<div class="rfr_field">
							<form:input type="text" cssClass="form-control" placeholder="Company contact person name" data-validation="required length" path="paymentTransactions[0].fullName" data-validation-length="4-100" />
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">Contact Number</div>
						<div class="rfr_field">
							<form:input type="text" cssClass="form-control" placeholder="e.g. 0123456789" data-validation="number length" path="paymentTransactions[0].companyContactNumber" data-validation-length="6-14" />
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">Login Email</div>
						<div class="rfr_field">
							<form:input type="text" id="idLoginEmail" cssClass="form-control" placeholder="e.g. contact@company.com" data-validation="required length email login_email" data-validation-length="6-128" path="paymentTransactions[0].loginEmail" />
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">Communication Email</div>
						<div class="rfr_field">
							<form:input type="text" cssClass="form-control" placeholder="All email communications will be sent to this address" data-validation="email" path="paymentTransactions[0].communicationEmail" />
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text">Registration Country</div>
						<div class="rfr_field">
							<form:select path="paymentTransactions[0].country" id="idRegCountry" cssClass="chosen-select form-control" data-validation="required crn_number company_name">
								<form:option value="">Select Country of Registration</form:option>
								<form:options items="${countryList}" itemValue="id" itemLabel="countryName" />
							</form:select>
						</div>
					</div>
					<div class="rf_row">
						<div class="rfr_text"></div>
						<div class="rfr_field">

							<div style="width: 50%; margin-top: 30px;">
								<table id="totalFeeTable${plan.planType == 'PER_USER' ? 'User': 'Event'}" class="td-border table1" style="width: 100%">
									<tr>
										<th colspan="2">
											<h4 style="font-weight: bold; color: #3f96d8;">TOTAL FEE</h4>
										</th>
									</tr>

									<c:if test="${not empty plan.basePrice}">
										<%-- <fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${plan.basePrice}" /> --%>
										<fmt:formatNumber groupingUsed = "false" var="BasePrice" type="number" value="${plan.basePrice}" />
										<input type="hidden" id="basePrice" value="${BasePrice}">
										<input type="hidden" id="baseUsers" value="${plan.baseUsers}">
										<tr>
											<td>${subscription.plan.currency.currencyCode}
												<fmt:formatNumber groupingUsed = "true" type="number" value="${plan.basePrice}" />
												for ${plan.baseUsers} Users X ${subscription.planPeriod.planDuration} Months

											</td>
											<td>
												<fmt:formatNumber groupingUsed = "false" var="planBasePrice" type="number" minFractionDigits="2" maxFractionDigits="2" value="${plan.basePrice}" />
										
											<fmt:formatNumber groupingUsed = "true" type="number" minFractionDigits="2" maxFractionDigits="2" value="${planBasePrice * subscription.planPeriod.planDuration}" />
												
											</td>
										</tr>
									</c:if>
									<c:set var="userQuantity" value="${subscription.userQuantity}" />
									<c:set var="approverUserQuantity" value="${subscription.userQuantity}" />
									<c:if test="${not empty plan.basePrice and subscription.userQuantity ge plan.baseUsers}">
										<tr>
											<td id="totalFeeLabel">${subscription.plan.currency.currencyCode}
												<fmt:formatNumber groupingUsed = "true" type="number" value="${subscription.range.price}" />
												X
												<c:if test="${plan.planType == 'PER_USER'}">
													<c:if test="${not empty plan.basePrice}">
														<c:set var="userQuantity" value="${subscription.userQuantity - plan.baseUsers}" />
													</c:if>
										 ${userQuantity} Users X ${subscription.planPeriod.planDuration} Months
										</c:if>
												<c:if test="${plan.planType == 'PER_EVENT'}">
										 ${subscription.eventQuantity} Event
										</c:if>
											</td>
											<td id="totalFeeValue">
												<fmt:formatNumber groupingUsed = "true" type="number" minFractionDigits="2" maxFractionDigits="2" value="${subscription.priceAmount}" />
											</td>
										</tr>
									</c:if>
									<c:if test="${empty plan.basePrice}">
										<tr>
											<td id="totalFeeLabel">${subscription.plan.currency.currencyCode}
												<fmt:formatNumber groupingUsed = "true" type="number" value="${subscription.range.price}" />
												X
												<c:if test="${plan.planType == 'PER_USER'}">
										 ${userQuantity} Users X ${subscription.planPeriod.planDuration} Months
										</c:if>
												<c:if test="${plan.planType == 'PER_EVENT'}">
										 ${subscription.eventQuantity} Event
										</c:if>
											</td>
											<td id="totalFeeValue">
												<fmt:formatNumber groupingUsed = "true" type="number" minFractionDigits="2" maxFractionDigits="2" value="${subscription.priceAmount}" />
											</td>
										</tr>
									</c:if>
									<c:if test="${plan.planType == 'PER_USER'}">
										<!-- table column for show free approver user -->
										<tr>
											<td>${approverUserQuantity}approver users</td>
											<td>0.00</td>
										</tr>
										<tr>
											<td id="totalFeeDiscountLabel">Subscription Discount ${subscription.planPeriod.planDiscount }%</td>
											<td id="totalFeeDiscountValue">
												-
												<fmt:formatNumber groupingUsed = "true" type="number" minFractionDigits="2" maxFractionDigits="2" value="${subscription.priceDiscount}" />
											</td>
										</tr>
									</c:if>
									<c:if test="${not empty subscription.paymentTransactions[0].promoCode}">
										<tr>
											<td id="totalFeePromoLabel">${subscription.paymentTransactions[0].promoCode.promoName}-${subscription.paymentTransactions[0].promoCode.promoDiscount}${subscription.paymentTransactions[0].promoCode.discountType == 'PERCENTAGE'  ? '% OFF' : ' OFF' }</td>
											<td id="totalFeePromoValue">
												-
												<fmt:formatNumber groupingUsed = "true" type="number" minFractionDigits="2" maxFractionDigits="2" value="${subscription.promoCodeDiscount}" />
											</td>
										</tr>
									</c:if>
									<tr>
										<c:set var="tax" value="${not empty plan.tax ? plan.tax : 0}" />
										<fmt:formatNumber groupingUsed = "false" var="taxFormt" type="number" minFractionDigits="0" maxFractionDigits="2" value="${tax}" />
										<td>Tax ${taxFormt}% SST</td>

										
										<fmt:formatNumber groupingUsed = "false" var="priceAmount" type="number" minFractionDigits="0" maxFractionDigits="2" value="${subscription.totalPriceAmount}" />
										<fmt:formatNumber groupingUsed = "false" var="taxAmount" type="number" minFractionDigits="2" maxFractionDigits="2" value="${( priceAmount*taxFormt)/100}" />
										<fmt:formatNumber groupingUsed = "false" var="totalAmountWithTax" type="number" minFractionDigits="2" maxFractionDigits="2" value="${priceAmount + (priceAmount *taxFormt)/100}" />
										<td>
										<fmt:formatNumber groupingUsed = "true"  type="number" minFractionDigits="2" maxFractionDigits="2" value="${taxAmount}" />
										
										</td>
									</tr>
									<tr>
										<td colspan="2" class="total-td">
											<h4 style="font-weight: bold; color: #212223;" id="totalFeeAmount">${subscription.plan.currency.currencyCode}
											
											
											<fmt:formatNumber groupingUsed = "true" type="number" minFractionDigits="0" maxFractionDigits="2" value="${totalAmountWithTax}" />
											
											 </h4>
											<div class="row marg_bottom_10">
												<div class="col-md-8">
													<div class="rf_row">
														<div class="rfr_text">&nbsp;</div>
														<div class="rfr_field" id="idButtonHolder"></div>
														<div class="rfr_text">&nbsp;</div>
														<div class="">
															<!-- <img src="https://www.paypalobjects.com/webstatic/en_US/i/buttons/cc-badges-ppppcmcvdam.png" alt="Credit Card Badges"> -->
														</div>
													</div>
												</div>
												<div class="col-md-4"></div>
											</div>
										</td>
									</tr>
								</table>
							</div>

						</div>
					</div>



					<%-- 	<c:if test="${subscription.plan.price > 0}">
						<div class="rf_row">
							<div class="rfr_text">Plan Price</div>
							<div class="rfr_field">
								<input type="text" class="form-control" value="${subscription.plan.currency.currencyCode} ${subscription.plan.price}" readonly="true" />
								<form:hidden id="idPlanPrice" path="plan.price" />
							</div>
						</div>
						<div class="rf_row">
							<div class="rfr_text">Quantity</div>
							<div class="rfr_field">
								<form:input type="text" cssClass="form-control" placeholder="e.g. 12" data-validation="required number" data-validation-error-msg-number="Range allowed 1 to 9999"
									data-validation-allowing="range[1;9999]" path="planQuantity" />
							</div>
						</div>
						<c:if test="${subscription.plan.chargeModel == 'PER_UNIT'}">
							<div class="rf_row">
								<div class="rfr_text">Credit Quantity</div>
								<div class="rfr_field">
									<form:input type="text" id="idCreditQuantity" cssClass="form-control" placeholder="e.g. 5" data-validation="required number"
										data-validation-error-msg-number="Range allowed 1 to ${subscription.plan.eventLimit} for the chosen plan" data-validation-allowing="range[1;${subscription.plan.eventLimit}]"
										path="eventLimit" />
								</div>
							</div>
						</c:if>
						<div class="rf_row">
							<div class="rfr_text">Total Price</div>
							<div class="rfr_field" style="color: #0095d5; font-weight: bold;">
								${subscription.plan.currency.currencyCode}
								<span id="idTotalPrice">${subscription.plan.price}</span>
							</div>
						</div>
					</c:if>
					<div class="rf_row">
						<div class="rfr_text">&nbsp;</div>
						<div class="rfr_field" id="idButtonHolder">
							<!-- input type='image' name='submit' src='https://www.paypalobjects.com/webstatic/en_US/i/btn/png/btn_subscribe_cc_147x47.png' border='0' align='top' alt='Check out with PayPal' / -->
							<c:if test="${subscription.plan.price == 0}">
								<button type="submit" class="frm_bttn1 hvr-pop sub_frm">Subscribe</button>
							</c:if>
						</div>
						<div class="rfr_text">&nbsp;</div>
						<c:if test="${subscription.plan.price > 0}">
							<div class="rfr_field">
								<!-- <img src="https://www.paypalobjects.com/webstatic/en_US/i/buttons/cc-badges-ppppcmcvdam.png" alt="Credit Card Badges"> -->
							</div>
						</c:if>
					</div> --%>
				</form:form>
			</div>
		</div>
		<div class="clear"></div>
	</div>
</div>
<style>
.table1 td, th {
	text-align: center;
}

.td-border td {
	border: 2px solid #a1a1a1;
	padding-top: 10px;
	padding-bottom: 10px;
	padding-left: 10px;
	padding-right: 10px;
	font-size: 15px;
	padding-right: 10px;
}

.td-border {
	border: 2px solid #a1a1a1;
}

.total-td {
	text-align: right !important;
	padding-top: 0px !important;
	padding-bottom: 0px !important;
}

.marg_bottom_10 {
	margin-bottom: 10px;
}
</style>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript">
	$.formUtils.addValidator({
		name : 'crn_number',
		validatorFunction : function(value, $el, config, language, $form) {
			var countryID = $('#idRegCountry').val();
			var crnNum = $('#idCompRegNum').val();
			var response = true;
			if (countryID == '') {
				return response;
			}
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");

			$.ajax({
				url : "${pageContext.request.contextPath}/owner/checkBuyerRegistrationNumber",
				data : {
					'countryID' : countryID,
					'crnNum' : crnNum
				},
				async : false,
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
				},
				success : function(data) {
					console.log(data);
					response = true;
				},
				error : function(request, textStatus, errorThrown) {
					console.log(textStatus);
					response = false;
				}
			});
			return response;
		},
		errorMessage : 'Company Registration Number already registered in the system',
		errorMessageKey : 'badCrnNumber'
	});

	$.formUtils.addValidator({
		name : 'company_name',
		validatorFunction : function(value, $el, config, language, $form) {
			var countryID = $('#idRegCountry').val();
			var companyName = $('#idCompanyName').val();
			var response = true;
			if (countryID == '') {
				return response;
			}
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url : "${pageContext.request.contextPath}/owner/checkBuyerCompanyName",
				data : {
					'countryID' : countryID,
					'companyName' : companyName
				},
				async : false,
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
				},
				success : function(data) {
					console.log(data);
					response = true;
				},
				error : function(request, textStatus, errorThrown) {
					console.log(textStatus);
					response = false;
				}
			});
			return response;
		},
		errorMessage : 'Company Name already registered in the system',
		errorMessageKey : 'badCompanyName'
	});

	$.formUtils.addValidator({
		name : 'login_email',
		validatorFunction : function(value, $el, config, language, $form) {
			var loginEmailId = $('#idLoginEmail').val();
			var response = true;
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url : "${pageContext.request.contextPath}/owner/checkLoginEmail",
				data : {
					'loginEmail' : loginEmailId
				},
				async : false,
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
				},
				success : function(data) {
					console.log(data);
					response = true;
				},
				error : function(request, textStatus, errorThrown) {
					console.log(textStatus);
					response = false;
				}
			});
			return response;
		},
		errorMessage : 'Login Email already registered in the system',
		errorMessageKey : 'badLoginEmail'
	});

	$.validate({
		lang : 'en',
		modules : 'date, security'
	});

	$(document).ready(function() {
		$('#planQuantity').keyup(function() {
			if ($('#idCreditQuantity').length) {
				$('#idTotalPrice').html(parseInt($.trim($('#idPlanPrice').val())) * parseInt($.trim($(this).val())) * parseInt($.trim($('#idCreditQuantity').val())));
			} else {
				$('#idTotalPrice').html(parseInt($.trim($('#idPlanPrice').val())) * parseInt($.trim($(this).val())));
			}
		});
		if ($('#idCreditQuantity').length) {
			$('#idCreditQuantity').keyup(function() {
				if ($('#idCreditQuantity')) {
					$('#idTotalPrice').html(parseInt($.trim($('#idPlanPrice').val())) * parseInt($.trim($(this).val())) * parseInt($.trim($('#planQuantity').val())));
				}
			});
		}
	});
	<c:if test="${not empty subscription.plan.id}">
	window.paypalCheckoutReady = function() {
		paypal.checkout.setup('${merchantId}', {
			environment : '${paypalEnvironment}',
			container : 'idSubscribeForm',
			condition : function() {
				return $('#idSubscribeForm').isValid();
			},
			//button: 'placeOrderBtn'
			buttons : [ {
				container : 'idButtonHolder',
				type : 'checkout',
				color : 'blue',
				size : 'medium',
				shape : 'rect'
			} ]
		});
	};
	</c:if>
</script>
<script src="//www.paypalobjects.com/api/checkout.js" async></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$('#planQuantity').mask('99', {
			placeholder : "e.g. 12"
		});

		if ($('#idCreditQuantity').length) {
			$('#idCreditQuantity').mask('999', {
				placeholder : "e.g. 5"
			});
		}
	});
</script>
