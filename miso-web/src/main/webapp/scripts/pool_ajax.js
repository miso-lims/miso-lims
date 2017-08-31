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

var Pool = Pool || {
  deletePool : function(poolId, successfunc) {
    if (confirm("Are you sure you really want to delete pool " + poolId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'poolControllerHelperService',
        'deletePool',
        {
          'poolId':poolId,
          'url':ajaxurl
        },
        {
          'doOnSuccess':function() {
            successfunc();
          }
        }
      );
    }
  },

  validatePool: function () {
    Validate.cleanFields('#pool-form');
    jQuery('#pool-form').parsley().destroy();

    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-required', 'true');
    jQuery('#alias').attr('data-parsley-maxlength', '100');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-maxlength', '255');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);

    // Platform Type input select validation
    jQuery('#platformType').attr('class', 'form-control');
    jQuery('#platformType').attr('required', 'true');
    jQuery('#sampleTypes').attr('data-parsley-error-message', 'You must select a Platform');

    // Concentration input field validation
    jQuery('#concentration').attr('class', 'form-control');
    jQuery('#concentration').attr('data-parsley-required', 'true');
    jQuery('#concentration').attr('data-parsley-maxlength', '10');
    jQuery('#concentration').attr('data-parsley-type', 'number');

    // Creation Date input field validation
    jQuery('#creationDate').attr('class', 'form-control');
    jQuery('#creationDate').attr('required', 'true');
    jQuery('#creationDate').attr('data-parsley-pattern', Utils.validation.dateRegex);
    jQuery('#creationDate').attr('data-date-format', 'YYYY-MM-DD');
    jQuery('#creationDate').attr('data-parsley-error-message', 'Date must be of form YYYY-MM-DD');

    // Volume validation
    jQuery('#volume').attr('class', 'form-control');
    jQuery('#volume').attr('data-parsley-maxlength', '10');
    jQuery('#volume').attr('data-parsley-type', 'number');

    jQuery('#pool-form').parsley();
    jQuery('#pool-form').parsley().validate();

    Validate.updateWarningOrSubmit('#pool-form');
    return false;
  }
};

Pool.qc = {
  insertPoolQCRow : function(poolId, includeId) {
    if (!jQuery('#poolQcTable').attr("qcInProgress")) {
      jQuery('#poolQcTable').attr("qcInProgress", "true");

      jQuery('#poolQcTable')[0].insertRow(1);
      //QCId  QCed By  	QC Date  	Method  	Results

      if (includeId) {
        var column1 = jQuery('#poolQcTable')[0].rows[1].insertCell(-1);
        column1.innerHTML = "<input type='hidden' id='poolId' name='poolId' value='" + poolId + "'/>";
      }
      var column2 = jQuery('#poolQcTable')[0].rows[1].insertCell(-1);
      column2.innerHTML = "<input id='poolQcUser' name='poolQcUser' type='hidden' value='" + jQuery('#currentUser')[0].innerHTML + "'/>" + jQuery('#currentUser')[0].innerHTML;
      var column3 = jQuery('#poolQcTable')[0].rows[1].insertCell(-1);
      column3.innerHTML = "<input id='poolQcDate' name='poolQcDate' type='text'/>";
      var column4 = jQuery('#poolQcTable')[0].rows[1].insertCell(-1);
      column4.innerHTML = "<select id='poolQcType' name='poolQcType' onchange='Pool.qc.changePoolQcUnits(this);'/>";
      var column5 = jQuery('#poolQcTable')[0].rows[1].insertCell(-1);
      column5.innerHTML = "<input id='poolQcResults' name='poolQcResults' type='text'/><span id='units'/>";
      var column6 = jQuery('#poolQcTable')[0].rows[1].insertCell(-1);
      column6.innerHTML = "<a href='javascript:void(0);' onclick='Pool.qc.addPoolQC();'/>Add</a>";

      jQuery("#poolQcDate").val(jQuery.datepicker.formatDate(Utils.ui.goodDateFormat, new Date()));
      Utils.ui.addMaxDatePicker("poolQcDate", 0);

      Fluxion.doAjax(
        'poolControllerHelperService',
        'getPoolQcTypes',
        {
          'url':ajaxurl
          },
        {
          'doOnSuccess':function(json) {
            jQuery('#poolQcType').html(json.types);
            jQuery('#units').html(jQuery('#poolQcType option:first').attr("units"));
          }
        }
      );
    } else {
      alert("Cannot add another QC when one is already in progress.");
    }
  },

  changePoolQcUnits : function() {
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
      {
        'updateElement':'poolQcTable',
        'doOnSuccess':function() {
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
      {
        'doOnSuccess':function(json) {
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
      {
        'doOnSuccess':Utils.page.pageReload
      }
    );
  }
};

Pool.ui = {
  selectElementsByBarcodes : function(codes) {
    if (codes === "") {
      alert("Please input at least one barcode...");
    } else {
      Fluxion.doAjax(
        'poolControllerHelperService',
        'selectElementsByBarcodeList',
        {
          'barcodes':codes,
          'url':ajaxurl
        },
        {
          'updateElement':'importlist'
        }
      );
    }
  },

  dilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {
        'url':ajaxurl
      },
      {
        'statusElement':'statusdiv',
        'progressElement':'trash',
        'doOnSuccess':self.dilutionFileUploadSuccessFunc
      },
      {'':''}
    );
  },

  dilutionFileUploadSuccessFunc : function() {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'selectDilutionsByBarcodeFile',
      {
        'url':ajaxurl
      },
      {
        'updateElement':'dilimportfile'
      }
    );
  },

  /** Deprecated */
  libraryDilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {
        'url':ajaxurl
      },
      {
        'statusElement':'statusdiv',
        'progressElement':'trash',
        'doOnSuccess':self.libraryDilutionFileUploadSuccessFunc
      },
      {'':''}
    );
  },

  /** Deprecated */
  libraryDilutionFileUploadSuccessFunc : function() {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'selectLibraryDilutionsByBarcodeFile',
      {
        'url':ajaxurl
      },
      {
        'updateElement':'dilimportfile'
      }
    );
  },

  /** Deprecated */
  ls454EmPcrDilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {
        'url':ajaxurl
      },
      {
        'statusElement':'statusdiv',
        'progressElement':'trash',
        'doOnSuccess':self.ls454EmPcrDilutionFileUploadSuccessFunc
      },
      {'':''}
    );
  },

  /** Deprecated */
  ls454EmPcrDilutionFileUploadSuccessFunc : function() {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'select454EmPCRDilutionsByBarcodeFile',
      {
        'url':ajaxurl
      },
      {
        'updateElement':'dilimportfile'
      }
    );
  },

  /** Deprecated */
  solidEmPcrDilutionFileUploadProgress : function() {
    var self = this;
    Fluxion.doAjaxUpload(
      'ajax_upload_form',
      'fileUploadProgressBean',
      'checkUploadStatus',
      {
        'url':ajaxurl
      },
      {
        'statusElement':'statusdiv',
        'progressElement':'trash',
        'doOnSuccess':self.solidEmPcrDilutionFileUploadSuccessFunc
      },
      {'':''}
    );
  },

  /** Deprecated */
  solidEmPcrDilutionFileUploadSuccessFunc : function() {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'selectSolidEmPCRDilutionsByBarcodeFile',
      {
        'url':ajaxurl
      },
      {
        'updateElement':'dilimportfile'
      }
    );
  },

  createListingPoolsTablePlatform: function (platform, poolConcentrationUnits) {
    var table = 'listing' + platform + 'PoolsTable';
    // This URL has to be singular because of mapping in PoolRestController
    Pool.ui.createListingPoolsTable(table, poolConcentrationUnits, "/miso/rest/pool/dt/platform/" + platform);
  },
  createListingPoolsTable: function(table, poolConcentrationUnits, url) {
    jQuery('#'+table).html('');

    jQuery('#'+table).dataTable(Utils.setSortFromPriority({
      "aoColumns": [
        {
          "sTitle": "Name",
          "mData": "id",
          "mRender": function (data, type, full) {
            return "<a href=\"/miso/pool/" + data + "\">" + full.name + "</a>";
          },
          "iSortPriority" : 1
        },
        {
          "sTitle": "Alias",
          "mData": "alias",
          "mRender": function (data, type, full) {
            return "<a href=\"/miso/pool/" + full.id + "\">" + data + "</a>";
          },
          "iSortPriority" : 0
        },
        {
          "sTitle": "Description",
          "mData": "description",
          "iSortPriority": 0
        },
        {
          "sTitle": "Date Created",
          "mData": "creationDate",
          "iSortPriority" : 0
        },
        {
          "sTitle": "Conc. (" + poolConcentrationUnits + ")",
          "mData": "concentration",
          "iSortPriority" : 0
        },
        {
          "sTitle": "Location",
          "mData": "locationLabel",
          "bSortable": false,
          "mRender": function (data, type, full) {
            return full.boxId ? "<a href='/miso/box/" + full.boxId + "'>" + data + "</a>" : data;
          },
          "iSortPriority" : 0
        },
        {
          "sTitle": "Last Updated",
          "mData": "lastModified",
          "bVisible": (Sample.detailedSample ? "true" : "false"),
          "iSortPriority" : 2
        },
        {
          "sTitle": "Barcode",
          "mData": "identificationBarcode",
          "bVisible": false,
          "iSortPriority" : 0
        }
      ],
      "bJQueryUI": true,
      "bAutoWidth": false,
      "iDisplayLength": 25,
      "iDisplayStart": 0,
      "sDom": '<l<"#toolbar">f>r<t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
      "sPaginationType": "full_numbers",
      "bProcessing": true,
      "bServerSide": true,
      "sAjaxSource": url,
      "fnServerData": function (sSource, aoData, fnCallback) {
        jQuery('#'+table).addClass('disabled');
        jQuery.ajax({
          "dataType": "json",
          "type": "GET",
          "url": sSource,
          "data": aoData,
          "success": fnCallback // do not alter this DataTables property
        });
      },
      "fnDrawCallback": function (oSettings) {
        jQuery('#'+table).removeClass('disabled');
        jQuery('#'+table+'_paginate').find('.fg-button').removeClass('fg-button');
      }
    })).fnSetFilteringDelay();
  },

  showPoolNoteDialog: function (poolId) {
    var self = this;
    jQuery('#addNoteDialog')
      .html("<form>" +
        "<fieldset class='dialog'>" +
        "<label for='internalOnly'>Internal Only?</label>" +
        "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />" +
        "<br/>" +
        "<label for='notetext'>Text</label>" +
        "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' autofocus />" +
        "</fieldset></form>");

    jQuery('#addNoteDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function () {
          if (jQuery('#notetext').val().length > 0) {
            self.addPoolNote(poolId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
            jQuery(this).dialog('close');
          } else {
            jQuery('#notetext').focus();
          }
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  addPoolNote: function (poolId, internalOnly, text) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'addPoolNote',
      {
        'poolId': poolId,
        'internalOnly': internalOnly,
        'text': text,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  deletePoolNote: function (poolId, noteId) {
    var deleteIt = function() {
      Fluxion.doAjax(
        'poolControllerHelperService',
        'deletePoolNote',
        {
          'poolId': poolId,
          'noteId': noteId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': Utils.page.pageReload
        }
      );
    }
    Utils.showConfirmDialog('Delete Note', 'Delete',
      ["Are you sure you want to delete this note?"],
      deleteIt
    );
  }
};

Pool.search = {
  poolSearchExperiments : function(input, platform) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'poolSearchExperiments',
      {
        'str':jQuery(input).val(),
        'platform':platform,
        'id':input.id,
        'url':ajaxurl
      },
      {
        'doOnSuccess': function(json) {
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
    } else {
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
      {
        'str':jQuery(input).val(),
        'platform':platform,
        'id':input.id,
        'url':ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          jQuery('#searchDilutionResult').css('visibility', 'visible');
          jQuery('#searchDilutionResult').html(json.html);
        }
      }
    );
  }
};

Pool.barcode = {
  editPoolIdBarcode: function (span, id) {
    var v = span.find('a').text();
    if (v && v !== "") {
      span.html("<input type='text' value='" + v + "' name='identificationBarcode' id='identificationBarcode'>");
    }
  },

  showPoolIdBarcodeChangeDialog: function (poolId, poolIdBarcode) {
    var self = this;
    jQuery('#changeIdBarcodeDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<strong><label>Current Barcode: </label></strong><span id='idBarcodeCurrent'>" + poolIdBarcode +
            "</span><br /><strong><label for='notetext'>New Barcode:</label></strong>" +
            "<input type='text' name='idBarcodeInput' id='idBarcodeInput' class='text ui-widget-content ui-corner-all' />" +
            "</fieldset></form>");

    jQuery('#changeIdBarcodeDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Save": function () {
          self.changePoolIdBarcode(poolId, jQuery('#idBarcodeInput').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  changePoolIdBarcode: function (poolId, idBarcode) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'changePoolIdBarcode',
      {
        'poolId': poolId,
        'identificationBarcode': idBarcode,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },
};

Pool.orders = Pool.orders || {
  'showDialog': function() {
     Pool.orders.dialog = jQuery( "#order-dialog" ).dialog({
        autoOpen: true,
        height: 400,
        width: 350,
        modal: true,
        buttons: {
          "Save": function() {
            var method;
            var url;
            var order = { 'poolId': Pool.orders.poolId, 'partitions': document.getElementById('orderPartitions').value, 'parameters': { 'id': document.getElementById('orderParameterId').value } };
            if (Pool.orders.editingOrderId == 0) {
              method = 'POST';
              url = '/miso/rest/poolorder';
            } else {
              method = 'PUT';
              url = '/miso/rest/poolorder/' + Pool.orders.editingOrderId;
            }
            jQuery.ajax({
              'type': method,
              'url': url,
              'contentType': "application/json; charset=utf-8",
              'data': JSON.stringify(order)
            }).done(function() {
              Pool.orders.makeTable(Pool.orders.poolId);
              Pool.orders.dialog.dialog("close");
            });
          },
          "Cancel": function() {
            Pool.orders.dialog.dialog( "close" );
          }
        }
      });
    },
	'makeTable': function(poolId) {
      if (poolId == 0) return;
      if (Pool.orders.table) {
        Pool.orders.table.fnDestroy();
        jQuery('#order-completion-table').dataTable().fnDestroy();
      }
      Pool.orders.poolId = poolId;
      Pool.orders.table = jQuery('#edit-order-table').dataTable({
      "aoColumns": [
        {
          "sTitle": "Platform",
          "mData": "parameters.platform.instrumentModel"
        },
        {
          "sTitle": "Sequencing Parameters",
          "mData": "parameters.id",
          "mRender": function (data, type, full) {
            return Utils.array.maybeGetProperty(
              Utils.array.findFirstOrNull(Utils.array.idPredicate(data), Constants.sequencingParameters),
              'name');
          }
        },
        {
          "sTitle": (!document.getElementById('partitionName') ? "Partitions" : document.getElementById('partitionName').innerHTML),
          "mData": "partitions"
        },
        {
          "sTitle": "Edit",
          "mData": "id",
          "mRender": function (data, type, full) {
            return '<span onclick=\'Pool.orders.editOrder(' + JSON.stringify(full) + ')\' class="ui-icon ui-icon-pencil"></span>';
          }
        },
        {
          "sTitle": "Remove",
          "mData": "id",
          "mRender": function (data, type, full) {
            return '<span onclick="Pool.orders.removeOrder(' + data + ')" class="ui-icon ui-icon-circle-close"></span>';
          }
        }
      ],
      "bJQueryUI": true,
      "bAutoWidth": false,
      "iDisplayLength": 25,
      "iDisplayStart": 0,
      "sPaginationType": "full_numbers",
      "bProcessing": true,
      "sAjaxSource": '/miso/rest/pool/' + poolId + '/orders',
      "fnServerData": function (sSource, aoData, fnCallback) {
        jQuery('#edit-order-table').addClass('disabled');
        jQuery.ajax({
          "dataType": "json",
          "url": sSource,
          "data": aoData,
          "success": function(x) {
            fnCallback ({ 'aaData': x } );
          }
        });
      },
      "fnDrawCallback": function (oSettings) {
        jQuery('#edit-order-table').removeClass('disabled');
        jQuery('#edit-order-table').find('.fg-button').removeClass('fg-button');
      }
    });
    ListUtils.createTable('order-completion-table', ListTarget.completion, null, { "poolId" : poolId });
  },

  'setOptionsForPlatform': function(platformId, selectedParameterId) {
	document.getElementById('orderPlatformId').value = platformId;
    var options = Constants.sequencingParameters.filter(function(parameter) {
      return parameter.platform.id == platformId;
    }).sort(function(a, b) {
         return a.name < b.name ? -1 : (a.name == b.name ? 0 : 1);
       }).map(function(parameter) {
         return '<option value="' + parameter.id + '"' + ((parameter.id == selectedParameterId) ? " selected" : "") + '>' + parameter.name + '</option>';
       }).join('');
    document.getElementById('orderParameterId').innerHTML = options;
  },

  'changePlatform': function() {
    var platformId = document.getElementById('orderPlatformId').value;
     Pool.orders.setOptionsForPlatform(platformId, null);
  },

  'createOrder': function(order) {
     document.getElementById('orderPartitions').value = 1;
     Pool.orders.setOptionsForPlatform(Defaults.all.platforms[0].id, null);
     Pool.orders.editingOrderId = 0;
     Pool.orders.showDialog();
  },

  'editOrder': function(order) {
     document.getElementById('orderPartitions').value = order.partitions;
     Pool.orders.setOptionsForPlatform(order.parameters.platform.id, order.parameters.id);
     document.getElementById('orderParameterId').value = order.parameters.id;
     Pool.orders.editingOrderId = order.id;
     Pool.orders.showDialog();
  },

  'removeOrder': function(orderId) {
    if (confirm('Delete this order?')) {
        jQuery.ajax({
          "type": "DELETE",
          "url": '/miso/rest/poolorder/' + orderId,
        }).done(function() {
          Pool.orders.makeTable(Pool.orders.poolId);
        });
    }
    return false;
  }
};
