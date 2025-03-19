var stripe = null
var elements = null;

$(document).ready(function () {

	try {
		if (window.location.href.indexOf('payment_intent') != -1) {
			var newURL = window.location.href.replaceAll('payment_intent', 'payment_intent_old');
			window.history.replaceState({}, document.title, "" + newURL);
		}
	} catch (error) {
		console.log('Error:', error);
	}

	if ($('#publishKeyId').val() && $('#publishKeyId').val().length > 0) {
		stripe = Stripe($('#publishKeyId').val());
		elements = stripe.elements();
	}

	$(document).on("click", ".reject-invitation", function (e) {
		e.preventDefault();
		$('#confirmRemoveEvent').find('#deleteIdEvent').val($(this).attr('eventId'));
		$('#confirmRemoveEvent').modal().show();
	});

	$(document).on("click", ".finish-event", function (e) {
		e.preventDefault();
		$('#confirmFinishEvent').find('#finishIdEvent').val($(this).attr('eventId'));
		$('#confirmFinishEvent').modal().show();
	});

	$(".Invited-Supplier-List").click(function () {
		$(this).toggleClass("small-accordin-tab");
	});

	checkHeightMettingBlock();

	$('#acceptInvitation').on('change', function () {
		if ($(this).is(':checked')) {
			$('.accept-invitation').removeClass('disabled');
		} else {
			$('.accept-invitation').addClass('disabled');
		}
	});

	// $(document).delegate('.accept-invitation', 'click', function() {
	// var eventId = $(".eventId").val();
	// var accP = $("#acceptInvitation").val();
	// console.log("accept-invitation " + eventId + " Value " + accP);
	// var header = $("meta[name='_csrf_header']").attr("content");
	// var token = $("meta[name='_csrf']").attr("content");
	// var ajaxUrl = getContextPath() + '/supplier/acceptOrRejectInvitation/' + eventId + '/' + getEventType() + '/true';
	// $.ajax({
	// url : ajaxUrl,
	// type : "POST",
	// beforeSend : function(xhr) {
	// $('#loading').show();
	// xhr.setRequestHeader(header, token);
	// },
	// success : function(data, textStatus, request) {
	// $('#loading').hide();
	// var info = request.getResponseHeader('success');
	// if (request.getResponseHeader('info')) {
	// $('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success').split(",").join("<br/>"));
	// $('div[id=idGlobalSuccess]').show();
	// }
	// alert(request.getResponseHeader('url'));
	// window.location = getContextPath() + request.getResponseHeader('url');
	// },
	// error : function(request, textStatus, errorThrown) {
	// console.log("ERROR : " + request.getResponseHeader('error'));
	// if (request.getResponseHeader('error')) {
	// $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
	// $('div[id=idGlobalError]').show();
	// }
	// $('#loading').hide();
	// },
	// complete : function() {
	// $('#loading').hide();
	// }
	// });
	// return false;
	// });
	//
	//	
	// $(document).delegate( '#confRemoveEvent', 'click', function(e) {
	// e.preventDefault();
	// $('div[id=idGlobalError]').hide();
	// $('div[id=idGlobalSuccess]').hide();
	// var eventId = $(".eventId").val();
	// console.log("event id"+ eventId);
	// var header = $("meta[name='_csrf_header']").attr("content");
	// var token = $("meta[name='_csrf']").attr("content");
	// var ajaxUrl = getContextPath() + '/supplier/acceptOrRejectInvitation/' + eventId + '/' + getEventType() + '/false';
	// $.ajax({
	// url : ajaxUrl,
	// type : "POST",
	// beforeSend : function(xhr) {
	// $('#loading').show();
	// xhr.setRequestHeader(header, token);
	// },
	// success : function(data, textStatus, request) {
	// $('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
	// $('div[id=idGlobalSuccess]').show();
	// $('#loading').hide();
	// $('#confirmRemoveEvent').modal().hide();
	// window.location = getContextPath() + '/supplier/supplierDashboard';
	// },
	// error : function(request, textStatus, errorThrown) {
	// console.log("ERROR : " + request.getResponseHeader('error'));
	// if (request.getResponseHeader('error')) {
	// $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
	// $('div[id=idGlobalError]').show();
	// }
	// $('#loading').hide();
	// $('#confirmRemoveEvent').modal().hide();
	// },
	// complete : function() {
	// $('#loading').hide();
	// }
	// });
	// return false;
	// });

	$(document).delegate('.finish-invitation', 'click', function () {
		var eventId = $(".finish-invitation").val();
		var siteVisitMandatoryCheck = $(".siteVisitMandatoryCheck").val();
		var siteVisitMandatory = {
			'siteVisitMandatoryCheck': siteVisitMandatoryCheck
		}
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() + '/supplier/finishEvent/' + getEventType() + '/' + eventId;
		$.ajax({
			url: ajaxUrl,
			type: "POST",
			data: siteVisitMandatory,
			beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success: function (data, textStatus, request) {
				$('#loading').hide();
				var message = request.getResponseHeader('error');
				var message2 = request.getResponseHeader('success');
				console.log("ERROR :  " + message);
				if (message != null)
					window.location = getContextPath() + request.getResponseHeader('url') + "?error=" + message;
				else
					window.location = getContextPath() + request.getResponseHeader('url') + "?success=" + message2;
			},
			error: function (request, textStatus, errorThrown) {
				console.log("ERROR :  " + request.getResponseHeader('error'));
				var message = request.getResponseHeader('error');
				if (message != null)
					window.location = encodeURI(getContextPath() + request.getResponseHeader('url') + "?error=" + message);
				$('#loading').hide();

				var rejectedMeetingNamesList = request.getResponseHeader('rejectedMeetingNamesList');
				if (rejectedMeetingNamesList != null && rejectedMeetingNamesList != undefined) {
					$(".siteVisitMandatoryCheck").val("false");
					$('#confirmRejectedMeeting').find('#rejectedMeetingNamesList').text(rejectedMeetingNamesList);
					$('#confirmFinishEvent').modal().hide();
					$('#confirmRejectedMeeting').modal().show();
				}
			},
			complete: function () {
				$('#loading').hide();
			}
		});
		return false;

	});

	$(document).delegate('.siteVisitClose', 'click', function () {
		$(".siteVisitMandatoryCheck").val("true");
	});

	$("#downloadDoc").on('click', function () {
		var id = [];
		$('.table-2 input[type="checkbox"]:checked').each(function () {
			id.push($(this).val());
			$('#docsId').val($(this).val());
		});
		console.log(" Download IDs :: " + id);
	});

	$(document).delegate('.openModelSure , .goingToAttendmeeting', 'click', function () {
		$('#myModal-sure').find('#meetingId').val($(this).parents('.mettingEachBlock').attr('data-metting'));
		$('#myModal-sure').find('input[type="text"], textarea').val('');
		$('#myModal-sure').modal();
	});

	$(document).delegate('.openModelSureCancel, .notgoingInmeeting', 'click', function () {
		$('#myModal-sureCancle').find('#meetingId').val($(this).parents('.mettingEachBlock').attr('data-metting'));
		$('#myModal-sureCancle').find('input[type="text"], textarea').val('');
		$('#myModal-sureCancle').modal();
	});

	function checkHeightMettingBlock() {
		/*
		 * $('.mettingEachBlock').each(function() { var sure1hgt = 95; if ($(this).find('.popup_for_sure1, .popup_for_sure').is(":visible")) {
		 * console.log($(this).find('.popup_for_sure1, .popup_for_sure').height()); sure1hgt = sure1hgt + $(this).find('.popup_for_sure1,
		 * .popup_for_sure').height(); } $(this).css('min-height', sure1hgt); });
		 */
	}

	$(document).delegate('#saveMeetingAttendanceCancel', 'click', function (e) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		e.preventDefault();
		var meetingId = $('#myModal-sureCancle').find('#meetingId').val();
		var eventMeeting = {
			"name": $("#rejectname").val(),
			"rejectReason": $("#rejectReason").val(),
			"eventId": $("#eventId").val(),
			"meetingId": meetingId
		}
		var ajaxUrl = getContextPath() + '/supplier/rejectMeeting/' + getEventType();
		$.ajax({
			url: ajaxUrl,
			type: "POST",
			data: eventMeeting,
			beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success: function (data) {
				$('#myModal-sureCancle').modal('hide');
				$('#supplierMeetingRegisterForm').find('#name').val('');
				$('#supplierMeetingRegisterForm').find('#rejectReason').val('');
				console.log("data" + data);
				$('#loading').hide();
				$('.mettingEachBlock[data-metting="' + meetingId + '"]').find('.popup_for_sure, .popup_for_sure1').remove();
				var html = showsupplierAttendanceRejected(data);
				$('.mettingEachBlock[data-metting="' + meetingId + '"]').append(html);
				checkHeightMettingBlock();
			},
			error: function (request, textStatus, errorThrown) {
				console.log("ERROR :  " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			},
			complete: function () {
				$('#loading').hide();
			}
		});
		return false;
	});

	$(document).delegate('#saveMeetingAttendance', 'click', function (e) {

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		e.preventDefault();

		var meetingId = $('#myModal-sure').find('#meetingId').val();
		var eventMeeting = {
			"name": $("#name").val(),
			"designation": $("#designation").val(),
			"mobileNumber": $("#mobileNumber").val(),
			"remarks": $("#remarks").val(),
			"eventId": $("#eventId").val(),
			"meetingId": meetingId
		}
		var ajaxUrl = getContextPath() + '/supplier/acceptMeeting/' + getEventType();
		console.log("eventMeeting : " + ajaxUrl);
		$.ajax({
			url: ajaxUrl,
			type: "POST",
			data: eventMeeting,
			beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success: function (data) {
				$('#supplierMeetingRegisterForm').find('#name').val('');
				$('#supplierMeetingRegisterForm').find('#designation').val('');
				$('#supplierMeetingRegisterForm').find('#mobileNumber').val('');
				$('#supplierMeetingRegisterForm').find('#remarks').val('');
				$('#loading').hide();
				$('#myModal-sure').modal('hide');
				$('.mettingEachBlock[data-metting="' + meetingId + '"]').find('.popup_for_sure, .popup_for_sure1').remove();
				var html = showsupplierAttendanceAccepted(data);
				$('.mettingEachBlock[data-metting="' + meetingId + '"]').append(html);
				checkHeightMettingBlock();
			},
			error: function (request, textStatus, errorThrown) {
				if (request.getResponseHeader('error')) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			},
			complete: function () {
				$('#loading').hide();
			}
		});
		return false;
	});

	$('#eventFilter').change(function () {
		var meetingStatus = $('#eventFilter').val();
		console.log("event filter called");
		/*
		 * if ($.trim(meetingStatus).length == 0) { meetingStatus ="ALL"; }
		 */
		var eventId = $("#eventId").val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() + '/supplier/listMeetingStatus/' + getEventType() + '/' + eventId + ($.trim(meetingStatus).length != 0 ? '/' + meetingStatus : '');
		$.ajax({
			url: ajaxUrl,
			type: "POST",
			beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success: function (data, textStatus, request) {
				// var info = request.getResponseHeader('success');
				// $('p[id=idGlobalSuccessMessage]').html(info);
				// $('div.ajax-msg[id=idGlobalSuccess]').show();
				var table = '';
				$.each(data, function (i, item) {
					table += '<div class="Progress-Report-main marg-top-20 meeting-detail pending mettingEachBlock" data-metting="' + item.id + '">';
					table += '<div class="panel-heading">';
					table += '<h4 class="panel-title">' + item.title + '</h4></div>';
					table += '<div id="main" class="float-left set-h3-main">';
					table += '<div class="content">';
					table += '<div class="counter set-h3">';
					table += '<h3>' + item.appointmentDateTime + '</h3>';
					var curDateTime = '';
					if (item.appointmentDateTime < curDateTime) {
						table += '<div id="countdown' + item.id + '" class="countDownTimer" data-date="' + item.appointmentDateTime + '"></div>';
					}
					table += '</div></div></div>';
					table += '<div class="meetingListInerCustom">';
					table += '<div class="meeting_inner">';
					table += '<div class="meeting_inner_main">';
					table += '<h3>' + item.meetingType + '</h3></div>';
					if (item.rfxEventMeetingContacts != undefined && item.rfxEventMeetingContacts != null) {
						$.each(item.rfxEventMeetingContacts, function (i, contactData) {
							table += '<div class="meeting_inner_main">';
							if (contactData.contactName != '' && contactData.contactName != undefined) {
								table += '<img src="' + getContextPath() + '/resources/images/meeting_profile.png" alt="meeting profile" />';
								table += '<p>' + contactData.contactName + '</p>';
							}
							if (contactData.contactEmail != '' && contactData.contactEmail != undefined) {
								table += '<img src="' + getContextPath() + '/resources/images/meeting_message.png" alt="meeting message" />';
								table += '<p>' + contactData.contactEmail + '</p>';
							}
							if (contactData.contactNumber != '' && contactData.contactNumber != undefined) {
								table += '<img src="' + getContextPath() + '/resources/images/meeting_phone.png" alt="meetting phone" />';
								table += '<p>' + contactData.contactNumber + '</p>';
							}
							table += '</div>';
						});
					}
					table += '</div>';
					table += '<div class="meeting_inner">';
					table += '<div class="meeting_inner_main">';
					if (item.status == 'CANCELLED') {
						table += '<h3>';
						table += '<font color="red">' + item.status + '</font>';
						table += '</h3>';
					}
					if (item.status != 'CANCELLED') {
						table += '<h3>' + item.status + '</h3>';
					}
					table += '</div>';
					table += '<div class="meeting_inner_main">';
					table += '<label>Venue:</label>';
					table += '<p>' + item.venue + '</p>';
					table += '</div>';
					table += '<div class="meeting_inner_main">';
					table += '<label>Mandatory:</label>';
					if (item.meetingAttendMandatory == true) {
						table += '<span style="color:#ff0000"><b>Yes</b></span>';
					} else {
						table += '<span>No</span>';
					}
					table += '</div>';
					if (item.eventMeetingDocument != undefined && item.eventMeetingDocument != '') {
						table += '<div class="meeting_inner_main">';
						table += '<label>Attachment :</label>';
						$.each(item.eventMeetingDocument, function (i, docData) {
							table += (i + 1) + '. <a class="bluelink" href="' + getContextPath() + '/supplier/downloadEventMeetingDocument/' + getEventType() + '/' + docData.id + '">' + docData.fileName + '';
							table += '(' + (docData.fileSizeInKb != undefined ? ReplaceNumberWithCommas(docData.fileSizeInKb) : 0) + ' KB) </a>  <br />';
						});
						table += '</div>';
					}
					table += '</div>';
					table += '<div class="meeting_inner_main" style="margin-left: 3%;">';
					table += '<label>Remark:</label>';
					var itemremarks = item.remarks;
					if (item.remarks == undefined) {
						itemremarks = 'N/A';
					}
					table += '<p class="pull-left">' + itemremarks + '</p>';
					table += '</div></div>';
					if (item.supplierAttendance == null && item.status == 'SCHEDULED' && (eventStatus == 'ACTIVE' || eventStatus == 'CLOSED' || eventStatus == 'COMPLETE')) {
						table += '<div class="popup_for_sure pad_all_15">';
						table += '<h3>Are you Going ?</h3>';
						table += '<button class="btn ph_btn_small btn-info  hvr-pop hvr-rectangle-out openModelSure">Yes</button>';
						table += '<button class="btn ph_btn_small btn-black marg-left-10 fade-btn  hvr-pop hvr-rectangle-out1 openModelSureCancel">No</button>';
						table += '</div>';
					}
					if (item.supplierAttendance != null && item.supplierAttendance.meetingAttendanceStatus == 'Accepted') {
						table += '<div class="popup_for_sure1 pad_all_15 attending">';
						table += '<div class="Attandance-div">';
						table += '<h3>Meeting Accepted</h3>';
						table += '<div class="Attandance-div-inner">';
						table += '<label>Name</label>';
						table += '<p class="m_name">' + item.supplierAttendance.name + '</p>';
						table += '</div>';
						table += '<div class="Attandance-div-inner">';
						table += '<label>Designation</label>';
						table += '<p class="m_designation">' + item.supplierAttendance.designation + '</p>';
						table += '</div>';
						table += '<div class="Attandance-div-inner">';
						table += '<label>Mobile Number</label>';
						table += '<p class="m_number">' + item.supplierAttendance.mobileNumber + '</p>';
						table += '</div>';
						table += '<div class="Attandance-div-inner">';
						table += '<label>Remark</label>';
						table += '<p class="m_remark">' + item.supplierAttendance.remarks + '</p>';
						table += '</div>';
						table += '</div>';
						if ((eventStatus == 'ACTIVE' || eventStatus == 'CLOSED' || eventStatus == 'COMPLETE') && item.status == 'SCHEDULED') {
							table += '<button class="btn ph_btn_small btn-info  hvr-pop hvr-rectangle-out notgoingInmeeting">Not Going</button>';
						}
						table += '</div>';
					}
					if (item.supplierAttendance != null && item.supplierAttendance.meetingAttendanceStatus == 'Rejected') {
						table += '<div class="popup_for_sure1 pad_all_15 notAttending">';
						table += '<div class="Attandance-div">';
						table += '<input type="hidden" name="meetingId" id="meetingId" value="" />';
						table += '<h3>Not Attending</h3>';
						table += '<div class="Attandance-div-inner">';
						table += '<label>Name</label>';
						table += '<p class="m_name">' + item.supplierAttendance.name + '</p>';
						table += '</div>';
						table += '<div class="Attandance-div-inner">';
						table += '<label>Reject Date</label>';
						table += '<p class="m_actionDate">' + item.supplierAttendance.actionDate + '</p>';
						table += '</div>';
						table += '<div class="Attandance-div-inner">';
						table += '<label>Reason</label>';
						table += '<p class="m_reason" pattern="dd/MMM/yyyy -HH:mm a">' + item.supplierAttendance.rejectReason + '</p>';
						table += '</div>';
						table += '</div>';
						if ((eventStatus == 'ACTIVE' || eventStatus == 'CLOSED' || eventStatus == 'COMPLETE') && item.status == 'SCHEDULED') {
							table += '<button class="btn ph_btn_small btn-info  hvr-pop hvr-rectangle-out goingToAttendmeeting">Going To Attend</button>';
						}
						table += '</div>';
					}
					table += '</div>';
					/*
					 * table += '<div class="Progress-Report-main marg-top-20 meeting-detail pending mettingEachBlock" data-metting="' + item.id +
					 * '">' + '<div class="panel-heading"><h4 class="panel-title">' + item.appointmentDateTime + ' ('+ item.status+')</h4></div>' + '<div
					 * class="event-side-visit-in pad_all_15"><div class="event-side-visit-in-first"><div class="row">' + '<div class="col-md-2
					 * col-sm-3 col-xs-6"><label>Venue :</label></div><div class="col-md-5 col-sm-6 col-xs-6">' + '<p>' + item.venue + '<br></p></div></div></div><div
					 * class="event-side-visit-in-first"><div class="row">' + '<div class="col-md-2 col-sm-3 col-xs-6"><label>Contact Information :</label></div>' + '<div
					 * class="col-md-5 col-sm-6 col-xs-6">'; if (item.rfxEventMeetingContacts != undefined && item.rfxEventMeetingContacts != null) {
					 * $.each(item.rfxEventMeetingContacts, function(i, contactData) { table += '<p>' + contactData.contactName + '<br>' +
					 * contactData.contactEmail + '<br>' + contactData.contactNumber + '</p>'; }); } table += '</div></div></div><div
					 * class="event-side-visit-in-first"><div class="row"><div class="col-md-2 col-sm-3 col-xs-6">' + '<label>Remark :</label></div><div
					 * class="col-md-5 col-sm-6 col-xs-6"><p>' + (item.remarks != undefined ? item.remarks : '') + '<br></p></div></div></div></div>';
					 * if (item.supplierAttendance == null) { table += '<div class="popup_for_sure pad_all_15"><h3>Are you Going ?</h3>'; table += '<button
					 * class="btn ph_btn_small btn-info hvr-pop hvr-rectangle-out openModelSure">Yes</button>'; table += '<button class="btn
					 * ph_btn_small btn-black marg-left-10 fade-btn hvr-pop hvr-rectangle-out1 openModelSureCancel">No</button></div>'; } if
					 * (item.supplierAttendance != null && item.supplierAttendance.meetingAttendanceStatus == 'Accepted') { table += '<div
					 * class="popup_for_sure1 pad_all_15 attending"><div class="Attandance-div">'; table += '<h3>Meeting Accepted</h3><div
					 * class="Attandance-div-inner"><label>Name</label>'; table += '<p class="m_name">' + item.supplierAttendance.name + '</p></div>';
					 * table += '<div class="Attandance-div-inner"><label>Designation</label>'; table += '<p class="m_designation">' +
					 * item.supplierAttendance.designation + '</p></div>'; table += '<div class="Attandance-div-inner"><label>Mobile Number</label>';
					 * table += '<p class="m_number">' + item.supplierAttendance.mobileNumber + '</p></div>'; table += '<div
					 * class="Attandance-div-inner"><label>Remark</label>'; table += '<p class="m_remark">' + item.supplierAttendance.remarks + '</p></div></div>';
					 * if(item.status == 'SCHEDULED'){ table += '<button class="btn ph_btn_small btn-info hvr-pop hvr-rectangle-out
					 * notgoingInmeeting">Not Going</button></div>'; } } if (item.supplierAttendance != null &&
					 * item.supplierAttendance.meetingAttendanceStatus == 'Rejected') { table += '<div class="popup_for_sure1 pad_all_15
					 * notAttending">'; table += '<div class="Attandance-div"><input type="hidden" name="meetingId" id="meetingId" value="" />';
					 * table += '<h3>Not Attending</h3><div class="Attandance-div-inner"><label>Name</label>'; table += '<p class="m_name">' +
					 * item.supplierAttendance.name + '</p></div>'; table += '<div class="Attandance-div-inner"><label>Reject Date</label>';
					 * table += '<p class="m_actionDate" pattern="dd/MMM/yyyy -HH:mm a">' + item.supplierAttendance.actionDate + '</p></div>';
					 * table += '<div class="Attandance-div-inner"><label>Reason</label>'; table += '<p class="m_reason">' +
					 * item.supplierAttendance.rejectReason + '</p></div></div>'; table += '<button class="btn ph_btn_small btn-info hvr-pop
					 * hvr-rectangle-out goingToAttendmeeting">Going To Attend</button></div>'; } table += '</div></div>';
					 */
				});
				// console.log(table);
				if (table === '') {
					$('.allmettingsData').html('<div class="panel-heading Progress-Report-main marg-top-20 meeting-detail pending mettingEachBlock">No Meetings.</div>');
				} else {
					$('.allmettingsData').html(table);
				}
				checkHeightMettingBlock();
				$('#loading').hide();

			},
			complete: function () {
				$('#loading').hide();
			}
		});
	});

	function showsupplierAttendance(data) {
		var table = '';
		table += '<div class="popup_for_sure pad_all_15"><h3>Are you Going ?</h3>';
		table += '<button class="btn ph_btn_small btn-info  hvr-pop hvr-rectangle-out openModelSure">Yes</button>';
		table += '<button class="btn ph_btn_small btn-black marg-left-10 fade-btn  hvr-pop hvr-rectangle-out1 openModelSureCancel">No</button></div>';
		return table;
	}

	function showsupplierAttendanceAccepted(data) {
		var table = '';
		table += '<div class="popup_for_sure1 pad_all_15 attending"><div class="Attandance-div">';
		table += '<h3>Meeting Accepted</h3><div class="Attandance-div-inner"><label>Name</label>';
		table += '<p class="m_name">' + data.name + '</p></div>';
		table += '<div class="Attandance-div-inner"><label>Designation</label>';
		table += '<p class="m_designation">' + data.designation + '</p></div>';
		table += '<div class="Attandance-div-inner"><label>Mobile Number</label>';
		table += '<p class="m_number">' + data.mobileNumber + '</p></div>';
		table += '<div class="Attandance-div-inner"><label>Remark</label>';
		table += '<p class="m_remark">' + data.remarks + '</p></div></div>';
		table += '<button class="btn ph_btn_small btn-info  hvr-pop hvr-rectangle-out notgoingInmeeting">Not Going</button></div>';
		return table;
	}

	function showsupplierAttendanceRejected(data) {
		var table = '';
		table += '<div class="popup_for_sure1 pad_all_15 notAttending">';
		table += '<div class="Attandance-div"><input type="hidden" name="meetingId" id="meetingId" value="" />';
		table += '<h3>Not Attending</h3><div class="Attandance-div-inner"><label>Name</label>';
		table += '<p class="m_name">' + data.name + '</p></div>';
		table += '<div class="Attandance-div-inner"><label>Reject Date</label>';
		table += '<p class="m_actionDate">' + data.actionDate + '</p></div>';
		table += '<div class="Attandance-div-inner"><label>Reason</label>';
		table += '<p class="m_reason">' + data.rejectReason + '</p></div></div>';
		table += '<button class="btn ph_btn_small btn-info  hvr-pop hvr-rectangle-out goingToAttendmeeting">Going To Attend</button></div>';
		return table;
	}

	// $('.access_check').on('click', function(e) {
	$(document).delegate('.access_check', 'click', function (e) {
		e.stopPropagation();
		$('.access_check').prop('checked', false);
		$(this).prop('checked', true);
		tempId = $(this).attr("id");
		selector = $("#" + tempId).closest("div").find(".dropdown-toggle");
		// console.log(selector);
		if ($(this).val() == "Editor") {
			selector.html("<i class='glyphicon glyphicon-pencil' aria-hidden='true '></i>");
		}
		if ($(this).val() == "Viewer") {
			selector.html("<i class='glyphicon glyphicon-eye-open' aria-hidden='true '></i>");
		}

		if ($(this).data('uid') == "" || $(this).data('uid') == undefined)
			return;

		/** ** Update Team Members ** */
		var memberType = $(this).val();
		var currentBlock = $(this);
		selector.click();
		var userId = $(this).data('uid');
		var eventId = $("#eventId").val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() + '/supplier/addTeamMemberToList/' + getEventType();
		$.ajax({
			type: "POST",
			url: ajaxUrl,
			data: {
				memberType: memberType,
				userId: userId,
				eventId: eventId
			},
			beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				$('#loading').hide();
			},
			success: function (data) {
				userList = userListForEvent(data, memberType);
				// currentBlock.closest('.width100').next('.usersListTable').html(userList);
				// currentBlock.parent().prev().find('select').find('option[value="' + userId + '"]').remove();
				// currentBlock.parent().prev().find('select').val('').trigger("chosen:updated");
				$('#loading').hide();
				if ($('#eventTeamMembersList').length > 0) {
					$('#eventTeamMembersList').DataTable().ajax.reload();
				}

				$('#appUsersList').html("");
				$('#appUsersList').html(userList);
				$('#TeamMemberList').find('option[value="' + userId + '"]').remove();
				$('#TeamMemberList').trigger("chosen:updated");
				$('#loading').hide();

			},
			error: function (request, textStatus, errorThrown) {

				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete: function () {
				$('#loading').hide();
			}
		});

	});

	// addTeamMembers

	$('.addTeamMemberToList').click(function (e) {
		e.preventDefault();
		var currentBlock = $(this);
		var memberType = $(this).closest('div').find('.access_check:checkbox:checked').val(); // $('.access_check:checkbox:checked').val();// Viewer
		// currentBlock.attr('list-type');
		// console.log(memberType);
		if (memberType == undefined || memberType == "") {
			memberType = "Viewer";
		}
		$('.access_check').prop('checked', false);

		var userId = $("#TeamMemberList").val();
		var eventId = $("#eventId").val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() + '/supplier/addTeamMemberToList/' + getEventType();

		if (userId == "") {
			// $('p[id=idGlobalErrorMessage]').html("Please Select User");
			// $('div[id=idGlobalError]').show();
			$("#editor-err").removeClass("hide ");

			return;
		}
		$("#editor-err").addClass("hide");
		$.ajax({
			type: "POST",
			url: ajaxUrl,
			data: {
				memberType: memberType,
				userId: userId,
				eventId: eventId
			},
			beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				$('#loading').hide();
			},
			success: function (data) {
				userList = userListForEvent(data, memberType);

				$('#appUsersList').html("");
				$('#appUsersList').html(userList);
				$('#TeamMemberList').find('option[value="' + userId + '"]').remove();
				$('#TeamMemberList').trigger("chosen:updated");
				$('#loading').hide();
			},
			error: function (request, textStatus, errorThrown) {

				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete: function () {
				$('#loading').hide();
			}
		});
	});

	function userListForEvent(data, type) {
		var userList = '';

		$.each(data, function (i, user) {
			userList += '<tr  data-username="' + user.user.name + '" approver-id="' + user.user.id + '">';
			userList += '<td class="width_50_fix "></td>';
			userList += '<td>' + user.user.name + '<br> <span>' + user.user.loginId + '</span> </td>';
			userList += '<td class="edit-drop"><div class="advancee_menu"><div class="adm_box">';
			if (user.teamMemberType == "Editor")
				userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"  title="Editor"> <i class="glyphicon glyphicon-pencil " aria-hidden="true " ></i> </a>';
			else if (user.teamMemberType == "Viewer")
				userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"  title="Viewer"> <i class="glyphicon glyphicon-eye-open" aria-hidden="true " ></i> </a>';
			userList += '<ul class="dropdown-menu dropup"><li><a href="javascript:void(0);" class="small"><input ';
			if (user.teamMemberType == "Editor")
				userList += ' checked ';
			userList += 'data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Editor" type="checkbox" value="Editor">&nbsp;Editor</a> </li>';
			userList += '<li><a  href="javascript:void(0);" class="small"><input data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Viewer" ';
			if (user.teamMemberType == "Viewer")
				userList += ' checked ';
			userList += 'type="checkbox" value="Viewer">&nbsp;Viewer</a> </li>';
			userList += '</ul></li></ul></div></div></td><td>'
			userList += '<div class="cqa_del"> <a href="#" list-type="Team Member" class="removeApproversList" data-toggle="dropdown"  title="Delete" ></a> </div></td></tr>'

			/*
			 * userList += '<div class="row" user-id="' + user.id + '">'; if (listType == 'Approver') { userList += '<div class="col-md-1 pad0"><a
			 * href="" class="upbutton"><i class="fa fa-level-up" aria-hidden="true"></i></a>'; userList += '<a href="" class="downbutton"><i
			 * class="fa fa-level-down" aria-hidden="true"></i></a></div>'; userList += '<div class="col-md-9 pad0"><p>' + user.name + '</p></div>'; }
			 * else { userList += '<div class="col-md-10"><p>' + user.name + '</p></div>'; } userList += '<div class="col-md-2">'; userList += '<a
			 * href="" class="removeUserList" list-type="' + listType + '"><i class="fa fa-times-circle"></i></a></div></div>';
			 * 
			 */
		});
		if (userList == '') {
			// userList += '<div class="row" user-id=""><div class="col-md-12"><p>Add ' + listType + '</p></div></div>';
		}
		return userList;
	}

	function userListForEventDropdown(data, listType) {
		return "";
		var userList = '<option value="">Select Editor</option>';
		$.each(data, function (i, user) {
			userList += '<option value="' + user.id + '">' + user.name + '</option>';
		});
		return userList;
	}

	// remove approvers list
	$(document).delegate('.removeApproversList', 'click', function (e) {

		e.preventDefault();
		var currentBlock = $(this);
		var listType = currentBlock.attr('list-type');
		var listUserId = currentBlock.closest('tr').attr('approver-id');
		var userName = currentBlock.closest('tr').attr('data-username');
		$("#removeApproverListPopup").dialog({
			modal: true,
			maxWidth: 400,
			minHeight: 100,
			dialogClass: "",
			show: "fadeIn",
			draggable: true,
			resizable: false,
			dialogClass: "dialogBlockLoaded"
		});
		$('.ui-widget-overlay').addClass('bg-white opacity-60');
		$("#removeApproverListPopup").find('.approverInfoBlock').find('span:first-child').text(userName);
		$("#removeApproverListPopup").find('.approverInfoBlock').find('span:last-child').text(listType);
		$("#removeApproverListPopup").find('#approverListId').val(listUserId);
		$("#removeApproverListPopup").find('#approverListType').val(listType);
		$('.ui-dialog-title').text('Remove ' + listType);
	});

	$(document).delegate('.removeApproverListPerson', 'click', function (e) {

		var eventId = $("#eventId").val();
		var userId = $("#removeApproverListPopup").find('#approverListId').val();
		var listType = $("#removeApproverListPopup").find('#approverListType').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() + '/supplier/removeTeamMemberfromList/' + getEventType();

		$.ajax({
			type: "POST",
			url: ajaxUrl,
			data: {
				eventId: eventId,
				listType: listType,
				userId: userId
			},
			beforeSend: function (xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
			},
			success: function (data) {
				userList = approverListForEventDropdown(data);
				// $('.row[user-id="'+userId+'"]').closest('.width100.usersListTable').html(userList);
				// console.log()
				$('#appUsersList tr[approver-id="' + userId + '"]').remove();
				$('#TeamMemberList').html(userList).trigger("chosen:updated");
				// $('#TeamMemberList').html(userList).trigger("chosen:updated");

				// if ($('.row[approver-id="' + userId + '"]').siblings().length == 0) {
				// userList = approverListForEvent('', listType);
				// $('.row[approver-id="' + userId + '"]').closest('.width100.usersListTable').html(userList);
				// }

				// $('.row[approver-id="' + userId + '"]').remove();
				$("#removeApproverListPopup").dialog('close');
				// reorderApproverList();
			},
			error: function (e) {
				console.log("Error");
			},
			complete: function () {
				$('#loading').hide();
			}
		});
	});

	$(document).on('submit', '#payment-form-fpx', function (event) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var fpxBank = elements.getElement('fpxBank');
		event.preventDefault();
		payByFpx(header, token, event, fpxBank);
	});
});

$(document).on("click", "#payFees", function (e) {
	e.preventDefault();
	$('#payFees').prop('disabled', true);
	$('#payFees').addClass('disabled');
	$('#makePaymentModal').attr('payment-amount', $('#eventParticipationFeesId').val())
	$('#makePaymentModal').modal().show();
	$('#tabOneId').click();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
});

$(document).on("change", "#paymentEmailFpx", function (e) {
	var fpxBank = elements.getElement('fpxBank');
	if (fpxBank._complete && $('#emailFpxForm').isValid()) {
		$('#fpx-button').disabled = false;
		$('#fpx-button').removeClass('disabled');
		$('#fpx-button').addClass('btn-success');
	} else if (!$('#emailFpxForm').isValid()) {
		$('#fpx-button').disabled = true;
		$('#fpx-button').removeClass('btn-success');
		$('#fpx-button').addClass('disabled');
	}
});

$(document).on("change", "#paymentEmailCard", function (e) {
	var card = elements.getElement('card')
	if (card._complete && $('#emailCardForm').isValid()) {
		$('#payByCardId').disabled = false;
		$('#payByCardId').removeClass('disabled');
		$('#payByCardId').addClass('btn-success');
	} else if (!$('#emailCardForm').isValid()) {
		$('payByCardId').disabled = true;
		$('payByCardId').removeClass('btn-success');
		$('payByCardId').addClass('disabled');
	}
});

$('#makePaymentModal').on('hide.bs.modal', function (e) {
	$('#payFees').prop('disabled', false);
	$('#payFees').removeClass('disabled');
	var errorMsg = document.querySelector(".sr-field-error");
	errorMsg.textContent = '';
	var fpxBank = elements.getElement('fpxBank');
	var card = elements.getElement('card');
	card ? card.clear() : '';
	fpxBank ? fpxBank.clear() : '';
	$('#payFees').prop('disabled', false);
	$('#payFees').removeClass('disabled');
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
		if (event.complete && $('#emailCardForm').isValid()) {
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
		if (event.complete && $('#emailFpxForm').isValid()) {
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

function payByFpx(header, token, event, fpxBank) {
	$.ajax({
		type: "POST",
		url: getContextPath() + '/supplier/initializePayment/' + $('#makePaymentModal').attr('payment-mode') + '/' + getEventType() + '/' + $('#eventIdNumberId').val() + '?email=' + $('#paymentEmailFpx').val(),
		contentType: "application/json",
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
					showErrorForPayment(result.error.message)
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
	$.ajax({
		type: "POST",
		url: getContextPath() + '/supplier/initializePayment/' + $('#makePaymentModal').attr('payment-mode') + '/' + getEventType() + '/' + $('#eventIdNumberId').val() + '?email=' + $('#paymentEmailFpx').val(),
		contentType: "application/json",
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
					location += '?&payment_intent=done'
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

function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}

function approverListForEventDropdown(data, listType) {
	var userList = '<option value="">Select Team Member</option>';
	$.each(data, function (i, user) {
		userList += '<option value="' + user.id + '">' + user.name + '</option>';
	});
	return userList;
}

function getUrlParameter(sParam) {
	var sPageURL = window.location.search.substring(1),
		sURLVariables = sPageURL.split('&'),
		sParameterName,
		i;

	for (i = 0; i < sURLVariables.length; i++) {
		sParameterName = sURLVariables[i].split('=');

		if (sParameterName[0] === sParam) {
			return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
		}
	}
};