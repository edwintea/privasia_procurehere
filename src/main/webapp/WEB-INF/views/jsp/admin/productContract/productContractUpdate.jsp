<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<sec:authentication property="principal.id" var="loggedInUserId" />
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ROLE_CONTRACT_EDIT')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_CONTRACT_VIEW_ONLY')" var="buyerReadOnlyAdmin" />
<jsp:useBean id="today" class="java.util.Date" scope="request" />
<spring:message var="productListDesk" code="application.buyer.product.list" />

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">

<style>
.select-abs {
	border: 1px solid #757575 !important;
	border-radius: 50% !important;
	color: #757575;
	font-size: 17px;
	opacity: 1;
	padding: 1px 4px !important;
	right: 5px;
	top: 3px;
	outline: none;
	position: relative !important;
	margin-top: 28px;
	margin-right: -19px;
}

.select-abs:hover {
	opacity: 1 !important;
}

.deleleteDocument>i.fa {
	font-size: 18px;
	color: #7f7f7f;
	padding-top: 0;
	margin-top: 13px;
	margin-right: -5px;
}

.deleteLoaDocument>i.fa {
	font-size: 18px;
	color: #7f7f7f;
	padding-top: 0;
	margin-top: 13px;
	margin-right: -5px;
}

.deleleteAgreementDocument>i.fa {
	font-size: 18px;
	color: #7f7f7f;
	padding-top: 0;
	margin-top: 13px;
	margin-right: -5px;
}

.border_box {
	border: 1px #ccc solid;
	background-color: #F8F8F8;
}

.closePopUp {
	margin-top: -120px;
	margin-right: -30px;
}

/* Team member type menu start */
.dropdown-menu a.small label {
    padding-top: 4px !important;
    padding: 2px 2px !important;
}

.dropdown-menu .checker {
	margin-right: 0px !important;
}

.dropdown-menu > li > a {
	padding-left: 10px !important;
}

#appUsersList .dropdown-menu {
    min-width: 150px !important;
}

.cqa_del {
    width: 50%;
    float: left;
}
.adm_menu_link:hover {
    background: #e9e9e9;
}
.tableApp>tbody>tr>td {
    padding: 5px !important;
}
.input-group.mrg15T.mrg15B {
    background-color: #f5f5f5;
    margin-bottom: 0;
    padding: 12px;
}
.advancee_menu {
    width: 10%;
    float: right;
    text-align: center;
}
.advancee_menu ul {
    position: absolute;
    left: 60px;
    top: 0;
    float: left;
    list-style: none;
    padding: 0px;
    z-index: 1;
}
.advancee_menu ul {
    top: 100% !important;
    left: 0 !important;
}
.open>.dropdown-menu {
    padding-bottom: 17px;
    padding-top: 0;
}
.adm_box {
    width: 50px;
    height: 50px;
    float: right;
    position: relative;
}
.adm_menu_link {
    width: 50px;
    height: 50px;
    border-radius: 50px;
    float: left;
    text-align: center;
    padding: 15px 0 0 0;
    transition: all 0.8s ease;
}
/* Team member type menu end */

.reminderInterval span {
	margin-top: 47px;
	margin-left: -70px;
}

.modal-body input {
	margin: 0;
}
#appUsersList tr:nth-child(odd) {
	background: #FFF
}

#appUsersList tr:nth-child(even) {
	background: #f5f5f5
}
.mem-tab {
	border: 1px solid #ddd;
	border-radius: 2px;
	float: left;
	height: 300px;
	overflow: auto;
	position: relative;
	width: 100%;
}
#saveProductListMaintenance {
	float: left;
	margin-left: 10px;
}

.draft {
	float: left;
	margin-left: 10px;
}


#agrDate {
color: #2b2f33 !important;
cursor: auto !important;
}

#loaDate {
color: #2b2f33 !important;
cursor: auto !important;
}

</style>

<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${productListDesk}] });
});
</script>
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
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

.col-custom-md-7 {
	width: 54.333333%;
}

.col-custom-md-4 {
	width: 38.333333%;
}

.col-custom-md-1 {
	width: 3.333333%;
	margin-top: 10px;
}

.ph_table_border {
	border: 1px solid #e8e8e8;
	border-radius: 2px;
}

.ph_btn_small {
	height: 35px;
	min-width: 100px;
	line-height: 35px;
}
.select-radio-lineHgt {
	display: inline-flex;
	align-items: center;
}

.select-radio-lineHgt input[type="radio"] {
	margin-right: 5px; /* Adjust the spacing between the input and label */
}

</style>
<script src="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.js"/>"></script>
<link rel="stylesheet" href="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.min.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/multiselect/multi-select.css"/>">
<!--Side Bar End  -->

<div id="page-content" view-name="product-contract" class="white-bg">
	<div class="container">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
			</a></li>
			<c:url value="/buyer/productContractList" var="listUrl" />
			<li><a id="listLink" href="${listUrl} "> <spring:message code="product.contracts.list.dashboard" />
			</a></li>
			<li class="active"><spring:message code="Productz.list.contract" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="Productz.list.contract" />
			</h2>
			<h2 class="trans-cap pull-right">
				<spring:message code="application.status" />
				: ${productContract.status}
			</h2>
		</div>
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<div >
			<jsp:include page="contractHeader.jsp"></jsp:include>
		</div>
				<!-- 	------ form start  here--------- -->
		<form:form id="productListMaintenanceForm" data-parsley-validate="" commandName="productContract" action="saveProductContract?${_csrf.parameterName}=${_csrf.token}" method="post" cssClass="form-horizontal bordered-row" enctype="multipart/form-data">
			<input type="hidden" value="${productContract.id}" id="id" name="id" />
			<input type="hidden" value="${loggedInUserId}" id="loggedInUserId" />
			<input type="hidden" value="${loaAndAgrcontractDocument.id}" id="loaAndAgreementId" />
					
    <!-- Contract Information -->
			<div class="Invited-Supplier-List import-supplier white-bg">
				<div class="meeting2-heading">
					<h3>
						<spring:message code="product.contract.information" />
					</h3>
				</div>
				<div class="import-supplier-inner-first-new pad_all_15 global-list marg-top-10">
					<c:if test="${not empty productContract.id and not empty productContract.contractId}">
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="contractId" cssClass="marg-top-10">
									<spring:message code="product.Contract.Id" />
								</form:label>
							</div>
							<div class="col-md-5 ${ buyerReadOnlyAdmin ? 'disabled':''}">
								<spring:message code="contract.id.required" var="required" />
								<spring:message code="contract.id.length" var="length" />
								<spring:message code="product.id.placeholder" var="productId" />
								<form:input path="contractId" readonly="true" cssClass="form-control " id="idProductCode" placeholder="${productId}" />
								<form:errors path="contractId" cssClass="error" />
							</div>
						</div>
					</c:if>

						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="contractId" cssClass="marg-top-10">
									<spring:message code="product.contract.name" />
								</form:label>
							</div>
							<div class="col-md-5 ${ (buyerReadOnlyAdmin || !productContract.isEditable) ? 'disabled':''}">
								<spring:message code="contract.event.length" var="length" />
								<spring:message code="contract.name.placeholder" var="contractName" />
								<form:input path="contractName" data-validation="required length" data-validation-length="1-250" readonly="${(productContract.status == 'TERMINATED') ? 'true' : ''}" cssClass="form-control " id="idProductCode" placeholder="${contractName}" />
								<form:errors path="contractName" cssClass="error" />
							</div>
						</div>

					<c:if test="${not empty productContract.id and not empty productContract.eventId}">
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="contractId" cssClass="marg-top-10">
									<spring:message code="product.contract.event.id" />
								</form:label>
							</div>
							<div class="col-md-5 ">
								<spring:message code="contract.event.placeholder" var="eventId" />
								<div class="form-control">${productContract.eventId}</div>
								<form:errors path="eventId" cssClass="error" />
							</div>
						</div>
					</c:if>

					<c:if test="${not empty productContract.createdBy}">
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<label cssClass="marg-top-10"> <spring:message code="product.Contract.creator" />
								</label>
							</div>
							<div class="col-md-5 ${ buyerReadOnlyAdmin ? 'disabled':''}">
								<spring:message code="systemsetting.enter.email.placeholder" var="email" />
								<div class="form-control">${productContract.contractCreator.name} (${productContract.contractCreator.loginId})</div>
								<form:errors path="createdBy" cssClass="error" />
							</div>
						</div>
					</c:if>

					<c:if test="${not empty productContract.sapContractNumber and productContract.status ne 'DRAFT'}">
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="contractId" cssClass="marg-top-10">
									<spring:message code="product.contract.sap.number" />
								</form:label>
							</div>
							<div class="col-md-5 ${ buyerReadOnlyAdmin ? 'disabled':''}">
								<spring:message code="product.contract.sap.number" var="eventId" />
								<%-- <form:input path="sapContractNumber" readonly="true" data-validation-length="1-10" cssClass="form-control " id="idProductCode" placeholder="${eventId}" /> --%>
								<div class="form-control">${productContract.sapContractNumber}</div>
								<form:errors path="sapContractNumber" cssClass="error" />
							</div>
						</div>
					</c:if>
				</div>
			</div>
			
         <!-- Contract Details -->
			<div class="Invited-Supplier-List import-supplier white-bg marg-top-10">
				<div class="meeting2-heading">
					<h3>
						<spring:message code="product.contract.details" />
					</h3>
				</div>
				<div class="import-supplier-inner-first-new pad_all_15 global-list">
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="renewalContract" cssClass="marg-top-10">
								<spring:message code="contract.renewal" />
							</form:label>
						</div>
						<div class="col-md-5 ${ buyerReadOnlyAdmin || (productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED') ? 'disabled':''}">
							<form:checkbox path="renewalContract" cssClass="custom-checkbox" />
							<label style="line-height: 0px;"><spring:message code="contract.renewal.yes" /></label>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="contractReferenceNumber" cssClass="marg-top-10">
								<spring:message code="product.Contract.code" />
							</form:label>
						</div>
						<div class="col-md-5 ${ buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':''}">
							<spring:message code="application.referencenumber" var="contractReferenceNumber" />
							<spring:message code="contract.reference.number.required" var="required" />
							<spring:message code="contract.reference.number.length" var="length" />
							<spring:message code="systemsetting.product.code.placeholder" var="productcode" />
							<form:input path="contractReferenceNumber" readonly="${(productContract.status == 'TERMINATED') ? 'true' : ''}" data-validation-length="1-64" data-validation="required length" data-validation-error-msg-required="${required}" data-validation-error-msg-length="${length}" cssClass="form-control " id="idProductCode" placeholder="${contractReferenceNumber}" />
							<form:errors path="contractReferenceNumber" cssClass="error" />
						</div>
					</div>

					<c:if test="${not empty productContract.id}">
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="contractId" cssClass="marg-top-10">
									<spring:message code="product.contract.previous.contract" />
								</form:label>
							</div>
							<div class="col-md-5 ${ buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':''}">
								<spring:message code="contract.previous.contract.placeholder" var="eventId" />
								<form:input path="previousContractNo" readonly="${(productContract.status == 'TERMINATED') ? 'true' : ''}" data-validation-length="0-32" data-validation="length" cssClass="form-control" placeholder="${eventId}" />
								<form:errors path="previousContractNo" cssClass="error" />
							</div>
						</div>
					</c:if>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="businessUnit" for="businessUnit" class="marg-top-10">
								<spring:message code="label.businessUnit" />
							</form:label>
						</div>
						<div id="idBusinessUnitDiv" class="col-md-5 ${( buyerReadOnlyAdmin or productContract.idBasedOnBusinessUnit or productContract.status == 'ACTIVE' or productContract.status == 'SUSPENDED') ? 'disabled':''}">
							<spring:message code="business.unit.empty" var="required" />
							<form:select path="businessUnit" readonly="${(productContract.status == 'TERMINATED' or not empty productContract.eventId) ? 'true' : ''}" id="chosenBusinessUnit" cssClass="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<form:option value="">
									<spring:message code="pr.select.business.unit" />
								</form:option>
								<c:forEach items="${businessUnit}" var="unitData">
									<c:if test="${empty unitData.id}">
										<option value="" disabled>${unitData.unitName}</option>
									</c:if>
									<c:if test="${!empty unitData.id and unitData.id != productContract.businessUnit.id}">
										<option value="${unitData.id}">${unitData.unitName}</option>
									</c:if>
									<c:if test="${!empty unitData.id and unitData.id == productContract.businessUnit.id}">
										<option value="${unitData.id}" selected>${unitData.unitName}</option>
									</c:if>
								</c:forEach>
							</form:select>
						</div>
					</div>

					<!-- Group Code -->
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="groupCode" for="groupCode" class="marg-top-10">
								<spring:message code="product.Contract.groupCode" />
							</form:label>
						</div>
						<div class="col-md-5 ${ buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':''}">
							<spring:message code="contract.group.code.empty" var="required" />
							<form:select path="groupCode" readonly="${(productContract.status == 'TERMINATED') ? 'true' : ''}" id="chosenGroupCode" cssClass="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<form:option value="">
									<spring:message code="contract.select.group.Code" />
								</form:option>
								<c:forEach items="${groupCodeList}" var="groupCode">
									<c:if test="${empty groupCode.id}">
										<option value="" disabled>${groupCode.groupCode}- ${groupCode.description}</option>
									</c:if>
									<c:if test="${!empty groupCode.id and groupCode.id != productContract.groupCode.id}">
										<option value="${groupCode.id}">${groupCode.groupCode}- ${groupCode.description}</option>
									</c:if>
									<c:if test="${!empty groupCode.id and groupCode.id == productContract.groupCode.id}">
										<option value="${groupCode.id}" selected>${groupCode.groupCode}- ${groupCode.description}</option>
									</c:if>
								</c:forEach>
							</form:select>
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="form-tander1 pad_all_30">
							<div class="col-md-3">
								<label for="idpc" class="marg-top-10">
									<spring:message code="Productz.favoriteSupplier" />
								</label>
							</div>
							<div class="col-md-5 ${!productContract.isEditable ? 'disabled' : ''}">
								<div class="radio-info pull-left marg-right-10 marg_top_15">
									<label class="select-radio-lineHgt"> <input type="radio" name="supplierChoice" id="supplierChoice" checked="checked" class="custom-radio showSupplierBlocks" value="LIST" /> <spring:message code="prtemplate.radio.my.supplier" />
									</label>
								</div>
								<div class="radio-info pull-left marg-left-10 marg_top_15">
									<label class="select-radio-lineHgt"> <input type="radio" name="supplierChoice" id="supplierChoice" class="custom-radio showSupplierBlocks" value="MANUAL"
										<c:if test="${not empty productContract.eventId}">
											disabled="disabled"
										</c:if>
										/>
										<spring:message code="prtemplate.radio.open.supplier" />
									</label>
								</div>
							</div>
						</div>

					<div class="col-md-3"></div>
					<div class="col-md-5 ${(buyerReadOnlyAdmin or !empty productContract.eventId or productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED') ? 'disabled' : ''}">
						<div class="showPartMANUAL supplierBoxes"  style="margin-top: 10px;">
								<spring:message code="product.favouriteSupplier.Name" var="supplierName" />
								<form:input path="supplierName" type="text" placeholder="${supplierName}" id="chosenSupplierName" data-validation="required length alphanumeric" data-validation-length="max128" data-validation-allowing="',-_&.()\/ " cssClass="form-control "  />
							<form:errors path="supplierName" cssClass="error" />
						</div>
						<div class="form_field showPartLIST supplierBoxes" style="margin-top: 10px;">
							<form:select path="supplier" readonly="${(productContract.status == 'TERMINATED' or not empty productContract.eventId) ? 'true' : ''}" id="chosenSupplier" class="chosen-select">
								<form:option value="">
									<spring:message code="Product.favoriteSupplier" />
								</form:option>
								<c:forEach items="${favSupp}" var="supp">
									<c:if test="${empty supp.id}">
										<option value="" disabled>${supp.companyName}</option>
									</c:if>
									<c:if test="${!empty supp.id and supp.id != productContract.supplier.id}">
										<option value="${supp.id}">${supp.companyName}</option>
									</c:if>
									<c:if test="${!empty supp.id and supp.id == productContract.supplier.id}">
										<option value="${supp.id}" selected>${supp.companyName}</option>
									</c:if>
								</c:forEach>
							</form:select>
						</div>
					</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="procurementCategory" for="procurementCategory" class="marg-top-10">
								<spring:message code="contract.category" />
							</form:label>
						</div>
						<div id="idProcurementCategoryDiv" class="col-md-5 ${ buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':''}">
							<form:select path="procurementCategory" readonly="${(productContract.status == 'TERMINATED') ? 'true' : ''}" id="chosenProcurementCategory" cssClass="chosen-select" >
								<form:option value="">
									<spring:message code="contract.select.category" />
								</form:option>
								<c:forEach items="${procurementCategoryList}" var="category">
									<c:if test="${empty category.id}">
										<option value="" disabled>${category.procurementCategories}</option>
									</c:if>
									<c:if test="${!empty category.id and category.id != productContract.procurementCategory.id}">
										<option value="${category.id}">${category.procurementCategories}</option>
									</c:if>
									<c:if test="${!empty category.id and category.id == productContract.procurementCategory.id}">
										<option value="${category.id}" selected>${category.procurementCategories}</option>
									</c:if>
								</c:forEach>
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="agreementType" for="agreementType" class="marg-top-10">
								<spring:message code="label.agreement.type" />
							</form:label>
						</div>
						<div id="agreementType" class="col-md-5 ${ buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':''}">
							<form:select path="agreementType" readonly="${(productContract.status == 'TERMINATED') ? 'true' : ''}" id="chosenAgreementType" cssClass="chosen-select">
								<form:option value="">
									<spring:message code="contract.select.agreement" />
								</form:option>
								<c:forEach items="${agreementTypeList}" var="agreeType">
									<c:if test="${empty agreeType.id}">
										<option value="" disabled>${agreeType.agreementType} - ${agreeType.description}</option>
									</c:if>
									<c:if test="${!empty agreeType.id and agreeType.id != productContract.agreementType.id}">
										<option value="${agreeType.id}">${agreeType.agreementType} - ${agreeType.description}</option>
									</c:if>
									<c:if test="${!empty agreeType.id and agreeType.id == productContract.agreementType.id}">
										<option value="${agreeType.id}" selected>${agreeType.agreementType} - ${agreeType.description}</option>
									</c:if>
								</c:forEach>
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="remark" cssClass="marg-top-10">
								<spring:message code="product.contract.remark" />
							</form:label>
						</div>
						<div class="col-md-5 ${ productContract.isEditable ? (buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':'') : ''}">
							<textarea Class="form-control"  id="remark" name="remark" data-validation-optional="true" data-validation="length alphanumeric" data-validation-length="max1000" data-validation-allowing="',-_&.()\/ " rows="5" ></textarea>
							<span class="sky-blue"><spring:message code="dashboard.valid.max2.characters" /></span>
						</div>
					</div>
				</div>
			</div>

    <!-- Contract Period -->
			<div class="Invited-Supplier-List import-supplier white-bg marg-top-10">
				<div class="meeting2-heading">
					<h3>
						<spring:message code="product.contract.period" />
					</h3>
				</div>
				<div class="import-supplier-inner-first-new pad_all_15 global-list">

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="contractStartDate" cssClass="marg-top-10">
								<spring:message code="contract.start.date" />
							</form:label>
						</div>

						<div class="col-md-5 ${!productContract.isEditable ? 'disabled' : ''}">
							<div class="input-prepend input-group">
								<spring:message code="dateformat.placeholder" var="dateformat" />
								<form:input id="contractStartDateId" path="contractStartDate" class="form-control for-clander-view contractStartDate" data-fv-date-min="15/10/2016" 
									placeholder="${dateformat}" autocomplete="off" data-validation="required custom" data-validation-regexp="^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$"
									data-validation-error-msg-custom="Invalid date - Should be in dd/mm/yyyy format" />
								<div id="contract_dateErrorBlock"></div>
								<form:errors path="contractStartDate" cssClass="error" />
							</div>
						</div>
					</div>

					<div class="row marg-bottom-20 ${!productContract.isEditable ? 'disabled' : ''}">
						<div class="col-md-3">
							<form:label path="contractEndDate" cssClass="marg-top-10">
								<spring:message code="contract.end.date" />
							</form:label>
						</div>
						<div class="col-md-5">
							<div class="input-prepend input-group">
								<spring:message code="dateformat.placeholder" var="dateformat" />
								<form:input id="contractEndDateId" path="contractEndDate" class="bootstrap-datepicker form-control for-clander-view contractEndDate" 
									data-fv-date-min="15/10/2016" placeholder="${dateformat}" autocomplete="off" data-validation="required custom" data-validation-regexp="^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$"
									data-validation-error-msg-custom="Invalid date - Should be in dd/mm/yyyy format" />
								<div id="contract_dateErrorBlock"></div>
								<form:errors path="contractEndDate" cssClass="error" />
							</div>
						</div>
					</div>

					<!--reminder for contract  -->
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label for="idStatus" class="marg-top-10"><spring:message code="product.contract.reminder" /> </label>
						</div>
						<div class="col-md-5 ${ productContract.isEditable ? (buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':'') : ''}">
							<div class="">
								<a class="btn btn-info btn-tooltip gray hvr-pop hvr-rectangle-out ${ productContract.isEditable ? ( buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':'') : ''}" title='<spring:message code="add.reminder.placeholder" />' data-placement="top" id="idAddReminder" data-toggle="tooltip"> <img src="${pageContext.request.contextPath}/resources/images/ring_cion.png"> Add Reminder
								</a>
							</div>
							<div class="ph_table_border marg-top-10">
								<div class="reminderList marginDisable contractReminderList">
									<c:forEach items="${reminderList}" var="reminder">
										<div class="row reminderId " id="${reminder.id}">
											<input type="hidden" id="reminderDate" name="reminderDate" value="${reminder.reminderDate}"> <input type="hidden" name="remindMeDays" value="${reminder.interval}">
											<fmt:formatDate var="reminderDateTime" value="${reminder.reminderDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											<input type="hidden" id="reminderDateDel" class="reminderDateDel" value="${reminderDateTime}">
											<div class="col-md-10 reminders">
												<p id="reminderDate">
													<b>Reminder Date: </b>${reminderDateTime}</p>
											</div>
											<c:if test="${ !buyerReadOnlyAdmin}">
												<div class="col-md-2 ${canEdit ? '' : ' disabled'}">
													<a href="" class="removeContractReminder"> <i class="fa fa-times-circle"></i>
													</a>
												</div>
											</c:if>
										</div>
									</c:forEach>
								</div>
							</div>
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label for="idStatus" class="marg-top-10"><spring:message code="contract.reminder.notify.users" /> </label>
						</div>
						<div class="col-md-5 ${ productContract.isEditable ? ( buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':'') : ''}">
							<form:select style="margin-left: 7px;" id="notifyUsers" path="notifyUsers" cssClass="form-control  user-list-all chosen-select" readonly="${(productContract.status == 'TERMINATED') ? 'true' : ''}" data-placeholder="Select Notify Users" multiple="multiple">
								<c:forEach items="${notifyUserList}" var="usr">
									<c:if test="${usr.id != '-1'}">
										<form:option value="${usr.id}" label="${usr.name}" />
									</c:if>
									<c:if test="${usr.id == '-1'}">
										<form:option value="-1" label="${usr.name}" disabled="true" />
									</c:if>
								</c:forEach>
							</form:select>
						</div>
					</div>
				</div>
			</div> 
          <!-- Contract value -->
			<div class="Invited-Supplier-List import-supplier white-bg marg-top-10">
				<div class="meeting2-heading">
					<h3>
						<spring:message code="product.contract.value" />
					</h3>
				</div>
				<div class="import-supplier-inner-first-new pad_all_15 global-list">


					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label class="marg-top-10"> <spring:message code="eventdescription.basecurrency.label" />
							</label>
						</div>
						<div class="col-md-5 ${ buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':''}">
							<spring:message code="application.currency.required" var="required" />
							<form:select path="currency" cssClass="form-control chosen-select autoSave" readonly="${(productContract.status == 'TERMINATED') ? 'true' : ''}" id="idCurrency" data-validation="required" data-validation-error-msg-required="${required}">
								<form:option value="">
									<spring:message code="currency.select" />
								</form:option>
								<form:options items="${currencyList}" itemValue="id" />
							</form:select>
						</div>
					</div>

					<div class="row marg-bottom-10">
						<div class="col-md-3">
							<label class="marg-top-10"> <spring:message code="eventdescription.decimal.label" />
							</label>
						</div>
						<div class="col-md-5 ${ buyerReadOnlyAdmin || productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':''}">
							<form:select path="decimal" readonly="${(productContract.status == 'TERMINATED') ? 'true' : ''}" cssClass="form-control chosen-select  disablesearch decimalChange autoSave" id="iddecimal">
								<form:option value="1">1</form:option>
								<form:option value="2">2</form:option>
								<form:option value="3">3</form:option>
								<form:option value="4">4</form:option>
								<form:option value="5">5</form:option>
								<form:option value="6">6</form:option>
							</form:select>
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="contractValue" cssClass="marg-top-10">
								<spring:message code="product.Contract.value" />
							</form:label>
						</div>
						<div class="col-md-5 ${ (buyerReadOnlyAdmin || !productContract.isEditable) ? 'disabled':''}">
							<spring:message code="product.contract.value.placeholder" var="contractplace" />
							<fmt:formatNumber var="contractValue" type="number" minFractionDigits="${productContract.decimal}" maxFractionDigits="${productContract.decimal}" value="${productContract.contractValue}" />
							<form:input path="contractValue" value="${contractValue}" cssClass="form-control" id="contractValue" readonly="${(productContract.status == 'TERMINATED') ? 'true' : ''}" 
								data-validation="contract_item validate_custom_length required number custom" placeholder="${contractplace}" 
								data-validation-regexp="^[\d,]{1,16}(\.\d{1,${productContract.decimal}})?$" 
								data-validation-allowing="range[0.1;9999999999999999.999999],float" data-validation-ignore=",." 
								data-validation-error-msg="Only non-zero positive numbers allowed, length should be less than 16 or please check the decimal to be allowed"								
								/>
							<form:errors path="contractValue" cssClass="error" />
						</div>
					</div>
				</div>
			</div>

        <!-- Contract Document -->
			<div class="Invited-Supplier-List import-supplier white-bg marg-top-10">
				<div class="meeting2-heading">
					<h3>
						<spring:message code="product.contract.document" />
					</h3>
				</div>
				<div class="import-supplier-inner-first-new pad_all_15 global-list">
				
				<!-- LOA DATE -->
					<div class="row marg-bottom-10">
						<div class="col-md-3">
							<label cssClass="marg-top-10">
								<spring:message code="contract.LOA.date" />
							</label>
						</div>
	
						<div class="col-md-5">
							<div class="input-prepend input-group">
								<spring:message code="dateformat.placeholder" var="dateformat" />
								<fmt:formatDate var="loaDateVal" value="${loaAndAgrcontractDocument.loaDate}" pattern="dd/MM/yyyy" />
								<input id="loaDate" name="loaDate" class="form-control for-clander-view" data-validation-optional="true" data-validation="custom" data-validation-regexp="^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$"
									data-validation-error-msg="Invalid date - Should be in dd/mm/yyyy format"
									value="${loaDateVal}" data-fv-date-min="15/10/2016" placeholder="${dateformat}" autocomplete="off" />
								<div id="contract_dateErrorBlock"></div>
							</div>
						</div>
						<div class="col-md-2" style="padding-left: 0px;">
							<a href="#" class="btn btn-plus btn-black btn-default btn-tooltip hvr-pop hvr-rectangle-out1 clearLoaDate" data-placement="top" data-toggle="tooltip" title="Clear LOA Date">
								<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
							</a>
						</div>
						
					</div>
					<div class="error loaTypError" hidden>
						<spring:message code="please.select.loa.date" />
					</div>
	
						<!---------LOA Document --------->
					<div class="row">
						<c:set var="fileType" value="" />
						<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
							<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
						</c:forEach>
						<div class="col-md-3">
							<label class="marg-top-10">LOA Document</label>
						</div>
						
						<div class="col-md-5 ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<c:if test="${not empty loaAndAgrcontractDocument.loaFileName}">
								<div class="marginDisable ">
									<%-- <c:forEach items="${loaAndAgrcontractDocument}" var="document"> --%>
										<input type="hidden" name="loaDocs" value="${loaAndAgrcontractDocument.loaFileName}">
										<div class="col-md-12 border_box marg-top-5 loadoc-${loaAndAgrcontractDocument.id}">
											<p>
												<c:if test="${ !buyerReadOnlyAdmin}">
													<a href="${pageContext.request.contextPath}/buyer/deleteLoaDocument/${loaAndAgrcontractDocument.id}" id="deleteLoaDocument" data-id="${loaAndAgrcontractDocument.id}" data-name="${loaAndAgrcontractDocument.loaFileName}" class="deleteLoaDocument pull-right"> 
														<i class="fa fa-times-circle"></i>
													</a>
												</c:if>
												<a href="${pageContext.request.contextPath}/buyer/downloadLoaDocument/${loaAndAgrcontractDocument.id}" class="pull-left"> 
													<b>${loaAndAgrcontractDocument.loaFileName} </b>
												</a> 
												<span class="pull-right marg-right-10 marg-top-10"><b>File Size:</b> ${loaAndAgrcontractDocument.loaFileSizeInKb} KB</span> <br> 
												<span class="marg-right-10"><b>Upload Date:</b> <fmt:formatDate value="${loaAndAgrcontractDocument.loaUploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></span>
												<%-- <div class="marg-right-10"><b>Description:</b> ${loaAndAgrcontractDocument.loaDescription} </div> --%>
											</p>
										</div>
									<%-- </c:forEach> --%>
								</div>
							</c:if>
							<div class="${ buyerReadOnlyAdmin ? 'disabled':''} loadocfile-${loaAndAgrcontractDocument.id}" style="${empty loaAndAgrcontractDocument.loaFileName ? "" : "display:none" }">	
								<div id="loaDocDiv" data-provides="fileinput" class="fileinput fileinput-new input-group" >
									<spring:message code="event.doc.file.required" var="required" />
									<spring:message code="event.doc.file.length" var="filelength" />
									<spring:message code="event.doc.file.mimetypes" var="mimetypes" />
									<div data-trigger="fileinput" class="form-control">
										<span class="fileinput-filename show_name"></span>
									</div>
									<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="event.document.selectfile" /></span>
									<span class="fileinput-exists"> <spring:message code="event.document.change" /></span>
									 <input type="file" data-validation-allowing="${fileType}" data-validation="mime size" data-validation-error-msg-container="#loa_file_error" 
									 data-validation-max-size="${ownerSettings.fileSizeLimit}M" name="loaDocs" id="loaDocs" data-validation-error-msg-required="${required}"
										data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="You can only upload files of type ${fileType}">
									</span>
								</div>
								<div id="loa_file_error"></div>
							</div>	
						</div>
					</div>
						<div class="row loadocfile-${loaAndAgrcontractDocument.id}" style="${empty loaAndAgrcontractDocument.loaFileName ? "" : "display:none" }" >
							<div class="col-md-3"></div>
							<div class="col-md-5">
								Note:<br />
								<ul>
									<li>Max allowed file size is ${ownerSettings.fileSizeLimit} MB</li>
									<li>Allowed file extensions: ${fileType}.</li>
								</ul>
							</div>
						</div>

					<!-- LOA Description -->
					<div class="row marg-bottom-10 marg-top-10 ${ buyerReadOnlyAdmin ? 'disabled':''}">
						<div class="col-md-3">
						</div>
						<div class="col-md-5">
							<spring:message code="event.doc.file.descrequired" var="descrequired" />
							<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
							<spring:message code="event.document.filedesc" var="filedesc" />
							<textarea name="loaDesc" id="loaDesc" class="form-control" data-validation="length" data-validation-length="max250" placeholder="${filedesc}" maxlength="250">${loaAndAgrcontractDocument.loaDescription}</textarea>
							<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
						</div>
					</div>

					<!-- Agreement Date -->
					<div class="row marg-bottom-10">
						<div class="col-md-3">
							<label cssClass="marg-top-10">
								<spring:message code="contract.agreement.date" />
							</label>
						</div>

						<div class="col-md-5" >
							<div class="input-prepend input-group">
								<spring:message code="dateformat.placeholder" var="dateformat" />
								<fmt:formatDate var="agrDateVal" value="${loaAndAgrcontractDocument.agreementDate}" pattern="dd/MM/yyyy" />
								<input id="agrDate" name="agrDate" class="form-control for-clander-view"
								data-validation-optional="true" data-validation="custom" data-validation-regexp="^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$"
								data-validation-error-msg="Invalid date - Should be in dd/mm/yyyy format"
								 data-fv-date-min="15/10/2016" value="${agrDateVal}" placeholder="${dateformat}" autocomplete="off" />
								<div id="contract_dateErrorBlock"></div>
							</div>
						</div>
						<div class="col-md-2" style="padding-left: 0px;">
							<a href="#" class="btn btn-plus btn-black btn-default btn-tooltip hvr-pop hvr-rectangle-out1 clearAgrDate" data-placement="top" data-toggle="tooltip" title="Clear Agreement Date">
								<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
							</a>
						</div>
					</div>
					<div class="error agrTypError" hidden>
						<spring:message code="please.select.agr.date" />
					</div>

					<!---------Agreement Document --------->
					<div class="row">
						<c:set var="fileType" value="" />
						<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
							<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
						</c:forEach>
						<div class="col-md-3">
							<label class="marg-top-10">Agreements Document</label>
						</div>
						<div class="col-md-5 ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<c:if test="${not empty loaAndAgrcontractDocument.agreementFileName}">
								<div class="marginDisable ">
									<%-- <c:forEach items="${loaAndAgrcontractDocument}" var="document"> --%>
										<input type="hidden" name="agrDocs" value="${loaAndAgrcontractDocument.agreementFileName}">
										<div class="col-md-12 border_box marg-top-5 agrdoc-${loaAndAgrcontractDocument.id}">
											<p>
												<c:if test="${!buyerReadOnlyAdmin}">
													<a href="${pageContext.request.contextPath}/buyer/deleteAgreementDocument/${loaAndAgrcontractDocument.id}" id="deleleteLAgreementDocument" data-id="${loaAndAgrcontractDocument.id}" data-name="${loaAndAgrcontractDocument.agreementFileName}" class="deleleteAgreementDocument pull-right"> 
														<i class="fa fa-times-circle"></i>
													</a>
												</c:if>
												<a href="${pageContext.request.contextPath}/buyer/downloadAgreementDocument/${loaAndAgrcontractDocument.id}" class="pull-left"> 
													<b>${loaAndAgrcontractDocument.agreementFileName} </b>
												</a> 
												<span class="pull-right marg-right-10 marg-top-10"><b>File Size:</b> ${loaAndAgrcontractDocument.agreementFileSizeInKb} KB</span> <br> 
												<span class="marg-right-10"><b>Upload Date:</b> <fmt:formatDate value="${loaAndAgrcontractDocument.agreementUploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></span>
											</p>
										</div>
									<%-- </c:forEach> --%>
								</div>
							</c:if>
							<div class="${ buyerReadOnlyAdmin ? 'disabled':''} agrdocfile-${loaAndAgrcontractDocument.id}" style="${empty loaAndAgrcontractDocument.agreementFileName ? "" : "display:none" }">
								<div id="agrDocDiv" data-provides="fileinput" class="fileinput fileinput-new input-group">
									<spring:message code="event.doc.file.required" var="required" />
									<spring:message code="event.doc.file.length" var="filelength" />
									<spring:message code="event.doc.file.mimetypes" var="mimetypes" />
									<div data-trigger="fileinput" class="form-control">
										<span class="fileinput-filename show_name"></span>
									</div>
									<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="event.document.selectfile" />
									</span> <span class="fileinput-exists"> <spring:message code="event.document.change" />
									</span> 
									<input data-validation-allowing="${fileType}" data-validation="mime size" data-validation-error-msg-container="#agr_file_error" data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" name="agrDocs" id="agrDocs" data-validation-error-msg-required="${required}"
										data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="You can only upload files of type ${fileType}">
									</span>
								</div>
								<div id="agr_file_error"></div>
							</div>
						</div>
					</div>
					<div class="row agrdocfile-${loaAndAgrcontractDocument.id}" style="${empty loaAndAgrcontractDocument.agreementFileName ? "" : "display:none" }" >
						<div class="col-md-3"></div>
						<div class="col-md-5"> 
							Note:<br />
							<ul>
								<li>Max allowed file size is ${ownerSettings.fileSizeLimit} MB</li>
								<li>Allowed file extensions: ${fileType}.</li>
							</ul>
						</div>
					</div>

					<!-- Agreement Description -->
							<div class="row marg-bottom-10 marg-top-10 ${ buyerReadOnlyAdmin ? 'disabled':''}">
								<div class="col-md-3">
								</div>
								<div class="col-md-5">
									<spring:message code="event.doc.file.descrequired" var="descrequired" />
									<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
									<spring:message code="event.document.filedesc" var="filedesc" />
									<textarea name="agrDesc" id="agrDesc" class="form-control" data-validation="length" data-validation-length="max250" placeholder="${filedesc}" maxlength="250">${loaAndAgrcontractDocument.agreementDescription}</textarea>
									<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
								</div>
							</div>

					<!---------Other Document --------->
							<div class="row marg-bottom-10">
								<c:set var="fileType" value="" />
								<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
									<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
								</c:forEach>
								<div class="col-md-3">
									<label class="marg-top-10">Other Documents</label>
								</div>
								<div class="col-md-5">
									<c:if test="${not empty contractDocument}">
										<div>
											<c:forEach items="${contractDocument}" var="document">
												<input type="hidden" name="docs" value="${document.fileName}">
												<div class="col-md-12 border_box marg-top-5 marg-bottom-5 doc-${document.id}">
													<p>
														<c:if test="${ !buyerReadOnlyAdmin}">
															<a href="" id="deleleteDocument" data-id="${document.id}" data-name="${document.fileName}" class="deleleteDocument pull-right"> <i class="fa fa-times-circle"></i></a>
														</c:if>
														<a href="${pageContext.request.contextPath}/buyer/downloadContractDocument/${document.id}" class="pull-left"> <b>${document.fileName} </b></a> 
														<span class="pull-right marg-right-10 marg-top-10"><b>File Size:</b> ${document.fileSizeInKb} KB</span> <br> 
														<span class="marg-right-10"><b>Upload Date:</b> <fmt:formatDate value="${document.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></span>
														<div class="marg-right-10"><b>Description: </b>${document.description}</div>
													</p>
												</div>
											</c:forEach>
										</div>
									</c:if>
									<div>
										<div id="appendFile" class="marg-top-5"></div>
										<button name="addMore" id="addMoreFiles" class="more-btn btn btn-info ph_btn_small hvr-pop hvr-rectangle-out mr-10 marg-top-5 ${ buyerReadOnlyAdmin  ? 'disabled':''}">Add Attachment</button>
										<div id="Load_File-error" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
									</div>
								</div>
							</div>
							<div class="row marg-bottom-10">
								<div class="col-md-3"></div>
								<div class="col-md-5">
									Note:<br />
									<ul>
										<li>Max allowed file size is ${ownerSettings.fileSizeLimit} MB</li>
										<li>Allowed file extensions: ${fileType}.</li>
									</ul>
								</div>
							</div>

					<!-- Other Description -->
<%-- 					<div class="row marg-bottom-10 ${contractExpire ? 'disabled':''}">
						<div class="col-md-3">
						</div>
						<div class="col-md-5">
							<spring:message code="event.doc.file.descrequired" var="descrequired" />
							<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
							<spring:message code="event.document.filedesc" var="filedesc" />
							<input class="form-control"  name="docDesc" id="docDesc" data-validation="length" data-validation-length="max250" type="text" placeholder="File Description" maxlength="250"> <span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
						</div>
					</div>
 --%>				
 					</div>
			</div>
			
			<!-- Contract Team Member -->
	<div class="Invited-Supplier-List import-supplier white-bg marg-top-10">
		<div class="meeting2-heading">
			<h3>
				<spring:message code="product.contract.team.member" />
			</h3>
		</div>
		<div class="import-supplier-inner-first-new pad_all_15 global-list">
			<div>
				<div class="col-sm-5 col-md-5 col-xs-6 ${!empty rfxTemplate and rfxTemplate.readOnlyTeamMember ?'disabled':''  }">
					<div class="col-md-12">
						<div class="ia-invite-controls-area">
							<div class="group">
								<div class="input-group mrg15T mrg15B ${ productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':''}">
									<select id="TeamMemberList" class="user-list-normal chosen-select" selected-id="approver-id" name="approverList1" cssClass="form-control chosen-select">
										<option value=""><spring:message code="teammeber.search.user" /></option>
										<c:forEach items="${userTeamMemberList}" var="TeamMember">
											<c:if test="${TeamMember.id != '-1' }">
												<option value="${TeamMember.id}">${TeamMember.name}</option>
											</c:if>
											<c:if test="${TeamMember.id == '-1' }">
												<option value="-1" disabled="true">${TeamMember.name}</option>
											</c:if>
										</c:forEach>
									</select>
									<div class="input-group-btn">
										<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
											<span class="glyphicon glyphicon-eye-open"></span>
										</button>
										<ul class="dropdown-menu dropup">
											<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="access_editor" value="Editor" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.editor" />
											</a></li>
											<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="access_viewer" value="Viewer" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.viewer" />
											</a></li>
											<li>
												<a href="javascript:void(0);" class="small " tabIndex="-1"> 
													<input id="access_Associate_Owner" value="Associate_Owner" class="access_check" type="checkbox" /> <spring:message code="eventsummary.checkbox.associate.owner" />
												</a>
											</li>
										</ul>
										<button class="btn btn-primary addTeamMemberToList" type="button"><i class="fa fa-plus"></i></button>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="mem-tab ${ productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED' ? 'disabled':''}" >
							<table cellpading="0" cellspacing="0" width="100%" id="appUsersList" class="table-hover table tableApp">
								<c:forEach items="${productContract.teamMembers}" var="teamMembers">
									<tr data-username="${teamMembers.user.name}" approver-id="${teamMembers.user.id}">
										<td class="width_50_fix ">
											<!--	<img src='<c:url value="/resources/images/profile_setting_image.png"/>' alt="profile " /> -->
										</td>
										<td>${teamMembers.user.name}<br> <span>${teamMembers.user.loginId}</span>
										</td>
										<td class="edit-drop">
											<div class="advancee_menu">
												<div class="adm_box">
													<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"> <i class="glyphicon ${teamMembers.teamMemberType=='Editor'?'glyphicon-pencil':teamMembers.teamMemberType=='Viewer'?'glyphicon-eye-open':teamMembers.teamMemberType=='Associate_Owner'?'fa fa-user-plus':''}" aria-hidden="true"
														title="${teamMembers.teamMemberType=='Editor'?'Editor':teamMembers.teamMemberType=='Viewer'?'Viewer':teamMembers.teamMemberType=='Associate_Owner'?'Associate Owner':''}"></i>
													</a>

													<ul class="dropdown-menu dropup">
														<li style="margin-top: 10px;">
															<a href="javascript:void(0);" class="small" tabIndex="-1"> 
																<input data-uid="${teamMembers.user.id}" id="${teamMembers.user.id}-Editor" ${teamMembers.teamMemberType=='Editor' ? 'checked' : ''} value="Editor" class="access_check" type="checkbox" /> &nbsp;
																<label for="${teamMembers.user.id}-Editor"><spring:message code="eventsummary.checkbox.editor" /></label>
															</a>
														</li>
														<li>
															<a href="javascript:void(0);" class="small " tabIndex="-1"> 
																<input id="${teamMembers.user.id}-Viewer" ${teamMembers.teamMemberType=='Viewer'?'checked': ''} value="Viewer" data-uid="${teamMembers.user.id}" class="access_check" type="checkbox" /> 
																&nbsp;
																<label for="${teamMembers.user.id}-Viewer"><spring:message code="eventsummary.checkbox.viewer" /></label>
															</a>
														</li>
														<li>
															<a href="javascript:void(0);" class="small " tabIndex="-1"> 
																<input id="${teamMembers.user.id}-Associate_Owner" ${teamMembers.teamMemberType=='Associate_Owner'?'checked': ''} value="Associate_Owner" data-uid="${teamMembers.user.id}" class="access_check" label="ABCD" type="checkbox" /> &nbsp;
																<label for="${teamMembers.user.id}-Associate_Owner"><spring:message code="eventsummary.checkbox.associate.owner" /></label>
															</a>
														</li>
													</ul>
												</div>
											</div>
										</td>
										<td>
											<div class="cqa_delx">
												<a href="javascript:void(0);" list-type="Team Member " class="adm_menu_link removeApproversList" title='Remove'>
												<i class="glyphicon glyphicon-trash" style="color: red" aria-hidden="true" title="Remove"></i>
												</a>
											</div>
										</td>
									</tr>
								</c:forEach>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!-- Contract Approvals -->
 	<div class="Invited-Supplier-List import-supplier white-bg marg-top-10">
		<div class="meeting2-heading">
			<h3>
				<spring:message code="contract.approval.route" />
			</h3>
		</div>
		<div class="import-supplier-inner-first-new pad_all_15 global-list">
			<div id="apprTab" class="import-supplier-inner-first-new pad_all_15 global-list">
				<jsp:include page="contractApproval.jsp"></jsp:include>
			</div>
 		</div>
	</div> 
	
		<c:if test="${productContract.status == 'DRAFT' or ( (productContract.status == 'ACTIVE' or productContract.status == 'SUSPENDED') and (eventPermissions.owner or eventPermissions.editor or eventPermissions.viewer) ) }">
			<div class="row">
				<c:if test="${ !buyerReadOnlyAdmin and ( ((productContract.status == 'DRAFT' or productContract.status == 'SUSPENDED' ) and (eventPermissions.owner || eventPermissions.editor)) or (productContract.status == 'ACTIVE' and eventPermissions.owner) ) }">
					<input type="button" value="Save & Next" id="saveProductListMaintenance" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-top-20" ${canEdit ? "":  "disabled='disabled'"}>
				</c:if>
				<c:if test="${ (buyerReadOnlyAdmin or eventPermissions.viewer or (eventPermissions.editor and productContract.status == 'ACTIVE'))}">
					<c:url var="nextUrl" value="/buyer/productContractItemList/${productContract.id}" />
					<a href="${nextUrl}" id="nextButton" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-top-20" >Next</a>
				</c:if>
				<c:if test="${productContract.status == 'DRAFT'  and !buyerReadOnlyAdmin and eventPermissions.owner}">
					<a href="#confirmCancelContract" role="button" class="btn btn-danger hvr-pop ph_btn_midium float-right marg-top-20 marg-right-20 marg-bottom-20 draft ${ buyerReadOnlyAdmin ? 'disabled' : '' }" data-toggle="modal"><spring:message code="event.cancel.draft" /></a>
				</c:if>
			</div>
		</c:if>			
		</form:form>
	</div>

<!--Approver  popup-->
<div class="flagvisibility dialogBox" id="removeApproverListPopup" title='<spring:message code="tooltip.remove.team.member" />'>
	<div class="float-left width100 pad_all_15 white-bg">
		<input type="hidden" id="approverListId" name="approverListId" value=""> 
		<input type="hidden" id="approverListType" name="approverListType" value="">
		<input type="hidden" id="approverListUserName" name="approverListType" value="">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock">
				<spring:message code="event.confirm.to.remove" />
				<span></span>
				<spring:message code="application.from" />
				<span></span>
				<spring:message code="application.list" />?
			</p>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeApproverListPerson" data-original-title="Delete"> 
						<spring:message code="tooltip.delete" />
					</a>
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>

<div class="modal fade" id="addReminder" role="dialog" data-backdrop="static">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="product.contract.reminder.heder" />
				</h3>
				<button class="close for-absulate" data-dismiss="modal" id="crossRem" type="button" style="border: none;">
					<i class="fa fa-times-circle"></i>
				</button>
			</div>
			<form action="${pageContext.request.contextPath}/buyer/saveProductContract" method="post" id="reminder1">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<div class="row d-flex-line mb-15">
						<input name="reminderId" id="reminderId" value="${reminderId}" type="hidden"> <label class="remind-lbl" style="margin-left: 15px;"> <spring:message code="product.contract.sendreminder" />
						</label>
					</div>
					<div class="row" style="margin-top: 10px">
						<div class="col-md-10">
							<div class="reminderInterval">
								<input name="remindMeDays" id="remindMeDays" data-validation="required, number" 
									data-validation-error-msg-container="#reminderDaysErr" data-validation-allowing="range[1;999]"
									maxlength="3" type="text" class="form-control col-md-6" style="margin-left: 10px"/>
							</div>
							<div class="col-md-4">
								DAYS
							</div>
							<div class="col-md-12 text-left" id="reminderDaysErr"></div>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="button" id="saveReminderButton" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">
						<spring:message code="application.save" />
					</button>
					<button type="button" id="reminderCan" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<div class="modal fade" id="confirmDeleteReminder" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="rfaevent.confirm.delete.reminderlist" />
				</label> <input type="hidden" id="deleteIdReminder" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelReminder">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>

<!-----------popup delete document-------------------->
<div class="modal fade" id="confirmDeleteDocument" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="contract.confirm.delete.document" /></label> <input type="hidden" id="deleteIdDocument" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelDocument">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>

<!-----------popup delete LOA document-------------------->
<div class="modal fade" id="confirmDeleteLoaDocument" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="contract.confirm.delete.loa.document" /></label> <!-- <input type="hidden" id="deleteIdLoaDocument" /> -->
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<input type="hidden" id="idcontract" name="idcontract" value="${productContract.id}">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelLoaDocument">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>

<!-----------popup delete Agreement document-------------------->
<div class="modal fade" id="confirmDeleteAgreementDocument" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="contract.confirm.delete.agr.document" /></label> <input type="hidden" id="deleteIdDocument" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelAgreementDocument">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="deleteContractItem" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" onclick="javascript:doCancel();" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="contract.item.delete" /></label>
			</div>
			<input type="hidden" id="deleteId" value="" />
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" title="Delete"><spring:message code="application.delete" /></a>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>

<!-- cancel contract popup  -->
<div class="modal fade" id="confirmCancelContract" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="idFrmCancelContract" action="${pageContext.request.contextPath}/buyer/cancelContract" method="get">
				<input type="hidden" name="contractId" value="${productContract.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="contract.cancel.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="reasonCancellation" />
							<textarea class="width-100" placeholder="${reasonCancellation}" rows="3" name="cancelReason" id="cancelReason" data-validation="required length" data-validation-length="max1000"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="cancelContract" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>



<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<script>
<c:if test="${buyerReadOnlyAdmin or !canEdit or productContract.status == 'TERMINATED' or eventPermissions.viewer or (!eventPermissions.owner and !eventPermissions.editor and !eventPermissions.viewer) or (eventPermissions.editor and productContract.status == 'ACTIVE')}">
	$(window).bind('load', function() {
		var allowedFields = '.button-previous, #bubble, #dashboardLink, #listLink,.pagination,#nextButton';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if>
	$('#contractEndDateId').datepicker().on('changeDate', function (ev) {
	    var endDate = $('#contractEndDateId').val();
	    var dt = new Date();
    	var time = dt.getHours() + ":" + dt.getMinutes() + ":" + dt.getSeconds();
       
	    $('input[name="remindMeDays"]').each(function(i, item) {
	    	var days = parseInt($(this).val());
	    	var alertDate = new Date();
	    	alertDate.setTime(ev.date.getTime()-(days * 24 * 60 * 60 * 1000));
	     	if(dt > alertDate){
	    		$(this).closest('.row').remove();
	    	} else {
	    		$(this).parent().find('p').html('<b>Reminder Date: </b>' + moment(alertDate).format('DD/MM/YYYY'));
	    	}
	    });
	});
	
	var fileSizeLimit = ${ownerSettings.fileSizeLimit};
	var mimetypes = '<spring:message code="meeting.doc.file.mimetypes" />';
	var fileType = "${fileType}";
	
	
	$('#idFreeTextItem').on('click', function(e) {
		console.log('Checkbox : ', $(this).prop("checked"));
		if($(this).prop("checked")) {
			$('#idItemFreeText').show();
			$('#itemName').removeAttr('data-validation-optional');
			$('#idItemList').hide();
			$('#chosenProductItem').attr('data-validation-optional', 'true');
		} else {
			$('#idItemFreeText').hide();
			$('#itemName').attr('data-validation-optional', 'true');
			$('#idItemList').show();
			$('#chosenProductItem').removeAttr('data-validation-optional');
		}
	});

/* 	$('#loaDocs').change(function() {
	   var loaDate = $.trim($('#loaDate').val());
	   if (loaDate == '') {
			$('.loaTypError').removeAttr('hidden');	
	   }
	});
	
	$('#agrDocs').change(function() {
		   var agrDate = $.trim($('#agrDate').val());
		   if (agrDate == '') {
				$('.agrTypError').removeAttr('hidden');	
		   }
	});
 */
	$(document).ready(function() {

		$('.clearAgrDate').on('click', function(e) {
			e.preventDefault();
			$('#agrDate').val('');
		});
		
		$('.clearLoaDate').on('click', function(e) {
			e.preventDefault();
			$('#loaDate').val('');
		});
		
		$(document.body).on("change.bs.fileinput", "div.fileinput.fileinput-exists", function(e) {
	        console.log('File changed..', $(this).find('input[type=file]'));
	        $(this).find('input[type=file]').validate();
		});
	});

// Initially hide the manual section
$(".showPartMANUAL").hide();

// Handle radio button click event
$("input[name='supplierChoice']").on("click", function() {
	var selectedValue = $(this).val();

	if (selectedValue === "LIST") {
		$(".showPartLIST").show();
		$(".showPartMANUAL").hide();
	} else if (selectedValue === "MANUAL") {
		$(".showPartLIST").hide();
		$(".showPartMANUAL").show();

		// Reset supplier when "My Supplier" is selected
		$("#chosenSupplier").val("").trigger("chosen:updated");

		// Reset supplier when "Open Supplier" is selected
		$("#chosenSupplierName").val("").trigger("chosen:updated");
	}
});
</script>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/productContractUpdate.js?7"/>"></script>
