/* Datepicker bootstrap */

$(function() {
	"use strict";
	
	$('#deliveryDate').bsdatepicker({ 
		format : 'dd/mm/yyyy',
		onRender : function(date) {
			if(date.valueOf() < $.now()){
				return 'disabled' ;
			}
		}
	
	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
	});

	$.validate({ lang : 'en'});

});

$.formUtils.addValidator({ name : 'buyer_address', validatorFunction : function(value, $el, config, language, $form) {
	var response = true;
	var fieldName = $el.attr('name');
	console.log($('[name="' + fieldName + '"]:checked').length);
	if ($('[name="' + fieldName + '"]:checked').length == 0) {
		response = false;
	}
	return response;
}, errorMessage : 'This is a required field', errorMessageKey : 'badBuyerAddress' });

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
	$('.buyerAddressRadios').addClass('active enabledBlock');
	$.uniform.update();
	$("#sub-credit").slideUp();
});
$(document).delegate('#deletecorpAddress', 'click', function() { 
 $(".buyerAddressRadios").removeClass("active");
 $('#sub-credit input[type="radio"]').prop('checked', false);
 $.uniform.update();
 $("#sub-credit").slideDown();
});
$(document).delegate('.phisicalArressBlock', 'click', function(event) { 
	$("#sub-credit").slideToggle();
});
//Skip JQuery validations for save draft
$(".skipvalidation ").on('click', function(e){  
	  if($("#skipper").val() == undefined ){  
		e.preventDefault();
	   
		$(this).after("<input type='hidden' id='skipper' value='1'>");		
		 $('form.has-validation-callback :input').each(function(){	   
			$(this).on('beforeValidation', function(value, lang, config) {
				$(this).attr('data-validation-skipped', 1);
		  }); 
	   });
		$(this).trigger("click");		
	  }  
	});

// save draft
$('#submitStep1PrDetailDraft').click(function(e) {
	$('#prDeliveryForm').attr('action',getContextPath() + "/buyer/savePrDeliveryDraft");
	$('#prDeliveryForm').submit();
	
});

$('.timepicker-example').timepicker({
	disableFocus : true,
	explicitMode : false
}).on('hide.timepicker', function(e) {
	e.preventDefault();
	$(this).blur();
});