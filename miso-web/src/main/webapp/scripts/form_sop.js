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
      var isEdit = config.pageMode === "edit";

      return [
        {
          title: "SOP Information",
          fields: [
            {
              title: "SOP ID",
              data: "id",
              type: "read-only",
              getDisplayValue: function (sop) {
                return sop.id ? sop.id : "Unsaved";
              },
            },
            {
              title: "Alias",
              data: "alias",
              type: "text",
              required: true,
              maxLength: 255,
            },
            {
              title: "Version",
              data: "version",
              type: isEdit ? "read-only" : "text",
              required: true,
              maxLength: 50,
            },
            {
              title: "Category",
              data: "category",
              type: isEdit ? "read-only" : "dropdown",
              required: true,
              source: [
                { name: "Sample", value: "SAMPLE" },
                { name: "Library", value: "LIBRARY" },
                { name: "Run", value: "RUN" },
              ],
              sortSource: Utils.sorting.standardSort("name"),
              getItemLabel: function (item) {
                return item.name;
              },
              getItemValue: function (item) {
                return item.value;
              },
              onChange: function (value, formObject) {
                var isRun = value === "RUN";
                jQuery("#listSopFields").toggle(isRun);
                jQuery("#sopFieldsUnsupported").toggle(!isRun);
                jQuery("#sopForm_fieldsError").empty();
                if (!isRun) {
                  Sop.setFields([]);
                }
              },
            },
            {
              title: "URL",
              data: "url",
              type: "text",
              required: true,
              maxLength: 500,
              regex: Utils.validation.uriRegex,
            },
            {
              title: "Archived",
              data: "archived",
              type: "checkbox",
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
