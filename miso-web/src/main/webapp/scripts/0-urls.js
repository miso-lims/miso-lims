Urls = (function () {
  function idUrlFunction(baseUrl) {
    return function (id) {
      return baseUrl + "/" + id;
    };
  }

  function middleIdUrlFunction(prefix, suffix) {
    return function (id) {
      return prefix + "/" + id + suffix;
    };
  }

  var restBase = "/rest";

  var ui = {};
  var rest = {};
  var download = {};
  var upload = {};
  var external = {};

  // Admin
  var adminRestBase = restBase + "/admin";
  rest.admin = {
    clearHibernateCache: adminRestBase + "/cache/clear",
    refreshConstants: adminRestBase + "/constants/refresh",
    regenerateBarcodes: adminRestBase + "/barcode/regen",
  };

  // API Keys
  var apiKeyRestBase = restBase + "/apikeys";
  rest.apiKeys = {
    create: apiKeyRestBase,
  };

  // Arrays
  var arrayUiBase = "/array";
  ui.arrays = {
    create: arrayUiBase + "/new",
    edit: idUrlFunction(arrayUiBase),
  };

  var arrayRestBase = restBase + "/arrays";
  rest.arrays = {
    changelog: middleIdUrlFunction(arrayRestBase, "/changelog"),
    create: arrayRestBase,
    update: idUrlFunction(arrayRestBase),
    datatable: arrayRestBase + "/dt",
    position: function (arrayId, position) {
      return arrayRestBase + "/" + arrayId + "/positions/" + position;
    },
    sampleSearch: arrayRestBase + "/sample-search",
  };

  // Array Models
  var arrayModelUiBase = "/arraymodel";
  ui.arrayModels = {
    bulkCreate: arrayModelUiBase + "/bulk/new",
    bulkEdit: arrayModelUiBase + "/bulk/edit",
  };

  var arrayModelRestBase = restBase + "/arraymodels";
  rest.arrayModels = {
    bulkSave: arrayModelRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(arrayModelRestBase + "/bulk"),
  };

  // Array Runs
  var arrayRunUiBase = "/arrayrun";
  ui.arrayRuns = {
    create: arrayRunUiBase + "/new",
    edit: idUrlFunction(arrayRunUiBase),
  };

  var arrayRunRestBase = restBase + "/arrayruns";
  rest.arrayRuns = {
    create: arrayRunRestBase,
    update: idUrlFunction(arrayRunRestBase),
    datatable: arrayRunRestBase + "/dt",
    projectDatatable: idUrlFunction(arrayRunRestBase + "/dt/project"),
    requisitionDatatable: idUrlFunction(arrayRunRestBase + "/dt/requisition"),
    arraySearch: arrayRunRestBase + "/array-search",
  };

  // Assays
  var assayUiBase = "/assay";
  ui.assays = {
    create: assayUiBase + "/new",
    edit: idUrlFunction(assayUiBase),
  };

  var assayRestBase = restBase + "/assays";
  rest.assays = {
    create: assayRestBase,
    update: idUrlFunction(assayRestBase),
  };

  // Assay Tests
  var assayTestUiBase = "/assaytest";
  ui.assayTests = {
    bulkCreate: assayTestUiBase + "/bulk/new",
    bulkEdit: assayTestUiBase + "/bulk/edit",
  };

  var assayTestRestBase = restBase + "/assaytests";
  rest.assayTests = {
    bulkSave: assayTestRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(assayTestRestBase + "/bulk"),
  };

  // Attachments
  var attachmentRestBase = restBase + "/attachments";
  rest.attachments = {
    delete: function (entityType, entityId, attachmentId) {
      return attachmentRestBase + "/" + entityType + "/" + entityId + "/" + attachmentId;
    },
    link: function (entityType, entityId) {
      return attachmentRestBase + "/" + entityType + "/" + entityId;
    },
  };

  // Attachment Categories
  var attachmentCategoryUiBase = "/attachmentcategories";
  ui.attachmentCategories = {
    bulkCreate: attachmentCategoryUiBase + "/bulk/new",
    bulkEdit: attachmentCategoryUiBase + "/bulk/edit",
  };

  var attachmentCategoryRestBase = restBase + "/attachmentcategories";
  rest.attachmentCategories = {
    bulkSave: attachmentCategoryRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(attachmentCategoryRestBase + "/bulk"),
  };

  // Barcodables
  var barcodableRestBase = restBase + "/barcodables";
  rest.barcodables = {
    search: barcodableRestBase + "/search",
  };

  // Boxes
  var boxUiBase = "/box";
  ui.boxes = {
    create: boxUiBase + "/new",
    edit: idUrlFunction(boxUiBase),
    bulkCreate: boxUiBase + "/bulk/new",
    bulkEdit: boxUiBase + "/bulk/edit",
  };

  var boxRestBase = restBase + "/boxes";
  rest.boxes = {
    bulkSave: boxRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(boxRestBase + "/bulk"),
    create: boxRestBase,
    update: idUrlFunction(boxRestBase),
    updatePosition: function (boxId, position) {
      return boxRestBase + "/" + boxId + "/position/" + position;
    },
    removePosition: function (boxId, position) {
      return boxRestBase + "/" + boxId + "/positions/" + position;
    },
    discardPosition: function (boxId, position) {
      return boxRestBase + "/" + boxId + "/positions/" + position + "/discard";
    },
    updateContents: middleIdUrlFunction(boxRestBase, "/bulk-update"),
    removeContents: middleIdUrlFunction(boxRestBase, "/bulk-remove"),
    discardContents: middleIdUrlFunction(boxRestBase, "/bulk-discard"),
    discardAll: middleIdUrlFunction(boxRestBase, "/discard-all"),
    searchPartial: boxRestBase + "/search/partial",
    setLocation: middleIdUrlFunction(boxRestBase, "/setlocation"),
    datatable: boxRestBase + "/dt",
    useDatatable: idUrlFunction(boxRestBase + "/dt/use"),
    bulkDelete: boxRestBase + "/bulk-delete",
    fillByPattern: middleIdUrlFunction(boxRestBase, "/positions/fill-by-pattern"),
    prepareScan: boxRestBase + "/prepare-scan",
    scan: middleIdUrlFunction(boxRestBase, "/scan"),
    spreadsheet: middleIdUrlFunction(boxRestBase, "/spreadsheet"),
    boxSpreadsheet: boxRestBase + "/spreadsheet",
    fragmentAnalyserSheet: middleIdUrlFunction(boxRestBase, "/fragmentAnalyser"),
  };

  // Boxables
  var boxablesRestBase = restBase + "/boxables";
  rest.boxables = {
    search: boxablesRestBase + "/search",
    queryByBox: boxablesRestBase + "/query-by-box",
  };

  // Box Sizes
  var boxSizeUiBase = "/boxsize";
  ui.boxSizes = {
    bulkCreate: boxSizeUiBase + "/bulk/new",
    bulkEdit: boxSizeUiBase + "/bulk/edit",
  };

  var boxSizeRestBase = restBase + "/boxsizes";
  rest.boxSizes = {
    bulkSave: boxSizeRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(boxSizeRestBase + "/bulk"),
  };

  // Box Uses
  var boxUseUiBase = "/boxuse";
  ui.boxUses = {
    bulkCreate: boxUseUiBase + "/bulk/new",
    bulkEdit: boxUseUiBase + "/bulk/edit",
  };

  var boxUseRestBase = restBase + "/boxuses";
  rest.boxUses = {
    bulkSave: boxUseRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(boxUseRestBase + "/bulk"),
  };

  // Contacts
  var contactUiBase = "/contact";
  ui.contacts = {
    bulkCreate: contactUiBase + "/bulk/new",
    bulkEdit: contactUiBase + "/bulk/edit",
  };

  var contactRestBase = restBase + "/contacts";
  rest.contacts = {
    search: contactRestBase,
    bulkSave: contactRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(contactRestBase + "/bulk"),
  };

  // Containers
  var containerUiBase = "/container";
  ui.containers = {
    create: idUrlFunction(containerUiBase + "/new"),
    edit: idUrlFunction(containerUiBase),
  };

  var containerRestBase = restBase + "/containers";
  rest.containers = {
    create: containerRestBase,
    update: idUrlFunction(containerRestBase),
    spreadsheet: containerRestBase + "/spreadsheet",
    datatable: containerRestBase + "/dt",
    platformDatatable: idUrlFunction(containerRestBase + "/dt/platform"),
    spreadsheet: containerRestBase + "/spreadsheet",
    bulkDelete: containerRestBase + "/bulk-delete",
  };

  // Container Models
  var containerModelUiBase = "/containermodel";
  ui.containerModels = {
    bulkCreate: containerModelUiBase + "/bulk/new",
    bulkEdit: containerModelUiBase + "/bulk/edit",
  };

  var containerModelRestBase = restBase + "/containermodels";
  rest.containerModels = {
    bulkSave: containerModelRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(containerModelRestBase + "/bulk"),
    search: containerModelRestBase + "/search",
  };

  // Deletions
  var deletionRestBase = restBase + "/deletions";
  rest.deletions = {
    datatable: deletionRestBase + "/dt",
  };

  // Detailed QC Statuses
  var detailedQcStatusUiBase = "/detailedqcstatus";
  ui.detailedQcStatuses = {
    bulkCreate: detailedQcStatusUiBase + "/bulk/new",
    bulkEdit: detailedQcStatusUiBase + "/bulk/edit",
  };

  var detailedQcStatusRestBase = restBase + "/detailedqcstatuses";
  rest.detailedQcStatuses = {
    bulkSave: detailedQcStatusRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(detailedQcStatusRestBase + "/bulk"),
  };

  // Experiments
  var experimentUiBase = "/experiment";
  ui.experiments = {
    edit: idUrlFunction(experimentUiBase),
  };

  var experimentRestBase = restBase + "/experiments";
  rest.experiments = {
    create: experimentRestBase,
    update: idUrlFunction(experimentRestBase),
    addKit: middleIdUrlFunction(experimentRestBase, "/addkit"),
    addRunPartition: middleIdUrlFunction(experimentRestBase, "/add"),
  };

  // Freezers - REST components are under Storage Locations
  var freezerUiBase = "/freezer";
  ui.freezers = {
    create: freezerUiBase + "/new",
    edit: idUrlFunction(freezerUiBase),
    createRecord: function (locationId) {
      return freezerUiBase + "/" + locationId + "/servicerecord/new";
    },
    editRecord: function (locationId, recordId) {
      return freezerUiBase + "/" + locationId + "/servicerecord/" + recordId;
    },
  };

  // Freezer Maps
  var freezerMapUiBase = "/freezermaps";
  ui.freezerMaps = {
    view: function (filename, anchor) {
      return freezerMapUiBase + "/" + filename + (anchor ? "#" + anchor : "");
    },
  };

  // Groups
  var groupUiBase = "/admin/group";
  ui.groups = {
    create: groupUiBase + "/new",
    edit: idUrlFunction(groupUiBase),
  };

  var groupRestBase = restBase + "/groups";
  rest.groups = {
    create: groupRestBase,
    update: idUrlFunction(groupRestBase),
    addUsers: middleIdUrlFunction(groupRestBase, "/users"),
    removeUsers: middleIdUrlFunction(groupRestBase, "/users/remove"),
  };

  // Handsontables
  var hotRestBase = restBase + "/hot";
  rest.hot = {
    bulkImport: hotRestBase + "/import",
    bulkExport: hotRestBase + "/spreadsheet",
  };

  // Index Distance Tool
  var indexDistanceRestBase = restBase + "/indexdistance";
  rest.indexDistance = {
    check: indexDistanceRestBase,
  };

  // Instruments
  var instrumentUiBase = "/instrument";
  ui.instruments = {
    create: instrumentUiBase + "/new",
    edit: idUrlFunction(instrumentUiBase),
    editRecord: function (instrumentId, recordId) {
      return instrumentUiBase + "/" + instrumentId + "/servicerecord/" + recordId;
    },
    createRecord: function (instrumentId) {
      return instrumentUiBase + "/" + instrumentId + "/servicerecord/new";
    },
  };

  var instrumentRestBase = restBase + "/instruments";
  rest.instruments = {
    create: instrumentRestBase,
    update: idUrlFunction(instrumentRestBase),
    workstationDatatable: idUrlFunction(instrumentRestBase + "/dt/workstation"),
    datatable: instrumentRestBase + "/dt",
    instrumentTypeDatatable: idUrlFunction(instrumentRestBase + "/dt/instrument-type"),
    list: instrumentRestBase,
    createRecord: function (instrumentId) {
      return instrumentRestBase + "/" + instrumentId + "/servicerecords";
    },
  };

  // Instrument Models
  var instrumentModelUiBase = "/instrumentmodel";
  ui.instrumentModels = {
    create: instrumentModelUiBase + "/new",
    edit: idUrlFunction(instrumentModelUiBase),
  };

  var instrumentModelRestBase = restBase + "/instrumentmodels";
  rest.instrumentModels = {
    create: instrumentModelRestBase,
    datatable: instrumentModelRestBase + "/dt",
    update: idUrlFunction(instrumentModelRestBase),
  };

  // Instrument Status
  rest.instrumentStatus = restBase + "/instrumentstatus";

  // Kit Descriptors
  var kitDescriptorUiBase = "/kitdescriptor";
  ui.kitDescriptors = {
    create: kitDescriptorUiBase + "/new",
    edit: idUrlFunction(kitDescriptorUiBase),
  };

  var kitDescriptorRestBase = restBase + "/kitdescriptors";
  rest.kitDescriptors = {
    create: kitDescriptorRestBase,
    update: idUrlFunction(kitDescriptorRestBase),
    updateTargetedSequencings: middleIdUrlFunction(kitDescriptorRestBase, "/targetedsequencing"),
    datatable: kitDescriptorRestBase + "/dt",
    typeDatatable: idUrlFunction(kitDescriptorRestBase + "/dt/type"),
    updateTargetedSequencings: middleIdUrlFunction(kitDescriptorRestBase, "/targetedsequencing"),
    search: kitDescriptorRestBase + "/search",
  };

  // Labs
  var labUiBase = "/lab";
  ui.labs = {
    bulkCreate: labUiBase + "/bulk/new",
    bulkEdit: labUiBase + "/bulk/edit",
  };

  var labRestBase = restBase + "/labs";
  rest.labs = {
    bulkSave: labRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(labRestBase + "/bulk"),
  };

  // Libraries
  var libraryUiBase = "/library";
  ui.libraries = {
    edit: idUrlFunction(libraryUiBase),
    bulkReceive: libraryUiBase + "/bulk/receive",
    bulkEdit: libraryUiBase + "/bulk/edit",
    bulkPropagate: libraryUiBase + "/bulk/propagate",
    batch: function (id) {
      return libraryUiBase + "/batch/" + encodeURIComponent(id);
    },
    qcHierarchy: middleIdUrlFunction(libraryUiBase, "/qc-hierarchy"),
  };

  var libraryRestBase = restBase + "/libraries";
  rest.libraries = {
    create: libraryRestBase,
    update: idUrlFunction(libraryRestBase),
    parents: idUrlFunction(libraryRestBase + "/parents"),
    children: idUrlFunction(libraryRestBase + "/children"),
    query: libraryRestBase + "/query",
    datatable: libraryRestBase + "/dt",
    workstationDatatable: idUrlFunction(libraryRestBase + "/dt/workstation"),
    projectDatatable: idUrlFunction(libraryRestBase + "/dt/project"),
    batchDatatable: idUrlFunction(libraryRestBase + "/dt/batch"),
    requisitionDatatable: idUrlFunction(libraryRestBase + "/dt/requisition"),
    requisitionSupplementalDatatable: idUrlFunction(
      libraryRestBase + "/dt/requisition-supplemental"
    ),
    requisitionRelatedDatatable: idUrlFunction(libraryRestBase + "/dt/requisition-prepared"),
    bulkDelete: libraryRestBase + "/bulk-delete",
    spreadsheet: libraryRestBase + "/spreadsheet",
    bulkSave: libraryRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(libraryRestBase + "/bulk"),
    worksetDatatable: idUrlFunction(libraryRestBase + "/dt/workset"),
    findRelated: libraryRestBase + "/find-related",
  };

  // Library Aliquots
  var libraryAliquotUiBase = "/libraryaliquot";
  ui.libraryAliquots = {
    edit: idUrlFunction(libraryAliquotUiBase),
    bulkEdit: libraryAliquotUiBase + "/bulk/edit",
    bulkPropagate: libraryAliquotUiBase + "/bulk/propagate",
    bulkRepropagate: libraryAliquotUiBase + "/bulk/repropagate",
    bulkPoolTogether: libraryAliquotUiBase + "/bulk/merge",
    bulkPoolSeparate: libraryAliquotUiBase + "/bulk/pool-separate",
    bulkPoolCustom: libraryAliquotUiBase + "/bulk/pool",
    qcHierarchy: middleIdUrlFunction(libraryAliquotUiBase, "/qc-hierarchy"),
  };

  var libraryAliquotRestBase = restBase + "/libraryaliquots";
  rest.libraryAliquots = {
    datatable: libraryAliquotRestBase + "/dt",
    projectDatatable: function (projectId) {
      return libraryAliquotRestBase + "/dt/project/" + projectId;
    },
    includedInPoolDatatable: function (poolId) {
      return libraryAliquotRestBase + "/dt/pool/" + poolId + "/included";
    },
    availableForPoolDatatable: function (poolId) {
      return libraryAliquotRestBase + "/dt/pool/" + poolId + "/available";
    },
    query: libraryAliquotRestBase + "/query",
    create: libraryAliquotRestBase,
    update: idUrlFunction(libraryAliquotRestBase),
    bulkDelete: libraryAliquotRestBase + "/bulk-delete",
    spreadsheet: libraryAliquotRestBase + "/spreadsheet",
    parents: idUrlFunction(libraryAliquotRestBase + "/parents"),
    children: idUrlFunction(libraryAliquotRestBase + "/children"),
    bulkSave: libraryAliquotRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(libraryAliquotRestBase + "/bulk"),
    worksetDatatable: idUrlFunction(libraryAliquotRestBase + "/dt/workset"),
  };

  // Library Designs
  var libraryDesignUiBase = "/librarydesign";
  ui.libraryDesigns = {
    bulkCreate: libraryDesignUiBase + "/bulk/new",
    bulkEdit: libraryDesignUiBase + "/bulk/edit",
  };

  var libraryDesignRestBase = restBase + "/librarydesigns";
  rest.libraryDesigns = {
    bulkDelete: libraryDesignRestBase + "/bulk-delete",
    bulkSave: libraryDesignRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(libraryDesignRestBase + "/bulk"),
  };

  // Library Design Codes
  var libraryDesignCodeUiBase = "/librarydesigncode";
  ui.libraryDesignCodes = {
    bulkCreate: libraryDesignCodeUiBase + "/bulk/new",
    bulkEdit: libraryDesignCodeUiBase + "/bulk/edit",
  };
  var libraryDesignCodeRestBase = restBase + "/librarydesigncodes";
  rest.libraryDesignCodes = {
    bulkSave: libraryDesignCodeRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(libraryDesignCodeRestBase + "/bulk"),
  };

  // Library Index Families
  var libraryIndexFamilyUiBase = "/libraryindexfamily";
  ui.libraryIndexFamilies = {
    create: libraryIndexFamilyUiBase + "/new",
    edit: idUrlFunction(libraryIndexFamilyUiBase),
  };

  var libraryIndexFamilyRestBase = restBase + "/libraryindexfamilies";
  rest.libraryIndexFamilies = {
    create: libraryIndexFamilyRestBase,
    update: idUrlFunction(libraryIndexFamilyRestBase),
  };

  // Library Indices
  var libraryIndexUiBase = "/libraryindex";
  ui.libraryIndices = {
    bulkCreate: libraryIndexUiBase + "/bulk/new",
    bulkEdit: libraryIndexUiBase + "/bulk/edit",
  };

  var libraryIndexRestBase = restBase + "/libraryindices";
  rest.libraryIndices = {
    bulkSave: libraryIndexRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(libraryIndexRestBase + "/bulk"),
    datatable: libraryIndexRestBase + "/dt",
    platformDatatable: idUrlFunction(libraryIndexRestBase + "/dt/platform"),
    search: libraryIndexRestBase + "/search",
  };

  // Library Selections
  var librarySelectionUiBase = "/libraryselection";
  ui.librarySelections = {
    bulkCreate: librarySelectionUiBase + "/bulk/new",
    bulkEdit: librarySelectionUiBase + "/bulk/edit",
  };

  var librarySelectionRestBase = restBase + "/libraryselections";
  rest.librarySelections = {
    bulkSave: librarySelectionRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(librarySelectionRestBase + "/bulk"),
  };

  // Library Spike-Ins
  var librarySpikeInUiBase = "/libraryspikein";
  ui.librarySpikeIns = {
    bulkCreate: librarySpikeInUiBase + "/bulk/new",
    bulkEdit: librarySpikeInUiBase + "/bulk/edit",
  };

  var librarySpikeInRestBase = restBase + "/libraryspikeins";
  rest.librarySpikeIns = {
    bulkSave: librarySpikeInRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(librarySpikeInRestBase + "/bulk"),
  };

  // Library Strategies
  var libraryStrategyUiBase = "/librarystrategy";
  ui.libraryStrategies = {
    bulkCreate: libraryStrategyUiBase + "/bulk/new",
    bulkEdit: libraryStrategyUiBase + "/bulk/edit",
  };

  var libraryStrategyRestBase = restBase + "/librarystrategies";
  rest.libraryStrategies = {
    bulkSave: libraryStrategyRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(libraryStrategyRestBase + "/bulk"),
  };

  // Library Templates
  var libraryTemplateUiBase = "/librarytemplate";
  ui.libraryTemplates = {
    create: libraryTemplateUiBase + "/new",
    edit: idUrlFunction(libraryTemplateUiBase),
    bulkCreate: libraryTemplateUiBase + "/bulk/new",
    bulkEdit: libraryTemplateUiBase + "/bulk/edit",
    addIndices: middleIdUrlFunction(libraryTemplateUiBase, "/indices/add"),
    editIndices: middleIdUrlFunction(libraryTemplateUiBase, "/indices/edit"),
  };

  var libraryTemplateRestBase = restBase + "/librarytemplates";
  rest.libraryTemplates = {
    create: libraryTemplateRestBase,
    update: idUrlFunction(libraryTemplateRestBase),
    bulkSave: libraryTemplateRestBase + "/bulk",
    bulkSaveIndices: middleIdUrlFunction(libraryTemplateRestBase, "/indices"),
    bulkSaveProgress: idUrlFunction(libraryTemplateRestBase + "/bulk"),
    query: libraryTemplateRestBase + "/query",
    datatable: libraryTemplateRestBase + "/dt",
    projectDatatable: idUrlFunction(libraryTemplateRestBase + "/dt/project"),
    bulkDelete: libraryTemplateRestBase + "/bulk-delete",
    addProject: libraryTemplateRestBase + "/project/add",
    removeProject: libraryTemplateRestBase + "/project/remove",
  };

  // Library Types
  var libraryTypeUiBase = "/librarytype";
  ui.libraryTypes = {
    bulkCreate: libraryTypeUiBase + "/bulk/new",
    bulkEdit: libraryTypeUiBase + "/bulk/edit",
  };

  var libraryTypeRestBase = restBase + "/librarytypes";
  rest.libraryTypes = {
    bulkSave: libraryTypeRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(libraryTypeRestBase + "/bulk"),
  };

  // Location Maps
  var locationMapRestBase = restBase + "/locationmaps";
  rest.locationMaps = {
    create: locationMapRestBase,
  };

  // Metrics
  var metricUiBase = "/metric";
  ui.metrics = {
    bulkCreate: metricUiBase + "/bulk/new",
    bulkEdit: metricUiBase + "/bulk/edit",
  };

  var metricRestBase = restBase + "/metrics";
  rest.metrics = {
    bulkSave: metricRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(metricRestBase + "/bulk"),
  };

  // Metric Subcategories
  var metricSubcategoryUiBase = "/metricsubcategory";
  ui.metricSubcategories = {
    bulkCreate: metricSubcategoryUiBase + "/bulk/new",
    bulkEdit: metricSubcategoryUiBase + "/bulk/edit",
  };

  var metricSubcategoryRestBase = restBase + "/metricsubcategories";
  rest.metricSubcategories = {
    bulkSave: metricSubcategoryRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(metricSubcategoryRestBase + "/bulk"),
  };

  // Notes
  var noteRestBase = restBase + "/notes";
  rest.notes = {
    create: function (entityType, entityId) {
      return noteRestBase + "/" + entityType + "/" + entityId;
    },
    remove: function (entityType, entityId, noteId) {
      return noteRestBase + "/" + entityType + "/" + entityId + "/" + noteId;
    },
    bulkDelete: noteRestBase + "/bulk-delete",
  };

  // Partition QC Types
  var partitionQcTypeUiBase = "/partitionqctype";
  ui.partitionQcTypes = {
    bulkCreate: partitionQcTypeUiBase + "/bulk/new",
    bulkEdit: partitionQcTypeUiBase + "/bulk/edit",
  };

  var partitionQcTypeRestBase = restBase + "/partitionqctypes";
  rest.partitionQcTypes = {
    bulkSave: partitionQcTypeRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(partitionQcTypeRestBase + "/bulk"),
  };

  // Pipelines
  var pipelineUiBase = "/pipeline";
  ui.pipelines = {
    bulkCreate: pipelineUiBase + "/bulk/new",
    bulkEdit: pipelineUiBase + "/bulk/edit",
  };

  var pipelineRestBase = restBase + "/pipelines";
  rest.pipelines = {
    bulkSave: pipelineRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(pipelineRestBase + "/bulk"),
  };

  // Pools
  var poolUiBase = "/pool";
  ui.pools = {
    create: poolUiBase + "/new",
    edit: idUrlFunction(poolUiBase),
    bulkEdit: poolUiBase + "/bulk/edit",
    bulkMerge: poolUiBase + "/bulk/merge",
  };

  var poolRestBase = restBase + "/pools";
  rest.pools = {
    create: poolRestBase,
    update: idUrlFunction(poolRestBase),
    parents: idUrlFunction(poolRestBase + "/parents"),
    children: idUrlFunction(poolRestBase + "/children"),
    query: poolRestBase + "/query",
    search: poolRestBase + "/search",
    picker: {
      recent: poolRestBase + "/picker/recent",
      search: poolRestBase + "/picker/search",
    },
    assign: middleIdUrlFunction(poolRestBase, "/assign"),
    projectDatatable: idUrlFunction(poolRestBase + "/dt/project"),
    platformDatatable: idUrlFunction(poolRestBase + "/dt/platform"),
    completionsDatatable: middleIdUrlFunction(poolRestBase, "/dt/completions"),
    bulkDelete: poolRestBase + "/bulk-delete",
    spreadsheet: poolRestBase + "/spreadsheet",
    contentsSpreadsheet: poolRestBase + "/contents/spreadsheet",
    samplesheet: poolRestBase + "/samplesheet",
    dragenSamplesheet: poolRestBase + "/dragensamplesheet",
    bulkSave: poolRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(poolRestBase + "/bulk"),
  };

  // Pool Orders
  var poolOrderUiBase = "/poolorder";
  ui.poolOrders = {
    create: poolOrderUiBase + "/new",
    edit: idUrlFunction(poolOrderUiBase),
  };

  var poolOrderRestBase = restBase + "/poolorders";
  rest.poolOrders = {
    create: poolOrderRestBase,
    update: idUrlFunction(poolOrderRestBase),
    indexChecker: poolOrderRestBase + "/indexchecker",
    statusDatatable: idUrlFunction(poolOrderRestBase + "/dt"),
  };

  // Printers
  var printerRestBase = restBase + "/printers";
  rest.printers = {
    datatable: printerRestBase + "/dt",
    create: printerRestBase,
    enable: printerRestBase + "/enable",
    disable: printerRestBase + "/disable",
    layout: middleIdUrlFunction(printerRestBase, "/layout"),
    list: printerRestBase,
    duplicate: middleIdUrlFunction(printerRestBase, "/duplicate"),
    print: idUrlFunction(printerRestBase),
    printBoxContents: middleIdUrlFunction(printerRestBase, "/boxcontents"),
    printBoxPositions: middleIdUrlFunction(printerRestBase, "/boxpositions"),
  };

  // Projects
  var projectUiBase = "/project";
  ui.projects = {
    create: projectUiBase + "/new",
    edit: idUrlFunction(projectUiBase),
  };

  var projectRestBase = restBase + "/projects";
  rest.projects = {
    datatable: projectRestBase + "/dt",
    get: idUrlFunction(projectRestBase),
    create: projectRestBase,
    update: idUrlFunction(projectRestBase),
    search: projectRestBase + "/search",
    attachments: middleIdUrlFunction(projectRestBase, "/files"),
  };

  // QCs
  var qcUiBase = "/qc";
  ui.qcs = {
    bulkAddFrom: idUrlFunction(qcUiBase + "/bulk/addFrom"),
    bulkEdit: idUrlFunction(qcUiBase + "/bulk/edit"),
    bulkEditFrom: idUrlFunction(qcUiBase + "/bulk/editFrom"),
  };

  var qcRestBase = restBase + "/qcs";
  rest.qcs = {
    bulkSave: qcRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(qcRestBase + "/bulk"),
    bulkDelete: qcRestBase + "/bulk-delete",
  };

  // QC Statuses
  var qcStatusRestBase = restBase + "/qcstatuses";
  rest.qcStatuses = {
    update: qcStatusRestBase,
    bulkUpdate: qcStatusRestBase + "/bulk",
  };

  // QC Types
  var qcTypeUiBase = "/qctype";
  ui.qcTypes = {
    create: qcTypeUiBase + "/new",
    edit: idUrlFunction(qcTypeUiBase),
  };

  var qcTypeRestBase = restBase + "/qctypes";
  rest.qcTypes = {
    create: qcTypeRestBase,
    update: idUrlFunction(qcTypeRestBase),
  };

  // Reference Genomes
  var referenceGenomeUiBase = "/referencegenome";
  ui.referenceGenomes = {
    bulkCreate: referenceGenomeUiBase + "/bulk/new",
    bulkEdit: referenceGenomeUiBase + "/bulk/edit",
  };

  var referenceGenomeRestBase = restBase + "/referencegenomes";
  rest.referenceGenomes = {
    bulkSave: referenceGenomeRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(referenceGenomeRestBase + "/bulk"),
  };

  // Requisitions
  var requisitionUiBase = "/requisition";
  ui.requisitions = {
    create: requisitionUiBase + "/new",
    edit: idUrlFunction(requisitionUiBase),
  };

  var requisitionRestBase = restBase + "/requisitions";
  rest.requisitions = {
    datatable: requisitionRestBase + "/dt",
    create: requisitionRestBase,
    update: idUrlFunction(requisitionRestBase),
    addSamples: middleIdUrlFunction(requisitionRestBase, "/samples"),
    removeSamples: middleIdUrlFunction(requisitionRestBase, "/samples/remove"),
    moveSamples: middleIdUrlFunction(requisitionRestBase, "/samples/move"),
    samplesUpdateProgress: idUrlFunction(requisitionRestBase + "/samplesupdate"),
    addSupplementalSamples: middleIdUrlFunction(requisitionRestBase, "/supplementalsamples"),
    removeSupplementalSamples: middleIdUrlFunction(
      requisitionRestBase,
      "/supplementalsamples/remove"
    ),
    addLibraries: middleIdUrlFunction(requisitionRestBase, "/libraries"),
    removeLibraries: middleIdUrlFunction(requisitionRestBase, "/libraries/remove"),
    moveLibraries: middleIdUrlFunction(requisitionRestBase, "/libraries/move"),
    librariesUpdateProgress: idUrlFunction(requisitionRestBase + "/librariesupdate"),
    addSupplementalLibraries: middleIdUrlFunction(requisitionRestBase, "/supplementallibraries"),
    removeSupplementalLibraries: middleIdUrlFunction(
      requisitionRestBase,
      "/supplementallibraries/remove"
    ),
    search: requisitionRestBase + "/search",
    searchPausedByIds: requisitionRestBase + "/paused",
    bulkResume: requisitionRestBase + "/bulk-resume",
    bulkSaveProgress: idUrlFunction(requisitionRestBase + "/bulk"),
    listRunLibraries: middleIdUrlFunction(requisitionRestBase, "/runlibraries"),
  };

  // Runs
  var runUiBase = "/run";
  ui.runs = {
    create: idUrlFunction(runUiBase + "/new"),
    edit: idUrlFunction(runUiBase),
  };

  var runRestBase = restBase + "/runs";
  rest.runs = {
    create: runRestBase,
    update: idUrlFunction(runRestBase),
    datatable: runRestBase + "/dt",
    projectDatatable: idUrlFunction(runRestBase + "/dt/project"),
    sequencerDatatable: idUrlFunction(runRestBase + "/dt/sequencer"),
    platformDatatable: idUrlFunction(runRestBase + "/dt/platform"),
    setPartitionQcs: middleIdUrlFunction(runRestBase, "/qc"),
    setPartitionPurposes: middleIdUrlFunction(runRestBase, "/partition-purposes"),
    updateAliquots: middleIdUrlFunction(runRestBase, "/aliquots"),
    addContainer: middleIdUrlFunction(runRestBase, "/add"),
    removeContainers: middleIdUrlFunction(runRestBase, "/remove"),
    potentialExperiments: middleIdUrlFunction(runRestBase, "/potentialExperiments"),
    spreadsheet: runRestBase + "/spreadsheet",
    parents: idUrlFunction(runRestBase + "/parents"),
    search: runRestBase + "/search",
    recent: runRestBase + "/recent",
  };

  // Run-Libraries
  var runLibraryUiBase = "/runlibraries";
  ui.runLibraries = {
    qcHierarchy: middleIdUrlFunction(runLibraryUiBase, "/qc-hierarchy"),
  };

  // Run-Library QC Statuses
  var runLibraryQcStatusUiBase = "/runlibraryqcstatus";
  ui.runLibraryQcStatuses = {
    bulkCreate: runLibraryQcStatusUiBase + "/bulk/new",
    bulkEdit: runLibraryQcStatusUiBase + "/bulk/edit",
  };

  var runLibraryQcStatusRestBase = restBase + "/runlibraryqcstatuses";
  rest.runLibraryQcStatuses = {
    bulkSave: runLibraryQcStatusRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(runLibraryQcStatusRestBase + "/bulk"),
  };

  // Run Purposes
  var runPurposeUiBase = "/runpurpose";
  ui.runPurposes = {
    bulkCreate: runPurposeUiBase + "/bulk/new",
    bulkEdit: runPurposeUiBase + "/bulk/edit",
  };

  var runPurposeRestBase = restBase + "/runpurposes";
  rest.runPurposes = {
    bulkSave: runPurposeRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(runPurposeRestBase + "/bulk"),
  };

  // Samples
  var sampleUiBase = "/sample";
  ui.samples = {
    edit: idUrlFunction(sampleUiBase),
    bulkCreate: sampleUiBase + "/bulk/new",
    bulkEdit: sampleUiBase + "/bulk/edit",
    bulkPropagate: sampleUiBase + "/bulk/propagate",
    qcHierarchy: middleIdUrlFunction(sampleUiBase, "/qc-hierarchy"),
  };

  var sampleRestBase = restBase + "/samples";
  rest.samples = {
    create: sampleRestBase,
    update: idUrlFunction(sampleRestBase),
    parents: idUrlFunction(sampleRestBase + "/parents"),
    children: idUrlFunction(sampleRestBase + "/children"),
    query: sampleRestBase + "/query",
    datatable: sampleRestBase + "/dt",
    projectDatatable: idUrlFunction(sampleRestBase + "/dt/project"),
    projectArrayedDatatable: middleIdUrlFunction(sampleRestBase + "/dt/project", "/arrayed"),
    bulkDelete: sampleRestBase + "/bulk-delete",
    identitiesLookup: sampleRestBase + "/identitiesLookup",
    spreadsheet: sampleRestBase + "/spreadsheet",
    bulkSave: sampleRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(sampleRestBase + "/bulk"),
    worksetDatatable: idUrlFunction(sampleRestBase + "/dt/workset"),
    requisitionDatatable: idUrlFunction(sampleRestBase + "/dt/requisition"),
    requisitionSupplementalDatatable: idUrlFunction(
      sampleRestBase + "/dt/requisition-supplemental"
    ),
    samplesPreparedDatatable: idUrlFunction(sampleRestBase + "/dt/samples-prepared"),
    findRelated: sampleRestBase + "/find-related",
  };

  // Sample Classes
  var sampleClassUiBase = "/sampleclass";
  ui.sampleClasses = {
    create: sampleClassUiBase + "/new",
    edit: idUrlFunction(sampleClassUiBase),
  };

  var sampleClassRestBase = restBase + "/sampleclasses";
  rest.sampleClasses = {
    create: sampleClassRestBase,
    update: idUrlFunction(sampleClassRestBase),
  };

  // Sample Index Families
  var sampleIndexFamilyUiBase = "/sampleindexfamily";
  ui.sampleIndexFamilies = {
    create: sampleIndexFamilyUiBase + "/new",
    edit: idUrlFunction(sampleIndexFamilyUiBase),
  };

  var sampleIndexFamilyRestBase = restBase + "/sampleindexfamilies";
  rest.sampleIndexFamilies = {
    create: sampleIndexFamilyRestBase,
    update: idUrlFunction(sampleIndexFamilyRestBase),
  };

  // Sample Indices
  var sampleIndexUiBase = "/sampleindex";
  ui.sampleIndices = {
    bulkCreate: sampleIndexUiBase + "/bulk/new",
    bulkEdit: sampleIndexUiBase + "/bulk/edit",
  };

  var sampleIndexRestBase = restBase + "/sampleindices";
  rest.sampleIndices = {
    bulkSave: sampleIndexRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(sampleIndexRestBase + "/bulk"),
  };

  // Sample Purposes
  var samplePurposeUiBase = "/samplepurpose";
  ui.samplePurposes = {
    bulkCreate: samplePurposeUiBase + "/bulk/new",
    bulkEdit: samplePurposeUiBase + "/bulk/edit",
  };

  var samplePurposeRestBase = restBase + "/samplepurposes";
  rest.samplePurposes = {
    bulkSave: samplePurposeRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(samplePurposeRestBase + "/bulk"),
  };

  // Sample Types
  var sampleTypeUiBase = "/sampletype";
  ui.sampleTypes = {
    bulkCreate: sampleTypeUiBase + "/bulk/new",
    bulkEdit: sampleTypeUiBase + "/bulk/edit",
  };

  var sampleTypeRestBase = restBase + "/sampletypes";
  rest.sampleTypes = {
    bulkSave: sampleTypeRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(sampleTypeRestBase + "/bulk"),
  };

  // Scientific Names
  var scientificNameUiBase = "/scientificname";
  ui.scientificNames = {
    bulkCreate: scientificNameUiBase + "/bulk/new",
    bulkEdit: scientificNameUiBase + "/bulk/edit",
  };

  var scientificNameRestBase = restBase + "/scientificnames";
  rest.scientificNames = {
    bulkSave: scientificNameRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(scientificNameRestBase + "/bulk"),
  };

  // Sequencing Control Types
  var sequencingControlTypeUiBase = "/sequencingcontroltype";
  ui.sequencingControlTypes = {
    bulkCreate: sequencingControlTypeUiBase + "/bulk/new",
    bulkEdit: sequencingControlTypeUiBase + "/bulk/edit",
  };

  var sequencingControlTypeRestBase = restBase + "/sequencingcontroltypes";
  rest.sequencingControlTypes = {
    bulkSave: sequencingControlTypeRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(sequencingControlTypeRestBase + "/bulk"),
  };

  // Sequencing Orders
  var sequencingOrderUiBase = "/sequencingorder";
  ui.sequencingOrders = {
    bulkCreate: sequencingOrderUiBase + "/bulk/new",
  };

  var sequencingOrderRestBase = restBase + "/sequencingorders";
  rest.sequencingOrders = {
    create: sequencingOrderRestBase,
    update: idUrlFunction(sequencingOrderRestBase),
    bulkSave: sequencingOrderRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(sequencingOrderRestBase + "/bulk"),
    search: sequencingOrderRestBase + "/search",
    picker: {
      chemistry: sequencingOrderRestBase + "/picker/chemistry",
      active: sequencingOrderRestBase + "/picker/active",
    },
    completionsDatatable: function (status, platform) {
      return sequencingOrderRestBase + "/dt/completions/" + status + "/" + platform;
    },
  };

  // Sequencing Parameters
  var sequencingParametersUiBase = "/sequencingparameters";
  ui.sequencingParameters = {
    bulkCreate: sequencingParametersUiBase + "/bulk/new",
    bulkEdit: sequencingParametersUiBase + "/bulk/edit",
  };

  var sequencingParametersRestBase = restBase + "/sequencingparameters";
  rest.sequencingParameters = {
    bulkSave: sequencingParametersRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(sequencingParametersRestBase + "/bulk"),
  };

  // Service Records
  var serviceRecordRestBase = restBase + "/servicerecords";
  rest.serviceRecords = {
    update: idUrlFunction(serviceRecordRestBase),
    bulkDelete: serviceRecordRestBase + "/bulk-delete",
  };

  // attachment
  var attachmentUiBase = "/attachments";
  ui.attachments = {
    serviceRecord: function (recordId, fileId) {
      return attachmentUiBase + "/servicerecord/" + recordId + "/" + fileId;
    },
  };

  // SOPs
  var sopUiBase = "/sop";
  ui.sops = {
    bulkCreate: sopUiBase + "/bulk/new",
    bulkEdit: sopUiBase + "/bulk/edit",
  };

  var sopRestBase = restBase + "/sops";
  rest.sops = {
    bulkSave: sopRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(sopRestBase + "/bulk"),
    categoryDatatable: idUrlFunction(sopRestBase + "/dt/category"),
  };

  // Stains
  var stainUiBase = "/stain";
  ui.stains = {
    bulkCreate: stainUiBase + "/bulk/new",
    bulkEdit: stainUiBase + "/bulk/edit",
  };

  var stainRestBase = restBase + "/stains";
  rest.stains = {
    bulkSave: stainRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(stainRestBase + "/bulk"),
  };

  // Stain Categories
  var stainCategoryUiBase = "/staincategory";
  ui.stainCategories = {
    bulkCreate: stainCategoryUiBase + "/bulk/new",
    bulkEdit: stainCategoryUiBase + "/bulk/edit",
  };

  var stainCategoryRestBase = restBase + "/staincategories";
  rest.stainCategories = {
    bulkSave: stainCategoryRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(stainCategoryRestBase + "/bulk"),
  };

  // Storage Labels
  var storageLabelUiBase = "/storagelabel";
  ui.storageLabels = {
    bulkCreate: storageLabelUiBase + "/bulk/new",
    bulkEdit: storageLabelUiBase + "/bulk/edit",
  };

  var storageLabelRestBase = restBase + "/storagelabels";
  rest.storageLabels = {
    bulkSave: storageLabelRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(storageLabelRestBase + "/bulk"),
  };

  // Storage Locations
  var storageLocationRestBase = restBase + "/storagelocations";
  function createStorageFunction(storageType) {
    return function (freezerId, shelfId) {
      var url = storageLocationRestBase + "/freezers/" + freezerId;
      if (shelfId) {
        url += "/shelves/" + shelfId;
      }
      url += "/" + storageType;
      return url;
    };
  }

  rest.storageLocations = {
    children: middleIdUrlFunction(storageLocationRestBase, "/children"),
    createFreezer: storageLocationRestBase + "/freezers",
    createLooseStorage: createStorageFunction("loose"),
    createRack: createStorageFunction("racks"),
    createRoom: storageLocationRestBase + "/rooms",
    createShelf: createStorageFunction("shelves"),
    createStack: createStorageFunction("stacks"),
    createTrayRack: createStorageFunction("tray-racks"),
    deleteComponent: idUrlFunction(storageLocationRestBase),
    freezers: storageLocationRestBase + "/freezers",
    updateComponent: idUrlFunction(storageLocationRestBase),
    updateFreezer: idUrlFunction(storageLocationRestBase + "/freezers"),
    queryByBarcode: storageLocationRestBase + "/bybarcode",
    createRecord: function (locationId) {
      return storageLocationRestBase + "/" + locationId + "/servicerecords";
    },
  };

  // Studies
  var studyUiBase = "/study";
  ui.studies = {
    create: studyUiBase + "/new",
    createInProject: idUrlFunction(studyUiBase + "/new"),
    edit: idUrlFunction(studyUiBase),
  };

  var studyRestBase = restBase + "/studies";
  rest.studies = {
    create: studyRestBase,
    update: idUrlFunction(studyRestBase),
    datatable: studyRestBase + "/dt",
    projectDatatable: idUrlFunction(studyRestBase + "/dt/project"),
  };

  // Study Types
  var studyTypeUiBase = "/studytype";
  ui.studyTypes = {
    bulkCreate: studyTypeUiBase + "/bulk/new",
    bulkEdit: studyTypeUiBase + "/bulk/edit",
  };

  var studyTypeRestBase = restBase + "/studytypes";
  rest.studyTypes = {
    bulkSave: studyTypeRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(studyTypeRestBase + "/bulk"),
  };

  // Submissions
  var submissionUiBase = "/submission";
  ui.submissions = {
    create: submissionUiBase + "/new",
    edit: idUrlFunction(submissionUiBase),
  };

  var submissionRestBase = restBase + "/submissions";
  rest.submissions = {
    create: submissionRestBase,
    update: idUrlFunction(submissionRestBase),
    download: middleIdUrlFunction(submissionRestBase, "/download"),
  };

  // Subprojects
  var subprojectUiBase = "/subproject";
  ui.subprojects = {
    bulkCreate: subprojectUiBase + "/bulk/new",
    bulkEdit: subprojectUiBase + "/bulk/edit",
  };

  var subprojectRestBase = restBase + "/subprojects";
  rest.subprojects = {
    bulkSave: subprojectRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(subprojectRestBase + "/bulk"),
  };

  // Targeted Sequencings
  var targetedSequencingUiBase = "/targetedsequencing";
  ui.targetedSequencings = {
    bulkCreate: targetedSequencingUiBase + "/bulk/new",
    bulkEdit: targetedSequencingUiBase + "/bulk/edit",
  };

  var targetedSequencingRestBase = restBase + "/targetedsequencings";
  rest.targetedSequencings = {
    bulkSave: targetedSequencingRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(targetedSequencingRestBase + "/bulk"),
    kitAvailableDatatable: middleIdUrlFunction(
      targetedSequencingRestBase + "/dt/kit",
      "/available"
    ),
  };

  // Tissue Materials
  var tissueMaterialUiBase = "/tissuematerial";
  ui.tissueMaterials = {
    bulkCreate: tissueMaterialUiBase + "/bulk/new",
    bulkEdit: tissueMaterialUiBase + "/bulk/edit",
  };

  var tissueMaterialRestBase = restBase + "/tissuematerials";
  rest.tissueMaterials = {
    bulkSave: tissueMaterialRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(tissueMaterialRestBase + "/bulk"),
  };

  // Tissue Origins
  var tissueOriginUiBase = "/tissueorigin";
  ui.tissueOrigins = {
    bulkCreate: tissueOriginUiBase + "/bulk/new",
    bulkEdit: tissueOriginUiBase + "/bulk/edit",
  };

  var tissueOriginRestBase = restBase + "/tissueorigins";
  rest.tissueOrigins = {
    bulkSave: tissueOriginRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(tissueOriginRestBase + "/bulk"),
  };

  // Tissue Piece Types
  var tissuePieceTypesUiBase = "/tissuepiecetype";
  ui.tissuePieceTypes = {
    bulkCreate: tissuePieceTypesUiBase + "/bulk/new",
    bulkEdit: tissuePieceTypesUiBase + "/bulk/edit",
  };

  var tissuePieceTypesRestBase = restBase + "/tissuepiecetypes";
  rest.tissuePieceTypes = {
    bulkSave: tissuePieceTypesRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(tissuePieceTypesRestBase + "/bulk"),
  };

  // Tissue Types
  var tissueTypeUiBase = "/tissuetype";
  ui.tissueTypes = {
    bulkCreate: tissueTypeUiBase + "/bulk/new",
    bulkEdit: tissueTypeUiBase + "/bulk/edit",
  };

  var tissueTypeRestBase = restBase + "/tissuetypes";
  rest.tissueTypes = {
    bulkSave: tissueTypeRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(tissueTypeRestBase + "/bulk"),
  };

  // Transfers
  var transferUiBase = "/transfer";
  ui.transfers = {
    create: transferUiBase + "/new",
    edit: idUrlFunction(transferUiBase),
  };

  var transferRestBase = restBase + "/transfers";
  rest.transfers = {
    datatable: idUrlFunction(transferRestBase + "/dt"),
    create: transferRestBase,
    update: idUrlFunction(transferRestBase),
    addNotification: middleIdUrlFunction(transferRestBase, "/notifications"),
    resendNotification: function (transferId, notificationId) {
      return transferRestBase + "/" + transferId + "/notifications/" + notificationId + "/resend";
    },
    bulkDeleteNotifications: middleIdUrlFunction(transferRestBase, "/notifications/bulk-delete"),
  };

  // Users
  var userUiBase = "/admin/user";
  ui.users = {
    create: userUiBase + "/new",
    edit: idUrlFunction(userUiBase),
    editSelf: idUrlFunction("/user"),
  };

  var userRestBase = restBase + "/users";
  rest.users = {
    create: userRestBase,
    update: idUrlFunction(userRestBase),
    resetPassword: middleIdUrlFunction(userRestBase, "/password"),
    search: userRestBase,
  };

  // Deliverables
  var deliverableUiBase = "/deliverable";
  ui.deliverables = {
    bulkCreate: deliverableUiBase + "/bulk/new",
    bulkEdit: deliverableUiBase + "/bulk/edit",
  };

  var deliverableRestBase = restBase + "/deliverables";
  rest.deliverables = {
    bulkSave: deliverableRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(deliverableRestBase + "/bulk"),
  };

  // Deliverable categories
  var deliverableCategoryUiBase = "/deliverablecategory";
  ui.deliverableCategories = {
    bulkCreate: deliverableCategoryUiBase + "/bulk/new",
    bulkEdit: deliverableCategoryUiBase + "/bulk/edit",
  };

  var deliverableCategoryRestBase = restBase + "/deliverablecategories";
  rest.deliverableCategories = {
    bulkSave: deliverableCategoryRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(deliverableCategoryRestBase + "/bulk"),
  };

  // Contact Roles
  var contactRoleUiBase = "/contactrole";
  ui.contactRoles = {
    bulkCreate: contactRoleUiBase + "/bulk/new",
    bulkEdit: contactRoleUiBase + "/bulk/edit",
  };

  var contactRoleRestBase = restBase + "/contactroles";
  rest.contactRoles = {
    bulkSave: contactRoleRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(contactRoleRestBase + "/bulk"),
  };

  // Workflows
  var workflowUiBase = "/workflow";
  ui.workflows = {
    create: idUrlFunction(workflowUiBase + "/new"),
    edit: idUrlFunction(workflowUiBase),
  };

  var workflowRestBase = restBase + "/workflows";
  rest.workflows = {
    list: workflowRestBase,
    addFavourite: idUrlFunction(workflowRestBase + "/favourites/add"),
    removeFavourite: idUrlFunction(workflowRestBase + "/favourites/remove"),
    getStep: function (workflowId, stepNumber) {
      return workflowRestBase + "/" + workflowId + "/step/" + stepNumber;
    },
    processInput: function (workflowId, stepNumber) {
      return workflowRestBase + "/" + workflowId + "/step/" + stepNumber;
    },
    latestStep: middleIdUrlFunction(workflowRestBase, "/step/latest"),
    execute: middleIdUrlFunction(workflowRestBase, "/execute"),
  };

  // Worksets
  var worksetUiBase = "/workset";
  ui.worksets = {
    create: worksetUiBase + "/new",
    edit: idUrlFunction(worksetUiBase),
  };

  var worksetRestBase = restBase + "/worksets";
  rest.worksets = {
    create: worksetRestBase,
    update: idUrlFunction(worksetRestBase),
    query: worksetRestBase,
    addSamples: middleIdUrlFunction(worksetRestBase, "/samples"),
    addLibraries: middleIdUrlFunction(worksetRestBase, "/libraries"),
    addLibraryAliquots: middleIdUrlFunction(worksetRestBase, "/libraryaliquots"),
    addPools: middleIdUrlFunction(worksetRestBase,"/pools"),
    removeSamples: middleIdUrlFunction(worksetRestBase, "/samples"),
    removeLibraries: middleIdUrlFunction(worksetRestBase, "/libraries"),
    removeLibraryAliquots: middleIdUrlFunction(worksetRestBase, "/libraryaliquots"),
    removePools: middleIdUrlFunction(worksetRestBase,"pools"),
    moveSamples: middleIdUrlFunction(worksetRestBase, "/samples/move"),
    moveLibraries: middleIdUrlFunction(worksetRestBase, "/libraries/move"),
    moveLibraryAliquots: middleIdUrlFunction(worksetRestBase, "/libraryaliquots/move"),
    movePools: middleIdUrlFunction(workflowRestBase,"/pools/move"),
    merge: worksetRestBase + "/merge",
    categoryDatatable: idUrlFunction(worksetRestBase + "/dt"),
    bulkDelete: worksetRestBase + "/bulk-delete",
  };

  // Workset Categories
  var worksetCategoryUiBase = "/worksetcategory";
  ui.worksetCategories = {
    bulkCreate: worksetCategoryUiBase + "/bulk/new",
    bulkEdit: worksetCategoryUiBase + "/bulk/edit",
  };

  var worksetCategoryRestBase = restBase + "/worksetcategories";
  rest.worksetCategories = {
    bulkSave: worksetCategoryRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(worksetCategoryRestBase + "/bulk"),
  };

  // Workset Stages
  var worksetStageUiBase = "/worksetstage";
  ui.worksetStages = {
    bulkCreate: worksetStageUiBase + "/bulk/new",
    bulkEdit: worksetStageUiBase + "/bulk/edit",
  };

  var worksetStageRestBase = restBase + "/worksetstages";
  rest.worksetStages = {
    bulkSave: worksetStageRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(worksetStageRestBase + "/bulk"),
  };

  // Workstations
  var workstationUiBase = "/workstation";
  ui.workstations = {
    bulkCreate: workstationUiBase + "/bulk/new",
    bulkEdit: workstationUiBase + "/bulk/edit",
    edit: idUrlFunction(workstationUiBase),
  };

  var workstationRestBase = restBase + "/workstations";
  rest.workstations = {
    update: idUrlFunction(workstationRestBase),
    bulkSave: workstationRestBase + "/bulk",
    bulkSaveProgress: idUrlFunction(workstationRestBase + "/bulk"),
  };

  // Downloads
  download.attachment = function (entityType, entityId, attachmentId) {
    return "/attachments/" + entityType + "/" + entityId + "/" + attachmentId;
  };
  download.boxSpreadsheet = idUrlFunction("/download/box/forms");

  // Uploads
  upload.attachment = function (entityType, entityId) {
    return "/attachments/" + entityType + "/" + entityId;
  };

  // External sites
  external.userManual = function (section, subsection) {
    var url = "https://miso-lims.readthedocs.io/en/" + Constants.docsVersion + "/user_manual/";
    if (section) {
      url += section + "/";
      if (subsection) {
        url += "#" + subsection;
      }
    }
    return url;
  };

  external.enaAccession = idUrlFunction("http://www.ebi.ac.uk/ena/data/view");

  return {
    ui: ui,
    rest: rest,
    download: download,
    upload: upload,
    external: external,
  };
})();
