ListTarget.storagelabel = (function () {
  var TYPE_LABEL = "Storage Labels";

  return {
    name: TYPE_LABEL,
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "storage-labels");
    },
    showNewOptionSop: true,
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.storagelabel.getBulkActions(config);
      if (config.isAdmin) {
        actions.push(
          ListUtils.createBulkDeleteAction(TYPE_LABEL, "storagelabels", Utils.array.get("label"))
        );
      }
      return actions;
    },
    createStaticActions: function (config, projectId) {
      return config.isAdmin
        ? [ListUtils.createStaticAddAction(TYPE_LABEL, Urls.ui.storageLabels.bulkCreate, true)]
        : [];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Label",
          mData: "label",
          include: true,
          iSortPriority: 1,
          bSortDirection: true,
        },
      ];
    },
  };
})();
