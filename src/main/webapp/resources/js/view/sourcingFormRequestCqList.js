$(document).ready(function() {	
	$('.uploadQuestionFileAttch').on('change.bs.fileinput', function(event) {
	    if($(this).val() != ''){
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
		oMyForm.append("uploadQuestionFile",currentParentBlock.find('.uploadQuestionFileAttch')[0].files[0]);
		oMyForm.append("cqitemid",currentParentBlock.attr('data-itemId'));		
		//console.log(oMyForm);
		$.ajax({
			url : getContextPath()+ "/uploadQuestionFile",
			data : oMyForm,
			type : "POST",
			enctype : 'multipart/form-data',
			processData : false,
			contentType : false,
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data,textStatus, request) {
				$('div[id=idGlobalSuccess]').show();	
				var table=data.fileName+'<a href="" data-fileid="'+data.id+'" class="pull-right removeFile"><i class="fa fa-trash-o"></i></a>';
				currentParentBlock.hide().find('[data-dismiss="fileinput"]').click();
				currentParentBlock.siblings('div.uploadedFileBlockQues').find('p').html(table).show();
				$('#loading').hide();$('#loading').hide();
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
		var sourcingidHiddenData = $('#sourcingidHiddenData').val();
		var cqId =$('#cqId').val(); 
 		var ajaxUrl = getContextPath() + '/buyer/resetCqAttachment/'+sourcingidHiddenData+'/'+removeOtherId;
		$.ajax({
			url : ajaxUrl,
			type : "POST",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data,textStatus, request) {
				if(data == true){
					$('div[id=idGlobalSuccess]').show();	
					currentParentBlock.hide();
					currentParentBlock.siblings('.uploadFileBlockQuesInput').show().find('[data-dismiss="fileinput"]').click();
					var message = request.getResponseHeader('sucess');
					window.location.href = getContextPath() + "/buyer/viewSourcingCqDetails/"+sourcingidHiddenData+"/"+cqId+"?success="+$.trim(message);
				}else{
					$('div[id=idGlobalError]').show();	
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

$(document).delegate('.sourcingFormRequestCqList', 'click', function(e) {
	e.preventDefault();
	alert("hellooooooooooooo");
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var sourcingReqId = $(this).attr('sourcingReq-id');
	var sourcingCqId = $(this).attr('sourcingCq-id');
	var ajaxUrl = getContextPath() + '/buyer/viewSourcingCqDetails/' + sourcingReqId + '/' + sourcingCqId;
	$.ajax({ 
		url : ajaxUrl,
		type : "POST",
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader("Content-Type", "application/json");
			xhr.setRequestHeader(header, token);
		},
		success : function(data) {
			var html = sourcingReqCqList(data, sourcingReqId, sourcingCqId);
			$(".doc-fir-inner").hide();
			$('.cqlistDetails').html(html);
			
			if(allowedFields != '') {
				disableFormFields(allowedFields);
			}
		},
		error : function(request, textStatus, errorThrown) {
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

function sourcingReqCqList(data, sourcingReqId, sourcingCqId) {
	var html = '';

	html += '<div class="Gen-ques">';
	html += '<h3 class="marg-left-none">General Questionnaire</h3>';
	html += '<div class="Gen-ques-inner">';
	html += '<p>The answer format required is included for most questions. Please complete your answers only in the field column provoided. </p>';
	html += '<p class="marg-top-15 marg-bottom-15">* Fields are required</p>';
	html += '<form class="bordered-row" id="SourcingCqForm" method="post" >';
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
		html += '<div class="col-md-5 col-sm-5 col-xs-6 note-tag">' + item.cqItem.itemDescription + '</div>';
		html += '</div>';
		html += '</div>';

	});
	html += '<div class="clear"></div>';
	html += '<div class="row">';
	html += '<div class="col-md-12 col-xs-12 col-sm-12">';
	html += '<button class="btn btn-black ph_btn_midium back_to_Question">Back to Questionnaire</button>';
	html += '<button class="btn btn-info ph_btn_midium marg-left-10">Save</button>';
	html +='</form>'
	html += '</div>';
	html += '</div>';
	html += '</div>';
	html += '</div>';
	html += '</div>';
	return html;
}

$(document).on("click", ".reArrangeOrder", function(e) {
	$('#confirmReArrangeOrder').modal('show');
});

$('#updateCqOrder').click(function(e) {
	e.preventDefault();
	var cqId = [];
	var formId = $("#formId").val();
	$("#sortable li").each(function(i) {
		cqId.push($(this).attr('data-item'));
    });
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$.ajax({
		url : getContextPath() + '/buyer/updateSourcingCqOrder/' + formId,
		data : JSON.stringify(cqId),
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
			window.location.href = getContextPath() + "/buyer/sourcingFormRequestCqList/"+formId+"?success="+$.trim(success);
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
