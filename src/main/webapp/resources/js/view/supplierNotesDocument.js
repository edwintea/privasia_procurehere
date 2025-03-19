function updateProgress(evt) {
	if (evt.lengthComputable) {
		var percentComplete = Math.ceil((evt.loaded / evt.total) * 100);
		progress(percentComplete, $('.progressbar'));
		var percentVal = percentComplete + '%';
		//   console.log(percentVal);
	} else {
		// Unable to compute progress information since the total size is
		// unknown
		//   console.log('unable to complete');
	}
}

$(document).ready(
		function() {
			$('.mega').on('scroll', function() {
				$('.header').css('top', $(this).scrollTop());
			});

			var files = [];
			$(document).on("change", "#supNotesUploadDocument", function(event) {
				files = event.target.files;
			});
			$('.mega').on('scroll', function() {
				$(this).find('.header').css('top', $(this).scrollTop());
			});
			$('#supNotesUploadDocument').click(function(e) {
				$('div[id=Load_File-error-dialog]').hide();
				$('.text-danger').html('');
			});

			// upload SuppNotesDoc files
			$('#uploadSuppNotesDoc').click(
					function(e) {
						console.log(' uploadSuppNotesDoc called ');
						e.preventDefault();
						$('div[id=idGlobalError]').hide();
						$('div[id=idGlobalSuccess]').hide();
						//idGlobalSuccess
						$('.text-danger').html('');
						if (!$('#NotesDocForm').isValid()) {
							$('div[id=Load_File-error-dialog]').show();
							return false;
						}
						if ($('#supNotesUploadDocument').val().length == 0) {
//							$('p[id=idGlobalErrorMessage]').html("Please select upload file.");
//							$('div[id=idGlobalError]').show();
//							$('div[id=Load_File-error-dialog]').show();
							$('.fileinput-new.input-group').after('<p style="margin-bottom:10px;" class="text-danger">Please select upload file.</p>');
							return;
						}
						var header = $("meta[name='_csrf_header']").attr("content");
						var token = $("meta[name='_csrf']").attr("content");

						var file_data = $('#supNotesUploadDocument').prop('files')[0];
						var docDescription = $('#docDescription').val();
						var supplierId = $('#idSupplier').val();
						
						
						var visible = $("#visibleDocument").val();
						if (visible === undefined){
							visible = true;  
						 }
						console.log(file_data+":file_data & docDescription:" +docDescription);
					    console.log("supplierId  ::  " + supplierId);
						
						var form_data = new FormData();
						form_data.append('file', file_data);
						form_data.append("desc", docDescription);
						form_data.append("supplierId", supplierId);
						form_data.append("visible", visible);
						console.log("visible ::  " + visible);
						console.log("make a call to method url");
						var uploadUrl = getContextPath() + '/supplierreg/suppNotesUploadDocument';
						$.ajax({
							url : uploadUrl,
							data : form_data,
							cache : false,
							xhr : function() { 
								// custom xhr
								myXhr = $.ajaxSettings.xhr();
								// check if upload property exists
								if (myXhr.upload) { 
								//	 for handling the progress of the upload				
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
								console.log(" *** Success message : ***" + info);
								
								document.getElementById("supNotesUploadDocument").value = "";
								document.getElementById("docDescription").value = "";
								
								
//								dataTableCallForSupplierNotesDoc();
								supplierNoteDocTable.ajax.reload();
								
								$(".show_name").html('');
								$('p[id=idGlobalSuccessMessage]').html(info);
								$('div[id=idGlobalSuccess]').show();
								
//								$('div[id=idGlobalSuccess]').show();
							//	$('div[id=Load_File-error-dialog]').hide();
//								$('.ajax-msg').show();
								$.jGrowl(info, {
									sticky : false,
									position : 'top-right',
									theme : 'bg-green'
								});
								$('#loading').hide();

								
								$('#supNotesUploadDocument').change();
							},
							error : function(request, textStatus, errorThrown) {
								var error = request.getResponseHeader('error');
								$('p[id=idGlobalErrorMessage]').html(error);
								$('div[id=idGlobalError]').show();
								//$('.ajax-msg').show();
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
			
			$(document).delegate('.removeNoteSuppDocFile', 'click', function(e) {
				e.preventDefault();
				
				$('#confDelSuppNotesDocument').find('#deleteIdDocument').val($(this).attr('removeDocId'));
				console.log('removeNoteSuppDocFile called');
				$('#confDelSuppNotesDocument').modal();
			});

		$(document).delegate('#confDelDocument', 'click', function(e) {
						e.preventDefault();
						$('div[id=idGlobalError]').hide();
						$('div[id=idGlobalSuccess]').hide();

						var header = $("meta[name='_csrf_header']").attr("content");
						var token = $("meta[name='_csrf']").attr("content");
						console.log('confDelDocument called');
						var removeDocId = $('#confDelSuppNotesDocument').find('#deleteIdDocument').val();
						var supplierId = $('#supplierId').val();
						console.log(supplierId+': supplierId & removeDocId :'+removeDocId);
						var removeUrl = getContextPath() + '/supplierreg/removeSuppNotesDocument'+'/'+removeDocId;
						console.log(removeUrl +" : removeUrl");
						$.ajax({
							url : removeUrl,
							data : ({
								removeDocId : removeDocId,
								supplierId : supplierId
							}),
							type : "GET",
							beforeSend : function(xhr) {
								xhr.setRequestHeader(header, token);
								$('#loading').show();
							},
							success : function(data, textStatus, request) {
								var info = request.getResponseHeader('success');
								console.log('info :'+ info);
//								location.reload();

								//reload only datatable 
								supplierNoteDocTable.ajax.reload();
								
								$(".show_name").html('');
								$('p[id=idGlobalSuccessMessage]').html(info);
								
								$('div[id=idGlobalSuccess]').show();
							//	$('div[id=Load_File-error-dialog]').hide();
								//$('.ajax-msg').show();
								
								$.jGrowl(info, {
									sticky : false,
									position : 'top-right',
									theme : 'bg-green'
								});
							
								$('#loading').hide();
							},
							error : function(request, textStatus, errorThrown) {
								//   console.log(JSON.stringify(request));
								var error = request.getResponseHeader('error');
								console.log(" error :"+ error);
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
								$('#confDelSuppNotesDocument').modal('hide');
								$('#loading').hide();
							}
						});
					});
//});
			// Edit SuppNotes Document
			$(document).delegate('.editNoteSuppDocFile', 'click', function(e) {
				e.preventDefault();
				var id = $(this).attr('data-id');
				console.log(' id :'+ id);
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
				$('#documentDescriptionPopup').find('#editIdDocument').val($(this).attr('data-id'));
				var description = $(this).attr('data-desc');
				console.log( " description :"+ description )
				if(description == 'undefined'){
					description = "";
				}
				$('#documentDescriptionPopup').find('#docDec').val(description);
				$('.ui-dialog-title').text('Document Description');
			});

			$(document).delegate('#confUpdSuppNotesDoc', 'click', function(e) {
						e.preventDefault();
						console.log(" *** confUpdSuppNotesDoc called ***");
						$('div[id=idGlobalError]').hide();
						$('div[id=idGlobalSuccess]').hide();
						var docId = $("#documentDescriptionPopup").find('#editIdDocument').val();
						var docDesc = $("#documentDescriptionPopup").find('#docDec').val();
						var supplierId = $('#supplierId').val();
						console.log(docDesc +": docDesc & docId :"+ docId );
						console.log( "  supplierId : "+ supplierId );
						var header = $("meta[name='_csrf_header']").attr("content");
						var token = $("meta[name='_csrf']").attr("content");
						$.ajax({
							type : "GET",
							url : getContextPath() +'/supplierreg/updateSuppNotesDocumentDesc'+'/'+ docId,
							data : {
								docId : docId,
								docDesc : docDesc,
								supplierId : supplierId
							},
							beforeSend : function(xhr) {
								$('#loading').show();
								xhr.setRequestHeader(header, token);
								xhr.setRequestHeader("Accept", "application/json");
							},
							success : function(data, textStatus, request) {
								var info = request.getResponseHeader('success');
								console.log(' info :'+ info);
								
//								location.reload();
								//reload only datatable 
								supplierNoteDocTable.ajax.reload();
								
								$(".show_name").html('');
								$('p[id=idGlobalSuccessMessage]').html(info);
								
							  $('div[id=idGlobalSuccess]').show();
							//	$('div[id=Load_File-error-dialog]').hide();
							// 	$('.ajax-msg').show();
							
								$.jGrowl(info, {
									sticky : false,
									position : 'top-right',
									theme : 'bg-green'
								});
								
								$('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
								$('div[id=idGlobalSuccess]').show();
								$('#loading').hide();
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
			