ListTarget.lab = {
  name: "Labs",
  getUserManualUrl: function () {
    return Urls.external.userManual("type_data", "labs");
  },
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  showNewOptionSop: true,
  createBulkActions: function (config, projectId) {
    var actions = BulkTarget.lab.getBulkActions(config);
    if (config.isAdmin) {
      actions.push(ListUtils.createBulkDeleteAction("Labs", "labs", Utils.array.getAlias));
    }
    return actions;
  },
  createStaticActions: function (config, projectId) {
    return [
      {
        name: "Add",
        handler: function () {
          Utils.showDialog(
            "Create Labs",
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
                Utils.showOkDialog("Create Labs", ["That's a peculiar number of labs to create."]);
                return;
              }
              window.location =
                Urls.ui.labs.bulkCreate +
                "?" +
                Utils.page.param({
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
        sTitle: "Alias",
        mData: "alias",
        iSortPriority: 1,
        bSortDirection: true,
      },
      {
        sTitle: "Archived",
        mData: "archived",
        mRender: ListUtils.render.archived,
      },
    ];
  },
};
