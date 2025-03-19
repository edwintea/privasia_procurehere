var refreshIssued = false;
$(window).on('load', function() {

	function strfobj(str) {
      var parsed = str.split(':'),
        obj = {};
      labels.forEach(function(label, i) {
        obj[label] = parsed[i]
      });	  
      return obj;
    }
    function diff(obj1, obj2) {
      var diff = [];
      labels.forEach(function(key) {
        if (obj1[key] !== obj2[key]) {
          diff.push(key);
        }
      });
      return diff;
    }
	var htmlData = '<div class="time <%= label %>"><span class="count curr top"><%= curr %></span><span class="count next top"><%= next %></span><span class="count next bottom"><%= next %></span><span class="count curr bottom"><%= curr %></span><span class="label"><%= label %></span></div>';
    var labels = ['DAYS', 'HOURS', 'MINUTES', 'SECONDS'],
      nextYear = $('#main-example1').attr('data-date'),
     //nextYear = moment.tz("2017-01-01 00:00", "America/Sao_Paulo");
      //nextYear = moment.tz(nextYear, "America/Sao_Paulo"),.toDate();
      template = _.template(htmlData),
      currDate = '00:00:00:00:00',
      nextDate = '00:00:00:00:00',
      parser = /([0-9]{2})/gi,
      $example = $('#main-example1');
      var dataUrl = $example.attr('data-url');
//      console.log("dataUrl : " + dataUrl);
      var reloadUrl = $example.attr('data-reload-url');
      var initData = strfobj(currDate);
      var checkCounterInitial = 0;
      labels.forEach(function(label, i) {
	      $example.append(template({
	        curr: initData[label],
	        next: initData[label],
	        label: label
	      }));
      });
      
    var difference = 0;
    if(nextYear != '' && nextYear != null && nextYear != undefined){
	    nextYear = moment.tz(nextYear, timeZoneLocation);
	    var eventId = $('#eventId').val();
	    if(dataUrl != '' && dataUrl != undefined){
//		    var remainingTimeUrl = dataUrl;
		    difference = remainingTimeUrl(dataUrl, difference);
//		    console.log("difference : " + difference);
		    setInterval(function(){ 
//		    	console.log("Data Url : " + dataUrl + " difference : " + difference);
	    		difference = remainingTimeUrl(dataUrl, difference);
//	    		console.log("After remaining : " + difference);
	    	}, 2000);
	    }
//	    console.log("nextYear : " + nextYear);
	    $example.countdown(nextYear.toDate(), {elapse: true})
	    .on('update.countdown', function(event) {
	    	// console.log("nextYear.toDate() : " + nextYear.toDate());
	    	if(dataUrl != '' && dataUrl != undefined){
		    	var seconds = Math.floor(difference / 1000);
				var minutes = Math.floor(seconds / 60);
				var hours = Math.floor(minutes / 60);
				var days = Math.floor(hours / 24);
				hours %= 24;
				minutes %= 60;
				seconds %= 60;
				if(difference > 0){
					difference = difference - 1000;
				}
				if(days < 10){
					days = '0'+days;
				}
				if(hours < 10){
					hours = '0'+hours;
				}
				if(minutes < 10){
					minutes = '0'+minutes;
				}
				if(seconds < 10){
					seconds = '0'+seconds;
				}
				var newDate = days+':'+hours+':'+minutes+':'+seconds,
				    data;
	    	} else {
	    		var newDate = event.strftime('%D:%H:%M:%S'),
	    			data;
	    	}
	        //console.log("newDate : " + newDate);
	        //console.log("checkCounterInitial : " + checkCounterInitial);
	      if(((newDate == '0-1:0-1:0-1:0-1') ||(newDate == '00:00:00:00')) && checkCounterInitial > 1){
		      console.log("reload url : ", reloadUrl);
		      if(browserOnline && !refreshIssued) {
		    	  window.location.href=reloadUrl;
		    	  refreshIssued = true;
		      }
	      } else {
		      checkCounterInitial = checkCounterInitial + 1;
		      if (newDate !== nextDate) {
		        currDate = nextDate;
		        nextDate = newDate;
		        data = {
		          'curr': strfobj(currDate),
		          'next': strfobj(nextDate)
		        };
		  //      console.log("currDate :"+ currDate + " nextDate :" +nextDate);
		        diff(data.curr, data.next).forEach(function(label) {
		          var selector = '.%s'.replace(/%s/, label),
		              $node = $example.find(selector);
		          $node.removeClass('flip');
		          $node.find('.curr').text(data.curr[label]);
		          $node.find('.next').text(data.next[label]);
		          _.delay(function($node) {
		            $node.addClass('flip');
		          }, 50, $node);
		        });
		      }
	      }
	    });
    }
  });

function remainingTimeUrl(dataUrl, difference){
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
//	console.log("dataUrl >>>> " + dataUrl);
	$.ajax({
		type : "GET",
		url : dataUrl,
		dataType : "json",
		async : false,
		beforeSend : function(xhr) {
			xhr.setRequestHeader(header, token);
		},
		success : function(data) {
			difference = data.remainingTime;
		},
		error : function(request, textStatus, errorThrown) {
			difference = 0;
		}
	});
	return difference;
}
$(document).ready(function(){
	 var datetime = $('#datetime'),
     date = moment(new Date()),
     update = function(){
         datetime.html(date.format('dddd, MMMM Do YYYY, h:mm:ss a'));
     };
	 update();
	 setInterval(update, 1000);
});