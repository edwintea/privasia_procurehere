$.validate({
	lang: 'en',
	modules: 'file'
});

var idx = 0;
$('document').ready(function() {

	$('#itemQuantity').on('change', function(e) {
		if($('#itemQuantity').val() !== '') {
			console.log('Changed ', $('#itemQuantity').val(), parseFloat($('#itemQuantity').val()), parseFloat($('#itemQuantity').val()).toFixed(decimalLimit));
			$('#itemQuantity').val(parseFloat($('#itemQuantity').val()).toFixed(decimalLimit));
			console.log('Val ', $('#itemQuantity').val());
		}
	});

	$('#saveContractItem').click(function(e) {
		e.preventDefault();
		if (!$('#addcontractItemForm').isValid()) {
			console.log("invalid");
			/*var unitPrice = $("#unitPriceId").val();
			var uom = $("#chosenUom").val();
			if (uom) {
				$("#chosenUom").attr('data-validation', 'required');
				if (!$('#addcontractItemForm').isValid()) {
					return false;
				}
			}
			if (unitPrice) {
				$("#unitPriceId").attr('data-validation', 'required');
				if (!$('#addcontractItemForm').isValid()) {
					return false;
				}
			}*/
			return false;
		} else {
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var contractId = $("#productContractId").val();
			var itemId = $('#hiddenId').val();
			var itemBrand = $("#contractItemBrand").val();
			var productItem = $("#chosenProductItem").val();
			var itemName = $("#itemName").val();
			var itemCode = $("#itemCode").val();
			var freeTextItemEntered = $('#idFreeTextItem').prop("checked");
			var quantity = $("#itemQuantity").val();
			//var balanceQuantity = $("#balanceQuantity").val();
			var unitPrice = $("#unitPriceId").val();
			var pricePerUnit = $("#pricePerUnit").val();
			var storageLocation = $("#storageLocationId").val();
			var uom = $("#chosenUom").val();
			var businessUnitId = $("#chosenBusinessUnitId").val();
			var costCenterId = $("#chosenCostCenter").val();
			var itemTax = $("#itemTax").val();
			var itemProductCategory = $("#chosenProductCategory").val();
			var itemProductItemType = $("#chosenProductItemType").val();
			$('#contractItemModel').modal('hide');
			$.ajax({
				url: getContextPath() + '/buyer/saveContractItem',
				data: {
					'itemId': itemId,
					'contractId': contractId,
					'productItem': productItem,
					'quantity': quantity,
					'unitPrice': unitPrice,
					'pricePerUnit' : pricePerUnit,
					'storageLocation': storageLocation,
					'uom': uom,
					'businessUnit': businessUnitId,
					'costCenterId': costCenterId,
					//					'balanceQuantity': balanceQuantity,
					'itemBrand': itemBrand,
					'itemTax': itemTax,
					'itemProductCategory': itemProductCategory,
					'productItemType': itemProductItemType,
					'freeTextItemEntered': freeTextItemEntered,
					'itemName': itemName,
					'itemCode': itemCode
				},
				type: 'POST',
				beforeSend: function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
					$("#idGlobalSuccess").hide();
				},
				success: function(data, textStatus, request) {
					var success = request.getResponseHeader('success');
					if (success != undefined) {
						
						$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
						$('.alert').hide();
						$('div#idGlobalSuccess.ajax-msg').show();
						$("#idGlobalError").hide();
					}
				},
				error: function(request, textStatus, errorThrown) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('.alert').hide();
					$('div[id=idGlobalError]').show();
					$('#loading').hide();
				},
				complete: function() {
					contractItemTableList.ajax.reload();
					$('#loading').hide();
				}
			});
		}
	});

	$('#idConfirmDelete').click(function(e) {
		e.preventDefault();
		var itemId = $('#deleteId').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$('#deleteContractItem').modal('hide');

		var ajaxUrl = getContextPath() + '/buyer/deleteContractItem/' + itemId;
		$.ajax({
			url: ajaxUrl,
			type: 'POST',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success: function(data, textStatus, request) {
				var success = request.getResponseHeader('success');
				if (success != undefined) {
					$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('div[id=idGlobalSuccess]').show();
					$("#idGlobalError").hide();
				}
			},
			error: function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete: function() {
				contractItemTableList.ajax.reload();
				$('#loading').hide();
			}
		});
	});


	$(document).on("keyup", "#chosenBusinessUnit_chosen .chosen-search input", keyDebounceDelay(function(e) {
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
		var businessUnit = $.trim(this.value);
		if (businessUnit.length > 2 || businessUnit.length == 0 || e.keyCode == 8) {
			reloadBusinessUnitList();
		}
	}, 650));

	$(document).on("keyup", "#chosenBusinessUnitId_chosen .chosen-search input", keyDebounceDelay(function(e) {
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
		var businessUnit = $.trim(this.value);
		if (businessUnit.length > 2 || businessUnit.length == 0 || e.keyCode == 8) {
			reloadBusinessUnitList();
		}
	}, 650));

	$(document).on("keyup", "#chosenCostCenter_chosen .chosen-search input", keyDebounceDelay(function(e) {
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
		var costCenter = $.trim(this.value);
		if (costCenter.length > 2 || costCenter.length == 0 || e.keyCode == 8) {
			reloadCostCenterList();
		}
	}, 650));
	
	$(document).on("keyup", "#chosenProductCategory_chosen .chosen-search input", keyDebounceDelay(function(e) {
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
		var productCategory = $.trim(this.value);
		if (productCategory.length > 2 || productCategory.length == 0 || e.keyCode == 8) {
			reloadProductCategoryList();
		}
	}, 650));	

	$(document).on("keyup", "#chosenProductItem_chosen .chosen-search input", keyDebounceDelay(function(e) {
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
		var productItem = $.trim(this.value);
		if (productItem.length > 2 || productItem.length == 0 || e.keyCode == 8) {
			reloadProductItemList();
		}
	}, 650));

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
		var supplier = $.trim(this.value);
		if (supplier.length > 2 || supplier.length == 0 || e.keyCode == 8) {
			reloadSupplierList();
		}
	}, 650));

	$('.disableSubmitComment').click(function() {
		if ($('#idFrmContractDocument').isValid()) {
			$('#loading').show();
			$('.disableSubmitComment').addClass('disableCq');
		}
	});

});

$("#chosenBusinessUnit").change(function() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	var businessUnitId = $('#chosenBusinessUnit').val();
	$.ajax({
		url: getContextPath() + '/buyer/getBusinessUnitByBusinessUnit',
		type: 'GET',
		data: {
			'businessUnitId': businessUnitId
		},
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data, textStatus, request) {
			console.log(data);
			var html = '';
			if (data != '' && data != null && data.length > 0) {
				$('#chosenBusinessUnitId').find('option:not(:first)').remove();
				$.each(data, function(key, value) {
					if (value.id == null || value.id == '') {
						html += '<option value="" disabled>' + value.displayName + '</option>';
					} else {
						html += '<option value="' + value.id + '">' + value.displayName + '</option>';
					}
				});
			}
			$('#chosenBusinessUnitId').append(html);
			$("#chosenBusinessUnitId").trigger("chosen:updated");
		},

		error: function(request, textStatus, errorThrown) {
			console.log(request);
		},
		complete: function() {
			$('#loading').hide();
		}
	});
});

var selectedCostCenter = '';

$("#chosenBusinessUnitId").change(function() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	var businessUnitId = $('#chosenBusinessUnitId').val();
	$.ajax({
		url: getContextPath() + '/buyer/getCostCenterByBusinessUnit',
		data: {
			'businessUnitId': businessUnitId
		},
		type: 'GET',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data, textStatus, request) {
			var len = data.length;
			$('#chosenCostCenter').find('option:not(:first)').remove();
			for (var i = 0; i < len; i++) {
				var id = data[i]['id'];
				var costCenter = null;
				if (!data[i]['description']) {
					costCenter = data[i]['costCenter'];
				} else {
					costCenter = data[i]['costCenter'] + ' - ' + data[i]['description'];
				}
				if(id == null || id == '' || id == '-1') {
					$("#chosenCostCenter").append("<option value='" + id + "' disabled>" + costCenter + "</option>");
				} else {
					$("#chosenCostCenter").append("<option value='" + id + "'>" + costCenter + "</option>");
				}
			}
			$("#chosenCostCenter").val(selectedCostCenter != null ? selectedCostCenter : '');
			$("#chosenCostCenter").trigger("chosen:updated");
		},

		error: function(request, textStatus, errorThrown) {
			console.log(request);
		},
		complete: function() {
			$('#loading').hide();
		}
	});
});

function editContractItem(id) {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var itemId = id;
	var contractId = $("#productContractId").val();

	$.ajax({
		type: "GET",
		url: getContextPath() + "/buyer/editContractItem/" + itemId + "/" + contractId,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data, request, textStatus) {
			$("#addcontractItemForm").find('#hiddenId').val(id);
			$("#addcontractItemForm").find('#contractItemNumberId').val(data.contractItemNumber);
			$("#addcontractItemForm").find('#contractItemBrand').val(data.brand);
			$("#addcontractItemForm").find('#itemName').val(data.itemName);
			$("#addcontractItemForm").find('#itemCode').val(data.itemCode);
			$("#addcontractItemForm").find('#pricePerUnit').val(data.pricePerUnit);
			console.log('contractStatus', contractStatus, 'Free Text', data.freeTextItemEntered);
			
			if (data.freeTextItemEntered) {
				console.log('Free Text Item');
				$('#idItemFreeText').show();
				$('#itemName').removeAttr('data-validation-optional');
				$('#idItemList').hide();
				$('#itemCode').parent().removeClass('disabled');
				$("#addcontractItemForm").find('#itemTax').parent().removeClass('disabled');
				$("#addcontractItemForm").find('#contractItemBrand').parent().removeClass('disabled');
				$("#addcontractItemForm").find('#unitPriceId').parent().removeClass('disabled');
				$("#addcontractItemForm").find('#chosenUom').parent().removeClass('disabled');
				$("#addcontractItemForm").find('#chosenProductCategory').parent().removeClass('disabled');
				$("#addcontractItemForm").find('#chosenProductItemType').parent().removeClass('disabled');
				$('#chosenProductItem').attr('data-validation-optional', 'true');
			} else {
				console.log('List Item');
				$('#idItemFreeText').hide();
				$('#itemName').attr('data-validation-optional', 'true');
				$('#idItemList').show();
				$('#itemCode').parent().addClass('disabled');
				$("#addcontractItemForm").find('#unitPriceId').parent().addClass('disabled');
				$("#addcontractItemForm").find('#itemTax').parent().addClass('disabled');
				$("#addcontractItemForm").find('#contractItemBrand').parent().addClass('disabled');
				$("#addcontractItemForm").find('#chosenUom').parent().addClass('disabled');
				$("#addcontractItemForm").find('#chosenProductCategory').parent().addClass('disabled');
				$("#addcontractItemForm").find('#chosenProductItemType').parent().addClass('disabled');
				$('#chosenProductItem').removeAttr('data-validation-optional');
				$("#addcontractItemForm").find('#chosenProductItem').val(data.productItem != null ? data.productItem : '').trigger("chosen:updated");
			}
			$('#idFreeTextItem').prop('checked', data.freeTextItemEntered);
			$.uniform.update();

			var itemQuantity = !isNaN(data.quantity) ? data.quantity.toFixed(decimalLimit) : '';
			$("#addcontractItemForm").find('#itemQuantity').val(ReplaceNumberWithCommas((itemQuantity)));
			var unitPriceId = !isNaN(data.unitPrice) ? data.unitPrice.toFixed(decimalLimit) : '';
			$("#addcontractItemForm").find('#unitPriceId').val(data.unitPrice != null ? ReplaceNumberWithCommas((unitPriceId)) : '');
			$("#addcontractItemForm").find('#storageLocationId').val(data.storageLoc != null ? data.storageLoc : '');
			$("#addcontractItemForm").find('#chosenUom').val(data.uom != null ? data.uom : '').trigger("chosen:updated");

			if(data.businessUnit != null && $("#chosenCostCenter option[value='" + data.businessUnit + "']").length == 0) {
				$("#addcontractItemForm").find('#chosenCostCenter option').eq(1).before($("<option></option>").val(data.businessUnit).text(data.businessUnitCode + " - " + data.businessUnitName));
			}
			$("#addcontractItemForm").find('#chosenBusinessUnitId').val(data.businessUnit != null ? data.businessUnit : '').trigger("chosen:updated");

			selectedCostCenter = data.costCenter;
			if(selectedCostCenter != null && $("#chosenCostCenter option[value='" + selectedCostCenter + "']").length == 0) {
				$("#addcontractItemForm").find('#chosenCostCenter option').eq(1).before($("<option></option>").val(selectedCostCenter).text(data.costCenterName + " - " + data.costCenterDescription));
			}
			$("#addcontractItemForm").find('#chosenCostCenter').val(data.costCenter != null ? data.costCenter : '').trigger("chosen:updated");

			$("#addcontractItemForm").find('#itemTax').val(data.tax != null ? data.tax : '0');
			
			if(data.productCategory != null && $("#chosenCostCenter option[value='" + data.productCategory + "']").length == 0) {
				$("#addcontractItemForm").find('#chosenCostCenter option').eq(1).before($("<option></option>").val(data.productCategory).text(data.productCategoryCode + " - " + data.productCategoryName));
			}
			$("#addcontractItemForm").find('#chosenProductCategory').val(data.productCategory != null ? data.productCategory : '').trigger("chosen:updated").change();

			$("#addcontractItemForm").find('#chosenProductItemType').val(data.itemType != null ? data.itemType : '').trigger("chosen:updated");

			// If the item is not free text and created from Event, then disable certain fields.
			if(!data.freeTextItemEntered && contractCreatedFromEvent) {
				$('.contractCreatedFromEvent').addClass('disabled');
			}
			// During active status only quantity is allowed to be changed.
			if((contractStatus == 'ACTIVE' || contractStatus == 'SUSPENDED') && data.erpTransferred)  {
				console.log('Item is already transferred to ERP')
				$("#addcontractItemForm").find('#idFreeTextItem').parent().addClass('disabled');
				$("#addcontractItemForm").find('#itemCode').parent().addClass('disabled');
				$("#addcontractItemForm").find('#itemName').parent().addClass('disabled');
				$("#addcontractItemForm").find('#chosenProductItem').parent().addClass('disabled');
				$("#addcontractItemForm").find('#chosenProductItemType').parent().addClass('disabled');
				$("#addcontractItemForm").find('#chosenProductCategory').parent().addClass('disabled');
				$("#addcontractItemForm").find('#chosenUom').parent().addClass('disabled');
				$("#addcontractItemForm").find('#unitPriceId').parent().addClass('disabled');
				$("#addcontractItemForm").find('#itemTax').parent().addClass('disabled');
				$("#addcontractItemForm").find('#pricePerUnit').parent().addClass('disabled');
				$("#addcontractItemForm").find('#contractItemBrand').parent().addClass('disabled');
				$("#addcontractItemForm").find('#storageLocationId').parent().addClass('disabled');
				$("#addcontractItemForm").find('#chosenBusinessUnitId').parent().addClass('disabled');
				$("#addcontractItemForm").find('#chosenCostCenter').parent().addClass('disabled');
			}
			

			$('#saveContractItem').html("Update");

/*			$('#addcontractItemForm').validate();
			$('#addcontractItemForm').isValid();
*/
 		},
		error: function(request, textStatus, errorThrown) {
			var error = request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "";
			$.jGrowl(error, {
				header: 'Error',
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
}

function updateLink(id) {
	$("#deleteId").val(id);
}


function reloadBusinessUnitList() {
	var buDropDownId = '';

	if($('#chosenBusinessUnit').length > 0) {
		buDropDownId = '#chosenBusinessUnit';
	}
	if($('#chosenBusinessUnitId').length > 0) {
		buDropDownId = '#chosenBusinessUnitId';
	}
	
	var searchUnit = $.trim($(buDropDownId + '_chosen .chosen-search input').val());
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url: getContextPath() + '/buyer/searchBusinessUnit',
		data: {
			'searchUnit': searchUnit,
		},
		type: 'POST',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data) {
			var html = '';
			if (data != '' && data != null && data.length > 0) {
				$(buDropDownId).find('option:not(:first)').remove();
				$.each(data, function(key, value) {
					if (value.id == null || value.id == '' || value.id == '-1') {
						html += '<option value="" disabled>' + value.displayName + '</option>';
					} else {
						html += '<option value="' + value.id + '">' + value.unitName + '</option>';
					}
				});
			}
			$(buDropDownId).append(html);
			$(buDropDownId).trigger("chosen:updated");
			$(buDropDownId + '_chosen .chosen-search input').val(searchUnit);
			$('#loading').hide();
		},
		error: function(error) {
			console.log(error);
		}
	});
}

function reloadCostCenterList() {
	var searchCost = $.trim($('#chosenCostCenter_chosen .chosen-search input').val());
	var buId = $("#chosenBusinessUnit").val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url: getContextPath() + '/buyer/searchCostCenter',
		data: {
			'searchCost': searchCost,
			'businessUnitId' : buId
		},
		type: 'POST',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data) {
			var html = '';
			if (data != '' && data != null && data.length > 0) {
				$('#chosenCostCenter').find('option:not(:first)').remove();
				$.each(data, function(key, value) {
					console.log('Value.id', value.id,'Value.costCenter', value.costCenter, 'Value.description', value.description)
					if (value.id == null || value.id == '' || value.id == '-1') {
						console.log('Received count ..........................................................................');
						html += '<option value="" disabled>' + value.costCenter + '</option>';
					} else {
						html += '<option value="' + value.id + '">' + value.costCenter + '-' + value.description + '</option>';
					}
				});
			}
			$('#chosenCostCenter').append(html);
			$("#chosenCostCenter").trigger("chosen:updated");
			$('#chosenCostCenter_chosen .chosen-search input').val(searchCost);
			$('#loading').hide();
		},
		error: function(error) {
			console.log(error);
		}
	});
}

function reloadProductCategoryList() {
	var searchStr = $.trim($('#chosenProductCategory_chosen .chosen-search input').val());
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url: getContextPath() + '/buyer/searchProductCategory',
		data: {
			'searchStr': searchStr,
		},
		type: 'POST',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data) {
			var html = '';
			if (data != '' && data != null && data.length > 0) {
				$('#chosenProductCategory').find('option:not(:first)').remove();
				$.each(data, function(key, value) {
					if (value.id == null || value.id == '' || value.id == '-1') {
						html += '<option value="" disabled>' + value.productName + '</option>';
					} else {
						html += '<option value="' + value.id + '">' + value.productCode + '-' + value.productName + '</option>';
					}
				});
			}
			$('#chosenProductCategory').append(html);
			$("#chosenProductCategory").trigger("chosen:updated");
			$('#chosenProductCategory_chosen .chosen-search input').val(searchStr);
			$('#loading').hide();
		},
		error: function(error) {
			console.log(error);
		}
	});
}



function reloadProductItemList() {
	
	console.log("Loading product list...");
	
	var searchProductItem = $.trim($('#chosenProductItem_chosen .chosen-search input').val());
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url: getContextPath() + '/buyer/searchProductItemName',
		data: {
			'searchProductItem': searchProductItem,
		},
		type: 'POST',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data) {
			var html = '';
			if (data != '' && data != null && data.length > 0) {
				$('#chosenProductItem').find('option:not(:first)').remove();
				$.each(data, function(key, value) {
					if (value.id == null || value.id == '' || value.id == '-1') {
						html += '<option value="" disabled>' + value.productCode + " - " + value.itemName + '</option>';
					} else {
						html += '<option value="' + value.id + '">' + value.productCode + " - " + value.itemName + '</option>';
					}
				});
			}
			$('#chosenProductItem').append(html);
			$("#chosenProductItem").trigger("chosen:updated");
			$('#chosenProductItem_chosen .chosen-search input').val(searchProductItem);
			$('#loading').hide();
		},
		error: function(error) {
			console.log(error);
		}
	});
}

function reloadSupplierList() {
	var searchSupplier = $.trim($('#chosenSupplier_chosen .chosen-search input').val());
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url: getContextPath() + '/buyer/searchFavouriteuppliers',
		data: {
			'searchSupplier': searchSupplier,
		},
		type: 'POST',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data) {
			var html = '';
			if (data != '' && data != null && data.length > 0) {
				$('#chosenSupplier').find('option:not(:first)').remove();
				$.each(data, function(key, value) {
					if (value.id == null || value.id == '' || value.id == '-1') {
						html += '<option value="" disabled>' + value.companyName + '</option>';
					} else {
						html += '<option value="' + value.id + '">' + value.companyName + '</option>';
					}
				});
			}
			$('#chosenSupplier').append(html);
			$("#chosenSupplier").trigger("chosen:updated");
			$('#chosenSupplier_chosen .chosen-search input').val(searchSupplier);
			$('#loading').hide();
		},
		error: function(error) {
			console.log(error);
		}
	});
}
$("#createContractItemId").click(function(e) {
	e.preventDefault();
	$('#addcontractItemForm').trigger("reset");
	$("#addcontractItemForm").find('#chosenProductItem').val('').trigger("chosen:updated");
	$("#addcontractItemForm").find('#chosenUom').val('').trigger("chosen:updated");
	$("#addcontractItemForm").find('#chosenBusinessUnitId').val('').trigger("chosen:updated");
	$("#addcontractItemForm").find('#chosenCostCenter').val('').trigger("chosen:updated");
	$("#addcontractItemForm").find('#chosenProductCategory').val('').trigger("chosen:updated");
	$('#hiddenId').val('');
	selectedCostCenter = '';

	// Free text item checkbox
	$('#idFreeTextItem').prop('checked', true);
	$.uniform.update();
	$('#idItemFreeText').show();
	$('#itemName').removeAttr('data-validation-optional');
	$('#idItemList').hide();
	$('#chosenProductItem').attr('data-validation-optional', 'true');
	$("#addcontractItemForm").find('#itemCode').parent().removeClass('disabled');
	
	// remove disabled during create
	$('.contractCreatedFromEvent').removeClass('disabled');
	
	$('#idFreeTextItem').change();
	$('#saveContractItem').html("Save");
	
	$("#addcontractItemForm").find('#idFreeTextItem').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#itemCode').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#itemName').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#chosenProductItem').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#chosenProductItemType').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#chosenProductCategory').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#chosenUom').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#unitPriceId').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#itemTax').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#pricePerUnit').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#itemBrand').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#storageLocationId').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#chosenBusinessUnitId').parent().removeClass('disabled');
	$("#addcontractItemForm").find('#chosenCostCenter').parent().removeClass('disabled');
	
	$("#contractItemModel").modal("show");
});

$.formUtils.addValidator({
	name: 'contract_item',
	validatorFunction: function(value, $el, config, language, $form) {
		var response = true;
		console.log($el.attr('name'), value);
		if ($('#contractItem1').is(":checked") && $.trim(value) == '') {
			response = false;
		}
		return response;
	},
	errorMessage: 'This is a required field',
	errorMessageKey: 'badContractItem'
});


$.formUtils.addValidator({
	name: 'contract_date',
	validatorFunction: function(value, $el, config, language, $form) {
		var response = true;
		console.log($('[name="dateTimeRange"]').val());
		if ($('#contractItem1').is(":checked") && $.trim($('#datepicker').val()) == '') {
			response = false;
		}
		return response;
	},
	errorMessage: 'This is a required field',
	errorMessageKey: 'badContractDate'
});

var dateTimeRange;
var historyPricing;
$(function() {
	$("#contractItem1").click(function() {
		if ($(this).is(":checked")) {
			$("#datepicker").prop("disabled", false);
			$("#datepicker").focus();
			$("#datepicker-date-time-nodisable").val(dateTimeRange);
			$("#datepicker-date-time-nodisable").val();
			$("#contractReferenceNumber").val(historyPricing);
			$("#contractReferenceNumber").val();
			$("#datepicker-date-time-nodisable").prop('disabled', false);
			$("#datepicker-date-time-nodisable").focus();
			$("#contractReferenceNumber").prop('disabled', false);
			$("#contractReferenceNumber").focus();

		} else {
			$("#datepicker").val('');
			$("#datepicker").prop("disabled", true);
			dateTimeRange = $("#datepicker-date-time-nodisable").val();
			$("#datepicker-date-time-nodisable").val('');
			$("#datepicker-date-time-nodisable").prop('disabled', true);
			historyPricing = $("#contractReferenceNumber").val();
			$("#contractReferenceNumber").val('');
			$("#contractReferenceNumber").prop('disabled', true);
		}
	});
});



$(function() {
	"use strict";
	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate() + 1, 0, 0, 0, 0);
	// console.log(now);
	$('.bootstrap-datepicker').bsdatepicker({
		format: 'dd/mm/yyyy',
		onRender: function(date) {
			return date.valueOf() < now.valueOf() ? 'disabled' : '';
		}
	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
	});

	$('.contractStartDate').bsdatepicker({
		format: 'dd/mm/yyyy',
	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
	});
	
	$('#loaDate').bsdatepicker({
		format: 'dd/mm/yyyy',
	}).on('changeDate', function(e) {
		//$(this).blur();
		$(this).bsdatepicker('hide');
		console.log('LOA Date ', $(this).val())	;
	});
	
	$('#agrDate').bsdatepicker({
		format: 'dd/mm/yyyy',
	}).on('changeDate', function(e) {
		//$(this).blur();
		$(this).bsdatepicker('hide');
		console.log('Agreement Date ', $(this).val())	;
	});

});


$.formUtils.addValidator({
	name: 'validate_custom_length',
	validatorFunction: function(value, $el, config, language, $form) {
		var val = value.split(".");
		var regp = new RegExp('[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)');
		var t = regp.test(val);
		if (val[0].replace(/,/g, '').length > 16 || t == true) {
			return false;
		} else {
			return true;
		}
	},
	errorMessage: 'The input value is longer than 16 characters',
	errorMessageKey: 'validateLengthCustom'
});

$("#idAddReminder").click(function(e) {
	e.preventDefault();
	var reminderDate = $.trim($('#contractEndDateId').val());
	if (reminderDate != '') {
		$('#addReminder').find('#remindMeDays').val('');
		$("#addReminder").modal("show");
	} else {
		$('#contractEndDateId').blur();
	}
});

$("#saveReminderButton").click(function(e) {
	e.preventDefault();
	if ($('#reminder1').isValid()) {
		var remindMeDays = $('#remindMeDays').val();

		if (remindMeDays.length == 0) {
			$.jGrowl("Reminder Days cannot be empty", {
				sticky: false,
				position: 'top-right',
				theme: 'bg-red'
			});
		}

		if ($('.contractReminderList').find(('input[value="' + remindMeDays + '"]')).val() == remindMeDays) {
			$.jGrowl("Reminder already exists for same date", {
				sticky: false,
				position: 'top-right',
				theme: 'bg-red'
			});
			return false;
		}

		var contractEndDate = $.trim($('#contractEndDateId').val());
		console.log('contractEndDate', contractEndDate);
		
		var dateParts = contractEndDate.split("/");
		console.log('dateParts', dateParts);

		var contractStartDate = $.trim($('#contractStartDateId').val());
		console.log('contractStartDate', contractStartDate);
		var startDateParts = contractStartDate.split("/");
		console.log('startDateParts', startDateParts);

		// month is 0-based, that's why we need dataParts[1] - 1
		var reminderDate = new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);
		reminderDate.setDate(reminderDate.getDate() - parseInt(remindMeDays));
		console.log('reminderDate', reminderDate);

		if (reminderDate.getTime() < new Date().getTime()) {
			$.jGrowl("Reminder Date cannot be in past", {
				sticky: false,
				position: 'top-right',
				theme: 'bg-red'
			});
			return false;
		} else if(reminderDate.getTime() < new Date(+startDateParts[2], startDateParts[1] - 1, +startDateParts[0]).getTime()) {
			$.jGrowl("Reminder Date cannot be less than Stratt Date", {
				sticky: false,
				position: 'top-right',
				theme: 'bg-red'
			});
			return false;
		} else {
			$('.contractReminderList.marginDisable .blankEvelator').remove();
			var dataHtml = '<div class="row">';
			dataHtml += '<div class="col-md-10"> <p><b>Reminder Date: </b>' + moment(reminderDate).format('DD/MM/YYYY') + '</p> </div>';
			dataHtml += '<div class="col-md-2"> <a href="" class="removeContractReminder"><i class="fa fa-times-circle"></i></a></div>';
			dataHtml += '<input type="hidden" name="remindMeDays" value="' + remindMeDays + '">';
			dataHtml += '</div>';
			$('.contractReminderList.marginDisable').append(dataHtml);
		}

		$('#addReminder').modal('toggle');
	}
});

$(document).delegate('.removeContractReminder', 'click', function(e) {
	e.preventDefault();
	$(this).closest('.row').remove();
});



$('#saveProductListMaintenance').click(function(e) {
	if (!$('#productListMaintenanceForm').isValid()) {
		return false;
	}
	
	$('[name="docDesc"]').each(function( index ) {
		if($(this).val() == '') {
			$(this).val(' ');
		}
	});
	
	if(document.getElementById('idEnableApproval').checked && $('.approval_id_hidden').length == 0) {
		$.jGrowl("Approval levels need to be defined if Contract Approvals is enabled.", {
			sticky: false,
			position: 'top-right',
			theme: 'bg-red'
		});		
		 return false;
	}
	
	if ($('#contractValue').val() == 0) {
		$.jGrowl("Contract value must be greater than zero", {
			sticky: false,
			position: 'top-right',
			theme: 'bg-red'
		});
		return false;
	} else {
		$('#loading').show();
		$("#productListMaintenanceForm").submit();
	}
});

$(document).delegate('.decimalChange', 'change', function(e) {
	var decimalLimit = $(this).val();
	var contractValue = parseFloat($('#contractValue').val().replace(/\,|\s|\#/g, ''));
	if ($('#contractValue').val() === '' || $('#contractValue').val() === undefined) {
		$('#contractValue').val('');
	} else {
		contractValue = !isNaN(contractValue) ? contractValue.toFixed(decimalLimit) : '';
		$('#contractValue').val(ReplaceNumberWithCommas((contractValue)));
	}

	var unitPriceId = parseFloat($('#unitPriceId').val().replace(/\,|\s|\#/g, ''));
	if ($('#unitPriceId').val() === '' || $('#unitPriceId').val() === undefined) {
		$('#unitPriceId').val('');
	} else {
		unitPriceId = !isNaN(unitPriceId) ? unitPriceId.toFixed(decimalLimit) : '';
		$('#unitPriceId').val(ReplaceNumberWithCommas((unitPriceId)));
	}


	var itemQuantity = parseFloat($('#itemQuantity').val().replace(/\,|\s|\#/g, ''));
	if ($('#itemQuantity').val() === '' || $('#itemQuantity').val() === undefined) {
		$('#itemQuantity').val('');
	} else {
		itemQuantity = !isNaN(itemQuantity) ? itemQuantity.toFixed(decimalLimit) : '';
		$('#itemQuantity').val(ReplaceNumberWithCommas((itemQuantity)));
	}

});
$(document).delegate('input[name="contractValue"]', 'change', function(e) {
	var decimalLimit = $('.decimalChange').val();
	var contractValue = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
	contractValue = !isNaN(contractValue) ? contractValue.toFixed(decimalLimit) : '';
	$('#contractValue').val(ReplaceNumberWithCommas((contractValue)));
});

/*$(document).delegate('input[name="quantity"]', 'change', function(e) {
	var decimalLimit = $('.decimalChange').val();
	var quantity = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
	quantity = !isNaN(quantity) ? quantity.toFixed(decimalLimit) : '';
	$('#itemQuantity').val(ReplaceNumberWithCommas((quantity)));
});
*/
$(document).delegate('input[name="unitPrice"]', 'change', function(e) {
	var unitPrice = parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
	unitPrice = !isNaN(unitPrice) ? unitPrice.toFixed(decimalLimit) : '';
	$('#unitPriceId').val(ReplaceNumberWithCommas((unitPrice)));
});


function ReplaceNumberWithCommas(yourNumber) {
	// Seperates the components of the number
	var n = yourNumber.toString().split(".");
	// Comma-fies the first part
	n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	// Combines the two sections
	return n.join(".");
}

var i = 1;
$('#addMoreFiles').on('click', function(e) {
	e.preventDefault();
	i++;
	var html = '<div class="hideThis">';
	html += '<div class="">';
	html += '<div class="marg-top-2">';
	html += '<label>Select File</label>';
	html += '</div>';
	html += '<div class="">';
	html += '<div class="fileinput fileinput-new input-group" data-provides="fileinput">';
	html += '<div data-trigger="fileinput" class="form-control">';
	html += '<span class="fileinput-filename show_name "></span>';
	html += '</div>';
	html += '<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> Select File';
	html += '</span> <span class="fileinput-exists"> Change';
	html += '</span> <input name="docs" class="other_docs" id="docs" data-validation-allowing="' + fileType + '" data-validation-error-msg-container="#Load_File-error' + i + '"data-validation-max-size="' + fileSizeLimit + 'M" type="file" data-validation="required mime size" data-validation-error-msg-size="You can not upload file larger than ' + fileSizeLimit + 'MB" data-validation-error-msg-mime="You can only upload files of type ' + fileType + '">';
	html += '</span>';
	html += '</div>';
	html += '<div id="Load_File-error' + i + '"style="width: 100%; float: left; margin-top: -15px;"></div>';
	html += '<textarea class="form-control"  name="docDesc" id="docDesc" data-validation="length" data-validation-length="max250" type="text" placeholder="File Description" maxlength="250"></textarea>';
	html += '<div class="sky-blue">Max 250 characters only</div>';
	html += '</div>';
	html += '<button class="close select-abs closePopUp" type="button">&times;</button>';
	html += '</div>';
	html += '</div>';

	$('#appendFile').append(html);
});


$(document).delegate('.closePopUp', 'click', function() {
	$(this).closest("div.hideThis").html('');
});


$(document).delegate('.deleleteDocument', 'click', function(e) {
	e.preventDefault();
	$('#confirmDeleteDocument').find('#deleteIdDocument').val($(this).attr('data-id'));
	$('#confirmDeleteDocument').modal();
});

$(document).delegate('#confDelDocument', 'click', function(e) {
	e.preventDefault();
	console.log("&&&&&");
	var documentId = $('#confirmDeleteDocument').find('#deleteIdDocument').val();
	var contractId = $('#idcontract').val();
	var fileName = $('#deleleteDocument').attr('data-name');
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url: getContextPath() + '/buyer/deleteContractDocument',
		data: {
			'documentId': documentId,
			'contractId': contractId
		},
		type: 'POST',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			$('.doc-' + documentId).html('');
			$('.doc-' + documentId).hide();
			$('#confirmDeleteDocument').modal('hide');

			$.jGrowl("File " + fileName + " Deleted successfully", {
				sticky: false,
				position: 'top-right',
				theme: 'bg-green'
			});
		},
		error: function(request, textStatus, errorThrown) {
			console.log(request);
			var info = request.getResponseHeader('error').split(",").join("<br/>");
			$.jGrowl(info, {
				sticky: false,
				position: 'top-right',
				theme: 'bg-red'
			});
		},
		complete: function() {
			$('#loading').hide();
		}
	});
});

$(document).delegate('.deleteLoaDocument', 'click', function(e) {
	e.preventDefault();
//	$('#confirmDeleteLoaDocument').find('#deleteIdLoaDocument').val($(this).attr('data-id'));
	$('#confirmDeleteLoaDocument').modal();
});

$(document).delegate('#confDelLoaDocument', 'click', function(e) {
	e.preventDefault();
	console.log("&&&&&");
	var documentId = $('#loaAndAgreementId').val();
	var contractId = $('#id').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url: getContextPath() + '/buyer/deleteLoaDocument',
		data: {
			'documentId': documentId,
			'contractId': contractId
		},
		type: 'POST',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			$('.loadoc-' + documentId).html('');
			$('.loadoc-' + documentId).hide();
			$('.loadocfile-' + documentId).show();

			$.jGrowl("LOA File Deleted successfully", {
				sticky: false,
				position: 'top-right',
				theme: 'bg-green'
			});
		},
		error: function(request, textStatus, errorThrown) {
			console.log(request);
			var info = request.getResponseHeader('error').split(",").join("<br/>");
			$.jGrowl(info, {
				sticky: false,
				position: 'top-right',
				theme: 'bg-red'
			});
		},
		complete: function() {
			$('#loading').hide();
			$('#confirmDeleteLoaDocument').modal('hide');
		}
	});
});


$(document).delegate('.deleleteAgreementDocument', 'click', function(e) {
	e.preventDefault();
	$('#confirmDeleteAgreementDocument').find('#deleteIdDocument').val($(this).attr('data-id'));
	$('#confirmDeleteAgreementDocument').modal();
});

$(document).delegate('#confDelAgreementDocument', 'click', function(e) {
	e.preventDefault();
	console.log("&&&&&");
	var documentId = $('#loaAndAgreementId').val();
	var contractId = $('#id').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url: getContextPath() + '/buyer/deleteAgreementDocument',
		data: {
			'documentId': documentId,
			'contractId': contractId
		},
		type: 'POST',
		dataType: 'json',
		beforeSend: function(xhr) {
			xhr.setRequestHeader(header, token);
			$('#loading').show();
		},
		success: function(data, textStatus, request) {
			var success = request.getResponseHeader('success');
			$('.agrdoc-' + documentId).html('');
			$('.agrdoc-' + documentId).hide();
			$('.agrdocfile-' + documentId).show();

			$.jGrowl("Agreement File Deleted successfully", {
				sticky: false,
				position: 'top-right',
				theme: 'bg-green'
			});
		},
		error: function(request, textStatus, errorThrown) {
			console.log(request);
			var info = request.getResponseHeader('error').split(",").join("<br/>");
			$.jGrowl(info, {
				sticky: false,
				position: 'top-right',
				theme: 'bg-red'
			});
		},
		complete: function() {
			$('#loading').hide();
			$('#confirmDeleteAgreementDocument').modal('hide');
		}
	});
});
	
	$(document).delegate('.removeApproval', 'click', function(e) {
		$(this).closest(".new-approval").remove();
		$(this).closest(".approval_id_hidden").remove();

		$(".new-approval").each(function(i, v) {
			i++;
			$(this).attr("id", "new-approval-" + i);
			$(this).find(".level").text('Level ' + i);
			
			$(this).find(".approval_id_hidden").each(function(){					
				$(this).attr("name",'approvals[' +(i-1) + '].id');					
			});
			
			$(this).find(".approval_condition").each(function(){
				$(this).attr("name",'approvals[' +(i-1) + '].approvalType');
			}) // checkbox name reindex
			
			$(this).find("input[name='_approvals[" + i + "].approvalType']").each(function(){
				$(this).attr('name','_approvals[' +(i-1) + '].approvalType');
			}); //Checkbox hidden val reindex
			
			
			$(this).find(".tagTypeMultiSelect").each(function(){
				$(this).attr("name",'approvals[' +(i-1) + '].approvalUsers');
				$(this).attr("id", "multipleSelectExample-" + ((i-1)) + "");
			}) //select name reindex
			
			$(this).find("input[name='_approvals[" + i + "].approvalUsers']").each(function(){
				$(this).attr("name",'_approvals[' +(i-1) + '].approvalUsers');
			}) //select name reindex hidden
			
		});
		index--;
		
	});
	
	$("#idEnableApproval").change(function() {
	    if(this.checked) {
	    	$('.cloneready').show();
	    	$('.approval_reminder').removeClass('disabled');
	    } else {
			$('.approval_reminder').addClass('disabled');
			$("#idEnableApprovalReminder").prop("checked", false);
			$('#idEnableApprovalReminder').change();
			$.uniform.update();			
			$(".new-approval").remove();
			$(".approval_id_hidden").remove();	
	    	$('.cloneready').hide();
	    	index = 1;
	    }
		$.uniform.update();
	});
	// $('.access_check').on('click', function(e) {
			$(document).delegate('.access_check', 'click', function(e) {
				$('.access_check').prop('checked', false);
				$(this).prop('checked', true);
				tempId = $(this).attr("id");
				selector = $("#" + tempId).closest("div").find(".dropdown-toggle");
				console.log(selector);
				if ($(this).val() == "Editor") {

					selector.html("<i class='glyphicon glyphicon-pencil' aria-hidden='true '></i>");
				}
				if ($(this).val() == "Associate_Owner") {
					selector.html("<i class='fa fa-user-plus' aria-hidden='true '></i>");
				}
				if ($(this).val() == "Viewer") {
					selector.html("<i class='glyphicon glyphicon-eye-open' aria-hidden='true '></i>");
				}

				if ($(this).data('uid') == "" || $(this).data('uid') == undefined) return;

				/** ** Update ** */
				var memberType = $(this).val();
				var currentBlock = $(this);

				var userId = $(this).data('uid');
				var eventId = $('.event_form').find('#id').val();
				var contractId = $("#id").val();
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				$.ajax({
					type : "POST",
					url : getContextPath() + '/buyer/addContractTeamMember',
					data : {
						memberType : memberType,
						userId : userId,
						contractId : contractId
					},
					beforeSend : function(xhr) {
						$('#loading').show();
						xhr.setRequestHeader(header, token);
						xhr.setRequestHeader("Accept", "application/json");
						$('#loading').hide();
					},
					success : function(data) {
						userList = userListForEvent(data);
						if ($('#eventTeamMembersList').length > 0) {
							$('#eventTeamMembersList').DataTable().ajax.reload();
						}

						$('#appUsersList').html("");
						$('#appUsersList').html(userList);
						$('#TeamMemberList').find('option[value="' + userId + '"]').remove();
						$('#TeamMemberList').trigger("chosen:updated");
					},
					error : function(request, textStatus, errorThrown) {
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
						$('div[id=idGlobalError]').show();
					},
					complete : function() {
						$('#loading').hide();
						//$("#teamMemberListPopup").hide();
						//window.location.href = window.location.href;
					}
				});

			});
			
	$('.addTeamMemberToList').click(function(e) {
		e.preventDefault();
		console.log("*****");
		var currentBlock = $(this);

		var memberType = $(this).closest('div').find('.access_check:checkbox:checked').val(); // $('.access_check:checkbox:checked').val();//
		if (memberType == undefined || memberType == "") {
			memberType = "Viewer";
		}
		var userId = $("#TeamMemberList").val();// currentBlock.parent().prev().find('select').val();
		var formId = $('.event_form').find('#id').val();
		var contractId = $("#id").val();
		console.log("Contract Id  " +contractId);
		var ajaxUrl = getContextPath() + "/buyer/addContractTeamMember"
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		if (userId == "") {
			$("#editor-err").removeClass("hide ");
			return;
		}
		$("#editor-err").addClass("hide");
		$.ajax({
			type : "POST",
			url : ajaxUrl,
			data : {
				memberType : memberType,
				userId : userId,
				formId : formId,
				contractId : contractId
			},
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
			},
			success : function(data) {
				userList = userListForEvent(data);

				$('#appUsersList').html("");
				$('#appUsersList').html(userList);
				$('#TeamMemberList').find('option[value="' + userId + '"]').remove();
				$('#TeamMemberList').trigger("chosen:updated");
			},
			error : function(request, textStatus, errorThrown) {

				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
			},
			complete : function() {
				$('#loading').hide();
				//window.location.href = window.location.href;
			}
		});
	});
	
	$(document).delegate('.removeApproversList', 'click', function(e) {
	
		e.preventDefault();
		var currentBlock = $(this);
		var listType = currentBlock.attr('list-type');
		var listUserId = currentBlock.closest('tr').attr('approver-id');
		var userName = currentBlock.closest('tr').attr('data-username');
	
		$("#removeApproverListPopup").dialog({
			modal : true,
			maxWidth : 400,
			minHeight : 100,
			dialogClass : "",
			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded"
		});
		$('.ui-widget-overlay').addClass('bg-white opacity-60');
		$("#removeApproverListPopup").find('.approverInfoBlock').find('span:first-child').text(userName);
		$("#removeApproverListPopup").find('.approverInfoBlock').find('span:last-child').text(listType);
		$("#removeApproverListPopup").find('#approverListId').val(listUserId);
		$("#removeApproverListPopup").find('#approverListUserName').val(userName);
		$("#removeApproverListPopup").find('#approverListType').val(listType);
		/* $('.ui-dialog-title').text('Remove ' + listType); */
	});	
	
	$(document).delegate('.removeApproverListPerson', 'click', function(e) {
	
		e.preventDefault();
		
		var userId = $("#removeApproverListPopup").find('#approverListId').val();
		var listType = $("#removeApproverListPopup").find('#approverListType').val();
		var userName = $("#removeApproverListPopup").find('#approverListUserName').val();
		var contractId = $("#id").val();
		console.log("Contract Id  " + contractId);
		var ajaxUrl = getContextPath() + "/buyer/removeContractTeamMember"
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		$.ajax({
			type : "POST",
			url : ajaxUrl,
			data : {
				userId : userId,
				contractId : contractId
			},
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
			},
			success : function(data) {
				$.jGrowl("Team member '" + userName + "' removed successfully", {
					header: 'Success',
					sticky: false,
					position: 'top-right',
					theme: 'bg-green'
				});
				
				userList = userListForEvent(data);
				$('#appUsersList').html("");
				$('#appUsersList').html(userList);
				
				updateUserList('', $('#TeamMemberList'), 'NORMAL_USER');
				$("#removeApproverListPopup").dialog('close');
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	
	});	
	
	function userListForEvent(data) {
		var userList = '';
		$.each(data, function(i, user) {
			userList += '<tr  data-username="' + user.user.name + '" approver-id="' + user.user.id + '">';
			userList += '<td class="width_50_fix "></td>';
			userList += '<td>' + user.user.name + '<br> <span>' + user.user.loginId + '</span> </td>';
			userList += '<td class="edit-drop"><div class="advancee_menu"><div class="adm_box">';
			if (user.teamMemberType == "Editor")
				userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"  title="Editor"> <i class="glyphicon glyphicon-pencil " aria-hidden="true"></i> </a>';
			else if (user.teamMemberType == "Viewer")
				userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"  title="Viewer"> <i class="glyphicon glyphicon-eye-open" aria-hidden="true"></i> </a>';
			else if (user.teamMemberType == "Associate_Owner") userList += '<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown"  title="Associate Owner"> <i class="fa fa-user-plus" aria-hidden="true"></i> </a>';
			userList += '<ul class="dropdown-menu dropup"><li><a href="javascript:void(0);" class="small" style="margin-top: 10px;"><input ';
			if (user.teamMemberType == "Editor") userList += ' checked="checked"  ';
			userList += 'data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Editor" type="checkbox" value="Editor">&nbsp;<label for="' + user.user.id + '-Editor">Editor</label></a> </li>';
			userList += '<li><a  href="javascript:void(0);" class="small"><input data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Viewer" ';
			if (user.teamMemberType == "Viewer") userList += ' checked="checked"  ';
			userList += 'data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Viewer" type="checkbox" value="Viewer">&nbsp;<label for="' + user.user.id + '-Viewer">Viewer<label></a> </li>';
			userList += '<li><a  href="javascript:void(0);" class="small"><input data-uid="' + user.user.id + '" class="access_check" id="' + user.user.id + '-Associate_Owner" ';
			if (user.teamMemberType == "Associate_Owner") userList += ' checked="checked"  ';
			userList += 'type="checkbox" value="Associate_Owner">&nbsp;<label for="' + user.user.id + '-Associate_Owner">Associate Owner</label></a> </li>';
			userList += '</ul></li></ul></div></div></td><td>'
			userList += '<div class="cqa_delx"> <a href="#" list-type="Team Member" class="adm_menu_link removeApproversList"  title="Remove"><i class="glyphicon glyphicon-trash" style="color: red" aria-hidden="true" title="Remove"></i></a> </div></td></tr>'
		
 

		});
		return userList;
	}
	
	$('#cancelContract').click(function(){
		if( $('#idFrmCancelContract').isValid()) {
			$('#idFrmCancelContract').submit();
			$('#loading').show();
		}
	});


