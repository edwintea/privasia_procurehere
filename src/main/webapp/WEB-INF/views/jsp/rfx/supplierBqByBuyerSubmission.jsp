<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="mar-top-20 lump_sum ">
	<div class="panel pad_all_10 float-left width-100 marg-none marg-bottom-20">
		<div class="col-md-3 col-sm-3">
			<label><spring:message code="supplierbq.reduce.all.item" /></label>
		</div>
		<div class="col-md-2 col-sm-2">
			<div class="reduce ">
				<input type="text" placeholder="Enter Amount" class="form-control">
			</div>
		</div>
		<div class="col-md-1 col-sm-1">
			<button class=" btn btn-info ph_btn_small hvr-pop hvr-rectangle-out reduceAllPrices"><spring:message code="application.reduce" /></button>
		</div>
		<div class="col-md-4 col-sm-4">
			<label><spring:message code="supplierbq.total.amount" /> : ${event.baseCurrency}</label>&nbsp;&nbsp;<label id="finalTotal">${rfaSupplierBq.totalAfterTax}</label>
		</div>
		<div class="col-md-2 col-sm-2">
			<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out float-right" id="submitBidPrice" type="submit"><spring:message code="supplierbq.submit.prebid" /></button>
		</div>
	</div>
</div>
<div class="fut-bid mar-top-20">
	<div class="row">
		<!-- <div class="col-md-6 col-sm-6 col-xs-6 align-left li-32">Next feasible bid RM 1,000</div>
							<div class="col-md-6 col-XS-6 col-sm-6 align-right">
								<a href="Pre_bid_price.html" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out fr"></a>
							</div> -->
	</div>
</div>
<form:form id="supplierBqList" method="post" action="${pageContext.request.contextPath}/buyer/RFA/submitEnglishAuction/${event.id}" modelAttribute="rfaSupplierBq">
	<div class="panel float-left width-100">
		<jsp:include page="/WEB-INF/views/jsp/rfx/auctionBq.jsp"/>
	</div>
	<button type="submit" class="btn btn-info btn-save hvr-pop hvr-rectangle-out fr"><spring:message code="application.save" /></button>
</form:form>