<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('BUYER_FAV_SUPPLIER_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.languageCode" var="languageCode" />
<spring:message var="supplierImport" code="application.supplier.import" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css?1"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/chardinjs.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<!-- this is added for second  token input on same page because we want to show into modal view  -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input1.css"/>" />

<style>
.showhidebutton {
	cursor: pointer;
	color: #28b4f2 !important;
}

.showhidebutton:hover {
	text-decoration: underline;
}

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
	top: 10px;
	position: absolute !important;
}

.select-abs:hover {
	opacity: 1 !important;
}

.more-btn {
	margin: 10px 0;
}

.mt-15 {
	margin-top: 15px;
}

.mt-10 {
	margin-top: 15px;
}

.mb-10 {
	margin-bottom: 10px !important;
}

.height-60 {
	height: 420px;
	overflow-y: auto;
	overflow-x: hidden;
	position: relative;
}

.center-btn {
	text-align: center;
	margin: 0 auto;
	width: 100%;
}

.daterangepicker select.yearselect {
	width: 45% !important;
}

.ranges .daterangepicker_end_input {
	padding-left: 0 !important;
}

.form-control[disabled], .form-control[readonly], fieldset[disabled] .form-control,
	input[disabled], select[disabled], textarea[disabled] {
	cursor: default;
}

.wrp_btn {
	display: inline-block
}

.box-bottom.width50 {
	width: 50%;
}

.main-div {
	max-width: 900px;
	text-align: center;
	margin-left: auto;
	margin-right: auto;
	width: 100%;
}

.box-bottom span {
	font-size: 16px;
	width: 100%;
	left: 0;
	position: absolute;
	top: 0;
	line-height: 16;
	height: 100%;
}

.box-bottom div {
	width: 100%;
	line-height: 3;
}

.border-right-shaded {
	border-width: 1px;
	border-left: 0;
	border-style: solid;
	-webkit-border-image: -webkit-gradient(linear, 0 100%, 0 0, from(#CCC),
		to(rgba(0, 0, 0, 0))) 1 100%;
	-webkit-border-image: -webkit-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0))
		1 100%;
	-moz-border-image: -moz-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0))
		1 100%;
	-o-border-image: -o-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0)) 1
		100%;
	border-image: linear-gradient(to top, #CCC, rgba(0, 0, 0, 0)) 1 100%;
}

.box-main {
	border: 1px solid #eeeff0;
	border-radius: 0;
	margin: 0 1% 30px;
	height: 150px;
	overflow: hidden;
	position: relative;
	width: 200px;
	cursor: pointer;
	display: inline-block;
}

.box-top {
	top: 0;
	color: #fff;
	font-size: 14px;
	line-height: 18px;
	padding: 10px 3%;;
	position: absolute;
	text-align: left;
	text-transform: uppercase;
	width: 100%;
	border-radius: 0;
}

.box-bottom {
	float: left;
	padding: 23% 0 13% 0;
	position: relative;
	text-align: center;
	width: 100%;
	font-size: 34px;
}

.box-top span {
	float: left;
	line-height: 25px;
	margin-left: 3%;
	width: 79%;
}

.yellow {
	background: #F9C851;
	border: 1px solid #F9C851;
	color: #ffffff !important;
}

.yellow-con {
	/* font-size: 60px; */
	color: #F9C851;
	display: inline-block;
}

.orange {
	background: #FFA500;
	border: 1px solid #F9C851;
}

.limegreen {
	background: #32CD32;
	border: 1px solid #32CD32;
}

.green {
	background: #06CCB3;
	border: 1px solid #06CCB3;
}

.crimson {
	background: #ff5b5b;
	border: 1px solid #FF5B5B;
}

.gold {
	background: #FFD700;
	border: 1px solid #FFD700;
}

.navy {
	background: #000080;
	border: 1px solid #000080;
}

.navy-con {
	color: #0000CD;
	display: inline-block;
}

.gold-con {
	color: #FFD700;
	display: inline-block;
}

.red {
	background: #FF5B5B;
	border: 1px solid #FF5B5B;
}

.blue {
	background: #627fa7;
	border: 1px solid #627fa7;
}

.sky-blue-dash {
	background: #35b4e9;
	border: 1px soild #35b4e9;
}

.sky-blue-con {
	color: #35b4e9;
	display: inline-block;
}

.coffi {
	background: #cebf98;
	border: 1px solid #cebf98;
}

.light-blue {
	background: #00d1c6;
	border: 1px solid #00d1c6;
}

.light-blue-con {
	color: #00d1c6;
	display: inline-block;
}

.light-gray {
	background: #727c88;
	border: 1px solid #727c88;
}

.light-gray-con {
	color: #727c88;
	display: inline-block;
}

.perpal {
	background: #8809ff;
	border: 1px solid #8809ff;
}

.perpal-con {
	color: #8809ff;
	display: inline-block;
}

.db-li {
	line-height: 19px !important;
}

.limegreen-con {
	color: #32CD32;
}

.crimson-con {
	color: #ff5b5b;
}

.red-con {
	color: #e93535;
}

.blck-con {
	font-size: 25px;
	color: #333333;
}

.orange-con {
	color: #FFA500;
	display: inline-block;
}

.green-con {
	color: #06ccb3;
	display: inline-block;
}

.gray-con {
	color: #333333;
	display: inline-block;
}

.bottom-text {
	bottom: 5px;
	color: #333;
	float: left;
	font-size: 22px;
	width: 100%;
	margin-top: 30px;
}

.box-top img {
	width: 30px;
	vertical-align: top;
	float: right;
}
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}

.ph_table td, .ph_table th {
	text-align: -moz-center;
}

.nopad {
	padding: 10px 0 10px 0 !important;
}

.noti-icon-inputfix, .noti-icon-messagefix {
	width: auto;
}

#prDraftList th {
	text-align: left;
}

.height-100 {
	height: 100%;
}

.form-pos-rel {
	position: relative;
	/* height: 78%; */
}

.footer-abs-pos {
	position: sticky;
	z-index: 99;
}

.text-center-msg {
	text-align: left;
	margin: 15px 0;
}

.w-90 {
	width: 90px;
}

ul#industryCategoryList {
	list-style: none;
	padding: 0;
	position: absolute;
	z-index: 9;
	background: #fff;
	border-left: 1px solid #ccc;
	width: 100%;
	border-right: 1px solid #ccc;
	max-height: 200px;
	overflow: auto;
	top: 38px;
}

#industryCategoryList li:first-child {
	border-top: 1px solid #ccc;
}

#industryCategoryList li {
	border-bottom: 1px solid #ccc;
	padding: 10px;
	cursor: pointer;
}

#industryCategoryList li:hover {
	background: #0cb6ff;
	color: #fff;
}

.RightPad {
	padding-right: 50px;
}

.w-88 {
	width: 88%;
}

.pos-rel {
	position: relative;
}

.mr-10 {
	margin-right: 10px;
}

</style>


<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierImport}] });
});
</script>
<script type="text/javascript">
	function canEdit() {
		return "${canEdit}";
	}
</script>
<div id="page-content-wrapper">

	<c:set var="fileType" value="" />
	<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
		<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
	</c:forEach>
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="import.supplier" /></li>
			</ol>
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap tender-request-heading">
					<spring:message code="import.supplier" />
				</h2>
			</div>
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div class="Invited-Supplier-List white-bg dashboard-main">
				<div class="main-div marg-top-20">
					<div class="box-main activeContainer">
						<div class="box-top sky-blue-dash">
							<span><spring:message code="supplier.fav.suppliers" /> </span> 
						</div>
						<div class="box-bottom width50"  data-target="myActiveSupplierData">
							<div class="sky-blue-con border-right-shaded activeContainer">${activeSuppCount}</div>
							<span><spring:message code="supplier.fav.activeSupplier" /></span>
						</div>
						<div class="box-bottom width50"  data-target="myActiveSupplierData1">
							<div class="sky-blue-con activeContainer">${totalGlobalSuppCount}</div>
							<span><spring:message code="supplier.fav.totalSupplier" /></span>
						</div> 
					</div>
					<div class="box-main requestedContainer">
						<div class="box-top orange">
							<span><spring:message code="supplier.fav.newRequest" /> </span> 
						</div>
						<div class="box-bottom width50"  data-target="myRequestedSupplierData">
							<div class="orange-con border-right-shaded requestedContainer">${requestedPendingSuppCount}</div>
							<span><spring:message code="supplier.associated.pendingBuyers" /></span>
						</div>
						<div class="box-bottom width50"  data-target="myRequestedSupplierData1">
							<div class="orange-con requestedContainer">${requesteddRejectedSuppCount}</div>
							<span><spring:message code="supplier.fav.rejected" /></span>
						</div>
					</div>
					<div class="box-main restrictedContainer">
						<div class="box-top crimson">
							<span><spring:message code="supplier.fav.restricted" /> </span> 
						</div>
						<div class="box-bottom width50" data-target="myRestrictedSupplierData1">
							<div class="crimson-con border-right-shaded  restrictedContainer">${suspendedSuppCount}</div>
							<span><spring:message code="supplier.fav.suspendedSupplier" /></span>
						</div>
						<div class="box-bottom width50" data-target="myRestrictedSupplierData">
							<div class="crimson-con  restrictedContainer">${blacklistedSuppCount}</div>
							<span><spring:message code="supplier.fav.blocklistedupplier" /></span>
						</div>
					</div>
					<div class="box-main supplierFormContainer">
						<div class="box-top navy">
							<span><spring:message code="dashboard.suppliers.forms" /> </span> 
						</div>
						<div class="box-bottom width50" data-target="mySupplierFormSubData1">
							<div class="navy-con border-right-shaded  supplierFormContainer" id="pendingSuppFormCountId">${pendingSuppFormCount}</div>
							<span><spring:message code="supplier.form.pending" /></span>
						</div>
						<div class="box-bottom width50" data-target="mySupplierFormSubData">
							<div class="navy-con  supplierFormContainer">${submittedSuppFormCount}</div>
							<span><spring:message code="supplier.form.submitted" /></span>
						</div>
					</div>
				</div>
			</div>
			<div class="example-box-wrapper wigad-new  white-bg">
				<c:if test="${buyer.allowSupplierUpload}">
					<div class="pull-right marg-top-10 marg-bottom-10">
						<button class="btn btn-default hvr-pop hvr-rectangle-out3 marg-left-10" id="downloadSupplierTemplate">
							<i class="excel_icon"></i>
							<spring:message code="suppliers.download.template.btn" />
						</button>
						<button class="btn green-btn hvr-pop hvr-rectangle-out2 marg-left-10 marg-right-10 ${!buyerReadOnlyAdmin ? '' : 'disabled' }" id="uploadSupplierList">
							<i class="upload_icon"></i>
							<spring:message code="suppliers.upload.supplier.btn" />
						</button>
						<div data-provides="fileinput" class="fileinput hide fileinput-new input-group">
							<spring:message code="event.doc.file.required" var="required" />
							<div data-trigger="fileinput" class="form-control">
								<i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename show_name"></span>
							</div>
							<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="event.document.selectfile" />
							</span> <span class="fileinput-exists"> <spring:message code="event.document.change" />
							</span> <input type="file" data-buttonName="btn-black" id="uploadSupplierListFile" name="uploadSupplierListFile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
							</span>
						</div>
						<!-- <button style="visibility: hidden" id="uploadsupplierListFile"></button> -->
					</div>
				</c:if>
				<div class="Invited-Supplier-List import-supplier white-bg">
					<div class="panel sum-accord">
						<div class="panel-heading">
							<h4 class="panel-title">
								<a data-toggle="collapse" class="accordion" href="#collapseSearchSupplier"><spring:message code="import.search.global.list" /> </a>
							</h4>
						</div>
					</div>
					<div id="collapseSearchSupplier" class="panel-collapse collapse">
						<div class="panel-body pad_all_15  border-bottom">
							<div class="import-supplier-inner-first-new pad_all_15 global-list">
								<input type="hidden" id="showMoreOption" value="${not empty industryCategories ||  not empty naicsCategories}">
								<form:form id="searchSupplierForm" class="form-horizontal" commandName="supplierSearchPojo">
									<form:hidden path="id" id="searchSupplierId" />
									<form:hidden path="status" id="favSupplierStatus" />
									<div class="row marg-bottom-10">
										<div class="col-md-3 col-sm-4">
											<label class="marg-top-10"> <spring:message code="import.company.name" />
											</label>
										</div>
										<div class="col-md-4 col-sm-5">
											<spring:message code="suppliers.name.placeholder" var="compname" />
											<form:input path="companyName" class="form-control" type="text" id="companyName" placeholder="${compname}" />
										</div>
									</div>
									<div class="row marg-bottom-10">
										<div class="col-md-3 col-sm-4">
											<label class="marg-top-10"> <spring:message code="import.registration.number" />
											</label>
										</div>
										<div class="col-md-4 col-sm-5">
											<spring:message code="suppliers.company.reginumber.placeholder" var="regiNo" />
											<form:input path="companyRegistrationNumber" class="form-control" type="text" id="companyRegistrationNumber" placeholder="${regiNo}" />
										</div>
									</div>
									<div class="row marg-bottom-10">
										<div class="col-md-3 col-sm-4">
											<label class="marg-top-10"> <spring:message code="import.sort.by" />
											</label>
										</div>
										<div class="col-md-4 col-sm-5">
											<select class="chosen-select disablesearch orderSupplierCustom" name="dd">
												<option><spring:message code="import.newest" /></option>
												<option><spring:message code="import.oldest" /></option>
											</select>
										</div>
									</div>
									<div class="row marg-bottom-10" id="idStatus">
										<div class="col-md-3 col-sm-4">
											<label class="marg-top-10"> <spring:message code="import.status" />
											</label>
										</div>
										<div class="col-md-4 col-sm-5">
											<select name="supplierCustomStatus" class="chosen-select disablesearch">
												<c:forEach items="${statusList}" var="item">
													<option value="${item}">${item}</option>
												</c:forEach>
											</select>
										</div>
									</div>
									<div class="row marg-bottom-10">
										<div class="col-md-3 col-sm-4">&nbsp;</div>
										<div class="col-md-9 col-sm-8">
											<div class="checkbox checkbox-info">
												<label> <form:checkbox path="globalSearch" value="" id="globalSearch" name="showglobal" class="custom-checkbox checkallcheckbox hideCustomFields" /> <spring:message code="import.show.results" />
												</label>
											</div>
										</div>
									</div>
		
									<div class="row marg-bottom-10">
										<div class="col-md-3 col-sm-4">&nbsp;</div>
										<div class="col-md-9 col-sm-8">
											<div class="checkbox checkbox-info">
												<label> <form:checkbox path="registered" value="" id="registered" name="registered" class="custom-checkbox checkallcheckbox " />Activation Pending
												</label>
											</div>
										</div>
									</div>
									<div class="list-of-product">
										<div class="row marg-bottom-10">
											<div class="col-md-3">
												<label class="marg-top-10"></label>
											</div>
											<div class="col-xs-12 col-sm-12 col-md-7">
												<label class="addadvenceSearch showhidebutton sky-blue "> <i class="fa fa-angle-double-down" aria-hidden="true"></i> <spring:message code="import.show.more" />
												</label>
											</div>
										</div>
									</div>
									<div class="hidenAdvanceSrchFields" style="display: none;">
										<div class="row marg-bottom-10 indusCatgryToHideOnGlobal">
											<div class="col-md-3 col-sm-4">
												<label class="marg-top-10"> <spring:message code="import.industry.category" />
												</label>
											</div>
											<div class="col-md-4 col-sm-5">
												<div class="input-group search_box_gray">
													<form:select path="industryCategories" class="chosen-select" id="chosenCategoryAll">
														<form:option value="">
															<spring:message code="import.select.category" />
														</form:option>
														<form:options items="${industryCategories}" itemValue="id"></form:options>
													</form:select>
												</div>
											</div>
										</div>
										<div class="row marg-bottom-10 naicsToHideOnLocal" style="display: none;">
											<div class="col-md-3 col-sm-4">
												<label class="marg-top-10"> <spring:message code="import.naics.category" />
												</label>
											</div>
											<div class="col-md-4 col-sm-5">
												<div class="input-group search_box_gray">
													<form:select path="naicsCode" class="chosen-select" id="chosenCategoryAllNaics">
														<form:option value="">
															<spring:message code="import.select.category" />
														</form:option>
														<form:options items="${naicsCategories}" itemValue="id"></form:options>
													</form:select>
												</div>
											</div>
										</div>
										<div class="row marg-bottom-10">
											<div class="col-md-3">
												<label class="marg-top-10"> <spring:message code="import.project.name" />
												</label>
											</div>
											<div class="col-md-4">
												<spring:message code="suppliers.project.name.placeholder" var="projname" />
												<form:input path="projectName" class="form-control marg-bottom-10" type="text" id="projectName" placeholder="${projname}" />
												<div class="">
													<label class="hidenAdvanceSrchFields showhidebutton sky-blue"> <i class="fa fa-angle-double-up" aria-hidden="true"></i> <spring:message code="import.show.less" />
													</label>
												</div>
											</div>
										</div>
									</div>
									<!--  Start test category here -->
									<div class="row marg-bottom-10">
										<div class="col-md-3">&nbsp;</div>
										<div class="col-md-6">
											<input type="button" value='<spring:message code="application.search" />' class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-top-20 ${!buyerReadOnlyAdmin ? '' : 'disabled' }" id="searchSupplier">
											<input type="button" style="margin-left:10px;" value='<spring:message code="application.reset" />' class="btn btn-black ph_btn_midium hvr-pop hvr-rectangle-out marg-top-20 ${!buyerReadOnlyAdmin ? '' : 'disabled' }" id="resetSearchSupplier">
										</div>
									</div>
								</form:form>
							</div>
						</div>
					</div>
					<div class="panel sum-accord">
						<div class="panel-heading">
							<h4 class="panel-title">
								<a data-toggle="collapse" class="accordion" href="#collapseAssignFormSupplier" id="idCollapseAssignFormSupplier"><spring:message code="supplier.assign.form.label" /> </a>
							</h4>
						</div>
					</div>
					<div id="collapseAssignFormSupplier" class="panel-collapse collapse">
						<div class="panel-body pad_all_15  border-bottom">
							<div class="import-supplier-inner-first-new pad_all_15 global-list">
								<form:form id="assignSupplierForm" class="form-horizontal" method="post" action="${pageContext.request.contextPath}/buyer/assignFormToSupplier" modelAttribute="supplierAssignFormPojo">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									
									<div class="row marg-bottom-10">
										<div class="col-md-3 col-sm-4">
											<label class="marg-top-10"> <spring:message code="assign.form.selected.suppliers" />
											</label>
										</div>
										<div class="col-md-4 col-sm-5">
											<div class="input-group search_box_gray" id="chosenSupplierIdDiv">
												<spring:message code="select.suppliers.placeholder" var="supplierSelect" />
												<form:select  path="supplierIds" class="choosenAssignSuppliers chosen-select" id="chosenSupplierIds" data-placeholder="${supplierSelect}" name="supplierIds" data-validation="requiredSuppliers" multiple="multiple">
													<c:forEach items="${favSupplierList}" var="supp">
														<c:if test="${supp.id == '-1' }">
															<form:option value="-1" label="${supp.companyName}" disabled="true" />
														</c:if>
														<c:if test="${supp.id != '-1' }">
															<form:option value="${supp.id}" label="${supp.companyName}" />
														</c:if>
													</c:forEach>
												</form:select>
											</div>
										</div>
										<div class="col-md-3 col-sm-4">
											<div class="marg-top-10" > 
												<label><form:checkbox path="assignToAllSuppliers" value="" id="assignToAllSuppliers" name="assignToAllSuppliers" class="custom-checkbox" /> <spring:message code="assign.form.allActive.suppliers" /></label>  
											</div>
										</div>
 										
									</div>
									<div class="row marg-bottom-10">
										<div class="col-md-3 col-sm-4">
											<label class="marg-top-10"> <spring:message code="select.supplier.form.label" />
											</label>
										</div>
										<div class="col-md-4 col-sm-5">
											<div class="input-group search_box_gray">
												<spring:message code="select.supplier.form.label" var="formsSelect"/>
												<form:select  path="supplierFormIds" class="chosen-select" id="chosenSupplierForms" data-placeholder="${formsSelect}"  name="supplierFormIds"  data-validation="required" multiple="multiple">
													<c:forEach items="${supplierFormList}" var="item">
															<option value="${item.id}">${item.name}</option>
													</c:forEach>
												</form:select>
											</div>
										</div>
										<div class="col-md-3 col-sm-4">
											<div class="marg-top-10">
												<label> <form:checkbox path="reassignForm" value="" id="reassignForm" name="reassignForm" class="custom-checkbox" /><spring:message code="reassign.form.selected.suppliers"/></label>
											</div>
										</div>
 
									</div>
									
									<!--  Start test category here -->
									<div class="row marg-bottom-10">
										<div class="col-md-3">&nbsp;</div>
										<div class="col-md-6">
											<button  type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-top-20 ${!buyerReadOnlyAdmin ? '' : 'disabled' }" >
												<spring:message code="assign.form.button.label" />
											</button>
										</div>
									</div>
								</form:form>
							</div>
						</div>
					</div>
		
					<div class="lower-bar-search-contant">
						<div class="lower-bar-search-contant-heading gray-up-bg">
							<div class="row">
								<div class="col-md-9 col-sm-12">
									<label> <spring:message code="import.search.result" />
									</label>
								</div>
								<div class="col-md-3 col-sm-12">
									<h4 class="pull-right" style="margin-top: 10px;">
										<spring:message code="suppliers.active.favourite" />
										: ${totalSupplierCount}
									</h4>
									<c:if test="${!empty pendingSupplierCount}">
										<h4 class="pull-right" style="margin-top: 10px;">
											<spring:message code="suppliers.pending.activation" />
											: ${pendingSupplierCount}
										</h4>
									</c:if>
								</div>
							</div>
						</div>
					</div>
							<jsp:include page="favouriteSuppliersList.jsp" />
				</div>
			</div>
		</div>
	</div>
</div>
<div id="supplierAddAfavorite" class="modal fade" role="dialog">
	<form action="" id="addListForm">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		<div class="modal-dialog" style="width: 90%; max-width: 800px;">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center">
						<spring:message code="import.supplier.details" />
					</h3>
					<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">x</button>
				</div>
				<div class="modal-body">
					<input type="hidden" name="supplierId" id="supplierId" value="" />
					<div id="hideWarn">
						<div class="alert alert-warning" id="idGlobalWarn">
							<div class="bg-orange alert-icon">
								<i class="glyph-icon icon-exclamation"></i>
							</div>
							<div class="alert-content">
								<h4 class="alert-title">Warning</h4>
								<p id="warnMsg"></p>
							</div>
						</div>
					</div>

					<div class="row ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }">
						<div class="col-md-4">
							<label> <spring:message code="import.communication.email" />
							</label>
						</div>
						<div class="col-md-8">
							<input type="text" data-validation="required" class="form-control marg-bottom-10" id="sEmail" value="sEmail" maxlength="128" />
						</div>
					</div>
					<div class="row ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }">
						<div class="col-md-4">
							<label> <spring:message code="import.full.name" />
							</label>
						</div>
						<div class="col-md-8">
							<input type="text" data-validation="required" class="form-control marg-bottom-10" id="sName" value="sName" maxlength="128" />
						</div>
					</div>
					<div class="row ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }">
						<div class="col-md-4">
							<label> <spring:message code="import.designation" />
							</label>
						</div>
						<div class="col-md-8">
							<input type="text" data-validation="required" class="form-control marg-bottom-10" id="sdesgn" value="sdesgn" maxlength="128" />
						</div>
					</div>
					<div class="row ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }">
						<div class="col-md-4">
							<label> <spring:message code="import.contact.number" />
							</label>
						</div>
						<div class="col-md-8">
							<input type="text" data-validation="required number" data-validation-ignore="+ " class="form-control marg-bottom-10" id="sCnumb" value="sCnumb" maxlength="16" />
						</div>
					</div>
					<div class="row ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }">
						<div class="col-md-4">
							<label><spring:message code="application.fax.number" /></label>
						</div>
						<div class="col-md-8">
							<!-- <input type="text" class="form-control marg-bottom-10" data-validation="custom" maxlength="32" data-validation-ignore="+ " data-validation-regexp="^[\d\s]*$" data-validation-error-msg="Only numbers allowed" id="sTnumb" value="sTnumb" /> -->

							<input type="text" class="form-control marg-bottom-10" data-validation-length="0-32" data-validation-optional="true" id="sTnumb" value="sTnumb" />
						</div>
					</div>
					<div class="row ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }">
						<div class="col-md-4">
							<label><spring:message code="suppliers.code" /></label>
						</div>
						<div class="col-md-8">
							<input type="text" class="form-control marg-bottom-10" data-validation="length" data-validation-length="0-100" data-validation-optional="true" id="sVenderCode" />
						</div>
					</div>

					<c:choose>
						<c:when test="${!buyerReadOnlyAdmin and canEdit}">
							<div class="row">
								<div class="col-md-4">
									<label><spring:message code="import.select.category" /></label>
								</div>
								<div class="col-md-8 marg-bottom-10">
									<input type="text" id="demo-input-local" name="blah"/>
									<div class="col-md-12 selectListAjax"></div>
									<div id="catValErr"></div>
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<div class="row">
								<div class="col-md-4 disabled">
									<label><spring:message code="import.select.category" /></label>
								</div>
								<div class="col-md-8 marg-bottom-10">
									<textarea class="form-control marg-bottom-10" id="collected-values" readonly style="height: 100px;"></textarea>
								</div>
							</div>
						</c:otherwise>
					</c:choose>


					<div class="row ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }">
						<div class="col-md-4">
							<label> <spring:message code="import.supplier.tags" />
							</label>
						</div>
						<div class="col-md-8 marg-bottom-10" style="color: rgb(43, 47, 51); font-weight: bold;  font-family: open_sansregular,Helvetica Neue,Helvetica,Arial,sans-serif;">
							<select multiple="multiple" id="supplierTags" name="supplierTags"  data-placeholder="Select Supplier Tags" class="chosen-select">
								<c:forEach items="${searchSupplierTags}" var="item1">
									<option value="${item1.id}">${item1.supplierTags}</option>
								</c:forEach>
							</select>
						</div>
					</div>

					<div class="row ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }">
						<div class="col-md-4">
							<label> <spring:message code="Product.category" />
							</label>
						</div>
						<div class="col-md-8 marg-bottom-10">
							<input type="text" id="prod-input-local" name="productCategory" />
							<div class="col-md-12 selectListAjax1"></div>
							<div id="prodValErr"></div>
						</div>
					</div>

					<div class="row ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }">
						<div class="col-md-4">
							<label> <spring:message code="import.status" />
							</label>
						</div>
						<div class="col-md-8 marg-bottom-10">
							<select id="status" name="status" class="chosen-select">
								<c:forEach items="${favSupplierstatusList}" var="item">
									<option value="${item}">${item}</option>
								</c:forEach>
							</select>
						</div>
					</div>

					<div class="row ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }">
						<div class="col-md-4">
							<label>Ratings</label>
						</div>
						<div class="col-md-8">
							<input type="text" name="ratings" id="ratings" onkeypress="return isNumberKey(event)" placeholder='<spring:message code="numeric.value.placeholder" />' class="form-control marg-bottom-10" data-validation-regexp="^[\d,]{1,16}(\.\d{1,${decimal}})?$" />
						</div>
					</div>

					<div class="row  ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }">
						<div class="col-md-4">
							<label>Subsidiary</label>
						</div>
						<div class="col-md-8">
							<div class="checkbox checkbox-info">
								<input type="checkbox" name="subsidiary" id="subsidiary" style="margin: 3px;"/>
								<span style="padding-left: 25px;"> Mark supplier as subsidiary</span>
							</div>
						</div>
					</div>

				</div>
				<div class="modal-footer  text-center">
					<input class="btn btn-info hvr-pop hvr-rectangle-out addToListBtnWishlistPop  ph_btn_midium  ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }" id="Btn" type="button" value="Btn"/>

					<button class="btn btn-danger  ph_btn_midium  ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }" type="button" data-dismiss="modal" id="suspend">
						<spring:message code="application.suspend" />
					</button>
					<button class="btn btn-danger  ph_btn_midium  ${(buyerReadOnlyAdmin or !canEdit)  ? 'disabled' : '' }" type="button" data-dismiss="modal" id="bList" value="">BlackList</button>
					<button class="btn btn-black hvr-pop hvr-rectangle-out1  ph_btn_midium" type="button" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</form>
</div>

<div class="modal fade" id="black" role="dialog">
	<div class="modal-dialog for-delete-all reminder" style="width: 90%; max-width: 800px;">
		<!-- Modal content-->
		<div class="modal-content height-60">
			<div class="modal-header">
				<h3 id="blackListPop"></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form class="form-pos-rel" action="${pageContext.request.contextPath}/buyer/suppBlackUploadDocument?${_csrf.parameterName}=${_csrf.token}" method="POST" enctype="multipart/form-data">
				<input type="hidden" id="suppId" value="" name="suppId">
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label id="confirmation"> </br>Reason :
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea rows="4" cols="50" class="width-100" placeholder="Mention reason .." data-validation="required length" data-validation-length="max500" id="blackListRemark" name="blackListRemark"></textarea>
						</div>

					</div>
				</div>
				<div class="row height-100">
					<div class="col-md-12">
						<div class="col-md-12 pad_all_15">
							<div id="appendFile">
							</div>
							<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
							<div class="progressbar flagvisibility" data-value="0">
								<div class="progressbar-value bg-purple">
									<div class="progress-overlay"></div>
									<div class="progress-label">0%</div>
								</div>
							</div>
							<button name="addMore" id="addMoreFiles" class="more-btn btn btn-info ph_btn_small hvr-pop hvr-rectangle-out mr-10">Add Attachment</button>
							<div>
								Note:<br />
								<ul>
									<li>Max allowed file size is ${ownerSettings.fileSizeLimit} MB</li>
									<li>Allowed file extensions: ${fileType}.</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
				<div class="pad_all_15 width-100 border-top-width-1 center-btn footer-abs-pos">
					<button type="submit" class="w-90 btn btn-info ph_btn_small hvr-pop hvr-rectangle-out mr-10" id="submitStatus">Blacklist</button>
					<button type="button" class="w-90 btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">Cancel</button>
				</div>
			</form>
		</div>
	</div>
</div>

<div class="modal fade" id="suspendSupplier" role="dialog">
	<div class="modal-dialog for-delete-all reminder" style="width: 90%; max-width: 800px;">
		<!-- Modal content-->
		<div class="modal-content height-60">
			<div class="modal-header">
				<h3 id="suspendPop"></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form class="form-pos-rel" action="${pageContext.request.contextPath}/buyer/suppSuspendUploadDocument?${_csrf.parameterName}=${_csrf.token}" method="POST" enctype="multipart/form-data">
				<input type="hidden" id="suppIdforSusPend" value="" name="suppId">
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label id="confirmationSuspend"> </br>Reason :
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea rows="4" cols="50" class="width-100" placeholder="Mention reason ...." data-validation="required length" data-validation-length="max500" id="suspendRemark" name="suspendRemark"></textarea>
						</div>
						<div class="col-sm-6 col-md-6 col-xs-6" id="suspendRange">
							<div class="input-prepend input-group">
								<jsp:useBean id="now" class="java.util.Date" />
								<label>Suspension period</label> <input autocomplete="off" name="suspendDuration" data-date-start-date="0d" id="daterangepicker-time" data-startdate="" class="form-control for-clander-view for-clander-view" type="text" data-validation="required" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P"></input>
							</div>
						</div>
					</div>
				</div>
				<div class="row height-100">
					<div class="col-md-12">
						<div class="col-md-12 pad_all_15">
							<div id="appendFileSuspend">
							</div>
							<button name="addMore" id="addMoreFilesSuspend" class="more-btn btn btn-info ph_btn_small hvr-pop hvr-rectangle-out mr-10">Add Attachment</button>
							<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
							<div class="progressbar flagvisibility" data-value="0">
								<div class="progressbar-value bg-purple">
									<div class="progress-overlay"></div>
									<div class="progress-label">0%</div>
								</div>
							</div>
							<div>
								Note:<br />
								<ul>
									<li>Max allowed file size is ${ownerSettings.fileSizeLimit} MB</li>
									<li>Allowed file extensions: ${fileType}.</li>
								</ul>
							</div>
						</div>
						<input type="hidden" value="" id="activeSuspend" name="activeSuspend">

					</div>
				</div>
				<div class="pad_all_15 width-100 border-top-width-1 center-btn footer-abs-pos">
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out mr-10 wrp_btn" id="submitStatusSuspend">Suspend</button>
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out mr-10 wrp_btn" id="Reschedule">Reschedule</button>
					<button type="button" class="w-90 btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">Cancel</button>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal fade" id="uploadSupplierModel" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.supplier" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>

			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<div class="radio">
					<label class="RightPad"> <input type="radio" name="isUploadNewSupplier" checked id="isUploadNewSupplier"> <spring:message code="suppliers.add.suppliers.radio" />
					</label> <label class="RightPad"> <input type="radio" name="isUploadNewSupplier" id="isUpdateSupplier"> <spring:message code="suppliers.update.suppliers.radio" />
					</label>
					<p class="text-center-msg" id="updateSupplier">
						<spring:message code="suppliers.company.email.country" />
					</p>
				</div>

				<button type="submit" class=" btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="uploadSupplier">
					<spring:message code="supplier.upload.button" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="myModalDelOtherDocConfirm" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
			</div>
			<div class="modal-body">
				<label><spring:message code="suppliers.sure.delete.document" /></label> <input type="hidden" id="deleteOtherDocId" value="" /> <input type="hidden" id="otherDocFileName" value="" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a type="button" href="javascript:void(0);" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1" data-dismiss="modal"><spring:message code="application.cancel" /></a>
				<button id="idConfirmDeleteOtherDocument" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">
					<spring:message code="application.remove" />
				</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<!-- this is added for second  token input on same page because we want to show into modal view  -->
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput1.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/favoriteSupplier.js?4"/>"></script>
<%-- <script type="text/javascript" src="<c:url value="/resources/js/view/test.js"/>"></script>    --%>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.min.js"/>"></script>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'date'
	});
</script>
<!-- EQul height js-->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<!-- Theme layout -->
<!-- daterange picker js and css start -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js"/>"></script>
<script>

$.validate({
	lang : 'en',
	modules : 'file'
});


function doCancel() {
	$('#supplierTags').val('').trigger('chosen:updated');
}

	$(document).ready(function() {
		var i=1;	
		$('#addMoreFiles').on('click',function (e){
			i++;
			e.preventDefault();
			var html='<div class="row hideThis">'
			html+='<div class="col-md-6">'
			html+='<spring:message code="event.doc.file.descrequired" var="descrequired" />'
			html+='<spring:message code="event.doc.file.maxlimit" var="maxlimit" />'
			html+='<spring:message code="event.document.filedesc" var="filedesc" />'
			html+='<spring:message code="meeting.doc.file.mimetypes" var="mimetypes" />'
			html+='<label>File Description</label> <input class="form-control mb-10" name="docDescription" id="docDescription" data-validation=" required length" data-validation-length="max250" type="text" placeholder="${filedesc} ${maxlimit}">'
			html+='</div>'
			html+='<div class="col-md-6">'
			html+='<label>Select File</label>'
			html+='<div class="pos-rel">'
			html+='<div class="fileinput fileinput-new input-group w-88 " data-provides="fileinput">'
			html+='<spring:message code="meeting.doc.file.length" var="filelength" />'
			html+='<spring:message code="meeting.doc.file.mimetypes" var="mimetypes" />'
			html+='<div data-trigger="fileinput" class="form-control">'
			html+='<span class="fileinput-filename show_name"></span>'
			html+='</div>'
			html+='<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="application.selectfile" />'
			html+='</span> <span class="fileinput-exists"> <spring:message code="event.document.change" />'
			html+='</span> <input name="docs" id="docs" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialog'+i+ '"data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" data-validation=" required extension size" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="${mimetypes}">'			
			html+='</span>'
			html+='</div>'
			html+='<div id="Load_File-error-dialog'+i+'"style="width: 100%; float: left; margin: 0 0 0 0;"></div>'
			html+='<button class="close select-abs closePopUp" type="button">&times;</button>'
			html+='</div>'
			html+='</div>'
			html+='</div>'
			$('#appendFile').append(html);
			
		});

	
		
		$('#addMoreFilesSuspend').on('click',function (e){
			
			e.preventDefault();
			i++;
			var html='<div class="row hideThis">'
			html+='<div class="col-md-6 mt-10">'
			html+='<spring:message code="event.doc.file.descrequired" var="descrequired" />'
			html+='<spring:message code="event.doc.file.maxlimit" var="maxlimit" />'
			html+='<spring:message code="event.document.filedesc" var="filedesc" />'
			html+='<label>File Description</label> <input class="form-control mb-10" name="docDescription" id="docDescription" data-validation=" required length" data-validation-length="max250" type="text" placeholder="${filedesc} ${maxlimit}">'
			html+='</div>'
			html+='<div class="col-md-6 mt-10">'
			html+='<label>Select File</label>'
			html+='<div class="pos-rel">'
			html+='<div class="fileinput fileinput-new input-group  w-88" data-provides="fileinput">'
			html+='<spring:message code="meeting.doc.file.length" var="filelength" />'
			html+='<spring:message code="meeting.doc.file.mimetypes" var="mimetypes" />'
			html+='<div data-trigger="fileinput" class="form-control">'
			html+='<span class="fileinput-filename show_name"></span>'
			html+='	</div>'
			html+='<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="application.selectfile" />'
			html+='</span> <span class="fileinput-exists"> <spring:message code="event.document.change" />'
			html+='</span> <input name="docs" id="docs" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialog'+i+'"data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" data-validation="required extension size" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="${mimetypes}">'
			html+='</span>'
			html+='</div>'
			html+='<div id="Load_File-error-dialog'+i +'"style="width: 100%; float: left; margin: 0 0 0 0;"></div>'
			html+='<button class="close select-abs closePopUp" type="button">&times;</button>'
			html+='</div>'
			html+='</div>'
			html+='</div>'
			
			$('#appendFileSuspend').append(html);
		});
		
		$(document).delegate('.closePopUp', 'click', function(){
		 $(this).closest("div.hideThis").hide();
	});	

		
		
		
		$('#bList').click(function (){
		var sId = $('#supplierId').val();
		$('#suppId').val(sId);
		$('#black').modal('show');
	});
		$('#suspend').click(function (){
			var sId = $('#supplierId').val();
			$('#suppIdforSusPend').val(sId); 
			$('#suspendSupplier').modal('show');
		});
		
		$('#suspend').click(function (){
			var sId = $('#supplierId').val();
			$('#suppIdforSusPend').val(sId); 
			$('#suspendSupplier').modal('show');
		});
		
		
		
		
		
		$('[data-toggle="tooltip"]').tooltip({
			placement : "left"
		});
		$(".toggle").click(function() {
			$(this).parent().toggleClass("highlight");
		});

		$("#test-select").treeMultiselect({
			enableSelectAll : true,
			sortable : true
		});
		
	
		$("#assignToAllSuppliers").on('change', function() {
			if($('#assignToAllSuppliers').is(':checked')) {
				$("#chosenSupplierIdDiv").addClass("disabled");
				$("#chosenSupplierIds").validate();
				$("#chosenSupplierIds").val('').trigger("chosen:updated");
			}else{
				$("#chosenSupplierIdDiv").removeClass("disabled");
			}
		});

	$.formUtils.addValidator({
		  name : 'requiredSuppliers',
		  validatorFunction : function(value, $el, config, language, $form) {		  
			  var response = true;
				var supplierIdList = [];
				$.each($("#chosenSupplierIds option:selected"), function() {
					supplierIdList.push($(this).val());
				});
			  var assignToAllSuppliers=false;
				if($("#assignToAllSuppliers").is(':checked')){
					assignToAllSuppliers=true;
				}
			  if((supplierIdList == "" || supplierIdList.length==0) && !assignToAllSuppliers){
				  response = false;
			  }
			  return response;
		  },
		  errorMessage : 'This is a required field',
		  errorMessageKey: 'requiredSuppliersCheck'
		});

		$("#assignSupplierForm").submit(function(){
			$('#loading').show();
		});
		
		
		$(document).on("keyup", ".choosenAssignSuppliers  ~ .chosen-container input", keyDebounceDelay(function(e) {
			// ignore arrow keys
			switch (e.keyCode) {
			case 17: // CTRL
				return false;
				break;
			case 18: // ALT
				return false;
				break;
			case 37:
				return false;
				break;
			case 38:
				return false;
				break;
			case 39:
				return false;
				break;
			case 40:
				return false;
				break;
			}
			var supplier = $.trim(this.value);
			if (supplier.length > 2 || supplier.length == 0 || e.keyCode == 8) {
				reloadSupplierList(supplier);
			}
		}, 650));
		
		function keyDebounceDelay(callback, ms) {
			var timer = 0;
			return function() {
				var context = this, args = arguments;
				clearTimeout(timer);
				timer = setTimeout(function() {
					callback.apply(context, args);
				}, ms || 0);
			};
		}
		function reloadSupplierList(favSupp) {
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url : getContextPath() + '/buyer/searchSupplierName',
				data : {
					'search' : favSupp,
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data) {
					var selectElement=$('#chosenSupplierIds');
					var html = '';

					if (data != '' && data != null && data.length > 0) {
						if (selectElement.attr('multiple') !== undefined) {
							console.log('Clearing non selected items...');
							selectElement.find('option').not(':selected').remove();
						} else {
							if (selectElement.find('option:first').val() === '') {
								console.log('Clearing all except first...');

								selectElement.find('option').each(function() {
									if (this.value == '' || this.value === selectElement.val()) {
										console.log(' Not Removing ', this);
									} else {
										this.remove();
									}
								});

							} else {
								console.log('Clearing all items...');
								selectElement.find('option').not(':selected').remove();
							}
						}
						$.each(data, function(key, value) {

							var selectedIds = selectElement.attr("selected-id");
							if (value.id == null || value.id == '') {
								html += '<option value="" disabled>' + value.companyName + '</option>';
							} else if (value.id == '-1') {
								html += '<option value="-1" disabled>' + value.companyName + '</option>';
							} else {
								if (selectElement.find('option[value=' + value.id + ']:selected').val() === undefined) {
									var found = false;
									if (selectedIds !== undefined) {
										$('[' + selectedIds + ']').each(function(index) {
											if ($(this).attr(selectedIds) === value.id) {
												// html += '<option value="' + value.id + '" data-name="'+value.name+'" disabled>' + value.name + '</option>';
												found = true;
												return false;
											}
										});
									}
									if (!found) {
										html += '<option value="' + value.id + '" data-name="' + value.companyName + '">' + value.companyName + '</option>';
									}
								}
							}
						});
					}

					selectElement.append(html);
					selectElement.trigger("chosen:updated")
					$('#loading').hide();

					
				},
				error : function(error) {
					console.log(error);
				}
			});
		}
	});
</script>
<script>
	$(function() {

		$(document).on("change", "#load_file", function() {
			$(".show_name").html($(this).val());
		});

		var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
		$("#tags").autocomplete({
			source : availableTags
		});
		$("#tagres").autocomplete({
			source : availableTags
		});
	});
</script>

<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chardin/chardinjs.min.js"/>"></script>
