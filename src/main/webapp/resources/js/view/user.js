jQuery(document).ready(function($) {
	// RFX Template
	$('.checkAllCheckbox').change(function(){
		$('.subCheckBoxes').prop('checked', $(this).prop('checked'));
		$.uniform.update();
	});
	$('.subCheckBoxes').change(function(){
		if($('.subCheckBoxes').length == $('.subCheckBoxes:checked').length){
			$('.checkAllCheckbox').prop('checked', true);
		} else {
			$('.checkAllCheckbox').prop('checked', false);
		}
		$.uniform.update();
	});
	
	// PR Checkboxes
	$('.prCheckAllCheckbox').change(function(){
		$('.prSubCheckBoxes').prop('checked', $(this).prop('checked'));
		$.uniform.update();
	});
	$('.prSubCheckBoxes').change(function(){
		if($('.prSubCheckBoxes').length == $('.prSubCheckBoxes:checked').length){
			$('.prCheckAllCheckbox').prop('checked', true);
		} else {
			$('.prCheckAllCheckbox').prop('checked', false);
		}
		$.uniform.update();
	});
	
	// Sourcing Template Checkboxes
	$('.srCheckAllCheckbox').change(function(){
		$('.srSubCheckBoxes').prop('checked', $(this).prop('checked'));
		$.uniform.update();
	});
	$('.srSubCheckBoxes').change(function(){
		if($('.srSubCheckBoxes').length == $('.srSubCheckBoxes:checked').length){
			$('.srCheckAllCheckbox').prop('checked', true);
		} else {
			$('.srCheckAllCheckbox').prop('checked', false);
		}
		$.uniform.update();
	});
	
		// Sourcing Template Checkboxes
	$('.sptCheckAllCheckbox').change(function(){
		$('.sptSubCheckBoxes').prop('checked', $(this).prop('checked'));
		$.uniform.update();
	});
	$('.sptSubCheckBoxes').change(function(){
		if($('.sptSubCheckBoxes').length == $('.sptSubCheckBoxes:checked').length){
			$('.sptCheckAllCheckbox').prop('checked', true);
		} else {
			$('.sptCheckAllCheckbox').prop('checked', false);
		}
		$.uniform.update();
	});

	
	// request Checkboxes
	$('.requestCheckAllCheckbox').change(function(){
		$('.requestSubCheckBoxes').prop('checked', $(this).prop('checked'));
		$.uniform.update();
	});
	$('.requestSubCheckBoxes').change(function(){
		if($('.requestSubCheckBoxes').length == $('.requestSubCheckBoxes:checked').length){
			$('.requestCheckAllCheckbox').prop('checked', true);
		} else {
			$('.requestCheckAllCheckbox').prop('checked', false);
		}
		$.uniform.update();
	});

        // Business Unit Checkboxes
        $('.buCheckAllCheckbox').change(function(){
            console.log(" buCheckAllCheckbox");
            var isChecked = $(this).prop('checked');

            // Set the state of all sub-checkboxes
            $('.buSubCheckBoxes').prop('checked', isChecked);
            $.uniform.update();

            // Trigger validation to update the validation message
            var isValidB = validateBusinessUnitSelection();

            // Re-enable the save button if valid
                       if (isValidB) {
                           console.log("can create/update?");
                           $('#saveUser').removeClass('disabled');
                       } else {
                           console.log("cannot create/update?");
                           $('#saveUser').addClass('disabled');
                       }
        });

        $('.buSubCheckBoxes').change(function() {
            console.log(" buSubCheckBoxes");
            var isValidB = validateBusinessUnitSelection();

            // Check if all checkboxes are selected
            if ($('.buSubCheckBoxes').length == $('.buSubCheckBoxes:checked').length) {
                $('.buCheckAllCheckbox').prop('checked', true);
            } else {
                $('.buCheckAllCheckbox').prop('checked', false);
            }
            $.uniform.update();

            // Re-enable the save button if valid
            if (isValidB) {
                console.log("can create/update?");
                $('#saveUser').removeClass('disabled');
            } else {
                console.log("cannot create/update?");
                $('#saveUser').addClass('disabled');
            }
        });


        $('#saveUser').click(function(event) {
			if ($('#saveUser').hasClass('disabled')) {
				return; // Prevent form submission if the button is disabled
			}

            // Perform the validation
            var isBusinessUnitValid = validateBusinessUnitSelection();
            var isFormValid = $('#userRegistration').isValid(); // Assuming this is a function that checks form validity

            if (isFormValid && isBusinessUnitValid) {
                // Allow form submission
                $('#userRegistration').submit(); // Submit the form
            } else {
                // Prevent form submission
                event.preventDefault(); // Prevent default form submission behavior
            }
        });

	$("#userType").change(function(e){
		e.preventDefault();
		
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");

		var userType = $(this).val();
		
		$.ajax({
			type : "POST",
			url : "getUserRole",
			data : {
				'userType' :userType
			},
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data) {
				console.log('data: ' + data);
				var userRoleTable = '<option value="">Select User Role</option>';
				var firstRoleId = '';
				$.each(data, function(i, item) {
					if(i == 0){
						firstRoleId = item.id ;
						$(".form-error").remove();
						$("#idUserRole").parent().removeClass("has-error");
						$("#idUserRole").removeClass("error");
						$('#idUserRole').css("border-color", "");
					}else{
						firstRoleId = '';
					}
					userRoleTable += '<option value="'+item.id+'">' + item.roleName+ '</option>'; '</li>';
				});
				$('#idUserRole').html(userRoleTable).trigger("chosen:updated");
				$('#idUserRole').val(firstRoleId).trigger("chosen:updated");
			},
			error : function(request, textStatus, errorThrown) {
				alert('Error: ' + request.getResponseHeader('error'));
			}
		});
		
		});
	
	
	
});

function validateBusinessUnitSelection() {
    var checkboxes = document.querySelectorAll('.buSubCheckBoxes');
    var atLeastOneChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);

    var container = document.getElementById('businessUnitContainer');
    var errorMsg = document.getElementById('businessUnitErrorMsg');

    if (!atLeastOneChecked) {
        console.log("none selected");
        container.style.border = '2px solid red';
        errorMsg.style.display = 'block';

        // Scroll to the business unit section
        container.scrollIntoView({ behavior: 'smooth' });
        return false;
    } else {
        console.log("1 selected");
        container.style.border = 'none';
        errorMsg.style.display = 'none';
        return true;
    }
}