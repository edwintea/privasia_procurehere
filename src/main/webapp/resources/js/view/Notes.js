jQuery(document).ready(
		function($) {
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");

			$('#saveNotes').click(function(event) {
				event.preventDefault();

				if (!$('#noteForm').isValid()) {
					return false;
				}
				$('div[id=idGlobalError]').hide();
				$('div[id=idGlobalSuccess]').hide();

				var incidentType = $('.incidentType').val();
				var description = $('#description').val();
				var supplierId = $('#idSupplier').val();
				var length = $('#tableList_info').val();

				var data = {};
				data["incidentType"] = incidentType;
				data["description"] = description;
				data["id"] = supplierId;
				// console.log(token);
				$.ajax({
					type : "POST",
					contentType : "application/json",
					url : getContextPath() + "/supplierreg/saveNote",
					data : JSON.stringify(data),
					dataType : 'json',

					beforeSend : function(xhr) {
						xhr.setRequestHeader(header, token);
						xhr.setRequestHeader("Accept", "application/json");
						xhr.setRequestHeader("Content-Type", "application/json");
						$('#loading').show();
					},
					success : function(data, textStatus, request) {
						var info = request.getResponseHeader('info');
						console.log("Success message : " + info);
						$('p[id=idGlobalSuccessMessage]').html(info != null ? info.split(",").join("<br/>") : "");
						$('div[id=idGlobalSuccess]').show();
						var table = [];
						$.each(data, function(i, item) {
							table.push({
								"createdDate" : item.createdDate,
								"description" : item.description,
								"incidentType" : item.incidentType
							});
						});
						$('#tableList').DataTable().clear();
						$('#tableList').DataTable().rows.add(table).draw();
						$('#description').val('');
					},
					error : function(request, textStatus, errorThrown) {
						console.log("Error  :: " + request.getResponseHeader('error'));
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
						$('#loading').hide();
					},
					complete : function() {
						$('#loading').hide();
					}
				});
			});

			var files = [];
			$(document).on("change", "#otherDocumentUpload", function(event) {
				files = event.target.files;
			});
			$('.mega').on('scroll', function() {
				$(this).find('.header').css('top', $(this).scrollTop());
			});
			
			$('#otherDocumentUpload').click(function(e) {
				$('div[id=Load_File-error-dialog]').hide();
			});
			

			// upload Other files
			$('#OtherDocsUpload').click(
					function() {
						$('div[id=idGlobalError]').hide();
						$('div[id=idGlobalSuccess]').hide();

						if (!$('#otherDocumentUploadForm').isValid()) {
							return false;
						}

						if ($('#otherDocumentUpload').val().length == 0) {
							$('p[id=idGlobalErrorMessage]').html("Please select upload file.");
							$('div[id=idGlobalError]').show();
							return;
						}
						var header = $("meta[name='_csrf_header']").attr("content");
						var token = $("meta[name='_csrf']").attr("content");

						var file_data = $('#otherDocumentUpload').prop('files')[0];
						var docDescription = $('#otherDocumentDesc').val();
						var expiryDate = $('#expiryDate').val();
						var supplierId = $('#idSupplier').val();
						var form_data = new FormData();
						form_data.append('file', file_data);
						form_data.append("desc", docDescription);
						form_data.append("expiryDate", expiryDate);
						form_data.append("supplierId", supplierId);

						$.ajax({
							url : getContextPath() + "/supplierreg/otherDocumentUpload",
							data : form_data,
							type : "POST",
							enctype : 'multipart/form-data',
							processData : false,
							contentType : false,
							beforeSend : function(xhr) {
								xhr.setRequestHeader(header, token);
								$('#loading').show();
							},
							success : function(data, textStatus, request) {
								var info = request.getResponseHeader('error');
								console.log("Success message : " + info);
								$('#otherDocumentDesc').val("");
								$('#expiryDate').val("");
								document.getElementById("otherDocumentUpload").value = "";
								$('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
								$('div[id=idGlobalSuccess]').show();
								$('span[id=fileinput-exists]').remove();
								$('span[id=idOtherFileUploadSpan]').text('');
								$('.glyphicon').remove();
								$('#remove').remove();
								$('span[id=selectNew]').show();
								
								var table = '';
								$.each(data, function(i, item) {
									var itemdescription = '&nbsp;';
									if (item.description != null) {
										itemdescription = item.description;
									}
									var itemexpiryDate = '&nbsp;';
									if (item.expiryDate === undefined) {
										itemexpiryDate = '&nbsp;';
									}
									else{
										itemexpiryDate = item.expiryDate;
									}
									table += '<tr>' 
										+ '<td class="align-center width_100_fix" ><span class="removeOtherFile" removeOtherId="' + item.id
										+ '" otherDocFile="' + item.fileName + '"><a href=""><i class="fa fa-trash-o" aria-hidden="true"></a></i></span></td>' 
										+ '<td class="align-left width_200"><form:form method="GET">' + '<a href="' + getContextPath() + '/supplierreg/downloadOtherDocument/' + item.id + '">' + item.fileName + '</a>' + '<form:form>' + '</td>' 
										+ '<td class="align-left width_200">' + itemdescription + '</td>' 
										+  '<td class="align-left width_200_fix">' + item.uploadDate + '</td>'  
										+  '<td class="align-left width_200_fix">' + itemexpiryDate + '</td>' 
									    + '</tr>';
								});
								// console.log(table);
								$('#uploadOtherFiless tbody').html(table);

							},
							error : function(request, textStatus, errorThrown) {
								$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
								$('div[id=idGlobalError]').show();
								$('#loading').hide();
							},
							complete : function() {
								$('#loading').hide();
							}
						});

					});

			
			
			
			
			/* Remove Other Document*/
			
			$(document).on("click", ".removeOtherFile", function(e) {
				$('#myModalDelOtherDocConfirm').modal();
				$('#deleteOtherDocId').val($(this).attr('removeOtherId'));
				$('#otherDocFileName').val($(this).attr('otherDocFile'));
				e.preventDefault();

			});
			
			$(document).delegate('#idConfirmDeleteOtherDocument', 'click', function(e) {
			    e.preventDefault();
			    $('div[id=idGlobalError]').hide();
			    $('div[id=idGlobalSuccess]').hide();

			    var header = $("meta[name='_csrf_header']").attr("content");
			    var token = $("meta[name='_csrf']").attr("content");
			    var removeOtherDocId = $('#deleteOtherDocId').val();
			    var otherDocFile = $('#otherDocFileName').val();
			    var supplierId = $('#idSupplier').val();
			    $('#myModalDelOtherDocConfirm').modal('hide');
			    console.log("Profile Id : " + otherDocFile);

			    $.ajax({
			        url: getContextPath() + "/supplierreg/removeOtherDocument",
			        data: {
			        	removeOtherDocId: removeOtherDocId, 
			            otherDocFile: otherDocFile,
			            supplierId: supplierId
			        },
			        type: "GET",
			        beforeSend: function(xhr) {
			            xhr.setRequestHeader(header, token);
			            $('#loading').show();
			        },
			        success: function(data, textStatus, request) {
			            var info = request.getResponseHeader('error');
			            $('p[id=idGlobalSuccessMessage]').html(info.split(",").join("<br/>"));
			            $('div[id=idGlobalSuccess]').show();
			            var table = '';
						$.each(data, function(i, item) {
							var itemdescription = '&nbsp;';
							if (item.description != null) {
								itemdescription = item.description;
							}
							var itemexpiryDate = '&nbsp;';
							if (item.expiryDate === undefined) {
								itemexpiryDate = '&nbsp;';
							}
							else{
								itemexpiryDate = item.expiryDate;
							}
							table += '<tr>' 
							+ '<td class="align-center width_100_fix" ><span class="removeOtherFile" removeOtherId="' + item.id
							+ '" otherDocFile="' + item.fileName + '"><a href=""><i class="fa fa-trash-o" aria-hidden="true"></a></i></span></td>' 
							+ '<td class="align-left width_200"><form:form method="GET">' + '<a href="' + getContextPath() + '/supplierreg/downloadOtherDocument/' + item.id + '">' + item.fileName + '</a>' + '<form:form>' + '</td>' 
							+ '<td class="align-left width_200">' + itemdescription + '</td>' 
							+  '<td class="align-left width_200_fix">' + item.uploadDate + '</td>'  
							+  '<td class="align-left width_200_fix">' + itemexpiryDate + '</td>' 
						    + '</tr>';
						});			            //console.log(table);
			            $('#uploadOtherFiless tbody').html(table);
			        },
			        error: function(request, textStatus, errorThrown) {
			            console.log(JSON.stringify(request));
			            $('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			            $('div[id=idGlobalError]').show();
			            $('#loading').hide();
			        },
			        complete: function() {
			            $('#loading').hide();
			        }
			    });

			});	
			
		});
