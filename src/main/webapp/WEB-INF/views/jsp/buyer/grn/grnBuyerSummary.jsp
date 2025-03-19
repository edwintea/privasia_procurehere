<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="(hasRole('ROLE_GRN_EDIT') or hasRole('ADMIN')) and hasRole('BUYER')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_GRN_VIEW_ONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerAdminReadOnly" />

<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});

	function canEdit() {
		return "${canEdit}";
	}
</script>
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
					<spring:message code="grn.id.label" />
					: ${goodsReceiptNote.grnId}
				</h2>
				<br>
				<h2>
					<spring:message code="application.supplier" />
					<c:if test="${not empty goodsReceiptNote.supplierName}">
						: ${goodsReceiptNote.supplierName}
					</c:if>
				</h2>
				<br>
				<h2>
					<spring:message code="grn.list.businessunit" />
					<c:if test="${not empty goodsReceiptNote.businessUnit}">
						: ${goodsReceiptNote.businessUnit.unitName}
					</c:if>
				</h2>
			</div>
		</div>
	</div>
	<c:if test="${goodsReceiptNote.status ne 'DRAFT'}">
		<div class="pull-right">
			<form:form action="${pageContext.request.contextPath}/buyer/downloadGrn/${goodsReceiptNote.id}" method="GET">
				<button class="btn btn-sm float-right btn-info hvr-pop marg-left-10 downloadGrnBtn" type="submit" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="grnsummary.download.grn.button" />'>
					<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
					</span> <span class="button-content"><spring:message code="grnsummary.download.grn.button" /></span>
				</button>
			</form:form>
		</div>
	</c:if>
	
</div>
<form:form name="form" id="grnForm" method="post" action="">
<input type="hidden" id="id" name="id" value="${goodsReceiptNote.id}">
<input type="hidden" id="poId" name="poId" value="${goodsReceiptNote.po.id}">
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
			  <c:if test="${goodsReceiptNote.status eq 'DRAFT'}">
			      <input name="grnName" value="${goodsReceiptNote.grnTitle}" type="text" placeholder="${placeReference}" data-validation="required length" data-validation-length="max128" class="form-control" />
			  </c:if>
			  <c:if test="${goodsReceiptNote.status ne 'DRAFT'}">
				 <p>${goodsReceiptNote.grnTitle}</p> 
			  </c:if>
			</div>
		</div>		
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="grn.list.grn.referenceNumber" />
				</label>
			</div>
			<spring:message var="placeReference" code="grn.list.grn.referenceNumber" />
			<div class="col-sm-5 col-md-5 col-xs-6">
			  <c:if test="${goodsReceiptNote.status eq 'DRAFT'}">
			      <input name="referenceNumber" value="${goodsReceiptNote.referenceNumber}" type="text" placeholder="${placeReference}" data-validation="required length" data-validation-length="max64" class="form-control" />
			  </c:if>
			  <c:if test="${goodsReceiptNote.status ne 'DRAFT'}">
				 <p>${goodsReceiptNote.referenceNumber}</p> 
			  </c:if>
			</div>
		</div>	
		<c:if test="${not empty goodsReceiptNote.po}">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="grn.poNumber.label" />
					</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
				<p><a href="${pageContext.request.contextPath}/buyer/poView/${goodsReceiptNote.po.id}">${goodsReceiptNote.po.poNumber}</a></p>
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
		<h4><spring:message code="grn.summary.supplier.label" /></h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="prsummary.supplier.info" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<c:if test="${not empty goodsReceiptNote.supplier}">
					<div class="">
						<%-- <h5>${pr.supplier.supplier.companyName}</h5> --%>
						<span class='desc'>${goodsReceiptNote.supplier.companyName}<br />${goodsReceiptNote.po.supplier.fullName}<br />${goodsReceiptNote.po.supplier.communicationEmail}<br />${goodsReceiptNote.po.supplier.companyContactNumber}</span>
					</div>
				</c:if>
				<c:if test="${empty goodsReceiptNote.supplier}">
					<div class="">
						<span class='desc'>${goodsReceiptNote.supplierName}<br />${goodsReceiptNote.supplierTelNumber}<br />${goodsReceiptNote.supplierFaxNumber}<br />${goodsReceiptNote.supplierTaxNumber}</span>
					</div>
				</c:if>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="prevent.supplier.address" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<c:if test="${not empty goodsReceiptNote.supplier}">
					<div class="">
						<span class='desc'>${goodsReceiptNote.supplier.line1}<br />${goodsReceiptNote.supplier.line2}<br />${goodsReceiptNote.supplier.city}</span>
					</div>
				</c:if>
				<c:if test="${empty goodsReceiptNote.supplier}">
					<div class="">
						<span class='desc'>${goodsReceiptNote.po.supplierAddress}</span>
					</div>
				</c:if>
			</div>
		</div>
	</div>

	

	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4><spring:message code="grn.delivery.details.label" /></h4>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<c:if test="${goodsReceiptNote.status eq  'DRAFT' }">
					<div class="form-tander1">
						<div class="form_field">
							<div class="form-group">
									<div class="form-group">
										<label for="idReceiver" class="col-sm-3 control-label"><spring:message code="supplier.do.summary.receiver" /> :</label>
											<div class="col-sm-6 col-md-5">
											    <input name="deliveryReceiver" type="text" value="${goodsReceiptNote.deliveryReceiver}" data-validation="required length" data-validation-length="1-150" class="form-control" placeholder="Receiver" />
											</div> 
									</div>
									<div class="form-group">
										<label for="idAdressOne" class="col-sm-3 control-label"><spring:message code="delivery.adds.line1" /> :</label>
											<div class="col-sm-6 col-md-5">
											    <input name="deliveryAddressLine1" type="text" value="${goodsReceiptNote.deliveryAddressLine1}" data-validation="required length" data-validation-length="1-250" class="form-control" placeholder="Address Line 1" />
											</div> 
									</div>
									<div class="form-group">
										<label for="idAdressTwo" class="col-sm-3 control-label"><spring:message code="delivery.adds.line2" /> :</label>
											<div class="col-sm-6 col-md-5">
											    <input name="deliveryAddressLine2" type="text" value="${goodsReceiptNote.deliveryAddressLine2}" data-validation="required length" data-validation-length="1-250" class="form-control" placeholder="Address Line 2" />
											</div> 
									</div>
									<div class="form-group">
										<label for="idAdressCity" class="col-sm-3 control-label"><spring:message code="delivery.city" /> :</label>
											<div class="col-sm-6 col-md-5">
											   <input name="deliveryAddressCity" type="text" value="${goodsReceiptNote.deliveryAddressCity}" data-validation="required length" data-validation-length="1-250" class="form-control" placeholder="City" />
											</div> 
									</div>
									<div class="form-group">
										<label for="idAdressState" class="col-sm-3 control-label"><spring:message code="delivery.state" /> :</label>
											<div class="col-sm-6 col-md-5">
											   <input name="deliveryAddressState" type="text" value="${goodsReceiptNote.deliveryAddressState}" data-validation="required length" data-validation-length="1-150" class="form-control" placeholder="State" />
											</div> 
									</div>
									<div class="form-group">
										<label for="idAdressPostCode" class="col-sm-3 control-label"><spring:message code="delivery.postcode" /> :</label>
											<div class="col-sm-6 col-md-5">
											    <input name="deliveryAddressZip" type="text" value="${goodsReceiptNote.deliveryAddressZip}" data-validation="required length" data-validation-length="1-32" class="form-control" placeholder="Zip Code" />
											</div> 
									</div>
									<div class="form-group">
										<label for="idAdressCountry" class="col-sm-3 control-label"><spring:message code="delivery.country" /> :</label>
											<div class="col-sm-6 col-md-5">
											   <input name="deliveryAddressCountry" type="text" value="${goodsReceiptNote.deliveryAddressCountry}" data-validation="required length" data-validation-length="1-128" class="form-control" placeholder="Country" />
											</div> 
									</div>
									<div class="form-group">
										<label for="idGoodsReceiptDate" class="col-sm-3 control-label"><spring:message code="grnsummary.receipt.date" /> </label>
											<div class="col-sm-6 col-md-5">
												<div class="col-sm-4 col-md-4" style="margin-left: -14px;">
												   	<spring:message code="dateformat.placeholder" var="dateformat"/>
												     <spring:message code="grnsummary.receipt.date" var="grnDate"/>
												     <fmt:formatDate var="receiptDate" value="${goodsReceiptNote.goodsReceiptDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
													  <input name="goodsReceiptDate" style="cursor: pointer;" id="goodsReceiptDate" value="${receiptDate}" data-placement="top" data-toggle="tooltip" data-original-title="${grnDate}" class="bootstrap-datepicker form-control for-clander-view" data-validation="required date"  data-validation-format="dd/mm/yyyy"
												       placeholder="${dateformat}" autocomplete="off" />
											  	</div>
											  	<div class="col-sm-3 col-md-3">
											  		<fmt:formatDate var="receiptTime"  value="${goodsReceiptNote.goodsReceiptTime}" pattern="hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
											    	  <input name="goodsReceiptTime" style="width: 110px; cursor: pointer;"  value="${receiptTime}" id="goodsReceiptTime" onfocus="this.blur()"  class="bootstrap-timepicker timepicker-example for-timepicker-view form-control" data-validation="required" autocomplete="off"   />
												</div>
											</div> 
											
									</div>
								<div id="address-buyer-dialog"></div>
							</div>
						</div>
					</div>	
				</c:if>
			 <c:if test="${goodsReceiptNote.status ne 'DRAFT'}">
				<div class="form-group">
					<label for="idReceiver" class="col-sm-3 control-label"><spring:message code="supplier.do.summary.receiver" /> </label>
						<div class="col-sm-6 col-md-5">
						    <p>${goodsReceiptNote.deliveryReceiver}</p>
						</div> 
				</div>
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
				<div class="form-group">
					<label for="idGoodsReceiptDate" class="col-sm-3 control-label"><spring:message code="grnsummary.receipt.date" /> </label>
						<div class="col-sm-6 col-md-5">
						   	<spring:message code="dateformat.placeholder" var="dateformat"/>
						     <spring:message code="grnsummary.receipt.date" var="grnDate"/>
						    <fmt:formatDate var="receiptDate" value="${goodsReceiptNote.goodsReceiptDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						     <p>${receiptDate}</p>
					</div> 
				</div>
			</c:if>
		</div>
	</div>
	
		<jsp:include page="grnItems.jsp" />
	
		<jsp:include page="grnAudit.jsp" />
		
			<div class="row">
				<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20">
		          <c:if test="${goodsReceiptNote.status == 'DRAFT'}">
					 <form:form id="finishGrnForm" method="post">
						<input type="hidden" name="grnId" value="${goodsReceiptNote.id}"> 
						<a href="#confirmReceived" id="confirmReceivedBtn" role="button" class="btn btn-info ph_btn hvr-pop marg-left-10 right-header-button hvr-rectangle-out ${ !canEdit or buyerAdminReadOnly ? 'disabled' : '' } " data-toggle="modal"><spring:message code="grn.received.items" /></a>
					 </form:form>
					 <button type="button" id="saveDraftBtn"  class="btn btn-black ph_btn hvr-pop marg-left-10 right-header-button skipvalidation ${ !canEdit or buyerAdminReadOnly ? 'disabled' : '' }" >
						<spring:message code="application.draft" />
					</button>
				 </c:if>
				 <c:if test="${goodsReceiptNote.status eq 'RECEIVED' or goodsReceiptNote.status eq 'DRAFT'}">
						<a href="#confirmCancel" id="confirmCancelBtn" role="button" class="btn btn-danger ph_btn hvr-pop marg-left-10 right-header-button  ${ !canEdit or buyerAdminReadOnly ? 'disabled' : '' }" data-toggle="modal"><spring:message code="grn.application.cancel" /></a>
				 </c:if>
				</div>
			</div>
			
				
</div>
</form:form>

<div class="modal fade" id="confirmReceived" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="grn.confirm.receive" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="grn.receive.confirm" />
							</label>
						</div>
					</div>
			</div>
				<input type="hidden" name="id" value="${goodsReceiptNote.id}"> 
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="receivedBtn" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			
		</div>
	</div>
</div>


<div class="modal fade" id="confirmCancel" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="grn.confirm.cancelgrn" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/buyer/cancelGrn" method="post">
				<input type="hidden" name="grnId" value="${goodsReceiptNote.id}"> 
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
					    <div class="modal-body col-md-12">
							<label> <spring:message code="grn.cancel.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Enter Comments.(Mandatory)" rows="3" name="buyerRemark" id="buyerRemark" data-validation="required length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="cancelGrn" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
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
	width: 42px !important;
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
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script>
	<c:if test="${buyerReadOnlyAdmin and !canEdit}">
	$(window).bind('load', function() {
		var allowedFields = '.button-previous, #dashboardLink, #listLink';
		disableFormFields(allowedFields);
	});
	</c:if>
</script>
<script>
	$.validate({
		lang : 'en',
		modules : 'file'
	});
	
	$('document').ready(function() {
		
		$('#receivedBtn').on('click', function(e) {
			e.preventDefault();
			if($("#grnForm").isValid()) {
				$(this).addClass('disabled');
				$('#confirmReceivedBtn').addClass('disabled');
				$('#confirmCancelBtn').addClass('disabled');
				$('#saveDraftBtn').addClass('disabled');
				$('#grnForm').attr('action', getContextPath() + '/buyer/receivedGrn');
				$("#grnForm").submit();
			} else {
				 $('body').removeClass("modal-open");
				$("#confirmReceived").hide();
				return;
			}

		});

		//Skip JQuery validations for save draft
		$(".skipvalidation ").on('click', function(e) {
			if ($("#skipper").val() == undefined) {
				e.preventDefault();
				$(this).after("<input type='hidden' id='skipper' value='1'>");
				$('form.has-validation-callback :input').each(function() {
					$(this).on('beforeValidation', function(value, lang, config) {
						$(this).attr('data-validation-skipped', 1);
					});
				});
				$(this).trigger("click");
			}
		});
		
		
		$('#saveDraftBtn').on('click', function(e) {
			$('#grnForm').attr('action', getContextPath() + '/buyer/saveGrnDraft');
			$("#grnForm").submit();
		});

	});
</script>

<script>
$(function() {
	"use strict";
	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

	$('.bootstrap-datepicker').bsdatepicker({
		format : 'dd/mm/yyyy',
		maxDate : now,
		onRender : function(date) {
			return date.valueOf() > now.valueOf() ? 'disabled' : '';
		}

	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
		$('.bootstrap-datepicker').validate(function(valid, elem) {});
	});
});

$('document').ready(function(){
	$('.timepicker-example').timepicker({
		disableFocus : false,
		explicitMode : false
	}).on('show.timepicker', function(e) {
		setTimeout(function() {
			$('.bootstrap-timepicker-widget.dropdown-menu.open input').addClass('disabled');
			$('.timepicker-example').validate(function(valid, elem) {});
		}, 500);
		
		
	});
});
</script>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">