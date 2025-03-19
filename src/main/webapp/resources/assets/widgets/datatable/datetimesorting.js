/* Auther : Pawan Goyal */
jQuery.extend( jQuery.fn.dataTableExt.oSort, {
    "custom-date-pre": function ( a ) {
        var x;

        if ( $.trim(a) !== '' ) {
            var frDatea = $.trim(a).split(' ');
            var frTimea = (undefined != frDatea[1]) ? frDatea[1].split(':') : [00,00,00];
            var frDatea2 = frDatea[0].split('/');
			var frDatea2 = frDatea[0].split('/');
			var dateString = frDatea2[2]+'-'+frDatea2[1]+'-'+frDatea2[0]+' '+frTimea[0]+':'+frTimea[1]+':00 GMT';
			x = Date.parse(dateString) / 1000;
			var ampm = (undefined != frDatea[2]) ? frDatea[2] : 'AM';
			if(ampm.toUpperCase() == 'PM'){
				x = x + (12*60*60);
			}
        }
        else {
            x = Infinity;
        }

        return x;
    },

    "custom-date-asc": function ( a, b ) {
        return a - b;
    },

    "custom-date-desc": function ( a, b ) {
        return b - a;
    }
} );