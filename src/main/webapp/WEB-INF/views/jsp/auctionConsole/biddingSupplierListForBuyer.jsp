<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('BUYER')">
	<div class="">
		<div class="row">
			<div class="col-md-12">
				<div class="col-md-6 pull-left divSupplierList">
					<img src="${pageContext.request.contextPath}/resources/images/icon/Icon2/online2.png"><span class="spanSupplierList"><spring:message code="rfaevent.online" /></span>&nbsp;&nbsp;&nbsp; <img src="${pageContext.request.contextPath}/resources/images/icon/Icon2/away2.png"><span class="spanSupplierList"><spring:message code="rfaevent.away" /></span>&nbsp;&nbsp;&nbsp; <img src="${pageContext.request.contextPath}/resources/images/icon/Icon2/offline2.png"><span
						class="spanSupplierList"><spring:message code="rfaevent.not.logged.in" /></span>
				</div>
			</div>
		</div>
		<table id="tableList" class="display table table-bordered noarrow" cellspacing="0" width="98%">
			<thead>
				<tr class="tableHeaderWithSearch">
					<th class="width_50 width_50_fix f-w-b "><spring:message code="application.case.status" /></th>
					<c:if test="${((event.status eq 'SUSPENDED' or event.status eq 'ACTIVE' or event.status eq 'CLOSED' or event.status eq 'COMPLETE' or event.status eq 'FINISHED' ) and  (event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH')) or ((event.status eq 'COMPLETE' or event.status eq 'CLOSED' or event.status eq 'FINISHED') and (event.auctionType eq 'FORWARD_SEALED_BID' or event.auctionType eq 'REVERSE_SEALED_BID') and (auctionRules.buyerAuctionConsoleRankType ne 'SHOW_NONE')) }">
						<th class="width_50 width_50_fix f-w-b"><spring:message code="application.case.rank" /></th>
					</c:if>
					<th class="width_200 text-left f-w-b"><spring:message code="application.case.bidders" /></th>
					<c:if test="${((event.status eq 'SUSPENDED' or event.status eq 'ACTIVE' or event.status eq 'CLOSED' or event.status eq 'COMPLETE' or event.status eq 'FINISHED' ) and  (event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH')) or ((event.status eq 'COMPLETE' or event.status eq 'CLOSED' or event.status eq 'FINISHED') and (event.auctionType eq 'FORWARD_SEALED_BID' or event.auctionType eq 'REVERSE_SEALED_BID')) }">
						<th class="width_100 f-w-b "><spring:message code="auctionrules.case.number.bids" /></th>
						<th class="width_100 align-right f-w-b"><spring:message code="auctionrules.case.initial.price" /></th>
						<th class="width_100 align-right f-w-b"><spring:message code="auctionrules.case.current.price" /></th>
						<th class="width_50 f-w-b">% <spring:message code="rfaevent.case.diff" /></th>

						<c:if test="${(event.autoDisqualify eq 'false') and (event.status eq 'ACTIVE') and !(eventPermissions.viewer or buyerReadOnlyAdmin)}">
							<th class="width_50 f-w-b"><spring:message code="rfa.disqualify.supplier" /></th>
						</c:if>
					</c:if>
				</tr>
			</thead>
		</table>
	</div>
</sec:authorize>
<style>
.f-w-b {
	font-weight: bold !important;
}

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
	var suppliersListConsole = null;
	$('document').ready(function() {
		var eventId = $('#eventId').val();
		suppliersListConsole = $('#tableList').DataTable({
			"processing" : false,
			"deferRender" : true,
			"serverSide" : false,
			"paging" : false,
			"info" : false,
			"searching" : false,
			"ajax" : {
				"url" : getContextPath() + '/buyer/refreshAuctionConsole/' + eventId,
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
			<c:if test="${((event.status eq 'SUSPENDED' or event.status eq 'ACTIVE' or event.status eq 'CLOSED' or event.status eq 'COMPLETE' or event.status eq 'FINISHED' ) and  (event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH')) or ((event.status eq 'COMPLETE' or event.status eq 'CLOSED' or event.status eq 'FINISHED') and (event.auctionType eq 'FORWARD_SEALED_BID' or event.auctionType eq 'REVERSE_SEALED_BID') and (auctionRules.buyerAuctionConsoleRankType ne 'SHOW_NONE')) }">
			
			,{
				"mData" : "rankOfSupplier",
				"searchable" : false,
				"orderable" : true,
				"defaultContent" : ""
			} 
			</c:if>
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
			<c:if test="${((event.status eq 'SUSPENDED' or event.status eq 'ACTIVE' or event.status eq 'CLOSED' or event.status eq 'COMPLETE' or event.status eq 'FINISHED' ) and  (event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH')) or ((event.status eq 'COMPLETE' or event.status eq 'CLOSED' or event.status eq 'FINISHED') and (event.auctionType eq 'FORWARD_SEALED_BID' or event.auctionType eq 'REVERSE_SEALED_BID')) }">
			,{
				"mData" : "numberOfBids",
				"searchable" : false,
				"orderable" : false,
				"defaultContent" : ""
			}, {
				"mData" : "initialPrice",
				"searchable" : false,
				"orderable" : false,
				"className" : "text-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					if (row.initialPrice === "" || row.initialPrice === undefined) {
						return row.initialPrice;
					} else {
						return ReplaceNumberWithCommas(row.initialPrice.toFixed($('#eventDecimal').val()));
					}
				}
			}, {
				"mData" : "currentPrice",
				"searchable" : false,
				"orderable" : true,
				"className" : "text-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					if (row.currentPrice === "" || row.currentPrice === undefined) {
						return row.currentPrice;
					} else {
						return ReplaceNumberWithCommas(row.currentPrice.toFixed($('#eventDecimal').val()));
					}

				}
			}, {
				"mData" : "differencePercentage",
				"searchable" : false,
				"orderable" : false,
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					if (row.differencePercentage === "" || row.differencePercentage === undefined) {
						return row.differencePercentage;
					} else {
						return row.differencePercentage.toFixed(2);
					}

				}
			}
			</c:if>
			<c:if test="${(event.autoDisqualify eq 'false' )  and (event.status eq 'ACTIVE') and !(eventPermissions.viewer or buyerReadOnlyAdmin) }">
			,{
				"mData" : "id",
				"searchable" : false,
				"orderable" : false,
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					if(!row.isDisqualify){
						var html = '';
						html += '<a   class="idDisqualifySupplier btn btn-danger disqualify-supplier"  data-id="'+ row.supplierId+'" data-toggle="modal" id="idDisqualifySupplier" title="Disqualify Supplier">Disqualify</a>';
						return html;
					}else{
						var html = '';
						html += '<a   class="btn btn-default disqualified-supplier"    title="'+ row.disqualifyRemarks+'">Disqualified</a>';
						return html;
						
						}
				}
			}
			</c:if>

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
	
	
	
	$(document).on("click", ".idDisqualifySupplier", function () {
	     var id = $(this).data('id');
	    
	     $(".supid").val( id );
	     
	     $('#confirmDisqualifysupllier').modal('show');
	});
	
	
</script>
<style>
.spanSupplierList {
	margin-left: 5px;
}

.divSupplierList {
	padding-top: 2px;
	padding-bottom: 2px;
}

.divSupplierList>span {
	font-size: 10px;
}

.disqualify-supplier {
	border-radius: 10px;
	padding: 0 9% 0 9%;
}

.disqualified-supplier {
	border-radius: 10px;
	border: 1px solid #ccc;
}
</style>




<div class="modal fade" id="confirmDisqualifysupllier" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="bidsupplier.confirm.disqualify" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal" onclick="javascript:$('#idSuspendForm').get(0).reset();">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="bidsupplier.sure.disqualify" />
				</label>
			</div>
			<form id="disqualifySupplierId"  method="post">
			<%-- <form action="${pageContext.request.contextPath}/buyer/disqualifySupplier" method="post"> --%>
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input name="supplierId" type="hidden" class="supid"> <input name="eventId" type="hidden" value="${event.id}">
				<div class="form-group col-md-6">
					<textarea rows="4" name="remark" data-validation="required length" data-validation-length="max500" class="form-control width-100" placeholder='<spring:message code="rfaevent.enter.remarks" />'></textarea>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="button" id="clickdisqualifySupplier" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">
						<spring:message code="application.yes" />
					</button>
					<button type="button" id="clickNoId" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" onclick="javascript:$('#idSuspendForm').get(0).reset();">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

<script type="text/javascript">
	$('document').ready(function() {
		
		$('#clickdisqualifySupplier').on('click', function(e) {
			e.preventDefault();
			if($("#disqualifySupplierId").isValid()) {
				$(this).addClass('disabled');
				$('#clickNoId').addClass('disabled');
				$('#disqualifySupplierId').attr('action', getContextPath() + '/buyer/disqualifySupplier');
				$("#disqualifySupplierId").submit();
			} else {
				return;
			}

		});
	})
</script>