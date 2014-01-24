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

var ImportExport = ImportExport || {

  searchSamples: function (text) {
    jQuery('#sampleList').html("<img src='/styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
            'importExportControllerHelperService',
            'searchSamples',
            {  'str': text,
              'url': ajaxurl
            },
            {'doOnSuccess': function (json) {
              jQuery('#sampleList').html(json.html);
              jQuery('#sampleList .dashboard').each(function () {
                var inp = jQuery(this);
                inp.dblclick(function () {
                  ImportExport.insertSampleNextAvailable(inp);
                });
              });
            }
            });
  },

  selectSampleElement: function (elementId, elementName) {
    var div = "<div onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard'>";
    div += "<span class='float-left' id='element" + elementId + "'><input type='hidden' id='poolableElements" + elementId + "' value='" + elementName + "' name='poolableElements'/>";
    div += "<b>Element: " + elementName + "</b></span>";
    div += "<span onclick='Utils.ui.confirmRemove(jQuery(this).parent());' class='float-right ui-icon ui-icon-circle-close'></span></div>";
    jQuery('#dillist').append(div);
    jQuery('#searchElementsResult').css('visibility', 'hidden');
  },

  insertSampleNextAvailable: function (sampleDiv) {
    var sample = jQuery(sampleDiv);
    var sampleId = sample.find('input').attr("id");
    var sampleName = sample.find('input').attr("name");
    var projectName = sample.find('input').attr("projectname");
    var projectAlias = sample.find('input').attr("projectalias");
    var sampleAlias = sample.find('input').attr("samplealias");
    var dnaOrRNA = sample.find('input').attr("dnaOrRNA");
    jQuery('.plateWell:empty:first').each(function () {
      var wellId = jQuery(this).attr("id");
      if (jQuery("#selecttubeform").attr("checked") == "checked") {
        wellId = 'NA';
      }
      jQuery(this).append("<input type=\"hidden\" value=\"SAM" + sampleId + ":" + wellId + ":" + sampleAlias + ":" + projectName + ":" + projectAlias + ":" + dnaOrRNA + "\" name=\"sampleinwell\"/> " + sampleName);
      jQuery(this).append(" <span onclick='ImportExport.confirmSampleRemove(this);' class='ui-icon ui-icon-circle-close'></span>");
    });
  },

  confirmSampleRemove: function (t) {
    if (confirm("Remove this sample?")) {
      jQuery(t).parent().html('');
    }
  },

  exportSampleForm: function () {
    Utils.ui.disableButton("exportSampleForm");
    Fluxion.doAjax(
            'importExportControllerHelperService',
            'exportSampleForm',
            {
              'form': jQuery('#sampleExportForm').serializeArray(),
              // 'documentFormat':documentFormat,
              'url': ajaxurl
            },
            {'doOnSuccess': function (json) {
              Utils.page.pageRedirect('/miso/download/sample/forms/' + json.response);
            }
            }
    );
    Utils.ui.reenableButton("exportSampleForm", "Export Excel");
  },

  sampleSheetUploadSuccess: function (json) {
    jQuery('#samplesheet_statusdiv').html("Processing...");
    ImportExport.processSampleSheetUpload(json.frameId);
  },

  processSampleSheetUpload: function (frameId) {
    var iframe = document.getElementById(frameId);
    var iframedoc = iframe.document;
    if (iframe.contentDocument)
      iframedoc = iframe.contentDocument;
    else if (iframe.contentWindow)
      iframedoc = iframe.contentWindow.document;
    var response = jQuery(iframedoc).contents().find('body:first').find('#uploadresponsebody').val();
    if (!Utils.validation.isNullCheck(response)) {
      jQuery('#samplesheet_statusdiv').html("Processing... complete. Now Generating Library and Pool Export Sheet.");
      Utils.page.pageRedirect('/miso/download/library/forms/' + response);
    }
    else {
      setTimeout(function () {
        ImportExport.processSampleSheetUpload(frameId)
      }, 2000);
    }
  },

  cancelSampleSheetUpload: function () {
    jQuery('#samplesheetformdiv').css("display", "none");
  },

  processLibraryPoolSheetUpload: function (frameId) {
    var iframe = document.getElementById(frameId);
    var iframedoc = iframe.document;
    if (iframe.contentDocument)
      iframedoc = iframe.contentDocument;
    else if (iframe.contentWindow)
      iframedoc = iframe.contentWindow.document;
    var response = jQuery(iframedoc).contents().find('body:first').find('#uploadresponsebody').val();
    if (!Utils.validation.isNullCheck(response)) {
      jQuery('#librarypoolsheet_statusdiv').html("Processing... complete.<br/><br/>Jump to <a href='/miso/libraries'>List Libraries</a> & <a href='/miso/pools'>List Pools</a>");
    }
    else {
      setTimeout(function () {
        ImportExport.processLibraryPoolSheetUpload(frameId)
      }, 2000);
    }
  },

  libraryPoolSheetUploadSuccess: function (json) {
    jQuery('#librarypoolsheet_statusdiv').html("Processing...");
    ImportExport.processLibraryPoolSheetUpload(json.frameId);
  },

  cancelLibraryPoolSheetUpload: function () {
    jQuery('#librarypoolsheetformdiv').css("display", "none");
  }
};