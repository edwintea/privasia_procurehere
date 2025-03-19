$(document).ready(function() {

	$(document).on("focusout", "#idSupplierListChosen_chosen .chosen-search input", function(e) {
		if ($('#idSupplierListChosen').children('option').length > 1) {
			return false;
		}
		$('#idSupplierListChosen_chosen .chosen-search input').val('');
		reloadSupplierList();
	});
	
	
	$(document).on("keyup", "#idSupplierListChosen_chosen .chosen-search input", keyDebounceDelay(function(e) {
		// ignore arrow keys
		switch (e.keyCode) {
		case 17: // CTRL
			return false;
			break;
		case 18: // ALT
			return false;
			break;
		case 37:
			return false;
			break;
		case 38:
			return false;
			break;
		case 39:
			return false;
			break;
		case 40:
			return false;
			break;
		}		
		  var favSupp = $.trim(this.value);
		  if (favSupp.length > 2 || favSupp.length == 0 || e.keyCode == 8) {
			  reloadSupplierList();
		  }
	}, 650));
	
});

function reloadSupplierList() {

	var favSupp = $.trim($('#idSupplierListChosen_chosen .chosen-search input').val());
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var eventId = $('#eventId').val();
	$.ajax({
		url : getBuyerContextPath('searchCurrentSuppliers'),
		data : {
			'searchSupplier' : favSupp,
			'eventId' : eventId
		},
		type : 'POST',
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data) {
			var html = '<option value="">Search suppliers</option>';
			if (data != '' && data != null && data.length > 0) {
				$.each(data, function(key, value) {
					html += '<option value="' + value.id + '" ' + (value.id ? '' : 'disabled')  +  ' >' + value.companyName + '</option>';
				});
			}
			$('#idSupplierListChosen').html(html);
			$("#idSupplierListChosen").trigger("chosen:updated");
			$('#idSupplierListChosen_chosen .chosen-search input').val(favSupp);
		},
		error : function(error) {
			console.log(error);
		}, complete : function() {
			$('#loading').hide();
		}
	});
}