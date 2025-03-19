<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:if test="${! empty bqList}">
	<div class="panel sum-accord">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" class="accordion" href="#collapseBq"> <spring:message code="bq.label" />
				</a>
			</h4>
		</div>
		<div id="collapseBq" class="panel-collapse collapse">
			<div class="panel-body pad_all_15">
				<c:forEach items="${bqList}" var="suppBq">
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>${suppBq.name}</h3>
						</div>
						<div class="import-supplier-inner-first-new global-list form-middle">
							<div class="ph_tabel_wrapper">
								<div class="main_table_wrapper ph_table_border mega" style="margin: 0;">
									<table class="ph_table border-none header" width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
										<thead>
											<tr>
												<th class="for-left width_50 width_50_fix"><spring:message code="application.no2" /></th>
												<th class="for-left width_100 width_100_fix"><spring:message code="label.rftbq.th.itemname" /></th>
												<th class="for-left width_100 width_100_fix"><spring:message code="label.rftbq.th.quantity.only" /></th>
												<th class="for-left width_100 width_100_fix"><spring:message code="label.uom" /></th>
												<th class=" align-center width_100 width_100_fix"><spring:message code="product.unit.price" /></th>
												<th class=" align-center width_100 width_100_fix"><spring:message code="prtemplate.total.amount" /></th>
												<th class=" align-center width_100 width_100_fix"><spring:message code="rfs.bqsummary.case.tax" /></th>
												<th class=" align-center width_100 width_100_fix"><spring:message code="rfs.reqsummary.total.amttax" /></th>
												<c:if test="${not empty suppBq.field1Label and suppBq.field1ToShowSupplier}">
													<th class=" align-center width_100 width_100_fix">${suppBq.field1Label}</th>
												</c:if>
												<c:if test="${not empty suppBq.field2Label and suppBq.field2ToShowSupplier}">
													<th class=" align-center width_100 width_100_fix">${suppBq.field2Label}</th>
												</c:if>
												<c:if test="${not empty suppBq.field3Label and suppBq.field3ToShowSupplier}">
													<th class=" align-center width_100 width_100_fix">${suppBq.field3Label}</th>
												</c:if>
												<c:if test="${not empty suppBq.field4Label and suppBq.field4ToShowSupplier}">
													<th class=" align-center width_100 width_100_fix">${suppBq.field4Label}</th>
												</c:if>
											</tr>
										</thead>
									</table>

									<table class="ph_table data border-none" width="100%" border="0" cellspacing="0" cellpadding="0">
										<tbody>
											<c:forEach items="${suppBq.supplierBqItems}" var="item">
												<tr>
													<td class="for-left width_50 width_50_fix">
														<c:if test="${item.order== 0}"><h5>${item.level}.${item.order}</h5></c:if>
														<c:if test="${item.order > 0}">${item.level}.${item.order} </c:if>
													</td>
													<td class="for-left width_100 width_100_fix" align="center">
													<span class="item_name">
														<c:if test="${item.priceType == 'TRADE_IN_PRICE'}">
																<span class="bs-label label-info"><spring:message code="eventsummary.bq.trade.price" /></span>&nbsp;
														</c:if> 
														<c:if test="${item.priceType == 'BUYER_FIXED_PRICE'}">
																<span class="bs-label label-success"><spring:message code="eventsummary.bq.buyer.fixedprice" /></span>&nbsp;
														</c:if> 
														<c:if test="${item.order == 0}"><h5>${item.itemName}</h5> </c:if>
														<c:if test="${item.order > 0}">${item.itemName} </c:if>	     
														</span> 
														<c:if test="${not empty item.itemDescription}">
															<span data-toggle="collapse" data-target="#demo-${item.id}" class="s2_view_desc"><spring:message code="application.view.description" /></span>
														</c:if></td>
														
													<td class="for-left width_100 width_100_fix" align="center">
													<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${item.quantity}" />
												    </td>
													<td class="for-left width_100 width_100_fix" align="center">${item.uom.uom}</td>
													<td class=" align-center width_100 width_100_fix"><c:if test="${item.order != '0' }">
															<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${item.unitPrice}" />
													</c:if>
													</td>
													<c:if test="${item.priceType == 'BUYER_FIXED_PRICE'}">
														<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${item.unitPrice}" />
													</c:if>
													&nbsp;&nbsp;
													</td>
													<td class="align-center width_100 width_100_fix"><c:if test="${item.order != '0'}">
															<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${item.totalAmount}" />
													</c:if>
													</td>
													<td class="align-center width_100 width_100_fix" align="center"><c:if test="${item.order != '0'}"><fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${item.tax}" /></c:if></td>
													<td class="align-center width_100 width_100_fix" align="center"><c:if test="${item.order != '0'}"><fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${item.totalAmountWithTax}" /></c:if></td>
													<c:if test="${not empty suppBq.field1Label and suppBq.field1ToShowSupplier}">
														<td class=" align-center width_100 width_100_fix">${item.field1}&nbsp;</td>
													</c:if>
													<c:if test="${not empty suppBq.field2Label and suppBq.field2ToShowSupplier}">
														<td class=" align-center width_100 width_100_fix">${item.field2}&nbsp;</td>
													</c:if>
													<c:if test="${not empty suppBq.field3Label and suppBq.field3ToShowSupplier}">
														<td class=" align-center width_100 width_100_fix">${item.field3}&nbsp;</td>
													</c:if>
													<c:if test="${not empty suppBq.field4Label and suppBq.field4ToShowSupplier}">
														<td class=" align-center width_100 width_100_fix">${item.field4}&nbsp;</td>
													</c:if>
												</tr>
												<tr id="#demo-${item.id}" style="display: none;" class="collapse">
													<td style="border-top: 0 !important;"></td>
													<td colspan="${suppBq.headerCount}" style="border-top: 0 !important;"><span class="item_detail s2_text_small">${item.itemDescription}</span></td>

												</tr>

											</c:forEach>
										</tbody>
									</table>
									<c:if test="${!(auctionRules.lumsumBiddingWithTax == false || auctionRules.itemizedBiddingWithTax == false) && !(not empty event.erpEnable?event.erpEnable:false)}">
										<div class="total-with-tax pad_all_12 marg-top-10">
											<div class="total-with-tax-inner">
												<label><spring:message code="submission.report.grandtotal" /> (${event.currencyCode}):</label>
												<p class="color-black" id="amountAfterTax">
													<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${suppBq.grandTotal}" />
												</p>
											</div>
										</div>
										<div class="total-with-tax pad_all_12 ">
											<div class="total-with-tax-inner">
												<label><spring:message code="submission.report.additional.tax" /> :</label>
												<p class="color-black" id="additionalTax">
													<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${suppBq.additionalTax != null ? suppBq.additionalTax : '0.00'}" />
												</p>
											</div>
										</div>
									</c:if>
									<c:if test="${(auctionRules.lumsumBiddingWithTax == false || auctionRules.itemizedBiddingWithTax == false)}">
										<div class="total-with-tax pad_all_12 marg-bottom-10">
											<div class="total-with-tax-inner">
												<label><spring:message code="submission.report.grandtotal" /> (${event.currencyCode}):</label>
												<p class="color-black" id="totalafterTax">
													<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${suppBq.totalAfterTax != null ? suppBq.totalAfterTax : '0.00' }" />
												</p>
											</div>
										</div>
									</c:if>
									<c:if test="${!(auctionRules.lumsumBiddingWithTax == false || auctionRules.itemizedBiddingWithTax == false)}">
										<div class="total-with-tax pad_all_12 marg-bottom-10">
											<div class="total-with-tax-inner">
												<label><spring:message code="rfaevent.total.after.tax" /> (${event.currencyCode}): </label>
												<p class="color-black" id="totalafterTax">
													<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${suppBq.totalAfterTax != null ? suppBq.totalAfterTax : '0.00' }" />
												</p>
											</div>
										</div>
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
</c:if>
<style>
.ph_table tr:first-child {
	
}

.ph_table.data td:first-child {
	padding: 30px 10px 15px 10px;
}

.ph_table.data td:nth-child(2) {
	padding: 30px 10px 15px 10px;
}

.s2_text_small {
	margin: 5px 0 0 0;
	font-size: 11px;
	display: none;
	max-height: 150px;
	text-align: left;
	margin-top: -1%;
}

.ph_table.header th {
	padding: 17px 10px;
	font-family: 'Open Sans', sans-serif;
	font-weight: 600;
}

.pad_all_12 {
	padding: 12px;
}
</style>
