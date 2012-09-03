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

function collapseInputs(tableselector) {
  var tableObj = jQuery(tableselector);
  var table = tableObj.dataTable();
  tableObj.find("input,select").each(function() {
    var tr = jQuery(this).parent().parent().parent();
    var td = jQuery(this).parent().parent();
    var row = tr.parent().children().index(tr);
    var col = tr.children().index(td);

    var inval = jQuery(this).val();
    if (isNullCheck(inval)) {
      table.fnUpdate("", row, col, false);
    }
    else {
      table.fnUpdate(inval, row, col, false);
    }
    jQuery(this).blur();
  });

  tableObj.dataTable();
}

function toggleSelectAll(tableselector, span) {
  var tableObj = jQuery(tableselector);
  var s = jQuery(span);
  var sel = s.attr("sel");
  if (sel == "none") {
    tableObj.find('tbody tr').each(function() { jQuery(this).addClass('row_selected') });
    s.attr("sel", "all");
  }
  else {
    tableObj.find('tbody tr').each(function() { jQuery(this).removeClass('row_selected') });
    s.attr("sel", "none");
  }
}

function fillDown(tableselector, th) {
  collapseInputs(tableselector);
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
}

function fnGetSelected(datatable) {
  var aReturn = new Array();
  var aTrs = datatable.fnGetNodes();
  for (var i = 0; i < aTrs.length; i++) {
    if (jQuery(aTrs[i]).hasClass('row_selected')) {
      aReturn.push(aTrs[i]);
    }
  }
  return aReturn;
}