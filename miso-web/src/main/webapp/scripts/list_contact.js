ListTarget.contact = (function ($) {
  return {
    name: "Contacts",
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "contacts");
    },
    createUrl: function (config, projectId) {
      throw new Error("Must be provided statically");
    },
    createBulkActions: function (config, projectId) {
      var actions = BulkTarget.contact.getBulkActions(config);
      actions.push(
        ListUtils.createBulkDeleteAction("Contacts", "contacts", function (item) {
          return item.name + " <" + item.email + ">";
        })
      );
      return actions;
    },
    createStaticActions: function (config, projectId) {
      return [ListUtils.createStaticAddAction("Contacts", Urls.ui.contacts.bulkCreate, true)];
    },
    createColumns: function (config, projectId) {
      return [
        {
          sTitle: "Name",
          mData: "name",
        },
        {
          sTitle: "Email",
          mData: "email",
          mRender: function (data, type, full) {
            if (type === "display") {
              return '<a href="mailto:' + data + '">' + data + "</a>";
            }
            return data;
          },
        },
      ];
    },
  };
})(jQuery);
