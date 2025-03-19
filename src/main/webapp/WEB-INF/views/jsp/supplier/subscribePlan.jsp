<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/saas.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<div id="page-content">
	<div class="container col-md-12">
		<header class="form_header">
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap Events-Listing-heading">Supplier Subscription Plans</h2>
			</div>
		</header>
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<div class="select_pack_wrap marg-top-20">
			<div class="sp_inner">
				<input type="hidden"  id="stripePublishKey" value="${publicKey}">
				<c:forEach items="${planList}" var="plan">
					<c:if test="${empty supplier.supplierSubscription or (!empty supplier.supplierSubscription && supplier.supplierSubscription.supplierPlan.id != plan.id)}">
						<c:if test="${supplierCountry != 'MY'}">
							<c:set var="tax" value="0" />
						</c:if>
						<c:if test="${supplierCountry == 'MY'}">
							<c:set var="tax" value="${plan.tax}" />
						</c:if>
						<input type="hidden" id="tax_${plan.id}" value="${tax}">
						<input type="hidden" id="price_${plan.id}" value="${plan.price}">
						<div class="sp_box1">
							<div class="spb_heading1">
								<c:out value="${plan.planName}" />
							</div>
							<div class="tpb_open_box">
								<c:out value="${plan.description}" escapeXml="false" />
							</div>
							<div class="clear"></div>
							<div class="marg-top-10">
								<form id="idSubscribeForm${plan.id}" action='${pageContext.request.contextPath}/supplier/payment/${plan.id}' method="post">
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									<c:if test="${plan.buyerLimit == 1}">
										<select name="buyerId" id="buyerId" data-validation="required" class="chosen-select">
											<option value="">Select Buyer</option>
											<c:forEach items="${buyerList}" var="buyer">
												<option value="${buyer.id}">${buyer.companyName}</option>
											</c:forEach>
										</select>
									</c:if>
									<div class="choose_bttn">
										<button type="button" value="Pay" id="pay_${plan.id}" plan-id="${plan.id}" currency-code="${plan.currency.currencyCode}"
											class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out full-width stripe-payment-btn">Pay</button>
									</div>
								</form>
							</div>
						</div>
					</c:if>
				</c:forEach>
				<c:if test="${empty planList}">
					<div class="sp_box2">
						<div class="spb_heading4">NO PLANS DEFINED. LOGIN AS ADMIN AND DEFINE SOME PLANS.</div>
					</div>
				</c:if>
			</div>
			<div class="clear"></div>
			<div style="height: 150px;"></div>
		</div>
	</div>
</div>
<div class="modal fade" id="makePaymentModal" role="dialog" payment-mode="">
		<div class="modal-dialog for-delete-all reminder documentBlock" style="width: 485px !important;">
			<!-- Modal content-->
			<div class="modal-content">
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
	.spb_heading1 {
		font-size: 30px;
		text-transform: none;
	}

	.sp_box1 {
		border: 1px solid #ccc;
		margin-bottom: 10px;
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

	.full-width {
		width: 100%;
	}

	.disabled{background:#aeaeae !important; color:#ababab !important;}


</style>
<script type="text/javascript" src="<c:url value="/resources/js/masonry.js"/>"></script>
<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang: 'en',
		modules: 'date, security'
	});

	$(document).ready(function () {
		$('.sp_inner').masonry({
			itemSelector: '.sp_box1',
			gutter: 10
		});
	});
	
	$(document).ready(function () {
		var stripe = null;
		var elements = null;
		stripe = Stripe($('#stripePublishKey').val());
		elements = stripe.elements()
		$(document).on('submit', '#payment-form-fpx', function (event) {
			var fpxBank = elements.getElement('fpxBank');
			event.preventDefault();
			payByFpx(event, fpxBank);
		});

		$(document).on("click", ".stripe-payment-btn", function (e) {
			if ($('#supplierCheckoutForm').isValid()) {
				e.preventDefault();
				$(this).prop('disabled', true);
				$(this).addClass('disabled');
				$('#makePaymentModal').attr('plan-id', $(this).attr('plan-id'));
				$('#makePaymentModal').attr('currency-code', $(this).attr('currency-code'));
				
				var amount = Number($('#price_' + $(this).attr('plan-id')).val()) + (Number($('#price_' + $(this).attr('plan-id')).val()) * Number($('#tax_' + $(this).attr('plan-id')).val()) / 100);
				if (!amount) {
					amount = $('input[name=totalFeeAmount]').val();
				}
	
				$('#checkoutCardAmount').html(' ' + $(this).attr('currency-code') + ' ' + amount.toLocaleString(undefined, { maximumFractionDigits: 2, minimumFractionDigits: 2 }));
				$('#checkoutFpxAmount').html(' ' + $(this).attr('currency-code') + ' ' + amount.toLocaleString(undefined, { maximumFractionDigits: 2, minimumFractionDigits: 2 }));
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
			$.ajax({
				type: "POST",
				url: getContextPath() + '/supplier/payment/' + $('#makePaymentModal').attr('plan-id') + '?paymentMode=fpx',
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
			$.ajax({
				type: "POST",
				url: getContextPath() + '/supplier/payment/' + $('#makePaymentModal').attr('plan-id') + '?paymentMode=card',
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
	});


</script>
<script src="https://js.stripe.com/v3/"></script>
