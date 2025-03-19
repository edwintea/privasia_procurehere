<%@page import="org.apache.velocity.runtime.parser.node.GetExecutor"%>
<%@page import="org.w3c.dom.Document"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ROLE_CONTRACT_EDIT')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_CONTRACT_VIEW_ONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.id" var="loggedInUserId" />

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">

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

.modal-body input {
	margin: 0px !important;
}

</style>

<div id="page-content" view-name="product-contract" class="white-bg">
	<div class="container">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" /></a></li>
			<c:url value="/buyer/productContractList" var="listUrl" />
			<li><a id="listLink" href="${listUrl} "> <spring:message code="product.contracts.list.dashboard" /></a></li>
			<li class="active">Contract Item Details</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="contract.item.list" />
			</h2>
			<h2 class="trans-cap pull-right">
				<spring:message code="application.status" />: ${productContract.status}
			</h2>
		</div>
		<div>
			<jsp:include page="contractHeader.jsp"></jsp:include>
		</div>
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		
		<form:form id="productListMaintenanceForm" data-parsley-validate="" commandName="productContract" action="saveProductContract?${_csrf.parameterName}=${_csrf.token}" method="post" cssClass="form-horizontal bordered-row" enctype="multipart/form-data">
			<form:hidden path="id" id="id" />
			<input type="hidden" value="${loggedInUserId}" id="loggedInUserId">
			<input type="hidden" value="${productContract.id}" id="productContractId">
			<input type="hidden" value="${productContract.status}" id="contractStatus">
	       	<c:if test="${not empty productContract.id}">
				
				<c:if test="${ (!buyerReadOnlyAdmin and canEdit) and productContract.isEditable}">
					<button type="button" class="btn btn-plus btn-info top-marginAdminList" style="margin-bottom: 20px;" id="createContractItemId"  >
						<spring:message code="contract.item.create" />
					</button>
				</c:if>
				<div class="container-fluid col-md-12">
					<div class="row">
						<div class="col_12">
							<div class="white_box_brd pad_all_15">
								<section class="index_table_block">
									<div class="row">
										<div class="col-xs-12">

											<div class="ph_tabel_wrapper scrolableTable_UserList">
												<table id="contractItemTableList" class=" display table table-bordered noarrow" cellspacing="0" width="100%">
													<thead>
														<tr class="tableHeaderWithSearch">
															<th search-type="" class="align-left width_100_fix"><spring:message code="application.action" /></th>
															<th search-type="text" class="align-left"><spring:message code="product.Contract.itemNum" /></th>
															<th search-type="text" class="align-left"><spring:message code="product.itemName" /></th>
															<th search-type="text" class="align-left"><spring:message code="product.itemCode" /></th>
															<th search-type="text" class="align-left"><spring:message code="contract.item.category" /></th>
															<th search-type="text" class="align-left"><spring:message code="storage.uom" /></th>
															<th search-type="text" class="align-right"><spring:message code="product.item.Quantity" /></th>
															<th search-type="text" class="align-right"><spring:message code="product.item.BQuantity" /></th>
															<th search-type="text" class="align-right"><spring:message code="productlist.unitPrice.item" /></th>
															<th search-type="text" class="align-right"><spring:message code="contract.item.tax" /></th>
															<th search-type="select" search-options="itemTypeList" class="align-left"><spring:message code="product.item.type" /></th>
															<th search-type="text" class="align-left"><spring:message code="contract.item.brand" /></th>
															<th search-type="text" class="align-left"><spring:message code="storage.Location" /></th>
															<th search-type="text" class="align-left"><spring:message code="label.businessUnit" /></th>
															<th search-type="text" class="align-left"><spring:message code="label.costcenter" /></th>
															
														</tr>
													</thead>
													<tbody>
													</tbody>
												</table>
											</div>

											<c:if test="${productContract.status == 'DRAFT' or ( (productContract.status == 'ACTIVE' || productContract.status == 'SUSPENDED') and eventPermissions.owner ) }">
											<div class="row text-left">
												<c:url var="previous" value="/buyer/productContractListEdit?id=${productContract.id}" />
												<a href="${previous}" class="btn btn-black ph_btn_midium hvr-pop float-left marg-top-20 marg-left-10" id="previousButton"><spring:message code="application.previous" /></a>
												<c:if test="${productContract.status == 'DRAFT'  and !buyerReadOnlyAdmin and eventPermissions.owner}">
													<a href="#confirmCancelContract" role="button" class="btn btn-danger hvr-pop hvr-rectangle-out1 ph_btn_midium marg-top-20 marg-right-10 float-right draf  ${ buyerReadOnlyAdmin ? 'disabled' : '' }" data-toggle="modal"><spring:message code="event.cancel.draft" /></a>
												</c:if>
												</a>
												<c:if test="${!buyerReadOnlyAdmin}">
													<a href="${pageContext.request.contextPath}/buyer/contractSummary/${productContract.id}" id="nextButton" class="btn btn-info ph_btn_midium hvr-pop float-right hvr-rectangle-out marg-top-20 marg-right-10">Next</a>
												</c:if>
											</div>
											</c:if>
										</div>
									</div>
								</section>
							</div>
						</div>
					</div>
					<div class="row" style="height: 10px;"></div>
				</div>
			</c:if>
		</form:form>
	</div>
</div>
		
<div id="contractItemModel" class="modal fade" role="dialog">
	<form action="" id="addcontractItemForm" autocomplete="off" >
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
		<input type="hidden" id="productContractId" value="${productContract.id}" />
		<div class="modal-dialog" style="width: 90%; max-width: 60%;">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="modal-title text-center">
						<spring:message code="contract.item" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">x</button>
				</div>
				<div class="modal-body">
 					<div class="row marg-bottom-20">
						<div class="col-md-6 "></div>
						<div class="col-md-2 ">
							<label class="marg-top-10">Free Text Item</label>
						</div>
						<div class="col-md-4 marg-top-10 ${ buyerReadOnlyAdmin ? "disabled" : ""}" >
							<input type="checkbox" id="idFreeTextItem" name="freeTextItemEntered" class="custom-checkbox" title="Free Text Contract Item" />
						</div>
					</div>
					
					<div class="row marg-bottom-20">
						<div class="col-md-2" id="productCodeLabel">
							<label class="marg-top-10"> 
								<spring:message code="contract.product.code" />
							</label>
						</div>
						<div id="idItemCode" class="col-md-4 ${ buyerReadOnlyAdmin ? "disabled" : ""}" >
							<input type="text" data-validation="length" data-validation-length="0-50" id="itemCode" name="itemCode" class="form-control" placeholder="Enter Item Code">
						</div>
						<div class="col-md-2" id="productItemLabel">
							<label class="marg-top-10"> 
								<spring:message code="contract.product.item" />
							</label>
						</div>
						<div id="idItemFreeText" class="col-md-4 ${ buyerReadOnlyAdmin ? "disabled" : ""}" style="display:none;">
							<input type="text" data-validation="required length" data-validation-length="1-250" id="itemName" name="itemName" class="form-control" placeholder="Enter Item Name">
						</div>

						<div id="idItemList" class="col-md-4 ${ buyerReadOnlyAdmin ? "disabled" : ""}">
							<spring:message code="product.item.empty" var="required" />
							<select name="productItem" id="chosenProductItem" class="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<option value="">
									<spring:message code="product.select.item" />
								</option>
								<c:forEach items="${productItemList}" var="item">
									<c:if test="${empty item.id}">
										<option value="" disabled>${item.productCode} - ${item.itemName}</option>
									</c:if>
									<c:if test="${!empty item.id and item.id != productContract.businessUnit.id}">
										<option value="${item.id}">${item.productCode} - ${item.itemName}</option>
									</c:if>
									<c:if test="${!empty item.id and item.id == productContract.businessUnit.id}">
										<option value="${item.id}" selected>${item.productCode} - ${item.itemName}</option>
									</c:if>
								</c:forEach>
							</select>
						</div>
					</div>
					
					<div class="row marg-bottom-20">
						<div class="col-md-2">
							<label for="idProductItemType" class="marg-top-10"><spring:message code="product.item.type" /> </label>
						</div>
						<div class="col-md-4 contractCreatedFromEvent ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<select name="productItemType" id="chosenProductItemType" class="chosen-select">
								<option value="">Select Item Type</option>
								<c:forEach items="${itemTypeList}" var="type">
									<option value="${type}">${type}</option>
								</c:forEach>
							</select>
						</div>

						<!-- Category -->
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="contract.item.category" />
							</label>
						</div>
						<div class="col-md-4 contractCreatedFromEvent ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<spring:message code="product.category.required" var="categoryRequired" />
							<select name="productItemCategory" id="chosenProductCategory" class="chosen-select" data-validation="required" data-validation-error-msg-required="${categoryRequired}">
								<option value="">
									<spring:message code="Product.select.category" />
								</option>
								<c:forEach items="${productCategoryList}" var="pc">
									<c:if test="${pc.id == '-1'}">
										<option value="-1" disabled>${pc.productCode} - ${pc.productName}</option>
									</c:if>
									<c:if test="${pc.id != '-1'}">
										<option value="${pc.id}">${pc.productCode} - ${pc.productName}</option>
									</c:if>
								</c:forEach>
							</select>
						</div>

					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="product.item.Quantity" />
							</label>
						</div>
						<div class="col-md-4 ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<spring:message code="contract.quantity.empty" var="required" />
							<spring:message code="event.document.quantity" var="quantity" />
							<input type="text" data-validation="required number custom" 
								data-validation-error-msg-required="${required}" name="quantity" 
								class="form-control" id="itemQuantity" 
								placeholder="${quantity}" data-validation-ignore=",." 
								data-validation-allowing="float"
								data-validation-regexp="^[\d,]{1,10}(\.\d{1,${productContract.decimal}})?$"
								data-validation-error-msg="Only numbers allowed, length should be less than 10 or please check the decimal to be allowed">
						</div>

						<div class="col-md-2 ">
							<label class="marg-top-10" for="chosenUom"> <spring:message code="storage.uom" />
							</label>
						</div>
						<div class="col-md-4 contractCreatedFromEvent ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<spring:message code="contract.uom.empty" var="required" />
							<select name="uom" id="chosenUom" class="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<option value="">
									<spring:message code="product.select.uom" />
								</option>
								<c:forEach items="${uomList}" var="uom">
									<option value="${uom.id}">${uom.uom}- ${uom.uomDescription}</option>
								</c:forEach>
							</select>
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="productlist.unitPrice.item" />
							</label>
						</div>
						<div class="col-md-4 contractCreatedFromEvent ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<spring:message code="event.document.unitpricing" var="unitpricing" />
							<spring:message code="contract.unit.price.empty" var="required" />
							<input type="text" class="form-control" data-validation="required custom" data-validation-error-msg-required="${required}" name="unitPrice" id="unitPriceId" placeholder="${unitpricing}" data-validation-ignore=","
								data-validation-regexp="^(?:\d+)(?:(?:\d+)|(?:(?:,\d+)?))+(?:\.\d{1,})?$" data-validation-error-msg="Only numbers allowed, length should be less than 10 or please check the decimal to be allowed">
						</div>
						<!-- Tax -->
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="contract.item.tax" />
							</label>
						</div>
						<div class="col-md-4 contractCreatedFromEvent ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<spring:message code="contract.tax.empty" var="required" />
							<spring:message code="contract.tax.price" var="price" />
							<input type="text" name="tax" class="form-control" id="itemTax" data-validation-optional="true" data-validation="custom" placeholder="${price}" data-validation-error-msg-required="${required}" data-validation-ignore="," data-validation-regexp="^(?:\d+)(?:(?:\d+)|(?:(?:,\d+)?))+(?:\.\d{1,})?$"
								data-validation-error-msg="Only numbers allowed, length should be less than 10 or please check the decimal to be allowed">
						</div>

					</div>

					<div class="row marg-bottom-20">					
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="contract.price.per.unit" />
							</label>
						</div>
						<div class="col-md-4 ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<spring:message code="enter.price.per.unit" var="pricePerUnit" />
							<input type="text" class="form-control" data-validation="custom number length" data-validation-optional="true" name="pricePerUnit" id="pricePerUnit" 
							placeholder="${pricePerUnit}" data-validation-ignore=","
							 data-validation-length="1-5"
							data-validation-error-msg="Price Per Unit length should be between 1-5">
						</div>
						<!--  Brand -->
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="contract.item.brand" />
							</label>
						</div>
						<div class="col-md-4 contractCreatedFromEvent ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<spring:message code="contract.brand.quantity" var="itemBrand" />
							<input type="text" id="contractItemBrand" name="contractBrand" maxlength="64" class="form-control marg-bottom-10" placeholder="${itemBrand}" />
						</div>
					</div>


					<div class="row marg-bottom-20">
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="storage.Location" />
							</label>
						</div>
						<div class="col-md-4 ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<spring:message code="product.storage.location.placeholder" var="storageLoc" />
							<spring:message code="product.storage.location.length" var="length" />
							<input name="storageLocation" data-validation="length" data-validation-length="0-32" id="storageLocationId" class="form-control marg-bottom-10" placeholder="${storageLoc}" />
						</div>
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="label.budget.businessUnit" />
							</label>
						</div>
						<div class="col-md-4 ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<select name="businessUnit" id="chosenBusinessUnitId" class="chosen-select"  >
								<option value="">
									<spring:message code="pr.select.business.unit" />
								</option>
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
							</select>
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-2 ">
							<label class="marg-top-10"> <spring:message code="label.budget.costCenter" />
							</label>
						</div>
						<div class="col-md-4 ${ buyerReadOnlyAdmin ? 'disabled':''}">
							<select name="costCenter" id="chosenCostCenter" class="chosen-select" >
								<option value="">
									<spring:message code="pr.select.cost.center" />
								</option>
								<c:forEach items="${costCenterList}" var="cost">
									<c:if test="${cost.id == '-1'}">
										<option value="-1" disabled>${cost.costCenter} - ${cost.description}</option>
									</c:if>
									<c:if test="${cost.id != '-1' }">
										<option value="${cost.id}">${cost.costCenter} - ${cost.description}</option>
									</c:if>
								</c:forEach>
							</select>
						</div>
					</div>

				</div>
				<input type="hidden" id="hiddenId" value="" />
				<div class="modal-footer  text-center">
					<c:if test="${ !buyerReadOnlyAdmin and ( eventPermissions.owner or eventPermissions.editor)}">
						<button type="button" id="saveContractItem" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" >
							<spring:message code="application.save" />
						</button>
					</c:if>
					<button type="button" id="reminderCan" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</form>
</div>

<div class="modal fade" id="deleteContractItem" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="contract.item.delete" /></label>
			</div>
			<input type="hidden" id="deleteId" value="" />
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button id="idConfirmDelete" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" title="Delete"><spring:message code="application.delete" /></button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
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

<script>

	var contractCreatedFromEvent = ${!empty productContract.eventId};

	var decimalLimit = ${productContract.decimal};
	
	var contractStatus = '${productContract.status}';

	<c:if test="${buyerReadOnlyAdmin or !canEdit or eventPermissions.viewer or (!eventPermissions.owner and !eventPermissions.editor and !eventPermissions.viewer)  or (eventPermissions.editor and (productContract.status == 'ACTIVE' or productContract.status == 'SUSPENDED' ))}">
	$(window).bind('load', function() {
		var allowedFields = '.button-previous, #dashboardLink,#bubble,#listLink,.pagination,#previousButton,#nextButton';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if>
	
	var contractItemTableList;
	
	$('document').ready(function() {
	var contractId = $("#productContractId").val();
	
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	
	contractItemTableList = $('#contractItemTableList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
		},
		"processing" : true,
		"deferRender" : true,
		"preDrawCallback" : function(settings) {
			setTimeout(function() { 
				$('div[id=idGlobalError]').hide();
			}, 3000);
			$('#loading').show();
			return true;
		},
		"drawCallback" : function() {
			// in case your overlay needs to be put away automatically you can put it here
			$('#loading').hide();
		},
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/productContractListItem",
			"data" : function(d) {
				d.id = contractId
				//var table = $('#contractItemTableList').DataTable()
				//d.page = (table != undefined) ? table.page.info().page : 0;
				//d.size = (table != undefined) ? table.page.info().length : 10;
				//d.sort = d.columns[d.order[0].column].data + ',' + d.order[0].dir;
			},
			beforeSend : function(xhr) {
				$('#loading').show();
			},
			"error" : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				if (error != undefined) {
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		},
		"order" : [],
		"rowCallback" : function (row, data) {
		    //if it is deleted item, add disable background color
		    if ( data.deleted) {
		        $(row).addClass('deleted_row');
		    }
		},
		"columns" : [ {
			"data" : "id",
			"searchable" : false,
			"orderable" : false,
		"render" : function(data, type, row) {
			if(${ productContract.isEditable && ( !buyerReadOnlyAdmin && canEdit && (eventPermissions.owner or eventPermissions.editor))} && !row.deleted) {
				var ret = '<a href="#contractItemModel" onClick="javascript:editContractItem(\'' + row.id + '\');"  title=<spring:message code="tooltip.edit" />  role="button" data-toggle="modal"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
				var startDate = row.contractStartDate;
				if(Date.parse(new Date()) < Date.parse(startDate) || '${productContract.status}' == 'DRAFT' || ( ('${productContract.status}' == 'ACTIVE' || '${productContract.status}' == 'SUSPENDED') && '${eventPermissions.owner}')){
					ret += '<a href="#deleteContractItem" onClick="javascript:updateLink(\'' + row.id + '\');" title=<spring:message code="tooltip.delete" />  role="button" class="${!buyerReadOnlyAdmin ? "":"disabled"}" data-toggle="modal">';
					ret += '<img src="${pageContext.request.contextPath}/resources/images/delete1.png"></a>';
				}
				return ret;
			} else {
				return '';
			}
		}
		},{
			"data" : "contractItemNumber"
		},{
			"data" : "itemName",
			"defaultContent" : ""
		},{
			"data" : "itemCode",
			"defaultContent" : ""
		},{
			"data": "productCategory",
			"defaultContent": "",
			"render": function (data, type, row) {
				// Check if productCategory is null or undefined
				if (row.productCode != undefined || row.productName != undefined ) {
					return row.productCode + ' - ' + row.productName;
				} else {
					return ""; // Display empty string
				}
			}
		},{
			"data" :"uom",
			"defaultContent" : ""
		},{
			"data" : "quantity",
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommasFormat(row.quantity, row.decimal);
			}
		},{
			"data" : "balanceQuantity",
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommasFormat(row.balanceQuantity, row.decimal);
			}
		},{
			"data" :"unitPrice",
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommasFormat(row.unitPrice, row.decimal);
			}
		},{
			"data" :"tax",
			"className" : "align-right",
			"defaultContent" : "",
			"mRender" : function(data, type, row) {
				return ReplaceNumberWithCommasFormat(row.tax, row.decimal);
			}
		},{
			"data":"itemType",
			"defaultContent" : ""
		}, {
			"data" :"brand",
			"defaultContent" : ""
		},{
			"data" :"storageLoc",
			"defaultContent" : ""
		},{
			"data":"businessUnit",
			"defaultContent" : ""
		},{
			"data":"costCenter",
			"defaultContent" : "",
			"render" : function(data, type, row) {
			if(row.description != null) {
			    return row.costCenter +' - '+ row.description; 
			} else {
				return row.costCenter;
			}
		}, 
		}],
		"initComplete": function(settings, json) {
			var htmlSearch = '<tr class="tableHeaderWithSearch">';
			$('#contractItemTableList thead tr:nth-child(1) th').each(function(i) {
				var title = $(this).text();
				if (!(title == "Actions") && $(this).attr('search-type') != '') {
					if ($(this).attr('search-type') == 'select') {
						var optionsType = $(this).attr('search-options');
						htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value=""><spring:message code="application.search"/> ' + title + '</option>';
						if (optionsType == 'statusList') {
							<c:forEach items="${statusList}" var="item">
							htmlSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						if(optionsType == 'itemTypeList'){
							<c:forEach items="${itemTypeList}" var="item">
							htmlSearch += '<option value="${item}">${item}</option>';
							</c:forEach>
						}
						htmlSearch += '</select></th>';
					} else {
						htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
					}
				} else {
					htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
				}
			});
			htmlSearch += '</tr>';
			$('#contractItemTableList thead').append(htmlSearch);
			$(contractItemTableList.table().container()).on('keyup', 'thead input', function() {
				if ($.trim(this.value).length > 2 || this.value.length == 0) {
					contractItemTableList.column($(this).data('index')).search(this.value).draw();
				}
			});
			$(contractItemTableList.table().container()).on('change', 'thead select', function() {
				contractItemTableList.column($(this).data('index')).search(this.value).draw();
			});
		}
	});
	
	
	// Disable the Business Unit at Contract level if there are Contract Items in this contract
	contractItemTableList.on( 'draw', function () {
	    console.log('Table rendering done....', contractItemTableList.page.info().recordsTotal);
		if(contractItemTableList.page.info().recordsTotal > 0) {
			$('#idBusinessUnitDiv').addClass('disabled')
		} else {
			$('#idBusinessUnitDiv').removeClass('disabled')
		}
	    
	});
	
	});
	
	function ReplaceNumberWithCommasFormat(yourNumber, decimaltoFormate) {
		if(yourNumber!='' && yourNumber!=undefined){
			yourNumber = parseFloat(yourNumber).toFixed(decimaltoFormate);
			var n = yourNumber.toString().split(".");
			n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			return n.join(".");
		}
		return yourNumber; 
	}
	
	
	$( "#chosenProductItem" ).change(function() {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		  
		  var productItemId = $('#chosenProductItem').val();
		  $.ajax({
				url: getContextPath() + '/buyer/itemDetailsOnProductBase',
				data: {
					'productItemId' :productItemId
				},
				type: 'GET',
				dataType: 'json',
				beforeSend: function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success: function(data, textStatus, request) {
					$("#addcontractItemForm").find('#contractItemBrand').val(data.brand);
					$("#addcontractItemForm").find('#contractItemBrand').parent().addClass('disabled');
					$("#addcontractItemForm").find('#contractItemBrand').validate();
					$("#addcontractItemForm").find('#balanceQuantity').val(data.balanceQuantity);
					$("#addcontractItemForm").find('#balanceQuantity').validate();
					$("#addcontractItemForm").find('#chosenUom').val(data.uom != null ? data.uom : '').trigger("chosen:updated");
					$("#addcontractItemForm").find('#chosenUom').validate();
					$("#addcontractItemForm").find('#chosenUom').parent().addClass('disabled');
					$("#addcontractItemForm").find('#itemTax').val(data.tax);
					$("#addcontractItemForm").find('#itemTax').parent().addClass('disabled');
					$("#addcontractItemForm").find('#itemTax').validate();
					$("#addcontractItemForm").find('#itemCode').val(data.itemCode);
					$("#addcontractItemForm").find('#itemCode').parent().addClass('disabled');
					$("#addcontractItemForm").find('#unitPriceId').val(data.unitPrice);
					$("#addcontractItemForm").find('#unitPriceId').validate();
					$("#addcontractItemForm").find('#unitPriceId').parent().addClass('disabled');
					if(data.productCategory != null && $("#chosenProductCategory option[value='" + data.productCategory + "']").length == 0) {
						$("#addcontractItemForm").find('#chosenProductCategory option').eq(2).before($("<option></option>").val(data.productCategory).text(data.productCategoryCode + " - " + data.productCategoryName));
					}
					$("#addcontractItemForm").find('#chosenProductCategory').val(data.productCategory != null ? data.productCategory : '').trigger("chosen:updated");
					$("#addcontractItemForm").find('#chosenProductCategory').validate();
					$("#addcontractItemForm").find('#chosenProductCategory').parent().addClass('disabled');
					$("#addcontractItemForm").find('#chosenProductItemType').val(data.productItemType != null ? data.productItemType : '').trigger("chosen:updated");
					$("#addcontractItemForm").find('#chosenProductItemType').validate();
					$("#addcontractItemForm").find('#chosenProductItemType').parent().addClass('disabled');
				},
	
				error: function(request, textStatus, errorThrown) {
					console.log(request);
				},
				complete: function() {
					$('#loading').hide();
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
			$("#addcontractItemForm").find('#contractItemBrand').parent().removeClass('disabled');
			$("#addcontractItemForm").find('#itemCode').parent().removeClass('disabled');
			$("#addcontractItemForm").find('#unitPriceId').parent().removeClass('disabled');
			$("#addcontractItemForm").find('#itemTax').parent().removeClass('disabled');
			$("#addcontractItemForm").find('#chosenUom').parent().removeClass('disabled');
			$("#addcontractItemForm").find('#chosenProductCategory').parent().removeClass('disabled');
			$("#addcontractItemForm").find('#chosenProductItemType').parent().removeClass('disabled');
			$('#idItemList').hide();
			$('#chosenProductItem').attr('data-validation-optional', 'true');
		} else {
			$('#idItemFreeText').hide();
			$("#addcontractItemForm").find('#itemCode').parent().addClass('disabled');
			$('#itemName').attr('data-validation-optional', 'true');
			$("#addcontractItemForm").find('#unitPriceId').parent().addClass('disabled');
			$("#addcontractItemForm").find('#itemTax').parent().addClass('disabled');
			$("#addcontractItemForm").find('#contractItemBrand').parent().addClass('disabled');
			$("#addcontractItemForm").find('#chosenUom').parent().addClass('disabled');
			$("#addcontractItemForm").find('#chosenProductCategory').parent().addClass('disabled');
			$("#addcontractItemForm").find('#chosenProductItemType').parent().addClass('disabled');
			$('#idItemList').show();
			$('#chosenProductItem').removeAttr('data-validation-optional');
		}
	});
	
	$('#reminderCan').on('click', function(e) {
		$('#addcontractItemForm').trigger("reset");
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/productContractUpdate.js?12"/>"></script>

