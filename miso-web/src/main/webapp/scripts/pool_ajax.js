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

var Pool = Pool || {
  deletePool : function(poolId, successfunc) {
    if (confirm("Are you sure you really want to delete pool " + poolId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'poolControllerHelperService',
        'deletePool',
        {'poolId':poolId, 'url':ajaxurl},
        {'doOnSuccess':function(json) {
          successfunc();
        }
        }
      );
    }
  }
};

Pool.qc = {
  insertPoolQCRow : function(poolId, includeId) {
    if (!jQuery('#poolQcTable').attr("qcInProgress")) {
      jQuery('#poolQcTable').attr("qcInProgress", "true");

      $('poolQcTable').insertRow(1);
      //QCId  QCed By  	QC Date  	Method  	Results

      if (includeId) {
        var column1 = $('poolQcTable').rows[1].insertCell(-1);
        column1.innerHTML = "<input type='hidden' id='poolId' name='poolId' value='" + poolId + "'/>";
      }
      var column2 = $('poolQcTable').rows[1].insertCell(-1);
      column2.innerHTML = "<input id='poolQcUser' name='poolQcUser' type='hidden' value='" + $('currentUser').innerHTML + "'/>" + $('currentUser').innerHTML;
      var column3 = $('poolQcTable').rows[1].insertCell(-1);
      column3.innerHTML = "<input id='poolQcDate' name='poolQcDate' type='text'/>";
      var column4 = $('poolQcTable').rows[1].insertCell(-1);
      column4.innerHTML = "<select id='poolQcType' name='poolQcType' onchange='Pool.qc.changePoolQcUnits(this);'/>";
      var column5 = $('poolQcTable').rows[1].insertCell(-1);
      column5.innerHTML = "<input id='poolQcResults' name='poolQcResults' type='text'/><span id='units'/>";
      var column6 = $('poolQcTable').rows[1].insertCell(-1);
      column6.innerHTML = "<a href='javascript:void(0);' onclick='Pool.qc.addPoolQC();'/>Add</a>";

      Utils.ui.addMaxDatePicker("poolQcDate", 0);

      Fluxion.doAjax(
        'poolControllerHelperService',
        'getPoolQcTypes',
        {'url':ajaxurl},
        {'doOnSuccess':function(json) {
            jQuery('#poolQcType').html(json.types);
            jQuery('#units').html(jQuery('#poolQcType option:first').attr("units"));
          }
        }
      );
    }
    else {
      alert("Cannot add another QC when one is already in progress.")
    }
  },

  changePoolQcUnits : function(input) {
    jQuery('#units').html(jQuery('#poolQcType').find(":selected").attr("units"));
  },

  addPoolQC : function() {
    var f = Utils.mappifyInputs("addQcForm");
    Fluxion.doAjax(
      'poolControllerHelperService',
      'addPoolQC',
      {
        'poolId':f.id,
        'qcCreator':f.poolQcUser,
        'qcDate':f.poolQcDate,
        'qcType':f.poolQcType,
        'results':f.poolQcResults,
        'url':ajaxurl
      },
      {'updateElement':'poolQcTable',
        'doOnSuccess':function(json) {
          jQuery('#poolQcTable').removeAttr("qcInProgress");
        }
      }
    );
  },

  changePoolQCRow : function(qcId, poolId) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'changePoolQCRow',
      {
        'poolId':poolId,
        'qcId':qcId,
        'url':ajaxurl
      },
      {'doOnSuccess':function(json) {
          jQuery('#result' + qcId).html(json.results);
          jQuery('#edit' + qcId).html(json.edit);
        }
      }
    );
  },

  editPoolQC : function(qcId, poolId) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'editPoolQC',
      {
        'poolId':poolId,
        'qcId':qcId,
        'result':jQuery('#results' + qcId).val(),
        'url':ajaxurl
      },
      {'doOnSuccess':Utils.page.pageReload
      }
    );
  }
};

Pool.wizard = {
  insertPoolQCRow : function() {
    if (!jQuery('#poolQcTable').attr("qcInProgress")) {
      jQuery('#poolQcTable').attr("qcInProgress", "true");

      $('poolQcTable').insertRow(1);
      var column3 = $('poolQcTable').rows[1].insertCell(-1);
      column3.innerHTML = "<input id='poolQcDate' name='poolQcDate' type='text'/>";
      var column4 = $('poolQcTable').rows[1].insertCell(-1);
      column4.innerHTML = "<select id='poolQcType' name='poolQcType' onchange='Pool.qc.changePoolQcUnits(this);'/>";
      var column5 = $('poolQcTable').rows[1].insertCell(-1);
      column5.innerHTML = "<input id='poolQcResults' name='poolQcResults' type='text'/><span id='units'/>";
      var column6 = $('poolQcTable').rows[1].insertCell(-1);
      column6.innerHTML = "<a href='javascript:void(0);' onclick='Pool.wizard.addPoolQC(this);'/>Add</a>";

      jQuery("#poolQcDate").val(jQuery.datepicker.formatDate('dd/mm/yy', new Date()));
      Utils.ui.addMaxDatePicker("poolQcDate", 0);

      Fluxion.doAjax(
        'poolControllerHelperService',
        'getPoolQcTypes',
        {'url':ajaxurl},
        {'doOnSuccess':function(json) {
            jQuery('#poolQcType').html(json.types);
            jQuery('#units').html(jQuery('#poolQcType option:first').attr("units"));
          }
        }
      );
    }
    else {
      alert("Cannot add another QC when one is already in progress.")
    }
  },

  addPoolQC : function(add) {
    var row = jQuery(add).parent().parent();
    jQuery(add).html("Remove");
    jQuery(add).removeAttr("onclick");
    jQuery(add).click(function() {
      if (confirm("Remove this QC?")) {
        row.remove();
      }
    });
    jQuery('#poolQcTable').removeAttr("qcInProgress");

    row.find(":input").each(function() {
      var td = jQuery(this).parent();
      jQuery(td).attr("name", jQuery(this).attr("name"));
      jQuery(this).parent().html(jQuery(this).val());
    });
  }
};

Pool.ui = {
  selectElementsByBarcodes : function(codes) {
    if (codes === "") {
      alert("Please input at least one barcode...");
    }
    else {
      Fluxion.doAjax(
        'poolControllerHelperService',
        'selectElementsByBarcodeList',
        {
          'barcodes':codes,
          'url':ajaxurl
        },
        {'updateElement':'importlist'}
      );
    }
  },

  dilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {'url':ajaxurl},
      {'statusElement':'statusdiv', 'progressElement':'trash', 'doOnSuccess':self.dilutionFileUploadSuccessFunc},
      {'':''}
    );
  },

  dilutionFileUploadSuccessFunc : function(json) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'selectDilutionsByBarcodeFile',
      {'url':ajaxurl},
      {'updateElement':'dilimportfile'}
    );
  },

  /** Deprecated */
  libraryDilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {'url':ajaxurl},
      {'statusElement':'statusdiv', 'progressElement':'trash', 'doOnSuccess':self.libraryDilutionFileUploadSuccessFunc},
      {'':''}
    );
  },

  /** Deprecated */
  libraryDilutionFileUploadSuccessFunc : function(json) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'selectLibraryDilutionsByBarcodeFile',
      {'url':ajaxurl},
      {'updateElement':'dilimportfile'}
    );
  },

  /** Deprecated */
  ls454EmPcrDilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {'url':ajaxurl},
      {'statusElement':'statusdiv', 'progressElement':'trash', 'doOnSuccess':self.ls454EmPcrDilutionFileUploadSuccessFunc},
      {'':''}
    );
  },

  /** Deprecated */
  ls454EmPcrDilutionFileUploadSuccessFunc : function(json) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'select454EmPCRDilutionsByBarcodeFile',
      {'url':ajaxurl},
      {'updateElement':'dilimportfile'}
    );
  },

  /** Deprecated */
  solidEmPcrDilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {'url':ajaxurl},
      {'statusElement':'statusdiv', 'progressElement':'trash', 'doOnSuccess':self.solidEmPcrDilutionFileUploadSuccessFunc},
      {'':''}
    );
  },

  /** Deprecated */
  solidEmPcrDilutionFileUploadSuccessFunc : function(json) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'selectSolidEmPCRDilutionsByBarcodeFile',
      {'url':ajaxurl},
      {'updateElement':'dilimportfile'}
    );
  },

  listPoolAverageInsertSizes : function() {
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
      }
    );
  },

  createListingPoolsTable : function(platform) {
    var table = 'listing'+platform+'PoolsTable';
    jQuery('#'+table).html("<img src='../styles/images/ajax-loader.gif'/>");
    jQuery.fn.dataTableExt.oSort['no-po-asc'] = function(x, y) {
      var a = parseInt(x.replace(/^.*PO/i, ""));
      var b = parseInt(y.replace(/^.*PO/i, ""));
      return ((a < b) ? -1 : ((a > b) ? 1 : 0));
    };
    jQuery.fn.dataTableExt.oSort['no-po-desc'] = function(x, y) {
      var a = parseInt(x.replace(/^.*PO/i, ""));
      var b = parseInt(y.replace(/^.*PO/i, ""));
      return ((a < b) ? 1 : ((a > b) ? -1 : 0));
    };
    Fluxion.doAjax(
      'poolControllerHelperService',
      'listPoolsDataTable',
      {
        'url':ajaxurl,
        'platform':platform
      },
      {'doOnSuccess': function(json) {
        jQuery('#'+table).html('');
        jQuery('#'+table).dataTable({
          "aaData": json.pools,
          "aoColumns": [
            { "sTitle": "Name", "sType":"no-po"},
            { "sTitle": "Alias"},
            { "sTitle": "Date Created"},
            { "sTitle": "Information"},
            { "sTitle": "Average Insert Size"},
            { "sTitle": "Concentration"},
            { "sTitle": "Edit"}
          ],
          "bJQueryUI": true,
          "iDisplayLength":  25,
          "aaSorting":[
            [0,"desc"]
          ] ,
          "fnRowCallback": function(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
            Fluxion.doAjax(
              'poolControllerHelperService',
              'checkInfoByPoolId',
              {
                'poolId':aData[3],
                'url':ajaxurl
              },
              {'doOnSuccess': function(json) {
                jQuery('td:eq(3)', nRow).html(json.response);
              }
              }
            );

            Fluxion.doAjax(
              'poolControllerHelperService',
              'checkAverageInsertSizeByPoolId',
              {
                'poolId':aData[4],
                'url':ajaxurl
              },
              {'doOnSuccess': function(json) {
                jQuery('td:eq(4)', nRow).html(json.response);
              }
              }
            );

            Fluxion.doAjax(
              'poolControllerHelperService',
              'checkConcentrationByPoolId',
              {
                'poolId':aData[5],
                'url':ajaxurl
              },
              {'doOnSuccess': function(json) {
                jQuery('td:eq(5)', nRow).html(json.response);
              }
              }
            );
          }
        });
        }
      }
    );
  },

  getPoolableElementInfo : function(poolId, elementId) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'getPoolableElementInfo',
      {'poolId':poolId, 'elementId':elementId, 'url':ajaxurl},
      {'doOnSuccess': function(json) {
        jQuery('#element'+elementId).append(json.info);
      }
      }
    );
  },

  createElementSelectDatatable : function(platform) {
    jQuery('#elementSelectDatatableDiv').html("<table cellpadding='0' width='100%' cellspacing='0' border='0' class='display' id='elementSelectDatatable'></table>");
    jQuery('#elementSelectDatatable').html("<img src='/styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
            'poolControllerHelperService',
            'createElementSelectDataTable',
            {
              'url':ajaxurl,
              'platform':platform
            },
            {'doOnSuccess': function(json) {

              jQuery('#elementSelectDatatable').html('');
              jQuery('#elementSelectDatatable').dataTable({
                                            "aaData": json.poolelements,
                                            "aoColumns": [
                                              { "sTitle": "Dilution Name", "sType":"natural"},
                                              { "sTitle": "Library", "sType":"natural"},
                                              { "sTitle": "Sample", "sType":"natural"},
                                              { "sTitle": "Project", "sType":"natural"},
                                              { "sTitle": "Add"}
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
  },

  prepareElements : function () {
    Pool.ui.createElementSelectDatatable(jQuery('#platformType').val());
  }
};

Pool.search = {
  poolSearchExperiments : function(input, platform) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'poolSearchExperiments',
      {'str':jQuery(input).val(), 'platform':platform, 'id':input.id, 'url':ajaxurl},
      {'doOnSuccess': function(json) {
        jQuery('#exptresult').css('visibility', 'visible');
        jQuery('#exptresult').html(json.html);
        jQuery(input).blur(function() {
          jQuery('#exptresult :first-child').hide();
        });
      }
      }
    );
  },

  poolSearchSelectExperiment : function(experimentId, experimentName) {
    if (jQuery("#experiments" + experimentId).length > 0) {
      alert("Experiment " + experimentName + " is already associated with this pool.");
    }
    else {
      var div = "<div onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard'>";
      div += "<span class='float-left'><input type='hidden' id='experiment" + experimentId + "' value='" + experimentId + "' name='experiments'/>";
      div += "<b>Experiment: " + experimentName + "</b></span>";
      div += "<span onclick='Utils.ui.confirmRemove(jQuery(this).parent());' class='float-right ui-icon ui-icon-circle-close'></span></div>";
      jQuery('#exptlist').append(div);
    }
    jQuery('#exptresult').css('visibility', 'hidden');
  },

  poolSearchLibraryDilution : function(input, platform) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'poolSearchLibraryDilution',
      {'str':jQuery(input).val(), 'platform':platform, 'id':input.id, 'url':ajaxurl},
      {'doOnSuccess': function(json) {
        jQuery('#searchDilutionResult').css('visibility', 'visible');
        jQuery('#searchDilutionResult').html(json.html);
      }
      }
    );
  },

  poolSearchEmPcrDilution : function(input, platform) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'poolSearchEmPcrDilution',
      {'str':jQuery(input).val(), 'platform':platform, 'id':input.id, 'url':ajaxurl},
      {'doOnSuccess': function(json) {
        jQuery('#searchDilutionResult').css('visibility', 'visible');
        jQuery('#searchDilutionResult').html(json.html);
      }
      }
    );
  },

  poolSearchElements : function(input, platform) {
    Fluxion.doAjax(
      'poolSearchService',
      'poolSearchElements',
      {'str':jQuery(input).val(), 'platform':platform, 'id':input.id, 'url':ajaxurl},
      {'doOnSuccess': function(json) {
        jQuery('#searchElementsResult').css('visibility', 'visible');
        jQuery('#searchElementsResult').html(json.html);
        jQuery(input).blur(function() {
          jQuery('#searchElementsResult :first-child').hide();
        });
      }
      }
    );
  },

  poolSearchSelectElement : function(elementId, elementName) {
    if (jQuery("#element" + elementId).length > 0) {
      alert("Element " + elementName + " is already part of this pool.");
    }
    else {
      var div = "<div onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard'>";
      div += "<span class='float-left' id='element"+elementId+"'><input type='hidden' id='poolableElements" + elementId + "' value='" + elementName + "' name='poolableElements'/>";
      div += "<b>Element: " + elementName + "</b></span>";
      div += "<span onclick='Utils.ui.confirmRemove(jQuery(this).parent());' class='float-right ui-icon ui-icon-circle-close'></span></div>";
      jQuery('#dillist').append(div);
    }
    jQuery('#searchElementsResult').css('visibility', 'hidden');
  }
};

Pool.barcode = {
  printPoolBarcodes : function() {
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
  },

  selectPoolBarcodesToPrint : function(tableId) {
    if (!jQuery(tableId).hasClass("display")) {
      //destroy current table and recreate
      jQuery(tableId).dataTable().fnDestroy();
      //bug fix to reset table width
      jQuery(tableId).removeAttr("style");
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

      jQuery("div.toolbar").html("<button type='button' onclick=\"Pool.barcode.printSelectedPoolBarcodes('" + tableId + "');\" class=\"fg-button ui-state-default ui-corner-all\">Print Selected</button>");
      jQuery("div.toolbar").append("<button onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\">Cancel</button>");
      jQuery("div.toolbar").removeClass("toolbar");
    }
  },

  printSelectedPoolBarcodes : function(tableId) {
    var pools = [];
    var table = jQuery(tableId).dataTable();
    var nodes = DatatableUtils.fnGetSelected(table);
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
                  {'doOnSuccess':function (json) {
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
  }
};