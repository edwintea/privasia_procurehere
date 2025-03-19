$(document).ready(function () {

	var userBasedPlan = true;
	$('#planPriceId').html(ReplaceNumberWithCommas(Number($('#planPriceId').html())));
	$('.skipPayment').click(function (e) {
		e.preventDefault();
		$('#idTrailSkipForm').attr('action', getContextPath() + "/buyerSubscription/freeTrialSignup");
		$('#idTrailSkipForm').submit();

	});

	$('.userTab').click(function (e) {
		userBasedPlan = true;
		$('#idButtonHolderUser').show();
		$('#idButtonHolderEvent').hide();
	});

	$('.eventTab').click(function (e) {
		userBasedPlan = false;
		$('#idButtonHolderUser').hide();
		$('#idButtonHolderEvent').show();
	});

	$('.proceedPayment').click(function (e) {
		e.preventDefault();
		if (userBasedPlan) {
			if (!$('#idTrailUserBasedForm').isValid()) {
				return false;
			}
		} else {
			if (!$('#idTrailEventBasedForm').isValid()) {
				return false;
			}
		}

		if (userBasedPlan) {
			$('#idTrailUserBasedForm').attr('action', getContextPath() + "/buyerSubscription/doInitiateTrialPayment");
			$('#idTrailUserBasedForm').submit();
		} else {
			$('#idTrailEventBasedForm').attr('action', getContextPath() + "/buyerSubscription/doInitiateTrialPayment");
			$('#idTrailEventBasedForm').submit();
		}

	});

	if ($('#additionalEvents').val()) {
		var noOfEvent = parseInt($('#additionalEvents').val());
		var promoCode = $('#promoCodeEvent').val();
		calculateEventBasedPlan(noOfEvent, promoCode);
	}

	if ($('#additionalUsers').val()) {
		var noOfUser = 0
		var promoCode = $('#promoCodeUser').val();
		var selectedPeriodId = $("input[name=periodId]:checked").val();
		if (selectedPeriodId === undefined || selectedPeriodId === '') {
			selectedPeriodId = null;
		}
		calculateUserBasedPlan(noOfUser, promoCode, selectedPeriodId);
	}

});

$(document).on("keyup", "#additionalUsers", function (e) {
	e.preventDefault();
	var noOfUser = parseInt($(this).val());
	// $('#userError').html('').removeClass('has-error');
	var promoCode = $('#promoCodeUser').val();
	var selectedPeriodId = $("input[name=periodId]:checked").val();
	if (selectedPeriodId === undefined || selectedPeriodId === '') {
		selectedPeriodId = null;
	}
	if (isNaN(noOfUser)) {
		// TODO show error
		console.log("This is not a correct number");
		noOfUser = 0;
	}

	// debugger
	calculateUserBasedPlan(noOfUser, promoCode, selectedPeriodId);
});

$(document).on("keyup", "#additionalEvents", function (e) {
	e.preventDefault();
	var noOfEvent = parseInt($(this).val());
	// $('#userError').html('').removeClass('has-error');
	var promoCode = $('#promoCodeEvent').val();

	if (isNaN(noOfEvent)) {
		// TODO show error
		console.log("This is not a correct number");
		noOfEvent = 0;
	}

	// debugger
	calculateEventBasedPlan(noOfEvent, promoCode);
});

$('input[name="periodId"]:radio').on('change', function (e) {
	var noOfUser = parseInt($('#additionalUsers').val());
	var promoCode = $('#promoCodeUser').val();
	var selectedPeriodId = $(this).val();

	if (isNaN(noOfUser)) {
		noOfUser = 0;
	}

	calculateUserBasedPlan(noOfUser, promoCode, selectedPeriodId);
});

$('#promoCodeUser').on('change', function (e) {
	var noOfUser = parseInt($('#additionalUsers').val());
	var promoCode = $(this).val();
	var selectedPeriodId = $("input[name=periodId]:checked").val();
	$('#promoErrorUser').html('').removeClass('has-error');
	if (isNaN(noOfUser)) {
		noOfUser = 0;
	}

	calculateUserBasedPlan(noOfUser, promoCode, selectedPeriodId);

});

$('#promoCodeEvent').on('change', function (e) {
	var noOfEvent = parseInt($('#additionalEvents').val());
	var promoCode = $(this).val();
	$('#promoErrorEvent').html('').removeClass('has-error');
	var promoCode = $('#promoCodeEvent').val();
	if (isNaN(noOfEvent)) {
		noOfEvent = 0;
	}
	// debugger
	calculateEventBasedPlan(noOfEvent, promoCode);
});

function calculateUserBasedPlan(noOfUser, promoCode, selectedPeriodId) {
	var userPlanId = $('#userPlanId').val();
	var countryId = $('#buyerCountry').val();
	url = getContextPath() + "/buyerSubscription/getUserBasedPrice";
	$.ajax({
		type: "GET",
		url: url,
		data: {
			'planId': userPlanId,
			'noOfUser': noOfUser,
			'promoCode': promoCode,
			'periodId': selectedPeriodId,
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
			$('#promoErrorUser').html('').removeClass('has-error');
			setSubscriptionUserPrice(data);

		},
		error: function (request, textStatus, errorThrown) {
			var data = JSON.parse(request.responseText);
			setSubscriptionUserPrice(data);
			var error = data.error;

			$('#promoErrorUser').html('<span class="help-block form-error">' + error + '</span>').addClass('has-error');

		}

	});

}
function calculateEventBasedPlan(noOfEvent, promoCode) {
	var eventPlanId = $('#eventPlanId').val();
	var countryId = $('#buyerCountry').val();
	url = getContextPath() + "/buyerSubscription/getEventBasedPrice";
	$.ajax({
		type: "GET",
		url: url,
		data: {
			'planId': eventPlanId,
			'noOfEvent': noOfEvent,
			'promoCode': promoCode,
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
			$('#promoErrorEvent').html('').removeClass('has-error');
			setSubscriptionEventPrice(data);

		},
		error: function (request, textStatus, errorThrown) {
			var data = JSON.parse(request.responseText);
			setSubscriptionEventPrice(data);
			var error = data.error;
			$('#promoErrorEvent').html('<span class="help-block form-error">' + error + '</span>').addClass('has-error');

		}

	});

}

function setSubscriptionUserPrice(data) {

	var basePrice = data.basePrice;
	var addtionalUserPrice = data.addtionalUserPrice;
	var subscriptionDiscountPrice = data.subscriptionDiscountPrice;
	var promoDiscountPrice = data.promoDiscountPrice;
	var totalPrice = data.totalPrice + data.taxAmount;
	var planDuration = data.planDuration;
	var rangeId = data.rangeId;
	var promoCodeId = data.promoCodeId;

	$('#rangeUserId').val(rangeId);
	$('#promoCodeUserId').val(promoCodeId);

	$('#ubp').find('#additionalShowUserPrice').html(ReplaceNumberWithCommas(addtionalUserPrice.toFixed(2)));

	$('#ubp').find('#planDuration').text(planDuration);

	var baseHtml = '<input type="hidden" name="eventPrice" value="' + basePrice + '" >' + ReplaceNumberWithCommas(basePrice.toFixed(2));
	$('#ubp').find('#basePrice').html(baseHtml);

	var addtionalUserHtml = '<input type="hidden" name="addtionalUserPrice" value="' + addtionalUserPrice + '" >' + ReplaceNumberWithCommas(addtionalUserPrice.toFixed(2));
	$('#ubp').find('#addtionalUserPrice').html(addtionalUserHtml);

	var subscriptionDiscountHtml = '<input type="hidden" name="subscriptionDiscountPrice" value="' + subscriptionDiscountPrice + '" >' + ReplaceNumberWithCommas(subscriptionDiscountPrice.toFixed(2));
	$('#ubp').find('#subscriptionDiscountPrice').html(subscriptionDiscountHtml);

	var promoDiscountHtml = '<input type="hidden" name="promoDiscountPrice" value="' + promoDiscountPrice + '" >' + ReplaceNumberWithCommas(promoDiscountPrice.toFixed(2));
	$('#ubp').find('#promoDiscountPrice').html(promoDiscountHtml);

	var totalHtml = 'US$ <input type="hidden" name="totalFeeAmount" value="' + totalPrice + '" >' + ReplaceNumberWithCommas(totalPrice.toFixed(2));
	$('#ubp').find('#totalPrice').html(totalHtml);

	$('#taxAmount').html(data.taxAmount);
}

function setSubscriptionEventPrice(data) {
	var eventPrice = data.eventPrice;
	var noOfEvent = data.noOfEvent;
	var promoDiscountPrice = data.promoDiscountPrice;
	var totalPrice = data.totalPrice + data.taxAmount;
	var rangeId = data.rangeId;
	var promoCodeId = data.promoCodeId;

	$('#rangeEventId').val(rangeId);
	$('#promoCodeEventId').val(promoCodeId);

	$('#ebp').find('#eventShowPrice').html(ReplaceNumberWithCommas(totalPrice.toFixed(2)));

	$('#ebp').find('#selectedNoEvent').text(noOfEvent);

	var eventPriceHtml = '<input type="hidden" name="eventPrice" value="' + eventPrice + '" >' + ReplaceNumberWithCommas(eventPrice.toFixed(2));
	$('#ebp').find('#eventPrice').html(eventPriceHtml);

	var promoDiscountHtml = '<input type="hidden" name="promoDiscountPrice" value="' + promoDiscountPrice + '" >' + ReplaceNumberWithCommas(promoDiscountPrice.toFixed(2));
	$('#ebp').find('#promoEventDiscountPrice').html(promoDiscountHtml);

	$('#taxAmount').html(data.taxAmount);

	var totalHtml = 'US$ <input type="hidden" name="totalFeeAmount" value="' + totalPrice + '" >' + ReplaceNumberWithCommas(totalPrice.toFixed(2));
	$('#ebp').find('#totalEventPrice').html(totalHtml);
}

function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}
