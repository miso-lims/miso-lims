ListTarget.array = {
  name: "Arrays",
  getUserManualUrl: function () {
    return Urls.external.userManual("arrays");
  },
  createUrl: function (config, projectId) {
    return Urls.rest.arrays.datatable;
  },
  createBulkActions: function (config, projectId) {
    return [
      ListUtils.createBulkDeleteAction("Arrays", "arrays", function (array) {
        return array.alias;
      }),
    ];
  },
  createStaticActions: function (config, projectId) {
    return [
      {
        name: "Add",
        handler: function () {
          window.location = Urls.ui.arrays.create;
        },
      },
    ];
  },
  createColumns: function (config, projectId) {
    return [
      ListUtils.idHyperlinkColumn("ID", Urls.ui.arrays.edit, "id", Utils.array.getId, 0, true),
      ListUtils.idHyperlinkColumn(
        "Alias",
        Urls.ui.arrays.edit,
        "id",
        Utils.array.getAlias,
        0,
        true
      ),
      ListUtils.idHyperlinkColumn(
        "Serial Number",
        Urls.ui.arrays.edit,
        "id",
        function (item) {
          return item.serialNumber;
        },
        0,
        true
      ),
      {
        sTitle: "Modified",
        mData: "lastModified",
        include: true,
        iSortPriority: 2,
        bVisible: Constants.isDetailedSample,
      },
    ];
  },
};
