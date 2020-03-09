Urls = (function() {

  function idUrlFunction(baseUrl) {
    return function(id) {
      return baseUrl + '/' + id;
    }
  }

  function middleIdUrlFunction(prefix, suffix) {
    return function(id) {
      return prefix + '/' + id + suffix;
    }
  }

  var baseUrl = '/miso';
  var restBase = baseUrl + '/rest';

  var ui = {};
  var rest = {};
  var external = {};

  // Arrays
  var arrayUiBase = baseUrl + '/array';
  ui.arrays = {
    create: arrayUiBase + '/new',
    edit: idUrlFunction(arrayUiBase)
  };

  var arrayRestBase = restBase + '/arrays';
  rest.arrays = {
    datatable: arrayRestBase + '/dt'
  };

  // Array Models
  var arrayModelRestBase = restBase + '/arraymodels'
  rest.arrayModels = {
    create: arrayModelRestBase,
    update: idUrlFunction(arrayModelRestBase)
  };

  // Array Runs
  var arrayRunUiBase = baseUrl + '/arrayrun';
  ui.arrayRuns = {
    create: arrayRunUiBase + '/new',
    edit: idUrlFunction(arrayRunUiBase)
  };

  var arrayRunRestBase = restBase + '/arrayruns';
  rest.arrayRuns = {
    datatable: arrayRunRestBase + '/dt',
    projectDatatable: idUrlFunction(arrayRunRestBase + '/dt/project')
  };

  // Attachment Categories
  var attachmentCategoryUiBase = baseUrl + '/attachmentcategories';
  ui.attachmentCategories = {
    bulkCreate: attachmentCategoryUiBase + '/bulk/new',
    bulkEdit: attachmentCategoryUiBase + '/bulk/edit'
  };

  var attachmentCategoryRestBase = restBase + '/attachmentcategories'
  rest.attachmentCategories = {
    create: attachmentCategoryRestBase,
    update: idUrlFunction(attachmentCategoryRestBase)
  };

  // Boxes
  var boxUiBase = baseUrl + '/box';
  ui.boxes = {
    edit: idUrlFunction(boxUiBase)
  };

  var boxRestBase = restBase + '/boxes';
  rest.boxes = {
    create: boxRestBase,
    update: idUrlFunction(boxRestBase),
    searchPartial: boxRestBase + '/search/partial',
    setLocation: middleIdUrlFunction(boxRestBase, '/setlocation')
  };

  // Box Sizes
  var boxSizeRestBase = restBase + '/boxsizes';
  rest.boxSizes = {
    create: boxSizeRestBase,
    update: idUrlFunction(boxSizeRestBase)
  };

  // Box Uses
  var boxUseRestBase = restBase + '/boxuses';
  rest.boxUses = {
    create: boxUseRestBase,
    update: idUrlFunction(boxUseRestBase)
  };

  // Containers
  var containerUiBase = baseUrl + '/container';
  ui.containers = {
    edit: idUrlFunction(containerUiBase)
  };

  var containerRestBase = restBase + '/containers';
  rest.containers = {
    create: containerRestBase,
    update: idUrlFunction(containerRestBase),
    spreadsheet: containerRestBase + '/spreadsheet'
  };

  // Container Models
  var containerModelUiBase = baseUrl + '/containermodel';
  ui.containerModels = {
    bulkEdit: containerModelUiBase + '/bulk/edit'
  };

  var containerModelRestBase = restBase + '/containermodels';
  rest.containerModels = {
    create: containerModelRestBase,
    update: idUrlFunction(containerModelRestBase),
    search: containerModelRestBase + '/search'
  };

  // Detailed QC Statuses
  var detailedQcStatusRestBase = restBase + '/detailedqcstatuses';
  rest.detailedQcStatuses = {
    create: detailedQcStatusRestBase,
    update: idUrlFunction(detailedQcStatusRestBase)
  };

  // Experiments
  var experimentUiBase = baseUrl + '/experiment';
  ui.experiments = {
    edit: idUrlFunction(experimentUiBase)
  };

  // Freezers - REST components are under Storage Locations
  var freezerUiBase = baseUrl + '/freezer';
  ui.freezers = {
    create: freezerUiBase + '/new',
    edit: idUrlFunction(freezerUiBase)
  };

  // Freezer Maps
  var freezerMapUiBase = '/freezermap';
  ui.freezerMaps = {
    view: function(filename, anchor) {
      return freezerMapUiBase + '/' + filename + (anchor ? '#' + anchor : '');
    }
  };

  // Groups
  var groupUiBase = baseUrl + '/admin/group';
  ui.groups = {
    create: groupUiBase + '/new',
    edit: idUrlFunction(groupUiBase)
  };

  // Index Families
  var indexFamilyUiBase = baseUrl + '/indexfamily';
  ui.indexFamilies = {
    create: indexFamilyUiBase + '/new',
    edit: idUrlFunction(indexFamilyUiBase)
  };

  var indexFamilyRestBase = restBase + '/indexfamilies';
  rest.indexFamilies = {
    create: indexFamilyRestBase,
    update: idUrlFunction(indexFamilyRestBase)
  };

  // Indices
  var indexUiBase = baseUrl + '/index';
  ui.indices = {
    bulkCreate: indexUiBase + '/bulk/new',
    bulkEdit: indexUiBase + '/bulk/edit'
  };

  var indexRestBase = restBase + '/indices';
  rest.indices = {
    create: indexRestBase,
    update: idUrlFunction(indexRestBase),
    datatable: indexRestBase + '/dt',
    platformDatatable: idUrlFunction(indexRestBase + '/dt/platform')
  };

  // Institutes
  var instituteUiBase = baseUrl + '/institute'
  ui.institutes = {
    bulkCreate: instituteUiBase + '/bulk/new',
    bulkdEdit: instituteUiBase + '/bulk/edit'
  };

  var instituteRestBase = restBase + '/institutes';
  rest.institutes = {
    create: instituteRestBase,
    update: idUrlFunction(instituteRestBase)
  };

  // Instruments
  var instrumentUiBase = baseUrl + '/instrument';
  ui.instruments = {
    create: instrumentUiBase + '/new',
    edit: idUrlFunction(instrumentUiBase)
  };

  var instrumentRestBase = restBase + '/instruments';
  rest.instruments = {
    create: instrumentRestBase,
    update: idUrlFunction(instrumentRestBase),
    datatable: instrumentRestBase + '/dt',
    instrumentTypeDatatable: idUrlFunction(instrumentRestBase + '/dt/instrument-type'),
    list: instrumentRestBase
  };

  // Instrument Models
  var instrumentModelUiBase = baseUrl + '/instrumentmodel';
  ui.instrumentModels = {
    create: instrumentModelUiBase + '/new',
    edit: idUrlFunction(instrumentModelUiBase)
  };

  var instrumentModelRestBase = restBase + '/instrumentmodels';
  rest.instrumentModels = {
    create: instrumentModelRestBase,
    datatable: instrumentModelRestBase + '/dt',
    update: idUrlFunction(instrumentModelRestBase)
  };

  // Kit Descriptors
  var kitDescriptorUiBase = baseUrl + '/kitdescriptor';
  ui.kitDescriptors = {
    edit: idUrlFunction(kitDescriptorUiBase)
  };

  var kitDescriptorRestBase = restBase + '/kitdescriptors';
  rest.kitDescriptors = {
    updateTargetedSequencings: middleIdUrlFunction(kitDescriptorRestBase, '/targetedsequencing')
  };

  // Labs
  var labUiBase = baseUrl + '/lab';
  ui.labs = {
    bulkCreate: labUiBase + '/bulk/new',
    bulkEdit: labUiBase + '/bulk/edit'
  };

  var labRestBase = restBase + '/labs';
  rest.labs = {
    create: labRestBase,
    update: idUrlFunction(labRestBase)
  };

  // Libraries
  var libraryUiBase = baseUrl + '/library';
  ui.libraries = {
    edit: idUrlFunction(libraryUiBase),
    bulkReceive: libraryUiBase + '/bulk/receive',
    bulkEdit: libraryUiBase + '/bulk/edit',
    bulkPropagate: libraryUiBase + '/bulk/propagate'
  };

  var libraryRestBase = restBase + '/libraries';
  rest.libraries = {
    create: libraryRestBase,
    update: idUrlFunction(libraryRestBase),
    parents: idUrlFunction(libraryRestBase + '/parents'),
    children: idUrlFunction(libraryRestBase + '/children'),
    query: libraryRestBase + '/query',
    datatable: libraryRestBase + '/dt',
    projectDatatable: idUrlFunction(libraryRestBase + '/dt/project'),
    bulkDelete: libraryRestBase + '/bulk-delete',
    spreadsheet: libraryRestBase + '/spreadsheet'
  };

  // Library Aliquots
  var libraryAliquotUiBase = baseUrl + '/libraryaliquot';
  ui.libraryAliquots = {
    edit: idUrlFunction(libraryAliquotUiBase),
    bulkEdit: libraryAliquotUiBase + '/bulk/edit',
    bulkPropagate: libraryAliquotUiBase + '/bulk/propagate',
    bulkRepropagate: libraryAliquotUiBase + '/bulk/repropagate',
    bulkPoolTogether: libraryAliquotUiBase + '/bulk/merge',
    bulkPoolSeparate: libraryAliquotUiBase + '/bulk/pool-separate',
    bulkPoolCustom: libraryAliquotUiBase + '/bulk/pool'
  };

  var libraryAliquotRestBase = restBase + '/libraryaliquots';
  rest.libraryAliquots = {
    datatable: libraryAliquotRestBase + '/dt',
    projectDatatable: function(projectId) {
      return libraryAliquotRestBase + '/dt/project/' + projectId;
    },
    includedInPoolDatatable: function(poolId) {
      return libraryAliquotRestBase + '/dt/pool/' + poolId + '/included';
    },
    availableForPoolDatatable: function(poolId) {
      return libraryAliquotRestBase + '/dt/pool/' + poolId + '/available';
    },
    query: libraryAliquotRestBase + '/query',
    create: libraryAliquotRestBase,
    update: idUrlFunction(libraryAliquotRestBase),
    bulkDelete: libraryAliquotRestBase + '/bulk-delete',
    spreadsheet: libraryAliquotRestBase + '/spreadsheet',
    parents: idUrlFunction(libraryAliquotRestBase + '/parents'),
    children: idUrlFunction(libraryAliquotRestBase + '/children')
  };

  // Library Designs
  var libraryDesignRestBase = restBase + '/librarydesigns';
  rest.libraryDesigns = {
    create: libraryDesignRestBase,
    update: idUrlFunction(libraryDesignRestBase)
  };

  // Library Design Codes
  var libraryDesignCodeRestBase = restBase + '/librarydesigncodes';
  rest.libraryDesignCodes = {
    create: libraryDesignCodeRestBase,
    update: idUrlFunction(libraryDesignCodeRestBase)
  };

  // Library Selections
  var librarySelectionRestBase = restBase + '/libraryselections';
  rest.librarySelections = {
    create: librarySelectionRestBase,
    update: idUrlFunction(librarySelectionRestBase)
  };

  // Library Spike-Ins
  var librarySpikeInRestBase = restBase + '/libraryspikeins';
  rest.librarySpikeIns = {
    create: librarySpikeInRestBase,
    update: idUrlFunction(librarySpikeInRestBase)
  };

  // Library Strategies
  var libraryStrategyRestBase = restBase + '/librarystrategies';
  rest.libraryStrategies = {
    create: libraryStrategyRestBase,
    update: idUrlFunction(libraryStrategyRestBase)
  };

  // Library Templates
  var libraryTemplateUiBase = baseUrl + '/librarytemplate';
  ui.libraryTemplates = {
    create: libraryTemplateUiBase + '/new',
    edit: idUrlFunction(libraryTemplateUiBase),
    addIndices: middleIdUrlFunction(libraryTemplateUiBase, '/indices/add'),
    editIndices: middleIdUrlFunction(libraryTemplateUiBase, '/indices/edit')
  };

  var libraryTemplateRestBase = restBase + '/librarytemplates';
  rest.libraryTemplates = {
    create: libraryTemplateRestBase,
    update: idUrlFunction(libraryTemplateRestBase),
    query: libraryTemplateRestBase + '/query',
    datatable: libraryTemplateRestBase + '/dt',
    projectDatatable: idUrlFunction(libraryTemplateRestBase + '/dt/project'),
    bulkDelete: libraryTemplateRestBase + '/bulk-delete',
    addProject: libraryTemplateRestBase + '/project/add',
    removeProject: libraryTemplateRestBase + '/project/remove'
  };

  // Library Types
  var libraryTypeRestBase = restBase + '/librarytypes';
  rest.libraryTypes = {
    create: libraryTypeRestBase,
    update: idUrlFunction(libraryTypeRestBase)
  };

  // Partition QC Types
  var partitionQcTypeRestBase = restBase + '/partitionqctypes';
  rest.partitionQcTypes = {
    create: partitionQcTypeRestBase,
    update: idUrlFunction(partitionQcTypeRestBase)
  };

  // Pools
  var poolUiBase = baseUrl + '/pool';
  ui.pools = {
    edit: idUrlFunction(poolUiBase),
    bulkEdit: poolUiBase + '/bulk/edit',
    bulkMerge: poolUiBase + '/bulk/merge'
  };

  var poolRestBase = restBase + '/pools';
  rest.pools = {
    create: poolRestBase,
    update: idUrlFunction(poolRestBase),
    parents: idUrlFunction(poolRestBase + '/parents'),
    query: poolRestBase + '/query',
    search: poolRestBase + '/search',
    picker: {
      recent: poolRestBase + '/picker/recent',
      search: poolRestBase + '/picker/search'
    },
    assign: middleIdUrlFunction(poolRestBase, '/assign'),
    spreadsheet: poolRestBase + '/spreadsheet',
    contentsSpreadsheet: poolRestBase + '/contents/spreadsheet',
    samplesheet: poolRestBase + '/samplesheet'
  };

  // Pool Orders
  var poolOrderUiBase = baseUrl + '/poolorder';
  ui.poolOrders = {
    create: poolOrderUiBase + '/new',
    edit: idUrlFunction(poolOrderUiBase)
  };

  var poolOrderRestBase = restBase + '/poolorders';
  rest.poolOrders = {
    create: poolOrderRestBase,
    update: idUrlFunction(poolOrderRestBase),
    indexChecker: poolOrderRestBase + '/indexchecker'
  };

  // Printers
  var printerRestBase = restBase + '/printers';
  rest.printers = {
    datatable: printerRestBase + '/dt',
    create: printerRestBase,
    enable: printerRestBase + '/enable',
    disable: printerRestBase + '/disable'
  };

  // Projects
  var projectUiBase = baseUrl + '/project';
  ui.projects = {
    create: projectUiBase + '/new',
    edit: idUrlFunction(projectUiBase)
  };

  var projectRestBase = restBase + '/projects';
  rest.projects = {
    get: idUrlFunction(projectRestBase),
    create: projectRestBase,
    update: idUrlFunction(projectRestBase),
    search: projectRestBase + '/search'
  };

  // QCs
  var qcUiBase = baseUrl + '/qc';
  ui.qcs = {
    bulkAddFrom: idUrlFunction(qcUiBase + '/bulk/addFrom'),
    bulkEdit: idUrlFunction(qcUiBase + '/bulk/edit'),
    bulkEditFrom: idUrlFunction(qcUiBase + '/bulk/editFrom')
  };

  var qcRestBase = restBase + '/qcs';
  rest.qcs = {
    create: qcRestBase,
    update: idUrlFunction(qcRestBase)
  };

  // QC Types
  var qcTypeUiBase = baseUrl + '/qctype';
  ui.qcTypes = {
    create: qcTypeUiBase + '/new',
    edit: idUrlFunction(qcTypeUiBase),
    bulkCreate: qcTypeUiBase + '/bulk/new',
    bulkEdit: qcTypeUiBase + '/bulk/edit'
  };

  var qcTypeRestBase = restBase + '/qctypes';
  rest.qcTypes = {
    create: qcTypeRestBase,
    update: idUrlFunction(qcTypeRestBase)
  };

  // Reference Genomes
  var referenceGenomeRestBase = restBase + '/referencegenomes';
  rest.referenceGenomes = {
    create: referenceGenomeRestBase,
    update: idUrlFunction(referenceGenomeRestBase)
  };

  // Runs
  var runUiBase = baseUrl + '/run'
  ui.runs = {
    create: idUrlFunction(runUiBase + '/new'),
    edit: idUrlFunction(runUiBase)
  };

  var runRestBase = restBase + '/runs';
  rest.runs = {
    create: runRestBase,
    update: idUrlFunction(runRestBase),
    datatable: runRestBase + '/dt',
    projectDatatable: idUrlFunction(runRestBase + '/dt/project'),
    sequencerDatatable: idUrlFunction(runRestBase + '/dt/sequencer'),
    platformDatatable: idUrlFunction(runRestBase + '/dt/platform'),
    setPartitionQcs: middleIdUrlFunction(runRestBase, '/qc'),
    setPartitionPurposes: middleIdUrlFunction(runRestBase, '/partition-purposes'),
    setAliquotPurposes: middleIdUrlFunction(runRestBase, '/aliquot-purposes'),
    addContainer: middleIdUrlFunction(runRestBase, '/add'),
    removeContainers: middleIdUrlFunction(runRestBase, '/remove')
  };

  // Run Purposes
  var runPurposeBase = baseUrl + '/runpurpose';
  ui.runPurposes = {
    bulkEdit: runPurposeBase + '/bulk/edit'
  };

  var runPurposeRestBase = restBase + '/runpurposes';
  rest.runPurposes = {
    create: runPurposeRestBase,
    update: idUrlFunction(runPurposeRestBase)
  };

  // Samples
  var sampleUiBase = baseUrl + '/sample';
  ui.samples = {
    edit: idUrlFunction(sampleUiBase),
    bulkCreate: sampleUiBase + '/bulk/new',
    bulkEdit: sampleUiBase + '/bulk/edit',
    bulkPropagate: sampleUiBase + '/bulk/propagate'
  };

  var sampleRestBase = restBase + '/samples';
  rest.samples = {
    create: sampleRestBase,
    update: idUrlFunction(sampleRestBase),
    parents: idUrlFunction(sampleRestBase + '/parents'),
    children: idUrlFunction(sampleRestBase + '/children'),
    query: sampleRestBase + '/query',
    datatable: sampleRestBase + '/dt',
    projectDatatable: idUrlFunction(sampleRestBase + '/dt/project'),
    projectArrayedDatatable: middleIdUrlFunction(sampleRestBase + '/dt/project', '/arrayed'),
    bulkDelete: sampleRestBase + '/bulk-delete',
    identitiesLookup: sampleRestBase + '/identitiesLookup',
    spreadsheet: sampleRestBase + '/spreadsheet'
  };

  // Sample Classes
  var sampleClassUiBase = baseUrl + '/sampleclass';
  ui.sampleClasses = {
    create: sampleClassUiBase + '/new',
    edit: idUrlFunction(sampleClassUiBase)
  };

  var sampleClassRestBase = restBase + '/sampleclasses';
  rest.sampleClasses = {
    create: sampleClassRestBase,
    update: idUrlFunction(sampleClassRestBase)
  };

  // Sample Purposes
  var samplePurposeUiBase = baseUrl + '/samplepurpose';
  ui.samplePurposes = {
    bulkCreate: samplePurposeUiBase + '/bulk/new',
    bulkEdit: samplePurposeUiBase + '/bulk/edit'
  };

  var samplePurposeRestBase = restBase + '/samplepurposes';
  rest.samplePurposes = {
    create: samplePurposeRestBase,
    update: idUrlFunction(samplePurposeRestBase)
  };

  // Sample Types
  var sampleTypeRestBase = restBase + '/sampletypes';
  rest.sampleTypes = {
    create: sampleTypeRestBase,
    update: idUrlFunction(sampleTypeRestBase)
  };

  // Sequencing Orders
  var sequencingOrderUiBase = baseUrl + '/sequencingorder';
  ui.sequencingOrders = {
    bulkCreate: sequencingOrderUiBase + '/bulk/create'
  };

  var sequencingOrderRestBase = restBase + '/sequencingorders';
  rest.sequencingOrders = {
    create: sequencingOrderRestBase,
    update: idUrlFunction(sequencingOrderRestBase),
    search: sequencingOrderRestBase + '/search',
    picker: {
      chemistry: sequencingOrderRestBase + '/picker/chemistry',
      active: sequencingOrderRestBase + '/picker/active'
    }
  };

  // Sequencing Parameters
  var sequencingParametersUiBase = baseUrl + '/sequencingparameters';
  ui.sequencingParameters = {
    bulkEdit: sequencingParametersUiBase + '/bulk/edit'
  };

  var sequencingParametersRestBase = restBase + '/sequencingparameters';
  rest.sequencingParameters = {
    create: sequencingParametersRestBase,
    update: idUrlFunction(sequencingParametersRestBase)
  };

  // Service Records
  var serviceRecordUiBase = baseUrl + '/instrument/servicerecord';
  ui.serviceRecords = {
    edit: idUrlFunction(serviceRecordUiBase)
  };

  // Stains
  var stainRestBase = restBase + '/stains';
  rest.stains = {
    create: stainRestBase,
    update: idUrlFunction(stainRestBase)
  };

  // Stain Categories
  var stainCategoryRestBase = restBase + '/staincategories';
  rest.stainCategories = {
    create: stainCategoryRestBase,
    update: idUrlFunction(stainCategoryRestBase)
  };

  // Storage Locations
  var storageLocationRestBase = restBase + '/storagelocations';
  function createStorageFunction(storageType) {
    return function(freezerId, shelfId) {
      var url = storageLocationRestBase + '/freezers/' + freezerId;
      if (shelfId) {
        url += '/shelves/' + shelfId;
      }
      url += '/' + storageType;
      return url;
    }
  }

  rest.storageLocations = {
    children: middleIdUrlFunction(storageLocationRestBase, '/children'),
    createFreezer: storageLocationRestBase + '/freezers',
    createLooseStorage: createStorageFunction('loose'),
    createRack: createStorageFunction('racks'),
    createRoom: storageLocationRestBase + '/rooms',
    createShelf: createStorageFunction('shelves'),
    createStack: createStorageFunction('stacks'),
    createTrayRack: createStorageFunction('tray-racks'),
    deleteComponent: idUrlFunction(storageLocationRestBase),
    freezers: storageLocationRestBase + '/freezers',
    updateComponent: idUrlFunction(storageLocationRestBase),
    updateFreezer: idUrlFunction(storageLocationRestBase + '/freezers'),
    queryByBarcode: storageLocationRestBase + '/bybarcode'
  };

  // Studies
  var studyUiBase = baseUrl + '/study';
  ui.studies = {
    create: studyUiBase + '/new',
    createInProject: idUrlFunction(studyUiBase + '/new'),
    edit: idUrlFunction(studyUiBase)
  };

  var studyRestBase = restBase + '/studies';
  rest.studies = {
    datatable: studyRestBase + '/dt',
    projectDatatable: idUrlFunction(studyRestBase + '/dt/project')
  };

  // Study Types
  var studyTypeRestBase = restBase + '/studytypes';
  rest.studyTypes = {
    create: studyTypeRestBase,
    update: idUrlFunction(studyTypeRestBase)
  };

  // Submissions
  var submissionUiBase = baseUrl + '/submission';
  ui.submissions = {
    edit: idUrlFunction(submissionUiBase)
  };

  // Subprojects
  var subprojectUiBase = baseUrl + '/subproject';
  ui.subprojects = {
    bulkCreate: subprojectUiBase + '/bulk/new',
    bulkEdit: subprojectUiBase + '/bulk/edit'
  };

  var subprojectRestBase = restBase + '/subprojects';
  rest.subprojects = {
    create: subprojectRestBase,
    update: idUrlFunction(subprojectRestBase)
  };

  // Targeted Sequencings
  var targetedSequencingUiBase = baseUrl + '/targetedsequencing';
  ui.targetedSequencings = {
    bulkEdit: targetedSequencingUiBase + '/bulk/edit'
  };

  var targetedSequencingRestBase = restBase + '/targetedsequencings';
  rest.targetedSequencings = {
    create: targetedSequencingRestBase,
    update: idUrlFunction(targetedSequencingRestBase),
    kitAvailableDatatable: middleIdUrlFunction(targetedSequencingRestBase + '/dt/kit', '/available'),
    kitIncludedDatatable: middleIdUrlFunction(targetedSequencingRestBase + '/dt/kit', '/included')
  };

  // Tissue Materials
  var tissueMaterialUiBase = baseUrl + '/tissuematerial';
  ui.tissueMaterials = {
    bulkCreate: tissueMaterialUiBase + '/bulk/new',
    bulkEdit: tissueMaterialUiBase + '/bulk/edit'
  };

  var tissueMaterialRestBase = restBase + '/tissuematerials';
  rest.tissueMaterials = {
    create: tissueMaterialRestBase,
    update: idUrlFunction(tissueMaterialRestBase)
  };

  // Tissue Origins
  var tissueOriginUiBase = baseUrl + '/tissueorigin';
  ui.tissueOrigins = {
    bulkCreate: tissueOriginUiBase + '/bulk/new',
    bulkEdit: tissueOriginUiBase + '/bulk/edit'
  };

  var tissueOriginRestBase = restBase + '/tissueorigins';
  rest.tissueOrigins = {
    create: tissueOriginRestBase,
    update: idUrlFunction(tissueOriginRestBase)
  };

  // Tissue Piece Types
  var tissuePieceTypesRestBase = restBase + '/tissuepiecetypes';
  rest.tissuePieceTypes = {
    create: tissuePieceTypesRestBase,
    update: idUrlFunction(tissuePieceTypesRestBase)
  };
  var tissuePieceTypesUiBase = baseUrl + '/tissuepiecetype';
  ui.tissuePieceTypes = {
    bulkEdit: tissuePieceTypesUiBase + '/bulk/edit',
  };

  // Tissue Types
  var tissueTypeRestBase = restBase + '/tissuetypes';
  rest.tissueTypes = {
    create: tissueTypeRestBase,
    update: idUrlFunction(tissueTypeRestBase)
  };

  // Transfers
  var transferUiBase = baseUrl + '/transfer';
  ui.transfers = {
    create: transferUiBase + '/new',
    edit: idUrlFunction(transferUiBase)
  };

  var transferRestBase = restBase + '/transfers';
  rest.transfers = {
    datatable: idUrlFunction(transferRestBase + '/dt'),
    create: transferRestBase,
    update: idUrlFunction(transferRestBase)
  };

  // Worksets
  var worksetUiBase = baseUrl + '/workset';
  ui.worksets = {
    edit: idUrlFunction(worksetUiBase)
  };

  var worksetRestBase = restBase + '/worksets';
  rest.worksets = {
    addSamples: middleIdUrlFunction(worksetRestBase, '/samples'),
    addLibraries: middleIdUrlFunction(worksetRestBase, '/libraries'),
    addLibraryAliquots: middleIdUrlFunction(worksetRestBase, '/libraryaliquots'),
    removeSamples: middleIdUrlFunction(worksetRestBase, '/samples'),
    removeLibraries: middleIdUrlFunction(worksetRestBase, '/libraries'),
    removeLibraryAliquots: middleIdUrlFunction(worksetRestBase, '/libraryaliquots'),
    moveSamples: middleIdUrlFunction(worksetRestBase, '/samples/move'),
    moveLibraries: middleIdUrlFunction(worksetRestBase, '/libraries/move'),
    moveLibraryAliquots: middleIdUrlFunction(worksetRestBase, '/libraryaliquots/move')
  };

  // External sites
  external.userManual = function(version, section, subsection) {
    var url = 'https://miso-lims.readthedocs.io/projects/docs/en/' + version + '/user_manual/';
    if (section) {
      url += section + '/';
      if (subsection) {
        url += '#' + subsection;
      }
    }
    return url;
  };

  external.enaAccession = idUrlFunction('http://www.ebi.ac.uk/ena/data/view');

  return {
    ui: ui,
    rest: rest,
    external: external
  };

})();
