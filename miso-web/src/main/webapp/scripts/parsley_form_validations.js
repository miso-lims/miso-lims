var Validate = Validate || {
  // Attach a Parsley instance to the given form
  attachParsley: function (formSelector) {
    if (jQuery(formSelector).length > 0) {
      jQuery(formSelector).parsley();
    }
    window.Parsley.on("parsley:field:validate", function () {
      Validate.updateWarningorSubmit(formSelector);
    });
  },

  // Trim whitespace from input fields
  cleanFields: function (formSelector) {
    jQuery(formSelector)
      .find("input:text")
      .each(function () {
        Utils.validation.clean_input_field(jQuery(this));
      });
  },

  updateWarningOrSubmit: function (
    formSelector,
    extraValidationsFunction,
    submitMethod,
    invalidCallback
  ) {
    jQuery(formSelector)
      .parsley()
      .whenValidate()
      .done(function () {
        jQuery(formSelector + " .bs-callout-warning").addClass("hidden");
        // submit if form is valid
        if (extraValidationsFunction) {
          extraValidationsFunction(jQuery(formSelector));
        } else {
          if (submitMethod) {
            submitMethod();
          } else {
            jQuery(formSelector).submit();
          }
        }
      })
      .fail(function () {
        jQuery(formSelector + " .bs-callout-warning").removeClass("hidden");
        if (invalidCallback) {
          invalidCallback();
        }
        return false;
      });
  },

  /**
   * Display all errors from a RestError. Errors may apply to a specific field, or be "general" errors which belong to no specific field.
   * The form should include error containers with ID '<formId>_<fieldName>Error' e.g. 'sampleForm_aliasError', and one container with
   * class 'generalErrors'
   */
  displayErrors: function (restError, formId) {
    var formSelector = "#" + formId;
    Validate.clearErrors(formSelector);
    jQuery(formSelector + " .bs-callout-warning").removeClass("hidden");

    if (!restError || !restError.data) {
      Validate.displayError(
        formId,
        "GENERAL",
        "Something has gone terribly wrong. Please report this to your MISO administrator."
      );
    } else if (restError.dataFormat === "validation") {
      jQuery.each(restError.data, function (key, value) {
        Validate.displayError(formId, key, value);
      });
    } else {
      Validate.displayError(formId, "GENERAL", restError.detail);
      if (restError.data && restError.data.uiHelp) {
        Validate.displayError(formId, "GENERAL", restError.data.uiHelp);
      }
    }
  },

  /**
   * Displays an error in the appropriate container. See Validate.displayErrors above
   */
  displayError: function (formId, property, message) {
    var messages = message.split("\n");
    var container = null;
    if (property !== "GENERAL") {
      var fieldErrors = jQuery("#" + formId + "_" + property + "Error");
      if (fieldErrors.length) {
        container = fieldErrors;
      } else {
        messages = messages.map(function (x) {
          return property + ": " + x;
        });
      }
    }
    if (!container) {
      container = jQuery("#" + formId + " .generalErrors");
    }
    var list = container.find(".errorList");
    if (!list.length) {
      list = jQuery('<ul class="parsley-errors-list filled">');
      container.append(list);
    }
    jQuery.each(messages, function (i, msg) {
      list.append(jQuery("<li>" + msg + "</li>"));
    });
  },

  clearErrors: function (formSelector) {
    jQuery(formSelector + " .bs-callout-warning").addClass("hidden");
    jQuery(formSelector).parsley().destroy();
    jQuery(formSelector + " .generalErrors").empty();
    jQuery(formSelector + " .errorContainer").empty();
    // for error containers that are outside of the form such as for lists
    jQuery('[id^="' + formSelector.substring(1) + '_"][id$="Error"]').empty();
  },

  makeDecimalField: function (selector, precision, scale, required, allowNegative) {
    jQuery(selector).attr("class", "form-control");
    jQuery(selector).attr("data-parsley-type", "number");
    jQuery(selector).attr("data-parsley-maxlength", precision + 1);
    var max = Math.pow(10, precision - scale) - Math.pow(0.1, scale);
    var min = allowNegative ? max * -1 : 0;
    jQuery(selector).attr("data-parsley-range", "[" + min + ", " + max + "]");
    var pattern = "\\d{0," + (precision - scale) + "}(?:\\.\\d{1," + scale + "})?";
    jQuery(selector).attr("data-parsley-pattern", pattern);
    jQuery(selector).attr("data-parsley-required", required ? "true" : "false");
    jQuery(selector).attr(
      "data-parsley-error-message",
      "Must be a number between " + min + " and " + max
    );
  },

  removeValidation: function (selector) {
    jQuery(selector).removeAttr("data-parsley-type");
    jQuery(selector).removeAttr("data-parsley-maxlength");
    jQuery(selector).removeAttr("data-parsley-range");
    jQuery(selector).removeAttr("data-parsley-pattern");
    jQuery(selector).removeAttr("data-parsley-required");
    jQuery(selector).removeAttr("data-parsley-error-message");
    jQuery(selector).removeAttr("data-parsley-min");
    jQuery(selector).removeAttr("data-parsley-max");
    jQuery(selector).removeAttr("data-date-format");
  },
};
