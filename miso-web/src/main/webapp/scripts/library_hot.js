/**
 * Library-specific Handsontable code
 */

Library.hot = {
  libraryData: null,
  showQcs: false,
  libraryTypeAliases: {},
  
  getLibraryTypeAliasLists: function () {
    Hot.dropdownRef.libraryTypes.forEach(function (lt) {
      if (Library.hot.libraryTypeAliases[lt.platform]) {
        Library.hot.libraryTypeAliases[lt.platform].push(lt.alias);
      } else {
        Library.hot.libraryTypeAliases[lt.platform] = [lt.alias];
      }
    });
  },
  
  /**
   * Modifies attributes of Library Dtos so Handsontable displays them correctly
   */
  prepLibrariesForPropagate: function (libraries) {
    return libraries.map(function (lib) {
      if (lib.libraryAdditionalInfo) {
        // if any members are null, fill them with empty objects otherwise things go poorly
        if (!lib.libraryAdditionalInfo.prepKit) {
          lib.libraryAdditionalInfo.prepKit = { id: '', alias: '' };
        }
      }
      return lib;
    });
  },

/**
   * Modifies attributes of LibraryDtos so Handsontable displays them for edit
   */
  prepLibrariesForEdit: function (libraries) {
    return libraries.map(function (lib) {
      lib.librarySelectionTypeAlias = Hot.getAliasFromId(lib.librarySelectionTypeId, Hot.dropdownRef.selectionTypes);
      lib.libraryStrategyTypeAlias = Hot.getAliasFromId(lib.libraryStrategyTypeId, Hot.dropdownRef.strategyTypes);
      if (lib.libraryAdditionalInfo) {
        // if any members are null, fill them with empty objects otherwise things go poorly
        if (!lib.libraryAdditionalInfo.prepKit) {
          lib.libraryAdditionalInfo.prepKit = { id: '', alias: '' };
        }
      }
      if (!lib.indexFamilyName) {
        lib.indexFamilyName = 'No index';
      }
      return lib;
    });
  },
  
  /**
   * Makes create/edit libraries table
   */
  makeHOT: function (startingValues) {
    // assign functions which will be required during save
    Hot.buildDtoFunc = Library.hot.buildDtos;
    Hot.saveOneFunc = Library.hot.saveOne;
    Hot.updateOneFunc = Library.hot.updateOne;
    
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
      dataSchema: Library.hot.dataSchema,
      maxRows: startingValues.length
    });
    document.getElementById('hotContainer').style.display = '';
    
    Library.hot.ltIndex = Hot.getColIndex('libraryTypeAlias');
    Library.hot.ifamIndex = Hot.getColIndex('indexFamilyName');
    Library.hot.index1ColIndex = Hot.getColIndex('index1Label');
    Library.hot.index2ColIndex = Hot.getColIndex('index2Label');
    Library.hot.pfIndex = Hot.getColIndex('platformName');
    
    var aliasColIndex = Hot.getColIndex('alias');
    Hot.startData.forEach(function (library, index) {
      if (!library.nonStandardAlias) {
        Hot.hotTable.setCellMeta(index, aliasColIndex, 'validator', Library.hot.validateAlias);
      } else {
        Hot.hotTable.setCellMeta(index, aliasColIndex, 'renderer', Hot.nsAliasRenderer);
        Hot.hotTable.setCellMeta(index, aliasColIndex, 'validator', Hot.requiredText);
        jQuery('#nonStandardAliasNote').show();
      }
    });
    Hot.hotTable.render();
    
    // enable save button if it was disabled
    if (Hot.saveButton && Hot.saveButton.classList.contains('disabled')) Hot.toggleButtonAndLoaderImage(Hot.saveButton);
  },
  
  // TODO: add function regenerateWithQcs
  
  /**
   * Bulk changes to Platform or Index Family mean there need to be corresponding bulk changes to Indices, etc. 
   * This hook sets the correct source for dependent columns once Platform or Index Family are changed.
   */
  addPlatformAndIndexHooks: function () {
    Hot.hotTable.addHook('afterChange', function (changes, source) {
      // 'changes' is a variable-length array of arrays. Each inner array has the following structure:
      // [rowIndex, colName, oldValue, newValue]
      if (['edit', 'autofill', 'paste'].indexOf(source)!= -1) {
        for (var i = 0; i < changes.length; i++) {
          // trigger only if old value is different from new value
          switch (changes[i][1]) {
            case 'platformName':
              if (changes[i][2] != changes[i][3]) {
                Library.hot.changePlatform(changes[i][0], changes[i][1], changes[i][2], changes[i][3]);
              }
              break;
            case 'indexFamilyName':
              if (changes[i][2] != changes[i][3]) {
                Library.hot.changeIndexFamily(changes[i][0], changes[i][1], changes[i][2], changes[i][3]);
              }
              break;
          }
        }
      }
    });
  },
  
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
    indexFamilyName: '',
    index1Label: '',
    index2Label: '',
    volume: null,
    libraryAdditionalInfo: {
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
   * Gets array of kit descriptor names
   */
  getKitDescriptors: function () {
    return Hot.sortByProperty(Hot.sampleOptions['kitDescriptorsDtos'], 'manufacturer')
      .filter(function (kit) { return kit.kitType == 'Library'; })
      .map(function (kit) { return kit.name; });
  },
  
  /**
   * Gets array of index labels (name and sequence)
   */
  getIndexLabels: function (indexCollection) {
    return Hot.sortByProperty(indexCollection, 'id').map(function (index) { return index.label; });
  },
  
  /**
   * Gets index id associated with given index label (name and sequence)
   */
  getIdFromIndexLabel: function (label, indexCollection) {
    return indexCollection.filter(function (index) {
      return (index.label == label); 
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
          validator: Hot.requiredText
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
          source: '',
          validator: Hot.requiredText
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
          header: 'Index Kit',
          data: 'indexFamilyName',
          type: 'dropdown',
          trimDropdown: false,
          source: ''
        },{
          header: 'Index 1',
          data: 'index1Label',
          type: 'autocomplete',
          strict: true,
          allowInvalid: true,
          trimDropdown: false,
          source: [""]
        },{
          header: 'Index 2',
          data: 'index2Label',
          type: 'autocomplete',
          strict: true,
          allowInvalid: true,
          trimDropdown: false,
          source: [""]
        },{
          header: 'QC Passed?',
          data: 'qcPassed',
          type: 'dropdown',
          trimDropdown: false,
          source: ['true', 'false', 'unknown']
        },{
          header: 'Volume',
          data: 'volume',
          type: 'numeric',
          format: '0.0'
        }
      ];
      
      return libCols;
    }
    
    function setAliasCol () {
      var aliasCol = [
        {
          header: 'Library Alias',
          data: 'alias',
          type: 'text'
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
        }
      ];
      
      return additionalCols;
    }
  },
  
  validateAlias: function (value, callback) {
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
  },

  /**
   * Detects Platform change for a row and clears out library type, index family, and indices as these are all platform-specific
   */
  changePlatform: function (row, col, from, to) {
    // update library types
    Hot.startData[row].libraryTypeAlias = '';
    Hot.hotTable.setCellMeta(row, Library.hot.ltIndex, 'source', Library.hot.libraryTypeAliases[to]);

    Library.hot.updateIndexFamilyCellsSources(row, to);
    // clear indexFamily and indices columns
    Hot.hotTable.setDataAtCell(row, Library.hot.ifamIndex, '', 'platform change');
    Hot.startData[row].index1Label = '';
    Hot.startData[row].index2Label = '';
  },

  /**
   * Updates Index Family column source (usually triggered by Platform change)
   */
  updateIndexFamilyCellsSources: function (row, platformName) {
    // update index family
    // use stored index family if it has already been retrieved.
    Hot.hotTable.setCellMeta(row, Library.hot.index1ColIndex, 'source', [""]);
    Hot.hotTable.setCellMeta(row, Library.hot.index2ColIndex, 'source', [""]);
    if (Hot.dropdownRef.indexFamilies[platformName]) {
      Hot.hotTable.setCellMeta(row, Library.hot.ifamIndex, 'source', Object.keys(Hot.dropdownRef.indices[platformName]));
    } else if (platformName) {
      jQuery.get('../../indexFamiliesJson', {platform: platformName},
        function (data) {
          Hot.hotTable.setCellMeta(row, Library.hot.ifamIndex, 'source', data['indexFamilies']);
          Hot.dropdownRef.indexFamilies[platformName] = {};
          Hot.dropdownRef.indexFamilies[platformName] = data['indexFamilies'];
        }    
      );
    }
  },

  /**
   * Updates the source arrays for index1Label and index2Label columns (usually triggered by Index Family change)
   */
  updateIndexCellsSources: function (row, platformName, ifam) {
    function setIndexSource (platformName, ifam, pos, len) {
      Hot.hotTable.setCellMeta(row, Library.hot['index' + pos + 'ColIndex'], 'source', Library.hot.getIndexLabels(Hot.dropdownRef.indices[platformName][ifam][pos]));
      Hot.hotTable.setCellMeta(row, Library.hot['index' + pos + 'ColIndex'], 'readOnly', false);
      // empty source array for index 2 column in case there are no indices for position 2
      if (pos == 1) {
        Hot.hotTable.setCellMeta(row, Library.hot['index2ColIndex'], 'source', [""]);
        Hot.hotTable.setCellMeta(row, Library.hot['index2ColIndex'], 'readOnly', true);
      }
    }
    if (Hot.dropdownRef.indices[platformName] && Hot.dropdownRef.indices[platformName][ifam] && Hot.dropdownRef.indices[platformName][ifam]['1']) {
      // if indices for this indexFamily are already stored locally, use these
      var ifamKeysLength = Object.keys(Hot.dropdownRef.indices[platformName][ifam]).length;
      for (var posn = 1; posn <= ifamKeysLength; posn++) {
        setIndexSource(platformName, ifam, posn, ifamKeysLength);
      }
    } else if (ifam != 'No index' && ifam && platformName) {
      // get indices from server
      jQuery.get("../../library/indexPositionsJson", {indexFamily: ifam},
        function(posData) {
          for (var pos = 1; pos < posData['numApplicableIndices']; pos++) {
            // set indices in stored object
            Hot.dropdownRef.indices[platformName][ifam] = {};
            jQuery.get('../../library/indicesJson', {indexFamily: ifam, position: pos},
              function (json) {
                Hot.dropdownRef.indices[platformName][ifam][pos] = json.indices;
                setIndexSource(platformName, ifam, pos, posData['numApplicableIndices']);
              }
            );
          }
        }
      );
    } else if (ifam == 'No index') {
      Hot.hotTable.setCellMeta(row, Library.hot.index1ColIndex, 'readOnly', true);
      Hot.hotTable.setCellMeta(row, Library.hot.index2ColIndex, 'readOnly', true);
    }
  },
  
  /**
   * Detects index family change for a row and clears out indices
   */
  changeIndexFamily: function (row, col, from, to) {
    // use stored indices if these have already been retrieved. 'to' == indexFamily name
    var pfName = Hot.hotTable.getDataAtCell(row, Library.hot.pfIndex);

    // clear out pre-existing indices
    Hot.startData[row].index1Label = '';
    Hot.startData[row].index2Label = '';
    Hot.hotTable.setDataAtCell(row, Library.hot.index1ColIndex, '', 'index family change');
    Hot.hotTable.setDataAtCell(row, Library.hot.index2ColIndex, '', 'index family change');
    Library.hot.updateIndexCellsSources(row, pfName, to);
  },
  
  /**
   * Gets index families data from server
   */
  getIndexFamiliesOnly: function (platformName) {
    jQuery.get('../../indexFamiliesJson', {platform: platformName},
      function (data) {
        Hot.dropdownRef.indexFamilies[platformName] = {};
        Hot.dropdownRef.indexFamilies[platformName] = data['indexFamilies'];
      }    
    );
  },
  
  /**
   * Gets indices data from server
   */
  getIndexPositionsOnly: function (ifam) {
    if (ifam) {
      jQuery.get("../../indexPositionsJson", {indexFamily : ifam},
        function(posData) {
          for (var pos = 1; pos <= posData['numApplicableIndices']; pos++) {
            // store indices
            jQuery.get('../../indicesJson', {indexFamily : ifam , position: pos},
              function (json) {
                Hot.dropdownRef.indices[ifam][pos] = json.indices;
              }    
            );
          }
        }
      );
    }
  },
  
  /**
   * Creates the Library Dtos to pass to the server
   */
  buildDtos: function (obj) {
    var lib = {};
    // wrap this in try/catch because this callback doesn't trigger error logging
    try {
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

      if (obj.indexFamilyName && obj.indexFamilyName != 'No index') {
        var ifam = obj.indexFamilyName;
        lib.indexFamilyName = ifam;
        if (obj.index1Label) {
          lib.index1Id = Library.hot.getIdFromIndexLabel(obj.index1Label, Hot.dropdownRef.indices[lib.platformName][ifam]['1']);
        } else {
          lib.index1Id = undefined;
        }
        if (obj.index2Label) {
          lib.index2Id = Library.hot.getIdFromIndexLabel(obj.index2Label, Hot.dropdownRef.indices[lib.platformName][ifam]['2']);
        } else {
          lib.index2Id = undefined;
        }
      }

      if (obj.volume) {
        lib.volume = obj.volume;
      } else {
        lib.volume = 0;
      }

      if (obj.libraryAdditionalInfo) {
        lib.libraryAdditionalInfo = {};
        if (obj.libraryAdditionalInfo.prepKit.alias) {
          var prepKitAlias = obj.libraryAdditionalInfo.prepKit.alias;
          lib.libraryAdditionalInfo.prepKit = Hot.sampleOptions.kitDescriptorsDtos.filter(function (kd) { return (kd.name == prepKitAlias); })[0];
        }
        if (obj.libraryAdditionalInfo.archived) {
          lib.libraryAdditionalInfo.archived = obj.libraryAdditionalInfo.archived;
        } else {
          lib.libraryAdditionalInfo.archived = false;
        }
        lib.libraryAdditionalInfo.nonStandardAlias = obj.libraryAdditionalInfo.nonStandardAlias;
      }

      lib.qcPassed = (obj.qcPassed && obj.qcPassed != 'unknown' ? obj.qcPassed : '') || '';

      // TODO: add qcCols
    } catch (e) {
      console.log(e.error);
      return null;
    }
    return lib;
  },
  
  /**
   * Posts a single library to server and processes result
   */
  saveOne: function (data, rowIndex, numberToSave, callback) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
      callback(); // Indicate request has completed.
        xhr.status === 201 ? Library.hot.successSave(xhr, rowIndex, numberToSave) : Hot.failSave(xhr, rowIndex, numberToSave);
      }
    };
    xhr.open('POST', '/miso/rest/library');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(data);
  },
  
  /**
   * Puts a single library to server and processes result
   */
  updateOne: function (data, libraryId, rowIndex, numberToSave, callback) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
      callback(); // Indicate request has completed.
        xhr.status === 200 ? Library.hot.successSave(xhr, rowIndex, numberToSave) : Hot.failSave(xhr, rowIndex, numberToSave);
      }
    };
    xhr.open('PUT', '/miso/rest/library/' + libraryId);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(data);
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
    } else {
      libraryId = Hot.startData[rowIndex].id;
    }
    Hot.messages.success[rowIndex] = Hot.startData[rowIndex].alias;
    
    // add a 'saved' attribute to the data source 
    Hot.startData[rowIndex].saved = true;
    
    // create success and error messages
    Hot.addSuccessesAndErrors();
  },
  
  /**
   * Checks if cells are all valid. If yes, saves libraries that need to be saved.
   */
  saveData: function () {
    var continueValidation = Hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;
    
    Hot.hotTable.validateCells(function (isValid) {
      if (isValid) {
        Hot.saveTableData("alias", Library.hot.propagateOrEdit); 
      } else {
        Hot.validationFails();
        return false;
      }
    });
  }
};