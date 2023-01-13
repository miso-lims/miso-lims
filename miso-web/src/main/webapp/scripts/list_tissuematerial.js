ListTarget.tissuematerial = {
  name: "Tissue Materials",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "tissue-materials");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.tissuematerial.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(
        ListUtils.createBulkDeleteAction(
          "Tissue Materials",
          "tissuematerials",
          Utils.array.getAlias
        )
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
                "Create Tissue Materials",
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
                    Utils.showOkDialog("Create Tissue Materials", [
                      "That's a peculiar number of tissuematerials to create.",
                    ]);
                    return;
                  }
                  window.location =
                    Urls.ui.tissueMaterials.bulkCreate +
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
    ];
  },
};
