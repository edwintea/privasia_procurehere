
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="agreementTypeCreateDesk" code="application.agreement.type.create" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${agreementTypeCreateDesk}] });
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
<div id="page-content" view-name="uom">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard">
					<spring:message code="application.dashboard" />
				</a></li>
			<c:url value="/buyer/agreementType/agreementTypeList" var="listUrl" />
			<li><a id="listLink" href="${listUrl}">
					<spring:message code="application.agreement.type.list" />
				</a></li>
			<li class="active"><c:out value='${btnValue}' /> <spring:message code="label.agreement.type" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap line_icon">
				<spring:message code="agreement.type.administration" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="label.agreement.type" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<c:url var="saveAgreementType" value="saveAgreementType" />
				<form:form id="agreementTypemRegistration" cssClass="form-horizontal bordered-row" method="post" action="agreementType" modelAttribute="agreementTypeObject">
					<header class="form_header"> </header>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="agreementType" cssClass="marg-top-10">
								<spring:message code="label.agreement.type" />
							</form:label>
						</div>
						<form:hidden path="id" />
						<div class="col-md-5">
							<spring:message code="agreement.type.placeholder" var="place" />
							<spring:message code="agreement.type.required" var="required" />
							<spring:message code="agreement.type.length" var="length" />
							<form:input path="agreementType" value="${agreementType.agreementType}" data-validation="required length alphanumeric" data-validation-length="1-8" data-validation-allowing="-&/_, " cssClass="form-control " id="idagreementType" placeholder="${place}" />
							<form:errors path="agreementType" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="description" cssClass="marg-top-10">
								<spring:message code="agreement.type.description" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="agreement.type.description.placeholder" var="desc" />
							<spring:message code="agreement.type.description.required" var="required" />
							<spring:message code="agreement.type.description.length" var="length" />
							<form:textarea path="description" cssClass="form-control" data-validation-length="0-250" data-validation="length" pattern='[A-Za-z\\s]*' id="idDescription" placeholder="${desc}" />
							<form:errors path="description" cssClass="error" />
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
							<sec:authorize access="hasRole('ROLE_AGREEMENT_TYPE_EDIT') or hasRole('ADMIN')" var="canEdit" />
							<form:button type="button" value="save" id="saveAgreementType" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" disabled="${!canEdit}">${btnValue}</form:button>
							<c:url value="/buyer/agreementType/agreementTypeList" var="createUrl" />
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
	<c:if test="${buyerReadOnlyAdmin}">
	$(window).bind('load', function() {
		var allowedFields = '.button-previous, #dashboardLink, #listLink';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if>

	$.validate({
		lang : 'en'
	});
	
	
	$('#saveAgreementType').on('click', function(){
		if($('#agreementTypemRegistration').isValid()) {
			$('#loading').show();
			$('#agreementTypemRegistration').submit();
		}
	});
	
</script>
