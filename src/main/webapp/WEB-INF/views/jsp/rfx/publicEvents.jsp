<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/datatable/datatable.css"/>">

<style>

.logo-center {
    margin: 0 auto;
    display: block;
    padding: 20px 0;
 }
</style>

<input type="hidden" id="tenantId" name="tenantId" value="${tenantId}" />
<div id="page-content-wrapper">
<div id="page-content">
<div class="container">
<div class="Invited-Supplier-List-table pad_all_15 add-supplier">
	<div class="ph_table_border">
		<div class="row">
			<div class="col_12">
				<div class="white_box_brd pad_all_15">
					<section class="index_table_block">
						<header class="form_header">
							<form:errors path="*" cssClass="error" />
							<c:if test="${not empty errors}">
								<span class="error">${errors}</span>
							</c:if>
							<c:if test="${not empty logoImg}">
								<img id="logoImageHolder" class="logo-center" src="data:image/jpeg;base64,${logoImg}" alt="Logo" />
							</c:if>
							<h5 class="pr-5 fw-bold">Announcements</h5>		
							<hr>
							<c:if test="${not empty announcementList}">		
								<c:forEach items="${announcementList}" var="announcement" varStatus="loop">
									<div class="row d-flex">
										<span class="pr-5">${loop.index + 1}.</span>
										<span class="mb-0">${announcement.publicOrEmailContent}</span>
									</div>
									<div class="row announce ann-start">
										<span class="pr-5"><b>Announcement Date:</b></span>
										<fmt:formatDate value="${announcement.announcementStart}" pattern="dd/MM/yyyy hh:mm a" timeZone="${timezone}"/>
									</div>
								</c:forEach>
							</c:if>
							<c:if test="${empty announcementList}">
								<div class="row d-flex">
									<Span class="mb-0">No data available</Span>
								</div>
							</c:if>	
							<hr>
							<h5 class="pr-5 fw-bold mt-30"> Published and Ongoing Events</h5>														
							<div class="row">
								<div class="col-xs-12">
									<div class="ph_tabel_wrapper scrolableTable_UserList">
									<table id="tableList" class=" display table table-bordered noarrow" cellspacing="0"width="100%">
											<thead>
												<tr class="tableHeaderWithSearch">
													<th search-type="text">Reference Number</th>
													<th search-type="text">Name of Event</th>
													<th search-type="text">Business Unit</th>
													<th search-type="text">Event Category</th>
													<th search-type="select" search-options="eventTypeList">Event Type</th>
													<th search-type="">Participation Fee</th>
													<th search-type="">Start Date</th>
													<th search-type="">End Date</th>
													<th search-type="">Site Visit</th>
												</tr>
											</thead>
										</table>
									</div>
									<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
								</div>
							</div>
						</header>
					</section>
				</div>
			</div>
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
</style>

<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}
#main {
    float: none;
}
</style>
<script src="<c:url value="/resources/assets/js-core/nested-sortable-tests.js"/>"></script>
<script type="text/javascript">
	$('document')
			.ready(
					function() {
						var tenantId = $('#tenantId').val();

						var table = $('#tableList')
								.DataTable(
										{

											"processing" : false,
											"deferRender" : true,
											"preDrawCallback" : function(settings) {
												$('#loading').show();
												console.log("preDrawCallback");
												return true;
											},
											"drawCallback" : function() {
												// in case your overlay needs to be put away automatically you can put it here
												console.log("drawCallback");
												$('#loading').hide();
											},
											"serverSide" : true,
											//"pageLength" : 20,  
											"paging" : false,
											"searching" : true,
											"ajax" : {
												"url" : getContextPath() + "/publicEventsListData/" + tenantId,
												"data" : function(d) {
												}
											},
											"order" : [],
											"columns" : [
													{
														"data" : "referanceNumber",
														"className" : "align-left",
														"mRender" : function(data, type, row) {

															return '<a  href="${pageContext.request.contextPath}/viewPublicEventSummary/'+row.type+'/'+row.id+'/'+row.buyerId+'" data-toggle="tooltip" data-placement="top" title="View Details" class="viewSuppBtn pull-left algn-actn" aria-hidden="true">'+ row.referanceNumber +'</a>';
														}
														
													}, {
														"data" : "eventName",
														"className" : "align-left",
														"defaultContent" : ""
													},  {
														"data" : "unitName",
														"className" : "align-left",
														"defaultContent" : ""
													},{
														"data" : "industryCategoriesNames",
														"className" : "align-left",
														"defaultContent" : "",
													},{
														"data" : "type",
														"className" : "align-left",
														"defaultContent" : ""
													},{
													"data" : "participationFees",
													"className" : "align-left",
													"defaultContent" : "",
													"render": function (data, type, row) {
														// Assuming data is a BigDecimal value
														var formattedValue = row.currencyCode+' '+ parseFloat(data).toFixed(2); // Format as RM 0.00
														return formattedValue;
													}
												    },{
														"data" : "eventStart",
														"className" : "align-left",
														"defaultContent" : ""
													}, {
														"data" : "eventEndDate",
														"className" : "align-left",
														"defaultContent" : ""
													}, {
														"data" : "siteVisitMeetingDetails",
														"className" : "align-left",
														"defaultContent" : "",
														"orderable": false
													}]
										});

						var htmlSearch = '<tr class="tableHeaderWithSearch">';
						$('#tableList thead tr:nth-child(1) th').each(function(i) {
							var title = $(this).text();
							if (!(title == "Actions") && $(this).attr('search-type') != '') {
								if ($(this).attr('search-type') == 'select') {
									var optionsType = $(this).attr('search-options');
									htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><select data-index="'+i+'"><option value="">Search '+ title+ '</option>';
									if (optionsType == 'eventTypeList') {
										<c:forEach items="${eventTypeList}" var="item">
										htmlSearch += '<option value="${item}">${item}</option>';
										</c:forEach>
									}
									htmlSearch += '</select></th>';
								} else {
									htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><input type="text" class="border search-placeholder" placeholder="Search '+title+'"  data-index="'+i+'" /></th>';
								}
							} else {
								htmlSearch += '<th class="align-left" style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
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

					});
</script>
 <style>
/* #tablePubEventsList td {
	text-align: center;
}

.table.dataTable>tbody>tr>td {
	padding-left: 0px !important;
}

.tableHeaderWithSearch:first-child>th {
	padding: 10px 0px 5px 0px !important;
	line-height: 1.4 !important;
	border: none !important;
}

.table>tbody>tr>td {
	padding: 5px 5px;
}

.tableHeaderWithSearch:nth-child(2)>th {
	padding: 0px 10px 5px 0px !important;
	line-height: 1 !important;
	border: none !important;
}

.algn-actn {
	z-index: 999;
	padding-left: 40%;
	padding-top: 7px;
}

.neg-marg {
	margin-top: -50px;
}

.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: hidden !important;
}

.dynamic_align {
	text-align: left;
}


.border {
	border: 1px solid #08080e5e;
} --> */
@media screen and (max-width: 768px) {
	.scrolableTable_UserList>div.dataTables_wrapper {
		overflow: auto !important;
	}
}

.d-flex {
	display: flex;
	padding-left: 35px;
}
.pr-5 {
	padding-right: 5px;
}
.mb-0 p {
	margin-bottom: 0;
}
.announce {
    padding-left: 52px;
    margin-bottom: 8px;
}
.announce .pr-5 {
	color: #000;
}

.fw-bold {
	font-weight: bold;
}
.mt-2rem {
	margin-top: 2rem;
}
/* h1, h2, h3, h4,  */
h5, h6 {
    color: #424242;
    font-family: inherit !important;
}
/*
b, strong {
    font-family: inherit !important;    
}
 */
 .mt-30 {
	margin-top: 30px;
}
#tableList th {
	font-weight: 550;
}
.search-placeholder {
	font-weight: 505;
}
</style> 
<!-- Required for datatable to make its server side ajax params Spring/Java friendly -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datatable/datatable.js"/>"></script> 
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datatable/datatable-bootstrap.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datatable/datatable-tabletools.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.spring-friendly.js"/>"></script>
