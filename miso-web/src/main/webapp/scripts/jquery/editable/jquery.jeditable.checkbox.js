/**
 * Created by IntelliJ IDEA.
 * User: davey
 * Date: 31-Jan-2011
 * Time: 11:17:21
 */
// Create a custom input type for checkboxes
jQuery.editable.addInputType("checkbox", {
	element : function(settings, original) {
		var input = jQuery('<input type="checkbox">');
		jQuery(this).append(input);

		// Update <input>'s value when clicked
		jQuery(input).click(function() {
			var value = jQuery(input).attr("checked") ? 'true' : 'false';
			jQuery(input).val(value);
		});
		return(input);
	},
	content : function(string, settings, original) {
		var checked = string == "true" ? 1 : 0;
		var input = jQuery(':input:first', this);
		jQuery(input).attr("checked", checked);
		var value = jQuery(input).attr("checked") ? 'true' : 'false';
		jQuery(input).val(value);
	}
});

