<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<sec:authorize access="hasRole('ROLE_PRODUCT_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="productListDesk" code="application.buyer.product.list" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${productListDesk}] });
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
</style>
<script src="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.js"/>"></script>
<link rel="stylesheet" href="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.min.css"/>">
<!--Side Bar End  -->
<div id="page-content" view-name="product">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
			</a></li>
			<c:url value="/buyer/productList" var="listUrl" />
			<li><a id="listLink" href="${listUrl} "> <spring:message code="Productz.list" /> </a></li>
			<li class="active"><spring:message code="Productz.Maintenance" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="Productz.Maintenance" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3><spring:message code="systemsetting.productlist.detail" /></h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<!-- 	------ form start  here--------- -->
				<form:form id="productListMaintenanceForm" data-parsley-validate="" commandName="productListMaintenance" action="saveProductListMaintenance?${_csrf.parameterName}=${_csrf.token}" method="post" cssClass="form-horizontal bordered-row" enctype="multipart/form-data">
					<form:hidden path="id" id="productId"/>
					<form:hidden path="contentType" />
					<form:hidden path="fileName" />
					<form:hidden path="fileAttatchment" />
					<div class="row marg-bottom-20 ">
						<div class="col-md-3">
							<form:label path="productCode" cssClass="marg-top-10">
								<spring:message code="productz.code" />
							</form:label>
						</div>
						<div class="col-md-5 ">
							<spring:message code="productz.description" var="desc" />
							<spring:message code="productz.code.required" var="required" />
							<spring:message code="productz.code.length" var="length" />
							<spring:message code="systemsetting.product.code.placeholder" var="productcode" />
							<form:input path="productCode" data-validation-length="1-50" data-validation="required length" data-validation-error-msg-required="${required}" data-validation-error-msg-length="${length}" cssClass="form-control" id="idProductCode" placeholder="${productcode}" />
							<form:errors path="productCode" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="productName" cssClass="marg-top-10">
								<spring:message code="productz.name" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="productz.name.placeholder" var="name" />
							<spring:message code="productz.name.required" var="required" />
							<spring:message code="productz.name.length" var="length" />
							<form:input path="productName" data-validation="required length" data-validation-length="1-64" cssClass="form-control" id="idProductName" placeholder="${name}" data-validation-error-msg-required="${required}" />
							<form:errors path="productName" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="uom" for="idUom" class="marg-top-10">
								<spring:message code="product.UOM" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="application.uom.required" var="required" />
							<form:select path="uom" id="chosenUom" cssClass="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<form:option value="">
									<spring:message code="product.select.uom" />
								</form:option>
								<c:forEach items="${uoms}" var="uom">
								   <c:if test="${empty uom.id}">
									 <option value="" disabled>${uom.uom} - ${uom.uomDescription}</option>
								  </c:if>
								  <c:if test="${!empty uom.id and uom.id != productListMaintenance.uom.id}">
									<option value="${uom.id}">${uom.uom} - ${uom.uomDescription}</option>
								  </c:if>
								  <c:if test="${!empty uom.id and uom.id == productListMaintenance.uom.id}">
										<option value="${uom.id}" selected>${uom.uom} - ${uom.uomDescription}</option>
								  </c:if>
								</c:forEach>
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label for="idpc" class="marg-top-10"> <spring:message code="Product.category" />
							</label>
						</div>
						<div class="col-md-5">
							<spring:message code="application.productCategory.required" var="required" />
							<form:select path="productCategory" id="chosenProductCategory" cssClass="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<form:option value="">
									<spring:message code="Product.select.category" />
								</form:option>
								<c:forEach items="${pcms}" var="pc">
									<c:if test="${empty pc.id}">
										<option value="" disabled>${pc.productName}</option>
									</c:if>
									<c:if test="${!empty pc.id and pc.id != productListMaintenance.productCategory.id}">
										<option value="${pc.id}">${pc.productName}</option>
								    </c:if>
								    <c:if test="${!empty pc.id and pc.id == productListMaintenance.productCategory.id}">
										<option value="${pc.id}" selected>${pc.productName}</option>
									</c:if>
							 </c:forEach>	
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label for="idProductItemType" class="marg-top-10"><spring:message code="product.item.type" /> </label>
						</div>
						<div class="col-md-5">
							<form:select path="productItemType" cssClass="form-control chosen-select disablesearch" id="idProductItemType">
							     <form:option value="">
									<spring:message code="product.item.select" />
								</form:option>
								<form:options items="${itemTypeList}" />
							</form:select>
							<form:errors path="productItemType" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label for="idsupplier" class="marg-top-10"> <spring:message code="Productz.favoriteSupplier" />
							</label>
						</div>
						<div class="col-md-5">
							<spring:message code="application.productCategory.required" var="required" />
							<form:select path="favoriteSupplier" id="chosenSupplier" class="chosen-select">
								<form:option value="">
									<spring:message code="Product.favoriteSupplier" />
								</form:option>
								<c:forEach items="${favSupp}" var="supp">
									<c:if test="${empty supp.id}">
										<option value="" disabled>${supp.companyName}</option>
									</c:if>
									<c:if test="${!empty supp.id and supp.id != productListMaintenance.favoriteSupplier.id}">
										<option value="${supp.id}">${supp.companyName}</option>
								    </c:if>
								    <c:if test="${!empty supp.id and supp.id == productListMaintenance.favoriteSupplier.id}">
										<option value="${supp.id}" selected>${supp.companyName}</option>
									</c:if>
							 </c:forEach>	
							</form:select>
						</div>
					</div>
					
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="unitPrice" cssClass="marg-top-10">
								<spring:message code="product.unit.price" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="systemsetting.enter.unit.placeholder" var="unitpriceplace" />
							<fmt:formatNumber var="uPrice" type="number" minFractionDigits="${decimal}" maxFractionDigits="${decimal}" value="${productListMaintenance.unitPrice}" />
							
							<form:input path="unitPrice" value="${uPrice}" cssClass="form-control" id="idUnitPrice" data-validation="contract_item validate_custom_length positive" placeholder="${unitpriceplace}"  data-validation-regexp="^[\d,]{1,16}(\.\d{1,${decimal}})?$" data-sanitize="numberFormat"/>
							<form:errors path="unitPrice" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="tax" cssClass="marg-top-10">
								<spring:message code="product.list.tax" />&nbsp(%)
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="systemsetting.enter.tax.placeholder" var="tax" />
							<fmt:formatNumber var="utax" type="number" minFractionDigits="${decimal}" maxFractionDigits="${decimal}" value="${productListMaintenance.tax}" />
							
							<form:input path="tax" value="${utax}" data-validation="required tax_custom_length" cssClass="form-control" id="idtax" placeholder="${tax}" data-validation-length="1-3" data-sanitize="numberFormat"/>
							<form:errors path="tax" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="glCode" cssClass="marg-top-10">
								<spring:message code="product.gl.code" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="systemsetting.enter.glcode.placeholder" var="glcode" />
							<form:input path="glCode" cssClass="form-control" id="idGlCode" placeholder="${glcode}" data-validation="length" data-validation-optional="true" data-validation-length="1-20" />
							<form:errors path="glCode" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="unspscCode" cssClass="marg-top-10">
								<spring:message code="Productz.unspsc" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="systemsetting.enter.unspsc.placeholder" var="unpsc_code" />
							<form:input path="unspscCode" cssClass="form-control" id="idunspscCode" placeholder="${unpsc_code}" data-validation="alphanumeric,length" data-validation-optional="true" data-validation-length="1-10" />
							<form:errors path="unspscCode" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label for="idStatus" class="marg-top-10"><spring:message code="Productz.status" /> </label>
						</div>
						<div class="col-md-5">
							<form:select path="status" cssClass="form-control chosen-select disablesearch" id="idStatus">
								<form:options items="${statusList}" />
							</form:select>
							<form:errors path="status" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-10">
						<div class="col-md-3">
							<label> <spring:message code="Product.Attatchment" />
							</label>
						</div>
						<div class="ph_table_border col-md-5 uploadFileDiv ${productListMaintenance.fileName != null ? '' : 'hide'}">
							<div class="contactList marginDisable">
								<div class="row">
									<div class="col-md-9">
										<p id="productFileName">
											<a href="${pageContext.request.contextPath}/buyer/downloadProductDocument/${productListMaintenance.id}" class="pull-left">${productListMaintenance.fileName}</a>
										</p>
									</div>
									<div class="col-md-3">
										<a class="deleteFile"> <i class="fa fa-times-circle"></i>
										</a>
									</div>
								</div>
							</div>
						</div>
						<div class="col-md-5 uploadFile ${productListMaintenance.fileName == null ? '' : 'hide'}">
							<div data-provides="fileinput" class="fileinput ${!buyerReadOnlyAdmin ? '':'disabled'}  fileinput-new input-group">
								<spring:message code="event.doc.file.required" var="required" />
								<spring:message code="event.doc.file.length" var="filelength" />
								<spring:message code="event.doc.file.mimetypes" var="mimetypes" />
								<div data-trigger="fileinput" class="form-control">
									<span class="fileinput-filename show_name" id="show_name"></span>
								</div>
								<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="event.document.selectfile" />
								</span> <span class="fileinput-exists"> <spring:message code="event.document.selectfile" />
								</span> <c:set var="fileType" value="" /> <c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
										<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
									</c:forEach> <form:input path="" data-validation-allowing="${fileType}" data-validation="extension size" data-validation-error-msg-container="#Load_File-error-dialog" data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" name="uploadDocx" id="uploadDocx" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-original-title="More Search Fields" />
								</span>
							</div>
							<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
							<span> <spring:message code="application.note" />:<br />
								<ul>
									<li><spring:message code="createrfi.documents.max.size" /> ${ownerSettings.fileSizeLimit} MB</li>
									<li><spring:message code="createrfi.documents.allowed.file.extension" />: ${fileType}.</li>
								</ul>
							</span>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="remarks" cssClass="marg-top-10">
								<spring:message code="Product.remarks" />
							</form:label>
						</div>
						<div class="col-md-7 col-custom-md-7">
							<div class="form-group textarea">
								<spring:message code="systemsetting.max.chars.placeholder"  var="maxchars"/>
								<form:textarea placeholder="${maxchars}" path="remarks" name="remarks" id="remarks" rows="3" class="form-control" data-parsley-id="7481" data-validation='length' data-validation-length="max250"></form:textarea>
								<span class="sky-blue"><spring:message code="rfaevnt.max.characters" /></span>
								<ul class="parsley-errors-list" id="parsley-id-7481"></ul>
							</div>
						</div>
					</div>

				
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="validityDate" cssClass="marg-top-10">
								<spring:message code="productlist.contact.item" />
							</form:label>
						</div>
						<div class="col-md-1 col-custom-md-1">
							<spring:message code="tooltip.contact.item"  var="contactitem"/>
							<form:checkbox path="contractItem" id="contractItem1" class="custom-checkbox" data-validation-error-msg-container="#contract_dateErrorBlock" title="${contactitem}" data-validation="contract_date" />
						</div>
						<div class="col-md-4 col-custom-md-4">
							<div class="input-prepend input-group">
								<spring:message code="dateformat.placeholder" var="dateformat" />
								
								<fmt:formatDate var="validityDate" value="${productListMaintenance.validityDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								<form:input id="datepicker" value="${validityDate}" disabled="true" path="validityDate" class="bootstrap-datepicker form-control for-clander-view" data-validation="date" data-validation-optional="true" data-fv-date-min="15/10/2016" data-validation-format="dd/mm/yyyy" placeholder="${dateformat}"
									autocomplete="off" />
								<div id="contract_dateErrorBlock"></div>
								<form:errors path="validityDate" cssClass="error" />
							</div>

						</div>
					</div>
						<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="contractReferenceNumber" cssClass="marg-top-10"><spring:message code="contact.reference.number" /></form:label>
						</div>
						<div class="col-md-5">
							<form:input cssClass="form-control" path="contractReferenceNumber" id="contractReferenceNumber" placeholder="Enter Contract Reference Number" data-validation="length" data-validation-optional="true" data-validation-length="1-50" />
							<form:errors path="contractReferenceNumber" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="historicPricingRefNo" cssClass="marg-top-10">
								<spring:message code="Productz.historicPricing" />
							</form:label>
						</div>
						<div class="col-md-5">
					
							<spring:message code="systemsetting.enter.historic.placeholder"  var="historicprice"/>
							<form:input path="historicPricingRefNo" cssClass="form-control" id="idHistoricPricingRefNo" placeholder="${historicprice}" data-validation="validate_custom_length positive" data-validation-regexp="^[\d,]{1,16}(\.\d{1,${decimal}})?$" data-validation-optional="true" data-sanitize="numberFormat"/>
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="purchaseGroupCode" cssClass="marg-top-10">
								<spring:message code="Productz.purcahseGroupCode" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="systemsetting.purchase.group.placeholder"  var="purchasecode"/>
							<form:input path="purchaseGroupCode" cssClass="form-control" id="idPurchaseGroupCode" placeholder="${purchasecode}" data-validation="length" data-validation-optional="true" data-validation-length="max30" />
						</div>
					</div>

					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="interfaceCode" cssClass="marg-top-10">
								<spring:message code="interface.code" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="productz.description" var="desc" />
							<spring:message code="productz.interface.code.length" var="length" />
							<spring:message code="systemsetting.product.interface.code.placeholder" var="interfacecode" />
							<form:input path="interfaceCode" data-validation-length="0-20" data-validation="length"  data-validation-error-msg-length="${length}" cssClass="form-control" id="idInterfaceCode" placeholder="${interfacecode}" />
							<form:errors path="interfaceCode" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="brand" cssClass="marg-top-10">
								<spring:message code="Productz.brand" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="systemsetting.enter.Brand.placeholder"  var="brandplace"/>
							<form:input path="brand" cssClass="form-control" id="idBrand" placeholder="${brandplace}" data-validation="length" data-validation-optional="true" data-validation-length="max500" />
						</div>
					</div>


					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
							<input type="submit" value="${btnValue2}" id="saveProductListMaintenance" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" ${!canEdit ? "disabled='disabled'" : ""}>
							<c:url value="/buyer/productList" var="createUrl" />
							<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
							</a>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script>
<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '.button-previous, #dashboardLink, #listLink';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>

	$.formUtils.addValidator({
		name : 'contract_item',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			console.log($el.attr('name'), value);
			if ($('#contractItem1').is(":checked") && $.trim(value) == '') {
				response = false;
			}
			return response;
		},
		errorMessage : 'This is a required field',
		errorMessageKey : 'badContractItem'
	});

	$.formUtils.addValidator({
		name : 'contract_date',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			console.log($('[name="dateTimeRange"]').val());
			if ($('#contractItem1').is(":checked") && $.trim($('#datepicker').val()) == '') {
				response = false;
			}
			return response;
		},
		errorMessage : 'This is a required field',
		errorMessageKey : 'badContractDate'
	});

	/* if ($('#contractItem1').is(":checked")) {
		$('[name="validityDate"]').removeClass('disabled');
	} else {
		$('[name="validityDate"]').addClass('disabled').val('');
	}
 */

 	var dateTimeRange;
 	var historyPricing;
	 $(function () {
	        $("#contractItem1").click(function () {
	            if ($(this).is(":checked")) {
	                $("#datepicker").prop("disabled", false);
	                $("#datepicker").focus();	
	             $("#datepicker-date-time-nodisable").val(dateTimeRange);
	             $("#datepicker-date-time-nodisable").val();
	             $("#contractReferenceNumber").val(historyPricing);
	             $("#contractReferenceNumber").val();
	                $("#datepicker-date-time-nodisable").prop('disabled',false);
	                $("#datepicker-date-time-nodisable").focus();
	                $("#contractReferenceNumber").prop('disabled',false);
	                $("#contractReferenceNumber").focus();
	                
	            } else {
	            	 $("#datepicker").val('');
	            	 $("#datepicker").prop("disabled", true);
	            	 dateTimeRange=$("#datepicker-date-time-nodisable").val();
	            	 $("#datepicker-date-time-nodisable").val('');
	            	 $("#datepicker-date-time-nodisable").prop('disabled', true);
	            	 historyPricing=$("#contractReferenceNumber").val();
	            	 $("#contractReferenceNumber").val('');
	            	 $("#contractReferenceNumber").prop('disabled',true);
	            } 
	        });
	    });
	/* $('#contractItem1').change(function() { 
		 if ($('#contractItem1').is(":checked")) {
			$('[name="validityDate"]').removeClass('disabled');
		} else {
			$('[name="validityDate"]').addClass('disabled');
		}
 		
 	}) ;  */

	$.validate({
		lang : 'en',
		modules : 'file'
	});
</script>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		 if ($("#contractItem1").is(":checked")) {
			   $("#contractReferenceNumber").prop('disabled',false);
			   $("#datepicker").prop('disabled', false);
		 }else{
			 $("#contractReferenceNumber").prop('disabled',true);
			 $("#datepicker").prop('disabled', true);
		 }
		 
		 
		 
		/* $('#idUnitPrice').mask('#0.${decimals}', {
			placeholder : "e.g. 100.${decimals}",
			reverse : true
		});
		$('#idtax').mask('#0.${decimals}', {
			placeholder : "e.g. 12.${decimals}",
			reverse : true
		}); */

		$('#uploadDocx').change(function() {
			if (!$('#productListMaintenanceForm').isValid()) {
				return false;
			}
			var index = $('#uploadDocx').val().lastIndexOf('\\');
			var name = $('#uploadDocx').val().substring(index + 1);
			$('p[id=productFileName]').html(name);
			$('.uploadFile').addClass('hide');
			$('.uploadFileDiv').removeClass('hide');
		});

		$('.deleteFile').click(function() {
			$('.uploadFileDiv').addClass('hide');
			$('.uploadFile').removeClass('hide');
			$('span[id="show_name"]').html('');
			$('glyphicon').removeClass('glyphicon-file');
			$('#uploadDocx').val('');
		});

		$(document).on("keyup", "#chosenSupplier_chosen .chosen-search input", keyDebounceDelay(function(e) {
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
			var favSupp = $.trim(this.value);
			if (favSupp.length > 2 || favSupp.length == 0 || e.keyCode == 8) {
				reloadSupplierList();
			}
		}, 650));
		
		$(document).on("keyup", "#chosenUom_chosen .chosen-search input", keyDebounceDelay(function(e) {
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
			var uom = $.trim(this.value);
			if (uom.length > 2 || uom.length == 0 || e.keyCode == 8) {
				reloadUomList();
			}
		}, 650));
		
		$(document).on("keyup", "#chosenProductCategory_chosen .chosen-search input", keyDebounceDelay(function(e) {
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
			var productCategory = $.trim(this.value);
			if (productCategory.length > 2 || productCategory.length == 0 || e.keyCode == 8) {
				reloadProductCategoryList();
			}
		}, 650));
		
	});
	
	function reloadSupplierList() {
		var productId=$('#productId').val();
		var favSupp = $.trim($('#chosenSupplier_chosen .chosen-search input').val());
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url : getContextPath() + '/buyer/searchMoreFavouritesuppliers',
			data : {
				'searchSupplier' : favSupp,
				'productId' : productId,
			},
			type : 'POST',
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data) {
				var html = '';
				if (data != '' && data != null && data.length > 0) {
					$('#chosenSupplier').find('option:not(:first)').remove();
					$.each(data, function(key, value) {
						if (value.id == null || value.id == '') {
							html += '<option value="" disabled>' + value.companyName + '</option>';
						}else{
							html += '<option value="' + value.id + '">' + value.companyName + '</option>';
						}
					});
				}
				$('#chosenSupplier').append(html);
				$("#chosenSupplier").trigger("chosen:updated");
				$('#chosenSupplier_chosen .chosen-search input').val(favSupp);
				$('#loading').hide();
			},
			error : function(error) {
				console.log(error);
			}
		});
	}
	
	function reloadUomList() {
		var uom = $.trim($('#chosenUom_chosen .chosen-search input').val());
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url : getContextPath() + '/buyer/searchUomFromList',
			data : {
				'uom' : uom,
			},
			type : 'POST',
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data) {
				var html = '';
				if (data != '' && data != null && data.length > 0) {
					$('#chosenUom').find('option:not(:first)').remove();
					$.each(data, function(key, value) {
						if (value.id == null || value.id == '') {
							html += '<option value="" disabled>' + value.uom+ ' - '+value.uomDescription + '</option>';
						}else{
							html += '<option value="' + value.id + '">' + value.uom+ ' - ' +value.uomDescription + '</option>';
						}
					});
				}
				$('#chosenUom').append(html);
				$("#chosenUom").trigger("chosen:updated");
				$('#chosenUom_chosen .chosen-search input').val(uom);
				$('#loading').hide();
			},
			error : function(error) {
				console.log(error);
			}
		});
	}
	
	function reloadProductCategoryList() {
		var productName = $.trim($('#chosenProductCategory_chosen .chosen-search input').val());
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url : getContextPath() + '/buyer/searchProductCategoryFromList',
			data : {
				'productName' : productName,
			},
			type : 'POST',
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data) {
				var html = '';
				if (data != '' && data != null && data.length > 0) {
					$('#chosenProductCategory').find('option:not(:first)').remove();
					$.each(data, function(key, value) {
						if (value.id == null || value.id == '') {
							html += '<option value="" disabled>' + value.productName + '</option>';
						}else{
							html += '<option value="' + value.id + '">' + value.productName + '</option>';
						}
					});
				}
				$('#chosenProductCategory').append(html);
				$("#chosenProductCategory").trigger("chosen:updated");
				$('#chosenProductCategory_chosen .chosen-search input').val(productName);
				$('#loading').hide();
			},
			error : function(error) {
				console.log(error);
			}
		});
	}
	
	
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript">
	// Datepicker bootstrap /

	$(function() {
		"use strict";
		var nowTemp = new Date();
		var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate() + 1, 0, 0, 0, 0);
		// console.log(now);
		$('.bootstrap-datepicker').bsdatepicker({
			format : 'dd/mm/yyyy',
			onRender : function(date) {
				return date.valueOf() < now.valueOf() ? 'disabled' : '';
			}
		}).on('changeDate', function(e) {
			$(this).blur();
			$(this).bsdatepicker('hide');
		});
	});
	
	$.formUtils.addValidator({
		name : 'validate_custom_length',
		validatorFunction : function(value, $el, config, language, $form) {
			var val = value.split(".");
			var regp = new RegExp('[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)');
			var t=regp.test(val); 
			if (val[0].replace(/,/g, '').length > 13 || t==true) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The input value is longer than 13 characters',
		errorMessageKey : 'validateLengthCustom'
	});
	
	$(document).delegate('input[name="unitPrice"]', 'change', function(e) {
		var decimalLimit = ${decimal};
		var unitPrice = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
		unitPrice = !isNaN(unitPrice) ? unitPrice.toFixed(decimalLimit) : '';
		 console.log(unitPrice);
		 unitPrice = Number(unitPrice).toLocaleString(undefined, { minimumFractionDigits: decimalLimit , maximumFractionDigits:decimalLimit})
		$('#idUnitPrice').val(unitPrice);
		// Number(num).toLocaleString(undefined, { minimumFractionDigits: 2 })
	});
	
 	$(document).delegate('input[name="tax"]', 'change', function(e) {
		var decimalLimit = ${decimal};
		var tax = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
		tax = !isNaN(tax) ? tax.toFixed(decimalLimit) : '';
		tax = Number(tax).toLocaleString(undefined, { minimumFractionDigits: decimalLimit , maximumFractionDigits:decimalLimit})
		 console.log(tax);
		$('#idtax').val(tax);
	}); 
	
	$.formUtils.addValidator({
		name : 'tax_custom_length',
		validatorFunction : function(value, $el, config, language, $form) {
			var val = value.split(".");
			if (val[0].replace(/,/g, '').length > 3) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The input value is longer than 3 characters',
		errorMessageKey : 'validateLengthCustom'
	});
	
	$(document).delegate('input[name="historicPricingRefNo"]', 'change', function(e) {
		var decimalLimit = ${decimal};
		var historicPricing = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
		historicPricing = !isNaN(historicPricing) ? historicPricing.toFixed(decimalLimit) : '';
		historicPricing = Number(historicPricing).toLocaleString(undefined, { minimumFractionDigits: decimalLimit , maximumFractionDigits:decimalLimit})
		$('#idHistoricPricingRefNo').val(historicPricing);
		// Number(num).toLocaleString(undefined, { minimumFractionDigits: 2 })
	});

</script>


<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js"/>"></script>