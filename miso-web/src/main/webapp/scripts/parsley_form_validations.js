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
  attachParsley: function (formSelector) {
    if (jQuery(formSelector).length > 0) {
      jQuery(formSelector).parsley();
    }
    window.Parsley.on('parsley:field:validate', function () {
      Validate.updateWarningorSubmit(formSelector);
    });
  },
  
  // Trim whitespace from input fields
  cleanFields: function (formSelector) {
    jQuery(formSelector).find('input:text').each(function() {
      Utils.validation.clean_input_field(jQuery(this));
    });
  },
  
  updateWarningOrSubmit: function(formSelector, extraValidationsFunction) {
    jQuery(formSelector).parsley().whenValidate()
      .done(function() {
        jQuery('.bs-callout-warning').addClass('hidden');
        // submit if form is valid
        if (extraValidationsFunction) { 
          extraValidationsFunction(jQuery(formSelector));
        } else {
          jQuery(formSelector).submit();
        }
      })
      .fail(function() {
        jQuery('.bs-callout-warning').removeClass('hidden');
        return false;
      });
  }
  
};