$.formUtils.addValidator({ name : 'company_name', validatorFunction : function(value, $el, config, language, $form) {
		var companyName= $('#companyName').val();
		console.log("country--------",countryID)
		var countryID = $('#registrationOfCountry').val();
		var buyerID = $('#id').val();
		console.log("registration country------------",countryID);
		var response = true;
		if (countryID == '') {
			return response;
		}
		if (companyName == '') {
			return response;
		}
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({ url : getContextPath()+"/owner/checkBuyerCompanyName", data : { 'countryID' : countryID, 'companyName' : companyName ,'buyerID':buyerID}, async : false, beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
		}, success : function(data) {
			console.log(data);
			response = true;
		}, error : function(request, textStatus, errorThrown) {
			console.log(textStatus);
			response = false;
		} });
		return response;
	}, errorMessage : 'Company name already registered in the system', errorMessageKey : 'badCompanyName' });



