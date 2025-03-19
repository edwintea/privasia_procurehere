<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<sec:authorize access="hasRole('BUYER')">
	
	<div class="">
		<table id="tableList" class="display table table-bordered noarrow" cellspacing="0" width="98%">
			<thead>
				<tr class="tableHeaderWithSearch">
					<c:if test="${auctionRules.auctionConsoleRankType ne 'SHOW_NONE'}">
						<th search-type=""><spring:message code="application.case.rank"/></th>
					</c:if>
					<th search-type="text" class="align-left"><spring:message code="application.case.bidders"/></th>
					<th search-type="text"><spring:message code="auctionrules.case.number.bids"/></th>
					<th search-type="text"><spring:message code="auctionrules.case.initial.price"/></th>
					<th search-type="text"><spring:message code="auctionrules.case.current.price"/></th>
					<th search-type="text">% <spring:message code="rfaevent.case.diff"/></th>
				</tr>
			</thead>
		</table>
	</div>
</sec:authorize>
<style>
#tableList td {
	text-align: center;
}
#tableList th { 
    padding: 17px 10px !important;
    font-family: 'Open Sans', sans-serif;
    font-weight: 600;
}
</style>
<c:if test=""></c:if>
<script type="text/javascript">
	var suppliersListConsole = null;
	$('document').ready(function() {

		var eventId = $('#eventId').val();
		var bqId = $('#supplierBqList #bqId').val();
		//var supplierId = $('#supplierId').val();
		var tenantId = $('#supplierId').val();
		var refreshUrl = getContextPath() + "/supplier/englishAuctionConsole/" + eventId;

		function refreshData() {
			if (suppliersListConsole) {
				suppliersListConsole.destroy();
			}

			suppliersListConsole = $('#tableList').DataTable({
				"processing": false,
				"deferRender": false,
				"serverSide": false,
				"paging": false,
				"info": false,
				"searching": false,
				"ajax": {
					"url": getContextPath() + '/supplier/refreshAuctionConsole/' + eventId,
					"data": function(d) {},
					"dataSrc": function(json) {
						<c:if test="${event.status eq 'ACTIVE'}">
						if (json.status != 'ACTIVE') {
							window.location.href = refreshUrl;
						}
						</c:if>
						<c:if test="${event.status eq 'SUSPENDED'}">
						if (json.status != 'SUSPENDED') {
							window.location.href = refreshUrl;
						}
						</c:if>
						return json.data;
					},
					"error": function(request, textStatus, errorThrown) {
						var error = request.getResponseHeader('error');
						if (error != undefined) {
							$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
							$('div[id=idGlobalError]').show();
							// Implement growl
						}
					}
				},
				"order": [[0, "asc"]],
				"columns": [
					<c:if test="${auctionRules.auctionConsoleRankType ne 'SHOW_NONE'}">
					{
						"mData": "rankOfSupplier",
						"searchable": false,
						"orderable": true,
						"defaultContent": ""
					},
					</c:if>
					{
						"mData": "supplierCompanyName",
						"searchable": false,
						"orderable": true,
						"className": "align-left",
						"defaultContent": "",
						"mRender": function(data, type, row) {
							if (tenantId == row.supplierId) {
								if (row.isDisqualify) {
									window.location.href = refreshUrl;
								} else {
									return row.supplierCompanyName;
								}
							} else {
								return row.supplierCompanyName;
							}
						}
					}, {
						"mData": "numberOfBids",
						"searchable": false,
						"orderable": false,
						"defaultContent": ""
					},{
						"mData": "initialPrice",
						"searchable": false,
						"orderable": false,
						"className": "align-right",
						"defaultContent": "",
						"mRender": function(data, type, row) {
							if(row.initialPrice === "" || row.initialPrice === undefined){
								return row.initialPrice;
							} else {
								return ReplaceNumberWithCommas(row.initialPrice.toFixed($('#decimal').val()));
							}
						}
					}, {
						"mData": "currentPrice",
						"searchable": false,
						"orderable": true,
						"className": "align-right",
						"defaultContent": "",
						"mRender": function(data, type, row) {
							if(row.currentPrice === "" || row.currentPrice === undefined){
								return row.currentPrice;
							} else {
								return ReplaceNumberWithCommas(row.currentPrice.toFixed($('#decimal').val()));
							}
						}
					}, {
						"mData": "differencePercentage",
						"searchable": false,
						"orderable": false,
						"defaultContent": "",
						"mRender": function(data, type, row) {
							if(row.differencePercentage === "" || row.differencePercentage === undefined){
								return row.differencePercentage;
							} else {
								return row.differencePercentage.toFixed(2);
							}
						}
					}
				]
			});
		}

		refreshData();
		setInterval(refreshData, 1800000); // 30 minutes


	function ReplaceNumberWithCommas(yourNumber) {
		// Seperates the components of the number
		var n = yourNumber.toString().split(".");
		// Comma-fies the first part
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		// Combines the two sections
		return n.join(".");
	}
	});
</script>