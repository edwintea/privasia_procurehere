$('.supplierSelect').change(
		function() {
			var supplierId = $('.supplierSelect').val();

			/*$(".yehSupplierHai").each(function(){
				alert($(this));
				$(this).val(supplierId).trigger("chosen:updated");
			});*/
			

			if (supplierId == '') {
				$('.price-total').addClass('flagvisibility');
				$('.padDetailsDiv').show();
				return;
			} else {
				$('.padDetailsDiv').hide();
			}

			var eventId = $("#eventId").val();
			var bqId = $("#bqId").val();
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var ajaxUrl = getContextPath() + '/buyer/getBqofSelectSupplier';
			var bqData = {
				'eventId' : eventId,
				'supplierId' : supplierId,
				'bqId' : bqId,
			};
			$.ajax({
				url : ajaxUrl,
				type : "POST",
				data : bqData,
				beforeSend : function(xhr) {
					$('#loading').show();
					xhr.setRequestHeader(header, token);
				},
				success : function(data, textStatus, request) {
					var info = request.getResponseHeader('success');
					var html = '';
					console.log(data);
					$.each(data, function(i, item) {
						console.log(item);
						html += '<tr><th>' + item.level + '.' + item.order + '&nbsp;&nbsp;' + item.itemName + ' : </th><th class="align-right"></th></tr>';
						$.each(item.children, function(i, child) {
							html += '<tr><td>' + child.level + '.' + child.order + '&nbsp;&nbsp;' + child.itemName + ' : </td><td class="align-right">' + currencyCode + ' '
									+ ReplaceNumberWithCommas(child.totalAmountWithTax.toFixed($('#eventDecimal').val())) + '</td></tr>';

						});

						$('.price-total').removeClass('flagvisibility');
						$('#amountAfterTax').text(currencyCode + ' ' + ReplaceNumberWithCommas(item.supplierBq.totalAfterTax.toFixed($('#eventDecimal').val())));
						$('#bottomAmountAfterTax').text(currencyCode + ' ' + ReplaceNumberWithCommas(item.supplierBq.totalAfterTax.toFixed($('#eventDecimal').val())));
						$('#totalBqSupplier').text(currencyCode + ' ' + ReplaceNumberWithCommas(item.supplierBq.totalAfterTax.toFixed($('#eventDecimal').val())));
					});
					// html += '<tr><th>&nbsp;&nbsp;Total : </th><th class="align-right"></th></tr>';
					console.log(html);
					$('.bqBiddingValue > table').html(html);
					// alert("success : ");
					
					$('#loading').hide();

				},
				error : function(request, textStatus, errorThrown) {
					console.log(request.responseText);
				},
				complete : function() {
					$('#loading').hide();
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
		});

$('#idSelectBidders').change(function() {
	reloadBidderList();
});

function reloadBidderList() {
	if (typeof suppliersListConsole !== 'undefined') {
		suppliersListConsole.ajax.reload();
		if (typeof resetExpiry === "function") {
			resetExpiry();
		}
	}
}

$('#idSuspendEvent').click(function() {
	var eventId = $("#eventId").val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getContextPath() + '/buyer/suspendEnglishAuction';
	var suspendData = {
		'eventId' : eventId,
	};
	$.ajax({
		url : ajaxUrl,
		type : "POST",
		data : suspendData,
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success : function(data, textStatus, request) {
			var info = request.getResponseHeader('success');
			var html = '';
			window.location.href = getContextPath() + '/buyer/buyerDashboard';
			$('#loading').hide();

		},
		error : function(request, textStatus, errorThrown) {
			console.log(request.responseText);
		},
		complete : function() {
			$('#loading').hide();
		}
	});

});

$('.auctionBidSupplier1').change(function() {
	var supplierId = $('.auctionBidSupplier').val();
	var eventId = $("#eventId").val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getContextPath() + '/buyer/getAuctionBidsOfSuppliers/' + eventId + '/' + supplierId;
	$.ajax({
		url : ajaxUrl,
		type : "POST",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success : function(data, textStatus, request) {
			var info = request.getResponseHeader('success');
			var html = '';
			console.log(data);
			$.each(data, function(i, item) {
				console.log(item);
				html += '<tr>';
				if (item.rankForBid === undefined || item.rankForBid === '') {
					html += '<td>&nbsp</td>';
				} else {
					html += '<td>' + item.rankForBid + '</td>';
				}
				html += '<td class="align-right">' + item.amount + '</td><td class="align-right">' + item.bidSubmissionDate + '</td><td><input type="button" value="Revert to this bid"></td><tr>';
			});
			console.log(html);
			$('.auctionbids > table > tbody ').html(html);
			$('#loading').hide();

		},
		error : function(request, textStatus, errorThrown) {
			console.log(request.responseText);
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

$('.refreshAuctionBids').click(function() {
	fetchSupplierAuctionBids();
});

$('.auctionBidSupplier').change(function() {
	fetchSupplierAuctionBids();
});

$(document).delegate('.revertBid', 'click', function(e) {
	var supplierId = $('.auctionBidSupplier').val();
	var eventId = $("#eventId").val();
	var auctionBidId = $(this).attr('data-id');
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var ajaxUrl = getContextPath() + '/buyer/revertOnAuctionBid/' + eventId + '/' + supplierId;
	var auctionBidId = {
		'auctionBidId' : auctionBidId,
	};
	$.ajax({
		url : ajaxUrl,
		type : "POST",
		data : auctionBidId,
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success : function(data, textStatus, request) {
			var info = request.getResponseHeader('success');
			$('#loading').hide();
			if (request.getResponseHeader('success')) {
				var success = request.getResponseHeader('success');
				$.jGrowl(success, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-green'
				});
			}
			window.location.href = getContextPath() + '/buyer/englishAuctionConsole/' + eventId;
		},
		error : function(request, textStatus, errorThrown) {
			console.log(request.responseText);
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

jQuery(document).ready(function() {
	selectSupplier();
	// for hide div on select on timeExtensionType
	$('.selectSupp').on('change', function() {
		selectSupplier();
	});
});

function selectSupplier() {
	if ($('.selectSupp').val() == '') {
		$(".hideDiv").hide();
		$("#bidsThead").hide();
		$(".padDiv").show();
	} else {
		$(".hideDiv").show();
		$("#bidsThead").show();
		$(".padDiv").hide();
	}
}