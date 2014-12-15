/**
 * Created by IntelliJ IDEA.
 * User: davey
 * Date: 03-Dec-2012
 * Time: 11:17:21
 */
// Create a custom input type for radiobuttons
jQuery.editable.addInputType('qcradio', {
	element : function(settings, original) {
      var f = "<label for='qcPassedU'>Unknown</label><input type='radio' name='qcPassed' id='qcPassedU' checked='checked' class='text ui-widget-content ui-corner-all' value=''/><br/>"+
              "<label for='qcPassedT'>True</label><input type='radio' name='qcPassed' id='qcPassedT' class='text ui-widget-content ui-corner-all' value='true'/><br/>"+
              "<label for='qcPassedF'>False</label><input type='radio' name='qcPassed' id='qcPassedF' class='text ui-widget-content ui-corner-all' value='false'/>";
      var input = jQuery(f);
      jQuery(this).append(input);

      // Update <input>'s value when clicked
      jQuery(input).click(function() {
          var value = jQuery(this).val();
          jQuery(input).val(value);
      });
      return(input);
	},
	content : function(string, settings, original) {
		var input = jQuery(':input[value="'+string+'"]', this);
        jQuery(input).attr("checked", "checked");
        var value = jQuery('input:radio[name=qcPassed]:checked').val();
		jQuery(input).val(value);
	}
});

