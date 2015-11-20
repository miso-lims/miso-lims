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
  changePrinterServiceRow : function(printService) {
    Fluxion.doAjax(
      'printerControllerHelperService',
      'changePrinterServiceRow',
      {
        'serviceName':printService,
        'url':ajaxurl
      },
      {'doOnSuccess':function(json) {
        jQuery('#host-' + printService).html(json.hostname);
        jQuery('#edit-' + printService).html(json.edit);
        if(jQuery('#schema-' + printService).is(':empty')){
          jQuery('#schema-' + printService).html(json.barcodableSchemas);
        }else{
          var selectedschema =  jQuery('#schema-' + printService).val();
          jQuery('#schema-' + printService).html(json.barcodableSchemas);
          jQuery('#schema-' + printService+' select').val(selectedschema);
        }
      }
    });
  },

  editPrinterService : function(serviceName) {
    var schema='';
    if(!jQuery('#newschema-' + serviceName).length == 0) {
      schema =  jQuery('#newschema-' + serviceName+' option:selected').val();
    }
    Fluxion.doAjax(
      'printerControllerHelperService',
      'editPrinterService',
      {
        'serviceName':serviceName,
        'host':jQuery('#newhost-' + serviceName).val(),
        'schema':schema,
        'url':ajaxurl
      },
      {'doOnSuccess':Utils.page.pageReload
      }
    );
  },

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
    if (!jQuery('#printerTable').attr("addInProgress")) {
      jQuery('#printerTable').attr("addInProgress", "true");
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
      column4.innerHTML = "<select id='barcodableSchemas' name='printSchema'>" +json.barcodableSchemas+ "</select>";
      var column5 = $('printerTable').rows[1].insertCell(-1);
      column5.innerHTML = "<select id='barcodables' name='printServiceFor'>" +json.barcodables+ "</select>";
      var column6 = $('printerTable').rows[1].insertCell(-1);
      column6.innerHTML = "<div id='available'></div>";
      var column7 = $('printerTable').rows[1].insertCell(-1);
      column7.id = "addTd";
      column7.innerHTML = "Add";
    }
    else {
      alert("Cannot add another printer service when one is already in progress.")
    }
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
              $('addTd').innerHTML = "<a href='javascript:void(0);' onclick='Print.service.addPrinterService();'/>Add</a>";
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

  addPrinterService : function() {
    jQuery('#printerTable').removeAttr("addInProgress");

    var f = Utils.mappifyForm("addPrinterForm");
    var cf = {};
    jQuery('input[id*="contextField-"]').each(function(e) {
      var field = jQuery(this).attr("field");
      cf[field] = jQuery(this).val();
    });

    Fluxion.doAjax(
      'printerControllerHelperService',
      'addPrintService',
      {
        'serviceName':f.serviceName,
        'contextName':f.context,
        'contextFields':cf,
        'serviceFor':f.printServiceFor,
        'schema':f.printSchema,
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
  },

  printCustomBarcodes: function () {
    var samples = [];
    for (var i = 0; i < arguments.length; i++) {
      samples[i] = {'sampleId': arguments[i]};
    }

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'serviceClass': 'net.sf.json.JSONObject',
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#printServiceSelectDialog')
                  .html("<form>" +
                        "<fieldset class='dialog'>" +
                        "<select name='serviceSelect' id='serviceSelect' class='ui-widget-content ui-corner-all'>" +
                        json.services +
                        "</select></fieldset></form>");

          var barcodeitornot = 'no';
          if (jQuery('#barcodeit').is(':checked')) {
            barcodeitornot = 'yes';
          }
          jQuery('#printServiceSelectDialog').dialog({
            width: 400,
            modal: true,
            resizable: false,
            buttons: {
              "Print": function () {
                Fluxion.doAjax(
                  'printerControllerHelperService',
                  'printCustomBarcode',
                  {
                    'serviceName': jQuery('#serviceSelect').val(),
                    'line1': jQuery('#customPrintLine1').val(),
                    'line2': jQuery('#customPrintLine2').val(),
                    'line3': jQuery('#customPrintLine3').val(),
                    'barcodeit': barcodeitornot,
                    'url': ajaxurl
                  },
                  {
                    'doOnSuccess': function (json) {
                      alert(json.response);
                    }
                  }
                );
                jQuery(this).dialog('close');
              },
              "Cancel": function () {
                jQuery(this).dialog('close');
              }
            }
          });
        },
        'doOnError': function (json) {
          alert(json.error);
        }
      }
    );
  },

  printCustom1DBarcodes: function () {
    var samples = [];
    for (var i = 0; i < arguments.length; i++) {
      samples[i] = {'sampleId': arguments[i]};
    }

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'serviceClass': 'net.sf.json.JSONObject',
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
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
              "Print": function () {
                Fluxion.doAjax(
                  'printerControllerHelperService',
                  'printCustom1DBarcode',
                  {
                    'serviceName': jQuery('#serviceSelect').val(),
                    'line1': jQuery('#custom1DPrintLine1').val(),
                    'line2': jQuery('#custom1DPrintLine2').val(),
                    'url': ajaxurl
                  },
                  {
                    'doOnSuccess': function (json) {
                      alert(json.response);
                    }
                  }
                );
                jQuery(this).dialog('close');
              },
              "Cancel": function () {
                jQuery(this).dialog('close');
              }
            }
          });
        },
        'doOnError': function (json) {
          alert(json.error);
        }
      }
    );
  } ,

  printCustom1DBarcodesBulk: function (codes) {

    if (codes === "") {
      alert("Please input at least one barcode...");
    }
    var samples = [];
    for (var i = 0; i < arguments.length; i++) {
      samples[i] = {'sampleId': arguments[i]};
    }

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'serviceClass': 'net.sf.json.JSONObject',
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
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
              "Print": function () {
                Fluxion.doAjax(
                  'printerControllerHelperService',
                  'printCustom1DBarcodeBulk',
                  {
                    'serviceName': jQuery('#serviceSelect').val(),
                    'barcodes': codes,
                    'url': ajaxurl
                  },
                  {
                    'doOnSuccess': function (json) {
                      alert(json.response);
                    }
                  }
                );
                jQuery(this).dialog('close');
              },
              "Cancel": function () {
                jQuery(this).dialog('close');
              }
            }
          });
        },
        'doOnError': function (json) {
          alert(json.error);
        }
      }
    );
  }
};