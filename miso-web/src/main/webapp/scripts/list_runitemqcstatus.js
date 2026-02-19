ListTarget.runitemqcstatus = (function () {
  var pluralLabel = "Run-Item QC Statuses";

  return {
    name: pluralLabel,
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "run-item-qc-statuses");
    },
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    getQueryUrl: null,
    showNewOptionSop: true,
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.runitemqcstatus.getBulkActions(config);
      if (config.isAdmin) {
        actions.push(
          ListUtils.createBulkDeleteAction(
            pluralLabel,
            "runitemqcstatuses",
            Utils.array.get("description")
          )
        );
      }
      return actions;
    },
    createStaticActions: function (config, projectId) {
      return config.isAdmin
        ? [
            ListUtils.createStaticAddAction(
              pluralLabel,
              Urls.ui.runItemQcStatuses.bulkCreate,
              true
            ),
          ]
        : [];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Description",
          mData: "description",
          iSortPriority: 1,
        },
        {
          sTitle: "QC Passed",
          mData: "qcPassed",
          mRender: ListUtils.render.booleanChecks,
        },
      ];
    },
  };
})();
