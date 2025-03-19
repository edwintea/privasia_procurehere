$(function() {
	priviousTemplatesBlocksEvent();
	$('.nav-tabs a.createEventsTopTabs').on('hidden.bs.tab', function(e) {
		priviousTemplatesBlocksEvent();
	});
	$('.searchrftEvent1hgg').click(
			function(event) {
				
				
				
				event.preventDefault();
				$("#idGlobalInfo").hide();
				$("#idGlobalError").hide();
				$("#idGlobalWarn").hide();
				$("#idEventInfo").hide();
				// var referenceNumber = $('#idRefNumber').val();
				var searchValue = $('#idEventName').val();
				var industryCategory = $('#chosenCategoryAll').val();
				var eventType = $('#eventTypeSearch').val();
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				$.ajax({
					type : "POST",
					url : getContextPath() + "/buyer/searchEvent",
					data : {
						// referenceNumber : referenceNumber,
						searchValue : searchValue,
						industryCategory : industryCategory,
						eventType : eventType
					},
					dataType : "json",
					beforeSend : function(xhr) {
						$('#loading').show();
						xhr.setRequestHeader(header, token);
					},
					complete : function() {
						$('#loading').hide();
					},
					success : function(data) {
						var html = '';
						var found = false;
						$.each(data, function(key, value) {
							found = true;
							html += '<div class="col-md-3 marg-bottom-10 idRftEvent" id="' + value.id + '" data-value="' + value.id + '" style="display: block">' + '<div class="lower-bar-search-contant-main-block" id="test" style="min-height: 331px;">'
									+ '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10"> <div style="display: flex;justify-content: space-between;"> <div>' + '<h4>' + (value.eventId ? value.eventId : '')
									+ '</h4> </div><div><h4>' + (value.status ? value.status : '') + '</h4></div> </div></div>' + '<div class="pad-top-side-5">' + '<label>Event Name :</label> <span class="green">'
									+ (value.eventName ? value.eventName : '') + '</span> </div>' + '<div class="pad-top-side-5">' + '<label>Reference Number :</label> <span class="green">' + (value.referanceNumber ? value.referanceNumber : '')
									+ '</span> </div>' + '<div class="pad-top-side-5">' + '<label>Category :</label> <span> ' + (value.industryCategory ? value.industryCategory.name : '') + '</span></div>' + '<div class="pad-top-side-10">'
									+ '<label>Start Date : </label> <span>' + (value.eventStart ? value.eventStart : '') + '</span></div>' + '<div class="pad-top-side-5">' + '<label>End Date :</label> <span>' + (value.eventEnd ? value.eventEnd : '')
									+ '</span></div>';
							if (value.rfaAuctionType) {
								html += '<div class="pad-top-side-5">'

								html += '<label>Auction Type :</label> <span class="green">' + (value.rfaAuctionType ? value.rfaAuctionType : '') + '</span> </div>'
							}
							html += '<div class="col-md-12 pad_all_10" style="position: absolute; bottom: 0 ;"><div>' + '<form action="' + getContextPath() + '/buyer/copyFrom" class="hover_tooltip-top col-md-12" method="post" style="float: right;">'
									+ '<input type="hidden" id="eventType" value="' + eventType + '" name="eventType">' + '<input type="hidden" id="eventId" value="' + value.id + '" name="eventId">' + '<input type="hidden" id="_csrf" value="'
									+ token + '" name="_csrf">';
							if (value.templateActive) {
								html += '<button disabled class="btn btn-black btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This </button>';
								html += '<span class="tooltiptext-top"> Not able to copy due to template is inactive </span>';
							} else {
								html += '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>';
								html += '<span class="tooltiptext-top"> Create New Event</span>';
							}

							html += '</form>' + '</div></div></div></div>';
						});
						$('#rftEvents').show();
						$('#rftEvents > .row').html(html);
						priviousTemplatesBlocksEvent();
						if (!found) {
							$("#idEventInfoMessage").html('No matching data found');
							$("#idEventInfo").show();
							$('#rftEvents').hide();
						}
					},
					error : function(request, textStatus, errorThrown) {
						if (request.getResponseHeader('error')) {
							$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
							$("#idGlobalError").show();
						}
						if (request.getResponseHeader('info')) {
							$("#idGlobalInfoMessage").html(request.getResponseHeader('info').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
							$("#idGlobalInfo").show();
						}
					}
				});
			});

	$(function() {
		$('.chosenCategoryAllll').change(
				function(event) {
					event.preventDefault();

					$("#idGlobalInfo").hide();
					$("#idGlobalError").hide();
					$("#idGlobalWarn").hide();
					$("#idEventInfo").hide();

					// var referenceNumber = $('#idRefNumber').val();
					var searchValue = $('#idEventName').val();
					var industryCategory = $('#chosenCategoryAll').val();
					var eventType = $('#eventTypeSearch').val();
					var header = $("meta[name='_csrf_header']").attr("content");
					var token = $("meta[name='_csrf']").attr("content");
					$.ajax({
						type : "POST",
						url : getContextPath() + "/buyer/searchEvent",
						data : {
							// referenceNumber : referenceNumber,
							searchValue : searchValue,
							industryCategory : industryCategory,
							eventType : eventType
						},
						dataType : "json",
						beforeSend : function(xhr) {
							$('#loading').show();
							xhr.setRequestHeader(header, token);
						},
						complete : function() {
							$('#loading').hide();
						},
						success : function(data) {
							var html = '';
							var found = false;
							$.each(data, function(key, value) {
								found = true;
								html += '<div class="col-md-3 marg-bottom-10 idRftEvent " id="' + value.id + '" data-value="' + value.id + '" style="display: block">' + '<div class="lower-bar-search-contant-main-block" id="test" style="min-height: 331px;">'
										+ '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10"> <div style="display: flex;justify-content: space-between;"> <div>' + '<h4>' + (value.eventId ? value.eventId : '')
										+ '</h4> </div><div><h4>' + (value.status ? value.status : '') + '</h4></div> </div></div>'

										+ '<div class="lower-bar-search-contant-main-contant pad-top-side-5">' + '<label>Event Name :</label> <span class="green">' + (value.eventName ? value.eventName : '') + '</span> </div>'

										+ '<div class="lower-bar-search-contant-main-contant pad-top-side-5">' + '<label>Reference Number :</label> <span class="green">' + (value.referanceNumber ? value.referanceNumber : '') + '</span> </div>'
										+ '<div class="lower-bar-search-contant-main-contant pad-top-side-5">' + '<label>Event Category :</label> <span> ' + (value.industryCategory ? value.industryCategory.name : '') + '</span></div>'
										+ '<div class="lower-bar-search-contant-main-contant pad-top-side-10">' + '<label>Event Start Date : </label> <span>' + (value.eventStart ? value.eventStart : '') + '</span></div>'
										+ '<div class="lower-bar-search-contant-main-contant pad-top-side-5">' + '<label>Event End Date :</label> <span>' + (value.eventEnd ? value.eventEnd : '') + '</span></div>'
								if (value.rfaAuctionType) {
									html += '<div class="lower-bar-search-contant-main-contant  pad_all_10">'

									html += '<label>Auction Type :</label> <span class="green">' + (value.rfaAuctionType ? value.rfaAuctionType : '') + '</span> </div>'
								}
								html +='<div class="lower-bar-search-contant-main-contant  pad_all_10" style="position: absolute; bottom: 0;"><div>' + '<form action="' + getContextPath() + '/buyer/copyFrom" class="col-md-12" method="post" style="float: right;">'
										+ '<input type="hidden" id="eventType" value="' + eventType + '" name="eventType">' + '<input type="hidden" id="eventId" value="' + value.id + '" name="eventId">' + '<input type="hidden" id="_csrf" value="'
										+ token + '" name="_csrf">' + '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>' + '</form>' + '</div></div></div></div>';
							});
							$('#rftEvents').show();
							$('#rftEvents > .row').html(html);
							priviousTemplatesBlocksEvent();
							if (!found) {
								$("#idEventInfoMessage").html('No matching data found');
								$("#idEventInfo").show();
								$('#rftEvents').hide();
							}
						},
						error : function(request, textStatus, errorThrown) {
							if (request.getResponseHeader('error')) {
								$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
								$("#idGlobalError").show();
							}
							if (request.getResponseHeader('info')) {
								$("#idGlobalInfoMessage").html(request.getResponseHeader('info').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
								$("#idGlobalInfo").show();
							}
						}
					});
				});

	});

	$(document).delegate('.quickview ', 'click', function(e) {

		diag_id = $(this).data("qv");

		$("#" + diag_id).dialog({
			modal : true,
			minWidth : 300,
			width : '90%',
			maxWidth : 600,
			minHeight : 200,
			dialogClass : "",
			show : "fadeIn",
			draggable : false,
			dialogClass : "dialogBlockLoaded"
		});
	});

	$(document).on("keyup", "#chosenCategoryAll_chosen .chosen-search input", function() {
		var industryCat = $.trim($(this).val());
		var industryCatOrig = $(this).val();
		var currentSearchBlk = $(this);
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		if (industryCat.length > 2) {
			$.ajax({
				url : getContextPath() + '/buyer/searchCategory',
				data : {
					'search' : industryCat
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data) {
					var html = '<option value="">All Categories</option>';
					if (data != '' && data != null && data.length > 0) {
						$.each(data, function(key, value) {
							html += '<option value="' + value.id + '">' + value.code + ' - ' + value.name + '</option>';
						});
					}
					$('#chosenCategoryAll').html(html);
					$("#chosenCategoryAll").trigger("chosen:updated");
					currentSearchBlk.val(industryCatOrig);
				},
				error : function(error) {
					console.log(error);
				},
				complete : function() {
					$('#loading').hide();
				}

			});
		}
	});

	$('.searchRftTemplateNHH').click(
			function(event) {
				event.preventDefault();

				$("#idGlobalInfo").hide();
				$("#idTemplateInfo").hide();
				$("#idGlobalError").hide();
				$("#idGlobalWarn").hide();

				var templateName = $('#idTemplateName').val();
				var eventType = $('#eventTypeSearch').val();
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				$.ajax({
					type : "POST",
					url : getContextPath() + "/buyer/searchTemplate",
					data : {
						templateName : templateName,
						eventType : eventType
					},
					dataType : "json",
					beforeSend : function(xhr) {
						$('#loading').show();
						xhr.setRequestHeader(header, token);
					},
					complete : function() {
						$('#loading').hide();
					},
					success : function(data) {
						var html = '';
						// html += '<div class="col-md-3">';
						// html += '<div class="previous-box blank-div idRftEventsHgt">';
						// html += '<a href="' + getContextPath() + '/buyer/navigateEvent/' + getEventType() + '">Create from blank</a>';
						// html += '</div>';
						// html += '</div>';
						var found = false;
						$.each(data, function(key, value) {
							found = true;
							html += '<div class="col-md-3 marg-bottom-10 idRftEvent " id="' + value.id + '" data-value="' + value.id + '" style="display: block">'
									+ '<div class="lower-bar-search-contant-main-block" id="test" style="height: auto;">'
									+ '<div class="lower-bar-search-contant-main-block-heading light-gray-bg pad_all_10 disp-f">'
									+ '<h4 class="ellip-title" title="' + value.templateName + '">' + value.templateName + '</h4>'
									+ '</div>'
									+ '<div class="lower-bar-search-contant-main-contant pad-top-side-5 disp-f h-30">'
									+ '<div><label style="width: 83px;">Description :</label></div>'
									+ '<div><span data-toggle="tooltip" data-original-title=" ' + value.templateDescription + ' " class="green ellip-desc">' + ( value.templateDescription == undefined ? '' : value.templateDescription ) + '</span></div>'
									+ '</div>' 
									+ '<div class="lower-bar-search-contant-main-contant pad-top-side-5">'
									+ '<label>Created By :</label> <span class="green">' + (value.createdBy ? value.createdBy.name : '') + '</span></div>' + '<div class="lower-bar-search-contant-main-contant pad-top-side-5">'
									+ '<label>Created Date :</label> <span class="green">' + value.createdDate + '</span> </div>' + '<div class="lower-bar-search-contant-main-contant  pad_all_10"><div>' + '<form action="' + getContextPath()
									+ '/buyer/copyFromTemplate" class="col-md-12" method="post" style="float: right;">' + '<input type="hidden" id="eventType" value="' + eventType + '" name="eventType"> <input type="hidden" id="templateId" value="'
									+ value.id + '" name="templateId">' + '<input type="hidden" id="_csrf" value="' + token + '" name="_csrf">'
									+ '<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out" style="width: 100%" type="submit">Use This</button>' + '</form></div>';
							// '<div>'
							// + '<button class="btn btn-black for-form-back hvr-pop hvr-rectangle-out1" type="submit">Quick View</button>' +
							// '</div>';
							//									
							html += '</div></div></div>';
						});
						$('#rftTemplates > .row').html(html);
						priviousTemplatesBlocksEvent();
						if (!found) {
							$("#idTemplateInfoMessage").html('No matching data found');
							$("#idTemplateInfo").show();
						}
					},
					error : function(request, textStatus, errorThrown) {
						if (request.getResponseHeader('error')) {
							$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
							$("#idGlobalError").show();
						}
						if (request.getResponseHeader('info')) {
							$("#idGlobalInfoMessage").html(request.getResponseHeader('info').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
							$("#idGlobalInfo").show();
						}
					}
				});
			});

});

$(document).on("click", ".auctionTypeButton", function(e) {
	e.preventDefault();

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	// var auctionType = $.trim($('#myModal-auction').find('#inlineRadio110').chcked().val());
	var auctionType = $("input[name='example-radio']:checked").val();
	var eventType = $('#eventTypeSearch').val();
	var ajaxUrl = getContextPath() + "/buyer/navigateEvent/" + eventType;
	var auctionData = {
		'auctionType' : auctionType,
		'eventType' : eventType,
	};
	$.ajax({
		url : ajaxUrl,
		data : auctionData,
		type : "GET",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success : function(data) {
			window.location.href = getBuyerContextPath('createEventDetails/' + data);
			var info = 'Success';
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
			$('#myModal-auction').modal('hide');
		}
	});
	// $('.createEventsTopTabs').click(function(){
	// alert('b');
	// priviousTemplatesBlocksEvent();
	// });
});
function priviousTemplatesBlocksEvent() {
	var heights = [];
	var fullWidth = parseInt($('.tab-pane.active').width());
	var blkWidth = parseInt($('.currentTemplates:first').width());
	var noofBlok = parseInt(fullWidth / blkWidth);
	$(".idRftEventsHgt:visible").each(function(i) {
		var eachhgt = {};
		if ($(this).attr('data-value') != undefined) {
			// eachhgt['hgt'] = $(this).height();
			if ($(this).hasClass('hightedBlock')) {
				eachhgt['hgt'] = $(this).height() - 55;
			} else {
				eachhgt['hgt'] = $(this).height();
			}
		} else {
			eachhgt['hgt'] = 0;
		}
		eachhgt['value'] = $(this).attr('data-value');
		heights.push(eachhgt);
	});
	console.log(heights);
	var maxhgt = [];
	var prevBlocks = [];
	$.each(heights, function(i, hgt) {
		maxhgt.push(hgt.hgt);
		prevBlocks.push(hgt.value);
		if (((i + 1) % noofBlok) == 0) {
			var MaxValHgt = Math.max.apply(null, maxhgt);
			var descHgt = [];
			var createdHgt = [];
			$.each(prevBlocks, function(i, blk) {
				if (blk != undefined) {
					$(".idRftEventsHgt[data-value=" + blk + "] > div").css('min-height', (MaxValHgt + 55)).parent().addClass('hightedBlock');
				}
			});
			maxhgt = [];
			prevBlocks = [];
		}
	});
	var MaxValHgt = Math.max.apply(null, maxhgt);
	$.each(prevBlocks, function(i, blk) {
		if (blk != undefined) {
			$(".idRftEventsHgt[data-value=" + blk + "] > div").css('min-height', (MaxValHgt + 55)).parent().addClass('hightedBlock');
		}
	});
}

$('#tabTemplateId').click(function() {
	$("#idGlobalInfo").hide();
	$("#idGlobalError").hide();
	$("#idGlobalWarn").hide();
	$("#idEventInfo").hide();
	$("#idTemplateInfo").hide();
	$('.searchTemplatefield').removeClass('flagvisibility');
	$('.searchpreviousfield').addClass('flagvisibility');
});
$('#tabPreviousId').click(function() {
	$("#idGlobalInfo").hide();
	$("#idGlobalError").hide();
	$("#idGlobalWarn").hide();
	$("#idEventInfo").hide();
	$("#idTemplateInfo").hide();
	$('.searchpreviousfield').removeClass('flagvisibility');
	$('.searchTemplatefield').addClass('flagvisibility');
});