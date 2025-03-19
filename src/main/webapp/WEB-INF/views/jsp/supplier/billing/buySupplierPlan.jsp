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
	<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
	<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
	<div class="sp_inner">
		<div class="sp_box1">
			<div class="clear"></div>

			<input type="hidden"  id="stripePublishKey" value="${publicKey}">
			<c:forEach items="${planList}" var="plan" varStatus="status">
				<div id="tab-${status.index}" class="tab-content ${status.index == 0 ? 'current' : '' } doc-fir tab-main-inner" style="display: none;">




					<fmt:formatNumber groupingUsed="false" type="number" var="basePrice" minFractionDigits="2" maxFractionDigits="2" value="${plan.price}" />
					<div>
						<c:choose>
							<c:when test="${empty changePlan}">
								<c:url var="buyerSubs" value="/buyerSubscription/get/${plan.id}" />
							</c:when>
							<c:otherwise>
								<c:url var="buyerSubs" value="/buyer/billing/buyBuyerPlan/${plan.id}" />
							</c:otherwise>
						</c:choose>

						<form:form action="${pageContext.request.contextPath}/supplier/billing/doBuyPlanInitiate/${plan.id}" id="supplierCheckoutForm" method="POST"  commandName="subscription">

							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<div class="col-sm-12 col-md-12 col-lg-4 col-xs-12 pack-amount-details" id="ubp">
								<h4 class="border-btm">${plan.planName}</h4>
								<div class="pack-details">

									<c:if test="${supplier.registrationOfCountry.countryCode == 'MY'}">
										<c:set var="tax" value="${not empty plan.tax ? plan.tax : 0}" />
									</c:if>
									<c:if test="${supplier.registrationOfCountry.countryCode != 'MY'}">
										<c:set var="tax" value="0" />
									</c:if>
									<div class="pack-details">
										<label class="width-label-100"> <input type="radio" name="supplerPlan" value="ALLBUYER" checked="checked"> <span class="left-span">${plan.planName}</span> <span class="pull-right width-label-100-span-2"><span id="planPriceId"></span></span>
										</label>
										<p class="usd-details">${plan.currency.currencyCode}&nbsp;${plan.price}&nbsp;/&nbsp;${plan.periodUnit}</p>
										<p class="usd-details">( Inclusive of ${tax} % SST )</p>
										<input type="hidden" id="tax_${plan.id}" value="${tax}">
										<input type="hidden" name="supplerCountry" value="${supplier.registrationOfCountry.countryCode}">
									</div>

								</div>


								<div class="pack-details">
									<div class="form-inline">
										<label class="margin-right-20">Promo Code</label> <input type="text" name="promocode" id="promocode" class="form-control"> <span class="pull-right " id="promoDiscountPrice"> <input type="hidden" name="promoDiscountPrice" value="0.00"> 0.00
										</span> <span id="promoError"></span>
									</div>
								</div>


								<div class="pack-details">

									<div class="pack-details">
										<h4 class="border-btm"></h4>
										<h4 class="pull-right" id="totalPrice">
											US$ <input type="hidden" name="totalFeeAmount" value="${basePrice}"> ${basePrice}
										</h4>

										<div id="paypalPayment">
											<!-- <span class="rfr_field proceedPayment" id="supplierCheckOutBtn"></span> -->
											<button type="button" value="Pay" id="pay_${plan.id}" plan-id="${plan.id}" currency-code="${plan.currency.currencyCode}" amount="${basePrice}"
											class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out full-width stripe-payment-btn">Pay</button>
										</div>
									</div>

								</div>
							</div>
						</form:form>
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

	<div class="modal fade" id="makePaymentModal" role="dialog" payment-mode="" payment-amount="">
		<div class="modal-dialog for-delete-all reminder documentBlock" style="width: 485px !important;">
			<!-- Modal content-->
			<div class="modal-content">
				<!-- <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> -->
				<div class="modal-body">
					<button class="close for-absulate" id="makePaymentModalCloseId" type="button"
						data-dismiss="modal">×</button>
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
		/* border: 1px solid #ccc; */
		margin-bottom: 10px;
	}

	< !-- for buyer subscription tab -->.home_tab_wrap {
		width: 100%;
		float: left;
	}

	ul.tabs {
		margin: 0px;
		padding: 0px;
		list-style: none;
	}

	.pull-left,
	.float-left {
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

	.table1 td,
	th {
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

	.help {
		color: #fff;
		background-color: #feb22a;
		width: 12px;
		height: 12px;
		display: inline-block;
		border-radius: 100%;
		font-size: 10px;
		text-align: center;
		text-decoration: none;
		-webkit-box-shadow: inset -1px -1px 1px 0px rgba(0, 0, 0, 0.25);
		-moz-box-shadow: inset -1px -1px 1px 0px rgba(0, 0, 0, 0.25);
		box-shadow: inset -1px -1px 1px 0px rgba(0, 0, 0, 0.25);
	}

	.paypal1 {
		margin-left: 45px;
	}

	.pass-desc {
		color: #7f7f7f;
		font-weight: normal;
		/* 	font-size: 13px; */
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


<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script src="//www.paypalobjects.com/api/checkout.js" async></script>
<script type="text/javascript">
	$.validate({
		lang: 'en',
		modules: 'date, security'
	});


	$('#promocode').on('change', function (e) {
		$('#promoError').html('').removeClass('has-error');
		var promoCode = $(this).val();
		var selectedPlan = $("input[name=supplerPlan]:checked").val();
		calculateSupplierPlan(promoCode, selectedPlan);

	});

	function calculateSupplierPlan(promoCode, selectedPlan) {
		var countryId = $("input[name=supplerCountry]").val();
		url = getContextPath() + "/suppliersubscription/getSupplierPrice";
		$.ajax({
			type: "GET",
			url: url,
			data: {
				'promoCode': promoCode,
				'selectedPlan': selectedPlan,
				'countryId':countryId
			},
			beforeSend: function (xhr) {
				$('#loading').show();
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			complete: function () {
				$('#loading').hide();
			},
			success: function (data) {
				$('#promoError').html('').removeClass('has-error');

				if (selectedPlan === 'SINGLEBUYER') {
					data = {
						"promoDiscountPrice": 0,
						"totalPrice": 0,
						"supplierPrice": 0,
						"promoCodeId": "",
					};
				}
				setSupplierPrice(data);

			},
			error: function (request, textStatus, errorThrown) {
				var data = JSON.parse(request.responseText);
				var error = data.error;
				if (selectedPlan === 'SINGLEBUYER') {
					data = {
						"promoDiscountPrice": 0,
						"totalPrice": 0,
						"supplierPrice": 0,
						"promoCodeId": "",
					};
				}
				setSupplierPrice(data);

				$('#promoError').html('<span class="help-block form-error">' + error + '</span>').addClass('has-error');
			}

		});

	}

	function setSupplierPrice(data) {
		var supplierPrice = data.supplierPrice;
		var promoDiscountPrice = data.promoDiscountPrice;
		var totalPrice = data.totalPrice;
		var promoCodeId = data.promoCodeId;

		$('#supplierPrice').val(supplierPrice);

		if (totalPrice === parseFloat(0)) {
			console.log("FREE TIRE");
			$("#toptitle").text("SINGLE BUYER");

			$('#submitPayment').css({
				'display': 'block'
			});
			$('#paypalPayment').css({
				'display': 'none'
			});
		} else {

			console.log("BUY TIRE");
			$("#toptitle").text("UNLIMITED BUYER");

			$('#submitPayment').css({
				'display': 'none'
			});
			$('#paypalPayment').css({
				'display': 'block'
			});
		}

		$('#promoCodeId').val(promoCodeId);

		if(promoDiscountPrice){
			var promoDiscountHtml = '<input type="hidden" name="promoDiscountPrice" value="' + promoDiscountPrice + '" >' + ReplaceNumberWithCommas(promoDiscountPrice.toFixed(2));
			$('#promoDiscountPrice').html(promoDiscountHtml);
		}

		if(totalPrice){
			var totalHtml = 'US$ <input type="hidden" name="totalPrice" id="totalPriceInput" value="' + totalPrice + '" >' + ReplaceNumberWithCommas(totalPrice.toFixed(2));
			$('#totalPrice').html(totalHtml);
			$('#planPriceId').html((Number(totalPrice).toFixed(2)));
		}

	}


	$(document).ready(function () {
		console.log('aaaa');
		stripe = Stripe($('#stripePublishKey').val());
		elements = stripe.elements()
		calculateSupplierPlan('', 'ALLBUYER');
		$(document).on('submit', '#payment-form-fpx', function (event) {
			var fpxBank = elements.getElement('fpxBank');
			event.preventDefault();
			payByFpx(event, fpxBank);
		});

	});

	$(document).on("click", ".stripe-payment-btn", function (e) {
		if ($('#supplierCheckoutForm').isValid()) {
			e.preventDefault();
			$(this).prop('disabled', true);
			$(this).addClass('disabled');
			$('#makePaymentModal').attr('plan-id', $(this).attr('plan-id'));
			$('#makePaymentModal').attr('currency-code', $(this).attr('currency-code'));
			var amount = Number($('#totalPriceInput').val());
			$('#checkoutCardAmount').html(' ' + $(this).attr('currency-code') + ' ' + amount.toLocaleString(undefined, { maximumFractionDigits: 2, minimumFractionDigits: 2 }))
			$('#checkoutFpxAmount').html(' ' + $(this).attr('currency-code') + ' ' + amount.toLocaleString(undefined, { maximumFractionDigits: 2, minimumFractionDigits: 2 }))
			if ($(this).attr('currency-code') != 'MYR') {
				$('#tabTwoIdParent').addClass('hidden')
			} else {
				$('#tabTwoIdParent').removeClass('hidden')
			}
			$('#makePaymentModal').modal().show();
			$('#tabOneId').click();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();
		}
	});

	function payByFpx(header, token, event, fpxBank) {
		var data = {};
		data['id'] = null;
		$.ajax({
			type: "POST",
			url: getContextPath() + '/supplier/billing/doBuyPlanInitiate/' + $('#makePaymentModal').attr('plan-id') + '?paymentMode=fpx' + '&promocode=' + $('#promocode').val(),
			contentType: "application/json",
			data: JSON.stringify(data),
			dataType: 'json',
			beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
			},
			success: function (data) {
				$('#fpx-button').attr('data-secret', data.clientSecret);
				event.preventDefault();
				var fpxButton = document.getElementById('fpx-button');
				var clientSecret = fpxButton.dataset.secret;
				stripe.confirmFpxPayment(clientSecret, { payment_method: { fpx: fpxBank, }, return_url: window.location.href }).then(function (result) {
					if (result.error) {
						showErrorForPayment(result.error.message)
					}
				});
			},
			error: function (request) {
				$('#makePaymentModalCloseId').click();
				if (request.getResponseHeader('error')) {
					$('div[id=idGlobalError]').hide();
					$('div[id=idGlobalSuccess]').hide();
					showErrorForPayment(request.getResponseHeader('error').split(",").join("<br/>"))
				}
				$('#loading').hide();
			},
			complete: function () {
				$('#loading').hide();
			}
		});
	}

	function payByCard() {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var card = elements.getElement('card');
		var data = {};
		data['id'] = null;
		$.ajax({
			type: "POST",
			url: getContextPath() + '/supplier/billing/doBuyPlanInitiate/' + $('#makePaymentModal').attr('plan-id') + '?paymentMode=card' + '&promocode=' + $('#promocode').val(),
			contentType: "application/json",
			data: JSON.stringify(data),
			dataType: 'json',
			beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
			},
			success: function (data) {
				stripe.confirmCardPayment(data.clientSecret, { payment_method: { card: card } }).then(function (result) {
					if (result.error) {
						$('div[id=idGlobalError]').hide();
						$('div[id=idGlobalSuccess]').hide();
						if (result.error.payment_intent && result.error.payment_intent.id) {
							var location = getContextPath() + '/supplier/supplierSubscriptionError/' + $('#makePaymentModal').attr('plan-id') + '/' + result.error.payment_intent.id
							window.location.href = location;
						} else {
							showErrorForPayment(result.error.message);
						}
					} else {
						$('#loading').show();
						var location = window.location.href;
						location += '?&payment_intent=' + result.paymentIntent.id;
						window.location.href = location;
					}
				});
			},
			error: function (request) {
				$('#makePaymentModalCloseId').click();
				if (request.getResponseHeader('error')) {
					showErrorForPayment(request.getResponseHeader('error').split(",").join("<br/>"))
				}
				$('#loading').hide();
			},
			complete: function () {
				$('#loading').hide();
			}
		});
	}

	function showErrorForPayment(errorMsgText) {
		$('#payByCardId').disabled = true;
		$('#payByCardId').addClass('disabled');
		$('#payByCardId').removeClass('btn-success');
		$('#fpx-button').disabled = true;
		$('#fpx-button').addClass('disabled');
		$('#fpx-button').removeClass('btn-success');
		$('#makePaymentModalCloseId').click();
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
		var fpxBank = elements ? elements.getElement('fpxBank') : '';
		var card = elements ? elements.getElement('card') : '';
		card ? card.clear() : '';
		fpxBank ? fpxBank.clear() : '';
		$('p[id=idGlobalErrorMessage]').html(errorMsgText);
		$('div[id=idGlobalError]').show();
		document.getElementById("idGlobalError").scrollIntoView({ behavior: 'smooth', block: 'center' })

	}

	$('#makePaymentModal').on('hide.bs.modal', function (e) {
		$('#tabTwoIdParent').removeClass('hidden')
		$('#pay_' + $(this).attr('plan-id')).prop('disabled', false);
		$('#pay_' + $(this).attr('plan-id')).removeClass('disabled');
		var errorMsg = document.querySelector(".sr-field-error");
		errorMsg.textContent = '';
		var fpxBank = elements.getElement('fpxBank');
		var card = elements.getElement('card');
		card ? card.clear() : '';
		fpxBank ? fpxBank.clear() : '';
		$('#pay_' + $(this).attr('plan-id')).prop('disabled', false);
		$('#pay_' + $(this).attr('plan-id')).removeClass('disabled');
	});

	$('#tabOneId').on('shown.bs.tab', function (e) {
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
		var card = elements.getElement('card');
		$('#payByCardId').disabled = true;
		$('#payByCardId').addClass('disabled');
		$('#payByCardId').removeClass('btn-success');
		$('#makePaymentModal').attr('payment-mode', "card")
		var style = {
			base: {
				color: "#32325d",
				fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
				fontSmoothing: "antialiased",
				fontSize: "16px",
				"::placeholder": {
					color: "#aab7c4"
				}
			},
			invalid: {
				color: "#fa755a",
				iconColor: "#fa755a"
			}
		};
		card = elements.create("card", { style: style, hidePostalCode: true });
		card.mount("#card-element");
		card.on('change', function (event) {

			if (event.error) {
				$('#payByCardId').disabled = true;
				$('#payByCardId').removeClass('btn-success');
				$('#payByCardId').addClass('disabled');
			}

			if (event.complete) {
				$('#payByCardId').disabled = false;
				$('#payByCardId').removeClass('disabled');
				$('#payByCardId').addClass('btn-success');
			}
			var displayError = document.getElementById('card-errors');
			if (event.error) {
				displayError.textContent = event.error.message;
			} else {
				displayError.textContent = '';
			}
		});

	});

	$('#tabOneId').on('hidden.bs.tab', function (e) {
		var card = elements.getElement('card');
		card ? card.destroy() : '';
		document.getElementById('card-errors').textContent = '';
	});

	$('#tabTwoId').on('shown.bs.tab', function (e) {
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
		var fpxBank = elements.getElement('fpxBank');
		$('#loading').show();
		$('#fpx-button').disabled = true;
		$('#fpx-button').addClass('disabled');
		$('#fpx-button').removeClass('btn-success');
		var style = { base: { padding: '10px 12px', color: '#32325d', fontSize: '16px', } };
		fpxBank = elements.create('fpxBank', { style: style, accountHolderType: 'individual' });
		e.preventDefault();
		$('#makePaymentModal').attr('payment-mode', "fpx")
		fpxBank.mount('#fpx-bank-element');
		fpxBank.on('change', function (event) {
			$('#loading').hide();
			if (event.error) {
				$('#loading').hide();
				showErrorForPayment(event.error)
			}
			if (event.complete) {
				$('#fpx-button').disabled = false;
				$('#fpx-button').removeClass('disabled');
				$('#fpx-button').addClass('btn-success');
			}
		});
	});

	$('#tabTwoId').on('hidden.bs.tab', function (e) {
		var fpxBank = elements.getElement('fpxBank');
		fpxBank ? fpxBank.destroy() : '';
	});

	$(document).on("click", "#payByCardId", function (e) {
		payByCard();
	});

	function ReplaceNumberWithCommas(yourNumber) {
		// Seperates the components of the number
		var n = yourNumber.toString().split(".");
		// Comma-fies the first part
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		// Combines the two sections
		return n.join(".");
	}
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
</script>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script src="https://js.stripe.com/v3/"></script>
<script>
	$(document).ready(function() {
		$(".pwd").click(function() {
			$('#passwordPlaceHolder').hide();
		});

		$('#idAdminMobileNo').mask('+00 00000000000', {
			placeholder : "+60 122735465"
		});
		$('#idCompanyContactNumber').mask('+00 00000000000', {
			placeholder : "+60 322761533"
		});
	});
</script>
