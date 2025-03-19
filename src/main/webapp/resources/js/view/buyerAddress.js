$(document).ready(function() {

	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	$('#idCountry').change(function() {
		var countryId = $('#idCountry').val();
		$.ajax({ type : "GET", url : getContextPath() + "/buyer/countryStatesList", data : { countryId : countryId }, beforeSend : function(xhr) {
			$('#loading').show();
			xhr.setRequestHeader(header, token);
			xhr.setRequestHeader("Accept", "application/json");
			xhr.setRequestHeader("Content-Type", "application/json");
		}, success : function(data) {
			var stateList = '<option value="">Select State</option>';
			$.each(data, function(i, obj) {
				stateList += '<option value="' + obj.id + '">' + obj.stateName + '</option>';
			});
			$('#loading').hide();
			$('#stateList').html(stateList);
			$('#stateList').trigger("chosen:updated");
		}, error : function(e) {
			console.log("Error");
			$('#loading').hide();
		}, });
	});

});