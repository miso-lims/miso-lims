ListTarget.sampleindexfamily = {
  name: "Sample Index Families",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "indices");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          ListUtils.createBulkDeleteAction(
            "Sample Index Families",
            "sampleindexfamilies",
            Utils.array.getName
          ),
        ];
  },
  createStaticActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          {
            name: "Add",
            handler: function () {
              window.location = Urls.ui.sampleIndexFamilies.create;
            },
          },
        ];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.labelHyperlinkColumn(
        "Name",
        Urls.ui.sampleIndexFamilies.edit,
        Utils.array.getId,
        "name",
        1,
        true
      ),
    ];
  },
};
