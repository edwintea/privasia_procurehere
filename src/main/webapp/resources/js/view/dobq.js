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
	var grandTotal = 0.0;
	
	// If additional tax is empty, assign it a value of 0.xxx
	if($('#additionalTax').val() == undefined || $('#additionalTax').val() == ''){
		$('#additionalTax').val(parseFloat('0').toFixed(decimalLimit));
	}
	var additionalTax = parseFloat($.trim($('#additionalTax').val()).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));

	$('.rowTotalAfterTaxAmount').each(function() {
		bqTotal += parseFloat( $.trim($(this).html()).replace(/,/g, '') );
	});

	grandTotal = bqTotal + additionalTax;
	
	$('#idTotal').html(ReplaceNumberWithCommas(bqTotal.toFixed(decimalLimit)));
	$('#idGrandTotal').html(ReplaceNumberWithCommas(grandTotal.toFixed(decimalLimit)));
	$('#additionalTax').val(ReplaceNumberWithCommas(additionalTax.toFixed(decimalLimit)));

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
            if($.trim(value).length > 2000) {
            	return 'Max 2000 characters allowed'
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

		// Get all inputs for current row
		var inputs = $(this).closest('tr').find('input[type=text]');

		if($.trim($(inputs[0]).val()) == ''){
			return;
		}
		if($.trim($(inputs[1]).val()) == ''){
			return;
		}

		if($.trim($(inputs[2]).val()) == ''){
			$(inputs[2]).val(parseFloat('0').toFixed(decimalLimit));
		}

		var qty = parseFloat($.trim($(inputs[0]).val()).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));
		var price = parseFloat($.trim($(inputs[1]).val()).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));
		var tax = parseFloat($.trim($(inputs[2]).val()).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));

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
		$(inputs[0]).val(ReplaceNumberWithCommas(qty.toFixed(decimalLimit)));
		$(inputs[1]).val(ReplaceNumberWithCommas(price.toFixed(decimalLimit)));
		$(inputs[2]).val(ReplaceNumberWithCommas(tax.toFixed(decimalLimit)));
		
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

