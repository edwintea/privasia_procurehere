<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" href="<c:url value="/resources/assets/icons/fontawesome/font-awesome.min.css"/>">
<script>
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
			<div id="tab-2" class="tab-content tab-main-inner pad_all_15" style="display: none;">
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap">Metrics</h2>
				
			</div>
				
			<div class="Section-title title_border marg-bottom-10 white-bg">
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<form>
					<div class="metric_tfild_wrap">
						<div class="metric_country1">
								<div class="row">
									<label>Select Country</label>
									<select name="searchCountry" data-validation="required" class="form-control chosen-select" id="selectCountry">
								<option value="">All Countries<option>	
									<c:forEach items="${countryList}" var="list">
								<option value="${list.id}">${list.countryName}</option>
							</c:forEach>
									</select>  
								</div>
							</div>
					</div>
					
					<div class="metric_dats_wrap">
						<div class="metric_date">
							<label>Start Date</label> 
							<input type="text" name="startDate" id="startDate" 
							class="bootstrap-datepicker form-control for-clander-view" 
							data-validation="required" data-validation-format="dd/mm/yyyy"
							placeholder="dd/MM/yyyy"/>
						</div>
						<div class="metric_date">
							<label><spring:message code="rfaevent.end.date"/></label>
							<input type="text" name="endDate" id="endDate"  class="bootstrap-datepicker form-control for-clander-view" 
							data-validation-format="dd/mm/yyyy" data-validation="required"
							 placeholder="dd/MM/yyyy" pattern="dd/MM/yyyy" />
							
						</div>
						<div class="metric_search">
							<button class="searchOwnerMetric btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" type="button">Search</button>
						</div>
					</div>
					<div class="clear"></div>
				</form>
			</div>
			<section class="Overview_section">
				<div class="ph_row">
					 <div class="ph_col-3" id="NewId">
						<article class="overview_box pending_item ">
							<div class="box_top white_box_brd">
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_blue" id="trail_pros">0</div>
									<span>Trials in Progress</span>
								</div>
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_blue">
										<a href="#" data-target="#conversion_rate" data-toggle="modal" id="conversionRate">0
										<c:if test="0">
										 <img src="${pageContext.request.contextPath}/resources/assets/images/pop_icon.png" width="12" height="13">
										</c:if>
										</a>
									</div>
									<span>Conversion Rate </span>
								</div>
							</div>
							<div class="metric_col_footer ftr_bg_blue">
								<span class="b_title">Growth Details</span>
							</div>
						</article>
					</div> 
					<div class="ph_col-3">
						<article class="overview_box pending_item ">
							<div class="box_top white_box_brd">
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_green">
										<a href="#" data-toggle="modal" data-target="#suspended_buyers" id="suspendedBuyers" >0
										<c:if test="0">
											<img src="${pageContext.request.contextPath}/resources/assets/images/pop_icon.png" width="12" height="13">
										</c:if>
										</a>
									</div>
									<span>Suspended Buyers</span>
								</div>
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_green" id="active_buy">0</div>
									<span>Buyer Active in the Period</span>
								</div>
							</div>
							<div class="metric_col_footer ftr_bg_green">
								<span class="b_title">Buyer Activity</span>
							</div>
						</article>
					</div>
					<div class="ph_col-3">
						<article class="overview_box pending_item">
							<div class="box_top white_box_brd">
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_orange">
										<a data-toggle="modal" data-target="#new_buyers" href="#" id="newBuyer">0
										<c:if test="0">
											<img src="${pageContext.request.contextPath}/resources/assets/images/pop_icon.png" width="12" height="13">
										</c:if>
										</a>
									</div>
									<span>New Buyers</span>
								</div>
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_orange">
										<a data-toggle="modal" data-target="#total_buyers" href="#" id="totalBuyers">0
										<c:if test="0">
											<img src="${pageContext.request.contextPath}/resources/assets/images/pop_icon.png" width="12" height="13">
										</c:if>
										</a>
									</div>
									<span>Total Buyer</span>
								</div>
							</div>
							<div class="metric_col_footer ftr_bg_orange">
								<span class="b_title">Buyer Info</span>
							</div>
						</article>
					</div>
					<div class="ph_col-3">
						<article class="overview_box pending_item">
							<div class="box_top white_box_brd">
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_drkplp" id="fail_pay">0</div>
									<span>Failed payment Transactions</span>
								</div>
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_drkplp">
									
										<a data-toggle="modal" data-target="#revenue_generated" href="#" id="revenueGenerated">0
									<c:if test="0">
											<img src="${pageContext.request.contextPath}/resources/assets/images/pop_icon.png" width="12" height="13">
										</c:if>
										</a>
									</div>
									<span>Revenue Generated</span>
								</div>
							</div>
							<div class="metric_col_footer ftr_bg_drkplp">
								<span class="b_title">Payment Info</span>
							</div>
						</article>
					</div>
					<div class="ph_col-3">
						<article class="overview_box pending_item">
							<div class="box_top white_box_brd">
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_peach" id="auto_ext">0</div>
									<span>Auto Extensions</span>
								</div>
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_peach" id="manu_ext">0</div>
									<span>Manual Extensions</span>
								</div>
							</div>
							<div class="metric_col_footer ftr_bg_peach">
								<span class="b_title">Extensions</span>
							</div>
						</article>
					</div>
					<div class="ph_col-3">
						<article class="overview_box pending_item">
							<div class="box_top white_box_brd">
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_red" id="eve_cancel">0</div>
									<span>Events Cancelled</span>
								</div>
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_red" id="tot_eve">0</div>
									<span>Total Finished Events</span>
								</div>
							</div>
							<div class="metric_col_footer ftr_bg_red">
								<span class="b_title">Events Status</span>
							</div>
						</article>
					</div>
					<div class="ph_col-3">
						<article class="overview_box pending_item">
							<div class="box_top white_box_brd">
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_darkgrey">
										<a data-toggle="modal" data-target="#average_time" href="#" id="averageTime">0(hour)
										<c:if test="0">
										<img src="${pageContext.request.contextPath}/resources/assets/images/pop_icon.png" width="12" height="13">
									</c:if>
										</a>
									</div>
									<span>Average Time Per Event</span>
								</div>
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_darkgrey" id="eve_cat">0</div>
									<span>Average Events per Category</span>
								</div>
							</div>
							<div class="metric_col_footer ftr_bg_darkgrey">
								<span class="b_title">Events Info</span>
							</div>
						</article>
					</div>
					<div class="ph_col-3">
						<article class="overview_box trial_item">
							<div class="box_top white_box_brd">
								<div class="databox_1 item_data">
									<div class="table_block">
										<div class="table_cell">
											<div class="mtr_box_heading  mtr_clr_aqua" id="tot_supp">0</div>
											<span>Total Suppliers</span>
										</div>
									</div>
								</div>
							</div>
							<div class="metric_col_footer ftr_bg_aqua">
								<span class="b_title">Suppliers Info</span>
							</div>
						</article>
					</div>
					<div class="ph_col-3">
						<article class="overview_box pending_item">
							<div class="box_top white_box_brd">
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_nvblue" id="tot_pr">0</div>
									<span>Total PR</span>
								</div>
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_nvblue" id="tot_po">0</div>
									<span>Total PO</span>
								</div>
							</div>
							<div class="metric_col_footer ftr_bg_nvblue">
								<span class="b_title">PR/PO</span>
							</div>
						</article>
					</div>
					<div class="ph_col-3">
						<article class="overview_box pending_item">
							<div class="box_top white_box_brd">
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_yellow">
										<a data-toggle="modal" data-target="#auction_total" href="#" id="totalSave">0
										<c:if test="0">
											<img src="${pageContext.request.contextPath}/resources/assets/images/pop_icon.png" width="12" height="13">
										</c:if>
										</a>
									</div>
									<span>Total Saving</span>
								</div>
								<div class="databox_1 item_data">
									<div class="mtr_box_heading  mtr_clr_yellow" id="averageSave">0
									
										<%-- <a data-toggle="modal" data-target="#auction_average" href="#" id="averageSave">0
										<c:if test="0">
											<img src="${pageContext.request.contextPath}/resources/assets/images/pop_icon.png" width="12" height="13">
										</c:if>
										</a> --%>
									</div>
									<span>Average Saving</span>
								</div>
							</div>
							<div class="metric_col_footer ftr_bg_yellow">
								<span class="b_title">Auctions info</span>
							</div>
						</article>
					</div>
				</div>
			</section>
		</div>
	</div>
</div>
</div>
<!-----------popup-------------------->
<div class="modal fade" id="event_per" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Events per category</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="model_pop_mid">
				<div class="mpm_left">
					<div class="mpm_row">Request for Information (RFI)</div>
					<div class="mpm_row">Request for Quotation (RFQ)</div>
					<div class="mpm_row">Request for Prposal (RFP)</div>
					<div class="mpm_row">Request for Tender (RFT)</div>
					<div class="mpm_row">Request for Auction (RFA)</div>
				</div>
				<div class="mpm_right">
					<div class="mpr_row">10</div>
					<div class="mpr_row">15</div>
					<div class="mpr_row">15</div>
					<div class="mpr_row">5</div>
					<div class="mpr_row">5</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<!-----------popup-------------------->
<div class="modal fade" id="average_time" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content" style ="width: 537px;">
			<div class="modal-header">
				<h3>Average Time Per Event</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="model_pop_mid">
				<div class="mpm_left" id="avgPerEvent">
					<div class="mpm_row"></div>
				</div>
				<div class="mpm_right" id="avgPerWeekCount">
					<div class="mpr_row"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<!-----------popup-------------------->
<div class="modal fade" id="total_events" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Total Events</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="model_pop_mid">
				<div class="mpm_left" id="totalEventPlan">
					<div class="mpm_row"></div>
				</div>
				<div class="mpm_right" id="totalEventCount">
					<div class="mpr_row"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<!-----------popup-------------------->
<div class="modal fade" id="total_buyers" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Total Buyers Per Plan</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="model_pop_mid">
				<div class="model_prpo">
					<div class="mprleft">Package</div>
					<div class="mprright">Buyer</div>
				</div>
				<div class="mpm_left2" id="totalBuyerPlan">
					<div class="mpm_row"></div>
				</div>
				<div class="mpm_right" id="totalBuyerCount">
					<div class="mpr_row"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- <!-----------popup-------------------->
<!-- ---------popup------------------ -->
<div class="modal fade" id="pr_count" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		Modal content
		<div class="modal-content">
			<div class="modal-header">
				<h3>Number PR and PO’s Raised</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="model_pop_mid">
				<div class="model_prpo">
					<div class="mprleft1">Subscription</div>
					<div class="mprmid1">PR Count</div>
					<div class="mprright1">PO Count</div>
				</div>
				<div class="mprpo_box1">
					<div class="mpm_row">Advanced</div>
					<div class="mpm_row">Plus</div>
					<div class="mpm_row">Premium</div>
				</div>
				<div class="mprpo_box2">
					<div class="mpr_row">10</div>
					<div class="mpr_row">15</div>
					<div class="mpr_row">20</div>
				</div>
				<div class="mprpo_box3">
					<div class="mpr_row">10</div>
					<div class="mpr_row">15</div>
					<div class="mpr_row">20</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<!-----------popup-------------------->
<div class="modal fade" id="new_buyers" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>New Buyers Per Plan</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="model_pop_mid">
				<div class="model_prpo">
					<div class="mprleft">Package</div>
					<div class="mprright">Buyer</div>
				</div>
				<div class="mpm_left2" id="newBuyerPlan">
					<div class="mpm_row myClear"></div>
					
				</div>
				<div class="mpm_right" id="newBuyerPlnCount">
					<div class="mpr_row"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<!-----------popup-------------------->
<div class="modal fade" id="suspended_buyers" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Suspended Buyers</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="model_pop_mid">
				<div class="model_prpo">
					<div class="mprleft">Package</div>
					<div class="mprright">Buyer</div>
				</div>
				<div class="mpm_left2" id="planType">
					<div class="mpm_row"></div>
				</div>
				<div class="mpm_right" id="planCount">
				<div class="mpr_row"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<!-----------popup-------------------->
<div class="modal fade" id="revenue_generated" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Revenue Generated</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="model_pop_mid">
				<div class="model_prpo">
					<div class="mprleft">Package</div>
					<div class="mprright">Revenue</div>
				</div>
				<div class="mpm_left2" id="revenuePlan">
					<div class="mpm_row"></div>
				</div>
				<div class="mpm_right" id="revenueCount">
					<div class="mpr_row"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<!-----------popup-------------------->
<div class="modal fade" id="conversion_rate" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Conversion Rate</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="model_pop_mid">
				<div class="model_prpo">
					<div class="mprleft">Package</div>
					<div class="mprright">Conversion Rate</div>
				</div>
				<div class="mpm_left2" id="conversionPlan">
					<div class="mpm_row"></div>
				</div>
				<div class="mpm_right" id="conversionCont">
					<div class="mpr_row"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<!-----------popup-------------------->
<div class="modal fade" id="auction_total" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content" style = "width: 454px;">
			<div class="modal-header">
				<h3>Total Saving for Auction</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="model_pop_mid">
			<div class="model_prpo">
					<div class="mprleft">Auction Type</div>
					<div class="mprright">Saving</div>
				</div>
				
				<div class="mpm_right" id="auctionTotalSaving">
					<div class="mpm_row" ></div>
				</div>
				<div class="mpm_left2" id="auctionTotalName">
					<div class="mpr_row"></div>
					
				</div>
				
			</div>
		</div>
	</div>
</div>
<!-----------popup-------------------->
<!-----------popup-------------------->
<div class="modal fade" id="auction_average" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Average Saving for Auction</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="model_pop_mid">
				<div class="mpm_left">
					<div class="mpm_row">Forward Auction</div>
					<div class="mpm_row">Reverse Auction</div>
					
				</div>
				<div class="mpm_right">
					<div class="mpr_row">10</div>
					<div class="mpr_row">5</div>
					
				</div>
			</div>
		</div>
	</div>
</div>



<!-- daterange picker js and css start -->
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
		var month_back = new Date(${currentTime});
		
		var now = new Date(${currentTime}); /*new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp
				.getDate(), 0, 0, 0, 0);*/
		
		//${currentTime}
		console.log(new Date(${currentTime}).getMonth());
		month_back.setDate(month_back.getDate()-30);

		
		var day = now.getDate();
		var mnth = now.getMonth()+1;
		
		var st_day = month_back.getDate();
		var st_mnth = month_back.getMonth()+1;
	
		$('#endDate').val((day<10 ? '0' : '') + day+'/'+(mnth<10 ? '0' : '') + mnth+'/'+now.getFullYear());
		$('#startDate').val((st_day<10 ? '0' : '') + st_day+'/'+(st_mnth<10 ? '0' : '') + st_mnth +'/'+month_back.getFullYear());
	
	//$('#endDate').val(now.getDate()+'/'+now.getMonth()+'/'+now.getFullYear());
	//$('#startDate').val((st_day<10 ? '0' : '') + st_day+'/'+(st_mnth<10 ? '0' : '') + st_mnth+'/'+month_back.getFullYear());
		$('#endDate').bsdatepicker({
			format : 'dd/mm/yyyy',
			minDate : now,
			onRender : function(date) {
					return date.valueOf() > now.valueOf() ? 'disabled' : '';
			}
		});
		
		
		$('#startDate').bsdatepicker({
			format : 'dd/mm/yyyy',
			defaultDate :month_back,
			minDate : now,
			onRender : function(date) {
				return date.valueOf() > now.valueOf() ? 'disabled' : '';
			}

		});
}) 
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

#auctionRow{
 padding-left: 116px !important;;
}
/* .item_data img {
    display: none;
} */
</style>

<script>
	$(function() {
		var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
		$("#tags").autocomplete({ source : availableTags });
		$("#tagres").autocomplete({ source : availableTags });
	})
</script>
 <script type="text/javascript" src="<c:url value="/resources/js/view/owner.js"/>"></script>
 <script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>  
