var stripe = null;
var elements = null;
$(document).ready(function () {
	$("#radio_0").attr('checked', true);
	if ($('.tab-link-0').attr('data-att-tab') === "userTab") {
		calculateTotalFeeUser();
	} else {
		calculateTotalFeeEvent();
	}
	$('ul.tabs li').click(function () {
		var tab_id = $(this).attr('data-tab');

		$('ul.tabs li').removeClass('current');
		$('.tab-content').removeClass('current');

		$(this).addClass('current');
		$("#" + tab_id).addClass('current');
	})
	stripe = Stripe($('#stripePublishKey').val());
	elements = stripe.elements()
	$(document).on('submit', '#payment-form-fpx', function (event) {
		var fpxBank = elements.getElement('fpxBank');
		event.preventDefault();
		payByFpx(event, fpxBank);
	});
});

$('.eventTab').on('click', function (event) {
	calculateTotalFeeEvent();
});

$('.userTab').on('click', function (event) {
	calculateTotalFeeUser();
});

$(document).on("keyup", ".noOfEvent", function (e) {
	var noOfEvent = parseInt($(this).val());
	var currencyCode = $('#eventcurrencyId').val();
	var promoCode = $('#promoCodeEvent').val();

	$('#userError').html('').removeClass('has-error');
	ajaxCallOnChangeEventFields(noOfEvent, promoCode, currencyCode);
});


$('#promoCodeEvent').on('change', function (e) {
	var noOfEvent = parseInt($('.noOfEvent').val());
	$('#promoErrorEvent').html('').removeClass('has-error');
	var promoCode = $(this).val();
	var currencyCode = $('#eventcurrencyId').val();
	ajaxCallOnChangeEventFields(noOfEvent, promoCode, currencyCode);
});


$(document).on("keyup", ".noOfuser", function (e) {
	var noOfUser = parseInt($(this).val());
	if (isNaN(noOfUser)) {
		$('#userNo').text("each user");
		$('#appNo').text("1 approver user");
		var approvalUser = 'no approver users ';
		$("#approvalUser").html(approvalUser);
	} else {
		$('#userNo').text(noOfUser + " users");
		$('#appNo').text(noOfUser + " approver users");
		var approvalUser = noOfUser + ' approver users ';
		$("#approvalUser").html(approvalUser);
	}

	$('#userError').html('').removeClass('has-error');
	var promoCode = $('#promoCodeUser').val();
	var selectedMonths = parseInt($("input[name=periodId]:checked").attr('data-duration'));
	var radioIndex = $("input[name=periodId]:checked").attr('data-index');
	var montlhyDiscount = parseInt($('#discountValue' + radioIndex).text());
	var currencyCode = $('#usercurrencyId').val();
	ajaxCallOnChangeUserFields(noOfUser, promoCode, selectedMonths, montlhyDiscount, currencyCode);
});


$('input[name="periodId"]:radio').on('change', function (e) {
	var noOfUser = parseInt($('.noOfuser').val());
	$('#userError').html('').removeClass('has-error');
	var promoCode = $('#promoCodeUser').val();
	var selectedMonths = parseInt($(this).attr('data-duration'));
	var radioIndex = $(this).attr('data-index');
	var montlhyDiscount = parseInt($('#discountValue' + radioIndex).text());
	var currencyCode = $('#usercurrencyId').val();
	ajaxCallOnChangeUserFields(noOfUser, promoCode, selectedMonths, montlhyDiscount, currencyCode);
});

$('#promoCodeUser').on('change', function (e) {
	//alert($(this).val());
	var noOfUser = parseInt($('.noOfuser').val());
	//	console.log("noOfUser :"+$(this).val());
	$('#promoErrorUser').html('').removeClass('has-error');
	/*if($('.noOfuser').val() === 'undefined' || $('.noOfuser').val() === '0' ){
		console.log("noOfUser :"+$(this).val());
		$('#userError').html('<span class="help-block form-error">User should be greater then 0</span>').addClass('has-error');
		return false;
	}*/
	var promoCode = $(this).val();

	var selectedMonths = parseInt($("input[name=periodId]:checked").attr('data-duration'));

	var radioIndex = $("input[name=periodId]:checked").attr('data-index');
	var montlhyDiscount = parseInt($('#discountValue' + radioIndex).text());

	var currencyCode = $('#usercurrencyId').val();

	ajaxCallOnChangeUserFields(noOfUser, promoCode, selectedMonths, montlhyDiscount, currencyCode);
});


function calculateTotalFeeUser() {
	var noOfUser = parseInt($('.noOfuser').val());
	var taxFormt = parseFloat($('#taxFormtUser').val());
	console.log("taxFormt: " + taxFormt);
	//for show approver users 
	$("#approvalUser").html(noOfUser + ' approver users ');

	var currencyCode = $('#usercurrencyId').val();
	var selectedMonths = $('#radio_0').attr('data-duration');
	$('#userRangeTable tr').each(function () {
		var rangeId = $(this).children('td:first').attr('data-id');
		var start = parseInt($(this).children('td:first').attr('data-start'));
		var end = parseInt($(this).children('td:first').attr('data-end'));
		var price = parseInt($(this).children('td:first').attr('data-price'));
		var basePrice = $('#basePrice').val();
		var baseUsers = $('#baseUsers').val();

		//    console.log("basePrice :" + basePrice);
		//   console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfUser :"+ noOfUser);

		if (noOfUser >= start && noOfUser <= end) {
			//	    	console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfUser1 :"+ noOfUser);

			$('#rangeUserId').val(rangeId);

			var baseFeeValue = 0;
			if (basePrice !== undefined) {
				//	console.log("baseprice ppppp :" +basePrice);
				basePrice = parseInt($('#basePrice').val());
				//	    		$('#totalFeeTableUser tr').eq(-1).before("<tr><td>new row</td></tr>")
				var baseFeeLabel = currencyCode + " " + basePrice + " for " + baseUsers + " Users X " + selectedMonths + " Months";
				$('#totalFeeTableUser').find("#baseFeeLabel").html(baseFeeLabel);

				baseFeeValue = (basePrice * selectedMonths);
				//	console.log("baseFeeValue :" + baseFeeValue);
				$('#totalFeeTableUser').find("#baseFeeValue").html(ReplaceNumberWithCommas((baseFeeValue).toFixed(2)));
				$('#baseFeeTr').removeClass('flagvisibility');

				if (baseUsers >= noOfUser) {
					var totalFeeValue = (price * noOfUser * selectedMonths);
					//		console.log("totalFeeValue :" + totalFeeValue);
					var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
					$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
					$('#totalFeeTr').addClass('flagvisibility');
				} else {
					noOfUser = noOfUser - baseUsers;
					var totalFeeLabel = currencyCode + " " + price + " X " + noOfUser + " Users X " + selectedMonths + " Months";
					$('#totalFeeTableUser').find("#totalFeeLabel").html(totalFeeLabel);

					var totalFeeValue = (price * noOfUser * selectedMonths);
					//		console.log("totalFeeValue :" + totalFeeValue);
					var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
					$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
					$('#totalFeeTr').removeClass('flagvisibility');
				}
				totalFeeValue = (basePrice !== undefined ? basePrice : 0) + totalFeeValue;
			} else {

				var totalFeeLabel = currencyCode + " " + price + " X " + noOfUser + " Users X " + selectedMonths + " Months";
				$('#totalFeeTableUser').find("#totalFeeLabel").html(totalFeeLabel);

				var totalFeeValue = (price * noOfUser * selectedMonths);
				//	    	console.log("totalFeeValue :" + totalFeeValue);
				var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
				$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
				$('#totalFeeTr').removeClass('flagvisibility');
			}



			var selectMonthDiscount = parseInt($('#discountValue0').text());
			var totalFeeDiscountLabel = "Subscription Discount " + selectMonthDiscount + "%";
			//	    	console.log("totalFeeDiscountLabel :" + totalFeeDiscountLabel);
			$('#totalFeeTableUser').find("#totalFeeDiscountLabel").html(totalFeeDiscountLabel);

			var totalFeeDiscountValue = (totalFeeValue / 100) * selectMonthDiscount;
			//	    	console.log("totalFeeDiscountValue :"+ totalFeeDiscountValue);
			var feeDiscountValue = '<input type="hidden" name="feeDiscountValue" value="' + totalFeeDiscountValue + '" >';
			$('#totalFeeTableUser').find("#totalFeeDiscountValue").html(feeDiscountValue + "-" + ReplaceNumberWithCommas((totalFeeDiscountValue).toFixed(2)));
			//	    	console.log("totalFeeValue :" + totalFeeValue);
			var totalFeeAmount = totalFeeValue - totalFeeDiscountValue;
			//	    	console.log("totalFeeAmount : "+totalFeeAmount);
			var totalTax = (totalFeeAmount * taxFormt) / 100;
			totalFeeAmount = totalFeeAmount + totalTax;
			var totalFeeAmountValue = '<input type="hidden" name="totalFeeAmount" value="' + totalFeeAmount + '" >';
			$('#totalFeeTableUser').find("#tax").html(ReplaceNumberWithCommas((totalTax).toFixed(2)));
			$('#totalFeeTableUser').find('#totalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
		}
	});
}

function calculateTotalFeeEvent() {
	var noOfEvent = $('.noOfEvent').val();
	var currencyCode = $('#eventcurrencyId').val();
	var taxFormt = parseFloat($('#taxFormtEvent').val());

	$('#eventRangeTable tr').each(function () {
		var rangeId = $(this).children('td:first').attr('data-id');
		var start = parseInt($(this).children('td:first').attr('data-start'));
		var end = parseInt($(this).children('td:first').attr('data-end'));
		var price = parseInt($(this).children('td:first').attr('data-price'));
		//   console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfEvent :"+ noOfEvent);

		if (noOfEvent >= start && noOfEvent <= end) {
			//   	console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfEvent1 :"+ noOfEvent);
			$('#rangeEventId').val(rangeId);

			var totalFeeLabel = currencyCode + " " + price + " X " + noOfEvent + " Events ";
			$('#totalFeeTableEvent').find("#totalFeeLabel").html(totalFeeLabel);
			var totalFeeValue = (price * noOfEvent);
			var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
			$('#totalFeeTableEvent').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));

			var totalTax = (totalFeeValue * taxFormt) / 100;
			totalFeeValue = totalFeeValue + totalTax;
			var totalFeeAmountValue = '<input type="hidden" name="totalFeeAmount" value="' + totalFeeValue + '" >';

			$('#totalFeeTableEvent').find("#tax").html(ReplaceNumberWithCommas((totalTax).toFixed(2)));
			$('#totalFeeTableEvent').find('#totalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));

		}
	});

}

function ajaxCallOnChangeUserFields(noOfUser, promoCode, selectedMonths, montlhyDiscount, currencyCode) {
	var taxFormt = parseFloat($('#taxFormtUser').val());
	if (promoCode !== 'undefined' && promoCode !== '') {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var url = getContextPath() + "/buyerSubscription/getPromoCode";
		var changePlan = $('#changePlanId').val();
		var basePrice = $('#basePrice').val();
		var baseUsers = $('#baseUsers').val();
		var planId = '';
		if ($('#idSubscribeForm')) {
			planId = $('#idSubscribeForm').attr('action').split('/')[$('#idSubscribeForm').attr('action').split('/').length - 1];
		}
		//		console.log("changePlan : " + changePlan);
		if (changePlan !== 'undefined' && changePlan) {
			url = getContextPath() + "/buyer/billing/getPromoCode";
		}
		$.ajax({
			type: "GET",
			url: url,
			data: {
				promoCode: promoCode,
				plan: planId,
				totalPrice: Number($('input[name=totalFeeAmount]').val())
			},
			beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			complete: function () {
				$('#loading').hide();
			},
			success: function (data) {
				//console.log(data);
				$('#promoErrorUser').html('').removeClass('has-error');
				$('#userRangeTable tr').each(function () {
					var rangeId = $(this).children('td:first').attr('data-id');
					var start = parseInt($(this).children('td:first').attr('data-start'));
					var end = parseInt($(this).children('td:first').attr('data-end'));
					var price = parseInt($(this).children('td:first').attr('data-price'));
					var basePrice = $('#basePrice').val();
					var baseUsers = $('#baseUsers').val();
					//   console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfUser :"+ noOfUser);

					if (noOfUser >= start && noOfUser <= end) {
						//					    	console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfUser1 :"+ noOfUser);

						$('#rangeUserId').val(rangeId);

						$('#promoCodeUserId').val(data.id);

						var baseFeeValue = 0;
						if (basePrice !== undefined) {
							//		console.log("baseprice ppppp :" +basePrice);
							basePrice = parseInt($('#basePrice').val());
							//					    		$('#totalFeeTableUser tr').eq(-1).before("<tr><td>new row</td></tr>")
							var baseFeeLabel = currencyCode + " " + basePrice + " for " + baseUsers + " Users X " + selectedMonths + " Months";
							$('#totalFeeTableUser').find("#baseFeeLabel").html(baseFeeLabel);

							baseFeeValue = (basePrice * selectedMonths);
							//    	console.log("baseFeeValue :" + baseFeeValue);
							$('#totalFeeTableUser').find("#baseFeeValue").html(ReplaceNumberWithCommas((baseFeeValue).toFixed(2)));
							$('#baseFeeTr').removeClass('flagvisibility');

							if (baseUsers >= noOfUser) {
								var totalFeeValue = (price * noOfUser * selectedMonths);
								//	    		console.log("totalFeeValue :" + totalFeeValue);
								var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
								$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
								$('#totalFeeTr').addClass('flagvisibility');
							} else {
								noOfUser = noOfUser - baseUsers;
								var totalFeeLabel = currencyCode + " " + price + " X " + noOfUser + " Users X " + selectedMonths + " Months";
								$('#totalFeeTableUser').find("#totalFeeLabel").html(totalFeeLabel);

								var totalFeeValue = (price * noOfUser * selectedMonths);
								//    		console.log("totalFeeValue :" + totalFeeValue);
								var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
								$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
								$('#totalFeeTr').removeClass('flagvisibility');
							}
							totalFeeValue = (baseFeeValue !== undefined ? baseFeeValue : 0) + totalFeeValue;
						} else {

							var totalFeeLabel = currencyCode + " " + price + " X " + noOfUser + " Users X " + selectedMonths + " Months";
							$('#totalFeeTableUser').find("#totalFeeLabel").html(totalFeeLabel);

							var totalFeeValue = (price * noOfUser * selectedMonths);
							//					    	console.log("totalFeeValue :" + totalFeeValue);
							var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
							$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
							$('#totalFeeTr').removeClass('flagvisibility');
						}
						//					    	var totalFeeLabel = currencyCode + " "+ price + " X " + noOfUser + " Users X " + selectedMonths + " Months";
						//					    	$('#totalFeeTableUser').find("#totalFeeLabel").html(totalFeeLabel);
						//					    	
						//					    	var totalFeeValue = (price * noOfUser * selectedMonths);
						//					    	
						//					    	var feeValue = '<input type="hidden" name="feeValue" value="'+totalFeeValue+'" >';
						//					    	$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue +ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
						////					    	

						var totalFeeDiscountLabel = "Subscription Discount " + montlhyDiscount + "%";
						$('#totalFeeTableUser').find("#totalFeeDiscountLabel").html(totalFeeDiscountLabel);

						var totalFeeDiscountValue = (totalFeeValue / 100) * montlhyDiscount;
						var feeDiscountValue = '<input type="hidden" name="feeDiscountValue" value="' + totalFeeDiscountValue + '" >';
						$('#totalFeeTableUser').find("#totalFeeDiscountValue").html(feeDiscountValue + "-" + ReplaceNumberWithCommas((totalFeeDiscountValue).toFixed(2)));

						var totalFeePromoValue = 0;
						var totalFeePromoLabel = data.promoName;
						//	console.log(data.promoDiscount);
						if (data.discountType !== 'undefined' && data.discountType === 'PERCENTAGE') {
							totalFeePromoValue = (totalFeeValue / 100) * data.promoDiscount;
							totalFeePromoLabel += "-" + data.promoDiscount + " % OFF";
						} else {
							totalFeePromoValue = data.promoDiscount;
							totalFeePromoLabel += "- " + currencyCode + " " + data.promoDiscount + " OFF";
						}
						$('#totalFeeTableUser').find('#totalFeePromoLabel').html(totalFeePromoLabel);
						var promoCodeDiscount = '<input type="hidden" name="promoCodeDiscount" value="' + totalFeePromoValue + '" >';
						$('#totalFeeTableUser').find('#totalFeePromoValue').html(promoCodeDiscount + "-" + ReplaceNumberWithCommas((totalFeePromoValue).toFixed(2)));

						var totalFeeAmount = (totalFeeValue - totalFeeDiscountValue) - totalFeePromoValue;

						var totalTax = (totalFeeAmount * taxFormt) / 100;
						totalFeeAmount = totalFeeAmount + totalTax;
						var totalFeeAmountValue = '<input type="hidden" name="totalFeeAmount" value="' + totalFeeAmount + '" >';
						$('#totalFeeTableUser').find("#tax").html(ReplaceNumberWithCommas((totalTax).toFixed(2)));
						$('#totalFeeTableUser').find('#totalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));

					}
				});

			},
			error: function (request, textStatus, errorThrown) {

				$('#userRangeTable tr').each(function () {
					var rangeId = $(this).children('td:first').attr('data-id');
					var start = parseInt($(this).children('td:first').attr('data-start'));
					var end = parseInt($(this).children('td:first').attr('data-end'));
					var price = parseInt($(this).children('td:first').attr('data-price'));
					var basePrice = $('#basePrice').val();
					var baseUsers = $('#baseUsers').val();
					//   console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfUser :"+ noOfUser);

					if (noOfUser >= start && noOfUser <= end) {
						//					    	console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfUser1 :"+ noOfUser);

						$('#rangeUserId').val(rangeId);


						var baseFeeValue = 0;
						if (basePrice !== undefined) {
							//		console.log("baseprice ppppp :" +basePrice);
							basePrice = parseInt($('#basePrice').val());
							//					    		$('#totalFeeTableUser tr').eq(-1).before("<tr><td>new row</td></tr>")
							var baseFeeLabel = currencyCode + " " + basePrice + " for " + baseUsers + " Users X " + selectedMonths + " Months";
							$('#totalFeeTableUser').find("#baseFeeLabel").html(baseFeeLabel);

							baseFeeValue = (basePrice * selectedMonths);
							//  	console.log("baseFeeValue :" + baseFeeValue);
							$('#totalFeeTableUser').find("#baseFeeValue").html(ReplaceNumberWithCommas((baseFeeValue).toFixed(2)));
							$('#baseFeeTr').removeClass('flagvisibility');

							if (baseUsers >= noOfUser) {
								var totalFeeValue = (price * noOfUser * selectedMonths);
								//		console.log("totalFeeValue :" + totalFeeValue);
								var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
								$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
								$('#totalFeeTr').addClass('flagvisibility');
							} else {
								noOfUser = noOfUser - baseUsers;
								var totalFeeLabel = currencyCode + " " + price + " X " + noOfUser + " Users X " + selectedMonths + " Months";
								$('#totalFeeTableUser').find("#totalFeeLabel").html(totalFeeLabel);

								var totalFeeValue = (price * noOfUser * selectedMonths);
								//		console.log("totalFeeValue :" + totalFeeValue);
								var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
								$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
								$('#totalFeeTr').removeClass('flagvisibility');
							}
							totalFeeValue = (baseFeeValue !== undefined ? baseFeeValue : 0) + totalFeeValue;
						} else {

							var totalFeeLabel = currencyCode + " " + price + " X " + noOfUser + " Users X " + selectedMonths + " Months";
							$('#totalFeeTableUser').find("#totalFeeLabel").html(totalFeeLabel);

							var totalFeeValue = (price * noOfUser * selectedMonths);
							//					    	console.log("totalFeeValue :" + totalFeeValue);
							var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
							$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
							$('#totalFeeTr').removeClass('flagvisibility');
						}

						//					    	var totalFeeLabel = currencyCode + " "+ price + " X " + noOfUser + " Users X " + selectedMonths + " Months";
						//					    	$('#totalFeeTableUser').find("#totalFeeLabel").html(totalFeeLabel);
						//					    	
						//					    	var totalFeeValue = (price * noOfUser * selectedMonths);
						//					    	var feeValue = '<input type="hidden" name="feeValue" value="'+totalFeeValue+'" >';
						//					    	$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue +ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
						//					    	

						var totalFeeDiscountLabel = "Subscription Discount " + montlhyDiscount + "%";
						$('#totalFeeTableUser').find("#totalFeeDiscountLabel").html(totalFeeDiscountLabel);

						var totalFeeDiscountValue = (totalFeeValue / 100) * montlhyDiscount;
						var feeDiscountValue = '<input type="hidden" name="feeDiscountValue" value="' + totalFeeDiscountValue + '" >';
						$('#totalFeeTableUser').find("#totalFeeDiscountValue").html(feeDiscountValue + "-" + ReplaceNumberWithCommas((totalFeeDiscountValue).toFixed(2)));

						$('#totalFeeTableUser').find('#totalFeePromoLabel').html("No Promotional Code");
						$('#totalFeeTableUser').find('#totalFeePromoValue').html("0.00");

						var totalFeeAmount = (totalFeeValue - totalFeeDiscountValue);

						var totalTax = (totalFeeAmount * taxFormt) / 100;
						totalFeeAmount = totalFeeAmount + totalTax;
						var totalFeeAmountValue = '<input type="hidden" name="totalFeeAmount" value="' + totalFeeAmount + '" >';
						$('#totalFeeTableUser').find("#tax").html(ReplaceNumberWithCommas((totalTax).toFixed(2)));
						$('#totalFeeTableUser').find('#totalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));

					}

					$('#promoErrorUser').html('<span class="help-block form-error">' + request.getResponseHeader('error') + '</span>').addClass('has-error');
				});
			}

		});
	} else {
		$('#userRangeTable tr').each(function () {
			var rangeId = $(this).children('td:first').attr('data-id');
			var start = parseInt($(this).children('td:first').attr('data-start'));
			var end = parseInt($(this).children('td:first').attr('data-end'));
			var price = parseInt($(this).children('td:first').attr('data-price'));
			var basePrice = $('#basePrice').val();
			var baseUsers = $('#baseUsers').val();
			//   console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfUser :"+ noOfUser);

			if (noOfUser >= start && noOfUser <= end) {
				//		    	console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfUser1 :"+ noOfUser);

				$('#rangeUserId').val(rangeId);


				var baseFeeValue = 0;
				if (basePrice !== undefined) {
					//	console.log("baseprice ppppp :" +basePrice);
					basePrice = parseInt($('#basePrice').val());
					//		    		$('#totalFeeTableUser tr').eq(-1).before("<tr><td>new row</td></tr>")
					var baseFeeLabel = currencyCode + " " + basePrice + " for " + baseUsers + " Users X " + selectedMonths + " Months";
					$('#totalFeeTableUser').find("#baseFeeLabel").html(baseFeeLabel);

					baseFeeValue = (basePrice * selectedMonths);
					//   	console.log("baseFeeValue :" + baseFeeValue);
					$('#totalFeeTableUser').find("#baseFeeValue").html(ReplaceNumberWithCommas((baseFeeValue).toFixed(2)));
					$('#baseFeeTr').removeClass('flagvisibility');

					if (baseUsers >= noOfUser) {
						var totalFeeValue = (price * noOfUser * selectedMonths);
						//	    		console.log("totalFeeValue :" + totalFeeValue);
						var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
						$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
						$('#totalFeeTr').addClass('flagvisibility');
					} else {
						noOfUser = noOfUser - baseUsers;
						var totalFeeLabel = currencyCode + " " + price + " X " + noOfUser + " Users X " + selectedMonths + " Months";
						$('#totalFeeTableUser').find("#totalFeeLabel").html(totalFeeLabel);

						var totalFeeValue = (price * noOfUser * selectedMonths);
						//   		console.log("totalFeeValue :" + totalFeeValue);
						var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
						$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
						$('#totalFeeTr').removeClass('flagvisibility');
					}
					totalFeeValue = (baseFeeValue !== undefined ? baseFeeValue : 0) + totalFeeValue;
				} else {

					var totalFeeLabel = currencyCode + " " + price + " X " + noOfUser + " Users X " + selectedMonths + " Months";
					$('#totalFeeTableUser').find("#totalFeeLabel").html(totalFeeLabel);

					var totalFeeValue = (price * noOfUser * selectedMonths);
					//		    	console.log("totalFeeValue :" + totalFeeValue);
					var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
					$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));
					$('#totalFeeTr').removeClass('flagvisibility');
				}

				//		    	var totalFeeLabel = currencyCode + " "+ price + " X " + noOfUser + " Users X " + selectedMonths + " Months";
				//		    	$('#totalFeeTableUser').find("#totalFeeLabel").html(totalFeeLabel);
				//		    	
				//		    	var totalFeeValue = (price * noOfUser * selectedMonths);
				//		    	var feeValue = '<input type="hidden" name="feeValue" value="'+totalFeeValue+'" >';
				//		    	$('#totalFeeTableUser').find("#totalFeeValue").html(feeValue +ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));


				var totalFeeDiscountLabel = "Subscription Discount " + montlhyDiscount + "%";
				$('#totalFeeTableUser').find("#totalFeeDiscountLabel").html(totalFeeDiscountLabel);

				var totalFeeDiscountValue = (totalFeeValue / 100) * montlhyDiscount;
				var feeDiscountValue = '<input type="hidden" name="feeDiscountValue" value="' + totalFeeDiscountValue + '" >';
				$('#totalFeeTableUser').find("#totalFeeDiscountValue").html(feeDiscountValue + "-" + ReplaceNumberWithCommas((totalFeeDiscountValue).toFixed(2)));

				$('#totalFeeTableUser').find("#totalFeePromoLabel").html("No Promotional Code");
				$('#totalFeeTableUser').find("#totalFeePromoValue").html("0.00");

				var totalFeeAmount = (totalFeeValue - totalFeeDiscountValue);

				var totalTax = (totalFeeAmount * taxFormt) / 100;
				totalFeeAmount = totalFeeAmount + totalTax;
				var totalFeeAmountValue = '<input type="hidden" name="totalFeeAmount" value="' + totalFeeAmount + '" >';
				$('#totalFeeTableUser').find("#tax").html(ReplaceNumberWithCommas((totalTax).toFixed(2)));
				$('#totalFeeTableUser').find('#totalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));

			}
		});
	}
}

function ajaxCallOnChangeEventFields(noOfEvent, promoCode, currencyCode) {
	var taxFormt = parseFloat($('#taxFormtEvent').val());
	if (promoCode !== 'undefined' && promoCode !== '') {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var url = getContextPath() + "/buyerSubscription/getPromoCode";
		var changePlan = $('#changePlanId').val();
		if ($('#idSubscribeForm')) {
			planId = $('#idSubscribeForm').attr('action').split('/')[$('#idSubscribeForm').attr('action').split('/').length - 1];
		}
		//	console.log("changePlan : " + changePlan);
		if (changePlan !== 'undefined' && changePlan) {
			url = getContextPath() + "/buyer/billing/getPromoCode";
		}
		$.ajax({
			type: "GET",
			url: url,
			data: {
				promoCode: promoCode,
				plan: planId,
				totalPrice: Number($('input[name=totalFeeAmount]').val())
			},
			beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			complete: function () {
				$('#loading').hide();
			},
			success: function (data) {
				//console.log(data);
				$('#promoErrorEvent').html('').removeClass('has-error');


				$('#eventRangeTable tr').each(function () {
					var rangeId = $(this).children('td:first').attr('data-id');
					var start = parseInt($(this).children('td:first').attr('data-start'));
					var end = parseInt($(this).children('td:first').attr('data-end'));
					var price = parseInt($(this).children('td:first').attr('data-price'));
					//   console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfEvent :"+ noOfEvent);

					if (noOfEvent >= start && noOfEvent <= end) {
						//	    	console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfEvent1 :"+ noOfEvent);

						$('#rangeEventId').val(rangeId);

						$('#promoCodeEventId').val(data.id);

						var totalFeeLabel = currencyCode + " " + price + " X " + noOfEvent + " Events ";
						$('#totalFeeTableEvent').find("#totalFeeLabel").html(totalFeeLabel);
						var totalFeeValue = (price * noOfEvent);
						var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
						$('#totalFeeTableEvent').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));


						var totalFeePromoValue = (totalFeeValue / 100) * data.promoDiscount;
						var totalFeePromoLabel = data.promoName;
						if (data.discountType !== 'undefined' && data.discountType === 'PERCENTAGE') {
							totalFeePromoValue = (totalFeeValue / 100) * data.promoDiscount;
							totalFeePromoLabel += "-" + data.promoDiscount + " % OFF";
						} else {
							totalFeePromoValue = data.promoDiscount;
							totalFeePromoLabel += "- " + currencyCode + " " + data.promoDiscount + " OFF";
						}

						$('#totalFeeTableEvent').find('#totalFeePromoLabel').html(totalFeePromoLabel);

						var promoCodeDiscount = '<input type="hidden" name="promoCodeDiscount" value="' + totalFeePromoValue + '" >';
						$('#totalFeeTableEvent').find('#totalFeePromoValue').html(promoCodeDiscount + "-" + ReplaceNumberWithCommas((totalFeePromoValue).toFixed(2)));

						var totalFeeAmount = (totalFeeValue - totalFeePromoValue);
						var totalTax = (totalFeeAmount * taxFormt) / 100;
						totalFeeAmount = totalFeeAmount + totalTax;
						var totalFeeAmountValue = '<input type="hidden" name="totalFeeAmount" value="' + totalFeeAmount + '" >';
						$('#totalFeeTableEvent').find("#tax").html(ReplaceNumberWithCommas((totalTax).toFixed(2)));
						$('#totalFeeTableEvent').find('#totalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));
					}
				});

			},
			error: function (request, textStatus, errorThrown) {

				$('#eventRangeTable tr').each(function () {
					var rangeId = $(this).children('td:first').attr('data-id');
					var start = parseInt($(this).children('td:first').attr('data-start'));
					var end = parseInt($(this).children('td:first').attr('data-end'));
					var price = parseInt($(this).children('td:first').attr('data-price'));
					//   console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfEvent :"+ noOfEvent);

					if (noOfEvent >= start && noOfEvent <= end) {
						//  	console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfEvent1 :"+ noOfEvent);

						$('#rangeEventId').val(rangeId);

						var totalFeeLabel = currencyCode + " " + price + " X " + noOfEvent + " Events ";
						$('#totalFeeTableEvent').find("#totalFeeLabel").html(totalFeeLabel);
						var totalFeeValue = (price * noOfEvent);
						var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
						$('#totalFeeTableEvent').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));


						$('#totalFeeTableEvent').find('#totalFeePromoLabel').html("No Promotional Code");
						$('#totalFeeTableEvent').find('#totalFeePromoValue').html("0.00");

						var totalFeeAmount = totalFeeValue;
						var totalTax = (totalFeeAmount * taxFormt) / 100;
						totalFeeAmount = totalFeeAmount + totalTax;
						var totalFeeAmountValue = '<input type="hidden" name="totalFeeAmount" value="' + totalFeeAmount + '" >';
						$('#totalFeeTableEvent').find("#tax").html(ReplaceNumberWithCommas((totalTax).toFixed(2)));
						$('#totalFeeTableEvent').find('#totalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));

					}
					$('#promoErrorEvent').html('<span class="help-block form-error">' + request.getResponseHeader('error') + '</span>').addClass('has-error');
				});

			}

		});
	} else {
		$('#eventRangeTable tr').each(function () {
			var rangeId = $(this).children('td:first').attr('data-id');
			var start = parseInt($(this).children('td:first').attr('data-start'));
			var end = parseInt($(this).children('td:first').attr('data-end'));
			var price = parseInt($(this).children('td:first').attr('data-price'));
			//   console.log("start :" + start+ " End :"+ end + " Price :"+ price +" noOfEvent :"+ noOfEvent);

			if (noOfEvent >= start && noOfEvent <= end) {
				//	console.log("start1 :" + start+ " End1 :"+ end + " Price1 :"+ price +" noOfEvent1 :"+ noOfEvent);

				$('#rangeEventId').val(rangeId);

				var totalFeeLabel = currencyCode + " " + price + " X " + noOfEvent + " Events ";
				$('#totalFeeTableEvent').find("#totalFeeLabel").html(totalFeeLabel);
				var totalFeeValue = (price * noOfEvent);
				var feeValue = '<input type="hidden" name="feeValue" value="' + totalFeeValue + '" >';
				$('#totalFeeTableEvent').find("#totalFeeValue").html(feeValue + ReplaceNumberWithCommas((totalFeeValue).toFixed(2)));


				$('#totalFeeTableEvent').find('#totalFeePromoLabel').html("No Promotional Code");
				$('#totalFeeTableEvent').find('#totalFeePromoValue').html("0.00");

				var totalFeeAmount = totalFeeValue;
				var totalTax = (totalFeeAmount * taxFormt) / 100;
				totalFeeAmount = totalFeeAmount + totalTax;
				var totalFeeAmountValue = '<input type="hidden" name="totalFeeAmount" value="' + totalFeeAmount + '" >';
				$('#totalFeeTableEvent').find("#tax").html(ReplaceNumberWithCommas((totalTax).toFixed(2)));
				$('#totalFeeTableEvent').find('#totalFeeAmount').html(totalFeeAmountValue + ReplaceNumberWithCommas((totalFeeAmount).toFixed(2)));

			}
		});

	}
}


function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}


$(document).on("click", ".stripe-payment-btn", function (e) {

	if ($('#idSubscribeForm').isValid()) {
		e.preventDefault();
		$(this).prop('disabled', true);
		$(this).addClass('disabled');
		$('#makePaymentModal').attr('plan-id', $(this).attr('plan-id'));
		$('#makePaymentModal').attr('currency-code', $(this).attr('currency-code'));
		$('#makePaymentModal').attr('mode', $(this).attr('mode'));
		var amount = Number($('input[name=totalFeeAmount]').val());
		if (!amount) {
			amount = $('input[name=totalFeeAmount]').val();
		}
		$('#checkoutCardAmount').html(' ' + $(this).attr('currency-code') + ' ' + Number(amount).toLocaleString(undefined, { maximumFractionDigits: 2, minimumFractionDigits: 2 }));
		$('#checkoutFpxAmount').html(' ' + $(this).attr('currency-code') + ' ' + Number(amount).toLocaleString(undefined, { maximumFractionDigits: 2, minimumFractionDigits: 2 }));
		$('#makePaymentModal').attr('endpoint', $(this).attr('endpoint'));
		if ($(this).attr('currency-code') != 'MYR') {
			$('#tabTwoIdParent').addClass('hidden')
		} else {
			$('#tabTwoIdParent').removeClass('hidden')
		}
		$('#makePaymentModal').modal().show();
		$('#tabOneId').click();
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
	}


});

function payByFpx(header, token, event, fpxBank) {
	var data = {};
	data['id'] = null;
	var url = null;

	var numberUserEvent = ($("input[name=numberUserEvent]").val() ? $("input[name=numberUserEvent]").val() : $("input[name=userQuantity]").val())
	var periodId = ($("input[name=periodId]:checked").val() ? $("input[name=periodId]:checked").val() : "")
	var rangeId = ($("input[name=rangeId]").val() ? $("input[name=rangeId]").val() : $("#rangeUserId").val() ? $("#rangeUserId").val() : "")

	var feeValue = 0;
	if ($("input[name=feeValue]").val()) {
		feeValue = Number($("input[name=feeValue]").val())
	} else if ($("input[name=eventPrice]").val()) {
		feeValue = Number($("input[name=eventPrice]").val())
	}

	var feeDiscountValue = 0;
	if ($("input[name=feeDiscountValue]").val()) {
		feeDiscountValue = Number($("input[name=feeDiscountValue]").val())
	} else if ($("input[name=subscriptionDiscountPrice]").val()) {
		feeDiscountValue = Number($("input[name=subscriptionDiscountPrice]").val())
	}

	url = $('#makePaymentModal').attr('endpoint') +
		'?immediateEffect=' + ($('#immediateEffect').prop('checked') ? $('#immediateEffect').prop('checked') : false) +
		'&numberUserEvent=' + numberUserEvent +
		'&periodId=' + periodId +
		'&rangeId=' + rangeId +
		'&promoCodeId=' + $("input[name=promoCodeId]").val() +
		'&feeValue=' + feeValue +
		'&feeDiscountValue=' + feeDiscountValue +
		'&promoCodeDiscount=' + Number($("input[name=promoCodeDiscount]").val() ? $("input[name=promoCodeDiscount]").val() : 0) +
		'&totalFeeAmount=' + Number($("input[name=totalFeeAmount]").val()) +
		'&mode=' + 'fpx' +
		'&autoChargeSubscription=' + ($("input[id=autoChargeSubscription]").prop('checked') ? $("input[id=autoChargeSubscription]").prop('checked') : false);

	$.ajax({
		type: "POST",
		url: url,
		contentType: "application/json",
		data: JSON.stringify(data),
		dataType: 'json',
		beforeSend: function (xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success: function (data) {
			$('#fpx-button').attr('data-secret', data.clientSecret);
			event.preventDefault();
			var fpxButton = document.getElementById('fpx-button');
			var clientSecret = fpxButton.dataset.secret;
			stripe.confirmFpxPayment(clientSecret, { payment_method: { fpx: fpxBank, }, return_url: window.location.href }).then(function (result) {
				if (result.error) {
					showErrorForPayment(result.error.message);
				}
			});
		},
		error: function (request) {
			$('#makePaymentModalCloseId').click();
			if (request.getResponseHeader('error')) {
				$('div[id=idGlobalError]').hide();
				$('div[id=idGlobalSuccess]').hide();
				showErrorForPayment(request.getResponseHeader('error').split(",").join("<br/>"))
			}
			$('#loading').hide();
		},
		complete: function () {
			$('#loading').hide();
		}
	});
}

function payByCard() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var card = elements.getElement('card');
	var data = {};
	data['id'] = null;

	var url = null;
	var numberUserEvent = ($("input[name=numberUserEvent]").val() ? $("input[name=numberUserEvent]").val() : $("input[name=userQuantity]").val())
	var periodId = ($("input[name=periodId]:checked").val() ? $("input[name=periodId]:checked").val() : null)
	var rangeId = ($("input[name=rangeId]").val() ? $("input[name=rangeId]").val() : $("#rangeUserId").val() ? $("#rangeUserId").val() : null)
	var feeValue = 0;
	if ($("input[name=feeValue]").val()) {
		feeValue = Number($("input[name=feeValue]").val())
	} else if ($("input[name=eventPrice]").val()) {
		feeValue = Number($("input[name=eventPrice]").val())
	}

	var feeDiscountValue = 0;
	if ($("input[name=feeDiscountValue]").val()) {
		feeDiscountValue = Number($("input[name=feeDiscountValue]").val())
	} else if ($("input[name=subscriptionDiscountPrice]").val()) {
		feeDiscountValue = Number($("input[name=subscriptionDiscountPrice]").val())
	}

	url = $('#makePaymentModal').attr('endpoint') +
		'?immediateEffect=' + ($('#immediateEffect').prop('checked') ? $('#immediateEffect').prop('checked') : false) +
		'&numberUserEvent=' + numberUserEvent +
		'&periodId=' + periodId +
		'&rangeId=' + rangeId +
		'&promoCodeId=' + $("input[name=promoCodeId]").val() +
		'&feeValue=' + feeValue +
		'&feeDiscountValue=' + feeDiscountValue +
		'&promoCodeDiscount=' + Number($("input[name=promoDiscountPrice]").val() ? $("input[name=promoDiscountPrice]").val() : 0) +
		'&totalFeeAmount=' + Number($("input[name=totalFeeAmount]").val()) +
		'&mode=' + 'card' +
		'&autoChargeSubscription=' + ($("input[id=autoChargeSubscription]").prop('checked') ? $("input[id=autoChargeSubscription]").prop('checked') : false);


	$.ajax({
		type: "POST",
		url: url,
		contentType: "application/json",
		data: JSON.stringify(data),
		dataType: 'json',
		beforeSend: function (xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success: function (data) {
			stripe.confirmCardPayment(data.clientSecret, { payment_method: { card: card } }).then(function (result) {
				if (result.error) {
					$('div[id=idGlobalError]').hide();
					$('div[id=idGlobalSuccess]').hide();
					showErrorForPayment(result.error.message);
				} else {
					$('#loading').show();
					var location = window.location.href;
					location += '?&payment_intent=' + result.paymentIntent.id;
					window.location.href = location;
				}
			});
		},
		error: function (request) {
			$('#makePaymentModalCloseId').click();
			if (request.getResponseHeader('error')) {
				showErrorForPayment(request.getResponseHeader('error').split(",").join("<br/>"))
			}
			$('#loading').hide();
		},
		complete: function () {
			$('#loading').hide();
		}
	});
}

function showErrorForPayment(errorMsgText) {
	$('#payByCardId').disabled = true;
	$('#payByCardId').addClass('disabled');
	$('#payByCardId').removeClass('btn-success');
	$('#fpx-button').disabled = true;
	$('#fpx-button').addClass('disabled');
	$('#fpx-button').removeClass('btn-success');
	$('#makePaymentModalCloseId').click();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var fpxBank = elements ? elements.getElement('fpxBank') : '';
	var card = elements ? elements.getElement('card') : '';
	card ? card.clear() : '';
	fpxBank ? fpxBank.clear() : '';
	$('p[id=idGlobalErrorMessage]').html(errorMsgText);
	$('div[id=idGlobalError]').show();
	document.getElementById("idGlobalError").scrollIntoView({ behavior: 'smooth', block: 'center' })

}

$('#makePaymentModal').on('hide.bs.modal', function (e) {
	$('#tabTwoIdParent').removeClass('hidden')
	$('#pay_' + $(this).attr('plan-id')).prop('disabled', false);
	$('#pay_' + $(this).attr('plan-id')).removeClass('disabled');
	var errorMsg = document.querySelector(".sr-field-error");
	errorMsg.textContent = '';
	var fpxBank = elements.getElement('fpxBank');
	var card = elements.getElement('card');
	card ? card.clear() : '';
	fpxBank ? fpxBank.clear() : '';
	$('#pay_' + $(this).attr('plan-id')).prop('disabled', false);
	$('#pay_' + $(this).attr('plan-id')).removeClass('disabled');
});

$('#tabOneId').on('shown.bs.tab', function (e) {
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var card = elements.getElement('card');
	$('#payByCardId').disabled = true;
	$('#payByCardId').addClass('disabled');
	$('#payByCardId').removeClass('btn-success');
	$('#makePaymentModal').attr('payment-mode', "card")
	var style = {
		base: {
			color: "#32325d",
			fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
			fontSmoothing: "antialiased",
			fontSize: "16px",
			"::placeholder": {
				color: "#aab7c4"
			}
		},
		invalid: {
			color: "#fa755a",
			iconColor: "#fa755a"
		}
	};
	card = elements.create("card", { style: style, hidePostalCode: true });
	card.mount("#card-element");
	card.on('change', function (event) {

		if (event.error) {
			$('#payByCardId').disabled = true;
			$('#payByCardId').removeClass('btn-success');
			$('#payByCardId').addClass('disabled');
		}

		if (event.complete) {
			$('#payByCardId').disabled = false;
			$('#payByCardId').removeClass('disabled');
			$('#payByCardId').addClass('btn-success');
		}
		var displayError = document.getElementById('card-errors');
		if (event.error) {
			displayError.textContent = event.error.message;
		} else {
			displayError.textContent = '';
		}
	});

});

$('#tabOneId').on('hidden.bs.tab', function (e) {
	var card = elements.getElement('card');
	card ? card.destroy() : '';
	document.getElementById('card-errors').textContent = '';
});

$('#tabTwoId').on('shown.bs.tab', function (e) {
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var fpxBank = elements.getElement('fpxBank');
	$('#loading').show();
	$('#fpx-button').disabled = true;
	$('#fpx-button').addClass('disabled');
	$('#fpx-button').removeClass('btn-success');
	var style = { base: { padding: '10px 12px', color: '#32325d', fontSize: '16px', } };
	fpxBank = elements.create('fpxBank', { style: style, accountHolderType: 'individual' });
	e.preventDefault();
	$('#makePaymentModal').attr('payment-mode', "fpx")
	fpxBank.mount('#fpx-bank-element');
	fpxBank.on('change', function (event) {
		$('#loading').hide();
		if (event.error) {
			$('#loading').hide();
			showErrorForPayment(event.error)
		}
		if (event.complete) {
			$('#fpx-button').disabled = false;
			$('#fpx-button').removeClass('disabled');
			$('#fpx-button').addClass('btn-success');
		}
	});
});

$('#tabTwoId').on('hidden.bs.tab', function (e) {
	var fpxBank = elements.getElement('fpxBank');
	fpxBank ? fpxBank.destroy() : '';
});

$(document).on("click", "#payByCardId", function (e) {
	payByCard();
});

