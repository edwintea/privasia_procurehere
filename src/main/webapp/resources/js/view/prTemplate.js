$.formUtils.addValidator({
	name : 'buyer_address',
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
	errorMessageKey : 'badBuyerAddress'
});
$.formUtils.addValidator({
	name : 'readonly_data',
	validatorFunction : function(value, $el, config, language, $form) {
		var response = true;
		var fieldName = $el.attr('name');
		if ($el.is(":checked")) {
			var inputVal = $el.closest('.row').find('div:nth-child(2)').find('input[type="text"], textarea, select').val();
			if (inputVal == '') {
				response = false;
			}
		}
		return response;
	},
	errorMessage : 'This is a required field',
	errorMessageKey : 'badReadonlyData'
});

$.formUtils.addValidator({
	name : 'businessUnit',
	validatorFunction : function(value, $el, config, language, $form) {
		var response = true;
		var fieldName = $el.attr('name');
		console.log($('[name="' + fieldName + '"]:checked').length);
		var inputVal = $el.closest('.row').find('div:nth-child(2)').find('input[type="text"], textarea, select').val();
		if (inputVal == '') {
			response = false;
		}
		return response;
	},
	errorMessage : 'This is a required field',
	errorMessageKey : 'badReadonlyData'
});
$.formUtils.addValidator({
	name : 'readonly_data_address',
	validatorFunction : function(value, $el, config, language, $form) {
		var response = true;
		var fieldName = $el.attr('name');
		if ($el.is(":checked")) {
			var inputVal = $el.closest('.row').find('div:nth-child(2)').find('input[type="radio"]:checked').length;
			if (inputVal == 0) {
				response = false;
			}
		}
		return response;
	},
	errorMessage : 'This is a required field',
	errorMessageKey : 'badReadonlyDataAddress'
});

$(document).delegate('.delivery_add', 'keyup', function() {
	var $rows = $('.role-bottom-ul li');
	var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
	$rows.show().filter(function() {
		var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
		return !~text.indexOf(val);
	}).hide();
});

$(document).delegate('.role-bottom-ul li [type="radio"]', 'click', function() {
	var dataAddress = $(this).closest('li').children('.del-add').html();
	$('.phisicalArressBlock').html(dataAddress);
	$('.physicalCriterion input[type="checkbox"]').prop('checked', true);
	$('.buyerAddressRadios').addClass('active');
	$.uniform.update();
	$("#sub-credit").slideUp();
	$(this).closest('#sub-credit').parent().removeClass('has-error').find('span.help-block.form-error')
	// .children removed because validation message wasn't getting removed even after selcting value
	/* .children('span.help-block.form-error') */
	.remove();
});
$(document).delegate('#deletecorpAddress', 'click', function() {
	$(".buyerAddressRadios").removeClass("active");
	$('.physicalCriterion input[type="checkbox"]').prop('checked', false);
	$('.overscrollInnerBox input[type="radio"]').prop('checked', false);
	$.uniform.update();
	$("#sub-credit").slideToggle();
})
$(document).on("click", ".physicalCriterion + span.pull-left", function(e) {
	// $("#sub-credit").slideToggle();
});

$(document).ready(function() {
	$('input[type="checkbox"][title="Visible"]').change(function(e) {
		/*
		 * $(this).closest('.check-wrapper.first').prev().removeClass('has-error').find('span.help-block.form-error').remove();
		 * if($(this).prop('checked')){ var labelVal = $.trim($(this).closest('.check-wrapper.first').prev().children('input, textarea,
		 * select').val()); if(labelVal == ''){ $(this).prop('checked',false); $.uniform.update();
		 * $(this).closest('.check-wrapper.first').prev().append('<span class="help-block form-error">This is a required field</span>').addClass('has-error');
		 * return false; } $(this).closest('.check-wrapper').siblings('.check-wrapper').find('input[type="checkbox"]').prop('checked',true);
		 * $.uniform.update(); } else
		 */if ($(this).prop('checked') == false) {
			$(this).closest('.check-wrapper').siblings('.check-wrapper').find('input[type="checkbox"]').prop('checked', false);
			$.uniform.update();
			$(this).closest('.check-wrapper').prev().find('input, textarea').val('');
		}
	});

	

	$('input[type="checkbox"][title="Read Only"]').not('.approvalCheck, .readOnlyCheck, .approvalPoCheck, .readOnlyPoCheck, .approvalGrCheck, .readOnlyGrCheck, .approvalInvoiceCheck, .readOnlyInvoiceCheck').change(function() {
		// alert($(this).closest('.check-wrapper').siblings('.check-wrapper').length);
		if ($(this).closest('.check-wrapper').siblings('.check-wrapper').length > 0) {
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().removeClass('has-error').find('span.help-block.form-error').remove();
			if ($(this).prop('checked')) {
				var labelVal = $.trim($(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().children('input, textarea, select').val());
				if (labelVal == '' && $(this).attr('title') == "Read Only") {
					$(this).prop('checked', false);
					$.uniform.update();
					$(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().append('<span class="help-block form-error">This is a required field</span>').addClass('has-error');
					return false;
				}
				$(this).closest('.check-wrapper').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
				$.uniform.update();
			}
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().removeClass('has-error').find('span.help-block.form-error').remove();

		} else {
			if ($(this).prop('checked')) {
				$(this).closest('.check-wrapper').prev().removeClass('has-error').find('span.help-block.form-error').remove();
				var labelVal = $.trim($(this).closest('.check-wrapper').prev().find('input[type="text"], input[type="radio"]:checked, textarea, select').not('.delivery_add').val());
				// alert(labelVal);
				if (labelVal == '' && $(this).attr('title') == "Read Only") {
					$(this).prop('checked', false);
					$.uniform.update();
					$(this).closest('.check-wrapper').prev().append('<span class="help-block form-error">This is a required field</span>').addClass('has-error');
					return false;
				}
				// $(this).closest('.check-wrapper').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked',true);
				// $.uniform.update();
			}
		}
	});

	$('input.readOnlyCheck[type="checkbox"][title="Read Only"]').change(function() {
		$('#addSelect').parent().removeClass('has-error').find('.form-error').remove();
		if ($(this).prop('checked')) {
			var labelVal = $.trim($(this).closest('.row').next().find('select').select2("val"));
			if ((labelVal == '' || $(this).closest('.row').next().find('select').length == 0) && $(this).attr('title') == "Read Only") {
				$(this).prop('checked', false);
				$.uniform.update();
				$('#addSelect').parent().addClass('has-error').append('<span class="help-block form-error"> Please select at least 1 approval</span>');
				return false;
			}
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$.uniform.update();

		}
		$('#addSelect').parent().removeClass('has-error').find('.form-error').remove();
	});

	$('input.readOnlyPoCheck[type="checkbox"][title="Read Only"]').change(function() {
		$('#poAddSelect').parent().removeClass('has-error').find('.form-error').remove();
		if ($(this).prop('checked')) {
			var labelVal = $.trim($(this).closest('.row').next().next().find('select').select2("val"));
			if ((labelVal == '' || $(this).closest('.row').next().next().find('select').length == 0) && $(this).attr('title') == "Read Only") {
				$(this).prop('checked', false);
				$.uniform.update();
				$('#poAddSelect').parent().addClass('has-error').append('<span class="help-block form-error"> Please select at least 1 approval</span>');
				return false;
			}
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$.uniform.update();
		}
		$('#poAddSelect').parent().removeClass('has-error').find('.form-error').remove();
	});

	$('input.readOnlyGrCheck[type="checkbox"][title="Read Only"]').change(function() {
		$('#grAddSelect').parent().removeClass('has-error').find('.form-error').remove();
		if ($(this).prop('checked')) {
			var labelVal = $.trim($(this).closest('.row').next().next().find('select').select2("val"));
			if ((labelVal == '' || $(this).closest('.row').next().next().find('select').length == 0) && $(this).attr('title') == "Read Only") {
				$(this).prop('checked', false);
				$.uniform.update();
				$('#grAddSelect').parent().addClass('has-error').append('<span class="help-block form-error"> Please select at least 1 approval</span>');
				return false;
			}
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$.uniform.update();
		}
		$('#grAddSelect').parent().removeClass('has-error').find('.form-error').remove();
	});

	$('input.readOnlyInvoiceCheck[type="checkbox"][title="Read Only"]').change(function() {
		$('#invoiceAddSelect').parent().removeClass('has-error').find('.form-error').remove();
		if ($(this).prop('checked')) {
			var labelVal = $.trim($(this).closest('.row').next().next().find('select').select2("val"));
			if ((labelVal == '' || $(this).closest('.row').next().next().find('select').length == 0) && $(this).attr('title') == "Read Only") {
				$(this).prop('checked', false);
				$.uniform.update();
				$('#invoiceAddSelect').parent().addClass('has-error').append('<span class="help-block form-error"> Please select at least 1 approval</span>');
				return false;
			}
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$.uniform.update();
		}
		$('#invoiceAddSelect').parent().removeClass('has-error').find('.form-error').remove();
	});


	

	$('select').change(function() {
		$(this).parent().removeClass('has-error').find('span.help-block.form-error')
		// .children removed because validation message wasn't getting removed even after selcting value
		/* .children('span.help-block.form-error') */
		.remove();
	});
	// help-block form-error

	$('.check-wrapper.first').prev().find('input, textarea').blur(function() {
		$(this).closest('.col-md-3').removeClass('has-error').find('span.help-block.form-error').remove();
		if ($.trim($(this).val()) != '') {
			$(this).closest('.col-md-3').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
		} else {
			$(this).closest('.col-md-3').siblings('.check-wrapper.first').next().find('input[type="checkbox"]').prop('checked', false);
		}
		$.uniform.update();
	});

	$('.check-wrapper.first').prev().find('select').chosen().change(function() {
		if ($.trim($(this).val()) != '') {
			$(this).closest('.col-md-3').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
		} else {
			$(this).closest('.col-md-3').siblings('.check-wrapper.first').next().find('input[type="checkbox"]').prop('checked', false);
		}
		$.uniform.update();
	});

	$('#saveRfxTemplate').click(function(e) {
		e.preventDefault();

		$('.chosen-select').each(function() {
			$(this).show();
		});

		if ($("#frmRfxTemplate").isValid()) {
			$('.chosen-select').each(function() {
				$(this).hide();
			});

			$("#frmRfxTemplate").submit();
		}
	});

});

$('#saveAsPrTemplate').click(function() {
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	$('.customError').html('');
	$('#tempName').val("");
	$('#tempDescription').val("");
	$("#prTemplateSaveAsPopup").dialog({
		modal : true,
		minWidth : 300,
		width : '50%',
		maxWidth : 400,
		minHeight : 150,
		dialogClass : "",
		show : "fadeIn",
		draggable : true,
		resizable : false,
		dialogClass : "dialogBlockLoaded"
	});
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
	$('#prTemplateSaveAsPopup').find('#tempId').val($('#templateId').val());
	$('.ui-dialog-title').text(templateSaveLabel);
});

$(document).on("focusout", '#tempName', function(e) {
	$('.customError').html('');
});

/*
 * $("#prTemplateSaveAsPopup").validate({ rules : { tempName : true } });
 */
$('#saveAsTemp').click(function() {

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var templateId = $('#templateId').val();
	var templateName = $('#tempName').val();
	var templateDesc = $('#tempDescription').val();
	if (templateName != '') {
		$.ajax({
			url : 'copyPrTemplate',
			data : ({
				templateId : templateId,
				templateName : templateName,
				templateDesc : templateDesc
			}),
			type : "POST",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('div[id=idMessageJsp]').html("");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var info = request.getResponseHeader('success');
				$('p[id=idGlobalSuccessMessage]').html(info);
				$('div[id=idGlobalSuccess]').show();
				$("#templateSaveAsPopup").dialog('close');
				window.location.href = getContextPath() + "/buyer/editPrTemplate?id=" + data + "&success=" + info;

			},
			error : function(request, textStatus, errorThrown) {

				console.log("Error :" + request.getResponseHeader('error'));
				$('.customError').html('<font color="red">' + request.getResponseHeader('error') + '</font>');
				// $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				// $('div[id=idGlobalError]').show();
				$('#loading').hide();
				// $("#templateSaveAsPopup").dialog('close');
			},
			complete : function() {
				$('#loading').hide();

			}
		});
	} else {
		$('.customError').html('<font color="red">' + request.getResponseHeader('error') + '</font>');
	}
});
