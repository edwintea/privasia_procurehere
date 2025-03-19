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

$(document).ready(
		function() {
			$('.mega').on('scroll', function() {
				$('.header').css('top', $(this).scrollTop());
			});
			 $("body").tooltip({ selector: '[data-toggle=tooltip]' });
			var files = [];
			$(document).on("change", "#rftUploadDocument", function(event) {
				files = event.target.files;
			});
			$('.mega').on('scroll', function() {
				$(this).find('.header').css('top', $(this).scrollTop());
			});
			$('#rftUploadDocument').click(function(e) {
				$('div[id=Load_File-error-dialog]').hide();
			});
			// upload RFT files
			$('#uploadRftDoc').click(
					function() {
						$('div[id=idGlobalError]').hide();
						$('div[id=idGlobalSuccess]').hide();
						if (!$('#rftDocumentForm').isValid()) {
							$('div[id=Load_File-error-dialog]').show();
							return false;
						}
						if ($('#rftUploadDocument').val().length == 0) {
							$('p[id=idGlobalErrorMessage]').html("Please select upload file.");
							$('div[id=idGlobalError]').show();
							return;
						}
						var header = $("meta[name='_csrf_header']").attr("content");
						var token = $("meta[name='_csrf']").attr("content");
						
						var file_data = $('#rftUploadDocument').prop('files')[0];
						var docDescription = $('#docDescription').val();
						var eventId = $('#eventId').val();
						var internal = ($(".internal").prop('checked'));
						var form_data = new FormData();
						form_data.append('file', file_data);
						form_data.append("desc", docDescription);
						form_data.append("eventId", eventId);
						form_data.append("internal", internal);
						console.log("eventId  ::  " + eventId);
						$.ajax({
							url : getBuyerContextPath('createEventDocuments'),
							data : form_data,
							cache : false,
							xhr : function() { // custom xhr
								myXhr = $.ajaxSettings.xhr();
								if (myXhr.upload) { // check if upload property
													// exists
									myXhr.upload.addEventListener('progress', updateProgress, false); 
								}
								return myXhr;
							},
							type : "POST",
							enctype : 'multipart/form-data',
							processData : false,
							contentType : false,
							beforeSend : function(xhr) {
								xhr.setRequestHeader(header, token);
								$('#loading').show();
								$('.progressbar').removeClass('flagvisibility');
							},
							success : function(data, textStatus, request) {
								var info = request.getResponseHeader('success');
								
								console.log("Success message : " + info);
								document.getElementById("rftUploadDocument").value = "";
								document.getElementById("docDescription").value = "";
								$(".show_name").html('');
								// $('#uploadRftDoc').removeClass('btn-blue').addClass('btn-gray');
								$('p[id=idGlobalSuccessMessage]').html(info);
								$('div[id=idGlobalSuccess]').show();

								
								//Code below commented to hide pop-up success message which is already displayed using above code.
							/*	$.jGrowl(info, {
									sticky : false,
									position : 'top-right',
									theme : 'bg-green'
								});*/

								var table = '';
								$.each(data, function(i, item) {
									var itemdescription = '&nbsp;';
									if (item.description != null) {
										itemdescription = escapeHtml(item.description);
									}
									var url = getBuyerContextPath('downloadRftDocument') + "/" + item.id;
									table += '<tr> <td class="width_150 width_150_fix align-left">' +
										'<form method="GET"><a id="downloadDocument" href="' + url + '" data-placement="top" title="Download">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
										'<img src="' + getContextPath() + '/resources/images/download.png"></a>&nbsp;' + '<span class="removeDocFile" removeDocId="' + item.id + '">' +
										'<a href=""><img src="' + getContextPath() + '/resources/images/delete.png" data-placement="top" title="Delete"></a></span>&nbsp' +
										'<span class="editDocFile" editDocId="' + item.id + '" eventDocDesc="' + itemdescription + '" eventDocInternal="' + item.internal + '">' +
										'<a href=""><img src="' + getContextPath()
											+ '/resources/images/edit1.png" data-placement="top" title="Edit"></a></span></td>' +
										'<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' +
										'<td class="width_300 width_300_fix align-left">' + itemdescription + '</td>  ' +
										'<td class="width_100 width_100_fix align-left">' + item.fileSizeInKb + 'KB</td>' +
										' <td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td>' +
										'<td class="width_200 width_200_fix align-left">' + item.uploadByName + '</td>' +
										'<td class="width_200 width_200_fix align-left">' + (item.internal == true ? "Internal" : "External") + '</form></td></tr>';
								});
								// console.log(table);
								$('#rftDocList tbody').html(table);
								$('#rftUploadDocument').change();
								// $('#loading').hide();
							},
							error : function(request, textStatus, errorThrown) {
								$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
								$('div[id=idGlobalError]').show();
								$('#loading').hide();
							},
							complete : function() {
								$('#loading').hide();
								$('.progressbar').addClass('flagvisibility');
								$('input[type="checkbox"]').prop('checked' , false);
								progress(0, $('.progressbar'));
							}
						});
					});
		});

$(document).on("change", "#rftUploadDocument", function() {
	$(".show_name").html($(this).val());
});

// Edit Document
$(document).delegate('.editDocFile', 'click', function(e) {
	e.preventDefault();

	$("#documentDescriptionPopup").dialog({
		modal : true,
		minWidth : 300,
		width : '50%',
		maxWidth : 400,
		minHeight : 150,
		dialogClass : "",
		show : "fadeIn",
		draggable : true,
		resizable : false,
		dialogClass : "dialogBlockLoaded"
	});
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
	$('#documentDescriptionPopup').find('#editIdDocument').val($(this).attr('editDocId'));
	$('#documentDescriptionPopup').find('#docDec').val($(this).attr('eventDocDesc'));
	var isInternal = $(this).attr('eventDocInternal') === 'true';
	$('#documentDescriptionPopup').find('#internal_1').prop('checked', isInternal);
	console.log("Checkbox is popup:", isInternal); // Debug log
	$('.ui-dialog-title').text(documentDescriptionLabel);
});

$(document).delegate(
		'#confDocDec',
		'click',
		function(e) {
			e.preventDefault();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();
			var docId = $("#documentDescriptionPopup").find('#editIdDocument').val();
			var docDesc = $("#documentDescriptionPopup").find('#docDec').val();
			var eventId = $('#eventId').val();
			var internal_1 = ($(".internal_1").prop('checked'));
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				type : "POST",
				url : getBuyerContextPath('updateEventDocumentDesc'),
				data : {
					docId : docId,
					docDesc : docDesc,
					eventId : eventId,
					internal: internal_1
				},
				beforeSend : function(xhr) {
					$('#loading').show();
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
				},
				success : function(data, textStatus, request) {
					var info = request.getResponseHeader('success');
					$('p[id=idGlobalSuccessMessage]').html(info);
					$('div[id=idGlobalSuccess]').show();
					var table = '';
					$.each(data, function(i, item) {
						var itemdescription = '&nbsp;';
						if (item.description != null) {
							itemdescription = escapeHtml(item.description);
						}
						var url = getBuyerContextPath('downloadRftDocument') + "/" + item.id;
						table += '<tr> <td class="width_150 width_150_fix"><form method="GET">' +
							'<a id="downloadDocument" href="' + url + '"  data-placement="top" title="Download">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
							'<img src="' + getContextPath() + '/resources/images/download.png"></a>&nbsp;' + '<span class="removeDocFile" removeDocId="' + item.id + '">' +
							'<a href=""  data-placement="top" title="Delete"><img src="' + getContextPath() + '/resources/images/delete.png"></a></span>&nbsp' +
							'<span class="editDocFile" editDocId="' + item.id + '" eventDocDesc="' + itemdescription + '" eventDocInternal="' + item.internal + '">' +
							'<a href="" data-placement="top" title="Edit"><img src="' + getContextPath()
								+ '/resources/images/edit1.png"></a></span></td>' +
							'<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' +
							'<td class="width_300 width_300_fix align-left">' + itemdescription + '</td>' +
							' <td class="width_100 width_100_fix align-left">' + item.fileSizeInKb + 'KB</td> ' +
							'<td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td>' +
							'<td class="width_200 width_200_fix align-left">' + item.uploadByName + '</td>' +
							'<td class="width_200 width_200_fix align-left">' + (item.internal == true ? "Internal" : "External") + '</form></td></tr>';
					});
					// console.log(table);
					$('#rftDocList tbody').html(table);
				},
				error : function(e) {
					console.log(JSON.stringify(request));
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					$('#loading').hide();
				},
				complete : function() {
					$("#documentDescriptionPopup").dialog('close');
					$('#loading').hide();
				}
			});
		});

$(document).delegate('.removeDocFile', 'click', function(e) {
	e.preventDefault();
	$('#confirmDeleteDocuemnt').find('#deleteIdDocument').val($(this).attr('removeDocId'));
	$('#confirmDeleteDocuemnt').modal();
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
			var removeDocId = $('#confirmDeleteDocuemnt').find('#deleteIdDocument').val();
			var eventId = $('#eventId').val();

			$.ajax({
				url : getBuyerContextPath('removeRftDocument'),
				data : ({
					removeDocId : removeDocId,
					eventId : eventId
				}),
				type : "GET",
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
					var info = request.getResponseHeader('error');
					$('p[id=idGlobalSuccessMessage]').html(info);
					$('div[id=idGlobalSuccess]').show();
					var table = '';
					$.each(data, function(i, item) {
						var itemdescription = '&nbsp;';
						if (item.description != null) {
							itemdescription = escapeHtml(item.description);
						}
						var url = getBuyerContextPath('downloadRftDocument') + "/" + item.id;
						table += '<tr> <td class="width_150 width_150_fix align-left">' +
							'<form method="GET"><a id="downloadDocument" href="' + url + '" data-placement="top" title="Download">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
							'<img src="' + getContextPath() + '/resources/images/download.png"></a>&nbsp;' + '<span class="removeDocFile" removeDocId="' + item.id + '">' +
							'<a href=""  title="Delete"><img src="' + getContextPath() + '/resources/images/delete.png"></a></span>&nbsp' +
							'<span class="editDocFile" editDocId="' + item.id + '" eventDocDesc="' + itemdescription + '" eventDocInternal="' + item.internal + '">' +
							'<a href="" data-placement="top" title="Edit"><img src="' + getContextPath()
								+ '/resources/images/edit1.png"></a></span></td>' +
							'<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' +
							'<td class="width_300 width_300_fix align-left">' + itemdescription + '</td> ' +
							'<td class="width_100 width_100_fix align-left">' + item.fileSizeInKb + 'KB</form></td>' +
							' <td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td>' +
							'<td class="width_200 width_200_fix align-left">' + item.uploadByName + '</td>' +
							'<td class="width_200 width_200_fix align-left">' + (item.internal == true ? "Internal" : "External") + '</form></td></tr>';
					});
					// console.log(table);
					$('#rftDocList tbody').html(table);
				},
				error : function(request, textStatus, errorThrown) {
					console.log(JSON.stringify(request));
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('div[id=idGlobalError]').show();
					$('#loading').hide();
				},
				complete : function() {
					$('#confirmDeleteDocuemnt').modal('hide');
					$('#loading').hide();
				}
			});
		});

function escapeHtml(unsafe) {
	return unsafe
		.replace(/&/g, "&amp;")
		.replace(/</g, "&lt;")
		.replace(/>/g, "&gt;")
		.replace(/"/g, "&quot;")
		.replace(/'/g, "&#039;");
}