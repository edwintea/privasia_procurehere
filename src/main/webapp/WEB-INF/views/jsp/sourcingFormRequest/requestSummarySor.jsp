<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:if test="${! empty sorList}">
	<div class="panel sum-accord "style="margin-top: -1%;">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" class="accordion" href="#collapseSor"> <spring:message code="eventdescription.schedule.rate.label" /> </a>
			</h4>
		</div>
		<div id="collapseSor" class="panel-collapse collapse">
			<div class="panel-body pad_all_15">
				<c:forEach items="${sorList}" var="suppBq">
					<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
						<div class="meeting2-heading">
							<h3>${suppBq.name}</h3>
						</div>
						<div class="import-supplier-inner-first-new global-list form-middle">
							<div class="ph_tabel_wrapper">
								<div class="main_table_wrapper ph_table_border mega" style="margin: 0;">
									<table class="tabaccor padding-none-td table" width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
										<thead>
											<tr>
												<th class="for-left width_50 width_50_fix"><spring:message code="application.no2" /></th>
												<th class="for-left width_100 width_100_fix"><spring:message code="label.rftbq.th.itemname" /></th>
												<th class="for-left width_100 width_100_fix"><spring:message code="label.uom" /></th>
											</tr>
										</thead>
									</table>

									<table class="ph_table data border-none" width="100%" border="0" cellspacing="0" cellpadding="0">
										<tbody>
											<c:forEach items="${suppBq.sorItems}" var="item">
												<tr>
													<td class="for-left width_50 width_50_fix">
														<c:if test="${item.order== 0}"><h5>${item.level}.${item.order}</h5></c:if>
														<c:if test="${item.order > 0}">${item.level}.${item.order} </c:if>	  
													</td>
													<td class="for-left width_100 width_100_fix" align="center">
<%--														<c:if test="${item.priceType == 'TRADE_IN_PRICE'}">--%>
<%--															<span class="bs-label label-info"><spring:message code="eventsummary.bq.trade.price" /></span>&nbsp;--%>
<%--												</c:if>--%>
<%--														<c:if test="${item.priceType == 'BUYER_FIXED_PRICE'}">--%>
<%--															<span class="bs-label label-success"><spring:message code="eventsummary.bq.buyer.fixedprice" /></span>&nbsp;--%>
<%--												</c:if>--%>
														<c:if test="${item.order == 0}"><h5>${item.itemName}</h5> </c:if>
														<c:if test="${item.order > 0}">${item.itemName} </c:if>	     
														
														<span class="item_detail s1_text_small">${item.itemDescription}</span>
													</td>
														<td class="for-left width_100 width_100_fix" align="center">${item.uom.uom}</td>
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
</style>