function updateLink(id) {
	var link = $("a#idConfirmDelete");
	link.data('href', link.attr("href"));
	link.attr("href", link.data('href') + '' + id);
}
function doCancel() {
	var link = $("a#idConfirmDelete");
	link.data('href', link.attr("href"));
	link.attr("href", getContextPath() + '/supplierplan/deletePlan/');
}
$(document).ready(function(){
	$('#idPlanName').keyup(function(){
		$('#idEditorPlanName').html($.trim($(this).val()));
		$('#idPlanButton').html('Get ' + $.trim($(this).val()));
	});
	$('#idPlanDescription').keyup(function(){
		$('#idEditorPlanDesc strong').html($.trim($(this).val()));
	});
	$('#price').keyup(function(){
		if($("#chosenCurrency option:selected").text()) {
			$('#idEditorPrice').html($("#chosenCurrency option:selected").text() + ' ' + $.trim($(this).val()));
		} else {
			$('#idEditorPrice').html('USD ' + $.trim($(this).val()));
		}
		//$('#idEditorPrice').html($.trim($(this).val()));
	});
	$('#chosenCurrency').change(function(){
		if($('#chosenCurrency option:selected').text()) {
			if($('#price').val()) {
				$('#idEditorPrice').html($('#chosenCurrency option:selected').text() + ' ' + $.trim($('#price').val()));
			} else {
				$('#idEditorPrice').html($('#chosenCurrency option:selected').text() + ' 50');
			}
		} else {
			$('#idEditorPrice').html('USD ' + $.trim($(this).val()));
		}
	});
	$('#periodUnit').change(function(){
		if($('#periodUnit option:selected').text()) {
			$('#idEditorDuration').html('PER ' + $('#periodUnit option:selected').text());
		}
	});
	$('#chargeModel').change(function(){
		if($('#chargeModel option:selected').text()) {
			var chargeModel = $('#chargeModel option:selected').text();
			if(chargeModel == 'Flat Fee') {
				$('#idEditorFeeType').html('');
			} else {
				$('#idEditorFeeType').html('PER EVENT ');
			}
			//$('#idEditorFeeType').html($('#periodUnit option:selected').text());
		}
	});
});