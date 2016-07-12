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
 * Created by IntelliJ IDEA.
 * User: davey
 * Date: 17-Aug-2011
 * Time: 16:37:48
 */
var DatatableUtils = DatatableUtils || {};

DatatableUtils = {
  collapseInputs: function (tableselector) {

    var tableObj = jQuery(tableselector);
    var table = tableObj.dataTable();
    tableObj.find("input,select").each(function () {
      var cell = jQuery(this).closest('td');
      var cellIndex = cell[0].cellIndex;

      var tr = jQuery(this).parent().parent().parent();
      var row = tr.parent().children().index(tr);

      var inval = jQuery(this).val();
      if (Utils.validation.isNullCheck(inval)) {
        table.fnUpdate("", row, cellIndex, false);
      }
      else {
        table.fnUpdate(inval, row, cellIndex, false);
      }
      jQuery(this).blur();
    });

    tableObj.dataTable();
  },

  toggleSelectAll: function (tableselector, span) {
    var tableObj = jQuery(tableselector);
    var s = jQuery(span);
    var sel = s.attr("sel");
    if (sel == "none") {
      tableObj.find('tbody tr').each(function () {
        jQuery(this).addClass('row_selected');
      });
      s.attr("sel", "all");
    }
    else {
      tableObj.find('tbody tr').each(function () {
        jQuery(this).removeClass('row_selected');
      });
      s.attr("sel", "none");
    }
  },

  fillDown: function (tableselector, th) {
    var self = this;
    self.collapseInputs(tableselector);

    var tableObj = jQuery(tableselector);
    var table = tableObj.dataTable();
    var header = jQuery(th);
    var headerName = header.attr("header");
    var firstSelectedRow = tableObj.find(".row_selected").first();
    if (firstSelectedRow.length > 0) {
      var td = firstSelectedRow.find("td[name=" + headerName + "]");
      var tdtext = td.html();
      var col = firstSelectedRow.children().index(td);

      var frId = 0;
      var aTrs = table.fnGetNodes();
      for (var i = 0; i < aTrs.length; i++) {
        if (jQuery(aTrs[i]).hasClass('row_selected')) {
          frId = i;
          break;
        }
      }

      tableObj.find("tr:gt(" + frId + ")").each(function () {
        table.fnUpdate(tdtext, table.fnGetPosition(this), col);
      });
    }
    else {
      alert("Please select a row to use as the Fill Down template by clicking in the Select column for that row.");
    }
  },

  fnGetSelected: function (datatable) {
    var aReturn = [];
    var aTrs = datatable.fnGetNodes();
    for (var i = 0; i < aTrs.length; i++) {
      if (jQuery(aTrs[i]).hasClass('row_selected')) {
        aReturn.push(aTrs[i]);
      }
    }
    return aReturn;
  }
};

jQuery.fn.dataTableExt.oApi.fnSetFilteringDelay = function ( oSettings, iDelay ) {
  var _that = this;

  if (iDelay === undefined) iDelay = 250;

  this.each(function (i) {
    jQuery.fn.dataTableExt.iApiIndex = i;
    var oTimerId = null,
        sPreviousSearch = null,
        anControl = jQuery('input', _that.fnSettings().aanFeatures.f);
    anControl.unbind('keyup search input').bind('keyup search input', function () {
      if (sPreviousSearch === null || sPreviousSearch != anControl.val()) {
        window.clearTimeout(oTimerId);
        sPreviousSearch = anControl.val();
        oTimerId = window.setTimeout(function () {
          jQuery.fn.dataTableExt.iApiIndex = i;
          _that.fnFilter(anControl.val());
        }, iDelay);
      }
    });
    return this;
  });
  return this;
};