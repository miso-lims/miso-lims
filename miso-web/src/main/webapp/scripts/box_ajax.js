var Box = Box || {
  boxJSON: null,
  originalBox: null,
  boxId: null,
  visual: null,

  // Start the scanning process
  fakeScan: function () {
    var dialog = jQuery("#dialogDialog");
    jQuery("#dialogInfoBelow").html("");
    jQuery("#dialogInfoAbove").html(
      "<p>Please enter the prefix for your bacodes (e.g., MYBOX for MYBOXA01, MYBOXA02, ...). Existing items with barcodes matching this pattern will be added to the box in positions depending on the suffix.</p>"
    );
    jQuery("#dialogVisual").html(
      '<p>Prefix: <input id="prefix" type="text"/></p><p>Suffix: <input type="radio" name="suffix" id="standardSuffix" value="standard"/><label for="standardSuffix"> Standard (A01, A02, ... as row letter/column number)</label> <input type="radio" name="suffix" id="numericSuffix" value="numeric"/><label for="numericSuffix"> Numeric (001, 002, ... moving in rows)</label></p>'
    );
    dialog.dialog({
      autoOpen: true,
      width: 500,
      height: 400,
      modal: true,
      resizable: false,
      title: "Fill by Barcode Pattern",
      position: [jQuery(window).width() / 2 - 400 / 2, 50],
      buttons: {
        Fill: function () {
          var prefix = document.getElementById("prefix").value;
          var suffix = jQuery('input[name="suffix"]:checked').val() || "standard";
          if (!prefix || !new RegExp(Utils.validation.sanitizeRegex).test(prefix)) {
            Utils.showOkDialog("Invalid prefix", [
              (prefix ? prefix + " is n" : "N") + "ot a valid prefix.",
            ]);
            return;
          }
          dialog.dialog("close");
          jQuery("#dialogVisual").html("<p>Saving...</p>");
          jQuery("#dialogInfoAbove").html("");
          dialog.dialog({
            autoOpen: true,
            width: 400,
            height: 300,
            modal: true,
            resizable: false,
            position: [jQuery(window).width() / 2 - 400 / 2, 50],
            buttons: {},
          });
          var url =
            Urls.rest.boxes.fillByPattern(Box.boxJSON.id) +
            "?" +
            Utils.page.param({
              prefix: prefix,
              suffix: suffix,
            });
          Utils.ajaxWithDialog("Filling by Pattern", "POST", url, null, function () {
            Utils.page.pageReload();
          });
        },
        Cancel: function () {
          dialog.dialog("close");
        },
      },
    });
  },
  // Start the scanning process
  initScan: function (scannerName) {

    var dialog = jQuery("#dialogDialog");
    jQuery("#dialogInfoAbove").html("<h1>Choose Scan Mode</h1>");
    jQuery("#dialogInfoBelow").html("<p>Do you want to update item locations or assign barcodes?</p>");
    jQuery("#dialogVisual").html("");
    dialog.dialog({
        autoOpen: true,
        title: "Scan Options",
        modal: true,
        width: 400,
        height: 200,
        buttons: {
            "Update Locations": function(){
                dialog.dialog("close");
                Box.startUpdateLocationsScan(scannerName);
            },
            "Assign Barcodes": function(){
                dialog.dialog("close");
                Box.startAssignBarcodesScan(scannerName);
            },
            Cancel: function(){
                dialog.dialog("close");
            }
        }
    });
  },

  startUpdateLocationsScan: function(scannerName){
    Box.dialogWidth = Box.boxJSON.cols * 40 + 150;
    Box.dialogHeight = Box.boxJSON.rows * 40 + 300;
    Box.updateLocationsScanDialog = Box.UpdateLocationsScanDialog(scannerName);
    Box.scanDiff = Box.ScanDiff(scannerName);

    var onPrepareSuccess = function () {
        Box.updateLocationsScanDialog.show({
          size: {
            rows: Box.boxJSON.rows,
            cols: Box.boxJSON.cols,
          },
          data: Box.boxJSON.items,
        });
    }
    Box.prepareScannerDialog = Box.PrepareScannerDialog(scannerName, onPrepareSuccess);
    Box.prepareScannerDialog.show();
  },

  startAssignBarcodesScan: function(scannerName){
    Box.dialogWidth = Box.boxJSON.cols * 40 + 150;
    Box.dialogHeight = Box.boxJSON.rows * 40 + 300;

    Box.assignBarcodesScanProgressDialog = Box.AssignBarcodesScanProgressDialog(scannerName);
    Box.assignBarcodesScanDialog = Box.AssignBarcodesScanDialog(scannerName);

    var onPrepareSuccess = function () {

        Box.assignBarcodesScanProgressDialog.show({
        size:{
                rows: Box.boxJSON.rows,
                cols: Box.boxJSON.cols,
            },
            data: Box.boxJSON.items,
        });
    };

      Box.prepareScannerDialog = Box.PrepareScannerDialog(scannerName, onPrepareSuccess);
      Box.prepareScannerDialog.show();
  },

  createVisualization: function () {
    Box.visual.setDisabled(false);
    var selected =
      Box.visual.selectedItems && Box.visual.selectedItems.length === 1
        ? Box.visual.selectedItems[0]
        : null;
    Box.visual.create({
      div: "#boxContentsTable",
      size: {
        rows: Box.boxJSON.rows,
        cols: Box.boxJSON.cols,
      },
      data: Box.boxJSON.items,
    });
    if (selected) {
      Box.visual.selectPos(selected.row, selected.col);
    }
  },

  // Saves entire box (stored in Box.boxJSON)
  saveContents: function (items, emptyPositions) {
    var data = items
      .map(function (item) {
        return {
          position: item.coordinates,
          searchString: item.identificationBarcode,
        };
      })
      .concat(
        emptyPositions.map(function (position) {
          return {
            position: position,
            searchString: null,
          };
        })
      );
    var url = Urls.rest.boxes.updateContents(Box.boxJSON.id);

    jQuery
      .ajax({
        url: url,
        type: "POST",
        contentType: "application/json; charset=utf8",
        data: JSON.stringify(data),
      })
      .done(function (data) {
        Box.boxJSON.items = items;
        Box.ui.update();
      })
      .fail(function (xhr, textStatus, errorThrown) {
        Utils.showAjaxErrorDialog(xhr, textStatus, errorThrown);
      });
  },
};

Box.scan = {
  prepareScanner: function (scannerName, boxRows, boxColumns, onSuccess) {
    var prepareScannerTimeout = setTimeout(Box.prepareScannerDialog.error, 10000); // otherwise box scanner may poll indefinitely

    jQuery
      .ajax({
        url: Urls.rest.boxes.prepareScan,
        type: "POST",
        contentType: "application/json; charset=utf8",
        data: JSON.stringify({
          scannerName: scannerName,
          rows: boxRows,
          columns: boxColumns,
        }),
      })
      .done(function (data) {
        clearTimeout(prepareScannerTimeout);
        jQuery("#dialogDialog").dialog("close");
        onSuccess();
      })
      .fail(function (response, textStatus, serverStatus) {
        clearTimeout(prepareScannerTimeout);
        Box.prepareScannerDialog.error();
      });
  },

  scanBox: function (scannerName) {
    jQuery
      .ajax({
        url: Urls.rest.boxes.updateLocationsScan(Box.boxJSON.id),
        type: "POST",
        contentType: "application/json; charset=utf8",
        data: JSON.stringify({
          scannerName: scannerName,
        }),
      })
      .done(function (data) {
        jQuery("#magnify").stop();
        Box.scanDiff.show(data);
      })
      .fail(function (response, textStatus, serverStatus) {
        jQuery("#magnify").stop();
        jQuery("#dialogDialog").dialog("close");
        var error =
          response && response.responseText && response.responseText.detail
            ? response.responseText.detail
            : "Scan failed";
        Box.updateLocationsScanDialog.error(error);
      });
  },

  scanAssignBarcodes: function (scannerName, onSuccess, onError) {
    jQuery
      .ajax({
        url: Urls.rest.boxes.assignBarcodesScan(Box.boxJSON.id),
        type: "POST",
        contentType: "application/json; charset=utf8",
        data: JSON.stringify({ scannerName : scannerName})
      })
      .done(function (data) {
        jQuery("#magnify").stop();
        Box.assignBarcodesScanDialog.show(data);
      })
      .fail(function (response, textStatus, serverStatus) {
        jQuery("#magnify").stop();
        jQuery("#dialogDialog").dialog("close");
        var error =
          response && response.responseText && response.responseText.detail
            ? response.responseText.detail
            : "Scan failed";
            Box.assignBarcodesScanProgressDialog.error(error);
      });
  },
};

Box.ui = {
  update: function () {
    Box.ui.createListingBoxablesTable(Box.boxJSON);
    Box.createVisualization();
    Utils.ui.setDisabled(
      "#updateSelected, #removeSelected, #emptySelected, #resultSelect, #search, #searchField",
      true
    );
  },

  onSelectionChanged: function (items, singleBoxable) {
    jQuery("#singlePositionControls").hide();
    jQuery("#bulkPositionControls").hide();

    var positions = items.map(function (item) {
      return Box.utils.getPositionString(item.row, item.col);
    });
    Box.ui.filterTableByBoxPositions(positions);
    Box.ui.clearBoxableSearchResults();

    if (singleBoxable) {
      // filled position selected
      jQuery("#singlePositionControls").show();
      jQuery("#selectedPosition").text(positions[0]);
      jQuery("#selectedName").text(singleBoxable.name);
      if (singleBoxable.identificationBarcode) {
        jQuery("#selectedBarcode").text(singleBoxable.identificationBarcode);
      } else {
        jQuery("#selectedBarcode").empty();
      }
      jQuery("#selectedAlias").html(
        Box.utils.hyperlinkifyBoxable(singleBoxable.name, singleBoxable.id, singleBoxable.alias)
      );
      jQuery("#selectedName").html(
        Box.utils.hyperlinkifyBoxable(singleBoxable.name, singleBoxable.id, singleBoxable.name)
      );
      jQuery("#removeSelected, #emptySelected, #searchField, #search")
        .prop("disabled", false)
        .removeClass("disabled");
    } else {
      // empty position, no positions, or multiple positions selected
      if (positions.length > 1) {
        Box.ui.showBulkUpdateTable(positions);
      } else if (positions.length === 1) {
        jQuery("#singlePositionControls").show();
        jQuery("#selectedPosition").text(positions[0]);
      } else {
        jQuery("#selectedPosition").empty();
        jQuery("#search, #searchField, #resultSelect, #updateSelected")
          .prop("disabled", true)
          .addClass("disabled");
      }
      jQuery("#selectedName").empty();
      jQuery("#selectedAlias").empty();
      jQuery("#selectedBarcode").empty();
      jQuery("#updateSelected, #removeSelected, #emptySelected")
        .prop("disabled", true)
        .addClass("disabled");
    }
    jQuery("#warningMessages").html("");
    jQuery("#searchField").val("");
    jQuery("#searchField").select().focus();
  },

  showBulkUpdateTable: function (positions) {
    jQuery("#bulkUpdateTable tbody").empty();
    jQuery("#bulkPositionControls").show();

    for (var i = 0; i < positions.length; i++) {
      var pos = positions[i];
      var tr = jQuery("<tr>");
      tr.append(jQuery("<td>" + pos + "</td>"));
      var td = jQuery("<td>");
      var input = jQuery('<input type="text" class="bulkUpdateInput"/>');
      input.data("position", pos);
      if (i < positions.length - 1) {
        input.keyup(
          (function (event) {
            var index = i;
            return function (event) {
              if (event.which == "13") {
                jQuery("#bulkUpdateTable tbody input:eq(" + (index + 1) + ")").focus();
              }
            };
          })()
        );
        input.on(
          "paste",
          (function () {
            var index = i;
            return function (e) {
              for (
                var clipEvent = e;
                clipEvent.originalEvent && clipEvent.type == "paste";
                clipEvent = clipEvent.originalEvent
              );
              var lines = clipEvent.clipboardData
                ? clipEvent.clipboardData.getData("Text").split(/\r?\n/)
                : [];
              if (lines.length > 1) {
                for (var next = 0; next < lines.length; next++) {
                  var inputBox = jQuery("#bulkUpdateTable tbody input:eq(" + next + ")");
                  inputBox.val(lines[next].replace(/^\s*|\s*$/g, ""));
                }
                jQuery("#bulkUpdateTable tbody input:eq(" + next + ")").focus();
                e.preventDefault();
                return;
              }
              window.setTimeout(function () {
                jQuery("#bulkUpdateTable tbody input:eq(" + (index + 1) + ")").focus();
              }, 100);
            };
          })()
        );
      } else {
        input.keyup(function (event) {
          if (event.which == "13") {
            jQuery("#bulkUpdate").click();
          }
        });
      }
      td.append(input);
      tr.append(td);
      jQuery("#bulkUpdateTable tbody").append(tr);
      if (i === 0) {
        input.focus();
      }
    }
    jQuery("#bulkUpdateInput").click(function () {
      $(this).select();
    });
  },

  changeBoxListing: function (alias) {
    var table = jQuery("#listingBoxesTable").dataTable();
    table.fnFilter(alias, 5);
  },

  bulkRemoveItems: function () {
    Utils.showConfirmDialog(
      "Remove Items",
      "Remove",
      [
        "Are you sure you wish to set location to unknown for all selected items? You should " +
          "re-home them as soon as possible.",
      ],
      function () {
        var positions = Box.ui.getSelectedPositions();
        var url = Urls.rest.boxes.removeContents(Box.boxJSON.id);
        Utils.ajaxWithDialog("Remove Items", "POST", url, positions, function (responseData) {
          Box.boxJSON = responseData;
          Box.ui.update();
        });
      }
    );
  },

  bulkDiscardItems: function () {
    Utils.showConfirmDialog(
      "Discard Items",
      "Discard",
      ["Are you sure you wish to set discard all selected items?"],
      function () {
        var positions = Box.ui.getSelectedPositions();
        var url = Urls.rest.boxes.discardContents(Box.boxJSON.id);
        Utils.ajaxWithDialog("Discard Items", "POST", url, positions, function (responseData) {
          Box.boxJSON = responseData;
          Box.ui.update();
        });
      }
    );
  },

  getSelectedPositions: function () {
    return jQuery.map(jQuery("#bulkUpdateTable tbody input"), function (input) {
      return jQuery(input).data("position");
    });
  },

  bulkUpdatePositions: function () {
    var data = [];
    var inputs = jQuery("#bulkUpdateTable tbody input");
    inputs.each(function (index, input) {
      data.push({
        position: jQuery(input).data("position"),
        searchString: jQuery(input).val(),
      });
    });
    var positions = data.map(function (item) {
      return item.position;
    });
    var currentOccupants = Box.boxJSON.items.filter(function (boxable) {
      return positions.indexOf(boxable.coordinates) >= 0;
    });
    var doUpdate = function () {
      var url = Urls.rest.boxes.updateContents(Box.boxJSON.id);
      Utils.ajaxWithDialog("Update Positions", "POST", url, data, function (responseData) {
        Box.boxJSON = responseData;
        Box.ui.update();
      });
    };
    if (currentOccupants.length) {
      var lines = ["Some of these positions already contain other items:"];
      currentOccupants.forEach(function (item) {
        lines.push("* " + item.coordinates + ": " + item.name + " (" + item.alias + ")");
      });
      lines.push("Are you sure you wish to replace them?");
      Utils.showConfirmDialog("Replace Items", "Replace", lines, doUpdate);
    } else {
      doUpdate();
    }
  },

  // creates the table of the box contents
  createListingBoxablesTable: function (box) {
    jQuery("#listingBoxablesTable").empty();
    var array = box.items.map(function (item) {
      return [
        item.coordinates,
        Box.utils.hyperlinkifyBoxable(item.name, item.id, item.name),
        Box.utils.hyperlinkifyBoxable(item.name, item.id, item.alias),
      ];
    });
    jQuery("#listingBoxablesTable")
      .dataTable({
        aaData: array,
        aoColumns: [
          {
            sTitle: "Position",
          },
          {
            sTitle: "Element Name",
          },
          {
            sTitle: "Element Alias",
          },
        ],
        bJQueryUI: true,
        bDestroy: true,
        aLengthMenu: [
          [Box.boxJSON.cols * Box.boxJSON.rows, 50, 25, 10],
          [Box.boxJSON.cols * Box.boxJSON.rows, 50, 25, 10],
        ],
        iDisplayLength: Box.boxJSON.cols * Box.boxJSON.rows,
        sPaginationType: "full_numbers",
        sDom: '<"#toolbar.fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"lf>r<t><"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
        aaSorting: [[0, "asc"]],
      })
      .css("width", "100%");
    jQuery("#toolbar").append(
      '<button style="margin-left:5px;" class="fg-button ui-state-default ui-corner-all" id="listAllItems" onclick="Box.ui.selectNone();">List all Box Contents</button>'
    );
    Box.ui.getBulkActions();
  },

  selectNone: function () {
    Box.visual.clearSelection();
    Box.ui.filterTableByBoxPositions();
    Box.ui.onSelectionChanged([]);
  },

  filterTableByBoxPositions: function (positionStrings) {
    var t = jQuery("#listingBoxablesTable").dataTable();
    var filter = "";
    if (positionStrings && positionStrings.length > 0) {
      filter = "^(?:" + positionStrings.join("|") + ")$";
    }
    t.fnFilter(filter, 0, true);
    Box.ui.getBulkActions(positionStrings);
  },

  getBulkActions: function (positionStrings) {
    var items;
    if (positionStrings) {
      items = Box.boxJSON.items.filter(function (item) {
        return positionStrings.indexOf(item.coordinates) >= 0;
      });
    } else {
      // if no items are selected, determine bulk actions based on the entity
      // types of all box contents
      items = Box.boxJSON.items;
    }

    var actions = [
      {
        name: "Print Barcodes by Position",
        action: function () {
          // Ignore items; it's a mess of different object types

          var additionalFields = [
            {
              label: "Print by",
              type: "select",
              property: "order",
              required: true,
              values: ["column", "row"],
            },
          ];
          Utils.printSelectDialog(function (printer, copies, result) {
            var input =
              items.length == 0
                ? Box.boxJSON.items.map(function (i) {
                    return i.coordinates;
                  })
                : positionStrings;
            Utils.ajaxWithDialog(
              "Printing",
              "POST",
              Urls.rest.printers.printBoxPositions(printer),
              {
                boxId: Box.boxId,
                positions: input,
                copies: copies,
                sortOrder: result.order,
              },
              function (result) {
                Utils.showOkDialog("Printing", [
                  result == input.length
                    ? "Barcodes sent to printer."
                    : result + " of " + input.length + " printed.",
                ]);
              }
            );
          }, additionalFields);
        },
      },
    ];

    if (items.length < 1) {
      if (Box.boxJSON.items.length) {
        // there are items in the box but an empty position is selected
        Box.ui.refreshToolbar(actions, items, "Select one or more items to see bulk actions.");
        return;
      } else {
        // empty box
        Box.ui.refreshToolbar([], items, "Add items to box to see bulk actions.");
        return;
      }
    }

    if (items.length == 1) {
      actions = [];
    }

    var entityTypes = Utils.array.deduplicateString(
      items.map(function (item) {
        return item.entityType;
      })
    );

    if (entityTypes.length > 1) {
      if (positionStrings) {
        // heterogenous items are selected
        Box.ui.refreshToolbar(
          actions,
          items,
          "Selection contains multiple types of items. Select items of the same type to see bulk actions."
        );
        return;
      } else {
        // no items are selected, but box contains heterogenous items
        Box.ui.refreshToolbar(
          actions,
          items,
          "Box contains multiple types of items. Select items of same type to see bulk actions."
        );
        return;
      }
    }

    switch (entityTypes[0]) {
      case "SAMPLE":
        actions = actions.concat(
          BulkTarget.sample.getBulkActions({
            sortLibraryPropagate: "sampleBoxColumn",
          })
        );
        break;
      case "LIBRARY":
        actions = actions.concat(BulkTarget.library.getBulkActions({}));
        break;
      case "LIBRARY_ALIQUOT":
        actions = actions.concat(BulkTarget.libraryaliquot.getBulkActions({}));
        break;
      case "POOL":
        actions = actions.concat(BulkTarget.pool.getBulkActions({}));
        break;
    }

    Box.ui.refreshToolbar(actions, items, null);
  },

  createToolbarIfNecessary: function () {
    var toolbar = jQuery("#listingBoxablesToolbar");
    if (!toolbar.length) {
      toolbar = jQuery("<div />", {
        id: "listingBoxablesToolbar",
        class:
          "fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix paging_full_numbers",
      });
      toolbar.insertBefore(jQuery("#toolbar"));
    }
  },

  refreshToolbar: function (actions, items, memo) {
    Box.ui.createToolbarIfNecessary();
    var toolbar = jQuery("#listingBoxablesToolbar");
    toolbar.empty();
    actions.forEach(function (action) {
      var button;
      if (action) {
        button = Utils.ui.makeBulkActionButton(action, function () {
          return items;
        });
      } else {
        button = jQuery("<span />", {
          class: "ui-state-default",
        });
      }
      button.appendTo(toolbar);
    });

    if (memo) {
      jQuery("<span/>", {
        text: memo,
      }).appendTo(toolbar);
    }
  },

  exportBox: function (boxId) {
    jQuery
      .ajax({
        url: Urls.rest.boxes.spreadsheet(boxId),
        type: "GET",
        contentType: "application/json; charset=utf8",
        dataType: "json",
      })
      .done(function (json) {
        // REST endpoint will return a JSON object with the spreadsheet filename's hashCode inside
        // Send the hashCode to the DownloadController to download the spreadsheet
        Utils.page.pageRedirect(Urls.download.boxSpreadsheet(json.hashCode));
      });
  },

  exportFragmentAnalyser: function (boxId) {
    if (
      Box.boxJSON.items.some(function (item) {
        return item.coordinates === "H12";
      })
    ) {
      Utils.showConfirmDialog(
        "Warning",
        "OK",
        [
          "The item in position H12 will not be included in the sheet because Fragment Analyser requires H12 for ladder. Generate sheet anyway?",
        ],
        function () {
          Utils.ajaxDownloadWithDialog(Urls.rest.boxes.fragmentAnalyserSheet(boxId));
        }
      );
    } else {
      Utils.ajaxDownloadWithDialog(Urls.rest.boxes.fragmentAnalyserSheet(boxId));
    }
  },

  getItemAtPosition: function (coordinates) {
    var selected = Box.boxJSON.items.filter(function (item) {
      return item.coordinates == coordinates;
    });
    return selected.length == 1 ? selected[0] : null;
  },

  addItemToBox: function () {
    if (Box.visual.selectedItems.length !== 1) {
      jQuery("#warningMessages").html("Please select a single position from the grid, then retry.");
      return;
    }
    if (jQuery("#resultSelect").val() == -1) {
      jQuery("#warningMessages").html("Please select an item to add.");
      return;
    }

    var selectedPosition = Box.utils.getPositionString(
      Box.visual.selectedItems[0].row,
      Box.visual.selectedItems[0].col
    );
    var selectedItem = Box.ui.getItemAtPosition(selectedPosition);

    var addTheItem = function () {
      jQuery("#emptySelected, #removeSelected").prop("disabled", true).addClass("disabled");
      Box.ui.showBoxableSearchLoading();

      jQuery
        .ajax({
          url:
            Urls.rest.boxes.updatePosition(Box.boxId, selectedPosition) +
            "?" +
            Utils.page.param({
              entity: jQuery("#resultSelect").val(),
            }),
          type: "PUT",
          dataType: "json",
          contentType: "application/json; charset=utf8",
        })
        .done(function (data) {
          Box.boxJSON = data;
          Box.ui.update();
          Box.ui.getBulkActions();
          Box.ui.clearBoxableSearchResults();
          jQuery("#searchField").val("");
          jQuery("#emptySelected, #removeSelected").prop("disabled", false).removeClass("disabled");
        })
        .fail(function (response, textStatus, serverStatus) {
          var error = JSON.parse(response.responseText);
          var message = error.detail ? error.detail : error.message;
          jQuery("#warningMessages").html("Error adding item: " + message);
          jQuery("#ajaxLoader").addClass("hidden");
          jQuery("#searchField, #search, #resultSelect, #updateSelected")
            .prop("disabled", false)
            .removeClass("disabled");
          Box.visual.setDisabled(false);
          if (Box.visual.selectedItems.length === 1) {
            jQuery("#emptySelected, #removeSelected")
              .prop("disabled", false)
              .removeClass("disabled");
          }
        });
    };

    if (selectedItem) {
      var selectedEntity = selectedItem.entityType + ":" + selectedItem.id;
      if (selectedEntity === jQuery("#resultSelect").val()) {
        // setting same item where it already is. No change necessary
        Box.ui.clearBoxableSearchResults();
        jQuery("#searchField").val("");
        return;
      }
      // if selectedPosition is already filled, confirm before deleting that position
      var sampleInfo = selectedItem.name + " (" + selectedItem.alias + ")";
      Utils.showConfirmDialog(
        "Replace Item",
        "Replace",
        [
          sampleInfo +
            " is already located at position " +
            selectedPosition +
            ". Are you sure you wish to replace it?",
          "If so, you should re-home " + selectedItem.name + " as soon as possible.",
        ],
        addTheItem
      );
    } else {
      addTheItem();
    }
  },

  removeOneItem: function () {
    if (Box.visual.selectedItems.length !== 1) {
      Utils.showOkDialog("Too many items selected", ["Select a single item to remove."]);
      return;
    }
    var selectedPosition = Box.utils.getPositionString(
      Box.visual.selectedItems[0].row,
      Box.visual.selectedItems[0].col
    );
    var selectedItem = Box.ui.getItemAtPosition(selectedPosition);

    var removeIt = function () {
      jQuery("#updateSelected, #emptySelected, #removeSelected")
        .prop("disabled", true)
        .addClass("disabled");
      jQuery("#warningMessages").html(
        '<img id="ajaxLoader" src="/styles/images/ajax-loader.gif" alt="Loading" />'
      );

      var url = Urls.rest.boxes.removePosition(Box.boxJSON.id, selectedPosition);
      Utils.ajaxWithDialog(
        "Remove item",
        "DELETE",
        url,
        null,
        function () {
          Utils.page.pageReload();
        },
        function () {
          jQuery("#updateSelected, #emptySelected, #removeSelected")
            .prop("disabled", false)
            .removeClass("disabled");
          jQuery("#selectedBarcode").val(selectedItem.identificationBarcode);
        }
      );
    };

    Utils.showConfirmDialog(
      "Remove Item",
      "Remove",
      [
        "Are you sure you wish to set location to unknown for " +
          selectedItem.name +
          "? You should re-home it as soon as possible.",
      ],
      removeIt
    );
  },

  discardOneItem: function () {
    if (Box.visual.selectedItems.length !== 1) {
      Utils.showOkDialog("Too many items selected", ["Select a single item to discard"]);
      return;
    }

    var selectedPosition = Box.utils.getPositionString(
      Box.visual.selectedItems[0].row,
      Box.visual.selectedItems[0].col
    );

    var discardIt = function () {
      jQuery("#updateSelected, #emptySelected, #removeSelected")
        .prop("disabled", true)
        .addClass("disabled");
      jQuery("#warningMessages").html(
        '<img id="ajaxLoader" src="/styles/images/ajax-loader.gif" alt="Loading" />'
      );

      var url = Urls.rest.boxes.discardPosition(Box.boxJSON.id, selectedPosition);
      Utils.ajaxWithDialog(
        "Discard item",
        "POST",
        url,
        null,
        function () {
          Utils.page.pageReload();
        },
        function () {
          jQuery("#updateSelected, #emptySelected, #removeSelected")
            .prop("disabled", false)
            .removeClass("disabled");
        }
      );
    };

    Utils.showConfirmDialog(
      "Discard Item",
      "Discard",
      ["Are you sure you wish to discard this item?"],
      discardIt
    );
  },

  discardAllContents: function (boxId) {
    var url = Urls.rest.boxes.discardAll(boxId);
    var discardBox = function () {
      Utils.ajaxWithDialog("Discard All Contents", "POST", url, null, function () {
        Utils.page.pageReload();
      });
    };
    Utils.showConfirmDialog(
      "Discard All Contents",
      "Discard",
      ["Are you sure you wish to discard all contents of this box?"],
      discardBox
    );
  },

  searchBoxables: function () {
    var searchString = jQuery("#searchField").val();
    if (!searchString) {
      Box.ui.clearBoxableSearchResults();
      return;
    }
    Box.ui.showBoxableSearchLoading();
    var url = Urls.rest.boxables.search + "?q=" + searchString;
    jQuery
      .ajax({
        url: url,
        contentType: "application/json; charset=utf8",
        dataType: "json",
        type: "GET",
      })
      .done(function (data) {
        Box.ui.showBoxableSearchResults(data);
      })
      .fail(function (response, textStatus, serverStatus) {
        Box.ui.clearBoxableSearchResults();
        var error = JSON.parse(response.responseText);
        var message = error.detail ? error.detail : error.message;
        Utils.showOkDialog("Search error", [message]);
      });
  },

  showBoxableSearchLoading: function () {
    jQuery("#ajaxLoader").removeClass("hidden");
    jQuery("#searchField, #search, #resultSelect, #updateSelected")
      .prop("disabled", true)
      .addClass("disabled");
    Box.visual.setDisabled(true);
  },

  clearBoxableSearchResults: function () {
    Box.ui.showBoxableSearchResults();
  },

  showBoxableSearchResults: function (results) {
    jQuery("#resultSelect").empty();
    jQuery("#warningMessages").html("");

    var focusSelector = null;
    if (!results || !results.length) {
      jQuery("#resultSelect").append('<option value="-1" selected="selected">No results</option>');
      focusSelector = "#searchField";
    } else {
      if (results.length > 1) {
        jQuery("#resultSelect").append('<option value="-1" selected="selected">SELECT</option>');
        focusSelector = "#resultSelect";
      } else {
        focusSelector = "#updateSelected";
      }
      jQuery.each(results, function (index, result) {
        var opt = jQuery("<option>");
        opt.val(result.entityType + ":" + result.id);
        opt.text(result.name + ": " + result.alias);
        jQuery("#resultSelect").append(opt);
      });
      jQuery("#updateSelected").prop("disabled", false).removeClass("disabled");
    }

    jQuery("#ajaxLoader").addClass("hidden");
    jQuery("#searchField, #search, #resultSelect").prop("disabled", false).removeClass("disabled");
    Box.visual.setDisabled(false);
    jQuery(focusSelector).focus();
  },
};
