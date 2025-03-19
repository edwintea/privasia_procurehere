
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<spring:message var="timeZoneCreateDesk" code="application.owner.timezone.create" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${timeZoneCreateDesk}] });
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
</style>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="timezone.title" /></title>
<meta charset="UTF-8">
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
</head>
<body>
	<!-- <div id="loading" style="background-color: rgba(255, 255, 255, 0.6)">
		<div class="spinner">
			<div class="bounce1"></div>
			<div class="bounce2"></div>
			<div class="bounce3"></div>
		</div>
	</div> -->
	<div id="page-content">
		<div class="col-md-offset-1 col-md-10">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<li><a href="#"><spring:message code="application.dashboard" /></a></li>
				<c:url value="/admin/listTimeZone" var="listUrl" />
				<li><a href="${listUrl}"><spring:message code="timezone.list" /></a></li>
				<li class="active"><c:out value='${btnValue}' />
					<spring:message code="label.timezone" /></li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap manage_icon">
					<spring:message code="timezone.administration" />
				</h2>
			</div>
			<div class="Invited-Supplier-List import-supplier white-bg">
				<div class="meeting2-heading">
					<h3>
						<c:out value='${btnValue}' />
						<spring:message code="label.timezone" />
					</h3>
				</div>
				<div class="import-supplier-inner-first-new pad_all_15 global-list">
					<c:url var="saveTimeZone" value="timeZone" />
					<form:form id="timeZone" data-parsley-validate="" cssClass="form-horizontal bordered-row" method="post" action="${saveTimeZone}" modelAttribute="timeZoneObj">
						<header class="form_header"> </header>
						<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="timeZone" cssClass="marg-top-10">
									<spring:message code="label.timezone" />
								</form:label>
							</div>
							<form:hidden path="id" />
							<div class="col-md-5">
								<spring:message code="timezone.placeholder" var="place" />
								<spring:message code="timezone.required" var="required" />
								<spring:message code="timezone.length" var="length" />
								<form:input path="timeZone" data-validation-length="1-64" data-validation="required alphanumeric length" data-validation-error-msg-required="${required}" data-validation-error-msg-length="${length}" style="text-transform:uppercase"
									data-validation-allowing="+:" cssClass="form-control " id="idtimeZone" placeholder="${place}"></form:input>
								<form:errors path="timeZone" cssClass="error" />
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="status" cssClass="marg-top-10">
									<spring:message code="application.status" />
								</form:label>
							</div>
							<div class="col-md-5">
								<form:select path="status" cssClass="form-control chosen-select" id="idStatus">
									<form:options items="${statusList}" />
								</form:select>
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="timeZoneDescription" cssClass="marg-top-10">
									<spring:message code="timezone.description" />
								</form:label>
							</div>
							<div class="col-md-5">
								<spring:message code="timezone.description" var="desc" />
								<spring:message code="timezone.description.required" var="required" />
								<spring:message code="timezone.description.length" var="length" />
								<form:input path="timeZoneDescription" cssClass="form-control"
									id="idtimeZoneDescription" placeholder="${desc}" />
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<label for="idCountry" class="marg-top-10"><spring:message code="application.country" /> </label>
							</div>
							<div class="col-md-5">
								<spring:message code="application.country" var="cntry" />
								<spring:message code="application.country.required" var="cntryreq" />
								<form:select path="country" name="${cntry}" id="idCountry" cssClass="chosen-select" data-validation="required" data-validation-error-msg-required="${cntryreq}">
									<form:option value="">
										<spring:message code="application.country" />
									</form:option>
									<form:options items="${country}" itemValue="id" itemLabel="countryName"></form:options>
								</form:select>
							</div>
						</div>
						<div class="row marg-bottom-202">
							<div class="col-md-3">
								<label class="marg-top-10"></label>
							</div>
							<div class="col-md-9 dd sky mar_b_15">
								<%-- <form:button type="button" id="saveCountry" value="${btnValue}" class="btn bnt-block btn-primary "></form:button> --%>
								<input type="submit" value="${btnValue}" id="saveTimeZone" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">
								<c:url value="/admin/listTimeZone" var="listUrl" />
								<a href="${listUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"><spring:message code="application.cancel" /></a>
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
	<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
	<script>
		$(document).ready(function() {
			$('#idtimeZone').mask('GMT+00:00');
		});
	</script>
</body>
</html>