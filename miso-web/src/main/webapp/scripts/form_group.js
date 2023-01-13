if (typeof FormTarget === "undefined") {
  FormTarget = {};
}
FormTarget.group = (function ($) {
  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("users_and_groups", "groups");
    },
    getSaveUrl: function (group) {
      return group.id ? Urls.rest.groups.update(group.id) : Urls.rest.groups.create;
    },
    getSaveMethod: function (group) {
      return group.id ? "PUT" : "POST";
    },
    getEditUrl: function (group) {
      return Urls.ui.groups.edit(group.id);
    },
    getSections: function (config, object) {
      return [
        {
          title: "Group Information",
          fields: [
            {
              title: "Group ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (group) {
                return group.id || "Unsaved";
              },
            },
            {
              title: "Name",
              data: "name",
              type: "text",
              required: true,
              maxLength: 255,
            },
            {
              title: "Description",
              data: "description",
              type: "text",
              maxLength: 255,
            },
          ],
        },
      ];
    },
  };
})(jQuery);
