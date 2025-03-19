<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel-body pad0">
	<div class="example-box-wrapper tab_panel_wrapper mega" style="max-height: 500px !important;">
		<table class="ph_table border-none header" width="100%" border="0" cellspacing="0" cellpadding="0">
			<form:hidden path="bq.id" />
			<form:hidden path="id" />
			<form:hidden path="supplier.id" />
			<input type="hidden" name="supplierId" id="supplierId" value="${supplierId}" />
			<%-- <input type="hidden" value="${rfaSupplierBq.bq.id}" id="eventBqId" name="eventBqId"> --%>
			<input type="hidden" value="${event.id}" id="rfteventId" name="rfteventId">
			<thead>
				<tr>
					<th class="width_50 width_50_fix align-left"><spring:message code="rfaevent.no.col" /></th>

					<th class="width_300 width_300_fix align-left text-bold">
						<spring:message code="label.rftbq.th.itemname" />
					</th>
					<th class="width_100 width_100_fix align-left text-bold">
						<spring:message code="rfaevent.units.label" />
					</th>
					<th class="width_200 width_200_fix align-right text-bold">
						<spring:message code="rfaevent.qty.label" />
					</th>
					<th class="width_200 width_200_fix align-center text-bold">
						<spring:message code="rfaevent.unitprice.label" />
					</th>
					<th class="width_200 width_200_fix align-center text-bold">
						<spring:message code="prtemplate.total.amount" />
					</th>
					<c:if test="${auctionRules.itemizedBiddingWithTax or auctionRules.lumsumBiddingWithTax}">
						<th class="width_300 width_300_fix align-left" style="font-weight: bold; padding-left: 6rem;">
							<spring:message code="product.list.tax" />
						</th>
					</c:if>
					<c:if test="${!empty rfaSupplierBq.field1Label}">
						<th class="width_200 width_200_fix align-center text-bold">${rfaSupplierBq.field1Label}</th>
					</c:if>
					<c:if test="${!empty rfaSupplierBq.field2Label}">
						<th class="width_200 width_200_fix align-center text-bold">${rfaSupplierBq.field2Label}</th>
					</c:if>
					<c:if test="${!empty rfaSupplierBq.field3Label}">
						<th class="width_200 width_200_fix align-center text-bold">${rfaSupplierBq.field3Label}</th>
					</c:if>
					<c:if test="${!empty rfaSupplierBq.field4Label}">
						<th class="width_200 width_200_fix align-center text-bold">${rfaSupplierBq.field4Label}</th>
					</c:if>
					<c:if test="${!empty rfaSupplierBq.field5Label}">
						<th class="width_200 width_200_fix align-center text-bold">${rfaSupplierBq.field5Label}</th>
					</c:if>
					<c:if test="${!empty rfaSupplierBq.field6Label}">
						<th class="width_200 width_200_fix align-center text-bold">${rfaSupplierBq.field6Label}</th>
					</c:if>
					<c:if test="${!empty rfaSupplierBq.field7Label}">
						<th class="width_200 width_200_fix align-center text-bold">${rfaSupplierBq.field7Label}</th>
					</c:if>
					<c:if test="${!empty rfaSupplierBq.field8Label}">
						<th class="width_200 width_200_fix align-center text-bold">${rfaSupplierBq.field8Label}</th>
					</c:if>
					<c:if test="${!empty rfaSupplierBq.field9Label}">
						<th class="width_200 width_200_fix align-center text-bold">${rfaSupplierBq.field9Label}</th>
					</c:if>
					<c:if test="${!empty rfaSupplierBq.field10Label}">
						<th class="width_200 width_200_fix align-center text-bold">${rfaSupplierBq.field10Label}</th>
					</c:if>
					<c:if test="${auctionRules.itemizedBiddingWithTax or auctionRules.lumsumBiddingWithTax}">
						<th class="width_200 width_200_fix align-center" style="font-weight: bold">
							<spring:message code="rfaevent.total.amt.withtax" />
						</th>
					</c:if>
				</tr>
			</thead>
		</table>
		<table class=" deta ph_table table bq-table" width="100%" border="0" cellspacing="0" cellpadding="0">
			<tbody>
				<c:forEach items="${rfaSupplierBq.supplierBqItems}" var="supplierBqItem" varStatus="vs">
					<c:if test="${empty supplierBqItem.quantity}">
						<tr class="odd gradeX parent_box" data-item="${supplierBqItem.id}">
							<form:hidden path="supplierBqItems[${vs.index}].id" />
							<td class="width_50 width_50_fix align-left">
								<span>${supplierBqItem.level}.${supplierBqItem.order}&nbsp;</span>
							</td>
							<td class="width_300 width_300_fix align-left">
								<p>
									<strong>${supplierBqItem.itemName}</strong>
								</p>
								<c:if test="${not empty supplierBqItem.itemDescription}">
									<span class="item_detail s1_view_desc"> <spring:message code="application.view.description" />
									</span>
									<p class="s1_tent_tb_description s1_text_small">${supplierBqItem.itemDescription}</p>
								</c:if>
							</td>
							<td class="width_100 width_100_fix align-left"></td>
							<td class="width_200 width_200_fix align-right"></td>
							<td class="width_200 width_200_fix align-right"></td>
							<td class="width_200 width_200_fix align-center">
								<span>&nbsp;</span>
							</td>
							<c:if test="${auctionRules.itemizedBiddingWithTax or auctionRules.lumsumBiddingWithTax}">
								<td class="width_300 width_300_fix align-left"></td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field1Label}">
								<td class="width_200 width_200_fix align-center"></td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field2Label}">
								<td class="width_200 width_200_fix align-center"></td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field3Label}">
								<td class="width_200 width_200_fix align-center"></td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field4Label}">
								<td class="width_200 width_200_fix align-left"></td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field5Label}">
								<td class="width_200 width_200_fix align-left"></td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field6Label}">
								<td class="width_200 width_200_fix align-left"></td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field7Label}">
								<td class="width_200 width_200_fix align-left"></td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field8Label}">
								<td class="width_200 width_200_fix align-left"></td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field9Label}">
								<td class="width_200 width_200_fix align-left"></td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field10Label}">
								<td class="width_200 width_200_fix align-left"></td>
							</c:if>
							<c:if test="${auctionRules.itemizedBiddingWithTax or auctionRules.lumsumBiddingWithTax}">
								<td class="width_200 width_200_fix align-center"></td>
							</c:if>
						</tr>
					</c:if>
					<c:if test="${!empty supplierBqItem.quantity}">
						<tr class="odd sub_item gradeX sub-child" data-item="${supplierBqItem.id}">
							<input type="hidden" name="totalAmountWithTax" id="" value="${supplierBqItem.totalAmountWithTax}" />
							<input type="hidden" name="itemName" id="" value="${supplierBqItem.itemName}" />
							<input type="hidden" name="quantity" id="" value="${supplierBqItem.quantity}" />
							<input type="hidden" name="priceType" id="" value="${supplierBqItem.priceType}" />



							<%-- <form:hidden path="supplierBqItems[${vs.index}].id" /> --%>
							<form:hidden path="supplierBqItems[${vs.index}].id" />
							<form:hidden path="supplierBqItems[${vs.index}].unitPrice" />
							<form:hidden path="supplierBqItems[${vs.index}].totalAmount" />
							<form:hidden path="supplierBqItems[${vs.index}].tax" />
							<form:hidden path="supplierBqItems[${vs.index}].taxType" />
							<form:hidden path="supplierBqItems[${vs.index}].field1" />
							<form:hidden path="supplierBqItems[${vs.index}].field2" />
							<form:hidden path="supplierBqItems[${vs.index}].field3" />
							<form:hidden path="supplierBqItems[${vs.index}].field4" />
							<form:hidden path="supplierBqItems[${vs.index}].field5" />
							<form:hidden path="supplierBqItems[${vs.index}].field6" />
							<form:hidden path="supplierBqItems[${vs.index}].field7" />
							<form:hidden path="supplierBqItems[${vs.index}].field8" />
							<form:hidden path="supplierBqItems[${vs.index}].field9" />
							<form:hidden path="supplierBqItems[${vs.index}].field10" />
							<form:hidden path="supplierBqItems[${vs.index}].totalAmountWithTax" />
							<form:hidden path="supplierBqItems[${vs.index}].bqItemId"  value="${supplierBqItem.bqItem.id}"/>
							
							
							<td class="width_50 width_50_fix align-left">
								<span>${supplierBqItem.level}.${supplierBqItem.order}&nbsp;</span>
							</td>
							<td class="width_300 width_300_fix align-left">
								<c:if test="${supplierBqItem.priceType == 'BUYER_FIXED_PRICE'}">
									<span class="bs-label label-success" style="color: #fff"><spring:message code="eventsummary.bq.buyer.fixedprice" /></span>&nbsp;
							</c:if>
								<c:if test="${supplierBqItem.priceType == 'TRADE_IN_PRICE'}">
									<span class="bs-label label-info" style="color: #fff"><spring:message code="eventsummary.bq.trade.price" /></span>&nbsp;</c:if>
								<p>${supplierBqItem.itemName}</p>
								<c:if test="${not empty supplierBqItem.itemDescription}">
									<span class="item_detail s1_view_desc"><spring:message code="application.view.description" /> </span>
									<p class="s1_tent_tb_description s1_text_small">${supplierBqItem.itemDescription}</p>
								</c:if>
							</td>
							<td class="width_100 width_100_fix align-left text-black">${supplierBqItem.uom.uom}</td>
							<td class="width_200 width_200_fix align-right text-black">
								<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${supplierBqItem.quantity}" />
							</td>
							<td class="width_200 width_200_fix align-right text-black">
								<fmt:formatNumber var="unitPriceFormated" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${supplierBqItem.unitPrice}" />
								<input type="text" class="form-control tab_input text-right validate" data-pos="1" name="unitPrice" value="${unitPriceFormated}" data-validation="required" data-validation-allowing="float" id="unitPrice">
							</td>
							<td class="width_200 width_200_fix align-right text-black">
								<fmt:formatNumber var="totalAmountFormated" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${supplierBqItem.totalAmount != undefined ? supplierBqItem.totalAmount : ''}" />
								<input data-pos="2" class="validate form-control text-right" data-pos="2" type="text" name="totalAmount" ${event.disableTotalAmount ? 'readonly' : ''} data-validation="required length" data-validation-allowing="float" data-validation-length="max22" value="${totalAmountFormated}" placeholder="${child.totalAmount}" id="totalAmount" />
							</td>
							<c:if test="${auctionRules.itemizedBiddingWithTax or auctionRules.lumsumBiddingWithTax}">
								<td class="width_300 width_300_fix align-left">
									<select name="taxType" id="custom_${supplierBqItem.id}" class="chosen-select disablesearch" data-pos="4">
										<c:forEach items="${taxTypeList}" var="taxType">
											<option value="${taxType}" ${(not empty supplierBqItem and  supplierBqItem.taxType eq taxType) ?'selected':'' }>${taxType}</option>
										</c:forEach>
									</select>
									<fmt:formatNumber var="taxFormated" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${supplierBqItem.tax != undefined ? supplierBqItem.tax : ''}" />
									<input data-pos="3" type="text" data-pos="3" name="tax" class="validate form-control width_30per text-right" value="${taxFormated}" id="tax">
								</td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field1Label}">
								<td class="width_200 width_200_fix align-center">
									<input type="text" data-pos="5" name="field1" ${(rfaSupplierBq.field1FilledBy != 'BOTH' && rfaSupplierBq.field1FilledBy == 'SUPPLIER' ) ? 'readonly' : ''} class="form-control" data-validation-optional="true" data-validation="length" data-validation-length="max100" value="${supplierBqItem.field1}" id="field1">
								</td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field2Label}">
								<td class="width_200 width_200_fix align-center">
									<input type="text" data-pos="6" name="field2" ${(rfaSupplierBq.field2FilledBy != 'BOTH' && rfaSupplierBq.field2FilledBy == 'SUPPLIER' ) ? 'readonly' : ''} class="form-control" data-validation-optional="true" data-validation="length" data-validation-length="max100" value="${supplierBqItem.field2}" id="field2">
								</td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field3Label}">
								<td class="width_200 width_200_fix align-center">
									<input type="text" data-pos="7" name="field3" ${(rfaSupplierBq.field3FilledBy != 'BOTH' && rfaSupplierBq.field3FilledBy == 'SUPPLIER' ) ? 'readonly' : ''} class="form-control" data-validation-optional="true" data-validation="length" data-validation-length="max100" value="${supplierBqItem.field3}" id="field3">
								</td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field4Label}">
								<td class="width_200 width_200_fix align-center">
									<input type="text" data-pos="8" name="field4" ${(rfaSupplierBq.field4FilledBy != 'BOTH' && rfaSupplierBq.field4FilledBy == 'SUPPLIER' ) ? 'readonly' : ''} class="form-control" data-validation-optional="true" data-validation="length" data-validation-length="max100" value="${supplierBqItem.field4}" id="field4">
								</td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field5Label}">
								<td class="width_200 width_200_fix align-center">
									<input type="text" data-pos="9" name="field5" ${(rfaSupplierBq.field5FilledBy != 'BOTH' && rfaSupplierBq.field5FilledBy == 'SUPPLIER' ) ? 'readonly' : ''} class="form-control" data-validation-optional="true" data-validation="length" data-validation-length="max100" value="${supplierBqItem.field5}" id="field5">
								</td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field6Label}">
								<td class="width_200 width_200_fix align-center">
									<input type="text" data-pos="10" name="field6" ${(rfaSupplierBq.field6FilledBy != 'BOTH' && rfaSupplierBq.field6FilledBy == 'SUPPLIER' ) ? 'readonly' : ''} class="form-control" data-validation-optional="true" data-validation="length" data-validation-length="max100" value="${supplierBqItem.field6}" id="field6">
								</td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field7Label}">
								<td class="width_200 width_200_fix align-center">
									<input type="text" data-pos="11" name="field7" ${(rfaSupplierBq.field7FilledBy != 'BOTH' && rfaSupplierBq.field7FilledBy == 'SUPPLIER' ) ? 'readonly' : ''} class="form-control" data-validation-optional="true" data-validation="length" data-validation-length="max100" value="${supplierBqItem.field7}" id="field7">
								</td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field8Label}">
								<td class="width_200 width_200_fix align-center">
									<input type="text" data-pos="12" name="field8" ${(rfaSupplierBq.field8FilledBy != 'BOTH' && rfaSupplierBq.field8FilledBy == 'SUPPLIER' ) ? 'readonly' : ''} class="form-control" data-validation-optional="true" data-validation="length" data-validation-length="max100" value="${supplierBqItem.field8}" id="field8">
								</td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field9Label}">
								<td class="width_200 width_200_fix align-center">
									<input type="text" data-pos="13" name="field9" ${(rfaSupplierBq.field9FilledBy != 'BOTH' && rfaSupplierBq.field9FilledBy == 'SUPPLIER' ) ? 'readonly' : ''} class="form-control" data-validation-optional="true" data-validation="length" data-validation-length="max100" value="${supplierBqItem.field9}" id="field9">
								</td>
							</c:if>
							<c:if test="${!empty rfaSupplierBq.field10Label}">
								<td class="width_200 width_200_fix align-center">
									<input type="text" data-pos="14" name="field10" ${(rfaSupplierBq.field10FilledBy != 'BOTH' && rfaSupplierBq.field1FilledBy == 'SUPPLIER' ) ? 'readonly' : ''} class="form-control" data-validation-optional="true" data-validation="length" data-validation-length="max100" value="${supplierBqItem.field10}" id="field10">
								</td>
							</c:if>
							<c:if test="${auctionRules.itemizedBiddingWithTax or auctionRules.lumsumBiddingWithTax}">
								<td class="width_200 width_200_fix align-center text-black">
									<span class="totalAmountWithTax"> <fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${supplierBqItem.totalAmountWithTax}" />
									</span>
								</td>
							</c:if>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="total-with-tax pad_all_15 marg-top-10 marg-bottom-10">
		<div class="total-with-tax-inner">
			<label class="ml-30"><spring:message code="submission.report.grandtotal" /> : </label> <input type="hidden" name="grandTotalOfBq" id="idGrandTotalOfBq" value="${rfaSupplierBq.grandTotal}">
			<form:hidden path="grandTotal" />
			<label>&nbsp;${event.baseCurrency.currencyCode}&nbsp;</label>
			<p class="text-black" id="grandTotalOfBq">
				<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${rfaSupplierBq.grandTotal}" />
			</p>
		</div>
	</div>
	<c:if test="${auctionRules.itemizedBiddingWithTax or auctionRules.lumsumBiddingWithTax}">
		<div class="additinol-text pad_all_15 marg-top-20 marg-bottom-20">
			<div class="add_tex">
				<label>&nbsp;</label>
				<%-- 				<form:hidden path="additionalTax" />
					<input type="text" name="additionalTax" id="additionalTax" class="validateregex form-control" data-validation-regexp="^(?!\\.?$)\\d{0,10}(\\.\\d{0,6})?$" value="${rfaSupplierBq.additionalTax}" placeholder="Additional Tax Amount">
 --%>
				<fmt:formatNumber var="additionalTaxFormated" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${rfaSupplierBq.additionalTax}" />
				<spring:message code="rfaevent.additional.tax.placeholder" var="addtax" />
				<form:input path="additionalTax" cssClass="validateregex form-control text-right" data-validation-regexp="^(?!\\.?$)\\d{0,10}(\\.\\d{0,6})?$" value="${additionalTaxFormated}" placeholder="${addtax}" id="additionalTax" />
			</div>
			<div class="Description_lower">
				<label><spring:message code="rfaevent.additional.tax.case1" /></label>
				<%-- 				<form:hidden path="taxDescription" />
					<input type="hidden" name="additionalTaxDescription" id="idAdditionalTaxDescription" value="${rfaSupplierBq.taxDescription}">
					<textarea class="form-control" name="additionalTaxDescription">${rfaSupplierBq.taxDescription}</textarea>
 --%>
				<form:textarea path="taxDescription" cssClass="form-control" id="taxDesc" />
			</div>
		</div>
		<div class="total-with-tax-final pad_all_15 marg-top-10 marg-bottom-10">
			<div class="total-with-tax-inner">
				<label class="ml-30"><spring:message code="rfaevent.total.after.tax" /> : </label>
				<form:hidden path="totalAfterTax" />
				<input type="hidden" name="amountAfterTax" id="idAmountAfterTax" value="${rfaSupplierBq.totalAfterTax}"> <label class="color-red">&nbsp;${event.baseCurrency.currencyCode}&nbsp;</label>
				<p class="color-red" id="amountAfterTax">
					<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${rfaSupplierBq.totalAfterTax}" />
				</p>
			</div>
		</div>
	</c:if>
</div>
