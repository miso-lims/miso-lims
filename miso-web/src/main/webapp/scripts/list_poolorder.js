ListTarget.poolorder = {
  name: "Pool Orders",
  getUserManualUrl: function () {
    return Urls.external.userManual("pool_orders");
  },
  createUrl: function (config, projectId) {
    return Urls.rest.poolOrders.statusDatatable(config.status);
  },
  queryUrl: null,
  createBulkActions: function (config, projectId) {
    return [ListUtils.createBulkDeleteAction("Pool Orders", "poolorders", Utils.array.getAlias)];
  },
  createStaticActions: function (config, projectId) {
    return [
      {
        name: "Add",
        handler: function () {
          window.location.href = Urls.ui.poolOrders.create;
        },
      },
    ];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.idHyperlinkColumn("ID", Urls.ui.poolOrders.edit, "id", Utils.array.getId, 1, true),
      ListUtils.labelHyperlinkColumn(
        "Alias",
        Urls.ui.poolOrders.edit,
        Utils.array.getId,
        "alias",
        0,
        true
      ),
      {
        sTitle: "Purpose",
        mData: "purposeAlias",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Description",
        mData: "description",
        mRender: Warning.tableWarningRenderer(WarningTarget.poolorder),
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Library Aliquots",
        mData: "orderAliquots",
        include: true,
        bSortable: false,
        mRender: function (data, type, full) {
          return data ? data.length : 0;
        },
      },
      {
        sTitle: "Longest Index",
        mData: "longestIndex",
        bSortable: false,
        iSortPriority: 0,
        include: !config.poolId,
      },
      {
        sTitle: "Instrument Model",
        mData: "parametersName",
        include: true,
        bSortable: false,
        mRender: function (data, type, full) {
          return !data
            ? "n/a"
            : Utils.array.findUniqueOrThrow(
                Utils.array.idPredicate(full.parametersId),
                Constants.sequencingParameters
              ).instrumentModelAlias;
        },
      },
      {
        sTitle: "Sequencing Parameters",
        mData: "parametersName",
        include: true,
        bSortable: false,
        mRender: function (data, type, full) {
          if (type === "display") {
            return data || "n/a";
          } else {
            return data;
          }
        },
      },
      {
        sTitle: "Partitions",
        mData: "partitions",
        include: true,
        iSortPriority: 0,
        mRender: function (data, type, full) {
          if (type === "display") {
            return data || "n/a";
          } else {
            return data;
          }
        },
      },
    ];
  },
  searchTermSelector: function (searchTerms) {
    return [searchTerms["id"], searchTerms["fulfilled"], searchTerms["active"]];
  },
};
