jQuery.getScript("/scripts/box_visualization.js");

var Box = Box || {
  boxJSON: null,
  originalBox: null,
  boxId: null,
  visual: null,

  // Start the scanning process
  initScan: function() {
    Box.dialogWidth = Box.boxJSON.size.columns * 40 + 150;
    Box.dialogHeight = Box.boxJSON.size.rows * 40 + 300;
    Box.scanDialog = Box.ScanDialog();
    Box.prepareScannerDialog = Box.PrepareScannerDialog();
    Box.scanDiff = Box.ScanDiff();
    Box.prepareScannerDialog.show();
  },

  update: function() {
    Box.createListingTable();
    Box.createVisualization();
    jQuery('#updateSelected, #removeSelected, #emptySelected').prop('disabled', true).addClass('disabled');
  },

  createVisualization: function() {
    var selected = Box.visual.selected;
    Box.visual.create({
      div: '#boxContentsTable',
      size: {
        rows: Box.boxJSON.size.rows,
        cols: Box.boxJSON.size.columns
      },
      data: Box.boxJSON.boxables
    });
    if (selected)
      Box.visual.click(selected.row, selected.col);
  },

  createListingTable: function() {
    Box.ui.createListingBoxablesTable(Box.boxJSON);
  },

  // Saves entire box (stored in Box.boxJSON)
  saveContents: function() {
    Fluxion.doAjax(
      'boxControllerHelperService',
      'saveBoxContents',
      {
        'boxJSON': Box.boxJSON,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function() { Box.update(); },
        'doOnError': function(json) { alert("Error saving box contents: "+json.error); }
      }
    );
  },

  deleteBox: function (boxId, successfunc) {
    if (confirm("Are you sure you really want to delete BOX" + Box.boxId + "? This operation is permanent!")) {
      Fluxion.doAjax(
        'boxControllerHelperService',
        'deleteBox',
        {
          'boxId': Box.boxId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function () { window.location.href = '/miso/boxes'; }
        }
      );
    }
  },
  
  lookupBoxableByBarcode: function() {
    var barcode = jQuery('#selectedBarcode').val();
    if (!jQuery('#selectedPosition').text()) { 
      jQuery('#warningMessages').html("Please select a position from the grid, then rescan the barcode."); 
      return null;
    }
    if (Utils.validation.isNullCheck(barcode)) {
      alert("Please enter a barcode.");
    } else {
      jQuery('#lookupBarcode').prop('disabled', true).addClass('disabled');
      jQuery('#warningMessages').html('<img src="/styles/images/ajax-loader.gif" alt="Loading" />');
      Fluxion.doAjax(
        'boxControllerHelperService',
        'lookupBoxableByBarcode',
        {
          'barcode': barcode,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function (json) {
            var boxable = json.boxable;
            jQuery('#selectedName').html(Box.utils.hyperlinkifyBoxable(boxable.name, boxable.id, boxable.name));
            jQuery('#selectedAlias').html(Box.utils.hyperlinkifyBoxable(boxable.name, boxable.id, boxable.alias));
            jQuery('#currentLocationText').html("Current Location:");
            if (boxable.boxAlias) {
              jQuery('#currentLocation').html(boxable.boxAlias + " - " + boxable.boxPosition);
            } else {
              jQuery('#currentLocation').html("unknown");
            }
            jQuery('#lookupBarcode').prop('disabled', false).removeClass('disabled');
            jQuery('#warningMessages').html('');
            if (json.hasOwnProperty("trashed")) {
              jQuery('#warningMessages').html(json.trashed);
              jQuery('#updateSelected').prop('disabled', true).addClass('disabled');
            } else {
              jQuery('#updateSelected').prop('disabled', false).removeClass('disabled');
            }
            Box.ui.createListingBoxablesTable(Box.boxJSON);
          },
          'doOnError': function (json) {
            jQuery('#warningMessages').html('');
            alert(json.error);
            jQuery('#selectedBarcode').val('').focus();
            jQuery('#lookupBarcode').prop('disabled', false).removeClass('disabled');
          }
        }
      );
    }
  },
  
  // Validate methods are in parsley_form_validations.js
  validateBox: function () {
    Validate.cleanFields('#box-form');
    jQuery('#box-form').parsley().destroy();
    
    // Alias input field validation
    jQuery('#alias').attr('class', 'form-control');
    jQuery('#alias').attr('data-parsley-required', 'true');
    jQuery('#alias').attr('data-parsley-maxlength', '100');
    jQuery('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    
    // Description input field validation
    jQuery('#description').attr('class', 'form-control');
    jQuery('#description').attr('data-parsley-required', 'true');
    jQuery('#description').attr('data-parsley-max-length', '250');
    jQuery('#description').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    
    // BoxUse input field validation
    jQuery('#boxUse').attr('class', 'form-control');
    jQuery('#boxUse').attr('data-parsley-required', 'true');
    
    // BoxSize input field validation
    jQuery('#boxSize').attr('class', 'form-control');
    jQuery('#boxSize').attr('data-parsley-required', 'true');
    
    jQuery('#box-form').parsley();
    jQuery('#box-form').parsley().validate();
    
    Validate.updateWarningOrSubmit('#box-form');
    return false;
  }
};

Box.scan = {
  prepareScanner: function(boxRows, boxColumns) {
    var prepScannerTimeout = setTimeout(Box.prepareScannerDialog.error, 10000); // otherwise box scanner may poll indefinitely
    Fluxion.doAjax(
      'boxControllerHelperService',
      'prepareBoxScanner',
      {
        'rows': boxRows,
        'columns': boxColumns,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          clearTimeout(prepareScannerTimeout);
          jQuery('#dialogDialog').dialog('close');
          Box.scanDialog.show({
            size: {
              rows: Box.boxJSON.size.rows,
              cols: Box.boxJSON.size.columns
            },
            data: Box.boxJSON.boxables
          });
        },
        'doOnError': function() {
          clearTimeout(prepareScannerTimeout);
          Box.prepareScannerDialog.error();
        }
      }
    );
  },

  scanBox: function() {
    Fluxion.doAjax(
      'boxControllerHelperService',
      'getBoxScan',
      {
        'boxId': Box.boxJSON.id,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function(json) {
          jQuery('#magnify').stop();
          var scan = json;

          if (scan.hasOwnProperty("errors")) {
            console.log("scan JSON from AJAX call has read errors");
            var scanErrors = Box.ScanErrors();
            scanErrors.show({
                scan: scan,
              size: {
                rows: Box.boxJSON.size.rows,
                cols: Box.boxJSON.size.columns
              },
              data: Box.boxJSON.boxables
            });
          } else {
            console.log("scan JSON from AJAX call was successful");
            Box.scanDiff.show({
              scan: scan.boxJSON,
              size: {
                rows: Box.boxJSON.size.rows,
                cols: Box.boxJSON.size.columns
              },
              data: Box.boxJSON.boxables
            });
          }
        },
        'doOnError': function(json) {
          jQuery('#magnify').stop();
          jQuery('#dialogDialog').dialog('close');
          Box.scanDialog.error(json.error);
        }
      }
    );
  }
};


Box.ui = {
  changeBoxListing : function (alias) {
    var table = jQuery('#listingBoxesTable').dataTable();
    table.fnFilter(alias, 6);
  },

  createListingBoxesTable: function () {
    jQuery('#listingBoxesTable').html("<img src='/styles/images/ajax-loader.gif'/>");

    Fluxion.doAjax(
      'boxControllerHelperService',
      'listAllBoxesTable',
      {
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          jQuery('#listingBoxesTable').html('');
          jQuery('#listingBoxesTable').dataTable({
            "aaData": json.array,
            "aoColumns": [
              { "sTitle" : "Box Name" },
              { "sTitle" : "Alias" },
              { "sTitle" : "Location" },
              { "sTitle" : "Items/Capacity" },
              { "sTitle" : "Size" },
              { "sTitle" : "Barcode" },
              { "sTitle" : "Box Use" },
              { "sTitle" : "ID", "bVisible": false }
            ],
            "bJQueryUI": true,
            "bAutoWidth": false,
            "bRetrieve": true,
            "iDisplayLength": 25,
            "sDom": '<l<"#toolbar">f>r<t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>'
          });
          jQuery("#toolbar").parent().addClass("fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix");
          jQuery("#toolbar").append("<button style=\"margin-left:5px;\" onclick=\"window.location.href='/miso/box/new';\" class=\"fg-button ui-state-default ui-corner-all\">Add Box</button>");
        }
      }
    );
  },

  //creates the table of the box contents
  createListingBoxablesTable: function(box) {
    jQuery('#listingBoxablesTable').empty();
    array = [];
    for (var pos in box.boxables) {
      row = [];
      row.push(pos);
      row.push(Box.utils.hyperlinkifyBoxable(box.boxables[pos].name, box.boxables[pos].id, box.boxables[pos].name));
      row.push(Box.utils.hyperlinkifyBoxable(box.boxables[pos].name, box.boxables[pos].id, box.boxables[pos].alias));
      array.push(row);
    }

    jQuery('#listingBoxablesTable').dataTable({
      "aaData" : array,
      "aoColumns" : [
        { "sTitle" : "Position" },
        { "sTitle" : "Element Name" },
        { "sTitle" : "Element Alias" }
      ],
      "bJQueryUI" : true,
      "bDestroy": true,
      "aLengthMenu": [[Box.boxJSON.size.columns*Box.boxJSON.size.rows, 50, 25, 10],
                      [Box.boxJSON.size.columns*Box.boxJSON.size.rows, 50, 25, 10]],
      "iDisplayLength": Box.boxJSON.size.columns*Box.boxJSON.size.rows,
      "sDom": '<l<"#toolbar">f>r<t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
      "aaSorting": [
        [0, "asc"]
      ]
    }).css("width", "100%");
    jQuery("#toolbar").append('<button style=\"margin-left:5px;\" class=\"fg-button ui-state-default ui-corner-all\" id="listAllItems" onclick="Box.ui.filterTableByColumn(\'#listingBoxablesTable\', \'\', 0);">List all Box Contents</button>');
  },

  // filters the table using a given (String) filter and column
  filterTableByColumn: function (table, filter, col) {
    var t = jQuery(table).dataTable();
    t.fnFilter(filter, col);
  },

  editBoxIdBarcode: function (span, id) {
    Fluxion.doAjax(
      'loggedActionService',
      'logAction',
      {
        'objectId': id,
        'objectType': 'Box',
        'action': 'editBoxIdBarcode',
        'url': ajaxurl
      },
      {}
    );

    var v = span.find('a').text();
    if (v && v !== "") {
      span.html("<input type='text' value='" + v + "' name='identificationBarcode' id='identificationBarcode'>");
    }
  },

  showBoxIdBarcodeChangeDialog: function (boxId, boxIdBarcode) {
    var self = this;
    jQuery('#changeBoxIdBarcodeDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<strong><label>Current Barcode: </label></strong>" + boxIdBarcode +
            "<br /><strong><label for='notetext'>New Barcode:</label></strong>" +
            "<input type='text' name='idBarcodeInput' id='idBarcodeInput' class='text ui-widget-content ui-corner-all' />" +
            "</fieldset></form>");

    jQuery('#changeBoxIdBarcodeDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Save": function () {
          self.changeBoxIdBarcode(boxId, jQuery('#idBarcodeInput').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  changeBoxIdBarcode: function (boxId, idBarcode) {
    Fluxion.doAjax(
      'boxControllerHelperService',
      'changeBoxIdBarcode',
      {
        'boxId': boxId,
        'identificationBarcode': idBarcode,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  editBoxLocationBarcode: function (span) {
    var v = span.find('a').text();
    span.html("<input type='text' value='" + v + "' name='locationBarcode' id='locationBarcode'>");
  },

  showBoxLocationChangeDialog: function (boxId, boxLocationBarcode) {
    var self = this;
    jQuery('#changeBoxLocationDialog')
      .html("<form>" +
            "<fieldset class='dialog'>" +
            "<strong><label>Current Location: </label></strong>" + boxLocationBarcode +
            "<br/><strong><label for='notetext'>New Location:</label>" +
            "<input type='text' name='locationBarcodeInput' id='locationBarcodeInput' class='text ui-widget-content ui-corner-all'/>" +
            "</fieldset></form>");

    jQuery('#changeBoxLocationDialog').dialog({
      width: 400,
      modal: true,
      resizable: false,
      buttons: {
        "Save": function () {
          self.changeBoxLocation(boxId, jQuery('#locationBarcodeInput').val());
          jQuery(this).dialog('close');
        },
        "Cancel": function () {
          jQuery(this).dialog('close');
        }
      }
    });
  },

  changeBoxLocation: function (boxId, barcode) {
    Fluxion.doAjax(
      'boxControllerHelperService',
      'changeBoxLocation',
      {
        'boxId': boxId,
        'locationBarcode': barcode,
        'url': ajaxurl
      },
      {
        'doOnSuccess': Utils.page.pageReload
      }
    );
  },

  exportBox: function (boxId) {
    Fluxion.doAjax(
      'boxControllerHelperService',
      'exportBoxContentsForm',
      {
        'boxId': boxId,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function (json) {
          Utils.page.pageRedirect('/miso/download/box/forms/' + json.response);
        }
      }
    );
  },
  
  addItemToBox: function() {
    var barcode = jQuery('#selectedBarcode').val();
    if (!jQuery('#selectedPosition').text()) {
      jQuery('#warningMessages').html("Please select a position from the grid, then rescan the barcode.");
      return null;
    }
    if (Utils.validation.isNullCheck(barcode)) {
      alert("Please enter a barcode.");
    } else {
      var selectedBarcode = jQuery('#selectedBarcode').val().trim();
      var selectedPosition = Box.utils.getPositionString(Box.visual.selected.row, Box.visual.selected.col);
      // if selectedPosition is already filled, confirm before deleting that position
      if (Box.boxJSON.boxables[selectedPosition] && Box.boxJSON.boxables[selectedPosition]["identificationBarcode"] != selectedBarcode) {
        var sampleInfo = Box.boxJSON.boxables[selectedPosition]["name"]+"::"+ Box.boxJSON.boxables[selectedPosition]["alias"];
        if(confirm(sampleInfo + " is already located at position " + selectedPosition + ". Are you sure you wish to remove it from the box?")) {
          delete Box.boxJSON.boxables[selectedPosition];
        } else {
          return null;
        }
      }
  
      jQuery('#updateSelected, #emptySelected, #removeSelected').prop('disabled', true).addClass('disabled');
      
      Fluxion.doAjax(
        'boxControllerHelperService',
        'updateOneItem',
        {
          'boxJSON': Box.boxJSON,
          'barcode': selectedBarcode,
          'position': selectedPosition,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function(json) {
            Box.boxJSON = JSON.parse(json.boxJSON);
            Box.update();
            console.log(json);
            jQuery('#updateSelected, #emptySelected, #removeSelected').prop('disabled', false).removeClass('disabled');
        },
          'doOnError': function (json) {
            alert(json.error);
            jQuery('#selectedBarcode').val(Box.boxJSON.boxables[selectedPosition].identificationBarcode);
          }
        }
      );  
    }
  },
  
  removeOneItem: function() {
    var selectedBarcode = jQuery('#selectedBarcode').val().trim();
    var selectedPosition = Box.utils.getPositionString(Box.visual.selected.row, Box.visual.selected.col);
  
    if (confirm("Are you sure you wish to set location to unknown for " + Box.boxJSON.boxables[selectedPosition].name + "? You should re-home it as soon as possible")) {
      jQuery('#updateSelected, #emptySelected, #removeSelected').prop('disabled', true).addClass('disabled');
      
      Fluxion.doAjax(
        'boxControllerHelperService',
        'removeTubeFromBox',
        {
          'boxJSON': Box.boxJSON,
          'position': selectedPosition,
          'url': ajaxurl
        },
        {
          'doOnSuccess': Utils.page.pageReload,
          'doOnError': function (json) {
            alert(json.error);
            jQuery('#updateSelected, #emptySelected, #removeSelected').prop('disabled', false).removeClass('disabled');
            jQuery('#selectedBarcode').val(Box.boxJSON.boxables[selectedPosition].identificationBarcode);
          }
        }
      );  
    } 
  },
  
  emptyOneItem: function() {
    var selectedPosition = Box.utils.getPositionString(Box.visual.selected.row, Box.visual.selected.col);
    if(confirm("Are you sure you wish to trash this tube?")) {
      jQuery('#updateSelected, #emptySelected, #removeSelected').prop('disabled', true).addClass('disabled');
      
      Fluxion.doAjax(
        'boxControllerHelperService',
        'emptySingleTube',
        {
          'boxId': Box.boxId,
          'position': selectedPosition,
          'url': ajaxurl
        },
        {
          'doOnSuccess': Utils.page.pageReload,
          'doOnError': function (json) { 
            alert(json.error); 
            jQuery('#updateSelected, #emptySelected, #removeSelected').prop('disabled', false).removeClass('disabled');
          }
        }
      );
    } 
  },

  emptyEntireBox: function (boxId) {
    if(confirm("Are you sure you wish to trash all tubes in this box?")) {      
      Fluxion.doAjax(
        'boxControllerHelperService',
        'emptyEntireBox',
        {
          'boxId': boxId,
          'url': ajaxurl
        },
        {
          'doOnSuccess': Utils.page.pageReload,
          'doOnError': function (json) { 
            alert(json.error); 
          }
        }
      );
    }
  }
};

Box.barcode = {
  printBoxBarcodes: function () {
    var boxes = [];
    for (var i = 0; i < arguments.length; i++) {
      boxes[i] = {'boxId': arguments[i]};
    }

    Fluxion.doAjax(
      'printerControllerHelperService',
      'listAvailableServices',
      {
        'serviceClass': 'uk.ac.bbsrc.tgac.miso.core.data.Box',
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
                  'boxControllerHelperService',
                  'printBoxBarcodes',
                  {
                    'serviceName': jQuery('#serviceSelect').val(),
                    'boxes': boxes,
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
