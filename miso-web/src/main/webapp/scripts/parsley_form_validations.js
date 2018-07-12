/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

var Validate = Validate || {
  // Attach a Parsley instance to the given form
  attachParsley: function(formSelector) {
    if (jQuery(formSelector).length > 0) {
      jQuery(formSelector).parsley();
    }
    window.Parsley.on('parsley:field:validate', function() {
      Validate.updateWarningorSubmit(formSelector);
    });
  },

  // Trim whitespace from input fields
  cleanFields: function(formSelector) {
    jQuery(formSelector).find('input:text').each(function() {
      Utils.validation.clean_input_field(jQuery(this));
    });
  },

  updateWarningOrSubmit: function(formSelector, extraValidationsFunction, submitMethod) {
    jQuery(formSelector).parsley().whenValidate().done(function() {
      jQuery(formSelector + ' .bs-callout-warning').addClass('hidden');
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
    }).fail(function() {
      jQuery(formSelector + ' .bs-callout-warning').removeClass('hidden');
      return false;
    });
  },

  /**
   * Display all errors from a RestError. Errors may apply to a specific field, or be "general" errors which belong to no specific field.
   * The form should include error containers with ID '[fieldName]Error' e.g. 'aliasError' for alias, and one container with class
   * 'generalErrors'
   */
  displayErrors: function(restError, formSelector) {
    Validate.clearErrors(formSelector);
    jQuery(formSelector + ' .bs-callout-warning').removeClass('hidden');

    if (!restError || !restError.data || restError.dataFormat !== 'validation') {
      Validate.displayError(formSelector, 'GENERAL', 'Something has gone terribly wrong. Please report this to your MISO administrator.');
    } else {
      jQuery.each(restError.data, function(key, value) {
        Validate.displayError(formSelector, key, value);
      });
    }
  },

  /**
   * Displays an error in the appropriate container. See Validate.displayErrors above
   */
  displayError: function(formSelector, property, message) {
    var messages = message.split('\n');
    var container = null;
    if (property === 'GENERAL') {
      container = jQuery(formSelector + ' .generalErrors');
    } else {
      container = jQuery(formSelector + ' #' + property + 'Error');
    }
    var list = container.find('.errorList');
    if (!list.length) {
      list = jQuery('<ul class="parsley-errors-list filled">')
      container.append(list);
    }
    jQuery.each(messages, function(i, msg) {
      list.append(jQuery('<li>' + msg + '</li>'));
    });
  },

  clearErrors: function(formSelector) {
    jQuery(formSelector + ' .bs-callout-warning').addClass('hidden');
    jQuery(formSelector).parsley().destroy();
    jQuery(formSelector + ' .generalErrors').empty();
    jQuery(formSelector + ' .errorContainer').empty();
  }

};