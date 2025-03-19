var item_level_order = '';
$('document').ready(function() {

	// setting value for select Page Length on user preferences
	var bqPageLength = $('#bqPageLength').val();
	if (bqPageLength === undefined || bqPageLength === '') {
		bqPageLength = 50;
	}
	// console.log(" =="+ bqPageLength);
	$("#selectPageLen").val(bqPageLength).trigger("chosen:updated");
	$("#selectPageLenSor").val(bqPageLength).trigger("chosen:updated");

	/* Disable CQBQ Items */
	$(document).delegate('.disableItemFromCqBq .rejectSupplier', 'click', function(e) {
		e.preventDefault();
		var dataId = $(this).closest('th').attr('data-id');
		var dataeventType = $(this).closest('th').attr('data-type');
		$("#disableCqBqItemPopup").find('[name="supplier"]').val(dataId);
		$("#disableCqBqItemPopup").dialog({
			modal: true,
			minWidth: 400,
			maxWidth: 500,
			minHeight: 100,
			dialogClass: "",
			show: "fadeIn",
			draggable: true,
			resizable: false,
			dialogClass: "dialogBlockLoaded"
		});
	});

	$(document).delegate('.disableItemFromCqBq .passSupplier', 'click', function(e) {
		e.preventDefault();
		var dataId = $(this).closest('th').attr('data-id');
		var dataeventType = $(this).closest('th').attr('data-type');
		$("#enableCqBqItemPopup").find('[name="supplier"]').val(dataId);
		$("#enableCqBqItemPopup").dialog({
			modal: true,
			minWidth: 400,
			maxWidth: 500,
			minHeight: 100,
			dialogClass: "",
			show: "fadeIn",
			draggable: true,
			resizable: false,
			dialogClass: "dialogBlockLoaded"
		});
	});

	// DO DISQUALIFIED FOR CQ
	var totalCqTables = $('.table-1').length;
	if (totalCqTables > 0) {
		var totalCqCols = $('.table-1 > thead > tr:first-child > th').length;
		$('.table-1 > thead > tr:first-child > th').each(function(i) {
			// Do the iteration for only one table. Stop processing once one table column is iterated through as the disable is based on column class
			// name
			if ((i + 1) > (totalCqCols / totalCqTables)) {
				console.log('skipping at col index' + i);
				return false;
			}
			if ($(this).hasClass('disqualified')) {
				console.log('Disq Col index : ' + i);
				var ChildDibl = (parseInt(i) + 1);
				ChildDibl = ((parseInt(i) + 1) % ((totalCqCols / totalCqTables) + 1));
				console.log('Going to disable col : ' + ChildDibl);
				$('.table-2 tr > td:nth-child(' + ChildDibl + ')').addClass('disabled');
			}
		});
	}

	// DO DISQUALIFIED FOR BQ
	disqualifyForBq();

	// DO DISQUALIFIED FOR SOR
	disqualifyForSor();

	$('.table-1 .custom-checkbox:first').on('change', function() {

		var check = this.checked;
		$(".table-2 [type=checkbox]").each(function() {
			$(".table-2 [type=checkbox]").prop('checked', check);
			$.uniform.update($(this));
		});
	});

	$('.table-2 [type=checkbox]').on('change', function() {

		var total = $(".table-2 [type=checkbox]").length;
		var checked = $(".table-2 .checker .checked").length;
		var firstObj = $('.table-1 .custom-checkbox:first');
		if (checked == total) {
			firstObj.prop('checked', true);
		} else {
			firstObj.prop('checked', false);
		}
		$.uniform.update(firstObj);
	});

	$('#inlineCheckbox130').on('change', function() {

		var obj = $(this).closest('table').find('.inner_checkbox').find(".custom-checkbox");

		if ($(this).is(':checked')) {
			obj.attr('checked', true);
			obj.parent('span').addClass('checked');

		} else {
			obj.attr('checked', false);
			obj.parent('span').removeClass('checked');
		}

	});
	$('.inner_checkbox [type=checkbox]').on('change', function() {

		var total = $('.inner_checkbox').length;
		var checked = $('.inner_checkbox .checker .checked').length;
		var firstObj = $('#inlineCheckbox130');

		if (checked == total) {
			firstObj.attr('checked', true);
			firstObj.parent('span').addClass('checked');
		} else {
			firstObj.attr('checked', false);
			firstObj.parent('span').removeClass('checked');
		}

	});

	/** ************ Add Comment Ajx ************** */

	// $(".openComment").click(function() {
	$(document).delegate('.openComment', 'click', function(e) {
		$(".form-error").remove();
		$("#suppComment").parent().removeClass("has-error");
		$("#suppComment").removeClass("error");
		$('#suppComment').css("border-color", "");

		var eventId = $("#eventId").val();
		var itemId = $(this).data("itemid");
		var supplierId = $(this).data("supplier");
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var Commmet_type = $(this).data("subtype");

		$.ajax({
			type: "GET",
			url: getContextPath() + "/buyer/" + Commmet_type + "/" + getEventType() + "/" + eventId + "/" + itemId + "/" + supplierId,
			dataType: "json",
			beforeSend: function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			complete: function() {
				$('#loading').hide();
			},
			success: function(data) {
				// console.log(data);
				$("#commentAnswer").find('[name="supplierId"]').val(supplierId);
				$("#commentAnswer").find('[name="itemId"]').val(itemId);

				cAddType = "addComments";
				delcAddType = "removeComments";

				if (Commmet_type == "getSupplierCommentsForBq") {
					cAddType = "addCommentsForBq";
					delcAddType = "removeCommentsBq";
				}

				if (Commmet_type == "getSupplierCommentsForSor") {
					cAddType = "addCommentsForSor";
					delcAddType = "removeCommentsSor";
				}

				$("#commentAnswer").find('[name="subType"]').val(cAddType);
				$("#commentAnswer").find('[name="delsubType"]').val(delcAddType);

				$("#commentAnswer").dialog({
					modal: true,
					minWidth: 300,
					width: '400',
					dialogClass: "",
					show: "fadeIn",
					draggable: true,
					resizable: false,
					dialogClass: "dialogBlockLoaded",
					position: {
						my: "left top"
					}
				});
				addComment(data, itemId, supplierId);

				//				 
			},
			error: function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();

			},
			complete: function() {
				$('#loading').hide();
			}
		});

	});
	/** ************ Add Comment Ajx End ************** */

	/** ******** Save Commnet *********** */

	$(document).delegate('#saveCommentAnswer', 'click', function(e) {
		e.preventDefault();
		if (!$('#cancelCommentAnswer').isValid()) {
			return false;
		}

		var comment = $("#commentAnswer").find('textarea').val();
		if (comment == undefined || comment.length == 0) {
			return;
		}

		var eventId = $("#eventId").val();
		var itemId = $("#commentAnswer").find('[name="itemId"]').val();
		var supplierId = $("#commentAnswer").find('[name="supplierId"]').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var saveType = $("#commentAnswer").find('[name="subType"]').val();
		var eventType = $('#eventTypeSearch').val();

		var data = {
			'eventId': eventId,
			'itemId': itemId,
			'supplierId': supplierId,
			'comments': comment
		};
		$.ajax({
			type: "POST",
			url: getContextPath() + "/buyer/" + saveType + "/" + eventType,
			data: data,
			dataType: "json",
			beforeSend: function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			complete: function() {
				$('#loading').hide();
			},
			success: function(data) {
				addComment(data, itemId, supplierId);
				$("#commentAnswer").find('textarea').val('');
				$("#commentAnswer").dialog('close');
			},
			error: function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();

			},
			complete: function() {
				$('#loading').hide();
			}
		});
	});

	/** ******** Save Commnet *********** */

	function addComment(data, itemId, supplierId) {
		var commentHtml = '';
		$.each(data, function(i, comment) {
			commentHtml += '<div class="row remark" id="' + comment.id + '"><p class="col-md-12"><span class="width100 pull-left align-left">';
			commentHtml += comment.userName != undefined ? comment.userName : "Test User";
			if (comment.flagForCommentOwner) {
				commentHtml += ' : <a href="" class="deleteAnswer pull-right"><i class="fa fa-times-circle"></i></a></span><span class="width100 pull-left align-left">' + comment.comment + '</span><span class="width100 pull-right align-right">';
			} else {
				commentHtml += ' :</span><span class="width100 pull-left align-left">' + comment.comment + '</span><span class="width100 pull-right align-right">';
			}
			// commentHtml += ' : <a href="" class="deleteAnswer pull-right"><i class="fa fa-times-circle"></i></a></span><span class="width100
			// pull-left align-left">' + comment.comment + '</span><span class="width100 pull-right align-right">';
			commentHtml += comment.createdDate != undefined ? comment.createdDate : "";
			commentHtml += '</span></p></div>';
		});
		// console.log($("#cancelCommentAnswer").find('.ph_table_border.answersBlock'));
		$('a.triangleParentBlock[data-itemid="' + itemId + '"][data-supplier="' + supplierId + '"]').find('.triangleBlock').remove();
		if (commentHtml == '') {
			$("#cancelCommentAnswer").find('.ph_table_border.answersBlock').addClass('border-none');
		} else {
			$("#cancelCommentAnswer").find('.ph_table_border.answersBlock').removeClass('border-none');
			$('a.triangleParentBlock[data-itemid="' + itemId + '"][data-supplier="' + supplierId + '"]').prepend('<span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span>');
		}

		$("#cancelCommentAnswer").find('.reminderList.marginDisable').html(commentHtml);
		$('#cancelCommentAnswer .answersBlock').scrollTop($('.answersBlock').height() + 100);
	}

	/** ******* Delete Comment ***** */
	$(document).delegate('.deleteAnswer', 'click', function(e) {

		e.preventDefault();
		var commentId = $(this).closest('.row.remark').attr('id');
		var eventId = $("#eventId").val();
		var comment = $("#commentAnswer").find('textarea').val();
		var itemId = $("#commentAnswer").find('[name="itemId"]').val();
		var supplierId = $("#commentAnswer").find('[name="supplierId"]').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var delType = $("#commentAnswer").find('[name="delsubType"]').val();
		var eventType = $('#eventTypeSearch').val();

		var ajaxUrl = getContextPath() + "/buyer/" + delType + "/" + eventType + "/" + eventId + "/" + itemId + "/" + supplierId + "/" + commentId;
		$.ajax({
			url: ajaxUrl,
			type: "POST",
			beforeSend: function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader("Content-Type", "application/json");
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {
				addComment(data, itemId, supplierId);
				$("#commentAnswer").find('textarea').val('');
			},
			error: function(request, textStatus, errorThrown) {
				console.log("ERROR : " + request.getResponseHeader('error'));
				var error = request.getResponseHeader('error');
				if (request.getResponseHeader('error')) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
				}
				$.jGrowl(error, {
					sticky: false,
					position: 'top-right',
					theme: 'bg-red'
				});
				$('#loading').hide();
			},
			complete: function() {
				$('#loading').hide();
			}
		});

	});

	$(document).delegate('.totalBqComents', 'click', function(e) {
		$(".form-error").remove();
		$("#bqCommentAdded").parent().removeClass("has-error");
		$("#bqCommentAdded").removeClass("error");
		$('#bqCommentAdded').css("border-color", "");

		var bqId = $(this).attr('data-bqId');
		var supplierId = $(this).attr('data-supplier');

		var eventId = $("#eventId").val();
		console.log("bqId : " + bqId + "  supplierId : " + supplierId + " eventId :  " + eventId);
		$('#addBqCommentForm').find('#bqId').val(bqId);
		$('#addBqCommentForm').find('#supplierId').val(supplierId);
		var leadEvaluationComment = $('#bqCommentAdded').val();
		var eventType = $('#eventType').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type: "GET",
			url: getContextPath() + "/buyer/getBqTotalComment/" + eventType + "/" + bqId + "/" + supplierId + "/" + eventId,
			beforeSend: function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success: function(data, request, textStatus) {
				console.log(data);
				$("#commentBqAnswer").dialog({
					modal: true,
					minWidth: 300,
					width: '400',
					dialogClass: "",
					show: "fadeIn",
					draggable: true,
					resizable: false,
					dialogClass: "dialogBlockLoaded",
					position: {
						my: "left top"
					}
				});
				addBqTotalComment(data, bqId, supplierId);
			},
			error: function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete: function() {
				$('#loading').hide();
			}
		});

	});

	$(document).delegate('#bqAddComment', 'click', function(e) {
		e.preventDefault();
		if (!$('#addBqCommentForm').isValid()) {
			return false;
		}
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var bqId = $('#addBqCommentForm').find('#bqId').val();
		var supplierId = $('#addBqCommentForm').find('#supplierId').val();
		var eventId = $('#addBqCommentForm').find('#eventId').val();
		var comments = $('#bqCommentAdded').val();
		var eventType = $('#eventType').val();
		var data = {
			'eventId': eventId,
			'bqId': bqId,
			'supplierId': supplierId,
			'comments': comments
		};

		$.ajax({
			type: "POST",
			url: getContextPath() + "/buyer/addTotalCommentsForBq/" + eventType,
			data: data,
			beforeSend: function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success: function(data, textStatus, request) {
				addBqTotalComment(data, bqId, supplierId);
				$("#addBqCommentForm").find('textarea').val('');
				$("#commentBqAnswer").dialog('close');
			},
			error: function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete: function() {
				$('#loading').hide();
			}
		});
	});

	/** ******* Delete Comment ***** */
	$(document).delegate('.deleteBqAnswer', 'click', function(e) {
		e.preventDefault();
		var commentId = $(this).closest('.row.remark').attr('id');
		var comment = $("#addBqCommentForm").find('textarea').val();
		var bqId = $("#addBqCommentForm").find('[name="bqId"]').val();
		var supplierId = $("#addBqCommentForm").find('[name="supplierId"]').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var eventType = $('#eventType').val();
		var eventId = $('#addBqCommentForm').find('#eventId').val();
		var ajaxUrl = getContextPath() + "/buyer/deleteBqTotalComent/" + eventType + "/" + eventId + "/" + bqId + "/" + supplierId + "/" + commentId;
		$.ajax({
			url: ajaxUrl,
			type: "POST",
			beforeSend: function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader("Content-Type", "application/json");
				xhr.setRequestHeader(header, token);
			},
			success: function(data) {
				addBqTotalComment(data, bqId, supplierId);
				$("#commentAnswer").find('textarea').val('');
			},
			error: function(request, textStatus, errorThrown) {
				console.log("ERROR : " + request.getResponseHeader('error'));
				if (request.getResponseHeader('error')) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			},
			complete: function() {
				$('#loading').hide();
			}
		});

	});

	function addBqTotalComment(data, bqId, supplierId) {
		var commentHtml = '';
		$.each(data, function(i, comment) {
			commentHtml += '<div class="row remark" id="' + comment.id + '"><p class="col-md-12"><span class="width100 pull-left align-left">';
			commentHtml += comment.userName != undefined ? comment.userName : "Test User";
			if (comment.flagForCommentOwner) {
				commentHtml += ' : <a href="" class="deleteBqAnswer pull-right"><i class="fa fa-times-circle"></i></a></span><span class="width100 pull-left align-left">' + comment.comment + '</span><span class="width100 pull-right align-right">';
			} else {
				commentHtml += ' :</span><span class="width100 pull-left align-left">' + comment.comment + '</span><span class="width100 pull-right align-right">';
			}
			// commentHtml += ' : <a href="" class="deleteBqAnswer pull-right"><i class="fa fa-times-circle"></i></a></span><span class="width100
			// pull-left align-left">' + comment.comment + '</span><span class="width100 pull-right align-right">';
			commentHtml += comment.createdDate != undefined ? comment.createdDate : "";
			commentHtml += '</span></p></div>';
		});
		$('a.triangleParentBlock[data-bqId="' + bqId + '"][data-supplier="' + supplierId + '"]').find('.triangleBlock').remove();
		if (commentHtml == '') {
			$("#addBqCommentForm").find('.ph_table_border.answersBlock').addClass('border-none');
		} else {
			$("#addBqCommentForm").find('.ph_table_border.answersBlock').removeClass('border-none');
			$('a.triangleParentBlock[data-bqId="' + bqId + '"][data-supplier="' + supplierId + '"]').prepend('<span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span>');
		}

		$("#addBqCommentForm").find('.reminderList.marginDisable').html(commentHtml);
		$('#addBqCommentForm .answersBlock').scrollTop($('.answersBlock').height() + 100);
	}

	// BQ search filter

	$('#bqItemSearch').keyup(function(e) {
		e.preventDefault();
		// console.log("search value :" + $('#bqItemSearch').val());
		var searchVal = $('#bqItemSearch').val();
		var choosenVal = $('#chooseSection option:selected').text();
		var pageLength = $('#selectPageLen').val();
		// reset pagination to 1
		var pagination = jQuery('.pagination').data('twbsPagination');
		/*
		 * var pageToShow = 1; pagination.show(pageToShow);
		 */

		var selectPageNo = $('#pagination').find('li.active').text();
		var pageNo = parseInt(selectPageNo);
		// console.log("searchVal.length "+ searchVal.length);
		if (searchVal.length > 2 || searchVal.length == 0) {
			console.log(searchVal.length);
			var isPageEnable = false;

			// New requirement for filter fetch selected filter value page records.
			if (choosenVal) {
				var selectedIndex = $('#chooseSection option:selected').index();
				var pageToShow = selectedIndex / pageLength;
				var pageNo = parseInt(pageToShow);
				if (pageToShow % 1 !== 0) {
					pageNo = parseInt(pageToShow) + 1;
				}
				if (pageNo == 0) {
					pageNo = 1;
				}
				pagination.show(pageNo);
				var choosenVal = "";
			} else {
				var pageToShow = 1;
				pagination.show(pageToShow);
				var pageNo = parseInt(pageToShow);
			}

			evaluationSearchFilterBqItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable);
		}
	});


	$('#sorItemSearch').keyup(function(e) {
		e.preventDefault();
		// console.log("search value :" + $('#bqItemSearch').val());
		var searchVal = $('#sorItemSearch').val();
		var choosenVal = $('#chooseSectionSor option:selected').text();
		var pageLength = $('#selectPageLenSor').val();
		// reset pagination to 1
		var pagination = jQuery('.pagination').data('twbsPagination');
		/*
         * var pageToShow = 1; pagination.show(pageToShow);
         */

		var selectPageNo = $('#paginationSor').find('li.active').text();
		var pageNo = parseInt(selectPageNo);
		// console.log("searchVal.length "+ searchVal.length);
		if (searchVal.length > 2 || searchVal.length == 0) {
			console.log(searchVal.length);
			var isPageEnable = false;

			// New requirement for filter fetch selected filter value page records.
			if (choosenVal) {
				var selectedIndex = $('#chooseSectionSor option:selected').index();
				var pageToShow = selectedIndex / pageLength;
				var pageNo = parseInt(pageToShow);
				if (pageToShow % 1 !== 0) {
					pageNo = parseInt(pageToShow) + 1;
				}
				if (pageNo == 0) {
					pageNo = 1;
				}
				pagination.show(pageNo);
				var choosenVal = "";
			} else {
				var pageToShow = 1;
				pagination.show(pageToShow);
				var pageNo = parseInt(pageToShow);
			}

			evaluationSearchFilterSorItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable);
		}
	});

	$(document).delegate("#resetButton", "click", function(e) {
		e.preventDefault();
		var pageLen = "50";
		if ($('#selectPageLen option:selected').text()) {
			pageLen = $('#selectPageLen').val();
		}
		item_level_order = '';
		$("#bqItemSearch").val("");
		$("#chooseSection").val('').trigger("chosen:updated");
		$('#pagination').find('li.active').removeClass(".active");

		// reset pagination to 1
		var pagination = jQuery('.pagination').data('twbsPagination');
		var pageToShow = 1;
		pagination.show(pageToShow);

		var searchVal = "";
		var choosenVal = "";
		var selectPageNo = 1;
		var pageLength = parseInt(pageLen);
		var pageNo = parseInt(selectPageNo);
		// console.log(" pageLength : "+pageLen + "searchVal :"+searchVal + " choosenVal : "+choosenVal + " pageNo :" +pageNo) ;

		var isPageEnable = false;
		if (searchVal == "" || filterVal == "") {
			var isPageEnable = true;
		}
		evaluationSearchFilterBqItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable);
	});



	$(document).delegate("#resetButtonSor", "click", function(e) {
		e.preventDefault();
		var pageLen = "50";
		if ($('#selectPageLen option:selected').text()) {
			pageLen = $('#selectPageLenSor').val();
		}
		item_level_order = '';
		$("#sorItemSearch").val("");
		$("#chooseSectionSor").val('').trigger("chosen:updated");
		$('#paginationSor').find('li.active').removeClass(".active");

		// reset pagination to 1
		var pagination = jQuery('.pagination').data('twbsPagination');
		var pageToShow = 1;
		pagination.show(pageToShow);

		var searchVal = "";
		var choosenVal = "";
		var selectPageNo = 1;
		var pageLength = parseInt(pageLen);
		var pageNo = parseInt(selectPageNo);

		var isPageEnable = false;
		if (searchVal == "" || filterVal == "") {
			var isPageEnable = true;
		}
		evaluationSearchFilterSorItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable);
	});

	$('#selectPageLen').change(function() {
		var pageLen = "50";
		if ($('#selectPageLen option:selected').text()) {
			// var pageLen = $('#selectPageLen option:selected').text();
			pageLen = $('#selectPageLen').val();
		}
		var searchVal = $('#bqItemSearch').val();
		var choosenVal = $('#chooseSection option:selected').text();
		var pageLength = parseInt(pageLen);
		var selectPageNo = $('#pagination').find('li.active').text();
		var pageNo = parseInt(selectPageNo);
		console.log(" pageLength  : " + pageLen + "searchVal :" + searchVal + " choosenVal  : " + choosenVal + " pageNo :" + pageNo);
		// New requirement for filter fetch selected filter value page records.
		if (choosenVal) {
			var pagination = jQuery('.pagination').data('twbsPagination');
			var selectedIndex = $('#chooseSection option:selected').index();
			var pageToShow = selectedIndex / pageLength;
			var pageNo = parseInt(pageToShow);
			if (pageToShow % 1 !== 0) {
				pageNo = parseInt(pageToShow) + 1;
			}
			if (pageNo == 0) {
				pageNo = 1;
			}
			pagination.show(pageNo);
			var choosenVal = "";
		} else {
			item_level_order = '';
		}
		var isPageEnable = false;
		if (searchVal == "" || choosenVal == "") {
			var isPageEnable = true;
		}
		evaluationSearchFilterBqItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable);
		resetPagination(pageLen);
	});


	$('#selectPageLenSor').change(function() {
		var pageLen = "50";
		if ($('#selectPageLenSor option:selected').text()) {
			// var pageLen = $('#selectPageLen option:selected').text();
			pageLen = $('#selectPageLenSor').val();
		}
		var searchVal = $('#sorItemSearch').val();
		var choosenVal = $('#chooseSectionSor option:selected').text();
		var pageLength = parseInt(pageLen);
		var selectPageNo = $('#paginationSor').find('li.active').text();
		var pageNo = parseInt(selectPageNo);
		console.log(" pageLength  : " + pageLen + "searchVal :" + searchVal + " choosenVal  : " + choosenVal + " pageNo :" + pageNo);
		// New requirement for filter fetch selected filter value page records.
		if (choosenVal) {
			var pagination = jQuery('.pagination').data('twbsPagination');

			var selectedIndex = $('#chooseSectionSor option:selected').index();
			var pageToShow = selectedIndex / pageLength;
			var pageNo = parseInt(pageToShow);
			if (pageToShow % 1 !== 0) {
				pageNo = parseInt(pageToShow) + 1;
			}
			if (pageNo == 0) {
				pageNo = 1;
			}
			pagination.show(pageNo);
			var choosenVal = "";
		} else {
			item_level_order = '';
		}
		var isPageEnable = false;
		if (searchVal == "" || choosenVal == "") {
			var isPageEnable = true;
		}
		evaluationSearchFilterSorItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable);
		resetPaginationSor(pageLen);
	});

	$('#chooseSection').change(function() {
		if ($('#chooseSection option:selected').text()) {
			var choosenVal = $('#chooseSection option:selected').text();
		}
		var searchVal = $('#bqItemSearch').val();
		var pageLength = $('#selectPageLen').val();
		var selectPageNo = $('#pagination').find('li.active').text();
		var supplierList = [];
		var pagination = jQuery('.pagination').data('twbsPagination');

		item_level_order = choosenVal.replace(".", "_");
		item_level_order = "item_" + item_level_order;
		if (!$('#chooseSection option:selected').text()) {
			item_level_order = '';
		}
		console.log("item_level_order :" + item_level_order);

		var selectedIndex = $('#chooseSection option:selected').index();
		console.log("selectedIndex :" + selectedIndex);
		// New requirement for filter fetch selected filter value page records.
		var pageToShow = selectedIndex / pageLength;

		var pageNo = parseInt(pageToShow);
		if (pageToShow % 1 !== 0) {
			console.log("pageToShow :" + pageToShow);
			pageNo = parseInt(pageToShow) + 1;
		}
		if (pageNo == 0) {
			pageNo = 1;
		}
		pagination.show(pageNo);
		console.log(" pageNo  : " + pageNo);
		var choosenVal = "";

		// var pageNo = parseInt(selectPageNo);
		// console.log(" pageLength : "+pageLength);
		var isPageEnable = false;
		evaluationSearchFilterBqItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable);
	});


	$('#chooseSectionSor').change(function() {
		if ($('#chooseSectionSor option:selected').text()) {
			var choosenVal = $('#chooseSectionSor option:selected').text();
		}

		var searchVal = $('#sorItemSearch').val();
		var pageLength = $('#selectPageLenSor').val();
		var selectPageNo = $('#paginationSor').find('li.active').text();
		var supplierList = [];
		var pagination = jQuery('.pagination').data('twbsPagination');

		item_level_order = choosenVal.replace(".", "_");
		item_level_order = "soritem_" + item_level_order;
		if (!$('#chooseSectionSor option:selected').text()) {
			item_level_order = '';
		}
		console.log("item_level_order :" + item_level_order);

		var selectedIndex = $('#chooseSectionSor option:selected').index();
		console.log("selectedIndex :" + selectedIndex);
		// New requirement for filter fetch selected filter value page records.
		var pageToShow = selectedIndex / pageLength;

		var pageNo = parseInt(pageToShow);
		if (pageToShow % 1 !== 0) {
			console.log("pageToShow :" + pageToShow);
			pageNo = parseInt(pageToShow) + 1;
		}
		if (pageNo == 0) {
			pageNo = 1;
		}
		pagination.show(pageNo);
		console.log(" pageNo  : " + pageNo);
		var choosenVal = "";
		var isPageEnable = false;
		evaluationSearchFilterSorItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable);
	});
});

$(document).delegate('.cqComment', 'click', function(e) {
	$(".form-error").remove();
	$("#commentAdded").parent().removeClass("has-error");
	$("#commentAdded").removeClass("error");
	$('#commentAdded').css("border-color", "");
	var row = $(this).attr('data-row');
	var itemId = $('#itemId' + row + '').val();
	$('#addCommentForm').find('#item_id').val(itemId);
	var leadEvaluationComment = $('#commentAdded').val();
	var eventType = $('#eventType').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		type: "GET",
		url: getContextPath() + "/buyer/getCqComment/" + eventType + "/" + itemId,
		beforeSend: function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success: function(data, request, textStatus) {
			$("#addCommentForm").find('#commentAdded').val(data);
			// $('#addCommentForm').find('#item_id').val(itemId);

			$("#commentCqAnswer").dialog({
				modal: true,
				minWidth: 300,
				width: '400',
				dialogClass: "",
				show: "fadeIn",
				draggable: true,
				resizable: false,
				dialogClass: "dialogBlockLoaded",
				position: {
					my: "left top"
				}
			});
		},
		error: function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete: function() {
			$('#loading').hide();
		}
	});

});

$('#cancel').on('click', function(e) {
	$("#commentCqAnswer").dialog("close");
});

$(document).delegate('#addComment', 'click', function(e) {
	e.preventDefault();
	if (!$('#addCommentForm').isValid()) {
		return false;
	}
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var itemId = $('#addCommentForm').find('#item_id').val();
	var leadEvaluationComment = $('#commentAdded').val();
	var eventType = $('#eventType').val();
	var data = {
		'itemId': itemId,
		'leadEvaluationComment': leadEvaluationComment,
		'eventType': eventType
	};
	$.ajax({
		type: "POST",
		url: getContextPath() + "/buyer/addCqComment",
		data: data,
		beforeSend: function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		complete: function() {
			$('#loading').hide();
		},
		success: function(data, textStatus, request) {
			// var success = request.getResponseHeader('success');
			$("#commentCqAnswer").find('textarea').val('');
			$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
			$('div[id=idGlobalSuccess]').show();
			$('#loading').hide();
		},
		error: function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete: function() {
			$('#loading').hide();
			$("#commentCqAnswer").dialog("close");
		}
	});

});

function evaluationSearchFilterBqItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable) {
	var eventType = $('#eventType').val();
	var eventId = $('#eventId').val();
	var envelopId = $('#evenvelopId').val();

	// console.log("eventType :" + eventType + " =envelopId :"+ envelopId+ " = eventId :"+eventId );
	var filterVal = choosenVal;
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		type: "POST",
		url: getContextPath() + "/buyer/getBqItemForSearchFilter/" + eventType + "/" + eventId + "/" + envelopId,
		data: {
			'filterVal': filterVal,
			'searchVal': searchVal,
			'pageNo': pageNo,
			'pageLength': pageLength,
			'supplierList': selectedSuppliers
		},
		beforeSend: function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success: function(data) {

			$.each(data, function(i, item) {
				var table = '';
				// console.log("i :" + i +"\n");
				$.each(item.data, function(j, itemData) {
					// console.log("itemData :" + itemData[0]);
					table += searchRenderGrid(itemData);
				});
				// console.log("table :" + table);
				$('#bqItemList' + i).html(table);
			});

			// DO DISQUALIFIED FOR BQ
			disqualifyForBq();

			/* ===== tooltip ========== */
			$('[data-toggle="tooltip"]').tooltip();

			if (item_level_order.length) {
				$('.' + item_level_order).each(function() {
					$.each(this.cells, function() {
						$(this).animate({
							backgroundColor: "#ffff00"
						}, 2000);
					});
				});

				setTimeout(function() {
					$('.' + item_level_order).each(function() {
						$.each(this.cells, function() {
							$(this).animate({
								backgroundColor: "#ffffff"
							}, 2000);
						});
					});
				}, 4000);
			}

		},
		error: function(request, textStatus, errorThrown) {
			console.log("error");
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete: function() {
			$('#loading').hide();
		}
	});
}




function evaluationSearchFilterSorItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable) {
	var pageLen = $('#selectPageLenSor').val(); // Change from selectPageLen to selectPageLenSor
	var eventType = $('#eventType').val();
	var eventId = $('#eventId').val();
	var envelopId = $('#evenvelopId').val();

	if (!pageLength) {
		pageLength = parseInt(pageLen);
	}

	// console.log("eventType :" + eventType + " =envelopId :"+ envelopId+ " = eventId :"+eventId );
	var filterVal = choosenVal;
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$.ajax({
		type: "POST",
		url: getContextPath() + "/buyer/getSorItemForSearchFilter/" + eventType + "/" + eventId + "/" + envelopId,
		data: {
			'filterVal': filterVal,
			'searchVal': searchVal,
			'pageNo': pageNo,
			'pageLength': pageLength,
			'supplierList': selectedSuppliers
		},
		beforeSend: function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success: function(data) {
			$.each(data, function(i, item) {
				var table = '';
				$.each(item.data, function(j, itemData) {
					table += searchRenderGridSor(itemData);
				});
				$('#sorItemList' + i).html(table);
			});
			disqualifyForSor();

			/* ===== tooltip ========== */
			$('[data-toggle="tooltip"]').tooltip();

			if (item_level_order.length) {
				$('.' + item_level_order).each(function() {
					$.each(this.cells, function() {
						$(this).animate({
							backgroundColor: "#ffff00"
						}, 2000);
					});
				});

				setTimeout(function() {
					$('.' + item_level_order).each(function() {
						$.each(this.cells, function() {
							$(this).animate({
								backgroundColor: "#ffffff"
							}, 2000);
						});
					});
				}, 4000);
			}

		},
		error: function(request, textStatus, errorThrown) {
			console.log("error");
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete: function() {
			$('#loading').hide();
		}
	});
}

function searchRenderGrid(itemData) {
	var eventStatus = $('#eventStatus').val();
	var eventDecimal = $('#eventDecimal').val();
	var table = '';
	var levelOrder = '';
	$.each(itemData, function(k, answers) {
		if (k == 0) {
			// console.log("level order if :" + answers);
			levelOrder = answers;
			return false;
		}
	});
	levelOrder = levelOrder.replace(".", "_");
	levelOrder = "item_" + levelOrder;
	table += '<tr class="' + levelOrder + '">';
	$.each(itemData, function(k, answers) {
		// console.log("answers :" + answers);
		if (k == 0) {
			table += '			<td class="width_45  width_45_fix align-left">';
			table += '			<p>' + answers + '</p>';
			table += '			</td>';
		}
		if (k == 1) {
			table += '			<td class="width_400_fix align-left">';
			table += '				<p>' + answers + '</p>';
			table += '			</td>';
		}
		if (k == 2) {
			table += '			<td class="width_100  width_100_fix text-center">';
			table += '				<p>' + answers + '</p>';
			table += '			</td>';
		}
		if (k == 3) {
			table += '			<td class="width_100  width_100_fix text-right">';
			table += '					<p>' + (answers !== 'undefined' ? ReplaceNumberWithCommas(answers) : "") + '</p>';
			table += '				</td>';
		}
		if (k > 3) {
			table += '			<td class="width_250_fix text-right">';
			if (typeof answers !== 'undefined' && answers !== '') {
				var answer = answers.split("-");
				table += '					<p class="char_check" id="check_char-' + answer[2] + '">';
				table += '						<a data-supplier="' + answer[1] + '" data-subtype="getSupplierCommentsForBq" data-itemid="' + answer[2] + '" class="' + (eventStatus === "CLOSED" ? "openComment " : "") + 'triangleParentBlock" data-toggle="tooltip"';
				table += '							data-original-title="' + (eventStatus === "CLOSED" ? "Click to Add comment" : "") + '" href="javascript:void(0);">';
				if (answer[0] === 'true') {
					table += '								<span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span>';
				}
				table += '					<strong>' + ((typeof answer[3] !== 'undefined') ? ReplaceNumberWithCommas(parseFloat(answer[3]).toFixed(eventDecimal)) : '') + '</strong>';
				table += '						</a>';
				table += '						</p>';
			}
			table += '			</td>';
		}
	});
	table += '	</tr>';
	return table;
}


function searchRenderGridSor(itemData) {
	var eventStatus = $('#eventStatus').val();
	var eventDecimal = $('#eventDecimal').val();
	var table = '';
	var levelOrder = '';
	$.each(itemData, function(k, answers) {
		if (k == 0) {
			// console.log("level order if :" + answers);
			levelOrder = answers;
			return false;
		}
	});
	levelOrder = levelOrder.replace(".", "_");
	levelOrder = "soritem_" + levelOrder;
	table += '<tr class="' + levelOrder + '">';
	$.each(itemData, function(k, answers) {
		// console.log("answers :" + answers);
		if (k == 0) {
			table += '			<td class="width_45  width_45_fix align-left">';
			table += '			<p>' + answers + '</p>';
			table += '			</td>';
		}
		if (k == 1) {
			table += '			<td class="width_400_fix align-left">';
			table += '				<p>' + answers + '</p>';
			table += '			</td>';
		}
		if (k == 2) {
			table += '			<td class="width_100  width_100_fix text-center">';
			table += '				<p>' + answers + '</p>';
			table += '			</td>';
		}
		if (k == 3) {
			table += '			<td class="width_100  width_100_fix text-center">';
			table += '				<p></p>';
			table += '			</td>';
		}
		if (k >= 4) {
			table += '			<td class="width_250_fix text-right">';
			if (typeof answers !== 'undefined' && answers !== '') {
				var answer = answers.split("-");
				table += '					<p class="char_check" id="check_char-' + answer[2] + '">';
				table += '						<a data-supplier="' + answer[1] + '" data-subtype="getSupplierCommentsForSor" data-itemid="' + answer[2] + '" class="' + (eventStatus === "CLOSED" ? "openComment " : "") + 'triangleParentBlock" data-toggle="tooltip"';
				table += '							data-original-title="' + (eventStatus === "CLOSED" ? "Click to Add comment" : "") + '" href="javascript:void(0);">';
				if (answer[0] === 'true') {
					table += '								<span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span>';
				}
				table += '					<strong>' + ((typeof answer[3] !== 'undefined') ? ReplaceNumberWithCommas(parseFloat(answer[3]).toFixed(eventDecimal)) : '') + '</strong>';
				table += '						</a>';
				table += '						</p>';
			}
			table += '			</td>';
		}
	});
	table += '	</tr>';
	return table;
}

function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}

function disqualifyForBq() {
	var totalBqTables = $('.bqitemsDataTable').length;
	if (totalBqTables > 0) {
		var totalBqCols = $('.bqitemsDataTable > thead > tr:first-child > th').length;
		$('.bqitemsDataTable > thead > tr:first-child > th').each(function(i) {
			// Do the iteration for only one table. Stop processing once one table column is iterated through as the disable is based on column class
			// name
			if ((i + 1) > (totalBqCols / totalBqTables)) {
				console.log('skipping at col index' + i);
				return false;
			}
			if ($(this).hasClass('disqualified')) {
				var ChildDibl = (parseInt(i) + 1);
				ChildDibl = (parseInt(i) + 1) % ((totalBqCols / totalBqTables) + 1);
				console.log('Going to disable col : ' + ChildDibl);
				$('.bqitemsDataTable tr > td:nth-child(' + ChildDibl + ')').addClass('disabled');
				// $(this).closest('thead').next('tbody').find('tr > td:nth-child(' + ChildDibl + ')').addClass('disabled');
			}
		});
	}
}

function disqualifyForSor() {
	var totalBqTables = $('.soritemsDataTable').length;
	if (totalBqTables > 0) {
		var totalBqCols = $('.soritemsDataTable > thead > tr:first-child > th').length;
		$('.soritemsDataTable > thead > tr:first-child > th').each(function(i) {
			if ((i + 1) > (totalBqCols / totalBqTables)) {
				console.log('skipping at col index' + i);
				return false;
			}
			if ($(this).hasClass('disqualified')) {
				var ChildDibl = (parseInt(i) + 1);
				ChildDibl = (parseInt(i) + 1) % ((totalBqCols / totalBqTables) + 1);
				console.log('Going to disable col : ' + ChildDibl);
				$('.soritemsDataTable tr > td:nth-child(' + ChildDibl + ')').addClass('disabled');
			}
		});
	}
}

function resetPagination(bqPageLength) {
	var totalPage = 500;
	var visiblePage = 5;
	if (bqPageLength === undefined || bqPageLength === '') {
		bqPageLength = 50;
	}
	var totalBqItemCount = Math.ceil($('.bqitemsDataTable tr').length / $('.bqitemsDataTable thead').length);
	// alert(totalBqItemCount);
	totalPage = Math.ceil(totalBqItemCount / bqPageLength);

	if (totalPage == 0 || totalPage === undefined || totalPage === '') {
		totalPage = 1;
	}

	if (totalPage < 5) {
		visiblePage = totalPage;
	}
	if (isNaN(totalPage)) {

		totalPage = 1;
	}

	var $pagination = $('#pagination');
	var defaultOpts = {
		totalPages: totalPage
	};
	$pagination.twbsPagination(defaultOpts);

	var currentPage = $pagination.twbsPagination('getCurrentPage');
	$pagination.twbsPagination('destroy');
	$pagination.twbsPagination($.extend({}, defaultOpts, {
		startPage: currentPage,
		totalPages: totalPage
	}));

}

function resetPaginationSor(bqPageLength) {
	var totalPage = 500;
	var visiblePage = 5;
	if (bqPageLength === undefined || bqPageLength === '') {
		bqPageLength = 50;
	}
	var totalBqItemCount = Math.ceil($('.soritemsDataTable tr').length / $('.soritemsDataTable thead').length);
	// alert(totalBqItemCount);
	totalPage = Math.ceil(totalBqItemCount / bqPageLength);

	if (totalPage == 0 || totalPage === undefined || totalPage === '') {
		totalPage = 1;
	}

	if (totalPage < 5) {
		visiblePage = totalPage;
	}
	if (isNaN(totalPage)) {

		totalPage = 1;
	}

	var $pagination = $('#pagination');
	var defaultOpts = {
		totalPages: totalPage
	};
	$pagination.twbsPagination(defaultOpts);

	var currentPage = $pagination.twbsPagination('getCurrentPage');
	$pagination.twbsPagination('destroy');
	$pagination.twbsPagination($.extend({}, defaultOpts, {
		startPage: currentPage,
		totalPages: totalPage
	}));

}

$(document).delegate('.deleteSummary', 'click', function(e) {
	e.preventDefault();
	/*
	 * var summaryData = $(this).closest('td').attr('contact-id'); $('#confirmDeleteSummary').find('#deleteIdContact').val(contactId);
	 */
	$('#confirmDeleteSummary').modal();

});

$(document).delegate('.supplierRemark', 'click', function(e) {

	var bqId = $(this).attr('data-bqId');
	var supplierId = $(this).attr('data-supplier');
	var eventId = $("#eventId").val();

	var eventType = $('#eventType').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		type: "GET",
		url: getContextPath() + "/buyer/getSupplierRemarkComment/" + eventType + "/" + bqId + "/" + supplierId + "/" + eventId,
		beforeSend: function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
		},
		success: function(data, request, textStatus) {
			$("#showSupplierRemark").dialog({
				modal: true,
				minWidth: 300,
				width: '400',
				dialogClass: "",
				show: "fadeIn",
				draggable: true,
				resizable: false,
				dialogClass: "dialogBlockLoaded",
				position: {
					my: "left top"
				}
			});
			var v = data;
			$('.remarkBlock').text(data);
			if (v == '') {
				$("#showSupplierRemark").find('.ph_table_border.remarkBlock').addClass('border-none');
			} else {
				$("#showSupplierRemark").find('.ph_table_border.remarkBlock').removeClass('border-none');
			}
		},
		error: function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete: function() {
			$('#loading').hide();
		}
	});
});
$(document).delegate('.deleteEvaluationDocument', 'click', function(e) {
	e.preventDefault();
	/*
	 * var summaryData = $(this).closest('td').attr('contact-id'); $('#confirmDeleteDocument').find('#deleteIdDocument').val(contactId);
	 */
	$('#confirmDeleteDocument').modal();

});




