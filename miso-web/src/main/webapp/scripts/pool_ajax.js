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
    jQuery('#creationDate').attr('data-date-format', 'DD/MM/YYYY');
    jQuery('#creationDate').attr('data-parsley-error-message', 'Date must be of form DD/MM/YYYY');
    
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

  createElementSelectDatatable : function(platform, poolId, libraryDilutionConcentrationUnits) {
    jQuery('#elementSelectDatatableDiv').html("<table cellpadding='0' width='100%' cellspacing='0' border='0' class='display' id='elementSelectDatatable'></table>");
    jQuery('#elementSelectDatatable').html("<img src='/styles/images/ajax-loader.gif'/>");
    Fluxion.doAjax(
      'poolControllerHelperService',
      'createElementSelectDataTable',
      {
        'url':ajaxurl,
        'poolId':poolId,
        'platform':platform
      },
      {
        'doOnSuccess': function(json) {
          jQuery('#elementSelectDatatable').html('');
          jQuery('#elementSelectDatatable').dataTable({
            "aaData": json.poolelements,
            "aoColumns": [
              { "sTitle": "Dilution Name", "sType":"natural"},
              { "sTitle": "Conc. ("+libraryDilutionConcentrationUnits+")", "sType":"natural"},
              { "sTitle": "Library", "sType":"natural"},
              { "sTitle": "Sample", "sType":"natural"},
              { "sTitle": "Indices", "sType":"natural"},
              { "sTitle": "Low Quality", "bSortable": false},
              { "sTitle": "Add"}
            ],
            "bJQueryUI": true,
            "iDisplayLength":  25,
            "sPaginationType": "full_numbers",
            "aaSorting":[
              [0,"desc"]
            ],
            "fnRowCallback": function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
              jQuery(nRow).attr("id", "poolable_" + aData[0]);
              if (jQuery('#pooled_' + aData[0]).length) {
                jQuery('td:eq(6)', nRow).addClass('disabled');
                jQuery('td:eq(6)', nRow).prop('disabled', true);
                jQuery('td:eq(6)', nRow).css('cursor', 'default');
              } else {
                jQuery('td:eq(6)', nRow).css('cursor', 'pointer');
              }
            },
            "fnDrawCallback": function () {
              jQuery('#elementSelectDatatable_paginate').find('.fg-button').removeClass('fg-button');
            } 
          });
        }
      }
    );
  },

  removePooledElement : function (poolId, dilutionId, elementName) {
    var extra = (jQuery('#pooledElementsDatatable').dataTable().fnGetData().length == 1) ? '\n\nDeleting this item would make the pool empty.' : '';
    if (confirm("Are you sure you want to remove " + elementName + " from this pool?" + extra)) {
      Fluxion.doAjax(
        'poolControllerHelperService',
        'removePooledElement',
        {
          'poolId':poolId,
          'dilutionId':dilutionId,
          'url':ajaxurl
        },
        {
          'doOnSuccess': function() {
            function findByName (arrayElement, index, array) {
              return arrayElement[0] == elementName;
            }
            var indexToDelete = jQuery('#pooledElementsDatatable').dataTable().fnGetData().findIndex(findByName);
            // remove it from the Selected element(s) table
            jQuery('#pooledElementsDatatable').dataTable().fnDeleteRow(indexToDelete);
            // re-enable the Add button on Select poolable elements table if it's been disabled
            if (jQuery('#poolable_' + elementName).length && jQuery('#poolable_' + elementName).children().last().hasClass('disabled')) {
              jQuery('#poolable_' + elementName).children().last().removeClass('disabled');
              jQuery('#poolable_' + elementName).children().last().prop('disabled', false);
              jQuery('#poolable_' + elementName).children().last().css('cursor', 'pointer');
            }
          }
        }
      );
    }
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

  poolSearchSelectElement : function(poolId, elementId, elementName) {
    if (jQuery("#pooled_" + elementName).length > 0) {
      alert("Element " + elementName + " is already part of this pool.");
      jQuery('#searchElementsResult').css('visibility', 'hidden');
    } else {
      function addElement () {
        var addToPooled = [];
        var poolable = jQuery('#poolable_' + elementName).clone();
        poolable.children().each(function (index, value) {
          addToPooled.push(jQuery(value).html());
        });
        addToPooled.pop();
        addToPooled.push('<span id="pooled_' + elementName + '" onclick="Pool.ui.removePooledElement(' + poolId + ', ' + elementId + ', \'' + elementName + '\');" class="ui-icon ui-button ui-icon-circle-close"></span>');
        jQuery('#pooledElementsDatatable').dataTable().fnAddData(addToPooled);
        jQuery('#searchElementsResult').css('visibility', 'hidden');
      }
      function disableAddAndFadeCheckmark (addRowId) {
        // add checkmark beside plus button then fade out
        var checkmark = '<div><img id="checkmark_' + addRowId + '" src="/styles/images/ok.png" height="25" width="25" /></div>';
        var addTd = jQuery('#' + addRowId).children().last();
        addTd.prop('disabled', true);
        addTd.css('float', 'left');
        addTd.children().last().append(checkmark);
        jQuery('#checkmark_' + addRowId).fadeOut("slow", function () {
          jQuery(this).parent().parent().parent().css('clear', 'both');
          jQuery(this).parent().remove();
          addTd.addClass('disabled');
          addTd.css('cursor', 'default');
        });
      }
      Fluxion.doAjax(
        'poolControllerHelperService',
        'addPoolableElement',
        {
          'poolId':poolId,
          'dilutionId':elementId,
          'url':ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            // add row to Pooled elements table
            addElement();
            // add success checkmark and disable the 'Add' td in Select poolable elements table
            var tableRowId = 'poolable_' + elementName;
            disableAddAndFadeCheckmark(tableRowId);
          }
        }
      );
    }
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
      }
      if (Pool.orders.completionTable) {
        Pool.orders.completionTable.fnDestroy();
      }
      Pool.orders.poolId = poolId;
      Pool.orders.table = jQuery('#edit-order-table').dataTable({
      "aoColumns": [
        {
          "sTitle": "Platform",
          "mData": "parameters.platformId",
          "mRender": function (data, type, full) {
            return Hot.maybeGetProperty(
              Hot.findFirstOrNull(Hot.idPredicate(data), Defaults.all.platforms),
              'nameAndModel');
          }

        },
        {
          "sTitle": "Sequencing Parameters",
          "mData": "parameters.id",
          "mRender": function (data, type, full) {
            return Hot.maybeGetProperty(
              Hot.findFirstOrNull(Hot.idPredicate(data), Defaults.all.sequencingParameters),
              'name');
          }
        },
        {
          "sTitle": "Partitions",
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
    jQuery.ajax({
      'url': '/miso/rest/pool/' + poolId + '/completions',
      'dataType': "json"
    }).done(function(data) {
      Pool.orders.completionTable = jQuery('#order-completion-table').dataTable({
        "aoColumns":
          [
            {
              "sTitle": "Platform",
              "mData": "parametersId",
              "mRender": function (data, type, full) {
                var platformId = Hot.maybeGetProperty(
                  Hot.findFirstOrNull(Hot.idPredicate(data), Defaults.all.sequencingParameters),
                  'platformId');
                if (!platformId) {
                  return "N/A";
                }
                return Hot.maybeGetProperty(
                  Hot.findFirstOrNull(Hot.idPredicate(platformId), Defaults.all.platforms),
                  'nameAndModel');
              }
            },
            {
              "sTitle": "Sequencing Parameters",
              "mData": "parametersId",
              "mRender": function (data, type, full) {
                return Hot.maybeGetProperty(
                  Hot.findFirstOrNull(Hot.idPredicate(data), Defaults.all.sequencingParameters),
                  'name');
              }
            }
          ].concat(data.headings.map(function(heading) {
            return { "sTitle": heading, "mData": heading };
          })).concat([ { "sTitle": "Remaining", "mData": "Remaining" } ]),
        "bJQueryUI": true,
        "bAutoWidth": false,
        "iDisplayLength": 25,
        "iDisplayStart": 0,
        "sPaginationType": "full_numbers",
        "bProcessing": true,
        "aaData": data.completions,
        "fnDrawCallback": function (oSettings) {
          jQuery('#edit-order-table').removeClass('disabled');
          jQuery('#edit-order-table').find('.fg-button').removeClass('fg-button');
        }
      });
    });
  },

  'setOptionsForPlatform': function(platformId, selectedParameterId) {
	document.getElementById('orderPlatformId').value = platformId;
    var options = Defaults.all.sequencingParameters.filter(function(parameter) {
      return parameter.platformId == platformId;
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
     Pool.orders.setOptionsForPlatform(order.parameters.platformId, order.parameters.id);
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
