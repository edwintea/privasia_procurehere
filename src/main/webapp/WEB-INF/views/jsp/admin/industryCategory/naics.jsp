<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('INDUSTRY_CATEGORY_EDIT') or hasRole('ADMIN')" var="canEdit" />
<spring:message var="naicsCreateDesk" code="application.owner.naics.code.create" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${naicsCreateDesk}] });
});
</script>
<div id="page-content">
	<div class="col-md-offset-1 col-md-10">
		<ol class="breadcrumb">
			<li>
				<a href="#">
					<spring:message code="application.dashboard" />
				</a>
			</li>
			<c:url value="/admin/naicsList" var="listUrl" />
			<li>
				<a href="${listUrl }">
					<spring:message code="naics.list" />
				</a>
			</li>
			<li class="active">
				<c:out value='${btnValue}' />
				<spring:message code="label.naics" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="label.naics" />
			</h2>
		</div>
		<!-- page title block -->
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="label.naics" />
					</
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<c:url var="saveCat" value="saveCat" />
				<form:form id="saveCatForm" commandName="naicsCode" action="naics" method="post" cssClass="form-horizontal bordered-row">
					<header class="form_header"> </header>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<form:hidden path="id" />
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="categoryCode" cssClass="marg-top-10">
								<spring:message code="naics.catagory.code" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="naics.placeholder" var="place" />
							<spring:message code="naics.code.required" var="required" />
							<spring:message code="naics.code.length" var="length" />
							<spring:message code="naics.code.number" var="num" />
							<form:input path="categoryCode" data-validation-length="max7" data-validation="required length number" data-validation-error-msg-length="${length}" data-validation-error-msg-number="${num}" data-validation-error-msg-required="${required}"
								cssClass="form-control" id="idcategoryCode" placeholder="${place} " readonly="true" />
							<form:errors path="categoryCode" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="categoryName" cssClass="marg-top-10">
								<spring:message code="naics.catagory.name" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="naics.name.placeholder" var="place" />
							<spring:message code="naics.name.required" var="required" />
							<spring:message code="naics.name.length" var="length" />
							<form:input path="categoryName" data-validation-length="1-128" data-validation="required length" data-validation-error-msg-length="${length}" data-validation-error-msg-required="${required}" cssClass="form-control" id="idcategoryName"
								placeholder="${place}" readonly="true" />
							<form:errors path="categoryName" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="parent" class="marg-top-10">
								<spring:message code="naics.parent" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:select path="parent" id="parent" cssClass="chosen-select">
								<form:option value="">
									<spring:message code="application.selectparent" />
								</form:option>
								<form:options items="${parentList}" itemValue="id" itemLabel="categoryName"></form:options>
							</form:select>
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
								<form:options items="${statusList}"></form:options>
							</form:select>
						</div>
					</div>
					<div class="form-group  col-md-12" style="text-align: center;">
						<div class="col-sm-6 col-md-12">
							<input type="submit" value="${btnValue2}" id="saveCat" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ${canEdit ? '':'disabled'}">
							<c:url value="/admin/naicsList" var="createUrl" />
							<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium">
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
	$.validate({ lang : 'en' });
</script>
<!--   <script type="text/javascript" src="<c:url value="/resources/js/view/naics.js"/>"></script> -->
