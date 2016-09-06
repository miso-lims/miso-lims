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
    jQuery('#description').attr('data-parsley-required', 'true');
    jQuery('#description').attr('data-parsley-maxlength', '100');
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
    jQuery('#creationDate').attr('data-date-format', 'DD/MM/YYYY');
    jQuery('#creationDate').attr('data-parsley-error-message', 'Date must be of form DD/MM/YYYY');
    
    // Volume validation
    jQuery('#volume').attr('class', 'form-control');
    jQuery('#volume').attr('data-parsley-required', 'true');
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

Pool.wizard = {
  insertPoolQCRow : function() {
    if (!jQuery('#poolQcTable').attr("qcInProgress")) {
      jQuery('#poolQcTable').attr("qcInProgress", "true");

      jQuery('#poolQcTable')[0].insertRow(1);
      var column3 = jQuery('#poolQcTable')[0].rows[1].insertCell(-1);
      column3.innerHTML = "<input id='poolQcDate' name='poolQcDate' type='text'/>";
      var column4 = jQuery('#poolQcTable')[0].rows[1].insertCell(-1);
      column4.innerHTML = "<select id='poolQcType' name='poolQcType' onchange='Pool.qc.changePoolQcUnits(this);'/>";
      var column5 = jQuery('#poolQcTable')[0].rows[1].insertCell(-1);
      column5.innerHTML = "<input id='poolQcResults' name='poolQcResults' type='text'/><span id='units'/>";
      var column6 = jQuery('#poolQcTable')[0].rows[1].insertCell(-1);
      column6.innerHTML = "<a href='javascript:void(0);' onclick='Pool.wizard.addPoolQC(this);'/>Add</a>";

      jQuery("#poolQcDate").val(jQuery.datepicker.formatDate('dd/mm/yy', new Date()));
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
            jQuery('#average' + i).html(val);
          });
        }
      }
    );
  },

  createListingPoolsTable: function (platform, poolConcentrationUnits) {
    var table = 'listing' + platform + 'PoolsTable';
    jQuery('#'+table).html('');

    function renderPoolElements (data, type, full) {
      var elements = data.map(function (ld) {
        return "<li><a href=\"/miso/library/" + ld.library.id + "\">" + ld.library.alias
        + (ld.library.index1Label ? "(" + ld.library.index1Label + (ld.library.index2Label ? ", " + ld.library.index2Label + ")" : ")") : "") 
        + "</a>" + "</li>";
      });
      var string;
      if (elements.length === 0) {
        return "No elements";
      } else {
        var selector = "more_" + full.id;
        var num = "" + elements.length + " dilutions  ";
        var more = "<span id=\"" + selector + "_fewer\"><a href=\"javascript:void(0);\" onclick=\"jQuery('." + selector + "').show();jQuery('#" + selector + "_fewer').hide();\">"
          + "(See all...)</a></span>";
        var els = "<div class='" + selector + "' style='display:none'><ul>" 
          + elements.join('')
          + "</ul><span><a href=\"javascript:void(0);\" onclick=\"jQuery('." + selector + "').hide();jQuery('#" + selector + "_fewer').show();\">Hide all...</a></span></div>";
        return num + more + els;
      }
    };
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
          "sTitle": "Elements",
          "mData": "pooledElements",
          "mRender": renderPoolElements,
          "bSortable": false,
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
          "iSortPriority" : 0
        },
        {
          "sTitle": "Last Updated",
          "mData": "lastModified",
          "bVisible": (Sample.detailedSample ? "true" : "false"),
          "iSortPriority" : 2
        },
        {
          "sTitle": "ID",
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
      "sAjaxSource": "/miso/rest/pool/dt/platform/" + platform, // has to be singular because of mapping in PoolRestController
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

  getPoolableElementInfo : function(poolId, elementId) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'getPoolableElementInfo',
      {
        'poolId':poolId,
        'elementId':elementId,
        'url':ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          jQuery('#element'+elementId).append(json.info);
        }
      }
    );
  },

  createElementSelectDatatable : function(platform, libraryDilutionConcentrationUnits) {
    jQuery('#elementSelectDatatableDiv').html("<table cellpadding='0' width='100%' cellspacing='0' border='0' class='display' id='elementSelectDatatable'></table>");
    jQuery('#elementSelectDatatable').html("<img src='/styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
      'poolControllerHelperService',
      'createElementSelectDataTable',
      {
        'url':ajaxurl,
        'platform':platform
      },
      {
        'doOnSuccess': function(json) {
          jQuery('#elementSelectDatatable').html('');
          jQuery('#elementSelectDatatable').dataTable({
            "aaData": json.poolelements,
            "aoColumns": [
              { "sTitle": "Dilution Name", "sType":"natural"},
              { "sTitle": "Concentration ("+libraryDilutionConcentrationUnits+")", "sType":"natural"},
              { "sTitle": "Library", "sType":"natural"},
              { "sTitle": "Sample", "sType":"natural"},
              { "sTitle": "Indices", "sType":"natural"},
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
  },

  showPoolNoteDialog: function (poolId) {
    var self = this;
    jQuery('#addPoolNoteDialog')
      .html("<form>" +
        "<fieldset class='dialog'>" +
        "<label for='internalOnly'>Internal Only?</label>" +
        "<input type='checkbox' checked='checked' name='internalOnly' id='internalOnly' class='text ui-widget-content ui-corner-all' />" +
        "<br/>" +
        "<label for='notetext'>Text</label>" +
        "<input type='text' name='notetext' id='notetext' class='text ui-widget-content ui-corner-all' />" +
        "</fieldset></form>");

    jQuery('#addPoolNoteDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Add Note": function () {
          self.addPoolNote(poolId, jQuery('#internalOnly').val(), jQuery('#notetext').val());
          jQuery(this).dialog('close');
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
    if (confirm("Are you sure you want to delete this note?")) {
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
  },

  poolSearchEmPcrDilution : function(input, platform) {
    Fluxion.doAjax(
      'poolControllerHelperService',
      'poolSearchEmPcrDilution',
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
  },

  poolSearchElements : function(input, platform) {
    Fluxion.doAjax(
      'poolSearchService',
      'poolSearchElements',
      {
        'str':jQuery(input).val(),
        'platform':platform,
        'id':input.id,
        'url':ajaxurl
      },
      {
        'doOnSuccess': function(json) {
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
    } else {
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
  editPoolIdBarcode: function (span, id) {
    Fluxion.doAjax(
      'loggedActionService',
      'logAction',
      {
        'objectId': id,
        'objectType': 'Pool',
        'action': 'editPoolIdBarcode',
        'url': ajaxurl
      },
      {}
    );

    var v = span.find('a').text();
    if (v && v !== "") {
      span.html("<input type='text' value='" + v + "' name='identificationBarcode' id='identificationBarcode'>");
    }
  },

  showPoolIdBarcodeChangeDialog: function (poolId, poolIdBarcode) {
    var self = this;
    jQuery('#changePoolIdBarcodeDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<strong><label>Current Barcode: </label></strong>" + poolIdBarcode +
            "<br /><strong><label for='notetext'>New Barcode:</label></strong>" +
            "<input type='text' name='idBarcodeInput' id='idBarcodeInput' class='text ui-widget-content ui-corner-all' />" +
            "</fieldset></form>");

    jQuery('#changePoolIdBarcodeDialog').dialog({
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
        } else {
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

          jQuery('#printServiceSelectDialog').dialog({
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
        },
        'doOnError':function (json) {
          alert(json.error);
        }
      }
    );
  }
};

Pool.orders = Pool.orders || {
  'makeXhrRequest': function (method, endpoint, callback, data, callbackarg) {
    var expectedStatus;
    var unauthorizedStatus = 401;
    if (method == 'POST') {
      expectedStatus = [201, 200];
    } else {
      expectedStatus = [200];
    }
    var xhr = new XMLHttpRequest();
    xhr.open(method, endpoint);
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (expectedStatus.indexOf(xhr.status) != -1) {
          if (!callback) {
            document.location.reload();
          } else {
            data ? ( callbackarg ? callback(callbackarg) : callback() ) : callback(xhr) ;
          }
        } else if (xhr.status === unauthorizedStatus) {
          alert("You are not authorized to view this page.");
        } else {
          alert("Sorry, something went wrong. Please try again. If the issue persists, contact your administrator.");
        }
      }
    };
    xhr.setRequestHeader('Content-Type', 'application/json');
    data ? xhr.send(data) : xhr.send();
  },

  'addOrder': function(poolId) {
    Pool.orders.makeXhrRequest('POST', '/miso/rest/poolorder', Pool.orders.loadOrders, JSON.stringify({ 'poolId': poolId, 'partitions': document.getElementById('newOrderParitions').value, 'parameters': { 'id': document.getElementById('newOrderParameterId').value } }), poolId);
    return false;
  },

  'loadOrders': function(poolId) {
    Pool.orders.makeXhrRequest('GET', '/miso/rest/pool/' + poolId + '/orders', function(xhr) {
      var orders = JSON.parse(xhr.responseText);
      document.getElementById('orderlist').innerHTML = orders.map(function(order) {
        var platformOptions = Defaults.all.platforms.map(function(platform) {
          return '<option value="' + platform.id + '"' + ((platform.id == order.parameters.platformId) ? " selected" : "") + '>' + platform.nameAndModel + '</option>';
        }).join('');
        var parameterOptions = Pool.orders.optionsForPlatform(function(parameter) {
          return parameter.platformId == order.parameters.platformId;
        }, order.parameters.id);
        return ('<div id="order' + order.id + '" onmouseover="this.className=\'dashboardhighlight\'" onmouseout="this.className=\'dashboard\'" class="dashboard">' +
          '<span class="float-left">' +
          '<b>Partitions:</b> <input type="text" id="partitions' + order.id + '" value="' + order.partitions + '"/><br/>' +
          '<b>Platform:</b> <select id="platforms' + order.id + '" onchange="Pool.orders.changePlatform(' + order.id + ')">' + platformOptions + '</select><br/>' +
          '<b>Sequencing Parameters:</b> <select id="parameters' + order.id + '">' + parameterOptions + '</select><br/>' +
          '<input type="submit" class="br-button ui-state-default ui-corner-all" onclick="return Pool.orders.saveOrder(' + order.id + ', ' + poolId + ')" value="Save" /></span>' +
          '<span onclick="Pool.orders.removeOrder(' + order.id + ', ' + poolId + ');" class="float-right ui-icon ui-icon-circle-close"></span>' +
          '</div>');
      }).join('');
    });
  },

  'optionsForPlatform' : function(filterCallback, selectedParameterId) {
    var options = Defaults.all.sequencingParameters.filter(filterCallback).sort(function(a, b) {
         return a.name < b.name ? -1 : (a.name == b.name ? 0 : 1);
       }).map(function(parameter) {
         return '<option value="' + parameter.id + '"' + ((parameter.id == selectedParameterId) ? " selected" : "") + '>' + parameter.name + '</option>';
       }).join('');
    if (options == '') {
      return '<option value="-1" selected>Default</option>';
    }
    return options;
  },

  'changePlatform' : function(orderId) {
    var platformId = document.getElementById(orderId == null ? 'newOrderPlatformId' : ('platforms' + orderId)).value;
    document.getElementById(orderId == null ? 'newOrderParameterId' : ('parameters' + orderId)).innerHTML = Pool.orders.optionsForPlatform(function(parameter) {
      return parameter.platformId == platformId;
    }, null);
  },

  'saveOrder': function(orderId, poolId) {
    Pool.orders.makeXhrRequest('PUT', '/miso/rest/poolorder/' + orderId, function() { Pool.orders.loadOrders(poolId); }, JSON.stringify({ 'partitions': document.getElementById('partitions' + orderId).value, 'parameters': { 'id': document.getElementById('parameters' + orderId).value } }));
    return false;
  },

  'removeOrder': function(orderId, poolId) {
    if (confirm('Delete this order?')) {
      Pool.orders.makeXhrRequest('DELETE', '/miso/rest/poolorder/' + orderId, function() { Pool.orders.loadOrders(poolId); }, null);
    }
    return false;
  }
};
