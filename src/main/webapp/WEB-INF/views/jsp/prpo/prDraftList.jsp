<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<spring:message var="prDraftListDesk" code="application.pr.draft.list" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${prDraftListDesk}] });
});
</script>
<div id="page-content">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url value="/buyer/buyerDashboard" var="dashboardUrl" />
			<li><a href="${dashboardUrl}"> <spring:message
						code="application.dashboard" />
			</a></li>
			<li class="active"><spring:message code="prdraft.label"/></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon"><spring:message code="prdraft.title"/></h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="row">
				<div class="col_12">
					<div class="white_box_brd pad_all_15">
						<section class="index_table_block">
							<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
							<div class="row">
								<div class="col-xs-12">
									<div class="ph_tabel_wrapper scrolableTable_UserList">
										<table id="tableList"
											class="data  display table table-bordered noarrow"
											cellspacing="0" width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="" class="width_100_fix"><spring:message
															code="application.action1" /></th>
													<th search-type="text"><spring:message code="eventdetails.event.referencenumber" /></th>
													<th search-type="text"><spring:message code="buyer.dashboard.prname" /></th> 
													<th search-type="text"><spring:message code="supplier.name" /></th>
													<th search-type="text"><spring:message code="application.description"/></th>
													<th search-type="text"><spring:message code="pr.id"/></th>
													<th search-type="text"><spring:message code="application.createdby"/></th>
													<th search-type="" class="width_200 width_200_fix"><spring:message code="application.createddate"/></th>
														
													<th search-type="text"><spring:message code="application.modifiedby"/></th>
													<th search-type="" class="width_200 width_200_fix"><spring:message code="application.modifieddate"/></th>
													<th search-type="" class="width_100 width_100_fix align-right"><spring:message code="label.currency"/></th>
													<th search-type=""
														class="align-right width_200 width_200_fix"><spring:message code="buyer.dashboard.prgrandtotal"/></th>
														
													<th search-type="text" class="align-center"><spring:message code="label.businessUnit"/></th>
														
												</tr>
											</thead>
											<tbody>
											</tbody>
										</table>
									</div>
									<div id="morris-bar-yearly" class="graph"
										style="visibility: hidden"></div>
								</div>
							</div>
						</section>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}

#tableList th {
	text-align: left;
}
</style>
<script type="text/javascript">
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
<script type="text/javascript">
	$('document').ready(function() {
		var table = $('#tableList').DataTable({
			"oLanguage":{
				"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : true,
			"deferRender" : true,
			"preDrawCallback" : function(settings) {
			//	$('div[id=idGlobalError]').hide();
				$('#loading').show();
				return true;
			},
			"drawCallback" : function() {
				$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			"searching" : true,
			"ajax" : {
				"url" : getContextPath() + "/buyer/prDraftData",
				"data" : function(d) {
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				}
			},
			"order" : [7, "desc"],
			"columns" : [ {
				"mData" : "id",
				"searchable" : false,
				"orderable" : false,
				"mRender" : function(data, type, row) {
					return '<a href="createPrDetails/' + row.id + '" title=<spring:message code="tooltip.edit" />><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';

				}
			}, {
				"data" : "referenceNumber",
				"defaultContent" : ""
			}, {
				"data" : "name",
				"defaultContent" : ""
			}, {
				"data" : "supplier.fullName",
				"defaultContent" : ""
			}, {
				"data" : "description",
				"defaultContent" : ""
			}, {
				"data" : "prId",
				"defaultContent" : ""
			}, {
				"data" : "createdBy.name",
				"defaultContent" : ""
			}, {
				"data" : "prCreatedDate",
				"searchable" : false,
				"defaultContent" : ""
			}, {
				"data" : "modifiedBy.name",
				"defaultContent" : ""
			}, {
				"data" : "modifiedDate",
				"searchable" : false,
				"defaultContent" : "",
			}, {
				"data" : "currency.currencyCode",
				"className" : "align-right",
				"searchable" : false,
				"defaultContent" : ""
				
			},{
				"data" : "grandTotal",
				"className" : "align-right",
				"defaultContent" : "",
				"mRender" : function(data, type, row) {
					return ReplaceNumberWithCommas(row.grandTotal.toFixed(row.decimal));
				}
			} , {
				"data" : "businessUnit.unitName",
				"defaultContent" : ""
			}],
			"initComplete": function(settings, json) {
		var htmlSearch = '<tr class="tableHeaderWithSearch">';
		$('#tableList thead tr:nth-child(1) th').each(function(i) {
			var title = $(this).text();
			if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
			} else {
				htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
			}
		});
		htmlSearch += '</tr>';
		$('#tableList thead').append(htmlSearch);
		$(table.table().container()).on('keyup', 'thead input', function() {
			if ($.trim(this.value).length > 2 || this.value.length == 0) {
				table.column($(this).data('index')).search(this.value).draw();
			}
		});
		$(table.table().container()).on('change', 'thead select', function() {
			table.column($(this).data('index')).search(this.value).draw();
		});
		}
		});
	});
	function ReplaceNumberWithCommas(yourNumber) {
		var n = yourNumber.toString().split(".");
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		return n.join(".");
	}
</script>