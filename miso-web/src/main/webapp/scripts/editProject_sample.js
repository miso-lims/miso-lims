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


/**
 * Created by bianx on 24/02/2014.
 */


function bulkSampleQcTable(tableName) {
  var sTable = jQuery(tableName);
  if (!sTable.hasClass("display")) {
    //destroy current table and recreate
    sTable.dataTable().fnDestroy();
    //bug fix to reset table width
    sTable.removeAttr("style");

    sTable.addClass("display");

    //remove edit and delete header and column
    jQuery(tableName + ' tr:first th:gt(5)').remove();
    jQuery(tableName + ' tr:first th:eq(4)').remove();

    var headers = ['rowsel',
                   'name',
                   'alias',
                   'description',
                   'sampleType',
                   'qcPassed',
                   'qcDate',
                   'qcType',
                   'results'];

    sTable.find("tr").each(function () {
      //remove rows where the sample QC has already passed
      if (jQuery(this).find("td:eq(5)").html() == "true") {
        jQuery(this).remove();
      }
      else {
        jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
        jQuery(this).find("td:eq(4)").remove();
        jQuery(this).find("td:gt(4)").remove();
        jQuery(this).find("td:eq(4)").addClass("passedCheck");
      }
    });

//headers
    jQuery(tableName + " tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\""+tableName+"\", this);'></span></th>");
    jQuery(tableName + ' tr:first th:eq(5)').html("QC Passed <span header='qcPassed' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\""+tableName+"\", this);'></span>");
    jQuery(tableName + " tr:first").append("<th>QC Date <span header='qcDate' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\""+tableName+"\", this);'></span></th>");
    jQuery(tableName + " tr:first").append("<th>QC Method <span header='qcType' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\""+tableName+"\", this);'></span></th>");
    jQuery(tableName + " tr:first").append("<th>Results</th>");

//columns
    jQuery(tableName + " tr:gt(0)").prepend("<td class='rowSelect'></td>");
    jQuery(tableName + " tr:gt(0)").append("<td class='dateSelect'></td>");
    jQuery(tableName + " tr:gt(0)").append("<td class='typeSelect'></td>");
    jQuery(tableName + " tr:gt(0)").append("<td class='defaultEditable'></td>");

    var datatable = sTable.dataTable({
      "aoColumnDefs": [
        {
          "bUseRendered": false,
          "aTargets": [ 0 ]
        }
      ],
      "aoColumns": [
        {"bSortable": false},
        { "sType": 'natural' },
        { "sType": 'natural' },
        { "sType": 'natural' },
        null,
        {"bSortable": false},
        {"bSortable": false},
        {"bSortable": false},
        {"bSortable": false}
      ],
      "bPaginate": false,
      "bInfo": false,
      "bJQueryUI": true,
      "bAutoWidth": true,
      "bSort": true,
      "bFilter": false,
      "sDom": '<<"toolbar">f>r<t>ip>'
    });

    sTable.find("tr:gt(0)").each(function () {
      for (var i = 0; i < this.cells.length; i++) {
        jQuery(this.cells[i]).attr("name", headers[i]);
      }
    });

    jQuery(tableName + ' .rowSelect').click(function () {
      if (jQuery(this).parent().hasClass('row_selected'))
        jQuery(this).parent().removeClass('row_selected');
      else if (!jQuery(this).parent().hasClass('row_saved'))
        jQuery(this).parent().addClass('row_selected');
    });

    //jQuery("div.toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
    jQuery("div.toolbar").html("<input type='button' value='Save QCs' id=\"bulkSampleQcButton\" onclick=\"Project.ui.saveBulkSampleQc('"+tableName+"');\" class=\"fg-button ui-state-default ui-corner-all\"/>");
    jQuery("div.toolbar").append("<input type='button' value='Cancel' onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\"/>");
    jQuery("div.toolbar").removeClass("toolbar");

    jQuery(tableName + ' .defaultEditable').editable(
      function (value, settings) {
        return value;
      },
      {
        callback: function (sValue, y) {
          var aPos = datatable.fnGetPosition(this);
          datatable.fnUpdate(sValue, aPos[0], aPos[1]);
        },
        submitdata: function (value, settings) {
          return {
            "row_id": this.parentNode.getAttribute('id'),
            "column": datatable.fnGetPosition(this)[2]
          };
        },
        onblur: 'submit',
        placeholder: '',
        height: '14px'
      }
    );

    jQuery(".typeSelect").editable(
      function (value, settings) {
        return value;
      },
      {
        data: sampleQcTypesString,
        type: 'select',
        onblur: 'submit',
        placeholder: '',
        style: 'inherit',
        callback: function (sValue, y) {
          var aPos = datatable.fnGetPosition(this);
          datatable.fnUpdate(sValue, aPos[0], aPos[1]);
        },
        submitdata: function (value, settings) {
          return {
            "row_id": this.parentNode.getAttribute('id'),
            "column": datatable.fnGetPosition(this)[2]
          };
        }
      }
    );

    jQuery(".dateSelect").editable(
      function (value, settings) {
        return value;
      },
      {
        type: 'datepicker',
        width: '100px',
        onblur: 'submit',
        placeholder: '',
        style: 'inherit',
        datepicker: {
          dateFormat: 'dd/mm/yy',
          showButtonPanel: true,
          maxDate: 0
        },
        callback: function (sValue, y) {
          var aPos = datatable.fnGetPosition(this);
          datatable.fnUpdate(sValue, aPos[0], aPos[1]);
        },
        submitdata: function (value, settings) {
          return {
            "row_id": this.parentNode.getAttribute('id'),
            "column": datatable.fnGetPosition(this)[2]
          };
        }
      }
    );

    jQuery(".passedCheck").editable(
      function (value, settings) {
        return value;
      },
      {
        //type : 'checkbox',
        type: 'qcradio',
        onblur: 'submit',
        placeholder: '',
        style: 'inherit',
        callback: function (sValue, y) {
          var aPos = datatable.fnGetPosition(this);
          datatable.fnUpdate(sValue, aPos[0], aPos[1]);
        },
        submitdata: function (value, settings) {
          return {
            "row_id": this.parentNode.getAttribute('id'),
            "column": datatable.fnGetPosition(this)[2]
          };
        }
            }
    );
  }
}

function generateSampleDeliveryForm(tableName, projectId) {
  var sTable = jQuery(tableName);
  if (!sTable.hasClass("display")) {
    //destroy current table and recreate
    sTable.dataTable().fnDestroy();
    //bug fix to reset table width
    sTable.removeAttr("style");
    sTable.addClass("display");

    //remove edit header and column
    jQuery(tableName + ' tr:first th:gt(4)').remove();

    var headers = ['rowsel',
                   'name',
                   'alias',
                   'description',
                   'sampleType',
                   'qcPassed'];

    sTable.find("tr").each(function () {
      jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
      jQuery(this).find("td:gt(4)").remove();
    });

//headers
    jQuery(tableName + " tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\""+tableName+"\", this);'></span></th>");
    jQuery(tableName + " tr:gt(0)").prepend("<td class='rowSelect'></td>");

    sTable.dataTable({
      "aoColumnDefs": [
        {
          "bUseRendered": false,
          "aTargets": [ 0 ]
        }
      ],
      "aoColumns": [
        {"bSortable": false},
        { "sType": 'natural' },
        { "sType": 'natural' },
        { "sType": 'natural' },
        null,
        { "sType": 'natural' }
      ],
      "bPaginate": false,
      "bInfo": false,
      "bJQueryUI": true,
      "bAutoWidth": true,
      "bFilter": false,
      "sDom": '<<"toolbar">f>r<t>ip>'
    });

    sTable.find("tr:gt(0)").each(function () {
      for (var i = 0; i < this.cells.length; i++) {
        jQuery(this.cells[i]).attr("name", headers[i]);
      }
    });

    jQuery(tableName + ' .rowSelect').click(function () {
      if (jQuery(this).parent().hasClass('row_selected'))
        jQuery(this).parent().removeClass('row_selected');
      else if (!jQuery(this).parent().hasClass('row_saved'))
        jQuery(this).parent().addClass('row_selected');
    });

    jQuery("div.toolbar").html("Plate: <input type='radio' name='plateinformationform' value='yes'/>Yes |<input type='radio' name='plateinformationform' value='no' checked='checked'/>No " + "<button type='button' onclick=\"Project.ui.processSampleDeliveryForm('"+tableName+"', " + projectId + ");\" class=\"fg-button ui-state-default ui-corner-all\">Generate Form</button>");
  }
}

function getBulkSampleInputForm(projectId) {
  jQuery('#getBulkSampleInputFormDialog')
          .html("<form>" +
                "<fieldset class='dialog'>" +
                "<p>Select desired document format</p><br/>" +
                "<label for='formTypeOds'>OpenOffice (ODS)</label>" +
                "<input type='radio' name='formType' id='formTypeOds' class='text ui-widget-content ui-corner-all' value='ods'/>" +
                "<label for='formTypeXlsx'>Excel (XLSX)</label>" +
                "<input type='radio' name='formType' id='formTypeXls' class='text ui-widget-content ui-corner-all' value='xlsx'/>" +
                "</fieldset></form>");

  jQuery(function () {
    jQuery('#getBulkSampleInputFormDialog').dialog({
      autoOpen: false,
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Get Form": function () {
          Project.ui.downloadBulkSampleInputForm(projectId, jQuery('input[name=formType]:checked').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  });
  jQuery('#getBulkSampleInputFormDialog').dialog('open');
}

function getPlateInputForm(projectId) {
  jQuery('#getPlateInputFormDialog')
          .html("<form>" +
                "<fieldset class='dialog'>" +
                "<p>Select desired document format</p><br/>" +
                "<label for='formTypeOds'>OpenOffice (ODS)</label>" +
                "<input type='radio' name='formType' id='formTypeOds' class='text ui-widget-content ui-corner-all' value='ods'/>" +
                "<label for='formTypeXlsx'>Excel (XLSX)</label>" +
                "<input type='radio' name='formType' id='formTypeXls' class='text ui-widget-content ui-corner-all' value='xlsx'/>" +
                "</fieldset></form>");

  jQuery(function () {
    jQuery('#getPlateInputFormDialog').dialog({
      autoOpen: false,
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Get Form": function () {
          Project.ui.downloadPlateInputForm(projectId, jQuery('input[name=formType]:checked').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  });
  jQuery('#getPlateInputFormDialog').dialog('open');
}

