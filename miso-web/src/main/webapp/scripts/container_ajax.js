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

var Container = Container
    || {
      validateContainer: function() {
        Validate.cleanFields('#container-form');
        jQuery('#container-form').parsley().destroy();

        // ID Barcode validation
        jQuery('#identificationBarcode').attr('class', 'form-control');
        jQuery('#identificationBarcode').attr('data-parsley-required', 'true');
        jQuery('#identificationBarcode').attr('data-parsley-maxlength', '100');
        jQuery('#identificationBarcode').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

        // Description input field validation
        jQuery('#description').attr('class', 'form-control');
        jQuery('#description').attr('data-parsley-maxlength', '255');
        jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

        // Received Date validation
        jQuery('#receivedDate').attr('class', 'form-control');
        jQuery('#receivedDate').attr('data-parsley-required', 'true');
        jQuery('#receivedDate').attr('data-date-format', 'YYYY-MM-DD');
        jQuery('#receivedDate').attr('data-parsley-pattern', Utils.validation.dateRegex);
        jQuery('#receivedDate').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');

        // Returned Date validation
        jQuery('#returnedDate').attr('class', 'form-control');
        jQuery('#returnedDate').attr('data-date-format', 'YYYY-MM-DD');
        jQuery('#returnedDate').attr('data-parsley-pattern', Utils.validation.dateRegex);
        jQuery('#returnedDate').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');

        jQuery('#container-form').parsley();
        jQuery('#container-form').parsley().validate();

        // Ensure provided serial number is unique.
        var serialNumberField = jQuery('#identificationBarcode');
        var serialNumber = serialNumberField.val();
        var containerIdField = jQuery("#containerId");
        var containerId = null;
        if (containerIdField.length > 0) {
          containerId = parseInt(containerIdField.text());
          if (isNaN(containerId)) {
            containerId = null;
          }
        }

        Fluxion.doAjax('containerControllerHelperService', 'isSerialNumberUnique', {
          'serialNumber': serialNumber,
          'containerId': containerId,
          'url': ajaxurl
        }, {
          'doOnSuccess': function(json) {
            if (json.isSerialNumberUnique === "true") {
              Validate.updateWarningOrSubmit('#container-form', Container.validateStudyAdded);
            } else {
              // Serial number is not unique.
              window.ParsleyUI.addError(serialNumberField.parsley(), "serialNumberError",
                  'This serial number is already in use. Please choose another.');
              return false;
            }
          },
          'doOnError': function(json) {
            // Unable to perform lookup.
            Utils.showOkDialog('Check Serial Number', [json.error]);
            window.ParsleyUI.addError(serialNumberField.parsley(), "serialNumberError", 'Unable to determine if serial number is unique.');
            return false;
          }
        });
      },
    };
