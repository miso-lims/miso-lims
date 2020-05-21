BulkUtils = (function($) {

  /*
   * BulkTarget structure: {
   *   getSaveUrl: required function() returning url for bulk save. POST is used for create and PUT
   *       for edits
   *   getUserManualUrl: optional (recommended) function() returning url of specific user manual
   *       page to set on 'Help' link
   *   getCustomActions: optional function() returning array of Custom Actions (see below) to make
   *       available on the bulk edit page while editing
   *   getBulkActions: optional function(config) returning array of Bulk Actions (see below) to
   *       make available after successful save
   *   getFixedColumns: optional function(config) returning int. Number of columns to freeze.
   *       Default: 0
   *   getColumns: required function(config, limitedApi) returning array of columns (see below)
   *   prepareData: optional function(data); allows manipulating source data prior to table
   *       creation
   *   confirmSave: optional function(data) returning promise. Resolve promise to allow save, or
   *       fail to cancel
   * }
   * 
   * Custom Action structure: {
   *   name: required string; text to display on action button
   *   title: optional string; text to display on hover over button
   *   action: required function(api); function to call on click. See API object below
   * }
   * 
   * Bulk Action structure: {
   *   name: required string; text to display on action button
   *   title: optional string; text to display on hover over button
   *   action: required function(items); function to call on click
   * }
   * 
   * Column structure: {
   *   title: required string; column heading
   *   type: required string (text|read-only|int|decimal|date|dropdown); type of field
   *   data: required string; JSON property to use for value
   *   getDisplayValue: optional function(object) returning string; generate a value to display in
   *       a read-only field instead of the data value
   *   include: optional boolean (default: true); determines whether the column is displayed
   *   omit: optional boolean (default: false); determines whether field is saved. Field is saved
   *       by default. If true, the data property doesn't need to exist in the JSON, and won't be
   *       updated even if it does
   *   source: array of objects or function(data, limitedApi); required for dropdown fields;
   *       Provides dropdown options
   *   sortSource: optional function(a, b); sort function for dropdown options
   *   getItemLabel: function(item) returning string; get the label for a dropdown option. If
   *       omitted and the item is a string, it is used as the label; otherwise, an error is thrown
   *   getItemValue: function(item) returning string; get the value for a dropdown option. If
   *       omitted, the item is used as the value
   *   validationCache: optional string for dropdown columns; if set, any key found in the named
   *       cache will be marked valid
   *   required: optional boolean (default: false); whether the field is required
   *   maxLength: optional integer; maximum number of characters for text input
   *   regex: optional regex string; validation regex for text input
   *   initial: optional string; value to initialize field value to when missing. Affects new items
   *       only unless initializeOnEdit is set to true
   *   initializeOnEdit: optional boolean (default: false); if true, the initial value will also be
   *       used when editing existing items
   *   min: optional int/decimal; minimum value for int or decimal input
   *   max: optional int/decimal; maximum value for int or decimal input
   *   precision: optional int (default: 21); maximum precision (length, excluding the decimal) for
   *       decimal input
   *   scale: optional int (default: 17); maximum scale (decimal places) for decimal input
   *   onChange: function(rowIndex, newValue, api); action to take when the field is modified. See
   *       API object below
   *   sortable: optional boolean (default: true); whether the data can be sorted by this column
   *   customSorting: optional array of custom sorts (see below); sorting configuration. Default
   *       will sort alphabetically with empty values at the bottom
   * }
   * 
   * Custom Sort object: {
   *   name: string label for sort option
   *   sort: standard sort function
   * }
   * 
   * API object: {
   *   getCache: function(cacheName); retrieve a named cache. Available in limited API
   *   showError: function(message); display an error message above the table. Available in limited
   *       API
   *   getRowCount: function()
   *   getValue: function(rowIndex, dataProperty)
   *   updateField: function(rowIndex, dataProperty, options). options may include
   *       * 'value' (string)
   *       * 'source' (array of objects)
   *       * 'required' (boolean)
   *       * 'disabled' (boolean)
   *   updateData: function(changes); update fields in bulk. Use this rather than multiple
   *       updateField calls to improve performance. changes is an array of arrays where the inner
   *       arrays have three elements - rowIndex, dataProperty, and newValue
   * }
   * 
   */

  var DEFAULT_DECIMAL_PRECISION = 21;
  var DEFAULT_DECIMAL_SCALE = 17;

  CONTAINER_ID = 'hotContainer';
  SAVE = '#save';
  LOADER = '#ajaxLoader';
  SUCCESS_CONTAINER = '#successes';
  SUCCESS_MESSAGE = '#successMessage';
  ERRORS_CONTAINER = '#errorsContainer';
  ERRORS_BOX = '#errors';
  ACTION_BAR = '#bulkactions';

  var INTEGER_REGEXP = new RegExp('^-?\\d+$');

  var caches = {};
  var commentLocations = [];
  var disableTable = false;

  return {

    makeTable: function(target, config, data) {
      // No HTML IDs params required as this is only made to work with handsontables2.jsp
      Utils.showWorkingDialog('Bulk Table', function() {
        makeTable(target, config, data, false);
        showLoading(false, true);
      });
    }

  };

  function makeTable(target, config, data, skipActions) {
    var hotContainer = document.getElementById(CONTAINER_ID);

    if (target.prepareData) {
      target.prepareData(data);
    }

    var api = makeApi();

    var columns = target.getColumns(config, api).filter(function(column) {
      return !column.hasOwnProperty('include') || column.include;
    });

    var tableData = makeTableData(data, columns, config);
    var cellMetas = processDropdownSources(columns, data, tableData, api);

    // Note: can never call udpateSettings else column header display bugs happen
    var hot = new Handsontable(hotContainer, {
      licenseKey: 'non-commercial-and-evaluation',
      fixedColumnsLeft: target.hasOwnProperty('getFixedColumns') ? target.getFixedColumns(config) : 1,
      manualColumnResize: true,
      rowHeaders: true,
      colHeaders: columns.map(function(column) {
        return column.title;
      }),
      viewportColumnRenderingOffset: 700,
      preventOverflow: 'horizontal',
      contextMenu: false,
      columns: columns.map(makeHotColumn),
      cell: cellMetas,
      data: tableData,
      maxRows: data.length,
      renderAllRows: true,
      comments: true,
      cells: function(row, col, prop) {
        // Note: this is to permanently disable the table. To undo, we'd have to track which cells should
        // remain read-only as this function overrides future changes too
        var cellProperties = {};
        if (disableTable) {
          cellProperties.readOnly = true;
        }
        return cellProperties;
      }
    });

    hot.addHook('beforeAutofill', function(start, end, rows) {
      incrementAutofill(hot, start, end, rows);
    });

    extendApi(api, hot, columns);
    setupOnChangeListeners(hot, columns, api);
    if (!skipActions) {
      setupActions(hot, target, columns, api, config, data);
    }
    setupSave(hot, target, columns, config, data);
  }

  function makeHotColumn(column) {
    var base = {
      data: column.data,
      allowEmpty: !column.required
    };

    switch (column.type) {
    case 'read-only':
      return makeReadOnlyColumn(column, base);
    case 'text':
      return makeTextColumn(column, base);
    case 'int':
      return makeIntColumn(column, base);
    case 'decimal':
      return makeDecimalColumn(column, base);
    case 'date':
      return makeDateColumn(column, base);
    case 'dropdown':
      return makeDropdownColumn(column, base);
    default:
      throw new Error('Unknown field type: ' + column.type);
    }
  }

  function makeReadOnlyColumn(column, base) {
    base.type = 'text';
    base.readOnly = true;
    return base;
  }

  function makeTextColumn(column, base) {
    base.type = 'text';
    base.validator = textValidator(column);
    return base;
  }

  function makeIntColumn(column, base) {
    base.type = 'numeric';
    base.validator = intValidator(column);
    return base;
  }

  function makeDecimalColumn(column, base) {
    base.type = 'text';
    base.validator = decimalValidator(column);
    return base;
  }

  function makeDateColumn(column, base) {
    base.type = 'date';
    base.dateFormat = 'YYYY-MM-DD';
    base.datePickerConfig = {
      firstDay: 0,
      numberOfMonths: 1
    };
    return base;
  }

  function makeDropdownColumn(column, base) {
    var source = [];
    // if source is a function, it will be initialized per row later
    if (Array.isArray(column.source)) {
      source = getDropdownOptionLabels(column.source, column.getItemLabel, column.sortSource);
    }

    base.type = 'dropdown';
    base.source = source;
    base.trimDropdown = false;
    if (column.validationCache) {
      base.validator = acceptCachedValidator('boxes');
    }
    return base;
  }

  function getDropdownOptionLabels(source, getItemLabel, sortSource) {
    var sorted = sortSource ? source.sort(sortSource) : source;
    // map or copy the array to avoid modifying the original
    var labels = getItemLabel ? source.map(getItemLabel) : source.slice();
    return labels;
  }

  function getSourceLabelForValue(source, value, column) {
    var item = value;
    if (column.getItemValue) {
      item = source.find(function(sourceItem) {
        return column.getItemValue(sourceItem) === value;
      });
    }
    if (item === undefined || item === null) {
      return null;
    }
    return column.getItemLabel ? column.getItemLabel(item) : item;
  }

  function getSourceValueForLabel(source, label, column) {
    if (label === null || label === '') {
      return null;
    }
    var item = source.find(function(sourceItem) {
      if (column.getItemLabel) {
        return column.getItemLabel(sourceItem) === label;
      } else {
        return sourceItem === label;
      }
    });
    if (item === undefined) {
      throw new Error('No matching item found in source');
    }
    return column.getItemValue ? column.getItemValue(item) : item;
  }

  function makeApi() {
    return {
      getCache: function(cacheName) {
        if (!caches[cacheName]) {
          caches[cacheName] = {};
        }
        return caches[cacheName];
      },

      showError: function(message) {
        showError(message);
      }
    };
  }

  function makeTableData(data, columns, config) {
    var tableData = [];
    for (var i = 0; i < data.length; i++) {
      var rowData = {};
      columns.forEach(function(column) {
        if (!column.data) {
          throw new Error('Missing data property for column definition: ' + column.title);
        }
        if (column.type === 'read-only' && column.getDisplayValue) {
          rowData[column.data] = column.getDisplayValue(data[i]);
          return;
        }
        var defaultValue = null;
        if (column.hasOwnProperty('initial') && (column.initializeOnEdit || config.pageMode !== 'edit')) {
          defaultValue = column.initial;
        }
        if (data[i].hasOwnProperty(column.data) && data[i][column.data] !== null) {
          if (column.type === 'dropdown') {
            if (Array.isArray(column.source)) {
              rowData[column.data] = getSourceLabelForValue(column.source, data[i][column.data], column);
            } else {
              // if dropdown source is a function value gets set later in processDropdownSources
              rowData[column.data] = null;
            }
          } else {
            rowData[column.data] = data[i][column.data];
          }
        } else {
          if (column.type === 'dropdown') {
            if (Array.isArray(column.source)) {
              if (defaultValue !== null) {
                rowData[column.data] = getSourceLabelForValue(column.source, defaultValue, column);
              } else {
                rowData[column.data] = null;
              }
            }
          } else {
            rowData[column.data] = defaultValue;
          }
        }
      });
      tableData.push(rowData);
    }
    return tableData;
  }

  function processDropdownSources(columns, data, tableData, api) {
    var cellMetas = [];
    columns.forEach(function(column, colIndex) {
      if (column.type === 'dropdown' && typeof column.source === 'function') {
        for (var rowIndex = 0; rowIndex < data.length; rowIndex++) {
          var source = column.source(data[rowIndex], api);
          var labels = getDropdownOptionLabels(source, column.getItemLabel, column.sortSource);
          cellMetas.push({
            row: rowIndex,
            col: colIndex,
            source: labels,
            sourceData: source
          });
          tableData[rowIndex][column.data] = getSourceLabelForValue(source, data[rowIndex][column.data], column);
        }
      }
    });
    return cellMetas;
  }

  function incrementAutofill(hot, start, end, rows) {
    // only operate on fill from 2 selected rows
    if (rows.length !== 2) {
      return;
    }
    // prepare data
    var incrementSetup = getIncrementSetup(rows);
    if ($.isEmptyObject(incrementSetup)) {
      return;
    }

    var newData = [];
    // add changes to newData
    var rowCount = end['row'] - start['row'] + 1;
    var columnCount = end['col'] - start['col'] + 1
    for (var col_i = 0; col_i < columnCount; col_i++) {
      var currentCol = start['col'] + col_i;
      var setup = incrementSetup[col_i];
      if (!setup) {
        continue;
      }
      var placeholders = setup.pattern.match(/{\d+}/g);
      for (var row_i = 0; row_i < rowCount; row_i++) {
        var template = setup.pattern;
        for (var placeholder_i = 0; placeholder_i < placeholders.length; placeholder_i++) {
          var incremented = increment(setup.secondNumbers[placeholder_i], row_i + 1);
          template = template.replace(placeholders[placeholder_i], incremented);
        }
        newData.push([start['row'] + row_i, start['col'] + col_i, template]);
      }
    }

    setTimeout(function() {
      hot.setDataAtCell(newData);
    }, 200);
  }

  function getIncrementSetup(rows) {
    var columnCount = rows[0].length;
    for (var cols_i = 0; cols_i < columnCount; cols_i++) {
      var secondNumbers = [];
      var value1 = rows[0][cols_i];
      var value2 = rows[1][cols_i];
      var numbersInCell1 = value1.match(/\d+/g);
      var numbersInCell2 = value2.match(/\d+/g);
      if (!numbersInCell1 || !numbersInCell2 || numbersInCell1.length !== numbersInCell2.length) {
        continue;
      }
      var index = 0;
      var pattern = '';
      var doIncrement = false;
      var incrementConfig = {};
      var numIncrements = 0;
      for (var num_i = 0; num_i < numbersInCell1.length; num_i++) {
        var numIndex = value1.indexOf(numbersInCell1[num_i], index);
        var numEndIndex = numIndex + numbersInCell1[num_i].length;
        var nextNumber = increment(numbersInCell1[num_i], 1);
        if (value1.substring(index, numEndIndex) === value2.substring(index, numEndIndex)) {
          // number not incremented. treat as static
          pattern += value1.substring(index, numEndIndex)
        } else if (value2.substring(index, numEndIndex) === value1.substring(index, numIndex) + nextNumber) {
          // number incremented
          pattern += value1.substring(index, numIndex) + '{' + numIncrements + '}';
          numIncrements++;
          secondNumbers.push(nextNumber);
          doIncrement = true;
        } else {
          // strings don't match. abort incrementing
          doIncrement = false;
          break;
        }
        index = numEndIndex;
      }
      if (value1.substring(index) !== value2.substring(index)) {
        doIncrement = false;
      } else {
        pattern += value1.substring(index);
      }
      if (doIncrement) {
        incrementConfig[cols_i] = {
          pattern: pattern,
          secondNumbers: secondNumbers
        };
      }
    }
    return incrementConfig;
  }

  function increment(number, increment) {
    var incrementedInt = parseInt(number) + increment;
    var incrementedString = incrementedInt.toString();
    if (number.startsWith('0')) {
      incrementedString = incrementedString.padStart(number.length, '0');
    }
    return incrementedString;
  }

  function extendApi(api, hot, columns) {
    api.showError = function(message) {
      showError(message, hot);
    };

    api.getRowCount = function() {
      return hot.countRows();
    }

    api.getValue = function(row, dataProperty) {
      return hot.getDataAtRowProp(row, dataProperty);
    }

    api.updateField = function(rowIndex, dataProperty, options) {
      var column = columns.find(function(col) {
        return col.data === dataProperty;
      });
      if (!column) {
        throw new Error('No column found for data property: ' + dataProperty);
      }
      var colIndex = hot.propToCol(dataProperty);
      var cellMeta = hot.getCellMeta(rowIndex, colIndex);

      var forceValidate = false;
      Object.keys(options).forEach(function(option) {
        switch (option) {
        case 'value':
          // handled after everything else so validation is only triggered once
          break;
        case 'source':
          if (column.type !== 'dropdown') {
            throw new Error('Can\'t update source of non-dropdown column: ' + dataProperty);
          }
          var labels = getDropdownOptionLabels(options.source, column.getItemLabel, column.sortSource);
          hot.setCellMeta(rowIndex, colIndex, 'source', labels);
          hot.setCellMeta(rowIndex, colIndex, 'sourceData', options.source);
          forceValidate = true;
          break;
        case 'required':
          switch (column.type) {
          case 'text':
          case 'dropdown':
            hot.setCellMeta(rowIndex, colIndex, 'allowEmpty', !options.required);
            break;
          default:
            throw new Error('Can\'t change required property for column type: ' + column.type);
          }
          forceValidate = true;
          break;
        case 'disabled':
          hot.setCellMeta(rowIndex, colIndex, 'readOnly', options.disabled);
          break;
        default:
          throw new Error('Invalid field update option: ' + option);
        }
      });
      if (options.hasOwnProperty('value')) {
        hot.setDataAtCell(rowIndex, colIndex, options.value);
      } else if (forceValidate) {
        // Note: intended to be a private function, but it works and is more efficient than validating the entire row/column/table
        hot._validateCells(null, [rowIndex], [colIndex]);
      }
    };

    api.updateData = function(changes) {
      // changes = [[row, prop, value]...]
      hot.setDataAtRowProp(changes);
    }
  }

  function setupOnChangeListeners(hot, columns, api) {
    var rowCount = hot.countRows();
    var listeners = {};
    // intercept setDataAtCell to apply entire column of changes at once for performance
    var originalSetData = hot.setDataAtCell;
    var dataChanges = [];
    hot.setDataAtCell = function(rowIndex, colIndex, value) {
      dataChanges.push([rowIndex, colIndex, value]);
    }
    columns.forEach(function(column) {
      if (column.onChange) {
        listeners[column.data] = column.onChange;
        for (var rowIndex = 0; rowIndex < rowCount; rowIndex++) {
          column.onChange(rowIndex, hot.getDataAtRowProp(rowIndex, column.data), api);
        }
        if (dataChanges.length) {
          originalSetData(dataChanges);
          dataChanges = [];
        }
      }
    });
    hot.setDataAtCell = originalSetData;

    hot.addHook('afterChange', function(changes, source) {
      // changes = [[row, prop, oldVal, newVal], ...]
      changes.forEach(function(change) {
        if (listeners[change[1]]) {
          listeners[change[1]](change[0], change[3], api);
        }
      });
    });
  }

  function setupActions(hot, target, columns, api, config, data) {
    var actions = target.getCustomActions ? target.getCustomActions() : [];
    actions.push(makeSortAction(hot, target, columns, config, data), makeImportAction(hot), makeExportAction(hot));
    $(ACTION_BAR).empty().append(
        actions.map(function(customAction) {
          return $('<a>').text(customAction.name).prop('href', '#').addClass('ui-button ui-state-default').prop('title',
              customAction.title || '').click(function() {
            customAction.action(api);
          });
        }));
  }

  function makeSortAction(hot, target, columns, config, data) {
    // Note: columnSorting plugin is not used because enabling it causes header display bug
    return {
      name: "Sort",
      action: function() {
        var sortOptions = [];
        var sortEmptyLast = function(sortNonEmpty) {
          return function(value, nextValue) {
            if (value) {
              return nextValue ? sortNonEmpty(value, nextValue) : -1;
            } else if (nextValue) {
              return 1;
            } else {
              return 0;
            }
          };
        };
        var defaultSort = sortEmptyLast(function(value, nextValue) {
          return value.localeCompare(nextValue);
        });
        columns.filter(function(column) {
          return !column.hasOwnProperty('sortable') || column.sortable;
        }).forEach(function(column) {
          if (column.customSorting) {
            sortOptions = sortOptions.concat(column.customSorting.map(function(customSort) {
              return {
                name: customSort.name,
                data: column.data,
                sort: sortEmptyLast(customSort.sort)
              };
            }));
          } else {
            sortOptions.push({
              name: column.title,
              data: column.data,
              sort: defaultSort
            });
          }
        });
        var makeOptionField = function(prop, label, required) {
          return {
            property: prop,
            label: label,
            type: 'select',
            values: required ? sortOptions : [{
              name: '(None)',
              sort: null
            }].concat(sortOptions),
            getLabel: function(item) {
              return item.name;
            }
          };
        };
        var makeOrderField = function(prop) {
          return {
            property: prop,
            label: 'Order',
            type: 'select',
            values: ['Ascending', 'Descending']
          };
        };
        Utils.showDialog('Sort Table', 'Sort', [makeOptionField('option1', 'Primary Sort', true), makeOrderField('order1'),
            makeOptionField('option2', 'Secondary Sort', false), makeOrderField('order2'),
            makeOptionField('option3', 'Tertiary Sort', false), makeOrderField('order3')], function(results) {
          var sorted = data.map(function(dataRow, index) {
            return {
              data: dataRow,
              index: index
            };
          }).sort(
              function(a, b) {
                var sortByOption = function(sortOption, order) {
                  if (!sortOption.sort) {
                    return 0;
                  }
                  var sortValueA = hot.getDataAtRowProp(a.index, sortOption.data);
                  var sortValueB = hot.getDataAtRowProp(b.index, sortOption.data);
                  var val = sortOption.sort(sortValueA, sortValueB);
                  if (order === 'Descending') {
                    val = val * -1;
                  }
                  return val;
                };
                return sortByOption(results.option1, results.order1) || sortByOption(results.option2, results.order2)
                    || sortByOption(results.option3, results.order3);
              }).map(function(sortItem) {
            return sortItem.data;
          });
          Utils.showWorkingDialog('Sorting', function() {
            rebuildTable(hot, target, config, sorted);
          });
        });
      }
    };
  }

  function makeImportAction(hot) {
    return {
      name: "Import",
      action: function() {
        var mainDialog = function() {
          var dialogArea = $('#dialog');
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
                  if (!$('#fileInput').val()) {
                    Utils.showOkDialog('Error', ['No file selected!'], mainDialog);
                    return;
                  }
                  var formData = new FormData($('#uploadForm')[0]);
                  dialog.dialog("close");
                  dialogArea.empty();
                  dialogArea.append($('<p>Uploading...</p>'));

                  dialog = $('#dialog').dialog({
                    autoOpen: true,
                    height: 400,
                    width: 350,
                    title: 'Uploading File',
                    modal: true,
                    buttons: {},
                    closeOnEscape: false,
                    open: function(event, ui) {
                      $(this).parent().children().children('.ui-dialog-titlebar-close').hide();
                    }
                  });

                  jQuery.ajax({
                    url: Urls.rest.hot.bulkImport,
                    type: 'POST',
                    data: formData,
                    cache: false,
                    contentType: false,
                    processData: false
                  }).success(
                      function(columnData) {
                        dialog.dialog("close");
                        Utils.showWorkingDialog('Import', function() {
                          var hotHeaders = hot.getColHeader();
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
                          if (maxRowLength > hot.countRows()) {
                            Utils.showOkDialog('Error', ['The spreadsheet contains ' + maxRowLength + ' rows, but the table only contains '
                                + hot.countRows()]);
                            return;
                          }
                          // validate read-only cells
                          for (var colI = 0; colI < columnData.length; colI++) {
                            var columnIndex = hotHeaders.indexOf(columnData[colI].heading);
                            for (var rowIndex = 0; rowIndex < columnData[colI].data.length; rowIndex++) {
                              var cellValue = hot.getDataAtCell(rowIndex, columnIndex);
                              var importValue = columnData[colI].data[rowIndex];
                              if (hot.getCellMeta(rowIndex, columnIndex).readOnly
                                  && (!!importValue != !!cellValue || (!!importValue && !!cellValue && importValue != cellValue))) {
                                Utils.showOkDialog('Error',
                                    ['Values in read-only column \'' + columnData[colI].heading + '\' do not match']);
                                return;
                              }
                            }
                          }
                          // set values from spreadsheet
                          var changes = [];
                          columnData.forEach(function(column) {
                            var columnIndex = hotHeaders.indexOf(column.heading);
                            for (var rowIndex = 0; rowIndex < column.data.length; rowIndex++) {
                              // hot.setDataAtCell(rowIndex, columnIndex, column.data[rowIndex], 'CopyPaste.paste');
                              changes.push([rowIndex, columnIndex, column.data[rowIndex]]);
                            }
                          });
                          hot.setDataAtCell(changes, 'CopyPaste.paste');
                        });
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
    };
  }

  function makeExportAction(hot) {
    return {
      name: "Export",
      action: function() {
        var data = {
          headers: hot.getColHeader(),
          rows: []
        };

        for (var row = 0; row < hot.countRows(); row++) {
          data.rows.push(hot.getDataAtRow(row));
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
          Utils.ajaxDownloadWithDialog(Urls.rest.hot.bulkExport + '?' + $.param({
            format: result.format.name
          }), data);
        });
      }
    };
  }

  function setupSave(hot, target, columns, config, data) {
    $(SAVE).click(
        function() {
          showLoading(true, false);
          clearMessages(hot);
          hot.validateCells(function(valid) {
            if (!valid) {
              showError('Please fix highlighted cells. See the Quick Help section '
                  + '(above) for additional information regarding specific fields.');
              showLoading(false, true);
              return;
            }

            updateSourceData(data, hot, columns);

            $.when(target.confirmSave ? target.confirmSave(data) : null).then(function() {
              var method = config.pageMode === 'edit' ? 'PUT' : 'POST';
              Utils.ajaxWithDialog('Saving', method, target.getSaveUrl(), data, function(savedData) {
                Utils.showWorkingDialog('Saving', function() {
                  disableTable = true;
                  rebuildTable(hot, target, config, savedData, true);
                  showSuccess('Saved ' + savedData.length + ' items');
                  showLoading(false, false);
                  showBulkActions(target, config, savedData);
                });
              }, function(response, textStatus, errorThrown) {
                showSaveError(response, hot, columns);
                showLoading(false, true);
              }, true);
            }).fail(function() {
              showError('Save cancelled');
              showLoading(false, true);
            });
          });
        });
  }

  function showLoading(loading, allowSave) {
    Utils.ui.setDisabled(SAVE, loading || (allowSave === false));
    if (loading) {
      $(LOADER).removeClass('hidden');
    } else {
      $(LOADER).addClass('hidden');
    }
  }

  function updateSourceData(data, hot, columns) {
    var tableData = hot.getData();
    for (var rowIndex = 0; rowIndex < tableData.length; rowIndex++) {
      for (var colIndex = 0; colIndex < columns.length; colIndex++) {
        var column = columns[colIndex];
        if (column.omit) {
          continue;
        }
        if (tableData[rowIndex][colIndex] === null || tableData[rowIndex][colIndex] === '') {
          data[rowIndex][column.data] = null;
          continue;
        }
        if (column.type === 'dropdown') {
          if (column.validationCache) {
            data[rowIndex][column.data] = caches[column.validationCache][tableData[rowIndex][colIndex]];
          } else {
            var source = hot.getCellMeta(rowIndex, colIndex).sourceData;
            if ((!source || !source.length) && Array.isArray(column.source)) {
              source = column.source;
            }
            data[rowIndex][column.data] = getSourceValueForLabel(source, tableData[rowIndex][colIndex], column);
          }
        } else {
          data[rowIndex][column.data] = tableData[rowIndex][colIndex];
        }
      }
    }
  }

  function rebuildTable(hot, target, config, data, skipActions) {
    clearMessages(hot);
    $(ACTION_BAR).empty();
    $(SAVE).off('click');
    hot.destroy();
    makeTable(target, config, data, skipActions);
  }

  function showBulkActions(target, config, data) {
    var bulkActionBar = $(ACTION_BAR);
    bulkActionBar.empty();
    if (target.getBulkActions) {
      bulkActionBar.append(target.getBulkActions(config).map(
          function(bulkAction) {
            return $('<a>').text(bulkAction.name).prop('href', '#').addClass('ui-button ui-state-default').prop('title',
                bulkAction.title || '').click(function() {
              bulkAction.action(data);
            });
          }));
    }
  }

  function showSuccess(message, hot) {
    clearMessages(hot);
    $(SUCCESS_MESSAGE).text(message);
    $(SUCCESS_CONTAINER).removeClass('hidden');
  }

  function showSaveError(response, hot, columns) {
    var responseData = null;
    if (response && response.responseText) {
      responseData = JSON.parse(response.responseText);
    }
    if (!responseData || !responseData.detail) {
      showError('Something went terribly wrong. Please file a ticket with a screenshot or copy-paste of the data that you were trying to save.');
    } else if (responseData.dataFormat === 'bulk validation') {
      showValidationErrors(responseData.detail, responseData.data, hot, columns);
    } else {
      showError(responseData.detail);
    }
  }

  function showError(message, hot) {
    clearMessages(hot);
    $(ERRORS_BOX).append($('<p>').text(message));
    $(ERRORS_CONTAINER).removeClass('hidden');
  }

  function showValidationErrors(message, errors, hot, columns) {
    clearMessages(hot);
    var comments = hot.getPlugin('comments');
    $(ERRORS_BOX).append($('<p>').text(message));
    var list = $('<ul>');
    errors.forEach(function(error) {
      error.fields.forEach(function(field) {
        list.append($('<li>').text('Row ' + (error.row + 1)));
        var sublist = $('<ul>');
        var colIndex = -1;
        if (field.field !== 'GENERAL') {
          var colIndex = columns.findIndex(function(column) {
            return column.data === field.field;
          });
        }
        if (colIndex !== -1) {
          comments.setCommentAtCell(error.row, colIndex, field.errors.join('\n'));
          commentLocations.push({
            row: error.row,
            col: colIndex
          });
          field.errors.forEach(function(error) {
            sublist.append($('<li>').text(columns[colIndex].title + ': ' + error));
          });
        } else {
          field.errors.forEach(function(error) {
            sublist.append($('<li>').text((field.field === 'GENERAL' ? '' : field.field + ': ') + error));
          });
        }
        list.append(sublist);
      });
    });
    $(ERRORS_BOX).append(list)
    $(ERRORS_CONTAINER).removeClass('hidden');
  }

  function clearMessages(hot) {
    if (hot) {
      var comments = hot.getPlugin('comments');
      commentLocations.forEach(function(location) {
        comments.removeCommentAtCell(location.row, location.col);
      });
      commentLocations = [];
    }
    $(SUCCESS_CONTAINER).addClass('hidden');
    $(ERRORS_CONTAINER).addClass('hidden');
    $(SUCCESS_MESSAGE).empty();
    $(ERRORS_BOX).empty();
  }

  function textValidator(column) {
    var sanitize = new RegExp(Utils.validation.sanitizeRegex);
    var regex = column.regex ? new RegExp(column.regex) : null;
    return function(value, callback) {
      if (Utils.validation.isEmpty(value)) {
        callback(this.instance.getCellMeta(this.row, this.col).allowEmpty);
      } else if (column.maxLength && value.length > column.maxLength) {
        callback(false);
      } else if (!sanitize.test(value)) {
        callback(false);
      } else if (regex && !regex.test(value)) {
        callback(false);
      } else {
        callback(true);
      }
    };
  }

  function decimalValidator(column) {
    var precision = column.precision || DEFAULT_DECIMAL_PRECISION;
    var scale = column.scale || DEFAULT_DECIMAL_SCALE;
    var max = Math.pow(10, precision - scale) - Math.pow(0.1, scale);
    var min = column.hasOwnProperty('min') ? column.min : max * -1;
    max = column.hasOwnProperty('max') ? column.max : max;
    var pattern = '^\\d{0,' + (precision - scale) + '}';
    if (scale > 0) {
      pattern += '(?:\\.\\d{1,' + scale + '})?';
    }
    pattern += '$';
    var regex = new RegExp(pattern);
    return function(value, callback) {
      if (Utils.validation.isEmpty(value)) {
        callback(this.instance.getCellMeta(this.row, this.col).allowEmpty);
      } else {
        callback(Handsontable.helper.isNumeric(value) && regex.test(value) && value >= min && value <= max);
      }
    };
  }

  function intValidator(column) {
    return function(value, callback) {
      if (Utils.validation.isEmpty(value)) {
        callback(this.instance.getCellMeta(this.row, this.col).allowEmpty);
      } else if (!INTEGER_REGEXP.test(value)) {
        callback(false);
      } else if (column.hasOwnProperty('min') && value < column.min) {
        callback(false);
      } else if (column.hasOwnProperty('max') && value > column.max) {
        callback(false);
      } else {
        callback(true);
      }
    };
  }

  function acceptCachedValidator(cacheName) {
    return function(value, callback) {
      if (value && caches[cacheName]) {
        callback(!!caches[cacheName][value]);
      } else {
        Handsontable.validators.AutocompleteValidator.call(this, value, callback)
      }
    };
  }

})(jQuery);
