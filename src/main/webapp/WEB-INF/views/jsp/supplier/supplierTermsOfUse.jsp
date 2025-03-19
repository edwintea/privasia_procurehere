<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="year" value="${now}" pattern="yyyy" />
<head>

<link href="<c:url value="/resources/assets/elements/saas.css"/>" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/component.css"/>" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/response-messages.css"/>">
<link rel="stylesheet" href="<c:url value="/resources/assets/elements/reset.css"/>" />
<!-- CSS reset -->
<link rel="stylesheet" href="<c:url value="/resources/assets/elements/testimonail.css"/>" />
<!-- Resource style -->
<script src="<c:url value="/resources/assets/js-core/modernizr.custom.js"/>" /></script>
<script src="<c:url value="/resources/assets/js-core/jquery.dlmenu.js"/>" /></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/bootstrap/css/bootstrap.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
</head>
<div class="wrapper ev-de">
	<div class="header">
		<header id="header">
			<div class="header_top">
				<div class="logo">
					<a href="${pageContext.request.contextPath}"> <img src="${pageContext.request.contextPath}/resources/assets/images/saas_pro_logo.png">
					</a>
				</div>

				<div class="clear"></div>
			</div>
		</header>
	</div>
	<div class="pro_set_wrap">
		<div style="height: 430px;" class="banner_area">
			<img src="<c:url value="/resources/assets/images/terms_header.jpg"/>" class="width100" alt="terms of use">
		</div>
		<div class="tou_mid">
			<%-- <jsp:include page="../termsAndConditions.jsp" /> --%>

			<div class="tom_top" style="min-height: 464px;">
				<div class="clear error_div">
					<div class="col-md-12">
						<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					</div>
				</div>
				<div class="tom_box">
					<div class="tab-content current tc_box" id="tab-1" style="display: none;margin-top: 4%;padding: 3% 0 3% 0;">
						<div class="tom_dis_headding tc-style06">SaaS Terms and Conditions</div>
						<div class="tom_dis_content">
						<%-- <jsp:include page="../termsAndConditionsContent.jsp" /> --%>
						<div>
								<div class="tc-style06">
									Please click here to download and view <a target="_blank"
										href="<c:url value="/resources/termsandcondition.pdf"/>">Terms
										and conditions</a>
								</div>
								<div class="tc-style06">
									<span>
										<input type="checkbox" ${not empty errors ? 'disabled="disabled"' : ''} name="agree" id="agree" value="agree">&nbsp;I have read and agree with the above terms and conditions.
									</span>
								</div>
						</div>


						</div>
						<div class="tom_bot row">
							<!-- <div class="col-md-3"></div> -->
							<div class="col-md-6 tc-style06" style="padding-left: 0px">
								<c:url var="supplierTermsOfUse" value="supplierTermsOfUse" />
								<form:form action="${supplierTermsOfUse}" id="demo-form" method="post" autocomplete="off" modelAttribute="supplier">
									<form:hidden path="id" />
									<div class="tob_btn">
										<%-- <c:if test="${empty error}"> --%>
										<button type="submit" id="agreeSubmit" disabled="disabled" class=" tbb_blue hvr-pophvr-rectangle-out">Accept</button>
										<%-- </c:if> --%>
									</div>
									<div class="tob_btn">
										<button type="button" class="tbb_grey  hvr-pop hvr-rectangle-out1" id="buyerCancel">Cancel</button>
									</div>
									<div class="clear"></div>
								</form:form>
							</div>
							<!-- <div class="col-md-3"></div> -->
						</div>
						<!--- For Buyer  ---->
					</div>
				</div>
			</div>


		</div>
		<div class="pset_footer">&copy; ${year} All rights reserved</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/view/buyer.js?1"/>"></script>



<script>
	$(document).ready(function(e) {
		$("#agree").click(function() {
			if ($('#agree').is(":checked")) {
				$("#agreeSubmit").attr("disabled", false);
			} else {
				$("#agreeSubmit").attr("disabled", true);
			}

		});
	});
</script>
<style>
.tob_agree {
	margin-top: 8%;
	margin-left: 4%;
}

* {
	color: #000;
}

.tc-style01 {
	text-align: justify;
	margin-bottom: 8.4px;
	font-size: 13px;
	font-family: arial, sans-serif;
}

.tc-def {
	text-align: justify;
	margin-bottom: 1.4px;
	font-size: 16px;
	font-family: arial, sans-serif;
}

.tc-style02 {
	margin-bottom: 8.4px;
}

.tc-style03 {
	font-size: 13px;
	font-family: arial, sans-serif;
}

.tc-style04 {
	page-break-before: always;
	margin-bottom: 8.4px;
}

.tc-style05 {
	text-align: justify;
	margin-left: 0.42in;
	text-indent: -0.42in;
	margin-bottom: 0.14in
}

.tc-style06 {
	text-align: justify;
	margin-left: 0.42in;
	margin-bottom: 0.14in
}

.subscription_tab {
	text-align: justify;
	/* margin-left: 0.42in; */
	margin-bottom: 0.14in
}

.tc-style07 {
	text-align: justify;
	margin-left: 0.83in;
	text-indent: -0.42in;
	margin-bottom: 0.14in;
}

.tc-style08 {
	text-align: justify;
	margin-left: 1.25in;
	text-indent: -0.42in;
	margin-bottom: 0.14in
}

.tc-style09 {
	text-align: justify;
	margin-left: 0.79in;
	text-indent: -0.39in;
	margin-bottom: 0.14in
}

.tc-style10 {
	text-align: justify;
	margin-left: 0.79in;
	text-indent: -0.37in;
	margin-bottom: 0.14in
}

.tc-style11 {
	margin-left: 0.39in;
	text-indent: -0.39in;
	margin-bottom: 0.14in
}

.tc-style12 {
	text-align: justify;
}

/* .tc-style06 {
	margin-bottom: 0in
}
 */
.tc-style14 {
	text-align: justify;
	margin-left: 0.39in;
	text-indent: -0.39in;
	margin-bottom: 0in
}

.tc-style15 {
	text-align: justify;
	margin-left: 0.55in;
	margin-bottom: 0in
}

.tc-style16 {
	width: 354px;
	background-color: #e7e6e6;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 0.07in;
	padding-bottom: 0.07in;
	padding-left: 0.07in;
	padding-right: 0in
}

.tc-style17 {
	width: 285px;
	background-color: #e7e6e6;
	border: 1.00pt solid #000000;
	padding: 0in 0.08in
}

.tc-style18 {
	padding-right: 38%;
	width: 354px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 0.07in;
	padding-bottom: 0.07in;
	padding-left: 0.07in;
	padding-right: 0.07in
}

.tc-style19 {
	width: 197px;
	border: 1.00pt solid #000000;
	padding: 0in 0.08in;
}

.tc-style20 {
	margin-left: 0.39in;
	text-align: justify;
	margin-bottom: 0in
}

.tc-style21 {
	margin-left: 0.49in;
	text-indent: -0.49in;
	margin-bottom: 0in
}

.tc-style22 {
	width: 340px;
	background-color: #e7e6e6;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 0.07in;
	padding-bottom: 0.07in;
	padding-left: 0.07in;
	padding-right: 0in
}

.tc-style23 {
	width: 211px;
	background-color: #e7e6e6;
	border: 1.00pt solid #000000;
	padding: 0in 0.08in
}

.tc-style24 {
	width: 340px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 0.07in;
	padding-bottom: 0.07in;
	padding-right: 38%;
	padding-left: 0.07in;
	padding-right: 0in
}

.tc-style25 {
	width: 211px;
	border: 1.00pt solid #000000;
	padding: 0in 0.08in
}

.tc-style26 {
	border: 1.00pt solid #000000;
	padding: 0.07in
}

.tc-style27 {
	margin-left: 0.79in;
	text-indent: -0.39in;
	margin-bottom: 0in
}

.tc-style28 {
	margin-left: 0.25in;
	margin-bottom: 0in;
}

.tc-style29 {
	margin-left: 0.5in;
	margin-bottom: 0in;
}

.tc-style30 {
	width: 132px;
	background-color: #95b3d7;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: 1.00pt solid #000000;
	padding-top: 0.07in;
	padding-bottom: 0.07in;
	padding-left: 0.07in;
	padding-right: 0.07in
}

.tc-style31 {
	widows: 0;
	orphans: 0
}

.tc-style32 {
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 14px;
	padding-bottom: 0in;
	padding-left: 0.07in;
	padding-right: 0.07in
}

.tc-style33 {
	width: 133px;
	background-color: #dbe5f1
}

.tc-style34 {
	border: 1.00pt solid #000000;
	padding: 14px 65px;
}

.tc-style35 {
	text-align: center;
}

.tc-style36 {
	width: 20%;
	background-color: #dbe5f1;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 0.07in;
	padding-bottom: 0.03in;
	padding-left: 0.07in;
	padding-right: 0.07in
}

.tc-style37 {
	width: 178px;
	background-color: #c6d9f1;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: 1.00pt solid #000000;
	padding-top: 0.07in;
	padding-bottom: 0.07in;
	padding-left: 0.0in;
	padding-right: 0.07in;
}

.tc-style38 {
	width: 76px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 14px;
	padding-bottom: 0in;
	padding-left: 0.07in;
	padding-right: 0.07in;
	background-color: #c6d9f1;
}

.tc-style39 {
	width: 178px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 0.07in;
	padding-bottom: 0.07in;
	padding-left: 0.07in;
	padding-right: 0in
}

.tc-style40 {
	width: 28px;
	background-color: #c6d9f1;
	padding: 3% 4%;
	background-color: #c6d9f1;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
}

.tc-style41 {
	width: 136px;
	background-color: #c6d9f1;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 0.07in;
	padding-bottom: 0.07in;
	padding-left: 0.07in;
	padding-right: 0.07in
}

.tc-style42 {
	width: 376px;
	background-color: #c6d9f1;
}

.tc-style43 {
	width: 136px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 14px;
	padding-bottom: 0in;
	padding-left: 0.07in;
	padding-right: 0.07in;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 0.07in;
	padding-bottom: 0.07in;
	padding-left: 0.07in;
	padding-right: 0in
}

.tc-style44 {
	page-break-before: always
}

.tc-style45 {
	width: 99px;
	background-color: #dbe5f1;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0.07in
}

.tc-style46 {
	width: 99px;
	background-color: #fabf8f;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0.07in
}

.tc-style47 {
	background-color: #fabf8f;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 14px;
	padding-bottom: 0in;
	padding-left: 0.07in;
	padding-right: 0.07in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0.07in
}

.tc-style48 {
	width: 100px;
	background-color: #dbe5f1;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: 1.00pt solid #000000;
	padding: 0in 0.08in
}

.tc-style49 {
	width: 99px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 14px;
	padding-bottom: 0in;
	padding-left: 0.07in;
	padding-right: 0.07in;
}

.tc-style50 {
	width: 39px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1px solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style51 {
	width: 46px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1px solid #000000;
	border-left: 1px solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 14px;
	padding-bottom: 0in;
	padding-left: 0.07in;
	padding-right: 0.07in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style52 {
	width: 40px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1px solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style53 {
	width: 45px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1px solid #000000;
	border-left: 1px solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style54 {
	width: 41px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1px solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style55 {
	width: 100px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: 1.00pt solid #000000;
	padding: 0in 0.08in
}

.tc-style56 {
	width: 39px;
	border-top: 1px solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	tc-style32 padding-left: 0.08in;
	padding-right: 0in
}

.tc-style57 {
	width: 46px;
	border-top: 1px solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1px solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style58 {
	width: 40px;
	border-top: 1px solid #000000;
	tc-style32 border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style59 {
	width: 45px;
	border-top: 1px solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1px solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style60 {
	width: 41px;
	border-top: 1px solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style61 {
	width: 99px;
	background-color: #d6e3bc;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 14px;
	padding-bottom: 0in;
	padding-left: 0.07in;
	padding-right: 0.07in;
}

.tc-style62 {
	width: 39px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style63 {
	width: 46px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1px solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style64 {
	width: 40px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style65 {
	width: 45px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1px solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style66 {
	width: 41px;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 3.00pt solid #000000;
	border-right: none;
	padding-top: 0in;
	padding-bottom: 0in;
	padding-left: 0.08in;
	padding-right: 0in
}

.tc-style67 {
	width: 101px;
	background-color: #c6d9f1;
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 0.07in;
	padding-bottom: 0.07in;
	padding-left: 0.07in;
	padding-right: 0.07in
}

.tc-style69 {
	
}

.tc-style70 {
	margin-left: 0.42in;
}

.tc_box {
	width: 100%;
	float: left;
	/* max-width: 100%; */
	/* padding: 10px 2%; */
	border: 1px solid #e9e7e7;
	border-radius: 4px;
	margin-left: 12%;
	padding-top: 10px;
	padding-right: 7%;
	padding-bottom: 10px;
	padding-left: 2%;
}

.font-style {
	font-family: sans-serif;
}

.term-font {
	font-size: 20px;
}

.tom_box {
	/* border-top: 1.00pt solid #000000; */
	/* border-bottom: 1.00pt solid #000000; */
	/* border-left: 1.00pt solid #000000; */
	border-right: none;
	padding-top: 14px;
	padding-bottom: 0in;
	padding-left: 0.07in;
	padding-right: 0.07in;
	width: 80%;
	float: left;
}

.table-width {
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 14px;
	padding-bottom: 0in;
	padding-left: 1.0in;
	padding-right: 0.7in;
}

.table-data {
	border-top: 1.00pt solid #000000;
	border-bottom: 1.00pt solid #000000;
	border-left: 1.00pt solid #000000;
	border-right: none;
	padding-top: 14px;
	padding-bottom: 0in;
	padding-left: 1.0in;
	padding-right: 0.7in;
}

.tab-head {
	border: 1.00pt solid #000000;
	padding: 14px 10px;
}

@media only screen and (max-width: 600px) and (min-width: 320px) {
	.tc_box {
		width: 100%;
		float: left;
		padding: 0;
		border: 1px solid #e9e7e7;
		border-radius: 4px;
		margin-left: 0px;
		padding-top: 10px;
		padding-right: 38%;
		padding-bottom: 10px;
		padding-left: 0px;
	}
	table {
		width: 70%;
		margin-left: 0px;
	}
}

.error_div {
	margin-left: 9%;
	margin-right: 10%;
	margin-top: 0%;
}
</style>

