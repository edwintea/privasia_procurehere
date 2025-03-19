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
<style type="text/css">
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
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
</style>
<div class="pro_set_wrap">
	<div class="pro_set_top">
		<div class="pset_inner">
			<div class="pset_logo">
				<a href="#"><img src="<c:url value="/resources/assets/images/pro_logo.png"/>" alt="logo"></a>
			</div>
			
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
					<br> <span><spring:message code="buyer.profile.setup.form.msg" /></span>
				</div>
				<div class="rec_form">
					<div class="row clearfix">
						<div class="col-md-12">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						</div>
					</div>
					<c:url var="financeCompanyProfileSetup" value="financeCompanyProfileSetup" />
					<form:form action="${financeCompanyProfileSetup}" id="demo-form" method="post" modelAttribute="financeCompany" autocomplete="off">
						<form:hidden path="id" />
						<input type="hidden" name="d" value="${d}">
						<input type="hidden" name="v" value="${v}">
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.companyname" />
							</div>
							<div class="rec_form_inp">
								<spring:message code="company.name.placeholder" var="companyname" />
								<form:input type="text" path="companyName" placeholder="${companyname}" class="rec_inp_style1" data-validation="required" readonly="true" />
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.regnumber" />
							</div>
							<div class="rec_form_inp">
								<form:input type="text" path="companyRegistrationNumber" placeholder="" class="rec_inp_style1" data-validation="required" />
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
								<spring:message code="buyer.profilesetup.contactno" />
							</div>
							<div class="rec_form_inp">
								<form:input type="text" path="companyContactNumber" id="idCompanyContactNumber" placeholder="" class="rec_inp_style1" data-validation="required length" data-validation-ignore="+ " data-validation-length="6-14" />
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
								Year Establish
							</div>
							<div class="rec_form_inp">
								
								<form:input type="year" path="yearOfEstablished" placeholder="Enter year establish" class="rec_inp_style1" />
							</div>
						</div>
						
						
						<div class="rec_form_row">
							<div class="rec_form_lable">
								Company Fax
							</div>
							<div class="rec_form_inp">
								
								<form:input type="text" path="faxNumber" placeholder="Fax Number" class="rec_inp_style1" />
							</div>
						</div>
						
						
						<div class="rec_form_row">
							<div class="rec_form_lable">
								Company Web-site
							</div>
							<div class="rec_form_inp">
								
								<form:input type="text" path="companyWebsite" placeholder="Enter company website" class="rec_inp_style1"  />
							</div>
						</div>
						
						
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.Password" />
							</div>
							<div class="rec_form_inp">
								<spring:message code="changepassword.new.required" var="required" />
								<spring:message code="changepassword.new.length" var="length" />
								<spring:message code="changepassword.custom" var="custom" />
								<spring:message code="password.placeholder" var="pasword" />
								<form:password path="password" class="rec_inp_style1" placeholder="${pasword}"  data-validation="required custom length" data-validation-length="max15" data-validation-regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$@$!%*?&])[A-Za-z\d$@$!%*?&]{8,15}$"
									data-validation-error-msg-required="${required}" data-validation-error-msg-length="${length}" data-validation-error-msg-custom="${custom}" />
							</div>
						</div>
						<div class="rec_form_row">
							<div class="rec_form_lable">
								<spring:message code="buyer.profilesetup.confirmpassword" />
							</div>
							<div class="rec_form_inp">
								<form:password path="" placeholder="" name="buyerPassword" class="rec_inp_style1" data-validation="confirmation" data-validation-confirm="password"></form:password>
							</div>
						</div>
						
						
						<div class="rec_form_row">
							<div class="rec_form_lable">&nbsp;</div>
							<div class="rec_form_inp">
								<div class="recf_bttn">
									<button type="submit" class="pset_bot_bttn hvr-pop hvr-rectangle-out">
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
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript">
	$.validate({
		lang : 'en',
		modules : 'date, security'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/buyer.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$('#idMobileNumber').mask('+00 00000000000', {
			placeholder : "e.g. +60 122735465"
		});
		$('#idCompanyContactNumber').mask('+00 00000000000', {
			placeholder : "e.g. +60 122735465"
		});
	});
</script>
