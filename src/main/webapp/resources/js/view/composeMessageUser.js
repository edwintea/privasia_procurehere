$(document).ready(function(){
$("#idUser").keyup(function(){
		$.ajax({
		type: "POST",
		url: "",
		data:'keyword='+$(this).val(),
		beforeSend: function(){
			$("#idUser").css("background","#FFF url(LoaderIcon.gif) no-repeat 165px");
		},
		success: function(data){
			$("#suggesstion-box").show();
			$("#suggesstion-box").html(data);
			$("#search-box").css("background","#FFF");
		}
		});
	});
});
//To select country name
function selectCountry(val) {
$("#search-box").val(val);
$("#suggesstion-box").hide();
}