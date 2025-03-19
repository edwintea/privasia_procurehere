function updateLink(id) {
	var link = $("a#idConfirmDelete");
	link.data('href', link.attr("href"));
	link.attr("href", link.data('href') + ''+ id);
}
function doCancel() {
	var link = $("a#idConfirmDelete");
	link.data('href', link.attr("href"));
	link.attr("href", '${pageContext.request.contextPath}/admin/deleteCountry?countryId=');
}

