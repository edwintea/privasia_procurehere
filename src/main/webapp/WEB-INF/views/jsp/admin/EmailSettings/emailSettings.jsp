<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="label.emailsettings" /></title>
<meta charset="UTF-8">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
</head>
<body>
	<div id="page-content">
		<div class="col-md-offset-1 col-md-10">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<li><a href="#"><spring:message code="application.dashboard" /></a></li>
				<li class="active"><spring:message code="label.emailsettings" /></li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap manage_icon">
					<spring:message code="email.administration" />
				</h2>
			</div>
			<div class="Invited-Supplier-List import-supplier white-bg">
				<div class="meeting2-heading">
					<h3>
						<c:out value='${btnValue}' />
						<spring:message code="label.email" />
					</h3>
				</div>
				<div class="import-supplier-inner-first-new pad_all_15 global-list">
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<c:url var="saveEmail" value="saveEmail" />
					<form:form id="idEmailForm" commandName="emailObject" action="saveEmail" method="post" cssClass="form-horizontal bordered-row">
						<form:hidden path="id" value="${emailObject.id}" />
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="supplierSignupNotificationEmailAccount" cssClass="marg-top-10">
									<spring:message code="label.email" />
								</form:label>
							</div>
							<div class="col-md-5">
								<form:input path="supplierSignupNotificationEmailAccount" data-validation-length="1-500" data-validation="required length" cssClass="form-control" data-validation-ignore=",;" id="idAdminEmailAccount"
									value="${emailObject.supplierSignupNotificationEmailAccount}" />
								<form:errors path="supplierSignupNotificationEmailAccount" cssClass="error" />
							</div>
						</div>
						<br>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<label class="marg-top-10"></label>
							</div>
							<div class="col-md-9 dd sky mar_b_15">
								<input type="submit" value="<spring:message code="email.update"/>" id="saveEmail" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out  ">
								<c:url value="/owner/ownerDashboard" var="createUrl" />
								<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"><spring:message code="application.cancel" /></a>
							</div>
						</div>
					</form:form>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
	<script>
		$.validate({ lang : 'en' });
	</script>
	<!-- <script type="text/javascript" src="<c:url value="/resources/js/view/emailSettings.js"/>"></script> -->
</body>
</html>