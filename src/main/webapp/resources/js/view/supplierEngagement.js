$("#switchToSupplierEngagementId").click(function() {
	$(this).removeClass('font-white');
	$(this).parent().addClass('current');
	$(this).addClass('font-gray');
	$("#switchToSupplierProfileId").parent().removeClass('current');
	$("#switchToSupplierProfileId").addClass('font-white');
	$("#switchToSupplierProfileId").removeClass('font-gray');
	$("#switchToSupplierPerfomanceId").parent().removeClass('current');
	$("#switchToSupplierPerfomanceId").addClass('font-white');
	$("#switchToSupplierPerfomanceId").removeClass('font-gray');

	$("#supplierProfileViewId").show();
	$("#supplierEngagementViewId").hide();
	$("#supplierPerformanceId").hide();
});

$("#switchToSupplierProfileId").click(function() {
	$(this).removeClass('font-white');
	$(this).parent().addClass('current');
	$(this).addClass('font-gray');
	$("#switchToSupplierEngagementId").parent().removeClass('current');
	$("#switchToSupplierEngagementId").addClass('font-white');
	$("#switchToSupplierEngagementId").removeClass('font-gray');

	$("#switchToSupplierPerfomanceId").parent().removeClass('current');
	$("#switchToSupplierPerfomanceId").addClass('font-white');
	$("#switchToSupplierPerfomanceId").removeClass('font-gray');
	
	$("#supplierProfileViewId").hide();
	$("#supplierEngagementViewId").show();
	$("#supplierPerformanceId").hide();

});


$("#switchToSupplierPerfomanceId").click(function() {
	$(this).removeClass('font-white');
	$(this).parent().addClass('current');
	$(this).addClass('font-gray');
	$("#switchToSupplierEngagementId").parent().removeClass('current');
	$("#switchToSupplierEngagementId").addClass('font-white');
	$("#switchToSupplierEngagementId").removeClass('font-gray');
	$("#switchToSupplierProfileId").parent().removeClass('current');
	$("#switchToSupplierProfileId").addClass('font-white');
	$("#switchToSupplierProfileId").removeClass('font-gray');
	$("#supplierProfileViewId").hide();
	$("#supplierEngagementViewId").hide();
	$("#supplierPerformanceId").show();
});

$(document).ready(function() {

	var idOverallScoreBySpForm = $('#idOverallScoreBySpForm').DataTable();

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var supplierId = $('#supplierId').val();

	var indCatSeleted = '';
	$(document).find('.token-input-list').prepend(indCatSeleted);
	indCatSeleted = []
	finalCategories = []
	indCatSeleted = $("#industryCategoryCodes").val() ? $("#industryCategoryCodes").val().replace('[', '').replace(']', '').split('*') : [];
	ids = [];
	ids = $("#industryCategoryCodes").val() ? $('#industryCategoryCodes').val().replace('[', '').replace(']', '').split('*') : [];
	for (var index = 0; index < indCatSeleted.length; index++) {
		data = {};
		if (indCatSeleted[index] != "") {
			data['code'] = indCatSeleted[index].split('-')[1].trim();
			data['name'] = indCatSeleted[index].split('-')[2].trim();
			data['id'] = ids[index].split('-')[0].replace(',', '').trim();
			finalCategories.push(data)
		}
	}

	$("#demo-input-local").tokenInput("searchIndustryCategories", {
		minChars: 1,
		method: 'GET',
		hintText: "Start typing to search industry categories...",
		noResultsText: "No results",
		searchingText: "Searching...",
		queryParam: "search",
		propertyToSearch: "name",
		propertyToSearchCode: "code",
		preventDuplicates: true,
		minChars: 3,
		prePopulate: finalCategories,
		readonly: true
	});
	$('#demo-input-local').attr('data-validation', 'required');
	$('#demo-input-local').attr('data-validation-error-msg-container', '#catValErr');
	$.validate({});
	if ($("#statusId").val() === 'APPROVED') {
		$('#approveRequest').hide();
		$('#rejectRequest').hide();
		$('#buyerRemarkId').prop('readonly', true);
		$('#buyerRemarkId').attr('data-validation', '');
		$('#pointerEventId').attr('style', 'pointer-events: none;');
	}


	if (isBuyer) {
		$('#idBusinessUnit').val('').trigger("chosen:updated").change();
		$('#idFormId').val('').trigger("chosen:updated").change();
/*
		var buId = $('#idBusinessUnit').val();
		getOverallScoreOfSupplierByBuyerIdAndSpFormAndBUnit(header, token, null, null, buId, supplierId);
		var frmId = $('#idFormId').val();
		getOverallScoreByCriteriaAndFormID(header, token, null, null, frmId, supplierId);
*/	}


	$('#idBusinessUnit').on('change', function() {
		console.log("change >>>");
		var unitId = this.value;
		if (unitId == '') {
			idOverallScoreBySpForm.destroy();
			$('#scoringBySpForm').html('');
			idOverallScoreBySpForm = $('#idOverallScoreBySpForm').DataTable();
			return;
		}
		var supplierId = $('#supplierId').val();

		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1] + " supplierId " + supplierId);

		var startDate = myArr[0];
		var endDate = myArr[1];
		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1] + " unitId " + unitId);

		getOverallScoreOfSupplierByBuyerIdAndSpFormAndBUnit(header, token, startDate, endDate, unitId, supplierId);
	});


	function getOverallScoreOfSupplierByBuyerIdAndSpFormAndBUnit(header, token, startDate, endDate, unitId, supplierId) {

		var url = '';
		url = getContextPath() + '/buyer/getOverallScoreSpFormAndBUnit';

		$.ajax({
			url: url,
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'unitId': unitId,
				'supplierId': supplierId,
			},
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {
				idOverallScoreBySpForm.destroy();
				$('#scoringBySpForm').html('');
				var html = '';
				data.forEach(function(item) {
					html += '<tr><td class="width_100 width_100 align-left">' + item.formId + '</td>';
					html += '<td class="width_100 width_100 align-left">' + item.eventId + '</td>';
					html += '<td class="width_200_fix width_200 align-left">' + (item.procurementCategory != undefined ? item.procurementCategory : "") + '</td>';
					html += '<td class="width_200_fix width_200 align-left">' + item.unitName + '</td>';
					html += '<td class="width_100 width_100 align-left">' + item.overallScore + '</td>';
					html += '<td class="width_100 width_100 align-left">' + item.rating + '</td>';
					html += '<td class="width_200_fix width_200 align-left">' + item.ratingDescription + '</td></tr>';
					//				console.log(" ....... "+html);
				});

				$('#scoringBySpForm').html(html);

				idOverallScoreBySpForm = $('#idOverallScoreBySpForm').DataTable();

			},
			error: function(error) {
				console.log(error);
			}
		});
	}


	$('#idFormId').on('change', function() {
		console.log("change >>>");
		var formId = this.value;
		if (formId == '') {
			$('#scoringBySpCriteria').html('<tr><td class="align-center" colspan="3">No data available in table</td>');
			$('#scoringBySpCriteriaFoot').html('');

			return;
		}
		var supplierId = $('#supplierId').val();

		var date = $("input[name='dateTimeRange']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1] + " supplierId " + supplierId);

		var startDate = myArr[0];
		var endDate = myArr[1];
		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1] + " formId " + formId);

		getOverallScoreByCriteriaAndFormID(header, token, startDate, endDate, formId, supplierId);
	});

	function getOverallScoreByCriteriaAndFormID(header, token, startDate, endDate, formId, supplierId) {

		var url = '';
		url = getContextPath() + '/buyer/getOverallScoreByFormIdForCriteria';

		$.ajax({
			url: url,
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'formId': formId,
				'supplierId': supplierId,
			},
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {

				$('#scoringBySpCriteria').html('');
				$('#scoringBySpCriteriaFoot').html('');
				var html = '';
				if (data && data.overallScoreBySpForm) {
					data.overallScoreBySpForm.forEach(function(item) {
						html += '<tr><td class="align-left">' + (item.criteriaName != undefined ? item.criteriaName : "") + ' </td>';
						html += '<td class="align-left">' + (item.weightage != undefined ? item.weightage : "") + ' </td>';
						html += '<td class="align-left">' + (item.overallScore != undefined ? item.overallScore : "") + '</td></tr>';
					});
					html += '<tr><th class="align-left">Overall Score (%)</th><th>&nbsp;</th><th class="align-left">' + (data.ratingDescription != undefined ? data.totalOverallScore : "") + '</th></tr>';
					html += '<tr><th class="align-left">Rating</th><th>&nbsp;</th><th class="align-left">' + (data.ratingDescription != undefined ? data.rating : "") + '</th></tr>';
					html += '<tr><th class="align-left">Rating Description</th><th>&nbsp;</th><th class="align-left">' + (data.ratingDescription != undefined ? data.ratingDescription : "") + '</th></tr>';
				}else{
					html += '<tr><td class="align-left" colspan="3">No Date Found</td>';
				}
				$('#scoringBySpCriteria').html(html);

			},
			error: function(error) {
				console.log(error);
			}
		});
	}

	$('#datepickerAnalytics').on('apply.daterangepicker', function(e, picker) {
		console.log("apply click");
		e.preventDefault();
		$('.error-range.text-danger').remove();

		var date = $("input[name='dateTimeRange']").val();
		var myArr = date.split(" - ");
		var startDate = myArr[0];
		var endDate = myArr[1];

		getFormIdByDate(header, token, startDate, endDate, supplierId);

		$('#idBusinessUnit').val('').trigger("chosen:updated").change();
		//getOverallScoreOfSupplierByBuyerIdAndSpFormAndBUnit(header, token, startDate, endDate, buId, supplierId);

		$('#idFormId').val('').trigger("chosen:updated").change();
		//getOverallScoreByCriteriaAndFormID(header, token, startDate, endDate, frmId, supplierId);

		getOverallScoreByBuyerAndSupplierID(header, token, startDate, endDate, supplierId);

		getOverallScoreBySupplierIDForBU(header, token, startDate, endDate, supplierId);


	});

	$("#resetDate").click(function(e) {
		e.preventDefault();
		$('#datepickerAnalytics').data('daterangepicker').setStartDate(new Date());
		$('#datepickerAnalytics').data('daterangepicker').setEndDate(new Date());
		$("#datepickerAnalytics").val('');
		$("#datepickerAnalytics").trigger("apply.daterangepicker");

	});


	function getFormIdByDate(header, token, startDate, endDate, supplierId) {
		var url = '';
		url = getContextPath() + '/buyer/getFormIdByDate';
		console.log("********* " + startDate + ' .. ' + endDate);
		$.ajax({
			url: url,
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'supplierId': supplierId,
			},
			type: 'GET',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {

				console.log(">>>>>>>>>>>>>>>>>>");
				var html = '';
				$('#idFormId').html("");
				$('#idFormId').val('');
				html += '<option value="">Please Select Form ID</option>';
				$.each(data, function(i, item) {
					html += '<option value="' + item.id + '">' + item.formId + '</option>';
				});
				$('#idFormId').html(html);
				$("#idFormId").trigger("chosen:updated");

			},
			error: function(error) {
				console.log(error);
			}
		});
	}



	function getOverallScoreByBuyerAndSupplierID(header, token, startDate, endDate, supplierId) {
		var url = '';
		url = getContextPath() + '/buyer/getOverallScoreByBuyerAndSupplierID';

		$.ajax({
			url: url,
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'supplierId': supplierId,
			},
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {

				$('#scoringByBuyer').html('');
				var html = '';
				//				data.forEach(function(item) {
				html += '<tr><td class="align-left">' + data.buyerName + ' </td>';
				html += '<td class="align-left">' + (data && (data.overallScore != null && data.overallScore != undefined) ? data.overallScore : 'N/A') + '</td>';
				html += '<td class="align-left">' + (data && (data.scoreRating != null && data.scoreRating != undefined) ? data.scoreRating : 'N/A') + ' </td>';
				html += '<td class="align-left">' + (data && (data.ratingDescription != null && data.ratingDescription != undefined) ? data.ratingDescription : 'N/A') + '</td></tr>';
				$('#scoringByBuyer').html(html);

			},
			error: function(error) {
				console.log(error);
			}
		});
	}


	function getOverallScoreBySupplierIDForBU(header, token, startDate, endDate, supplierId) {
		var url = '';
		url = getContextPath() + '/buyer/getOverallScoreBySupplierIDForBU';

		$.ajax({
			url: url,
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'supplierId': supplierId,
			},
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {

				$('#scoringByBUnit').html('');
				var html = '';
				data.forEach(function(item) {
					html += '<tr><td class="align-left">' + item.unitName + ' </td>';
					html += '<td class="align-left">' + item.overallScore + '</td>';
					html += '<td class="align-left">' + item.scoreRating + ' </td>';
					html += '<td class="align-left">' + item.ratingDescription + '</td></tr>';
				});
				if (data.length == 0) {
					html += '<tr><td colspan="4" class="align-center">No data available in table</td></tr>';
				}
				$('#scoringByBUnit').html(html);

			},
			error: function(error) {
				console.log(error);
			}
		});
	}

});

$("#approveRequest").click(function() {
	$('#buyerRemarkId').attr('data-validation', 'length');
	$('#supplierRequestForm').isValid();
	var indCat = [];
	select = $("#token-input-demo-input-local").parent().parent().find(".token-input-token");
	select.each(function() {
		indCat.push($(this).find('input').val());
	});
	if (indCat.length <= 0) {
		event.preventDefault();
		$("#token-input-demo-input-local").closest(".token-input-list").css({
			'border-color': '#a94442'
		})
		return;
	}
	if ($('#supplierRequestForm').isValid()) {
		var id = $("#requestId").val()
		$("#approveSupplierRequest").attr("request-id", id);
		$("#approveRequestModal").modal('show')
	}
});

$("#rejectRequest").click(function() {
	var indCat = [];
	select = $("#token-input-demo-input-local").parent().parent().find(".token-input-token");
	select.each(function() {
		indCat.push($(this).find('input').val());
	});
	if (indCat.length <= 0) {
		event.preventDefault();
		$("#token-input-demo-input-local").closest(".token-input-list").css({
			'border-color': '#a94442'
		})
		return;
	}
	if ($('#buyerRemarkId').val() && $('#buyerRemarkId').val().length > 0) {
		var id = $("#requestId").val()
		$("#rejectSupplierRequest").attr("request-id", id);
		$("#rejectRequestModal").modal('show')
	} else {
		$('#buyerRemarkId').attr('data-validation', 'length required');
		$('#supplierRequestForm').isValid();
	}

});

$("#closeRequest").click(function() {
	$("#closeView").click()
});

$("#rejectSupplierRequest").click(function() {
	$("#rejectRequestModal").modal('hide');
	var requestId = $("#rejectSupplierRequest").attr("request-id");
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	data = {};
	data['buyerRemark'] = $('#buyerRemarkId').val();
	var indCat = [];
	select = $("#token-input-demo-input-local").parent().parent().find(".token-input-token");
	select.each(function() {
		indCat.push($(this).find('input').val());
	});
	data['indCat'] = indCat;

	$.ajax({
		url: getContextPath() + "/buyer/rejectSupplierRequest/" + requestId,
		type: "POST",
		dataType: 'json',
		contentType: 'application/json',
		data: JSON.stringify(data),
		beforeSend: function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success: function(request, status, xhr) {
			var success = xhr.getResponseHeader('success');
			$('p[id=idGlobalSuccessMessage]').html(success);
			$('div[id=idGlobalSuccess]').show();
			$('div[id=idGlobalError]').hide();
			$('#supplierStatusId').html(request.status)
			$('#rejectRequest').hide();
			supplierFormsData1.ajax.reload();
		},
		error: function(request, status, xhr) {
			$('p[id=idGlobalErrorMessage]').html(xhr.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('div[id=idGlobalSuccess]').hide();
		},
		complete: function() {
			$('#loading').hide();
		}
	});

});

$("#approveSupplierRequest").click(function() {
	$("#approveRequestModal").modal('hide');
	var requestId = $("#approveSupplierRequest").attr("request-id");
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	data = {};
	data['buyerRemark'] = $('#buyerRemarkId').val();
	var indCat = [];
	select = $("#token-input-demo-input-local").parent().parent().find(".token-input-token");
	select.each(function() {
		indCat.push($(this).find('input').val());
	});
	data['indCat'] = indCat;
	$.ajax({
		url: getContextPath() + "/buyer/acceptSupplierRequest/" + requestId,
		type: "POST",
		dataType: 'json',
		contentType: 'application/json',
		data: JSON.stringify(data),
		beforeSend: function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success: function(request, status, xhr) {
			var success = xhr.getResponseHeader('success');
			$('p[id=idGlobalSuccessMessage]').html(success);
			$('div[id=idGlobalSuccess]').show();
			$('div[id=idGlobalError]').hide();
			$('#supplierStatusId').html(request.status)
			$('#approveRequest').hide();
			$('#rejectRequest').hide();
			$('#buyerRemarkId').prop('readonly', true);
			$('#pointerEventId').attr('style', 'pointer-events: none;');
			$('#buyerRemarkId').attr('data-validation', '');
		},
		error: function(request, status, xhr) {
			$('p[id=idGlobalErrorMessage]').html(xhr.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('div[id=idGlobalSuccess]').hide();
		},
		complete: function() {
			$('#loading').hide();
		}
	});
});

