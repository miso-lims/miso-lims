HotTarget.libraryTemplate_index = (function($) {

  return {
    getUserManualUrl: function() {
      return Urls.external.userManual('libraries', 'library-templates');
    },
    getCreateUrl: function() {
      return null;
    },
    getUpdateUrl: function(id) {
      return null;
    },
    requestConfiguration: function(config, callback) {
      callback(config)
    },
    fixUp: function(index, errorHandler) {
    },
    createColumns: function(config, create, data) {
      var index1s = config.indexFamily.indices.filter(function(index) {
        return index.position === 1;
      });
      var index2s = config.indexFamily.indices.filter(function(index) {
        return index.position === 2;
      });
      return [
          HotUtils.makeColumnForText('Box Position', true, 'boxPosition', {
            readOnly: config.pageMode === 'edit',
            validator: HotUtils.validator.regex('^[A-Z](0[1-9]|1[0-9]|2[0-6])$', true),
            customSorting: [{
              buttonText: 'Sort by Rows',
              sortTarget: 'Rows',
              sortFunc: HotUtils.sorting.rowSort
            }, {
              buttonText: 'Sort by Columns',
              sortTarget: 'Columns',
              sortFunc: HotUtils.sorting.colSort
            }]
          }),
          HotUtils.makeColumnForConstantsList('Index 1', !!index1s.length, 'index1Id', 'index1Id', 'id', 'label', index1s, false, {}, null,
              'Unspecified'),
          HotUtils.makeColumnForConstantsList('Index 2', !!index2s.length, 'index2Id', 'index2Id', 'id', 'label', index2s, false, {
            depends: 'index1Id',
            update: function(item, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (config.indexFamily.uniqueDualIndex) {
                var index1 = Utils.array.findFirstOrNull(function(index) {
                  return index.label === value;
                }, index1s);
                if (index1) {
                  var index2 = Utils.array.findFirstOrNull(function(index) {
                    return index.name === index1.name;
                  }, index2s);
                  if (index2) {
                    setData(index2.label);
                  }
                }
              }
            }
          }, null, 'Unspecified')];
    },
    getBulkActions: function(config) {
      return [];
    },
    customSave: function(table, config) {
      setSaveDisabled(table, true);

      var template = config.libraryTemplate;
      var data = table.getDtoData();
      if (config.pageMode === 'create') {
        var errors = [];
        data.forEach(function(item) {
          if ((template.indexOneIds && template.indexOneIds.hasOwnProperty(item.boxPosition))
              || (template.indexTwoIds && template.indexTwoIds.hasOwnProperty(item.boxPosition))) {
            errors.push('This template already defines indices for position ' + item.boxPosition);
          }
        });
        if (errors.length) {
          showSaveErrors(errors);
          setSaveDisabled(table, false);
          return;
        }
      }

      table
          .validateCells(function(valid) {
            if (valid) {
              data.forEach(function(item) {
                template.indexOneIds[item.boxPosition] = item.index1Id;
                template.indexTwoIds[item.boxPosition] = item.index2Id;
              });
              template.indexOneIds = filterUnsetIndices(template.indexOneIds);
              template.indexTwoIds = filterUnsetIndices(template.indexTwoIds);
              $
                  .ajax({
                    url: Urls.rest.libraryTemplates.update(template.id),
                    type: 'PUT',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf8',
                    data: JSON.stringify(template)
                  })
                  .success(function(data) {
                    showSuccess();
                  })
                  .fail(
                      function(response, textStatus, serverStatus) {
                        if (!response || !response.data || response.dataFormat !== 'validation') {
                          showSaveErrors('Something went terribly wrong. Please file a ticket with a screenshot or copy-paste of the data that you were trying to save.');
                        } else {
                          var errors = [];
                          $.each(response.data, function(key, value) {
                            if (key === 'GENERAL') {
                              errors.push(value);
                            } else {
                              errors.push(key + ': ' + value);
                            }
                          });
                        }
                      });
            } else {
              showSaveErrors(['Please fix highlighted cells. See the Quick Help section (above) for additional information regarding specific fields.']);
              setSaveDisabled(table, false);
            }
          });
    }
  };

  function filterUnsetIndices(indexMap) {
    var filtered = {};
    for ( var key in indexMap) {
      if (indexMap[key])
        filtered[key] = indexMap[key];
    }
    return filtered;
  }

  function setSaveDisabled(table, disabled) {
    table.updateSettings({
      readOnly: disabled
    });
    Utils.ui.setDisabled('#save', disabled);
    if (disabled) {
      $('ajaxLoader').removeClass('hidden');
    } else {
      $('ajaxLoader').addClass('hidden');
    }
  }

  function showSaveErrors(errors) {
    var container = $('#saveErrors');
    container.empty();
    container.append($('<ul>').append(errors.map(function(error) {
      return $('<li>').text(error);
    })));
    $('#errors').removeClass('hidden');
  }

  function showSuccess() {
    $('#errors').addClass('hidden');
    var container = $('#saveSuccesses');
    container.empty();
    container.append($('<p>').text('Saved successfully'));
    container.removeClass('hidden');
  }

})(jQuery);
