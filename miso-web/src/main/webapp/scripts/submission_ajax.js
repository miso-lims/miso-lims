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

function previewSubmissionMetadata(submissionId) {
    Fluxion.doAjax(
            'submissionControllerHelperService',
            'previewSubmissionMetadata',
    {'submissionId':submissionId,'url':ajaxurl},
    {'doOnSuccess':function(json) {
        var win = window.open('', 'windowName');
        win.document.open('text/xml');
        //win.document.write("<?xml version='1.0' encoding='UTF-8'?>");
        //var doc = json.metadata;
        //doc = doc.replace("<","&lt");
        //doc = doc.replace(">","&gt");
        win.document.write(json.metadata);

        win.document.close();        
      }
    });
}

function validateSubmissionMetadata(submissionId) {
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
}

function submitSubmissionMetadata(submissionId) {
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
}

function submitSequenceData(submissionId) {
    jQuery('#submissionreport').empty();
    jQuery('#submissionreport').append("<h2>Starting datafile Upload...</h2>");
    Fluxion.doAjax(
            'submissionControllerHelperService',
            'submitSequenceData',
    {'submissionId':submissionId,'url':ajaxurl},
    {'doOnSuccess':function(json) {

        jQuery('#submissionreport').append("<h3>"+json.response+"</h3>")
        displayUploadProgress(submissionId);
      }

    });
}

  function displayUploadProgress(submissionId) {

    Fluxion.doAjax(
            'submissionControllerHelperService',
            'checkUploadProgress',
    {'submissionId':submissionId,'url':ajaxurl},
    //{'ajaxType':'periodical', 'updateFrequency':5},
    {'doOnSuccess':function(json) {


      //jQuery('#submissionreport').empty();
      //jQuery('#submissionreport').append("displayUploadProgress called:");

      //jQuery('#submissionreport').append("<h2>Uploading...</h2>");
      jQuery('#submissionreport').append("<p>" + json.message + "</p>");
//      jQuery('#submissionreport').append(
//          "<table><td style='width: 200px;'>File:</td>" +
//                 "<td style='width: 200px;'>%age complete</td></tr>");
      //SORT OUT!  if(json.has(uploadJobs)&&uploadJobs.size!=0){
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

    // }
    }
    )
  }


function updateUploadProgress(submissionId) {

    Fluxion.doAjax(
            'submissionControllerHelperService',
            'checkUploadProgress',
    {'submissionId':submissionId,'url':ajaxurl},
    {'ajaxType':'periodical', 'updateFrequency':5},
    {'doOnSuccess':actuallyUpdateProgress
    });
}

var actuallyUpdateProgress = function(json) {
  for (var i = 0; i < json.uploadJobs.length; i++) {
        jQuery("#progressbar"+i).progressbar({ value: json.uploadJobs[i].percent });
};
}
function parseSampleXml(xmlString) {
    jQuery(xmlString).find("location").each(function() {
        var foo = jQuery(this).find("foo").text();
        $("table#table tbody").append("<tr>" + "<td nowrap='nowrap'>" + jQuery(this).find("bar").text() + "</td>" + "<td>" + jQuery(this).find("baz").text() + "</td>" + "</tr>");
        $('table#table tbody tr:even').addClass('alt');
    });
}

function openSubmissionProjectNodes(submissionId) {

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
       populateSubmissionProject(json.projects[i],submissionId);
       // jQuery('#project' + json.projects[i]).removeClass("jstree-closed").addClass("jstree-open");
      }
    }
  });
}

function populateSubmissionProject(projectId, submissionId) {
    //alert(">"+projectId+"-"+submissionId+"<");

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
        //jQuery(alert("populateSubmissionProject has been successful for submission:" +submissionId));
        //jQuery("#submissionTree").jstree("refresh");

        jQuery('#project' + projectId).removeClass("jstree-closed").addClass("jstree-open");
      }
    });
  }

    else{

        //jQuery('#project' + projectId).toggle(function(){
        jQuery('#runList' + projectId).hide('slow');
        jQuery('#project' + projectId).removeClass("jstree-open").addClass("jstree-closed");
        //},function(){
        //jQuery('#runList'+projectId).show('fast');
   //});

  }
  
}