<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<spring:message var="supplierPlanDesk" code="application.owner.supplier.subscription.plan" />
<!-- <script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierPlanDesk}] });
});
</script> -->
<%-- <jsp:include page="/WEB-INF/views/jsp/subscription/SelectSubscriptionPlan.jsp" /> --%>
<form:form commandName="buyer" id="idTrailSkipForm" method="post">
	<form:hidden path="companyName" />
	<form:hidden path="companyRegistrationNumber" />
	<form:hidden path="registrationOfCountry" />
	<form:hidden path="fullName" />
	<form:hidden path="loginEmail" />
	<form:hidden path="communicationEmail" />
	<form:hidden path="companyContactNumber" />
</form:form>
<div class="container margin-bottom-5">
	<div class="buyer-check-section">
		<p>
			Just one step left to subscribe to Procurehere's market leading simplified <br> e-procurement solution
		</p>
		<p>
			Complete your purchase through our secure payment system, and unlock access to the platform which <br> has saved users US$1.3 billion to date and counting
		</p>
	</div>
	<div class="row">
		<div class="col-sm-3 col-md-3 col-lg-3 col-xs-0"></div>
		<div class="col-sm-6 col-md-6 col-lg-6 col-xs-12">
			<ul class="nav nav-tabs">
				<c:forEach items="${planList}" var="plan" varStatus="status" begin="0" end="1">
					<li class="${plan.planType == 'PER_USER' ? 'active': ''}">
						<a data-toggle="tab" href="${plan.planType == 'PER_USER' ? '#ubp': '#ebp'}" class="tab-link ${status.index == 0 ? 'current' : '' } ${plan.planType == 'PER_USER' ? 'userTab': 'eventTab'} tab-link-${status.index}" data-tab="tab-${status.index}"
							data-att-tab="${plan.planType == 'PER_USER' ? 'userTab': 'eventTab'}">${plan.planType == 'PER_USER' ? 'User Based': 'Event Based'}</a>
					</li>
				</c:forEach>
			</ul>
			<div class="tab-content text-center">
				<c:forEach items="${planList}" var="plan" varStatus="status" begin="0" end="1">
					<c:if test="${plan.planType == 'PER_USER'}">
						<input type="hidden" id="userPlanId" value="${plan.id}">
						<input type="hidden" id="usercurrencyId" value="${plan.currency.currencyCode}">
						<div id="ubp" class="tab-pane fade in active">
							<form:form commandName="buyer" id="idTrailUserBasedForm" method="post" action="${pageContext.request.contextPath}/buyerSubscription/doInitiateTrialPayment">
								<form:hidden path="companyName" />
								<form:hidden path="companyRegistrationNumber" />
								<form:hidden path="registrationOfCountry" />
								<form:hidden path="fullName" />
								<form:hidden path="loginEmail" />
								<form:hidden path="communicationEmail" />
								<form:hidden path="companyContactNumber" />
								
								<input type="hidden" name="signupId" id="${signupId}">
								<input type="hidden" name="promoCodeId" id="promoCodeUserId">
								<input type="hidden" name="rangeId" id="rangeUserId">
								<input type="hidden" name="planId" id="planId" value="${plan.id}">
								<input type="hidden" name="countryId" id="countryId" value="${buyer.registrationOfCountry.id}">
								<div class="pack-amount-details pack-padding">
									<h4 class="border-btm">USER BASED</h4>
									<div class="pack-details">
										<p>
											<span class="">Starter Pack</span>
											<span class="pull-right">
												<fmt:formatNumber groupingUsed="false" var="basePrice" type="number" minFractionDigits="2" maxFractionDigits="2" value="${plan.basePrice}" />
												${basePrice}
											</span>
										</p>
										<p>${plan.baseUsers}XUsers + ${plan.baseUsers} X Approvers</p>
									</div>
									<div class="pack-details">
										<c:forEach items="${plan.rangeList}" var="range" varStatus="index" begin="1" end="1">
											<c:set var="endRange" value="${range.rangeEnd}" />
											<c:set var="addtionalUserPrice" value="${range.price}" />
										</c:forEach>
										<p>
											<span class="form-inline form-group">
												Addtional Users
												<!-- <input type="text" name="adduser" id="additionalUsers" class="form-control"> -->
												<input type="text" class="form-control" name="numberUserEvent" id="additionalUsers" data-validation="length number" value="0" data-validation-allowing="range[0;${endRange}]" data-validation-optional="true" data-validation-length="1-3">
											</span>
											<span class="pull-right" id="additionalShowUserPrice">0.00</span>
										</p>
										<fmt:formatNumber groupingUsed="false" var="addtionalFmtUserPrice" type="number" minFractionDigits="2" maxFractionDigits="2" value="${addtionalUserPrice}" />
										<p class="pack-margin-neg-12">US$ ${addtionalFmtUserPrice} per user</p>
									</div>
									<div class="pack-details">
										<p>
											<span class="">Subscription Method 
										</p>
										<%-- <form> --%>
										<c:forEach items="${plan.planPeriodList}" var="period" varStatus="status">
											<div class="radio">
												<label>
													<!-- <input type="radio" name="yearly"> -->
													<c:if test="${period.planDuration == '1'}">
														<input type="radio" name="periodId" value="${period.id}" id="periodId" data-validation="required" checked="checked" />
												Monthly
												</c:if>
													<c:if test="${period.planDuration != '1'}">
														<input type="radio" name="periodId" value="${period.id}" id="periodId" data-validation="required" />
												${period.planDuration} Months &nbsp; &nbsp; ${period.planDiscount}% Discount
												</c:if>
												</label>
											</div>
										</c:forEach>
										<%-- </form> --%>
									</div>
									<div class="pack-details">
										<form class="form-group">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<div class="form-inline">
												<label class="margin-right-20">Promo Code</label>
												<!-- <input type="text" name="promocode" class="form-control"> -->
												<input type="text" class="form-control" id="promoCodeUser" name="promoCode">
												<span id="promoErrorUser"></span>
											</div>
										</form>
									</div>
									<h4>TOTAL FEE</h4>
									<div class="pack-details">
										<p>
											<span class="left-span">
												Starter Pack x &nbsp
												<span id="planDuration">1</span>
												&nbsp Month
											</span>
											<span class="pull-right" id="basePrice">
												<input type="hidden" name="basePrice" value="${plan.basePrice}">
												${basePrice}
											</span>
										</p>
									</div>
									<div class="pack-details">
										<p>
											<span class="left-span">Additional Users</span>
											<span class="pull-right" id="addtionalUserPrice">
												<input type="hidden" name="addtionalUserPrice" value="0">
												0.00
											</span>
										</p>
									</div>
									<div class="pack-details">
										<p>
											<span class="left-span">Subscription Discount</span>
											<span class="pull-right" id="subscriptionDiscountPrice">
												<input type="hidden" name="subscriptionDiscountPrice" value="0">
												0.00
											</span>
										</p>
									</div>
									<div class="pack-details">
										<p>
											<span class="left-span">Promotional Code</span>
											<span class="pull-right" id="promoDiscountPrice">
												<input type="hidden" name="promoDiscountPrice" value="0">
												0.00
											</span>
										</p>
									</div>
									<h4 class="border-btm"></h4>
									<h4 class="pull-right" id="totalPrice">
										US$
										<input type="hidden" name="totalPrice" value="${plan.basePrice}">
										${basePrice}
									</h4>
								</div>
							</form:form>
						</div>
					</c:if>
					<c:if test="${plan.planType == 'PER_EVENT'}">
						<div id="ebp" class="tab-pane fade">
							<form:form commandName="buyer" id="idTrailEventBasedForm" method="post" action="${pageContext.request.contextPath}/buyerSubscription/doInitiateTrialPayment">
								<form:hidden path="companyName" />
								<form:hidden path="companyRegistrationNumber" />
								<form:hidden path="registrationOfCountry" />
								<form:hidden path="fullName" />
								<form:hidden path="loginEmail" />
								<form:hidden path="communicationEmail" />
								<form:hidden path="companyContactNumber" />
								<input type="hidden" name="promoCodeId" id="promoCodeEventId">
								<input type="hidden" name="rangeId" id="rangeEventId">
								<input type="hidden" name="planId" id="planId" value="${plan.id}">
								<input type="hidden" name="countryId" id="countryId" value="${buyer.registrationOfCountry.id}">
								<input type="hidden" id="eventPlanId" value="${plan.id}">

								<c:forEach items="${plan.rangeList}" var="range" varStatus="index" begin="0" end="0">
									<c:set var="endEventRange" value="${range.rangeEnd}" />
									<c:set var="eventPrice" value="${range.price}" />
								</c:forEach>
								<fmt:formatNumber groupingUsed="false" type="number" var="eventFirstPrice" minFractionDigits="2" maxFractionDigits="2" value="${eventPrice}" />
								<div class="pack-amount-details pack-padding">
									<h4 class="border-btm">EVENT BASED</h4>
									<div class="pack-details">
										<p>
											<span class="">Event Pack</span>
											<span class="pull-right">${eventFirstPrice}</span>
										</p>
									</div>
									<div class="pack-details">
										<p>
											<span class="form-inline">
												Number of Events
												<!-- <input type="text" name="adduser" class="form-control"> -->
												<input type="text" class="form-control" name="numberUserEvent" id="additionalEvents" data-validation="length number" value="1" data-validation-allowing="range[1;${endEventRange}]" data-validation-length="1-3">
											</span>
											<span class="pull-right" id="eventShowPrice">${eventFirstPrice}</span>
										</p>
										<fmt:formatNumber groupingUsed="false" var="eventFormatPrice" type="number" minFractionDigits="0" maxFractionDigits="0" value="${eventPrice}" />
										<p class="pack-margin-neg-12">US$ ${eventFormatPrice} per event</p>
									</div>
									<div class="pack-details">
										<form class="form-group">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<div class="form-inline">
												<label class="margin-right-20">Promo Code</label>
												<!-- <input type="text" name="promocode" class="form-control"> -->
												<input type="text" class="form-control" id="promoCodeEvent" name="promoCode">
												<span id="promoErrorEvent"></span>
											</div>
										</form>
									</div>
									<h4>TOTAL FEE</h4>
									<div class="pack-details">
										<p>
											<span class="left-span">
												Event Pack x &nbsp;
												<span class="left-span" id="selectedNoEvent">1</span>
											</span>
											<span class="pull-right" id="eventPrice">
											<input type="hidden" name="eventPrice" value="${eventFirstPrice}">
											${eventFirstPrice}</span>
										</p>
									</div>
									<!-- <div class="pack-details">
										<p>
											<span class="left-span">Additional Events</span>
											<span class="pull-right">0.00</span>
										</p>
									</div> -->
									<div class="pack-details">
										<p>
											<span class="left-span">Promotional Code</span>
											<span class="pull-right" id="promoEventDiscountPrice">
												<input type="hidden" name="promoDiscountPrice" value="0">
												0.00
											</span>
										</p>
									</div>
									<h4 class="border-btm"></h4>
									<h4 class="pull-right" id="totalEventPrice">
										US$
										<input type="hidden" name="totalPrice" value="${eventFirstPrice}">
										${eventFirstPrice}
									</h4>
								</div>
							</form:form>
						</div>
					</c:if>
				</c:forEach>
			</div>
		</div>
		<div class="col-sm-3 col-md-3 col-lg-3 col-xs-0"></div>
	</div>
	<div class="row buyer-purchase-details trans-bg">
		<div class="col-sm-3 col-md-3 col-lg-3 col-xs-0"></div>
		<div class="col-sm-6 col-md-6 col-lg-6 col-xs-0">
			<!--   <div class="form-group">
          <div id="g-recaptcha" data-sitekey="6LdG1iUTAAAAAJB9POQXAHttIeHra0NuqA1NwzBg">
            <div style="width: 304px; height: 78px;">
              <div>
                <iframe src="https://www.google.com/recaptcha/api2/anchor?ar=1&amp;k=6LezmB8TAAAAAFfN-D8nEnnuGpPohFZn4JKUbdNg&amp;co=aHR0cHM6Ly9hcHAucHJvY3VyZWhlcmUuY29tOjQ0Mw..&amp;hl=en&amp;v=v1526338122299&amp;theme=light&amp;size=normal&amp;cb=r6ek5xxe09ir"
                  width="304" height="78" role="presentation" frameborder="0" scrolling="no" sandbox="allow-forms allow-popups allow-same-origin allow-scripts allow-top-navigation allow-modals allow-popups-to-escape-sandbox"></iframe>
              </div>
              <textarea id="g-recaptcha-response" name="g-recaptcha-response" class="g-recaptcha-response" style="width: 250px; height: 40px; border: 1px solid #c1c1c1; margin: 10px 25px; padding: 0px; resize: none;  display: none; "></textarea>
            </div>
          </div>
          <input id="recaptchaResponse" name="recaptchaResponse" type="hidden" value="">


        </div> -->
			<div>
				<p class="ts-pera">
					*By proceeding, you agree to our
					<span>
						<a target="_blank" href="<c:url value="/resources/termsandcondition.pdf"/>">terms & conditions</a>
					</span>
					<div ><p><b>Note:</b>Don't worry! We will always ask permission before we charge you.</p></div>
				</p>
				<div class="row">
					<div class="col-md-6">
						<span class="rfr_field proceedPayment" id="idButtonHolderUser"></span>
						<span class="rfr_field proceedPayment" id="idButtonHolderEvent" style="display: none"></span>
					</div>
					<div class="col-md-6">
						<button class="btn btn-default btn-padding border-blue skipPayment">Skip for now</button>
					</div>
				</div>
				<!-- 				<button type="submit" class="btn btn-primary payment-btn-checkout margin-right-3 proceedPayment">Proceed To Payment</button>
 -->
 
			</div>
			<div>
				<a href="#">
					<img src="<c:url value="/resources/images/public/Visa.png"/>" alt="Visa-badges">
				</a>
				<a href="#">
					<img src="<c:url value="/resources/images/public/mastercard-badge.png"/>" alt="Mastercard-badge">
				</a>
				<a href="#">
					<img src="<c:url value="/resources/images/public/american-express.png"/>" alt="American-Express">
				</a>
				<a href="#">
					<img src="<c:url value="/resources/images/public/discover.png"/>" alt="visa-badge">
				</a>
				<br>
				<!-- <a href="#">
					<img src="<c:url value="/resources/images/public/paypal.png"/>" alt="Paypal-badge">
				</a> -->
				<a href="#">
					<img src="<c:url value="/resources/images/public/McAfee.png"/>" alt="McAfee-badge">
				</a>
			</div>
		</div>
		<div class="col-sm-4 col-md-4 col-lg-4 col-xs-0"></div>
	</div>
</div>
<%-- </form:form> --%>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script src="//www.paypalobjects.com/api/checkout.js" async></script>
<script type="text/javascript">
	$.validate({ lang : 'en', modules : 'date, security' });

	window.paypalCheckoutReady = function() {
		paypal.checkout.setup('${merchantId}', { environment : '${paypalEnvironment}', container : 'idTrailUserBasedForm', condition : function() {
			return $('#idTrailUserBasedForm').isValid();
		},
		//button: 'placeOrderBtn'
		buttons : [ { container : 'idButtonHolderUser', type : 'checkout', color : 'blue', size : 'medium', shape : 'rect' } ] });

		 paypal.checkout.setup('${merchantId}', {
			environment : '${paypalEnvironment}',
			container : 'idTrailEventBasedForm',
			condition : function() {
				return $('#idTrailEventBasedForm').isValid();
			},
			//button: 'placeOrderBtn'
			buttons : [ {
				container : 'idButtonHolderEvent',
				type : 'checkout',
				color : 'blue',
				size : 'medium',
				shape : 'rect'
			} ]
		});

	};
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/freeTrialPlan.js"/>"></script>