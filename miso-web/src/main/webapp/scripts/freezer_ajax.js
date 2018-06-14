(function(Freezer, $, undefined) { // NOSONAR (paranoid assurance that undefined is undefined)
  var freezerJson = null;

  var heightField = {
    type: 'int',
    label: 'Height (boxes)',
    property: 'height',
    required: true
  };

  var depthField = {
    type: 'int',
    label: 'Depth (stacks)',
    property: 'depth',
    required: true
  };

  Freezer.setFreezerJson = function(json) {
    freezerJson = json;
    updatePage();
  };

  Freezer.validateAndSave = function() {
    Validate.cleanFields('#freezer-form');
    Validate.clearErrors('#freezer-form');
    
    $('#room').attr('class', 'form-control');
    $('#room').attr('data-parsley-required', 'true');
    $('#room').attr('data-parsley-errors-container', '#roomError');

    $('#alias').attr('class', 'form-control');
    $('#alias').attr('data-parsley-required', 'true');
    $('#alias').attr('data-parsley-maxlength', '255');
    $('#alias').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    $('#alias').attr('data-parsley-errors-container', '#aliasError');

    $('#identificationBarcode').attr('class', 'form-control');
    $('#identificationBarcode').attr('data-parsley-maxlength', '255');
    $('#identificationBarcode').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    $('#identificationBarcode').attr('data-parsley-errors-container', '#identificationBarcodeError');

    $('#freezer-form').parsley();
    $('#freezer-form').parsley().validate();

    Validate.updateWarningOrSubmit('#freezer-form', null, function() {
      save(!freezerJson || !freezerJson.id);
    });
  };
  
  Freezer.addRoom = function () {
    Freezer.addRoomWithCallback(function() { Utils.showOkDialog('Room created', []); });
  },
  Freezer.addRoomWithCallback = function (callback) {
    var fields = [{
	  type: 'text',
	  label: 'Alias',
	  property: 'alias',
	  required: true
	}, {
	  type: 'text',
	  label: 'Barcode',
	  property: 'identificationBarcode',
	  required: false
	}];
    Utils.showDialog('Add Room', 'Add', fields, function(output) {
      var params = {};
      fields.forEach(function(field) {
        params[field.property] = output[field.property];
      });
      var url = '/miso/rest/storagelocations/rooms' + '?' + $.param(params);
      Utils.ajaxWithDialog("Adding Room", 'POST', url, {}, function(responseData) {
        callback();
      });
    });
  };

  Freezer.addFreezerStorage = function() {
    Utils.showWizardDialog('Add Freezer Storage', [{
      name: 'Shelf',
      handler: function() {
        var url = '/miso/rest/storagelocations/freezers/' + freezerJson.id + '/shelves';
        Utils.ajaxWithDialog("Adding Storage", 'POST', url, {}, function(responseData) {
          window.location.href = '/miso/freezer/' + freezerJson.id;
        });
      }
    }, makeHandler('Stack', [heightField], '/stacks')]);
  };

  Freezer.addShelfStorage = function(shelf) {
    Utils.showWizardDialog('Add Shelf Storage', [makeHandler('Rack', [depthField, heightField], '/shelves/' + shelf.id + '/racks'),
        makeHandler('Stack', [heightField], '/shelves/' + shelf.id + '/stacks'), {
          name: 'Loose Storage',
          handler: function() {
            var url = '/miso/rest/storagelocations/freezers/' + freezerJson.id + '/shelves/' + shelf.id + '/loose';
            Utils.ajaxWithDialog("Adding Storage", 'POST', url, {}, function(responseData) {
              window.location.href = '/miso/freezer/' + freezerJson.id;
            });
          }
        }]);
  };

  function makeHandler(name, fields, relativeUrl) {
    return {
      name: name,
      handler: function() {
        Utils.showDialog('Add ' + name, 'OK', fields, function(output) {
          var params = {};
          fields.forEach(function(field) {
            params[field.property] = output[field.property];
          });
          var url = '/miso/rest/storagelocations/freezers/' + freezerJson.id + relativeUrl + '?' + $.param(params);
          Utils.ajaxWithDialog("Adding Storage", 'POST', url, {}, function(responseData) {
            window.location.href = '/miso/freezer/' + freezerJson.id;
          });
        });
      }
    }
  }

  function updatePage() {
    if (freezerJson.id) {
      // freezer is being edited
      $('#id').text(freezerJson.id);
      $('#room').val(freezerJson.parentLocationId);
      $('#alias').val(freezerJson.alias);
      $('#identificationBarcode').val(freezerJson.identificationBarcode);
      updateVisual();
    }
  }

  function updateVisual() {
    if (freezerJson.childLocations) {
      var table = $('#freezerLayout');
      var shelves = freezerJson.childLocations.filter(function(location) {
        return location.locationUnit === 'SHELF';
      });
      var stacks = freezerJson.childLocations.filter(function(location) {
        return location.locationUnit === 'STACK';
      });
      if (shelves.length + stacks.length != freezerJson.childLocations.length) {
        throw 'Unexpected location units';
      }
      shelves.sort(compareLocations).forEach(function(shelf) {
        addShelf(shelf, table);
      });
      if (stacks.length) {
        var stackRow = $('<tr>');
        var stackCell = $('<td>');
        stacks.sort(compareLocations).forEach(function(stack) {
          addShelfItem(stack, stackCell);
        });
        stackRow.append(stackCell);
        table.append(stackRow);
      }

      $('#levelTwoStorageContainer').hide();
    }
  }

  function compareLocations(a, b) {
    if (a.alias < b.alias) {
      return -1;
    }
    if (a.alias > b.alias) {
      return 1;
    }
    return 0;
  }

  function addShelf(shelf, container) {
    var row = $('<tr>');
    var cell = $('<td>');
    cell.append('<span class="storageComponentLabel">' + shelf.displayLocation + '</span>');
    var button = $('<button type="button" class="ui-state-default storageComponentButton">Add Storage</button>');
    button.click(function() {
      Freezer.addShelfStorage(shelf);
    });
    cell.append(button);
    cell.append('<div class="clearfix"></div>');
    if (shelf.childLocations) {
      var shelfItems = shelf.childLocations.filter(function(location) {
        return location.locationUnit === 'RACK';
      }).sort(compareLocations).concat(shelf.childLocations.filter(function(location) {
        return location.locationUnit === 'STACK';
      }).sort(compareLocations)).concat(shelf.childLocations.filter(function(location) {
        return location.locationUnit === 'LOOSE_STORAGE';
      }).sort(compareLocations));
      if (shelfItems.length != shelf.childLocations.length) {
        throw 'Unexpected location units';
      }
      shelfItems.forEach(function(item) {
        addShelfItem(item, cell);
      });
    }
    row.append(cell);
    container.append(row);
  }

  function addShelfItem(item, shelfCell) {
    var node = $('<div>').addClass('storageNode').text(item.displayLocation);
    node.click(function() {
      $('#freezerLayoutContainer .selected').removeClass('selected');
      node.addClass('selected');
      displayLevelTwoStorage(item);
    });
    shelfCell.append(node);
  }

  function displayLevelTwoStorage(storage) {
    $('#levelTwoStorageAlias').text(storage.displayLocation);
    $('#levelTwoStorageLayout').empty();
    switch (storage.locationUnit) {
    case 'RACK':
      displayRack(storage);
      break;
    case 'STACK':
      displayStack(storage);
      break;
    case 'LOOSE_STORAGE':
      displayLooseStorage(storage);
      break;
    default:
      throw 'Unexpected location unit';
    }
    $('#levelTwoStorageContainer').show();
  }

  function getLevelTwoNodeSelectFunction(node) {
    function assignBox() {
      Utils.showDialog('Search for Box to Assign', 'Search', [ {
        type : "text",
        label : "Search",
        property : "query",
        value : ""
      }, ], function(results) {
        Utils.ajaxWithDialog('Searching for Boxes', 'GET', '/miso/rest/boxes/search/partial?' + jQuery.param({
          q : results.query,
          b : true
          }), null, function(response) {
          Utils.showWizardDialog('Select Box to Assign', response.map(function(box) {
            return {
              name : box.alias,
              handler : function() {
                Utils.ajaxWithDialog('Moving Box', 'POST', '/miso/rest/box/' + box.id + '/setlocation?' + jQuery.param({
                  storageId: node.item.id
                }), {}, Utils.page.pageReload, function(json) {
                  Utils.showOkDialog('Error Moving Box', [json.error]);
                });
                }
            }
            }));
          })
      });
    }
    switch (node.item.locationUnit) {
    case 'STACK_POSITION':
      return function() {
      assignBox();
        $('#levelTwoStorageContainer .selected').removeClass('selected');
        node.addClass('selected');
      };
      break;
    case 'LOOSE_STORAGE':
      return function() {
        var actions = node.item.boxes.map(function(box) {
          return {
            name : "View " + box.alias,
            handler : function() {
              window.location = window.location.origin + '/miso/box/' + box.id;
            }
          };
        });
        actions.unshift({
          name : "Add Box to Storage",
          handler : assignBox
        });
        Utils
            .showWizardDialog('Boxes in ' + node.item.displayLocation, actions);
        $('#levelTwoStorageContainer .selected').removeClass('selected');
        node.addClass('selected');
      };
      break;
    default:
      throw 'Unexpected box location';
    }
  }

  function displayRack(rack) {
    var table = $('#levelTwoStorageLayout');
    var stackCount = rack.childLocations.length;
    var stackHeight = rack.childLocations[0].childLocations.length;
    rack.childLocations.sort(compareLocations).forEach(function(stack) {
      if (stack.locationUnit != 'STACK') {
        throw 'Unexpected location unit';
      }
      if (stack.childLocations.length != stackHeight) {
        throw 'Uneven stack heights within rack';
      }
      stack.childLocations.sort(compareLocations).reverse().forEach(function(stackpos) {
        if (stackpos.locationUnit != 'STACK_POSITION') {
          throw 'Unexpected location unit';
        }
      });
    });
    for (var row = 0; row < stackHeight; row++) {
      var tableRow = $('<tr>');
      var cells = [];
      for (var col = 0; col < stackCount; col++) {
        var node = $('<td>').text(
            rack.childLocations[col].displayLocation + ', ' + rack.childLocations[col].childLocations[row].displayLocation + 
            ' (' + (rack.childLocations[col].childLocations[row].boxes[0] ? rack.childLocations[col].childLocations[row].boxes[0].alias : 'empty') + ')');
        node.item = rack.childLocations[col].childLocations[row];
        node.click(getLevelTwoNodeSelectFunction(node));
        cells.unshift(node);
      }
      if (row === 0) {
        cells.unshift('<td rowspan="' + stackHeight + '"><strong>B<br/>A<br/>C<br/>K</strong></td>');
        cells.push('<td rowspan="' + stackHeight + '"><strong>F<br/>R<br/>O<br/>N<br/>T</strong></td>');
      }
      tableRow.append(cells);
      table.append(tableRow);
    }
  }

  function displayStack(stack) {
    var table = $('#levelTwoStorageLayout');
    stack.childLocations.sort(compareLocations).reverse().forEach(function(stackpos) {
      if (stackpos.locationUnit != 'STACK_POSITION') {
        throw 'Unexpected location unit';
      }
      var row = $('<tr>');
      var cell = $('<td>').text(stackpos.displayLocation + ' (' + (stackpos.boxes[0] ? stackpos.boxes[0].alias : 'empty') + ')');
      cell.item = stackpos;
      cell.click(getLevelTwoNodeSelectFunction(cell));
      row.append(cell);
      table.append(row);
    });
  }

  function displayLooseStorage(storage) {
    var table = $('#levelTwoStorageLayout');
    var row = $('<tr>');
    var cell = $('<td>');
    for(box in storage.boxes){
    	cell.append(document.createTextNode(storage.boxes[box].alias)).append('<br/>');
    }
    cell.append(document.createTextNode('(Unorganized Space)'));
    cell.item = storage;
    cell.click(getLevelTwoNodeSelectFunction(cell));
    row.append(cell);
    table.append(row);
  }

  function updateJson() {
    var freezer = freezerJson || {};
    freezer.parentLocationId = $('#room').val();
    freezer.alias = $('#alias').val();
    freezer.identificationBarcode = $('#identificationBarcode').val();
    freezer.locationUnit = 'FREEZER';
    freezerJson = freezer;
  }

  function save(isNew) {
    updateJson();
    var url = '/miso/rest/storagelocations/freezers';
    if (!isNew) {
      url += '/' + freezerJson.id;
    }
    var type = isNew ? 'POST' : 'PUT';

    $.ajax({
      url: url,
      type: type,
      dataType: 'json',
      contentType: 'application/json; charset=utf8',
      data: JSON.stringify(freezerJson)
    }).success(function(data) {
      window.location.href = '/miso/freezer/' + data.id;
    }).fail(function(response, textStatus, serverStatus) {
      Validate.displayErrors(JSON.parse(response.responseText), '#freezer-form');
    });
  }

}(window.Freezer = window.Freezer || {}, jQuery));
