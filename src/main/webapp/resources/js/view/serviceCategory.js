jQuery(document).ready(function($) {
	
	
	$("#saveServiceCategory").click(function(event) {
		event.preventDefault();
	
		$.ajax({
			type : "POST",
			url : "serviceCategory",
			data : $('#idCategoryForm').serialize(),
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
  		  		$("#saveServiceCategory").prop("disabled", false);
  		  		console.log(" ERROR : "+ errorThrown + " ErrMsg : " + request.getResponseHeader('error'));
  		  		$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",","<br/>"));
  		  		$("#idGlobalError").show();
  		  	}
		});

	});

});