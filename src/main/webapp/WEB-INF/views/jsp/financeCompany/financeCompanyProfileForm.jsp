<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/response-messages.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<spring:message var="buyerProfile" code="application.buyer.profile" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerProfile}] });
});
</script>
<style type="text/css">
.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

#page-content-wrapper {
	min-height: 1200px;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}

.marLeft {
	margin-left: 45px;
}
</style>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a id="dashboardLink" href="${pageContext.request.contextPath}/finance/financeDashboard">Dashboard</a></li>
				<li class="active">profile</li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap progress-head">Finance Company Profile</h2>
				<div class="right-header-button"></div>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div class="example-box-wrapper wigad-new white-bg">
				<!-- 	</div>
			</div> -->
				<div class="pro_set_wrap">
					<div class="Invited-Supplier-List import-supplier white-bg">

						<div class="rec_form">
							<div class="row">
								<div class="col-md-offset-3 col-md-6">
									<c:url var="financeCompanyProfileForm" value="financeCompanyProfileForm" />
									<form:form action="${financeCompanyProfileForm}" id="demo-form" method="post" modelAttribute="financeCompany" autocomplete="off">
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
												<form:input type="text" path="companyRegistrationNumber" placeholder="" class="rec_inp_style1" data-validation="required" readonly="true" />
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
								Year Establish
							</div>
							<div class="rec_form_inp">
								
								<form:input type="text" path="yearOfEstablished" placeholder="Enter year establish" class="rec_inp_style1" />
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
										
										
										<div class="rec_address">
											<spring:message code="buyer.profilesetup.company.address" />
										</div>


										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="application.country" />
											</div>
											<div class="rec_form_inp">
												<form:select path="registrationOfCountry" data-validation="required" id="registrationOfCountry" class="chosen-select rec_inp_style2">
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
												<spring:message code="application.addr1" />
											</div>
											<div class="rec_form_inp">
												<form:input type="text" path="line1" placeholder="" class="rec_inp_style1" data-validation="length" data-validation-allowing="- " data-validation-length="2-250"></form:input>
											</div>
										</div>
										<div class="rec_form_row">
											<div class="rec_form_lable">
												<spring:message code="application.addr2" />
											</div>
											<div class="rec_form_inp">
												<form:input type="text" path="line2" placeholder="" class="rec_inp_style1"></form:input>
											</div>
										</div>

										<div class="rec_form_row">
											<div class="rec_form_lable">&nbsp;</div>
											<div class="rec_form_inp">
												<div class="recf_bttn">
													<button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out">Update</button>
													<a href="${pageContext.request.contextPath}/finance/financeDashboard" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">Cancel</a>
												</div>
											</div>
										</div>
									</form:form>
								</div>
							</div>
							<div class="clearfix"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript">
<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '.button-previous, #dashboardLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>
	$.validate({ lang : 'en',
	modules : 'date, security' });
</script>
<script type="text/javascript " src="<c:url value="/resources/js/view/buyer.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$('#idMobileNumber').mask('+00 00000000000', { placeholder : "e.g. +60 122735465" });
		$('#idCompanyContactNumber').mask('+00 00000000000', { placeholder : "e.g. +60 122735465" });
	});
</script>
