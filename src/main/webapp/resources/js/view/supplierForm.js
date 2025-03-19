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
			$('.mega').on('scroll', function() {
				$(this).find('.header').css('top', $(this).scrollTop());
			});

			// upload SuppNotesDoc files
			$('#sendForm').click(function(e) {
						console.log(' sendForm called ');
						e.preventDefault();
						$('div[id=idGlobalError]').hide();
						$('div[id=idGlobalSuccess]').hide();
						//idGlobalSuccess
						$('.text-danger').html('');
						if (!$('#sendFormToSupplier').isValid()) {
							$('div[id=Load_File-error-dialog]').show();
							return false;
						}
						if ($('#idSupplierFormSelect').val()=='') {
							$('#selectFormDiv').after('<p style="margin-bottom:10px;" class="text-danger">Please select form.</p>');
							return;
						}
						var header = $("meta[name='_csrf_header']").attr("content");
						var token = $("meta[name='_csrf']").attr("content");

						var suppId = $('#idSupplier').val();
						var formIdValues=$('#idSupplierFormSelect').val();
					    console.log("supplierId  ::  " + supplierId +" formId:"+formIdValues);
						console.log("make a call to method url");
						var ajaxUrl = getBuyerContextPath('sendFormToSupplier') ;
						var formIdList = [];
							formIdList.push(formIdValues);
						var formIdV = formIdList.toString();
						$.ajax({
							url : ajaxUrl,
							data :{
								'supplierId' : suppId,
								'formIds' :formIdV
							},
							type : "POST",
							beforeSend : function(xhr) {
								$('#loading').show();
								xhr.setRequestHeader(header, token);
							},
							success : function(data, textStatus, request) {
								var info = request.getResponseHeader('success');
								console.log(" *** Success message : ***" + info);
								supplierFormsData1.ajax.reload();
								$('#idSupplierFormSelect').val('').trigger("chosen:updated");
								$(".show_name").html('');
								$('p[id=idGlobalSuccessMessage]').html(info);
								$('div[id=idGlobalSuccess]').show()
								
								// $('.ajax-msg').show();
								$.jGrowl(info, {
									sticky : false,
									position : 'top-right',
									theme : 'bg-green'
								});
								$('#loading').hide();

								
							},
							error : function(request, textStatus, errorThrown) {
								var error = request.getResponseHeader('error');
								console.log("error");
								$('p[id=idGlobalErrorMessage]').html(error);
								$('div[id=idGlobalError]').show();
								// $('.ajax-msg').show();
								$.jGrowl(error, {
									sticky : false,
									position : 'top-right',
									theme : 'bg-red'
								});
								$('#loading').hide();
							},
							complete : function() {
								$('#loading').hide();
							}
						});
					});
		});
			
			

//delete form 
$('#idFormConfirmDelete').click(function(e) {
			console.log(' form delete called ');
			e.preventDefault();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();
			//idGlobalSuccess
			$('.text-danger').html('');
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var formIdValues=$(this).attr("data-id");
			var ajaxUrl = getBuyerContextPath('deleteSuppFormSubmission') ;
			$.ajax({
				url : ajaxUrl,
				data :{
					'formId' : formIdValues
				},
				type : "POST",
				beforeSend : function(xhr) {
					$('#loading').show();
					xhr.setRequestHeader(header, token);
				},
				success : function(data, textStatus, request) {
					var info = request.getResponseHeader('success');
					console.log(" *** Success message : ***" + info);
					supplierFormsData1.ajax.reload();
					$(".show_name").html('');
					$('p[id=idGlobalSuccessMessage]').html(info);
					$('div[id=idGlobalSuccess]').show();
					
					// $('.ajax-msg').show();
					$.jGrowl(info, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					});
					$('#loading').hide();
					$('#mydeleteFormModal').hide();

					
				},
				error : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					console.log("error");
					$('p[id=idGlobalErrorMessage]').html(error);
					$('div[id=idGlobalError]').show();
					// $('.ajax-msg').show();
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
					$('#loading').hide();
					$('#mydeleteFormModal').hide();

				},
				complete : function() {
					$('#loading').hide();
					$('#mydeleteFormModal').hide();
				}
			});
		});