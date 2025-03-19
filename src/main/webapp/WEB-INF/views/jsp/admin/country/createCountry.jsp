
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:message var="currencyCreateDesk" code="application.owner.country.create" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${currencyCreateDesk}] });
});
</script>
<div id="page-content">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li>
				<a href="#">
					<spring:message code="application.dashboard" />
				</a>
			</li>
			<c:url value="/admin/listCountry" var="createUrl" />
			<li>
				<a href="${createUrl} ">
					<spring:message code="country.list" />
				</a>
			</li>
			<li class="active">
				<c:out value='${btnValue}' />
				<spring:message code="label.country" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="country.administration" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="label.country" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<c:url var="saveCountry" value="saveCountry" />
				<form:form id="countryRegistration" data-parsley-validate="" cssClass="form-horizontal bordered-row" method="post" action="${pageContext.request.contextPath}/admin/saveCountry"
					modelAttribute="countryObj">
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="countryCode" cssClass="marg-top-10">
								<spring:message code="country.code" />
							</form:label>
						</div>
						<form:hidden path="id" />
						<div class="col-md-5">
							<spring:message code="country.description" var="desc" />
							<spring:message code="country.code.required" var="required" />
							<spring:message code="country.code.length" var="length" />
							<form:input path="countryCode" data-validation-length="1-4" data-validation="required length" cssClass="form-control " id="idCountryCode" placeholder="${desc}" />
							<form:errors path="countryCode" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="countryName" cssClass="marg-top-10">
								<spring:message code="country.name" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="country.name" var="name" />
							<spring:message code="country.name.required" var="required" />
							<spring:message code="country.name.length" var="length" />
							<form:input path="countryName" data-validation-length="1-64" data-validation="required length" cssClass="form-control" pattern='[A-Za-z\\s]*' id="idcountryName" placeholder="${name}" />
							<form:errors path="countryName" cssClass="error" />
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
					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
							<%-- <form:button type="button" id="saveCountry" value="${btnValue}" class="btn bnt-block btn-primary "></form:button> --%>
							<input type="submit" value="${btnValue}" id="saveCountry" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">
							<c:url value="/admin/listCountry" var="createUrl" />
							<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">
								<spring:message code="application.cancel" />
							</a>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
</script>
<!-- <script type="text/javascript" src="<c:url value="/resources/js/view/country.js"/>"></script> -->
