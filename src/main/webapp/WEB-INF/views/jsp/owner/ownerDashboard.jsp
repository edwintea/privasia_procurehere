<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ page import="java.util.Date"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<link rel="stylesheet" href="<c:url value="/resources/assets/icons/fontawesome/font-awesome.min.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/websockets/sockjs.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/websockets/stomp.js"/>"></script>
<spring:message var="ownerDashboardDesk" code="application.owner.dashboard" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${ownerDashboardDesk}] });
});
 
 $(document).ready(function() {

		$('ul.tabs li').click(function() {
			var tab_id = $(this).attr('data-tab');

			$('ul.tabs li').removeClass('current');
			$('.tab-content').removeClass('current');

			$(this).addClass('current');
			$("#" + tab_id).addClass('current');
		})

	})
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<li>
					<a href="${pageContext.request.contextPath}/owner/ownerDashboard">
						<spring:message code="application.dashboard" />
					</a>
 				</li>
			</ol>
			<div class="clear"></div>
			<div class="home_tab_wrap">
				<ul class="tabs">
					<li href="#" class="tab-link current" data-tab="tab-1">
						<spring:message code="application.home" />
					</li>
					<li class="tab-link" data-tab="tab-2">
						<spring:message code="owner.metric" />
					</li>
				</ul>
			</div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<form:form cssClass="form-horizontal bordered-row" action="ownerDashboard" method="post" modelAttribute="dashboardObject">
				<div id="tab-1" class="tab-content current doc-fir tab-main-inner pad_all_15" style="display: none;">
					<div class="Section-title title_border marg-bottom-10 white-bg">
						<div class="metric_tfild_wrap">
							<div class="metric_country">
								<div class="row">
									<label>
										<spring:message code="application.selectcountry" />
									</label>
									<form:select path="country" data-validation="required" cssClass="form-control chosen-select" id="regCountry" onchange="javascript:this.form.submit();">
										<form:option value="">All Countries</form:option>
										<form:options items="${countryList}" itemValue="id" itemLabel="countryName" />
									</form:select>
								</div>
							</div>
						</div>
						<div class="clear"></div>
					</div>
					<section class="Overview_section">
						<div class="ph_row">
							<div class="ph_col-3">
								<article class="overview_box pending_item ">
									<div class="box_top white_box_brd">
										<div class="databox_1 item_data">
											<h3>${dashboard.pendingBuyers}</h3>
											<span>
												<spring:message code="owner.buyerpending" />
											</span>
										</div>
										<div class="databox_1 item_data">
											<h3>${dashboard.pendingSuppliers}</h3>
											<span>
												<spring:message code="owner.supplierpending" />
											</span>
										</div>
										<span class="pos_item">
											<i class="glyph-icon icon-ban"></i>
										</span>
									</div>
									<div class="box_footer">
										<span class="b_title">
											<spring:message code="owner.pending" />
										</span>
										<span class="item_icon">
											<i class="glyph-icon icon-ban"></i>
										</span>
									</div>
								</article>
							</div>
							<div class="ph_col-3">
								<article class="overview_box approve_item ">
									<div class="box_top white_box_brd">
										<div class="databox_1 item_data">
											<h3>${dashboard.activeBuyers}</h3>
											<span>
												<spring:message code="owner.approvedbuyer" />
											</span>
										</div>
										<div class="databox_1 item_data">
											<h3>${dashboard.activeSuppliers}</h3>
											<span>
												<spring:message code="owner.approvedsupplier" />
											</span>
										</div>
										<span class="pos_item">
											<i class="glyph-icon icon-check"></i>
										</span>
									</div>
									<div class="box_footer">
										<span class="b_title">
											<spring:message code="owner.approved" />
										</span>
										<span class="item_icon">
											<i class="glyph-icon icon-check"></i>
										</span>
									</div>
								</article>
							</div>
							<div class="ph_col-3">
								<article class="overview_box rejected_item ">
									<div class="box_top white_box_brd">
										<div class="databox_1 item_data">
											<h3>${dashboard.suspendedBuyers}</h3>
											<span>
												<spring:message code="owner.suspendedsupplier" />
											</span>
										</div>
										<div class="databox_1 item_data">
											<h3>${dashboard.rejectedSuppliers}</h3>
											<span>
												<spring:message code="owner.rejectedsupplier" />
											</span>
										</div>
										<span class="pos_item">
											<i class="glyph-icon icon-lock"></i>
										</span>
									</div>
									<div class="box_footer">
										<span class="b_title">
											<spring:message code="owner.rejected" />
										</span>
										<span class="item_icon">
											<i class="glyph-icon icon-lock"></i>
										</span>
									</div>
								</article>
							</div>
							<div class="ph_col-3 ph_col_last">
								<article class="overview_box trial_item">
									<div class="box_top white_box_brd ">
										<div class="databox_1 item_data">
											<div class="table_block">
												<div class="table_cell">
													<h3>${trailBuyer}</h3>
													<span>
														<spring:message code="owner.trail" />
													</span>
												</div>
											</div>
										</div>
										<span class="pos_item">
											<i class="glyph-icon icon-users"></i>
										</span>
									</div>
									<div class="box_footer">
										<span class="b_title">
											<spring:message code="owner.appliedtrail" />
										</span>
										<span class="item_icon">
											<i class="glyph-icon icon-users"></i>
										</span>
									</div>
								</article>
							</div>
						</div>
					</section>
					<div class="clear"></div>
					<section class="Section_row_chart">
						<div class="ph_row">
							<div class="ph_col_4">
								<section class="circle_chart white_box_brd pad_all_22">
									<div class="inner_box">
										<h2 class="widget_title">
											<spring:message code="owner.totalregistered" />
										</h2>
										<div id="circle_chart_box">
											<div class="chart-alt-10" data-percent="${totalBuyer}">
												<span class="big_num">${totalBuyer}</span>
											</div>
										</div>
										<div class="total_value">
											<span>
												<spring:message code="owner.todayregistered" />
											</span>
											<h3>${regToday}</h3>
										</div>
										<div class="f_value_row ph_row">
											<div class="ph_col_6">
												<span class="txt_label">
													<spring:message code="owner.weekregistered" />
												</span>
												<span class="txt_num">${reqLastWeek}</span>
											</div>
											<div class="ph_col_6">
												<span class="txt_label">
													<spring:message code="owner.monthregistered" />
												</span>
												<span class="txt_num">${reqLastMonth}</span>
											</div>
										</div>
									</div>
								</section>
							</div>
							<div class="ph_col_8">
								<section class="bar_charts_section white_box_brd pad_all_22">
									<div class="widget_title">
										<div class="col-md-12">
											<div class="col-md-2">
												<h3>
													<label>
														<spring:message code="owner.statistics" />
													</label>
												</h3>
											</div>
											<div class="col-md-3">
												<select id="statSel" name="statistics" class="form-control disablesearch chosen-select">
													<option value="1" selected="selected">Revenue</option>
													<option value="2">Subscription</option>
												</select>
											</div>
											<div class="col-md-7">
												<div class="ph_pill_tabs">
													<ul class="nav nav-pills ph_tabs">
														<li>
															<a href="#home" id="weekBar">
																<spring:message code="owner.weekbar" />
															</a>
														</li>
														<li>
															<a href="#menu1" id="monthBar">
																<spring:message code="owner.monthbar" />
															</a>
														</li>
														<li>
															<a href="#menu2" id="quaterBar">
																<spring:message code="owner.quarterbar" />
															</a>
														</li>
														<li class="active">
															<a href="#menu4" id="anualBar">
																<spring:message code="owner.annualbar" />
															</a>
														</li>
													</ul>
												</div>
											</div>
										</div>
									</div>
									<div class="tab-content">
										<div id="graph_loader" style="display: none" class="opacity-60">
											<img src="<c:url value="/resources/images/ajaxloader.gif"/>" />
										</div>
										<div id="home" class="tab-pane fade ">
											<div class="example-box-wrapper">
												<div id="morris-bar-weekly" class="graph"></div>
											</div>
										</div>
										<div id="menu1" class="tab-pane fade ">
											<div class="example-box-wrapper">
												<div id="morris-bar-monthly" class="graph"></div>
											</div>
										</div>
										<div id="menu2" class="tab-pane fade">
											<div class="example-box-wrapper">
												<div id="morris-qtr" class="graph"></div>
											</div>
										</div>
										<div id="menu3" class="tab-pane fade">
											<div class="example-box-wrapper">
												<div id="morris-half-year" class="graph"></div>
											</div>
										</div>
										<div id="menu4" class="tab-pane active in">
											<div class="example-box-wrapper">
												<div id="morris-bar-yearly" class="graph"></div>
											</div>
										</div>
									</div>
								</section>
							</div>
						</div>
					</section>
			</form:form>
			<div class="clear"></div>
			<div class="container-fluid mydiv">
				<div class="row">
					<div class="col_12">
						<div class="white_box_brd">
							<section class="index_table_block">
								<h2 class="block_title pad_all_15">
									<spring:message code="owner.newbuyercompany" />
								</h2>
								<div class="row">
									<div class="col-xs-12">
										<div class="col-md-2 col-sm-2 col-xs-2">
											<label>
												<spring:message code="owner.number" />
											</label>
											<select class="disablesearch chosen-select" name="" data-parsley-id="0644" id="selectSession">
												<option value="10">10</option>
												<option value="20">20</option>
												<option value="30">30</option>
											</select>
										</div>
										<div class="ph_tabel_wrapper pad_all_15">
											<table id="datatable-example" class="ph_table display table table-bordered noarrow" cellspacing="0" width="100%">
												<thead>
													<tr>
														<th><spring:message code="buyercreation.company" /></th>
														<th><spring:message code="owner.registereddate" /></th>
														<th><spring:message code="owner.registercountry" /></th>
														<th><spring:message code="supplier.registration.company.number" /></th>
													</tr>
												</thead>
												<tbody>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</section>
						</div>
					</div>
				</div>
			</div>
			<div></div>
			<!-- - -->
			<div class="container-fluid">
				<div class="row">
					<div class="col_12">
						<div class="white_box_brd">
							<section class="index_table_block">
								<h2 class="block_title pad_all_15">
									<spring:message code="owner.newsuppliercompany" />
								</h2>
								<div class="row">
									<div class="col-xs-12">
										<div class="ph_tabel_wrapper pad_all_15">
											<table id="datatable-example1" class="ph_table display table table-bordered noarrow" cellspacing="0" width="100%">
												<thead>
													<tr>
														<th><spring:message code="buyercreation.company" /></th>
														<th><spring:message code="owner.registereddate" /></th>
														<th><spring:message code="owner.registercountry" /></th>
														<th><spring:message code="supplier.registration.company.number" /></th>
													</tr>
												</thead>
											</table>
										</div>
									</div>
								</div>
							</section>
						</div>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="ownerDashboardMetrics.jsp"></jsp:include>
	</div>
</div>
</div>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript">
	/* Datepicker bootstrap */

	$(function() {
		"use strict";
		var nowTemp = new Date();
		var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

		$('#endDate').bsdatepicker({
			format : 'dd/mm/yyyy',
			minDate : now,
			onRender : function(date) {
				return date.valueOf() > now.valueOf() ? 'disabled' : '';
			}

		});

	})
</script>
<script>
	$('document').ready(function() {

		//$('#datatable-example').dataTable();
		//$('#datatable-example1').dataTable();
		$('.dataTables_filter input[type="search"]').attr('placeholder', 'Search Company').css({
			'width' : '250px',
			'display' : 'inline-block'
		});

	});
</script>
<script type="text/javascript">
	//$('document').ready(function () {

	function returnNext() {
		//debugger;
		var paths = location.pathname.split('/');
		paths[paths.length - 1] = 'Mail_Inbox.html'; //'prohere-admin-step1.html'; // new value
		location.pathname = paths.join('/');

	}
	// });
</script>
<style>
#graph_loader {
	background: #fff none repeat scroll 0 0;
	height: 100%;
	left: 0;
	position: absolute;
	top: 0;
	width: 100%;
	z-index: 5555;
}

#graph_loader img {
	padding-left: 45%;
	padding-top: 33%;
}

#statSel_chosen {
	height: 36px;
}

#statSel_chosen a div {
	height: 34px;
}

.dataTable th {
	width: 200px !important;
	//
	ie
	fix
}
</style>
<script>
	$('document').ready(function() {

		//	$('#datatable-example').dataTable();
		//	$('#datatable-example1').dataTable();
		$('.dataTables_filter input[type="search"]').attr('placeholder', 'Search Company').css({
			'width' : '250px',
			'display' : 'inline-block'
		});

		$('.dataTables_filter input[type="search"]').attr('placeholder', 'Search Company').css({
			'width' : '250px',
			'display' : 'inline-block'
		});

		//	$('#datatable-example').dataTable();
		//	$('#datatable-example1').dataTable();
		$('.dataTables_filter input[type="search"]').attr('placeholder', 'Search Company').css({
			'width' : '250px',
			'display' : 'inline-block'
		});

		$('.dataTables_filter input[type="search"]').attr('placeholder', 'Search Company').css({
			'width' : '250px',
			'display' : 'inline-block'
		});
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
