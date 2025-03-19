jQuery(document).ready(function($) {
	

	
	$("#saveCompanyStatus").click(function(event) {
		event.preventDefault();

		var data = {}
	

		$.ajax({
			type : "POST",
			url : "companyStatus",
			data :  $('#companyStatusForm').serialize(),
			beforeSend : function(xhr) {
				$('#loading').show();
				
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data) {
				$("#btn-update").prop("disabled", false);
				window.location=getContextPath() + data;
			},
			error : function(request, textStatus, errorThrown) {
				$("#saveCompanyStatus").prop("disabled", false);
				console.log(" Error : "+ request.getResponseHeader('error'));
				$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",","<br/>"));
				$("#idGlobalError").show();
			}
		});

	});


$("#editMode").click(function(event) {
	event.preventDefault();
	
	
	/*function editLink(id) {
	     var link1 = $("a#id");
		 link1.attr("value",id);
		 alert("abhi yaha aaya " +id)
	}*/
	
	var data = {}
	data["id"] = $("#id").val();
	alert(id)
	$.ajax({
		
		type : "POST",
		url : "editCompanyStatus",
		data : JSON.stringify(data),
		beforeSend : function(xhr) {
			$('#loading').show();
			
		},
		complete : function() {
			$('#loading').hide();
			alert(data);
		},
		success : function(data) {
			$("#btn-update").prop("disabled", false);
			console.log(" SUCCESS : "+ data);
			window.location=getContextPath() + data;
		},
		error : function(request, textStatus, errorThrown) {
			$("#saveCompanyStatus").prop("disabled", false);
			alert(data);
			console.log(" Error : "+ request.getResponseHeader('error'));
			$("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",","<br/>"));
			$("#idGlobalError").show();
		}
	});

});



});