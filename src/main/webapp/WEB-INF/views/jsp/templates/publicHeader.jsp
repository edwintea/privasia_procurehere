<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<fmt:setBundle basename="application" var="message" scope="application" />
<fmt:message key="customer" bundle="${ message }" var="customer" />

<!-- -----------------------------Header Section Starts-------------------------------- -->
<div class="header">
	<div class="header_top">
		<div class="logo buyer-check-logo">
			<div class="col-md-2" style="margin-top: 20px;">
				<a class="" href="<spring:message code="app.root.url" />">
					<img src="<c:url value="/resources/images/public/procurehere-header.png"/>" alt="Procurement Most Intuitive e-procurement system" />
				</a>
			</div>
			<c:if test="${customer == 'sme'}">
				<div class="client col" style="padding:0px;">
					<a class="" href="javascript:void(0);">
						<img src="<c:url value="/resources/images/customer_logo/smebank_logo.png"/>" alt="SME Bank" />
					</a>
				</div>
			</c:if>
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
			<li class="text-center" style="border: none"><a class="#"><i class="fa fa-search search-icon"></i>
			<form action="<spring:message code="app.root.url" />/" method="get" autocomplete="off">
			  <input type="text" name="s" id="s1" placeholder="Search" class="tab-search">
			  </form>
			</a></li>
			<li><a href="<spring:message code="app.root.url" />/buyer/">Buyer</a></li>
					<li><a href="<spring:message code="app.root.url" />/supplier/">Supplier</a></li>
					<li><a href="<spring:message code="app.root.url" />/pricing/">Pricing</a></li>
					<li><a href="<spring:message code="app.root.url" />/about-us/">About Us</a></li>
					<li><a href="<spring:message code="app.root.url" />/blog/">Blog</a></li>
					<li class="width-51"><a href="<spring:message code="app.root.url" />" class="start-free-trl-btn">Start Free Trial</a></li>
					<li class="width-51"><a href="<spring:message code="app.url"/>/login" class="signin_bttn">Login</a></li>
					<li class="phone-li padding-15-25"><a href="#" class="icon-margin-10"><i class="fa fa-phone"></i><a></li>
		  </div>

		  <div id="mySidenav" class="sidenav">
			<a href="javascript:void(0)" class="closebtn" onclick="closeNav()">&times;</a>
			  <li class="text-center" style="border: none"><a class="#"><i class="fa fa-search search-icon"></i>
				<form action="<spring:message code="app.root.url" />/" method="get" autocomplete="off">
				<input type="text" name="s" id="s2" placeholder="Search" class="tab-search">
				</form>
			  </a></li>
			  <li><a href="<spring:message code="app.root.url" />/buyer/">Buyer</a></li>
					  <li><a href="<spring:message code="app.root.url" />/supplier/">Supplier</a></li>
					  <li><a href="<spring:message code="app.root.url" />/pricing/">Pricing</a></li>
					  <li><a href="<spring:message code="app.root.url" />/about-us/">About Us</a></li>
					  <li><a href="<spring:message code="app.root.url" />/blog/">Blog</a></li>
			</div>

		<div class="mobile-icons">
			<span>
				<a href="<spring:message code="app.root.url" />/free-trial/">
					<i class="fa fa-user-plus"> </i>
				</a>
				<a href="<spring:message code="app.url"/>/login">
					<i class="fa fa-sign-in"> </i>
				</a>
				<i class="fa fa-search search-icon"> </i>
			</span>
			<span>
				<i class="fa fa-bars fa-2x pull-right bars-icon" onclick="openNav()"></i>
			</span>
			<br>
			<form action="<spring:message code="app.root.url" />/" method="get" autocomplete="off">
				<input type="text" name="s" id="s3" placeholder="Search" class="tab-search mobile-search">
			</form>
		</div>
		<div class="header-bottom">
			<div class="header-bottom-center">
				<div class="menu_wrap">
					<div class="nav_inner">
						<ul class="nav menu">
							<li><a href="<spring:message code="app.root.url" />/buyer/">Buyer</a></li>
							<li><a href="<spring:message code="app.root.url" />/supplier/">Supplier</a></li>
							<li><a href="<spring:message code="app.root.url" />/pricing/">Pricing</a></li>
							<li><a href="<spring:message code="app.root.url" />/about-us/">About Us</a></li>
							<li><a href="<spring:message code="app.root.url" />/blog/">Blog</a></li>
							<li><a href="<spring:message code="app.root.url" />/free-trial/" class="start-free-trl-btn" >Start Free Trial</a></li>
							<li><a href="<spring:message code="app.url"/>/login" class="signin_bttn">Login</a></li>
							<li><div class="wrapper">
									<div class="search-box">
										<form action="<spring:message code="app.root.url" />/" method="get" autocomplete="off">
										<input type="text" name="s" id="s4" placeholder="Search" class="input">
										<div class="btn-search">
											<i class="fa fa-search" aria-hidden="true"></i>
										</div>
										<input type="submit" class="submit" value="" style="display:none;">
										</form>
									</div>
								</div>
							</li>							
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