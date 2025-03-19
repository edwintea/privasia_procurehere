$(document).on('click', '.toggle-button', function() {
	$(this).toggleClass('toggle-button-selected');
	$(this).find('input').val(Math.abs($(this).find('input').val() - 1));
});

function DropDown(el) {
	this.dd = el;
	this.placeholder = this.dd.children('span');
	this.opts = this.dd.find('ul.dropdown > li');
	this.val = '';
	this.index = -1;
	this.initEvents();
}
DropDown.prototype = {
	initEvents : function() {
		var obj = this;

		obj.dd.on('click', function(event) {
			$(this).toggleClass('active');
			return false;
		});

		obj.opts.on('click', function() {
			var opt = $(this);
			jQuery('ul.dropdown > li').removeClass('active_cq');
			console.log(opt);
			objType = opt.data('cqtype');
			opt.addClass('active_cq');
			// console.log(objType);
			// console.log(opt.text());
			obj.val = $.trim(opt.text());
			obj.index = opt.index();
			obj.placeholder.text(obj.val);
			// alert(objType);
			changeOptionsAccording(objType);
		});
	},

	getValue : function() {
		return this.val;
	},
	getIndex : function() {
		return this.index;
	}
}
var documentListArray=[];
$(function() {
	var dd = new DropDown($('#dd'));

	$(document).click(function() {
		// all dropdowns
		$('.wrapper-dropdown-3').removeClass('active');
	});
});

$(document).on('click', '.addOptionElement', function(e) {
	e.preventDefault();
	$('.nonfieldserror').remove();
	var limit = 30;
	
//	 && window.location.href.indexOf('sourcingCqItemList') != -1
	if (jQuery(".active_cq").data('cqtype') == 'LIST' && window.location.href.indexOf('sourcingCqItemList') != -1){
		limit =100;
	}
	
	if ($('.width100.pull-left.maxhgt400 > div.box_qus_row1.drager_point').length < limit) {
		html = addoptionBlock(jQuery(".active_cq").data('cqtype'), 'Option');
		console.log(jQuery(".active_cq").data('cqtype'));
		// $('.box_qus_row1.drager_point:not(.otherBlock)').last().after(html);
		$('.width100.pull-left.maxhgt400').append(html);
	} else {
		$('.addOptionElement').parent().after('<span class="font-red form-error nonfieldserror pull-left marg-top-10">Maximum ' + limit + ' options</span>');
	}
	reorderOptionsNumber();
	$(".maxhgt400").animate({ scrollTop: $(".maxhgt400")[0].scrollHeight}, 10);	
});
$(document).on('click', '.addOtherElement', function(e) {
	e.preventDefault();
	html = addoTherBlock(jQuery(".active_cq").data('cqtype'), 'Other');
	console.log(jQuery(".active_cq").data('cqtype'));
	$('.box_qus_row1.drager_point:not(.otherBlock)').last().after(html);
	$('.addOthers').hide();
	reorderOptionsNumber();
});
$(document).on('click', '.cq_removeOption', function(e) {
	e.preventDefault();
	if ($(this).parents('.box_qus_row1.drager_point').hasClass('otherBlock')) {
		$('.addOthers').show();
	}
	$(this).parents('.box_qus_row1.drager_point').remove();
	reorderOptionsNumber();
});

function selectedOptionsWithVal(blockType, item, i, extraOne) {
	$("#documentListArray").removeAttr('data-validation');

	switch (blockType) {
	case "TEXT":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="short_answer">' + item + '</div></div>';
		return html;
		break;
	case "CHOICE":
		var html = "";
		if (i == 0 || i == 1) { // required
			html += '<div class="box_qus_row1 drager_point">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>' + '<div class="box_qus_area"><div class="group">'
					+ '<input type="text" value="' + item + '" required="" class="to_validate cqr_title4a">' + '<span class="highlight"></span><span class="bar"></span>'
					+ '<label class="cqr_title4"></label></div></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>';
		} else {
			html += '<div class="box_qus_row1 drager_point">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>' + '<div class="box_qus_area"><div class="group"> '
					+ '<input type="text" class=" to_validate cqr_title4a" value="' + item + '">' + '</div></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>';

		}
		$("#eventDocumentList").hide();
		return html;
		break;

	case "CHOICE_WITH_SCORE":

		var html = '';
		if (i == 0) { // Required Fields [no cross arrow] [label]
			html = '<div class="box_qus_row1 drager_point">' + '<div class="col-md-2"><strong>Option Score</strong></div><div class="col-md-10"><strong>Option value </strong></div><div class="col-md-2"><input placeholder="score" required="" value="'
					+ extraOne + '" class="score-spinner" name="value"></div><div class="col-md-10"><div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>'
					+ '<div class="box_qus_area"><div class="group">' + '<input type="text" value="' + item + '" class="to_validate cqr_title4a">'
					+ '</div><span class="font-red hide form-error">This is required field  </span></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div></div>'
		} else if (i == 1) { // Required Fields [no cross arrow]
			html = '<div class="box_qus_row1 drager_point">' + '<div class="col-md-2"><input placeholder="score" value="' + extraOne + '"  class="score-spinner" name="value"></div><div class="col-md-10"><div class="box_qus_radio"><img src="'
					+ getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>' + '<div class="box_qus_area"><div class="group">' + '<input type="text" value="' + item + '" class="to_validate cqr_title4a">'
					+ '</div><span class="font-red hide form-error">This is required field </span></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div></div>'

		} else {
			var html = '<div class="box_qus_row1 drager_point">' + '<div class="col-md-2"><input  value="' + extraOne + '" class="score-spinner" name="value"></div><div class="col-md-10"><div class="box_qus_radio"><img src="' + getContextPath()
					+ '/resources/assets/images/Radio_Button_blank.png"></div>' + '<div class="box_qus_area"><div class="group">' + '<input type="text"  value="' + item + '"  class="to_validate cqr_title4a" >'
					+ '</div></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div></div>';

		}
		$("#eventDocumentList").hide();
		return html;

	case "CHECKBOX":
		var html = "";
		if (i == 0) { // required no cross
			html += '<div class="box_qus_row1 drager_point">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Blank_checbox.png"></div>' + '<div class="box_qus_area"><div class="group">'
					+ '<input type="text" value="' + item + '" class="to_validate cqr_title4a">' + '</div><span class="font-red hide form-error">This is required field </span></div><div class="cq_removeOption"><img src="' + getContextPath()
					+ '/resources/assets/images/cqform_cross.png"></div></div>'

		} else {
			html += '<div class="box_qus_row1 drager_point"><div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Blank_checbox.png"></div>' + '<div class="box_qus_area"><div class="group">'
					+ '<input type="text" value="' + item + '" class="to_validate cqr_title4a" >' + '</div></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>';
		}
		$("#eventDocumentList").hide();
		return html;
		break;
	case "LIST":
		var html = "";
		if (i == 0) { // required no cross
			html += '<div class="box_qus_row1 drager_point">' + '<div class="box_qus_number">1.</div>' + '<div class="box_qus_area"><div class="group">' + '<input type="text" value="' + item + '"  class="to_validate cqr_title4a">'
					+ '</div><span class="font-red hide form-error">Att least one option required </span></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>'

		} else if (i == 1) { // required no cross
			html += '<div class="box_qus_row1 drager_point">' + '<div class="box_qus_number">2.</div>' + '<div class="box_qus_area"><div class="group">' + '<input type="text" value="' + item + '"  class="to_validate cqr_title4a">'
					+ '</div><span class="font-red hide form-error">Att least one option required </span></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>'

		} else {
			html += '<div class="box_qus_row1 drager_point"><div class="box_qus_number">1.</div>' + '<div class="box_qus_area"><div class="group">' + '<input type="text" value="' + item + '" class="to_validate cqr_title4a" >'
					+ '</div></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>';

		}
		$("#eventDocumentList").hide();
		return html;
		break;

	case "DATE":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="short_answer">' + item + '</div></div>';
		$("#eventDocumentList").hide();
		return html;
		break;

	case "NUMBER":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="short_answer">' + item + '</div></div>';
		$("#eventDocumentList").hide();
		return html;
		break;

	case "PARAGRAPH":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="short_answer">' + item + '</div></div>';
		$("#eventDocumentList").hide();
		return html;
		break;

	case "DOCUMENT_DOWNLOAD_LINK":
		var html = "";
		$("#eventDocumentList").show();
		$("#documentListArray").attr('data-validation', 'required');
		return html;
		break;
	}
}

function addOptionsByTypenData(blockType, data, extra) {
	var html = '';
	
	if(blockType !== 'DOCUMENT_DOWNLOAD_LINK') {
		html = '<div class="width100 pull-left maxhgt400">';
	}
	
	$.each(data, function(i, item) {
		if (blockType == "CHOICE_WITH_SCORE")
			html += selectedOptionsWithVal(blockType, item, i, extra[i]);
		else
			html += selectedOptionsWithVal(blockType, item, i, null);
	});
	if(blockType !== 'DOCUMENT_DOWNLOAD_LINK') {
		html += '</div>';
	}

	if (blockType == 'CHOICE') {
		html += '<div class="box_qus_row2">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>' + '<div class="box_qus_area2">'
				+ '<a href="#" class="addOptionElement">Add option</a></div></div>';
	}
	if (blockType == 'CHOICE_WITH_SCORE') {
		html += '<div class="box_qus_row2">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div><div class="box_qus_area2">'
				+ '<a href="#" class="addOptionElement">Add option</a></div></div>';
	}
	if (blockType == 'CHECKBOX') {
		html += '<div class="box_qus_row2">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Blank_checbox.png"></div><div class="box_qus_area2">'
				+ '<a href="#" class="addOptionElement">Add option</a></div></div>';
	}

	if (blockType == 'LIST') {
		html += '<div class="box_qus_row2">' + '<div class="box_qus_number"></div>' + '<div class="box_qus_area2">' + '<a href="#" class="addOptionElement">Add option</a></div></div>';
	}
	
	return html;
}

function updateOptions(blockType) {
	$("#documentListArray").removeAttr('data-validation');
	console.log(blockType);
	$('.toggleButtons').show();
	switch (blockType) {
	case "TEXT":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="short_answer">Text</div></div>';
		$("#eventDocumentList").hide();
		return html;
		break;
	case "CHOICE":
		var html = '<div class="width100 pull-left maxhgt400"><div class="box_qus_row1 drager_point">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>'
				+ '<div class="box_qus_area"><div class="group">' + '<input type="text" placeholder="Option 1"  class="to_validate  cqr_title4a" data-validation="length" data-validation-length="0-128" >'
				+ '</div><span class="font-red hide form-error">This is required field </span></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>'
				+ '<div class="box_qus_row1 drager_point">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>' + '<div class="box_qus_area"><div class="group">'
				+ '<input type="text" placeholder="Option 2" required="" class="to_validate cqr_title4a" data-validation="length" data-validation-length="0-128">'
				+ '</div><span class="font-red hide form-error">This is required field </span></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div></div>'
				+ '<div class="box_qus_row2">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>' + '<div class="box_qus_area2">'
				+ '<a href="#" class="addOptionElement">Add option</a></div></div>';
		$("#eventDocumentList").hide();
		return html;
		break;
	case "CHOICE_WITH_SCORE":
		var html = '<div class="width100 pull-left maxhgt400"><div class="box_qus_row1 drager_point">'
				+ '<div class="col-md-2"><strong>Option Score</strong></div><div class="col-md-10"><strong>Option value </strong></div><div class="col-md-2"><input placeholder="score" required="" value="0" class="score-spinner" name="value"></div><div class="col-md-10"><div class="box_qus_radio"><img src="'
				+ getContextPath()
				+ '/resources/assets/images/Radio_Button_blank.png"></div>'
				+ '<div class="box_qus_area"><div class="group">'
				+ '<input type="text" placeholder="Option 1" class="to_validate cqr_title4a">'
				+ '</div><span class="font-red hide form-error">This is required field  </span></div><div class="cq_removeOption"><img src="'
				+ getContextPath()
				+ '/resources/assets/images/cqform_cross.png"></div></div></div>'
				+ '<div class="box_qus_row1 drager_point">'
				+ '<div class="col-md-2"><strong>Option Score</strong></div><div class="col-md-10"><strong>Option value </strong></div><div class="col-md-2"><input placeholder="score" value="0"  class="score-spinner" name="value"></div><div class="col-md-10"><div class="box_qus_radio"><img src="'
				+ getContextPath()
				+ '/resources/assets/images/Radio_Button_blank.png"></div>'
				+ '<div class="box_qus_area"><div class="group">'
				+ '<input type="text" placeholder="Option 2" required="" class="to_validate cqr_title4a">'
				+ '</div><span class="font-red hide form-error">This is required field </span></div><div class="cq_removeOption"><img src="'
				+ getContextPath()
				+ '/resources/assets/images/cqform_cross.png"></div></div></div></div>'
				+ '<div class="box_qus_row2">'
				+ '<div class="box_qus_radio"><img src="'
				+ getContextPath()
				+ '/resources/assets/images/Radio_Button_blank.png"></div>'
				+ '<div class="box_qus_area2">'
				+ '<a href="#" class="addOptionElement">Add option</a></div></div>';
		$("#eventDocumentList").hide();
		return html;
		break;
	case "CHECKBOX":
		var html = '<div class="width100 pull-left maxhgt400"><div class="box_qus_row1 drager_point">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Blank_checbox.png"></div>'
				+ '<div class="box_qus_area"><div class="group">' + '<input type="text" placeholder="Option 1" class="to_validate cqr_title4a">'
				+ '</div><span class="font-red hide form-error">This is required field </span></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div></div>'
				+ '<div class="box_qus_row2">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Blank_checbox.png"></div><div class="box_qus_area2">'
				+ '<a href="#" class="addOptionElement">Add option</a></div></div>';
		$("#eventDocumentList").hide();
		return html;
		break;
	case "LIST":
		var html = '<div class="width100 pull-left maxhgt400"><div class="box_qus_row1 drager_point">' + '<div class="box_qus_number">1.</div>' + '<div class="box_qus_area"><div class="group">'
				+ '<input type="text" placeholder="Option 1"  class="to_validate  cqr_title4a">' + '</div><span class="font-red hide form-error">This is required field </span></div><div class="cq_removeOption"><img src="' + getContextPath()
				+ '/resources/assets/images/cqform_cross.png"></div></div>' + '<div class="box_qus_row1 drager_point">' + '<div class="box_qus_number">2.</div>' + '<div class="box_qus_area"><div class="group">'
				+ '<input type="text" placeholder="Option 2" required="" class="to_validate cqr_title4a">' + '</div><span class="font-red hide form-error">This is required field </span></div><div class="cq_removeOption"><img src="'
				+ getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div></div>' + '<div class="box_qus_row2">'
				 + '<div class="qus_number"></div>' 
				 + '<div class="box_qus_area2">'
				+ '<a href="#" class="addOptionElement">Add option</a></div></div>';
		$("#eventDocumentList").hide();
		return html;
		break;
	case "DATE":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="short_answer">Date</div></div>';
		$("#eventDocumentList").hide();
		return html;
		break;
	case "NUMBER":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="short_answer">Number</div></div>';
		$("#eventDocumentList").hide();
		return html;
		break;
	case "PARAGRAPH":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="short_answer">Paragraph</div></div>';
		$("#eventDocumentList").hide();
		return html;
		break;
	case "DOCUMENT_DOWNLOAD_LINK":
		$("#eventDocumentList").show();
		$('.toggleButtons').hide();
		$("#documentListArray").attr('data-validation', 'required');
		break;
	}

}

function changeOptionsAccording(blockType) {
	console.log("changeOptionsAccording");
	var html = updateOptions(blockType);
	$('.bottom_box.listboxBlock').empty();
	if (blockType != "DOCUMENT_DOWNLOAD_LINK") {
		$('.bottom_box.listboxBlock').html(html);
	}
	initSpinner();
}

function addoTherBlock(blockType, optType) {
	console.log("addoTherBlock");
	switch (blockType) {
	case "CHOICE":
		var html = '<div class="box_qus_row1 drager_point otherBlock">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>' + '<div class="box_qus_area"><div class="group"> '
				+ '<input type="text"  readonly value="' + optType + '" required="" class="cqr_title4a">' + '<span class="highlight"></span><span class="bar"></span>'
				+ '<label class="cqr_title4"></label></div></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>';
		return html;
		break;
	case "CHECKBOX":
		var html = '<div class="box_qus_row1 drager_point otherBlock"><div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Blank_checbox.png"></div>' + '<div class="box_qus_area"><div class="group">'
				+ '<input type="text" readonly value="' + optType + '" required="" class="cqr_title4a">' + '<span class="highlight"></span><span class="bar"></span>'
				+ '<label class="cqr_title4"></label></div></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>';
		return html;
		break;
	case "LIST":
		var html = '<div class="box_qus_row1 drager_point otherBlock"><div class="box_qus_number">1.</div>' + '<div class="box_qus_area"><div class="group">' + '<input type="text" readonly value="' + optType
				+ '" required="" class="cqr_title4a"><span class="highlight"></span><span class="bar"></span>' + '<label class="cqr_title4"></label></div></div><div class="cq_removeOption"><img src="' + getContextPath()
				+ '/resources/assets/images/cqform_cross.png"></div></div>';
		return html;
		break;

	}
}
function addoptionBlock(blockType, optType) {
	console.log("addoptionBlock");
	switch (blockType) {
	case "TEXT":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>' + '<div class="box_qus_area"><div class="group"> '
				+ '<input type="text" class="cqr_title4a" placeholder="' + optType + '">' + '</div></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>';
		return html;
	case "CHOICE":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>' + '<div class="box_qus_area"><div class="group">'
				+ '<input type="text" placeholder="Option"  class="to_validate  cqr_title4a" data-validation="length" data-validation-length="0-128" >'
				+ '</div><span class="font-red hide form-error">This is required field </span></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>';
		return html;
		break;
	case "CHOICE_WITH_SCORE":
		var html = '<div class="box_qus_row1 drager_point">'
				+ '<div class="col-md-2"><strong>Option Score</strong></div><div class="col-md-10"><strong>Option value </strong></div><div class="col-md-2"><input placeholder="score" required="" value="0" class="score-spinner" name="value"></div><div class="col-md-10"><div class="box_qus_radio"><img src="'
				+ getContextPath() + '/resources/assets/images/Radio_Button_blank.png"></div>' + '<div class="box_qus_area"><div class="group">' + '<input type="text" placeholder="Option" class="to_validate cqr_title4a">'
				+ '</div><span class="font-red hide form-error">This is required field  </span></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div></div>';
		return html;
		break;
	case "CHECKBOX":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="box_qus_radio"><img src="' + getContextPath() + '/resources/assets/images/Blank_checbox.png"></div>' + '<div class="box_qus_area"><div class="group">'
				+ '<input type="text" placeholder="Option" class="to_validate cqr_title4a">' + '</div><span class="font-red hide form-error">This is required field </span></div><div class="cq_removeOption"><img src="' + getContextPath()
				+ '/resources/assets/images/cqform_cross.png"></div></div>';
		return html;
		break;
	case "LIST":
		var html = '<div class="box_qus_row1 drager_point">' + '<div class="box_qus_number">1.</div>' + '<div class="box_qus_area"><div class="group">' + '<input type="text" placeholder="Option"  class="to_validate cqr_title4a">'
				+ '</div><span class="font-red hide form-error">This is required field</span></div><div class="cq_removeOption"><img src="' + getContextPath() + '/resources/assets/images/cqform_cross.png"></div></div>';
		return html;
		break;
	}
}
function reorderOptionsNumber() {
	console.log("reorderOptionsNumber");
	var count = 1;
	$('.box_qus_row1.drager_point').each(function(e) {
		$(this).find('.box_qus_number').text(count + '.');
		$(this).find("input").attr("id", "box_qus_number-" + count).attr("placeholder", "Option " + count);
		count++;
		// $(this).find('.box_qus_number').text(count + '.');
	});
	// $('.box_qus_row2').find('.box_qus_number').text(count + '.');

	initSpinner();
	// $(".width100.pull-left.maxhgt400").animate({ scrollTop: $('.width100.pull-left.maxhgt400').height()}, 1000);
//	$(".width100.pull-left.maxhgt400").animate({
//		scrollTop : $(document).height()
//	}, "slow");
}

function initSpinner() {
	$(".score-spinner").spinner();
	$('.score-spinner').spinner('option', 'min', 0);
	$('.score-spinner').spinner('option', 'max', 100);
	$(".spinner").unbind("keypress");
	$('.score-spinner').bind("keydown", function(event) {
		var oldVal = $.trim($(this).val()) + event.key;
		// console.log(oldVal+event.key);
		var key = window.event ? event.keyCode : event.which;
		if (event.keyCode === 8 || event.keyCode === 46) {
			return true;
		} else if (key < 48 || key > 57) {
			return false;
		} else if (oldVal > 100) {
			return false;
		} else {
			return true;
		}
	});
}