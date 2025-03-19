<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel sum-accord">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseEight"> <spring:message code="eventdescription.billofquantity.label" /> </a>
		</h4>
	</div>
	<div id="collapseEight" class="panel-collapse collapse">
		<div class="panel-body pad_all_15">
			<c:forEach items="${bqList}" var="bq">
				<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
					<div>
						<h3 class="bqname">&nbsp ${bq.name}</h3>
					</div>
					<div class="import-supplier-inner-first-new global-list form-middle">
						<div class="ph_tabel_wrapper">
							<div class="main_table_wrapper ph_table_border mega" style="margin: 0;">
								<table class="ph_table border-none display table table-bordered " width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
									<thead>
										<tr>
											<th class="for-left width_50 width_50_fix"><spring:message code="application.no2" /></th>
											<th class="for-left width_200 width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>
											<th class="for-left width_100 width_100_fix"><spring:message code="label.rftbq.th.quantity.only" /></th>
											<th class="for-left width_100 width_100_fix"><spring:message code="product.UOM" /></th>
											<th class=" align-center width_100 width_100_fix"><spring:message code="product.unit.price" /></th>
											<c:if test="${not empty bq.field1Label}">
												<th class=" align-center width_100 width_100_fix">${bq.field1Label}</th>
											</c:if>
											<c:if test="${not empty bq.field2Label}">
												<th class=" align-center width_100 width_100_fix">${bq.field2Label}</th>
											</c:if>
											<c:if test="${not empty bq.field3Label}">
												<th class=" align-center width_100 width_100_fix">${bq.field3Label}</th>
											</c:if>
											<c:if test="${not empty bq.field4Label}">
												<th class=" align-center width_100 width_100_fix">${bq.field4Label}</th>
											</c:if>

											<c:if test="${not empty bq.field5Label}">
												<th class=" align-center width_100 width_100_fix">${bq.field5Label}</th>
											</c:if>
											<c:if test="${not empty bq.field6Label}">
												<th class=" align-center width_100 width_100_fix">${bq.field6Label}</th>
											</c:if>
											<c:if test="${not empty bq.field7Label}">
												<th class=" align-center width_100 width_100_fix">${bq.field7Label}</th>
											</c:if>
											<c:if test="${not empty bq.field8Label}">
												<th class=" align-center width_100 width_100_fix">${bq.field8Label}</th>
											</c:if>
											<c:if test="${not empty bq.field9Label}">
												<th class=" align-center width_100 width_100_fix">${bq.field9Label}</th>
											</c:if>
											<c:if test="${not empty bq.field10Label}">
												<th class=" align-center width_100 width_100_fix">${bq.field10Label}</th>
											</c:if>

										</tr>
									</thead>
								</table>
								<table class="ph_table data border-none" width="100%" border="0" cellspacing="0" cellpadding="0">
									<tbody>
										<c:forEach items="${bq.bqItems}" var="item">
											<tr>
												<td class="for-left width_50 width_50_fix">
												<c:if test="${item.order == 0}">
													<span class="section_name"> 
												 </c:if>
												 <c:if test="${item.order > 0}">
													<span class="item_name"> 
												 </c:if>
												 	${item.level}.${item.order}
												 </span>
												</td>
												<td class="for-left width_200 width_200_fix" align="center">
												 <c:if test="${item.order == 0}">
													<span class="section_name"> 
												 </c:if>
												 <c:if test="${item.order > 0}">
													<span class="item_name"> 
												 </c:if>
												 		<c:if test="${item.priceType == 'TRADE_IN_PRICE'}">
															<span class="bs-label label-info"><spring:message code="eventsummary.bq.trade.price" /></span>&nbsp;
														</c:if>
														<c:if test="${item.priceType == 'BUYER_FIXED_PRICE'}">
															<span class="bs-label label-success"><spring:message code="eventsummary.bq.buyer.fixedprice" /></span>&nbsp;
														</c:if> ${item.itemName}
													</span>
													<c:if test="${not empty item.itemDescription}">
														<span data-toggle="collapse" class="s2_view_desc">View Description</span>
														<%-- 													<span class="item_detail s1_text_small">${item.itemDescription}</span> --%>
													</c:if>
												</td>
												<td class="for-left width_100 width_100_fix" align="center">
												<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${item.quantity}" />
												</td>
												<td class="for-left width_100 width_100_fix" align="center">${item.uom.uom}</td>
												<td class=" align-center width_100 width_100_fix"><c:if test="${item.priceType == 'BUYER_FIXED_PRICE'}">
														<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${item.unitPrice}" />
													</c:if> &nbsp;</td>
												<c:if test="${not empty bq.field1Label}">
													<td class=" align-center width_100 width_100_fix">${item.field1}&nbsp;</td>
												</c:if>
												<c:if test="${not empty bq.field2Label}">
													<td class=" align-center width_100 width_100_fix">${item.field2}&nbsp;</td>
												</c:if>
												<c:if test="${not empty bq.field3Label}">
													<td class=" align-center width_100 width_100_fix">${item.field3}&nbsp;</td>
												</c:if>
												<c:if test="${not empty bq.field4Label}">
													<td class=" align-center width_100 width_100_fix">${item.field4}&nbsp;</td>
												</c:if>

												<c:if test="${not empty bq.field5Label}">
													<td class=" align-center width_100 width_100_fix">${item.field5}&nbsp;</td>
												</c:if>
												<c:if test="${not empty bq.field6Label}">
													<td class=" align-center width_100 width_100_fix">${item.field6}&nbsp;</td>
												</c:if>
												<c:if test="${not empty bq.field7Label}">
													<td class=" align-center width_100 width_100_fix">${item.field7}&nbsp;</td>
												</c:if>
												<c:if test="${not empty bq.field8Label}">
													<td class=" align-center width_100 width_100_fix">${item.field8}&nbsp;</td>
												</c:if>
												<c:if test="${not empty bq.field9Label}">
													<td class=" align-center width_100 width_100_fix">${item.field9}&nbsp;</td>
												</c:if>
												<c:if test="${not empty bq.field10Label}">
													<td class=" align-center width_100 width_100_fix">${item.field10}&nbsp;</td>
												</c:if>
											</tr>
											<tr class="collapse">
												<td style="border-top: 0 !important;"></td>
												<td colspan="${bq.headerCount}" style="border-top: 0 !important;"><span class="item_detail s2_text_small">${item.itemDescription}</span></td>

											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</div>

<style>
.s2_text_small {
	margin: 5px 0 0 0;
	font-size: 11px;
	display: none;
	max-height: 150px;
	text-align: left;
	margin-top: -1%;
}
.bqname {
   font-size: 16px;
}
</style>