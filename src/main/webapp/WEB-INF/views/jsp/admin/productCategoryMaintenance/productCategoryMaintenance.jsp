<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('PRODUCT_CATEGORY_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="productCatgDesk" code="application.buyer.product.category" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${productCatgDesk}] });
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
<div id="page-content" view-name="productCategory">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard" /></a></li>
			<c:url value="/buyer/listProductCategory" var="createUrl" />
			<li><a id="listLink" href="${createUrl} ">  <spring:message code="Productz.form" />  </a></li>
			<li class="active">
			<c:out value='${btnValue2}' /> 
			<spring:message code="Productz.form" /> </li> 
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
			 <spring:message code="Product.pcma" /> 
				
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
				 <c:out value='${btnValue2}' />
				 <spring:message code="Product.pcm" />  
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<!-- 	------ form start  here--------- -->
				<form:form id="productCategoryMaintenanceForm" data-parsley-validate="" commandName="productCategoryMaintenance" action="productCategoryMaintenance" method="post" cssClass="form-horizontal bordered-row">
					<form:hidden path="id" />
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="productCode" cssClass="marg-top-10">
								<spring:message code="product.code" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="product.code.placeholder" var="desc" />
							<spring:message code="product.code.required" var="required" />
							<spring:message code="product.code.length" var="length" />
							<form:input path="productCode" data-validation-length="1-64" data-validation="required length" data-validation-error-msg-required="This is a required field" data-validation-error-msg-length="${length}" cssClass="form-control" id="idProductCode"
								placeholder="${desc} " />
							<form:errors path="productCode" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="productName" cssClass="marg-top-10">
								<spring:message code="product.name" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="product.name.placeholder" var="name" />
							<spring:message code="product.name.required" var="required" />
							<spring:message code="product.name.length" var="length" />
							<form:input path="productName" data-validation="required length" data-validation-length="1-128" cssClass="form-control" id="idProductName" placeholder="${name}" />
							<form:errors path="productName" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label for="idStatus" class="marg-top-10"> <spring:message code="application.status" /></label>
						</div>
						<div class="col-md-5">
												<spring:message code="product.status.required" var="required" />
												<form:select path="status" cssClass="form-control chosen-select" id="idStatus">
													<form:options items="${statusList}" />
												</form:select>
												<form:errors path="status" cssClass="error" />
											</div>
					</div>
					
					
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
							<input type="submit" value="${btnValue2}" id="saveProductCategoryMaintenance" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" ${!canEdit ? "disabled='disabled'" : ""}>
							<c:url value="/buyer/listProductCategory" var="createUrl" />
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
<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '.button-previous, #dashboardLink, #listLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>

	$.validate({ lang : 'en' });
</script>
