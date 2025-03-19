<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="select_pack_wrap marg-top-20">
	<div class="container">
		<header class="form_header">
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap Events-Listing-heading">Supplier Subscription Plans</h2>
			</div>
			<div class="h2"></div>
		</header>
	</div>
	<div class="sp_inner">
		<c:forEach items="${planList}" var="plan">
			<div class="sp_box1">
				<div class="spb_heading1">
					<c:out value="${plan.planName}" />
				</div>
				<div class="tpb_open_box">
					<c:out value="${plan.description}" escapeXml="false" />
				</div>
				<div class="choose_bttn">
					<a href="${pageContext.request.contextPath}/supplierSignup/${plan.id}" class="cb_style cb_grey hvr-pop">Get ${plan.planName}</a>
				</div>
			</div>
		</c:forEach>
		<c:if test="${empty planList}">
			<div class="sp_box2">
				<div class="spb_heading4">NO PLANS DEFINED. LOGIN AS ADMIN AND DEFINE SOME PLANS.</div>
			</div>
		</c:if>
	</div>
	<div class="clear"></div>
</div>
<style>
.spb_heading1 {
	font-size: 30px;
	text-transform: none;
}

.sp_box1 {
	border: 1px solid #ccc;
	margin-bottom: 10px;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/js/masonry.js"/>"></script>
<script>
	$(document).ready(function() {

		$('.sp_inner').masonry({
			// options
			itemSelector : '.sp_box1',
			gutter : 10
		});

		/*
		
		$('.planGroup').each(function(i) {
			$(this).addClass('planGroupColor_' + i);
		}); //cb_grey 
		$('.sp_box1, .sp_box2').each(function(i) {
			$(this).find("div[class^='spb_heading']").addClass('planColor_' + (i + 1));
			$(this).find(".spa_left_bot").addClass('planColor_' + (i + 1));
			$(this).find(".cb_style").addClass('planColorBg_' + (i + 1));
		}); */
	});
</script>
