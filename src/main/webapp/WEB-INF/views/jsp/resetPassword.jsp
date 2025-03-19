<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery-form-validator/2.3.26/jquery.form-validator.min.js"></script>
<div id="page-content-wrapper">
	<section id="admin_regSteps_wrapper">
		<c:url var="url" value="resetPassword" />
		<form:form action="${url}" modelAttribute="password" method="POST" cssClass="form-horizontal">
			<div class="container-fluid">
				<div class="row">
					<div class="col-xs-12">
						<div class="reset_passwrap">
							<div class="reset_heading">Reset Password</div>
							<div class="reset_box">
								<c:if test="${not empty error or not empty errors}">
									<div class="alert alert-danger" id="idError">
										<div class="bg-red alert-icon">
											<i class="glyph-icon icon-times"></i>
										</div>
										<div class="alert-content">
											<h4 class="alert-title">Error</h4>
											<p id="idErrorMessage">
												${error}
												<c:forEach items="${errors}" var="errorMsg">
												    ${errorMsg}<br>
												</c:forEach>
											</p>
										</div>
									</div>
								</c:if>
								<div class="reset_form">
									<div class="form-group">
										<label class="reset_text">Login Email</label>
										<div class="reset_inp">
											<form:input type="text" path="loginEmail" class="form-control" required="required" id="email" readonly="true"></form:input>
										</div>
									</div>
									<div class="clear"></div>
									<div class="form-group">
										<label class="reset_text">Username</label>
										<div class="reset_inp">
											<form:input type="text" path="username" class="form-control" required="required" id="username" readonly="true"></form:input>
										</div>
									</div>
									<div class="clear">
										<input type="hidden" name="oldPassword" value="dummy" class="form-control" id="old_pwd" />
									</div>
									<div class="form-group">
										<label class="reset_text">New Password </label>
										<div class="reset_inp">
											<c:set var="rgx" value="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,15}$"></c:set>
											<form:password path="newPassword" name="newPassword" data-validation=" required custom length " data-validation-length="max64" data-validation-regexp="${!empty regex ? regex.regx :rgx}" data-validation-error-msg-custom="${!empty regex ? regex.message :''}" cssClass="form-control pwd" id="new_pwd" placeholder="Enter new password" disabled="${not empty tokenInvalidFlag and tokenInvalidFlag ? 'true' : 'false'}"></form:password>
											<form:errors path="newPassword" cssClass="error" />
											<p id="passwordPlaceHolder">${!empty regex ? regex.message :''}</p>
										</div>
									</div>
									<div class="clear"></div>
									<div class="form-group">
										<label class="reset_text">Confirm Password</label>
										<div class="reset_inp">
											<form:password data-validation="required custom length confirmation" data-validation-length="max64" data-validation-regexp="${!empty regex ? regex.regx :rgx}" data-validation-error-msg-custom="${!empty regex ? regex.message :''}" data-validation-error-msg-confirmation="New password and confirm password must be same" path="confirmPassword" data-validation-confirm="newPassword" cssClass="form-control" id="confirm_pwd" placeholder="Enter confirm password"
												disabled="${not empty tokenInvalidFlag and tokenInvalidFlag ? 'true' : 'false'}" />
											<form:errors path="confirmPassword" cssClass="error" />
										</div>
									</div>
									<c:if test="${not empty tokenInvalidFlag and !tokenInvalidFlag}">
										<div class="reset_bttn_wrap">
											<form:button type="submit" value="Save Password" class="btn btn-info ph_btn hvr-pop hvr-rectangle-out" disabled="${not empty tokenInvalidFlag and tokenInvalidFlag ? 'true' : 'false'}">Save</form:button>
										</div>
									</c:if>
								</div>
							</div>
							<div class="clear"></div>
						</div>
					</div>
				</div>
			</div>
		</form:form>
	</section>
</div>
<!-- WIDGETS -->
<!-- Uniform -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform-demo.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js"/>"></script>
<!-- Bootstrap Tooltip -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/tooltip/tooltip.js"/>"></script>
<!-- Content box -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<script>
	$(document).ready(function() {
		$(".pwd").click(function() {
			$('#passwordPlaceHolder').hide();
		});

	});
</script>
<style>
.reset_text {
	width: 35%;
}

.reset_inp {
	width: 55%;
}

.reset_bttn_wrap {
	text-align: center;
	padding-top: 15px;
}

.form-group {
	margin: 10px 0;
}
</style>
<script>
	$.validate({
		lang : 'en',
		modules : 'security'
	});
</script>
