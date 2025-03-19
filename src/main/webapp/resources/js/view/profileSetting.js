//for image select
$(document).ready(function() {

	$("#image-holder").on('click', '.thumb-image', function() {
		$(this).toggleClass("selectedItem");
	});

	$("#cancelChangePass").on("click", function() {
		$('#idDialogError').hide();
	});

	$("#selectedItem").on("click", function() {
		$(".selectedItem").remove();
	});

	$("#prfPic").on('change', function() {
		var pic = $(this);
		if (typeof (FileReader) == null) {
			var image_holder = document.getElementById("defaultProfileImageHolder").src;
			image_holder.attr('src', getContextPath() + 'resources/images/profile_setting_image.png');
		} else if (typeof (FileReader) != "undefined") {
//			var image_holder = $("#profileImageHolder");
			$('.profile_picture').each(function(item){
				console.log('Id wala element mil gaya ', item);
				var image_holder = $(this);
				image_holder.empty();
				var reader = new FileReader();
				reader.onload = function(e) {
					image_holder.attr('src', e.target.result);
				}
				image_holder.show();
				reader.readAsDataURL(pic[0].files[0]);
			});
		} else {
			//alert("This browser does not support FileReader.");
		}
	});

});


//for password dialog

$(document).delegate('.changePass', 'click', function(event) {
	event.preventDefault();
	$('#idDialogError').hide();
	$('#changePasswordModal').modal();
});

// to change password
$(document).delegate('#changeThePass', 'click', function(event) {
	var data = {};
	if(!$('#idChangePass').isValid()) {
		return;
	}
	event.preventDefault();
	$('div[id=idGlobalSuccess]').hide();
	$('div[id=idGlobalError]').hide();
	$('#loading').show();
	data["oldPassword"] = $('#oldPass').val();
	data["newPassword"] = $('#newPass').val();
	data["confirmPassword"] = $('#newPass2').val();
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	console.log(data);
	$.ajax({
		type : "POST",
		url : "changeUserPassword",
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
			$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
			$('div[id=idGlobalSuccess]').show();
			console.log("inside success");
			$('#changePasswordModal').modal('hide');
			$('#idDialogError').hide();
		},
		error : function(request, textStatus, errorThrown) {
			console.log("inside error");
			console.log('Error: ' + request.getResponseHeader('error'));
			var msg = request.getResponseHeader('error').replace(/,/g, "<br/>");
		
			$('#idDialogErrorMessage').html(msg);

			$('#idDialogError').show();
			$('#supplierAddAfavorite').modal('hide');
		}
	});
});