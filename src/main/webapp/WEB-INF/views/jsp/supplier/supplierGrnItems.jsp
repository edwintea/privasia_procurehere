<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>Goods Receipt Note Item</h4>

		<div class="pad_all_15 float-left width-100">
			<div class="main_table_wrapper ph_table_border mega">
				<table class="ph_table border-none header parentBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<thead>
						<tr>
							<th class="width_50 width_50_fix"><spring:message code="supplier.no.col" /></th>
							<th class="align-left width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>
							<th class="align-left width_100_fix"><spring:message code="label.rftbq.th.uom" /></th>
							<th class="align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.quantity" /></th>
							<th class=" align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.unitprice" /> (${goodsReceiptNote.currency})</th>
							<c:if test="${not empty goodsReceiptNote.field1Label}">
								<th class="align-left width_100 width_100_fix">${goodsReceiptNote.field1Label}</th>
							</c:if>
							<c:if test="${not empty goodsReceiptNote.field2Label}">
								<th class="align-left width_100 width_100_fix">${goodsReceiptNote.field2Label}</th>
							</c:if>
							<c:if test="${not empty goodsReceiptNote.field3Label}">
								<th class="align-left width_100 width_100_fix">${goodsReceiptNote.field3Label}</th>
							</c:if>
							<c:if test="${not empty goodsReceiptNote.field4Label}">
								<th class="align-left width_100 width_100_fix">${goodsReceiptNote.field4Label}</th>
							</c:if>
							<th class="width_150 width_150_fix align-right"><spring:message code="prtemplate.total.amount" /> (${goodsReceiptNote.currency})</th>
							<th class="width_150 align-right width_150_fix"><spring:message code="prtemplate.tax.amount" /> (${goodsReceiptNote.currency})</th>
							<th class="width_200 width_200_fix align-right"><spring:message code="prtemplate.total.amount.tax" /> (${goodsReceiptNote.currency})</th>
						</tr>
					</thead>
				</table>
				<table class="data ph_table border-none childBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<tbody>
						<c:forEach items="${grnItemlist}" var="item">
							<tr>
								<td class="width_50 width_50_fix">${item.level}.${item.order}
								</td>
								<td class="align-left width_200_fix">${item.itemName}<c:if test="${not empty item.itemDescription}">
										<span class="item_detail s1_view_desc"> <spring:message code="application.view.description" /> </span>
									</c:if>
									<p class="s1_tent_tb_description s1_text_small">${item.itemDescription}</p>
								</td>
								<td class="align-left width_100_fix">${item.unit.uom}</td>
								<td class="align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.quantity}" /></td>
								<td class=" align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.unitPrice}" /></td>
								<c:if test="${not empty goodsReceiptNote.field1Label}">
									<td class=" align-left width_100 width_100_fix">${item.field1}&nbsp;</td>
								</c:if>
								<c:if test="${not empty goodsReceiptNote.field2Label}">
									<td class="align-left width_200 approvalUsers width_200_fix">${item.field2}&nbsp;</td>
								</c:if>
								<c:if test="${not empty goodsReceiptNote.field3Label}">
									<td class="align-left width_200 width_200_fix">${item.field3}&nbsp;</td>
								</c:if>
								<c:if test="${not empty goodsReceiptNote.field4Label}">
									<td class="align-left width_200 width_200_fix">${item.field4}&nbsp;</td>
								</c:if>
								<td class="width_150 width_150_fix align-right"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.totalAmount}" />
									</c:if></td>
								<td class="width_150 align-right width_150_fix"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.taxAmount}" />
									</c:if></td>
								<td class="width_200 width_200_fix align-right"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.totalAmountWithTax}" />
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
					<td class="align-left" style="white-space: nowrap;"><strong><spring:message code="prsummary.total2" /> (${goodsReceiptNote.currency}) :</strong></td>
					<td style="white-space: nowrap;"><fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${goodsReceiptNote.total}" /></td>
				</tr>
				<tr>
					<td style="white-space: nowrap; vertical-align: top;"><strong><spring:message code="prtemplate.additional.charges" />:</strong></td>
					<td style="width: 100%; padding-left: 10px; padding-right: 10px;">${goodsReceiptNote.taxDescription}</td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-left"><strong>(${goodsReceiptNote.currency}):</strong></td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${goodsReceiptNote.additionalTax}" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td style="white-space: nowrap;" class="align-left"><strong><spring:message code="submission.report.grandtotal" /> (${goodsReceiptNote.currency}):</strong></td>
					<td style="white-space: nowrap;" class="align-right"><fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${goodsReceiptNote.grandTotal}" /></td>
				</tr>
			</table>
		</div>
	</div>
	