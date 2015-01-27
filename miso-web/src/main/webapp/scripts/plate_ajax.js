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

var projectColorMap = JSON.parse('{}');

var Plate = Plate || {
  deletePlate: function (plateId, successfunc) {
    if (confirm("Are you sure you really want to delete plate " + plateId + "? This operation is permanent!")) {
      Fluxion.doAjax(
              'plateControllerHelperService',
              'deletePlate',
              {'plateId': plateId, 'url': ajaxurl},
              {'doOnSuccess': function (json) {
                successfunc();
              }
              }
      );
    }
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
              'serviceClass': 'uk.ac.bbsrc.tgac.miso.core.data.Plate',
              'url': ajaxurl
            },
            {
              'doOnSuccess': function (json) {
                jQuery('#printServiceSelectDialog')
                        .html("<form>" +
                              "<fieldset class='dialog'>" +
                              "<select name='serviceSelect' id='serviceSelect' class='ui-widget-content ui-corner-all'>" +
                              json.services +
                              "</select></fieldset></form>");

                jQuery(function () {
                  jQuery('#printServiceSelectDialog').dialog({
                                                               autoOpen: false,
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
                });
                jQuery('#printServiceSelectDialog').dialog('open');
              },
              'doOnError': function (json) {
                alert(json.error);
              }
            }
    );

    /*
     Fluxion.doAjax(
     'plateControllerHelperService',
     'printPlateBarcodes',
     {
     'plates':plates,
     'url':ajaxurl
     },
     {
     'doOnSuccess':function (json) { alert(json.response); }
     }
     );
     */
  },

  showPlateLocationChangeDialog: function (plateId) {
    var self = this;
    jQuery('#changePlateLocationDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<label for='notetext'>New Location:</label>" +
            "<input type='text' name='locationBarcode' id='locationBarcode' class='form-control'/>" +
            "</fieldset></form>");

    jQuery(function () {
      jQuery('#changePlateLocationDialog').dialog({
                                                    autoOpen: false,
                                                    width: 400,
                                                    modal: true,
                                                    resizable: false,
                                                    buttons: {
                                                      "Save": function () {
                                                        self.changePlateLocation(plateId, jQuery('#locationBarcode').val());
                                                        jQuery(this).dialog('close');
                                                      },
                                                      "Cancel": function () {
                                                        jQuery(this).dialog('close');
                                                      }
                                                    }
                                                  });
    });
    jQuery('#changePlateLocationDialog').dialog('open');
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

Plate.tagbarcode = {
  getPlateBarcodesByMaterialType: function (form) {
    Fluxion.doAjax(
            'plateControllerHelperService',
            'getTagBarcodesForMaterialType',
            {'materialType': form.value, 'url': ajaxurl},
            {'doOnSuccess': function (json) {
              jQuery('#plateBarcodeSelect').html(json.plateBarcodes);
            }
            }
    );
  }
};

Plate.ui = {
  downloadPlateInputForm: function (documentFormat) {
    Fluxion.doAjax(
            'plateControllerHelperService',
            'downloadPlateInputForm',
            {
              'documentFormat': documentFormat,
              'url': ajaxurl
            },
            {'doOnSuccess': function (json) {
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
    if (iframe.contentDocument)
      iframedoc = iframe.contentDocument;
    else if (iframe.contentWindow)
      iframedoc = iframe.contentWindow.document;
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
            }
            else {
              var plate = pool.poolableElements[0];
              jQuery('#description').val(plate.identificationBarcode);
              jQuery('#size').val(plate.elements.length);
              impb.append(plate.elements.length + "-well plate: <b>" + plate.identificationBarcode + "</b>");
              impb.append("<ul>");
              for (var k = 0; k < plate.elements.length; k++) {
                var library = plate.elements[k];
                impb.append("<li>" + library.alias + "</li>")
              }
              impb.append("</ul>");
              impb.append("</div>");
            }
          });
        }
      }
    }
    else {
      setTimeout(function () {
        Plate.ui.processPlateUpload(frameId)
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
    if (iframe.contentDocument)
      iframedoc = iframe.contentDocument;
    else if (iframe.contentWindow)
      iframedoc = iframe.contentWindow.document;
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
                'tagBarcode': jQuery("input[name='tagBarcode']:selected").val(),
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
    }
    else {
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
      {'doOnSuccess': function (json) {
        jQuery('#plateElementsTable').html('');
        jQuery('#plateElementsTable').dataTable({
          "aaData": json.elementsArray,
          "aoColumns": [
            { "sTitle": "Name", "sType": "no-pla"},
            { "sTitle": "Alias"},
            { "sTitle": "Barcode Kit"},
            { "sTitle": "Barcode Sequence"},
            { "sTitle": "Edit"}
          ],
          "bJQueryUI": false,
          "iDisplayLength": 25,
          "aaSorting": [
            [0, "desc"]
          ]
          /*
           ,

           "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
           Fluxion.doAjax(
           'projectControllerHelperService',
           'checkOverviewByProjectId',
           {
           'projectId':aData[4],
           'url':ajaxurl
           },
           {'doOnSuccess': function(json) {
           jQuery('td:eq(4)', nRow).html(json.response);
           }
           }
           );
           }
           */
        });
        jQuery("#plateElementsTable_wrapper").prepend("<div class='float-right toolbar'></div>");
        //jQuery("#toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
      }
      }
    );
  },
  searchSamples: function (text) {
    jQuery('#sampleList').html("<img src='/styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
            'plateControllerHelperService',
            'searchSamples',
            {  'str': text,
              'url': ajaxurl
            },
            {'doOnSuccess': function (json) {
              jQuery('#sampleList').html(json.html);
              jQuery('#sampleList .dashboard').each(function () {
                var inp = jQuery(this);
                inp.dblclick(function () {
                  Plate.ui.insertSampleNextAvailable(inp);
                });
              });
            }
            });

  },

  selectSampleElement: function (elementId, elementName) {
    var div = "<div onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard'>";
    div += "<span class='float-left' id='element" + elementId + "'><input type='hidden' id='poolableElements" + elementId + "' value='" + elementName + "' name='poolableElements'/>";
    div += "<b>Element: " + elementName + "</b></span>";
    div += "<span onclick='Utils.ui.confirmRemove(jQuery(this).parent());' class='fa fa-fw fa-2x fa-times-circle-o pull-right'></span></div>";
    jQuery('#dillist').append(div);
    jQuery('#searchElementsResult').css('visibility', 'hidden');
  },


  insertSampleNextAvailable: function (sampleName,projectName) {
    var bgcolor;
    if (projectColorMap[projectName]!=null) {
      bgcolor= projectColorMap[projectName];
    } else{
      bgcolor= Plate.ui.get_random_color();
      projectColorMap[projectName]=bgcolor;
    }

    jQuery('.plateWell:empty:first').each(function () {
      var wellId = jQuery(this).attr("id");
      var sampleNameStr = sampleName.toString();
      jQuery(this).append('<div class="plateElement" data-toggle="popover" data-original-title="Edit Element" data-placement="bottom" data-trigger="focus" title="Sample: '
                                  + sampleNameStr + ' Project: '+ projectName + '" data-content="Sample: '
                                  + sampleNameStr + ' Project: '+ projectName + '"> <i onclick="Plate.ui.confirmRemove(jQuery(this));" class="fa fa-fw fa-1 fa-times-circle-o move-right"></i><input type="hidden" value="'
                                  + sampleName + ':' + wellId + ':' + projectName + '" name="sampleinwell"/></div>');
      jQuery(this).droppable("option", "disabled", true);
    });

    Plate.ui.makeElementDraggable();

  },

  get_random_color: function () {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
      color += letters[Math.floor(Math.random() * 16)];
    }
    return '#'+color;
  },

  confirmRemove: function (obj) {
    if (confirm("Are you sure you wish to remove this item?")) {
      obj.parent().parent().droppable("option", "disabled", false);
      obj.parent().parent().html('');
    }
  },

  makeElementDraggable: function () {

  jQuery( ".plateElement" ).draggable({
    containment:'#formbox',
    cursor:'pointer',
    stack: '#formbox div',
    revert: true
  });
  },

  exportSampleForm: function () {
    Utils.ui.disableButton("exportSampleForm");
    Fluxion.doAjax(
            'plateControllerHelperService',
            'exportSampleForm',
            {
              'form': jQuery('#plateExportForm').serializeArray(),
              // 'documentFormat':documentFormat,
              'url': ajaxurl
            },
            {'doOnSuccess': function (json) {
              Utils.page.pageRedirect('/miso/download/plate/forms/' + json.response);
            }
            }
    );
    Utils.ui.reenableButton("exportSampleForm", "Export Excel");
  },

  saveElements: function (plateId) {

    Utils.ui.disableButton("saveElements");
    Fluxion.doAjax(
            'plateControllerHelperService',
            'saveElements',
            {
              plateId: plateId,
              'form': jQuery('#elementsForm').serializeArray(),
              'url': ajaxurl
            },
            {'doOnSuccess': function (json) {
              Utils.ui.reenableButton("saveElements", "Saved");
            }
            }
    );
  },

  createPlateElementsUI: function (plateId, size) {
    if (size == '96'){
       jQuery('#elementsForm').html(jQuery('#plate96structure').html());
    } else if (size == '384'){
      jQuery('#elementsForm').html(jQuery('#plate384structure').html());
    } else {
      jQuery('#elementsForm').html('<b>Not supported plate size</b>');
    }

    jQuery('#plateElementsTable').html("<img src='../../styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
            'plateControllerHelperService',
            'createPlateElementsUI',
            {
              'url': ajaxurl,
              'plateId': plateId
            },
            {'doOnSuccess': function (json) {
              var elementsArray = JSON.parse(json[elementsArray]);

              for (var element in elementsArray) {
                jQuery('#' + element[0]).html('<input type="hidden" value="SAM' + element[1] + ':' + element[0] + '" name="sampleinwell"> SAM' + element[1]
                                                      + '<span onclick="ImportExport.confirmSampleRemove(this);" class="ui-icon ui-icon-circle-close"></span>');
              }
            }
            }
    );
  },


  createSampleSelectionTable: function () {
    jQuery('#sampleSelectionTable').html("<img src='/styles/images/ajax-loader.gif'/>");
    jQuery.fn.dataTableExt.oSort['no-sam-asc'] = function (x, y) {
      var a = parseInt(x.replace(/^SAM/i, ""));
      var b = parseInt(y.replace(/^SAM/i, ""));
      return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['no-sam-desc'] = function (x, y) {
      var a = parseInt(x.replace(/^SAM/i, ""));
      var b = parseInt(y.replace(/^SAM/i, ""));
      return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    };
    Fluxion.doAjax(
            'plateControllerHelperService',
            'listSampleSelectionTable',
            {
              'url': ajaxurl
            },
            {'doOnSuccess': function (json) {
              jQuery('#sampleSelectionTable').html('');
              var oTable = jQuery('#sampleSelectionTable').dataTable({
                                                         "aaData": json.array,
                                                         "aoColumns": [
                                                           { "sTitle": "Project Name", "sType": "natrual"},
                                                           { "sTitle": "Project Alias"},
                                                           { "sTitle": "Sample Name", "sType": "no-sam"},
                                                           { "sTitle": "Sample Alias"},
                                                           { "sTitle": "Add"}
                                                         ],
                                                         "bJQueryUI": false,
                                                         "iDisplayLength": 96,
                                                         "dom": 'f<"toolbar">rtip',
                                                          "fnRowCallback": function (nRow, aData, iDisplayIndex) {
                                                            if (jQuery.inArray([(aData[2]),(aData[0])], selected) != -1) {
                                                              jQuery(nRow).addClass('row_selected');
                                                            }
                                                            return nRow;
                                                          },
                                                         "aaSorting": [
                                                           [3, "asc"]
                                                         ]
                                                       });


              /* Click event handler */
              jQuery('#sampleSelectionTable tbody tr').live('click', function () {
                var aData = oTable.fnGetData(this);
                var iId = [(aData[2]),(aData[0])];

                if (jQuery.inArray(iId, selected) == -1) {
                  selected[selected.length++] = iId;
                }
                else {
                  selected = jQuery.grep(selected, function (value) {
                    return value != iId;
                  });
                }
                jQuery(this).toggleClass('row_selected');
              });

              jQuery("div.toolbar").html("<button type=\"button\" id=\"createPoolButton\" onClick=\"addBulkSamples();\" class=\"btn btn-default\">Add Selected Samples</button>");
              jQuery("div.toolbar").removeClass("toolbar");
            }
            }
    );
  }

};