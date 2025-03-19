<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="prTemplateDesk" code="application.pr.template" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${prTemplateDesk}] });
});
</script>
<div id="page-content" view-name="rfxTemplate">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
			<li><a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" />
			</a></li>
			<c:url value="/buyer/prTemplateList" var="createUrl" />
			<li><a id="listLink" href="${createUrl} "> <spring:message code="sourcing.template.list" /></a></li>
			<li class="active"><c:out value='${btnValue}' /><spring:message code="sourcing.form.template.label" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon"><spring:message code="sourcing.form.administration" /></h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="sourcing.form.template.label" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list white-bg">
				<c:url var="saveRfxTemplate" value="saveRfxTemplate" />
				<form:form id="sourcetemplate" cssClass="form-horizontal bordered-row" method="post" action="${pageContext.request.contextPath}/buyer/saveSourcingFormTemplate" modelAttribute="sourceForm">
						<form:input path="id" type="hidden"></form:input>
					<div class="row marg-bottom-20 marg_left_0 white-bg">
						<div class="col-md-3">
							<form:label path="" cssClass="marg-top-10">
								<spring:message code="formdetails.form.name" />
							</form:label>

						</div>
						<form:hidden path="id" />
						<div class="col-md-5 white-bg">
							<spring:message code="sourcing.enter.form.placeholder"  var="entername"/>
							<form:input path="formName" data-validation-length="1-128" data-validation="required length" cssClass="form-control" id="idRfxTemplateName" placeholder="${entername} " />
						</div>
					</div>
					<div class="row marg-bottom-20 marg_left_0 white-bg">
						<div class="col-md-3">
							<form:label path="" cssClass="marg-top-10">
								<spring:message code="formdetails.form.description.label" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="sourcing.description.placeholder"  var="enterDesc"/>
							<form:textarea path="description" class="form-control textarea-autosize" data-validation-length="0-250" data-validation="length" cssClass="form-control" id="idRfxTemplateDescription" placeholder="${enterDesc}" />
							<span class="sky-blue"><spring:message code="sourcing.max.charactes.only" /></span>
						</div>
					</div>

					<div class="clear"></div>
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20"></div>

					<div class="row" style="margin-left: 40px">
						<div class="upload_download_wrapper collapseable  clearfix marg-top-10 event_info Approval-tab">
							<jsp:include page="sourcingFormApproval.jsp" />
						</div>
					</div>
					<div class="row" style="margin-left: 40px">
						<div class="meeting2-heading col-sm-4 col-lg-4 col-md-4">
							<!-- <!-- <h3>Create Questionnaire</h3> --> -->
						</div>
						<div>
							<button class="btn btn-info hvr-pop toggleCreateBq marg-left-20 marg-right-20 marg-top-10 marg-bottom-10 pull-right" type="button" data-toggle="tooltip" data-original-title="Add Questionnaire" style="margin-right: 1080px;"><spring:message code="questionnaire.add.button" /></button>
						</div>
						<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle" style="display: none;">

							<jsp:include page="SourcingFormAddCq.jsp" />

						</div>
					</div>


					<div class="row marg-bottom-202">
						<!-- <div class="col-md-3">
							<label class="marg-top-10"></label>
						</div> -->


						<div class="col-md-9 btns-lower marg-top-20">
							<form:button type="submit" id="saveRfxTemplate" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" style=" margin-left: 16px;"><spring:message code="application.save" /></form:button>

						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>


<style>
.radio>span {
	padding: 0 !important;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
<c:if test="${assignedCount > 0} ">
$(window).bind('load', function() {
	var allowedFields = '#idRfxTemplateName, #idRfxTemplateDescription, #idStatus, #saveAsPrTemplate, #saveRfxTemplate, #cancelId, #dashboardLink, #listLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>
<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '#cancelId, #dashboardLink, #listLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>

	$.validate({
		lang : 'en'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/sourceformCq.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/prTemplate.js"/>"></script>