<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" isELIgnored="false"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
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
<div class="container">
	<div class="logout-text">
		<img src="<c:url value="/resources/images/public/blue tick.png"/>" alt="success-icon" class="img-reponsive text-center">
		<p>
			You have securely logged out<br>from PROCUREHERE
		</p>
	</div>
</div>
<div class="container-fluid blog-slider">
	<p>In the meantime, why not get up to date with all the latest procurement news and insight on PROCUREHERE blog?</p>
	<div class="owl-carousel owl-theme container">
		<!-- div class="item">
      <div class="blog-content">
      <img src="<c:url value="/resources/images/public/blog-2.png"/>" alt="blog-1">
      <p class="font-22">ERP Integration with<br>Procurehere</p>
       <span>Lorem ipsum dolor sit amet,<br> consectetur adipiscing elit, sed<br> do eiusmod tempor<br> incididunt ut</span>
       </div>
       <a href="#" class="blog-read-more">Read More &nbsp;></a>
      </div>
    <div class="item">
      <div class="blog-content">
      <img src="<c:url value="/resources/images/public/blog-1.png"/>" alt="blog-1">
       <p class="font-22">How to Embed a Culture<br>of Innovation</p>
       <span>Lorem ipsum dolor sit amet,<br> consectetur adipiscing elit, sed<br> do eiusmod tempor<br> incididunt ut</span>
       </div>
       <a href="#" class="blog-read-more">Read More &nbsp;></a>
     </div>
    <div class="item">
      <div class="blog-content">
      <img src="<c:url value="/resources/images/public/blog-3.png"/>" alt="blog-1">
       <p class="font-22">Saving Costs with<br>Procurehere</p>
       <span>Lorem ipsum dolor sit amet,<br> consectetur adipiscing elit, sed<br> do eiusmod tempor<br> incididunt ut</span>
     </div>
       <a href="#" class="blog-read-more">Read More &nbsp;></a>
   </div>
    <div class="item">
      <div class="blog-content">
      <img src="<c:url value="/resources/images/public/blog-2.png"/>" alt="blog-1">
      <p class="font-22">ERP Integration with<br>Procurehere</p>
       <span>Lorem ipsum dolor sit amet,<br> consectetur adipiscing elit, sed<br> do eiusmod tempor<br> incididunt ut</span>
      </div>
       <a href="#" class="blog-read-more">Read More &nbsp;></a>
      </div>
    <div class="item">
      <div class="blog-content">
      <img src="<c:url value="/resources/images/public/blog-1.png"/>" alt="blog-1">
       <p class="font-22">How to Embed a Culture<br>of Innovation</p>
       <span>Lorem ipsum dolor sit amet,<br> consectetur adipiscing elit, sed<br> do eiusmod tempor<br> incididunt ut</span>
     </div>
       <a href="#" class="blog-read-more">Read More &nbsp;></a>
   </div>
    <div class="item">
      <div class="blog-content">
      <img src="<c:url value="/resources/images/public/blog-3.png"/>" alt="blog-1">
       <p class="font-22">Saving Costs with<br>Procurehere</p>
       <span>Lorem ipsum dolor sit amet,<br> consectetur adipiscing elit, sed<br> do eiusmod tempor<br> incididunt ut</span>
      </div>
        <a href="#" class="blog-read-more">Read More &nbsp;></a>
   </div -->
	</div>
</div>
<div>
	<a href="#">
		<img src="<c:url value="/resources/images/public/chat-icon.png"/>" alt="chat-icon" class="chat-icon">
	</a>
</div>
<style>
.owl-carousel {
	display: block !important;
}
</style>
<script type="text/javascript">
	
</script>
