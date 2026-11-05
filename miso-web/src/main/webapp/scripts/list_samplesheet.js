ListTarget.samplesheet = (function () {
  var TYPE_LABEL = "Sample Sheets";

  return {
    name: TYPE_LABEL,
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "sample-sheets");
    },
    createUrl: function (config, projectId) {
      throw new Error("Static data only");
    },
    createBulkActions: function (config, projectId) {
      return !config.isAdmin
        ? []
        : [ListUtils.createBulkDeleteAction(TYPE_LABEL, "samplesheets", Utils.array.getName)];
    },
    createStaticActions: function (config, projectId) {
      return !config.isAdmin
        ? []
        : [
            {
              name: "Add",
              handler: function () {
                Utils.page.pageRedirect(Urls.ui.sampleSheets.create);
              },
            },
          ];
    },
    createColumns: function (config, projectId) {
      return [
        ListUtils.labelHyperlinkColumn(
          "Name",
          Urls.ui.sampleSheets.edit,
          Utils.array.getId,
          "name",
          1,
          true
        ),
        {
          sTitle: "Platform",
          mData: "platformType",
        },
      ];
    },
  };
})();
