<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="year" value="${now}" pattern="yyyy" />
<!-- ------------------------------------Footer Section Starts----------------------------------------- -->

<div class="container-fluid footer-div">
	<div class="container">
		<div class="row">
			<div class="col-sm-3 col-md-3 col-lg-3 col-xs-12 footer-logo-div">
				<img src="<c:url value="/resources/images/public/procurehere-footer.png"/>" alt="footer-logo">
				<p>
					Unit C-21-02, 3 Two Square,<br>No.2, Jalan 19/1,<br>46300 Petaling Jaya<br>Selangor, Malaysia
				</p>
				<p>
					<a href="mailto:info@procurehere.com">info@procurehere.com</a>
				</p>
			</div>
			<div class="col-sm-2 col-md-2 col-lg-2 col-xs-12">
				<ul>
					<li>PRODUCT</li>
					<li><a href="<spring:message code="app.root.url" />/buyer/">Buyer</a></li>
					<li><a href="<spring:message code="app.root.url" />/supplier/">Supplier</a></li>
					<li><a href="<spring:message code="app.root.url" />/rfx/">RFx</a></li>
					<li><a href="<spring:message code="app.root.url" />/auctions/">Auctions</a></li>
					<li><a href="<spring:message code="app.root.url" />/requisitioning/">Requisitioning</a></li>
					<li><a href="<spring:message code="app.root.url" />/pricing">Pricing</a></li>
				</ul>
			</div>
			<div class="col-sm-2 col-md-2 col-lg-2 col-xs-12">
				<ul>
					<li>COMPANY</li>
					<li><a href="<spring:message code="app.root.url" />/about-us/">About Us</a></li>
					<li><a href="#">Privacy Policy</a></li>
					<li><a href="<c:url value="/resources/termsandcondition.pdf"/>">Terms & Conditions</a></li>
				</ul>
			</div>
			<div class="col-sm-2 col-md-2 col-lg-2 col-xs-12">
				<ul>
					<li>RESOURCES</li>
					<li><a href="<spring:message code="app.root.url" />/blog/">Blog</a></li>
				</ul>
			</div>
			<div class="col-sm-3 col-md-3 col-lg-3 col-xs-12 text-right">
				<ul>
					<li><a href="https://apps.apple.com/us/app/id1284687738">
							<img src="<c:url value="/resources/images/public/apple-app-store.png"/>" alt="app-store">
						</a></li>
					<li><a href="https://play.google.com/store/apps/details?id=com.procurehere.mobile">
							<img src="<c:url value="/resources/images/public/google-play.png"/>" alt="app-store">
						</a></li>
				</ul>
			</div>
		</div>
		  <div class="pset_footer1">&copy; ${year} <spring:message code="footer.all.rights.reserved"/></div>
	</div>
</div>

<!-- -------------------------------Footer Section Ends--------------------------------------------- -->


<script type="text/javascript">
	$(document).ready(function() {

		$("#toggleme").click(function() {
			$(".header-bottom").fadeToggle("slow");
		});

		$("#toggleme").click(function() {
			$(".navbar-nav").fadeToggle("slow");
		});

		$(".btn-search").click(function() {
			$(".input").toggleClass("active").focus;
			$(this).toggleClass("animate");
			$(".input").val("");
		});
	});
</script>

<style>
 .pset_footer1 {
    font-size: 13px !important;
    font-family: Open Sans;
    color: #ffffff;
    margin: 0 !important;
    text-align: center;
}
</style>