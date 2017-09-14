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

var Experiment = Experiment || {
  validateExperiment: function() {
    Validate.cleanFields('#experiment-form');
    jQuery('#experiment-form').parsley().destroy();

    // Title input field validation
    jQuery('#title').attr('class', 'form-control');
    jQuery('#title').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    jQuery('#title').attr('data-parsley-required', 'true');
    jQuery('#title').attr('data-parsley-maxlength', '255');

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    jQuery('#alias').attr('data-parsley-required', 'true');
    jQuery('#alias').attr('data-parsley-maxlength', '100');

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    jQuery('#description').attr('data-parsley-maxlength', '255');

    jQuery('#experiment-form').parsley();
    jQuery('#experiment-form').parsley().validate();

    Validate.updateWarningOrSubmit('#experiment-form');
    return false;
  }
};
