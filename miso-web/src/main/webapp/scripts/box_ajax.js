var Box = Box || {
  boxJSON: null,
  originalBox: null,
  boxId: null,
  visual: null,

  // Start the scanning process
  fakeScan: function() {
    var dialog = jQuery('#dialogDialog');
    jQuery('#dialogInfoBelow').html('');
    jQuery('#dialogInfoAbove').html('<p>Please enter the prefix for your bacodes (e.g., MYBOX for MYBOXA01, MYBOXA02, ...). Existing tubes with barcodes matching this pattern will be added to the box in positions depending on the suffix.</p>');
    jQuery('#dialogVisual').html('<p>Prefix: <input id="prefix" type="text"/></p><p>Suffix: <input type="radio" name="suffix" id="standardSuffix" value="standard"/><label for="standardSuffix"> Standard (A01, A02, ... as row letter/column number)</label> <input type="radio" name="suffix" id="numericSuffix" value="numeric"/><label for="numericSuffix"> Numeric (001, 002, ... moving in rows)</label></p>');
    dialog.dialog({
      autoOpen: true,
      width: 500,
      height: 400,
      modal: true,
      resizable: false,
      title: 'Fill by Barcode Pattern',
      position: [jQuery(window).width()/2 - 400/2, 50],
      buttons: {
        "Fill": function () {
          var prefix = document.getElementById('prefix').value;
          var suffix = jQuery('input[name="suffix"]:checked').val() || 'standard';
          if (!prefix || !(new RegExp(Utils.validation.sanitizeRegex).test(prefix))) {
            alert('Not a valid prefix.');
            return;
          }
          dialog.dialog('close');
          jQuery('#dialogVisual').html('<p>Saving...</p>');
          jQuery('#dialogInfoAbove').html('');
          dialog.dialog({
            autoOpen: true,
            width: 400,
            height: 300,
            modal: true,
            resizable: false,
            position: [jQuery(window).width()/2 - 400/2, 50],
            buttons: {}
          });
          Fluxion.doAjax(
            'boxControllerHelperService',
            'recreateBoxFromPrefix',
            {
              'boxId': Box.boxJSON.id,
              'prefix': prefix,
              'suffix': suffix,
              'url': ajaxurl
            },
            {
              'doOnSuccess':  Utils.page.pageReload,
              'doOnError': function(json) {
                alert("Error saving box contents: " + json.error);
                dialog.dialog('close');
              }
            }
          );
        },
        "Cancel": function() {
          dialog.dialog('close');
        }
      }
    });
  },
  // Start the scanning process
  initScan: function() {
    Box.dialogWidth = Box.boxJSON.cols * 40 + 150;
    Box.dialogHeight = Box.boxJSON.rows * 40 + 300;
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
        rows: Box.boxJSON.rows,
        cols: Box.boxJSON.cols
      },
      data: Box.boxJSON.items
    });
    if (selected) {
      Box.visual.click(selected.row, selected.col);
    }
  },

  createListingTable: function() {
    Box.ui.createListingBoxablesTable(Box.boxJSON);
  },

  // Saves entire box (stored in Box.boxJSON)
  saveContents: function(items) {
    Box.boxJSON.items = items;
    Fluxion.doAjax(
      'boxControllerHelperService',
      'saveBoxContents',
      {
        'boxId': Box.boxJSON.id,
        'items': items,
        'url': ajaxurl
      },
      {
        'doOnSuccess': function() { Box.update(); },
        'doOnError': function(json) { alert("Error saving box contents: "+json.error); }
      }
    );
  },

  deleteBox: function () {
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
      jQuery('#warningMessages').html("Please select a single position from the grid, then rescan the barcode."); 
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
            jQuery('#updateSelected').focus();
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
    var prepareScannerTimeout = setTimeout(Box.prepareScannerDialog.error, 10000); // otherwise box scanner may poll indefinitely
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
              rows: Box.boxJSON.rows,
              cols: Box.boxJSON.cols
            },
            data: Box.boxJSON.items
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
          Box.scanDiff.show(json);
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
    table.fnFilter(alias, 5);
  },

  //creates the table of the box contents
  createListingBoxablesTable: function(box) {
    jQuery('#listingBoxablesTable').empty();
    var array = box.items.map(function(item) { return [
      item.coordinates,
      Box.utils.hyperlinkifyBoxable(item.name, item.id, item.name),
      Box.utils.hyperlinkifyBoxable(item.name, item.id, item.alias)
    ]; });
    jQuery('#listingBoxablesTable').dataTable({
      "aaData" : array,
      "aoColumns" : [
        { "sTitle" : "Position" },
        { "sTitle" : "Element Name" },
        { "sTitle" : "Element Alias" }
      ],
      "bJQueryUI" : true,
      "bDestroy": true,
      "aLengthMenu": [[Box.boxJSON.cols*Box.boxJSON.rows, 50, 25, 10],
                      [Box.boxJSON.cols*Box.boxJSON.rows, 50, 25, 10]],
      "iDisplayLength": Box.boxJSON.cols*Box.boxJSON.rows,
      "sPaginationType" : "full_numbers",
      "sDom": '<"#toolbar.fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"lf>r<t><"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
      "aaSorting": [
        [0, "asc"]
      ]
    }).css("width", "100%");
    jQuery("#toolbar").append('<button style=\"margin-left:5px;\" class=\"fg-button ui-state-default ui-corner-all\" id="listAllItems" onclick="Box.ui.filterTableByBoxPositions();">List all Box Contents</button>');
    Box.ui.getBulkActions();
  },
  
  filterTableByBoxPositions: function (positionStrings) {
    var t = jQuery('#listingBoxablesTable').dataTable();
    var filter = '';
    if (positionStrings && positionStrings.length > 0) {
      filter = '^(?:' + positionStrings.join('|') + ')$';
    }
    t.fnFilter(filter, 0, true);
    Box.ui.getBulkActions(positionStrings);
  },
  
  getBulkActions: function(positionStrings) {
    function addToolbarMemo(memo) {
      // empty all previous bulk actions and/or info
      Box.ui.createToolbarIfNecessary();
      var toolbar = jQuery('#listingBoxablesToolbar');
      toolbar.empty();
      jQuery('<span/>', {
          'text': memo
      }).appendTo(toolbar);
    }
    
    var items;
    if (positionStrings) {
      items = Box.visual.data.filter(function(item) { return positionStrings.indexOf(item.coordinates) >= 0 });
    } else {
      // if no items are selected, determine bulk actions based on the entity types of all box contents
      items = Box.visual.data;
    }
    if (items.length < 1) {
      if (Box.visual.data.length) {
        // there are items in the box but an empty position is selected
        addToolbarMemo("Select one or more itmes to see bulk actions.");
        return;
      } else {
        // empty box
        addToolbarMemo("Add items to box to see bulk actions.");
        return;
      }
    }
    
    
    var entityTypes = Utils.array.deduplicateString(items.map(function(item) {
      return item.entityType;
    }));
    
    if (entityTypes.length > 1) {
      if (positionStrings) {
        // heterogenous items are selected
        addToolbarMemo("Selection contains multiple types of tubes. Select tubes of the same type to see bulk actions.");
        return;
      } else {
        // no items are selected, but box contains heterogenous items
        addToolbarMemo("Box contains multiple types of tubes. Select tubes of same type to see bulk actions.");
        return;
      }
    }
    
    var actions = [];
    switch (entityTypes[0]) {
    case 'SAMPLE':
      actions = HotTarget.sample.bulkActions;
      break;
    case 'LIBRARY':
      actions = HotTarget.library.bulkActions;
      break;
    case 'DILUTION':
      actions = HotTarget.dilution.bulkActions;
      break;
    case 'POOL':
      actions = HotTarget.pool.bulkActions;
      break;
    }
    
    Box.ui.refreshToolbar(actions, items);
  },
  
  createToolbarIfNecessary: function() {
    var toolbar = jQuery('#listingBoxablesToolbar');
    if (!toolbar.length) {
      toolbar = jQuery('<div />', {
        id: 'listingBoxablesToolbar',
        'class': 'fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix paging_full_numbers'
      });
      toolbar.insertBefore(jQuery('#toolbar'));
    }
  },
  
  refreshToolbar: function(actions, items) {
    Box.ui.createToolbarIfNecessary();
    var toolbar = jQuery('#listingBoxablesToolbar');
    toolbar.empty();
    actions.forEach(function(action) {
      var button = jQuery('<a />', {
        'class': 'ui-button ui-state-default',
        title: action.title || '',
        text: action.name
      });
      button.click(function() {
        action.action(items);
      });
      button.appendTo(toolbar);
    });
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

  getItemAtPosition: function(coordinates) {
    var selected = Box.boxJSON.items.filter(function(item) { return item.coordinates == coordinates; });
    return selected.length == 1 ? selected[0] : null;
  },
  
  addItemToBox: function() {
    var barcode = jQuery('#selectedBarcode').val();
    if (!jQuery('#selectedPosition').text()) {
      jQuery('#warningMessages').html("Please select a single position from the grid, then rescan the barcode.");
      return null;
    }
    if (Utils.validation.isNullCheck(barcode)) {
      alert("Please enter a barcode.");
    } else {
      var selectedBarcode = jQuery('#selectedBarcode').val().trim();
      if (Box.visual.selectedItems.length !== 1) {
        alert('Select a single location to add the sample to');
        return;
      }
      var selectedPosition = Box.utils.getPositionString(Box.visual.selectedItems[0].row, Box.visual.selectedItems[0].col);
      var selectedItem = Box.ui.getItemAtPosition(selectedPosition);
      // if selectedPosition is already filled, confirm before deleting that position
      if (selectedItem && selectedItem.identificationBarcode != selectedBarcode) {
        var sampleInfo = selectedItem.name +"::"+ selectedItem.alias;
        if(confirm(sampleInfo + " is already located at position " + selectedPosition + ". Are you sure you wish to remove it from the box?")) {
          Box.boxJSON.items = Box.boxJSON.items.filter(function(item) { return item.coordinates != selectedPosition; });
        } else {
          return null;
        }
      }
  
      jQuery('#updateSelected, #emptySelected, #removeSelected').prop('disabled', true).addClass('disabled');
      jQuery('#warningMessages').html('<img id="ajaxLoader" src="/styles/images/ajax-loader.gif" alt="Loading" />');
      
      Fluxion.doAjax(
        'boxControllerHelperService',
        'updateOneItem',
        {
          'boxId': Box.boxId,
          'barcode': selectedBarcode,
          'position': selectedPosition,
          'url': ajaxurl
        },
        {
          'doOnSuccess': function(json) {
            Box.boxJSON = JSON.parse(json.boxJSON);
            Box.update();
            Box.ui.getBulkActions();
            console.log(json);
            jQuery('#updateSelected, #emptySelected, #removeSelected').prop('disabled', false).removeClass('disabled');
            jQuery('#ajaxLoader').remove();
        },
          'doOnError': function (json) {
            alert(json.error);
            jQuery('#selectedBarcode').val(selectedItem.identificationBarcode);
            jQuery('#ajaxLoader').remove();
          }
        }
      );
    }
  },
  
  removeOneItem: function() {
    if (Box.visual.selectedItems.length !== 1) {
      alert('Select a single tube to remove');
      return;
    }
    var selectedPosition = Box.utils.getPositionString(Box.visual.selectedItems[0].row, Box.visual.selectedItems[0].col);
    var selectedItem = Box.ui.getItemAtPosition(selectedPosition);
  
    if (confirm("Are you sure you wish to set location to unknown for " + selectedItem.name + "? You should re-home it as soon as possible")) {
      jQuery('#updateSelected, #emptySelected, #removeSelected').prop('disabled', true).addClass('disabled');
      jQuery('#warningMessages').html('<img id="ajaxLoader" src="/styles/images/ajax-loader.gif" alt="Loading" />');
      
      Fluxion.doAjax(
        'boxControllerHelperService',
        'removeTubeFromBox',
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
            jQuery('#selectedBarcode').val(selectedItem.identificationBarcode);
          }
        }
      );  
    } 
  },
  
  discardOneItem: function() {
    if (Box.visual.selectedItems.length !== 1) {
      alert('Select a single tube to discard');
      return;
    }
    var selectedPosition = Box.utils.getPositionString(Box.visual.selectedItems[0].row, Box.visual.selectedItems[0].col);
    if(confirm("Are you sure you wish to discard this tube?")) {
      jQuery('#updateSelected, #emptySelected, #removeSelected').prop('disabled', true).addClass('disabled');
      
      Fluxion.doAjax(
        'boxControllerHelperService',
        'discardSingleTube',
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

  discardEntireBox: function (boxId) {
    if(confirm("Are you sure you wish to discard all tubes in this box?")) {      
      Fluxion.doAjax(
        'boxControllerHelperService',
        'discardEntireBox',
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
                    'printerId': jQuery('#serviceSelect').val(),
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
