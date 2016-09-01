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

var Plate = Plate || {
  deletePlate: function (plateId, successfunc) {
    if (confirm("Are you sure you really want to delete plate " + plateId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'plateControllerHelperService',
        'deletePlate',
        {
          'plateId': plateId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function () {
            successfunc();
          }
        }
      );
    }
  },

  validatePlate: function () {
    Validate.cleanFields('#plate-form');
    jQuery('#plate-form').parsley().destroy();
    
    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-required', 'true');
    jQuery('#description').attr('data-parsley-maxlength', '100');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    
    // Creation Date input field validation
    jQuery('#creationdatepicker').attr('class', 'form-control');
    jQuery('#creationdatepicker').attr('required', 'true');
    jQuery('#creationdatepicker').attr('data-parsley-pattern', Utils.validation.dateRegex);
    jQuery('#creationdatepicker').attr('data-date-format', 'DD/MM/YYYY');
    jQuery('#creationdatepicker').attr('data-parsley-error-message', 'Date must be of form DD/MM/YYYY');
    
    // Plate Material Type radio button validation
    jQuery('#plateMaterialType').attr('class', 'form-control');
    jQuery('#plateMaterialType1').attr('required', 'true');
    jQuery('#plateMaterialType').attr('data-parsley-error-message', 'You must select a Plate Material Type');
    jQuery('#plateMaterialType1').attr('data-parsley-errors-container', '#plateMaterialError');
    jQuery('#plateMaterialType').attr('data-parsley-class-handler', '#plateMaterialButtons');
    
    jQuery('#plate-form').parsley();
    jQuery('#plate-form').parsley().validate();
    
    Validate.updateWarningOrSubmit('#plate-form');
    return false;
  }
};

Plate.barcode = {
  printPlateBarcodes: function () {
    var plates = [];
    for (var i = 0; i < arguments.length; i++) {
      plates[i] = {'plateId': arguments[i]};
    }

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'serviceClass':'uk.ac.bbsrc.tgac.miso.core.data.Plate',
        'url':ajaxurl
      },
      {
        'doOnSuccess':function (json) {
          jQuery('#printServiceSelectDialog')
            .html("<form>" +
                  "<fieldset class='dialog'>" +
                  "<select name='serviceSelect' id='serviceSelect' class='ui-widget-content ui-corner-all'>" +
                  json.services +
                  "</select></fieldset></form>");

          jQuery('#printServiceSelectDialog').dialog({
            width: 400,
            modal: true,
            resizable: false,
            buttons: {
              "Print": function () {
                Fluxion.doAjax(
                 'plateControllerHelperService',
                 'printPlateBarcodes',
                 {
                   'serviceName': jQuery('#serviceSelect').val(),
                   'plates': plates,
                   'url': ajaxurl
                 },
                 {
                   'doOnSuccess': function (json) {
                     alert(json.response);
                   }
                 }
               );
               jQuery(this).dialog('close');
             },
             "Cancel": function () {
               jQuery(this).dialog('close');
             }
           }
         });
        },
        'doOnError': function (json) {
          alert(json.error);
        }
      }
    );
  },

  showPlateLocationChangeDialog: function (plateId) {
    var self = this;
    jQuery('#changePlateLocationDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<label for='notetext'>New Location:</label>" +
            "<input type='text' name='locationBarcode' id='locationBarcode' class='text ui-widget-content ui-corner-all'/>" +
            "</fieldset></form>");

    jQuery('#changePlateLocationDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Save": function() {
          self.changePlateLocation(plateId, jQuery('#locationBarcode').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function() {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  changePlateLocation: function (plateId, barcode) {
    Fluxion.doAjax(
      'plateControllerHelperService',
      'changePlateLocation',
      {
        'plateId': plateId,
        'locationBarcode': barcode,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  }
};

Plate.index = {
  getIndicesByMaterialType: function (form) {
    Fluxion.doAjax(
      'plateControllerHelperService',
      'getIndicesForMaterialType',
      {
        'materialType': form.value,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#plateIndexSelect').html(json.plateIndices);
        }
      }
    );
  }
};

Plate.ui = {
  editPlateIdBarcode: function(span, id) {
    Fluxion.doAjax(
      'loggedActionService',
      'logAction',
      {
        'objectId': id,
        'objectType': 'Plate',
        'action': 'editPlateIdBarcode',
        'url': ajaxurl
      },
      {}
    );

    var v = span.find('a').text();
    if (v && v !== "") {
      span.html("<input type='text' value='" + v + "' name='identificationBarcode' id='identificationBarcode'>");
    }
  },

  showPlateIdBarcodeChangeDialog: function (plateId, plateIdBarcode) {
    var self = this;
    jQuery('#changePlateIdBarcodeDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<strong><label>Current Barcode: </label></strong>" + plateIdBarcode +
            "<br /><strong><label for='notetext'>New Barcode:</label></strong>" +
            "<input type='text' name='idBarcodeInput' id='idBarcodeInput' class='text ui-widget-content ui-corner-all' />" +
            "</fieldset></form>");

    jQuery('#changePlateIdBarcodeDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Save": function () {
          self.changePlateIdBarcode(plateId, jQuery('#idBarcodeInput').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  changePlateIdBarcode: function (plateId, idBarcode) {
    Fluxion.doAjax(
      'plateControllerHelperService',
      'changePlateIdBarcode',
      {
        'plateId': plateId,
        'identificationBarcode': idBarcode,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  downloadPlateInputForm: function (documentFormat) {
    Fluxion.doAjax(
      'plateControllerHelperService',
      'downloadPlateInputForm',
      {
        'documentFormat': documentFormat,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          Utils.page.pageRedirect('/miso/download/plate/forms/' + json.response);
        }
      }
    );
  },

  uploadPlateInputForm: function () {
    jQuery('#plateformdiv').css("display", "block");
  },

  cancelPlateInputFormUpload: function () {
    jQuery('#plateformdiv').css("display", "none");
  },

  plateInputFormUploadSuccess: function (json) {
    jQuery('#plateform_statusdiv').html("Processing...");
    Plate.ui.processPlateUpload(json.frameId);
  },

  processPlateUpload: function (frameId) {
    var iframe = document.getElementById(frameId);
    var iframedoc = iframe.document;
    if (iframe.contentDocument) {
      iframedoc = iframe.contentDocument;
    } else if (iframe.contentWindow) {
        iframedoc = iframe.contentWindow.document;
    }
    var response = jQuery(iframedoc).contents().find('body:first').find('#uploadresponsebody').val();
    if (!Utils.validation.isNullCheck(response)) {
      var json = jQuery.parseJSON(response);
      if (!Utils.validation.isNullCheck(json.pools)) {
        jQuery('#plateform_statusdiv').html("Processing... complete.");
        for (var i = 0; i < json.pools.length; i++) {
          jQuery.each(json.pools[i], function (key, value) {
            jQuery('#plateform_import').append("<div id='importbox-" + key + "' class='simplebox backwhite'>");
            var pool = value;
            var impb = jQuery('#importbox-' + key);
            impb.append("<span style='float:right;'><button type='button' class='fg-button ui-state-default ui-corner-all' onclick='Plate.ui.removeImportBox(this);'>Cancel</button>");
            impb.append("<button type='button' id='saveImportedElementsButton' class='fg-button ui-state-default ui-corner-all' onclick='Plate.ui.saveImportedElements(\"" + frameId + "\");'>Import</button></span>");
            impb.append("Pool alias: <b>" + pool.alias + "</b></br>");
            if (pool.poolableElements.length > 1) {
              alert("Something strange has happened. Each plate import sheet should only represent a single plate instance, but more have been found!");
            } else {
              var plate = pool.poolableElements[0];
              jQuery('#description').val(plate.identificationBarcode);
              jQuery('#size').val(plate.elements.length);
              impb.append(plate.elements.length + "-well plate: <b>" + plate.identificationBarcode + "</b>");
              impb.append("<ul>");
              for (var k = 0; k < plate.elements.length; k++) {
                var library = plate.elements[k];
                impb.append("<li>" + library.alias + "</li>");
              }
              impb.append("</ul>");
              impb.append("</div>");
            }
          });
        }
      }
    } else {
      setTimeout(function () {
        Plate.ui.processPlateUpload(frameId);
      }, 2000);
    }
  },

  removeImportBox: function (button) {
    if (confirm("Are you sure you want to cancel the plate import?")) {
      jQuery(button).parent().parent().remove();
    }
  },

  saveImportedElements: function (frameId) {
    Utils.ui.disableButton("saveImportedElementsButton");
    var iframe = document.getElementById(frameId);
    var iframedoc = iframe.document;
    if (iframe.contentDocument) {
        iframedoc = iframe.contentDocument;
    } else if (iframe.contentWindow) {
        iframedoc = iframe.contentWindow.document;
    }
    var response = jQuery(iframedoc).contents().find('body:first').find('#uploadresponsebody').val();
    if (!Utils.validation.isNullCheck(response)) {
      var json = jQuery.parseJSON(response);
      Fluxion.doAjax(
        'plateControllerHelperService',
        'saveImportedElements',
        {
          'description': jQuery('#description').val(),
          'creationDate': jQuery('#creationdatepicker').val(),
          'plateMaterialType': jQuery("input[name='plateMaterialType']:checked").val(),
          'index': jQuery("input[name='index']:selected").val(),
          'elements': json,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            Plate.ui.createMultiPlateElementsTable(json);
          }
        }
      );
    }
  },

  createMultiPlateElementsTable: function (json) {
    if (json.error) {
      alert(json.error);
    } else {
      if (json.plates) {
        for (var i = 0; i < json.plates.length; i++) {
          Plate.ui.createPlateElementsTable(json.plates[i].plateId);
        }
      }
    }
  },

  createPlateElementsTable: function (plateId) {
    jQuery('#plateformdiv').html("");
    jQuery('#plateElementsTable').html("<img src='../../styles/images/ajax-loader.gif'/>");
    jQuery.fn.dataTableExt.oSort['no-pla-asc'] = function (x, y) {
      var a = parseInt(x.replace(/^PLA/i, ""));
      var b = parseInt(y.replace(/^PLA/i, ""));
      return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['no-pla-desc'] = function (x, y) {
      var a = parseInt(x.replace(/^PLA/i, ""));
      var b = parseInt(y.replace(/^PLA/i, ""));
      return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    };
    Fluxion.doAjax(
      'plateControllerHelperService',
      'plateElementsDataTable',
      {
        'url': ajaxurl,
        'plateId': plateId
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#plateElementsTable').html('');
          jQuery('#plateElementsTable').dataTable({
            "aaData": json.elementsArray,
            "aoColumns": [
              { "sTitle": "Name", "sType": "no-pla"},
              { "sTitle": "Alias"},
              { "sTitle": "Index Family"},
              { "sTitle": "Index Sequence"},
              { "sTitle": "Edit"}
            ],
            "bJQueryUI": true,
            "iDisplayLength": 25,
            "aaSorting": [
              [0, "desc"]
            ],
            "sDom": '<l<"#toolbar">f>r<t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>'
          });
          jQuery("#toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
        }
      }
    );
  },
  searchSamples: function (text) {
    jQuery('#sampleList').html("<img src='/styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
      'plateControllerHelperService',
      'searchSamples',
      {
        'str': text,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#sampleList').html(json.html);
          jQuery('#sampleList .dashboard').each(function () {
            var inp = jQuery(this);
            inp.dblclick(function () {
              Plate.ui.insertSampleNextAvailable(inp);
            });
          });
        }
      }
    );
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
    var projectName = sample.find('input').attr("projectName");
    var sampleAlias = sample.find('input').attr("sampleAlias");
    jQuery('.plateWell:empty:first').each(function () {
      var wellId = jQuery(this).attr("id");
      jQuery(this).append("<input type=\"hidden\" value=\"" + sampleId + ":" + wellId+ ":" +sampleAlias+ ":" +projectName + "\" name=\"sampleinwell\"/> " + sampleName);
      jQuery(this).append(" <span onclick='Plate.ui.confirmSampleRemove(this);' class='ui-icon ui-icon-circle-close'></span>");
    });
  },

  confirmSampleRemove: function (t) {
    if (confirm("Remove this sample?")) {
      jQuery(t).parent().html('');
    }
  },

  exportSampleForm: function (){
    Utils.ui.disableButton("exportSampleForm");
    Fluxion.doAjax(
      'plateControllerHelperService',
      'exportSampleForm',
      {
        'form':jQuery('#plateExportForm').serializeArray(),
        'url':ajaxurl
      },
      {
        'doOnSuccess':function (json) {
          Utils.page.pageRedirect('/miso/download/plate/forms/' + json.response );
        }
      }
    );
    Utils.ui.reenableButton("exportSampleForm", "Export Excel");
  }
};
