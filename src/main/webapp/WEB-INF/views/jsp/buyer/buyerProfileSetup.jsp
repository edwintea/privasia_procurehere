<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/response-messages.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<spring:message var="buyerRegProfileSetupDesk" code="application.buyer.registration.profile.setup" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerRegProfileSetupDesk}] });
});
</script>
<script type="text/javascript" src="//cdn.wishpond.net/connect.js?merchantId=1419231&writeKey=185257b4d56c" async></script>
<!-- Google Tag Manager -->
<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push(

{'gtm.start': new Date().getTime(),event:'gtm.js'}
);var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-KC3J7RN');</script>
<!-- End Google Tag Manager -->
<!-- Google Tag Manager (noscript) -->
<noscript>
	<iframe src="https://www.googletagmanager.com/ns.html?id=GTM-KC3J7RN" height="0" width="0" style="display: none; visibility: hidden"></iframe>
</noscript>
<!-- End Google Tag Manager (noscript) -->
<script type="text/javascript" src="//cdn.wishpond.net/connect.js?merchantId=1419231&writeKey=185257b4d56c" async></script>
<style type="text/css">
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}
.pass-desc {
	color: #7f7f7f;
	font-weight: normal;
	font-size: 13px;
}
.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}

#stateList_chosen .chosen-drop {
	border-bottom: 0;
	border-top: 1px solid #aaa;
	top: auto;
	bottom: 40px;
}

.rec_form_check {
	padding: 0 0 0 0 !important;
}
</style>
<div class="pro_set_wrap">
	<div class="pro_set_top">
		<div class="pset_inner">
			<div class="pset_logo">
				<a href="#">
					<img src="<c:url value="/resources/assets/images/pro_logo.png"/>" alt="logo">
				</a>
			</div>
			<%-- 			<div class="pset_top_right">
				<a href="#" onclick="zE(function() { zE.activate(); }); return false;" class="pset_top_bttn hvr-rectangle-out hvr-pop"><spring:message code="buyer.profilesetup.button.contactus" /></a> 
				<a href="#" onclick="zE(function() { zE.activate(); }); return false;" class="pset_top_bttn hvr-rectangle-out hvr-pop marg-left-10"><spring:message code="buyer.profilesetup.button.help" /></a>
			</div>
 --%>
			<div class="clearfix"></div>
		</div>
	</div>
	<div class="banner_area">
		<img src="${pageContext.request.contextPath}/resources/assets/images/header_eleements.png" alt="Thank you for choosing us">
	</div>
	<div class="register_wrap">
		<div class="register_content">
			<div class="rec_inner">
				<div class="rec_icon">
					<img src="${pageContext.request.contextPath}/resources/assets/images/register_icon.png" alt="Register">
				</div>
				<div class="rec_heading">
					<spring:message code="buyer.profile.setup" />
					<br> <span> <spring:message code="buyer.profile.setup.form.msg" />
					</span>
				</div>
				<div class="rec_form">
					<div class="row clearfix">
						<div class="col-md-12">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						</div>
					</div>
					<c:url var="buyerProfileSetup" value="buyerProfileSetup" />
					<form:form action="${buyerProfileSetup}" id="demo-form" method="post" modelAttribute="buyer" autocomplete="off">
						<form:hidden path="id" />
						<input type="hidden" name="d" value="${d}">
						<input type="hidden" name="v" value="${v}">
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.companyname" />
							</div>
							<div class="rec_form_inp">
								<spring:message code="company.name.placeholder" var="companyname" />
								<form:input type="text" id="companyName" readonly="${not empty buyer.companyName ?true : false }" cssClass="form-control" path="companyName" 
								placeholder="${companyname}" class="rec_inp_style1" data-validation="required length alphanumeric company_name" 
								data-validation-allowing=",-_ &.()\/" 
								data-validation-error-msg="The input value can only contain alphanumeric characters and ,-_ &.()\/ and spaces"
								data-validation-length="4-124" />
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.regnumber" />
							</div>
							<div class="rec_form_inp">
								<spring:message code="buyerprofile.company.regi.placeholder" var="reginumber" />
								<form:input type="text" path="companyRegistrationNumber" readonly="${not empty buyer.companyRegistrationNumber ? true : false }" cssClass="form-control"
								data-validation="required length alphanumeric"
								data-validation-length="2-124"
								data-validation-allowing=" &.,/()_-" 
								data-validation-error-msg="The input value can only contain alphanumeric characters and ,-_&.()/ and spaces"
								placeholder="${reginumber}" class="rec_inp_style1"  />
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.fullname" />
							</div>
							<div class="rec_form_inp">
								<spring:message code="full.name.placeholder" var="fulname" />
								<form:input type="text" path="fullName" placeholder="${fulname}" class="rec_inp_style1" data-validation="required" readonly="true" />
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.mobileno" />
							</div>
							<div class="rec_form_inp">
								<spring:message code="mobile.number.placeholder" var="mobilenumber" />
								<form:input type="text" path="mobileNumber" id="idMobileNumber" placeholder="${mobilenumber}" class="rec_inp_style1" data-validation="required length" data-validation-ignore="+ " data-validation-length="6-14" />
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.contactno" />
							</div>
							<div class="rec_form_inp">
								<form:input type="text" path="companyContactNumber" id="idCompanyContactNumber" placeholder="" class="rec_inp_style1" data-validation="required length" data-validation-ignore="+ " data-validation-length="6-14" />
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.commemail" />
							</div>
							<div class="rec_form_inp">
								<form:input type="text" path="communicationEmail" placeholder="" class="rec_inp_style1" data-validation="required email" />
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.loginmail" />
							</div>
							<div class="rec_form_inp">
								<form:input type="text" path="loginEmail" class="rec_inp_style1" data-validation="required email" readonly="true" />
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.Password" />
							</div>
							<div class="rec_form_inp">
								<c:set var="rgx" value="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,15}$"></c:set>
								<spring:message code="changepassword.new.required" var="required" />
								<spring:message code="changepassword.new.length" var="length" />
								<spring:message code="changepassword.custom" var="custom" />
								<spring:message code="password.placeholder" var="pasword" />
								<form:password path="password" class="rec_inp_style1 pwd" placeholder="Enter Password" data-validation="required custom length" data-validation-length="max64" data-validation-regexp="${!empty regex ? regex.regx :rgx}" data-validation-error-msg-required="${required}" data-validation-error-msg-length="${length}"
									data-validation-error-msg-custom="${!empty regex.message?regex.message:''}" />
								<p class="pass-desc" id="passwordPlaceHolder">${!empty regex.message? regex.message :''}</p>
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.confirmpassword" />
							</div>
							<div class="rec_form_inp">
								<form:password path="" placeholder="" name="buyerPassword" class="rec_inp_style1" data-validation="confirmation" data-validation-confirm="password" data-validation-error-msg-confirmation="Your password does not match"></form:password>
							</div>
						</div>
						<div class="rec_address">
							<spring:message code="buyer.profilesetup.company.address" />
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="application.addr1" />
							</div>
							<div class="rec_form_inp">
								<form:input type="text" path="line1" placeholder="" class="rec_inp_style1" data-validation="length" data-validation-allowing="- " data-validation-length="1-250"></form:input>
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="application.addr2" />
							</div>
							<div class="rec_form_inp">
								<form:input type="text" path="line2" placeholder="" data-validation="length" data-validation-allowing="- " data-validation-length="1-250" class="rec_inp_style1"></form:input>
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="application.city" />
							</div>
							<div class="rec_form_inp">
								<spring:message code="buyerprofile.city.custom" var="errmsg" />
								<form:input type="text" path="city" placeholder="" class="rec_inp_style1" data-validation="required custom length" data-validation-length="2-200" data-validation-error-msg-custom="${errmsg}" data-validation-regexp="^([a-zA-Z\\-\\s]+)$"></form:input>
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="application.postcode" />
							</div>
							<div class="rec_form_inp">
								<spring:message code="buyerprofile.postcode" var="errmsg" />
								<form:input type="text" path="postcode" placeholder="" class="rec_inp_style1" data-validation="required length alphanumeric" data-validation-length="2-15" data-validation-allowing="\ "></form:input>
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="application.country" />
							</div>
							<div class="rec_form_inp">
								<form:select path="registrationOfCountry" id="registrationOfCountry" class="chosen-select rec_inp_style2" data-validation="required ">
									<form:option value="">
										<spring:message code="application.country.registration" />
									</form:option>
									<form:options items="${registeredCountry}" itemValue="id" itemLabel="countryName" />
								</form:select>
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="application.state" />
							</div>
							<div class="rec_form_inp">
								<form:select path="state" data-validation="required" class="chosen-select rec_inp_style2" id="stateList">
									<form:option value="">
										<spring:message code="application.selectstate" />
									</form:option>
									<form:options items="${countryStates}" itemValue="id" itemLabel="stateName"></form:options>
								</form:select>
							</div>
						</div>
						<div class="rec_form_row row">
							<div class="rec_form_lable rec_form_check">
								<input type="checkbox" ${allowTocheckTandC ? 'disabled="disabled"' : ''} name="agree" id="agree" value="agree" style="float: right">
							</div>
							<div class="rec_form_inp" style="padding-left: 15px;">
								<a target="_blank" href="<c:url value="/resources/termsandcondition.pdf"/>">Terms and conditions</a>
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">&nbsp;</div>
							<div class="rec_form_inp">
								<div class="recf_bttn">
									<button type="submit" disabled="disabled" class="pset_bot_bttn hvr-pop hvr-rectangle-out">
										<spring:message code="buyer.profilesetup.button.save" />
									</button>
								</div>
								<%-- <div class="recf_terms">
									<a href="termsOfUse.jsp"><spring:message code="buyer.profilesetup.term" /></a>
								</div> --%>
							</div>
						</div>
					</form:form>
				</div>
			</div>
			<div class="clearfix"></div>
		</div>
	</div>
	<div class="pset_footer">
		<spring:message code="application.allright" />
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js?1"/>"></script>
<script type="text/javascript">
	$.validate({
		lang : 'en',
		modules : 'date, security'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/buyerProfileSetup.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/buyer.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		
		$(document).ready(function() {
			$(".pwd").click(function() {
				$('#passwordPlaceHolder').hide();
			});
		
		});
			
		$('#idMobileNumber').mask('+00 00000000000', {
			placeholder : "e.g. +60 122735465"
		});
		$('#idCompanyContactNumber').mask('+00 00000000000', {
			placeholder : "e.g. +60 122735465"
		});
		$("#agree").click(function() {
			if ($('#agree').is(":checked")) {
				$(".pset_bot_bttn").attr("disabled", false);
			} else {
				$(".pset_bot_bttn").attr("disabled", true);
			}

		});
	});
</script>
