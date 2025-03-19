$(document).ready(function() {
	$('.uploadQuestionFileAttch').on('change.bs.fileinput', function(event) {
		if ($(this).val() != '') {
			$(this).parent().addClass('hideElement');
		} else {
			$(this).parent().removeClass('hideElement');
		}
	});
	$(document).delegate('.uploadQuestionFile', 'click', function(e) {
		e.preventDefault();
		var currentParentBlock = $(this).closest('.uploadFileBlockQuesInput');
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var oMyForm = new FormData();
		oMyForm.append("uploadQuestionFile", currentParentBlock.find('.uploadQuestionFileAttch')[0].files[0]);
		oMyForm.append("cqitemid", currentParentBlock.attr('data-itemId'));
		// console.log(oMyForm);
		$.ajax({
			url : getContextPath() + "/uploadQuestionFile",
			data : oMyForm,
			type : "POST",
			enctype : 'multipart/form-data',
			processData : false,
			contentType : false,
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				$('div[id=idGlobalSuccess]').show();
				var table = data.fileName + '<a href="" data-fileid="' + data.id + '" class="pull-right removeFile"><i class="fa fa-trash-o"></i></a>';
				currentParentBlock.hide().find('[data-dismiss="fileinput"]').click();
				currentParentBlock.siblings('div.uploadedFileBlockQues').find('p').html(table).show();
				$('#loading').hide();
				$('#loading').hide();
			},
			error : function(request, textStatus, errorThrown) {
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});
	$(document).delegate('.removeFile', 'click', function(e) {
		e.preventDefault();
		var currentParentBlock = $(this).closest('.uploadedFileBlockQues');
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var removeOtherId = $(this).attr('removeOtherId');
		var otherCredFile = $(this).attr('otherCredFile');
		var eventTypeHiddenData = $('#eventTypeHiddenData').val();
		var eventidHiddenData = $('#eventidHiddenData').val();
		var cqId = $('#cqId').val();
		var ajaxUrl = getContextPath() + '/supplier/resetAttachment/' + eventTypeHiddenData + '/' + eventidHiddenData + '/' + removeOtherId;
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				if (data == true) {
					$('div[id=idGlobalSuccess]').show();
					currentParentBlock.hide();
					currentParentBlock.siblings('.uploadFileBlockQuesInput').show().find('[data-dismiss="fileinput"]').click();
					var message = request.getResponseHeader('sucess');
//					window.location.href = getContextPath() + "/supplier/viewCqDetails/" + eventTypeHiddenData + "/" + cqId + "?success=" + $.trim(message);
					window.location.href = getContextPath() + "/supplier/viewCqDetails/" + eventTypeHiddenData + '/' + cqId  + "/" + eventidHiddenData+ "?success=" + $.trim(message);
				} else {
//					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			},
			error : function(request, textStatus, errorThrown) {
				if (request.getResponseHeader('error')) {
					$('div[id=idGlobalError]').show();
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				}
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});
});

$(document).delegate('.viewSupplierCq', 'click', function(e) {
	e.preventDefault();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var rfteventId = $(this).attr('rftevent-id');
	var eventCqId = $(this).attr('eventCq-id');
	var ajaxUrl = getContextPath() + '/supplier/viewCqDetails/' + getEventType() + '/' + eventCqId + '/' + rfteventId;
	$.ajax({
		url : ajaxUrl,
		type : "POST",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader("Content-Type", "application/json");
			xhr.setRequestHeader(header, token);
		},
		success : function(data) {
			var html = supplierCqList(data, rfteventId, eventCqId);
			$(".doc-fir-inner").hide();
			$('.cqlistDetails').html(html);
			/*
			 * $('.cqlistDetails').find('.custom-select').uniform(); $('.cqlistDetails').find(".selector").append('<i class="glyph-icon
			 * icon-caret-down"></i>'); $('.cqlistDetails').find(".selector").find('span').css('border', 'none');
			 */
			if (allowedFields != '') {
				disableFormFields(allowedFields);
			}
		},
		error : function(request, textStatus, errorThrown) {
			alert("Code me h error");
			console.log("ERROR :  " + request.getResponseHeader('error'));
			if (request.getResponseHeader('error')) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error').split(",").join("<br/>"));
				$('div[id=idGlobalError]').show();
			}
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

function supplierCqList(data, rfteventId, eventCqId) {
	var html = '';

	html += '<div class="Gen-ques">';
	html += '<h3 class="marg-left-none">General Questionnaire</h3>';
	html += '<div class="Gen-ques-inner">';
	html += '<p>The answer format required is included for most questions. Please complete your answers only in the field column provoided. </p>';
	html += '<p class="marg-top-15 marg-bottom-15">* Fields are required</p>';
	html += '<form class="bordered-row" id="SupplierCqForm" method="post" >';
	html += '<div class="Gen-ques-inner1 pad_all_15">';
	$.each(data, function(i, item) {
		var required = item.cqItem.optional ? '*' : '';
		html += '<div class="Quest-textbox">';
		html += '<div class="row">';
		html += '<div class="col-md-12 col-sm-12 col-xs-12">';
		html += '<label>' + required + '' + item.cqItem.itemName + '</label>';
		html += '</div>';
		html += '</div>';
		html += '<div class="row">';
		
		if (item.cqItem.cqType == 'Text') {
			html += '<div class="col-md-5 col-sm-5 col-xs-6">';
			html += '<input type="textarea" class="form-control textarea-autosize"></textarea>';
			if (item.cqItem.attachment) {
				html += '<div class="fileinput fileinput-new input-group" data-provides="fileinput">';
				html += '<div class="form-control" data-trigger="fileinput"> <i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span> </div>';
				html += '<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new">Select file</span> <span class="fileinput-exists">Change</span>';
				html += '<input type="file" name="...">';
				html += '</span> <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a> </div>';
			}
			html += '</div>';
		}

		if (item.cqItem.cqType == 'Choice') {
			html += '<div class="col-md-5 col-sm-5 col-xs-6">';
			$.each(item.cqItem.cqOptions, function(i, option) {
				html += '<div class="radio_yes-no width100">'
				html += '<label class="select-radio">';
				html += '<input type="radio" id="showAll" name="example-radio" value=' + option.value + ' class="custom-radio" checked>' + option.value + '</label>';
				html += '</div>';
			});
			if (item.cqItem.attachment) {
				html += '<div class="fileinput fileinput-new input-group" data-provides="fileinput">';
				html += '<div class="form-control" data-trigger="fileinput"> <i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span> </div>';
				html += '<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new">Select file</span> <span class="fileinput-exists">Change</span>';
				html += '<input type="file" name="...">';
				html += '</span> <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a> </div>';
			}
			html += '</div>';
		}

		if (item.cqItem.cqType == 'List') {
			html += '<div class="col-md-5 col-sm-5 col-xs-6">';
			html += '<select  name="" >';
			html += '<option value="">Select</option>';
			$.each(item.cqItem.cqOptions, function(i, option) {
				html += '<option value="' + option.value + '">' + option.value + '</option>';
			});
			html += '</select>';
			if (item.cqItem.attachment) {
				html += '<div class="fileinput fileinput-new input-group" data-provides="fileinput">';
				html += '<div class="form-control" data-trigger="fileinput"> <i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span> </div>';
				html += '<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new">Select file</span> <span class="fileinput-exists">Change</span>';
				html += '<input type="file" name="...">';
				html += '</span> <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a> </div>';
			}
			html += '</div>';
		}
		if (item.cqItem.cqType == 'Checkboxes') {
			html += '<div class="col-md-5 col-sm-5 col-xs-6">';
			$.each(item.cqItem.cqOptions, function(i, option) {
				html += '<div class="radio_yes-no width100">'
				html += '<input type="checkbox" id="inlineCheckbox114" value="" class="custom-checkbox"  />' + option.value + '</label>';
				html += '</div>';
			});
			if (item.cqItem.attachment) {
				html += '<div class="fileinput fileinput-new input-group" data-provides="fileinput">';
				html += '<div class="form-control" data-trigger="fileinput"> <i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span> </div>';
				html += '<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new">Select file</span> <span class="fileinput-exists">Change</span>';
				html += '<input type="file" name="...">';
				html += '</span> <a href="#" class="input-group-addon btn btn-default fileinput-exists" data-dismiss="fileinput">Remove</a> </div>';
			}
			html += '</div>';
		}
		if (item.cqItem.cqType == 'Date') {
			html += '<div class="col-md-5 col-sm-5 col-xs-6">';
			html += '<input type="text" class="form-control "/> </div>';
		}
		html += '<div class="col-md-5 col-sm-5 col-xs-6 note-tag">' + item.cqItem.itemDescription + '</div>';
		html += '</div>';
		html += '</div>';

	});
	html += '<div class="clear"></div>';
	html += '<div class="row">';
	html += '<div class="col-md-12 col-xs-12 col-sm-12">';
	html += '<button class="btn btn-black ph_btn_midium back_to_Question">Back to Questionnaire</button>';
	html += '<button class="btn btn-info ph_btn_midium marg-left-10">Save</button>';
	html += '</form>'
	html += '</div>';
	html += '</div>';
	html += '</div>';
	html += '</div>';
	html += '</div>';
	return html;

}
