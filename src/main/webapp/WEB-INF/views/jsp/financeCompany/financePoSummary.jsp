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

.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}
</style>
<div class="clear"></div>
<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
<div class="white-bg border-all-side float-left width-100 pad_all_15">
	<div class="row">
		<div class="col-md-6 col-sm-12 col-xs-12">
			<input type="hidden" id="prId" value="${po.id}">
			<div class="tag-line">
				<h2>
					<c:if test="${empty po.businessUnit}">N/A</c:if>${po.businessUnit.displayName}
				</h2>
				<br>
				<h2>Requester : ${po.requester}</h2>
			</div>
		</div>
	</div>

		<form:form action="${pageContext.request.contextPath}/finance/financePoReport/${po.id}" method="GET">
			<button class="btn btn-sm float-right btn-primary hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title="Download PO">
				<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
				</span> <span class="button-content">Download</span>
			</button>
		</form:form>
</div>
<div class="clear"></div>
<div class="tab-pane" style="display: block">
	<div class="upload_download_wrapper clearfix event_info">
		<h4>General Information</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> PO Reference Number </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.poNumber}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label>Date &amp; Time</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p><fmt:formatDate value="${po.createdDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></p>
			</div>
		</div>

		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label>Finance PO Reference Number</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${pofinance.referenceNum}</p>
			</div>
		</div>

		<%-- <div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> PO Creator </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					${po.createdBy.name} <br> ${po.createdBy.communicationEmail} <br>
					<c:if test="${not empty po.buyer.companyContactNumber}">Tel: ${po.buyer.companyContactNumber}</c:if>
					<c:if test="${not empty po.buyer.faxNumber}">Fax: ${po.buyer.faxNumber}</c:if>
					<c:if test="${not empty po.createdBy.phoneNumber}">HP: ${ po.createdBy.phoneNumber}</c:if>
				</p>
			</div>
		</div> --%>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.requester" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.requester}</p>
			</div>
		</div>

	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>Finance Information</h4>



		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> Payment Terms : </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.paymentTerm}</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="pr.correspondence.address" />
		</h4>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height">Address Title</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
					<c:if test="${not empty po.correspondAddressTitle}">
						${po.correspondAddressTitle} <br> ${po.correspondAddressLine1},
						<c:if test="${not empty po.correspondAddressLine2}">${po.correspondAddressLine2} ,</c:if>${ po.correspondAddressZip},
						<br>${ po.correspondAddressState},${ po.correspondAddressCountry}
					</c:if>
				</p>
			</div>
		</div>
	</div>

		<jsp:include page="financePoDocument.jsp"></jsp:include>
 
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4><spring:message code="pr.delivery.detail" /></h4>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height">Receiver</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">${po.deliveryReceiver}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height">Delivery Address</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
					<c:if test="${not empty po.deliveryAddressTitle}">
						<h5>${po.deliveryAddressTitle}</h5>
						<span class='desc'>${po.deliveryAddressLine1}, ${po.deliveryAddressLine2}, ${po.deliveryAddressCity}, ${po.deliveryAddressZip}, ${po.deliveryAddressState}, ${po.deliveryAddressCountry}</span>
					</c:if>
				</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box ">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label>Delivery Date &amp; Time</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<fmt:formatDate value="${po.deliveryDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>Purchase Item</h4>

		<div class="pad_all_15 float-left width-100">
			<div class="main_table_wrapper ph_table_border mega">
				<table class="ph_table border-none header parentBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<thead>
						<tr>
							<th class="width_50 width_50_fix">No.</th>
							<th class="align-left width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>
							<th class="align-left width_100_fix"><spring:message code="label.rftbq.th.unit" /></th>
							<th class="align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.quantity" /></th>
							<th class=" align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.unitprice" /> (${po.currency})</th>
							<c:if test="${not empty po.field1Label}">
								<th class="align-left width_100 width_100_fix">${po.field1Label}</th>
							</c:if>
							<c:if test="${not empty po.field2Label}">
								<th class="align-left width_100 width_100_fix">${po.field2Label}</th>
							</c:if>
							<c:if test="${not empty po.field3Label}">
								<th class="align-left width_100 width_100_fix">${po.field3Label}</th>
							</c:if>
							<c:if test="${not empty po.field4Label}">
								<th class="align-left width_100 width_100_fix">${po.field4Label}</th>
							</c:if>
							<th class="width_150 width_150_fix align-right"><spring:message code="prtemplate.total.amount" /> (${po.currency})</th>
							<th class="width_150 align-right width_150_fix">Tax Amount (${po.currency})</th>
							<th class="width_200 width_200_fix align-right">Total Amount With Tax (${po.currency})</th>
						</tr>
					</thead>
				</table>
				<table class="data ph_table border-none childBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<tbody>
						<c:forEach items="${prItemlist}" var="item">
							<tr>
								<td class="width_50 width_50_fix">${item.level}<c:if test="${item.order != '0'}">.${item.order}</c:if>
								</td>
								<td class="align-left width_200_fix">${item.itemName}<c:if test="${not empty item.itemDescription}">
										<span class="item_detail s1_view_desc"><spring:message code="application.view.description" /></span>
									</c:if>
									<p class="s1_tent_tb_description s1_text_small">${item.itemDescription}</p>
								</td>
								<td class="align-left width_100_fix">${item.product.uom.uom}</td>
								<td class="align-right width_100 width_100_fix"> <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.quantity}" /></td>
								<td class=" align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.unitPrice}" /></td>
								<c:if test="${not empty po.field1Label}">
									<td class=" align-left width_100 width_100_fix">${item.field1}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field2Label}">
									<td class="align-left width_200 approvalUsers width_200_fix">${item.field2}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field3Label}">
									<td class="align-left width_200 width_200_fix">${item.field3}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field4Label}">
									<td class="align-left width_200 width_200_fix">${item.field4}&nbsp;</td>
								</c:if>
								<td class="width_150 width_150_fix align-right"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.totalAmount}" />
									</c:if></td>
								<td class="width_150 align-right width_150_fix"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.taxAmount}" />
									</c:if></td>
								<td class="width_200 width_200_fix align-right"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.totalAmountWithTax}" />
									</c:if></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>

		<div class="total_all total-with-tax-final table-resposive" style="font-size: 16px;">
			<table cellspacing="3" cellpadding="3" style="width: 100%; border-collapse: separate; border-spacing: 8px; padding-right: 20px;">
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td class="align-left" style="white-space: nowrap;"><strong>Total (${po.currency}) :</strong></td>
					<td style="white-space: nowrap;"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.total}" /></td>
				</tr>
				<tr>
					<td style="white-space: nowrap; vertical-align: top;"><strong>Additional Charges:</strong></td>
					<td style="width: 100%; padding-left: 10px; padding-right: 10px;">${po.taxDescription}</td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-left"><strong>(${po.currency}):</strong></td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.additionalTax}" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td style="white-space: nowrap;" class="align-left"><strong><spring:message code="submission.report.grandtotal" /> (${po.currency}):</strong></td>
					<td style="white-space: nowrap;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.grandTotal}" /></td>
				</tr>
			</table>
		</div>






	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info remark-tab">
		<c:if test="${not empty po.remarks}">
			<h4>Remarks</h4>
			<div class="pad_all_15 float-left width-100">
				<label>General Remarks</label>
				<p>${po.remarks}</p>
			</div>
		</c:if>
		<c:if test="${not empty po.termsAndConditions}">
			<div class="pad_all_15 float-left width-100">
				<label>Terms & Conditions</label>
				<p>${po.termsAndConditions}</p>
			</div>
		</c:if>
	</div>



	<c:if test="${!isShared}">
		<div class="upload_download_wrapper clearfix marg-top-10 event_info">
			<h4>Finance</h4>
			<c:url var="financePoForm" value="/finance/financePo" />
			<form:form action="${financePoForm}" id="demo-form" method="post" modelAttribute="pofinance" autocomplete="off">
				<input name="poId" type="hidden" value="${po.id}" />
				<form:input path="id" class="rec_inp_style1" type="hidden" />
				<div class="row marg-bottom-20">
					<div class="col-sm-6">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label class="set-line-height">PO Status</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<spring:message code="timezone.required" var="required" />
							<form:select id="financePoStatus" path="financePoStatus" class="chosen-select" data-validation="required">
								<option value="">Select Status</option>
								<form:options items="${financePoStatus}" itemLabel="value"></form:options>
							</form:select>
						</div>
					</div>
					<div class="col-sm-6" id="referralFee">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label class="set-line-height">Referral Fee</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<form:input path="referralFee" class="rec_inp_style1" type="text" />
						</div>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-sm-6">

						<div class="col-sm-4 col-md-3 col-xs-6">
							<label class="set-line-height">Remark</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<form:textarea path="remark" class="rec_inp_style1" />
						</div>


					</div>
				</div>

				<div class="row marg-bottom-202">
					<div class="col-md-3">
						<label class="marg-top-10"></label>
					</div>
					<div class="col-md-9 dd sky mar_b_15">
						<button type="submit" id="financeSettings" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">${not empty financeSettings.id ? 'Update' : 'Save' }</button>
						<c:url value="/finance/financePoList" var="financeDashboard" />
						<a href="${financeDashboard}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
						</a>
					</div>
				</div>

			</form:form>
		</div>

		<jsp:include page="financePoAudit.jsp" />
	</c:if>
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
</script>
<script type="text/javascript">
	$(document).ready(function() {
		var value = $('#financePoStatus').val();
		if (value == 'FINANCE_SETTLED') {
			$('#referralFee').show();
		} else {
			$('#referralFee').hide();
		}

		$('#financePoStatus').change(function() {
			var val = $(this).val();

			if (val == 'FINANCE_SETTLED') {
				$('#referralFee').show();
			} else {
				$('#referralFee').hide();
			}
		});
	});
</script>


<script type="text/javascript" src="<c:url value="/resources/js/view/prCreate.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/prSummary.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">