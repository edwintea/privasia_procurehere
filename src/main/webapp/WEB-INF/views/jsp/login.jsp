<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" isELIgnored="false"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<fmt:setBundle basename="application" var="message" scope="application" />
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-core.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-widget.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-mouse.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-position.js"/>"></script>


<link rel="shortcut icon" href="<c:url value="/resources/assets/images/icons/favicon.png"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/login.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/bootstrap/css/bootstrap.css"/>">

<!-- Start of procurehere Zendesk Widget script -->
<script>
	/*<![CDATA[*/window.zEmbed
			|| function(e, t) {
				var n, o, d, i, s, a = [], r = document.createElement("iframe");
				window.zEmbed = function() {
					a.push(arguments)
				}, window.zE = window.zE || window.zEmbed, r.src = "javascript:false", r.title = "", r.role = "presentation", (r.frameElement || r).style.cssText = "display: none", d = document.getElementsByTagName("script"), d = d[d.length - 1],
						d.parentNode.insertBefore(r, d), i = r.contentWindow, s = i.document;
				try {
					o = s
				} catch (e) {
					n = document.domain, r.src = 'javascript:var d=document.open();d.domain="' + n + '";void(0);', o = s
				}
				o.open()._l = function() {
					var e = this.createElement("script");
					n && (this.domain = n), e.id = "js-iframe-async", e.src = "https://assets.zendesk.com/embeddable_framework/main.js", this.t = +new Date, this.zendeskHost = "procurehere.zendesk.com", this.zEQueue = a, this.body.appendChild(e)
				}, o.write('<body onload="document._l();">'), o.close()
			}();
	/*]]>*/
</script>
<!-- End of procurehere Zendesk Widget script -->
<spring:message var="loginDesk" code="application.login.page" />
<spring:message var="forgotPasswordDesk" code="application.forgot.password" />
<script>
window.scatrack('enableFormTracking');
</script>

<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${loginDesk}, ${forgotPasswordDesk}] });
});
</script>

<script type="text/javascript">
	var TxtType = function(el, toRotate, period) {
		this.toRotate = toRotate;
		this.el = el;
		this.loopNum = 0;
		this.period = parseInt(period, 10) || 2000;
		this.txt = '';
		this.tick();
		this.isDeleting = false;
	};

	TxtType.prototype.tick = function() {
		var i = this.loopNum % this.toRotate.length;
		var fullTxt = this.toRotate[i];

		if (this.isDeleting) {
			this.txt = fullTxt.substring(0, this.txt.length - 1);
		} else {
			this.txt = fullTxt.substring(0, this.txt.length + 1);
		}

		this.el.innerHTML = '<span class="wrap">' + this.txt + '</span>';

		var that = this;
		var delta = 200 - Math.random() * 100;

		if (this.isDeleting) {
			delta /= 2;
		}

		if (!this.isDeleting && this.txt === fullTxt) {
			delta = this.period;
			this.isDeleting = true;
		} else if (this.isDeleting && this.txt === '') {
			this.isDeleting = false;
			this.loopNum++;
			delta = 500;
		}

		setTimeout(function() {
			that.tick();
		}, delta);
	};

	window.onload = function() {
		var elements = document.getElementsByClassName('typewrite');
		for (var i = 0; i < elements.length; i++) {
			var toRotate = elements[i].getAttribute('data-type');
			var period = elements[i].getAttribute('data-period');
			if (toRotate) {
				new TxtType(elements[i], JSON.parse(toRotate), period);
			}
		}
		// INJECT CSS
		var css = document.createElement("style");
		css.type = "text/css";
		css.innerHTML = ".typewrite > .wrap { border-right: 0.08em solid #fff}";
		document.body.appendChild(css);
	};

	zE(function() {
		zE.logout();
	});
</script>

<body class="bg-com">

	<!--img src="assets/image-resources/image-procurehere/Login-bg1.jpg" class="login-img wow fadeIn" alt=""-->

	<div class="center-vertical saas-login">
		<div class="login_content_area">
			<div class="container-fluid">
				<div class=" row">
					<div class="col-xs-12">
						<div class="login_logo">
							<h1>
								<span class="sr-only">Procurehere</span>
								<img src="<c:url value="/resources/images/pro_logo.png"/>" alt="privasia" />
							</h1>
						</div>
					</div>

					<section class="leftloginPart col-xs-12 col-sm-6 col-md-8">
						<hgroup class="loging_quote">

							<div class="ty-ed">
								<h2>e-Sourcing and requisitioning made easy</h2>
							</div>
							<h4>Most Intuitive e-procurement system.</h4>
						</hgroup>

						<ul class="dot_list_login">
							<li>
								<div class="dot_list1">
									<img src="<c:url value="/resources/images/plain-circle.png"/>" alt="arrow">
								</div>
								<h4>Efficient</h4> <span class="subtext">Cut out the manual work</span>
							</li>
							<li>
								<div class="dot_list1">
									<img src="<c:url value="/resources/images/plain-circle.png"/>" alt="arrow">
								</div>
								<h4>Integrity</h4> <span class="subtext">Everything is tamper-proof and transparent</span>
							</li>
							<li>
								<div class="dot_list1">
									<img src="<c:url value="/resources/images/plain-circle.png"/>" alt="arrow">
								</div>
								<h4>Cost savings</h4> <span class="subtext">Auctions drive it down significantly</span>
							</li>

						</ul>

					</section>
					<div class=" col-xs-12 col-sm-6 col-md-4">
						<c:url var="loginUrl" value="/login" />
						<form action="${loginUrl}" id="login-validation" method="post" autocomplete="off">
							<div id="login-form" class="content-box">
								<div class="content-box-wrapper">
									<div class="form-group">
										<c:if test="${param.error != null}">
											<p class="error" style="color: #ff8000 !important;">
												${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} <br />
												<c:out value="${failedMessage}" />
											</p>
										</c:if>
										<c:if test="${param.logout != null}">
											<p class="error">You have been logged out successfully.</p>
										</c:if>
										<c:if test="${param.pchange != null}">
											<p>Your password has been changed successfully. You can now login with new Password.</p>
										</c:if>
										<c:if test="${param.reset != null}">
											<p>Password reset link has been emailed to your registered email address.</p>
										</c:if>
										<c:if test="${param.bsuccess != null}">
											<p id="p">You completed profile successfully. Please login to use the system.</p>
										</c:if>
									</div>
									<div class="form-group">
										<label class="sr-only" for="username">Email address:</label>
										<input type="text" class="form-control" name="username" id="username" autocomplete="off" data-validation="email" data-validation-length="min1" id="username" placeholder="ENTER EMAIL" />

									</div>
									<div class="form-group">
										<label class="sr-only" for="password">Password:</label>
										<input type="password" class="form-control" name="password" id="password" autocomplete="new-password" data-validation="required" data-validation-length="6-64" data-validation-error-msg="Password must be between 6-64 characters" placeholder="PASSWORD" />
									</div>
									<div class="row forgot_txt_row">
										<div class="col-xs-12">
											<a href="#" class="switch-button" id="goToForget" title="Recover password">Forgot Password</a>
										</div>
									</div>
								 	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="countryCode" value="MY" /> <input type="hidden" name="timeZone" value="Asia/Kuala_Lumpur" /> <input type="hidden" name="timeZoneGmt"
										value="GMT+8" /> 

									<div class="button-panel marg-top-20">
										<button type="submit" class="btn btn-block btn-blue btn-lg hvr-pop hvr-rectangle-out" id="loginSigninBtn">Sign in</button>

									</div>

								</div>
								<div class="col-xs-12 version_text" style="text-align: right;">
									Release Version: <font color="red"><fmt:message key="environment-key" bundle="${ message }" /></font>
									<fmt:message key="app.version" bundle="${ message }" />
								</div>
							</div>
						</form>

						<c:url var="resetPasswordUrl" value="admin/recoverPassword" />
						<form action="${resetPasswordUrl}" id="forget-form" method="post">
							<div id="login-forgot" class="content-box" style="display: none;">
								<div class="content-box-wrapper pad20A">
									<label class="verification-confirmation-msg" style="display: none"> Error</label>
									<div class="form-group">
										<label for="idForgotPasswordEmail" class="sr-only">Email address:</label>
										<input type="text" name="loginEmail" class="form-control" id="idForgotPasswordEmail" data-validation="email" data-validation-length="min6" placeholder="Enter your login email">
									</div>
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<div class="form-group">
										Login
										<div id="g-recaptcha" data-sitekey="6LdG1iUTAAAAAJB9POQXAHttIeHra0NuqA1NwzBg"></div>
										<input type="hidden" id="recaptchaResponse" name="recaptchaResponse">
										<script type="text/javascript">
											var onloadCallback = function() {
												grecaptcha.render('g-recaptcha', {
													'sitekey' : '<fmt:message key="recaptcha.site-key" bundle="${ message }" />',
													'callback' : function(response) {
														document.getElementById('recaptchaResponse').value = response;
													},
													'theme' : 'light'
												});
											}
										</script>
										<script src="https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit" async defer></script>
									</div>
									<div class="button-panel text-center">
										<button type="button" class="btn btn-block btn-blue btn-lg" id="rec_passowrd">Recover Password</button>
										<span class="or_text">Or</span>
										<h3 class="sign_text" id="backToLogin">
											<a href="#">Back to Login</a>
										</h3>
									</div>
								</div>
							</div>
						</form>

					</div>
				</div>
			</div>
		</div>
	</div>


	<!-- WIDGETS -->

	<div class="popupforgotPass">
		<p>Password reset link has been emailed to your registered email address.</p>
	</div>
	<div class="popupforgotInvalid">
		<p>No account is registered with the provided login email address.</p>
	</div>



</body>

<style>
.popupforgotPass {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	text-align: center;
	z-index: 999999;
	display: none;
}

#p {
	color: #0f0 !important;
	font-weight: bold;
	text-transform: none;
	font-size: 16px;
}

.popupforgotInvalid {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	text-align: center;
	z-index: 999999;
	display: none;
}

.popupforgotPass p {
	font-size: 20px;
	margin: 10% auto;
	background: rgba(255, 255, 255, 0.4);
	width: 390px;
	border-radius: 10px;
	padding: 19px;
	color: #99e0ff;
	position: relative;
}

.popupforgotInvalid p {
	font-size: 20px;
	margin: 10% auto;
	background: rgba(255, 255, 255, 0.4);
	width: 390px;
	border-radius: 10px;
	padding: 19px;
	color: #99e0ff;
	position: relative;
}

.has-error .help-block, .text-danger, .font-red, .parsley-required, p.error,
	.form-group p, .invalidEmailError {
	color: #99e0ff !important;
	font-weight: bold;
	text-transform: none;
	font-size: 16px;
}

#login-form .form-control, #login-form, #login-forgot .form-control {
	text-transform: none;
}
/* #goToForget{
	text-transform:uppercase;
} */
</style>
<!-- WIDGETS -->
<script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js"/>"></script>
<script>
	$(document).ready(function() {

		$('#goToForget').click(function(event) {
			event.preventDefault();
			$(".popupforgotInvalid").hide();
			$('.popupforgotPass').hide();
			jQuery('#login-form').hide();
			jQuery('#login-forgot').show();
		});

		$('#backToLogin').click(function(event) {
			event.preventDefault();
			jQuery('#login-forgot').hide();
			jQuery('#login-form').show();
		});

		$('#rec_passowrd').click(function(e) {
			e.preventDefault();
			if ($('#forget-form').isValid()) {
				doResetPassword(e);
			}
		});

		//Parsley.options.maxlength = 42;
		//var formInstance = $('form').parsley();
		//var field = $('#exampleInputEmail1').parsley();
		//field.options.required = 'Email id required';

	});
	
	$('#loginSigninBtn').on("click", function (e) {
        $("#loginSigninBtn").text("Please Wait");
        $('#loginSigninBtn').attr('disabled', 'disabled');
	});
	
	$('#idForgotPasswordEmail, #username').keyup(function() {
		$('.invalidEmailError').remove();
	});
	function doResetPassword(e) {
		$('.invalidEmailError').remove();
		$("#rec_passowrd").prop("disabled", true);
		e.preventDefault();
		$.ajax({
			type : "POST",
			url : "admin/recoverPassword",
			data : $('#forget-form').serialize(),
			beforeSend : function(xhr) {
				//$('#loading').show();
			},
			complete : function() {
				//$('#loading').hide();
				$("#rec_passowrd").prop("disabled", false);
			},
			success : function(data) {
				console.log(" SUCCESS : " + data);
				window.location.href = "${pageContext.request.contextPath}/login?reset=true";
				//$('#login-form').find('#username').before('<p class="invalidEmailError" style="padding-bottom:10px;">Password reset link has been emailed to your registered email address.</p>');
				//$('#backToLogin').click();
				//setTimeout(function(){
				//	$('#forget-form#idForgotPasswordEmail').find('input').val('');
				//	grecaptcha.reset();
				//},5000);
			},
			error : function(request, textStatus, errorThrown) {
				console.log(" Error : " + request.getResponseHeader('error'));
				var message = request.getResponseHeader('error');
				if (message != null && message != undefined   && message.trim().length > 0) {
					$('.popupforgotInvalid > p').html(request.getResponseHeader('error'));
					$('#idForgotPasswordEmail').after('<span class="invalidEmailError">' + message +'</span>');
				}
				$('#forget-form').find('input#idForgotPasswordEmail').val('');
				grecaptcha.reset();
			}
		});

	}
</script>
<!-- script type="text/javascript" src="<c:url value="/resources/assets/widgets/parsley/parsley.js"/>"/ -->
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		//$('#username').mask('+00 000000', {placeholder: "+"});
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<%--    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
 --%>
<script>
	$.validate({
		lang : 'en'
	//form:'#demo-form',
	//validateOnBlur : false, // disable validation when input looses focus
	//errorMessagePosition : 'top' ,// Instead of 'inline' which is default
	//scrollToTopOnError : false // Set this property to true on longer forms
	});
</script>
<script>
	/* 	function getTimeZone() {
	 var offset = new Date().getTimezoneOffset(), o = Math.abs(offset);
	 return (offset < 0 ? "+" : "-") + ("00" + Math.floor(o / 60)).slice(-2) + ":" + ("00" + (o % 60)).slice(-2);
	 }

	 $(document).ready(function() {
	 $.getJSON('http://freegeoip.net/json/', function(result) {
	 $('input[name="countryCode"]').val(result.country_code);
	 $('input[name="timeZone"]').val(result.time_zone);
	 });
	 $('input[name="timeZoneGmt"]').val(getTimeZone());
	 });
	 */
</script>
</body>
</html>
