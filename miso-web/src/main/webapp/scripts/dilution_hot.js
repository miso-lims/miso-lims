var Dilution = {
  hot: {
    /**
     * Creates proto-dilutions with the parent library as an attribute.
     */
    modifyLibrariesForDilutions: function (libsArray) {
      return libsArray.map(function (lib) {
        return {
          library: lib
        };
      });
    },

    /**
     * Make the Handsontable instance
     */
    makeHOT: function (startingValues) {
      // assign functions which will be required during save
      Hot.buildDtoFunc = Dilution.hot.buildDtos;
      Hot.saveOneFunc = Dilution.hot.saveOne;
      Hot.updateOneFunc = Dilution.hot.saveOne;
      
      Hot.colConf = Dilution.hot.getAppropriateColumns(Hot.detailedSample);
      
      if (!startingValues) {
        if (confirm("Please select libraries to dilute.")) {
          window.location = '/libraries';
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
        dataSchema: Dilution.hot.dataSchema,
        maxRows: startingValues.length,
        beforeAutofill: Hot.incrementingAutofill,
      });
      document.getElementById('hotContainer').style.display = '';
      var tarseqColumnIndex = Hot.getColIndex('targetedSequencingAlias');
      if (tarseqColumnIndex != -1) {
        for (var i = 0; i < startingValues.length; i++) {
          Hot.hotTable.setCellMeta(i, tarseqColumnIndex, 'source', Dilution.hot.getTarSeqs(startingValues[i].library));
        }
      }
      // enable save button if it was disabled
      if (Hot.saveButton && Hot.saveButton.classList.contains('disabled')) Hot.toggleButtonAndLoaderImage(Hot.saveButton);
    },
    
    /**
     * Data schema for each row in table
     */
    dataSchema: {
      id: null,
      name: null,
      identificationBarcode: null,
      locationLabel: null,
      concentration: null,
      dilutionuserName: null,
      targetedSequencingId: null,
      targetedSequencingAlias: null,
      library: {
        id: '',
        alias: ''
      },
      libraryUrl: null
    },
    
    /**
     * Get targeted sequencing aliases (all or by id) (detailed sample only)
     */
    getTarSeqs: function (library) {
      return Hot.sortByProperty(Dilution.hot.tarSeqs, 'targetedSequencingId').filter(function(tarSeq) { return tarSeq.kitDescriptorId == library.kitDescriptorId; }).map(Hot.getAlias);
    },

    /**
     * Gets the targeted sequencing alias for a given id
     */
    getTarSeqAliasFromId: function (tsID) {
      var results = Dilution.hot.tarSeqs.filter(function (ts) {
        return ts.targetedSequencingId == tsID;
      });
      return results.length > 0 ? results[0].alias : null;
    },

    /**
     * Gets the targeted resequecing id for a given alias
     */
    getTarSeqIdFromAlias: function (tsAlias) {
      var results = Dilution.hot.tarSeqs.filter(function (ts) {
        return ts.alias == tsAlias;
      });
      return results.length > 0 ? results[0].targetedSequencingId : null;
    },

    /**
     * Returns a list of the columns to be displayed for the current situation
     * params: string action: one of propagate or update
     *         boolean isDetailed: a detailed sample
     */
    getAppropriateColumns: function (isDetailed) {
      return [
        // Basic columns
        {
          header: 'Dilution Name',
          data: 'name',
          readOnly: true,
          include: true
        },
        {
          header: 'Matrix Barcode',
          data: 'identificationBarcode',
          readOnly: (Hot.autoGenerateIdBarcodes ? true : false),
          include: true
        },
        {
          header: 'Library Alias',
          data: 'library.alias',
          readOnly: true,
          include: true
        },
        {
          header: 'Conc. (' + Dilution.hot.units + ')',
          data: 'concentration',
          type: 'numeric',
          format: '0.00',
          validator: Hot.requiredNumber,
          renderer: Hot.requiredNumericRenderer,
          include: true
        },
        {
          header: 'Creation Date',
          data: 'creationDate',
          type: 'date',
          dateFormat: 'YYYY-MM-DD',
          datePickerConfig: {
            firstDay: 0,
            numberOfMonths: 1
          },
          allowEmpty: false,
          validator: Hot.requiredText,
          renderer: Hot.requiredAutocompleteRenderer,
          include: true
        },
        {
          header: 'Targeted Sequencing',
          data: 'targetedSequencingAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: [],
          validator: Hot.permitEmpty,
          include: isDetailed
        }
      ].filter(function(x) { return x.include; });
    },
    
    /**
     * Creates the DilutionDtos to pass to the server
     */
    buildDtos: function (obj) {
      var dilution = {};

      // wrap this in try/catch because this callback doesn't trigger error logging
      try {
        if (obj.id) {
          dilution.id = obj.id;
          dilution.name = obj.name;
        }

        dilution.library = Dilution.hot.librariesJSON.filter(function(original) { return original.id = obj.library.id; })[0];
        dilution.identificationBarcode = obj.identificationBarcode;
        dilution.concentration = obj.concentration;
        dilution.creationDate = obj.creationDate;
        if (Hot.detailedSample && obj.targetedSequencingAlias) {
          dilution.targetedSequencingId = Dilution.hot.getTarSeqIdFromAlias(obj.targetedSequencingAlias);
        }
      } catch (e) {
        console.log(e);
        return null;
      }
      return dilution;
    },
    
    /**
     * Get single dilution from server and update name in table source
     */
    getDilutionName: function (dilutionId, rowIndex) {
      var xhr = new XMLHttpRequest();
      xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
          xhr.status === 200 ? Dilution.hot.updateNameAndIDBC(xhr, rowIndex) : console.log(xhr);
        }
      };
      xhr.open('GET', '/miso/rest/librarydilution/' + dilutionId);
      xhr.setRequestHeader('Content-Type', 'application/json');
      xhr.send();
    },
    
    /**
     * Posts a single dilution to server and processes results
     */
    saveOne: function (data, rowIndex, numberToSave, callback) {
      var xhr = new XMLHttpRequest();
      xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE) {
          callback(); // Indicate request has completed.
          xhr.status === 201 ? Dilution.hot.successSave(xhr, rowIndex, numberToSave) : Hot.failSave(xhr, rowIndex, numberToSave);
        }
      };
      var restMethod = (Dilution.hot.propagateOrEdit == 'Edit' ? 'PUT' : 'POST');
      xhr.open(restMethod, '/miso/rest/librarydilution');
      xhr.setRequestHeader('Content-Type', 'application/json');
      xhr.send(data);
    },
    
    /**
     * Check if cells are all valid. If yes, POSTs dilutions that need to be saved.
     */
    saveData: function () {
      var continueValidation = Hot.cleanRowsAndToggleSaveButton();
      if (continueValidation === false) return false;
      
      Hot.hotTable.validateCells(function (isValid) {
        if (isValid) {
          Hot.saveTableData("name", Dilution.hot.propagateOrEdit);    
        } else {
          Hot.validationFails();
          return false;
        }
      });
    },
    
    updateNameAndIDBC: function (xhr, rowIndex) {
      var dilution = JSON.parse(xhr.response);
      Hot.messages.success[rowIndex] = dilution.name;
      Hot.hotTable.setDataAtCell(rowIndex, 0, dilution.name);
      if (Hot.autoGenerateIdBarcodes) Hot.hotTable.setDataAtCell(rowIndex, 1, dilution.identificationBarcode);
      Hot.addSuccessesAndErrors();
    },

    /**
     * Processes a successful save and gets dilution from server (to update name)
     */
    successSave: function (xhr, rowIndex, numberToSave) {
      // add dilution id to the data source if the dilution is newly created
      var dilutionId;
      if (!Hot.startData[rowIndex].id) {
        dilutionId = xhr.getResponseHeader('Id');
        Hot.startData[rowIndex].id = dilutionId;
      } else {
        dilutionId = Hot.startData[rowIndex].id;
      }

      // add a 'saved' attribute to the data source
      Hot.startData[rowIndex].saved = true;

      // get dilution data and update name
      Dilution.hot.getDilutionName(dilutionId, rowIndex);
    },
    
    updateNameAndIDBC: function (xhr, rowIndex) {
      var dilution = JSON.parse(xhr.response);
      Hot.messages.success[rowIndex] = dilution.name;
      Hot.hotTable.setDataAtCell(rowIndex, 0, dilution.name);
      if (dilution.identificationBarcode) Hot.hotTable.setDataAtCell(rowIndex, 1, dilution.identificationBarcode);
      Hot.addSuccessesAndErrors();
    }
    
  }  
};
