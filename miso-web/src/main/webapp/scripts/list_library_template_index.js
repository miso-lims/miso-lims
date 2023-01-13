ListTarget.libraryTemplate_index = {
  name: "Indices",
  createUrl: function (config, projectId) {
    throw new Error("Must be provided statically");
  },
  getQueryUrl: null,
  createBulkActions: function (config, projectId) {
    return [
      {
        name: "Edit",
        action: function (items) {
          window.location =
            Urls.ui.libraryTemplates.editIndices(config.libraryTemplateId) +
            "?" +
            Utils.page.param({
              positions: items
                .map(function (item) {
                  return item.boxPosition;
                })
                .join(","),
            });
        },
      },
      {
        name: "Remove",
        action: function (items) {
          LibraryTemplate.removeIndices(items);
        },
      },
    ];
  },
  createStaticActions: function (config, projectId) {
    return [
      {
        name: "Add",
        handler: function () {
          Utils.showDialog(
            "Add Indices",
            "Add",
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
                Utils.showOkDialog("Add Indices", ["Quantity must be 1 or more."]);
                return;
              }
              window.location =
                Urls.ui.libraryTemplates.addIndices(config.libraryTemplateId) +
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
        sTitle: "Box Position",
        mData: "boxPosition",
        include: true,
        iSortPriority: 1,
      },
      {
        sTitle: "Index 1",
        mData: function (full, type, setData) {
          return full.index1 ? full.index1.label : "Unspecified";
        },
        include: true,
        iSortPriority: 0,
      },
      {
        sTitle: "Index 2",
        mData: function (full, type, setData) {
          return full.index2 ? full.index2.label : "Unspecified";
        },
        include: true,
        iSortPriority: 0,
      },
    ];
  },
};
