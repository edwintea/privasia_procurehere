<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/popover.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/xeditable.css"/>">

<div class="upload_download_wrapper clearfix marg-top-10 event_info">
	<h4>Delivery Item</h4>

	<div class="pad_all_15 float-left width-100">
		<div class="main_table_wrapper ph_table_border mega">
			<table class=" ph_table border-none header parentBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
				<thead>
					<tr>
						<th class="width_100 width_100_fix">
							<spring:message code="application.action" />
						</th>
						<th class="width_50 width_50_fix"><spring:message code="supplier.no.col" /></th>
						<th class="align-left width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>
						<th class="align-left width_100_fix"><spring:message code="label.rftbq.th.uom" /></th>
						<th class="align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.quantity" /></th>
						<th class=" align-right width_150 width_150_fix"><spring:message code="label.rftbq.th.unitprice" /> (${deliveryOrder.currency})</th>
						<th class=" align-right width_100 width_100_fix"><spring:message code="item.tax" /> </th>
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
						<th class="width_250 width_250_fix align-right"><spring:message code="prtemplate.total.amount.tax" /> (${deliveryOrder.currency})</th>
					</tr>
				</thead>
			</table>
			<table class="draftTabItems data ph_table border-none childBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
				<tbody>
					<c:forEach items="${doItemlist}" var="item">
						<tr id="${item.id}">
							<td class="width_100 width_100_fix">
								<c:if test="${not empty item.parent.id}">
									<a href="#myModal" onClick="javascript:deleteLink('${item.id}');" id="${item.id}" role="button" data-toggle="modal"><img src="${pageContext.request.contextPath}/resources/images/delete1.png"></a>
								</c:if>
							</td>
							<td class="width_50 width_50_fix">${item.level}.${item.order}
								<c:if test="${not empty item.parent.id}">
									<input type="hidden" name="itemId" id="itemId" value="${item.id}"/>
									<input type="hidden" name="parentId" id="parentId" value="${item.parent.id}"/>
								</c:if>
							</td>
							<td class="align-left width_200_fix">
								<c:if test="${item.order > 0}">
									<a href="#" class="inline" data-type="text" data-title="Item Name">${item.itemName}</a>
									<input type="hidden" data-pos="0" name="itemName" class="validate form-control" value="${item.itemName}"  data-validation="required" data-validation-length="max250"  />
									<input type="hidden" data-pos="0" name="itemDescription" class="validate form-control" value="<c:out value="${item.itemDescription}"/>" data-validation="required" data-validation-length="max2100"  />
									<div>
										<a href="#" class="inlineDescription desc_text" data-type="textarea" data-title="Item Description">${item.itemDescription}</a>
									</div>
								</c:if>
								<c:if test="${item.order == 0}">
									${item.itemName}
								</c:if>
							</td>
							<td class="align-left width_100_fix">${item.unit.uom}</td>
							<td class="align-right width_100 width_100_fix">
								<c:if test="${item.order > 0}">
								<fmt:formatNumber var="quantity" type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${item.quantity}" />
								<input type="text" class="validate form-control itemValue text-right" data-pos="1"  name="quantity" data-validation="custom number required length" data-validation-regexp="^\d{1,16}(\.\d{1,${deliveryOrder.decimal}})?$" data-validation-ignore=",." value="${quantity}"   data-validation-length="1-22"  />
								</c:if>	

							</td>
							<td class=" align-right width_150 width_150_fix">
								<c:if test="${item.order > 0}">
								<fmt:formatNumber var="unitPrice" type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${item.unitPrice}" />
								<input type="text" data-pos="2"  name="unitPrice" class="validate form-control itemValue text-right" value="${unitPrice}"  data-validation="custom number required length" data-validation-regexp="^\d{1,16}(\.\d{1,${deliveryOrder.decimal}})?$" data-validation-ignore=",."  data-validation-length="1-22"  />
								</c:if>	
							</td>
							<td class=" align-right width_100 width_100_fix">
								<c:if test="${item.order > 0}">
									<fmt:formatNumber var="itemTax" type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${item.itemTax}" />
									<input type="text" data-pos="3"  name="itemTax" class="validate form-control itemValue text-right" value="${itemTax}"  data-validation="custom number length" data-validation-optional="true" data-validation-regexp="^\d{1,16}(\.\d{1,${deliveryOrder.decimal}})?$" data-validation-ignore=",."  data-validation-length="1-22"  />
								</c:if>
							</td>
							
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
							<c:if test="${not empty deliveryOrder.field5Label}">
								<td class="align-left width_200 width_200_fix">${item.field5}&nbsp;</td>
							</c:if>
							<c:if test="${not empty deliveryOrder.field6Label}">
								<td class="align-left width_200 width_200_fix">${item.field6}&nbsp;</td>
							</c:if>
							<c:if test="${not empty deliveryOrder.field7Label}">
								<td class="align-left width_200 width_200_fix">${item.field7}&nbsp;</td>
							</c:if>
							<c:if test="${not empty deliveryOrder.field8Label}">
								<td class="align-left width_200 width_200_fix">${item.field8}&nbsp;</td>
							</c:if>
							<c:if test="${not empty deliveryOrder.field9Label}">
								<td class="align-left width_200 width_200_fix">${item.field9}&nbsp;</td>
							</c:if>
							<c:if test="${not empty deliveryOrder.field10Label}">
								<td class="align-left width_200 width_200_fix">${item.field10}&nbsp;</td>
							</c:if>

							<td class="width_150 width_150_fix align-right"><c:if test="${item.order != '0' }">
									<span class="rowTotalAmount"> <fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${item.totalAmount}" /> </span>
								</c:if></td>
							<td class="width_150 align-right width_150_fix"><c:if test="${item.order != '0' }">
									<span class="rowTaxAmount"> <fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${item.taxAmount}" /> </span>
								</c:if></td>
							<td class="width_250 width_250_fix align-right"><c:if test="${item.order != '0' }">
									<span class="rowTotalAfterTaxAmount"> <fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${item.totalAmountWithTax}" /> </span>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<div class="total_all total-with-tax-final table-resposive" style="font-size: 16px;">
			<table cellspacing="3" cellpadding="3" style="width: 100%; border-collapse: separate; border-spacing: 8px; margin-right: 20px;">
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td class="align-left" style="white-space: nowrap;"><spring:message code="prsummary.total2" /> (${deliveryOrder.currency}) :</td>
					<td class="width_250 width_250_fix align-right" style="white-space: nowrap;"><span id="idTotal"><fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${deliveryOrder.total}" /></span></td>
				</tr>
				<tr>
					<td style="white-space: nowrap; vertical-align: top;" class="align-right">&nbsp;</td>
					<td style="width: 100%; padding-left: 10px; padding-right: 10px;">${deliveryOrder.taxDescription}</td>
					<td style="white-space: nowrap; vertical-align: middle;" class="align-left">
						<spring:message code="prtemplate.additional.charges" />:
						(${deliveryOrder.currency}):
					</td>
					<td style="white-space: nowrap; vertical-align: top;" class="width_250 width_250_fix align-right">
						<fmt:formatNumber var="additionalTax" type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${deliveryOrder.additionalTax}" />
						<input type="text" data-pos="6" style="font-size:16px;font-family:inherit;padding-right:0px;color:#7f7f7f;"  id="additionalTax" name="additionalTax" class="validate form-control text-right" value="${additionalTax}"   data-validation-length="max22"  />
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td style="white-space: nowrap;" class="align-left">
						<strong><spring:message code="submission.report.grandtotal" /> (${deliveryOrder.currency}):</strong>
					</td>
					<td style="white-space: nowrap;" class="width_250 width_250_fix align-right"><strong><span id="idGrandTotal"><fmt:formatNumber type="number" minFractionDigits="${deliveryOrder.decimal}" maxFractionDigits="${deliveryOrder.decimal}" value="${deliveryOrder.grandTotal}" /></span></strong></td>
				</tr>
			</table>
		</div>
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
	var decimalLimit = ${deliveryOrder.decimal};
</script>	
<script type="text/javascript" src="<c:url value="/resources/js/view/dobq.js?1"/>"></script>
