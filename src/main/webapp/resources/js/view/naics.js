jQuery(document).ready(function($) {
	
	
	$("#saveCat").click(function(event) {
		event.preventDefault();
	
		$.ajax({
			type : "POST",
			url : "naics",
			data : $('#saveCatForm').serialize(),
			beforeSend : function(xhr) {
				$('#loading').show();
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data) {
				$("#btn-update").prop("disabled", false);
				console.log(" SUCCESS : "+ data);
				window.location=getContextPath() + data;
			},
  		  	error: function(request, textStatus, errorThrown){  
  		  		console.log(" ERROR : "+ errorThrown + " ErrMsg : " + request.getResponseHeader('error'));
  		  		$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",","<br/>"));
  		  		$("#idGlobalError").show();
  		  	}
		});

	});

});