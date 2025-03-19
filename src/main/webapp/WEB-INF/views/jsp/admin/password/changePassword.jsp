<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:message var="changePasswordDesk" code="application.change.password" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${changePasswordDesk}] });
});
</script>
<script>
	$.validate();
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a>
						<spring:message code="application.dashboard" />
					</a></li>
				<li class="active"><spring:message code="changepassword.manage" /></li>
			</ol>
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap manage_icon">
					<spring:message code="changepassword.manage" />
				</h2>
			</div>
			<div class="clear"></div>
			<div class="Invited-Supplier-List import-supplier white-bg">
				<div class="meeting2-heading">
					<h3>
						<spring:message code="changepassword.manage.account" />
					</h3>
				</div>
				<div class="import-supplier-inner-first-new pad_all_15 global-list">
					<c:url var="changePassword" value="changePassword" />
					<form:form action="${changePassword}" data-parsley-validate="" modelAttribute="passwordObj" class="form-horizontal create-template" id="login-validation">
						<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<label class="marg-top-10">
									<spring:message code="changepassword.old" />
								</label>
							</div>
							<div class="col-md-4">
								<spring:message code="changepassword.old.length" var="length" />
								<form:input type="password" data-validation="required length" data-validation-length="8-15" data-validation-error-msg-length="${length}" path="oldPassword" cssClass="form-control" id="idOldPassword" />
								<form:errors path="oldPassword" cssClass="error" />
								<ul class="parsley-errors-list" id="parsley-id-4396"></ul>
								<strong></strong>
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<label class="marg-top-10">
									<spring:message code="changepassword.new" />
								</label>
							</div>
							<div class="col-md-4">
								<spring:message code="changepassword.new.length" var="length" />
								<spring:message code="changepassword.custom" var="custom" />
								<c:set var="rgx" value="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,15}$"></c:set>
								<form:input type="password" name="newPassword" data-validation="required custom length" data-validation-length="max64" data-validation-regexp="${!empty regex ? regex.regx :rgx}" data-validation-error-msg-length="${length}" data-validation-error-msg-custom="${!empty regex.message? regex.message :''}" path="newPassword"
									cssClass="form-control pwd" id="idNewPassword" />
								<p id="passwordPlaceHolder">${!empty regex.message? regex.message :''}</p>
								<ul class="parsley-errors-list" id="parsley-id-4396"></ul>
								<strong></strong>
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<label class="marg-top-10">
									<spring:message code="changepassword.confirm" />
								</label>
							</div>
							<div class="col-md-4">
								<spring:message code="changepassword.confirm.same" var="confirm" />
								<form:input type="password" data-validation="required confirmation" data-validation-error-msg-confirmation="${confirm}" path="confirmPassword" data-validation-confirm="newPassword" cssClass="form-control" id="idConfirmPassword" />
								<ul class="parsley-errors-list" id="parsley-id-4396"></ul>
								<strong></strong>
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<label class="marg-top-10">&nbsp;</label>
							</div>
							<div class="col-md-4 dd sky mar_b_15 ">
								<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" value="submit" id="changePassword" type="submit">
									<spring:message code="changepassword.reset" />
								</button>
								<c:url value="/" var="cancelUrl" />
								<a href="${cancelUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous open1">
									<spring:message code="application.cancel" />
								</a>
							</div>
						</div>
					</form:form>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js?1"/>"></script>
<script>
	$(document).ready(function() {
		$(".pwd").click(function() {
			$('#passwordPlaceHolder').hide();
		});
	
	});
</script>
<script>
$.validate({
	lang : 'en',
	modules : 'security'
});

</script>
