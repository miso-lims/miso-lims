ListTarget.RunItemQcStatus = (function () {
  var pluralLabel = "Run-Library QC Statuses";

  return {
    name: pluralLabel,
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "run-library-qc-statuses");
    },
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    getQueryUrl: null,
    showNewOptionSop: true,
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.RunItemQcStatus.getBulkActions(config);
      if (config.isAdmin) {
        actions.push(
          ListUtils.createBulkDeleteAction(
            pluralLabel,
            "RunItemQcStatuses",
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
              Urls.ui.RunItemQcStatuses.bulkCreate,
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
