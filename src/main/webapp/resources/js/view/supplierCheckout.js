var stripe = null;
var elements = null;

$.formUtils.addValidator({
	name: 'crn_number',
	validatorFunction: function (value, $el, config, language, $form) {
		var countryID = $('#idRegCountry').val();
		var crnNum = $('#idCompRegNum').val();
		var response = true;

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		$.ajax({
			url: getContextPath() + "/checkRegistrationNumber",
			data: {
				countryID: countryID,
				crnNum: crnNum
			},
			async: false,
			beforeSend: function (xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function (data) {
				console.log(data);
				response = true;
			},
			error: function (request, textStatus, errorThrown) {
				console.log(textStatus);
				response = false;
			}
		});
		return response;
	},
	errorMessage: 'Company Registration Number Already registered in the system',
	errorMessageKey: 'badCrnNumber'
});

$.formUtils.addValidator({
	name: 'company_name',
	validatorFunction: function (value, $el, config, language, $form) {
		var countryID = $('#idRegCountry').val();
		var companyName = $('#idCompanyName').val();
		var response = true;
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url: getContextPath() + "/checkCompanyNameExis",
			data: {
				countryID: countryID,
				companyName: companyName
			},
			async: false,
			beforeSend: function (xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function (data) {
				console.log(data);
				response = true;
			},
			error: function (request, textStatus, errorThrown) {
				console.log(textStatus);
				response = false;
			}
		});
		return response;
	},
	errorMessage: 'Company Name Already registered in the system',
	errorMessageKey: 'badCompanyName'
});

$.formUtils.addValidator({
	name: 'login_email',
	validatorFunction: function (value, $el, config, language, $form) {
		var loginEmailId = $('#idLoginEmail').val();
		var response = true;
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		// alert(loginId);
		$.ajax({
			url: getContextPath() + "/checkLoginEmail",
			data: {
				loginEmailId: loginEmailId
			},
			async: false,
			beforeSend: function (xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function (data) {
				console.log(data);
				response = true;
			},
			error: function (request, textStatus, errorThrown) {
				console.log(textStatus);
				response = false;
			}
		});
		return response;
	},
	errorMessage: 'Login Email Already registered in the system',
	errorMessageKey: 'badLoginEmail'
});

$('input[name="supplerPlan"]:radio').on('change', function (e) {
	var promoCode = $('#promocode').val();
	var selectedPlan = $(this).val();

	if (selectedPlan === 'SINGLEBUYER') {
		$("#toptitle").text("SINGLE BUYER");

		$('#submitPayment').css({
			'display': 'block'
		});
		$('#paypalPayment').css({
			'display': 'none'
		});
	} else {
		$("#toptitle").text("UNLIMITED BUYER");

		$('#submitPayment').css({
			'display': 'none'
		});
		$('#paypalPayment').css({
			'display': 'block'
		});
	}

	calculateSupplierPlan(promoCode, selectedPlan);
});

$('.submitSuppButton').on('click', function (e) {
	e.preventDefault();
	if (!$('#supplierCheckoutForm').isValid()) {
		return false;
	}
	$('#supplierCheckoutForm').attr('action', getContextPath() + "/suppliersubscription/supplierCheckout");
	$('#supplierCheckoutForm').submit();
});

$('#promocode').on('change', function (e) {
	$('#promoError').html('').removeClass('has-error');
	var promoCode = $(this).val();
	var selectedPlan = $("input[name=supplerPlan]:checked").val();
	calculateSupplierPlan(promoCode, selectedPlan);

});

$('#idRegCountry').on('change', function (e) {
	$('#promoError').html('').removeClass('has-error');
	var promoCode = $('#promocode').val();
	var selectedPlan = $("input[name=supplerPlan]:checked").val();
	calculateSupplierPlan(promoCode, selectedPlan);

});

function calculateSupplierPlan(promoCode, selectedPlan) {
	var countryId = $("#idRegCountry option:selected").val();
	url = getContextPath() + "/suppliersubscription/getSupplierPrice";
	$.ajax({
		type: "GET",
		url: url,
		data: {
			'promoCode': promoCode,
			'selectedPlan': selectedPlan,
			'countryId': countryId
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

	if (promoDiscountPrice) {
		var promoDiscountHtml = '<input type="hidden" name="promoDiscountPrice" value="' + promoDiscountPrice + '" >' + ReplaceNumberWithCommas(promoDiscountPrice.toFixed(2));
		$('#promoDiscountPrice').html(promoDiscountHtml);
	}

	if (totalPrice) {
		var totalHtml = 'US$ <input type="hidden" name="totalPrice" id="totalPriceInput" value="' + totalPrice + '" >' + ReplaceNumberWithCommas(totalPrice.toFixed(2));
		$('#totalPrice').html(totalHtml);
		$('#amount_span').html(totalPrice.toFixed(2))
	}

	$('#tax_span').html(data.tax);
}

function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}

$(document).ready(function () {
	stripe = Stripe($('#stripePublishKey').val());
	elements = stripe.elements()
	$("#toptitle").text("UNLIMITED BUYER");
	calculateSupplierPlan('', 'ALLBUYER');

	$(document).on('submit', '#payment-form-fpx', function (event) {
		var fpxBank = elements.getElement('fpxBank');
		event.preventDefault();
		payByFpx(event, fpxBank);
	});

});

$(document).on("click", "#makeSupplierStripePayment", function (e) {
	if ($('#supplierCheckoutForm').isValid()) {
		e.preventDefault();
		$('#makeSupplierStripePayment').prop('disabled', true);
		$('#makeSupplierStripePayment').addClass('disabled');
		var tax = $('#tax').val();
		if ($("#idRegCountry option:selected").html() != "Malaysia") {
			tax = 0
		}
		var amount = $('#totalPriceInput').val();
		$('#checkoutCardAmount').html(' USD ' + Number(amount).toLocaleString(undefined, { maximumFractionDigits: 2, minimumFractionDigits: 2 }));
		$('#checkoutFpxAmount').html(' USD ' + Number(amount).toLocaleString(undefined, { maximumFractionDigits: 2, minimumFractionDigits: 2 }));
		$('#makePaymentModal').modal().show();
		$('#tabOneId').click();
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
	}
});

$('#makePaymentModal').on('hide.bs.modal', function (e) {
	$('#makeSupplierStripePayment').prop('disabled', false);
	$('#makeSupplierStripePayment').removeClass('disabled');
	var errorMsg = document.querySelector(".sr-field-error");
	errorMsg.textContent = '';
	var fpxBank = elements.getElement('fpxBank');
	var card = elements.getElement('card');
	card ? card.clear() : '';
	fpxBank ? fpxBank.clear() : '';
	$('#makeSupplierStripePayment').prop('disabled', false);
	$('#makeSupplierStripePayment').removeClass('disabled');
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

function payByFpx(event, fpxBank) {
	var url = getContextPath() + '/suppliersubscription/doInitiateSupplierPayment/' +
		'?promoCodeId='
		+ $('#promoCodeId').val()
		+ '&supplerPlan='
		+ $("input[name=supplerPlan]:checked").val()
		+ '&supplierPrice='
		+ Number($('#supplierPrice').val())
		+ '&promoDiscountPrice='
		+ Number($('#promoDiscountPrice').val())
		+ '&totalPrice='
		+ Number($('#totalPriceInput').val())
		+ '&country='
		+ $('#idRegCountry').val()
		+ '&paymentMode=fpx';
	var form = $('#supplierCheckoutForm').serializeArray();
	var formData = {};
	$.each(form, function (i, v) {
		if (v.name != "registrationOfCountry") {
			formData[v.name] = v.value;
		}
	});
	console.log(formData);
	$.ajax({
		type: "POST",
		url: url,
		contentType: "application/json",
		data: JSON.stringify(formData),
		dataType: 'json',
		beforeSend: function (xhr) {
			$('#loading').show();
		},
		success: function (data) {
			console.log(data);
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
	var card = elements.getElement('card');

	var url = getContextPath() + '/suppliersubscription/doInitiateSupplierPayment/' +
		'?promoCodeId='
		+ $('#promoCodeId').val()
		+ '&supplerPlan=ALLBUYER'
		+ '&supplierPrice='
		+ Number($('#supplierPrice').val())
		+ '&promoDiscountPrice='
		+ Number($('#promoDiscountPrice').val())
		+ '&totalPrice='
		+ Number($('#totalPriceInput').val())
		+ '&country='
		+ $('#idRegCountry').val()
		+ '&paymentMode=card';
	var form = $('#supplierCheckoutForm').serializeArray();
	var formData = {};
	$.each(form, function (i, v) {
		if (v.name != "registrationOfCountry") {
			formData[v.name] = v.value;
		}
	});
	$.ajax({
		type: "POST",
		url: url,
		contentType: "application/json",
		data: JSON.stringify(formData),
		dataType: 'json',
		beforeSend: function (xhr) {
			$('#loading').show();
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