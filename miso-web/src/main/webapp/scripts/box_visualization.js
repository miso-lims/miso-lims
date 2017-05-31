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

var BoxItem = function(opts) {
  var self = {};
  self.title = opts.title || '';
  self.selInfo = opts.selected;
  self.row = opts.row || null;
  self.col = opts.col || null;
  self.selectedImg = opts.selectedImg || null;
  self.unselectedImg = opts.unselectedImg || null;
  self.element = opts.element || jQuery('<img>');

  self.setImage = opts.setImage || function(imgurl) {
    self.element.prop('src', imgurl);
  };

  self.select = opts.select || function() {
    self.setImage(self.selectedImg);
    self.selected = true;
  };

  self.unselect = opts.unselect || function() {
    self.setImage(self.unselectedImg);
    self.selected = false;
  };

  self.normalClick = function() {
    if (self.selInfo.item !== null) {
      self.selInfo.item.unselect();
    }
    self.selInfo.item = self;
    self.select();
  };

  self.clearSelectedItems = function() {
    for (var i = 0; i < self.selInfo.items.length; i++) {
      var item = self.selInfo.items[i];
      item.unselect();
    }
    self.selInfo.items = [];
  };

  self.controlClick = function() {
    // Clear a selected item before ctrl-clicking begins
    if (self.selInfo.item !== null) {
      self.selInfo.item.unselect();
      self.selInfo.item = null;
    }

    if (jQuery.inArray(self, self.selInfo.items) === -1) {
      self.selInfo.items.push(self);
      self.select();
      //jQuery('#multiAdd').attr('style', '');
      //jQuery('#multiAdd').focus();
      //jQuery('#multiAdd').val(jQuery('#multiAdd').val()+self.title+'\t');
    }
  };

  self.click = function(event) {
    self.selInfo.row = event.data.row;
    self.selInfo.col = event.data.col;

    if (event.ctrlKey) {
      self.controlClick();
    } else {
      self.clearSelectedItems();
      self.normalClick();
    }
    opts.onClick();
  };

  self.click = opts.click || self.click;
  self.element.click({'row': self.row, 'col': self.col}, self.click);
  self.selected ? self.select() : self.unselect();
  return self;
};


/*
 * A BoxPosition represents a single position (cell) in the Box visualization (table).
 * It is responsible for handling the functionality of a cell. This includes changing the title, click event and image
 * associated with the position.
 *
 * See below for examples of usage of BoxPosition.
 */
var BoxPosition = function(opts) {
  var self = {};

  self.item = opts.boxItem || null;

  self.clear = function() {
    jQuery(self.cell).empty();
  };

  self.click = function(event) {
    self.item.click(event);
  };

  self.addItem = function(boxItem) {
    self.item = boxItem;
    self.cell.append(boxItem.element);
  };

  self.select = function() {
    self.item.select();
  };

  self.unselect = function() {
    self.item.unselect();
  };

  self.setTitle = function() {
    self.cell.prop('title', Box.utils.getPositionString(self.row, self.col) + ': ' + self.item.title);
  };

  self.cell = opts.cell;
  if (typeof self.item !== 'undefined') {
    self.addItem(self.item);
  }
  self.row = opts.row || null;
  self.col = opts.col || null;
  self.setTitle();
  return self;
};


var BoxVisual = function() {
  var self = {};

  self.selected = {
    item: null,
    items: [],
    row: null,
    col: null
  };

  self.clear = function() {
    jQuery(self.div).empty();
  };

  self.create = function(opts) {
    self.data = opts.data;
    self.size = opts.size;
    self.div = opts.div;
    self.table = self.div+'Visualization';
    self.position = new Array(self.size.rows+1);

    for (var i = 1; i <= self.size.rows; i++) {
      self.position[i] = new Array(self.size.cols+1);
    }

    self.clear();
    var table = jQuery('<table>');
    table.attr('id', self.table.substring(1));
    table.attr('style', 'border:1px solid darkgrey;background:lightgrey;margin:20px;padding:10px;');
    jQuery(self.div).append(table);

    var tBody = jQuery('<tBody>');
    var tRow = jQuery('<tr>');
    var tCell = jQuery('<td>');
    tCell.css('width', '30px');
    tCell.css('height', '30px');
    tRow.append(tCell);

    for (var col = 1; col <= self.size.cols; col++) {
      tCell = jQuery('<td>').text(col).css('font-weight', 'bold');
      tCell.css('width', '30px');
      tCell.css('text-align', 'center');
      tRow.append(tCell);
    }
    tBody.append(tRow);

    for (var row = 1; row <= self.size.rows; row++) {
      tRow = jQuery('<tr>');
      tRow.attr('id', self.div.substring(1)+'Row'+row);
      tCell = jQuery('<td>');
      tCell.css('height','30px');
      tCell.css('text-align','center');
      tCell.text(Box.utils.getRowLetter(row)).css('font-weight','bold');
      tRow.append(tCell);

      for (col = 1; col <= self.size.cols; col++) {
        tCell = jQuery('<td>');
        tCell.addClass('Col'+col);
        self.position[row][col] = self.getBoxPosition(row, col, tCell);
        tRow.append(self.position[row][col].cell);
      }
      tBody.append(tRow);
    }
    jQuery(self.table).append(tBody);
    jQuery('#updateSelected, #removeSelected, #emptySelected').prop('disabled', true).addClass('disabled');
  };

  self.getBoxPosition = function(row, col, tCell) {
    // Override this method
    return new BoxPosition({
      row: row,
      col: col,
      cell: tCell,
      boxItem: self.getBoxItem(row, col)
    });
  };

  self.click = function(row, col) {
    var event = {
      data: {
        'row': row,
        'col': col
      }
    };
    self.position[row][col].click(event);
  };
  return self;
};


var Box = Box || {};

Box.DialogVisual = function(scan) {
  var self = new BoxVisual();
  self.scan = scan;

  self.show = function(opts) {
    jQuery('#dialogInfoAbove').html(opts.infoabove);
    jQuery('#dialogInfoBelow').html(opts.infobelow);
    jQuery('#dialogDialog').dialog({
      autoOpen: false,
      title: 'Scan',
      width: Box.dialogWidth,
      modal: true,
      resizable: false,
      position: [jQuery(window).width()/2 - Box.dialogWidth/2, 50],
      buttons: {}
    });
    self.create({
      div: '#dialogVisual',
      size: {
        rows: self.size.rows,
        cols: self.size.cols
      },
      data: self.scan.items
    });
    jQuery('#updateSelected, #removeSelected, #emptySelected').prop('disabled', true).addClass('disabled');
    jQuery('#dialogDialog').dialog('open');
  };

  self.getBoxItem = function(row, col) {
    var pos = Box.utils.getPositionString(row, col);
    var boxables;

    if (jQuery.inArray(pos, self.scan.readErrorPositions) !== -1) {
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: 'Read Error',
        selectedImg: '/styles/images/tube_error.png',
        unselectedImg: '/styles/images/tube_error.png'
      });
    }
    boxables = self.data.filter(function(item) { return item.coordinates == pos; });
    if (boxables.length > 0) {
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: boxables[0].alias,
        selectedImg: '/styles/images/tube_full_selected.png',
        unselectedImg: '/styles/images/tube_full.png',
        onClick: function() {}
      });
    } else {
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: 'Empty',
        selectedImg: '/styles/images/tube_empty_selected.png',
        unselectedImg: '/styles/images/tube_empty.png',
        onClick: function() {}
      });
    }
  };

  self.getBoxPosition = function(row, col, tCell) {
    return new BoxPosition({
      row: row,
      col: col,
      cell: tCell,
      boxItem: self.getBoxItem(row, col)
    });
  };
  return self;
};

Box.Visual = function() {
  var self = new BoxVisual();

  //@Override
  self.getBoxItem = function(row, col) {
    var pos = Box.utils.getPositionString(row, col);
    var boxables;
    boxables = self.data.filter(function(item) { return item.coordinates == pos; });
    if (boxables.length > 0) {
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: boxables[0].alias,
        selectedImg: '/styles/images/tube_full_selected.png',
        unselectedImg: '/styles/images/tube_full.png',
        onClick: function() {
          jQuery('#selectedPosition').text(pos);
          jQuery('#selectedName').text(boxables[0].name);
          jQuery('#selectedAlias').html(Box.utils.hyperlinkifyBoxable(boxables[0].name, boxables[0].id, boxables[0].alias));
          jQuery('#selectedName').html(Box.utils.hyperlinkifyBoxable(boxables[0].name, boxables[0].id, boxables[0].name));
          jQuery('#selectedBarcode').val(boxables[0].identificationBarcode);
          jQuery('#selectedBarcode').select().focus();
          Box.ui.filterTableByColumn('#listingBoxablesTable', Box.utils.getPositionString(row, col), 0);
          jQuery('#removeSelected, #emptySelected').prop('disabled', false).removeClass('disabled');
          jQuery('#currentLocation, #currentLocationText, #warningMessages').html('');
        }
      });
    } else {
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: 'Empty',
        selectedImg: '/styles/images/tube_empty_selected.png',
        unselectedImg: '/styles/images/tube_empty.png',
        onClick: function() {
          jQuery('#selectedPosition').text(Box.utils.getPositionString(row, col));
          jQuery('#selectedName').empty();
          jQuery('#selectedAlias').empty();
          jQuery('#selectedBarcode').val('');
          jQuery('#selectedBarcode').focus();
          jQuery('#updateSelected, #removeSelected, #emptySelected').prop('disabled', true).addClass('disabled');
          jQuery('#currentLocation, #currentLocationText, #warningMessages').html('');
        }
      });
    }
  };

  //@Override
  self.getBoxPosition = function(row, col, tCell) {
    return new BoxPosition({
      row: row,
      col: col,
      cell: tCell,
      boxItem: self.getBoxItem(row, col)
    });
  };
  return self;
};


Box.ScanDialog = function() {
  var self = new BoxVisual();

  self.show = function(opts) {
    self.size = opts.size;
    self.data = opts.data;

    self.getNewPosition = function () {
      // Ignore all the magic numbers
      var h = Box.boxJSON.rows*30 - 100;
      var w = Box.dialogWidth-400;
      return [Math.floor(Math.random() * h)+100, Math.floor(Math.random() * w)+100];
    };

    self.animateMag = function () {
      var newpos = self.getNewPosition();
      var oldpos = jQuery('#magnify').offset();
      var speed = self.getSpeed([oldpos.top, oldpos.left], newpos);
      jQuery('#magnify').animate({ top: newpos[0], left: newpos[1] }, speed, function() {
        self.animateMag();
      });
    };

    self.getSpeed = function (prev, next) {
      var x = Math.abs(prev[1] - next[1]);
      var y = Math.abs(prev[0] - next[0]);
      var greatest = x > y ? x : y;
      var speedModifier = 0.1;
      return Math.ceil(greatest/speedModifier);
    };

    jQuery('#dialogInfoAbove').html('<h1>Place box on scanner</h1><br>'+
      '<img id="magnify" src="/styles/images/magnifying_glass.png" style="position:absolute;"></img>');
    jQuery('#dialogInfoBelow').html('<p>Please place the box on the scanner. The box will be scanned automatically.</p>');
    jQuery('#magnify').offset({'top': self.getNewPosition()[0], 'left': self.getNewPosition()[1]});
    self.animateMag();
    jQuery('#dialogDialog').dialog({
      autoOpen: false,
      title: 'Scan',
      width: Box.dialogWidth,
      height: Box.dialogHeight,
      modal: true,
      resizable: false,
      position: [jQuery(window).width()/2 - Box.dialogWidth/2, 50],
      buttons: {}
    });

    // Generate visual
    self.create({
      div: '#dialogVisual',
      size: {
        rows: self.size.rows,
        cols: self.size.cols
      },
      data: self.data
    });
    jQuery('#updateSelected, #removeSelected, #emptySelected').prop('disabled', true).addClass('disabled');
    jQuery('#dialogDialog').dialog('open');

    // Initiate Scan
    Box.scan.scanBox();
  };

  self.getBoxItem = function(row, col) {
    return new BoxItem({
      row: row,
      col: col,
      selected: self.selected,
      title: 'Empty',
      selectedImg: '/styles/images/tube_empty_selected.png',
      unselectedImg: '/styles/images/tube_empty.png',
      onClick: function() {}
    });
  };

  self.getBoxPosition = function(row, col, tCell) {
    return new BoxPosition({
      row: row,
      col: col,
      cell: tCell,
      boxItem: self.getBoxItem(row, col)
    });
  };

  self.error = function(message) {
    jQuery('#dialogInfoBelow').html('');
    jQuery('#dialogInfoAbove').html('<h1 class="warning">Error: Scanner did not detect a box</h1>'+
                                    '<p>'+message+'</p><br>');
    jQuery('#dialogVisual').html('');
    jQuery('#dialogDialog').dialog({
      autoOpen: false,
      title: 'Scan',
      width: Box.dialogWidth,
      modal: true,
      height: "auto",
      position: [jQuery(window).width()/2 - Box.dialogWidth/2, 50],
      buttons: {
        "Retry": function () {
          Box.initScan();
        },
        "Cancel": function() {
          jQuery('#dialogDialog').dialog('close');
        }
      }
    });
    jQuery('#dialogDialog').dialog('open');
  };

  return self;
};

Box.ScanDiff = function() {
  var self = new BoxVisual();

  self.show = function(results) {
    self.results = results;

    var diffs = results.diffs.map(function(d) {
      switch (d.action) {
        case 'added':
          return '<li style="color:green;"><b>+</b> ' + d.modified.name + ' added to the box at position ' + d.coordinates + '</li>';
        case 'removed':
          return '<li style="color:red;"><b>-</b> ' + d.original.name + ' removed from ' + d.coordinates + '</li>';
        case 'changed':
          return '<li style="color:orange;"><b>!</b> ' + d.original.name + ' replaced by ' + d.modified.name + ' at ' + d.coordinates + '</li>';
        default:
          return '<li><b>?</b> Unknown change at ' + (d.coordinates || 'unknown position') + '</li>';
      }
    });

    var message = (results.errors.length == 0 ? "<h1>Scan Success! </h1>" : '<h1 class="warning">Scan Failed!</h1>') +
        results.errors.map(function(err) { return "<p>Position " + err.coordinates + ": " + err.message + "</p>"; }).join("") +
        (diffs.length == 0 ? "" : "<p>Box has changed. See below.</p>");

    jQuery('#dialogInfoAbove').html(message);
    jQuery('#dialogInfoBelow').html("<ul style=\"list-style-type: none;overflow:hidden; overflow-y:scroll;height:125px;\">" + diffs.join("") + '</ul>');

    jQuery('#dialogDialog').dialog({
      autoOpen: false,
      title: 'Scan Results',
      width: Box.dialogWidth,
      modal: true,
      resizable: false,
      position: [jQuery(window).width() / 2 - Box.dialogWidth / 2, 50],
      buttons: {
        "Save": function() {
          if (results.errors.length == 0 || confirm("Do you want to save changes even though there are scanning errors?")) {
            Box.saveContents(self.results.items);
            jQuery('#dialogDialog').dialog('close');
          }
        },
        "Rescan": function() {
          Box.initScan();
        },
        "Cancel": function() {
          jQuery('#dialogDialog').dialog('close');
        }
      }
    });
    self.create({
      div: '#dialogVisual',
      size: {
        rows: Box.boxJSON.rows,
        cols: Box.boxJSON.cols
      },
      data: self.results.items
    });
    jQuery('#updateSelected, #removeSelected, #emptySelected').prop('disabled', true).addClass('disabled');
    jQuery('#dialogDialog').dialog('open');
  };

  self.getBoxItem = function(row, col) {
    var pos = Box.utils.getPositionString(row, col);
    var sel, unsel, title;

    var boxables = self.results.items.filter(function(item) { return item.coordinates == pos; });
    var diffs = self.results.diffs.filter(function(item) { return item.coordinates == pos; });
    var errors = self.results.errors.filter(function(item) { return item.coordinates == pos; });
    if (errors.length > 0) {
      title = errors[0].message;
      sel = '/styles/images/tube_error.png';
      unsel = '/styles/images/tube_error.png';
    } else if (diffs.length > 0) {
      title = (diffs[0].modified ? diffs[0].modified.alias : 'Empty') + " (Previously " + (diffs[0].original ? diffs[0].original.alias : 'Empty') + ")";
      sel = diffs[0].modified ? '/styles/images/tube_full_selected_changed.png' : '/styles/images/tube_empty_selected_changed.png';
      unsel = diffs[0].modified ? '/styles/images/tube_full_changed.png' : '/styles/images/tube_empty_changed.png';
    } else if (boxables.length > 0) {
      title = boxables[0].alias;
      sel = '/styles/images/tube_full_selected.png';
      unsel = '/styles/images/tube_full.png';
    } else {
      title = 'Empty';
      sel = '/styles/images/tube_empty_selected.png';
      unsel = '/styles/images/tube_empty.png';
    }
    return new BoxItem({
      row: row,
      col: col,
      selected: self.selected,
      title: title,
      selectedImg: sel,
      unselectedImg: unsel,
      onClick: function() {}
    });
  };

  self.getBoxPosition = function(row, col, tCell) {
    return new BoxPosition({
      row: row,
      col: col,
      cell: tCell,
      boxItem: self.getBoxItem(row, col)
    });
  };

  return self;
};

Box.PrepareScannerDialog = function() {
  var self = {};

  self.show = function() {
    jQuery('#dialogInfoAbove').html('<h1>Preparing scanner</h1>');
    jQuery('#dialogVisual').html('');
    jQuery('#dialogInfoBelow').html('<p>Please remove box from scanner until prompted.</p>' +
                                     '<img class="center" src="/styles/images/ajax-loader.gif"/>');   
    jQuery('#dialogDialog').dialog({
      autoOpen: false,
      title: 'Scan',
      width: Box.dialogWidth,
      height: "auto",
      modal: true,
      resizable: false,
      position: [jQuery(window).width()/2 - Box.dialogWidth/2, 50],
      buttons: {}
    });
    jQuery('#dialogDialog').dialog('open');
    Box.scan.prepareScanner(Box.boxJSON.rows, Box.boxJSON.cols);
  };

  self.error = function() {
    jQuery('#dialogInfoAbove').html('<h1 class="warning">Error: could not find the scanner</h1>');
    jQuery('#dialogVisual').html('');
    jQuery('#dialogInfoBelow').html('<p>Please ensure that the scanner software is running, ' +
                                    'and remove the box before retrying.</p>');
    jQuery('#dialogDialog').dialog({
      autoOpen: true,
      title: 'Scan',
      width: Box.dialogWidth,
      height: "auto",
      modal: true,
      resizable: false,
      position: [jQuery(window).width()/2 - Box.dialogWidth/2, 50],
      buttons: {
        "Retry": function() {
          Box.initScan();
        },
        "Cancel": function(){ jQuery('#dialogDialog').dialog('close'); }
      }
    });
    jQuery('#dialogDialog').dialog('open');
  };
  return self;
};

Box.utils = {
  getRowLetter: function(row) {
    return String.fromCharCode('A'.charCodeAt(0)-1+row);
  },

  getPositionString: function(row, col) {
    var pos = Box.utils.getRowLetter(row);
    if (col < 10) {
      pos += 0;
    }
    pos += col;
    return pos;
  },

  hyperlinkify: function(path, text) {
    return "<a href=\""+path+"\">"+text+"</a>";
  },

  hyperlinkifyBoxable: function(name, id, text) {
    var path = "/miso/";
    var prefix = name.substring(0,3);
    if (prefix == "SAM" || prefix == "LIB" || prefix.substring(1,3) == "PO") {
      if (prefix == "SAM") {
        path += "sample/";
      } else if (prefix == "LIB") {
        path += "library/";
      } else {
        path += "pool/";
      }
      path += id;
      return Box.utils.hyperlinkify(path, text);
    } else {
      return text;
    }
  },

  getRowFromStringPos: function(pos) {
    return pos.charCodeAt(0) - 'A'.charCodeAt(0) + 1;
  },

  getColFromStringPos: function(pos) {
    return parseInt(pos.substring(1));
  },

  // Return the position of a boxable item given its id in a box else return null
  findItemPos: function(name, boxables) {
    var matches = boxables.filter(function(item) { return item.name == name; });
    return matches.length == 1 ? mathces[0] : null;
  }
};

