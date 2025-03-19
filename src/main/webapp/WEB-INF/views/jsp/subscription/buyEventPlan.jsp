<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere-public.css"/>">
<div class="select_pack_wrap">
	<div class="sp_inner">
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<div class="sp_box1">
			<div class="clear"></div>

			<input type="hidden"  id="stripePublishKey" value="${publicKey}">
			<input type="hidden" id="buyerCountry" value="${buyerCountry}">
			<c:forEach items="${planList}" var="plan" varStatus="status">
				<div>
					<input type="hidden" id="${plan.planType == 'PER_USER' ? 'user': 'event'}currencyId" value="${plan.currency.currencyCode}">
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
					</table>
					<fmt:formatNumber groupingUsed="false" type="number" var="eventFirstPrice" minFractionDigits="2" maxFractionDigits="2" value="${eventPrice}" />
					<div>
						<c:choose>
							<c:when test="${empty changePlan}">
								<c:url var="buyerSubs" value="/buyerSubscription/get/${plan.id}" />
							</c:when>
							<c:otherwise>
								<c:url var="buyerSubs" value="/buyer/billing/buyBuyerPlan/${plan.id}" />
							</c:otherwise>
						</c:choose>
						<input type="hidden" id="${plan.planType == 'PER_USER' ? 'user': 'event'}currencyId" value="${plan.currency.currencyCode}">

						<%-- <div class="tpb_open_box">
						<c:out value="${plan.shortDescription}" escapeXml="false" />
					</div> --%>
						<form action="${buyerSubs}" id="idSubscribeForm" method="post">
							<div class="col-sm-12 col-md-12 col-lg-4 col-xs-12 pack-amount-details" id="ebp">
								<h4 class="border-btm">EVENT BASED</h4>
								<div class="pack-details">
									<input type="hidden" name="plan.id" id="planId" value="${plan.id}"> <input type="hidden" id="eventPlanId" value="${plan.id}"> <input type="hidden" name="range.id" id="rangeUserId"> <input type="hidden"
										name="promoCodeId" id="promoCodeEventId">
									<c:forEach items="${plan.rangeList}" var="range" varStatus="index" begin="0" end="0">
										<c:set var="endEventRange" value="${range.rangeEnd}" />
										<c:set var="eventPrice" value="${range.price}" />
									</c:forEach>
									<fmt:formatNumber groupingUsed="false" type="number" var="eventFirstPrice" minFractionDigits="2" maxFractionDigits="2" value="${eventPrice}" />
								</div>

								<div class="pack-details">

									<p>
										<span class="">Event Pack</span> <span class="pull-right">${eventFirstPrice}</span>
									</p>
								</div>
								<div class="pack-details">
									<p>
										<span class="form-inline"> Number of Events <%-- <input type="text" class="form-control" name="numberUserEvent" id="additionalEvents" data-validation="length number" value="1" data-validation-allowing="range[1;${endEventRange}]" data-validation-length="1-3"> --%>
											<input type="text" class="form-control" name="userQuantity" id="additionalEvents" data-validation="length number" value="1" data-validation-allowing="range[1;${endEventRange}]" data-validation-length="1-3" />
										</span> <span class="pull-right" id="eventShowPrice">${eventFirstPrice}</span>
										
									</p>

									<p class="pack-margin-neg-12">US$ ${eventFirstPrice} per event</p>
									<p>(Inclusive of ${tax}% SST)</p>
								</div>
								<div class="pack-details">
									<form class="form-group">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<div class="form-inline">
											<label class="margin-right-20">Promo Code</label> <input type="text" class="form-control" id="promoCodeEvent" name="promoCode"> <span id="promoErrorEvent"></span>
											<span id="promoErrorUser"></span>
										</div>
									</form>
								</div>
								<h4>TOTAL FEE</h4>

								<div class="pack-details">
									<p>
										<span class="left-span"> Event Pack x &nbsp; <span class="left-span" id="selectedNoEvent">1</span>
										</span> <span class="pull-right" id="eventPrice"> <input type="hidden" name="eventPrice" value="${eventFirstPrice}"> ${eventFirstPrice}
										</span>
									</p>

								</div>
								<!-- <div class="pack-details">
					<p>
						<span class="left-span">Additional Events</span>
						<span class="pull-right" id="addtionalUserPrice">
							<input type="hidden" name="addtionalUserPrice" value="1">
							0.00
						</span>
					</p>
				</div> -->
								<div class="pack-details">
									<p>
										<span class="left-span">Promotional Code</span> <span class="pull-right" id="promoEventDiscountPrice"> <input type="hidden" name="promoDiscountPrice" value="0"> 0.00
										</span>
									</p>
								</div>

								<c:if test="${tax > 0}">
									<div class="pack-details">
										<p>
											<span class="left-span">SST ${tax}%</span> <span class="pull-right" id="taxAmount">0.00
											</span>
										</p>
									</div>
								</c:if>
								<!-- <div class="pack-details">
									<p>
									<div>
										<input type="checkbox" id="autoChargeSubscription" name="autoChargeSubscription" class="custom-checkbox" />
									</div>
									<span class="left-span"> <label style="line-height: 0px;">Auto charge subscription <i class="glyph-icon icon-question-circle" data-toggle="tooltip" data-placement="right" title=""
											data-original-title="This will enable auto-renewal of subscription."></i>
									</label>
									</span>
									</p>
								</div> -->

								<h4 class="border-btm"></h4>
								<h4 class="pull-right" id="totalEventPrice">
									${plan.currency.currencyCode} <input type="hidden" name="totalFeeAmount" value="${eventPrice}">${eventFirstPrice}
								</h4>


								<c:choose>
									<c:when test="${empty changePlan}">
										<input type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out " value="Get ${plan.planName}">
									</c:when>
									<c:otherwise>
										<!-- <div class="rfr_field paypal1" id="idButtonHolder"></div> -->
										<button type="button" value="Pay" id="pay_${plan.id}" plan-id="${plan.id}" currency-code="${plan.currency.currencyCode}" endpoint="${buyerSubs}" amount="${eventFirstPrice}"
										class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out full-width stripe-payment-btn">Pay</button>
									</c:otherwise>
								</c:choose>
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
				<div class="spb_heading4">NO PLANS DEFINED. LOGIN AS ADMIN AND DEFINE SOME PLANS.</div>
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
	border: none;
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

.paypal1
{
    margin-left: 45px;
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
<script type="text/javascript" src="<c:url value="/resources/js/view/buyUserPlan.js"/>"></script>