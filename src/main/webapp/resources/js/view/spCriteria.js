$(document).delegate('.validateNumber', 'keydown', function(e) {
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

$(document).ready(function() {
	$('.cq_form_google_form').hide();	
	
	$(document).on("click", "#addCriteria", function () {
		$('#crateNewSection').find('#crtrName, #idDescription, #wghtg, #crtrId, input[type="text"]').val('');
		$('#allowToUpdateSectionWeightage').prop('checked', false).change().uniform();
		// $.uniform.update();
		$('#savePrSection').text('Create');
	    $('#addEditSectionForm').trigger("reset");
		$('#crateNewSection').modal('show');
		var templateId = $(this).attr("data-templ-id");
		$("#tempId").val(templateId);
		$('#sectionTitle').text('Add Criteria');
	});
	
	$(document).delegate('#confirmDeleteCriteria', 'click', function(e) {
	e.preventDefault();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var criteriaId = $(this).attr("data-critr-id");
	var templateId = $(this).attr("data-templt-id");
	console.log("criteriaId..  "+criteriaId +" temp.... "+templateId);
	$('#myModalCriteriaDelete').modal('hide');
	
	$.ajax({
		url : getContextPath() + '/buyer/supplierPerformanceTemplateCriteriaList/deleteCriteria',
		data : {
			criteriaId : criteriaId,
			templateId : templateId
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
			$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
			$('.alert').hide();
			$('.alert-danger').hide();
			$('div[id=idGlobalSuccess]').show();
			
			window.location.href = getContextPath() + "/buyer/supplierPerformanceTemplateCriteriaList/" + templateId;
			
			$('#loading').hide();
		},
		error : function(request, textStatus, errorThrown) {
			var error = request.getResponseHeader('error');
			$('p[id=idGlobalErrorMessage]').html(error);
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
	
	$(document).delegate('#cancelSubCriteriaForm', 'click', function(e) {
		$('#criteriaName').val();
		$('#maximumScore').val();
		$('#weightage').val();
		
		$('#add_subCriteria_form').hide();
	});

});


$(document).on("click", ".add_subCriteria_table", function () {
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	$('#parentId').val($(this).parents('.sub_item').attr('data-id'));
	console.log("........xyz "+xyz);
	
	$("#creat_seaction_form").hide();
	$('.cq_form_google_form').find('#criteriaName, #description, #maximumScore, #weightage, #criteriaId, input[type="text"]').val('');
	$('.cq_form_google_form').find('#maximumScore').val(xyz);
	$('.cq_form_google_form').show();
	var criteriaId = $(this).attr("data-criteria-id");
	console.log(">>>>>>>>>>>>>>>> "+criteriaId);
	$('#createSubCriteria').text('Create');
	$('.checker span').find('.glyph-icon.icon-check').remove();
	$('.checker span').append('<i class="glyph-icon icon-check"></i>');
	$('#add_subCriteria_form').find('#createSubCriteria').attr("data-parent-id", criteriaId);

	$.uniform.update();
});

$(document).on("click", ".Edit_section_table", function() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var criteriaId = $(this).attr("data-criteria-id");
	var templateId = $(this).attr("data-template-id");
	$('#savePrSection').text('Update');
	$('#sectionTitle').text('Edit Criteria');

	$.ajax({
		url : getContextPath() + '/buyer/supplierPerformanceTemplateCriteriaList/editCriteria',
		data : {
			templateId : templateId,
			criteriaId : criteriaId
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
			$('#addEditSectionForm').attr("modelAttribute",data);
			$.each(data, function(i, item) {
				if (i == "name")
					$("#crtrName").val(item);
				if (i == "description")
					$("#idDescription").val(item);
				if (i == "allowToUpdateSectionWeightage"){
					console.log("Booolean >>>>>>>>> "+item);
					$("#allowToUpdateSectionWeightage").prop('checked', item).change().uniform();
				}
				if (i == "weightage")
					$("#wghtg").val(item);		

				$("#crtrId").val(criteriaId);
				$("#tempId").val(templateId);

				$('#crateNewSection').modal('show');
			});
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});
	
	
$(document).on("click", ".editSubitme", function() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var criteriaId = $(this).attr("data-crtr-id");
	var templateId = $(this).attr("data-templ-id");
	var parentId = $(this).attr("data-parent-id");
	$('#createSubCriteria').text('Update');
	
	$.ajax({
		url : getContextPath() + '/buyer/supplierPerformanceTemplateCriteriaList/editCriteria',
		data : {
			templateId : templateId,
			criteriaId : criteriaId
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
			$('#addSubCriteriaform').attr("modelAttribute",data);
			$.each(data, function(i, item) {
				if (i == "name")
					$("#criteriaName").val(item);
				if (i == "description")
					$("#description").val(item);
				if (i == "weightage")
					$("#weightage").val(item);	
				if (i == "maximumScore")
					$("#maximumScore").val(item);			

				$("#criteriaId").val(criteriaId);
				$("#templateId").val(templateId);
				$("#parentId").val(parentId);

				$('#add_subCriteria_form').show();
			});
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});


$(document).on("click", ".deleteCriteria", function() {
	var criteriaId = $(this).attr("data-critr-id");
	var templateId = $(this).attr("data-templt-id");
	
	$('#confirmDeleteCriteria').attr("data-critr-id", criteriaId);
	$('#confirmDeleteCriteria').attr("data-templt-id", templateId);	
	$('#myModalCriteriaDelete').modal('show');
});


// code for reorder
var ns = $('ol.sortable').nestedSortable({
	forcePlaceholderSize: true,
	handle: 'div',
	helper: 'clone',
	items: 'li:not(.disableDragDrop)',
	opacity: .6,
	placeholder: 'placeholder',
	revert: 250,
	tabSize: 25,
	tolerance: 'pointer',
	toleranceElement: '> div',
	maxLevels: 2,
	isTree: true,
	expandOnHover: 700,
	startCollapsed: false,
	isAllowed: function(item, parent, currentItem) {
		return true;
	},
	change: function(e) {
		// console.log(e);
	},
	stop: function(item, currentItem) {
		var templateId = $('#templateId').val();
		var itemId = currentItem.item.attr('data-item');
		var parentId = currentItem.item.attr('data-parent');
		var oldPos = parseInt(currentItem.item.attr('data-level'));
		var itemName = currentItem.item.find('span.item_name').html();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		
		console.log('Old Position ', oldPos);
		
		var newParentId = currentItem.item.parents('li').attr('data-item');
		if (newParentId == undefined) {
			newParentId = '';
		}
		var numberPos = currentItem.item.children('div').find('.number').text();
		var newPos = 1;
		if ($('ol.sortable').find('li[data-item="' + itemId + '"]').prev().attr('data-level') === undefined) {
		} else {
			newPos = parseInt($('ol.sortable').find('li[data-item="' + itemId + '"]').prev().attr('data-level'));
			if (newPos < oldPos || newParentId !== parentId) {
				newPos = newPos + 1;
			}
		}

		console.log('New Position ', newPos);
		currentItem.item.attr('data-parent', newParentId);

		var data = {};
		data["id"] = itemId;
		data["parent"] = newParentId;
		data["order"] = newPos;
		data["templateId"] = templateId;
		data["name"] = name;
		var supplierBqCount = $('#supplierBqCount').val();
		var addBillOfQuantityControl = $('.addBillOfQuantityControl').val();
		$.ajax({
			url: getContextPath() + '/buyer/supplierPerformanceTemplateCriteriaList/criteriaOrder',
			data: JSON.stringify(data),
			type: "POST",
			contentType: "application/json",
			dataType: 'json',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success: function(data, textStatus, request) {
				var success = request.getResponseHeader('success');
				if (success != undefined) {
					$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('div[id=idGlobalSuccess]').show();
				}
				var isPageEnable = true;
				var table = constructTable(data, isPageEnable);

				$('#bqitemList').html(table);
				$('#s1_tender_delete_btn').addClass('disabled');
				$('.custom-checkbox.checksubcheckbox').uniform();
				$('.checker span').find('.glyph-icon.icon-check').remove();
				$('.checker span').append('<i class="glyph-icon icon-check"></i>');
				// numberingTable();
				$('#loading').hide();
			},
			error: function(request, textStatus, errorThrown) {
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
				// numberingTable();
				$('#loading').hide();
			},
			complete: function() {
				$('.custom-checkbox.checkallcheckbox').prop("checked", false);
				$.uniform.update()
				$('#loading').hide();
			}
		});
	}
});

function constructTable(data, isPageEnable) {
	var table = '';
	// console.log("data :" + data);

	if (data != "") {
		$.each(data, function(i, item) {
			// console.log("item :"+item);
			table += searchRenderGrid(item, isPageEnable);
		});
	} else {
		table += "No matching records found";
	}
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
	var suspensionType = $('#suspensiontype').val();
	var eventstatus = $('#eventstatus').val();
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

function drawSection(item, disableDragDrop, isPageEnable) {
	var suspensionType = $('#suspensiontype').val();
	var eventstatus = $('#eventstatus').val();
	var countNewData = 1;
	var table = '';

	table += '<div class="menuDiv">';
	table += '<table class="table data ph_table diagnosis_list sorted_table" id="table_id" style=" background: #fff;">';
	table += '<tr class="sub_item" data-id="' + item.id + '">';
	table += '<td class="width_50 width_50_fix move_col align-left">';
	table += '<a href="#"> <i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>';
	table += '</a>';
	table += '</td>';
	table += '<td class="width_150 width_150_fix align-left">';
	if(!isTemplateUsed) {
		if(item.order == 0) {
			table += '<a title="" style="float:left;" class="Edit_section_table" data-criteria-id="' + item.id + '" data-template-id="' + item.templateId + '" href="#creat_seaction_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="Edit" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i>';
			table += '</a>';
			table += '<a title="" style="float:left;" class="deleteCriteria" id="deleteCriteria" data-critr-id="' + item.id + '" data-templt-id="' + item.templateId + '" href="#myModalCriteriaDelete"><i class="glyph-icon tooltip-button custom-icon icon-trash" title="Delete" data-original-title="icon-trash" aria-describedby="tooltip69681"></i> </a>';
			table += '<a title="" style="float:left;" class="add_subCriteria_table" id="addSubCriteria" href="#creat_subitem_form"><i class="glyph-icon tooltip-button custom-icon icon-plus" title="Add" data-original-title=".icon-plus" aria-describedby="tooltip69681"></i> </a>';
			
		} else {
			table += '<a title="" style="float:left;" class="editSubitme" data-crtr-id="' + item.id + '" data-templ-id="' + item.templateId + '" data-parent-id="' + item.parent + '" href="#creat_subitem_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="Edit" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i></a>';
			table += '<a title="" style="float:left;" class="deleteCriteria" id="deleteCriteria" data-critr-id="' + item.id + '" data-templt-id="' + item.templateId + '" href="#myModalCriteriaDelete"><i class="glyph-icon tooltip-button custom-icon icon-trash" title="Delete" data-original-title="icon-trash" aria-describedby="tooltip69681"></i> </a>';
		} 
		table += '</td>';
		
	}
	if(item.order == 0) {
		table += '<td class="width_50 width_50_fix align-left">';
		table += '<span class="sectionD section_name">' + item.level + '.' + item.order + '</span>';
		table += '</td>';
		table += '<td class="width_200 align-left">';
		table += '<span class="sectionD section_name">' + item.name + '</span>';
		if (item.description != undefined || item.description == "") {
			table += '<span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)">View Description';
			table += '</a>';
			table += '</span>';
			
		}
		table += '<p class="s1_tent_tb_description s1_text_small">'+ item.description + '</p>';
		table += '</td>';
		table += '<td class="width_150 width_150_fix align-right section_name">' + "" + '</td>';
		table += '<td class="width_150 width_150_fix align-right section_name">' + item.weightage + '</td>';
	} else {
		table += '<td class="width_50 width_50_fix align-left">';
		table += '<span class="sectionD">' + item.level + '.' + item.order + '</span>';
		table += '</td>';
		table += '<td class="width_200 align-left">';
		table += '<span class="sectionD">' + item.name + '</span>';
		if (item.description != undefined || item.description == "") {
			table += '<span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)"> <spring:message code="application.view.description" />';
			table += '</a>';
			table += '</span>';
		}
		table += '<p class="s1_tent_tb_description s1_text_small">'+ item.description + '</p>';
		table += '</td>';
		table += '<td class="width_150 width_150_fix align-right">' + item.maximumScore + '</td>';
		table += '<td class="width_150 width_150_fix align-right">' + item.weightage + '</td>';
	}
	table += '</tr>';
	table += '</table>';
	table += '</div>'; 

	return table;
}


function drawItems(item, disableDragDrop, isPageEnable) {
	var suspensionType = $('#suspensiontype').val();
	var eventstatus = $('#eventstatus').val();
	var countNewData = 1;
	// alert("item id:" + item.id);
	// alert("parent :" + item.parent);
	// alert("parent id :" + item.parent.id);
	var table = '';
	
	table += '<li style="display: list-item;" class="mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="menuItem_2_1" data-foo="baz" data-item="' + item.id + '" data-parent="' + item.parent + '" data-level="' + item.order + '" data-order="' + item.order + '">';
	table += '<div class="menuDiv sub-color-change">';
	table += '<table style=" background: #eef7fc;" class="table data ph_table diagnosis_list sorted_table" id="table_id">';
	table += '<tr class="sub_item" data-id="' + item.id + '">';
	table += '<td class="width_50 width_50_fix move_col align-left">';
	table += '<a href="#"> <i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i>';
	table += '</a>';
	table += '</td>';
	table += '<td class="width_150 width_150_fix align-left">';
	if (!isTemplateUsed) {
		if(item.order == 0) {
			table += '<a title="" style="float:left;" class="Edit_section_table" data-criteria-id="' + item.id + '" data-template-id="' + item.templateId + '" href="#creat_seaction_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="Edit" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i>';
			table += '</a>';
			table += '<a title="" style="float:left;" class="deleteCriteria" id="deleteCriteria" data-critr-id="' + item.id + '" data-templt-id="' + item.templateId + '" href="#myModalCriteriaDelete"><i class="glyph-icon tooltip-button custom-icon icon-trash" title="Delete" data-original-title="icon-trash" aria-describedby="tooltip69681"></i> </a>';
			table += '<a title="" style="float:left;" class="add_subCriteria_table" id="addSubCriteria" href="#creat_subitem_form"><i class="glyph-icon tooltip-button custom-icon icon-plus" title="Add" data-original-title=".icon-plus" aria-describedby="tooltip69681"></i> </a>';
		} else {
			table += '<a title="" style="float:left;" class="editSubitme" data-crtr-id="' + item.id + '" data-templ-id="' + item.templateId + '" data-parent-id="' + item.parent + '" href="#creat_subitem_form"><i class="glyph-icon tooltip-button custom-icon icon-edit" title="Edit" data-original-title=".icon-edit" aria-describedby="tooltip455757"></i></a>';
			table += '<a title="" style="float:left;" class="deleteCriteria" id="deleteCriteria" data-critr-id="' + item.id + '" data-templt-id="' + item.templateId + '" href="#myModalCriteriaDelete"><i class="glyph-icon tooltip-button custom-icon icon-trash" title="Delete" data-original-title="icon-trash" aria-describedby="tooltip69681"></i> </a>';
		}
		
	}
	table += '</td>';
	if(item.order == 0) {
		table += '<td class="width_50 width_50_fix align-left">';
		table += '<span class="sectionD section_name">' + item.level + '.' + item.order + '</span>';
		table += '</td>';
		table += '<td class="width_200 align-left">';
		table += '<span class="sectionD section_name">' +  item.name + '</span>';
		if (item.description != undefined || item.description == "") {
			table += '<span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)"> <spring:message code="application.view.description" />';
			table += '</a>';
			table += '</span>';
		}
		table += '<p class="s1_tent_tb_description s1_text_small">' + item.description + '</p>';
		table += '</td>';
		table += '<td class="width_150 width_150_fix align-right section_name">' + "" + '</td>';
		table += '<td class="width_150 width_150_fix align-right section_name">' + item.weightage + '</td>';
	} else {
		table += '<td class="width_50 width_50_fix align-left">';
		table += '<span class="sectionD">' + item.level + '.' + item.order + '</span>';
		table += '</td>';
		table += '<td class="width_200 align-left">';
		table += '<span class="sectionD">' +  item.name + '</span>';
		if (item.description != undefined || item.description == "") {
			table += '<span class="item_detail"> <a class="s1_view_desc" href="javascript:void(0)"> <spring:message code="application.view.description" />';
			table += '</a>';
			table += '</span>';
		}
		table += '<p class="s1_tent_tb_description s1_text_small">' + item.description + '</p>';
		table += '</td>';
		table += '<td class="width_150 width_150_fix align-right">' + item.maximumScore + '</td>';
		table += '<td class="width_150 width_150_fix align-right">' + item.weightage + '</td>';
	}			
	table += '</tr>';
	table += '</table>';
	table += '</div>';
	table += '</li>';		
			
	return table;
}	

// code for reorder