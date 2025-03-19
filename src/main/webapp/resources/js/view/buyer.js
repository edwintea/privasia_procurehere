$(document).ready(function() {
	prPageNo = 0;
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$('.buyerStatus').change(function(event) {
		prPageNo = 0;
		event.preventDefault();

		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();
		var status = $('.buyerStatus').val();
		var order = $('.buyerOrder').val();
		var search = $('.idGlobalSearch').val();

		if (search == null || search == undefined) {
			search = "";
		}

		var data = {}
		data["globalSreach"] = search;
		data["status"] = status;
		data["order"] = order;
		data["pageNo"]=0;

		$.ajax({
			type : "POST",
			url : "buyerListForPagination",
			data : JSON.stringify(data),
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data) {
				renderGrid(data);
			},
			error : function(request, textStatus, errorThrown) {
				alert('Error: ' + request.getResponseHeader('error'));
			}
		});
	});

	$('.buyerOrder').change(function(event) {
		prPageNo = 0;
		event.preventDefault();
		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();
		var status = $('.buyerStatus').val();
		var order = $('.buyerOrder').val();
		var search = $('.idGlobalSearch').val();

		if (search == null || search == undefined) {
			search = "";
		}

		var data = {}
		data["globalSreach"] = search;
		data["status"] = status;
		data["order"] = order;
		data["pageNo"] = 0;

		$.ajax({
			type : "POST",
			url : "buyerListForPagination",
			data : JSON.stringify(data),
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data) {
				renderGrid(data);
			},
			error : function(request, textStatus, errorThrown) {
				alert('Error: ' + request.getResponseHeader('error'));
			}
		});
	});

	$('#idSupplierUpload').click(function(event) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		event.preventDefault();
		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();
		var id = $('#buyerId').val();
		$.ajax({
			type : "POST",
			url : getContextPath() + "/owner/allowSupplierUpload/" + id,
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data, textStatus, request) {
				showMessage('SUCCESS', request.getResponseHeader('success'));
				$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
				$('div[id=idGlobalSuccess]').show();
			},
			error : function(request, textStatus, errorThrown) {
				showMessage('ERROR', request.getResponseHeader('error'));
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				// alert('Error: ' + request.getResponseHeader('error'));
			}
		});
	});

	$('#idResendBuyerActivationEmail').click(function(event) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		event.preventDefault();
		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();
		var id = $('#buyerId').val();
		$.ajax({
			type : "POST",
			url : getContextPath() + "/owner/resendBuyerActivationLink/" + id,
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data, textStatus, request) {
				showMessage('SUCCESS', request.getResponseHeader('info'));
				$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('info'));
				$('div[id=idGlobalSuccess]').show();
			},
			error : function(request, textStatus, errorThrown) {
				showMessage('ERROR', request.getResponseHeader('error'));
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				// alert('Error: ' + request.getResponseHeader('error'));
			}
		});
	});

	$('#idBuyerSearch').click(function(event) {
		event.preventDefault();
		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();
		var status = $('.buyerStatus').val();
		var order = $('.buyerOrder').val();
		var search = $('.idGlobalSearch').val();

		if (search == null || search == undefined) {
			search = "";
		}

		var data = {}
		data["globalSreach"] = search;
		data["status"] = status;
		data["order"] = order;

		$.ajax({
			type : "POST",
			url : "searchBuyer",
			data : JSON.stringify(data),
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data) {
				renderGrid(data);
				console.log("Done");
			},
			error : function(request, textStatus, errorThrown) {
				alert('Error: ' + request.getResponseHeader('error'));
			}
		});
	});

	$('#registrationOfCountry').change(function() {
		var countryId = $('#registrationOfCountry').val();
		$.ajax({
			type : "GET",
			url : getContextPath() + "/buyer/countryStates",
			data : {
				countryId : countryId
			},
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			success : function(data) {
				var stateList = '<option value="">Select State</option>';
				$.each(data, function(i, obj) {
					stateList += '<option value="' + obj.id + '">' + obj.stateName + '</option>';
				});
				$('#loading').hide();
				$('#stateList').html(stateList);
				$('#stateList').trigger("chosen:updated");
			},
			error : function(e) {
				console.log("Error");
			},
		});
	});

	$('#addBuyerNotes').click(function(event) {
		event.preventDefault();
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();

		var incidentType = $('.incidentType').val();
		var description = $('#description').val();
		var buyerId = $('#buyerId').val();

		var data = {};
		data["incidentType"] = incidentType;
		data["description"] = description;
		data["id"] = buyerId;
		var dataUrl = getOwnerContextPath() + "/addBuyerNotes";
		// console.log(token);
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : dataUrl,
			data : JSON.stringify(data),
			dataType : 'json',

			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var info = request.getResponseHeader('info');
				console.log("Success message : " + info);
				$('p[id=idGlobalSuccessMessage]').html(info != null ? info.split(",").join("<br/>") : "");
				$('div[id=idGlobalSuccess]').show();
				var table = [];
				$.each(data, function(i, item) {
					table.push({
						"createdDate" : item.createdDate,
						"description" : item.description,
						"incidentType" : item.incidentType
					});
				});
				$('#buyerNoteDetails').DataTable().clear();
				$('#buyerNoteDetails').DataTable().rows.add(table).draw();
				// $('.incidentType').val('');
				$('#description').val('');
			},
			error : function(request, textStatus, errorThrown) {
				console.log("Error  :: " + request.getResponseHeader('error'));
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('div[id=idGlobalError]').show();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});
});
function renderGrid(data) {
	console.log(data);
	var htmlText = '<div class="row">';
	for ( var key in data) {
		var id = "'" + data[key].id + "'";
		var status = data[key].status;
		companyRegistrationNumber = registrationDate = companyName = country = registrationCompleteDate = "";
		fullName = mobileNumber = "";
		if (data[key].registrationCountryName != null && data[key].registrationCountryName!= '') {
			country = data[key].registrationCountryName;
		}
		if (data[key].companyName != null && data[key].companyName != '') {
			companyName = data[key].companyName;
		}
		if (data[key].registrationDate != null && data[key].registrationDate != '') {
			registrationDate = data[key].registrationDate;
		}
		if (data[key].companyRegistrationNumber != null && data[key].companyRegistrationNumber != '') {
			companyRegistrationNumber = data[key].companyRegistrationNumber;
		}
		if (data[key].fullName != null && data[key].fullName != '') {
			fullName = data[key].fullName;
		}
		if (data[key].companyContactNumber != null && data[key].companyContactNumber != '') {
			mobileNumber = data[key].companyContactNumber;
		}

		if (data[key].registrationCompleteDate != null && data[key].registrationCompleteDate != '') {
			registrationCompleteDate = data[key].registrationCompleteDate;
		}

		htmlText += '<form method="get" action="' + getContextPath() + '/owner/buyerDetails/' + data[key].id + '">';
		htmlText += '<form><div class="col-sm-6 col-md-4 "><article class="event_box" style="height: 262px;">';
		htmlText += '<header class="event_box_head"><h4>' + companyName + '<span class="closed_grp_txt_mod">' + status + '</span></h4>';
		htmlText += '<span>' + country + '</span></header><div class="box-boder">';
		htmlText += '<div class="box_content">';
		// htmlText += '<label>Registration Date
		// :</label>'+registrationDate+'<br/>';
		htmlText += '<label>Registration No: </label> ' + companyRegistrationNumber + '<br/>';
		htmlText += '<label>Registration Completion:</label> ' + registrationCompleteDate + '<br/>';
		htmlText += '<label>Person in Charge: </label> ' + fullName + '<br/>';
		htmlText += '<label>Phone No: </label> ' + mobileNumber + '<br/>';
		if (data[key].line1 == null) {
			data[key].line1 = '';
		}
		if (data[key].line2 == null) {
			data[key].line2 = '';
		}
		if (data[key].city == null) {
			data[key].city = '';
		}

		htmlText += '<label>Address :</label><span class="date_event">' + data[key].line1 + '</span><span class="end_t_txt">' + data[key].line2 + " " + data[key].city + '</span>';
		htmlText += '</div><footer class="event_box_footer"></footer></div>';
		htmlText += '<span class="box_overlay"><a href="javascript:void(0);" onclick="$(this).closest(\'form\').submit();" class="btn btn-info hvr-pop hvr-rectangle-out">Know more <i class="glyph-icon icon-long-arrow-right"></i></a>';
		htmlText += '</span></article></div></form>';

	}
	htmlText += ' </div>';
	// console.log(htmlText);
	$('#idBuyerList').html(htmlText);

}

function renderGrid(data) {
	console.log(data);
	var htmlText = '<div class="row">';
	for ( var key in data) {
		var id = "'" + data[key].id + "'";
		var status = data[key].status;
		companyRegistrationNumber = registrationDate = companyName = country = registrationCompleteDate = "";
		fullName = mobileNumber = "";
		if (data[key].registrationOfCountry != null && data[key].registrationOfCountry != '') {
			country = data[key].registrationOfCountry.countryName;
		}
		if (data[key].companyName != null && data[key].companyName != '') {
			companyName = data[key].companyName;
		}
		if (data[key].registrationDate != null && data[key].registrationDate != '') {
			registrationDate = data[key].registrationDate;
		}
		if (data[key].companyRegistrationNumber != null && data[key].companyRegistrationNumber != '') {
			companyRegistrationNumber = data[key].companyRegistrationNumber;
		}
		if (data[key].fullName != null && data[key].fullName != '') {
			fullName = data[key].fullName;
		}
		if (data[key].companyContactNumber != null && data[key].companyContactNumber != '') {
			mobileNumber = data[key].companyContactNumber;
		}

		if (data[key].registrationCompleteDate != null && data[key].registrationCompleteDate != '') {
			registrationCompleteDate = data[key].registrationCompleteDate;
		}

		htmlText += '<form method="get" action="' + getContextPath() + '/owner/buyerDetails/' + data[key].id + '">';
		htmlText += '<form><div class="col-sm-6 col-md-4 "><article class="event_box" style="height: 262px;">';
		htmlText += '<header class="event_box_head"><h4>' + companyName + '<span class="closed_grp_txt_mod">' + status + '</span></h4>';
		htmlText += '<span>' + country + '</span></header><div class="box-boder">';
		htmlText += '<div class="box_content">';
		// htmlText += '<label>Registration Date
		// :</label>'+registrationDate+'<br/>';
		htmlText += '<label>Registration No: </label> ' + companyRegistrationNumber + '<br/>';
		htmlText += '<label>Registration Completion:</label> ' + registrationCompleteDate + '<br/>';
		htmlText += '<label>Person in Charge: </label> ' + fullName + '<br/>';
		htmlText += '<label>Phone No: </label> ' + mobileNumber + '<br/>';
		if (data[key].line1 == null) {
			data[key].line1 = '';
		}
		if (data[key].line2 == null) {
			data[key].line2 = '';
		}
		if (data[key].city == null) {
			data[key].city = '';
		}

		htmlText += '<label>Address :</label><span class="date_event">' + data[key].line1 + '</span><span class="end_t_txt">' + data[key].line2 + " " + data[key].city + '</span>';
		htmlText += '</div><footer class="event_box_footer"></footer></div>';
		htmlText += '<span class="box_overlay"><a href="javascript:void(0);" onclick="$(this).closest(\'form\').submit();" class="btn btn-info hvr-pop hvr-rectangle-out">Know more <i class="glyph-icon icon-long-arrow-right"></i></a>';
		htmlText += '</span></article></div></form>';

	}
	htmlText += ' </div>';
	// console.log(htmlText);
	$('#idBuyerList').html(htmlText);

}
function renderGridPag(data) {
	console.log(data);
	var htmlText = '';
	for ( var key in data) {
		var id = "'" + data[key].id + "'";
		var status = data[key].status;
		companyRegistrationNumber = registrationDate = companyName = country = registrationCompleteDate = "";
		fullName = mobileNumber = "";
		if (data[key].registrationOfCountry != null && data[key].registrationOfCountry != '') {
			country = data[key].registrationOfCountry.countryName;
		}
		if (data[key].companyName != null && data[key].companyName != '') {
			companyName = data[key].companyName;
		}
		if (data[key].registrationDate != null && data[key].registrationDate != '') {
			registrationDate = data[key].registrationDate;
		}
		if (data[key].companyRegistrationNumber != null && data[key].companyRegistrationNumber != '') {
			companyRegistrationNumber = data[key].companyRegistrationNumber;
		}
		if (data[key].fullName != null && data[key].fullName != '') {
			fullName = data[key].fullName;
		}
		if (data[key].companyContactNumber != null && data[key].companyContactNumber != '') {
			mobileNumber = data[key].companyContactNumber;
		}

		if (data[key].registrationCompleteDate != null && data[key].registrationCompleteDate != '') {
			registrationCompleteDate = data[key].registrationCompleteDate;
		}

		htmlText += '<form method="get" action="' + getContextPath() + '/owner/buyerDetails/' + data[key].id + '">';
		htmlText += '<form><div class="col-sm-6 col-md-4 "><article class="event_box" style="height: 262px;">';
		htmlText += '<header class="event_box_head"><h4>' + companyName + '<span class="closed_grp_txt_mod">' + status + '</span></h4>';
		htmlText += '<span>' + country + '</span></header><div class="">';
		htmlText += '<div class="box_content">';
		// htmlText += '<label>Registration Date
		// :</label>'+registrationDate+'<br/>';
		htmlText += '<label>Registration No: </label> ' + companyRegistrationNumber + '<br/>';
		
		htmlText += '<label>Person in Charge: </label> ' + fullName + '<br/>';
		htmlText += '<label>Phone No: </label> ' + mobileNumber + '<br/>';
		if (data[key].line1 == null) {
			data[key].line1 = '';
		}
		if (data[key].line2 == null) {
			data[key].line2 = '';
		}
		if (data[key].city == null) {
			data[key].city = '';
		}

		htmlText += '<label>Address :</label><span class="date_event">' + data[key].line1 + '</span><span class="end_t_txt">' + data[key].line2 + " " + data[key].city + '</span>';
		htmlText += '</div><footer class="event_box_footer"></footer></div>';
		htmlText += '<span class="box_overlay"><a href="javascript:void(0);" onclick="$(this).closest(\'form\').submit();" class="btn btn-info hvr-pop hvr-rectangle-out">Know more <i class="glyph-icon icon-long-arrow-right"></i></a>';
		htmlText += '</span></article></div></form>';

	}
	htmlText += ' </div>';
	$('#idBuyerList> .row').append(htmlText);
	// console.log(htmlText);
	//$('#idBuyerList').html(htmlText);

}
