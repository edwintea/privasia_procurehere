<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('ROLE_SUPPLIER_TAG_EDIT') or hasRole('ADMIN')" var="canEdit" />
<title><spring:message code="suppliertags.title" /></title>
<spring:message var="suppliertagsDesk" code="application.buyer.supplier.tags" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierTagsDesk}] });
});
</script>
<div id="page-content">
	<div class="col-md-offset-1 col-md-10">
		<ol class="breadcrumb">
			<c:url value="/buyer/buyerDashboard" var="dashboardUrl" />
			<li><a id="dashboardLink" href="${dashboardUrl}"><spring:message code="application.dashboard" /></a></li>
			<c:url value="/buyer/listSupplierTags" var="listUrl" />
			<li><a id="listLink" href="${listUrl}"><spring:message code="suppliertags.list" /></a></li>
			<li class="active"><c:out value='${btnValue}' /> <spring:message code="label.suppliertags" /></li>
		</ol>
		<!-- page title block -->
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap line_icon">
				<spring:message code="suppliertags.administration" />
			</h2>
		</div>
		<div class="col_12 graph">
			<div class="white_box_brd pad_all_15">
				<section class="index_table_block">
					<div class="row">
						<div class="col-xs-12 ">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="Invited-Supplier-List import-supplier white-bg">
								<div class="meeting2-heading">
									<h3>
										<c:out value='${btnValue}' />
										<spring:message code="label.suppliertags" />
									</h3>
								</div>
								<div class="import-supplier-inner-first-new pad_all_15 global-list">
									<form:form cssClass="form-horizontal" action="supplierTags" method="post" modelAttribute="supplierTags" id="idsupplierTags">
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="supplierTags" cssClass="marg-top-10">
													<spring:message code="label.supplier.tagName" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="suppliertags.placeholder.suppliertags" var="place" />
												<spring:message code="suppliertags.required.suppliertags" var="required" />
												<spring:message code="suppliertags.length.suppliertags" var="length" />
												<form:hidden path="id" name="id" />
												<form:input path="supplierTags" type="text" autocomplete="off" data-validation-length="1-64" data-validation="required length custom" data-validation-regexp="^[^\s][a-zA-Z0-9\s-]+$" data-validation-error-msg-required="${required}" data-validation-error-msg-length="${length}" cssClass="form-control"
													id="idsuppliertags" placeholder="${place}" />
												<form:errors path="supplierTags" cssClass="error" />
											</div>
										</div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<form:label path="description" cssClass="marg-top-10">
													<spring:message code="label.suppliertags.description" />
												</form:label>
											</div>
											<div class="col-md-5">
												<spring:message code="suppliertags.placeholder.description" var="place" />
												<spring:message code="suppliertags.required.description" var="required" />
												<spring:message code="suppliertags.length.description" var="length" />
												<form:textarea path="description" data-validation-length="max128" data-validation="length " data-validation-allowing=" _-" data-validation-error-msg-required="${required}" data-validation-error-msg-length="${length}" cssClass="form-control"
													id="idDescription" autocomplete="off" placeholder="${place}" value="${description}" />
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
												<spring:message code="application.status.required" var="required" />
												<form:select path="status" cssClass="form-control chosen-select" id="idStatus" data-validation="required" data-validation-error-msg-required="${required}">
													<form:options items="${statusList}" />
												</form:select>
											</div>
										</div>
										<div class="clear"></div>
										<div class="row marg-bottom-20">
											<div class="col-md-3">
												<label class="marg-top-10"></label>
											</div>
											<div class="col-md-9 dd sky mar_b_15">
												<input type="submit" value="${btnValue}" id="saveSuppliertags" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ${canEdit and !buyerReadOnlyAdmin ? '':'disabled'}"">
												<c:url value="/buyer/listSupplierTags" var="listUrl" />
												<a href="${listUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"><spring:message code="application.cancel" /></a>
											</div>
										</div>
									</form:form>
								</div>
							</div>
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
		</script>