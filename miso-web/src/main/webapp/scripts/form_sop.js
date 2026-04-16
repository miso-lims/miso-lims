if (typeof FormTarget === "undefined") {
  FormTarget = {};
}

FormTarget.sop = (function ($) {
  "use strict";

  return {
    getUserManualUrl: function () {
      return Urls.external.userManual("type_data", "standard-operating-procedures");
    },

    getSaveUrl: function (sop) {
      return sop.id ? Urls.rest.sops.update(sop.id) : Urls.rest.sops.create;
    },

    getSaveMethod: function (sop) {
      return sop.id ? "PUT" : "POST";
    },

    getEditUrl: function (sop) {
      return Urls.ui.sops.edit(sop.id);
    },

    getSections: function (config, object) {
      return [
        {
          title: "SOP Information",
          fields: [
            FormUtils.makeIdField("SOP"),
            {
              title: "Alias",
              data: "alias",
              type: config.isAdmin ? "text" : "read-only",
              required: true,
              maxLength: 255,
            },
            {
              title: "Version",
              data: "version",
              type: config.isAdmin && config.pageMode === "create" ? "text" : "read-only",
              required: true,
              maxLength: 50,
            },
            {
              title: "Category",
              data: "category",
              type: config.isAdmin && config.pageMode === "create" ? "dropdown" : "read-only",
              required: true,
              source: [
                { name: "Sample", value: "SAMPLE" },
                { name: "Library", value: "LIBRARY" },
                { name: "Run", value: "RUN" },
              ],
              sortSource: Utils.sorting.standardSort("name"),
              getItemLabel: Utils.array.getName,
              getItemValue: Utils.array.get("value"),
              onChange: function (value, formObject) {
                var isRun = value === "RUN";
                $("#listSopFields").toggle(isRun);
                $("#sopFieldsUnsupported").toggle(!isRun);
                $("#sopForm_fieldsError").empty();
                if (!isRun) {
                  Sop.setFields([]);
                }
              },
            },
            {
              title: "URL",
              data: "url",
              type: config.isAdmin ? "text" : "read-only",
              getLink: function (object) {
                return object.url;
              },
              openNewTab: true,
              required: true,
              maxLength: 500,
              regex: Utils.validation.uriRegex,
            },
            {
              title: "Archived",
              data: "archived",
              type: "checkbox",
              disabled: !config.isAdmin,
            },
          ],
        },
      ];
    },

    confirmSave: function (sop, isDialog) {
      if (sop.category === "RUN") {
        sop.fields = Sop.getFields();
      }
    },
  };
})(jQuery);
