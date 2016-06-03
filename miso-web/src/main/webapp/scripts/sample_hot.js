/**
 * Sample-specific Handsontable code
 */

Sample.hot = {
  projectsArray: null,
  selectedProjectId: null,
  sampleClassId: null,
  sciName: null,
  sampleData: null,
  showQcs: false,
  
  /**
   * Additional sample-specific processing.
   */
  processSampleOptionsFurther: function () {
    Sample.hot.addInstituteAliasToLab();
    Sample.hot.sciName = 'Homo sapiens';
    if (document.getElementById('projectSelect')) {
      Sample.hot.addProjectEtcDropdowns();
    }
  },
  
  /**
   * Modifies attributes of Sample Dtos (ex. adds the alias for each attribute ID) so Handsontable displays correctly
   */
  modifySamplesForEdit: function (samplesArray) {
    return samplesArray.map(function (sam) {
      
      // add sampleAdditionalInfo values
      if (sam.sampleAdditionalInfo) {
        sam.sampleAdditionalInfo.sampleClassAlias = Hot.getAliasFromId(sam.sampleAdditionalInfo.sampleClassId, Hot.sampleOptions.sampleClassesDtos);
        sam.sampleAdditionalInfo.parentSampleClassAlias = Hot.getAliasFromId(sam.sampleAdditionalInfo.parentSampleClassId, Hot.sampleOptions.sampleClassesDtos);
        sam.sampleAdditionalInfo.tissueOriginAlias = Hot.getAliasFromId(sam.sampleAdditionalInfo.tissueOriginId, Hot.sampleOptions.tissueOriginsDtos);
        sam.sampleAdditionalInfo.tissueTypeAlias = Hot.getAliasFromId(sam.sampleAdditionalInfo.tissueTypeId, Hot.sampleOptions.tissueTypesDtos);
        if (sam.sampleAdditionalInfo.prepKitId) {
          sam.sampleAdditionalInfo.prepKitAlias = Hot.getAliasFromId(sam.sampleAdditionalInfo.prepKitId, Hot.sampleOptions.kitDescriptorsDtos);
        }
        if (sam.sampleAdditionalInfo.subprojectId) {
          sam.sampleAdditionalInfo.subprojectAlias = Hot.getAliasFromId(sam.sampleAdditionalInfo.subprojectId, Hot.sampleOptions.subprojectsDtos);
        }
        if (sam.sampleAdditionalInfo.labId) {
          sam.sampleAdditionalInfo.labComposite = Sample.hot.getLabCompositeFromId(sam.sampleAdditionalInfo.labId, Hot.sampleOptions.labsDtos);
        }
        
        // add sampleAnalyte values, if applicable
        if (Sample.hot.getCategoryFromClassId(sam.sampleAdditionalInfo.sampleClassId) == 'Analyte') {
          if (sam.sampleAnalyte.tissueMaterialId) {
            sam.sampleAnalyte.tissueMaterialAlias = Hot.getAliasFromId(sam.sampleAnalyte.tissueMaterialId, Hot.sampleOptions.tissueMaterialsDtos);
          }
          if (sam.sampleAnalyte.samplePurposeId) {
            sam.sampleAnalyte.samplePurposeAlias = Hot.getAliasFromId(sam.sampleAnalyte.samplePurposeId, Hot.sampleOptions.samplePurposesDtos);
          }
        }
      }
      if (sam.receivedDate) {
        sam.receivedDate = sam.receivedDate.substring(0,10);
      }
      
      return sam;
    });
  },
  
  /**
   * Creates proto-samples with inherited attributes from parent samples.
   */
  modifySamplesForPropagate: function (samplesArray) {
    function clone (obj) {
      if (null == obj || "object" != typeof obj) return obj;
      var copy = obj.constructor();
      for (var attr in obj) {
        if (obj.hasOwnProperty(attr)) copy[attr] = obj[attr];
      }
      return copy;
    }
    return samplesArray.map(function (sam) {
      var newSam = {};
      newSam.sampleType = sam.sampleType; 
      newSam.projectId = sam.projectId;
      newSam.scientificName = sam.scientificName;
      newSam.sampleAdditionalInfo = clone(sam.sampleAdditionalInfo);
      newSam.sampleAdditionalInfo.parentSampleClassId = newSam.sampleAdditionalInfo.sampleClassId;
      newSam.sampleAdditionalInfo.parentSampleClassAlias = Hot.getAliasFromId(newSam.sampleAdditionalInfo.parentSampleClassId, Hot.sampleOptions.sampleClassesDtos);
      newSam.sampleAdditionalInfo.sampleClassId = Sample.hot.sampleClassId;
      newSam.sampleAdditionalInfo.sampleClassAlias = Hot.getAliasFromId(newSam.sampleAdditionalInfo.sampleClassId, Hot.sampleOptions.sampleClassesDtos);
      newSam.sampleAdditionalInfo.parentId = parseInt(sam.id);
      newSam.sampleAdditionalInfo.parentAlias = clone(sam.alias);
      if (sam.sampleAdditionalInfo.groupId.length) {
        newSam.sampleAdditionalInfo.groupId = parseInt(sam.sampleAdditionalInfo.groupId);
        newSam.sampleAdditionalInfo.groupDescription = sam.sampleAdditionalInfo.groupDescription;
      }
      
      return newSam;
    });
  },
  
  /**
   * Creates required dropdowns and adds them to the page
   */
  addProjectEtcDropdowns: function () {
    // hide HOT container
    document.getElementById('hotContainer').style.display = 'none';
    // add project dropdown (and optional subproject)
    Sample.hot.addProjectSelect();
    // if detailedSample is selected, add sample class dropdown
    if (Hot.detailedSample) {
      Sample.hot.addClassSelect();
    } else {
      document.getElementById('makeTable').disabled = false;
      document.getElementById('makeTable').classList.remove('disabled');
    }
  },
  
  /**
   * Adds institute alias to lab (lab dropdown displays lab alias + institute alias) (detailed sample only)
   */
  addInstituteAliasToLab: function () {
    // need to be able to display the lab alias and the institute alias
    var labs = Hot.sampleOptions['labsDtos'];
    for (var i=0; i<labs.length; i++) {
      var instituteAlias = Hot.sampleOptions['institutesDtos'].filter(function (inst) {
        return inst.id == labs[i].instituteId;
      })[0].alias;
      labs[i].instituteAlias = instituteAlias;
    }
  },
  
  /**
   * Creates the project dorpdown and adds it to the page
   */
  addProjectSelect: function () {
    Sample.hot.projectsArray = Hot.sortByProperty(Hot.dropdownRef.projects, 'id');
    var select = [];
    select.push('<option value="">Select project</option>');
    for (var i=0; i<Sample.hot.projectsArray.length; i++) {
      select.push('<option value="'+ Sample.hot.projectsArray[i].id +'"');
      select.push(Sample.hot.projectsArray[i].id == Sample.hot.selectedProjectId ? ' selected' : '');
      select.push('>'+ Sample.hot.projectsArray[i].name +' ('+ Sample.hot.projectsArray[i].alias +')</option>');
    }
    document.getElementById('projectSelect').insertAdjacentHTML('beforeend', select.join(''));
    
    // if detailedSample is selected, add subproject dropdown
    if (Hot.detailedSample && Sample.hot.selectedProjectId) {
      Sample.hot.addSubprojectSelect();
      document.getElementById('projectSelect').addEventListener('change', Sample.hot.addSubprojectSelect);
    }
  },

  /**
   * Creates the subproject dropdown and adds it to the page
   */
  addSubprojectSelect: function() {
    if (document.getElementById('projectSelect').value === '') {
      alert("Please select a project.");
      return null;
    }
    var projectId = document.getElementById('projectSelect').value;
    var filteredSubprojects = Hot.sortByProperty(Sample.hot.filterSubprojectsByProjectId(projectId), 'id');
    // display nothing if project has no subprojects
    if (filteredSubprojects.length === 0) {
      document.getElementById('subpSelectOptions').style.display = 'none';
    }  else {
      var select = [];
      select.push('Subproject: ');
      select.push('<select id="subprojectSelect">');
      select.push('<option value="">None</option>');
      for (var i=0; i<filteredSubprojects.length; i++) {
        select.push('<option value="'+ filteredSubprojects[i].id +'">'+ filteredSubprojects[i].alias +'</option>');
      }
      select.push('</select>');
      var subprojectSelect = document.getElementById('subpSelectOptions');
      subprojectSelect.innerHTML = '';
      subprojectSelect.innerHTML = select.join('');
      subprojectSelect.style.display = '';
    }
  },
  
  /**
   * Helper method to return subprojects associated with a given project
   */
  filterSubprojectsByProjectId: function (projectId) {
    var projId = parseInt(projectId);
    var rtn = Hot.sampleOptions.subprojectsDtos.filter(function(subp) {
      return subp.parentProjectId == projId;
    });
    return rtn;
  },
  
  /**
   * Creates dropdown for sample classes
   */
  addClassSelect: function () {
    var select = [];
    var classes = Hot.sortByProperty(Hot.sampleOptions.sampleClassesDtos, 'id');
    select.push('<select id="classDropdown">');
    select.push('<option value="">Select class</option>');
    for (var i=0; i<classes.length; i++) {
      if (classes[i].alias == "Identity") continue;
      select.push('<option value="'+ classes[i].id +'">'+ classes[i].alias +'</option>');
    }
    select.push('</select>');
    document.getElementById('classOptions').innerHTML = select.join('');
    document.getElementById('classDropdown').addEventListener('change', Sample.hot.enableTableButton);
  },
  
  /**
   * Enables "Make Table" button once required dropdowns are chosen
   */
  enableTableButton: function (event) {
    if (event.target.value !== '') {
      document.getElementById('makeTable').disabled = false;
      document.getElementById('makeTable').classList.remove('disabled');
    }
  },
  
  /**
   * Checks to see if table exists before creating a new one.
   */
  makeNewSamplesTable: function () {
    // if this is disabled, alert the user as to why
    if (document.getElementById('makeTable').disabled || document.getElementById('projectSelect').value === '') {
      var message = 'Please select a project ' + (Hot.detailedSample ? 'and sample class ' : '') + 'before creating the table.';
      alert(message);
      return false;
    }
    // if table exists, confirm before obliterating it and creating a new one
    // TODO: add a more nuanced check that checks for actual unsaved changes...
    if (Hot.hotTable) {
      if (confirm("You have unsaved data. Are you sure you wish to abandon your changes and start with a new table?"
          + " (You can press 'Cancel' now and copy your data to paste it in later)") === false) {
        // if detailedSample is enabled, reset the sampleClass value to where it was before the change
        if (Hot.detailedSample) {
          document.getElementById('classDropdown').value = Sample.hot.sampleClassId;
        }
        return false;
      } else {
        // destroy existing table if they do wish to abandon changes
        Hot.hotTable.destroy();
        Hot.startData = [];
      }
    }
    // if detailedSample is enabled, re-store the selected sampleClassId for this table
    if (Hot.detailedSample) {
      Sample.hot.sampleClassId = document.getElementById('classDropdown').value;
      Sample.hot.dataSchema.sampleAdditionalInfo.sampleClassAlias = Hot.getAliasFromId(Sample.hot.sampleClassId, Hot.sampleOptions.sampleClassesDtos);
    }
    
    // make the table
    var sampleCategory = null;
    if (Hot.detailedSample) {
      sampleCategory = Sample.hot.getCategoryFromClassId(document.getElementById('classDropdown').value);
      Sample.hot.dataSchema.scientificName = "Homo sapiens";
    }
    Sample.hot.makeHOT(null, sampleCategory);
    
    // disable sampleClass dropdown so they can't change it midway through editing table values
    if (document.getElementById('classDropdown')) {
      document.getElementById('classDropdown').setAttribute('disabled', 'disabled');
      document.getElementById('classDropdown').classList.add('disabled');
    }
  },
  
  /**
   * Gets number of rows to add to table
   */
  parseIntRows: function (message) {
    var number = window.prompt(message + "How many samples would you like to create?");
    if (number === null) {
      document.getElementById('classDropdown').removeAttribute('disabled');
      document.getElementById('classDropdown').classList.remove('disabled');
      return false;
    }
    if (parseInt(number)) {
      return parseInt(number);
    } else {
      Sample.hot.parseIntRows(number + " is not a number. Please enter a number.\n\n");
    }
  },

  /**
   * Makes create/edit samples table
   */
  makeHOT: function (startingValues, sampleCategory) {
    // are new samples parented to IDs being requested
    var idColBoolean = (startingValues ? false : true);
    Hot.colConf = Sample.hot.setColumnData(Hot.detailedSample, sampleCategory, idColBoolean);

    if (!startingValues) {
      // set initial number of rows to display. 
      var startRowsNumber = Sample.hot.parseIntRows("");
      if (startRowsNumber === false) return false;
      
      // stringify and parse to clone the default object (instead of using referential copies) if detailedSample is enabled
      var defaultObject = (Hot.detailedSample ? JSON.stringify(Sample.hot.getDefaultDetailedValues()) : null);
      Sample.hot.addEmptyRow((startRowsNumber - Hot.startData.length), defaultObject);

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
      dataSchema: Sample.hot.dataSchema
    });
    document.getElementById('hotContainer').style.display = '';
    
    // enable save button if it was disabled
    if (Hot.saveButton && Hot.saveButton.classList.contains('disabled')) Hot.toggleButtonAndLoaderImage(Hot.saveButton);
  },
  
  /**
   * Redraws the samples table to include QC columns
   * TODO: finish this (make sure it contains the necessary columns)
   */
  regenerateWithQcs: function () {
    Sample.hot.showQcs = true;
    var sampleCategory = Sample.hot.getCategoryFromClassId(Hot.hotTable.getSourceData()[0].sampleAdditionalInfo.sampleClassId);
    Hot.colConf = Sample.hot.setColumnData(Hot.detailedSample, sampleCategory, false);
    
    Hot.hotTable.updateSettings({ columns: Hot.colConf, colHeaders: Hot.getValues('header', Hot.colConf) });
  },
  
  /**
   * Hides columns which don't change often and do take up extra space
   * TODO: finish this
   */
  hideAdditionalCols: function () {
    var sampleCategory = Sample.hot.getCategoryFromClassId(Hot.hotTable.getSourceData()[0].sampleAdditionalInfo.sampleClassId);
    Hot.colConf = Sample.hot.setColumnData(false, sampleCategory, false);
    
    Hot.hotTable.updateSettings({ columns: Hot.colConf, colHeaders: Hot.getValues('header', Hot.colConf) });
  },
 
  /**
   * Data schema for each row in table
   */
  dataSchema: {
    project: null,
    id: null,
    description: null,
    receivedDate: null,
    identificationBarcode: null,
    scientificName: this.sciName,
    sampleType: null,
    alias: null,
    qcPassed: '',
    volume: null,
    sampleIdentity: {
      externalName: null,
      donorSex: null
    },
    sampleAdditionalInfo: {
      sampleClassId: null,
      sampleClassAlias: null,
      tissueOriginId: null,
      tissueOriginAlias: null,
      tissueTypeId: null,
      tissueTypeAlias: null,
      passageNumber: null,
      timesReceived: null,
      tubeNumber: null,
      subprojectId: null,
      subprojectAlias: null,
      labId: null,
      labComposite: null,
      prepKitId: null,
      prepKitAlias: null,
      concentration: null,
      qcPassedDetailId: null,
      groupId: null,
      groupDescription:null

    },
    sampleAnalyte: {
      samplePurposeId: null,
      samplePurposeAlias: null,
      tissueMaterialId: null,
      tissueMaterialAlias: null,
      strStatus: null,
      region: null,
      tubeId: null
    },
    sampleTissue: {
      instituteTissueName: null,
      cellularity: null
    }
  },
 
  /**
   * Adds an object to the startData (can have default values)
   */
  addEmptyRow: function (numberToAdd, defaultObject) {
    var number = (numberToAdd === undefined ? 1 : numberToAdd);
    for (var i=1; i<=number; i++) {
      Hot.startData.push((defaultObject ? JSON.parse(defaultObject) : {}));
    }
  },
 
  /**
   * Pre-populates default values for certain columns. (detailed sample only)
   */
  getDefaultDetailedValues: function () {
    var sampleClassAlias = Hot.getAliasFromId(Sample.hot.sampleClassId, Hot.sampleOptions.sampleClassesDtos);
    var rootSampleClassId = Sample.hot.getRootSampleClassId();
    var sampleCategory = Sample.hot.getCategoryFromClassId(document.getElementById('classDropdown').value);
    return {
      'sampleAdditionalInfo': {
        'sampleClassAlias': sampleClassAlias,
        'parentSampleClassId': rootSampleClassId
      },
      'sampleAnalyte': {
        'strStatus': sampleCategory === 'Analyte' ? 'Not Submitted' : null
      },
      'scientificName': Sample.hot.sciName
    };
  },

  /**
   * Gets array of subproject aliases (detailed sample only)
   */
  getSubprojects: function() {
    return Hot.sortByProperty(Hot.sampleOptions.subprojectsDtos, 'id').map(Hot.getAlias);
  },
  
  /**
   * Gets array of sample type aliases (detailed sample only)
   */
  getSampleTypes: function () {
    return Hot.dropdownRef['sampleTypes'];
  },

  /**
   * Gets array of qc values
   */
  getQcValues: function () {
    return Hot.dropdownRef['qcValues'].map(function (val) { if (val === '') val = 'unknown'; return val; });
  },
  
  /**
   * Gets array of STR statuses (detailed sample only)
   */
  getStrStatuses: function () {
    return Hot.dropdownRef['strStatuses'];
  },

  /**
   * Gets array of donor sex values (detailed sample only)
   */
  getDonorSexes: function () {
    return Hot.dropdownRef['donorSexes'];
  },

  /**
   * Gets array of tissue origin aliases (detailed sample only)
   */
  getTissueOrigins: function () {
    return Hot.sortByProperty(Hot.sampleOptions.tissueOriginsDtos, 'id').map(Hot.getAlias);
  },

  /**
   * Gets array of tissue type aliases (detailed sample only)
   */
  getTissueTypes: function () {
    return Hot.sortByProperty(Hot.sampleOptions.tissueTypesDtos, 'id').map(Hot.getAlias);
  },
  
  /**
   * Gets array of tissue material aliases (detailed sample only)
   */
  getTissueMaterials: function () {
    return Hot.sortByProperty(Hot.sampleOptions.tissueMaterialsDtos, 'id').map(Hot.getAlias);
  },

  /**
   * Gets array of sample class aliases (detailed sample only)
   */
  getSampleClasses: function () {
    return Hot.sortByProperty(Hot.sampleOptions.sampleClassesDtos, 'id').map(Hot.getAlias);
  },
  
  /**
   * Gets array of sample class aliases that are a valid child of the given parent (detailed sample only)
   */
  getValidClassesForParent: function (parentScId) {
    var parentSampleClassId = parentScId;
    return Hot.sortByProperty(Hot.sampleOptions.sampleValidRelationshipsDtos, 'id')
               .filter(function (rel) { return rel.parentId == parentSampleClassId; })
               .map(function (rel) { return Hot.getAliasFromId(rel.childId, Hot.sampleOptions.sampleClassesDtos); });
  },

  /**
   * Gets array of sample purpose aliases (detailed sample only)
   */
  getSamplePurposes: function () {
    return Hot.sortByProperty(Hot.sampleOptions.samplePurposesDtos, 'id').map(Hot.getAlias); 
  },

  /**
   * Gets array of qc passed details descriptions (detailed sample only)
   * TODO: remove??
   */
  getQcPassedDetails: function () {
    return Hot.sortByProperty(Hot.sampleOptions.qcPassedDetailsDtos, 'id').map(function (qcpd) { return qcpd.description; });
  },
  
  /**
   * Gets array of lab custom aliases (with institute alias) (detailed sample only)
   */
  getLabs: function () {
    return Hot.sortByProperty(Hot.sampleOptions.labsDtos,'id').map(function (lab) { return lab.alias +' - '+ lab.instituteAlias; });
  },
  
  /**
   * Gets array of kit descriptor names(detailed sample only)
   */
  getKitDescriptors: function () {
    return Hot.sortByProperty(Hot.sampleOptions.kitDescriptorsDtos, 'manufacturer')
      .filter(function (kit) { return kit.kitType == 'Extraction'; })
      .map(function (kit) { return kit.name; });
  },
  
  /**
   * sets which columns will be displayed
   * params: boolean detailedSample
   *         boolean qcBool (have qc columns been requested)
   *         String sampleCategory
   *         boolean idColBoolean (true if no starting values are provided so parent is Identity, false if starting values are provided 
   *                               so parent is sample)
   */
  setColumnData: function (detailedBool, sampleCategory, idColBoolean) {
    var qcBool = Sample.hot.showQcs;
    var cols;
    if (!detailedBool && !qcBool) {
     // if neither detailed sample not qcs are requested
      cols = Hot.concatArrays(setAliasCol(), setPlainCols());
    } else if (!detailedBool && qcBool) {
      // if detailed sample is not requested but qcs are
      cols = Hot.concatArrays(setAliasCol(), setPlainCols(), setQcCols());
    } else if (detailedBool && !qcBool){
      // if detailed sample is requested but qcs are
      cols = Hot.concatArrays(setAliasCol(), setPlainCols(), setDetailedCols(sampleCategory, idColBoolean));
    } else if (detailedBool && qcBool) {
      // if detailed sample and qcs are requested
      cols = Hot.concatArrays(setAliasCol(), setPlainCols(), setDetailedCols(sampleCategory, idColBoolean), setQcCols());
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
      var sampleCols = [
        {
          header: 'Description',
          data: 'description',
          type: 'text',
          validator: requiredText
        }
      ];
        
      if (!detailedBool || idColBoolean) {
        sampleCols.push({
          header: 'Date of receipt',
          data: 'receivedDate',
          type: 'date',
          dateFormat: 'YYYY-MM-DD',
          datePickerConfig: {
            firstDay: 0,
            numberOfMonths: 1
          },
          allowEmpty: true,
          extraneous: true
        });
      }
      
      sampleCols.push(
        {
          header: 'Sample Type',
          data: 'sampleType',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getSampleTypes(),
          validator: validateSampleTypes,
          extraneous: true
        },{
          header: 'Sci. Name',
          data: 'scientificName',
          type: 'text',
          source: Sample.hot.sciName,
          validator: requiredText,
          extraneous: true
        }
      );
      
      return sampleCols;
    }
    
    function setAliasCol () {
      var aliasCol = [
        {
          header: 'Sample Alias',
          data: 'alias',
          type: 'text',
          validator: validateAlias
        }
      ];
            
      return aliasCol;
    }
    
    function setDetailedCols (sampleCategory, idColBoolean) {
      var additionalCols = [
        
      ];
      
      var tissueCols = [
        {
          header: 'Cellularity',
          data: 'sampleTissue.cellularity',
          type: 'text'
        }                 
      ];
       
      var analyteCols = [
        {
          header: 'Material',
          data: 'sampleAnalyte.tissueMaterialAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getTissueMaterials(),
          validator: permitEmpty
        },{
          header: 'Purpose',
          data: 'sampleAnalyte.samplePurposeAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getSamplePurposes(),
          validator: permitEmpty
        },{
          header: 'STR Status',
          data: 'sampleAnalyte.strStatus',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getStrStatuses()
        },{
          header: 'Region',
          data: 'sampleAnalyte.region',
          type: 'text'
        },{
          header: 'Tube ID',
          data: 'sampleAnalyte.tubeId',
          type: 'text'
        },{
          header: 'Group ID',
          data: 'sampleAdditionalInfo.groupId',
          type: 'numeric',
          validator: validateNumber
        },{
          header: 'Group Desc.',
          data: 'sampleAdditionalInfo.groupDescription',
          type: 'text',
          validator: permitEmpty
        },{
          header: 'Kit',
          data: 'sampleAnalyte.prepKitAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getKitDescriptors(),
          validator: permitEmpty
        }
      ];  
      
      // attach either the external name column & cols required to create sample alias, 
      //   or the parentAlias column (depending on what kind of parent is required)
      (function () {
        var parentIdentityCols = [
          {
            header: 'External Name',
            data: 'sampleIdentity.externalName',
            type: 'text',
            validator: requiredText
          },{
            header: 'Sex',
            data: 'sampleIdentity.donorSex',
            type: 'dropdown',
            trimDropdown: false,
            source: Sample.hot.getDonorSexes(),
            validator: permitEmpty
          },{
            header: 'Tissue Origin',
            data: 'sampleAdditionalInfo.tissueOriginAlias',
            type: 'dropdown',
            trimDropdown: false,
            source: Sample.hot.getTissueOrigins(),
            validator: validateTissueOrigins
          },{
            header: 'Tissue Type',
            data: 'sampleAdditionalInfo.tissueTypeAlias',
            type: 'dropdown',
            trimDropdown: false,
            source: Sample.hot.getTissueTypes(),
            validator: validateTissueTypes
          },{
            header: 'Passage #',
            data: 'sampleAdditionalInfo.passageNumber',
            type: 'text',
            validator: validateNumber
          },{
            header: 'Times Received',
            data: 'sampleAdditionalInfo.timesReceived',
            type: 'numeric',
            validator: requiredText
          },{
            header: 'Tube Number',
            data: 'sampleAdditionalInfo.tubeNumber',
            type: 'numeric',
            validator: requiredText
          },{
            header: 'Sample Class',
            data: 'sampleAdditionalInfo.sampleClassAlias',
            type: 'dropdown',
            trimDropdown: false,
            source: Sample.hot.getValidClassesForParent(Sample.hot.getRootSampleClassId())
          },{
            header: 'Lab',
            data: 'sampleAdditionalInfo.labComposite',
            type: 'dropdown',
            trimDropdown: false,
            source: Sample.hot.getLabs(),
            validator: permitEmpty
          },{
            header: 'Ext. Inst. Identifier',
            data: 'sampleAdditionalInfo.externalInstituteIdentifier',
            type: 'text'
          }
        ];
        var parentSampleCols = [
          {
            header: 'Parent Alias',
            data: 'sampleAdditionalInfo.parentAlias',
            type: 'text',
            readOnly: true
          },{
            header: 'Parent Sample Class',
            data: 'sampleAdditionalInfo.parentSampleClassAlias',
            type: 'dropdown',
            trimDropdown: false,
            source: Sample.hot.getSampleClasses(),
            readOnly: true
          },{
            header: 'Sample Class',
            data: 'sampleAdditionalInfo.sampleClassAlias',
            type: 'dropdown',
            trimDropdown: false,
            source: Sample.hot.getSampleClasses()
          }
        ];
        var parentColumn = (idColBoolean ? parentIdentityCols : parentSampleCols);
        additionalCols = Hot.concatArrays(parentColumn, additionalCols);
      }()); 
      return Hot.concatArrays(additionalCols, getSampleCategoryCols(sampleCategory, tissueCols, analyteCols));
    }
    
    function getSampleCategoryCols (sampleCategory, tissueCols, analyteCols) {
      var categoryCols = {
        'Tissue': tissueCols,
        'Analyte': analyteCols
      };
      return categoryCols[sampleCategory];
    }
    
    function setQcCols () {    
      var qcCols = [
        {
          header: 'Vol.',
          data: 'volume',
          type: 'numeric',
          format: '0.00'
        },{
          header: 'Conc.',
          data: 'sampleAdditionalInfo.concentration',
          type: 'numeric',
          format: '0.00'
        },{
          header: 'QC Passed?',
          data: 'qcValue',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getQcValues(),
          validator: permitEmpty
        },{
          header: 'QC Detail',
          data: 'sampleAdditionalInfo.qcPassedDetailAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getQcPassedDetails(),
          validator: permitEmpty
        }
      ];
      return qcCols;
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
        var countAliases = 0;
        for (var i=0; i<Hot.startData.length; i++) {
          if (Hot.startData[i].alias == value) {
            countAliases += 1;
          }
          // the first one will be the first to save, later ones will be unwanted duplicates
          if (countAliases > 1) {
            return callback(false);
          }
        }
        Fluxion.doAjax(
          'sampleControllerHelperService',
          'validateSampleAlias',
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
      } else if (Sample.hot.aliasGenerationEnabled) {
        return callback(true);
      } else {
        return callback(false);
      }
    }
    
    function validateSampleTypes (value, callback) {
      if (Sample.hot.getSampleTypes().indexOf(value) == -1) {
        return callback(false);
      } else {
        return callback(true);
      }
    }
    
    function validateTissueOrigins (value, callback) {
      if (Sample.hot.getTissueOrigins().indexOf(value) == -1) {
        return callback(false);
      } else {
        return callback(true);
      }
    }
    
    function validateTissueTypes (value, callback) {
      if (Sample.hot.getTissueTypes().indexOf(value) == -1) {
        return callback(false);
      } else {
        return callback(true);
      }
    }
    
    function validateNumber (value, callback) {
      if (value === '' || value === null || Handsontable.helper.isNumeric(value) && value > 0) {
        return callback(true);
      } else {
        return callback(false);
      }
    }
  },
  
  /**
   * Gets the sampleCategory associated with a given sampleClass
   */
  getCategoryFromClassId: function (sampleClassId) {
    return Hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
      return sampleClass.id == sampleClassId;
    })[0].sampleCategory;
  },
  
  /**
   * Gets lab id associated with a given lab composite (alias and institute alias)
   */
  getIdFromLabComposite: function (aliasComposite, labCollection) {
    return labCollection.filter(function (lab) {
      // labsDtos was processed at an earlier step to include instituteAlias as property on each lab
      return lab.alias +" - "+ lab.instituteAlias == aliasComposite;
    })[0].id;
  },
  
  /**
   * Creates custom lab composite (alias and institute alias)
   */
  getLabCompositeFromId: function (id, labCollection) {
    var lab = labCollection.filter(function (lab) {
      return lab.id == id;
    })[0];
    return lab.alias +' - '+ lab.instituteAlias;
  },
  
  /**
   * Gets sampleClass id for sampleClass at base of hierarchy (detailed sample only; Identity for OICR)
   */
  getRootSampleClassId: function () {
    return Hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
      // TODO: make this configurable in case an institute wants a root sample class with a different name
      return sampleClass.alias == 'Identity'; 
    })[0].id;
  },
  
  /**
   * Creates the SampleDtos to pass to the server
   */
  buildSampleDtosFromData: function (obj) {
    var sample = {};
    
    if (obj.id) {
      sample.id = obj.id;
      sample.name = obj.name;
      sample.alias = obj.alias;
    }
    
    // add SampleDto attributes
    sample.description = obj.description || '';
    sample.identificationBarcode = obj.identificationBarcode;
    sample.sampleType = obj.sampleType;
    sample.qcPassed = '';
    sample.alias = obj.alias || '';
    sample.projectId = (parseInt(obj.projectId) || parseInt(document.getElementById('projectSelect').value));
    sample.scientificName = obj.scientificName;
    if (obj.receivedDate && obj.receivedDate.length) {
      // the time string is added for detailedSample because the server is expecting a datetime value
      sample.receivedDate = obj.receivedDate += "T00:00:00-05:00";
    }
    
    // if it's a plain sample, return now.
    if (!Hot.detailedSample) {
      return sample;
    }

    sample.rootSampleClassId = Sample.hot.getRootSampleClassId();
    
    // add sample parent attributes. 
    if (obj.sampleIdentity && obj.sampleIdentity.externalName) {
      sample.sampleIdentity = {
          externalName: obj.sampleIdentity.externalName
      };
      if (obj.sampleIdentity.donorSex && obj.sampleIdentity.donorSex.length) {
        sample.sampleIdentity.donorSex = obj.sampleIdentity.donorSex;
      }
    }
    
    // add sampleAdditionalInfo attributes
    sample.sampleAdditionalInfo = {
      passageNumber: (obj.sampleAdditionalInfo.passageNumber == '' ? null : parseInt(obj.sampleAdditionalInfo.passageNumber)),
      timesReceived: parseInt(obj.sampleAdditionalInfo.timesReceived),
      tubeNumber: parseInt(obj.sampleAdditionalInfo.tubeNumber)
    };
    
    // if the table data couldn't have changed (no alias value) then use the original id; 
    // otherwise, generate id from alias (rather than calculating for each field whether the original id corresponds to the current alias
    if (obj.sampleAdditionalInfo.tissueOriginId && !obj.sampleAdditionalInfo.tissueOriginAlias) {
      sample.sampleAdditionalInfo.tissueOriginId = obj.sampleAdditionalInfo.tissueOriginId;
    } else {
      sample.sampleAdditionalInfo.tissueOriginId = Hot.getIdFromAlias(obj.sampleAdditionalInfo.tissueOriginAlias, Hot.sampleOptions.tissueOriginsDtos);
    }
    if (obj.sampleAdditionalInfo.tissueTypeId && !obj.sampleAdditionalInfo.tissueTypeAlias) {
      sample.sampleAdditionalInfo.tissueTypeId = obj.sampleAdditionalInfo.tissueTypeId;
    } else {
      sample.sampleAdditionalInfo.tissueTypeId = Hot.getIdFromAlias(obj.sampleAdditionalInfo.tissueTypeAlias, Hot.sampleOptions.tissueTypesDtos);
    }
    if (obj.sampleAdditionalInfo.sampleClassId && !obj.sampleAdditionalInfo.sampleClassAlias) {
      sample.sampleAdditionalInfo.sampleClassId = obj.sampleAdditionalInfo.sampleClassId;
    } else {
      sample.sampleAdditionalInfo.sampleClassId = Hot.getIdFromAlias(obj.sampleAdditionalInfo.sampleClassAlias, Hot.sampleOptions.sampleClassesDtos);
    }
    // add optional attributes
    if (obj.sampleAdditionalInfo.subprojectId && !obj.sampleAdditionalInfo.subprojectAlias) {
      sample.sampleAdditionalInfo.subprojectId = obj.sampleAdditionalInfo.subprojectId;
    } else if (obj.sampleAdditionalInfo.subprojectAlias){
      sample.sampleAdditionalInfo.subprojectId = Hot.getIdFromAlias(obj.sampleAdditionalInfo.subprojectAlias, Hot.sampleOptions.subprojectsDtos);
    } else if (document.getElementById('subprojectSelect') && document.getElementById('subprojectSelect').value > 0) {
      sample.sampleAdditionalInfo.subprojectId = parseInt(document.getElementById('subprojectSelect').value);
    }
    if (obj.sampleAdditionalInfo.labComposite && obj.sampleAdditionalInfo.labComposite.length) {
      sample.sampleAdditionalInfo.labId = Sample.hot.getIdFromLabComposite(obj.sampleAdditionalInfo.labComposite, Hot.sampleOptions.labsDtos);
    }
    if (obj.sampleAdditionalInfo.externalInstituteIdentifier && obj.sampleAdditionalInfo.externalInstituteIdentifier.length) {
      sample.sampleAdditionalInfo.externalInstituteIdentifier = obj.sampleAdditionalInfo.externalInstituteIdentifier;
    }
    if (obj.sampleAdditionalInfo.prepKitAlias && obj.sampleAdditionalInfo.prepKitAlias.length) {
      sample.sampleAdditionalInfo.prepKitId = Hot.getIdFromAlias(obj.sampleAdditionalInfo.prepKitAlias, Sample.hot.kitDescriptorsDtos);
    }
    if (obj.sampleAdditionalInfo.parentId) {
      sample.sampleAdditionalInfo.parentId = obj.sampleAdditionalInfo.parentId;
    }
    if (obj.sampleAdditionalInfo.groupId) {
      sample.sampleAdditionalInfo.groupId = obj.sampleAdditionalInfo.groupId;
    }
    if (obj.sampleAdditionalInfo.groupDescription && obj.sampleAdditionalInfo.groupDescription.length) {
      sample.sampleAdditionalInfo.groupDescription = obj.sampleAdditionalInfo.groupDescription;
    }
    
    // add sampleAnalyte attributes. 
    if (Sample.hot.getCategoryFromClassId(sample.sampleAdditionalInfo.sampleClassId) == 'Analyte') {
      sample.sampleAnalyte = {};
    
      if (obj.sampleAnalyte) {
        if (obj.sampleAnalyte.samplePurposeAlias && obj.sampleAnalyte.samplePurposeAlias.length) {
          sample.sampleAnalyte.samplePurposeId = Hot.getIdFromAlias(obj.sampleAnalyte.samplePurposeAlias, Hot.sampleOptions.samplePurposesDtos);
        }
        if (obj.sampleAnalyte.sampleGroupComposite && obj.sampleAnalyte.sampleGroupComposite.length) {
          sample.sampleAnalyte.sampleGroupId = Sample.hot.getIdFromSGComposite(obj.sampleAnalyte.sampleGroupComposite, Hot.sampleOptions.sampleGroupsDtos);
        }
        if (obj.sampleAnalyte.tissueMaterialAlias && obj.sampleAnalyte.tissueMaterialAlias.length) {
          sample.sampleAnalyte.tissueMaterialId = Hot.getIdFromAlias(obj.sampleAnalyte.tissueMaterialAlias, Hot.sampleOptions.tissueMaterialsDtos);
        }
        if (obj.sampleAnalyte.strStatus && obj.sampleAnalyte.strStatus.length) {
          sample.sampleAnalyte.strStatus = obj.sampleAnalyte.strStatus;
        }
        if (obj.sampleAnalyte.region && obj.sampleAnalyte.region.length) {
          sample.sampleAnalyte.region = obj.sampleAnalyte.region;
        }
        if (obj.sampleAnalyte.tubeId && obj.sampleAnalyte.tubeId.length) {
          sample.sampleAnalyte.tubeId = obj.sampleAnalyte.tubeId;
        }
      }
    } else if (Sample.hot.getCategoryFromClassId(sample.sampleAdditionalInfo.sampleClassId) == 'Tissue') {
      sample.sampleTissue = {};
      
      if (obj.sampleTissue) {
       if (obj.sampleTissue.cellularity && obj.sampleTissue.cellularity.length) {
          sample.sampleTissue.cellularity = obj.sampleTissue.cellularity;
        }
      }
    }
    
    // TODO: add qcCols attributes to their objects:
    if (obj.qcPassed) {
      sample.qcPassed = (obj.qcPassed == 'unknown' ? '' : obj.qcPassed);
    }
     // TODO: fix QCPD
     //sample.sampleAdditionalInfo.qcPassedDetailId = Hot.getIdFromAlias(obj.sampleAdditionalInfo.qcPassedDetailAlias, Hot.sampleOptions.qcPassedDetailsDtos);
    if (obj.volume && obj.volume.length) {
      sample.volume = obj.volume;
    }
    if (obj.sampleAdditionalInfo.concentration && obj.sampleAdditionalInfo.concentration.length) {
      sample.sampleAdditionalInfo.concentration = obj.sampleAdditionalInfo.concentration;
    }
    return sample;
  },
  
  /**
   * Gets a single sample from server and update alias in table source
   */
  getSampleAlias: function (sampleId, rowIndex) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        xhr.status === 200 ? Sample.hot.updateAlias(xhr, rowIndex) : console.log(xhr);
      }
    };
    xhr.open('GET', '/miso/rest/tree/sample/' + sampleId);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
  },
  
  /**
   * Posts a single sample to server and processes result
   */
  saveOneSample: function (data, rowIndex, numberToSave, callback) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        callback(); // Indicate request has completed.
        xhr.status === 201 ? Sample.hot.successSave(xhr, rowIndex, numberToSave) : Sample.hot.failSave(xhr, rowIndex, numberToSave);
      }
    };
    xhr.open('POST', '/miso/rest/tree/sample');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(data);
  },
  
  /**
   * Puts a single sample to server and processes result
   */
  updateOneSample: function (data, sampleId, rowIndex, numberToSave, callback) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        callback(); // Indicate request has completed.
        xhr.status === 200 ? Sample.hot.successSave(xhr, rowIndex, numberToSave) : Sample.hot.failSave(xhr, rowIndex, numberToSave);
      }
    };
    xhr.open('PUT', '/miso/rest/tree/sample/' + sampleId);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(data);
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
   * Processes a successful save and gets sample from server (to update alias)
   */
  successSave: function (xhr, rowIndex, numberToSave) {
    // add sample url and id to the data source if the sample is newly created
    var sampleId;
    if (!Hot.startData[rowIndex].id) {
      Hot.startData[rowIndex].url = xhr.getResponseHeader('Location');
      sampleId = Hot.startData[rowIndex].url.split('/').pop();
      Hot.startData[rowIndex].id = sampleId;
    } else {
      sampleId = Hot.startData[rowIndex].id;
    }
    
    // add a 'saved' attribute to the data source 
    Hot.startData[rowIndex].saved = true;
    
    // get sample data and update alias
    Sample.hot.getSampleAlias(sampleId, rowIndex);
  },
  
  /**
   * Updates a sample's alias in table source data
   */
  updateAlias: function (xhr, rowIndex) {
    var sample = JSON.parse(xhr.response);
    Hot.messages.success[rowIndex] = sample.alias;
    Hot.hotTable.setDataAtCell(rowIndex, 0, sample.alias);
    Hot.addSuccessesAndErrors();
  },

  /**
   * Checks if cells are all valid. If yes, POSTs samples that need to be saved. (detailed sample only)
   */
  saveDetailedData: function () {
    // check that a project and class have been declared
    if (document.getElementById('projectSelect').value === '' || document.getElementById('classDropdown').value === '') {
      Hot.messages.failed.push('Make sure both Project and Sample Class are selected before saving.');
      Hot.addErrors(Hot.messages);
      return false;
    }
    
    var continueValidation = Hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;
    
    Hot.hotTable.validateCells(function (isValid) { 
      if (isValid) {
        // check for sampleValidRelationship with RootSampleClass as parent
        var parentClassId = Sample.hot.getRootSampleClassId();
        // TODO: update this to check each be the cell, not the dropdown
        var childClassId = document.getElementById('classDropdown').value;
        var validRelationship = Sample.hot.findMatchingRelationship(parentClassId, childClassId);
        if (validRelationship.length === 0) {
          Hot.messages.failed.push(Sample.hot.makeErrorMessageForInvalidRelationship(parentClassId, childClassId) 
                  +  " Please copy your data, select another child class and save again.");
          Hot.addErrors(Hot.messages);
          document.getElementById('classDropdown').removeAttribute('disabled');
          document.getElementById('classDropdown').classList.remove('disabled');
          document.getElementById('classDropdown').classList.add('invalid');
          return false;
        }

        // send it through the parser to get a sampleData array that isn't merely a reference to Hot.hotTable.getSourceData()
        var sampleData = JSON.parse(JSON.parse(JSON.stringify(Hot.hotTable.getSourceData())));
        
        // add aliases of previously-saved items to the position corresponding to their row (zero-index data, one-index UI)
        // aliases of successfully-saved items will be added after save
        Hot.messages.success = sampleData.map(function (sample) { return (sample.saved === true ? sample.alias : null); });

        // Array of save functions, one for each line in the table
        var sampleSaveArray = Sample.hot.getArrayOfNewObjects(sampleData);
        Hot.serial(sampleSaveArray); // Execute saves serially      
      } else {
        Hot.validationFails();
        return false;
      }
    });
  },
  
  /**
   * Creates Sample Dtos for samples to be POSTed
   */
  getArrayOfNewObjects: function (sampleData) {
    // Returns a save function for a single line in the table.
    function sampleSaveFunction(data, index, numberToSave) {
      // The callback is called once the http request in saveOneSample completes.
      return function(callback) {
        Sample.hot.saveOneSample(data, index, numberToSave, callback);
      };
    }
    var len = sampleData.length;
    var arrayOfObjects = [];
    
    // return an array of samples or saveFunctions for samples
    for (var i = 0; i < len; i++) {
      if (sampleData[i].saved) continue;
      
      var newSample = Sample.hot.buildSampleDtosFromData(sampleData[i]);
      if (Hot.detailedSample) {
        arrayOfObjects.push(sampleSaveFunction(JSON.stringify(newSample), i, len));
      } else {
        arrayOfObjects.push(newSample);
      }
    }
    return arrayOfObjects;
  },
  
  /**
   * Creates Sample Dtos for samples to be PUT-ed
   */
  getArrayOfUpdatedObjects: function (sampleData) {
    // Returns a save function for a single line in the table.
    function sampleSaveFunction(data, id, rowIndex, numberToSave) {
      // The callback is called once the http request in saveOneSample completes.
      return function(callback) {
        Sample.hot.updateOneSample(data, id, rowIndex, numberToSave, callback);
      };
    }
    var len = sampleData.length;
    var arrayOfObjects = [];
    
    // return an array of samples or saveFunctions for samples
    for (var i = 0; i < len; i++) {
      if (sampleData[i].saved) continue;
      
      var newSample = Sample.hot.buildSampleDtosFromData(sampleData[i]);
      
      // all updated objects go through the REST WS
      arrayOfObjects.push(sampleSaveFunction(JSON.stringify(newSample), newSample.id, i, len));
    }
    return arrayOfObjects;
  },
  
  /**
   * Creates a custom error message for invalid parent-child relationship
   */
  makeErrorMessageForInvalidRelationship: function (parentClassId, childClassId) {
    var parentClassAlias = Hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
      return sampleClass.id == parentClassId;
    })[0].alias;
    var childClassAlias = Hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
      return sampleClass.id == childClassId;
    })[0].alias;
    return parentClassAlias + ' is not a valid parent for ' + childClassAlias + '.';
  },
  
  /**
   * Checks if all cells are valid. If yes, POSTs samples that need to be saved. (plain sample only)
   */
  savePlainData: function () {
    // check that a project has been declared
    if (document.getElementById('projectSelect').value === '') {
      Hot.messages.failed.push('Make sure that a Project is selected before saving.');
      Hot.addErrors(Hot.messages);
      return false;
    }
    
    var continueValidation = Hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;

    Hot.hotTable.validateCells(function (isValid) {
      if (isValid) {
        document.getElementById('errorMessages').innerHTML = '';
        document.getElementById('saveErrors').classList.add('hidden');
        
        // send it through the parser to get a sampleData array that isn't merely a reference to Hot.hotTable.getSourceData()
        var sampleData = JSON.parse(JSON.parse(JSON.stringify(Hot.hotTable.getSourceData())));
        
        var samplesArray = Sample.hot.getArrayOfNewObjects(sampleData);
        
        Fluxion.doAjax(
          'sampleControllerHelperService',
          'bulkSaveSamples',
          {
            'projectId': Sample.hot.selectedProjectId,
            'samples': samplesArray,
            'url': ajaxurl
          },
          {
            'doOnSuccess': function (json) {
              var taxonErrorSamples = json.taxonErrorSamples;
              var savedSamples = json.savedSamples; // array of saved samples aliases
              
              for (var j=0; j<sampleData.length; j++) {
                // yell if a row's alias is not present in the returned (saved) data
                if (savedSamples.indexOf(sampleData[j].alias) == -1) {
                  Hot.messages.failed.push("Row " + (j+1) +": "+ " Sample did not save. Please check that the sample alias is unique!");
                } else {
                  for (var k=0; k<Hot.startData.length; k++) {
                    if (Hot.startData[k].alias == sampleData[j].alias) {
                      Hot.startData[k].saved = true;
                      Hot.messages.success.push(Hot.startData[k].alias);
                      break;
                    }
                  }
                }
              } 
              // display error/success messages
              Hot.addSuccessesAndErrors();
            },
            'doOnError': function (json) {
              Hot.messages.failed.push(json.error);
              Hot.addErrors(Hot.messages);
              return false;
            }
          }
        );
      } else {
        Hot.validationFails();
        return false;
      }
    });
  },

  /**
   * Checks if all cells are valid. If yes, PUTs samples that need to be saved.
   */
  updateData: function () {
    var continueValidation = Hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;
      
    Hot.hotTable.validateCells(function (isValid) {
      if (isValid) {
        // no check for sampleValidRelationship, since the sampleClass is not editable
        // send data through the parser to get a sampleData array that isn't merely a reference to Hot.hotTable.getSourceData()
        var sampleData = JSON.parse(JSON.parse(JSON.stringify(Hot.hotTable.getSourceData())));
        
        // add previously-saved aliases to success message, and placeholders for items to be saved
        Hot.messages.success = sampleData.map(function (sample) { return (sample.saved === true ? sample.alias : null); });
        
        // Array of save functions, one for each line in the table
        var sampleSaveArray = Sample.hot.getArrayOfUpdatedObjects(sampleData);
        Hot.serial(sampleSaveArray); // Execute saves serially    
      } else {
        Hot.validationFails();
        return false;
      }
    });
  },
  
  /**
   * Checks if all cells are valid. If yes, POSTs new samples (parented to other samples) that need to be saved.
   */
  propagateData: function () {
    var continueValidation = Hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;
    
    // send table data through the parser to get a copy of (not a reference to) sampleData array
    var sampleData = JSON.parse(JSON.parse(JSON.stringify(Hot.hotTable.getSourceData())));
    
    // check for SampleValidRelationships
    var validRelationships = Sample.hot.assessValidRelationships(sampleData);
    if (!validRelationships) {
      Hot.addErrors(Hot.messages);
      return false;
    }
    
    Hot.hotTable.validateCells(function (isValid) {
      if (isValid) {        
        
        // add previously-saved aliases to success message, and placeholders for items to be saved
        Hot.messages.success = sampleData.map(function (sample) { return (sample.saved === true ? sample.alias : null); });
        
        // Array of save functions, one for each line in the table
        var sampleSaveArray = Sample.hot.getArrayOfNewObjects(sampleData);
        Hot.serial(sampleSaveArray); // Execute saves serially
      } else {
        Hot.validationFails();
        return false;
      }
    });
  },
  
  /**
   * Gets sampleValidRelationship which corresponds to given parent and child
   */
  findMatchingRelationship: function (parentClassId, childClassId) {
    var value = Hot.sampleOptions.sampleValidRelationshipsDtos.filter(function (sampleClass) {
      return (sampleClass.parentId == parentClassId && sampleClass.childId == childClassId);
    });
    return value;
  },
  
  /**
   * Gets parent sampleClass and child sampleClass and checks to see if parent and child have an associated valid relationship
   */
  assessValidRelationships: function (sampleData) {
    var col;
    for (var j = 0; j < Hot.hotTable.countCols(); j++) {
      if (Hot.hotTable.getCellMeta(0, j).prop == "sampleAdditionalInfo.sampleClassAlias") {
        col = j;
        break;
      }
    }
    for (var i = 0; i < sampleData.length; i++) {
      var parentClassId = Hot.getIdFromAlias(sampleData[i].sampleAdditionalInfo.parentSampleClassAlias, Hot.sampleOptions.sampleClassesDtos);
      var childClassId = Hot.getIdFromAlias(sampleData[i].sampleAdditionalInfo.sampleClassAlias, Hot.sampleOptions.sampleClassesDtos);
      var validRelationship = Sample.hot.findMatchingRelationship(parentClassId, childClassId);
      if (validRelationship.length === 0) {
        Hot.messages.failed.push("Row " + i + ": " + Sample.hot.makeErrorMessageForInvalidRelationship(parentClassId, childClassId));
        Hot.failedComplexValidation.push([i, col]);
      }
    }
    return (Hot.messages.failed.length ? false : true);
  }
};