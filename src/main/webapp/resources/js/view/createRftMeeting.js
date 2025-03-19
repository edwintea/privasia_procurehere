// Global Variable
var isAddContactPerson = true;


function updateProgress(evt) {
	if (evt.lengthComputable) {
		var percentComplete = Math.ceil((evt.loaded / evt.total) * 100);
		progress(percentComplete, $('.progressbar'));
		var percentVal = percentComplete + '%';
		console.log(percentVal);
	} else {
		// Unable to compute progress information since the total size is
		// unknown
		console.log('unable to complete');
	}
}
$(function() {
	$.validate({
		lang : 'en',
		modules : 'file'
	});
	"use strict";
	if ($('.bootstrap-datepicker').attr('start-date') != '' && $('.bootstrap-datepicker').attr('start-date') != undefined) {
		var dateArr = $('.bootstrap-datepicker').attr('start-date').split('/');
		var nowTemp = new Date();
		nowTemp.setYear(dateArr[2]);
		nowTemp.setDate(dateArr[0]);
		nowTemp.setMonth(dateArr[1] - 1); // month starts from 0
		// var nowTemp = new Date();
	} else {
		var nowTemp = new Date();
	}

	if ($('.bootstrap-datepicker').attr('end-date') != '' && $('.bootstrap-datepicker').attr('end-date') != undefined) {
		var dateArr = $('.bootstrap-datepicker').attr('end-date').split('/');
		console.log(dateArr);
		var endDateTemp = new Date();
		endDateTemp.setYear(dateArr[2]);
		endDateTemp.setDate(dateArr[0]);
		endDateTemp.setMonth(dateArr[1] - 1); // month starts from 0
		// var nowTemp = new Date();
		var endDate = new Date(endDateTemp.getFullYear(), endDateTemp.getMonth(), endDateTemp.getDate(), 0, 0, 0, 0);
	}
	// console.log(endDate.valueOf());

	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
	// console.log(now);
	$('.bootstrap-datepicker').bsdatepicker({
		format : 'dd/mm/yyyy',
		onRender : function(date) {
			if (date.valueOf() < now.valueOf()) {
				return 'disabled';
			}
			if (endDate != undefined) {

				if (date.valueOf() > endDate.valueOf()) {
					return 'disabled';
				}
			}
			return '';
		}
	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
	});

	$('.bootstrap-datepicker1').bsdatepicker({
		format : 'dd/mm/yyyy'
	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
	});

	$('#noofcontacts').val($('.contactList.marginDisable > .row').not('.placeHolderBlock').length);

	$.formUtils.addValidator({
		name : 'contact_name',
		validatorFunction : function(value, $el, config, language, $form) {
			var existName = true;
			var nName = $.trim(value);
			var contactId = $('#contactPerson').find('#id').val();
			// console.log(contactId);
			$('.contactList.marginDisable > .row').each(function() {
				// console.log($(this).attr('id'));
				if (contactId == '' || contactId != $(this).attr('id')) {
					var oName = $.trim($(this).find('[name="contactName[]"]').val());
					if (nName == oName) {
						existName = false;
					}
				}
			});
			return existName;
		},
		errorMessage : 'Contact Name Already registered in the system',
		errorMessageKey : 'badContactName'
	});

	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});

	setTimeout(function() {
		$('#loading').fadeOut(400, "linear");
	}, 300);
	$('.timepicker-example').timepicker().on('hide.timepicker', function(e) {
		$(this).blur();
	});
	// $('.timepicker-example').timepicker();

	// pop-up past meeting
	$("#inlineCheckbox200").change(function() {
		if ($(this).is(":checked")) {
			$("#myModal4").modal("show");
		}
	});
	$("#inlineCheckbox200").change(function() {
		if ($(this).is(":checked")) {
			$("#myModal4").modal("show");
		}
	});
	$("#idAddContact").click(function(e) {
		e.preventDefault();
		// $('#idCreateEditContactDlg')[0].reset();
		$("#idCreateEditContactDlg").find('.emptyData').val('');
		$("#idCreateEditContactDlg").find('#resetFrom').click();
		$("#contactPerson").find('input').closest(".form-group").removeClass("has-error");
		$("#contactPerson").find('input').removeClass("error");
		$("#contactPerson").find('input').removeAttr("style");
		$("#contactPerson").find('.form-error').remove();
		$('#addContact').val(addLabel)
		$("#idCreateEditContactDlg").modal("show");
		isAddContactPerson = true;
	});
	$("#idAddReminder").click(function(e) {
		e.preventDefault();
		var reminderDate = $.trim($('#meetingDate').val());
		var reminderTime = $.trim($('#appointmentTime').val());
		console.log(reminderDate + " " + reminderTime);
		if (reminderDate != '' && reminderTime != '') {
			$("#idCreateEditContactDlg").find('#resetFrom').click();
			$("#addReminder").modal("show");
		} else {
			$('#meetingDate, #appointmentTime').blur();
		}
	});

});
$('#rftUploadDocument').click(function(e) {
	// $('div[id=Load_File-error-dialog]').hide();
});
// upload document
$('#uploadRftDoc').click(
		function(e) {
			e.preventDefault();

			// Here rftUploadDocument is the element id
			if(validateFile('rftUploadDocument')) {
				$('div[id=Load_File-error-dialog]').hide();
			} else {
				$('div[id=Load_File-error-dialog]').show();
			}

			if (!$('#demo-form4').isValid()) {
				return false;
			}
			if ($('#rftUploadDocument').val().length == 0) {
				$('div[id=Load_File-error-dialog]').removeClass('has-success');
				$('div[id=Load_File-error-dialog]').addClass('has-error');
				$('div[id=Load_File-error-dialog]').html('<span class="help-block form-error">This is required field</span>');

				$('div[id=Load_File-error-dialog]').show();
				return false;
			}
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();

			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");

			var file_data = $('#rftUploadDocument').prop('files')[0];
			var id = $('#id').val();
			var form_data = new FormData();
			form_data.append('id', id);
			form_data.append('file', file_data);
			$.ajax({
				url : getBuyerContextPath('uploadMeetingFile'),
				data : form_data,
				cache : false,
				xhr : function() { // custom xhr
					myXhr = $.ajaxSettings.xhr();
					if (myXhr.upload) { // check if upload property
						// exists
						myXhr.upload.addEventListener('progress', updateProgress, false); // for
						// handling
						// the
						// progress
						// of
						// the
						// upload
					}
					return myXhr;
				},
				type : "POST",
				enctype : 'multipart/form-data',
				processData : false,
				contentType : false,
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('div[id=idMessageJsp]').html("");
					$('#loading').show();
					$('.progressbar').removeClass('flagvisibility');
				},
				success : function(data, textStatus, request) {
					var success = request.getResponseHeader('success');
					console.log("Success message : " + success);
					document.getElementById("rftUploadDocument").value = "";
					$(".show_name").html('');
					$('#rftUploadDocument').closest('.fileinput-exists').addClass('fileinput-new').removeClass('fileinput-exists');
					$('p[id=idGlobalSuccessMessage]').html(success);
					$('div[id=idGlobalSuccess]').show();

					var table = '';
					$.each(data, function(i, item) {
						console.log("Success message : " + item.fileName);
						table += '<tr>' + '<td class="width_200 width_200_fix align-left"><a class="bluelink" href="downloadMeetingDocument?docId=' + item.id + '&docRefId=' + item.refId + '" >' + item.fileName + '</a></td>'
								+ '<td class="width_100 width_100_fix align-left">' + item.fileSize + ' KB </td>' + '<td class="width_100  width_100_fix align-center"><a href=""> <span class="removeDocFile" removeDocRefId="' + item.refId
								+ '" removeDocId="' + item.id + '"> <i class="fa fa-times-circle"></i></span></a></td>' + '</tr>';
					});
					console.log(table);
					$('#rftDocList tbody').html(table);
					$('#loading').hide();

					/**
					 * ******************below code commented to remove pop-up success message as it is already displayed using above code.
					 * **************
					 */

					// $.jGrowl(success, {
					// sticky : false,
					// position : 'top-right',
					// theme : 'bg-green'
					// });
				},
				error : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					$('#loading').hide();
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
				},
				complete : function() {
					$('#loading').hide();
					$('.progressbar').addClass('flagvisibility');
					progress(0, $('.progressbar'));
				}
			});
		});
$(document).on("change", "#rftUploadDocument", function() {
	$(".show_name").html($(this).val());
});

// remove InviteSupplier
$(document).delegate(
		'.removeMeetSupplier',
		'click',
		function(e) {
			e.preventDefault();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();

			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var removeSupId = $(this).attr('removeSupplierId');
			var meetingId = $('#meetingId').val();

			$.ajax({
				url : getBuyerContextPath('removeInviteSupplier'),
				data : ({
					removeSupId : removeSupId,
					meetingId : meetingId
				}),
				type : "GET",
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('div[id=idMessageJsp]').html("");
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
					var info = request.getResponseHeader('success');
					$('p[id=idGlobalSuccessMessage]').html(info);
					$('div.ajax-msg[id=idGlobalSuccess]').show();
					// document.getElementById("name").value = "";
					$('#loading').hide();
					var table = '';
					$.each(data, function(i, item) {
						var disabled = '';
						if (item.status == 'CANCELLED') {
							disabled = 'disabled';
						}
						table += '<div class="suppliers-name">' + '<label>' + item.companyName + '</label>' + '<span><a href="" class="removeMeetSupplier ' + disabled + '" removeSupplierId="' + item.id + '">' + '<img src="' + getContextPath()
								+ '/resources/images/black-xross.png"></a></span>' + '</div>';
					});
					console.log(table);
					$('.overscrollInnerBox.scroll_box_inner').html(table);
				},
				error : function(request, textStatus, errorThrown) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					$('#loading').hide();
				},
				complete : function() {
					$('#loading').hide();
				}
			});
		});
$(".for-toggle").click(function() {
	$(".add-contact-contant").toggle();
});

// add Contact Person
$('#addContact').click(
		function(e) {
			e.preventDefault();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();
			if (!$('#idCreateEditContactDlg').isValid()) {
				return false;
			}
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var nName = $.trim($('#contactPerson').find('#contactName').val());
			var ncontactEmail = $.trim($('#contactPerson').find('#contactEmail').val());
			var ncontactNumber = $.trim($('#contactPerson').find('#contactNumber').val());
			var nNameId = nName.replace(/\.|\s|\#|\@|\'|\"/g, '_');
			nName = nName.replace(/"/g, '&quot;');
			var contactId = $('#contactPerson').find('#id').val();
			var cId = '';

			if (isAddContactPerson == false) {
				cId = $('.contactList.marginDisable').find('#' + contactId).find('[name="contactId[]"]').val();
			}
			var table = '<div class="row" id="' + nNameId + '">' + '<input type="hidden" name="contactId[]" value="' + cId + '">' + '<input type="hidden" name="contactName[]" value="' + nName + '">'
					+ '<input type="hidden" name="contactEmail[]" value="' + ncontactEmail + '">' + '<input type="hidden" name="contactNumber[]" value="' + ncontactNumber + '">'
					+ '<div class="col-md-2"><a href="" class="editMeetContact pull-left" contactId="" data-placement="top" title="Edit"><i class="fa fa-edit"></i></a></div>' + '<div class="col-md-8"><p>' + nName + '</p><p>' + ncontactEmail + '/'
					+ ncontactNumber + '</p>' + '</div><div class="col-md-2"><a href="" class="deleteMeetContact" contactId=""><i class="fa fa-times-circle"></i></a></div></div>';
			if (isAddContactPerson == true) {
				$('.contactList.marginDisable').append(table);
			} else {
				$('.contactList.marginDisable').find('#' + contactId).replaceWith(table);
			}
			$('.contactList.marginDisable').find('.placeHolderBlock').remove();
			$('#noofcontacts').val($('.contactList.marginDisable > .row').not('.placeHolderBlock').length).blur();
			$("#idCreateEditContactDlg").find(".close").click();
			$("#idCreateEditContactDlg").find(".emptyData").val('');
			$('#loading').hide();
			if (isAddContactPerson == true) {
				var info = "Meeting contact person added successfully";
				$.jGrowl(info, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-green'
				});
			} else {
				var info = "Meeting contact person updated successfully";

				$.jGrowl(info, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-green'
				});

			}

		});

// edit Contact
$(document).delegate('.editMeetContact', 'click', function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var contactId = $(this).parents('.row').attr('id');
	$('#idCreateEditContactDlg').find('#id').val(contactId);
	$('#idCreateEditContactDlg').find('#contactName').val($('.contactList.marginDisable').find('#' + contactId).find('[name="contactName[]"]').val());
	$('#idCreateEditContactDlg').find('#contactEmail').val($('.contactList.marginDisable').find('#' + contactId).find('[name="contactEmail[]"]').val());
	$('#idCreateEditContactDlg').find('#contactNumber').val($('.contactList.marginDisable').find('#' + contactId).find('[name="contactNumber[]"]').val());
	$('#idCreateEditContactDlg').find('#addContact').val('Update');
	$("#idCreateEditContactDlg").modal('show');
	isAddContactPerson = false;
});

// delete contact
$(document).delegate('.deleteMeetContact', 'click', function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var contactId = $(this).attr('contactId');
	$('#confirmDeleteContect').find('#deleteId').val($(this).parents('.row').attr('id'));
	$('.modal-body b').text($(this).closest('.row').find('[name="contactName[]"]').val());
	$('#confirmDeleteContect').modal('show');
});
$('#confDelContact').click(function() {
	var deleteId = $('#confirmDeleteContect').find('#deleteId').val();
	$('.contactList.marginDisable').find('#' + deleteId).remove();
	if ($('.contactList.marginDisable').find('.row').not('.placeHolderBlock').length == 0) {
		$('.contactList.marginDisable').html('<div class="row placeHolderBlock"><div class="col-md-12"><p>Add Contact Person</p></div></div>');
	}
	$('#confirmDeleteContect').find('#deleteId').val('');
	$('#confirmDeleteContect').modal('hide');
	$('#noofcontacts').val($('.contactList.marginDisable > .row').not('.placeHolderBlock').length).blur();
	var info = "Meeting Contact deleted";
	$.jGrowl(info, {
		sticky : false,
		position : 'top-right',
		theme : 'bg-green'
	});
});

$('#reminderButton').click(
		function(e) {
			e.preventDefault();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();
			if (!$('#addReminder').isValid()) {
				return false;
			}
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			// var eventStartDate =
			// $.trim($('#demo-form1').find('#idStartEndDate').val());
			var reminderDuration = $.trim($('#addReminder').find('#remindMe').val());
			var reminderDurationType = $.trim($('#addReminder').find('.remindMeTime').val());
			reminderDateList = [];

			$(".myreminder").each(function(key, val) {
				reminderDateList.push($(this).children("#idreminderDateAndTime").val());
				console.log($(this).children("#idreminderDateAndTime").val());
			});
			var attrReminder = $("#addReminder").attr('data-dtype');

			var currentBlock = $('.rfaMeetingIdAddReminder[data-dtype="' + attrReminder + '"]');
			var reminderDate = $.trim($('#meetingDate').val());
			var reminderTime = $.trim($('.timepicker-example').val());
			var reminderId = $.trim($('#reminderId').val());
			var reminderData = {
				'reminderDuration' : reminderDuration,
				'reminderDurationType' : reminderDurationType,
				'meetingDate' : reminderDate,
				'meetingTime' : reminderTime,
				'reminderDateList' : reminderDateList,
				'reminderId' : reminderId
			};
			$.ajax({
				url : getBuyerContextPath('addReminderOfEventMeeting'),
				data : reminderData,
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('div[id=idMessageJsp]').html("");
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
					console.log(request.getResponseHeader('success'));
					var info = request.getResponseHeader('success');
					var table = '';
					newID = "myreminder" + jQuery(".myreminder").length + 1;
					table = '<div class="row myreminder" id="' + newID + '">' + '<input type="hidden" id="idReminderDuration" name="reminderDuration[]" value="' + data.interval + '">'
							+ '<input type="hidden" id="idreminderDateAndTime" name="reminderDateAndTime[]" value="' + data.reminderDate + '">' + '<input type="hidden" id="idReminderDurationType" name="reminderDurationType[]" value="'
							+ data.intervalType + '">' + '<div class="col-md-10"><p><b>Reminder Date: </b>' + data.reminderDate + '</p>' + '</div><div class="col-md-2"><a href="" class="deleteReminder" data-remtype="" reminderId="' + newID
							+ '"><i class="fa fa-times-circle"></i></a></div></div>';
					// $.each(data, function(i, item) {
					// });
					if (table == '') {
						table += '<div class="row" id=""><div class="col-md-12"><p>Add Reminder</p></div></div>';
					}
					// currentBlock.closest('.row').next().find('.reminderList.marginDisable').html(table);
					jQuery(".reminderList").append(table);
					jQuery(".reminderList .placeHolderBlock").hide();
					console.log(currentBlock);
					$.jGrowl(info, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					});
				},
				error : function(request, textStatus, errorThrown) {
					console.log(request.getResponseHeader('error'));
					var error = request.getResponseHeader('error');
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					// $.jGrowl(error, {
					// sticky : false,
					// position : 'top-right',
					// theme : 'bg-red'
					// });
				},
				complete : function() {
					$('#loading').hide();
				}
			});
		});

$(document).delegate('.deleteReminder', 'click', function(e) {

	e.preventDefault();
	console.log(jQuery(this).closest('.row').attr('id'));
	$('#confirmDeleteReminder').find('#deleteIdReminder').val($(this).closest('.row').attr('id'));
	$('#confirmDeleteReminder').find('.modal-body').find('b').text($(this).closest('.row').find('[name="reminderDate[]"]').val());
	$('#confirmDeleteReminder').modal();
});

$(document).delegate('#appointmentDateTime, #appointmentTime', 'keypress', function(e) {
	e.preventDefault();
	return false;
});

$(document).delegate('#confDelReminder', 'click', function(e) {
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	var confDelReminder = $('#confirmDeleteReminder').find('#deleteIdReminder').val();
	$('.reminderList.marginDisable > .row#' + confDelReminder).remove();
	if ($('.reminderList.marginDisable').find('.row').length == 0) {
		$('.reminderList.marginDisable').html('<div class="row placeHolderBlock"><div class="col-md-12"><p>Add Reminder</p></div></div>');
	}
	if (jQuery(".myreminder").length == 0)
		jQuery(".reminderList .placeHolderBlock").show();

	$('#confirmDeleteReminder').modal('hide');
});

function addZero(num) {
	return (num >= 0 && num < 10) ? "0" + num : num + "";
}

// cancel meeting
$('#cancelMeeting').click(function(e) {
	e.preventDefault();
	if (!$('#cancelMeetingForm').isValid()) {
		return false;
	}
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var meetingId = $('#meetingId').val();
	var cancelReason = $('#cancelMeetingReason').val();
	$.ajax({
		url : getBuyerContextPath('cancelMeeting'),
		data : ({
			meetingId : meetingId,
			cancelReason : cancelReason
		}),
		type : "POST",
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			// $('#cancelMeeting').hide();
			var info = request.getResponseHeader('success');
			$('p[id=idGlobalSuccessMessage]').html(info);
			$('div.ajax-msg[id=idGlobalSuccess]').show();
			$('#loading').hide();
			// $("#cancelMeetingButton").prop("disabled",true);
			// $("#editButton").prop("disabled",true);
			$('#cancelMeetingButton').hide();
			$('#editButton').hide();
			// document.getElementById("cancelMeetingButton").disabled = true;
			// document.getElementById("editButton").disabled = true;
			$('.removeMeetSupplier').addClass('disabled');
			$("#cancelPopUp").dialog("close");
			$('#cancelMeetingReason').val("");
			console.log('<h3><font color="red">' + data + '</font></h3>');
			$('#summaryCancelMeetingButton' + meetingId).hide();
			$('#MeetingAttendanceButton' + meetingId).hide();

			// var meet = $('#cancelMeetingButton'+meetingId).attr('id');
			$('#' + meetingId).html('<h3><font color="red">' + data + '</font></h3>');
			// $('#idMeetStatus').remove();

		},
		error : function(request, textStatus, errorThrown) {
			console.log(JSON.stringify(request));
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
			$("#cancelPopUp").dialog("close");
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

// remove document
$(document).delegate('.removeDocFile', 'click', function(e) {
	e.preventDefault();
	$('#confirmDeleteDocument').find('#deleteIdDocument').val($(this).attr('removeDocId'));
	$('#confirmDeleteDocument').find('#deleteRefIdDocument').val($(this).attr('removeDocRefId'));
	$('#confirmDeleteDocument').modal();
});

$(document).delegate(
		'#confDelDocument',
		'click',
		function(e) {
			e.preventDefault();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();

			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var removeDocId = $('#confirmDeleteDocument').find('#deleteIdDocument').val();
			var removeDocRefId = $('#confirmDeleteDocument').find('#deleteRefIdDocument').val();
			// var prId = $('#prId').val();
			var removeUrl = getBuyerContextPath('removeMeetingDocument');
			$.ajax({
				url : removeUrl,
				data : ({
					removeDocRefId : removeDocRefId,
					removeDocId : removeDocId
				}),
				type : "GET",
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('div[id=idMessageJsp]').html("");
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
					var info = request.getResponseHeader('success');
					console.log(info);
					$('p[id=idGlobalSuccessMessage]').html(info);
					$('div[id=idGlobalSuccess]').show();
					$.jGrowl(info, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					});
					var table = '';
					$.each(data, function(i, item) {
						table += '<tr>' + '<td class="width_200 width_200_fix align-left"><a class="bluelink" href="downloadMeetingDocument?docId=' + item.id + '">' + item.fileName + '</a></td>' + '<td class="width_100 width_100_fix align-left">'
								+ item.fileSize + ' KB </td>' + '<td class="width_100  width_100_fix align-center"><a href=""> <span class="removeDocFile" removeDocRefId="' + item.refId + '" removeDocId="' + item.id
								+ '"> <i class="fa fa-times-circle"></i></span></a></td>' + '</tr>';
					});
					console.log(table);
					$('#rftDocList tbody').html(table);
					$('#loading').hide();
				},
				error : function(request, textStatus, errorThrown) {
					console.log(JSON.stringify(request));
					var error = request.getResponseHeader('error');
					$('p[id=idGlobalErrorMessage]').html(error);
					$('div[id=idGlobalError]').show();

					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
					$('#loading').hide();
				},
				complete : function() {
					$('#confirmDeleteDocument').modal('hide');
					$('#loading').hide();
				}
			});
		});
// cancel meeting popup
$(document).delegate('.cancelMeetingPop', 'click', function(e) {
	e.preventDefault();
	$('#resetEventContctForm').click();
	$('#cancelPopUp').find('#contactId, #id').val('');
	$("#cancelPopUp").dialog({
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
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
	$('#meetingId').val($(this).attr('data-id'));
	/* $('#cancelMeetingReason').val(""); */
});

function onchangeDateOrTime() {

	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	if (!$('#addReminder').isValid()) {
		return false;
	}
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	// var eventStartDate =
	// $.trim($('#demo-form1').find('#idStartEndDate').val());
	var meetingDate = $.trim($('#meetingDate').val());
	var appointmentTime = $.trim($('#appointmentTime').val());
	reminderDur = [];
	reminderType = [];

	$(".myreminder").each(function(key, val) {
		reminderDur.push($(this).children("#idReminderDuration").val());
		reminderType.push($(this).children("#idReminderDurationType").val());
	});
	var meetingData = {
		'meetingDate' : meetingDate,
		'meetingTime' : appointmentTime,
		'eventId' : $('#id').val(),
		'reminderDuration' : reminderDur,
		'reminderDurationType' : reminderType,
	};
	$.ajax({
		url : getBuyerContextPath('manageReminderOfEventMeeting'),
		data : meetingData,
		type : 'POST',
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			var table = '';

			$(".myreminder").remove();
			table = "";
			$.each(data, function(i, item) {
				newID = "myreminder" + i;
				console.log(item.reminderDate);
				table += '<div class="row myreminder" id="' + newID + '">' + '<input type="hidden" id="idReminderDuration" name="reminderDuration[]" class="idReminderDuration" value="' + item.interval + '">'
						+ '<input type="hidden" id="idreminderDateAndTime" name="reminderDateAndTime[]" value="' + item.reminderDate + '">'
						+ '<input type="hidden" id="idReminderDurationType" name="reminderDurationType[]" class="idReminderDurationType" value="' + item.intervalType + '">' + '<div class="col-md-10"><p><b>Reminder Date: </b>' + item.reminderDate
						+ '</p>' + '</div><div class="col-md-2"><a href="" class="deleteReminder" data-remtype=""reminderId="' + newID + '"><i class="fa fa-times-circle"></i></a></div></div>';
			});
			if (table != '') {
				jQuery(".reminderList .placeHolderBlock").hide();
				// table += '<div class="row" id=""><div class="col-md-12"><p>Add Reminder</p></div></div>';
			}

			// currentBlock.closest('.row').next().find('.reminderList.marginDisable').html(table);
			jQuery(".reminderList").append(table);

		},
		error : function(request, textStatus, errorThrown) {
			console.log(request);
			var error = request.getResponseHeader('error');
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
		},
		complete : function() {
			$('#loading').hide();
		}
	});
}

$('#meetingDate').datepicker().on('changeDate', function(e) {
	e.preventDefault();
	onchangeDateOrTime();
});
$('#appointmentTime').timepicker().on('hide.timepicker', function(e) {
	e.preventDefault();
	onchangeDateOrTime();
});

$(document).delegate('.submitMeetingAttandence', 'click', function(e) {
	e.preventDefault();

	var meetingId = $(this).closest('div').attr('meeting-id');
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	var attendanceList = [];
	$('tr.mettingsDataAll').each(function() {
		var dataRow = {};
		$(this).find('input, select').each(function() {
			if ($(this).attr('name') != undefined) {
				console.log($(this).val());
				dataRow[$(this).attr('name')] = $(this).val();
			}
			if (dataRow['attended'] == '1') {
				dataRow['attended'] = true;
			} else {
				dataRow['attended'] = false;
			}
		});
		dataRow['eventId'] = $('#eventId').val();
		console.log(dataRow);
		attendanceList.push(dataRow);
	});
	var postData = JSON.stringify(attendanceList);

	$.ajax({
		url : getBuyerContextPath('saveMeetingAttendance'),
		data : postData,
		type : 'POST',
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Content-Type", "application/json");
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			console.log(request.getResponseHeader('success'));
			$.jGrowl(request.getResponseHeader('success'), {
				sticky : false,
				position : 'top-right',
				theme : 'bg-green'
			});
			var htmlData = '';
			$('.contantDataHtmlData tbody').html(htmlData);
			$("#attendancePopUp").dialog('close');
		},
		error : function(request, textStatus, errorThrown) {
			console.log(request);
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

$(document).delegate('.MeetingAttendancePop', 'click', function(e) {
	e.preventDefault();
	var meetingId = $(this).closest('div').attr('meeting-id');
	var meetingStatus = $(this).closest('div').attr('meeting-status');
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url : getBuyerContextPath('meetingAttendance'),
		data : {
			'meetingId' : meetingId,
			'eventId' : $('#eventId').val()
		},
		type : 'POST',
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			var htmlData = '';
			$(data).each(function(i, atandnce) {
				var id = '';
				if (atandnce.id != undefined) {
					id = atandnce.id;
				}
				var companyName = '';
				if (atandnce.companyName != undefined) {
					companyName = atandnce.companyName;
				}
				var name = '';
				if (atandnce.name != undefined) {
					name = atandnce.name;
				}
				var designation = '';
				if (atandnce.designation != undefined) {
					designation = atandnce.designation;
				}
				var mobileNumber = '';
				if (atandnce.mobileNumber != undefined) {
					mobileNumber = atandnce.mobileNumber;
				}
				var attended = '';
				if (atandnce.attended != undefined) {
					attended = atandnce.attended;
				}
				var remarks = '';
				if (atandnce.remarks != undefined) {
					remarks = atandnce.remarks;
				}
				var cancelReason = '';
				if (atandnce.rejectReason != undefined) {
					cancelReason = atandnce.rejectReason;
				}
				var supplierId = '';
				if (atandnce.supplierId != undefined) {
					supplierId = atandnce.supplierId;
				}
				var meetingId = '';
				if (atandnce.meetingId != undefined) {
					meetingId = atandnce.meetingId;
				}
				var disableEdData = '';
				if (meetingStatus != 'ONGOING') {
					disableEdData = 'disabled readonly';
				}
				if (meetingStatus != 'ONGOING' && (atandnce.meetingAttendanceStatus == 'Rejected' || atandnce.meetingAttendanceStatus == 'Accepted')) {
					disableEdData = 'disabled readonly';
				}
				htmlData += '<tr class="mettingsDataAll ' + disableEdData + '"><td class=" width_200">';
				htmlData += '<span class="pad-top-bottom-10 pull-left ">';
				htmlData += '' + companyName + '</span></td>';
				htmlData += '<td class=" width_200 ">';
				htmlData += '<input class="form-control attandName" type="text" data-validation="required length" data-validation-length="max160" name="name" value="' + name + '" ' + disableEdData + '>';
				htmlData += '<input type="hidden" class="attandSupplierId" name="id" value="' + id + '">';
				htmlData += '<input type="hidden" class="attandSupplierId" name="supplierId" value="' + supplierId + '">';
				htmlData += '<input type="hidden" class="attandMeetingId" name="meetingId" value="' + meetingId + '">';
				htmlData += '</td><td class=" width_200 ">';
				htmlData += '<input class="form-control attandDesignation" type="text" data-validation="required length" name="designation"  data-validation-length="max128" value="' + designation + '" ' + disableEdData + '>';
				htmlData += '</td><td class=" width_200 ">';
				htmlData += '<input class="form-control attandMobileNumber" type="text" data-validation="required length" data-validation-length="max16" name="mobileNumber" value="' + mobileNumber + '" ' + disableEdData + '>';
				htmlData += '</td><td class=" width_200 ">';
				htmlData += '<select class="chosen-select attandAttended" data-validation="required" name="attended" ' + disableEdData + '>';
				if (attended) {
					htmlData += '<option value="1" selected>Yes</option><option value="0">No</option></select>';
				} else {
					htmlData += '<option value="1">Yes</option><option value="0" selected>No</option></select>';
				}
				htmlData += '</td><td class=" width_200 ">';
				if (atandnce.meetingAttendanceStatus === 'Rejected') {
					htmlData += '<input class="form-control attandRemarks" type="text" name="remarks" data-validation="required length" data-validation-length="max500" value="' + cancelReason + '" ' + disableEdData + '></td></tr>';
				} else {
					htmlData += '<input class="form-control attandRemarks" type="text" name="remarks" data-validation="required length" data-validation-length="max500" value="' + remarks + '" ' + disableEdData + '></td></tr>';
				}
			});

			if(meetingStatus == 'SCHEDULED') {
				var submitButton = document.querySelector(".submitMeetingAttandence");
				if (submitButton) {
					submitButton.disabled = true;
					submitButton.setAttribute("readonly", true);
				}
			}
			else {
				var submitButton = document.querySelector(".submitMeetingAttandence");
				if (submitButton) {
					submitButton.disabled = false;
					submitButton.setAttribute("readonly", false);
				}
			}

			$('.contantDataHtmlData tbody').html(htmlData);
			$('.mettingsDataAll .chosen-select').chosen({
				disable_search : true
			});
			$(".mettingsDataAll .chosen-search").append('<i class="glyph-icon icon-search"></i>');
			$(".mettingsDataAll .chosen-single div").html('<i class="glyph-icon icon-caret-down"></i>');
			/*
			 * $("#attMeetingId").val(meetingId); $(".attMeetingId").text(meetingId); $(".attMeetingStatus").text(meetingStatus);
			 */
		if (meetingStatus == 'ONGOING') {
				$('.completMeetingAttandence').show();
			} else {
				$('.completMeetingAttandence').hide();
			}
			$("#attendancePopUp").dialog({
				modal : true,
				minWidth : 300,
				width : '90%',
				maxWidth : 900,
				minHeight : 200,
				dialogClass : "",
				show : "fadeIn",
				draggable : true,
				resizable : false,
				dialogClass : "dialogBlockLoaded"
			});
			$('.contantDataHtmlData').css('margin-top', $('.headerOfTable').height() - 1);
			$('.contantDataHtmlData').css('margin-bottom', '42px');
		},
		error : function(request, textStatus, errorThrown) {
			console.log(request);
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

$(document).delegate('.completMeetingAttandence', 'click', function(e) {
	e.preventDefault();
	$('#attendancePopUpComplete').dialog({
		modal : true,
		minWidth : 300,
		width : '35%',
		maxWidth : 400,
		minHeight : 100,
		dialogClass : "",
		show : "fadeIn",
		draggable : true,
		resizable : false,
		dialogClass : "dialogBlockLoaded"
	});
});
$(document).delegate('.completeNoMeetingAttandence', 'click', function(e) {
	e.preventDefault();
	$('#attendancePopUpComplete').dialog('close');
});
$(document).delegate('.completeYesMeetingAttandence', 'click', function(e) {
	e.preventDefault();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url : getBuyerContextPath('completMeeting'),
		data : {
			'meetingId' : $('.attandMeetingId').val(),
			'eventId' : $('#eventId').val()
		},
		type : 'POST',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			console.log(request.getResponseHeader('success'));
			$.jGrowl(request.getResponseHeader('success'), {
				sticky : false,
				position : 'top-right',
				theme : 'bg-green'
			});
			window.location.href = getBuyerContextPath('eventSummary') + "/" + $('#eventId').val();
		},
		error : function(request, textStatus, errorThrown) {
			console.log(request);
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});
