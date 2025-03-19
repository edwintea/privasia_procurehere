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
			$(document).on("change", "#prUploadDocument", function(event) {
				files = event.target.files;
			});
			$('.mega').on('scroll', function() {
				$(this).find('.header').css('top', $(this).scrollTop());
			});
			$('#prUploadDocument').click(function(e) {
				$('div[id=Load_File-error-dialog]').hide();
			});

			// upload Pr files
			$('#uploadPrDoc').click(
					function(e) {
						e.preventDefault();
						$('div[id=idGlobalError]').hide();
						$('div[id=idGlobalSuccess]').hide();
						if (!$('#prDocumentForm').isValid()) {
							$('div[id=Load_File-error-dialog]').show();
							return false;
						}
						if ($('#prUploadDocument').val().length == 0) {
							$('p[id=idGlobalErrorMessage]').html("Please select upload file.");
							$('div[id=idGlobalError]').show();
							return;
						}
						var header = $("meta[name='_csrf_header']").attr("content");
						var token = $("meta[name='_csrf']").attr("content");

						var file_data = $('#prUploadDocument').prop('files')[0];
						var docDescription = $('#docDescription').val();
						var prId = $('#prId').val();
						var form_data = new FormData();
						form_data.append('file', file_data);
						form_data.append("desc", docDescription);
						form_data.append("prId", prId);
						console.log("prId  ::  " + prId);
						var uploadUrl = getContextPath() + '/buyer/prDocument';
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
								document.getElementById("prUploadDocument").value = "";
								document.getElementById("docDescription").value = "";
								$(".show_name").html('');
								$('p[id=idGlobalSuccessMessage]').html(info);
								$('div[id=idGlobalSuccess]').show();
							//	$('div[id=Load_File-error-dialog]').hide();
								$.jGrowl(info, {
									sticky : false,
									position : 'top-right',
									theme : 'bg-green'
								});

								var table = '';
								$.each(data, function(i, item) {

									var itemdescription = '&nbsp;';
									if (item.description != null) {
										itemdescription = item.description;
									}
									table += '<tr>'+ '<td class="width_200 width_200_fix align-center"><form method="GET">' + '<a id="downloadButton" data-placement="top" title="Download" href="' + getContextPath()
										+ '/buyer/downloadPrDocument/' + item.id + '"><img src="' + getContextPath() 
										+ '/resources/images/download.png"></a>&nbsp;' + '<span><a href="" class="removeDocFile" data-placement="top" title="Delete" removeDocId="' + item.id
										+ '"><img src="' + getContextPath() + '/resources/images/delete.png"></a></span>&nbsp<a href=""><span class="editDocFile" data-placement="top" title="Edit"  editDocId="' + item.id + '" editDocDec="' + itemdescription + '"><img src="' 
										+ getContextPath() + '/resources/images/edit1.png"></span></a></form></td>' 
										+ '<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' + '<td class="width_300 width_300_fix align-left">' 
										+ itemdescription + '</td>'
										+ '<td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td></tr>';
								});
								$('#prDocList tbody').html(table);
								$('#loading').hide();
								$('#prUploadDocument').change();
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
							}
						});
					});
		});

$(document).on("change", "#prUploadDocument", function() {
	$(".show_name").html($(this).val());
});

//Edit Document
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
	if(description == 'undefined'){
		description = "";
	}
	$('#documentDescriptionPopup').find('#docDec').val(description);

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
			var prId = $('#prId').val();
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				type : "POST",
				url : getBuyerContextPath('updateEventDocumentDesc'),
				data : {
					docId : docId,
					docDesc : docDesc,
					prId : prId
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
							itemdescription = item.description;
						}
						table += '<tr>'+ '<td class="width_200 width_200_fix align-center"><form method="GET">' + '<a id="downloadButton" href="' + getContextPath()
							+ '/buyer/downloadPrDocument/' + item.id + '"><img src="' + getContextPath() 
							+ '/resources/images/download.png"></a>&nbsp;' + '<span><a href="" class="removeDocFile" removeDocId="' + item.id
							+ '"><img src="' + getContextPath() + '/resources/images/delete.png"></a></span>&nbsp<a href=""><span class="editDocFile" editDocId="' + item.id + '" editDocDec="' + itemdescription + '"><img src="' 
							+ getContextPath() + '/resources/images/edit1.png"></span></a></form></td>' 
							+ '<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' + '<td class="width_300 width_300_fix align-left">' 
							+ itemdescription + '</td>'
							+ '<td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td></tr>';
					});
					// console.log(table);
					$('#prDocList tbody').html(table);
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
			var prId = $('#prId').val();
			var removeUrl = getContextPath() + '/buyer/removePrDocument';
			$.ajax({
				url : removeUrl,
				data : ({
					removeDocId : removeDocId,
					prId : prId
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
							itemdescription = item.description;
						}
						table += '<tr>'+ '<td class="width_200 width_200_fix align-center"><form method="GET">' + '<a id="downloadButton"  href="' + getContextPath()
							+ '/buyer/downloadPrDocument/' + item.id + '"><img src="' + getContextPath() 
							+ '/resources/images/download.png"></a>&nbsp;' + '<span><a href="" class="removeDocFile" removeDocId="' + item.id
							+ '"><img src="' + getContextPath() + '/resources/images/delete.png"></a></span>&nbsp<a href=""><span class="editDocFile" editDocId="' + item.id + '" editDocDec="' + itemdescription + '"><img src="' 
							+ getContextPath() + '/resources/images/edit1.png"></span></a></form></td>' 
							+ '<td class="width_200 width_200_fix align-left">' + item.fileName + '</td>' + '<td class="width_300 width_300_fix align-left">' 
							+ itemdescription + '</td>'
							+ '<td class="width_200 width_200_fix align-left">' + item.uploadDate + '</td></tr>';
					});
					$('#prDocList tbody').html(table);
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
