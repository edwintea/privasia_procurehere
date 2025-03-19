<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.isBuyerErpEnable" var="isBuyerErpEnable" />
<div class="main-div marg-top-20">

    <div class="box-main draftContainer" >
        <div class="box-top yellow">
            <span><spring:message code="label.rftenvelop.status.draft"/></span>
            <img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/draft-event.png" alt="draft-event">
        </div>
        <center>
            <div class="box-bottom " data-target="DRAFT">
                <div class="border-right-shaded draftContainer">${draftPoCount}</div>
                <span><spring:message code="buyer.dashboard.po.draft"/></span>
            </div>
        </center>
    </div>

    <div class="box-main-col3" >
        <div class="box-top perpal">
            <span><spring:message code="buyer.dashboard.pendingapproval"/></span>
            <img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/pending-event.png" alt="draft-event">
        </div>
        <div class="box-bottom width30" data-target="PENDING">
            <div class="perpal-con border-right-shaded activeContainer" style="margin-top:-35%;">${pendingPoCount}</div>
            <span><spring:message code="buyer.dashboard.po.creation"/></span>
        </div>
        <div class="box-bottom width30" data-target="REVISION">
            <div class="perpal-con border-right-shaded activeContainer" style="margin-top:-35%;">${revisePoCount}</div>
            <span><spring:message code="buyer.dashboard.po.revision"/></span>
        </div>
        <div class="box-bottom width30" data-target="ONCANCELLATION">
            <div style="margin-left:15%;margin-top:-35%;" class="perpal-con closedContainer col3"  >${onCancellationPoCount}</div>
            <span style="margin-left:15%;"><spring:message code="buyer.dashboard.po.cancellation"/></span>
        </div>
    </div>

	<div class="box-main myTaskContainer" data-intro="This is your task list. You can click on it to view the action items below" data-position="bottom">
		<div class="box-top navy">
			<span><spring:message code="buyer.dashboard.po.ready"/></span>
			<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/pending-event.png" alt="draft-event">
		</div>
		<center>
            <div class="box-bottom " data-target="READY">
                <div class="navy-con draftContainer">${readyPoCount}</div>
                <span><spring:message code="buyer.dashboard.po.ready"/></span>
            </div>
        </center>
	</div>


	<div class="box-main scheduledContainer" >
		<div class="box-top orange">
			<span><spring:message code="buyer.dashboard.po.ordered"/></span>
			<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/ongoing-event.png" alt="ongoing-event">
		</div>
		<div class="box-bottom" data-target="ORDERED">
			<div class="orange-con scheduledContainer">${orderedPoCount}</div>
			<span><spring:message code="buyer.dashboard.po.ordered"/></span>
		</div>
	</div>
	<div class="box-main activeContainer" >
		<div class="box-top limegreen">
			<span><spring:message code="buyer.dashboard.po.acceptance"/></span>
			<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/ongoing-event.png" alt="ongoing-event">
		</div>
		<div class="box-bottom width50" data-target="ACCEPTED">
			<div class="limegreen-con border-right-shaded activeContainer">${acceptedPoCount}</div>
			<span><spring:message code="buyer.dashboard.po.accepted"/></span>
		</div>
		<div class="box-bottom width50" data-target="DECLINED">
			<div class="red">${declinedPoCount}</div>
			<span><spring:message code="buyer.dashboard.po.declined"/></span>
		</div>
	</div>

	<div class="box-main finishedContainer" >
		<div class="box-top redPo">
			<span><spring:message code="buyer.dashboard.po.suspended"/></span>
			<img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/ongoing-event.png" alt="ongoing-event">
		</div>
		<center>
		<div class="box-bottom" data-target="SUSPENDED">
			<div class="red finishedContainer">${suspendedPoCount}</div>
			<span><spring:message code="buyer.dashboard.po.suspended"/></span>
		</div>
		</center>
	</div>

    <div class="box-main prcheseOrdersContainer" >
        <div class="box-top light-blue">
            <span style="font-size: 13px;"><spring:message code="buyer.dashboard.po.closed"/></span>
            <img src="${pageContext.request.contextPath}/resources/images/buyerDashboardImages/cancel-event.png" alt="cancel-event">
        </div>
        <div class="box-bottom width50" data-target="CLOSED">
            <div class="light-blue-con border-right-shaded closedContainer">${closedPoCount}</div>
            <span><spring:message code="buyer.dashboard.po.closed"/></span>
        </div>
        <div class="box-bottom width50" data-target="CANCELLED">
            <div class="light-blue-con finishedContainer">${cancelledPoCount}</div>
            <span><spring:message code="buyer.dashboard.po.cancelled"/></span>
        </div>
    </div>

</div>
<style>
.box-bottom.width50 {
	width: 50%;
}
.box-bottom.width30 {
	width: 30%;
}
.box-top {
    height: 50px;
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
    line-height:3em;
}

.box-bottom.col3 div  {
	width: 100%;
}

.border-right-shaded {
	/* border-width: 1px;
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
	border-image: linear-gradient(to top, #CCC, rgba(0, 0, 0, 0)) 1 100%; */
	border-width: 1.5px 1.5px 1.5px 0;
    border-style: solid;
    -webkit-border-image:
      -webkit-gradient(linear, 0 0, 100% 0, from(black), to(rgba(0, 0, 0, 0))) 1 100%;
    -webkit-border-image:
      -webkit-linear-gradient(left, black, rgba(0, 0, 0, 0)) 1 50%;
    -moz-border-image:
      -moz-linear-gradient(left, black, rgba(0, 0, 0, 0)) 1 50%;
    -o-border-image:
      -o-linear-gradient(left, black, rgba(0, 0, 0, 0)) 1 50%;
    border-image:
      linear-gradient(to top, #CCC, rgba(0, 0, 0, 0)) 1 50%;
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

.box-main-col3{
	border: 1px solid #eeeff0;
	border-radius: 0;
	margin: 0 1% 30px;
	height: 150px;
	overflow: hidden;
	position: relative;
	width: 300px;
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
	line-height: 16px;
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

.redPo {
	background: #FF5B5B;
	border: 1px solid #FF5B5B;
}

.blue {
	background: #627fa7;
	border: 1px solid #627fa7;
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
	border: 1px solid #727c88;
}

.light-gray-con {
	/* font-size: 60px; */
	color: #727c88;
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
	color: #f9c851;
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
    var status="";

    $(window).bind('load', function() {

        $('.box-bottom').click(function(e) {
            e.preventDefault();
            var targetElm = $(this).attr('data-target');
            $('.Invited-Supplier-List.dashboard-main.tabulerDataList').addClass('flagvisibility');
            $('#status').val(targetElm);
            status=targetElm;

            $('#poList').DataTable().ajax.reload();

            $('html,body').animate({
                scrollTop : $('#poList').offset().top
            }, 'slow');
        });
    });
</script>