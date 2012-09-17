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

var Print = Print || {};

Print.ui = {
  getPrinterFormEntities : function() {
    var self = this;
    Fluxion.doAjax(
      'printerControllerHelperService',
      'getPrinterFormEntities',
      {'url':ajaxurl},
      {'doOnSuccess':self.processPrinterServiceRow}
    );
  },

  processPrinterServiceRow : function(json) {
    jQuery('#printerTable tr:first th:eq(4)').remove();
    jQuery('#printerTable tr:first th:eq(3)').remove();

    jQuery('#printerTable').find("tr").each(function() {
      jQuery(this).find("td:eq(4)").remove();
      jQuery(this).find("td:eq(3)").remove();
    });

    jQuery('#printerTable tr:first').append("<th>Printable Entity</th><th>Available</th><th></th>");

    $('printerTable').insertRow(1);

    var column1 = $('printerTable').rows[1].insertCell(-1);
    column1.innerHTML = "<input id='serviceName' name='serviceName' type='text'/>";
    var column2 = $('printerTable').rows[1].insertCell(-1);
    column2.innerHTML = "<i>Set in context fields</i>";
    var column3 = $('printerTable').rows[1].insertCell(-1);
    column3.innerHTML = "<select id='contexts' name='context' onchange='Print.ui.getContextFieldsForContext(this)'>" +json.contexts+ "</select><br/><div id='contextFields' name='contextFields'/>";
    var column4 = $('printerTable').rows[1].insertCell(-1);
    column4.innerHTML = "<select id='barcodables' name='printServiceFor'>" +json.barcodables+ "</select>";
    var column5 = $('printerTable').rows[1].insertCell(-1);
    column5.innerHTML = "<div id='available'></div>";
    var column6 = $('printerTable').rows[1].insertCell(-1);
    column6.id = "addTd";
    column6.innerHTML = "Add";
  },

  getContextFieldsForContext : function(contextSelect) {
    var self = this;
    var context = jQuery(contextSelect).val();
    Fluxion.doAjax(
            'printerControllerHelperService',
            'getContextFields',
    {'contextName':context, 'url':ajaxurl},
    {'doOnSuccess':self.processContextChange}
    );
  },

  processContextChange : function(json) {
    jQuery('#contextFields').html("Context fields:<br/>");
    var fields = json.contextFields;
    for (var key in fields) {
      if (fields.hasOwnProperty(key)) {
        jQuery('#contextFields').append(key +": <input id='contextField-"+key+"' field='"+key+"' type='text' value='"+fields[key]+"'/><br/>");
      }
    }
    jQuery('#contextField-host').keyup(function() { Print.service.validatePrinter(this) });
  }
};

Print.service = {
  validatePrinter : function(t) {
    $('available').innerHTML="<div align='center'><img src='../../styles/images/ajax-loader.gif'/></div>";

    if (t.value != t.lastValue) {
      if (t.timer) clearTimeout(t.timer);

      t.timer = setTimeout(function () {
        Fluxion.doAjax(
          'printerControllerHelperService',
          'checkPrinterAvailability',
          {'host':t.value, 'url':ajaxurl},
          {"doOnSuccess": function(json) {
            $('available').innerHTML = json.html;
            if (json.html == "OK") {
              $('available').setAttribute("style", "background-color:green");
              $('addTd').innerHTML = "<a href='javascript:void(0);' onclick='Print.service.addPrinterService(\"addPrinterForm\");'/>Add</a>";
            }
            else {
              $('available').setAttribute("style", "background-color:red");
            }
          }
        });
      }, 200);
      t.lastValue = t.value;
    }
  },

  addPrinterService : function(form) {
    var f = $(form);
    var cf = {};
    jQuery('input[id*="contextField-"]').each(function(e) {
      var field = jQuery(this).attr("field");
      cf[field] = jQuery(this).val();
    });

    Fluxion.doAjax(
      'printerControllerHelperService',
      'addPrintService',
      {
        'serviceName':f.serviceName.value,
        'contextName':f.context.value,
        'contextFields':cf,
        'serviceFor':f.printServiceFor.value,
        'url':ajaxurl},
      {'doOnSuccess':Utils.page.pageReload,
       'doOnError':function(json) {
         alert(json.error);
       }
      }
    );
  },

  disablePrintService : function(printerName) {
    Fluxion.doAjax(
    'printerControllerHelperService',
    'disablePrintService',
    {
      'printerName':printerName,
      'url':ajaxurl},
    {
      'doOnSuccess':Utils.page.pageReload
    });
  },

  enablePrintService : function(printerName) {
    Fluxion.doAjax(
    'printerControllerHelperService',
    'enablePrintService',
    {
      'printerName':printerName,
      'url':ajaxurl},
    {
      'doOnSuccess':Utils.page.pageReload
    });
  },

  reprintJob : function(jobId) {
    Fluxion.doAjax(
    'printerControllerHelperService',
    'reprintJob',
    {
      'jobId':jobId,
      'url':ajaxurl},
    {
      'doOnSuccess': function(json) {
        alert(json.response);
      }
    });
  }
};