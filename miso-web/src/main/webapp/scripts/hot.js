/**
 * Module for Handsontable code which is shared between multiple instances
 */
var HotUtils = {
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
        return Handsontable.validators.AutocompleteValidator.call(this, value, callback)
      }
    },

    /**
     * Custom validator for a field that must be blank
     */
    requiredEmpty: function(value, callback) {
      return callback(!value);
    },

    /**
     * Custom validator for integers
     */
    integer: function(required, min, max) {
      return function(value, callback) {
        if (Utils.validation.isEmpty(value)) {
          return callback(!required);
        }
        if (!/^-?[0-9]*$/g.test(value)) {
          return callback(false);
        }
        if (min !== undefined && min !== null && parseInt(value) < min) {
          return callback(false);
        }
        if (max !== undefined && max !== null && parseInt(value) > max) {
          return callback(false);
        }
        return callback(true);
      };
    },

    /**
     * Custom validator for decimals
     */
    decimal: function(precision, scale, allowNegative, required) {
      var max = Math.pow(10, precision - scale) - Math.pow(0.1, scale);
      var min = allowNegative ? max * -1 : 0;
      var pattern = '^\\d{0,' + (precision - scale) + '}';
      if (scale > 0) {
        pattern += '(?:\\.\\d{1,' + scale + '})?';
      }
      pattern += '$';
      var regex = new RegExp(pattern);
      return {
        min: min,
        max: max,
        validator: function(value, callback) {
          return callback((Utils.validation.isEmpty(value) && !required)
              || (Handsontable.helper.isNumeric(value) && regex.test(value) && value >= min && value <= max));
        }
      };
    },

    regex: function(pattern, required) {
      var regex = new RegExp(pattern);
      return function(value, callback) {
        if (Utils.validation.isEmpty(value)) {
          return callback(!required);
        }
        return callback(regex.test(value));
      }
    },

    /**
     * Custom validator for optional numeric fields
     */
    optionalNumber: function(value, callback) {
      return callback(Utils.validation.isEmpty(value) || Handsontable.helper.isNumeric(value));
    },

    /**
     * Custom validator for optional numeric fields
     */
    requiredNumber: function(value, callback) {
      if (Utils.validation.isEmpty(value)) {
        return callback(false);
      }
      return callback(Handsontable.helper.isNumeric(value));
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
     * Custom validator for text fields that fails on extra-special characters, but passes if text is empty
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
        Handsontable.validators.AutocompleteValidator.call(this, value, callback);
      }
    },

    /**
     * @return Custom validator for dropdown fields that fails on empty or a specified value that represents null/empty (e.g. "None"), or if
     *         the value is not a member of the source array
     */
    requiredAutocompleteWithNullValue: function(nullValue) {
      return function(value, callback) {
        if (value === nullValue) {
          callback(false);
        } else {
          HotUtils.validator.requiredAutocomplete(value, callback);
        }
      }

    },
    /**
     * Custom validator for setting fields to manually be invalid
     */
    invalid: function(value, callback) {
      return callback(false);
    }
  },

  /**
   * Create a Handsontables for our data. This involves a “target” that knows how to handle the particulars of our object type, whether
   * these are being created or modified and a list of data items.
   */
  makeTable: function(target, create, data, config) {
    function drawHeaderWarning() {
      var warnings = [];
      if (target.hasOwnProperty("headerWarnings")) {
        var potentialWarnings = target.headerWarnings(config, data);
        if (potentialWarnings)
          warnings = warnings.concat(potentialWarnings);
      }
      if (warnings.length) {
        var headerWarningsDiv = document.getElementById('creationErrors');
        headerWarningsHTML = '<ul>';
        for (var i = 0; i < warnings.length; ++i) {
          headerWarningsHTML += '<li>' + warnings[i] + '</li>';
        }
        headerWarningsHTML += '</ul>';
        headerWarningsDiv.innerHTML = headerWarningsHTML;
        document.getElementById('warnings').classList.remove('hidden');
      }
    }

    jQuery('#additionalHotNotes').hide();
    jQuery('#additionalHotNotes').empty();
    if (typeof target.getNotes === 'function') {
      var notes = target.getNotes(config);
      if (notes && notes.length) {
        notes.forEach(function(note) {
          jQuery('#additionalHotNotes').append('<p>' + note + '</p>');
        });
        jQuery('#additionalHotNotes').show();
      }
    }

    var hotContainer = document.getElementById('hotContainer');
    // Get all the columns we intend to show and create a “flat” dummy object
    // for each row in the table.
    var columns = target.createColumns(config, create, data).filter(function(c) {
      return c.include;
    });

    if (columns.some(function(c) {
      return c.description;
    })) {
      jQuery('#hotColumnHelp').empty();
      jQuery('#hotColumnHelp').append('</br>');
      jQuery('#hotColumnHelp').append('<p>Column Descriptions:</p>');
    }
    columns.forEach(function(c, i) {
      if (c.description && c.include) {
        jQuery('#hotColumnHelp').append('<p>' + c.header + ' - ' + c.description + '</p>');
      }
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
    var sortFunctions = {};
    var sortEmptyLast = function(sortOrder, sortNonEmpty) {
      return function(value, nextValue) {
        var val = null;
        if (value) {
          val = nextValue ? sortNonEmpty(value, nextValue) : -1;
        } else if (nextValue) {
          val = 1;
        } else {
          val = 0;
        }
        if (sortOrder === 'desc') {
          val = val * -1;
        }
        return val;
      };
    };
    var defaultSort = function(value, nextValue) {
      return value.localeCompare(nextValue);
    };
    var table = new Handsontable(hotContainer, {
      licenseKey: 'non-commercial-and-evaluation',
      fixedColumnsLeft: target.hasOwnProperty('getFixedColumns') ? target.getFixedColumns(config) : 1,
      manualColumnResize: true,
      rowHeaders: false,
      colHeaders: columns.map(function(c) {
        return c.header;
      }),
      viewportColumnRenderingOffset: 700,
      preventOverflow: 'horizontal',
      contextMenu: false,
      columns: columns,
      data: flatObjects,
      maxRows: data.length,
      renderAllRows: true,
      columnSorting: {
        compareFunctionFactory: function(sortOrder, columnMeta) {
          return sortEmptyLast(sortOrder, sortFunctions[columnMeta.data] || defaultSort);
        }
      },
      cells: function(row, col, prop) {
        var cellProperties = {};

        if (flatObjects[row] && flatObjects[row].saved) {
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
            // regex iterative with /g, so it will return an array of matches.
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
                var previous = icc['lastContents'][placeholder_i];
                var placeholder = placeholders[placeholder_i];
                var incrementedInt = parseInt(previous) + row_i + 1;
                var incrementedString = incrementedInt.toString();
                if (previous.startsWith('0')) {
                  var zeroPadding = previous.length - incrementedInt.toString().length;
                  if (zeroPadding > 0) {
                    incrementedString = ('0').repeat(zeroPadding) + incrementedString;
                  }
                }
                template = template.replace(placeholder, incrementedString);
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

    table.sortFunctions = sortFunctions;

    table.getDtoData = function() {
      var errorHandler = function(errorMessage) {
        // ignore errors since we are not saving yet
      };
      for (var i = 0; i < data.length; i++) {
        columns.forEach(function(c) {
          c.pack(data[i], flatObjects[i], function(errorMessage) {
            table.setCellMeta(i, c.hotIndex, 'valid', false);
            errorHandler(errorMessage);
          });
        });
      }
      return data;
    };

    drawHeaderWarning(config, target);

    hotContainer.style.display = '';

    cellMetaData.forEach(function(data) {
      table.setCellMeta(data.row, data.col, data.key, data.val);
    });
    table.addHook('beforeChange', function(changes, source) {
      // Each element of changes contains [rowIndex, colName, oldValue, newValue]
      for (var i = 0; i < changes.length; i++) {
        // auto-select partial matches from dropdowns
        // Note: object returned by getCellMeta doesn't always have the correct type and source, but the one in getCellMetaAtRow does...
        if (!changes[i][3]) {
          continue;
        }
        var cellMetas = table.getCellMetaAtRow(changes[i][0]);
        var column = Utils.array.findUniqueOrThrow(function(cellMeta) {
          return cellMeta.prop === changes[i][1];
        }, cellMetas);
        if ((column.type === 'dropdown' || column.type === 'autocomplete') && column.source && column.source.length) {
          var matchingOptions = column.source.filter(function(option) {
            return option.startsWith(changes[i][3]);
          });
          if (matchingOptions.length === 1) {
            changes[i][3] = matchingOptions[0];
            needsRender = true;
          }
        }
      }
    });
    table.addHook('afterChange', function(changes, source) {
      var needsRender = false;
      // 'changes' is a variable-length array of arrays. Each inner array has
      // the following structure:
      // [rowIndex, colName, oldValue, newValue]
      if (['edit', 'Autofill.fill', 'CopyPaste.paste'].indexOf(source) == -1) {
        return;
      }
      // update function may return a promise. If so, we will wait for it to be resolved/rejected before revalidating and rerendering
      var updateJobs = [];
      var triggeredChanges = [];
      for (var i = 0; i < changes.length; i++) {
        // trigger only if old value is different from new value
        // also trigger on blank -> blank because chained afterChanges seem to miss the old value when clearing cells
        if (changes[i][2] == changes[i][3] && (changes[i][2] || changes[i][3])) {
          continue;
        }
        const currentChange = changes[i];
        const visualRow = currentChange[0];
        const dataRow = table.toPhysicalRow(currentChange[0]);
        const flat = flatObjects[dataRow];
        const obj = data[dataRow];

        columns.filter(function(column) {
          return (Array.isArray(column.depends) && column.depends.indexOf(currentChange[1]) > -1) || column.depends == currentChange[1];
        }).forEach(function(column) {
          var update = column.update(obj, flat, currentChange[1], currentChange[3], function(readOnly) {
            table.setCellMeta(visualRow, column.hotIndex, 'readOnly', readOnly);
            needsRender = true;
          }, function(optionsObj) {
            for (prop in optionsObj) {
              table.setCellMeta(visualRow, column.hotIndex, prop, optionsObj[prop]);
              needsRender = true;
            }
          }, function(value) {
            var oldVal = flat[column.data];
            if (!value || oldVal !== value) {
              flat[column.data] = value;
              needsRender = true;
              triggeredChanges.push([visualRow, column.data, oldVal, value]);
            }
          });
          if (update) {
            updateJobs.push(update);
          }
        });
      }

      // render immediately to allow "loading" type messages
      if (updateJobs.length > 0 && needsRender) {
        table.render();
      }

      jQuery.when.apply(jQuery, updateJobs).always(function() {
        if (needsRender) {
          table.validateCells(function() {
            table.render();
          });
        }
        if (triggeredChanges.length) {
          table.runHooks('afterChange', triggeredChanges, 'Autofill.fill');
        }
      });

      // Need to validate the changes made in beforeChange
      table.validateCells();
    });

    // For cells that have change notifiers, we have to call them to set up the
    // initial sources.
    columns.filter(function(column) {
      return column.depends;
    }).forEach(function(column) {
      for (var i = 0; i < data.length; i++) {
        var flat = flatObjects[i];
        var obj = data[i];
        column.update(obj, flat, '*start', flat[column.depends], function(readOnly) {
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

    var makeSortButton = function(sortOption, sortProperty) {
      var button = document.createElement('SPAN');
      button.setAttribute('class', 'ui-button ui-state-default');
      button.id = 'sort' + sortOption.sortTarget;
      button.innerText = sortOption.buttonText;
      button.addEventListener('click', function() {
        HotUtils.sortTable(table, sortProperty, sortOption.sortFunc);
      });
      return button;
    };

    // Columns that have custom sorters need to make the sorting accessible
    columns.filter(function(column) {
      return column.customSorting;
    }).forEach(function(column) {
      column.customSorting.forEach(function(sortOption) {
        var sortBy = makeSortButton(sortOption, column.data);
        document.getElementById('bulkactions').appendChild(sortBy);
      });
    });

    var save = document.getElementById('save');
    function setSaveDisabled(disabled) {
      save.disabled = disabled;
      if (disabled) {
        jQuery(save).addClass('disabled');
      } else {
        jQuery(save).removeClass('disabled');
      }
    }
    jQuery(save)
        .click(
            function() {
              jQuery
                  .when(target.hasOwnProperty('confirmSave') ? target.confirmSave(flatObjects, create, config, table) : null)
                  .done(
                      function() {
                        if (typeof target.customSave === 'function') {
                          target.customSave(table, config);
                          return;
                        }
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

                        setSaveDisabled(true);
                        var ajaxLoader = document.getElementById('ajaxLoader');
                        ajaxLoader.classList.remove('hidden');
                        // Check if the table is valid and all of the converters are happy
                        // with their column values
                        anyInvalidCells = false;
                        table
                            .validateCells(function() {
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
                              if (anyInvalidCells) {
                                failed.push('Please fix highlighted cells. See the Quick Help section (above) for additional information '
                                    + 'regarding specific fields.');
                                renderErrors();
                                setSaveDisabled(false);
                                ajaxLoader.classList.add('hidden');
                                return;
                              }
                              if (!okToSave) {
                                if (failed.length == 0) {
                                  failed.push('It looks like some cells are not yet valid. Please fix them before saving.');
                                }
                                renderErrors();
                                table.render();
                                setSaveDisabled(false);
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
                                      while (bulkActionsDiv.children.length > 0) {
                                        bulkActionsDiv.removeChild(bulkActionsDiv.lastChild);
                                      }
                                      target.getBulkActions(config).forEach(function(bulkAction) {
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
                                  setSaveDisabled(allSaved);
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
                                        var messageHtml = '<b>Row ' + (index + 1) + ': ';
                                        if (response.detail) {
                                          messageHtml += response.detail;
                                          if (response.dataFormat === 'validation' && response.data) {
                                            messageHtml += '<ul>';
                                            jQuery.each(response.data, function(key, value) {
                                              messageHtml += '<li>' + key + ': ' + value + '</li>';
                                            });
                                            messageHtml += '</ul>';
                                          }
                                        } else {
                                          messageHtml += 'Something went terribly wrong. Please file a ticket with a screenshot or copy-paste of the data that you were trying to save.';
                                        }
                                        messageHtml += '</b>';
                                        failed.push(messageHtml);
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
                                xhr.open(create ? 'POST' : 'PUT', create ? target.getCreateUrl() : (target.getUpdateUrl(data[index].id)));
                                xhr.setRequestHeader('Content-Type', 'application/json');
                                xhr.setRequestHeader('Accept', 'application/json');
                                xhr.send(JSON.stringify(data[index]));
                              };
                              invokeNext(0);
                            });
                      });
            });

    var customActions = [
        {
          buttonText: "Import",
          eventHandler: function() {
            var mainDialog = function() {
              var dialogArea = jQuery('#dialog');
              dialogArea.empty();

              var form = jQuery('<form id="uploadForm">');
              form.append(jQuery('<p><input id="fileInput" type="file" name="file"></p>'));
              dialogArea.append(form);

              var dialog = dialogArea.dialog({
                autoOpen: true,
                width: 500,
                title: 'Import Spreadsheet',
                modal: true,
                buttons: {
                  upload: {
                    id: 'upload',
                    text: 'Import',
                    click: function() {
                      if (!jQuery('#fileInput').val()) {
                        Utils.showOkDialog('Error', ['No file selected!'], mainDialog);
                        return;
                      }
                      var formData = new FormData(jQuery('#uploadForm')[0]);
                      dialog.dialog("close");
                      dialogArea.empty();
                      dialogArea.append(jQuery('<p>Uploading...</p>'));

                      dialog = jQuery('#dialog').dialog({
                        autoOpen: true,
                        height: 400,
                        width: 350,
                        title: 'Uploading File',
                        modal: true,
                        buttons: {},
                        closeOnEscape: false,
                        open: function(event, ui) {
                          jQuery(this).parent().children().children('.ui-dialog-titlebar-close').hide();
                        }
                      });

                      jQuery.ajax({
                        url: '/miso/rest/hot/import',
                        type: 'POST',
                        data: formData,
                        cache: false,
                        contentType: false,
                        processData: false
                      }).success(
                          function(columnData) {
                            dialog.dialog("close");
                            var hotHeaders = table.getColHeader();
                            // validate column headings and row count
                            var errorCols = [];
                            var maxRowLength = 0;
                            columnData.forEach(function(column) {
                              if (hotHeaders.indexOf(column.heading) === -1) {
                                errorCols.push(column.heading);
                              }
                              if (column.data.length > maxRowLength) {
                                maxRowLength = column.data.length;
                              }
                            });
                            if (errorCols.length) {
                              Utils.showOkDialog('Error', ['The following spreadsheet columns do not match any table columns:']
                                  .concat(errorCols.map(function(errorCol) {
                                    return "* " + errorCol;
                                  })));
                              return;
                            }
                            if (maxRowLength > table.countRows()) {
                              Utils.showOkDialog('Error', ['The spreadsheet contains ' + maxRowLength
                                  + ' rows, but the table only contains ' + table.countRows()]);
                              return;
                            }
                            // validate read-only cells
                            for (var colI = 0; colI < columnData.length; colI++) {
                              var columnIndex = hotHeaders.indexOf(columnData[colI].heading);
                              for (var rowIndex = 0; rowIndex < columnData[colI].data.length; rowIndex++) {
                                var cellValue = table.getDataAtCell(rowIndex, columnIndex);
                                var importValue = columnData[colI].data[rowIndex];
                                if (table.getCellMeta(rowIndex, columnIndex).readOnly
                                    && (!!importValue != !!cellValue || (!!importValue && !!cellValue && importValue != cellValue))) {
                                  Utils.showOkDialog('Error', ['Values in read-only column \'' + columnData[colI].heading
                                      + '\' do not match']);
                                  return;
                                }
                              }
                            }
                            // set values from spreadsheet
                            var changes = [];
                            columnData.forEach(function(column) {
                              var columnIndex = hotHeaders.indexOf(column.heading);
                              for (var rowIndex = 0; rowIndex < column.data.length; rowIndex++) {
                                changes.push([rowIndex, columnIndex, column.data[rowIndex]]);
                              }
                            });
                            table.setDataAtCell(changes, 'CopyPaste.paste');
                          }).fail(function(xhr, textStatus, errorThrown) {
                        dialog.dialog("close");
                        Utils.showAjaxErrorDialog(xhr, textStatus, errorThrown);
                      });
                    }
                  },
                  cancel: {
                    id: 'cancel',
                    text: 'Cancel',
                    click: function() {
                      dialog.dialog("close");
                    }
                  }
                }
              });
            }
            mainDialog();
          }
        }, {
          buttonText: "Export",
          eventHandler: function() {
            var data = {
              headers: table.getColHeader(),
              rows: []
            };

            for (var row = 0; row < table.countRows(); row++) {
              data.rows.push(table.getDataAtRow(row));
            }

            Utils.showDialog('Export Data', 'Export', [{
              property: 'format',
              type: 'select',
              label: 'Format',
              values: Constants.spreadsheetFormats,
              getLabel: function(x) {
                return x.description;
              }
            }], function(result) {
              Utils.ajaxDownloadWithDialog('/miso/rest/hot/spreadsheet?format=' + result.format.name, data);
            });
          }
        }];
    if (target.hasOwnProperty('getCustomActions')) {
      customActions = target.getCustomActions(table, config).concat(customActions);
    }
    customActions.forEach(function(action) {
      var button = document.createElement('A');
      button.setAttribute('class', 'ui-button ui-state-default');
      button.innerText = action.buttonText;
      button.href = '#';
      button.addEventListener('click', action.eventHandler);
      document.getElementById('bulkactions').appendChild(button);
    });

    if (typeof target.onLoad === 'function') {
      target.onLoad(config, table);
    }

    table.validateCells(function() {
      table.render();
    });
  },

  sorting: {
    /** Sorts by box row: A01, A02, B01, B03, H02 */
    rowSort: function(a, b) {
      return Utils.sorting.sortBoxPositions(a, b, true);
    },

    /**
     * Sorts by box column: A01, B01, A02, H02, B03. Useful for applying indices to libraries using the Sciclone machine.
     */
    colSort: function(a, b) {
      return Utils.sorting.sortBoxPositions(a, b, false);
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
      Handsontable.renderers.TextRenderer.apply(this, arguments);
      td.classList.add('nonStandardAlias');
      return td;
    });
    jQuery('#nonStandardAliasNote').show();
  },

  multipleOptionsRenderer: function(instance, td, row, col, prop, value, cellProperties) {
    Handsontable.renderers.AutocompleteRenderer.apply(this, arguments);
    td.classList.add('multipleOptions');
    return td;
  },

  notificationRenderer: function(instance, td, row, col, prop, value, cellProperties) {
    Handsontable.renderers.TextRenderer.apply(this, arguments);
    td.classList.add('notification');
    return td;
  },

  makeColumnForConstantsList: function(headerName, include, flatProperty, modelProperty, id, name, items, required, baseobj, sortFunc,
      nullLabel, defaultValue) {
    var labels = !include ? [] : items.sort(sortFunc || Utils.sorting.standardSort(name)).map(function(item) {
      return item[name];
    });
    if (!required)
      labels.unshift(nullLabel || '(None)');
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
      }, items), name) || defaultValue || (required ? null : nullLabel || '(None)');
    };
    baseobj.pack = function(obj, flat, errorHandler) {
      obj[modelProperty] = Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(item) {
        return item[name] == flat[flatProperty];
      }, items), id);
    };
    return baseobj;
  },

  makeAutocompleteColumnForConstantsList: function(headerName, include, flatProperty, modelProperty, id, name, items, required, search,
      getData, baseobj, sortFunc) {
    baseobj = HotUtils.makeColumnForConstantsList(headerName, include, flatProperty, modelProperty, id, name, items, required, baseobj,
        sortFunc);
    baseobj.depends = flatProperty;
    baseobj.update = function(sam, flat, flatProperty, value, setReadOnly, setOptions, setData) {
      if (value) {
        var match = items.find(function(item) {
          return search(item, value);
        });
        if (match) {
          setData(getData(match));
        }
      }
    }
    return baseobj;
  },

  makeColumnForBoolean: function(headerName, include, property, required, baseObj, defaultValue) {
    var column = baseObj || {};
    column.header = headerName;
    column.data = property;
    column.type = 'dropdown';
    column.trimDropdown = false;
    column.source = required ? ['True', 'False'] : ['Unknown', 'True', 'False'];
    column.include = include;
    column.unpack = function(obj, flat, setCellMeta) {
      var result;
      if (obj[property] === true) {
        result = 'True';
      } else if (obj[property] === false) {
        result = 'False';
      } else if (defaultValue !== undefined) {
        result = defaultValue;
      } else if (required) {
        result = 'False';
      } else {
        result = 'Unknown';
      }
      flat[property] = result;
    };
    column.pack = function(obj, flat, errorHandler) {
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
    };
    return column;
  },

  makeColumnForFloat: function(headerName, include, property, required) {
    return {
      'header': headerName,
      'data': property,
      'type': 'text',
      'include': include,
      'unpack': function(obj, flat, setCellMeta) {
        flat[property] = Utils.valOrNull(obj[property]);
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

  makeColumnForDecimal: function(headerName, include, property, precision, scale, required, allowNegative, baseObj) {
    var validation = HotUtils.validator.decimal(precision, scale, allowNegative, required);

    var column = baseObj || {};
    column.header = headerName;
    column.data = property;
    column.type = 'text';
    column.include = include;
    column.unpack = function(obj, flat, setCellMeta) {
      flat[property] = Utils.valOrNull(obj[property]);
    };
    column.validator = validation.validator;
    column.pack = function(obj, flat, errorHandler) {
      validation.validator(flat[property], function(valid) {
        if (!valid) {
          errorHandler(headerName + ' must be a number between ' + validation.min + ' and ' + validation.max);
        } else {
          obj[property] = Utils.valOrNull(flat[property]);
        }
      });
    };
    return column;
  },

  makeColumnForInt: function(headerName, include, property, validator, baseObj) {
    var column = baseObj || {};
    column.header = headerName;
    column.data = property;
    column.type = 'numeric';
    column.include = include;
    column.validator = validator;
    column.unpack = function(obj, flat, setCellMeta) {
      flat[property] = Utils.valOrNull(obj[property]);
    };
    column.pack = function(obj, flat, errorHandler) {
      if (!Utils.validation.isEmpty(flat[property])) {
        obj[property] = flat[property];
      } else {
        obj[property] = null;
      }
    };
    return column;
  },

  makeColumnForText: function(headerName, include, property, baseobj, defaultValue) {
    baseobj.header = headerName;
    baseobj.data = property;
    baseobj.type = 'text';
    baseobj.include = include;
    if (!baseobj.hasOwnProperty('unpack')) {
      baseobj.unpack = function(obj, flat, setCellMeta) {
        flat[property] = Utils.valOrNull(obj[property]);
        if (flat[property] == null) {
          flat[property] = Utils.valOrNull(defaultValue);
        }
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

  makeColumnForEnum: function(headerName, include, required, property, source, defaultValue, nullValue, baseObj) {
    var validator = Handsontable.validators.AutocompleteValidator;
    if (required) {
      if (nullValue) {
        validator = HotUtils.validator.requiredAutocompleteWithNullValue(nullValue);
      } else {
        validator = HotUtils.validator.requiredAutocomplete;
      }
    }
    var column = baseObj || {};
    column.header = headerName;
    column.data = property;
    column.type = 'dropdown';
    column.trimDropdown = false;
    column.source = nullValue ? [nullValue].concat(source) : source;
    column.include = include;
    column.validator = validator;
    column.unpack = function(obj, flat, setCellMeta) {
      flat[property] = obj[property] || defaultValue;
    };
    column.pack = function(obj, flat, errorHandler) {
      obj[property] = flat[property];
    };
    return column;
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

  spreadsheetAction: function(url, sheets, generateErrors, name) {
    return {
      name: name || 'Download',
      action: function(items) {
        Utils.showDialog('Download Spreadsheet', 'Download', [{
          property: 'sheet',
          type: 'select',
          label: 'Type',
          values: sheets,
          getLabel: function(x) {
            return x.description;
          }
        }, {
          property: 'format',
          type: 'select',
          label: 'Format',
          values: Constants.spreadsheetFormats,
          getLabel: function(x) {
            return x.description;
          }
        }], function(result) {
          var errors = generateErrors(items, result)
          if (errors.length >= 1) {
            Utils.showOkDialog("Error", errors);
          } else {
            Utils.ajaxDownloadWithDialog(url, {
              ids: items.map(Utils.array.getId),
              format: result.format.name,
              sheet: result.sheet.name
            });
          }
        });
      },
      allowOnLibraryPage: true
    };
  },

  makeParents: function(parentsByCategoryUrlFunction, parentCategories) {
    return HotUtils.makeRelations(parentsByCategoryUrlFunction, 'Parents', parentCategories, true);
  },

  makeChildren: function(childrenByCategoryUrlFunction, childCategories) {
    return HotUtils.makeRelations(childrenByCategoryUrlFunction, 'Children', childCategories, false);
  },

  makeRelations: function(categoryUrlFunction, relationship, relationCategories, useParentBound) {

    return {
      name: relationship,
      action: function(items) {
        function makeCategoriesFilter(items) {
          if (!Constants.isDetailedSample) {
            return function(category) {
              if (useParentBound) {
                return category.index >= Constants.sampleCategories.length;
              }
              return category.index > Constants.sampleCategories.length;
            };
          }
          var childBound = Constants.sampleCategories.length - 1;
          var parentBound = 0;
          for (sample in items) {
            if (items[sample].sampleClassId === undefined) {
              parentBound = Constants.sampleCategories.length - 1;
              continue;
            }
            var index = Constants.sampleCategories.indexOf(Constants.sampleClasses.find(function(sampleClass) {
              return sampleClass.id == items[sample].sampleClassId;
            }).sampleCategory);
            if (index > parentBound) {
              parentBound = index;
            }
            if (index < childBound) {
              childBound = index;
            }
          }
          if (useParentBound) {
            return function(category) {
              return category.index <= parentBound || category.index >= Constants.sampleCategories.length;
            };
          } else {
            return function(category) {
              return category.index >= childBound;
            }
          }
        }

        var actions = relationCategories.filter(makeCategoriesFilter(items)).map(function(category) {
          return {
            "name": category.name,
            "handler": function() {
              Utils.ajaxWithDialog('Searching', 'POST', categoryUrlFunction(category.name), items.map(function(s) {
                return s.id;
              }), function(relations) {
                var selectedActions = category.getBulkActions(category.config).filter(function(bulkAction) {
                  return !!bulkAction;
                }).map(function(bulkAction) {
                  return {
                    "name": bulkAction.name,
                    "handler": function() {
                      bulkAction.action(relations);
                    }
                  };
                });
                selectedActions.unshift({
                  "name": "View Selected",
                  "handler": function() {
                    Utils.showOkDialog(category.name + ' ' + relationship, relations.map(function(relation) {
                      return relation.name + (relation.alias ? ' (' + relation.alias + ')' : '');
                    }), showActionDialog);
                  }
                });
                var showActionDialog = function() {
                  Utils.showWizardDialog(category.name + ' Actions', selectedActions);
                };
                showActionDialog();
              });
            }
          };
        })
        if (actions.length == 1) {
          actions[0].handler();
        } else {
          Utils.showWizardDialog(relationship, actions);
        }
      }
    };
  },

  makeAddToWorkset: function(pluralLabel, idsField, getAddUrlForWorksetId) {
    return {
      name: 'Add to Workset',
      action: function(items) {
        var ids = items.map(Utils.array.getId);
        Utils.showWizardDialog('Add to Workset', [
            {
              name: 'Existing Workset',
              handler: function() {
                var doSearch = function() {
                  var fields = [{
                    label: 'Workset search',
                    property: 'query',
                    type: 'text',
                    required: true
                  }]
                  Utils.showDialog('Add to Existing Workset', 'Search', fields, function(input) {
                    Utils.ajaxWithDialog('Finding Worksets', 'GET', Urls.rest.worksets.query + '?' + jQuery.param({
                      q: input.query
                    }), null, function(worksets) {
                      var selectFields = [];
                      if (!worksets || !worksets.length) {
                        Utils.showOkDialog('Workset Search', ['No matching worksets found.'], doSearch);
                      } else {
                        worksets.forEach(function(workset) {
                          selectFields.push({
                            name: workset.alias,
                            handler: function() {
                              Utils.ajaxWithDialog('Adding to Workset', 'POST', getAddUrlForWorksetId(workset.id), ids, function() {
                                Utils.showOkDialog('Add to Workset', ['The selected ' + pluralLabel + ' have been added to workset \''
                                    + workset.alias + '\'.']);
                              });
                            }
                          });
                          Utils.showWizardDialog('Add to Existing Workset', selectFields);
                        });
                      }
                    });
                  });
                }
                doSearch();
              }
            }, {
              name: 'New Workset',
              handler: function() {
                var fields = [{
                  label: 'Alias',
                  property: 'alias',
                  type: 'text',
                  required: true
                }, {
                  label: 'Description',
                  property: 'description',
                  type: 'textarea',
                  rows: 3,
                  required: false
                }];
                Utils.showDialog('New Workset', 'Create', fields, function(input) {
                  var workset = {
                    alias: input.alias,
                    description: input.description
                  };
                  workset[idsField] = ids;
                  Utils.ajaxWithDialog('Creating Workset', 'POST', Urls.rest.worksets.create, workset, function() {
                    Utils.showOkDialog('Add to Workset', ['New workset \'' + workset.alias + '\' created.']);
                  });
                });
              }
            }]);
      }
    }
  },

  makeRemoveFromWorkset: function(pluralLabel, url) {
    return {
      name: 'Remove from Workset',
      action: function(items) {
        Utils.showConfirmDialog('Remove ' + pluralLabel, 'Remove', ['Remove these ' + items.length + ' ' + pluralLabel
            + ' from the workset?'], function() {
          var ids = items.map(Utils.array.getId);
          Utils.ajaxWithDialog('Removing ' + pluralLabel, 'DELETE', url, ids, function() {
            Utils.showOkDialog('Removed', [items.length + ' ' + pluralLabel + ' removed.'], function() {
              Utils.page.pageReload();
            });
          });
        });
      }
    }
  },

  makeMoveFromWorkset: function(pluralLabel, url) {
    return {
      name: "Move to Workset",
      action: function(items) {
        var fields = [{
          label: 'Workset search',
          property: 'query',
          type: 'text',
          required: true
        }];
        Utils.showDialog('Move to Workset', 'Search', fields, function(input) {
          Utils.ajaxWithDialog('Finding Worksets', 'GET', Urls.rest.worksets.query + '?' + jQuery.param({
            q: input.query
          }), null, function(worksets) {
            if (!worksets || !worksets.length) {
              Utils.showOkDialog('Workset Search', ['No matching worksets found.'], doSearch);
            } else {
              Utils.showWizardDialog('Move to Workset', worksets.map(function(workset) {
                return {
                  name: workset.alias,
                  handler: function() {
                    var requestData = {
                      targetWorksetId: workset.id,
                      itemIds: items.map(Utils.array.getId)
                    };
                    Utils.ajaxWithDialog('Moving items', 'POST', url, requestData, function() {
                      Utils.showOkDialog('Items moved', ['The selected ' + pluralLabel + ' have been moved to workset \'' + workset.alias
                          + '\'.'], Utils.page.pageReload);
                    });
                  }
                };
              }));
            }
          });
        });
      }
    }
  },

  makeAttachFile: function(entityType, getProjectId) {
    return {
      name: 'Attach Files',
      action: function(items) {
        var ids = items.map(Utils.array.getId).join(",");
        var projects = Utils.array.deduplicateNumeric(items.map(getProjectId));
        if (projects.length > 1) {
          ListTarget.attachment.showUploadDialog(entityType, 'shared', ids);
        } else {
          Utils.showWizardDialog('Attach Files', [{
            name: 'Upload New Files',
            handler: function() {
              ListTarget.attachment.showUploadDialog(entityType, 'shared', ids);
            }
          }, {
            name: 'Link Project File',
            handler: function() {
              ListTarget.attachment.showLinkDialog(entityType, 'shared', projects[0], ids);
            }
          }]);
        }
      }
    }
  },

  makeTransferAction: function(idsParameter) {
    return {
      name: 'Transfer',
      action: function(items) {
        var params = {};
        params[idsParameter] = items.map(Utils.array.getId).join(",");
        window.location = Urls.ui.transfers.create + '?' + jQuery.param(params);
      }
    };
  },

  relationCategoriesForDetailed: function() { // Change name to relationCategoriesForDetailed
    return Constants.isDetailedSample ? Constants.sampleCategories.map(function(category) {
      return {
        "name": category,
        "getBulkActions": BulkTarget.sample.getBulkActions,
        "config": {},
        "index": Constants.sampleCategories.indexOf(category)
      };
    }) : [{
      "name": "Sample",
      "getBulkActions": BulkTarget.sample.getBulkActions,
      "config": {},
      "index": Constants.sampleCategories.length
    }];
  },

  relations: {
    library: function() {
      return {
        "name": "Library",
        "getBulkActions": BulkTarget.library.getBulkActions,
        "config": {},
        "index": Constants.sampleCategories.length + 1
      };
    },
    libraryAliquot: function() {
      return {
        "name": "Library Aliquot",
        "getBulkActions": BulkTarget.libraryaliquot.getBulkActions,
        "config": {},
        "index": Constants.sampleCategories.length + 2
      };
    },
    pool: function() {
      return {
        "name": "Pool",
        "getBulkActions": HotTarget.pool.getBulkActions,
        "config": {},
        "index": Constants.sampleCategories.length + 3
      };
    }
  },

  warnIfConsentRevoked: function(items, callback, getLabel) {
    var consentRevoked = items.filter(function(item) {
      return item.identityConsentLevel === 'Revoked';
    })

    if (consentRevoked.length) {
      var lines = ['Donor has revoked consent for the following item' + (consentRevoked.length > 1 ? 's' : '') + '.'];
      jQuery.each(consentRevoked, function(index, item) {
        lines.push('* ' + (typeof getLabel === 'function' ? getLabel(item) : item.name + ' (' + item.alias + ')'));
      });
      Utils.showConfirmDialog('Warning', 'Proceed anyway', lines, callback);
    } else {
      callback();
    }
  },

  sortTable: function(table, sortProperty, sortFunction) {
    table.sortFunctions[sortProperty] = sortFunction;

    table.getPlugin('columnSorting').sort({
      column: table.propToCol(sortProperty),
      sortOrder: 'asc'
    });
  },

  getPlatformType: function(value) {
    return Utils.array.maybeGetProperty(Utils.array.findFirstOrNull(function(platformType) {
      return platformType.key == value;
    }, Constants.platformTypes), 'name');
  },

  updateFromTemplateOrDesign: function(design, template, idProperty, source, displayProperty, setReadOnly, setData) {
    var id = null;
    if (design) {
      id = design[idProperty];
    } else if (template) {
      id = template[idProperty];
    }
    if (id) {
      var change = Utils.array.findFirstOrNull(Utils.array.idPredicate(id), source);
      if (change) {
        setData(change[displayProperty]);
      }
    }
    setReadOnly(design || (template && template[idProperty]));
  },

  showDialogForBoxCreation: function(title, okButton, fields, pageURL, generateParams, getItemCount) {
    fields.push(ListUtils.createBoxField);
    Utils.showDialog(title, okButton, fields, function(result) {
      var params = generateParams(result);
      if (params == null) {
        return;
      }
      var loadPage = function() {
        window.location = window.location.origin + pageURL + '?' + jQuery.param(params);
      }
      if (result.createBox) {
        Utils.createBoxDialog(result, getItemCount, function(newBox) {
          params.boxId = newBox.id;
          loadPage();
        });
      } else {
        loadPage();
      }
    });
  }

};

HotTarget = {};
