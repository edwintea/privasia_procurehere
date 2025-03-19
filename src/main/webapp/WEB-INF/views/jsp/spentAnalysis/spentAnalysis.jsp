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
                    <spring:message code="application.spent.analysis" />
                </h2>
                <div style="display: flex;justify-content: flex-end;">
                    <div class="col-md-2">
                        <!-- businessUnit -->
                        <select id="month" class="form-control chosen-select">
                            <option value="0">January</option>
                            <option value="1">February</option>
                            <option value="2">March</option>
                            <option value="3">April</option>
                            <option value="4">May</option>
                            <option value="5">June</option>
                            <option value="6">July</option>
                            <option value="7">August</option>
                            <option value="8">September</option>
                            <option value="9">October</option>
                            <option value="10">November</option>
                            <option value="11">December</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <select id="year" class="form-control chosen-select">
                            <c:forEach items="${yearList}" var="year">
                                <option value="${year}">${year}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button class="btn btn-info hvr-pop hvr-rectangle-out" id="updateDashboard"><i
                            class="glyphicon glyphicon-refresh"></i></button>
                </div>
            </div>
            <!-- page title block -->
            <div class="row clearfix">
                <div class="col-sm-12">
                    <jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
                </div>
            </div>
            <div class="tab">
                <div class="col-md-2 p-l-0 p-r-0">
                    <button class="tablinks-overall">Overall Statistics</button>
                </div>
                <div class="col-md-2 p-l-0 p-r-0">
                    <button class="tablinks-subsidiary">Subsidiary Statistics</button>
                </div>
                <div class="col-md-2 p-l-0 p-r-0">
                    <button class="tablinks-nonsubsidiary">Non Subsidiary Statistics</button>
                </div>
            </div>
            <div class="row">
                <div class="col-md-2 margin-top-10">
                    <div class="card card-bg-sky-blue">
                        <div class="card-header status-card-header">
                            <span class="color-white">PO SPEND</span>
                        </div>
                        <div class="card-block" style="width:100%; height:150px;">
                            <div id="crrYearSpendId" class="align-center justify-center h-70 f-s-17"></div>
                            <div class="align-center justify-center">For Year&nbsp;<span id="yearSpend"></span></div>
                        </div>
                    </div>
                </div>
                <div class="col-md-2 margin-top-10">
                    <div class="card card-bg-dark-blue">
                        <div class="card-header status-card-header">
                            <span class="color-white">PO VOLUME</span>
                        </div>
                        <div class="card-block" style="width:100%; height:150px;">
                            <div id="crrYearVolumeId" class="align-center justify-center h-70 f-s-30"></div>
                            <div class="align-center justify-center">For Year&nbsp;<span id="yearVolume"></span></div>
                        </div>
                    </div>
                </div>
                <div class="col-md-2 margin-top-10">
                    <div class="card card-bg-light-green">
                        <div class="card-header status-card-header">
                            <span class="color-white">PO STATUS</span>
                        </div>
                        <div class="card-block" style="width:100%; height:150px;">
                            <div class="h-100">
                                <div class="col-sm-6 h-100">
                                    <div id="orderedStatusId" class="align-center justify-center h-70 f-s-30"></div>
                                    <div class="align-center justify-center">ORDERED</div>
                                </div>
                                <div class="col-sm-6 h-100">
                                    <div id="readyStatusId" class="align-center justify-center h-70 f-s-30"></div>
                                    <div class="align-center justify-center">READY</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-2 margin-top-10">
                    <div class="card card-bg-purple">
                        <div class="card-header status-card-header">
                            <span class="color-white">PO STATUS</span>
                        </div>
                        <div class="card-block" style="width:100%; height:150px;">
                            <div class="h-100">
                                <div class="col-sm-6 h-100">
                                    <div id="acceptedStatusId" class="align-center justify-center h-70 f-s-30"></div>
                                    <div class="align-center justify-center">ACCEPTED</div>
                                </div>
                                <div class="col-sm-6 h-100">
                                    <div id="cancelledStatusId" class="align-center justify-center h-70 f-s-30"></div>
                                    <div class="align-center justify-center">CANCELLED</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-2 margin-top-10">
                    <div class="card card-bg-orange">
                        <div class="card-header status-card-header">
                            <span class="color-white">PO CATEGORY</span>
                        </div>
                        <div class="card-block" style="width:100%; height:150px;">
                            <div id="categorySpend" class="align-center justify-center h-70 f-s-17"></div>
                            <div class="align-center justify-center">Total Spend</div>
                        </div>
                    </div>
                </div>
                <div class="col-md-2 margin-top-10">
                    <div class="card card-bg-yellow">
                        <div class="card-header status-card-header">
                            <span class="color-white">PO CATEGORY</span>
                        </div>
                        <div class="card-block" style="width:100%; height:150px;">
                            <div id="categoryVolume" class="align-center justify-center h-70 f-s-30"></div>
                            <div class="align-center justify-center">Total Volume</div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Overall Tab -->
            <div id="overallTab" class="show">
                <div class="row margin-top-10">
                    <div class="col-sm-12">
                        <div class="card">
                            <div class="card-header">
                                <h5>PO VOLUME BY MONTH
                                    <small id="volumeByMonth_overall"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="volumeLineIconId_overall"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="volumeBarIconId_overall"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="col-md-8">
                                        <div id="barChart_overall" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4 margin-top-20">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOTAL PO VOLUME FOR YEAR&nbsp;<span
                                                        id="tableSpan_overall"></span><br>
                                                    <small id="totalVolumeId_overall"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered">
                                                        <thead>
                                                            <tr>
                                                                <th>Month</th>
                                                                <th>Volume</th>
                                                                <th>Volume ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="poVolumeDataTable_overall"></tbody>
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
                                <h5>PO SPEND BY MONTH
                                    <small id="valueByMonth_overall"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="spendLineIconId_overall"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="spendBarIconId_overall"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="col-md-8">
                                        <div id="lineChartForValue_overall" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4 margin-top-20">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOTAL PO SPEND FOR YEAR&nbsp;<span
                                                        id="tableValueSpan_overall"></span><br>
                                                    <small id="totalValueId_overall"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper extend-with"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered">
                                                        <thead>
                                                            <tr>
                                                                <th>Month</th>
                                                                <th>Value</th>
                                                                <th>Value ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="poValueDataTable_overall"></tbody>
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
                                <h5>PO VOLUME FOR THE LAST 5 YEARS
                                    <small id="valueForPrevious_overall"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="previousYearsLineIconId_overall"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="previousYearsBarIconId_overall"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div id="lineChart_overall" style="width:100%; height:400px;"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-sm-12">
                        <div class="card">
                            <div class="card-header">
                                <h5> TOP 10 PO VOLUME BY CATEGORY
                                    <small id="volumeByCategory_overall"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard  icon-left"
                                            id="categoryVolumePieId_overall"></i>
                                    </span>
                                    <span>
                                        <!-- change to tree -->
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="categoryVolumeTreeId_overall"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="align-center margin-top-bottom" id="includeExcludeVolume_overall">
                                        <span class="p-r-20 p-l-20">Please Click on a category to include/excude
                                            it</span>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="selectAllOptionsForVolume_overall">
                                            <span>&nbsp;Select All</span>
                                        </label>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="unselectAllOptionsForVolume_overall">
                                            <span>&nbsp;Unselect all</span>
                                        </label>
                                    </div>
                                    <div class="col-md-8">
                                        <div id="pieChart_overall" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOP 10 PO VOLUME BY CATEGORY<br>
                                                    <small id="totalVolumeCategoryId_overall"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper extend-with"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered layout-fixed">
                                                        <thead>
                                                            <tr>
                                                                <th class="width-50">Rank</th>
                                                                <!-- <th>Vendor</th> -->
                                                                <th class="width-200">Category</th>
                                                                <th class="width-150">Volume</th>
                                                                <th class="width-150">Volume ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="totalVolumeCategoryTableId_overall"></tbody>
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
                                <h5>TOP 10 PO VALUE BY CATEGORY
                                    <small id="valueByCategory_overall"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard  icon-left"
                                            id="categoryValuePieId_overall"></i>
                                    </span>
                                    <span>
                                        <!-- change to tree -->
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="categoryValueTreeId_overall"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="align-center margin-top-bottom" id="includeExcludeValue_overall">
                                        <span class="p-r-20 p-l-20">Please Click on a category to include/excude
                                            it</span>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="selectAllOptionsForValue_overall">
                                            <span>&nbsp;Select All</span>
                                        </label>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="unselectAllOptionsForValue_overall">
                                            <span>&nbsp;Unselect all</span>
                                        </label>
                                    </div>
                                    <div class="col-md-8">
                                        <div id="pieChartForPoValue_overall" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOP 10 PO VALUE BY CATEGORY<br>
                                                    <small id="totalValueCategoryId_overall"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper extend-with"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered layout-fixed">
                                                        <thead>
                                                            <tr>
                                                                <th class="width-50">Rank</th>
                                                                <!-- <th>Vendor</th> -->
                                                                <th class="width-200">Category</th>
                                                                <th class="width-150">Value</th>
                                                                <th class="width-150">Value ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="totalValueCategoryTableId_overall"></tbody>
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
                        <div class="panel">
                            <div class="panel-head">
                                <h3 class="title-hero">
                                    TOTAL PO SPEND/VOLUME BY INTER-CO & EXTERNAL<br>
                                </h3>
                            </div>
                            <div class="panel-body">
                                <div>
                                    <table class="table table-bordered">
                                        <thead>
                                            <tr>
                                                <th>PO Type</th>
                                                <!-- <th>Vendor</th> -->
                                                <th>Value</th>
                                                <th>Value ( % )</th>
                                                <th>Count</th>
                                                <th>Count ( % )</th>
                                            </tr>
                                        </thead>
                                        <tbody id="totalPoInterExternalTableId_overall">
                                            <tr>
                                                <td>Inter-co</td>
                                                <td id="totalIntercoId"></td>
                                                <td id="totalIntercoPercentId"></td>
                                                <td id="totalIntercoCountId"></td>
                                                <td id="totalIntercoCountPercentId"></td>
                                            </tr>
                                            <tr>
                                                <td>External</td>
                                                <td id="totalExternalId"></td>
                                                <td id="totalExternalPercentId"></td>
                                                <td id="totalExternalCountId"></td>
                                                <td id="totalExternalCountPercentId"></td>
                                            </tr>
                                            <tr>
                                                <td>Total</td>
                                                <td id="totalInterExternalSpendId"></td>
                                                <td></td>
                                                <td id="totalInterExternalCountId"></td>
                                                <td></td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-sm-12">
                        <div class="card">
                            <div class="card-header">
                                <h5>TOP 10 PO SPEND BY INTER-CO & EXTERNAL
                                    <small id="interAndExternalValue_overall"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="lineChartForSpentSubsidiaryId_overall"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="barChartForSpentSubsidiaryId_overall"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block" style="padding-bottom: 30px;">
                                <div id="lineChartForSpentSubsidiary_overall" style="width:100%; height:550px;"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-sm-12">
                        <div class="card">
                            <div class="card-header">
                                <h5>TOP 10 PO VOLUME BY INTER CO & EXTERNAL
                                    <small id="interAndExternalVolume_overall"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="lineChartForVolumeSubsidiaryId_overall"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="barChartForVolumeSubsidiaryId_overall"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block" style="padding-bottom: 30px;">
                                <div id="lineChartForVolumeSubsidiary_overall" style="width:100%; height:550px;"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-sm-12">
                        <div class="card">
                            <div class="card-header">
                                <h5>TOP 10 SUPPLIERS BY VOLUME
                                    <small id="topSuppliersByVolume_overall"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard  icon-left"
                                            id="supplierVolumePieIconId_overall"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="supplierVolumeBarIconId_overall"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="col-md-8">
                                        <div id="supplierBarChart_overall" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4 margin-top-20">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOP 10 SUPPLIERS BY VOLUME<br>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered layout-fixed">
                                                        <thead>
                                                            <tr>
                                                                <th class="width-50">Rank</th>
                                                                <th class="width-200">Name</th>
                                                                <th class="width-150">Volume</th>
                                                                <th class="width-150">Volume ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="supplierVolumeDataTable_overall"></tbody>
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
                                <h5>TOP 10 SUPPLIERS BY VALUE
                                    <small id="topSuppliersByValue_overall"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard icon-left"
                                            id="supplierValuePieIconId_overall"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="supplierValueBarIconId_overall"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="col-md-8">
                                        <div id="supplierValuebarChart_overall" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4 margin-top-20">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOP 10 SUPPLIERS BY VALUE<br>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered layout-fixed">
                                                        <thead>
                                                            <tr>
                                                                <th class="width-50">Rank</th>
                                                                <th class="width-200">Name</th>
                                                                <th class="width-150">Value</th>
                                                                <th class="width-100px">Value ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="supplierValueDataTable_overall"></tbody>
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
            <!-- Subsidiary Tab -->
            <div id="subsidiaryTab" class="hidden">
                <div class="row margin-top-10">
                    <div class="col-sm-12">
                        <div class="card">
                            <div class="card-header">
                                <h5>PO VOLUME BY MONTH
                                    <small id="volumeByMonth_subsidiary"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="volumeLineIconId_subsidiary"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="volumeBarIconId_subsidiary"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="col-md-8">
                                        <div id="barChart_subsidiary" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4 margin-top-20">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOTAL PO VOLUME FOR YEAR&nbsp;<span
                                                        id="tableSpan_subsidiary"></span><br>
                                                    <small id="totalVolumeId_subsidiary"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered">
                                                        <thead>
                                                            <tr>
                                                                <th>Month</th>
                                                                <th>Volume</th>
                                                                <th>Volume ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="poVolumeDataTable_subsidiary"></tbody>
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
                                <h5>PO SPEND BY MONTH
                                    <small id="valueByMonth_subsidiary"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="spendLineIconId_subsidiary"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="spendBarIconId_subsidiary"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="col-md-8">
                                        <div id="lineChartForValue_subsidiary" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4 margin-top-20">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOTAL PO SPEND FOR YEAR&nbsp;<span
                                                        id="tableValueSpan_subsidiary"></span><br>
                                                    <small id="totalValueId_subsidiary"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper extend-with"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered">
                                                        <thead>
                                                            <tr>
                                                                <th>Month</th>
                                                                <th>Value</th>
                                                                <th>Value ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="poValueDataTable_subsidiary"></tbody>
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
                                <h5>PO VOLUME FOR THE LAST 5 YEARS
                                    <small id="valueForPrevious_subsidiary"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="previousYearsLineIconId_subsidiary"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="previousYearsBarIconId_subsidiary"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div id="lineChart_subsidiary" style="width:100%; height:400px;"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-sm-12">
                        <div class="card">
                            <div class="card-header">
                                <h5> TOP 10 PO VOLUME BY CATEGORY
                                    <small id="volumeByCategory_subsidiary"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard  icon-left"
                                            id="categoryVolumePieId_subsidiary"></i>
                                    </span>
                                    <span>
                                        <!-- change to tree -->
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="categoryVolumeTreeId_subsidiary"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="align-center margin-top-bottom" id="includeExcludeVolume_subsidiary">
                                        <span class="p-r-20 p-l-20">Please Click on a category to include/excude
                                            it</span>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="selectAllOptionsForVolume_subsidiary">
                                            <span>&nbsp;Select All</span>
                                        </label>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="unselectAllOptionsForVolume_subsidiary">
                                            <span>&nbsp;Unselect all</span>
                                        </label>
                                    </div>
                                    <div class="col-md-8">
                                        <div id="pieChart_subsidiary" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOP 10 PO VOLUME BY CATEGORY<br>
                                                    <small id="totalVolumeCategoryId_subsidiary"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper extend-with"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered layout-fixed">
                                                        <thead>
                                                            <tr>
                                                                <th class="width-50">Rank</th>
                                                                <!-- <th>Vendor</th> -->
                                                                <th class="width-200">Category</th>
                                                                <th class="width-150">Volume</th>
                                                                <th class="width-150">Volume ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="totalVolumeCategoryTableId_subsidiary"></tbody>
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
                                <h5>TOP 10 PO VALUE BY CATEGORY
                                    <small id="valueByCategory_subsidiary"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard  icon-left"
                                            id="categoryValuePieId_subsidiary"></i>
                                    </span>
                                    <span>
                                        <!-- change to tree -->
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="categoryValueTreeId_subsidiary"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="align-center margin-top-bottom" id="includeExcludeValue_subsidiary">
                                        <span class="p-r-20 p-l-20">Please Click on a category to include/excude
                                            it</span>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="selectAllOptionsForValue_subsidiary">
                                            <span>&nbsp;Select All</span>
                                        </label>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="unselectAllOptionsForValue_subsidiary">
                                            <span>&nbsp;Unselect all</span>
                                        </label>
                                    </div>
                                    <div class="col-md-8">
                                        <div id="pieChartForPoValue_subsidiary" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOP 10 PO VALUE BY CATEGORY<br>
                                                    <small id="totalValueCategoryId_subsidiary"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper extend-with"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered layout-fixed">
                                                        <thead>
                                                            <tr>
                                                                <th class="width-50">Rank</th>
                                                                <!-- <th>Vendor</th> -->
                                                                <th class="width-200">Category</th>
                                                                <th class="width-150">Value</th>
                                                                <th class="width-150">Value ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="totalValueCategoryTableId_subsidiary"></tbody>
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
            <!-- NonSubsidiary Tab -->
            <div id="nonsubsidiaryTab" class="hidden">
                <div class="row margin-top-10">
                    <div class="col-sm-12">
                        <div class="card">
                            <div class="card-header">
                                <h5>PO VOLUME BY MONTH
                                    <small id="volumeByMonth_nonsubsidiary"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="volumeLineIconId_nonsubsidiary"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="volumeBarIconId_nonsubsidiary"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="col-md-8">
                                        <div id="barChart_nonsubsidiary" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4 margin-top-20">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOTAL PO VOLUME FOR YEAR&nbsp;<span
                                                        id="tableSpan_nonsubsidiary"></span><br>
                                                    <small id="totalVolumeId_nonsubsidiary"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered">
                                                        <thead>
                                                            <tr>
                                                                <th>Month</th>
                                                                <th>Volume</th>
                                                                <th>Volume ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="poVolumeDataTable_nonsubsidiary"></tbody>
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
                                <h5>PO SPEND BY MONTH
                                    <small id="valueByMonth_nonsubsidiary"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="spendLineIconId_nonsubsidiary"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="spendBarIconId_nonsubsidiary"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="col-md-8">
                                        <div id="lineChartForValue_nonsubsidiary" style="width:100%; height:400px;">
                                        </div>
                                    </div>
                                    <div class="col-md-4 margin-top-20">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOTAL PO SPEND FOR YEAR&nbsp;<span
                                                        id="tableValueSpan_nonsubsidiary"></span><br>
                                                    <small id="totalValueId_nonsubsidiary"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper extend-with"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered">
                                                        <thead>
                                                            <tr>
                                                                <th>Month</th>
                                                                <th>Value</th>
                                                                <th>Value ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="poValueDataTable_nonsubsidiary"></tbody>
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
                                <h5>PO VOLUME FOR THE LAST 5 YEARS
                                    <small id="valueForPrevious_nonsubsidiary"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="previousYearsLineIconId_nonsubsidiary"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="previousYearsBarIconId_nonsubsidiary"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div id="lineChart_nonsubsidiary" style="width:100%; height:400px;"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-sm-12">
                        <div class="card">
                            <div class="card-header">
                                <h5> TOP 10 PO VOLUME BY CATEGORY
                                    <small id="volumeByCategory_nonsubsidiary"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard  icon-left"
                                            id="categoryVolumePieId_nonsubsidiary"></i>
                                    </span>
                                    <span>
                                        <!-- change to tree -->
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="categoryVolumeTreeId_nonsubsidiary"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="align-center margin-top-bottom" id="includeExcludeVolume_nonsubsidiary">
                                        <span class="p-r-20 p-l-20">Please Click on a category to include/excude
                                            it</span>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="selectAllOptionsForVolume_nonsubsidiary">
                                            <span>&nbsp;Select All</span>
                                        </label>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="unselectAllOptionsForVolume_nonsubsidiary">
                                            <span>&nbsp;Unselect all</span>
                                        </label>
                                    </div>
                                    <div class="col-md-8">
                                        <div id="pieChart_nonsubsidiary" style="width:100%; height:400px;"></div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOP 10 PO VOLUME BY CATEGORY<br>
                                                    <small id="totalVolumeCategoryId_nonsubsidiary"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper extend-with"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered layout-fixed">
                                                        <thead>
                                                            <tr>
                                                                <th class="width-50">Rank</th>
                                                                <!-- <th>Vendor</th> -->
                                                                <th class="width-200">Category</th>
                                                                <th class="width-150">Volume</th>
                                                                <th class="width-150">Volume ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="totalVolumeCategoryTableId_nonsubsidiary"></tbody>
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
                                <h5>TOP 10 PO VALUE BY CATEGORY
                                    <small id="valueByCategory_nonsubsidiary"></small>
                                </h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-dashboard  icon-left"
                                            id="categoryValuePieId_nonsubsidiary"></i>
                                    </span>
                                    <span>
                                        <!-- change to tree -->
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="categoryValueTreeId_nonsubsidiary"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div class="row" style="width:100%;">
                                    <div class="align-center margin-top-bottom" id="includeExcludeValue_nonsubsidiary">
                                        <span class="p-r-20 p-l-20">Please Click on a category to include/excude
                                            it</span>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="selectAllOptionsForValue_nonsubsidiary">
                                            <span>&nbsp;Select All</span>
                                        </label>
                                        <label class="align-center p-r-20">
                                            <input type="checkbox" id="unselectAllOptionsForValue_nonsubsidiary">
                                            <span>&nbsp;Unselect all</span>
                                        </label>
                                    </div>
                                    <div class="col-md-8">
                                        <div id="pieChartForPoValue_nonsubsidiary" style="width:100%; height:400px;">
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="panel">
                                            <div class="panel-head">
                                                <h3 class="title-hero">
                                                    TOP 10 PO VALUE BY CATEGORY<br>
                                                    <small id="totalValueCategoryId_nonsubsidiary"></small>
                                                </h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="example-box-wrapper extend-with"
                                                    style="height: 275px;overflow: scroll;">
                                                    <table class="table table-bordered layout-fixed">
                                                        <thead>
                                                            <tr>
                                                                <th class="width-50">Rank</th>
                                                                <!-- <th>Vendor</th> -->
                                                                <th class="width-200">Category</th>
                                                                <th class="width-150">Value</th>
                                                                <th class="width-150">Value ( % )</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody id="totalValueCategoryTableId_nonsubsidiary"></tbody>
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
            <!-- budget Tab -->
            <div id="budgetTab" class="hidden">
                <div class="margin-top-10">
                    <div class="card">
                        <div class="card-header">
                            <div class="col-md-3">
                                <!-- businessUnit -->
                                <label>Select Business Unit:</label> <select id="businessUnit"
                                    class="form-control chosen-select">
                                    <option value="">Select Business unit</option>
                                    <c:forEach items="${businessUnitList}" var="businessUnit">
                                        <option value="${businessUnit.id}">${businessUnit.unitName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label>Select Cost Center:</label> <select id="costCenter"
                                    class="form-control chosen-select">
                                    <option value="">Select Cost Center</option>
                                    <c:forEach items="${costCenterList}" var="costCenter">
                                        <option value="${costCenter.id}">${costCenter.costCenter}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out"
                                style="margin-top: 24px; margin-left: 16px;" id="updateGraph">View</button>
                            <div class="col-md-6"></div>
                        </div>
                    </div>
                </div>
                <div class="row margin-top-10">
                    <div class="col-sm-12">
                        <div class="card">
                            <div class="card-header">
                                <h5>BUDGET BASED ON BUSINESS UNIT AND COSTCENTER</h5>
                                <span class="icon-span">
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-line-chart icon-left"
                                            id="lineChartForBudgetValueId_budget"></i>
                                    </span>
                                    <span>
                                        <i class="glyph-icon tooltip-button demo-icon icon-bar-chart-o icon-right"
                                            id="barChartForBudgetVolumeId_budget"></i>
                                    </span>
                                </span>
                            </div>
                            <div class="card-block">
                                <div id="lineChartForBudgetVolume_budget" style="width:100%; height:400px;"></div>
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

    /* Pricing tables */

    .pricing-box {
        text-align: center;
    }

    .pricing-box .pricing-title,
    .pricing-box .pricing-specs {
        margin: -1px -1px 0;
    }

    .pricing-box .pricing-title {
        font-weight: normal;
        padding: 15px;
    }

    .pricing-box ul {
        margin: 0;
        padding: 0;
        list-style: none;
    }

    .pricing-box .pricing-specs {
        padding: 10px 15px 20px;
    }

    .pricing-box .pricing-specs span {
        font-size: 50px;
    }

    .pricing-box .pricing-specs span sup {
        font-size: 30px;
        margin-left: -20px;
        padding-right: 5px;
    }

    .pricing-box .pricing-specs i {
        font-size: 14px;
        font-style: normal;
        display: block;
        color: rgba(255, 255, 255, .6);
    }

    .pricing-box ul li {
        font-size: 14px;
        line-height: 48px;
        height: 48px;
        padding: 0 10px;
        border-bottom: #eee solid 1px;
    }

    .pricing-box ul li:nth-child(even) {
        background: #fafafa;
    }

    /* Pricing table alternate style */

    .pricing-table .pricing-box {
        padding: 0;
    }

    .pricing-table .pricing-box+.pricing-box {
        border-width: 1px 1px 1px 0;
        border-radius: 0;
    }

    .pricing-table .pricing-box+.pricing-box:nth-child(2):last-child {
        border-width: 1px 1px 1px;
    }

    .pricing-table .pricing-box .pricing-title,
    .pricing-table .pricing-box .pricing-specs {
        margin: 0;
        border-bottom: #eee solid 1px;
        border-radius: 0;
    }

    .pricing-table .pricing-box .pricing-specs span {
        font-size: 40px;
    }

    .pricing-table .pricing-box .pricing-specs span sup {
        font-size: 20px;
        margin-left: -10px;
        padding-right: 5px;
    }

    .pricing-table .pricing-box .pricing-specs i {
        color: rgba(0, 0, 0, .5);
    }

    .pricing-table .pricing-best {
        position: relative;
        z-index: 15;
        margin-right: -1px;
        margin-left: -1px;
        box-shadow: 0 0 10px 0 rgba(0, 0, 0, .1);
    }

    .pricing-table .pricing-best .pricing-specs {
        background: #fafafa;
    }

    .pricing-table .pricing-best .pricing-title {
        font-size: 28px;
        line-height: 60px;
        height: 90px;
        margin: -25px -1px 0;
    }

    /* Pricing alternate */

    .pricing-box-alt {
        position: relative;
    }

    .pricing-box-alt .col-md-3 {
        padding: 0;
        border: #c6c6c6 solid 1px;
        border-width: 1px 1px 1px 0;
        width: 26%;
        text-align: center;
    }

    .pricing-box-alt .plans-features {
        width: 22%;
        text-align: right;
        border-color: transparent #c6c6c6 transparent transparent;
    }

    .pricing-box-alt .plans-features .plan-header {
        height: 170px;
    }

    .pricing-box-alt .plans-features ul li {
        border-left: #f0f0f0 solid 1px;
    }

    .pricing-box-alt ul {
        list-style: none;
        margin: 0;
        padding: 0;
    }

    .pricing-box-alt .plan-header {
        padding: 15px;
        border-bottom: #f0f0f0 solid 1px;
    }

    .pricing-box-alt .plan-header h4 {
        margin: 0;
        color: #f26b33;
        text-transform: uppercase;
        font-size: 17px;
        font-weight: bold;
        height: 40px;
        line-height: 30px;
        border-bottom: #F3F3F3 solid 1px;
    }

    .pricing-box-alt .plan-header .plan-price {
        font-size: 45px;
        font-weight: 100;
        height: 60px;
        line-height: 65px;
        margin: 0 0 5px;
    }

    .pricing-box-alt .plan-header .plan-price small {
        font-size: 30px;
        opacity: 0.4;
        padding-right: 3px;
    }

    .pricing-box-alt .studio-plan .plan-header h4 {
        color: #32cf4e;
    }

    .pricing-box-alt .unlimited-plan .plan-header h4 {
        color: #3792f2;
    }

    .pricing-box-alt ul li {
        height: 32px;
        line-height: 32px;
        padding: 0 10px;
        border-bottom: #f0f0f0 solid 1px;
        color: #0093d9;
        font-size: 14px;
        font-weight: bold;
    }

    .pricing-box-alt .plans-features ul li {
        color: #6f6f6f;
        font-weight: normal;
    }

    .pricing-box-alt ul li .feature-included,
    .pricing-box-alt ul li .feature-excluded {
        border-radius: 30px;
        width: 12px;
        height: 12px;
        display: inline-block;
    }

    .pricing-box-alt ul li .feature-excluded {
        background: #e6e6e6;
    }

    .pricing-box-alt .pricing-btn {
        padding: 15px;
        background: #fafafa;
    }

    .pricing-box-alt .pricing-btn .btn {
        padding: 15px 0;
        font-weight: bold;
        font-size: 16px;
        box-sizing: initial;
        display: block;
        line-height: 1;
    }

    .pricing-box-alt .pricing-btn .btn b {
        opacity: 0.6;
        display: block;
        padding: 6px 0 0;
        font-size: 13px;
        font-weight: normal;
    }

    .individual-plan .pricing-btn {
        border-left: #c6c6c6 solid 1px;
        margin-left: -1px;
    }

    .pricing-box-alt ul li.header {
        background: #f9f9f9;
        text-transform: uppercase;
        font-weight: bold;
        text-align: right;
        font-size: 12px;
        color: #000;
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
</style>

<script type="text/javascript" src="<c:url value="/resources/js/view/spentAnalysis.js?11"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/echarts.min.js"/>"></script>