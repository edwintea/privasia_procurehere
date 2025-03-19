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
			<input type="hidden" id="doId" value="${deliveryOrder.id}">
			<div class="tag-line">
				<h2>
					<spring:message code="supplier.doListing.deliveryId" />
					: ${deliveryOrder.deliveryId}
				</h2>
				<br>
				<c:if test="${deliveryOrder.status ne 'DRAFT'}">
					<h2>
						<spring:message code="supplier.do.summary.doDate" />
						:
						<fmt:formatDate value="${deliveryOrder.doSendDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
					</h2>
					<br>
				</c:if>
				<h2>
					<spring:message code="application.supplier" />
					: ${deliveryOrder.supplier.companyName}
				</h2>
				<br>
				<h2>
					<spring:message code="supplier.po.summary.businessUnit" />
					:
					<c:if test="${empty deliveryOrder.businessUnit}">N/A</c:if>${deliveryOrder.businessUnit.unitName}
				</h2>
			</div>
		</div>
		<div class="col-lg-6 col-md-12 col-sm-12 col-xs-12">
			<c:if test="${deliveryOrder.status != 'CANCELLED' and deliveryOrder.status != 'DECLINED'}">
				<div class="pull-right marg-right-10">
					<form:form action="${pageContext.request.contextPath}/buyer/downloadDoReport/${deliveryOrder.id}" method="GET">
						<button class="btn ph_btn_small float-right btn-info hvr-pop marg-left-10 downloadPoBtn" type="submit" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="dosummary.download.do.button" />'>
							<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span> <span class="button-content"><spring:message code="dosummary.download.do.button" /></span>
						</button>
					</form:form>
				</div>
			</c:if>
			<c:if test="${deliveryOrder.status=='DELIVERED'}">
				<button id="declinedDo" class="btn ph_btn_small float-right btn-danger hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.buyer.do.declined" />'>
					<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-times"></i></span> - <span class="button-content"><spring:message code="supplier.po.summary.declined" /></span>
				</button>

				<button id="acceptDo" class="btn ph_btn_small float-right btn-success hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.buyer.do.accept" />'>
					<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-check"></i></span> <span class="button-content"><spring:message code="supplier.po.summary.accept" /></span>
				</button>
			</c:if>
		</div>
	</div>

</div>
<div class="clear"></div>
<div class="tab-pane" style="display: block">
	<div class="upload_download_wrapper clearfix event_info">
		<h4>
			<spring:message code="prsummary.general.information" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="supplier.doListing.deliveryName" />
				</label>
			</div>
			<spring:message var="placeReference" code="do.place.name" />
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${deliveryOrder.name}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="supplier.doListing.poNumber" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<a href="${pageContext.request.contextPath}/buyer/poView/${deliveryOrder.po.id}">${deliveryOrder.po.poNumber}</a>
				</p>
			</div>
		</div>

		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="supplier.do.summary.creator" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					${deliveryOrder.createdBy.name} <br> ${deliveryOrder.createdBy.communicationEmail} <br>
					<c:if test="${not empty deliveryOrder.supplier.companyContactNumber}">
						<spring:message code="prdraft.tel" />: ${deliveryOrder.supplier.companyContactNumber}</c:if>
					<c:if test="${not empty deliveryOrder.supplier.faxNumber}">
						<spring:message code="prtemplate.fax" />: ${deliveryOrder.supplier.faxNumber}</c:if>
					<c:if test="${not empty deliveryOrder.createdBy.phoneNumber}">
						<spring:message code="prtemplate.hp" />: ${ deliveryOrder.createdBy.phoneNumber}</c:if>
				</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="rfs.summary.finance.information" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.base.currency" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${deliveryOrder.currency.currencyCode}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.decimal.label" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${deliveryOrder.decimal}</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="supplierbilling.billing.detail" />
		</h4>

		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.billing.address" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
					<span class='desc'> ${deliveryOrder.line1} <br> <c:if test="${not empty deliveryOrder.line2}">${deliveryOrder.line2}  <br>
						</c:if> <c:if test="${not empty deliveryOrder.line3}">${deliveryOrder.line3}  <br>
						</c:if> <c:if test="${not empty deliveryOrder.line4}">${deliveryOrder.line4}  <br>
						</c:if> <c:if test="${not empty deliveryOrder.line5}">${deliveryOrder.line5}  <br>
						</c:if> <c:if test="${not empty deliveryOrder.line6}">${deliveryOrder.line6}  <br>
						</c:if> <c:if test="${not empty deliveryOrder.line7}">${deliveryOrder.line7}  <br>
						</c:if>
					</span>
				</p>
			</div>
		</div>
	</div>


	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="pr.delivery.detail" />
		</h4>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.do.summary.receiver" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">${deliveryOrder.deliveryReceiver}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.do.summary.deliveryadds" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
					<span class='desc'> ${deliveryOrder.deliveryAddressLine1} <br> <c:if test="${not empty deliveryOrder.deliveryAddressLine2}"> ${deliveryOrder.deliveryAddressLine2} <br>
						</c:if> <c:if test="${not empty deliveryOrder.deliveryAddressCity}"> ${deliveryOrder.deliveryAddressCity} <br>
						</c:if> <c:if test="${not empty deliveryOrder.deliveryAddressZip}"> ${deliveryOrder.deliveryAddressZip} <br>
						</c:if> ${deliveryOrder.deliveryAddressState}, ${deliveryOrder.deliveryAddressCountry}

					</span>
				</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box ">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="eventsummary.eventdetail.deliverydate" /> &amp; <spring:message code="application.time" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<fmt:formatDate value="${deliveryOrder.deliveryDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				</p>
			</div>
		</div>



		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="delivery.tracking.number" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">${deliveryOrder.trackingNumber != null ? deliveryOrder.trackingNumber : 'N/A'}</p>
			</div>
		</div>

		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="delivery.courier.name" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">${deliveryOrder.courierName != null ? deliveryOrder.courierName : 'N/A'}</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>Delivery Item</h4>

		<div class="pad_all_15 float-left width-100">
			<div class="main_table_wrapper ph_table_border mega">
				<table class="ph_table border-none header parentBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<thead>
						<tr>
							<th class="width_50 width_50_fix"><spring:message code="supplier.no.col" /></th>
							<th class="align-left width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>
							<th class="align-left width_100_fix"><spring:message code="label.rftbq.th.uom" /></th>
							<th class="align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.quantity" /></th>
							<th class=" align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.unitprice" /> (${deliveryOrder.currency})</th>
							<c:if test="${not empty deliveryOrder.field1Label}">
								<th class="align-left width_100 width_100_fix">${deliveryOrder.field1Label}</th>
							</c:if>
							<c:if test="${not empty deliveryOrder.field2Label}">
								<th class="align-left width_100 width_100_fix">${deliveryOrder.field2Label}</th>
							</c:if>
							<c:if test="${not empty deliveryOrder.field3Label}">
								<th class="align-left width_100 width_100_fix">${deliveryOrder.field3Label}</th>
							</c:if>
							<c:if test="${not empty deliveryOrder.field4Label}">
								<th class="align-left width_100 width_100_fix">${deliveryOrder.field4Label}</th>
							</c:if>
							<th class="width_150 width_150_fix align-right"><spring:message code="prtemplate.total.amount" /> (${deliveryOrder.currency})</th>
							<th class="width_150 align-right width_150_fix"><spring:message code="prtemplate.tax.amount" /> (${deliveryOrder.currency})</th>
							<th class="width_200 width_200_fix align-right"><spring:message code="prtemplate.total.amount.tax" /> (${deliveryOrder.currency})</th>
						</tr>
					</thead>
				</table>
				<table class="data ph_table border-none childBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<tbody>
						<c:forEach items="${doItemlist}" var="item">
							<tr>
								<td class="width_50 width_50_fix">${item.level}.${item.order}</td>
								<td class="align-left width_200_fix">${item.itemName}<c:if test="${not empty item.itemDescription}">
										<span class="item_detail s1_view_desc"> <spring:message code="application.view.description" />
										</span>
									</c:if>
									<p class="s1_tent_tb_description s1_text_small">${item.itemDescription}</p>
								</td>
								<td class="align-left width_100_fix">${item.unit.uom}</td>
								<td class="align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${item.quantity}" /></td>
								<td class=" align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${item.unitPrice}" /></td>
								<c:if test="${not empty deliveryOrder.field1Label}">
									<td class=" align-left width_100 width_100_fix">${item.field1}&nbsp;</td>
								</c:if>
								<c:if test="${not empty deliveryOrder.field2Label}">
									<td class="align-left width_200 approvalUsers width_200_fix">${item.field2}&nbsp;</td>
								</c:if>
								<c:if test="${not empty deliveryOrder.field3Label}">
									<td class="align-left width_200 width_200_fix">${item.field3}&nbsp;</td>
								</c:if>
								<c:if test="${not empty deliveryOrder.field4Label}">
									<td class="align-left width_200 width_200_fix">${item.field4}&nbsp;</td>
								</c:if>
								<td class="width_150 width_150_fix align-right"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${item.totalAmount}" />
									</c:if></td>
								<td class="width_150 align-right width_150_fix"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${item.taxAmount}" />
									</c:if></td>
								<td class="width_200 width_200_fix align-right"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${item.totalAmountWithTax}" />
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
					<td class="align-left" style="white-space: nowrap;"><strong><spring:message code="prsummary.total2" /> (${deliveryOrder.currency}) :</strong></td>
					<td style="white-space: nowrap;"><fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${deliveryOrder.total}" /></td>
				</tr>
				<tr>
					<td style="white-space: nowrap; vertical-align: top;"><strong><spring:message code="prtemplate.additional.charges" />:</strong></td>
					<td style="width: 100%; padding-left: 10px; padding-right: 10px;">${deliveryOrder.taxDescription}</td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-left"><strong>(${deliveryOrder.currency}):</strong></td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${deliveryOrder.additionalTax}" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td style="white-space: nowrap;" class="align-left"><strong><spring:message code="submission.report.grandtotal" /> (${deliveryOrder.currency}):</strong></td>
					<td style="white-space: nowrap;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${deliveryOrder.grandTotal}" /></td>
				</tr>
			</table>
		</div>
	</div>
	<jsp:include page="buyerDoAudit.jsp" />
</div>

<!-- Accept Do -->
<div class="modal fade" id="modal-doAccept" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.poAccept.confirm.header" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="idDoForm" method="post" acceptCharset="UTF-8">
				<input type="hidden" name="doId" value="${deliveryOrder.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="buyer.do.accept.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Enter Comments." rows="3" name="buyerRemark" id="buyerRemark" data-validation="required length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="finishBtn" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

<!-- Decline Do -->
<div class="modal fade" id="modal-doDeclined" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.poDecline.confirm" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="idDeclineDo" method="post" acceptCharset="UTF-8">
				<input type="hidden" name="doId" value="${deliveryOrder.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="do.decline.confirm" />
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

	$('#declinedDo').click(function() {
		$('#modal-doDeclined').modal();

	});

	$('#acceptDo').click(function() {
		$('#modal-doAccept').modal();

	});

	$('document').ready(
			function() {
				$('#finishBtn').on(
						'click',
						function(e) {
							console.log('Accepted clicked....');
							e.preventDefault();
							if ($("#idDoForm").isValid()) {
								$(this).addClass('disabled');
								$('#acceptDo').addClass('disabled');
								$('#declinedDo').addClass('disabled');
								$('#idDoForm').attr('action',
										getContextPath() + '/buyer/acceptDo');
								$("#idDoForm").submit();
							} else {
								return;
							}
						});

				$('#idDecline').on(
						'click',
						function(e) {
							console.log('Decline clicked....');
							e.preventDefault();
							if ($("#idDeclineDo").isValid()) {
								$(this).addClass('disabled');
								$('#acceptDo').addClass('disabled');
								$('#declinedDo').addClass('disabled');
								$('#idDeclineDo').attr('action',
										getContextPath() + '/buyer/declineDo');
								$("#idDeclineDo").submit();
							} else {
								return;
							}
						});

			});
</script>


<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">