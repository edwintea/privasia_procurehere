<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<fmt:setBundle basename="application" var="message" scope="application" />
<fmt:message key="customer" bundle="${ message }" var="customer" />

<!-- -----------------------------Header Section Starts-------------------------------- -->
<div class="header">

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/border-radius.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/typography.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/utils.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/colors.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/response-messages.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/themes/admin/procurehere.css?2"/>">


	<style>
		.header-bottom {
			float: right !important;
			width: auto !important;
		}
	</style>

	<div class="header_top">
		<div class="logo buyer-check-logo">
			<div class="col-md-2" style="margin-top: 20px;">
				<a class="" href="<spring:message code="app.root.url" />">
					<c:if test="${companyLogo == null}">
						<img src="<c:url value="/resources/images/public/procurehere-header.png"/>" alt="Procurement Most Intuitive e-procurement system" />
					</c:if>
					<c:if test="${companyLogo != null}">
						<img src="data:image/png;base64, ${companyLogo}" alt="${companyName != null ? companyName : 'Procurement - Most intuitive e-procurement system'}" />
					</c:if>
				</a>
			</div>
			<div class="tab-icons"><i class="fa fa-bars font-30" onclick="openNavTab()"></i><i class="fa fa-search btn-search-tab"></i>
				<br><input type="text" placeholder="Search" class="tab-search tab-view-search">
			  </div>
			<!-- <div>
				<input type="text" placeholder="Search" class="media-input form-control" />
				<div class="menu-btn" id="toggleme">
					<i class="fa fa-bars"></i>
				</div>
			</div> -->
		</div>

		<div id="mySidenav-tab" class="sidenav text-left">
			<a href="javascript:void(0)" class="closebtn" onclick="closeNavTab()">&times;</a>
			<li class="width-51"><a href="<spring:message code="app.url"/>/login" class="signin_bttn">Login</a></li>
		</div>

		<div id="mySidenav" class="sidenav">
			<a href="javascript:void(0)" class="closebtn" onclick="closeNav()">&times;</a>
		</div>

		<div class="mobile-icons float-right">
			<span>
				<a href="<spring:message code="app.url"/>/login">
					<i class="fa fa-sign-in"> </i>
				</a>
			</span>
<!-- 			<span>
				<i class="fa fa-bars fa-2x pull-right bars-icon" onclick="openNav()"></i>
			</span>
 -->		</div>
		<div class="header-bottom">
			<div class="header-bottom-center">
				<div class="float-right">
					<div class="nav_inner">
						<ul class="nav menu">
							<li><a href="<spring:message code="app.url"/>/login" class="signin_bttn float-right">Login</a></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- ------------------------------Head Section Ends------------------------------- -->

<script>
// ----------------Sidebar----------------
function openNav() {
    document.getElementById("mySidenav").style.width = "250px";
   document.getElementById("main").style.marginLeft = "-250px";
    /* document.getElementById("main").style.transition = "all .5s ease"; */
    document.body.style.backgroundColor = "rgba(0,0,0,0.4)";
    document.body.style.overflow = "hidden";
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0";
   document.getElementById("main").style.marginLeft = "auto";
    document.body.style.backgroundColor = "white";
    document.body.style.overflow = "auto";

}
function openNavTab() {
    document.getElementById("mySidenav-tab").style.width = "250px";
   document.getElementById("main").style.marginLeft = "-250px";
    document.body.style.backgroundColor = "rgba(0,0,0,0.4)";
    document.body.style.overflow = "hidden";
}

function closeNavTab() {
    document.getElementById("mySidenav-tab").style.width = "0";
    document.getElementById("main").style.marginLeft = "auto"; 
    document.body.style.backgroundColor = "white";
    document.body.style.overflow = "auto";

}

$(".search-icon").click(function(){
  $(".tab-search").fadeToggle(500);
  $(".tab-search").toggleClass('tab-search-width');

});
$(".btn-search-tab").click(function(){
  $(".tab-search").fadeToggle(500);
  $(".tab-search").toggleClass('tab-search-width');

});
$(".closebtn, .bars-icon").click(function(){
  $(".tab-search").hide();
  $(".tab-search").removeClass('tab-search-width');
  

});


</script>