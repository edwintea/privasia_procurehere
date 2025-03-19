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
		<div class="col-md-6 col-sm-12 col-xs-12">
			<input type="hidden" id="invoiceId" value="${invoice.id}">
			<div class="tag-line">
				<h2>
					<spring:message code="supplier.invoiceListing.invoiceId" />
					: ${invoice.invoiceId}
				</h2>
				<br/>
				<c:if test="${invoice.status ne 'DRAFT' }">	
					<h2>
						<spring:message code="supplier.invoice.summary.invoiceDate" />
						: <fmt:formatDate value="${invoice.invoiceSendDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
					</h2>
					<br>
				</c:if>
				<h2>
					<spring:message code="application.supplier" />
					: ${invoice.supplier.companyName}
				</h2>
				<br/>
				<h2>
					<spring:message code="supplier.invoiceListing.businessUnit" />
					: <c:if test="${empty invoice.businessUnit}">N/A</c:if>${invoice.businessUnit.unitName}
				</h2>
				<c:if test="${invoice.requestForFinance and invoice.status == 'INVOICED'}">
					<br/>
					<h2 class="text-warning">
						<b>Note:</b> FinansHere has approved the financing for this invoice. Accepting this invoice will auto accept the financing request and the payment instruction.
					</h2>
				</c:if>
				<c:if test="${not empty invoiceFinanceRequest}">
					<br>
					<h2>
						<spring:message code="funding.requested.date" />
							: <fmt:formatDate value="${invoiceFinanceRequest.requestedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
					</h2>
					<br/>
					<h2>
						<spring:message code="funding.requested.status" />
							: ${invoiceFinanceRequest.requestStatus}
					</h2>
				</c:if>
			</div>
		</div>
		
		<div class="col-lg-6 col-md-12 col-sm-12 col-xs-12">
		<c:if test="${invoice.status != 'CANCELLED' and invoice.status != 'DECLINED'}">
			<div class="pull-right marg-right-10">
				<form:form action="${pageContext.request.contextPath}/buyer/downloadInvoiceReport/${invoice.id}" method="GET">
					<button class="btn ph_btn_small float-right btn-info hvr-pop marg-left-10 downloadPoBtn" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="invoicesummary.download.invoice.button" />'>
						<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
						</span> <span class="button-content"><spring:message code="invoicesummary.download.invoice.button" /></span>
					</button>
				</form:form>
			</div>
			</c:if>
			<c:if test="${invoice.status eq 'ACCEPTED' and !empty invoiceFinanceRequest and invoiceFinanceRequest.requestStatus == 'REQUESTED'}">
				<div class="pull-right">
					<form:form action="${pageContext.request.contextPath}/buyer/declineFinanceRequest" method="POST">
						<input type="hidden" name="invoiceId" value="${invoice.id}">
						<button class="btn ph_btn_small float-right btn-danger hvr-pop marg-left-10" type="submit" data-toggle="tooltip" data-placement="top" data-original-title='Decline Request for Financing'>
							<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-times"></i></span> 
							<span class="button-content">Decline Request for Financing</span>
						</button>
					</form:form>
				</div>
				<div class="pull-right">
					<form:form action="${pageContext.request.contextPath}/buyer/acceptFinanceRequest" method="POST">
						<input type="hidden" name="invoiceId" value="${invoice.id}">
						<button class="btn ph_btn_small float-right btn-success hvr-pop marg-left-10" type="submit" data-toggle="tooltip" data-placement="top" data-original-title='Accept Request for Financing'>
							<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-check"></i></span> 
							<span class="button-content">Accept Request for Financing</span>
						</button>
					</form:form>
				</div>
			</c:if>
			<c:if test="${invoice.status=='INVOICED'}">
				<button id="declinedInvoice" class="btn ph_btn_small float-right btn-danger hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.buyer.invoice.declined" />'>
					<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-times"></i></span>
					<span class="button-content"><spring:message code="supplier.po.summary.declined" /></span>
				</button>
			
				<button id="acceptInvoice" class="btn ph_btn_small float-right btn-success hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.buyer.invoice.accept" />'>
					 <span class="glyph-icon icon-separator"> <i class="glyph-icon icon-check"></i></span> 
					 <span class="button-content"><spring:message code="supplier.po.summary.accept" /></span>
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
				<label> <spring:message code="supplier.invoiceListing.invoiceName" />
				</label>
			</div>
			<spring:message var="placeReference" code="invoice.place.name" />
			<div class="col-sm-5 col-md-5 col-xs-6">
 				 <p>${invoice.name}</p> 
 			</div>
		</div>		
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="supplier.invoiceListing.poNumber" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
			<p><a href="${pageContext.request.contextPath}/buyer/poView/${invoice.po.id}">${invoice.po.poNumber}</a></p>
			</div>
		</div>
 		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="supplier.invoice.summary.creator" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					${invoice.createdBy.name} <br> ${invoice.createdBy.communicationEmail} <br>
					<c:if test="${not empty invoice.supplier.companyContactNumber}"><spring:message code="prdraft.tel" />: ${invoice.supplier.companyContactNumber}</c:if>
					<c:if test="${not empty invoice.supplier.faxNumber}"><spring:message code="prtemplate.fax" />: ${invoice.supplier.faxNumber}</c:if>
					<c:if test="${not empty invoice.createdBy.phoneNumber}"><spring:message code="prtemplate.hp" />: ${ invoice.createdBy.phoneNumber}</c:if>
				</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4><spring:message code="rfs.summary.finance.information" /></h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.base.currency" /> </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${invoice.currency.currencyCode}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.decimal.label" /> </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${invoice.decimal}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.paymentterm.label" /> </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${invoice.paymentTerm}</p>
			</div>
		</div>
		<c:if test="${not empty invoice.paymentTermDays}">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> Payment Days :</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>${invoice.paymentTermDays}</p>
				</div>
			</div>
		</c:if>
		
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="invoice.summary.finance.request.label" /> </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${invoice.requestForFinance ? 'Yes' : 'No'}</p>
			</div>
		</div>

	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="invoice.billing.details" />
		</h4>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.attention" /></label>
			</div>
			<spring:message var="placeholdReference" code="supplier.place.attention" />
			<div class="col-sm-5 col-md-5 col-xs-6">
 				<p class="set-line-height">${invoice.attentionTo}</p>
 			</div>
		</div>
		
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.billing.address" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
 					  ${invoice.line1} <br> 
						<c:if test="${not empty invoice.line2}">${invoice.line2}  <br></c:if>
						<c:if test="${not empty invoice.line3}">${invoice.line3}  <br></c:if>
						<c:if test="${not empty invoice.line4}">${invoice.line4}  <br></c:if>
						<c:if test="${not empty invoice.line5}">${invoice.line5}  <br></c:if>
						<c:if test="${not empty invoice.line6}">${invoice.line6}  <br></c:if>
						<c:if test="${not empty invoice.line7}">${invoice.line7}  <br></c:if>
					
				</p>
			</div>
		   </div>	
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="supplier.invoice.duedate" /> </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<fmt:formatDate value="${invoice.dueDate}" pattern="dd/MM/yyyy"	timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				</p>
			</div>
	</div>

	<c:if test="${invoice.includeDelievryAdress}">
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4><spring:message code="pr.delivery.detail" /></h4>

		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.do.summary.receiver" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">${invoice.deliveryReceiver}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.do.summary.deliveryadds" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
						<span class='desc'> ${invoice.deliveryAddressLine1}  <br>
							<c:if test="${not empty invoice.deliveryAddressLine2}"> ${invoice.deliveryAddressLine2} <br></c:if>
							<c:if test="${not empty invoice.deliveryAddressCity}"> ${invoice.deliveryAddressCity} <br></c:if>
							<c:if test="${not empty invoice.deliveryAddressZip}"> ${invoice.deliveryAddressZip} <br></c:if>
							${invoice.deliveryAddressState}, ${invoice.deliveryAddressCountry}
						</span>
					
				</p>
			</div>
		</div>

	</div>
	</c:if>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>Invoice Item</h4>

		<div class="pad_all_15 float-left width-100">
			<div class="main_table_wrapper ph_table_border mega">
				<table class="ph_table border-none header parentBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<thead>
						<tr>
							<th class="width_50 width_50_fix"><spring:message code="supplier.no.col" /></th>
							<th class="align-left width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>
							<th class="align-left width_100_fix"><spring:message code="label.rftbq.th.uom" /></th>
							<th class="align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.quantity" /></th>
							<th class=" align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.unitprice" /> (${invoice.currency})</th>
							<c:if test="${not empty invoice.field1Label}">
								<th class="align-left width_100 width_100_fix">${invoice.field1Label}</th>
							</c:if>
							<c:if test="${not empty invoice.field2Label}">
								<th class="align-left width_100 width_100_fix">${invoice.field2Label}</th>
							</c:if>
							<c:if test="${not empty invoice.field3Label}">
								<th class="align-left width_100 width_100_fix">${invoice.field3Label}</th>
							</c:if>
							<c:if test="${not empty invoice.field4Label}">
								<th class="align-left width_100 width_100_fix">${invoice.field4Label}</th>
							</c:if>
							<th class="width_150 width_150_fix align-right"><spring:message code="prtemplate.total.amount" /> (${invoice.currency})</th>
							<th class="width_150 align-right width_150_fix"><spring:message code="prtemplate.tax.amount" /> (${invoice.currency})</th>
							<th class="width_200 width_200_fix align-right"><spring:message code="prtemplate.total.amount.tax" /> (${invoice.currency})</th>
						</tr>
					</thead>
				</table>
				<table class="data ph_table border-none childBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<tbody>
						<c:forEach items="${invoiceItemlist}" var="item">
							<tr>
								<td class="width_50 width_50_fix">${item.level}.${item.order}
								</td>
								<td class="align-left width_200_fix">${item.itemName}<c:if test="${not empty item.itemDescription}">
										<span class="item_detail s1_view_desc"> <spring:message code="application.view.description" /> </span>
									</c:if>
									<p class="s1_tent_tb_description s1_text_small">${item.itemDescription}</p>
								</td>
								<td class="align-left width_100_fix">${item.unit.uom}</td>
								<td class="align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${invoice.decimal}" maxFractionDigits="${invoice.decimal}" value="${item.quantity}" /></td>
								<td class=" align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${invoice.decimal}" maxFractionDigits="${invoice.decimal}" value="${item.unitPrice}" /></td>
								<c:if test="${not empty invoice.field1Label}">
									<td class=" align-left width_100 width_100_fix">${item.field1}&nbsp;</td>
								</c:if>
								<c:if test="${not empty invoice.field2Label}">
									<td class="align-left width_200 approvalUsers width_200_fix">${item.field2}&nbsp;</td>
								</c:if>
								<c:if test="${not empty invoice.field3Label}">
									<td class="align-left width_200 width_200_fix">${item.field3}&nbsp;</td>
								</c:if>
								<c:if test="${not empty invoice.field4Label}">
									<td class="align-left width_200 width_200_fix">${item.field4}&nbsp;</td>
								</c:if>
								<td class="width_150 width_150_fix align-right"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${invoice.decimal}" maxFractionDigits="${invoice.decimal}" value="${item.totalAmount}" />
									</c:if></td>
								<td class="width_150 align-right width_150_fix"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${invoice.decimal}" maxFractionDigits="${invoice.decimal}" value="${item.taxAmount}" />
									</c:if></td>
								<td class="width_200 width_200_fix align-right"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${invoice.decimal}" maxFractionDigits="${invoice.decimal}" value="${item.totalAmountWithTax}" />
									</c:if></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>

		<div class="total_all total-with-tax-final table-resposive" style="font-size: 16px;">
			<table cellspacing="3" cellpadding="3" style="width: 98.8%; border-collapse: separate; border-spacing: 8px; margin-right: 20px;">
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td class="align-left" style="white-space: nowrap;"><strong><spring:message code="prsummary.total2" /> (${invoice.currency}) :</strong></td>
					<td style="white-space: nowrap;"><fmt:formatNumber type="number" minFractionDigits="${invoice.decimal}" maxFractionDigits="${invoice.decimal}" value="${invoice.total}" /></td>
				</tr>
				<tr>
					<td style="white-space: nowrap; vertical-align: top;"><strong><spring:message code="prtemplate.additional.charges" />:</strong></td>
					<td style="width: 100%; padding-left: 10px; padding-right: 10px;">${invoice.taxDescription}</td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-left"><strong>(${invoice.currency}):</strong></td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${invoice.decimal}" maxFractionDigits="${invoice.decimal}" value="${invoice.additionalTax}" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td style="white-space: nowrap;" class="align-left"><strong><spring:message code="submission.report.grandtotal" /> (${invoice.currency}):</strong></td>
					<td style="white-space: nowrap;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${invoice.decimal}" maxFractionDigits="${invoice.decimal}" value="${invoice.grandTotal}" /></td>
				</tr>
			</table>
		</div>
	</div>
		
	<jsp:include page="buyerInvoiceAudit.jsp" />
	
</div>


<!-- Accept Invoice -->
<div class="modal fade" id="modal-invoiceAccept" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.poAccept.confirm.header" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="idInvoiceAct" method="post">
				<input type="hidden" name="invoiceId" value="${invoice.id}"> 
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="buyer.invoice.accept.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Enter comments" rows="3" name="buyerRemark" id="buyerRemark" data-validation="required length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="idAccept" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

<!-- Decline  Invoice -->
<div class="modal fade" id="modal-invoiceDeclined" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.poDecline.confirm" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="idInvoiceDec"  method="post">
				<input type="hidden" name="invoiceId" value="${invoice.id}"> 
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="buyer.invoice.decline.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Enter Comments.(Mandatory)" rows="3" name="buyerRemark" id="buyerRemark" data-validation="required length" data-validation-length="max500"></textarea>
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

	$('#declinedInvoice').click(function() {
			$('#modal-invoiceDeclined').modal();
		
	});

	$('#acceptInvoice').click(function() {
			$('#modal-invoiceAccept').modal();
		
	});
	
	$('document').ready(function() {
		
		$('#idAccept').on('click', function(e) {
			e.preventDefault();
			if($("#idInvoiceAct").isValid()) {
				$(this).addClass('disabled');
	 			$('#acceptInvoice').addClass('disabled');
				$('#declinedInvoice').addClass('disabled');
				$('#idInvoiceAct').attr('action', getContextPath() + '/buyer/acceptInvoice');
				$("#idInvoiceAct").submit();
			} else {
				return;
			}

		});
		

		$('#idDecline').on('click', function(e) {
			e.preventDefault();
			if($("#idInvoiceDec").isValid()) {
				$(this).addClass('disabled');
	 			$('#acceptInvoice').addClass('disabled');
				$('#declinedInvoice').addClass('disabled');
				$('#idInvoiceDec').attr('action', getContextPath() + '/buyer/declineInvoice');
				$("#idInvoiceDec").submit();
			} else {
				return;
			}

		});

	});
	
	
</script>


<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">