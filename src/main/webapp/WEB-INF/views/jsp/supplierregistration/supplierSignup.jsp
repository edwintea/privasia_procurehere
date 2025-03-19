<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<script>
	window.scatrack('enableFormTracking');
</script>
 
<div id="page-content-wrapper">
	<div class="lightgray_bg_block"></div>
	<div class="gray_bg_block"></div>
	<div class="equal_2w_col signupCol2wCol">
		<div class="col_50 first_blc signupCol50">
			<jsp:include page="testimonial.jsp"></jsp:include>
		</div>
		<div class="col_50 signupCol50">

			<div class="supplyer_reg_form">
				<c:url var="supplierSignup" value="supplierSignup" />
				<form:form action="${supplierSignup}" id="demo-form" method="post" modelAttribute="supplier" autocomplete="off">

					<header class="form_header" style="height: 50px">
						<h1><spring:message code="supplier.sighnup.label"/></h1>
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
							<label for="idCompanyName"><spring:message code="buyer.profilesetup.companyname"/></label>
							<spring:message code="supplier.once.submitted.placehholder"  var="placecompany"/>
							<form:input path="companyName" cssClass="form-control" id="idCompanyName" placeholder="${placecompany}" data-validation="required length alphanumeric" data-validation-allowing="-_ &.()" data-validation-length="4-124" />
						</div>

						<div class="form-group">
							<label for="idRegCountry"><spring:message code="buyercreation.registration"/></label>
							<form:select path="registrationOfCountry" id="idRegCountry" cssClass="chosen-select form-control" data-validation="required crn_number">
								<form:option value=""><spring:message code="buyplan.country.registration"/></form:option>
								<form:options items="${registeredCountry}" itemValue="id" itemLabel="countryName" />
							</form:select>
						</div>

						<div class="form-group">
							<label for="idCompRegNum"><spring:message code="supplier.registration.company.number"/></label>
							<form:input path="companyRegistrationNumber" cssClass="form-control" id="idCompRegNum" placeholder="${placecompany}" data-validation="required length alphanumeric crn_number" data-validation-allowing="-_ " data-validation-length="2-124" />
						</div>

						<div class="form-group">
							<label for="idFullName"><spring:message code="buyercreation.name"/></label>
							<form:input path="fullName" cssClass="form-control" id="idFullName" placeholder="${placecompany}" data-validation="required length alphanumeric" data-validation-allowing="-_ ." data-validation-length="2-128" />
						</div>

						<div class="form-group">
							<label for="idDesignation"><spring:message code="supplier.designation" /></label>
							<form:input path="designation" cssClass="form-control" id="idDesignation" placeholder="${placecompany}" data-validation="required length alphanumeric" data-validation-allowing="- " data-validation-length="2-128" />
						</div>


					</div>

					<div class="form_block blc_right">
						<div class="form-group">
							<label for="idAdminMobileNo"><spring:message code="buyer.profilesetup.mobileno"/></label>
							<div class="input_percent">
								<spring:message code="supplier.country.code.number.placehholder" var="code"/>
								<form:input path="mobileNumber" cssClass="form-control" id="idAdminMobileNo" placeholder="${code}" data-validation="required length number" data-validation-ignore="+ " data-validation-length="6-14" />
							</div>
							<div class="clear"></div>
						</div>
						<div class="form-group">
							<label for="idCompanyContactNumber"><spring:message code="buyer.profilesetup.contactno"/></label>
							<spring:message code="supplier.company.contact.placehholder"  var="contact"/>
							<form:input path="companyContactNumber" cssClass="form-control" id="idCompanyContactNumber" placeholder="${contact}" data-validation="required length number" data-validation-ignore="+ " data-validation-length="6-14" />
						</div>


						<div class="form-group">
							<label for="idCommunicationEmail"><spring:message code="buyer.profilesetup.commemail"/> </label>
							<spring:message code="supplier.email.placeholder"  var="email"/>
							<form:input path="communicationEmail" class="form-control" id="idCommunicationEmail" placeholder="${email}" data-validation="required length email" data-validation-length="max124" />
						</div>

						<div class="form-group">
							<label for="idLoginEmail"><spring:message code="buyer.profilesetup.loginmail"/> </label>
							<spring:message code="supplier.email.changed.placeholder"  var="email2"/>
							<form:input path="loginEmail" cssClass="form-control" id="idLoginEmail" placeholder="${email2}" data-validation="required length email login_email" data-validation-length="max124" />
						</div>

						<div class="form-group">
							<label for="exampleInputPass"><spring:message code="buyer.profilesetup.Password"/></label>
							<spring:message code="password.placeholder"  var="pwd"/>
							<form:password path="password" cssClass="form-control" id="exampleInputPass1" placeholder="${pwd}" data-validation="custom" data-validation-regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$@$!%*?&])[A-Za-z\d$@$!%*?&]{8,15}$"
								data-validation-error-msg="Password should contain at least 8 characters, one digit, one small case, one capital case letter and a special character.Special characters allowed: ! @ $ % & * ?" />
						</div>
					</div>
					<div class="bot_textarea">
						<div class="form-group">
							<spring:message code="supplier.remark.placeholder"  var="remark"/>
							<form:textarea cssClass="form-control" path="remarks" rows="4" data-validation="length" data-validation-length="max1000" placeholder="${remark}"></form:textarea>
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

					<div class="btn_pane">
						<form:button id="Verification_btn" type="submit" class="btn btn-block btn-primary"><spring:message code="supplier.sighnup.submit.verification"/></form:button>
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
#footer {
	display: none !important;
}

.pset_footer {
	/* position: absolute;
               bottom: 0;    */
	display: none !important;
}

.chosen-container.chosen-container-single {
	background: #fff;
	color: #7a7a7a;
}

.chosen-container.chosen-container-single a {
	color: #7a7a7a;
}

@media screen and (max-width:760px) {
	#sb-site #header-logo {
		display: block;
	}
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

