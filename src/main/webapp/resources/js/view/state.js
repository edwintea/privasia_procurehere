jQuery(document).ready(function($) {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	
	$("#saveState").click(function(event) {
		event.preventDefault();
		 
		  $.ajax({
			   type : "POST",
			   url : "state",
			   data : $('#stateRegisterForm').serialize(),
			   //dataType: "json",
			   beforeSend : function(xhr) {
			    $('#loading').show();
			    xhr.setRequestHeader(header, token);
			    //xhr.setRequestHeader("Accept", "application/json");
			    //xhr.setRequestHeader("Content-Type", "application/json");
			   },
			   complete : function() {
			    $('#loading').hide();
			   },
			   success : function(data) {
				    $("#btn-update").prop("disabled", false);
				     console.log(" SUCCESS : "+ data);
				    // alert('Successfully added State');
				     window.location=getContextPath()+data;
				   },
				   error: function(request, textStatus, errorThrown){  
				    $("#saveState").prop("disabled", false);
				  //  alert('Error Occured : ' + request.getResponseHeader('error'));
				    console.log(" Error : " + request.getResponseHeader('error'));
				    console.log(" Error : " + request.getResponseHeader('errrors'));
				    $("#idGlobalErrorMessage").html(request.getResponseHeader('error').replace(",","<br/>").replace(",","<br/>").replace(",","<br/>"));
				    $("#idGlobalError").show();
				    $("#idGlobalErrorMessage").html(request.getResponseHeader('errrors').replace(",","<br/>").replace(",","<br/>").replace(",","<br/>"));
				    $("#idGlobalError").show();
				   }
				  });

				 });

				});



