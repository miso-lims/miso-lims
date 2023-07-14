ListTarget.contactRole = (function ($) {
  return {
    name: "Contact Roles",
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
          ListUtils.createBulkDeleteAction("Contact Roles", "contactRoles", function (item) {
            return item.name;
          })
        );
        return actions;
      }
      return [];
    },
    createStaticActions: function (config, projectId) {
      return config.isAdmin
        ? [ListUtils.createStaticAddAction("Contact Roles", Urls.ui.contactRoles.bulkCreate, true)]
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
