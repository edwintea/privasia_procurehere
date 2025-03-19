var formdata=new FormData();
var fileAttach=0;
$(document).ready(function() {
	$('#documentListArray').chosen();
	var filesizeAllow=$('#fileSizeAllow').val();
	console.log(filesizeAllow);
	var fileTypeAllow=$('#fileTypeAllow').val();
	var allowedFiles ='';
	if(fileTypeAllow!=''){
		allowedFiles=fileTypeAllow.split(",").join("|");
		allowedFiles=allowedFiles.replace(/ /g, '');
		allowedFiles='^.*\.('+allowedFiles+')$';
	}
	if(allowedFiles==''){
		allowedFiles='^.*.(pdf|doc|docx|xls|ppt|jpeg|zip|rar|png|xlsx)$';
	}
    var valid = new RegExp(allowedFiles);
    console.log(valid);
	$('#fileupload').fileupload({
	    done: function (e, data) {
	        $.each(data.files, function (index, file) {
	            var sfilename = file.name;  
		        fileAttach=fileAttach+1;
		        var sid='file'+fileAttach;
		        formdata.append(sid, file);  
		        var markup = "<tr class='template-upload' id='" + sid + "'><td>"+sfilename+"</td><td><a href='#' onclick='deleteFile(\"" + sid + "\",\"" + sfilename +  "\")' title='Delete'><span class='glyphicon glyphicon-remove red'></span></a></td></tr>";  
		        // Binding the file name  
		        $("#filesList tbody").append(markup);  
	        });
	        $('#fileupload').val('');  
	    },
	     fail:function (e, data) {
	    	 var error="Error while uploding";
	    	 if(data.files.error){
		    	 $('.ajax-msg').show();
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
		    	 }
	     },
	     change: function(e,data){
	     },
	    progressall: function (e, data) {
	        var progress = parseInt(data.loaded / data.total * 100, 10);
	        $('.itemcc #progress .progressbar').css(
	            'width',progress + '%'
	        );
	    },
	    messages : {
	    	acceptFileTypes:"File type not allowed, You can upload file types of  "+fileTypeAllow,
	    	maxFileSize:"You can not upload file larger than"+filesizeAllow+"MB",
	    },
	    acceptFileTypes:valid,
	    maxFileSize:filesizeAllow+'M',
	   
	}).on('fileuploadadded', function (e, data) {
        console.log(data.files.valid);
    }).on('fileuploadprocessalways', function (e, data) {
		var error=data.files[0].error;
		if(data.files.error){
    	 $('.ajax-msg').show();
			$.jGrowl(error, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-red'
			});
		}
	    console.log(data.files.error);//true if error
	    console.log(data.files[0].error);//error message
	    if(!data.files.error) data.submit(); //do submission here
	});
	
	
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	var cqName = $('#editCqName').val();
	// $('.cq_form_google_form').hide();
	$('[data-toggle="tooltip"]').tooltip();
	$('.toggleCreateBq, #cqCancel').click(function() {
		$(".form-error").remove();
		$("#name").parent().removeClass("has-error");
		$("#name").removeClass("error");
		$('#name').css("border-color", "");
		$('.toggleCreateBq').parent().next().slideToggle();
		$('.toggleCreateBq').find('i').toggleClass('fa-minus').toggleClass('fa-plus');
		$('#idCreateRftCq').find('#description').val('');
		$('#idCreateRftCq').find('#name').val('');
		$('#idMeetHead').text('Title : ' + cqName);
		$('#cqSave').text(saveLabel);
	});

	jQuery('body').on('change keyup paste', 'input.to_validate', function() {
		if ($(this).val().length) {
			$(this).parent().parent().find('.form-error').addClass("hide");
		} else {
			$(this).parent().parent().find('.form-error').removeClass("hide");
		}
	});

	/* this code use for table drag and drop */
	
	var suspendedEvent=$('#suspendedEvent').val();
	if(suspendedEvent!="true"){
	var ns = $('ol.sortable').nestedSortable({
		forcePlaceholderSize : true,
		handle : 'div',
		helper : 'clone',
		items : 'li',
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
		isAllowed : function(e) {
			return true;
		},
		change : function(e) {
			// console.log((e));
		},
		stop : function(item, currentItem) {

			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess').hide();

			var eventId = $('#eventId').val();
			var cqId = $('#cqId').val();
			var itemId = currentItem.item.attr('data-item');
			var parentId = currentItem.item.attr('data-parent');
			var itemName = currentItem.item.find('span.item_name').html();
			console.log("cqId :" + cqId);
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			console.log('Item Id' + currentItem.item.parents('li').attr('data-item'));
			var newParentId = currentItem.item.parents('li').attr('data-item');
			if (newParentId == undefined) {
				newParentId = '';
			}
			var numberPos = currentItem.item.children('div').find('.number').text();
			var newPos = $('ol.sortable').find('li[data-item="' + itemId + '"]').index() + 1;
			currentItem.item.attr('data-parent', newParentId);

			var data = {};
			data["id"] = itemId;
			data["parent"] = newParentId;
			data["order"] = newPos;
			data["itemName"] = itemName;
			data["cq"] = cqId;
			console.log("Data : " + data);
			$.ajax({
				url : getBuyerContextPath('supplierFormItemOrder'),
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
					console.log(success);
					if (success != undefined) {
						$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
						$('.alert').hide();
						$('div[id=idGlobalSuccess].ajax-msg').show();
					}
					if (info != undefined) {
						$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
						$('.alert').hide();
						$('div[id=idGlobalInfo]').show();
					}
					var table = '';
					$.each(data, function(i, item) {
						console.log(item);
						table += renderGrid(item);
					});
					$('#cqitemList').html(table);
					$('#s1_tender_delete_btn').addClass('disabled');
					$('.custom-checkbox.checksubcheckbox').uniform();
					$('.checker span').find('.glyph-icon.icon-check').remove();
					$('.checker span').append('<i class="glyph-icon icon-check"></i>');
					numberingTable();
					$('#loading').hide();
				},
				error : function(request, textStatus, errorThrown) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('.alert').hide();
					$('div[id=idGlobalError]').show();
					var table = '';
					$.each($.parseJSON(request.responseText), function(i, item) {
						console.log(item);
						table += renderGrid(item);
					});
					$('#cqitemList').html(table);
					$('#s1_tender_delete_btn').addClass('disabled');

					$('.custom-checkbox.checksubcheckbox').uniform();
					$('.checker span').find('.glyph-icon.icon-check').remove();
					$('.checker span').append('<i class="glyph-icon icon-check"></i>');
					numberingTable();
					$('#loading').hide();
				},
				complete : function() {
					// reset main check
					$("#inlineCheckbox115").closest('span').removeClass("checked")
					$("#inlineCheckbox115").prop("checked", false);
					$('#loading').hide();
				}
			});
		}
	});
}

	numberingTable();

	/* edit cq */
	$(document).delegate('.editCq', 'click', function(e) {
		e.preventDefault();
		$(".form-error").remove();
		$("#name").parent().removeClass("has-error");
		$("#name").removeClass("error");
		$('#name').css("border-color", "");
		var eventId = $('#eventId').val();
		// var cqId = $('#editCqId').val();

		// var cqId = $('.editCq').attr('data-id');
		console.log("CqId : " + cqId);
		var cqDesc = $('#editCqDesc').val();
		// $(this).parents('tr').find('td:nth-child(3)').text();
		var cqName = $('#editCqName').val();
		// alert(cqId+"==cqDesc=="+cqDesc+"==cqName=="+cqName);
		// $(this).parents('tr').find('td:nth-child(2)').text();
		// alert(cqId+"======"+cqDesc+"======"+cqName);
		// $('#idCreateRftCq').find('#cqId').val(cqId);
		console.log("Name : " + cqName + " Desc : " + cqDesc);
		$('#idCreateRftCq').find('#description').val(cqDesc);
		$('#idCreateRftCq').find('#name').val(cqName);
		$('.meeting2-heading').find('h3').text('Update Questionnaire');
		$('#cqSave').text('Update');
		$(".cq_form_google_form").find('#itemId').val('');
		$('.toggleCreateBq').parent().next().slideDown();
		$('.toggleCreateBq').find('i').addClass('fa-minus').removeClass('fa-plus');
	});

	/* this code use for table drag and drop */

	$(document).on("click", ".add_question", function() {
		$('.main-New-Question').find('#createCqItemReset').click();
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();

		$('#parentId').val('');
		// $(".main-New-Question").toggle();
		$('#creat_subitem_form').hide();

		$("#creat_seaction_form").hide();

		$(".main-edit-Question").hide();
		$(".main-New-Question").find('#itemId').val('');
		$('#addeditQuestionTable').text('Create a New Question');
		$('#createCqItem').text('Create');
		$("#cqType").val('').trigger("chosen:updated");
		$(".cq_form_google_form").find('#itemId').val('');
		$('.cq_removeOption').click();
		$('.toggle-button').removeClass('toggle-button-selected').next().val('0');
		$('.cq_form_google_form').find('#itemName, #itemDesc, input[type="text"]').val('');
		$('.cq_form_google_form').slideToggle();
		$('.cq_form_google_form').find('#dd').find('.dropdown').find('li:nth-child(2)').click();

		$.uniform.update();
	});

	$(document).on("click", ".add_question_table", function() {
		$('.itemcc #progress .progressbar').css(
	            'width','0%'
	        );		
		formdata=new FormData();
		$("#filesList tbody").html('');
		document.getElementById("supplierAttachRequired").disabled = true;
		$('.main-New-Question').find('#createCqItemReset').click();
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
		$('#parentId').val($(this).parents('.sub_item').attr('data-id'));
		// $(".main-New-Question").show();
		$("#creat_seaction_form").hide();
		$(".main-edit-Question").hide();
		$(".main-New-Question").find('#itemId').val('');
		$('#addeditQuestionTable').text('Create a New Question');
		$('#createCqItem').text(saveLabel);
		$("#cqType").val('').trigger("chosen:updated");

		$('.cq_removeOption').click();
		$('.toggle-button').removeClass('toggle-button-selected').next().val('0');
		$('input[name="isSupplierAttach"]').val('0');
		$('input[name="isRequired"]').val('0');
		$('.cq_form_google_form').find('#itemName, #itemDesc, input[type="text"]').val('');
		// $('.cq_form_google_form').show();
		$('.cq_form_google_form').slideDown();
		$('.cq_form_google_form').find('#dd').find('.dropdown').find('li:nth-child(2)').click();

		$('.custom-checkbox').prop('checked', false).change().uniform();
		$('.custom-checkbox.checksubcheckbox').uniform();
		$('.checker span').find('.glyph-icon.icon-check').remove();
		$('.checker span').append('<i class="glyph-icon icon-check"></i>');

		$("#documentListArray").val('').trigger("chosen:updated");
		$("#documentListArray").removeAttr('data-validation');
		
		$.uniform.update();
	});

	$('#cancelCqItem,#closeCqItemDiv').click(function(e) {
		e.preventDefault();
		$("body").focus();
		$('.cq_form_google_form').slideUp();

		// Wait until the dialog is closed before resetting the form. else the toggle animation is visible.
		setTimeout(function() {
			$("#documentListArray").val('').trigger("chosen:updated");
			$('#parentId').val('');
			$('#itemId').val('');
			// $(".main-New-Question").show();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();
			$(".form-error").remove();
			$("#itemName").removeClass("error");
			$("#itemName").removeAttr("style");
			$("#itemDesc").removeClass("error");
			$("#creat_seaction_form").hide();
			$(".main-edit-Question").hide();
			$(".main-New-Question").find('#itemId').val('');
			$('.toggle-button').removeClass('toggle-button-selected').next().val('0');
			$('.cq_form_google_form').find('#itemName, #itemDesc, input[type="text"]').val('');
			$('.cqr_title3').css({
				'top' : '10px',
				'font-size' : "20px"
			});
			$('.cqr_title2').css({
				'top' : '10px',
				'font-size' : "15px"
			});
			$('#add_question_form1').get(0).reset();
		}, 500);

	});

	$(document).on("click", ".edit_btn_table", function(e) {
		e.preventDefault();
		$('.itemcc #progress .progressbar').css(
	            'width','0%'
	        );		
		formdata=new FormData();
		$("#documentListArray").val('').trigger("chosen:updated");
		$("#documentListArray").removeAttr('data-validation');
		
		$('.toggleButtons').show();
		$('.custom-checkbox').prop('checked', false).change().uniform();
		$('.custom-checkbox.checksubcheckbox').uniform();
		$('.checker span').find('.glyph-icon.icon-check').remove();
		$('.checker span').append('<i class="glyph-icon icon-check"></i>');

		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
		$(".form-error").remove();
		$("#itemName").removeClass("error");
		$("#itemDesc").removeClass("error");
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$('#addeditQuestionTable').text('Update Question');
		$('#createCqItem').text('Update');
		var itemId = $(this).parents('.sub_item').attr('data-id');
		$('#parentId').val($(this).parents('.sub_item').attr('data-parent'));
		$.ajax({
			url : getBuyerContextPath('getFormItemData'),
			data : {
				'itemId' : itemId
			},
			type : "GET",
			contentType : "application/json",
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				setTimeout(function() {
					$('.cq_form_google_form').slideDown();
					$('.cq_form_google_form').show();

				}, 500);
				$('.toggle-button').removeClass('toggle-button-selected').next().val('0');
				$('.cq_form_google_form').find('#itemName, #itemDesc, input[type="text"]').val('');
				$("#creat_seaction_form").hide();

				$.each(data, function(name, value) {

					$(".cq_form_google_form").find('#itemName').val(value.itemName);
					$(".cq_form_google_form").find('#itemName').addClass("valid");
					$(".cq_form_google_form").find('#itemDesc').val(value.itemDescription);
					$(".cq_form_google_form").find('#itemDesc').addClass("valid");

					if (value.optional === true) {
						$(".cq_form_google_form").find('.toggle-button').find('[name="isRequired"]').val('1').parent().addClass('toggle-button-selected');
					} else {
						$(".cq_form_google_form").find('.toggle-button').find('[name="isRequired"]').val('0').parent().removeClass('toggle-button-selected');
					}
					if (value.attachment === true) {
						$(".cq_form_google_form").find('.toggle-button').find('[name="isSupplierAttach"]').val('1').parent().addClass('toggle-button-selected');
					} else {
						$(".cq_form_google_form").find('.toggle-button').find('[name="isSupplierAttach"]').val('0').parent().removeClass('toggle-button-selected');
					}

					if (value.isSupplierAttachRequired === true) {
						$(".cq_form_google_form").find('.toggle-button').find('[name="isSupplierAttachRequired"]').val('1').parent().addClass('toggle-button-selected');
					} else {
						$(".cq_form_google_form").find('.toggle-button').find('[name="isSupplierAttachRequired"]').val('0').parent().removeClass('toggle-button-selected');
					}
					
					var isSupplierAttach= document.getElementById("isSupplierAttach").value;
					if(isSupplierAttach==0){
					document.getElementById("supplierAttachRequired").disabled = true;
					}else{
					document.getElementById("supplierAttachRequired").disabled = false;
					}


					label_txt = $("ul.dropdown").find("[data-cqtype='" + value.cqType + "']").text();

					$(".cq_form_google_form").find('#dd').children('span').text(label_txt);
					var options = '';
					if (value.options != undefined && value.options != null && value.options != '') {

						if (value.optionScore != undefined && value.optionScore != null && value.optionScore != '') {
							options = addOptionsByTypenData(value.cqType, value.options, value.optionScore);
						} else {
							options = addOptionsByTypenData(value.cqType, value.options, null);
						}
						$('.bottom_box.listboxBlock').html(options);
					}
					$('.bottom_box.listboxBlock').html(options);
					reorderOptionsNumber();
					$("ul.dropdown li").removeClass("active_cq");
					$("ul.dropdown").find("[data-cqtype='" + value.cqType + "']").addClass("active_cq");
					
					if (value.cqType == "CHOICE_WITH_SCORE") {
						initSpinner();
					}
					if (value.cqType == "DOCUMENT_DOWNLOAD_LINK") {
						$("#documentListArray").attr('data-validation', 'required');
						
						if (value.options != undefined && value.options != null && value.options != '') {
							$("#documentListArray").val(value.options).trigger("chosen:updated");
						}
						
						$('.bottom_box.listboxBlock').empty();
						$('.toggleButtons').hide();
					}
					var markup = '';
					if(value.formItemAttachments != undefined && value.formItemAttachments != null && value.formItemAttachments !=''){
						$.each(value.formItemAttachments, function(name, value){
							console.log(value.fileName);
							markup+='<tr id=' + value.id + '><td><a href="' + getContextPath() + '/buyer/downloadFormItemAttachment/'+ value.id +'" title="Download">'+value.fileName+'</a></td>';
							markup+='<td><a href="#myModalDocDelete"  onClick="javascript:deleteItemDocFile(\'' + value.id +'\');"  title="Delete" role="button" data-toggle="modal"><span class="glyphicon glyphicon-remove red"></span></a></td></tr>';  
						});
					}
					$(".cq_form_google_form").find("#filesList tbody").html(markup);  
					
				});


				$(".cq_form_google_form").find('#itemId').val(itemId);
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('errors') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

	function deleteBtn() {

		var total = $(".sortable [type=checkbox]:checked").length;
		if (total > 0) {
			$('#s1_tender_delete_btn').removeClass('disabled');
		} else {
			$('#s1_tender_delete_btn').addClass('disabled');
		}
		console.log(total);
	}

	$('#s1_tender_delete_btn').click(function() {
		$('#myModalDeleteCQs').modal('show');
	});

	$('#idConfirmDelete').click(function() {
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$('#myModalDeleteCQs').modal('hide');
		var items = [];
		$('.sortable [type=checkbox]:checked').each(function() {
			items.push($(this).val());
		});
		var formId = $('#formId').val();
		console.log("formId : " + formId);
		$.ajax({
			url : getBuyerContextPath('deleteFormItem') + "/" + formId,
			data : JSON.stringify(items),
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
				var table = '';
				$('#cqitemList').html(table);
				$.each(data, function(i, item) {
					console.log("Object : " + item.level);
					table += renderGrid(item);
				});
				$('#cqitemList').html(table);
				$('.custom-checkbox').prop('checked', false).change().uniform();
				$('.custom-checkbox.checksubcheckbox').uniform();
				$('.checker span').find('.glyph-icon.icon-check').remove();
				$('.checker span').append('<i class="glyph-icon icon-check"></i>');
				console.log('Message : ', request.getResponseHeader('errors'));
				$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('errors'));
				$('.alert').hide();
				$('div[id=idGlobalSuccess]').show();
				$('#loading').hide();
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('errors') != null ? request.getResponseHeader('errors').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

	$('.header_table .custom-checkbox').on('change', function() {
		var check = this.checked;
		$(".sortable [type=checkbox]").each(function() {
			$(".sortable [type=checkbox]").prop('checked', check);
			$.uniform.update($(this));
		});
		deleteBtn();
	});

	$(document).delegate(".sortable [type=checkbox]", "change", function(e) {
		// console.log("breakpoint 2");
		var childBoxeslength = $(this).closest('.menuDiv').next('ol').find('[type=checkbox]').length;
		// alert(childBoxeslength);
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

	// $('#createCqItem').click(function(e) {
	$(document).on("click", "#createCqItem", function(e) {
		e.preventDefault();
		console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>");
		var empty_fields = false;
		$('.to_validate').each(function() {
			if ($(this).val() == "") {
				console.log("empty field");
				empty_fields = true;
				$(this).parent().parent().find('.form-error').removeClass("hide");
			}
		});
		var cqTypeOptns = $(".active_cq").data('cqtype');
		if (cqTypeOptns == 'CHOICE' || cqTypeOptns == 'CHOICE_WITH_SCORE' || cqTypeOptns == 'LIST') {
			console.log("11111111111111111111111111111111");
			$('.addOptionElement').parent().next('.nonfieldserror').remove();
			if ($('.to_validate').length < 2) {
				console.log("222222222222222222222222222222222");
				$('.addOptionElement').parent().after('<span class="font-red form-error nonfieldserror pull-left marg-top-10">Add minimum 2 options</span>');
				return false;
			}
		} else if (cqTypeOptns == 'CHECKBOX') {
			$('.addOptionElement').parent().next('.nonfieldserror').remove();
			if ($('.to_validate').length == 0) {
				$('.addOptionElement').parent().after('<span class="font-red form-error nonfieldserror pull-left marg-top-10">Add minimum 1 option</span>');
				return false;
			}
		}

		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();

		if (!$('#add_question_form1').isValid()) {
			console.log("Form not valid...");
			return false;
		}

		if (empty_fields) {
			console.log("Empty field.....");
			return false;
		}

		var itemName = $(".cq_form_google_form").find('#itemName').val();
		var itemDesc = $(".cq_form_google_form").find('#itemDesc').val();
		var parentId = $(".cq_form_google_form").find('#parentId').val();

		var formId = $(".cq_form_google_form").find('#formId').val();
		var attachment = $('input[name="isSupplierAttach"]').val();

		var isSupplierAttachRequired = $('input[name="isSupplierAttachRequired"]').val();

		var optional = $('input[name="isRequired"]').val();
		var itemId = $(".cq_form_google_form").find('#itemId').val();
		$(".cq_form_google_form").find('#itemId').val("");

		var data = {};
		var cqType = jQuery(".active_cq").data('cqtype');
		var optionsVal = [];
		var optionsKey = [];
		console.log("itemId :" + itemId);
		$('.box_qus_row1.drager_point .score-spinner').each(function() {
			optionsKey.push($(this).val());
		});

		// checking duplicate options
		var tempOptionArr = [];
		var duplicateField = false;
		$('.box_qus_row1.drager_point .cqr_title4a').each(function() {
			optionsVal.push($(this).val());
			console.log(optionsVal);
			for (var i = 0; i < tempOptionArr.length && !duplicateField; i++) {
				if (tempOptionArr[i] === $(this).val().toUpperCase()) {
					$('p[id=idGlobalErrorMessage]').html("\"" + $(this).val() + "\" is already exists");
					$('.alert').hide();
					$('.alert-danger').hide();
					$('div[id=idGlobalError]').show();
					duplicateField = true;
				}
			}
			tempOptionArr.push($(this).val().toUpperCase());
		});

		if (duplicateField) {
			return false;
		}
		if (attachment == 0) {
			attachment = false;
		} else {
			attachment = true;
		}

		if (isSupplierAttachRequired == 0) {
			isSupplierAttachRequired = false;
		} else {
			isSupplierAttachRequired = true;
		}

		if (optional == 0) {
			optional = false;
		} else {
			optional = true;
		}

		if(cqType === 'DOCUMENT_DOWNLOAD_LINK') {
			optionsVal = $("#documentListArray").chosen().val();
		}
		
		var myForm =new FormData();
		//Get uploaded file
		formdata.forEach(function(value, key){
		    myForm.append("file",value);
		});
		
		// var datat
		myForm.append("id",itemId);
		myForm.append("itemName",itemName);
		myForm.append("itemDescription", itemDesc);
		myForm.append("supplierForm", formId);
		myForm.append("attachment", attachment);
		myForm.append("isSupplierAttachRequired",isSupplierAttachRequired);
		myForm.append("optional", optional);
		myForm.append("parent",parentId);
		myForm.append("cqType", cqType);
		myForm.append("options", optionsVal);
		myForm.append("optionScore", optionsKey);
		console.log("optionsKey :" + optionsKey + " optionsVal :" + optionsVal);
		$(".bottom_box .form-error").addClass("hide ");

		var ajaxUrl = getBuyerContextPath('createFormItem');
		if ($.trim(itemId) != '') {
			var ajaxUrl = getBuyerContextPath('updateFormItem');
		}
		$.ajax({
			url : ajaxUrl,
			data : myForm,
			type : "POST",
			enctype : 'multipart/form-data',
			processData : false,
			contentType : false,
			cache:false,
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var success = request.getResponseHeader('success');
				var info = request.getResponseHeader('info');
				// $(".cq_form_google_form").hide();
				$(".cq_form_google_form").find('input[type="text"], textarea').val('');
				$('div[id=idGlobalError]').hide();
				$('div[id=idGlobalSuccess]').hide();
				$('p[id=idGlobalSuccessMessage]').html("");
				$('p[id=idGlobalErrorMessage]').html("");
				if (success != undefined) {
					$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('div[id=idGlobalSuccess].ajax-msg').show();
					$(".cq_form_google_form").hide();
				}
				if (info != undefined) {
					$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
					$('.alert').hide();
					$('div[id=idGlobalInfo]').show();
				}
				var table = '';

				$.each(data, function(i, item) {
					console.log(item);
					table += renderGrid(item);
				});
				// console.log(table);
				$('#cqitemList').html(table);
				$('.custom-checkbox.checksubcheckbox').uniform();
				$('.checker span').find('.glyph-icon.icon-check').remove();
				$('.checker span').append('<i class="glyph-icon icon-check"></i>');
				$(".cq_form_google_form").find('#itemId').val('');
				numberingTable();
				/*
				 * setTimeout(function() { $('.alert').hide(); }, 2000);
				 */
				/*
				 * $('.main-New-Question').find('#createCqItemReset').click(); $('.main-New-Question').find('[type="radio"],
				 * [type="checkbox"]').uniform(); $('#cqType').trigger("chosen:updated");
				 */

				$(".cq_form_google_form").find('.toggle-button').find('[name="isSupplierAttachRequired"]').val('0').parent().removeClass('toggle-button-selected');
				document.getElementById("supplierAttachRequired").disabled = true;

				$('#loading').hide();
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$(".cq_form_google_form").hide();
				$('#loading').hide();
			},
		});
	});
});

function renderGrid(item) {
	var table = '';
	table += '<li style="display: list-item;" class="mjs-nestedSortable-leaf" id="menuItem_1" data-item="' + item.id + '" data-parent=""><div class="menuDiv">';
	table += '<table class="table data ph_table diagnosis_list sorted_table">' + '<tr class="sub_item" data-id="' + item.id + '">' + '<td class="width_50 width_50_fix align-left"><a href="#">'
			+ '<i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i></a></td>' + '<td class="width_50 width_50_fix align-left"><div class="checkbox checkbox-info">';
		table += '<label> <input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="' + item.id + '"></label>';
	table += '</div></td>' + '<td class="width_50 width_50_fix align-left"><span class="number1">' + item.level + '.' + item.order + '</span></td>' + '<td class="width_200 width_200_fix align-left">' + '<span class="item_name">' + item.itemName + '</span>';
	if (item.itemDescription != undefined || item.itemDescription == "") {
		table += '<span class="item_detail s1_view_desc">View Description</span>';
		table += '<p class="s1_tent_tb_description s1_text_small">' + item.itemDescription + '</p>';
	} else {
		table += '<p class="s1_tent_tb_description s1_text_small">&nbsp</p>';
	}
	table += '<td class="width_200 width_200_fix align-left">' + '<button class="btn btn-info ph_btn_extra_small hvr-pop hvr-rectangle-out add_question_table">Add Question</button></td>'
			+ '<td class="width_200 width_200_fix align-left"><a title="" class="btn btn-sm btn-black ph_btn_small color-white hvr-pop hvr-rectangle-out1 edit_section_btn_table" href="#">Edit</a></td></tr></table>'
	// }
	table += '</div>';
	if (item.children != undefined && item.children != null) {
		table += '<ol>'
		$.each(item.children, function(i, child) {
			table += '<li style="display: list-item;" class="mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="menuItem_2_1" data-foo="baz" data-item="' + child.id + '" data-parent="' + item.id + '">'
			table += '<div class="menuDiv sub-color-change">' + '<table class="table data ph_table diagnosis_list sorted_table">' + '<tr class="sub_item" data-id="' + child.id + '" data-parent="' + item.id + '">'
			table += '<td  class="width_50 width_50_fix align-left"><a href="#"><i aria-hidden="true" class="glyph-icon icon-arrows move_icon"></i></a></td>' + '<td  class="width_50 width_50_fix align-left">'
			table += '<div class="checkbox checkbox-info"><label>';
			table += '<input type="checkbox" id="inlineCheckbox116" class="custom-checkbox checksubcheckbox" value="' + child.id + '">';
			table += '</label></div>' + '</td><td  class="width_50 width_50_fix align-left"><span class="number">' + child.level + '.' + child.order + '</span></td>' + '<td  class="width_200 width_200_fix align-left">' + '<span class="item_name">'+ child.itemName + '</span> ';
			if (child.itemDescription != undefined || child.itemDescription == "") {
				table += '<span class="item_detail s1_view_desc">View Description</span>';
				table += '<p class="s1_tent_tb_description s1_text_small">' + child.itemDescription + '</p>';
			} else {
				table += '<p class="s1_tent_tb_description s1_text_small">&nbsp</p>';
			}
			table += '</td><td  class="width_200 width_200_fix align-left"></td>' + '<td  class="width_200 width_200_fix align-left">'
					+ '<a title="" class="btn btn-sm edit-btn-table ph_btn_small color-white hvr-pop hvr-rectangle-out1 edit_btn_table">Edit</a>' + '</td></tr></table></div></li>';
		});
		table += '</ol>'
	}
	table += '</li>';
	return table;
}

function numberingTable() {
	$(".sortable > li").each(function(index) {

		var ind = parseInt(index + 1);
		var par = $(this).find('ol').length;
		$(this).find('.number').html(ind);

		if (par > 0) {
			$(this).find('.menuDiv ').removeClass("sub-color-change");
			var editbtn = '<a class="btn btn-sm edit-btn-table ph_btn_small color-white hvr-pop hvr-rectangle-out1 edit_sections_btn_table" title="">Edit</a>';
			$(this).find('table tr td:nth-child(6)').html(editbtn);
			// $(this).find('table .item_detail').hide();

			$(this).find('ol li').each(function(index2) {
				$(this).find('.menuDiv ').addClass("sub-color-change");
				// $(this).find('table tr
				// td:nth-child(5)').html('text');
				$(this).find('.number').html(parseInt(ind) + "." + parseInt(index2 + 1));
				var htm = '<a class="btn btn-sm edit-btn-table ph_btn_small color-white hvr-pop hvr-rectangle-out1 edit_btn_table" title="">Edit</a>';
				$(this).find('table tr td:nth-child(6)').html(htm);
				// $(this).find('table
				// .item_detail').show();
			});
		} else {
			$(this).find('.menuDiv ').removeClass("sub-color-change");
			// $(this).find('table .item_detail').hide();
			var htm = '<button class="btn btn-info ph_btn_extra_small hvr-pop hvr-rectangle-out add_question_table">Add Question</button>';
			$(this).find('table tr td:nth-child(5)').html(htm);
			var editbtn = '<a class="btn btn-sm edit-btn-table ph_btn_small color-white hvr-pop hvr-rectangle-out1 edit_section_btn_table" title="">Edit</a>';

			$(this).find('table tr td:nth-child(6)').html(editbtn);

		}
	});
}

$('.left_button').on('click', '#addsection_btn', function(event) {
	event.preventDefault();
	$(".form-error").remove();
	$("#sectionName").parent().removeClass("has-error");
	$("#sectionName").removeClass("error");
	$('#sectionName').css("border-color", "");
	$("#add_question_form").hide();
	$("#creat_seaction_form").find('#itemId').val('');
	$('#creat_seaction_form').find("input[type='text'],textarea").val('');
	$('#creat_seaction_form').find('h3.s1_box_title').text(createNewSectionLabel);
	$('#creat_seaction_form').show();
	$('#saveCqSection').text(saveLabel);

	$('.custom-checkbox').prop('checked', false).change().uniform();
	$('.custom-checkbox.checksubcheckbox').uniform();
	$('.checker span').find('.glyph-icon.icon-check').remove();
	$('.checker span').append('<i class="glyph-icon icon-check"></i>');
	$('.itemcc #progress .progressbar').css(
            'width','0%'
        );		
	formdata=new FormData();
});

$(function() {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$('#saveCqSection').click(function(e) {
		e.preventDefault();
		$('div[id=idGlobalError]').hide();
		$('div[id=idGlobalSuccess]').hide();
		if (!$('#addSectionForm').isValid()) {
			return false;
		}
		{
			var itemName = $("#sectionName").val();
			var itemDesc = $("#sectionDescription").val();
			var formId =$('#formId').val();
			var itemId = $("#creat_seaction_form").find('#itemId').val();
			var data = {};

			var myForm=new FormData();
			myForm.append("id", itemId);
			myForm.append("itemName",itemName);
			myForm.append("itemDescription", itemDesc);
			myForm.append("supplierForm", formId);
			var ajaxUrl = getBuyerContextPath('createFormItem');
			if ($.trim(itemId) != '') {
				var ajaxUrl = getBuyerContextPath('updateFormItem');
			}
			$.ajax({
				url : ajaxUrl,
				data : myForm,
				type : "POST",
				enctype : 'multipart/form-data',
				processData : false,
				contentType : false,
				cache:false,
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#idMessageJsp').html('');
					$('#loading').show();
				},
				success : function(data, textStatus, request) {

					var success = request.getResponseHeader('success');
					var info = request.getResponseHeader('info');
					$("#creat_seaction_form").hide();
					$("#creat_seaction_form").find('input[type="text"], textarea').val('');
					if (success != undefined) {
						$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
						$('.alert').hide();
						$('div[id=idGlobalSuccess].ajax-msg').show();
					}
					if (info != undefined) {
						$('p[id=idGlobalInfoMessage]').html(info != null ? info.split(",").join("<br/>") : "");
						$('.alert').hide();
						$('div[id=idGlobalInfo]').show();
					}
					var table = '';

					$.each(data, function(i, item) {
						console.log(item);
						table += renderGrid(item);
					});
					$('#cqitemList').html(table);
					$('.custom-checkbox.checksubcheckbox').uniform();
					$('.checker span').find('.glyph-icon.icon-check').remove();
					$('.checker span').append('<i class="glyph-icon icon-check"></i>');
					$(".creat_seaction_form").find('#itemId').val('');
					// numberingTable();

					$(".creat_seaction_form").hide();

					$('#loading').hide();
				},
				error : function(request, textStatus, errorThrown) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
					$('.alert').hide();
					$('div[id=idGlobalError]').show();
					$('#loading').hide();
				},
			});

		}
	});
});

$(document).on("click", ".edit_sections_btn_table", function() {
	$('.itemcc #progress .progressbar').css(
            'width','0%'
        );		
	formdata=new FormData();
	$(".form-error").remove();
	$("#sectionName").parent().removeClass("has-error");
	$("#sectionName").removeClass("error");
	$('#sectionName').css("border-color", "");
	$('.custom-checkbox').prop('checked', false).change().uniform();
	$('.custom-checkbox.checksubcheckbox').uniform();
	$('.checker span').find('.glyph-icon.icon-check').remove();
	$('.checker span').append('<i class="glyph-icon icon-check"></i>');
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$('#saveCqSection').text('Update');
	var itemId = $(this).parents('.sub_item').attr('data-id');
	$.ajax({
		url : getBuyerContextPath('getFormItemData'),
		data : {
			'itemId' : itemId
		},
		type : "GET",
		contentType : "application/json",
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
			$('#loading').show();

		},
		success : function(data, textStatus, request) {
			$('.creat_seaction_form').find('#itemName, #itemDesc, input[type="text"]').val('');
			$('.cq_form_google_form').hide();
			$("#creat_seaction_form").show();

			$.each(data, function(name, value) {
				console.log(" ItemName : " + value.itemName + " Desc : " + value.itemDescription);
				$("#creat_seaction_form").find("#sectionDescription").val(value.itemDescription);
				$("#creat_seaction_form").find("#sectionName").val(value.itemName);
				$("#creat_seaction_form").find('#itemId').val(itemId);
			});

		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('errors') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

$(document).on("click", ".edit_section_btn_table", function() {
	$('.itemcc #progress .progressbar').css(
            'width','0%'
        );		
	formdata=new FormData();
	$(".form-error").remove();
	$("#sectionName").parent().removeClass("has-error");
	$("#sectionName").removeClass("error");
	$('#sectionName').css("border-color", "");
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$('#saveCqSection').text('Update');
	var itemId = $(this).parents('.sub_item').attr('data-id');
	$.ajax({
		url : getBuyerContextPath('getFormItemData'),
		data : {
			'itemId' : itemId
		},
		type : "GET",
		contentType : "application/json",
		dataType : 'json',
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
			$('#loading').show();

		},
		success : function(data, textStatus, request) {
			$('.creat_seaction_form').find('#itemName, #itemDesc, input[type="text"]').val('');
			$('.cq_form_google_form').hide();
			$("#creat_seaction_form").show();

			$.each(data, function(name, value) {
				console.log(" ItemName : " + value.itemName + " Desc : " + value.itemDescription);
				$("#creat_seaction_form").find("#sectionDescription").val(value.itemDescription);
				$("#creat_seaction_form").find("#sectionName").val(value.itemName);
				$("#creat_seaction_form").find('#itemId').val(itemId);
			});

		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('errors') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
});

$('#cancelCqSection').click(function(e) {
	e.preventDefault();
	$("#addSectionForm").get(0).reset();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	$('.cq_form_google_form').hide();
	// $("#creat_seaction_form").find('#sectionName, #sectionDescription,
	// input[type="text"]').val('');
	$("#creat_seaction_form").slideUp();
});

$('#downloadTemplate').click(function(e) {
	e.preventDefault();
	var formId = $('#formId').val();
	window.location.href = getBuyerContextPath('formItemTemplate') + "/" + formId;
})

$('#uploadCqItems').click(function(e) {
	$('#uploadCqItemFile').trigger("click");

});

$("#uploadCqItemFile").on("change", function() {
	if ($(this).val() == "") {
		return;
	}

	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();

	if ($('#uploadCqItemFile').val().length == 0) {
		$('p[id=idGlobalErrorMessage]').html("Please select upload file.");
		$('div[id=idGlobalError]').show();
		return;
	}

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	var formId = $('#formId').val();
	var myForm = new FormData();
	myForm.append("file", $('#uploadCqItemFile')[0].files[0]);
	myForm.append("formId", formId);
	$.ajax({
		url : getBuyerContextPath('uploadFormItems'),
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
			var warn = request.getResponseHeader('warn');
			if (success != undefined) {
				$('p[id=idGlobalSuccessMessage]').html(success != null ? success.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalSuccess].ajax-msg').show();
			}
			if (warn != undefined) {
				$('p[id=idGlobalWarnMessage]').html(warn != null ? warn.split(",").join("<br/>") : "");
				$('.alert').hide();
				$('div[id=idGlobalWarn]').show();
			}

			var table = '';

			$.each(data, function(i, item) {
				table += renderGrid(item);
			});
			$('#cqitemList').html(table);
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
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
		},
		complete : function() {
			$('#loading').hide();
		}
	});
	$(this).val("");
});

$('#uploadCqItemsFile').click(function(e) {
	e.preventDefault();

});
function deleteFile(Fileid, FileName) {  
	 console.log("deleting file");
      formdata['delete'](Fileid);  
      $("#filesList tbody").find("#"+Fileid).remove();  
      $("#itemAttachFiles").val(formdata);
  }  

//delete form 
$('#idItemdocConfirmDelete').click(function(e) {
			console.log(' form delete called ');
			e.preventDefault();
			$('div[id=idGlobalError]').hide();
			$('div[id=idGlobalSuccess]').hide();
			//idGlobalSuccess
			$('.text-danger').html('');
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var documId=$(this).attr("data-id");
			var ajaxUrl = getBuyerContextPath('deleteFormItemDocument') ;
			$.ajax({
				url : ajaxUrl,
				data :{
					'docId' : documId
				},
				type : "POST",
				beforeSend : function(xhr) {
					$('#loading').show();
					xhr.setRequestHeader(header, token);
				},
				success : function(data, textStatus, request) {
					var info = request.getResponseHeader('success');
					console.log(" *** Success message : ***" + info);
					$(".show_name").html('');
					$('p[id=idGlobalSuccessMessage]').html(info);
					  $("#filesList tbody").find("#"+documId).remove();  
					$('.ajax-msg').show();
					$.jGrowl(info, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					});
					$('#loading').hide();
					$('#myModalDocDelete').hide();
					$('body').removeClass('modal-open');


					
				},
				error : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					console.log("error");
					$('p[id=idGlobalErrorMessage]').html(error);
					$('.ajax-msg').show();
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
					$('#loading').hide();
					$('#myModalDocDelete').hide();
					$('body').removeClass('modal-open');

				},
				complete : function() {
					$('#loading').hide();
					$('#myModalDocDelete').hide();
				}
			});
		});
  