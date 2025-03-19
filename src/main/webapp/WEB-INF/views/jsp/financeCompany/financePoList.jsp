<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<div id="page-content">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url value="/finance/financeDashboard" var="dashboardUrl" />
			<li><a href="${dashboardUrl}"> <spring:message
						code="application.dashboard" />
			</a></li>
			<li class="active">Shared PO List</li>

		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">Shared PO List</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<div class="row">
				<div class=" col-sm-6 col-md-6 marg-top-10">
					<label><spring:message code="application.filter.By.Date" /></label>
					<div class="row">
						<div class="col-md-8 col-sm-8">
							<input onfocus="this.blur()" name="dateTimeRange"
								data-date-start-date="0d" id="datepicker-date-time-nodisable"
								class="form-control for-clander-view" type="text"
								data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" />
						</div>
						<div class="col-md-4 resetBtn-padding">
							<button id="resetDate" class="btn btn-sm btn-black "
								data-toggle="tooltip" data-placement="top"
								data-original-title="Reset">
								<i aria-hidden="true" class="glyph-icon icon-close"></i> <i
									class="glyph-icon icon-cross"></i>
							</button>
						</div>
					</div>
				</div>

<!-- Remove the supplier search function According to PH-163 CR-->
				<%-- <div class="col-md-6 marg-top-10">
					<label>Supplier</label>
					<div class="row">
						<div class="col-md-8 col-sm-8">
							<select name="supplierCompanyList"
								class="chosen-select rec_inp_style2 supplierCompanyList"
								id="supplierCompanyList">
								<option value=""></option>
								<c:forEach items="${supplierList}" var="financepo">

									<option value="${financepo.supplier.id}">${financepo.supplier.companyName}</option>
								</c:forEach>
							</select>
						</div>

					</div>
				</div> --%>

					<div class="col_12">
						<div class="white_box_brd pad_all_15">
							<section class="index_table_block">
								<div class="row">
									<div class="col-xs-12">
										<div class="ph_tabel_wrapper scrolableTable_UserList">
											<table id="tableList"
												class="data  display table table-bordered noarrow"
												cellspacing="0" width="100%">
												<thead>
													
													<tr class="tableHeaderWithSearch">
														<th search-type="" class="width_100_fix"><spring:message
																code="application.action" /></th>
														<th search-type="text"><spring:message code="supplier.name" /></th>

														<th search-type="text">PO Title</th>
														<th search-type="text"><spring:message code="application.description"/></th>
														<th search-type="text"><spring:message code="buyer.dashboard.po.number"/></th>
														<th search-type="select" search-options="financePoStatus">Finance
															Status</th>
														<th search-type="" class="width_200 width_200_fix">PO
															<spring:message code="application.createddate"/></th>
														<th search-type=""
															class="align-right width_200 width_200_fix">PO GRAND
															TOTAL</th>

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

.resetBtn-padding {
	padding-top: 3px;
	padding-left: 0px;
}

.excel_icon {
	background: url(../image-resources/image-procurehere/excel-icon.png)
		no-repeat 0 0;
	width: 24px;
	height: 26px;
	display: inline-block;
	margin-right: 4px;
	vertical-align: middle;
}
</style>
<!-- <script type="text/javascript">
		$("#test-select").treeMultiselect({
			enableSelectAll : true,
			sortable : true
		});
	</script> -->
<script type="text/javascript">
	var table;
	$('document')
			.ready(
					function() {
						// Setup - add a text input to each footer cell
						var header = $("meta[name='_csrf_header']").attr(
								"content");
						var token = $("meta[name='_csrf']").attr("content");

						table = $('#tableList')
								.DataTable(
										{
											"processing" : true,
											"deferRender" : true,
											"preDrawCallback" : function(
													settings) {
												$('#loading').show();
												return true;
											},
											"drawCallback" : function() {
												// in case your overlay needs to be put away automatically you can put it here
												$("#poReportId").val(false);
												$('#loading').hide();
											},
											"serverSide" : true,
											"pageLength" : 10,
											"searching" : true,
											"ajax" : {
												"url" : getContextPath()
														+ "/finance/poListData",
												"data" : function(d) {
															d.dateTimeRange = $(
																	"input[name='dateTimeRange']")
																	.val(),
															d.selectedSupplier = $(
																	"#supplierCompanyList")
																	.val();
												},
												"error" : function(request,
														textStatus, errorThrown) {
													var error = request
															.getResponseHeader('error');
													if (error != undefined) {
														$(
																'p[id=idGlobalErrorMessage]')
																.html(
																		error != null ? error
																				.split(
																						",")
																				.join(
																						"<br/>")
																				: "");
														$(
																'div[id=idGlobalError]')
																.show();
													}
													$('#loading').hide();
												}
											},

											"order" : [],
											"columns" : [
													{
														"mData" : "id",
														"searchable" : false,
														"orderable" : false,
														"mRender" : function(
																data, type, row) {
															var action = '<a href="financePOView/' + row.po.id + '"  data-placement="top" title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>&nbsp;&nbsp;';
															action += '<a href="financePoReport/' + row.po.id + '"  data-placement="top" title="Download"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>';
															return action;
														}
													},
													{

														// supplier.fullName is an company name here.........
														"data" : "supplier.companyName",
														"defaultContent" : ""
													},
													{
														"data" : "po.name",
														"defaultContent" : ""
													},
													{
														"data" : "po.description",
														"defaultContent" : ""
													},
													{
														"data" : "po.poNumber",
														"defaultContent" : ""
													},
													{
														"data" : "statusValue",
														"orderable" : false,
														"defaultContent" : ""
													},
													{
														"data" : "po.createdDate",
														"searchable" : false,
														"type" : 'custom-date',
														"defaultContent" : ""
													},
													{
														"data" : "po.grandTotal",
														"orderable" : true,
														"className" : "align-right",
														"defaultContent" : "",
														"mRender" : function(
																data, type, row) {
															return ReplaceNumberWithCommas(row.po.grandTotal
																	.toFixed(row.po.decimal));

														}
													} ]
										});
						var htmlSearch = '<tr class="tableHeaderWithSearch">';
						$('#tableList thead tr:nth-child(1) th')
								.each(
										function(i) {
											var title = $(this).text();
											if (!(title == "Actions")
													&& $(this).attr(
															'search-type') != '') {
												if ($(this).attr('search-type') == 'select') {
													var optionsType = $(this)
															.attr(
																	'search-options');
													htmlSearch += '<th ><select data-index="'+i+'">';
													if (optionsType == 'financePoStatus') {
														htmlSearch += '<option value="ALL">All Status</option>';
														<c:forEach items="${financePoStatusList}" var="item">
														htmlSearch += '<option value="${item}">${item.value}</option>';
														</c:forEach>
														
													}
													htmlSearch += '</select></th>';
												} else {
													htmlSearch += '<th ><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
												}
											} else {
												htmlSearch += '<th style="'
														+ $(this).attr("style")
														+ '"><div style="visibility:hidden;'
														+ $(this).attr("style")
														+ '"></div></th>';
											}
										});
						htmlSearch += '</tr>';
						$('#tableList thead').append(htmlSearch);
						$(table.table().container()).on(
								'keyup',
								'thead input',
								function() {
									if ($.trim(this.value).length > 2
											|| this.value.length == 0) {
										table.column($(this).data('index'))
												.search(this.value).draw();
									}
								});
						$(table.table().container()).on(
								'change',
								'thead select',
								function() {
									table.column($(this).data('index')).search(
											this.value).draw();
								});

						$('#datepicker-date-time-nodisable').on(
								'apply.daterangepicker', function(e, picker) {
									e.preventDefault();
									table.ajax.reload();
								});

						$("#resetDate").click(
								function(e) {
									e.preventDefault();
									if ($("#datepicker-date-time-nodisable")
											.val() !== '') {
										$("#datepicker-date-time-nodisable")
												.val('');
										table.ajax.reload();
									}
								});

						$("#supplierCompanyList").change(function() {

							table.ajax.reload();

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

<script type="text/javascript"
	src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>