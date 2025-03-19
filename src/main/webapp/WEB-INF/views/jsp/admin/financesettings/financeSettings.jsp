
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="buyerSettingDesk" code="application.buyer.settings" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerSettingDesk}] });
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
<div id="page-content">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a id="dashboardLink" href='<c:url value="/finance/financeDashboard"/>'> <spring:message code="application.dashboard" />
			</a></li>
			<%-- <c:url value="/buyer/listBuyerSettings" var="listUrl"  />
					<li><a href="${listUrl }"><spring:message code="buyersettings.list"/></a></li> --%>
			<li class="active">
				<%-- <c:out  value='${btnValue}'/>  --%> Finance Company Settings
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<%-- <spring:message code="buyersettings.administration" /> --%>
				Finance Company Settings
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					Finance Company Settings
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<form:form id="financeSettingsForm" data-parsley-validate="" cssClass="form-horizontal bordered-row" modelAttribute="financeSettings" commandName="financeSettings" method="post" action="financeSettings">
					<header class="form_header"> </header>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<form:hidden path="id" />
					
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="timeZone" for="idTimeZone" class="marg-top-10">
								<spring:message code="label.timezone" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="timezone.required" var="required" />
							<form:select path="timeZone" id="idTimeZone" cssClass="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<form:option value="">
									<spring:message code="buyersettings.selecttimezone" />
								</form:option>
								<form:options items="${timeZone}" itemValue="id" itemLabel="fullTimeZone"></form:options>
							</form:select>
							<form:errors path="timeZone" cssClass="error" />
						</div>
					</div>
					
					
					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
								<form:button type="submit" id="financeSettings" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">${not empty financeSettings.id ? 'Update' : 'Save' }</form:button>
							<c:url value="/finance/financeDashboard" var="financeDashboard" />
							<a href="${financeDashboard}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
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
	var allowedFields = '.button-previous, #dashboardLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>
	$.validate({
		lang : 'en'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$('#idTimeZone').chosen({ search_contains: true });
		$('#idtimeZone').mask('GMT+00:00', {
			placeholder : "<spring:message code="timezone.placeholder"/>"
		});
	});
</script>
