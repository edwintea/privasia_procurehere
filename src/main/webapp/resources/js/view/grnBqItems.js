function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}

function deleteLink(id) {
	var button = $(".deleteItem");
	button.attr("id",  id);
}

function deleteItem(id) {
	//var rowCount = $('#draftTabItems tr').length;
	$('#'+id).remove();
	computeTotals();
}

function computeTotals() {
	var bqTotal = 0.0;
	
	$('.rowTotalAfterTaxAmount').each(function() {
		bqTotal += parseFloat( $.trim($(this).html()).replace(/,/g, '') );
	});

	
	$('#idTotal').html(ReplaceNumberWithCommas(bqTotal.toFixed(decimalLimit)));
	$('#idGrandTotal').html(ReplaceNumberWithCommas(bqTotal.toFixed(decimalLimit)));

}

$(document).ready(function() {
	
	$.fn.editable.defaults.mode = 'inline';
	
    $('.inline').editable({
    	validate: function(value) {
            if($.trim(value) == '') {
                return 'Value is required.';
            }
            if($.trim(value).length > 250) {
            	return 'Max 250 characters allowed'
            }
        },
        success: function(response, newValue) {
        	console.log('Success called...', response, newValue);
        	$(this).closest('td').find('input[name=itemName]').val($.trim(newValue));
        }
    });

    $('.inlineDescription').editable({
    	emptytext: 'No Description',
    	validate: function(value) {
            if($.trim(value).length > 1000) {
            	return 'Max 1000 characters allowed'
            }
        },
        success: function(response, newValue) {
        	console.log('Success called...', response, newValue);
        	$(this).closest('td').find('input[name=itemDescription]').val($.trim(newValue));
        }
    });

	$(document).delegate('#additionalTax', 'change', function(e) {
		e.preventDefault();
		computeTotals();
	});
	
	$(document).delegate('.itemValue', 'change', function(e) {
		e.preventDefault();
			$(this).closest('td').children('.cuse').remove();
		// Get all inputs for current row
		var receivedQty=$(this).val();
		var unitprice=$(this).closest('tr').find("#idunitprice").text();
		var itemtax=$(this).closest('tr').find("#iditemTax").text();
		var previousReceivedQuantity = $(this).closest('tr').find("#previousReceivedQuantity").text();
		var idquantity=$(this).closest('tr').find("#idquantity").text();
		
		
		console.log("unitprice : " + unitprice + " receivedQty : " + receivedQty + " previousReceivedQuantity  : " + previousReceivedQuantity + " PO quantity : " + idquantity);

		if(receivedQty == ''){
			return false;
		}

		if($.trim(itemtax) == ''){
			$(itemtax).val(parseFloat('0').toFixed(decimalLimit));
		}

		var qty = parseFloat($.trim(receivedQty).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));
		var price = parseFloat($.trim(unitprice).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));
		var tax = parseFloat($.trim(itemtax).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));

		var pqty = parseFloat($.trim(previousReceivedQuantity).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));
		var tqty = parseFloat($.trim(idquantity).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));
		
		$(this).closest('td').children('.cuse').remove();
		if((parseFloat(qty)+parseFloat(pqty)) > parseFloat(tqty)){
			console.log("error.....");
			$(this).parent().addClass('has-error').append('<span class="cuse error-range text-danger">Received Quantity cannot be more than PO quantity</span>');
			return false;
		} 
		var total = 0;
		var taxAmount = 0;
		var totalWithTax = 0;
		
		total = (qty * price).toFixed(decimalLimit); // Do rounding on calculation result
//		total = parseFloat(total.toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));
		taxAmount = ((total*tax)/100).toFixed(decimalLimit); // Do rounding on calculation result
//		taxAmount = parseFloat(taxAmount.toString().replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")))
		totalWithTax = (parseFloat(total) + parseFloat(taxAmount));
		
		$(this).closest('tr').find('.rowTotalAmount').html(ReplaceNumberWithCommas(total));
		$(this).closest('tr').find('.rowTaxAmount').html(ReplaceNumberWithCommas(taxAmount));
		$(this).closest('tr').find('.rowTotalAfterTaxAmount').html(ReplaceNumberWithCommas(totalWithTax.toFixed(decimalLimit)));

		//put back the corrected and formatted values into inputs
		$(this).val(ReplaceNumberWithCommas(qty.toFixed(decimalLimit)));
		
		computeTotals();
		
	});
	
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

});

