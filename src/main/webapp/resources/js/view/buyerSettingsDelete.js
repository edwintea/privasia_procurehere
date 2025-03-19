jQuery(document).ready(function($) {
	/* getting success msg aftr delete buyersetting */
	var succes= sessionStorage.getItem('succes');
	sessionStorage.removeItem('succes');
	if(succes!=null){
		console.log("success :" +succes);
		$('p[id=idGlobalSuccessMessage]').html(succes);
		$('div[id=idGlobalSuccess]').show();
	};
	
	$("#idConfirmDelete").click(function(event) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		event.preventDefault();
		$("#btn-update").prop("disabled", true);

		var data = {}
		data["id"] = $('#hiddenId').val();
		$.ajax({
			type : "POST",
			url :  "buySetDlt",
			data :  JSON.stringify(data),
			beforeSend : function(xhr) {
				 xhr.setRequestHeader("Content-Type", "application/json");
				 xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			complete : function() {
				$('#loading').hide();
				$("#btn-update").prop("disabled", false);
			},
			success : function(data, textStatus, request) {
				var success=request.getResponseHeader('success');
				console.log("success :" +success);
				sessionStorage.setItem("succes", success);
				window.location=getContextPath() + data;
			},
			error : function(request, textStatus, errorThrown) {
				console.log(" Error : " + request.getResponseHeader('error'));
				$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",","<br/>"));
				$("#idGlobalError").show();
			}
		});

	});

});