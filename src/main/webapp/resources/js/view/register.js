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
  
    
$(document).ready(function () {
 

});

     