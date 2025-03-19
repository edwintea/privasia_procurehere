<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<div id="page-content-wrapper">
	<div class="lightgray_bg_block"></div>
	<div class="gray_bg_block"></div>
	<div class="equal_2w_col signupCol2wCol">
		<div class="col_50 first_blc signupCol50">
			<jsp:include page="testimonial.jsp"></jsp:include>
		</div>
		<div class="col_50 signupCol50">
			<div class="supplyer_reg_form">
				<form:form action="${pageContext.request.contextPath}/suppliersubscription/payment/${plan.id}" id="idSubscribeForm" method="post" modelAttribute="supplier" autocomplete="off">
					<header class="form_header" style="height: 40px;">
						<h1 style="font-size: 32px; margin-top: 20px;">Enter Details to purchase [${plan.shortDescription}]</h1>
					</header>
					<c:if test="${not empty errors}">
						<div class="alert alert-danger" id="idGlobalError">
							<div class="bg-red alert-icon">
								<i class="glyph-icon icon-times"></i>
							</div>
							<div class="alert-content">
								<h4 class="alert-title">Error</h4>
								<p id="idGlobalErrorMessage">
									<c:forEach var="error" items="${errors}">
													${error}<br />
									</c:forEach>
								</p>
							</div>
						</div>
					</c:if>
					<div class="form_block blc_left">
						<div class="form-group">
							<label for="idCompanyName">Company Name</label>
							<form:input path="companyName" cssClass="form-control" id="idCompanyName" placeholder="Once submitted, cannot be changed" data-validation="required length alphanumeric"
								data-validation-allowing="-_ &.()" data-validation-length="4-124" />
						</div>
						<div class="form-group">
							<label for="idRegCountry">Country of Registration</label>
							<form:select path="registrationOfCountry" id="idRegCountry" cssClass="chosen-select form-control" data-validation="required crn_number">
								<form:option value="">Select Country of Registration</form:option>
								<form:options items="${registeredCountry}" itemValue="id" itemLabel="countryName" />
							</form:select>
						</div>
						<div class="form-group">
							<label for="idCompRegNum"><spring:message code="supplier.registration.company.number" /></label>
							<form:input path="companyRegistrationNumber" cssClass="form-control" id="idCompRegNum" placeholder="Once submitted, cannot be changed" data-validation="required length alphanumeric crn_number"
								data-validation-allowing="-_ " data-validation-length="2-124" />
						</div>
						<div class="form-group">
							<label for="idFullName">Full Name</label>
							<form:input path="fullName" cssClass="form-control" id="idFullName" placeholder="Once submitted, cannot be changed" data-validation="required length alphanumeric"
								data-validation-allowing="-_ ." data-validation-length="2-128" />
						</div>
						<div class="form-group">
							<label for="idDesignation"><spring:message code="supplier.designation" /></label>
							<form:input path="designation" cssClass="form-control" id="idDesignation" placeholder="Once submitted, cannot be changed" data-validation="required length alphanumeric"
								data-validation-allowing="- " data-validation-length="2-128" />
						</div>
					</div>
					<div class="form_block blc_right">
						<div class="form-group">
							<label for="idAdminMobileNo">Admin Mobile Number</label>
							<div class="input_percent">
								<form:input path="mobileNumber" cssClass="form-control" id="idAdminMobileNo" placeholder="Country code and number e.g. +6017666666" data-validation="required length number"
									data-validation-ignore="+ " data-validation-length="6-14" />
							</div>
							<div class="clear"></div>
						</div>
						<div class="form-group">
							<label for="idCompanyContactNumber">Company Contact Number</label>
							<form:input path="companyContactNumber" cssClass="form-control" id="idCompanyContactNumber" placeholder="+60345678906" data-validation="required length number" data-validation-ignore="+ "
								data-validation-length="6-14" />
						</div>
						<div class="form-group">
							<label for="idCommunicationEmail">Communication Email </label>
							<form:input path="communicationEmail" class="form-control" id="idCommunicationEmail" placeholder="Email will be sent to this address" data-validation="required length email"
								data-validation-length="max124" />
						</div>
						<div class="form-group">
							<label for="idLoginEmail">Login Email </label>
							<form:input path="loginEmail" cssClass="form-control" id="idLoginEmail" placeholder="This email cannot be changed later" data-validation="required length email login_email"
								data-validation-length="max124" />
						</div>
						<div class="form-group">
							<label for="exampleInputPass">Password</label>
							<form:password path="password" cssClass="form-control" id="exampleInputPass1" placeholder="Minimum one capital character & eight letters" data-validation="custom"
								data-validation-regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$@$!%*?&])[A-Za-z\d$@$!%*?&]{8,15}$"
								data-validation-error-msg="Password should contain at least 8 characters, one digit, one small case, one capital case letter and a special character." />
						</div>
					</div>
					<div class="bot_textarea">
						<div class="form-group">
							<form:textarea cssClass="form-control" path="remarks" rows="4" data-validation="length" data-validation-length="max1000" placeholder="Please write the remarks for admin here"></form:textarea>
						</div>
					</div>
					<div class="form-group">
						<div id="g-recaptcha" data-sitekey="6LdG1iUTAAAAAJB9POQXAHttIeHra0NuqA1NwzBg"></div>
						<form:hidden path="recaptchaResponse" />
						<script type="text/javascript">
							var onloadCallback = function() {
								grecaptcha.render('g-recaptcha', {
									'sitekey' : '<c:out value="${recaptchaSiteKey}" />',
									'callback' : function(response) {
										document.getElementById('recaptchaResponse').value = response;
									},
									'theme' : 'light'
								});
							}
						</script>
						<script src="https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit" async defer></script>
					</div>
					<div class="btn_pane" id="idButtonHolder">
						<c:if test="${plan.price > 0}">
							<div class="rfr_field">
								<!-- <img src="https://www.paypalobjects.com/webstatic/en_US/i/buttons/cc-badges-ppppcmcvdam.png" alt="Credit Card Badges"> -->
							</div>
						</c:if>
						<c:if test="${plan.price == 0}">
							<form:button id="Verification_btn" type="submit" class="btn btn-block btn-primary">Submit for Verification</form:button>
						</c:if>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
</div>
</div>
<!-- WIDGETS -->
<style>
.chosen-container.chosen-container-single {
	background: #fff;
	color: #7a7a7a;
}

.chosen-container.chosen-container-single a {
	color: #7a7a7a;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js"/>"></script>
<!-- Content box -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<!-- BX SLIDER --->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/jquery.bxslider/jquery.bxslider.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/jquery.bxslider/jquery.bxslider.min.js"/>"></script>
<script>
	$('document').ready(function() {
		var minhGt = $(window).height() - 75;
		$('.signupCol50').css('min-height', minhGt);
		$('.carousel-reg').bxSlider({
			pager : false,
			useCSS : true
		});

	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.formUtils.addValidator({
		name : 'crn_number',
		validatorFunction : function(value, $el, config, language, $form) {
			var countryID = $('#idRegCountry').val();
			var crnNum = $('#idCompRegNum').val();
			var response = true;

			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");

			$.ajax({
				url : getContextPath() + "/checkRegistrationNumber",
				data : {
					countryID : countryID,
					crnNum : crnNum
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
		errorMessage : 'Company Registration Number Already registered in the system',
		errorMessageKey : 'badCrnNumber'
	});

	$.formUtils.addValidator({
		name : 'company_name',
		validatorFunction : function(value, $el, config, language, $form) {
			var countryID = $('#idRegCountry').val();
			var companyName = $('#idCompanyName').val();
			var response = true;
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url : getContextPath() + "/checkCompanyNameExis",
				data : {
					countryID : countryID,
					companyName : companyName
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
		errorMessage : 'Company Name Already registered in the system',
		errorMessageKey : 'badCompanyName'
	});

	$.formUtils.addValidator({
		name : 'login_email',
		validatorFunction : function(value, $el, config, language, $form) {
			var loginEmailId = $('#idLoginEmail').val();
			var response = true;
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			//alert(loginId);
			$.ajax({
				url : getContextPath() + "/checkLoginEmail",
				data : {
					loginEmailId : loginEmailId
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
		errorMessage : 'Login Email Already registered in the system',
		errorMessageKey : 'badLoginEmail'
	});

	$.validate({
		lang : 'en'
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
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$('#idAdminMobileNo').mask('+00 00000000000', {
			placeholder : "+60 122735465"
		});
		$('#idCompanyContactNumber').mask('+00 00000000000', {
			placeholder : "+60 322761533"
		});
	});
</script>
<!-- <script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js"/>"></script> 
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/parsley/parsley.js"/>"></script> -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>
<script src="//www.paypalobjects.com/api/checkout.js" async></script>
