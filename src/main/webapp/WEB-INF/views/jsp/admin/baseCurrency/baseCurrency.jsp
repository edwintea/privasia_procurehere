<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="currency.title" /></title>
<meta charset="UTF-8">
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
</head>
<body>
	<!--Side Bar End  -->
	<spring:message var="currencyCreateDesk" code="application.owner.currency.create" />
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
				<c:url value="/admin/baseCurrencyList" var="listUrl" />
				<li>
					<a href="${listUrl }">
						<spring:message code="currency.list" />
					</a>
				</li>
				<li class="active">
					<c:out value='${btnValue}' />
					<spring:message code="label.currency" />
				</li>
			</ol>
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap manage_icon">
					<spring:message code="currency.administration" />
				</h2>
			</div>
			<div class="Invited-Supplier-List import-supplier white-bg">
				<div class="meeting2-heading">
					<h3>
						<c:out value='${btnValue}' />
						<spring:message code="label.currency" />
					</h3>
				</div>
				<div class="import-supplier-inner-first-new pad_all_15 global-list">
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<c:url var="baseCurrency" value="baseCurrency" />
					<form:form id="BaseCurrencyForm" commandName="CurrencyObject" cssClass="form-horizontal bordered-row" method="post" action="baseCurrency">
						<form:hidden path="id" />
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="currencyCode" cssClass="marg-top-10">
									<spring:message code="currency.code" />
								</form:label>
							</div>
							<div class="col-md-5">
								<spring:message code="basecurrency.description" var="desc" />
								<spring:message code="basecurrency.code.required" var="required" />
								<spring:message code="basecurrency.code.length" var="length" />
								<spring:message code="basecurrency.code.custom" var="errmsg" />
								<form:input path="currencyCode" data-validation-length="1-64" data-validation="required custom length" data-validation-regexp="^[A-z]+$" cssClass="form-control" id="idCurrencyCode" placeholder="${desc} " />
								<form:errors path="currencyCode" cssClass="error" />
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="currencyName" cssClass="marg-top-10">
									<spring:message code="currency.name" />
								</form:label>
							</div>
							<div class="col-md-5">
								<spring:message code="basecurrency.name" var="name" />
								<spring:message code="basecurrency.name.required" var="required" />
								<spring:message code="basecurrency.name.length" var="length" />
								<form:input path="currencyName" data-validation-length="1-64" data-validation="required length" cssClass="form-control" id="idCurrencyName" placeholder="${name}" />
								<form:errors path="currencyName" cssClass="error" />
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="status" class="marg-top-10">
									<spring:message code="application.status" />
								</form:label>
							</div>
							<div class="col-md-5">
								<form:select path="status" cssClass="form-control chosen-select" id="status">
									<form:options items="${statusList}" />
								</form:select>
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<label class="marg-top-10"></label>
							</div>
							<div class="col-md-9 dd sky mar_b_15">
								<form:button value="${btnValue}" id="baseCurrency" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" type="submit">${btnValue}</form:button>
								<c:url value="/admin/baseCurrencyList" var="createUrl" />
								<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">
									<spring:message code="application.cancel" />
								</a>
							</div>
						</div>
					</form:form>
				</div>
				`
			</div>
		</div>
	</div>
	<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>">
		
	</script>
	<script>
		$.validate({ lang : 'en' });
	</script>
</body>
</html>