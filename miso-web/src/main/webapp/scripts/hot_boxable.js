/**
 * Boxable-specific Handsontable code. Provides columns that are intended for use with Boxable types such as Samples a Libraries
 */
HotTarget.boxable = (function() {

  var boxSearchCache = [];
  var boxesByAlias = [];

  var boxAliasValidator = function(value, callback) {
    return callback(Utils.validation.isEmpty(value) || value in boxesByAlias);
  };

  var mustBeFalseValidator = function(value, callback) {
    return callback(value === 'False');
  };

  var cacheBox = function(box, itemPos) {
    var cached = boxesByAlias[box.alias];
    if (!cached) {
      box.emptyPositions = Utils.getEmptyBoxPositions(box);
      boxesByAlias[box.alias] = box;
      cached = box;
    }
    if (itemPos && cached.emptyPositions.indexOf(itemPos) === -1) {
      cached.emptyPositions.push(itemPos);
      cached.emptyPositions.sort();
    }
  };

  var makeDiscardedColumn = function() {
    var col = HotUtils.makeColumnForBoolean('Discarded', true, 'discarded', true);
    col.depends = 'boxPosition';
    col.update = function(obj, flat, flatProperty, value, setReadOnly, setOptions, setData) {
      setOptions({
        validator: !value ? HotUtils.validator.requiredAutocomplete : mustBeFalseValidator
      });
    };
    return col;
  };
  
  var isTargetIdentity = function(config) {
    return config && config.targetSampleClass && config.targetSampleClass.alias == 'Identity';
  }

  function fillBoxPositions(table, sortFunction) {
    var rowCount = table.countRows();
    var freeByAlias = [];

    for (var row = 0; row < rowCount; row++) {
      var boxAlias = table.getDataAtRowProp(row, 'boxAlias');
      if (boxAlias) {
        freeByAlias[boxAlias] = freeByAlias[boxAlias] || boxesByAlias[boxAlias].emptyPositions.slice();
        var pos = table.getDataAtRowProp(row, 'boxPosition');
        if (pos) {
          freeByAlias[boxAlias] = freeByAlias[boxAlias].filter(function(freePos) {
            return freePos !== pos;
          });
        }
      }
    }
    for ( var key in freeByAlias) {
      freeByAlias[key].sort(sortFunction);
    }
    var changes = [];
    for (var row = 0; row < rowCount; row++) {
      var boxAlias = table.getDataAtRowProp(row, 'boxAlias');
      if (boxAlias && !table.getDataAtRowProp(row, 'boxPosition')) {
        var free = freeByAlias[boxAlias];
        if (free && free.length > 0) {
          var pos = free.shift();
          changes.push([row, 'boxPosition', pos]);
          freeByAlias[boxAlias] = free.filter(function(freePos) {
            return freePos !== pos;
          });
        }
      }
    }
    table.setDataAtRowProp(changes);
  }

  return {
    makeBoxLocationColumns: function(config) {
      if (config.box) {
        cacheBox(config.box);
      }
      return [
          {
            header: 'Box Search',
            data: 'boxSearch',
            allowEmpty: true,
            include: !config.box,
            unpack: function(obj, flat, setCellMeta) {
              // search field only
            },
            pack: function(obj, flat, errorHandler) {
              // search field only
            },
            depends: ['discarded', 'distributed'],
            update: function(obj, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if ((flatProperty === 'discarded' && value == 'True') || (flatProperty == 'distributed' && value == 'Sent Out')) {
                setData(null);
                setReadOnly(true);
              } else {
                setReadOnly(false);
              }
            }
          },
          {
            header: 'Box Alias',
            data: 'boxAlias',
            type: 'dropdown',
            validator: boxAliasValidator,
            include: true,
            readOnly: !!config.box,
            trimDropdown: false,
            source: [''],
            description: 'Searches by Box name, alias, or barcode. (Accepts partial matches)',
            unpack: function(obj, flat, setCellMeta) {
              if (obj.box && obj.box.alias) {
                flat.boxAlias = obj.box.alias;
                cacheBox(obj.box, obj.boxPosition);
              }
            },
            pack: function(obj, flat, errorHandler) {
              obj.box = {};
              if (flat.boxAlias) {
                obj.box.alias = flat.boxAlias;
                obj.box.id = boxesByAlias[flat.boxAlias].id;
              } else {
                obj.box.alias = null;
                obj.box.id = null;
              }
            },
            depends: ['boxSearch', 'distributed', 'discarded'],
            update: function(obj, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              var applyChanges = function(source, autoSelect) {
                setOptions({
                  source: source
                });

                setData(source.length > 1 && !autoSelect ? 'SELECT' : source[0]);
              };
              if (!value) {
                applyChanges([''], false);
                return;
              }
              if ((flatProperty === 'discarded') || (flatProperty === 'distributed')) {
                applyChanges([''], true);
                return;
              }
              if (boxSearchCache[value.toLowerCase()]) {
                applyChanges(boxSearchCache[value.toLowerCase()].map(function(item) {
                  return item.alias;
                }),
                    (boxSearchCache[value.toLowerCase()][0].name.toLowerCase() == value.toLowerCase()
                        || boxSearchCache[value.toLowerCase()][0].alias.toLowerCase() == value.toLowerCase() || (boxSearchCache[value
                        .toLowerCase()][0].identificationBarcode && boxSearchCache[value.toLowerCase()][0].identificationBarcode
                        .toLowerCase() == value.toLowerCase())));
                return;
              }

              setData('(...searching...)');
              var deferred = jQuery.Deferred();
              jQuery
                  .ajax({
                    url: '/miso/rest/boxes/search/partial?' + jQuery.param({
                      q: value,
                      b: false
                    }),
                    contentType: "application/json; charset=utf8",
                    dataType: "json"
                  })
                  .success(
                      function(data) {
                        boxSearchCache[value.toLowerCase()] = data;
                        jQuery.each(data, function(index, item) {
                          if (!boxesByAlias[item.alias]) {
                            cacheBox(item);
                          }
                        });
                        if (!data.length) {
                          applyChanges([''], false);
                        } else {
                          applyChanges(
                              data.map(function(item) {
                                return item.alias;
                              }),
                              (data[0].name.toLowerCase() == value.toLowerCase() || data[0].alias.toLowerCase() == value.toLowerCase() || (data[0].identificationBarcode && data[0].identificationBarcode
                                  .toLowerCase() == value.toLowerCase())));
                        }
                      }).fail(function(response, textStatus, serverStatus) {
                    applyChanges([''], false);
                    HotUtils.showServerErrors(response, serverStatus);
                  }).always(function() {
                    deferred.resolve();
                  });
              return deferred.promise();
            }
          },
          {
            header: 'Position',
            data: 'boxPosition',
            type: 'dropdown',
            include: true,
            trimDropdown: false,
            source: [''],
            unpack: function(obj, flat, setCellMeta) {
              flat.boxPosition = obj.boxPosition || null;
              setCellMeta('validator', (obj.box && obj.box.alias) ? HotUtils.validator.requiredAutocomplete
                  : HotUtils.validator.requiredEmpty);
            },
            pack: function(obj, flat, errorHandler) {
              obj.boxPosition = flat.boxPosition;
              if (flat.boxPosition && !flat.boxAlias && !config.box) {
                errorHandler('No box specified for box position');
              } else if ((flat.boxAlias || !!config.box) && !flat.boxPosition) {
                errorHandler('Box position missing');
              }
            },
            depends: config.box ? '*start' : 'boxAlias',
            update: function(obj, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (flatProperty != '*start') {
                setReadOnly(!value);
                setOptions({
                  validator: value ? HotUtils.validator.requiredAutocomplete : HotUtils.validator.requiredEmpty
                });
              }
              var box = config.box || (!value ? null : boxesByAlias[value]);
              if (!box) {
                setData('');
                setOptions({
                  source: ['']
                });
              } else {
                setOptions({
                  source: box.emptyPositions
                });
              }
            }
          }, makeDiscardedColumn(),
          // Distribution to collaborator or outside destination
          {
            header: 'Distributed',
            data: 'distributed',
            type: 'dropdown',
            trimDropdown: false,
            source: ['Sent Out', 'No'],
            include: !config.create && !config.propagate && (!Constants.isDetailedSample || !isTargetIdentity(config)) && !config.isLibraryReceipt,
            unpack: function(obj, flat, setCellMeta) {
              if (obj.distributed === true) {
                flat.distributed = 'Sent Out';
              } else {
                flat.distributed = 'No';
              }
            },
            pack: function(obj, flat, errorHandler) {
              if (flat.distributed === 'Sent Out') {
                obj.distributed = true;
              } else {
                obj.distributed = false;
              }
            }
          }, {
            header: 'Distribution Date',
            data: 'distributionDate',
            type: 'date',
            dateFormat: 'YYYY-MM-DD',
            datePickerConfig: {
              firstDay: 0,
              numberOfMonths: 1
            },
            allowEmpty: true,
            include: !config.create && !config.propagate && (!Constants.isDetailedSample || !isTargetIdentity(config)) && !config.isLibraryReceipt,
            depends: 'distributed',
            update: function(obj, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (value === 'Sent Out') {
                setReadOnly(false);
                setOptions({
                  required: true,
                  validator: HotUtils.validator.requiredTextNoSpecialChars
                });
              } else {
                setReadOnly(true);
                setOptions({
                  validator: HotUtils.validator.requiredEmpty
                });
                setData(null);
              }
            },
            unpack: function(obj, flat, setCellMeta) {
              if (obj.distributionDate) {
                flat.distributionDate = Utils.valOrNull(obj.distributionDate);
              }
            },
            pack: function(obj, flat, errorHandler) {
              obj.distributionDate = flat.distributionDate;
            }
          }, {
            header: 'Distribution Recipient',
            data: 'distributionRecipient',
            type: 'text',
            include: !config.create && !config.propagate && (!Constants.isDetailedSample || !isTargetIdentity(config)) && !config.isLibraryReceipt,
            depends: 'distributed',
            update: function(obj, flat, flatProperty, value, setReadOnly, setOptions, setData) {
              if (value === 'Sent Out') {
                setOptions({
                  required: true,
                  validator: HotUtils.validator.requiredTextNoSpecialChars
                });
                setReadOnly(false);
              } else {
                setOptions({
                  validator: HotUtils.validator.requiredEmpty
                });
                setData(null);
                setReadOnly(true);
              }
            },
            unpack: function(obj, flat, setCellMeta) {
              if (obj.distributionRecipient) {
                flat.distributionRecipient = Utils.valOrNull(obj.distributionRecipient);
              }
            },
            pack: function(obj, flat, errorHandler) {
              obj.distributionRecipient = flat.distributionRecipient;
            }
          }];
    },
    getCustomActions: function(table) {
      return [{
        buttonText: 'Fill Boxes by Row',
        eventHandler: function() {
          fillBoxPositions(table, function(a, b) {
            return Utils.sorting.sortBoxPositions(a, b, true);
          });
        }
      }, {
        buttonText: 'Fill Boxes by Column',
        eventHandler: function() {
          fillBoxPositions(table, function(a, b) {
            return Utils.sorting.sortBoxPositions(a, b, false);
          });
        }
      }];
    }
  }

})();
