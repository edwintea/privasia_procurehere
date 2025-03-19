
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<spring:message var="scoreRatingCreateDesk" code="application.score.rating.create" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${scoreRatingCreateDesk}] });
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
<div id="page-content" view-name="scoreRating">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
			</a></li>
			<c:url value="/admin/scoreRating/scoreRatingList" var="listUrl" />
			<li><a id="listLink" href="${listUrl}"> <spring:message code="score.rating.list" />
			</a></li>
			<li class="active"><c:out value='${btnValue}' /> <spring:message code="defaultmenu.score.rating" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="defaultmenu.score.rating" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="defaultmenu.score.rating" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<c:url var="createScoreRating" value="createScoreRating" />
				<form:form id="scoreRatingCreate" cssClass="form-horizontal bordered-row" method="post" action="createScoreRating" autocomplete="off" modelAttribute="scoreRatingPojo">
					<form:hidden path="id" />
					<header class="form_header"> </header>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="minScore" cssClass="marg-top-10">
								<spring:message code="label.minscore" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="score.rating.min.placeholder" var="place" />
							<spring:message code="score.rating.min.required" var="required" />
							<form:input path="minScore" value="${minScore}" data-validation="required number validateMinScore custom" data-validation-allowing="range[0;999]" data-validation-error-msg-required="${required}" data-validation-regexp="^\d{1,10}$" data-validation-length="1-3" maxlength="3" cssClass="form-control " id="idMinScore" placeholder="${place}" />
							<form:errors path="minScore" cssClass="error" />

						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="maxScore" cssClass="marg-top-10">
								<spring:message code="label.maxscore" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="score.rating.max.placeholder" var="place" />
							<spring:message code="score.rating.max.required" var="required" />
							<form:input path="maxScore" value="${maxScore}" data-validation="required number validateMaxScore custom" data-validation-allowing="range[1;999]" data-validation-error-msg-required="${required}" data-validation-regexp="^\d{1,10}$" data-validation-length="1-3" maxlength="3" cssClass="form-control " id="idMaxScore" placeholder="${place}" />
							<form:errors path="maxScore" cssClass="error" />
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="rating" cssClass="marg-top-10">
								<spring:message code="label.rating" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="score.rating.placeholder" var="place" />
							<spring:message code="score.rating.required" var="required" />
							<form:input path="rating" value="${rating}" data-validation="required number custom" data-validation-error-msg-required="${required}" data-validation-allowing="range[1;99]" data-validation-regexp="^\d{1,10}$" data-validation-length="1-2" maxlength="2" cssClass="form-control " id="idRating" placeholder="${place}" />
							<form:errors path="rating" cssClass="error" />
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="description" cssClass="marg-top-10">
								<spring:message code="application.description" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="score.rating.description.placeholder" var="desc" />
							<spring:message code="score.rating.description.length" var="length" />
							<form:textarea path="description" cssClass="form-control" data-validation-length="0-16" data-validation="length" maxlength="16" data-validation-allowing=" &/_,-" id="idDescription" placeholder="${desc}" />
							<span class="sky-blue"><spring:message code="score.rating.valid.max3.characters" /></span>
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
							<sec:authorize access="hasRole('SCORE_RATING_EDIT') or hasRole('ADMIN')" var="canEdit" />
							<form:button type="submit" value="save" id="createScoreRating" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out " disabled="${!canEdit}">${btnValue}</form:button>
							<c:url value="/admin/scoreRating/scoreRatingList" var="createUrl" />
							<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
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
	
	$.formUtils.addValidator({
		name : 'validateMaxScore',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var minValue=$("#idMinScore").val();
			 var maxValue=$("#idMaxScore").val();
			 if(maxValue != '' && minValue != '') {
				 if(parseFloat($("#idMaxScore").val()) <= parseFloat($("#idMinScore").val()) && parseFloat($("#idMaxScore").val()) == parseFloat($("#idMinScore").val())){
					 response = false;
				 }
			 }
			return response;
		},
		errorMessage : 'Maximum Score should be more than Minimum Score',
		errorMessageKey : 'badMinMax'
	});

	$.formUtils.addValidator({
		name : 'validateMinScore',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var minValue=$("#idMinScore").val();
			 var maxValue=$("#idMaxScore").val();
			 if(maxValue != '' && minValue != '') {
				 if(parseFloat($("#idMinScore").val()) >= parseFloat($("#idMaxScore").val()) && parseFloat($("#idMaxScore").val()) != parseFloat($("#idMinScore").val())){
					 response = false;
				 }
			 }
			return response;
		},
		errorMessage : 'Minimum Score should be less than Maximum Score',
		errorMessageKey : 'badMinMax'
	});
	
	$('#createScoreRating').click(function(e){
		e.preventDefault();
		if($('#idDescription').val()){
			$('#idDescription').attr('data-validation','alphanumeric');
		}else{
			$('#idDescription').removeAttr('data-validation');
		}
		
		if($('#scoreRatingCreate').isValid()){
			$('div[id=idGlobalError]').hide();
			$('#scoreRatingCreate').submit();
			$('#loading').show();
		}
		
	});
	
</script>
