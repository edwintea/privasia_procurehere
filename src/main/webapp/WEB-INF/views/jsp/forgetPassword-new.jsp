<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" isELIgnored="false"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<fmt:setBundle basename="application" var="message" scope="application" />
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-core.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-widget.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-mouse.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-position.js"/>"></script>

<div class="container margin-bottom-5">
	<div class="buyer-check-section">
		<p class="font-30">Forgotten your password?</p>
		<p class="reset-link-text font-20">
			Just confirm your email and we'll send you a link<br> to reset your password
		</p>
		<c:url var="resetPasswordUrl" value="recoverPassword" />
		 <form action="${resetPasswordUrl}" id="forget-form" method="post" class="form-width-35">
		 <c:if test="${not empty error}">
				<div class="text text-danger margin-btm-2">
					<strong>Error!</strong> ${error}
				</div>
			</c:if>
			<div class="form-group">
				<label for="email">Email</label>
				<input type="email" class="form-control" name="loginEmail" class="form-control" id="idForgotPasswordEmail" data-validation="email" data-validation-length="min6" placeholder="Enter your login email">
			</div>

			<div class="form-group">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="form-group">
					<div id="g-recaptcha" data-sitekey="6LdG1iUTAAAAAJB9POQXAHttIeHra0NuqA1NwzBg"></div>
					<input type="hidden" id="recaptchaResponse" name="recaptchaResponse">
					<script type="text/javascript">
						var onloadCallback = function() {
							grecaptcha
									.render(
											'g-recaptcha',
											{
												'sitekey' : '<fmt:message key="recaptcha.site-key" bundle="${ message }" />',
												'callback' : function(response) {
													document
															.getElementById('recaptchaResponse').value = response;
												},
												'theme' : 'light'
											});
						}
					</script>
					<script src="https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit" async="" defer=""></script>
				</div>
			</div>
			<button type="submit" class="btn btn-primary" id="rec_passowrd">Submit</button>
			
		</form>  

	</div>
</div>
<!-- <div>
	<a href="#">
		<img src="./images/chat-icon.png" alt="chat-icon" class="chat-icon">
	</a>
</div> -->


<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
</body>