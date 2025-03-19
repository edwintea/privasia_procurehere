<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
    <!-- Favicons -->
    <link rel="shortcut icon" href="<c:url value="/resources/assets/images/icons/favicon.png"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/boilerplate.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/helpers/grid.css"/>">
	<style>
	    body {
	        background: #fff;
	        overflow: hidden;
	        overflow-x: hidden;
	    }
	</style>

<!-- body style="overflow-x:hidden;" class="popup-userlist" -->
<img alt="" class="login-img wow fadeIn animated" src="<c:url value="/resources/assets/image-resources/blurred-bg/blurred-bg-4.jpg"/>" style="visibility: visible; animation-name: fadeIn;">  

<div class="center-vertical">
    <div class="center-content row">

        <div class="col-md-6 center-margin">
            <div class="server-message wow bounceInDown inverse">
                <h1><spring:message code="application.error"/></h1>
                <h2><spring:message code="error.access"/></h2>
                <p>${requestedUrl}</p>
                <p><spring:message code="error.login"/>
                	<a href="<c:url value="/login"/>" ><spring:message code="application.login"/></a> 
                </p>
            </div>
        </div>

    </div>
</div>

