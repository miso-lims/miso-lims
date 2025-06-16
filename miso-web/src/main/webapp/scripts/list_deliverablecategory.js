ListTarget.deliverablecategory = (function ($) {
  return {
    name: "Deliverable Categories",
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "deliverables");
    },
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.deliverablecategory.getBulkActions(config);
      if (config.isAdmin) {
        actions.push(
          ListUtils.createBulkDeleteAction(
            "Deliverable categories",
            "deliverablecategories",
            Utils.array.getName
          )
        );
      }
      return actions;
    },
    createStaticActions: function (config, projectId) {
      return config.isAdmin
        ? [
            ListUtils.createStaticAddAction(
              "Deliverable Categories",
              Urls.ui.deliverableCategories.bulkCreate,
              true
            ),
          ]
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
