<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<sec:authorize access="hasRole('BUYER')">
	<div class="">
		<div class="row">
			<div class="col-md-12">
				<div class="col-md-6 pull-left divSupplierList">
					<img src="${pageContext.request.contextPath}/resources/images/icon/Icon2/online2.png"><span class="spanSupplierList"><spring:message code="rfaevent.online"/></span>&nbsp;&nbsp;&nbsp;
					<img src="${pageContext.request.contextPath}/resources/images/icon/Icon2/away2.png"><span class="spanSupplierList"><spring:message code="rfaevent.away"/></span>&nbsp;&nbsp;&nbsp;
					<img src="${pageContext.request.contextPath}/resources/images/icon/Icon2/offline2.png"><span class="spanSupplierList"><spring:message code="rfaevent.not.logged.in"/></span>
				</div>
			</div>
		</div>
		<table id="tableList" class="display table table-bordered noarrow" cellspacing="0" width="98%">
			<thead>
				<tr class="tableHeaderWithSearch">
					<th class="width_200 width_200_fix "><spring:message code="application.case.status"/></th>
					<th class="width_200 text-left"><spring:message code="application.case.bidders"/></th>
				</tr>
			</thead>
		</table>
	</div>
</sec:authorize>
<style>
#tableList td {
	text-align: center;
}

.disableRow {
	background-color: red;
	color: black;
	margin-top: 10px;
	visibility: hidden;
}
</style>
<script type="text/javascript">

function reloadBidderList() {
	console.log("lo main aa ya");
	if (typeof suppliersListConsole !== 'undefined') {
		suppliersListConsole.ajax.reload();
		if (typeof resetExpiry === "function") {
			resetExpiry();
		}
	}
}



$(document).ready(function() {
	<c:if test="${event.status == 'ACTIVE'}">
	setInterval(reloadBidderList, 3000);
	</c:if>
});

</script>
<script type="text/javascript">
	var suppliersListConsole = null;
	$('document').ready(function() {
		var eventId = $('#eventId').val();
		console.log(eventId);
		suppliersListConsole = $('#tableList').DataTable({
			"processing" : false,
			"deferRender" : true,
			"serverSide" : false,
			"paging" : false,
			"info" : false,
			"searching" : false,
			"ajax" : {
				"url" : getContextPath() + '/auction/refreshDutchAuctionConsoleForSupplierList/' + eventId,
				"data" : function(params) {
					params.limit = $('#idSelectBidders').val();
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
						// impliment growl
					}
				}
			},
			<c:if test="${event.auctionType eq 'FORWARD_SEALED_BID' or event.auctionType eq 'REVERSE_SEALED_BID'}">
			"order" : [[ 1, "asc"]],
			</c:if>
			<c:if test="${event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH'}">
			"order" : [[ 1, "asc"],[ 2, "asc"]],
			</c:if>
			//"order" :[],
			"columns" : [
			{
				"mData" : "onlineStatus",
				"searchable" : false,
				"orderable" : false,
				"defaultContent" : "",
				"mRender" : function(data, type) {
					if (data == 'ONLINE') {
						var html = '<div class="status-badge mrg10A">';
						html += '<img src="${pageContext.request.contextPath}/resources/images/icon/online.png">';
						html += '<div class="small-badge bg-green"></div>';
						return html;
					}
					if (data == 'AWAY') {
						var html = '<div class="status-badge mrg10A">';
						html += '<img src="${pageContext.request.contextPath}/resources/images/icon/away.png">';
						html += '<div class="small-badge bg-orange"></div>';
						return html;
					}
					if (data == 'NOT_LOGGEDIN') {
						var html = '<div class="status-badge mrg10A">';
						html += '<img src="${pageContext.request.contextPath}/resources/images/icon/offline.png">';
						html += '<div class="small-badge bg-red"></div>';
						return html;
					}
				}
			}
			
			,{	"mData" : "supplierCompanyName",
				"searchable" : false,
				"orderable" : true,
				"defaultContent" : "",
				"className" : "text-left",
				"mRender" : function(data, type, row) {
					if (row.isDisqualify) {
						//console.log(row.isDisqualify);
						//$('tr', row).css('background-color', 'Red');
						// $("#table_id tbody tr").addClass("gradeA")
						//$('tr' , row).addClass("disableRow");
						return row.supplierCompanyName;
					}else{
						return row.supplierCompanyName;
					}
				}
			}
			]
		});
	});
	function ReplaceNumberWithCommas(yourNumber) {
		// Seperates the components of the number
		var n = yourNumber.toString().split(".");
		// Comma-fies the first part
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		// Combines the two sections
		return n.join(".");
	}
</script>
<style>
.spanSupplierList{
margin-left: 5px;
}
.divSupplierList{
padding-top: 2px;
padding-bottom: 2px;
}
.divSupplierList > span{
font-size: 10px;
}


</style>
