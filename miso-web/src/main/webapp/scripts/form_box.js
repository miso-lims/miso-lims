if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.box = (function($) {

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('boxes');
    },
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
          source: Constants.boxUses,
          getItemLabel: Utils.array.getAlias,
          getItemValue: Utils.array.getId,
          required: true
        }, {
          title: 'Box Size',
          data: 'sizeId',
          type: 'dropdown',
          source: Constants.boxSizes,
          getItemLabel: Utils.array.get('label'),
          getItemValue: Utils.array.getId,
          required: true,
          include: config.isNew
        }, {
          title: 'Box Size',
          data: 'sizeId',
          getDisplayValue: Utils.array.get('sizeLabel'),
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
          getLink: function(box) {
            return box.freezerId ? Urls.ui.freezers.edit(box.freezerId) : null;
          },
          type: 'read-only'
        }, {
          title: 'Change Location (scan or select)',
          type: 'special',
          makeControls: makeLocationSelect
        }]
      }];
    },
    onLoad: function(form) {
      resetLocationSearch(form);
    }
  }

  function makeLocationSelect(form) {
    var container = $('<div>').css({
      'width': '95%',
      'display': 'flex',
      'align-items': 'center'
    });
    container.append($('<input>').attr('id', 'freezerLocationScan').attr('type', 'text').css({
      'margin-right': '2px',
      'min-width': '120px',
      'flex': 1
    }).keyup(function(event) {
      if (event.which == "13") {
        onLocationScan(form);
      }
    }).on('paste', function(e) {
      window.setTimeout(function() {
        onLocationScan(form);
      }, 100);
    }).after(' '));
    container.append($('<span>').attr('id', 'freezerLocationRoot').css('margin', '2px'));
    container.append($('<select>').attr('id', 'freezerLocationSelect').css({
      'margin': '2px',
      'min-width': '250px',
      'flex': 2
    }).change(function() {
      onLocationSelect(form);
    }).after(' '));
    container.append($('<img>').attr('id', 'freezerLocationLoader').addClass('fg-button hidden').attr('src',
        '/styles/images/ajax-loader.gif').css('display', 'none').css('margin', '2px'));
    container.append($('<button>').attr('id', 'setFreezerLocation').addClass('ui-state-default').attr('type', 'button').text('Set').click(
        function() {
          setFreezerLocation(form);
        }).css('margin', '2px'));
    container.append($('<button>').attr('id', 'resetFreezerLocation').addClass('ui-state-default').attr('type', 'button').text('Reset')
        .click(function() {
          resetLocationSearch(form);
        }).css('margin', '2px'));
    container.append($('<button>').attr('id', 'removeFreezerLocation').addClass('ui-state-default').attr('type', 'button').text('Remove')
        .click(function() {
          removeFreezerLocation(form);
        }).css('margin', '2px'));
    return container;
  }

  function onLocationScan(form) {
    $('#freezerLocationLoader').show();
    disableLocationControls(true, form);

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
            setFreezerLocationOptions(data.childLocations, data, false, form);
          } else {
            $('#freezerLocationRoot').text('');
            $('#freezerLocationSelect').empty();
            $('#freezerLocationSelect').append(
                $('<option>').val(data.id).text(data.fullDisplayLocation + (data.availableStorage ? ' *' : '')));
            freezerLocations = [data];
            parentFreezerLocation = null;
          }
        }).fail(function(response, textStatus, serverStatus) {
      Utils.showOkDialog('Error', ['No storage location found with barcode \'' + barcode + '\'']);
    }).always(function() {
      $('#freezerLocationLoader').hide();
      disableLocationControls(false, form);
    });
  }

  function onLocationSelect(form) {
    $('#freezerLocationScan').empty();
    disableLocationControls(true, form);

    var location = getSelectedLocation();

    if (location.availableStorage) {
      disableLocationControls(false, form);
    } else {
      $('#freezerLocationLoader').show();
      $.ajax({
        url: Urls.rest.storageLocations.children(location.id),
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json; charset=utf8'
      }).success(function(data) {
        $('#freezerLocationRoot').text(location.fullDisplayLocation + ' > ');
        setFreezerLocationOptions(data, location, false, form);
      }).fail(function(response, textStatus, serverStatus) {
        Utils.showOkDialog('Error', ['Location search failed']);
      }).always(function() {
        $('#freezerLocationLoader').hide();
        disableLocationControls(false, form);
      });
    }
  }

  function setFreezerLocation(form) {
    disableLocationControls(true, form);
    var location = getSelectedLocation();
    form.updateField('storageLocationId', {
      value: location.id,
      label: location.fullDisplayLocation,
      link: Urls.ui.freezers.edit(location.freezerId)
    })
    resetLocationSearch(form);
  }

  function removeFreezerLocation(form) {
    disableLocationControls(true, form);
    form.updateField('storageLocationId', {
      value: null,
      label: 'Unknown',
      link: null
    });
    resetLocationSearch(form);
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

  function disableLocationControls(disable, form) {
    if (disable) {
      Utils.ui.setDisabled('#setFreezerLocation', true);
      Utils.ui.setDisabled('#resetFreezerLocation', true);
      Utils.ui.setDisabled('#removeFreezerLocation', true);
    } else {
      if ($('#freezerLocationRoot').text()) {
        if (getSelectedLocation().availableStorage) {
          Utils.ui.setDisabled('#setFreezerLocation', false);
        }
        Utils.ui.setDisabled('#resetFreezerLocation', false);
      }
      if (form.get('storageLocationId')) {
        Utils.ui.setDisabled('#removeFreezerLocation', false);
      }
    }
  }

  function resetLocationSearch(form) {
    disableLocationControls(true, form);
    $('#freezerLocationLoader').show();
    $('#freezerLocationScan').empty();
    $('#freezerLocationRoot').text('');
    $('#freezerLocationSelect').empty();

    $.ajax({
      url: Urls.rest.storageLocations.freezers,
      type: 'GET',
      dataType: 'json',
      contentType: 'application/json; charset=utf8'
    }).success(function(data) {
      setFreezerLocationOptions(data, null, true, form);
    }).fail(function(response, textStatus, serverStatus) {
      Utils.showOkDialog('Error', ['Freezer search failed']);
    }).always(function() {
      jQuery('#freezerLocationLoader').hide();
      disableLocationControls(false, form);
    });
  }

  var freezerLocations = [];
  var parentFreezerLocation = null;

  function setFreezerLocationOptions(locations, parentLocation, fullDisplay, form) {
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
        onLocationSelect(form);
      }
    }
  }

})(jQuery);
