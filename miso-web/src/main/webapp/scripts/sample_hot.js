Sample.hot = {
  dropdownRef: null,
  detailedSample: null,
  projectsArray: null,
  selectedProjectId: null,
  sampleOptions: null,
  sampleClassId: null,
  colConf: null,
  hotTable: null,
  sampleData: null,
  messages: {
    success: [],
    failed: []
  },

  fetchSampleOptions: function () {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) xhr.status === 200 ? Sample.hot.processSampleOptionsXhr(xhr) : console.log(xhr.response);
    };
    xhr.open('GET', '/miso/rest/ui/sampleoptions');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send();
  },
  
  processSampleOptionsXhr: function (xhr) {
    this.sampleOptions = JSON.parse(xhr.responseText);
    // process sampleOptions further
    this.addInstituteAliasToLab();
    
    this.addProjectEtcDropdowns();
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
        Sample.hot.startData = [{
          project: null,
          description: null,
          receivedDate: null,
          identificationBarcode: null,
          locationBarcode: null,
          scientificName: null,
          sampleType: null,
          qcValue: '',
          alias: null
        }];
      }
    }
    
    // make the table
    var sampleCategory = null;
    if (Sample.hot.detailedSample) {
      sampleCategory = Sample.hot.getCategoryFromClassId(document.getElementById('classDropdown').value);
    }
    Sample.hot.makeHOT(null, false, sampleCategory);
    
    // disable sampleClass dropdown so they can't change it midway through editing table values
    if (document.getElementById('classDropdown')) {
      document.getElementById('classDropdown').setAttribute('disabled', 'disabled');
    }
    
    // if detailedSample is enabled, re-store the selected sampleClassId for this table
    if (Sample.hot.detailedSample) {
      Sample.hot.sampleClassId = document.getElementById('classDropdown').value;
    }
  },
  
  parseIntRows: function (message) {
    var number = window.prompt(message + "How many samples would you like to create?");
    if (number === null) return false;
    if (parseInt(number)) {
      return parseInt(number);
    } else {
      this.parseIntRows(number + " is not a number. Please enter a number.\n\n");
    }
  },

  makeHOT: function (startingValues, qcBoolean, sampleCategory) {
    // set sources for requested columns
    var qcBoolean = qcBoolean || false;
    this.colConf = Sample.hot.setColumnData(this.detailedSample, qcBoolean, sampleCategory);

    if (!startingValues) {
      // set initial number of rows to display. 
      var startRowsNumber = Sample.hot.parseIntRows("");
      if (startRowsNumber === false) return false;
      Sample.hot.addEmptyRow(startRowsNumber - this.startData.length);
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
    
    // enable save button
    var button = (Sample.hot.detailedSample ? document.getElementById('saveDetailed') : document.getElementById('savePlain'));
    button.disabled = false;
    button.classList.remove('disabled');
  },
  
 startData: [{
   project: null,
   description: null,
   receivedDate: null,
   identificationBarcode: null,
   scientificName: null,
   sampleType: null,
   qcValue: '',
   alias: null,
 }],
 
 dataSchema: {
   project: null,
   description: null,
   receivedDate: null,
   identificationBarcode: null,
   scientificName: null,
   sampleType: null,
   qcValue: '',
   alias: null,
 },
 
 addEmptyRow: function (numberToAdd) {
   var number = (numberToAdd === undefined ? 1 : numberToAdd);
   for (var i=1; i<=number; i++) {
     Sample.hot.startData.push({
       project: null,
       description: null,
       receivedDate: null,
       identificationBarcode: null,
       scientificName: null,
       sampleType: null,
       qcValue: '',
       alias: null,
     });
   }
 },
  
  getAlias: function (obj) {
    if (obj["alias"]) return obj["alias"];
  },
  
  getValues: function (key, objArr) {
    var rtn = [];
    for (var i=0; i<objArr.length; i++) {
      rtn.push(objArr[i][key]);
    }
    return rtn;
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
    return this.dropdownRef['qcValues'].map(function(obj) { return obj["label"]; });
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

  getSamplePurposes: function () {
    return this.sortByProperty(this.sampleOptions['samplePurposesDtos'], 'id').map(this.getAlias); 
  },

  getSampleGroups: function () {
    var filteredSGs = this.sampleOptions['sampleGroupsDtos'].filter(function (group) { 
      return group.projectId == Sample.hot.selectedProjectId;
    });
    return this.sortByProperty(filteredSGs, 'id').reverse().map(function (group) { return group.groupId +' - '+ group.description});
  },

  getQcPassedDetails: function () {
    return this.sortByProperty(this.sampleOptions['qcPassedDetailsDtos'], 'id').map(this.getAlias);
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
   */
  setColumnData: function (detailedBool, qcBool, sampleCategory) {
    
    if (!detailedBool && !qcBool) {
     // if neither detailed sample not qcs are requested
      return this.concatArrays(setAliasCol(), setPlainCols());
    } else if (!detailedBool && qcBool) {
      // if detailed sample is not requested but qcs are
      return this.concatArrays(setAliasCol(), setPlainCols(), setQcCols());
    } else if (detailedBool && !qcBool){
      // if detailed sample is requested but qcs are
      return this.concatArrays(setAliasCol(), setPlainCols(), setDetailedCols(sampleCategory));
    } else if (detailedBool && qcBool) {
      // if detailed sample and qcs are requested
      return this.concatArrays(setAliasCol(), setPlainCols(), setDetailedCols(sampleCategory), setQcCols());
    }
    
    function setPlainCols () {
      
      function pad (number, offset) {
        number = number.toString();
        if (number.length < offset) { 
          number = "0" + number;
          pad(number, offset);
        } 
        return number;
      }
      var todate = new Date();
      var dateString = pad((todate.getMonth() + 1), 2) + "/" + pad(todate.getDay(), 2) + "/" + todate.getFullYear();
      
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
          allowEmpty: true
        },{
          header: 'Sample Type',
          data: 'sampleType',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getSampleTypes(),
          validator: validateSampleTypes
        },{
          header: 'Barcode',
          data: 'identificationBarcode',
          type: 'text'
        },{
          header: 'Sci. Name',
          data: 'scientificName',
          type: 'text',
          validator: requiredText
        }
      ];
      
      return sampleCols;
    };
    
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
    };
    
    function setDetailedCols (sampleCategory) {
      var additionalCols = [
        {
          header: 'External Name',
          data: 'externalName',
          type: 'text'
        },{
          header: 'Tissue Origin',
          data: 'tissueOrigin',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getTissueOrigins(),
          validator: validateTissueOrigins
        },{
          header: 'Tissue Type',
          data: 'tissueType',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getTissueTypes(),
          validator: validateTissueTypes
        },{
          header: 'Passage #',
          data: 'passageNumber',
          type: 'text',
          validator: validatePassageNumber
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
          header: 'Lab',
          data: 'lab',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getLabs(),
          validator: permitEmpty
        }
      ];
      
      var tissueCols = [
        {
          header: 'External Reference',
          data: 'instituteTissueName',
          type: 'text'
        },{
          header: 'Cellularity',
          data: 'cellularity',
          type: 'text'
        }                 
      ];
       
      var analyteCols = [
        {
          header: 'Material',
          data: 'tissueMaterial',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getTissueMaterials(),
          validator: permitEmpty
        },{
          header: 'Purpose',
          data: 'samplePurpose',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getSamplePurposes(),
          validator: permitEmpty
        },{
          header: 'Region',
          data: 'region',
          type: 'text'
        },{
          header: 'Tube ID',
          data: 'tubeId',
          type: 'text'
        },{
          header: 'Group ID',
          data: 'sampleGroupId',
          type: 'autocomplete',
          strict: false,
          trimDropdown: false,
          allowInvalid: false,
          source: Sample.hot.getSampleGroups(),
          validator: permitEmpty
        },{
          header: 'Number',
          data: 'analyteNumber',
          type: 'numeric'
        },{
          header: 'Kit',
          data: 'kitDescriptor',
          type: 'dropdown',
          trimDropdown: false,
          source: Sample.hot.getKitDescriptors(),
          validator: permitEmpty
        }
      ];  
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
          type: 'text'
        },{
          header: 'Conc.',
          data: 'concentration',
          type: 'text'
        },{
          header: 'QC Passed?',
          data: 'qcValue',
          type: 'dropdown',
          source: Sample.hot.getQcValues(),
          validator: permitEmpty
        },{
          header: 'QC Detail',
          data: 'qcDetail',
          type: 'dropdown'
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
      if (value == 'nn' || Handsontable.helper.isNumeric(value)) {
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

  generateAliases: function () {
    alert("Add a Fluxion.doAjax call to generate Aliases, and have them be editable by the user before saving");
  },
  
  getIdFromAlias: function (alias, referenceCollection) {
    return referenceCollection.filter(function (item) {
      return item.alias == alias; 
    })[0].id;
  },
  
  getIdFromLabValue: function (aliasComposite, labCollection) {
    return labCollection.filter(function (lab) {
      // labsDtos was processed at an earlier step to include instituteAlias as property on each lab
      return lab.alias +" - "+ lab.instituteAlias == aliasComposite;
    })[0].id;
  },
  
  getIdFromSGComposite: function (sgComposite, sgCollection) {
    var sgByProject = sgCollection.filter(function (group) { return group.projectId == Sample.hot.selectedProjectId; });
    // samplegroupComposite is "[groupId] - [description]"
    var array = sgComposite.split(/\s\W\s/);
    sgByProject.filter(function (group) {
      return (group.groupId == array[0] && group.description == array[1]);
    })[0].id;
  },
  
  getRootSampleClassId: function () {
    return Sample.hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
      // TODO: make this configurable in case an institute wants a root sample class with a different name
      return sampleClass.alias == 'Identity'; 
    })[0].id;
  },
  
  buildSampleDtosFromData: function (obj) {
    var sample = {};
    
    // add SampleDto attributes
    sample.description = obj.description || '';
    sample.identificationBarcode = obj.identificationBarcode;
    sample.sampleType = obj.sampleType;
    sample.qcPassed = '';
    sample.alias = obj.alias;
    sample.projectId = parseInt(document.getElementById('projectSelect').value);
    sample.scientificName = obj.scientificName;
    if (obj.receivedDate && obj.receivedDate.length) {
      // the time string is added for detailedSample because the server is expecting a datetime value
      sample.receivedDate = obj.receivedDate;
      if (Sample.hot.detailedSample) {
        obj.receivedDate += "T00:00:00-05:00";
      }
    }
    
    // if it's a plain sample, return now.
    if (!Sample.hot.detailedSample) {
      return sample;
    }

    sample.rootSampleClassId = this.getRootSampleClassId();
    
    // add sampleIdentity attributes. 
    // TODO: change this later to account for samples parented to other samples (add parentId instead of sampleIdentity)
    sample.sampleIdentity = {
        externalName: obj.externalName
    };
    
    // add sampleAdditionalInfo attributes
    sample.sampleAdditionalInfo = {
      sampleClassId: parseInt(document.getElementById('classDropdown').value),
      tissueOriginId: this.getIdFromAlias(obj.tissueOrigin, this.sampleOptions.tissueOriginsDtos),
      tissueTypeId: this.getIdFromAlias(obj.tissueType, this.sampleOptions.tissueTypesDtos),
      passageNumber: (obj.passageNumber == 'nn' ? 0 : parseInt(obj.passageNumber)),
      timesReceived: parseInt(obj.timesReceived),
      tubeNumber: parseInt(obj.tubeNumber)
    };
    // add optional attributes
    if (document.getElementById('subprojectSelect') && document.getElementById('subprojectSelect').value > 0) {
      sample.sampleAdditionalInfo.subprojectId = parseInt(document.getElementById('subprojectSelect').value);
    }
    if (obj.lab && obj.lab.length) {
      sample.sampleAdditionalInfo.labId = Sample.hot.getIdFromLabValue(obj.lab, this.sampleOptions.labsDtos);
    }
    
    if (obj.kitDescriptor && obj.kitDescriptor.length) {
      sample.sampleAdditionalInfo.prepKitId = Sample.hot.getIdFromAlias(obj.prepKitId, Sample.hot.kitDescriptorsDtos);
    }
    
    // add sampleAnalyte attributes. 
    if (Sample.hot.getCategoryFromClassId(sample.sampleAdditionalInfo.sampleClassId) == 'Analyte') {
      sample.sampleAnalyte = {};
    
      if (obj.samplePurpose && obj.samplePurpose.length) {
        sample.sampleAnalyte.samplePurposeId = this.getIdFromAlias(obj.samplePurpose, this.sampleOptions.samplePurposesDtos);
      }
      if (obj.sampleGroupId && obj.sampleGroupId.length) {
        sample.sampleAnalyte.sampleGroupId = this.getIdFromSGComposite(obj.sampleGroupId, this.sampleOptions.sampleGroupsDtos);
      }
      if (obj.tissueMaterial && obj.tissueMaterial.length) {
        sample.sampleAnalyte.tissueMaterialId = this.getIdFromAlias(obj.tissueMaterial, this.sampleOptions.tissueMaterialsDtos);
      }
      if (obj.region && obj.region.length) {
        sample.sampleAnalyte.region = obj.region;
      }
      if (obj.tubeId && obj.tubeId.length) {
        sample.sampleAnalyte.tubeId = obj.tubeId;
      }
      if (obj.analyteNumber) {
        var classDropdown = document.getElementById('classDropdown');
        var classAlias = classDropdown.options[classDropdown.selectedIndex].text;
        if (classAlias.indexOf("stock")) {
          sample.sampleAnalyte.stockNumber = parseInt(obj.analyteNumber);
        } else {
          sample.sampleAnalyte.aliquotNumber = parseInt(obj.analyteNumber);
        }
      }

    } else if (Sample.hot.getCategoryFromClassId(sample.sampleAdditionalInfo.sampleClassId) == 'Tissue') {
      sample.sampleTissue = {};
      
      if (obj.instituteTissueName && obj.instituteTissueName.length) {
        sample.sampleTissue.instituteTissueName = obj.instituteTissueName;
      }
      if (obj.cellularity && obj.cellularity.length) {
        sample.sampleTissue.cellularity = obj.cellularity;
      }
    }
    
    /* TODO: add qcCols attributes to their objects:
     * sample.qcPassed = obj.qcPassed;
     * sample.sampleAdditionalInfo.qcPassedDetailId = Sample.hot.getIdFromAlias(obj.qcPassedDetail, Sample.hot.sampleOptions.qcPassedDetailsDtos);
     * sample.sampleAdditionalInfo.volume = obj.volume;
     * sample.sampleAdditionalInfo.concentration = obj.concentration;
     */
    return sample;
  },
  
  saveOneSample: function (data, index, messages, numberToSave) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        xhr.status === 201 ? Sample.hot.successSave(xhr, index, messages, numberToSave) : Sample.hot.failSave(xhr, index, messages, numberToSave);
      }
    };
    xhr.open('POST', '/miso/rest/tree/sample');
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send(data);
  },
  
  failSave: function (xhr, rowIndex, messages, numberToSave) {
    console.log(xhr);
    var failedSave = xhr;
    var responseText = JSON.parse(xhr.responseText);
    var allColumnData = Sample.hot.getValues('data', Sample.hot.colConf);
    if (responseText.data && responseText.data.constraintName) {
      // if a column's constraint was violated, extract it here
      var column = responseText.data.constraintName;
      var columnIndex = allColumnData.indexOf(column);
    }
    var rowIndex = rowIndex;
    console.log(rowIndex, columnIndex);
    if (rowIndex !== undefined && columnIndex !== -1) {
      Sample.hot.hotTable.setCellMeta(rowIndex, columnIndex, 'valid', false);
      Sample.hot.hotTable.render();
    }
    // process error message if it was a SQL violation
    var reUserMessage = /could not execute .*?: (.*)/;
    messages.failed.push("Row "+ (rowIndex + 1) +": "+ responseText.detail.replace(reUserMessage, "$1")); 
    
    // display any errors if this is the final sample to be saved
    if (messages.success.length + messages.failed.length == numberToSave) {
      Sample.hot.addAnyErrors(messages);
    }
  },
  
  successSave: function (xhr, rowIndex, messages, numberToSave) {
    messages.success.push(rowIndex); 
    
    // add a 'saved' attribute to the data source 
    Sample.hot.startData[rowIndex].saved = true;
    
    // add sampleId to the data source
    Sample.hot.startData[rowIndex].sampleId = xhr.getResponseHeader('Location');
    
    // display any errors if this is the final sample to be saved
    if (messages.success.length + messages.failed.length == numberToSave) {
      Sample.hot.addAnyErrors(messages);
    }
  },

  saveDetailedData: function () {
    // reset error messages
    Sample.hot.messages.failed = [];
    
    // check that a project and class have been declared
    if (document.getElementById('projectSelect').value === '' || document.getElementById('classDropdown').value === '') {
      Sample.hot.messages.failed.push('Make sure both Project and Sample Class are selected before saving.');
      Sample.hot.addAnyErrors(Sample.hot.messages);
      return false;
    }
    
    // if last row is empty, remove it before validation
    var tableData = Sample.hot.startData;
    while (Sample.hot.startData.length > 1 && Sample.hot.hotTable.isEmptyRow(tableData.length - 1)) {
      Sample.hot.hotTable.alter('remove_row', parseInt(tableData.length - 1), keepEmptyRows = false);
    }
    // if there are no rows, add one back in and exit
    if (tableData.length === 0) {
      Sample.hot.startData = [{
        project: null,
        description: null,
        receivedDate: null,
        identificationBarcode: null,
        locationBarcode: null,
        scientificName: null,
        sampleType: null,
        qcValue: '',
        alias: null,
      }];
      Sample.hot.hotTable.render();
      Sample.hot.hotTable.validateCells();
      Sample.hot.messages.failed.push("It looks like some cells are not yet valid. Please fix them before saving.");
      Sample.hot.addAnyErrors(Sample.hot.messages);
      return false;
    }
    
    // disable the save button
    var button = document.getElementById('saveDetailed');
    if (button) {
      button.classList.add('disabled');
      button.setAttribute('disabled', 'disabled');
      var ajaxLoader = "<img id='ajaxLoader' src='/../styles/images/ajax-loader.gif'/>";
      button.insertAdjacentHTML('afterend', ajaxLoader);
    }
    
    this.hotTable.validateCells(function (isValid) { 
      if (isValid) {
        // check for sampleValidRelationship
        // TODO: make parentClassId configurable
        var parentClassId = Sample.hot.getRootSampleClassId();
        var childClassId = document.getElementById('classDropdown').value
        var validRelationship = Sample.hot.checkValidRelationship(parentClassId, childClassId);
        if (validRelationship.length === 0) {
          var parentClassAlias = Sample.hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
            return sampleClass.id == parentClassId;
          })[0].alias;
          var childClassAlias = Sample.hot.sampleOptions.sampleClassesDtos.filter(function (sampleClass) {
            return sampleClass.id == childClassId;
          })[0].alias
          Sample.hot.messages.failed.push(parentClassAlias + ' is not a valid parent for ' + childClassAlias + '. Please select another child class and save again.');
          
          Sample.hot.addAnyErrors(Sample.hot.messages);
          return false;
        }

        // send it through the parser to get a sampleData array that isn't merely a reference to Sample.hot.hotTable.getSourceData()
        var sampleData = JSON.parse(JSON.parse(JSON.stringify(Sample.hot.hotTable.getSourceData())));
        
        // attempt to save each of the objects
        for (var i=0; i<sampleData.length; i++) {
          // skip if it's already been saved
          if (sampleData[i].saved) {
            Sample.hot.messages.success.push(sampleData[i].alias);
            continue;
          }
          
          var newSample = Sample.hot.buildSampleDtosFromData(sampleData[i]);
          Sample.hot.saveOneSample(JSON.stringify(newSample), i, Sample.hot.messages, sampleData.length);
        }
      } else {
        Sample.hot.messages.failed.push("It looks like some cells are not yet valid. Please fix them before saving.");
        Sample.hot.addAnyErrors(Sample.hot.messages);
        Sample.hot.hotTable.validateCells();
        return false;
      }
    });
  },
  
  savePlainData: function () {
    // reset error messages
    Sample.hot.messages.failed = [];
    
    // check that a project has been declared
    if (document.getElementById('projectSelect').value === '') {
      Sample.hot.messages.failed.push('Make sure that a Project is selected before saving.');
      Sample.hot.addAnyErrors(Sample.hot.messages);
      return false;
    }
    
    // if last row is empty, remove it before validation
    var tableData = Sample.hot.startData;
    while (Sample.hot.startData.length > 1 && Sample.hot.hotTable.isEmptyRow(tableData.length - 1)) {
      Sample.hot.hotTable.alter('remove_row', parseInt(tableData.length - 1), keepEmptyRows = false);
    }
    // if there are no rows, add one back in and exit
    if (tableData.length === 0) {
      Sample.hot.startData = [{
        project: null,
        description: null,
        receivedDate: null,
        identificationBarcode: null,
        locationBarcode: null,
        scientificName: null,
        sampleType: null,
        qcValue: '',
        alias: null,
      }];
      Sample.hot.hotTable.render();
      Sample.hot.messages.failed.push("It looks like some cells are not yet valid. Please fix them before saving.");
      Sample.hot.addAnyErrors(Sample.hot.messages);
      Sample.hot.hotTable.validateCells();
      return false;
    }
    
    // disable the save button
    var button = document.getElementById('savePlain');
    if (button) {
      button.classList.add('disabled');
      button.setAttribute('disabled', 'disabled');
      var ajaxLoader = "<img id='ajaxLoader' src='/../styles/images/ajax-loader.gif'/>";
      button.insertAdjacentHTML('afterend', ajaxLoader);
    }
    
    this.hotTable.validateCells(function (isValid) {
      if (isValid) {
        document.getElementById('errorMessages').innerHTML = '';
        document.getElementById('saveErrors').classList.add('hidden');
        
        // send it through the parser to get a sampleData array that isn't merely a reference to Sample.hot.hotTable.getSourceData()
        var sampleData = JSON.parse(JSON.parse(JSON.stringify(Sample.hot.hotTable.getSourceData())));
        
        var samplesArray = [];
        
        for (var i=0; i<sampleData.length; i++) {
          // skip if it's already been saved
          if (sampleData[i].saved) continue;
          
          var newSample = Sample.hot.buildSampleDtosFromData(sampleData[i]);
          samplesArray.push(newSample);
        }
        
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
                    }
                  }
                }
              } 
              // display error/success messages
              Sample.hot.addAnyErrors(Sample.hot.messages);
            },
            'doOnError': function (json) {
              Sample.hot.messages.failed.push(json.error);
              Sample.hot.addAnyErrors(Sample.hot.messages);
              return false;
            }
          }
        );
      } else {
        Sample.hot.messages.failed.push("It looks like some cells are not yet valid. Please fix them before saving.");
        Sample.hot.addAnyErrors(Sample.hot.messages);
        return false;
      }
    });
  },
  
  checkValidRelationship: function (parentClassId, childClassId) {
    var value = Sample.hot.sampleOptions.sampleValidRelationshipsDtos.filter(function (sampleClass) {
      return (sampleClass.parentId == parentClassId && sampleClass.childId == childClassId);
    });
    return value;
  },
  
  makeNewHOT: function () {
    Sample.hot.colConf = Sample.hot.concatArrays(Sample.hot.getSampleCategoryCols());
    var hotContainer2 = document.getElementById('hotContainer2');
    
    if (Sample.hot.hotTable) Sample.hot.hotTable.destroy();
    Sample.hot.hotTable = new Handsontable(hotContainer2, {
      debug: true,
      fixedColumnsLeft: 1,
      manualColumnsResize: true,
      minSpareRows: 1,
      rowHeaders: true,
      colHeaders: Sample.hot.getValues('header', colConf),
      contextMenu: false,
      columns: Sample.hot.colConf,
      data: Sample.hot.sampleData
    });
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
  
  addAnyErrors: function (messages) {
    console.log(messages);
    Sample.hot.makeSavedRowsReadOnly();
    if (messages.success.length) {
      var successMessage = "Successfully saved " + messages.success.length + " out of " + (messages.success.length + messages.failed.length) + " samples.";
      document.getElementById('successMessages').innerHTML = successMessage;
      document.getElementById('saveSuccesses').classList.remove('hidden');
    } else {
      document.getElementById('saveSuccesses').classList.add('hidden');
    }
    
    Sample.hot.hotTable.render();
    var button = document.getElementById('saveDetailed') || document.getElementById('savePlain');
    if (button) {
      button.classList.remove('disabled');
      button.removeAttribute('disabled');
      var ajaxLoader = document.getElementById('ajaxLoader');
      if (ajaxLoader) ajaxLoader.parentNode.removeChild(ajaxLoader);
    }
    
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
      return false;
    } else {
      document.getElementById('saveErrors').classList.add('hidden');
    }
    return true;
  }
};