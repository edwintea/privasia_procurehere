<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication var="lastLoginTime" property="principal.lastLoginTime" />
<spring:message var="buyerDashboardDesk" code="application.buyer.dashboard" />
<sec:authentication property="principal.isBuyerErpEnable" var="isBuyerErpEnable" />
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />

<div id="page-content-wrapper">
    <div id="page-content">
        <div class="container">
            <div class="Section-title title_border gray-bg">
                <h2 class="trans-cap Events-Listing-heading">
                    <spring:message code="defultMenu.suppliers.performance.analytics" />
                </h2>
            </div>
            <!-- page title block -->
            <div class="row clearfix">
                <div class="">
                    <jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
                    <div class="marg-top-10" style="margin-bottom: 1%;">
						<label class="mr-15"><spring:message code="application.filter.By.Date" /></label>
						<div class="row margin-bottom-10">
							<div class="col-md-5 col-sm-8 col-lg-4" style="padding-right: 0px;">
								<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepickerAnalytics" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" />
							</div>
							<div class="col-md-2 col-sm-4" style="padding-left: 0px;">
								<spring:message code="application.reset" var="resetLabel" />
								<button type="button" id="resetDate" class="btn btn-sm btn-black " data-toggle="tooltip" data-placement="top" data-original-title="${resetLabel}" style="height: 37px;">
									<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
								</button>
							</div>
						</div>
						
					    <div id="overallTab" class="show">
							<div class="row margin-top-10">
			                    <div class="col-sm-12">
			                        <div class="card">
			                            <div class="card-header">
			                                <h5 class="ellipse-text">Top 5 High Performance Supplier 
			                                </h5>
			                                <span class="icon-span">
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard icon-left" id="highPerformanceSuppPieIconId"></i>
			                                    </span>
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right" id="highPerformanceSuppBarIconId"></i>
			                                    </span>
			                                </span>
			                            </div>
			                            <div class="card-block">
			                                <div class="row marg-top-10" >
			                                    <div class="col-md-7">
			                                        <div id="highPerformanceSuppBarChart" style="width:100%; height:500px;"></div>
			                                    </div>
			                                    <div class="col-md-5">
			                                        <div class="panel">
			                                            <div class="panel-head">
			                                                <h3 class="title-hero">
			                                                    Top 5 High Performance Supplier<br>
			                                                </h3>
			                                            </div>
			                                            <div class="panel-body">
		                                                    <table class="table table-bordered">
		                                                        <thead>
		                                                            <tr>
		                                                                <th class="">Rank</th>
		                                                                <th class="width-200">Supplier</th>
		                                                                <th class="width-150">Overall Score (%)</th>
		                                                                <th class="">Rating</th>
		                                                            </tr>
		                                                        </thead>
		                                                        <tbody id="rfsCategoryByVolumeDataTable"></tbody>
		                                                    </table>
			                                            </div>
			                                        </div>
			                                    </div>
			                                </div>
			                            </div>
			                        </div>
			                    </div>
			                </div>
			                
			                <div class="row margin-top-10">
			                    <div class="col-sm-12">
			                        <div class="card">
			                            <div class="card-header">
			                                <h5 class="ellipse-text">Top 5 High Performance Supplier By Business Unit
			                                </h5>
			                                <span class="icon-span">
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard icon-left" id="highPerformanceSuppByBUPieIconId"></i>
			                                    </span>
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right" id="highPerformanceSuppByBUBarIconId"></i>
			                                    </span>
			                                </span>
			                            </div>
			                            <div class="card-block">
			                                <div class="row marg-top-10">
			                                    <div class="col-md-7">
			                                        <div id="highPerformanceSuppByBUBarChart" style="width:100%; height:500px;"></div>
			                                    </div>
			                                    <div class="col-md-5">
			                                    	<div style="padding-left: 0px; padding-right: 0px;" class="marg-bottom-10">
														<select class="form-control chosen-select" id="idBusinessUnit">
															<c:forEach items="${businessUnitList}" var="bu">
                               									 <option value="${bu.id}">${bu.displayName}</option>
                            								</c:forEach>
														</select>
													</div>
			                                        <div class="panel">
			                                            <div class="panel-head">
			                                                <h3 class="title-hero">
			                                                    Top 5 High Performance Supplier By Business Unit<br>
			                                                </h3>
<!-- 			                                                    <h5 class="title-subtext rfsVolumeByBU-heading"></h5> -->
			                                            </div>
			                                            <div class="panel-body">
			                                                <div class="example-box-wrapper" >
			                                                    <table class="table table-bordered ">
			                                                        <thead>
			                                                            <tr>
			                                                                <th class="">Rank</th>
			                                                                <th class="width-200">Supplier</th>
			                                                                <th class="width-150">Overall Score (%)</th>
			                                                                <th class="">Rating</th>
			                                                            </tr>
			                                                        </thead>
			                                                        <tbody id="rfsVolumeByBusinessUnitDataTable"></tbody>
			                                                    </table>
			                                                </div>
			                                            </div>
			                                        </div>
			                                    </div>
			                                </div>
			                            </div>
			                        </div>
			                    </div>
			                </div>
			                <div class="row margin-top-10">
			                    <div class="col-sm-12">
			                        <div class="card">
			                            <div class="card-header">
			                                <h5 class="ellipse-text">Top 5 High Performance Supplier by Category
			                                </h5>
			                                <span class="icon-span">
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard icon-left" id="topHighPerformanceSuppByCatPieIconId"></i>
			                                    </span>
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right" id="topHighPerformanceSuppByCatBarIconId"></i>
			                                    </span>
			                                </span>
			                            </div>
			                            <div class="card-block">
			                                <div class="row marg-top-10" >
			                                    <div class="col-md-7">
			                                        <div id="topHighPerformanceSuppByCatBarChart" style="width:100%; height:500px;"></div>
			                                    </div>
			                                    <div class="col-md-5">
			                                   		<div style="padding-left: 0px; padding-right: 0px; " class="marg-bottom-10">
														<select class="form-control chosen-select" id="idPcList">
															<c:forEach items="${procureherementCategoryList}" var="pc">
                               									 <option value="${pc.id}">${pc.procurementCategories}</option>
                            								</c:forEach>
														</select>
													</div>
			                                        <div class="panel margin-top-10">
			                                            <div class="panel-head">
			                                                <h3 class="title-hero">
			                                                    Top 5 High Performance Supplier by Category <br>
			                                                </h3>
<!-- 			                                                    <h5 class="title-subtext rfxVolByCat-head"></h5> -->
			                                            </div>
			                                            <div class="panel-body">
			                                                <div class="example-box-wrapper" >
			                                                    <table class="table table-bordered ">
			                                                        <thead>
			                                                            <tr>
			                                                                <th class="">Rank</th>
			                                                                <th class="width-200">Supplier</th>
			                                                                <th class="width-150">Overall Score (%)</th>
			                                                                <th class="">Rating</th>
			                                                            </tr>
			                                                        </thead>
			                                                        <tbody id="rfxVolumeByCategoryDataTable"></tbody>
			                                                    </table>
			                                                </div>
			                                            </div>
			                                        </div>
			                                    </div>
			                                </div>
			                            </div>
			                        </div>
			                    </div>
			                </div>
			                
			                <div class="row margin-top-10">
			                    <div class="col-sm-12">
			                        <div class="card">
			                            <div class="card-header">
			                                <h5 class="ellipse-text">Top 5 Low Performance Supplier
			                                </h5>
			                                <span class="icon-span">
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard icon-left" id="lowPerformanceSuppPieIconId"></i>
			                                    </span>
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right" id="lowPerformanceSuppBarIconId"></i>
			                                    </span>
			                                </span>
			                            </div>
			                            <div class="card-block">
			                                <div class="row marg-top-10" >
			                                    <div class="col-md-7">
			                                        <div id="lowPerformanceSuppBarChart" style="width:100%; height:500px;"></div>
			                                    </div>
			                                    <div class="col-md-5">
			                                        <div class="panel">
			                                            <div class="panel-head">
			                                                <h3 class="title-hero">
			                                                    Top 5 Low Performance Supplier<br>
			                                                </h3>
			                                            </div>
			                                            <div class="panel-body">
			                                                <div class="example-box-wrapper">
			                                                    <table class="table table-bordered ">
			                                                        <thead>
			                                                            <tr>
			                                                                <th class="">Rank</th>
			                                                                <th class="width-200">Supplier</th>
			                                                                <th class="width-150">Overall Score (%)</th>
			                                                                <th class="">Rating</th>
			                                                            </tr>
			                                                        </thead>
			                                                        <tbody id="lowPerformanceSuppDataTable"></tbody>
			                                                    </table>
			                                                </div>
			                                            </div>
			                                        </div>
			                                    </div>
			                                </div>
			                            </div>
			                        </div>
			                    </div>
			                </div>
			                
			                <div class="row margin-top-10">
			                    <div class="col-sm-12">
			                        <div class="card">
			                            <div class="card-header">
			                                <h5 class="ellipse-text">Top 5 Low Performance Supplier By Business Unit</h5>
			                                <span class="icon-span">
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard icon-left" id="lowPerformanceSuppByBUPieIconId"></i>
			                                    </span>
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right" id="lowPerformanceSuppByBUBarIconId"></i>
			                                    </span>
			                                </span>
			                            </div>
			                            <div class="card-block">
			                                <div class="row marg-top-10">
			                                    <div class="col-md-7">
			                                        <div id="lowPerformanceSuppByBUBarChart" style="width:100%; height:500px;"></div>
			                                    </div>
			                                    <div class="col-md-5">
			                                   		<div style="padding-left: 0px; padding-right: 0px; width: 100%;">
				                                   		<div style="padding-left: 0px; padding-right: 0px; " class="marg-bottom-10">
															<select class="form-control chosen-select" id="idLowPermBusinessUnit">
																<c:forEach items="${businessUnitList}" var="bu">
	                               									 <option value="${bu.id}">${bu.displayName}</option>
	                            								</c:forEach>
															</select>
														</div>
													</div>
													<div class="clearfix">
													</div>
			                                        <div class="panel margin-top-10">
			                                            <div class="panel-head">
			                                                <h3 class="title-hero">
			                                                    Top 5 Low Performance Supplier By Business Unit <br>
			                                                </h3>
<!-- 			                                                    <h5 class="title-subtext rfxVolByBU-heading"></h5> -->
			                                            </div>
			                                            <div class="panel-body">
			                                                <div class="example-box-wrapper" >
			                                                    <table class="table table-bordered ">
			                                                        <thead>
			                                                            <tr>
			                                                                <th class="">Rank</th>
			                                                                <th class="width-200">Supplier</th>
			                                                                <th class="width-150">Overall Score (%)</th>
			                                                                <th class="">Rating</th>
			                                                            </tr>
			                                                        </thead>
			                                                        <tbody id="lowPerformanceSuppByBUDataTable"></tbody>
			                                                    </table>
			                                                </div>
			                                            </div>
			                                        </div>
			                                    </div>
			                                </div>
			                            </div>
			                        </div>
			                    </div>
			                </div>
			                
			                 <div class="row margin-top-10">
			                    <div class="col-sm-12">
			                        <div class="card">
			                            <div class="card-header">
			                                <h5 class="ellipse-text">Top 5 Low Performance Supplier By Category
<!--  			                                    <small id="topSuppliersByValue_overall"></small>  -->
			                                </h5>
			                                <span class="icon-span">
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard icon-left" id="lowPerfSuppByProcCatPieIconId"></i>
			                                    </span>
			                                    <span>
			                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right" id="lowPerfSuppByProcCatBarIconId"></i>
			                                    </span>
			                                </span>
			                            </div>
			                            <div class="card-block">
			                                <div class="row marg-top-10">
			                                    <div class="col-md-7">
			                                        <div id="lowPerfSuppByProcCatBarChart" style="width:100%; height:500px;"></div>
			                                    </div>
			                                    <div class="col-md-5">
													<div style="padding-left: 0px; padding-right: 0px; " class="marg-bottom-10">
														<select class="form-control chosen-select" id="idLowPerfPcList">
															<c:forEach items="${procureherementCategoryList}" var="pc">
                               									 <option value="${pc.id}">${pc.procurementCategories}</option>
                            								</c:forEach>
														</select>
													</div>
			                                        <div class="panel">
			                                            <div class="panel-head">
			                                                <h3 class="title-hero pnlHeadAwValByBU">
			                                                    TOP 5 LOW PERFORMANCE SUPPLIER BY CATEGORY <br>
			                                                </h3>
			                                            </div>
			                                            <div class="panel-body">
			                                                <div class="example-box-wrapper" >
			                                                    <table class="table table-bordered ">
			                                                        <thead>
			                                                            <tr>
			                                                               <th class="">Rank</th>
			                                                                <th class="width-200">Supplier</th>
			                                                                <th class="width-150">Overall Score (%)</th>
			                                                                <th class="">Rating</th>
			                                                            </tr>
			                                                        </thead>
			                                                        <tbody id="lowPerfSuppByProcCatDataTable"></tbody>
			                                                    </table>
			                                                </div>
			                                            </div>
			                                        </div>
			                                    </div>
			                                </div>
			                            </div>
			                        </div>
			                    </div>
			                </div>
						</div>
					</div>
                </div>
            </div>
        </div>
        <div class="container">
        </div>
    </div>
</div>
<script type="text/javascript">
$('document').ready(function() {
	
	$('#datepickerAnalytics').daterangepicker({
		format : 'DD/MM/YYYY h:mm A',
	});
	
});
</script>
<style>
    .center-align {
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .card .card-header h5:after {
        content: "";
        background-color: #04a9f5;
        position: absolute;
        left: 15px;
        top: 20px;
        width: 4px;
        height: 20px;
    }

    .card {
        background: white;
        box-shadow: 0 1px 20px 0 rgba(69, 90, 100, .08);
        transition: all .5s ease-in-out;
        box-shadow: 0 1px 20px 0 rgba(69, 90, 100, .08);
        border-radius: 5px;
    }

    .card-header {
        border-bottom: 1px solid #f1f1f1;
        padding: 20px 25px;
        border-radius: 5px 5px 0px 0px;
    }

    .card-bg-light-green {
        color: #fff;
        border-color: #29b765;
        background: #2ecc71;
        border-radius: 7px;
    }

    .card-bg-light-blue {
        color: #fff;
        border-color: #308dcc;
        background: #3498db;
        border-radius: 7px;
    }

    .card-bg-orange {
        color: #fff;
        border-color: #d67520;
        background: #e67e22;
        border-radius: 7px;
    }

    .card-bg-dark-blue {
        color: #fff;
        border-color: #3b5998;
        background: #3b5998;
        border-radius: 7px;
    }

    .card-bg-purple {
        color: #fff;
        background: linear-gradient(-135deg, #899FD4 0%, #A389D4 100%);
        border-radius: 7px;
    }

    .color-white {
        color: white;
        border: 0 !important;
    }

    .card-bg-yellow {
        color: #fff;
        border-color: #f4c22b;
        background: #f4c22b;
        border-radius: 7px;
    }

    .card-bg-sky-blue {
        color: #fff;
        border-color: #1dc4e9;
        background: #1dc4e9;
        border-radius: 7px;
    }


    .status-card-header {
        border: 0 !important;
        background: rgba(255, 255, 255, .2);
        display: flex;
        justify-content: center;
    }

    .margin-top-10 {
        margin-top: 10px;
    }

    .p-l-0 {
        padding-left: 0% !important;
    }

    .p-r-0 {
        padding-right: 0% !important;
    }

    .extend-with {
        width: 101% !important;
    }

    .scroll-content {
        height: 300px;
        overflow: auto;
    }

    .demo-icon {
        margin: 5px;
        border-radius: 0 !important;
        font-size: 15px;
        line-height: 40px;
        width: 30px;
        height: 30px;
        margin: 5px;
        display: flex;
        justify-content: center;
        align-items: center;
        background: #8080804d;
        color: white;
        border: none;
        cursor: pointer;
    }

    .icon-span {
        position: absolute;
        right: 20px;
        top: 8px;
    }

    .icon-right {
        border-left: 0;
        margin-left: 0;
    }

    .icon-left {
        border-right: 0;
        margin-right: 0;
    }

    .icon-active {
        background: #1dc4e9;
        color: white;
    }

    .demo-icon:hover {
        color: white;
        border-color: #ecf3ff;
    }

    input[type=checkbox],
    input[type=radio] {
        width: 15px;
        height: 15px;
        background-color: white;
        border-radius: 50px;
        vertical-align: middle;
        border: 2px solid #1dc4e9;
        -webkit-appearance: none;
        outline: none;
        cursor: pointer;
    }

    input[type=checkbox]:checked,
    input[type=radio]:checked {
        background-color: #1dc4e9;
    }

    input[type=checkbox]:after {
        content: '\2713';
        color: white;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 0px;
        font-size: 10px;
    }

    .p-r-20 {
        padding-right: 20px;
    }

    .p-l-20 {
        padding-left: 25px;
    }

    .align-center {
        display: flex;
        align-items: center;
        /* justify-content: center; */
    }

    input[type='checkbox']:focus {
        outline: thin dotted #333;
        outline: none;
        outline-offset: 0;
    }

    .margin-top-bottom {
        margin-top: 10px;
        margin-bottom: 10px;
        padding-top: 5px;
        padding-bottom: 10px;
    }

    /* Style the tab */
    .tab {
        overflow: hidden;
        background-color: #ffffff;
    }

    /* Style the buttons that are used to open the tab content */
    .tab button {
        width: 97%;
        margin-bottom: 10px;
        background-color: #808080b5;
        color: white;
        border-radius: 5px 5px 0px 0px;
        float: left;
        border: none;
        outline: none;
        cursor: pointer;
        padding: 14px 16px;
        transition: 0.3s;
        margin-right: 5px;
    }

    .tab button:active {
        background-color: #2ecc71;
        color: white;
        border-radius: 5px 5px 0px 0px;
        border-bottom: none;
    }

    /* Change background color of buttons on hover */
    .tab button:hover {
        border-radius: 5px 5px 0px 0px;
    }

    /* Create an active/current tablink class */
    .tab button.active {
        background-color: #2ecc71;
        color: white;
        border-radius: 5px 5px 0px 0px;
        border-bottom: none;
    }

    .main-tab {
        background: white;
        border: 1px solid #ececec;
    }

    h1,
    h2,
    h3,
    h4,
    h5,
    h6 {
        opacity: 0.8;
    }

    h1 small,
    h2 small,
    h3 small,
    h1 .small,
    h2 .small,
    h3 .small {
        opacity: 0.8;
    }

    .panel-head {
        padding: 15px;
        background: #404040;
    }

    .panel-body {
        padding: 0;
        position: relative;
        padding-top: 0;
    }

    .title-hero {
        margin: 0;
        line-height: inherit;
        opacity: 1;
        color: white;
    }

    .panel {
        border-radius: 5px;
    }

    .badge-black,
    .label-black,
    .btn-black,
    .hover-black:hover,
    .bg-black {
        color: white;
        border-color: #000;
        background: #404040;
    }

    .bg-grad {
        background: #4e58c3;
        border-color: #4e58c3;
        color: #fff;
        border-radius: 7px;
    }

    .bg-purple-gradient {
        background: #7c43cc;
        border-color: #7c43cc;
        color: #fff;
        border-radius: 7px;
    }

    .margin-top-20 {
        margin-top: 20px
    }

    .h-70 {
        height: 70%;
    }

    .h-100 {
        height: 100%;
    }

    .f-s-30 {
        font-size: 30px;
    }

    .bg-card-red {
        color: #fff;
        border-color: #cf4436;
        background: #e74c3c;
        border-radius: 7px;
    }

    .justify-center {
        justify-content: center;
    }

    .table>tbody>tr>td {
        padding: 8px 10px !important;
    }

    .table>thead>tr>th {
        padding: 8px 12px !important;
    }

    .table>thead>tr>th,
    .table>tbody>tr>th,
    .table>tfoot>tr>th,
    .table>thead>tr>td,
    .table>tbody>tr>td,
    .table>tfoot>tr>td,
    .table>thead>tr>th,
    .table-bordered {
        border-color: #e8e8e8;
        border-right: 0;
        border-left: 0;
        border-bottom: 0;
    }

    .f-s-17 {
        font-size: 17px;
    }

    .layout-fixed {
        table-layout: fixed;
    }

    .width-50 {
        width: 50px;
    }

    .width-200 {
        width: 200px;
    }

    .width-150 {
        width: 150px;
    }

    .width-75 {
        width: 75px;
    }

    .width-100px {
        width: 100px;
    }

    .row {
        margin-right: 0;
        margin-left: 0;
    }

    .overflow-table {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        width: 200px;
        display: inline-block;
    }
    
    .title-subtext {
         margin: 0px;
    	 line-height: inherit;
    	 color: #979191;
    }
    .mr-15 {
		margin-left: 15px;
	}	
	
	.ellipse-text {
		max-width: 80%;
    	text-overflow: ellipsis;
    	overflow: hidden;
    	white-space: nowrap;
	}
	
	.tableTdEllipse-text {
    	max-width: 100%;
    	text-overflow: ellipsis;
    	overflow: hidden;
    	white-space: nowrap;
	}
       
</style>
<script type="text/javascript" src="<c:url value="/resources/js/view/spAnalytics.js?4"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/echarts.min.js"/>"></script>
