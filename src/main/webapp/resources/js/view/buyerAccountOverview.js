var stripe = null;
var elements = null;

$('document').ready(function () {

	var chargeMonths = 0;
	updateFields(chargeMonths);

	$("#openRenew").click(function () {
		$("#idRenewDialog").modal();
		console.log('Opening modal.....');
		if (document.getElementsByClassName("stripe-payment-btn")) {
			var element = document.getElementsByClassName("stripe-payment-btn");
			for (var index = 0; index < element.length; index++) {
				const el = element[index];
				el.removeAttribute('disabled');
				el.classList.remove("disabled");
			}
		}
		calculateTotalFee();
	});

	$("#confirmPayment").click(function () {
		$("#idconfirmPayment").modal();
	});
	$(".openEventCredits").click(function () {
		var subsId = $(this).attr('data-subscriptionId');
		console.log("subsId ==" + subsId);
		// var promoCode = 'PROMO_498_VAL';
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type: "GET", url: getContextPath() + "/buyer/billing/getSubscription/" + subsId, beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			}, complete: function () {
				$('#loading').hide();
				updateFields(chargeMonths);
				console.log("==complete==");
			}, success: function (data) {
				console.log("==success== Subs Id :" + data.id);
				chargeMonths = data.chargeMonths;
				updateSubscription(data);
				calculateUpdateTotalFee(chargeMonths);
				$('#loading').hide();
			}, error: function (request, textStatus, errorThrown) {
				console.log("==error==");
				$('#loading').hide();
			}

		});
	});

	// on ready previous subscription radio check
	var lastSubsPeriodId = $('#selectPeriodId').val();
	$('.periodRadioList input').each(function () {
		//		console.log(" ==" + $(this).val() + " lastSubsId period id====" + lastSubsPeriodId);
		if ($(this).val() === lastSubsPeriodId) {
			$(this).attr('checked', true);
		}
	});

	$(document).on("keyup", "#numberUserEvent", function (e) {
		var numberUserEvent = parseInt($(this).val());
		// console.log("numberUserEvent :"+numberUserEvent);
		$('#numberError').html('').removeClass('has-error');
		if ($(this).val() === 'undefined' || $(this).val() === '0') {
			// console.log("numberUserEvent :"+$(this).val());
			$('#numberError').html('<span class="help-block form-error">Number should be greater then 0</span>').addClass('has-error');
			return false;
		}
		if (isNaN(numberUserEvent)) {
			$('#numberError').html('<span class="help-block form-error">It should be correct number</span>').addClass('has-error');
			return false;
		}

		//Show approve user in note and table
		var planType = $('#planType').val();

		if (planType === 'PER_USER') {
			if (isNaN(numberUserEvent)) {
				$('#userNo').text("each user");
				$('#appNo').text("1 approver user");
				var approvalUser = numberUserEvent + ' approver users ';
				$("#approverUser").html(approvalUser);

			} else {
				$('#userNo').text(numberUserEvent + " users");
				$('#appNo').text(numberUserEvent + " approver users");
				var approvalUser = numberUserEvent + ' approver users ';
				$("#approverUser").html(approvalUser);
			}
			//	
		}

		var promoCode = $('#promoCode').val();
		var selectedMonths = parseInt($("input[name=periodId]:checked").attr('data-duration'));
		var montlhyDiscount = parseInt($("input[name=periodId]:checked").attr('data-discount'));
		var planType = $('#planType').val();
		var currencyCode = $('#currencyId').val();
		ajaxCallOnChangeFields(numberUserEvent, promoCode, selectedMonths, montlhyDiscount, currencyCode, planType);
	});

	$('input[name="periodId"]:radio').on('change', function (e) {
		var numberUserEvent = parseInt($('#numberUserEvent').val());
		$('#promoError').html('').removeClass('has-error');
		$('#numberError').html('').removeClass('has-error');
		if ($('#numberUserEvent').val() === 'undefined' || $('#numberUserEvent').val() === '0') {
			// console.log("numberUserEvent :"+$(this).val());
			$('#numberError').html('<span class="help-block form-error">Number should be greater then 0</span>').addClass('has-error');
			return false;
		}
		if (isNaN(numberUserEvent)) {
			$('#numberError').html('<span class="help-block form-error">It should be correct number</span>').addClass('has-error');
			return false;
		}
		var promoCode = $('#promoCode').val();
		var selectedMonths = parseInt($(this).attr('data-duration'));
		var montlhyDiscount = parseInt($(this).attr('data-discount'));
		var planType = $('#planType').val();
		var currencyCode = $('#currencyId').val();

		ajaxCallOnChangeFields(numberUserEvent, promoCode, selectedMonths, montlhyDiscount, currencyCode, planType);
	});

	$('#promoCode').on('change', function (e) {
		// alert($(this).val());
		var numberUserEvent = parseInt($('#numberUserEvent').val());
		$('#promoError').html('').removeClass('has-error');
		$('#numberError').html('').removeClass('has-error');
		if ($('#numberUserEvent').val() === 'undefined' || $('#numberUserEvent').val() === '0') {
			// console.log("numberUserEvent :"+$(this).val());
			$('#numberError').html('<span class="help-block form-error">Number should be greater then 0</span>').addClass('has-error');
			return false;
		}
		if (isNaN(numberUserEvent)) {
			$('#numberError').html('<span class="help-block form-error">It should be correct number</span>').addClass('has-error');
			return false;
		}
		var promoCode = $(this).val();
		var selectedMonths = parseInt($("input[name=periodId]:checked").attr('data-duration'));
		var montlhyDiscount = parseInt($("input[name=periodId]:checked").attr('data-discount'));
		var planType = $('#planType').val();
		var currencyCode = $('#currencyId').val();

		ajaxCallOnChangeFields(numberUserEvent, promoCode, selectedMonths, montlhyDiscount, currencyCode, planType);
	});

	stripe = Stripe($('#stripePublishKey').val());
	elements = stripe.elements()
	// calculateSupplierPlan('', 'ALLBUYER');
	$(document).on('submit', '#payment-form-fpx', function (event) {
		var fpxBank = elements.getElement('fpxBank');
		event.preventDefault();
		payByFpx(event, fpxBank);
	});


});

function calculateTotalFee() {
	// alert();
	var numberUserEvent = parseInt($('#numberUserEvent').val());


	var selectedMonths = parseInt($("input[name=periodId]:checked").attr('data-duration'));
	var montlhyDiscount = parseInt($("input[name=periodId]:checked").attr('data-discount'));
	var planType = $('#planType').val();
	// shows no approver users
	if (planType === 'PER_USER') {
		if (isNaN(numberUserEvent)) {
			var approvalUser = 'no approver users ';
			$("#approverUser").html(approvalUser);
		} else {
			var approvalUser = numberUserEvent + ' approver users ';
			$("#approverUser").html(approvalUser);
		}
	}

	console.log("numberUserEvent :" + numberUserEvent + " selectedMonths :" + selectedMonths + " montlhyDiscount :" + montlhyDiscount);
	var currencyCode = $('#currencyId').val();
	var promoCode = $('#promoCode').val();
	ajaxCallOnChangeFields(numberUserEvent, promoCode, selectedMonths, montlhyDiscount, currencyCode, planType);
}

function ajaxCallOnChangeFields(numberUserEvent, promoCode, selectedMonths, montlhyDiscount, currencyCode, planType) {
	var basePrice = $('#basePrice').val();
	var baseUsers = $('#baseUsers').val();
	var planId = '';
	if ($('#idRenewSubscribeForm')) {
		planId = $('#idRenewSubscribeFormPlanId').val()
	}
	var taxFormt = parseFloat($('#renewTaxFormt').val());
	if (promoCode !== 'undefined' && promoCode !== '') {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type: "GET", url: getContextPath() + "/buyer/billing/getPromoCode", data: {
				promoCode: promoCode,
				plan: planId,
				totalPrice: (Number($('input[name=totalFeeAmount]').val()) + Number($('#renewTaxAmount').html().replace(/,/g, '')))
			}, beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			}, complete: function () {
				$('#loading').hide();
			}, success: function (data) {
				// console.log(data);
				$('#rangeTable tr').each(function () {
					var rangeId = $(this).children('td:first').attr('data-id');
					var start = parseInt($(this).children('td:first').attr('data-start'));
					var end = parseInt($(this).children('td:first').attr('data-end'));
					var price = parseInt($(this).children('td:first').attr('data-price'));
					// console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfUser :"+ noOfUser);

					if (numberUserEvent >= start && numberUserEvent <= end) {
						// console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfUser1 :"+ noOfUser);

						$('#rangeId').val(rangeId);
						$('#promoCodeId').val(data.id);

						//					var baseFeeValue = 0;
						if (basePrice !== undefined) {
							if (planType === 'PER_USER') {

								basePrice = parseInt($('#basePrice').val());
								var baseFeeLabel = currencyCode + " " + basePrice + " for " + baseUsers + " Users X " + selectedMonths + " Months";
								$("#baseFeeLabel").html(baseFeeLabel);

								baseFeeValue = (basePrice * selectedMonths);
								console.log("baseFeeValue :" + baseFeeValue);
								$("#baseFeeValue").html(ReplaceNumberWithCommas((baseFeeValue).toFixed(2)));
								$('#baseFeeTr').removeClass('flagvisibility');


								if (baseUsers >= numberUserEvent) {
									$('#totalFeeTr').addClass('flagvisibility');
								} else {
									numberUserEvent = numberUserEvent - baseUsers;
									var totalFeeLabel = currencyCode + " " + price + " X " + numberUserEvent + " Users X " + selectedMonths + " Months";
									$("#totalFeeLabel").html(totalFeeLabel);

									$('#totalFeeTr').removeClass('flagvisibility');
								}
								var totalFeeValue = (price * numberUserEvent * selectedMonths);
								var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
								$("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
								var totalFeeAmount = baseFeeValue + totalFeeValue;
							}
						} else {

							var totalFeeLabel = currencyCode + " " + price + " X " + numberUserEvent;
							if (planType === 'PER_USER') {
								totalFeeLabel += " Users X " + selectedMonths + " Months";
							} else {
								totalFeeLabel += " Events ";
							}
							$("#totalFeeLabel").html(totalFeeLabel);

							var totalFeeValue = (price * numberUserEvent);
							if (planType === 'PER_USER') {
								totalFeeValue = totalFeeValue * selectedMonths;
							}

							// console.log("totalFeeValue :" + totalFeeValue);
							var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
							$("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));

							var totalFeeAmount = totalFeeValue;
						}

						if (planType === 'PER_USER') {
							var totalFeeDiscountLabel = "Subscription Discount " + montlhyDiscount + "%";
							// console.log("totalFeeDiscountLabel :" + totalFeeDiscountLabel);
							$("#totalFeeDiscountLabel").html(totalFeeDiscountLabel);
							var totalFeeDiscountValue = (totalFeeAmount / 100) * montlhyDiscount;
							// console.log("totalFeeDiscountValue :"+ totalFeeDiscountValue);
							var feeDiscountValue = '<input type="hidden" name="feeDiscountValue" value="' + totalFeeDiscountValue + '" >';
							$("#totalFeeDiscountValue").html(feeDiscountValue + "-" + ReplaceNumberWithCommas((totalFeeDiscountValue).toFixed(2)));

							//						totalFeeAmount = totalFeeAmount - totalFeeDiscountValue;

						} else {
							$('.subsDiscount').hide();
						}

						// console.log(data.promoDiscount);
						var totalFeePromoValue = 0;
						var totalFeePromoLabel = data.promoName;
						if (data.discountType !== 'undefined' && data.discountType === 'PERCENTAGE') {
							totalFeePromoValue = (totalFeeAmount / 100) * data.promoDiscount;
							totalFeePromoLabel += "-" + data.promoDiscount + " % OFF";
						} else {
							totalFeePromoValue = data.promoDiscount;
							totalFeePromoLabel += "- " + currencyCode + " " + data.promoDiscount + " OFF";
						}
						$('#totalFeePromoLabel').html(totalFeePromoLabel);
						var promoCodeDiscount = '<input type="hidden" name="promoCodeDiscount" value="' + totalFeePromoValue + '" >';
						$('#totalFeePromoValue').html(promoCodeDiscount + "-" + ReplaceNumberWithCommas((totalFeePromoValue).toFixed(2)));
						if (planType === 'PER_USER') {
							totalFeeAmount = (totalFeeAmount - totalFeeDiscountValue) - totalFeePromoValue;
						} else {
							totalFeeAmount = totalFeeAmount - totalFeePromoValue;
						}
						// console.log("totalFeeAmount : "+totalFeeAmount);
						var totalTax = (totalFeeAmount * taxFormt) / 100;
						totalFeeAmount = totalFeeAmount + totalTax;
						var totalFeeAmountValue = '<input type="hidden" name="totalFeeAmount" value="' + totalFeeAmount + '" >';
						$('#renewTaxAmount').html(ReplaceNumberWithCommas((totalTax).toFixed(2)));

						$('#totalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
						if (document.getElementsByClassName("stripe-payment-btn")) {
							var element = document.getElementsByClassName("stripe-payment-btn");
							for (var index = 0; index < element.length; index++) {
								const el = element[index];
								el.setAttribute('amount', ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
							}

						}
					}
				});

			}, error: function (request, textStatus, errorThrown) {
				$('#rangeTable tr').each(function () {
					var rangeId = $(this).children('td:first').attr('data-id');
					var start = parseInt($(this).children('td:first').attr('data-start'));
					var end = parseInt($(this).children('td:first').attr('data-end'));
					var price = parseInt($(this).children('td:first').attr('data-price'));
					// console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfUser :"+ noOfUser);

					if (numberUserEvent >= start && numberUserEvent <= end) {
						// console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfUser1 :"+ noOfUser);

						$('#rangeId').val(rangeId);
						var baseFeeValue = 0;
						if (basePrice !== undefined) {
							if (planType === 'PER_USER') {

								basePrice = parseInt($('#basePrice').val());
								var baseFeeLabel = currencyCode + " " + basePrice + " for " + baseUsers + " Users X " + selectedMonths + " Months";
								$("#baseFeeLabel").html(baseFeeLabel);

								baseFeeValue = (basePrice * selectedMonths);
								console.log("baseFeeValue :" + baseFeeValue);
								$("#baseFeeValue").html(ReplaceNumberWithCommas((baseFeeValue).toFixed(2)));
								$('#baseFeeTr').removeClass('flagvisibility');


								if (baseUsers >= numberUserEvent) {
									$('#totalFeeTr').addClass('flagvisibility');
								} else {
									numberUserEvent = numberUserEvent - baseUsers;
									var totalFeeLabel = currencyCode + " " + price + " X " + numberUserEvent + " Users X " + selectedMonths + " Months";
									$("#totalFeeLabel").html(totalFeeLabel);

									$('#totalFeeTr').removeClass('flagvisibility');
								}
								var totalFeeValue = (price * numberUserEvent * selectedMonths);
								var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
								$("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
								var totalFeeAmount = baseFeeValue + totalFeeValue;
							}
						} else {

							var totalFeeLabel = currencyCode + " " + price + " X " + numberUserEvent;
							if (planType === 'PER_USER') {
								totalFeeLabel += " Users X " + selectedMonths + " Months";
							} else {
								totalFeeLabel += " Events ";
							}
							$("#totalFeeLabel").html(totalFeeLabel);

							var totalFeeValue = (price * numberUserEvent);
							if (planType === 'PER_USER') {
								totalFeeValue = totalFeeValue * selectedMonths;
							}

							// console.log("totalFeeValue :" + totalFeeValue);
							var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
							$("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));

							var totalFeeAmount = totalFeeValue;
						}


						//					var totalFeeLabel = currencyCode + " " + price + " X " + numberUserEvent;
						//					if (planType === 'PER_USER') {
						//						totalFeeLabel += " Users X " + selectedMonths + " Months";
						//					} else {
						//						totalFeeLabel += " Events ";
						//					}
						//					$("#totalFeeLabel").html(totalFeeLabel);
						//
						//					var totalFeeValue = (price * numberUserEvent);
						//					if (planType === 'PER_USER') {
						//						totalFeeValue = totalFeeValue * selectedMonths;
						//					}
						//
						//					// console.log("totalFeeValue :" + totalFeeValue);
						//					var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
						//					$("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
						//
						//					var totalFeeAmount = totalFeeValue;
						if (planType === 'PER_USER') {
							var totalFeeDiscountLabel = "Subscription Discount " + montlhyDiscount + "%";
							// console.log("totalFeeDiscountLabel :" + totalFeeDiscountLabel);
							$("#totalFeeDiscountLabel").html(totalFeeDiscountLabel);
							var totalFeeDiscountValue = (totalFeeAmount / 100) * montlhyDiscount;
							// console.log("totalFeeDiscountValue :"+ totalFeeDiscountValue);
							var feeDiscountValue = '<input type="hidden" name="feeDiscountValue" value="' + totalFeeDiscountValue + '" >';
							$("#totalFeeDiscountValue").html(feeDiscountValue + "-" + ReplaceNumberWithCommas((totalFeeDiscountValue).toFixed(2)));

							totalFeeAmount = totalFeeAmount - totalFeeDiscountValue;
						} else {
							$('.subsDiscount').hide();
						}
						$('#totalFeePromoLabel').html("No Promotional Code");
						$('#totalFeePromoValue').html("0.00");

						// console.log("totalFeeAmount : "+totalFeeAmount);
						var totalTax = (totalFeeAmount * taxFormt) / 100;
						totalFeeAmount = totalFeeAmount + totalTax;
						var totalFeeAmountValue = '<input type="hidden" name="totalFeeAmount" value="' + totalFeeAmount + '" >';
						$('#renewTaxAmount').html(ReplaceNumberWithCommas((totalTax).toFixed(2)));

						$('#totalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
						if (document.getElementsByClassName("stripe-payment-btn")) {
							var element = document.getElementsByClassName("stripe-payment-btn");
							for (var index = 0; index < element.length; index++) {
								const el = element[index];
								el.setAttribute('amount', ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
							}

						}
					}
				});
				$('#promoError').html('<span class="help-block form-error">' + request.getResponseHeader('error') + '</span>').addClass('has-error');
			}

		});
	} else {
		$('#rangeTable tr').each(function () {
			var rangeId = $(this).children('td:first').attr('data-id');
			var start = parseInt($(this).children('td:first').attr('data-start'));
			var end = parseInt($(this).children('td:first').attr('data-end'));
			var price = parseInt($(this).children('td:first').attr('data-price'));
			// console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfUser :"+ noOfUser);

			if (numberUserEvent >= start && numberUserEvent <= end) {
				// console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfUser1 :"+ noOfUser);

				$('#rangeId').val(rangeId);


				var baseFeeValue = 0;
				if (basePrice !== undefined) {
					if (planType === 'PER_USER') {

						basePrice = parseInt($('#basePrice').val());
						var baseFeeLabel = currencyCode + " " + basePrice + " for " + baseUsers + " Users X " + selectedMonths + " Months";
						$("#baseFeeLabel").html(baseFeeLabel);

						baseFeeValue = (basePrice * selectedMonths);
						console.log("baseFeeValue :" + baseFeeValue);
						$("#baseFeeValue").html(ReplaceNumberWithCommas((baseFeeValue).toFixed(2)));
						$('#baseFeeTr').removeClass('flagvisibility');


						if (baseUsers >= numberUserEvent) {
							$('#totalFeeTr').addClass('flagvisibility');
						} else {
							numberUserEvent = numberUserEvent - baseUsers;
							var totalFeeLabel = currencyCode + " " + price + " X " + numberUserEvent + " Users X " + selectedMonths + " Months";
							$("#totalFeeLabel").html(totalFeeLabel);

							$('#totalFeeTr').removeClass('flagvisibility');
						}
						var totalFeeValue = (price * numberUserEvent * selectedMonths);
						var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
						$("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
						var totalFeeAmount = baseFeeValue + totalFeeValue;
					}
				} else {

					var totalFeeLabel = currencyCode + " " + price + " X " + numberUserEvent;
					if (planType === 'PER_USER') {
						totalFeeLabel += " Users X " + selectedMonths + " Months";
					} else {
						totalFeeLabel += " Events ";
					}
					$("#totalFeeLabel").html(totalFeeLabel);

					var totalFeeValue = (price * numberUserEvent);
					if (planType === 'PER_USER') {
						totalFeeValue = totalFeeValue * selectedMonths;
					}

					// console.log("totalFeeValue :" + totalFeeValue);
					var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
					$("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));

					var totalFeeAmount = totalFeeValue;
				}

				if (planType === 'PER_USER') {
					var totalFeeDiscountLabel = "Subscription Discount " + montlhyDiscount + "%";
					// console.log("totalFeeDiscountLabel :" + totalFeeDiscountLabel);
					$("#totalFeeDiscountLabel").html(totalFeeDiscountLabel);
					var totalFeeDiscountValue = (totalFeeAmount / 100) * montlhyDiscount;
					// console.log("totalFeeDiscountValue :"+ totalFeeDiscountValue);
					var feeDiscountValue = '<input type="hidden" name="feeDiscountValue" value="' + totalFeeDiscountValue + '" >';
					$("#totalFeeDiscountValue").html(feeDiscountValue + "-" + ReplaceNumberWithCommas((totalFeeDiscountValue).toFixed(2)));

					totalFeeAmount = totalFeeAmount - totalFeeDiscountValue;
				} else {
					$('.subsDiscount').hide();
				}
				$('#totalFeePromoLabel').html("No Promotional Code");
				$('#totalFeePromoValue').html("0.00");

				// console.log("totalFeeAmount : "+totalFeeAmount);
				var totalFeeAmountValue = '<input type="hidden" name="totalFeeAmount" value="' + totalFeeAmount + '" >';

				var totalTax = (totalFeeAmount * taxFormt) / 100;
				totalFeeAmount = totalFeeAmount + totalTax;
				$('#renewTaxAmount').html(ReplaceNumberWithCommas((totalTax).toFixed(2)));

				$('#totalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
				if (document.getElementsByClassName("stripe-payment-btn")) {
					var element = document.getElementsByClassName("stripe-payment-btn");
					for (var index = 0; index < element.length; index++) {
						const el = element[index];
						el.setAttribute('amount', ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
					}

				}
			}
		});
	}
}

function updateSubscription(data) {
	data.plan.tax = parseFloat($('#taxFormt').val());
	var formHtml = '<h3>Update ' + data.plan.planName + ' Subscription</h3>';

	formHtml += '<button class="close for-absulate" type="button" data-dismiss="modal">x</button>';
	formHtml += '<input type="hidden" id="updateCurrencyId" value="' + data.plan.currencyCode + '">';
	formHtml += '<input type="hidden" id="updatePlanType" value="' + data.planType + '">';
	if (typeof data.planPeriod !== 'undefined' && data.planPeriod !== '') {
		formHtml += '<input type="hidden" id="updateSelectPeriodId" value="' + data.planPeriod.id + '">';
	}

	formHtml += '<table id="updateRangeTable" class="marg-top-20">';
	var lastRange = 0;
	$(data.plan.rangeList).each(function (i, range) {
		lastRange = range.rangeEnd;
		formHtml += '<tr class="row">';
		formHtml += '<td class="col-md-6" data-id="' + range.id + '" data-start="' + range.rangeStart + '" data-end="' + range.rangeEnd + '" data-price="' + range.price + '">';
		formHtml += '<h4 style="font-weight: bold; color: #3f96d8;">' + range.displayLabel + '' + (data.plan.planType === 'PER_USER' ? " Users" : " Events") + '</h4>';
		formHtml += '</td>';
		formHtml += '<td class="col-md-6"> ' + data.plan.currencyCode;
		formHtml += '<span style="font-size: 23px;">';

		console.log("data plan Base price : " + data.plan.basePrice);
		var itemTax = 0;
		if (data.plan.basePrice !== undefined && i == 0) {
			formHtml += '<input type="hidden" id="updateBasePrice" value="' + data.plan.basePrice + '">';
			formHtml += '<input type="hidden" id="updateBaseUsers" value="' + data.plan.baseUsers + '">';
			formHtml += (data.plan.basePrice).toFixed(2);

			itemTax = parseFloat(((data.plan.basePrice).toFixed(2) * (data.plan.tax).toFixed(2)) / 100) + parseFloat((data.plan.basePrice).toFixed(2));
		} else {
			formHtml += (range.price).toFixed(2);
			itemTax = parseFloat(((range.price).toFixed(2) * (data.plan.tax).toFixed(2)) / 100) + parseFloat((range.price).toFixed(2));
		}
		formHtml += '</span>';
		if (data.plan.basePrice !== undefined && i == 0) {
			formHtml += (data.plan.planType === 'PER_USER' ? " /month" : " /event");
		} else {
			formHtml += (data.plan.planType === 'PER_USER' ? " /user/month" : " /event");
		}


		formHtml += '<br><div">';

		formHtml += '(' + itemTax + ' inclusive of ' + (data.plan.tax).toFixed(2) + '% SST)';

		formHtml += '</div>';
		formHtml += '</td>';
		formHtml += '</tr>';




	});
	formHtml += '</table>';

	formHtml += '<form id="idUpdateSubscribeForm" action="' + getContextPath() + '/buyer/billing/updateSubscription/' + data.id + '" method="post">';
	formHtml += '<input type="hidden" id="lastRange" value="' + lastRange + '">';
	formHtml += '<input type="hidden" id="idUpdateSubscribeFormPlanId" value="' + data.plan.id + '">';
	formHtml += '<div class="row marg-top-20">';
	formHtml += '<div class="col-md-4">';
	formHtml += '<label>Additional ' + (data.plan.planType === 'PER_USER' ? "User" : "Event") + '</label>';
	formHtml += '</div>';
	formHtml += '<div class="col-md-8">';
	formHtml += '<input type="hidden" name="oldNumberUserEvent" id="oldNumberUserEvent" value="' + (data.plan.planType === 'PER_USER' ? data.userQuantity : data.eventQuantity) + '" >';
	formHtml += '<input type="text" class="form-control" name="updateNumberUserEvent" id="updateNumberUserEvent" value="' + (data.plan.planType === 'PER_USER' ? data.userQuantity : data.eventQuantity)
		+ '" data-validation="required length number" data-validation-length="1-3">';
	formHtml += '<input type="hidden" name="updateRangeId" id="updateRangeId">';
	formHtml += '<span id="updateNumberError"></span>';
	formHtml += '</div>';
	formHtml += '</div>';

	formHtml += '<div class="row marg-top-20">';
	formHtml += '<div class="col-md-4"><label>Promo Code</label></div>';
	formHtml += '<div class="col-md-8">';
	formHtml += '<input type="hidden" name="updatePromoCodeId" id="updatePromoCodeId">';
	formHtml += '<input type="text" class="form-control" id="updatePromoCode" name="updatePromoCode">';
	formHtml += '<span id="updatePromoError"></span>';
	formHtml += '</div></div>';

	formHtml += '<div class="row marg-top-20 flagvisibility" id="updateBaseFeeTr" >';
	formHtml += '<div class="col-md-6">	<label id="updateBaseFeeLabel"></label></div>';
	formHtml += '<div class="col-md-3 align-right"><label id="updateBaseFeeValue"></label></div>';
	formHtml += '</div>';

	formHtml += '<div class="row marg-top-20" id="updateTotalFeeTr">';
	formHtml += '<div class="col-md-6">	<label id="updateTotalFeeLabel"></label></div>';
	formHtml += '<div class="col-md-3 align-right"><label id="updateTotalFeeValue"></label></div>';
	formHtml += '</div>';

	/*formHtml += '<div class="row marg-top-20 subsDiscount">';
	formHtml += '<div class="col-md-6"><label id="updateTotalFeeDiscountLabel"></label></div>';
	formHtml += '<div class="col-md-6"><label id="updateTotalFeeDiscountValue"> </label></div>';
	formHtml += '</div>';*/

	//label that shows approver user
	if (data.plan.planType === 'PER_USER') {
		formHtml += '<div class="row marg-top-20">';
		formHtml += '<div class="col-md-6"><label id="updateTotalFeeApproverLabel">' + data.userQuantity + ' approver users </label></div>';
		formHtml += '<div class="col-md-3 align-right"><label > 0.00</label></div>';
		formHtml += '</div>';
	}
	formHtml += '<div class="row marg-top-20">';
	formHtml += '<div class="col-md-6"><label id="updateTotalFeePromoLabel"></label></div>';
	formHtml += '<div class="col-md-3 align-right"><label id="updateTotalFeePromoValue"> </label></div>';
	formHtml += '</div>';

	formHtml += '<div class="row marg-top-20">';
	formHtml += '<div class="col-md-6"><label>Tax ' + data.plan.tax + '% SST</label></div>';
	formHtml += '<div class="col-md-3 align-right"><label id="updateTaxAmount"></label></div>';
	formHtml += '</div>';

	formHtml += '<div class="row marg-top-20">';
	formHtml += '<div class="col-md-6" style="padding-right: 0px;"><label>Total Fee</label></div>';
	formHtml += '<div class="col-md-3 align-right"><label class="marg-right-10">' + data.currencyCode + '</label><label id="updateTotalFeeAmount"></label></div>';
	formHtml += '</div>';

	formHtml += '<div class="row marg-top-20">';
	formHtml += '<div class="col-md-3">&nbsp;</div><div class="col-md-6"><div id="idUpdateButtonHolder_old"><button type="button" value="Pay" id="pay_' + data.id + '" plan-id="' + data.id + '" currency-code="' + data.plan.currencyCode + '"class="margin-bottom-15 btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out full-width stripe-payment-btn">Pay</button></div></div><div class="col-md-3">&nbsp;</div>';
	formHtml += '</div>';
	formHtml += '<div class="row">';
	formHtml += '<div class="col-md-2">&nbsp;</div><div class="col-md-10">';
	// formHtml += '<img src="https://www.paypalobjects.com/webstatic/en_US/i/buttons/cc-badges-ppppcmcvdam.png" alt="Credit Card Badges">';
	formHtml += '</div>';
	formHtml += '</div>';
	formHtml += '</form>';
	// it shows note for approver user offer
	if (data.plan.planType === 'PER_USER') {
		formHtml += '<div class="col-md-12 " style="margin-top: 15px;">';
		formHtml += '<span style="color: red;"> * </span>';
		formHtml += 'Note : For	<span id="userNo1">' + data.userQuantity + ' users</span> you will get	<span id="appNo1">' + data.userQuantity + ' approver users</span>	free.';
		formHtml += '</div>';
	}
	$('#UpdateSubsDialogText').html(formHtml);

	$("#idUpdateSubsDialog").modal();

	window.paypalCheckoutReady = function () {
		paypal.checkout.setup('${merchantId}', {
			environment: '${paypalEnvironment}', container: 'idUpdateSubscribeForm', condition: function () {
				return $('#idUpdateSubscribeForm').isValid();
			},
			// button: 'placeOrderBtn'
			buttons: [{ container: 'idUpdateButtonHolder', type: 'checkout', color: 'blue', size: 'medium', shape: 'rect' }]
		});
	};
}

function calculateUpdateTotalFee(chargeMonths) {
	var updateNumberUserEvent = parseInt($('#updateNumberUserEvent').val());
	var TotalNumberUserEvent = parseInt($('#oldNumberUserEvent').val()) + updateNumberUserEvent;
	var updatePlanType = $('#updatePlanType').val();
	//	console.log("updateNumberUserEvent :" + updateNumberUserEvent + " updatePlanType :" + updatePlanType);
	var updateCurrencyCode = $('#updateCurrencyId').val();
	var updatePromoCode = $('#updatePromoCode').val();
	//console.log("updateCurrencyCode :" + updateCurrencyCode + " updatePromoCode :" + updatePromoCode);
	var updateSelectedMonths = 0;

	if (updatePlanType === 'PER_USER') {
		updateSelectedMonths = parseInt(chargeMonths);
	}
	//	console.log("updateSelectedMonths :" + updateSelectedMonths);
	ajaxCallOnChangeFieldsForUpdateSubs(updateNumberUserEvent, updatePromoCode, updateSelectedMonths, updateCurrencyCode, updatePlanType, TotalNumberUserEvent);
}

function ajaxCallOnChangeFieldsForUpdateSubs(updateNumberUserEvent, updatePromoCode, updateSelectedMonths, updateCurrencyCode, updatePlanType, TotalNumberUserEvent) {
	var basePrice = $('#updateBasePrice').val();
	var baseUsers = $('#updateBaseUsers').val();
	var taxFormt = parseFloat($('#taxFormt').val());
	var planId = '';
	if ($('#idUpdateSubscribeForm')) {
		planId = $('#idUpdateSubscribeFormPlanId').val()
	}
	console.log("basePrice :" + basePrice + "== baseUsers :" + baseUsers);
	var lastRange = parseInt($('#lastRange').val());
	if (updatePromoCode !== 'undefined' && updatePromoCode !== '') {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type: "GET", url: getContextPath() + "/buyer/billing/getPromoCode", data: {
				promoCode: updatePromoCode,
				plan: planId,
				totalPrice: (Number($('input[name=updateTotalFeeAmount]').val()) + Number($('#updateTaxAmount').html().replace(/,/g, '')))
			}, beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			}, complete: function () {
				$('#loading').hide();
			}, success: function (data) {
				// console.log(data);
				$('#updateRangeTable tr').each(function () {
					var rangeId = $(this).children('td:first').attr('data-id');
					var start = parseInt($(this).children('td:first').attr('data-start'));
					var end = parseInt($(this).children('td:first').attr('data-end'));
					var price = parseInt($(this).children('td:first').attr('data-price'));
					console.log("start :" + start + " End :" + end + " Price :" + price + " noOfUser :" + TotalNumberUserEvent);

					if (TotalNumberUserEvent >= start && TotalNumberUserEvent <= end) {
						//					console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfUser1 :"+ updateNumberUserEvent);

						$('#updateRangeId').val(rangeId);
						$('#updatePromoCodeId').val(data.id);

						var baseFeeValue = 0;
						if (basePrice !== undefined) {
							if (updatePlanType === 'PER_USER') {

								//			    		basePrice = parseInt(basePrice);
								//			    		var baseFeeLabel = updateCurrencyCode + " "+ basePrice + " for " + baseUsers + " Users X " + updateSelectedMonths + " Months";
								//			    		$("#updateBaseFeeLabel").html(baseFeeLabel);
								//			    		
								//			    		baseFeeValue = (basePrice * updateSelectedMonths);
								//				    	console.log("baseFeeValue :" + baseFeeValue);
								//				    	$("#updateBaseFeeValue").html(ReplaceNumberWithCommas((baseFeeValue).toFixed(2)));
								//			    		$('#updateBaseFeeTr').removeClass('flagvisibility');
								//			    		
								//			    		
								//			    		if(baseUsers >= TotalNumberUserEvent ){
								//				    		$('#updateTotalFeeTr').addClass('flagvisibility');
								//				    	}else{
								//				    		updateNumberUserEvent = TotalNumberUserEvent - baseUsers;
								var totalFeeLabel = updateCurrencyCode + " " + price + " X " + updateNumberUserEvent + " Users X " + updateSelectedMonths + " Months";
								$("#updateTotalFeeLabel").html(totalFeeLabel);

								$('#updateTotalFeeTr').removeClass('flagvisibility');
								//				    	}
								var totalFeeValue = (price * updateNumberUserEvent * updateSelectedMonths);
								var feeValue = '<input type="hidden" name="updateFeeValue" value="' + totalFeeValue + '" >';
								var chargeMonths = '<input type="hidden" name="chargeMonths" value="' + updateSelectedMonths + '" > ';
								$("#updateTotalFeeValue").html(chargeMonths + feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
								var totalFeeAmount = baseFeeValue + totalFeeValue;
								console.log("totalFeeAmount :" + totalFeeAmount);
							}
						} else {

							var totalFeeLabel = updateCurrencyCode + " " + price + " X " + updateNumberUserEvent;
							if (updatePlanType === 'PER_USER') {
								totalFeeLabel += " Users X " + updateSelectedMonths + " Months";
							} else {
								totalFeeLabel += " Events ";
							}
							$("#updateTotalFeeLabel").html(totalFeeLabel);

							var totalFeeValue = (price * updateNumberUserEvent);
							if (updatePlanType === 'PER_USER') {
								totalFeeValue = totalFeeValue * updateSelectedMonths;
							}

							var feeValue = '<input type="hidden" name="updateFeeValue" value="' + totalFeeValue + '" >';
							var chargeMonths = '<input type="hidden" name="chargeMonths" value="' + updateSelectedMonths + '" > ';
							$("#updateTotalFeeValue").html(chargeMonths + feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));

							var totalFeeAmount = totalFeeValue;
							console.log("totalFeeAmount :" + totalFeeAmount);
						}

						//					var totalFeeLabel = updateCurrencyCode + " " + price + " X " + updateNumberUserEvent;
						//					if (updatePlanType === 'PER_USER') {
						//						totalFeeLabel += " Users X " + updateSelectedMonths + " Months";
						//					} else {
						//						totalFeeLabel += " Events ";
						//					}
						//					$("#updateTotalFeeLabel").html(totalFeeLabel);
						//
						//					var totalFeeValue = (price * updateNumberUserEvent);
						//					if (updatePlanType === 'PER_USER') {
						//						totalFeeValue = totalFeeValue * updateSelectedMonths;
						//					}
						//
						//					// console.log("totalFeeValue :" + totalFeeValue);
						//					var feeValue = '<input type="hidden" name="updateFeeValue" value="' + totalFeeValue + '" >';
						//					var chargeMonths = '<input type="hidden" name="chargeMonths" value="' + updateSelectedMonths + '" > ';
						//					$("#updateTotalFeeValue").html(chargeMonths + feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
						//
						//					var totalFeeAmount = totalFeeValue;
						//					


						var totalFeePromoValue = 0;
						var totalFeePromoLabel = data.promoName;
						if (data.discountType !== 'undefined' && data.discountType === 'PERCENTAGE') {
							totalFeePromoValue = (totalFeeAmount / 100) * data.promoDiscount;
							totalFeePromoLabel += "-" + data.promoDiscount + " % OFF";
						} else {
							totalFeePromoValue = data.promoDiscount;
							totalFeePromoLabel += "- " + updateCurrencyCode + " " + data.promoDiscount + " OFF";
						}
						$('#updateTotalFeePromoLabel').html(totalFeePromoLabel);
						var promoCodeDiscount = '<input type="hidden" name="updatePromoCodeDiscount" value="' + totalFeePromoValue + '" >';
						console.log(totalFeePromoValue);
						$('#updateTotalFeePromoValue').html(promoCodeDiscount + "-" + ReplaceNumberWithCommas((totalFeePromoValue).toFixed(2)));
						if (planType === 'PER_USER') {
							totalFeeAmount = (totalFeeAmount - totalFeeDiscountValue) - totalFeePromoValue;
						} else {
							totalFeeAmount = totalFeeAmount - totalFeePromoValue;
						}

						var totalTax = (totalFeeAmount * taxFormt) / 100;
						totalFeeAmount = totalFeeAmount + totalTax;
						console.log("totalFeeAmount : " + totalFeeAmount);
						var totalFeeAmountValue = '<input type="hidden" name="updateTotalFeeAmount" value="' + totalFeeAmount + '" >';
						$('#updateTaxAmount').html(ReplaceNumberWithCommas((totalTax).toFixed(2)));
						$('#updateTotalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
						if (document.getElementsByClassName("stripe-payment-btn")) {
							var element = document.getElementsByClassName("stripe-payment-btn");
							for (var index = 0; index < element.length; index++) {
								const el = element[index];
								el.setAttribute('amount', ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
							}

						}
					} else if (TotalNumberUserEvent >= lastRange) {
						$('#updateNumberError').html('<span class="help-block form-error">Total user should be less then ' + lastRange + '</span>').addClass('has-error');
						return false;
					}
				});

			}, error: function (request, textStatus, errorThrown) {
				$('#updateRangeTable tr').each(function () {
					var rangeId = $(this).children('td:first').attr('data-id');
					var start = parseInt($(this).children('td:first').attr('data-start'));
					var end = parseInt($(this).children('td:first').attr('data-end'));
					var price = parseInt($(this).children('td:first').attr('data-price'));
					// console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfUser :"+ noOfUser);

					if (TotalNumberUserEvent >= start && TotalNumberUserEvent <= end) {
						//					console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfUser1 :"+ updateNumberUserEvent);

						$('#updateRangeId').val(rangeId);

						var baseFeeValue = 0;
						if (basePrice !== undefined) {
							if (updatePlanType === 'PER_USER') {

								//			    		basePrice = parseInt(basePrice);
								//			    		var baseFeeLabel = updateCurrencyCode + " "+ basePrice + " for " + baseUsers + " Users X " + updateSelectedMonths + " Months";
								//			    		$("#updateBaseFeeLabel").html(baseFeeLabel);
								//			    		
								//			    		baseFeeValue = (basePrice * updateSelectedMonths);
								//				    	console.log("baseFeeValue :" + baseFeeValue);
								//				    	$("#updateBaseFeeValue").html(ReplaceNumberWithCommas((baseFeeValue).toFixed(2)));
								//			    		$('#updateBaseFeeTr').removeClass('flagvisibility');
								//			    		
								//			    		
								//			    		if(baseUsers >= TotalNumberUserEvent ){
								//				    		$('#updateTotalFeeTr').addClass('flagvisibility');
								//				    	}else{
								//				    		updateNumberUserEvent = TotalNumberUserEvent - baseUsers;
								var totalFeeLabel = updateCurrencyCode + " " + price + " X " + updateNumberUserEvent + " Users X " + updateSelectedMonths + " Months";
								$("#updateTotalFeeLabel").html(totalFeeLabel);

								$('#updateTotalFeeTr').removeClass('flagvisibility');
								//				    	}
								var totalFeeValue = (price * updateNumberUserEvent * updateSelectedMonths);
								var feeValue = '<input type="hidden" name="updateFeeValue" value="' + totalFeeValue + '" >';
								var chargeMonths = '<input type="hidden" name="chargeMonths" value="' + updateSelectedMonths + '" > ';
								$("#updateTotalFeeValue").html(chargeMonths + feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
								var totalFeeAmount = baseFeeValue + totalFeeValue;
								//			    		console.log("totalFeeAmount :" + totalFeeAmount );
							}
						} else {

							var totalFeeLabel = updateCurrencyCode + " " + price + " X " + updateNumberUserEvent;
							if (updatePlanType === 'PER_USER') {
								totalFeeLabel += " Users X " + updateSelectedMonths + " Months";
							} else {
								totalFeeLabel += " Events ";
							}
							$("#updateTotalFeeLabel").html(totalFeeLabel);

							var totalFeeValue = (price * updateNumberUserEvent);
							if (updatePlanType === 'PER_USER') {
								totalFeeValue = totalFeeValue * updateSelectedMonths;
							}

							//						 console.log("totalFeeValue :" + totalFeeValue);
							var feeValue = '<input type="hidden" name="updateFeeValue" value="' + totalFeeValue + '" >';
							var chargeMonths = '<input type="hidden" name="chargeMonths" value="' + updateSelectedMonths + '" > ';
							$("#updateTotalFeeValue").html(chargeMonths + feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));

							var totalFeeAmount = totalFeeValue;
						}

						//					var totalFeeLabel = updateCurrencyCode + " " + price + " X " + updateNumberUserEvent;
						//					if (updatePlanType === 'PER_USER') {
						//						totalFeeLabel += " Users X " + updateSelectedMonths + " Months";
						//					} else {
						//						totalFeeLabel += " Events ";
						//					}
						//					$("#updateTotalFeeLabel").html(totalFeeLabel);
						//
						//					var totalFeeValue = (price * updateNumberUserEvent);
						//					if (updatePlanType === 'PER_USER') {
						//						totalFeeValue = totalFeeValue * updateSelectedMonths;
						//					}
						//
						//					// console.log("totalFeeValue :" + totalFeeValue);
						//					var feeValue = '<input type="hidden" name="updateFeeValue" value="' + totalFeeValue + '" >';
						//					var chargeMonths = '<input type="hidden" name="chargeMonths" value="' + updateSelectedMonths + '" > ';
						//					$("#updateTotalFeeValue").html(chargeMonths + feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
						//
						//					var totalFeeAmount = totalFeeValue;
						//					
						$('#updateTotalFeePromoLabel').html("No Promotional Code");
						$('#updateTotalFeePromoValue').html("0.00");

						// console.log("totalFeeAmount : "+totalFeeAmount);
						var totalTax = (totalFeeAmount * taxFormt) / 100;
						totalFeeAmount = totalFeeAmount + totalTax;
						var totalFeeAmountValue = '<input type="hidden" name="updateTotalFeeAmount" value="' + totalFeeAmount + '" >';
						$('#updateTaxAmount').html(ReplaceNumberWithCommas((totalTax).toFixed(2)));
						$('#updateTotalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
						if (document.getElementsByClassName("stripe-payment-btn")) {
							var element = document.getElementsByClassName("stripe-payment-btn");
							for (var index = 0; index < element.length; index++) {
								const el = element[index];
								el.setAttribute('amount', ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
							}

						}
					} else if (TotalNumberUserEvent >= lastRange) {
						$('#updateNumberError').html('<span class="help-block form-error">Total user should be less then ' + lastRange + '</span>').addClass('has-error');
						return false;
					}
				});
				$('#updatePromoError').html('<span class="help-block form-error">' + request.getResponseHeader('error') + '</span>').addClass('has-error');
			}

		});
	} else {
		$('#updateRangeTable tr').each(function () {
			var rangeId = $(this).children('td:first').attr('data-id');
			var start = parseInt($(this).children('td:first').attr('data-start'));
			var end = parseInt($(this).children('td:first').attr('data-end'));
			var price = parseInt($(this).children('td:first').attr('data-price'));
			console.log("start :" + start + " End :" + end + " Price :" + price + " TotalNumberUserEvent :" + TotalNumberUserEvent);

			if (TotalNumberUserEvent >= start && TotalNumberUserEvent <= end) {
				//				console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfUser1 :"+ updateNumberUserEvent);

				$('#updateRangeId').val(rangeId);


				var baseFeeValue = 0;
				if (basePrice !== undefined) {
					if (updatePlanType === 'PER_USER') {

						//		    		basePrice = parseInt(basePrice);
						//		    		var baseFeeLabel = updateCurrencyCode + " "+ basePrice + " for " + baseUsers + " Users X " + updateSelectedMonths + " Months";
						//		    		$("#updateBaseFeeLabel").html(baseFeeLabel);
						//		    		
						//		    		baseFeeValue = (basePrice * updateSelectedMonths);
						//			    	console.log("baseFeeValue :" + baseFeeValue);
						//			    	$("#updateBaseFeeValue").html(ReplaceNumberWithCommas((baseFeeValue).toFixed(2)));
						//		    		$('#updateBaseFeeTr').removeClass('flagvisibility');


						//		    		if(baseUsers >= TotalNumberUserEvent ){
						//			    		$('#updateTotalFeeTr').addClass('flagvisibility');
						//			    	}else{
						//updateNumberUserEvent = TotalNumberUserEvent - baseUsers;
						var totalFeeLabel = updateCurrencyCode + " " + price + " X " + updateNumberUserEvent + " Users X " + updateSelectedMonths + " Months";
						$("#updateTotalFeeLabel").html(totalFeeLabel);

						$('#updateTotalFeeTr').removeClass('flagvisibility');
						//			    	}


						console.log("price :" + price);
						var totalFeeValue = (price * updateNumberUserEvent * updateSelectedMonths);
						var feeValue = '<input type="hidden" name="updateFeeValue" value="' + totalFeeValue + '" >';
						var chargeMonths = '<input type="hidden" name="chargeMonths" value="' + updateSelectedMonths + '" > ';
						$("#updateTotalFeeValue").html(chargeMonths + feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
						var totalFeeAmount = baseFeeValue + totalFeeValue;
						console.log("totalFeeAmount :" + totalFeeAmount);
					}
				} else {

					var totalFeeLabel = updateCurrencyCode + " " + price + " X " + updateNumberUserEvent;
					if (updatePlanType === 'PER_USER') {
						totalFeeLabel += " Users X " + updateSelectedMonths + " Months";
					} else {
						totalFeeLabel += " Events ";
					}
					$("#updateTotalFeeLabel").html(totalFeeLabel);

					var totalFeeValue = (price * updateNumberUserEvent);
					if (updatePlanType === 'PER_USER') {
						totalFeeValue = totalFeeValue * updateSelectedMonths;
					}

					//					 console.log("totalFeeValue :" + totalFeeValue);
					var feeValue = '<input type="hidden" name="updateFeeValue" value="' + totalFeeValue + '" >';
					var chargeMonths = '<input type="hidden" name="chargeMonths" value="' + updateSelectedMonths + '" > ';
					$("#updateTotalFeeValue").html(chargeMonths + feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));

					var totalFeeAmount = totalFeeValue;
				}

				$('#updateTotalFeePromoLabel').html("No Promotional Code");
				$('#updateTotalFeePromoValue').html("0.00");


				var totalTax = (totalFeeAmount * taxFormt) / 100;
				totalFeeAmount = totalFeeAmount + totalTax;
				var totalFeeAmountValue = '<input type="hidden" name="updateTotalFeeAmount" value="' + totalFeeAmount + '" >';
				$('#updateTaxAmount').html(ReplaceNumberWithCommas((totalTax).toFixed(2)));


				console.log("totalFeeAmount : " + totalFeeAmount);
				$('#updateTotalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
				if (document.getElementsByClassName("stripe-payment-btn")) {
					var element = document.getElementsByClassName("stripe-payment-btn");
					for (var index = 0; index < element.length; index++) {
						const el = element[index];
						el.setAttribute('amount', ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
					}

				}
			}
			else if (TotalNumberUserEvent >= lastRange) {
				$('#updateNumberError').html('<span class="help-block form-error">Total user should be less then ' + lastRange + '</span>').addClass('has-error');
				return false;
			}
		});
	}
}

function updateFields(chargeMonths) {
	$('#updatePromoCode').on('change', function (e) {
		var updateNumberUserEvent = parseInt($('#updateNumberUserEvent').val());
		$('#updatePromoError').html('').removeClass('has-error');
		$('#updateNumberError').html('').removeClass('has-error');
		if ($('#updateNumberUserEvent').val() === 'undefined' || $('#updateNumberUserEvent').val() === '0') {
			// console.log("numberUserEvent :"+$(this).val());
			$('#updateNumberError').html('<span class="help-block form-error">Number should be greater then 0</span>').addClass('has-error');
			return false;
		}
		if (isNaN(updateNumberUserEvent)) {
			$('#updateNumberError').html('<span class="help-block form-error">It should be correct number</span>').addClass('has-error');
			return false;
		}

		var updateNumberUserEvent = parseInt($('#updateNumberUserEvent').val());
		var TotalNumberUserEvent = parseInt($('#oldNumberUserEvent').val()) + updateNumberUserEvent;
		var updatePlanType = $('#updatePlanType').val();
		//		console.log("updateNumberUserEvent :" + updateNumberUserEvent + " updatePlanType :" + updatePlanType);
		var updateCurrencyCode = $('#updateCurrencyId').val();
		var updatePromoCode = $(this).val();
		//console.log("updateCurrencyCode :" + updateCurrencyCode + " updatePromoCode :" + updatePromoCode);
		var updateSelectedMonths = 0;

		if (updatePlanType === 'PER_USER') {
			updateSelectedMonths = parseInt(chargeMonths);
		}
		//		console.log("updateSelectedMonths :" + updateSelectedMonths);
		ajaxCallOnChangeFieldsForUpdateSubs(updateNumberUserEvent, updatePromoCode, updateSelectedMonths, updateCurrencyCode, updatePlanType, TotalNumberUserEvent);

	});


	$(document).on("keyup", "#updateNumberUserEvent", function (e) {
		var updateNumberUserEvent = parseInt($(this).val());
		var TotalNumberUserEvent = parseInt($('#oldNumberUserEvent').val()) + updateNumberUserEvent;
		$('#updatePromoError').html('').removeClass('has-error');
		$('#updateNumberError').html('').removeClass('has-error');
		if ($(this).val() === 'undefined' || $(this).val() === '0') {
			// console.log("numberUserEvent :"+$(this).val());
			$('#updateNumberError').html('<span class="help-block form-error">Number should be greater then 0</span>').addClass('has-error');
			return false;
		}
		if (isNaN(updateNumberUserEvent)) {
			$('#updateNumberError').html('<span class="help-block form-error">It should be correct number</span>').addClass('has-error');
			return false;
		}

		var lastRange = parseInt($('#lastRange').val());
		console.log("last range: " + lastRange);
		if (updateNumberUserEvent > lastRange) {
			$('#updateNumberError').html('<span class="help-block form-error">It should be less then ' + lastRange + '</span>').addClass('has-error');
			return false;
		}
		console.log("updateNumberUserEvent 1 : " + updateNumberUserEvent);
		var updateNumberUserEvent = parseInt($('#updateNumberUserEvent').val());
		console.log("updateNumberUserEvent 2 : " + updateNumberUserEvent);
		var updatePlanType = $('#updatePlanType').val();
		//		console.log("updateNumberUserEvent :" + updateNumberUserEvent + " updatePlanType :" + updatePlanType);
		var updateCurrencyCode = $('#updateCurrencyId').val();
		var updatePromoCode = $('#updatePromoCode').val();
		//console.log("updateCurrencyCode :" + updateCurrencyCode + " updatePromoCode :" + updatePromoCode);
		var updateSelectedMonths = 0;

		if (updatePlanType === 'PER_USER') {
			// setting free approver user *Note
			$("#updateTotalFeeApproverLabel").html(updateNumberUserEvent + ' approver users ');
			$('#userNo1').text(updateNumberUserEvent + " users");
			$('#appNo1').text(updateNumberUserEvent + " approver users");

			updateSelectedMonths = parseInt(chargeMonths);
		}
		//		console.log("updateSelectedMonths :" + updateSelectedMonths);
		ajaxCallOnChangeFieldsForUpdateSubs(updateNumberUserEvent, updatePromoCode, updateSelectedMonths, updateCurrencyCode, updatePlanType, TotalNumberUserEvent);
	});
}

function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}

$(document).on("click", ".stripe-payment-btn", function (e) {

	if (($(this).attr('mode') == 'renew' && $('#idRenewSubscribeForm').isValid())
		|| $('#supplierCheckoutForm').isValid() && $(this).attr('mode') != 'renew') {
		e.preventDefault();
		// $(this).prop('disabled', true);
		$(this).addClass('disabled');
		$('#makePaymentModal').attr('payment-amount', $('#eventParticipationFeesId').val())
		$('#makePaymentModal').attr('plan-id', $(this).attr('plan-id'));
		$('#makePaymentModal').attr('currency-code', $(this).attr('currency-code'));
		$('#makePaymentModal').attr('mode', $(this).attr('mode'));
		$('#checkoutCardAmount').html($(this).attr('currency-code') + ' ' + $(this).attr('amount'));
		$('#checkoutFpxAmount').html($(this).attr('currency-code') + ' ' + $(this).attr('amount'));
		if ($(this).attr('currency-code') != 'MYR') {
			$('#tabTwoIdParent').addClass('hidden')
		} else {
			$('#tabTwoIdParent').removeClass('hidden')
		}
		$('#makePaymentModal').modal().show();
		$('#tabOneId').click();
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();

		$('#idRenewDialog').is(':visible') ? $("#idRenewDialog").modal("toggle") : '';
		$('#idUpdateSubsDialog').is(':visible') ? $("#idUpdateSubsDialog").modal("toggle") : '';



	}


});

function payByFpx(header, token, event, fpxBank) {
	var data = {};
	data['id'] = null;


	var url = null;

	if ($('#makePaymentModal').attr('mode') === 'renew') {
		url = getContextPath() + '/buyer/billing/renew/' + $('#makePaymentModal').attr('plan-id') +
			'?numberUserEvent=' + $('#numberUserEvent').val() +
			'&periodId=' + $("input[name=periodId]:checked").val() +
			'&rangeId=' + $("input[name=rangeId]").val() +
			'&promoCodeId=' + $('#promoCodeId').val() +
			'&feeDiscountValue=' + Number($("input[name=feeDiscountValue]").val() ? $("input[name=feeDiscountValue]").val() : 0) +
			'&feeValue=' + Number($("input[name=feeValue]").val() ? $("input[name=feeValue]").val() : 0) +
			'&promoCodeDiscount=' + Number($("input[name=promoCodeDiscount]").val() ? $("input[name=promoCodeDiscount]").val() : 0) +
			'&totalFeeAmount=' + Number($("input[name=totalFeeAmount]").val() ? $("input[name=totalFeeAmount]").val() : 0) +
			'&mode=' + 'fpx' +
			'&autoChargeSubscription=' + ($("input[id=autoChargeSubscription]:checked").val() == "on" ? true : false);
	} else {
		url = getContextPath() + '/buyer/billing/updateSubscription/' + $('#makePaymentModal').attr('plan-id') +
			'?updateNumberUserEvent=' + $('#updateNumberUserEvent').val() +
			'&chargeMonths=' + $("input[name=chargeMonths]").val() +
			'&updatePeriodId=' + null +
			'&updateRangeId=' + $('#updateRangeId').val() +
			'&updatePromoCodeId=' + $('#updatePromoCodeId').val() +
			'&updateFeeValue=' + Number($("input[name=updateFeeValue]").val() ? $("input[name=updateFeeValue]").val() : 0) +
			'&updateFeeDiscountValue=' + 0 +
			'&updatePromoCodeDiscount=' + Number($("input[name=updatePromoCodeDiscount]").val() ? $("input[name=updatePromoCodeDiscount]").val() : 0) +
			'&updateTotalFeeAmount=' + Number($("input[name=updateTotalFeeAmount]").val()) +
			'&mode=' + 'fpx' +
			'&promocode=' + $('#updatePromoCodeId').val()
	}


	$.ajax({
		type: "POST",
		url: url,
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
					showErrorForPayment(result.error.message);
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

	var url = null;
	if ($('#makePaymentModal').attr('mode') === 'renew') {
		url = getContextPath() + '/buyer/billing/renew/' + $('#makePaymentModal').attr('plan-id') +
			'?numberUserEvent=' + $('#numberUserEvent').val() +
			'&periodId=' + $("input[name=periodId]:checked").val() +
			'&rangeId=' + $("input[name=rangeId]").val() +
			'&promoCodeId=' + $('#promoCodeId').val() +
			'&feeDiscountValue=' + Number($("input[name=feeDiscountValue]").val() ? $("input[name=feeDiscountValue]").val() : 0) +
			'&feeValue=' + Number($("input[name=feeValue]").val() ? $("input[name=feeValue]").val() : 0) +
			'&promoCodeDiscount=' + Number($("input[name=promoCodeDiscount]").val() ? $("input[name=promoCodeDiscount]").val() : 0) +
			'&totalFeeAmount=' + Number($("input[name=totalFeeAmount]").val() ? $("input[name=totalFeeAmount]").val() : 0) +
			'&mode=' + 'card' +
			'&autoChargeSubscription=' + ($("input[id=autoChargeSubscription]:checked").val() == "on" ? true : false);
	} else {
		url = getContextPath() + '/buyer/billing/updateSubscription/' + $('#makePaymentModal').attr('plan-id') +
			'?updateNumberUserEvent=' + $('#updateNumberUserEvent').val() +
			'&chargeMonths=' + $("input[name=chargeMonths]").val() +
			'&updatePeriodId=' + null +
			'&updateRangeId=' + $('#updateRangeId').val() +
			'&updatePromoCodeId=' + $('#updatePromoCodeId').val() +
			'&updateFeeValue=' + Number($("input[name=updateFeeValue]").val() ? $("input[name=updateFeeValue]").val() : 0) +
			'&updateFeeDiscountValue=' + 0 +
			'&updatePromoCodeDiscount=' + Number($("input[name=updatePromoCodeDiscount]").val() ? $("input[name=updatePromoCodeDiscount]").val() : 0) +
			'&updateTotalFeeAmount=' + Number($("input[name=updateTotalFeeAmount]").val()) +
			'&mode=' + 'card' +
			'&promocode=' + $('#updatePromoCodeId').val();
	}


	$.ajax({
		type: "POST",
		url: url,
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
					showErrorForPayment(result.error.message);
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