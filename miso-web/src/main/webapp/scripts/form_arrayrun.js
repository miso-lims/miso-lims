if (typeof FormTarget === 'undefined') {
  FormTarget = {};
}
FormTarget.arrayrun = (function($) {

  /*
   * Expected config {
   *   isAdmin: boolean,
   *   instruments: array
   * }
   */

  return {
    getSaveUrl: function(arrayrun) {
      return arrayrun.id ? ('/miso/rest/arrayruns/' + arrayrun.id) : '/miso/rest/arrayruns';
    },
    getSaveMethod: function(arrayrun) {
      return arrayrun.id ? 'PUT' : 'POST';
    },
    getEditUrl: function(arrayrun) {
      return '/miso/arrayrun/' + arrayrun.id;
    },
    getSections: function(config, object) {
      return [{
        title: 'Array Run Information',
        fields: [{
          title: 'Array Run ID',
          data: 'id',
          type: 'read-only',
          getDisplayValue: function(arrayrun) {
            return arrayrun.id || 'Unsaved';
          }
        }, {
          title: 'Instrument',
          data: 'instrumentId',
          type: 'dropdown',
          include: !object.id,
          required: true,
          nullValue: 'SELECT',
          getSource: function() {
            return config.instruments;
          },
          getItemLabel: Utils.array.getName,
          getItemValue: Utils.array.getId,
          sortSource: Utils.sorting.standardSort('name')
        }, {
          title: 'Instrument',
          data: 'instrumentId',
          type: 'read-only',
          include: !!object.id,
          getDisplayValue: function(arrayrun) {
            return arrayrun.instrumentName;
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
          title: 'Run Path',
          data: 'filePath',
          type: 'text',
          maxLength: 255
        }, {
          title: 'Array',
          data: 'arrayId',
          type: 'read-only',
          getDisplayValue: function(arrayrun) {
            return arrayrun.arrayAlias;
          },
          getLink: function(arrayrun) {
            return '/miso/array/' + arrayrun.arrayId;
          }
        }, {
          title: 'Change Array',
          type: 'special',
          makeControls: function() {
            return [$('<button>').addClass('ui-state-default').attr('type', 'button').text('Search').click(function() {
              Utils.showDialog('Array Search', 'Search', [{
                label: 'Search',
                property: 'query',
                type: 'text',
                required: true
              }], function(formData) {
                Utils.ajaxWithDialog('Searching', 'GET', '/miso/rest/arrayruns/array-search?' + jQuery.param({
                  q: formData.query
                }), null, function(data) {
                  if (!data || !data.length) {
                    Utils.showOkDialog('Search Results', ['No matching arrays found']);
                    return;
                  } else {
                    Utils.showWizardDialog('Search Results', data.map(function(array) {
                      return {
                        name: array.alias,
                        handler: function() {
                          $('#arrayId').val(array.id);
                          $('#arrayIdLabel').text(array.alias);
                          $('#arrayIdLabel').attr('href', '/miso/array/' + array.id);
                          updateSamplesTable(array);
                        }
                      };
                    }));
                  }
                });
              });
            }), $('<button>').addClass('ui-state-default').attr('type', 'button').text('Remove').click(function() {
              if ($('#arrayId').val()) {
                Utils.showConfirmDialog("Remove Array", "Remove", ["Remove the array from this array run?"], function() {
                  $('#arrayId').val(null);
                  $('#arrayIdLabel').text('');
                  $('#arrayIdLabel').removeAttr('href');
                  updateSamplesTable(null);
                });
              } else {
                Utils.showOkDialog('Remove Array', ['No array set']);
              }
            })];
          }
        }, {
          title: 'Status',
          data: 'status',
          type: 'dropdown',
          required: true,
          getSource: function() {
            return Constants.healthTypes;
          },
          getItemLabel: function(item) {
            return item.label;
          },
          getItemValue: function(item) {
            return item.label;
          },
          onChange: function(newValue, updateField) {
            var status = getStatus(newValue);
            var updates = {
              required: status.isDone,
              // Editable if run is done and either there's no value set or user is admin
              disabled: !status.isDone || ($('#completionDate').val() && !config.isAdmin)
            };
            if (!status.isDone) {
              updates.value = null;
            }
            updateField('completionDate', updates);
          },
          // Only editable by admin if run is done
          disabled: !object.status ? false : (getStatus(object.status).isDone && !config.isAdmin)
        }, {
          title: 'Start Date',
          data: 'startDate',
          type: 'date',
          required: true,
          disabled: object.startDate && !config.isAdmin
        }, {
          title: 'Completion Date',
          data: 'completionDate',
          type: 'date'
        }]
      }];
    }
  }

  function getStatus(label) {
    return Utils.array.findUniqueOrThrow(function(item) {
      return item.label === label;
    }, Constants.healthTypes);
  }

  function updateSamplesTable(array) {
    $('#listingSamplesTable').empty();
    var data = [];
    var lengthOptions = [50, 25, 10];
    if (array) {
      data = array.samples.map(function(sample) {
        return [sample.coordinates, Box.utils.hyperlinkifyBoxable(sample.name, sample.id, sample.name),
            Box.utils.hyperlinkifyBoxable(sample.name, sample.id, sample.alias)];
      });
      lengthOptions.unshift(array.columns * array.rows);
    }
    $('#listingSamplesTable')
        .dataTable(
            {
              "aaData": data,
              "aoColumns": [{
                "sTitle": "Position"
              }, {
                "sTitle": "Sample Name"
              }, {
                "sTitle": "Sample Alias"
              }],
              "bJQueryUI": true,
              "bDestroy": true,
              "aLengthMenu": [lengthOptions, lengthOptions],
              "iDisplayLength": lengthOptions[0],
              "sPaginationType": "full_numbers",
              "sDom": '<"#toolbar.fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"lf>r<t><"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
              "aaSorting": [[0, "asc"]]
            }).css("width", "100%");
  }

})(jQuery);
