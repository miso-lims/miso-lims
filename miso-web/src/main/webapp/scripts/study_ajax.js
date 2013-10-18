/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

var Study = Study || {
  deleteStudy : function(studyId, successfunc) {
    if (confirm("Are you sure you really want to delete STU" + studyId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'studyControllerHelperService',
        'deleteStudy',
        {'studyId':studyId, 'url':ajaxurl},
        {'doOnSuccess':function(json) {
          successfunc();
        }
      });
    }
  }
};

Study.ui = {
  createListingStudiesTable : function() {
    jQuery('#listingStudiesTable').html("<img src='../styles/images/ajax-loader.gif'/>");
    jQuery.fn.dataTableExt.oSort['no-stu-asc'] = function(x, y) {
      var a = parseInt(x.replace(/^STU/i, ""));
      var b = parseInt(y.replace(/^STU/i, ""));
      return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['no-stu-desc'] = function(x, y) {
      var a = parseInt(x.replace(/^STU/i, ""));
      var b = parseInt(y.replace(/^STU/i, ""));
      return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    };
    Fluxion.doAjax(
      'studyControllerHelperService',
      'listStudiesDataTable',
      {
        'url':ajaxurl
      },
      {'doOnSuccess': function(json) {
        jQuery('#listingStudiesTable').html('');
        jQuery('#listingStudiesTable').dataTable({
          "aaData": json.array,
          "aoColumns": [
            { "sTitle": "Study Name", "sType":"no-stu"},
            { "sTitle": "Alias"},
            { "sTitle": "Description"},
            { "sTitle": "Type"},
            { "sTitle": "Edit"}
          ],
          "bJQueryUI": true,
          "iDisplayLength":  25,
          "aaSorting":[
            [0,"desc"]
          ]
        });
      }
      }
    );
  }
};