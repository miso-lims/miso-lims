/**
* Module for Handsontsble code which is shared between multiple instances
*/
var Hot = {
  detailedSample: null,
  colConf: null,
  sampleOptions: null,
  hotTable: null,
  startData: [],
  dropdownRef: null,
  messages: {
    success: [],
    failed: []
  },
  failedComplexValidation: [],
  saveButton: null,
  buildDtoFunc: null,
  saveOneFunc: null,
  updateOneFunc: null,
  propagateOrEdit: null,

  /**
   * Gets data needed to fill in dropdowns for various sample/library attributes. Callback is generally a "make table"-type function call.
   */
  fetchSampleOptions: function (callback) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (xhr.status === 200) {
          Hot.sampleOptions = JSON.parse(xhr.responseText);
          callback();
        } else {
          console.log(xhr.response);
        }
      }
    };
    xhr.open('GET', '/miso/rest/ui/sampleoptions');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
  },

  /**
   * Custom renderer to visually highlight a non-standard alias
   */
  nsAliasRenderer: function (instance, td, row, col, prop, value, cellProperties) {
    td.setAttribute('style', 'background-color: #ffffcc');
    td.innerHTML = value;
    return td;
  },

  /**
   * Custom renderer to visually highlight a read-only non-standard alias (generally for non-standard parent aliases)
   */
  nsAliasReadOnlyRenderer: function (instance, td, row, col, prop, value, cellProperties) {
    td.setAttribute('style', 'background-color: #ffffcc');
    td.classList.add('htDimmed');
    td.innerHTML = value;
    return td;
  },
  
  /**
   * Custom validator for fields that must contain data
   */
  requiredText: function (value, callback) {
    return callback(value != undefined && value != null && value != '' && value.length > 0);
  },

  /**
   * Custom validator for fields that may remain empty
   */
  permitEmpty: function (value, callback) {
    return callback(value === undefined || value === null || value === '' || value.length > 0);
  },
  
  /**
   * Custom validator for required numeric fields
   */
  requiredNumber: function (value, callback) {
    return callback(value != undefined && value != null && value != '' && Handsontable.helper.isNumeric(value));
  },

  /**
   * Gets item's alias
   */
  getAlias: function (obj) {
    if (obj.alias) return obj.alias;
  },

  /**
   * Gets values for a given key
   */
  getValues: function (key, objArr) {
    return objArr.map(function (obj) { return obj[key]; });
  },

  /**
   * Sorts by a given property
   */
  sortByProperty: function (array, propertyName) {
    return array.sort(function (a, b) {
      return a[propertyName] > b[propertyName] ? 1 : ((b[propertyName] > a[propertyName]) ? -1 : 0);
    });
  },

  /**
   * Gets the object from a given id and collection
   */
  getObjById: function (id, referenceCollection) {
    var results = referenceCollection.filter(function (item) {
      return item.id == id;
    });
    return results.length > 0 ? results[0] : null;
  },
  /**
   * Gets the id of an object from a given alias and collection
   */
  getIdFromAlias: function (alias, referenceCollection) {
    var results = referenceCollection.filter(function (item) {
      return item.alias == alias;
    });
    return results.length > 0 ? results[0].id : null;
  },

  /**
   * Gets the alias of an object from a given id and collection
   */
  getAliasFromId: function (id, referenceCollection) {
    var results = referenceCollection.filter(function (item) {
      return item.id == id;
    });
    return results.length > 0 ? results[0].alias : null;
  },

  /**
   * Gets array of qc values
   */
  getQcValues: function () {
    return ['true', 'false', 'unknown'];
  },
  
  /**
   * Gets the column index for a given attribute
   */
  getColIndex: function (attrName) {
    for (var i = 0; i < Hot.colConf.length; i++) {
      if (Hot.colConf[i].data == attrName) return i;
    }
  },
  
  /**
   * Concatenates and reduces arrays
   */
  concatArrays: function () {
    // call this function with any number of col groups and it will concat and reduce them all
    var cols = [];
    for (var i = 0; i<arguments.length; i++) {
      cols.push.apply(cols, arguments[i]);
    }
    return cols.reduce(function (a, b) { return a.concat(b); }, []);
  },

  /**
   * Serial execution of an array of functions.
   * @param {Function[]} aof - each function takes a single callback function parameter
   */
  serial: function (aof) {
    var invokeNext = function(index) {
      if(index < (aof.length)) {
        aof[index](function() { invokeNext(index + 1);} );
      }
    };
    invokeNext(0)
  },
  
  /**
   * Builds an array of objects or save functions for objects.
   * Note: Hot.buildDtoFunc and Hot.saveOneFunc must be assigned before this is called.
   */
  getArrayOfNewObjects: function (data) {
    // Returns a save function for a single line in the table.
    function saveFunction(data, index, numberToSave) {
      // The callback is called once the http request in saveOne completes.
      return function(callback) {
        Hot.saveOneFunc(data, index, numberToSave, callback);
      };
    }
    var len = data.length;
    var arrayOfObjects = [];
    
    // return an array of objects or saveFunctions for objects
    for (var i = 0; i < len; i++) {
      if (data[i].saved) continue;
      
      var newObj = Hot.buildDtoFunc(data[i]);
      if (Hot.detailedSample) {
        arrayOfObjects.push(saveFunction(JSON.stringify(newObj), i, len));
      } else {
        arrayOfObjects.push(newObj);
      }
    }
    return arrayOfObjects;
  },
  
  /**
   * Builds an array of objects or save functions for objects.
   * Note: Hot.buildDtoFunc and Hot.updateOneFunc must be assigned before this is called.
   */
  getArrayOfUpdatedObjects: function (data) {
    // Returns a save function for a single line in the table.
    function saveFunction(data, id, rowIndex, numberToSave) {
      // The callback is called once the http request in saveOne completes.
      return function(callback) {
        Hot.updateOneFunc(data, id, rowIndex, numberToSave, callback);
      };
    }
    var len = data.length;
    var arrayOfObjects = [];

    // return an array of objects or saveFunctions for objects
    for (var i = 0; i < len; i++) {
      if (data[i].saved) continue;

      var newObj = Hot.buildDtoFunc(data[i]);

      // all updated objects go through the REST WS
      arrayOfObjects.push(saveFunction(JSON.stringify(newObj), newObj.id, i, len));
    }
    return arrayOfObjects;
  },

  /**
   * Toggles loader gif and disabled styling and attribute of save button
   */
  toggleButtonAndLoaderImage: function (button) {
    var ajaxLoader;
    if (button.className.indexOf('disabled') == -1) {
      button.classList.add('disabled');
      button.setAttribute('disabled', 'disabled');
      ajaxLoader = "<img id='ajaxLoader' src='/../styles/images/ajax-loader.gif' class='fg-button'/>";
      button.insertAdjacentHTML('afterend', ajaxLoader);
    } else {
      button.classList.remove('disabled');
      button.removeAttribute('disabled');
      ajaxLoader = document.getElementById('ajaxLoader');
      if (ajaxLoader) ajaxLoader.parentNode.removeChild(ajaxLoader);
    }
  },

  /**
   * Actions undertaken once "Save" button is clicked. Clears out empty bottom rows and disables save button.
   */
  cleanRowsAndToggleSaveButton: function () {
    // reset error and success messages
    Hot.messages.failed = [];
    Hot.messages.success = [];

    // disable the save button
    if (Hot.saveButton && Hot.saveButton.classList.contains('disabled') === false) Hot.toggleButtonAndLoaderImage(Hot.saveButton);

    var tableData = Hot.startData;

    // if there are no rows, add one back in and exit
    if (tableData.length === 0) {
      Hot.startData = [];
      Hot.validationFails();
      return false;
    }
  },

  /**
   * Returns true if all items to save have either been successfully saved (not null) or marked as failed.
   */
  areAllProcessed: function () {
    var messages = Hot.messages;
    var savedItems = messages.success.filter(function (item) { return (item !== null); });
    if (savedItems.length + messages.failed.length == messages.success.length) {
      return true;
    } else {
      return false;
    }
  },
  
  /**
   * Saves table data
   * @param attribute of type string ("alias" or "name")
   */
  saveTableData: function (attribute, action) {
    // send table data through the parser to get a data array that isn't merely a reference to Hot.hotTable.getSourceData()
    data = JSON.parse(JSON.parse(JSON.stringify(Hot.hotTable.getSourceData())));
      
    // add previously-saved (alias/name) to success message, and placeholders for items to be saved
    Hot.messages.success = data.map(function (dil) { return (dil.saved === true ? dil[attribute] : null); });
  
    // Array of save functions, one for each line in the table
    var saveArray = (action == "Edit" ? Hot.getArrayOfUpdatedObjects(data) : Hot.getArrayOfNewObjects(data));
    Hot.serial(saveArray); // Execute saves serially  
  },

  /**
   * Adds error message for invalid data and triggers error message display.
   */
  validationFails: function () {
    Hot.messages.failed.push("It looks like some cells are not yet valid. Please fix them before saving.");
    Hot.addErrors(Hot.messages);
  },

  /**
   * Sets cells as invalid so invalid styling will show.
   */
  markCellsInvalid: function (rowIndex, colIndex) {
    Hot.hotTable.setCellMeta(rowIndex, colIndex, 'valid', false);
  },

  /**
   * Disallows further editing after a successful save.
   */
  makeSavedRowsReadOnly: function () {
    Hot.hotTable.updateSettings({
      cells: function (row, col, prop) {
        var cellProperties = {};

        if (Hot.hotTable.getSourceData()[row].saved) {
          cellProperties.readOnly = true;
        }

        return cellProperties;
      }
    });
  },
  
  /**
   * Processes a failure to save (adds invalid attribute to cell, creates user message)
   */
  failSave: function (xhr, rowIndex, numberToSave) {
    console.log(xhr);
    var responseText = JSON.parse(xhr.responseText);
    if (xhr.status >= 500 || responseText.detail == undefined) {
      Hot.messages.failed.push("<b>Row " + (rowIndex + 1) + ": Something went terribly wrong. Please file a ticket with a screenshot or "
          + "copy-paste of the data that you were trying to save.</b>");
    } else {
      var allColumnData = Hot.getValues('data', Hot.colConf);
      var column, columnIndex;
      if (responseText.data && responseText.data.constraintName) {
        // if a column's constraint was violated, extract it here
        column = responseText.data.constraintName;
        columnIndex = allColumnData.indexOf(column);
      }
      console.log(rowIndex, columnIndex);
      if (rowIndex !== undefined && columnIndex !== -1 && columnIndex !== undefined) {
        Hot.hotTable.setCellMeta(rowIndex, columnIndex, 'valid', false);
      }
      // process error message if it was a SQL violation, and add any errors to the messages array
      var reUserMessage = /could not execute .*?: (.*)/;
      var extraCVEMessage = /(.*)ConstraintViolationException: (.*)/;
      var errorMessage1 = responseText.detail.replace(reUserMessage, "$1");
      var finalErrorMessage = errorMessage1.replace(extraCVEMessage, "$2");
      Hot.messages.failed.push("Row "+ (rowIndex + 1) +": "+ finalErrorMessage);
    }
    Hot.addSuccessesAndErrors();
  },

  /**
   * Creates the error message and adds it to the page. Marks cells invalid if necessary. Enables save button.
   */
  addErrors: function (messages) {
    console.log(messages);
    var errorMessages = document.getElementById('errorMessages');
    var ary = messages.failed.map(function (msg) { return '<li>' + msg + '</li>'; });
    errorMessages.innerHTML = '<ul>' + ary.join('') + '</ul>';
    document.getElementById('saveErrors').classList.remove('hidden');
    Hot.hotTable.validateCells();
    for (var j = 0; j < Hot.failedComplexValidation.length; j++) {
      var failedIndices = Hot.failedComplexValidation[j];
      Hot.markCellsInvalid(failedIndices[0], failedIndices[1]);
    }
    Hot.hotTable.render();

    // enable the save button
    if (Hot.saveButton && Hot.saveButton.classList.contains('disabled')) Hot.toggleButtonAndLoaderImage(Hot.saveButton);
  },

  /**
   * Adds the success messages to the page, and calls errors message function if necessary.
   */
  addSuccessesAndErrors: function () {
    // break if not all items have been processed yet
    if (Hot.areAllProcessed() === false) {
      return false;
    }
    var messages = Hot.messages;

    // add error messages
    if (messages.failed.length) {
      Hot.addErrors(messages);
    } else {
      document.getElementById('saveErrors').classList.add('hidden');

      // enable the save button
      if (Hot.saveButton && Hot.saveButton.classList.contains('disabled')) Hot.toggleButtonAndLoaderImage(Hot.saveButton);
    }

    // add success messages
    var successfullySaved = messages.success.filter(function (message) { return (message !== null); });
    if (successfullySaved.length) {
      var successMessage = "Saved " + successfullySaved.length + " items.";
      document.getElementById('successMessages').innerHTML = successMessage;
      document.getElementById('saveSuccesses').classList.remove('hidden');
      Hot.makeSavedRowsReadOnly();
    } else {
      document.getElementById('saveSuccesses').classList.add('hidden');
    }
  }
};
