ListTarget.runaliquot = {
  name: "Library Aliquots",
  createUrl: function(config, projectId) {
    throw new Error("Can only be created statically");
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    return [{
      name: 'Set Purpose',
      action: function(aliquots) {
        Utils.showWizardDialog("Set Purpose", Constants.runPurposes.sort(Utils.sorting.standardSort('alias')).map(
            function(purpose) {
              return {
                name: purpose.alias,
                handler: function() {
                  aliquots.forEach(function(aliquot) {
                    aliquot.runPurposeId = purpose.id;
                  });
                  Utils.ajaxWithDialog('Setting Purpose', 'PUT', Urls.rest.runs.setAliquotPurposes(config.runId), aliquots,
                      Utils.page.pageReload);
                }
              }
            }));
      }
    }];
  },
  createStaticActions: function(config, projectId) {
    return [];
  },
  createColumns: function(config, projectId) {
    return [ListUtils.labelHyperlinkColumn("Container", Urls.ui.containers.edit, function(item) {
      return item.containerId;
    }, "containerIdentificationBarcode", 2, true), {
      sTitle: "Partition",
      mData: "partitionNumber",
      include: true,
      iSortPriority: 1,
      bSortDirection: true
    }, ListUtils.labelHyperlinkColumn("Name", Urls.ui.libraryAliquots.edit, function(item) {
      return item.aliquotId;
    }, "aliquotName", 0, true), ListUtils.labelHyperlinkColumn("Alias", Urls.ui.libraryAliquots.edit, function(item) {
      return item.aliquotId;
    }, "aliquotAlias", 0, true), {
      sTitle: "Purpose",
      mData: "runPurposeId",
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.textFromId(Constants.runPurposes, 'alias', '(Unset)')
    }];
  }
};
