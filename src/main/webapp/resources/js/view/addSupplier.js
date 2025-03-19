$(document).ready(function() {

	var supplierSearchtable = $('#tableList2 ').DataTable({
		"serverSide" : false,
		"searching" : false,
		"ordering" : false,

		"ajax" : {
			"type" : 'POST',
			"url" : getContextPath() + '/buyer/searchCountryAndState',
			"data" : function(d) {
				d.country = $.trim($('#registrationOfCountry').val());
				d.state = $.trim($('#stateList').val());
				d.coverage = $.trim($('#coverageList').val());
				d.eventType = $.trim($('#eventTypeSearch').val());
				d.eventId = $.trim($('#eventId').val());
				d.supplierTagName = $.trim($('#supplierTagName').val());
				d.inclusive = ($("#access_inclusive").prop('checked'));
				d.exclusive = ($("#access_Exclusive").prop('checked'))
			},
		},
		"order" : [],
		"columns" : [ {
			"data" : "id",
			'orderable' : false,
			"className" : "align-left vert-center pad-left-20",
			'render' : function(data, type, row) {
				return '<input type="checkbox" class="custom-checkbox1" value="' + row.id + '" id="select-all" name="select-all">';
			}
		}, {
			"className" : "align-left vert-center pad-left-20",
			"data" : "companyName"
		}, {
			"className" : "align-left vert-center pad-left-20",
			"data" : "communicationEmail"
		}, {
			"className" : "align-left vert-center pad-left-20",
			"data" : "companyContactNumber"
		} ]
	});
	
	$('#example-select-all').on('click', function(){
	      var rows = supplierSearchtable.rows({ 'search': 'applied' }).nodes();
	      $('input[type="checkbox"]', rows).prop('checked', this.checked);
	   });
	
	$(document).on("click", ".searchCatAndState", function(e) {
		event.preventDefault();
		$("#idGlobalInfo").hide();
		$("#idGlobalError").hide();
		$("#idGlobalWarn").hide();
		$("#idEventInfo").hide();

		var country = $.trim($('#registrationOfCountry').val());
		var state = $.trim($('#stateList').val());
		var coverage = $.trim($('#coverageList').val());
		var eventType = $.trim($('#eventTypeSearch').val());
		var eventId = $.trim($('#eventId').val());
		supplierSearchtable.ajax.reload();
	});

	var eventStatus = $('#supplierForm').find('#eventStatus').val();
	
	$(document).delegate('.chosenCategoryAll.chosen-select', "chosen:hiding_dropdown", function(e) {
/*		if ($('#chosenCategoryAll').children('option').length > 1) {
			return false;
		}
*/		
		$('#chosenCategoryAll_chosen .chosen-search input').val('');
		reloadSupplierList();
	});


	$('.cancelEvent').click(function(e) {
		e.preventDefault();
		$('#confirmCancelEvent').modal('show');
		return false;
	});
	$(document).on("click", ".submitCompanyName", function(e) {
		e.preventDefault();
		$("#idGlobalError").hide();
		$("#idGlobalSuccess").hide();

		var vId = $.trim($("#chosenCategoryAll").val());
		var eventId = $.trim($("#eventId").val());

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		if (vId.length > 2) {
			$.ajax({
				url : getBuyerContextPath('addCurrentSuppliers'),
				data : {
					'addSupplier' : vId,
					'eventId' : eventId
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
					var success = request.getResponseHeader('success');
					var table = [];
					$.each(data, function(i, item) {
						var deleteData = "";
						if (eventStatus === "DRAFT") {
							deleteData = '<a href="" data-id="' + item.id + '" data-suppId="' + item.supplier.id + '" class="deleteSupplier" title="Delete"><img src="' + getContextPath() + '/resources/images/delete1.png"></a>';
						}
						table.push([ deleteData, item.supplierCompanyName, item.supplier.communicationEmail, item.supplier.companyContactNumber ]);
					});
					$('#tableList1').DataTable().rows().remove().draw();
					$('#tableList1').DataTable().destroy();
					$('#tableList1').DataTable().rows.add(table).draw();
					if (success != null) {
						$("#idGlobalSuccessMessage").html(success);
						$("#idGlobalSuccess").show();
						$("#idGlobalError").hide();

 					}
					$("#chosenCategoryAll option[value='" + vId + "']").remove();
					$("#chosenCategoryAll").trigger("chosen:updated");
					$('#chosenCategoryAll_chosen .chosen-search input').val('');
					supplierDataTable.ajax.reload();
					reloadSupplierList();
					$("#addMoreSupplier").addClass("disabled", true);
					$("#addMore").removeAttr("title");
				},
				error : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('errors');
					if (error != null) {
						$("#idGlobalErrorMessage").html(error);
						$("#idGlobalError").show();
						$("#idGlobalSuccess").hide();
						// showMessage('ERROR', error);
					}
				},
				complete : function() {
					$('#select-all').prop('checked', false);
					if($("#supplierDataTable .selectedSupplier:checked").length == 0) {
						$('#deleteSuppliers').addClass("disabled");
					}
					$('#loading').hide();
				}
			});
		} else {
			$('#industryCategoryList').hide();
		}
	});
	$(document).on("click", "#idConfirmDeleteSupplier", function(e) {
		e.preventDefault();
		var dataId = $('#deleteSupplierId').val();
		var eventId = $.trim($("#eventId").val());
		var favSupp = $.trim($(this).val());
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$('#myModalDelConfirm').modal('hide');
		console.log("ID : " + dataId);
		$.ajax({
			url : getBuyerContextPath('deleteRftSupplier'),
			data : {
				'deleteSupplier' : dataId,
				'eventId' : eventId
			},
			type : 'POST',
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var success = request.getResponseHeader('success');

				$("#idGlobalSuccessMessage").html(success);
				$("#idGlobalSuccess").show();
				$("#idGlobalError").hide();
				/*
				 * $.jGrowl(success, { sticky : false, position : 'top-right', theme : 'bg-green' });
				 */
				var table = [];
				$('#tableList1').DataTable().rows().remove().draw();
				$('#tableList1').DataTable().destroy();

				/*
				 * var table = []; $('#tableList1').DataTable().rows().remove().draw(); $('#tableList1').DataTable().destroy();
				 * 
				 * $.each(data, function(i, item) { console.log("Item " + item.supplier.companyName); var deleteData = ""; if (eventStatus ===
				 * "DRAFT") { deleteData = '<a href="" data-id="' + item.id + '" class="deleteSupplier" title="Delete"><img src="' +
				 * getContextPath() + '/resources/images/delete1.png"></a>'; } table.push([ deleteData, item.supplier.companyName,
				 * item.supplier.communicationEmail, item.supplier.companyContactNumber ]); });
				 * 
				 * $('#tableList1').DataTable().rows.add(table).draw();
				 */

				$("#idGlobalSuccessMessage").html(success.replace(/,/g, "<br/>"));
				$("#idGlobalSuccess").show();
				$("#idGlobalError").hide();

				

			},
			error : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('errors');
				if (error != null) {
					$("#idGlobalErrorMessage").html(error.replace(/,/g, "<br/>"));
					$("#idGlobalError").show();
					$("#idGlobalSuccess").hide();
				}
			},
			complete : function() {
				supplierDataTable.ajax.reload();
				reloadSupplierList();
				$('#loading').hide();
			}
		});
	});

	/**
	 * 
	 */
	$(document).on("click", ".deleteSupplier", function(e) {
		$('#myModalDelConfirm').modal();
		$('#deleteSupplierId').val($(this).attr('data-id'));
		$('#supplierId').val($(this).attr('data-suppId'));
		e.preventDefault();

	});

	$(document).on("keyup", "#chosenCategoryAll_chosen .chosen-search input", keyDebounceDelay(function(e) {
		// ignore arrow keys
		switch (e.keyCode) {
		case 17: // CTRL
			return false;
			break;
		case 18: // ALT
			return false;
			break;
		case 37:
			return false;
			break;
		case 38:
			return false;
			break;
		case 39:
			return false;
			break;
		case 40:
			return false;
			break;
		}		
		  var favSupp = $.trim(this.value);
		  if (favSupp.length > 2 || favSupp.length == 0 || e.keyCode == 8) {
			  reloadSupplierList();
		  }
	}, 650));

	
});

function reloadSupplierList() {

	var favSupp = $.trim($('#chosenCategoryAll_chosen .chosen-search input').val());
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var eventId = $('#eventId').val();
	$.ajax({
		url : getBuyerContextPath('searchCurrentSuppliers'),
		data : {
			'searchSupplier' : favSupp,
			'eventId' : eventId
		},
		type : 'POST',
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data) {
			var html = '';
			if (data != '' && data != null && data.length > 0) {
				if ($('#chosenCategoryAll').find('option:first').val() === '') {
					console.log('Clearing all except first...');

					$('#chosenCategoryAll').find('option').each(function() {
						if (this.value == '' || this.value === $('#chosenCategoryAll').val()) {
							console.log(' Not Removing ', this);
						} else {
							this.remove();
						}
					});
				} else {
					console.log('Clearing all items...');
					$('#chosenCategoryAll').find('option').not(':selected').remove();
				}
				
				$.each(data, function(key, value) {
					if (value.id == null || value.id == '') {
						html += '<option value="" disabled>' + value.companyName + '</option>';
					} else if (value.id == '-1') {
						html += '<option value="-1" disabled>' + value.companyName + '</option>';
					} else  if($('#chosenCategoryAll').val() !== value.id) {
						html += '<option value="' + value.id + '" data-name="' + value.companyName + '">' + value.companyName + '</option>';
					}
					
				});
			}
			$('#chosenCategoryAll').append(html);
			$("#chosenCategoryAll").trigger("chosen:updated");
//			$('#chosenCategoryAll_chosen .chosen-search input').val(favSupp);
			$('#loading').hide();
		},
		error : function(error) {
			console.log(error);
		}
	});
}
$(document).on("click", ".addFavSupplierList", function() {
	var val = [];
		$('.custom-checkbox1:checked').each(function(i) {
			val[i] = $(this).val();
		});
	$("#idGlobalError").hide();
	$("#idGlobalSuccess").hide();
	var eventStatus = $('#supplierForm').find('#eventStatus').val();
	var eventId = $.trim($("#eventId").val());
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var vId = val.toString();
	var select_all = ($(".allInvitedsupp").prop('checked'))
	var country = $.trim($('#registrationOfCountry').val());
	var state = $.trim($('#stateList').val());
	var supplierTagName = $.trim($('#supplierTagName').val());
	var inclusive = ($("#access_inclusive").prop('checked'));
	var exclusive = ($("#access_Exclusive").prop('checked'))
	var ajaxurl = getBuyerContextPath('addSupplierList');
	if (vId.length > 2) {
		$.ajax({
			url : ajaxurl,
			data : {
				'addSupplier' : vId,
				'eventId' : eventId,
				'select_all' : select_all,
				'country' : country,
				'state' : state,
				'supplierTagName' : supplierTagName,
				'exclusive' :exclusive,
				'inclusive' : inclusive
			},
			type : 'POST',
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var success = request.getResponseHeader('success');
				var table = [];
				$.each(data, function(i, item) {
					var deleteData = "";
					if (eventStatus === "DRAFT") {
						console.log("data" + data);
						deleteData = '<a href="" data-id="' + item.id + '" data-suppId="' + item.supplier.id + '" class="deleteSupplier" title="Delete"><img src="' + getContextPath() + '/resources/images/delete1.png"></a>';
					}
					table.push([ deleteData, item.supplier.companyName, item.supplier.communicationEmail, item.supplier.companyContactNumber ]);
				});
				$('#tableList1').DataTable().rows().remove().draw();
				$('#tableList1').DataTable().destroy();
				$('#tableList1').DataTable().rows.add(table).draw();
				$('#filterBy_State').modal('hide');
				if (success != null) {
					$("#idGlobalSuccessMessage").html(success);
					$("#idGlobalSuccess").show();
					$("#idGlobalError").hide();

					// $.jGrowl(success, {
					// sticky : false,
					// position : 'top-right',
					// theme : 'bg-green'
					// });
				}
				$("#addSuppliList option[value='" + vId + "']").remove();
				$("#addSuppliList").trigger("chosen:updated");
				$('#chosenCategoryAll_chosen .chosen-search input').val('');
				supplierDataTable.ajax.reload();
				reloadSupplierList();
			},
			error : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('errors');
				if (error != null) {
					$("#idGlobalErrorMessage").html(error);
					$("#idGlobalError").show();
					$("#idGlobalSuccess").hide();
					// showMessage('ERROR', error);
				}
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	} else {
		$('#industryCategoryList').hide();
	}
});
