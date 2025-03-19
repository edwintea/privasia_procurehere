<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<spring:message var="stateCreateDesk" code="application.owner.state.create" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${stateCreateDesk}] });
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
<script src="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.js"/>"></script>
<link rel="stylesheet" href="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.min.css"/>">
<!--Side Bar End  -->
<div id="page-content">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a href="#"><spring:message code="application.dashboard" /></a></li>
			<c:url value="/admin/listState" var="createUrl" />
			<li><a href="${createUrl} "><spring:message code="state.list" /></a></li>
			<li class="active"><spring:message code="label.state" /> <c:out value='${btnValue2}' /> <spring:message code="label.state.form" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="state.administration" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<c:out value='${btnValue2}' />
					<spring:message code="label.state" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<!-- 	------ form start  here--------- -->
				<c:url var="saveState" value="saveState" />
				<form:form id="stateRegisterForm" data-parsley-validate="" commandName="stateObject" action="state" method="post" cssClass="form-horizontal bordered-row">
					<form:hidden path="id" />
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="stateCode" cssClass="marg-top-10">
								<spring:message code="state.code" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="state.description" var="desc" />
							<spring:message code="state.code.required" var="required" />
							<spring:message code="state.code.length" var="length" />
							<spring:message code="state.code.custom" var="custom" />
							<form:input path="stateCode" data-validation-length="1-10" data-validation="required,length,custom" data-validation-error-msg-custom="${custom}" cssClass="form-control" id="idStateCode"  data-validation-regexp="^([A-Za-z -]+)$"
								placeholder="${desc}" />
							<form:errors path="stateCode" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="stateName" cssClass="marg-top-10">
								<spring:message code="state.name" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="state.name" var="name" />
							<spring:message code="state.name.required" var="required" />
							<spring:message code="state.name.length" var="length" />
							<spring:message code="state.name.custom" var="custom" />
							<form:input path="stateName" data-validation="required,length,alphanumeric" data-validation-length="1-150" cssClass="form-control" data-validation-allowing="- " id="idStateName" placeholder="${name}"/>
							<form:errors path="stateName" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label for="idCountry" class="marg-top-10"><spring:message code="application.country" /></label>
						</div>
						<div class="col-md-5">
							<spring:message code="application.country.required" var="required" />
							<form:select path="country" id="idCountry" cssClass="chosen-select" data-validation="required" >
								<form:option value="">
									<spring:message code="application.country" />
								</form:option>
								<form:options items="${countrys}" itemValue="id" itemLabel="countryName"></form:options>
							</form:select>
							<form:errors path="country" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="status" cssClass="marg-top-10">
									<spring:message code="application.status" />
								</form:label>
							</div>
							<div class="col-md-5">
								<form:select path="status" cssClass="form-control chosen-select disablesearch" id="idStatus">
									<form:options items="${statusList}" />
								</form:select>
							</div>
						</div>
					
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
							<input type="submit" value="${btnValue2}" id="saveState" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out">
							<c:url value="/admin/listState" var="createUrl" />
							<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"><spring:message code="application.cancel" /></a>
						</div>
					</div>
				</form:form>
			</div>
			`
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({ lang : 'en' });
</script>
