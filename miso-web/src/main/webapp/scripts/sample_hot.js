/**
 * Sample-specific Handsontable code
 */

Sample.hot = {
  projectsArray: null,
  sampleClassId: null,
  sciName: null,
  sampleData: null,
  identityRequestCounter: 0,
  selectedProjectId: null,

  /**
   * Additional sample-specific processing.
   */
  processSampleOptionsFurther: function () {
    Sample.hot.addInstituteAliasToLab();
    if (Hot.detailedSample) {
      Sample.hot.sciName = 'Homo sapiens';
    }
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

      // add detailedSample values
      if (sam.type != 'Plain') {
        // add attributes if it's a first receipt
        sam.sampleClassAlias = Hot.getAliasFromId(sam.sampleClassId, Hot.sampleOptions.sampleClassesDtos);
        if (sam.parentTissueSampleClassId) sam.parentTissueSampleClassAlias = Hot.getAliasFromId(sam.parentTissueSampleClassId, Hot.sampleOptions.sampleClassesDtos);
        if (sam.tissueOriginId) sam.tissueOriginLabel = Hot.sampleOptions.tissueOriginsDtos.filter(function (tod) { return tod.id == sam.tissueOriginId; })[0].label;
        if (sam.tissueTypeId) sam.tissueTypeLabel = Hot.sampleOptions.tissueTypesDtos.filter(function (ttd) { return ttd.id == sam.tissueTypeId; })[0].label;
        if (sam.labId) sam.labComposite = Sample.hot.getLabCompositeFromId(sam.labId, Hot.sampleOptions.labsDtos);
        if (sam.prepKitId) sam.prepKitAlias = Hot.getAliasFromId(sam.prepKitId, Hot.sampleOptions.kitDescriptorsDtos);
        if (sam.subprojectId) sam.subprojectAlias = Hot.getAliasFromId(sam.subprojectId, Hot.sampleOptions.subprojectsDtos);
        if (sam.detailedQcStatusId) {
          sam.detailedQcStatusDescription = Hot.maybeGetProperty(Hot.findFirstOrNull(Hot.idPredicate(sam.detailedQcStatusId), Hot.sampleOptions.detailedQcStatusesDtos), 'description');
        } else {
          sam.detailedQcStatusDescription = 'Not Ready';
        }
        // add sampleAnalyte values, if applicable
        if (Sample.hot.getCategoryFromClassId(sam.sampleClassId) == 'Tissue') {
          if (sam.tissueMaterialId) {
            sam.tissueMaterialAlias = Hot.getAliasFromId(sam.tissueMaterialId, Hot.sampleOptions.tissueMaterialsDtos);
          }
  		    sam.tissueOriginLabel = Hot.sampleOptions.tissueOriginsDtos.filter(function (tod) { return tod.id == sam.tissueOriginId; })[0].label;
  		    sam.tissueTypeLabel = Hot.sampleOptions.tissueTypesDtos.filter(function (ttd) { return ttd.id == sam.tissueTypeId; })[0].label;
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
      var newSam = Sample.hot.getDefaultDetailedValues();
      newSam.sampleType = sam.sampleType;
      newSam.projectId = sam.projectId;
      newSam.scientificName = sam.scientificName;
      newSam.parentTissueSampleClassId = sam.sampleClassId;
      newSam.parentTissueSampleClassAlias = Hot.getAliasFromId(newSam.parentTissueSampleClassId, Hot.sampleOptions.sampleClassesDtos);
      newSam.sampleClassId = Sample.hot.sampleClassId;
      newSam.sampleClassAlias = Hot.getAliasFromId(newSam.sampleClassId, Hot.sampleOptions.sampleClassesDtos);
      newSam.parentId = parseInt(sam.id);
      newSam.parentAlias = clone(sam.alias);
      newSam.tissueOriginId = sam.tissueOriginId;
      newSam.tissueOriginLabel = sam.tissueOriginLabel;
      newSam.tissueTypeId = sam.tissueTypeId;
      newSam.tissueTypeLabel = sam.tissueTypeLabel;
      newSam.timesReceived = sam.timesReceived;
      newSam.tubeNumber = sam.tubeNumber;
      newSam.passageNumber = sam.passageNumber || null;
      newSam.externalInstituteIdentifier = sam.externalInstituteIdentifier;
      newSam.labId = sam.labId;
      newSam.nonStandardAlias = sam.nonStandardAlias;
      if (sam.groupId && sam.groupDescription && sam.groupDescription.length) {
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
    Sample.hot.projectsArray = Hot.sortByProperty(Hot.dropdownRef.projects, 'alias');
    var select = [];
    select.push('<option value="">Select project</option>');
    for (var i=0; i<Sample.hot.projectsArray.length; i++) {
      select.push('<option value="'+ Sample.hot.projectsArray[i].id +'"');
      select.push(Sample.hot.projectsArray[i].id == Sample.hot.selectedProjectId ? ' selected' : '');
      select.push('>'+ Sample.hot.projectsArray[i].shortname +' ('+ Sample.hot.projectsArray[i].name +')</option>');
    }
    document.getElementById('projectSelect').insertAdjacentHTML('beforeend', select.join(''));
    document.getElementById('projectSelect').addEventListener('change', Sample.hot.updateSelectedProjectId);

    // if detailedSample is selected, add subproject dropdown
    if (Hot.detailedSample && Sample.hot.selectedProjectId) {
      Sample.hot.addSubprojectSelect();
      document.getElementById('projectSelect').addEventListener('change', Sample.hot.addSubprojectSelect);
    }
  },
  
  updateSelectedProjectId: function () {
    Sample.hot.selectedProjectId = jQuery('#projectSelect').val();
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
    var filteredSubprojects = Hot.sortByProperty(Sample.hot.filterSubprojectsByProjectId(projectId), 'alias');
    // display nothing if project has no subprojects
    if (filteredSubprojects.length === 0) {
      document.getElementById('subpSelectOptions').style.display = 'none';
    }  else {
      var select = [];
      select.push('<label>Subproject: ');
      select.push('<select id="subprojectSelect">');
      select.push('<option value="">None</option>');
      for (var i=0; i<filteredSubprojects.length; i++) {
        select.push('<option value="'+ filteredSubprojects[i].id +'">'+ filteredSubprojects[i].alias +'</option>');
      }
      select.push('</select></label>');
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

  compareSampleClass: function(a, b) {
    var categories = ['Identity', 'Tissue', 'Tissue Processing', 'Stock', 'Aliquot'];
    return (categories.indexOf(a.sampleCategory) - categories.indexOf(b.sampleCategory)) || a.alias.localeCompare(b.alias);
  },

  /**
   * Creates dropdown for sample classes
   */
  addClassSelect: function () {
    var select = [];
    var classes = Hot.sortByProperty(Hot.sampleOptions.sampleClassesDtos, 'id');
    select.push('<label>Sample Class: <select id="classDropdown">');
    select.push('<option value="">Select class</option>');
    var classOptions = Sample.hot.getNewSampleClassOptions();
    for (var i=0; i<classOptions.length; i++) {
       select.push('<option value="'+ classOptions[i].id +'">'+ classOptions[i].alias +'</option>');
    }
    select.push('</select></label>');
    document.getElementById('classOptions').innerHTML = select.join('');
  },

  /**
   * Returns the SampleClasses which may be created without an existing parent
   */
  getNewSampleClassOptions: function () {
    var classes = Hot.sampleOptions.sampleClassesDtos.sort(Sample.hot.compareSampleClass);
    return classes.filter(function(sc) { return sc.canCreateNew; });
  },

  /**
   * Returns the alias of each SampleClass which may be created without an existing parent
   */
  getNewSampleClassOptionsAliasOnly: function () {
    return Sample.hot.getNewSampleClassOptions().map(function(sc) { return sc.alias; })
  },

  getTissueClassesAliasOnly: function () {
    var classes = Hot.sampleOptions.sampleClassesDtos.sort(Sample.hot.compareSampleClass);
    var options = [];
    for (var i=0; i<classes.length; i++) {
      if (classes[i].sampleCategory === 'Tissue') {
        options.push(classes[i].alias);
      }
    }
    return options;
  },

  /**
   * Checks to see if table exists before creating a new one.
   */
  makeNewSamplesTable: function () {
    // if this is disabled, alert the user as to why
    if (document.getElementById('projectSelect').value === '') {
      alert('Please select a project.');
      document.getElementById('projectSelect').focus();
      return false;
    }
    if (Hot.detailedSample && document.getElementById('classDropdown').value === '') {
      alert('Please select a sample class.');
      document.getElementById('classDropdown').focus();
      return false;
    }
    var numSamples = parseInt(document.getElementById('numSamples').value);
    if (!numSamples > 0) {
      alert('Please input number of samples to create.');
      document.getElementById('numSamples').focus();
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
      Sample.hot.sampleCategory = Sample.hot.getCategoryFromClassId(Sample.hot.sampleClassId);
      Sample.hot.dataSchema.sampleClassAlias = Hot.getAliasFromId(Sample.hot.sampleClassId, Hot.sampleOptions.sampleClassesDtos);
    }

    // make the table
    var sampleCategory = null;
    if (Hot.detailedSample) {
      sampleCategory = Sample.hot.getCategoryFromClassId(document.getElementById('classDropdown').value);
      Sample.hot.dataSchema.scientificName = "Homo sapiens";
    }
    Sample.hot.makeHOT(null, 'create', null, sampleCategory);
    if (document.getElementById('lookupIdentities') && document.getElementById('lookupIdentities').hasAttribute('disabled')) {
      document.getElementById('lookupIdentities').removeAttribute('disabled');
    }
  },

  /**
   * Makes create/edit samples table
   */
  makeHOT: function (startingValues, action, sourceSampleCategory, targetSampleCategory) {
    // assign functions which will be required during save
    Hot.buildDtoFunc = Sample.hot.buildDtos;
    Hot.saveOneFunc = Sample.hot.saveOne;
    Hot.updateOneFunc = Sample.hot.updateOne;
    Hot.afterAllSucceed = function () { Hot.addBulkMenu('saveSamples', Sample.hot.getBulkActions); };
    if (targetSampleCategory) Sample.hot.sampleCategory = targetSampleCategory;
    
    // reset params which track successes and errors
    Hot.messages = {
      success: {},
      failed: {}
    };
    document.getElementById('saveSuccesses').classList.add('hidden');
    document.getElementById('saveErrors').classList.add('hidden');
    
    
    // are new samples parented to IDs being requested
    Sample.hot.generateColumnData = function(showQcs) { return Sample.hot.getAppropriateColumns(action, sourceSampleCategory, targetSampleCategory, showQcs); };
    Hot.colConf = Sample.hot.generateColumnData(false);

    if (!startingValues) {
      // set initial number of rows to display.
      var startRowsNumber = parseInt(document.getElementById('numSamples').value);

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
      dataSchema: Sample.hot.dataSchema,
      maxRows: startRowsNumber || startingValues.length,
      beforeAutofill: Hot.incrementingAutofill,
    });
    document.getElementById('hotContainer').style.display = '';
    
    // add alias validator for standard aliases
    var aliasColIndex = Hot.getColIndex('alias');
    var parentAliasColIndex = Hot.getColIndex('parentAlias');
    Hot.startData.forEach(function (sample, index) {
      if (sample.nonStandardAlias) {
        Hot.hotTable.setCellMeta(index, aliasColIndex, 'renderer', Hot.nsAliasRenderer);
        Hot.hotTable.setCellMeta(index, aliasColIndex, 'validator', Hot.requiredText);
        jQuery('#nonStandardAliasNote').show();
        if (parentAliasColIndex) {
          Hot.hotTable.setCellMeta(index, parentAliasColIndex, 'renderer', Hot.nsAliasReadOnlyRenderer);
        }
      } else {
        Hot.hotTable.setCellMeta(index, aliasColIndex, 'validator', Sample.hot.validateAlias);
      }
    });
    Hot.hotTable.render();

    // enable save button if it was disabled
    if (Hot.saveButton.classList.contains('disabled')) Hot.toggleButtonAndLoaderImage(Hot.saveButton);
    
    Sample.hot.addDetailedQcHooks();
  },
  
  /**
   * This hook applies changes to the QC Note column based on changes to the QC Status column (if both exist)
   */
  addDetailedQcHooks: function () {
    Hot.hotTable.addHook('afterChange', function (changes, source) {
      // 'changes' is a variable-length array of arrays. Each inner array has the following structure:
      // [rowIndex, colName, oldValue, newValue]
      if (['edit', 'autofill', 'paste'].indexOf(source) != -1) {
        for (var i = 0; i < changes.length; i++) {
          // trigger only if old value is different from new value
          if (changes[i][2] == changes[i][3]) {
            continue;
          }
          switch (changes[i][1]) {
            case 'detailedQcStatusDescription':
              var qcpd = Hot.findFirstOrNull(Hot.descriptionPredicate(changes[i][3]), Hot.sampleOptions.detailedQcStatusesDtos);
              var row = changes[i][0];
              Hot.startData[row].detailedQcStatusNote = '';
              Sample.hot.dqcsNoteIndex = Hot.getColIndex('detailedQcStatusNote');
              if (qcpd === null || !qcpd.noteRequired) {
                Hot.hotTable.setCellMeta(row, Sample.hot.dqcsNoteIndex, 'readOnly', true);
                Hot.hotTable.setCellMeta(row, Sample.hot.dqcsNoteIndex, 'validator', Hot.permitEmpty);
                Hot.hotTable.setCellMeta(row, Sample.hot.dqcsNoteIndex, 'renderer', Hot.alwaysValidRenderer);
              } else {
                Hot.hotTable.setCellMeta(row, Sample.hot.dqcsNoteIndex, 'readOnly', false);
                Hot.hotTable.setCellMeta(row, Sample.hot.dqcsNoteIndex, 'validator', Hot.requiredText);
                Hot.hotTable.setCellMeta(row, Sample.hot.dqcsNoteIndex, 'renderer', Hot.requiredTextRenderer);
              }
          }
        }
        Hot.hotTable.render();
      }
    });
  },

  /**
   * Redraws the samples table to include QC columns
   */
  regenerateWithQcs: function () {
    Hot.colConf = Sample.hot.generateColumnData(true);

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
    alias: '',
    qcPassed: '',
    volume: null,
    identityAlias: null,
    externalName: null,
    donorSex: null,
    sampleClassId: null,
    sampleClassAlias: null,
    tissueOriginId: null,
    tissueOriginLabel: null,
    tissueTypeId: null,
    tissueTypeLabel: null,
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
    detailedQcStatusId: null,
    detailedQcStatusDescription: null,
    detailedQcStatusNote: '',
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
    slides: null,
    discards: null,
    thickness: null,
    slidesConsumed: null
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
    var sampleCategory = Sample.hot.getCategoryFromClassId(Sample.hot.sampleClassId);
    return {
      'sampleClassAlias': sampleClassAlias,
      'parentTissueSampleClassId': rootSampleClassId,
      'strStatus': sampleCategory === 'Stock' ? 'Not Submitted' : null,
      'scientificName': Sample.hot.sciName,
      'detailedQcStatusDescription': 'Not Ready'
    };
  },

  /**
   * Gets array of subproject aliases (detailed sample only)
   */
  getSubprojects: function() {
    return Hot.sortByProperty(Hot.sampleOptions.subprojectsDtos, 'alias').map(Hot.getAlias);
  },

  /**
   * Gets array of sample type aliases (detailed sample only)
   */
  getSampleTypes: function () {
    return Hot.dropdownRef['sampleTypes'];
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
    return Hot.sortByProperty(Hot.sampleOptions.tissueOriginsDtos, 'alias').map(function (to) { return to.label; });
  },

  /**
   * Gets array of tissue type aliases (detailed sample only)
   */
  getTissueTypes: function () {
    return Hot.sortByProperty(Hot.sampleOptions.tissueTypesDtos, 'alias').reverse().map(function (to) { return to.label; });
  },

  /**
   * Gets array of tissue material aliases (detailed sample only)
   */
  getTissueMaterials: function () {
    return Hot.sortByProperty(Hot.sampleOptions.tissueMaterialsDtos, 'alias').map(Hot.getAlias);
  },

  /**
   * Gets array of sample class aliases (detailed sample only)
   */
  getSampleClasses: function () {
    return Hot.sampleOptions.sampleClassesDtos.sort(Sample.hot.compareSampleClass).map(Hot.getAlias);
  },
  
  getSampleClassesByCategory: function () {
    return (Hot.sampleOptions.sampleClassesDtos.sort(Sample.hot.compareSampleClass)
               .filter(function (sc) { return sc.sampleCategory == Sample.hot.sampleCategory; })
               .map(Hot.getAlias));
  },

  /**
   * Gets array of sample class aliases that are a valid child of the given parent (detailed sample only)
   */
  getValidClassesForParent: function (parentScId) {
    var parentTissueSampleClassId = parentScId;
    return Hot.sortByProperty(Hot.sampleOptions.sampleValidRelationshipsDtos, 'alias')
               .filter(function (rel) { return rel.parentId == parentTissueSampleClassId; })
               .map(function (rel) { return Hot.getAliasFromId(rel.childId, Hot.sampleOptions.sampleClassesDtos); });
  },

  /**
   * Gets array of sample purpose aliases (detailed sample only)
   */
  getSamplePurposes: function () {
    return Hot.sortByProperty(Hot.sampleOptions.samplePurposesDtos, 'alias').map(Hot.getAlias);
  },

  /**
   * Gets array of detailed QC status descriptions (detailed sample only)
   */
  getDetailedQcStatuses: function () {
    var statuses = Hot.sortByProperty(Hot.sampleOptions.detailedQcStatusesDtos, 'id').map(function (dqcs) { return dqcs.description; });
    statuses.unshift("Not Ready");
    return statuses;
  },

  /**
   * Gets array of lab custom aliases (with institute alias) (detailed sample only)
   */
  getLabs: function () {
    return Hot.sortByProperty(Hot.sampleOptions.labsDtos,'alias').map(function (lab) { return lab.alias +' - '+ lab.instituteAlias; });
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
   * Returns a list of the columns will be displayed for the current situation
   * params: string action: one of create, update, or propagate
   *         string sourceSampleCategory: if propagating, the sample category of the parent, null otherwise
   *         string targetSampleCategory: the sample category of the sample being created or modified, null if plain
   *         Boolean showQcs: show quality control columns
   */
  getAppropriateColumns: function (action, sourceSampleCategory, targetSampleCategory, showQcs) {
    var isDetailed = targetSampleCategory != null;
    var sampleClass = Hot.detailedSample ? Hot.getObjById(Sample.hot.sampleClassId, Hot.sampleOptions.sampleClassesDtos) : null;
    var sampleClassAlias = sampleClass ? sampleClass.alias : null;
	  // We assume we have a linear progression of information that must be
	  // collected as a sample progressed through the hierarchy.
    var progression = ['Identity', 'Tissue', 'Tissue Processing', 'Stock', 'Aliquot'];
    // First, set all the groups of detailed columns we will show to off.
    var show = {};
    for (var i = 0; i <= progression.length; i++) {
      show[progression[i]] = false;
    }
    // Determine the indices of the first and less steps in the progression.
    var endProgression = targetSampleCategory == null ? -1 : progression.indexOf(targetSampleCategory);
    var startProgression;
    if (!isDetailed) {
      startProgression = -1;
    } else if (action == 'create') {
      startProgression = 0;
    } else if (action == 'update') {
      startProgression = endProgression;
    } else {
      // Start at the category *after* our source type.
      startProgression = progression.indexOf(sourceSampleCategory) + 1;
    }
    // Now, mark all the appropriate column groups active
    for (i = startProgression; i <= endProgression && i != -1; i++) {
      show[progression[i]] = true;
    }
  	// If we aren't starting or finished with a tissue processing, hide those
  	// columns since we don't really want to show tissue processing unless the
  	// user specifically requested it.
  	if (sourceSampleCategory != 'Tissue Processing' && targetSampleCategory != 'Tissue Processing') {
      show['Tissue Processing'] = false;
    }

    return [
      // Basic columns
      {
        header: 'Sample Alias',
        data: 'alias',
        include: true
      },
      {
        header: 'Description',
        data: 'description',
        include: true
      },
      {
        header: 'Date of receipt',
        data: 'receivedDate',
        type: 'date',
        dateFormat: 'YYYY-MM-DD',
        datePickerConfig: {
          firstDay: 0,
          numberOfMonths: 1
        },
        allowEmpty: true,
        extraneous: true,
        include: true
      },
      {
        header: 'Matrix Barcode',
        data: 'identificationBarcode',
        include: !Hot.autoGenerateIdBarcodes
      },
      {
        header: 'Sample Type',
        data: 'sampleType',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getSampleTypes(),
        validator: validateSampleTypes,
        renderer: Hot.requiredAutocompleteRenderer,
        extraneous: true,
        include: true
      },
      {
        header: 'Sci. Name',
        data: 'scientificName',
        source: Sample.hot.sciName,
        validator: Hot.requiredText,
        renderer: Hot.requiredTextRenderer,
        extraneous: true,
        include: true
      },

      // Parent columns
      {
        header: 'Parent Alias',
        data: 'parentAlias',
        readOnly: true,
        include: isDetailed && action == 'propagate'
      },
      {
        header: 'Parent Sample Class',
        data: 'parentTissueSampleClassAlias',
        readOnly: true,
        include: isDetailed && action == 'propagate'
      },

      // Identity columns
      {
        header: 'Identity Alias',
        data: 'identityAlias',
        type: 'dropdown',
        trimDropdown: false,
        strict: true,
        allowInvalid: true,
        include: show['Identity']
      },
      {
        header: 'External Name',
        data: 'externalName',
        validator: Hot.noSpecialChars,
        renderer: Hot.requiredTextRenderer,
        include: show['Identity']
      },
      {
        header: 'Sex',
        data: 'donorSex',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getDonorSexes(),
        validator: Hot.permitEmpty,
        include: show['Identity']
      },

      // Detailed columns
      {
        header: 'Sample Class',
        data: 'sampleClassAlias',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getSampleClassesByCategory(),
        renderer: Hot.requiredAutocompleteRenderer,
        readOnly: action == 'update',
        include: isDetailed
      },
      {
        header: 'Group ID',
        data: 'groupId',
        validator: validateAlphanumeric,
        include: isDetailed
      },
      {
        header: 'Group Desc.',
        data: 'groupDescription',
        validator: Hot.permitEmpty,
        include: isDetailed
      },

      // Tissue columns
      {
        header: 'Tissue Class',
        data: 'parentTissueSampleClassAlias',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getTissueClassesAliasOnly(),
        validator: validateTissueClasses,
        renderer: Hot.requiredAutocompleteRenderer,
        include: show['Tissue'] && targetSampleCategory != 'Tissue'
      },
      {
        header: 'Tissue Origin',
        data: 'tissueOriginLabel',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getTissueOrigins(),
        validator: validateTissueOrigins,
        renderer: Hot.requiredAutocompleteRenderer,
        include: show['Tissue']
      },
      {
        header: 'Tissue Type',
        data: 'tissueTypeLabel',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getTissueTypes(),
        validator: validateTissueTypes,
        renderer: Hot.requiredAutocompleteRenderer,
        include: show['Tissue']
      },
      {
        header: 'Passage #',
        data: 'passageNumber',
        validator: validateNumber,
        include: show['Tissue']
      },
      {
        header: 'Times Received',
        data: 'timesReceived',
        validator: validatePosReqdNumber,
        renderer: Hot.requiredNumericRenderer,
        include: show['Tissue']
      },
      {
        header: 'Tube Number',
        data: 'tubeNumber',
        validator: validatePosReqdNumber,
        renderer: Hot.requiredNumericRenderer,
        include: show['Tissue']
      },
      {
        header: 'Lab',
        data: 'labComposite',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getLabs(),
        validator: Hot.permitEmpty,
        include: show['Tissue']
      },
      {
        header: 'Ext. Inst. Identifier',
        data: 'externalInstituteIdentifier',
        include: show['Tissue']
      },
      {
        header: 'Material',
        data: 'tissueMaterialAlias',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getTissueMaterials(),
        validator: Hot.permitEmpty,
        include: show['Tissue']
      },
      {
        header: 'Region',
        data: 'region',
        validator: Hot.permitEmpty,
        include: show['Tissue']
      },

      // Tissue Processing: CV Slides columns
      {
        header: 'Slides',
        data: 'slides',
        validator: validatePosReqdNumber,
        renderer: Hot.requiredNumericRenderer,
        include: show['Tissue Processing'] && sampleClassAlias == 'CV Slide'
      },
      {
        header: 'Discards',
        data: 'discards',
        type: 'numeric',
        include: show['Tissue Processing'] && sampleClassAlias == 'CV Slide'
      },
      {
        header: 'Thickness',
        data: 'thickness',
        type: 'numeric',
        include: show['Tissue Processing'] && sampleClassAlias == 'CV Slide'
      },

      // Tissue Processing: LCM Tube columns
      {
        header: 'Slides Consumed',
        data: 'slidesConsumed',
        validator: validatePosReqdNumber,
        renderer: Hot.requiredNumericRenderer,
        include: show['Tissue Processing'] && sampleClassAlias == 'LCM Tube'
      },

      // Stock columns
      {
        header: 'STR Status',
        data: 'strStatus',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getStrStatuses(),
        include: show['Stock']
      },
      {
        header: 'DNAse',
        data: 'dnaseTreated',
        type: 'dropdown',
        trimDropdown: false,
        source: [ 'true', 'false' ],
        include: show['Stock'] && sampleClass.dnaseTreatable
      },
      
      // Aliquot columns
      {
        header: 'Kit',
        data: 'prepKitAlias',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getKitDescriptors(),
        validator: Hot.permitEmpty,
        include: show['Aliquot']
      },

      // QC columns
      {
        header: 'Vol. (&#181;l)',
        data: 'volume',
        type: 'numeric',
        format: '0.00',
        include: showQcs || show['Stock']
      },
      {
        header: 'Conc. (ng/&#181;l)',
        data: 'concentration',
        type: 'numeric',
        format: '0.00',
        include: show['Stock']
      },
      {
        header: 'QC Passed?',
        data: 'qcPassed',
        type: 'dropdown',
        trimDropdown: false,
        source: ['unknown', 'true', 'false'],
        include: !isDetailed
      },
      {
        header: 'QC Status',
        data: 'detailedQcStatusDescription',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getDetailedQcStatuses(),
        include: isDetailed
      },
      {
        header: 'QC Note',
        data: 'detailedQcStatusNote',
        readOnly: true,
        validator: Hot.permitEmpty,
        include: isDetailed
      },


      // Aliquot columns
      {
        header: 'Purpose',
        data: 'samplePurposeAlias',
        type: 'dropdown',
        trimDropdown: false,
        source: Sample.hot.getSamplePurposes(),
        validator: Hot.permitEmpty,
        include: show['Aliquot']
      }
    ].filter(function(x) { return x.include; });

    function validateSampleTypes (value, callback) {
      return callback(Sample.hot.getSampleTypes().indexOf(value) != -1);
    }

    function validateTissueClasses (value, callback) {
      return callback(Sample.hot.getTissueClassesAliasOnly().indexOf(value) != -1);
    }

    function validateTissueOrigins (value, callback) {
      return callback(Sample.hot.getTissueOrigins().indexOf(value) != -1);
    }

    function validateTissueTypes (value, callback) {
      return callback(Sample.hot.getTissueTypes().indexOf(value) != -1);
    }

    function validateNumber (value, callback) {
      return callback(value === '' || value === null || Handsontable.helper.isNumeric(value) && value >= 0);
    }

    function validatePosReqdNumber (value, callback) {
      return callback(Handsontable.helper.isNumeric(value) && value >= 0);
    }

    function validateAlphanumeric (value, callback) {
      var alphanumRegex = /^[-\w]+$/;
      return callback(value === '' || value === null || alphanumRegex.test(value));
    }
  },
  
  validateAlias: function (value, callback) {
    if (value) {
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
  },

  /**
   * Gets the sampleCategory associated with a given sampleClass
   */
  getCategoryFromClassId: function (sampleClassId) {
    var sampleClasses = (Hot.sampleOptions.sampleClassesDtos || Sample.sampleClasses).filter(function (sampleClass) {
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
      return sampleClass.alias == 'Identity';
    })[0].id;
  },
 
 /**
   * Creates the SampleDtos to pass to the server
   */
  buildDtos: function (obj) {
    var sample = {};

    // wrap this in try/catch because this callback doesn't trigger error logging
    try {
      if (obj.id) {
        sample.id = obj.id;
        sample.name = obj.name;
        sample.alias = obj.alias;
      }

      // add SampleDto attributes
      sample.description = obj.description || '';
      sample.identificationBarcode = obj.identificationBarcode;
      sample.sampleType = obj.sampleType;
      sample.qcPassed = (obj.qcPassed && obj.qcPassed != 'unknown' ? obj.qcPassed : '') || '';
      sample.alias = obj.alias || '';
      sample.projectId = (parseInt(obj.projectId) || parseInt(document.getElementById('projectSelect').value));
      sample.scientificName = obj.scientificName;
      if (obj.receivedDate && obj.receivedDate.length) {
        sample.receivedDate = obj.receivedDate;
      }

      // if it's a plain sample, return now.
      if (!Hot.detailedSample) {
        sample.type = 'Plain';
        return sample;
      }

      sample.rootSampleClassId = Sample.hot.getRootSampleClassId();
      sample.nonStandardAlias = obj.nonStandardAlias;

      // add sample parent attributes, and all other attributes for the first receipt of a sample
      if (obj.externalName) {
        if (obj.sampleClassAlias != 'Identity') {
          sample.identityId = Sample.hot.getParentIdFromIdentityLabel(obj.identityAlias);
        }
        sample.externalName = obj.externalName;
        if (obj.donorSex && obj.donorSex.length) sample.donorSex = obj.donorSex;
      }

      // if the table data couldn't have changed (no sample class (sc) alias value) then use the original sc id;
      // otherwise, generate sc id from sc alias (rather than calculating for each field whether the original sc id corresponds to the current sc alias
      if (obj.sampleClassId && !obj.sampleClassAlias) {
        sample.sampleClassId = obj.sampleClassId;
      } else {
        sample.sampleClassId = Hot.getIdFromAlias(obj.sampleClassAlias, Hot.sampleOptions.sampleClassesDtos);
      }
      sample.type = Sample.hot.getCategoryFromClassId(sample.sampleClassId);

      // if it's an identity, return now.
      if (obj.sampleClassAlias == 'Identity') return sample;

      if (obj.tissueOriginId && !obj.tissueOriginLabel) {
        sample.tissueOriginId = obj.tissueOriginId;
      } else if (obj.tissueOriginLabel) {
        sample.tissueOriginId = Hot.sampleOptions.tissueOriginsDtos.filter(function (tod) { return tod.label == obj.tissueOriginLabel; })[0].id;
      }
      if (obj.tissueTypeId && !obj.tissueTypeLabel) {
        sample.tissueTypeId = obj.tissueTypeId;
      } else if (obj.tissueTypeLabel) {
        sample.tissueTypeId = Hot.sampleOptions.tissueTypesDtos.filter(function (ttd) { return ttd.label == obj.tissueTypeLabel; })[0].id;
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
      if (obj.tissueMaterialAlias && obj.tissueMaterialAlias.length) {
        sample.tissueMaterialId = Hot.getIdFromAlias(obj.tissueMaterialAlias, Hot.sampleOptions.tissueMaterialsDtos);
      }
      
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
      } else if (obj.parentTissueSampleClassAlias) {
        sample.parentTissueSampleClassId = Hot.getIdFromAlias(obj.parentTissueSampleClassAlias, Hot.sampleOptions.sampleClassesDtos);
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
        sample.dnaseTreated = (obj.dnaseTreated == 'true');
        break;
      case 'Tissue Processing':
        if (obj.slides) {
          sample.slides = obj.slides;
          sample.type = 'CV Slide'; // add type info for deserialization
          sample.discards = (obj.discards ? obj.discards : 0);
          if (obj.thickness) sample.thickness = obj.thickness;
        }
        if (obj.slidesConsumed) {
          sample.slidesConsumed = obj.slidesConsumed;
          sample.type = 'LCM Tube'; // add type info for deserialization
        }
        break;
      }

      sample.detailedQcStatusId = Hot.maybeGetProperty(Hot.findFirstOrNull(Hot.descriptionPredicate(obj.detailedQcStatusDescription), Hot.sampleOptions.detailedQcStatusesDtos), 'id');
      sample.detailedQcStatusNote = obj.detailedQcStatusNote;

      if (obj.volume) {
        sample.volume = obj.volume;
      }
      if (obj.concentration) {
        sample.concentration = obj.concentration;
      }
    } catch (e) {
      console.log(e);
      return null;
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
  saveOne: function (data, rowIndex, numberToSave, callback) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        callback(); // Indicate request has completed.
        xhr.status === 201 ? Sample.hot.successSave(xhr, rowIndex, numberToSave) : Hot.failSave(xhr, rowIndex, numberToSave);
      }
    };
    xhr.open('POST', '/miso/rest/tree/sample');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(data);
  },

  /**
   * Puts a single sample to server and processes result
   */
  updateOne: function (data, sampleId, rowIndex, numberToSave, callback) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        callback(); // Indicate request has completed.
        xhr.status === 200 ? Sample.hot.successSave(xhr, rowIndex, numberToSave) : Hot.failSave(xhr, rowIndex, numberToSave);
      }
    };
    xhr.open('PUT', '/miso/rest/tree/sample/' + sampleId);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(data);
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
    if (Hot.startData[rowIndex].nonStandardAlias) Hot.hotTable.setCellMeta(rowIndex, 0, 'renderer', Hot.nsAliasReadOnlyRenderer);
    Hot.addSuccessesAndErrors();
  },

  /**
   * Checks if cells are all valid. If yes, POSTs samples that need to be saved. (detailed sample only)
   */
  saveData: function () {
    // check that a project and class have been declared
    if (document.getElementById('projectSelect').value === '') {
      Hot.messages.failed = ['Select a Project before saving.'];
      Hot.addErrors(Hot.messages);
      return false;
    }
    
    if (document.getElementById('lookupIdentities') && Sample.hot.identityRequestCounter == 0) {
      Hot.messages.failed = ['Click "Look up Identities" and select parent identities before saving.'];
      Hot.addErrors(Hot.messages);
      return false;
    }

    var continueValidation = Hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;

    Hot.hotTable.validateCells(function (isValid) {
      if (isValid) {
        Hot.saveTableData("alias", "Create");
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
        Hot.saveTableData("alias", "Edit");
      } else {
        Hot.validationFails();
        return false;
      }
    });
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
        Hot.saveTableData("alias", "Create");
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
    var findRecursive = function(child) {
      return Hot.sampleOptions.sampleValidRelationshipsDtos.filter(function (relationship) {
        if (relationship.childId != child || relationship.childId == relationship.parentId) {
          return false;
        }
        if (relationship.parentId == parentClassId) {
          return true;
        }
        return findRecursive(relationship.parentId).length > 0;
      });
    };
    return findRecursive(childClassId);
  },

  /**
   * Gets parent sampleClass and child sampleClass and checks to see if parent and child have an associated valid relationship
   */
  assessValidRelationships: function (sampleData) {
    var col = Hot.getColIndex("sampleClassAlias");

    for (var i = 0; i < sampleData.length; i++) {
      var parentClassId = Hot.getIdFromAlias(sampleData[i].parentTissueSampleClassAlias, Hot.sampleOptions.sampleClassesDtos);
      var childClassId = Hot.getIdFromAlias(sampleData[i].sampleClassAlias, Hot.sampleOptions.sampleClassesDtos);
      var validRelationship = Sample.hot.findMatchingRelationship(parentClassId, childClassId);
      if (validRelationship.length === 0) {
        Hot.messages.failed.push("Row " + i + ": " + Sample.hot.makeErrorMessageForInvalidRelationship(parentClassId, childClassId));
        Hot.failedComplexValidation.push([i, col]);
      }
    }
    return (Hot.messages.failed.length ? false : true);
  },

  /**
   * Checks whether the provided sample class alias 
   */
  canSampleClassPropagateLibraries: function (sampleClassAlias) {
    var libraryable = Hot.sampleOptions.sampleClassesDtos.filter(function (sc) { return sc.sampleCategory == 'Aliquot' && sc.alias != 'whole RNA (aliquot)'; });
    var canPropagate = libraryable.filter(function (sc) { return sc.alias == sampleClassAlias; });
    return canPropagate.length == 1;
  },
  
  /**
   * Adds buttons: "Update Samples", "Propagate Samples", "Propagate Libraries" (if appropriate)
   */
  getBulkActions: function () {
    var actions = [];
    actions.push('<a onclick="Sample.hot.bulkUpdate();" href="javascript:void(0);">Update selected</a>');
    if (Hot.detailedSample) {
      Sample.sampleClasses = Hot.sampleOptions.sampleClassesDtos;
      Sample.validRelationships = Hot.sampleOptions.sampleValidRelationshipsDtos;
      var uniqueParentScAliases = Sample.ui.getUniqueValues(
          Hot.startData.map(function (sam) { return sam.sampleClassAlias; })
      );
      var uniqueParentSCs = Sample.sampleClasses.filter(function (sc) { return uniqueParentScAliases.indexOf(sc.alias) != -1; });
      var validChildren = Sample.ui.getChildSampleClasses(uniqueParentSCs);
      if (validChildren.length > 0) {
        for (var i = 0; i < validChildren.length; i++) {
          actions.push('<a onclick="Sample.hot.propagateSamples(' + validChildren[i].id + ')" href="javascript:void(0);">Propagate ' + validChildren[i].alias + '</a>');
        }
      }
    }
    if (!Hot.detailedSample || Sample.hot.canSampleClassPropagateLibraries(Hot.startData[0].sampleClassAlias)) {
      actions.push('<a onclick="Sample.hot.propagateLibraries();" href="javascript:void(0);">Propagate libraries</a>');
    }
    return actions.join('');
},

  /**
   * Takes samples from current table and redirects to page for updating them
   */
  bulkUpdate: function () {
    var sampleIds = Hot.startData.map(function (s) { return s.id; });
    if (sampleIds.indexOf(undefined) != -1) {
      alert("Samples must all be saved before updating.");
      return;
    } else {
      window.location = window.location.origin + "/miso/sample/bulk/edit/" + sampleIds.join(',');
    }
  },
  
  /**
   * Takes samples from current table and redirects to page for creating libraries parented to these samples
   */
  propagateLibraries: function () {
    var sampleIds = Hot.startData.map(function (s) { return s.id; });
    if (sampleIds.indexOf(undefined) != -1) {
      alert("Samples must all be saved before propagating libraries.");
      return;
    } else {
      window.location = window.location.origin + "/miso/library/bulk/propagate/" + sampleIds.join(',');
    }
  },
  
  /**
   * Takes samples from current table and redirects to page for creating child samples of the parameter sampleClass parented to these samples
   */
  propagateSamples: function (childClassId) {
    var sampleIds = Hot.startData.map(function (s) { return s.id; });
    if (sampleIds.indexOf(undefined) != -1) {
      alert("Samples must all be saved before propagating new samples.");
      return;
    } else {
      window.location = window.location.origin + "/miso/sample/bulk/create/" + sampleIds.join(',') + "&scid=" + childClassId;
    }
  },
  

  /**
   * Take the data in the external names column and send it to the server to find matching identities
   */
  lookupIdentities: function () {
    var lookupButton = document.getElementById('lookupIdentities');
    lookupButton.setAttribute('disabled', 'disabled');
    var identitiesSearches = [];
    var blankRows = []
    var externalName;
    for (var i = 0; i < Hot.startData.length; i++) {
      externalName = Hot.startData[i].externalName;
      if (externalName == null || externalName == undefined || externalName.length == 0) {
        blankRows.push(i + 1);
      } else {
        externalName = externalName.replace(",", ";");
        identitiesSearches.push(externalName);
      }
    }
    if (blankRows.length || !Hot.startData.length) {
      var msg = "Rows " + blankRows.join(", ") + ": External name can not be blank";
      Hot.addErrors({ failed: [msg] });
      lookupButton.removeAttribute('disabled');
      return false;
    } else {
      Sample.hot.identityRequestCounter++;
      jQuery.ajax({
        url:"/miso/rest/tree/identities",
        data: "{\"identitiesSearches\":" + JSON.stringify(identitiesSearches) 
                 + ", \"requestCounter\":" + Sample.hot.identityRequestCounter + "}", 
        contentType:'application/json; charset=utf8',
        dataType: 'json',
        type: 'POST'
      }).complete(function (data) {
        console.log(data);
        lookupButton.removeAttribute('disabled');
      }).success(function (data) {
        // make sure there haven't been any newer requests sent while we were fetching data
        if (data.requestCounter == Sample.hot.identityRequestCounter) {
          Sample.hot.foundIdentities = data.identitiesResults;
          Sample.hot.flattenedIdentities = [].concat.apply([], Sample.hot.foundIdentities);
          Sample.hot.setIdentitySources();
          Hot.hotTable.render();
          Sample.hot.displayCheckmark();
        }
      });
    }
  },
  
  /**
   * Processes returned AJAX data for identities and sets source for cells in table
   */
  setIdentitySources: function () {
    var identityColIndex = Hot.getColIndex('identityAlias');
    var rowCount = Sample.hot.foundIdentities.length;
    var selectedProjectId = Sample.hot.selectedProjectId;
    for (var i = 0; i < rowCount; i++) {
      if (Hot.startData[i].id) continue;
      var sortedIdentities = Sample.hot.foundIdentities[i].sort(function (a, b) {
        var aSortId = a.projectId == selectedProjectId ? 0 : a.projectId;
        var bSortId = b.projectId == selectedProjectId ? 0 : b.projectId;
        return aSortId - bSortId;
      });
      var hasIdentityInProject = (sortedIdentities.length > 0 && sortedIdentities[0].projectId == selectedProjectId);
      var identityItems = sortedIdentities.map(Sample.hot.getIdentityLabel);
      if (!hasIdentityInProject) {
        var projShortName = Hot.maybeGetProperty(Hot.findFirstOrNull(Hot.idPredicate(selectedProjectId), Hot.dropdownRef.projects), 'shortname');
        identityItems.unshift("First Receipt" + (projShortName ? " (" + projShortName + ")" : ""));
      }
      Hot.hotTable.setCellMeta(i, identityColIndex, 'source', identityItems);
      Hot.hotTable.setCellMeta(i, identityColIndex, 'renderer', Hot.requiredAutocompleteRenderer);
    }
  },
  
  /**
   * Custom identity label for Handsontable
   * Sample Alias -- External Name(s)
   */
  getIdentityLabel: function (obj) {
    if (obj.alias) {
      return obj.alias + " -- " + obj.externalName;
    } else {
      return "First receipt";
    }
  },
  
  identityLabelPredicate: function (identityLabel) {
    return function (item) {
      return item.alias +" -- "+ item.externalName == identityLabel;
    }
  },
  
  getParentIdFromIdentityLabel: function (identityLabel) {
    return Hot.maybeGetProperty(Hot.findFirstOrNull(Sample.hot.identityLabelPredicate(identityLabel), Sample.hot.flattenedIdentities), 'id');
  },
  
  displayCheckmark: function () {
    var checkmark = '<div><img id="checkmark" src="/styles/images/ok.png" style="float:left"/></div><div class="clear"></div>';
    jQuery('#tableProps').after(checkmark);
    jQuery('#checkmark').fadeOut("slow", function() {
      jQuery(this).remove();
    });
  }
};
