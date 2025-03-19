$.formUtils.addValidator({
	name: 'country_change', validatorFunction: function (value, $el, config, language, $form) {
		var countryID = $('#idRegCountry').val();
		var crnNum = $('#idCompRegNum').val();
		var companyName = $('#idCompanyName').val();
		if (crnNum == '' || crnNum == 'undefined' || companyName == '' || companyName == 'undefined') {
			return false;
		} else {
			return true
		}
	}, errorMessage: 'Please select company name and registartion'
});

$.formUtils.addValidator({ name : 'crn_number', validatorFunction : function(value, $el, config, language, $form) {
	var countryID = $('#idRegCountry').val();
	var crnNum = $('#idCompRegNum').val();
	var response = true;
	if (countryID == '') {
		return response;
	}
	if (crnNum == '') {
		return response;
	}
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$.ajax({ url : getContextPath()+"/owner/checkBuyerRegistrationNumber", data : { 'countryID' : countryID, 'crnNum' : crnNum }, async : false, beforeSend : function(xhr) {
		xhr.setRequestHeader(header, token);
	}, success : function(data) {
		console.log(data);
		response = true;
	}, error : function(request, textStatus, errorThrown) {
		console.log(textStatus);
		response = false;
	} });
	return response;
}, errorMessage : 'Company registration number already registered in the system', errorMessageKey : 'badCrnNumber' });

$.formUtils.addValidator({
	name: 'company_name', validatorFunction: function (value, $el, config, language, $form) {
		var countryID = $('#idRegCountry').val();
		var companyName = $('#idCompanyName').val();
		var response = true;
		if (countryID == '') {
			return response;
		}
		if (companyName == '') {
			return response;
		}
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url: getContextPath() + "/owner/checkBuyerCompanyName", data: { 'countryID': countryID, 'companyName': companyName }, async: false, beforeSend: function (xhr) {
				xhr.setRequestHeader(header, token);
			}, success: function (data) {
				console.log(data);
				response = true;
			}, error: function (request, textStatus, errorThrown) {
				console.log(textStatus);
				response = false;
			}
		});
		return response;
	}, errorMessage: 'Company name already registered in the system', errorMessageKey: 'badCompanyName'
});

$.formUtils.addValidator({
	name: 'login_email', validatorFunction: function (value, $el, config, language, $form) {
		var loginEmailId = $('#idLoginEmail').val();
		var response = true;
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url: getContextPath() + "/owner/checkLoginEmail", data: { 'loginEmail': loginEmailId }, async: false, beforeSend: function (xhr) {
				xhr.setRequestHeader(header, token);
			}, success: function (data) {
				console.log(data);
				response = true;
			}, error: function (request, textStatus, errorThrown) {
				console.log(textStatus);
				response = false;
			}
		});
		return response;
	}, errorMessage: 'Login email already registered in the system', errorMessageKey: 'badLoginEmail'
});


$(document).ready(function () {


	$('.submitPaymentBtn').click(function (e) {
		e.preventDefault();
		if (!$('#idTrailSignupForm').isValid()) {
			return false;
		}

		$('#myModal').modal('show');
	});


	$('.skipPayment').click(function (e) {
		e.preventDefault();
		$('#idTrailSignupForm').attr('action', getContextPath() + "/buyerSubscription/freeTrialSignup");
		$('#idTrailSignupForm').submit();

	});


	$('.proceedPayment').click(function (e) {
		e.preventDefault();
		$('#idTrailSignupForm').attr('action', getContextPath() + "/buyerSubscription/freeTrialPlan");
		$('#idTrailSignupForm').submit();

	});

});







