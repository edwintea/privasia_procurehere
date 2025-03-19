<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" isELIgnored="false"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<fmt:setBundle basename="application" var="message" scope="application" />
<%-- <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script> --%>
<style>
	.ticker-container {
		background-color: white;
		color: black;
		padding: 10px;
		margin: 0 auto; /* Center the container horizontally */
		display: inline-block;  /* Adjust the width to control the size of the container */
		border: 2px solid #03AAF5;
		border-radius: 10px;
		text-align: center;
	}

	.parent-container {
		text-align: center; /* Center the inline-block container */
	}

	.ticker-container p {
		text-align: center;
		margin: 0; /* Remove top and bottom margins */
		font-size: 16px; /* Adjust the font size as needed */
		line-height: 1.5; /* Improve readability */
	}

	.ticker-container a[href^="mailto:"] {
		color: #3366CC;
	}

	.highlight {
		color: black;
		font-size: 130%;
		font-weight: bold;
	}
</style>

<div class="container">
	<div class="buyer-check-section">
		<c:if test="${param.bsuccess == null}">
			<p class="login-text font-27 margin-btm-2">
				Login for secure access to your <br> PROCUREHERE account
			</p>
		</c:if>
		<c:url var="loginUrl" value="/login" />
		<form class="form-width-35" action="${loginUrl}" id="login-validation" method="post" autocomplete="off">
			<%-- <div class="text text-success margin-btm-2" style="display: none">
				<strong>Success!</strong> This alert box could indicate a successful
				or positive action.
			</div>
			<div class="text text-info margin-btm-2" style="display: none">
				<strong>Info!</strong> This alert box could indicate a neutral
				informative change or action.
			</div>
			<div class="text text-warning margin-btm-2" style="display: none">
				<strong>Warning!</strong> This alert box could indicate a warning
				that might need attention.
			</div>
			<div class="text text-danger margin-btm-2">
				<strong>Error!</strong> This alert box could indicate a dangerous or
				potentially negative action.
			</div> --%>
			<c:if test="${param.error != null}">
				<div class="text text-danger margin-btm-2">
					<strong>Error!</strong> ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
				</div>
				<c:out value="${failedMessage}" />
			</c:if>
			<c:if test="${param.bsuccess != null}">
				<div class="text text-info " style="margin-top:40px;margin-bottom:30px;font-size:18px;" >
					<strong>Signup Successful!</strong><br/> Please login with your registered credentials.
				</div>
			</c:if>

			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<input type="hidden" name="countryCode" value="MY" />
			<input type="hidden" name="timeZone" value="Asia/Kuala_Lumpur" />
			<input type="hidden" name="timeZoneGmt" value="GMT+8" />
			<div class="form-group">
				<label for="username">Username</label>
				<input type="text" class="form-control" name="username" id="username" autocomplete="off" data-validation="email" data-validation-length="min1" id="username" placeholder="ENTER EMAIL">
			</div>
			<div class="form-group">
				<label for="password">Password</label>
				<input type="password" class="form-control" name="password" id="password" autocomplete="new-password" data-validation="required" data-validation-length="6-64" data-validation-error-msg="Password must be between 6-64 characters" placeholder="PASSWORD">
				<p class="forgot-password-link">
					<a href="${pageContext.request.contextPath}/admin/forgetPassword">Forgotten your password?</a>
				</p>
			</div>
			<div class="text-center">
				<button class="btn btn-primary margin-btm-2" type="submit" id="loginSigninBtn">Login</button>
			</div>
			<p class="release-text">
				Release Version: <font color="red"><fmt:message key="environment-key" bundle="${ message }" /></font>
				<fmt:message key="app.version" bundle="${ message }" />
			</p>
		</form>
	</div>
</div>
<div class="maintenance-message" style="display: none;">
	<div class="parent-container">
		<div class="ticker-container">
			<p><span class="highlight">Notice:</span> <span
					style="border: none; background: none; position: relative; top: -3px;">
            <img src="${pageContext.request.contextPath}/resources/assets/images/settings.png" alt="Logo"
				 style="width: 20px; height: 20px; vertical-align: middle;"></span>
				<span class="maintenance-notice">Scheduled Monthly Maintenance Window for PROCUREHERE | 19th October 2024 (Saturday) - 1:00AM till 5:00AM</span>
			</p>
		</div>
	</div>
</div>
<div class="container-fluid login-options">
	<div class="row">
		<div class="col-sm-1 col-md-1 col-lg-1 col-xs-0"></div>
		<div class="col-sm-5 col-md-5 col-lg-5 col-xs-12 redirect-here-section padding-left-5">
			<img src="<c:url value="/resources/images/public/user-icon.png"/>" alt="user-icon" class="img-responsive">
			<p>
				Not yet a member? Explore our simple<br>subcription packages today.
			</p><a href="<spring:message code="app.root.url" />/pricing">
			<button class="btn btn-default">Click Here</button>
		</a>
		</div>
		<div class="col-sm-5 col-md-5 col-lg-5 col-xs-12 redirect-here-section padding-right-5">
			<img src="<c:url value="/resources/images/public/supplier-sign.png"/>" alt="supplier-sign" class="img-responsive">
			<p>
				Are you a supplier looking to access<br>global procurement opportunities?
			</p>
			<a href="<spring:message code="app.root.url" />/supplier">
				<button class="btn btn-default">Click Here</button>
			</a>
		</div>
		<div class="col-sm-1 col-md-1 col-lg-1 col-xs-0"></div>
	</div>
</div>
<%-- <div>
	<a href="#">
		<img src="<c:url value="/resources/images/public/chat-icon.png"/>" alt="chat-icon" class="chat-icon">
	</a>
</div> --%>
<!-- Start of procurehere Zendesk Widget script -->
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/moment.min.js"/>"></script>
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
<%-- <script>
window.scatrack('enableFormTracking');
</script> --%>
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

		$.get("<spring:message code="app.url" />/maintenance-notification/active", function(data, status){
			console.log(data);

			var currentDate = moment();

			$(".maintenance-message").hide();

			if(data){
				if(data.isEnable){
					var weekNumber = data.noticeWeek;
					var firstDateOfMonth = moment().startOf("month");
					var firstDayOfWeek = firstDateOfMonth.clone().weekday(data.noticeDayOfWeek);
					// Check if first firstDayOfWeek is in the given month
					if( firstDayOfWeek.month() != firstDateOfMonth.month() ){
						weekNumber++;
					}

					var maintenanceDate = firstDayOfWeek.add(weekNumber-1, 'weeks').startOf('day');
					var maintenanceStart = maintenanceDate.clone().set({ "hour": data.noticeStartTime.split(":")[0], "minute": data.noticeStartTime.split(":")[1]});
					var maintenanceEnd = maintenanceDate.clone().set({ "hour": data.noticeEndTime.split(":")[0], "minute": data.noticeEndTime.split(":")[1]});

					function getOrdinal(num) { // num is defined here as a parameter
						const suffixes = ["th", "st", "nd", "rd"];
						const remainder = num % 100;
						return num + (suffixes[(remainder - 20) % 10] || suffixes[remainder] || suffixes[0]);
					}

					const dayWithOrdinal = getOrdinal(maintenanceDate.date()); // Get the day with ordinal
					console.log("maintenanceDate>>" + maintenanceDate.date());

					console.log(maintenanceDate.format('D MMMM YYYY (dddd) HH:mm'), maintenanceStart.format('D MMMM YYYY (dddd) HH:mm'), maintenanceEnd.format('D MMMM YYYY (dddd) HH:mm'))

						$(".maintenance-message").show()
						$(".maintenance-message .maintenance-notice").text(data.noticeDesc + " | " +  dayWithOrdinal + " " +  maintenanceDate.format('MMMM YYYY (dddd)') + " - "
								+ moment(data.noticeStartTime, ['h:m a', 'H:m']).format("hh:mmA") + " till " + moment(data.noticeEndTime, ['h:m a', 'H:m']).format("hh:mmA"))
				}else{
					$(".maintenance-message").hide()
				}
			}
		});
	};

	zE(function() {
		zE.logout();
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
</script>
