ListTarget.transfer = (function() {
  return {
    name: "Transfers",
    getUserManualUrl: function() {
      return Urls.external.userManual('transfers');
    },
    createUrl: function(config, projectId) {
      return Urls.rest.transfers.datatable(config.tab);
    },
    createBulkActions: function(config, projectId) {
      return [];
    },
    createStaticActions: function(config, projectId) {
      return [{
        name: 'Add',
        handler: function() {
          var params = {};
          if (config.sampleId) {
            params.sampleIds = config.sampleId;
          } else if (config.libraryId) {
            params.libraryIds = config.libraryId;
          } else if (config.libraryAliquotId) {
            params.libraryAliquotIds = config.libraryAliquotId;
          } else if (config.poolId) {
            params.poolIds = config.poolId;
          } else {
            window.location = Urls.ui.transfers.create;
            return;
          }
          window.location = Urls.ui.transfers.create + '?' + jQuery.param(params);
        }
      }];
    },
    createColumns: function(config, projectId) {
      return [ListUtils.idHyperlinkColumn('ID', Urls.ui.transfers.edit, 'id', Utils.array.getId, 0, true), {
        sTitle: 'Items',
        mData: 'items',
        include: true
      }, {
        sTitle: 'Sender',
        mData: function(full, type) {
          return full.senderLabLabel || full.senderGroupName;
        },
        include: true,
        bSortable: false
      }, {
        sTitle: 'Recipient',
        mData: function(full, type) {
          return full.recipient || full.recipientGroupName;
        },
        include: true,
        bSortable: false
      }, {
        sTitle: 'Transfer Time',
        mData: 'transferTime',
        include: true
      }, {
        sTitle: 'Received',
        mData: 'received',
        mRender: function(data, type, full) {
          return makeSummary(full.received, full.items, full.receiptPending);
        },
        include: true,
        bSortable: false
      }, {
        sTitle: 'QC Passed',
        mData: 'qcPassed',
        mRender: function(data, type, full) {
          return makeSummary(full.qcPassed, full.items, full.qcPending);
        },
        include: true,
        bSortable: false
      }, {
        sTitle: 'Last Modified',
        mData: 'lastModified',
        include: true,
        iSortPriority: 1
      }];
    },
    searchTermSelector: function(searchTerms) {
      return [searchTerms['id']]; // TODO
    }
  }

  function makeSummary(value, total, pending) {
    return value + '/' + total + ' (' + pending + ' pending)';
  }

})();
