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
   * Toggles loader gif and disabled styling and attribute of save button
   */
  toggleButtonAndLoaderImage: function (button) {
    var ajaxLoader;
    if (button.className.indexOf('disabled') == -1) {
      button.classList.add('disabled');
      button.setAttribute('disabled', 'disabled');
      ajaxLoader = "<img id='ajaxLoader' src='/../styles/images/ajax-loader.gif'/>";
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

    // if last row is empty, remove it before validation
    Hot.removeEmptyBottomRows(tableData);

    // if there are no rows, add one back in and exit
    if (tableData.length === 0) {
      Hot.startData = [];
      Hot.validationFails();
      return false;
    }
  },

  /**
   * Removes bottom rows if bottom rows are empty (usually from the user dragging too far down)
   */
  removeEmptyBottomRows: function (tableData) {
    while (Hot.startData.length > 1 && Hot.hotTable.isEmptyRow(tableData.length - 1)) {
      Hot.hotTable.alter('remove_row', tableData.length - 1, keepEmptyRows = false);
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
