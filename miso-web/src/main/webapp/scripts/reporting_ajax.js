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

/*
function toggleAddTableToQuerySet(tableInput) {
  if (tableInput.checked) {
    Fluxion.doAjax(
            'reportingControllerHelperService',
            'getTableColumns',
    {'table':tableInput.value,'url':ajaxurl},
    {'doOnSuccess':function(json) {
      jQuery('#querydiv').append(json.html);
    }
    }
            );
  }

  else {
    jQuery('#' + tableInput.value + '_div').remove();
  }
}

function processQueryParameters() {
  var qParams = "{\"qparams\":[";

  var tcount = 0;
  var tlen = jQuery('#querydiv table[class=paramtable]').length;

  jQuery('#querydiv table[class=paramtable]').each(function(e) {
    tcount++;
    var tab = jQuery(this);
    var tName = tab.attr("table");

    qParams += "{\"" + tName + "\":[";

    var ccount = 0;
    var clen = tab.find('input[type=checkbox]:checked').length;

    tab.find('input[type=checkbox]:checked').each(function(e) {
      ccount++;
      var inp = jQuery(this);
      var cName = inp.attr("column");

      qParams += "{\"" + cName + "\":[";

      var k = jQuery('select[table=' + tName + '][column=' + cName + ']').val();
      var v = jQuery('#' + cName + '_value').val();
      if (v == "") v = "-";
      qParams += "{\"" + k + "\":\"" + v + "\"}";

      qParams += "]}";
      if (ccount < clen) qParams += ",";
    }
            );
    qParams += "]}";
    if (tcount < tlen) qParams += ",";
  });

  qParams += "]}";

  alert(qParams);

  Fluxion.doAjax(
          'reportingControllerHelperService',
          'processQueryParameters',
  {'parameters':jQuery.parseJSON(qParams), 'url':ajaxurl},
  {'doOnSuccess':function(json) {
    alert(json.html);
  }
  }
          );
}

function changeReportType(form) {
  Fluxion.doAjax(
          'reportingControllerHelperService',
          'changeReportType',
  {'reportType':form.value, 'url':ajaxurl},
  {'doOnSuccess':function(json) {
    $('reportTable').innerHTML = json.html;
    $('title').innerHTML = 'Report for ' + form.value + 's';
    reportloadAll(form.value);
    prepareTable();
  }
  }
          );
}

function reportSearch(inp, throbber) {
  var t = jQuery(inp);
  var id = t.attr('id');
  Fluxion.doAjax(
          'reportingControllerHelperService',
          id,
  {'str':t.val(), 'url':ajaxurl},
  {
    "doOnLoading":
            function(json) {
              if (throbber) {
                jQuery('#' + id + 'result').html("<img src='../styles/images/ajax-loader.gif'/>");
              }
            },
    "doOnSuccess":
            function(json) {
              if (throbber) {
                jQuery('#' + id + 'result').html("");
              }

              if (json.html && json.html !== "") {
                jQuery('#' + id + 'result').html(json.html);
              }
              else {
                jQuery('#' + id + 'result').html("No matches");
              }
              prepareTable();
            }
  });
  return true;
}

function reportloadAll(type) {
  Utils.timer.timedFunc(reportSearch(jQuery('#search' + type), true), 200);
}

function generateReport(form) {
  Utils.ui.disableButton('generateReportButton');

  Fluxion.doAjax(
          'reportingControllerHelperService',
          'generateReport',
  {'form':jQuery('#' + form).serializeArray(), 'url':ajaxurl},
  {'doOnSuccess':function(json) {
      jQuery('#generateReportButton').removeAttr('disabled');
      jQuery('#generateReportButton').html("Generate Report");
      writeConsole(json.html);
    }
  });
}

function writeConsole(content) {
  top.consoleRef = window.open('', 'Report',
          'width=1000,height=800'
                  + ',menubar=0'
                  + ',toolbar=1'
                  + ',status=0'
                  + ',scrollbars=1'
                  + ',resizable=1');
  top.consoleRef.document.writeln(
          content
          );
  top.consoleRef.document.close();
}
*/
