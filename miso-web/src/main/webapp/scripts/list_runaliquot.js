ListTarget.runaliquot = {
  name: "Library Aliquots",
  createUrl: function(config, projectId) {
    throw new Error("Can only be created statically");
  },
  getQueryUrl: null,
  createBulkActions: function(config, projectId) {
    return [{
      name: 'Set QC',
      action: function(aliquots) {
        function showUpdateDialog() {
          var fields = [{
            label: 'Status',
            type: 'select',
            property: 'status',
            values: [{
              id: null,
              description: 'Pending'
            }].concat(Constants.runLibraryQcStatuses),
            getLabel: Utils.array.get('description')
          }, {
            label: 'Note',
            type: 'text',
            property: 'qcNote'
          }];
          Utils.showDialog('Set QC', 'OK', fields, function(output) {
            aliquots.forEach(function(aliquot) {
              aliquot.qcStatusId = output.status.id;
              aliquot.qcNote = output.qcNote ? output.qcNote : null;
            });
            Utils.ajaxWithDialog('Setting QC', 'PUT', Urls.rest.runs.updateAliquots(config.runId), aliquots, Utils.page.pageReload);
          });
        }
        
        if (aliquots.some(function(aliquot) {
          return aliquot.dataReview !== null;
        })) {
          showConfirmDialog('Warning', 'OK', ['Changing QC status will reset the data review. Do you wish to proceed?'], showUpdateDialog);
        } else {
          showUpdateDialog();
        }
      }
    }, {
      name: 'Data Review',
      action: function(aliquots) {
        if (aliquots.some(function(aliquot) {
          return !aliquot.qcStatusId;
        })) {
          Utils.showOkDialog('Error', ['Cannot set data review before QC status']);
          return;
        }
        var fields = [{
          label: 'Data Review',
          type: 'select',
          property: 'dataReview',
          values: [{
            value: null,
            label: 'Pending'
          }, {
            value: true,
            label: 'Pass'
          }, {
            value: false,
            label: 'Fail'
          }],
          getLabel: Utils.array.get('label')
        }];
        Utils.showDialog('Set Data Review', 'OK', fields, function(output) {
          aliquots.forEach(function(aliquot) {
            aliquot.dataReview = output.dataReview.value;
          });
          Utils.ajaxWithDialog('Setting Data Review', 'PUT', Urls.rest.runs.updateAliquots(config.runId), aliquots, Utils.page.pageReload);
        });
      }
    }, {
      name: 'Set Purpose',
      action: function(aliquots) {
        Utils.showWizardDialog("Set Purpose", Constants.runPurposes.sort(Utils.sorting.standardSort('alias')).map(function(purpose) {
          return {
            name: purpose.alias,
            handler: function() {
              aliquots.forEach(function(aliquot) {
                aliquot.runPurposeId = purpose.id;
              });
              Utils.ajaxWithDialog('Setting Purpose', 'PUT', Urls.rest.runs.updateAliquots(config.runId), aliquots, Utils.page.pageReload);
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
      sTitle: "QC Status",
      mData: "qcStatusId",
      mRender: function(data, type, full) {
        if (data === undefined || data === null) {
          return 'Pending';
        }
        var status = Utils.array.findUniqueOrThrow(Utils.array.idPredicate(data), Constants.runLibraryQcStatuses);
        if (type !== 'display') {
          return status.description;
        }
        return '<div class="tooltip"><span>' + status.description + '</span>'
            + '<span class=tooltiptext>Set by ' + full.qcUserName + ', ' + full.qcDate
            + '</span></div>';
      }
    }, {
      sTitle: "QC Note",
      mData: 'qcNote',
      sDefaultContent: ''
    }, {
      sTitle: 'Data Review',
      mData: 'dataReview',
      mRender: function(data, type, full) {
        if (data === undefined || data === null) {
          return 'Pending';
        }
        var status = data ? 'Pass' : 'Fail';
        if (type !== 'display') {
          return status;
        }
        return '<div class="tooltip"><span>' + status + '</span>'
            + '<span class=tooltiptext>Set by ' + full.dataReviewer + ', ' + full.dataReviewDate
            + '</span></div>';
      }
    }, {
      sTitle: 'Hierarchy',
      mData: function(full) {
        return full.runId + '-' + full.partitionId + '-' + full.aliquotId;
      },
      mRender: function(data, type, full) {
        if (type === 'display') {
          return '<a href="' + Urls.ui.runLibraries.qcHierarchy(data) + '">View</a>'
        }
        return data;
      }
    }, {
      sTitle: "Purpose",
      mData: "runPurposeId",
      include: true,
      iSortPriority: 0,
      mRender: ListUtils.render.textFromId(Constants.runPurposes, 'alias', '(Unset)')
    }];
  }
};
