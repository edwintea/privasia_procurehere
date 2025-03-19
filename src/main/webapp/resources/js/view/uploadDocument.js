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

		var files = [];
		$(document).on("change", "#rfsUploadDocument", function(event) {
			files = event.target.files;
		});
		$('.mega').on('scroll', function() {
			$(this).find('.header').css('top', $(this).scrollTop());
		});
		$('#rfsUploadDocument').click(function(e) {
			$('div[id=Load_File-error-dialog]').hide();
		});

		// upload Pr files
		$('#uploadRfsDoc').click(
			function(e) {
				$('div[id=idGlobalError]').hide();
				$('div[id=idGlobalSuccess]').hide();
				if (!$('#rfsDocumentForm').isValid()) {
					$('div[id=Load_File-error-dialog]').show();
					return false;
				}
				if ($('#rfsUploadDocument').val().length == 0) {
					$('p[id=idGlobalErrorMessage]').html("Please select upload file.");
					$('div[id=idGlobalError]').show();
					return;
				}
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");

				var file_data = $('#rfsUploadDocument').prop('files')[0];
				var docDescription = $('#docDescription').val();
				var formId = $('#formId').val();
				var internal = ($(".internal").prop('checked'));
				var eventStatus = $('#status').val();
				var form_data = new FormData();
				form_data.append('file', file_data);
				form_data.append("desc", docDescription);
				form_data.append("formId", formId);
				console.log("formId  ::  " + formId);
				form_data.append("internal", internal);
				var uploadUrl = getContextPath() + '/buyer/rfsDocument';
				$.ajax({
					url : uploadUrl,
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
						$('#loading').show();
						$('.progressbar').removeClass('flagvisibility');
					},
					success : function(data, textStatus, request) {
						var info = request.getResponseHeader('success');
						console.log("Success message : " + info);
						document.getElementById("rfsUploadDocument").value = "";
						document.getElementById("docDescription").value = "";
						$(".show_name").html('');
						$('p[id=idGlobalSuccessMessage]').html(info);
						$('div[id=idGlobalSuccess]').show();
						// $('div[id=Load_File-error-dialog]').hide();
						$.jGrowl(info, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-green'
						});

						var table = '';
						$.each(data, function(i, item) {

							var itemdescription = '&nbsp;';
							if (item.description != null) {
								itemdescription = escapeHtml(item.description);
							}
							if (item.editorMember == true) {
								table += '<tr>' + '<td class="width_200 width_200_fix align-center"><form method="GET">' + '<a id="downloadButton" href="' + getContextPath() + '/buyer/downloadRfsDocument/' + item.id + '"><img src="'
									+ getContextPath() + '/resources/images/download.png"></a>&nbsp;' + '<span><a href="" class="removeDocFile" removeDocId="' + item.id + '"><img src="' + getContextPath()
									+ '/resources/images/delete.png"></a></span>&nbsp' +
									'<a href=""><span class="editDocFile" editDocId="' + item.id + '" editDocDec="' + itemdescription + '" eventDocInternal="' + item.internal + '">' +
									'<img src="' + getContextPath()
									+ '/resources/images/edit1.png"></span></a></form></td>' +
									'<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' +
									'<td class="width_300 width_300_fix align-left">'
									+ itemdescription + '</td>' +
									'<td class="width_100 width_100_fix align-left">' + item.fileSizeInKb + ' KB</td> ' +
									'<td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td>' +
									'<td class="width_200 width_200_fix align-left">' + item.uploadByName + '</td>' +
									'<td class="width_100 width_100_fix align-left">' + (item.internal == true ? "Internal" : "External") +'</td>' +
									'</tr>';
							} else {
								table += '<tr>' + '<td class="width_200 width_200_fix align-center"><form method="GET">' + '<a id="downloadButton" href="' + getContextPath() + '/buyer/downloadRfsDocument/' + item.id + '"><img src="'
									+ getContextPath() + '/resources/images/download.png"></a>&nbsp;'

								if (item.loggedInMember == false && item.approverMember == false) {
									table += '<span><a href="" class="removeDocFile" removeDocId="' + item.id + '">' +
										'<img src="' + getContextPath()
										+ '/resources/images/delete.png"></a></span>&nbsp' +
										'<a href=""><span class="editDocFile" editDocId="' + item.id + '" editDocDec="' + itemdescription + '" eventDocInternal="' + item.internal + '"><img src="' + getContextPath()
										+ '/resources/images/edit1.png"></span></a></form></td>';
								}
								table += '<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' +
									'<td class="width_300 width_300_fix align-left">' + itemdescription + '</td>' +
									'<td class="width_100 width_100_fix align-left">' + item.fileSizeInKb + ' KB</td> ' +
									'<td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td>' +
									'<td class="width_200 width_200_fix align-left">' + item.uploadByName + '</td>' +
									'<td class="width_100 width_100_fix align-left">' + (item.internal == true ? "Internal" : "External") +'</td>' +
									'</tr>';
							}

						});
						$('#prDocList tbody').html(table);
						$('#rfsUploadDocument').change();
						setTimeout(function() {
							if (eventStatus === 'PENDING' || eventStatus === 'APPROVED') {
								window.location.href = getContextPath() + "/buyer/viewSourcingSummary/" + formId;
							} else if (eventStatus === 'DRAFT') {
								window.location.href = getContextPath() + "/buyer/sourcingRequestSummary/" + formId;
							}
						}, 2000); // 2-second delay
					},
					error : function(request, textStatus, errorThrown) {
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
						$('#loading').hide();
						$('.progressbar').addClass('flagvisibility');
						progress(0, $('.progressbar'));
						$('#rfsUploadDocument').modal(hide);
					}
				});
			});
	});

$(document).on("change", "#rfsUploadDocument", function() {
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
	var description = $(this).attr('editDocDec');
	if (description == 'undefined') {
		description = "";
	}
	$('#documentDescriptionPopup').find('#docDec').val(description);
	var isInternal = $(this).attr('eventDocInternal') === 'true';
	$('#documentDescriptionPopup').find('#internal_1').prop('checked', isInternal);
	console.log("Checkbox is popup:", isInternal); // Debug log
	$('.ui-dialog-title').text('Document Description');
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
		var eventStatus = $('#status').val();
		var formId = $('#formId').val();
		if (formId == undefined) {
			formId = $('#rfsId').val();
		}
		var internal_1 = ($(".internal_1").prop('checked'));
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type : "POST",
			url : getBuyerContextPath('updateRfsDocumentDesc'),
			data : {
				docId : docId,
				docDesc : docDesc,
				formId : formId,
				internal: internal_1
			},
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
			},
			success : function(data, textStatus, request) {
				var info = request.getResponseHeader('success');
				$('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
				$('div[id=idGlobalSuccess]').show();
				var table = '';
				$.each(data, function(i, item) {
					var itemdescription = '&nbsp;';
					if (item.description != null) {
						itemdescription = escapeHtml(item.description);
					}
					if (item.editorMember == true) {
						table += '<tr>' + '<td class="width_200 width_200_fix align-center"><form method="GET">' + '<a id="downloadButton" href="' + getContextPath() + '/buyer/downloadRfsDocument/' + item.id + '"><img src="' + getContextPath()
							+ '/resources/images/download.png"></a>&nbsp;' + '<span>' +
							'<a href="" class="removeDocFile" removeDocId="' + item.id + '">' +
							'<img src="' + getContextPath()
							+ '/resources/images/delete.png"></a></span>&nbsp' +
							'<a href=""><span class="editDocFile" editDocId="' + item.id + '" editDocDec="' + itemdescription + '" eventDocInternal="' + item.internal + '">' +
							'<img src="' + getContextPath()
							+ '/resources/images/edit1.png"></span></a></form></td>' +
							'<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' +
							'<td class="width_300 width_300_fix align-left">' + itemdescription + '</td>' +
							'<td class="width_100 width_100_fix align-left">' + item.fileSizeInKb + 'KB</td> ' +
							'<td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td>' +
							'<td class="width_200 width_200_fix align-left">' + item.uploadByName + '</td>' +
							'<td class="width_100 width_100_fix align-left">' + (item.internal == true ? "Internal" : "External") +'</td>' +
							'</tr>';
					} else {
						table += '<tr>' + '<td class="width_200 width_200_fix align-center"><form method="GET">' + '<a id="downloadButton" href="' + getContextPath() + '/buyer/downloadRfsDocument/' + item.id + '"><img src="' + getContextPath()
							+ '/resources/images/download.png"></a>&nbsp;'

						if (item.loggedInMember == false) {
							table += '<span><a href="" class="removeDocFile" removeDocId="' + item.id + '"><img src="' + getContextPath() + '/resources/images/delete.png"></a></span>&nbsp' +
								'<a href=""><span class="editDocFile" editDocId="'
								+ item.id + '" editDocDec="' + itemdescription + '" eventDocInternal="' + item.internal + '"><img src="' + getContextPath() + '/resources/images/edit1.png"></span></a></form></td>';
						}
						table += '<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' +
							'<td class="width_300 width_300_fix align-left">' + itemdescription + '</td>' +
							'<td class="width_100 width_100_fix align-left">' + item.fileSizeInKb + 'KB</td> ' +
							'<td class="width_200 width_200_fix align-left">'
							+ item.uploadDate + '</td>' +
							'<td class="width_200 width_200_fix align-left">' + item.uploadByName + '</td>' +
							'<td class="width_100 width_100_fix align-left">' + (item.internal == true ? "Internal" : "External") +'</td>' +
							'</tr>';
					}
				});
				// console.log(table);
				$('#prDocList tbody').html(table);
				setTimeout(function() {
					if (eventStatus === 'PENDING' || eventStatus === 'APPROVED') {
						window.location.href = getContextPath() + "/buyer/viewSourcingSummary/" + formId;
					} else if (eventStatus === 'DRAFT') {
						window.location.href = getContextPath() + "/buyer/sourcingRequestSummary/" + formId;
					}
				}, 2000); // 2-second delay
			},
			error : function(e) {
				console.log(JSON.stringify(request));
				// $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
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
	$('#confirmDeleteDocument').find('#deleteIdDocument').val($(this).attr('removeDocId'));
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
		var eventStatus = $('#status').val();
		var formId = $('#formId').val();
		if (formId == undefined) {
			formId = $('#rfsId').val();
		}
		var removeUrl = getContextPath() + '/buyer/removeRfsDocument';
		$.ajax({
			url : removeUrl,
			data : ({
				removeDocId : removeDocId,
				formId : formId
			}),
			type : "GET",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var info = request.getResponseHeader('success');
				$('p[id=idGlobalSuccessMessage]').html(info);
				$('div[id=idGlobalSuccess]').show();
				$.jGrowl(info, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-green'
				});
				var table = '';
				$.each(data, function(i, item) {
					var itemdescription = '&nbsp;';
					if (item.description != null) {
						itemdescription = escapeHtml(item.description);
					}
					if (item.editorMember == true) {
						table += '<tr>' + '<td class="width_200 width_200_fix align-center"><form method="GET">' + '<a id="downloadButton" href="' + getContextPath() + '/buyer/downloadRfsDocument/' + item.id + '"><img src="' + getContextPath()
							+ '/resources/images/download.png"></a>&nbsp;' + '<span>' +
							'<a href="" class="removeDocFile" removeDocId="' + item.id + '"><img src="' + getContextPath()
							+ '/resources/images/delete.png"></a></span>&nbsp' +
							'<a href=""><span class="editDocFile" editDocId="' + item.id + '" editDocDec="' + itemdescription + '" eventDocInternal="' + item.internal + '"><img src="' + getContextPath()
							+ '/resources/images/edit1.png"></span></a></form></td>' +
							'<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' +
							'<td class="width_300 width_300_fix align-left">' + itemdescription + '</td>' +
							'<td class="width_100 width_100_fix align-left">' + item.fileSizeInKb + 'KB</td> ' +
							'<td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td>' +
							'<td class="width_200 width_200_fix align-left">' + item.uploadByName + '</td>' +
							'<td class="width_100 width_100_fix align-left">' + (item.internal == true ? "Internal" : "External") +'</td>' +
							'</tr>';
					} else {
						table += '<tr>' + '<td class="width_200 width_200_fix align-center"><form method="GET">' + '<a id="downloadButton" href="' + getContextPath() + '/buyer/downloadRfsDocument/' + item.id + '"><img src="' + getContextPath()
							+ '/resources/images/download.png"></a>&nbsp;'

						if (item.loggedInMember == false) {
							table += '<span><a href="" class="removeDocFile" removeDocId="' + item.id + '"><img src="' + getContextPath() + '/resources/images/delete.png"></a></span>&nbsp' +
								'<a href=""><span class="editDocFile" editDocId="'
								+ item.id + '" editDocDec="' + itemdescription + '" eventDocInternal="' + item.internal + '">' +
								'<img src="' + getContextPath() + '/resources/images/edit1.png"></span></a></form></td>';
						}
						table += '<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' +
							'<td class="width_300 width_300_fix align-left">' + itemdescription + '</td>' +
							'<td class="width_100 width_100_fix align-left">' + item.fileSizeInKb + 'KB</td> ' +
							'<td class="width_200 width_200_fix align-left">'
							+ item.uploadDate + '</td>' +
							'<td class="width_200 width_200_fix align-left">' + item.uploadByName + '</td>' +
							'<td class="width_100 width_100_fix align-left">' + (item.internal == true ? "Internal" : "External") +'</td>' +
							'</tr>';
					}
				});
				$('#prDocList tbody').html(table);
				setTimeout(function() {
					if (eventStatus === 'PENDING' || eventStatus === 'APPROVED') {
						window.location.href = getContextPath() + "/buyer/viewSourcingSummary/" + formId;
					} else if (eventStatus === 'DRAFT') {
						window.location.href = getContextPath() + "/buyer/sourcingRequestSummary/" + formId;
					}
				}, 2000); // 2-second delay
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
function escapeHtml(unsafe) {
	return unsafe
		.replace(/&/g, "&amp;")
		.replace(/</g, "&lt;")
		.replace(/>/g, "&gt;")
		.replace(/"/g, "&quot;")
		.replace(/'/g, "&#039;");
}