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
							<c:if test="${goodsReceiptNote.status =='DRAFT'}">
								<th class="width_100 width_100_fix">
									<spring:message code="application.action" />
								</th>	
							</c:if>	
							<th class="width_50 width_50_fix"><spring:message code="supplier.no.col" /></th>
							<th class="align-left width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>
							<th class="align-left width_100_fix"><spring:message code="label.rftbq.th.uom" /></th>
							<th class="align-right width_100 width_100_fix"><spring:message code="grnitems.po.quantity" /></th>
							<th class="align-right width_100 width_100_fix"><spring:message code="grnitems.received.quantity" /></th>
							<th class=" align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.unitprice" /> (${goodsReceiptNote.currency})</th>
							<th class=" align-right width_100 width_100_fix"><spring:message code="item.tax" /> </th>
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
							<tr id="${item.id}">
								<c:if test="${goodsReceiptNote.status =='DRAFT'}">
									<td class="width_100 width_100_fix">
										<input type="hidden" name="previousReceivedQuantity" id="previousReceivedQuantity" value="${item.previousReceivedQuantity}"/>
										<p id="previousReceivedQuantity" style="display: none;">${item.previousReceivedQuantity}</p>
										<c:if test="${not empty item.parent.id}">
											<a href="#myModal" onClick="javascript:deleteLink('${item.id}');" id="${item.id}" role="button" data-toggle="modal"><img src="${pageContext.request.contextPath}/resources/images/delete1.png"></a>
										</c:if>
									</td>	
								</c:if>	
								<td class="width_50 width_50_fix">${item.level}.${item.order}
								<c:if test="${not empty item.parent.id}">
									<input type="hidden" name="itemId" id="itemId" value="${item.id}"/>
									<input type="hidden" name="parentId" id="parentId" value="${item.parent.id}"/>
								</c:if>
								</td>
								<td class="align-left width_200_fix">${item.itemName}<c:if test="${not empty item.itemDescription}">
										<span class="item_detail s1_view_desc"> <spring:message code="application.view.description" /> </span>
									</c:if>
									<p class="s1_tent_tb_description s1_text_small">${item.itemDescription}</p>
								</td>
								<td class="align-left width_100_fix">${item.unit.uom}</td>
								<td class="align-right width_100 width_100_fix">
									<fmt:formatNumber var="quantity" type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.quantity}" />
									<p id="idquantity">${quantity}</p>
								</td>
								<c:if test="${goodsReceiptNote.status !='DRAFT'}">
									<td class="align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.receivedQuantity}" /></td>
								</c:if>
								<c:if test="${goodsReceiptNote.status =='DRAFT'}">
									<td class="align-right width_100 width_100_fix">
										<c:if test="${item.order > 0}">
										<fmt:formatNumber var="receivedQuantity" type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.receivedQuantity}" />
										<input type="text" class="validate form-control itemValue text-right" data-pos="1"  name="receivedQuantity" data-validation="required custom number length" data-validation-regexp="^\d{1,16}(\.\d{1,${goodsReceiptNote.decimal}})?$" data-validation-ignore=",." value="${receivedQuantity}"   data-validation-length="1-22"  />
										</c:if>	
									</td>
								</c:if>
								<td class="align-right width_100 width_100_fix">
									<c:if test="${item.order > 0}">
										<fmt:formatNumber var="unitPrice" type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.unitPrice}" />
										<p id="idunitprice">${unitPrice}</p>
									</c:if>	
								</td>
								<td class="align-right width_100 width_100_fix">
									<c:if test="${item.order > 0}">
										<fmt:formatNumber var="itemTax" type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.itemTax}" />
										<p  id="iditemTax">${itemTax}</p>
									</c:if>
								</td>
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
											<span class="rowTotalAmount"><fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.totalAmount}" /></span>
									</c:if></td>
								<td class="width_150 align-right width_150_fix"><c:if test="${item.order != '0' }">
											<span class="rowTaxAmount"><fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.taxAmount}" /></span>
									</c:if></td>
								<td class="width_200 width_200_fix align-right"><c:if test="${item.order != '0' }">
										<span class="rowTotalAfterTaxAmount"><fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${item.totalAmountWithTax}" /></span>
									</c:if></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>

		<div class="total_all total-with-tax-final table-resposive" style="font-size: 16px;">
			<table cellspacing="1" cellpadding="1" style="width: 98.8%; border-collapse: separate; border-spacing: 8px; margin-right: 20px;">
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td style="white-space: nowrap; text-align: right ;" ><strong><spring:message code="submission.report.grandtotal" /> (${goodsReceiptNote.currency}):</strong></td>
					<td style="white-space: nowrap;" class="align-right"><span id="idGrandTotal"><fmt:formatNumber type="number" minFractionDigits="${goodsReceiptNote.decimal}" maxFractionDigits="${goodsReceiptNote.decimal}" value="${goodsReceiptNote.grandTotal}" /></span></td>
				</tr>
			</table>
		</div>
	</div>
	
<div class="modal fade" id="myModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate"  type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="do.item.delete" />
				</label>
			</div>

			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="deleteItem" class="deleteItem btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left delItem" value="Yes" data-dismiss="modal" onClick="deleteItem(this.id)">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" >
						<spring:message code="application.no2" />
					</button>
			</div>
			
		</div>
	</div>
</div>
<script type="text/javascript">
	var decimalLimit = ${goodsReceiptNote.decimal};
</script>	
<script type="text/javascript" src="<c:url value="/resources/js/view/grnBqItems.js"/>"></script>
	