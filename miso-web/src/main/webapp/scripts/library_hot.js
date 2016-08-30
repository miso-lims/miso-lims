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
      if (!lib.tagBarcodeFamilyName) {
        lib.tagBarcodeFamilyName = 'No barcode';
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
    Library.hot.tbfIndex = Hot.getColIndex('tagBarcodeFamilyName');
    Library.hot.tb1ColIndex = Hot.getColIndex('tagBarcodeIndex1Label');
    Library.hot.tb2ColIndex = Hot.getColIndex('tagBarcodeIndex2Label');
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
   * Bulk changes to Platform or Barcode Kit mean there need to be corresponding bulk changes to Indexes, etc. 
   * This hook sets the correct source for dependent columns once Platform or Barcode Kit are changed.
   */
  addPlatformAndTBHooks: function () {
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
            case 'tagBarcodeFamilyName':
              if (changes[i][2] != changes[i][3]) {
                Library.hot.changeBarcodeKit(changes[i][0], changes[i][1], changes[i][2], changes[i][3]);
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
    tagBarcodeFamilyName: '',
    tagBarcodeIndex1Label: '',
    tagBarcodeIndex2Label: '',
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
   * Gets array of tagBarcode composites (name and sequence)
   */
  getBcLabels: function (bcCollection) {
    return Hot.sortByProperty(bcCollection, 'id').map(function (bc) { return bc.label; });
  },
  
  /**
   * Gets tagBarcode id associated with given barcode composite (name and sequence)
   */
  getIdFromBcLabel: function (label, bcCollection) {
    return bcCollection.filter(function (bc) {
      return (bc.label == label); 
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
          header: 'Barcode Kit',
          data: 'tagBarcodeFamilyName',
          type: 'dropdown',
          trimDropdown: false,
          source: ''
        },{
          header: 'Index 1',
          data: 'tagBarcodeIndex1Label',
          type: 'autocomplete',
          strict: true,
          allowInvalid: true,
          trimDropdown: false,
          source: [""]
        },{
          header: 'Index 2',
          data: 'tagBarcodeIndex2Label',
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
   * Detects Platform change for a row and clears out library types, tagBarcode kits, tagBarcodes
   */
  changePlatform: function (row, col, from, to) {
    // update library types
    Hot.startData[row].libraryTypeAlias = '';
    Hot.hotTable.setCellMeta(row, Library.hot.ltIndex, 'source', Library.hot.libraryTypeAliases[to]);

    Library.hot.updateTBFamilyCellsSources(row, to);
    // clear tagBarcodeFamily and tagBarcodes
    Hot.hotTable.setDataAtCell(row, Library.hot.tbfIndex, '', 'platform change');
    Hot.startData[row].tagBarcodeIndex1Label = '';
    Hot.startData[row].tagBarcodeIndex2Label = '';
  },
  
  updateTBFamilyCellsSources: function (row, platformName) {
    // update barcode kits
    // use stored barcode kits if these have already been retrieved.
    Hot.hotTable.setCellMeta(row, Library.hot.tb1ColIndex, 'source', [""]);
    Hot.hotTable.setCellMeta(row, Library.hot.tb2ColIndex, 'source', [""]);
    if (Hot.dropdownRef.barcodeKits[platformName]) {
      Hot.hotTable.setCellMeta(row, Library.hot.tbfIndex, 'source', Object.keys(Hot.dropdownRef.tagBarcodes[platformName]));
    } else if (platformName) {
      jQuery.get('../../tagBarcodeFamiliesJson', {platform: platformName},
        function (data) {
          Hot.hotTable.setCellMeta(row, Library.hot.tbfIndex, 'source', data['barcodeKits']);
          Hot.dropdownRef.barcodeKits[platformName] = {};
          Hot.dropdownRef.barcodeKits[platformName] = data['barcodeKits'];
        }    
      );
    }
  },
  
  updateTBCellsSources: function (row, platformName, tbfName) {
    function setTBSource (platformName, tbfName, pos, len) {
      Hot.hotTable.setCellMeta(row, Library.hot['tb' + pos + 'ColIndex'], 'source', Library.hot.getBcLabels(Hot.dropdownRef.tagBarcodes[platformName][tbfName][pos]));
      Hot.hotTable.setCellMeta(row, Library.hot['tb' + pos + 'ColIndex'], 'readOnly', false);
      // empty source array for barcode 2 column in case there are no barcodes for position 2
      if (pos == 1) {
        Hot.hotTable.setCellMeta(row, Library.hot['tb2ColIndex'], 'source', [""]);
        Hot.hotTable.setCellMeta(row, Library.hot['tb2ColIndex'], 'readOnly', true);
      }
    }
    if (Hot.dropdownRef.tagBarcodes[platformName] && Hot.dropdownRef.tagBarcodes[platformName][tbfName] && Hot.dropdownRef.tagBarcodes[platformName][tbfName]['1']) {
      // if tagBarcodes for this tagBarcodeFamily are already stored locally, use these
      var tbfNameKeysLength = Object.keys(Hot.dropdownRef.tagBarcodes[platformName][tbfName]).length;
      for (var posn = 1; posn <= tbfNameKeysLength; posn++) {
        setTBSource(platformName, tbfName, posn, tbfNameKeysLength);
      }
    } else if (tbfName != 'No barcode' && tbfName && platformName) {
      // get tagBarcodes from server
      jQuery.get("../../barcodePositionsJson", {tagBarcodeFamily: tbfName},
        function(posData) {
          for (var pos = 1; pos < posData['numApplicableBarcodes']; pos++) {
            // set tagBarcodeData
            Hot.dropdownRef.tagBarcodes[platformName][tbfName] = {};
            jQuery.get('../../tagBarcodesJson', {tagBarcodeFamily: tbfName, position: pos},
              function (bcData) {
                Hot.dropdownRef.tagBarcodes[platformName][tbfName][pos] = bcData.tagBarcodes;
                setTBSource(platformName, tbfName, pos, posData['numApplicableBarcodes']);
              }
            );
          }
        }
      );
    } else if (tbfName == 'No barcode') {
      Hot.hotTable.setCellMeta(row, Library.hot.tb1ColIndex, 'readOnly', true);
      Hot.hotTable.setCellMeta(row, Library.hot.tb2ColIndex, 'readOnly', true);
    }
  },
  
  /**
   * Detects tagBarcode kit changes for a row and clears out tagBarcodes
   */
  changeBarcodeKit: function (row, col, from, to) {
    // use stored barcodes if these have already been retrieved. 'to' == tagBarcodeFamily name
    var pfName = Hot.hotTable.getDataAtCell(row, Library.hot.pfIndex);

    // clear out pre-existing tagBarcodes
    Hot.startData[row].tagBarcodeIndex1Label = '';
    Hot.startData[row].tagBarcodeIndex2Label = '';
    Hot.hotTable.setDataAtCell(row, Library.hot.tb1ColIndex, '', 'barcode kit change');
    Hot.hotTable.setDataAtCell(row, Library.hot.tb2ColIndex, '', 'barcode kit change');
    Library.hot.updateTBCellsSources(row, pfName, to);
  },
  
  /**
   * Gets tagBarcode kits data from server
   */
  getBarcodeKitsOnly: function (platformName) {
    jQuery.get('../../tagBarcodeFamiliesJson', {platform: platformName},
      function (data) {
        Hot.dropdownRef.barcodeKits[platformName] = {};
        Hot.dropdownRef.barcodeKits[platformName] = data['barcodeKits'];
      }    
    );
  },
  
  /**
   * Gets tagBarcodes data from server
   */
  getBarcodePositionsOnly: function (tbFamily) {
    if (tbFamily) {
      jQuery.get("../../barcodePositionsJson", {tagBarcodeFamily : tbFamily},
        function(posData) {
          for (var pos = 1; pos <= posData['numApplicableBarcodes']; pos++) {
            // set tagBarcodeData
            jQuery.get('../../tagBarcodesJson', {tagBarcodeFamily : tbFamily , position: pos},
              function (bcData) {
                Hot.dropdownRef.tagBarcodes[tbFamily][pos] = bcData.tagBarcodes;
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

      if (obj.tagBarcodeFamilyName && obj.tagBarcodeFamilyName != 'No barcode') {
        var tbfn = obj.tagBarcodeFamilyName;
        lib.tagBarcodeFamilyName = tbfn;
        if (obj.tagBarcodeIndex1Label) {
          lib.tagBarcodeIndex1Id = Library.hot.getIdFromBcLabel(obj.tagBarcodeIndex1Label, Hot.dropdownRef.tagBarcodes[lib.platformName][tbfn]['1']);
        } else {
          lib.tagBarcodeIndex1Id = undefined;
        }
        if (obj.tagBarcodeIndex2Label) {
          lib.tagBarcodeIndex2Id = Library.hot.getIdFromBcLabel(obj.tagBarcodeIndex2Label, Hot.dropdownRef.tagBarcodes[lib.platformName][tbfn]['2']);
        } else {
          lib.tagBarcodeIndex2Id = undefined;
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