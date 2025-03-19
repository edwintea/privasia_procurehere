<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="select_pack_wrap">
	<div class="sp_inner">
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<div class="sp_box1" style="border:0px;">
			<div class="home_tab_wrap">
				<ul class="tabs pull-left">
					<c:forEach items="${planList}" var="plan" varStatus="status">
						<li href="#" class="tab-link ${status.index == 0 ? 'current' : '' } ${plan.planType == 'PER_USER' ? 'userTab': 'eventTab'} tab-link-${status.index}" data-tab="tab-${status.index}" data-att-tab="${plan.planType == 'PER_USER' ? 'userTab': 'eventTab'}">${plan.planName}</li>
					</c:forEach>
				</ul>
			</div>
			<input type="hidden"  id="stripePublishKey" value="${publicKey}">
			<c:forEach items="${planList}" var="plan" varStatus="status">
				<div id="tab-${status.index}" class="tab-content ${status.index == 0 ? 'current' : '' } doc-fir tab-main-inner" style="display: none;">
					<input type="hidden" id="${plan.planType == 'PER_USER' ? 'user': 'event'}currencyId" value="${plan.currency.currencyCode}">
					<div class="spb_heading1">
						<c:out value="${plan.planName}" />
					</div>
					<%-- <div class="tpb_open_box">
						<c:out value="${plan.shortDescription}" escapeXml="false" />
					</div> --%>
					<c:set var="endRange" value="999" />
					<table id="${plan.planType == 'PER_USER' ? 'userRangeTable': 'eventRangeTable'}" class="table_align_center table1">
						<c:if test="${buyerCountry == 'MY'}">
							<c:set var="tax" value="${not empty plan.tax ? plan.tax : 0}" />
						</c:if>
						<c:if test="${buyerCountry != 'MY'}">
							<c:set var="tax" value="0" />
						</c:if>
						<input type="hidden" id="tax_${plan.id}" value="${tax}">
						<fmt:formatNumber groupingUsed="false" var="taxFormt" type="number" minFractionDigits="0" maxFractionDigits="2" value="${tax}" />
						<fmt:formatNumber groupingUsed="false" var="base" type="number" minFractionDigits="0" maxFractionDigits="2" value="${plan.basePrice}" />
						<input type="hidden" id="taxFormt${plan.planType == 'PER_USER' ? 'User': 'Event'}" value="${tax}">
						<c:set var="basePriceWithTax" value="${not empty base ? base + ((base * taxFormt)/100) : 0}" />
						<fmt:formatNumber groupingUsed="false" var="basePriceWithTaxFormt" type="number" minFractionDigits="2" maxFractionDigits="2" value="${basePriceWithTax}" />
						<c:forEach items="${plan.rangeList}" var="range" varStatus="index">
							<fmt:formatNumber groupingUsed="false" var="rangePrice" type="number" minFractionDigits="0" maxFractionDigits="2" value="${range.price}" />
							<c:set var="rangePriceWithTax" value="${not empty range.price ? rangePrice + ((rangePrice * taxFormt)/100) : 0}" />
							<fmt:formatNumber groupingUsed="false" var="rangePriceWithTaxFormt" type="number" minFractionDigits="2" maxFractionDigits="2" value="${rangePriceWithTax}" />
							<tr>
								<c:set var="endRange" value="${range.rangeEnd}" />
								<td data-id="${range.id}" data-start="${range.rangeStart}" data-end="${range.rangeEnd}" data-price="${range.price}" style="text-align: right"><span style="font-size: 23px; font-weight: bold; color: #3f96d8;">${range.rangeStart}</span> <span style="font-size: 14px; font-weight: bold; color: #3f96d8;">to</span> <span style="font-size: 23px; font-weight: bold; color: #3f96d8;">${range.rangeEnd}</span> <span style="font-size: 14px; font-weight: bold; color: #3f96d8;">${plan.planType == 'PER_USER' ? ' Users': ' Events'}</span>
								</td>
								<td>${plan.currency.currencyCode}<span style="font-size: 23px;"> <c:if test="${not empty plan.basePrice and index.index == 0 }">
											<fmt:formatNumber groupingUsed="true" type="number" minFractionDigits="2" maxFractionDigits="2" value="${plan.basePrice}" />
											<fmt:formatNumber groupingUsed="false" var="BasePrice" type="number" value="${plan.basePrice}" />
											<input type="hidden" id="basePrice" value="${BasePrice}">
											<input type="hidden" id="baseUsers" value="${plan.baseUsers}">
										</c:if> <c:if test="${!(not empty plan.basePrice and index.index == 0)}">
											<fmt:formatNumber groupingUsed="true" type="number" minFractionDigits="2" maxFractionDigits="2" value="${range.price}" />
										</c:if>
								</span> <c:if test="${not empty plan.basePrice and index.index == 0 }">
									/month (${basePriceWithTaxFormt} inclusive of ${taxFormt}% SST)
									</c:if> <c:if test="${!(not empty plan.basePrice and index.index == 0)}">
									${plan.planType == 'PER_USER' ? '/user/month': '/event'} (${rangePriceWithTaxFormt} inclusive of ${taxFormt}% SST)
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</table>
					<div>
						<c:choose>
							<c:when test="${empty changePlan}">
								<c:url var="buyerSubs" value="/buyerSubscription/get/${plan.id}" />
							</c:when>
							<c:otherwise>
								<c:url var="buyerSubs" value="/buyer/billing/changeBuyerPlan/${plan.id}" />
							</c:otherwise>
						</c:choose>

						<form action="${buyerSubs}" id="idSubscribeForm" method="post">
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<div class="" style="width: 50%; float: left;">
								<c:if test="${not empty changePlan && plan.planType == 'PER_USER'}">
									<div class="col-md-12 marg_top_30 marg_bottom_10">
										<div class="col-md-6">
											<input type="checkbox" id="immediateEffect" name="immediateEffect" class="custom-checkbox" /> <label style="line-height: 0px;">Immediate Effect</label>
										</div>
										<div class="col-md-6"></div>
									</div>
								</c:if>
								<!-- <div class="col-md-12 marg_top_30 marg_bottom_10">
									<div class="col-md-8">
										<input type="checkbox" id="autoChargeSubscription" name="autoChargeSubscription" class="custom-checkbox" /> <label style="line-height: 0px;"><spring:message code="account.overview.aucto.charge" /></label>
									</div>
									<div class="col-md-4"></div>
								</div> -->
								<div class="marg_top_30 col-md-12">
									<div class="col-md-6">
										<spring:message code="appication.user" var="userLabel" />
										<spring:message code="buyer.dashboard.event" var="eventLabel" />
										<label><spring:message code="account.overview.number.of" /> ${plan.planType == 'PER_USER' ? userLabel: eventLabel}</label>
									</div>
									<div class="col-md-6">
										<input type="text" class="form-control noOf${plan.planType == 'PER_USER' ? 'user': 'Event'}" name="numberUserEvent" id="noOf${plan.planType == 'PER_USER' ? 'user': 'Event'}" value="1" data-validation="required length number" data-validation="number" data-validation-allowing="range[1;${endRange}]" data-validation-length="1-3"> <input type="hidden" name="rangeId" id="range${plan.planType == 'PER_USER' ? 'User': 'Event'}Id"> <span
											id="${plan.planType == 'PER_USER' ? 'user': 'event'}Error"></span>
									</div>
								</div>
								<c:forEach items="${plan.planPeriodList}" var="period" varStatus="status">
									<c:if test="${status.index == '0'}">
										<div class="marg_top_30 col-md-12">
											<div class="col-md-12">
												<label><spring:message code="account.overview.subscription.period" /></label>
											</div>
										</div>
									</c:if>
									<div class="">
										<div class="col-md-2">
											<input type="radio" name="periodId" value="${period.id}" data-duration="${period.planDuration}" class="periodDuration" data-index="${status.index}" id="radio_${status.index}" style="margin-left: 25px;" />
										</div>
										<div class="col-md-10">
											<spring:message code="changeplan.month.label" var="monthLabel" />
											<spring:message code="buyplan.months" var="monthsLabel" />
											<span style="font-size: 20px;"></span> ${period.planDuration} ${period.planDuration == '1' ? monthLabel : monthsLabel } &nbsp;&nbsp;&nbsp; <span id="discountValue${status.index}">${period.planDiscount}</span> % Discount
										</div>
									</div>
								</c:forEach>

								<%-- <div class="choose_bttn">
						<a href="${pageContext.request.contextPath}/subscription/get/${plan.id}" class="cb_style cb_grey hvr-pop">Get ${plan.planName}</a>
					</div> --%>
								<div class="col-md-12 marg_top_30 marg_bottom_10">
									<div class="col-md-6">
										<label><spring:message code="promo.code.title" /></label>
									</div>
									<div class="col-md-6">
										<input type="hidden" name="promoCodeId" id="promoCode${plan.planType == 'PER_USER' ? 'User': 'Event'}Id"> <input type="text" class="form-control" id="promoCode${plan.planType == 'PER_USER' ? 'User': 'Event'}" name="promoCode"> <span id="promoError${plan.planType == 'PER_USER' ? 'User': 'Event'}"></span>
									</div>
								</div>
								<!-- label to show free approver user -->
								<c:if test="${plan.planType == 'PER_USER'}">
									<div class="col-md-12 marg_top_30 marg_bottom_10">
										<span style='color: red;'> * </span>
										<spring:message code="account.overview.note.for" />
										<span id="userNo"><spring:message code="account.overview.each.other" /></span>
										<spring:message code="account.overview.you.get" />
										<span id="appNo">1 <spring:message code="account.overview.approver.user" /></span> free.
									</div>
								</c:if>
							</div>
							<div style="width: 50%; float: right; margin-top: 30px;">
								<table id="totalFeeTable${plan.planType == 'PER_USER' ? 'User': 'Event'}" class="td-border table1" style="width: 100%">
									<tr>
										<th colspan="2">
											<h4 style="font-weight: bold; color: #3f96d8;">
												<spring:message code="buyplan.total.fee" />
											</h4>
										</th>
									</tr>
									<tr id="baseFeeTr" class="flagvisibility">
										<td id="baseFeeLabel" style="text-align: left"></td>
										<td id="baseFeeValue" style="text-align: right"></td>
									</tr>
									<tr id="totalFeeTr">
										<td id="totalFeeLabel" style="text-align: left"></td>
										<td id="totalFeeValue" style="text-align: right"></td>
									</tr>
									<c:if test="${plan.planType == 'PER_USER'}">
										<!-- tabel column shows free approver user -->
										<tr>
											<td id="approvalUser" style="text-align: left"></td>
											<td style="text-align: right">0.00</td>
										</tr>
										<tr>
											<td id="totalFeeDiscountLabel" style="text-align: left"></td>
											<td id="totalFeeDiscountValue" style="text-align: right"></td>
										</tr>

									</c:if>
									<tr>
										<td id="totalFeePromoLabel" style="text-align: left"><spring:message code="no.promotional.code" /></td>
										<td id="totalFeePromoValue" style="text-align: right">0.00</td>
									</tr>
									<tr>
										<td style="text-align: left"><spring:message code="product.list.tax" /> ${taxFormt}% <spring:message code="account.overview.gst" /></td>
										<td id="tax" style="text-align: right">0.00</td>
									</tr>
									<tr>
										<td colspan="2" class="total-td"><span style="font-weight: bold; color: #212223;"> ${plan.currency.currencyCode} <span id="totalFeeAmount"></span>
										</span>
											<div class="row marg_bottom_10">
												<div class="col-md-3"></div>
												<div class="col-md-5">
													<c:choose>
														<c:when test="${empty changePlan}">
															<input type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" value="Get ${plan.planName}">
														</c:when>
														<c:otherwise>
															<!-- <div class="rfr_field" id="idButtonHolder"></div> -->
															<button type="button" value="Pay" id="pay_${plan.id}" plan-id="${plan.id}" currency-code="${plan.currency.currencyCode}" endpoint="${buyerSubs}" amount=""
															class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out full-width stripe-payment-btn">Pay</button>
														</c:otherwise>
													</c:choose>
												</div>
												<div class="col-md-4"></div>
											</div> <!-- <div class="row marg_bottom_10">
												<div class="col-md-12">
													<img src="https://www.paypalobjects.com/webstatic/en_US/i/buttons/cc-badges-ppppcmcvdam.png" alt="Credit Card Badges">
												</div>
												<div class="col-md-4"></div>
											</div> --></td>
									</tr>
								</table>
							</div>
						</form>
					</div>
				</div>
			</c:forEach>
		</div>
		<!--
		<div class="planGroup">
			<c:forEach items="${planList}" var="plan">
				<div class="sp_box1">
					<div class="spb_heading1">
						<c:out value="${plan.planName}" />
					</div>
					<div class="tpb_open_box">
						<c:out value="${plan.description}" escapeXml="false" />
					</div>
					<div class="choose_bttn">
						<a href="${pageContext.request.contextPath}/subscription/get/${plan.id}" class="cb_style cb_grey hvr-pop">Get ${plan.planName}</a>
					</div>
				</div>
			</c:forEach>
		</div> -->
		<c:if test="${empty planList}">
			<div class="sp_box2">
				<div class="spb_heading4">
					<spring:message code="subscription.no.plan.defined" />
				</div>
			</div>
		</c:if>
	</div>
	<div class="clear"></div>
</div>

<div class="modal fade" id="makePaymentModal" role="dialog" payment-mode="" payment-amount="">
		<div class="modal-dialog for-delete-all reminder documentBlock" style="width: 485px !important;">
			<!-- Modal content-->
			<div class="modal-content">
				<input type="hidden" value="${subscription.id}" id="subscriptionId">
				<!-- <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> -->
				<div class="modal-body">
					<button class="close for-absulate" id="makePaymentModalCloseId" type="button"
						data-dismiss="modal">x</button>
					<div class="row">
						<div class="col-md-12">
							<h3 class="title-hero text-center" class="header-section">
								Choose Payment Mode
							</h3>
							<div class="example-box-wrapper">
								<ul class="list-group list-group-separator row list-group-icons alignment">
									<li class="col-md-5">
										<a href="#tab-example-1" id="tabOneId" data-toggle="tab" class="list-group-item">
											<i class="glyphicon glyphicon-credit-card" style="top: 2px;"></i>&nbsp;&nbsp;
											<spring:message code="payment.mode.card" />
										</a>
									</li>
									<li class="col-md-5" id="tabTwoIdParent">
											<a href="#tab-example-2" id="tabTwoId" data-toggle="tab"
												class="list-group-item">
												<div class="icon icon-fpx"></div>&nbsp;&nbsp;
												<spring:message code="payment.mode.fpx" />
											</a>
									</li>
								</ul>
								<div class="tab-content">
									<div class="panel">
										<div class="panel-body">
											<div class="tab-pane fade" id="tab-example-1">
												<div class="row col-12 alignment">
													<img src="${pageContext.request.contextPath}/resources/assets/images/cards-logo.jpeg"
														class="payment-images-card">
												</div>
												<div id="cardBlock">
													<div class="sr-root">
														<div class="sr-main">
															<form id="payment-form" class="sr-payment-form">
																<div
																	class="margin-top-15 parent-card-div row center-align-row">
																	<div class="col-md-12 padding-left-right-0">
																		<div class="sr-combo-inputs-row">
																			<div class="sr-input sr-card-element form-control stripe-form-control"
																				id="card-element"></div>
																		</div>
																	</div>
																</div>
																<div class="sr-field-error" id="card-errors" role="alert">
																</div>
																<div class="margin-top-15 parent-card-div row">
																	<div class="col-md-12">
																		<button id="payByCardId" type="button"
																			class="ph_btn_small btn-success full-width payment-btn btn alignment">
																			Pay&nbsp;<span id="checkoutCardAmount"></span>
																		</button>
																	</div>
																</div>
															</form>
														</div>
													</div>
												</div>
											</div>
											<div class="tab-pane fade" id="tab-example-2">
												<div class="row col-12 alignment">
													<img src="${pageContext.request.contextPath}/resources/assets/images/fpx-logo.png"
														class="payment-images">
												</div>
												<div id="fpxBlock">
													<div class="payment-block">
														<form id="payment-form-fpx">
															<div class="row">
																<div class="col-md-12 align-center">
																	<div id="fpx-bank-element"
																		class="form-control stripe-form-control"
																		style="padding-top: 0;"></div>
																</div>
															</div>
															<div class="row margin-top-15">
																<div class="col-md-12 align-center">
																	<button id="fpx-button" data-secret=""
																		class="btn-success no-border ph_btn_small full-width payment-btn btn alignment">
																		Pay&nbsp;<span id="checkoutFpxAmount"></span>
																	</button>
																</div>
															</div>
														</form>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
</div>


<style>
div#idGlobalError {
	float: left;
	width: 100%;
}

.alert .alert-icon {
	line-height: 34px;
	float: left;
	width: 34px;
	height: 34px;
	margin: 5px 10px 0 0;
	text-align: center;
}

.alert .alert-title {
	font-size: 12px;
	font-weight: bold;
	margin: 4px 0 3px;
	padding: 0;
	text-transform: uppercase;
}

.table_align_center {
	width: 80%;
	margin-left: 10%;
	margin-right: 10%;
}

.spb_heading1 {
	font-size: 30px;
	text-transform: none;
}

.spb_heading1 {
	width: 100%;
	float: left;
	text-align: center;
	font-size: 35px;
	text-transform: uppercase;
	color: #758d94;
	padding: 0 0 20px 0;
}

.sp_box1 {
	width: 70%;
	border: 1px solid #ccc;
	margin-bottom: 10px;
}

<!--
for buyer subscription tab  -->.home_tab_wrap {
	width: 100%;
	float: left;
}

ul.tabs {
	margin: 0px;
	padding: 0px;
	list-style: none;
}

.pull-left, .float-left {
	float: left !important;
}

ul.tabs li.current {
	background: #fff;
	color: #636363;
	border-left: 1px solid #e8e8e8;
	border-right: 1px solid #e8e8e8;
	border-top: 1px solid #e8e8e8;
}

ul.tabs li {
	background: #a0a0a0;
	color: #fff;
	display: inline-block;
	padding: 10px 25px;
	cursor: pointer;
	font-family: 'open_sanssemibold';
	font-weight: normal;
	margin-right: 5px;
	border-radius: 3px 3px 0 0;
	position: relative;
	z-index: 2;
}

.tab-content.current {
	display: block !important;
}

.tab-main-inner {
	float: left;
	width: 100%;
	text-align: left;
	background: #fff;
	margin-top: -1px;
	border: 1px solid #e8e8e8;
	position: relative;
	z-index: 1;
}

.marg_top_30 {
	margin-top: 30px;
}

.marg_bottom_10 {
	margin-bottom: 10px;
}

.table1 td, th {
	text-align: center;
}

.td-border td {
	border: 2px solid #a1a1a1;
	padding-top: 10px;
	padding-bottom: 10px;
	padding-left: 10px;
	padding-right: 10px;
	font-size: 15px;
	padding-right: 10px;
}

.td-border {
	border: 2px solid #a1a1a1;
}

.total-td {
	text-align: right !important;
	padding-top: 0px !important;
	padding-bottom: 0px !important;
}

.flagvisibility {
	display: none;
}

.payment-div {
		text-align: left;
		color: #4f4d4d !important;
		font-family: 'open_sanssemibold';
		font-weight: normal;
		margin-top: 15px;
	}

	.payment-block {
		text-align: left;
		margin-top: 10px;
		margin-bottom: 10px;
	}

	.pad-left-12 {
		padding-left: 12px;
	}

	.pad-left-0 {
		padding-left: 0;
	}

	.margin-top-15 {
		margin-top: 15px;
	}

	.parent-card-div {
		display: flex;
		align-items: center;
	}

	.sr-field-error {
		margin-top: 10px;
		text-align: center;
		font-size: 14px;
		color: #ff5757;
	}

	.stripe-iframe {
		position: absolute;
		left: 30%;
		top: 10%;
	}

	ul {
		list-style: none;
	}

	.list-group-icons .list-group-item {
		font-weight: 700;
		display: block;
		padding: 15px 10px;
		text-align: center;
		text-overflow: ellipsis;
	}

	.list-group-icons .list-group-item>.glyph-icon {
		font-size: 18px;
		float: none;
		margin: 0 auto;
	}

	.alignment {
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.group-label {
		text-align: right;
		margin-top: 10px;
	}

	.modal-dialog.for-delete-all.reminder .modal-body label {
		float: left;
		width: 100%;
		text-align: left;
	}

	.modal-dialog.for-delete-all.reminder .modal-body input {
		float: left;
		width: 100%;
		margin-right: 2%;
		margin-top: 0;
	}

	.header-section {
		text-align: center;
		margin-bottom: 20px;
		color: black;
		font-weight: 900;
	}

	.payment-images {
		width: 30%;
		height: 60px;
		max-width: 125px;
		margin: 10px;
	}

	.payment-images-card {
		width: 65%;
		height: 60px;
		max-width: 260px;
	}

	.payment-btn:focus {
		outline-color: #58d68c;
	}

	.center-align-row {
		width: 100%;
		margin-left: 0;
	}

	.padding-left-right-0 {
		padding-left: 0;
		padding-right: 0;
	}

	.pass-desc {
		color: #7f7f7f;
		font-weight: normal;
		/* 	font-size: 13px; */
	}

	.full-width {
		width: 100%;
	}

	.disabled {
		background: #aeaeae !important;
		color: #ababab !important;
	}

</style>
<script type="text/javascript" src="<c:url value="/resources/js/masonry.js"/>"></script>
<script src="https://js.stripe.com/v3/"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/selectPlan.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});

	$(document).ready(function(e) {
		var confirmPaypal = true;

		<c:if test="${not empty changePlan}">
		window.paypalCheckoutReady = function() {
			console.log("paypal");
			paypal.checkout.setup('${merchantId}', {
				environment : '${paypalEnvironment}',
				container : 'idSubscribeForm',
				condition : function() {
					confirmPaypal = true;
					var immediateAlert = $('#immediateEffect').prop('checked');

					//console.log(""+ immediateAlert);

					if ($('#idSubscribeForm').isValid() && immediateAlert) {
						//Alert for immediate effect
						if (!confirm("Any unused event will be forfeited (No refund)")) {
							console.log("cancel");
							confirmPaypal = false;
						} else {
							confirmPaypal = true;
							console.log("yes");
							$('#idSubscribeForm').submit();
						}
					}
					return $('#idSubscribeForm').isValid() && confirmPaypal;
				},
				//button: 'placeOrderBtn'
				buttons : [ {
					container : 'idButtonHolder',
					type : 'checkout',
					color : 'blue',
					size : 'medium',
					shape : 'rect'
				} ]
			});
		};
		</c:if>
		$("#idButtonHolder").click(function(e) {
			console.log("confirmPaypal :" + !confirmPaypal);
			if (!confirmPaypal) {
				e.preventDefault();
			}
		});
	});
</script>
<script src="//www.paypalobjects.com/api/checkout.js" async></script>
