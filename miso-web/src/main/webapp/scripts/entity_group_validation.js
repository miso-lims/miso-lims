/**
 * User: davey
 * Date: 24/06/14
 * Time: 15:33
 */
function validate_entity_group(form) {
  var ok = true;
  var error = "Please correct the following error(s):\n\n";

  if (jQuery("select[name=assignee]").val() == 0) {
    ok = false;
    error += "You have not chosen an assignee for this entity group.\n";
  }

  if (jQuery('#groupElementList>.list-group-item').length == 0) {
    ok = false;
    error += "You have selected no entities for this entity group.\n";
  }

  if (!ok) {
    alert(error);
  }
  else {
    form.submit();
  }

  return ok;
}