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

var Submission = Submission || {
  saveSubmission : function(submissionId, form) {
    var arr = form.serializeArray();
    Fluxion.doAjax(
      'submissionControllerHelperService',
      'saveSubmission',
      {'submissionId':submissionId, 'url':ajaxurl, 'form':arr},
      {'doOnSuccess':function(json) {
          Utils.page.pageReload();
        }
      }
    )
  },

  validateSubmissionMetadata : function(submissionId) {
    Fluxion.doAjax(
      'submissionControllerHelperService',
      'validateSubmissionMetadata',
      {'submissionId':submissionId,'url':ajaxurl},
      {'doOnSuccess':function(json) {
        jQuery('#submissionreport').empty();
        if (json.ok) {
        }
        else {
          if (json.errors) {
            jQuery('#submissionreport').append("<h2>Errors</h2><div id='submissionreporterror' class='flasherror'></div>")
            for (var i = 0; i < json.errors.length; i++) {
              jQuery('#submissionreporterror').append(json.errors[i] + "<br/>");
            }
          }

          if (json.infos) {
            jQuery('#submissionreport').append("<h2>Info</h2><div id='submissionreportinfo' class='flashinfo'></div>")
            for (var i = 0; i < json.infos.length; i++) {
              jQuery('#submissionreportinfo').append(json.infos[i] + "<br/>");
            }
          }
        }
      }
    });
  },

  submitSubmissionMetadata : function(submissionId) {
    Fluxion.doAjax(
      'submissionControllerHelperService',
      'submitSubmissionMetadata',
      {'submissionId':submissionId,'url':ajaxurl},
      {'doOnSuccess':function(json) {
        jQuery('#submissionreport').empty();
        if (json.ok) {
        }
        else {
          if (json.errors) {
            jQuery('#submissionreport').append("<h2>Errors</h2><div id='submissionreporterror' class='flasherror'></div>")
            for (var i = 0; i < json.errors.length; i++) {
              jQuery('#submissionreporterror').append(json.errors[i] + "<br/>");
            }
          }

          if (json.infos) {
            jQuery('#submissionreport').append("<h2>Info</h2><div id='submissionreportinfo' class='flashinfo'></div>")
            for (var i = 0; i < json.infos.length; i++) {
              jQuery('#submissionreportinfo').append(json.infos[i] + "<br/>");
            }
          }
        }
      }
    });
  },

  submitSequenceData : function(submissionId) {
    jQuery('#submissionreport').empty();
    jQuery('#submissionreport').append("<h2>Starting datafile Upload...</h2>");
    Fluxion.doAjax(
            'submissionControllerHelperService',
            'submitSequenceData',
    {'submissionId':submissionId,'url':ajaxurl},
    {'doOnSuccess':function(json) {
        jQuery('#submissionreport').append("<h3>"+json.response+"</h3>")
        Submission.ui.displayUploadProgress(submissionId);
      }
    });
  }
};

Submission.ui = {
  openSubmissionProjectNodes : function(submissionId) {
    var self = this;
    Fluxion.doAjax(
      'submissionControllerHelperService',
      'openSubmissionProjectNodes',
    {
      'submissionId':submissionId,
      'url':ajaxurl
    },
    {
      'doOnSuccess': function(json) {
        for (var i = 0; i < json.projects.length; i++) {
         self.populateSubmissionProject(json.projects[i],submissionId);
        }
      }
    });
  },

  populateSubmissionProject : function(projectId, submissionId) {
    if (jQuery('#project' + projectId).hasClass("jstree-closed")) {
      Fluxion.doAjax(
        'submissionControllerHelperService',
        'populateSubmissionProject',
        {
          'projectId':projectId,
          'submissionId':submissionId,
          'url':ajaxurl
        },
        {
          'doOnSuccess': function(json) {
            if (jQuery('#runList'+projectId).length > 0) {
              jQuery('#project' + projectId).find('#runList'+projectId).remove();
            }
            jQuery('#project' + projectId).append(json.html);
            jQuery('#project' + projectId).removeClass("jstree-closed").addClass("jstree-open");
          }
        }
      );
    }
    else{
      jQuery('#runList' + projectId).hide('slow');
      jQuery('#project' + projectId).removeClass("jstree-open").addClass("jstree-closed");
    }
  },

  previewSubmissionMetadata : function(submissionId) {
    Fluxion.doAjax(
      'submissionControllerHelperService',
      'previewSubmissionMetadata',
      {'submissionId':submissionId,'url':ajaxurl},
      {'doOnSuccess':function(json) {
          Utils.page.pageReload();

          var win = window.open('', 'windowName');
          win.document.open('text/xml');
          win.document.write(json.metadata);
          win.document.close();
        }
      }
    );
  },

  downloadSubmissionMetadata : function(submissionId) {
    Fluxion.doAjax(
      'submissionControllerHelperService',
      'downloadSubmissionMetadata',
      {'submissionId':submissionId,'url':ajaxurl},
      {'doOnSuccess':function(json) {
          Utils.page.pageRedirect('/miso/download/submission/' + submissionId + '/' + json.response);
        }
      }
    );
  },

  displayUploadProgress : function(submissionId) {
    Fluxion.doAjax(
      'submissionControllerHelperService',
      'checkUploadProgress',
      {'submissionId':submissionId,'url':ajaxurl},
      {'doOnSuccess':function(json) {
        jQuery('#submissionreport').append("<p>" + json.message + "</p>");
        for (var i = 0; i < json.uploadJobs.length; i++) {
          jQuery('#submissionreport').append(
            "<tr><td style='width: 200px;'>"+ json.uploadJobs[i].filename+":</td>" +
            "<td style='width: 200px;'><div id='progressbar"+i+"'></div></td>" +
            "<td>"+ json.uploadJobs[i].percent+"%</td></tr>");
        }
        jQuery('#submissionreport').append("</table>");
        for (var i = 0; i < json.uploadJobs.length; i++) {
          jQuery("#progressbar"+i).progressbar({ value: json.uploadJobs[i].percent });
        }
      }
    });
  },

  updateUploadProgress : function(submissionId) {
    var self = this;
    Fluxion.doAjax(
      'submissionControllerHelperService',
      'checkUploadProgress',
      {'submissionId':submissionId,'url':ajaxurl},
      {'ajaxType':'periodical', 'updateFrequency':5},
      {'doOnSuccess':self.actuallyUpdateProgress
      }
    );
  },

  actuallyUpdateProgress : function(json) {
    for (var i = 0; i < json.uploadJobs.length; i++) {
      jQuery("#progressbar"+i).progressbar({ value: json.uploadJobs[i].percent });
    }
  },

  togglePartitionContents : function(checkbox) {
    var box = jQuery(checkbox);
    if (box.is(":checked")) {
      jQuery("ul input[type=checkbox]", jQuery(checkbox).parent()).each(function() {
        jQuery(this).attr("checked", "checked");
      });
    }
    else {
      jQuery("ul input[type=checkbox]", jQuery(checkbox).parent()).each(function() {
        jQuery(this).removeAttr("checked");
      });
    }
  }
};
