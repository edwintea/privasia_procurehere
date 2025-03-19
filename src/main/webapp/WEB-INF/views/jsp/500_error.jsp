<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/assets/helpers/boilerplate.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/assets/helpers/grid.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/assets/elements/response-messages.css">
<style>
html,body {
        height: 100%;
        overflow-x:hidden;
    }
    body {
        background: #fff;
        overflow: hidden;
    }
</style>
<img alt="" class="login-img wow fadeIn animated" src="${pageContext.request.contextPath}/resources/assets/image-resources/blurred-bg/blurred-bg-4.jpg" style="visibility: visible; animation-name: fadeIn;">  

<div class="center-vertical">
    <div class="center-content row">

        <div class="col-md-6 center-margin">
            <div class="server-message wow bounceInDown inverse">
                <h1><spring:message code="application.error2"/></h1>
                <h2><spring:message code="error500.syntex"/></h2>
                <p><spring:message code="error500.time"/></p>
            </div>
        </div>

    </div>
</div>
