function updateLink(id) {
	var link = $("a#idConfirmDelete");
	link.data('href', link.attr("href"));
	link.attr("href", link.data('href') + '' + id);
}

function doCancel() {
	var link = $("a#idConfirmDelete");
	link.data('href', link.attr("href"));
	link.attr("href", getContextPath() + '/plan/deletePlan/');
}
var baseUsers = 0;
$(document).ready(function(){
	
//	$('#chosenCurrency').val();
	
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
		var price = $(this).val();
		var taxValueFormat = $('#idTax').val();
		var taxAmountFormt = price;
		if(price !== undefined && price !== '' && taxValueFormat !== undefined && taxValueFormat !== '' ){
			price = parseFloat(price);
			taxValueFormat = parseFloat(taxValueFormat);
			taxAmountFormt = price + ((price * taxValueFormat)/100) ;
		}else{
			taxValueFormat = 0;
		}
		
		$('#idEditorTax').html('('+taxAmountFormt.toFixed(2)+' Inclusive '+taxValueFormat+'% SST)');
	});
	$('#idTax').change(function(){
		var price = $('#price').val();
		var taxValueFormat = $(this).val();
		var taxAmountFormt = price;
		if(price !== undefined && price !== '' && taxValueFormat !== undefined && taxValueFormat !== '' ){
			price = parseFloat(price);
			taxValueFormat = parseFloat(taxValueFormat);
			taxAmountFormt = price + ((price * taxValueFormat)/100) ;
			$('#idEditorTax').html('('+taxAmountFormt.toFixed(2)+' Inclusive '+taxValueFormat+'% SST)');
		}
		
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
			console.log("====" + chargeModel);
			if(chargeModel == 'Flat Fee') {
				console.log("====" + chargeModel);
				$('#idEditorDuration').html('');
			} else {
				$('#idEditorDuration').html('PER USER ');
			}
			//$('#idEditorFeeType').html($('#periodUnit option:selected').text());
		}
	});
	$(document).delegate('.removeRange', 'click', function(e) {
		e.preventDefault();
		$(this).closest( 'tr').remove();
	});
	
	
	
	
	//new Buyer plan 
	$(document).delegate('.removeRange', 'click', function(e) {
		e.preventDefault();
		$(this).closest( 'tr').remove();
	
	});
	
	$(document).delegate('#saveBuyerPlan', 'click', function(e) {
		e.preventDefault();
		var endRange = 9999999;
		var error = false;
		var startError = false;

		$('.error-range.text-danger').remove();
		if($('#rangeList tr input').length == 0){
			//alert("start range should be start from 1");
			$('#addRange').before('<p style="margin-bottom:10px;" class="error-range text-danger">Please add atleast 1 Plan range</p>');
			return false;
		}
		var rangeErrr = true;
		$('#rangeList tr input').each(function(index){
			// checking range in series
			var newStartRange = 0;
			if(endRange != 9999999 && $(this).attr('data-start') == 'start'){
				newStartRange = $(this).val();
				if(parseInt(newStartRange) !== (parseInt(endRange) + 1 )){
					error = true;
				}
			}		
			// Start range should be start from 1
			if(endRange == 9999999 && $(this).attr('data-start') == 'start' && parseInt($(this).val()) != 1){
				startError = true;
			}

			if(error){
				//alert("start range '" +newStartRange +"' is not in series with end range '" + endRange+"'");
				var errormSg = "Start range '" +newStartRange +"' is not in series with end range '" + endRange+"'";
				$('#addRange').before('<p style="margin-bottom:10px;" class="error-range text-danger">'+errormSg+'</p>');
				return false;
				rangeErrr = false;
			}
			if($(this).attr('data-end') == 'end'){
				endRange = $(this).val();
			}
			
		});
		if(startError){
			//alert("start range should be start from 1");
			$('#addRange').before('<p style="margin-bottom:10px;" class="error-range text-danger">Start range should be start from 1</p>');
			return false;
		}
		if(!rangeErrr){
			return false;
		}
		if(!error){
			$('#frmBuyerPlan').submit();
		}
	});
	
	function validateRangeData(){
		var rangeStartId = ($.trim($('#rangeStartId').val()) == ''? '':parseInt($.trim($('#rangeStartId').val())));
		var rangeEndId = ($.trim($('#rangeEndId').val()) == ''? '':parseInt($.trim($('#rangeEndId').val())));
		var priceId = ($.trim($('#priceId').val()) == ''? '':parseInt($.trim($('#priceId').val())));
		var errorChk = 0;
		var startError = true;
		console.log(rangeStartId +" =" + rangeEndId);
		$('.error-range.text-danger').remove();
		if(rangeStartId === 0){
			$('#priceId').before('<p style="margin-bottom:10px;" class="error-range text-danger">Start range value should be greater then "0"</p>');
			errorChk = 1;
			startError = false;
		}
		if((rangeStartId > rangeEndId || rangeStartId == rangeEndId || rangeStartId == '' ) && rangeEndId !== ''){
			$('#priceId').before('<p style="margin-bottom:10px;" class="error-range text-danger">Start range value should be less than end value</p>');
			errorChk = 1;
		}
		if((priceId < 0 || priceId == '') && rangeEndId !== '' && rangeStartId !== ''){
			$('#priceId').after('<p style="margin-top:10px;" class="error-range text-danger">Please enter Plan Price</p>');
			errorChk = 1;
		}
		
		if((rangeStartId === '' || rangeEndId === '' ) && startError){
			$('#priceId').before('<p style="margin-bottom:10px;" class="error-range text-danger">Please enter range value</p>');
			errorChk = 1;
		}
		
		
		return errorChk;
	}
	function validateTimeRangeData(){
		var durationId = ($.trim($('#durationId').val()) == ''? '':parseInt($.trim($('#durationId').val())));
		var discountId = ($.trim($('#discountId').val()) == ''? '':parseFloat($.trim($('#discountId').val())));
		var errorChk = 0;
		$('.error-range.text-danger').remove();
		if(durationId < 0 || durationId == ''){
			$('#discountId').before('<p style="margin-bottom:10px;" class="error-range text-danger">Please enter duration</p>');
			errorChk = 1;
		}
		console.log("Discount value :" + discountId);
		if(discountId === ''){
			console.log("Discount value :" + discountId);
			$('#discountId').after('<p style="margin-top:10px;" class="error-range text-danger">Please enter discount</p>');
			errorChk = 1;
		}
		return errorChk;
	}
	$(document).delegate('#rangeStartId, #rangeEndId, #priceId', 'change', function(e) {
		validateRangeData();
	});
	$(document).delegate('#durationId, #discountId', 'change', function(e) {
		validateTimeRangeData();
	});
	$(document).delegate('#addRange', 'click', function(e) {
		e.preventDefault();
		$('.range-header').removeClass('hide');	
		
		console.log('table postion :' + $( "tr:last" ).attr('data-pos'));
		var rangeStart = $('#rangeStartId').val();
		var rangeEnd = $('#rangeEndId').val();
		var price = $('#priceId').val();
		var currencyCode = $("#chosenCurrency option:selected").text();
		
		var displayLabel = rangeStart+' to '+rangeEnd;
		var pos = $( "#rangeList tr:last" ).attr('data-pos');
		
		if(typeof pos === 'undefined' || pos === '' ){
			pos = 0;
		}else{
			pos = parseInt(pos) + 1;
		}
		
		// setting the base user 
		if(pos == 0 || pos == '0'){
		$('#baseUsers').val(rangeEnd);
		}
		
		var html = '<tr data-pos="'+pos+'">';
			html += '<td class="width_50 width_50_fix align-center removeRange" > <a data-placement="top" title="Delete" > <img src="'+ getContextPath()+'/resources/images/delete1.png"></i></a> ';
			html += '<input id="rangeList'+pos+'.rangeStart" name="rangeList['+pos+'].rangeStart" value="'+rangeStart+'" data-start="start" class="range-start" type="hidden">';
			html += '<input id="rangeList'+pos+'.rangeEnd" name="rangeList['+pos+'].rangeEnd" value="'+rangeEnd+'" data-end="end" class="range-end" type="hidden">';
			html += '<input id="rangeList'+pos+'.price" name="rangeList['+pos+'].price" value="'+price+'" type="hidden">';
			html += '<input id="rangeList'+pos+'.displayLabel" name="rangeList['+pos+'].displayLabel" value="'+displayLabel+'" type="hidden">';
			html += '<td class="width_200 width_200_fix align-left"> '+displayLabel+' </td>';
			html += '<td class="width_100 width_100_fix align-center"> '+price+'</td>';
			html += '</tr>';
			$("#rangeList tbody").append(html);
			$('#rangeStartId').val('');
			$('#rangeEndId').val('');
			$('#priceId').val('');
	});
	
	
	$(document).delegate('#addPeriod', 'click', function(e) {
		e.preventDefault();
		if(validateTimeRangeData() == 1){
			return false;
		}
		$('.period-header').removeClass('hide');	
		
		var duration = $('#durationId').val();
		var discount = $('#discountId').val();
		var pos = $( "#periodList tr:last" ).attr('data-pos');
		
		
		if(typeof pos === 'undefined' || pos === '' ){
			pos = 0;
		}else{
			pos = parseInt(pos) + 1;
		}
		var html = '<tr data-pos="'+pos+'">';
			html += '<td class="width_50 width_50_fix align-center removePeriod" > <a data-placement="top" title="Delete" > <img src="'+ getContextPath()+'/resources/images/delete1.png"></i></a> ';
			html += '<input id="planPeriodList'+pos+'.planDuration" name="planPeriodList['+pos+'].planDuration" value="'+duration+'" type="hidden">';
			html += '<input id="planPeriodList'+pos+'.planDiscount" name="planPeriodList['+pos+'].planDiscount" value="'+discount+'" type="hidden">';
			html += '<td class="width_200 width_200_fix align-center"> '+duration+' </td>';
			html += '<td class="width_100 width_100_fix align-center"> '+discount+' ( % ) </td>';
			html += '</tr>';
			$("#periodList tbody").append(html);
			$('#durationId').val('');
			$('#discountId').val('');
	});
	
	$(document).delegate('.removePeriod', 'click', function(e) {
		e.preventDefault();
		$(this).closest( 'tr').remove();
	});
	
});


function deleteBuyerPlan(id) {
	var link = $("a#idConfirmBuyerPlanDelete");
	console.log(link);
	link.data('href', link.attr("href"));
	link.attr("href", link.data('href') + '' + id);
}


function doBuyerPlanCancel() {
	var link = $("a#idConfirmBuyerPlanDelete");
	link.data('href', link.attr("href"));
	link.attr("href", getContextPath() + '/plan/deleteBuyerPlan/');
}
