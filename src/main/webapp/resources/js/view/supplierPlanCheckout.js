$.formUtils.addValidator({
	name : 'crn_number',
	validatorFunction : function(value, $el, config, language, $form) {
		var countryID = $('#idRegCountry').val();
		var crnNum = $('#idCompRegNum').val();
		var response = true;

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		$.ajax({
			url : getContextPath() + "/checkRegistrationNumber",
			data : {
				countryID : countryID,
				crnNum : crnNum
			},
			async : false,
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success : function(data) {
				console.log(data);
				response = true;
			},
			error : function(request, textStatus, errorThrown) {
				console.log(textStatus);
				response = false;
			}
		});
		return response;
	},
	errorMessage : 'Company Registration Number Already registered in the system',
	errorMessageKey : 'badCrnNumber'
});

$.formUtils.addValidator({
	name : 'company_name',
	validatorFunction : function(value, $el, config, language, $form) {
		var countryID = $('#idRegCountry').val();
		var companyName = $('#idCompanyName').val();
		var response = true;
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url : getContextPath() + "/checkCompanyNameExis",
			data : {
				countryID : countryID,
				companyName : companyName
			},
			async : false,
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success : function(data) {
				console.log(data);
				response = true;
			},
			error : function(request, textStatus, errorThrown) {
				console.log(textStatus);
				response = false;
			}
		});
		return response;
	},
	errorMessage : 'Company Name Already registered in the system',
	errorMessageKey : 'badCompanyName'
});

$.formUtils.addValidator({
	name : 'login_email',
	validatorFunction : function(value, $el, config, language, $form) {
		var loginEmailId = $('#idLoginEmail').val();
		var response = true;
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		// alert(loginId);
		$.ajax({
			url : getContextPath() + "/checkLoginEmail",
			data : {
				loginEmailId : loginEmailId
			},
			async : false,
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success : function(data) {
				console.log(data);
				response = true;
			},
			error : function(request, textStatus, errorThrown) {
				console.log(textStatus);
				response = false;
			}
		});
		return response;
	},
	errorMessage : 'Login Email Already registered in the system',
	errorMessageKey : 'badLoginEmail'
});

$('input[name="supplerPlan"]:radio').on('change', function(e) {
	var promoCode = $('#promocode').val();
	var selectedPlan = $(this).val();

	if (selectedPlan === 'SINGLEBUYER') {
		$("#toptitle").text("SINGLE BUYER");

		$('#submitPayment').css({
			'display' : 'block'
		});
		$('#paypalPayment').css({
			'display' : 'none'
		});
	} else {
		$("#toptitle").text("UNLIMITED BUYER");

		$('#submitPayment').css({
			'display' : 'none'
		});
		$('#paypalPayment').css({
			'display' : 'block'
		});
	}

	calculateSupplierPlan(promoCode, selectedPlan);
});

$('.submitSuppButton').on('click', function(e) {
	e.preventDefault();
	if (!$('#supplierCheckoutForm').isValid()) {
		return false;
	}
	$('#supplierCheckoutForm').attr('action', getContextPath() + "/supplier/billing/supplierPlanCheckout");
	$('#supplierCheckoutForm').submit();
});

$('#promocode').on('change', function(e) {
	$('#promoError').html('').removeClass('has-error');
	var promoCode = $(this).val();
	var selectedPlan = $("#supplerPlan").val();
	calculateSupplierPlan(promoCode, selectedPlan);
});

function calculateSupplierPlan(promoCode, selectedPlan) {
	var countryId = '';
	url = getContextPath() + "/supplier/billing/getSupplierPrice";
	$.ajax({
		type : "GET",
		url : url,
		data : {
			'promoCode' : promoCode,
			'selectedPlan' : selectedPlan,
			'countryId':countryId
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data) {
			$('#promoError').html('').removeClass('has-error');

			if (selectedPlan === 'SINGLEBUYER') {
				data = {
					"promoDiscountPrice" : 0,
					"totalPrice" : 0,
					"supplierPrice" : 0,
					"promoCodeId" : "",
				};
			}
			setSupplierPrice(data);

		},
		error : function(request, textStatus, errorThrown) {
			var data = JSON.parse(request.responseText);
			var error = data.error;
			if (selectedPlan === 'SINGLEBUYER') {
				data = {
					"promoDiscountPrice" : 0,
					"totalPrice" : 0,
					"supplierPrice" : 0,
					"promoCodeId" : "",
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
	$('#promoCodeId').val(promoCodeId);

	var promoDiscountHtml = '<input type="hidden" name="promoDiscountPrice" value="' + promoDiscountPrice + '" >' + ReplaceNumberWithCommas(promoDiscountPrice.toFixed(2));
	$('#promoDiscountPrice').html(promoDiscountHtml);

	var totalHtml = 'US$ <input type="hidden" name="totalPrice" value="' + totalPrice + '" >' + ReplaceNumberWithCommas(totalPrice.toFixed(2));
	$('#totalPrice').html(totalHtml);
}

function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}
