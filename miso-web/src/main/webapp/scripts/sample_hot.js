Sample.hot = {
  dropdownRef: null,
  detailedSample: null,
  projectsArray: null,
  selectedProjectId: null,
  sampleOptions: null,
  sampleClassId: null,
  sciName: null,
  colConf: null,
  hotTable: null,
  sampleData: null,
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
        xhr.status === 200 ? Sample.hot.processSampleOptionsXhr(xhr, callback) : console.log(xhr.response);
      }
    };
    xhr.open('GET', '/miso/rest/ui/sampleoptions');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
  },
  
  processSampleOptionsXhr: function (xhr, callback) {
    this.sampleOptions = JSON.parse(xhr.responseText);
    // process sampleOptions further
    this.addInstituteAliasToLab();
    // set default scientific name
    this.sciName = 'Homo sapiens';
    
    // execute callback if provided
    if (callback) callback();
    
    if (document.getElementById('projectSelect')) {
      this.addProjectEtcDropdowns();
    }
  },
  
  /*
   * modifies attributes of Sample Dtos (ex. adds the alias for each attribute ID) so Handsontable displays correctly
   */
  modifySamplesForEdit: function (samplesArray) {
    return samplesArray.map(function (sam) {
      
      // add sampleAdditionalInfo values
      if (sam.sampleAdditionalInfo) {
        sam.sampleAdditionalInfo.sampleClassAlias = Sample.hot.getAliasFromId(sam.sampleAdditionalInfo.sampleClassId, Sample.hot.sampleOptions.sampleClassesDtos);
        sam.sampleAdditionalInfo.parentSampleClassAlias = Sample.hot.getAliasFromId(sam.sampleAdditionalInfo.parentSampleClassId, Sample.hot.sampleOptions.sampleClassesDtos);
        sam.sampleAdditionalInfo.tissueOriginAlias = Sample.hot.getAliasFromId(sam.sampleAdditionalInfo.tissueOriginId, Sample.hot.sampleOptions.tissueOriginsDtos);
        sam.sampleAdditionalInfo.tissueTypeAlias = Sample.hot.getAliasFromId(sam.sampleAdditionalInfo.tissueTypeId, Sample.hot.sampleOptions.tissueTypesDtos);
        if (sam.sampleAdditionalInfo.prepKitId) {
          sam.sampleAdditionalInfo.prepKitAlias = Sample.hot.getAliasFromId(sam.sampleAdditionalInfo.prepKitId, Sample.hot.sampleOptions.kitDescriptorsDtos);
        }
        if (sam.sampleAdditionalInfo.subprojectId) {
          sam.sampleAdditionalInfo.subprojectAlias = Sample.hot.getAliasFromId(sam.sampleAdditionalInfo.subprojectId, Sample.hot.sampleOptions.subprojectsDtos);
        }
        if (sam.sampleAdditionalInfo.labId) {
          sam.sampleAdditionalInfo.labComposite = Sample.hot.getLabCompositeFromId(sam.sampleAdditionalInfo.labId, Sample.hot.sampleOptions.labsDtos);
        }
        
        // add sampleAnalyte values, if applicable
        if (Sample.hot.getCategoryFromClassId(sam.sampleAdditionalInfo.sampleClassId) == 'Analyte') {
          if (sam.sampleAnalyte.tissueMaterialId) {
            sam.sampleAnalyte.tissueMaterialAlias = Sample.hot.getAliasFromId(sam.sampleAnalyte.tissueMaterialId, Sample.hot.sampleOptions.tissueMaterialsDtos);
          }
          if (sam.sampleAnalyte.sampleGroupId) {
            sam.sampleAnalyte.sampleGroupComposite = Sample.hot.getSGCompositeFromId(sam.sampleAnalyte.sampleGroupId, Sample.hot.sampleOptions.sampleGroupsDtos);
          }
          if (sam.sampleAnalyte.samplePurposeId) {
            sam.sampleAnalyte.samplePurposeAlias = Sample.hot.getAliasFromId(sam.sampleAnalyte.samplePurposeId, Sample.hot.sampleOptions.samplePurposesDtos);
          }
        }
      }
      if (sam.receivedDate) {
        sam.receivedDate = sam.receivedDate.substring(0,10);
      }
      
      return sam;
    });
  },
  
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
      newSam.sampleAdditionalInfo.parentSampleClassAlias = Sample.hot.getAliasFromId(newSam.sampleAdditionalInfo.parentSampleClassId, Sample.hot.sampleOptions.sampleClassesDtos);
      newSam.sampleAdditionalInfo.sampleClassId = Sample.hot.sampleClassId;
      newSam.sampleAdditionalInfo.sampleClassAlias = Sample.hot.getAliasFromId(newSam.sampleAdditionalInfo.sampleClassId, Sample.hot.sampleOptions.sampleClassesDtos);
      newSam.sampleAdditionalInfo.parentId = parseInt(sam.id);
      newSam.sampleAdditionalInfo.parentAlias = clone(sam.alias);
      
      return newSam;
    });
  },
  
  // build the dropdowns that need to be chosen before the Handsontable is created
  addProjectEtcDropdowns: function () {
    // hide HOT container
    document.getElementById('hotContainer').style.display = 'none';
    // add project dropdown (and optional subproject)
    this.addProjectSelect();
    // if detailedSample is selected, add sample class dropdown
    if (this.detailedSample) {
      this.addClassSelect();
    } else {
      document.getElementById('makeTable').disabled = false;
      document.getElementById('makeTable').classList.remove('disabled');
    }
  },
  
  addInstituteAliasToLab: function () {
    // need to be able to display the lab alias and the institute alias
    var labs = this.sampleOptions['labsDtos'];
    for (var i=0; i<labs.length; i++) {
      var instituteAlias = this.sampleOptions['institutesDtos'].filter(function (inst) {
        return inst.id == labs[i].instituteId;
      })[0].alias;
      labs[i].instituteAlias = instituteAlias;
    }
  },
  
  addProjectSelect: function () {
    this.projectsArray = this.sortByProperty(this.dropdownRef.projects, 'id');
    var select = [];
    select.push('<option value="">Select project</option>');
    for (var i=0; i<this.projectsArray.length; i++) {
      select.push('<option value="'+ this.projectsArray[i].id +'"');
      select.push(this.projectsArray[i].id == this.selectedProjectId ? ' selected' : '');
      select.push('>'+ this.projectsArray[i].name +' ('+ this.projectsArray[i].alias +')</option>');
    }
    document.getElementById('projectSelect').insertAdjacentHTML('beforeend', select.join(''));
    
    // if detailedSample is selected, add subproject dropdown
    if (this.detailedSample && this.selectedProjectId) {
      this.addSubprojectSelect();
    }
    document.getElementById('projectSelect').addEventListener('change', this.addSubprojectSelect);
  },

  addSubprojectSelect: function() {
    if (document.getElementById('projectSelect').value === '') {
      alert("Please select a project.");
      return null;
    }
    var projectId = document.getElementById('projectSelect').value;
    var filteredSubprojects = Sample.hot.sortByProperty(Sample.hot.filterSubprojectsByProjectId(projectId), 'id');
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
  
  filterSubprojectsByProjectId: function (projectId) {
    var projId = parseInt(projectId);
    var rtn = Sample.hot.sampleOptions.subprojectsDtos.filter(function(subp) {
      return subp.parentProjectId == projId;
    });
    return rtn;
  },
  
  addClassSelect: function () {
    var select = [];
    var classes = this.sortByProperty(this.sampleOptions.sampleClassesDtos, 'id');
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
  
  enableTableButton: function (event) {
    if (event.target.value !== '') {
      document.getElementById('makeTable').disabled = false;
      document.getElementById('makeTable').classList.remove('disabled');
    }
  },
  
  makeNewSamplesTable: function () {
    // if this is disabled, alert the user as to why
    if (document.getElementById('makeTable').disabled || document.getElementById('projectSelect') === '') {
      var message = 'Please select a project ' + (Sample.hot.detailedSample ? 'and sample class ' : '') + 'before creating the table.';
      alert(message);
      return false;
    }
    // if table exists, confirm before obliterating it and creating a new one
    // TODO: add a more nuanced check that checks for actual unsaved changes...
    if (Sample.hot.hotTable) {
      if (confirm("You have unsaved data. Are you sure you wish to abandon your changes and start with a new table?"
          + " (You can press 'Cancel' now and copy your data to paste it in later)") === false) {
        // if detailedSample is enabled, reset the sampleClass value to where it was before the change
        if (Sample.hot.detailedSample) {
          document.getElementById('classDropdown').value = Sample.hot.sampleClassId;
        }
        return false;
      } else {
        // destroy existing table if they do wish to abandon changes
        Sample.hot.hotTable.destroy();
        Sample.hot.startData = [];
      }
    }
    // if detailedSample is enabled, re-store the selected sampleClassId for this table
    if (Sample.hot.detailedSample) {
      Sample.hot.sampleClassId = document.getElementById('classDropdown').value;
      this.dataSchema.sampleAdditionalInfo.sampleClassAlias = this.getAliasFromId(this.sampleClassId, this.sampleOptions.sampleClassesDtos);
    }
    
    // make the table
    var sampleCategory = null;
    if (Sample.hot.detailedSample) {
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
      this.parseIntRows(number + " is not a number. Please enter a number.\n\n");
    }
  },

  makeHOT: function (startingValues, sampleCategory) {
    // are new samples parented to IDs being requested
    var idColBoolean = (startingValues ? false : true);
    this.colConf = Sample.hot.setColumnData(this.detailedSample, sampleCategory, idColBoolean);

    if (!startingValues) {
      // set initial number of rows to display. 
      var startRowsNumber = Sample.hot.parseIntRows("");
      if (startRowsNumber === false) return false;
      
      // stringify and parse to clone the object (instead of using referential copies
      var defaultObject = JSON.stringify(Sample.hot.getDefaultDetailedValues());
      Sample.hot.addEmptyRow((startRowsNumber - this.startData.length), defaultObject);
    } else {
      Sample.hot.startData = startingValues;
    }
    
    // make HOT instance
    var hotContainer = document.getElementById('hotContainer');
    Sample.hot.hotTable = new Handsontable(hotContainer, {
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
    
    // enable save button if it was disabled
    if (Sample.hot.button && Sample.hot.button.className.indexOf('disabled') !== -1) Sample.hot.toggleButtonAndLoaderImage(Sample.hot.button);
  },
  
  regenerateWithQcs: function () {
    Sample.hot.showQcs = true;
    var sampleCategory = Sample.hot.getCategoryFromClassId(Sample.hot.hotTable.getSourceData()[0].sampleAdditionalInfo.sampleClassId);
    this.colConf = Sample.hot.setColumnData(this.detailedSample, sampleCategory, false);
    
    this.hotTable.updateSettings({ columns: this.colConf, colHeaders: this.getValues('header', this.colConf) });
  },
  
  hideAdditionalCols: function () {
    var sampleCategory = Sample.hot.getCategoryFromClassId(Sample.hot.hotTable.getSourceData()[0].sampleAdditionalInfo.sampleClassId);
    this.colConf = Sample.hot.setColumnData(false, sampleCategory, false);
    
    this.hotTable.updateSettings({ columns: this.colConf, colHeaders: this.getValues('header', this.colConf) });
  },
  
 startData: [],
 
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
     externalName: null
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
     qcPassedDetailId: null
   },
   sampleAnalyte: {
     samplePurposeId: null,
     samplePurposeAlias: null,
     sampleGroupId: null,
     sampleGroupComposite: null,
     tissueMaterialId: null,
     tissueMaterialAlias: null,
     strStatus: null,
     region: null,
     tubeId: null,
     stockNumber: null,
     aliquotNumber: null
   },
   sampleTissue: {
     instituteTissueName: null,
     cellularity: null
   }
 },
 
 addEmptyRow: function (numberToAdd, defaultObject) {
   var number = (numberToAdd === undefined ? 1 : numberToAdd);
   for (var i=1; i<=number; i++) {
     Sample.hot.startData.push((defaultObject ? JSON.parse(defaultObject) : {}));
   }
 },
 
 /**
  *  pre-populate default values for certain columns
  */
 getDefaultDetailedValues: function () {
   var sampleClassAlias = Sample.hot.getAliasFromId(Sample.hot.sampleClassId, Sample.hot.sampleOptions.sampleClassesDtos);
   var rootSampleClassId = Sample.hot.getRootSampleClassId();
   return {
	 'sampleAdditionalInfo': {
	   'sampleClassAlias': sampleClassAlias,
	   'parentSampleClassId': rootSampleClassId
	 },
     'scientificName': Sample.hot.sciName
   };
 },
  
  getAlias: function (obj) {
    if (obj["alias"]) return obj["alias"];
  },
  
  getValues: function (key, objArr) {
    return objArr.map(function (obj) { return obj[key]; });
  },
  
  sortByProperty: function (array, propertyName) {
    return array.sort(function (a, b) {
      return a[propertyName] > b[propertyName] ? 1 : ((b[propertyName] > a[propertyName]) ? -1 : 0);
    });
  },

  getSubprojects: function() {
    return this.sortByProperty(this.sampleOptions['subprojectsDtos'], 'id').map(this.getAlias);
  },
  
  getSampleTypes: function () {
    return this.dropdownRef['sampleTypes'];
  },

  getQcValues: function () {
    return this.dropdownRef['qcValues'].map(function (val) { if (val === '') val = 'unknown'; return val; });
  },
  
  getStrStatuses: function () {
    return this.dropdownRef['strStatuses'];
  },

  getTissueOrigins: function () {
    return this.sortByProperty(this.sampleOptions['tissueOriginsDtos'], 'id').map(this.getAlias);
  },

  getTissueTypes: function () {
    return this.sortByProperty(this.sampleOptions['tissueTypesDtos'], 'id').map(this.getAlias);
  },

  getTissueMaterials: function () {
    return this.sortByProperty(this.sampleOptions['tissueMaterialsDtos'], 'id').map(this.getAlias);
  },

  getSampleClasses: function () {
    return this.sortByProperty(this.sampleOptions['sampleClassesDtos'], 'id').map(this.getAlias);
  },
  
  getValidClassesForParent: function (parentScId) {
	var parentSampleClassId = parentScId;
	return Sample.hot.sortByProperty(Sample.hot.sampleOptions['sampleValidRelationshipsDtos'], 'id')
	             .filter(function (rel) { return rel.parentId == parentSampleClassId; })
				 .map(function (rel) { return Sample.hot.getAliasFromId(rel.childId, Sample.hot.sampleOptions.sampleClassesDtos); });
  },

  getSamplePurposes: function () {
    return this.sortByProperty(this.sampleOptions['samplePurposesDtos'], 'id').map(this.getAlias); 
  },

  getSampleGroups: function () {
    var filteredSGs = this.sampleOptions['sampleGroupsDtos'].filter(function (group) { 
      return group.projectId == Sample.hot.selectedProjectId;
    });
    return this.sortByProperty(filteredSGs, 'id').reverse().map(function (group) { return group.groupId +' - '+ group.description; });
  },

  getQcPassedDetails: function () {
    return this.sortByProperty(this.sampleOptions['qcPassedDetailsDtos'], 'id').map(function (qcpd) { return qcpd.description; });
  },
  
  getLabs: function () {
    return this.sortByProperty(this.sampleOptions['labsDtos'], 'id').map(function (lab) { return lab.alias +' - '+ lab.instituteAlias; });
  },
  
  getKitDescriptors: function () {
    return this.sortByProperty(this.sampleOptions['kitDescriptorsDtos'], 'manufacturer')
      .filter(function (kit) { return kit.kitType == 'Extraction'; })
      .map(function (kit) { return kit.name; });
  },
  
  /*
   * set which columns will be displayed
   * params: boolean detailedSample
   *         boolean qcBool (have qc columns been requested)
   *         String sampleCategory
   *         boolean idColBoolean (true if no starting values are provided so parent is Identity, false if starting values are provided 
   *                               so parent is sample)
   */
  setColumnData: function (detailedBool, sampleCategory, idColBoolean) {
    var qcBool = Sample.hot.showQcs;
    if (!detailedBool && !qcBool) {
     // if neither detailed sample not qcs are requested
      return this.concatArrays(setAliasCol(), setPlainCols());
    } else if (!detailedBool && qcBool) {
      // if detailed sample is not requested but qcs are
      return this.concatArrays(setAliasCol(), setPlainCols(), setQcCols());
    } else if (detailedBool && !qcBool){
      // if detailed sample is requested but qcs are
      return this.concatArrays(setAliasCol(), setPlainCols(), setDetailedCols(sampleCategory, idColBoolean));
    } else if (detailedBool && qcBool) {
      // if detailed sample and qcs are requested
      return this.concatArrays(setAliasCol(), setPlainCols(), setDetailedCols(sampleCategory, idColBoolean), setQcCols());
    }
    
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
          header: 'Barcode',
          data: 'identificationBarcode',
          type: 'text'
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
          header: 'Alias',
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
          data: 'sampleAnalyte.sampleGroupComposite',
          type: 'autocomplete',
          strict: false,
          trimDropdown: false,
          allowInvalid: false,
          source: Sample.hot.getSampleGroups(),
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
            validator: validatePassageNumber
          },{
            header: 'Times Received',
            data: 'sampleAdditionalInfo.timesReceived',
            type: 'numeric',
            validator: requiredText
          },{
            header: 'Sample Class',
            data: 'sampleAdditionalInfo.sampleClassAlias',
            type: 'dropdown',
            trimDropdown: false,
            source: Sample.hot.getValidClassesForParent(Sample.hot.getRootSampleClassId())
          },{
            header: 'Tube Number',
            data: 'sampleAdditionalInfo.tubeNumber',
            type: 'numeric',
            validator: requiredText
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
        additionalCols = Sample.hot.concatArrays(parentColumn, additionalCols);
      }()); 
      return Sample.hot.concatArrays(additionalCols, getSampleCategoryCols(sampleCategory, tissueCols, analyteCols));
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
        for (var i=0; i<Sample.hot.startData.length; i++) {
          if (Sample.hot.startData[i].alias == value) {
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
    
    function validatePassageNumber (value, callback) {
      if (value === '' || Handsontable.helper.isNumeric(value) && value > 0) {
        return callback(true);
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
  
  getCategoryFromClassId: function (sampleClassId) {
    return Sample.hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
      return sampleClass.id == sampleClassId;
    })[0].sampleCategory;
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
  
  getIdFromLabComposite: function (aliasComposite, labCollection) {
    return labCollection.filter(function (lab) {
      // labsDtos was processed at an earlier step to include instituteAlias as property on each lab
      return lab.alias +" - "+ lab.instituteAlias == aliasComposite;
    })[0].id;
  },
  
  getLabCompositeFromId: function (id, labCollection) {
    var lab = labCollection.filter(function (lab) {
      return lab.id == id;
    })[0];
    return lab.alias +' - '+ lab.instituteAlias;
  },
  
  getIdFromSGComposite: function (sgComposite, sgCollection) {
    var sgByProject = sgCollection.filter(function (group) { return group.projectId == Sample.hot.selectedProjectId; });
    // samplegroupComposite is "[groupId] - [description]"
    var array = sgComposite.split(/\s\W\s/);
    return sgByProject.filter(function (group) {
      return (group.groupId == array[0] && group.description == array[1]);
    })[0].id;
  },
  
  getSGCompositeFromId: function (id, sgCollection) {
    var sg = sgCollection.filter(function (sg) {
      return sg.id == id;
    })[0];
    return sg.groupId + ' - ' + sg.description;
  },
  
  getRootSampleClassId: function () {
    return Sample.hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
      // TODO: make this configurable in case an institute wants a root sample class with a different name
      return sampleClass.alias == 'Identity'; 
    })[0].id;
  },
  
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
    if (!Sample.hot.detailedSample) {
      return sample;
    }

    sample.rootSampleClassId = this.getRootSampleClassId();
    
    // add sample parent attributes. 
    if (obj.sampleIdentity && obj.sampleIdentity.externalName) {
      sample.sampleIdentity = {
          externalName: obj.sampleIdentity.externalName
      };
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
      sample.sampleAdditionalInfo.tissueOriginId = this.getIdFromAlias(obj.sampleAdditionalInfo.tissueOriginAlias, this.sampleOptions.tissueOriginsDtos);
    }
    if (obj.sampleAdditionalInfo.tissueTypeId && !obj.sampleAdditionalInfo.tissueTypeAlias) {
      sample.sampleAdditionalInfo.tissueTypeId = obj.sampleAdditionalInfo.tissueTypeId;
    } else {
      sample.sampleAdditionalInfo.tissueTypeId = this.getIdFromAlias(obj.sampleAdditionalInfo.tissueTypeAlias, this.sampleOptions.tissueTypesDtos);
    }
    if (obj.sampleAdditionalInfo.sampleClassId && !obj.sampleAdditionalInfo.sampleClassAlias) {
      sample.sampleAdditionalInfo.sampleClassId = obj.sampleAdditionalInfo.sampleClassId;
    } else {
      sample.sampleAdditionalInfo.sampleClassId = this.getIdFromAlias(obj.sampleAdditionalInfo.sampleClassAlias, this.sampleOptions.sampleClassesDtos);
    }
    // add optional attributes
    if (obj.sampleAdditionalInfo.subprojectId && !obj.sampleAdditionalInfo.subprojectAlias) {
      sample.sampleAdditionalInfo.subprojectId = obj.sampleAdditionalInfo.subprojectId;
    } else if (obj.sampleAdditionalInfo.subprojectAlias){
    	sample.sampleAdditionalInfo.subprojectId = this.getIdFromAlias(obj.sampleAdditionalInfo.subprojectAlias, this.sampleOptions.subprojectsDtos);
    } else if (document.getElementById('subprojectSelect') && document.getElementById('subprojectSelect').value > 0) {
      sample.sampleAdditionalInfo.subprojectId = parseInt(document.getElementById('subprojectSelect').value);
    }
    if (obj.sampleAdditionalInfo.labComposite && obj.sampleAdditionalInfo.labComposite.length) {
      sample.sampleAdditionalInfo.labId = Sample.hot.getIdFromLabComposite(obj.sampleAdditionalInfo.labComposite, this.sampleOptions.labsDtos);
    }
    if (obj.sampleAdditionalInfo.externalInstituteIdentifier && obj.sampleAdditionalInfo.externalInstituteIdentifier.length) {
      sample.sampleAdditionalInfo.externalInstituteIdentifier = obj.sampleAdditionalInfo.externalInstituteIdentifier;
    }
    if (obj.sampleAdditionalInfo.prepKitAlias && obj.sampleAdditionalInfo.prepKitAlias.length) {
      sample.sampleAdditionalInfo.prepKitId = Sample.hot.getIdFromAlias(obj.sampleAdditionalInfo.prepKitAlias, Sample.hot.kitDescriptorsDtos);
    }
    if (obj.sampleAdditionalInfo.parentId) {
      sample.sampleAdditionalInfo.parentId = obj.sampleAdditionalInfo.parentId;
    }
    
    // add sampleAnalyte attributes. 
    if (Sample.hot.getCategoryFromClassId(sample.sampleAdditionalInfo.sampleClassId) == 'Analyte') {
      sample.sampleAnalyte = {};
    
      if (obj.sampleAnalyte) {
        if (obj.sampleAnalyte.samplePurposeAlias && obj.sampleAnalyte.samplePurposeAlias.length) {
          sample.sampleAnalyte.samplePurposeId = this.getIdFromAlias(obj.sampleAnalyte.samplePurposeAlias, this.sampleOptions.samplePurposesDtos);
        }
        if (obj.sampleAnalyte.sampleGroupComposite && obj.sampleAnalyte.sampleGroupComposite.length) {
          sample.sampleAnalyte.sampleGroupId = this.getIdFromSGComposite(obj.sampleAnalyte.sampleGroupComposite, this.sampleOptions.sampleGroupsDtos);
        }
        if (obj.sampleAnalyte.tissueMaterialAlias && obj.sampleAnalyte.tissueMaterialAlias.length) {
          sample.sampleAnalyte.tissueMaterialId = this.getIdFromAlias(obj.sampleAnalyte.tissueMaterialAlias, this.sampleOptions.tissueMaterialsDtos);
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
     //sample.sampleAdditionalInfo.qcPassedDetailId = Sample.hot.getIdFromAlias(obj.sampleAdditionalInfo.qcPassedDetailAlias, Sample.hot.sampleOptions.qcPassedDetailsDtos);
    if (obj.volume && obj.volume.length) {
      sample.volume = obj.volume;
    }
    if (obj.sampleAdditionalInfo.concentration && obj.sampleAdditionalInfo.concentration.length) {
      sample.sampleAdditionalInfo.concentration = obj.sampleAdditionalInfo.concentration;
    }
    return sample;
  },
  
  getOneSample: function (sampleId, rowIndex) {
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
  
  failSave: function (xhr, rowIndex, numberToSave) {
    console.log(xhr);
    var responseText = JSON.parse(xhr.responseText);
    var allColumnData = Sample.hot.getValues('data', Sample.hot.colConf);
    var column, columnIndex;
    if (responseText.data && responseText.data.constraintName) {
      // if a column's constraint was violated, extract it here
      column = responseText.data.constraintName;
      columnIndex = allColumnData.indexOf(column);
    }
    console.log(rowIndex, columnIndex);
    if (rowIndex !== undefined && columnIndex !== -1 && columnIndex !== undefined) {
      Sample.hot.hotTable.setCellMeta(rowIndex, columnIndex, 'valid', false);
    }
    // process error message if it was a SQL violation, and add any errors to the messages array
    var reUserMessage = /could not execute .*?: (.*)/;
    Sample.hot.messages.failed.push("Row "+ (rowIndex + 1) +": "+ responseText.detail.replace(reUserMessage, "$1")); 
    
    // display any errors if this is the final sample to be saved
    if (Sample.hot.messages.success.length + Sample.hot.messages.failed.length >= numberToSave) {
      Sample.hot.addAnyErrors();
    }
  },
  
  successSave: function (xhr, rowIndex, numberToSave) {
    // add sample url and id to the data source if the sample is newly created
    if (!Sample.hot.startData[rowIndex].id) {
      Sample.hot.startData[rowIndex].url = xhr.getResponseHeader('Location');
      var sampleId = Sample.hot.startData[rowIndex].url.split('/').pop();
      Sample.hot.startData[rowIndex].id = sampleId;
      
      // get sample data and update alias
      Sample.hot.getOneSample(sampleId, rowIndex);
    }

    // add a 'saved' attribute to the data source 
    Sample.hot.startData[rowIndex].saved = true;

    // display any errors if this is the final sample to be saved
    if (Sample.hot.messages.success.length + Sample.hot.messages.failed.length == numberToSave) {
      Sample.hot.addAnyErrors();
    }
  },
  
  updateAlias: function (xhr, rowIndex) {
    var sample = JSON.parse(xhr.response);
    Sample.hot.messages.success[rowIndex] = sample.alias;
    Sample.hot.hotTable.setDataAtCell(rowIndex, 0, sample.alias);
  },

  saveDetailedData: function () {
    // check that a project and class have been declared
    if (document.getElementById('projectSelect').value === '' || document.getElementById('classDropdown').value === '') {
      Sample.hot.messages.failed.push('Make sure both Project and Sample Class are selected before saving.');
      Sample.hot.addAnyErrors();
      return false;
    }
    
    var continueValidation = Sample.hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;
    
    Sample.hot.hotTable.validateCells(function (isValid) { 
      if (isValid) {
        // check for sampleValidRelationship with RootSampleClass as parent
        var parentClassId = Sample.hot.getRootSampleClassId();
        var childClassId = document.getElementById('classDropdown').value;
        var validRelationship = Sample.hot.findMatchingRelationship(parentClassId, childClassId);
        if (validRelationship.length === 0) {
          Sample.hot.messages.failed.push(Sample.hot.makeErrorMessageForInvalidRelationship(parentClassId, childClassId) 
                  +  " Please copy your data, select another child class and save again.");
          Sample.hot.addAnyErrors();
          document.getElementById('classDropdown').removeAttribute('disabled');
          document.getElementById('classDropdown').classList.remove('disabled');
          document.getElementById('classDropdown').classList.add('invalid');
          return false;
        }

        // send it through the parser to get a sampleData array that isn't merely a reference to Sample.hot.hotTable.getSourceData()
        var sampleData = JSON.parse(JSON.parse(JSON.stringify(Sample.hot.hotTable.getSourceData())));
        
        // add aliases of previously-saved items to the position corresponding to their row (zero-index data, one-index UI)
        // aliases of successfully-saved items will be added after save
        Sample.hot.messages.success = sampleData.map(function (sample) { return (sample.saved === true ? sample.alias : null); });

        // Array of save functions, one for each line in the table
        var sampleSaveArray = Sample.hot.getArrayOfNewObjects(sampleData);
        Sample.hot.serial(sampleSaveArray); // Execute saves serially      
      } else {
        Sample.hot.validationFails();
        return false;
      }
    });
  },
  
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
      if (Sample.hot.detailedSample) {
        arrayOfObjects.push(sampleSaveFunction(JSON.stringify(newSample), i, len));
      } else {
        arrayOfObjects.push(newSample);
      }
    }
    return arrayOfObjects;
  },
  
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
  
  removeEmptyBottomRows: function (tableData) {
    while (Sample.hot.startData.length > 1 && Sample.hot.hotTable.isEmptyRow(tableData.length - 1)) {
      Sample.hot.hotTable.alter('remove_row', parseInt(tableData.length - 1), keepEmptyRows = false);
    }
  },
  
  makeErrorMessageForInvalidRelationship: function (parentClassId, childClassId) {
    var parentClassAlias = Sample.hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
      return sampleClass.id == parentClassId;
    })[0].alias;
    var childClassAlias = Sample.hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
      return sampleClass.id == childClassId;
    })[0].alias;
    return parentClassAlias + ' is not a valid parent for ' + childClassAlias + '.';
  },
  
  cleanRowsAndToggleSaveButton: function () {
   // reset error and success messages
    Sample.hot.messages.failed = [];
    Sample.hot.messages.success = [];
    
    // disable the save button
    if (Sample.hot.button) Sample.hot.toggleButtonAndLoaderImage(Sample.hot.button);
    
    var tableData = Sample.hot.startData;
    
    // if last row is empty, remove it before validation
    Sample.hot.removeEmptyBottomRows(tableData);
    
    // if there are no rows, add one back in and exit
    if (tableData.length === 0) {
      Sample.hot.startData = [];
      Sample.hot.validationFails();
      return false;
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
  
  savePlainData: function () {
    // check that a project has been declared
    if (document.getElementById('projectSelect').value === '') {
      Sample.hot.messages.failed.push('Make sure that a Project is selected before saving.');
      Sample.hot.addAnyErrors();
      return false;
    }
    
    var continueValidation = Sample.hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;

    Sample.hot.hotTable.validateCells(function (isValid) {
      if (isValid) {
        document.getElementById('errorMessages').innerHTML = '';
        document.getElementById('saveErrors').classList.add('hidden');
        
        // send it through the parser to get a sampleData array that isn't merely a reference to Sample.hot.hotTable.getSourceData()
        var sampleData = JSON.parse(JSON.parse(JSON.stringify(Sample.hot.hotTable.getSourceData())));
        
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
                  Sample.hot.messages.failed.push("Row " + (j+1) +": "+ " Sample did not save. Please check that the sample alias is unique!");
                } else {
                  for (var k=0; k<Sample.hot.startData.length; k++) {
                    if (Sample.hot.startData[k].alias == sampleData[j].alias) {
                      Sample.hot.startData[k].saved = true;
                      Sample.hot.messages.success.push(Sample.hot.startData[k].alias);
                      break;
                    }
                  }
                }
              } 
              // display error/success messages
              Sample.hot.addAnyErrors();
            },
            'doOnError': function (json) {
              Sample.hot.messages.failed.push(json.error);
              Sample.hot.addAnyErrors();
              return false;
            }
          }
        );
      } else {
        Sample.hot.validationFails();
        return false;
      }
    });
  },

  updateData: function () {
    var continueValidation = Sample.hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;
      
    Sample.hot.hotTable.validateCells(function (isValid) {
      if (isValid) {
        // no check for sampleValidRelationship, since the sampleClass is not editable
        
        // send data through the parser to get a sampleData array that isn't merely a reference to Sample.hot.hotTable.getSourceData()
        var sampleData = JSON.parse(JSON.parse(JSON.stringify(Sample.hot.hotTable.getSourceData())));
        
        // add previously-saved aliases to success message
        Sample.hot.messages.success = sampleData.filter(function (sample) { return (sample.saved === true); })
                                                .map(function (sample) { return sample.alias; });
        
        // Array of save functions, one for each line in the table
        var sampleSaveArray = Sample.hot.getArrayOfUpdatedObjects(sampleData);
        Sample.hot.serial(sampleSaveArray); // Execute saves serially    
      } else {
        Sample.hot.validationFails();
        return false;
      }
    });
  },
  
  propagateData: function () {
    var continueValidation = Sample.hot.cleanRowsAndToggleSaveButton();
    if (continueValidation === false) return false;
    
    // send table data through the parser to get a copy of (not a reference to) sampleData array
    var sampleData = JSON.parse(JSON.parse(JSON.stringify(Sample.hot.hotTable.getSourceData())));
    
    // check for SampleValidRelationships
    var validRelationships = Sample.hot.assessValidRelationships(sampleData);
    if (!validRelationships) {
      Sample.hot.addAnyErrors();
      return false;
    }
    
    Sample.hot.hotTable.validateCells(function (isValid) {
      if (isValid) {        
        
        Sample.hot.messages.success = sampleData.filter(function (sample) { return (sample.saved === true); })
                                                .map(function (sample) { return sample.alias; });
        
        // Array of save functions, one for each line in the table
        var sampleSaveArray = Sample.hot.getArrayOfNewObjects(sampleData);
        Sample.hot.serial(sampleSaveArray); // Execute saves serially
      } else {
        Sample.hot.validationFails();
        return false;
      }
    });
  },
  
  findMatchingRelationship: function (parentClassId, childClassId) {
    var value = Sample.hot.sampleOptions.sampleValidRelationshipsDtos.filter(function (sampleClass) {
      return (sampleClass.parentId == parentClassId && sampleClass.childId == childClassId);
    });
    return value;
  },
  
  assessValidRelationships: function (sampleData) {
    var col;
    for (var j = 0; j < Sample.hot.hotTable.countCols(); j++) {
      if (Sample.hot.hotTable.getCellMeta(0, j).prop == "sampleAdditionalInfo.sampleClassAlias") {
        col = j;
        break;
      }
    }
    for (var i = 0; i < sampleData.length; i++) {
      var parentClassId = Sample.hot.getIdFromAlias(sampleData[i].sampleAdditionalInfo.parentSampleClassAlias, Sample.hot.sampleOptions.sampleClassesDtos);
      var childClassId = Sample.hot.getIdFromAlias(sampleData[i].sampleAdditionalInfo.sampleClassAlias, Sample.hot.sampleOptions.sampleClassesDtos);
      var validRelationship = Sample.hot.findMatchingRelationship(parentClassId, childClassId);
      if (validRelationship.length === 0) {
        Sample.hot.messages.failed.push("Row " + i + ": " + Sample.hot.makeErrorMessageForInvalidRelationship(parentClassId, childClassId));
        Sample.hot.failedComplexValidation.push([i, col]);
      }
    }
    return (Sample.hot.messages.failed.length ? false : true);
  },
  
  makeSavedRowsReadOnly: function () {
    Sample.hot.hotTable.updateSettings({
      cells: function (row, col, prop) {
        var cellProperties = {};
        
        if (Sample.hot.hotTable.getSourceData()[row].saved) {
          cellProperties.readOnly = true;
        }
        
        return cellProperties;
      }
    });
  },
  
  markCellsInvalid: function (rowIndex, colIndex) {
    Sample.hot.hotTable.setCellMeta(rowIndex, colIndex, 'valid', false);
  },
  
  validationFails: function () {
    Sample.hot.messages.failed.push("It looks like some cells are not yet valid. Please fix them before saving.");
    Sample.hot.addAnyErrors();
  },
  
  addAnyErrors: function () {
    var messages = Sample.hot.messages;
    console.log(Sample.hot.messages);
    var successfullySaved = messages.success.filter(function (message) { return (!parseInt(message) && message !== null); });
    if (successfullySaved.length) {
      var successMessage = successfullySaved.length + " samples are now saved.";
      document.getElementById('successMessages').innerHTML = successMessage;
      document.getElementById('saveSuccesses').classList.remove('hidden');
      Sample.hot.makeSavedRowsReadOnly();
    } else {
      document.getElementById('saveSuccesses').classList.add('hidden');
    }
    
    if (Sample.hot.button) Sample.hot.toggleButtonAndLoaderImage(Sample.hot.button);
    
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
      Sample.hot.hotTable.validateCells();
      for (var i = 0; i < Sample.hot.failedComplexValidation.length; i++) {
        var failedIndices = Sample.hot.failedComplexValidation[i];
        Sample.hot.markCellsInvalid(failedIndices[0], failedIndices[1]);
      }
      Sample.hot.hotTable.render();
      return false;
    } else {
      document.getElementById('saveErrors').classList.add('hidden');
    }
    return true;
  }
};