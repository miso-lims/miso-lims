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
      var actions = BulkTarget.deliverable.getBulkActions(config);
      if (config.isAdmin) {
        actions.push(
          ListUtils.createBulkDeleteAction("Deliverables", "deliverables", function (item) {
            return item.name;
          })
        );
        return actions;
      }
      return [];
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
