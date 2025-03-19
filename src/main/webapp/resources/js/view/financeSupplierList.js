$(document).ready(function() {

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	
	$('.supplierStatus').change(function(event) {
		event.preventDefault();
		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();
		
		var order = $('.supplierRegisterOrder').val();
		var search = $('.idGlobalSearch').val();

		if (search == null || search == undefined) {
			search = "";
		}

		var data = {}
		data["globalSreach"] = search;
		data["status"] = status;
		data["order"] = order;

		$.ajax({ type : "POST", url : "searchFinanceSupplier", data : JSON.stringify(data), beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		}, complete : function() {
			$('#loading').hide();
		}, success : function(data) {
			renderGrid(data);
		}, error : function(request, textStatus, errorThrown) {
			//alert('Error: ' + request.getResponseHeader('error'));
		} });
	});
	$('.supplierStatus').trigger('change');
	$('.supplierRegisterOrder').change(function(event) {
		event.preventDefault();
		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();
		
		var order = $('.supplierRegisterOrder').val();
		var search = $('.idGlobalSearch').val();

		if (search == null || search == undefined) {
			search = "";
		}

		var data = {}
		data["globalSreach"] = search;
		data["status"] = status;
		data["order"] = order;

		$.ajax({ type : "POST", url : "searchFinanceSupplier", data : JSON.stringify(data), beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		}, complete : function() {
			$('#loading').hide();
		}, success : function(data) {
			renderGrid(data);
		}, error : function(request, textStatus, errorThrown) {
			//alert('Error: ' + request.getResponseHeader('error'));
		} });
	});

	$('#idTxtSearch').click(function(event) {
		event.preventDefault();
		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();
		var status = $('.supplierStatus').val();
		var order = $('.supplierRegisterOrder').val();
		var search = $('.idGlobalSearch').val();

		if (search == null || search == undefined) {
			search = "";
		}

		var data = {}
		data["globalSreach"] = search;
		data["status"] = status;
		data["order"] = order;

		$.ajax({ type : "POST", url : "searchFinanceSupplier", data : JSON.stringify(data), beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		}, complete : function() {
			$('#loading').hide();
		}, success : function(data) {
			renderGrid(data);
		}, error : function(request, textStatus, errorThrown) {
			//alert('Error: ' + request.getResponseHeader('error'));
		} });
	});

});

function renderGrid(data) {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var htmlText = '<div class="row event_listing_section">';
	for ( var key in data) {
		//console.log(data)
		//return;
		var id = "'" + data[key].id + "'";
		var status = data[key].status;
		htmlText += '<form method="get" action="' + getContextPath() + '/finance/financeSupplierDetails/'+ data[key].supplier.id +'">';
		htmlText += '<input type="hidden" name="'+header+'" value="'+token+'">';
		htmlText +='<div class="col-sm-6 col-md-4">';
	htmlText +='<article class="event_box status.toLowerCase()">';
	htmlText +='<header class="event_box_head ">';
	htmlText +='<h4>'+data[key].supplier.companyName+'<span class="closed_grp_txt_mod"></span></h4> </header>';
	htmlText +='<div class="box-boder"><div class="box_content">';
	if(data[key].supplier.registrationDate != undefined){
		htmlText +='<label>Registration Date :</label> ' + data[key].supplier.registrationDate + ' <br/>';
	} else {
		htmlText +='<label>Registration Date :</label>  <br/>';
	}
	htmlText +='<label>Registration No:</label>' + data[key].companyRegistrationNumber +'<br/>';
	htmlText +='<label>Person in Charge :</label>' + data[key].supplier.fullName + '<br/>';
	htmlText +='<label>Phone No:</label>' + data[key].supplier.mobileNumber + '<br/>';

		
		htmlText +='</div><span class="box_overlay">';
		htmlText +='<a href="javascript:void(0);" onclick="$(this).closest(\'form\').submit();" class="idKnowMore btn btn-info hvr-pop hvr-rectangle-out">KNOW MORE</a>';
		htmlText +='</span>';
		
											
		if (status == 'PENDING') {
			var attr ="";
			if(canEdit()==="false"){
				attr = "disabled";
			}
			htmlText +='<div class="lower-bar-search-contant-main-contant  pad_all_10 buttons">';
			htmlText +='<button class="btn btn-info btn-block approve hvr-pop hvr-rectangle-out dis" type="submit" '+attr+'  onclick="doAjaxPost(event,' + id + ', \'APPROVED\')">Approve</button>';
			htmlText +='<button class="btn btn-black hvr-pop hvr-rectangle-out1" type="submit" '+attr+' onclick="doAjaxPost(event,' + id + ', \'REJECTED\')">Reject</button></div>';
		}
	htmlText +='</article></div>';
		htmlText += '</form>';

	}
	htmlText += ' </div>';
	$('#idSuppList').html(htmlText);

}

function doAjaxPost(event, id, status) {
	event.preventDefault();
	$('div[id=idGlobalSuccess]').hide();
	$('div[id=idGlobalError]').hide();

	var attr ="";
	if(canEdit()==="false"){
		attr = "disabled";
	}
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var data = {}
	data["id"] = id;
	data["status"] = status;
	$.ajax({ type : "POST", url : "confirmSupplier",
	// async: false,
	data : JSON.stringify(data), beforeSend : function(xhr) {
		$('#loading').show();
		xhr.setRequestHeader(header, token);
		xhr.setRequestHeader("Accept", "application/json");
		xhr.setRequestHeader("Content-Type", "application/json");
	}, complete : function() {
		$('#loading').hide();
	}, success : function(data, textStatus, request) {
		/*var htmlText = '<div class="row">';
		for ( var key in data) {
			var id = "'" + data[key].id + "'";
			var date = new Date(data[key].registrationDate);
			var day = date.getDate();
			var month = date.getUTCMonth();
			var year = date.getFullYear();
			var registerDate = day + '/' + month + '/' + year;
			htmlText += '<div class="col-md-4 marg-bottom-20 block-max-height">';
			htmlText += '<div class="lower-bar-search-contant-main-block block-min-height ' + data[key].status.toLowerCase() + '">';
			htmlText += '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">';
			htmlText += '<h4>' + data[key].companyName + '</h4>';
			htmlText += '<span> ' + data[key].registrationOfCountry.countryName + '</span> </div>';
			htmlText += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
			htmlText += '<label>Registration Date :</label>';
			htmlText += '<span> ' + data[key].registrationDate + '</span> </div>';
			htmlText += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
			htmlText += '<label>Account Status :</label>';
			htmlText += '<span class="green"> ' + data[key].status + '</span> </div>';
			htmlText += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
			htmlText += '<label>Person in Charge :</label>';
			htmlText += '<span> ' + data[key].fullName + '</span> </div>';
			htmlText += '<div class="lower-bar-search-contant-main-contant  pad_all_10 buttons">';
			htmlText += '<button class="btn btn-info btn-block approve hvr-pop hvr-rectangle-out dis" type="submit" '+attr+'  onclick="doAjaxPost(event,' + id + ', \'APPROVED\')">Approve</button>';
			htmlText += '<button class="btn btn-black hvr-pop hvr-rectangle-out1" type="submit" '+attr+'  onclick="doAjaxPost(event,' + id + ', \'REJECTED\')">Reject</button>';
			htmlText += '</div>';

			
			 * htmlText += '<div class="dwonloadico"><a class=" btn-tooltip" title="" data-placement="top" data-toggle="tooltip" href="#"
			 * data-original-title="Download Profile">'; htmlText += '<img width="20" src="resources/images/dwonload.png" alt="Profile image"></a></div>';
			 
			htmlText += '<div class="hover_black">';
			htmlText += '<a href="javascript:void(0);" onclick="javascript:document.getElementById(\'actionValue\').value=' + id + ';form.submit();" class="idKnowMore">KNOW MORE</a>';
			htmlText += '</div>';
			htmlText += '</div>';
			htmlText += '</div>';

		}
		htmlText += ' </div>';
		$('#idSuppList').html(htmlText);*/
		renderGrid(data);
		$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
		$('div[id=idGlobalSuccess]').show();
	}, error : function(request, textStatus, errorThrown) {
		console.log('Error: ' + request.getResponseHeader('error'));
		$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
		$('div[id=idGlobalError]').show();

	} });
}

function doAjaxPostPopup(event, id, status) {
	event.preventDefault();
	$('#rejectModel').find('#rejectId').val(id);
	// $('.rejectsubmit').attr('');
}
function doAjaxPostReject(event, status) {
	event.preventDefault();
	var id = $('#rejectModel').find('#rejectId').val();
	var rejectRemark = $('#rejectModel').find('.rejectRemark').val();
	$('div[id=idGlobalSuccess]').hide();
	$('div[id=idGlobalError]').hide();

	var attr ="";
	if(canEdit()==="false"){
		attr = "disabled";
	}
	
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var data = {}
	data["id"] = id;
	data["status"] = status;
	data["rejectRemark"] = rejectRemark;
	$('.close').click();
	$.ajax({ type : "POST", url : "confirmSupplier",
	// async: false,
	data : JSON.stringify(data), beforeSend : function(xhr) {
		$('#loading').show();
		xhr.setRequestHeader(header, token);
		xhr.setRequestHeader("Accept", "application/json");
		xhr.setRequestHeader("Content-Type", "application/json");
	}, complete : function() {
		$('#loading').hide();
	}, success : function(data, textStatus, request) {
/*		var htmlText = '<div class="row">';
		for ( var key in data) {
			var id = "'" + data[key].id + "'";
			var date = new Date(data[key].registrationDate);

			var day = date.getDate();
			var month = date.getUTCMonth();
			var year = date.getFullYear();
			var registerDate = day + '/' + month + '/' + year;
			htmlText += '<div class="col-md-4 marg-bottom-20 block-max-height">';
			htmlText += '<div class="lower-bar-search-contant-main-block block-min-height ' + data[key].status.toLowerCase() + '">';
			htmlText += '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10">';
			htmlText += '<h4>' + data[key].companyName + '</h4>';
			htmlText += '<span> ' + data[key].registrationOfCountry.countryName + '</span> </div>';
			htmlText += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
			htmlText += '<label>Registration Date :</label>';
			htmlText += '<span> ' + registerDate + '</span> </div>';
			htmlText += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
			htmlText += '<label>Account Status :</label>';
			htmlText += '<span class="green"> ' + data[key].status + '</span> </div>';
			htmlText += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">';
			htmlText += '<label>Person in Charge :</label>';
			htmlText += '<span> ' + data[key].fullName + '</span> </div>';
			htmlText += '<div class="lower-bar-search-contant-main-contant  pad_all_10 buttons">';
			htmlText += '<button class="btn btn-info btn-block approve hvr-pop hvr-rectangle-out dis" type="submit" '+attr+'   onclick="doAjaxPost(event,' + id + ', \'APPROVED\')">Approve</button>';
			htmlText += '<button class="btn btn-black hvr-pop hvr-rectangle-out1" type="submit" '+attr+'  onclick="doAjaxPost(event,' + id + ', \'REJECTED\')">Reject</button>';
			htmlText += '</div>';

			htmlText += '<div class="dwonloadico"><a class=" btn-tooltip" title="" data-placement="top" data-toggle="tooltip" href="#" data-original-title="Download Profile">';
			htmlText += '<img width="20" src="resources/images/dwonload.png" alt="Profile image"></a></div>';
			htmlText += '<div class="hover_black">';
			htmlText += '<a href="javascript:void(0);" onclick="javascript:document.getElementById(\'actionValue\').value=' + id + ';form.submit();" class="idKnowMore">KNOW MORE</a>';
			htmlText += '</div>';
			htmlText += '</div>';
			htmlText += '</div>';

		}
		htmlText += ' </div>';
		$('#idSuppList').html(htmlText);*/
		renderGrid(data);
		$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
		$('div[id=idGlobalSuccess]').show();
	}, error : function(request, textStatus, errorThrown) {
		console.log('Error: ' + request.getResponseHeader('error'));
		$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
		$('div[id=idGlobalError]').show();

	} });
}

$('#idReject').click(function(event) {
	event.preventDefault();
	$('div[id=idGlobalSuccess]').hide();
	$('div[id=idGlobalError]').hide();

	var id = $('#rejectId').val();
	var data = {}
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var data = {}
	data["id"] = id;
	console.log("ID  : " + id);
	$.ajax({ type : "POST", url :  "rejectSupplierDetails", data : JSON.stringify(data), beforeSend : function(xhr) {
		$('#loading').show();
		xhr.setRequestHeader(header, token);
		xhr.setRequestHeader("Accept", "application/json");
		xhr.setRequestHeader("Content-Type", "application/json");
	}, complete : function() {
		$('#loading').hide();
	}, success : function(data, textStatus, request) {
		$('#rejectModel').modal('hide');
		renderGrid(data);
		$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
		$('div[id=idGlobalSuccess]').show();
	}, error : function(request, textStatus, errorThrown) {
		//alert('Error: ' + request.getResponseHeader('error'));
		$('#rejectModel').modal('hide');
	} });
});

$('#idRejectClose').click(function(event) {
	$('#rejectModel').modal('hide');
});

