<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<script type="text/javascript">
$.get("<spring:message code="app.root.url" />/feed", function(data) {
	var $XML = $(data);
	$XML.find("item").each(function() {
		var $this = $(this), item = {
			title : $this.find("title").text(),
			link : $this.find("link").text(),
			description : $this.find("description").text(),
			pubDate : $this.find("pubDate").text(),
			imageUrl : $this.find("image").text(),
		};
		//alert(item.description);
		var str = '<div class="item"><div class="blog-content">';
		str += '<img src="' + item.imageUrl + '" alt="' + item.title + '">';
		str += '<p class="font-22">' + item.title + '</p>';
		str += '<span>' + item.description;
		+'</span>';
		str += '</div><a href="' + item.link + '" class="blog-read-more">Read More &nbsp;></a></div>';

		$('<img/>')[0].src = item.imageUrl;

		$('.owl-carousel').append(str);
		//etc...
	});

	$('.owl-carousel').owlCarousel({
		loop : true,
		margin : 10,
		nav : true,
		autoplay : 1000,
		responsive : {
			0 : {
				items : 1
			},
			1085 : {
				items : 2
			},
			1650 : {
				items : 3
			}
		}
	});

});
function doActivateChat() {
zE(function() { zE.activate(); });
}
</script>
 <div class="container margin-bottom-5">
  <div class="purchase-outline border-btm-centered">
    <img src="<c:url value="/resources/images/public/blue tick.png"/>" alt="success-icon" class="img-reponsive text-center">
    <h2>Thanks you for your purchase</h2>
    <p>You will soon receive an email confirming your payment and subscription details.</p>
    </div>
</div>
<div class="container login-options nopad-margin">
  <div class="row purchase-support">
    <div class="col-sm-1 col-md-1 col-lg-1 col-xs-0 redirect-here-section">
    </div>
    <div class="col-sm-3 col-md-3 col-lg-3 col-xs-12 redirect-here-section">
      <img src="<c:url value="/resources/images/public/information.png"/>" alt="user-icon" class="img-responsive">
      <p>You can find full details and <br>support information at our<br> online help desk, including the<br> first steps on how to setup<br>your account.</p>
      <button class="btn btn-default margin-top-8" onclick="location.href='https://procurehere.zendesk.com/hc/en-us'" >Click Here</button>
    </div>
    <div class="col-sm-3 col-md-3 col-lg-3 col-xs-12 redirect-here-section">
      <img src="<c:url value="/resources/images/public/helpdesk.png"/>" alt="supplier-sign" class="img-responsive">
      <p>If you have any further questions<br>or need support with your setup,<br> we offer 24/7 helpdesk support<br>through our helpline on<br> <a href="#" class="phone-number"> 1-800-88-77-48</a> and live chat on<br>the Procurehere homepage.</p>
       <button class="btn btn-default" onclick="doActivateChat();">Click Here</button>
    </div>
    <div class="col-sm-3 col-md-3 col-lg-3 col-xs-12 redirect-here-section">
      <img src="<c:url value="/resources/images/public/blog.png"/>" alt="supplier-sign" class="img-responsive">
      <p>Finally, if you want to explore a<br> world of procurement insight,<br>why not take look at the<br>Procurehere blog for all the<br>latest procurement news?</p>
       <button onclick="location.href='<spring:message code="app.root.url" />/blog'" class="btn btn-default margin-top-8">Click Here</button>
    </div>
  </div>
</div>
 <div class="freeTrial pad-mrg-t-b" style="margin-bottom: 1% !important;margin-top: 1% !important;">
     <h1>Welcome to Procurehere</h1>
 </div>
<div><a href="#"><img src="<c:url value="/resources/images/public/chat-icon.png"/>" alt="chat-icon" class="chat-icon"></a></div>



<style>
.owl-carousel {
   display: block !important;
}
</style>
