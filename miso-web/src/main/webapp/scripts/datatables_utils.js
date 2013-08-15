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
  naturalSort : function(a, b) {
      var re = /(^-?[0-9]+(\.?[0-9]*)[df]?e?[0-9]?$|^0x[0-9a-f]+$|[0-9]+)/gi,
          sre = /(^[ ]*|[ ]*$)/g,
          dre = /(^([\w ]+,?[\w ]+)?[\w ]+,?[\w ]+\d+:\d+(:\d+)?[\w ]?|^\d{1,4}[\/\-]\d{1,4}[\/\-]\d{1,4}|^\w+, \w+ \d+, \d{4})/,
          hre = /^0x[0-9a-f]+$/i,
          ore = /^0/,
          // convert all to strings and trim()
          x = a.toString().replace(sre, '') || '',
          y = b.toString().replace(sre, '') || '',
          // chunk/tokenize
          xN = x.replace(re, '\0$1\0').replace(/\0$/, '').replace(/^\0/, '').split('\0'),
          yN = y.replace(re, '\0$1\0').replace(/\0$/, '').replace(/^\0/, '').split('\0'),
          // numeric, hex or date detection
          xD = parseInt(x.match(hre)) || (xN.length != 1 && x.match(dre) && Date.parse(x)),
          yD = parseInt(y.match(hre)) || xD && y.match(dre) && Date.parse(y) || null;
      // first try and sort Hex codes or Dates
      if (yD) if (xD < yD) return -1;
      else if (xD > yD) return 1;
      // natural sorting through split numeric strings and default strings
      for (var cLoc = 0, numS = Math.max(xN.length, yN.length); cLoc < numS; cLoc++) {
          // find floats not starting with '0', string or 0 if not defined (Clint Priest)
          oFxNcL = !(xN[cLoc] || '').match(ore) && parseFloat(xN[cLoc]) || xN[cLoc] || 0;
          oFyNcL = !(yN[cLoc] || '').match(ore) && parseFloat(yN[cLoc]) || yN[cLoc] || 0;
          // handle numeric vs string comparison - number < string - (Kyle Adams)
          if (isNaN(oFxNcL) !== isNaN(oFyNcL)) return (isNaN(oFxNcL)) ? 1 : -1;
          // rely on string comparison if different types - i.e. '02' < 2 != '02' < '2'
          else if (typeof oFxNcL !== typeof oFyNcL) {
              oFxNcL += '';
              oFyNcL += '';
          }
          if (oFxNcL < oFyNcL) return -1;
          if (oFxNcL > oFyNcL) return 1;
      }
      return 0;
  },

  collapseInputs : function(tableselector) {
    var tableObj = jQuery(tableselector);
    var table = tableObj.dataTable();
    tableObj.find("input,select").each(function() {
      var tr = jQuery(this).parent().parent().parent();
      var td = jQuery(this).parent().parent();
      var row = tr.parent().children().index(tr);
      var col = tr.children().index(td);

      var inval = jQuery(this).val();
      if (Utils.validation.isNullCheck(inval)) {
        table.fnUpdate("", row, col, false);
      }
      else {
        table.fnUpdate(inval, row, col, false);
      }
      jQuery(this).blur();
    });

    tableObj.dataTable();
  },

  toggleSelectAll : function(tableselector, span) {
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
  },

  fillDown : function(tableselector, th) {
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

  fnGetSelected : function(datatable) {
    var aReturn = new Array();
    var aTrs = datatable.fnGetNodes();
    for (var i = 0; i < aTrs.length; i++) {
      if (jQuery(aTrs[i]).hasClass('row_selected')) {
        aReturn.push(aTrs[i]);
      }
    }
    return aReturn;
  }
};

jQuery(document).ready(function () {
  // Natural Sorting
  jQuery.fn.dataTableExt.oSort['natural-asc'] = function (a, b) {
    return DatatableUtils.naturalSort(a, b);
  };
  jQuery.fn.dataTableExt.oSort['natural-desc'] = function (a, b) {
    return DatatableUtils.naturalSort(a, b) * -1;
  };
});