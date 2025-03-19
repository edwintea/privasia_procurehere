<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" isELIgnored="false"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<fmt:setBundle basename="application" var="message" scope="application" />
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-core.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-widget.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-mouse.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-ui-position.js"/>"></script>
<div class="container margin-bottom-5">
  <div class="forgot-password">
    <img src="<c:url value="/resources/images/public/blue tick.png"/>" alt="success-icon" class="img-reponsive text-center">
    <h1>Thanks!</h1>
    <p>A reset link  has been sent to you. Please check your email for instructions.</p>
    </div>
</div>
<div><a href="#"><img src="<c:url value="/resources/images/public/chat-icon.png"/>"  alt="chat-icon" class="chat-icon"></a></div>