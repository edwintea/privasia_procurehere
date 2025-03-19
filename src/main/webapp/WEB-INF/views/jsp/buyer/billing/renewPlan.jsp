<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/saas.css"/>">
<div id="page-content">
	<div class="container">
		<div class="registraion_renew_wrap">
			<div class="reg_inner">
				<div class="reg_heading">
					<spring:message code="renew.plan.enter.details"/> <br />[${subscription.plan.planName.concat(' - ').concat(subscription.plan.shortDescription)}]
				</div>
				<div class="reg_form_box">
					<div class="rf_inner">
						<form:form class="form-horizontal" commandName="subscription" id="idSubscribeForm" action='${pageContext.request.contextPath}/buyer/billing/renew/${buyer.buyerPackage.plan.id}' method="post">
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.company.name"/></div>
								<div class="rfr_field col-md-12">
									<spring:message code="company.name.placeholder" var="companyname"/>
									<form:input type="text" id="idCompanyName" cssClass="form-control" readonly="true" placeholder="${companyname}" data-validation="required length alphanumeric " data-validation-allowing="-_ &." data-validation-length="4-124"
										path="buyer.companyName" />
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.current.validity"/></div>
								<div class="rfr_field">
									<c:if test="${buyer.buyerPackage != null}">
										<jsp:useBean id="now" class="java.util.Date" />
										<fmt:formatDate var="fromDate" value="${buyer.buyerPackage.startDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										<fmt:formatDate var="toDate" value="${buyer.buyerPackage.endDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
										<div class="col-md-9">
											<input type="text" class="form-control" value="${fromDate.concat(' to ').concat(toDate) }" readonly="true" />
										</div>
										<div class="col-md-3">
											<c:choose>
												<c:when test="${buyer.buyerPackage != null and buyer.buyerPackage.startDate != null and (now lt buyer.buyerPackage.startDate)}">
													<span style="color: red;"> (<spring:message code="account.overview.status.expired"/>) </span>
												</c:when>
												<c:when test="${buyer.buyerPackage != null and buyer.buyerPackage.endDate != null and (now gt buyer.buyerPackage.startDate and now lt buyer.buyerPackage.endDate)}">
													<span style="color: green;"> (<spring:message code="account.overview.status.active"/>) </span>
												</c:when>
												<c:otherwise>
													<span style="color: blue;"> (<spring:message code="account.overview.status.notactive"/>) </span>
												</c:otherwise>
											</c:choose>
										</div>
									</c:if>
									<c:if test="${buyer.buyerPackage == null }">
										<div class="col-md-12"><spring:message code="application.not.applicable2"/></div>
									</c:if>

								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.next.start.date"/></div>
								<div class="rfr_field col-md-12">
									<fmt:formatDate var="endDate" value="${buyer.buyerPackage.endDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
									<input type="text" class="form-control" value="${endDate}" readonly="true" />
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="renew.plan.price"/></div>
								<div class="rfr_field col-md-12">
									<input type="text" class="form-control" value="${subscription.plan.currency.currencyCode} ${subscription.plan.price}" readonly="true" />
									<form:hidden id="idPlanPrice" path="plan.price" />
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="label.rftbq.th.quantity" /></div>
								<div class="rfr_field col-md-12">
									<spring:message code="period.unit.placeholder" var="periodunit"/>
									<form:input type="text" cssClass="form-control" placeholder="${periodunit}" data-validation="required number" data-validation-error-msg-number="Range allowed 1 to 9999" data-validation-allowing="range[1;9999]" path="planQuantity" />
									<span>(${subscription.plan.periodUnit}S)</span>
								</div>
							</div>
							<c:if test="${subscription.plan.chargeModel == 'PER_UNIT'}">
								<div class="rf_row">
									<div class="rfr_text"><spring:message code="renew.plan.credit.quantity"/></div>
									<div class="rfr_field col-md-12">
										<spring:message code="event.limit.placeholder" var="eventlimit"/>
										<form:input type="text" id="idCreditQuantity" cssClass="form-control" placeholder="${eventlimit}" data-validation="required number" data-validation-error-msg-number="Range allowed 1 to ${subscription.plan.eventLimit} for the chosen plan"
											data-validation-allowing="range[1;${subscription.plan.eventLimit}]" path="eventLimit" />
										<span>(PER ${subscription.plan.periodUnit})</span>
									</div>
								</div>
							</c:if>
							<div class="rf_row">
								<div class="rfr_text"><spring:message code="eventawards.total.price"/></div>
								<div class="rfr_field col-md-12" style="color: #0095d5; font-weight: bold;">
									${subscription.plan.currency.currencyCode}
									<span id="idTotalPrice">${subscription.plan.price}</span>
								</div>
							</div>
							<div class="rf_row">
								<div class="rfr_text">&nbsp;</div>
								<div class="rfr_field col-md-12" id="idButtonHolder">
									<!-- input type='image' name='submit' src='https://www.paypalobjects.com/webstatic/en_US/i/btn/png/btn_subscribe_cc_147x47.png' border='0' align='top' alt='Check out with PayPal' / -->
									<!-- button class="frm_bttn1 hvr-pop sub_frm" onclick="window.location.href='confirm.html'">GO PREMIUM</button  -->
								</div>
								<div class="rfr_text">&nbsp;</div>
								<div class="rfr_field col-md-12">
									<!-- <img src="https://www.paypalobjects.com/webstatic/en_US/i/buttons/cc-badges-ppppcmcvdam.png" alt="Credit Card Badges"> -->
								</div>
							</div>
						</form:form>
					</div>
				</div>
				<div class="clear"></div>
			</div>
		</div>
	</div>
</div>

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
