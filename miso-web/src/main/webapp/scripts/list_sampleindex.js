ListTarget.sampleindex = {
  name: "Sample Indices",
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    var actions = [];
    if (config.indexFamilyId && config.isAdmin) {
      actions.push(
        BulkUtils.actions.edit(Urls.ui.sampleIndices.bulkEdit),
        ListUtils.createBulkDeleteAction("Sample Indices", "sampleindices", Utils.array.getName)
      );
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return !config.indexFamilyId || !config.isAdmin
      ? []
      : [
          {
            name: "Add",
            handler: function () {
              Utils.showDialog(
                "Create Indices",
                "Create",
                [
                  {
                    property: "quantity",
                    type: "int",
                    label: "Quantity",
                    required: true,
                    value: 1,
                  },
                ],
                function (result) {
                  if (result.quantity < 1) {
                    Utils.showOkDialog("Create Indices", ["Quantity must be 1 or more."]);
                    return;
                  }
                  window.location =
                    Urls.ui.sampleIndices.bulkCreate +
                    "?" +
                    Utils.page.param({
                      indexFamilyId: config.indexFamilyId,
                      quantity: result.quantity,
                    });
                }
              );
            },
          },
        ];
  },
  createColumns: function (config, projectId) {
    return [
      {
        sTitle: "Index Name",
        include: true,
        iSortPriority: 1,
        bSortDirection: false,
        mData: "name",
      },
    ];
  },
};
