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
    if (self.selInfo.item == null) {
      self.selInfo.item = self;
      self.select();
      return;
    }

    if (self.selInfo.item == self) {
      self.unselect();
      self.selInfo.item = null;
    } else {
      self.selInfo.item.unselect();
      self.selInfo.item = self;
      self.select();
    }
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
    if (self.selInfo.item != null) {
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

    if (ctrlPressed) {
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
  if (typeof self.item !== 'undefined')
    self.addItem(self.item);
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
    var item = self.getBoxItem(row, col);
    return new BoxPosition({
      row: row,
      col: col,
      cell: tCell,
      boxItem: self.getBoxItem(row, col)
    });
  };

  self.getBoxItem = function(row, col) {
    // Override this method
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
      data: self.scan.boxables
    });
    jQuery('#updateSelected, #removeSelected, #emptySelected').prop('disabled', true).addClass('disabled');
    jQuery('#dialogDialog').dialog('open');
  };

  self.getBoxItem = function(row, col) {
    var pos = Box.utils.getPositionString(row, col);
    var boxable = self.data[pos];

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

    if (typeof boxable !== 'undefined') {
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: boxable.alias,
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
    var boxable = self.data[pos];
    if (typeof boxable !== 'undefined') {
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: boxable.alias,
        selectedImg: '/styles/images/tube_full_selected.png',
        unselectedImg: '/styles/images/tube_full.png',
        onClick: function() {
          jQuery('#selectedPosition').text(pos);
          jQuery('#selectedName').text(boxable.name);
          jQuery('#selectedAlias').html(Box.utils.hyperlinkifyBoxable(boxable.name, boxable.id, boxable.alias));
          jQuery('#selectedName').html(Box.utils.hyperlinkifyBoxable(boxable.name, boxable.id, boxable.name));
          jQuery('#selectedBarcode').val(boxable.identificationBarcode);
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
      var h = Box.boxJSON.size.rows*30 - 100;
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

Box.ScanErrors = function() {
  var self = new BoxVisual();

  self.show = function(opts) {
    self.scan = opts.scan;
    self.size = opts.size;
    self.data = opts.data;

    jQuery('#dialogInfoAbove').html('<h1 class="warning">Scan Failed!</h1>'+
                                    '<p>'+self.scan.errors.message +
                                    '<br>Read error positions:</p><br>');
    jQuery('#dialogInfoBelow').html('');
    jQuery('#dialogDialog').dialog({
      autoOpen: false,
      width: Box.dialogWidth,
      modal: true,
      resizable: false,
      position: [jQuery(window).width()/2 - Box.dialogWidth/2, 50],
      buttons: {
        "Rescan": function () {
          Box.initScan();
        },
        "Cancel": function () {
          jQuery('#dialogDialog').dialog('close');
        }
      }
    });
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
  };

  self.getBoxItem = function(row, col) {
    var pos = Box.utils.getPositionString(row, col);
    var boxable = self.data[pos];
    var img = '/styles/images/tube_error.png';
    if (self.scan.errors.type == 'Unknown Barcode') {
      img = '/styles/images/tube_duplicate_barcode_error.png';
    } 

    if (jQuery.inArray(pos, self.scan.errors.errorPositions) !== -1) {
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: 'Read Error',
        selectedImg: img,
        unselectedImg: img,
        onClick: function() {}
      });
    } else if (jQuery.inArray(pos, self.scan.errors.successPositions) !== -1) {
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: 'Successful Read',
        selectedImg: '/styles/images/tube_full.png',
        unselectedImg: '/styles/images/tube_full.png',
        onClick: function() {}
      });
    } else {
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: 'No Tube',
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

Box.ScanDiff = function() {
  var self = new BoxVisual();

  self.show = function(opts) {
    self.scan = opts.scan;
    self.size = opts.size;
    self.data = opts.data;

    var diff = Box.utils.getDiff(self.data, self.scan.boxables, self.size.rows, self.size.cols);
    self.changed = diff.positions;

    jQuery('#dialogInfoAbove').html("<h1>Scan Success! </h1>"+
     "<p>The following changes will be applied on save:</p><br>");
    jQuery('#dialogInfoBelow').html('<ul style="list-style-type: none;overflow:hidden; overflow-y:scroll;height:125px;">'+diff.html.join('')+'</ul>');

    jQuery('#dialogDialog').dialog({
      autoOpen: false,
      width: Box.dialogWidth,
      modal: true,
      resizable: false,
      position: [jQuery(window).width()/2 - Box.dialogWidth/2, 50],
      buttons: {
        "Save": function() {
          Box.boxJSON.boxables = self.scan.boxables;
          Box.saveContents();
          jQuery('#dialogDialog').dialog('close');
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
        rows: self.size.rows,
        cols: self.size.cols
      },
      data: self.scan.boxables
    });
    jQuery('#updateSelected, #removeSelected, #emptySelected').prop('disabled', true).addClass('disabled');
    jQuery('#dialogDialog').dialog('open');
  };

  self.getBoxItem = function(row, col) {
    var pos = Box.utils.getPositionString(row, col);
    var boxable = self.data[pos];

    if (typeof boxable !== 'undefined') {
      var sel = jQuery.inArray(pos, self.changed) !== -1 ?
            '/styles/images/tube_full_selected_changed.png' :
            '/styles/images/tube_full_selected.png';

      var unsel = jQuery.inArray(pos, self.changed) !== -1 ?
            '/styles/images/tube_full_changed.png' :
            '/styles/images/tube_full.png';
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: boxable.alias,
        selectedImg: sel,
        unselectedImg: unsel,
        onClick: function() {}
      });
    } else {
      var sel = jQuery.inArray(pos, self.changed) !== -1 ?
            '/styles/images/tube_empty_selected_changed.png' :
            '/styles/images/tube_empty_selected.png';

      var unsel = jQuery.inArray(pos, self.changed) !== -1 ?
            '/styles/images/tube_empty_changed.png' :
            '/styles/images/tube_empty.png';
      return new BoxItem({
        row: row,
        col: col,
        selected: self.selected,
        title: '',
        selectedImg: sel,
        unselectedImg: unsel,
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

Box.PrepareScannerDialog = function() {
  var self = {};

  self.show = function() {
    jQuery('#dialogInfoAbove').html('<h1>Preparing scanner</h1>');
    jQuery('#dialogVisual').html('');
    jQuery('#dialogInfoBelow').html('<p>Please remove box from scanner until prompted.</p>' 
                                     + '<img class="center" src="/styles/images/ajax-loader.gif"/>');   
    jQuery('#dialogDialog').dialog({
      autoOpen: false,
      width: Box.dialogWidth,
      height: "auto",
      modal: true,
      resizable: false,
      position: [jQuery(window).width()/2 - Box.dialogWidth/2, 50],
      buttons: {}
    });
    jQuery('#dialogDialog').dialog('open');
    Box.scan.prepareScanner(Box.boxJSON.size.rows, Box.boxJSON.size.columns);
  };

  self.error = function() {
    jQuery('#dialogInfoAbove').html('<h1 class="warning">Error: could not find the scanner</h1>');
    jQuery('#dialogVisual').html('');
    jQuery('#dialogInfoBelow').html('<p>Please ensure that the scanner software is running, ' 
                                     + 'and remove the box before retrying.</p>');
    jQuery('#dialogDialog').dialog({
      autoOpen: true,
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
    if(col < 10) pos += 0;
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
    for (var pos in boxables) {
      if (boxables[pos].name == name) {
        return pos;
      }
    }
    return null;
  },

  // Return an array of changed positions as well as an HTML list representing the diff
  getDiff: function(oldBoxables, newBoxables) {
    var diff = [];
    var changed = [];

    // Look for old items in the new
    for (var oldpos in oldBoxables) {
      var name = oldBoxables[oldpos].name;
      var newpos = Box.utils.findItemPos(name, newBoxables);
      if (newpos == null) {
        diff.push('<li style="color:red;"><b>-</b> '+name+': removed from the box</li>');
        changed.push(oldpos);
      }  else {
        diff.push('<li style="color:orange;"><b>!</b> '+name+': moved ('+oldpos+'->'+newpos+')</li>');
        changed.push(oldpos);
        changed.push(newpos);
      }
    }

    // Look for new items
    for (var newpos in newBoxables) {
      var name = newBoxables[newpos].name;
      var oldpos = Box.utils.findItemPos(name, oldBoxables);
      if (oldpos == null) {
        diff.push('<li style="color:green;"><b>+</b> '+name+': added to the box at position '+newpos+'</li>');
        changed.push(newpos);
      }
    }
    return {'html': diff, 'positions': changed};
  }
};

