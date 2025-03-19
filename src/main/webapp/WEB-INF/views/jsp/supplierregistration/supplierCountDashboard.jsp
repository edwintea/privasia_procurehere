<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div class="main-div marg-top-20">

	<div class="box-main scheduledContainer" data-target="pendingEventsData">
		<div class="box-top orange">
			<span><spring:message code="buyercreation.newEvent" /> </span> <img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/pending-event.png" alt="pending">
		</div>

		<div class="box-bottom width50">
			<div class="orange-con border-right-shaded pendingContainer">${eventPendingCount}</div>
			<span><spring:message code="supplier.dashboard.invited" /></span>
		</div>
		<div class="box-bottom width50">
			<div class="orange-con pendingContainer">${eventAccseptCount}</div>
			<span><spring:message code="supplier.dashboard.accepted" /></span>
		</div>


		<%-- <div class="box-bottom">
			<div class="orange-con pendingContainer border-right-shaded">${eventPendingCount}</div>
			<span>Invited</span>
		</div>

		
		<div class="box-bottom width50">
			<div class="orange-con draftContainer">0</div>
			<span>PR</span>
		</div>
 --%>

	</div>

	<div class="box-main activeContainer" data-target="activeEventsData">
		<div class="box-top limegreen">
			<span><spring:message code="supplier.dashboard.active" /></span> <img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/ongoing-event.png" alt="ongoing-event">
		</div>
		<div class="box-bottom width50">
<%-- 		${eventActiveCount} --%>
			<div class="limegreen-con border-right-shaded activeContainer">${eventActivePendingCount}</div>
			<span><spring:message code="buyercreation.pending" /></span>
		</div>
		<div class="box-bottom width50">
			<div class="limegreen-con  activeContainer">${eventActiveSubmittedCount}</div>
			<span><spring:message code="supplier.dashboard.submitted" /></span>
		</div>

	</div>
	
	<div class="box-main suspendedContainer" data-target="suspendedEventsData">
		<div class="box-top sky-blue">
			<span><spring:message code="buyer.dashboard.suspended" /></span> <img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/suspend-event.png" alt="active">
		</div>
		<div class="box-bottom">
			<div class="sky-blue-con suspendedContainer">${eventSuspendedCount}</div>
			<span><spring:message code="buyer.dashboard.suspended" /></span>
		</div>
	</div>
	
	<div class="box-main closedContainer" data-target="closedEventsData">
		<div class="box-top perpal">
			<span><spring:message code="buyer.dashboard.closed" /></span> <img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/cancel-event.png" alt="cancel-event">
		</div>
		<div class="box-bottom">
			<div class="perpal-con  closedContainer">${eventClosedCount}</div>
			<span><spring:message code="buyer.dashboard.ended" /></span>
		</div>
	</div>

	<br>
	<%--  <div class="box-main completeContainer" data-target="completeEventsData">
		<div class="box-top yellow">
			<span>Completed</span>
			<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/pending-event.png" alt="complete">
		</div>
		<div class="box-bottom">
			<div class="yellow-con completeContainer">${eventCompletedCount}</div>
			<span>Submitted</span>
		</div>
	</div>
	 --%>
	<div class="box-main rejectedContainer" data-target="rejectedEventsData">
		<div class="box-top" style="background: #e93535;">
			<span><spring:message code="supplier.dashboard.rejected2" /></span> <img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/cancel-event.png" alt="rejected" >
		</div>
		<div class="box-bottom">
			<div class="red-con  rejectedContainer">${eventRejectedCount}</div>
			<span><spring:message code="supplier.dashboard.rejected2" /></span>
		</div>

	</div>
	<div class="box-main prcheseOrdersContainer" data-target="prcheseOrdersData">
		<div class="box-top light-gray">
			<span><spring:message code="supplier.dashboard.purchase.orders" /></span> <img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/per-order.png" alt="per-order">
		</div>
		<div class="box-bottom width50">
			<div class="light-gray-con border-right-shaded prcheseOrdersContainer">${orderedPoCount}</div>
			<span><spring:message code="supplier.dashoboard.ordered.po" /></span>
		</div>
		<div class="box-bottom width50">
			<div class="light-gray-con prcheseOrdersContainer">${acceptedPoCount}</div>
			<span><spring:message code="supplier.dashboard.accepted" /></span>
		</div>
	</div>

	<div class="box-main buyerFormsContainer" data-target="supplierFormsData">
		<div class="box-top green">
			<span><spring:message code="dashboard.buyer.forms" /></span> <img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/draft-event.png" alt="per-order">
		</div>
		<div class="box-bottom width50">
			<div class="green-con border-right-shaded buyerFormsContainer">${pendingFormCount}</div>
			<span><spring:message code="buyercreation.pending" /></span>
		</div>
		<div class="box-bottom width50">
			<div class="green-con buyerFormsContainer">${submittedFormCount}</div>
			<span><spring:message code="buyer.form.submitted" /></span>
		</div>
	</div>
	
</div>
<style>
.box-bottom.width50 {
	width: 50%;
}

.main-div {
	/* float: left; */
	/* float: left; */
	max-width: 900px;
	text-align: center;
	margin-left: auto;
	margin-right: auto;
	width: 100%;
}

.box-bottom span {
	font-size: 16px;
	width: 100%;
	left: 0;
	position: absolute;
	top: 0;
	line-height: 16;
	height: 100%;
}

.box-bottom div {
	width: 100%;
	line-height: 3;
}

.border-right-shaded {
	border-width: 1px;
	border-left: 0;
	border-style: solid;
	-webkit-border-image: -webkit-gradient(linear, 0 100%, 0 0, from(#CCC),
		to(rgba(0, 0, 0, 0))) 1 100%;
	-webkit-border-image: -webkit-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0))
		1 100%;
	-moz-border-image: -moz-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0))
		1 100%;
	-o-border-image: -o-linear-gradient(bottom, #CCC, rgba(0, 0, 0, 0)) 1
		100%;
	border-image: linear-gradient(to top, #CCC, rgba(0, 0, 0, 0)) 1 100%;
}

.box-main {
	border: 1px solid #eeeff0;
	border-radius: 0;
	margin: 0 1% 30px;
	height: 150px;
	overflow: hidden;
	position: relative;
	width: 200px;
	cursor: pointer;
	display: inline-block;
}

.box-top {
	top: 0;
	color: #fff;
	font-size: 14px;
	line-height: 18px;
	padding: 10px 3%;;
	position: absolute;
	text-align: left;
	text-transform: uppercase;
	width: 100%;
	border-radius: 0;
}

.box-bottom {
	float: left;
	padding: 23% 0 13% 0;
	position: relative;
	text-align: center;
	width: 100%;
	font-size: 34px;
}

.box-top span {
	float: left;
	line-height: 25px;
	margin-left: 3%;
	width: 79%;
}

.yellow {
	background: #F9C851;
	border: 1px solid #F9C851;
	color: #ffffff !important;
}

.yellow-con {
	/* font-size: 60px; */
	color: #F9C851;
	display: inline-block;
}

.orange {
	background: #FFA500;
	border: 1px solid #F9C851;
}

.limegreen {
	background: #32CD32;
	border: 1px solid #32CD32;
}

.green {
	color: #fff !important;
	background: #06CCB3;
	border: 1px solid #06CCB3;
}

.crimson {
	background: #ff5b5b;
	border: 1px solid #FF5B5B;
}

.gold {
	background: #FFD700;
	border: 1px solid #FFD700;
}

.navy {
	background: #000080;
	border: 1px solid #000080;
}

.navy-con {
	/* font-size: 60px; */
	color: #0000CD;
	display: inline-block;
}

.gold-con {
	/* font-size: 60px; */
	color: #FFD700;
	display: inline-block;
}

.red {
	background: #FF5B5B;
	border: 1px solid #FF5B5B;
}

.blue {
	background: #627fa7;
	/* border: 1px solid #627fa7; */
}

.sky-blue {
	background: #35b4e9;
	border: 1px soild #35b4e9;
}

.sky-blue-con {
	/* font-size: 60px; */
	color: #35b4e9;
	display: inline-block;
}

.coffi {
	background: #cebf98;
	border: 1px solid #cebf98;
}

.light-blue {
	background: #00d1c6;
	border: 1px solid #00d1c6;
}

.light-blue-con {
	/* font-size: 60px; */
	color: #00d1c6;
	display: inline-block;
}

.light-gray {
	background: #727c88;
	/* border: 1px solid #727c88; */
}

.light-gray-con {
	/* font-size: 60px; */
	color: #727c88;
	display: inline-block;
}

.blue {
	background: #0095d5;
	/* border: 1px solid #336600; */
}

.blue-con {
	color: #0095d5;
	display: inline-block;
}

.perpal {
	background: #8809ff;
	border: 1px solid #8809ff;
}

.perpal-con {
	/* font-size: 60px; */
	color: #8809ff;
	display: inline-block;
}

.db-li {
	line-height: 19px !important;
}

.limegreen-con {
	/* font-size: 60px; */
	color: #32CD32;
}

.crimson-con {
	/* font-size: 60px; */
	color: #ff5b5b;
}

.red-con {
	/* font-size: 60px; */
	color: #e93535;
}

.blck-con {
	font-size: 25px;
	color: #333333;
}

.orange-con {
	/* font-size: 60px; */
	color: #FFA500;
	display: inline-block;
}

.green-con {
	/* font-size: 60px; */
	color: #06ccb3;
	display: inline-block;
}

.gray-con {
	/* font-size: 60px; */
	color: #333333;
	display: inline-block;
}

.bottom-text {
	bottom: 5px;
	color: #333;
	float: left;
	font-size: 22px;
	width: 100%;
	margin-top: 30px;
}

.box-top img {
	width: 30px;
	vertical-align: top;
	float: right;
}
</style>
<script>
	$(window).bind('load', function() {
		$('.box-main').click(function(e) {
			e.preventDefault();
			var targetElm = $(this).attr('data-target');
			$('.Invited-Supplier-List.dashboard-main.tabulerDataList').addClass('flagvisibility');
			var showTable = $('.Invited-Supplier-List.dashboard-main.tabulerDataList.' + targetElm);

			// Max 3 tables in one group. Try each
			if (typeof window[targetElm] !== 'undefined') {
				window[targetElm].ajax.reload();
			}
			if (typeof window[targetElm + '1'] !== 'undefined') {
				window[targetElm + '1'].ajax.reload();
			}
			if (typeof window[targetElm + '2'] !== 'undefined') {
				window[targetElm + '2'].ajax.reload();
			}

			showTable.removeClass('flagvisibility');
			//JS error in pending events error fixed
			if (showTable.length) {

				$('html,body').animate({
					scrollTop : showTable.offset().top
				}, 'slow');
			}
		});
	});
</script>
