<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!-- <script src="js/bootstrap.min.js"></script> -->

<div class="container margin-bottom-5">
	<div class="buyer-check-section">
		<p>Activate your opportunity to unlock huge business savings</p>
		<p>Fill in your details to activate your free Procurehere account</p>
	</div>
	<div class="row">
		<div class="col-sm-3 col-md-3 col-lg-3 col-xs-0"></div>
		<div class="col-sm-12 col-md-6 col-lg-6 col-xs-12 buyer-purchase-details">
			<form:form commandName="buyer" id="idTrailSignupForm" method="post">
				<input type="hidden" name="signupId" value="${signupId}">
				<form:hidden path="companyRegistrationNumber" />
				<div class="form-group">
					<label>Company Name</label>
					<!-- <input type="text" class="form-control" id="" name="companyname"> -->
					<form:input type="text" id="idCompanyName" cssClass="form-control" placeholder="Enter your company name" data-validation="required length alphanumeric company_name" data-validation-allowing="-_ &.()" data-validation-length="4-124"
						path="companyName" />
				</div>
<%-- 				<div class="form-group">
					<label>Company Registration Number</label>
					<!-- <input type="text" class="form-control" id="" name="compregnumber"> -->
					<form:input type="text" id="idCompRegNum" cssClass="form-control" placeholder="Enter your company registration number" data-validation="required length alphanumeric crn_number" data-validation-allowing="-_ " data-validation-length="2-124"
						path="companyRegistrationNumber" />
				</div>
 --%>				<div class="form-group">
					<label>Country of Registration</label>
					<!-- <input type="text" class="form-control" id="" name="countryregistration"> -->
					<form:select path="registrationOfCountry" id="idRegCountry" cssClass="form-control" data-validation="required company_name">
						<form:option value="">Select Country of Registration</form:option>
						<form:options items="${countryList}" itemValue="id" itemLabel="countryName" />
					</form:select>
				</div>
				<div class="form-group">
					<label>Full Name</label>
					<!-- <input type="text" class="form-control" id="r" name="fname"> -->
					<form:input type="text" cssClass="form-control" placeholder="Company contact person name" data-validation="required length" path="fullName" data-validation-length="4-100" />
				</div>
				<div class="form-group">
					<label>Login Email</label>
					<!-- <input type="text" class="form-control" id="rr" name="lname"> -->
					<form:input type="text" id="idLoginEmail" cssClass="form-control" placeholder="e.g. contact@company.com" data-validation="required length email login_email" data-validation-length="6-128" path="loginEmail" />
				</div>
				<div class="form-group">
					<label>Communication Email</label>
					<!-- <input type="email" class="form-control" id="rrr" name="email"> -->
					<form:input type="text" cssClass="form-control" placeholder="All email communications will be sent to this address" data-validation="email" path="communicationEmail" />
				</div>
				<div class="form-group">
					<label>Contact Number</label>
					<!-- <input type="number" class="form-control" id="rrrr" name="companyname"> -->
					<form:input type="text" id="idCntNum" cssClass="form-control" placeholder="e.g. 0123456789" data-validation="number length" path="companyContactNumber" data-validation-length="6-14" />
				</div>
				<%-- <div class="form-group">
					<label>Password</label>
					<spring:message code="changepassword.custom" var="custom" />
					<!-- <input type="password" class="form-control" id="rrrrr" name="pwd"> -->
					<form:input path="password" type="password" class="form-control" id="newPass" data-validation="required custom length" data-validation-length="max15" placeholder="One digit, one small case, one capital case letter and a special character"
						data-validation-regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$@$!%*?&])[A-Za-z\d$@$!%*?&]{8,15}$" data-validation-error-msg-custom="${custom}" />
				</div> --%>
				<p class="ts-pera font-14-mrg">
					*By proceeding, you agree to our
					<span>
						<a target="_blank" href="<c:url value="/resources/termsandcondition.pdf"/>">terms & conditions</a>
					</span>
				</p>
				<button type="button" class="btn btn-primary payment-btn-checkout margin-btm-3 btn-padding submitPaymentBtn" data-toggle="modal">Submit</button>
				<div>
					<img src="<c:url value="/resources/images/public/McAfee.png"/>" alt="McAfee-badge" class="max-80 img-responsive">
				</div>
			</form:form>

			<!-- Modal -->
			<div class="modal fade" id="myModal" role="dialog">
				<div class="modal-dialog">

					<!-- Modal content-->
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close close-btn" data-dismiss="modal">&times;</button>
							<div class="buyer-check-section">
								<p class="margin-top-0">Want Uninterrupted Access?</p>
							</div>
						</div>
						<div class="modal-body">
							<div class="body-text">
								<p>
									Update your billing preference to enjoy seamless access after your free trial ends.
								</p>
							</div>
							<div class="">
								<button type="button" class="btn btn-primary payment-btn-checkout margin-right-3 proceedPayment">Save credit card details</button>
								<button type="button" class="btn btn-default btn-padding border-blue skipPayment">Skip for now</button>
								<div class="body-text"><p><b>Note:</b> Don't worry! We will ask you before we charge you.</p></div>
							</div>
							
						</div>
					</div>


				</div>
			</div>
		</div>
		<div class="col-sm-3 col-md-3 col-lg-3 col-xs-0"></div>
	</div>

</div>


<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/freeTrialSignup.js"/>"></script>
<script type="text/javascript">
	$.validate({ lang : 'en', modules : 'date, security' });
</script>