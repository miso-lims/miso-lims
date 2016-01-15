function validate_box(form) {
  // no alias regex validation, just need to check that it's present. 
  // Size will be present by default
  if (jQuery('#alias').val() == '') {
    alert("Please specify a box alias."); 
  } else {
    form.submit();
  }
}

function process_validate_box(form) {
  var ok = true;
  var error = "Please correct the following error(s):\n\n";

  if (jQuery(':text.validateable').length > 0) {
    jQuery(':text.validateable').each(function() {
      var result = Utils.validation.validate_input_field(this, 'Box', ok);
      ok = result.okstatus;
      error += result.errormsg;
    })
  }

  if (!ok) {
    alert(error);
  } else {
    form.submit();
  }

  return ok;
}
