ListTarget.contactRole = (function ($) {
  return {
    name: "ContactRoles",
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "contactRoles");
    },
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.contactRole.getBulkActions(config);
      if (config.isAdmin) {
        actions.push(
          ListUtils.createBulkDeleteAction("ContactRoles", "contactRoles", function (item) {
            return item.name;
          })
        );
        return actions;
      }
      return [];
    },
    createStaticActions: function (config, projectId) {
      return config.isAdmin
        ? [ListUtils.createStaticAddAction("ContactRoles", Urls.ui.contactRoles.bulkCreate, true)]
        : [];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTtitle: "Name",
          mData: "name",
        },
      ];
    },
  };
})(jQuery);
