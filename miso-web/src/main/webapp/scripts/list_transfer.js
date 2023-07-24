ListTarget.transfer = (function () {
  return {
    name: "Transfers",
    getUserManualUrl: function () {
      return Urls.external.userManual("transfers");
    },
    createUrl: function (config, projectId) {
      return Urls.rest.transfers.datatable(config.tab);
    },
    createBulkActions: function (config, projectId) {
      return [ListUtils.createBulkDeleteAction("Transfers", "transfers", getLabel)];
    },
    createStaticActions: function (config, projectId) {
      return [
        {
          name: "Add",
          handler: function () {
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
            window.location = Urls.ui.transfers.create + "?" + Utils.page.param(params);
          },
        },
      ];
    },
    createColumns: function (config, projectId) {
      return [
        ListUtils.idHyperlinkColumn("ID", Urls.ui.transfers.edit, "id", Utils.array.getId, 0, true),
        {
          sTitle: "Items",
          mData: "items",
          bSortable: false,
        },
        {
          sTitle: "Projects",
          mData: "projects",
          bSortable: false,
        },
        {
          sTitle: "Sender",
          mData: function (full, type) {
            return full.senderLabLabel || full.senderGroupName;
          },
          bSortable: false,
        },
        {
          sTitle: "Recipient",
          mData: function (full, type) {
            return full.recipient || full.recipientGroupName;
          },
          bSortable: false,
        },
        {
          sTitle: "Transfer Time",
          mData: "transferTime",
          mRender: ListUtils.render.dateWithTimeTooltip,
        },
        {
          sTitle: "Received",
          mData: "received",
          mRender: function (data, type, full) {
            return makeSummary(full.received, full.items, full.receiptPending);
          },
          bSortable: false,
        },
        {
          sTitle: "QC Passed",
          mData: "qcPassed",
          mRender: function (data, type, full) {
            return makeSummary(full.qcPassed, full.items, full.qcPending);
          },
          bSortable: false,
        },
        {
          sTitle: "Modified",
          mData: "lastModified",
          iSortPriority: 1,
        },
      ];
    },
    searchTermSelector: function (searchTerms) {
      return [
        searchTerms["id"],
        searchTerms["project"],
        searchTerms["subproject"],
        searchTerms["entered"],
        searchTerms["creator"],
        searchTerms["changed"],
        searchTerms["changedby"],
      ];
    },
  };

  function makeSummary(value, total, pending) {
    return value + "/" + total + " (" + pending + " pending)";
  }

  function getLabel(transfer) {
    if (transfer.senderLabLabel) {
      return makeLabel(
        "Receipt",
        transfer.senderLabLabel,
        transfer.recipientGroupName,
        transfer.items
      );
    } else if (transfer.recipient) {
      return makeLabel(
        "Distribution",
        transfer.senderGroupName,
        transfer.recipient,
        transfer.items
      );
    } else {
      return makeLabel(
        "Internal",
        transfer.senderGroupName,
        transfer.recipientGroupName,
        transfer.items
      );
    }
  }

  function makeLabel(transferType, sender, recipient, itemCount) {
    return transferType + ": " + sender + " → " + recipient + " (" + itemCount + " items)";
  }
})();
