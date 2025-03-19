<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!-- Morris charts -->
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/raphael.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/charts/morris/morris.js"/>"></script>
<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});
</script>
<script>
	$(document).ready(function() {
		$('.mega').on('scroll', function() {
			$('.header').css('top', $(this).scrollTop());
		});
	});
</script>
<div id="page-content">
	<div class="container">
		<ol class="breadcrumb">
			<li>
				<a href="${pageContext.request.contextPath}/owner/ownerDashboard">Dashboard</a>
			</li>
			<li class="active">Transaction</li>
		</ol>
		<!-- page title block -->
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap transaction">Transaction</h2>
		</div>
		<div class="column_button_bar clearfix"></div>
		<div class="clear"></div>
		<div class="Invited-Supplier-List create_sub marg-bottom-20">
			<div class="title">
				<h3 class="pad15">Advance Search</h3>
			</div>
			<form class="form-horizontal" id="login-validation">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-4 col-sm-4">
					<div class="short-by">
						<input data-validation="required" class="form-control" type="text" placeholder="Company Name">
					</div>
				</div>
				<div class="col-md-4 col-sm-4">
					<div class="short-by">
						<input data-validation="required" class="form-control" type="text" placeholder="Transaction ID">
					</div>
				</div>
				<div class="col-md-4 col-sm-4 ">
					<div class="short-by mar_0">
						<input data-validation="required" class="form-control" type="text" placeholder="Keyword">
					</div>
				</div>
				<div class="clear"></div>
				<div class="col-md-2  btnadd marg-bottom-20">
					<button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out">Search</button>
				</div>
			</form>
		</div>
		<div class="col-md-12 pad0">
			<div class="Invited-Supplier-List tabtrans dashboard-main">
				<div class="Invited-Supplier-List-table add-supplier">
					<div class="ph_tabel_wrapper">
						<div class="main_table_wrapper ">
							<div class="mega">
								<table class="ph_table border-none header" width="100%" border="0" cellspacing="0" cellpadding="0">
									<thead>
										<tr>
											<th align="center" class="width_300 width_300_fix" style="text-align: center;">Transaction Date</th>
											<th align="center" class="width_200 width_200_fix">Company Name</th>
											<th align="center" class="width_200  width_200_fix" style="text-align: center;">Transaction ID</th>
											<th align="center" class="width_200  width_200_fix" style="text-align: center;">Payment ID</th>
											<th align="center" class="width_300 width_300_fix" style="text-align: center;">Payment Method</th>
											<th align="center" class=" width_300 width_300_fix" style="text-align: center;">Card Number</th>
											<th align="center" class="width_200 width_200_fix" style="text-align: center;">Amount</th>
											<th align="center" class="width_100 width_100_fix" style="text-align: center;">Status</th>
											<th align="center" class="width_100 width_100_fix" style="text-align: center;">Notes</th>
										</tr>
									</thead>
								</table>
								<table class="data ph_table border-none transction" width="100%" border="0" cellspacing="0" cellpadding="0">
									<tbody>
										<tr>
											<td align="center" class="width_300 width_300_fix" style="text-align: center;">
												07/11/2015
												<span>11:47 AM</span>
											</td>
											<td align="center" class="width_200 width_200_fix">ik SDN Bhd</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">000001</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">000001</td>
											<td align="center" class="width_300 width_300_fix" style="text-align: center;">Credit Card</td>
											<td align="center" class="width_300 width_300_fix " style="text-align: center;">1111 2222 3333 4444</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">$200</td>
											<td align="center" class="width_100 width_100_fix" style="text-align: center;">
												<a href="#">Active</a>
											</td>
											<td align="center" class="width_100 width_100_fix" style="text-align: center;">
												<a class="btn btn-default noteicon" href="#" data-featherlight="#fl2" data-featherlight-variant="fixwidth">
													<img src="images/note.png">
												</a>
											</td>
										</tr>
										<tr>
											<td align="center" class="width_300 width_300_fix" style="text-align: center;">
												07/11/2015
												<span>11:47 AM</span>
											</td>
											<td align="center" class="width_200 width_200_fix">ik SDN Bhd</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">000001</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">000001</td>
											<td align="center" class="width_300 width_300_fix" style="text-align: center;">Credit Card</td>
											<td align="center" class="width_300 width_300_fix " style="text-align: center;">1111 2222 3333 4444</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">$200</td>
											<td align="center" class="width_100 width_100_fix" style="text-align: center;">
												<a href="#">Active</a>
											</td>
											<td align="center" class="width_100 width_100_fix" style="text-align: center;">
												<a class="btn btn-default noteicon" href="#" data-featherlight="#fl2" data-featherlight-variant="fixwidth">
													<img src="images/note.png">
												</a>
											</td>
										</tr>
										<tr>
											<td align="center" class="width_300 width_300_fix" style="text-align: center;">
												07/11/2015
												<span>11:47 AM</span>
											</td>
											<td align="center" class="width_200 width_200_fix">ik SDN Bhd</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">000001</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">000001</td>
											<td align="center" class="width_300 width_300_fix" style="text-align: center;">Credit Card</td>
											<td align="center" class="width_300 width_300_fix " style="text-align: center;">1111 2222 3333 4444</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">$200</td>
											<td align="center" class="width_100 width_100_fix" style="text-align: center;">
												<a href="#">Active</a>
											</td>
											<td align="center" class="width_100 width_100_fix" style="text-align: center;">
												<a class="btn btn-default noteicon" href="#" data-featherlight="#fl2" data-featherlight-variant="fixwidth">
													<img src="images/note.png">
												</a>
											</td>
										</tr>
										<tr>
											<td align="center" class="width_300 width_300_fix" style="text-align: center;">
												07/11/2015
												<span>11:47 AM</span>
											</td>
											<td align="center" class="width_200 width_200_fix">ik SDN Bhd</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">000001</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">000001</td>
											<td align="center" class="width_300 width_300_fix" style="text-align: center;">Credit Card</td>
											<td align="center" class="width_300 width_300_fix " style="text-align: center;">1111 2222 3333 4444</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">$200</td>
											<td align="center" class="width_100 width_100_fix" style="text-align: center;">
												<a href="#">Active</a>
											</td>
											<td align="center" class="width_100 width_100_fix" style="text-align: center;">
												<a class="btn btn-default noteicon" href="#" data-featherlight="#fl2" data-featherlight-variant="fixwidth">
													<img src="images/note.png">
												</a>
											</td>
										</tr>
										<tr>
											<td align="center" class="width_300 width_300_fix" style="text-align: center;">
												07/11/2015
												<span>11:47 AM</span>
											</td>
											<td align="center" class="width_200 width_200_fix">ik SDN Bhd</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">000001</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">000001</td>
											<td align="center" class="width_300 width_300_fix" style="text-align: center;">Credit Card</td>
											<td align="center" class="width_300 width_300_fix " style="text-align: center;">1111 2222 3333 4444</td>
											<td align="center" class="width_200 width_200_fix" style="text-align: center;">$200</td>
											<td align="center" class="width_100 width_100_fix" style="text-align: center;">
												<a href="#">Active</a>
											</td>
											<td align="center" class="width_100 width_100_fix" style="text-align: center;">
												<a class="btn btn-default noteicon" href="#" data-featherlight="#fl2" data-featherlight-variant="fixwidth">
													<img src="images/note.png">
												</a>
											</td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!--                         <div class="lightbox" id="fl2">
                            <h2>Notes</h2>
                            <p>
                                It's easy to override the styling of Featherlight. All you need to do is specify an additional class in the data-featherlight-variant of the triggering element. This class will be added and you can then override everything. You can also reset all CSS: <em>$('.special').featherlight({ resetCss: true });</em>
                            </p>
                        </div> -->
	</div>
</div>
<script type="text/javascript">
        $("#test-select ").treeMultiselect({
            enableSelectAll: true,
            sortable: true
        });
    </script>
<script src="assets/js-core/validation.js"></script>
<script>
	$.validate({
		lang : 'en',
		modules : 'date',
		inlineErrorMessageCallback : function($input, errorMessage, config) {
			$input.next('.custom_add').remove();
			if (errorMessage) {
				var htm = "<span class='help-block form-error custom_add'>" + errorMessage + "</span>";
				$($input).after(htm);
			}
		}
	});
</script>
<!-- EQul height js-->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
<!-- Theme layout -->
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/themes/admin/layout.js"/>"></script> --%>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script>
	$(function() {
		var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
		$("#tags").autocomplete({
			source : availableTags
		});
		$("#tagres").autocomplete({
			source : availableTags
		});
	});
</script>
