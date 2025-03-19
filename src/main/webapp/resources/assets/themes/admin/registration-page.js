// JavaScript Document

// Binding next button on first step
            $(".open1").click(function (event) {
                event.preventDefault();
                $(".tab-pane.active , .form-wizard .active").removeClass("active");
                $("#step-1 , .tb_1").addClass("active");
                $(".tb_1").prevAll().addClass("active");
				$(".tb_1").nextAll().removeClass("active");
				$(".tb_1").nextAll().removeClass("activeprev");
            });

            // Binding next button on second step
            $(".open2").click(function (event) {
                event.preventDefault();
                $(".tab-pane.active , .form-wizard .active").removeClass("active");
                $("#step-2 , .tb_2").addClass("active");
				$(".tb_2").prevAll().addClass("active");
				$(".tb_2").nextAll().removeClass("active");
				$(".tb_2").nextAll().removeClass("activeprev");
            });

            // Binding back button on second step
            $(".open3").click(function (event) {
                event.preventDefault();
                $(".tab-pane.active , .form-wizard .active").removeClass("active");
                $("#step-3 , .tb_3").addClass("active");
				$(".tb_3").prevAll().addClass("active");
				$(".tb_3").nextAll().removeClass("active");
				$(".tb_3").nextAll().removeClass("activeprev");
            });

            // Binding back button on third step
            $(".open4").click(function (event) {
                event.preventDefault();
                $(".tab-pane.active , .form-wizard .active").removeClass("active");
                $("#step-4 , .tb_4").addClass("active");
				$(".tb_4").prevAll().addClass("active");
				$(".tb_4").nextAll().removeClass("active");
				$(".tb_4").nextAll().removeClass("activeprev");
            });

            $(".open5").click(function (event) {
                event.preventDefault();
                $(".tab-pane.active , .form-wizard .active").removeClass("active");
                $("#step-5 , .tb_5").addClass("active");
				$(".tb_5").prevAll().addClass("active");
				$(".tb_5").nextAll().removeClass("active");
				$(".tb_5").nextAll().removeClass("activeprev");
            });
			
			   $(".open6").click(function (event) {
                event.preventDefault();
                $(".tab-pane.active , .form-wizard .active").removeClass("active");
                $("#step-6 , .tb_6").addClass("active");
				$(".tb_6").prevAll().addClass("active");
				$(".tb_6").nextAll().removeClass("active");
				$(".tb_6").nextAll().removeClass("activeprev");
            });
			
			$('a[data-toggle="tab"]').click(function(){
				$(this).parent('li').prevAll().addClass("activeprev");
				$(this).parent('li').nextAll().removeClass("activeprev");
			});
            
           /*===== tooltip ==========*/ 
             $('[data-toggle="tooltip"]').tooltip();
			 
           /*===== multi select tree ==========*/
		   $("#test-select , #test-select2").treeMultiselect({ enableSelectAll: true, sortable: true , collapsible : true}); 
		   
              $('.scroll_box_inner, .last_step_table').perfectScrollbar();