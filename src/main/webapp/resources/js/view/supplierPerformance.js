$(document).ready(function() {
	var ovralScoreByEventTable = $('.ovralScoreByEvent').DataTable();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$('#datepickerAnalytics').on('apply.daterangepicker', function(e, picker) {
		$(this).validate();
	});

	function getBUnitListByBuyerID(header, token, buyerId, startDate, endDate) {
		var url = '';
		url = getContextPath() + '/supplier/getBusinessUnitByDate';

		$.ajax({
			url: url,
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'buyerId': buyerId,
			},
			type: 'GET',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {
				ovralScoreByEventTable.destroy();
				var html = '';
				html += '<option value="">Please Select Business Unit</option>';
				if(data){
					$.each(data, function(i, item) {
						html += '<option value="' + item.id + '">' + item.unitName + '</option>';
					});
				}
				$('#idBusinessUnit').html(html);
				$("#idBusinessUnit").val('').trigger("chosen:updated");
				
				$('#scoringByEvent').html('');
				ovralScoreByEventTable = $('.ovralScoreByEvent').DataTable();

			},
			error: function(error) {
				console.log(error);
			}
		});
	}

	function getFormIdListByBuyerID(header, token, buyerId, startDate, endDate) {
		var url = getContextPath() + '/supplier/getFormIdByDate';
		$.ajax({
			url: url,
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'buyerId': buyerId
			},
			type: 'GET',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {

				var html = '';
				html += '<option value="">Please Select Form ID</option>';
				$.each(data, function(i, item) {
					html += '<option value="' + item.id + '">' + item.formId + '</option>';
				});
				$('#idFormId').html(html);
				$("#idFormId").val('').trigger("chosen:updated");
				$('#scoringBySpCriteria').html('<tr><td class="align-center" colspan="3">No data available in table</td>');
				$('#scoringBySpCriteriaFoot').html('');
			},
			error: function(error) {
				console.log(error);
			}
		});
	}

	$("#spFilter").click(function(e) {
		e.preventDefault();
		if (!$('#idSeachForm').isValid()) {
			return;
		}

		var date = $("input[name='dateTimeRange']").val();
		var buyerId = $("select[name='buyerList']").val();

		var myArr = date.split(" - ");
		console.log(" 1." + myArr[0] + "..2.." + myArr[1] + " buyerId " + buyerId);

		var startDate = myArr[0];
		var endDate = myArr[1];


		getOverallScoreOfSupplierByBuyer(header, token, buyerId, startDate, endDate);
		getOverallScoreOfSupplierByBUnit(header, token, buyerId, startDate, endDate);

		getBUnitListByBuyerID(header, token, buyerId, startDate, endDate);

		getFormIdListByBuyerID(header, token, buyerId, startDate, endDate);

	});

	function getOverallScoreOfSupplierByBuyer(header, token, buyerId, startDate, endDate) {
		var url = '';
		url = getContextPath() + '/supplier/getOverallScoreOfSupplierByBuyer';

		$.ajax({
			url: url,
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'buyerId': buyerId,
			},
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {

				$('#scoringByBuyer').html('');
				var html = '';
				html += '<tr><td class="align-left">' + (data.buyerName != undefined ? data.buyerName : "") + ' </td>';
				html += '<td class="align-left">' + (data.overallScore != undefined ? data.overallScore.toLocaleString(undefined, { maximumFractionDigit: 0 }) : "") + '</td>';
				html += '<td class="align-left">' + (data.scoreRating != undefined ? data.scoreRating : "") + ' </td>';
				html += '<td class="align-left">' + (data.ratingDescription != undefined ? data.ratingDescription : "") + '</td></tr>';
				$('#scoringByBuyer').html(html);

			},
			error: function(error) {
				console.log(error);
			}
		});
	}


	function getOverallScoreOfSupplierByBUnit(header, token, buyerId, startDate, endDate) {
		var url = getContextPath() + '/supplier/getOverallScoreOfSupplierByBUnit';

		$.ajax({
			url: url,
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'buyerId': buyerId,
			},
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {

				$('#scoringByBUnit').html('');
				var html = '';
				if (data) {
					data.forEach(function(item) {
						html += '<tr><td class="align-left">' + item.unitName + ' </td>';
						html += '<td class="align-left">' + item.overallScore.toLocaleString(undefined, { maximumFractionDigit: 0 }) + '</td>';
						html += '<td class="align-left">' + item.scoreRating + ' </td>';
						html += '<td class="align-left">' + item.ratingDescription + '</td></tr>';
					});
					if (data.length == 0) {
						html += '<tr><td colspan="4" class="align-left">No Data</td></tr>';
					}
				} else {
					html += '<tr><td colspan="4" class="align-left">No Data</td></tr>';
				}
				$('#scoringByBUnit').html(html);

			},
			error: function(error) {
				console.log(error);
			}
		});
	}


	function getOverallScoreOfSupplierByBUnitAndEvent(header, token, buyerId, unitId, startDate, endDate) {
		var url = getContextPath() + '/supplier/getOverallScoreOfSupplierByBUnitAndEvent';

		$.ajax({
			url: url,
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'unitId': unitId,
				'buyerId': buyerId,
			},
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {
				ovralScoreByEventTable.destroy();
				$('#scoringByEvent').html('');
				var html = '';
				if (data) {
					data.forEach(function(item) {

						html += '<tr><td class="width_100 width_100 align-left">' + item.eventId + '</td>';
						html += '<td class="width_100 width_100 align-left">' + item.formId + '</td>';
						html += '<td class="width_200_fix width_200 align-left">' + item.eventType + '</td>';
						html += '<td class="width_200_fix width_200 align-left">' + item.unitName + '</td>';
						html += '<td class="width_100 width_100 align-left">' + item.overallScore.toLocaleString(undefined, { maximumFractionDigit: 0 }) + '</td>';
						html += '<td class="width_100 width_100 align-left">' + item.rating + '</td>';
						html += '<td class="width_200_fix width_200 align-left">' + item.ratingDescription + '</td></tr>';
					});
				}
				$('#scoringByEvent').html(html);
				ovralScoreByEventTable = $('.ovralScoreByEvent').DataTable();
			},
			error: function(error) {
				console.log(error);
			}
		});
	}

	$('#idBusinessUnit').on('change', function() {
		console.log("change >>>");

		var unitId = this.value;
		if (unitId == '') {
			ovralScoreByEventTable.destroy();
			$('#scoringByEvent').html('');
			ovralScoreByEventTable = $('.ovralScoreByEvent').DataTable();
			return;
		}
		var date = $("input[name='dateTimeRange']").val();
		var buyerId = $("select[name='buyerList']").val();

		var myArr = date.split(" - ");

		var startDate = myArr[0];
		var endDate = myArr[1];
		console.log(" 1. " + myArr[0] + "..2.. " + myArr[1] + " unitId " + unitId + " buyerId " + buyerId);
		getOverallScoreOfSupplierByBUnitAndEvent(header, token, buyerId, unitId, startDate, endDate);
	});

	$('#idFormId').on('change', function() {
		console.log("change >>>");
		$('#scoringBySpCriteria').html('');
		$('#scoringBySpCriteriaFoot').html('');
		var formId = this.value;
		if (formId == '') {
			$('#scoringBySpCriteria').html('<tr><td class="align-center" colspan="3">No data available in table</td>');
			return;
		}
		var date = $("input[name='dateTimeRange']").val();
		var buyerId = $("select[name='buyerList']").val();

		var myArr = date.split(" - ");

		var startDate = myArr[0];
		var endDate = myArr[1];

		getOverallScoreByCriteriaAndFormID(header, token, buyerId, formId, startDate, endDate)

	});

	function getOverallScoreByCriteriaAndFormID(header, token, buyerId, formId, startDate, endDate) {

		var url = getContextPath() + '/supplier/getOverallScoreOfCriteriaByFormId';

		$.ajax({
			url: url,
			data: {
				'startDate': startDate,
				'endDate': endDate,
				'formId': formId,
				'buyerId': buyerId,
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
				} else {
					html += '<tr><td class="align-center" colspan="3">No Data Found.</td>';
					$('#scoringBySpCriteriaFoot').html('');
				}
				$('#scoringBySpCriteria').html(html);

			},
			error: function(error) {
				console.log(error);
			}
		});
	}
});

