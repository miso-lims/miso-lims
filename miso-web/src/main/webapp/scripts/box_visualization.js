/*
 * This module provides a visualization for Boxes/Plates/Things that hold other things.
 *
 * A box is a container for tubes containing samples, libraries or both.
 *
 * Boxes vary in dimensions and are labeled like the following example:
 *
 * ('O' represents a tube containing a sample or library)
 *
 *      01 02 03 04 05 06 07 08 09 10 11 12
 *   --------------------------------------
 *   A|  O  O  O  O  O  O  O  O  O  O  O  O
 *   B|  O  O  O  O  O  O  O  O  O  O  O  O
 *   C|  O  O  O  O  O  O  O  O  O  O  O  O
 *   D|  O  O  O  O  O  O  O  O  O  O  O  O
 *   E|  O  O  O  O  O  O  O  O  O  O  O  O
 *   F|  O  O  O  O  O  O  O  O  O  O  O  O
 *   G|  O  O  O  O  O  O  O  O  O  O  O  O
 *   H|  O  O  O  O  O  O  O  O  O  O  O  O
 *
 */

(function (Box, $, undefined) {
  // NOSONAR (paranoid assurance that undefined is undefined)

  function isMultiSelectClick(event) {
    // multi-select is accessible by pressing the Command key (metaKey) on Mac,
    // or the Control key (ctrlKey) on Windows or Linux
    return navigator.platform.indexOf("Mac") != -1 ? event.metaKey : event.ctrlKey;
  }

  /*
   * A BoxPosition represents a single position (cell) in the Box visualization (table). It is responsible for handling the functionality of
   * a cell. This includes changing the title and image associated with the position.
   */
  var BoxPosition = function (opts) {
    var self = {};

    self.title = opts.title || "";
    self.parentVisual = opts.parentVisual;
    self.row = opts.row;
    self.col = opts.col;
    self.cell = opts.cell;
    self.position = self.parentVisual.getPositionString(self.row, self.col);
    self.selectedImg = opts.selectedImg || null;
    self.unselectedImg = opts.unselectedImg || null;
    self.element = opts.element || jQuery("<img>");

    self.setTitle = function () {
      self.cell.prop(
        "title",
        self.parentVisual.getPositionString(self.row, self.col) + ": " + self.title
      );
    };

    self.setImage =
      opts.setImage ||
      function (imgurl) {
        self.element.prop("src", imgurl);
      };

    self.select =
      opts.select ||
      function () {
        self.setImage(self.selectedImg);
      };

    self.unselect =
      opts.unselect ||
      function () {
        self.setImage(self.unselectedImg);
      };

    self.click = function (event) {
      if (self.parentVisual.isMultiSelectEnabled() && isMultiSelectClick(event)) {
        self.controlClick();
      } else {
        self.normalClick();
      }
    };

    self.normalClick = function () {
      self.parentVisual.clearSelection();
      self.parentVisual.select(self);
    };

    self.controlClick = function () {
      if (jQuery.inArray(self, self.parentVisual.selectedItems) === -1) {
        self.parentVisual.select(self);
      } else {
        self.parentVisual.unselect(self);
      }
    };

    self.setTitle();
    self.unselect();
    self.element.click(
      {
        row: self.row,
        col: self.col,
      },
      self.click
    );
    self.cell.append(self.element);

    return self;
  };

  var BoxVisual = function () {
    var self = {};
    var disabled = false;

    self.selectedItems = [];

    self.isMultiSelectEnabled = function () {
      return true;
    };

    self.clearSelection = function () {
      if (disabled) {
        return;
      }
      self.selectedItems.forEach(function (item) {
        item.unselect();
      });
      self.selectedItems = [];
    };

    var callSelectionChanged = function (items) {
      var singleBoxable = null;
      if (items && items.length === 1) {
        var pos = self.getPositionString(items[0].row, items[0].col);
        var boxables = self.data.filter(function (item) {
          return item.coordinates === pos;
        });
        singleBoxable = boxables[0];
      }
      self.onSelectionChanged(items, singleBoxable);
    };

    self.select = function (item, skipCallback) {
      if (disabled) {
        return;
      }
      item.select();
      self.selectedItems.push(item);
      if (!skipCallback) {
        callSelectionChanged(self.selectedItems);
      }
    };

    self.unselect = function (item, skipCallback) {
      if (disabled) {
        return;
      }
      item.unselect();
      self.selectedItems = self.selectedItems.filter(function (selectedItem) {
        return selectedItem !== item;
      });
      if (!skipCallback) {
        callSelectionChanged(self.selectedItems);
      }
    };

    self.clear = function () {
      jQuery(self.div).empty();
    };

    self.create = function (opts) {
      self.data = opts.data;
      self.size = opts.size;
      self.div = opts.div;
      self.table = self.div + "Visualization";
      self.position = new Array(self.size.rows + 1);

      for (var i = 1; i <= self.size.rows; i++) {
        self.position[i] = new Array(self.size.cols + 1);
      }

      self.clear();
      var table = jQuery("<table>");
      table.attr("id", self.table.substring(1));
      table.attr("style", "border:1px solid darkgrey;background:lightgrey;padding:10px;");
      jQuery(self.div).append(table);

      var tBody = jQuery("<tBody>");
      var tRow = jQuery("<tr>");
      var emptyCornerCell = jQuery("<th>");
      emptyCornerCell.addClass("boxVisColHeader boxVisRowHeader");
      tRow.append(emptyCornerCell);

      var makeColSelectEventData = function (col) {
        return {
          col: col,
        };
      };

      var makeRowSelectEventData = function (row) {
        return {
          row: row,
        };
      };

      for (var col = 1; col <= self.size.cols; col++) {
        var thCell = jQuery("<th>").text(self.getColLabel(col));
        thCell.addClass("boxVisColHeader");
        thCell.click(makeColSelectEventData(col), self.selectCol);
        tRow.append(thCell);
      }
      tBody.append(tRow);

      for (var row = 1; row <= self.size.rows; row++) {
        tRow = jQuery("<tr>");
        tRow.attr("id", self.div.substring(1) + "Row" + row);
        var tCell = jQuery("<td>");
        tCell.addClass("boxVisRowHeader");
        tCell.text(self.getRowLabel(row));
        tCell.click(makeRowSelectEventData(row), self.selectRow);
        tRow.append(tCell);

        for (col = 1; col <= self.size.cols; col++) {
          tCell = jQuery("<td>");
          tCell.addClass("Col" + col + " boxVisCell");
          self.position[row][col] = self.getBoxPosition(row, col, tCell);
          tRow.append(self.position[row][col].cell);
        }
        tBody.append(tRow);
      }
      jQuery(self.table).append(tBody);
      self.clearSelection();
      callSelectionChanged(self.selectedItems);
    };

    var toggleGroupSelection = function (event, items) {
      if (!isMultiSelectClick(event)) {
        self.clearSelection();
      }
      var allSelected = true;
      items.forEach(function (item) {
        if (jQuery.inArray(item, self.selectedItems) === -1) {
          self.select(item, true);
          allSelected = false;
        }
      });
      if (isMultiSelectClick(event) && allSelected) {
        for (var i = 0; i < items.length; i++) {
          self.unselect(items[i], true);
        }
      }
      callSelectionChanged(self.selectedItems);
    };

    self.selectAll = function (event) {
      if (!self.isMultiSelectEnabled()) {
        return;
      }
      var items = [];
      for (var row = 1; row <= self.size.rows; row++) {
        for (var col = 1; col <= self.size.cols; col++) {
          items.push(self.position[row][col]);
        }
      }
      items.forEach(function (item) {
        self.select(item, true);
      });
      callSelectionChanged(self.selectedItems);
    };

    self.selectRow = function (event) {
      if (!self.isMultiSelectEnabled()) {
        return;
      }
      var row = event.data.row;
      var items = [];
      for (var col = 1; col <= self.size.cols; col++) {
        items.push(self.position[row][col]);
      }
      toggleGroupSelection(event, items);
    };

    self.selectCol = function (event) {
      if (!self.isMultiSelectEnabled()) {
        return;
      }
      var col = event.data.col;
      var items = [];
      for (var row = 1; row <= self.size.rows; row++) {
        items.push(self.position[row][col]);
      }
      toggleGroupSelection(event, items);
    };

    self.selectOddColumns = function () {
      selectOddOrEvenColumns(true);
    };

    self.selectEvenColumns = function () {
      selectOddOrEvenColumns(false);
    };

    var selectOddOrEvenColumns = function (odd) {
      if (!self.isMultiSelectEnabled()) {
        return;
      }
      self.clearSelection();
      var firstCol = odd ? 1 : 2;
      var items = [];
      for (var col = firstCol; col <= self.size.cols; col += 2) {
        for (var row = 1; row <= self.size.rows; row++) {
          items.push(self.position[row][col]);
        }
      }
      items.forEach(function (item) {
        self.select(item, true);
      });
      callSelectionChanged(self.selectedItems);
    };

    self.selectPos = function (row, col) {
      self.clearSelection();
      self.select(self.position[row][col], false);
    };

    self.getBoxPosition = function (row, col, tCell) {
      var opts = self.getBoxPositionOpts(row, col);
      opts.row = row;
      opts.col = col;
      opts.cell = tCell;
      opts.parentVisual = self;
      return new BoxPosition(opts);
    };

    self.onSelectionChanged = function (items, singleBoxable) {};

    self.setDisabled = function (disable) {
      disabled = disable;
    };

    self.getRowLabel = function (row) {
      return Box.utils.getRowLabel(row);
    };

    self.getColLabel = function (col) {
      return Box.utils.getColLabel(col);
    };

    self.getPositionString = function (row, col) {
      return Box.utils.getPositionString(row, col);
    };

    return self;
  };

  Box.DialogVisual = function (scan) {
    var self = new BoxVisual();
    self.scan = scan;

    self.show = function (opts) {
      jQuery("#dialogInfoAbove").html(opts.infoabove);
      jQuery("#dialogInfoBelow").html(opts.infobelow);
      jQuery("#dialogDialog").dialog({
        autoOpen: false,
        title: "Scan",
        width: Box.dialogWidth,
        modal: true,
        resizable: false,
        position: [jQuery(window).width() / 2 - Box.dialogWidth / 2, 50],
        buttons: {},
      });
      self.create({
        div: "#dialogVisual",
        size: {
          rows: self.size.rows,
          cols: self.size.cols,
        },
        data: self.scan.items,
      });
      jQuery("#updateSelected, #removeSelected, #emptySelected")
        .prop("disabled", true)
        .addClass("disabled");
      jQuery("#dialogDialog").dialog("open");
    };

    self.getBoxPositionOpts = function (row, col) {
      var pos = self.getPositionString(row, col);
      var boxables;

      if (jQuery.inArray(pos, self.scan.readErrorPositions) !== -1) {
        return {
          title: "Read Error",
          selectedImg: "/styles/images/tube_error.png",
          unselectedImg: "/styles/images/tube_error.png",
        };
      }
      boxables = self.data.filter(function (item) {
        return item.coordinates == pos;
      });
      if (boxables.length > 0) {
        return {
          title: boxables[0].alias,
          selectedImg: "/styles/images/tube_full_selected.png",
          unselectedImg: "/styles/images/tube_full.png",
        };
      } else {
        return {
          title: "Empty",
          selectedImg: "/styles/images/tube_empty_selected.png",
          unselectedImg: "/styles/images/tube_empty.png",
        };
      }
    };

    return self;
  };

  Box.Visual = function (selectionChangedCallback) {
    var self = new BoxVisual();

    // @Override
    self.getBoxPositionOpts = function (row, col) {
      var pos = self.getPositionString(row, col);
      var boxables;
      boxables = self.data.filter(function (item) {
        return item.coordinates == pos;
      });
      if (boxables.length > 0) {
        return {
          title: boxables[0].alias,
          selectedImg: "/styles/images/tube_full_selected.png",
          unselectedImg: "/styles/images/tube_full.png",
        };
      } else {
        return {
          title: "Empty",
          selectedImg: "/styles/images/tube_empty_selected.png",
          unselectedImg: "/styles/images/tube_empty.png",
        };
      }
    };

    self.onSelectionChanged =
      selectionChangedCallback ||
      function (items, singleBoxable) {
        // default does nothing
      };

    return self;
  };

  Box.ScanDialog = function (scannerName) {
    var self = new BoxVisual();

    self.show = function (opts) {
      self.size = opts.size;
      self.data = opts.data;

      self.getNewPosition = function () {
        // Ignore all the magic numbers
        var h = Box.boxJSON.rows * 30 - 100;
        var w = Box.dialogWidth - 400;
        return [Math.floor(Math.random() * h) + 100, Math.floor(Math.random() * w) + 100];
      };

      self.animateMag = function () {
        var newpos = self.getNewPosition();
        var oldpos = jQuery("#magnify").offset();
        var speed = self.getSpeed([oldpos.top, oldpos.left], newpos);
        jQuery("#magnify").animate(
          {
            top: newpos[0],
            left: newpos[1],
          },
          speed,
          function () {
            self.animateMag();
          }
        );
      };

      self.getSpeed = function (prev, next) {
        var x = Math.abs(prev[1] - next[1]);
        var y = Math.abs(prev[0] - next[0]);
        var greatest = x > y ? x : y;
        var speedModifier = 0.1;
        return Math.ceil(greatest / speedModifier);
      };

      jQuery("#dialogInfoAbove").html(
        "<h1>Place box on scanner</h1><br>" +
          '<img id="magnify" src="/styles/images/magnifying_glass.png" style="position:absolute;"></img>'
      );
      jQuery("#dialogInfoBelow").html(
        "<p>Please place the box on the scanner. The box will be scanned automatically.</p>"
      );
      jQuery("#magnify").offset({
        top: self.getNewPosition()[0],
        left: self.getNewPosition()[1],
      });
      self.animateMag();
      jQuery("#dialogDialog").dialog({
        autoOpen: false,
        title: "Scan",
        width: Box.dialogWidth,
        height: Box.dialogHeight,
        modal: true,
        resizable: false,
        position: [jQuery(window).width() / 2 - Box.dialogWidth / 2, 50],
        buttons: {},
      });

      // Generate visual
      self.create({
        div: "#dialogVisual",
        size: {
          rows: self.size.rows,
          cols: self.size.cols,
        },
        data: self.data,
      });
      jQuery("#updateSelected, #removeSelected, #emptySelected")
        .prop("disabled", true)
        .addClass("disabled");
      jQuery("#dialogDialog").dialog("open");

      // Initiate Scan
      Box.scan.scanBox(scannerName);
    };

    self.getBoxPositionOpts = function (row, col) {
      return {
        title: "Empty",
        selectedImg: "/styles/images/tube_empty_selected.png",
        unselectedImg: "/styles/images/tube_empty.png",
      };
    };

    self.error = function (message) {
      jQuery("#dialogInfoBelow").html("");
      jQuery("#dialogInfoAbove").html(
        '<h1 class="warning">Error: Scanner did not detect a box</h1>' +
          "<p>" +
          message +
          "</p><br>"
      );
      jQuery("#dialogVisual").html("");
      jQuery("#dialogDialog").dialog({
        autoOpen: false,
        title: "Scan",
        width: Box.dialogWidth,
        modal: true,
        height: "auto",
        position: [jQuery(window).width() / 2 - Box.dialogWidth / 2, 50],
        buttons: {
          Retry: function () {
            Box.initScan();
          },
          Cancel: function () {
            jQuery("#dialogDialog").dialog("close");
          },
        },
      });
      jQuery("#dialogDialog").dialog("open");
    };

    return self;
  };

  Box.ScanDiff = function () {
    var self = new BoxVisual();

    self.show = function (results) {
      self.results = results;

      var diffs = results.diffs.map(function (d) {
        switch (d.action) {
          case "added":
            return (
              '<li style="color:green;"><b>+</b> ' +
              d.modified.name +
              " added to the box at position " +
              d.coordinates +
              "</li>"
            );
          case "removed":
            return (
              '<li style="color:red;"><b>-</b> ' +
              d.original.name +
              " removed from " +
              d.coordinates +
              "</li>"
            );
          case "changed":
            return (
              '<li style="color:orange;"><b>!</b> ' +
              d.original.name +
              " replaced by " +
              d.modified.name +
              " at " +
              d.coordinates +
              "</li>"
            );
          default:
            return (
              "<li><b>?</b> Unknown change at " + (d.coordinates || "unknown position") + "</li>"
            );
        }
      });

      var message =
        (results.errors.length == 0
          ? "<h1>Scan Success! </h1>"
          : '<h1 class="warning">Scan Failed!</h1>') +
        results.errors
          .map(function (err) {
            return "<p>Position " + err.coordinates + ": " + err.message + "</p>";
          })
          .join("") +
        (diffs.length == 0 ? "" : "<p>Box has changed. See below.</p>");

      jQuery("#dialogInfoAbove").html(message);
      jQuery("#dialogInfoBelow").html(
        '<ul style="list-style-type: none;overflow:hidden; overflow-y:scroll;height:125px;">' +
          diffs.join("") +
          "</ul>"
      );

      jQuery("#dialogDialog").dialog({
        autoOpen: false,
        title: "Scan Results",
        width: Box.dialogWidth,
        modal: true,
        resizable: false,
        position: [jQuery(window).width() / 2 - Box.dialogWidth / 2, 50],
        buttons: {
          Save: function () {
            if (
              results.errors.length == 0 ||
              confirm("Do you want to save changes even though there are scanning errors?")
            ) {
              Box.saveContents(results.items, results.emptyPositions);
              jQuery("#dialogDialog").dialog("close");
            }
          },
          Rescan: function () {
            Box.initScan();
          },
          Cancel: function () {
            jQuery("#dialogDialog").dialog("close");
          },
        },
      });
      self.create({
        div: "#dialogVisual",
        size: {
          rows: Box.boxJSON.rows,
          cols: Box.boxJSON.cols,
        },
        data: self.results.items,
      });
      jQuery("#updateSelected, #removeSelected, #emptySelected")
        .prop("disabled", true)
        .addClass("disabled");
      jQuery("#dialogDialog").dialog("open");
    };

    self.getBoxPositionOpts = function (row, col) {
      var pos = self.getPositionString(row, col);
      var sel, unsel, title;

      var boxables = self.results.items.filter(function (item) {
        return item.coordinates == pos;
      });
      var diffs = self.results.diffs.filter(function (item) {
        return item.coordinates == pos;
      });
      var errors = self.results.errors.filter(function (item) {
        return item.coordinates == pos;
      });
      if (errors.length > 0) {
        title = errors[0].message;
        sel = "/styles/images/tube_error.png";
        unsel = "/styles/images/tube_error.png";
      } else if (diffs.length > 0) {
        title =
          (diffs[0].modified ? diffs[0].modified.alias : "Empty") +
          " (Previously " +
          (diffs[0].original ? diffs[0].original.alias : "Empty") +
          ")";
        sel = diffs[0].modified
          ? "/styles/images/tube_full_selected_changed.png"
          : "/styles/images/tube_empty_selected_changed.png";
        unsel = diffs[0].modified
          ? "/styles/images/tube_full_changed.png"
          : "/styles/images/tube_empty_changed.png";
      } else if (boxables.length > 0) {
        title = boxables[0].alias;
        sel = "/styles/images/tube_full_selected.png";
        unsel = "/styles/images/tube_full.png";
      } else {
        title = "Empty";
        sel = "/styles/images/tube_empty_selected.png";
        unsel = "/styles/images/tube_empty.png";
      }
      return {
        title: title,
        selectedImg: sel,
        unselectedImg: unsel,
      };
    };

    return self;
  };

  Box.PrepareScannerDialog = function (scannerName) {
    var self = {};

    self.show = function () {
      jQuery("#dialogInfoAbove").html("<h1>Preparing scanner</h1>");
      jQuery("#dialogVisual").html("");
      jQuery("#dialogInfoBelow").html(
        "<p>Please remove box from scanner until prompted.</p>" +
          '<img class="center" src="/styles/images/ajax-loader.gif"/>'
      );
      jQuery("#dialogDialog").dialog({
        autoOpen: false,
        title: "Scan",
        width: Box.dialogWidth,
        height: "auto",
        modal: true,
        resizable: false,
        position: [jQuery(window).width() / 2 - Box.dialogWidth / 2, 50],
        buttons: {},
      });
      jQuery("#dialogDialog").dialog("open");
      Box.scan.prepareScanner(scannerName, Box.boxJSON.rows, Box.boxJSON.cols);
    };

    self.error = function () {
      jQuery("#dialogInfoAbove").html('<h1 class="warning">Error: could not find the scanner</h1>');
      jQuery("#dialogVisual").html("");
      jQuery("#dialogInfoBelow").html(
        "<p>Please ensure that the scanner software is running, " +
          "and remove the box before retrying.</p>"
      );
      jQuery("#dialogDialog").dialog({
        autoOpen: true,
        title: "Scan",
        width: Box.dialogWidth,
        height: "auto",
        modal: true,
        resizable: false,
        position: [jQuery(window).width() / 2 - Box.dialogWidth / 2, 50],
        buttons: {
          Retry: function () {
            Box.initScan(scannerName);
          },
          Cancel: function () {
            jQuery("#dialogDialog").dialog("close");
          },
        },
      });
      jQuery("#dialogDialog").dialog("open");
    };
    return self;
  };

  Box.utils = {
    hyperlinkify: function (path, text) {
      return '<a href="' + path + '">' + text + "</a>";
    },

    hyperlinkifyBoxable: function (name, id, text) {
      var path = "/miso/";
      var prefix = name.substring(0, 3);
      if (["SAM", "LIB", "LDI", "IPO"].includes(prefix)) {
        if (prefix == "SAM") {
          path += "sample/";
        } else if (prefix == "LIB") {
          path += "library/";
        } else if (prefix == "LDI") {
          path += "libraryaliquot/";
        } else if (prefix == "IPO") {
          path += "pool/";
        }
        path += id;
        return Box.utils.hyperlinkify(path, text);
      } else {
        return text;
      }
    },

    getRowLabel: function (row) {
      return String.fromCharCode("A".charCodeAt(0) - 1 + row);
    },

    getColLabel: function (col) {
      return col >= 10 ? col : "0" + col;
    },

    getPositionString: function (row, col) {
      return Box.utils.getRowLabel(row) + Box.utils.getColLabel(col);
    },
  };
})((window.Box = window.Box || {}), jQuery);
