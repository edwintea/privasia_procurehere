<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:if test="${not empty success}">
	<div class="alert alert-success" id="idGlobalSuccess">
		<div class="bg-green alert-icon">
			<i class="glyph-icon icon-check"></i>
		</div>
		<div class="alert-content">
			<h4 class="alert-title">Success</h4>
			<c:choose>
				<c:when test="${!empty success && success.getClass().simpleName eq 'String'}">
					${success}
				</c:when>
				<c:otherwise>
					<c:forEach var="msg" items="${success}" >
						${msg}<br />
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>
 
<c:if test="${not empty param.success}">
	<div class="alert alert-success" id="idGlobalSuccess">
		<div class="bg-green alert-icon">
			<i class="glyph-icon icon-check"></i>
		</div>
		<div class="alert-content">
			<h4 class="alert-title">Success</h4>
			<c:choose>
				<c:when test="${!empty param.success && param.success.getClass().simpleName eq 'String'}">
					${param.success}
				</c:when>
				<c:otherwise>
					<c:forEach var="msg" items="${param.success}" >
						${msg}<br />
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>

<c:if test="${not empty info}">
	<div class="alert alert-notice" id="idGlobalInfo">
		<div class="bg-blue alert-icon">
			<i class="glyph-icon icon-info"></i>
		</div>
		<div class="alert-content">
			<h4 class="alert-title">Info</h4>
			<c:choose>
				<c:when test="${!empty info && info.getClass().simpleName eq 'String'}">
					${info}
				</c:when>
				<c:otherwise>
					<c:forEach var="msg" items="${info}" >
						${msg}<br />
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>
<c:if test="${not empty param.info}">
	<div class="alert alert-notice" id="idGlobalInfo">
		<div class="bg-blue alert-icon">
			<i class="glyph-icon icon-info"></i>
		</div>
		<div class="alert-content">
			<h4 class="alert-title">Info</h4>
			<c:choose>
				<c:when test="${!empty param.info && param.info.getClass().simpleName eq 'String'}">
					${param.info}
				</c:when>
				<c:otherwise>
					<c:forEach var="msg" items="${param.info}" >
						${msg}<br />
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>

<c:if test="${not empty warn}">
	<div class="alert alert-warning" id="idGlobalWarn">
		<div class="bg-orange alert-icon">
			<i class="glyph-icon icon-exclamation"></i>
		</div>
		<div class="alert-content">
			<h4 class="alert-title">Warning</h4>
			<c:choose>
				<c:when test="${!empty warn && warn.getClass().simpleName eq 'String'}">
					${warn}
				</c:when>
				<c:otherwise>
					<c:forEach var="msg" items="${warn}" >
						${msg}<br />
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>
<c:if test="${not empty param.warn}">
	<div class="alert alert-warning" id="idGlobalWarn">
		<div class="bg-orange alert-icon">
			<i class="glyph-icon icon-exclamation"></i>
		</div>
		<div class="alert-content">
			<h4 class="alert-title">Warning</h4>
			<c:choose>
				<c:when test="${!empty param.warn && warn.getClass().simpleName eq 'String'}">
					${param.warn}
				</c:when>
				<c:otherwise>
					<c:forEach var="msg" items="${param.warn}" >
						${msg}<br />
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if> 
<c:if test="${not empty error}">
	<div class="alert alert-danger" id="idGlobalError">
		<div class="bg-red alert-icon">
			<i class="glyph-icon icon-times"></i>
		</div>
		<div class="alert-content">
			<h4 class="alert-title">Error</h4>
			<c:choose>
				<c:when test="${!empty error && error.getClass().simpleName eq 'String'}">
					${error}
				</c:when>
				<c:otherwise>
					<c:forEach var="msg" items="${error}" >
						${msg}<br />
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>

<c:if test="${not empty param.error}">
	<div class="alert alert-danger" id="idGlobalError">
		<div class="bg-red alert-icon">
			<i class="glyph-icon icon-times"></i>
		</div>
		<div class="alert-content">
			<h4 class="alert-title">Error</h4>
			<c:choose>
				<c:when test="${!empty param.error && param.error.getClass().simpleName eq 'String'}">
					${param.error}
				</c:when>
				<c:otherwise>
					<c:forEach var="msg" items="${param.error}" >
						${msg}<br />
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>

<c:if test="${not empty errors}">
	<div class="alert alert-danger" id="idGlobalError">
		<div class="bg-red alert-icon">
			<i class="glyph-icon icon-times"></i>
		</div>
		<div class="alert-content">
			<h4 class="alert-title">Error</h4>
			<c:choose>
				<c:when test="${!empty errors && errors.getClass().simpleName eq 'String'}">
					${errors}
				</c:when>
				<c:otherwise>
					<c:forEach var="msg" items="${errors}" >
						${msg}<br />
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>

