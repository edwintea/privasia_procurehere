$.formUtils.addValidator({
	name : 'pr_supplier',
	validatorFunction : function(value, $el, config, language, $form) {
		var response = true;
		var fieldName = $el.attr('name');
		console.log($('[name="' + fieldName + '"]:checked').length);
		if ($('[name="' + fieldName + '"]:checked').length == 0) {
			response = false;
		}
		return response;
	},
	errorMessage : 'This is a required field',
	errorMessageKey : 'badPrSupplier'
});

$(document).ready(function() {
	$.validate({
		lang : 'en',
		modules : 'date'
	});
	showSupplierBlock();
	$('.showSupplierBlocks').change(function() {
		// showSupplierBlock();
		// alert('a');
		// if purchase item exists then show pop-up

		var contactSize = parseInt($('#contactListSize').val());
		if ($('#prItemExists').val() == 'true' || (typeof contactSize != "undefined" && contactSize > 0)) {

			$("#chosenSupplier").removeAttr('data-validation');

			$(this).prop('checked', false);
			if ($(this).val() == 'MANUAL') {
				$('[value="LIST"]').prop('checked', true);
			} else {
				$('[value="MANUAL"]').prop('checked', true);
			}
			$(this).prop('checked', false);
			$.uniform.update();

			var changeSupplierLabel = '';
			if ($('#prItemExists').val() == 'true' && (typeof contactSize != "undefined" && contactSize > 0)) {
				changeSupplierLabel += 'Are you sure you want to delete purchase item and contact details related to this supplier?';
			} else if ($('#prItemExists').val() == 'true') {
				changeSupplierLabel += 'Are you sure you want to delete purchase item related to this supplier?';
			} else {
				changeSupplierLabel += 'Are you sure you want to delete contact details related to this supplier?';
			}
			$('#changeSupplierLabel').text(changeSupplierLabel);
			$('#confirmChangeSupplier').modal();
		} else { // reset all fields
			showSupplierBlock();
			$('.supplierRadioButton').prop('checked', false);
			$.uniform.update();
			$('.buyerAddressRadios').removeClass('active enabledBlock');
			$('.showPartMANUAL').find('input, textarea').val('');
		}
	});

	/*
	 * $('.supplierRadioButton').change(function() { //if($('#prItemExists').val() == 'true'){ $('#confirmChangeSupplier').modal(); //} });
	 */
});

function showSupplierBlock() {
	var shb = $('.showSupplierBlocks:checked').val();

	if (shb == 'MANUAL') {
		$("#chosenSupplier").removeAttr('data-validation');
	} else {
		$("#chosenSupplier").attr('data-validation', 'required');
	}

	$('.supplierBoxes').hide();
	$('.showPart' + shb).show();
}

$(document).delegate('.delivery_add', 'keyup', function() {
	var $rows = $('.role-bottom-ul li');
	var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
	$rows.show().filter(function() {
		var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
		return !~text.indexOf(val);
	}).hide();
});
var clicks = new Array();
clicks[0] = $('.role-bottom-ul li [type="radio"]:checked').val();
var checkedVal = $('.role-bottom-ul li [type="radio"]:checked').val();
$(document).delegate('.role-bottom-ul li [type="radio"]', 'click', function() {
	clicks.push($(this).val());

	$('#selectedSuppId').val($(this).val());

	var contactSize = parseInt($('#contactListSize').val());
	if ($('#prItemExists').val() == 'true' || (typeof contactSize != "undefined" && contactSize > 0)) {
		$('#confirmChangeSupplierFavlist').modal();
		var lastPriVal = clicks[clicks.length - 2];
		$('.role-bottom-ul li [type="radio"][value="' + lastPriVal + '"]').prop('checked', true);
		$.uniform.update();
		var ChangeSupplierFavlistLabel = '';
		if ($('#prItemExists').val() == 'true' && (typeof contactSize != "undefined" && contactSize > 0)) {
			ChangeSupplierFavlistLabel += 'Are you sure you want to delete purchase item and contact details related to this supplier?';
		} else if ($('#prItemExists').val() == 'true') {
			ChangeSupplierFavlistLabel += 'Are you sure you want to delete purchase item related to this supplier?';
		} else {
			ChangeSupplierFavlistLabel += 'Are you sure you want to delete contact details related to this supplier?';
		}
		$('#ChangeSupplierFavlistLabel').text(ChangeSupplierFavlistLabel);
	} else {
		ajaxForDeletePrSupplierDetails(true);
		// var dataAddress = $(this).closest('li').children('.del-add').html();
		// $('.phisicalArressBlock').html(dataAddress);
		// $('.physicalCriterion input[type="checkbox"]').prop('checked', true);
		// $('.buyerAddressRadios').addClass('active enabledBlock');
		// $.uniform.update();
	}
});
$('#confirmChangeSupplierFavlist').on('hidden.bs.modal', function() {
	clicks = clicks.slice(0, -1);
})

// contact detail pop up
$(document).delegate('.addContactPersonPop', 'click', function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	$('#resetEventContctForm').click();
	$('#addEditContactPopup').find('#contactId, #id').val('');
	$("#addEditContactPopup").dialog({
		modal : true,
		minWidth : 300,
		width : '90%',
		maxWidth : 600,
		minHeight : 200,
		dialogClass : "",
		show : "fadeIn",
		draggable : false,
		dialogClass : "dialogBlockLoaded"
	});
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
	$('.ui-dialog-title').text('Add Contact Person');
	$('#addEditContactPopup').find('a.addContactPerson').text('Add Contact Person');
});

$(document).on("click", ".addContactPerson", function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	if (!$('#addContactForm').isValid())
		return false;

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$('#loading').show();
	var prId = $.trim($('#addEditContactPopup').find('#prId').val());
	var contactId = $.trim($('#addEditContactPopup').find('#contactId').val());
	var ajaxUrl = getContextPath() + "/buyer/addPrContactPerson/" + prId;
	$.ajax({
		url : ajaxUrl,
		data : $('#addContactForm').serialize(),
		type : "POST",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success : function(data, textstatus, request) {
			var success = request.getResponseHeader('success');
			// $('table.contactPersons').remove();
			$('#addContactForm').find('input[type="text"]').val('');
			var html = '';
			if (data != '' && data != null && data.length > 0) {
				html = setContactPersonGrid(data, html);
				$('#contactListSize').val(data.length);
			} else {
				$('#contactListSize').val("0");
			}
			$('.contactPersons tbody').html(html);
			$('#addEditContactPopup').dialog('close');
			$('p[id=idGlobalSuccessMessage]').html(success);
			$('div[id=idGlobalSuccess]').show();
			$.jGrowl(success, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-green'
			});

		},
		error : function(request, textStatus, errorThrown) {
			console.log("ERROR :  " + request.getResponseHeader('error'));
			var error = request.getResponseHeader('error');
			$('p[id=idGlobalErrorMessage]').html(error);
			$('div[id=idGlobalError]').show();
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
			$('#addEditContactPopup').modal('hide');
			// $("#" + buttonId).prop("disabled", false);
		}
	});
});

$(document).delegate('.editContact', 'click', function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var contactId = $(this).closest('td').attr('contact-id');
	$('#addEditContactPopup').find('#contactId').val(contactId);

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url : getContextPath() + "/buyer/editPrContact",
		data : {
			'contactId' : contactId,
		},
		type : 'POST',
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			$.each(data, function(name, value) {
				$('#addContactForm').find('input[name="' + name + '"]').val(value);
				// $('#contactId').find('input[name="'+item.name+'"]').val(item.value);
			});
			$("#addEditContactPopup").dialog({
				modal : true,
				minWidth : 300,
				width : '90%',
				maxWidth : 600,
				minHeight : 200,
				dialogClass : "",
				show : "fadeIn",
				draggable : false,
				dialogClass : "dialogBlockLoaded"
			});
			$('.ui-widget-overlay').addClass('bg-white opacity-60');
			$('.ui-dialog-title').text('Update Contact Person');
			$('#addEditContactPopup').find('a.addContactPerson').text('Update Contact Person');
		},
		error : function(request, textStatus, errorThrown) {
			console.log(request);
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

$(document).delegate('.deleteContact', 'click', function(e) {
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	e.preventDefault();
	var contactId = $(this).closest('td').attr('contact-id');
	$('#confirmDeleteContact').find('#deleteIdContact').val(contactId);
	$('#confirmDeleteContact').modal();
});

$(document).delegate('#confDelContact', 'click', function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var contactId = $('#confirmDeleteContact').find('#deleteIdContact').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url : getContextPath() + "/buyer/deletePrContact",
		data : {
			'contactId' : contactId,
			'prId' : $('#id').val()
		},
		type : 'POST',
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {

			$('#addContactForm').find('input[type="text"]').val('');
			$('#addEditContactPopup').find('#contactId').val('');
			var html = '';
			if (data != '' && data != null && data.length > 0) {
				html = setContactPersonGrid(data, html);
				$('#contactListSize').val(data.length);
			} else {
				$('#contactListSize').val("0");
			}
			var success = request.getResponseHeader('success');
			$('p[id=idGlobalSuccessMessage]').html(success);
			$('div[id=idGlobalSuccess]').show();
			$.jGrowl(success, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-green'
			});
			$('.contactPersons tbody').html(html);
			console.log("======");
			$('#confirmDeleteContact').modal('hide');
		},
		error : function(request, textStatus, errorThrown) {
			var error = request.getResponseHeader('error');
			$('p[id=idGlobalErrorMessage]').html(error);
			$('div[id=idGlobalError]').show();
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

function setContactPersonGrid(data, html) {
	$.each(data, function(key, value) {
		html += '<tr><td contact-id="' + value.id + '"><a class="editContact" href="" data-placement="top" title="Edit"><img src="' + getContextPath() + '/resources/images/edit1.png" ></a>';
		html += '<a class="deleteContact" href="" data-placement="top" title="Delete"><img src="' + getContextPath() + '/resources/images/delete1.png" ></a></td>';
		if (typeof value.title != "undefined") {
			html += '<td>' + value.title + '</td>';
		} else {
			html += '<td>&nbsp</td>';
		}
		html += '<td>' + value.contactName + '</td>';
		if (typeof value.designation != "undefined") {
			html += '<td>' + value.designation + '</td>';
		} else {
			html += '<td>&nbsp</td>';
		}
		if (typeof value.contactNumber != "undefined") {
			html += '<td>' + value.contactNumber + '</td>';
		} else {
			html += '<td>&nbsp</td>';
		}
		if (typeof value.mobileNumber != "undefined") {
			html += '<td>' + value.mobileNumber + '</td>';
		} else {
			html += '<td>&nbsp</td>';
		}
		if (typeof value.faxNumber != "undefined") {
			html += '<td>' + value.faxNumber + '</td>';
		} else {
			html += '<td>&nbsp</td>';
		}
		if (typeof value.comunicationEmail != "undefined") {
			html += '<td>' + value.comunicationEmail + '</td>';
		} else {
			html += '<td>&nbsp</td>';
		}
	});
	return html;
}
// on supplier change delete purchase items
$(document).delegate('#confDelSupplier', 'click', function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url : getContextPath() + "/buyer/deletePrSupplerDetails",
		data : {
			'prId' : $('#id').val()
		},
		type : 'POST',
		dataType : 'text',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			$('p[id=idGlobalSuccessMessage]').html(success);
			$('div[id=idGlobalSuccess]').show();
			$.jGrowl(success, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-green'
			});

			var lastSUpplier = clicks[clicks.length - 1];
			$('#confirmChangeSupplier').modal('hide');
			$('#prItemExists').val("false");
			if ($('.showSupplierBlocks:checked').val() == 'MANUAL') {
				$('[value="LIST"]').prop('checked', true);
			} else {
				$('[type="radio"][value="' + lastSUpplier + '"]').prop('checked', true);
				// clicks[clicks.length-1];
				// $('[value="MANUAL"]').prop('checked',true);
			}

			$('#contactListSize').val("0");
			$('.contactPersons tbody').html('');

			$('#loading').hide();
			$.uniform.update();
			showSupplierBlock();
		},
		error : function(request, textStatus, errorThrown) {
			var error = request.getResponseHeader('error');
			$('p[id=idGlobalErrorMessage]').html(error);
			$('div[id=idGlobalError]').show();
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});
// on supplier change delete purchase items
$(document).delegate('#confDelSupplierFav', 'click', function(e) {
	e.preventDefault();
	ajaxForDeletePrSupplierDetails(false);

});

function ajaxForDeletePrSupplierDetails(firstSelect) {
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url : getContextPath() + "/buyer/deletePrSupplerDetails",
		data : {
			'prId' : $('#id').val(),
			'supplierId' : $('#selectedSuppId').val()
		},
		type : 'POST',
		dataType : 'text',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {

			var success = request.getResponseHeader('success');
			$('p[id=idGlobalSuccessMessage]').html(success);
			data = jQuery.parseJSON(data);
			var html = '';
			if (data != '' && data != null) {
				html = setContactPersonGrid(data, html);
				$('#contactListSize').val('1');
			} else {
				$('#contactListSize').val("0");
			}

			$('.contactPersons tbody').html(html);

			if (firstSelect != true) {
				$('div[id=idGlobalSuccess]').show();
				$.jGrowl(success, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-green'
				});
			}
			var lastSUpplier = clicks[clicks.length - 1];
			$('#confirmChangeSupplierFavlist').modal('hide');
			$('#prItemExists').val("false");
			$('[type="radio"][value="' + lastSUpplier + '"]').prop('checked', true);
			$('#loading').hide();
			$.uniform.update();
			showSupplierBlock();

		},
		error : function(request, textStatus, errorThrown) {
			var error = request.getResponseHeader('error');
			$('p[id=idGlobalErrorMessage]').html(error);
			$('div[id=idGlobalError]').show();
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
}

/* Cancle Popup */
/*
 * $(document).delegate('#cnclDelSupp', 'click', function(e) { // $('#prItemExists').val("false");
 * $('.showSupplierBlocks').not(':checked').prop("checked", true); $.uniform.update(); $('.showSupplierBlocks').trigger("change");
 * $('#confirmChangeSupplier').modal('hide'); })
 */
// Skip JQuery validations for save draft
$(".skipvalidation ").on('click', function(e) {
	if ($("#skipper").val() == undefined) {
		e.preventDefault();

		$(this).after("<input type='hidden' id='skipper' value='1'>");
		$('form.has-validation-callback :input').each(function() {
			$(this).on('beforeValidation', function(value, lang, config) {
				$(this).attr('data-validation-skipped', 1);
			});
		});
		$(this).trigger("click");
	}
});

// save draft
$('#submitStep1PrDetailDraft').click(function(e) {
	$('#prSupplierForm').attr('action', getContextPath() + "/buyer/savePrSupplierDraft");
	$('#prSupplierForm').submit();

});