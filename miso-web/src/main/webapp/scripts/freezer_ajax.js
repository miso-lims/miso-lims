(function(Freezer, $, undefined) { // NOSONAR (paranoid assurance that undefined is undefined)
  var freezerJson = null;
  var changelogInitialised = false;
  var selectedComponent = null;

  var heightField = {
    type: 'int',
    label: 'Height (rows)',
    property: 'height',
    required: true
  };

  var depthField = {
    type: 'int',
    label: 'Depth (stacks)',
    property: 'depth',
    required: true
  };

  var barcodeField = {
    type: 'text',
    label: 'Barcode',
    property: 'identificationBarcode',
    required: false
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

    $('#mapUrl').attr('class', 'form-control');
    $('#mapUrl').attr('data-parsley-maxlength', '1024');
    $('#mapUrl').attr('data-parsley-type', 'url');
    $('#mapUrl').attr('data-parsley-errors-container', '#mapUrlError');

    $('#freezer-form').parsley();
    $('#freezer-form').parsley().validate();

    Validate.updateWarningOrSubmit('#freezer-form', null, function() {
      save(!freezerJson || !freezerJson.id);
    });
  };

  Freezer.validateAndSaveComponent = function() {
    Validate.cleanFields('#freezerComponent-form');
    Validate.clearErrors('#freezerComponent-form');

    $('#storageComponentBarcode').attr('class', 'form-control');
    $('#storageComponentBarcode').attr('data-parsley-maxlength', '255');
    $('#storageComponentBarcode').attr('data-parsley-pattern', Utils.validation.sanitizeRegex);
    $('#storageComponentBarcode').attr('data-parsley-errors-container', '#storageComponentBarcodeError');

    $('#freezerComponent-form').parsley();
    $('#freezerComponent-form').parsley().validate();

    Validate.updateWarningOrSubmit('#freezerComponent-form', null, function() {
      saveComponent();
    });
  };

  Freezer.addRoom = function() {
    Freezer.addRoomWithCallback(function() {
      Utils.showOkDialog('Room created', []);
    });
  };

  Freezer.addRoomWithCallback = function(callback) {
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
    Utils.showWizardDialog('Add Freezer Storage', [makeHandler('Shelf', [barcodeField], '/shelves'),
        makeHandler('Stack', [heightField, barcodeField], '/stacks', getStackChildLabels)]);
  };

  Freezer.addShelfStorage = function(shelf) {
    Utils.showWizardDialog('Add Shelf Storage', [
        makeHandler('Rack', [depthField, heightField, barcodeField], '/shelves/' + shelf.id + '/racks', getRackChildLabels),
        makeHandler('Stack', [heightField, barcodeField], '/shelves/' + shelf.id + '/stacks', getStackChildLabels),
        makeHandler('Tray Rack', [heightField, barcodeField], '/shelves/' + shelf.id + '/tray-racks', getTrayRackChildLabels), {
          name: 'Loose Storage',
          handler: function() {
            var url = '/miso/rest/storagelocations/freezers/' + freezerJson.id + '/shelves/' + shelf.id + '/loose';
            Utils.ajaxWithDialog("Adding Storage", 'POST', url, {}, function(responseData) {
              window.location.href = '/miso/freezer/' + freezerJson.id;
            });
          }
        }]);
  };

  function makeHandler(name, fields, relativeUrl, getChildBarcodeLabelsFunction) {
    return {
      name: name,
      handler: function() {
        Utils.showDialog('Add ' + name, 'OK', fields, function(output) {
          var params = {};
          fields.forEach(function(field) {
            params[field.property] = output[field.property];
          });
          var url = '/miso/rest/storagelocations/freezers/' + freezerJson.id + relativeUrl + '?' + $.param(params);
          var data = [];
          var submitFunction = function() {
            Utils.ajaxWithDialog("Adding Storage", 'POST', url, data, function(responseData) {
              window.location.href = '/miso/freezer/' + freezerJson.id;
            });
          };
          if (getChildBarcodeLabelsFunction) {
            var childBarcodeLabels = getChildBarcodeLabelsFunction(output);
            var childBarcodeFields = [];
            for (var i = 0; i < childBarcodeLabels.length; i++) {
              childBarcodeFields.push({
                type: 'text',
                label: childBarcodeLabels[i] + ' barcode',
                property: 'childBarcode' + i,
                required: false
              });
            }
            Utils.showDialog('Add ' + name + ' - Barcodes', 'OK', childBarcodeFields, function(output) {
              for ( var key in output) {
                data.push(output[key]);
              }
              submitFunction();
            });
          } else {
            submitFunction();
          }
        });
      }
    }
  }

  function getStackChildLabels(output) {
    var labels = [];
    for (var i = 1; i <= output.height; i++) {
      var label = 'Slot ' + i;
      if (i === 1) {
        label += ' (bottom)';
      } else if (i === output.height) {
        label += ' (top)';
      }
      labels.push(label);
    }
    return labels;
  }

  function getRackChildLabels(output) {
    var labels = [];
    for (var stack = 1; stack <= output.depth; stack++) {
      for (var slot = 1; slot <= output.height; slot++) {
        var label = 'Stack ' + stack + ', Slot ' + slot;
        if (stack === 1 && slot === 1) {
          label += ' (front bottom)';
        } else if (stack === output.depth && slot === output.height) {
          label += ' (back top)';
        }
        labels.push(label);
      }
    }
    return labels;
  }

  function getTrayRackChildLabels(output) {
    var labels = [];
    for (var i = 1; i <= output.height; i++) {
      var label = 'Tray ' + i;
      if (i === 1) {
        label += ' (top)';
      } else if (i === output.height) {
        label += ' (bottom)';
      }
      labels.push(label);
    }
    return labels;
  }

  function updatePage() {
    if (freezerJson.id) {
      // freezer is being edited
      $('#id').text(freezerJson.id);
      $('#room').val(freezerJson.parentLocationId);
      $('#alias').val(freezerJson.alias);
      $('#identificationBarcode').val(freezerJson.identificationBarcode);
      $('#mapUrl').val(freezerJson.mapUrl);
      updateVisual();
      updateChangelogs();
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
        throw new Error('Unexpected location units');
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

      $('#editStorageComponentContainer').hide();
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
    button.click(function(event) {
      Freezer.addShelfStorage(shelf);
      event.stopPropagation();
    });
    cell.append(button);
    cell.append('<div class="clearfix"></div>');
    if (shelf.childLocations) {
      var shelfItems = shelf.childLocations.filter(function(location) {
        return location.locationUnit === 'TRAY_RACK';
      }).sort(compareLocations).concat(shelf.childLocations.filter(function(location) {
        return location.locationUnit === 'RACK';
      }).sort(compareLocations)).concat(shelf.childLocations.filter(function(location) {
        return location.locationUnit === 'STACK';
      }).sort(compareLocations)).concat(shelf.childLocations.filter(function(location) {
        return location.locationUnit === 'LOOSE_STORAGE';
      }).sort(compareLocations));
      if (shelfItems.length != shelf.childLocations.length) {
        throw new Error('Unexpected location units');
      }
      shelfItems.forEach(function(item) {
        addShelfItem(item, cell);
      });
    }
    cell.click(function(event) {
      $('#freezerLayoutContainer .selected').removeClass('selected');
      selectedComponent = shelf;
      cell.addClass('selected');
      $('#levelTwoStorageContainer').hide();
      displayEditStorageComponentControls(shelf);
    });
    row.append(cell);
    container.append(row);
  }

  function addShelfItem(item, shelfCell) {
    var node = $('<div>').addClass('storageNode').text(item.displayLocation);
    node.click(function(event) {
      $('#freezerLayoutContainer .selected').removeClass('selected');
      selectedComponent = item;
      node.addClass('selected');
      displayLevelTwoStorage(item);
      event.stopPropagation();
    });
    shelfCell.append(node);
  }

  function displayLevelTwoStorage(storage) {
    $('#levelTwoStorageAlias').text(storage.displayLocation);
    $('#levelTwoStorageLayout').empty();
    switch (storage.locationUnit) {
    case 'RACK':
      displayRack(storage);
      displayEditStorageComponentControls(storage);
      break;
    case 'STACK':
      displayStack(storage);
      displayEditStorageComponentControls(storage);
      break;
    case 'LOOSE_STORAGE':
      displayLooseStorage(storage);
      $('#editStorageComponentContainer').hide();
      break;
    case 'TRAY_RACK':
      displayTrayRack(storage);
      displayEditStorageComponentControls(storage);
      break;
    default:
      throw new Error('Unexpected location unit');
    }

    $('#levelTwoStorageContainer').show();
  }

  function displayEditStorageComponentControls(storage) {
    $('#storageComponentAlias').text(storage.displayLocation);
    $('#storageComponentId').text(storage.id);
    $('#storageComponentBarcode').val(storage.identificationBarcode);
    $('#editStorageComponentContainer').show();
  }

  function getLevelTwoNodeSelectFunction(node) {
    function assignBox() {
      Utils.showDialog('Search for Box to Assign', 'Search', [{
        type: "text",
        label: "Search",
        property: "query",
        value: ""
      }, ], function(results) {
        Utils.ajaxWithDialog('Searching for Boxes', 'GET', '/miso/rest/boxes/search/partial?' + jQuery.param({
          q: results.query,
          b: true
        }), null, function(response) {
          Utils.showWizardDialog('Select Box to Assign', response.map(function(box) {
            return {
              name: box.alias,
              handler: function() {
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
        selectedComponent = node.item;
        node.addClass('selected');
        displayEditStorageComponentControls(node.item);
      };
      break;
    case 'LOOSE_STORAGE':
      return getUnorganizedStorageSelectFunction(node, false, assignBox);
      break;
    case 'TRAY':
      return getUnorganizedStorageSelectFunction(node, true, assignBox);
    default:
      throw new Error('Unexpected box location');
    }
  }

  function getUnorganizedStorageSelectFunction(node, allowEdit, assignBoxFunction) {
    return function() {
      var actions = node.item.boxes.map(function(box) {
        return {
          name: "View " + box.alias,
          handler: function() {
            window.location = window.location.origin + '/miso/box/' + box.id;
          }
        };
      });
      actions.unshift({
        name: "Add Box to Storage",
        handler: assignBoxFunction
      });
      Utils.showWizardDialog('Boxes in ' + node.item.displayLocation, actions);
      $('#levelTwoStorageContainer .selected').removeClass('selected');
      selectedComponent = node.item;
      node.addClass('selected');
      if (allowEdit) {
        displayEditStorageComponentControls(node.item);
      } else {
        $('#editStorageComponentContainer').hide();
      }
    };
  }

  function displayTrayRack(rack) {
    var table = $('#levelTwoStorageLayout');
    rack.childLocations.sort(compareLocations).forEach(function(tray) {
      if (tray.locationUnit != 'TRAY') {
        throw new Error('Unexpected location unit');
      }
      var row = $('<tr>');
      var cell = $('<td>');
      cell.append(document.createTextNode(tray.displayLocation)).append('<br/>');
      for ( var box in tray.boxes) {
        cell.append(document.createTextNode(tray.boxes[box].alias)).append('<br/>');
      }
      cell.append(document.createTextNode('(Unorganized Space)'));
      cell.item = tray;
      cell.click(getLevelTwoNodeSelectFunction(cell));
      row.append(cell);
      table.append(row);
    });
  }

  function displayRack(rack) {
    var table = $('#levelTwoStorageLayout');
    var stackCount = rack.childLocations.length;
    var stackHeight = rack.childLocations[0].childLocations.length;
    rack.childLocations.sort(compareLocations).forEach(function(stack) {
      if (stack.locationUnit != 'STACK') {
        throw new Error('Unexpected location unit');
      }
      if (stack.childLocations.length != stackHeight) {
        throw new Error('Uneven stack heights within rack');
      }
      stack.childLocations.sort(compareLocations).reverse().forEach(function(stackpos) {
        if (stackpos.locationUnit != 'STACK_POSITION') {
          throw new Error('Unexpected location unit');
        }
      });
    });
    for (var row = 0; row < stackHeight; row++) {
      var tableRow = $('<tr>');
      var cells = [];
      for (var col = 0; col < stackCount; col++) {
        var node = $('<td>').text(
            rack.childLocations[col].displayLocation
                + ', '
                + rack.childLocations[col].childLocations[row].displayLocation
                + ' ('
                + (rack.childLocations[col].childLocations[row].boxes[0] ? rack.childLocations[col].childLocations[row].boxes[0].alias
                    : 'empty') + ')');
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
        throw new Error('Unexpected location unit');
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
    for ( var box in storage.boxes) {
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
    freezer.mapUrl = $('#mapUrl').val();
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

  function updateChangelogs() {
    $.ajax({
      url: '/miso/rest/storagelocations/freezers/' + freezerJson.id + '/changelog',
      type: 'GET',
      dataType: 'json'
    }).success(function(data) {
      if (changelogInitialised) {
        $('#changelog').dataTable().fnDestroy();
        $('#changelog').empty();
      }
      changelogInitialised = true;
      ListUtils.createStaticTable('changelog', ListTarget.changelog, {}, data);
    }).fail(function(response, textStatus, serverStatus) {
      Validate.displayErrors(JSON.parse(response.responseText));
    });
  }

  function saveComponent() {
    var component = {};
    for ( var prop in selectedComponent) {
      component[prop] = selectedComponent[prop];
    }
    component.identificationBarcode = $('#storageComponentBarcode').val();

    $.ajax({
      url: '/miso/rest/storagelocations/' + component.id,
      type: 'PUT',
      dataType: 'json',
      contentType: 'application/json; charset=utf8',
      data: JSON.stringify(component)
    }).success(function() {
      window.location.href = '/miso/freezer/' + freezerJson.id;
    }).fail(function(response, textStatus, serverStatus) {
      Validate.displayErrors(JSON.parse(response.responseText), '#freezerComponent-form');
    });
  }

}(window.Freezer = window.Freezer || {}, jQuery));
