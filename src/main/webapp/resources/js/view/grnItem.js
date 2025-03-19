var supplierListEmptyFlag = false;
var grnDecimal=2;
$(document).ready(function() {
	
	$(document).delegate('.itemValue', 'change', function(e) {
		e.preventDefault();

		if($.trim(this.value) == ''){
			return;
		}
		var value = parseFloat($.trim(this.value).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));
		this.value=ReplaceNumberWithCommas(value.toFixed(decimalLimit));
	});
	
	$(document).delegate('.validate', 'keydown', function(e) {
		// Allow: backspace, delete, tab, escape, enter and .
		if ($.inArray(e.keyCode, [ 46, 8, 9, 27, 13, 110, 190 ]) !== -1 ||
		// Allow: Ctrl+A, Command+A
		(e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) ||
		// Allow: home, end, left, right, down, up
		(e.keyCode >= 35 && e.keyCode <= 40)) {
			// let it happen, don't do anything
			return;
		}
		// Ensure that it is a number and stop the keypress
		if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
			e.preventDefault();
		}
	});
	
	$(document).delegate('#additionalTax', 'keydown', function(e) {
		if ($.inArray(e.keyCode, [ 46, 8, 9, 27, 13, 110, 190 ]) !== -1 || (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) || (e.keyCode >= 35 && e.keyCode <= 40)) {
			return;
		}
		if ((e.keyCode >= 48 && e.keyCode <= 57) || (e.keyCode >= 96 && e.keyCode <= 105)) {
			var stringtc = $(this).val();
			var beforeD = stringtc;
			if (stringtc.indexOf('.') > -1) {
				tmp = stringtc.split(".");
				beforeD = tmp[0];
				// console.log(tmp[1].length);
				if (tmp[1].length > 5)
					e.preventDefault();
				else
					return;
			}
			if (beforeD.length > 9)
				e.preventDefault();
		}
		if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
			e.preventDefault();
		}
	});

	
	grnDecimal=$("#grnDecimalId").val();

	supplierListEmptyFlag = false;
	$("#idGlobalWarn").hide();

	$('#taxDescription').on('change', function(e) {
		e.preventDefault();
		addTax();
	});

	$('#additionalTax').on('change', function(e) {
		e.preventDefault();
		if($('#additionalTax').val() == undefined || $('#additionalTax').val() == ''){
			$('#additionalTax').val(parseFloat('0').toFixed(decimalLimit));
		}
		var additionalTax = parseFloat($.trim($('#additionalTax').val()).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));
		$('#additionalTax').val(ReplaceNumberWithCommas(additionalTax.toFixed(decimalLimit)));
		addTax();
	});

	function addTax() {
		$('.alert-danger').hide();
		$('.alert').hide();
		if ($('#additionalTax').val() != '' && !$('#addTaxForm').isValid()) {
			return false;
		}
		var additionalTax = $('#additionalTax').val();
		if ($('#additionalTax').val() == '') {
			additionalTax = 0;
		}
		var grnId = $('#grnId').val();
		var taxDescription = $('#taxDescription').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			type : "GET",
			url : getContextPath() + "/buyer/updateAdditionalTax",
			data : {
				additionalTax : additionalTax,
				prId : prId,
				taxDescription : taxDescription
			},
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
			},
			success : function(data) {
				var grandTotal = data[0];
				var decimal = data[1];
				if(decimal != null && decimal !==undefined && grandTotal !== undefined){
					console.log("success" + ReplaceNumberWithCommas(grandTotal.toFixed(decimal)));
					$("#gTotal").text(ReplaceNumberWithCommas(grandTotal.toFixed(decimal)));
				}else{
					console.log("success" + ReplaceNumberWithCommas(grandTotal));
				}
				
				$('.alert-danger').hide();
				$('.alert').hide();
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('.alert-danger').hide();
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
			},
		})
	}
	
	
	renumber_table();
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


	numberingTable();

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

	function deleteBtn() {
		var total = $(".sortable [type=checkbox]:checked").length;
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

	$('#s1_tender_reset_btn').click(function(e) {

		$('#myModalResetBQs').modal('show');

		return false;
	});

	$('#idConfirmResetButtonBQs').click(function() {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$('#myModalDeleteBQs').modal('hide');
		var prId = $('#resetPrId').val();
		var items = [];
		$('.sortable .checksubcheckbox:checked').each(function() {
			items.push($(this).val());
		});
		$.ajax({
			url : getContextPath() + '/buyer/resetPrItem/' + prId,
			data : JSON.stringify(items),
			type : "POST",
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var table = '';
				var success = request.getResponseHeader('success');
				var info = request.getResponseHeader('info');
				if (success != undefined) {
					$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('.alert-danger').hide();
					$('div[id=idGlobalSuccess]').show();
				}
				if (info != undefined) {
					$('p[id=idGlobalSuccessMessage]').html(info != null ? info.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('.alert-danger').hide();
					$('div[id=idGlobalSuccess]').show();
				}

				var table = '';
				var total_bat = 0;
				var addTax = 0;
				var gTotal = 0;
				var decimal = 2;
				$.each(data, function(i, item) {
					table += renderGrid(item);
					total_bat = item.pr.total;
					addTax = item.pr.additionalTax;
					gTotal = item.pr.grandTotal;
					decimal = item.pr.decimal;
				});

					if(total_bat !== undefined && decimal != null && decimal !==undefined){
						$('#total_bat').text(ReplaceNumberWithCommas(total_bat.toFixed(decimal)));
					}else{
						$('#total_bat').text(ReplaceNumberWithCommas(total_bat));
					}
					if(addTax !== undefined && decimal != null && decimal !==undefined){
						$('#addTax').text(ReplaceNumberWithCommas(addTax.toFixed(decimal)));
					}else{
						$('#addTax').text(ReplaceNumberWithCommas(addTax));
					}
					if(gTotal !== undefined && decimal != null && decimal !==undefined){
						$('#gTotal').text(ReplaceNumberWithCommas(gTotal.toFixed(decimal)));
					}else{
						$('#gTotal').text(ReplaceNumberWithCommas(gTotal));
					}
 
				$('#grnitemList').html(table);
				$('.custom-checkbox').prop('checked', false).change().uniform();
				$('.custom-checkbox.checksubcheckbox').uniform();
				$('.checker span').find('.glyph-icon.icon-check').remove();
				$('.checker span').append('<i class="glyph-icon icon-check"></i>');
				numberingTable();
				$('#loading').hide();
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('.alert-danger').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
				location.reload(true);
			}
		});
	});

	$('#idConfirmDeleteBQs').click(function() {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$('#myModalDeleteBQs').modal('hide');
		var grnId = $('#id').val();
		var items = [];
		$('.sortable .checksubcheckbox:checked').each(function() {
			items.push($(this).val());
		});
		$.ajax({
			url : getContextPath() + '/buyer/deleteGrnItem/' + grnId,
			data : JSON.stringify(items),
			type : "POST",
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var table = '';
				var success = request.getResponseHeader('success');
				var info = request.getResponseHeader('info');
				if (success != undefined) {
					$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('.alert-danger').hide();
					$('div[id=idGlobalSuccess]').show();
				}
				if (info != undefined) {
					$('p[id=idGlobalSuccessMessage]').html(info != null ? info.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('.alert-danger').hide();
					$('div[id=idGlobalSuccess]').show();
				}

				var table = '';
				var total_bat = 0;
				var addTax = 0;
				var gTotal = 0;
				var decimal = 2;
				$.each(data, function(i, item) {
					table += renderGrid(item);
					total_bat = item.goodsReceiptNote.total;
					addTax = item.goodsReceiptNote.additionalTax;
					gTotal = item.goodsReceiptNote.grandTotal;
					decimal = item.goodsReceiptNote.decimal;
				});
				
				if(decimal != null && decimal !==undefined && total_bat !== undefined){
					$('#total_bat').text(ReplaceNumberWithCommas(total_bat.toFixed(decimal)));
				}
				if(decimal != null && decimal !==undefined && addTax !== undefined){
					$('#addTax').text(ReplaceNumberWithCommas(addTax.toFixed(decimal)));
				}
				if(decimal != null && decimal !==undefined && gTotal !== undefined){
					$('#gTotal').text(ReplaceNumberWithCommas(gTotal.toFixed(decimal)));
				}
 
				$('#grnitemList').html(table);
				$('.custom-checkbox').prop('checked', false).change().uniform();
				$('.custom-checkbox.checksubcheckbox').uniform();
				$('.checker span').find('.glyph-icon.icon-check').remove();
				$('.checker span').append('<i class="glyph-icon icon-check"></i>');
				numberingTable();
				$('#loading').hide();
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('.alert-danger').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

	$(document).on("click", ".add_question", function() {
		$(".main-New-Question").toggle();
		$("#creat_seaction_form").hide();
		$(".main-Edit-Question").hide();
		$("#itemId").val('');
	});

	$(document).on("click", ".add_question_table", function() {

		$('#idSelectCurrency').validate(function(valid, elem) {});
		$('#idSelectDecimal').validate(function(valid, elem) {});

		var currency=$('#idSelectCurrency').val();
		var decimal =$('#idSelectDecimal').val();
		if(currency==''){
			var error = "Please select Currency"
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
			return false;
		}
		
		if(decimal==''){
			var error = "Please select Decimal"
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
			return false;
		}
		
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

		$('#creat_subitem_form').find('h3.s1_box_title').text('Add Sub Item ');
		$('#itemSave').text(saveLabel);
		$('#itemTitle').text('Add Item');
		$("#itemId").val('');
		$('.extraFields').html('');
		var grnId = $('#id').val();
		$("#uomTextDiv").removeClass("disabled");
		$('#itemUnitPriceDiv').removeClass('disabled');
		$("#uomText").val('').trigger("chosen:updated");
		$("#crateNewItem").find('[name="uomText"]').attr('data-validation', 'required');
		$('#crateNewItem').modal('show');

	});
	
	$(document).delegate('#idSelectPo', 'change', function(e) {
		e.preventDefault();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var poId=$(this).val();
		console.log("poId"+poId);
		var grnId= $('#grnId').val();
		var ajaxUrl = getContextPath() + '/buyer/copyPoItems';
		var poData = {
			'poId' : poId,
			'grnId' : grnId,
		};
		$.ajax({
			url :ajaxUrl,
			type : "POST",
			data : poData,
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			success : function(data, textStatus, request) {
				var table = '';

				console.log("dsds:" +data.goodsReceiptNoteItems);
				$.each(data.goodsReceiptNoteItems, function(i, item) {
					table += renderGrid(item);
				});
				$('#grnitemList').html(table);
				$('.custom-checkbox.checksubcheckbox').uniform();
				$('.checker span').find('.glyph-icon.icon-check').remove();
				$('.extraEachBlock').remove();
				$('.checker span').append('<i class="glyph-icon icon-check"></i>');
				numberingTable();
				$('#loading').hide();

			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});
	
	
	// on Edit fill value
	$(document).on("click", ".Edit_subitme_table", function() {

		$('#idSelectCurrency').validate(function(valid, elem) {});
		$('#idSelectDecimal').validate(function(valid, elem) {});

		var currency=$('#idSelectCurrency').val();
		var decimal =$('#idSelectDecimal').val();
		if(currency==''){
			var error = "Please select Currency"
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
			return false;
		}
		
		if(decimal==''){
			var error = "Please select Decimal"
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
			return false;
		}

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var grnItemId = $(this).parents('.sub_item').attr('data-id');
		var grnId = $('#id').val();
		$('#parentId').val($(this).parents('.sub_item').attr('data-parent'));
		var data = {};
		$('.extraFields').html('');
		$('#itemTax').val('');
		$("#creat_subitem_form").find('[name="itemDescription"]').val('');
		data["grnId"] = grnId;
		data["grnItemId"] = grnItemId;

		$.ajax({
			url : getContextPath() + '/buyer/editGrnItem',
			data : {
				grnId : grnId,
				grnItemId : grnItemId
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
				$.each(data.goodsReceiptNote, function(i, item) {
					var label = '';
					var fieldname = '';
					var id = '';
					if (i == "field1Label") {
						label = item;
						id = "field1Label";
						fieldname = "field1";
					} else if (i == "field2Label") {
						label = item;
						id = "field2Label";
						fieldname = "field2";
					} else if (i == "field3Label") {
						label = item;
						id = "field3Label";
						fieldname = "field3";
					} else if (i == "field4Label") {
						label = item;
						id = "field4Label";
						fieldname = "field4";
					} else if (i == "field5Label") {
						label = item;
						id = "field5Label";
						fieldname = "field5";
					} else if (i == "field6Label") {
						label = item;
						id = "field6Label";
						fieldname = "field6";
					} else if (i == "field7Label") {
						label = item;
						id = "field7Label";
						fieldname = "field7";
					} else if (i == "field8Label") {
						label = item;
						id = "field8Label";
						fieldname = "field8";
					} else if (i == "field9Label") {
						label = item;
						id = "field9Label";
						fieldname = "field9";
					} else if (i == "field10Label") {
						label = item;
						id = "field10Label";
						fieldname = "field10";
					}
					if (i == "field1Label" || i == "field2Label" || i == "field3Label" || i == "field4Label" || i == "field5Label" || i == "field6Label" || i == "field7Label" || i == "field8Label" || i == "field9Label" || i == "field10Label") {
						additionalHtml += '<div class="col-md-6 col-sm-6 col-xs-12 extraEachBlock">';
						additionalHtml += '<div class="form-group">';
						additionalHtml += '<label>' + label + '</label>';
						additionalHtml += '<input type="text" data-validation="length" data-validation-length="max100" class="form-control" name="' + fieldname + '" id="' + id + '"  placeholder="Enter ' + label + '" value="">';
						additionalHtml += '</div></div>';
					}

				});
				$("#creat_subitem_form").find('[name="uomText"]').attr('data-validation', 'required');
				$('.extraFields').html(additionalHtml);
				$.each(data, function(i, item) {
					if (i == 'unitPrice' || i == 'quantity') {
						if(grnDecimal != null && grnDecimal !==undefined && item !== undefined){
							$("#creat_subitem_form").find('[name="' + i + '"]').val(ReplaceNumberWithCommas(item.toFixed(grnDecimal)));
						}else{
							$("#creat_subitem_form").find('[name="' + i + '"]').val(ReplaceNumberWithCommas(item));
						}
						
					} else {
						if(i=='itemTax'){
							var itemTax = parseFloat(item);
							if(grnDecimal != null && grnDecimal !==undefined && itemTax !== undefined){
								$("#creat_subitem_form").find('[name="' + i + '"]').val(ReplaceNumberWithCommas(itemTax.toFixed(grnDecimal)));
							}else{
								$("#creat_subitem_form").find('[name="' + i + '"]').val(ReplaceNumberWithCommas(itemTax));
							}
						}else{
							$("#creat_subitem_form").find('[name="' + i + '"]').val(item);
						}
					}
					$("#extraFields").find('[name="' + i + '"]').val(item);
					$("#itemId").val(data.id);
					//

					$('#creat_subitem_form').find('h3.s1_box_title').text('Edit Item ');
					$('#itemSave').text('Update');
					$('#itemTitle').text('Edit Item');

				});

				try {
					$("#uomText").val(data.unit.id).trigger("chosen:updated");
				} catch (e) {
					$("#uomText").val(data.product.uom.id).trigger("chosen:updated");
				}


				$('#crateNewItem').modal('show');
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
		deleteBtn();
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
		deleteBtn();
	});

	$('.column_button_bar').on('click', '#s1_tender_adddel_btn', function(event) {
		event.preventDefault();
		$('#add_delete_column').show();
		$('#crateColumnSection').modal('show')

	});
	

	$(document).on("click", ".Edit_section_table", function() {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var grnItemId = $(this).parents('.sub_item').attr('data-id');

		$('#saveGrnSection').text('Update');
		$('#sectionTitle').text('Edit Section');

		$('#idSelectCurrency').validate(function(valid, elem) {});
		$('#idSelectDecimal').validate(function(valid, elem) {});

		var currency=$('#idSelectCurrency').val();
		var decimal =$('#idSelectDecimal').val();
		if(currency==''){
			var error = "Please select Currency"
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
			return false;
		}
		
		if(decimal==''){
			var error = "Please select Decimal"
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
			return false;
		}
		
		var grnId = $('#id').val();
		var data = {};
		data["grnId"] = grnId;
		data["grnItemId"] = grnItemId;
		$.ajax({
			url : getContextPath() + '/buyer/editGrnItem',
			data : {
				grnId : grnId,
				grnItemId : grnItemId
			},
			type : "GET",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				$('#loading').hide();
				$.each(data, function(i, item) {
					if (i == "itemName")
						$("#sectionName").val(item);
					if (i == "itemDescription")
						$("#sectionDescription").val(item);
				
					console.log("======================>" + data.id);
					$("#itemId").val(grnItemId);
					$('#saveGrnSection').text('Update');

					$('#crateNewSection').modal('show')

				});
			},
			complete : function() {
				$('#loading').hide();
			}

		});
	});


	$('.create_list_sectoin').on('click', '.bq_tender_addsub_item', function(event) {
		event.preventDefault();
	});

	$('.table-1 .custom-checkbox').on('change', function() {
		var check = this.checked;
		$(".table-2 [type=checkbox]").each(function() {
			$(".table-2 [type=checkbox]").prop('checked', check);
			$.uniform.update($(this));
			deleteBtn();
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
		deleteBtn();
	});

	$(document).on("keyup", "#chosenSupplier_chosen .chosen-search input", keyDebounceDelay(function(e) {
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

	var favSupp = $.trim($('#chosenSupplier_chosen .chosen-search input').val());
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var prId = $('#id').val();
	$.ajax({
		url : getContextPath() + '/buyer/searchSuppliersForPr',
		data : {
			'searchSupplier' : favSupp,
			'prId' : prId
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
				$('#chosenSupplier').find('option:not(:first)').remove();
				$.each(data, function(key, value) {
					if (value.id == null || value.id == '') {
						html += '<option value="" disabled>' + value.companyName + '</option>';
					}else{
						html += '<option value="' + value.id + '">' + value.companyName + '</option>';
					}
				});
			}
			$('#chosenSupplier').append(html);
			$("#chosenSupplier").trigger("chosen:updated");
			$('#chosenSupplier_chosen .chosen-search input').val(favSupp);
			$('#loading').hide();
		},
		error : function(error) {
			console.log(error);
		}
	});
}

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

// saving pr item
$(function() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$('#itemSave').click(function(e) {
		e.preventDefault();

		var currency=$('#idSelectCurrency').val();
		var decimal =$('#idSelectDecimal').val();
		
		if (!$('#addNewItems').isValid()) {
			return false;
		}
		var grnItemId = $("#itemId").val();
		var grnId = $('#id').val();
		$("#allthesets div:last input:first").attr("name")
		var itemName = '';
		var itemId = $('#itemName').val();
		$("#inputPickerDiv").css("display", "none");
		$("#idDescriptionDiv").css("display", "none");
		$("#textInputPickerDiv").css("display", "block");
		itemName = $("#itemNameText").val();
	
		var itemQuantity = $('#itemQuantity').val();
		var itemUnitPrice = $('#itemUnitPrice').val();
		var itemDescription = $('#itemDescription').val();
		var itemUnit = $('#itemUnit').val();
		var itemTax = $('#itemTax').val();
		var parentId = $('#parentId').val();
		var field1 = $('#field1Label') != undefined ? $('#field1Label').val() : "";
		var field2 = $('#field2Label') != undefined ? $('#field2Label').val() : "";
		var field3 = $('#field3Label') != undefined ? $('#field3Label').val() : "";
		var field4 = $('#field4Label') != undefined ? $('#field4Label').val() : "";

		var field5 = $('#field5Label') != undefined ? $('#field5Label').val() : "";
		var field6 = $('#field6Label') != undefined ? $('#field6Label').val() : "";
		var field7 = $('#field7Label') != undefined ? $('#field7Label').val() : "";
		var field8 = $('#field8Label') != undefined ? $('#field8Label').val() : "";
		var field9 = $('#field9Label') != undefined ? $('#field9Label').val() : "";
		var field10 = $('#field10Label') != undefined ? $('#field10Label').val() : "";

		var data = {};

		var goodsReceiptNote = {};
		goodsReceiptNote["id"] = grnId;
		data["goodsReceiptNote"] = goodsReceiptNote;
		var unit = {};
		unit["id"] = $('#uomText').val();
		data["unit"] = unit;
		data["itemName"] = itemName;
		data["itemId"] = itemId;
		data["quantity"] = itemQuantity.replace(/,/g, '');
		data["unitPrice"] = itemUnitPrice.replace(/,/g, '');
		data["itemDescription"] = itemDescription;
		data["itemTax"] = itemTax;
		if (parentId != "") {
			var parent = {};
			parent["id"] = parentId;
			data["parent"] = parent;
		}
		data["id"] = grnItemId;

		if(currency !=''){
			data["currency"] = currency;
		}
		
		if(decimal !=''){
			data["decimal"] = decimal;
		}
		data["field1"] = field1;
		data["field2"] = field2;
		data["field3"] = field3;
		data["field4"] = field4;
		data["field5"] = field5;
		data["field6"] = field6;
		data["field7"] = field7;
		data["field8"] = field8;
		data["field9"] = field9;
		data["field10"] = field10;

		var ajaxUrl = getContextPath() + '/buyer/saveGrnItem';
		if (grnItemId != '') {
			ajaxUrl = getContextPath() + '/buyer/updateGrnItem';
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
				if (success != undefined) {
					$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('.alert-danger').hide();
					$('div[id=idGlobalSuccess]').show();
				}
				if (info != undefined) {
					$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('.alert-danger').hide();
					$('div[id=idGlobalInfo]').show();
				}
				$("#creat_subitem_form").find('[name="uomText"]').removeAttr('data-validation');
				$("#crateNewItem").find('[name="uomText"]').removeAttr('data-validation');

				var table = '';
				var total_bat = '';
				var addTax = '';
				var gTotal = '';
				$.each(data, function(i, item) {
					if(item.goodsReceiptNote.decimal != null && item.goodsReceiptNote.decimal !==undefined && item.goodsReceiptNote.total !==undefined){
						total_bat = ReplaceNumberWithCommas(item.goodsReceiptNote.total.toFixed(item.goodsReceiptNote.decimal));
					}
					if(item.goodsReceiptNote.decimal != null && item.goodsReceiptNote.decimal !==undefined && item.goodsReceiptNote.additionalTax !==undefined){
						addTax = ReplaceNumberWithCommas(item.goodsReceiptNote.additionalTax.toFixed(item.goodsReceiptNote.decimal));
					}
					if(item.goodsReceiptNote.decimal != null && item.goodsReceiptNote.decimal !==undefined && item.goodsReceiptNote.grandTotal !==undefined){
						gTotal = ReplaceNumberWithCommas(item.goodsReceiptNote.grandTotal.toFixed(item.goodsReceiptNote.decimal));
					} 
					

					table += renderGrid(item);
				});

				$('#grnitemList').html(table);
				$('.custom-checkbox.checksubcheckbox').uniform();
				$('.checker span').find('.glyph-icon.icon-check').remove();
				$('.extraEachBlock').remove();
				$('.checker span').append('<i class="glyph-icon icon-check"></i>');
				numberingTable();

				$(".totalAmountWithTax").each(function() {
				});

				$('#total_bat').text(total_bat);
				$('#addTax').text(addTax);
				$('#gTotal').text(gTotal);

				$('#loading').hide();

			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('.alert').hide();
				$('.alert-danger').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();

				var error = request.getResponseHeader('error');
				$.jGrowl(error, {
					sticky : false,
					position : 'top-right',
					theme : 'bg-red'
				});
			},
		});
	});
});

// Add Section
$(function() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$(document).on("click", "#saveGrnSection", function(e) {

		e.preventDefault();
		if ($('#demo-form1').isValid()) {
			var grnItemId = document.getElementById("itemId").value;
			var grnId = document.getElementById("id").value;
			var itemName = document.getElementById("sectionName").value;
			var itemDescription = document.getElementById("sectionDescription").value;
			console.log("grnItemId:" + grnItemId);
			console.log("grnId" + grnId);
			console.log("itemName" + itemName);
			console.log("itemDescription" + itemDescription);

			var data = {};
			var goodsReceiptNote = {};
			goodsReceiptNote["id"] = grnId;

			data["goodsReceiptNote"] = goodsReceiptNote;
			data["itemName"] = itemName;
			data["itemDescription"] = itemDescription;
			data["id"] = grnItemId;
			var ajaxUrl = getContextPath() + '/buyer/saveGrnItem';
			if (grnItemId != '') {
				ajaxUrl = getContextPath() + '/buyer/updateGrnItem';
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
					if (success != undefined) {
						$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
						$('.alert').hide();
						$('.alert-danger').hide();
						$('div[id=idGlobalSuccess]').show();

					}
					if (info != undefined) {
						$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
						$('.alert').hide();
						$('.alert-danger').hide();
						$('div[id=idGlobalInfo]').show();
					}
					var table = '';

					$.each(data, function(i, item) {
						table += renderGrid(item);
					});
					$('#grnitemList').html(table);
					$('.custom-checkbox.checksubcheckbox').uniform();
					$('.checker span').find('.glyph-icon.icon-check').remove();
					$('.extraEachBlock').remove();
					$('.checker span').append('<i class="glyph-icon icon-check"></i>');
					numberingTable();
					$('#loading').hide();
				},
				error : function(request, textStatus, errorThrown) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('.alert').hide();
					$('.alert-danger').hide();
					$('div[id=idGlobalError]').show();
					$('#loading').hide();
				},
			});
		}

		return false;
	});
});


$(document).delegate('.s1_remove_tr', 'click', function(e) {
	e.preventDefault();
	$('#deleteColpos').val($(this).parents('.addColumsBlock').index());
	$('#myModalDeleteColum').modal('show');
	$("#AddColumnsToList").prop("disabled", false);
});



function ReplaceNumberWithCommas(yourNumber) {
	var n;
	// Seperates the components of the number
	if (yourNumber != null) {
		n = yourNumber.toString().split(".");
		// Comma-fies the first part
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		// Combines the two sections
		return n.join(".");
	}
	return n;
}

function addColumActivedeact(addBlock) {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	if ($('.addColumsBlock').length < 10) {
		var posIndex = $('.addColumsBlock').length + 1;

		var fieldLabelId = "field" + posIndex + "Label";
		var html = '<tr class="addColumsBlock" data-pos="' + posIndex + '">';
		html += '<td class="width_300">';
		html += '<div class="form-group s1-mrg-10">';
		html += '<input type="text" class="form-control fieldLabel" placeholder="Name" id="' + fieldLabelId + '" name="field' + posIndex + '" data-validation="required length" data-validation-length="max32" >';
		html += '</div></td>'
		html += '<td class="text_table_left width_100 s1-mrg-10"><a href="javascript:void(0)" class="s1_remove_tr" id="fieldRemove' + posIndex + '" name="fieldRemove' + posIndex + '">';
		html += '<i aria-hidden="true" class="glyph-icon icon-close"></i>';
		html += '</a></td></tr>';

		addBlock.parents('tr').before(html);
		$('.addColumsBlock').find('select').chosen();
		$('.addColumsBlock').find('.glyph-icon.icon-search').remove();
		$('.addColumsBlock').find('.glyph-icon.icon-caret-down').remove();
		$('.addColumsBlock').find(".chosen-search").append('<i class="glyph-icon icon-search"></i>');
		$('.addColumsBlock').find(".chosen-single div").html('<i class="glyph-icon icon-caret-down"></i>');
		$('.custom-checkbox.checksubcheckboxdata').uniform();
		$('.checker span').find('.glyph-icon.icon-check').remove();
	} else {
		$("#AddColumnsToList").attr("disabled", "disabled");
	}
}

function renderGrid(item) {

	var table = '';
	var countNewData = 1;
	var desc = item.itemDescription != undefined ? item.itemDescription : '';
	$('.extraFieldHeaders').remove();
	table += '<li style="display: list-item;" class="mjs-nestedSortable-leaf" id="menuItem_1" data-item="' + item.id + '" data-parent="">';
	table += '<div class="menuDiv">';
	table += '<table class="table data ph_table diagnosis_list sorted_table">';
	table += '<tbody><tr class="sub_item" data-id="' + item.id + '">';
	table += '<td class="width_50 width_50_fix move_col">';
	table += '<a href="javascript:void(0);"><i aria-hidden="true" class="glyphicon glyphicon-sort"></i></a>';
	table += '</td>' + '<td class="width_50 width_50_fix">';
	table += '<div class="checkbox checkbox-info ui-sortable-handle" style="margin-top: -12px;">';
	table += '<label>';
	table += '<div class="checker" id="uniform-inlineCheckbox116">';
	table += '<span><input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value = "' + item.id + '">';
	table += '<i class="glyph-icon icon-check"></i>';
	table += '</span></div></label></div></td>';


	table += '<td class="width_100 width_100_fix"></td>';

	table += '<td class="width_50 width_50_fix">';
	table += '<span>' + item.level + '.0</span></td>';
	table += '<td class="width_200 width_200_fix">';
	table += '<span class="item_name">' + item.itemName + '</span>';
	if (item.itemDescription != '' && item.itemDescription != undefined) {

		table += '<span class="item_detail">';
		table += '<a class="s1_view_desc" href="javascript:void(0)">View Description</a></span>';
	}

	table += '<p class="s1_tent_tb_description s1_text_small">' + desc + '</p></td>';
	table += '<td class="width_100 width_100_fix align-center">&nbsp;</td>';
	table += '<td class="width_100 width_100_fix align-center">&nbsp;</td>';
	table += '<td class="width_100 width_100_fix align-center">&nbsp;</td>';

	if (item.goodsReceiptNote.field1Label != undefined) {
		if (item.field1 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:nth-last-child(3)').before('<th class="hed_4 width_200 width_200_fix align-center extraFieldHeaders">' + item.goodsReceiptNote.field1Label + '</th>');
	}
	if (item.goodsReceiptNote.field2Label != undefined) {
		if (item.field2 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:nth-last-child(3)').before('<th class="hed_4 width_200 width_200_fix align-center extraFieldHeaders">' + item.goodsReceiptNote.field2Label + '</th>');
	}
	if (item.goodsReceiptNote.field3Label != undefined) {
		if (item.field3 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:nth-last-child(3)').before('<th class="hed_4 width_200 width_200_fix align-center extraFieldHeaders">' + item.goodsReceiptNote.field3Label + '</th>');
	}

	if (item.goodsReceiptNote.field4Label != undefined) {
		if (item.field4 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:nth-last-child(3)').before('<th class="hed_4 width_200 width_200_fix align-center extraFieldHeaders">' + item.goodsReceiptNote.field4Label + '</th>');
	}

	if (item.goodsReceiptNote.field5Label != undefined) {
		if (item.field5 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:nth-last-child(3)').before('<th class="hed_4 width_200 width_200_fix align-center extraFieldHeaders">' + item.goodsReceiptNote.field5Label + '</th>');
	}

	if (item.goodsReceiptNote.field6Label != undefined) {
		if (item.field6 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:nth-last-child(3)').before('<th class="hed_4 width_200 width_200_fix align-center extraFieldHeaders">' + item.goodsReceiptNote.field6Label + '</th>');
	}

	if (item.goodsReceiptNote.field7Label != undefined) {
		if (item.field7 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:nth-last-child(3)').before('<th class="hed_4 width_200 width_200_fix align-center extraFieldHeaders">' + item.goodsReceiptNote.field7Label + '</th>');
	}

	if (item.goodsReceiptNote.field8Label != undefined) {
		if (item.field8 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:nth-last-child(3)').before('<th class="hed_4 width_200 width_200_fix align-center extraFieldHeaders">' + item.goodsReceiptNote.field8Label + '</th>');
	}
	if (item.goodsReceiptNote.field9Label != undefined) {
		if (item.field9 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		}
		countNewData++;
		$('.header_table.header').find('th:nth-last-child(3)').before('<th class="hed_4 width_200 width_200_fix align-center extraFieldHeaders">' + item.goodsReceiptNote.field9Label + '</th>');
	}
	if (item.goodsReceiptNote.field10Label != undefined) {
		if (item.field10 != undefined) {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		} else {
			table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
		}
		countNewData++;
		console.log($('.header_table.header > tbody > tr').find('th:nth-last-child(4)'));
		$('.header_table.header > tbody > tr').find('th:nth-last-child(3)').before('<th class="hed_4 width_200 width_200_fix align-center extraFieldHeaders">' + item.goodsReceiptNote.field10Label + '</th>');
	}
	table += '<td class="width_120 width_120_fix align-center">&nbsp;</td>';
	table += '<td class="width_120 width_120_fix align-center">&nbsp;</td>';
	table += '<td class="width_200 width_200_fix align_center">&nbsp;</td>';

	table += '</tr></tbody></table>'
	// }
	table += '</div>';
	if (item.children != undefined && item.children != null) {
		table += '<ol>'
		$.each(item.children, function(i, child) {
			table += '<li style="display: list-item;" class="mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="menuItem_2_1" data-foo="baz" data-item="' + child.id + '" data-parent="' + item.id + '">';
			table += '<div class="menuDiv">';
			table += '<table class="table data ph_table diagnosis_list sorted_table">'
			table += '<tbody><tr class="sub_item" data-id="' + child.id + '" data-parent="' + item.id + '">';
			table += '<td class="width_50 width_50_fix move_col">';
			table += '<a href="javascript:void(0);"><i aria-hidden="true" class="glyphicon glyphicon-sort"></i></a></td>';
			table += '<td class="width_50 width_50_fix">';
			table += '<div class="checkbox checkbox-info ui-sortable-handle" style="margin-top: -12px;">';
			table += '<label>';
			table += '<div class="checker" id="uniform-inlineCheckbox116">';
			table += '<span><input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value = "' + child.id + '">';
			table += '<i class="glyph-icon icon-check"></i></span></div></label></div></td>';

			table += '<td class="width_100 width_100_fix"></td>';

			table += '<td class="width_50 width_50_fix"><span>' + child.level + '.' + child.order + '</span></td>';
			table += '<td class="width_200 width_200_fix"><span class="item_name">' + child.itemName + '</span>';
			if (child.itemDescription != '' && child.itemDescription != undefined) {
				table += '<span class="item_detail">' + '<a class="s1_view_desc" href="javascript:void(0)">View Description</a></span>';
			}

			table += '<p class="s1_tent_tb_description s1_text_small">' + child.itemDescription + '</p></td>';
			try {
				table += '<td class="width_100 width_100_fix align-center">' + child.unit.uom + '</td>';
			} catch (e) {
				table += '<td class="width_100 width_100_fix align-center">' + child.unit.uom + '</td>';
			}
			if(item.goodsReceiptNote.decimal != null && item.goodsReceiptNote.decimal !==undefined && child.quantity !==undefined){
				table += '<td class="width_100 width_100_fix align-center">' + ReplaceNumberWithCommas(child.quantity.toFixed(item.goodsReceiptNote.decimal)) + '</td>';
			}else{
				table += '<td class="width_100 width_100_fix align-center">' + ReplaceNumberWithCommas(child.quantity) + '</td>';
			}
			if(item.goodsReceiptNote.decimal != null && item.goodsReceiptNote.decimal !==undefined && child.unitPrice!==undefined){
				table += '<td class="width_100 width_100_fix align-center">' + ReplaceNumberWithCommas(child.unitPrice.toFixed(item.goodsReceiptNote.decimal)) + '</td>';
			}else{
				table += '<td class="width_100 width_100_fix align-center">' + ReplaceNumberWithCommas(child.unitPrice) + '</td>';
			} 
			
			if (child.goodsReceiptNote.field1Label != undefined) {
				if (child.field1 != undefined) {
					table += '<td class="width_200 width_200_fix align-center">' + child.field1 + '</td>';
				} else {
					table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
				}
				countNewData++;
			}
			if (child.goodsReceiptNote.field2Label != undefined) {
				if (child.field2 != undefined) {
					table += '<td class="width_200 width_200_fix align-center">' + child.field2 + '</td>';
				} else {
					table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
				}
				countNewData++;
			}
			if (child.goodsReceiptNote.field3Label != undefined) {
				if (child.field3 != undefined) {
					table += '<td class="width_200 width_200_fix align-center">' + child.field3 + '</td>';
				} else {
					table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
				}
				countNewData++;
			}
			if (child.goodsReceiptNote.field4Label != undefined) {
				if (child.field4 != undefined) {
					table += '<td class="width_200 width_200_fix align-center">' + child.field4 + '</td>';
				} else {
					table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
				}
				countNewData++;
			}
			if (child.goodsReceiptNote.field5Label != undefined) {
				if (child.field5 != undefined) {
					table += '<td class="width_200 width_200_fix align-center">' + child.field5 + '</td>';
				} else {
					table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
				}
				countNewData++;
			}
			if (child.goodsReceiptNote.field6Label != undefined) {
				if (child.field6 != undefined) {
					table += '<td class="width_200 width_200_fix align-center">' + child.field6 + '</td>';
				} else {
					table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
				}
				countNewData++;
			}
			if (child.goodsReceiptNote.field7Label != undefined) {
				if (child.field7 != undefined) {
					table += '<td class="width_200 width_200_fix align-center">' + child.field7 + '</td>';
				} else {
					table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
				}
				countNewData++;
			}
			if (child.goodsReceiptNote.field8Label != undefined) {
				if (child.field8 != undefined) {
					table += '<td class="width_200 width_200_fix align-center">' + child.field8 + '</td>';
				} else {
					table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
				}
				countNewData++;
			}
			if (child.goodsReceiptNote.field9Label != undefined) {
				if (child.field9 != undefined) {
					table += '<td class="width_200 width_200_fix align-center">' + child.field9 + '</td>';
				} else {
					table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
				}
				countNewData++;
			}
			if (child.goodsReceiptNote.field10Label != undefined) {
				if (child.field10 != undefined) {
					table += '<td class="width_200 width_200_fix align-center">' + child.field10 + '</td>';
				} else {
					table += '<td class="width_200 width_200_fix align-center">&nbsp;</td>';
				}
				countNewData++;
			}
			if(item.goodsReceiptNote.decimal != null && item.goodsReceiptNote.decimal !==undefined && child.totalAmount !==undefined){
				table += '<td class="width_120 width_120_fix align-center">' + ReplaceNumberWithCommas(child.totalAmount.toFixed(item.goodsReceiptNote.decimal)) +'</td>';
			}else{
				table += '<td class="width_120 width_120_fix align-center">' + ReplaceNumberWithCommas(child.totalAmount) +'</td>';
			}
			if(item.goodsReceiptNote.decimal != null && item.goodsReceiptNote.decimal !==undefined && child.taxAmount !==undefined){
				table += '<td class="width_120 width_120_fix align-center">' + ReplaceNumberWithCommas(child.taxAmount.toFixed(item.goodsReceiptNote.decimal)) + '</td>';
			}else{
				table += '<td class="width_120 width_120_fix align-center">' + ReplaceNumberWithCommas(child.taxAmount) + '</td>';
			}
			if(item.goodsReceiptNote.decimal != null && item.goodsReceiptNote.decimal !==undefined && child.totalAmountWithTax !==undefined){
				table += '<td class="width_200 width_200_fix align_center totalAmountWithTax">' + ReplaceNumberWithCommas(child.totalAmountWithTax.toFixed(item.goodsReceiptNote.decimal)) + '</td>';
			}else{
				table += '<td class="width_200 width_200_fix align_center totalAmountWithTax">' + ReplaceNumberWithCommas(child.totalAmountWithTax) + '</td>';
			}
 			table += '</tr></tbody></table></div></li>'
		});
		table += '</ol>'
	}
	table += '</li>';
	return table;
}

function numberingTable() {
	$(".sortable > li")
			.each(
					function(index) {

						var ind = parseInt(index + 1);
						var par = $(this).find('ol').length;
						$(this).find('.number').html(ind);

						var htm1 = '<a title="" class="Edit_section_table" href="#creat_seaction_form" style="display: inline-block;"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i></a>';
						htm1 += '<a title="" class="add_question_table"  href="#creat_subitem_form"><i class="glyph-icon tooltip-button custom-icon icon-plus" title="" data-original-title=".icon-plus" aria-describedby="tooltip69681"></i> </a>';
						if (par > 0) {

							$(this).find('.menuDiv').removeClass("sub-color-change");
							$(this).find('table tr td:eq(2)').html(htm1);

							$(this).find('ol li').each(function(index2) {
												$(this).find('.menuDiv ').addClass("sub-color-change");
												$(this).find('.number').html(parseInt(ind) + "." + parseInt(index2 + 1));
												var htm = '<a title="" class="Edit_subitme_table" href="#creat_subitem_form" style="display: inline-block;"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i></a>';
												$(this).find('table tr td:eq(2)').html(htm);
												$(this).find('table .item_detail').show();
											});

						} else {
							$(this).find('.menuDiv').removeClass("sub-color-change");
							$(this).find('table tr td:eq(2)').html(htm1);
						}
					});
}


$("#s1_tender_addsection_btn").click(function() {
	$("#itemId").val('');
	$("#parentId").val('');
	$('#saveGrnSection').text(saveLabel);
	$('#sectionTitle').text('Add Section');
	$('#crateNewSection').modal('show');
	$('#sectionName').val('');
	$('#sectionDescription').val('');
});

