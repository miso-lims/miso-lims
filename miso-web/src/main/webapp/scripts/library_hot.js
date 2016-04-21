Library.hot = {
  detailedSample: null,
  colConf: null,
  hotTable: null,
  sampleOptions: null,
  libraryData: null,
  showQcs: false,
  button: null,
  failedComplexValidation: [],
  messages: {
    success: [],
    failed: []
  },
  
  fetchSampleOptions: function (callback) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        xhr.status === 200 ? Library.hot.processSampleOptionsXhr(xhr, callback) : console.log(xhr.response);
      }
    };
    xhr.open('GET', '/miso/rest/ui/sampleoptions');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
  },
  
  processSampleOptionsXhr: function (xhr, callback) {
    this.sampleOptions = JSON.parse(xhr.responseText);
    
    // execute callback if provided
    if (callback) callback();
  },
  
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
  
  makeHOT: function (startingValues) {
    this.colConf = Library.hot.setColumnData(this.detailedSample);
    
    if (!startingValues) {
      if (confirm("Please select samples to use as parents for libraries.")) {
        window.location = '/samples';
      } else {
        return false;
      }
    } else {
      Library.hot.startData = startingValues;
    }
    
    // make HOT instance
    var hotContainer = document.getElementById('hotContainer');
    Library.hot.hotTable = new Handsontable(hotContainer, {
      debug: true,
      fixedColumnsLeft: 1,
      manualColumnResize: true,
      rowHeaders: true,
      colHeaders: this.getValues('header', this.colConf),
      contextMenu: false,
      columns: this.colConf,
      data: this.startData,
      dataSchema: this.dataSchema
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
    }, Library.hot.hotTable);
    
    // enable save button if it was disabled
    if (Library.hot.button && Library.hot.button.className.indexOf('disabled') !== -1) Library.hot.toggleButtonAndLoaderImage(Library.hot.button);
    
    // TODO: fix this hack
    Library.hot.getBarcodeKitsOnly("Illumina");
    Library.hot.getBarcodePositionsOnly("TruSeq Single Index");
    Library.hot.getBarcodePositionsOnly("Nextera Dual Index");
  },
  
  // TODO: add function regenerateWithQcs
  
  // TODO: add function hideAdditionalCols
  
  startData: [],
  
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
  
  getAlias: function (obj) {
    if (obj.alias) return obj.alias;
  },
  
  getValues: function (key, objArr) {
    return objArr.map(function (obj) { return obj[key]; });
  },
  
  sortByProperty: function (array, propertyName) {
    return array.sort(function (a, b) {
      return a[propertyName] > b[propertyName] ? 1 : ((b[propertyName] > a[propertyName]) ? -1 : 0);
    });
  },
  
  getPlatforms: function () {
    return this.dropdownRef['platformNames'];
  },
  
  getLibraryTypes: function () {
    return this.sortByProperty(this.dropdownRef['libraryTypes'], 'id').map(this.getAlias);
  },
  
  getSelectionTypes: function () {
    return this.sortByProperty(this.dropdownRef['selectionTypes'], 'id').map(this.getAlias);
  },
  
  getStrategyTypes: function () {
    return this.sortByProperty(this.dropdownRef['strategyTypes'], 'id').map(this.getAlias);
  },
  
  getBarcodeStrategies: function () {
    return this.sortByProperty(this.dropdownRef['barcodeStrategies'], 'id').map(function (bs) { return bs.strategyName; });
  },
  
  getQcValues: function () {
    return this.dropdownRef['qcValues'].map(function (val) { if (val === '') val = 'unknown'; return val; });
  },
  
  getTissueOrigins: function () {
    return this.sortByProperty(this.sampleOptions['tissueOriginsDtos'], 'id').map(this.getAlias);
  },

  getTissueTypes: function () {
    return this.sortByProperty(this.sampleOptions['tissueTypesDtos'], 'id').map(this.getAlias);
  },
  
  getKitDescriptors: function () {
    return this.sortByProperty(this.sampleOptions['kitDescriptorsDtos'], 'manufacturer')
      .filter(function (kit) { return kit.kitType == 'Library'; })
      .map(function (kit) { return kit.name; });
  },
  
  getIdFromAlias: function (alias, referenceCollection) {
    return referenceCollection.filter(function (item) {
      return item.alias == alias; 
    })[0].id;
  },
  
  getAliasFromId: function (id, referenceCollection) {
    return referenceCollection.filter(function (item) {
      return item.id == id;
    })[0].alias;
  },
  
  getBcComposites: function (bcCollection) {
    return this.sortByProperty(bcCollection, 'id').map(function (bc) { return bc.name + ' - ' + bc.sequence; });
  },
  
  getIdFromBcComposite: function (aliasComposite, bcCollection) {
    return bcCollection.filter(function (bc) {
      return (bc.name + ' - ' + bc.sequence == aliasComposite); 
    })[0].id;
  },
  
  setColumnData: function (detailedBool) {
    var qcBool = Library.hot.showQcs;
    if (detailedBool) {
      return this.concatArrays(setAliasCol(), setPlainCols(), setDetailedCols());
    } else {
      return this.concatArrays(setAliasCol(), setPlainCols());
    }
    
    function setPlainCols () {
      var libCols = [
        {
          header: 'Parent Alias',
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
          header: 'Barcode',
          data: 'identificationBarcode',
          type: 'text'
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
          header: 'Alias',
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
  
  concatArrays: function () {
    // call this function with any number of col groups and it will concat and reduce them all
    var cols = [];
    for (var i = 0; i<arguments.length; i++) {
      cols.push.apply(cols, arguments[i]); 
    }
    return cols.reduce(function (a, b) { return a.concat(b); }, []);
  },
  
  getColIndex: function (dataString) {
	for (var i = 0; i < Library.hot.colConf.length; i++) {
	  if (Library.hot.colConf[i].data == dataString) return i;
	}
  },
  
  changePlatform: function (row, col, from, to) {
    // update library types
    jQuery.get('../../libraryTypesJson', {platform: to},
      function (data) {
    	var libTypeColIndex = Library.hot.getColIndex('libraryTypeAlias');
        Library.hot.hotTable.setDataAtCell(row, libTypeColIndex, '', 'platform change');
        Library.hot.hotTable.getCellMeta(row, libTypeColIndex).source = data['libraryTypes'];
      }    
    );
    // update barcode kits
    // use stored barcode kits if these have already been retrieved. 'to' -- platform
    var bcStratColIndex = Library.hot.getColIndex('tagBarcodeStrategyName');
    if (Library.hot.dropdownRef.barcodeKits[to]) {
      Library.hot.hotTable.setDataAtCell(row, bcStratColIndex, '', 'platform change');
      Library.hot.hotTable.getCellMeta(row, bcStratColIndex).source = Library.hot.dropdownRef.barcodeKits[to];
    } else {
      jQuery.get('../../barcodeStrategiesJson', {platform: to},
        function (data) {
        console.log(data);
          Library.hot.hotTable.setDataAtCell(row, bcStratColIndex, '', 'platform change');
          Library.hot.hotTable.getCellMeta(row, bcStratColIndex).source = data['barcodeKits'];
          Library.hot.dropdownRef.barcodeKits[to] = {};
          Library.hot.dropdownRef.barcodeKits[to] = data['barcodeKits'];
        }    
      );
    }
    // clear tagBarcodes
    Library.hot.hotTable.setDataAtCell(row, (bcStratColIndex + 1), '', 'platform change');
    Library.hot.hotTable.setDataAtCell(row, (bcStratColIndex + 2), '', 'platform change');
  },
  
  changeBarcodeKit: function (row, col, from, to) {
    // use stored barcodes if these have already been retrieved. 'to' == strategy
	var bcStratColIndex = Library.hot.getColIndex('tagBarcodeStrategyName');
	var tb1ColIndex = bcStratColIndex + 1;
	var tb2ColIndex = bcStratColIndex + 2;
    if (Library.hot.dropdownRef.tagBarcodes[to]) {
      Library.hot.hotTable.setDataAtCell(row, tb1ColIndex, '', 'barcode kit change');
      Library.hot.hotTable.getCellMeta(row, tb1ColIndex).source = Library.hot.getBcComposites(Library.hot.dropdownRef.tagBarcodes[to].one);
      if (Library.hot.dropdownRef.tagBarcodes[to].two) {
        Library.hot.hotTable.setDataAtCell(row, tb2ColIndex, '', 'barcode kit change');
        Library.hot.hotTable.getCellMeta(row, tb2ColIndex).source = Library.hot.getBcComposites(Library.hot.dropdownRef.tagBarcodes[to].two);
      }
    } else {
      // get barcodes from server
      jQuery.get("../../barcodePositionsJson", {strategy : to},
        function(posData) {
          // set tagBarcodeData
          jQuery.get('../../tagBarcodesJson', {strategy : to , position: 1},
            function (bc1Data) {
              Library.hot.hotTable.setDataAtCell(row, tb1ColIndex, '', 'barcode kit change');
              Library.hot.hotTable.getCellMeta(row, tb1ColIndex).source = Library.hot.getBcComposites(bc1Data.tagBarcodes);
              Library.hot.dropdownRef.tagBarcodes[to] = {};
              Library.hot.dropdownRef.tagBarcodes[to].one = bc1Data.tagBarcodes;
            }    
          );
          if (posData['numApplicableBarcodes'] == 2) {
            jQuery.get('../../tagBarcodesJson', {strategy : to , position: 2},
              function (bc2Data) {
                Library.hot.hotTable.setDataAtCell(row, tb2ColIndex, '', 'barcode kit change');
                Library.hot.hotTable.getCellMeta(row, tb2ColIndex).source = Library.hot.getBcComposites(bc2Data.tagBarcodes);
                Library.hot.dropdownRef.tagBarcodes[to].two = bc2Data.tagBarcodes;
              }    
            );
          }
        }
      );
    }
  },
  
  getBarcodeKitsOnly: function (platformName) {
    jQuery.get('../../barcodeStrategiesJson', {platform: platformName},
      function (data) {
        Library.hot.dropdownRef.barcodeKits[platformName] = {};
        Library.hot.dropdownRef.barcodeKits[platformName] = data['barcodeKits'];
      }    
    );
  },
  
  getBarcodePositionsOnly: function (strat) {
    jQuery.get("../../barcodePositionsJson", {strategy : strat},
      function(posData) {
        // set tagBarcodeData
        Library.hot.dropdownRef.tagBarcodes[strat] = {};
        if (posData['numApplicableBarcodes'] == 2) {
          
          jQuery.get('../../tagBarcodesJson', {strategy : strat , position: 2},
            function (bc2Data) {
              Library
              Library.hot.dropdownRef.tagBarcodes[strat].two = bc2Data.tagBarcodes;
            }    
          );
        }
        jQuery.get('../../tagBarcodesJson', {strategy : strat , position: 1},
          function (bc1Data) {
            Library.hot.dropdownRef.tagBarcodes[strat].one = bc1Data.tagBarcodes;
          }    
        );
      }
    );
  },
  
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
    
    lib.librarySelectionTypeId = Library.hot.getIdFromAlias(obj.librarySelectionTypeAlias, Library.hot.dropdownRef['selectionTypes']);
    lib.libraryStrategyTypeId = Library.hot.getIdFromAlias(obj.libraryStrategyTypeAlias, Library.hot.dropdownRef['strategyTypes']);
    lib.libraryTypeId = Library.hot.getIdFromAlias(obj.libraryTypeAlias, Library.hot.dropdownRef['libraryTypes']);
    
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
          lib.tagBarcodeIndex1Id = Library.hot.getIdFromBcComposite(obj.tagBarcodes.one.alias, Library.hot.dropdownRef.tagBarcodes[tbsn].one);
        }
        if (obj.tagBarcodes.two.alias) {
          lib.tagBarcodeIndex2Id = Library.hot.getIdFromBcComposite(obj.tagBarcodes.two.alias, Library.hot.dropdownRef.tagBarcodes[tbsn].two);
        }
      }
    }
    
    if (obj.volume) {
      lib.volume = obj.volume;
    } else {
      lib.volume = 0;
    }
    
    if (obj.libraryAdditionalInfo) {
      var tissueOriginAlias = obj.libraryAdditionalInfo.tissueOrigin.alias;
      var tissueTypeAlias = obj.libraryAdditionalInfo.tissueType.alias;
      lib.libraryAdditionalInfo = {
        tissueOrigin: obj.libraryAdditionalInfo.tissueOrigin,
        tissueType: obj.libraryAdditionalInfo.tissueType
      };
      if (obj.libraryAdditionalInfo.prepKit.alias) {
        var prepKitAlias = obj.libraryAdditionalInfo.prepKit.alias;
        lib.libraryAdditionalInfo.prepKit = Library.hot.sampleOptions.kitDescriptorsDtos.filter(function (kd) { return (kd.name == prepKitAlias); })[0];
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
  
  failSave: function (xhr, rowIndex, numberToSave) {
    console.log(xhr);
    var responseText = JSON.parse(xhr.responseText);
    var allColumnData = Library.hot.getValues('data', Library.hot.colConf);
    var column, columnIndex;
    if (responseText.data && responseText.data.constraintName) {
      // if a column's constraint was violated, extract it here
      column = responseText.data.constraintName;
      columnIndex = allColumnData.indexOf(column);
    }
    console.log(rowIndex, columnIndex);
    if (rowIndex !== undefined && columnIndex !== -1 && columnIndex !== undefined) {
      Library.hot.hotTable.setCellMeta(rowIndex, columnIndex, 'valid', false);
    }
    // process error message if it was a SQL violation, and add any errors to the messages array
    var reUserMessage = /could not execute .*?: (.*)/;
    Library.hot.messages.failed.push("Row "+ (rowIndex + 1) +": "+ responseText.detail.replace(reUserMessage, "$1")); 
    
    // display any errors if this is the final sample to be saved
    if (Library.hot.messages.success.length + Library.hot.messages.failed.length == numberToSave) {
      Library.hot.addAnyErrors();
    }
  },
  
  successSave: function (xhr, rowIndex, numberToSave) {
    // push row index for new saves (previously-saved items have their aliases added)
    Library.hot.messages.success.push(rowIndex); 
    
    // add a 'saved' attribute to the data source 
    Library.hot.startData[rowIndex].saved = true;
    
    // add sample url and id to the data source if the sample is newly created
    if (!Library.hot.startData[rowIndex].id) {
      Library.hot.startData[rowIndex].url = xhr.getResponseHeader('Location');
      Library.hot.startData[rowIndex].id = Library.hot.startData[rowIndex].url.split('/').pop();
    }
    
    // display any errors if this is the final sample to be saved
    if (Library.hot.messages.success.length + Library.hot.messages.failed.length == numberToSave) {
      Library.hot.addAnyErrors();
    }
  },
  
  createData: function () {
    var continueValidation = Library.hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;
    
    Library.hot.hotTable.validateCells(function (isValid) {
      if (isValid) {
        // send it through the parser to get a sampleData array that isn't merely a reference to Library.hot.hotTable.getSourceData()
        libsData = JSON.parse(JSON.parse(JSON.stringify(Library.hot.hotTable.getSourceData())));
          
        Library.hot.messages.success = libsData.filter(function (lib) { return (lib.saved === true); })
                                               .map(function (lib) { return lib.alias; });
    
        // Array of save functions, one for each line in the table
        var libsSaveArray = Library.hot.getArrayOfNewObjects(libsData);
        Library.hot.serial(libsSaveArray); // Execute saves serially     
      } else {
        Library.hot.validationFails();
        return false;
      }
    });
  },
  
  updateData: function () {
    var continueValidation = Library.hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;
    
    Library.hot.hotTable.validateCells(function (isValid) {
      if (isValid) {
        // send it through the parser to get a sampleData array that isn't merely a reference to Library.hot.hotTable.getSourceData()
        libsData = JSON.parse(JSON.parse(JSON.stringify(Library.hot.hotTable.getSourceData())));
        
        Library.hot.messages.success = libsData.filter(function (lib) { return (lib.saved === true); })
                                               .map(function (lib) { return lib.alias; });
        
        // Array of save functions, one for each line in the table
        var libsSaveArray = Library.hot.getArrayOfUpdatedObjects(libsData);
        Library.hot.serial(libsSaveArray); // Execute saves serially     
      } else {
        Library.hot.validationFails();
        return false;
      }
    });
  },
  
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
      if (Library.hot.detailedSample) {
        arrayOfObjects.push(librarySaveFunction(JSON.stringify(newLibrary), i, len));
      } else {
        arrayOfObjects.push(newLibrary);
      }
    }
    return arrayOfObjects;
  },
  
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
  },
  
  /**
   * Serial execution of an array of functions. 
   * @param {Function[]} aof - Each function takes a single callback function parameter.
   */
  serial: function(aof) {
    var invokeNext = function(index) {
      if(index < (aof.length)) {
        aof[index](function() { invokeNext(index + 1);} );
      }
    };
    invokeNext(0);
  },
  
  cleanRowsAndToggleSaveButton: function () {
    // reset error and success messages
    Library.hot.messages.success = [];
    Library.hot.messages.failed = [];
    
    // disable the save button
    if (Library.hot.button) Library.hot.toggleButtonAndLoaderImage(Library.hot.button);
    
    var tableData = Library.hot.startData;
    
    // if last row is empty, remove it before validation
    Library.hot.removeEmptyBottomRows(tableData);
    
    // if there are no rows, add one back in and exit
    if (tableData.length === 0) {
      Library.hot.startData = [];
      return false;
    }
  },
  
  removeEmptyBottomRows: function (tableData) {
    while (Library.hot.startData.length > 1 && Library.hot.hotTable.isEmptyRow(tableData.length - 1)) {
      Library.hot.hotTable.alter('remove_row', parseInt(tableData.length - 1), keepEmptyRows = false);
    }
  },
  
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
  
  validationFails: function () {
    Library.hot.messages.failed.push("It looks like some cells are not yet valid. Please fix them before saving.");
    Library.hot.addAnyErrors();
  },
  
  addAnyErrors: function () {
    var messages = Library.hot.messages;
    console.log(Library.hot.messages);
    
    if (messages.success.length) {
      var previouslySaved = messages.success.filter(function (message) { return (!parseInt(message) && message !== 0); });
      var successMessage = "Successfully saved " + messages.success.length + " out of " + (messages.success.length + messages.failed.length)
                             + " libraries. " + previouslySaved.length + " libraries were saved previously.";
      document.getElementById('successMessages').innerHTML = successMessage;
      document.getElementById('saveSuccesses').classList.remove('hidden');
      Library.hot.makeSavedRowsReadOnly();
    } else {
      document.getElementById('saveSuccesses').classList.add('hidden');
    }
    
    
    if (Library.hot.button) Library.hot.toggleButtonAndLoaderImage(Library.hot.button);
    
    if (messages.failed.length) {
      var errorMessages = document.getElementById('errorMessages');
      var ary = ["<ul>"];
      for (var i=0; i<messages.failed.length; i++) {
        ary.push("<li>"+ messages.failed[i] +"</li>");
      }
      ary.push("</ul>");
      errorMessages.innerHTML = '';
      errorMessages.innerHTML = ary.join('');
      document.getElementById('saveErrors').classList.remove('hidden');
      Library.hot.hotTable.validateCells();
      for (var i = 0; i < Library.hot.failedComplexValidation.length; i++) {
        var failedIndices = Library.hot.failedComplexValidation[i];
        Library.hot.markCellsInvalid(failedIndices[0], failedIndices[1]);
      }
      Library.hot.hotTable.render();
      return false;
    } else {
      document.getElementById('saveErrors').classList.add('hidden');
    }
    return true;
  },
  
  markCellsInvalid: function (rowIndex, colIndex) {
    Library.hot.hotTable.setCellMeta(rowIndex, colIndex, 'valid', false);
  },
  
  makeSavedRowsReadOnly: function () {
    Library.hot.hotTable.updateSettings({
      cells: function (row, col, prop) {
        var cellProperties = {};
        
        if (Library.hot.hotTable.getSourceData()[row].saved) {
          cellProperties.readOnly = true;
        }
        
        return cellProperties;
      }
    });
  }
};