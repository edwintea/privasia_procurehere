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
</script>
 <div class="freeTrial">
     <img src="<c:url value="/resources/images/public/inverse-tick.png"/>" alt="success-icon" class="text-center">
     <h1>Great News!</h1>
     <p>A confirmation email is on its way. Please check your inbox and click on the link to verify your account.
     <br>
     <br>
     (Don't forget to check your spam in case it gets caught up on the way)
     <!-- You're all signed up and ready to begin your free Procurehere trial. --></p>
 </div>
    
   <%--  <div class="container">
      <div class="row">
	        <div class="col-sm-6 col-md-6 col-lg-6 col-xs-12 guide-section">
	          <p>You should receive  an email shortly with all the details<br> required to get started. You can also find handy guides to<br> help you along the way through our online helpdesk.</p>
	          <button class="btn btn-default" onclick="location.href='https://procurehere.zendesk.com/hc/en-us'" >Click Here</button>
	          <p>Simply log on, set up your account, and begin your journey.</p>
	        </div>
	        
	        <div class="col-sm-6 col-md-6 col-lg-6 col-xs-12">
	          <img src="<c:url value="/resources/images/public/img-thanku.png"/>" alt="thank you img" class="img-responsive guide-img">
	        </div>
	        
      </di --%>v>
    </div>

<div class="container-fluid blog-slider blog-nomargin">
        <p>In the meantime, why not get up to date with all the latest procurement<br>news and insight on Procurehere blog? </p>
        
  <div class="owl-carousel owl-theme container">
  </div>
</div>

<div>
	<a href="#"><img src="<c:url value="/resources/images/public/chat-icon.png"/>" alt="chat-icon" class="chat-icon"></a>
</div>



<style>
.owl-carousel {
   display: block !important;
}
</style>
