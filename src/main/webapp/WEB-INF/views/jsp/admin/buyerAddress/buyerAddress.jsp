<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="buyerAddrDesk" code="application.buyer.address" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerAddrDesk}] });
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
		<ol class="breadcrumb">
			<li>
				<a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard">
					<spring:message code="application.dashboard" />
				</a>
			</li>
			<c:url value="/buyer/listBuyerAddress" var="createUrl" />
			<li>
				<a id="listLink" href="${createUrl} ">
					<spring:message code="buyeraddress.list" />
				</a>
			</li>
			<li class="active">
				<c:out value='${btnValue2}' />
				<spring:message code="buyeraddress.title" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="buyeraddress.administration" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<c:out value='${btnValue2}' />
					<spring:message code="buyeraddress.title" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<!-- 	------ form start  here--------- -->
				<c:url var="saveBuyerAddress" value="saveBuyerAddress" />
				<form:form id="buyerAddressRegisterForm" commandName="buyerAddressObject" action="buyerAddress" method="post" cssClass="form-horizontal bordered-row">
					<form:hidden path="id" />
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="title" cssClass="marg-top-10">
								<spring:message code="buyeraddress.caption.title" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="buyeraddress.caption.title.placeholder" var="place" />
							<spring:message code="buyeraddress.title.required" var="required" />
							<spring:message code="buyeraddress.title.length" var="length" />
							<form:input path="title" data-validation-length="1-128" data-validation="required length" cssClass="form-control" id="idBuyerAddress" placeholder="${place}" />
							<form:errors path="title" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="line1" cssClass="marg-top-10">
								<spring:message code="application.addr1" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="buyeraddress.line1.placeholder" var="place" />
							<spring:message code="buyeraddress.line1.required" var="required" />
							<spring:message code="buyeraddress.line1.length" var="length" />
							<form:input path="line1" data-validation-length="1-250" data-validation="required length" cssClass="form-control" id="idBuyerAddress" placeholder="${place}" />
							<form:errors path="line1" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="line2" cssClass="marg-top-10">
								<spring:message code="application.addr2" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="buyeraddress.line2.placeholder" var="place" />
							<form:input path="line2" cssClass="form-control" id="idBuyerAddress" placeholder="${place}" />
							<form:errors path="line2" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="city" cssClass="marg-top-10">
								<spring:message code="application.city" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="buyeraddress.city.placeholder" var="place" />
							<spring:message code="application.city.required" var="required" />
							<spring:message code="buyeraddress.length.required" var="length" />
							<form:input path="city" data-validation-length="1-250" data-validation="required length" cssClass="form-control" id="idBuyerAddress" placeholder="${place}" />
							<form:errors path="city" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="city" cssClass="marg-top-10">
								<spring:message code="application.country" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="application.country.required" var="cntryreq" />
							<form:select path="country" data-validation="required" cssClass="form-control chosen-select" id="idCountry">
								<form:option value="">
									<spring:message code="application.selectcountry" />
								</form:option>
								<form:options items="${countrys}" itemValue="id" itemLabel="countryName" />
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="zip" cssClass="marg-top-10">
								<spring:message code="application.state" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="buyeraddress.state.required" var="required" />
							<form:select path="state" data-validation="required" cssClass="form-control chosen-select" id="stateList">
								<form:option value="">
									<spring:message code="application.selectstate" />
								</form:option>
								<form:options items="${states}" itemValue="id" itemLabel="stateName" />
							</form:select>
						</div>
						<ul class="add_more_feture_ul"></ul>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="zip" cssClass="marg-top-10">
								<spring:message code="application.zip" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="buyeraddress.zip.placeholder" var="place" />
							<spring:message code="buyeraddress.zip.required" var="required" />
							<spring:message code="buyeraddress.zip.length" var="length" />
							<form:input path="zip" data-validation-length="2-15" data-validation="required length" data-validation-allowing="\ "  cssClass="form-control" id="idBuyerAddress" placeholder="${place}" />
							<form:errors path="zip" cssClass="error" />
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
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
							<sec:authorize access="hasRole('BUYER_ADDRESS_EDIT') or (hasRole('BUYER') and hasRole('ADMIN'))" var="access" />
							<button  id="saveBuyerAddress" ${access ? "" : "disabled='disabled'" }
							class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out"> ${btnValue2} </button>
							
							<c:url value="/buyer/listBuyerAddress" var="createUrl" />
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
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js?1"/>"></script>
<script>

<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '.button-previous, #dashboardLink, #listLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>
	$.validate({ lang : 'en'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/buyerAddress.js"/>"></script>
