$(document).ready(
		
		function() {

			$(".side-sticky-offer").click(function() {
				$(this).addClass("offer-open");
			});

			/*
			 * $('.mega').on('scroll', function() { $('.header').css('top', $(this).scrollTop()); });
			 */

			$("#timer-accord").click(function() {
				$("#timer-accord").toggleClass("small-accordin-tab");
			});

			var finalDate = $('#main-example1').attr('data-date');
			var eventId = $('#eventId').val();
			$(".side-sticky-offer").addClass("offer-open");
			$("#current_step").val(1);
			console.log('Event Date : ' + finalDate)
			$("#countdown1").countdown(finalDate).on(
					'update.countdown',
					function(event) {
						$(this).html(
								'<span class="days"><b>Days</b><span style="margin:0;padding:0;">' + event.strftime('%D') + '</span></span><span class="hours"><b>Hours</b><span style="margin:0;padding:0;">' + event.strftime('%H')
										+ '</span></span><span class="minutes"><b>Minutes</b><span style="margin:0;padding:0;">' + event.strftime('%M') + '</span></span><span class="seconds"><b>Seconds</b><span style="margin:0;padding:0;">'
										+ event.strftime('%S') + '</span></span>');

					}).on('finish.countdown', function(event) {
				// window.location.reload(); -- wrong logic... relook at this @Nitin Otageri
				window.location.href = getContextPath() + "/auction/dutchAuctionConsole/" + eventId;

			});

		});

function hideThis(e) {
	e.stopPropagation();
	$(".side-sticky-offer").removeClass("offer-open");
}

$(document).on("click", "#modalYes", function(e) {
	e.preventDefault();

	var auctionId = $("#auctionId").val();
	var currentStep = $("#currentStep").val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var currentStep = {
		'currentStep' : currentStep
	}
	$('#loading').show();
	var ajaxUrl = getContextPath() + "/auction/submitDutchAuction/" + auctionId;
	$.ajax({
		url : ajaxUrl,
		data : currentStep,
		type : "POST",
		beforeSend : function(xhr) {
			console.log(currentStep);
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success : function(data) {
			console.log(data);
			var info = 'Success';
			$.jGrowl(info, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-green'
			});

		},
		error : function(request, textStatus, errorThrown) {
			console.log("ERROR :  " + request.getResponseHeader('error'));
			if (request.getResponseHeader('error')) {
				var info = request.getResponseHeader('error').split(",").join("<br/>");
				$.jGrowl(info, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-red'
				});
			}
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
			$('#addEditContactPopup').modal('hide');
			// $("#" + buttonId).prop("disabled", false);
		}
	});
});

var startPolling = Boolean($("#startPolling").val() == 'true');
var auctionId = $("#auctionId").val();
var eventStatus = $("#eventStatus").val();
var header = $("meta[name='_csrf_header']").attr("content");
var token = $("meta[name='_csrf']").attr("content");
var eventId = $('#eventId').val();
// $('#loading').show();
var ajaxUrl = getContextPath() + '/auction/refreshDutchAuctionConsole/' + auctionId;
function auctionPoll() {
	startPolling = Boolean($("#startPolling").val() == 'true');
	if (!startPolling)
		return;
	setTimeout(function() {
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			beforeSend : function(xhr) {
				// $('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success : function(data) {
				console.log(data);

				var i = data.currentStepNo;
				if ($('*[data-timestamp="' + i + '"]').attr('class') != undefined) {
					//currentStep
					$('#currentStep').val(i);
					var offTop = $('*[data-timestamp="' + i + '"]').offset().top - 300;
					// $(document).scrollTop(offTop);

					$('.timestamp .active').hide();
					$('.timestamp .inactive').show();
					$('.timestamp').addClass('time-ammount-contant');
					$('.timestamp').removeClass('time-ammount-heading green-with-border').addClass('time-ammount-contant');
					$('*[data-timestamp="' + i + '"]').addClass('time-ammount-heading green-with-border').removeClass('time-ammount-contant').find('.active').show();
					$('*[data-timestamp="' + i + '"]').find('.inactive').hide();
					i++;
					$("#current_step").val(i);

				}
				var status = data.currentStatus;
				var myStatus = 'ACTIVE';
				if (status != myStatus) {
					//window.location.href = getContextPath() + "/auction/dutchAuctionConsole/" + eventId;
				}

			},
			error : function(request, textStatus, errorThrown) {
				console.log("ERROR :  " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					var errorMsg = request.getResponseHeader('error').split(",").join("<br/>");
					showMessage('ERROR', errorMsg);
				}
				// $('#loading').hide();
			},
			complete : function() {
				if (startPolling) {
					auctionPoll();
				}

				// $('#loading').hide();
				// $('#addEditContactPopup').modal('hide');
				// $("#" + buttonId).prop("disabled", false);
			}
		});
	}, 2000);
}
$(document).ready(function() {
	startPolling = Boolean($("#startPolling").val() == 'true');
	if (startPolling) {
		auctionPoll();
	}
});

$(document).on("click", "#modalYes", function(e) {
	e.preventDefault();

	var auctionId = $("#auctionId").val();
	var currentStep = $("#currentStep").val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var currentStep = {
		'currentStep' : currentStep
	}
	$('#loading').show();
	var ajaxUrl = getContextPath() + "/auction/submitDutchAuction/" + auctionId;
	$.ajax({
		url : ajaxUrl,
		data : currentStep,
		type : "POST",
		beforeSend : function(xhr) {
			console.log(currentStep);
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success : function(data) {
			/*
			 * console.log(data); var info = 'Success'; $.jGrowl(info, { sticky : false, position : 'top-right', theme : 'bg-green' });
			 */
			$("#startPolling").val(startPolling);
			window.location.href = getContextPath() + "/auction/dutchAuctionConsole/" + data;
		},
		error : function(request, textStatus, errorThrown) {
			console.log("ERROR :  " + request.getResponseHeader('error'));
			if (request.getResponseHeader('error')) {
				var info = request.getResponseHeader('error').split(",").join("<br/>");
				$.jGrowl(info, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-red'
				});
			}
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
			$('#addEditContactPopup').modal('hide');
			// $("#" + buttonId).prop("disabled", false);
		}
	});
});

$(document).on("click", "#idSuspendEvent", function(e) {
	e.preventDefault();

	var auctionId = $("#auctionId").val();
	var currentStep = $("#currentStep").val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var currentStepData = {
		'currentStep' : currentStep
	}
	$('#loading').show();
	var ajaxUrl = getContextPath() + "/auction/suspendDutchAuction/" + auctionId;
	$.ajax({
		url : ajaxUrl,
		data : currentStepData,
		type : "POST",
		beforeSend : function(xhr) {
			console.log(currentStep);
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success : function(data) {
			console.log(data);
			var info = 'Success';
			$.jGrowl(info, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-green'
			});
			$("#startPolling").val(startPolling);
			window.location.href = getContextPath() + "/auction/dutchAuctionConsole/" + data;
		},
		error : function(request, textStatus, errorThrown) {
			console.log("ERROR :  " + request.getResponseHeader('error'));
			if (request.getResponseHeader('error')) {
				var info = request.getResponseHeader('error').split(",").join("<br/>");
				$.jGrowl(info, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-red'
				});
			}
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
			$('#addEditContactPopup').modal('hide');
			// $("#" + buttonId).prop("disabled", false);
		}
	});
});

$(document).ready(function() {
	$('#refreshEvent').click(function() {
		var eventId = $('#eventId').val();
		window.location.href = getContextPath() + "/auction/dutchAuctionConsole/" + eventId;
	});
});
