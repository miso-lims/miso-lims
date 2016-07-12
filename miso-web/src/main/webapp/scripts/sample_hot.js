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
    Sample.hot.sampleClassId = samplesArray[0].sampleClassId;
    return samplesArray.map(function (sam) {

      // add sampleAdditionalInfo values
      if (sam.type != 'Plain') {
        // add attributes if it's a first receipt
        sam.sampleClassAlias = Hot.getAliasFromId(sam.sampleClassId, Hot.sampleOptions.sampleClassesDtos);
        if (sam.parentSampleClassId) sam.parentSampleClassAlias = Hot.getAliasFromId(sam.parentSampleClassId, Hot.sampleOptions.sampleClassesDtos);
        if (sam.tissueOriginId) sam.tissueOriginAlias = Hot.getAliasFromId(sam.tissueOriginId, Hot.sampleOptions.tissueOriginsDtos);
        if (sam.tissueTypeId) sam.tissueTypeAlias = Hot.getAliasFromId(sam.tissueTypeId, Hot.sampleOptions.tissueTypesDtos);
        if (sam.labId) sam.labComposite = Sample.hot.getLabCompositeFromId(sam.labId, Hot.sampleOptions.labsDtos);
        if (sam.prepKitId) sam.prepKitAlias = Hot.getAliasFromId(sam.prepKitId, Hot.sampleOptions.kitDescriptorsDtos);
        if (sam.subprojectId) sam.subprojectAlias = Hot.getAliasFromId(sam.subprojectId, Hot.sampleOptions.subprojectsDtos);

        // add sampleAnalyte values, if applicable
        if (Sample.hot.getCategoryFromClassId(sam.sampleClassId) == 'Tissue') {
          if (sam.tissueMaterialId) {
            sam.tissueMaterialAlias = Hot.getAliasFromId(sam.tissueMaterialId, Hot.sampleOptions.tissueMaterialsDtos);
          }
		  sam.tissueOriginAlias = Hot.getAliasFromId(sam.tissueOriginId, Hot.sampleOptions.tissueOriginsDtos);
		  sam.tissueTypeAlias = Hot.getAliasFromId(sam.tissueTypeId, Hot.sampleOptions.tissueTypesDtos);
		  if (sam.labId) {
		    sam.labComposite = Sample.hot.getLabCompositeFromId(sam.labId, Hot.sampleOptions.labsDtos);
		  }
        }
        if (Sample.hot.getCategoryFromClassId(sam.sampleClassId) == 'Aliquot') {
          if (sam.samplePurposeId) {
            sam.samplePurposeAlias = Hot.getAliasFromId(sam.samplePurposeId, Hot.sampleOptions.samplePurposesDtos);
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
   * Creates proto-samples with some inherited attributes from parent samples.
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
      newSam.parentSampleClassId = sam.sampleClassId;
      newSam.parentSampleClassAlias = Hot.getAliasFromId(newSam.parentSampleClassId, Hot.sampleOptions.sampleClassesDtos);
      newSam.sampleClassId = Sample.hot.sampleClassId;
      newSam.sampleClassAlias = Hot.getAliasFromId(newSam.sampleClassId, Hot.sampleOptions.sampleClassesDtos);
      newSam.parentId = parseInt(sam.id);
      newSam.parentAlias = clone(sam.alias);
      newSam.tissueOriginId = sam.tissueOriginId;
      newSam.tissueOriginAlias = sam.tissueOriginAlias;
      newSam.tissueTypeId = sam.tissueTypeId;
      newSam.tissueTypeAlias = sam.tissueTypeAlias;
      newSam.timesReceived = sam.timesReceived;
      newSam.tubeNumber = sam.tubeNumber;
      newSam.passageNumber = sam.passageNumber;
      newSam.externalInstituteIdentifier = sam.externalInstituteIdentifier;
      newSam.labId = sam.labId;
      if (sam.groupId.length) {
        newSam.groupId = parseInt(sam.groupId);
        newSam.groupDescription = sam.groupDescription;
      }
      if (sam.subprojectId) {
        newSam.subprojectId = parseInt(sam.subprojectId);
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
      select.push('>'+ Sample.hot.projectsArray[i].alias +' ('+ Sample.hot.projectsArray[i].name +')</option>');
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
    var classOptions = Sample.hot.getNewSampleClassOptions();
    for (var i=0; i<classOptions.length; i++) {
     if (classes[i].alias == "Identity") continue;
       select.push('<option value="'+ classOptions[i].id +'">'+ classOptions[i].alias +'</option>');
    }
    select.push('</select>');
    document.getElementById('classOptions').innerHTML = select.join('');
    document.getElementById('classDropdown').addEventListener('change', Sample.hot.enableTableButton);
  },

  /**
   * Returns true if a new sample of the provided SampleClass can be created without an existing parent
   */
  canCreateNew: function (sampleClass) {
    return sampleClass.sampleCategory === "Tissue" || (sampleClass.sampleCategory === "Analyte" && sampleClass.stock === true);
  },

  /**
   * Returns the SampleClasses which may be created without an existing parent
   */
  getNewSampleClassOptions: function () {
    var classes = Hot.sortByProperty(Hot.sampleOptions.sampleClassesDtos, 'id');
    var options = [];
    for (var i=0; i<classes.length; i++) {
      if (Sample.hot.canCreateNew(classes[i])) {
        options.push(classes[i]);
      }
    }
    return options;
  },

  /**
   * Returns the alias of each SampleClass which may be created without an existing parent
   */
  getNewSampleClassOptionsAliasOnly: function () {
    var classes = Hot.sortByProperty(Hot.sampleOptions.sampleClassesDtos, 'id');
    var options = [];
    for (var i=0; i<classes.length; i++) {
      if (Sample.hot.canCreateNew(classes[i])) {
        options.push(classes[i].alias);
      }
    }
    return options;
  },

  getTissueClassesAliasOnly: function () {
    var classes = Hot.sortByProperty(Hot.sampleOptions.sampleClassesDtos, 'id');
    var options = [];
    for (var i=0; i<classes.length; i++) {
      if (classes[i].sampleCategory === 'Tissue') {
        options.push(classes[i].alias);
      }
    }
    return options;
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
      Sample.hot.dataSchema.sampleClassAlias = Hot.getAliasFromId(Sample.hot.sampleClassId, Hot.sampleOptions.sampleClassesDtos);
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
    var sampleCategory = Sample.hot.getCategoryFromClassId(Hot.hotTable.getSourceData()[0].sampleClassId);
    Hot.colConf = Sample.hot.setColumnData(Hot.detailedSample, sampleCategory, false);

    Hot.hotTable.updateSettings({ columns: Hot.colConf, colHeaders: Hot.getValues('header', Hot.colConf) });
  },

  /**
   * Hides columns which don't change often and do take up extra space
   * TODO: finish this
   */
  hideAdditionalCols: function () {
    var sampleCategory = Sample.hot.getCategoryFromClassId(Hot.hotTable.getSourceData()[0].sampleClassId);
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
    externalName: null,
    donorSex: null,
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
    groupDescription:null,
    samplePurposeId: null,
    samplePurposeAlias: null,
    tissueMaterialId: null,
    tissueMaterialAlias: null,
    strStatus: null,
    region: null,
    tubeId: null,
    instituteTissueName: null,
    cuts: null,
    discards: null,
    thickness: null,
    cutsConsumed: null
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
    return {
      'sampleClassAlias': sampleClassAlias,
      'parentSampleClassId': rootSampleClassId,
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
    var cols = Hot.concatArrays(setAliasCol(), setPlainCols(), detailedBool ? setDetailedCols(sampleCategory, idColBoolean) : [], qcBool ? setQcCols() : []);
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
        },{
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
        },{
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
      ];

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
        {
          header: 'Group ID',
          data: 'groupId',
          type: 'numeric',
          validator: validateNumber
        },{
          header: 'Group Desc.',
          data: 'groupDescription',
          type: 'text',
          validator: permitEmpty
        }
      ];
      
      var tissueCols = [
        {
          header: 'Material',
          data: 'tissueMaterialAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getTissueMaterials(),
          validator: permitEmpty
        },{
          header: 'Region',
          data: 'region',
          type: 'text'
        }
      ];

      var aliquotCols = [
        {
          header: 'Purpose',
          data: 'samplePurposeAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getSamplePurposes(),
          validator: permitEmpty
        },{
          header: 'Kit',
          data: 'prepKitAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getKitDescriptors(),
          validator: permitEmpty
        }
      ];
      var stockCols = [
        {
          header: 'STR Status',
          data: 'strStatus',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getStrStatuses()
        }
      ];

      var tissueProcessingCols = {
        'CV Slide': [
          {
            header: 'Cuts',
            data: 'cuts',
            type: 'numeric',
            validator: requiredText
          },{
            header: 'Discards',
            data: 'discards',
            type: 'numeric'
          },{
            header: 'Thickness',
            data: 'thickness',
            type: 'numeric'
          }
        ],
        'LCM Tube': [
          {
            header: 'Cuts Consumed',
            data: 'cutsConsumed',
            type: 'numeric',
            validator: requiredText
          }
        ],
        'HE Slide': [],
        'Curls': []
      };

      var aliquotCols = [
        {
          header: 'Purpose',
          data: 'samplePurposeAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getSamplePurposes(),
          validator: permitEmpty
        },{
          header: 'Kit',
          data: 'prepKitAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getKitDescriptors(),
          validator: permitEmpty
        }
      ];
      var stockCols = [
        {
          header: 'STR Status',
          data: 'strStatus',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getStrStatuses()
        }
      ];
      
      // fields required to create a tissue parent and identify or create an identity parent 
      // for the tissue. Used when receiving new samples
      var parentIdentityCols = [
        {
          header: 'External Name',
          data: 'externalName',
          type: 'text',
          validator: requiredText
        },{
          header: 'Sex',
          data: 'donorSex',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getDonorSexes(),
          validator: permitEmpty
        }
      ];
      
      if (sampleCategory === 'Aliquot' || sampleCategory === 'Stock') {
        parentIdentityCols.push({
          header: 'Tissue Class',
          data: 'parentSampleClassAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getTissueClassesAliasOnly(),
          validator: validateTissueClasses
        });
      }
      
      parentIdentityCols.push({
        header: 'Tissue Origin',
        data: 'tissueOriginAlias',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getTissueOrigins(),
        validator: validateTissueOrigins
      },{
        header: 'Tissue Type',
        data: 'tissueTypeAlias',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getTissueTypes(),
        validator: validateTissueTypes
      },{
        header: 'Passage #',
        data: 'passageNumber',
        type: 'text',
        validator: validateNumber
      },{
        header: 'Times Received',
        data: 'timesReceived',
        type: 'numeric',
        validator: requiredText
      },{
        header: 'Tube Number',
        data: 'tubeNumber',
        type: 'numeric',
        validator: requiredText
      },{
        header: 'Cellularity',
        data: 'cellularity',
        type: 'text'
      },{
        header: 'Sample Class',
        data: 'sampleClassAlias',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getNewSampleClassOptionsAliasOnly()
      },{
        header: 'Lab',
        data: 'labComposite',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getLabs(),
        validator: permitEmpty
      },{
        header: 'Ext. Inst. Identifier',
        data: 'externalInstituteIdentifier',
        type: 'text'
      });
      
      // fields used when propagating
      var parentSampleCols = [
        {
          header: 'Parent Alias',
          data: 'parentAlias',
          type: 'text',
          readOnly: true
        },{
          header: 'Parent Sample Class',
          data: 'parentSampleClassAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getSampleClasses(),
          readOnly: true
        },{
          header: 'Sample Class',
          data: 'sampleClassAlias',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getSampleClasses()
        }
      ];
      var parentColumn = (idColBoolean ? parentIdentityCols : parentSampleCols);
      additionalCols = Hot.concatArrays(parentColumn, additionalCols);
      
      return Hot.concatArrays(additionalCols, getSampleCategoryCols(sampleCategory, tissueCols, tissueProcessingCols, analyteCols));
    }

    function getSampleCategoryCols (sampleCategory, tissueCols, tissueProcessingCols, aliquotCols, stockCols) {
      var categoryCols = {
        'Tissue': tissueCols,
        'Aliquot': aliquotCols,
        'Stock': stockCols,
        'Tissue Processing': tissueProcessingCols[Hot.getAliasFromId(Sample.hot.sampleClassId, Hot.sampleOptions.sampleClassesDtos)]
        // Tissue Processing is different because the columns vary by sample class, unlike the other sample categories
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
          data: 'concentration',
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
          data: 'qcPassedDetailAlias',
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

    function validateTissueClasses (value, callback) {
      if (Sample.hot.getTissueClassesAliasOnly().indexOf(value) == -1) {
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
    var sampleClasses = Hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
      return sampleClass.id == sampleClassId;
    });
    if (sampleClasses.length > 0) {
      return sampleClasses[0].sampleCategory;
    }
    return 'Plain';
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
      sample.type = 'Plain';
      return sample;
    }

    sample.rootSampleClassId = Sample.hot.getRootSampleClassId();
    
    // add sample parent attributes, and all other attributes for the first receipt of a sample
    if (obj.externalName) {
      sample.externalName = obj.externalName;
      if (obj.donorSex && obj.donorSex.length) sample.donorSex = obj.donorSex;
      
      if (obj.tissueOriginId && !obj.tissueOriginAlias) {
        sample.tissueOriginId = obj.tissueOriginId;
      } else {
        sample.tissueOriginId = Hot.getIdFromAlias(obj.tissueOriginAlias, Hot.sampleOptions.tissueOriginsDtos);
      }
      if (obj.tissueTypeId && !obj.tissueTypeAlias) {
        sample.tissueTypeId = obj.tissueTypeId;
      } else {
        sample.tissueTypeId = Hot.getIdFromAlias(obj.tissueTypeAlias, Hot.sampleOptions.tissueTypesDtos);
      }
      sample.passageNumber = (obj.passageNumber == '' ? null : parseInt(obj.passageNumber));
      sample.timesReceived = parseInt(obj.timesReceived);
      sample.tubeNumber = parseInt(obj.tubeNumber);
      if (obj.labComposite && obj.labComposite.length) {
        sample.labId = Sample.hot.getIdFromLabComposite(obj.labComposite, Hot.sampleOptions.labsDtos);
      }
      if (obj.externalInstituteIdentifier && obj.externalInstituteIdentifier.length) {
        sample.externalInstituteIdentifier = obj.externalInstituteIdentifier;
      }
    }


    // if the table data couldn't have changed (no alias value) then use the original id;
    // otherwise, generate id from alias (rather than calculating for each field whether the original id corresponds to the current alias
    if (obj.sampleClassId && !obj.sampleClassAlias) {
      sample.sampleClassId = obj.sampleClassId;
    } else {
      sample.sampleClassId = Hot.getIdFromAlias(obj.sampleClassAlias, Hot.sampleOptions.sampleClassesDtos);
    }
    sample.type = Sample.hot.getCategoryFromClassId(sample.sampleClassId);
    // add optional attributes
    if (obj.subprojectId && !obj.subprojectAlias) {
      sample.subprojectId = obj.subprojectId;
    } else if (obj.subprojectAlias){
      sample.subprojectId = Hot.getIdFromAlias(obj.subprojectAlias, Hot.sampleOptions.subprojectsDtos);
    } else if (document.getElementById('subprojectSelect') && document.getElementById('subprojectSelect').value > 0) {
      sample.subprojectId = parseInt(document.getElementById('subprojectSelect').value);
    }
    if (obj.prepKitAlias && obj.prepKitAlias.length) {
      sample.prepKitId = Hot.getIdFromAlias(obj.prepKitAlias, Sample.hot.kitDescriptorsDtos);
    }
    if (obj.parentId) {
      sample.parentId = obj.parentId;
    }
    if (obj.groupId) {
      sample.groupId = obj.groupId;
    }
    if (obj.groupDescription && obj.groupDescription.length) {
      sample.groupDescription = obj.groupDescription;
    }

    // add SampleCategory-specific attributes.
    switch (Sample.hot.getCategoryFromClassId(sample.sampleClassId)) {
    case 'Aliquot':
      if (obj.samplePurposeAlias && obj.samplePurposeAlias.length) {
        sample.samplePurposeId = Hot.getIdFromAlias(obj.samplePurposeAlias, Hot.sampleOptions.samplePurposesDtos);
      }
      if (obj.sampleGroupComposite && obj.sampleGroupComposite.length) {
        sample.sampleGroupId = Sample.hot.getIdFromSGComposite(obj.sampleGroupComposite, Hot.sampleOptions.sampleGroupsDtos);
      }
      break;
    case 'Stock':
      if (obj.strStatus && obj.strStatus.length) {
        sample.strStatus = obj.strStatus;
      }
    case 'Tissue Processing':
      if (obj.cuts) {
        sample.cuts = obj.cuts;
        sample.type = 'CV Slide'; // add type info for deserialization
        sample.discards = (obj.discards ? obj.discards : 0);
        if (obj.thickness) sample.thickness = obj.thickness;
      }
      if (obj.cutsConsumed) {
        sample.cutsConsumed = obj.cutsConsumed;
        sample.type = 'LCM Tube'; // add type info for deserialization
      }
      break;
    }
    
    // TODO: add qcCols attributes to their objects:
    if (obj.qcPassed) {
      sample.qcPassed = (obj.qcPassed == 'unknown' ? '' : obj.qcPassed);
    }
     // TODO: fix QCPD
     //sample.qcPassedDetailId = Hot.getIdFromAlias(obj.qcPassedDetailAlias, Hot.sampleOptions.qcPassedDetailsDtos);
    if (obj.volume) {
      sample.volume = obj.volume;
    }
    if (obj.concentration) {
      sample.concentration = obj.concentration;
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
      if (Hot.hotTable.getCellMeta(0, j).prop == "sampleClassAlias") {
        col = j;
        break;
      }
    }
    for (var i = 0; i < sampleData.length; i++) {
      var parentClassId = Hot.getIdFromAlias(sampleData[i].parentSampleClassAlias, Hot.sampleOptions.sampleClassesDtos);
      var childClassId = Hot.getIdFromAlias(sampleData[i].sampleClassAlias, Hot.sampleOptions.sampleClassesDtos);
      var validRelationship = Sample.hot.findMatchingRelationship(parentClassId, childClassId);
      if (validRelationship.length === 0) {
        Hot.messages.failed.push("Row " + i + ": " + Sample.hot.makeErrorMessageForInvalidRelationship(parentClassId, childClassId));
        Hot.failedComplexValidation.push([i, col]);
      }
    }
    return (Hot.messages.failed.length ? false : true);
  }
};
