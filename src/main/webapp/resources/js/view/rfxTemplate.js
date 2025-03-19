function updateLink(id) {
	var link = $("a#idConfirmDelete");
	link.data('href', link.attr("href"));
	link.attr("href", link.data('href') + '' + id);
}
function doCancel() {
	var link = $("a#idConfirmDelete");
	link.data('href', link.attr("href"));
	link.attr("href", '${pageContext.request.contextPath}/buyer/deleteRfxTemplate?id=');
}

$(document).ready(function() {

	$("#rfi_div").hide();

	if ($("#readOnlySupplier").prop("checked") == true) {

		$(".autoPopulateSupplier").addClass("disabled", true);
		$(".supplierBasedOnCategory").addClass("disabled", true);

	}

	if ($("#autoPopulateSupplier").prop("checked") == true) {

		$(".supplierBasedOnCategory").addClass("disabled", true);

	}

	$("#readOnlySupplier").click(function() {
		if ($("#readOnlySupplier").prop("checked") == true) {
			console.log("check....");
			$("#autoPopulateSupplier").prop("checked", true);
			$("#supplierBasedOnCategory").prop("checked", true);

			$(".supplierBasedOnCategory").addClass("disabled", true);
			$(".autoPopulateSupplier").addClass("disabled", true);

			$.uniform.update();
		} else {

			console.log("uncheck....");
			$(".autoPopulateSupplier").removeClass("disabled");

			if ($("#supplierBasedOnCategory").prop("checked") == true) {
				$(".supplierBasedOnCategory").addClass("disabled", true);

			} else {

				$(".supplierBasedOnCategory").removeClass("disabled");
			}
			$.uniform.update();
		}

	});

	$("#readOnlyViewAuctionHall").click(function() {

		if ($("#readOnlyViewAuctionHall").prop("checked") == true) {

			$("#visibleViewAuctionHall").prop("checked", true);
			$("#visibleViewAuctionHall").addClass("readOnlyClass", true);
			$("#auctionHallView").prop("checked", true);
		} else {
			$("#visibleViewAuctionHall").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

	$("#readOnlyRevertLastBid").click(function() {
		if ($("#readOnlyRevertLastBid").prop("checked") == true) {
			$("#visibleRevertLastBid").prop("checked", true);
			$("#visibleRevertLastBid").addClass("readOnlyClass", true);
			$("#revertLastBid").prop("checked", true);
		} else {
			$("#visibleRevertLastBid").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

	$("#readOnlyEvalCon").click(function() {
		if ($("#readOnlyEvalCon").prop("checked") == true) {
			$("#visibleEvalCon").prop("checked", true);
			$("#visibleEvalCon").addClass("readOnlyClass", true);
			$("#enableEvalCon").prop("checked", true);
		} else {
			$("#visibleEvalCon").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

	$('#unMaskedUser').change(function() {
		var unMaskedUserVal = ($('#unMaskedUser').val());
		if ((unMaskedUserVal != '')) {
			$("#visibleViewSupplierName").prop("checked", true);
			$("#enableMasking").prop("checked", true);
		}
		$.uniform.update();
	});

	$('#revertBidUser').change(function() {
		var revertBidUserVal = ($('#revertBidUser').val());
		if ((revertBidUserVal != '')) {
			$("#visibleRevertLastBid").prop("checked", true);
			$("#revertLastBid").prop("checked", true);
		}
		$.uniform.update();
	});

	$('#evalConUser').change(function() {
		var evalConUserVal = ($('#evalConUser').val());
		if ((evalConUserVal != '')) {
			$("#visibleEvalCon").prop("checked", true);
			$("#enableEvalCon").prop("checked", true);
		}
		$.uniform.update();
	});

	$("#readOnlyCloseEnvelope").click(function() {

		if ($("#readOnlyCloseEnvelope").prop("checked") == true) {

			$("#visibleCloseEnvelope").prop("checked", true);
			$("#visibleCloseEnvelope").addClass("readOnlyClass", true);
			$("#closeEnvelopeView").prop("checked", true);
		} else {
			$("#visibleCloseEnvelope").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

	$("#readOnlyAddSupplier").click(function() {

		if ($("#readOnlyAddSupplier").prop("checked") == true) {

			$("#visibleAddSupplier").prop("checked", true);
			$("#visibleAddSupplier").addClass("readOnlyClass", true);
			$("#addSupplierView").prop("checked", true);
		} else {
			$("#visibleAddSupplier").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

	$("#readOnlyViewSupplierName").click(function() {
		if ($("#readOnlyViewSupplierName").prop("checked") == true) {
			$("#visibleViewSupplierName").prop("checked", true);
			$("#visibleViewSupplierName").addClass("readOnlyClass", true);
			$("#enableMasking").prop("checked", true);
		} else {
			$("#visibleViewSupplierName").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

	$("#enableMasking").click(function() {

		if ($("#enableMasking").prop("checked") == true) {

			$("#visibleViewSupplierName").prop("checked", true);

		} else {
			$("#readOnlyViewSupplierName").prop("checked", false);
			$("#visibleViewSupplierName").removeClass("readOnlyClass", true);
			$('#unMaskedUser').val('').trigger('chosen:updated');
		}
		$.uniform.update();
	});

	$("#closeEnvelopeView").click(function() {

		if ($("#closeEnvelopeView").prop("checked") == true) {
			$("#visibleCloseEnvelope").prop("checked", true);
		} else {
			$("#readOnlyCloseEnvelope").prop("checked", false);
			$("#visibleCloseEnvelope").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

	$("#addSupplierView").click(function() {

		if ($("#addSupplierView").prop("checked") == true) {
			$("#visibleAddSupplier").prop("checked", true);
		} else {
			$("#readOnlyAddSupplier").prop("checked", false);
			$("#visibleAddSupplier").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

	$("#suspendEventView").click(function() {

		if ($("#suspendEventView").prop("checked") == true) {
			$("#visibleAllowToSuspendEvent").prop("checked", true);
		} else {
			$("#readOnlyAllowToSuspendEvent").prop("checked", false);
			$("#visibleAllowToSuspendEvent").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});
	
	
	$("#awardEventView").click(function() {

		if ($("#awardEventView").prop("checked") == true) {
			$("#visibleAllowToAwardEvent").prop("checked", true);
		} else {
			$("#readOnlyAllowToAwardEvent").prop("checked", false);
			$("#visibleAllowToAwardEvent").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

	$("#auctionHallView").click(function() {

		if ($("#auctionHallView").prop("checked") == true) {
			$("#visibleViewAuctionHall").prop("checked", true);
		} else {
			$("#readOnlyViewAuctionHall").prop("checked", false);
			$("#visibleViewAuctionHall").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

	$("#revertLastBid").click(function() {

		if ($("#revertLastBid").prop("checked") == true) {
			$("#visibleRevertLastBid").prop("checked", true);
		} else {
			$("#readOnlyRevertLastBid").prop("checked", false);
			$("#visibleRevertLastBid").removeClass("readOnlyClass", true);
			$('#revertBidUser').parent().removeClass('has-error').find('.form-error').remove();
			$('#revertBidUser').val('').trigger('chosen:updated');
		}
		$.uniform.update();
	});

	$("#enableEvalCon").click(function() {

		if ($("#enableEvalCon").prop("checked") == true) {
			$("#visibleEvalCon").prop("checked", true);
		} else {
			$("#readOnlyEvalCon").prop("checked", false);
			$("#visibleEvalCon").removeClass("readOnlyClass", true);
			$('#evalConUser').parent().removeClass('has-error').find('.form-error').remove();
			$('#evalConUser').val('').trigger('chosen:updated');
		}
		$.uniform.update();
	});


	$("#visibleViewSupplierName").click(function() {

		if ($("#visibleViewSupplierName").prop("checked") == false) {
			$("#enableMasking").prop("checked", false);
		}
		$.uniform.update();
	});

	$("#visibleCloseEnvelope").click(function() {

		if ($("#visibleCloseEnvelope").prop("checked") == false) {
			$("#closeEnvelopeView").prop("checked", false);
		}
		$.uniform.update();
	});

	$("#visibleAddSupplier").click(function() {

		if ($("#visibleAddSupplier").prop("checked") == false) {
			$("#addSupplierView").prop("checked", false);
		}
		$.uniform.update();
	});

	$("#visibleAllowToSuspendEvent").click(function() {

		if ($("#visibleAllowToSuspendEvent").prop("checked") == false) {
			$("#suspendEventView").prop("checked", false);
		}
		$.uniform.update();
	});

	$("#visibleViewAuctionHall").click(function() {

		if ($("#visibleViewAuctionHall").prop("checked") == false) {
			$("#auctionHallView").prop("checked", false);
		}
		$.uniform.update();
	});

	$("#visibleRevertLastBid").click(function() {
		if ($("#visibleRevertLastBid").prop("checked") == false) {
			$("#revertLastBid").prop("checked", false);
		}
		$.uniform.update();
	});

	$("#visibleEvalCon").click(function() {
		if ($("#visibleEvalCon").prop("checked") == false) {
			$("#enableEvalCon").prop("checked", false);
		}
		$.uniform.update();
	});

	$("#readOnlyAllowToSuspendEvent").click(function() {

		if ($("#readOnlyAllowToSuspendEvent").prop("checked") == true) {

			$("#visibleAllowToSuspendEvent").prop("checked", true);
			$("#visibleAllowToSuspendEvent").addClass("readOnlyClass", true);
			$("#suspendEventView").prop("checked", true);
		} else {
			$("#visibleAllowToSuspendEvent").removeClass("readOnlyClass", true);
		}
		$.uniform.update();
	});

	$("#autoPopulateSupplier").click(function() {
		if ($("#autoPopulateSupplier").prop("checked") == true) {
			$("#supplierBasedOnCategory").prop("checked", true);
			$(".supplierBasedOnCategory").addClass("disabled", true);

			$.uniform.update();
		} else {

			$(".supplierBasedOnCategory").removeClass("disabled");

			$.uniform.update();

		}

	});

	$("#supplierBasedOnState").click(function() {
		if ($("#supplierBasedOnState").prop("checked") == true) {
			$("#restrictSupplierByState").prop("checked", true);
			$(".restrictSupplierByState").addClass("disabled", true);

			$.uniform.update();
		} else {

			$(".restrictSupplierByState").removeClass("disabled");

			$.uniform.update();

		}

	});

	$("#optionalSupplierTags").click(function() {
		if ($("#optionalSupplierTags").prop("checked") == true) {
			$("#visibleSupplierTags").prop("checked", true);
			$(".visibleSupplierTags").addClass("disabled", true);

			$.uniform.update();
		} else {

			$(".visibleSupplierTags").removeClass("disabled");

			$.uniform.update();

		}

	});

	$("#optionalGeographicalCoverage").click(function() {
		if ($("#optionalGeographicalCoverage").prop("checked") == true) {
			$("#visibleGeographicalCoverage").prop("checked", true);
			$(".visibleGeographicalCoverage").addClass("disabled", true);

			$.uniform.update();
		} else {

			$(".visibleGeographicalCoverage").removeClass("disabled");

			$.uniform.update();

		}

	});


	$("#visibleEvaluationDeclare").click(function() {
		if ($("#visibleEvaluationDeclare").prop("checked") == false) {
			$('#chosenEvaluationDeclaraton').val('').trigger('chosen:updated');
			$("#enableEvlDeclare").prop("checked", false);
		} else {
			$("#enableEvlDeclare").prop("checked", true);
		}
		$.uniform.update();
	});

	$("#enableEvlDeclare").click(function() {
		if ($("#enableEvlDeclare").prop("checked") == true) {
			$("#visibleEvaluationDeclare").prop("checked", true);
		} else {
			$('#chosenEvaluationDeclaraton').val('').trigger('chosen:updated');
			$("#readOnlyEvaluationDeclare").prop("checked", false);
		}
		$.uniform.update();
	});

	$("#readOnlyEvaluationDeclare").click(function() {
		if ($("#readOnlyEvaluationDeclare").prop("checked") == true) {
			$("#visibleEvaluationDeclare").prop("checked", true);
			$("#visibleEvaluationDeclare").addClass("readOnlyClass", true);
			$("#enableEvlDeclare").prop("checked", true);
		} else {
			$("#visibleEvaluationDeclare").removeClass("readOnlyClass");
		}
		$.uniform.update();
	});

	$('#chosenEvaluationDeclaraton').change(function() {
		$("#enableEvlDeclare").prop("checked", true);
		$("#visibleEvaluationDeclare").prop("checked", true);
	});

	$("#visibleSupplierDeclare").click(function() {
		if ($("#visibleSupplierDeclare").prop("checked") == false) {
			$('#choseSupplierDeclaration').val('').trigger('chosen:updated');
			$("#enableSupplierDeclare").prop("checked", false);
		} else {
			$("#enableSupplierDeclare").prop("checked", true);
		}
		$.uniform.update();
	});

	$("#enableSupplierDeclare").click(function() {
		if ($("#enableSupplierDeclare").prop("checked") == true) {
			$("#visibleSupplierDeclare").prop("checked", true);
		} else {
			$('#choseSupplierDeclaration').val('').trigger('chosen:updated');
			$("#readOnlySupplierDeclare").prop("checked", false);
		}
		$.uniform.update();
	});

	$("#readOnlySupplierDeclare").click(function() {
		if ($("#readOnlySupplierDeclare").prop("checked") == true) {
			$("#visibleSupplierDeclare").prop("checked", true);
			$("#visibleSupplierDeclare").addClass("readOnlyClass", true);
			$("#enableSupplierDeclare").prop("checked", true);
		} else {
			$("#visibleSupplierDeclare").removeClass("readOnlyClass");
		}
		$.uniform.update();
	});

	$('#choseSupplierDeclaration').change(function() {
		$("#enableSupplierDeclare").prop("checked", true);
		$("#visibleSupplierDeclare").prop("checked", true);
	});

	$(document).on("keyup", "#chosenCategoryAll_chosen .chosen-search input", function() {
		var industryCat = $.trim($(this).val());
		var industryCatOrig = $(this).val();
		var currentSearchBlk = $(this);
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		if (industryCat.length > 2) {
			$.ajax({
				url: 'searchCategory',
				data: {
					'search': industryCat
				},
				type: 'POST',
				dataType: 'json',
				beforeSend: function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success: function(data) {
					var html = '<option value="">Select Category</option>';
					if (data != '' && data != null && data.length > 0) {
						$.each(data, function(key, value) {
							html += '<option value="' + value.id + '">' + value.code + ' - ' + value.name + '</option>';
						});
					}
					$('#chosenCategoryAll').html(html);
					$("#chosenCategoryAll").trigger("chosen:updated");
					currentSearchBlk.val(industryCatOrig);
				},
				error: function(error) {
					console.log(error);
				},
				complete: function() {
					$('#loading').hide();
					// $("#" +
					// buttonId).prop("disabled",
					// false);
				}

			});
		}
	});

	$('input[type="checkbox"][title="Visible"]').not('.indusCatReadCheck').change(function(e) {
		/*
		 * $(this).closest('.check-wrapper.first').prev().removeClass('has-error').find('span.help-block.form-error').remove();
		 * if($(this).prop('checked')){ var labelVal = $.trim($(this).closest('.check-wrapper.first').prev().children('input, textarea,
		 * select').val()); if(labelVal == ''){ $(this).prop('checked',false); $.uniform.update();
		 * $(this).closest('.check-wrapper.first').prev().append('<span class="help-block form-error">This is a required field</span>').addClass('has-error');
		 * return false; } $(this).closest('.check-wrapper').siblings('.check-wrapper').find('input[type="checkbox"]').prop('checked',true);
		 * $.uniform.update(); } else
		 */
		if ($(this).prop('checked') == false) {
			$(this).closest('.check-wrapper').siblings('.check-wrapper').find('input[type="checkbox"]').prop('checked', false);

			$.uniform.update();
			$(this).closest('.check-wrapper').prev().find('input, textarea').val('');

		}
	});

	$('input[type="checkbox"][title="Read Only"], input[type="checkbox"][title="Optional"]').not('.approvalCheck, .suspendCheck, .awardCheck').change(function() {
		$(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().removeClass('has-error').find('.error-range.text-danger').remove();
		if ($(this).prop('checked')) {
			var labelVal = $.trim($(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().children('input, textarea, select').val());
			console.log("labelVal :" + labelVal);
			if (labelVal == '' && $(this).attr('title') == "Read Only") {
				$(this).prop('checked', false);
				$.uniform.update();
				$(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().append('<span class="error-range text-danger">This is a required field</span>');
				return false;
			}
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$(this).closest('.check-wrapper').siblings('.additional-check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$.uniform.update();
		}
	});
	
	$('input[type="checkbox"][title="Read Only"], input[type="checkbox"][title="Optional"]').not('.approvalCheck, .awardCheck, .suspendCheck').change(function() {
		$(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().removeClass('has-error').find('.error-range.text-danger').remove();
		if ($(this).prop('checked')) {
			var labelVal = $.trim($(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().children('input, textarea, select').val());
			console.log("labelVal :" + labelVal);
			if (labelVal == '' && $(this).attr('title') == "Read Only") {
				$(this).prop('checked', false);
				$.uniform.update();
				$(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().append('<span class="error-range text-danger">This is a required field</span>');
				return false;
			}
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$(this).closest('.check-wrapper').siblings('.additional-check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$.uniform.update();
		}
	});
	
	$("#feesDisabled").click(function() {

		$(this).closest('.check-wrapper').siblings('.partiFees').prev().find('.error-range.text-danger').remove();
		if ($(this).prop('checked')) {
			var feesAndDeposit = $.trim($(this).closest('.check-wrapper').siblings('.partiFees').prev().children('input, textarea, select').val());
			if (feesAndDeposit == '') {
				$(this).prop('checked', false);
				$.uniform.update();
				$(this).closest('.check-wrapper').siblings('.partiFees').prev().append('<span class="error-range text-danger">This is a required field</span>');
				return false;
			}
			$(this).closest('.check-wrapper').siblings('.partiFees').find('input[type="checkbox"]').prop('checked', true);
			$.uniform.update();
		}
	});

	$("#depositDisabled").click(function() {

		$(this).closest('.check-wrapper').siblings('.deposit').prev().find('.error-range.text-danger').remove();

		if ($(this).prop('checked')) {
			// var labelVal = $.trim($(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().children('input, textarea,
			// select').val());
			var feesAndDeposit = $.trim($(this).closest('.check-wrapper').siblings('.deposit').prev().children('input, textarea, select').val());
			console.log("feesAndDeposit :" + feesAndDeposit);
			if (feesAndDeposit == '') {
				$(this).prop('checked', false);
				$.uniform.update();
				$(this).closest('.check-wrapper').siblings('.deposit').prev().append('<span class="error-range text-danger">This is a required field</span>');
				return false;
			}
			$(this).closest('.check-wrapper').siblings('.deposit').find('input[type="checkbox"]').prop('checked', true);
			$.uniform.update();
		}
	});

	$('#idtimeExtRead').change(function() {
		if ($('#idTypeExtension').val() == 'AUTOMATIC') {
			if ($(this).prop('checked')) {
				$('.myErrorSpan').text('');
				if ($('input[type="text"][id="child1"]').val() == '') {
					$(this).prop('checked', false);
					readOnlyPlay("child1");
					return false;
				}

				if ($('input[type="text"][id="child2"]').val() == '') {
					$(this).prop('checked', false);
					readOnlyPlay("child2");
					return false;
				}
				if ($('input[type="text"][id="child3"]').val() == '') {
					$(this).prop('checked', false);
					readOnlyPlay("child3");
					return false;
				}
				$.uniform.update();
			}
		}
	});

	function readOnlyPlay(id) {
		$(this).prop('checked', false);
		$.uniform.update();
		$('input[type="text"][id="' + id + '"]').parent().append('<span class="help-block form-error myErrorSpan">This is a required field</span>').addClass('has-error');
	}

	// need to do after focus out validation
	/*
	 * $('input[type="text"][id="child2"]').focusout(function(){ $('input[type="text"][id="child2"]').parent().append('<span class="help-block
	 * form-error myErrorSpan2">This is a required field readonly is true</span>').addClass('has-error'); $.uniform.update();
	 * if($('#idtimeExtRead').prop('checked') && ($('input[type="text"][id="child2"]').val() == '')){ } $.uniform.update(); });
	 */
	$('#idBidderDisRadio').change(function() {
		if ($(this).prop('checked')) {
			var inputValue = $('input[type="text"][id="child4"]').val();
			$('.myErrorSpan1').text('');
			if (inputValue === '') {
				$(this).prop('checked', false);
				$.uniform.update();
				$('input[type="text"][id="child4"]').parent().append('<span class="help-block form-error myErrorSpan1">This is a required field</span>').addClass('has-error');
				return false;
			} else if(!isValidBidderDisqualify(inputValue)) {
				$(this).prop('checked', false);
				$.uniform.update();
				return false;
			}
			$.uniform.update();
		}
	});

	$('#idIsBiddingMinValueFromPreviousDisabled').change(function() {

		if ($(this).prop('checked')) {
			$('.myErrorSpan1').text('');

			var inputValue = $('input[type="text"][id="idBiddingMinValue"]').val();

			if (inputValue === '' && $('#chkPrevious').prop('checked')) {
				// If input is empty
				$(this).prop('checked', false);
				$.uniform.update();
				$('input[type="text"][id="idBiddingMinValue"]').parent().append('<span class="help-block form-error myErrorSpan1">This is a required field</span>').addClass('has-error');
				return false;
			} else if (!isValidBiddingMinValue(inputValue)) {
				// If input is incorrect
				$(this).prop('checked', false);
				$.uniform.update();
				return false;
			}

			$.uniform.update();
		}
	});

// Validation function based on the provided data-validation-regexp
	function isValidBiddingMinValue(input) {
		var regexPattern = /^\d{1,9}(\.\d{1,2})?$/;
		return regexPattern.test(input);
	}

	function isValidBidderDisqualify(input) {
		var regexPattern = /^\d{0,9}?$/;
		return regexPattern.test(input);
	}


	$('input.approvalCheck[type="checkbox"][title="Read Only"], input.approvalCheck[type="checkbox"][title="Optional"]').change(function() {
		$('#addSelect').parent().removeClass('has-error').find('.form-error').remove();
		if ($(this).prop('checked')) {
			var labelVal = $.trim($(this).closest('.row').next().next().find('select').select2("val"));
			if ((labelVal == '' || $(this).closest('.row').next().next().find('select').length == 0) && $(this).attr('title') == "Read Only") {
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
	
	$('input.suspendCheck[type="checkbox"][title="Read Only"], input.suspendCheck[type="checkbox"][title="Optional"]').change(function() {
		$('#suspendAddSelect').parent().removeClass('has-error').find('.form-error').remove();
		if ($(this).prop('checked')) {
		console.log("&&&&&&&&&&");
			var labelVal = $.trim($(this).closest('.row').next().next().find('select').select2("val"));
			if ((labelVal == '' || $(this).closest('.row').next().next().find('select').length == 0) && $(this).attr('title') == "Read Only") {
				$(this).prop('checked', false);
				$.uniform.update();
				$('#suspendAddSelect').parent().addClass('has-error').append('<span class="help-block form-error"> Please select at least 1 approval</span>');
				return false;
			}
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$('#idEnableSuspendApproval').prop('checked', true);
			$('.suspApprChB').find('span').addClass('checked');
			$('#suspendAddSelect').removeClass('disabled');			
			$.uniform.update();
		}
		$('#suspendAddSelect').parent().removeClass('has-error').find('.form-error').remove();
	});
	
	$('input.awardCheck[type="checkbox"][title="Read Only"], input.awardCheck[type="checkbox"][title="Optional"]').change(function() {
		$('#awardAddSelect').parent().removeClass('has-error').find('.form-error').remove();
		console.log("@@@@@@@@@@@@@@@@@");
		if ($(this).prop('checked')) {
		console.log("&&&&&&&&&&");
			var labelVal = $.trim($(this).closest('.row').next().next().find('select').select2("val"));
			console.log("labelVal >>> " + labelVal + "  Title  : " + $(this).attr('title'));
			if ((labelVal == '' || $(this).closest('.row').next().next().find('select').length == 0) && $(this).attr('title') == "Read Only") {
				$(this).prop('checked', false);
				$.uniform.update();
				$('#awardAddSelect').parent().addClass('has-error').append('<span class="help-block form-error"> Please select at least 1 approval</span>');
				console.log("Done......");
				return false;
			}
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$('#idEnableAwardApproval').prop('checked', true);
			$('.awardApprChB').find('span').addClass('checked');
			$('#awardAddSelect').removeClass('disabled');			
			$.uniform.update();
		}
		$('#awardAddSelect').parent().removeClass('has-error').find('.form-error').remove();
	});
	

	$('select').change(function() {
		$(this).parent().removeClass('has-error').children('span.help-block.form-error').remove();
	});

	$('.check-wrapper.first').prev().find('input, textarea').blur(function() {
		$(this).closest('.col-md-3').removeClass('has-error').find('.error-range.text-danger').remove();
		if ($.trim($(this).val()) != '') {
			$(this).closest('.col-md-3').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
		} else {
			$(this).closest('.col-md-3').siblings('.check-wrapper.first').next().find('input[type="checkbox"]').prop('checked', false);
		}
		$.uniform.update();
	});

	$('.check-wrapper.second').prev().find('input, textarea').blur(function() {
		$(this).closest('.col-md-2').removeClass('has-error').find('.error-range.text-danger').remove();
		if ($.trim($(this).val()) != '') {
			$(this).closest('.col-md-2').siblings('.check-wrapper.second').find('input[type="checkbox"]').prop('checked', true);
		} else {
			$(this).closest('.col-md-2').siblings('.check-wrapper.second').next().find('input[type="checkbox"]').prop('checked', false);
		}
		$.uniform.update();
	});

	$('.check-wrapper.first').prev().find('select').chosen().change(function() {
		if ($.trim($(this).val()) != '') {
			$(this).closest('.col-md-3').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			$("#visibleCurrency").addClass("disabled", true);
			$("#visibleDecimal").addClass("disabled", true);
			$(this).closest('.col-md-3').siblings('.check-wrapper.first').prev().removeClass('has-error').find('.error-range.text-danger').remove();

		} else {
			$(this).closest('.col-md-3').siblings('.check-wrapper.first').next().find('input[type="checkbox"]').prop('checked', false);
			$("#visibleCurrency").removeClass("disabled");
			$("#visibleDecimal").removeClass("disabled");
		}
		$.uniform.update();
	});

	$('.partiFees').prev().find('select').chosen().change(function() {
		if ($.trim($(this).val()) != '') {
			$(this).closest('.col-md-2').siblings('.partiFees').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
		} else {
			$(this).closest('.col-md-2').siblings('.partiFees').siblings('.check-wrapper.first').next().find('input[type="checkbox"]').prop('checked', false);
		}
		$.uniform.update();
	});
	$('.deposit').prev().find('select').chosen().change(function() {
		if ($.trim($(this).val()) != '') {
			$(this).closest('.col-md-2').siblings('.deposit').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
		} else {
			$(this).closest('.col-md-2').siblings('.deposit').siblings('.check-wrapper.first').next().find('input[type="checkbox"]').prop('checked', false);
		}
		$.uniform.update();
	});

	$("#visibleCurrency").click(function() {
		$(this).closest('.check-wrapper').prev().removeClass('has-error').find('.error-range.text-danger').remove();
		var labelVal = $.trim($(this).closest('.check-wrapper').prev().children('input, textarea, select').val());
		$("#visibleCurrency").removeClass("disabled");
		console.log("labelVal :" + labelVal);
		if (labelVal == '' && $(this).attr('title') == "Visible") {
			$.uniform.update();
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().append('<span class="error-range text-danger">This is a required field</span>');
			$("#visibleCurrency").removeClass("disabled");
		} else {
			$(this).closest('.check-wrapper').find('input[type="checkbox"]').prop('checked', true);
			$("#visibleCurrency").addClass("disabled", true);
			$.uniform.update();
		}
		$.uniform.update();
	});

	$("#visibleDecimal").click(function() {
		$(this).closest('.check-wrapper').prev().removeClass('has-error').find('.error-range.text-danger').remove();
		var labelVal = $.trim($(this).closest('.check-wrapper').prev().children('input, textarea, select').val());
		$("#visibleDecimal").removeClass("disabled");
		console.log("labelVal :" + labelVal);
		if (labelVal == '' && $(this).attr('title') == "Visible") {
			$.uniform.update();
			$(this).closest('.check-wrapper').siblings('.check-wrapper.first').prev().append('<span class="error-range text-danger">This is a required field</span>');
			$("#visibleDecimal").removeClass("disabled");
		} else {
			$(this).closest('.check-wrapper').find('input[type="checkbox"]').prop('checked', true);
			$("#visibleDecimal").addClass("disabled", true);
			$.uniform.update();
		}
	});

});

$(document).ready(function() {

});
// For on change Auction data show
$(document).ready(function() {

	$(".rfi_div").hide();
	$(".notrfi_div").hide();
	$("#idStatus").change(function() {
		$(this).find("option:selected").each(function() {

			if ($(this).attr("value") == "RFI") {
				$('.rfi_div').show();
				$(".notrfi_div").hide();
			}

			if ($(this).attr("value") != "RFI") {
				$(".rfi_div").hide();
				$(".notrfi_div").show();
			}
			if ($(this).attr("value") == "RFA") {

				$(".hideSelect").show();
				var auctionType = $('#idAuctionType').val();
				if (auctionType == "FORWARD_ENGISH" || auctionType == "REVERSE_ENGISH" || auctionType == "FORWARD_SEALED_BID" || auctionType == "REVERSE_SEALED_BID") {
					$(".auctionRulesTemp").show();
				} else {
					$(".auctionRulesTemp").hide();
				}

				$(".hideSupplierSelect").hide();
				$(".submissionVal").hide();
			} else {

				$(".hideSupplierSelect").show();
				$(".hideSelect").hide();
				$(".submissionVal").show();
			}
		});
	}).change();

	// Multiple industry category search
	var indCate = $('#industryCatArrVal').val();

	var jsonIndCate = '';
	if (indCate !== undefined && indCate !== '') {
		jsonIndCate = jQuery.parseJSON(indCate);
		var indCat = [];
		$(jsonIndCate).each(function(i, item) {
			console.log("==" + i + "==" + item.id);
			indCat.push(item.id);
		});

		$('#industryCatArr').val(indCat);
	}

	$('.token-input-list').remove();
	$("#demo-input-local").tokenInput(getBuyerContextPath('searchCategory'), {
		minChars: 1,
		method: 'POST',
		hintText: "Start typing to search categories...",
		noResultsText: "No results",
		searchingText: "Searching...",
		queryParam: "search",
		propertyToSearch: "name",
		propertyToSearchCode: "code",
		minChars: 3,
		tokenLimit: 30,
		preventDuplicates: true,
		// setting saved value on update mode
		prePopulate: jsonIndCate
	});

	$(document).on("change", "#demo-input-local", function(e) {
		e.preventDefault();
		$(this).closest('.col-md-3').removeClass('has-error').find('.error-range.text-danger').remove();
		// adding arr of industry category
		var indCat = [];
		select1 = $("#token-input-demo-input-local").parent().parent().find(".token-input-token");
		select1.each(function() {
			indCat.push($(this).find('input').val());
		});
		if (indCat.length == 0) {
			if ($.trim($(this).find('input').val()) != '') {
				$(this).closest('.col-md-3').siblings('.check-wrapper.first').find('input[type="checkbox"]').prop('checked', true);
			} else {
				$(this).closest('.col-md-3').siblings('.check-wrapper.first').next().find('input[type="checkbox"]').prop('checked', false);
			}
			$.uniform.update();

		}
		// console.log(indCat);
		$('#industryCatArr').val(indCat);
	});

});

$(document).delegate('.decimalChange', 'change', function(e) {
	var decimalLimit = $('.decimalChange').val();
	if (decimalLimit === '' || decimalLimit === undefined) {
		decimalLimit = $('#buyerDecimal').val();
	}
	var budgetAmount = parseFloat($('#budgetAmount').val().replace(/\,|\s|\#/g, ''));
	if ($('#budgetAmount').val() === '' || $('#budgetAmount').val() === undefined) {
		$('#budgetAmount').val('');
	} else {
		budgetAmount = !isNaN(budgetAmount) ? budgetAmount : 0;
		$('#budgetAmount').val(ReplaceNumberWithCommas((budgetAmount).toFixed(decimalLimit)));
	}

	var historicaAmount = parseFloat($('#historicaAmount').val().replace(/\,|\s|\#/g, ''));
	if ($('#historicaAmount').val() === '' || $('#historicaAmount').val() === undefined) {
		$('#historicaAmount').val('');
	} else {
		historicaAmount = !isNaN(historicaAmount) ? historicaAmount : 0;
		$('#historicaAmount').val(ReplaceNumberWithCommas((historicaAmount).toFixed(decimalLimit)));
	}

	var estimatedBudget = parseFloat($('#estimatedBudget').val().replace(/\,|\s|\#/g, ''));
	if ($('#estimatedBudget').val() === '' || $('#estimatedBudget').val() === undefined) {
		$('#estimatedBudget').val('');
	} else {
		estimatedBudget = !isNaN(estimatedBudget) ? estimatedBudget : 0;
		$('#estimatedBudget').val(ReplaceNumberWithCommas((estimatedBudget).toFixed(decimalLimit)));
	}

});

$(document).delegate('input[name="templateFieldBinding.budgetAmount"]', 'change', function(e) {
	var decimalLimit = $('.decimalChange').val();
	if (decimalLimit === '' || decimalLimit === undefined) {
		decimalLimit = $('#buyerDecimal').val();
	}
	var budgetAmount = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
	if ($(this).val() === '' || $(this).val() === undefined) {
		$('#budgetAmount').val('');
	} else {
		budgetAmount = !isNaN(budgetAmount) ? budgetAmount : 0;
		$('#budgetAmount').val(ReplaceNumberWithCommas((budgetAmount).toFixed(decimalLimit)));
	}
});

$(document).delegate('input[name="templateFieldBinding.historicAmount"]', 'change', function(e) {
	var decimalLimit = $('.decimalChange').val();
	if (decimalLimit === '' || decimalLimit === undefined) {
		decimalLimit = $('#buyerDecimal').val();
	}
	var historicaAmount = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
	if ($(this).val() === '' || $(this).val() === undefined) {
		$('#historicaAmount').val('');
	} else {
		historicaAmount = !isNaN(historicaAmount) ? historicaAmount : 0;
		$('#historicaAmount').val(ReplaceNumberWithCommas((historicaAmount).toFixed(decimalLimit)));
	}
});

$(document).delegate('input[name="templateFieldBinding.estimatedBudget"]', 'change', function(e) {
	var decimalLimit = $('.decimalChange').val();
	if (decimalLimit === '' || decimalLimit === undefined) {
		decimalLimit = $('#buyerDecimal').val();
	}
	var estimatedBudget = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
	if ($(this).val() === '' || $(this).val() === undefined) {
		$('#estimatedBudget').val('');
	} else {
		estimatedBudget = !isNaN(estimatedBudget) ? estimatedBudget : 0;
		$('#estimatedBudget').val(ReplaceNumberWithCommas((estimatedBudget).toFixed(decimalLimit)));
	}
});


function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}

/*
 * $(document).delegate('input[name="templateFieldBinding.budgetAmount"]', 'change', function(e) { var decimalLimit = $('.decimalChange').val(); var
 * budgetAmount = parseFloat($(this).val().replace(/\,|\s|\#/g, '')); budgetAmount = !isNaN(budgetAmount) ? budgetAmount : 0;
 * console.log(ReplaceNumberWithCommas((budgetAmount).toFixed(decimalLimit)));
 * $('#templateFieldBinding.budgetAmount').val(ReplaceNumberWithCommas((templateFieldBinding.budgetAmount).toFixed(decimalLimit))); });
 * 
 * $(document).delegate('input[name="templateFieldBinding.historicaAmount"]', 'change', function(e) { var decimalLimit = $('.decimalChange').val();
 * var historicaAmount = parseFloat($(this).val().replace(/\,|\s|\#/g, '')); historicaAmount = !isNaN(historicaAmount) ? historicaAmount : 0;
 * console.log(ReplaceNumberWithCommas((historicaAmount).toFixed(decimalLimit)));
 * $('#historicaAmount').val(ReplaceNumberWithCommas((historicaAmount).toFixed(decimalLimit))); });
 */

$('#saveAsRfxTemplate').click(function() {
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	$('.customError').html('');
	$('#tempName').val("");
	$('#tempDescription').val("");
	$("#templateSaveAsPopup").dialog({
		modal: true,
		minWidth: 300,
		width: '50%',
		maxWidth: 400,
		minHeight: 150,
		dialogClass: "",
		show: "fadeIn",
		draggable: true,
		resizable: false,
		dialogClass: "dialogBlockLoaded"
	});
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
	$('#templateSaveAsPopup').find('#tempId').val($('#templateId').val());
	$('.ui-dialog-title').text(templateSaveLabel);
});

$(document).on("focusout", '#tempName', function(e) {
	$('.customError').html('');
});

$('#saveAsTemp').click(function() {

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var templateId = $('#templateId').val();
	var templateName = $('#tempName').val();
	var templateDesc = $('#tempDescription').val();
	if (templateName != '') {
		$.ajax({
			url: 'copyTemplate',
			data: ({
				templateId: templateId,
				templateName: templateName,
				templateDesc: templateDesc
			}),
			type: "POST",
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
				$('div[id=idMessageJsp]').html("");
				$('#loading').show();
			},
			success: function(data, textStatus, request) {
				var info = request.getResponseHeader('success');
				$('p[id=idGlobalSuccessMessage]').html(info);
				$('div[id=idGlobalSuccess]').show();
				$("#templateSaveAsPopup").dialog('close');
				window.location.href = getContextPath() + "/buyer/rfxTemplate/editRfxTemplate?id=" + data + "&success=" + info;

			},
			error: function(request, textStatus, errorThrown) {
				console.log("Error :" + request.getResponseHeader('error'));
				$('.customError').html('<font color="red">' + request.getResponseHeader('error') + '</font>');
				// $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				// $('div[id=idGlobalError]').show();
				$('#loading').hide();
				// $("#templateSaveAsPopup").dialog('close');
			},
			complete: function() {
				$('#loading').hide();

			}
		});
	}
});
