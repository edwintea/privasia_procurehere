$(document).ready(function() {
	var decimalLimit = $('#decimal').val();
	if (decimalLimit == '' || decimalLimit == undefined) {
		decimalLimit = 2;
	}
	/*
	 * $(".Invited-Supplier-List").click(function() { alert('1111111'); $(this).toggleClass("small-accordin-tab"); });
	 */
	$('#idTotalAmt').on('change', function(e) {
		var totalAmount = parseFloat($('#idTotalAmt').val().replace(/,/g, ''));
		if ($('#idTotalAmt').val() === '' || $('#idTotalAmt').val() === undefined) {
			$('#idTotalAmt').val('');
		} else {
			$('#idTotalAmt').val(ReplaceNumberWithCommas(totalAmount.toFixed(decimalLimit)));
			$('#totalAmountModel').val(ReplaceNumberWithCommas(totalAmount.toFixed(decimalLimit)));
		}
	});
	$(document).delegate(".saveSupplierBq", "click", function(e) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		e.preventDefault();
		var ajaxUrl = getContextPath() + '/supplier/saveSupplierBQDetails/' + getEventType();
		var rfteventId = $('#supplierBqList #rfteventId').val();
		var eventBqId = $('#supplierBqList #eventBqId').val();
		var supplierBqList = [];
		$('#supplierBqList table.deta.ph_table.table.bq-table tr').each(function() {
			var dataRow = {};
			$(this).find('input, select').each(function() {
				dataRow[$(this).attr('name')] = $(this).val();
			});
			dataRow['rfteventId'] = $('#supplierBqList').find('[name="rfteventId"]').val();
			// dataRow['eventBqId'] =
			// $('#supplierBqList').find('[name="eventBqId"]').val();

			// var supplierBq = {}
			// supplierBq['id'] =
			// $('#supplierBqList').find('[name="eventBqId"]').val();
			// supplierBq['additionalTax'] =
			// $('#supplierBqList').find('[name="additionalTax"]').val();
			// supplierBq['taxDescription'] =
			// $('#supplierBqList').find('[name="additionalTaxDescription"]').val();
			// dataRow["supplierBq"] = supplierBq;

			dataRow['id'] = $(this).attr('data-item');
			dataRow['additionalTax'] = $('#supplierBqList').find('[name="additionalTax"]').val().replace(/,/g, '');
			dataRow['taxDescription'] = $('#supplierBqList').find('[name="additionalTaxDescription"]').val();
			dataRow['bqId'] = $('#supplierBqList').find('[name="eventBqId"]').val();
			supplierBqList.push(dataRow);
		});
		console.log(JSON.stringify(supplierBqList));
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
				console.log("data" + data);
			},
			error : function(request, textStatus, errorThrown) {
				console.log("ERROR : " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
					var error = request.getResponseHeader('error');
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					});
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});

	});

	$(document).delegate('.reduce > input', 'change', function(e) {
		if (isNaN($(this).val())) {
			$(this).val('');
		}
	});

	$(document).delegate('.reduceAllPrices', 'click', function(e) {
		e.preventDefault();
		$(this).addClass('disabled');
		$(this).attr('disabled', true);
		$('#loading').show();
		var auctionType = $('#auctionType').val();
		// alert(auctionType);
		if (auctionType == 'FORWARD_ENGISH' || auctionType == 'FORWARD_SEALED_BID') {
			if ($.trim($('.reduce > input').val()) != '') {
				$('[name="totalAmount"][type="text"]').each(function() {
					var priceType = $(this).attr('data-type');
					if (priceType == 'BUYER_FIXED_PRICE') {
					} else {
						var unitP = parseFloat($(this).val().replace(/,/g, ''));
						// console.log(" $(this).val() : " + $(this).val());
						// console.log("UnitP " + unitP);
						var reduceBy = parseFloat($.trim($('.reduce > input').val().replace(/,/g, '')));
						// console.log("R by " + reduceBy + " " + decimalValue);
						var unitPrice = unitP + parseFloat(((unitP * reduceBy) / 100).toFixed(decimalValue));
						// console.log("unit final " + unitPrice);
						// This code is commented because we refactor the
						// calculation
						// var unitPrice = parseFloat($(this).val()) +
						// (parseFloat($(this).val()) *
						// parseFloat($.trim($('.reduce > input').val()))
						// /
						// 100);
						$(this).val(unitPrice).change();
					}
				});
				// $('.reduce > input').val('');
			}
		} else if (auctionType == 'REVERSE_ENGISH' || auctionType == 'REVERSE_SEALED_BID') {
			if ($.trim($('.reduce > input').val()) != '') {
				$('[name="unitPrice"]').each(function() {
					var priceType = $(this).attr('data-type');
					if (priceType == 'BUYER_FIXED_PRICE') {
					} else {
						var unitP = parseFloat($(this).val().replace(/,/g, ''));
						var reduceBy = parseFloat($.trim($('.reduce > input').val().replace(/,/g, '')));
						var unitPrice = unitP - ((unitP * reduceBy) / 100);
						$(this).val(unitPrice).change();
					}
				});
				// $('.reduce > input').val('');
			}
		}

		$(this).removeClass('disabled');
		$(this).attr('disabled', false );
		$('#loading').hide();
	});
	
	$(document).delegate('.reduceAllPricesLumsum', 'click', function(e) {
		e.preventDefault();
		$(this).attr('disabled',true );
		$('#loading').show();
		var auctionType = $('#auctionType').val();
		console.log(auctionType);
		if (auctionType == 'FORWARD_ENGISH' || auctionType == 'FORWARD_SEALED_BID') {
			if ($.trim($(this).closest('form').find('.reduce > input[type="text"]').val()) != '') {
				var oldPrice = $(this).closest('form').find('input[name="totalAfterTax"][type="hidden"]').val().replace(/,/g, "");
				var reducePrice = $(this).closest('form').find('.reduce > input[type="text"]').val().replace(/,/g, "");
				var newPrices = parseFloat(oldPrice) + (parseFloat(oldPrice) * parseFloat(reducePrice) / 100);
				$('#totalAmountModel').val(newPrices.toFixed(decimalLimit));
				$(this).closest('form').find('input[name="totalAfterTax"][type="hidden"]').val(newPrices.toFixed(decimalLimit));
				$(this).closest('form').find('label#finalTotal').text(ReplaceNumberWithCommas(newPrices.toFixed(decimalLimit)));
				$('.currentFinalTotal').text(ReplaceNumberWithCommas(newPrices.toFixed(decimalLimit)));
				// $('.reduce > input[type="text"]').val('');
			}
		} else if (auctionType == 'REVERSE_ENGISH' || auctionType == 'REVERSE_SEALED_BID') {
			if ($.trim($(this).closest('form').find('.reduce > input[type="text"]').val()) != '') {
				var oldPrice = $(this).closest('form').find('input[name="totalAfterTax"][type="hidden"]').val().replace(/,/g, "");
				var reducePrice = $(this).closest('form').find('.reduce > input[type="text"]').val().replace(/,/g, "");
				var newPrices = parseFloat(oldPrice) - (parseFloat(oldPrice) * parseFloat(reducePrice) / 100);
				$(this).closest('form').find('input[name="totalAfterTax"][type="hidden"]').val(newPrices.toFixed(decimalLimit));
				$('#totalAmountModel').val(newPrices.toFixed(decimalLimit));
				$(this).closest('form').find('label#finalTotal').text(ReplaceNumberWithCommas(newPrices.toFixed(decimalLimit)));
				$('.currentFinalTotal').text(ReplaceNumberWithCommas(newPrices.toFixed(decimalLimit)));
				// $('.reduce > input[type="text"]').val('');
			}
		}
		// showMessage('SUCCESS', 'Bid value changed. Please click on Submit
		// Price to submit bid');
		$('.undoButtonDiv').show();
		$(this).attr('disabled',false );
		$('#loading').hide();
	});
	$(document).delegate('#supplierBqList table.bq-table tr input, #supplierBqList table.bq-table tr select', 'change', function(e) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		e.preventDefault();
		
		
		if(e.target.type === 'text') {
			this.value = this.value.replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"));
			console.log(this.value);
		}
		
		
		var ajaxUrl = getContextPath() + '/supplier/updateAuctionSupplierBQItem';
		var rfteventId = $('#supplierBqList #rfteventId').val();
		var eventBqId = $('#supplierBqList #eventBqId').val();
		var grandTotalOfBq = $('#supplierBqList #idGrandTotalOfBq').val().replace(/,/g, "");
		if ($('#supplierBqList').find('[name="additionalTax"]').length) {
			var additionalTax = $('#supplierBqList').find('[name="additionalTax"]').val().replace(/,/g, '');
		} else {
			var additionalTax = 0;
		}
		if ($('#supplierBqList').find('[name="additionalTaxDescription"]').length) {
			var additionalTaxDescription = $('#supplierBqList').find('[name="additionalTaxDescription"]').val();
		} else {
			var additionalTaxDescription = '';
		}

		var amountAfterTax = $('#supplierBqList #idAmountAfterTax').val().replace(/,/g, "");
		var currentBlock = $(this);
		var dataRow = {};
		$(this).closest('tr').find('input, select').each(function() {
			dataRow[$(this).attr('name')] = $(this).val().replace(/,/g, "");
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
		dataRow['id'] = $(this).closest('tr').attr('data-item');
		dataRow['position'] = $(this).attr('data-pos');

		if ($(this).attr('name') == 'unitPrice') {
			dataRow['totalAmount'] = (parseFloat(dataRow['unitPrice'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))) * parseFloat(dataRow['quantity'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")))).toFixed(decimalLimit);
		} else if ($(this).attr('name') == 'totalAmount') {
			dataRow['unitPrice'] = (parseFloat(dataRow['totalAmount'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))) / parseFloat(dataRow['quantity'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")))).toFixed(decimalLimit);
			dataRow['totalAmount'] = (parseFloat(dataRow['unitPrice'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))) * parseFloat(dataRow['quantity'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")))).toFixed(decimalLimit);
		}
		if (dataRow['tax'] == '') {
			dataRow['tax'] = 0;
		}
		if (dataRow['taxType'] == 'Amount') {
			dataRow['totalAmountWithTax'] = (parseFloat(dataRow['totalAmount'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))) + parseFloat(dataRow['tax'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")))).toFixed(decimalLimit);
		} else if (dataRow['taxType'] == 'Percent') {
			dataRow['totalAmountWithTax'] = (parseFloat(dataRow['totalAmount'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))) + parseFloat((parseFloat(dataRow['totalAmount'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))) * parseFloat(dataRow['tax'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")))) / 100)).toFixed(decimalLimit);
		} else{
			dataRow['totalAmountWithTax'] = (parseFloat(dataRow['totalAmount'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")))).toFixed(decimalLimit);
		}
		$.each(dataRow, function(dataName, dataDetail) {
			var dataValueFlot = parseFloat(dataDetail);
			var float = /^\s*(\+|-)?((\d+(\.\d+)?)|(\.\d+))\s*$/;
			if (float.test(dataDetail)) {
				dataDetail = ReplaceNumberWithCommas(parseFloat(dataDetail.toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))).toFixed(decimalLimit));
			}
			currentBlock.closest('tr').find('[name="' + dataName + '"]').not('[name^="supplierBqItems"]').val(dataDetail);
			currentBlock.closest('tr').find('span.' + dataName).text(dataDetail);
			currentBlock.closest('tr').find('[name$=".' + dataName + '"]').val(dataDetail);
		});
		var grandTotalVal = 0;
		if ($('[name="taxType"]').length > 0) {
			$('[name="totalAmountWithTax"]').each(function() {
				console.log("=====1=========" , grandTotalVal, " + ", parseFloat($(this).val().replace(/,/g, "")) , " After rounding : ", parseFloat(grandTotalVal.toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))));
				grandTotalVal = parseFloat(grandTotalVal.toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))) + parseFloat($(this).val().replace(/,/g, ""));
				grandTotalVal = grandTotalVal.toFixed(decimalLimit);
			});
		} else {
			$('[name="totalAmount"][type="text"]').each(function() {
				console.log("======2========"+grandTotalOfBq);
				grandTotalVal = parseFloat(grandTotalVal.toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))) + parseFloat($(this).val().replace(/,/g, ""));
				grandTotalVal = grandTotalVal.toFixed(decimalLimit);
			});
		}
		
		var amountAfterTax = parseFloat(grandTotalVal.toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))) + parseFloat(dataRow['additionalTax'].toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));
		$('[name="grandTotalOfBq"], [name="grandTotal"]').val(grandTotalVal);
		$('[name="additionalTax"]').val(dataRow['additionalTax']);
		$('[name="amountAfterTax"], [name="totalAfterTax"]').val(amountAfterTax);
		
		console.log(grandTotalVal);
		console.log(" >>> " + ReplaceNumberWithCommas(grandTotalVal));
		
		$('#grandTotalOfBq').text(ReplaceNumberWithCommas(grandTotalVal));
		$('#amountAfterTax').text(ReplaceNumberWithCommas(parseFloat(amountAfterTax.toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))).toFixed(decimalLimit)));
		$('#finalTotal').text(ReplaceNumberWithCommas(parseFloat(amountAfterTax.toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"))).toFixed(decimalLimit)));
		$('.undoButtonDiv').show();
	});

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
	
	
	var decimalLimit = $('#decimal').val();
	this.value = this.value.replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}"));
	
	if (decimalLimit == '' || decimalLimit == undefined) {
		decimalLimit = 2;
	}
	var additionalTax = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
	var grandTotal = parseFloat($('#grandTotalOfBq').text().replace(/\,|\s|\#/g, ''));
	additionalTax = !isNaN(additionalTax) ? additionalTax : 0;
	grandTotal = !isNaN(grandTotal) ? grandTotal : 0;
	var afterTaxTotal = ReplaceNumberWithCommas((grandTotal + additionalTax).toFixed(decimalLimit));
	$('#amountAfterTax').text(afterTaxTotal);
	$('#finalTotal').text(afterTaxTotal);
	$('#idAmountAfterTax, #totalAfterTax').val((grandTotal + additionalTax).toFixed(decimalLimit));
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
	var decimalLimit = $('#decimal').val();
	if (decimalLimit == '' || decimalLimit == undefined) {
		decimalLimit = 2;
	}
	var grandTotalVal = 0;
	var totalAfterTax = 0;
	var additionalTax = 0;
	var additionalTaxDesc = "";
	var countNewData = 1;
	var html = '';
	$.each(item, function(i, data) {
		if (i > 0) {
			// return false;
		} else {
			html += '<h3>' + data.supplierBq.name + '</h3>';
			html += '<form id="supplierBqList">';
			html += '<div class="main_table_wrapper ph_table_border payment height-full1 mega">';
			html += '<table cellpadding="0" cellspacing="0" border="0" width="100%" class="header ph_table font-set" style="top: 0px;">';
			html += '<thead><tr><th class="width_50 width_50_fix"> </th><th class="width_300 width_300_fix"> Item Name </th>';
			html += '<th class="text-center width_100_fix width_100"> UOM </th><th class="text-center width_50_fix width_50"> Quantity </th>';
			html += '<th class="width_200 width_200_fix"> Unit Price (RM) </th>';
			html += '<th class="width_200 width_200_fix"> Total Amount (RM) </th>';
			html += '<th class="width_200 width_200_fix text-center"> TAX </th>';
			if (data.supplierBq.field1ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-center"> ' + data.supplierBq.field1Label + ' </th>';
			}
			if (data.supplierBq.field2ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-center"> ' + data.supplierBq.field2Label + ' </th>';
			}
			if (data.supplierBq.field3ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-center"> ' + data.supplierBq.field3Label + ' </th>';
			}
			if (data.supplierBq.field4ToShowSupplier) {
				html += '<th class="width_200 width_200_fix text-center"> ' + data.supplierBq.field4Label + ' </th>';
			}
			html += '<th class="width_200 width_200_fix text-center"> Total Amt with Tax (RM) </th>';
			html += '<th class="width_100 width_100_fix text-center"> </th>';
			html += '</tr></thead></table>';
		}
	});
	html += '<table class="deta ph_table table bq-table"><tbody>';
	html += '<input type="hidden" name="rfteventId" id="rfteventId" value="' + rfteventId + '"/>';
	html += '<input type="hidden" name="eventBqId" id="eventBqId" value="' + eventBqId + '"/>';

	$.each(item, function(i, data) {
		var totalAmount = '';
		if (data.totalAmount != undefined) {
			totalAmount = data.totalAmount;
		}
		html += '<tr data-item="' + data.id + '"><td class="width_50 width_50_fix">' + data.level + ' &nbsp; </td>';
		html += '<td class="width_300 width_300_fix"><span>' + data.itemName + '</span></td>';
		html += '<td class="text-center width_100 width_100_fix"></td>';
		html += '<td class="text-center width_50 width_50_fix"></td>';
		html += '<td class="width_200 width_200_fix text-center"></td>';
		html += '<td class="width_200 width_200_fix align-center">';
		html += '<span>&nbsp;</span></td>';
		html += '<td class="width_200 width_200_fix align-center"></td>';

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
		html += '<td class="width_200 width_200_fix align-center"></td>';
		html += '<td class="width_100 width_100_fix text-center"><div class="msg-popup">';
		html += '<div class="massage-popup-main" style="display:none;"><div class="hi-ca"></div>';
		html += '<textarea class="form-control">Write Your Remark...</textarea>';
		html += '<div class="btn-msg marg-top-15">';
		html += '<button class="btn btn-info ph_btn_extra_small saveRemarksBtn">Save</button>';
		html += '<button class="btn btn-black ph_btn_extra_small marg-left-10">Cancel</button>';
		html += '</div></div>';
		html += '</div></td>';
		html += '</tr>';

		if (data.children != undefined && data.children != null) {
			$.each(data.children, function(i, child) {
				html += renderChildBqHtml(child);
				if ((child.supplierBq && child.supplierBq.grandTotal) != undefined && (child.supplierBq && child.supplierBq.grandTotal) != null && grandTotalVal == 0) {
					grandTotalVal = parseFloat(child.supplierBq.grandTotal);
					totalAfterTax = parseFloat(child.supplierBq.totalAfterTax);
					additionalTax = parseFloat(child.supplierBq.additionalTax);
					additionalTaxDesc = child.supplierBq.taxDescription;
				}
			});
		}
	});
	html += '</tbody></table></div>';
	html += '<div class="total-with-tax pad_all_15 marg-top-10 marg-bottom-10">';
	html += '<div class="total-with-tax-inner"><label>Grand Total : </label><p id="grandTotalOfBq">';
	html += ReplaceNumberWithCommas(grandTotalVal.toFixed(decimalLimit));
	html += '</p></div></div>';
	html += '<div class="additinol-text pad_all_15 marg-top-20 marg-bottom-20">';
	html += '<div class="add_tex">';
	html += '<label>&nbsp;</label>';
	html += '<input type="text" name="additionalTax" id="additionalTax" class="validateregex form-control" data-validation-regexp="^(?!\\.?$)\\d{0,10}(\\.\\d{0,6})?$"  value="'
			+ ((additionalTax != null && additionalTax != undefined && !isNaN(additionalTax)) ? ReplaceNumberWithCommas(additionalTax.toFixed(2)) : '0.00') + '" placeholder="Additional Tax Amount"></div>';
	html += '<div class="Description_lower">';
	html += '<label>ADDITIONAL TAX DESC</label>';
	html += '<textarea class="form-control" name="additionalTaxDescription" >' + additionalTaxDesc + '</textarea>';
	html += '</div>';
	html += '</div></div>';

	html += '<div class="total-with-tax-final pad_all_15 marg-top-10 marg-bottom-10">';
	html += '<div class="total-with-tax-inner">';
	html += '<label>Total After Tax : </label><p class="color-red" id="amountAfterTax">';
	html += (((totalAfterTax != undefined && !isNaN(totalAfterTax)) ? ReplaceNumberWithCommas(totalAfterTax.toFixed(decimalLimit)) : (grandTotalVal != undefined ? ReplaceNumberWithCommas(grandTotalVal.toFixed(2)) : '0.00')));
	html += '</p></div></div>';
	html += '<div class="row">';
	html += '<div class="col-md-12 col-xs-12 col-sm-12">';
	html += '<button class="btn btn-black ph_btn_midium back_to_BQ hvr-pop hvr-rectangle-out1">Back to Bill of Quantity</button>';
	html += '<button id="pppp" class="btn btn-info ph_btn_midium marg-left-10 hvr-pop hvr-rectangle-out saveSupplierBq">Save</button>';
	html += '</div></div></form>';
	return html;
}

function renderChildBqHtml(child) {
	var decimalLimit = $('#decimal').val();
	if (decimalLimit == '' || decimalLimit == undefined) {
		decimalLimit = 2;
	}
	var html = '';
	html += '<tr class="sub_item" data-item="' + child.id + '">'
	html += child.totalAmountWithTax != undefined ? '<input type="hidden" name="totalAmountWithTax" id="" value="' + child.totalAmountWithTax + '"/>' : "";
	html += '<input type="hidden" name="itemName" id="" value="' + child.itemName + '"/>';
	html += '<input type="hidden" name="quantity" id="" value="' + child.quantity + '"/>';
	html += '<td class="width_50 width_50_fix"> ' + child.level + '.' + child.order + ' &nbsp;</td>';
	html += '<td class="width_300 width_300_fix">' + child.itemName + '<a data-toggle="collapse" data-target="#demo-' + child.id + '">View Description</a>';
	html += '<div id="demo-' + child.id + '" class="collapse table-contant">' + child.itemDescription + '.</div>' + '</td>';
	html += '<td class="text-center width_100 width_100_fix">  ';
	if (child.uom) {
		html += child.uom.uom;
	}
	html += '</td>';
	html += '<td class="text-center width_50 width_50_fix">   ' + child.quantity + '</td>';
	html += '<td class="width_200 width_200_fix text-center"><input type="text" data-pos="1"  name="unitPrice" class="validate form-control width_48per text-right" value=' + child.unitPrice + '></td>';
	html += '<td class="width_200 width_200_fix align-center"> <input data-pos="2"  class="validate form-control width_48per text-right" type="text" name="totalAmount" value="';
	html += child.totalAmount != undefined ? child.totalAmount : "";
	html += '"/></td>';
	html += '<td class="width_200 width_200_fix align-center">';
	html += '<input data-pos="3" type="text" name="tax" class="validate form-control width_30per" value="';
	html += child.tax != undefined ? child.tax : "";
	html += '"><select name="taxType" id="custom_' + child.id + '" class="custom-select" data-pos="4">';
	if (child.taxTypeList != undefined && child.taxTypeList != null) {
		$.each(child.taxTypeList, function(i, taxType) {
			html += '<option value="' + taxType + '"';
			html += child.taxType == taxType ? " selected='selected' " : "";
			html += '>' + taxType + '</option>';
		});
	}
	html += '</select>';
	html += '</td>';
	if (child.supplierBq && child.supplierBq.field1ToShowSupplier) {
		var isBuyer = (child.supplierBq.field1FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="5" name="field1"  class="validate form-control width_48per text-right"  value=' + child.field1 + ' >';
		} else {
			html += child.field1;
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field2ToShowSupplier) {
		var isBuyer = (child.supplierBq.field2FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="6" name="field2"  class="validate form-control width_48per text-right" value=' + child.field2 + '>';
		} else {
			html += child.field2;
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field3ToShowSupplier) {
		var isBuyer = (child.supplierBq.field3FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="7" name="field3"  class="validate form-control  width_48per text-right" value=' + child.field3 + '>';
		} else {
			html += child.field3;
		}
		html += '</td>';
	}
	if (child.supplierBq && child.supplierBq.field4ToShowSupplier) {
		var isBuyer = (child.supplierBq.field4FilledBy == 'BUYER' ? true : false);
		html += '<td class="width_200 width_200_fix align-center">';
		if (!isBuyer) {
			html += '<input type="text" data-pos="8" name="field4"  class="validate form-control width_48per text-right" value=' + child.field4 + '>';
		} else {
			html += child.field4;
		}
		html += '</td>';
	}
	html += '<td class="width_200 width_200_fix align-center">';
	html += child.totalAmountWithTax != undefined ? ReplaceNumberWithCommas(child.totalAmountWithTax.toFixed(decimalLimit)) : "";
	html += '</td>';
	html += '<td class="width_100 width_100_fix text-center"><div class="msg-popup">';
	html += '<div class="massage-popup-main" style="display:none;"><div class="hi-ca"></div>';
	html += '<textarea class="form-control">Write Your Remark...</textarea>';
	html += '<div class="btn-msg marg-top-15">';
	html += '<button class="btn btn-info ph_btn_extra_small saveRemarksBtn">Save</button>';
	html += '<button class="btn btn-black ph_btn_extra_small marg-left-10">Cancel</button>';
	html += '</div></div><button class="remark-toggle-btn"> <img src="' + getContextPath() + '/resources/images/msg-con.png" alt="pr"> </button>';
	html += '</div></td>';
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
			console.log(tmp[1].length);

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
function isFloat(value) {
	return value != "" && !isNaN(value) && Math.round(value) != value;
}
function remarksHtml(data) {
	var commentHtml = '';
	$.each(data, function(i, comment) {
		commentHtml += '<div class="row remark" id="' + comment.id + '"><p class="col-md-12"><span class="width100 pull-left align-left">';
		commentHtml += comment.userName != undefined ? comment.userName : "Test User";
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

// To refresh the page : -

var startPolling = Boolean($("#startPolling").val() == 'false');
var eventStatus = $("#eventStatus").val();
var header = $("meta[name='_csrf_header']").attr("content");
var token = $("meta[name='_csrf']").attr("content");
var eventId = $('#eventId').val();
var ajaxUrl = getContextPath() + '/supplier/refreshAuctionConsole/' + eventId;
function auctionPoll() {
// console.log("auctionPoll");
	startPolling = false;
	if (!startPolling)
		return;
	setTimeout(function() {
		$.ajax({
			url : ajaxUrl,
			type : "GET",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success : function(data) {
// console.log(data);

			},
			error : function(request, textStatus, errorThrown) {
				console.log("ERROR :  " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					var errorMsg = request.getResponseHeader('error').split(",").join("<br/>");
					showMessage('ERROR', errorMsg);
				}
			},
			complete : function() {
				if (startPolling) {
					auctionPoll();
				}
			}
		});
	}, 2000);
}
$(document).ready(function() {
	startPolling = Boolean($("#startPolling").val() == 'true');
// console.log("startPolling :"+startPolling);
	if (startPolling) {
		auctionPoll();
	}

});

function reloadBidderList() {
	if (typeof suppliersListConsole !== 'undefined') {
		suppliersListConsole.ajax.reload();
	}
}

$('#idTakeBid,#idTakeBid1').click(function(event) {

	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	
	var nextFeasibleBid = parseFloat($('#nextFeasibleBid').val());
	var auctionType = $('#auctionType').val();
// if (auctionType == 'REVERSE_ENGISH' || auctionType == 'FORWARD_ENGISH') {
		// console.log("$('input[name=totalAmount]').val() :"+
		// $('input[name=totalAmount]').val().replace(/,/g, ''));
		var currentBid = 0;
		if(event.currentTarget.id === 'idTakeBid' && isLumpsum){ 
			currentBid = parseFloat($('input[name=totalAmount]').val().replace(/,/g, ''));	
		}
		else{
			currentBid = $('input[name=totalAfterTax]').val() !== '' ? parseFloat($('input[name=totalAfterTax]').val().replace(/,/g, '')) : 0;
		}
// } else {
// var currentBid = $('input[name=totalAfterTax]').val() !== '' ?
// parseFloat($('input[name=totalAfterTax]').val().replace(/,/g, '')) : 0;
// }
	var forwardAuction = $('#forwardAuction').val();
	console.log(currentBid + " : " + nextFeasibleBid);
	console.log("nextFeasibleBid : " + nextFeasibleBid);
	if ($('#nextFeasibleBid').val() == "" || $('#nextFeasibleBid').val() == undefined ) {
		return true;
	} else {

		if (forwardAuction == "true") {
			if (nextFeasibleBid <= currentBid) {
				$('div[id=idGlobalError]').hide();
				return true;
			} else {
				var error = "Bid value is not accepted";
				$('p[id=idGlobalErrorMessage]').html(error);
				$('div[id=idGlobalError]').show();
				return false;
			}
		} else {
			if (nextFeasibleBid >= currentBid) {
				$('div[id=idGlobalError]').hide();
				return true;
			} else {
				var error = "Bid value is not accepted";
				$('p[id=idGlobalErrorMessage]').html(error);
				$('div[id=idGlobalError]').show();
				return false;
			}
		}
	}

});
