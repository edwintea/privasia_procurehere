
var scrollTimer, lastScrollFireTime = 0;
$(window).scroll(function() {

	var minScrollTime = 500;
	var now = new Date().getTime();

	if (!scrollTimer) {
		if (now - lastScrollFireTime > (3 * minScrollTime)) {
			lastScrollFireTime = now;
		}
		scrollTimer = setTimeout(function() {
			scrollTimer = null;
			lastScrollFireTime = new Date().getTime();
			processPrScroll();
		}, minScrollTime);
	}

	function processPrScroll() {
		$("#idGlobalInfo").hide();
		$("#idGlobalError").hide();
		$("#idGlobalWarn").hide();
		$("#idEventInfo").hide();

		var searchValue = $('#searchValue').val();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		if ($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
			prPageNo++;
			var status = $('.buyerStatus').val();
			var order = $('.buyerOrder').val();
			var search = $('.idGlobalSearch').val();

			var data = {}
			data["globalSreach"] = search;
			data["status"] = status;
			data["order"] = order;
			data["pageNo"] = prPageNo;
			$.ajax({
				type : "POST",
				url : "buyerListForPagination",
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
				success : function(data) {
					renderGridPag(data);
				},
				error : function(request, textStatus, errorThrown) {

				}
			});
		}
	}
});