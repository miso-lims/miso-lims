Urls = (function() {

  function idUrlFunction(baseUrl) {
    return function(id) {
      return baseUrl + '/' + id;
    }
  }

  var baseUrl = '/miso';
  var restBase = baseUrl + '/rest';

  var ui = {};
  var rest = {};

  // Arrays
  var arrayUiBase = baseUrl + '/array';
  ui.arrays = {
    edit: idUrlFunction(arrayUiBase)
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
    edit: idUrlFunction(arrayRunUiBase)
  };

  // Attachment Categories
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
    update: idUrlFunction(boxRestBase)
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

  // Institutes
  var instituteRestBase = restBase + '/institutes';
  rest.institutes = {
    create: instituteRestBase,
    update: idUrlFunction(instituteRestBase)
  };

  // Instruments
  var instrumentUiBase = baseUrl + '/instrument';
  ui.instruments = {
    edit: idUrlFunction(instrumentUiBase)
  }

  // Kit Descriptors
  var kitDescriptorUiBase = baseUrl + '/kitdescriptor';
  ui.kitDescriptors = {
    edit: idUrlFunction(kitDescriptorUiBase)
  }

  // Labs
  var labRestBase = restBase + '/labs';
  rest.labs = {
    create: labRestBase,
    update: idUrlFunction(labRestBase)
  };

  // Libraries
  var libraryUiBase = baseUrl + '/library';
  ui.libraries = {
    edit: idUrlFunction(libraryUiBase),
    bulkReceive: libraryUiBase + '/bulk/receive'
  };

  var libraryRestBase = restBase + '/libraries';
  rest.libraries = {
    create: libraryRestBase,
    update: idUrlFunction(libraryRestBase),
    parents: idUrlFunction(libraryRestBase + '/parents'),
    children: idUrlFunction(libraryRestBase + '/children'),
    query: libraryRestBase + '/query'
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
  var libraryTemplateRestBase = restBase + '/librarytemplates';
  rest.libraryTemplates = {
    create: libraryTemplateRestBase,
    update: idUrlFunction(libraryTemplateRestBase),
    query: libraryTemplateRestBase + '/query'
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
    bulkMerge: poolUiBase + '/bulk/merge'
  };

  var poolRestBase = restBase + '/pools';
  rest.pools = {
    create: poolRestBase,
    update: idUrlFunction(poolRestBase),
    parents: idUrlFunction(poolRestBase + '/parents'),
    query: poolRestBase + '/query'
  };

  // Projects
  var projectUiBase = baseUrl + '/project';
  ui.projects = {
    edit: idUrlFunction(projectUiBase)
  };

  // QCs
  var qcRestBase = restBase + '/qcs';
  rest.qcs = {
    create: qcRestBase,
    update: idUrlFunction(qcRestBase)
  };

  // QC Types
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
    edit: idUrlFunction(runUiBase)
  };

  // Samples
  var sampleUiBase = baseUrl + '/sample';
  ui.samples = {
    edit: idUrlFunction(sampleUiBase),
    bulkCreate: sampleUiBase + '/bulk/new'
  };

  var sampleRestBase = restBase + '/samples';
  rest.samples = {
    create: sampleRestBase,
    update: idUrlFunction(sampleRestBase),
    parents: idUrlFunction(sampleRestBase + '/parents'),
    children: idUrlFunction(sampleRestBase + '/children'),
    query: sampleRestBase + '/query'
  };

  // Sample Purposes
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
  var sequencingOrderRestBase = restBase + '/sequencingorders';
  rest.sequencingOrders = {
    create: sequencingOrderRestBase,
    update: idUrlFunction(sequencingOrderRestBase)
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

  // Studies
  var studyUiBase = baseUrl + '/study';
  ui.studies = {
    edit: idUrlFunction(studyUiBase)
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
  var subprojectRestBase = restBase + '/subprojects';
  rest.subprojects = {
    create: subprojectRestBase,
    update: idUrlFunction(subprojectRestBase)
  };

  // Tissue Materials
  var tissueMaterialRestBase = restBase + '/tissuematerials';
  rest.tissueMaterials = {
    create: tissueMaterialRestBase,
    update: idUrlFunction(tissueMaterialRestBase)
  };

  // Tissue Origins
  var tissueOriginRestBase = restBase + '/tissueorigins';
  rest.tissueOrigins = {
    create: tissueOriginRestBase,
    update: idUrlFunction(tissueOriginRestBase)
  };

  // Tissue Types
  var tissueTypeRestBase = restBase + '/tissuetypes';
  rest.tissueTypes = {
    create: tissueTypeRestBase,
    update: idUrlFunction(tissueTypeRestBase)
  };

  // Worksets
  var worksetUiBase = baseUrl + '/workset';
  ui.worksets = {
    edit: idUrlFunction(worksetUiBase)
  };

  var worksetRestBase = restBase + '/worksets';
  rest.worksets = {
    addSamples: function(worksetId) {
      return worksetRestBase + '/' + worksetId + '/samples';
    },
    addLibraries: function(worksetId) {
      return worksetRestBase + '/' + worksetId + '/libraries';
    },
    addLibraryAliquots: function(worksetId) {
      return worksetRestBase + '/' + worksetId + '/libraryaliquots';
    },
    removeSamples: function(worksetId) {
      return worksetRestBase + '/' + worksetId + '/samples';
    },
    removeLibraries: function(worksetId) {
      return worksetRestBase + '/' + worksetId + '/libraries';
    },
    removeLibraryAliquots: function(worksetId) {
      return worksetRestBase + '/' + worksetId + '/libraryaliquots';
    }
  };

  return {
    ui: ui,
    rest: rest
  };

})();
