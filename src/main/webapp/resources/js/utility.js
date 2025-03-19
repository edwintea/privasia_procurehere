	function toTitleCase(str)
	{
		return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});	
	}
	
	function ToUpper(ctrl)
	{  
	    var t = ctrl.value;
	    ctrl.value = t.toUpperCase();
    }

	function isSuccess(xhr, status, args) {
		if(args.validationFailed) {
			return false;
		} else if(args.error){
			return false;
		} else {
			if(statusDialog)
				statusDialog.hide();
			return true;
		}
	}

    function isNumberKey(evt)
    {
       var charCode = (evt.which) ? evt.which : event.keyCode;
       if (charCode > 31 && (charCode < 48 || charCode > 57))
          return false;

       return true;
    }

    function isDecimalNumberKey(evt)
    {
       var charCode = (evt.which) ? evt.which : event.keyCode;
       if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 190 && charCode != 110 && charCode != 46)
          return false;

       return true;
    }
	
	function checkSelected() {
		jQuery("td[id^='ygtvcontent']").each(function(index) {
			// |----- Check Box -----|
			if(jQuery(this).find('span:first').attr('class') == 'selected' && jQuery('#ygtvtable' + jQuery(this).attr('id').substr(11)).attr('class').indexOf('ygtv-highlight0') != -1)
			{
				jQuery(this).click();
			}
		});
	}
	
    function isSignedDecimalNumberKey(evt)
    {
       var charCode = (evt.which) ? evt.which : event.keyCode;
       if (charCode > 31 && (charCode < 48 || charCode > 57) && (charCode != 190 && charCode != 110 && charCode != 46) && (charCode != 45 && charCode != 189 && charCode != 109))
          return false;

       return true;
    }
