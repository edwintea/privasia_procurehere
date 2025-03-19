
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">

<div class="lower-table-reserve-auction bg-show-me float-left pad_all_5">
	<div class="">
		<div class="col-md-3 col-sd-3 marg-top-10">
			<label><spring:message code="auction.bidhistory.view" />: </label>
		</div>
		<div class="col-md-3 col-sd-3">
			<select class="chosen-select auctionBidSupplier selectSupp yehSupplierHai">
				<option value=""><spring:message code="Product.favoriteSupplier" /></option>
				<c:forEach items="${eventSupplierList}" var="eventSupplier">
					<option value="${eventSupplier.id}">${eventSupplier.companyName}</option>
				</c:forEach>
			</select>
		</div>

	</div>
	<div class="hideDiv">
		<button class="btnrefresh btn-success mrg5R refreshAuctionBids">
			<i class="glyph-icon font-size-11 icon-refresh "></i>
		</button>
	</div>
</div>
<div class="row">&nbsp;
</div>
<div class="auctionbids lower-table-reserve-auction price-total">
	<table cellpadding="0" cellspacing="0" style="width:600px;border: 1px #f4f5f9 solid;color: #4f4d4d;">
		<thead id="bidsThead">
			<tr>
				<th><spring:message code="summarydetails.auctionrules.rank" /></th>
				<th class="align-right"><spring:message code="auction.bidhistory.amount" /></th>
				<th class="align-right"><spring:message code="auction.bidhistory.date" /></th>
				<c:if test="${event.status eq 'SUSPENDED' and eventPermissions.revertBidUser}">
					<th class="align-right"><spring:message code="auction.bidhistory.revert" /></th>
				</c:if>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>
<div class="lower-table-reserve-auction1 price-total padDetailsDiv" style="height: 150px;"></div>
<!-- <div class="auctionbids padDiv" style="height: 200px;"></div>
 -->
<style>
.btnrefresh {
	line-height: 35px;
	height: 38px;
	min-width: 36px;
}
</style>
