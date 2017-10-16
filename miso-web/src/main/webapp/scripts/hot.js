/**
 * Module for Handsontable code which is shared between multiple instances
 */
var HotUtils = {
  /** Request counter for AJAX calls */
  counter: 0,
  serverErrors: [],

  validator: {
    /**
     * Custom validator for text fields that must contain data
     */
    requiredText: function(value, callback) {
      return callback(!Utils.validation.isEmpty(value));
    },

    /**
     * Custom validator for dropdown fields that may remain empty
     */
    permitEmptyDropdown: function(value, callback) {
      if (Utils.validation.isEmpty(value)) {
        return callback(true);
      } else {
        return Handsontable.AutocompleteValidator.call(this, value, callback)
      }
    },

    /**
     * Custom validator for required numeric fields
     */
    requiredNumber: function(value, callback) {
      return callback(!Utils.validation.isEmpty(value) && Handsontable.helper.isNumeric(value));
    },

    /**
     * Custom validator for optional numeric fields
     */
    optionalNumber: function(value, callback) {
      return callback(Utils.validation.isEmpty(value) || Handsontable.helper.isNumeric(value));
    },

    /**
     * Custom validator for text fields that fails on extra-special characters
     */
    noSpecialChars: function(value, callback) {
      return callback(!/[;''\\]+/g.test(value) && value != undefined && value != null && value != '' && value.length > 0);
    },
    /**
     * Custom validator for text fields that fails on empty or extra-special characters
     */
    requiredTextNoSpecialChars: function(value, callback) {
      return callback(!Utils.validation.isEmpty(value) && Utils.validation.hasNoSpecialChars(value));
    },

    /**
     * Custom validator for text fields that fails on empty or extra-special characters, but passes if text is empty
     */
    optionalTextNoSpecialChars: function(value, callback) {
      return callback(Utils.validation.isEmpty(value) || Utils.validation.hasNoSpecialChars(value))
    },

    /**
     * Custom validator for alphanumeric text fields
     */
    optionalTextAlphanumeric: function(value, callback) {
      var regex = new RegExp(Utils.validation.alphanumRegex);
      return callback(Utils.validation.isEmpty(value) || regex.test(value));
    },

    /**
     * Custom validator for dropdown fields that fail on empty or if the value is not a member of the source array
     */
    requiredAutocomplete: function(value, callback) {
      if (Utils.validation.isEmpty(value)) {
        callback(false);
      } else {
        Handsontable.AutocompleteValidator.call(this, value, callback);
      }
    }
  },
  /**
   * Create a Handsontables for our data. This involves a “target” that knows how to handle the particulars of our object type, whether
   * these are being created or modified and a list of data items.
   */
  makeTable: function(target, create, data, config) {
    var hotContainer = document.getElementById('hotContainer');
    // Get all the columns we intend to show and create a “flat” dummy object
    // for each row in the table.
    var columns = target.createColumns(config, create, data).filter(function(c) {
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
            'row': rowIndex,
            'col': colIndex,
            'key': key,
            'val': val
          });
        });
      });
      return flatObj;
    });
    var anyInvalidCells = false;
    var table = new Handsontable(hotContainer, {
      debug: true,
      fixedColumnsLeft: target.fixedColumns === undefined ? 1 : target.fixedColumns,
      manualColumnResize: true,
      rowHeaders: false,
      colHeaders: columns.map(function(c) {
        return c.header;
      }),
      preventOverflow: 'horizontal',
      contextMenu: false,
      columns: columns,
      data: flatObjects,
      maxRows: data.length,
      renderAllRows: true,
      columnSorting: true,
      cells: function(row, col, prop) {
        var cellProperties = {};

        if (flatObjects[row].saved) {
          cellProperties.readOnly = true;
        }

        return cellProperties;
      },
      afterValidate: function(isValid, value, row, prop, source) {
        if (!isValid) {
          anyInvalidCells = true;
        }
      },
      beforeAutofill: function(start, end, rows) {
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
              template = template.replace(numbersInCell[placeHolder_i], '{' + placeHolder_i + '}');
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
                template = template.replace('{' + numbersInCell_i + '}', currentNumber);
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
                incrementingContentColumns[key]['lastContents'].push(nextNumber);
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
                template = template.replace(placeholder, parseInt(icc['lastContents'][placeholder_i]) + row_i + 1);
              }
              newData.push([currentRow, currentCol, template]);
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
    var initialSetup = true;
    table.addHook('afterChange', function(changes, source) {
      var needsRender = false;
      var synchronous = true;
      // 'changes' is a variable-length array of arrays. Each inner array has
      // the following structure:
      // [rowIndex, colName, oldValue, newValue]
      if (['edit', 'autofill', 'paste'].indexOf(source) == -1) {
        return;
      }
      for (var i = 0; i < changes.length; i++) {
        // trigger only if old value is different from new value
        if (changes[i][2] == changes[i][3]) {
          continue;
        }
        columns.filter(function(column) {
          return column.depends == changes[i][1];
        }).forEach(function(column) {
          var currentChange = changes[i];
          var flat = flatObjects[currentChange[0]];
          var obj = data[currentChange[0]];
          flat[column.data] = '';
          column.update(obj, flat, currentChange[3], function(readOnly) {
            table.setCellMeta(currentChange[0], column.hotIndex, 'readOnly', readOnly);
            needsRender = true;
          }, function(optionsObj) {
            for (prop in optionsObj) {
              if (optionsObj.hasOwnProperty(prop)) {
                table.setCellMeta(currentChange[0], column.hotIndex, prop, optionsObj[prop]);
                needsRender = true;
              }
            }
            if (needsRender && !synchronous && !initialSetup) {
              table.validateCells(function() {
                table.render();
              })
            }
          }, function(value) {
            flatObjects[currentChange[0]][column.data] = value;
            needsRender = true;

            if (needsRender && !synchronous && !initialSetup) {
              table.validateCells(function() {
                table.render();
              })
            }
          });
        });
      }

      if (needsRender) {
        table.validateCells(function() {
          table.render();
        });
      }
      synchronous = false;
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
        }, function(optionsObj) {
          for (prop in optionsObj) {
            if (optionsObj.hasOwnProperty(prop)) {
              table.setCellMeta(i, column.hotIndex, prop, optionsObj[prop]);
              needsRender = true;
            }
          }
        }, function(value) {
          // Ignore any attempts to change the data thus far.
        });
      }
    });

    var makeSortButton = function(sortOption, sortColIndex) {
      var rowCount = table.countRows();
      function sortListener() {
        for (var i = 0; i < rowCount; i++) {
          table.setCellMeta(i, sortColIndex, 'sortFunction', sortOption.sortFunc);
        }
        table.sort(sortColIndex);
      }
      var button = document.createElement('input');
      button.type = 'button';
      button.id = 'sort' + sortOption.sortTarget;
      button.value = sortOption.buttonText;
      button.addEventListener('click', sortListener);
      return button;
    };

    // Columns that have custom sorters need to make the sorting accessible
    columns.filter(function(column) {
      return column.customSorting;
    }).forEach(function(column) {
      column.customSorting.forEach(function(sortOption) {
        var sortBy = makeSortButton(sortOption, column.hotIndex);
        document.getElementById('bulkactions').appendChild(sortBy);
      });
    });

    var save = document.getElementById('save');
    save
        .addEventListener(
            'click',
            function() {
              // reset server error messages
              HotUtils.serverErrors = [];
              // We are now saving the contents of the table. This can be called
              // multiple times if the save was unsuccessful
              var failed = [];
              var okToSave = true;
              // This is called when there might be errors to display on the
              // page.
              function renderErrors() {
                var errorClasses = document.getElementById('errors').classList;

                if (failed.length) {

                  var saveErrorMessages = document.getElementById('saveErrors');
                  if (!document.getElementById('failedToSave')) {
                    // add "failed to save" if not already present
                    var failedToSave = document.createElement('P');
                    failedToSave.id = 'failedToSave';
                    failedToSave.innerText = 'The following rows failed to save:';
                    saveErrorMessages.parentNode.insertBefore(failedToSave, saveErrorMessages);
                  }
                  saveErrorMessages.innerHTML = '<ul>' + failed.map(function(msg) {
                    return '<li>' + msg + '</li>';
                  }).join('') + '</ul>';
                  errorClasses.remove('hidden');
                } else {
                  if (!errorClasses.contains('hidden')) {
                    errorClasses.add('hidden');
                  }
                }
              }

              function updateFlatObjAfterSave(flatObj, item) {
                columns.forEach(function(c, colIndex) {
                  if (c.unpackAfterSave) {
                    c.unpack(item, flatObj, function(key, val) {
                      // Do nothing. We're unpacking only - not setting cell
                      // meta
                    });
                  }
                });
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
                          table.setCellMeta(i, c.hotIndex, 'valid', false);
                          errorHandler(errorMessage);
                        });
                      });
                      target.fixUp(data[i], errorHandler);
                    }
                    if (!okToSave) {
                      if (failed.length == 0) {
                        failed.push('It looks like some cells are not yet valid. Please fix them before saving.');
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
                        var saveSuccessesClasses = document.getElementById('saveSuccesses').classList;
                        if (numSaved > 0) {
                          var successMessageDiv = document.getElementById('successMessages');
                          successMessageDiv.innerHTML = 'Saved ' + numSaved + ' items.';
                          if (allSaved) {
                            var bulkActionsDiv = document.getElementById('bulkactions');
                            target.bulkActions.forEach(function(bulkAction) {
                              var button;
                              if (bulkAction) {
                                button = document.createElement('A');
                                button.href = '#';
                                button.setAttribute('class', 'ui-button ui-state-default');
                                button.setAttribute('title', bulkAction.title || '');
                                button.onclick = function() {
                                  bulkAction.action(data);
                                };
                                button.appendChild(document.createTextNode(bulkAction.name));
                              } else {
                                button = document.createElement('SPAN');
                                button.setAttribute('class', 'ui-state-default');
                              }
                              bulkActionsDiv.appendChild(button);
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
                      if (flatObjects[index].saved) {
                        invokeNext(index + 1);
                        return;
                      }
                      // This item must be saved. Send it to the server.
                      var xhr = new XMLHttpRequest();
                      xhr.onreadystatechange = function() {
                        if (xhr.readyState === XMLHttpRequest.DONE) {
                          if (xhr.status === 200 || xhr.status === 201) {
                            data[index] = JSON.parse(xhr.response);
                            updateFlatObjAfterSave(flatObjects[index], data[index]);
                            flatObjects[index].saved = true;
                          } else {
                            try {
                              var response = JSON.parse(xhr.responseText);
                              failed
                                  .push('<b>Row '
                                      + (index + 1)
                                      + ': '
                                      + (response.detail || 'Something went terribly wrong. Please file a ticket with a screenshot or copy-paste of the data that you were trying to save.</b>'));
                            } catch (e) {
                              failed
                                  .push('<b>Row '
                                      + (index + 1)
                                      + ': The server is talking nonsense again. Please file a ticket with a screenshot or copy-paste of the data that you were trying to save.</b>');
                            }
                          }
                          invokeNext(index + 1);
                        }
                      };
                      xhr.open(create ? 'POST' : 'PUT', create ? target.createUrl : (target.updateUrl + data[index].id));
                      xhr.setRequestHeader('Content-Type', 'application/json');
                      xhr.setRequestHeader('Accept', 'application/json');
                      xhr.send(JSON.stringify(data[index]));
                    };
                    invokeNext(0);
                  });
            });
    table.validateCells(function() {
      table.render();
      initialSetup = false;
    });
  },

  sorting: {
    /** Sorts by box row: A01, A02, B01, B03, H02 */
    rowSort: function(sortOrder) {
      return function(a, b) {
        // a & b are each an array: [row_index, element_value]
        return Utils.sorting.sortBoxPositions(a[1], b[1], true);
      }
    },

    /**
     * Sorts by box column: A01, B01, A02, H02, B03. Useful for applying indices to libraries using the Sciclone machine.
     */
    colSort: function(sortOrder) {
      return function(a, b) {
        // a & b are each an array: [row_index, element_value]
        return Utils.sorting.sortBoxPositions(a[1], b[1], false);
      }
    }
  },

  showServerErrors: function(response, serverStatus) {
    var responseText = JSON.parse(response.responseText);
    var alreadyShown = HotUtils.serverErrors.filter(function(error) {
      if (error.status == serverStatus && error.message == responseText.detail) {
        // if it exists, update the number
        error.rows++;
        return true;
      } else {
        return false;
      }
    });
    if (!alreadyShown.length) {
      // add this response to the server errors to display
      HotUtils.serverErrors.push({
        rows: 1,
        message: responseText.detail,
        status: serverStatus
      });
    }

    if (HotUtils.serverErrors.length) {
      var serverErrorMessages = document.getElementById('serverErrors');

      serverErrorMessages.innerHTML = '<ul>' + HotUtils.serverErrors.map(function(error) {
        return '<li>' + error.rows + ' ' + error.status + ' error' + (error.rows == 1 ? '' : 's') + '. ' + error.message + '</li>';
      }).join('') + '</ul>';

      document.getElementById('errors').classList.remove('hidden');
    }
  },

  makeCellNSAlias: function(setCellMeta) {
    setCellMeta('renderer', function(instance, td, row, col, prop, value, cellProperties) {
      Handsontable.TextRenderer.apply(this, arguments);
      td.classList.add('nonStandardAlias');
      return td;
    });
    jQuery('#nonStandardAliasNote').show();
  },

  multipleOptionsRenderer: function(instance, td, row, col, prop, value, cellProperties) {
    Handsontable.AutocompleteRenderer.apply(this, arguments);
    td.classList.add('multipleOptions');
    return td;
  },

  makeColumnForConstantsList: function(headerName, include, flatProperty, modelProperty, id, name, items, required, baseobj, sortFunc) {
    var labels = items.sort(sortFunc || Utils.sorting.standardSort(name)).map(function(item) {
      return item[name];
    });
    if (!required)
      labels.unshift('(None)');
    if (!baseobj)
      baseobj = {};
    baseobj.header = headerName;
    baseobj.include = include;
    baseobj.data = flatProperty;
    baseobj.type = 'dropdown';
    baseobj.trimDropdown = false;
    baseobj.source = labels;
    /*
     * if it's not a required field, '(None)' will be present in the list and selected by default
     */
    baseobj.validator = HotUtils.validator.requiredAutocomplete;
    baseobj.unpack = function(obj, flat, setCellMeta) {
      flat[flatProperty] = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
        return item[id] == obj[modelProperty];
      }, items), name) || (required ? '' : '(None)');
    };
    baseobj.pack = function(obj, flat, errorHandler) {
      obj[modelProperty] = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
        return item[name] == flat[flatProperty];
      }, items), id);
    };
    return baseobj;
  },

  makeColumnForBoolean: function(headerName, include, property, required) {
    return {
      header: headerName,
      data: property,
      type: 'dropdown',
      trimDropdown: false,
      source: required ? ['True', 'False'] : ['Unknown', 'True', 'False'],
      include: include,
      unpack: function(obj, flat, setCellMeta) {
        var result;
        if (obj[property] === true) {
          result = 'True';
        } else if (obj[property] === false) {
          result = 'False';
        } else if (required) {
          errorHandler(headerName + ' is missing');
          return;
        } else {
          result = 'Unknown';
        }
        flat[property] = result;
      },
      pack: function(obj, flat, errorHandler) {
        if (flat[property] === 'True') {
          obj[property] = true;
        } else if (flat[property] === 'False') {
          obj[property] = false;
        } else if (required) {
          errorHandler(headerName + ' is missing');
          return;
        } else {
          obj[property] = null;
        }
      }
    };
  },

  makeColumnForFloat: function(headerName, include, property, required) {
    return {
      'header': headerName,
      'data': property,
      'type': 'text',
      'include': include,
      'unpack': function(obj, flat, setCellMeta) {
        flat[property] = obj[property] || null;
      },
      'validator': required ? HotUtils.validator.requiredNumber : HotUtils.validator.optionalNumber,
      'pack': function(obj, flat, errorHandler) {
        var output;
        if (Utils.validation.isEmpty(flat[property])) {
          if (required) {
            errorHandler(headerName + ' is required.');
            return;
          }
          output = null;
        } else {
          var result = parseFloat(flat[property]);
          if (isNaN(result)) {
            errorHandler(headerName + ' is not a number.');
            return;
          }
          output = result;
        }
        obj[property] = output;
      }
    };
  },

  makeColumnForInt: function(headerName, include, property, validator) {
    return {
      'header': headerName,
      'data': property,
      'type': 'numeric',
      'include': include,
      'validator': validator,
      'unpack': function(obj, flat, setCellMeta) {
        flat[property] = obj[property] || null;
      },
      'pack': function(obj, flat, errorHandler) {
        if (!Utils.validation.isEmpty(flat[property])) {
          obj[property] = flat[property];
        } else {
          obj[property] = null;
        }
      }
    }
  },

  makeColumnForText: function(headerName, include, property, baseobj) {
    baseobj.header = headerName;
    baseobj.data = property;
    baseobj.type = 'text';
    baseobj.include = include;
    if (!baseobj.hasOwnProperty('unpack')) {
      baseobj.unpack = function(obj, flat, setCellMeta) {
        flat[property] = obj[property] || null;
      };
    }
    baseobj.pack = function(obj, flat, errorHandler) {
      if (!Utils.validation.isEmpty(flat[property])) {
        obj[property] = flat[property];
      } else {
        obj[property] = null;
      }
    };
    return baseobj;
  },

  makeColumnForEnum: function(headerName, include, required, property, source, defaultValue) {
    return {
      'header': headerName,
      'data': property,
      'type': 'dropdown',
      'trimDropdown': false,
      'source': source,
      'include': include,
      'validator': (required ? HotUtils.validator.requiredAutocomplete : Handsontable.AutocompleteValidator),
      'unpack': function(obj, flat, setCellMeta) {
        flat[property] = obj[property] || defaultValue;
      },
      'pack': function(obj, flat, errorHandler) {
        obj[property] = flat[property];
      }
    }
  },

  printAction: function(type) {
    return {
      name: 'Print Barcode(s)',
      action: function(items) {
        Utils.printDialog(type, items.map(Utils.array.getId));
      },
      allowOnLibraryPage: true
    };
  },

  makeQcActions: function(qcTarget) {
    return [null, {
      name: 'Add QCs',
      action: function(items) {
        Utils.showDialog('Add QCs', 'Add', [{
          property: 'copies',
          type: 'int',
          label: 'QCs per ' + qcTarget,
          value: 1
        }, ], function(result) {
          window.location = window.location.origin + '/miso/qc/bulk/addFrom/' + qcTarget + '?' + jQuery.param({
            entityIds: items.map(Utils.array.getId).join(','),
            copies: result.copies

          });
        });
      }
    }, {
      name: 'Edit QCs',
      action: function(items) {
        window.location = window.location.origin + '/miso/qc/bulk/editFrom/' + qcTarget + '?' + jQuery.param({
          entityIds: items.map(Utils.array.getId).join(',')
        });
      }
    }, ];
  },
};

HotTarget = {};
