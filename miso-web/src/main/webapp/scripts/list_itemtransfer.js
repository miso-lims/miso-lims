ListTarget.itemtransfer = (function () {
  return {
    name: "Transfers",
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function (config, projectId) {
      return [];
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
          mData: "items.length",
          include: true,
          bSortable: false,
        },
        {
          sTitle: "Sender",
          mData: function (full, type) {
            return full.senderLabLabel || full.senderGroupName;
          },
          include: true,
          bSortable: false,
        },
        {
          sTitle: "Recipient",
          mData: function (full, type) {
            return full.recipient || full.recipientGroupName;
          },
          include: true,
          bSortable: false,
        },
        {
          sTitle: "Transfer Time",
          mData: "transferTime",
          include: true,
          iSortPriority: 1,
        },
        {
          sTitle: "Received",
          mData: function (full, type) {
            return makeSummary(full, "received");
          },
          include: true,
          bSortable: false,
        },
        {
          sTitle: "QC Passed",
          mData: function (full, type) {
            return makeSummary(full, "qcPassed");
          },
          include: true,
          bSortable: false,
        },
      ];
    },
  };

  function makeSummary(transfer, property) {
    var total = transfer.items.length;
    var value = transfer.items.filter(function (item) {
      return item[property] === true;
    }).length;
    var pending = transfer.items.filter(function (item) {
      return typeof item[property] !== "boolean";
    }).length;
    return value + "/" + total + " (" + pending + " pending)";
  }
})();
