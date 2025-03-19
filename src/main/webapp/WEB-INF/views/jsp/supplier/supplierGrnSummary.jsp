<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<style>
.ph_btn_custom {
	height: 40px !important;
	min-width: 170px !important;
	font-size: 16px !important;
	line-height: 39px;
	font-weight: 500;
}
</style>
<div class="clear"></div>
<div class="white-bg border-all-side float-left width-100 pad_all_15">
	<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
	<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
	<div class="row">
		<div class="col-lg-6 col-md-12 col-sm-12 col-xs-12">
			<input type="hidden" id="grnId" value="${goodsReceiptNote.id}">
			<div class="tag-line">
				<h2>
					<spring:message code="grn.number.label" />
					: ${goodsReceiptNote.grnId}
				</h2>
				<br>
				<h2>
					<spring:message code="grn.summary.grnDate" />
					: <fmt:formatDate value="${goodsReceiptNote.grnSendDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				</h2>
				<br>
				<h2>
					<spring:message code="application.lable.buyer" />
					: ${goodsReceiptNote.buyer.companyName}
				</h2>
				<c:if test="${not empty goodsReceiptNote.businessUnit}">
					<br>
					<h2>
						<spring:message code="supplier.po.summary.businessUnit" />
						: ${goodsReceiptNote.businessUnit.unitName}
					</h2>
				</c:if>
			</div>
		</div>
	<div class="col-lg-6 col-md-12 col-sm-12 col-xs-12">
			<c:if test="${goodsReceiptNote.status != 'CANCELLED' and goodsReceiptNote.status != 'DECLINED'}">
				<div class="pull-right marg-right-10">
					<form:form action="${pageContext.request.contextPath}/supplier/downloadGrnReport/${goodsReceiptNote.id}" method="GET">
						<button class="btn ph_btn_small float-right btn-info hvr-pop marg-left-10 downloadGrnBtn" type="submit" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="grnsummary.download.grn.button" />'>
							<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> 
							<span class="button-content"><spring:message code="grnsummary.download.grn.button" /></span>
						</button>
					</form:form>
				</div>
			</c:if>
			<c:if test="${goodsReceiptNote.status=='DELIVERED'}">
				<button id="declinedGrn" class="btn ph_btn_small float-right btn-danger hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.supplier.grn.declined" />'>
					 <span class="glyph-icon icon-separator"> <i class="glyph-icon icon-times"></i></span> -
					<span class="button-content"><spring:message code="supplier.grn.summary.declined" /></span>
				</button>
			
				<button id="acceptGrn" class="btn ph_btn_small float-right btn-success hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.supplier.grn.accept" />'>
					 <span class="glyph-icon icon-separator"> <i class="glyph-icon icon-check"></i></span> 
					 <span class="button-content"><spring:message code="supplier.grn.summary.accept" /></span>
				</button>
			</c:if>
		</div>
	</div>
	
</div>
<div class="clear"></div>
<div class="tab-pane" style="display: block">
	<div class="upload_download_wrapper clearfix event_info">
		<h4><spring:message code="prsummary.general.information" /></h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="grn.number.label" />
				</label>
			</div>
			<spring:message var="placeReference" code="grn.number.label" />
			<div class="col-sm-5 col-md-5 col-xs-6">
 				 <p>${goodsReceiptNote.grnTitle}</p> 
			</div>
		</div>		
		
		<c:if test="${not empty goodsReceiptNote.po}">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="grn.poNumber.label" />
					</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${goodsReceiptNote.po.poNumber}</p>
				</div>
			</div>
		</c:if>

		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="grn.creater.label" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					${goodsReceiptNote.createdBy.name} <br> ${goodsReceiptNote.createdBy.communicationEmail} <br>
					<c:if test="${not empty goodsReceiptNote.createdBy.phoneNumber}"><spring:message code="prtemplate.hp" />: ${ goodsReceiptNote.createdBy.phoneNumber}</c:if>
				</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4><spring:message code="rfs.summary.finance.information" /></h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="grn.base.currency.label" /> : </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${goodsReceiptNote.currency.currencyCode}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="grn.decimal.label" /> : </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${goodsReceiptNote.decimal}</p>
			</div>
		</div>
	</div>

	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4><spring:message code="grn.delivery.details.label" /></h4>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="grn.summary.delivery.address" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
						<span class='desc'> ${goodsReceiptNote.deliveryAddressLine1}  <br>
							<c:if test="${not empty goodsReceiptNote.deliveryAddressLine2}"> ${goodsReceiptNote.deliveryAddressLine2} <br></c:if>
							<c:if test="${not empty goodsReceiptNote.deliveryAddressCity}"> ${goodsReceiptNote.deliveryAddressCity} <br></c:if>
							<c:if test="${not empty goodsReceiptNote.deliveryAddressZip}"> ${goodsReceiptNote.deliveryAddressZip} <br></c:if>
							${goodsReceiptNote.deliveryAddressState}, ${goodsReceiptNote.deliveryAddressCountry}  
							
						</span>
 				</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4><spring:message code="grn.summary.supplier.label" /></h4>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="grn.summary.supplier.label" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
					<c:if test="${not empty goodsReceiptNote.supplier}">
						<span class='desc'> ${goodsReceiptNote.supplier.companyName}  <br>
							<c:if test="${not empty goodsReceiptNote.supplier.companyContactNumber}"><spring:message code="prdraft.tel" />: ${goodsReceiptNote.supplier.companyContactNumber}</c:if>
							<c:if test="${not empty goodsReceiptNote.supplier.faxNumber}"><spring:message code="prtemplate.fax" />: ${goodsReceiptNote.supplier.faxNumber}</c:if>
						</span>
					</c:if>
					<c:if test="${not empty goodsReceiptNote.supplierName}">
						<span class='desc'> ${goodsReceiptNote.supplierName}  <br>
							<c:if test="${not empty goodsReceiptNote.supplierAddress}"><spring:message code="prdraft.tel" />: ${goodsReceiptNote.supplierAddress}</c:if>
							<c:if test="${not empty goodsReceiptNote.supplierFaxNumber}"><spring:message code="prtemplate.fax" />: ${goodsReceiptNote.supplierFaxNumber}</c:if>
							<c:if test="${not empty goodsReceiptNote.supplierTelNumber}"><spring:message code="prtemplate.fax" />: ${goodsReceiptNote.supplierTelNumber}</c:if>
							<c:if test="${not empty goodsReceiptNote.supplierTaxNumber}"><spring:message code="prtemplate.fax" />: ${goodsReceiptNote.supplierTaxNumber}</c:if>
						</span>
					</c:if>
					
 				</p>
			</div>
		</div>
	</div>
		<jsp:include page="supplierGrnItems.jsp" />
	
		<jsp:include page="supplierGrnAudit.jsp" />
		
</div>
<!-- Accept GRN -->
<div class="modal fade" id="modal-grnAccept" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.grnAccept.confirm.header" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="idGrnForm"  method="post"> 
				<input type="hidden" name="grnId" value="${goodsReceiptNote.id}"> 
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="supplier.grn.accept.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Enter Comments.(Optional)" rows="3" name="supplierRemark" id="supplierRemark" data-validation="required length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="acceptGrnBtn" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
		 	</form> 
		</div>
	</div>
</div>

<!-- Decline GRN -->
<div class="modal fade" id="modal-grnDeclined" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.grnDecline.confirm" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="idDeclineGrn"  method="post">
				<input type="hidden" name="grnId" value="${goodsReceiptNote.id}"> 			 
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="grn.decline.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Enter Comments.(Mandatory)" rows="3" name="supplierRemark" id="supplierRemark" data-validation="required length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="idDecline" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>



<style>
.width-90 {
	width: 90%;
}

div.radio[id^='uniform-']>span {
	margin-top: 0 !important;
}

label.select-radio {
	width: auto;
}

#event {
	padding-left: 0;
}

.input-group.mrg15T.mrg15B {
	background-color: #f5f5f5;
	margin-bottom: 0;
	padding: 0;
}

.margeinAllMDZero {
	margin: 0;
	clear: both;
}

.marginBottomA {
	margin-bottom: 20px;
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

.box_active {
	background: rgba(0, 0, 0, 0) none repeat scroll 0 0 !important;
}

//
.caret {
	color: #fff !important;
}

.cq_row_parent .input-group-btn {
	width: 200px;
}

.dropdown-menu input {
	display: inline !important;
	width: auto !important;
}

.advancee_menu ul {
	top: 100% !important;
	left: 0 !important;
}

.dropdown-menu .checkbox {
	display: inline-block !important;
	margin: 0 !important;
	padding: 0 !important;
	position: relative !important;
}

.dataTables_wrapper.form-inline.no-footer {
	overflow: hidden;
}

.grand-price-heading {
	width: 250px;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting_asc::after {
	content: "" !important;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting::after {
	content: "" !important
}

.inactiveCaption {
	margin: 0 0px 0 0px !important;
	font-weight: bold !important;
	color: #ff1d33 !important;
}

.white-space-pre {
	white-space: pre;
}
</style>

<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'file'
	});
	
	$('#declinedGrn').click(function() {
		$('#modal-grnDeclined').modal();
	});

	$('#acceptGrn').click(function() {
		$('#modal-grnAccept').modal();
	});

$('document').ready(function() {
	$('#acceptGrnBtn').on('click', function(e) {
		console.log('Accepted clicked....');
		e.preventDefault();
		if($("#idGrnForm").isValid()) {
			$(this).addClass('disabled');
 			$('#acceptGrn').addClass('disabled');
			$('#declinedGrn').addClass('disabled');
 			$('#idGrnForm').attr('action', getContextPath() + '/supplier/acceptGrn');
			$("#idGrnForm").submit();
		}else{
			return;
		}
	});

	$('#idDecline').on('click', function(e) {
		console.log('Decline clicked....');
		e.preventDefault();
		if($("#idDeclineGrn").isValid()) {
			$(this).addClass('disabled');
 			$('#acceptGrn').addClass('disabled');
			$('#declinedGrn').addClass('disabled');
 			$('#idDeclineGrn').attr('action', getContextPath() + '/supplier/declineGrn');
			$("#idDeclineGrn").submit();
		}else{
			return;
		}
	});

		
});


</script>


<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">