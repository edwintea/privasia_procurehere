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

<!-- body class="popup-userlist" -->
<img alt="" class="login-img wow fadeIn animated" src="<c:url value="/resources/assets/image-resources/blurred-bg/blurred-bg-7.jpg"/>" style="visibility: visible; animation-name: fadeIn;">  

<div class="center-vertical">
    <div class="center-content row">

        <div class="col-md-6 center-margin">
            <div class="server-message wow bounceInDown inverse">
                <h1><spring:message code="application.error1"/></h1>
                <h2><spring:message code="error400.page"/></h2>
                <p><spring:message code="error400.exist"/></p>

                <form>
                    <button type="submit" class="btn btn-lg btn-success" formaction="${pageContext.request.contextPath}/" formtarget="_self" formmethod="get" ><spring:message code="error400.return.previous"/></button>
                </form>
            </div>
        </div>

    </div>
</div>