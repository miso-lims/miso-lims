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


function bulkEmPcrDilutionTable() {
  if (!jQuery('#empcrs_table').hasClass("display")) {
    //destroy current table and recreate
    jQuery('#empcrs_table').dataTable().fnDestroy();
    //bug fix to reset table width
    jQuery('#empcrs_table').removeAttr("style");

    jQuery('#empcrs_table').addClass("display");
    //remove edit header and column
    jQuery('#empcrs_table tr:first th:gt(4)').remove();

    var dilutionheaders = ['rowsel',
                           'pcrName',
                           'libDilName',
                           'pcrCreator',
                           'pcrDate',
                           'pcrConc',
                           'pcrDilutionDate',
                           'results'];

    jQuery('#empcrs_table').find("tr").each(function () {
      jQuery(this).removeAttr("onmouseover").removeAttr("onmouseout");
      jQuery(this).find("td:gt(4)").remove();
    });

    //headers
    jQuery("#empcrs_table tr:first").prepend("<th>Select <span sel='none' header='select' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.toggleSelectAll(\"#empcrs_table\", this);'></span></th>");
    jQuery("#empcrs_table tr:first").append("<th>Dilution Date <span header='pcrDilutionDate' class='ui-icon ui-icon-arrowstop-1-s' style='float:right' onclick='DatatableUtils.fillDown(\"#empcrs_table\", this);'></span></th>");
    jQuery("#empcrs_table tr:first").append("<th>Concentration</th>");

    //columns
    jQuery("#empcrs_table tr:gt(0)").prepend("<td class='rowSelect'></td>");
    jQuery("#empcrs_table tr:gt(0)").append("<td class='dateSelect'></td>");
    jQuery("#empcrs_table tr:gt(0)").append("<td class='defaultEditable'></td>");

    var datatable = jQuery('#empcrs_table').dataTable({
                                                        "aoColumnDefs": [
                                                          {
                                                            "bUseRendered": false,
                                                            "aTargets": [ 0 ]
                                                          }
                                                        ],
                                                        "aoColumns": [
                                                          {"bSortable": false},
                                                          { "sType": 'natural' },
                                                          null,
                                                          null,
                                                          null,
                                                          null,
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

    jQuery('#empcrs_table').find("tr:gt(0)").each(function () {
      for (var i = 0; i < this.cells.length; i++) {
        jQuery(this.cells[i]).attr("name", dilutionheaders[i]);
      }
    });

    jQuery('#empcrs_table .rowSelect').click(function () {
      if (jQuery(this).parent().hasClass('row_selected'))
        jQuery(this).parent().removeClass('row_selected');
      else if (!jQuery(this).parent().hasClass('row_saved'))
        jQuery(this).parent().addClass('row_selected');
    });

    jQuery("div.toolbar").html("<input type='button' value='Save Dilutions' id=\"bulkEmPcrDilutionButton\" onclick=\"Project.ui.saveBulkEmPcrDilutions();\" class=\"fg-button ui-state-default ui-corner-all\"/>");
    jQuery("div.toolbar").append("<input type='button' value='Cancel' onclick=\"Utils.page.pageReload();\" class=\"fg-button ui-state-default ui-corner-all\"/>");
    jQuery("div.toolbar").removeClass("toolbar");

    jQuery('#empcrs_table .defaultEditable').editable(function (value, settings) {
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

    jQuery("#empcrs_table .dateSelect").editable(function (value, settings) {
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
