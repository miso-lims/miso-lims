ListTarget.tissueorigin = {
  name: "Tissue Origins",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "tissue-origins");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.tissueorigin.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction("Tissue Origins", "tissueorigins", Utils.array.getAlias)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return config.isAdmin
      ? [
          {
            name: "Add",
            handler: function () {
              Utils.showDialog(
                "Create Tissue Origins",
                "Create",
                [
                  {
                    property: "quantity",
                    type: "int",
                    label: "Quantity",
                    value: 1,
                  },
                ],
                function (result) {
                  if (result.quantity < 1) {
                    Utils.showOkDialog("Create Tissue Origins", [
                      "That's a peculiar number of tissueorigins to create.",
                    ]);
                    return;
                  }
                  window.location =
                    Urls.ui.tissueOrigins.bulkCreate +
                    "?" +
                    Utils.page.param({
                      quantity: result.quantity,
                    });
                }
              );
            },
          },
        ]
      : [];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Alias",
        mData: "alias",
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Description",
        mData: "description",
        include: true,
        iSortPriority: 0,
      },
    ];
  },
};
