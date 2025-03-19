var decimalLimit = $('#eventDecimal').val();
var item_level_order = '';
var item_order = '';

var totalPage = 500;
var visiblePage = 5;
$(document).ready(function() {
	$(".Invited-Supplier-List").click(function() {
		$(this).toggleClass("small-accordin-tab");
	});
	var decimalLimit = $('#eventDecimal').val();

	$(document).delegate(".saveSupplierBq", "click", function(e) {
		e.preventDefault();
		if (!$('#supplierBqList').isValid()) {
			return;
		}
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		e.preventDefault();

		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();

		var totalAmountForValidation = $('#amountAfterTax').text();
		var rfteventId = $('#supplierBqList #rfteventId').val();
		var remarks = $("#supplierBqList #remarks").val();

		var ajaxUrl = getContextPath() + '/supplier/saveSupplierBQDetails/' + getEventType() + '/' + rfteventId + '/' + totalAmountForValidation +'/';
		var eventBqId = $('#supplierBqList #eventBqId').val();
		var supplierBqList = [];
		$('#supplierBqList table.deta.ph_table.table.bq-table tr').each(function() {
			var dataRow = {};
			$(this).find('input, select').each(function() {
				dataRow[$(this).attr('name')] = $(this).val().replace(/,/g, '');
			});
			dataRow['rfteventId'] = $('#supplierBqList').find('[name="rfteventId"]').val();
			// dataRow['eventBqId'] =
			// $('#supplierBqList').find('[name="eventBqId"]').val();

			dataRow['id'] = $(this).attr('data-item');
			if ($('#supplierBqList').find('[name="additionalTax"]').length) {
				dataRow['additionalTax'] = $('#supplierBqList').find('[name="additionalTax"]').val().replace(/,/g, '');
			} else {
				dataRow['additionalTax'] = 0;
			}
			if ($('#supplierBqList').find('[name="additionalTaxDescription"]').length) {
				dataRow['taxDescription'] = $('#supplierBqList').find('[name="additionalTaxDescription"]').val();
			} else {
				dataRow['taxDescription'] = '';
			}

			dataRow['remarks'] = $('#supplierBqList').find('[name="remarks"]').val();

			dataRow['bqId'] = $('#supplierBqList').find('[name="eventBqId"]').val();
			// dataRow['totalAmountForValidation'] = $('#amountAfterTax').val();
			supplierBqList.push(dataRow);
		});
		// console.log(JSON.stringify(supplierBqList));
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			data : JSON.stringify(supplierBqList),
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader("Content-Type", "application/json");
				xhr.setRequestHeader(header, token);
			},
			success : function(data, textStatus, request) {
				if (request.getResponseHeader('success')) {
					var success = request.getResponseHeader('success');
					$.jGrowl(success, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					});
				}
				// console.log("data" + data);
				window.location.href = getContextPath() + "/supplier/viewBqList/" + getEventType() + '/' + rfteventId + '?success=Bill%20of%20Quantity%20completed%20successfully';

			},
			error : function(request, textStatus, errorThrown) {
				console.log("ERROR : " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					// document.getElementById('#idGlobalSuccessMessage').innerHTML = "";
					// $('#newDiv').clear();
					// document.getElementById("newDiv").innerHTML = "";
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
					var error = request.getResponseHeader('error');
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});

	});

	$(document).delegate('#supplierBqList table.bq-table tr input, #supplierBqList table.bq-table tr select', 'change', function(e) {
		e.preventDefault();

		console.log("====Extra column=============" + $(this).hasClass('extra-column'));
		// No need to call Ajax for 10 extra fields
		if ($(this).hasClass('extra-column')) {
			return;
		}
		
		if (e.target.type === 'text') {
			this.value = this.value.replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}"));
		}

		if (!$('#supplierBqList').isValid()) {
			doCompute($(this), false);
		} else {
			doCompute($(this), true);
		}

	});

	$(document).delegate('#additionalTax', 'change', function(e) {
		doComputeAdditionalTax($('#supplierBqList table.bq-table tr input , #supplierBqList table.bq-table tr select'), true);

	});

	function doComputeAdditionalTax(block, showSuccess) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var dataRow = {};
		var currentBlock = block;
		var eventId = $('#supplierBqList #rfteventId').val();

		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();

		var ajaxUrl = getContextPath() + '/supplier/updateSupplierAdditionalTax/' + getEventType() + '/' + eventId;
		dataRow['id'] = $('#supplierBqList #eventBqId').val();
		if ($('#supplierBqList').find('[name="additionalTax"]').length) {
			dataRow['additionalTax'] = $('#supplierBqList').find('[name="additionalTax"]').val().replace(/,/g, '');
		} else {
			dataRow['additionalTax'] = 0;
		}
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			data : JSON.stringify(dataRow),
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader("Content-Type", "application/json");
				xhr.setRequestHeader(header, token);
			},
			success : function(data, textStatus, request) {
				var ItemId = currentBlock.closest('tr').attr('data-item');
				$('.saveSupplierBq').prop("disabled", false);
				revisedGrandTotalVal = parseFloat(data.revisedGrandTotal);
				grandTotalVal = parseFloat(data.grandTotal);
				totalAfterTax = parseFloat(data.totalAfterTax);
				additionalTax = parseFloat(data.additionalTax);
				$('#grandTotalOfBq').text(ReplaceNumberWithCommas(grandTotalVal.toFixed(decimalLimit)));
				$('[name="additionalTax"]').val(ReplaceNumberWithCommas(additionalTax.toFixed(decimalLimit)));
				$('#amountAfterTax').text(ReplaceNumberWithCommas(totalAfterTax.toFixed(decimalLimit)));
				if (revisedSubmissionMode == true && revisedGrandTotalVal != null && revisedGrandTotalVal != totalAfterTax) {
					$('#amountAfterTax').text(ReplaceNumberWithCommas(totalAfterTax.toFixed(decimalLimit)) + ' (' + ReplaceNumberWithCommas(revisedGrandTotalVal.toFixed(decimalLimit)) + ')');
					$('.saveSupplierBq').addClass('disabled');
					$('#amountAfterTax').removeClass('color-green');
					$('#amountAfterTax').addClass('color-red');
					$('p[id=idGlobalErrorMessage]').html('Your Bill Of Quantities total does not match with your final Auction bid price. Please revise and submit');
					$('div[id=idGlobalError]').show();
					$('div[id=idGlobalSuccess]').hide();

				} else if (revisedSubmissionMode == true && revisedGrandTotalVal != null && revisedGrandTotalVal == totalAfterTax) {
					$('#amountAfterTax').text(ReplaceNumberWithCommas(totalAfterTax.toFixed(decimalLimit)));
					$('#amountAfterTax').removeClass('color-red');
					$('#amountAfterTax').addClass('color-green');
					$('p[id=idGlobalSuccessMessage]').html('Total matches with your Auction bid price. Please click save to save your price.');
					$('div[id=idGlobalSuccess]').show();
					$('div[id=idGlobalError]').hide();

				} else {
					// document.getElementById("newDiv").innerHTML = "";
					// $('#newDiv').clear();
				}

				if (revisedSubmissionMode == true && revisedGrandTotalVal != null && revisedGrandTotalVal == totalAfterTax) {
					$('.saveSupplierBq').removeClass('disabled');
				}

				if (request.getResponseHeader('error')) {
					// $('#newDiv').clear();
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					var error = request.getResponseHeader('error');
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
				} else {
					if (showSuccess) {
						var success = request.getResponseHeader('success');
						$.jGrowl(success, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-green'
						});
					}

				}
			},
			error : function(request, textStatus, errorThrown) {
				if (request.responseText != null) {
					try {
						$('.saveSupplierBq').prop("disabled", true);
						var revisedGrandTotal = parseFloat(request.responseJSON.revisedGrandTotal);
						var totalAfterTax = parseFloat(request.responseJSON.totalAfterTax);
						totalAfterTax = (totalAfterTax).toFixed(decimalLimit);
						$('#amountAfterTax').removeClass('color-green');
						$('#amountAfterTax').text(totalAfterTax + '(' + revisedGrandTotal + ')');
						$('#amountAfterTax').addClass('color-red');

					} catch (e) {
						console.log(e);
					}
				}
				if (request.getResponseHeader('error')) {
					// $('#newDiv').clear();
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					var error = request.getResponseHeader('error');
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});

	}

	function doCompute(block, showSuccess) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();

		var totalAmountForValidation = $('#amountAfterTax').text();
		var rfteventId = $('#supplierBqList #rfteventId').val();
		var ajaxUrl = getContextPath() + '/supplier/updateSupplierBQItemDetails/' + getEventType();
		var eventBqId = $('#supplierBqList #eventBqId').val();
		var remarks = $("#supplierBqList #remarks").val();

		var currentBlock = block;
		var dataRow = {};
		block.closest('tr').find('input, select').each(function() {
			dataRow[$(this).attr('name')] = $(this).val().replace(/,/g, '');
		});
		if ($('#supplierBqList').find('[name="additionalTax"]').length) {
			dataRow['additionalTax'] = $('#supplierBqList').find('[name="additionalTax"]').val().replace(/,/g, '');
		} else {
			dataRow['additionalTax'] = 0;
		}
		if ($('#supplierBqList').find('[name="additionalTaxDescription"]').length) {
			dataRow['taxDescription'] = $('#supplierBqList').find('[name="additionalTaxDescription"]').val();
		} else {
			dataRow['taxDescription'] = '';
		}
		dataRow['eventId'] = $('#supplierBqList').find('[name="rfteventId"]').val();
		dataRow['eventBqId'] = $('#supplierBqList').find('[name="eventBqId"]').val();
		dataRow['remarks'] = $('#supplierBqList').find('[name="remarks"]').val();
		dataRow['id'] = block.closest('tr').attr('data-item');
		dataRow['position'] = block.attr('data-pos');
		// console.log(JSON.stringify(dataRow));
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			data : JSON.stringify(dataRow),
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader("Content-Type", "application/json");
				xhr.setRequestHeader(header, token);
			},
			success : function(data, textStatus, request) {

				$('.saveSupplierBq').prop("disabled", false);
				if (currentBlock.closest('tr').next('tr').hasClass('in')) {
					currentBlock.closest('tr').next('tr').remove();
				}
				html = renderChildBqHtml(data);
				var ItemId = currentBlock.closest('tr').attr('data-item');
				currentBlock.closest('tr').replaceWith(html);
				revisedGrandTotalVal = parseFloat(data.supplierBq.revisedGrandTotal);
				grandTotalVal = parseFloat(data.supplierBq.grandTotal);
				totalAfterTax = parseFloat(data.supplierBq.totalAfterTax);
				additionalTax = parseFloat(data.supplierBq.additionalTax);
				$('#grandTotalOfBq').text(ReplaceNumberWithCommas(grandTotalVal.toFixed(decimalLimit)));
				$('[name="additionalTax"]').val(ReplaceNumberWithCommas(additionalTax.toFixed(decimalLimit)));
				$('#amountAfterTax').text(ReplaceNumberWithCommas(totalAfterTax.toFixed(decimalLimit)));
				if (revisedSubmissionMode == true && revisedGrandTotalVal != null && revisedGrandTotalVal != totalAfterTax) {
					$('#amountAfterTax').text(ReplaceNumberWithCommas(totalAfterTax.toFixed(decimalLimit)) + ' (' + ReplaceNumberWithCommas(revisedGrandTotalVal.toFixed(decimalLimit)) + ')');
					$('.saveSupplierBq').addClass('disabled');
					$('#amountAfterTax').removeClass('color-green');
					$('#amountAfterTax').addClass('color-red');
					$('.complete-event').addClass('disabled');
					$('p[id=idGlobalErrorMessage]').html('Your Bill Of Quantities total does not match with your final Auction bid price. Please revise and submit');
					$('div[id=idGlobalError]').show();
					$('div[id=idGlobalSuccess]').hide();

				} else if (revisedSubmissionMode == true && revisedGrandTotalVal != null && revisedGrandTotalVal == totalAfterTax) {
					$('#amountAfterTax').text(ReplaceNumberWithCommas(totalAfterTax.toFixed(decimalLimit)));
					$('#amountAfterTax').removeClass('color-red');
					$('#amountAfterTax').addClass('color-green');
					$('.complete-event').removeClass('disabled');
					$('p[id=idGlobalSuccessMessage]').html('Total matches with your Auction bid price. Please click save to save your price.');
					$('div[id=idGlobalSuccess]').show();
					$('div[id=idGlobalError]').hide();

				} else {
					// document.getElementById("newDiv").innerHTML = "";
					// $('#newDiv').clear();
				}

				if (revisedSubmissionMode == true && revisedGrandTotalVal != null && revisedGrandTotalVal == totalAfterTax) {
					$('.saveSupplierBq').removeClass('disabled');
				}

				$('.bqlistDetails').find('[data-item="' + ItemId + '"]').find('.custom-select').uniform();
				$('.bqlistDetails').find('[data-item="' + ItemId + '"]').find(".selector").append('<i class="glyph-icon icon-caret-down"></i>');
				$('.bqlistDetails').find('[data-item="' + ItemId + '"]').find(".selector").find('span').css('border', 'none');
				// console.log("data" + data);

				if (request.getResponseHeader('error')) {
					// $('#newDiv').clear();
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					var error = request.getResponseHeader('error');
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
				} else {
					if (showSuccess) {
						var success = request.getResponseHeader('success');
						$.jGrowl(success, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-green'
						});
					}

				}
			},
			error : function(request, textStatus, errorThrown) {
				if (request.responseText != null) {
					try {
						// removed as currant row get reverted
						// $('.saveSupplierBq').prop("disabled", true);
						var data = JSON.parse(request.responseText);
						html = renderChildBqHtml(data);
						var ItemId = currentBlock.closest('tr').attr('data-item');
						currentBlock.closest('tr').replaceWith(html);
						grandTotalVal = parseFloat(data.supplierBq.grandTotal);
						totalAfterTax = parseFloat(data.supplierBq.totalAfterTax);
						additionalTax = parseFloat(data.supplierBq.additionalTax);
						$('#grandTotalOfBq').text(ReplaceNumberWithCommas(grandTotalVal.toFixed(decimalLimit)));
						$('[name="additionalTax"]').val(ReplaceNumberWithCommas(additionalTax.toFixed(decimalLimit)));
						$('#amountAfterTax').text(ReplaceNumberWithCommas(totalAfterTax.toFixed(decimalLimit)));
						$('.bqlistDetails').find('[data-item="' + ItemId + '"]').find('.custom-select').uniform();
						$('.bqlistDetails').find('[data-item="' + ItemId + '"]').find(".selector").append('<i class="glyph-icon icon-caret-down"></i>');
						$('.bqlistDetails').find('[data-item="' + ItemId + '"]').find(".selector").find('span').css('border', 'none');
					} catch (e) {
						// TODO: handle exception
					}
				}
				if (request.getResponseHeader('error')) {
					// $('#newDiv').clear();
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					var error = request.getResponseHeader('error');
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	}

	$(document).delegate('.msg-popup button:not(.saveRemarksBtn)', 'click', function(e) {
		e.preventDefault();
		var bqItemId = $(this).closest('tr').attr('data-item');
		$("#remarkPopUp").dialog({
			modal : true,
			minWidth : 300,
			width : '400',
			dialogClass : "",
			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded",
			position : {
				my : "left top"
			}
		});
		$('.ui-widget-overlay').addClass('bg-white opacity-60');
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		e.preventDefault();
		$("#remarkPopUp").find('[name="itemId"]').val(bqItemId);
		$("#remarkPopUp").find('textarea').val('');
		var ajaxUrl = getContextPath() + '/supplier/getSupplierBQRemarks/' + getEventType() + '/' + bqItemId;
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			// data : data,
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader("Content-Type", "application/json");
				xhr.setRequestHeader(header, token);
			},
			success : function(data) {
				remarksHtml(data);
			},
			error : function(request, textStatus, errorThrown) {
				console.log("ERROR : " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

	/*
	 * $(document).delegate('.unitPrice', 'click', function(e) {
	 * 
	 * });
	 */
	$(document).delegate('.deleteRemark', 'click', function(e) {
		e.preventDefault();
		var remarkId = $(this).closest('.row.remark').attr('id');
		var itemId = $("#remarkPopUp").find('[name="itemId"]').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() + '/supplier/removeSupplierBQRemarks/' + getEventType() + '/' + remarkId;
		var data = {
			'id' : remarkId,
			'bqItemId' : itemId
		};
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader("Content-Type", "application/json");
				xhr.setRequestHeader(header, token);
			},
			success : function(data) {
				remarksHtml(data);
				$("#remarkPopUp").find('textarea').val('');
			},
			error : function(request, textStatus, errorThrown) {
				console.log("ERROR : " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

	$(document).delegate('#saveRemarksBtn', 'click', function(e) {
		e.preventDefault();
		var comment = $("#remarkPopUp").find('textarea').val();
		var itemId = $("#remarkPopUp").find('[name="itemId"]').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() + '/supplier/saveSupplierBQRemarks/' + getEventType();
		var data = {
			'comment' : comment,
			'bqItemId' : itemId
		};
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			data : JSON.stringify(data),
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader("Content-Type", "application/json");
				xhr.setRequestHeader(header, token);
			},
			success : function(data) {
				remarksHtml(data);
				$("#remarkPopUp").find('textarea').val('');
			},
			error : function(request, textStatus, errorThrown) {
				console.log("ERROR : " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

});

$(document).delegate('input[name="additionalTax"]', 'keyup', function(e) {
	var additionalTax = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
	var grandTotal = parseFloat($('#grandTotalOfBq').text().replace(/\,|\s|\#/g, ''));
	additionalTax = !isNaN(additionalTax) ? additionalTax : 0;
	grandTotal = !isNaN(grandTotal) ? grandTotal : 0;
	var afterTaxTotal = ReplaceNumberWithCommas((grandTotal + additionalTax).toFixed(decimalLimit));
	$('#amountAfterTax').text(afterTaxTotal);
	// $('[name="additionalTax"]').val(ReplaceNumberWithCommas(additionalTax.toFixed(decimalLimit)));
});
$(document).delegate('input[name="additionalTax"]', 'change', function(e) {
	if ($(this).val() === '' || $(this).val() === undefined) {
		var additionalTax = 0;
	} else {
		this.value = this.value.replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"));
		
		additionalTax = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
	}
	$(this).val(ReplaceNumberWithCommas(additionalTax.toFixed(decimalLimit)));
});
$(document).delegate('.viewSupplierBillOfQuantity', 'click', function(e) {
	e.preventDefault();

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var rfteventId = $(this).attr('rftevent-id');
	var eventBqId = $(this).attr('eventBq-id');
	var remarks = $("#supplierBqList #remarks").val();
	var bqStatus = $(this).attr('bq-status');
	console.log("bqStatus ---"+bqStatus);

	var ajaxUrl = getContextPath() + '/supplier/viewBqDetails/' + getEventType() + '/' + eventBqId;
	$.ajax({
		url : ajaxUrl,
		type : "POST",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader("Content-Type", "application/json");
			xhr.setRequestHeader(header, token);
		},
		success : function(data) {
			// var html = supplierBqList(data, rfteventId, eventBqId);
			var html = supplierSearchBqList(data, rfteventId, eventBqId, bqStatus);
			$(".doc-fir-inner1").hide();
			$('.bqlistDetails').html(html);
			if ((allowedFields != '' && revisedSubmissionMode == false) || allowBqChanges ) {
				if(allowBqChanges){
					allowedFields='.tab-link > a, back_to_BQ,#bqItemSearch';
					disableFormFields(allowedFields);
					$('#supplierBqList').find('.back_to_BQ').removeClass("disabled");
					$('#supplierBqList').find('[name="taxType"]').parent('td').addClass('disabled');
				}else{
					disableFormFields(allowedFields);
				}
			}
			
			$('.bqlistDetails').find('.custom-select').uniform();
			$('.bqlistDetails').find(".selector").append('<i class="glyph-icon icon-caret-down"></i>');
			$('.bqlistDetails').find(".selector").find('span').css('border', 'none');
			
			console.log("===================1===================");
			var bqPageLength = $('#bqPageLength').val();

			// setting value for select Page Length on user preferences
			if (bqPageLength === undefined || bqPageLength === '') {
				bqPageLength = 50;
			}
			var totalBqItemCount = data.totalBqItemCount;
			console.log("totalBqItemCount :" + totalBqItemCount + "== bqPageLength :" + bqPageLength + "===totalBqItemCount/bqPageLength :" + totalBqItemCount / bqPageLength);
			totalPage = Math.ceil(totalBqItemCount / bqPageLength);

			if (totalPage == 0) {
				totalPage = 1;
			}

			if (totalPage < 5) {
				visiblePage = totalPage;
			}

			$('#pagination').twbsPagination({
				totalPages : totalPage,
				visiblePages : visiblePage
			});

			console.log(" ==" + bqPageLength);
			$("#selectPageLen").val(bqPageLength).trigger("chosen:updated");

		},
		error : function(request, textStatus, errorThrown) {

			console.log("ERROR :  " + request.getResponseHeader('error'));
			if (request.getResponseHeader('error')) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
				$('div[id=idGlobalError]').show();
			}
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}

function supplierBqList(item, rfteventId, eventBqId) {
	var decimalLimit = $('#eventDecimal').val();
	var grandTotalVal = 0;
	var revisedGrandTotal = 0;
	var totalAfterTax = 0;
	var additionalTax = 0;
	var additionalTaxDesc = "";
	var countNewData = 1;
	var html = '';
	var buyerSubmit = false;
	$.each(item, function(i, data) {
		if (data.supplierBq.buyerSubmited != null && data.supplierBq.buyerSubmited == true) {
			buyerSubmit = true;
		}
		if (i > 0) {
			// return false;
		} else {
			var eventCurr = $('#eventCurrency').val();
			html += '<h3>' + data.supplierBq.name + '</h3>';
			html += '<div class=""><select path="" class="chosen-select" id="chooseSection">';
			html += '<option value="">&nbsp;</option>';
			html += '</select></div>';

			html += '<form id="supplierBqList">';
			html += '<div class="main_table_wrapper ph_table_border payment height-full1 mega">';
			html += '<table cellpadding="0" cellspacing="0" border="0" width="100%" class="ph_table font-set table" style="top: 0px;">';
			html += '<thead><tr><th class="width_50 width_50_fix">No</th><th class="width_300_fix width_300"> Item Name </th>';
			html += '<th class="text-center width_100_fix width_100">UOM</th><th class="text-right width_200_fix width_200">Quantity</th>';
			html += '<th class="width_200 width_200_fix text-right">Unit Price </th>';
			html += '<th class="width_200 width_200_fix text-right">Total Amount </th>';
			if (!isWithoutTax) {
				html += '<th class="width_200 width_200_fix align-center"> Tax </th>';
			}
			if (data.supplierBq.field1ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left"> ' + (data.supplierBq.field1Label != undefined ? data.supplierBq.field1Label : "") + ' </th>';
			}
			if (data.supplierBq.field2ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left"> ' + (data.supplierBq.field2Label != undefined ? data.supplierBq.field2Label : "") + ' </th>';
			}
			if (data.supplierBq.field3ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left"> ' + (data.supplierBq.field3Label != undefined ? data.supplierBq.field3Label : "") + ' </th>';
			}
			if (data.supplierBq.field4ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left"> ' + (data.supplierBq.field4Label != undefined ? data.supplierBq.field4Label : "") + ' </th>';
			}

			if (data.supplierBq.field5ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left"> ' + (data.supplierBq.field5Label != undefined ? data.supplierBq.field5Label : "") + ' </th>';
			}
			if (data.supplierBq.field6ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left"> ' + (data.supplierBq.field6Label != undefined ? data.supplierBq.field6Label : "") + ' </th>';
			}
			if (data.supplierBq.field7ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left"> ' + (data.supplierBq.field7Label != undefined ? data.supplierBq.field7Label : "") + ' </th>';
			}
			if (data.supplierBq.field8ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left"> ' + (data.supplierBq.field8Label != undefined ? data.supplierBq.field8Label : "") + ' </th>';
			}
			if (data.supplierBq.field9ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left"> ' + (data.supplierBq.field9Label != undefined ? data.supplierBq.field9Label : "") + ' </th>';
			}
			if (data.supplierBq.field10ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left"> ' + (data.supplierBq.field10Label != undefined ? data.supplierBq.field10Label : "") + ' </th>';
			}
			if (!isWithoutTax) {
				html += '<th class="width_200 width_200_fix text-right">Total Amt with Tax</th>';
			}
			/* html += '<th class="width_100 width_100_fix text-center"> </th>'; */
			html += '</tr></thead></table>';
		}
	});
	html += '<table class="deta ph_table table bq-table"><tbody id="bqItemList">';
	html += '<input type="hidden" name="rfteventId" id="rfteventId" value="' + rfteventId + '"/>';
	html += '<input type="hidden" name="eventBqId" id="eventBqId" value="' + eventBqId + '"/>';

	$.each(item, function(i, data) {
		var totalAmount = '';

		if (data.totalAmount != undefined) {
			totalAmount = data.totalAmount;
		}
		html += '<tr style="border-bottom: 0 !important; border-top: 2px solid #ddd !important;" data-item="' + data.id + '"><td class="width_50 width_50_fix"><span>' + data.level + '.' + data.order + '</span>&nbsp; </td>';
		html += '<td class="width_300 width_300_fix"><span>' + data.itemName + '</span>';
		html += data.itemDescription ? '<a data-toggle="collapse" class="s3_view_desc" data-target="#demo-' + data.id + '">View Description</a>' : "";
		// html += '<div id="demo-' + data.id + '" class="collapse table-contant">' + data.itemDescription + '</div>' + '</td>';
		html += '<td class="text-center width_100 width_100_fix"></td>';
		html += '<td class="width_200 width_200_fix text-center"></td>';
		html += '<td class="width_200 width_200_fix text-center"></td>';
		html += '<td class="width_200 width_200_fix align-center">';
		html += '<span>&nbsp;</span></td>';

		if (!isWithoutTax) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}

		if (data.supplierBq.field1ToShowSupplier) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}
		if (data.supplierBq.field2ToShowSupplier) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}
		if (data.supplierBq.field3ToShowSupplier) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}
		if (data.supplierBq.field4ToShowSupplier) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}

		if (data.supplierBq.field5ToShowSupplier) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}
		if (data.supplierBq.field6ToShowSupplier) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}
		if (data.supplierBq.field7ToShowSupplier) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}
		if (data.supplierBq.field8ToShowSupplier) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}
		if (data.supplierBq.field9ToShowSupplier) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}
		if (data.supplierBq.field10ToShowSupplier) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}
		if (!isWithoutTax) {
			html += '<td class="width_200 width_200_fix align-center"></td>';
		}
		/*
		 * html += '<td class="width_100 width_100_fix text-center"><div class="msg-popup">'; html += '<div class="massage-popup-main"
		 * style="display:none;"><div class="hi-ca"></div>'; html += '<textarea class="form-control">Write Your Remark...</textarea>'; html += '<div
		 * class="btn-msg marg-top-15">'; html += '<button class="btn btn-info ph_btn_extra_small saveRemarksBtn">Save</button>'; html += '<button
		 * class="btn btn-black ph_btn_extra_small marg-left-10">Cancel</button>'; html += '</div></div>'; html += '</div></td>';
		 */
		html += '</tr>';

		html += '<tr id="demo-' + data.id + '" class="collapse" colspan="7" style=" background: #fff;">';
		html += '<td  style=" background: #fff;">';
		html += '</td>';
		html += '<td colspan="7" style=" background: #fff;">';
		html += '<p class=" " style="margin: 5px 0 0 0; font-size: 11px; max-height: 150px; text-align: left;margin-top: -1%;">' + data.itemDescription + '</p>';
		html += '</td>';
		html += '</tr>';

		if (data.children != undefined && data.children != null) {
			$.each(data.children, function(i, child) {
				html += renderChildBqHtml(child);
				if ((child.supplierBq && child.supplierBq.grandTotal) != undefined && (child.supplierBq && child.supplierBq.grandTotal) != null && grandTotalVal == 0) {
					revisedGrandTotal = parseFloat(child.supplierBq.revisedGrandTotal);
					grandTotalVal = parseFloat(child.supplierBq.grandTotal);
					totalAfterTax = parseFloat(child.supplierBq.totalAfterTax);
					additionalTax = parseFloat(child.supplierBq.additionalTax);
					additionalTaxDesc = child.supplierBq.taxDescription;
				}
			});
		}
	});
	var eventCurr = $('#eventCurrency').val();
	html += '</tbody></table></div>';
	// Dont show additional tax if auction type is without tax
	if (!isWithoutTax) {
		html += '<div class="total-with-tax pad_all_15 marg-top-10 marg-bottom-10">';
		html += '<div class="total-with-tax-inner"><label>Grand Total (' + eventCurr + '): </label><p id="grandTotalOfBq">';
		html += ReplaceNumberWithCommas(grandTotalVal.toFixed(decimalLimit));
		html += '</p></div></div>';
		html += '<div class="additinol-text pad_all_15 marg-top-20 marg-bottom-20">';
		html += '<div class="add_tex">';
		html += '<label>&nbsp;</label>';
		html += '<input type="text" name="additionalTax" id="additionalTax" class="validateregex form-control" data-validation-regexp="^(?!\\.?$)\\d{0,10}(\\.\\d{0,6})?$"  value="';
		html += '' + ((additionalTax != null && additionalTax != undefined && !isNaN(additionalTax)) ? ReplaceNumberWithCommas(additionalTax.toFixed(decimalLimit)) : '0.00') + '" placeholder="Additional Tax Amount">';
		html += '</div>';
		html += '<div class="Description_lower">';
		html += '<label>ADDITIONAL TAX DESC</label>';
		html += '<textarea class="form-control" name="additionalTaxDescription"  >' + (additionalTaxDesc != undefined ? additionalTaxDesc : '') + '</textarea>';
		html += '</div>';
		html += '</div>';
	}
	html += '</div>';
	html += '<div class="total-with-tax-final pad_all_15 marg-top-10 marg-bottom-10">';
	html += '<div class="total-with-tax-inner">';
	html += '<label>' + (isWithoutTax ? 'Grand Total' : 'Total After Tax') + ' (' + eventCurr + '): </label><p class="color-' + (revisedSubmissionMode == true && revisedGrandTotal != null && revisedGrandTotal != totalAfterTax ? 'red' : 'green')
			+ '" id="amountAfterTax">';
	html += (((totalAfterTax != undefined && !isNaN(totalAfterTax)) ? ReplaceNumberWithCommas(totalAfterTax.toFixed(decimalLimit)) : (grandTotalVal != undefined ? ReplaceNumberWithCommas(grandTotalVal.toFixed(decimalLimit)) : '0.00')));
	if (revisedSubmissionMode == true && revisedGrandTotal != totalAfterTax) {
		html += ' (' + ReplaceNumberWithCommas(revisedGrandTotal.toFixed(decimalLimit)) + ')';
		$('p[id=idGlobalErrorMessage]').html('Your Bill Of Quantities total does not match with your final Auction bid price. Please revise and submit');
		$('div[id=idGlobalError]').show();
		$('div[id=idGlobalSuccess]').hide();

	}
	html += '</p></div></div>';
	html += '<div class="row">';
	html += '<div class="col-md-12 col-xs-12 col-sm-12">';
	html += '<button class="btn btn-black ph_btn_midium back_to_BQ hvr-pop hvr-rectangle-out1">Back to Bill of Quantity</button>';
	if (buyerSubmit == false || revisedSubmissionMode == true) {
		html += '<button id="pppp" class="btn btn-info ph_btn_midium marg-left-10 hvr-pop hvr-rectangle-out saveSupplierBq ' + (revisedSubmissionMode == true && revisedGrandTotal != null && revisedGrandTotal != totalAfterTax ? 'disabled' : '')
				+ '">Save</button>';
	} else if (allowedFields == '') {
		html += '<button id="pppp" class="btn btn-info ph_btn_midium marg-left-10 hvr-pop hvr-rectangle-out saveSupplierBq">Confirm</button>';
	}
	html += '</div></div></form>';
	return html;
}

function renderChildBqHtml(child) {
	// console.log("decimalLimit :"+$('#eventDecimal').val());
	var decimalLimit = $('#eventDecimal').val();
	var html = '';
	html += '<tr style="border-bottom: 0 !important; border-top: 2px solid #ddd !important;" class=" sub_item" data-item="' + child.id + '">'
	html += child.totalAmountWithTax != undefined ? '<input type="hidden" name="totalAmountWithTax" id="" value="' + child.totalAmountWithTax + '"/>' : "";
	html += '<input type="hidden" name="itemName" id="" value="' + child.itemName + '"/>';
	html += '<input type="hidden" name="quantity" id="" value="' + child.quantity + '"/>';
	html += '<td class="width_50 width_50_fix"> ' + child.level + '.' + child.order + ' &nbsp;</td>';
	html += '<td class="width_300 width_300_fix">';
	if (child.priceType == 'BUYER_FIXED_PRICE') {
		html += '<span class="bs-label label-success" style="color: #fff">Buyer Fixed Price</span>&nbsp;';
	} else if (child.priceType == 'TRADE_IN_PRICE') {
		html += '<span class="bs-label label-info" style="color: #fff">Trade In Price</span>&nbsp;';
	}
	html += '' + child.itemName;
	html += (child.itemDescription ? '<a data-toggle="collapse" class="s3_view_desc" data-target="#demo-' + child.id + '">View Description</a>' : "");
	// html += '<div id="demo-' + child.id + '" class="collapse table-contant">' + child.itemDescription + '</div>' + '</td>';
	html += '<td class="text-center width_100 width_100_fix">  ';
	if (child.uom) {
		html += child.uom.uom;
	}
	html += '</td>';
	html += '<td class="width_200 width_200_fix align-right">   ' + ReplaceNumberWithCommas(child.quantity.toFixed(decimalLimit)) + '</td>';
	html += '<td class="width_200 width_200_fix text-center"><input type="text" data-pos="1"  name="unitPrice" class="validate form-control text-right" value="';
	html += (child.unitPrice != undefined ? ReplaceNumberWithCommas(child.unitPrice.toFixed(decimalLimit)) : "") + '" ';
	html += child.priceType != undefined ? (child.priceType == 'BUYER_FIXED_PRICE' ? ' readonly="readonly" ' : '') : '';
	html += ' data-validation="required" data-validation-length="max10" ';
	html += '/></td>';
	html += '<td class="width_200 width_200_fix align-center"> <input data-pos="2"  class="validate form-control text-right" type="text" name="totalAmount" value="';
	html += (child.totalAmount != undefined ? ReplaceNumberWithCommas(child.totalAmount.toFixed(decimalLimit)) : "") + '" ';
	html += child.priceType != undefined ? (child.priceType == 'BUYER_FIXED_PRICE' || disableTotalAmount ? ' readonly="readonly" ' : '') : '';
	html += '/></td>';
	if (!isWithoutTax) {
		html += '<td class="width_200 width_200_fix align-right">';
		html += '<input data-pos="3" type="text" name="tax" class="validate form-control width_38per text-right" value="';
		html += child.tax != undefined ? ReplaceNumberWithCommas(child.tax.toFixed(decimalLimit)) : "";
		html += '"><select name="taxType" id="custom_' + child.id + '" class="custom-select" ' + (allowedFields != '' ? 'disabled="true"' : '') + ' data-pos="4">';
		if (child.taxTypeList != undefined && child.taxTypeList != null) {
			$.each(child.taxTypeList, function(i, taxType) {
				html += '<option value="' + taxType + '"';
				html += child.taxType == taxType ? " selected='selected' " : "";
				html += '>' + taxType + '</option>';
			});
		}
		html += '</select>';
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field1ToShowSupplier) {
		var isBuyer = (child.supplierBq.field1FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="5" name="field1"  data-validation="' + ((child.supplierBq.field1Required != undefined && child.supplierBq.field1Required  )? "required" : "") + '" class="extra-column  form-control "  style="text-align:left;"  value="' + (child.field1 != undefined ? child.field1 : "") + '" >';
		} else {
			html += child.field1 != undefined ? child.field1 : "";
			html += '<input type="hidden" name="field1"  value="' + (child.field1 != undefined ? child.field1 : "") + '">';
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field2ToShowSupplier) {
		var isBuyer = (child.supplierBq.field2FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="6" name="field2" data-validation="' + ((child.supplierBq.field2Required != undefined && child.supplierBq.field2Required  )? "required" : "") + '" class="extra-column  form-control " style="text-align:left;" value="' + (child.field2 != undefined ? child.field2 : "") + '">';
		} else {
			html += child.field2 != undefined ? child.field2 : "";
			html += '<input type="hidden" name="field2"  value="' + (child.field2 != undefined ? child.field2 : "") + '">';
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field3ToShowSupplier) {
		var isBuyer = (child.supplierBq.field3FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="7" name="field3" data-validation="' + ((child.supplierBq.field3Required != undefined && child.supplierBq.field3Required  )? "required" : "") + '" class="extra-column  form-control  " style="text-align:left;" value="' + (child.field3 != undefined ? child.field3 : "") + '">';
		} else {
			html += child.field3 != undefined ? child.field3 : "";
			html += '<input type="hidden" name="field3"  value="' + (child.field3 != undefined ? child.field3 : "") + '">';
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field4ToShowSupplier) {
		var isBuyer = (child.supplierBq.field4FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="8" name="field4" data-validation="' + ((child.supplierBq.field4Required != undefined && child.supplierBq.field4Required  )? "required" : "") + '" class="extra-column  form-control " style="text-align:left;" value="' + (child.field4 != undefined ? child.field4 : "") + '">';
		} else {
			html += child.field4 != undefined ? child.field4 : "";
			html += '<input type="hidden" name="field4"  value="' + (child.field4 != undefined ? child.field4 : "") + '">';
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field5ToShowSupplier) {
		var isBuyer = (child.supplierBq.field5FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="9" name="field5" data-validation="' + ((child.supplierBq.field5Required != undefined && child.supplierBq.field5Required  )? "required" : "") + '" class="extra-column  form-control " style="text-align:left;" value="' + (child.field5 != undefined ? child.field5 : "") + '">';
		} else {
			html += child.field5 != undefined ? child.field5 : "";
			html += '<input type="hidden" name="field5"  value="' + (child.field5 != undefined ? child.field5 : "") + '">';
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field6ToShowSupplier) {
		var isBuyer = (child.supplierBq.field6FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="10" name="field6" data-validation="' + ((child.supplierBq.field6Required != undefined && child.supplierBq.field6Required  )? "required" : "") + '" class="extra-column  form-control " style="text-align:left;" value="' + (child.field6 != undefined ? child.field6 : "") + '">';
		} else {
			html += child.field6 != undefined ? child.field6 : "";
			html += '<input type="hidden" name="field6"  value="' + (child.field6 != undefined ? child.field6 : "") + '">';
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field7ToShowSupplier) {
		var isBuyer = (child.supplierBq.field7FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="11" name="field7" data-validation="' + ((child.supplierBq.field7Required != undefined && child.supplierBq.field7Required  )? "required" : "") + '" class="extra-column  form-control " style="text-align:left;" value="' + (child.field7 != undefined ? child.field7 : "") + '">';
		} else {
			html += child.field7 != undefined ? child.field7 : "";
			html += '<input type="hidden" name="field7"  value="' + (child.field7 != undefined ? child.field7 : "") + '">';
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field8ToShowSupplier) {
		var isBuyer = (child.supplierBq.field8FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="13" name="field8" data-validation="' + ((child.supplierBq.field8Required != undefined && child.supplierBq.field8Required  )? "required" : "") + '" class="extra-column  form-control " style="text-align:left;" value="' + (child.field8 != undefined ? child.field8 : "") + '">';
		} else {
			html += child.field8 != undefined ? child.field8 : "";
			html += '<input type="hidden" name="field8"  value="' + (child.field8 != undefined ? child.field8 : "") + '">';
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field9ToShowSupplier) {
		var isBuyer = (child.supplierBq.field9FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="14" name="field9" data-validation="' + ((child.supplierBq.field9Required != undefined && child.supplierBq.field9Required  )? "required" : "") + '" class="extra-column  form-control " style="text-align:left;" value="' + (child.field9 != undefined ? child.field9 : "") + '">';
		} else {
			html += child.field9 != undefined ? child.field9 : "";
			html += '<input type="hidden" name="field9"  value="' + (child.field9 != undefined ? child.field9 : "") + '">';
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field10ToShowSupplier) {
		var isBuyer = (child.supplierBq.field10FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="15" name="field10" data-validation="' + ((child.supplierBq.field10Required != undefined && child.supplierBq.field10Required  )? "required" : "") + '" class="extra-column  form-control " style="text-align:left;" value="' + (child.field10 != undefined ? child.field10 : "") + '">';
		} else {
			html += child.field10 != undefined ? child.field10 : "";
			html += '<input type="hidden" name="field10"  value="' + (child.field10 != undefined ? child.field10 : "") + '">';
		}
		html += '</td>';
	}

	if (!isWithoutTax) {
		html += '<td class="width_200 width_200_fix align-right">';
		html += child.totalAmountWithTax != undefined ? ReplaceNumberWithCommas(child.totalAmountWithTax.toFixed(decimalLimit)) : "";
		html += '</td>';
	}

	/*
	 * html += '<td class="width_100 width_100_fix text-center"><div class="msg-popup">'; html += '<div class="massage-popup-main"
	 * style="display:none;"><div class="hi-ca"></div>'; html += '<textarea class="form-control">Write Your Remark...</textarea>'; html += '<div
	 * class="btn-msg marg-top-15">'; html += '<button class="btn btn-info ph_btn_extra_small saveRemarksBtn">Save</button>'; html += '<button
	 * class="btn btn-black ph_btn_extra_small marg-left-10">Cancel</button>'; html += '</div></div><button class="remark-toggle-btn"> <img src="' +
	 * getContextPath() + '/resources/images/msg-con.png" alt="pr"> </button>'; html += '</div></td>';
	 */
	html += '</tr>';

	html += '<tr id="demo-' + child.id + '" class="collapse" style=" background: #eef7fc;">';
	html += '<td  style=" background: #eef7fc;">';
	html += '</td>';
	html += '<td colspan="7" style=" background: #eef7fc;;">';
	html += '<p class=" " style="margin: 5px 0 0 0; font-size: 11px; max-height: 150px; text-align: left;margin-top: -1%;">' + child.itemDescription + '</p>';
	html += '</td>';

	if (child.supplierBq && child.supplierBq.field1ToShowSupplier) {
		var isBuyer = (child.supplierBq.field1FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field2ToShowSupplier) {
		var isBuyer = (child.supplierBq.field2FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field3ToShowSupplier) {
		var isBuyer = (child.supplierBq.field3FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field4ToShowSupplier) {
		var isBuyer = (child.supplierBq.field4FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field5ToShowSupplier) {
		var isBuyer = (child.supplierBq.field5FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field6ToShowSupplier) {
		var isBuyer = (child.supplierBq.field6FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field7ToShowSupplier) {
		var isBuyer = (child.supplierBq.field7FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field8ToShowSupplier) {
		var isBuyer = (child.supplierBq.field8FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field9ToShowSupplier) {
		var isBuyer = (child.supplierBq.field9FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field10ToShowSupplier) {
		var isBuyer = (child.supplierBq.field10FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}

	html += '</tr>';

	return html;
}

// $(".validate").keydown(function(e){
$(document).delegate('.validate', 'keydown', function(e) {

	// Allow: backspace, delete, tab, escape, enter and .
	if ($.inArray(e.keyCode, [ 46, 8, 9, 27, 13, 110, 190 ]) !== -1 ||
	// Allow: Ctrl+A, Command+A
	(e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) ||
	// Allow: home, end, left, right, down, up
	(e.keyCode >= 35 && e.keyCode <= 40)) {
		// let it happen, don't do anything
		return;
	}
	// Ensure that it is a number and stop the keypress
	if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
		e.preventDefault();
	}
});

$(document).delegate('#additionalTax', 'keydown', function(e) {
	if ($.inArray(e.keyCode, [ 46, 8, 9, 27, 13, 110, 190 ]) !== -1 || (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) || (e.keyCode >= 35 && e.keyCode <= 40)) {
		return;
	}
	if ((e.keyCode >= 48 && e.keyCode <= 57) || (e.keyCode >= 96 && e.keyCode <= 105)) {
		var stringtc = $(this).val();
		var beforeD = stringtc;
		if (stringtc.indexOf('.') > -1) {
			tmp = stringtc.split(".");
			beforeD = tmp[0];
			// console.log(tmp[1].length);
			if (tmp[1].length > 5)
				e.preventDefault();
			else
				return;
		}
		if (beforeD.length > 9)
			e.preventDefault();
	}
	if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
		e.preventDefault();
	}
});

function remarksHtml(data) {
	var commentHtml = '';
	$.each(data, function(i, comment) {
		commentHtml += '<div class="row remark" id="' + comment.id + '"><p class="col-md-12"><span class="width100 pull-left align-left">';
		commentHtml += comment.userName != undefined ? comment.userName : "";
		commentHtml += ' : <a href="" class="deleteRemark pull-right"><i class="fa fa-times-circle"></i></a></span><span class="width100 pull-left align-left">' + comment.comment + '</span><span class="width100 pull-right align-right">';
		commentHtml += comment.createdDate != undefined ? comment.createdDate : "";
		commentHtml += '</span></p></div>';
	});
	if (commentHtml == '') {
		$("#remarkPopUp").find('.ph_table_border.remarksBlock').addClass('border-none');
	} else {
		$("#remarkPopUp").find('.ph_table_border.remarksBlock').removeClass('border-none');
	}
	$("#remarkPopUp").find('.reminderList.marginDisable').html(commentHtml);
	$('#remarkPopUp .remarksBlock').scrollTop($('.remarksBlock').height() + 100);
}

$(document).delegate('#resetButton', 'click', function(e) {
	e.preventDefault();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	item_level_order = '';
	$("#bqItemSearch").val("");
	$("#chooseSection").val('').trigger("chosen:updated");
	$('#pagination').find('li.active').removeClass(".active");
	var pageLen = "50";
	pageLen = $('#selectPageLen option:selected').val();
	var pageLength = parseInt(pageLen);
	var eventType = getEventType();

	// reset pagination to 1
	var pagination = jQuery('.pagination').data('twbsPagination');
	var pageToShow = 1;
	pagination.show(pageToShow);

	var searchVal = "";
	var choosenVal = "";
	var selectPageNo = 1;
	var pageNo = parseInt(selectPageNo);

	var eventBqId = $('#supplierBqList #eventBqId').val();
	var rfteventId = $('#supplierBqList #rfteventId').val();

	$.ajax({
		type : "POST",
		url : getContextPath() + '/supplier/getEventBqForResetValue',
		data : {
			'rfteventId' : rfteventId,
			'eventBqId' : eventBqId,
			'eventType' : eventType,
			'searchVal' : searchVal,
			'pageNo' : pageNo,
			'pageLength' : pageLength,
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success : function(data) {
			// console.log(data);
			var html = searchRenderBqItemGrid(data);
			$(".doc-fir-inner1").hide();
			$('#bqItemList').html(html);
			if ((allowedFields != '' && revisedSubmissionMode == false) || allowBqChanges ) {
				if(allowBqChanges){
					allowedFields='.tab-link > a, back_to_BQ,#bqItemSearch';
					disableFormFields(allowedFields);
					$('#supplierBqList').find('.back_to_BQ').removeClass("disabled");
					$('#supplierBqList').find('[name="taxType"]').parent('td').addClass('disabled');
				}else{
					disableFormFields(allowedFields);
				}
			}
			
			$('.bqlistDetails').find('.custom-select').uniform();
			$('.bqlistDetails').find(".selector").append('<i class="glyph-icon icon-caret-down"></i>');
			$('.bqlistDetails').find(".selector").find('span').css('border', 'none');
			
			// set pagination according to result
			visiblePage = 5;
			totalPage = Math.ceil(data.totalBqItemCount / pageLength);

			if (totalPage == 0) {
				totalPage = 1;
			}

			if (totalPage < 5) {
				visiblePage = totalPage;
			}
			var opts = {
				totalPages : 500,
				visiblePages : 5
			};

			$('.pagination').twbsPagination('destroy');
			$('.pagination').twbsPagination($.extend(opts, {
				totalPages : totalPage,
				visiblePages : visiblePage
			}));
			var pagination = jQuery('.pagination').data('twbsPagination');
			pagination.show(pageNo);

			// Building Level filter drop down
			var filterTable = '<option value="">&nbsp;</option>';
			$.each(data.levelOrderList, function(i, item) {
				filterTable += '<option value="">' + item.level + '.' + item.order + '</option>'; // '<li class="active-result">' + item.level +'.'
				// + item.order + '</li>';
			});
			$('#chooseSection').html(filterTable).trigger("chosen:updated");

		},
		error : function(request, textStatus, errorThrown) {
			console.log("ERROR : " + request.getResponseHeader('error'));
			if (request.getResponseHeader('error')) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
				$('div[id=idGlobalError]').show();
			}
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

$(document).delegate('.page-link', 'click', function(e) {
	e.preventDefault();
	var searchVal = $('#bqItemSearch').val();
	var choosenSection = ''; // $('#chooseSection option:selected').text();
	item_level_order = '';
	var pageNo = parseInt($('#pagination').find('li.active').text());
	var pageLen = "50";
	if ($('#selectPageLen option:selected').text()) {
		pageLen = $('#selectPageLen').val();
	}
	var pageLength = parseInt(pageLen);
	// console.log("searchVal :"+searchVal + " choosenSection : "+choosenSection +" page :"+pageNo + " pageLength : "+pageLength) ;
	console.log("================2======================");
	$("#chooseSection").val('').trigger("chosen:updated");
	var isChooseSection = false;
	searchFilterBqItem(searchVal, pageNo, pageLength, choosenSection, isChooseSection);
});

$(document).ready(function() {

	var bqPageLength = $('#bqPageLength').val();
	if (bqPageLength === undefined || bqPageLength === '') {
		bqPageLength = 50;
	}
	console.log(" bqPageLength :" + bqPageLength);
	$("#selectPageLen").val(bqPageLength).trigger("chosen:updated");

	$(document).delegate('input[name="bqItemSearch"]', 'keyup', function(e) {
		e.preventDefault();
		console.log(" ===bqItemSearch=== ");
		var pageLen = "50";
		pageLen = $('#selectPageLen option:selected').val();
		var pageLength = parseInt(pageLen);
		var selectPageNo = $('#pagination').find('li.active').text();

		var pagination = jQuery('.pagination').data('twbsPagination');
		var pageToShow = 1;
		pagination.show(pageToShow);
		var pageNo = parseInt(pageToShow);
		// var pageNo = parseInt(selectPageNo);

		if ($(this).val() == "") {
			var searchVal = $(this).val();
			var choosenSection = ''; // $('#chooseSection option:selected').val();
			var pageLen = "50";
			pageLen = $('#selectPageLen option:selected').val();
			var pageLength = parseInt(pageLen);
			var selectPageNo = $('#pagination').find('li.active').text();

			var pagination = jQuery('.pagination').data('twbsPagination');

			// var pageNo = parseInt(selectPageNo);

			// New requirement for filter fetch selected filter value page records.
			if (choosenSection) {
				var selectedIndex = $('#chooseSection option:selected').index();
				var pageToShow = selectedIndex / pageLength;
				var pageNo = parseInt(pageToShow);
				if (pageToShow % 1 !== 0) {
					pageNo = parseInt(pageToShow) + 1;
				}
				if (pageNo == 0) {
					pageNo = 1;
				}
				pagination.show(pageNo);
				choosenSection = "";
			} else {
				var pageToShow = 1;
				pagination.show(pageToShow);
				var pageNo = parseInt(pageToShow);
			}
			console.log(" pageNo  : " + pageNo);
			var isChooseSection = false;
			searchFilterBqItem(searchVal, pageNo, pageLength, choosenSection, isChooseSection);

		}
		if (this.value.length > 2) {
			var searchVal = $(this).val();
			var choosenSection = ''; // $('#chooseSection option:selected').text();
			console.log(" pageLength  :" + pageLength + " searchVal :" + searchVal + "pageNo :" + pageNo + " choosenSection  : ");
			var isChooseSection = false;
			searchFilterBqItem(searchVal, pageNo, pageLength, choosenSection, isChooseSection);
		}
	});

	$(document).delegate('#selectPageLen', 'change', function(e) {
		var pageLen = "50";
		pageLen = $('#selectPageLen option:selected').val();
		var searchVal = $('#bqItemSearch').val();
		var pageLength = parseInt(pageLen);
		var selectPageNo = $('#pagination').find('li.active').text();
		var choosenSection = $('#chooseSection option:selected').text();
		var pageNo = parseInt(selectPageNo);

		// New requirement for filter fetch selected filter value page records.
		if (choosenSection) {
			var pagination = jQuery('.pagination').data('twbsPagination');
			var selectedIndex = $('#chooseSection option:selected').index();
			var pageToShow = selectedIndex / pageLength;
			var pageNo = parseInt(pageToShow);
			if (pageToShow % 1 !== 0) {
				pageNo = parseInt(pageToShow) + 1;
			}
			if (pageNo == 0) {
				pageNo = 1;
			}
			pagination.show(pageNo);
			choosenSection = "";
		} else {
			item_level_order = '';
		}

		var isChooseSection = false;
		// console.log(" pageLength : "+pageLen + "searchVal :"+searchVal) ;
		searchFilterBqItem(searchVal, pageNo, pageLength, choosenSection, isChooseSection);
	});

	$(document).delegate('#chooseSection', 'change', function(e) {
		if ($('#chooseSection option:selected').text()) {
			var choosenVal = $('#chooseSection option:selected').val();
		}
		var chooseSection = choosenVal;
		var searchVal = $('#bqItemSearch').val();
		var pageLen = "50";
		pageLen = $('#selectPageLen').val();
		var pageLength = parseInt(pageLen);
		var selectPageNo = $('#pagination').find('li.active').text();

		var pagination = jQuery('.pagination').data('twbsPagination');

		var selectedIndex = $('#chooseSection option:selected').index();
		console.log("selectedIndex :" + selectedIndex);

		item_order = choosenVal.split(".")[1];
		item_level_order = chooseSection.replace(".", "_");
		item_level_order = "item_" + item_level_order;
		if (!$('#chooseSection option:selected').text()) {
			item_level_order = '';
		}
		console.log("item_level_order :" + item_level_order);

		// New requirement for filter fetch selected filter value page records.
		var pageToShow = selectedIndex / pageLength;

		var pageNo = parseInt(pageToShow);
		if (pageToShow % 1 !== 0) {
			console.log("pageToShow :" + pageToShow);
			pageNo = parseInt(pageToShow) + 1;
		}
		if (pageNo == 0) {
			pageNo = 1;
		}
		pagination.show(pageNo);
		console.log(" pageNo  : " + pageNo);
		chooseSection = "";

		var isChooseSection = true;
		// var pageNo = parseInt(selectPageNo);
		// console.log(" pageLength : "+pageLength + " searchVal :"+searchVal +" pageNo :"+pageNo +" choosenVal :"+choosenVal);
		searchFilterBqItem(searchVal, pageNo, pageLength, chooseSection, isChooseSection);
	});
});

function searchFilterBqItem(searchVal, pageNo, pageLength, choosenSection, isChooseSection) {
	var eventBqId = $('#supplierBqList #eventBqId').val();
	var rfteventId = $('#supplierBqList #rfteventId').val();
	var eventType = getEventType();

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$.ajax({
		type : "POST",
		url : getContextPath() + '/supplier/getBqItemForSearchFilterForSupplier',
		data : {
			'rfteventId' : rfteventId,
			'eventBqId' : eventBqId,
			'eventType' : eventType,
			'searchVal' : searchVal,
			'pageNo' : pageNo,
			'pageLength' : pageLength,
			'choosenSection' : choosenSection,
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success : function(data) {
			var html = searchRenderBqItemGrid(data);
			// var html =supplierSearchBqList(data, rfteventId, eventBqId, null);
			$(".doc-fir-inner1").hide();
			$('#bqItemList').html(html);
			if ((allowedFields != '' && revisedSubmissionMode == false) || allowBqChanges ) {
				if(allowBqChanges){
					allowedFields='.tab-link > a, back_to_BQ,#bqItemSearch';
					disableFormFields(allowedFields);
					$('#supplierBqList').find('.back_to_BQ').removeClass("disabled");
					$('#supplierBqList').find('[name="taxType"]').parent('td').addClass('disabled');
				}else{
					disableFormFields(allowedFields);
				}
			}
			
			$('.bqlistDetails').find('.custom-select').uniform();
			$('.bqlistDetails').find(".selector").append('<i class="glyph-icon icon-caret-down"></i>');
			$('.bqlistDetails').find(".selector").find('span').css('border', 'none');
			
			if (item_level_order.length) {
				$('.' + item_level_order).each(function() {
					$.each(this.cells, function() {
						$(this).animate({
							backgroundColor : "#ffff00"
						}, 2000);
					});
				});

				setTimeout(function() {
					$('.' + item_level_order).each(function() {
						$.each(this.cells, function() {
							if (item_order == '0') {
								$(this).animate({
									backgroundColor : "#ffffff"
								}, 2000);
							} else {
								$(this).animate({
									backgroundColor : "#eef7fc"
								}, 2000);
							}
						});
					});
				}, 4000);
			}

			// set pagination according to result
			visiblePage = 5;
			totalPage = Math.ceil(data.totalBqItemCount / pageLength);

			if (totalPage == 0) {
				totalPage = 1;
			}

			if (totalPage < 5) {
				visiblePage = totalPage;
			}
			var opts = {
				totalPages : 500,
				visiblePages : 5
			};

			$('.pagination').twbsPagination('destroy');
			$('.pagination').twbsPagination($.extend(opts, {
				totalPages : totalPage,
				visiblePages : visiblePage
			}));
			var pagination = jQuery('.pagination').data('twbsPagination');
			pagination.show(pageNo);

			// Building Level filter drop down
			if (!isChooseSection) {
				var filterTable = '<option value="">&nbsp;</option>';
				$.each(data.levelOrderList, function(i, item) {
					filterTable += '<option value="">' + item.level + '.' + item.order + '</option>'; // '<li class="active-result">' + item.level
					// +'.' + item.order + '</li>';
				});
				$('#chooseSection').html(filterTable).trigger("chosen:updated");
			}

		},
		error : function(request, textStatus, errorThrown) {
			console.log("error");
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
}

function supplierSearchBqList(item, rfteventId, eventBqId, bqStatus) {
	var decimalLimit = $('#eventDecimal').val();
	var grandTotalVal = 0;
	var revisedGrandTotal = 0;
	var totalAfterTax = 0;
	var additionalTax = 0;
	var additionalTaxDesc = "";
	var countNewData = 1;
	var html = '';
	var buyerSubmit = false;
	var remarks = "";

	$.each(item.supplierBqItemList, function(i, data) {
		console.log(" item.supplierBqItemList :");
		if (data.supplierBq.buyerSubmited != null && data.supplierBq.buyerSubmited == true) {
			buyerSubmit = true;
		}
		if (i > 0) {
			// return false;
		} else {
			var eventCurr = $('#eventCurrency').val();
			html += '<h3>' + data.supplierBq.name + '</h3>';

			html += '<div class="column_button_bar">';
			html += '<div class="pull-left">';

			html += '<div aria-label="Page navigation">';
			html += '<ul class="pagination" id="pagination"></ul>';
			html += '</div>';

			html += '<div class="col-md-12 col-sm-12 col-xs-12">';
			html += '<label class="marg-right-10">';
			html += ' Jump to item';
			html += '</label>';
			html += '<select path="" class="choosen-select" name="" data-parsley-id="0644" id="chooseSection" style ="background: white; width: 49px;height: 35px;">';
			html += '<option value=""></option>';
			$.each(item.levelOrderList, function(i, data) {
				html += '<option  value="' + data.level + '.' + data.order + '">' + data.level + '.' + data.order + '</option>';
				if (data.children != undefined && data.children != null) {
					$.each(data.children, function(i, child) {
						html += '<option value="">' + child.level + '.' + child.order + '</option>';
					});
				}
			});
			html += '</select>';
			html += '</div>';
			html += '</div>';

			html += '<div class="pull-right">';
			html += '<div class="col-md-12 col-sm-12 col-xs-12">';
			html += '<label>';
			html += '<select class="disablesearch choosen-select" name="" data-parsley-id="0644" id="selectPageLen" style="background: white; width: 59px;height: 36px;">';
			html += '<option value="10">10</option>';
			html += '<option value="30">30</option>';
			html += '<option value="50">50</option>';
			html += '<option value="100">100</option>';
			html += '<option value="9999">ALL</option>';
			html += '</select>';
			html += ' records per page';
			html += '</label>';
			html += '</div>';
			html += '</div>';

			html += '<div class="right_button">';
			html += '<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" id="resetButton">Reset</button>';
			html += '</div>';
			html += '<div class="pull-right" style="padding-right: 12px;">';
			html += '<div class="row" style="width: 306px;">';
			html += '<input name="bqItemSearch" type="text" id="bqItemSearch" placeholder="Search bq by item name & description.." class="form-control" />';
			html += '</div>';
			html += '</div>';
			html += '</div>';
			html += '</div>';

			html += '<form id="supplierBqList">';
			html += '<div class="main_table_wrapper ph_table_border payment height-full1 mega">';
			html += '<table cellpadding="0" cellspacing="0" border="0" width="100%" class="ph_table font-set table" style="top: 0px; ">';
			html += '<thead><tr><th class="width_50 width_50_fix">No</th><th class="width_300_fix width_300"> Item Name </th>';
			html += '<th class="text-center width_100_fix width_100"> UOM </th><th class="text-center width_200 width_200_fix align-right">Quantity</th>';
			html += '<th class="width_200 width_200_fix align-right">Unit Price</th>';
			html += '<th class="width_200 width_200_fix align-right">Total Amount</th>';

			console.log(data.supplierBq.field4ToShowSupplier);
			console.log(data.supplierBq.field5ToShowSupplier);

			if (!isWithoutTax) {
				html += '<th class="width_200 width_200_fix align-left" style="padding-left:52px;"> Tax </th>';
			}
			if (data.supplierBq.field1ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left align-center"> ' + (data.supplierBq.field1Label != undefined ? data.supplierBq.field1Label : "") + ' </th>';
			}
			if (data.supplierBq.field2ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left  align-center"> ' + (data.supplierBq.field2Label != undefined ? data.supplierBq.field2Label : "") + ' </th>';
			}
			if (data.supplierBq.field3ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left  align-center"> ' + (data.supplierBq.field3Label != undefined ? data.supplierBq.field3Label : "") + ' </th>';
			}
			if (data.supplierBq.field4ToShowSupplier) {
				html += '<th class="width_200 width_200_fix  text-left align-center"> ' + (data.supplierBq.field4Label != undefined ? data.supplierBq.field4Label : "") + ' </th>';
			}

			if (data.supplierBq.field5ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-left  align-center"> ' + (data.supplierBq.field5Label != undefined ? data.supplierBq.field5Label : "") + ' </th>';
			}
			if (data.supplierBq.field6ToShowSupplier) {
				html += '<th class="width_200 width_200_fix  text-left align-center"> ' + (data.supplierBq.field6Label != undefined ? data.supplierBq.field6Label : "") + ' </th>';
			}
			if (data.supplierBq.field7ToShowSupplier) {
				html += '<th class="width_200 width_200_fix  text-left align-center"> ' + (data.supplierBq.field7Label != undefined ? data.supplierBq.field7Label : "") + ' </th>';
			}
			if (data.supplierBq.field8ToShowSupplier) {
				html += '<th class="width_200 width_200_fix  text-left align-center"> ' + (data.supplierBq.field8Label != undefined ? data.supplierBq.field8Label : "") + ' </th>';
			}
			if (data.supplierBq.field9ToShowSupplier) {
				html += '<th class="width_200 width_200_fix  text-left align-center"> ' + (data.supplierBq.field9Label != undefined ? data.supplierBq.field9Label : "") + ' </th>';
			}
			if (data.supplierBq.field10ToShowSupplier) {
				html += '<th class="width_200 width_200_fix  text-left align-center"> ' + (data.supplierBq.field10Label != undefined ? data.supplierBq.field10Label : "") + ' </th>';
			}
			if (!isWithoutTax) {
				html += '<th class="width_200 width_200_fix align-right">Total Amount with Tax</th>';
			}
			/* html += '<th class="width_100 width_100_fix text-center"> </th>'; */
			html += '</tr></thead></table>';
		}
	});
	html += '<table class="deta ph_table table bq-table"><tbody id="bqItemList">';
	html += '<input type="hidden" name="rfteventId" id="rfteventId" value="' + rfteventId + '"/>';
	html += '<input type="hidden" name="eventBqId" id="eventBqId" value="' + eventBqId + '"/>';

	$.each(item.supplierBqItemList, function(i, data) {
		var totalAmount = '';
		if (data.order == 0) {
			if (data.totalAmount != undefined) {
				totalAmount = data.totalAmount;
			}
			html += '<tr style="border-bottom: 0 !important; border-top: 2px solid #ddd !important;" data-item="' + data.id + '" class=" item_' + data.level + '_' + data.order + '"><td class="width_50 width_50_fix"><span>' + data.level + '.'
					+ data.order + '</span>&nbsp; </td>';
			html += '<td class="width_300 width_300_fix"><span>' + data.itemName + '</span>';
			html += data.itemDescription ? '<a data-toggle="collapse" class="s3_view_desc" data-target="#demo-' + data.id + '">View Description</a>' : "";
			// html += '<div id="demo-' + data.id + '" class="collapse table-contant">' + data.itemDescription + '</div>' + '</td>';
			html += '<td class="text-center width_100 width_100_fix"></td>';
			html += '<td class="width_200 width_200_fix text-center"></td>';
			html += '<td class="width_200 width_200_fix text-center"></td>';
			html += '<td class="width_200 width_200_fix align-center">';
			html += '<span>&nbsp;</span></td>';

			if (!isWithoutTax) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}

			if (data.supplierBq.field1ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field2ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field3ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field4ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}

			if (data.supplierBq.field5ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field6ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field7ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field8ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field9ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field10ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (!isWithoutTax) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}

			html += '<tr id="demo-' + data.id + '" class="collapse" style=" background: #fff;">';
			html += '<td  style=" background: #fff;">';
			html += '</td>';
			html += '<td colspan="7" style=" background: #fff;">';
			html += '<p class=" " style="margin: 5px 0 0 0; font-size: 11px; max-height: 150px; text-align: left;margin-top: -1%;">' + data.itemDescription + '</p>';
			html += '</td>';
			html += '</tr>';

		} else {
			// console.log("sassasasasasasa");
			// if (data.children != undefined && data.children != null) {
			// $.each(data.children, function(i, child) {
			html += renderSearchChildBqHtml(data);
		}
		if ((data.supplierBq && data.supplierBq.grandTotal) != undefined && (data.supplierBq && data.supplierBq.grandTotal) != null && grandTotalVal == 0) {
			revisedGrandTotal = parseFloat(data.supplierBq.revisedGrandTotal);
			grandTotalVal = parseFloat(data.supplierBq.grandTotal);
			totalAfterTax = parseFloat(data.supplierBq.totalAfterTax);
			additionalTax = parseFloat(data.supplierBq.additionalTax);
			additionalTaxDesc = data.supplierBq.taxDescription;
			remarks = data.supplierBq.remark;

		}
		// });
		// }
	});
	var eventCurr = $('#eventCurrency').val();
	html += '</tbody></table></div>';
	// Dont show additional tax if auction type is without tax
	if (!isWithoutTax && !isErpEnable) {
		html += '<div class="total-with-tax pad_all_15 marg-top-10 marg-bottom-10">';
		html += '<div class="total-with-tax-inner"><label>Grand Total (' + eventCurr + '): </label><p id="grandTotalOfBq">';
		html += ReplaceNumberWithCommas(grandTotalVal.toFixed(decimalLimit));
		html += '</p></div></div>';
		html += '<div class="additinol-text pad_all_15 marg-top-20 marg-bottom-20">';
		html += '<div class="add_tex">';
		html += '<label>&nbsp;</label>';
		html += '<input type="text" name="additionalTax" id="additionalTax" class="validateregex form-control" data-validation-regexp="^(?!\\.?$)\\d{0,10}(\\.\\d{0,6})?$"  value="';
		html += '' + ((additionalTax != null && additionalTax != undefined && !isNaN(additionalTax)) ? ReplaceNumberWithCommas(additionalTax.toFixed(decimalLimit)) : '0.00') + '" placeholder="Additional Tax Amount">';
		html += '</div>';
		html += '<div class="Description_lower">';
		html += '<label>ADDITIONAL TAX DESC</label>';
		html += '<textarea class="form-control" name="additionalTaxDescription"  >' + (additionalTaxDesc != undefined ? additionalTaxDesc : '') + '</textarea>';
		html += '</div>';
		html += '</div>';
	}
	html += '</div>';
	html += '<div class="total-with-tax-final pad_all_15 marg-top-10 marg-bottom-10">';

	html += '<div><label>Remark</label></div>'
	html += '<textarea rows="3"  cols="42" name="remarks" id="remarks" placeholder ="Write Your Remarks" maxlength="3000" minlength="0" >' + (remarks != undefined ? remarks : '') + '</textarea>';

	html += '<div class="total-with-tax-inner">';
	html += '<label>' + (isWithoutTax ? 'Grand Total' : 'Total After Tax') + ' (' + eventCurr + '): </label><p class="color-' + (revisedSubmissionMode == true && revisedGrandTotal != null && revisedGrandTotal != totalAfterTax ? 'red' : 'green')
			+ '" id="amountAfterTax">';
	html += (((totalAfterTax != undefined && !isNaN(totalAfterTax)) ? ReplaceNumberWithCommas(totalAfterTax.toFixed(decimalLimit)) : (grandTotalVal != undefined ? ReplaceNumberWithCommas(grandTotalVal.toFixed(decimalLimit)) : '0.00')));
	if (revisedSubmissionMode == true && revisedGrandTotal != totalAfterTax) {
		html += ' (' + ReplaceNumberWithCommas(revisedGrandTotal.toFixed(decimalLimit)) + ')';
		$('p[id=idGlobalErrorMessage]').html('Your Bill Of Quantities total does not match with your final Auction bid price. Please revise and submit');
		$('div[id=idGlobalError]').show();
		$('div[id=idGlobalSuccess]').hide();

	}
	html += '</p></div></div>';
	html += '<div class="row">';
	html += '<div class="col-md-12 col-xs-12 col-sm-12">';
	html += '<button class="btn btn-black ph_btn_midium back_to_BQ hvr-pop hvr-rectangle-out1">Back to Bill of Quantity</button>';
	if (buyerSubmit == false || revisedSubmissionMode == true) {
		html += '<button id="pppp" class="btn btn-info ph_btn_midium marg-left-10 hvr-pop hvr-rectangle-out pull-right complete-event ' + (revisedSubmissionMode == true && revisedGrandTotal != null && revisedGrandTotal != totalAfterTax ? 'disabled' : '')
				+ '">Complete</button>';
	} else if (allowedFields == '') {
		html += '<button id="pppp" class="btn btn-info ph_btn_midium marg-left-10 hvr-pop hvr-rectangle-out pull-right complete-event">Confirm</button>';
	}
	if(bqStatus != 'COMPLETED'){
		html += '<button class="btn btn-black ph_btn_midium hvr-pop hvr-rectangle-out1 pull-right skipvalidation saveDraftSupplierBqButton">Save Draft</button>';
	}
	html += '</div></div></form>';
	return html;
}

function renderSearchChildBqHtml(data) {

	// console.log("decimalLimit :"+$('#eventDecimal').val());
	var decimalLimit = $('#eventDecimal').val();
	console.log("*********** event "+disableTotalAmount);
	var html = '';
	html += '<tr style="border-bottom: 0 !important; border-top: 2px solid #ddd !important; " background: #eef7fc;" class=" sub_item item_' + data.level + '_' + data.order + '" data-item="' + data.id + '">'
	html += data.totalAmountWithTax != undefined ? '<input type="hidden" name="totalAmountWithTax" id="" value="' + data.totalAmountWithTax + '"/>' : "";
	html += '<input type="hidden" name="itemName" id="" value="' + data.itemName + '" />';
	html += '<input type="hidden" name="quantity" id="" value="' + data.quantity + '"/>';
	html += '<td class="width_50 width_50_fix"> ' + data.level + '.' + data.order + ' &nbsp;</td>';
	html += '<td class="width_300 width_300_fix">';
	if (data.priceType == 'BUYER_FIXED_PRICE') {
		html += '<span class="bs-label label-success" style="color: #fff">Buyer Fixed Price</span>&nbsp;';
	} else if (data.priceType == 'TRADE_IN_PRICE') {
		html += '<span class="bs-label label-info" style="color: #fff">Trade In Price</span>&nbsp;';
	}
	html += '' + data.itemName;
	html += (data.itemDescription ? '<a data-toggle="collapse" class="s3_view_desc" data-target="#demo-' + data.id + '">View Description</a>' : "");
	html += '</td>';
	html += '<td class="text-center width_100 width_100_fix">  ';
	if (data.uom) {
		html += data.uom.uom;
	}
	html += '</td>';
	html += '<td class="width_200 width_200_fix align-right">   ' + ReplaceNumberWithCommas(data.quantity.toFixed(decimalLimit)) + '</td>';
	html += '<td class="width_200 width_200_fix text-center"><input type="text" data-pos="1"  name="unitPrice" class="validate form-control text-right" value="';
	html += (data.unitPrice != undefined ? ReplaceNumberWithCommas(data.unitPrice.toFixed(decimalLimit)) : "") + '" ';
	html += data.priceType != undefined ? (data.priceType == 'BUYER_FIXED_PRICE' ? ' readonly="readonly" ' : '') : '';
	html += ' data-validation="required" data-validation-length="max10" ';
	html += '/></td>';
	html += '<td class="width_200 width_200_fix align-center"> <input data-pos="2"  class="validate form-control text-right" type="text" name="totalAmount" value="';
	html += (data.totalAmount != undefined ? ReplaceNumberWithCommas(data.totalAmount.toFixed(decimalLimit)) : "") + '" ';
	html += data.priceType != undefined ? (data.priceType == 'BUYER_FIXED_PRICE' || disableTotalAmount ? ' readonly="readonly" ' : '') : '';
	html += ' /></td>';
	if (!isWithoutTax) {
		html += '<td class="width_200 width_200_fix align-right">';
		html += '<input data-pos="3" type="text" name="tax" class="validate form-control width_38per text-right"" value="';
		html += data.tax != undefined ? ReplaceNumberWithCommas(data.tax.toFixed(decimalLimit)) : "";
		html += '"><select name="taxType" id="custom_' + data.id + '" class="custom-select" ' + (allowedFields != '' ? 'disabled="disabled"' : '') + ' data-pos="4">';
		if (data.taxTypeList != undefined && data.taxTypeList != null) {
			$.each(data.taxTypeList, function(i, taxType) {
				html += '<option value="' + taxType + '"';
				html += data.taxType == taxType ? " selected='selected' " : "";
				html += '>' + taxType + '</option>';
			});
		}
		html += '</select>';
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field1ToShowSupplier) {
		var isBuyer = (data.supplierBq.field1FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="5" name="field1"  data-validation="' + ((data.supplierBq.field1Required != undefined && data.supplierBq.field1Required  )? "required" : "") + '"  class="extra-column  form-control " style="text-align:left;"  value="' + (data.field1 != undefined ? data.field1 : "") + '" >';
		} else {
			html += data.field1 != undefined ? data.field1 : "";
			html += '<input type="hidden" name="field1"  value="' + (data.field1 != undefined ? data.field1 : "") + '">';
		}
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field2ToShowSupplier) {
		var isBuyer = (data.supplierBq.field2FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="6" name="field2"  data-validation="' + ((data.supplierBq.field2Required != undefined && data.supplierBq.field2Required  )? "required" : "") + '" class="extra-column  form-control " style="text-align:left;" value="' + (data.field2 != undefined ? data.field2 : "") + '">';
		} else {
			html += data.field2 != undefined ? data.field2 : "";
			html += '<input type="hidden" name="field2"  value="' + (data.field2 != undefined ? data.field2 : "") + '">';
		}
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field3ToShowSupplier) {
		var isBuyer = (data.supplierBq.field3FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="7" name="field3" data-validation="' + ((data.supplierBq.field3Required != undefined && data.supplierBq.field3Required  ) ? "required" : "") + '" class="extra-column form-control  " style="text-align:left;" value="' + (data.field3 != undefined ? data.field3 : "") + '">';
		} else {
			html += data.field3 != undefined ? data.field3 : "";
			html += '<input type="hidden" name="field3"  value="' + (data.field3 != undefined ? data.field3 : "") + '">';
		}
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field4ToShowSupplier) {
		var isBuyer = (data.supplierBq.field4FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="8" name="field4" data-validation="' + ((data.supplierBq.field4Required != undefined && data.supplierBq.field4Required  ) ? "required" : "") + '" class="extra-column form-control " style="text-align:left;" value="' + (data.field4 != undefined ? data.field4 : "") + '">';
		} else {
			html += data.field4 != undefined ? data.field4 : "";
			html += '<input type="hidden" name="field4"  value="' + (data.field4 != undefined ? data.field4 : "") + '">';
		}
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field5ToShowSupplier) {
		var isBuyer = (data.supplierBq.field5FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="9" name="field5" data-validation="' + ((data.supplierBq.field5Required != undefined && data.supplierBq.field5Required  ) ? "required" : "") + '" class="extra-column  form-control " style="text-align:left;"  value="' + (data.field5 != undefined ? data.field5 : "") + '" >';
		} else {
			html += data.field5 != undefined ? data.field5 : "";
			html += '<input type="hidden" name="field5"  value="' + (data.field5 != undefined ? data.field5 : "") + '">';
		}
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field6ToShowSupplier) {
		var isBuyer = (data.supplierBq.field6FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="10" name="field6" data-validation="' + ((data.supplierBq.field6Required != undefined && data.supplierBq.field6Required  ) ? "required" : "") + '" class="extra-column  form-control " style="text-align:left;"  value="' + (data.field6 != undefined ? data.field6 : "") + '" >';
		} else {
			html += data.field6 != undefined ? data.field6 : "";
			html += '<input type="hidden" name="field6"  value="' + (data.field6 != undefined ? data.field6 : "") + '">';
		}
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field7ToShowSupplier) {
		var isBuyer = (data.supplierBq.field7FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="11" name="field7" data-validation="' + ((data.supplierBq.field7Required != undefined && data.supplierBq.field7Required  ) ? "required" : "") + '" class="extra-column  form-control " style="text-align:left;"  value="' + (data.field7 != undefined ? data.field7 : "") + '" >';
		} else {
			html += data.field7 != undefined ? data.field7 : "";
			html += '<input type="hidden" name="field7"  value="' + (data.field7 != undefined ? data.field7 : "") + '">';
		}
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field8ToShowSupplier) {
		var isBuyer = (data.supplierBq.field8FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="12" name="field8" data-validation="' + ((data.supplierBq.field8Required != undefined && data.supplierBq.field8Required  ) ? "required" : "") + '" class="extra-column  form-control " style="text-align:left;"  value="' + (data.field8 != undefined ? data.field8 : "") + ' ">';
		} else {
			html += data.field8 != undefined ? data.field8 : "";
			html += '<input type="hidden" name="field8"  value="' + (data.field8 != undefined ? data.field8 : "") + '">';
		}
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field9ToShowSupplier) {
		var isBuyer = (data.supplierBq.field9FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="13" name="field9" data-validation="' + ((data.supplierBq.field9Required != undefined && data.supplierBq.field9Required  ) ? "required" : "") + '" class="extra-column  form-control " style="text-align:left;"  value="' + (data.field9 != undefined ? data.field9 : "") + '" >';
		} else {
			html += data.field9 != undefined ? data.field9 : "";
			html += '<input type="hidden" name="field9"  value="' + (data.field9 != undefined ? data.field9 : "") + '">';
		}
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field10ToShowSupplier) {
		var isBuyer = (data.supplierBq.field10FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="14" name="field10" data-validation="' + ((data.supplierBq.field10Required != undefined && data.supplierBq.field10Required  ) ? "required" : "") + '" class="extra-column  form-control " style="text-align:left;" value="' + (data.field10 != undefined ? data.field10 : "") + '" >';
		} else {
			html += data.field10 != undefined ? data.field10 : "";
			html += '<input type="hidden" name="field10"  value="' + (data.field10 != undefined ? data.field10 : "") + '">';
		}
		html += '</td>';
	}

	if (!isWithoutTax) {
		html += '<td class="width_200 width_200_fix align-right">';
		html += data.totalAmountWithTax != undefined ? ReplaceNumberWithCommas(data.totalAmountWithTax.toFixed(decimalLimit)) : "";
		html += '</td>';
	}

	html += '</tr>';

	html += '<tr id="demo-' + data.id + '" class="collapse"  style=" background: #eef7fc;">';
	html += '<td  style=" background: #eef7fc;">';
	html += '</td>';
	html += '<td colspan="7" style=" background: #eef7fc; border-bottom: 0 !important;">';
	html += '<p class=" " style=" margin: 5px 0 0 0; font-size: 11px; max-height: 150px; text-align: left;margin-top: -1%;">' + data.itemDescription + '</p>';
	html += '</td>';

	if (data.supplierBq && data.supplierBq.field1ToShowSupplier) {
		var isBuyer = (data.supplierBq.field1FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field2ToShowSupplier) {
		var isBuyer = (data.supplierBq.field2FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field3ToShowSupplier) {
		var isBuyer = (data.supplierBq.field3FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field4ToShowSupplier) {
		var isBuyer = (data.supplierBq.field4FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field5ToShowSupplier) {
		var isBuyer = (data.supplierBq.field5FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field6ToShowSupplier) {
		var isBuyer = (data.supplierBq.field6FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field7ToShowSupplier) {
		var isBuyer = (data.supplierBq.field7FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field8ToShowSupplier) {
		var isBuyer = (data.supplierBq.field8FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field9ToShowSupplier) {
		var isBuyer = (data.supplierBq.field9FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}
	if (data.supplierBq && data.supplierBq.field10ToShowSupplier) {
		var isBuyer = (data.supplierBq.field10FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-left">';
		html += '</td>';
	}

	html += '</tr>';

	return html;
}

function searchRenderBqItemGrid(item) {
	var decimalLimit = $('#eventDecimal').val();
	var grandTotalVal = 0;
	var revisedGrandTotal = 0;
	var totalAfterTax = 0;
	var additionalTax = 0;
	var additionalTaxDesc = "";
	var countNewData = 1;
	var buyerSubmit = false;

	var rfteventId = $('#supplierBqList #rfteventId').val();
	var eventBqId = $('#supplierBqList #eventBqId').val();

	var html = '';
	html += '<input type="hidden" name="rfteventId" id="rfteventId" value="' + rfteventId + '"/>';
	html += '<input type="hidden" name="eventBqId" id="eventBqId" value="' + eventBqId + '"/>';
	$.each(item.supplierBqItemList, function(i, data) {
		if (data.order == 0) {

			var totalAmount = '';

			if (data.totalAmount != undefined) {
				totalAmount = data.totalAmount;
			}

			html += '<tr style="border-bottom: 0 !important; border-top: 2px solid #ddd !important;" data-item="' + data.id + '" class=" item_' + data.level + '_' + data.order + '" ><td class="width_50 width_50_fix"><span>' + data.level + '.'
					+ data.order + '</span>&nbsp; </td>';
			html += '<td class="width_300 width_300_fix"><span>' + data.itemName + '</span>';
			html += data.itemDescription ? '<a data-toggle="collapse" class="s3_view_desc" data-target="#demo-' + data.id + '">View Description</a>' : "";
			// html += '<div id="demo-' + data.id + '" class="collapse table-contant">' + data.itemDescription + '</div>' + '</td>';
			html += '<td class="text-center width_100 width_100_fix"></td>';
			html += '<td class="width_200 width_200_fix text-center"></td>';
			html += '<td class="width_200 width_200_fix text-center"></td>';
			html += '<td class="width_200 width_200_fix align-center">';
			html += '<span>&nbsp;</span></td>';

			if (!isWithoutTax) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}

			if (data.supplierBq.field1ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field2ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field3ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field4ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}

			if (data.supplierBq.field5ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field6ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field7ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field8ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field9ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (data.supplierBq.field10ToShowSupplier) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			if (!isWithoutTax) {
				html += '<td class="width_200 width_200_fix align-center"></td>';
			}
			html += '</tr>';

			html += '<tr id="demo-' + data.id + '" class="collapse"  style=" background: #fff;">';
			html += '<td  style=" background: #fff;">';
			html += '</td>';
			html += '<td colspan="7" style=" background: #fff;;">';
			html += '<p class=" " style="margin: 5px 0 0 0; font-size: 11px; max-height: 150px; text-align: left;margin-top: -1%;">' + data.itemDescription + '</p>';
			html += '</td>';
			html += '</tr>';

		} else {
			html += renderSearchChildBqHtml(data);
		}
		if ((data.supplierBq && data.supplierBq.grandTotal) != undefined && (data.supplierBq && data.supplierBq.grandTotal) != null && grandTotalVal == 0) {
			revisedGrandTotal = parseFloat(data.supplierBq.revisedGrandTotal);
			grandTotalVal = parseFloat(data.supplierBq.grandTotal);
			totalAfterTax = parseFloat(data.supplierBq.totalAfterTax);
			additionalTax = parseFloat(data.supplierBq.additionalTax);
			additionalTaxDesc = data.supplierBq.taxDescription;
		}
	});
	return html;
}


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

$(document).delegate('.completeSupplierBq', 'click', function(e) {
		e.preventDefault();
//		$('#confirmCompleteEvent').find('#finishIdEvent').val($(this).attr('eventId'));
		$('#confirmCompleteEvent').modal().show();
	});
	

$(document).delegate(".saveDraftSupplierBqButton", "click", function(e) {
		e.preventDefault();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();

//		var totalAmountForValidation = $('#amountAfterTax').text();
		var rfteventId = $('#supplierBqList #rfteventId').val();
		var remarks = $("#supplierBqList #remarks").val();

		var ajaxUrl = getContextPath() + '/supplier/saveDraftSupplierBq/' + getEventType() + '/' + rfteventId;
		var eventBqId = $('#supplierBqList #eventBqId').val();
		var supplierBqList = [];
		$('#supplierBqList table.deta.ph_table.table.bq-table tr').each(function() {
			var dataRow = {};
			$(this).find('input, select').each(function() {
				dataRow[$(this).attr('name')] = $(this).val().replace(/,/g, '');
			});
			dataRow['rfteventId'] = $('#supplierBqList').find('[name="rfteventId"]').val();
			// dataRow['eventBqId'] =
			// $('#supplierBqList').find('[name="eventBqId"]').val();

			dataRow['id'] = $(this).attr('data-item');
			if ($('#supplierBqList').find('[name="additionalTax"]').length) {
				dataRow['additionalTax'] = $('#supplierBqList').find('[name="additionalTax"]').val().replace(/,/g, '');
			} else {
				dataRow['additionalTax'] = 0;
			}
			if ($('#supplierBqList').find('[name="additionalTaxDescription"]').length) {
				dataRow['taxDescription'] = $('#supplierBqList').find('[name="additionalTaxDescription"]').val();
			} else {
				dataRow['taxDescription'] = '';
			}

			dataRow['remarks'] = $('#supplierBqList').find('[name="remarks"]').val();

			dataRow['bqId'] = $('#supplierBqList').find('[name="eventBqId"]').val();
			// dataRow['totalAmountForValidation'] = $('#amountAfterTax').val();
			supplierBqList.push(dataRow);
		});
		// console.log(JSON.stringify(supplierBqList));
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			data : JSON.stringify(supplierBqList),
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader("Content-Type", "application/json");
				xhr.setRequestHeader(header, token);
			},
			success : function(data, textStatus, request) {
				if (request.getResponseHeader('success')) {
					var success = "Bill of Quantity saved successfully";
					$.jGrowl(success, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					});
				}
				// console.log("data" + data);
				//window.location.href = getContextPath() + "/supplier/viewBqList/" + getEventType() + '/' + rfteventId + '?success=Bill%20of%20Quantity%20saved%20successfully';

			},
			error : function(request, textStatus, errorThrown) {
				console.log("ERROR : " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					// document.getElementById('#idGlobalSuccessMessage').innerHTML = "";
					// $('#newDiv').clear();
					// document.getElementById("newDiv").innerHTML = "";
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
					var error = request.getResponseHeader('error');
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});

	});	
	

$(document).delegate(".complete-event", "click", function(e) {
		e.preventDefault();
		if (!$('#supplierBqList').isValid()) {
			return;
		}
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		e.preventDefault();

		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();

		var totalAmountForValidation = $('#amountAfterTax').text();
		var rfteventId = $('#supplierBqList #rfteventId').val();
		var remarks = $("#supplierBqList #remarks").val();

		var ajaxUrl = getContextPath() + '/supplier/saveSupplierBQDetails/' + getEventType() + '/' + rfteventId + '/' + totalAmountForValidation + '/';
		var eventBqId = $('#supplierBqList #eventBqId').val();
		var supplierBqList = [];
		$('#supplierBqList table.deta.ph_table.table.bq-table tr').each(function() {
			var dataRow = {};
			$(this).find('input, select').each(function() {
				dataRow[$(this).attr('name')] = $(this).val().replace(/,/g, '');
			});
			dataRow['rfteventId'] = $('#supplierBqList').find('[name="rfteventId"]').val();
			// dataRow['eventBqId'] =
			// $('#supplierBqList').find('[name="eventBqId"]').val();

			dataRow['id'] = $(this).attr('data-item');
			if ($('#supplierBqList').find('[name="additionalTax"]').length) {
				dataRow['additionalTax'] = $('#supplierBqList').find('[name="additionalTax"]').val().replace(/,/g, '');
			} else {
				dataRow['additionalTax'] = 0;
			}
			if ($('#supplierBqList').find('[name="additionalTaxDescription"]').length) {
				dataRow['taxDescription'] = $('#supplierBqList').find('[name="additionalTaxDescription"]').val();
			} else {
				dataRow['taxDescription'] = '';
			}

			dataRow['remarks'] = $('#supplierBqList').find('[name="remarks"]').val();

			dataRow['bqId'] = $('#supplierBqList').find('[name="eventBqId"]').val();
			// dataRow['totalAmountForValidation'] = $('#amountAfterTax').val();
			supplierBqList.push(dataRow);
		});
		// console.log(JSON.stringify(supplierBqList));
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			data : JSON.stringify(supplierBqList),
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader("Content-Type", "application/json");
				xhr.setRequestHeader(header, token);
			},
			success : function(data, textStatus, request) {
				if (request.getResponseHeader('success')) {
					var success = request.getResponseHeader('success');
					$.jGrowl(success, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					});
				}
				// console.log("data" + data);
				window.location.href = getContextPath() + "/supplier/viewBqList/" + getEventType() + '/' + rfteventId + '?success=Bill%20of%20Quantity%20completed%20successfully';

			},
			error : function(request, textStatus, errorThrown) {
				console.log("ERROR : " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					// document.getElementById('#idGlobalSuccessMessage').innerHTML = "";
					// $('#newDiv').clear();
					// document.getElementById("newDiv").innerHTML = "";
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
					var error = request.getResponseHeader('error');
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});

	});

