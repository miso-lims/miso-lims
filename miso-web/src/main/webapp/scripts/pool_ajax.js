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

function poolSearchExperiments(input, platform) {
  Fluxion.doAjax(
          'poolControllerHelperService',
          'poolSearchExperiments',
          {'str':jQuery(input).val(), 'platform':platform, 'id':input.id, 'url':ajaxurl},
          {'doOnSuccess': function(json) {
            jQuery('#exptresult').css('visibility', 'visible');
            jQuery('#exptresult').html(json.html);
          }
          }
  );
}

function poolSearchSelectExperiment(experimentId, experimentName) {
  if (jQuery("#experiments" + experimentId).length > 0) {
    alert("Experiment " + experimentName + " is already associated with this pool.");
  }
  else {
    var div = "<div onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard'>";
    div += "<span class='float-left'><input type='hidden' id='experiment" + experimentId + "' value='" + experimentId + "' name='experiments'/>";
    div += "<b>Experiment: " + experimentName + "</b></span>";
    div += "<span onclick='confirmRemove(jQuery(this).parent());' class='float-right ui-icon ui-icon-circle-close'></span></div>";
    jQuery('#exptlist').append(div);
  }
  jQuery('#exptresult').css('visibility', 'hidden');
}

function poolSearchLibraryDilution(input, platform) {
  Fluxion.doAjax(
          'poolControllerHelperService',
          'poolSearchLibraryDilution',
          {'str':jQuery(input).val(), 'platform':platform, 'id':input.id, 'url':ajaxurl},
          {'doOnSuccess': function(json) {
            jQuery('#libdilresult').css('visibility', 'visible');
            jQuery('#libdilresult').html(json.html);
          }
          }
  );
}

function poolSearchSelectLibraryDilution(dilutionId, dilutionName) {
  if (jQuery("#dilutions" + dilutionId).length > 0) {
    alert("Dilution " + dilutionName + " is already part of this pool.");
  }
  else {
    var div = "<div onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard'>";
    div += "<span class='float-left'><input type='hidden' id='dilutions" + dilutionId + "' value='" + dilutionName + "' name='dilutions'/>";
    div += "<b>Dilution: " + dilutionName + "</b></span>";
    div += "<span onclick='confirmRemove(jQuery(this).parent());' class='float-right ui-icon ui-icon-circle-close'></span></div>";
    jQuery('#dillist').append(div);
  }
  jQuery('#libdilresult').css('visibility', 'hidden');
}

function poolSearchEmPcrDilution(input, platform) {
  Fluxion.doAjax(
          'poolControllerHelperService',
          'poolSearchEmPcrDilution',
          {'str':jQuery(input).val(), 'platform':platform, 'id':input.id, 'url':ajaxurl},
          {'doOnSuccess': function(json) {
            jQuery('#libdilresult').css('visibility', 'visible');
            jQuery('#libdilresult').html(json.html);
          }
          }
  );
}

function poolSearchSelectEmPcrDilution(dilutionId, dilutionName) {
  if (jQuery("#dilutions" + dilutionId).length > 0) {
    alert("Dilution " + dilutionName + " is already part of this pool.");
  }
  else {
    var div = "<div onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard'>";
    div += "<span class='float-left'><input type='hidden' id='dilutions" + dilutionId + "' value='" + dilutionName + "' name='dilutions'/>";
    div += "<b>Dilution: " + dilutionName + "</b></span>";
    div += "<span onclick='confirmRemove(jQuery(this).parent());' class='float-right ui-icon ui-icon-circle-close'></span></div>";
    jQuery('#dillist').append(div);
  }
  jQuery('#libdilresult').css('visibility', 'hidden');
}

function selectLibraryDilutionsByBarcodes(codes) {
  if (codes === "") {
    alert("Please input at least one barcode...");
  }
  else {
    Fluxion.doAjax(
            'poolControllerHelperService',
            'selectLibraryDilutionsByBarcodeList',
            {
              'barcodes':codes,
              'url':ajaxurl
            },
            {'updateElement':'dilimportlist'}
    );
  }
}

function select454EmPCRDilutionsByBarcodes(codes) {
  if (codes === "") {
    alert("Please input at least one barcode...");
  }
  else {
    Fluxion.doAjax(
            'poolControllerHelperService',
            'select454EmPCRDilutionsByBarcodeList',
            {
              'barcodes':codes,
              'url':ajaxurl
            },
            {'updateElement':'dilimportlist'}
    );
  }
}

function selectSolidEmPCRDilutionsByBarcodes(codes) {
  if (codes === "") {
    alert("Please input at least one barcode...");
  }
  else {
    Fluxion.doAjax(
            'poolControllerHelperService',
            'selectSolidEmPCRDilutionsByBarcodeList',
            {
              'barcodes':codes,
              'url':ajaxurl
            },
            {'updateElement':'dilimportlist'}
    );
  }
}

function libraryDilutionFileUploadProgress() {
  Fluxion.doAjaxUpload(
          'ajax_upload_form',
          'fileUploadProgressBean',
          'checkUploadStatus',
          {'url':ajaxurl},
          {'statusElement':'statusdiv', 'progressElement':'trash', 'doOnSuccess':libraryDilutionFileUploadSuccessFunc},
          {'':''}
  );
}

var libraryDilutionFileUploadSuccessFunc = function(json) {
  Fluxion.doAjax(
          'poolControllerHelperService',
          'selectLibraryDilutionsByBarcodeFile',
          {'url':ajaxurl},
          {'updateElement':'dilimportfile'}
  );
};

function ls454EmPcrDilutionFileUploadProgress() {
  Fluxion.doAjaxUpload(
          'ajax_upload_form',
          'fileUploadProgressBean',
          'checkUploadStatus',
          {'url':ajaxurl},
          {'statusElement':'statusdiv', 'progressElement':'trash', 'doOnSuccess':ls454EmPcrDilutionFileUploadSuccessFunc},
          {'':''}
  );
}

var ls454EmPcrDilutionFileUploadSuccessFunc = function(json) {
  Fluxion.doAjax(
          'poolControllerHelperService',
          'select454EmPCRDilutionsByBarcodeFile',
          {'url':ajaxurl},
          {'updateElement':'dilimportfile'}
  );
};

function solidEmPcrDilutionFileUploadProgress() {
  Fluxion.doAjaxUpload(
          'ajax_upload_form',
          'fileUploadProgressBean',
          'checkUploadStatus',
          {'url':ajaxurl},
          {'statusElement':'statusdiv', 'progressElement':'trash', 'doOnSuccess':solidEmPcrDilutionFileUploadSuccessFunc},
          {'':''}
  );
}

var solidEmPcrDilutionFileUploadSuccessFunc = function(json) {
  Fluxion.doAjax(
          'poolControllerHelperService',
          'selectSolidEmPCRDilutionsByBarcodeFile',
          {'url':ajaxurl},
          {'updateElement':'dilimportfile'}
  );
};

function printPoolBarcodes() {
  var pools = [];
  for (var i = 0; i < arguments.length; i++) {
    pools[i] = {'poolId':arguments[i]};
  }
  Fluxion.doAjax(
          'poolControllerHelperService',
          'printPoolBarcodes',
          {
            'pools':pools,
            'url':ajaxurl
          },
          {
            'doOnSuccess':function (json) {
              alert(json.response);
            }
          }
  );
}

function selectPoolBarcodesToPrint(tableId) {
  if (!jQuery(tableId).hasClass("display")) {
    jQuery(tableId).addClass("display");

    jQuery(tableId).find('tr:first th:eq(3)').remove();
    jQuery(tableId).find("tr").each(function() {
      jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
      jQuery(this).find("td:eq(3)").remove();
    });

    var headers = ['rowsel',
                   'name',
                   'creationDate',
                   'info'];

    var oTable = jQuery(tableId).dataTable({
                                             "aoColumnDefs": [
                                               {
                                                 "bUseRendered": false,
                                                 "aTargets": [ 0 ]
                                               }
                                             ],
                                             "bPaginate": false,
                                             "bInfo": false,
                                             "bJQueryUI": true,
                                             "bAutoWidth": true,
                                             "bSort": false,
                                             "bFilter": false,
                                             "sDom": '<<"toolbar">f>r<t>ip>'
                                           });

    jQuery(tableId).find("tr:first").prepend("<th>Select</th>");
    jQuery(tableId).find("tr:gt(0)").prepend("<td class='rowSelect'></td>");

    jQuery(tableId).find('.rowSelect').click(function() {
      if (jQuery(this).parent().hasClass('row_selected')) {
        jQuery(this).parent().removeClass('row_selected');
      }
      else {
        jQuery(this).parent().addClass('row_selected');
      }
    });

    jQuery("div.toolbar").html("<button onclick=\"printSelectedPoolBarcodes('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\">Print Selected</button>");
    jQuery("div.toolbar").append("<button onclick=\"pageReload();\" class=\"fg-button ui-state-default ui-corner-all\">Cancel</button>");
    jQuery("div.toolbar").removeClass("toolbar");
  }
}

function printSelectedPoolBarcodes(tableId) {
  var pools = [];
  var table = jQuery(tableId).dataTable();
  var nodes = fnGetSelected(table);
  for (var i = 0; i < nodes.length; i++) {
    pools[i] = {'poolId':jQuery(nodes[i]).attr("poolId")};
  }

  Fluxion.doAjax(
          'printerControllerHelperService',
          'listAvailableServices',
          {
            'serviceClass':'uk.ac.bbsrc.tgac.miso.core.data.Pool',
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

              jQuery(function() {
                jQuery('#printServiceSelectDialog').dialog({
                                                             autoOpen: false,
                                                             width: 400,
                                                             modal: true,
                                                             resizable: false,
                                                             buttons: {
                                                               "Print": function() {
                                                                 Fluxion.doAjax(
                                                                         'poolControllerHelperService',
                                                                         'printPoolBarcodes',
                                                                         {
                                                                           'serviceName':jQuery('#serviceSelect').val(),
                                                                           'pools':pools,
                                                                           'url':ajaxurl
                                                                         },
                                                                         {
                                                                           'doOnSuccess':function (json) {
                                                                             alert(json.response);
                                                                           }
                                                                         }
                                                                 );
                                                                 jQuery(this).dialog('close');
                                                               },
                                                               "Cancel": function() {
                                                                 jQuery(this).dialog('close');
                                                               }
                                                             }
                                                           });
              });
              jQuery('#printServiceSelectDialog').dialog('open');
            },
            'doOnError':function (json) {
              alert(json.error);
            }
          }
  );

  /*
   Fluxion.doAjax(
   'poolControllerHelperService',
   'printPoolBarcodes',
   {
   'pools':pools,
   'url':ajaxurl
   },
   {
   'doOnSuccess':function (json) {
   alert(json.response);
   }
   }
   );
   */
}

function filterRunPoolList(input) {
  var func = function(input) {
    var filter = jQuery(input).val();
    if (filter) {
      jQuery('#poolList').find("li").each(function() {
        var li = jQuery(this);
        var hide = false;

        if (li.not("[pName*=" + filter + "]")) {
          hide = true;
        }

        li.find(".poolListProjectAlias").each(function () {
          if (jQuery(this).not("[alias*=" + filter + "]")) {
            hide = true;
          }
        });

        if (hide) {
          li.hide();
        }
        else {
          li.show();
        }
      });

      //jQuery('#poolList').find("li[pName*=" + filter + "]").show();
    }
    else {
      jQuery('#poolList').find("li").show();
    }
  };

  timedFunc(func(input), 200);
}


function listPoolAverageInsertSizes() {
  jQuery('.averageInsertSize').html("<img src='../styles/images/ajax-loader.gif'/>");
  Fluxion.doAjax(
          'poolControllerHelperService',
          'listPoolAverageInsertSizes',
          {
            'url':ajaxurl
          },
          {
            'doOnSuccess': function(json) {
              jQuery.each(json, function(i, val) {
                jQuery('#average' + i).html(val)
              });
            }
          });
}

function deletePool(poolId, successfunc) {
  if (confirm("Are you sure you really want to delete pool " + poolId + "? This operation is permanent!")) {
    Fluxion.doAjax(
            'poolControllerHelperService',
            'deletePool',
            {'poolId':poolId, 'url':ajaxurl},
            {'doOnSuccess':function(json) {
              successfunc();
            }
            });
  }
}