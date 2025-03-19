<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div id="page-content">
	<div class="container col-md-12">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<c:url value="/buyer/buyerDashboard" var="dashboardUrl" />
			<li><a href="${dashboardUrl}"> <spring:message code="application.dashboard" />
			</a></li>
			<li class="active">PO List</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">PO List</h2>
		</div>
		<div class="container-fluid col-md-12">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />

			<c:url value="/owner/financepoReports" var="financepoReports" />
			<form action="${financepoReports}" method="post">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="row">
					<div class="col-md-12 marg-top-10">
						<label><spring:message code="application.filter.By.Date" /></label>
						<div class="row">
							<div class="col-md-4 col-sm-8">
								<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepicker-date-time-nodisable" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P"
									data-validation="required" />
							</div>
							<div class="col-md-2 resetBtn-padding">
								<button id="resetDate" class="btn btn-sm btn-black " data-toggle="tooltip" data-placement="top" data-original-title="Reset">
									<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
								</button>
							</div>
						</div>
					</div>
					<div class="col-md-12 marg-top-10">
						<button type="submit" class="btn btn-sm btn-success hvr-pop marg-left-10 marg-top-10" data-toggle="tooltip" data-placement="top" data-original-title="Export PO Report">

							<span class="glyph-icon icon-separator">
								<i class="glyph-icon icon-download"></i>
							</span>
							<span class="button-content">Export PO Report</span>
						</button>
					</div>
				</div>
			</form>





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

.mega {
	max-height: 380px !important;
}

.fa-check:before {
	content: "\f00c";
	color: green;
	font-size: 20px;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script type="text/javascript">
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});

	$.validate({
		lang : 'en',
		modules : 'date'
	});
</script>
<script type="text/javascript">
	$('document').ready(function() {

		$('#datepicker-date-time-nodisable').on('apply.daterangepicker', function(e, picker) {
			e.preventDefault();
			$('.custom-checkbox').prop('checked', false).change().uniform();
			$('.custom-checkbox').uniform();
			$('.checker span').find('.glyph-icon.icon-check').remove();
			$('.checker span').append('<i class="glyph-icon icon-check"></i>');
			var dateTimeRange = $("input[name='dateTimeRange']").val();
			console.log("date_range :" + dateTimeRange);
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");

		});

		$("#resetDate").click(function(e) {
			e.preventDefault();
			if ($("#datepicker-date-time-nodisable").val() !== '') {
				location.reload();
			}
			$("#datepicker-date-time-nodisable").val('');
		});

		$("#exportPoReport").click(function(e) {
			e.preventDefault();

			$('.error-range.text-danger').remove();
			var val = [];
			$(':checkbox:checked').each(function(i) {
				val[i] = $(this).val();
			});
			//console.log(val + "val");

			if (typeof val === 'undefined' || val == '') {
				console.log("Error");
				$('#exportPoReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one PO</p>');
				return false;
			} else {
				$('#exportPoReportForm').submit();
			}

		});

		$('.custom-checkAllbox').on('change', function() {
			//$('.custom-checkbox').on('change', function() {

			var check = this.checked;
			$("[type=checkbox]").each(function() {
				$("[type=checkbox]").prop('checked', check);
				$.uniform.update($(this));
			});
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

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>