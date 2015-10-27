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


function bulkLibraryQcTable(tableName) {
  var lTable = jQuery(tableName);
  if (!lTable.hasClass("display")) {
    //destroy current table and recreate
    lTable.dataTable().fnDestroy();
    //bug fix to reset table width
    lTable.removeAttr("style");

    lTable.addClass("display");

    //remove edit header and column
    jQuery(tableName + ' tr:first th:gt(8)').remove();
    jQuery(tableName + ' tr:first th:eq(6)').remove();

    var libraryheaders = ['rowsel',
                          'name',
                          'alias',
                          'date',
                          'description',
                          'libraryType',
                          'platform',
      //'tagBarcode',
                          'insertSize',
                          'qcPassed',
                          'qcDate',
                          'qcType',
                          'results'];

    lTable.find("tr").each(function () {
      if (jQuery(this).find("td:eq(8)").html() == "true") {
        jQuery(this).remove();
      }
      else {
        jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
        jQuery(this).find("td:gt(8)").remove();
        jQuery(this).find("td:eq(6)").remove();
        jQuery(this).find("td:eq(8)").addClass("passedCheck");
      }
    });

    //headers
    jQuery(tableName + " tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"#library_table\", this);'></span></th>");
    jQuery(tableName + ' tr:first th:eq(8)').html("QC Passed <span header='qcPassed' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\"#library_table\", this);'></span>");
    jQuery(tableName + " tr:first").append("<th>QC Date <span header='qcDate' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\"#library_table\", this);'></span></th>");
    jQuery(tableName + " tr:first").append("<th>QC Method <span header='qcType' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\"#library_table\", this);'></span></th>");
    jQuery(tableName + " tr:first").append("<th>Concentration</th>");

    //columns
    jQuery(tableName + " tr:gt(0)").prepend("<td class='rowSelect'></td>");
    jQuery(tableName + " tr:gt(0)").find("td:eq(7)").addClass("defaultEditable");
    jQuery(tableName + " tr:gt(0)").find("td:eq(8)").addClass("passedCheck");
    jQuery(tableName + " tr:gt(0)").append("<td class='dateSelect'></td>");
    jQuery(tableName + " tr:gt(0)").append("<td class='typeSelect'></td>");
    jQuery(tableName + " tr:gt(0)").append("<td class='defaultEditable'></td>");

    var datatable = lTable.dataTable({
                                       "aoColumnDefs": [
                                         {
                                           "bUseRendered": false,
                                           "aTargets": [ 0 ]
                                         }
                                       ],
                                       "aaSorting": [
                                         [2, 'asc']
                                       ],
                                       "aoColumns": [
                                         {"bSortable": false},
                                         { "sType": 'natural' },
                                         { "sType": 'natural' },
                                         { "sType": 'natural' },
                                         { "sType": 'natural' },
                                         null,
                                         null,
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
                                       "bFilter": true,
                                       "sDom": '<<"toolbar">f>r<t>ip>'
                                     });

    lTable.find("tr:gt(0)").each(function () {
      for (var i = 0; i < this.cells.length; i++) {
        jQuery(this.cells[i]).attr("name", libraryheaders[i]);
      }
    });

    jQuery(tableName + ' .rowSelect').click(function () {
      if (jQuery(this).parent().hasClass('row_selected'))
        jQuery(this).parent().removeClass('row_selected');
      else if (!jQuery(this).parent().hasClass('row_saved'))
        jQuery(this).parent().addClass('row_selected');
    });

    jQuery("div.toolbar").html("<input type='button' value='Save QCs' id=\"bulkLibraryQcButton\" onclick=\"Sample.qc.saveBulkLibraryQc('" + tableName + "');\" class=\"fg-button ui-state-default ui-corner-all\"/>");
    jQuery("div.toolbar").append("<input type='button' value='Cancel'  onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\"/>");
    jQuery("div.toolbar").removeClass("toolbar");

    jQuery(tableName + ' .defaultEditable').editable(function (value, settings) {
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
                                                     });

    jQuery(tableName + " .typeSelect").editable(function (value, settings) {
                                                  return value;
                                                },
                                                {
                                                  data: libraryQcTypesString,
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
                                                });

    jQuery(tableName + " .dateSelect").editable(function (value, settings) {
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
                                                });

    jQuery(tableName + " .passedCheck").editable(function (value, settings) {
                                                   return value;
                                                 },
                                                 {
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
                                                 });
  }
}

function bulkLibraryDilutionTable(tableName) {
  var lTable = jQuery(tableName);
  if (!lTable.hasClass("display")) {
    //destroy current table and recreate
    lTable.dataTable().fnDestroy();
    //bug fix to reset table width
    lTable.removeAttr("style");

    lTable.addClass("display");

    //remove edit header and column
    jQuery(tableName + ' tr:first th:gt(8)').remove();
    jQuery(tableName + ' tr:first th:eq(6)').remove();

    var dilutionheaders = ['rowsel',
                           'name',
                           'alias',
                           'date',
                           'description',
                           'libraryType',
                           'platform',
                           'insertSize',
                           'qcPassed',
                           'dilutionDate',
                           'results'];

    lTable.find("tr").each(function () {
      if (jQuery(this).find("td:eq(8)").html() == "false") {
        jQuery(this).remove();
      }
      else {
        jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
        jQuery(this).find("td:gt(8)").remove();
        jQuery(this).find("td:eq(6)").remove();
      }
    });

    //headers
    jQuery(tableName + " tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"#library_table\", this);'></span></th>");
    jQuery(tableName + " tr:first").append("<th>Dilution Date <span header='dilutionDate' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\"#library_table\", this);'></span></th>");
    jQuery(tableName + " tr:first").append("<th>Concentration <span header='qcType' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\"#library_table\", this);'></span></th>");

    //columns
    jQuery(tableName + " tr:gt(0)").prepend("<td class='rowSelect'></td>");
    jQuery(tableName + " tr:gt(0)").append("<td class='dateSelect'></td>");
    jQuery(tableName + " tr:gt(0)").append("<td class='defaultEditable'></td>");

    var datatable = lTable.dataTable({
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
                                         { "sType": 'natural' },
                                         null,
                                         {"bSortable": false},
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

    lTable.find("tr:gt(0)").each(function () {
      for (var i = 0; i < this.cells.length; i++) {
        jQuery(this.cells[i]).attr("name", dilutionheaders[i]);
      }
    });

    jQuery(tableName + ' .rowSelect').click(function () {
      if (jQuery(this).parent().hasClass('row_selected'))
        jQuery(this).parent().removeClass('row_selected');
      else if (!jQuery(this).parent().hasClass('row_saved'))
        jQuery(this).parent().addClass('row_selected');
    });

    jQuery("div.toolbar").html("<input type='button' value='Save Dilutions' id=\"bulkLibraryDilutionButton\" onclick=\"Sample.library.saveBulkLibraryDilutions('" + tableName + "');\" class=\"fg-button ui-state-default ui-corner-all\"/>");
    jQuery("div.toolbar").append("<input type='button' value='Cancel' onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\"/>");
    jQuery("div.toolbar").removeClass("toolbar");

    jQuery(tableName + ' .defaultEditable').editable(function (value, settings) {
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
                                                     });

    jQuery(tableName + " .dateSelect").editable(function (value, settings) {
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
                                                });
  }
}
