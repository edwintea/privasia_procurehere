$(document).delegate('.addToListBtnWishlistPop', 'click', function(event) {
	var data = {}

	if (!$('#addListForm').isValid())
		return false;

	data["communicaionEmail"] = $('#sEmail').val();
	data["fullName"] = $('#sName').val();
	data["designation"] = $('#sdesgn').val();
	data["contactNumber"] = $('#sCnumb').val();
	data["favouriteSupplierTaxNumber"] = $('#sTnumb').val();
	data["status"] = $('#status :selected').val();
	data["vendorCode"] = $('#sVenderCode').val();
	data["subsidiary"] = $('#subsidiary').is(":checked");

	var indCat = [];
	var prodCat = [];
	select1 = $("#token-input-demo-input-local").parent().parent().find(".token-input-token");
	select1.each(function() {
		indCat.push($(this).find('input').val());
	});
	console.log(indCat);
	select2 = $("#token1-input-prod-input-local").parent().parent().find(".token-input-token");
	select2.each(function() {
		prodCat.push($(this).find('input').val());
	});

	var supplierTags = [];
	$.each($("#supplierTags option:selected"), function() {
		supplierTags.push($(this).val());
	});

	console.log(prodCat)

	if (indCat.length <= 0) {

		event.preventDefault();
		$("#token-input-demo-input-local").closest(".token-input-list").css({
			'border-color' : '#a94442'
		})
		return;
	}

	/*
	 * if (prodCat.length <= 0) { event.preventDefault(); $("#token1-input-prod-input-local").closest(".token-input-list").css({ 'border-color' :
	 * '#a94442' }) return; }
	 */

	data["supplierTags"] = supplierTags;
	data["id"] = $('#supplierId').val();
	data["indCat"] = indCat;
	data["productCategory"] = prodCat;
	data["ratings"] = $('#ratings').val();
	data["saveOrUpdate"] = $('#Btn').val();
	AddToList(event, data, 'ADD')
});
// To Add to list
function AddToList(event, data, status) {
	event.preventDefault();
	if (!$('#addListForm').isValid()) {
		return false;
	}
	$('div[id=idGlobalSuccess]').hide();
	$('div[id=idGlobalError]').hide();
	$('#loading').show();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	console.log(data);
	var supplierId = data.id;
	$.ajax({
		type : "POST",
		url : "addToList",
		// async: false,
		data : JSON.stringify(data),
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data, textStatus, request) {
			console.log("inside success");
			if (data.saveOrUpdate == "Add to My List") {
				$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
				$('div[id=idGlobalSuccess]').show();
				$('#supplierAddAfavorite').modal('hide');
				$('.idSupplier[data-value="' + supplierId + '"]').remove();
				setTimeout(function() {
					window.location.reload()
				}, 900);
				// window.location.reload();
			} else if (data.saveOrUpdate == "Update") {
				console.log("inside success Update");
				$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
				$('div[id=idGlobalSuccess]').show();
				$('#supplierAddAfavorite').modal('hide');
				setTimeout(function() {
					window.location.reload()
				}, 900);

			}

			// $('.idSupplier[data-value="'+supplierId+'"]').find('.addToListBtnWishlist').addClass('removeToListBtnWishlist').removeClass('addToListBtnWishlist').text('Remove');
		},
		error : function(request, textStatus, errorThrown) {
			console.log("inside error");
			console.log('Error: ' + request.getResponseHeader('error'));
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('#supplierAddAfavorite').modal('hide');

		}
	});
}

/* for toggle the form */
jQuery(document).ready(function($) {
	var showMoreOption = $('#showMoreOption').val();
	if (showMoreOption === 'true') {
		$('.hidenAdvanceSrchFields').show();
		$('.oldCategorySearch, .addadvenceSearch').hide();
	} else {
		$('.hidenAdvanceSrchFields').hide();
	}
	var globalSearch = $('#globalSearch').is(':checked');

	var registered = $('#registered').is(':checked');

	if (globalSearch) {
		$('.statusListDropDwn').hide();
	} else {
		$('.statusListDropDwn').show();
	}

	$('.addadvenceSearch').click(function() {
		$('.hidenAdvanceSrchFields').show();
		$('.oldCategorySearch, .addadvenceSearch').hide();
	});
	$('label.hidenAdvanceSrchFields').click(function() {
		$('.hidenAdvanceSrchFields').hide();
		$('.oldCategorySearch, .addadvenceSearch').show();
	});
	equalHeightBlock();
	// To ADD all the suppliers(removed)

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$('#AddAll').click(function(event) {
		event.preventDefault();

		var idSupplier = [];
		$('.idSupplier').each(function() {
			idSupplier.push($(this).attr('data-value'));

		});

		var data = [];
		data = idSupplier;
		$('.idSupplier').remove();
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "addAll",
			data : JSON.stringify(data),
			dataType : 'json',

			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var info = request.getResponseHeader('info');
				console.log("Success message : " + info);
				$('p[id=idGlobalSuccessMessage]').html(info != null ? info.split(",").join("<br/>") : "");
				$('div[id=idGlobalSuccess]').show();

			},
			error : function(request, textStatus, errorThrown) {
				console.log("Error  :: " + request.getResponseHeader('error'));
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});

});

// Bootstrap model to set the values

$(document).delegate('.addToListBtnWishlist', 'click', function(event) {
	event.preventDefault();
	$('#hideWarn').hide();
	$('#blackListPop').text("Confirm Blacklist");
	$('#suspendPop').text("Confirm Suspend")
	$('#supplierId').val($(this).attr('wishListSuppId'));
	$('#sEmail').val($(this).attr('wCommEmail'));
	$('#sName').val($(this).attr('wFullName'));
	$('#sdesgn').val($(this).attr('wDesignation'));
	$('#sCnumb').val($(this).attr('wCompanyContactNum'));
	$('#sTnumb').val($(this).attr('wCompanyFaxNum'));
	$('#activeOrInactive').val($(this).attr('wActiveInactive'));
	var activeInActice = $('#activeOrInactive').val();
	if (activeInActice != "BLACKLISTED" || activeInActice != 'SUSPENDED') {
		$('#bList').text("BlackList");
		$('#suspend').text("Suspend");
		$('#submitStatus').text("BlackList");
		$('#suspend').show();
		$('#bList').show();
	}

	$('#bList').hide();
	$('#suspend').hide();
	$('#Btn').show();
	$('#Btn').val("Add to My List");
	$('.token-input-list').remove();
	$("#demo-input-local").tokenInput("searchIndustryCategories", {
		minChars : 1,
		method : 'GET',
		hintText : "Start typing to search industry categories...",
		noResultsText : "No results",
		searchingText : "Searching...",
		queryParam : "search",
		propertyToSearch : "name",
		propertyToSearchCode : "code",
		minChars : 3,
		preventDuplicates : true
	});

	$("#prod-input-local").tokenInput1("searchProductCategories", {
		minChars : 1,
		method : 'GET',
		hintText : "Start typing to search product categories...",
		noResultsText : "No results",
		searchingText : "Searching...",
		queryParam : "search",
		propertyToSearch : "productName",
		propertyToSearchCode : "productCode",
		preventDuplicates : true,
		minChars : 3,
	});

	$('#supplierAddAfavorite').modal();
	// AddToList(event,$(this).parents('.idSupplier').attr('data-value'), 'ADD')
});

// Search based on input fields and tabs

jQuery(document).ready(function($) {

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$("#resetSearchSupplier").click(function(event) {
		$('#companyName').val('');
		$('#companyRegistrationNumber').val('');
		$('.orderSupplierCustom').val('Newest').trigger("chosen:updated");
		$('select[name=supplierCustomStatus]').val('ACTIVE').trigger("chosen:updated");

		$('#globalSearch').prop("checked", false);
		$('#registered').prop("checked", false);
		$.uniform.update();
		$('.indusCatgryToHideOnGlobal').show();
		$('.naicsToHideOnLocal').hide();
		$('#idStatus').show();

		$('#chosenCategoryAll').val('').trigger("chosen:updated");
		$('#chosenCategoryAllNaics').val('').trigger("chosen:updated");
		$('#projectName').val('');

		$('.statusListDropDwn').show();
		table.ajax.reload();
	});

	$(document).delegate('#registered', 'click', function(event) {
		$('#registeredValue').val($('#registered').is(':checked'));
	});

	/*
	 * $('.orderSupplierCustom').change(function(event) { orderedSearch(); });
	 */
	/*
	 * function orderedSearch() { $('div[id=idGlobalSuccess]').hide(); $('div[id=idGlobalError]').hide();
	 * 
	 * //console.log("orderedSearch"); // event.defaultPrevented(); var str = $("#searchSupplierForm").serialize(); str += "&order=" +
	 * $('.orderSupplierCustom').val();
	 * 
	 * var globalSearch = $('#globalSearch').is(':checked'); //console.log(globalSearch); if(globalSearch != true){ str += "&status=" +
	 * $('select[name=supplierCustomStatus]').val(); } //console.log(str); $.ajax({
	 * 
	 * type : "GET", url : "searchSupplier", data : str, dataType : "json", beforeSend : function(xhr) { $('#loading').show();
	 * xhr.setRequestHeader(header, token); xhr.setRequestHeader("Accept", "application/json"); xhr.setRequestHeader("Content-Type",
	 * "application/json"); }, complete : function() { $('#loading').hide(); }, success : function(data) { $("#inferno").show();
	 * $("#idGlobalInfo").hide(); $("#btn-update").prop("disabled", false); var html = ''; html += '<div class="row"> <div class="col-xs-12"> <div
	 * class="form-group col-md-12 bordered-row">' html += '<table id="tableList" class="display table table-bordered noarrow" cellspacing="0"
	 * width="100%">' html += '<thead> <tr class="tableHeaderWithSearch">' html += '<th search-type=""><spring:message code="application.action" /></th>'
	 * html += '<th search-type="text">Company Name</th>' html += '<th search-type="text">Country</th>' html += '<th search-type="">Date of
	 * Established</th>' html += '<th search-type="">Registration Date</th>' html += '<th search-type="select" search-options="statusList"><spring:message
	 * code="application.status" /></th>' html += '<th search-type="text">Person in Charge</th>' html += '</tr></thead>'; $.each(data,
	 * function(key, value) { console.log(value.favourite); var displayBlockfav = 'none'; var displayremovfav = 'block'; if (value.favourite == true) {
	 * displayBlockfav = 'block'; displayremovfav = 'none'; } var onclickAction = "javascript:document.getElementById('actionValue').value='" +
	 * value.id + "' ;form.submit();";
	 * 
	 * html += '<tr><td><button class="editfavDetails" type="button" id="editSuppId" name="editSuppId" value="' + value.id html += '"
	 * data-toggle="modal"><img src="'+getContextPath()+'/resources/images/edit1.png"></button>' html += '<button class="removeFavSupplierBtn"
	 * type="button" value="' + value.id + '" data-toggle="modal"><img src="'+getContextPath()+'/resources/images/delete1.png"></button></td>'
	 * html += '<td>'+value.companyName+'</td>' html += '<td>'+value.registrationOfCountry.countryName+'</td>' if(value.yearOfEstablished !=
	 * undefined){ html += '<td>' + value.yearOfEstablished + '</td>'; } else { html += '<td></td>'; } if(value.registrationCompleteDate !=
	 * undefined){ html += '<td>' + value.registrationCompleteDate + '</td>'; } else { html += '<td></td>'; } html += '<td>' +
	 * value.activeInactive + '</td>'; html += '<td>'+value.fullName+'</td></tr>'; /*var onclickAction =
	 * "javascript:document.getElementById('actionValue').value='" + value.id + "' ;form.submit();"; html += '<div class="col-md-4 marg-bottom-10
	 * idSupplier" id="' + value.id + '" data-value="' + value.id + '" style="display: block">'; html += '<div
	 * class="lower-bar-search-contant-main-block" id="test">'; html += '<div class="lower-bar-search-contant-main-block-heading light-gray-bg
	 * pad_all_10">'; html += '<div style=" display:' + displayBlockfav + ';">'; html += '<a class="pull-right editfavDetails" href=""><img
	 * src="'+getContextPath()+'/resources/images/edit1.png"></a> <span style="margin-top: -1px; margin-right: 10px; float: right;
	 * data-toggle="tooltip" data-original-title="This supplier is in your favourite list"> <i class="fa fa-heart fa-2x mar_5" aria-hidden="true"
	 * style="color: #0CB6FF;"></i></span>'; html += '</div>'; html += '<h4>' + value.companyName + '</h4>'; html += '<span> ' +
	 * value.registrationOfCountry.countryName + '</span>'; html += '</div>'; html += '<div class="lower-bar-search-contant-main-contant
	 * pad-top-side-10">'; if(value.yearOfEstablished != undefined){ html += '<label>Date of Established : </label> <span>' + value.yearOfEstablished + '</span>'; }
	 * else { html += '<label>Date of Established : </label> <span></span>'; } html += '</div>'; html += '<div
	 * class="lower-bar-search-contant-main-contant pad-top-side-5">'; if(value.registrationCompleteDate != undefined){ html += '<label>Registration
	 * Date :</label> <span>' + value.registrationCompleteDate + '</span>'; } else { html += '<label>Registration Date :</label> <span></span>'; }
	 * html += '</div>'; html += '<div class="lower-bar-search-contant-main-contant pad-top-side-5">'; html += '<label>Account Status :</label>
	 * <span class="green">' + value.activeInactive + '</span>'; html += '</div>'; html += '<div class="lower-bar-search-contant-main-contant
	 * pad-top-side-5">'; html += '<label>Person in Charge :</label> <span> ' + value.fullName + '</span>'; html += '</div>'; html += '<input
	 * type="hidden" id="fullNameOld" value="' + value.fullName + '"> <input type="hidden" id="companyContactNumberOld" value="' +
	 * value.companyContactNumber + '"> <input type="hidden" id="communicationEmailOld" value="' + value.communicationEmail + '"> <input type="hidden"
	 * id="designationOld" value="' + value.designation + '">'; html += '<div class="lower-bar-search-contant-main-contant pad_all_10">'; var attr
	 * =""; if(canEdit()==="false"){ attr = "disabled"; } if (value.favourite != true) { html += '<button class="btn btn-info btn-block hvr-pop
	 * hvr-rectangle-out addToListBtnWishlist" style="display:' + displayremovfav + '" '+attr+' type="submit">Add to My List</button>'; } else { html += '<button
	 * class="btn btn-info btn-block hvr-pop hvr-rectangle-out removeFavSupplierBtn" value="' + value.id + '" style="display:' + displayBlockfav + '"
	 * type="button" '+attr+'>Remove</button>'; } html += '<button class="btn btn-black for-form-back hvr-pop hvr-rectangle-out1" onclick="' +
	 * onclickAction + '" type="submit">View Details</button>'; html += '</div>'; html += '</div>'; html += '</div>'; }); html += '</table></div></div></div>';
	 * $('#inferno').html(html); equalHeightBlock(); }, error : function(request, textStatus, errorThrown) { $("#inferno").hide();
	 * $("#searchSupplierz").prop("disabled", false); console.log(" Error : " + request.getResponseHeader('error')); console.log(" Info : " +
	 * request.getResponseHeader('info')); if (request.getResponseHeader('error')) {
	 * $("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
	 * $("#idGlobalError").show(); } if (request.getResponseHeader('info')) {
	 * $("#idGlobalInfoMessage").html(request.getResponseHeader('info').replace(",", "<br/>").replace(",", "<br/>").replace(",", "<br/>"));
	 * $("#idGlobalInfo").show(); } } }); }
	 */
});

/* Search industry category and NAICS code based suppliers */

$(document).ready(function() {
	$(document).on("keyup", "#chosenCategoryAll_chosen .chosen-search input", function() {
		var industryCat = $.trim($(this).val());
		var industryCatOrg = $(this).val();
		var currentObj = $(this);
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		if (industryCat.length > 2) {
			$.ajax({

				url : 'searchIndustryCategory',
				data : {
					'search' : industryCat
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data) {
					console.log(data);
					var html = '<option value="">Select Category</option>';
					if (data != '' && data != null && data.length > 0) {
						$.each(data, function(key, value) {
							html += '<option value="' + value.id + '">' + value.name + '</option>';
						});
					}
					$('#chosenCategoryAll').html(html);
					$("#chosenCategoryAll").trigger("chosen:updated");
					currentObj.val(industryCatOrg);
				},
				error : function(error) {
					console.log(error);
				},
				complete : function() {
					$('#loading').hide();
				}
			});
		}
	});
	$(document).on("keyup", "#chosenCategoryAllNaics_chosen .chosen-search input", function() {
		var industryCat = $.trim($(this).val());
		var industryCatOrg = $(this).val();
		var currentObj = $(this);
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		if (industryCat.length > 2) {
			$.ajax({
				url : 'searchNaicsCategory',
				data : {
					'search' : industryCat
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data) {
					console.log(data);
					var html = '<option value="">Select Category</option>';
					if (data != '' && data != null && data.length > 0) {
						$.each(data, function(key, value) {
							html += '<option value="' + value.id + '">' + value.categoryName + '</option>';
						});
					}
					$('#chosenCategoryAllNaics').html(html);
					$("#chosenCategoryAllNaics").trigger("chosen:updated");
					currentObj.val(industryCatOrg);
					$('#loading').hide();
				},
				error : function(error) {
					console.log(error);
				}
			});
		}
	});
});

$(document).delegate('#removeFavSupplierById', 'click', function(event) {
	var data = {}
	data["id"] = $('#favSuppDeleteId').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$('#favSuppDelete').modal('hide');
	$.ajax({
		type : "POST",
		url : "removeFavouriteSupplier",
		// async: false,
		data : JSON.stringify(data),
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		},
		complete : function() {
			$('#loading').hide();
			// toggle(id);
		},
		success : function(result, textStatus, request) {
			$('.idSupplier#' + data.id).remove();
			$('div[id=idGlobalError]').hide();
			console.log("inside success" + data.id);
			$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
			$('div[id=idGlobalSuccess]').show();
			$('.idSupplier[data-value="' + data.id + '"]').remove();
			$('#favSuppDeleteId').val('');
			table.ajax.reload();
			// $('.idSupplier[data-value="'+supplierId+'"]').find('.addToListBtnWishlist').addClass('removeToListBtnWishlist').removeClass('addToListBtnWishlist').text('Remove');
		},
		error : function(request, textStatus, errorThrown) {
			console.log("inside error");
			console.log('Error: ' + request.getResponseHeader('error'));
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('div[id=idGlobalError]').show();
			$('div[id=idGlobalSuccess]').hide();

		}
	});

});

$(document).ready(function() {
	$('.hideCustomFields').change(function() {
		if (this.checked) {
			$('.indusCatgryToHideOnGlobal').hide();
			$('.naicsToHideOnLocal').show();
			$('#chosenCategoryAll').val('').trigger("chosen:updated");
			$('#idStatus').hide();
			$('.statusListDropDwn').hide();
		} else {
			$('.indusCatgryToHideOnGlobal').show();
			$('.naicsToHideOnLocal').hide();
			$('#chosenCategoryAllNaics').val('').trigger("chosen:updated");
			$('#idStatus').show();
			$('.statusListDropDwn').show();
		}
	});

});

// Edit favorite supplier details dialog
$(document).delegate('.editfavDetails', 'click', function(event) {
	event.preventDefault();
	var data = {}
	data["id"] = $(this).attr('data-id');
	// $(this).parents('.idSupplier').attr('id');
	$('.token-input-list').remove();
	$('#suspendRemark').val('');
	$('#blackListRemark').val('');
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	$('#catValErr').html("");
	$.ajax({
		type : "POST",
		url : "editFavSupp",
		// async: false,
		data : JSON.stringify(data),
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data, textStatus, request) {
			$('#daterangepicker-time').val("");
			
			if(data.status=="ACTIVE" && (!data.isFutureSuspended)){
				$('#submitStatusSuspend').text("Suspend");
			}
			
			if (data.suspensionDuration != undefined && data.suspensionDuration.toString() != "null - null") {
				if (data.suspensionDuration && data.isFutureSuspended) {
					$('#daterangepicker-time').val(data.suspensionDuration);
				}
			}
			if (data.status == "BLACKLISTED" || data.status == 'SUSPENDED') {
				$('#warnMsg').text("This supplier has been " + data.status);
			} else {
				$('#hideWarn').hide();
			}

			$('#Reschedule').hide();

			$('#bList').show();
			$('#suspend').show();
			$('#Btn').show();
			$('#bList').text("BlackList");
			if (data.status == "BLACKLISTED") {
				$('#blackListPop').text("Confirm Remove Blacklist");
				$('#suspend').hide();

				$('#bList').text("Un-Blacklist");
				$('#confirmation').text("Are you sure you want to Remove supplier from BlackList ?")
				$('#submitStatus').text("Active");
				$('#warnMsg').text("This supplier has been " + data.status);
				$('#hideWarn').show();
			} else if (data.status == 'SUSPENDED') {
				$('#suspend').text("Un-Suspend");
				$('#suspendPop').text("Confirm Remove Suspension");
				$('#bList').hide();
				$('#hideWarn').show();
				$('#warnMsg').text("This supplier has been " + data.status);
				$('#confirmationSuspend').text("Are you sure you want to Remove supplier Suspension ?")
				$('#submitStatusSuspend').text("Active");
				$('#suspendRange').hide();
			} else {
				$('#blackListPop').text("Confirm Blacklist");
				$('#bList').text("BlackList");
				$('#confirmation').text("Are you sure you want to Blacklist this supplier ?");
				$('#submitStatus').text("BlackList");

				$('#suspendPop').text("Confirm Suspend");

				if (data.isFutureSuspended == true) {
					$('#Reschedule').show();
					$('#activeSuspend').val("reschedule");
					$('#suspendPop').text("Confirm Reschedule / Unsuspend");
					$('#suspend').text("Reschedule /Cancel Suspend");
					$('#submitStatusSuspend').text("Active");
					$('#confirmationSuspend').text("Are you sure you want to Reschedule / Cancel supplier suspension ?")
					$("#submitStatusSuspend").click(function() {
						$('#activeSuspend').val('');
					})

				} else {
					$('#suspend').text("Suspend");
					$('#confirmationSuspend').text("Are you sure you want to Suspend this supplier ?")
				}
			}
			$('#supplierId').val(data.id);
			$('#bList').val(data.id);
			$('#sEmail').val(data.communicationEmail);
			$('#sName').val(data.fullName);
			$('#sdesgn').val(data.designation);
			$('#sCnumb').val(data.companyContactNumber);
			$('#sTnumb').val(data.favouriteSupplierTaxNumber);
			$('#ratings').val(data.ratings);
			$("#subsidiary").prop("checked", data.subsidiary);

			var tagArr = [];
			$.each(data.supplierTags, function(index, value) {
				tagArr.push(value.id);
			});

			$("#supplierTags").val(tagArr).trigger("chosen:updated");

			var vendorCode = '';
			if (data.vendorCode !== undefined) {
				vendorCode = data.vendorCode;
			}
			$('#sVenderCode').val(vendorCode);
			$('#status').val(data.status).trigger("chosen:updated");
			var indCatSeleted = '';
			/*
			 * $.each(data.industryCategory, function(i, category){ indCatSeleted += '<li class="token-input-token"><input name="catFav"
			 * value="'+category.id+'" type="hidden"><p>'+category.code+'-'+category.name+'</p><span class="token-input-delete-token">&times;</span></li>';
			 * });
			 */
			$("#demo-input-local").tokenInput("searchIndustryCategories", {
				minChars : 1,
				method : 'GET',
				hintText : "Start typing to search industry categories...",
				noResultsText : "No results",
				searchingText : "Searching...",
				queryParam : "search",
				propertyToSearch : "name",
				propertyToSearchCode : "code",
				preventDuplicates : true,
				minChars : 3,
				prePopulate : data.industryCategory
			});

			var industryCategory = data.industryCategory;
			var itemNames = [];
			industryCategory.forEach(function(item) {
				itemNames.push(item.code+"-"+item.name);
			});

			var namesString = itemNames.join("\n");
			$("#collected-values").val(namesString);

			$("#prod-input-local").tokenInput1("searchProductCategories", {
				minChars : 1,
				method : 'GET',
				hintText : "Start typing to search product categories...",
				noResultsText : "No results",
				searchingText : "Searching...",
				queryParam : "search",
				propertyToSearch : "productName",
				propertyToSearchCode : "productCode",
				preventDuplicates : true,
				minChars : 3,
				prePopulate : data.productCategory,
			});

			$('#supplierAddAfavorite').find('.token-input-list').prepend(indCatSeleted);
			$('#demo-input-local').attr('data-validation', 'required');
			// $('#prod-input-local').attr('data-validation', 'required');
			$('#demo-input-local').attr('data-validation-error-msg-container', '#catValErr');
			// $('#prod-input-local').attr('data-validation-error-msg-container',
			// '#prodValErr');
			$.validate({});
			// $('#demo-input-local').val(data.indCat);
			$('#Btn').val("Update");

			$('#supplierAddAfavorite').modal();

		},
		error : function(request, textStatus, errorThrown) {
			console.log("inside error");

		}
	});

});
$(document).delegate('.removeFavSupplierBtn', 'click', function(event) {
	event.preventDefault();
	$('#favSuppDeleteId').val($(this).attr('removeSuppId'));
	$('#favSuppDelete').modal();
});
function equalHeightBlock() {
	var maxHeight = Math.max.apply(null, $("div.idSupplier").map(function() {
		return $(this).height();
	}).get());
	$("div.idSupplier").css('min-height', maxHeight);
}

$('#downloadSupplierTemplate').click(function(e) {
	e.preventDefault();
	window.location.href = getBuyerContextPath('SupplierTemplate');
})

$('#uploadSupplier').click(function(e) {
	e.preventDefault();
	$('#uploadSupplierListFile').trigger("click");

});

$('#uploadSupplierList').click(function(e) {
	e.preventDefault();
	$('#uploadSupplierModel').modal('show');
	$('#updateSupplier').hide();
	$("#isUploadNewSupplier").prop("checked", true);
	$('#supplierTags').val('').trigger('chosen:updated');

});

$('#isUploadNewSupplier').click(function(e) {
	$('#updateSupplier').hide();
});

$('#isUpdateSupplier').click(function(e) {
	$('#updateSupplier').show();
});

$("#uploadSupplierListFile").on("change", function() {
	if ($(this).val() == "") {
		return;
	}

	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();

	if ($('#uploadSupplierListFile').val().length == 0) {
		$('p[id=idGlobalErrorMessage]').html("Please select upload file.");
		$('div[id=idGlobalError]').show();
		return;
	}

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var myForm = new FormData();
	var isUploadNewSupplier = false;
	if (document.getElementById('isUploadNewSupplier').checked) {
		isUploadNewSupplier = true;
	}

	myForm.append('isUploadNewSupplier', isUploadNewSupplier);
	myForm.append("file", $('#uploadSupplierListFile')[0].files[0]);

	$.ajax({
		url : getBuyerContextPath('uploadSupplierList'),
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
			$('#favSupptableList').DataTable().ajax.reload();
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
			$('#uploadSupplierModel').modal('hide');
			$('#loading').hide();
			$("#isUploadNewSupplier").prop("checked", true);
		},
		error : function(request, textStatus, errorThrown) {
			$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
			$('.alert').hide();
			$('div[id=idGlobalError]').show();
			$('#loading').hide();
			$('#uploadSupplierModel').modal('hide');
			$("#isUploadNewSupplier").prop("checked", true);
		},
		complete : function() {
			$('#loading').hide();
		}
	});
	$(this).val("");
});


$('#assignFormSupplierButton').click(function(e) {
	var data = {}
	if (!$('#assignSupplierForm').isValid())
		return false;

	var supplierForms = [];
	$.each($("#chosenSupplierForms option:selected"), function() {
		supplierForms.push($(this).val());
	});

	var supplierIdList = [];
	$.each($("#chosenSupplierIds option:selected"), function() {
		supplierIdList.push($(this).val());
	});
	
	var reassign=false;
	if($("#reassignForm").is(':checked')){
		reassign=true;
	}
	var assignToAllSuppliers=false;
	if($("#assignToAllSuppliers").is(':checked')){
		assignToAllSuppliers=true;
	}
	data["supplierFormIds"] = supplierForms;
	data["supplierIds"] = supplierIdList;
	data["assignToAllSuppliers"] = assignToAllSuppliers;
	data["reassignForm"] = reassign;
		

	event.preventDefault();
	$('div[id=idGlobalSuccess]').hide();
	$('div[id=idGlobalError]').hide();
	$('#loading').show();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	console.log(data);
	$.ajax({
		type : "POST",
		url : getBuyerContextPath("assignFormToSupplier"),
		data : JSON.stringify(data),
		beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		},
		complete : function() {
			$('#loading').hide();
		},
		success : function(data, textStatus, request) {
			console.log("inside success");
			$('#chosenSupplierIds').val('').trigger("chosen:updated");
			$('#chosenSupplierForms').val('').trigger("chosen:updated");
			$("#reassignForm").prop('checked', false);
			$("#assignToAllSuppliers").prop('checked', false);
			var info = request.getResponseHeader('success');
			console.log(" *** Success message : ***" + info);
			$(".show_name").html('');
			$('p[id=idGlobalSuccessMessage]').html(info);
			 var pendingSuppFormCount=request.getResponseHeader('pendingSuppFormCount');
			$('#pendingSuppFormCountId').html(pendingSuppFormCount);
			$('.ajax-msg').show();
			$.jGrowl(info, {
				sticky : false,
				position : 'top-right',
				theme : 'bg-green'
			});
			$("#idCollapseAssignFormSupplier").attr("aria-expanded",false);
			$('#loading').hide();
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
		}
	});
});