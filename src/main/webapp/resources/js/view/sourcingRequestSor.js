var item_level_order = '';
var item_order = '';
$(function() {
	var bqName = $('#editBqName').val();
	$('[data-toggle="tooltip"]').tooltip();

	$('.toggleCreateBq, #bqCancel').click(function() {

		if ($('#demo-form1').length > 0) {
			$('#demo-form1').get(0).reset();
		}

		if ($('#bqFormAddEdit').length > 0) {
			$('#bqFormAddEdit').get(0).reset();
		}

		$('.toggleCreateBq').parent().next().slideToggle();
		$('.toggleCreateBq').find('i').toggleClass('fa-minus').toggleClass('fa-plus');
		$('#bqFormAddEdit').find('#bqDesc').val('');
		$('#bqFormAddEdit').find('#bqName').val('');
		$('#bqFormAddEdit').find('#bqId').val('');
		$("#itemId").val('');
		$('#bqSave').text(saveLabel);
	});

	$('.bqCancelSec').click(function(e) {
		e.preventDefault();
		$('#demo-form1').get(0).reset();
		$('#creat_seaction_form').hide(); // demo-form1
		$('#creat_subitem_form').hide(); // demo-form1
		$('#sectionName').find('#bqDesc').val('');
		$('#sectionDescription').val('');

	});
});
$(document).ready(function() {

	// setting value for select Page Length on user preferences
	var bqPageLength = $('#bqPageLength').val();
	if (bqPageLength === undefined || bqPageLength === '') {
		bqPageLength = 50;
	}
	// console.log(" =="+ bqPageLength);
	$("#selectPageLen").val(bqPageLength).trigger("chosen:updated");

	renumber_table();
	// Helper function to keep table row from collapsing when
	// being sorted
	var fixHelperModified = function(e, tr) {
		var $originals = tr.children();
		var $helper = tr.clone();
		$helper.children().each(function(index) {
			$(this).width($originals.eq(index).width())
		});
		return $helper;
	};

	var fixHelperModified1 = function(e, tr) {
		var $originals = tr.children();
		var $helper = tr.clone();
		$helper.children().each(function(index) {
			$(this).width($originals.eq(index).width())
		});
		return $helper;
	};

	$('.uploadBqItmsBtn').click(function(e) {
		e.preventDefault();
		$('#uploadBqItems').click();
	});

	$('#uploadBqItems').change(function() {
		var eventId = $('#eventId').val();
		var bqId = $('#bqId').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
	});

	// Make diagnosis table sortable

	/* this code use for table drag and drop */
	var ns = $('ol.sortable').nestedSortable({
		forcePlaceholderSize : true,
		handle : 'div',
		helper : 'clone',
		items : 'li:not(.disableDragDrop)',
		opacity : .6,
		placeholder : 'placeholder',
		revert : 250,
		tabSize : 25,
		tolerance : 'pointer',
		toleranceElement : '> div',
		maxLevels : 2,
		isTree : true,
		expandOnHover : 700,
		startCollapsed : false,
		isAllowed : function(item, parent, currentItem) {
			return true;

		},
		change : function(e) {
			// console.log(e);
		},
		stop : function(item, currentItem) {
			var formId = $('#formId').val();
			var bqId = $('#bqId').val();
			// console.log( "order bqId
			// :"+bqId);
			var itemId = currentItem.item.attr('data-item');
			var parentId = currentItem.item.attr('data-parent');
			var oldPos = parseInt(currentItem.item.attr('data-level'));
			var itemName = currentItem.item.find('span.item_name').html();
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");

			var pageLength = $('#selectPageLen').val();
			var selectPageNo = $('#pagination').find('li.active').text();
			var pageNo = parseInt(selectPageNo);

			var newParentId = currentItem.item.parents('li').attr('data-item');
			if (newParentId == undefined) {
				newParentId = '';
			}
			var numberPos = currentItem.item.children('div').find('.number').text();
			// console.log("itemId : " +
			// itemId);
			var newPos = 1;
			if ($('ol.sortable').find('li[data-item="' + itemId + '"]').prev().attr('data-level') === undefined) {

			} else {
				newPos = parseInt($('ol.sortable').find('li[data-item="' + itemId + '"]').prev().attr('data-level'));
				if (newPos < oldPos || newParentId !== parentId) {
					newPos = newPos + 1;
				}
			}

			currentItem.item.attr('data-parent', newParentId);

			var data = {};
			data["id"] = itemId;
			data["parent"] = newParentId;
			data["order"] = newPos;
			data["sor"] = bqId;
			data["itemName"] = itemName;
			data["formId"] = formId;
			data["pageNo"] = pageNo;
			data["pageLength"] = pageLength;
			// console.log("bqId :" + bqId);
			$.ajax({
				url : getContextPath() + "/buyer/sourcingSOROrder",
				data : JSON.stringify(data),
				type : "POST",
				contentType : "application/json",
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
					$('#loading').show();

				},
				success : function(data, textStatus, request) {
					var success = request.getResponseHeader('success');
					var info = request.getResponseHeader('info');
					if (success != undefined) {
						$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
						$('.alert').hide();
						$('div[id=idGlobalSuccess]').show();
					}
					if (info != undefined) {
						$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
						$('.alert').hide();
						$('div[id=idGlobalInfo]').show();
					}
					var isPageEnable = true;
					var table = constructTable(data, isPageEnable);

					$('#bqitemList').html(table);
					$('#s1_tender_delete_btn').addClass('disabled');
					$('.custom-checkbox.checksubcheckbox').uniform();
					$('.checker span').find('.glyph-icon.icon-check').remove();
					$('.checker span').append('<i class="glyph-icon icon-check"></i>');
					$('#loading').hide();
				},
				error : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('.alert').hide();
						$('div[id=idGlobalError]').show();
					}
					var isPageEnable = true;
					var table = constructTable($.parseJSON(request.responseText), isPageEnable);

					$('#bqitemList').html(table);
					$('#s1_tender_delete_btn').addClass('disabled');
					$('.custom-checkbox.checksubcheckbox').uniform();
					$('.checker span').find('.glyph-icon.icon-check').remove();
					$('.checker span').append('<i class="glyph-icon icon-check"></i>');
					$('#loading').hide();
				},
				complete : function() {
					$('.custom-checkbox.checkallcheckbox').prop("checked", false);
					$.uniform.update()
					$('#loading').hide();
				}
			});
		}
	});

	// numberingTable();

	/* this code use for table drag and drop */

	function addQuestioButton() {

		var btn = '<button class="btn btn-info ph_btn_extra_small hvr-pop hvr-rectangle-out add_question_table">Add Question</button>';
		$(".table-2 tr").each(function() {
			var num = $(this).find(".number").html().trim();
			if (num % 1 == 0) {
				$(this).find(".add_btn_td").html(btn);
				$(this).find('.hvr-rectangle-out1').hide();
			} else {
				$(this).find('.hvr-rectangle-out1').show();
				$(this).find(".add_btn_td").html("Text");
			}
		});
	}

	// Renumber table rows
	function renumber_table() {
		var counttotal = 1;
		$(".diagnosis_list > tbody > tr").each(function() {

			$(this).find('.number').html(counttotal);
			var countsubtotal = 1;
			if ($(this).hasClass('sectionblock')) {
				var blankHtml = '<tr class="preventBlock"><td colspan="8">&nbsp;</td><tr>';
				if ($(this).find('tbody tr').length > 0) {
					$(this).find('tr.preventBlock').remove();
					$(this).find('tbody tr').each(function() {
						$(this).find('.number').html(counttotal + '.' + countsubtotal);
						countsubtotal = countsubtotal + 1;
					});
				} else {
					$(this).find('tbody').append(blankHtml);
				}
			}
			counttotal = counttotal + 1;

		});

		addQuestioButton();

	}

	function deleteBtn(total) {

		// var total = $(".sortable .checked").length;

		if (total > 0) {
			$('#s1_tender_delete_btn').removeClass('disabled');
			$('#s1_tender_copy_btn').removeClass('disabled');
		} else {
			$('#s1_tender_delete_btn').addClass('disabled');
			$('#s1_tender_copy_btn').addClass('disabled');
		}
	}

	$('#s1_tender_delete_btn').click(function(e) {
		e.preventDefault();
		$('#myModalDeleteBQs').modal('show');
		return false;
	});

	/* BQ List Events */
	$(document).delegate('.editBqItem', 'click', function(e) {
		e.preventDefault();
		$(".form-error").remove();
		$("#bqName").parent().removeClass("has-error");
		$("#bqName").removeClass("error");
		$('#bqName').css("border-color", "");
		var formId = $('#formId').val();
		var bqId = $('.editBqItem').attr('data-id');
		var bqDesc = $('#editBqDesc').val();
		var bqName = $('#editBqName').val();
		$('.toggleCreateBq').parent().next().slideDown();
		$('.toggleCreateBq').find('i').addClass('fa-minus').removeClass('fa-plus');
		$('.meeting2-heading').find('h3').text('Edit Schedule of Rate');
		$('#bqSave').text('Update');
	});

	$(document).on("click", ".add_question", function() {
		$(".main-New-Question").toggle();
		$("#creat_seaction_form").hide();
		$(".main-Edit-Question").hide();
		$("#itemId").val('');
	});

	$(document).on("click", ".add_question_table", function() {

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		$("#exampleInputPassword3").val('');
		$("#exampleInputEmail3").val('');
		$("#price_frm").val('');
		$("#desc_box").val('');
		$('#parentId').val($(this).parents('.sub_item').attr('data-id'));
		$("#creat_subitem_form").show();
		$('#creat_subitem_form').find("input[type='text'],textarea").val('');
		$("#itemUnit, #pricingTypes").val('').trigger("chosen:updated");
		$('#creat_subitem_form').find('h3.s1_box_title').text('Add Item');
		$('#itemSave').text(saveLabel);
		$("#itemId").val('');
		$('.extraFields').html('');

		var bqId = $('#bqId').val();
		$.ajax({
			url : getContextPath() + "/buyer/getSourcingSorForNewFields",
			data : {
				bqId : bqId
			},
			type : "GET",
			beforeSend : function(xhr) {

				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var additionalHtml = '';
				var readonly = '';
				var isReq = '';
				$.each(data, function(i, item) {
					var label = '';
					var fieldname = '';
					var id = '';

					if (i == "field1Label") {
						label = item;
						id = "field1Label";
						fieldname = "field1";
						if (data.field1FilledBy == "SUPPLIER") {
							readonly = "readonly";
						} else {
							readonly = "";
						}

						if(data.field1Required != null && data.field1Required ==true) {
							isReq = "required";
						}
					}
					if (i == "field1Label") {
						additionalHtml += '<div class="col-md-6 col-sm-6 col-xs-12 extraEachBlock">' + '<div class="form-group">';
						additionalHtml += '<label>' + label + '</label>';
						additionalHtml += '<input type="text" data-validation="length ' + isReq + '" data-validation-length="0-100" class="form-control" ' + readonly + ' name="' + fieldname + '" id="' + id + '"  placeholder="Enter ' + label
							+ '" value="">' + '</div></div>';
					}

				});
				$('.extraFields').html(additionalHtml);
			},
			complete : function() {
				$('#loading').hide();
			}
		});

		$("#creat_seaction_form").hide();
		$("#add_delete_column").hide();

	});

	$(document).on("click", ".Edit_section_table", function() {
		$(".form-error").remove();
		$("#sectionName").parent().removeClass("has-error");
		$("#sectionName").removeClass("error");
		$('#sectionName').css("border-color", "");
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var bqItemId = $(this).parents('.sub_item').attr('data-id')
		var bqId = $('#bqId').val();
		var formId = $('#formId').val();
		var data = {};
		data["bqId"] = bqId;
		data["bqItemId"] = bqItemId;

		$.ajax({
			url : getContextPath() + "/buyer/getSourcingSorForEdit",
			data : {
				bqId : bqId,
				bqItemId : bqItemId
			},
			type : "GET",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				$.each(data, function(i, item) {
					// console.log(i + ' : ' + item);
					// $("#creat_seaction_form").find('[name="'
					// + i +
					// '"]').val(item);
					if (i == "itemName")
						$("#sectionName").val(item);
					if (i == "itemDescription")
						$("#sectionDescription").val(item);

					$("#creat_subitem_form").hide();
					$("#creat_seaction_form").show();
					$("#add_delete_column").hide();
					$("#itemId").val(bqItemId);
					$('#creat_seaction_form').find('h3.s1_box_title').text('Edit Section ');
					$('#saveBqSection').text('Update');

				});
			},
			complete : function() {
				$('#loading').hide();
			}

		});
	});

	$(document).on("click", ".Edit_subitme_table", function() {
		$("#addNewItems").get(0).reset();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var bqItemId = $(this).parents('.sub_item').attr('data-id')
		var bqId = $('#bqId').val();
		$('#parentId').val($(this).parents('.sub_item').attr('data-parent'));
		var formId = $('#formId').val();

		var data = {};
		$('.extraFields').html('');

		data["bqId"] = bqId;
		data["bqItemId"] = bqItemId;

		$.ajax({
			url : getContextPath() + "/buyer/getSourcingSorForEdit",
			data : {
				bqId : bqId,
				bqItemId : bqItemId
			},
			type : "GET",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var html = '';

				var additionalHtml = '';
				var readonly = '';
				var isReq = '';
				$.each(data.sor, function(i, item) {
					console.log(i + ' : ' + item);
					var label = '';
					var fieldname = '';
					var id = '';
					if (i == "field1Label") {
						label = item;
						id = "field1Label";
						fieldname = "field1";
						if (data.sor.field1FilledBy == "SUPPLIER") {
							readonly = "readonly";
						} else {
							readonly = "";
						}

						if(data.sor.field1Required != null && data.sor.field1Required ==true && data.sor.field1FilledBy != "SUPPLIER") {
							isReq = "required";
						}
					}
					if (i == "field1Label") {
						additionalHtml += '<div class="col-md-6 col-sm-6 col-xs-12 extraEachBlock">' + '<div class="form-group">';
						additionalHtml += '<label>' + label + '</label>';
						additionalHtml += '<input type="text" data-validation="length ' + isReq + '" data-validation-length="0-100" class="form-control"  ' + readonly + ' name="' + fieldname + '" id="' + id + '"  placeholder="Enter ' + label
							+ '" value="">' + '</div></div>';
					}

				});
				$('.extraFields').html(additionalHtml);
				$.each(data, function(i, item) {
					$("#creat_subitem_form").find('[name="' + i + '"]').val(item);
 					$("#extraFields").find('[name="' + i + '"]').val(item);
					$("#creat_subitem_form").show();
					$("#creat_seaction_form").hide();
					$("#add_delete_column").hide();
					$("#itemId").val(bqItemId);
					
					$('#creat_subitem_form').find('h3.s1_box_title').text('Edit Item ');
					$('#itemSave').text('Update');
					// $('#addNewItems').populate(data);
				});
				$("#itemUnit").val(data.uom.uom);
				$('select').trigger("chosen:updated");
			},
			complete : function() {
				$('#loading').hide();
			}
		});

	});

	$(document).on("click", ".Edit_section_btn_table", function() {

		var title = $(this).closest("tr").find(".item_name").clone().children().remove().end().text();
		$(".main-Edit-Question").hide();
		$(".main-New-Question").hide();
		$("#creat_seaction_form").show().find(".section_name").val(title);

	});

	$('.header_table .custom-checkbox').on('change', function() {
		var check = this.checked;
		if (check == false) {
			$(".sortable [type=checkbox]").prop('checked', check).parent().removeClass('checked');
		} else {
			$(".sortable [type=checkbox]").prop('checked', check).parent().addClass('checked');
		}
		deleteBtn(check);
	});

	$(document).on("change", ".sortable [type=checkbox]", function() {
		var childBoxeslength = $(this).closest('.menuDiv').next('ol').find('[type=checkbox]').length;
		if (this.checked && childBoxeslength > 0) {
			var childBoxes = $(this).closest('.menuDiv').next('ol').find('[type=checkbox]');
			childBoxes.prop('checked', true).parent().addClass('checked');
		}
		if (childBoxeslength == 0) {
			var checkParents = $(this).closest('ol').prev('.menuDiv').find('[type=checkbox]').length;
			if (checkParents == 1) {
				$(this).closest('ol').prev('.menuDiv').find('[type=checkbox]').prop('checked', false).parent().removeClass('checked');
			}
		}
		var total = $(".sortable [type=checkbox]").length;
		var checked = $(".sortable [type=checkbox]:checked").length;
		var firstObj = $('.header_table .custom-checkbox');
		if (checked == total) {
			firstObj.prop('checked', true);
		} else {
			firstObj.prop('checked', false);
		}
		$.uniform.update(firstObj);
		deleteBtn(checked);
	});

	$('.column_button_bar').on('click', '#s1_tender_addsection_btn', function(event) {
		event.preventDefault();
		$(".form-error").remove();
		$("#sectionName").removeClass("error");
		if ($('#creat_seaction_form').is(":visible")) {
			$('#creat_seaction_form').hide();
		} else {
			$('.hidecolumnoption').hide();
			$('#creat_seaction_form').show();
		}

		$("#itemId").val('');
		$("#parentId").val('');
		$('#creat_seaction_form').find("input[type='text'],textarea").val('');
		$('#creat_seaction_form').find('h3.s1_box_title').text(createNewSectionLabel);

		$('#saveBqSection').text(saveLabel);
	});

	$('.column_button_bar').on('click', '#s1_tender_adddel_btn', function(event) {
		event.preventDefault();
		if ($('#add_delete_column').is(':visible')) {
			$('#add_delete_column').fadeOut();
			$('.hidecolumnoption').hide();
		} else {
			$('.hidecolumnoption').hide();
			$('#add_delete_column').show();
		}
	});

	$('.column_button_bar').on('click', '#s1_tender_additem_btn', function(event) {

		$("#exampleInputPassword3").val('');
		$("#exampleInputEmail3").val('');
		$("#itemId").val('');
		$("#parentId").val('');
		$('.extraEachBlock').remove();
		$('#creat_subitem_form').find("input[type='text'],textarea").val('');
		$("#itemUnit, #pricingTypes").val('').trigger("chosen:updated");
		$('#creat_subitem_form').find('h3.s1_box_title').text(createNewSectionLabel);
		$('#itemSave').text(saveLabel);

		event.preventDefault();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		var bqId = $('#bqId').val();
		alert(bqId);
		$.ajax({
			url : getContextPath() + "/buyer/getSourcingSorForNewFields",
			data : {
				bqId : bqId
			},
			type : "GET",
			beforeSend : function(xhr) {

				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var additionalHtml = '';
				$('.extraFields').html(additionalHtml);
			},
			complete : function() {
				$('#loading').hide();
			}
		});

		if ($('#creat_subitem_form').is(':visible')) {
			$('.hidecolumnoption').hide();
		} else {
			$('.hidecolumnoption').hide();
			$('#creat_subitem_form').show();
			$('#creat_subitem_form').find('h3.s1_box_title').text(createNewSectionLabel);
			$('#itemSave').text(saveLabel);
		}
	});

	$('.create_list_sectoin').on('click', '.bq_tender_addsub_item', function(event) {
		event.preventDefault();
		if ($('#creat_subitem_form').is(':visible')) {
			$('.hidecolumnoption').hide();
		} else {
			$('.hidecolumnoption').hide();
			$('#creat_subitem_form').show();
			$('#creat_subitem_form').find('h3.s1_box_title').text('Edit Item ');
		}
	});

	$('.closealloptions').click(function() {
		$('.hidecolumnoption').hide();
	});
	$('.table-1 .custom-checkbox').on('change', function() {

		var check = this.checked;
		$(".table-2 [type=checkbox]").each(function() {
			$(".table-2 [type=checkbox]").prop('checked', check);
			$.uniform.update($(this));
			deleteBtn(check);
		});

	});

	$('.table-2 [type=checkbox]').on('change', function() {

		var total = $(".table-2 [type=checkbox]").length;
		var checked = $(".table-2 .checker .checked").length;
		var firstObj = $('.table-1 .custom-checkbox');
		if (checked == total) {
			firstObj.prop('checked', true);
		} else {
			firstObj.prop('checked', false);
		}
		$.uniform.update(firstObj);
		deleteBtn(checked);
	});

	$("#pricingTypes").change(function() {

		if ($("#pricingTypes :selected").val() == "BUYER_FIXED_PRICE") {
			$("#itemUnitPrice").removeAttr("disabled");
		} else {
			$("#itemUnitPrice").attr("disabled", true);
			$("#itemUnitPrice").val("");
			$(".unitPriceClass").find('span').text('');
			$("#itemUnitPrice").parent().removeClass("has-error");
			$("#itemUnitPrice").removeClass("error");
			$('#itemUnitPrice').css("border-color", "");
		}

		$.validate({
			modules : 'logic'
		});
	});

});

$(function() {
	$(document).on("change", "#load_file", function() {
		$(".show_name").html($(this).val());
	});

	var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
	$("#tags").autocomplete({
		source : availableTags
	});
	$("#tagres").autocomplete({
		source : availableTags
	});
});

$(function() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

});

// NEW FUNCTION FOR SAVE BQ ITEMS

$(function() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$('#itemSave').click(function(e) {
		e.preventDefault();
		if (!$('#addNewItems').isValid()) {
			return false;
		}
		var bqItemId = $("#itemId").val();
		var formId = $('#formId').val();
		var itemName = $('#itemName').val();
		// var itemQuantity = $('#itemQuantity').val();
		// var itemUnitPrice = $('#itemUnitPrice').val();
		var itemDesc = $('#itemDesc').val();
		var itemUnit = $('#itemUnit').val();
		var pricingTypes = $('#pricingTypes').val();
		var parentId = $('#parentId').val();
		var bqId = $('#bqIdNewItems').val();
		// var unitBudgetPrice = $('#unitBudgetPrice').val();
		
		var filterVal = $('#chooseSection option:selected').text();
		var searchVal = $('#bqItemSearch').val();
		var selectPageNo = $('#pagination').find('li.active').text();
		var pageNo = parseInt(selectPageNo);
		var pageLen = "50";
		if ($('#selectPageLen option:selected').text()) {
			pageLen = $('#selectPageLen').val();
		}
		var pageLength = parseInt(pageLen);

		console.log(" filterVal :" + filterVal + " searchVal :  " + searchVal);

		var data = {};
		data["formId"] = formId;
		data["itemName"] = itemName;
		// data["quantity"] = itemQuantity.replace(/,/g, '');
		// data["unitPrice"] = itemUnitPrice.replace(/,/g, '');
		// data["unitBudgetPrice"] = unitBudgetPrice.replace(/,/g, '');

		data["itemDescription"] = itemDesc;
		data["uom"] = itemUnit;
		// data["priceType"] = pricingTypes;
		data["parent"] = parentId;
		data["sor"] = bqId;
		data["id"] = bqItemId;

		data["searchVal"] = searchVal;
		data["filterVal"] = filterVal;
		data["pageLength"] = pageLength;
		data["pageNo"] = pageNo;
		
		//additional fields
		for (i = 1; i <= 10; i++) {
			if($('input[name="field' + i +  '"]').val() !== undefined) {
				data["field" + i] = $('input[name="field' + i +  '"]').val();
			}
		}

		var isPageEnable = false;
		if (searchVal == "" || filterVal == "") {
			var isPageEnable = true;
		}

		var ajaxUrl = getContextPath() + "/buyer/createSourcingSorItem";
		if (bqItemId != '') {
			ajaxUrl = getContextPath() + "/buyer/updateSourcingSorItem";
		}
		$.ajax({
			url : ajaxUrl,
			data : JSON.stringify(data),
			type : "POST",
			contentType : "application/json",
			dataType : 'json',
			beforeSend : function(xhr) {
				// console.log("DATA :: "+ JSON.stringify(data));
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var success = request.getResponseHeader('success');
				var info = request.getResponseHeader('info');
				$('#creat_subitem_form').hide();
				$('#demo-form1').find('input[type="text"], textarea').val('');
				$('#bqItemSearch').val(searchVal !== undefined ? searchVal : "");
				$("#itemId").val('');
				$('#parentId').val('');
				if (success != undefined) {
					$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('div[id=idGlobalSuccess]').show();
				}
				if (info != undefined) {
					$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('div[id=idGlobalInfo]').show();
				}
				var table = constructTable(data.bqItemList, isPageEnable);
				// console.log(table);

				// Building Level filter drop down
				var filterTable = '<option value="">&nbsp;</option>';
				$.each(data.leveLOrderList, function(i, item) {
					filterTable += '<option value="">' + item.level + '.' + item.order + '</option>';
				});
				$('#chooseSection').html(filterTable).trigger("chosen:updated");

				$('#bqitemList').html(table);
				$('.custom-checkbox.checksubcheckbox').uniform();
				$('.checker span').find('.glyph-icon.icon-check').remove();
				$('.extraEachBlock').remove();
				$('.checker span').append('<i class="glyph-icon icon-check"></i>');
				$('#loading').hide();
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
		});

	});
});

// NEW FUNCTION FOR SAVE BQ SECTION

$(function() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$('#saveBqSection').click(function(e) {
		console.log("ssssssssssss");
		e.preventDefault();
		if (!$('#demo-form1').isValid()) {
			return false;
		}
		var formId = $('#formId').val();
		var bqItemId = $("#itemId").val();
		var itemName = $('#sectionName').val();
		var itemQuantity = '';
		var itemUnitPrice = '';
		var itemDesc = $('#sectionDescription').val();
		var itemUnit = '';
		var pricingTypes = '';
		var parentId = '';
		var bqId = $('#sectionBqId').val();

		var filterVal = $('#chooseSection option:selected').text();
		var searchVal = $('#bqItemSearch').val();
		var selectPageNo = $('#pagination').find('li.active').text();
		var pageNo = parseInt(selectPageNo);
		var pageLen = "50";
		if ($('#selectPageLen option:selected').text()) {
			pageLen = $('#selectPageLen').val();
		}
		var pageLength = parseInt(pageLen);

		var isPageEnable = false;
		if (searchVal == "" || filterVal == "") {
			var isPageEnable = true;
		}

		var data = {};
		data["formId"] = formId;
		data["itemName"] = itemName;
		data["quantity"] = itemQuantity;
		data["unitPrice"] = itemUnitPrice;
		data["itemDescription"] = itemDesc;
		data["uom"] = itemUnit;
		data["parent"] = parentId;
		data["sor"] = bqId;
		data["id"] = bqItemId;

		data["searchVal"] = searchVal;
		data["filterVal"] = filterVal;
		data["pageLength"] = pageLength;
		data["pageNo"] = pageNo;
		
		var ajaxUrl = getContextPath() + "/buyer/createSourcingSorItem";
		if (bqItemId != '') {
			ajaxUrl = getContextPath() + "/buyer/updateSourcingSorItem";
		}
		$.ajax({
			url : ajaxUrl,
			data : JSON.stringify(data),
			type : "POST",
			contentType : "application/json",
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var success = request.getResponseHeader('success');
				var info = request.getResponseHeader('info');
				$('#creat_seaction_form').hide();
				$('#demo-form1').find('input[type="text"], textarea').val('');
				$('#bqItemSearch').val(searchVal !== undefined ? searchVal : "");
				$("#itemId").val('');
				$('#parentId').val('');
				if (success != undefined) {
					$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('div[id=idGlobalSuccess]').show();
				}
				if (info != undefined) {
					$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('div[id=idGlobalInfo]').show();
				}
				var table = constructTable(data.bqItemList, isPageEnable);

				// Building Level filter drop down
				var filterTable = '<option value="">&nbsp;</option>';
				$.each(data.leveLOrderList, function(i, item) {
					filterTable += '<option value="">' + item.level + '.' + item.order + '</option>';
				});
				$('#chooseSection').html(filterTable).trigger("chosen:updated");

				$('#bqitemList').html(table);
				$('.custom-checkbox.checksubcheckbox').uniform();
				$('.checker span').find('.glyph-icon.icon-check').remove();
				$('.extraEachBlock').remove();
				$('.checker span').append('<i class="glyph-icon icon-check"></i>');
				$('#loading').hide();
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
		});
	});
});

// NEW FUNCTION FOR DELETE BQ ITEMS

$('#idConfirmDeleteBQs').click(function() {

	console.log("dadasdasadasdasad");

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$('#myModalDeleteBQs').modal('hide');
	var items = "";
	$('.sortable .checksubcheckbox:checked').each(function() {
		items += $(this).val() + ",";
	});

	var bqId = $('#bqIdNewItems').val();
	var filterVal = $('#chooseSection option:selected').text();
	var searchVal = $('#bqItemSearch').val();
	var selectPageNo = $('#pagination').find('li.active').text();
	var pageNo = parseInt(selectPageNo);
	var pageLen = "50";
	if ($('#selectPageLen option:selected').text()) {
		pageLen = $('#selectPageLen').val();
	}
	var pageLength = parseInt(pageLen);

	var isPageEnable = false;
	if (searchVal == "" || filterVal == "") {
		var isPageEnable = true;
	}
	// // return false;
	$.ajax({
		url : getContextPath() + "/buyer/deleteSourcingSorItems/" + bqId,
		data : {
			'items' : items,
			'filterVal' : filterVal,
			'searchVal' : searchVal,
			'pageLength' : pageLength,
			'pageNo' : pageNo
		},

		type : "POST",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success : function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			var info = request.getResponseHeader('info');
			if (success != undefined) {
				$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalSuccess]').show();
			}
			if (info != undefined) {
				$('p[id=idGlobalSuccessMessage]').html(info != null ? info.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalSuccess]').show();
			}
			var table = constructTable(data.bqItemList, isPageEnable);
			// console.log(table);

			// Building Level filter drop down
			var filterTable = '<option value="">&nbsp;</option>';
			$.each(data.leveLOrderList, function(i, item) {
				filterTable += '<option value="">' + item.level + '.' + item.order + '</option>'; // '<li
				// class="active-result">'
				// + item.level
				// +'.' +
				// item.order +
				// '</li>';
			});
			$('#chooseSection').html(filterTable).trigger("chosen:updated");

			$('#bqitemList').html(table);
			$('.custom-checkbox').prop('checked', false).change().uniform();
			$('.custom-checkbox.checksubcheckbox').uniform();
			$('.checker span').find('.glyph-icon.icon-check').remove();
			$('.checker span').append('<i class="glyph-icon icon-check"></i>');
			// numberingTable();
			/*
			 * setTimeout(function() { $('.alert').hide(); }, 2000);
			 */
			$('#loading').hide();
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
			/*
			 * setTimeout(function() { $('.alert').hide(); }, 2000);
			 */
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

$(document).delegate('#idConfirmDelete', 'click', function(e) {
	console.log("asdasdwsrfwerasfdasfs");

	e.preventDefault();

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var bqId = $('#bqId').val();
	var label = $('.addColumsBlock').eq($('#deleteColpos').val()).find('td:first-child').find('input').attr('name');
	$('.addColumsBlock').eq($('#deleteColpos').val()).remove();
	$('#myModalDeleteColum').modal('hide');
	$('#deleteColpos').val('');
	$('.addColumsBlock').each(function() {
		$(this).attr('data-pos', $(this).index() + 1);
	});

	var pageLen = "50";
	if ($('#selectPageLen option:selected').text()) {
		pageLen = $('#selectPageLen').val();
	}
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

	var isPageEnable = false;
	if (searchVal == "" || filterVal == "") {
		isPageEnable = true;
	}

	$.ajax({
		url : getContextPath()+"/buyer/deleteSorNewField",
		data : {
			bqId : bqId,
			label : label,
			'filterVal' : choosenVal,
			'searchVal' : searchVal,
			'pageNo' : pageNo,
			'pageLength' : pageLength
		},
		type : "GET",
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			// console.log("Success :" + success);
			$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalSuccess]').show();

			var table = constructTable(data, isPageEnable);
			// console.log(table);
			$('#bqitemList').html(table);
			$('.custom-checkbox.checksubcheckbox').uniform();
			$('.checker span').find('.glyph-icon.icon-check').remove();
			$('.extraEachBlock').remove();
			$('.checker span').append('<i class="glyph-icon icon-check"></i>');
			// numberingTable();
			$('#loading').hide();
		},
		error : function(request, textStatus, errorThrown) {
			var error = request.getResponseHeader('error');
			$('p[id=idGlobalErrorMessage]').html(error);
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});

	// $('#newFieldsSave').click();
});

$('#newFieldsSave').click(function(e) {
	e.preventDefault();
	if (!$('#newFieldForm').isValid()) {
		return false;
	}
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var eventId = $('#eventId').val();
	var bqId = $('#bqId').val();

	// var fieldData = {};
	if (!$('.addColumsBlock').length > 0) {
		// console.log("currentPos : 0");
		$('p[id=idGlobalErrorMessage]').html("Please add atleast one column");
		$('.alert').hide();
		$('div[id=idGlobalError]').show();
		return false;
	}

	var tempFeildArr = [];
	var duplicateField = false;

	var data = {};
	$('.addColumsBlock').each(function() {
		var currentPos = $(this).attr('data-pos');
		var fieldName = $.trim($(this).find('td:first-child').find('input').val());
		if (fieldName != '') {

			data['id'] = bqId;
			data['field' + currentPos + 'Label'] = fieldName;
			data['field' + currentPos + 'FilledBy'] = $.trim($(this).find('td:nth-child(2)').find('select').val());
			data['field' + currentPos + 'ToShowSupplier'] = $(this).find('td:nth-child(3)').find('input[type="checkbox"]').prop('checked');
			data['field' + currentPos + 'Required'] = $(this).find('td:nth-child(4)').find('input[type="checkbox"]').prop('checked');
		}
		// checking duplicate column
		for (var i = 0; i < tempFeildArr.length && !duplicateField; i++) {
			if (tempFeildArr[i] === fieldName.toUpperCase()) {
				$('p[id=idGlobalErrorMessage]').html("\"" + fieldName + "\" is already exists");
				$('.alert').hide();
				$('.alert-danger').hide();
				$('div[id=idGlobalError]').show();
				duplicateField = true;
			}
		}
		tempFeildArr.push(fieldName.toUpperCase());
	});

	if (duplicateField) {
		return false;
	}
	var pageLen = "50";
	if ($('#selectPageLen option:selected').text()) {
		pageLen = $('#selectPageLen').val();
	}
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

	var isPageEnable = false;
	if (searchVal == "" || filterVal == "") {
		isPageEnable = true;
	}
	data['filterVal'] = choosenVal;
	data['searchVal'] = searchVal;
	data['pageNo'] = pageNo;
	data['pageLength'] = pageLength;

	// fieldData.add(data);
	// console.log(JSON.stringify(data));

	$.ajax({
		url : getContextPath() + "/buyer/addNewColumnsSor",
		data : JSON.stringify(data),
		type : "POST",
		contentType : "application/json",
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			var info = request.getResponseHeader('info');
			$('#creat_subitem_form').hide();
			$('#demo-form1').find('input[type="text"], textarea').val('');
			$("#itemId").val('');
			if (success != undefined) {
				$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalSuccess]').show();
			}
			if (info != undefined) {
				$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalInfo]').show();
			}

			var table = constructTable(data, isPageEnable);
			$('#bqitemList').html(table);
			$('.custom-checkbox.checksubcheckbox').uniform();
			$('.checker span').find('.glyph-icon.icon-check').remove();
			$('.extraEachBlock').remove();
			$('.checker span').append('<i class="glyph-icon icon-check"></i>');
			// numberingTable();
			$('#add_delete_column').hide();
			/*
			 * setTimeout(function() { $('.alert').hide(); }, 2000);
			 */
			$('#loading').hide();
		},
		error : function(request, textStatus, errorThrown) {
			var error = request.getResponseHeader('error');
			$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});

});

$('#AddColumnsToList').click(function(e) {
	e.preventDefault();
	addColumActivedeact($(this));
});

$(document).delegate('.s1_remove_tr', 'click', function(e) {
	e.preventDefault();
	// console.log($(this).parents('.addColumsBlock').index());
	$('#deleteColpos').val($(this).parents('.addColumsBlock').index());
	$('#myModalDeleteColum').modal('show');
	$("#AddColumnsToList").prop("disabled", false);
	// $(this).parents('.addColumsBlock').remove();

});

function addColumActivedeact(addBlock) {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var data = $('#bqFilledBy').val();
	// alert("DATA :: "+ data);
	if ($('.addColumsBlock').length < 1) {
		var posIndex = $('.addColumsBlock').length + 1;
		/*
		 * $.ajax({ url : getBuyerContextPath('filledByList'), type : "GET", contentType : "application/json", dataType : 'json', beforeSend :
		 * function(xhr) { xhr.setRequestHeader(header, token); xhr.setRequestHeader("Accept", "application/json");
		 * xhr.setRequestHeader("Content-Type", "application/json"); $('#loading').show(); }, success : function(data, textStatus, request) {
		 * 
		 */
		// var filledBy = $("#bqFilledBy").attr("value");
		var fieldFilledBy = "field" + posIndex + "FilledBy";
		var fieldLabelId = "field" + posIndex + "Label";

		var html = '<tr class="addColumsBlock" data-pos="' + posIndex + '">';
		html += '<td class="width_300">';
		html += '<div class="form-group s1-mrg-10">';
		html += '<input type="text" class="form-control fieldLabel" placeholder="Name" id="' + fieldLabelId + '" name="field' + posIndex + '" data-validation="required length" data-validation-length="max32">';
		html += '</div></td>';
		html += '<td class="width_300">';
		html += '<div class="form-group s1-mrg-10">';
		html += '<select data-validation="required" id="' + fieldFilledBy + '" name="' + fieldFilledBy + '" class="chosen-select filledBy">';
		html += '<option value="BUYER">BUYER</option>';
		html += '<option value="SUPPLIER">SUPPLIER</option>';
		html += '<option value="BOTH">BOTH</option>';
		html += '</select>';
		html += '</div></td>';
		html += '<td class="width_100">';
		html += '<div class="checkbox checkbox-info checkbox_td_center s1-mrg-15">';
		html += '<label> <input type="checkbox" id="field' + posIndex + 'ToShowSupplier" name="field' + posIndex + 'ToShowSupplier"  class="custom-checkbox checksubcheckboxdata"/>';
		html += '</label></div></td>' + '<td class="text_table_center width_100">' + '<div class="checkbox checkbox-info checkbox_td_center s1-mrg-15">';
		html += '<label> <input type="checkbox" id="field' + posIndex + 'Required" name="field' + posIndex + 'Required"  class="custom-checkbox checksubcheckboxdata" />' + '</label></div></td>';
		html += '<td class=" width_100" id="fieldRemove' + posIndex + '" name="fieldRemove' + posIndex + '"><a href="javascript:void(0)" class="s1_remove_tr">' + '<i aria-hidden="true" class="glyph-icon icon-close"></i>';
		html += '</a></td></tr>';
		// console.log(html);
		addBlock.parents('tr').before(html);
		$('.addColumsBlock').find('select').chosen();
		$('.addColumsBlock').find('.glyph-icon.icon-search').remove();
		$('.addColumsBlock').find('.glyph-icon.icon-caret-down').remove();
		$('.addColumsBlock').find(".chosen-search").append('<i class="glyph-icon icon-search"></i>');
		$('.addColumsBlock').find(".chosen-single div").html('<i class="glyph-icon icon-caret-down"></i>');
		$('.custom-checkbox.checksubcheckboxdata').uniform();
		$('.checker span').find('.glyph-icon.icon-check').remove();
		$('.checker span').append('<i class="glyph-icon icon-check"></i>');
		/*
		 * }, complete : function() { $('#loading').hide(); } });
		 */
	} else {
		$("#AddColumnsToList").attr("disabled", "disabled");
	}
}

function renderGrid(item) {
	// var suspensionType = $('#suspensiontype').val();
	var sourcingstatus = $('#sourcingstatus').val();
	var table = '';
	var countNewData = 1;
	$('.extraFieldHeaders').remove();
	table += '<li style="display: list-item;" class="mjs-nestedSortable-leaf" id="menuItem_1" data-item="' + item.id + '" data-parent="" data-level="' + item.level + '" data-order="' + item.order + '"><div class="menuDiv">';
	// if (item.parent == undefined || item.parent.id == null) {
	table += '<table class="table data ph_table diagnosis_list sorted_table">' + '<tbody><tr class="sub_item" data-id="' + item.id + '" data-pricetype="' + item.priceType + '">';
	table += '<td class="width_50 width_50_fix move_col align-left"><a href="javascript:void(0);"><i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i></a></td>' + '<td class="width_50 align-left width_50_fix">';
	table += '<div class="checkbox checkbox-info ui-sortable-handle"><label>';
	if (sourcingstatus === 'DRAFT') {
		table += '<div class="checker" id="uniform-inlineCheckbox116"><span>';
		table += '<input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value = "' + item.id + '"><i class="glyph-icon icon-check"></i></span></div>';
	}
	table += '</label></div></td>' + '<td class="width_100_fix_custom"><span class="sectionD">' + item.level + '</span></td>' + '<td class="width_200 width_200_fix align-left"><span class="item_name sectionD">' + item.itemName + '</span>'
	if (item.itemDescription != undefined || item.itemDescription == "") {
		table += '<span class="item_detail s1_view_desc">View Description</span>' + '<p class="s1_tent_tb_description s1_text_small">' + item.itemDescription + '</p>'
	}

	table += '</td><td class="width_100 width_100_fix align-center">&nbsp;</td>' + '<td class="width_100 width_140_fix align-center">&nbsp;</td>' + '<td class="width_100 width_140_fix align-center">&nbsp;</td>';
	// $('.extraFieldHeaders').remove();
	// $('.header_table.header').find('th:last-child').attr('colspan',
	// countNewData);
	table += '<td class="width_300 width_300_fix">';
	table += '		<a title="" class="btn btn-sm edit-btn-table ph_btn_small hvr-pop hvr-rectangle-out1 Edit_section_table" href="javascript:void(0);" style="display: inline-block;">Edit</a>';
	table += '       <a title="" class="btn btn-sm add-btn-table ph_btn_small btn-default marg-left-10 add_question_table" href="javascript:void(0)">Add Item</a>';
	table += '</td></tr></tbody></table>';
	// }
	table += '</div>';
	if (item.children != undefined && item.children != null) {
		table += '<ol>'
		$.each(item.children, function(i, child) {
			table += '<li style="display: list-item;" class="mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="menuItem_2_1" data-foo="baz" data-item="';
			table += child.id;
			table += '" data-parent="';
			table += item.id;
			table += '" data-level="' + child.order + '" data-order="' + child.order + '">';
			table += '<div class="menuDiv"><table class="table data ph_table diagnosis_list sorted_table">';
			table += '<tbody><tr class="sub_item" data-id="' + child.id + '" data-pricetype="' + child.priceType + '"  data-parent="' + item.id + '">';
			table += '<td class="width_50 width_50_fix align-left move_col"><a href="javascript:void(0);"><i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i></a></td>';
			table += '<td class="width_50 align-left  width_50_fix">';
			table += '<div class="checkbox checkbox-info ui-sortable-handle"><label>';
			if (sourcingstatus === 'DRAFT') {
				table += '<div class="checker" id="uniform-inlineCheckbox116"><span><input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value = "' + child.id + '">';
				table += '<i class="glyph-icon icon-check"></i></span></div>';
			}
			table += '</label></div></td>';
			table += '<td class="width_100_fix_custom"><span class="itemNameD">' + child.level + '.' + child.order + '</span></td>';
			table += '<td class="width_200 width_200_fix align-left"><span class="item_name itemNameD">';
			if (child.priceType == 'TRADE_IN_PRICE') {
				table += '<span class="bs-label label-info">Trade In Price</span>&nbsp;';
			} else if (child.priceType == 'BUYER_FIXED_PRICE') {
				table += '<span class="bs-label label-success">Buyer Fixed Price</span>&nbsp;';
			}

			table += child.itemName + '</span>';
			if (child.itemDescription != undefined || item.itemDescription == "") {
				table += '<span class="item_detail s1_view_desc">View Description</span>' + '<p class="s1_tent_tb_description s1_text_small">' + child.itemDescription + '</p>';
			}
			table += '</td>';
			if (child.uom != undefined) {
				table += '<td class="width_100 width_100_fix align-center">' + child.uom.uom + '</td>';
			} else {
				table += '<td class="width_100 width_100_fix align-center"> &nbsp;</td>';
			}
			if (child.quantity != undefined) {
				table += '<td class="width_100 width_140_fix align-center">' + ReplaceNumberWithCommas(child.quantity.toFixed($('#sourcingDecimal').val())) + '</td>';
			} else {
				table += '<td class="width_100 width_140_fix align-center"> &nbsp; </td>';
			}
			if (child.unitPrice != undefined) {
				table += '<td class="width_100 width_140_fix align-center">' + ReplaceNumberWithCommas(child.unitPrice.toFixed($('#sourcingDecimal').val())) + '</td>';
			} else {
				table += '<td class="width_100 width_140_fix align-center"> &nbsp; </td>';
			}
			table += '<td class="width_300 width_300_fix">';
			table += '<a title="" class="btn btn-sm ph_btn_small edit-btn-table hvr-pop hvr-rectangle-out1 Edit_subitme_table" href="javascript:void(0);" style="display: inline-block;">Edit</a>';
			table += '<a title="" class="btn btn-sm add-btn-table ph_btn_small btn-default marg-left-10" style="visibility:hidden" href="javascript:void(0)"></a>';
			table += '</td></tr></tbody></table></div></li>';
		});
		table += '</ol>';
	}
	table += '</li>';
	return table;
}

$('#bqSave').click(function(e) {
	e.preventDefault();
	if (!$('#bqFormAddEdit').isValid()) {
		return false;
	}
	var formId = $('#formId').val();
	var bqName = $('#bqName').val();
	var bqId = $.trim($('#bqId').val());
	var bqDesc = $('#bqDesc').val();
	var urlAjax = "";
	var data = {};
	data["id"] = bqId;
	data["formId"] = formId;
	data["sorName"] = bqName;
	data["sorDesc"] = bqDesc;
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	if ($.trim(bqId) != '') {
		urlAjax = getContextPath() + "/buyer/updateSourcingSorList";
	} else {
		urlAjax = getContextPath() + "/buyer/createSourcingRequestSorList";
	}
	$.ajax({
		url : urlAjax,
		data : JSON.stringify(data),
		type : "POST",
		contentType : "application/json",
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			var info = request.getResponseHeader('info');

			if (success != undefined) {
				$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalSuccess]').show();
			}
			if (info != undefined) {
				$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalInfo]').show();
			}
			$('#bqFormAddEdit').find('#bqId').val('');
			$('#bqFormAddEdit').find('#bqDesc').val('');
			$('#bqFormAddEdit').find('#bqName').val('');
			$('#bqSave').text(saveLabel);
			$('.toggleCreateBq').parent().next().slideToggle();
			$('.toggleCreateBq').find('i').toggleClass('fa-minus').toggleClass('fa-plus');
			buildtable(data,formId,token);
			window.location.reload();
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error') : '');
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

function deleteBQ(bqId) {
	$("#bqDisplay").css("display", "none");
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var formId = $('#formId').val();
	var data = {};
	data["id"] = bqId;
	data["formId"] = formId;
	$.ajax({
		url : getContextPath() + "/buyer/deleteSourcingSor",
		data : JSON.stringify(data),
		type : "POST",
		contentType : "application/json",
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			var info = request.getResponseHeader('info');
			if (success != undefined) {
				$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalSuccess]').show();
			}
			if (info != undefined) {
				$('p[id=idGlobalSuccessMessage]').html(info != null ? info.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalSuccess]').show();
			}
			buildtable(data,formId,token);
			window.location.reload();
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
}
/* BQ List Events End */

// Fetch envelope data
$('.editBqItem').click(function(e) {
	e.preventDefault();
	var formId = $('#formId').val();
	var bqId = $('.editBqItem').attr('data-id');
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$.ajax({
		type : "POST",
		url : getContextPath() + "/buyer/getSourcingSorbyId",
		data : {
			'formId' : formId,
			'bqId' : bqId
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success : function(data) {

			$('#bqId').val(data.id);
			$('#bqDesc').val(data.description);
			$('#bqName').val(data.name);
		},
		error : function(request, textStatus, errorThrown) {
			console.log("error");
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}

$(document).delegate("#resetButton", "click", function(e) {
	e.preventDefault();
	item_level_order = '';
	var pageLen = "50";
	if ($('#selectPageLen option:selected').text()) {
		pageLen = $('#selectPageLen').val();
	}
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
	// console.log(" pageLength : "+pageLen + "searchVal :"+searchVal +
	// " choosenVal : "+choosenVal + " pageNo :" +pageNo) ;

	var isPageEnable = false;
	if (searchVal == "" || filterVal == "") {
		var isPageEnable = true;
	}
	var isChooseSection = false;
	searchFilterBqItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable, isChooseSection);

});

$(document).ready(function() {

	$('#selectPageLen').change(function() {
		var pageLen = "50";
		if ($('#selectPageLen option:selected').text()) {
			pageLen = $('#selectPageLen').val();
		}
		var searchVal = $('#bqItemSearch').val();
		var choosenVal = $('#chooseSection option:selected').text();
		var pageLength = parseInt(pageLen);
		var selectPageNo = $('#pagination').find('li.active').text();
		var pageNo = parseInt(selectPageNo);
		console.log(" pageLength  : " + pageLen + "searchVal :" + searchVal + " choosenVal  : " + choosenVal + " pageNo :" + pageNo);
		// New requirement for filter fetch selected filter
		// value page records.
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
		var isChooseSection = false;
		searchFilterBqItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable, isChooseSection);

	});

	$('#chooseSection').change(function() {
		var choosenVal = "";
		if ($('#chooseSection option:selected').text()) {
			choosenVal = $('#chooseSection option:selected').text();
		}
		item_order = choosenVal.split(".")[1];
		item_level_order = choosenVal.replace(".", "_");
		item_level_order = "item_" + item_level_order;
		if (!$('#chooseSection option:selected').text()) {
			item_level_order = '';
		}
		console.log("item_level_order :" + item_level_order + "==item_order :" + item_order);

		console.log("choosenVal :" + choosenVal);
		var selectedIndex = $('#chooseSection option:selected').index();
		console.log("selectedIndex :" + selectedIndex);
		var searchVal = $('#bqItemSearch').val();
		var pageLength = $('#selectPageLen').val();
		var selectPageNo = $('#pagination').find('li.active').text();

		var pagination = jQuery('.pagination').data('twbsPagination');
		// New requirement for filter fetch selected filter
		// value page records.
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
		var isChooseSection = true;
		searchFilterBqItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable, isChooseSection);
	});

	$('#bqItemSearch').keyup(function(e) {
		e.preventDefault();

		if ($(this).val() == "") {
			item_level_order = '';
			var searchVal = $(this).val();
			var choosenVal = ''; // $('#chooseSection
			// option:selected').text();
			var pageLength = $('#selectPageLen').val();
			var selectPageNo = $('#pagination').find('li.active').text();

			var pagination = jQuery('.pagination').data('twbsPagination');
			// New requirement for filter fetch selected filter
			// value page records.
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

			console.log(" pageNo  : " + pageNo);
			var isPageEnable = true;
			var isChooseSection = false;
			searchFilterBqItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable, isChooseSection);
		}
		if (this.value.length < 3)
			return;
		var searchVal = $(this).val();
		var choosenVal = ''; // $('#chooseSection
		// option:selected').text();
		var pageLength = $('#selectPageLen').val();

		var selectPageNo = $('#pagination').find('li.active').text();

		var pagination = jQuery('.pagination').data('twbsPagination');
		var pageToShow = 1;
		pagination.show(pageToShow);

		var pageNo = parseInt(pageToShow);
		console.log(" pageNo  : " + pageNo);

		// var pageNo = parseInt(selectPageNo);
		if (this.value.length > 2 || this.value.length == 0)
			var isPageEnable = false;
		var isChooseSection = false;
		searchFilterBqItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable, isChooseSection);
	});

});

function searchFilterBqItem(choosenVal, searchVal, pageNo, pageLength, isPageEnable, isChooseSection) {
	console.log("ddddddddd");
	var formId = $('#formId').val();
	var bqId = $('.editBqItem').attr('data-id');
	var filterVal = choosenVal;
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$.ajax({
		type : "POST",
		url : getContextPath() + "/buyer/getSourcingSorItemForSearchFilter",
		data : {
			'formId' : formId,
			'bqId' : bqId,
			'filterVal' : filterVal,
			'searchVal' : searchVal,
			'pageNo' : pageNo,
			'pageLength' : pageLength
		},
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
		},
		success : function(data) {
			var table = constructTable(data.bqItemList, isPageEnable);
			$('#bqitemList').html(table);
			$('.custom-checkbox.checksubcheckbox').uniform();
			$('.checker span').find('.glyph-icon.icon-check').remove();
			$('.extraEachBlock').remove();
			$('.checker span').append('<i class="glyph-icon icon-check"></i>');
			$('#loading').hide();

			// set color animation for filter
			if (item_level_order.length) {
				$('.' + item_level_order).each(function() {
					$.each(this.cells, function() {
						$(this).animate({
							backgroundColor : "#ffff00"
						}, 2000);
					});
				});

				setTimeout(function() {
					$('.' + item_level_order).each(function() {
						$.each(this.cells, function() {
							if (item_order == '0') {
								$(this).animate({
									backgroundColor : "#ffffff"
								}, 2000);
							} else {
								$(this).animate({
									backgroundColor : "#eef7fc"
								}, 2000);
							}
						});
					});
				}, 4000);
			}

			// set pagination according to result
			visiblePage = 5;
			totalPage = Math.ceil(data.totalBqItemCount / pageLength);
			if (totalPage == 0) {
				totalPage = 1;
			}

			if (totalPage < 5) {
				visiblePage = totalPage;
			}
			var opts = {
				totalPages : 500,
				visiblePages : 5
			};

			$('.pagination').twbsPagination('destroy');
			$('.pagination').twbsPagination($.extend(opts, {
				totalPages : totalPage,
				visiblePages : visiblePage
			}));
			var pagination = jQuery('.pagination').data('twbsPagination');
			pagination.show(pageNo);

			// Building Level filter drop down
			if (!isChooseSection) {
				console.log("!isChooseSection :" + !isChooseSection);
				var filterTable = '<option value="">&nbsp;</option>';
				$.each(data.leveLOrderList, function(i, item) {
					filterTable += '<option value="">' + item.level + '.' + item.order + '</option>'; // '<li
					// class="active-result">'
					// +
					// item.level
					// +'.'
					// +
					// item.order
					// +
					// '</li>';
				});
				$('#chooseSection').html(filterTable).trigger("chosen:updated");
			}

		},
		error : function(request, textStatus, errorThrown) {
			console.log("error");
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
}

function drawSection(item, disableDragDrop, isPageEnable) {
	// var suspensionType = $('#suspensiontype').val();
	var sourcingstatus = $('#sourcingstatus').val();
	var countNewData = 1;
	var table = '';

	table += '<div class="menuDiv">';
	table += '<table class="table data ph_table diagnosis_list sorted_table" id="table_id">';
	table += '<tr class="sub_item item_' + item.level + '_' + item.order + '" data-id="' + item.id + '" data-priceType="' + item.priceType + '">';
	table += '<td class="width_50 width_50_fix move_col align-left"><a href="javascript:void(0);">';
	table += '				<i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>';
	table += '			</a></td>';
	table += '	<td class="width_50 width_50_fix align-left">';
	table += '		<div class="checkbox checkbox-info">';
	table += '				<label>';
	if ((sourcingstatus === 'DRAFT') && isPageEnable) {
		table += '<div class="checker" id="uniform-inlineCheckbox116"><span>';
		table += '<input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value = "' + item.id + '"><i class="glyph-icon icon-check"></i></span></div>';
	}
	table += '		</label>';
	table += '	</div>';
	table += '	</td>';
	table += '	<td class="width_100_fix_custom align-left"><span class="sectionD">' + item.level + '.' + item.order + '</span></td>';
	table += '	<td class="width_300 width_300_fix align-left"><span class="item_name sectionD">' + item.itemName + '</span>';
	if (item.itemDescription != undefined || item.itemDescription == "") {
		table += '			<span class="item_detail s1_view_desc">View Description';
		table += '			</span>';
		table += '			<p class="s1_tent_tb_description s1_text_small">' + item.itemDescription + '</p>';
	}
	table += '</td>';
	if (item.sor.field1Label != undefined) {
		if (item.field1 != undefined) {
			table += '<td class="width_200 width_200_fix  align-center">' + item.field1 + '</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field1Label + '</th>');
	}
	if (item.sor.field2Label != undefined) {
		if (item.field2 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">' + item.field2 + '</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field2Label + '</th>');
	}
	if (item.sor.field3Label != undefined) {
		if (item.field3 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">' + item.field3 + '</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field3Label + '</th>');
	}

	if (item.sor.field4Label != undefined) {
		if (item.field4 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">' + item.field4 + '</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field4Label + '</th>');
	}
	if (item.sor.field5Label != undefined) {
		if (item.field5 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">' + item.field5 + '</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field5Label + '</th>');
	}
	if (item.sor.field6Label != undefined) {
		if (item.field6 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">' + item.field6 + '</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field6Label + '</th>');
	}
	if (item.sor.field7Label != undefined) {
		if (item.field7 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">' + item.field7 + '</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field7Label + '</th>');
	}
	if (item.sor.field8Label != undefined) {
		if (item.field8 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">' + item.field8 + '</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field8Label + '</th>');
	}
	if (item.sor.field9Label != undefined) {
		if (item.field9 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">' + item.field9 + '</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field9Label + '</th>');
	}
	if (item.sor.field10Label != undefined) {
		if (item.field10 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">' + item.field10 + '</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header > tbody > tr').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field10Label + '</th>');
	}
	
	table += '		<td class="width_300 width_300_fix">';
	table += '		<a title="" class="btn btn-sm edit-btn-table ph_btn_small hvr-pop hvr-rectangle-out1 Edit_section_table" href="#creat_seaction_form" style="display: inline-block;">Edit</a>';
	table += '       <a title="" class="btn btn-sm ph_btn_small btn-info marg-left-10 add_question_table" href="#creat_subitem_form">Add Item</a>';
	table += '</td>';

	table += '	</tr>';
	table += '	</table>';
	table += '</div>';
	return table;
}

function drawItems(item, disableDragDrop, isPageEnable) {
	// var suspensionType = $('#suspensiontype').val();
	var sourcingstatus = $('#sourcingstatus').val();
	var countNewData = 1;
	// alert("item id:" + item.id);
	// alert("parent :" + item.parent);
	// alert("parent id :" + item.parent.id);
	var table = '';
	if (item.parent != undefined)
		table += '<li style="display: list-item;" class="mjs-nestedSortable-branch mjs-nestedSortable-expanded ' + disableDragDrop + ' " id="menuItem_2_1" data-foo="baz" data-item="' + item.id + '" data-parent="' + item.parent.id + '" data-level="'
				+ item.order + '" data-order="' + item.order + '">';
	table += '<div class="menuDiv sub-color-change">';
	table += '	<table class="table data ph_table diagnosis_list sorted_table" id="table_id">';
	if (item.parent != undefined)
		table += '		<tr class="sub_item item_' + item.level + '_' + item.order + '" data-id="' + item.id + '" data-priceType="' + item.priceType + '" data-parent="' + item.parent.id + '">';
	table += '			<td class="width_50 width_50_fix move_col align-left">';
	table += '			<a href="javascript:void(0);">';
	table += '				<i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>';
	table += '			</a>';
	table += '		</td>';
	table += '		<td class="width_50 width_50_fix align-left">';
	table += '			<div class="checkbox checkbox-info">';
	table += '				<label>';
	if ((sourcingstatus === 'DRAFT') && isPageEnable) {
		table += '           <div class="checker" id="uniform-inlineCheckbox116"><span><input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value = "' + item.id + '">';
		table += '           <i class="glyph-icon icon-check"></i></span></div>';
	}
	table += '				</label>';
	table += '			</div>';
	table += '		</td>';
	table += '		<td class="width_100_fix_custom align-left">';
	table += '			<span class="itemNameD">' + item.level + '.' + item.order + '</span>';
	table += '		</td>';
	table += '		<td class="width_200 width_200_fix align-left">';
	table += '			<span class="item_name itemNameD">';
	table += item.itemName + '</span>';
	if (item.itemDescription != undefined || item.itemDescription == "") {
		table += '		<span class="item_detail s1_view_desc">';
		table += '				View Description';
		table += '			</span>';
		table += '		<p class="s1_tent_tb_description s1_text_small">' + item.itemDescription + '</p>';
		table += '	 </td>';
	}
	if (item.uom != undefined) {
		table += '			<td class="width_100 width_100_fix align-left">' + item.uom.uom + '</td>';
	} else {
		table += '			<td class="width_100 width_100_fix align-left"> &nbsp;</td>';
	}

	if (item.sor.field1Label != undefined) {
		if (item.field1 != undefined) {
			table += '			<td class="width_200 width_200_fix  align-center">' + item.field1 + '</td>';
		} else {
			table += '			<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field1Label + '</th>');
	}
	if (item.sor.field2Label != undefined) {
		if (item.field2 != undefined) {
			table += '			<td class="width_200 width_200_fix align-center">' + item.field2 + '</td>';
		} else {
			table += '			<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field2Label + '</th>');
	}
	if (item.sor.field3Label != undefined) {
		if (item.field3 != undefined) {
			table += '			<td class="width_200 width_200_fix align-center">' + item.field3 + '</td>';
		} else {
			table += '			<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field3Label + '</th>');
	}

	if (item.sor.field4Label != undefined) {
		if (item.field4 != undefined) {
			table += '			<td class="width_200 width_200_fix align-center">' + item.field4 + '</td>';
		} else {
			table += '			<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field4Label + '</th>');
	}
	if (item.sor.field5Label != undefined) {
		if (item.field5 != undefined) {
			table += '			<td class="width_200 width_200_fix align-center">' + item.field5 + '</td>';
		} else {
			table += '			<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field5Label + '</th>');
	}
	if (item.sor.field6Label != undefined) {
		if (item.field6 != undefined) {
			table += '			<td class="width_200 width_200_fix align-center">' + item.field6 + '</td>';
		} else {
			table += '			<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field6Label + '</th>');
	}
	if (item.sor.field7Label != undefined) {
		if (item.field7 != undefined) {
			table += '			<td class="width_200 width_200_fix align-center">' + item.field7 + '</td>';
		} else {
			table += '			<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field7Label + '</th>');
	}
	if (item.sor.field8Label != undefined) {
		if (item.field8 != undefined) {
			table += '			<td class="width_200 width_200_fix align-center">' + item.field8 + '</td>';
		} else {
			table += '			<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field8Label + '</th>');
	}
	if (item.sor.field9Label != undefined) {
		if (item.field9 != undefined) {
			table += '			<td class="width_200 width_200_fix align-center">' + item.field9 + '</td>';
		} else {
			table += '			<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field9Label + '</th>');
	}
	if (item.sor.field10Label != undefined) {
		if (item.field10 != undefined) {
			table += '			<td class="width_200 width_200_fix align-center">' + item.field10 + '</td>';
		} else {
			table += '			<td class="width_200 width_200_fix align-left">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header > tbody > tr').find('th:last-child').before('<th class="hed_4 width_200 width_200_fix extraFieldHeaders">' + item.sor.field10Label + '</th>');
	}
	table += '			<td class="width_300 width_300_fix">';
	table += '<a title="" class="btn btn-sm ph_btn_small edit-btn-table hvr-pop hvr-rectangle-out1 Edit_subitme_table" href="#creat_subitem_form" style="display: inline-block;">Edit</a>';
	table += '<a title="" class="btn btn-sm add-btn-table ph_btn_small btn-default marg-left-10" style="visibility:hidden" href="javascript:void(0)"></a>';
	table += '			</td>';
	table += '		</tr>';
	table += '	</table>';
	table += '	</div>';
	table += '</li>';
	return table;
}

function constructTable(data, isPageEnable) {
	var table = '';
	// console.log("data :" + data);
	$.each(data, function(i, item) {
		// console.log("item :"+item);
		table += searchRenderGrid(item, isPageEnable);
	});
	if (table.length > 1) {
		table += '</ol>';
		table += '</li>';
		if (table.startsWith('</ol>')) {
			table = table.substring(10);
		}
	}
	return table;
}

function searchRenderGrid(item, isPageEnable) {
	// var sourcingstatus = $('#sourcingstatus').val();
	var table = '';
	var countNewData = 1;
	$('.extraFieldHeaders').remove();
	var disableDragDrop = "";
	if (!isPageEnable) {
		disableDragDrop = "disableDragDrop";
	}
	// console.log(disableDragDrop +"===" +isPageEnable );

	// console.log("item order :"+ item);

	if (item.order !== undefined) {
		if (item.order == 0) {
			table += '</ol>';
			table += '</li>';
			table += '<li style="display: list-item;" class="mjs-nestedSortable-leaf ' + disableDragDrop + '" id="menuItem_1" data-item="' + item.id + '" data-parent="" data-level="' + item.level + '" data-order="' + item.order + '">';
			table += drawSection(item, disableDragDrop, isPageEnable);
			table += '<ol>';
		} else {
			table += drawItems(item, disableDragDrop, isPageEnable);
		}
	}
	return table;

}

$('#downloadTemplate').click(function(e) {
	e.preventDefault();
	// var eventId = $('#eventId').val();
	var bqId = $.trim($('#bqId').val());
	// console.log(" Event ID :: " + eventId + "SOR ID :: "+ bqId);
	window.location.href = getBuyerContextPath('sorItemRfsTemplate') + "/" + bqId;
})

$('#uploadBqItems').click(function(e) {
	e.preventDefault();
	$('#uploadBqItemFile').trigger("click");
});

$("#uploadBqItemFile").on("change", function() {
	if ($(this).val() == "") {
		return;
	}
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();

	if ($('#uploadBqItemFile').val().length == 0) {
		$('p[id=idGlobalErrorMessage]').html("Please select upload file.");
		$('div[id=idGlobalError]').show();
		return;
	}

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var bqId = $.trim($('#bqId').val());
	var eventId = $('#formId').val();
	var pageLen = "50";
	if ($('#selectPageLen option:selected').text()) {
		pageLen = $('#selectPageLen').val();
	}
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
	// console.log(" pageLength : "+pageLen + "searchVal :"+searchVal +
	// " choosenVal : "+choosenVal + " pageNo :" +pageNo) ;

	var isPageEnable = false;
	if (searchVal == "" || filterVal == "") {
		isPageEnable = true;
	}

	var myForm = new FormData();
	myForm.append("file", $('#uploadBqItemFile')[0].files[0]);
	myForm.append("bqId", bqId);
	myForm.append("eventId", eventId);
	myForm.append("filterVal", choosenVal);
	myForm.append("searchVal", searchVal);
	myForm.append("pageNo", pageNo);
	myForm.append("pageLength", pageLength);
	// console.log(myForm);
	$.ajax({
		url : getBuyerContextPath('uploadRfsSorItems'),
		data : myForm,
		type : "POST",
		enctype : 'multipart/form-data',
		processData : false,
		contentType : false,
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			var info = request.getResponseHeader('info');
			var rftBqColumns = data.rftBqColumns;

//			reRenderColumns(rftBqColumns);

			if (success != undefined) {
				$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalSuccess]').show();
			}
			if (info != undefined) {
				$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalInfo]').show();
			}
			var table = constructTable(data.list, isPageEnable);
			$('#bqitemList').html(table);
			$('.custom-checkbox.checksubcheckbox').uniform();
			$('.checker span').find('.glyph-icon.icon-check').remove();
			$('.extraEachBlock').remove();
			$('.checker span').append('<i class="glyph-icon icon-check"></i>');
			// numberingTable();

			// Re-Render Paginator
			totalBqItemCount = data.totalBqItemCount;
			bqPageLength = $('#bqPageLength').val();
			if (bqPageLength === undefined || bqPageLength === '') {
				bqPageLength = 50;
			}
			console.log("totalBqItemCount :" + totalBqItemCount + "== bqPageLength :" + bqPageLength + "===totalBqItemCount/bqPageLength :" + totalBqItemCount / bqPageLength);
			totalPage = Math.ceil(totalBqItemCount / bqPageLength);
			if (totalPage == 0 || totalPage === undefined || totalPage === '') {
				totalPage = 1;
			}
			if (totalPage < 5) {
				visiblePage = totalPage;
			}

			var $pagination = $('#pagination');
			var defaultOpts = {
				totalPages : totalPage
			};
			$pagination.twbsPagination(defaultOpts);

			var currentPage = $pagination.twbsPagination('getCurrentPage');
			$pagination.twbsPagination('destroy');
			$pagination.twbsPagination($.extend({}, defaultOpts, {
				startPage : currentPage,
				totalPages : totalPage
			}));

			$('#loading').hide();
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
	$(this).val("");
});

function reRenderColumns(addBlock) {

	var html = '';
	var i;
	for (i = 1; i <= 10; i++) {
		var keyLabel = 'field' + i + 'Label';

		if (addBlock[keyLabel] !== undefined) {
			html += '<tr class="addColumsBlock" data-pos="' + i + '">';
			html += '<td class="width_300">';
			html += '<div class="form-group s1-mrg-10">';
			html += '<input type="text" class="form-control" id="' + keyLabel + '" name="field' + i + '" data-validation="required length" data-validation-length="max32" placeholder="Name" value="' + addBlock[keyLabel] + '">';
			html += '</div></td>';
			html += '<td class="width_300">';
			html += '<div class="form-group s1-mrg-10">';
			html += '<select data-validation="required" id="' + keyFilledBy + '" name="' + keyFilledBy + '" class="chosen-select filledBy">';
			html += '<option value="BUYER">BUYER</option>';
			html += '<option value="SUPPLIER">SUPPLIER</option>';
			html += '<option value="BOTH">BOTH</option>';
			html += '</select>';
			html += '</div></td>';
			html += '<td class=" width_100" id="fieldRemove' + i + '" name="fieldRemove' + i + '"><a href="javascript:void(0)" class="s1_remove_tr">' + '<i aria-hidden="true" class="glyph-icon icon-close"></i>';
			html += '</a></td></tr>';
		}
	}

	html += '<tr>';
	html += '<td colspan="5">';
	html += '<button class="btn btn-black ph_btn s1_mrg-r-20" type="button" id="AddColumnsToList">Add columns</button>';
	html += '<button type="button" class="btn btn-info ph_btn" id="newFieldsSave">Save</button>';
	html += '</td>';
	html += '</tr>';
	$('.columnsExtra').html(html);

	$('.addColumsBlock').find('select').chosen();
	$('.addColumsBlock').find('.glyph-icon.icon-search').remove();
	$('.addColumsBlock').find('.glyph-icon.icon-caret-down').remove();
	$('.addColumsBlock').find(".chosen-search").append('<i class="glyph-icon icon-search"></i>');
	$('.addColumsBlock').find(".chosen-single div").html('<i class="glyph-icon icon-caret-down"></i>');
	$('.custom-checkbox.checksubcheckboxdata').uniform();
	$('.checker span').find('.glyph-icon.icon-check').remove();
	$('.checker span').append('<i class="glyph-icon icon-check"></i>');

	/*
	 * Object.keys(addBlock).forEach(function(key){ var value = addBlock[key]; console.log(key + ':' + value); });
	 */
	
}

$(document).on("click", ".reArrangeOrder", function(e) {
	$('#confirmReArrangeOrder').modal('show');
});

$('#updateBqOrder').click(function(e) {
	e.preventDefault();
	var bqId = [];
	var formId = $("#formId").val();
	$("#sortable li").each(function(i) {
		bqId.push($(this).attr('data-item'));
    });
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url : getContextPath() + '/buyer/updateSourcingSorOrder/' + formId,
		data : JSON.stringify(bqId),
		type : "POST",
		contentType : "application/json",
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
			$('#loading').show();
		},
		success : function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			var info = request.getResponseHeader('info');
			if (success != undefined) {
				$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalSuccess]').show();
			}
			if (info != undefined) {
				$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalInfo]').show();
			}
			buildtable(data,formId, token);
		},
		error : function(request, textStatus, errorThrown) {
			var error = request.getResponseHeader('error');
			if (error != undefined) {
				$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('.alert-danger').hide();
				$('div[id=idGlobalError]').show();
			}
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

function buildtable(data, formId, token){
	var table = '';
	var sourcingstatus = $('#sourcingstatus').val();
	$.each(data, function(i, item) {
		if (item.id == bqId) {
			$('#bqTitleId').text('Title : ' + item.name);
		}
		table += '<tr><td class="col-md-3 width_100_fix" data-id="' + item.id + '">' + '<span class="marg-right-5 pull-left">' + '<form action="' + getContextPath() + '/buyer/showSourcingSorItems" method="post">';
		table += '<input type="hidden" name="_csrf" value="' + token + '" />' + '<input type="hidden" name="formId" value="' + formId + '"/>' + '<input type="hidden" name="bqId" value="' + item.id + '"/>';
		table += '<button type="submit" name="submitbq" class=" font-size-26"><img style="width:20px;" src="' + getContextPath() + '/resources/images/edit1.png"/></button>' + '</form></span>' + '<span title="" class="pull-left">';
		if (sourcingstatus === 'DRAFT') {
			table += '<button type="button" class=" font-size-26 deleteBq"><img style="width:20px;" src="' + getContextPath() + '/resources/images/delete1.png"/></button>';
		}
		table += '</span><spring:url value="showSourcingSorItems" var="showBqItems" htmlEscape="true" />' + '</td>';

		if(item.sorOrder != null){
		 table += ' <td class="col-md-3 width_100_fix align-left" style="padding-left: 18px;">' + item.sorOrder + '</td>';
		}
		if(item.sorOrder == null){
		 table += ' <td class="col-md-3 width_100_fix align-left" style="padding-left: 18px;">' + (i+1) + '</td>';
		}
		table += ' <td class="col-md-3 align-left">' + item.name + '</td>';
		if (typeof item.description != "undefined") {
			table += '<td class="col-md-5 align-left">' + item.description + '</td>';
		} else {
			table += '<td class="col-md-5 align-left">&nbsp</td></tr>';
		}
	});
	$('tbody.catecontent').html(table);
}
