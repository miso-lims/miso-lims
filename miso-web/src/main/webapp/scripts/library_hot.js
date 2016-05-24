/**
 * Library-specific Handsontable code
 */

Library.hot = {
  libraryData: null,
  showQcs: false,
  
  /**
   * Modifies attributes of Library Dtos so Handsontable displays them correctly
   */
  prepLibrariesForTable: function (libraries) {
    return libraries.map(function (lib) {
      if (lib.libraryAdditionalInfo) {
        // if any members are null, fill them with empty objects otherwise things go poorly
        if (!lib.libraryAdditionalInfo.tissueOrigin) {
          lib.libraryAdditionalInfo.tissueOrigin = { id: '', alias: '' };
        }
        if (!lib.libraryAdditionalInfo.tissueType) {
          lib.libraryAdditionalInfo.tissueType = { id: '', alias: '' };
        }
        if (!lib.libraryAdditionalInfo.prepKit) {
          lib.libraryAdditionalInfo.prepKit = { id: '', alias: '' };
        }
      }
      return lib;
    });
  },
  
  /**
   * Makes create/edit libraries table
   */
  makeHOT: function (startingValues) {
    Hot.colConf = Library.hot.setColumnData(Hot.detailedSample);
    
    if (!startingValues) {
      if (confirm("Please select samples to use as parents for libraries.")) {
        window.location = '/samples';
      } else {
        return false;
      }
    } else {
      Hot.startData = startingValues;
    }
    
    // make HOT instance
    var hotContainer = document.getElementById('hotContainer');
    Hot.hotTable = new Handsontable(hotContainer, {
      debug: true,
      fixedColumnsLeft: 1,
      manualColumnResize: true,
      rowHeaders: true,
      colHeaders: Hot.getValues('header', Hot.colConf),
      contextMenu: false,
      columns: Hot.colConf,
      data: Hot.startData,
      dataSchema: Library.hot.dataSchema
    });
    document.getElementById('hotContainer').style.display = '';
    
    // add listeners for platform and barcode changes
    Handsontable.hooks.add('afterChange', function (changes, source) {
      //TODO: change listeners.
      if('edit' === source) {
        var row = changes[0][0];
        var col = changes[0][1];
        var from = changes[0][2];
        var to = changes[0][3];
        if('platformName' === col) {
            Library.hot.changePlatform(row, col, from, to);
        }
        if('tagBarcodeStrategyName' === col) {
            Library.hot.changeBarcodeKit(row, col, from, to);
        }
      }
    }, Hot.hotTable);
    
    // enable save button if it was disabled
    if (Hot.saveButton && Hot.saveButton.classList.contains('disabled')) Hot.toggleButtonAndLoaderImage(Hot.saveButton);
    
    // TODO: fix this hack
    Library.hot.getBarcodeKitsOnly("Illumina");
    Library.hot.getBarcodePositionsOnly("TruSeq Single Index");
    Library.hot.getBarcodePositionsOnly("Nextera Dual Index");
  },
  
  // TODO: add function regenerateWithQcs
  
  // TODO: add function hideAdditionalCols
  
  /**
   * Data schema for each row in table
   */
  dataSchema: {
    alias: '',
    name: null,
    parentSampleId: null,
    parentSampleAlias: null,
    description: null,
    id: null,
    identificationBarcode: '',
    concentration: 0,
    paired: true,
    qcPassed: '',
    librarySelectionTypeId: null,
    librarySelectionTypeAlias: '',
    libraryStrategyTypeId: null,
    libraryStrategyTypeAlias: '',
    libraryTypeId: null,
    libraryTypeAlias: '',
    lowQuality: null,
    platformName: '',
    tagBarcodes: {
      strategyName: '',
      one: {
        id: '',
        alias: '',
        sequence: ''
      },
      two: {
        id: '',
        alias: '',
        sequence: ''
      }
    },
    volume: null,
    libraryAdditionalInfo: {
      tissueOrigin: {
        id: '',
        alias: ''
      },
      tissueType: {
        id: '',
        alias: ''
      },
      prepKit: {
        id: '',
        alias: ''
      },
      archived: false
    }
  },
  
  /**
   * Gets array of platform names
   */
  getPlatforms: function () {
    return Hot.dropdownRef['platformNames'];
  },
  
  /**
   * Gets array of library types
   */
  getLibraryTypes: function () {
    return Hot.sortByProperty(Hot.dropdownRef['libraryTypes'], 'id').map(Hot.getAlias);
  },
  
  /**
   * Gets array of library selection types
   */
  getSelectionTypes: function () {
    return Hot.sortByProperty(Hot.dropdownRef['selectionTypes'], 'id').map(Hot.getAlias);
  },
  
  /**
   * Gets array of library strategy types
   */
  getStrategyTypes: function () {
    return Hot.sortByProperty(Hot.dropdownRef['strategyTypes'], 'id').map(Hot.getAlias);
  },
  
  /**
   * Gets array of tagbarcode strategies
   */
  getBarcodeStrategies: function () {
    return Hot.sortByProperty(Hot.dropdownRef['barcodeStrategies'], 'id').map(function (bs) { return bs.strategyName; });
  },
  
  /**
   * Gets array of qc values
   */
  getQcValues: function () {
    return Hot.dropdownRef['qcValues'].map(function (val) { if (val === '') val = 'unknown'; return val; });
  },
  
  /**
   * Gets array of tissue origin aliases (detailed sample only)
   */
  getTissueOrigins: function () {
    return Hot.sortByProperty(Hot.sampleOptions['tissueOriginsDtos'], 'id').map(Hot.getAlias);
  },

  /**
   * Gets array of tissue type aliases (detailed sample only)
   */
  getTissueTypes: function () {
    return Hot.sortByProperty(Hot.sampleOptions['tissueTypesDtos'], 'id').map(Hot.getAlias);
  },
  
  /**
   * Gets array of kit descriptor names
   */
  getKitDescriptors: function () {
    return Hot.sortByProperty(Hot.sampleOptions['kitDescriptorsDtos'], 'manufacturer')
      .filter(function (kit) { return kit.kitType == 'Library'; })
      .map(function (kit) { return kit.name; });
  },
  
  /**
   * Gets array of tagBarcode composites (name and sequence)
   */
  getBcComposites: function (bcCollection) {
    return Hot.sortByProperty(bcCollection, 'id').map(function (bc) { return bc.name + ' - ' + bc.sequence; });
  },
  
  /**
   * Gets tagBarcode id associated with given barcode composite (name and sequence)
   */
  getIdFromBcComposite: function (aliasComposite, bcCollection) {
    return bcCollection.filter(function (bc) {
      return (bc.name + ' - ' + bc.sequence == aliasComposite); 
    })[0].id;
  },
  
  /**
   * Sets columns for table
   */
  setColumnData: function (detailedBool) {
    var qcBool = Library.hot.showQcs;
    var cols;
    if (detailedBool) {
      cols = Hot.concatArrays(setAliasCol(), setPlainCols(), setDetailedCols());
    } else {
      cols = Hot.concatArrays(setAliasCol(), setPlainCols());
    }
    // add the ID Barcode column if it is not auto-generated
    if (!Hot.autoGenerateIdBarcodes) {
      cols.splice(3, 0, {
          header: 'Matrix Barcode',
          data: 'identificationBarcode',
          type: 'text'
        }
      );
    }
    return cols;
    
    function setPlainCols () {
      var libCols = [
        {
          header: 'Sample Alias',
          data: 'parentSampleAlias',
          type: 'text',
          readOnly: true
        },{
          header: 'Description',
          data: 'description',
          type: 'text',
          validator: requiredText
        },{
          header: 'Platform',
          data: 'platformName',
          type: 'dropdown',
          trimDropdown: false,
          source: Library.hot.getPlatforms()
        },{
          header: 'Type',
          data: 'libraryTypeAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: ''
        },{
          header: 'Selection',
          data: 'librarySelectionTypeAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Library.hot.getSelectionTypes()
        },{
          header: 'Strategy',
          data: 'libraryStrategyTypeAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Library.hot.getStrategyTypes()
        },{
          header: 'Barcode Kit',
          data: 'tagBarcodeStrategyName',
          type: 'dropdown',
          trimDropdown: false,
          source: ''
        },{
          header: 'Index 1',
          data: 'tagBarcodes.one.alias',
          type: 'dropdown',
          trimDropdown: false,
          source: []
        },{
          header: 'Index 2',
          data: 'tagBarcodes.two.alias',
          type: 'dropdown',
          trimDropdown: false,
          source: [],
          validator: permitEmpty
        } 
      ];
      
      return libCols;
    }
    
    function setAliasCol () {
      var aliasCol = [
        {
          header: 'Library Alias',
          data: 'alias',
          type: 'text',
          validator: validateAlias
        }
      ];
      
      return aliasCol;
    }
    
    function setDetailedCols () {
      var additionalCols = [
        {
          header: 'Kit',
          data: 'libraryAdditionalInfo.prepKit.alias',
          type: 'dropdown',
          trimDropdown: false,
          source: Library.hot.getKitDescriptors()
        },{
          header: 'Volume',
          data: 'volume',
          type: 'numeric',
          format: '0.0'
        },{
          header: 'Conc.',
          data: 'concentration',
          type: 'numeric',
          format: '0.00'
        }
      ];
      
      return additionalCols;
    }
    
    function requiredText (value, callback) {
      if (!value || value.length === 0) {
        callback(false);
      } else {
        callback(true);
      }
    }
    
    function permitEmpty (value, callback) {
      if (value === undefined || value === null || value.length > 0 || value === '') {
        return callback(true);
      } else {
        return callback(false);
      }
    }
    
    function validateAlias (value, callback) {
      if (value) {
        Fluxion.doAjax(
          'libraryControllerHelperService',
          'validateLibraryAlias',
          {
            'alias': value,
            'url': ajaxurl
          },
          {
            'doOnSuccess': function () {
              return callback(true);
            },
            'doOnError': function (json) {
              return callback(false);
            }
          }
        );
      } else {
        return callback(false);
      }
    }
  },
  
  /**
   * Gets the column index for a given attribute
   */
  getColIndex: function (dataString) {
  	for (var i = 0; i < Hot.colConf.length; i++) {
  	  if (Hot.colConf[i].data == dataString) return i;
  	}
  },
  
  /**
   * Detects Platform change for a row and clears out library types, tagBarcode kits, tagBarcodes
   */
  changePlatform: function (row, col, from, to) {
    // update library types
    jQuery.get('../../libraryTypesJson', {platform: to},
      function (data) {
    	var libTypeColIndex = Library.hot.getColIndex('libraryTypeAlias');
        Hot.hotTable.setDataAtCell(row, libTypeColIndex, '', 'platform change');
        Hot.hotTable.getCellMeta(row, libTypeColIndex).source = data['libraryTypes'];
      }    
    );
    // update barcode kits
    // use stored barcode kits if these have already been retrieved. 'to' -- platform
    var bcStratColIndex = Library.hot.getColIndex('tagBarcodeStrategyName');
    if (Hot.dropdownRef.barcodeKits[to]) {
      Hot.hotTable.setDataAtCell(row, bcStratColIndex, '', 'platform change');
      Hot.hotTable.getCellMeta(row, bcStratColIndex).source = Hot.dropdownRef.barcodeKits[to];
    } else {
      jQuery.get('../../barcodeStrategiesJson', {platform: to},
        function (data) {
        console.log(data);
          Hot.hotTable.setDataAtCell(row, bcStratColIndex, '', 'platform change');
          Hot.hotTable.getCellMeta(row, bcStratColIndex).source = data['barcodeKits'];
          Hot.dropdownRef.barcodeKits[to] = {};
          Hot.dropdownRef.barcodeKits[to] = data['barcodeKits'];
        }    
      );
    }
    // clear tagBarcodes
    Hot.hotTable.setDataAtCell(row, (bcStratColIndex + 1), '', 'platform change');
    Hot.hotTable.setDataAtCell(row, (bcStratColIndex + 2), '', 'platform change');
  },
  
  /**
   * Detects tagBarcode kit for a row and clears out tagBarcodes
   */
  changeBarcodeKit: function (row, col, from, to) {
    // use stored barcodes if these have already been retrieved. 'to' == strategy
  	var bcStratColIndex = Library.hot.getColIndex('tagBarcodeStrategyName');
  	var tb1ColIndex = bcStratColIndex + 1;
  	var tb2ColIndex = bcStratColIndex + 2;
    if (Hot.dropdownRef.tagBarcodes[to]) {
      Hot.hotTable.setDataAtCell(row, tb1ColIndex, '', 'barcode kit change');
      Hot.hotTable.getCellMeta(row, tb1ColIndex).source = Library.hot.getBcComposites(Hot.dropdownRef.tagBarcodes[to].one);
      if (Hot.dropdownRef.tagBarcodes[to].two) {
        Hot.hotTable.setDataAtCell(row, tb2ColIndex, '', 'barcode kit change');
        Hot.hotTable.getCellMeta(row, tb2ColIndex).source = Library.hot.getBcComposites(Hot.dropdownRef.tagBarcodes[to].two);
      }
    } else {
      // get barcodes from server
      jQuery.get("../../barcodePositionsJson", {strategy : to},
        function(posData) {
          // set tagBarcodeData
          jQuery.get('../../tagBarcodesJson', {strategy : to , position: 1},
            function (bc1Data) {
              Hot.hotTable.setDataAtCell(row, tb1ColIndex, '', 'barcode kit change');
              Hot.hotTable.getCellMeta(row, tb1ColIndex).source = Library.hot.getBcComposites(bc1Data.tagBarcodes);
              Hot.dropdownRef.tagBarcodes[to] = {};
              Hot.dropdownRef.tagBarcodes[to].one = bc1Data.tagBarcodes;
            }    
          );
          if (posData['numApplicableBarcodes'] == 2) {
            jQuery.get('../../tagBarcodesJson', {strategy : to , position: 2},
              function (bc2Data) {
                Hot.hotTable.setDataAtCell(row, tb2ColIndex, '', 'barcode kit change');
                Hot.hotTable.getCellMeta(row, tb2ColIndex).source = Library.hot.getBcComposites(bc2Data.tagBarcodes);
                Hot.dropdownRef.tagBarcodes[to].two = bc2Data.tagBarcodes;
              }    
            );
          }
        }
      );
    }
  },
  
  /**
   * Gets tagBarcode kits data from server
   */
  getBarcodeKitsOnly: function (platformName) {
    jQuery.get('../../barcodeStrategiesJson', {platform: platformName},
      function (data) {
        Hot.dropdownRef.barcodeKits[platformName] = {};
        Hot.dropdownRef.barcodeKits[platformName] = data['barcodeKits'];
      }    
    );
  },
  
  /**
   * Gets tagBarcodes data from server
   */
  getBarcodePositionsOnly: function (strat) {
    jQuery.get("../../barcodePositionsJson", {strategy : strat},
      function(posData) {
        // set tagBarcodeData
        Hot.dropdownRef.tagBarcodes[strat] = {};
        if (posData['numApplicableBarcodes'] == 2) {
          
          jQuery.get('../../tagBarcodesJson', {strategy : strat , position: 2},
            function (bc2Data) {
              Library
              Hot.dropdownRef.tagBarcodes[strat].two = bc2Data.tagBarcodes;
            }    
          );
        }
        jQuery.get('../../tagBarcodesJson', {strategy : strat , position: 1},
          function (bc1Data) {
            Hot.dropdownRef.tagBarcodes[strat].one = bc1Data.tagBarcodes;
          }    
        );
      }
    );
  },
  
  /**
   * Creates the Library Dtos to pass to the server
   */
  buildLibraryDtosFromData: function (obj) {
    var lib = {};
    
    if (obj.id) {
      lib.id = obj.id;
      lib.name = obj.name;
    }
    
    if (obj.alias) {
      lib.alias = obj.alias;
    }
    
    lib.paired = true;
    
    // add basic library attributes
    lib.description = obj.description;
    lib.parentSampleId = obj.parentSampleId;
    if (obj.initialConcentration) {
      lib.concentration = obj.concentration;
    }
    if (obj.identificationBarcode) {
      lib.identificationBarcode = obj.identificationBarcode;
    }
    
    lib.platformName = obj.platformName;
    
    lib.librarySelectionTypeId = Hot.getIdFromAlias(obj.librarySelectionTypeAlias, Hot.dropdownRef['selectionTypes']);
    lib.libraryStrategyTypeId = Hot.getIdFromAlias(obj.libraryStrategyTypeAlias, Hot.dropdownRef['strategyTypes']);
    lib.libraryTypeId = Hot.getIdFromAlias(obj.libraryTypeAlias, Hot.dropdownRef['libraryTypes']);
    
    if (obj.lowQuality !== undefined) {
      lib.lowQuality = obj.lowQuality;
    } else {
      lib.lowQuality = false;
    }
    
    if (obj.tagBarcodes) {
      if (obj.tagBarcodeStrategyName) {
        var tbsn = obj.tagBarcodeStrategyName;
        lib.tagBarcodeStrategyName = tbsn;
        if (obj.tagBarcodes.one.alias) {
          lib.tagBarcodeIndex1Id = Library.hot.getIdFromBcComposite(obj.tagBarcodes.one.alias, Hot.dropdownRef.tagBarcodes[tbsn].one);
        }
        if (obj.tagBarcodes.two.alias) {
          lib.tagBarcodeIndex2Id = Library.hot.getIdFromBcComposite(obj.tagBarcodes.two.alias, Hot.dropdownRef.tagBarcodes[tbsn].two);
        }
      }
    }
    
    if (obj.volume) {
      lib.volume = obj.volume;
    } else {
      lib.volume = 0;
    }
    
    if (obj.libraryAdditionalInfo) {
      lib.libraryAdditionalInfo = {
        tissueOrigin: obj.libraryAdditionalInfo.tissueOrigin,
        tissueType: obj.libraryAdditionalInfo.tissueType
      };
      if (obj.libraryAdditionalInfo.prepKit.alias) {
        var prepKitAlias = obj.libraryAdditionalInfo.prepKit.alias;
        lib.libraryAdditionalInfo.prepKit = Hot.sampleOptions.kitDescriptorsDtos.filter(function (kd) { return (kd.name == prepKitAlias); })[0];
      }
      if (obj.libraryAdditionalInfo.archived) {
        lib.libraryAdditionalInfo.archived = obj.libraryAdditionalInfo.archived;
      } else {
        lib.libraryAdditionalInfo.archived = false;
      }
    }
    
    lib.qcPassed = obj.qcPassed;
    
    // TODO: add qcCols
    
    return lib;
  },
  
  /**
   * Posts a single library to server and processes result
   */
  saveOneLibrary: function (data, rowIndex, numberToSave, callback) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
      callback(); // Indicate request has completed.
        xhr.status === 201 ? Library.hot.successSave(xhr, rowIndex, numberToSave) : Library.hot.failSave(xhr, rowIndex, numberToSave);
      }
    };
    xhr.open('POST', '/miso/rest/library');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(data);
  },
  
  /**
   * Puts a single library to server and processes result
   */
  updateOneLibrary: function (data, libraryId, rowIndex, numberToSave, callback) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        callback(); // Indicate request has completed.
        xhr.status === 200 ? Library.hot.successSave(xhr, rowIndex, numberToSave) : Library.hot.failSave(xhr, rowIndex, numberToSave);
      }
    };
    xhr.open('PUT', '/miso/rest/library/' + libraryId);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(data);
  },
  
  /**
   * Processes a failure to save (adds invalid attribute to cell, creates user message)
   */
  failSave: function (xhr, rowIndex, numberToSave) {
    console.log(xhr);
    var responseText = JSON.parse(xhr.responseText);
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
    Hot.messages.failed.push("Row "+ (rowIndex + 1) +": "+ responseText.detail.replace(reUserMessage, "$1")); 

    Hot.addSuccessesAndErrors();
  },
  
  /**
   * Processes a successful save
   */
  successSave: function (xhr, rowIndex, numberToSave) {
    // add library url and id to the data source if the library is newly created
    var libraryId;
    if (!Hot.startData[rowIndex].id) {
      Hot.startData[rowIndex].url = xhr.getResponseHeader('Location');
      libraryId = Hot.startData[rowIndex].url.split('/').pop();
      Hot.startData[rowIndex].id = libraryId;
      
      // update this when library alias generator is enabled
      Hot.messages.success[rowIndex] = Hot.startData[rowIndex].alias;
    } else {
      libraryId = Hot.startData[rowIndex].id;
    }
    
    // add a 'saved' attribute to the data source 
    Hot.startData[rowIndex].saved = true;
    
    // create success and error messages
    Hot.addSuccessesAndErrors();
  },
  
  /**
   * Checks if cells are all valid. If yes, POSTs libraries that need to be saved.
   */
  createData: function () {
    var continueValidation = Hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;
    
    Hot.hotTable.validateCells(function (isValid) {
      if (isValid) {
        // send it through the parser to get a sampleData array that isn't merely a reference to Hot.hotTable.getSourceData()
        libsData = JSON.parse(JSON.parse(JSON.stringify(Hot.hotTable.getSourceData())));
          
        // add previously-saved aliases to success message, and placeholders for items to be saved
        Hot.messages.success = libsData.map(function (lib) { return (lib.saved === true ? lib.alias : null); });
    
        // Array of save functions, one for each line in the table
        var libsSaveArray = Library.hot.getArrayOfNewObjects(libsData);
        Hot.serial(libsSaveArray); // Execute saves serially     
      } else {
        Hot.validationFails();
        return false;
      }
    });
  },
  
  /**
   * Checks if cells are all valid. If yes, PUTs libraries that need to be saved.
   */
  updateData: function () {
    var continueValidation = Hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;
    
    Hot.hotTable.validateCells(function (isValid) {
      if (isValid) {
        // send it through the parser to get a sampleData array that isn't merely a reference to Hot.hotTable.getSourceData()
        libsData = JSON.parse(JSON.parse(JSON.stringify(Hot.hotTable.getSourceData())));
        
     // add previously-saved aliases to success message, and placeholders for items to be saved
        Hot.messages.success = libsData.map(function (lib) { return (lib.saved === true ? lib.alias : null); });
        
        // Array of save functions, one for each line in the table
        var libsSaveArray = Library.hot.getArrayOfUpdatedObjects(libsData);
        Hot.serial(libsSaveArray); // Execute saves serially     
      } else {
        Hot.validationFails();
        return false;
      }
    });
  },
  
  /**
   * Creates Library Dtos for libraries to be POSTed
   */
  getArrayOfNewObjects: function (libraryData) {
    // Returns a save function for a single line in the table.
    function librarySaveFunction(data, index, numberToSave) {
      // The callback is called once the http request in saveOneLibrary completes.
      return function(callback) {
        Library.hot.saveOneLibrary(data, index, numberToSave, callback);
      };
    }
    var len = libraryData.length;
    var arrayOfObjects = [];
    
    // return an array of libraries or saveFunctions for libraries
    for (var i = 0; i < len; i++) {
      if (libraryData[i].saved) continue;
      
      var newLibrary = Library.hot.buildLibraryDtosFromData(libraryData[i]);
      if (Hot.detailedSample) {
        arrayOfObjects.push(librarySaveFunction(JSON.stringify(newLibrary), i, len));
      } else {
        arrayOfObjects.push(newLibrary);
      }
    }
    return arrayOfObjects;
  },
  
  /**
   * Creates Library Dtos for libraries to be PUT-ed
   */
  getArrayOfUpdatedObjects: function (libraryData) {
    // Returns a save function for a single line in the table.
    function librarySaveFunction(data, id, rowIndex, numberToSave) {
      // The callback is called once the http request in saveOneLibrary completes.
      return function(callback) {
        Library.hot.updateOneLibrary(data, id, rowIndex, numberToSave, callback);
      };
    }
    var len = libraryData.length;
    var arrayOfObjects = [];
    
    // return an array of samples or saveFunctions for samples
    for (var i = 0; i < len; i++) {
      if (libraryData[i].saved) continue;
      
      var newLibrary = Library.hot.buildLibraryDtosFromData(libraryData[i]);
      
      // all updated objects go through the REST WS
      arrayOfObjects.push(librarySaveFunction(JSON.stringify(newLibrary), newLibrary.id, i, len));
    }
    return arrayOfObjects;
  }
};