ListTarget.instrumentmodel = {
  name: "Instrument Models",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "instrument-models");
  },
  createUrl: function (config, projectId) {
    return Urls.rest.instrumentModels.datatable;
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    return !config.isAdmin
      ? []
      : [
          ListUtils.createBulkDeleteAction(
            "Instrument Models",
            "instrumentmodels",
            function (item) {
              return item.platformType + " - " + item.alias;
            }
          ),
        ];
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [
          {
            name: "Add",
            handler: function () {
              window.location = Urls.ui.instrumentModels.create;
            },
          },
        ]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.labelHyperlinkColumn(
        "Alias",
        Urls.ui.instrumentModels.edit,
        Utils.array.getId,
        "alias",
        2,
        true
      ),
      {
        sTitle: "Platform",
        mData: "platformType",
        include: true,
        iSortPriority: 1,
      },
      {
        sTitle: "Instrument Type",
        mData: "instrumentType",
        include: true,
        iSortPriority: 0,
      },
    ];
  },
};
