if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.box = (function($) {

  return {
    getSaveUrl: function(box) {
      if (box.id) {
        return Urls.rest.boxes.update(box.id);
      } else {
        return Urls.rest.boxes.create;
      }
    },
    getSaveMethod: function(box) {
      return box.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(box) {
      return Urls.ui.boxes.edit(box.id);
    },
    getSections: function(config) {
      return [{
        title: 'Box Information',
        fields: [{
          title: 'Box ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(box) {
            return box.id || 'Unsaved';
          }
        }, {
          title: 'Name',
          data: 'name',
          type: 'read-only',
          getDisplayValue: function(box) {
            return box.name || 'Unsaved';
          }
        }, {
          title: 'Alias',
          data: 'alias',
          type: 'text',
          required: true,
          maxLength: 255
        }, {
          title: 'Description',
          data: 'description',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Matrix Barcode',
          data: 'identificationBarcode',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Box Use',
          data: 'useId',
          type: 'dropdown',
          getSource: function() {
            return Constants.boxUses;
          },
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          required: true
        }, {
          title: 'Box Size',
          data: 'sizeId',
          type: 'dropdown',
          getSource: function() {
            return Constants.boxSizes;
          },
          getItemLabel: function(boxSize) {
            return boxSize.rowsByColumnsWithScan;
          },
          getItemValue: Utils.array.getId,
          required: true,
          include: config.isNew
        }, {
          title: 'Box Size',
          data: 'sizeId',
          getDisplayValue: function(box) {
            return box.rows + ' × ' + box.cols + ' (can ' + (box.scannable ? '' : 'not ') + 'be scanned by your lab\'s bulk scanner)';
          },
          type: 'read-only',
          include: !config.isNew
        }, {
          title: 'Location',
          data: 'locationBarcode',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Freezer Location',
          data: 'storageLocationId',
          getDisplayValue: function(box) {
            return box.storageDisplayLocation || 'Unknown';
          },
          type: 'read-only'
        }, {
          title: 'Change Location (scan or select)',
          type: 'special',
          makeControls: makeLocationSelect
        }]
      }];
    },
    onLoad: function() {
      resetLocationSearch();
    }
  }

  function makeLocationSelect(form) {
    var controls = [];
    controls.push($('<input>').attr('id', 'freezerLocationScan').attr('type', 'text').css({
      'width': '25%',
      'min-width': '120px'
    }).keyup(function(event) {
      if (event.which == "13") {
        onLocationScan();
      }
    }).on('paste', function(e) {
      window.setTimeout(function() {
        onLocationScan();
      }, 100);
    }).after(' '));
    controls.push($('<span>').attr('id', 'freezerLocationRoot').after(' '));
    controls.push($('<select>').attr('id', 'freezerLocationSelect').css({
      'width': '50%',
      'min-width': '250px'
    }).change(onLocationSelect).after(' '));
    controls.push($('<img>').attr('id', 'freezerLocationLoader').addClass('fg-button hidden').attr('src', '/styles/images/ajax-loader.gif')
        .css('display', 'none').after(' '));
    controls.push($('<button>').attr('id', 'setFreezerLocation').addClass('ui-state-default').attr('type', 'button').text('Set').click(
        function() {
          setFreezerLocation(form);
        }).after(' '));
    controls.push($('<button>').attr('id', 'resetFreezerLocation').addClass('ui-state-default').attr('type', 'button').text('Reset').click(
        resetLocationSearch).after(' '));
    return controls;
  }

  function onLocationScan() {
    $('#freezerLocationLoader').show();
    Utils.ui.setDisabled('#setFreezerLocation', true);
    Utils.ui.setDisabled('#resetFreezerLocation', true);

    var barcode = $('#freezerLocationScan').val();

    $.ajax({
      url: Urls.rest.storageLocations.queryByBarcode + '?' + jQuery.param({
        q: barcode
      }),
      type: 'GET',
      dataType: 'json',
      contentType: 'application/json; charset=utf8'
    }).success(
        function(data) {
          if (data.childLocations && data.childLocations.length) {
            $('#freezerLocationRoot').text(data.fullDisplayLocation + ' > ');
            setFreezerLocationOptions(data.childLocations, data, false);
          } else {
            $('#freezerLocationRoot').text('');
            $('#freezerLocationSelect').empty();
            $('#freezerLocationSelect').append(
                $('<option>').val(data.id).text(data.fullDisplayLocation + (data.availableStorage ? ' *' : '')));
            freezerLocations = [data];
            parentFreezerLocation = null;
          }
          Utils.ui.setDisabled('#setFreezerLocation', !data.availableStorage);
        }).fail(function(response, textStatus, serverStatus) {
      Utils.showOkDialog('Error', ['No storage location found with barcode \'' + barcode + '\'']);
    }).always(function() {
      $('#freezerLocationLoader').hide();
      Utils.ui.setDisabled('#resetFreezerLocation', false);
    });
  }

  function onLocationSelect() {
    $('#freezerLocationScan').empty();
    Utils.ui.setDisabled('#setFreezerLocation', true);
    Utils.ui.setDisabled('#resetFreezerLocation', true);

    var location = getSelectedLocation();

    if (location.availableStorage) {
      Utils.ui.setDisabled('#resetFreezerLocation', false);
      Utils.ui.setDisabled('#setFreezerLocation', false);
    } else {
      $('#freezerLocationLoader').show();
      $.ajax({
        url: Urls.rest.storageLocations.children(location.id),
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json; charset=utf8'
      }).success(function(data) {
        $('#freezerLocationRoot').text(location.fullDisplayLocation + ' > ');
        setFreezerLocationOptions(data, location, false);
      }).fail(function(response, textStatus, serverStatus) {
        Utils.showOkDialog('Error', ['Location search failed']);
      }).always(function() {
        $('#freezerLocationLoader').hide();
        Utils.ui.setDisabled('#resetFreezerLocation', false);
      });
    }
  }

  function setFreezerLocation(form) {
    Utils.ui.setDisabled('#setFreezerLocation', true);
    var location = getSelectedLocation();
    form.updateField('storageLocationId', {
      value: location.id,
      label: location.fullDisplayLocation
    })
    resetLocationSearch();
  }

  function getSelectedLocation() {
    var locationId = $('#freezerLocationSelect').val();
    if (locationId == -1) {
      if (!parentFreezerLocation) {
        throw new Error('No location selected');
      }
      return parentFreezerLocation;
    }
    return Utils.array.findUniqueOrThrow(function(location) {
      return location.id == locationId;
    }, freezerLocations);
  }

  function resetLocationSearch() {
    $('#freezerLocationLoader').show();
    $('#freezerLocationScan').empty();
    $('#freezerLocationRoot').text('');
    $('#freezerLocationSelect').empty();
    Utils.ui.setDisabled('#setFreezerLocation', true);
    Utils.ui.setDisabled('#resetFreezerLocation', true);

    $.ajax({
      url: Urls.rest.storageLocations.freezers,
      type: 'GET',
      dataType: 'json',
      contentType: 'application/json; charset=utf8'
    }).success(function(data) {
      setFreezerLocationOptions(data, null, true);
    }).fail(function(response, textStatus, serverStatus) {
      Utils.showOkDialog('Error', ['Freezer search failed']);
    }).always(function() {
      jQuery('#freezerLocationLoader').hide();
      Utils.ui.setDisabled('#resetFreezerLocation', false);
    });
  }

  var freezerLocations = [];
  var parentFreezerLocation = null;

  function setFreezerLocationOptions(locations, parentLocation, fullDisplay) {
    freezerLocations = locations;
    parentFreezerLocation = parentLocation;
    $('#freezerLocationSelect').empty();
    if (!locations || !locations.length) {
      $('#freezerLocationSelect').append($('<option>').val('-1').text('NO SPACE'));
    } else {
      if (!parentLocation || locations.length > 1) {
        $('#freezerLocationSelect').append($('<option>').val('-1').text('SELECT'));
        $('#freezerLocationSelect').val('-1');
      }
      var displayProperty = fullDisplay ? 'fullDisplayLocation' : 'displayLocation';
      locations.sort(function(a, b) {
        if (a[displayProperty] < b[displayProperty]) {
          return -1;
        }
        if (a[displayProperty] > b[displayProperty]) {
          return 1;
        }
        return 0;
      });
      locations.forEach(function(location) {
        $('#freezerLocationSelect').append(
            $('<option>').val(location.id).text(location[displayProperty] + (location.availableStorage ? ' *' : '')));
      });
      if (parentLocation && locations.length === 1) {
        $('#freezerLocationSelect').val(locations[0].id);
        onLocationSelect();
      }
    }
  }

})(jQuery);
