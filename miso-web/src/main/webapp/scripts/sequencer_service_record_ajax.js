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

var ServiceRecord = ServiceRecord || {
  
  validateServiceRecord: function() {
    Validate.cleanFields('#serviceRecordForm');
    
    jQuery('#serviceRecordForm').parsley().destroy();
    
    jQuery('#title').attr('required', 'true');
    jQuery('#title').attr('data-parsley-maxlength', '50');
    
    jQuery('#details').attr('data-parsley-maxlength', '65535');
    
    jQuery('#servicedByName').attr('required', 'true');
    jQuery('#servicedByName').attr('data-parsley-maxlength', '30');
    
    jQuery('#referenceNumber').attr('data-parsley-maxlength', '30');
    
    jQuery('#serviceDatePicker').attr('required', 'true');
    jQuery('#serviceDatePicker').attr('data-date-format', 'YYYY-MM-DD');
    jQuery('#serviceDatePicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
    jQuery('#serviceDatePicker').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');
    
    jQuery('#shutdownTime').attr('data-date-format', 'YYYY-MM-DD hh:mm:ss');
    jQuery('#shutdownTime').attr('data-parsley-pattern', Utils.validation.dateTimeRegex);
    jQuery('#shutdownTime').attr('data-parsley-error-message', 'Time must be of form YYYY-MM-DD hh:mm:ss');
    
    jQuery('#restoredTime').attr('data-date-format', 'YYYY-MM-DD hh:mm:ss');
    jQuery('#restoredTime').attr('data-parsley-pattern', Utils.validation.dateTimeRegex);
    jQuery('#restoredTime').attr('data-parsley-error-message', 'Time must be of form YYYY-MM-DD hh:mm:ss');
    
    jQuery('#serviceRecordForm').parsley();
    jQuery('#serviceRecordForm').parsley().validate();
    
    Validate.updateWarningOrSubmit('#serviceRecordForm');
    return false;
  }
  
};

ServiceRecord.ui = {
  
  deleteFile: function (serviceRecordId, fileKey) {
    if (confirm("Are you sure you want to delete this file?")) {
      Fluxion.doAjax(
        'serviceRecordControllerHelperService',
        'deleteServiceRecordAttachment',
        {'id': serviceRecordId, 'hashcode': fileKey, 'url': ajaxurl},
        {'doOnSuccess': Utils.page.pageReload}
      );
    }
  },

  serviceRecordFileUploadSuccess: function () {
    jQuery('#statusDiv').html("Upload complete.");
    Utils.page.pageReload();
  }
    
};