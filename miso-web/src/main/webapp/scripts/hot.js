/**
 * Module for Handsontable code which is shared between multiple instances
 */
var HotUtils = {
  validator : {
    /**
     * Custom validator for fields that must contain data
     */
    requiredText : function(value, callback) {
      return callback(!Utils.validation.isEmpty(value));
    },
    
    /**
     * Custom validator for fields that may remain empty
     */
    permitEmpty : function(value, callback) {
      return callback(true);
    },
    
    /**
     * Custom validator for required numeric fields
     */
    requiredNumber : function(value, callback) {
      return callback(!Utils.validation.isEmpty(value) && Handsontable.helper
          .isNumeric(value));
    },
    
    /**
     * Custom validator for text fields that fails on extra-special characters
     */
    noSpecialChars : function(value, callback) {
      return callback(!/[;''\\]+/g.test(value) && value != undefined && value != null && value != '' && value.length > 0);
    },
    /**
     * Custom validator for text fields that fails on empty or extra-special
     * characters
     */
    requiredTextNoSpecialChars : function(value, callback) {
      return callback(!Utils.validation.isEmpty(value) && Utils.validation
          .hasNoSpecialChars(value));
    },
    optionalTextNoSpecialChars : function(value, callback) {
      return callback(Utils.validation.isEmpty(value) || Utils.validation
          .hasNoSpecialChars(value))
    },
    
    /**
     * Custom validator for alphanumeric text fields
     */
    optionalTextAlphanumeric : function(value, callback) {
      var regex = new RegExp(Utils.validation.alphanumRegex);
      return callback(Utils.validation.isEmpty(value) || regex.test(value));
    },
  },
  /**
   * Create a Handsontables for our data. This involves a “target” that knows
   * how to handle the particulars of our object type, whether these are being
   * created or modified and a list of data items.
   */
  makeTable : function(target, create, data, config) {
    var hotContainer = document.getElementById('hotContainer');
    // Get all the columns we intend to show and create a “flat” dummy object
    // for each row in the table.
    var columns = target.createColumns(config, create, data).filter(
        function(c) {
          return c.include;
        });
    columns.forEach(function(c, i) {
      c.hotIndex = i;
    });
    var cellMetaData = [];
    var flatObjects = data.map(function(item, rowIndex) {
      var flatObj = {};
      columns.forEach(function(c, colIndex) {
        c.unpack(item, flatObj, function(key, val) {
          cellMetaData.push({
            'row' : rowIndex,
            'col' : colIndex,
            'key' : key,
            'val' : val
          });
        });
      });
      return flatObj;
    });
    var anyInvalidCells = false;
    var table = new Handsontable(
        hotContainer,
        {
          debug : true,
          fixedColumnsLeft : 1,
          manualColumnResize : true,
          rowHeaders : true,
          colHeaders : columns.map(function(c) {
            return c.header;
          }),
          contextMenu : false,
          columns : columns,
          data : flatObjects,
          maxRows : data.length,
          cells : function(row, col, prop) {
            var cellProperties = {};
            
            if (flatObjects[row].saved) {
              cellProperties.readOnly = true;
            }
            
            return cellProperties;
          },
          afterValidate : function(isValid, value, row, prop, source) {
            if (!isValid) {
              anyInvalidCells = true;
            }
          },
          beforeAutofill : function(start, end, rows) {
            var incrementingContentColumns = {};
            var valid = true;
            (function() {
              // validation check
              var cols = rows[rows.length - 1];
              if (cols.length !== (end.col - start.col) + 1) {
                valid = false;
                return;
              }
              if (rows.length !== 2) {
                valid = false;
                return;
              }
              if (!cols) {
                valid = false;
                return;
              }
              for (var cols_i = 0; cols_i < cols.length; cols_i++) {
                if (!cols[cols_i]) {
                  valid = false;
                  return;
                }
              }
            })();
            if (!valid) {
              // allow default functionality to take over.
              return;
            }
            (function() {
              // prepare data
              var rows_i = 0;
              var cols = rows[rows_i];
              for (var cols_i = 0; cols_i < cols.length; cols_i++) {
                var cellContents = '' + cols[cols_i];
                // regex iterative with /g, so it will return an
                // array of
                // matches.
                var numbersInCell = cellContents.match(/\d+/g);
                var template = cellContents;
                if (!numbersInCell) {
                  break;
                }
                for (var placeHolder_i = 0; placeHolder_i < numbersInCell.length; placeHolder_i++) {
                  template = template.replace(numbersInCell[placeHolder_i],
                      '{' + placeHolder_i + '}');
                }
                for (var numbersInCell_i = 0; numbersInCell_i < numbersInCell.length; numbersInCell_i++) {
                  var incrementing = false;
                  var currentNumber = numbersInCell[numbersInCell_i];
                  var nextRow_i = 1;
                  var nextContents = '' + rows[nextRow_i][cols_i];
                  var nextNumbersInCell = nextContents.match(/\d+/g);
                  if (!nextNumbersInCell) {
                    break;
                  }
                  var nextNumber = nextNumbersInCell[numbersInCell_i];
                  if (nextNumber == (parseInt(currentNumber) + 1)) {
                    incrementing = true;
                  } else {
                    template = template.replace('{' + numbersInCell_i + '}',
                        currentNumber);
                    if (!incrementingContentColumns[key]) {
                      incrementingContentColumns[key] = {};
                      incrementingContentColumns[key]['template'] = template;
                      incrementingContentColumns[key]['lastContents'] = [];
                    } else {
                      incrementingContentColumns[key]['template'] = template;
                    }
                    incrementing = false;
                    continue;
                  }
                  if (incrementing) {
                    var key = cols_i;
                    if (!incrementingContentColumns[key]) {
                      incrementingContentColumns[key] = {};
                      incrementingContentColumns[key]['template'] = template;
                      incrementingContentColumns[key]['lastContents'] = [];
                    }
                    incrementingContentColumns[key]['lastContents']
                        .push(nextNumber);
                  }
                }
              }
            })();
            if (jQuery.isEmptyObject(incrementingContentColumns)) {
              // obj is empt!
              return;
            }
            var newData = [];
            (function() {
              // add changes to newData
              var startRow = start['row'];
              var endRow = end['row'];
              var rowsToFill = (endRow - startRow) + 1;
              for (var row_i = 0; row_i < rowsToFill; row_i++) {
                var currentRow = start['row'] + row_i;
                for (var icc_i = 0; icc_i < (end['col'] - start['col']) + 1; icc_i++) {
                  var currentCol = start['col'] + icc_i;
                  var icc = incrementingContentColumns[icc_i];
                  if (!icc) {
                    continue;
                  }
                  var template = icc['template'];
                  var placeholders = template.match(/{\d+}/g);
                  for (var placeholder_i = 0; placeholder_i < placeholders.length; placeholder_i++) {
                    var placeholder = placeholders[placeholder_i];
                    template = template
                        .replace(
                            placeholder,
                            parseInt(icc['lastContents'][placeholder_i]) + row_i + 1);
                  }
                  newData.push([ currentRow, currentCol, template ]);
                }
              }
            })();
            
            setTimeout(function() {
              // apply changes to table
              table.setDataAtCell(newData);
            }, 200);
          }
        });
    hotContainer.style.display = '';
    
    cellMetaData.forEach(function(data) {
      table.setCellMeta(data.row, data.col, data.key, data.val);
    });
    table.addHook('afterChange', function(changes, source) {
      var needsRender = false;
      // 'changes' is a variable-length array of arrays. Each inner array has
      // the following structure:
      // [rowIndex, colName, oldValue, newValue]
      if ([ 'edit', 'autofill', 'paste' ].indexOf(source) == -1) {
        return;
      }
      for (var i = 0; i < changes.length; i++) {
        // trigger only if old value is different from new value
        if (changes[i][2] == changes[i][3]) {
          continue;
        }
        columns.filter(function(column) {
          return column.depends == changes[i][1];
        }).forEach(
            function(column) {
              var flat = flatObjects[changes[i][0]];
              var obj = data[changes[i][0]];
              flat[column.data] = '';
              column.update(obj, flat, changes[i][3], function(readOnly) {
                table.setCellMeta(changes[i][0], column.hotIndex, 'readOnly',
                    readOnly);
                needsRender = true;
              }, function(values) {
                table.setCellMeta(changes[i][0], column.hotIndex, 'source',
                    values);
                needsRender = true;
              }, function(value) {
                flatObjects[changes[i][0]][column.data] = value;
                needsRender = true;
              });
            });
      }
      
      if (needsRender) {
        table.validateCells(function() {
          table.render();
        });
      }
    });
    
    // For cells that have change notifiers, we have to call them to set up the
    // initial sources.
    columns.filter(function(column) {
      return column.depends;
    }).forEach(function(column) {
      for (var i = 0; i < data.length; i++) {
        var flat = flatObjects[i];
        var obj = data[i];
        column.update(obj, flat, flat[column.depends], function(readOnly) {
          table.setCellMeta(i, column.hotIndex, 'readOnly', readOnly);
          needsRender = true;
        }, function(values) {
          table.setCellMeta(i, column.hotIndex, 'source', values);
          needsRender = true;
        }, function(value) {
          // Ignore any attempts to change the data thus far.
        });
      }
    });
    
    var save = document.getElementById('save');
    save
        .addEventListener(
            'click',
            function() {
              // We are now saving the contents of the table. This can be called
              // multiple times if the save was unsuccessful
              var failed = [];
              var okToSave = true;
              // This is called when there might be errors to display on the
              // page.
              function renderErrors() {
                var saveErrorClasses = document.getElementById('saveErrors').classList;
                
                if (failed.length) {
                  var errorMessages = document.getElementById('errorMessages');
                  errorMessages.innerHTML = '<ul>' + failed.map(function(msg) {
                    return '<li>' + msg + '</li>';
                  }).join('') + '</ul>';
                  
                  saveErrorClasses.remove('hidden');
                } else {
                  if (!saveErrorClasses.contains('hidden')) {
                    saveErrorClasses.add('hidden');
                  }
                }
              }
              
              function toFlatObj(item) {
                var flatObj = {};
                columns.forEach(function(c, colIndex) {
                  c.unpack(item, flatObj, function(key, val) {
                    // Do nothing. We're unpacking only - not setting cell meta
                  });
                });
                return flatObj;
              }
              
              save.disabled = true;
              var ajaxLoader = document.getElementById('ajaxLoader');
              ajaxLoader.classList.remove('hidden');
              // Check if the table is valid and all of the converters are happy
              // with their column values
              anyInvalidCells = false;
              table
                  .validateCells(function() {
                    if (anyInvalidCells) {
                      failed.push('Please fix highlighted cells.');
                      renderErrors();
                      save.disabled = false;
                      ajaxLoader.classList.add('hidden');
                      return;
                    }
                    for (var i = 0; i < data.length; i++) {
                      var errorHandler = function(errorMessage) {
                        okToSave = false;
                        failed.push('Row ' + i + ': ' + errorMessage);
                      };
                      columns.forEach(function(c) {
                        c.pack(data[i], flatObjects[i], function(errorMessage) {
                          table.setCellMeta(i, c.holIndex, 'valid', false);
                          errorHandler(errorMessage);
                        });
                      });
                      target.fixUp(data[i], errorHandler);
                    }
                    if (!okToSave) {
                      if (failed.length == 0) {
                        failed
                            .push('It looks like some cells are not yet valid. Please fix them before saving.');
                      }
                      renderErrors();
                      table.render();
                      save.disabled = false;
                      ajaxLoader.classList.add('hidden');
                      return;
                    }
                    
                    // Save and item, then repeat for the next item.
                    var invokeNext = function(index) {
                      // We have (attempted) to save all the items.
                      if (index >= data.length) {
                        var numSaved = flatObjects.reduce(function(acc, item) {
                          return acc + (item.saved ? 1 : 0);
                        }, 0);
                        
                        var allSaved = numSaved == data.length;
                        var saveSuccessesClasses = document
                            .getElementById('saveSuccesses').classList;
                        if (numSaved > 0) {
                          var successMessageDiv = document
                              .getElementById('successMessages');
                          successMessageDiv.innerHTML = 'Saved ' + numSaved + ' items.';
                          if (allSaved) {
                            var bulkActionsDiv = document
                                .getElementById('bulkactions');
                            var ids = data.map(Utils.array.getId);
                            target.bulkActions
                                .forEach(function(bulkAction) {
                                  var link = document.createElement('A');
                                  link.href = '#';
                                  link.setAttribute('class',
                                      'ui-button ui-state-default');
                                  link.setAttribute('title',
                                      bulkAction.title || '');
                                  link.onclick = function() {
                                    bulkAction.action(ids);
                                  };
                                  link.appendChild(document
                                      .createTextNode(bulkAction.name));
                                  bulkActionsDiv.append(link);
                                });
                          }
                          saveSuccessesClasses.remove('hidden');
                        } else {
                          saveSuccessesClasses.add('hidden');
                        }
                        renderErrors();
                        table.render();
                        save.disabled = allSaved;
                        ajaxLoader.classList.add('hidden');
                        return;
                      }
                      // If this item was previously saved, continue along.
                      if (data[index].saved) {
                        invokeNext(index + 1);
                        return;
                      }
                      // This item must be saved. Send it to the server.
                      var xhr = new XMLHttpRequest();
                      xhr.onreadystatechange = function() {
                        if (xhr.readyState === XMLHttpRequest.DONE) {
                          if (xhr.status === 200 || xhr.status === 201) {
                            data[index] = JSON.parse(xhr.response);
                            flatObjects[index] = toFlatObj(data[index]);
                            flatObjects[index].saved = true;
                          } else {
                            try {
                              var response = JSON.parse(xhr.responseText);
                              failed
                                  .push('<b>Row ' + (index + 1) + ': ' + (response.detail || 'Something went terribly wrong. Please file a ticket with a screenshot or copy-paste of the data that you were trying to save.</b>'));
                            } catch (e) {
                              failed
                                  .push('<b>Row ' + (index + 1) + ': The server is talking nonsense again. Please file a ticket with a screenshot or copy-paste of the data that you were trying to save.</b>');
                            }
                          }
                          invokeNext(index + 1);
                        }
                      };
                      xhr.open(create ? 'POST' : 'PUT', create
                          ? target.createUrl
                          : (target.updateUrl + data[index].id));
                      xhr.setRequestHeader('Content-Type', 'application/json');
                      xhr.setRequestHeader('Accept', 'application/json');
                      xhr.send(JSON.stringify(data[index]));
                    };
                    invokeNext(0);
                    
                  });
            });
    table.validateCells(function() {
      table.render();
    });
  },
  
  makeCellNSAlias : function(setCellMeta) {
    setCellMeta('validator', HotUtils.validator.requiredTextNoSpecialChars);
    setCellMeta('renderer', function(instance, td, row, col, prop, value,
        cellProperties) {
      Handsontable.renderers.TextRenderer.apply(this, arguments);
      td.classList.add('nonStandardAlias');
      return td;
    });
    jQuery('#nonStandardAliasNote').show();
  },
  
  makeColumnForConstantsList : function(headerName, include, flatProperty,
      modelProperty, id, name, items, baseobj) {
    baseobj.header = headerName;
    baseobj.include = include;
    baseobj.data = flatProperty;
    baseobj.type = 'dropdown';
    baseobj.trimDropdown = false;
    baseobj.source = items.map(function(item) {
      return item[name];
    }).sort();
    baseobj.validator = HotUtils.validator.requiredText;
    baseobj.include = include;
    baseobj.unpack = function(obj, flat, setCellMeta) {
      flat[flatProperty] = Utils.array.maybeGetProperty(Utils.array
          .findFirstOrNull(function(item) {
            return item[id] == obj[modelProperty];
          }, items), name);
    };
    baseobj.pack = function(obj, flat, errorHandler) {
      obj[modelProperty] = Utils.array.maybeGetProperty(Utils.array
          .findFirstOrNull(function(item) {
            return item[name] == flat[flatProperty];
          }, items), id);
    };
    return baseobj;
  },
  
  makeColumnForOptionalBoolean : function(headerName, include, property) {
    return {
      header : headerName,
      data : property,
      type : 'dropdown',
      trimDropdown : false,
      source : [ 'Unknown', 'True', 'False' ],
      include : include,
      unpack : function(obj, flat, setCellMeta) {
        var result;
        if (obj[property] === true) {
          result = 'True';
        } else if (obj[property] === false) {
          result = 'False';
        } else {
          result = 'Unknown';
        }
        flat[property] = result;
      },
      pack : function(obj, flat, errorHandler) {
        if (flat[property] === 'True') {
          obj[property] = true;
        } else if (flat[property] === 'False') {
          obj[property] = false;
        } else {
          obj[property] = null;
        }
      }
    };
  },
  
  makeColumnForFloat : function(headerName, include, property, required) {
    return {
      'header' : headerName,
      'data' : property,
      'type' : 'numeric',
      'include' : true,
      'unpack' : function(obj, flat, setCellMeta) {
        flat[property] = obj[property];
      },
      'validator' : required ? HotUtils.validator.requiredNumber : null,
      'pack' : function(obj, flat, errorHandler) {
        var output = null;
        var raw = flat[property];
        if (raw) {
          var result = parseFloat(raw);
          if (isNaN(result)) {
            errorHandler(flat.dnaSize + ' is not a number.');
          } else {
            output = result;
          }
        }
        obj[property] = output;
      }
    };
  },

};

HotTarget = {};
