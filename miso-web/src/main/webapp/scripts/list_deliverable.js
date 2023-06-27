ListTarget.deliverable = (function ($) {
  return {
    name: "Deliverables",
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "deliverables");
    },
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function (config, projectId) {
      return config.isAdmin
        ? [
            BulkUtils.actions.edit(Urls.ui.deliverables.bulkEdit),
            ListUtils.createBulkDeleteAction("Deliverables", "deliverables", function (item) {
              return item.name;
            }),
          ]
        : [];
    },
    createStaticActions: function (config, projectId) {
      return config.isAdmin
        ? [ListUtils.createStaticAddAction("Deliverables", Urls.ui.deliverables.bulkCreate, true)]
        : [];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Name",
          mData: "name",
        },
      ];
    },
  };
})(jQuery);
