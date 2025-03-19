<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<style>
.tour-overlay {
	display: none;
	background: #666;
	opacity: 0.5;
	z-index: 9997;
	min-height: 100%;
	height: 100%;
	position: fixed;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
}

.tour-highlight {
	background: white;
	position: relative;
	border-radius: 4px;
	box-shadow: inset 0 0 2px rgba(0, 0, 0, 0.2);
	z-index: 9998;
}

.remove-bottom {
	margin-bottom: 0 !important;
}

.tourButton span {
	font-family: serif;
	font-weight: bold;
	color: black;
}

.tourbus-next span {
	color: green;
}

.tourbus-prev span {
	color: orange;
	margin-left: -3px;
}

.tourbus-stop span {
	color: red;
	margin-left: -3px;
}

.tourText {
	margin: 0 0 20px 0;
}

.tourButton {
	background: #eee linear-gradient(top, rgba(0, 0, 0, 0.1) 0%,
		rgba(0, 0, 0, 0.2) 100%); /* W3C */
	border: 1px solid #aaa;
	border-top: 1px solid #ccc;
	border-left: 1px solid #ccc;
	-moz-border-radius: 3px;
	-webkit-border-radius: 3px;
	border-radius: 3px;
	color: #666;
	display: inline-block;
	font-size: 13px;
	font-weight: normal;
	text-decoration: none;
	text-shadow: 0 1px rgba(255, 255, 255, 0.75);
	cursor: pointer;
	margin-bottom: 20px;
	line-height: normal;
	padding: 4px 12px;
	font-family: "HelveticaNeue", "Helvetica Neue", Helvetica, Arial,
		sans-serif;
}

.tourButton .button:hover, button:hover, input[type="submit"]:hover,
	input[type="reset"]:hover, input[type="button"]:hover {
	color: #222;
	background: #ddd linear-gradient(top, rgba(0, 0, 0, 0.3) 0%,
		rgba(0, 0, 0, 0.4) 100%); /* W3C */
	border: 1px solid #888;
	border-top: 1px solid #aaa;
	border-left: 1px solid #aaa;
}
</style>
<!-- Industry Category Not Setup -->
<ol class='tourbus-legs' id='industryCategoryTour'>
	<li data-orientation='centered' data-highlight="true">
		<h2><spring:message code="rfadetail.data.missing" /></h2>
		<p class="tourText"><spring:message code="rfadetail.look.like.notsetup" /></p>
		<div>
			<a href='javascript:void(0);' class='tourButton remove-bottom tourbus-next' style="float: right"> <spring:message code="application.next" /> <span>»</span>
			</a>
		</div>
	</li>
	<li data-el='#idIndustryCategoryDiv' data-orientation='left' data-align="center" data-width='400' data-highlight="true">
		<h2><spring:message code="rfaevent.industry.category" /></h2>
		<p class="tourText"><spring:message code="rfaevent.not.industry.category" /></p>
		<div>
			<a href='javascript:void(0);' class='tourButton remove-bottom tourbus-stop'> <span>x</span> <spring:message code="eventReport.cls" />
			</a> <a href='javascript:void(0);' class='tourButton remove-bottom tourbus-next' style="float: right"> <spring:message code="eventReport.cls" /> <span>»</span>
			</a>
		</div>
	</li>
	<li data-el='#idIndustryCategoryMenu' data-orientation='bottom' data-width='300' data-highlight="true">
		<p class="tourText"><spring:message code="rfadetail.click.industry.category" /></p>
		<div>
			<a href='javascript:void(0);' class='tourButton remove-bottom tourbus-stop' style="float: right"> <span>x</span> <spring:message code="rfaevent.done" />!
			</a>
		</div>
	</li>
</ol>

<c:if test="${eventType != 'RFA' }">
	<!-- Address Not Setup -->
	<ol class='tourbus-legs' id='addressTour'>
		<li data-orientation='centered' data-highlight="true">
			<h2><spring:message code="rfadetail.data.missing" /></h2>
			<p class="tourText"><spring:message code="rfadetail.look.like.notsetup" /></p>
			<div>
				<a href='javascript:void(0);' class='tourButton remove-bottom tourbus-next' style="float: right"> <spring:message code="application.next" /> <span>»</span>
				</a>
			</div>
		</li>
		<li data-el='#idAddressDiv' data-orientation='left' data-align="center" data-width='400' data-highlight="true">
			<h2><spring:message code="rfadetail.addresses.not.setup" /></h2>
			<p class="tourText"><spring:message code="rfadetail.looks.like.notdefined" /></p>
			<div>
				<a href='javascript:void(0);' class='tourButton remove-bottom tourbus-stop'> <span>x</span> <spring:message code="eventReport.cls" />
				</a> <a href='javascript:void(0);' class='tourButton remove-bottom tourbus-next' style="float: right"> <spring:message code="eventReport.cls" /> <span>»</span>
				</a>
			</div>
		</li>
		<li data-el='#idAddressMenu' data-orientation='bottom' data-width='300' data-highlight="true">
			<p class="tourText"><spring:message code="rfadetail.click.define.addshq" /></p>
			<div>
				<a href='javascript:void(0);' class='tourButton remove-bottom tourbus-stop' style="float: right"> <span>x</span> <spring:message code="rfaevent.done" />!
				</a>
			</div>
		</li>
	</ol>
</c:if>
<script type="text/javascript">
$(document).ready(function() {
	$('body').append('<div class="tour-overlay"></div>');
	var industryCategoryTour = $.tourbus( '#industryCategoryTour', {
		debug: false, 
		autoDepart: false,
		onLegStart: function( leg, bus ) {
			  //alert('Start : ' + leg.index);
			if(leg.index == 1) {
				//alert('End : ' + leg.index);
				$("#sidebar-menu").find("li#idIndustryCategoryMenu").children('a').addClass('sfActive').parents().eq(3).superclick('show');
			}
			if( leg.rawData.highlight ) {
				leg.$target.addClass('tour-highlight');
				$('.tour-overlay').show();
			}
		},
		onLegEnd: function( leg, bus ) {
			if( leg.rawData.highlight ) {
				leg.$target.removeClass('tour-highlight');
				$('.tour-overlay').hide();
			}
		}			  
	});
	<c:if test="${eventType != 'RFA' }">  
	var addressTour = $.tourbus( '#addressTour', {
		debug: false, 
		autoDepart: false,
		onLegStart: function( leg, bus ) {
			if(leg.index == 1) {
				$("#sidebar-menu").find("li#idAddressMenu").children('a').addClass('sfActive').parents().eq(3).superclick('show');
			}
			if( leg.rawData.highlight ) {
				leg.$target.addClass('tour-highlight');
				$('.tour-overlay').show();
			}
		},
		onLegEnd: function( leg, bus ) {
			if( leg.rawData.highlight ) {
				leg.$target.removeClass('tour-highlight');
					$('.tour-overlay').hide();
				}
			}			  
	});
	${ indusCatListFlag ? 'addressTour.depart();' : ( indusCatListFlag ? 'industryCategoryTour.depart()' : '')}
	</c:if>
	
	<c:if test="${eventType == 'RFA' }">  
	  ${ indusCatListFlag ? 'industryCategoryTour.depart()' : ''}
	</c:if>
});
</script>